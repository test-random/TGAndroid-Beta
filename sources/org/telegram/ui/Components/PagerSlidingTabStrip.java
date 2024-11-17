package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme$$ExternalSyntheticApiModelOutline2;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    private int currentPosition;
    private float currentPositionOffset;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    public ViewPager.OnPageChangeListener delegatePageListener;
    private int dividerPadding;
    private int indicatorColor;
    private int indicatorHeight;
    private int lastScrollX;
    private AnimatedFloat lineLeftAnimated;
    private AnimatedFloat lineRightAnimated;
    private final PageListener pageListener;
    private ViewPager pager;
    private Paint rectPaint;
    private Theme.ResourcesProvider resourcesProvider;
    private int scrollOffset;
    private boolean shouldExpand;
    private int tabCount;
    private int tabPadding;
    private LinearLayout tabsContainer;
    private int underlineColor;
    private int underlineHeight;

    public interface IconTabProvider {
        boolean canScrollToTab(int i);

        void customOnDraw(Canvas canvas, View view, int i);

        Drawable getPageIconDrawable(int i);

        int getTabPadding(int i);
    }

    public class PageListener implements ViewPager.OnPageChangeListener {
        private PageListener() {
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            if (i == 0) {
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.scrollToChild(pagerSlidingTabStrip.pager.getCurrentItem(), 0);
            }
            ViewPager.OnPageChangeListener onPageChangeListener = PagerSlidingTabStrip.this.delegatePageListener;
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStateChanged(i);
            }
        }

        @Override
        public void onPageScrolled(int i, float f, int i2) {
            PagerSlidingTabStrip.this.currentPosition = i;
            PagerSlidingTabStrip.this.currentPositionOffset = f;
            if (PagerSlidingTabStrip.this.tabsContainer.getChildAt(i) != null) {
                PagerSlidingTabStrip.this.scrollToChild(i, (int) (r0.tabsContainer.getChildAt(i).getWidth() * f));
                PagerSlidingTabStrip.this.invalidate();
                ViewPager.OnPageChangeListener onPageChangeListener = PagerSlidingTabStrip.this.delegatePageListener;
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(i, f, i2);
                }
            }
        }

        @Override
        public void onPageSelected(int i) {
            ViewPager.OnPageChangeListener onPageChangeListener = PagerSlidingTabStrip.this.delegatePageListener;
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(i);
            }
            int i2 = 0;
            while (i2 < PagerSlidingTabStrip.this.tabsContainer.getChildCount()) {
                PagerSlidingTabStrip.this.tabsContainer.getChildAt(i2).setSelected(i2 == i);
                i2++;
            }
        }
    }

    public class TextTab extends TextView {
        final int position;

        public TextTab(Context context, int i) {
            super(context);
            this.position = i;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, this, this.position);
            }
        }

        @Override
        public void setSelected(boolean z) {
            super.setSelected(z);
            Drawable background = getBackground();
            if (Build.VERSION.SDK_INT >= 21 && background != null) {
                int themedColor = PagerSlidingTabStrip.this.getThemedColor(z ? Theme.key_chat_emojiPanelIconSelected : Theme.key_chat_emojiBottomPanelIcon);
                Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor)), true);
            }
            setTextColor(PagerSlidingTabStrip.this.getThemedColor(z ? Theme.key_chat_emojiPanelIconSelected : Theme.key_chat_emojiPanelBackspace));
        }

        public void setSelectedProgress(float f) {
            setTextColor(ColorUtils.blendARGB(PagerSlidingTabStrip.this.getThemedColor(Theme.key_chat_emojiPanelBackspace), PagerSlidingTabStrip.this.getThemedColor(Theme.key_chat_emojiPanelIconSelected), f));
        }
    }

    public PagerSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.pageListener = new PageListener();
        this.currentPosition = 0;
        this.currentPositionOffset = 0.0f;
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.shouldExpand = false;
        this.scrollOffset = AndroidUtilities.dp(52.0f);
        this.indicatorHeight = AndroidUtilities.dp(8.0f);
        this.underlineHeight = AndroidUtilities.dp(2.0f);
        this.dividerPadding = AndroidUtilities.dp(12.0f);
        this.tabPadding = AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.lineLeftAnimated = new AnimatedFloat(this, 350L, cubicBezierInterpolator);
        this.lineRightAnimated = new AnimatedFloat(this, 350L, cubicBezierInterpolator);
        this.resourcesProvider = resourcesProvider;
        setFillViewport(true);
        setWillNotDraw(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(-2, -1);
    }

    private void addIconTab(final int i, Drawable drawable, CharSequence charSequence) {
        ImageView imageView = new ImageView(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                    ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, this, i);
                }
            }

            @Override
            public void setSelected(boolean z) {
                super.setSelected(z);
                Drawable background = getBackground();
                if (Build.VERSION.SDK_INT < 21 || background == null) {
                    return;
                }
                int themedColor = PagerSlidingTabStrip.this.getThemedColor(z ? Theme.key_chat_emojiPanelIconSelected : Theme.key_chat_emojiBottomPanelIcon);
                Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor)), true);
            }
        };
        imageView.setFocusable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable m = Theme$$ExternalSyntheticApiModelOutline2.m(Theme.createSelectorDrawable(getThemedColor(Theme.key_chat_emojiBottomPanelIcon), 1, AndroidUtilities.dp(18.0f)));
            Theme.setRippleDrawableForceSoftware(m);
            imageView.setBackground(m);
        }
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PagerSlidingTabStrip.this.lambda$addIconTab$0(i, view);
            }
        });
        this.tabsContainer.addView(imageView);
        imageView.setSelected(i == this.currentPosition);
        imageView.setContentDescription(charSequence);
    }

    private void addTab(final int i, CharSequence charSequence) {
        TextTab textTab = new TextTab(getContext(), i);
        textTab.setTextSize(1, 14.0f);
        textTab.setTypeface(AndroidUtilities.bold());
        textTab.setTextColor(getThemedColor(Theme.key_chat_emojiPanelBackspace));
        textTab.setFocusable(true);
        textTab.setGravity(17);
        textTab.setText(charSequence);
        textTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PagerSlidingTabStrip.this.lambda$addTab$1(i, view);
            }
        });
        textTab.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.tabsContainer.addView(textTab, LayoutHelper.createLinear(-2, -2, 10.0f, 0.0f, 10.0f, 0.0f));
        textTab.setSelected(i == this.currentPosition);
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$addIconTab$0(int i, View view) {
        if (!(this.pager.getAdapter() instanceof IconTabProvider) || ((IconTabProvider) this.pager.getAdapter()).canScrollToTab(i)) {
            this.pager.setCurrentItem(i, false);
        }
    }

    public void lambda$addTab$1(int i, View view) {
        if (!(this.pager.getAdapter() instanceof IconTabProvider) || ((IconTabProvider) this.pager.getAdapter()).canScrollToTab(i)) {
            this.pager.setCurrentItem(i, false);
        }
    }

    public void scrollToChild(int i, int i2) {
        View childAt;
        if (this.tabCount == 0 || (childAt = this.tabsContainer.getChildAt(i)) == null) {
            return;
        }
        int left = childAt.getLeft() + i2;
        if (i > 0 || i2 > 0) {
            left -= this.scrollOffset;
        }
        if (left != this.lastScrollX) {
            this.lastScrollX = left;
            scrollTo(left, 0);
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View childAt = this.tabsContainer.getChildAt(i);
            childAt.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                childAt.setPadding(0, 0, 0, 0);
                childAt.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0f));
            } else {
                int tabPadding = this.pager.getAdapter() instanceof IconTabProvider ? ((IconTabProvider) this.pager.getAdapter()).getTabPadding(i) : this.tabPadding;
                childAt.setPadding(tabPadding, 0, tabPadding, 0);
            }
        }
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public View getTab(int i) {
        if (i < 0 || i >= this.tabsContainer.getChildCount()) {
            return null;
        }
        return this.tabsContainer.getChildAt(i);
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void notifyDataSetChanged() {
        Drawable pageIconDrawable;
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (!(this.pager.getAdapter() instanceof IconTabProvider) || (pageIconDrawable = ((IconTabProvider) this.pager.getAdapter()).getPageIconDrawable(i)) == null) {
                addTab(i, this.pager.getAdapter().getPageTitle(i));
            } else {
                addIconTab(i, pageIconDrawable, this.pager.getAdapter().getPageTitle(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.currentPosition = pagerSlidingTabStrip.pager.getCurrentItem();
                PagerSlidingTabStrip pagerSlidingTabStrip2 = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip2.scrollToChild(pagerSlidingTabStrip2.currentPosition, 0);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float f;
        float f2;
        int i;
        if (isInEditMode() || this.tabCount == 0) {
            super.onDraw(canvas);
            return;
        }
        int height = getHeight();
        if (this.underlineHeight != 0) {
            this.rectPaint.setColor(this.underlineColor);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, height - this.underlineHeight, this.tabsContainer.getWidth(), height);
            float f3 = this.underlineHeight / 2.0f;
            canvas.drawRoundRect(rectF, f3, f3, this.rectPaint);
        }
        View childAt = this.tabsContainer.getChildAt(this.currentPosition);
        if (childAt != null) {
            float left = childAt.getLeft() + childAt.getPaddingLeft();
            float right = childAt.getRight() - childAt.getPaddingRight();
            if (this.currentPositionOffset <= 0.0f || (i = this.currentPosition) >= this.tabCount - 1) {
                f = this.lineLeftAnimated.set(left);
                f2 = this.lineRightAnimated.set(right);
            } else {
                View childAt2 = this.tabsContainer.getChildAt(i + 1);
                float left2 = childAt2.getLeft() + childAt2.getPaddingLeft();
                float right2 = childAt2.getRight() - childAt2.getPaddingRight();
                float f4 = this.currentPositionOffset;
                float f5 = 1.0f - f4;
                f = (left2 * f4) + (left * f5);
                f2 = (f4 * right2) + (f5 * right);
                this.lineLeftAnimated.set(f, true);
                this.lineRightAnimated.set(f2, true);
                if (childAt instanceof TextTab) {
                    ((TextTab) childAt).setSelectedProgress(1.0f - this.currentPositionOffset);
                }
                if (childAt2 instanceof TextTab) {
                    ((TextTab) childAt2).setSelectedProgress(this.currentPositionOffset);
                }
            }
            if (this.indicatorHeight != 0) {
                this.rectPaint.setColor(this.indicatorColor);
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set(f - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f), f2 + AndroidUtilities.dp(12.0f), height - AndroidUtilities.dp(6.0f));
                canvas.drawRoundRect(rectF2, rectF2.height() / 2.0f, rectF2.height() / 2.0f, this.rectPaint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (!this.shouldExpand || View.MeasureSpec.getMode(i) == 0) {
            return;
        }
        this.tabsContainer.measure(getMeasuredWidth() | 1073741824, i2);
    }

    @Override
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.shouldExpand) {
            return;
        }
        post(new Runnable() {
            @Override
            public final void run() {
                PagerSlidingTabStrip.this.notifyDataSetChanged();
            }
        });
    }

    public void setDividerPadding(int i) {
        this.dividerPadding = i;
        invalidate();
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setIndicatorColorResource(int i) {
        this.indicatorColor = getResources().getColor(i);
        invalidate();
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.delegatePageListener = onPageChangeListener;
    }

    public void setScrollOffset(int i) {
        this.scrollOffset = i;
        invalidate();
    }

    public void setShouldExpand(boolean z) {
        this.shouldExpand = z;
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        updateTabStyles();
        requestLayout();
    }

    public void setTabPaddingLeftRight(int i) {
        this.tabPadding = i;
        updateTabStyles();
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.underlineColor = getResources().getColor(i);
        invalidate();
    }

    public void setUnderlineHeight(int i) {
        this.underlineHeight = i;
        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {
        this.pager = viewPager;
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        viewPager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }
}
