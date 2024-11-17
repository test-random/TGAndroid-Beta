package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class DialogRadioCell extends FrameLayout {
    public int itemId;
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    public DialogRadioCell(Context context) {
        this(context, false);
    }

    public DialogRadioCell(Context context, boolean z) {
        super(context);
        int color;
        int i;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        TextView textView2 = this.textView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        TextView textView3 = this.textView;
        boolean z2 = LocaleController.isRTL;
        addView(textView3, LayoutHelper.createFrame(-1, -1.0f, (z2 ? 5 : 3) | 48, z2 ? 23.0f : 61.0f, 0.0f, z2 ? 61.0f : 23.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.valueTextView = textView4;
        textView4.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlue2 : Theme.key_windowBackgroundWhiteValueText));
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(truncateAt);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        this.valueTextView.setVisibility(8);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 23.0f, 0.0f, 23.0f, 0.0f));
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        RadioButton radioButton2 = this.radioButton;
        if (z) {
            color = Theme.getColor(Theme.key_dialogRadioBackground);
            i = Theme.key_dialogRadioBackgroundChecked;
        } else {
            color = Theme.getColor(Theme.key_radioBackground);
            i = Theme.key_radioBackgroundChecked;
        }
        radioButton2.setColor(color, Theme.getColor(i));
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, 20.0f, 15.0f, 20.0f, 0.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f), getHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(LocaleController.isRTL ? 60.0f : 0.0f), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp((this.valueTextView.getVisibility() == 0 ? 12 : 0) + 84);
        this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), 1073741824));
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
            measuredWidth -= this.valueTextView.getMeasuredWidth() + AndroidUtilities.dp(12.0f);
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setEnabled(boolean z, boolean z2) {
        super.setEnabled(z);
        if (z2) {
            this.textView.animate().alpha(z ? 1.0f : 0.5f).start();
            this.valueTextView.animate().alpha(z ? 1.0f : 0.5f).start();
            this.radioButton.animate().alpha(z ? 1.0f : 0.5f).start();
        } else {
            this.textView.setAlpha(z ? 1.0f : 0.5f);
            this.valueTextView.setAlpha(z ? 1.0f : 0.5f);
            this.radioButton.setAlpha(z ? 1.0f : 0.5f);
        }
    }

    public void setText(CharSequence charSequence, boolean z, boolean z2) {
        this.valueTextView.setVisibility(8);
        this.textView.setText(charSequence);
        this.radioButton.setChecked(z, false);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public void setTextAndValue(CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        this.valueTextView.setVisibility(0);
        this.valueTextView.setText(charSequence2);
        this.textView.setText(charSequence);
        this.radioButton.setChecked(z, false);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}
