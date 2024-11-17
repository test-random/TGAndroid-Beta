package org.telegram.ui.Components.Premium.boosts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class GradientButtonWithCounterView extends ButtonWithCounterView {
    private final CellFlickerDrawable flickerDrawable;
    private boolean incGradient;
    private float progress;
    private final RectF rect;

    public GradientButtonWithCounterView(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context, z, resourcesProvider);
        this.rect = new RectF();
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.flickerDrawable = cellFlickerDrawable;
        cellFlickerDrawable.animationSpeedScale = 1.2f;
        cellFlickerDrawable.drawFrame = false;
        cellFlickerDrawable.repeatProgress = 4.0f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        boolean z;
        if (this.incGradient) {
            float f = this.progress + 0.016f;
            this.progress = f;
            if (f > 3.0f) {
                z = false;
                this.incGradient = z;
            }
        } else {
            float f2 = this.progress - 0.016f;
            this.progress = f2;
            if (f2 < 1.0f) {
                z = true;
                this.incGradient = z;
            }
        }
        this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), (-getMeasuredWidth()) * 0.1f * this.progress, 0.0f);
        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), PremiumGradient.getInstance().getMainGradientPaint());
        this.flickerDrawable.setParentWidth(getMeasuredWidth());
        this.flickerDrawable.draw(canvas, this.rect, AndroidUtilities.dp(8.0f), null);
        super.onDraw(canvas);
        invalidate();
    }
}
