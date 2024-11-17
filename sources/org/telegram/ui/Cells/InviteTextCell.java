package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class InviteTextCell extends FrameLayout {
    private ImageView imageView;
    private SimpleTextView textView;

    public InviteTextCell(Context context) {
        super(context);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(17);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int i6 = i3 - i;
        int textHeight = (i5 - this.textView.getTextHeight()) / 2;
        int dp = AndroidUtilities.dp(!LocaleController.isRTL ? 71.0f : 24.0f);
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(dp, textHeight, simpleTextView.getMeasuredWidth() + dp, this.textView.getMeasuredHeight() + textHeight);
        int measuredHeight = (i5 - this.imageView.getMeasuredHeight()) / 2;
        int dp2 = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
        ImageView imageView = this.imageView;
        imageView.layout(dp2, measuredHeight, imageView.getMeasuredWidth() + dp2, this.imageView.getMeasuredHeight() + measuredHeight);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(72.0f);
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        setMeasuredDimension(size, AndroidUtilities.dp(72.0f));
    }

    public void setTextAndIcon(String str, int i) {
        this.textView.setText(str);
        this.imageView.setImageResource(i);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}
