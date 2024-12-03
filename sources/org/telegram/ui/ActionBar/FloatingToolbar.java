package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.FloatingToolbar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public final class FloatingToolbar {
    private static final MenuItem.OnMenuItemClickListener NO_OP_MENUITEM_CLICK_LISTENER = new MenuItem.OnMenuItemClickListener() {
        @Override
        public final boolean onMenuItemClick(MenuItem menuItem) {
            boolean lambda$static$0;
            lambda$static$0 = FloatingToolbar.lambda$static$0(menuItem);
            return lambda$static$0;
        }
    };
    public static final List premiumOptions = Arrays.asList(Integer.valueOf(R.id.menu_bold), Integer.valueOf(R.id.menu_italic), Integer.valueOf(R.id.menu_strike), Integer.valueOf(R.id.menu_link), Integer.valueOf(R.id.menu_mono), Integer.valueOf(R.id.menu_underline), Integer.valueOf(R.id.menu_spoiler), Integer.valueOf(R.id.menu_quote));
    private int currentStyle;
    private Menu mMenu;
    private final FloatingToolbarPopup mPopup;
    private int mSuggestedWidth;
    private final View mWindowView;
    private Runnable premiumLockClickListener;
    private Utilities.Callback0Return quoteShowCallback;
    private final Theme.ResourcesProvider resourcesProvider;
    private final Rect mContentRect = new Rect();
    private final Rect mPreviousContentRect = new Rect();
    private List mShowingMenuItems = new ArrayList();
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
    private boolean mWidthChanged = true;
    private final View.OnLayoutChangeListener mOrientationChangeHandler = new View.OnLayoutChangeListener() {
        private final Rect mNewRect = new Rect();
        private final Rect mOldRect = new Rect();

        @Override
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            this.mNewRect.set(i, i2, i3, i4);
            this.mOldRect.set(i5, i6, i7, i8);
            if (!FloatingToolbar.this.mPopup.isShowing() || this.mNewRect.equals(this.mOldRect)) {
                return;
            }
            FloatingToolbar.this.mWidthChanged = true;
            FloatingToolbar.this.updateLayout();
        }
    };
    private final Comparator mMenuItemComparator = new Comparator() {
        @Override
        public final int compare(Object obj, Object obj2) {
            int lambda$new$1;
            lambda$new$1 = FloatingToolbar.lambda$new$1((MenuItem) obj, (MenuItem) obj2);
            return lambda$new$1;
        }
    };

    public final class FloatingToolbarPopup {
        private final Drawable mArrow;
        private final AnimationSet mCloseOverflowAnimation;
        private final ViewGroup mContentContainer;
        private final Context mContext;
        private final AnimatorSet mDismissAnimation;
        private final Interpolator mFastOutLinearInInterpolator;
        private final Interpolator mFastOutSlowInInterpolator;
        private boolean mHidden;
        private final AnimatorSet mHideAnimation;
        private final int mIconTextSpacing;
        private boolean mIsOverflowOpen;
        private final int mLineHeight;
        private final Interpolator mLinearOutSlowInInterpolator;
        private final Interpolator mLogAccelerateInterpolator;
        private final ViewGroup mMainPanel;
        private Size mMainPanelSize;
        private final int mMarginHorizontal;
        private final int mMarginVertical;
        private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
        private final AnimationSet mOpenOverflowAnimation;
        private boolean mOpenOverflowUpwards;
        private final Drawable mOverflow;
        private final FrameLayout mOverflowButton;
        private final ImageView mOverflowButtonIcon;
        private final View mOverflowButtonShadow;
        private final Size mOverflowButtonSize;
        private final TextView mOverflowButtonText;
        private final OverflowPanel mOverflowPanel;
        private Size mOverflowPanelSize;
        private final OverflowPanelViewHelper mOverflowPanelViewHelper;
        private final View mParent;
        private final PopupWindow mPopupWindow;
        private final AnimatorSet mShowAnimation;
        private final AnimatedVectorDrawable mToArrow;
        private final AnimatedVectorDrawable mToOverflow;
        private int mTransitionDurationScale;
        private final Rect mViewPortOnScreen = new Rect();
        private final Point mCoordsOnWindow = new Point();
        private final int[] mTmpCoords = new int[2];
        private final Region mTouchableRegion = new Region();
        private final Runnable mPreparePopupContentRTLHelper = new Runnable() {
            @Override
            public void run() {
                FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
                FloatingToolbarPopup.this.mContentContainer.setAlpha(1.0f);
            }
        };
        private boolean mDismissed = true;
        private final View.OnClickListener mMenuItemButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(view.getTag() instanceof MenuItem) || FloatingToolbarPopup.this.mOnMenuItemClickListener == null) {
                    return;
                }
                FloatingToolbarPopup.this.mOnMenuItemClickListener.onMenuItemClick((MenuItem) view.getTag());
            }
        };
        private int shiftDp = -4;

        public class AnonymousClass14 implements Animation.AnimationListener {
            AnonymousClass14() {
            }

            public void lambda$onAnimationEnd$0() {
                FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FloatingToolbarPopup.this.mContentContainer.post(new Runnable() {
                    @Override
                    public final void run() {
                        FloatingToolbar.FloatingToolbarPopup.AnonymousClass14.this.lambda$onAnimationEnd$0();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                FloatingToolbarPopup.this.mOverflowButton.setEnabled(false);
                FloatingToolbarPopup.this.mMainPanel.setVisibility(0);
                FloatingToolbarPopup.this.mOverflowPanel.setVisibility(0);
            }
        }

        public class AnonymousClass4 extends AnimatorListenerAdapter {
            final FloatingToolbar val$this$0;

            AnonymousClass4(FloatingToolbar floatingToolbar) {
                this.val$this$0 = floatingToolbar;
            }

            public void lambda$onAnimationEnd$0() {
                FloatingToolbarPopup.this.mPopupWindow.dismiss();
                FloatingToolbarPopup.this.mContentContainer.removeAllViews();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new Runnable() {
                    @Override
                    public final void run() {
                        FloatingToolbar.FloatingToolbarPopup.AnonymousClass4.this.lambda$onAnimationEnd$0();
                    }
                });
            }
        }

        public class AnonymousClass5 extends AnimatorListenerAdapter {
            final FloatingToolbar val$this$0;

            AnonymousClass5(FloatingToolbar floatingToolbar) {
                this.val$this$0 = floatingToolbar;
            }

            public void lambda$onAnimationEnd$0() {
                FloatingToolbarPopup.this.mPopupWindow.dismiss();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new Runnable() {
                    @Override
                    public final void run() {
                        FloatingToolbar.FloatingToolbarPopup.AnonymousClass5.this.lambda$onAnimationEnd$0();
                    }
                });
            }
        }

        private final class LogAccelerateInterpolator implements Interpolator {
            private final int BASE;
            private final float LOGS_SCALE;

            private LogAccelerateInterpolator() {
                this.BASE = 100;
                this.LOGS_SCALE = 1.0f / computeLog(1.0f, 100);
            }

            private float computeLog(float f, int i) {
                return (float) (1.0d - Math.pow(i, -f));
            }

            @Override
            public float getInterpolation(float f) {
                return 1.0f - (computeLog(1.0f - f, 100) * this.LOGS_SCALE);
            }
        }

        public final class OverflowPanel extends ListView {
            private final FloatingToolbarPopup mPopup;

            OverflowPanel(FloatingToolbarPopup floatingToolbarPopup) {
                super(floatingToolbarPopup.mContext);
                this.mPopup = floatingToolbarPopup;
                setVerticalScrollBarEnabled(false);
                setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                    }
                });
                setClipToOutline(true);
            }

            @Override
            protected boolean awakenScrollBars() {
                return super.awakenScrollBars();
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (this.mPopup.isOverflowAnimating()) {
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                int height;
                int height2;
                height = this.mPopup.mOverflowPanelSize.getHeight();
                height2 = this.mPopup.mOverflowButtonSize.getHeight();
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(height - height2, 1073741824));
            }
        }

        public final class OverflowPanelViewHelper {
            private final Context mContext;
            private final int mIconTextSpacing;
            private final int mSidePadding = AndroidUtilities.dp(18.0f);
            private final View mCalculator = createMenuButton(null);

            public OverflowPanelViewHelper(Context context, int i) {
                this.mContext = context;
                this.mIconTextSpacing = i;
            }

            private View createMenuButton(MenuItem menuItem) {
                View createMenuItemButton = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing, false, false);
                int i = this.mSidePadding;
                createMenuItemButton.setPadding(i, 0, i, 0);
                return createMenuItemButton;
            }

            public int calculateWidth(MenuItem menuItem) {
                FloatingToolbar.updateMenuItemButton(this.mCalculator, menuItem, this.mIconTextSpacing, FloatingToolbar.this.premiumLockClickListener != null);
                this.mCalculator.measure(0, 0);
                return this.mCalculator.getMeasuredWidth();
            }

            public View getView(MenuItem menuItem, int i, View view) {
                if (view != null) {
                    FloatingToolbar.updateMenuItemButton(view, menuItem, this.mIconTextSpacing, FloatingToolbar.this.premiumLockClickListener != null);
                } else {
                    view = createMenuButton(menuItem);
                }
                view.setMinimumWidth(i);
                return view;
            }
        }

        public FloatingToolbarPopup(Context context, View view) {
            Drawable drawable;
            Drawable drawable2;
            Drawable drawable3;
            Drawable drawable4;
            ViewGroup viewGroup;
            int themedColor;
            this.mParent = view;
            this.mContext = context;
            ViewGroup createContentContainer = FloatingToolbar.this.createContentContainer(context);
            this.mContentContainer = createContentContainer;
            this.mPopupWindow = FloatingToolbar.createPopupWindow(createContentContainer);
            this.mMarginHorizontal = AndroidUtilities.dp(16.0f);
            this.mMarginVertical = AndroidUtilities.dp(8.0f);
            this.mLineHeight = AndroidUtilities.dp(48.0f);
            int dp = AndroidUtilities.dp(8.0f);
            this.mIconTextSpacing = dp;
            this.mLogAccelerateInterpolator = new LogAccelerateInterpolator();
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
            this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
            drawable = context.getDrawable(R.drawable.ft_avd_tooverflow);
            Drawable mutate = drawable.mutate();
            this.mArrow = mutate;
            mutate.setAutoMirrored(true);
            drawable2 = context.getDrawable(R.drawable.ft_avd_toarrow);
            Drawable mutate2 = drawable2.mutate();
            this.mOverflow = mutate2;
            mutate2.setAutoMirrored(true);
            drawable3 = context.getDrawable(R.drawable.ft_avd_toarrow_animation);
            AnimatedVectorDrawable m = AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0.m(drawable3.mutate());
            this.mToArrow = m;
            m.setAutoMirrored(true);
            drawable4 = context.getDrawable(R.drawable.ft_avd_tooverflow_animation);
            AnimatedVectorDrawable m2 = AnimatedVectorDrawableCompat$$ExternalSyntheticApiModelOutline0.m(drawable4.mutate());
            this.mToOverflow = m2;
            m2.setAutoMirrored(true);
            FrameLayout frameLayout = new FrameLayout(context);
            this.mOverflowButton = frameLayout;
            ImageButton imageButton = new ImageButton(context) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (FloatingToolbarPopup.this.mIsOverflowOpen) {
                        return false;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.mOverflowButtonIcon = imageButton;
            imageButton.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(48.0f)));
            imageButton.setPaddingRelative(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageButton.setImageDrawable(mutate2);
            TextView textView = new TextView(context);
            this.mOverflowButtonText = textView;
            textView.setText(LocaleController.getString(R.string.Back));
            textView.setTextSize(1, 16.0f);
            textView.setAlpha(0.0f);
            View view2 = new View(context);
            this.mOverflowButtonShadow = view2;
            if (FloatingToolbar.this.currentStyle == 0) {
                int i = Theme.key_dialogTextBlack;
                int themedColor2 = FloatingToolbar.this.getThemedColor(i);
                int i2 = Theme.key_listSelector;
                viewGroup = createContentContainer;
                imageButton.setBackground(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor(i2), 1));
                frameLayout.setBackground(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor(i2), 2));
                view2.setBackgroundColor(Theme.multAlpha(FloatingToolbar.this.getThemedColor(i), 0.4f));
                themedColor = themedColor2;
            } else {
                viewGroup = createContentContainer;
                if (FloatingToolbar.this.currentStyle == 2) {
                    imageButton.setBackground(Theme.createSelectorDrawable(553648127, 1));
                    frameLayout.setBackground(Theme.createSelectorDrawable(553648127, 2));
                    view2.setBackgroundColor(-16777216);
                    themedColor = -328966;
                } else {
                    themedColor = FloatingToolbar.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
                    int i3 = Theme.key_listSelector;
                    imageButton.setBackground(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor(i3), 1));
                    frameLayout.setBackground(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor(i3), 2));
                    view2.setBackgroundColor(FloatingToolbar.this.getThemedColor(Theme.key_divider));
                }
            }
            mutate2.setTint(themedColor);
            mutate.setTint(themedColor);
            m.setTint(themedColor);
            m2.setTint(themedColor);
            textView.setTextColor(themedColor);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view3) {
                    FloatingToolbar.FloatingToolbarPopup.this.lambda$new$0(view3);
                }
            });
            frameLayout.addView(imageButton, LayoutHelper.createFrame(-2, -2, 19));
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 19, 56.0f, 0.0f, 0.0f, 0.0f));
            frameLayout.addView(view2, LayoutHelper.createFrame(-1.0f, 1.0f / AndroidUtilities.density, 55));
            this.mOverflowButtonSize = measure(imageButton);
            this.mMainPanel = createMainPanel();
            this.mOverflowPanelViewHelper = new OverflowPanelViewHelper(context, dp);
            this.mOverflowPanel = createOverflowPanel();
            Animation.AnimationListener createOverflowAnimationListener = createOverflowAnimationListener();
            AnimationSet animationSet = new AnimationSet(true);
            this.mOpenOverflowAnimation = animationSet;
            animationSet.setAnimationListener(createOverflowAnimationListener);
            AnimationSet animationSet2 = new AnimationSet(true);
            this.mCloseOverflowAnimation = animationSet2;
            animationSet2.setAnimationListener(createOverflowAnimationListener);
            this.mShowAnimation = FloatingToolbar.createEnterAnimation(viewGroup);
            ViewGroup viewGroup2 = viewGroup;
            this.mDismissAnimation = FloatingToolbar.createExitAnimation(viewGroup2, 150, new AnonymousClass4(FloatingToolbar.this));
            this.mHideAnimation = FloatingToolbar.createExitAnimation(viewGroup2, 0, new AnonymousClass5(FloatingToolbar.this));
        }

        private int calculateOverflowHeight(int i) {
            int height;
            int min = Math.min(4, Math.min(Math.max(2, i), this.mOverflowPanel.getCount()));
            int i2 = min < this.mOverflowPanel.getCount() ? (int) (this.mLineHeight * 0.5f) : 0;
            int i3 = min * this.mLineHeight;
            height = this.mOverflowButtonSize.getHeight();
            return i3 + height + i2;
        }

        private void cancelDismissAndHideAnimations() {
            this.mDismissAnimation.cancel();
            this.mHideAnimation.cancel();
        }

        private void cancelOverflowAnimations() {
            this.mContentContainer.clearAnimation();
            this.mMainPanel.animate().cancel();
            this.mOverflowPanel.animate().cancel();
            this.mToArrow.stop();
            this.mToOverflow.stop();
        }

        private void clearPanels() {
            this.mOverflowPanelSize = null;
            this.mMainPanelSize = null;
            this.mIsOverflowOpen = false;
            updateOverflowButtonClickListener();
            this.mMainPanel.removeAllViews();
            ArrayAdapter arrayAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            arrayAdapter.clear();
            this.mOverflowPanel.setAdapter((ListAdapter) arrayAdapter);
            this.mContentContainer.removeAllViews();
        }

        private void closeOverflow() {
            final int width;
            final int height;
            width = this.mMainPanelSize.getWidth();
            final int width2 = this.mContentContainer.getWidth();
            final float x = this.mContentContainer.getX();
            final float width3 = x + this.mContentContainer.getWidth();
            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float f, Transformation transformation) {
                    OverflowPanel overflowPanel;
                    float width4;
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, width2 + ((int) (f * (width - width2))));
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(x);
                        width4 = 0.0f;
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        overflowPanel = FloatingToolbarPopup.this.mOverflowPanel;
                    } else {
                        FloatingToolbarPopup.this.mContentContainer.setX(width3 - FloatingToolbarPopup.this.mContentContainer.getWidth());
                        FloatingToolbarPopup.this.mMainPanel.setX(FloatingToolbarPopup.this.mContentContainer.getWidth() - width);
                        overflowPanel = FloatingToolbarPopup.this.mOverflowPanel;
                        width4 = FloatingToolbarPopup.this.mContentContainer.getWidth() - width2;
                    }
                    overflowPanel.setX(width4);
                }
            };
            height = this.mMainPanelSize.getHeight();
            final int height2 = this.mContentContainer.getHeight();
            final float y = this.mContentContainer.getY() + this.mContentContainer.getHeight();
            Animation animation2 = new Animation() {
                @Override
                protected void applyTransformation(float f, Transformation transformation) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, height2 + ((int) (f * (height - height2))));
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(y - FloatingToolbarPopup.this.mContentContainer.getHeight());
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float x2 = this.mOverflowButton.getX();
            final float width4 = isInRTLMode() ? (x2 - width2) + this.mOverflowButtonIcon.getWidth() : (width2 + x2) - this.mOverflowButtonIcon.getWidth();
            Animation animation3 = new Animation() {
                @Override
                protected void applyTransformation(float f, Transformation transformation) {
                    float f2 = x2;
                    FloatingToolbarPopup.this.mOverflowButton.setX(f2 + ((width4 - f2) * f) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : FloatingToolbarPopup.this.mContentContainer.getWidth() - width2));
                    float f3 = 1.0f - f;
                    FloatingToolbarPopup.this.mOverflowButtonText.setAlpha(f3);
                    FloatingToolbarPopup.this.mOverflowButtonShadow.setAlpha(f3);
                }
            };
            animation.setInterpolator(this.mFastOutSlowInInterpolator);
            animation.setDuration(getAdjustedDuration(250));
            animation2.setInterpolator(this.mLogAccelerateInterpolator);
            animation2.setDuration(getAdjustedDuration(250));
            animation3.setInterpolator(this.mFastOutSlowInInterpolator);
            animation3.setDuration(getAdjustedDuration(250));
            this.mCloseOverflowAnimation.getAnimations().clear();
            this.mCloseOverflowAnimation.addAnimation(animation);
            this.mCloseOverflowAnimation.addAnimation(animation2);
            this.mCloseOverflowAnimation.addAnimation(animation3);
            this.mContentContainer.startAnimation(this.mCloseOverflowAnimation);
            this.mIsOverflowOpen = false;
            updateOverflowButtonClickListener();
            this.mMainPanel.animate().alpha(1.0f).withLayer().setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100L).start();
            this.mOverflowPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(150L).start();
        }

        private ViewGroup createMainPanel() {
            return new LinearLayout(this.mContext) {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return FloatingToolbarPopup.this.isOverflowAnimating();
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    int width;
                    if (FloatingToolbarPopup.this.isOverflowAnimating() && FloatingToolbarPopup.this.mMainPanelSize != null) {
                        width = FloatingToolbarPopup.this.mMainPanelSize.getWidth();
                        i = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
                    }
                    super.onMeasure(i, i2);
                }
            };
        }

        private Animation.AnimationListener createOverflowAnimationListener() {
            return new AnonymousClass14();
        }

        private OverflowPanel createOverflowPanel() {
            final OverflowPanel overflowPanel = new OverflowPanel(this);
            overflowPanel.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            overflowPanel.setDivider(null);
            overflowPanel.setDividerHeight(0);
            overflowPanel.setAdapter((ListAdapter) new ArrayAdapter(this.mContext, 0) {
                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    int width;
                    OverflowPanelViewHelper overflowPanelViewHelper = FloatingToolbarPopup.this.mOverflowPanelViewHelper;
                    MenuItem menuItem = (MenuItem) getItem(i);
                    width = FloatingToolbarPopup.this.mOverflowPanelSize.getWidth();
                    return overflowPanelViewHelper.getView(menuItem, width, view);
                }
            });
            overflowPanel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                    FloatingToolbar.FloatingToolbarPopup.this.lambda$createOverflowPanel$4(overflowPanel, adapterView, view, i, j);
                }
            });
            return overflowPanel;
        }

        private int getAdjustedDuration(int i) {
            int i2 = this.mTransitionDurationScale;
            return i2 < 150 ? Math.max(i - 50, 0) : i2 > 300 ? i + 50 : i;
        }

        private int getAdjustedToolbarWidth(int i) {
            refreshViewPort();
            int width = this.mViewPortOnScreen.width() - (AndroidUtilities.dp(16.0f) * 2);
            if (i <= 0) {
                i = AndroidUtilities.dp(400.0f);
            }
            return Math.min(i, width);
        }

        private int getOverflowWidth() {
            int count = this.mOverflowPanel.getAdapter().getCount();
            int i = 0;
            for (int i2 = 0; i2 < count; i2++) {
                i = Math.max(this.mOverflowPanelViewHelper.calculateWidth((MenuItem) this.mOverflowPanel.getAdapter().getItem(i2)), i);
            }
            return i;
        }

        private boolean hasOverflow() {
            return this.mOverflowPanelSize != null;
        }

        public boolean isInRTLMode() {
            return false;
        }

        public boolean isOverflowAnimating() {
            return (this.mOpenOverflowAnimation.hasStarted() && !this.mOpenOverflowAnimation.hasEnded()) || (this.mCloseOverflowAnimation.hasStarted() && !this.mCloseOverflowAnimation.hasEnded());
        }

        public void lambda$createOverflowPanel$4(OverflowPanel overflowPanel, AdapterView adapterView, View view, int i, long j) {
            MenuItem menuItem = (MenuItem) overflowPanel.getAdapter().getItem(i);
            if (FloatingToolbar.this.premiumLockClickListener == null || !FloatingToolbar.premiumOptions.contains(Integer.valueOf(menuItem.getItemId()))) {
                MenuItem.OnMenuItemClickListener onMenuItemClickListener = this.mOnMenuItemClickListener;
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onMenuItemClick(menuItem);
                    return;
                }
                return;
            }
            int i2 = -this.shiftDp;
            this.shiftDp = i2;
            AndroidUtilities.shakeViewSpring(view, i2);
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
            FloatingToolbar.this.premiumLockClickListener.run();
        }

        public static int lambda$layoutOverflowPanelItems$3(MenuItem menuItem, MenuItem menuItem2) {
            List list = FloatingToolbar.premiumOptions;
            return (list.contains(Integer.valueOf(menuItem.getItemId())) ? 1 : 0) - (list.contains(Integer.valueOf(menuItem2.getItemId())) ? 1 : 0);
        }

        public void lambda$new$0(View view) {
            onBackPressed();
        }

        public void lambda$updateOverflowButtonClickListener$1(View view) {
            onBackPressed();
        }

        public void lambda$updateOverflowButtonClickListener$2(View view) {
            onBackPressed();
        }

        private void layoutOverflowPanelItems(List list) {
            OverflowPanel overflowPanel;
            int height;
            float f;
            int width;
            ArrayAdapter arrayAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            arrayAdapter.clear();
            if (FloatingToolbar.this.premiumLockClickListener != null) {
                Collections.sort(list, new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$layoutOverflowPanelItems$3;
                        lambda$layoutOverflowPanelItems$3 = FloatingToolbar.FloatingToolbarPopup.lambda$layoutOverflowPanelItems$3((MenuItem) obj, (MenuItem) obj2);
                        return lambda$layoutOverflowPanelItems$3;
                    }
                });
            }
            int size = list.size();
            boolean premiumFeaturesBlocked = MessagesController.getInstance(UserConfig.selectedAccount).premiumFeaturesBlocked();
            for (int i = 0; i < size; i++) {
                MenuItem menuItem = (MenuItem) list.get(i);
                boolean z = true;
                if (FloatingToolbar.this.premiumLockClickListener != null && FloatingToolbar.premiumOptions.contains(Integer.valueOf(menuItem.getItemId()))) {
                    z = true ^ premiumFeaturesBlocked;
                }
                if (z) {
                    arrayAdapter.add(menuItem);
                }
            }
            this.mOverflowPanel.setAdapter((ListAdapter) arrayAdapter);
            if (this.mOpenOverflowUpwards) {
                overflowPanel = this.mOverflowPanel;
                f = 0.0f;
            } else {
                overflowPanel = this.mOverflowPanel;
                height = this.mOverflowButtonSize.getHeight();
                f = height;
            }
            overflowPanel.setY(f);
            int overflowWidth = getOverflowWidth();
            width = this.mOverflowButtonSize.getWidth();
            Size size2 = new Size(Math.max(overflowWidth, width), calculateOverflowHeight(4));
            this.mOverflowPanelSize = size2;
            setSize(this.mOverflowPanel, size2);
        }

        private void maybeComputeTransitionDurationScale() {
            int width;
            int width2;
            int height;
            int height2;
            Size size = this.mMainPanelSize;
            if (size == null || this.mOverflowPanelSize == null) {
                return;
            }
            width = size.getWidth();
            width2 = this.mOverflowPanelSize.getWidth();
            int i = width - width2;
            height = this.mOverflowPanelSize.getHeight();
            height2 = this.mMainPanelSize.getHeight();
            int i2 = height - height2;
            double sqrt = Math.sqrt((i * i) + (i2 * i2));
            double d = this.mContentContainer.getContext().getResources().getDisplayMetrics().density;
            Double.isNaN(d);
            this.mTransitionDurationScale = (int) (sqrt / d);
        }

        private Size measure(View view) {
            view.measure(0, 0);
            return new Size(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        private void onBackPressed() {
            if (this.mIsOverflowOpen) {
                this.mOverflowButtonIcon.setImageDrawable(this.mToOverflow);
                this.mToOverflow.start();
                closeOverflow();
            } else {
                this.mOverflowButtonIcon.setImageDrawable(this.mToArrow);
                this.mToArrow.start();
                openOverflow();
            }
        }

        private void openOverflow() {
            final int width;
            final int height;
            width = this.mOverflowPanelSize.getWidth();
            height = this.mOverflowPanelSize.getHeight();
            final int width2 = this.mContentContainer.getWidth();
            final int height2 = this.mContentContainer.getHeight();
            final float y = this.mContentContainer.getY();
            final float x = this.mContentContainer.getX();
            final float width3 = x + this.mContentContainer.getWidth();
            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float f, Transformation transformation) {
                    OverflowPanel overflowPanel;
                    float width4;
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, width2 + ((int) (f * (width - width2))));
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(x);
                        width4 = 0.0f;
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        overflowPanel = FloatingToolbarPopup.this.mOverflowPanel;
                    } else {
                        FloatingToolbarPopup.this.mContentContainer.setX(width3 - FloatingToolbarPopup.this.mContentContainer.getWidth());
                        FloatingToolbarPopup.this.mMainPanel.setX(FloatingToolbarPopup.this.mContentContainer.getWidth() - width2);
                        overflowPanel = FloatingToolbarPopup.this.mOverflowPanel;
                        width4 = FloatingToolbarPopup.this.mContentContainer.getWidth() - width;
                    }
                    overflowPanel.setX(width4);
                }
            };
            Animation animation2 = new Animation() {
                @Override
                protected void applyTransformation(float f, Transformation transformation) {
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, height2 + ((int) (f * (height - height2))));
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(y - (FloatingToolbarPopup.this.mContentContainer.getHeight() - height2));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float x2 = this.mOverflowButton.getX();
            float f = width;
            final float width4 = isInRTLMode() ? (f + x2) - this.mOverflowButtonIcon.getWidth() : (x2 - f) + this.mOverflowButtonIcon.getWidth();
            Animation animation3 = new Animation() {
                @Override
                protected void applyTransformation(float f2, Transformation transformation) {
                    float f3 = x2;
                    FloatingToolbarPopup.this.mOverflowButton.setX(f3 + ((width4 - f3) * f2) + (FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : FloatingToolbarPopup.this.mContentContainer.getWidth() - width2));
                    FloatingToolbarPopup.this.mOverflowButtonText.setAlpha(f2);
                    FloatingToolbarPopup.this.mOverflowButtonShadow.setAlpha(f2);
                }
            };
            animation.setInterpolator(this.mLogAccelerateInterpolator);
            animation.setDuration(getAdjustedDuration(250));
            animation2.setInterpolator(this.mFastOutSlowInInterpolator);
            animation2.setDuration(getAdjustedDuration(250));
            animation3.setInterpolator(this.mFastOutSlowInInterpolator);
            animation3.setDuration(getAdjustedDuration(250));
            this.mOpenOverflowAnimation.getAnimations().clear();
            this.mOpenOverflowAnimation.addAnimation(animation);
            this.mOpenOverflowAnimation.addAnimation(animation2);
            this.mOpenOverflowAnimation.addAnimation(animation3);
            this.mContentContainer.startAnimation(this.mOpenOverflowAnimation);
            this.mIsOverflowOpen = true;
            updateOverflowButtonClickListener();
            this.mMainPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(250L).start();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mOverflowButton.getLayoutParams();
            layoutParams.width = this.mOverflowPanel.getWidth();
            this.mOverflowButton.setLayoutParams(layoutParams);
            this.mOverflowPanel.setAlpha(1.0f);
        }

        public void positionContentYCoordinatesIfOpeningOverflowUpwards() {
            int height;
            int height2;
            if (this.mOpenOverflowUpwards) {
                ViewGroup viewGroup = this.mMainPanel;
                int height3 = this.mContentContainer.getHeight();
                height = this.mMainPanelSize.getHeight();
                viewGroup.setY(height3 - height);
                this.mOverflowButton.setY(this.mContentContainer.getHeight() - this.mOverflowButton.getHeight());
                OverflowPanel overflowPanel = this.mOverflowPanel;
                int height4 = this.mContentContainer.getHeight();
                height2 = this.mOverflowPanelSize.getHeight();
                overflowPanel.setY(height4 - height2);
            }
        }

        private void preparePopupContent() {
            this.mContentContainer.removeAllViews();
            if (hasOverflow()) {
                this.mContentContainer.addView(this.mOverflowPanel);
            }
            this.mContentContainer.addView(this.mMainPanel);
            if (hasOverflow()) {
                this.mContentContainer.addView(this.mOverflowButton);
            }
            setPanelsStatesAtRestingPosition();
            setContentAreaAsTouchableSurface();
            if (isInRTLMode()) {
                this.mContentContainer.setAlpha(0.0f);
                this.mContentContainer.post(this.mPreparePopupContentRTLHelper);
            }
        }

        private void refreshCoordinatesAndOverflowDirection(Rect rect) {
            int i;
            int i2;
            refreshViewPort();
            int min = Math.min(rect.centerX() - (this.mPopupWindow.getWidth() / 2), this.mViewPortOnScreen.right - this.mPopupWindow.getWidth());
            int i3 = rect.top;
            Rect rect2 = this.mViewPortOnScreen;
            int i4 = i3 - rect2.top;
            int i5 = rect2.bottom - rect.bottom;
            int i6 = this.mMarginVertical * 2;
            int i7 = this.mLineHeight + i6;
            if (hasOverflow()) {
                int calculateOverflowHeight = calculateOverflowHeight(2) + i6;
                Rect rect3 = this.mViewPortOnScreen;
                int i8 = (rect3.bottom - rect.top) + i7;
                int i9 = (rect.bottom - rect3.top) + i7;
                if (i4 >= calculateOverflowHeight) {
                    updateOverflowHeight(i4 - i6);
                    i2 = rect.top;
                } else {
                    if (i4 >= i7 && i8 >= calculateOverflowHeight) {
                        updateOverflowHeight(i8 - i6);
                        i = rect.top - i7;
                    } else if (i5 >= calculateOverflowHeight) {
                        updateOverflowHeight(i5 - i6);
                        i = rect.bottom;
                    } else if (i5 < i7 || rect3.height() < calculateOverflowHeight) {
                        updateOverflowHeight(this.mViewPortOnScreen.height() - i6);
                        i = this.mViewPortOnScreen.top;
                    } else {
                        updateOverflowHeight(i9 - i6);
                        i2 = rect.bottom + i7;
                    }
                    this.mOpenOverflowUpwards = false;
                }
                i = i2 - this.mPopupWindow.getHeight();
                this.mOpenOverflowUpwards = true;
            } else {
                i = i4 >= i7 ? rect.top - i7 : i5 >= i7 ? rect.bottom : i5 >= this.mLineHeight ? rect.bottom - this.mMarginVertical : Math.max(this.mViewPortOnScreen.top, rect.top - i7);
            }
            this.mParent.getRootView().getLocationOnScreen(this.mTmpCoords);
            int[] iArr = this.mTmpCoords;
            int i10 = iArr[0];
            int i11 = iArr[1];
            this.mParent.getRootView().getLocationInWindow(this.mTmpCoords);
            int[] iArr2 = this.mTmpCoords;
            this.mCoordsOnWindow.set(Math.max(0, min - (i10 - iArr2[0])), Math.max(0, i - (i11 - iArr2[1])));
        }

        private void refreshViewPort() {
            this.mParent.getWindowVisibleDisplayFrame(this.mViewPortOnScreen);
        }

        private void runDismissAnimation() {
            this.mDismissAnimation.start();
        }

        private void runHideAnimation() {
            this.mHideAnimation.start();
        }

        private void runShowAnimation() {
            this.mShowAnimation.start();
        }

        private void setButtonTagAndClickListener(View view, MenuItem menuItem) {
            view.setTag(menuItem);
            view.setOnClickListener(this.mMenuItemButtonOnClickListener);
        }

        public void setContentAreaAsTouchableSurface() {
            int width;
            Size size;
            int height;
            if (this.mIsOverflowOpen) {
                width = this.mOverflowPanelSize.getWidth();
                size = this.mOverflowPanelSize;
            } else {
                width = this.mMainPanelSize.getWidth();
                size = this.mMainPanelSize;
            }
            height = size.getHeight();
            this.mTouchableRegion.set((int) this.mContentContainer.getX(), (int) this.mContentContainer.getY(), ((int) this.mContentContainer.getX()) + width, ((int) this.mContentContainer.getY()) + height);
        }

        public void setHeight(View view, int i) {
            setSize(view, view.getLayoutParams().width, i);
        }

        public void setPanelsStatesAtRestingPosition() {
            View view;
            int width;
            int width2;
            int width3;
            int width4;
            int width5;
            int height;
            int height2;
            int height3;
            int height4;
            int height5;
            int width6;
            int height6;
            int height7;
            int height8;
            int width7;
            int width8;
            this.mOverflowButton.setEnabled(true);
            this.mOverflowPanel.awakenScrollBars();
            if (this.mIsOverflowOpen) {
                Size size = this.mOverflowPanelSize;
                setSize(this.mContentContainer, size);
                this.mMainPanel.setAlpha(0.0f);
                this.mMainPanel.setVisibility(4);
                this.mOverflowPanel.setAlpha(1.0f);
                this.mOverflowPanel.setVisibility(0);
                this.mOverflowButtonIcon.setImageDrawable(this.mArrow);
                this.mOverflowButton.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
                if (isInRTLMode()) {
                    this.mContentContainer.setX(this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    FrameLayout frameLayout = this.mOverflowButton;
                    width7 = size.getWidth();
                    width8 = this.mOverflowButtonSize.getWidth();
                    frameLayout.setX(width7 - width8);
                } else {
                    ViewGroup viewGroup = this.mContentContainer;
                    int width9 = this.mPopupWindow.getWidth();
                    width6 = size.getWidth();
                    viewGroup.setX((width9 - width6) - this.mMarginHorizontal);
                    this.mMainPanel.setX(-this.mContentContainer.getX());
                    this.mOverflowButton.setX(0.0f);
                }
                this.mOverflowPanel.setX(0.0f);
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY(this.mMarginVertical);
                    ViewGroup viewGroup2 = this.mMainPanel;
                    height6 = size.getHeight();
                    viewGroup2.setY(height6 - this.mContentContainer.getHeight());
                    FrameLayout frameLayout2 = this.mOverflowButton;
                    height7 = size.getHeight();
                    height8 = this.mOverflowButtonSize.getHeight();
                    frameLayout2.setY(height7 - height8);
                    view = this.mOverflowPanel;
                    view.setY(0.0f);
                    return;
                }
                this.mContentContainer.setY(this.mMarginVertical);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                OverflowPanel overflowPanel = this.mOverflowPanel;
                height5 = this.mOverflowButtonSize.getHeight();
                overflowPanel.setY(height5);
            }
            Size size2 = this.mMainPanelSize;
            setSize(this.mContentContainer, size2);
            this.mMainPanel.setAlpha(1.0f);
            this.mMainPanel.setVisibility(0);
            this.mOverflowPanel.setAlpha(0.0f);
            this.mOverflowPanel.setVisibility(4);
            this.mOverflowButtonIcon.setImageDrawable(this.mOverflow);
            this.mOverflowButton.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            if (!hasOverflow()) {
                this.mContentContainer.setX(this.mMarginHorizontal);
                this.mContentContainer.setY(this.mMarginVertical);
                this.mMainPanel.setX(0.0f);
                view = this.mMainPanel;
                view.setY(0.0f);
                return;
            }
            if (isInRTLMode()) {
                this.mContentContainer.setX(this.mMarginHorizontal);
                this.mMainPanel.setX(0.0f);
                this.mOverflowButton.setX(0.0f);
                this.mOverflowPanel.setX(0.0f);
            } else {
                ViewGroup viewGroup3 = this.mContentContainer;
                int width10 = this.mPopupWindow.getWidth();
                width = size2.getWidth();
                viewGroup3.setX((width10 - width) - this.mMarginHorizontal);
                this.mMainPanel.setX(0.0f);
                FrameLayout frameLayout3 = this.mOverflowButton;
                width2 = size2.getWidth();
                width3 = this.mOverflowButtonSize.getWidth();
                frameLayout3.setX(width2 - width3);
                OverflowPanel overflowPanel2 = this.mOverflowPanel;
                width4 = size2.getWidth();
                width5 = this.mOverflowPanelSize.getWidth();
                overflowPanel2.setX(width4 - width5);
            }
            if (this.mOpenOverflowUpwards) {
                ViewGroup viewGroup4 = this.mContentContainer;
                int i = this.mMarginVertical;
                height = this.mOverflowPanelSize.getHeight();
                int i2 = i + height;
                height2 = size2.getHeight();
                viewGroup4.setY(i2 - height2);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                OverflowPanel overflowPanel3 = this.mOverflowPanel;
                height3 = size2.getHeight();
                height4 = this.mOverflowPanelSize.getHeight();
                overflowPanel3.setY(height3 - height4);
                return;
            }
            this.mContentContainer.setY(this.mMarginVertical);
            this.mMainPanel.setY(0.0f);
            this.mOverflowButton.setY(0.0f);
            OverflowPanel overflowPanel4 = this.mOverflowPanel;
            height5 = this.mOverflowButtonSize.getHeight();
            overflowPanel4.setY(height5);
        }

        private void setSize(View view, int i, int i2) {
            view.setMinimumWidth(i);
            view.setMinimumHeight(i2);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(0, 0);
            }
            layoutParams.width = i;
            layoutParams.height = i2;
            view.setLayoutParams(layoutParams);
        }

        private void setSize(View view, Size size) {
            int width;
            int height;
            width = size.getWidth();
            height = size.getHeight();
            setSize(view, width, height);
        }

        private void setTouchableSurfaceInsetsComputer() {
        }

        public void setWidth(View view, int i) {
            setSize(view, i, view.getLayoutParams().height);
        }

        private void setZeroTouchableSurface() {
            this.mTouchableRegion.setEmpty();
        }

        private void updateOverflowButtonClickListener() {
            ImageView imageView;
            View.OnClickListener onClickListener = null;
            if (this.mIsOverflowOpen) {
                this.mOverflowButton.setClickable(true);
                this.mOverflowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        FloatingToolbar.FloatingToolbarPopup.this.lambda$updateOverflowButtonClickListener$1(view);
                    }
                });
                this.mOverflowButtonIcon.setClickable(false);
                imageView = this.mOverflowButtonIcon;
            } else {
                this.mOverflowButton.setClickable(false);
                this.mOverflowButton.setOnClickListener(null);
                this.mOverflowButtonIcon.setClickable(true);
                imageView = this.mOverflowButtonIcon;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        FloatingToolbar.FloatingToolbarPopup.this.lambda$updateOverflowButtonClickListener$2(view);
                    }
                };
            }
            imageView.setOnClickListener(onClickListener);
        }

        private void updateOverflowHeight(int i) {
            int height;
            int height2;
            int height3;
            int width;
            if (hasOverflow()) {
                height = this.mOverflowButtonSize.getHeight();
                int calculateOverflowHeight = calculateOverflowHeight((i - height) / this.mLineHeight);
                height2 = this.mOverflowPanelSize.getHeight();
                if (height2 != calculateOverflowHeight) {
                    width = this.mOverflowPanelSize.getWidth();
                    this.mOverflowPanelSize = new Size(width, calculateOverflowHeight);
                }
                setSize(this.mOverflowPanel, this.mOverflowPanelSize);
                if (this.mIsOverflowOpen) {
                    setSize(this.mContentContainer, this.mOverflowPanelSize);
                    if (this.mOpenOverflowUpwards) {
                        height3 = this.mOverflowPanelSize.getHeight();
                        int i2 = height3 - calculateOverflowHeight;
                        ViewGroup viewGroup = this.mContentContainer;
                        float f = i2;
                        viewGroup.setY(viewGroup.getY() + f);
                        FrameLayout frameLayout = this.mOverflowButton;
                        frameLayout.setY(frameLayout.getY() - f);
                    }
                } else {
                    setSize(this.mContentContainer, this.mMainPanelSize);
                }
                updatePopupSize();
            }
        }

        private void updatePopupSize() {
            int i;
            int width;
            int height;
            int width2;
            int height2;
            Size size = this.mMainPanelSize;
            int i2 = 0;
            if (size != null) {
                width2 = size.getWidth();
                i = Math.max(0, width2);
                height2 = this.mMainPanelSize.getHeight();
                i2 = Math.max(0, height2);
            } else {
                i = 0;
            }
            Size size2 = this.mOverflowPanelSize;
            if (size2 != null) {
                width = size2.getWidth();
                i = Math.max(i, width);
                height = this.mOverflowPanelSize.getHeight();
                i2 = Math.max(i2, height);
            }
            this.mPopupWindow.setWidth(i + (this.mMarginHorizontal * 2));
            this.mPopupWindow.setHeight(i2 + (this.mMarginVertical * 2));
            maybeComputeTransitionDurationScale();
        }

        public void dismiss() {
            if (this.mDismissed) {
                return;
            }
            this.mHidden = false;
            this.mDismissed = true;
            this.mHideAnimation.cancel();
            runDismissAnimation();
            setZeroTouchableSurface();
        }

        public void hide() {
            if (isShowing()) {
                this.mHidden = true;
                runHideAnimation();
                setZeroTouchableSurface();
            }
        }

        public boolean isShowing() {
            return (this.mDismissed || this.mHidden) ? false : true;
        }

        public List layoutMainPanelItems(List list, int i) {
            int width;
            int width2;
            LinkedList linkedList = new LinkedList(list);
            this.mMainPanel.removeAllViews();
            this.mMainPanel.setPaddingRelative(0, 0, 0, 0);
            Iterator it = linkedList.iterator();
            int i2 = i;
            boolean z = true;
            while (it.hasNext()) {
                MenuItem menuItem = (MenuItem) it.next();
                boolean z2 = !it.hasNext();
                if (menuItem == null || FloatingToolbar.this.premiumLockClickListener == null || !FloatingToolbar.premiumOptions.contains(Integer.valueOf(menuItem.getItemId()))) {
                    View createMenuItemButton = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing, z, z2);
                    if (createMenuItemButton instanceof LinearLayout) {
                        ((LinearLayout) createMenuItemButton).setGravity(17);
                    }
                    double d = z ? 1.5d : 1.0d;
                    double paddingStart = createMenuItemButton.getPaddingStart();
                    Double.isNaN(paddingStart);
                    int i3 = (int) (d * paddingStart);
                    int paddingTop = createMenuItemButton.getPaddingTop();
                    double d2 = z2 ? 1.5d : 1.0d;
                    double paddingEnd = createMenuItemButton.getPaddingEnd();
                    Double.isNaN(paddingEnd);
                    createMenuItemButton.setPaddingRelative(i3, paddingTop, (int) (paddingEnd * d2), createMenuItemButton.getPaddingBottom());
                    createMenuItemButton.measure(0, 0);
                    int min = Math.min(createMenuItemButton.getMeasuredWidth(), i);
                    width2 = this.mOverflowButtonSize.getWidth();
                    boolean z3 = min <= i2 - width2;
                    boolean z4 = z2 && min <= i2;
                    if (!z3 && !z4) {
                        break;
                    }
                    setButtonTagAndClickListener(createMenuItemButton, menuItem);
                    this.mMainPanel.addView(createMenuItemButton);
                    ViewGroup.LayoutParams layoutParams = createMenuItemButton.getLayoutParams();
                    layoutParams.width = min;
                    createMenuItemButton.setLayoutParams(layoutParams);
                    i2 -= min;
                    it.remove();
                    z = false;
                }
            }
            if (!linkedList.isEmpty()) {
                ViewGroup viewGroup = this.mMainPanel;
                width = this.mOverflowButtonSize.getWidth();
                viewGroup.setPaddingRelative(0, 0, width, 0);
            }
            this.mMainPanelSize = measure(this.mMainPanel);
            return linkedList;
        }

        public void layoutMenuItems(List list, MenuItem.OnMenuItemClickListener onMenuItemClickListener, int i) {
            this.mOnMenuItemClickListener = onMenuItemClickListener;
            cancelOverflowAnimations();
            clearPanels();
            List layoutMainPanelItems = layoutMainPanelItems(list, getAdjustedToolbarWidth(i));
            if (!layoutMainPanelItems.isEmpty()) {
                layoutOverflowPanelItems(layoutMainPanelItems);
            }
            updatePopupSize();
        }

        public void show(Rect rect) {
            if (isShowing()) {
                return;
            }
            this.mHidden = false;
            this.mDismissed = false;
            cancelDismissAndHideAnimations();
            cancelOverflowAnimations();
            refreshCoordinatesAndOverflowDirection(rect);
            preparePopupContent();
            PopupWindow popupWindow = this.mPopupWindow;
            View view = this.mParent;
            Point point = this.mCoordsOnWindow;
            popupWindow.showAtLocation(view, 0, point.x, point.y);
            setTouchableSurfaceInsetsComputer();
            runShowAnimation();
        }

        public void updateCoordinates(Rect rect) {
            if (isShowing() && this.mPopupWindow.isShowing()) {
                cancelOverflowAnimations();
                refreshCoordinatesAndOverflowDirection(rect);
                preparePopupContent();
                PopupWindow popupWindow = this.mPopupWindow;
                Point point = this.mCoordsOnWindow;
                popupWindow.update(point.x, point.y, popupWindow.getWidth(), this.mPopupWindow.getHeight());
            }
        }
    }

    public FloatingToolbar(Context context, View view, int i, Theme.ResourcesProvider resourcesProvider) {
        this.mWindowView = view;
        this.currentStyle = i;
        this.resourcesProvider = resourcesProvider;
        this.mPopup = new FloatingToolbarPopup(context, view);
    }

    public ViewGroup createContentContainer(Context context) {
        int i;
        int i2;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int dp = AndroidUtilities.dp(20.0f);
        marginLayoutParams.rightMargin = dp;
        marginLayoutParams.topMargin = dp;
        marginLayoutParams.leftMargin = dp;
        marginLayoutParams.bottomMargin = dp;
        relativeLayout.setLayoutParams(marginLayoutParams);
        relativeLayout.setElevation(AndroidUtilities.dp(2.0f));
        relativeLayout.setFocusable(true);
        relativeLayout.setFocusableInTouchMode(true);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        float dp2 = AndroidUtilities.dp(6.0f);
        gradientDrawable.setCornerRadii(new float[]{dp2, dp2, dp2, dp2, dp2, dp2, dp2, dp2});
        int i3 = this.currentStyle;
        if (i3 != 0) {
            if (i3 == 2) {
                i2 = -115203550;
                gradientDrawable.setColor(i2);
                relativeLayout.setBackgroundDrawable(gradientDrawable);
                relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                relativeLayout.setClipToOutline(true);
                return relativeLayout;
            }
            if (i3 == 1) {
                i = Theme.key_windowBackgroundWhite;
            }
            relativeLayout.setBackgroundDrawable(gradientDrawable);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            relativeLayout.setClipToOutline(true);
            return relativeLayout;
        }
        i = Theme.key_dialogBackground;
        i2 = getThemedColor(i);
        gradientDrawable.setColor(i2);
        relativeLayout.setBackgroundDrawable(gradientDrawable);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        relativeLayout.setClipToOutline(true);
        return relativeLayout;
    }

    public static AnimatorSet createEnterAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f).setDuration(150L));
        return animatorSet;
    }

    public static AnimatorSet createExitAnimation(View view, int i, Animator.AnimatorListener animatorListener) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 1.0f, 0.0f).setDuration(100L));
        animatorSet.setStartDelay(i);
        animatorSet.addListener(animatorListener);
        return animatorSet;
    }

    public android.view.View createMenuItemButton(android.content.Context r18, android.view.MenuItem r19, int r20, boolean r21, boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.FloatingToolbar.createMenuItemButton(android.content.Context, android.view.MenuItem, int, boolean, boolean):android.view.View");
    }

    public static PopupWindow createPopupWindow(ViewGroup viewGroup) {
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext()) {
            private final int[] p = new int[2];
            private View downRootView = null;

            private boolean isParent(View view, View view2) {
                if (view == view2) {
                    return true;
                }
                if (view.getParent() == null) {
                    return false;
                }
                return view.getParent() instanceof View ? isParent((View) view.getParent(), view2) : view.getParent() == view2 || view.getRootView() == view2;
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
                if (!dispatchTouchEvent) {
                    getLocationOnScreen(this.p);
                    int[] iArr = this.p;
                    motionEvent.offsetLocation(iArr[0], iArr[1]);
                    if (motionEvent.getAction() == 0) {
                        List<View> allGlobalViews = AndroidUtilities.allGlobalViews();
                        if (allGlobalViews != null && allGlobalViews.size() > 1) {
                            for (int size = allGlobalViews.size() - 2; size >= 0; size--) {
                                View view = allGlobalViews.get(size);
                                if (!isParent(this, view)) {
                                    view.getLocationOnScreen(this.p);
                                    int[] iArr2 = this.p;
                                    motionEvent.offsetLocation(-iArr2[0], -iArr2[1]);
                                    dispatchTouchEvent = view.dispatchTouchEvent(motionEvent);
                                    if (dispatchTouchEvent) {
                                        this.downRootView = view;
                                        return true;
                                    }
                                    int[] iArr3 = this.p;
                                    motionEvent.offsetLocation(iArr3[0], iArr3[1]);
                                }
                            }
                        }
                    } else {
                        View view2 = this.downRootView;
                        if (view2 != null) {
                            view2.getLocationOnScreen(this.p);
                            int[] iArr4 = this.p;
                            motionEvent.offsetLocation(-iArr4[0], -iArr4[1]);
                            dispatchTouchEvent = view2.dispatchTouchEvent(motionEvent);
                        }
                    }
                }
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.downRootView = null;
                }
                return dispatchTouchEvent;
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }
        };
        PopupWindow popupWindow = new PopupWindow(linearLayout);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setSplitTouchEnabled(true);
        viewGroup.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        linearLayout.addView(viewGroup);
        return popupWindow;
    }

    private void doShow() {
        List visibleAndEnabledMenuItems = getVisibleAndEnabledMenuItems(this.mMenu);
        Collections.sort(visibleAndEnabledMenuItems, this.mMenuItemComparator);
        if (!isCurrentlyShowing(visibleAndEnabledMenuItems) || this.mWidthChanged) {
            this.mPopup.dismiss();
            this.mPopup.layoutMenuItems(visibleAndEnabledMenuItems, this.mMenuItemClickListener, this.mSuggestedWidth);
            this.mShowingMenuItems = visibleAndEnabledMenuItems;
        }
        if (!this.mPopup.isShowing()) {
            this.mPopup.show(this.mContentRect);
        } else if (!this.mPreviousContentRect.equals(this.mContentRect)) {
            this.mPopup.updateCoordinates(this.mContentRect);
        }
        this.mWidthChanged = false;
        this.mPreviousContentRect.set(this.mContentRect);
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private List getVisibleAndEnabledMenuItems(Menu menu) {
        Utilities.Callback0Return callback0Return;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; menu != null && i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isVisible() && item.isEnabled()) {
                SubMenu subMenu = item.getSubMenu();
                if (subMenu != null) {
                    arrayList.addAll(getVisibleAndEnabledMenuItems(subMenu));
                } else if ((item.getItemId() != R.id.menu_quote || (callback0Return = this.quoteShowCallback) == null || ((Boolean) callback0Return.run()).booleanValue()) && item.getItemId() != 16908353 && (item.getItemId() != R.id.menu_regular || this.premiumLockClickListener == null)) {
                    arrayList.add(item);
                }
            }
        }
        return arrayList;
    }

    private boolean isCurrentlyShowing(List list) {
        if (this.mShowingMenuItems == null || list.size() != this.mShowingMenuItems.size()) {
            return false;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = (MenuItem) list.get(i);
            MenuItem menuItem2 = (MenuItem) this.mShowingMenuItems.get(i);
            if (menuItem.getItemId() != menuItem2.getItemId() || !TextUtils.equals(menuItem.getTitle(), menuItem2.getTitle()) || !Objects.equals(menuItem.getIcon(), menuItem2.getIcon()) || menuItem.getGroupId() != menuItem2.getGroupId()) {
                return false;
            }
        }
        return true;
    }

    public static int lambda$new$1(MenuItem menuItem, MenuItem menuItem2) {
        return menuItem.getOrder() - menuItem2.getOrder();
    }

    public static boolean lambda$static$0(MenuItem menuItem) {
        return false;
    }

    private void registerOrientationHandler() {
        unregisterOrientationHandler();
        this.mWindowView.addOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    private void unregisterOrientationHandler() {
        this.mWindowView.removeOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    public static void updateMenuItemButton(View view, MenuItem menuItem, int i, boolean z) {
        ViewGroup viewGroup = (ViewGroup) view;
        TextView textView = (TextView) viewGroup.getChildAt(0);
        textView.setEllipsize(null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            textView.setText(menuItem.getTitle());
        }
        textView.setPaddingRelative(0, 0, 0, 0);
        viewGroup.getChildAt(2).setVisibility(z && premiumOptions.contains(Integer.valueOf(menuItem.getItemId())) ? 0 : 8);
    }

    public void dismiss() {
        unregisterOrientationHandler();
        this.mPopup.dismiss();
    }

    public void hide() {
        this.mPopup.hide();
    }

    public FloatingToolbar setContentRect(Rect rect) {
        this.mContentRect.set(rect);
        return this;
    }

    public FloatingToolbar setMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public FloatingToolbar setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        if (onMenuItemClickListener == null) {
            onMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
        }
        this.mMenuItemClickListener = onMenuItemClickListener;
        return this;
    }

    public void setOnPremiumLockClick(Runnable runnable) {
        this.premiumLockClickListener = runnable;
    }

    public void setQuoteShowVisible(Utilities.Callback0Return callback0Return) {
        this.quoteShowCallback = callback0Return;
    }

    public FloatingToolbar show() {
        registerOrientationHandler();
        doShow();
        return this;
    }

    public FloatingToolbar updateLayout() {
        if (this.mPopup.isShowing()) {
            doShow();
        }
        return this;
    }
}
