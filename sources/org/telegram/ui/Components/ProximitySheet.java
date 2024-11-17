package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.ProximitySheet;

public class ProximitySheet extends FrameLayout {
    private int backgroundPaddingLeft;
    private Paint backgroundPaint;
    private TextView buttonTextView;
    private ViewGroup containerView;
    private AnimatorSet currentAnimation;
    private AnimatorSet currentSheetAnimation;
    private int currentSheetAnimationType;
    private TLRPC.User currentUser;
    private LinearLayout customView;
    private boolean dismissed;
    private TextView infoTextView;
    private NumberPicker kmPicker;
    private NumberPicker mPicker;
    private boolean maybeStartTracking;
    private Runnable onDismissCallback;
    private onRadiusPickerChange onRadiusChange;
    private Interpolator openInterpolator;
    private boolean radiusSet;
    private android.graphics.Rect rect;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private int totalWidth;
    private int touchSlop;
    private boolean useFastDismiss;
    private boolean useHardwareLayer;
    private boolean useImperialSystem;
    private VelocityTracker velocityTracker;

    public class AnonymousClass6 extends AnimatorListenerAdapter {
        AnonymousClass6() {
        }

        public void lambda$onAnimationEnd$0() {
            try {
                ProximitySheet.this.dismissInternal();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (ProximitySheet.this.currentSheetAnimation == null || !ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                return;
            }
            ProximitySheet.this.currentSheetAnimation = null;
            ProximitySheet.this.currentSheetAnimationType = 0;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                ProximitySheet.this.currentSheetAnimation = null;
                ProximitySheet.this.currentSheetAnimationType = 0;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ProximitySheet.AnonymousClass6.this.lambda$onAnimationEnd$0();
                    }
                });
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
        }
    }

    public interface onRadiusPickerChange {
        boolean run(boolean z, int i);
    }

    public ProximitySheet(Context context, TLRPC.User user, onRadiusPickerChange onradiuspickerchange, final onRadiusPickerChange onradiuspickerchange2, Runnable runnable) {
        super(context);
        this.velocityTracker = null;
        this.startedTrackingPointerId = -1;
        this.maybeStartTracking = false;
        this.startedTracking = false;
        this.currentAnimation = null;
        this.rect = new android.graphics.Rect();
        this.backgroundPaint = new Paint();
        this.useHardwareLayer = true;
        this.openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        setWillNotDraw(false);
        this.onDismissCallback = runnable;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        android.graphics.Rect rect = new android.graphics.Rect();
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        mutate.getPadding(rect);
        this.backgroundPaddingLeft = rect.left;
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.containerView = frameLayout;
        frameLayout.setBackgroundDrawable(mutate);
        this.containerView.setPadding(this.backgroundPaddingLeft, (AndroidUtilities.dp(8.0f) + rect.top) - 1, this.backgroundPaddingLeft, 0);
        this.containerView.setVisibility(4);
        addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        this.useImperialSystem = LocaleController.getUseImperialSystemType();
        this.currentUser = user;
        this.onRadiusChange = onradiuspickerchange;
        NumberPicker numberPicker = new NumberPicker(context);
        this.kmPicker = numberPicker;
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        this.kmPicker.setItemCount(5);
        NumberPicker numberPicker2 = new NumberPicker(context);
        this.mPicker = numberPicker2;
        numberPicker2.setItemCount(5);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        LinearLayout linearLayout = new LinearLayout(context) {
            boolean ignoreLayout = false;

            @Override
            protected void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                ProximitySheet.this.kmPicker.setItemCount(i3);
                ProximitySheet.this.mPicker.setItemCount(i3);
                ProximitySheet.this.kmPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                ProximitySheet.this.mPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                this.ignoreLayout = false;
                ProximitySheet.this.totalWidth = View.MeasureSpec.getSize(i);
                if (ProximitySheet.this.totalWidth != 0) {
                    ProximitySheet.this.updateText(false, false);
                }
                super.onMeasure(i, i2);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.customView = linearLayout;
        linearLayout.setOrientation(1);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.customView.addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.LocationNotifiation));
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$0;
                lambda$new$0 = ProximitySheet.lambda$new$0(view, motionEvent);
                return lambda$new$0;
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setWeightSum(1.0f);
        this.customView.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        System.currentTimeMillis();
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.infoTextView = new TextView(context);
        this.buttonTextView = new TextView(context) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout2.addView(this.kmPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.kmPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i) {
                String lambda$new$1;
                lambda$new$1 = ProximitySheet.this.lambda$new$1(i);
                return lambda$new$1;
            }
        });
        this.kmPicker.setMinValue(0);
        this.kmPicker.setMaxValue(10);
        this.kmPicker.setWrapSelectorWheel(false);
        this.kmPicker.setTextOffset(AndroidUtilities.dp(20.0f));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public final void onValueChange(NumberPicker numberPicker3, int i, int i2) {
                ProximitySheet.this.lambda$new$2(numberPicker3, i, i2);
            }
        };
        this.kmPicker.setOnValueChangedListener(onValueChangeListener);
        this.mPicker.setMinValue(0);
        this.mPicker.setMaxValue(10);
        this.mPicker.setWrapSelectorWheel(false);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(20.0f));
        linearLayout2.addView(this.mPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.mPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public final String format(int i) {
                String lambda$new$3;
                lambda$new$3 = ProximitySheet.this.lambda$new$3(i);
                return lambda$new$3;
            }
        });
        this.mPicker.setOnValueChangedListener(onValueChangeListener);
        this.kmPicker.setValue(0);
        this.mPicker.setValue(6);
        this.customView.addView(frameLayout3, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setMaxLines(2);
        this.buttonTextView.setTypeface(AndroidUtilities.bold());
        this.buttonTextView.setBackgroundDrawable(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
        frameLayout3.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ProximitySheet.this.lambda$new$4(onradiuspickerchange2, view);
            }
        });
        this.infoTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.infoTextView.setGravity(17);
        this.infoTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        this.infoTextView.setTextSize(1, 14.0f);
        this.infoTextView.setAlpha(0.0f);
        this.infoTextView.setScaleX(0.5f);
        this.infoTextView.setScaleY(0.5f);
        frameLayout3.addView(this.infoTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
    }

    private void cancelCurrentAnimation() {
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    private void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    private void checkDismiss(float f, float f2) {
        if ((this.containerView.getTranslationY() >= AndroidUtilities.getPixelsInCM(0.8f, false) || (f2 >= 3500.0f && Math.abs(f2) >= Math.abs(f))) && (f2 >= 0.0f || Math.abs(f2) < 3500.0f)) {
            this.useFastDismiss = true;
            dismiss();
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.TRANSLATION_Y, 0.0f));
        this.currentAnimation.setDuration((int) ((Math.max(0.0f, r1) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f));
        this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (ProximitySheet.this.currentAnimation != null && ProximitySheet.this.currentAnimation.equals(animator)) {
                    ProximitySheet.this.currentAnimation = null;
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
            }
        });
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentAnimation.start();
    }

    public void dismissInternal() {
        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        this.onDismissCallback.run();
    }

    public static boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public String lambda$new$1(int i) {
        return this.useImperialSystem ? LocaleController.formatString("MilesShort", R.string.MilesShort, Integer.valueOf(i)) : LocaleController.formatString("KMetersShort", R.string.KMetersShort, Integer.valueOf(i));
    }

    public void lambda$new$2(NumberPicker numberPicker, int i, int i2) {
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        updateText(true, true);
    }

    public String lambda$new$3(int i) {
        if (this.useImperialSystem) {
            if (i == 1) {
                return LocaleController.formatString("FootsShort", R.string.FootsShort, 250);
            }
            if (i > 1) {
                i--;
            }
            return String.format(Locale.US, ".%d", Integer.valueOf(i));
        }
        if (i == 1) {
            return LocaleController.formatString("MetersShort", R.string.MetersShort, 50);
        }
        if (i > 1) {
            i--;
        }
        return LocaleController.formatString("MetersShort", R.string.MetersShort, Integer.valueOf(i * 100));
    }

    public void lambda$new$4(onRadiusPickerChange onradiuspickerchange, View view) {
        if (this.buttonTextView.getTag() == null && onradiuspickerchange.run(true, (int) Math.max(1.0f, getValue()))) {
            dismiss();
        }
    }

    private void startOpenAnimation() {
        if (this.dismissed) {
            return;
        }
        this.containerView.setVisibility(0);
        if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
            setLayerType(2, null);
        }
        this.containerView.setTranslationY(r2.getMeasuredHeight());
        this.currentSheetAnimationType = 1;
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentSheetAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.TRANSLATION_Y, 0.0f));
        this.currentSheetAnimation.setDuration(400L);
        this.currentSheetAnimation.setStartDelay(20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (ProximitySheet.this.currentSheetAnimation == null || !ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                    return;
                }
                ProximitySheet.this.currentSheetAnimation = null;
                ProximitySheet.this.currentSheetAnimationType = 0;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animator)) {
                    ProximitySheet.this.currentSheetAnimation = null;
                    ProximitySheet.this.currentSheetAnimationType = 0;
                    if (ProximitySheet.this.useHardwareLayer) {
                        ProximitySheet.this.setLayerType(0, null);
                    }
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
            }
        });
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
    }

    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        cancelSheetAnimation();
        this.currentSheetAnimationType = 2;
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentSheetAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, (Property<ViewGroup, Float>) View.TRANSLATION_Y, r3.getMeasuredHeight() + AndroidUtilities.dp(10.0f)));
        if (this.useFastDismiss) {
            float measuredHeight = this.containerView.getMeasuredHeight();
            this.currentSheetAnimation.setDuration(Math.max(60, (int) (((measuredHeight - this.containerView.getTranslationY()) * 250.0f) / measuredHeight)));
            this.useFastDismiss = false;
        } else {
            this.currentSheetAnimation.setDuration(250L);
        }
        this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.currentSheetAnimation.addListener(new AnonymousClass6());
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.dismissed) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public View getCustomView() {
        return this.customView;
    }

    public boolean getRadiusSet() {
        return this.radiusSet;
    }

    public float getValue() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProximitySheet.getValue():float");
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.dismissed || processTouchEvent(motionEvent, true);
    }

    @Override
    protected void onLayout(boolean r8, int r9, int r10, int r11, int r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ProximitySheet.onLayout(boolean, int, int, int, int):void");
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        setMeasuredDimension(size, size2);
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec((this.backgroundPaddingLeft * 2) + size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8 && childAt != this.containerView) {
                measureChildWithMargins(childAt, View.MeasureSpec.makeMeasureSpec(size, 1073741824), 0, View.MeasureSpec.makeMeasureSpec(size2, 1073741824), 0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.dismissed || processTouchEvent(motionEvent, false);
    }

    boolean processTouchEvent(MotionEvent motionEvent, boolean z) {
        if (this.dismissed) {
            return false;
        }
        if (motionEvent == null || (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2) || this.startedTracking || this.maybeStartTracking || motionEvent.getPointerCount() != 1)) {
            if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float abs = Math.abs((int) (motionEvent.getX() - this.startedTrackingX));
                float y = ((int) motionEvent.getY()) - this.startedTrackingY;
                this.velocityTracker.addMovement(motionEvent);
                if (this.maybeStartTracking && !this.startedTracking && y > 0.0f && y / 3.0f > Math.abs(abs) && Math.abs(y) >= this.touchSlop) {
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float translationY = this.containerView.getTranslationY() + y;
                    this.containerView.setTranslationY(translationY >= 0.0f ? translationY : 0.0f);
                    this.startedTrackingY = (int) motionEvent.getY();
                }
            } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                float translationY2 = this.containerView.getTranslationY();
                if (this.startedTracking || translationY2 != 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                } else {
                    this.maybeStartTracking = false;
                }
                this.startedTracking = false;
                VelocityTracker velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
        } else {
            this.startedTrackingX = (int) motionEvent.getX();
            int y2 = (int) motionEvent.getY();
            this.startedTrackingY = y2;
            if (y2 < this.containerView.getTop() || this.startedTrackingX < this.containerView.getLeft() || this.startedTrackingX > this.containerView.getRight()) {
                requestDisallowInterceptTouchEvent(true);
                dismiss();
                return true;
            }
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            cancelCurrentAnimation();
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.clear();
            }
        }
        return (!z && this.maybeStartTracking) || this.startedTracking;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void setRadiusSet() {
        this.radiusSet = true;
    }

    public void show() {
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        startOpenAnimation();
        updateText(true, false);
    }

    public void updateText(boolean z, boolean z2) {
        ViewPropertyAnimator scaleY;
        float value = getValue();
        String formatDistance = LocaleController.formatDistance(value, 2, Boolean.valueOf(this.useImperialSystem));
        if (this.onRadiusChange.run(z, (int) value) || this.currentUser == null) {
            if (this.currentUser == null) {
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonGroup", R.string.LocationNotifiationButtonGroup, formatDistance));
            } else {
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonUser", R.string.LocationNotifiationButtonUser, TextUtils.ellipsize(UserObject.getFirstName(this.currentUser), this.buttonTextView.getPaint(), Math.max(AndroidUtilities.dp(10.0f), (int) (((this.totalWidth - AndroidUtilities.dp(94.0f)) * 1.5f) - ((int) Math.ceil(this.buttonTextView.getPaint().measureText(LocaleController.getString(r13)))))), TextUtils.TruncateAt.END), formatDistance));
            }
            if (this.buttonTextView.getTag() == null) {
                return;
            }
            this.buttonTextView.setTag(null);
            this.buttonTextView.animate().setDuration(180L).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
            scaleY = this.infoTextView.animate().setDuration(180L).alpha(0.0f).scaleX(0.5f).scaleY(0.5f);
        } else {
            this.infoTextView.setText(LocaleController.formatString("LocationNotifiationCloser", R.string.LocationNotifiationCloser, formatDistance));
            if (this.buttonTextView.getTag() != null) {
                return;
            }
            this.buttonTextView.setTag(1);
            this.buttonTextView.animate().setDuration(180L).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
            scaleY = this.infoTextView.animate().setDuration(180L).alpha(1.0f).scaleX(1.0f).scaleY(1.0f);
        }
        scaleY.start();
    }
}
