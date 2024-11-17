package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.GenericProvider;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxBase;

public class CheckBox2 extends View {
    private CheckBoxBase checkBoxBase;
    int currentIcon;
    Drawable iconDrawable;

    public CheckBox2(Context context, int i) {
        this(context, i, null);
    }

    public CheckBox2(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.checkBoxBase = new CheckBoxBase(this, i, resourcesProvider);
    }

    public CheckBoxBase getCheckBoxBase() {
        return this.checkBoxBase;
    }

    public float getProgress() {
        return this.checkBoxBase.getProgress();
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.checkBoxBase.isChecked();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkBoxBase.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.checkBoxBase.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.iconDrawable == null) {
            this.checkBoxBase.draw(canvas);
            return;
        }
        int measuredWidth = getMeasuredWidth() >> 1;
        int measuredHeight = getMeasuredHeight() >> 1;
        Drawable drawable = this.iconDrawable;
        drawable.setBounds(measuredWidth - (drawable.getIntrinsicWidth() / 2), measuredHeight - (this.iconDrawable.getIntrinsicHeight() / 2), (this.iconDrawable.getIntrinsicWidth() / 2) + measuredWidth, (this.iconDrawable.getIntrinsicHeight() / 2) + measuredHeight);
        this.iconDrawable.draw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(AndroidUtilities.dp(1.2f));
        paint.setColor(Theme.getColor(Theme.key_switch2Track));
        canvas.drawCircle(measuredWidth, measuredHeight, measuredWidth - AndroidUtilities.dp(1.5f), paint);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(isChecked());
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.checkBoxBase.setBounds(0, 0, i3 - i, i4 - i2);
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.checkBoxBase.setChecked(i, z, z2);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBoxBase.setChecked(z, z2);
    }

    public void setCirclePaintProvider(GenericProvider<Void, Paint> genericProvider) {
        this.checkBoxBase.setCirclePaintProvider(genericProvider);
    }

    public void setColor(int i, int i2, int i3) {
        this.checkBoxBase.setColor(i, i2, i3);
    }

    public void setDrawBackgroundAsArc(int i) {
        this.checkBoxBase.setBackgroundType(i);
    }

    public void setDrawUnchecked(boolean z) {
        this.checkBoxBase.setDrawUnchecked(z);
    }

    public void setDuration(long j) {
        this.checkBoxBase.animationDuration = j;
    }

    @Override
    public void setEnabled(boolean z) {
        this.checkBoxBase.setEnabled(z);
        super.setEnabled(z);
    }

    public void setForbidden(boolean z) {
        this.checkBoxBase.setForbidden(z);
    }

    public void setIcon(int i) {
        if (i != this.currentIcon) {
            this.currentIcon = i;
            if (i == 0) {
                this.iconDrawable = null;
                return;
            }
            Drawable mutate = ContextCompat.getDrawable(getContext(), i).mutate();
            this.iconDrawable = mutate;
            mutate.setColorFilter(Theme.getColor(Theme.key_switch2Track), PorterDuff.Mode.MULTIPLY);
        }
    }

    public void setNum(int i) {
        this.checkBoxBase.setNum(i);
    }

    public void setProgressDelegate(CheckBoxBase.ProgressDelegate progressDelegate) {
        this.checkBoxBase.setProgressDelegate(progressDelegate);
    }
}
