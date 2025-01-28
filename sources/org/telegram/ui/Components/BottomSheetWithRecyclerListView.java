package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;

public abstract class BottomSheetWithRecyclerListView extends BottomSheet {
    protected ActionBar actionBar;
    protected boolean actionBarIgnoreTouchEvents;
    protected AnimatedFloat actionBarSlideProgress;
    private ActionBarType actionBarType;
    private BaseFragment baseFragment;
    protected boolean clipToActionBar;
    protected int contentHeight;
    EditTextEmoji editTextEmoji;
    protected boolean handleOffset;
    private RectF handleRect;
    public final boolean hasFixedSize;
    protected int headerHeight;
    protected int headerPaddingBottom;
    protected int headerPaddingTop;
    private final Drawable headerShadowDrawable;
    protected int headerTotalHeight;
    protected LinearLayoutManager layoutManager;
    public NestedSizeNotifierLayout nestedSizeNotifierLayout;
    protected RecyclerListView recyclerListView;
    private boolean restore;
    public boolean reverseLayout;
    private int savedScrollOffset;
    private int savedScrollPosition;
    private float shadowAlpha;
    private boolean showHandle;
    boolean showShadow;
    public final boolean stackFromEnd;
    protected boolean takeTranslationIntoAccount;
    public float topPadding;
    boolean wasDrawn;

    public enum ActionBarType {
        FADING,
        SLIDING
    }

    public BottomSheetWithRecyclerListView(Context context, BaseFragment baseFragment, boolean z, boolean z2, boolean z3, Theme.ResourcesProvider resourcesProvider) {
        this(context, baseFragment, z, z2, z3, ActionBarType.FADING, resourcesProvider);
    }

    public BottomSheetWithRecyclerListView(Context context, BaseFragment baseFragment, boolean z, boolean z2, boolean z3, ActionBarType actionBarType, Theme.ResourcesProvider resourcesProvider) {
        this(context, baseFragment, z, z2, z3, false, actionBarType, resourcesProvider);
    }

