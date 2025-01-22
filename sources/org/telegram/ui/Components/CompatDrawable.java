package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

public abstract class CompatDrawable extends Drawable {
    public final Paint paint = new Paint(1);

    public CompatDrawable(View view) {
        if (view != null) {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view2) {
                    CompatDrawable.this.onAttachedToWindow();
                }

                @Override
                public void onViewDetachedFromWindow(View view2) {
                    CompatDrawable.this.onDetachedToWindow();
                }
            });
            if (view.isAttachedToWindow()) {
                view.post(new Runnable() {
                    @Override
                    public final void run() {
                        CompatDrawable.this.onAttachedToWindow();
                    }
                });
            }
        }
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public void onAttachedToWindow() {
    }

    public void onDetachedToWindow() {
    }

    @Override
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }
}
