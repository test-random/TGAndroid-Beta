package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;

public class ShadowSectionCell extends View {
    private int backgroundColor;
    private boolean bottom;
    private Theme.ResourcesProvider resourcesProvider;
    private int size;
    private boolean top;

    public ShadowSectionCell(Context context) {
        this(context, 12, (Theme.ResourcesProvider) null);
    }

    public ShadowSectionCell(Context context, int i) {
        this(context, i, (Theme.ResourcesProvider) null);
    }

    public ShadowSectionCell(Context context, int i, int i2) {
        this(context, i, i2, null);
    }

    public ShadowSectionCell(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.top = true;
        this.bottom = true;
        this.resourcesProvider = resourcesProvider;
        this.backgroundColor = i2;
        this.size = i;
        updateBackground();
    }

    public ShadowSectionCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.top = true;
        this.bottom = true;
        this.resourcesProvider = resourcesProvider;
        this.size = i;
        updateBackground();
    }

    public ShadowSectionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 12, resourcesProvider);
    }

    private int getBackgroundResId() {
        boolean z = this.top;
        return (z && this.bottom) ? R.drawable.greydivider : z ? R.drawable.greydivider_bottom : this.bottom ? R.drawable.greydivider_top : R.drawable.transparent;
    }

    private void updateBackground() {
        int i = this.backgroundColor;
        if (i == 0) {
            setBackground((this.top || this.bottom) ? Theme.getThemedDrawable(getContext(), getBackgroundResId(), Theme.getColor(Theme.key_windowBackgroundGrayShadow, this.resourcesProvider)) : null);
            return;
        }
        if (!this.top && !this.bottom) {
            setBackgroundColor(i);
            return;
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(this.backgroundColor), Theme.getThemedDrawable(getContext(), getBackgroundResId(), Theme.getColor(Theme.key_windowBackgroundGrayShadow, this.resourcesProvider)), 0, 0);
        combinedDrawable.setFullsize(true);
        setBackground(combinedDrawable);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.size), 1073741824));
    }

    public void setTopBottom(boolean z, boolean z2) {
        if (this.top == z && this.bottom == z2) {
            return;
        }
        this.top = z;
        this.bottom = z2;
        updateBackground();
    }
}