    public BottomSheetWithRecyclerListView(Context context, BaseFragment baseFragment, boolean z, final boolean z2, boolean z3, final boolean z4, ActionBarType actionBarType, Theme.ResourcesProvider resourcesProvider) {
        super(context, z, resourcesProvider);
        final SizeNotifierFrameLayout sizeNotifierFrameLayout;
        this.topPadding = 0.4f;
        this.showShadow = true;
        this.shadowAlpha = 1.0f;
        this.showHandle = false;
        this.handleRect = new RectF();
        this.actionBarType = ActionBarType.FADING;
        this.headerTotalHeight = 0;
        this.headerHeight = 0;
        this.headerPaddingTop = 0;
        this.headerPaddingBottom = 0;
        this.actionBarIgnoreTouchEvents = false;
        this.takeTranslationIntoAccount = false;
        this.savedScrollPosition = -1;
        this.baseFragment = baseFragment;
        this.hasFixedSize = z2;
        this.stackFromEnd = z4;
        this.headerShadowDrawable = ContextCompat.getDrawable(context, R.drawable.header_shadow).mutate();
        if (z3) {
            NestedSizeNotifierLayout nestedSizeNotifierLayout = new NestedSizeNotifierLayout(context) {
                @Override
                public void dispatchDraw(Canvas canvas) {
                    BottomSheetWithRecyclerListView.this.preDrawInternal(canvas, this);
                    super.dispatchDraw(canvas);
                    BottomSheetWithRecyclerListView.this.postDrawInternal(canvas, this);
                }

                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0 && motionEvent.getY() < ((BottomSheet) BottomSheetWithRecyclerListView.this).shadowDrawable.getBounds().top) {
                        BottomSheetWithRecyclerListView.this.lambda$new$0();
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                @Override
                protected boolean drawChild(Canvas canvas, View view, long j) {
                    if (!z2) {
                        BottomSheetWithRecyclerListView bottomSheetWithRecyclerListView = BottomSheetWithRecyclerListView.this;
                        if (bottomSheetWithRecyclerListView.clipToActionBar && view == bottomSheetWithRecyclerListView.recyclerListView) {
                            canvas.save();
                            canvas.clipRect(0, BottomSheetWithRecyclerListView.this.actionBar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                            super.drawChild(canvas, view, j);
                            canvas.restore();
                            return true;
                        }
                    }
                    return super.drawChild(canvas, view, j);
                }

                @Override
                public void onMeasure(int i, int i2) {
                    BottomSheetWithRecyclerListView.this.contentHeight = View.MeasureSpec.getSize(i2);
                    BottomSheetWithRecyclerListView.this.onPreMeasure(i, i2);
                    if (z4) {
                        i2 = View.MeasureSpec.makeMeasureSpec(BottomSheetWithRecyclerListView.this.contentHeight, 1073741824);
                    }
                    super.onMeasure(i, i2);
                }
            };
            this.nestedSizeNotifierLayout = nestedSizeNotifierLayout;
            sizeNotifierFrameLayout = nestedSizeNotifierLayout;
        } else {
            sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
                private boolean ignoreLayout = false;

                private void onMeasureInternal(int i, int i2) {
                    int makeMeasureSpec;
                    int paddingTop;
                    EditTextEmoji editTextEmoji;
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    setMeasuredDimension(size, size2);
                    EditTextEmoji editTextEmoji2 = BottomSheetWithRecyclerListView.this.editTextEmoji;
                    if (editTextEmoji2 != null && !editTextEmoji2.isWaitingForKeyboardOpen() && AndroidUtilities.dp(20.0f) >= 0 && !BottomSheetWithRecyclerListView.this.editTextEmoji.isPopupShowing() && !BottomSheetWithRecyclerListView.this.editTextEmoji.isAnimatePopupClosing()) {
                        this.ignoreLayout = true;
                        BottomSheetWithRecyclerListView.this.editTextEmoji.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    if (AndroidUtilities.dp(20.0f) >= 0) {
                        int emojiPadding = (((BottomSheet) BottomSheetWithRecyclerListView.this).keyboardVisible || (editTextEmoji = BottomSheetWithRecyclerListView.this.editTextEmoji) == null) ? 0 : editTextEmoji.getEmojiPadding();
                        if (!AndroidUtilities.isInMultiwindow) {
                            size2 -= emojiPadding;
                            i2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
                        }
                    }
                    int childCount = getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = getChildAt(i3);
                        if (childAt != null && childAt.getVisibility() != 8) {
                            EditTextEmoji editTextEmoji3 = BottomSheetWithRecyclerListView.this.editTextEmoji;
                            if (editTextEmoji3 == null || !editTextEmoji3.isPopupView(childAt)) {
                                measureChildWithMargins(childAt, i, 0, i2, 0);
                            } else {
                                if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                    paddingTop = childAt.getLayoutParams().height;
                                } else if (AndroidUtilities.isTablet()) {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                    paddingTop = Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop());
                                } else {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                    paddingTop = (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop();
                                }
                                childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
                            }
                        }
                    }
                }

                @Override
                public void dispatchDraw(Canvas canvas) {
                    BottomSheetWithRecyclerListView.this.preDrawInternal(canvas, this);
                    super.dispatchDraw(canvas);
                    BottomSheetWithRecyclerListView.this.postDrawInternal(canvas, this);
                }

                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0 && motionEvent.getY() < ((BottomSheet) BottomSheetWithRecyclerListView.this).shadowDrawable.getBounds().top) {
                        BottomSheetWithRecyclerListView.this.lambda$new$0();
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                @Override
                protected boolean drawChild(Canvas canvas, View view, long j) {
                    if (!z2) {
                        BottomSheetWithRecyclerListView bottomSheetWithRecyclerListView = BottomSheetWithRecyclerListView.this;
                        if (bottomSheetWithRecyclerListView.clipToActionBar && view == bottomSheetWithRecyclerListView.recyclerListView) {
                            canvas.save();
                            canvas.clipRect(0, BottomSheetWithRecyclerListView.this.actionBar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                            super.drawChild(canvas, view, j);
                            canvas.restore();
                            return true;
                        }
                    }
                    return super.drawChild(canvas, view, j);
                }

                @Override
                public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BottomSheetWithRecyclerListView.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    BottomSheetWithRecyclerListView.this.contentHeight = View.MeasureSpec.getSize(i2);
                    BottomSheetWithRecyclerListView.this.onPreMeasure(i, i2);
                    if (z4) {
                        i2 = View.MeasureSpec.makeMeasureSpec(BottomSheetWithRecyclerListView.this.contentHeight, 1073741824);
                    }
                    if (BottomSheetWithRecyclerListView.this.editTextEmoji != null) {
                        onMeasureInternal(i, i2);
                    } else {
                        super.onMeasure(i, i2);
                    }
                }
            };
        }
        this.recyclerListView = new RecyclerListView(context, resourcesProvider) {
            @Override
            public boolean canHighlightChildAt(View view, float f, float f2) {
                return BottomSheetWithRecyclerListView.this.canHighlightChildAt(view, f, f2);
            }

            @Override
            public void onLayout(boolean z5, int i, int i2, int i3, int i4) {
                BottomSheetWithRecyclerListView.this.applyScrolledPosition();
                super.onLayout(z5, i, i2, i3, i4);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        if (z4) {
            linearLayoutManager.setStackFromEnd(true);
        }
        this.recyclerListView.setLayoutManager(this.layoutManager);
        NestedSizeNotifierLayout nestedSizeNotifierLayout2 = this.nestedSizeNotifierLayout;
        if (nestedSizeNotifierLayout2 != null) {
            nestedSizeNotifierLayout2.setBottomSheetContainerView(getContainer());
            this.nestedSizeNotifierLayout.setTargetListView(this.recyclerListView);
        }
        if (z2) {
            this.recyclerListView.setHasFixedSize(true);
            RecyclerListView recyclerListView = this.recyclerListView;
            recyclerListView.setAdapter(createAdapter(recyclerListView));
            setCustomView(sizeNotifierFrameLayout);
            sizeNotifierFrameLayout.addView(this.recyclerListView, LayoutHelper.createFrame(-1, -2.0f));
        } else {
            resetAdapter(context);
            this.containerView = sizeNotifierFrameLayout;
            ActionBar actionBar = new ActionBar(context) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (BottomSheetWithRecyclerListView.this.actionBarIgnoreTouchEvents) {
                        return false;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                @Override
                public void setAlpha(float f) {
                    if (getAlpha() != f) {
                        super.setAlpha(f);
                        sizeNotifierFrameLayout.invalidate();
                    }
                }

                @Override
                public void setTag(Object obj) {
                    super.setTag(obj);
                    BottomSheetWithRecyclerListView.this.updateStatusBar();
                }
            };
            this.actionBar = actionBar;
            actionBar.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
            this.actionBar.setTitleColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), false);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setItemsColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), false);
            this.actionBar.setCastShadows(true);
            this.actionBar.setTitle(getTitle());
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int i) {
                    if (i == -1) {
                        BottomSheetWithRecyclerListView.this.lambda$new$0();
                    }
                }
            });
            sizeNotifierFrameLayout.addView(this.recyclerListView);
            sizeNotifierFrameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f, 0, 6.0f, 0.0f, 6.0f, 0.0f));
            this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    sizeNotifierFrameLayout.invalidate();
                }
            });
        }
        if (actionBarType == ActionBarType.SLIDING) {
            setSlidingActionBar();
        }
        onViewCreated(sizeNotifierFrameLayout);
        updateStatusBar();
    }

    public BottomSheetWithRecyclerListView(BaseFragment baseFragment, boolean z, boolean z2) {
        this(baseFragment, z, z2, false, baseFragment == null ? null : baseFragment.getResourceProvider());
    }

    public BottomSheetWithRecyclerListView(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, Theme.ResourcesProvider resourcesProvider) {
        this(baseFragment.getParentActivity(), baseFragment, z, z2, z3, resourcesProvider);
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider)) > 0.699999988079071d;
    }

    public void updateStatusBar() {
        Window window;
        boolean isLightStatusBar;
        if (this.attachedFragment != null) {
            LaunchActivity.instance.checkSystemBarColors(true, true, true, false);
            return;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && actionBar.getTag() != null) {
            window = getWindow();
            isLightStatusBar = isLightStatusBar();
        } else {
            if (this.baseFragment == null) {
                return;
            }
            window = getWindow();
            isLightStatusBar = this.baseFragment.isLightStatusBar();
        }
        AndroidUtilities.setLightStatusBar(window, isLightStatusBar);
    }

    public void applyScrolledPosition() {
        applyScrolledPosition(false);
    }

    public void applyScrolledPosition(boolean z) {
        RecyclerListView recyclerListView = this.recyclerListView;
        if (recyclerListView == null || recyclerListView.getLayoutManager() == null || this.savedScrollPosition < 0) {
            return;
        }
        int top = (this.savedScrollOffset - this.containerView.getTop()) - this.recyclerListView.getPaddingTop();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.recyclerListView.findViewHolderForAdapterPosition(0);
        if (z && findViewHolderForAdapterPosition != null) {
            top -= Math.max(findViewHolderForAdapterPosition.itemView.getBottom() - this.recyclerListView.getPaddingTop(), 0);
        }
        if (this.recyclerListView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.savedScrollPosition, top);
        }
        this.savedScrollPosition = -1;
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    protected boolean canHighlightChildAt(View view, float f, float f2) {
        return true;
    }

    protected abstract RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView);

    protected int getActionBarProgressHeight() {
        return AndroidUtilities.dp(56.0f);
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    protected abstract CharSequence getTitle();

    @Override
    public boolean isAttachedLightStatusBar() {
        BaseFragment baseFragment;
        ActionBar actionBar = this.actionBar;
        if ((actionBar == null || actionBar.getTag() == null) && (baseFragment = this.baseFragment) != null) {
            return baseFragment.isLightStatusBar();
        }
        return isLightStatusBar();
    }

    protected boolean needPaddingShadow() {
        return true;
    }

    public void notifyDataSetChanged() {
        this.recyclerListView.getAdapter().notifyDataSetChanged();
    }

    protected void onPreDraw(Canvas canvas, int i, float f) {
    }

    public void onPreMeasure(int i, int i2) {
    }

    public void onViewCreated(FrameLayout frameLayout) {
    }

    public void postDrawInternal(android.graphics.Canvas r8, android.view.View r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BottomSheetWithRecyclerListView.postDrawInternal(android.graphics.Canvas, android.view.View):void");
    }

    public void preDrawInternal(Canvas canvas, View view) {
        int i;
        float f;
        ActionBar actionBar;
        Integer num;
        this.restore = false;
        if (this.hasFixedSize) {
            return;
        }
        if (this.reverseLayout) {
            int height = this.recyclerListView.getHeight();
            for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
                View childAt = this.recyclerListView.getChildAt(i2);
                int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt);
                if (childAdapterPosition != -1 && childAdapterPosition != this.recyclerListView.getAdapter().getItemCount() - 1) {
                    height = Math.min(height, childAt.getTop() + (this.takeTranslationIntoAccount ? (int) childAt.getTranslationY() : 0));
                }
            }
            i = height - AndroidUtilities.dp(16.0f);
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.recyclerListView.findViewHolderForAdapterPosition(0);
            int i3 = -AndroidUtilities.dp(16.0f);
            if (findViewHolderForAdapterPosition != null) {
                i3 = findViewHolderForAdapterPosition.itemView.getBottom() - AndroidUtilities.dp(16.0f);
                if (this.takeTranslationIntoAccount) {
                    i = ((int) findViewHolderForAdapterPosition.itemView.getTranslationY()) + i3;
                }
            }
            i = i3;
        }
        int i4 = i - ((this.headerHeight + this.headerPaddingTop) + this.headerPaddingBottom);
        if (this.showHandle && this.handleOffset) {
            i4 -= AndroidUtilities.dp(this.actionBarType == ActionBarType.SLIDING ? 8.0f : 16.0f);
        }
        ActionBarType actionBarType = this.actionBarType;
        float f2 = 1.0f;
        if (actionBarType == ActionBarType.FADING) {
            f = 1.0f - ((AndroidUtilities.dp(16.0f) + i4) / getActionBarProgressHeight());
            if (f < 0.0f) {
                f = 0.0f;
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.actionBar, f != 0.0f, 1.0f, this.wasDrawn);
        } else if (actionBarType == ActionBarType.SLIDING) {
            float max = Math.max(((AndroidUtilities.dp(8.0f) + i4) + this.headerPaddingTop) - AndroidUtilities.statusBarHeight, 0.0f);
            float f3 = this.actionBarSlideProgress.set(max == 0.0f ? 1.0f : 0.0f);
            if (f3 != 0.0f && f3 != 1.0f) {
                canvas.save();
                canvas.clipRect(0.0f, max, this.containerView.getMeasuredWidth(), this.containerView.getMeasuredHeight());
                this.restore = true;
            }
            this.shadowAlpha = f3;
            f2 = AndroidUtilities.lerp(1.0f, 0.5f, f3);
            this.actionBar.backButtonImageView.setAlpha(f3);
            this.actionBar.backButtonImageView.setScaleX(f3);
            this.actionBar.backButtonImageView.setPivotY(r6.getMeasuredHeight() / 2.0f);
            this.actionBar.backButtonImageView.setScaleY(f3);
            this.actionBar.getTitleTextView().setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dp(21.0f) - r6.getLeft(), 0.0f, f3));
            this.actionBar.setTranslationY(max);
            i4 -= AndroidUtilities.lerp(0, (((this.headerTotalHeight - this.headerHeight) - this.headerPaddingTop) - this.headerPaddingBottom) + AndroidUtilities.dp(13.0f), f3);
            this.actionBar.getBackground().setBounds(0, AndroidUtilities.lerp(this.actionBar.getHeight(), 0, f3), this.actionBar.getWidth(), this.actionBar.getHeight());
            if (f3 > 0.5f) {
                if (this.actionBarIgnoreTouchEvents) {
                    this.actionBarIgnoreTouchEvents = false;
                    actionBar = this.actionBar;
                    num = 1;
                    actionBar.setTag(num);
                }
                f = f3;
            } else {
                if (!this.actionBarIgnoreTouchEvents) {
                    this.actionBarIgnoreTouchEvents = true;
                    actionBar = this.actionBar;
                    num = null;
                    actionBar.setTag(num);
                }
                f = f3;
            }
        } else {
            f = 0.0f;
        }
        if (shouldDrawBackground()) {
            if (needPaddingShadow()) {
                this.shadowDrawable.setBounds(0, i4, view.getMeasuredWidth(), view.getMeasuredHeight());
            } else {
                this.shadowDrawable.setBounds(-AndroidUtilities.dp(6.0f), i4, view.getMeasuredWidth() + AndroidUtilities.dp(6.0f), view.getMeasuredHeight());
            }
            this.shadowDrawable.draw(canvas);
            if (this.showHandle && f2 > 0.0f) {
                int dp = AndroidUtilities.dp(36.0f);
                this.handleRect.set((view.getMeasuredWidth() - dp) / 2.0f, AndroidUtilities.dp(20.0f) + i4, (view.getMeasuredWidth() + dp) / 2.0f, r3 + AndroidUtilities.dp(4.0f));
                Theme.dialogs_onlineCirclePaint.setColor(getThemedColor(Theme.key_sheet_scrollUp));
                Theme.dialogs_onlineCirclePaint.setAlpha((int) (r14.getAlpha() * f2));
                canvas.drawRoundRect(this.handleRect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
            }
        }
        onPreDraw(canvas, i4, f);
    }

    protected void resetAdapter(final Context context) {
        final RecyclerListView.SelectionAdapter createAdapter = createAdapter(this.recyclerListView);
        this.recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                return createAdapter.getItemCount() + 1;
            }

            @Override
            public int getItemViewType(int i) {
                if (i == (BottomSheetWithRecyclerListView.this.reverseLayout ? getItemCount() - 1 : 0)) {
                    return -1000;
                }
                return createAdapter.getItemViewType(i - (!BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0));
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return createAdapter.isEnabled(viewHolder);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (i != (BottomSheetWithRecyclerListView.this.reverseLayout ? getItemCount() - 1 : 0)) {
                    createAdapter.onBindViewHolder(viewHolder, i - (!BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0));
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return i == -1000 ? new RecyclerListView.Holder(new View(context) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        BottomSheetWithRecyclerListView bottomSheetWithRecyclerListView = BottomSheetWithRecyclerListView.this;
                        int i4 = bottomSheetWithRecyclerListView.contentHeight;
                        int dp = i4 == 0 ? AndroidUtilities.dp(300.0f) : (int) (i4 * bottomSheetWithRecyclerListView.topPadding);
                        BottomSheetWithRecyclerListView bottomSheetWithRecyclerListView2 = BottomSheetWithRecyclerListView.this;
                        int i5 = dp - (((bottomSheetWithRecyclerListView2.headerTotalHeight - bottomSheetWithRecyclerListView2.headerHeight) - bottomSheetWithRecyclerListView2.headerPaddingTop) - bottomSheetWithRecyclerListView2.headerPaddingBottom);
                        if (i5 < 1) {
                            i5 = 1;
                        }
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(i5, 1073741824));
                    }

                    @Override
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ((BottomSheet) BottomSheetWithRecyclerListView.this).containerView.invalidate();
                    }
                }) : createAdapter.onCreateViewHolder(viewGroup, i);
            }

            @Override
            public void registerAdapterDataObserver(final RecyclerView.AdapterDataObserver adapterDataObserver) {
                createAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        adapterDataObserver.onChanged();
                    }

                    @Override
                    public void onItemRangeChanged(int i, int i2) {
                        adapterDataObserver.onItemRangeChanged(i + (!BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0), i2);
                    }

                    @Override
                    public void onItemRangeChanged(int i, int i2, Object obj) {
                        adapterDataObserver.onItemRangeChanged(i + (!BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0), i2, obj);
                    }

                    @Override
                    public void onItemRangeInserted(int i, int i2) {
                        adapterDataObserver.onItemRangeInserted(i + (!BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0), i2);
                    }

                    @Override
                    public void onItemRangeMoved(int i, int i2, int i3) {
                        RecyclerView.AdapterDataObserver adapterDataObserver2 = adapterDataObserver;
                        int i4 = !BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0;
                        adapterDataObserver2.onItemRangeMoved(i + i4, i2 + i4, i3);
                    }

                    @Override
                    public void onItemRangeRemoved(int i, int i2) {
                        adapterDataObserver.onItemRangeRemoved(i + (!BottomSheetWithRecyclerListView.this.reverseLayout ? 1 : 0), i2);
                    }
                });
            }
        });
    }

    public void saveScrollPosition() {
        RecyclerListView recyclerListView = this.recyclerListView;
        if (recyclerListView == null || this.layoutManager == null || recyclerListView.getChildCount() <= 0) {
            return;
        }
        View view = null;
        int i = -1;
        int i2 = Integer.MAX_VALUE;
        for (int i3 = 0; i3 < this.recyclerListView.getChildCount(); i3++) {
            View childAt = this.recyclerListView.getChildAt(i3);
            int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt);
            if (childAdapterPosition > 0 && childAt.getTop() < i2) {
                i2 = childAt.getTop();
                view = childAt;
                i = childAdapterPosition;
            }
        }
        if (view != null) {
            this.savedScrollPosition = i;
            this.savedScrollOffset = view.getTop() + this.containerView.getTop();
            smoothContainerViewLayout();
        }
    }

    public void setEditTextEmoji(EditTextEmoji editTextEmoji) {
        this.editTextEmoji = editTextEmoji;
    }

    public void setShowHandle(boolean z) {
        this.showHandle = z;
    }

    public void setShowShadow(boolean z) {
        this.showShadow = z;
        this.nestedSizeNotifierLayout.invalidate();
    }

    public void setSlidingActionBar() {
        if (this.hasFixedSize) {
            return;
        }
        this.actionBarType = ActionBarType.SLIDING;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
        this.headerHeight = currentActionBarHeight;
        this.headerTotalHeight = currentActionBarHeight + AndroidUtilities.statusBarHeight;
        this.headerPaddingTop = AndroidUtilities.dp(16.0f);
        this.headerPaddingBottom = AndroidUtilities.dp(-20.0f);
        this.actionBarSlideProgress = new AnimatedFloat(this.containerView, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.actionBar.backButtonImageView.setPivotX(0.0f);
        this.recyclerListView.setClipToPadding(true);
    }

    protected boolean shouldDrawBackground() {
        return true;
    }

    public void updateTitle() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
        }
    }

    public void updateTitleAnimated() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setTitleAnimated(getTitle(), false, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }
    }
}
