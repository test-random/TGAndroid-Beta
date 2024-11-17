package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.core.view.WindowInsetsCompat$$ExternalSyntheticApiModelOutline0;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;

public class DrawerLayoutContainer extends FrameLayout {
    public boolean allowDrawContent;
    private boolean allowOpenDrawer;
    private boolean allowOpenDrawerBySwipe;
    private Paint backgroundPaint;
    private boolean beginTrackingSent;
    private int behindKeyboardColor;
    private AnimatorSet currentAnimation;
    private boolean drawCurrentPreviewFragmentAbove;
    private FrameLayout drawerLayout;
    private View drawerListView;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean firstLayout;
    private boolean hasCutout;
    private int imeHeight;
    private boolean inLayout;
    private boolean keyboardVisibility;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin;
    private View navigationBar;
    private Paint navigationBarPaint;
    private INavigationLayout parentActionBarLayout;
    private BitmapDrawable previewBlurDrawable;
    private PreviewForegroundDrawable previewForegroundDrawable;
    private Rect rect;
    private float scrimOpacity;
    private Paint scrimPaint;
    private Drawable shadowLeft;
    private float startY;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;

    public static class PreviewForegroundDrawable extends Drawable {
        private final GradientDrawable bottomDrawable;
        private final GradientDrawable topDrawable;

        public PreviewForegroundDrawable() {
            GradientDrawable gradientDrawable = new GradientDrawable();
            this.topDrawable = gradientDrawable;
            gradientDrawable.setStroke(AndroidUtilities.dp(1.0f), Theme.getColor(Theme.key_actionBarDefault));
            gradientDrawable.setCornerRadius(AndroidUtilities.dp(6.0f));
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            this.bottomDrawable = gradientDrawable2;
            gradientDrawable2.setStroke(1, Theme.getColor(Theme.key_divider));
            gradientDrawable2.setCornerRadius(AndroidUtilities.dp(6.0f));
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.save();
            int i = bounds.left;
            int i2 = bounds.top;
            canvas.clipRect(i, i2, bounds.right, ActionBar.getCurrentActionBarHeight() + i2);
            this.topDrawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.clipRect(bounds.left, bounds.top + ActionBar.getCurrentActionBarHeight(), bounds.right, bounds.bottom);
            this.bottomDrawable.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getOpacity() {
            return -3;
        }

        @Override
        protected void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            this.topDrawable.setBounds(rect);
            this.bottomDrawable.setBounds(rect);
        }

