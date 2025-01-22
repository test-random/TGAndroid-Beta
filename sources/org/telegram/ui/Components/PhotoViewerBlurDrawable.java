package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.PhotoViewer;

public class PhotoViewerBlurDrawable extends CompatDrawable {
    private boolean applyBounds;
    private final BlurringShader.StoryBlurDrawer backgroundBlur;
    private final Path path;
    private final PhotoViewer photoViewer;
    private final RectF rect;
    private int rounding;
    private final View view;

    public PhotoViewerBlurDrawable(PhotoViewer photoViewer, BlurringShader.BlurManager blurManager, View view) {
        super(view);
        this.rounding = -1;
        this.applyBounds = true;
        this.path = new Path();
        this.rect = new RectF();
        this.photoViewer = photoViewer;
        this.view = view;
        this.backgroundBlur = new BlurringShader.StoryBlurDrawer(blurManager, view, 0, false);
    }

    @Override
    public void draw(Canvas canvas) {
        android.graphics.Rect bounds = getBounds();
        canvas.save();
        this.path.rewind();
        float alpha = this.paint.getAlpha() / 255.0f;
        int i = this.rounding;
        float min = i == -1 ? Math.min(bounds.width(), bounds.height()) / 2.0f : i;
        this.rect.set(bounds);
        this.path.addRoundRect(this.rect, min, min, Path.Direction.CW);
        canvas.clipPath(this.path);
        for (View view = this.view; view != null && view != this.photoViewer.windowView && (view.getParent() instanceof View); view = (View) view.getParent()) {
            canvas.translate(-view.getX(), -view.getY());
        }
        if (this.applyBounds) {
            RectF rectF = this.rect;
            canvas.translate(rectF.left, rectF.top);
        }
        this.photoViewer.drawCaptionBlur(canvas, this.backgroundBlur, Theme.multAlpha(-14277082, alpha), Theme.multAlpha(855638016, alpha), false, true, false);
        canvas.restore();
    }

    public PhotoViewerBlurDrawable setApplyBounds(boolean z) {
        this.applyBounds = z;
        return this;
    }
}
