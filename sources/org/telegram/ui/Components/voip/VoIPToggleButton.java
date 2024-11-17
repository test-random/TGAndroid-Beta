package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class VoIPToggleButton extends FrameLayout {
    private boolean animateBackground;
    int animateToBackgroundColor;
    public int animationDelay;
    private int backgroundCheck1;
    private int backgroundCheck2;
    int backgroundColor;
    private Paint bitmapPaint;
    private ValueAnimator checkAnimator;
    private boolean checkable;
    private boolean checkableForAccessibility;
    private boolean checked;
    private float checkedProgress;
    Paint circlePaint;
    private float crossOffset;
    private Paint crossPaint;
    private float crossProgress;
    int currentBackgroundColor;
    int currentIconColor;
    int currentIconRes;
    String currentText;
    private boolean drawBackground;
    private boolean drawCross;
    Drawable[] icon;
    private boolean iconChangeColor;
    private float pressedScale;
    private ValueAnimator pressedScaleAnimator;
    private float radius;
    ValueAnimator replaceAnimator;
    private int replaceColorFrom;
    float replaceProgress;
    Drawable rippleDrawable;
    FrameLayout textLayoutContainer;
    TextView[] textView;
    private Paint xRefPaint;

    public VoIPToggleButton(Context context) {
        this(context, 52.0f);
    }

    public VoIPToggleButton(Context context, float f) {
        super(context);
        this.circlePaint = new Paint(1);
        this.drawBackground = true;
        this.icon = new Drawable[2];
        this.textView = new TextView[2];
        this.crossPaint = new Paint(1);
        this.xRefPaint = new Paint(1);
        this.bitmapPaint = new Paint(1);
        this.pressedScale = 1.0f;
        this.radius = f;
        setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context);
        this.textLayoutContainer = frameLayout;
        addView(frameLayout);
        for (int i = 0; i < 2; i++) {
            TextView textView = new TextView(context);
            textView.setGravity(1);
            textView.setTextSize(1, 11.0f);
            textView.setTextColor(-1);
            textView.setImportantForAccessibility(2);
            this.textLayoutContainer.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, f + 6.0f, 0.0f, 0.0f));
            this.textView[i] = textView;
        }
        this.textView[1].setVisibility(8);
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.xRefPaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.crossPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.crossPaint.setStrokeCap(Paint.Cap.ROUND);
        this.bitmapPaint.setFilterBitmap(true);
    }

    public void lambda$setChecked$2(ValueAnimator valueAnimator) {
        this.checkedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setBackgroundColor(this.backgroundCheck1, this.backgroundCheck2);
    }

    public void lambda$setData$1(boolean z, ValueAnimator valueAnimator) {
        this.replaceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (z) {
            this.textView[0].setAlpha(1.0f - this.replaceProgress);
            this.textView[0].setScaleX(1.0f - this.replaceProgress);
            this.textView[0].setScaleY(1.0f - this.replaceProgress);
            this.textView[1].setAlpha(this.replaceProgress);
            this.textView[1].setScaleX(this.replaceProgress);
            this.textView[1].setScaleY(this.replaceProgress);
        }
    }

    public void lambda$setPressedBtn$0(ValueAnimator valueAnimator) {
        this.pressedScale = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setState(getDrawableState());
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPToggleButton.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.currentText);
        if (!this.checkable && !this.checkableForAccessibility) {
            accessibilityNodeInfo.setClassName(Button.class.getName());
            return;
        }
        accessibilityNodeInfo.setClassName(ToggleButton.class.getName());
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.checked);
    }

    public void setBackgroundColor(int i, int i2) {
        this.backgroundCheck1 = i;
        this.backgroundCheck2 = i2;
        this.backgroundColor = ColorUtils.blendARGB(i, i2, this.checkedProgress);
        invalidate();
    }

    public void setCheckable(boolean z) {
        this.checkable = z;
    }

    public void setCheckableForAccessibility(boolean z) {
        this.checkableForAccessibility = z;
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checked == z) {
            return;
        }
        this.checked = z;
        if (this.checkable) {
            if (!z2) {
                this.checkedProgress = z ? 1.0f : 0.0f;
                setBackgroundColor(this.backgroundCheck1, this.backgroundCheck2);
                return;
            }
            ValueAnimator valueAnimator = this.checkAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.checkAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.checkedProgress, this.checked ? 1.0f : 0.0f);
            this.checkAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    VoIPToggleButton.this.lambda$setChecked$2(valueAnimator2);
                }
            });
            this.checkAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    VoIPToggleButton voIPToggleButton = VoIPToggleButton.this;
                    voIPToggleButton.checkedProgress = voIPToggleButton.checked ? 1.0f : 0.0f;
                    VoIPToggleButton voIPToggleButton2 = VoIPToggleButton.this;
                    voIPToggleButton2.setBackgroundColor(voIPToggleButton2.backgroundCheck1, VoIPToggleButton.this.backgroundCheck2);
                }
            });
            this.checkAnimator.setDuration(150L);
            this.checkAnimator.start();
        }
    }

    public void setCrossOffset(float f) {
        this.crossOffset = f;
    }

    public void setData(int i, int i2, int i3, float f, boolean z, String str, boolean z2, boolean z3) {
        int dp;
        int i4;
        int i5;
        String str2;
        if (getVisibility() != 0) {
            setVisibility(0);
            z3 = false;
        }
        if (this.currentIconRes == i && this.currentIconColor == i2 && ((this.checkable || this.currentBackgroundColor == i3) && (str2 = this.currentText) != null && str2.equals(str) && z2 == this.drawCross)) {
            return;
        }
        if (this.rippleDrawable == null || z) {
            if (Color.alpha(i3) != 255 || AndroidUtilities.computePerceivedBrightness(i3) <= 0.5d) {
                dp = AndroidUtilities.dp(this.radius);
                i4 = (int) (f * 76.5f);
                i5 = -1;
            } else {
                dp = AndroidUtilities.dp(this.radius);
                i4 = (int) (f * 25.5f);
                i5 = -16777216;
            }
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(dp, 0, ColorUtils.setAlphaComponent(i5, i4));
            this.rippleDrawable = createSimpleSelectorCircleDrawable;
            createSimpleSelectorCircleDrawable.setCallback(this);
        }
        ValueAnimator valueAnimator = this.replaceAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.animateBackground = this.currentBackgroundColor != i3;
        boolean z4 = this.currentIconRes == i;
        this.iconChangeColor = z4;
        if (z4) {
            this.replaceColorFrom = this.currentIconColor;
        }
        this.currentIconRes = i;
        this.currentIconColor = i2;
        this.currentBackgroundColor = i3;
        this.currentText = str;
        this.drawCross = z2;
        if (z3) {
            if (!z4 && i != 0) {
                this.icon[1] = ContextCompat.getDrawable(getContext(), i).mutate();
                this.icon[1].setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
            }
            if (!this.checkable) {
                this.animateToBackgroundColor = i3;
            }
            final boolean z5 = !this.textView[0].getText().toString().equals(str);
            TextView[] textViewArr = this.textView;
            if (z5) {
                textViewArr[1].setText(str);
                this.textView[1].setVisibility(0);
                this.textView[1].setAlpha(0.0f);
                this.textView[1].setScaleX(0.0f);
                this.textView[1].setScaleY(0.0f);
            } else {
                textViewArr[0].setText(str);
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.replaceAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    VoIPToggleButton.this.lambda$setData$1(z5, valueAnimator2);
                }
            });
            this.replaceAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    Drawable[] drawableArr;
                    Drawable drawable;
                    VoIPToggleButton voIPToggleButton = VoIPToggleButton.this;
                    voIPToggleButton.replaceAnimator = null;
                    if (z5) {
                        TextView[] textViewArr2 = voIPToggleButton.textView;
                        TextView textView = textViewArr2[0];
                        textViewArr2[0] = textViewArr2[1];
                        textViewArr2[1] = textView;
                        textView.setVisibility(8);
                    }
                    if (!VoIPToggleButton.this.iconChangeColor && (drawable = (drawableArr = VoIPToggleButton.this.icon)[1]) != null) {
                        drawableArr[0] = drawable;
                        drawableArr[1] = null;
                    }
                    VoIPToggleButton.this.iconChangeColor = false;
                    if (!VoIPToggleButton.this.checkable) {
                        VoIPToggleButton voIPToggleButton2 = VoIPToggleButton.this;
                        voIPToggleButton2.backgroundColor = voIPToggleButton2.animateToBackgroundColor;
                    }
                    VoIPToggleButton voIPToggleButton3 = VoIPToggleButton.this;
                    voIPToggleButton3.replaceProgress = 0.0f;
                    voIPToggleButton3.invalidate();
                }
            });
            this.replaceAnimator.setDuration(150L).start();
        } else {
            if (i != 0) {
                this.icon[0] = ContextCompat.getDrawable(getContext(), i).mutate();
                this.icon[0].setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
            }
            this.crossPaint.setColor(i2);
            if (!this.checkable) {
                this.backgroundColor = i3;
            }
            this.textView[0].setText(str);
            this.crossProgress = this.drawCross ? 1.0f : 0.0f;
            this.iconChangeColor = false;
            this.replaceProgress = 0.0f;
        }
        invalidate();
    }

    public void setData(int i, int i2, int i3, String str, boolean z, boolean z2) {
        setData(i, i2, i3, 1.0f, true, str, z, z2);
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setEnabled(boolean z, boolean z2) {
        super.setEnabled(z);
        if (z2) {
            animate().alpha(z ? 1.0f : 0.5f).setDuration(180L).start();
        } else {
            clearAnimation();
            setAlpha(z ? 1.0f : 0.5f);
        }
    }

    public void setPressedBtn(boolean z) {
        ValueAnimator valueAnimator = this.pressedScaleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.pressedScale, z ? 0.8f : 1.0f);
        this.pressedScaleAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                VoIPToggleButton.this.lambda$setPressedBtn$0(valueAnimator2);
            }
        });
        this.pressedScaleAnimator.setDuration(150L);
        this.pressedScaleAnimator.start();
    }

    public void setTextSize(int i) {
        for (int i2 = 0; i2 < 2; i2++) {
            this.textView[i2].setTextSize(1, i);
        }
    }

    public void shakeView() {
        AndroidUtilities.shakeView(this.textView[0]);
        AndroidUtilities.shakeView(this.textView[1]);
    }

    public void showText(boolean z, boolean z2) {
        if (!z2) {
            this.textLayoutContainer.animate().cancel();
            this.textLayoutContainer.setAlpha(z ? 1.0f : 0.0f);
        } else {
            float f = z ? 1.0f : 0.0f;
            if (this.textLayoutContainer.getAlpha() != f) {
                this.textLayoutContainer.animate().alpha(f).start();
            }
        }
    }

    @Override
    public boolean verifyDrawable(Drawable drawable) {
        return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
    }
}
