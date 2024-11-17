package org.telegram.ui.Cells;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;

public class HeaderCell extends FrameLayout {
    private boolean animated;
    private AnimatedTextView animatedTextView;
    protected int bottomMargin;
    private int height;
    public int id;
    protected int padding;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private SimpleTextView textView2;

    public HeaderCell(Context context) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false, null);
    }

    public HeaderCell(Context context, int i) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, i, 15, false, null);
    }

    public HeaderCell(Context context, int i, int i2, int i3, int i4, boolean z, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, i3, i4, z, false, resourcesProvider);
    }

    public HeaderCell(android.content.Context r18, int r19, int r20, int r21, int r22, boolean r23, boolean r24, org.telegram.ui.ActionBar.Theme.ResourcesProvider r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.HeaderCell.<init>(android.content.Context, int, int, int, int, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public HeaderCell(Context context, int i, int i2, int i3, boolean z) {
        this(context, i, i2, i3, z, null);
    }

    public HeaderCell(Context context, int i, int i2, int i3, boolean z, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, i3, 0, z, resourcesProvider);
    }

    public HeaderCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, i, 15, false, resourcesProvider);
    }

    public HeaderCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false, resourcesProvider);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (Build.VERSION.SDK_INT >= 28) {
            accessibilityNodeInfo.setHeading(true);
        } else {
            AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo = accessibilityNodeInfo.getCollectionItemInfo();
            if (collectionItemInfo != null) {
                accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), true));
            }
        }
        accessibilityNodeInfo.setEnabled(true);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setBottomMargin(int i) {
        float f = i;
        ((FrameLayout.LayoutParams) this.textView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(f);
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            ((FrameLayout.LayoutParams) simpleTextView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(f);
        }
    }

    public void setEnabled(boolean z, ArrayList arrayList) {
        if (arrayList != null) {
            arrayList.add(ObjectAnimator.ofFloat(this.textView, (Property<TextView, Float>) View.ALPHA, z ? 1.0f : 0.5f));
        } else {
            this.textView.setAlpha(z ? 1.0f : 0.5f);
        }
    }

    public void setHeight(int i) {
        this.height = i;
        int dp = AndroidUtilities.dp(i) - ((FrameLayout.LayoutParams) this.textView.getLayoutParams()).topMargin;
        if (this.textView.getMinHeight() != dp) {
            this.textView.setMinHeight(dp);
            requestLayout();
        }
    }

    public void setText(CharSequence charSequence) {
        setText(charSequence, false);
    }

    public void setText(CharSequence charSequence, boolean z) {
        if (this.animated) {
            this.animatedTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.animatedTextView.setText(charSequence, z);
        } else {
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.textView.setText(charSequence);
        }
    }

    public void setText2(CharSequence charSequence) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView == null) {
            return;
        }
        simpleTextView.setText(charSequence);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextSize(float f) {
        if (this.animated) {
            this.animatedTextView.setTextSize(AndroidUtilities.dp(f));
        } else {
            this.textView.setTextSize(1, f);
        }
    }

    public void setTopMargin(int i) {
        ((FrameLayout.LayoutParams) this.textView.getLayoutParams()).topMargin = AndroidUtilities.dp(i);
        setHeight(this.height);
    }
}
