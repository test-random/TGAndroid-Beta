package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;

public class MessageSeenCheckDrawable {
    private int colorKey;
    private Drawable drawable;
    private int h;
    private int lastColor;
    private float lastDensity;
    private CharSequence lastSpanned;
    private float oy;
    private int resId;
    private int w;

    public MessageSeenCheckDrawable(int i, int i2) {
        this.w = -1;
        this.h = -1;
        this.oy = 4.66f;
        this.resId = i;
        this.colorKey = i2;
    }

    public MessageSeenCheckDrawable(int i, int i2, int i3, int i4, float f) {
        this(i, i2);
        this.w = i3;
        this.h = i4;
        this.oy = f;
    }

    public CharSequence getSpanned(Context context, Theme.ResourcesProvider resourcesProvider) {
        if (this.lastSpanned != null && this.drawable != null && AndroidUtilities.density == this.lastDensity) {
            if (this.lastColor != Theme.getColor(this.colorKey, resourcesProvider)) {
                Drawable drawable = this.drawable;
                int color = Theme.getColor(this.colorKey, resourcesProvider);
                this.lastColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
            return this.lastSpanned;
        }
        if (context == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("v ");
        this.lastDensity = AndroidUtilities.density;
        Drawable mutate = context.getResources().getDrawable(this.resId).mutate();
        this.drawable = mutate;
        int color2 = Theme.getColor(this.colorKey, resourcesProvider);
        this.lastColor = color2;
        mutate.setColorFilter(new PorterDuffColorFilter(color2, PorterDuff.Mode.SRC_IN));
        int i = this.w;
        int intrinsicWidth = i <= 0 ? this.drawable.getIntrinsicWidth() : AndroidUtilities.dp(i);
        int i2 = this.h;
        int intrinsicHeight = i2 <= 0 ? this.drawable.getIntrinsicHeight() : AndroidUtilities.dp(i2);
        int dp = AndroidUtilities.dp(this.oy);
        this.drawable.setBounds(0, dp, intrinsicWidth, intrinsicHeight + dp);
        spannableStringBuilder.setSpan(new ImageSpan(this.drawable, 2), 0, 1, 33);
        spannableStringBuilder.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(2.0f)), 1, 2, 33);
        this.lastSpanned = spannableStringBuilder;
        return spannableStringBuilder;
    }
}
