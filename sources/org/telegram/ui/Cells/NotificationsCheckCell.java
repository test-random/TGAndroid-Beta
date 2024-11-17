package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class NotificationsCheckCell extends FrameLayout {
    private boolean animationsEnabled;
    private Switch checkBox;
    private int currentHeight;
    private boolean drawLine;
    private ImageView imageView;
    private boolean isMultiline;
    private TextView multilineValueTextView;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private AnimatedTextView valueTextView;

    public NotificationsCheckCell(Context context) {
        this(context, 21, 70, false, null);
    }

    public NotificationsCheckCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, null);
    }

    public NotificationsCheckCell(Context context, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        this.drawLine = true;
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        this.currentHeight = i2;
        if (z) {
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setFocusable(false);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView2 = this.textView;
        boolean z2 = LocaleController.isRTL;
        int i3 = (z2 ? 5 : 3) | 48;
        if (z2) {
            f = 80.0f;
        } else {
            f = z ? 64 : i;
        }
        float f7 = ((this.currentHeight - 70) / 2) + 13;
        if (z2) {
            f2 = z ? 64 : i;
        } else {
            f2 = 80.0f;
        }
        addView(textView2, LayoutHelper.createFrame(-1, -2.0f, i3, f, f7, f2, 0.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context);
        this.valueTextView = animatedTextView;
        animatedTextView.setAnimationProperties(0.55f, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        AnimatedTextView animatedTextView2 = this.valueTextView;
        int i4 = Theme.key_windowBackgroundWhiteGrayText2;
        animatedTextView2.setTextColor(Theme.getColor(i4, resourcesProvider));
        this.valueTextView.setTextSize(AndroidUtilities.dp(13.0f));
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsizeByGradient(true);
        AnimatedTextView animatedTextView3 = this.valueTextView;
        boolean z3 = LocaleController.isRTL;
        int i5 = (z3 ? 5 : 3) | 48;
        if (z3) {
            f3 = 80.0f;
        } else {
            f3 = z ? 64 : i;
        }
        float f8 = (29 - (z ? 2 : 0)) + ((this.currentHeight - 70) / 2);
        if (z3) {
            f4 = z ? 64 : i;
        } else {
            f4 = 80.0f;
        }
        addView(animatedTextView3, LayoutHelper.createFrame(-1, -2.0f, i5, f3, f8, f4, 0.0f));
        TextView textView3 = new TextView(context);
        this.multilineValueTextView = textView3;
        textView3.setTextColor(Theme.getColor(i4, resourcesProvider));
        this.multilineValueTextView.setTextSize(1, 13.0f);
        this.multilineValueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.multilineValueTextView.setLines(0);
        this.multilineValueTextView.setMaxLines(0);
        this.multilineValueTextView.setSingleLine(false);
        this.multilineValueTextView.setEllipsize(null);
        this.multilineValueTextView.setPadding(0, 0, 0, 0);
        this.multilineValueTextView.setVisibility(8);
        TextView textView4 = this.multilineValueTextView;
        boolean z4 = LocaleController.isRTL;
        int i6 = (z4 ? 5 : 3) | 48;
        if (z4) {
            f5 = 80.0f;
        } else {
            f5 = z ? 64 : i;
        }
        float f9 = (38 - (z ? 2 : 0)) + ((this.currentHeight - 70) / 2);
        if (z4) {
            f6 = z ? 64 : i;
        } else {
            f6 = 80.0f;
        }
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, i6, f5, f9, f6, 0.0f));
        Switch r3 = new Switch(context, resourcesProvider) {
            @Override
            protected int processColor(int i7) {
                return NotificationsCheckCell.this.processColor(i7);
            }
        };
        this.checkBox = r3;
        int i7 = Theme.key_switchTrack;
        int i8 = Theme.key_switchTrackChecked;
        int i9 = Theme.key_windowBackgroundWhite;
        r3.setColors(i7, i8, i9, i9);
        addView(this.checkBox, LayoutHelper.createFrame(37, 40.0f, (LocaleController.isRTL ? 3 : 5) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        this.checkBox.setFocusable(false);
    }

    public Switch getCheckBox() {
        return this.checkBox;
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float dp;
        int i;
        if (this.needDivider) {
            if (LocaleController.isRTL) {
                dp = 0.0f;
            } else {
                dp = AndroidUtilities.dp(this.imageView != null ? 64.0f : 20.0f);
            }
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                i = AndroidUtilities.dp(this.imageView != null ? 64.0f : 20.0f);
            } else {
                i = 0;
            }
            canvas.drawLine(dp, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, Theme.dividerPaint);
        }
        if (this.drawLine) {
            int dp2 = LocaleController.isRTL ? AndroidUtilities.dp(76.0f) : (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - 1;
            canvas.drawRect(dp2, (getMeasuredHeight() - AndroidUtilities.dp(22.0f)) / 2, dp2 + 2, r1 + AndroidUtilities.dp(22.0f), Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        CharSequence text;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        StringBuilder sb = new StringBuilder();
        sb.append(this.textView.getText());
        if (this.isMultiline) {
            TextView textView = this.multilineValueTextView;
            if (textView != null && !TextUtils.isEmpty(textView.getText())) {
                sb.append("\n");
                text = this.multilineValueTextView.getText();
                sb.append(text);
            }
        } else {
            AnimatedTextView animatedTextView = this.valueTextView;
            if (animatedTextView != null && !TextUtils.isEmpty(animatedTextView.getText())) {
                sb.append("\n");
                text = this.valueTextView.getText();
                sb.append(text);
            }
        }
        accessibilityNodeInfo.setContentDescription(sb);
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.checkBox.isChecked());
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), this.isMultiline ? View.MeasureSpec.makeMeasureSpec(0, 0) : View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentHeight), 1073741824));
    }

    protected int processColor(int i) {
        return i;
    }

    public void setAnimationsEnabled(boolean z) {
        this.animationsEnabled = z;
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public void setChecked(boolean z, int i) {
        this.checkBox.setChecked(z, i, true);
    }

    public void setDrawLine(boolean z) {
        this.drawLine = z;
    }

    public void setMultiline(boolean z) {
        this.isMultiline = z;
        if (z) {
            this.multilineValueTextView.setVisibility(0);
            this.valueTextView.setVisibility(8);
            this.multilineValueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        } else {
            this.multilineValueTextView.setVisibility(8);
            this.valueTextView.setVisibility(0);
            this.valueTextView.setPadding(0, 0, 0, 0);
        }
    }

    public void setTextAndValueAndCheck(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, boolean z2, boolean z3) {
        setTextAndValueAndIconAndCheck(charSequence, charSequence2, 0, z, i, z2, z3);
    }

    public void setTextAndValueAndCheck(CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        setTextAndValueAndCheck(charSequence, charSequence2, z, 0, false, z2);
    }

    public void setTextAndValueAndIconAndCheck(CharSequence charSequence, CharSequence charSequence2, int i, boolean z, int i2, boolean z2, boolean z3) {
        setTextAndValueAndIconAndCheck(charSequence, charSequence2, i, z, i2, z2, z3, false);
    }

    public void setTextAndValueAndIconAndCheck(CharSequence charSequence, CharSequence charSequence2, int i, boolean z, int i2, boolean z2, boolean z3, boolean z4) {
        this.textView.setText(charSequence);
        ImageView imageView = this.imageView;
        if (imageView != null) {
            imageView.setImageResource(i);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
        }
        this.checkBox.setChecked(z, i2, this.animationsEnabled);
        setMultiline(z2);
        if (this.isMultiline) {
            this.multilineValueTextView.setText(charSequence2);
        } else {
            this.valueTextView.setText(charSequence2, z4);
        }
        (this.isMultiline ? this.multilineValueTextView : this.valueTextView).setVisibility(0);
        this.checkBox.setContentDescription(charSequence);
        this.needDivider = z3;
    }

    public void setValue(CharSequence charSequence) {
        if (this.isMultiline) {
            this.multilineValueTextView.setText(charSequence);
        } else {
            this.valueTextView.setText(charSequence, true);
        }
    }
}
