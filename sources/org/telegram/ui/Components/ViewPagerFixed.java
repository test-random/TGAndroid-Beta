package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Property;
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
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;

public class ViewPagerFixed extends FrameLayout {
    private static final Interpolator interpolator = new Interpolator() {
        @Override
        public final float getInterpolation(float f) {
            float lambda$static$0;
            lambda$static$0 = ViewPagerFixed.lambda$static$0(f);
            return lambda$static$0;
        }
    };
    private Adapter adapter;
    private float additionalOffset;
    private boolean allowDisallowInterceptTouch;
    private boolean animatingForward;
    private boolean backAnimation;
    private float backProgress;
    public int currentPosition;
    public float currentProgress;
    private ValueAnimator manualScrolling;
    private int maximumVelocity;
    private boolean maybeStartTracking;
    int nextPosition;
    AnimationNotificationsLocker notificationsLocker;
    private android.graphics.Rect rect;
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
    protected SparseArray viewsByType;

    public static abstract class Adapter {
        public abstract void bindView(View view, int i, int i2);

        public boolean canScrollTo(int i) {
            return true;
        }

        public abstract View createView(int i);

        public abstract int getItemCount();

        public int getItemId(int i) {
            return i;
        }

        public String getItemTitle(int i) {
            return "";
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean hasStableId() {
            return false;
        }

        public boolean needsTab(int i) {
            return true;
        }
    }

    public static class TabsView extends FrameLayout {
        private int activeTextColorKey;
        private ListAdapter adapter;
        private int additionalTabWidth;
        private int allTabsWidth;
        private boolean animatingIndicator;
        private float animatingIndicatorProgress;
        private Runnable animationRunnable;
        private float animationTime;
        private int backgroundColorKey;
        private Paint counterPaint;
        private float crossfadeAlpha;
        private Bitmap crossfadeBitmap;
        private Paint crossfadePaint;
        private int currentPosition;
        private TabsViewDelegate delegate;
        private Paint deletePaint;
        private float editingAnimationProgress;
        private boolean editingForwardAnimation;
        private float editingStartAnimationProgress;
        private float hideProgress;
        private SparseIntArray idToPosition;
        private boolean ignoreLayout;
        private float indicatorProgress2;
        private CubicBezierInterpolator interpolator;
        private boolean invalidated;
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
        private float overrideFromW;
        private float overrideFromX;
        private SparseIntArray positionToId;
        private SparseIntArray positionToWidth;
        private SparseIntArray positionToX;
        private Utilities.Callback2Return preTabClick;
        private int prevLayoutWidth;
        private int previousId;
        private int previousPosition;
        private Theme.ResourcesProvider resourcesProvider;
        private int scrollingToChild;
        private int selectedTabId;
        private int selectorColorKey;
        private GradientDrawable selectorDrawable;
        private final Paint selectorPaint;
        private int selectorType;
        private int tabLineColorKey;
        public int tabMarginDp;
        private ArrayList tabs;
        ValueAnimator tabsAnimator;
        private TextPaint textCounterPaint;
        private TextPaint textPaint;
        private int unactiveTextColorKey;

        public class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context mContext;

            public ListAdapter(Context context) {
                this.mContext = context;
            }

            @Override
            public int getItemCount() {
                return TabsView.this.tabs.size();
            }

            @Override
            public long getItemId(int i) {
                return ((Tab) TabsView.this.tabs.get(i)).id;
            }

            @Override
            public int getItemViewType(int i) {
                return 0;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((TabView) viewHolder.itemView).setTab((Tab) TabsView.this.tabs.get(i), i);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new TabView(this.mContext));
            }
        }

        public static class Tab {
            public float alpha = 1.0f;
            public int counter;
            public int id;
            public CharSequence title;
            public int titleWidth;

            public Tab(int i, CharSequence charSequence) {
                this.id = i;
                this.title = charSequence;
            }

            public int getWidth(boolean z, TextPaint textPaint) {
                int ceil = (int) Math.ceil(textPaint.measureText(this.title == null ? "" : r1.toString()));
                this.titleWidth = ceil;
                return Math.max(0, ceil);
            }
        }

        public class TabView extends View {
            private int currentPosition;
            private Tab currentTab;
            private CharSequence currentText;
            private RectF rect;
            private int tabWidth;
            private int textHeight;
            private StaticLayout textLayout;
            private int textOffsetX;

            public TabView(Context context) {
                super(context);
                this.rect = new RectF();
            }

            @Override
            public int getId() {
                return this.currentTab.id;
            }

