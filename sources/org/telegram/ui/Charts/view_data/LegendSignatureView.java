package org.telegram.ui.Charts.view_data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChannelMonetizationLayout;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Stars.StarsIntroActivity;

public class LegendSignatureView extends FrameLayout {
    Drawable backgroundDrawable;
    public boolean canGoZoom;
    public ImageView chevron;
    LinearLayout content;
    SimpleDateFormat format;
    SimpleDateFormat format2;
    SimpleDateFormat format3;
    SimpleDateFormat format4;
    private DecimalFormat formatterTON;
    Holder[] holders;
    SimpleDateFormat hourFormat;
    TextView hourTime;
    public boolean isTopHourChart;
    private RadialProgressView progressView;
    private Theme.ResourcesProvider resourcesProvider;
    Drawable shadowDrawable;
    public boolean showPercentage;
    Runnable showProgressRunnable;
    TextView time;
    public boolean useHour;
    public boolean useWeek;
    public boolean zoomEnabled;

    public class Holder {
        TextView percentage;
        final LinearLayout root;
        final TextView signature;
        final AnimatedEmojiSpan.TextViewEmojis value;

        Holder() {
            LinearLayout linearLayout = new LinearLayout(LegendSignatureView.this.getContext());
            this.root = linearLayout;
            linearLayout.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            if (LegendSignatureView.this.showPercentage) {
                TextView textView = new TextView(LegendSignatureView.this.getContext());
                this.percentage = textView;
                linearLayout.addView(textView);
                this.percentage.getLayoutParams().width = AndroidUtilities.dp(36.0f);
                this.percentage.setVisibility(8);
                this.percentage.setTypeface(AndroidUtilities.bold());
                this.percentage.setTextSize(1, 13.0f);
            }
            TextView textView2 = new TextView(LegendSignatureView.this.getContext());
            this.signature = textView2;
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 20.0f, 0.0f));
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(LegendSignatureView.this.getContext());
            this.value = textViewEmojis;
            linearLayout.addView(textViewEmojis, LayoutHelper.createLinear(-1, -2));
            textView2.setGravity(8388611);
            textViewEmojis.setGravity(8388613);
            textViewEmojis.setTypeface(AndroidUtilities.bold());
            textViewEmojis.setTextSize(1, 13.0f);
            textView2.setTextSize(1, 13.0f);
        }
    }

    public LegendSignatureView(Context context) {
        this(context, null);
    }

    public LegendSignatureView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.format = new SimpleDateFormat("E, ");
        this.format2 = new SimpleDateFormat("MMM dd");
        this.format3 = new SimpleDateFormat("d MMM yyyy");
        this.format4 = new SimpleDateFormat("d MMM");
        this.hourFormat = new SimpleDateFormat(" HH:mm");
        this.canGoZoom = true;
        this.showProgressRunnable = new Runnable() {
            @Override
            public void run() {
                LegendSignatureView.this.chevron.animate().setDuration(120L).alpha(0.0f);
                LegendSignatureView.this.progressView.animate().setListener(null).start();
                if (LegendSignatureView.this.progressView.getVisibility() != 0) {
                    LegendSignatureView.this.progressView.setVisibility(0);
                    LegendSignatureView.this.progressView.setAlpha(0.0f);
                }
                LegendSignatureView.this.progressView.animate().setDuration(120L).alpha(1.0f).start();
            }
        };
        this.resourcesProvider = resourcesProvider;
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.content = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.time = textView;
        textView.setTextSize(1, 14.0f);
        this.time.setTypeface(AndroidUtilities.bold());
        TextView textView2 = new TextView(context);
        this.hourTime = textView2;
        textView2.setTextSize(1, 14.0f);
        this.hourTime.setTypeface(AndroidUtilities.bold());
        ImageView imageView = new ImageView(context);
        this.chevron = imageView;
        imageView.setImageResource(R.drawable.ic_chevron_right_black_18dp);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(12.0f));
        this.progressView.setStrokeWidth(AndroidUtilities.dp(0.5f));
        this.progressView.setVisibility(8);
        addView(this.content, LayoutHelper.createFrame(-2, -2.0f, 0, 0.0f, 22.0f, 0.0f, 0.0f));
        addView(this.time, LayoutHelper.createFrame(-2, -2.0f, 8388611, 4.0f, 0.0f, 4.0f, 0.0f));
        addView(this.hourTime, LayoutHelper.createFrame(-2, -2.0f, 8388613, 4.0f, 0.0f, 4.0f, 0.0f));
        addView(this.chevron, LayoutHelper.createFrame(18, 18.0f, 8388661, 0.0f, 2.0f, 0.0f, 0.0f));
        addView(this.progressView, LayoutHelper.createFrame(18, 18.0f, 8388661, 0.0f, 2.0f, 0.0f, 0.0f));
        recolor();
    }

    private String capitalize(String str) {
        if (str.length() <= 0) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private String formatData(Date date) {
        if (this.useHour) {
            return capitalize(this.format2.format(date));
        }
        return capitalize(this.format.format(date)) + capitalize(this.format2.format(date));
    }

    public CharSequence formatWholeNumber(long j, int i, int i2, TextView textView, float f) {
        if (i == 1) {
            if (i2 != 0) {
                return "≈" + BillingController.getInstance().formatCurrency(((float) j) / f, "USD");
            }
            if (this.formatterTON == null) {
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
                decimalFormatSymbols.setDecimalSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
                this.formatterTON = decimalFormat;
                decimalFormat.setMinimumFractionDigits(2);
                this.formatterTON.setMaximumFractionDigits(6);
                this.formatterTON.setGroupingUsed(false);
            }
            this.formatterTON.setMaximumFractionDigits(j <= 1000000000 ? 6 : 2);
            StringBuilder sb = new StringBuilder();
            sb.append("TON ");
            DecimalFormat decimalFormat2 = this.formatterTON;
            double d = j;
            Double.isNaN(d);
            sb.append(decimalFormat2.format(d / 1.0E9d));
            return ChannelMonetizationLayout.replaceTON(sb.toString(), textView.getPaint(), 0.82f, false);
        }
        if (i == 2) {
            if (i2 == 0) {
                return StarsIntroActivity.replaceStarsWithPlain("XTR " + LocaleController.formatNumber(j, ' '), 0.7f);
            }
            return "≈" + BillingController.getInstance().formatCurrency(((float) j) / f, "USD");
        }
        float f2 = (float) j;
        if (j < 10000) {
            return String.format("%d", Long.valueOf(j));
        }
        int i3 = 0;
        while (f2 >= 1000.0f && i3 < AndroidUtilities.numbersSignatureArray.length - 1) {
            f2 /= 1000.0f;
            i3++;
        }
        return String.format("%.2f", Float.valueOf(f2)) + AndroidUtilities.numbersSignatureArray[i3];
    }

    public void recolor() {
        TextView textView = this.time;
        int i = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i, this.resourcesProvider));
        this.hourTime.setTextColor(Theme.getColor(i, this.resourcesProvider));
        ImageView imageView = this.chevron;
        int i2 = Theme.key_statisticChartChevronColor;
        imageView.setColorFilter(Theme.getColor(i2, this.resourcesProvider));
        this.progressView.setProgressColor(Theme.getColor(i2, this.resourcesProvider));
        this.shadowDrawable = getContext().getResources().getDrawable(R.drawable.stats_tooltip).mutate();
        this.backgroundDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider), Theme.getColor(Theme.key_listSelector, this.resourcesProvider), -16777216);
        CombinedDrawable combinedDrawable = new CombinedDrawable(this.shadowDrawable, this.backgroundDrawable, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        combinedDrawable.setFullsize(true);
        setBackground(combinedDrawable);
    }

    public void setData(int r23, long r24, java.util.ArrayList r26, boolean r27, int r28, float r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.view_data.LegendSignatureView.setData(int, long, java.util.ArrayList, boolean, int, float):void");
    }

    public void setSize(int i) {
        this.content.removeAllViews();
        this.holders = new Holder[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.holders[i2] = new Holder();
            this.content.addView(this.holders[i2].root);
        }
    }

    public void setUseWeek(boolean z) {
        this.useWeek = z;
    }

    public void showProgress(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(this.showProgressRunnable, 300L);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (z2) {
            this.progressView.setVisibility(8);
            return;
        }
        this.chevron.animate().setDuration(80L).alpha(1.0f).start();
        if (this.progressView.getVisibility() == 0) {
            this.progressView.animate().setDuration(80L).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    LegendSignatureView.this.progressView.setVisibility(8);
                }
            }).start();
        }
    }
}
