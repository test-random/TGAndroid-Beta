package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SpeedIconDrawable;

public abstract class ActionBarMenuSlider extends FrameLayout {
    private boolean backgroundDark;
    private final Paint backgroundPaint;
    private Bitmap blurBitmap;
    private AnimatedFloat blurBitmapAlpha;
    private Matrix blurBitmapMatrix;
    private BitmapShader blurBitmapShader;
    private boolean blurIsInChat;
    private final Paint blurPaint;
    private final Paint brightenBlurPaint;
    private final Paint darkenBlurPaint;
    private boolean dragging;
    private boolean drawBlur;
    private boolean drawShadow;
    private final Paint fillPaint;
    private float fromValue;
    private float fromX;
    private AnimatedTextView.AnimatedTextDrawable leftTextDrawable;
    private int[] location;
    private Utilities.Callback2 onValueChange;
    private Runnable prepareBlur;
    private boolean preparingBlur;
    private int pseudoBlurColor1;
    private int pseudoBlurColor2;
    private LinearGradient pseudoBlurGradient;
    private Matrix pseudoBlurMatrix;
    private final Paint pseudoBlurPaint;
    private int pseudoBlurWidth;
    protected Theme.ResourcesProvider resourcesProvider;
    private AnimatedTextView.AnimatedTextDrawable rightTextDrawable;
    private float roundRadiusDp;
    private final Paint shadowPaint;
    private final Paint stopPaint;
    private float[] stops;
    private long tapStart;
    private float value;
    private ValueAnimator valueAnimator;
    private ColorFilter whiteColorFilter;

    public static class SpeedSlider extends ActionBarMenuSlider {
        String label;
        private final SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;

        public SpeedSlider(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.label = null;
            setFocusable(true);
            setFocusableInTouchMode(true);
            setImportantForAccessibility(1);
            FloatSeekBarAccessibilityDelegate floatSeekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate(false) {
                @Override
                public CharSequence getContentDescription(View view) {
                    return SpeedIconDrawable.formatNumber(SpeedSlider.this.getSpeed()) + "x  " + LocaleController.getString(R.string.AccDescrSpeedSlider);
                }

                @Override
                public float getDelta() {
                    return 0.2f;
                }

                @Override
                protected float getMaxValue() {
                    return 3.0f;
                }

                @Override
                protected float getMinValue() {
                    return 0.2f;
                }

                @Override
                public float getProgress() {
                    return SpeedSlider.this.getSpeed();
                }

                @Override
                public void setProgress(float f) {
                    SpeedSlider.this.setSpeed(f, true);
                }
            };
            this.seekBarAccessibilityDelegate = floatSeekBarAccessibilityDelegate;
            setAccessibilityDelegate(floatSeekBarAccessibilityDelegate);
        }

        @Override
        protected int getColorValue(float f) {
            return ColorUtils.blendARGB(Theme.getColor(Theme.key_color_lightblue, this.resourcesProvider), Theme.getColor(Theme.key_color_blue, this.resourcesProvider), MathUtils.clamp((((f * 2.8f) + 0.2f) - 1.0f) / 1.0f, 0.0f, 1.0f));
        }

        @Override
        protected String getLeftStringValue(float f) {
            String str = this.label;
            if (str != null) {
                return str;
            }
            return SpeedIconDrawable.formatNumber((f * 2.8f) + 0.2f) + "x";
        }

        @Override
        protected String getRightStringValue(float f) {
            if (this.label == null) {
                return null;
            }
            return SpeedIconDrawable.formatNumber((f * 2.8f) + 0.2f) + "x";
        }

        public float getSpeed() {
            return getSpeed(getValue());
        }

        public float getSpeed(float f) {
            return (f * 2.8f) + 0.2f;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.seekBarAccessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        @Override
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.seekBarAccessibilityDelegate.performAccessibilityActionInternal(this, i, bundle);
        }

        public void setLabel(String str) {
            this.label = str;
        }

        public void setSpeed(float f, boolean z) {
            setValue((f - 0.2f) / 2.8f, z);
        }

        @Override
        public void setStops(float[] fArr) {
            for (int i = 0; i < fArr.length; i++) {
                fArr[i] = (fArr[i] - 0.2f) / 2.8f;
            }
            super.setStops(fArr);
        }
    }

