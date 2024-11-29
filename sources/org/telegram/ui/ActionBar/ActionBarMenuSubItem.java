package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class ActionBarMenuSubItem extends FrameLayout {
    boolean bottom;
    public CheckBox2 checkView;
    public boolean checkViewLeft;
    private boolean enabled;
    private ValueAnimator enabledAnimator;
    boolean expandIfMultiline;
    private int iconColor;
    private int iconResId;
    public RLottieImageView imageView;
    private int itemHeight;
    public Runnable openSwipeBackLayout;
    private final Theme.ResourcesProvider resourcesProvider;
    private ImageView rightIcon;
    private int selectorColor;
    int selectorRad;
    public TextView subtextView;
    private int textColor;
    public TextView textView;
    boolean top;

    public ActionBarMenuSubItem(android.content.Context r6, int r7, boolean r8, boolean r9, org.telegram.ui.ActionBar.Theme.ResourcesProvider r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarMenuSubItem.<init>(android.content.Context, int, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public ActionBarMenuSubItem(Context context, boolean z, boolean z2) {
        this(context, false, z, z2);
    }

    public ActionBarMenuSubItem(Context context, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        this(context, 0, z, z2, resourcesProvider);
    }

    public ActionBarMenuSubItem(Context context, boolean z, boolean z2, boolean z3) {
        this(context, z ? 1 : 0, z2, z3, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuSubItem(Context context, boolean z, boolean z2, boolean z3, Theme.ResourcesProvider resourcesProvider) {
        this(context, z ? 1 : 0, z2, z3, resourcesProvider);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$setEnabledByColor$0(int i, int i2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setTextColor(ColorUtils.blendARGB(i, i2, floatValue));
        setIconColor(ColorUtils.blendARGB(i, i2, floatValue));
    }

    public CheckBox2 getCheckView() {
        return this.checkView;
    }

    public int getIconResId() {
        return this.iconResId;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public ImageView getRightIcon() {
        return this.rightIcon;
    }

    public TextView getTextView() {
        return this.textView;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(isEnabled());
        CheckBox2 checkBox2 = this.checkView;
        if (checkBox2 == null || !checkBox2.isChecked()) {
            return;
        }
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.checkView.isChecked());
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
    }

    public void onItemShown() {
        if (this.imageView.getAnimatedDrawable() != null) {
            this.imageView.getAnimatedDrawable().start();
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.itemHeight), 1073741824));
        if (!this.expandIfMultiline || this.textView.getLayout().getLineCount() <= 1) {
            return;
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.itemHeight + 8), 1073741824));
    }

    public void openSwipeBack() {
        Runnable runnable = this.openSwipeBackLayout;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setAnimatedIcon(int i) {
        this.iconResId = 0;
        this.imageView.setAnimation(i, 24, 24);
    }

    public void setCheckColor(int i) {
        this.checkView.setColor(-1, -1, i);
    }

    public void setChecked(boolean z) {
        CheckBox2 checkBox2 = this.checkView;
        if (checkBox2 == null) {
            return;
        }
        checkBox2.setChecked(z, true);
    }

    public ActionBarMenuSubItem setColors(int i, int i2) {
        setTextColor(i);
        setIconColor(i2);
        return this;
    }

    public void setEnabledByColor(final boolean z, final int i, final int i2) {
        ValueAnimator valueAnimator = this.enabledAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.enabled ? 1.0f : 0.0f, z ? 1.0f : 0.0f);
        this.enabledAnimator = ofFloat;
        this.enabled = z;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ActionBarMenuSubItem.this.lambda$setEnabledByColor$0(i, i2, valueAnimator2);
            }
        });
        this.enabledAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                float f = z ? 1.0f : 0.0f;
                ActionBarMenuSubItem.this.setTextColor(ColorUtils.blendARGB(i, i2, f));
                ActionBarMenuSubItem.this.setIconColor(ColorUtils.blendARGB(i, i2, f));
            }
        });
        this.enabledAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enabledAnimator.start();
    }

    public void setIcon(int i) {
        RLottieImageView rLottieImageView = this.imageView;
        this.iconResId = i;
        rLottieImageView.setImageResource(i);
    }

    public void setIcon(Drawable drawable) {
        this.iconResId = 0;
        this.imageView.setImageDrawable(drawable);
    }

    public void setIconColor(int i) {
        if (this.iconColor != i) {
            RLottieImageView rLottieImageView = this.imageView;
            this.iconColor = i;
            rLottieImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setItemHeight(int i) {
        this.itemHeight = i;
    }

    public void setMultiline(boolean z) {
        this.textView.setLines(2);
        if (z) {
            this.textView.setTextSize(1, 14.0f);
        } else {
            this.expandIfMultiline = true;
        }
        this.textView.setSingleLine(false);
        this.textView.setGravity(16);
    }

    public void setRightIcon(int i) {
        if (this.rightIcon == null) {
            ImageView imageView = new ImageView(getContext());
            this.rightIcon = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.rightIcon.setColorFilter(this.iconColor, PorterDuff.Mode.MULTIPLY);
            if (LocaleController.isRTL) {
                this.rightIcon.setScaleX(-1.0f);
            }
            addView(this.rightIcon, LayoutHelper.createFrame(24, -1, (LocaleController.isRTL ? 3 : 5) | 16));
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.leftMargin = this.rightIcon != null ? AndroidUtilities.dp(32.0f) : 0;
        } else {
            layoutParams.rightMargin = this.rightIcon != null ? AndroidUtilities.dp(32.0f) : 0;
        }
        this.textView.setLayoutParams(layoutParams);
        setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 18.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 18.0f : 8.0f), 0);
        if (i == 0) {
            this.rightIcon.setVisibility(8);
        } else {
            this.rightIcon.setVisibility(0);
            this.rightIcon.setImageResource(i);
        }
    }

    public void setSelectorColor(int i) {
        if (this.selectorColor != i) {
            this.selectorColor = i;
            updateBackground();
        }
    }

    public void setSubtext(CharSequence charSequence) {
        if (this.subtextView == null) {
            TextView textView = new TextView(getContext());
            this.subtextView = textView;
            textView.setLines(1);
            this.subtextView.setSingleLine(true);
            this.subtextView.setGravity(3);
            this.subtextView.setEllipsize(TextUtils.TruncateAt.END);
            this.subtextView.setTextColor(getThemedColor(Theme.key_groupcreate_sectionText));
            this.subtextView.setVisibility(8);
            this.subtextView.setTextSize(1, 13.0f);
            this.subtextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
            addView(this.subtextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, 0.0f, 10.0f, 0.0f, 0.0f));
        }
        boolean z = !TextUtils.isEmpty(charSequence);
        if (z != (this.subtextView.getVisibility() == 0)) {
            this.subtextView.setVisibility(z ? 0 : 8);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
            layoutParams.bottomMargin = z ? AndroidUtilities.dp(10.0f) : 0;
            this.textView.setLayoutParams(layoutParams);
        }
        this.subtextView.setText(charSequence);
    }

    public void setSubtextColor(int i) {
        TextView textView = this.subtextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public void setTextAndIcon(CharSequence charSequence, int i) {
        setTextAndIcon(charSequence, i, null);
    }

    public void setTextAndIcon(CharSequence charSequence, int i, Drawable drawable) {
        int dp;
        int dp2;
        this.textView.setText(charSequence);
        if (i == 0 && drawable == null && this.checkView == null) {
            this.iconResId = 0;
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
            return;
        }
        if (drawable != null) {
            this.iconResId = 0;
            this.imageView.setImageDrawable(drawable);
        } else {
            this.iconResId = i;
            this.imageView.setImageResource(i);
        }
        this.imageView.setVisibility(0);
        TextView textView = this.textView;
        if (this.checkViewLeft) {
            dp = this.checkView != null ? AndroidUtilities.dp(43.0f) : 0;
        } else {
            dp = AndroidUtilities.dp((i == 0 && drawable == null) ? 0.0f : 43.0f);
        }
        if (this.checkViewLeft) {
            dp2 = AndroidUtilities.dp((i == 0 && drawable == null) ? 0.0f : 43.0f);
        } else {
            dp2 = this.checkView != null ? AndroidUtilities.dp(43.0f) : 0;
        }
        textView.setPadding(dp, 0, dp2, 0);
    }

    public void setTextColor(int i) {
        if (this.textColor != i) {
            TextView textView = this.textView;
            this.textColor = i;
            textView.setTextColor(i);
        }
    }

    public void updateBackground() {
        setBackground(Theme.createRadSelectorDrawable(this.selectorColor, this.top ? this.selectorRad : 0, this.bottom ? this.selectorRad : 0));
    }

    public void updateSelectorBackground(boolean z, boolean z2) {
        if (this.top == z && this.bottom == z2) {
            return;
        }
        this.top = z;
        this.bottom = z2;
        updateBackground();
    }

    public void updateSelectorBackground(boolean z, boolean z2, int i) {
        if (this.top == z && this.bottom == z2 && this.selectorRad == i) {
            return;
        }
        this.top = z;
        this.bottom = z2;
        this.selectorRad = i;
        updateBackground();
    }
}
