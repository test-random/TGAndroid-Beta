package org.telegram.ui.Components.Premium.boosts.cells.selector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.boosts.cells.BaseCell;
import org.telegram.ui.Components.RadioButton;

public class SelectorCountryCell extends BaseCell {
    private final CheckBox2 checkBox;
    private TLRPC.TL_help_country country;

    public static class SpaceDrawable extends ReplacementSpan {
        private final int size;

        public SpaceDrawable(int i) {
            this.size = i;
        }

        @Override
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        }

        @Override
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return AndroidUtilities.dp(this.size);
        }
    }

    public SelectorCountryCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.titleTextView.setTypeface(AndroidUtilities.bold());
        this.radioButton.setVisibility(8);
        this.imageView.setVisibility(8);
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_dialogRoundCheckBox, Theme.key_checkboxDisabled, Theme.key_dialogRoundCheckBoxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setDrawBackgroundAsArc(10);
        addView(checkBox2);
        checkBox2.setChecked(false, false);
        checkBox2.setLayoutParams(LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 16, 13.0f, 0.0f, 14.0f, 0.0f));
    }

    private CharSequence getCountryNameWithFlag(TLRPC.TL_help_country tL_help_country) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String languageFlag = LocaleController.getLanguageFlag(tL_help_country.iso2);
        if (languageFlag != null) {
            spannableStringBuilder.append((CharSequence) languageFlag).append((CharSequence) " ");
            spannableStringBuilder.setSpan(new SpaceDrawable(16), languageFlag.length(), languageFlag.length() + 1, 0);
        } else {
            spannableStringBuilder.append((CharSequence) " ");
            spannableStringBuilder.setSpan(new SpaceDrawable(34), 0, 1, 0);
        }
        spannableStringBuilder.append((CharSequence) tL_help_country.default_name);
        return spannableStringBuilder;
    }

    @Override
    protected int dividerPadding() {
        return 22;
    }

    public TLRPC.TL_help_country getCountry() {
        return this.country;
    }

    @Override
    protected int getFullHeight() {
        return 44;
    }

    @Override
    protected boolean needCheck() {
        return true;
    }

    @Override
    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() == 0) {
            this.checkBox.setChecked(z, z2);
        }
    }

    public void setCountry(TLRPC.TL_help_country tL_help_country, boolean z) {
        this.country = tL_help_country;
        this.titleTextView.setText(Emoji.replaceEmoji(getCountryNameWithFlag(tL_help_country), this.titleTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
        setDivider(z);
    }

    @Override
    public void updateLayouts() {
        SimpleTextView simpleTextView = this.titleTextView;
        boolean z = LocaleController.isRTL;
        simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 16, z ? 20.0f : 52.0f, 0.0f, z ? 52.0f : 20.0f, 0.0f));
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        boolean z2 = LocaleController.isRTL;
        simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 16, z2 ? 20.0f : 52.0f, 0.0f, z2 ? 52.0f : 20.0f, 0.0f));
        RadioButton radioButton = this.radioButton;
        boolean z3 = LocaleController.isRTL;
        radioButton.setLayoutParams(LayoutHelper.createFrame(22, 22.0f, (z3 ? 5 : 3) | 16, z3 ? 15.0f : 20.0f, 0.0f, z3 ? 20.0f : 15.0f, 0.0f));
    }
}
