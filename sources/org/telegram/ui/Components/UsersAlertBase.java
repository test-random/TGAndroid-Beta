package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UsersAlertBase;

public abstract class UsersAlertBase extends BottomSheet {
    public static final Property COLOR_PROGRESS = new AnimationProperties.FloatProperty("colorProgress") {
        @Override
        public Float get(UsersAlertBase usersAlertBase) {
            return Float.valueOf(usersAlertBase.getColorProgress());
        }

        @Override
        public void setValue(UsersAlertBase usersAlertBase, float f) {
            usersAlertBase.setColorProgress(f);
        }
    };
    private int backgroundColor;
    private float colorProgress;
    private boolean drawTitle;
    protected StickerEmptyView emptyView;
    protected FlickerLoadingView flickerLoadingView;
    protected FrameLayout frameLayout;
    protected boolean isEmptyViewVisible;
    protected int keyActionBarUnscrolled;
    protected int keyInviteMembersBackground;
    protected int keyLastSeenText;
    protected int keyLastSeenTextUnscrolled;
    protected int keyListSelector;
    protected int keyListViewBackground;
    protected int keyNameText;
    protected int keyScrollUp;
    protected int keySearchBackground;
    protected int keySearchIcon;
    protected int keySearchIconUnscrolled;
    protected int keySearchPlaceholder;
    protected int keySearchText;
    protected final FillLastLinearLayoutManager layoutManager;
    protected RecyclerListView listView;
    protected RecyclerView.Adapter listViewAdapter;
    protected boolean needSnapToTop;
    private RectF rect;
    protected int scrollOffsetY;
    protected RecyclerView.Adapter searchListViewAdapter;
    protected SearchField searchView;
    protected View shadow;
    protected AnimatorSet shadowAnimation;
    protected Drawable shadowDrawable;
    private TextView titleView;

    public class ContainerView extends FrameLayout {
        private boolean ignoreLayout;
        float snapToTopOffset;
        private Boolean statusBarOpen;
        ValueAnimator valueAnimator;

        public ContainerView(Context context) {
            super(context);
            this.ignoreLayout = false;
        }

        public void lambda$onMeasure$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.snapToTopOffset = floatValue;
            setTranslationY(floatValue);
        }

