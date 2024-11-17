package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioColorCell extends FrameLayout {
    public int heightDp;
    private RadioButton radioButton;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView text2View;
    private TextView textView;

    public RadioColorCell(Context context) {
        this(context, null);
    }

    public RadioColorCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.heightDp = 50;
        this.resourcesProvider = resourcesProvider;
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(getThemedColor(Theme.key_dialogRadioBackground), getThemedColor(Theme.key_dialogRadioBackgroundChecked));
        RadioButton radioButton2 = this.radioButton;
        boolean z = LocaleController.isRTL;
        addView(radioButton2, LayoutHelper.createFrame(22, 22.0f, (z ? 5 : 3) | 48, z ? 0 : 18, 14.0f, z ? 18 : 0, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        TextView textView2 = this.textView;
        boolean z2 = LocaleController.isRTL;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 51, 13.0f, z2 ? 51 : 21, 0.0f));
        TextView textView3 = new TextView(context);
        this.text2View = textView3;
        textView3.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        this.text2View.setTextSize(1, 14.0f);
        this.text2View.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.text2View.setVisibility(8);
        TextView textView4 = this.text2View;
        boolean z3 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 5 : 3) | 48, z3 ? 21 : 51, 37.0f, z3 ? 51 : 21, 0.0f));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
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
        if (this.text2View.getVisibility() == 0) {
            this.text2View.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i) - AndroidUtilities.dp(72.0f), 1073741824), i2);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.heightDp) + (this.text2View.getVisibility() == 0 ? AndroidUtilities.dp(4.0f) + this.text2View.getMeasuredHeight() : 0), 1073741824));
    }

    public void setCheckColor(int i, int i2) {
        this.radioButton.setColor(i, i2);
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setTextAndText2AndValue(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        this.textView.setText(charSequence);
        this.text2View.setVisibility(0);
        this.text2View.setText(charSequence2);
        this.radioButton.setChecked(z, false);
    }

    public void setTextAndValue(CharSequence charSequence, boolean z) {
        this.textView.setText(charSequence);
        this.text2View.setVisibility(8);
        this.radioButton.setChecked(z, false);
    }
}
