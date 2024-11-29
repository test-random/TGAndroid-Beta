package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController$$ExternalSyntheticLambda10;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;

public class AlertDialog extends Dialog implements Drawable.Callback, NotificationCenter.NotificationCenterDelegate {
    private View aboveMessageView;
    private int additioanalHorizontalPadding;
    private float aspectRatio;
    private int backgroundColor;
    private Rect backgroundPaddings;
    float blurAlpha;
    private boolean blurBehind;
    private Bitmap blurBitmap;
    private Matrix blurMatrix;
    private float blurOpacity;
    private Paint blurPaint;
    private BitmapShader blurShader;
    private boolean blurredBackground;
    private boolean blurredNativeBackground;
    private View bottomView;
    protected ViewGroup buttonsLayout;
    private boolean canCacnel;
    private AlertDialog cancelDialog;
    private boolean checkFocusable;
    private AlertDialogView containerView;
    private int[] containerViewLocation;
    private ScrollView contentScrollView;
    private int currentProgress;
    private boolean customMaxHeight;
    private View customView;
    private int customViewHeight;
    private int customViewOffset;
    private int customWidth;
    private int dialogButtonColorKey;
    private float dimAlpha;
    private Paint dimBlurPaint;
    private boolean dimCustom;
    private boolean dimEnabled;
    private boolean dismissDialogByButtons;
    private Runnable dismissRunnable;
    private boolean dismissed;
    private boolean drawBackground;
    private boolean focusable;
    private int[] itemIcons;
    private ArrayList itemViews;
    private CharSequence[] items;
    private int lastScreenWidth;
    private LineProgressView lineProgressView;
    private TextView lineProgressViewPercent;
    private CharSequence message;
    private TextView messageTextView;
    private boolean messageTextViewClickable;
    private DialogInterface.OnClickListener negativeButtonListener;
    private CharSequence negativeButtonText;
    private DialogInterface.OnClickListener neutralButtonListener;
    private CharSequence neutralButtonText;
    private boolean notDrawBackgroundOnTopView;
    private DialogInterface.OnClickListener onBackButtonListener;
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    private Utilities.Callback overridenDissmissListener;
    private DialogInterface.OnClickListener positiveButtonListener;
    private CharSequence positiveButtonText;
    private FrameLayout progressViewContainer;
    private int progressViewStyle;
    private final Theme.ResourcesProvider resourcesProvider;
    private LinearLayout scrollContainer;
    private CharSequence secondTitle;
    private TextView secondTitleTextView;
    private BitmapDrawable[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private boolean[] shadowVisibility;
    private Runnable showRunnable;
    private long shownAt;
    private CharSequence subtitle;
    private TextView subtitleTextView;
    private CharSequence title;
    private FrameLayout titleContainer;
    private SpoilersTextView titleTextView;
    private boolean topAnimationAutoRepeat;
    private int topAnimationId;
    private boolean topAnimationIsNew;
    private Map topAnimationLayerColors;
    private int topAnimationSize;
    private int topBackgroundColor;
    private Drawable topDrawable;
    private int topHeight;
    private RLottieImageView topImageView;
    private int topResId;
    private View topView;
    private boolean verticalButtons;
    private boolean withCancelDialog;

    public static class AlertDialogCell extends FrameLayout {
        private ImageView imageView;
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public AlertDialogCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
        }

        protected int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            this.textView.setText(charSequence);
            if (i == 0) {
                this.imageView.setVisibility(4);
                this.textView.setPadding(0, 0, 0, 0);
            } else {
                this.imageView.setImageResource(i);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
            }
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }
    }

    public class AlertDialogView extends LinearLayout {
        private Paint backgroundPaint;
        private AnimatedFloat blurPaintAlpha;
        private boolean inLayout;

        public AlertDialogView(Context context) {
            super(context);
            this.blurPaintAlpha = new AnimatedFloat(0.0f, this);
            this.backgroundPaint = new Paint(1);
        }

        public void lambda$onLayout$1() {
            AlertDialog alertDialog = AlertDialog.this;
            boolean z = false;
            alertDialog.runShadowAnimation(0, alertDialog.titleTextView != null && AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop());
            AlertDialog alertDialog2 = AlertDialog.this;
            if (alertDialog2.buttonsLayout != null && alertDialog2.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
                z = true;
            }
            alertDialog2.runShadowAnimation(1, z);
            AlertDialog.this.contentScrollView.invalidate();
        }

        public void lambda$onMeasure$0() {
            AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
            int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? AndroidUtilities.isSmallTablet() ? 446.0f : 496.0f : 356.0f);
            Window window = AlertDialog.this.getWindow();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = Math.min(dp2, dp) + AlertDialog.this.backgroundPaddings.left + AlertDialog.this.backgroundPaddings.right;
            try {
                window.setAttributes(layoutParams);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (AlertDialog.this.drawBackground && !AlertDialog.this.blurredBackground) {
                AlertDialog.this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                if (AlertDialog.this.topView == null || !AlertDialog.this.notDrawBackgroundOnTopView) {
                    AlertDialog.this.shadowDrawable.draw(canvas);
                } else {
                    int bottom = AlertDialog.this.topView.getBottom();
                    canvas.save();
                    canvas.clipRect(0, bottom, getMeasuredWidth(), getMeasuredHeight());
                    AlertDialog.this.shadowDrawable.draw(canvas);
                    canvas.restore();
                }
            }
            super.dispatchDraw(canvas);
        }

        @Override
        public void draw(Canvas canvas) {
            float dp;
            if (AlertDialog.this.blurredBackground && !AlertDialog.this.blurredNativeBackground) {
                if (AlertDialog.this.progressViewStyle != 3 || AlertDialog.this.progressViewContainer == null) {
                    dp = AndroidUtilities.dp(10.0f);
                    AndroidUtilities.rectTmp.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
                } else {
                    dp = AndroidUtilities.dp(18.0f);
                    float width = AlertDialog.this.progressViewContainer.getWidth() * AlertDialog.this.progressViewContainer.getScaleX();
                    float height = AlertDialog.this.progressViewContainer.getHeight() * AlertDialog.this.progressViewContainer.getScaleY();
                    AndroidUtilities.rectTmp.set((getWidth() - width) / 2.0f, (getHeight() - height) / 2.0f, (getWidth() + width) / 2.0f, (getHeight() + height) / 2.0f);
                }
                float f = this.blurPaintAlpha.set(AlertDialog.this.blurPaint != null ? 1.0f : 0.0f);
                if (AlertDialog.this.blurPaint != null) {
                    AlertDialog.this.blurPaint.setAlpha((int) (f * 255.0f));
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, dp, dp, AlertDialog.this.blurPaint);
                }
                if (AlertDialog.this.dimBlurPaint == null) {
                    AlertDialog.this.dimBlurPaint = new Paint(1);
                    AlertDialog.this.dimBlurPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (AlertDialog.this.dimAlpha * 255.0f)));
                }
                RectF rectF = AndroidUtilities.rectTmp;
                canvas.drawRoundRect(rectF, dp, dp, AlertDialog.this.dimBlurPaint);
                this.backgroundPaint.setColor(AlertDialog.this.backgroundColor);
                this.backgroundPaint.setAlpha((int) (r4.getAlpha() * ((f * (AlertDialog.this.blurOpacity - 1.0f)) + 1.0f)));
                canvas.drawRoundRect(rectF, dp, dp, this.backgroundPaint);
            }
            super.draw(canvas);
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!AlertDialog.this.withCancelDialog) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            AlertDialog.this.showCancelAlert();
            return false;
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (AlertDialog.this.progressViewStyle == 3) {
                int measuredWidth = ((i3 - i) - AlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                int measuredHeight = ((i4 - i2) - AlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                AlertDialog.this.progressViewContainer.layout(measuredWidth, measuredHeight, AlertDialog.this.progressViewContainer.getMeasuredWidth() + measuredWidth, AlertDialog.this.progressViewContainer.getMeasuredHeight() + measuredHeight);
            } else if (AlertDialog.this.contentScrollView != null) {
                if (AlertDialog.this.onScrollChangedListener == null) {
                    AlertDialog.this.onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public final void onScrollChanged() {
                            AlertDialog.AlertDialogView.this.lambda$onLayout$1();
                        }
                    };
                    AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                }
                AlertDialog.this.onScrollChangedListener.onScrollChanged();
            }
            getLocationOnScreen(AlertDialog.this.containerViewLocation);
            if (AlertDialog.this.blurMatrix == null || AlertDialog.this.blurShader == null) {
                return;
            }
            AlertDialog.this.blurMatrix.reset();
            AlertDialog.this.blurMatrix.postScale(8.0f, 8.0f);
            AlertDialog.this.blurMatrix.postTranslate(-AlertDialog.this.containerViewLocation[0], -AlertDialog.this.containerViewLocation[1]);
            AlertDialog.this.blurShader.setLocalMatrix(AlertDialog.this.blurMatrix);
        }

        @Override
        protected void onMeasure(int r13, int r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AlertDialog.AlertDialogView.onMeasure(int, int):void");
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!AlertDialog.this.withCancelDialog) {
                return super.onTouchEvent(motionEvent);
            }
            AlertDialog.this.showCancelAlert();
            return false;
        }

        @Override
        public void requestLayout() {
            if (this.inLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public static class Builder {
        private AlertDialog alertDialog;
        private final boolean[] red;

        public Builder(Context context) {
            this(context, null);
        }

        public Builder(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            this.red = new boolean[3];
            this.alertDialog = createAlertDialog(context, i, resourcesProvider);
        }

        public Builder(Context context, Theme.ResourcesProvider resourcesProvider) {
            this(context, 0, resourcesProvider);
        }

        public Builder(AlertDialog alertDialog) {
            this.red = new boolean[3];
            this.alertDialog = alertDialog;
        }

        public Builder aboveMessageView(View view) {
            this.alertDialog.aboveMessageView = view;
            return this;
        }

        public Builder addBottomView(View view) {
            this.alertDialog.bottomView = view;
            return this;
        }

        public AlertDialog create() {
            return this.alertDialog;
        }

        protected AlertDialog createAlertDialog(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            return new AlertDialog(context, i, resourcesProvider);
        }

        public Context getContext() {
            return this.alertDialog.getContext();
        }

        public Runnable getDismissRunnable() {
            return this.alertDialog.dismissRunnable;
        }

        public Builder makeCustomMaxHeight() {
            this.alertDialog.customMaxHeight = true;
            return this;
        }

        public Builder makeRed(int i) {
            int i2 = (-i) - 1;
            if (i2 >= 0) {
                boolean[] zArr = this.red;
                if (i2 < zArr.length) {
                    zArr[i2] = true;
                }
            }
            return this;
        }

        public void notDrawBackgroundOnTopView(boolean z) {
            this.alertDialog.notDrawBackgroundOnTopView = z;
            this.alertDialog.blurredBackground = false;
        }

        public Builder overrideDismissListener(Utilities.Callback callback) {
            this.alertDialog.overridenDissmissListener = callback;
            return this;
        }

        public Builder setAdditionalHorizontalPadding(int i) {
            this.alertDialog.additioanalHorizontalPadding = i;
            return this;
        }

        public void setButtonsVertical(boolean z) {
            this.alertDialog.verticalButtons = z;
        }

        public Builder setCheckFocusable(boolean z) {
            this.alertDialog.checkFocusable = z;
            return this;
        }

        public Builder setCustomViewOffset(int i) {
            this.alertDialog.customViewOffset = i;
            return this;
        }

        public Builder setDialogButtonColorKey(int i) {
            this.alertDialog.dialogButtonColorKey = i;
            return this;
        }

        public Builder setDimAlpha(float f) {
            this.alertDialog.dimAlpha = f;
            return this;
        }

        public Builder setDimEnabled(boolean z) {
            this.alertDialog.dimEnabled = z;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.items = charSequenceArr;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.items = charSequenceArr;
            this.alertDialog.itemIcons = iArr;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setMessage(CharSequence charSequence) {
            this.alertDialog.message = charSequence;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean z) {
            this.alertDialog.messageTextViewClickable = z;
            return this;
        }

        public Builder setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.negativeButtonText = charSequence;
            this.alertDialog.negativeButtonListener = onClickListener;
            return this;
        }

        public Builder setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.neutralButtonText = charSequence;
            this.alertDialog.neutralButtonListener = onClickListener;
            return this;
        }

        public Builder setOnBackButtonListener(DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.onBackButtonListener = onClickListener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.alertDialog.setOnCancelListener(onCancelListener);
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.alertDialog.onDismissListener = onDismissListener;
            return this;
        }

        public Builder setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.positiveButtonText = charSequence;
            this.alertDialog.positiveButtonListener = onClickListener;
            return this;
        }

        public Builder setSubtitle(CharSequence charSequence) {
            this.alertDialog.subtitle = charSequence;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.alertDialog.title = charSequence;
            return this;
        }

        public Builder setTopAnimation(int i, int i2, boolean z, int i3) {
            return setTopAnimation(i, i2, z, i3, null);
        }

        public Builder setTopAnimation(int i, int i2, boolean z, int i3, Map map) {
            this.alertDialog.topAnimationId = i;
            this.alertDialog.topAnimationSize = i2;
            this.alertDialog.topAnimationAutoRepeat = z;
            this.alertDialog.topBackgroundColor = i3;
            this.alertDialog.topAnimationLayerColors = map;
            return this;
        }

        public Builder setTopAnimationIsNew(boolean z) {
            this.alertDialog.topAnimationIsNew = z;
            return this;
        }

        public Builder setTopImage(Drawable drawable, int i) {
            this.alertDialog.topDrawable = drawable;
            this.alertDialog.topBackgroundColor = i;
            return this;
        }

        public Builder setTopView(View view) {
            this.alertDialog.topView = view;
            return this;
        }

        public void setTopViewAspectRatio(float f) {
            this.alertDialog.aspectRatio = f;
        }

        public Builder setView(View view) {
            return setView(view, -2);
        }

        public Builder setView(View view, int i) {
            this.alertDialog.customView = view;
            this.alertDialog.customViewHeight = i;
            return this;
        }

        public Builder setWidth(int i) {
            this.alertDialog.customWidth = i;
            return this;
        }

        public AlertDialog show() {
            TextView textView;
            this.alertDialog.show();
            int i = 0;
            while (true) {
                boolean[] zArr = this.red;
                if (i >= zArr.length) {
                    return this.alertDialog;
                }
                if (zArr[i] && (textView = (TextView) this.alertDialog.getButton(-(i + 1))) != null) {
                    textView.setTextColor(this.alertDialog.getThemedColor(Theme.key_text_RedBold));
                }
                i++;
            }
        }
    }

    public AlertDialog(Context context, int i) {
        this(context, i, null);
    }

    public AlertDialog(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.customWidth = -1;
        this.customViewHeight = -2;
        this.shadow = new BitmapDrawable[2];
        this.shadowVisibility = new boolean[2];
        this.shadowAnimation = new AnimatorSet[2];
        this.customViewOffset = 12;
        this.dialogButtonColorKey = Theme.key_dialogButton;
        this.topHeight = 132;
        this.messageTextViewClickable = true;
        this.canCacnel = true;
        this.dismissDialogByButtons = true;
        this.containerViewLocation = new int[2];
        this.checkFocusable = true;
        this.dismissRunnable = new BillingController$$ExternalSyntheticLambda10(this);
        this.showRunnable = new Runnable() {
            @Override
            public final void run() {
                AlertDialog.this.lambda$new$0();
            }
        };
        this.itemViews = new ArrayList();
        this.dimEnabled = true;
        this.dimAlpha = 0.5f;
        this.dimCustom = false;
        this.topAnimationAutoRepeat = true;
        float f = 0.8f;
        this.blurAlpha = 0.8f;
        this.resourcesProvider = resourcesProvider;
        this.progressViewStyle = i;
        int themedColor = getThemedColor(Theme.key_dialogBackground);
        this.backgroundColor = themedColor;
        boolean z = AndroidUtilities.computePerceivedBrightness(themedColor) < 0.721f;
        boolean z2 = supportsNativeBlur() && this.progressViewStyle == 0;
        this.blurredNativeBackground = z2;
        this.blurredBackground = (z2 || (!supportsNativeBlur() && SharedConfig.getDevicePerformanceClass() >= 2 && LiteMode.isEnabled(256))) && z;
        this.backgroundPaddings = new Rect();
        if (i != 3 || this.blurredBackground) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.popup_fixed_alert3).mutate();
            this.shadowDrawable = mutate;
            if (i == 3) {
                f = 0.55f;
            } else if (!z) {
                f = 0.985f;
            }
            this.blurOpacity = f;
            mutate.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.withCancelDialog = this.progressViewStyle == 3;
    }

    private boolean canTextInput(View view) {
        if (view.onCheckIsTextEditor()) {
            return true;
        }
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        while (childCount > 0) {
            childCount--;
            if (canTextInput(viewGroup.getChildAt(childCount))) {
                return true;
            }
        }
        return false;
    }

    public void lambda$inflateContent$1(View view) {
        DialogInterface.OnClickListener onClickListener = this.onClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, ((Integer) view.getTag()).intValue());
        }
        dismiss();
    }

    public void lambda$inflateContent$2(View view) {
        DialogInterface.OnClickListener onClickListener = this.positiveButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public void lambda$inflateContent$3(View view) {
        DialogInterface.OnClickListener onClickListener = this.negativeButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    public void lambda$inflateContent$4(View view) {
        DialogInterface.OnClickListener onClickListener = this.neutralButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public void lambda$inflateContent$5(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        if (this.blurPaint == null) {
            this.blurPaint = new Paint(1);
        }
        this.blurBitmap = bitmap;
        Bitmap bitmap2 = this.blurBitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
        this.blurShader = bitmapShader;
        this.blurPaint.setShader(bitmapShader);
        Matrix matrix = new Matrix();
        this.blurMatrix = matrix;
        matrix.postScale(8.0f, 8.0f);
        Matrix matrix2 = this.blurMatrix;
        int[] iArr = this.containerViewLocation;
        matrix2.postTranslate(-iArr[0], -iArr[1]);
        this.blurShader.setLocalMatrix(this.blurMatrix);
        this.containerView.invalidate();
    }

    public void lambda$new$0() {
        if (isShowing()) {
            return;
        }
        try {
            show();
        } catch (Exception unused) {
        }
    }

    public void lambda$showCancelAlert$6(DialogInterface dialogInterface, int i) {
        DialogInterface.OnCancelListener onCancelListener = this.onCancelListener;
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
        }
        dismiss();
    }

    public void lambda$showCancelAlert$7(DialogInterface dialogInterface) {
        this.cancelDialog = null;
    }

    public void runShadowAnimation(final int i, boolean z) {
        if ((!z || this.shadowVisibility[i]) && (z || !this.shadowVisibility[i])) {
            return;
        }
        this.shadowVisibility[i] = z;
        AnimatorSet animatorSet = this.shadowAnimation[i];
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.shadowAnimation[i] = new AnimatorSet();
        BitmapDrawable bitmapDrawable = this.shadow[i];
        if (bitmapDrawable != null) {
            this.shadowAnimation[i].playTogether(ObjectAnimator.ofInt(bitmapDrawable, "alpha", z ? 255 : 0));
        }
        this.shadowAnimation[i].setDuration(150L);
        this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (AlertDialog.this.shadowAnimation[i] == null || !AlertDialog.this.shadowAnimation[i].equals(animator)) {
                    return;
                }
                AlertDialog.this.shadowAnimation[i] = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (AlertDialog.this.shadowAnimation[i] == null || !AlertDialog.this.shadowAnimation[i].equals(animator)) {
                    return;
                }
                AlertDialog.this.shadowAnimation[i] = null;
            }
        });
        try {
            this.shadowAnimation[i].start();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", Integer.valueOf(this.currentProgress)));
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TextView textView;
        if (i != NotificationCenter.emojiLoaded || (textView = this.messageTextView) == null) {
            return;
        }
        textView.invalidate();
    }

    @Override
    public void dismiss() {
        Bitmap bitmap;
        Utilities.Callback callback = this.overridenDissmissListener;
        if (callback != null) {
            this.overridenDissmissListener = null;
            callback.run(new BillingController$$ExternalSyntheticLambda10(this));
            return;
        }
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        DialogInterface.OnDismissListener onDismissListener = this.onDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
        AlertDialog alertDialog = this.cancelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        try {
            super.dismiss();
        } catch (Throwable unused) {
        }
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
        if (this.blurShader == null || (bitmap = this.blurBitmap) == null) {
            return;
        }
        bitmap.recycle();
        this.blurShader = null;
        this.blurPaint = null;
        this.blurBitmap = null;
    }

    public void dismissUnless(long j) {
        long currentTimeMillis = System.currentTimeMillis() - this.shownAt;
        if (currentTimeMillis < j) {
            AndroidUtilities.runOnUIThread(new BillingController$$ExternalSyntheticLambda10(this), currentTimeMillis - j);
        } else {
            dismiss();
        }
    }

    public View getButton(int i) {
        ViewGroup viewGroup = this.buttonsLayout;
        if (viewGroup != null) {
            return viewGroup.findViewWithTag(Integer.valueOf(i));
        }
        return null;
    }

    public ViewGroup getButtonsLayout() {
        return this.buttonsLayout;
    }

    public AlertDialogView getContainerView() {
        return this.containerView;
    }

    public int getItemsCount() {
        return this.itemViews.size();
    }

    public ArrayList getThemeDescriptions() {
        return null;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public android.view.View inflateContent(boolean r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AlertDialog.inflateContent(boolean):android.view.View");
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DialogInterface.OnClickListener onClickListener = this.onBackButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        inflateContent(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    public void redPositive() {
        TextView textView = (TextView) getButton(-1);
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_text_RedBold));
        }
    }

    @Override
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.postDelayed(runnable, j);
        }
    }

    public void setBackgroundColor(int i) {
        this.backgroundColor = i;
        Drawable drawable = this.shadowDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setBlurParams(float f, boolean z, boolean z2) {
        this.blurAlpha = f;
        this.blurBehind = z;
        this.blurredBackground = z2;
    }

    public void setCanCancel(boolean z) {
        this.canCacnel = z;
    }

    public void setCancelDialog(boolean z) {
        this.withCancelDialog = z;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
    }

    public void setDismissDialogByButtons(boolean z) {
        this.dismissDialogByButtons = z;
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

    public void setItemColor(int i, int i2, int i3) {
        if (i < 0 || i >= this.itemViews.size()) {
            return;
        }
        AlertDialogCell alertDialogCell = (AlertDialogCell) this.itemViews.get(i);
        alertDialogCell.textView.setTextColor(i2);
        alertDialogCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
    }

    public void setMessage(CharSequence charSequence) {
        TextView textView;
        int i;
        this.message = charSequence;
        if (this.messageTextView != null) {
            if (TextUtils.isEmpty(charSequence)) {
                textView = this.messageTextView;
                i = 8;
            } else {
                this.messageTextView.setText(this.message);
                textView = this.messageTextView;
                i = 0;
            }
            textView.setVisibility(i);
        }
    }

    public void setMessageLineSpacing(float f) {
        TextView textView = this.messageTextView;
        if (textView != null) {
            textView.setLineSpacing(AndroidUtilities.dp(f), 1.0f);
        }
    }

    public void setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.negativeButtonText = charSequence;
        this.negativeButtonListener = onClickListener;
    }

    public void setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.neutralButtonText = charSequence;
        this.neutralButtonListener = onClickListener;
    }

    @Override
    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        super.setOnCancelListener(onCancelListener);
    }

    public void setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.positiveButtonText = charSequence;
        this.positiveButtonListener = onClickListener;
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener onClickListener) {
        this.positiveButtonListener = onClickListener;
    }

    public void setProgress(int i) {
        this.currentProgress = i;
        LineProgressView lineProgressView = this.lineProgressView;
        if (lineProgressView != null) {
            lineProgressView.setProgress(i / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    public void setTextColor(int i) {
        SpoilersTextView spoilersTextView = this.titleTextView;
        if (spoilersTextView != null) {
            spoilersTextView.setTextColor(i);
        }
        TextView textView = this.messageTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public void setTextSize(int i, int i2) {
        SpoilersTextView spoilersTextView = this.titleTextView;
        if (spoilersTextView != null) {
            spoilersTextView.setTextSize(1, i);
        }
        TextView textView = this.messageTextView;
        if (textView != null) {
            textView.setTextSize(1, i2);
        }
    }

    @Override
    public void setTitle(CharSequence charSequence) {
        this.title = charSequence;
        SpoilersTextView spoilersTextView = this.titleTextView;
        if (spoilersTextView != null) {
            spoilersTextView.setText(charSequence);
        }
    }

    @Override
    public void show() {
        if (AndroidUtilities.isSafeToShow(getContext())) {
            this.dismissed = false;
            super.show();
            FrameLayout frameLayout = this.progressViewContainer;
            if (frameLayout != null && this.progressViewStyle == 3) {
                frameLayout.setScaleX(0.0f);
                this.progressViewContainer.setScaleY(0.0f);
                this.progressViewContainer.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new OvershootInterpolator(1.3f)).setDuration(190L).start();
            }
            this.shownAt = System.currentTimeMillis();
        }
    }

    public void showCancelAlert() {
        if (this.canCacnel && this.cancelDialog == null) {
            Builder builder = new Builder(getContext(), this.resourcesProvider);
            builder.setTitle(LocaleController.getString(R.string.StopLoadingTitle));
            builder.setMessage(LocaleController.getString(R.string.StopLoading));
            builder.setPositiveButton(LocaleController.getString(R.string.WaitMore), null);
            builder.setNegativeButton(LocaleController.getString(R.string.Stop), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertDialog.this.lambda$showCancelAlert$6(dialogInterface, i);
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    AlertDialog.this.lambda$showCancelAlert$7(dialogInterface);
                }
            });
            try {
                this.cancelDialog = builder.show();
            } catch (Exception unused) {
            }
        }
    }

    public void showDelayed(long j) {
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
        AndroidUtilities.runOnUIThread(this.showRunnable, j);
    }

    protected boolean supportsNativeBlur() {
        return false;
    }

    @Override
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.removeCallbacks(runnable);
        }
    }
}
