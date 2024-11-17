package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Crop.CropRotationWheel;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.Crop.CropView;

public class PhotoCropView extends FrameLayout {
    public final Property ANIMATION_VALUE;
    public final Property PROGRESS_VALUE;
    private Paint circlePaint;
    public CropView cropView;
    private PhotoCropViewDelegate delegate;
    private float flashAlpha;
    private boolean inBubbleMode;
    public boolean isReset;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatorSet thumbAnimation;
    private float thumbAnimationProgress;
    private ImageReceiver thumbImageView;
    private boolean thumbImageVisible;
    private boolean thumbImageVisibleOverride;
    private float thumbImageVisibleProgress;
    private AnimatorSet thumbOverrideAnimation;
    public CropRotationWheel wheelView;

    public interface PhotoCropViewDelegate {
        int getVideoThumbX();

        boolean mirror();

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();

        void onVideoThumbClick();

        boolean rotate();
    }

    public PhotoCropView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.isReset = true;
        this.thumbImageVisibleOverride = true;
        this.thumbAnimationProgress = 1.0f;
        this.flashAlpha = 0.0f;
        this.circlePaint = new Paint(1);
        this.ANIMATION_VALUE = new AnimationProperties.FloatProperty("thumbAnimationProgress") {
            @Override
            public Float get(PhotoCropView photoCropView) {
                return Float.valueOf(PhotoCropView.this.thumbAnimationProgress);
            }

            @Override
            public void setValue(PhotoCropView photoCropView, float f) {
                PhotoCropView.this.thumbAnimationProgress = f;
                photoCropView.invalidate();
            }
        };
        this.PROGRESS_VALUE = new AnimationProperties.FloatProperty("thumbImageVisibleProgress") {
            @Override
            public Float get(PhotoCropView photoCropView) {
                return Float.valueOf(PhotoCropView.this.thumbImageVisibleProgress);
            }

            @Override
            public void setValue(PhotoCropView photoCropView, float f) {
                PhotoCropView.this.thumbImageVisibleProgress = f;
                photoCropView.invalidate();
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.inBubbleMode = context instanceof BubbleActivity;
        CropView cropView = new CropView(context);
        this.cropView = cropView;
        cropView.setListener(new CropView.CropViewListener() {
            @Override
            public void onAspectLock(boolean z) {
                PhotoCropView.this.wheelView.setAspectLock(z);
            }

            @Override
            public void onChange(boolean z) {
                PhotoCropView photoCropView = PhotoCropView.this;
                photoCropView.isReset = z;
                if (photoCropView.delegate != null) {
                    PhotoCropView.this.delegate.onChange(z);
                }
            }

            @Override
            public void onTapUp() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onTapUp();
                }
            }

            @Override
            public void onUpdate() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onUpdate();
                }
            }
        });
        this.cropView.setBottomPadding(AndroidUtilities.dp(64.0f));
        addView(this.cropView);
        this.thumbImageView = new ImageReceiver(this);
        CropRotationWheel cropRotationWheel = new CropRotationWheel(context);
        this.wheelView = cropRotationWheel;
        cropRotationWheel.setListener(new CropRotationWheel.RotationWheelListener() {
            @Override
            public void aspectRatioPressed() {
                PhotoCropView.this.cropView.showAspectRatioDialog();
            }

            @Override
            public boolean mirror() {
                if (PhotoCropView.this.delegate != null) {
                    return PhotoCropView.this.delegate.mirror();
                }
                return false;
            }

            @Override
            public void onChange(float f) {
                PhotoCropView.this.cropView.setRotation(f);
                PhotoCropView photoCropView = PhotoCropView.this;
                photoCropView.isReset = false;
                if (photoCropView.delegate != null) {
                    PhotoCropView.this.delegate.onChange(false);
                }
            }

            @Override
            public void onEnd(float f) {
                PhotoCropView.this.cropView.onRotationEnded();
            }

            @Override
            public void onStart() {
                PhotoCropView.this.cropView.onRotationBegan();
            }

            @Override
            public boolean rotate90Pressed() {
                if (PhotoCropView.this.delegate != null) {
                    return PhotoCropView.this.delegate.rotate();
                }
                return false;
            }
        });
        addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void cancelThumbAnimation() {
        AnimatorSet animatorSet = this.thumbAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.thumbAnimation = null;
            this.thumbImageVisible = false;
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        CropView cropView;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.thumbImageVisible && view == (cropView = this.cropView)) {
            RectF actualRect = cropView.getActualRect();
            int dp = AndroidUtilities.dp(32.0f);
            int videoThumbX = (this.delegate.getVideoThumbX() - (dp / 2)) + AndroidUtilities.dp(2.0f);
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(156.0f);
            float f = actualRect.left;
            float f2 = this.thumbAnimationProgress;
            float f3 = f + ((videoThumbX - f) * f2);
            float f4 = actualRect.top;
            float f5 = f4 + ((measuredHeight - f4) * f2);
            float width = actualRect.width() + ((dp - actualRect.width()) * this.thumbAnimationProgress);
            this.thumbImageView.setRoundRadius((int) (width / 2.0f));
            this.thumbImageView.setImageCoords(f3, f5, width, width);
            this.thumbImageView.setAlpha(this.thumbImageVisibleProgress);
            this.thumbImageView.draw(canvas);
            if (this.flashAlpha > 0.0f) {
                this.circlePaint.setColor(-1);
                this.circlePaint.setAlpha((int) (this.flashAlpha * 255.0f));
                canvas.drawCircle(actualRect.centerX(), actualRect.centerY(), actualRect.width() / 2.0f, this.circlePaint);
            }
            this.circlePaint.setColor(getThemedColor(Theme.key_chat_editMediaButton));
            this.circlePaint.setAlpha(Math.min(255, (int) (this.thumbAnimationProgress * 255.0f * this.thumbImageVisibleProgress)));
            canvas.drawCircle(videoThumbX + r1, measuredHeight + dp + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(3.0f), this.circlePaint);
        }
        return drawChild;
    }

    public float getRectSizeX() {
        return this.cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return this.cropView.getCropHeight();
    }

    public float getRectX() {
        return this.cropView.getCropLeft() - AndroidUtilities.dp(14.0f);
    }

    public float getRectY() {
        return (this.cropView.getCropTop() - AndroidUtilities.dp(14.0f)) - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
    }

    public Bitmap getVideoThumb() {
        if (this.thumbImageVisible && this.thumbImageVisibleOverride) {
            return this.thumbImageView.getBitmap();
        }
        return null;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.cropView.invalidate();
    }

    public boolean isReady() {
        return this.cropView.isReady();
    }

    public void makeCrop(MediaController.MediaEditState mediaEditState) {
        this.cropView.makeCrop(mediaEditState);
    }

    public boolean mirror() {
        return this.cropView.mirror();
    }

    public void onAppear() {
        this.cropView.willShow();
    }

    public void onAppeared() {
        this.cropView.show();
    }

    public void onDisappear() {
        this.cropView.hide();
    }

    public void onHide() {
        this.cropView.onHide();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.thumbImageVisibleOverride || !this.thumbImageVisible || !this.thumbImageView.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 1) {
            this.delegate.onVideoThumbClick();
        }
        return true;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.cropView.updateLayout();
    }

    public void onShow() {
        this.cropView.onShow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.thumbImageVisibleOverride || !this.thumbImageVisible || !this.thumbImageView.isInsideImage(motionEvent.getX(), motionEvent.getY())) {
            return super.onTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 1) {
            this.delegate.onVideoThumbClick();
        }
        return true;
    }

    public void reset(boolean z) {
        this.wheelView.reset(true);
        this.cropView.reset(z);
    }

    public boolean rotate(float f) {
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (cropRotationWheel != null) {
            cropRotationWheel.reset(false);
        }
        return this.cropView.rotate(f);
    }

    public void setAspectRatio(float f) {
        this.cropView.setAspectRatio(f);
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z, boolean z2, PaintingOverlay paintingOverlay, CropTransform cropTransform, VideoEditTextureView videoEditTextureView, MediaController.CropState cropState) {
        requestLayout();
        this.thumbImageVisible = false;
        this.thumbImageView.setImageBitmap((Drawable) null);
        this.cropView.setBitmap(bitmap, i, z, z2, paintingOverlay, cropTransform, videoEditTextureView, cropState);
        this.wheelView.setFreeform(z);
        this.wheelView.reset(true);
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (cropState != null) {
            cropRotationWheel.setRotation(cropState.cropRotate, false);
            this.wheelView.setRotated(cropState.transformRotation != 0);
            this.wheelView.setMirrored(cropState.mirrored);
        } else {
            cropRotationWheel.setRotated(false);
            this.wheelView.setMirrored(false);
        }
        this.wheelView.setVisibility(z ? 0 : 4);
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    public void setFreeform(boolean z) {
        this.cropView.setFreeform(z);
    }

    public void setSubtitle(String str) {
        this.cropView.setSubtitle(str);
    }

    public void setVideoThumb(Bitmap bitmap, int i) {
        this.thumbImageVisible = bitmap != null;
        this.thumbImageView.setImageBitmap(bitmap);
        this.thumbImageView.setOrientation(i, false);
        AnimatorSet animatorSet = this.thumbAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.thumbOverrideAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.thumbImageVisibleOverride = true;
        this.thumbImageVisibleProgress = 1.0f;
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.thumbAnimation = animatorSet3;
        animatorSet3.playTogether(ObjectAnimator.ofFloat(this, (Property<PhotoCropView, Float>) this.ANIMATION_VALUE, 0.0f, 1.0f));
        this.thumbAnimation.setDuration(250L);
        this.thumbAnimation.setInterpolator(new OvershootInterpolator(1.01f));
        this.thumbAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PhotoCropView.this.thumbAnimation = null;
            }
        });
        this.thumbAnimation.start();
    }

    public void setVideoThumbFlashAlpha(float f) {
        this.flashAlpha = f;
        invalidate();
    }

    public void setVideoThumbVisible(boolean z) {
        if (this.thumbImageVisibleOverride == z) {
            return;
        }
        this.thumbImageVisibleOverride = z;
        AnimatorSet animatorSet = this.thumbOverrideAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.thumbOverrideAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<PhotoCropView, Float>) this.PROGRESS_VALUE, z ? 1.0f : 0.0f));
        this.thumbOverrideAnimation.setDuration(180L);
        this.thumbOverrideAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PhotoCropView.this.thumbOverrideAnimation = null;
            }
        });
        this.thumbOverrideAnimation.start();
    }
}
