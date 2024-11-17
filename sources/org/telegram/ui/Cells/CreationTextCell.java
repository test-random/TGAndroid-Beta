package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class CreationTextCell extends FrameLayout {
    boolean divider;
    private ImageView imageView;
    public int startPadding;
    private SimpleTextView textView;

    public CreationTextCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.startPadding = i;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        SimpleTextView simpleTextView2 = this.textView;
        int i2 = Theme.key_windowBackgroundWhiteBlueText2;
        simpleTextView2.setTextColor(Theme.getColor(i2, resourcesProvider));
        this.textView.setTag(Integer.valueOf(i2));
        addView(this.textView);
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.divider) {
            canvas.drawLine(AndroidUtilities.dp(this.startPadding), getMeasuredHeight() - 1, getMeasuredWidth() + AndroidUtilities.dp(23.0f), getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int dp;
        int i5 = i3 - i;
        int textHeight = ((i4 - i2) - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            dp = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.startPadding : 25.0f);
        } else {
            dp = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.startPadding : 25.0f);
        }
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(dp, textHeight, simpleTextView.getMeasuredWidth() + dp, this.textView.getMeasuredHeight() + textHeight);
        int dp2 = !LocaleController.isRTL ? (AndroidUtilities.dp(this.startPadding) - this.imageView.getMeasuredWidth()) / 2 : (i5 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(25.0f);
        ImageView imageView = this.imageView;
        imageView.layout(dp2, 0, imageView.getMeasuredWidth() + dp2, this.imageView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        AndroidUtilities.dp(48.0f);
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        setMeasuredDimension(size, AndroidUtilities.dp(50.0f));
    }

    public void setTextAndIcon(String str, Drawable drawable, boolean z) {
        this.textView.setText(str);
        this.imageView.setImageDrawable(drawable);
        this.divider = z;
    }
}
