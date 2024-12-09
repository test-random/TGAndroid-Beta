package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitPreviewView;
import org.telegram.ui.Components.Premium.PremiumGradient;

public class LimitPreviewView extends LinearLayout {
    private boolean animateIncrease;
    private int animateIncreaseWidth;
    private boolean animatingRotation;
    boolean animationCanPlay;
    private ValueAnimator arrowAnimator;
    private int currentValue;
    private DarkGradientProvider darkGradientProvider;
    TextView defaultCount;
    private final TextView defaultText;
    public int gradientTotalHeight;
    int gradientYOffset;
    int icon;
    boolean inc;
    public boolean invalidationEnabled;
    private boolean isBoostsStyle;
    private boolean isSimpleStyle;
    public boolean isStatistic;
    CounterView limitIcon;
    float limitIconRotation;
    FrameLayout limitsContainer;
    private View parentVideForGradient;
    private float percent;
    private float position;
    TextView premiumCount;
    private final int premiumLimit;
    private boolean premiumLocked;
    private final TextView premiumText;
    float progress;
    Theme.ResourcesProvider resourcesProvider;
    PremiumGradient.PremiumGradientTools staticGradient;
    boolean wasAnimation;
    boolean wasHaptic;
    int width1;

    public class CounterView extends View {
        ArrayList animatedLayouts;
        StaticLayout animatedStableLayout;
        boolean animationInProgress;
        float arrowCenter;
        Paint dstOutPaint;
        boolean invalidatePath;
        Paint overlayPaint;
        Path path;
        PathEffect pathEffect;
        CharSequence text;
        StaticLayout textLayout;
        TextPaint textPaint;
        float textWidth;

        public class AnimatedLayout {
            public boolean direction;
            float progress;
            public boolean replace;
            ArrayList staticLayouts;
            ValueAnimator valueAnimator;
            float x;

            private AnimatedLayout() {
                this.staticLayouts = new ArrayList();
            }
        }

        public CounterView(Context context) {
            super(context);
            this.path = new Path();
            this.pathEffect = new CornerPathEffect(AndroidUtilities.dp(6.0f));
            this.textPaint = new TextPaint(1);
            this.animatedLayouts = new ArrayList();
            this.dstOutPaint = new Paint();
            this.overlayPaint = new Paint();
            this.textPaint.setTypeface(AndroidUtilities.bold());
            this.textPaint.setTextSize(AndroidUtilities.dp(22.0f));
            this.textPaint.setColor(-1);
            this.dstOutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.overlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        }

        public void checkAnimationComplete() {
            for (int i = 0; i < this.animatedLayouts.size(); i++) {
                if (((AnimatedLayout) this.animatedLayouts.get(i)).valueAnimator != null) {
                    return;
                }
            }
            this.animatedLayouts.clear();
            this.animationInProgress = false;
            invalidate();
        }

