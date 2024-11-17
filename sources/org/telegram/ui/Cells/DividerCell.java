package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class DividerCell extends View {
    private boolean forceDarkTheme;
    private Paint paint;
    private Theme.ResourcesProvider resourcesProvider;

    public DividerCell(Context context) {
        this(context, null);
    }

    public DividerCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.paint = new Paint();
        this.resourcesProvider = resourcesProvider;
        setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint;
        int color;
        if (this.forceDarkTheme) {
            paint = this.paint;
            color = ColorUtils.blendARGB(-16777216, Theme.getColor(Theme.key_voipgroup_dialogBackground, this.resourcesProvider), 0.2f);
        } else {
            paint = this.paint;
            color = Theme.getColor(Theme.key_divider, this.resourcesProvider);
        }
        paint.setColor(color);
        canvas.drawLine(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getPaddingTop(), this.paint);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), getPaddingTop() + getPaddingBottom() + 1);
    }

    public void setForceDarkTheme(boolean z) {
        this.forceDarkTheme = z;
    }
}
