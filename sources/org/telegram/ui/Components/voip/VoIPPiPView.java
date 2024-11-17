package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFragment;
import org.webrtc.EglBase;

public class VoIPPiPView implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    public static int bottomInset = 0;
    private static VoIPPiPView expandedInstance = null;
    private static VoIPPiPView instance = null;
    public static boolean switchingToPip = false;
    public static int topInset;
    ValueAnimator animatorToCameraMini;
    boolean callingUserIsVideo;
    public final VoIPTextureView callingUserTextureView;
    ImageView closeIcon;
    private int currentAccount;
    boolean currentUserIsVideo;
    public final VoIPTextureView currentUserTextureView;
    ImageView enlargeIcon;
    ValueAnimator expandAnimator;
    public boolean expanded;
    private boolean expandedAnimationInProgress;
    FloatingView floatingView;
    AnimatorSet moveToBoundsAnimator;
    boolean moving;
    public final int parentHeight;
    public final int parentWidth;
    float progressToCameraMini;
    long startTime;
    float startX;
    float startY;
    View topShadow;
    public WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    public FrameLayout windowView;
    public int xOffset;
    public int yOffset;
    ValueAnimator.AnimatorUpdateListener animatorToCameraMiniUpdater = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPPiPView.this.lambda$new$0(valueAnimator);
        }
    };
    float[] point = new float[2];
    int animationIndex = -1;
    Runnable collapseRunnable = new Runnable() {
        @Override
        public void run() {
            if (VoIPPiPView.instance != null) {
                VoIPPiPView.instance.floatingView.expand(false);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.windowLayoutParams.x = (int) floatValue;
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager windowManager = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                windowManager.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.windowLayoutParams.y = (int) floatValue;
            if (voIPPiPView.windowView.getParent() != null) {
                WindowManager windowManager = VoIPPiPView.this.windowManager;
                VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
                windowManager.updateViewLayout(voIPPiPView2.windowView, voIPPiPView2.windowLayoutParams);
            }
        }
    };

    public class FloatingView extends FrameLayout {
        float bottomPadding;
        float leftPadding;
        float rightPadding;
        float topPadding;
        float touchSlop;

        public class AnonymousClass3 extends AnimatorListenerAdapter {
            final boolean val$expanded;

            AnonymousClass3(boolean z) {
                this.val$expanded = z;
            }

            public void lambda$onAnimationEnd$0(boolean z) {
                if (VoIPPiPView.instance == null || VoIPPiPView.expandedInstance == null) {
                    return;
                }
                VoIPPiPView.expandedInstance.windowView.setAlpha(0.0f);
                VoIPPiPView.expandedInstance.finishInternal();
                VoIPPiPView.this.expandedAnimationInProgress = false;
                if (z) {
                    AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000L);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (VoIPPiPView.expandedInstance == null) {
                    return;
                }
                FloatingView.this.swapRender(VoIPPiPView.expandedInstance, VoIPPiPView.instance);
                VoIPPiPView.instance.windowView.setAlpha(1.0f);
                AndroidUtilities.setPreferredMaxRefreshRate(VoIPPiPView.this.windowManager, VoIPPiPView.instance.windowView, VoIPPiPView.instance.windowLayoutParams);
                VoIPPiPView.this.windowManager.addView(VoIPPiPView.instance.windowView, VoIPPiPView.instance.windowLayoutParams);
                final boolean z = this.val$expanded;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        VoIPPiPView.FloatingView.AnonymousClass3.this.lambda$onAnimationEnd$0(z);
                    }
                }, 64L);
            }
        }

        public FloatingView(Context context) {
            super(context);
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f / view.getScaleX()) * AndroidUtilities.dp(4.0f));
                    }
                });
                setClipToOutline(true);
            }
        }

        public void expand(boolean z) {
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            if (VoIPPiPView.instance == null || VoIPPiPView.this.expandedAnimationInProgress || VoIPPiPView.instance.expanded == z) {
                return;
            }
            VoIPPiPView.instance.expanded = z;
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            float f = voIPPiPView.parentWidth;
            float f2 = voIPPiPView.xOffset * 2;
            float f3 = (f * 0.25f) + f2;
            float f4 = voIPPiPView.parentHeight;
            float f5 = voIPPiPView.yOffset * 2;
            float f6 = (0.25f * f4) + f5;
            float f7 = (f * 0.4f) + f2;
            float f8 = (f4 * 0.4f) + f5;
            voIPPiPView.expandedAnimationInProgress = true;
            if (!z) {
                if (VoIPPiPView.expandedInstance == null) {
                    return;
                }
                VoIPPiPView.expandedInstance.floatingView.getRelativePosition(VoIPPiPView.this.point);
                float[] fArr = VoIPPiPView.this.point;
                float f9 = fArr[0];
                float f10 = fArr[1];
                VoIPPiPView.instance.windowLayoutParams.x = (int) (VoIPPiPView.expandedInstance.windowLayoutParams.x + ((f7 - f3) * f9));
                VoIPPiPView.instance.windowLayoutParams.y = (int) (VoIPPiPView.expandedInstance.windowLayoutParams.y + ((f8 - f6) * f10));
                final float scaleX = VoIPPiPView.this.floatingView.getScaleX() * 0.625f;
                VoIPPiPView.expandedInstance.floatingView.setPivotX(f9 * VoIPPiPView.this.parentWidth * 0.4f);
                VoIPPiPView.expandedInstance.floatingView.setPivotY(f10 * VoIPPiPView.this.parentHeight * 0.4f);
                showUi(false);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        VoIPPiPView.FloatingView.lambda$expand$2(scaleX, valueAnimator);
                    }
                });
                ofFloat.setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                ofFloat.addListener(new AnonymousClass3(z));
                ofFloat.start();
                VoIPPiPView.this.expandAnimator = ofFloat;
                return;
            }
            Context context = VoIPPiPView.instance.windowView.getContext();
            VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
            WindowManager.LayoutParams createWindowLayoutParams = VoIPPiPView.createWindowLayoutParams(context, voIPPiPView2.parentWidth, voIPPiPView2.parentHeight, 0.4f);
            Context context2 = getContext();
            VoIPPiPView voIPPiPView3 = VoIPPiPView.this;
            final VoIPPiPView voIPPiPView4 = new VoIPPiPView(context2, voIPPiPView3.parentWidth, voIPPiPView3.parentHeight, true);
            getRelativePosition(VoIPPiPView.this.point);
            VoIPPiPView voIPPiPView5 = VoIPPiPView.this;
            float[] fArr2 = voIPPiPView5.point;
            float f11 = fArr2[0];
            float f12 = fArr2[1];
            WindowManager.LayoutParams layoutParams = voIPPiPView5.windowLayoutParams;
            createWindowLayoutParams.x = (int) (layoutParams.x - ((f7 - f3) * f11));
            createWindowLayoutParams.y = (int) (layoutParams.y - ((f8 - f6) * f12));
            AndroidUtilities.setPreferredMaxRefreshRate(voIPPiPView5.windowManager, voIPPiPView4.windowView, createWindowLayoutParams);
            VoIPPiPView.this.windowManager.addView(voIPPiPView4.windowView, createWindowLayoutParams);
            voIPPiPView4.windowView.setAlpha(1.0f);
            voIPPiPView4.windowLayoutParams = createWindowLayoutParams;
            voIPPiPView4.windowManager = VoIPPiPView.this.windowManager;
            VoIPPiPView unused = VoIPPiPView.expandedInstance = voIPPiPView4;
            swapRender(VoIPPiPView.instance, VoIPPiPView.expandedInstance);
            final float scaleX2 = VoIPPiPView.this.floatingView.getScaleX() * 0.625f;
            voIPPiPView4.floatingView.setPivotX(f11 * VoIPPiPView.this.parentWidth * 0.4f);
            voIPPiPView4.floatingView.setPivotY(f12 * VoIPPiPView.this.parentHeight * 0.4f);
            voIPPiPView4.floatingView.setScaleX(scaleX2);
            voIPPiPView4.floatingView.setScaleY(scaleX2);
            VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
            VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
            VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VoIPPiPView.FloatingView.this.lambda$expand$1(scaleX2, voIPPiPView4);
                }
            }, 64L);
        }

        public void getRelativePosition(float[] fArr) {
            Point point = AndroidUtilities.displaySize;
            float f = point.x;
            float f2 = point.y;
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            float left = voIPPiPView.windowLayoutParams.x + voIPPiPView.floatingView.getLeft();
            float f3 = this.leftPadding;
            fArr[0] = (left - f3) / (((f - f3) - this.rightPadding) - VoIPPiPView.this.floatingView.getMeasuredWidth());
            VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
            float top = voIPPiPView2.windowLayoutParams.y + voIPPiPView2.floatingView.getTop();
            float f4 = this.topPadding;
            fArr[1] = (top - f4) / (((f2 - f4) - this.bottomPadding) - VoIPPiPView.this.floatingView.getMeasuredHeight());
            fArr[0] = Math.min(1.0f, Math.max(0.0f, fArr[0]));
            fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1]));
        }

        public static void lambda$expand$0(float f, float f2, VoIPPiPView voIPPiPView, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f3 = (f * (1.0f - floatValue)) + (f2 * floatValue);
            voIPPiPView.floatingView.setScaleX(f3);
            voIPPiPView.floatingView.setScaleY(f3);
            voIPPiPView.floatingView.invalidate();
            voIPPiPView.windowView.invalidate();
            if (Build.VERSION.SDK_INT >= 21) {
                voIPPiPView.floatingView.invalidateOutline();
            }
        }

        public void lambda$expand$1(final float f, final VoIPPiPView voIPPiPView) {
            if (VoIPPiPView.expandedInstance == null) {
                return;
            }
            VoIPPiPView.this.windowView.setAlpha(0.0f);
            try {
                VoIPPiPView.this.windowManager.removeView(VoIPPiPView.this.windowView);
            } catch (Throwable th) {
                FileLog.e(th);
            }
            animate().cancel();
            showUi(true);
            final float f2 = 1.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoIPPiPView.FloatingView.lambda$expand$0(f, f2, voIPPiPView, valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    VoIPPiPView.this.expandedAnimationInProgress = false;
                }
            });
            ofFloat.setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.start();
            VoIPPiPView.this.expandAnimator = ofFloat;
        }

        public static void lambda$expand$2(float f, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f2 = (1.0f - floatValue) + (f * floatValue);
            if (VoIPPiPView.expandedInstance != null) {
                VoIPPiPView.expandedInstance.floatingView.setScaleX(f2);
                VoIPPiPView.expandedInstance.floatingView.setScaleY(f2);
                VoIPPiPView.expandedInstance.floatingView.invalidate();
                if (Build.VERSION.SDK_INT >= 21) {
                    VoIPPiPView.expandedInstance.floatingView.invalidateOutline();
                }
                VoIPPiPView.expandedInstance.windowView.invalidate();
            }
        }

        private void showUi(boolean z) {
            if (VoIPPiPView.expandedInstance == null) {
                return;
            }
            if (z) {
                VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
            }
            ViewPropertyAnimator duration = VoIPPiPView.expandedInstance.topShadow.animate().alpha(z ? 1.0f : 0.0f).setDuration(300L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            VoIPPiPView.expandedInstance.closeIcon.animate().alpha(z ? 1.0f : 0.0f).setDuration(300L).setInterpolator(cubicBezierInterpolator).start();
            VoIPPiPView.expandedInstance.enlargeIcon.animate().alpha(z ? 1.0f : 0.0f).setDuration(300L).setInterpolator(cubicBezierInterpolator).start();
        }

        public void swapRender(VoIPPiPView voIPPiPView, VoIPPiPView voIPPiPView2) {
            voIPPiPView2.currentUserTextureView.setStub(voIPPiPView.currentUserTextureView);
            voIPPiPView2.callingUserTextureView.setStub(voIPPiPView.callingUserTextureView);
            voIPPiPView.currentUserTextureView.renderer.release();
            voIPPiPView.callingUserTextureView.renderer.release();
            EglBase eglBase = VideoCapturerDevice.eglBase;
            if (eglBase == null) {
                return;
            }
            voIPPiPView2.currentUserTextureView.renderer.init(eglBase.getEglBaseContext(), null);
            voIPPiPView2.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().setSinks(voIPPiPView2.currentUserTextureView.renderer, voIPPiPView2.callingUserTextureView.renderer);
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            VoIPPiPView.this.currentUserTextureView.setPivotX(r0.callingUserTextureView.getMeasuredWidth());
            VoIPPiPView.this.currentUserTextureView.setPivotY(r0.callingUserTextureView.getMeasuredHeight());
            VoIPPiPView.this.currentUserTextureView.setTranslationX((-AndroidUtilities.dp(4.0f)) * (1.0f / getScaleX()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setTranslationY((-AndroidUtilities.dp(4.0f)) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setRoundCorners(AndroidUtilities.dp(8.0f) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView voIPPiPView = VoIPPiPView.this;
            voIPPiPView.currentUserTextureView.setScaleX(((1.0f - voIPPiPView.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView voIPPiPView2 = VoIPPiPView.this;
            voIPPiPView2.currentUserTextureView.setScaleY(((1.0f - voIPPiPView2.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView voIPPiPView3 = VoIPPiPView.this;
            voIPPiPView3.currentUserTextureView.setAlpha(Math.min(1.0f, 1.0f - voIPPiPView3.progressToCameraMini));
            super.dispatchDraw(canvas);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.leftPadding = AndroidUtilities.dp(16.0f);
            this.rightPadding = AndroidUtilities.dp(16.0f);
            this.topPadding = AndroidUtilities.dp(60.0f);
            this.bottomPadding = AndroidUtilities.dp(16.0f);
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }

    public VoIPPiPView(final Context context, int i, int i2, boolean z) {
        this.parentWidth = i;
        this.parentHeight = i2;
        float f = i2 * 0.4f;
        this.yOffset = ((int) ((f * 1.05f) - f)) / 2;
        float f2 = i * 0.4f;
        this.xOffset = ((int) ((1.05f * f2) - f2)) / 2;
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.calls_pip_outershadow);
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.save();
                canvas.scale(VoIPPiPView.this.floatingView.getScaleX(), VoIPPiPView.this.floatingView.getScaleY(), VoIPPiPView.this.floatingView.getLeft() + VoIPPiPView.this.floatingView.getPivotX(), VoIPPiPView.this.floatingView.getTop() + VoIPPiPView.this.floatingView.getPivotY());
                drawable.setBounds(VoIPPiPView.this.floatingView.getLeft() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getTop() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getRight() + AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getBottom() + AndroidUtilities.dp(2.0f));
                drawable.draw(canvas);
                canvas.restore();
                super.onDraw(canvas);
            }
        };
        this.windowView = frameLayout;
        frameLayout.setWillNotDraw(false);
        FrameLayout frameLayout2 = this.windowView;
        int i3 = this.xOffset;
        int i4 = this.yOffset;
        frameLayout2.setPadding(i3, i4, i3, i4);
        this.floatingView = new FloatingView(context);
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true);
        this.callingUserTextureView = voIPTextureView;
        voIPTextureView.scaleType = VoIPTextureView.SCALE_TYPE_NONE;
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context, false, true);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setMirror(true);
        this.floatingView.addView(voIPTextureView);
        this.floatingView.addView(voIPTextureView2);
        this.floatingView.setBackgroundColor(-7829368);
        this.windowView.addView(this.floatingView);
        this.windowView.setClipChildren(false);
        this.windowView.setClipToPadding(false);
        if (z) {
            View view = new View(context);
            this.topShadow = view;
            view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 76), 0}));
            this.floatingView.addView(this.topShadow, -1, AndroidUtilities.dp(60.0f));
            ImageView imageView = new ImageView(context);
            this.closeIcon = imageView;
            imageView.setImageResource(R.drawable.pip_close);
            this.closeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.closeIcon.setContentDescription(LocaleController.getString(R.string.Close));
            this.floatingView.addView(this.closeIcon, LayoutHelper.createFrame(40, 40.0f, 53, 4.0f, 4.0f, 4.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.enlargeIcon = imageView2;
            imageView2.setImageResource(R.drawable.pip_enlarge);
            this.enlargeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.enlargeIcon.setContentDescription(LocaleController.getString(R.string.Open));
            this.floatingView.addView(this.enlargeIcon, LayoutHelper.createFrame(40, 40.0f, 51, 4.0f, 4.0f, 4.0f, 0.0f));
            this.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    VoIPPiPView.lambda$new$1(view2);
                }
            });
            this.enlargeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    VoIPPiPView.this.lambda$new$2(context, view2);
                }
            });
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.registerStateListener(this);
        }
        updateViewState();
    }

    public static WindowManager.LayoutParams createWindowLayoutParams(Context context, int i, int i2, float f) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        float f2 = i2;
        float f3 = f2 * 0.4f;
        float f4 = i;
        float f5 = 0.4f * f4;
        layoutParams.height = (int) ((f2 * f) + ((((int) ((f3 * 1.05f) - f3)) / 2) * 2));
        layoutParams.width = (int) ((f4 * f) + ((((int) ((1.05f * f5) - f5)) / 2) * 2));
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        layoutParams.type = AndroidUtilities.checkInlinePermissions(context) ? Build.VERSION.SDK_INT >= 26 ? 2038 : 2003 : 99;
        if (Build.VERSION.SDK_INT >= 21) {
            layoutParams.flags |= Integer.MIN_VALUE;
        }
        layoutParams.flags = 16778120;
        return layoutParams;
    }

    public static void finish() {
        if (switchingToPip) {
            return;
        }
        VoIPPiPView voIPPiPView = expandedInstance;
        if (voIPPiPView != null) {
            voIPPiPView.finishInternal();
        }
        VoIPPiPView voIPPiPView2 = instance;
        if (voIPPiPView2 != null) {
            voIPPiPView2.finishInternal();
        }
        expandedInstance = null;
        instance = null;
    }

    public void finishInternal() {
        this.currentUserTextureView.renderer.release();
        this.callingUserTextureView.renderer.release();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
        this.windowView.setVisibility(8);
        if (this.windowView.getParent() != null) {
            this.floatingView.getRelativePosition(this.point);
            float min = Math.min(1.0f, Math.max(0.0f, this.point[0]));
            ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0).edit().putFloat("relativeX", min).putFloat("relativeY", Math.min(1.0f, Math.max(0.0f, this.point[1]))).apply();
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    public static VoIPPiPView getInstance() {
        VoIPPiPView voIPPiPView = expandedInstance;
        return voIPPiPView != null ? voIPPiPView : instance;
    }

    public static boolean isExpanding() {
        return instance.expanded;
    }

    public void lambda$new$0(ValueAnimator valueAnimator) {
        this.progressToCameraMini = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingView.invalidate();
    }

    public static void lambda$new$1(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.hangUp();
        } else {
            finish();
        }
    }

    public void lambda$new$2(Context context, View view) {
        boolean z = context instanceof LaunchActivity;
        if (z && !ApplicationLoader.mainInterfacePaused) {
            VoIPFragment.show((Activity) context, this.currentAccount);
        } else if (z) {
            Intent intent = new Intent(context, (Class<?>) LaunchActivity.class);
            intent.setAction("voip");
            context.startActivity(intent);
        }
    }

    public static void prepareForTransition() {
        if (expandedInstance != null) {
            instance.expandAnimator.cancel();
        }
    }

    private void setRelativePosition(float f, float f2) {
        Point point = AndroidUtilities.displaySize;
        float f3 = point.x;
        float f4 = point.y;
        float dp = AndroidUtilities.dp(16.0f);
        float dp2 = AndroidUtilities.dp(16.0f);
        float dp3 = AndroidUtilities.dp(60.0f);
        float dp4 = AndroidUtilities.dp(16.0f);
        float f5 = this.parentWidth * 0.25f;
        float f6 = this.parentHeight * 0.25f;
        if (this.floatingView.getMeasuredWidth() != 0) {
            f5 = this.floatingView.getMeasuredWidth();
        }
        if (this.floatingView.getMeasuredWidth() != 0) {
            f6 = this.floatingView.getMeasuredHeight();
        }
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.x = (int) ((f * (((f3 - dp) - dp2) - f5)) - (this.xOffset - dp));
        layoutParams.y = (int) ((f2 * (((f4 - dp3) - dp4) - f6)) - (this.yOffset - dp3));
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static void show(Activity activity, int i, int i2, int i3, int i4) {
        if (instance != null || VideoCapturerDevice.eglBase == null) {
            return;
        }
        WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(activity, i2, i3, 0.25f);
        instance = new VoIPPiPView(activity, i2, i3, false);
        WindowManager windowManager = (WindowManager) (AndroidUtilities.checkInlinePermissions(activity) ? ApplicationLoader.applicationContext.getSystemService("window") : activity.getSystemService("window"));
        VoIPPiPView voIPPiPView = instance;
        voIPPiPView.currentAccount = i;
        voIPPiPView.windowManager = windowManager;
        voIPPiPView.windowLayoutParams = createWindowLayoutParams;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0);
        instance.setRelativePosition(sharedPreferences.getFloat("relativeX", 1.0f), sharedPreferences.getFloat("relativeY", 0.0f));
        NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
        windowManager.addView(instance.windowView, createWindowLayoutParams);
        instance.currentUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
        instance.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
        if (i4 != 0) {
            if (i4 == 1) {
                instance.windowView.setAlpha(0.0f);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService sharedInstance = VoIPService.getSharedInstance();
                    VoIPPiPView voIPPiPView2 = instance;
                    sharedInstance.setBackgroundSinks(voIPPiPView2.currentUserTextureView.renderer, voIPPiPView2.callingUserTextureView.renderer);
                    return;
                }
                return;
            }
            return;
        }
        instance.windowView.setScaleX(0.5f);
        instance.windowView.setScaleY(0.5f);
        instance.windowView.setAlpha(0.0f);
        instance.windowView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).start();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService sharedInstance2 = VoIPService.getSharedInstance();
            VoIPPiPView voIPPiPView3 = instance;
            sharedInstance2.setSinks(voIPPiPView3.currentUserTextureView.renderer, voIPPiPView3.callingUserTextureView.renderer);
        }
    }

    private void updateViewState() {
        boolean z = this.floatingView.getMeasuredWidth() != 0;
        boolean z2 = this.callingUserIsVideo;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            this.callingUserIsVideo = sharedInstance.getRemoteVideoState() == 2;
            this.currentUserIsVideo = sharedInstance.getVideoState(false) == 2 || sharedInstance.getVideoState(false) == 1;
            this.currentUserTextureView.renderer.setMirror(sharedInstance.isFrontFaceCamera());
            this.currentUserTextureView.setIsScreencast(sharedInstance.isScreencast());
            this.currentUserTextureView.setScreenshareMiniProgress(1.0f, false);
        }
        if (!z) {
            this.progressToCameraMini = this.callingUserIsVideo ? 1.0f : 0.0f;
            return;
        }
        if (z2 != this.callingUserIsVideo) {
            ValueAnimator valueAnimator = this.animatorToCameraMini;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToCameraMini, this.callingUserIsVideo ? 1.0f : 0.0f);
            this.animatorToCameraMini = ofFloat;
            ofFloat.addUpdateListener(this.animatorToCameraMiniUpdater);
            this.animatorToCameraMini.setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animatorToCameraMini.start();
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didEndCall) {
            finish();
        }
    }

    @Override
    public void onAudioSettingsChanged() {
    }

    @Override
    public void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override
    public void onCameraSwitch(boolean z) {
        updateViewState();
    }

    @Override
    public void onMediaStateUpdated(int i, int i2) {
        updateViewState();
    }

    public void onPause() {
        if (this.windowLayoutParams.type == 99) {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (this.currentUserIsVideo) {
                sharedInstance.setVideoState(false, 1);
            }
        }
    }

    public void onResume() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null || sharedInstance.getVideoState(false) != 1) {
            return;
        }
        sharedInstance.setVideoState(false, 2);
    }

    @Override
    public void onScreenOnChange(boolean z) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        if (!z && this.currentUserIsVideo) {
            sharedInstance.setVideoState(false, 1);
        } else if (z && sharedInstance.getVideoState(false) == 1) {
            sharedInstance.setVideoState(false, 2);
        }
    }

    @Override
    public void onSignalBarsCountChanged(int i) {
    }

    @Override
    public void onStateChanged(int i) {
        if (i == 11 || i == 17 || i == 4 || i == 10) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VoIPPiPView.finish();
                }
            }, 200L);
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            finish();
        } else if (i != 3 || sharedInstance.isVideoAvailable()) {
            updateViewState();
        } else {
            finish();
        }
    }

    public void onTransitionEnd() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().swapSinks();
        }
    }

    @Override
    public void onVideoAvailableChange(boolean z) {
    }
}
