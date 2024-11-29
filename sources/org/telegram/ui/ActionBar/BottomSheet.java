package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.camera.CameraView;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;

public class BottomSheet extends Dialog implements BaseFragment.AttachedSheet {
    private static final boolean AVOID_SYSTEM_CUTOUT_FULLSCREEN = false;
    private boolean allowCustomAnimation;
    private boolean allowDrawContent;
    protected boolean allowNestedScroll;
    private boolean applyBottomPadding;
    private boolean applyTopPadding;
    public BaseFragment attachedFragment;
    protected ColorDrawable backDrawable;
    protected int backgroundPaddingLeft;
    protected int backgroundPaddingTop;
    protected int behindKeyboardColor;
    protected int behindKeyboardColorKey;
    private boolean bigTitle;
    private int bottomInset;
    protected boolean calcMandatoryInsets;
    private boolean canDismissWithSwipe;
    private boolean canDismissWithTouchOutside;
    private int cellType;
    public ContainerView container;
    protected ViewGroup containerView;
    protected int currentAccount;
    private float currentPanTranslationY;
    protected AnimatorSet currentSheetAnimation;
    protected int currentSheetAnimationType;
    private View customView;
    protected int customViewGravity;
    protected BottomSheetDelegateInterface delegate;
    protected boolean dimBehind;
    protected int dimBehindAlpha;
    private boolean disableScroll;
    private Runnable dismissRunnable;
    private boolean dismissed;
    public boolean drawDoubleNavigationBar;
    public boolean drawNavigationBar;
    private boolean focusable;
    private boolean forceKeyboardOnDismiss;
    private boolean fullHeight;
    protected boolean fullWidth;
    private float hideSystemVerticalInsetsProgress;
    protected boolean isFullscreen;
    protected boolean isPortrait;
    private int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews;
    private CharSequence[] items;
    private ValueAnimator keyboardContentAnimator;
    protected int keyboardHeight;
    protected boolean keyboardVisible;
    private WindowInsets lastInsets;
    private int lastKeyboardHeight;
    private int layoutCount;
    private int leftInset;
    private boolean multipleLinesTitle;
    protected int navBarColor;
    protected int navBarColorKey;
    protected float navigationBarAlpha;
    protected ValueAnimator navigationBarAnimation;
    protected View nestedScrollChild;
    private AnimationNotificationsLocker notificationsLocker;
    public boolean occupyNavigationBar;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onHideListener;
    protected Interpolator openInterpolator;
    private boolean openNoDelay;
    protected int openedLayerNum;
    private int overlayDrawNavBarColor;
    public boolean pauseAllHeavyOperations;
    protected int playingImagesLayerNum;
    protected Theme.ResourcesProvider resourcesProvider;
    private int rightInset;
    public boolean scrollNavBar;
    private Integer selectedPos;
    protected Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    boolean showing;
    private boolean skipDismissAnimation;
    private long smoothContainerViewLayoutUntil;
    public boolean smoothKeyboardAnimationEnabled;
    public boolean smoothKeyboardByBottom;
    protected Runnable startAnimationRunnable;
    private int statusBarHeight;
    private int tag;
    private CharSequence title;
    private TextView titleView;
    public FrameLayout topBulletinContainer;
    private int touchSlop;
    private boolean transitionFromRight;
    public boolean useBackgroundTopPadding;
    private boolean useFastDismiss;
    protected boolean useHardwareLayer;
    protected boolean useLightNavBar;
    protected boolean useLightStatusBar;
    protected boolean useSmoothKeyboard;
    protected boolean waitingKeyboard;

    public class AnonymousClass7 extends AnimatorListenerAdapter {
        final int val$item;

        AnonymousClass7(int i) {
            this.val$item = i;
        }

        public void lambda$onAnimationEnd$0() {
            if (BottomSheet.this.onHideListener != null) {
                BottomSheet.this.onHideListener.onDismiss(BottomSheet.this);
            }
            try {
                BottomSheet.this.dismissInternal();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
            if (animatorSet == null || !animatorSet.equals(animator)) {
                return;
            }
            BottomSheet bottomSheet = BottomSheet.this;
            bottomSheet.currentSheetAnimation = null;
            bottomSheet.currentSheetAnimationType = 0;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
            if (animatorSet != null && animatorSet.equals(animator)) {
                BottomSheet bottomSheet = BottomSheet.this;
                bottomSheet.currentSheetAnimation = null;
                bottomSheet.currentSheetAnimationType = 0;
                if (bottomSheet.onClickListener != null) {
                    BottomSheet.this.onClickListener.onClick(BottomSheet.this, this.val$item);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        BottomSheet.AnonymousClass7.this.lambda$onAnimationEnd$0();
                    }
                });
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
        }
    }

    public class AnonymousClass8 extends AnimatorListenerAdapter {
        AnonymousClass8() {
        }

        public void lambda$onAnimationEnd$0() {
            try {
                BottomSheet.this.dismissInternal();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
            if (animatorSet == null || !animatorSet.equals(animator)) {
                return;
            }
            BottomSheet bottomSheet = BottomSheet.this;
            bottomSheet.currentSheetAnimation = null;
            bottomSheet.currentSheetAnimationType = 0;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            AnimatorSet animatorSet = BottomSheet.this.currentSheetAnimation;
            if (animatorSet != null && animatorSet.equals(animator)) {
                BottomSheet bottomSheet = BottomSheet.this;
                bottomSheet.currentSheetAnimation = null;
                bottomSheet.currentSheetAnimationType = 0;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        BottomSheet.AnonymousClass8.this.lambda$onAnimationEnd$0();
                    }
                });
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        private boolean checked;
        int currentType;
        private ImageView imageView;
        private ImageView imageView2;
        public boolean isSelected;
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, int i) {
            this(context, i, null);
        }

