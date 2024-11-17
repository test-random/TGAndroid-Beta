package org.telegram.ui.Components;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;

public class AnimatedAvatarContainer extends FrameLayout {
    private int leftPadding;
    boolean occupyStatusBar;
    AnimatedTextView subtitleTextView;
    AnimatedTextView titleTextView;

    public AnimatedAvatarContainer(Context context) {
        super(context);
        this.occupyStatusBar = true;
        this.leftPadding = AndroidUtilities.dp(8.0f);
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.titleTextView = animatedTextView;
        int i = Theme.key_actionBarDefaultTitle;
        animatedTextView.setTextColor(Theme.getColor(i));
        this.titleTextView.setTextSize(AndroidUtilities.dp(18.0f));
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.bold());
        this.titleTextView.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(12.0f));
        addView(this.titleTextView);
        AnimatedTextView animatedTextView2 = new AnimatedTextView(context, true, true, true);
        this.subtitleTextView = animatedTextView2;
        animatedTextView2.setTag(Integer.valueOf(Theme.key_actionBarDefaultSubtitle));
        this.subtitleTextView.setTextColor(Theme.getColor(i));
        this.subtitleTextView.setTextSize(AndroidUtilities.dp(14.0f));
        this.subtitleTextView.setGravity(3);
        this.subtitleTextView.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        addView(this.subtitleTextView);
        this.titleTextView.getDrawable().setAllowCancel(true);
        this.subtitleTextView.getDrawable().setAllowCancel(true);
        AnimatedTextView animatedTextView3 = this.titleTextView;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        animatedTextView3.setAnimationProperties(1.0f, 0L, 150L, cubicBezierInterpolator);
        this.subtitleTextView.setAnimationProperties(1.0f, 0L, 150L, cubicBezierInterpolator);
        setClipChildren(false);
    }

    public AnimatedTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public AnimatedTextView getTitle() {
        return this.titleTextView;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
        int i5 = this.leftPadding;
        if (this.subtitleTextView.getVisibility() != 8) {
            this.titleTextView.layout(i5, (AndroidUtilities.dp(1.0f) + currentActionBarHeight) - this.titleTextView.getPaddingTop(), this.titleTextView.getMeasuredWidth() + i5, (((this.titleTextView.getTextHeight() + currentActionBarHeight) + AndroidUtilities.dp(1.3f)) - this.titleTextView.getPaddingTop()) + this.titleTextView.getPaddingBottom());
        } else {
            this.titleTextView.layout(i5, (AndroidUtilities.dp(11.0f) + currentActionBarHeight) - this.titleTextView.getPaddingTop(), this.titleTextView.getMeasuredWidth() + i5, (((this.titleTextView.getTextHeight() + currentActionBarHeight) + AndroidUtilities.dp(11.0f)) - this.titleTextView.getPaddingTop()) + this.titleTextView.getPaddingBottom());
        }
        this.subtitleTextView.layout(i5, AndroidUtilities.dp(20.0f) + currentActionBarHeight, this.subtitleTextView.getMeasuredWidth() + i5, currentActionBarHeight + this.subtitleTextView.getTextHeight() + AndroidUtilities.dp(24.0f));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i) + this.titleTextView.getPaddingRight();
        int dp = size - AndroidUtilities.dp(16.0f);
        this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f) + this.titleTextView.getPaddingRight(), Integer.MIN_VALUE));
        this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
        setMeasuredDimension(size, View.MeasureSpec.getSize(i2));
    }
}