    public ActionBarMenuSlider(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.value = 0.5f;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.blurBitmapAlpha = new AnimatedFloat(1.0f, this, 0L, 320L, cubicBezierInterpolator);
        this.location = new int[2];
        this.roundRadiusDp = 0.0f;
        boolean z = true;
        Paint paint = new Paint(1);
        this.shadowPaint = paint;
        Paint paint2 = new Paint(1);
        this.backgroundPaint = paint2;
        this.blurPaint = new Paint(1);
        Paint paint3 = new Paint(1);
        this.brightenBlurPaint = paint3;
        Paint paint4 = new Paint(1);
        this.darkenBlurPaint = paint4;
        Paint paint5 = new Paint(1);
        this.pseudoBlurPaint = paint5;
        this.fillPaint = new Paint(1);
        Paint paint6 = new Paint(1);
        this.stopPaint = paint6;
        this.blurIsInChat = true;
        this.preparingBlur = false;
        this.prepareBlur = new Runnable() {
            @Override
            public final void run() {
                ActionBarMenuSlider.this.lambda$new$2();
            }
        };
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, z, z) {
            @Override
            public void invalidateSelf() {
                ActionBarMenuSlider.this.invalidate();
            }
        };
        this.leftTextDrawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
        this.leftTextDrawable.setTypeface(AndroidUtilities.bold());
        boolean z2 = false;
        this.leftTextDrawable.setAnimationProperties(0.3f, 0L, 165L, cubicBezierInterpolator);
        this.leftTextDrawable.setTextSize(AndroidUtilities.dpf2(14.0f));
        TextPaint paint7 = this.leftTextDrawable.getPaint();
        Paint.Style style = Paint.Style.FILL_AND_STROKE;
        paint7.setStyle(style);
        this.leftTextDrawable.getPaint().setStrokeWidth(AndroidUtilities.dpf2(0.3f));
        this.leftTextDrawable.setGravity(LocaleController.isRTL ? 5 : 3);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(z2, z, z) {
            @Override
            public void invalidateSelf() {
                ActionBarMenuSlider.this.invalidate();
            }
        };
        this.rightTextDrawable = animatedTextDrawable2;
        animatedTextDrawable2.setCallback(this);
        this.rightTextDrawable.setTypeface(AndroidUtilities.bold());
        this.rightTextDrawable.setAnimationProperties(0.3f, 0L, 165L, cubicBezierInterpolator);
        this.rightTextDrawable.setTextSize(AndroidUtilities.dpf2(14.0f));
        this.rightTextDrawable.getPaint().setStyle(style);
        this.rightTextDrawable.getPaint().setStrokeWidth(AndroidUtilities.dpf2(0.3f));
        this.rightTextDrawable.setGravity(LocaleController.isRTL ? 3 : 5);
        paint.setColor(0);
        paint.setShadowLayer(AndroidUtilities.dpf2(1.33f), 0.0f, AndroidUtilities.dpf2(0.33f), 1056964608);
        ColorMatrix colorMatrix = new ColorMatrix();
        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, -0.4f);
        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.1f);
        paint5.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        paint2.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
        boolean z3 = AndroidUtilities.computePerceivedBrightness(paint2.getColor()) <= 0.721f;
        this.backgroundDark = z3;
        this.leftTextDrawable.setTextColor(z3 ? -1 : -16777216);
        this.rightTextDrawable.setTextColor(this.backgroundDark ? -1 : -16777216);
        paint4.setColor(Theme.multAlpha(-16777216, 0.025f));
        paint3.setColor(Theme.multAlpha(-1, 0.35f));
        paint6.setColor(Theme.multAlpha(-1, 0.2f));
    }

    private void drawStops(Canvas canvas) {
        if (this.stops == null) {
            return;
        }
        int i = 0;
        while (true) {
            float[] fArr = this.stops;
            if (i >= fArr.length) {
                return;
            }
            float f = fArr[i];
            RectF rectF = AndroidUtilities.rectTmp;
            canvas.drawRect(rectF.left + (rectF.width() * f), rectF.top, rectF.left + (rectF.width() * f) + AndroidUtilities.dp(0.66f), rectF.bottom, this.stopPaint);
            i++;
        }
    }

    private void drawText(Canvas canvas, boolean z) {
        ColorFilter colorFilter;
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.leftTextDrawable;
        ColorFilter colorFilter2 = null;
        if (z) {
            colorFilter = this.whiteColorFilter;
            if (colorFilter == null) {
                colorFilter = new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN);
                this.whiteColorFilter = colorFilter;
            }
        } else {
            colorFilter = null;
        }
        animatedTextDrawable.setColorFilter(colorFilter);
        this.leftTextDrawable.setBounds(getPaddingLeft() + AndroidUtilities.dp(20.0f), getMeasuredHeight() / 2, (getMeasuredWidth() - getPaddingRight()) - AndroidUtilities.dp(20.0f), getMeasuredHeight() / 2);
        this.leftTextDrawable.draw(canvas);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = this.rightTextDrawable;
        if (z && (colorFilter2 = this.whiteColorFilter) == null) {
            colorFilter2 = new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN);
            this.whiteColorFilter = colorFilter2;
        }
        animatedTextDrawable2.setColorFilter(colorFilter2);
        this.rightTextDrawable.setBounds(getPaddingLeft() + AndroidUtilities.dp(20.0f), getMeasuredHeight() / 2, (getMeasuredWidth() - getPaddingRight()) - AndroidUtilities.dp(20.0f), getMeasuredHeight() / 2);
        this.rightTextDrawable.draw(canvas);
    }

    private Pair getBitmapGradientColors(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        float f = this.location[0] / AndroidUtilities.displaySize.x;
        int width = (int) (f * bitmap.getWidth());
        int measuredWidth = (int) (((r1 + getMeasuredWidth()) / AndroidUtilities.displaySize.x) * bitmap.getWidth());
        int currentActionBarHeight = (int) ((((this.location[1] - AndroidUtilities.statusBarHeight) - ActionBar.getCurrentActionBarHeight()) / AndroidUtilities.displaySize.y) * bitmap.getHeight());
        if (width < 0 || width >= bitmap.getWidth() || measuredWidth < 0 || measuredWidth >= bitmap.getWidth() || currentActionBarHeight < 0 || currentActionBarHeight >= bitmap.getHeight()) {
            return null;
        }
        return new Pair(Integer.valueOf(bitmap.getPixel(width, currentActionBarHeight)), Integer.valueOf(bitmap.getPixel(measuredWidth, currentActionBarHeight)));
    }

    public void lambda$new$1(Bitmap bitmap) {
        this.preparingBlur = false;
        this.blurBitmap = bitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        this.blurBitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
        Matrix matrix = this.blurBitmapMatrix;
        if (matrix == null) {
            this.blurBitmapMatrix = new Matrix();
        } else {
            matrix.reset();
        }
        this.blurBitmapMatrix.postScale(8.0f, 8.0f);
        Matrix matrix2 = this.blurBitmapMatrix;
        int[] iArr = this.location;
        matrix2.postTranslate(-iArr[0], -iArr[1]);
        this.blurBitmapShader.setLocalMatrix(this.blurBitmapMatrix);
        this.blurPaint.setShader(this.blurBitmapShader);
        ColorMatrix colorMatrix = new ColorMatrix();
        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, -0.2f);
        this.blurPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        invalidate();
    }

    public void lambda$new$2() {
        this.preparingBlur = true;
        AndroidUtilities.makeGlobalBlurBitmap(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ActionBarMenuSlider.this.lambda$new$1((Bitmap) obj);
            }
        }, 8.0f);
    }

    public void lambda$setValue$0(ValueAnimator valueAnimator) {
        this.value = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void updatePseudoBlurColors() {
        int color;
        int i;
        if (this.blurIsInChat) {
            Drawable cachedWallpaper = Theme.getCachedWallpaper();
            if (cachedWallpaper instanceof ColorDrawable) {
                color = ((ColorDrawable) cachedWallpaper).getColor();
            } else {
                Pair bitmapGradientColors = getBitmapGradientColors(cachedWallpaper instanceof MotionBackgroundDrawable ? ((MotionBackgroundDrawable) cachedWallpaper).getBitmap() : cachedWallpaper instanceof BitmapDrawable ? ((BitmapDrawable) cachedWallpaper).getBitmap() : null);
                if (bitmapGradientColors != null) {
                    int intValue = ((Integer) bitmapGradientColors.first).intValue();
                    i = ((Integer) bitmapGradientColors.second).intValue();
                    color = intValue;
                    if (this.pseudoBlurGradient == null && this.pseudoBlurColor1 == color && this.pseudoBlurColor2 == i) {
                        return;
                    }
                    this.pseudoBlurColor1 = color;
                    this.pseudoBlurColor2 = i;
                    LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 1.0f, 0.0f, new int[]{color, i}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                    this.pseudoBlurGradient = linearGradient;
                    this.pseudoBlurPaint.setShader(linearGradient);
                }
                color = Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider), 0.25f);
            }
        } else {
            color = Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider);
            if (!Theme.isCurrentThemeDark()) {
                color = Theme.blendOver(color, Theme.multAlpha(-16777216, 0.18f));
            }
        }
        i = color;
        if (this.pseudoBlurGradient == null) {
        }
        this.pseudoBlurColor1 = color;
        this.pseudoBlurColor2 = i;
        LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, 1.0f, 0.0f, new int[]{color, i}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
        this.pseudoBlurGradient = linearGradient2;
        this.pseudoBlurPaint.setShader(linearGradient2);
    }

    private void updateValue(float f, boolean z) {
        setValue(f, false);
        Utilities.Callback2 callback2 = this.onValueChange;
        if (callback2 != null) {
            callback2.run(Float.valueOf(this.value), Boolean.valueOf(z));
        }
    }

    protected abstract int getColorValue(float f);

    protected abstract String getLeftStringValue(float f);

    protected abstract String getRightStringValue(float f);

    public float getValue() {
        return this.value;
    }

    public void invalidateBlur(boolean z) {
        this.blurIsInChat = z;
        this.blurPaint.setShader(null);
        this.blurBitmapShader = null;
        Bitmap bitmap = this.blurBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.blurBitmap = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        if (this.drawShadow) {
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.shadowPaint);
        }
        if (this.drawBlur) {
            float f = this.blurBitmapAlpha.set(this.blurBitmap != null ? 1.0f : 0.0f);
            if (f < 1.0f) {
                if (this.pseudoBlurMatrix == null || this.pseudoBlurWidth != ((int) rectF.width())) {
                    Matrix matrix = this.pseudoBlurMatrix;
                    if (matrix == null) {
                        this.pseudoBlurMatrix = new Matrix();
                    } else {
                        matrix.reset();
                    }
                    Matrix matrix2 = this.pseudoBlurMatrix;
                    int width = (int) rectF.width();
                    this.pseudoBlurWidth = width;
                    matrix2.postScale(width, 1.0f);
                    this.pseudoBlurGradient.setLocalMatrix(this.pseudoBlurMatrix);
                }
                this.pseudoBlurPaint.setAlpha((int) ((1.0f - f) * 255.0f));
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.pseudoBlurPaint);
            }
            if (this.blurBitmap != null && this.value < 1.0f && f > 0.0f) {
                this.blurPaint.setAlpha((int) (f * 255.0f));
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.blurPaint);
            }
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.brightenBlurPaint);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.darkenBlurPaint);
            this.fillPaint.setColor(-1);
        } else {
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.backgroundPaint);
        }
        drawStops(canvas);
        if (!this.backgroundDark) {
            drawText(canvas, false);
        }
        if (this.value < 1.0f) {
            canvas.save();
            canvas.clipRect(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + (((getWidth() - getPaddingLeft()) - getPaddingRight()) * this.value), getHeight() - getPaddingBottom());
        }
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(this.roundRadiusDp), AndroidUtilities.dp(this.roundRadiusDp), this.fillPaint);
        drawStops(canvas);
        if (!this.backgroundDark) {
            drawText(canvas, true);
        }
        if (this.value < 1.0f) {
            canvas.restore();
        }
        if (this.backgroundDark) {
            drawText(canvas, false);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        getLocationOnScreen(this.location);
        Matrix matrix = this.blurBitmapMatrix;
        if (matrix != null) {
            matrix.reset();
            this.blurBitmapMatrix.postScale(8.0f, 8.0f);
            Matrix matrix2 = this.blurBitmapMatrix;
            int[] iArr = this.location;
            matrix2.postTranslate(-iArr[0], -iArr[1]);
            BitmapShader bitmapShader = this.blurBitmapShader;
            if (bitmapShader != null) {
                bitmapShader.setLocalMatrix(this.blurBitmapMatrix);
                invalidate();
            }
        }
        updatePseudoBlurColors();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        if (this.drawShadow) {
            i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i) + getPaddingRight() + getPaddingLeft(), 1073741824);
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f) + getPaddingTop() + getPaddingBottom(), 1073741824));
        boolean z = SharedConfig.getDevicePerformanceClass() >= 2 && LiteMode.isEnabled(256);
        if (this.drawBlur && this.blurBitmap == null && !this.preparingBlur && z) {
            this.prepareBlur.run();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX() - getPaddingLeft();
        int action = motionEvent.getAction();
        if (action == 0) {
            this.dragging = true;
            this.fromX = x;
            this.fromValue = this.value;
            this.tapStart = System.currentTimeMillis();
        } else if (action == 2 || action == 1) {
            int i = 0;
            if (action == 1) {
                this.dragging = false;
                if (System.currentTimeMillis() - this.tapStart < ViewConfiguration.getTapTimeout()) {
                    float paddingLeft = (x - getPaddingLeft()) / ((getWidth() - getPaddingLeft()) - getPaddingRight());
                    if (this.stops != null) {
                        while (true) {
                            float[] fArr = this.stops;
                            if (i >= fArr.length) {
                                break;
                            }
                            if (Math.abs(paddingLeft - fArr[i]) < 0.1f) {
                                paddingLeft = this.stops[i];
                                break;
                            }
                            i++;
                        }
                    }
                    Utilities.Callback2 callback2 = this.onValueChange;
                    if (callback2 != null) {
                        callback2.run(Float.valueOf(paddingLeft), Boolean.TRUE);
                    }
                    return true;
                }
            }
            float max = this.fromValue + ((x - this.fromX) / Math.max(1, (getWidth() - getPaddingLeft()) - getPaddingRight()));
            if (this.stops != null) {
                while (true) {
                    float[] fArr2 = this.stops;
                    if (i >= fArr2.length) {
                        break;
                    }
                    if (Math.abs(max - fArr2[i]) < 0.05f) {
                        max = this.stops[i];
                        break;
                    }
                    i++;
                }
            }
            updateValue(max, !this.dragging);
        }
        return true;
    }

    @Override
    public void setBackgroundColor(int i) {
        this.backgroundPaint.setColor(i);
        boolean z = AndroidUtilities.computePerceivedBrightness(this.backgroundPaint.getColor()) <= 0.721f;
        this.backgroundDark = z;
        this.leftTextDrawable.setTextColor(z ? -1 : -16777216);
        this.rightTextDrawable.setTextColor(this.backgroundDark ? -1 : -16777216);
    }

    public void setDrawBlur(boolean z) {
        this.drawBlur = z;
        invalidate();
    }

    public void setDrawShadow(boolean z) {
        this.drawShadow = z;
        int dp = z ? AndroidUtilities.dp(8.0f) : 0;
        setPadding(dp, dp, dp, dp);
        invalidate();
    }

    public void setOnValueChange(Utilities.Callback2<Float, Boolean> callback2) {
        this.onValueChange = callback2;
    }

    public void setRoundRadiusDp(float f) {
        this.roundRadiusDp = f;
        invalidate();
    }

    public void setStops(float[] fArr) {
        this.stops = fArr;
    }

    public void setTextColor(int i) {
        this.leftTextDrawable.setTextColor(i);
        this.rightTextDrawable.setTextColor(i);
    }

    public void setValue(float f, boolean z) {
        ValueAnimator valueAnimator = this.valueAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.valueAnimator = null;
        }
        final float clamp = MathUtils.clamp(f, 0.0f, 1.0f);
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.value, clamp);
            this.valueAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ActionBarMenuSlider.this.lambda$setValue$0(valueAnimator2);
                }
            });
            this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ActionBarMenuSlider.this.valueAnimator = null;
                    ActionBarMenuSlider.this.value = clamp;
                    ActionBarMenuSlider.this.invalidate();
                }
            });
            this.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.valueAnimator.setDuration(220L);
            this.valueAnimator.start();
        } else {
            this.value = clamp;
            invalidate();
        }
        String leftStringValue = getLeftStringValue(clamp);
        if (leftStringValue != null && !TextUtils.equals(this.leftTextDrawable.getText(), leftStringValue)) {
            this.leftTextDrawable.cancelAnimation();
            this.leftTextDrawable.setText(leftStringValue, true);
        }
        String rightStringValue = getRightStringValue(clamp);
        if (rightStringValue != null && !TextUtils.equals(this.rightTextDrawable.getText(), rightStringValue)) {
            this.rightTextDrawable.cancelAnimation();
            this.rightTextDrawable.setText(rightStringValue, true);
        }
        this.fillPaint.setColor(getColorValue(clamp));
    }
}
