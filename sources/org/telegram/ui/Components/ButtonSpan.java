package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;

public class ButtonSpan extends ReplacementSpan {
    private final Paint backgroundPaint = new Paint(1);
    private ButtonBounce bounce;
    private final Runnable onClickListener;
    private final Theme.ResourcesProvider resourcesProvider;
    private final Text text;

    public static class TextViewButtons extends LinkSpanDrawable.LinksTextView {
        ButtonSpan buttonToBeAdded;
        private ButtonSpan pressedSpan;

        public TextViewButtons(Context context) {
            super(context);
        }

        public TextViewButtons(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        public void addButton(ButtonSpan buttonSpan) {
            this.buttonToBeAdded = buttonSpan;
        }

        public ButtonSpan findSpan(float f, int i) {
            Layout layout;
            if (!(getText() instanceof Spanned) || (layout = getLayout()) == null) {
                return null;
            }
            int lineForVertical = layout.getLineForVertical(i);
            int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, f);
            Spanned spanned = (Spanned) getText();
            for (ButtonSpan buttonSpan : (ButtonSpan[]) spanned.getSpans(layout.getLineStart(lineForVertical), layout.getLineEnd(lineForVertical), ButtonSpan.class)) {
                if (spanned.getSpanStart(buttonSpan) <= offsetForHorizontal && spanned.getSpanEnd(buttonSpan) >= offsetForHorizontal && layout.getPrimaryHorizontal(spanned.getSpanStart(buttonSpan)) <= f && layout.getPrimaryHorizontal(spanned.getSpanEnd(buttonSpan)) >= f) {
                    return buttonSpan;
                }
            }
            return null;
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (this.buttonToBeAdded == null || getMeasuredWidth() <= 0) {
                return;
            }
            SpannableString spannableString = new SpannableString(" btn");
            spannableString.setSpan(this.buttonToBeAdded, 1, spannableString.length(), 33);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.ellipsize(getText(), getPaint(), (((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - this.buttonToBeAdded.getSize()) - AndroidUtilities.dp(4.0f), TextUtils.TruncateAt.END));
            spannableStringBuilder.append((CharSequence) spannableString);
            setText(spannableStringBuilder);
            this.buttonToBeAdded = null;
        }

        @Override
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            ButtonSpan buttonSpan;
            int action = motionEvent.getAction();
            ButtonSpan findSpan = findSpan(motionEvent.getX() - getPaddingLeft(), ((int) motionEvent.getY()) - getPaddingTop());
            if (action == 0) {
                this.pressedSpan = findSpan;
                if (findSpan != null) {
                    findSpan.setPressed(this, true);
                    return true;
                }
            } else {
                if (action == 1 || action == 3) {
                    ButtonSpan buttonSpan2 = this.pressedSpan;
                    if (buttonSpan2 != null) {
                        buttonSpan2.setPressed(this, false);
                        if (action == 1 && this.pressedSpan.onClickListener != null) {
                            this.pressedSpan.onClickListener.run();
                        }
                    }
                } else if (action == 2 && (buttonSpan = this.pressedSpan) != null && buttonSpan != findSpan) {
                    buttonSpan.setPressed(this, false);
                }
                this.pressedSpan = null;
            }
            return this.pressedSpan != null || super.onTouchEvent(motionEvent);
        }
    }

    public ButtonSpan(CharSequence charSequence, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.onClickListener = runnable;
        this.text = new Text(charSequence, 12.0f);
    }

    public static CharSequence make(CharSequence charSequence, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        SpannableString spannableString = new SpannableString("btn");
        spannableString.setSpan(new ButtonSpan(charSequence, runnable, resourcesProvider), 0, spannableString.length(), 33);
        return spannableString;
    }

    @Override
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        float dpf2 = AndroidUtilities.dpf2(17.0f);
        float f2 = (i3 + i5) / 2.0f;
        RectF rectF = AndroidUtilities.rectTmp;
        float f3 = dpf2 / 2.0f;
        rectF.set(f, f2 - f3, getSize() + f, f2 + f3);
        ButtonBounce buttonBounce = this.bounce;
        float scale = buttonBounce == null ? 1.0f : buttonBounce.getScale(0.025f);
        canvas.save();
        canvas.scale(scale, scale, rectF.centerX(), rectF.centerY());
        int color = Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider);
        this.backgroundPaint.setColor(Theme.multAlpha(color, 0.15f));
        canvas.drawRoundRect(rectF, f3, f3, this.backgroundPaint);
        this.text.draw(canvas, f + AndroidUtilities.dp(7.0f), f2, color, 1.0f);
        canvas.restore();
    }

    public int getSize() {
        return (int) (this.text.getCurrentWidth() + AndroidUtilities.dp(14.0f));
    }

    @Override
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        return getSize();
    }

    public void setPressed(View view, boolean z) {
        if (this.bounce == null) {
            this.bounce = new ButtonBounce(view);
        }
        this.bounce.setPressed(z);
    }
}
