package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextPriceCell extends FrameLayout {
    private TextView textView;
    private TextView valueTextView;

    public TextPriceCell(Context context) {
        super(context);
        setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        TextView textView2 = this.textView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextSize(1, 16.0f);
        this.valueTextView.setTypeface(AndroidUtilities.bold());
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(truncateAt);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(40.0f));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec((measuredWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        int i;
        Typeface typeface;
        TextView textView;
        this.textView.setText(str);
        TextView textView2 = this.valueTextView;
        if (str2 != null) {
            textView2.setText(str2);
            textView2 = this.valueTextView;
            i = 0;
        } else {
            i = 4;
        }
        textView2.setVisibility(i);
        if (z) {
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            setTag(Integer.valueOf(i2));
            this.textView.setTextColor(Theme.getColor(i2));
            this.valueTextView.setTextColor(Theme.getColor(i2));
            this.textView.setTypeface(AndroidUtilities.bold());
            textView = this.valueTextView;
            typeface = AndroidUtilities.bold();
        } else {
            int i3 = Theme.key_windowBackgroundWhiteGrayText2;
            setTag(Integer.valueOf(i3));
            this.textView.setTextColor(Theme.getColor(i3));
            this.valueTextView.setTextColor(Theme.getColor(i3));
            TextView textView3 = this.textView;
            typeface = Typeface.DEFAULT;
            textView3.setTypeface(typeface);
            textView = this.valueTextView;
        }
        textView.setTypeface(typeface);
        requestLayout();
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextValueColor(int i) {
        this.valueTextView.setTextColor(i);
    }
}