        public BottomSheetCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView;
            FrameLayout.LayoutParams createFrame;
            this.isSelected = false;
            this.resourcesProvider = resourcesProvider;
            this.currentType = i;
            if (i != Builder.CELL_TYPE_CALL) {
                setBackgroundDrawable(Theme.getSelectorDrawable(false, resourcesProvider));
            }
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            ImageView imageView2 = new ImageView(context);
            this.imageView2 = imageView2;
            imageView2.setScaleType(scaleType);
            this.imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_radioBackgroundChecked, resourcesProvider), PorterDuff.Mode.SRC_IN));
            addView(this.imageView2, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 3 : 5) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            if (i == 0 || i == Builder.CELL_TYPE_CALL) {
                this.textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 16.0f);
                textView = this.textView;
                createFrame = LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16);
            } else if (i == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.bold());
                textView = this.textView;
                createFrame = LayoutHelper.createFrame(-1, -1.0f);
            } else {
                if (i != 2) {
                    return;
                }
                this.textView.setGravity(17);
                this.textView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.bold());
                this.textView.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor(Theme.key_featuredStickers_addButton), 6.0f));
                textView = this.textView;
                createFrame = LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f);
            }
            addView(textView, createFrame);
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public TextView getTextView() {
            return this.textView;
        }

        protected int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        public boolean isChecked() {
            return this.checked;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (this.isSelected) {
                accessibilityNodeInfo.setSelected(true);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int i3 = this.currentType;
            int i4 = i3 == 2 ? 80 : 48;
            if (i3 == 0) {
                i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(i4), 1073741824));
        }

        public void setChecked(boolean z) {
            ImageView imageView = this.imageView2;
            this.checked = z;
            imageView.setImageResource(z ? R.drawable.checkbig : 0);
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setIconColor(int i) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            setTextAndIcon(charSequence, i, null, false);
        }

        public void setTextAndIcon(CharSequence charSequence, int i, Drawable drawable, boolean z) {
            this.textView.setText(charSequence);
            if (i == 0 && drawable == null) {
                this.imageView.setVisibility(4);
                this.textView.setPadding(AndroidUtilities.dp(z ? 21.0f : 16.0f), 0, AndroidUtilities.dp(z ? 21.0f : 16.0f), 0);
                return;
            }
            if (drawable != null) {
                this.imageView.setImageDrawable(drawable);
            } else {
                this.imageView.setImageResource(i);
            }
            this.imageView.setVisibility(0);
            if (z) {
                this.textView.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 21.0f : 72.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 72.0f : 21.0f), 0);
                this.imageView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(5.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(5.0f) : 5, 0);
            } else {
                this.textView.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 72.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 72.0f : 16.0f), 0);
                this.imageView.setPadding(0, 0, 0, 0);
            }
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            setTextAndIcon(charSequence, 0, drawable, false);
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        @Override
        public boolean canDismiss() {
            return true;
        }

        @Override
        public void onOpenAnimationEnd() {
        }
    }

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();
    }

    public static class Builder {
        public static int CELL_TYPE_CALL = 4;
        private BottomSheet bottomSheet;

        public Builder(Context context) {
            this(context, false);
        }

        public Builder(Context context, boolean z) {
            this(context, z, null);
        }

        public Builder(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            BottomSheet bottomSheet = new BottomSheet(context, z, resourcesProvider);
            this.bottomSheet = bottomSheet;
            bottomSheet.fixNavigationBar();
        }

        public Builder(Context context, boolean z, Theme.ResourcesProvider resourcesProvider, int i) {
            BottomSheet bottomSheet = new BottomSheet(context, z, resourcesProvider);
            this.bottomSheet = bottomSheet;
            bottomSheet.setBackgroundColor(i);
            this.bottomSheet.fixNavigationBar(i);
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public Runnable getDismissRunnable() {
            return this.bottomSheet.dismissRunnable;
        }

        public Builder selectedPos(Integer num) {
            this.bottomSheet.selectedPos = num;
            return this;
        }

        public Builder setApplyBottomPadding(boolean z) {
            this.bottomSheet.applyBottomPadding = z;
            return this;
        }

        public Builder setApplyTopPadding(boolean z) {
            this.bottomSheet.applyTopPadding = z;
            return this;
        }

        public Builder setCellType(int i) {
            this.bottomSheet.cellType = i;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public Builder setCustomView(View view, int i) {
            this.bottomSheet.customView = view;
            this.bottomSheet.customViewGravity = i;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate bottomSheetDelegate) {
            this.bottomSheet.setDelegate(bottomSheetDelegate);
            return this;
        }

        public BottomSheet setDimBehind(boolean z) {
            BottomSheet bottomSheet = this.bottomSheet;
            bottomSheet.dimBehind = z;
            return bottomSheet;
        }

        public Builder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.itemIcons = iArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.bottomSheet.setOnHideListener(onDismissListener);
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            return setTitle(charSequence, false);
        }

        public Builder setTitle(CharSequence charSequence, boolean z) {
            this.bottomSheet.title = charSequence;
            this.bottomSheet.bigTitle = z;
            return this;
        }

        public Builder setTitleMultipleLines(boolean z) {
            this.bottomSheet.multipleLinesTitle = z;
            return this;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }
    }

    public class ContainerView extends FrameLayout implements NestedScrollingParent {
        private boolean allowedSwipeToBack;
        private Paint backgroundPaint;
        private AnimatorSet currentAnimation;
        private boolean keyboardChanged;
        private boolean maybeStartTracking;
        private NestedScrollingParentHelper nestedScrollingParentHelper;
        private Rect rect;
        private boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private float swipeBackX;
        private VelocityTracker velocityTracker;
        private float y;

        public ContainerView(Context context) {
            super(context);
            this.velocityTracker = null;
            this.startedTrackingPointerId = -1;
            this.maybeStartTracking = false;
            this.startedTracking = false;
            this.currentAnimation = null;
            this.rect = new Rect();
            this.backgroundPaint = new Paint();
            this.y = 0.0f;
            this.swipeBackX = 0.0f;
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
            setWillNotDraw(false);
        }

        private void cancelCurrentAnimation() {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
        }

        private void checkDismiss(float f, float f2) {
            if ((BottomSheet.this.containerView.getTranslationY() >= AndroidUtilities.getPixelsInCM(0.8f, false) || (f2 >= 3500.0f && Math.abs(f2) >= Math.abs(f))) && (f2 >= 0.0f || Math.abs(f2) < 3500.0f)) {
                boolean z = BottomSheet.this.allowCustomAnimation;
                BottomSheet.this.allowCustomAnimation = false;
                BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                BottomSheet.this.allowCustomAnimation = z;
                return;
            }
            this.currentAnimation = new AnimatorSet();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BottomSheet.ContainerView.this.lambda$checkDismiss$0(valueAnimator);
                }
            });
            this.currentAnimation.playTogether(ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", 0.0f), ofFloat);
            this.currentAnimation.setDuration((int) ((Math.max(0.0f, r2) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 250.0f));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animator)) {
                        ContainerView.this.currentAnimation = null;
                    }
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
                }
            });
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentAnimation.start();
        }

        public void lambda$checkDismiss$0(ValueAnimator valueAnimator) {
            ContainerView containerView = BottomSheet.this.container;
            if (containerView != null) {
                containerView.invalidate();
            }
        }

        public void lambda$onLayout$4(ValueAnimator valueAnimator) {
            BottomSheet.this.containerView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
            BottomSheet bottomSheet = BottomSheet.this;
            bottomSheet.onSmoothContainerViewLayout(bottomSheet.containerView.getTranslationY());
            invalidate();
        }

        public void lambda$processTouchEvent$1(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.swipeBackX = floatValue;
            BottomSheet.this.containerView.setTranslationX(floatValue);
            BottomSheet.this.container.invalidate();
        }

        public void lambda$processTouchEvent$2(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.swipeBackX = floatValue;
            BottomSheet.this.containerView.setTranslationX(floatValue);
            BottomSheet.this.container.invalidate();
        }

        public void lambda$processTouchEvent$3(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            BottomSheet bottomSheet = BottomSheet.this;
            bottomSheet.backDrawable.setAlpha(bottomSheet.dimBehind ? (int) (bottomSheet.dimBehindAlpha * floatValue) : 0);
        }

        @Override
        protected void dispatchDraw(android.graphics.Canvas r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (!(view instanceof CameraView)) {
                return super.drawChild(canvas, view, j);
            }
            if (BottomSheet.this.shouldOverlayCameraViewOverNavBar()) {
                drawNavigationBar(canvas, 1.0f);
            }
            return super.drawChild(canvas, view, j);
        }

        public void drawNavigationBar(Canvas canvas, float f) {
            float max;
            int i = Build.VERSION.SDK_INT;
            if (i >= 26) {
                BottomSheet bottomSheet = BottomSheet.this;
                int i2 = bottomSheet.navBarColorKey;
                if (i2 >= 0) {
                    this.backgroundPaint.setColor(bottomSheet.getThemedColor(i2));
                } else {
                    this.backgroundPaint.setColor(bottomSheet.navBarColor);
                }
            } else {
                this.backgroundPaint.setColor(-16777216);
            }
            if (!BottomSheet.this.transitionFromRight || BottomSheet.this.containerView.getVisibility() == 0) {
                BottomSheet bottomSheet2 = BottomSheet.this;
                float f2 = 0.0f;
                if ((!bottomSheet2.drawNavigationBar || bottomSheet2.bottomInset == 0) && BottomSheet.this.currentPanTranslationY == 0.0f) {
                    return;
                }
                BottomSheet bottomSheet3 = BottomSheet.this;
                int bottomInset = bottomSheet3.drawNavigationBar ? bottomSheet3.getBottomInset() : 0;
                BottomSheet bottomSheet4 = BottomSheet.this;
                if (bottomSheet4.scrollNavBar || (i >= 29 && bottomSheet4.getAdditionalMandatoryOffsets() > 0)) {
                    BottomSheet bottomSheet5 = BottomSheet.this;
                    max = bottomSheet5.drawDoubleNavigationBar ? Math.max(0.0f, Math.min(bottomInset - bottomSheet5.currentPanTranslationY, BottomSheet.this.containerView.getTranslationY())) : Math.max(0.0f, BottomSheet.this.getBottomInset() - (bottomSheet5.containerView.getMeasuredHeight() - BottomSheet.this.containerView.getTranslationY()));
                } else {
                    max = 0.0f;
                }
                int alpha = this.backgroundPaint.getAlpha();
                if (BottomSheet.this.transitionFromRight) {
                    f *= BottomSheet.this.containerView.getAlpha();
                }
                int x = BottomSheet.this.transitionFromRight ? (int) BottomSheet.this.containerView.getX() : BottomSheet.this.containerView.getLeft();
                if (f < 1.0f) {
                    this.backgroundPaint.setAlpha((int) (alpha * f));
                }
                canvas.drawRect(BottomSheet.this.backgroundPaddingLeft + x, ((getMeasuredHeight() - bottomInset) + max) - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight() + max, this.backgroundPaint);
                this.backgroundPaint.setAlpha(alpha);
                if (BottomSheet.this.overlayDrawNavBarColor != 0) {
                    this.backgroundPaint.setColor(BottomSheet.this.overlayDrawNavBarColor);
                    int alpha2 = this.backgroundPaint.getAlpha();
                    if (f < 1.0f) {
                        this.backgroundPaint.setAlpha((int) (alpha2 * f));
                    } else {
                        f2 = max;
                    }
                    canvas.drawRect(x + BottomSheet.this.backgroundPaddingLeft, ((getMeasuredHeight() - bottomInset) + f2) - BottomSheet.this.currentPanTranslationY, BottomSheet.this.containerView.getRight() - BottomSheet.this.backgroundPaddingLeft, getMeasuredHeight() + f2, this.backgroundPaint);
                    this.backgroundPaint.setAlpha(alpha2);
                }
            }
        }

        @Override
        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        protected void onDraw(android.graphics.Canvas r11) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onDraw(android.graphics.Canvas):void");
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return (BottomSheet.this.canDismissWithSwipe() || BottomSheet.this.canSwipeToBack(motionEvent)) ? processTouchEvent(motionEvent, true) : super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        protected void onLayout(boolean r17, int r18, int r19, int r20, int r21) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onLayout(boolean, int, int, int, int):void");
        }

        @Override
        protected void onMeasure(int r12, int r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.ContainerView.onMeasure(int, int):void");
        }

        @Override
        public boolean onNestedFling(View view, float f, float f2, boolean z) {
            return false;
        }

        @Override
        public boolean onNestedPreFling(View view, float f, float f2) {
            return false;
        }

        @Override
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            cancelCurrentAnimation();
            float translationY = BottomSheet.this.containerView.getTranslationY();
            if (translationY <= 0.0f || i2 <= 0) {
                return;
            }
            float f = translationY - i2;
            iArr[1] = i2;
            BottomSheet.this.containerView.setTranslationY(f >= 0.0f ? f : 0.0f);
            BottomSheet.this.container.invalidate();
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            cancelCurrentAnimation();
            if (i4 != 0) {
                float translationY = BottomSheet.this.containerView.getTranslationY() - i4;
                if (translationY < 0.0f) {
                    translationY = 0.0f;
                }
                BottomSheet.this.containerView.setTranslationY(translationY);
                BottomSheet.this.container.invalidate();
            }
        }

        @Override
        public void onNestedScrollAccepted(View view, View view2, int i) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
            if (BottomSheet.this.dismissed || !BottomSheet.this.allowNestedScroll) {
                return;
            }
            cancelCurrentAnimation();
        }

        @Override
        public boolean onStartNestedScroll(View view, View view2, int i) {
            BottomSheet bottomSheet = BottomSheet.this;
            View view3 = bottomSheet.nestedScrollChild;
            if ((view3 == null || view == view3) && !bottomSheet.dismissed) {
                BottomSheet bottomSheet2 = BottomSheet.this;
                if (bottomSheet2.allowNestedScroll && i == 2 && !bottomSheet2.canDismissWithSwipe()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onStopNestedScroll(View view) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
            if (BottomSheet.this.dismissed) {
                return;
            }
            BottomSheet bottomSheet = BottomSheet.this;
            if (bottomSheet.allowNestedScroll) {
                bottomSheet.containerView.getTranslationY();
                checkDismiss(0.0f, 0.0f);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return processTouchEvent(motionEvent, false);
        }

        public boolean processTouchEvent(MotionEvent motionEvent, boolean z) {
            ValueAnimator ofFloat;
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(motionEvent)) {
                return true;
            }
            if (BottomSheet.this.canSwipeToBack(motionEvent) || this.allowedSwipeToBack) {
                if (motionEvent != null && ((motionEvent.getAction() == 0 || motionEvent.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && motionEvent.getPointerCount() == 1)) {
                    this.allowedSwipeToBack = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    cancelCurrentAnimation();
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    float x = motionEvent.getX() - this.startedTrackingX;
                    float y = motionEvent.getY() - this.startedTrackingY;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                    if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && x > 0.0f && x / 3.0f > Math.abs(y) && Math.abs(x) >= BottomSheet.this.touchSlop) {
                        this.startedTrackingX = (int) motionEvent.getX();
                        this.maybeStartTracking = false;
                        this.startedTracking = true;
                    } else if (this.startedTracking) {
                        float f = this.swipeBackX + x;
                        this.swipeBackX = f;
                        BottomSheet.this.containerView.setTranslationX(Math.max(f, 0.0f));
                        this.startedTrackingX = (int) motionEvent.getX();
                        BottomSheet.this.container.invalidate();
                    }
                } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    float xVelocity = this.velocityTracker.getXVelocity();
                    float yVelocity = this.velocityTracker.getYVelocity();
                    if (this.swipeBackX >= BottomSheet.this.containerView.getMeasuredWidth() / 3.0f || (xVelocity >= 3500.0f && xVelocity >= yVelocity)) {
                        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.swipeBackX, getMeasuredWidth());
                        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                BottomSheet.ContainerView.this.lambda$processTouchEvent$2(valueAnimator);
                            }
                        });
                        ofFloat2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                BottomSheet.this.skipDismissAnimation = true;
                                BottomSheet.this.containerView.setTranslationX(r2.getMeasuredWidth());
                                BottomSheet.this.dismiss();
                                BottomSheet.this.container.invalidate();
                            }
                        });
                        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                        ofFloat2.setInterpolator(cubicBezierInterpolator);
                        ofFloat2.setDuration(320L);
                        ofFloat2.start();
                        ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                BottomSheet.ContainerView.this.lambda$processTouchEvent$3(valueAnimator);
                            }
                        });
                        ofFloat.setInterpolator(cubicBezierInterpolator);
                        ofFloat.setDuration(320L);
                    } else {
                        float max = Math.max(this.swipeBackX, 0.0f);
                        this.swipeBackX = max;
                        ofFloat = ValueAnimator.ofFloat(max, 0.0f);
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                BottomSheet.ContainerView.this.lambda$processTouchEvent$1(valueAnimator);
                            }
                        });
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                ContainerView.this.swipeBackX = 0.0f;
                                BottomSheet.this.containerView.setTranslationX(0.0f);
                                BottomSheet.this.container.invalidate();
                            }
                        });
                        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        ofFloat.setDuration(220L);
                    }
                    ofFloat.start();
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    this.startedTrackingPointerId = -1;
                    this.allowedSwipeToBack = false;
                }
            } else if (BottomSheet.this.canDismissWithTouchOutside() && motionEvent != null && ((motionEvent.getAction() == 0 || motionEvent.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && motionEvent.getPointerCount() == 1)) {
                this.startedTrackingX = (int) motionEvent.getX();
                int y2 = (int) motionEvent.getY();
                this.startedTrackingY = y2;
                if (y2 < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.onDismissWithTouchOutside();
                    return true;
                }
                BottomSheet.this.onScrollUpBegin(this.y);
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                VelocityTracker velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (BottomSheet.this.canDismissWithSwipe() && motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float abs = Math.abs((int) (motionEvent.getX() - this.startedTrackingX));
                float y3 = ((int) motionEvent.getY()) - this.startedTrackingY;
                boolean onScrollUp = BottomSheet.this.onScrollUp(this.y + y3);
                this.velocityTracker.addMovement(motionEvent);
                if (!BottomSheet.this.disableScroll && this.maybeStartTracking && !this.startedTracking && y3 > 0.0f && y3 / 3.0f > Math.abs(abs) && Math.abs(y3) >= BottomSheet.this.touchSlop) {
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float f2 = this.y + y3;
                    this.y = f2;
                    if (!onScrollUp) {
                        this.y = Math.max(f2, 0.0f);
                    }
                    BottomSheet.this.containerView.setTranslationY(Math.max(this.y, 0.0f));
                    this.startedTrackingY = (int) motionEvent.getY();
                    BottomSheet.this.container.invalidate();
                }
            } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                BottomSheet.this.onScrollUpEnd(this.y);
                if (this.startedTracking || this.y > 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                } else {
                    this.maybeStartTracking = false;
                }
                this.startedTracking = false;
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
            if ((z || !this.maybeStartTracking) && !this.startedTracking) {
                return (BottomSheet.this.canDismissWithSwipe() || BottomSheet.this.canSwipeToBack(motionEvent)) ? false : true;
            }
            return true;
        }

        @Override
        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public BottomSheet(Context context, boolean z) {
        this(context, z, null);
    }

    public BottomSheet(android.content.Context r8, boolean r9, org.telegram.ui.ActionBar.Theme.ResourcesProvider r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.<init>(android.content.Context, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static int access$1012(BottomSheet bottomSheet, int i) {
        int i2 = bottomSheet.bottomInset + i;
        bottomSheet.bottomInset = i2;
        return i2;
    }

    static int access$1020(BottomSheet bottomSheet, int i) {
        int i2 = bottomSheet.bottomInset - i;
        bottomSheet.bottomInset = i2;
        return i2;
    }

    static int access$1510(BottomSheet bottomSheet) {
        int i = bottomSheet.layoutCount;
        bottomSheet.layoutCount = i - 1;
        return i;
    }

    public int getAdditionalMandatoryOffsets() {
        WindowInsets windowInsets;
        Insets systemGestureInsets;
        int i;
        int i2;
        int i3;
        if (!this.calcMandatoryInsets || (windowInsets = this.lastInsets) == null) {
            return 0;
        }
        systemGestureInsets = windowInsets.getSystemGestureInsets();
        if (this.keyboardVisible || !this.drawNavigationBar || systemGestureInsets == null) {
            return 0;
        }
        i = systemGestureInsets.left;
        if (i == 0) {
            i3 = systemGestureInsets.right;
            if (i3 == 0) {
                return 0;
            }
        }
        i2 = systemGestureInsets.bottom;
        return i2;
    }

    public void lambda$dismiss$8() {
        try {
            dismissInternal();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$dismiss$9(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    public void lambda$dismissWithButtonClick$6(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        setItemColor(this.selectedPos.intValue(), intValue, intValue);
    }

    public void lambda$dismissWithButtonClick$7(int i, ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        setItemColor(i, intValue, intValue);
    }

    public WindowInsets lambda$new$1(View view, WindowInsets windowInsets) {
        int systemWindowInsetTop;
        WindowInsets consumeSystemWindowInsets;
        WindowInsets windowInsets2;
        systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        if ((systemWindowInsetTop != 0 || AndroidUtilities.isInMultiwindow) && this.statusBarHeight != systemWindowInsetTop) {
            this.statusBarHeight = systemWindowInsetTop;
        }
        this.lastInsets = windowInsets;
        view.requestLayout();
        onInsetsChanged();
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsets2 = WindowInsets.CONSUMED;
            return windowInsets2;
        }
        consumeSystemWindowInsets = windowInsets.consumeSystemWindowInsets();
        return consumeSystemWindowInsets;
    }

    public void lambda$onCreateInternal$2() {
        AndroidUtilities.removeFromParent(this.container);
        this.attachedFragment.getLayoutContainer().addView(this.container);
    }

    public static boolean lambda$onCreateInternal$3(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$onCreateInternal$4(View view) {
        dismissWithButtonClick(((Integer) view.getTag()).intValue());
    }

    public void lambda$setHideSystemVerticalInsets$0(ValueAnimator valueAnimator) {
        this.hideSystemVerticalInsetsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.requestLayout();
        this.containerView.requestLayout();
    }

    public void lambda$startOpenAnimation$5(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    private void onCreateInternal() {
        Window window;
        int i;
        float f;
        TextView textView;
        int dp;
        float f2;
        TextView textView2;
        TextUtils.TruncateAt truncateAt;
        BaseFragment baseFragment = this.attachedFragment;
        if (baseFragment != null) {
            baseFragment.addSheet(this);
            if (this.attachedFragment.getLayoutContainer() == null) {
                return;
            }
            if (((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(this.attachedFragment.getLayoutContainer().getWindowToken(), 2)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        BottomSheet.this.lambda$onCreateInternal$2();
                    }
                }, 80L);
            } else {
                AndroidUtilities.removeFromParent(this.container);
                this.attachedFragment.getLayoutContainer().addView(this.container);
            }
            window = null;
        } else {
            window = getWindow();
            window.setWindowAnimations(R.style.DialogNoAnimation);
            setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        }
        if (this.useLightStatusBar && Build.VERSION.SDK_INT >= 23 && Theme.getColor(Theme.key_actionBarDefault, null, true) == -1) {
            this.container.setSystemUiVisibility(this.container.getSystemUiVisibility() | 8192);
        }
        if (this.useLightNavBar && Build.VERSION.SDK_INT >= 26) {
            AndroidUtilities.setLightNavigationBar(getWindow(), false);
        }
        if (this.containerView == null) {
            FrameLayout frameLayout = new FrameLayout(getContext()) {
                @Override
                public boolean hasOverlappingRendering() {
                    return false;
                }

                @Override
                public void setTranslationY(float f3) {
                    super.setTranslationY(f3);
                    FrameLayout frameLayout2 = BottomSheet.this.topBulletinContainer;
                    if (frameLayout2 != null) {
                        frameLayout2.setTranslationY((-(r0.container.getHeight() - BottomSheet.this.containerView.getY())) + BottomSheet.this.backgroundPaddingTop);
                    }
                    BottomSheet.this.onContainerTranslationYChanged(f3);
                }
            };
            this.containerView = frameLayout;
            frameLayout.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(this.backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + this.backgroundPaddingTop) - 1, this.backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        if (this.topBulletinContainer == null) {
            FrameLayout frameLayout2 = new FrameLayout(getContext());
            this.topBulletinContainer = frameLayout2;
            ContainerView containerView = this.container;
            containerView.addView(frameLayout2, containerView.indexOfChild(this.containerView) + 1, LayoutHelper.createFrame(-1, -2, 80));
        }
        if (this.title != null) {
            TextView textView3 = new TextView(getContext()) {
                @Override
                protected void onMeasure(int i2, int i3) {
                    super.onMeasure(i2, i3);
                    if (BottomSheet.this.multipleLinesTitle) {
                        int measuredHeight = getMeasuredHeight();
                        if (BottomSheet.this.customView != null) {
                            ((ViewGroup.MarginLayoutParams) BottomSheet.this.customView.getLayoutParams()).topMargin = measuredHeight;
                            return;
                        }
                        if (BottomSheet.this.containerView != null) {
                            for (int i4 = 1; i4 < BottomSheet.this.containerView.getChildCount(); i4++) {
                                View childAt = BottomSheet.this.containerView.getChildAt(i4);
                                if (childAt instanceof BottomSheetCell) {
                                    ((ViewGroup.MarginLayoutParams) childAt.getLayoutParams()).topMargin = measuredHeight;
                                    measuredHeight += AndroidUtilities.dp(48.0f);
                                }
                            }
                        }
                    }
                }
            };
            this.titleView = textView3;
            textView3.setText(this.title);
            if (this.bigTitle) {
                this.titleView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                this.titleView.setTextSize(1, 20.0f);
                this.titleView.setTypeface(AndroidUtilities.bold());
                textView = this.titleView;
                f = 21.0f;
                dp = AndroidUtilities.dp(21.0f);
                f2 = this.multipleLinesTitle ? 14.0f : 6.0f;
            } else {
                this.titleView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
                f = 16.0f;
                this.titleView.setTextSize(1, 16.0f);
                textView = this.titleView;
                dp = AndroidUtilities.dp(16.0f);
                f2 = this.multipleLinesTitle ? 8.0f : 0.0f;
            }
            textView.setPadding(dp, AndroidUtilities.dp(f2), AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
            if (this.multipleLinesTitle) {
                this.titleView.setSingleLine(false);
                this.titleView.setMaxLines(5);
                textView2 = this.titleView;
                truncateAt = TextUtils.TruncateAt.END;
            } else {
                this.titleView.setLines(1);
                this.titleView.setSingleLine(true);
                textView2 = this.titleView;
                truncateAt = TextUtils.TruncateAt.MIDDLE;
            }
            textView2.setEllipsize(truncateAt);
            this.titleView.setGravity(16);
            i = 48;
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, this.multipleLinesTitle ? -2.0f : 48));
            this.titleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$onCreateInternal$3;
                    lambda$onCreateInternal$3 = BottomSheet.lambda$onCreateInternal$3(view, motionEvent);
                    return lambda$onCreateInternal$3;
                }
            });
        } else {
            i = 0;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            if (this.useBackgroundTopPadding) {
                this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, this.customViewGravity, 0.0f, i, 0.0f, 0.0f));
            } else {
                this.containerView.setClipToPadding(false);
                this.containerView.setClipChildren(false);
                this.container.setClipToPadding(false);
                this.container.setClipChildren(false);
                float f3 = i;
                this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2.0f, this.customViewGravity, 0.0f, f3, 0.0f, 0.0f));
                ((ViewGroup.MarginLayoutParams) this.customView.getLayoutParams()).topMargin = (-this.backgroundPaddingTop) + AndroidUtilities.dp(f3);
            }
        } else if (this.items != null) {
            int i2 = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i2 >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i2] != null) {
                    BottomSheetCell bottomSheetCell = new BottomSheetCell(getContext(), this.cellType, this.resourcesProvider);
                    CharSequence charSequence = this.items[i2];
                    int[] iArr = this.itemIcons;
                    bottomSheetCell.setTextAndIcon(charSequence, iArr != null ? iArr[i2] : 0, null, this.bigTitle);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, i, 0.0f, 0.0f));
                    i += 48;
                    bottomSheetCell.setTag(Integer.valueOf(i2));
                    bottomSheetCell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            BottomSheet.this.lambda$onCreateInternal$4(view2);
                        }
                    });
                    this.itemViews.add(bottomSheetCell);
                }
                i2++;
            }
        }
        if (this.attachedFragment == null && window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = -1;
            attributes.gravity = 51;
            attributes.dimAmount = 0.0f;
            int i3 = attributes.flags & (-3);
            attributes.flags = i3;
            if (this.focusable) {
                attributes.softInputMode = 16;
            } else {
                attributes.flags = i3 | 131072;
            }
            if (this.isFullscreen) {
                if (Build.VERSION.SDK_INT >= 21) {
                    attributes.flags |= -2147417856;
                }
                attributes.flags |= 1024;
                this.container.setSystemUiVisibility(1284);
            }
            attributes.height = -1;
            if (Build.VERSION.SDK_INT >= 28) {
                attributes.layoutInDisplayCutoutMode = 1;
            }
            window.setAttributes(attributes);
        }
    }

    private void setShowing(boolean z) {
        if (this.showing == z) {
            return;
        }
        this.showing = z;
        if (this.openedLayerNum > 0) {
            if (z) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, Integer.valueOf(this.openedLayerNum));
            } else {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, Integer.valueOf(this.openedLayerNum));
            }
        }
    }

    public void startOpenAnimation() {
        AnimatorSet animatorSet;
        TimeInterpolator timeInterpolator;
        if (this.dismissed) {
            return;
        }
        this.containerView.setVisibility(0);
        if (onCustomOpenAnimation()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
            this.container.setLayerType(2, null);
        }
        if (this.transitionFromRight) {
            this.containerView.setTranslationX(AndroidUtilities.dp(48.0f));
            this.containerView.setAlpha(0.0f);
            this.containerView.setTranslationY(0.0f);
        } else {
            this.containerView.setTranslationY(getContainerViewHeight() + this.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? Math.max(0, Math.min(AndroidUtilities.navigationBarHeight, getBottomInset())) : 0));
        }
        this.currentSheetAnimationType = 1;
        ValueAnimator valueAnimator = this.navigationBarAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.navigationBarAlpha, 1.0f);
        this.navigationBarAnimation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BottomSheet.this.lambda$startOpenAnimation$5(valueAnimator2);
            }
        });
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.currentSheetAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofInt(this.backDrawable, (Property<ColorDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, this.dimBehind ? this.dimBehindAlpha : 0), this.navigationBarAnimation);
        if (this.transitionFromRight) {
            this.currentSheetAnimation.setDuration(250L);
            animatorSet = this.currentSheetAnimation;
            timeInterpolator = CubicBezierInterpolator.DEFAULT;
        } else {
            this.currentSheetAnimation.setDuration(400L);
            animatorSet = this.currentSheetAnimation;
            timeInterpolator = this.openInterpolator;
        }
        animatorSet.setInterpolator(timeInterpolator);
        this.currentSheetAnimation.setStartDelay(this.waitingKeyboard ? 0L : 20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        this.notificationsLocker.lock();
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                AnimatorSet animatorSet3 = BottomSheet.this.currentSheetAnimation;
                if (animatorSet3 == null || !animatorSet3.equals(animator)) {
                    return;
                }
                BottomSheet bottomSheet = BottomSheet.this;
                bottomSheet.currentSheetAnimation = null;
                bottomSheet.currentSheetAnimationType = 0;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatorSet animatorSet3 = BottomSheet.this.currentSheetAnimation;
                if (animatorSet3 != null && animatorSet3.equals(animator)) {
                    BottomSheet bottomSheet = BottomSheet.this;
                    bottomSheet.currentSheetAnimation = null;
                    bottomSheet.currentSheetAnimationType = 0;
                    bottomSheet.onOpenAnimationEnd();
                    BottomSheetDelegateInterface bottomSheetDelegateInterface = BottomSheet.this.delegate;
                    if (bottomSheetDelegateInterface != null) {
                        bottomSheetDelegateInterface.onOpenAnimationEnd();
                    }
                    BottomSheet bottomSheet2 = BottomSheet.this;
                    if (bottomSheet2.useHardwareLayer) {
                        bottomSheet2.container.setLayerType(0, null);
                    }
                    BottomSheet bottomSheet3 = BottomSheet.this;
                    if (bottomSheet3.isFullscreen) {
                        WindowManager.LayoutParams attributes = bottomSheet3.getWindow().getAttributes();
                        attributes.flags &= -1025;
                        BottomSheet.this.getWindow().setAttributes(attributes);
                    }
                }
                if (BottomSheet.this.pauseAllHeavyOperations) {
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
                }
                BottomSheet.this.notificationsLocker.unlock();
            }
        });
        if (this.pauseAllHeavyOperations) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        }
        this.currentSheetAnimation.start();
    }

    @Override
    public boolean attachedToParent() {
        ContainerView containerView = this.container;
        return containerView != null && containerView.isAttachedToWindow();
    }

    public boolean canDismissWithSwipe() {
        return this.canDismissWithSwipe;
    }

    protected boolean canDismissWithTouchOutside() {
        return this.canDismissWithTouchOutside;
    }

    protected boolean canSwipeToBack(MotionEvent motionEvent) {
        return false;
    }

    protected void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
        }
        this.currentSheetAnimationType = 0;
    }

    @Override
    public void dismiss() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheet.dismiss():void");
    }

    @Override
    public void dismiss(boolean z) {
        dismiss();
    }

    public void dismissInternal() {
        BaseFragment baseFragment = this.attachedFragment;
        if (baseFragment != null) {
            baseFragment.removeSheet(this);
            AndroidUtilities.removeFromParent(this.container);
        } else {
            try {
                super.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e, false);
            }
        }
    }

    public void dismissWithButtonClick(final int i) {
        ValueAnimator ofArgb;
        ValueAnimator ofArgb2;
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        cancelSheetAnimation();
        this.currentSheetAnimationType = 2;
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentSheetAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.TRANSLATION_Y, getContainerViewHeight() + this.keyboardHeight + AndroidUtilities.dp(10.0f) + (this.scrollNavBar ? Math.max(0, Math.min(AndroidUtilities.navigationBarHeight, getBottomInset())) : 0)), ObjectAnimator.ofInt(this.backDrawable, (Property<ColorDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0));
        this.currentSheetAnimation.setDuration(this.cellType == Builder.CELL_TYPE_CALL ? 330L : 180L);
        this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.currentSheetAnimation.addListener(new AnonymousClass7(i));
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        if (this.cellType != Builder.CELL_TYPE_CALL || this.selectedPos == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        int currentTextColor = getItemViews().get(this.selectedPos.intValue()).getTextView().getCurrentTextColor();
        int currentTextColor2 = getItemViews().get(i).getTextView().getCurrentTextColor();
        ofArgb = ValueAnimator.ofArgb(currentTextColor, currentTextColor2);
        ofArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BottomSheet.this.lambda$dismissWithButtonClick$6(valueAnimator);
            }
        });
        ofArgb.setDuration(130L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        ofArgb.setInterpolator(cubicBezierInterpolator);
        ofArgb.start();
        ofArgb2 = ValueAnimator.ofArgb(currentTextColor2, currentTextColor);
        ofArgb2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BottomSheet.this.lambda$dismissWithButtonClick$7(i, valueAnimator);
            }
        });
        ofArgb2.setDuration(130L);
        ofArgb2.setInterpolator(cubicBezierInterpolator);
        ofArgb2.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.dismissed) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void fixNavigationBar() {
        fixNavigationBar(getThemedColor(Theme.key_windowBackgroundGray));
    }

    public void fixNavigationBar(int i) {
        this.drawNavigationBar = !this.occupyNavigationBar;
        this.drawDoubleNavigationBar = true;
        this.scrollNavBar = true;
        this.navBarColorKey = -1;
        this.navBarColor = i;
        setOverlayNavBarColor(i);
    }

    public void forceKeyboardOnDismiss() {
        this.forceKeyboardOnDismiss = true;
    }

    public ColorDrawable getBackDrawable() {
        return this.backDrawable;
    }

    public int getBackgroundPaddingLeft() {
        return this.backgroundPaddingLeft;
    }

    public int getBackgroundPaddingTop() {
        return this.backgroundPaddingTop;
    }

    public int getBottomInset() {
        return (int) (this.bottomInset * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    protected int getBottomSheetWidth(boolean z, int i, int i2) {
        return z ? i : (int) Math.max(i * 0.8f, Math.min(AndroidUtilities.dp(480.0f), i));
    }

    public ContainerView getContainer() {
        return this.container;
    }

    public ViewGroup getContainerView() {
        return this.containerView;
    }

    public int getContainerViewHeight() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getMeasuredHeight();
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public ArrayList<BottomSheetCell> getItemViews() {
        return this.itemViews;
    }

    public int getLeftInset() {
        int systemWindowInsetLeft;
        WindowInsets windowInsets = this.lastInsets;
        if (windowInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
        return (int) (systemWindowInsetLeft * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    @Override
    public int getNavigationBarColor(int i) {
        float f;
        ViewGroup viewGroup;
        if (!attachedToParent() || (viewGroup = this.containerView) == null) {
            f = 0.0f;
        } else if (this.transitionFromRight) {
            f = viewGroup.getAlpha();
        } else {
            f = Utilities.clamp01(1.0f - (this.containerView.getTranslationY() / (((getContainerViewHeight() + this.keyboardHeight) + AndroidUtilities.dp(10.0f)) + (this.scrollNavBar ? Math.max(0, Math.min(AndroidUtilities.navigationBarHeight, getBottomInset())) : 0))));
        }
        return ColorUtils.blendARGB(i, this.navBarColor, f);
    }

    public int getRightInset() {
        int systemWindowInsetRight;
        WindowInsets windowInsets = this.lastInsets;
        if (windowInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
        return (int) (systemWindowInsetRight * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getSheetAnimationType() {
        return this.currentSheetAnimationType;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getStatusBarHeight() {
        return (int) (this.statusBarHeight * (1.0f - this.hideSystemVerticalInsetsProgress));
    }

    public int getTag() {
        return this.tag;
    }

    protected int getTargetOpenTranslationY() {
        return 0;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    @Override
    public View mo1002getWindowView() {
        return this.container;
    }

    @Override
    public boolean isAttachedLightStatusBar() {
        return this.useLightStatusBar;
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public boolean isFocusable() {
        return this.focusable;
    }

    @Override
    public boolean isFullyVisible() {
        return false;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    @Override
    public boolean isShown() {
        return !this.dismissed;
    }

    public void mainContainerDispatchDraw(Canvas canvas) {
    }

    public void makeAttached(BaseFragment baseFragment) {
        if (AndroidUtilities.isTablet()) {
            return;
        }
        this.attachedFragment = baseFragment;
    }

    @Override
    public boolean onAttachedBackPressed() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.attachedFragment == null) {
            super.onBackPressed();
        } else {
            dismiss();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public void onContainerDraw(Canvas canvas) {
    }

    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onContainerTranslationYChanged(float f) {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onCreateInternal();
    }

    protected boolean onCustomCloseAnimation() {
        return false;
    }

    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return false;
    }

    protected boolean onCustomMeasure(View view, int i, int i2) {
        return false;
    }

    public boolean onCustomOpenAnimation() {
        return false;
    }

    public void onDismissAnimationStart() {
    }

    protected void onDismissWithTouchOutside() {
        dismiss();
    }

    protected void onInsetsChanged() {
    }

    public void onOpenAnimationEnd() {
    }

    protected boolean onScrollUp(float f) {
        return false;
    }

    protected void onScrollUpBegin(float f) {
    }

    protected void onScrollUpEnd(float f) {
    }

    public void onSmoothContainerViewLayout(float f) {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void release() {
        dismissInternal();
    }

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            this.container.setBackgroundDrawable(z ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
        if (z) {
            return;
        }
        this.containerView.setTranslationY(0.0f);
    }

    public void setApplyBottomPadding(boolean z) {
        this.applyBottomPadding = z;
    }

    public void setApplyTopPadding(boolean z) {
        this.applyTopPadding = z;
    }

    public void setBackgroundColor(int i) {
        this.shadowDrawable.setColorFilter(i, PorterDuff.Mode.MULTIPLY);
    }

    public void setCalcMandatoryInsets(boolean z) {
        this.calcMandatoryInsets = z;
        this.drawNavigationBar = z;
    }

    public void setCanDismissWithSwipe(boolean z) {
        this.canDismissWithSwipe = z;
    }

    public void setCanDismissWithTouchOutside(boolean z) {
        this.canDismissWithTouchOutside = z;
    }

    public void setCurrentPanTranslationY(float f) {
        this.currentPanTranslationY = f;
        this.container.invalidate();
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.delegate = bottomSheetDelegateInterface;
    }

    public void setDimBehind(boolean z) {
        this.dimBehind = z;
    }

    public void setDimBehindAlpha(int i) {
        this.dimBehindAlpha = i;
    }

    public void setDisableScroll(boolean z) {
        this.disableScroll = z;
    }

    public void setFocusable(boolean z) {
        int i;
        if (this.focusable == z) {
            return;
        }
        this.focusable = z;
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (this.focusable) {
            attributes.softInputMode = 16;
            i = attributes.flags & (-131073);
        } else {
            attributes.softInputMode = 48;
            i = attributes.flags | 131072;
        }
        attributes.flags = i;
        window.setAttributes(attributes);
    }

    public void setHideSystemVerticalInsets(boolean z) {
        ValueAnimator duration = ValueAnimator.ofFloat(this.hideSystemVerticalInsetsProgress, z ? 1.0f : 0.0f).setDuration(180L);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BottomSheet.this.lambda$setHideSystemVerticalInsets$0(valueAnimator);
            }
        });
        duration.start();
    }

    public void setImageReceiverNumLevel(int i, int i2) {
        this.playingImagesLayerNum = i;
        this.openedLayerNum = i2;
    }

    public void setItemColor(int i, int i2, int i3) {
        if (i < 0 || i >= this.itemViews.size()) {
            return;
        }
        BottomSheetCell bottomSheetCell = this.itemViews.get(i);
        bottomSheetCell.textView.setTextColor(i2);
        bottomSheetCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
    }

    public void setItemText(int i, CharSequence charSequence) {
        if (i < 0 || i >= this.itemViews.size()) {
            return;
        }
        this.itemViews.get(i).textView.setText(charSequence);
    }

    public void setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener) {
        this.items = charSequenceArr;
        this.itemIcons = iArr;
        this.onClickListener = onClickListener;
    }

    @Override
    public void setKeyboardHeightFromParent(int i) {
    }

    public void setLastVisible(boolean z) {
        BaseFragment.AttachedSheet.CC.$default$setLastVisible(this, z);
    }

    @Override
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
    }

    @Override
    public void setOnDismissListener(final Runnable runnable) {
        if (runnable != null) {
            setOnHideListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    runnable.run();
                }
            });
        }
    }

    public void setOnHideListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onHideListener = onDismissListener;
    }

    public void setOpenNoDelay(boolean z) {
        this.openNoDelay = z;
    }

    public void setOverlayNavBarColor(int i) {
        this.overlayDrawNavBarColor = i;
        ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
        if (this.attachedFragment != null) {
            LaunchActivity.instance.checkSystemBarColors(true, true, true, false);
            AndroidUtilities.setLightNavigationBar(mo1002getWindowView(), AndroidUtilities.computePerceivedBrightness(getNavigationBarColor(getThemedColor(Theme.key_windowBackgroundGray))) >= 0.721f);
        } else {
            AndroidUtilities.setNavigationBarColor(getWindow(), this.overlayDrawNavBarColor);
            AndroidUtilities.setLightNavigationBar(getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(this.overlayDrawNavBarColor)) > 0.721d);
        }
    }

    public void setShowWithoutAnimation(boolean z) {
        this.showWithoutAnimation = z;
    }

    @Override
    public void setTitle(CharSequence charSequence) {
        setTitle(charSequence, false);
    }

    public void setTitle(CharSequence charSequence, boolean z) {
        this.title = charSequence;
        this.bigTitle = z;
    }

    public void setTitleColor(int i) {
        TextView textView = this.titleView;
        if (textView == null) {
            return;
        }
        textView.setTextColor(i);
    }

    public void setUseLightStatusBar(boolean z) {
        this.useLightStatusBar = z;
        if (Build.VERSION.SDK_INT >= 23) {
            int color = Theme.getColor(Theme.key_actionBarDefault, null, true);
            int systemUiVisibility = this.container.getSystemUiVisibility();
            this.container.setSystemUiVisibility((this.useLightStatusBar && color == -1) ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
        }
        if (this.attachedFragment != null) {
            LaunchActivity.instance.checkSystemBarColors(true, true, true, false);
        }
    }

    protected boolean shouldOverlayCameraViewOverNavBar() {
        return false;
    }

    @Override
    public void show() {
        if (AndroidUtilities.isSafeToShow(getContext())) {
            if (this.attachedFragment != null) {
                onCreateInternal();
            } else {
                super.show();
            }
            setShowing(true);
            if (this.focusable) {
                getWindow().setSoftInputMode(16);
            }
            this.dismissed = false;
            cancelSheetAnimation();
            this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
            if (this.showWithoutAnimation) {
                this.backDrawable.setAlpha(this.dimBehind ? this.dimBehindAlpha : 0);
                this.containerView.setTranslationY(0.0f);
                return;
            }
            this.backDrawable.setAlpha(0);
            this.layoutCount = 2;
            this.containerView.setTranslationY((Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight * (1.0f - this.hideSystemVerticalInsetsProgress) : 0.0f) + r1.getMeasuredHeight() + (this.scrollNavBar ? Math.max(0, Math.min(AndroidUtilities.navigationBarHeight, getBottomInset())) : 0));
            long j = this.openNoDelay ? 0L : 150L;
            if (this.waitingKeyboard) {
                j = 500;
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    BottomSheet bottomSheet = BottomSheet.this;
                    if (bottomSheet.startAnimationRunnable != this || bottomSheet.dismissed) {
                        return;
                    }
                    BottomSheet bottomSheet2 = BottomSheet.this;
                    bottomSheet2.startAnimationRunnable = null;
                    bottomSheet2.startOpenAnimation();
                }
            };
            this.startAnimationRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, j);
        }
    }

    @Override
    public boolean showDialog(Dialog dialog) {
        return false;
    }

    public void smoothContainerViewLayout() {
        this.smoothContainerViewLayoutUntil = System.currentTimeMillis() + 80;
    }

    public void transitionFromRight(boolean z) {
        this.transitionFromRight = z;
    }
}
