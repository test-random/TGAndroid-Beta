package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class ManageChatTextCell extends FrameLayout {
    private boolean divider;
    private int dividerColor;
    private ImageView imageView;
    private SimpleTextView textView;
    private SimpleTextView valueTextView;

    public ManageChatTextCell(Context context) {
        super(context);
        this.dividerColor = 0;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(16);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.valueTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        this.valueTextView.setTextSize(16);
        this.valueTextView.setGravity(LocaleController.isRTL ? 3 : 5);
        addView(this.valueTextView);
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
    }

    protected int getFullHeight() {
        return AndroidUtilities.dp(56.0f);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.divider) {
            int i = this.dividerColor;
            if (i != 0) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(i));
            }
            canvas.drawLine(AndroidUtilities.dp(71.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, this.dividerColor != 0 ? Theme.dividerExtraPaint : Theme.dividerPaint);
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int i6 = i3 - i;
        int textHeight = (i5 - this.valueTextView.getTextHeight()) / 2;
        int dp = LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(dp, textHeight, simpleTextView.getMeasuredWidth() + dp, this.valueTextView.getMeasuredHeight() + textHeight);
        int textHeight2 = (i5 - this.textView.getTextHeight()) / 2;
        int dp2 = !LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : AndroidUtilities.dp(24.0f);
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(dp2, textHeight2, simpleTextView2.getMeasuredWidth() + dp2, this.textView.getMeasuredHeight() + textHeight2);
        int dp3 = AndroidUtilities.dp(9.0f);
        int dp4 = !LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(21.0f);
        ImageView imageView = this.imageView;
        imageView.layout(dp4, dp3, imageView.getMeasuredWidth() + dp4, this.imageView.getMeasuredHeight() + dp3);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(24.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        setMeasuredDimension(size, getFullHeight() + (this.divider ? 1 : 0));
    }

    public void setColors(int i, int i2) {
        this.textView.setTextColor(Theme.getColor(i2));
        this.textView.setTag(Integer.valueOf(i2));
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.MULTIPLY));
        this.imageView.setTag(Integer.valueOf(i));
    }

    public void setDividerColor(int i) {
        this.dividerColor = i;
    }

    public void setText(String str, String str2, int i, int i2, boolean z) {
        this.textView.setText(str);
        if (str2 != null) {
            this.valueTextView.setText(str2);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.imageView.setPadding(0, AndroidUtilities.dp(i2), 0, 0);
        this.imageView.setImageResource(i);
        this.divider = z;
        setWillNotDraw(!z);
    }

    public void setText(String str, String str2, int i, boolean z) {
        setText(str, str2, i, 5, z);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}
