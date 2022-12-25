package org.telegram.p009ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.Components.RecyclerListView;
import org.telegram.p009ui.Components.ViewPagerFixed;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFiltersOrder;

public class ViewPagerFixed extends FrameLayout {
    private static final Interpolator interpolator = ViewPagerFixed$$ExternalSyntheticLambda2.INSTANCE;
    private Adapter adapter;
    private float additionalOffset;
    private boolean allowDisallowInterceptTouch;
    private boolean animatingForward;
    private boolean backAnimation;
    private float backProgress;
    int currentPosition;
    private int maximumVelocity;
    private boolean maybeStartTracking;
    int nextPosition;
    private Rect rect;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    TabsView tabsView;
    private final float touchSlop;
    ValueAnimator.AnimatorUpdateListener updateTabProgress;
    private VelocityTracker velocityTracker;
    protected View[] viewPages;
    private int[] viewTypes;
    protected SparseArray<View> viewsByType;

    public static abstract class Adapter {
        public abstract void bindView(View view, int i, int i2);

        public abstract View createView(int i);

        public abstract int getItemCount();

        public int getItemId(int i) {
            return i;
        }

        public abstract String getItemTitle(int i);

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean hasStableId() {
            return false;
        }
    }

    public static float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    protected void invalidateBlur() {
    }

    protected void onBack() {
    }

    protected boolean onBackProgress(float f) {
        return false;
    }

    protected void onItemSelected(View view, View view2, int i, int i2) {
    }

    protected void onTabPageSelected(int i) {
    }

    protected int tabMarginDp() {
        return 16;
    }

    public ViewPagerFixed(Context context) {
        this(context, null);
    }