        @Override
        public void setAlpha(int i) {
            this.topDrawable.setAlpha(i);
            this.bottomDrawable.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public DrawerLayoutContainer(Context context) {
        super(context);
        this.navigationBarPaint = new Paint();
        this.rect = new Rect();
        this.scrimPaint = new Paint();
        this.backgroundPaint = new Paint();
        this.allowOpenDrawerBySwipe = true;
        this.allowDrawContent = true;
        this.firstLayout = true;
        this.minDrawerMargin = (int) ((AndroidUtilities.density * 64.0f) + 0.5f);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    WindowInsets lambda$new$0;
                    lambda$new$0 = DrawerLayoutContainer.this.lambda$new$0(view, windowInsets);
                    return lambda$new$0;
                }
            });
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(R.drawable.menu_shadow);
    }

    private void applyMarginInsets(ViewGroup.MarginLayoutParams marginLayoutParams, Object obj, int i, boolean z) {
        int systemWindowInsetTop;
        int systemWindowInsetRight;
        int systemWindowInsetBottom;
        int systemWindowInsetLeft;
        int systemWindowInsetRight2;
        int systemWindowInsetBottom2;
        int systemWindowInsetLeft2;
        int systemWindowInsetTop2;
        int systemWindowInsetBottom3;
        WindowInsets m = WindowInsetsCompat$$ExternalSyntheticApiModelOutline0.m(obj);
        if (i == 3) {
            systemWindowInsetLeft2 = m.getSystemWindowInsetLeft();
            systemWindowInsetTop2 = m.getSystemWindowInsetTop();
            systemWindowInsetBottom3 = m.getSystemWindowInsetBottom();
            m = m.replaceSystemWindowInsets(systemWindowInsetLeft2, systemWindowInsetTop2, 0, systemWindowInsetBottom3);
        } else if (i == 5) {
            systemWindowInsetTop = m.getSystemWindowInsetTop();
            systemWindowInsetRight = m.getSystemWindowInsetRight();
            systemWindowInsetBottom = m.getSystemWindowInsetBottom();
            m = m.replaceSystemWindowInsets(0, systemWindowInsetTop, systemWindowInsetRight, systemWindowInsetBottom);
        }
        systemWindowInsetLeft = m.getSystemWindowInsetLeft();
        marginLayoutParams.leftMargin = systemWindowInsetLeft;
        marginLayoutParams.topMargin = z ? 0 : m.getSystemWindowInsetTop();
        systemWindowInsetRight2 = m.getSystemWindowInsetRight();
        marginLayoutParams.rightMargin = systemWindowInsetRight2;
        systemWindowInsetBottom2 = m.getSystemWindowInsetBottom();
        marginLayoutParams.bottomMargin = systemWindowInsetBottom2;
    }

    private void createBlurDrawable() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int i = (int) (measuredWidth / 6.0f);
        int i2 = (int) (measuredHeight / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(i, i2) / 180));
        BitmapDrawable bitmapDrawable = new BitmapDrawable(createBitmap);
        this.previewBlurDrawable = bitmapDrawable;
        bitmapDrawable.setBounds(0, 0, measuredWidth, measuredHeight);
    }

    private void dispatchChildInsets(View view, Object obj, int i) {
        int systemWindowInsetTop;
        int systemWindowInsetRight;
        int systemWindowInsetBottom;
        int systemWindowInsetLeft;
        int systemWindowInsetTop2;
        int systemWindowInsetBottom2;
        WindowInsets m = WindowInsetsCompat$$ExternalSyntheticApiModelOutline0.m(obj);
        if (i == 3) {
            systemWindowInsetLeft = m.getSystemWindowInsetLeft();
            systemWindowInsetTop2 = m.getSystemWindowInsetTop();
            systemWindowInsetBottom2 = m.getSystemWindowInsetBottom();
            m = m.replaceSystemWindowInsets(systemWindowInsetLeft, systemWindowInsetTop2, 0, systemWindowInsetBottom2);
        } else if (i == 5) {
            systemWindowInsetTop = m.getSystemWindowInsetTop();
            systemWindowInsetRight = m.getSystemWindowInsetRight();
            systemWindowInsetBottom = m.getSystemWindowInsetBottom();
            m = m.replaceSystemWindowInsets(0, systemWindowInsetTop, systemWindowInsetRight, systemWindowInsetBottom);
        }
        view.dispatchApplyWindowInsets(m);
    }

    private View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(this.rect);
                if (!this.rect.contains((int) f, (int) f2)) {
                    continue;
                } else {
                    if (childAt.canScrollHorizontally(-1)) {
                        return childAt;
                    }
                    if (childAt instanceof ViewGroup) {
                        Rect rect = this.rect;
                        View findScrollingChild = findScrollingChild((ViewGroup) childAt, f - rect.left, f2 - rect.top);
                        if (findScrollingChild != null) {
                            return findScrollingChild;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    private float getScrimOpacity() {
        return this.scrimOpacity;
    }

    public WindowInsets lambda$new$0(View view, WindowInsets windowInsets) {
        int systemWindowInsetTop;
        int systemWindowInsetTop2;
        int systemWindowInsetTop3;
        WindowInsets consumeSystemWindowInsets;
        WindowInsets windowInsets2;
        DisplayCutout displayCutout;
        List boundingRects;
        int ime;
        boolean isVisible;
        int ime2;
        Insets insets;
        int i;
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 30) {
            ime = WindowInsets.Type.ime();
            isVisible = windowInsets.isVisible(ime);
            ime2 = WindowInsets.Type.ime();
            insets = windowInsets.getInsets(ime2);
            i = insets.bottom;
            if (this.keyboardVisibility != isVisible || this.imeHeight != i) {
                this.keyboardVisibility = isVisible;
                this.imeHeight = i;
                requestLayout();
            }
        }
        DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) view;
        int i3 = AndroidUtilities.statusBarHeight;
        systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        if (i3 != systemWindowInsetTop) {
            drawerLayoutContainer.requestLayout();
        }
        systemWindowInsetTop2 = windowInsets.getSystemWindowInsetTop();
        if ((systemWindowInsetTop2 != 0 || AndroidUtilities.isInMultiwindow || this.firstLayout) && AndroidUtilities.statusBarHeight != systemWindowInsetTop2) {
            AndroidUtilities.statusBarHeight = systemWindowInsetTop2;
        }
        boolean z = false;
        this.firstLayout = false;
        this.lastInsets = windowInsets;
        systemWindowInsetTop3 = windowInsets.getSystemWindowInsetTop();
        drawerLayoutContainer.setWillNotDraw(systemWindowInsetTop3 <= 0 && getBackground() == null);
        if (i2 >= 28) {
            displayCutout = windowInsets.getDisplayCutout();
            if (displayCutout != null) {
                boundingRects = displayCutout.getBoundingRects();
                if (boundingRects.size() != 0) {
                    z = true;
                }
            }
            this.hasCutout = z;
        }
        invalidate();
        if (i2 >= 30) {
            windowInsets2 = WindowInsets.CONSUMED;
            return windowInsets2;
        }
        consumeSystemWindowInsets = windowInsets.consumeSystemWindowInsets();
        return consumeSystemWindowInsets;
    }

    public void onDrawerAnimationEnd(boolean z) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = z;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt != this.drawerLayout) {
                childAt.setImportantForAccessibility(z ? 4 : 0);
            }
        }
        sendAccessibilityEvent(32);
    }

    private void prepareForDrawerOpen(MotionEvent motionEvent) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        if (motionEvent != null) {
            this.startedTrackingX = (int) motionEvent.getX();
        }
        this.beginTrackingSent = false;
    }

    private void setScrimOpacity(float f) {
        this.scrimOpacity = f;
        invalidate();
    }

    public void cancelCurrentAnimation() {
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    public void closeDrawer() {
        if (this.drawerPosition != 0.0f) {
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    public void closeDrawer(boolean z) {
        if (this.drawerLayout == null) {
            return;
        }
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "drawerPosition", 0.0f));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(z ? Math.max((int) ((200.0f / this.drawerLayout.getMeasuredWidth()) * this.drawerPosition), 50) : 250L);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
            }
        });
        animatorSet.start();
    }

    public View createNavigationBar() {
        this.navigationBar = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), DrawerLayoutContainer.this.navigationBarPaint);
            }
        };
        this.navigationBarPaint.setColor(-16777216);
        return this.navigationBar;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        INavigationLayout iNavigationLayout;
        super.dispatchDraw(canvas);
        if (!this.drawCurrentPreviewFragmentAbove || (iNavigationLayout = this.parentActionBarLayout) == null) {
            return;
        }
        BitmapDrawable bitmapDrawable = this.previewBlurDrawable;
        if (bitmapDrawable != null) {
            bitmapDrawable.setAlpha((int) (iNavigationLayout.getCurrentPreviewFragmentAlpha() * 255.0f));
            this.previewBlurDrawable.draw(canvas);
        }
        this.parentActionBarLayout.drawCurrentPreviewFragment(canvas, Build.VERSION.SDK_INT >= 21 ? this.previewForegroundDrawable : null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.drawCurrentPreviewFragmentAbove || this.parentActionBarLayout == null) {
            return super.dispatchTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2) {
            float f = this.startY;
            if (f == 0.0f) {
                this.startY = motionEvent.getY();
                MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                super.dispatchTouchEvent(obtain);
                obtain.recycle();
            } else {
                this.parentActionBarLayout.movePreviewFragment(f - motionEvent.getY());
            }
        } else if (actionMasked == 1 || actionMasked == 6 || actionMasked == 3) {
            this.parentActionBarLayout.finishPreviewFragment();
        }
        return true;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        int i;
        int ceil;
        int i2 = 0;
        if (!this.allowDrawContent) {
            return false;
        }
        int height = getHeight();
        boolean z = view != this.drawerLayout;
        int width = getWidth();
        int save = canvas.save();
        if (z) {
            int childCount = getChildCount();
            i = 0;
            int i3 = 0;
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                if (childAt.getVisibility() == 0 && childAt != this.drawerLayout) {
                    i3 = i4;
                }
                if (childAt != view && childAt.getVisibility() == 0 && childAt == this.drawerLayout && childAt.getHeight() >= height && (ceil = ((int) Math.ceil(childAt.getX())) + childAt.getMeasuredWidth()) > i) {
                    i = ceil;
                }
            }
            if (i != 0) {
                canvas.clipRect(i - AndroidUtilities.dp(1.0f), 0, width, getHeight());
            }
            i2 = i3;
        } else {
            i = 0;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (this.scrimOpacity <= 0.0f || !z) {
            if (this.shadowLeft != null) {
                float max = Math.max(0.0f, Math.min(this.drawerPosition / AndroidUtilities.dp(20.0f), 1.0f));
                if (max != 0.0f) {
                    this.shadowLeft.setBounds((int) this.drawerPosition, view.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), view.getBottom());
                    this.shadowLeft.setAlpha((int) (max * 255.0f));
                    this.shadowLeft.draw(canvas);
                }
            }
        } else if (indexOfChild(view) == i2) {
            this.scrimPaint.setColor(((int) (this.scrimOpacity * 153.0f)) << 24);
            canvas.drawRect(i, 0.0f, width, getHeight(), this.scrimPaint);
        }
        return drawChild;
    }

    public FrameLayout getDrawerLayout() {
        return this.drawerLayout;
    }

    public float getDrawerPosition() {
        return this.drawerPosition;
    }

    public int getNavigationBarColor() {
        return this.navigationBarPaint.getColor();
    }

    public INavigationLayout getParentActionBarLayout() {
        return this.parentActionBarLayout;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isDrawCurrentPreviewFragmentAbove() {
        return this.drawCurrentPreviewFragmentAbove;
    }

    public boolean isDrawerOpened() {
        return this.drawerOpened;
    }

    public void moveDrawerByX(float f) {
        setDrawerPosition(this.drawerPosition + f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Object obj;
        int systemWindowInsetBottom;
        int systemWindowInsetLeft;
        int systemWindowInsetRight;
        if (Build.VERSION.SDK_INT < 21 || (obj = this.lastInsets) == null) {
            return;
        }
        WindowInsets m = WindowInsetsCompat$$ExternalSyntheticApiModelOutline0.m(obj);
        systemWindowInsetBottom = m.getSystemWindowInsetBottom();
        if (systemWindowInsetBottom > 0) {
            this.backgroundPaint.setColor(this.behindKeyboardColor);
            canvas.drawRect(0.0f, getMeasuredHeight() - systemWindowInsetBottom, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
        }
        if (this.hasCutout) {
            this.backgroundPaint.setColor(-16777216);
            systemWindowInsetLeft = m.getSystemWindowInsetLeft();
            if (systemWindowInsetLeft != 0) {
                canvas.drawRect(0.0f, 0.0f, systemWindowInsetLeft, getMeasuredHeight(), this.backgroundPaint);
            }
            systemWindowInsetRight = m.getSystemWindowInsetRight();
            if (systemWindowInsetRight != 0) {
                canvas.drawRect(systemWindowInsetRight, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int paddingTop;
        int measuredHeight;
        int i6;
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != childAt) {
                            i5 = layoutParams.leftMargin;
                            paddingTop = layoutParams.topMargin + getPaddingTop();
                            i6 = layoutParams.leftMargin + childAt.getMeasuredWidth();
                            measuredHeight = layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop();
                        } else {
                            i5 = -childAt.getMeasuredWidth();
                            paddingTop = layoutParams.topMargin + getPaddingTop();
                            measuredHeight = layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop();
                            i6 = 0;
                        }
                        childAt.layout(i5, paddingTop, i6, measuredHeight);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (this.drawerLayout != childAt) {
                    childAt.layout(layoutParams.leftMargin, layoutParams.topMargin + getPaddingTop(), layoutParams.leftMargin + childAt.getMeasuredWidth(), layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop());
                } else {
                    childAt.layout(-childAt.getMeasuredWidth(), layoutParams.topMargin + getPaddingTop(), 0, layoutParams.topMargin + childAt.getMeasuredHeight() + getPaddingTop());
                }
            }
        }
        this.inLayout = false;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        int i3 = Build.VERSION.SDK_INT;
        if (i3 < 21) {
            this.inLayout = true;
            if (size2 == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                size2 = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            this.inLayout = false;
        } else {
            int i4 = size2 - AndroidUtilities.statusBarHeight;
            if (i4 > 0 && i4 < 4096) {
                AndroidUtilities.displaySize.y = i4;
            }
        }
        boolean z = this.lastInsets != null && i3 >= 21;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                if (z) {
                    if (childAt.getFitsSystemWindows()) {
                        dispatchChildInsets(childAt, this.lastInsets, layoutParams.gravity);
                    } else if (childAt.getTag() == null) {
                        applyMarginInsets(layoutParams, this.lastInsets, layoutParams.gravity, Build.VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != childAt) {
                    int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((size - layoutParams.leftMargin) - layoutParams.rightMargin, 1073741824);
                    int i6 = layoutParams.height;
                    if (i6 <= 0) {
                        i6 = View.MeasureSpec.makeMeasureSpec((size2 - layoutParams.topMargin) - layoutParams.bottomMargin, 1073741824);
                    }
                    if ((childAt instanceof ActionBarLayout) && ((ActionBarLayout) childAt).storyViewerAttached()) {
                        childAt.forceLayout();
                    }
                    childAt.measure(makeMeasureSpec, i6);
                } else {
                    childAt.setPadding(0, 0, 0, 0);
                    childAt.measure(ViewGroup.getChildMeasureSpec(i, this.minDrawerMargin + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), ViewGroup.getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
        }
        View view = this.navigationBar;
        if (view != null) {
            if (view.getParent() == null) {
                ((FrameLayout) AndroidUtilities.findActivity(getContext()).getWindow().getDecorView()).addView(this.navigationBar);
            }
            if (this.navigationBar.getLayoutParams().height == AndroidUtilities.navigationBarHeight && ((FrameLayout.LayoutParams) this.navigationBar.getLayoutParams()).topMargin == View.MeasureSpec.getSize(i2)) {
                return;
            }
            this.navigationBar.getLayoutParams().height = AndroidUtilities.navigationBarHeight;
            ((FrameLayout.LayoutParams) this.navigationBar.getLayoutParams()).topMargin = View.MeasureSpec.getSize(i2);
            this.navigationBar.requestLayout();
        }
    }

    @Override
    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        if (!this.drawerOpened || view == this.drawerLayout) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DrawerLayoutContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void openDrawer(boolean z) {
        INavigationLayout iNavigationLayout;
        if (!this.allowOpenDrawer || this.drawerLayout == null) {
            return;
        }
        if (AndroidUtilities.isTablet() && (iNavigationLayout = this.parentActionBarLayout) != null && iNavigationLayout.getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.parentActionBarLayout.getParentActivity().getCurrentFocus());
        }
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "drawerPosition", this.drawerLayout.getMeasuredWidth()));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(z ? Math.max((int) ((200.0f / this.drawerLayout.getMeasuredWidth()) * (this.drawerLayout.getMeasuredWidth() - this.drawerPosition)), 50) : 250L);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
            }
        });
        animatorSet.start();
        this.currentAnimation = animatorSet;
    }

    public void presentFragment(BaseFragment baseFragment) {
        INavigationLayout iNavigationLayout = this.parentActionBarLayout;
        if (iNavigationLayout != null) {
            iNavigationLayout.presentFragment(baseFragment);
        }
        closeDrawer(false);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    @Override
    public void requestLayout() {
        if (this.inLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            invalidate();
        }
    }

    public void setAllowOpenDrawer(boolean z, boolean z2) {
        this.allowOpenDrawer = z;
        if (z || this.drawerPosition == 0.0f) {
            return;
        }
        if (z2) {
            closeDrawer(true);
        } else {
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    public void setAllowOpenDrawerBySwipe(boolean z) {
        this.allowOpenDrawerBySwipe = z;
    }

    public void setBehindKeyboardColor(int i) {
        this.behindKeyboardColor = i;
        invalidate();
    }

    public void setDrawCurrentPreviewFragmentAbove(boolean z) {
        PreviewForegroundDrawable previewForegroundDrawable;
        if (this.drawCurrentPreviewFragmentAbove != z) {
            this.drawCurrentPreviewFragmentAbove = z;
            if (z) {
                createBlurDrawable();
                previewForegroundDrawable = new PreviewForegroundDrawable();
            } else {
                this.startY = 0.0f;
                previewForegroundDrawable = null;
                this.previewBlurDrawable = null;
            }
            this.previewForegroundDrawable = previewForegroundDrawable;
            invalidate();
        }
    }

    public void setDrawerLayout(FrameLayout frameLayout, final View view) {
        this.drawerLayout = frameLayout;
        this.drawerListView = view;
        addView(frameLayout);
        this.drawerLayout.setVisibility(4);
        view.setVisibility(8);
        if (Build.VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                view.setVisibility(0);
            }
        }, 2500L);
    }

    public void setDrawerPosition(float f) {
        float measuredWidth;
        View view;
        if (this.drawerLayout == null) {
            return;
        }
        this.drawerPosition = f;
        if (f > r0.getMeasuredWidth()) {
            this.drawerPosition = this.drawerLayout.getMeasuredWidth();
        } else if (this.drawerPosition < 0.0f) {
            this.drawerPosition = 0.0f;
        }
        this.drawerLayout.setTranslationX(this.drawerPosition);
        if (this.drawerPosition > 0.0f && (view = this.drawerListView) != null && view.getVisibility() != 0) {
            this.drawerListView.setVisibility(0);
        }
        int i = this.drawerPosition > 0.0f ? 0 : 4;
        if (this.drawerLayout.getVisibility() != i) {
            this.drawerLayout.setVisibility(i);
        }
        if (!this.parentActionBarLayout.getFragmentStack().isEmpty()) {
            BaseFragment baseFragment = (BaseFragment) this.parentActionBarLayout.getFragmentStack().get(0);
            if (this.drawerPosition == this.drawerLayout.getMeasuredWidth()) {
                measuredWidth = 1.0f;
            } else {
                float f2 = this.drawerPosition;
                if (f2 == 0.0f) {
                    baseFragment.setProgressToDrawerOpened(0.0f);
                } else {
                    measuredWidth = f2 / this.drawerLayout.getMeasuredWidth();
                }
            }
            baseFragment.setProgressToDrawerOpened(measuredWidth);
        }
        setScrimOpacity(this.drawerPosition / this.drawerLayout.getMeasuredWidth());
    }

    public void setNavigationBarColor(int i) {
        this.navigationBarPaint.setColor(i);
        View view = this.navigationBar;
        if (view != null) {
            view.invalidate();
        }
    }

    public void setParentActionBarLayout(INavigationLayout iNavigationLayout) {
        this.parentActionBarLayout = iNavigationLayout;
    }
}
