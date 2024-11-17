package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class SettingsSearchCell extends FrameLayout {
    private ImageView imageView;
    private int left;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public static class VerticalImageSpan extends ImageSpan {
        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        @Override
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            int i6 = paint.getFontMetricsInt().descent;
            canvas.translate(f, ((i4 + i6) - ((i6 - r3.ascent) / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2));
            if (LocaleController.isRTL) {
                canvas.scale(-1.0f, 1.0f, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
            }
            drawable.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            Rect bounds = getDrawable().getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fontMetricsInt2 = paint.getFontMetricsInt();
                int i3 = fontMetricsInt2.descent;
                int i4 = fontMetricsInt2.ascent;
                int i5 = i4 + ((i3 - i4) / 2);
                int i6 = (bounds.bottom - bounds.top) / 2;
                int i7 = i5 - i6;
                fontMetricsInt.ascent = i7;
                fontMetricsInt.top = i7;
                int i8 = i5 + i6;
                fontMetricsInt.bottom = i8;
                fontMetricsInt.descent = i8;
            }
            return bounds.right;
        }
    }

    public SettingsSearchCell(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView2 = this.textView;
        boolean z = LocaleController.isRTL;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, z ? 5 : 3, z ? 16.0f : 71.0f, 10.0f, z ? 71.0f : 16.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView4 = this.valueTextView;
        boolean z2 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, z2 ? 5 : 3, z2 ? 16.0f : 71.0f, 33.0f, z2 ? 71.0f : 16.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 8.0f, 10.0f, 0.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(this.left), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(this.left) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setTextAndValue(CharSequence charSequence, String[] strArr, boolean z, boolean z2) {
        SpannableStringBuilder spannableStringBuilder;
        TextView textView;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        if (z) {
            this.valueTextView.setText(charSequence);
            spannableStringBuilder = new SpannableStringBuilder();
            for (int i = 0; i < strArr.length; i++) {
                if (i != 0) {
                    spannableStringBuilder.append((CharSequence) " > ");
                    Drawable mutate = getContext().getResources().getDrawable(R.drawable.settings_arrow).mutate();
                    mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
                    mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
                    spannableStringBuilder.setSpan(new VerticalImageSpan(mutate), spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                }
                spannableStringBuilder.append((CharSequence) strArr[i]);
            }
            textView = this.textView;
        } else {
            this.textView.setText(charSequence);
            if (strArr == null) {
                layoutParams.topMargin = AndroidUtilities.dp(21.0f);
                this.valueTextView.setVisibility(8);
                int dp = AndroidUtilities.dp(16.0f);
                layoutParams.rightMargin = dp;
                layoutParams.leftMargin = dp;
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.valueTextView.getLayoutParams();
                int dp2 = AndroidUtilities.dp(16.0f);
                layoutParams2.rightMargin = dp2;
                layoutParams2.leftMargin = dp2;
                this.imageView.setVisibility(8);
                this.needDivider = z2;
                setWillNotDraw(!z2);
                this.left = 16;
            }
            spannableStringBuilder = new SpannableStringBuilder();
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (i2 != 0) {
                    spannableStringBuilder.append((CharSequence) " > ");
                    Drawable mutate2 = getContext().getResources().getDrawable(R.drawable.settings_arrow).mutate();
                    mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), mutate2.getIntrinsicHeight());
                    mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), PorterDuff.Mode.MULTIPLY));
                    spannableStringBuilder.setSpan(new VerticalImageSpan(mutate2), spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                }
                spannableStringBuilder.append((CharSequence) strArr[i2]);
            }
            textView = this.valueTextView;
        }
        textView.setText(spannableStringBuilder);
        this.valueTextView.setVisibility(0);
        layoutParams.topMargin = AndroidUtilities.dp(10.0f);
        int dp3 = AndroidUtilities.dp(16.0f);
        layoutParams.rightMargin = dp3;
        layoutParams.leftMargin = dp3;
        FrameLayout.LayoutParams layoutParams22 = (FrameLayout.LayoutParams) this.valueTextView.getLayoutParams();
        int dp22 = AndroidUtilities.dp(16.0f);
        layoutParams22.rightMargin = dp22;
        layoutParams22.leftMargin = dp22;
        this.imageView.setVisibility(8);
        this.needDivider = z2;
        setWillNotDraw(!z2);
        this.left = 16;
    }

    public void setTextAndValueAndIcon(CharSequence charSequence, String[] strArr, int i, boolean z) {
        this.textView.setText(charSequence);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        if (strArr != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (i2 != 0) {
                    spannableStringBuilder.append((CharSequence) " > ");
                    Drawable mutate = getContext().getResources().getDrawable(R.drawable.settings_arrow).mutate();
                    mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
                    mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), PorterDuff.Mode.MULTIPLY));
                    spannableStringBuilder.setSpan(new VerticalImageSpan(mutate), spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                }
                spannableStringBuilder.append((CharSequence) strArr[i2]);
            }
            this.valueTextView.setText(spannableStringBuilder);
            this.valueTextView.setVisibility(0);
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.valueTextView.getLayoutParams();
            layoutParams2.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
            layoutParams2.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 71.0f : 16.0f);
        } else {
            layoutParams.topMargin = AndroidUtilities.dp(21.0f);
            this.valueTextView.setVisibility(8);
        }
        ImageView imageView = this.imageView;
        if (i != 0) {
            imageView.setImageResource(i);
            this.imageView.setVisibility(0);
        } else {
            imageView.setVisibility(8);
        }
        this.left = 69;
        this.needDivider = z;
        setWillNotDraw(!z);
    }
}