    public ViewPagerFixed(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.viewsByType = new SparseArray<>();
        this.updateTabProgress = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (ViewPagerFixed.this.tabsAnimationInProgress) {
                    float abs = Math.abs(ViewPagerFixed.this.viewPages[0].getTranslationX()) / ViewPagerFixed.this.viewPages[0].getMeasuredWidth();
                    ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                    TabsView tabsView = viewPagerFixed.tabsView;
                    if (tabsView != null) {
                        tabsView.selectTab(viewPagerFixed.nextPosition, viewPagerFixed.currentPosition, 1.0f - abs);
                    }
                }
            }
        };
        this.rect = new Rect();
        this.allowDisallowInterceptTouch = true;
        this.resourcesProvider = resourcesProvider;
        this.touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.viewTypes = new int[2];
        this.viewPages = new View[2];
        setClipChildren(true);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        this.viewTypes[0] = adapter.getItemViewType(this.currentPosition);
        this.viewPages[0] = adapter.createView(this.viewTypes[0]);
        adapter.bindView(this.viewPages[0], this.currentPosition, this.viewTypes[0]);
        addView(this.viewPages[0]);
        this.viewPages[0].setVisibility(0);
        fillTabs(false);
    }

    public TabsView createTabsView(boolean z) {
        TabsView tabsView = new TabsView(getContext(), z, this.resourcesProvider) {
            @Override
            public void selectTab(int i, int i2, float f) {
                super.selectTab(i, i2, f);
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                if (f > 0.5f) {
                    i = i2;
                }
                viewPagerFixed.onTabPageSelected(i);
            }
        };
        this.tabsView = tabsView;
        tabsView.tabMarginDp = tabMarginDp();
        this.tabsView.setDelegate(new TabsView.TabsViewDelegate() {
            @Override
            public void onSamePageSelected() {
            }

            @Override
            public void onPageSelected(int i, boolean z2) {
                ViewPagerFixed.this.animatingForward = z2;
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                viewPagerFixed.nextPosition = i;
                viewPagerFixed.updateViewForIndex(1);
                ViewPagerFixed.this.onTabPageSelected(i);
                if (z2) {
                    View[] viewArr = ViewPagerFixed.this.viewPages;
                    viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth());
                    return;
                }
                View[] viewArr2 = ViewPagerFixed.this.viewPages;
                viewArr2[1].setTranslationX(-viewArr2[0].getMeasuredWidth());
            }

            @Override
            public void onPageScrolled(float f) {
                if (f == 1.0f) {
                    ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                    if (viewPagerFixed.viewPages[1] != null) {
                        viewPagerFixed.swapViews();
                        ViewPagerFixed viewPagerFixed2 = ViewPagerFixed.this;
                        viewPagerFixed2.viewsByType.put(viewPagerFixed2.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                        ViewPagerFixed viewPagerFixed3 = ViewPagerFixed.this;
                        viewPagerFixed3.removeView(viewPagerFixed3.viewPages[1]);
                        ViewPagerFixed.this.viewPages[0].setTranslationX(0.0f);
                        ViewPagerFixed.this.viewPages[1] = null;
                        return;
                    }
                    return;
                }
                ViewPagerFixed viewPagerFixed4 = ViewPagerFixed.this;
                if (viewPagerFixed4.viewPages[1] == null) {
                    return;
                }
                if (viewPagerFixed4.animatingForward) {
                    View[] viewArr = ViewPagerFixed.this.viewPages;
                    viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth() * (1.0f - f));
                    View[] viewArr2 = ViewPagerFixed.this.viewPages;
                    viewArr2[0].setTranslationX((-viewArr2[0].getMeasuredWidth()) * f);
                    return;
                }
                View[] viewArr3 = ViewPagerFixed.this.viewPages;
                viewArr3[1].setTranslationX((-viewArr3[0].getMeasuredWidth()) * (1.0f - f));
                View[] viewArr4 = ViewPagerFixed.this.viewPages;
                viewArr4[0].setTranslationX(viewArr4[0].getMeasuredWidth() * f);
            }

            @Override
            public boolean canPerformActions() {
                return (ViewPagerFixed.this.tabsAnimationInProgress || ViewPagerFixed.this.startedTracking) ? false : true;
            }

            @Override
            public void invalidateBlur() {
                ViewPagerFixed.this.invalidateBlur();
            }
        });
        fillTabs(false);
        return this.tabsView;
    }

    public void updateViewForIndex(int i) {
        int i2 = i == 0 ? this.currentPosition : this.nextPosition;
        if (i2 < 0 || i2 >= this.adapter.getItemCount()) {
            return;
        }
        if (this.viewPages[i] == null) {
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view = this.viewsByType.get(this.viewTypes[i]);
            if (view == null) {
                view = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            addView(view);
            View[] viewArr = this.viewPages;
            viewArr[i] = view;
            this.adapter.bindView(viewArr[i], i2, this.viewTypes[i]);
            this.viewPages[i].setVisibility(0);
        } else if (this.viewTypes[i] == this.adapter.getItemViewType(i2)) {
            this.adapter.bindView(this.viewPages[i], i2, this.viewTypes[i]);
            this.viewPages[i].setVisibility(0);
        } else {
            this.viewsByType.put(this.viewTypes[i], this.viewPages[i]);
            this.viewPages[i].setVisibility(8);
            removeView(this.viewPages[i]);
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view2 = this.viewsByType.get(this.viewTypes[i]);
            if (view2 == null) {
                view2 = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            addView(view2);
            View[] viewArr2 = this.viewPages;
            viewArr2[i] = view2;
            viewArr2[i].setVisibility(0);
            Adapter adapter = this.adapter;
            adapter.bindView(this.viewPages[i], i2, adapter.getItemViewType(i2));
        }
    }

    public void fillTabs(boolean z) {
        TabsView tabsView;
        if (this.adapter == null || (tabsView = this.tabsView) == null) {
            return;
        }
        tabsView.removeTabs();
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            this.tabsView.addTab(this.adapter.getItemId(i), this.adapter.getItemTitle(i));
        }
        if (z) {
            TransitionManager.beginDelayedTransition(this.tabsView.listView, TransitionExt.createSimpleTransition());
        }
        this.tabsView.finishAddingTabs();
    }

    private boolean prepareForMoving(android.view.MotionEvent r5, boolean r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.ViewPagerFixed.prepareForMoving(android.view.MotionEvent, boolean):boolean");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        TabsView tabsView = this.tabsView;
        if (tabsView == null || !tabsView.isAnimatingIndicator()) {
            if (checkTabsAnimationInProgress()) {
                return true;
            }
            onTouchEvent(motionEvent);
            return this.startedTracking;
        }
        return false;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.allowDisallowInterceptTouch && this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.ViewPagerFixed.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void lambda$onTouchEvent$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.backProgress = floatValue;
        onBackProgress(floatValue);
    }

    public void swapViews() {
        View[] viewArr = this.viewPages;
        View view = viewArr[0];
        viewArr[0] = viewArr[1];
        viewArr[1] = view;
        int i = this.currentPosition;
        int i2 = this.nextPosition;
        this.currentPosition = i2;
        this.nextPosition = i;
        int[] iArr = this.viewTypes;
        int i3 = iArr[0];
        iArr[0] = iArr[1];
        iArr[1] = i3;
        onItemSelected(viewArr[0], viewArr[1], i2, i);
    }

    public boolean checkTabsAnimationInProgress() {
        if (this.tabsAnimationInProgress) {
            boolean z = true;
            if (this.backAnimation) {
                if (Math.abs(this.viewPages[0].getTranslationX()) < 1.0f) {
                    this.viewPages[0].setTranslationX(0.0f);
                    View[] viewArr = this.viewPages;
                    if (viewArr[1] != null) {
                        viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth() * (this.animatingForward ? 1 : -1));
                    }
                }
                z = false;
            } else {
                if (Math.abs(this.viewPages[1].getTranslationX()) < 1.0f) {
                    View[] viewArr2 = this.viewPages;
                    viewArr2[0].setTranslationX(viewArr2[0].getMeasuredWidth() * (this.animatingForward ? -1 : 1));
                    View[] viewArr3 = this.viewPages;
                    if (viewArr3[1] != null) {
                        viewArr3[1].setTranslationX(0.0f);
                    }
                }
                z = false;
            }
            if (z) {
                AnimatorSet animatorSet = this.tabsAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.tabsAnimation = null;
                }
                this.tabsAnimationInProgress = false;
            }
            return this.tabsAnimationInProgress;
        }
        return false;
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((f - 0.5f) * 0.47123894f);
    }

    public void setPosition(int i) {
        AnimatorSet animatorSet = this.tabsAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        View[] viewArr = this.viewPages;
        if (viewArr[1] != null) {
            this.viewsByType.put(this.viewTypes[1], viewArr[1]);
            removeView(this.viewPages[1]);
            this.viewPages[1] = null;
        }
        int i2 = this.currentPosition;
        if (i2 != i) {
            this.currentPosition = i;
            View view = this.viewPages[0];
            updateViewForIndex(0);
            onItemSelected(this.viewPages[0], view, this.currentPosition, i2);
            this.viewPages[0].setTranslationX(0.0f);
            TabsView tabsView = this.tabsView;
            if (tabsView != null) {
                tabsView.selectTab(i, 0, 1.0f);
            }
        }
    }

    public View[] getViewPages() {
        return this.viewPages;
    }

    public boolean isCurrentTabFirst() {
        return this.currentPosition == 0;
    }

    public void rebuild(boolean z) {
        onTouchEvent(null);
        if (!this.adapter.hasStableId()) {
            z = false;
        }
        AnimatorSet animatorSet = this.tabsAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.tabsAnimation = null;
        }
        View[] viewArr = this.viewPages;
        if (viewArr[1] != null) {
            removeView(viewArr[1]);
            this.viewPages[1] = null;
        }
        View[] viewArr2 = this.viewPages;
        viewArr2[1] = viewArr2[0];
        int intValue = (viewArr2[1] == null || viewArr2[1].getTag() == null) ? 0 : ((Integer) this.viewPages[1].getTag()).intValue();
        if (this.adapter.getItemCount() == 0) {
            View[] viewArr3 = this.viewPages;
            if (viewArr3[1] != null) {
                removeView(viewArr3[1]);
                this.viewPages[1] = null;
            }
            View[] viewArr4 = this.viewPages;
            if (viewArr4[0] != null) {
                removeView(viewArr4[0]);
                this.viewPages[0] = null;
                return;
            }
            return;
        }
        if (this.currentPosition > this.adapter.getItemCount() - 1) {
            this.currentPosition = this.adapter.getItemCount() - 1;
        }
        if (this.currentPosition < 0) {
            this.currentPosition = 0;
        }
        this.viewTypes[0] = this.adapter.getItemViewType(this.currentPosition);
        this.viewPages[0] = this.adapter.createView(this.viewTypes[0]);
        this.adapter.bindView(this.viewPages[0], this.currentPosition, this.viewTypes[0]);
        addView(this.viewPages[0]);
        this.viewPages[0].setVisibility(0);
        if ((this.viewPages[0].getTag() == null ? 0 : ((Integer) this.viewPages[0].getTag()).intValue()) == intValue) {
            z = false;
        }
        if (z) {
            this.tabsView.saveFromValues();
        }
        fillTabs(z);
        if (z) {
            this.tabsAnimation = new AnimatorSet();
            this.viewPages[1].setTranslationX(0.0f);
            this.viewPages[0].setTranslationX(-getMeasuredWidth());
            View[] viewArr5 = this.viewPages;
            if (viewArr5[1] != null) {
                this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(viewArr5[1], View.TRANSLATION_X, getMeasuredWidth()));
            }
            this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, 0.0f));
            this.tabsView.indicatorProgress2 = 0.0f;
            this.tabsView.listView.invalidateViews();
            this.tabsView.invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewPagerFixed.this.lambda$rebuild$2(valueAnimator);
                }
            });
            this.tabsAnimation.playTogether(ofFloat);
            this.tabsAnimation.setInterpolator(interpolator);
            this.tabsAnimation.setDuration(220L);
            this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ViewPagerFixed.this.tabsAnimation = null;
                    ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                    View[] viewArr6 = viewPagerFixed.viewPages;
                    if (viewArr6[1] != null) {
                        viewPagerFixed.removeView(viewArr6[1]);
                        ViewPagerFixed.this.viewPages[1] = null;
                    }
                    ViewPagerFixed.this.tabsAnimationInProgress = false;
                    TabsView tabsView = ViewPagerFixed.this.tabsView;
                    if (tabsView != null) {
                        tabsView.setEnabled(true);
                        ViewPagerFixed.this.tabsView.animatingIndicator = false;
                        ViewPagerFixed.this.tabsView.indicatorProgress2 = 1.0f;
                        ViewPagerFixed.this.tabsView.listView.invalidateViews();
                        ViewPagerFixed.this.tabsView.invalidate();
                    }
                }
            });
            this.tabsView.setEnabled(false);
            this.tabsAnimationInProgress = true;
            this.tabsAnimation.start();
            return;
        }
        View[] viewArr6 = this.viewPages;
        if (viewArr6[1] != null) {
            removeView(viewArr6[1]);
            this.viewPages[1] = null;
        }
    }

    public void lambda$rebuild$2(ValueAnimator valueAnimator) {
        this.updateTabProgress.onAnimationUpdate(valueAnimator);
        this.tabsView.indicatorProgress2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.tabsView.listView.invalidateViews();
        this.tabsView.invalidate();
    }

    @Override
    public boolean canScrollHorizontally(int i) {
        if (i == 0) {
            return false;
        }
        if (!this.tabsAnimationInProgress && !this.startedTracking) {
            boolean z = i > 0;
            if ((!z && this.currentPosition == 0) || (z && this.currentPosition == this.adapter.getItemCount() - 1)) {
                return false;
            }
        }
        return true;
    }

    public View getCurrentView() {
        return this.viewPages[0];
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public static class TabsView extends FrameLayout {
        private String activeTextColorKey;
        private ListAdapter adapter;
        private int additionalTabWidth;
        private int allTabsWidth;
        private boolean animatingIndicator;
        private float animatingIndicatorProgress;
        private Runnable animationRunnable;
        private float animationTime;
        private String backgroundColorKey;
        private Paint counterPaint;
        private float crossfadeAlpha;
        private Bitmap crossfadeBitmap;
        private Paint crossfadePaint;
        private int currentPosition;
        private TabsViewDelegate delegate;
        private Paint deletePaint;
        private float editingAnimationProgress;
        private float editingStartAnimationProgress;
        private float hideProgress;
        private SparseIntArray idToPosition;
        private boolean ignoreLayout;
        private float indicatorProgress2;
        private CubicBezierInterpolator interpolator;
        private boolean isEditing;
        private boolean isInHiddenMode;
        private long lastAnimationTime;
        float lastDrawnIndicatorW;
        float lastDrawnIndicatorX;
        private LinearLayoutManager layoutManager;
        private RecyclerListView listView;
        private int manualScrollingToId;
        private int manualScrollingToPosition;
        private boolean orderChanged;
        private SparseIntArray positionToId;
        private SparseIntArray positionToWidth;
        private SparseIntArray positionToX;
        private int prevLayoutWidth;
        private int previousId;
        private int previousPosition;
        private Theme.ResourcesProvider resourcesProvider;
        private int scrollingToChild;
        private int selectedTabId;
        private String selectorColorKey;
        private GradientDrawable selectorDrawable;
        private String tabLineColorKey;
        public int tabMarginDp;
        private ArrayList<Tab> tabs;
        ValueAnimator tabsAnimator;
        private TextPaint textCounterPaint;
        private TextPaint textPaint;
        private String unactiveTextColorKey;

        public interface TabsViewDelegate {
            boolean canPerformActions();

            void invalidateBlur();

            void onPageScrolled(float f);

            void onPageSelected(int i, boolean z);

            void onSamePageSelected();
        }

        public static void lambda$setIsEditing$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        public void saveFromValues() {
        }

        static float access$3116(TabsView tabsView, float f) {
            float f2 = tabsView.animationTime + f;
            tabsView.animationTime = f2;
            return f2;
        }

        public static class Tab {
            public int counter;
            public int f1158id;
            public String title;
            public int titleWidth;

            public Tab(int i, String str) {
                this.f1158id = i;
                this.title = str;
            }

            public int getWidth(boolean z, TextPaint textPaint) {
                int ceil = (int) Math.ceil(textPaint.measureText(this.title));
                this.titleWidth = ceil;
                return Math.max(AndroidUtilities.m35dp(40.0f), ceil);
            }
        }

        public class TabView extends View {
            private int currentPosition;
            private Tab currentTab;
            private String currentText;
            private RectF rect;
            private int tabWidth;
            private int textHeight;
            private StaticLayout textLayout;
            private int textOffsetX;

            public TabView(Context context) {
                super(context);
                this.rect = new RectF();
            }

            public void setTab(Tab tab, int i) {
                this.currentTab = tab;
                this.currentPosition = i;
                setContentDescription(tab.title);
                requestLayout();
            }

            @Override
            public int getId() {
                return this.currentTab.f1158id;
            }

            @Override
            protected void onMeasure(int i, int i2) {
                setMeasuredDimension(this.currentTab.getWidth(false, TabsView.this.textPaint) + AndroidUtilities.m35dp(TabsView.this.tabMarginDp * 2) + TabsView.this.additionalTabWidth, View.MeasureSpec.getSize(i2));
            }

            @Override
            @SuppressLint({"DrawAllocation"})
            protected void onDraw(Canvas canvas) {
                int i;
                int i2;
                String str;
                String str2;
                int i3;
                String str3;
                int i4;
                int i5;
                int i6;
                int i7;
                if (this.currentTab.f1158id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.save();
                    float f = TabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                    canvas.translate(AndroidUtilities.m35dp(0.66f) * f, 0.0f);
                    canvas.rotate(f, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                }
                if (TabsView.this.manualScrollingToId != -1) {
                    i = TabsView.this.manualScrollingToId;
                    i2 = TabsView.this.selectedTabId;
                } else {
                    i = TabsView.this.selectedTabId;
                    i2 = TabsView.this.previousId;
                }
                String str4 = "chats_tabUnreadActiveBackground";
                String str5 = "chats_tabUnreadUnactiveBackground";
                if (this.currentTab.f1158id == i) {
                    str = TabsView.this.activeTextColorKey;
                    str2 = TabsView.this.unactiveTextColorKey;
                } else {
                    str = TabsView.this.unactiveTextColorKey;
                    str2 = TabsView.this.activeTextColorKey;
                    str5 = "chats_tabUnreadActiveBackground";
                    str4 = "chats_tabUnreadUnactiveBackground";
                }
                if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToId != -1) && ((i3 = this.currentTab.f1158id) == i || i3 == i2)) {
                    TabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str2, TabsView.this.resourcesProvider), Theme.getColor(str, TabsView.this.resourcesProvider), TabsView.this.animatingIndicatorProgress));
                } else {
                    TabsView.this.textPaint.setColor(Theme.getColor(str, TabsView.this.resourcesProvider));
                }
                int i8 = this.currentTab.counter;
                if (i8 > 0) {
                    str3 = String.format("%d", Integer.valueOf(i8));
                    i4 = (int) Math.ceil(TabsView.this.textCounterPaint.measureText(str3));
                    i5 = Math.max(AndroidUtilities.m35dp(10.0f), i4) + AndroidUtilities.m35dp(10.0f);
                } else {
                    str3 = null;
                    i4 = 0;
                    i5 = 0;
                }
                if (this.currentTab.f1158id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                    i5 = (int) (i5 + ((AndroidUtilities.m35dp(20.0f) - i5) * TabsView.this.editingStartAnimationProgress));
                }
                int i9 = this.currentTab.titleWidth;
                if (i5 != 0) {
                    i6 = AndroidUtilities.m35dp((str3 != null ? 1.0f : TabsView.this.editingStartAnimationProgress) * 6.0f) + i5;
                } else {
                    i6 = 0;
                }
                this.tabWidth = i9 + i6;
                int measuredWidth = (getMeasuredWidth() - this.tabWidth) / 2;
                if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                    String str6 = this.currentTab.title;
                    this.currentText = str6;
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(str6, TabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.m35dp(15.0f), false), TabsView.this.textPaint, AndroidUtilities.m35dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.textLayout = staticLayout;
                    this.textHeight = staticLayout.getHeight();
                    this.textOffsetX = (int) (-this.textLayout.getLineLeft(0));
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate(this.textOffsetX + measuredWidth, ((getMeasuredHeight() - this.textHeight) / 2) + 1);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (str3 != null || (this.currentTab.f1158id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f))) {
                    TabsView.this.textCounterPaint.setColor(Theme.getColor(TabsView.this.backgroundColorKey, TabsView.this.resourcesProvider));
                    if (!Theme.hasThemeKey(str4) || !Theme.hasThemeKey(str5)) {
                        TabsView.this.counterPaint.setColor(TabsView.this.textPaint.getColor());
                    } else {
                        int color = Theme.getColor(str4, TabsView.this.resourcesProvider);
                        if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToPosition != -1) && ((i7 = this.currentTab.f1158id) == i || i7 == i2)) {
                            TabsView.this.counterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str5, TabsView.this.resourcesProvider), color, TabsView.this.animatingIndicatorProgress));
                        } else {
                            TabsView.this.counterPaint.setColor(color);
                        }
                    }
                    int m35dp = measuredWidth + this.currentTab.titleWidth + AndroidUtilities.m35dp(6.0f);
                    int measuredHeight = (getMeasuredHeight() - AndroidUtilities.m35dp(20.0f)) / 2;
                    if (this.currentTab.f1158id == Integer.MAX_VALUE || ((!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f) || str3 != null)) {
                        TabsView.this.counterPaint.setAlpha(255);
                    } else {
                        TabsView.this.counterPaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                    }
                    this.rect.set(m35dp, measuredHeight, m35dp + i5, AndroidUtilities.m35dp(20.0f) + measuredHeight);
                    RectF rectF = this.rect;
                    float f2 = AndroidUtilities.density;
                    canvas.drawRoundRect(rectF, f2 * 11.5f, f2 * 11.5f, TabsView.this.counterPaint);
                    if (str3 != null) {
                        if (this.currentTab.f1158id != Integer.MAX_VALUE) {
                            TabsView.this.textCounterPaint.setAlpha((int) ((1.0f - TabsView.this.editingStartAnimationProgress) * 255.0f));
                        }
                        RectF rectF2 = this.rect;
                        canvas.drawText(str3, rectF2.left + ((rectF2.width() - i4) / 2.0f), measuredHeight + AndroidUtilities.m35dp(14.5f), TabsView.this.textCounterPaint);
                    }
                    if (this.currentTab.f1158id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                        TabsView.this.deletePaint.setColor(TabsView.this.textCounterPaint.getColor());
                        TabsView.this.deletePaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                        float m35dp2 = AndroidUtilities.m35dp(3.0f);
                        canvas.drawLine(this.rect.centerX() - m35dp2, this.rect.centerY() - m35dp2, this.rect.centerX() + m35dp2, this.rect.centerY() + m35dp2, TabsView.this.deletePaint);
                        canvas.drawLine(this.rect.centerX() - m35dp2, this.rect.centerY() + m35dp2, this.rect.centerX() + m35dp2, this.rect.centerY() - m35dp2, TabsView.this.deletePaint);
                    }
                }
                if (this.currentTab.f1158id == Integer.MAX_VALUE || TabsView.this.editingAnimationProgress == 0.0f) {
                    return;
                }
                canvas.restore();
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setSelected((this.currentTab == null || TabsView.this.selectedTabId == -1 || this.currentTab.f1158id != TabsView.this.selectedTabId) ? false : true);
            }
        }

        public TabsView(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.indicatorProgress2 = 1.0f;
            this.textPaint = new TextPaint(1);
            this.textCounterPaint = new TextPaint(1);
            this.deletePaint = new TextPaint(1);
            this.counterPaint = new Paint(1);
            this.tabs = new ArrayList<>();
            this.crossfadePaint = new Paint();
            this.tabMarginDp = 16;
            this.selectedTabId = -1;
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
            this.scrollingToChild = -1;
            this.tabLineColorKey = "profile_tabSelectedLine";
            this.activeTextColorKey = "profile_tabSelectedText";
            this.unactiveTextColorKey = "profile_tabText";
            this.selectorColorKey = "profile_tabSelector";
            this.backgroundColorKey = "actionBarDefault";
            this.interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.positionToId = new SparseIntArray(5);
            this.idToPosition = new SparseIntArray(5);
            this.positionToWidth = new SparseIntArray(5);
            this.positionToX = new SparseIntArray(5);
            this.animationRunnable = new Runnable() {
                @Override
                public void run() {
                    if (TabsView.this.animatingIndicator) {
                        long elapsedRealtime = SystemClock.elapsedRealtime() - TabsView.this.lastAnimationTime;
                        if (elapsedRealtime > 17) {
                            elapsedRealtime = 17;
                        }
                        TabsView.access$3116(TabsView.this, ((float) elapsedRealtime) / 200.0f);
                        TabsView tabsView = TabsView.this;
                        tabsView.setAnimationIdicatorProgress(tabsView.interpolator.getInterpolation(TabsView.this.animationTime));
                        if (TabsView.this.animationTime > 1.0f) {
                            TabsView.this.animationTime = 1.0f;
                        }
                        if (TabsView.this.animationTime < 1.0f) {
                            AndroidUtilities.runOnUIThread(TabsView.this.animationRunnable);
                            return;
                        }
                        TabsView.this.animatingIndicator = false;
                        TabsView.this.setEnabled(true);
                        if (TabsView.this.delegate != null) {
                            TabsView.this.delegate.onPageScrolled(1.0f);
                        }
                    }
                }
            };
            this.resourcesProvider = resourcesProvider;
            this.textCounterPaint.setTextSize(AndroidUtilities.m35dp(13.0f));
            this.textCounterPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textPaint.setTextSize(AndroidUtilities.m35dp(15.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.deletePaint.setStyle(Paint.Style.STROKE);
            this.deletePaint.setStrokeCap(Paint.Cap.ROUND);
            this.deletePaint.setStrokeWidth(AndroidUtilities.m35dp(1.5f));
            this.selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null);
            float dpf2 = AndroidUtilities.dpf2(3.0f);
            this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey, resourcesProvider));
            setHorizontalScrollBarEnabled(false);
            RecyclerListView recyclerListView = new RecyclerListView(context) {
                @Override
                public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
                    super.addView(view, i, layoutParams);
                    if (TabsView.this.isInHiddenMode) {
                        view.setScaleX(0.3f);
                        view.setScaleY(0.3f);
                        view.setAlpha(0.0f);
                        return;
                    }
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    view.setAlpha(1.0f);
                }

                @Override
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    TabsView.this.invalidate();
                }

                @Override
                public boolean canHighlightChildAt(View view, float f, float f2) {
                    if (TabsView.this.isEditing) {
                        TabView tabView = (TabView) view;
                        float m35dp = AndroidUtilities.m35dp(6.0f);
                        if (tabView.rect.left - m35dp < f && tabView.rect.right + m35dp > f) {
                            return false;
                        }
                    }
                    return super.canHighlightChildAt(view, f, f2);
                }
            };
            this.listView = recyclerListView;
            if (z) {
                recyclerListView.setItemAnimator(null);
            } else {
                ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setDelayAnimations(false);
            }
            this.listView.setSelectorType(8);
            this.listView.setSelectorRadius(6);
            this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey, resourcesProvider));
            RecyclerListView recyclerListView2 = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false) {
                @Override
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        protected void onTargetFound(View view, RecyclerView.State state2, RecyclerView.SmoothScroller.Action action) {
                            int calculateDxToMakeVisible = calculateDxToMakeVisible(view, getHorizontalSnapPreference());
                            if (calculateDxToMakeVisible > 0 || (calculateDxToMakeVisible == 0 && view.getLeft() - AndroidUtilities.m35dp(21.0f) < 0)) {
                                calculateDxToMakeVisible += AndroidUtilities.m35dp(60.0f);
                            } else if (calculateDxToMakeVisible < 0 || (calculateDxToMakeVisible == 0 && view.getRight() + AndroidUtilities.m35dp(21.0f) > TabsView.this.getMeasuredWidth())) {
                                calculateDxToMakeVisible -= AndroidUtilities.m35dp(60.0f);
                            }
                            int calculateDyToMakeVisible = calculateDyToMakeVisible(view, getVerticalSnapPreference());
                            int max = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((calculateDxToMakeVisible * calculateDxToMakeVisible) + (calculateDyToMakeVisible * calculateDyToMakeVisible))));
                            if (max > 0) {
                                action.update(-calculateDxToMakeVisible, -calculateDyToMakeVisible, max, this.mDecelerateInterpolator);
                            }
                        }
                    };
                    linearSmoothScroller.setTargetPosition(i);
                    startSmoothScroll(linearSmoothScroller);
                }

                @Override
                public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
                    if (TabsView.this.isInHiddenMode) {
                        accessibilityNodeInfoCompat.setVisibleToUser(false);
                    }
                }
            };
            this.layoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.listView.setPadding(AndroidUtilities.m35dp(7.0f), 0, AndroidUtilities.m35dp(7.0f), 0);
            this.listView.setClipToPadding(false);
            this.listView.setDrawSelectorBehind(true);
            ListAdapter listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            listAdapter.setHasStableIds(z);
            this.listView.setAdapter(this.adapter);
            this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
                @Override
                public boolean hasDoubleTap(View view, int i) {
                    return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
                }

                @Override
                public void onDoubleTap(View view, int i, float f, float f2) {
                    RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
                }

                @Override
                public final void onItemClick(View view, int i, float f, float f2) {
                    ViewPagerFixed.TabsView.this.lambda$new$0(view, i, f, f2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    TabsView.this.invalidate();
                }
            });
            addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        }

        public void lambda$new$0(View view, int i, float f, float f2) {
            TabsViewDelegate tabsViewDelegate;
            if (this.delegate.canPerformActions()) {
                TabView tabView = (TabView) view;
                if (i != this.currentPosition || (tabsViewDelegate = this.delegate) == null) {
                    scrollToTab(tabView.currentTab.f1158id, i);
                } else {
                    tabsViewDelegate.onSamePageSelected();
                }
            }
        }

        public void setDelegate(TabsViewDelegate tabsViewDelegate) {
            this.delegate = tabsViewDelegate;
        }

        public boolean isAnimatingIndicator() {
            return this.animatingIndicator;
        }

        public int getCurrentPosition() {
            return this.currentPosition;
        }

        public int getPreviousPosition() {
            return this.previousPosition;
        }

        public float getAnimatingIndicatorProgress() {
            return this.animatingIndicatorProgress;
        }

        public void scrollToTab(int i, int i2) {
            int i3 = this.currentPosition;
            boolean z = i3 < i2;
            this.scrollingToChild = -1;
            this.previousPosition = i3;
            this.previousId = this.selectedTabId;
            this.currentPosition = i2;
            this.selectedTabId = i;
            ValueAnimator valueAnimator = this.tabsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.animatingIndicator) {
                this.animatingIndicator = false;
            }
            this.animationTime = 0.0f;
            this.animatingIndicatorProgress = 0.0f;
            this.animatingIndicator = true;
            setEnabled(false);
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageSelected(i2, z);
            }
            scrollToChild(i2);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.tabsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    TabsView.this.setAnimationIdicatorProgress(floatValue);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(floatValue);
                    }
                }
            });
            this.tabsAnimator.setDuration(250L);
            this.tabsAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.tabsAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    TabsView.this.animatingIndicator = false;
                    TabsView.this.setEnabled(true);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(1.0f);
                    }
                    TabsView.this.invalidate();
                }
            });
            this.tabsAnimator.start();
        }

        public void setAnimationIdicatorProgress(float f) {
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(f);
            }
        }

        public Drawable getSelectorDrawable() {
            return this.selectorDrawable;
        }

        public RecyclerListView getTabsContainer() {
            return this.listView;
        }

        public void addTab(int i, String str) {
            int size = this.tabs.size();
            if (size == 0 && this.selectedTabId == -1) {
                this.selectedTabId = i;
            }
            this.positionToId.put(size, i);
            this.idToPosition.put(i, size);
            int i2 = this.selectedTabId;
            if (i2 != -1 && i2 == i) {
                this.currentPosition = size;
            }
            Tab tab = new Tab(i, str);
            this.allTabsWidth += tab.getWidth(true, this.textPaint) + AndroidUtilities.m35dp(this.tabMarginDp * 2);
            this.tabs.add(tab);
        }

        public void removeTabs() {
            this.tabs.clear();
            this.positionToId.clear();
            this.idToPosition.clear();
            this.positionToWidth.clear();
            this.positionToX.clear();
            this.allTabsWidth = 0;
        }

        public void finishAddingTabs() {
            this.adapter.notifyDataSetChanged();
        }

        public int getCurrentTabId() {
            return this.selectedTabId;
        }

        public int getFirstTabId() {
            return this.positionToId.get(0, 0);
        }

        private void updateTabsWidths() {
            this.positionToX.clear();
            this.positionToWidth.clear();
            int m35dp = AndroidUtilities.m35dp(7.0f);
            int size = this.tabs.size();
            for (int i = 0; i < size; i++) {
                int width = this.tabs.get(i).getWidth(false, this.textPaint);
                this.positionToWidth.put(i, width);
                this.positionToX.put(i, (this.additionalTabWidth / 2) + m35dp);
                m35dp += width + AndroidUtilities.m35dp(this.tabMarginDp * 2) + this.additionalTabWidth;
            }
        }

        @Override
        protected boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.Components.ViewPagerFixed.TabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        @Override
        protected void onMeasure(int i, int i2) {
            if (!this.tabs.isEmpty()) {
                int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.m35dp(7.0f)) - AndroidUtilities.m35dp(7.0f);
                int i3 = this.additionalTabWidth;
                if (this.tabs.size() == 1) {
                    this.additionalTabWidth = 0;
                } else {
                    int i4 = this.allTabsWidth;
                    this.additionalTabWidth = i4 < size ? (size - i4) / this.tabs.size() : 0;
                }
                if (i3 != this.additionalTabWidth) {
                    this.ignoreLayout = true;
                    this.adapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
                updateTabsWidths();
            }
            super.onMeasure(i, i2);
        }

        public void updateColors() {
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey, this.resourcesProvider));
            this.listView.invalidateViews();
            this.listView.invalidate();
            invalidate();
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        private void scrollToChild(int i) {
            if (this.tabs.isEmpty() || this.scrollingToChild == i || i < 0 || i >= this.tabs.size()) {
                return;
            }
            this.scrollingToChild = i;
            this.listView.smoothScrollToPosition(i);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int i5 = i3 - i;
            if (this.prevLayoutWidth != i5) {
                this.prevLayoutWidth = i5;
                this.scrollingToChild = -1;
                if (this.animatingIndicator) {
                    AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                    this.animatingIndicator = false;
                    setEnabled(true);
                    TabsViewDelegate tabsViewDelegate = this.delegate;
                    if (tabsViewDelegate != null) {
                        tabsViewDelegate.onPageScrolled(1.0f);
                    }
                }
            }
        }

        public void selectTab(int i, int i2, float f) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            this.currentPosition = i;
            this.selectedTabId = this.positionToId.get(i);
            if (f > 0.0f) {
                this.manualScrollingToPosition = i2;
                this.manualScrollingToId = this.positionToId.get(i2);
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(i);
            if (f >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = i2;
                this.selectedTabId = this.positionToId.get(i2);
            }
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.invalidateBlur();
            }
        }

        public void selectTabWithId(int i, float f) {
            int i2 = this.idToPosition.get(i, -1);
            if (i2 < 0) {
                return;
            }
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            if (f > 0.0f) {
                this.manualScrollingToPosition = i2;
                this.manualScrollingToId = i;
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(i2);
            if (f >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = i2;
                this.selectedTabId = i;
            }
        }

        public void setIsEditing(boolean z) {
            this.isEditing = z;
            this.listView.invalidateViews();
            invalidate();
            if (this.isEditing || !this.orderChanged) {
                return;
            }
            MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
            TLRPC$TL_messages_updateDialogFiltersOrder tLRPC$TL_messages_updateDialogFiltersOrder = new TLRPC$TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i);
                tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i).f819id));
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, ViewPagerFixed$TabsView$$ExternalSyntheticLambda0.INSTANCE);
            this.orderChanged = false;
        }

        public class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context mContext;

            @Override
            public int getItemViewType(int i) {
                return 0;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            public ListAdapter(Context context) {
                this.mContext = context;
            }

            @Override
            public int getItemCount() {
                return TabsView.this.tabs.size();
            }

            @Override
            public long getItemId(int i) {
                return ((Tab) TabsView.this.tabs.get(i)).f1158id;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new TabView(this.mContext));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((TabView) viewHolder.itemView).setTab((Tab) TabsView.this.tabs.get(i), i);
            }
        }

        public void hide(boolean z, boolean z2) {
            this.isInHiddenMode = z;
            int i = 0;
            if (z2) {
                while (i < this.listView.getChildCount()) {
                    this.listView.getChildAt(i).animate().alpha(z ? 0.0f : 1.0f).scaleX(z ? 0.0f : 1.0f).scaleY(z ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(220L).start();
                    i++;
                }
            } else {
                while (i < this.listView.getChildCount()) {
                    View childAt = this.listView.getChildAt(i);
                    childAt.setScaleX(z ? 0.0f : 1.0f);
                    childAt.setScaleY(z ? 0.0f : 1.0f);
                    childAt.setAlpha(z ? 0.0f : 1.0f);
                    i++;
                }
                this.hideProgress = z ? 1.0f : 0.0f;
            }
            invalidate();
        }
    }

    private View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(this.rect);
                if (!this.rect.contains((int) f, (int) f2)) {
                    continue;
                } else if (childAt.canScrollHorizontally(-1)) {
                    return childAt;
                } else {
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

    public void drawForBlur(Canvas canvas) {
        RecyclerListView findRecyclerView;
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPages;
            if (i >= viewArr.length) {
                return;
            }
            if (viewArr[i] != null && viewArr[i].getVisibility() == 0 && (findRecyclerView = findRecyclerView(this.viewPages[i])) != null) {
                for (int i2 = 0; i2 < findRecyclerView.getChildCount(); i2++) {
                    View childAt = findRecyclerView.getChildAt(i2);
                    if (childAt.getY() < AndroidUtilities.m35dp(203.0f) + AndroidUtilities.m35dp(100.0f)) {
                        int save = canvas.save();
                        canvas.translate(this.viewPages[i].getX(), getY() + this.viewPages[i].getY() + findRecyclerView.getY() + childAt.getY());
                        childAt.draw(canvas);
                        canvas.restoreToCount(save);
                    }
                }
            }
            i++;
        }
    }

    private RecyclerListView findRecyclerView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof RecyclerListView) {
                    return (RecyclerListView) childAt;
                }
                if (childAt instanceof ViewGroup) {
                    findRecyclerView(childAt);
                }
            }
            return null;
        }
        return null;
    }

    public void setAllowDisallowInterceptTouch(boolean z) {
        this.allowDisallowInterceptTouch = z;
    }
}