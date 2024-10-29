package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class PipVideoOverlay {
    private static final FloatPropertyCompat PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((PipVideoOverlay) obj).pipX;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            PipVideoOverlay.lambda$static$1((PipVideoOverlay) obj, f);
        }
    });
    private static final FloatPropertyCompat PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((PipVideoOverlay) obj).pipY;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            PipVideoOverlay.lambda$static$3((PipVideoOverlay) obj, f);
        }
    });
    private static PipVideoOverlay instance = new PipVideoOverlay();
    private Float aspectRatio;
    private float bufferProgress;
    private boolean canLongClick;
    private View consumingChild;
    private FrameLayout contentFrameLayout;
    private ViewGroup contentView;
    private ValueAnimator controlsAnimator;
    private FrameLayout controlsView;
    private GestureDetectorFixDoubleTap gestureDetector;
    private View innerView;
    private boolean isDismissing;
    private boolean isScrollDisallowed;
    private boolean isScrolling;
    private boolean isShowingControls;
    private boolean isVideoCompleted;
    private boolean isVisible;
    private boolean isWebView;
    private int mVideoHeight;
    private int mVideoWidth;
    private boolean onSideToDismiss;
    private EmbedBottomSheet parentSheet;
    private PhotoViewer photoViewer;
    private PhotoViewerWebView photoViewerWebView;
    private PipConfig pipConfig;
    private int pipHeight;
    private int pipWidth;
    private float pipX;
    private SpringAnimation pipXSpring;
    private float pipY;
    private SpringAnimation pipYSpring;
    private ImageView playPauseButton;
    private boolean postedDismissControls;
    private ScaleGestureDetector scaleGestureDetector;
    private SeekSpeedDrawable seekSpeedDrawable;
    private float videoProgress;
    private VideoProgressView videoProgressView;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private float minScaleFactor = 0.75f;
    private float maxScaleFactor = 1.4f;
    private float scaleFactor = 1.0f;
    private VideoForwardDrawable videoForwardDrawable = new VideoForwardDrawable(false);
    private Runnable progressRunnable = new Runnable() {
        @Override
        public final void run() {
            PipVideoOverlay.this.lambda$new$4();
        }
    };
    private float[] longClickStartPoint = new float[2];
    private Runnable longClickCallback = new Runnable() {
        @Override
        public final void run() {
            PipVideoOverlay.this.onLongClick();
        }
    };
    private Runnable dismissControlsCallback = new Runnable() {
        @Override
        public final void run() {
            PipVideoOverlay.this.lambda$new$5();
        }
    };

    public class AnonymousClass3 implements ScaleGestureDetector.OnScaleGestureListener {
        AnonymousClass3() {
        }

        public void lambda$onScale$0() {
            PipVideoOverlay.this.contentView.invalidate();
            PipVideoOverlay.this.contentFrameLayout.requestLayout();
        }

        public void updateLayout() {
            PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
            WindowManager.LayoutParams layoutParams = pipVideoOverlay.windowLayoutParams;
            int suggestedWidth = (int) (PipVideoOverlay.this.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor);
            layoutParams.width = suggestedWidth;
            pipVideoOverlay.pipWidth = suggestedWidth;
            PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
            WindowManager.LayoutParams layoutParams2 = pipVideoOverlay2.windowLayoutParams;
            int suggestedHeight = (int) (PipVideoOverlay.this.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor);
            layoutParams2.height = suggestedHeight;
            pipVideoOverlay2.pipHeight = suggestedHeight;
            try {
                PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
            } catch (IllegalArgumentException unused) {
            }
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
            pipVideoOverlay.scaleFactor = MathUtils.clamp(pipVideoOverlay.scaleFactor * scaleGestureDetector.getScaleFactor(), PipVideoOverlay.this.minScaleFactor, PipVideoOverlay.this.maxScaleFactor);
            PipVideoOverlay.this.pipWidth = (int) (r0.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor);
            PipVideoOverlay.this.pipHeight = (int) (r0.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PipVideoOverlay.AnonymousClass3.this.lambda$onScale$0();
                }
            });
            (!PipVideoOverlay.this.pipXSpring.isRunning() ? (SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX) : PipVideoOverlay.this.pipXSpring).getSpring().setFinalPosition(scaleGestureDetector.getFocusX() >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (r1 - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
            PipVideoOverlay.this.pipXSpring.start();
            (!PipVideoOverlay.this.pipYSpring.isRunning() ? (SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY) : PipVideoOverlay.this.pipYSpring).getSpring().setFinalPosition(MathUtils.clamp(scaleGestureDetector.getFocusY() - (PipVideoOverlay.this.pipHeight / 2.0f), AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
            PipVideoOverlay.this.pipYSpring.start();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            if (PipVideoOverlay.this.isScrolling) {
                PipVideoOverlay.this.isScrolling = false;
                PipVideoOverlay.this.canLongClick = false;
                PipVideoOverlay.this.cancelRewind();
                AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
            }
            PipVideoOverlay.this.isScrollDisallowed = true;
            PipVideoOverlay.this.windowLayoutParams.width = (int) (PipVideoOverlay.this.getSuggestedWidth() * PipVideoOverlay.this.maxScaleFactor);
            PipVideoOverlay.this.windowLayoutParams.height = (int) (PipVideoOverlay.this.getSuggestedHeight() * PipVideoOverlay.this.maxScaleFactor);
            PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            if (!PipVideoOverlay.this.pipXSpring.isRunning() && !PipVideoOverlay.this.pipYSpring.isRunning()) {
                updateLayout();
                return;
            }
            final ArrayList arrayList = new ArrayList();
            DynamicAnimation.OnAnimationEndListener onAnimationEndListener = new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    dynamicAnimation.removeEndListener(this);
                    arrayList.add((SpringAnimation) dynamicAnimation);
                    if (arrayList.size() == 2) {
                        AnonymousClass3.this.updateLayout();
                    }
                }
            };
            if (PipVideoOverlay.this.pipXSpring.isRunning()) {
                PipVideoOverlay.this.pipXSpring.addEndListener(onAnimationEndListener);
            } else {
                arrayList.add(PipVideoOverlay.this.pipXSpring);
            }
            if (PipVideoOverlay.this.pipYSpring.isRunning()) {
                PipVideoOverlay.this.pipYSpring.addEndListener(onAnimationEndListener);
            } else {
                arrayList.add(PipVideoOverlay.this.pipYSpring);
            }
        }
    }

    public class AnonymousClass4 extends GestureDetectorFixDoubleTap.OnGestureListener {
        private float startPipX;
        private float startPipY;
        final int val$touchSlop;

        AnonymousClass4(int i) {
            this.val$touchSlop = i;
        }

        public void lambda$onScroll$0(float f, DynamicAnimation dynamicAnimation, boolean z, float f2, float f3) {
            if (z) {
                return;
            }
            PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(f + (PipVideoOverlay.this.pipWidth / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (r3 - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
        }

        @Override
        public boolean hasDoubleTap(MotionEvent motionEvent) {
            if (PipVideoOverlay.this.photoViewer == null) {
                return false;
            }
            if ((PipVideoOverlay.this.photoViewer.getVideoPlayer() == null && PipVideoOverlay.this.photoViewerWebView == null) || PipVideoOverlay.this.isDismissing || PipVideoOverlay.this.isVideoCompleted || PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.scaleGestureDetector.isInProgress() || !PipVideoOverlay.this.canLongClick) {
                return false;
            }
            return PipVideoOverlay.this.getCurrentPosition() != -9223372036854775807L && PipVideoOverlay.this.getDuration() >= 15000;
        }

        @Override
        public boolean onDoubleTap(android.view.MotionEvent r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipVideoOverlay.AnonymousClass4.onDoubleTap(android.view.MotionEvent):boolean");
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            if (PipVideoOverlay.this.isShowingControls) {
                for (int i = 1; i < PipVideoOverlay.this.contentFrameLayout.getChildCount(); i++) {
                    View childAt = PipVideoOverlay.this.contentFrameLayout.getChildAt(i);
                    if (childAt.dispatchTouchEvent(motionEvent)) {
                        PipVideoOverlay.this.consumingChild = childAt;
                        return true;
                    }
                }
            }
            this.startPipX = PipVideoOverlay.this.pipX;
            this.startPipY = PipVideoOverlay.this.pipY;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (!PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.isScrollDisallowed) {
                return false;
            }
            ((SpringAnimation) ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartVelocity(f)).setStartValue(PipVideoOverlay.this.pipX)).getSpring().setFinalPosition((PipVideoOverlay.this.pipX + (PipVideoOverlay.this.pipWidth / 2.0f)) + (f / 7.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (r0 - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f));
            PipVideoOverlay.this.pipXSpring.start();
            ((SpringAnimation) ((SpringAnimation) PipVideoOverlay.this.pipYSpring.setStartVelocity(f)).setStartValue(PipVideoOverlay.this.pipY)).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY + (f2 / 10.0f), AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(16.0f)));
            PipVideoOverlay.this.pipYSpring.start();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            int i;
            if (!PipVideoOverlay.this.isScrolling && PipVideoOverlay.this.controlsAnimator == null && !PipVideoOverlay.this.isScrollDisallowed && (Math.abs(f) >= this.val$touchSlop || Math.abs(f2) >= this.val$touchSlop)) {
                PipVideoOverlay.this.isScrolling = true;
                PipVideoOverlay.this.pipXSpring.cancel();
                PipVideoOverlay.this.pipYSpring.cancel();
                PipVideoOverlay.this.canLongClick = false;
                PipVideoOverlay.this.cancelRewind();
                AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
            }
            if (PipVideoOverlay.this.isScrolling) {
                float f3 = PipVideoOverlay.this.pipX;
                final float rawX = (this.startPipX + motionEvent2.getRawX()) - motionEvent.getRawX();
                PipVideoOverlay.this.pipY = (this.startPipY + motionEvent2.getRawY()) - motionEvent.getRawY();
                if (rawX <= (-PipVideoOverlay.this.pipWidth) * 0.25f || rawX >= AndroidUtilities.displaySize.x - (PipVideoOverlay.this.pipWidth * 0.75f)) {
                    if (!PipVideoOverlay.this.onSideToDismiss) {
                        SpringForce spring = ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(f3)).getSpring();
                        float f4 = rawX + (PipVideoOverlay.this.pipWidth / 2.0f);
                        int i2 = AndroidUtilities.displaySize.x;
                        if (f4 >= i2 / 2.0f) {
                            i = AndroidUtilities.dp(16.0f);
                        } else {
                            i2 = AndroidUtilities.dp(16.0f);
                            i = PipVideoOverlay.this.pipWidth;
                        }
                        spring.setFinalPosition(i2 - i);
                        PipVideoOverlay.this.pipXSpring.start();
                    }
                    PipVideoOverlay.this.onSideToDismiss = true;
                } else if (PipVideoOverlay.this.onSideToDismiss) {
                    if (PipVideoOverlay.this.onSideToDismiss) {
                        PipVideoOverlay.this.pipXSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                            @Override
                            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f5, float f6) {
                                PipVideoOverlay.AnonymousClass4.this.lambda$onScroll$0(rawX, dynamicAnimation, z, f5, f6);
                            }
                        });
                        ((SpringAnimation) PipVideoOverlay.this.pipXSpring.setStartValue(f3)).getSpring().setFinalPosition(rawX);
                        PipVideoOverlay.this.pipXSpring.start();
                    }
                    PipVideoOverlay.this.onSideToDismiss = false;
                } else {
                    if (PipVideoOverlay.this.pipXSpring.isRunning()) {
                        PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(rawX);
                    } else {
                        PipVideoOverlay.this.windowLayoutParams.x = (int) PipVideoOverlay.this.pipX = rawX;
                        PipVideoOverlay.this.getPipConfig().setPipX(rawX);
                    }
                    PipVideoOverlay.this.windowLayoutParams.y = (int) PipVideoOverlay.this.pipY;
                    PipVideoOverlay.this.getPipConfig().setPipY(PipVideoOverlay.this.pipY);
                    PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                }
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            if (PipVideoOverlay.this.controlsAnimator != null) {
                return true;
            }
            if (PipVideoOverlay.this.postedDismissControls) {
                AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.dismissControlsCallback);
                PipVideoOverlay.this.postedDismissControls = false;
            }
            PipVideoOverlay.this.isShowingControls = !r4.isShowingControls;
            PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
            pipVideoOverlay.toggleControls(pipVideoOverlay.isShowingControls);
            if (PipVideoOverlay.this.isShowingControls && !PipVideoOverlay.this.postedDismissControls) {
                AndroidUtilities.runOnUIThread(PipVideoOverlay.this.dismissControlsCallback, 2500L);
                PipVideoOverlay.this.postedDismissControls = true;
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return !hasDoubleTap(motionEvent) ? onSingleTapConfirmed(motionEvent) : super.onSingleTapUp(motionEvent);
        }
    }

    public static final class PipConfig {
        private SharedPreferences mPrefs;

        private PipConfig(int i, int i2) {
            this.mPrefs = ApplicationLoader.applicationContext.getSharedPreferences("pip_layout_" + i + "_" + i2, 0);
        }

        public float getPipX() {
            return this.mPrefs.getFloat("x", -1.0f);
        }

        public float getPipY() {
            return this.mPrefs.getFloat("y", -1.0f);
        }

        public float getScaleFactor() {
            return this.mPrefs.getFloat("scale_factor", 1.0f);
        }

        public void setPipX(float f) {
            this.mPrefs.edit().putFloat("x", f).apply();
        }

        public void setPipY(float f) {
            this.mPrefs.edit().putFloat("y", f).apply();
        }
    }

    public static class PipVideoViewGroup extends ViewGroup {
        public PipVideoViewGroup(Context context) {
            super(context);
        }
    }

    public final class VideoProgressView extends View {
        private Paint bufferPaint;
        private Paint progressPaint;

        public VideoProgressView(Context context) {
            super(context);
            this.progressPaint = new Paint();
            this.bufferPaint = new Paint();
            this.progressPaint.setColor(-1);
            Paint paint = this.progressPaint;
            Paint.Style style = Paint.Style.STROKE;
            paint.setStyle(style);
            Paint paint2 = this.progressPaint;
            Paint.Cap cap = Paint.Cap.ROUND;
            paint2.setStrokeCap(cap);
            this.progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.bufferPaint.setColor(this.progressPaint.getColor());
            this.bufferPaint.setAlpha((int) (this.progressPaint.getAlpha() * 0.3f));
            this.bufferPaint.setStyle(style);
            this.bufferPaint.setStrokeCap(cap);
            this.bufferPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!PipVideoOverlay.this.isWebView || (PipVideoOverlay.this.photoViewerWebView != null && PipVideoOverlay.this.photoViewerWebView.isControllable())) {
                int width = getWidth();
                int dp = AndroidUtilities.dp(10.0f);
                float f = (width - dp) - dp;
                int i = ((int) (PipVideoOverlay.this.videoProgress * f)) + dp;
                float height = getHeight() - AndroidUtilities.dp(8.0f);
                if (PipVideoOverlay.this.bufferProgress != 0.0f) {
                    float f2 = dp;
                    canvas.drawLine(f2, height, f2 + (f * PipVideoOverlay.this.bufferProgress), height, this.bufferPaint);
                }
                canvas.drawLine(dp, height, i, height, this.progressPaint);
            }
        }
    }

    public void cancelRewind() {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null || photoViewer.getVideoPlayerRewinder() == null) {
            return;
        }
        this.photoViewer.getVideoPlayerRewinder().cancelRewind();
    }

    private WindowManager.LayoutParams createWindowLayoutParams(boolean z) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        layoutParams.type = (z || !AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) ? 99 : Build.VERSION.SDK_INT >= 26 ? 2038 : 2003;
        layoutParams.flags = 520;
        return layoutParams;
    }

    public static void dismiss() {
        dismiss(false);
    }

    public static void dismiss(boolean z) {
        instance.dismissInternal(z, false);
    }

    public static void dismiss(boolean z, boolean z2) {
        instance.dismissInternal(z, z2);
    }

    public static void dismissAndDestroy() {
        PipVideoOverlay pipVideoOverlay = instance;
        EmbedBottomSheet embedBottomSheet = pipVideoOverlay.parentSheet;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        } else {
            PhotoViewer photoViewer = pipVideoOverlay.photoViewer;
            if (photoViewer != null) {
                photoViewer.destroyPhotoViewer();
                MediaController.getInstance().tryResumePausedAudio();
            }
        }
        dismiss();
    }

    private void dismissInternal(boolean z, boolean z2) {
        if (this.isDismissing) {
            return;
        }
        this.isDismissing = true;
        ValueAnimator valueAnimator = this.controlsAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.postedDismissControls) {
            AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
            this.postedDismissControls = false;
        }
        SpringAnimation springAnimation = this.pipXSpring;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.pipYSpring.cancel();
        }
        if (z || this.contentView == null) {
            if (z2) {
                onDismissedInternal();
                return;
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        PipVideoOverlay.this.onDismissedInternal();
                    }
                }, 100L);
                return;
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250L);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.contentView, (Property<ViewGroup, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.contentView, (Property<ViewGroup, Float>) View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.contentView, (Property<ViewGroup, Float>) View.SCALE_Y, 0.1f));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PipVideoOverlay.this.onDismissedInternal();
            }
        });
        animatorSet.start();
    }

    public long getCurrentPosition() {
        if (this.photoViewerWebView != null) {
            return r0.getCurrentPosition();
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        if (this.photoViewerWebView != null) {
            return r0.getVideoDuration();
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getDuration();
    }

    public static View getInnerView() {
        return instance.innerView;
    }

    public PipConfig getPipConfig() {
        if (this.pipConfig == null) {
            android.graphics.Point point = AndroidUtilities.displaySize;
            this.pipConfig = new PipConfig(point.x, point.y);
        }
        return this.pipConfig;
    }

    public static Rect getPipRect(boolean z, float f) {
        Rect rect = new Rect();
        float f2 = 1.0f / f;
        PipVideoOverlay pipVideoOverlay = instance;
        if (pipVideoOverlay.isVisible && !z) {
            rect.x = pipVideoOverlay.pipX;
            rect.y = pipVideoOverlay.pipY + AndroidUtilities.statusBarHeight;
            PipVideoOverlay pipVideoOverlay2 = instance;
            rect.width = pipVideoOverlay2.pipWidth;
            rect.height = pipVideoOverlay2.pipHeight;
            return rect;
        }
        float pipX = pipVideoOverlay.getPipConfig().getPipX();
        float pipY = instance.getPipConfig().getPipY();
        float scaleFactor = instance.getPipConfig().getScaleFactor();
        rect.width = getSuggestedWidth(f2) * scaleFactor;
        rect.height = getSuggestedHeight(f2) * scaleFactor;
        if (pipX != -1.0f) {
            float f3 = rect.width;
            float f4 = pipX + (f3 / 2.0f);
            float f5 = AndroidUtilities.displaySize.x;
            rect.x = f4 >= f5 / 2.0f ? (f5 - f3) - AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(16.0f);
        } else {
            rect.x = (AndroidUtilities.displaySize.x - rect.width) - AndroidUtilities.dp(16.0f);
        }
        rect.y = pipY != -1.0f ? MathUtils.clamp(pipY, AndroidUtilities.dp(16.0f), (AndroidUtilities.displaySize.y - AndroidUtilities.dp(16.0f)) - rect.height) + AndroidUtilities.statusBarHeight : AndroidUtilities.dp(16.0f) + AndroidUtilities.statusBarHeight;
        return rect;
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            this.aspectRatio = Float.valueOf(this.mVideoHeight / this.mVideoWidth);
            android.graphics.Point point = AndroidUtilities.displaySize;
            this.maxScaleFactor = (Math.min(point.x, point.y) - AndroidUtilities.dp(32.0f)) / getSuggestedWidth();
            this.videoForwardDrawable.setPlayScaleFactor(this.aspectRatio.floatValue() < 1.0f ? 0.6f : 0.45f);
        }
        return this.aspectRatio.floatValue();
    }

    public int getSuggestedHeight() {
        return getSuggestedHeight(getRatio());
    }

    private static int getSuggestedHeight(float f) {
        return (int) (getSuggestedWidth(f) * f);
    }

    public int getSuggestedWidth() {
        return getSuggestedWidth(getRatio());
    }

    private static int getSuggestedWidth(float f) {
        float min;
        float f2;
        if (f >= 1.0f) {
            android.graphics.Point point = AndroidUtilities.displaySize;
            min = Math.min(point.x, point.y);
            f2 = 0.35f;
        } else {
            android.graphics.Point point2 = AndroidUtilities.displaySize;
            min = Math.min(point2.x, point2.y);
            f2 = 0.6f;
        }
        return (int) (min * f2);
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    public void lambda$new$4() {
        float bufferedPosition;
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null) {
            return;
        }
        if (this.photoViewerWebView != null) {
            this.videoProgress = r1.getCurrentPosition() / this.photoViewerWebView.getVideoDuration();
            bufferedPosition = this.photoViewerWebView.getBufferedPosition();
        } else {
            VideoPlayer videoPlayer = photoViewer.getVideoPlayer();
            if (videoPlayer == null) {
                return;
            }
            float duration = (float) getDuration();
            this.videoProgress = ((float) videoPlayer.getCurrentPosition()) / duration;
            bufferedPosition = ((float) videoPlayer.getBufferedPosition()) / duration;
        }
        this.bufferProgress = bufferedPosition;
        this.videoProgressView.invalidate();
        AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
    }

    public void lambda$new$5() {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer != null && photoViewer.getVideoPlayerRewinder().rewinding) {
            AndroidUtilities.runOnUIThread(this.dismissControlsCallback, 1500L);
            return;
        }
        this.isShowingControls = false;
        toggleControls(false);
        this.postedDismissControls = false;
    }

    public void lambda$showInternal$10(boolean z, View view) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        boolean z2 = true;
        if (Build.VERSION.SDK_INT >= 21 && (runningAppProcesses = ((ActivityManager) view.getContext().getSystemService("activity")).getRunningAppProcesses()) != null && !runningAppProcesses.isEmpty() && runningAppProcesses.get(0).importance != 100) {
            z2 = false;
        }
        if (!z && (!z2 || !LaunchActivity.isResumed)) {
            Objects.requireNonNull(view);
            LaunchActivity.onResumeStaticCallback = new EmbedBottomSheet$$ExternalSyntheticLambda6(view);
            Context context = ApplicationLoader.applicationContext;
            Intent intent = new Intent(context, (Class<?>) LaunchActivity.class);
            intent.addFlags(268435456);
            context.startActivity(intent);
            return;
        }
        EmbedBottomSheet embedBottomSheet = this.parentSheet;
        if (embedBottomSheet != null) {
            embedBottomSheet.exitFromPip();
            return;
        }
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer != null) {
            photoViewer.exitFromPip();
        }
    }

    public void lambda$showInternal$11(View view) {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null) {
            return;
        }
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView == null) {
            VideoPlayer videoPlayer = photoViewer.getVideoPlayer();
            if (videoPlayer == null) {
                return;
            }
            if (videoPlayer.isPlaying()) {
                videoPlayer.pause();
            } else {
                videoPlayer.play();
            }
        } else if (photoViewerWebView.isPlaying()) {
            this.photoViewerWebView.pauseVideo();
        } else {
            this.photoViewerWebView.playVideo();
        }
        updatePlayButton();
    }

    public void lambda$showInternal$7(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        getPipConfig().setPipX(f);
    }

    public void lambda$showInternal$8(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        getPipConfig().setPipY(f);
    }

    public static void lambda$static$1(PipVideoOverlay pipVideoOverlay, float f) {
        WindowManager.LayoutParams layoutParams = pipVideoOverlay.windowLayoutParams;
        pipVideoOverlay.pipX = f;
        layoutParams.x = (int) f;
        try {
            pipVideoOverlay.windowManager.updateViewLayout(pipVideoOverlay.contentView, layoutParams);
        } catch (IllegalArgumentException unused) {
            pipVideoOverlay.pipXSpring.cancel();
        }
    }

    public static void lambda$static$3(PipVideoOverlay pipVideoOverlay, float f) {
        WindowManager.LayoutParams layoutParams = pipVideoOverlay.windowLayoutParams;
        pipVideoOverlay.pipY = f;
        layoutParams.y = (int) f;
        try {
            pipVideoOverlay.windowManager.updateViewLayout(pipVideoOverlay.contentView, layoutParams);
        } catch (IllegalArgumentException unused) {
            pipVideoOverlay.pipYSpring.cancel();
        }
    }

    public void lambda$toggleControls$6(ValueAnimator valueAnimator) {
        this.controlsView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void onDismissedInternal() {
        try {
            ViewGroup viewGroup = this.contentView;
            if (viewGroup != null && viewGroup.getParent() != null) {
                this.windowManager.removeViewImmediate(this.contentView);
            }
        } catch (Exception unused) {
        }
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null) {
            photoViewerWebView.showControls();
        }
        this.videoProgressView = null;
        this.innerView = null;
        this.photoViewer = null;
        this.photoViewerWebView = null;
        this.parentSheet = null;
        this.consumingChild = null;
        this.isScrolling = false;
        this.isVisible = false;
        this.isDismissing = false;
        this.canLongClick = false;
        cancelRewind();
        AndroidUtilities.cancelRunOnUIThread(this.longClickCallback);
    }

    public static void onRewindCanceled() {
        instance.onRewindCanceledInternal();
    }

    private void onRewindCanceledInternal() {
        this.videoForwardDrawable.setShowing(false);
    }

    public static void onRewindStart(boolean z) {
        instance.onRewindStartInternal(z);
    }

    private void onRewindStartInternal(boolean z) {
        this.videoForwardDrawable.setOneShootAnimation(false);
        this.videoForwardDrawable.setLeftSide(!z);
        this.videoForwardDrawable.setShowing(true);
        VideoProgressView videoProgressView = this.videoProgressView;
        if (videoProgressView != null) {
            videoProgressView.invalidate();
        }
        FrameLayout frameLayout = this.controlsView;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public static void onUpdateRewindProgressUi(long j, float f, boolean z) {
        instance.onUpdateRewindProgressUiInternal(j, f, z);
    }

    public void onUpdateRewindProgressUiInternal(long j, float f, boolean z) {
        this.videoForwardDrawable.setTime(0L);
        if (z) {
            this.videoProgress = f;
            VideoProgressView videoProgressView = this.videoProgressView;
            if (videoProgressView != null) {
                videoProgressView.invalidate();
            }
            FrameLayout frameLayout = this.controlsView;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
        }
    }

    public static void onVideoCompleted() {
        instance.onVideoCompletedInternal();
    }

    private void onVideoCompletedInternal() {
        VideoProgressView videoProgressView;
        if (!this.isVisible || (videoProgressView = this.videoProgressView) == null) {
            return;
        }
        this.isVideoCompleted = true;
        this.videoProgress = 0.0f;
        this.bufferProgress = 0.0f;
        videoProgressView.invalidate();
        updatePlayButtonInternal();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (this.isShowingControls) {
            return;
        }
        toggleControls(true);
        AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
    }

    public void seekTo(long j) {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null) {
            photoViewerWebView.seekTo(j);
            return;
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.seekTo(j);
    }

    public static void setBufferedProgress(float f) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.bufferProgress = f;
        VideoProgressView videoProgressView = pipVideoOverlay.videoProgressView;
        if (videoProgressView != null) {
            videoProgressView.invalidate();
        }
    }

    public static void setParentSheet(EmbedBottomSheet embedBottomSheet) {
        instance.parentSheet = embedBottomSheet;
    }

    public static void setPhotoViewer(PhotoViewer photoViewer) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.photoViewer = photoViewer;
        pipVideoOverlay.updatePlayButtonInternal();
    }

    public static boolean show(boolean z, Activity activity, View view, int i, int i2) {
        return show(z, activity, view, i, i2, false);
    }

    public static boolean show(boolean z, Activity activity, View view, int i, int i2, boolean z2) {
        return show(z, activity, null, view, i, i2, z2);
    }

    public static boolean show(boolean z, Activity activity, PhotoViewerWebView photoViewerWebView, View view, int i, int i2, boolean z2) {
        return instance.showInternal(z, activity, view, photoViewerWebView, i, i2, z2);
    }

    private boolean showInternal(final boolean r24, android.app.Activity r25, android.view.View r26, org.telegram.ui.Components.PhotoViewerWebView r27, int r28, int r29, boolean r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PipVideoOverlay.showInternal(boolean, android.app.Activity, android.view.View, org.telegram.ui.Components.PhotoViewerWebView, int, int, boolean):boolean");
    }

    public void toggleControls(boolean z) {
        ValueAnimator duration = ValueAnimator.ofFloat(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f).setDuration(200L);
        this.controlsAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.controlsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PipVideoOverlay.this.lambda$toggleControls$6(valueAnimator);
            }
        });
        this.controlsAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PipVideoOverlay.this.controlsAnimator = null;
            }
        });
        this.controlsAnimator.start();
    }

    public static void updatePlayButton() {
        instance.updatePlayButtonInternal();
    }

    private void updatePlayButtonInternal() {
        boolean isPlaying;
        ImageView imageView;
        int i;
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null || this.playPauseButton == null) {
            return;
        }
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null) {
            isPlaying = photoViewerWebView.isPlaying();
        } else {
            VideoPlayer videoPlayer = photoViewer.getVideoPlayer();
            if (videoPlayer == null) {
                return;
            } else {
                isPlaying = videoPlayer.isPlaying();
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (isPlaying) {
            this.playPauseButton.setImageResource(R.drawable.pip_pause_large);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
            return;
        }
        if (this.isVideoCompleted) {
            imageView = this.playPauseButton;
            i = R.drawable.pip_replay_large;
        } else {
            imageView = this.playPauseButton;
            i = R.drawable.pip_play_large;
        }
        imageView.setImageResource(i);
    }

    public void onLongClick() {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer != null) {
            if ((photoViewer.getVideoPlayer() == null && this.photoViewerWebView == null) || this.isDismissing || this.isVideoCompleted || this.isScrolling || this.scaleGestureDetector.isInProgress() || !this.canLongClick) {
                return;
            }
            VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
            boolean z = this.longClickStartPoint[0] >= (((float) getSuggestedWidth()) * this.scaleFactor) * 0.5f;
            long currentPosition = getCurrentPosition();
            long duration = getDuration();
            if (currentPosition == -9223372036854775807L || duration < 15000) {
                return;
            }
            if (this.photoViewerWebView != null) {
                this.photoViewer.getVideoPlayerRewinder().startRewind(this.photoViewerWebView, z, this.longClickStartPoint[0], this.photoViewer.getCurrentVideoSpeed(), this.seekSpeedDrawable);
            } else {
                this.photoViewer.getVideoPlayerRewinder().startRewind(videoPlayer, z, this.longClickStartPoint[0], this.photoViewer.getCurrentVideoSpeed(), this.seekSpeedDrawable);
            }
            if (this.isShowingControls) {
                return;
            }
            this.isShowingControls = true;
            toggleControls(true);
            if (this.postedDismissControls) {
                return;
            }
            AndroidUtilities.runOnUIThread(this.dismissControlsCallback, 1500L);
            this.postedDismissControls = true;
        }
    }
}
