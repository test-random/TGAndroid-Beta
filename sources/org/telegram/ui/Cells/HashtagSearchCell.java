package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class HashtagSearchCell extends TextView {
    private boolean needDivider;

    public HashtagSearchCell(Context context) {
        super(context);
        setGravity(16);
        setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        setTextSize(1, 17.0f);
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(0.0f, getHeight() - 1, getWidth(), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(48.0f) + 1);
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }
}
