package org.telegram.ui.Components;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;

public class CanvasButton {
    private static final int[] pressedState = {16842910, 16842919};
    boolean buttonPressed;
    private Runnable delegate;
    CornerPath drawingPath;
    private boolean longPressEnabled;
    Runnable longPressRunnable;
    Paint maskPaint;
    private final View parent;
    private boolean pathCreated;
    CornerPathEffect pathEffect;
    boolean rounded;
    Drawable selectorDrawable;
    int usingRectCount;
    ArrayList drawingRects = new ArrayList();
    Paint paint = new Paint(1);
    Runnable longPressRunnableInner = new Runnable() {
        @Override
        public void run() {
            CanvasButton.this.checkTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
            CanvasButton.this.parent.performHapticFeedback(0);
            Runnable runnable = CanvasButton.this.longPressRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    };
    float roundRadius = AndroidUtilities.dp(12.0f);

    public CanvasButton(View view) {
        this.parent = view;
        Paint paint = this.paint;
        CornerPathEffect cornerPathEffect = new CornerPathEffect(this.roundRadius);
        this.pathEffect = cornerPathEffect;
        paint.setPathEffect(cornerPathEffect);
        if (Build.VERSION.SDK_INT >= 21) {
            Paint paint2 = new Paint(1);
            this.maskPaint = paint2;
            paint2.setFilterBitmap(true);
            this.maskPaint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(12.0f)));
            this.maskPaint.setColor(-1);
            final Paint paint3 = new Paint(1);
            paint3.setFilterBitmap(true);
            paint3.setColor(-1);
            BaseCell.RippleDrawableSafe rippleDrawableSafe = new BaseCell.RippleDrawableSafe(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor(Theme.key_listSelector) & 436207615}), null, new Drawable() {
                @Override
                public void draw(Canvas canvas) {
                    CanvasButton canvasButton = CanvasButton.this;
                    canvasButton.drawInternal(canvas, canvasButton.usingRectCount > 1 ? canvasButton.maskPaint : paint3);
                }

                @Override
                public int getOpacity() {
                    return -2;
                }

                @Override
                public void setAlpha(int i) {
                }

                @Override
                public void setColorFilter(ColorFilter colorFilter) {
                }
            });
            this.selectorDrawable = rippleDrawableSafe;
            rippleDrawableSafe.setCallback(view);
        }
    }

    private boolean contains(int i, int i2) {
        for (int i3 = 0; i3 < this.usingRectCount; i3++) {
            if (((RectF) this.drawingRects.get(i3)).contains(i, i2)) {
                return true;
            }
        }
        return false;
    }

    public void drawInternal(Canvas canvas, Paint paint) {
        int i = this.usingRectCount;
        int i2 = 0;
        if (i <= 1) {
            if (i == 1) {
                Drawable drawable = this.selectorDrawable;
                if (drawable != null) {
                    drawable.setBounds((int) ((RectF) this.drawingRects.get(0)).left, (int) ((RectF) this.drawingRects.get(0)).top, (int) ((RectF) this.drawingRects.get(0)).right, (int) ((RectF) this.drawingRects.get(0)).bottom);
                }
                if (!this.rounded) {
                    paint.setPathEffect(this.pathEffect);
                    canvas.drawRoundRect((RectF) this.drawingRects.get(0), 0.0f, 0.0f, paint);
                    return;
                } else {
                    paint.setPathEffect(null);
                    float min = Math.min(((RectF) this.drawingRects.get(0)).width(), ((RectF) this.drawingRects.get(0)).height()) / 2.0f;
                    canvas.drawRoundRect((RectF) this.drawingRects.get(0), min, min, paint);
                    return;
                }
            }
            return;
        }
        if (!this.pathCreated) {
            CornerPath cornerPath = this.drawingPath;
            if (cornerPath == null) {
                this.drawingPath = new CornerPath(2);
            } else {
                cornerPath.rewind();
            }
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (true) {
                int i7 = this.usingRectCount;
                if (i2 >= i7) {
                    break;
                }
                int i8 = i2 + 1;
                if (i8 < i7) {
                    float f = ((RectF) this.drawingRects.get(i2)).right;
                    float f2 = ((RectF) this.drawingRects.get(i8)).right;
                    if (Math.abs(f - f2) < AndroidUtilities.dp(4.0f)) {
                        RectF rectF = (RectF) this.drawingRects.get(i8);
                        RectF rectF2 = (RectF) this.drawingRects.get(i2);
                        float max = Math.max(f, f2);
                        rectF2.right = max;
                        rectF.right = max;
                    }
                }
                if (i2 == 0 || ((RectF) this.drawingRects.get(i2)).bottom > i3) {
                    i3 = (int) ((RectF) this.drawingRects.get(i2)).bottom;
                }
                if (i2 == 0 || ((RectF) this.drawingRects.get(i2)).right > i4) {
                    i4 = (int) ((RectF) this.drawingRects.get(i2)).right;
                }
                if (i2 == 0 || ((RectF) this.drawingRects.get(i2)).left < i5) {
                    i5 = (int) ((RectF) this.drawingRects.get(i2)).left;
                }
                if (i2 == 0 || ((RectF) this.drawingRects.get(i2)).top < i6) {
                    i6 = (int) ((RectF) this.drawingRects.get(i2)).top;
                }
                this.drawingPath.addRect((RectF) this.drawingRects.get(i2), Path.Direction.CCW);
                Drawable drawable2 = this.selectorDrawable;
                if (drawable2 != null) {
                    drawable2.setBounds(i5, i6, i4, i3);
                }
                i2 = i8;
            }
            this.drawingPath.closeRects();
            this.pathCreated = true;
        }
        paint.setPathEffect(this.pathEffect);
        CornerPath cornerPath2 = this.drawingPath;
        if (cornerPath2 != null) {
            canvas.drawPath(cornerPath2, paint);
        }
    }

    public void addRect(RectF rectF) {
        int i = this.usingRectCount + 1;
        this.usingRectCount = i;
        if (i > this.drawingRects.size()) {
            this.drawingRects.add(new RectF());
        }
        ((RectF) this.drawingRects.get(this.usingRectCount - 1)).set(rectF);
    }

    public void cancelRipple() {
        Drawable drawable;
        if (Build.VERSION.SDK_INT < 21 || (drawable = this.selectorDrawable) == null) {
            return;
        }
        drawable.setState(StateSet.NOTHING);
        this.selectorDrawable.jumpToCurrentState();
    }

    public boolean checkTouchEvent(MotionEvent motionEvent) {
        Drawable drawable;
        Runnable runnable;
        Drawable drawable2;
        Drawable drawable3;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (contains(x, y)) {
                this.buttonPressed = true;
                if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.selectorDrawable) != null) {
                    drawable3.setHotspot(x, y);
                    this.selectorDrawable.setState(pressedState);
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnableInner);
                if (this.longPressEnabled) {
                    AndroidUtilities.runOnUIThread(this.longPressRunnableInner, ViewConfiguration.getLongPressTimeout());
                }
                this.parent.invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.buttonPressed) {
                if (motionEvent.getAction() == 1 && (runnable = this.delegate) != null) {
                    runnable.run();
                }
                this.parent.playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable) != null) {
                    drawable.setState(StateSet.NOTHING);
                }
                this.buttonPressed = false;
                this.parent.invalidate();
            }
            AndroidUtilities.cancelRunOnUIThread(this.longPressRunnableInner);
        } else if (motionEvent.getAction() == 2 && this.buttonPressed && Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable) != null) {
            drawable2.setHotspot(x, y);
        }
        return this.buttonPressed;
    }

    public void draw(Canvas canvas) {
        drawInternal(canvas, this.paint);
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void rewind() {
        this.pathCreated = false;
        this.usingRectCount = 0;
    }

    public void setColor(int i) {
        setColor(i, i);
    }

    public void setColor(int i, int i2) {
        this.paint.setColor(i);
        Drawable drawable = this.selectorDrawable;
        if (drawable == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        Theme.setSelectorDrawableColor(drawable, i2, true);
    }

    public void setDelegate(Runnable runnable) {
        this.delegate = runnable;
    }

    public void setLongPress(Runnable runnable) {
        this.longPressEnabled = true;
        this.longPressRunnable = runnable;
    }

    public void setRect(int i, int i2, int i3, int i4) {
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(i, i2, i3, i4);
        setRect(rectF);
    }

    public void setRect(RectF rectF) {
        rewind();
        addRect(rectF);
    }

    public void setRounded(boolean z) {
        this.rounded = z;
    }
}