        public void lambda$createAnimationLayouts$0(AnimatedLayout animatedLayout, ValueAnimator valueAnimator) {
            animatedLayout.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public void lambda$createAnimationLayoutsDiff$1(AnimatedLayout animatedLayout, ValueAnimator valueAnimator) {
            animatedLayout.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        private void updatePath() {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            float measuredWidth = getMeasuredWidth() * this.arrowCenter;
            float clamp = Utilities.clamp(AndroidUtilities.dp(8.0f) + measuredWidth, getMeasuredWidth(), 0.0f);
            float clamp2 = Utilities.clamp(AndroidUtilities.dp(10.0f) + measuredWidth, getMeasuredWidth(), AndroidUtilities.dp(24.0f));
            float clamp3 = Utilities.clamp(measuredWidth - AndroidUtilities.dp(this.arrowCenter >= 0.7f ? 24.0f : 10.0f), getMeasuredWidth(), 0.0f);
            float clamp4 = Utilities.clamp(measuredWidth - AndroidUtilities.dp(8.0f), getMeasuredWidth(), 0.0f);
            this.path.rewind();
            float f = measuredHeight;
            float f2 = f - (f / 2.0f);
            this.path.moveTo(clamp3, f2 - AndroidUtilities.dp(2.0f));
            this.path.lineTo(clamp3, f);
            this.path.lineTo(clamp4, f);
            this.path.lineTo(measuredWidth, measuredHeight + AndroidUtilities.dp(8.0f));
            if (this.arrowCenter < 0.7f) {
                this.path.lineTo(clamp, f);
            }
            this.path.lineTo(clamp2, f);
            this.path.lineTo(clamp2, f2 - AndroidUtilities.dp(2.0f));
            this.path.close();
        }

        void createAnimationLayouts() {
            this.animatedLayouts.clear();
            if (LimitPreviewView.this.isBoostsStyle && LimitPreviewView.this.currentValue == 0) {
                return;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.text);
            int i = 0;
            boolean z = true;
            for (int i2 = 0; i2 < this.text.length(); i2++) {
                if (Character.isDigit(this.text.charAt(i2))) {
                    AnimatedLayout animatedLayout = new AnimatedLayout();
                    this.animatedLayouts.add(animatedLayout);
                    animatedLayout.x = this.textLayout.getSecondaryHorizontal(i2);
                    animatedLayout.direction = z;
                    if (i >= 1) {
                        z = !z;
                        i = 0;
                    }
                    i++;
                    int charAt = this.text.charAt(i2) - '0';
                    if (charAt == 0) {
                        charAt = 10;
                    }
                    int i3 = 1;
                    while (i3 <= charAt) {
                        animatedLayout.staticLayouts.add(new StaticLayout("" + (i3 == 10 ? 0 : i3), this.textPaint, (int) this.textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                        i3++;
                    }
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                }
            }
            this.animatedStableLayout = new StaticLayout(spannableStringBuilder, this.textPaint, AndroidUtilities.dp(12.0f) + ((int) this.textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            for (int i4 = 0; i4 < this.animatedLayouts.size(); i4++) {
                this.animationInProgress = true;
                final AnimatedLayout animatedLayout2 = (AnimatedLayout) this.animatedLayouts.get(i4);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                animatedLayout2.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LimitPreviewView.CounterView.this.lambda$createAnimationLayouts$0(animatedLayout2, valueAnimator);
                    }
                });
                animatedLayout2.valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animatedLayout2.valueAnimator = null;
                        CounterView.this.checkAnimationComplete();
                    }
                });
                animatedLayout2.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatedLayout2.valueAnimator.setDuration(750L);
                animatedLayout2.valueAnimator.setStartDelay(((this.animatedLayouts.size() - 1) - i4) * 60);
                animatedLayout2.valueAnimator.start();
            }
        }

