package org.telegram.ui.Cells;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextColorCell extends FrameLayout {
    private static Paint colorPaint;
    public static final int[] colors = {-1031100, -29183, -12769, -8792480, -12521994, -12140801, -2984711, -45162, -4473925};
    public static final int[] colorsToSave = {-65536, -29183, -256, -16711936, -16711681, -16776961, -2984711, -65281, -1};
    private int currentColor;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public TextColorCell(Context context) {
        this(context, null);
    }

    public TextColorCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        if (colorPaint == null) {
            colorPaint = new Paint(1);
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
        int i = this.currentColor;
        if (i != 0) {
            colorPaint.setColor(i);
            canvas.drawCircle(LocaleController.isRTL ? AndroidUtilities.dp(33.0f) : getMeasuredWidth() - AndroidUtilities.dp(33.0f), getMeasuredHeight() / 2, AndroidUtilities.dp(10.0f), colorPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setEnabled(boolean z, ArrayList arrayList) {
        super.setEnabled(z);
        if (arrayList == null) {
            this.textView.setAlpha(z ? 1.0f : 0.5f);
            setAlpha(z ? 1.0f : 0.5f);
        } else {
            TextView textView = this.textView;
            Property property = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property, z ? 1.0f : 0.5f));
            arrayList.add(ObjectAnimator.ofFloat(this, (Property<TextColorCell, Float>) property, z ? 1.0f : 0.5f));
        }
    }

    public void setTextAndColor(String str, int i, boolean z) {
        this.textView.setText(str);
        this.needDivider = z;
        this.currentColor = i;
        setWillNotDraw(!z && i == 0);
        invalidate();
    }
}
