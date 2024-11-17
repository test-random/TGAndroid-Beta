package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Property;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;

public class WallpaperCheckBoxView extends View {
    public final Property PROGRESS_PROPERTY;
    private Paint backgroundPaint;
    private ObjectAnimator checkAnimator;
    private Paint checkPaint;
    private int[] colors;
    private String currentText;
    private int currentTextSize;
    private float dimAmount;
    private final Paint dimPaint;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private Paint eraserPaint;
    private boolean isChecked;
    private int maxTextSize;
    private View parentView;
    private float progress;
    private RectF rect;
    Theme.ResourcesProvider resourcesProvider;
    private TextPaint textPaint;

    public WallpaperCheckBoxView(Context context, boolean z, View view, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.colors = new int[4];
        this.PROGRESS_PROPERTY = new AnimationProperties.FloatProperty("progress") {
            @Override
            public Float get(WallpaperCheckBoxView wallpaperCheckBoxView) {
                return Float.valueOf(WallpaperCheckBoxView.this.progress);
            }

            @Override
            public void setValue(WallpaperCheckBoxView wallpaperCheckBoxView, float f) {
                WallpaperCheckBoxView.this.progress = f;
                WallpaperCheckBoxView.this.invalidate();
            }
        };
        this.dimPaint = new Paint(1);
        this.resourcesProvider = resourcesProvider;
        this.rect = new RectF();
        if (z) {
            this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Bitmap.Config.ARGB_4444);
            this.drawCanvas = new Canvas(this.drawBitmap);
        }
        this.parentView = view;
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.bold());
        Paint paint = new Paint(1);
        this.checkPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint2 = this.checkPaint;
        PorterDuff.Mode mode = PorterDuff.Mode.CLEAR;
        paint2.setXfermode(new PorterDuffXfermode(mode));
        Paint paint3 = new Paint(1);
        this.eraserPaint = paint3;
        paint3.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(mode));
        this.backgroundPaint = new Paint(1);
    }

    private void animateToCheckedState(boolean z) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, (Property<WallpaperCheckBoxView, Float>) this.PROGRESS_PROPERTY, z ? 1.0f : 0.0f);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(300L);
        this.checkAnimator.start();
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private void setProgress(float f) {
        if (this.progress == f) {
            return;
        }
        this.progress = f;
        invalidate();
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float f;
        this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        Theme.applyServiceShaderMatrixForView(this, this.parentView, this.resourcesProvider);
        canvas.drawRoundRect(this.rect, getMeasuredHeight() / 2, getMeasuredHeight() / 2, getThemedPaint("paintChatActionBackground"));
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider == null ? Theme.hasGradientService() : resourcesProvider.hasGradientService()) {
            canvas.drawRoundRect(this.rect, getMeasuredHeight() / 2, getMeasuredHeight() / 2, getThemedPaint("paintChatActionBackgroundDarken"));
        }
        if (this.dimAmount > 0.0f) {
            canvas.drawRoundRect(this.rect, getMeasuredHeight() / 2, getMeasuredHeight() / 2, this.dimPaint);
        }
        TextPaint textPaint = this.textPaint;
        int i = Theme.key_chat_serviceText;
        textPaint.setColor(Theme.getColor(i, this.resourcesProvider));
        int measuredWidth = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
        canvas.drawText(this.currentText, AndroidUtilities.dp(28.0f) + measuredWidth, AndroidUtilities.dp(21.0f), this.textPaint);
        canvas.save();
        canvas.translate(measuredWidth, AndroidUtilities.dp(7.0f));
        int i2 = 0;
        if (this.drawBitmap != null) {
            float f2 = this.progress;
            float f3 = f2 / 0.5f;
            if (f2 <= 0.5f) {
                f = f3;
            } else {
                f = 2.0f - f3;
                f3 = 1.0f;
            }
            float dp = AndroidUtilities.dp(1.0f) * f;
            this.rect.set(dp, dp, AndroidUtilities.dp(18.0f) - dp, AndroidUtilities.dp(18.0f) - dp);
            this.drawBitmap.eraseColor(0);
            this.backgroundPaint.setColor(Theme.getColor(i, this.resourcesProvider));
            Canvas canvas2 = this.drawCanvas;
            RectF rectF = this.rect;
            canvas2.drawRoundRect(rectF, rectF.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            if (f3 != 1.0f) {
                float min = Math.min(AndroidUtilities.dp(7.0f), (AndroidUtilities.dp(7.0f) * f3) + dp);
                this.rect.set(AndroidUtilities.dp(2.0f) + min, AndroidUtilities.dp(2.0f) + min, AndroidUtilities.dp(16.0f) - min, AndroidUtilities.dp(16.0f) - min);
                Canvas canvas3 = this.drawCanvas;
                RectF rectF2 = this.rect;
                canvas3.drawRoundRect(rectF2, rectF2.width() / 2.0f, this.rect.height() / 2.0f, this.eraserPaint);
            }
            if (this.progress > 0.5f) {
                float f4 = 1.0f - f;
                this.drawCanvas.drawLine(AndroidUtilities.dp(7.3f), AndroidUtilities.dp(13.0f), (int) (AndroidUtilities.dp(7.3f) - (AndroidUtilities.dp(2.5f) * f4)), (int) (AndroidUtilities.dp(13.0f) - (AndroidUtilities.dp(2.5f) * f4)), this.checkPaint);
                this.drawCanvas.drawLine(AndroidUtilities.dp(7.3f), AndroidUtilities.dp(13.0f), (int) (AndroidUtilities.dp(7.3f) + (AndroidUtilities.dp(6.0f) * f4)), (int) (AndroidUtilities.dp(13.0f) - (AndroidUtilities.dp(6.0f) * f4)), this.checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
        } else {
            this.rect.set(0.0f, 0.0f, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
            int[] iArr = this.colors;
            if (iArr[3] != 0) {
                while (i2 < 4) {
                    this.backgroundPaint.setColor(this.colors[i2]);
                    canvas.drawArc(this.rect, (i2 * 90) - 90, 90.0f, true, this.backgroundPaint);
                    i2++;
                }
            } else if (iArr[2] != 0) {
                while (i2 < 3) {
                    this.backgroundPaint.setColor(this.colors[i2]);
                    canvas.drawArc(this.rect, (i2 * 120) - 90, 120.0f, true, this.backgroundPaint);
                    i2++;
                }
            } else if (iArr[1] != 0) {
                while (i2 < 2) {
                    this.backgroundPaint.setColor(this.colors[i2]);
                    canvas.drawArc(this.rect, (i2 * 180) - 90, 180.0f, true, this.backgroundPaint);
                    i2++;
                }
            } else {
                this.backgroundPaint.setColor(iArr[0]);
                RectF rectF3 = this.rect;
                canvas.drawRoundRect(rectF3, rectF3.width() / 2.0f, this.rect.height() / 2.0f, this.backgroundPaint);
            }
        }
        canvas.restore();
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
    }

    public void setChecked(boolean z, boolean z2) {
        if (z == this.isChecked) {
            return;
        }
        this.isChecked = z;
        if (z2) {
            animateToCheckedState(z);
            return;
        }
        cancelCheckAnimator();
        this.progress = z ? 1.0f : 0.0f;
        invalidate();
    }

    public void setColor(int i, int i2) {
        if (this.colors == null) {
            this.colors = new int[4];
        }
        this.colors[i] = i2;
        invalidate();
    }

    public void setDimAmount(float f) {
        this.dimAmount = f;
        this.dimPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (f * 255.0f)));
        invalidate();
    }

    public void setText(String str, int i, int i2) {
        this.currentText = str;
        this.currentTextSize = i;
        this.maxTextSize = i2;
    }
}