            @Override
            protected void onDraw(Canvas canvas) {
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                int i6;
                int i7;
                String str;
                int i8;
                int i9;
                int i10;
                Paint paint;
                int i11;
                int i12;
                if (this.currentTab.id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.save();
                    float f = TabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                    canvas.translate(AndroidUtilities.dp(0.66f) * f, 0.0f);
                    canvas.rotate(f, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                }
                if (TabsView.this.manualScrollingToId != -1) {
                    i = TabsView.this.manualScrollingToId;
                    i2 = TabsView.this.selectedTabId;
                } else {
                    i = TabsView.this.selectedTabId;
                    i2 = TabsView.this.previousId;
                }
                if (this.currentTab.id == i) {
                    i3 = TabsView.this.activeTextColorKey;
                    i4 = TabsView.this.unactiveTextColorKey;
                    i5 = Theme.key_chats_tabUnreadActiveBackground;
                    i6 = Theme.key_chats_tabUnreadUnactiveBackground;
                } else {
                    i3 = TabsView.this.unactiveTextColorKey;
                    i4 = TabsView.this.activeTextColorKey;
                    i5 = Theme.key_chats_tabUnreadUnactiveBackground;
                    i6 = Theme.key_chats_tabUnreadActiveBackground;
                }
                if (TabsView.this.selectorType == 9) {
                    TabsView.this.textPaint.setColor(Theme.getColor(TabsView.this.unactiveTextColorKey, TabsView.this.resourcesProvider));
                } else if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToId != -1) && ((i7 = this.currentTab.id) == i || i7 == i2)) {
                    TabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(i4, TabsView.this.resourcesProvider), Theme.getColor(i3, TabsView.this.resourcesProvider), TabsView.this.animatingIndicatorProgress));
                } else {
                    TabsView.this.textPaint.setColor(Theme.getColor(i3, TabsView.this.resourcesProvider));
                }
                int i13 = this.currentTab.counter;
                if (i13 > 0) {
                    str = String.format("%d", Integer.valueOf(i13));
                    i8 = (int) Math.ceil(TabsView.this.textCounterPaint.measureText(str));
                    i9 = Math.max(AndroidUtilities.dp(10.0f), i8) + AndroidUtilities.dp(10.0f);
                } else {
                    str = null;
                    i8 = 0;
                    i9 = 0;
                }
                if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                    i9 = (int) (i9 + ((AndroidUtilities.dp(20.0f) - i9) * TabsView.this.editingStartAnimationProgress));
                }
                int i14 = this.currentTab.titleWidth;
                if (i9 != 0) {
                    i10 = AndroidUtilities.dp((str != null ? 1.0f : TabsView.this.editingStartAnimationProgress) * 6.0f) + i9;
                } else {
                    i10 = 0;
                }
                this.tabWidth = i14 + i10;
                int measuredWidth = (getMeasuredWidth() - this.tabWidth) / 2;
                if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                    CharSequence charSequence = this.currentTab.title;
                    this.currentText = charSequence;
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(charSequence, TabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), TabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
                if (str != null || (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f))) {
                    TabsView.this.textCounterPaint.setColor(Theme.getColor(TabsView.this.backgroundColorKey, TabsView.this.resourcesProvider));
                    if (Theme.hasThemeKey(i5) && Theme.hasThemeKey(i6)) {
                        int color = Theme.getColor(i5, TabsView.this.resourcesProvider);
                        if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToPosition != -1) && ((i12 = this.currentTab.id) == i || i12 == i2)) {
                            TabsView.this.counterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(i6, TabsView.this.resourcesProvider), color, TabsView.this.animatingIndicatorProgress));
                        } else {
                            TabsView.this.counterPaint.setColor(color);
                        }
                    } else {
                        TabsView.this.counterPaint.setColor(TabsView.this.textPaint.getColor());
                    }
                    int dp = measuredWidth + this.currentTab.titleWidth + AndroidUtilities.dp(6.0f);
                    int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2;
                    if (this.currentTab.id == Integer.MAX_VALUE || ((!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f) || str != null)) {
                        paint = TabsView.this.counterPaint;
                        i11 = 255;
                    } else {
                        paint = TabsView.this.counterPaint;
                        i11 = (int) (TabsView.this.editingStartAnimationProgress * 255.0f);
                    }
                    paint.setAlpha(i11);
                    this.rect.set(dp, measuredHeight, dp + i9, AndroidUtilities.dp(20.0f) + measuredHeight);
                    RectF rectF = this.rect;
                    float f2 = AndroidUtilities.density * 11.5f;
                    canvas.drawRoundRect(rectF, f2, f2, TabsView.this.counterPaint);
                    if (str != null) {
                        if (this.currentTab.id != Integer.MAX_VALUE) {
                            TabsView.this.textCounterPaint.setAlpha((int) ((1.0f - TabsView.this.editingStartAnimationProgress) * 255.0f));
                        }
                        RectF rectF2 = this.rect;
                        canvas.drawText(str, rectF2.left + ((rectF2.width() - i8) / 2.0f), measuredHeight + AndroidUtilities.dp(14.5f), TabsView.this.textCounterPaint);
                    }
                    if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                        TabsView.this.deletePaint.setColor(TabsView.this.textCounterPaint.getColor());
                        TabsView.this.deletePaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                        float dp2 = AndroidUtilities.dp(3.0f);
                        canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() - dp2, this.rect.centerX() + dp2, this.rect.centerY() + dp2, TabsView.this.deletePaint);
                        canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() + dp2, this.rect.centerX() + dp2, this.rect.centerY() - dp2, TabsView.this.deletePaint);
                    }
                }
                if (this.currentTab.id == Integer.MAX_VALUE || TabsView.this.editingAnimationProgress == 0.0f) {
                    return;
                }
                canvas.restore();
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setSelected((this.currentTab == null || TabsView.this.selectedTabId == -1 || this.currentTab.id != TabsView.this.selectedTabId) ? false : true);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                setMeasuredDimension(this.currentTab.getWidth(false, TabsView.this.textPaint) + AndroidUtilities.dp(TabsView.this.tabMarginDp * 2) + TabsView.this.additionalTabWidth, View.MeasureSpec.getSize(i2));
            }

            public void setTab(Tab tab, int i) {
                this.currentTab = tab;
                this.currentPosition = i;
                setContentDescription(tab.title);
                setAlpha(tab.alpha);
                requestLayout();
            }
        }

        public interface TabsViewDelegate {
            boolean canPerformActions();

            void invalidateBlur();

            boolean needsTab(int i);

            void onPageScrolled(float f);

            void onPageSelected(int i, boolean z);

            void onSamePageSelected();
        }

        public TabsView(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            RecyclerListView recyclerListView;
            FrameLayout.LayoutParams createFrame;
            int i2 = 0;
            Object[] objArr = 0;
            this.indicatorProgress2 = 1.0f;
            this.textPaint = new TextPaint(1);
            this.textCounterPaint = new TextPaint(1);
            this.deletePaint = new TextPaint(1);
            this.counterPaint = new Paint(1);
            this.tabs = new ArrayList();
            this.crossfadePaint = new Paint();
            this.tabMarginDp = 16;
            this.selectedTabId = -1;
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
            this.scrollingToChild = -1;
            this.tabLineColorKey = Theme.key_profile_tabSelectedLine;
            this.activeTextColorKey = Theme.key_profile_tabSelectedText;
            this.unactiveTextColorKey = Theme.key_profile_tabText;
            this.selectorColorKey = Theme.key_profile_tabSelector;
            this.backgroundColorKey = Theme.key_actionBarDefault;
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
                        TabsView.access$3416(TabsView.this, ((float) elapsedRealtime) / 200.0f);
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
            this.selectorPaint = new Paint(1);
            this.resourcesProvider = resourcesProvider;
            this.selectorType = i;
            this.textCounterPaint.setTextSize(AndroidUtilities.dp(13.0f));
            this.textCounterPaint.setTypeface(AndroidUtilities.bold());
            this.textPaint.setTextSize(AndroidUtilities.dp(i == 9 ? 14.0f : 15.0f));
            this.textPaint.setTypeface(AndroidUtilities.bold());
            this.deletePaint.setStyle(Paint.Style.STROKE);
            this.deletePaint.setStrokeCap(Paint.Cap.ROUND);
            this.deletePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
            this.selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null);
            float dpf2 = AndroidUtilities.dpf2(3.0f);
            this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey, resourcesProvider));
            setHorizontalScrollBarEnabled(false);
            RecyclerListView recyclerListView2 = new RecyclerListView(context) {
                @Override
                public void addView(View view, int i3, ViewGroup.LayoutParams layoutParams) {
                    float f;
                    super.addView(view, i3, layoutParams);
                    if (TabsView.this.isInHiddenMode) {
                        view.setScaleX(0.3f);
                        view.setScaleY(0.3f);
                        f = 0.0f;
                    } else {
                        f = 1.0f;
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                    }
                    view.setAlpha(f);
                }

                @Override
                public boolean canHighlightChildAt(View view, float f, float f2) {
                    if (TabsView.this.isEditing) {
                        TabView tabView = (TabView) view;
                        float dp = AndroidUtilities.dp(6.0f);
                        if (tabView.rect.left - dp < f && tabView.rect.right + dp > f) {
                            return false;
                        }
                    }
                    return super.canHighlightChildAt(view, f, f2);
                }

                @Override
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    TabsView.this.invalidate();
                }
            };
            this.listView = recyclerListView2;
            recyclerListView2.setOverScrollMode(2);
            RecyclerListView recyclerListView3 = this.listView;
            if (z) {
                recyclerListView3.setItemAnimator(null);
            } else {
                ((DefaultItemAnimator) recyclerListView3.getItemAnimator()).setDelayAnimations(false);
            }
            this.listView.setSelectorType(i);
            if (i == 3) {
                this.listView.setSelectorRadius(0);
            } else {
                this.listView.setSelectorRadius(6);
            }
            this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey, resourcesProvider));
            RecyclerListView recyclerListView4 = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, i2, objArr == true ? 1 : 0) {
                @Override
                public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
                    if (TabsView.this.isInHiddenMode) {
                        accessibilityNodeInfoCompat.setVisibleToUser(false);
                    }
                }

                @Override
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i3) {
                    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        protected void onTargetFound(View view, RecyclerView.State state2, RecyclerView.SmoothScroller.Action action) {
                            int calculateDxToMakeVisible = calculateDxToMakeVisible(view, getHorizontalSnapPreference());
                            if (calculateDxToMakeVisible > 0 || (calculateDxToMakeVisible == 0 && view.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                                calculateDxToMakeVisible += AndroidUtilities.dp(60.0f);
                            } else if (calculateDxToMakeVisible < 0 || (calculateDxToMakeVisible == 0 && view.getRight() + AndroidUtilities.dp(21.0f) > TabsView.this.getMeasuredWidth())) {
                                calculateDxToMakeVisible -= AndroidUtilities.dp(60.0f);
                            }
                            int calculateDyToMakeVisible = calculateDyToMakeVisible(view, getVerticalSnapPreference());
                            int max = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((calculateDxToMakeVisible * calculateDxToMakeVisible) + (calculateDyToMakeVisible * calculateDyToMakeVisible))));
                            if (max > 0) {
                                action.update(-calculateDxToMakeVisible, -calculateDyToMakeVisible, max, this.mDecelerateInterpolator);
                            }
                        }
                    };
                    linearSmoothScroller.setTargetPosition(i3);
                    startSmoothScroll(linearSmoothScroller);
                }
            };
            this.layoutManager = linearLayoutManager;
            recyclerListView4.setLayoutManager(linearLayoutManager);
            this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
            this.listView.setClipToPadding(false);
            this.listView.setDrawSelectorBehind(true);
            ListAdapter listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            listAdapter.setHasStableIds(z);
            this.listView.setAdapter(this.adapter);
            this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
                @Override
                public boolean hasDoubleTap(View view, int i3) {
                    return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i3);
                }

                @Override
                public void onDoubleTap(View view, int i3, float f, float f2) {
                    RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i3, f, f2);
                }

                @Override
                public final void onItemClick(View view, int i3, float f, float f2) {
                    ViewPagerFixed.TabsView.this.lambda$new$0(view, i3, f, f2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                    TabsView.this.invalidate();
                }
            });
            if (i == 9) {
                recyclerListView = this.listView;
                createFrame = LayoutHelper.createFrame(-2, -1, 1);
            } else {
                recyclerListView = this.listView;
                createFrame = LayoutHelper.createFrame(-1, -1.0f);
            }
            addView(recyclerListView, createFrame);
        }

        static float access$3416(TabsView tabsView, float f) {
            float f2 = tabsView.animationTime + f;
            tabsView.animationTime = f2;
            return f2;
        }

        public void lambda$new$0(View view, int i, float f, float f2) {
            TabsViewDelegate tabsViewDelegate;
            TabsViewDelegate tabsViewDelegate2 = this.delegate;
            if (tabsViewDelegate2 == null || tabsViewDelegate2.canPerformActions()) {
                TabView tabView = (TabView) view;
                if (i == this.currentPosition && (tabsViewDelegate = this.delegate) != null) {
                    tabsViewDelegate.onSamePageSelected();
                    return;
                }
                Utilities.Callback2Return callback2Return = this.preTabClick;
                if (callback2Return == null || !((Boolean) callback2Return.run(Integer.valueOf(tabView.currentTab.id), Integer.valueOf(i))).booleanValue()) {
                    scrollToTab(tabView.currentTab.id, i);
                }
            }
        }

        public void lambda$scrollToTab$1(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setAnimationIdicatorProgress(floatValue);
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(floatValue);
            }
        }

        public static void lambda$setIsEditing$2(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        public void saveFromValues() {
            this.overrideFromX = this.lastDrawnIndicatorX;
            this.overrideFromW = this.lastDrawnIndicatorW;
        }

        private void scrollToChild(int i) {
            if (this.tabs.isEmpty() || this.scrollingToChild == i || i < 0 || i >= this.tabs.size()) {
                return;
            }
            this.scrollingToChild = i;
            this.listView.smoothScrollToPosition(i);
        }

        private void updateTabsWidths() {
            this.positionToX.clear();
            this.positionToWidth.clear();
            int dp = AndroidUtilities.dp(7.0f);
            int size = this.tabs.size();
            for (int i = 0; i < size; i++) {
                int width = ((Tab) this.tabs.get(i)).getWidth(false, this.textPaint);
                this.positionToWidth.put(i, width);
                this.positionToX.put(i, (this.additionalTabWidth / 2) + dp);
                dp += width + AndroidUtilities.dp(this.tabMarginDp * 2) + this.additionalTabWidth;
            }
        }

        public void addTab(int i, CharSequence charSequence) {
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
            Tab tab = new Tab(i, charSequence);
            this.allTabsWidth += tab.getWidth(true, this.textPaint) + AndroidUtilities.dp(this.tabMarginDp * 2);
            this.tabs.add(tab);
        }

        @Override
        protected boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.TabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        public void finishAddingTabs() {
            this.adapter.notifyDataSetChanged();
        }

        public float getAnimatingIndicatorProgress() {
            return this.animatingIndicatorProgress;
        }

        public int getCurrentPosition() {
            return this.currentPosition;
        }

        public int getCurrentTabId() {
            return this.selectedTabId;
        }

        public int getFirstTabId() {
            return this.positionToId.get(0, 0);
        }

        public int getPreviousPosition() {
            return this.previousPosition;
        }

        public Drawable getSelectorDrawable() {
            return this.selectorDrawable;
        }

        public RecyclerListView getTabsContainer() {
            return this.listView;
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

        public boolean isAnimatingIndicator() {
            return this.animatingIndicator;
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

        @Override
        protected void onMeasure(int i, int i2) {
            if (!this.tabs.isEmpty()) {
                int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
                int i3 = this.additionalTabWidth;
                if (this.tabs.size() == 1 || this.selectorType == 9) {
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
                this.invalidated = false;
            }
            super.onMeasure(i, i2);
        }

        public void removeTabs() {
            this.tabs.clear();
            this.positionToId.clear();
            this.idToPosition.clear();
            this.positionToWidth.clear();
            this.positionToX.clear();
            this.allTabsWidth = 0;
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        public void scrollToTab(int i, int i2) {
            int i3 = this.currentPosition;
            boolean z = i3 < i2;
            this.scrollingToChild = -1;
            this.previousPosition = i3;
            this.previousId = this.selectedTabId;
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate == null || tabsViewDelegate.needsTab(i2)) {
                this.currentPosition = i2;
                this.selectedTabId = i;
            }
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
            TabsViewDelegate tabsViewDelegate2 = this.delegate;
            if (tabsViewDelegate2 != null) {
                tabsViewDelegate2.onPageSelected(i2, z);
            }
            scrollToChild(this.currentPosition);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.tabsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ViewPagerFixed.TabsView.this.lambda$scrollToTab$1(valueAnimator2);
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

        public void selectTab(int i, int i2, float f) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            this.currentPosition = i;
            this.selectedTabId = this.positionToId.get(i);
            if (f > 0.0f) {
                TabsViewDelegate tabsViewDelegate = this.delegate;
                if (tabsViewDelegate == null || tabsViewDelegate.needsTab(i2)) {
                    this.manualScrollingToPosition = i2;
                } else {
                    this.manualScrollingToPosition = i;
                }
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
            TabsViewDelegate tabsViewDelegate2 = this.delegate;
            if (tabsViewDelegate2 != null) {
                tabsViewDelegate2.invalidateBlur();
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

        public void setAnimationIdicatorProgress(float f) {
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(f);
            }
        }

        public void setDelegate(TabsViewDelegate tabsViewDelegate) {
            this.delegate = tabsViewDelegate;
        }

        public void setIsEditing(boolean z) {
            this.isEditing = z;
            this.editingForwardAnimation = true;
            this.listView.invalidateViews();
            invalidate();
            if (this.isEditing || !this.orderChanged) {
                return;
            }
            MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
            TLRPC.TL_messages_updateDialogFiltersOrder tL_messages_updateDialogFiltersOrder = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i);
                tL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i).id));
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_updateDialogFiltersOrder, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ViewPagerFixed.TabsView.lambda$setIsEditing$2(tLObject, tL_error);
                }
            });
            this.orderChanged = false;
        }

        public void setPreTabClick(Utilities.Callback2Return<Integer, Integer, Boolean> callback2Return) {
            this.preTabClick = callback2Return;
        }

        public void updateColors() {
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey, this.resourcesProvider));
            this.listView.invalidateViews();
            this.listView.invalidate();
            invalidate();
        }
    }

    public ViewPagerFixed(Context context) {
        this(context, null);
    }

    public ViewPagerFixed(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentProgress = 1.0f;
        this.viewsByType = new SparseArray();
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.updateTabProgress = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (ViewPagerFixed.this.tabsAnimationInProgress) {
                    float abs = Math.abs(ViewPagerFixed.this.viewPages[0].getTranslationX()) / ViewPagerFixed.this.viewPages[0].getMeasuredWidth();
                    ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                    float f = 1.0f - abs;
                    viewPagerFixed.currentProgress = f;
                    TabsView tabsView = viewPagerFixed.tabsView;
                    if (tabsView != null) {
                        tabsView.selectTab(viewPagerFixed.nextPosition, viewPagerFixed.currentPosition, f);
                    }
                }
                ViewPagerFixed.this.onTabAnimationUpdate(false);
            }
        };
        this.rect = new android.graphics.Rect();
        this.allowDisallowInterceptTouch = true;
        this.resourcesProvider = resourcesProvider;
        this.touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.viewTypes = new int[2];
        this.viewPages = new View[2];
        setClipChildren(true);
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((f - 0.5f) * 0.47123894f);
    }

    private RecyclerListView findRecyclerView(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
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
                        android.graphics.Rect rect = this.rect;
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

    public void lambda$onTouchEventInternal$2(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.backProgress = floatValue;
        onBackProgress(floatValue);
    }

    public void lambda$rebuild$3(ValueAnimator valueAnimator) {
        this.updateTabProgress.onAnimationUpdate(valueAnimator);
        this.tabsView.indicatorProgress2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.tabsView.listView.invalidateViews();
        this.tabsView.invalidate();
    }

    public void lambda$scrollToPosition$1(ValueAnimator valueAnimator) {
        View view;
        int measuredWidth;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view2 = this.viewPages[1];
        if (view2 == null) {
            return;
        }
        if (this.animatingForward) {
            view2.setTranslationX(r0[0].getMeasuredWidth() * (1.0f - floatValue));
            view = this.viewPages[0];
            measuredWidth = -view.getMeasuredWidth();
        } else {
            view2.setTranslationX((-r0[0].getMeasuredWidth()) * (1.0f - floatValue));
            view = this.viewPages[0];
            measuredWidth = view.getMeasuredWidth();
        }
        view.setTranslationX(measuredWidth * floatValue);
        this.currentProgress = floatValue;
        onTabAnimationUpdate(true);
    }

    public static float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private boolean prepareForMoving(android.view.MotionEvent r7, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.prepareForMoving(android.view.MotionEvent, boolean):boolean");
    }

    public void swapViews() {
        View[] viewArr = this.viewPages;
        View view = viewArr[0];
        View view2 = viewArr[1];
        viewArr[0] = view2;
        viewArr[1] = view;
        int i = this.currentPosition;
        int i2 = this.nextPosition;
        this.currentPosition = i2;
        this.nextPosition = i;
        this.currentProgress = 1.0f - this.currentProgress;
        int[] iArr = this.viewTypes;
        int i3 = iArr[0];
        iArr[0] = iArr[1];
        iArr[1] = i3;
        onItemSelected(view2, view, i2, i);
    }

    public void updateViewForIndex(int i) {
        View view;
        int i2 = i == 0 ? this.currentPosition : this.nextPosition;
        if (i2 < 0 || i2 >= this.adapter.getItemCount()) {
            return;
        }
        if (this.viewPages[i] == null) {
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view2 = (View) this.viewsByType.get(this.viewTypes[i]);
            if (view2 == null) {
                view2 = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            if (view2.getParent() != null) {
                ((ViewGroup) view2.getParent()).removeView(view2);
            }
            addView(view2);
            this.viewPages[i] = view2;
            this.adapter.bindView(view2, i2, this.viewTypes[i]);
            view = this.viewPages[i];
        } else {
            if (this.viewTypes[i] != this.adapter.getItemViewType(i2)) {
                this.viewsByType.put(this.viewTypes[i], this.viewPages[i]);
                this.viewPages[i].setVisibility(8);
                removeView(this.viewPages[i]);
                this.viewTypes[i] = this.adapter.getItemViewType(i2);
                View view3 = (View) this.viewsByType.get(this.viewTypes[i]);
                if (view3 == null) {
                    view3 = this.adapter.createView(this.viewTypes[i]);
                } else {
                    this.viewsByType.remove(this.viewTypes[i]);
                }
                addView(view3);
                this.viewPages[i] = view3;
                view3.setVisibility(0);
                Adapter adapter = this.adapter;
                adapter.bindView(this.viewPages[i], i2, adapter.getItemViewType(i2));
                return;
            }
            this.adapter.bindView(this.viewPages[i], i2, this.viewTypes[i]);
            view = this.viewPages[i];
        }
        view.setVisibility(0);
    }

    protected void addMoreTabs() {
    }

    public boolean canScroll(MotionEvent motionEvent) {
        return true;
    }

    protected boolean canScrollForward(MotionEvent motionEvent) {
        return canScroll(motionEvent);
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

    public boolean checkTabsAnimationInProgress() {
        boolean z;
        if (!this.tabsAnimationInProgress) {
            return false;
        }
        if (this.backAnimation) {
            if (Math.abs(this.viewPages[0].getTranslationX()) < 1.0f) {
                this.viewPages[0].setTranslationX(0.0f);
                View view = this.viewPages[1];
                if (view != null) {
                    view.setTranslationX(r0[0].getMeasuredWidth() * (this.animatingForward ? 1 : -1));
                }
                z = true;
            }
            z = false;
        } else {
            if (Math.abs(this.viewPages[1].getTranslationX()) < 1.0f) {
                this.viewPages[0].setTranslationX(r0.getMeasuredWidth() * (this.animatingForward ? -1 : 1));
                View view2 = this.viewPages[1];
                if (view2 != null) {
                    view2.setTranslationX(0.0f);
                }
                z = true;
            }
            z = false;
        }
        onTabAnimationUpdate(true);
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

    public void clearViews() {
        this.viewsByType.clear();
    }

    public TabsView createTabsView(boolean z, int i) {
        TabsView tabsView = new TabsView(getContext(), z, i, this.resourcesProvider) {
            @Override
            public void selectTab(int i2, int i3, float f) {
                super.selectTab(i2, i3, f);
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                if (f > 0.5f) {
                    i2 = i3;
                }
                viewPagerFixed.onTabPageSelected(i2);
            }
        };
        this.tabsView = tabsView;
        tabsView.tabMarginDp = tabMarginDp();
        this.tabsView.setDelegate(new TabsView.TabsViewDelegate() {
            @Override
            public boolean canPerformActions() {
                return (ViewPagerFixed.this.tabsAnimationInProgress || ViewPagerFixed.this.startedTracking) ? false : true;
            }

            @Override
            public void invalidateBlur() {
                ViewPagerFixed.this.invalidateBlur();
            }

            @Override
            public boolean needsTab(int i2) {
                if (ViewPagerFixed.this.adapter == null) {
                    return true;
                }
                return ViewPagerFixed.this.adapter.needsTab(i2);
            }

            @Override
            public void onPageScrolled(float f) {
                View view;
                int measuredWidth;
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
                    }
                    ViewPagerFixed viewPagerFixed4 = ViewPagerFixed.this;
                    viewPagerFixed4.onTabScrollEnd(viewPagerFixed4.currentPosition);
                    return;
                }
                ViewPagerFixed viewPagerFixed5 = ViewPagerFixed.this;
                if (viewPagerFixed5.viewPages[1] == null) {
                    return;
                }
                if (viewPagerFixed5.animatingForward) {
                    ViewPagerFixed.this.viewPages[1].setTranslationX(r3[0].getMeasuredWidth() * (1.0f - f));
                    view = ViewPagerFixed.this.viewPages[0];
                    measuredWidth = -view.getMeasuredWidth();
                } else {
                    ViewPagerFixed.this.viewPages[1].setTranslationX((-r3[0].getMeasuredWidth()) * (1.0f - f));
                    view = ViewPagerFixed.this.viewPages[0];
                    measuredWidth = view.getMeasuredWidth();
                }
                view.setTranslationX(measuredWidth * f);
                ViewPagerFixed.this.onTabAnimationUpdate(false);
            }

            @Override
            public void onPageSelected(int i2, boolean z2) {
                ViewPagerFixed.this.animatingForward = z2;
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                viewPagerFixed.nextPosition = i2;
                viewPagerFixed.updateViewForIndex(1);
                ViewPagerFixed.this.onTabPageSelected(i2);
                View view = ViewPagerFixed.this.viewPages[0];
                int measuredWidth = view != null ? view.getMeasuredWidth() : 0;
                View view2 = ViewPagerFixed.this.viewPages[1];
                if (view2 != null) {
                    view2.setTranslationX(z2 ? measuredWidth : -measuredWidth);
                }
            }

            @Override
            public void onSamePageSelected() {
            }
        });
        fillTabs(false);
        return this.tabsView;
    }

    public void drawForBlur(Canvas canvas) {
        RecyclerListView findRecyclerView;
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPages;
            if (i >= viewArr.length) {
                return;
            }
            View view = viewArr[i];
            if (view != null && view.getVisibility() == 0 && (findRecyclerView = findRecyclerView(this.viewPages[i])) != null) {
                for (int i2 = 0; i2 < findRecyclerView.getChildCount(); i2++) {
                    View childAt = findRecyclerView.getChildAt(i2);
                    if (childAt.getY() < AndroidUtilities.dp(203.0f) + AndroidUtilities.dp(100.0f)) {
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

    public void fillTabs(boolean z) {
        TabsView tabsView;
        if (this.adapter == null || (tabsView = this.tabsView) == null) {
            return;
        }
        tabsView.removeTabs();
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            if (this.adapter.needsTab(i)) {
                this.tabsView.addTab(this.adapter.getItemId(i), this.adapter.getItemTitle(i));
            }
        }
        addMoreTabs();
        if (z) {
            TransitionManager.beginDelayedTransition(this.tabsView.listView, TransitionExt.createSimpleTransition());
        }
        this.tabsView.finishAddingTabs();
    }

    public float getAvailableTranslationX() {
        return AndroidUtilities.displaySize.x;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public View getCurrentView() {
        return this.viewPages[0];
    }

    public float getPositionAnimated() {
        float f;
        View view = this.viewPages[0];
        if (view == null || view.getVisibility() != 0) {
            f = 0.0f;
        } else {
            f = (this.currentPosition * Utilities.clamp(1.0f - Math.abs(this.viewPages[0].getTranslationX() / getAvailableTranslationX()), 1.0f, 0.0f)) + 0.0f;
        }
        View view2 = this.viewPages[1];
        if (view2 == null || view2.getVisibility() != 0) {
            return f;
        }
        return f + (this.nextPosition * Utilities.clamp(1.0f - Math.abs(this.viewPages[1].getTranslationX() / getAvailableTranslationX()), 1.0f, 0.0f));
    }

    public View[] getViewPages() {
        return this.viewPages;
    }

    protected void invalidateBlur() {
    }

    public boolean isCurrentTabFirst() {
        return this.currentPosition == 0;
    }

    public boolean isManualScrolling() {
        ValueAnimator valueAnimator = this.manualScrolling;
        return valueAnimator != null && valueAnimator.isRunning();
    }

    public boolean isTouch() {
        return this.startedTracking;
    }

    protected void onBack() {
    }

    protected boolean onBackProgress(float f) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        TabsView tabsView = this.tabsView;
        if (tabsView != null && tabsView.isAnimatingIndicator()) {
            return false;
        }
        if (checkTabsAnimationInProgress()) {
            return true;
        }
        onTouchEvent(motionEvent);
        return this.startedTracking;
    }

    protected void onItemSelected(View view, View view2, int i, int i2) {
    }

    protected void onScrollEnd() {
    }

    public void onStartTracking() {
    }

    public void onTabAnimationUpdate(boolean z) {
    }

    protected void onTabPageSelected(int i) {
    }

    public void onTabScrollEnd(int i) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return onTouchEventInternal(motionEvent);
    }

    public boolean onTouchEventInternal(android.view.MotionEvent r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.onTouchEventInternal(android.view.MotionEvent):boolean");
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
        View view = this.viewPages[1];
        if (view != null) {
            removeView(view);
            this.viewPages[1] = null;
        }
        View[] viewArr = this.viewPages;
        View view2 = viewArr[0];
        viewArr[1] = view2;
        int intValue = (view2 == null || view2.getTag() == null) ? 0 : ((Integer) this.viewPages[1].getTag()).intValue();
        if (this.adapter.getItemCount() == 0) {
            View view3 = this.viewPages[1];
            if (view3 != null) {
                removeView(view3);
                this.viewPages[1] = null;
            }
            View view4 = this.viewPages[0];
            if (view4 != null) {
                removeView(view4);
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
        if (!z) {
            View view5 = this.viewPages[1];
            if (view5 != null) {
                removeView(view5);
                this.viewPages[1] = null;
                return;
            }
            return;
        }
        this.tabsAnimation = new AnimatorSet();
        View view6 = this.viewPages[1];
        if (view6 != null) {
            view6.setTranslationX(0.0f);
        }
        View view7 = this.viewPages[0];
        if (view7 != null) {
            view7.setTranslationX(-getMeasuredWidth());
        }
        View view8 = this.viewPages[1];
        if (view8 != null) {
            this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(view8, (Property<View, Float>) View.TRANSLATION_X, getMeasuredWidth()));
        }
        View view9 = this.viewPages[0];
        if (view9 != null) {
            this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(view9, (Property<View, Float>) View.TRANSLATION_X, 0.0f));
        }
        onTabAnimationUpdate(true);
        this.tabsView.indicatorProgress2 = 0.0f;
        this.tabsView.listView.invalidateViews();
        this.tabsView.invalidate();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewPagerFixed.this.lambda$rebuild$3(valueAnimator);
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
                View view10 = viewPagerFixed.viewPages[1];
                if (view10 != null) {
                    viewPagerFixed.removeView(view10);
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
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.allowDisallowInterceptTouch && this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void resetTouch() {
        if (this.startedTracking) {
            this.maybeStartTracking = true;
            this.startedTracking = false;
            this.viewPages[0].setTranslationX(0.0f);
            View view = this.viewPages[1];
            if (view != null) {
                view.setTranslationX(this.animatingForward ? r2[0].getMeasuredWidth() : -r2[0].getMeasuredWidth());
            }
            this.nextPosition = 0;
            this.currentProgress = 1.0f;
            TabsView tabsView = this.tabsView;
            if (tabsView != null) {
                tabsView.selectTab(0, this.currentPosition, 1.0f);
            }
            onTabAnimationUpdate(false);
        }
    }

    public boolean scrollToPosition(int i) {
        ValueAnimator valueAnimator;
        View view;
        float f;
        if (i == this.currentPosition || ((valueAnimator = this.manualScrolling) != null && this.nextPosition == i)) {
            return false;
        }
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.manualScrolling = null;
        }
        boolean z = this.currentPosition < i;
        this.animatingForward = z;
        this.nextPosition = i;
        updateViewForIndex(1);
        onTabPageSelected(i);
        View view2 = this.viewPages[0];
        int measuredWidth = view2 != null ? view2.getMeasuredWidth() : 0;
        View[] viewArr = this.viewPages;
        if (z) {
            view = viewArr[1];
            f = measuredWidth;
        } else {
            view = viewArr[1];
            f = -measuredWidth;
        }
        view.setTranslationX(f);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.manualScrolling = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ViewPagerFixed.this.lambda$scrollToPosition$1(valueAnimator2);
            }
        });
        this.manualScrolling.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                if (viewPagerFixed.viewPages[1] != null) {
                    viewPagerFixed.swapViews();
                    ViewPagerFixed viewPagerFixed2 = ViewPagerFixed.this;
                    viewPagerFixed2.viewsByType.put(viewPagerFixed2.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                    ViewPagerFixed viewPagerFixed3 = ViewPagerFixed.this;
                    viewPagerFixed3.removeView(viewPagerFixed3.viewPages[1]);
                    ViewPagerFixed.this.viewPages[0].setTranslationX(0.0f);
                    ViewPagerFixed.this.viewPages[1] = null;
                }
                ViewPagerFixed.this.manualScrolling = null;
                ViewPagerFixed.this.onTabAnimationUpdate(true);
                ViewPagerFixed.this.onScrollEnd();
                ViewPagerFixed.this.notificationsLocker.unlock();
            }
        });
        this.manualScrolling.setDuration(540L);
        this.manualScrolling.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.manualScrolling.start();
        return true;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        this.viewTypes[0] = adapter.getItemViewType(this.currentPosition);
        this.viewPages[0] = adapter.createView(this.viewTypes[0]);
        if (this.viewPages[0] == null && this.currentPosition != 0) {
            this.currentPosition = 0;
            this.viewTypes[0] = adapter.getItemViewType(0);
            this.viewPages[0] = adapter.createView(this.viewTypes[0]);
        }
        adapter.bindView(this.viewPages[0], this.currentPosition, this.viewTypes[0]);
        addView(this.viewPages[0]);
        this.viewPages[0].setVisibility(0);
        fillTabs(false);
    }

    public void setAllowDisallowInterceptTouch(boolean z) {
        this.allowDisallowInterceptTouch = z;
    }

    public void setPosition(int i) {
        if (this.adapter == null) {
            this.currentPosition = i;
            onTabAnimationUpdate(false);
        }
        AnimatorSet animatorSet = this.tabsAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        View view = this.viewPages[1];
        if (view != null) {
            this.viewsByType.put(this.viewTypes[1], view);
            removeView(this.viewPages[1]);
            this.viewPages[1] = null;
        }
        int i2 = this.currentPosition;
        if (i2 != i) {
            this.currentPosition = i;
            this.nextPosition = 0;
            this.currentProgress = 1.0f;
            View view2 = this.viewPages[0];
            updateViewForIndex(0);
            onItemSelected(this.viewPages[0], view2, this.currentPosition, i2);
            this.viewPages[0].setTranslationX(0.0f);
            TabsView tabsView = this.tabsView;
            if (tabsView != null) {
                tabsView.selectTab(this.currentPosition, this.nextPosition, this.currentProgress);
            }
            onTabAnimationUpdate(true);
        }
    }

    protected int tabMarginDp() {
        return 16;
    }

    public void updateCurrent() {
        if (this.viewTypes[0] != this.adapter.getItemViewType(this.currentPosition)) {
            updateViewForIndex(0);
            View view = this.viewPages[1];
            if (view != null) {
                this.viewsByType.put(this.viewTypes[1], view);
                removeView(this.viewPages[1]);
                this.viewPages[1] = null;
            }
            this.viewPages[0].setTranslationX(0.0f);
            onTabAnimationUpdate(true);
        }
    }
}
