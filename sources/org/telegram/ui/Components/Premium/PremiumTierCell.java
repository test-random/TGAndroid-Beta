package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PremiumPreviewFragment;

public class PremiumTierCell extends ViewGroup {
    private CheckBox2 checkBox;
    private int color0;
    private int color1;
    private int colorKey1;
    private int colorKey2;
    protected TextView discountView;
    private PremiumTierCell globalGradientView;
    private LinearGradient gradient;
    private int gradientWidth;
    private boolean hasDivider;
    private boolean isDrawingGradient;
    private long lastUpdateTime;
    private int leftPaddingToCheckboxDp;
    private int leftPaddingToTextDp;
    private Matrix matrix;
    private Paint paint;
    private int parentWidth;
    private float parentXOffset;
    private TextView pricePerMonthView;
    private TextView pricePerYearStrikeView;
    private TextView pricePerYearView;
    protected PremiumPreviewFragment.SubscriptionTier tier;
    private TextView titleView;
    private int totalTranslation;

    public PremiumTierCell(Context context) {
        super(context);
        this.leftPaddingToTextDp = 12;
        this.leftPaddingToCheckboxDp = 8;
        this.colorKey1 = Theme.key_windowBackgroundWhite;
        this.colorKey2 = Theme.key_windowBackgroundGray;
        this.paint = new Paint();
        this.matrix = new Matrix();
        CheckBox2 checkBox2 = new CheckBox2(context, 24);
        this.checkBox = checkBox2;
        checkBox2.setDrawBackgroundAsArc(10);
        CheckBox2 checkBox22 = this.checkBox;
        int i = Theme.key_radioBackground;
        checkBox22.setColor(i, i, Theme.key_checkboxCheck);
        addView(this.checkBox);
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextSize(1, 16.0f);
        this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleView.setTypeface(AndroidUtilities.bold());
        this.titleView.setSingleLine();
        addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 8.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.discountView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.discountView.setTextColor(-1);
        this.discountView.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        this.discountView.setTypeface(AndroidUtilities.bold());
        addView(this.discountView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, 0.0f, 0.0f, 0.0f, 8.0f));
        TextView textView3 = new TextView(context);
        this.pricePerYearStrikeView = textView3;
        textView3.setTextSize(1, 14.0f);
        TextView textView4 = this.pricePerYearStrikeView;
        int i2 = Theme.key_windowBackgroundWhiteGrayText;
        textView4.setTextColor(Theme.getColor(i2));
        this.pricePerYearStrikeView.getPaint().setStrikeThruText(true);
        this.pricePerYearStrikeView.setSingleLine();
        addView(this.pricePerYearStrikeView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, 0.0f, 0.0f, 0.0f, 8.0f));
        TextView textView5 = new TextView(context);
        this.pricePerYearView = textView5;
        textView5.setTextSize(1, 14.0f);
        this.pricePerYearView.setTextColor(Theme.getColor(i2));
        this.pricePerYearView.setSingleLine();
        addView(this.pricePerYearView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, 0.0f, 0.0f, 0.0f, 8.0f));
        TextView textView6 = new TextView(context);
        this.pricePerMonthView = textView6;
        textView6.setTextSize(1, 15.0f);
        this.pricePerMonthView.setTextColor(Theme.getColor(i2));
        this.pricePerMonthView.setSingleLine();
        addView(this.pricePerMonthView, LayoutHelper.createFrame(-2, -2, 8388613));
        setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(8.0f));
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    private void checkRtlAndLayout(View view) {
        Rect rect = AndroidUtilities.rectTmp2;
        rect.right = rect.left + view.getMeasuredWidth();
        rect.bottom = rect.top + view.getMeasuredHeight();
        if (LocaleController.isRTL) {
            int i = rect.right;
            rect.right = getWidth() - rect.left;
            rect.left = getWidth() - i;
        }
        view.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void bind(org.telegram.ui.PremiumPreviewFragment.SubscriptionTier r6, boolean r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.PremiumTierCell.bind(org.telegram.ui.PremiumPreviewFragment$SubscriptionTier, boolean):void");
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        if (!this.isDrawingGradient) {
            super.dispatchDraw(canvas);
            return;
        }
        Paint paint = this.paint;
        PremiumTierCell premiumTierCell = this.globalGradientView;
        if (premiumTierCell != null) {
            paint = premiumTierCell.paint;
        }
        drawChild(canvas, this.checkBox, getDrawingTime());
        updateColors();
        updateGradient();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(this.pricePerMonthView.getLeft(), this.pricePerMonthView.getTop() + AndroidUtilities.dp(4.0f), this.pricePerMonthView.getRight(), this.pricePerMonthView.getBottom() - AndroidUtilities.dp(4.0f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), paint);
        rectF.set(this.pricePerYearStrikeView.getLeft(), this.pricePerYearStrikeView.getTop() + AndroidUtilities.dp(3.0f), this.pricePerYearStrikeView.getRight(), this.pricePerYearStrikeView.getBottom() - AndroidUtilities.dp(3.0f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), paint);
        rectF.set(this.titleView.getLeft(), this.titleView.getTop() + AndroidUtilities.dp(4.0f), this.titleView.getRight(), this.titleView.getBottom() - AndroidUtilities.dp(4.0f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), paint);
        invalidate();
    }

    public PremiumPreviewFragment.SubscriptionTier getTier() {
        return this.tier;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left;
        float height;
        float width;
        float height2;
        Paint paint;
        super.onDraw(canvas);
        if (this.hasDivider) {
            if (LocaleController.isRTL) {
                height = getHeight() - 1;
                width = this.titleView.getRight();
                height2 = getHeight() - 1;
                paint = Theme.dividerPaint;
                left = 0.0f;
            } else {
                left = this.titleView.getLeft();
                height = getHeight() - 1;
                width = getWidth();
                height2 = getHeight() - 1;
                paint = Theme.dividerPaint;
            }
            canvas.drawLine(left, height, width, height2, paint);
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Rect rect = AndroidUtilities.rectTmp2;
        rect.set(AndroidUtilities.dp(this.leftPaddingToCheckboxDp) + getPaddingLeft(), (int) ((getMeasuredHeight() - this.checkBox.getMeasuredHeight()) / 2.0f), 0, 0);
        checkRtlAndLayout(this.checkBox);
        int measuredHeight = (int) ((getMeasuredHeight() - this.pricePerMonthView.getMeasuredHeight()) / 2.0f);
        if (AndroidUtilities.dp(this.leftPaddingToCheckboxDp + this.leftPaddingToTextDp + 24) + this.checkBox.getMeasuredWidth() + (this.pricePerYearStrikeView.getVisibility() == 0 ? this.pricePerYearStrikeView.getMeasuredWidth() : 0) + this.pricePerYearView.getMeasuredWidth() + getPaddingLeft() > getMeasuredWidth() - this.pricePerMonthView.getMeasuredWidth() && this.discountView.getVisibility() == 0) {
            measuredHeight = getPaddingTop() + AndroidUtilities.dp(2.0f);
        }
        rect.set(((getMeasuredWidth() - this.pricePerMonthView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f)) - getPaddingRight(), measuredHeight, 0, 0);
        checkRtlAndLayout(this.pricePerMonthView);
        rect.set(AndroidUtilities.dp(this.leftPaddingToCheckboxDp + this.leftPaddingToTextDp) + this.checkBox.getMeasuredWidth() + getPaddingLeft(), this.pricePerYearView.getVisibility() == 8 ? (int) ((getMeasuredHeight() - this.titleView.getMeasuredHeight()) / 2.0f) : getPaddingTop(), 0, 0);
        checkRtlAndLayout(this.titleView);
        if (this.discountView.getVisibility() == 0) {
            rect.set(AndroidUtilities.dp(this.leftPaddingToCheckboxDp + this.leftPaddingToTextDp + 6) + this.checkBox.getMeasuredWidth() + getPaddingLeft() + this.titleView.getMeasuredWidth(), getPaddingTop() + AndroidUtilities.dp(2.0f), 0, 0);
            checkRtlAndLayout(this.discountView);
        }
        rect.set(AndroidUtilities.dp(this.leftPaddingToCheckboxDp + this.leftPaddingToTextDp) + this.checkBox.getMeasuredWidth() + getPaddingLeft(), (getMeasuredHeight() - this.pricePerYearStrikeView.getMeasuredHeight()) - getPaddingBottom(), 0, 0);
        checkRtlAndLayout(this.pricePerYearStrikeView);
        rect.set(AndroidUtilities.dp(this.leftPaddingToCheckboxDp + this.leftPaddingToTextDp) + this.checkBox.getMeasuredWidth() + (this.pricePerYearStrikeView.getVisibility() == 0 ? this.pricePerYearStrikeView.getMeasuredWidth() + AndroidUtilities.dp(6.0f) : 0) + getPaddingLeft(), (getMeasuredHeight() - this.pricePerYearView.getMeasuredHeight()) - getPaddingBottom(), 0, 0);
        checkRtlAndLayout(this.pricePerYearView);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(58.0f);
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), 1073741824);
        this.checkBox.measure(makeMeasureSpec, makeMeasureSpec);
        this.pricePerMonthView.measure(View.MeasureSpec.makeMeasureSpec(size - this.checkBox.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        this.titleView.measure(View.MeasureSpec.makeMeasureSpec((size - this.checkBox.getMeasuredWidth()) - this.pricePerMonthView.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        if (this.discountView.getVisibility() == 0) {
            this.discountView.measure(View.MeasureSpec.makeMeasureSpec((size - this.checkBox.getMeasuredWidth()) - this.pricePerMonthView.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        } else {
            this.discountView.measure(View.MeasureSpec.makeMeasureSpec(0, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 1073741824));
        }
        this.pricePerYearStrikeView.measure(View.MeasureSpec.makeMeasureSpec(size - this.checkBox.getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        this.pricePerYearView.measure(View.MeasureSpec.makeMeasureSpec(((size - this.checkBox.getMeasuredWidth()) - (this.pricePerYearStrikeView.getVisibility() == 0 ? this.pricePerYearStrikeView.getMeasuredWidth() : 0)) - AndroidUtilities.dp(6.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
        if (this.pricePerYearView.getVisibility() != 0) {
            dp -= AndroidUtilities.dp(8.0f);
        }
        setMeasuredDimension(size, dp);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setCirclePaintProvider(GenericProvider<Void, Paint> genericProvider) {
        this.checkBox.setCirclePaintProvider(genericProvider);
    }

    @Override
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.titleView.setAlpha(z ? 1.0f : 0.6f);
        this.pricePerMonthView.setAlpha(z ? 1.0f : 0.6f);
        this.checkBox.setAlpha(z ? 1.0f : 0.6f);
    }

    public void setGlobalGradientView(PremiumTierCell premiumTierCell) {
        this.globalGradientView = premiumTierCell;
    }

    public void setParentXOffset(float f) {
        this.parentXOffset = f;
    }

    public void setProgressDelegate(CheckBoxBase.ProgressDelegate progressDelegate) {
        this.checkBox.setProgressDelegate(progressDelegate);
    }

    public void updateColors() {
        PremiumTierCell premiumTierCell = this.globalGradientView;
        if (premiumTierCell != null) {
            premiumTierCell.updateColors();
            return;
        }
        int color = Theme.getColor(this.colorKey1);
        int color2 = Theme.getColor(this.colorKey2);
        if (this.color1 == color2 && this.color0 == color) {
            return;
        }
        this.color0 = color;
        this.color1 = color2;
        int dp = AndroidUtilities.dp(200.0f);
        this.gradientWidth = dp;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color2, color, color, color2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
        this.gradient = linearGradient;
        this.paint.setShader(linearGradient);
    }

    public void updateGradient() {
        PremiumTierCell premiumTierCell = this.globalGradientView;
        if (premiumTierCell != null) {
            premiumTierCell.updateGradient();
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
        if (abs > 17) {
            abs = 16;
        }
        if (abs < 4) {
            abs = 0;
        }
        int i = this.parentWidth;
        if (i == 0) {
            i = getMeasuredWidth();
        }
        this.lastUpdateTime = elapsedRealtime;
        int i2 = (int) (this.totalTranslation + (((float) (abs * i)) / 400.0f));
        this.totalTranslation = i2;
        if (i2 >= i * 4) {
            this.totalTranslation = (-this.gradientWidth) * 2;
        }
        this.matrix.setTranslate(this.totalTranslation + this.parentXOffset, 0.0f);
        LinearGradient linearGradient = this.gradient;
        if (linearGradient != null) {
            linearGradient.setLocalMatrix(this.matrix);
        }
    }
}