        private void updateLightStatusBar(boolean z) {
            Boolean bool = this.statusBarOpen;
            if (bool == null || bool.booleanValue() != z) {
                boolean z2 = AndroidUtilities.computePerceivedBrightness(UsersAlertBase.this.getThemedColor(Theme.key_dialogBackground)) > 0.721f;
                boolean z3 = AndroidUtilities.computePerceivedBrightness(Theme.blendOver(UsersAlertBase.this.getThemedColor(Theme.key_actionBarDefault), 855638016)) > 0.721f;
                this.statusBarOpen = Boolean.valueOf(z);
                if (!z) {
                    z2 = z3;
                }
                AndroidUtilities.setLightStatusBar(UsersAlertBase.this.getWindow(), z2);
            }
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0, getPaddingTop(), getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override
        protected void onDraw(android.graphics.Canvas r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UsersAlertBase.ContainerView.onDraw(android.graphics.Canvas):void");
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                float y = motionEvent.getY();
                UsersAlertBase usersAlertBase = UsersAlertBase.this;
                if (y < usersAlertBase.scrollOffsetY) {
                    usersAlertBase.dismiss();
                    return true;
                }
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            UsersAlertBase.this.updateLayout();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int measurePadding;
            int size = View.MeasureSpec.getSize(i2);
            if (Build.VERSION.SDK_INT >= 21) {
                this.ignoreLayout = true;
                setPadding(((BottomSheet) UsersAlertBase.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) UsersAlertBase.this).backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int paddingTop = size - getPaddingTop();
            if (((BottomSheet) UsersAlertBase.this).keyboardVisible) {
                measurePadding = AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(false);
                int i3 = UsersAlertBase.this.scrollOffsetY;
                if (i3 != 0) {
                    float f = i3;
                    this.snapToTopOffset = f;
                    setTranslationY(f);
                    ValueAnimator valueAnimator = this.valueAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        this.valueAnimator.cancel();
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(this.snapToTopOffset, 0.0f);
                    this.valueAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            UsersAlertBase.ContainerView.this.lambda$onMeasure$0(valueAnimator2);
                        }
                    });
                    this.valueAnimator.setDuration(250L);
                    this.valueAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            ContainerView containerView = ContainerView.this;
                            containerView.snapToTopOffset = 0.0f;
                            containerView.setTranslationY(0.0f);
                            ContainerView.this.valueAnimator = null;
                        }
                    });
                    this.valueAnimator.start();
                } else if (this.valueAnimator != null) {
                    setTranslationY(this.snapToTopOffset);
                }
            } else {
                measurePadding = UsersAlertBase.this.measurePadding(paddingTop);
                UsersAlertBase.this.setAllowNestedScroll(true);
            }
            if (UsersAlertBase.this.listView.getPaddingTop() != measurePadding) {
                this.ignoreLayout = true;
                UsersAlertBase.this.listView.setPadding(0, measurePadding, 0, 0);
                this.ignoreLayout = false;
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, 1073741824));
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !UsersAlertBase.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            invalidate();
        }
    }

    public class SearchField extends FrameLayout {
        private final ImageView clearSearchImageView;
        private final CloseProgressDrawable2 progressDrawable;
        private final View searchBackground;
        protected EditTextBoldCursor searchEditText;
        private final ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor(UsersAlertBase.this.keySearchBackground, ((BottomSheet) UsersAlertBase.this).resourcesProvider)));
            addView(view, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            imageView.setImageResource(R.drawable.smiles_inputsearch);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder, ((BottomSheet) UsersAlertBase.this).resourcesProvider), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(scaleType);
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() {
                @Override
                protected int getCurrentColor() {
                    return Theme.getColor(UsersAlertBase.this.keySearchPlaceholder);
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView2.setImageDrawable(closeProgressDrawable2);
            closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
            imageView2.setScaleX(0.1f);
            imageView2.setScaleY(0.1f);
            imageView2.setAlpha(0.0f);
            addView(imageView2, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    UsersAlertBase.SearchField.this.lambda$new$0(view2);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - UsersAlertBase.this.listView.getMeasuredHeight());
                    if (obtain.getAction() == 1) {
                        obtain.setAction(3);
                    }
                    UsersAlertBase.this.listView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder));
            this.searchEditText.setTextColor(Theme.getColor(UsersAlertBase.this.keySearchText));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            this.searchEditText.setHint(LocaleController.getString(R.string.VoipGroupSearchMembers));
            this.searchEditText.setCursorColor(Theme.getColor(UsersAlertBase.this.keySearchText));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    RecyclerListView recyclerListView;
                    boolean z = SearchField.this.searchEditText.length() > 0;
                    if (z != (SearchField.this.clearSearchImageView.getAlpha() != 0.0f)) {
                        SearchField.this.clearSearchImageView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).scaleX(z ? 1.0f : 0.1f).scaleY(z ? 1.0f : 0.1f).start();
                    }
                    String obj = SearchField.this.searchEditText.getText().toString();
                    int itemCount = UsersAlertBase.this.listView.getAdapter() == null ? 0 : UsersAlertBase.this.listView.getAdapter().getItemCount();
                    UsersAlertBase.this.search(obj);
                    if (TextUtils.isEmpty(obj) && (recyclerListView = UsersAlertBase.this.listView) != null) {
                        RecyclerView.Adapter adapter = recyclerListView.getAdapter();
                        UsersAlertBase usersAlertBase = UsersAlertBase.this;
                        if (adapter != usersAlertBase.listViewAdapter) {
                            usersAlertBase.listView.setAnimateEmptyView(false, 0);
                            UsersAlertBase usersAlertBase2 = UsersAlertBase.this;
                            usersAlertBase2.listView.setAdapter(usersAlertBase2.listViewAdapter);
                            UsersAlertBase.this.listView.setAnimateEmptyView(true, 0);
                            if (itemCount == 0) {
                                UsersAlertBase.this.showItemsAnimated(0);
                            }
                        }
                    }
                    UsersAlertBase.this.flickerLoadingView.setVisibility(0);
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    boolean lambda$new$1;
                    lambda$new$1 = UsersAlertBase.SearchField.this.lambda$new$1(textView, i, keyEvent);
                    return lambda$new$1;
                }
            });
        }

        public void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void closeSearch() {
            this.clearSearchImageView.callOnClick();
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            UsersAlertBase.this.onSearchViewTouched(motionEvent, this.searchEditText);
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    public UsersAlertBase(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context, z, resourcesProvider);
        this.rect = new RectF();
        this.needSnapToTop = true;
        this.isEmptyViewVisible = true;
        this.keyScrollUp = Theme.key_sheet_scrollUp;
        this.keyListSelector = Theme.key_listSelector;
        this.keySearchBackground = Theme.key_dialogSearchBackground;
        int i2 = Theme.key_windowBackgroundWhite;
        this.keyInviteMembersBackground = i2;
        this.keyListViewBackground = i2;
        this.keyActionBarUnscrolled = i2;
        this.keyNameText = Theme.key_windowBackgroundWhiteBlackText;
        int i3 = Theme.key_windowBackgroundWhiteGrayText;
        this.keyLastSeenText = i3;
        this.keyLastSeenTextUnscrolled = i3;
        this.keySearchPlaceholder = Theme.key_dialogSearchHint;
        this.keySearchText = Theme.key_dialogSearchText;
        int i4 = Theme.key_dialogSearchIcon;
        this.keySearchIcon = i4;
        this.keySearchIconUnscrolled = i4;
        this.drawTitle = true;
        this.resourcesProvider = resourcesProvider;
        updateColorKeys();
        setDimBehindAlpha(75);
        this.currentAccount = i;
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        ContainerView createContainerView = createContainerView(context);
        this.containerView = createContainerView;
        createContainerView.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        ViewGroup viewGroup = this.containerView;
        int i5 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i5, 0, i5, 0);
        this.frameLayout = new FrameLayout(context);
        SearchField searchField = new SearchField(context);
        this.searchView = searchField;
        this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 51));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        this.flickerLoadingView.setColors(this.keyInviteMembersBackground, this.keySearchBackground, this.keyActionBarUnscrolled);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(this.flickerLoadingView, 0, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 2.0f, 0.0f, 0.0f));
        this.emptyView.title.setText(LocaleController.getString(R.string.NoResult));
        this.emptyView.subtitle.setText(LocaleController.getString(R.string.SearchEmptyViewFilteredSubtitle2));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        this.emptyView.setColors(this.keyNameText, this.keyLastSeenText, this.keyInviteMembersBackground, this.keySearchBackground);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) {
            @Override
            public boolean emptyViewIsVisible() {
                return getAdapter() != null && UsersAlertBase.this.isEmptyViewVisible && getAdapter().getItemCount() <= 2;
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                getLocationInWindow(new int[2]);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(2);
        this.listView.setTag(13);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHideIfEmpty(false);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.keyListSelector, resourcesProvider));
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(8.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        fillLastLinearLayoutManager.setBind(false);
        this.listView.setLayoutManager(fillLastLinearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i6) {
                RecyclerListView.Holder holder;
                if (i6 == 0) {
                    UsersAlertBase usersAlertBase = UsersAlertBase.this;
                    if (!usersAlertBase.needSnapToTop || usersAlertBase.scrollOffsetY + ((BottomSheet) usersAlertBase).backgroundPaddingTop + AndroidUtilities.dp(13.0f) >= AndroidUtilities.statusBarHeight * 2 || !UsersAlertBase.this.listView.canScrollVertically(1) || (holder = (RecyclerListView.Holder) UsersAlertBase.this.listView.findViewHolderForAdapterPosition(0)) == null || holder.itemView.getTop() <= 0) {
                        return;
                    }
                    UsersAlertBase.this.listView.smoothScrollBy(0, holder.itemView.getTop());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i6, int i7) {
                UsersAlertBase.this.updateLayout();
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, layoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        setColorProgress(0.0f);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
    }

    public float getColorProgress() {
        return this.colorProgress;
    }

    private void runShadowAnimation(final boolean z) {
        if ((!z || this.shadow.getTag() == null) && (z || this.shadow.getTag() != null)) {
            return;
        }
        this.shadow.setTag(z ? null : 1);
        if (z) {
            this.shadow.setVisibility(0);
        }
        AnimatorSet animatorSet = this.shadowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.shadowAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.shadow, (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.shadowAnimation.setDuration(150L);
        this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                AnimatorSet animatorSet3 = UsersAlertBase.this.shadowAnimation;
                if (animatorSet3 == null || !animatorSet3.equals(animator)) {
                    return;
                }
                UsersAlertBase.this.shadowAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatorSet animatorSet3 = UsersAlertBase.this.shadowAnimation;
                if (animatorSet3 == null || !animatorSet3.equals(animator)) {
                    return;
                }
                if (!z) {
                    UsersAlertBase.this.shadow.setVisibility(4);
                }
                UsersAlertBase.this.shadowAnimation = null;
            }
        });
        this.shadowAnimation.start();
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    protected ContainerView createContainerView(Context context) {
        return new ContainerView(context);
    }

    @Override
    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchView.searchEditText);
        super.dismiss();
    }

    protected int measurePadding(int i) {
        return (i - ((i / 5) * 3)) + AndroidUtilities.dp(8.0f);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.statusBarHeight = AndroidUtilities.getStatusBarHeight(getContext());
    }

    protected abstract void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor);

    public void search(String str) {
    }

    public void setColorProgress(float f) {
        this.colorProgress = f;
        this.backgroundColor = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyInviteMembersBackground, this.resourcesProvider), Theme.getColor(this.keyListViewBackground, this.resourcesProvider), f, 1.0f);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.frameLayout.setBackgroundColor(this.backgroundColor);
        fixNavigationBar(this.backgroundColor);
        int i = this.backgroundColor;
        this.navBarColor = i;
        this.listView.setGlowColor(i);
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyLastSeenTextUnscrolled), Theme.getColor(this.keyLastSeenText), f, 1.0f);
        int offsetColor2 = AndroidUtilities.getOffsetColor(Theme.getColor(this.keySearchIconUnscrolled), Theme.getColor(this.keySearchIcon), f, 1.0f);
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof GroupCallTextCell) {
                ((GroupCallTextCell) childAt).setColors(offsetColor, offsetColor);
            } else if (childAt instanceof GroupCallUserCell) {
                ((GroupCallUserCell) childAt).setGrayIconColor(this.shadow.getTag() != null ? this.keySearchIcon : this.keySearchIconUnscrolled, offsetColor2);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    @Override
    public void setTitle(CharSequence charSequence) {
        if (this.titleView == null) {
            TextView textView = new TextView(getContext());
            this.titleView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
            this.titleView.setTextSize(1, 20.0f);
            this.titleView.setTypeface(AndroidUtilities.bold());
            this.titleView.setLines(1);
            this.titleView.setMaxLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            this.frameLayout.addView(this.titleView, LayoutHelper.createFrame(-1, 36.0f, 51, 16.0f, 0.0f, 0.0f, 0.0f));
            ((FrameLayout.LayoutParams) this.searchView.getLayoutParams()).topMargin = AndroidUtilities.dp(30.0f);
            this.frameLayout.getLayoutParams().height = AndroidUtilities.dp(94.0f);
        }
        this.titleView.setText(charSequence);
    }

    public void setTranslationY(int i) {
        this.listView.setTopGlowOffset(i);
        float f = i;
        this.frameLayout.setTranslationY(f);
        this.emptyView.setTranslationY(f);
        this.containerView.invalidate();
    }

    public void showItemsAnimated(final int i) {
        if (isShowing()) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    UsersAlertBase.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = UsersAlertBase.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i2 = 0; i2 < childCount; i2++) {
                        View childAt = UsersAlertBase.this.listView.getChildAt(i2);
                        int childAdapterPosition = UsersAlertBase.this.listView.getChildAdapterPosition(childAt);
                        if (childAdapterPosition >= i) {
                            if (childAdapterPosition == 1 && UsersAlertBase.this.listView.getAdapter() == UsersAlertBase.this.searchListViewAdapter && (childAt instanceof GraySectionCell)) {
                                childAt = ((GraySectionCell) childAt).getTextView();
                            }
                            childAt.setAlpha(0.0f);
                            int min = (int) ((Math.min(UsersAlertBase.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop())) / UsersAlertBase.this.listView.getMeasuredHeight()) * 100.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                            ofFloat.setStartDelay(min);
                            ofFloat.setDuration(200L);
                            animatorSet.playTogether(ofFloat);
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    protected void updateColorKeys() {
    }

    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            return;
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
        int top = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() - AndroidUtilities.dp(8.0f) : 0;
        int i = (top <= 0 || findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.getAdapterPosition() != 0) {
            runShadowAnimation(true);
            top = i;
        } else {
            runShadowAnimation(false);
        }
        if (this.scrollOffsetY != top) {
            this.scrollOffsetY = top;
            setTranslationY(top);
        }
    }
}