        void createAnimationLayoutsDiff(CharSequence charSequence) {
            if (this.textLayout == null) {
                return;
            }
            this.animatedLayouts.clear();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.text);
            int length = this.text.length() - 1;
            int i = 0;
            while (length >= 0) {
                char charAt = length < charSequence.length() ? charSequence.charAt(length) : ' ';
                if (charAt != this.text.charAt(length) && Character.isDigit(this.text.charAt(length))) {
                    AnimatedLayout animatedLayout = new AnimatedLayout();
                    this.animatedLayouts.add(animatedLayout);
                    animatedLayout.x = this.textLayout.getSecondaryHorizontal(length);
                    animatedLayout.replace = true;
                    if (i >= 1) {
                        i = 0;
                    }
                    i++;
                    TextPaint textPaint = this.textPaint;
                    int i2 = (int) this.textWidth;
                    Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
                    animatedLayout.staticLayouts.add(new StaticLayout("" + charAt, textPaint, i2, alignment, 1.0f, 0.0f, false));
                    animatedLayout.staticLayouts.add(new StaticLayout("" + this.text.charAt(length), this.textPaint, (int) this.textWidth, alignment, 1.0f, 0.0f, false));
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), length, length + 1, 0);
                }
                length--;
            }
            this.animatedStableLayout = new StaticLayout(spannableStringBuilder, this.textPaint, AndroidUtilities.dp(12.0f) + ((int) this.textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            for (int i3 = 0; i3 < this.animatedLayouts.size(); i3++) {
                this.animationInProgress = true;
                final AnimatedLayout animatedLayout2 = (AnimatedLayout) this.animatedLayouts.get(i3);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                animatedLayout2.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LimitPreviewView.CounterView.this.lambda$createAnimationLayoutsDiff$1(animatedLayout2, valueAnimator);
                    }
                });
                animatedLayout2.valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animatedLayout2.valueAnimator = null;
                        CounterView.this.checkAnimationComplete();
                    }
                });
                animatedLayout2.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatedLayout2.valueAnimator.setDuration(250L);
                animatedLayout2.valueAnimator.setStartDelay(((this.animatedLayouts.size() - 1) - i3) * 60);
                animatedLayout2.valueAnimator.start();
            }
        }

        public float getArrowCenter() {
            return this.arrowCenter;
        }

        @Override
        protected void onDraw(android.graphics.Canvas r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.LimitPreviewView.CounterView.onDraw(android.graphics.Canvas):void");
        }

        @Override
        protected void onMeasure(int i, int i2) {
            TextPaint textPaint = this.textPaint;
            CharSequence charSequence = this.text;
            this.textWidth = textPaint.measureText(charSequence, 0, charSequence.length());
            this.textLayout = new StaticLayout(this.text, this.textPaint, ((int) this.textWidth) + AndroidUtilities.dp(12.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            setMeasuredDimension((int) (this.textWidth + getPaddingRight() + getPaddingLeft()), AndroidUtilities.dp(44.0f) + AndroidUtilities.dp(8.0f));
            updatePath();
        }

        public void setArrowCenter(float f) {
            if (this.arrowCenter != f) {
                this.arrowCenter = f;
                this.invalidatePath = true;
                invalidate();
            }
        }

        public void setText(CharSequence charSequence, boolean z) {
            if (!z) {
                this.text = charSequence;
                return;
            }
            CharSequence charSequence2 = this.text;
            this.text = charSequence;
            createAnimationLayoutsDiff(charSequence2);
        }

        @Override
        public void setTranslationX(float f) {
            if (f != getTranslationX()) {
                super.setTranslationX(f);
                invalidate();
            }
        }
    }

    public interface DarkGradientProvider {
        Paint setDarkGradientLocation(float f, float f2);
    }

    private class TextViewHolder extends FrameLayout {
        private final boolean isLeft;
        private final Paint paint;

        public TextViewHolder(Context context, boolean z) {
            super(context);
            Paint paint = new Paint();
            this.paint = paint;
            setLayerType(2, null);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            this.isLeft = z;
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (!(view instanceof TextView)) {
                return super.drawChild(canvas, view, j);
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            boolean z = false;
            boolean z2 = LimitPreviewView.this.percent != 0.0f && LimitPreviewView.this.percent <= 1.0f && this.isLeft;
            if (LimitPreviewView.this.percent == 1.0f && !this.isLeft) {
                z = true;
            }
            if ((z2 || z) && LimitPreviewView.this.hasDarkGradientProvider()) {
                canvas.saveLayer(view.getLeft(), view.getTop(), view.getRight(), view.getBottom(), this.paint, 31);
                canvas.drawRect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom(), LimitPreviewView.this.darkGradientProvider.setDarkGradientLocation(((ViewGroup) getParent()).getX() + getX(), ((ViewGroup) getParent()).getY() + getY()));
                canvas.restore();
                invalidate();
            }
            return drawChild;
        }
    }

    public LimitPreviewView(Context context, int i, int i2, int i3, float f, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        float f2;
        float f3;
        TextView textView;
        int i4;
        int i5;
        float f4;
        float f5;
        TextView textView2;
        int i6;
        int i7;
        this.animationCanPlay = true;
        this.invalidationEnabled = true;
        this.resourcesProvider = resourcesProvider;
        this.percent = MathUtils.clamp(f, 0.1f, 0.9f);
        this.icon = i;
        this.currentValue = i2;
        this.premiumLimit = i3;
        setOrientation(1);
        setClipChildren(false);
        setClipToPadding(false);
        if (i != 0) {
            setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
            this.limitIcon = new CounterView(context);
            setIconValue(i2, false);
            this.limitIcon.setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(14.0f));
            addView(this.limitIcon, LayoutHelper.createLinear(-2, -2, 0.0f, 3));
        }
        final TextViewHolder textViewHolder = new TextViewHolder(context, true);
        TextView textView3 = new TextView(context);
        this.defaultText = textView3;
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setText(LocaleController.getString(R.string.LimitFree));
        textView3.setGravity(16);
        int i8 = Theme.key_windowBackgroundWhiteBlackText;
        textView3.setTextColor(Theme.getColor(i8, resourcesProvider));
        TextView textView4 = new TextView(context);
        this.defaultCount = textView4;
        textView4.setTypeface(AndroidUtilities.bold());
        this.defaultCount.setText(String.format("%d", Integer.valueOf(i3)));
        this.defaultCount.setGravity(16);
        this.defaultCount.setTextColor(Theme.getColor(i8, resourcesProvider));
        if (LocaleController.isRTL) {
            f2 = 12.0f;
            f3 = 0.0f;
            textViewHolder.addView(textView3, LayoutHelper.createFrame(-1, 30.0f, 5, 12.0f, 0.0f, 12.0f, 0.0f));
            textView = this.defaultCount;
            i4 = -2;
            i5 = 3;
        } else {
            f2 = 12.0f;
            f3 = 0.0f;
            textViewHolder.addView(textView3, LayoutHelper.createFrame(-1, 30.0f, 3, 12.0f, 0.0f, 12.0f, 0.0f));
            textView = this.defaultCount;
            i4 = -2;
            i5 = 5;
        }
        textViewHolder.addView(textView, LayoutHelper.createFrame(i4, 30.0f, i5, f2, f3, 12.0f, 0.0f));
        final TextViewHolder textViewHolder2 = new TextViewHolder(context, false);
        TextView textView5 = new TextView(context);
        this.premiumText = textView5;
        textView5.setTypeface(AndroidUtilities.bold());
        textView5.setText(LocaleController.getString(R.string.LimitPremium));
        textView5.setGravity(16);
        textView5.setTextColor(-1);
        TextView textView6 = new TextView(context) {
            @Override
            public void setAlpha(float f6) {
                super.setAlpha(f6);
            }

            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(charSequence, bufferType);
            }

            @Override
            public void setTextColor(int i9) {
                super.setTextColor(i9);
            }
        };
        this.premiumCount = textView6;
        textView6.setTypeface(AndroidUtilities.bold());
        this.premiumCount.setText(String.format("%d", Integer.valueOf(i3)));
        this.premiumCount.setGravity(16);
        this.premiumCount.setTextColor(-1);
        if (LocaleController.isRTL) {
            f4 = 12.0f;
            f5 = 0.0f;
            textViewHolder2.addView(textView5, LayoutHelper.createFrame(-1, 30.0f, 5, 12.0f, 0.0f, 12.0f, 0.0f));
            textView2 = this.premiumCount;
            i6 = -2;
            i7 = 3;
        } else {
            f4 = 12.0f;
            f5 = 0.0f;
            textViewHolder2.addView(textView5, LayoutHelper.createFrame(-1, 30.0f, 3, 12.0f, 0.0f, 12.0f, 0.0f));
            textView2 = this.premiumCount;
            i6 = -2;
            i7 = 5;
        }
        textViewHolder2.addView(textView2, LayoutHelper.createFrame(i6, 30.0f, i7, f4, f5, 12.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context) {
            Paint grayPaint = new Paint();
            Paint whitePaint;

            {
                Paint paint = new Paint();
                this.whitePaint = paint;
                paint.setColor(-1);
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                Paint paint;
                int i9;
                if (!LimitPreviewView.this.isBoostsStyle) {
                    paint = this.grayPaint;
                    i9 = Theme.key_windowBackgroundGray;
                } else if (LimitPreviewView.this.isStatistic) {
                    paint = this.grayPaint;
                    i9 = Theme.key_listSelector;
                } else {
                    paint = this.grayPaint;
                    i9 = Theme.key_graySection;
                }
                paint.setColor(Theme.getColor(i9, resourcesProvider));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                if (LimitPreviewView.this.hasDarkGradientProvider()) {
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), LimitPreviewView.this.darkGradientProvider.setDarkGradientLocation(((ViewGroup) getParent()).getX() + getX(), ((ViewGroup) getParent()).getY() + getY()));
                } else {
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.grayPaint);
                }
                canvas.save();
                if (!LimitPreviewView.this.isBoostsStyle) {
                    canvas.clipRect(LimitPreviewView.this.width1, 0, getMeasuredWidth(), getMeasuredHeight());
                }
                Paint mainGradientPaint = LimitPreviewView.this.hasDarkGradientProvider() ? this.whitePaint : PremiumGradient.getInstance().getMainGradientPaint();
                if (LimitPreviewView.this.parentVideForGradient != null) {
                    View view = LimitPreviewView.this.parentVideForGradient;
                    PremiumGradient.PremiumGradientTools premiumGradientTools = LimitPreviewView.this.staticGradient;
                    if (premiumGradientTools != null) {
                        mainGradientPaint = premiumGradientTools.paint;
                        premiumGradientTools.gradientMatrixLinear(r6.gradientTotalHeight, -r6.gradientYOffset);
                    } else {
                        float f6 = 0.0f;
                        for (View view2 = this; view2 != view; view2 = (View) view2.getParent()) {
                            f6 += view2.getY();
                        }
                        PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getLeft(), -f6);
                    }
                } else {
                    PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getLeft(), -getTop());
                }
                if (LimitPreviewView.this.isBoostsStyle) {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, LimitPreviewView.this.width1, getMeasuredHeight());
                }
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), mainGradientPaint);
                canvas.restore();
                LimitPreviewView limitPreviewView = LimitPreviewView.this;
                if (limitPreviewView.staticGradient == null && limitPreviewView.invalidationEnabled) {
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }

            @Override
            protected void onLayout(boolean z, int i9, int i10, int i11, int i12) {
                if (getChildCount() != 2) {
                    super.onLayout(z, i9, i10, i11, i12);
                    return;
                }
                View childAt = getChildAt(0);
                View childAt2 = getChildAt(1);
                int measuredWidth = childAt.getMeasuredWidth();
                int i13 = i12 - i10;
                childAt.layout(0, 0, measuredWidth, i13);
                childAt2.layout(measuredWidth, 0, i11 - i9, i13);
            }

            @Override
            protected void onMeasure(int i9, int i10) {
                TextView textView7;
                if (getChildCount() != 2) {
                    super.onMeasure(i9, i10);
                    return;
                }
                int size = View.MeasureSpec.getSize(i9);
                int size2 = View.MeasureSpec.getSize(i10);
                textViewHolder.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                int max = Math.max(textViewHolder.getMeasuredWidth(), AndroidUtilities.dp(24.0f) + LimitPreviewView.this.defaultText.getMeasuredWidth() + (LimitPreviewView.this.defaultCount.getVisibility() == 0 ? AndroidUtilities.dp(24.0f) + LimitPreviewView.this.defaultCount.getMeasuredWidth() : 0));
                textViewHolder2.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                if (LimitPreviewView.this.isBoostsStyle) {
                    int i11 = -1;
                    if (LimitPreviewView.this.percent == 0.0f) {
                        LimitPreviewView limitPreviewView = LimitPreviewView.this;
                        limitPreviewView.width1 = 0;
                        limitPreviewView.premiumCount.setTextColor(limitPreviewView.hasDarkGradientProvider() ? -1 : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
                        textView7 = LimitPreviewView.this.defaultText;
                        if (!LimitPreviewView.this.hasDarkGradientProvider()) {
                            i11 = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider);
                        }
                    } else {
                        if (LimitPreviewView.this.percent < 1.0f) {
                            float measuredWidth = textViewHolder.getMeasuredWidth() - AndroidUtilities.dp(8.0f);
                            LimitPreviewView limitPreviewView2 = LimitPreviewView.this;
                            limitPreviewView2.width1 = (int) (measuredWidth + (((size - measuredWidth) - (textViewHolder2.getMeasuredWidth() - AndroidUtilities.dp(8.0f))) * limitPreviewView2.percent));
                            LimitPreviewView limitPreviewView3 = LimitPreviewView.this;
                            limitPreviewView3.premiumCount.setTextColor(limitPreviewView3.hasDarkGradientProvider() ? -1 : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
                        } else {
                            LimitPreviewView limitPreviewView4 = LimitPreviewView.this;
                            limitPreviewView4.width1 = size;
                            limitPreviewView4.premiumCount.setTextColor(-1);
                        }
                        textView7 = LimitPreviewView.this.defaultText;
                    }
                    textView7.setTextColor(i11);
                } else {
                    int max2 = Math.max(textViewHolder2.getMeasuredWidth(), AndroidUtilities.dp(24.0f) + LimitPreviewView.this.premiumText.getMeasuredWidth() + (LimitPreviewView.this.premiumCount.getVisibility() == 0 ? AndroidUtilities.dp(24.0f) + LimitPreviewView.this.premiumCount.getMeasuredWidth() : 0));
                    LimitPreviewView limitPreviewView5 = LimitPreviewView.this;
                    limitPreviewView5.width1 = (int) Utilities.clamp(size * limitPreviewView5.percent, size - max2, max);
                    textViewHolder.measure(View.MeasureSpec.makeMeasureSpec(LimitPreviewView.this.width1, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                    textViewHolder2.measure(View.MeasureSpec.makeMeasureSpec(size - LimitPreviewView.this.width1, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                }
                setMeasuredDimension(size, size2);
            }
        };
        this.limitsContainer = frameLayout;
        frameLayout.addView(textViewHolder, LayoutHelper.createFrame(-1, 30.0f));
        this.limitsContainer.addView(textViewHolder2, LayoutHelper.createFrame(-1, 30.0f));
        addView(this.limitsContainer, LayoutHelper.createLinear(-1, 30, 0.0f, 0, 14, i == 0 ? 0 : 12, 14, 0));
    }

    public LimitPreviewView(Context context, int i, int i2, int i3, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, i3, 0.5f, resourcesProvider);
    }

    public float getGlobalXOffset() {
        return (((-getMeasuredWidth()) * 0.1f) * this.progress) - (getMeasuredWidth() * 0.2f);
    }

    public boolean hasDarkGradientProvider() {
        return this.darkGradientProvider != null;
    }

    public void lambda$onLayout$0(boolean z, float f, float f2, float f3, float f4, boolean z2, float f5, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float min = Math.min(1.0f, floatValue);
        if (floatValue > 1.0f && z) {
            if (!this.wasHaptic) {
                this.wasHaptic = true;
                try {
                    this.limitIcon.performHapticFeedback(3);
                } catch (Exception unused) {
                }
            }
            this.limitIcon.setRotation(this.limitIconRotation + ((floatValue - 1.0f) * 60.0f));
        } else if (!this.animatingRotation) {
            this.limitIcon.setRotation(this.limitIconRotation);
        }
        if (valueAnimator == this.arrowAnimator) {
            float f6 = 1.0f - min;
            this.limitIcon.setTranslationX((f * f6) + (f2 * min));
            float f7 = (f3 * f6) + (f4 * min);
            this.limitIcon.setArrowCenter(f7);
            this.limitIcon.setPivotX(r6.getMeasuredWidth() * f7);
        }
        float min2 = Math.min(1.0f, 2.0f * min);
        if (z2) {
            this.width1 = (int) AndroidUtilities.lerp(this.animateIncreaseWidth, f5, min);
            this.limitsContainer.invalidate();
        } else {
            this.limitIcon.setScaleX(min2);
            this.limitIcon.setScaleY(min2);
        }
    }

    public void lambda$onLayout$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.limitIconRotation = floatValue < 0.5f ? (floatValue / 0.5f) * (-7.0f) : (1.0f - ((floatValue - 0.5f) / 0.5f)) * (-7.0f);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        boolean z;
        if (this.staticGradient == null) {
            if (this.inc) {
                float f = this.progress + 0.016f;
                this.progress = f;
                if (f > 3.0f) {
                    z = false;
                    this.inc = z;
                }
                invalidate();
            } else {
                float f2 = this.progress - 0.016f;
                this.progress = f2;
                if (f2 < 1.0f) {
                    z = true;
                    this.inc = z;
                }
                invalidate();
            }
        }
        super.dispatchDraw(canvas);
    }

    public void increaseCurrentValue(int i, int i2, int i3) {
        this.currentValue++;
        this.percent = MathUtils.clamp(i2 / i3, 0.0f, 1.0f);
        this.animateIncrease = true;
        this.animateIncreaseWidth = this.width1;
        setIconValue(i, true);
        this.limitsContainer.requestLayout();
        requestLayout();
    }

    @Override
    protected void onLayout(boolean r17, int r18, int r19, int r20, int r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.LimitPreviewView.onLayout(boolean, int, int, int, int):void");
    }

    public void setBagePosition(float f) {
        this.position = MathUtils.clamp(f, 0.1f, 0.9f);
    }

    public void setBoosts(TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, boolean z) {
        int i;
        TextView textView;
        String formatString;
        if ((tL_premium_boostsStatus.current_level_boosts == tL_premium_boostsStatus.boosts && z) || (i = tL_premium_boostsStatus.next_level_boosts) == 0) {
            this.percent = 1.0f;
            TextView textView2 = this.defaultText;
            int i2 = R.string.BoostsLevel;
            textView2.setText(LocaleController.formatString("BoostsLevel", i2, Integer.valueOf(tL_premium_boostsStatus.level - 1)));
            textView = this.premiumCount;
            formatString = LocaleController.formatString("BoostsLevel", i2, Integer.valueOf(tL_premium_boostsStatus.level));
        } else {
            this.percent = MathUtils.clamp((r2 - r1) / (i - r1), 0.0f, 1.0f);
            TextView textView3 = this.defaultText;
            int i3 = R.string.BoostsLevel;
            textView3.setText(LocaleController.formatString("BoostsLevel", i3, Integer.valueOf(tL_premium_boostsStatus.level)));
            textView = this.premiumCount;
            formatString = LocaleController.formatString("BoostsLevel", i3, Integer.valueOf(tL_premium_boostsStatus.level + 1));
        }
        textView.setText(formatString);
        ((FrameLayout.LayoutParams) this.premiumCount.getLayoutParams()).gravity = 5;
        setType(17);
        this.defaultCount.setVisibility(8);
        this.premiumText.setVisibility(8);
        this.premiumCount.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
        this.defaultText.setTextColor(-1);
        setIconValue(tL_premium_boostsStatus.boosts, false);
        this.isBoostsStyle = true;
    }

    public void setDarkGradientProvider(DarkGradientProvider darkGradientProvider) {
        this.darkGradientProvider = darkGradientProvider;
    }

    public void setDelayedAnimation() {
        this.animationCanPlay = false;
    }

    public void setIconValue(int i, boolean z) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "d").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
        spannableStringBuilder.append((CharSequence) " ").setSpan(new RelativeSizeSpan(0.8f), 1, 2, 0);
        spannableStringBuilder.append((CharSequence) Integer.toString(i));
        this.limitIcon.setText(spannableStringBuilder, z);
        this.limitIcon.requestLayout();
    }

    public void setParentViewForGradien(ViewGroup viewGroup) {
        this.parentVideForGradient = viewGroup;
    }

    public void setPremiumLocked() {
        this.limitsContainer.setVisibility(8);
        CounterView counterView = this.limitIcon;
        if (counterView != null) {
            counterView.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f));
        }
        this.premiumLocked = true;
    }

    public void setStaticGradinet(PremiumGradient.PremiumGradientTools premiumGradientTools) {
        this.staticGradient = premiumGradientTools;
    }

    public void setType(int i) {
        TextView textView;
        String str;
        if (i == 6) {
            str = "4 GB";
            if (this.limitIcon != null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append((CharSequence) "d ").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
                spannableStringBuilder.append((CharSequence) (UserConfig.getInstance(UserConfig.selectedAccount).isPremium() ? "4 GB" : "2 GB"));
                this.limitIcon.setText(spannableStringBuilder, false);
            }
            textView = this.premiumCount;
        } else {
            if (i != 11) {
                return;
            }
            if (this.limitIcon != null) {
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
                spannableStringBuilder2.append((CharSequence) "d").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
                this.limitIcon.setText(spannableStringBuilder2, false);
            }
            textView = this.premiumCount;
            str = "";
        }
        textView.setText(str);
    }

    public void startDelayedAnimation() {
        this.animationCanPlay = true;
        requestLayout();
    }
}
