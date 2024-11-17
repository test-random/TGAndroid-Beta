package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class SubtitleWithCounterCell extends org.telegram.ui.Cells.HeaderCell {
    private final AnimatedTextView counterTextView;

    public SubtitleWithCounterCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.counterTextView = animatedTextView;
        animatedTextView.setAnimationProperties(0.45f, 0L, 240L, CubicBezierInterpolator.EASE_OUT_QUINT);
        animatedTextView.setGravity(LocaleController.isRTL ? 3 : 5);
        animatedTextView.setTextSize(AndroidUtilities.dp(15.0f));
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
        addView(animatedTextView, LayoutHelper.createFrame(-2, 24.0f, (LocaleController.isRTL ? 3 : 5) | 80, 24.0f, 0.0f, 24.0f, 0.0f));
        setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
    }

    public void updateCounter(boolean z, int i) {
        String formatPluralString = i <= 0 ? "" : LocaleController.formatPluralString("BoostingBoostsCountTitle", i, Integer.valueOf(i));
        this.counterTextView.cancelAnimation();
        this.counterTextView.setText(formatPluralString, z);
    }
}
