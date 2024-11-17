package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class CollapseTextCell extends FrameLayout {
    private View collapsedArrow;
    private Theme.ResourcesProvider resourcesProvider;
    public final AnimatedTextView textView;

    public CollapseTextCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        AnimatedTextView animatedTextView = new AnimatedTextView(context);
        this.textView = animatedTextView;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        animatedTextView.setTextColor(Theme.getColor(i, resourcesProvider));
        animatedTextView.setTextSize(AndroidUtilities.dp(14.0f));
        animatedTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        animatedTextView.setImportantForAccessibility(2);
        animatedTextView.setOnWidthUpdatedListener(new Runnable() {
            @Override
            public final void run() {
                CollapseTextCell.this.updateCollapseArrowTranslation();
            }
        });
        addView(animatedTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 21.0f, 0.0f, 38.0f, 3.0f));
        this.collapsedArrow = new View(context);
        Drawable mutate = getContext().getResources().getDrawable(R.drawable.arrow_more).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        this.collapsedArrow.setBackground(mutate);
        addView(this.collapsedArrow, LayoutHelper.createFrameRelatively(14.0f, 14.0f, 8388627, 21.0f, 1.0f, 0.0f, 3.0f));
    }

    public void updateCollapseArrowTranslation() {
        View view;
        float currentWidth = this.textView.getDrawable().getCurrentWidth() + AndroidUtilities.dp(1.0f);
        if (LocaleController.isRTL) {
            view = this.collapsedArrow;
            currentWidth = -currentWidth;
        } else {
            view = this.collapsedArrow;
        }
        view.setTranslationX(currentWidth);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(46.0f), 1073741824));
        updateCollapseArrowTranslation();
    }

    public void set(CharSequence charSequence, boolean z) {
        this.textView.setText(charSequence);
        this.collapsedArrow.animate().cancel();
        this.collapsedArrow.animate().rotation(z ? 0.0f : 180.0f).setDuration(340L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
    }

    public void setColor(int i) {
        int color = Theme.getColor(i, this.resourcesProvider);
        this.textView.setTextColor(color);
        this.collapsedArrow.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }
}
