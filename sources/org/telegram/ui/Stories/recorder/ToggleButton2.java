package org.telegram.ui.Stories.recorder;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Stories.recorder.FlashViews;

public class ToggleButton2 extends View implements FlashViews.Invertable {
    private Bitmap activeBitmap;
    private final Paint activeBitmapPaint;
    private final Paint activePaint;
    private final AnimatedFloat animatedSelected;
    private ValueAnimator animator;
    private final Path clipPath;
    private int currentIcon;
    private Drawable drawable;
    private float scale;
    private boolean selected;

    public ToggleButton2(Context context) {
        super(context);
        this.clipPath = new Path();
        Paint paint = new Paint(1);
        this.activePaint = paint;
        Paint paint2 = new Paint(3);
        this.activeBitmapPaint = paint2;
        this.animatedSelected = new AnimatedFloat(this, 0L, 380L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.scale = 1.0f;
        paint.setColor(-1);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    public void lambda$setIcon$0(AtomicBoolean atomicBoolean, int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.scale = Math.abs(floatValue - 0.5f) + 0.5f;
        if (floatValue >= 0.5f && !atomicBoolean.get()) {
            atomicBoolean.set(true);
            setDrawable(i);
        }
        invalidate();
    }

    public void lambda$setIcon$1(AtomicBoolean atomicBoolean, Drawable drawable, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.scale = Math.abs(floatValue - 0.5f) + 0.5f;
        if (floatValue < 0.5f || atomicBoolean.get()) {
            return;
        }
        atomicBoolean.set(true);
        setDrawable(drawable);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.drawable == null) {
            return;
        }
        float f = this.animatedSelected.set(this.selected);
        int intrinsicWidth = this.drawable.getIntrinsicWidth();
        int intrinsicHeight = this.drawable.getIntrinsicHeight();
        Rect rect = AndroidUtilities.rectTmp2;
        rect.set((getWidth() - intrinsicWidth) / 2, (getHeight() - intrinsicHeight) / 2, (getWidth() + intrinsicWidth) / 2, (getHeight() + intrinsicHeight) / 2);
        if (f <= 0.0f) {
            this.drawable.setBounds(rect);
            this.drawable.draw(canvas);
        } else if (f < 1.0f) {
            canvas.save();
            this.clipPath.rewind();
            this.clipPath.addCircle(getWidth() / 2.0f, getHeight() / 2.0f, AndroidUtilities.dp(16.0f) * f, Path.Direction.CW);
            canvas.clipPath(this.clipPath, Region.Op.DIFFERENCE);
            this.drawable.setBounds(rect);
            this.drawable.draw(canvas);
            canvas.restore();
        }
        if (f > 0.0f) {
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, AndroidUtilities.dp(16.0f) * f, this.activePaint);
            canvas.save();
            Bitmap bitmap = this.activeBitmap;
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, (Rect) null, rect, this.activeBitmapPaint);
            }
            canvas.restore();
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.activeBitmap != null || this.currentIcon == 0) {
            return;
        }
        this.activeBitmap = BitmapFactory.decodeResource(getResources(), this.currentIcon);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Bitmap bitmap = this.activeBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.activeBitmap = null;
        }
    }

    public void setDrawable(int i) {
        this.drawable = getContext().getResources().getDrawable(i).mutate();
        Bitmap bitmap = this.activeBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.activeBitmap = null;
        }
        if (this.activeBitmap == null && i != 0) {
            this.activeBitmap = BitmapFactory.decodeResource(getResources(), i);
        }
        invalidate();
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        Bitmap bitmap = this.activeBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.activeBitmap = null;
        }
        if (this.activeBitmap == null && drawable != null && drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
            Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            this.activeBitmap = createBitmap;
            drawable.setBounds(0, 0, createBitmap.getWidth(), this.activeBitmap.getHeight());
            drawable.draw(new Canvas(this.activeBitmap));
        }
        invalidate();
    }

    public void setIcon(final int i, boolean z) {
        if (this.currentIcon == i) {
            return;
        }
        this.currentIcon = i;
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
        if (!z) {
            this.scale = 1.0f;
            setDrawable(i);
        } else {
            this.animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
            final AtomicBoolean atomicBoolean = new AtomicBoolean();
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ToggleButton2.this.lambda$setIcon$0(atomicBoolean, i, valueAnimator2);
                }
            });
            this.animator.start();
        }
    }

    public void setIcon(final Drawable drawable, boolean z) {
        if (this.drawable == drawable) {
            return;
        }
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
        if (!z) {
            this.scale = 1.0f;
            setDrawable(drawable);
        } else {
            this.animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
            final AtomicBoolean atomicBoolean = new AtomicBoolean();
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ToggleButton2.this.lambda$setIcon$1(atomicBoolean, drawable, valueAnimator2);
                }
            });
            this.animator.start();
        }
    }

    @Override
    public void setInvert(float f) {
        Drawable drawable = this.drawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(-1, -16777216, f), PorterDuff.Mode.MULTIPLY));
        }
        this.activePaint.setColor(ColorUtils.blendARGB(-1, -16777216, f));
        invalidate();
    }

    @Override
    public void setSelected(boolean z) {
        this.selected = z;
        invalidate();
    }

    public void setSelected(boolean z, boolean z2) {
        this.selected = z;
        if (!z2) {
            this.animatedSelected.set(z ? 1.0f : 0.0f, true);
        }
        invalidate();
    }
}
