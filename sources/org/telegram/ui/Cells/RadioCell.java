package org.telegram.ui.Cells;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioCell extends FrameLayout {
    private boolean needDivider;
    private RadioButton radioButton;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public RadioCell(Context context) {
        this(context, false, 21);
    }

    public RadioCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, false, 21, resourcesProvider);
    }

    public RadioCell(Context context, boolean z, int i) {
        this(context, z, i, null);
    }

    public RadioCell(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        int color;
        int i2;
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        float f = i;
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, f, 0.0f, f, 0.0f));
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        RadioButton radioButton2 = this.radioButton;
        if (z) {
            color = Theme.getColor(Theme.key_dialogRadioBackground, resourcesProvider);
            i2 = Theme.key_dialogRadioBackgroundChecked;
        } else {
            color = Theme.getColor(Theme.key_radioBackground, resourcesProvider);
            i2 = Theme.key_radioBackgroundChecked;
        }
        radioButton2.setColor(color, Theme.getColor(i2, resourcesProvider));
        RadioButton radioButton3 = this.radioButton;
        boolean z2 = LocaleController.isRTL;
        addView(radioButton3, LayoutHelper.createFrame(22, 22.0f, (z2 ? 3 : 5) | 48, z2 ? i + 1 : 0, 14.0f, z2 ? 0 : i + 1, 0.0f));
    }

    public void hideRadioButton() {
        this.radioButton.setVisibility(8);
    }

    public boolean isChecked() {
        return this.radioButton.isChecked();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.RadioButton");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(isChecked());
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setEnabled(boolean z, ArrayList arrayList) {
        super.setEnabled(z);
        if (arrayList == null) {
            this.textView.setAlpha(z ? 1.0f : 0.5f);
            this.radioButton.setAlpha(z ? 1.0f : 0.5f);
        } else {
            TextView textView = this.textView;
            Property property = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property, z ? 1.0f : 0.5f));
            arrayList.add(ObjectAnimator.ofFloat(this.radioButton, (Property<RadioButton, Float>) property, z ? 1.0f : 0.5f));
        }
    }

    public void setRadioIcon(Drawable drawable) {
        this.radioButton.setIcon(drawable);
    }

    public void setText(CharSequence charSequence, boolean z, boolean z2) {
        this.textView.setText(charSequence);
        this.radioButton.setChecked(z, false);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}
