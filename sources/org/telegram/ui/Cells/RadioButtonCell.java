package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioButtonCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    public TextView valueTextView;

    public RadioButtonCell(Context context) {
        this(context, false);
    }

    public RadioButtonCell(Context context, boolean z) {
        super(context);
        int color;
        int i;
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
        RadioButton radioButton3 = this.radioButton;
        boolean z2 = LocaleController.isRTL;
        addView(radioButton3, LayoutHelper.createFrame(22, 22.0f, (z2 ? 5 : 3) | 48, z2 ? 0 : 20, 10.0f, z2 ? 20 : 0, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        TextView textView2 = this.textView;
        boolean z3 = LocaleController.isRTL;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 5 : 3) | 48, z3 ? 23 : 61, 10.0f, z3 ? 61 : 23, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(z ? Theme.key_dialogTextGray2 : Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(0);
        this.valueTextView.setMaxLines(0);
        this.valueTextView.setSingleLine(false);
        this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
        TextView textView4 = this.valueTextView;
        boolean z4 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (z4 ? 5 : 3) | 48, z4 ? 17 : 61, 35.0f, z4 ? 61 : 17, 0.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f), getHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(LocaleController.isRTL ? 60.0f : 0.0f), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.RadioButton");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.radioButton.isChecked());
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setTextAndValue(String str, String str2, boolean z, boolean z2) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.radioButton.setChecked(z2, false);
        this.needDivider = z;
    }
}
