package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.webrtc.TextureViewRenderer;

public class VoIPTextureView extends FrameLayout {
    public static int SCALE_TYPE_ADAPTIVE = 2;
    public static int SCALE_TYPE_FILL = 0;
    public static int SCALE_TYPE_FIT = 1;
    public static int SCALE_TYPE_NONE = 3;
    int animateFromHeight;
    float animateFromRendererH;
    float animateFromRendererW;
    float animateFromThumbScale;
    int animateFromWidth;
    float animateFromX;
    float animateFromY;
    long animateNextDuration;
    boolean animateOnNextLayout;
    ArrayList animateOnNextLayoutAnimations;
    boolean animateWithParent;
    public float animationProgress;
    float aninateFromScale;
    float aninateFromScaleBlur;
    final boolean applyRotation;
    public View backgroundView;
    public TextureView blurRenderer;
    public Bitmap cameraLastBitmap;
    float clipHorizontal;
    boolean clipToTexture;
    float clipVertical;
    ValueAnimator currentAnimation;
    float currentClipHorizontal;
    float currentClipVertical;
    float currentThumbScale;
    boolean ignoreLayout;
    public final ImageView imageView;
    final boolean isCamera;
    public final TextureViewRenderer renderer;
    float roundRadius;
    public float scaleTextureToFill;
    private float scaleTextureToFillBlur;
    private float scaleThumb;
    public int scaleType;
    private boolean screencast;
    private ImageView screencastImage;
    private TextView screencastText;
    private FrameLayout screencastView;
    public float stubVisibleProgress;
    private Bitmap thumb;

    public VoIPTextureView(Context context, boolean z, boolean z2) {
        this(context, z, z2, true, false);
    }

    public VoIPTextureView(android.content.Context r21, boolean r22, boolean r23, boolean r24, boolean r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPTextureView.<init>(android.content.Context, boolean, boolean, boolean, boolean):void");
    }

    public void lambda$onLayout$0(float f, float f2, float f3, float f4, float f5, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f6 = 1.0f - floatValue;
        this.animationProgress = f6;
        this.currentClipVertical = this.clipVertical * floatValue;
        this.currentClipHorizontal = this.clipHorizontal * floatValue;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
        invalidate();
        float f7 = (f * floatValue) + (this.scaleTextureToFill * f6);
        this.renderer.setScaleX(f7);
        this.renderer.setScaleY(f7);
        float f8 = (f2 * floatValue) + (this.scaleTextureToFillBlur * f6);
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            textureView.setScaleX(f8);
            this.blurRenderer.setScaleY(f8);
        }
        setTranslationX(f3 * floatValue);
        setTranslationY(f4 * floatValue);
        this.currentThumbScale = (f5 * floatValue) + (this.scaleThumb * f6);
    }

    public void animateToLayout() {
        float x;
        if (this.animateOnNextLayout || getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            return;
        }
        this.animateFromHeight = getMeasuredHeight();
        this.animateFromWidth = getMeasuredWidth();
        if (!this.animateWithParent || getParent() == null) {
            this.animateFromY = getY();
            x = getX();
        } else {
            View view = (View) getParent();
            this.animateFromY = view.getY();
            x = view.getX();
        }
        this.animateFromX = x;
        this.aninateFromScale = this.scaleTextureToFill;
        this.aninateFromScaleBlur = this.scaleTextureToFillBlur;
        this.animateFromThumbScale = this.scaleThumb;
        this.animateFromRendererW = this.renderer.getMeasuredWidth();
        this.animateFromRendererH = this.renderer.getMeasuredHeight();
        this.animateOnNextLayout = true;
        requestLayout();
    }

    public void attachBackgroundRenderer() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            this.renderer.setBackgroundRenderer(textureView);
            if (this.renderer.isFirstFrameRendered()) {
                return;
            }
            this.blurRenderer.setAlpha(0.0f);
        }
    }

    public void cancelAnimation() {
        this.animateOnNextLayout = false;
        this.animateNextDuration = 0L;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.imageView.getVisibility() == 0 && this.renderer.isFirstFrameRendered()) {
            float f = this.stubVisibleProgress - 0.10666667f;
            this.stubVisibleProgress = f;
            if (f <= 0.0f) {
                this.stubVisibleProgress = 0.0f;
                this.imageView.setVisibility(8);
            } else {
                invalidate();
                this.imageView.setAlpha(this.stubVisibleProgress);
            }
        }
    }

    public boolean isInAnimation() {
        return this.currentAnimation != null;
    }

    protected void onFirstFrameRendered() {
        invalidate();
        if (this.renderer.getAlpha() != 1.0f) {
            this.renderer.animate().setDuration(300L).alpha(1.0f);
        }
        TextureView textureView = this.blurRenderer;
        if (textureView == null || textureView.getAlpha() == 1.0f) {
            return;
        }
        this.blurRenderer.animate().setDuration(300L).alpha(1.0f);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float top;
        float left;
        float measuredHeight;
        float measuredWidth;
        float f;
        super.onLayout(z, i, i2, i3, i4);
        if (this.blurRenderer != null) {
            this.scaleTextureToFillBlur = Math.max(getMeasuredHeight() / this.blurRenderer.getMeasuredHeight(), getMeasuredWidth() / this.blurRenderer.getMeasuredWidth());
        }
        if (!this.applyRotation) {
            this.renderer.updateRotation();
        }
        if (this.scaleType == SCALE_TYPE_NONE) {
            TextureView textureView = this.blurRenderer;
            if (textureView != null) {
                textureView.setScaleX(this.scaleTextureToFillBlur);
                this.blurRenderer.setScaleY(this.scaleTextureToFillBlur);
                return;
            }
            return;
        }
        if (this.renderer.getMeasuredHeight() == 0 || this.renderer.getMeasuredWidth() == 0 || getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            this.scaleTextureToFill = 1.0f;
            if (this.currentAnimation == null && !this.animateOnNextLayout) {
                this.currentClipHorizontal = 0.0f;
                this.currentClipVertical = 0.0f;
            }
        } else {
            int i5 = this.scaleType;
            if (i5 != SCALE_TYPE_FILL) {
                if (i5 == SCALE_TYPE_ADAPTIVE) {
                    if (Math.abs((getMeasuredHeight() / getMeasuredWidth()) - 1.0f) >= 0.02f) {
                        if (getMeasuredWidth() <= getMeasuredHeight() || this.renderer.getMeasuredHeight() <= this.renderer.getMeasuredWidth()) {
                            f = Math.min(getMeasuredHeight() / this.renderer.getMeasuredHeight(), getMeasuredWidth() / this.renderer.getMeasuredWidth());
                            this.scaleTextureToFill = f;
                        } else {
                            measuredHeight = getMeasuredHeight() / this.renderer.getMeasuredHeight();
                            measuredWidth = getMeasuredWidth() / 2.0f;
                            f = Math.max(measuredHeight, measuredWidth / this.renderer.getMeasuredWidth());
                            this.scaleTextureToFill = f;
                        }
                    }
                } else if (i5 == SCALE_TYPE_FIT) {
                    this.scaleTextureToFill = Math.min(getMeasuredHeight() / this.renderer.getMeasuredHeight(), getMeasuredWidth() / this.renderer.getMeasuredWidth());
                    if (this.clipToTexture && !this.animateWithParent && this.currentAnimation == null && !this.animateOnNextLayout) {
                        this.currentClipHorizontal = (getMeasuredWidth() - this.renderer.getMeasuredWidth()) / 2.0f;
                        this.currentClipVertical = (getMeasuredHeight() - this.renderer.getMeasuredHeight()) / 2.0f;
                        if (Build.VERSION.SDK_INT >= 21) {
                            invalidateOutline();
                        }
                    }
                }
            }
            measuredHeight = getMeasuredHeight() / this.renderer.getMeasuredHeight();
            measuredWidth = getMeasuredWidth();
            f = Math.max(measuredHeight, measuredWidth / this.renderer.getMeasuredWidth());
            this.scaleTextureToFill = f;
        }
        if (this.thumb != null) {
            this.scaleThumb = Math.max(getMeasuredWidth() / this.thumb.getWidth(), getMeasuredHeight() / this.thumb.getHeight());
        }
        if (!this.animateOnNextLayout) {
            if (this.currentAnimation == null) {
                this.renderer.setScaleX(this.scaleTextureToFill);
                this.renderer.setScaleY(this.scaleTextureToFill);
                TextureView textureView2 = this.blurRenderer;
                if (textureView2 != null) {
                    textureView2.setScaleX(this.scaleTextureToFillBlur);
                    this.blurRenderer.setScaleY(this.scaleTextureToFillBlur);
                }
                this.currentThumbScale = this.scaleThumb;
                return;
            }
            return;
        }
        this.aninateFromScale /= this.renderer.getMeasuredWidth() / this.animateFromRendererW;
        this.aninateFromScaleBlur /= this.renderer.getMeasuredWidth() / this.animateFromRendererW;
        this.animateOnNextLayout = false;
        if (!this.animateWithParent || getParent() == null) {
            top = this.animateFromY - getTop();
            left = this.animateFromX - getLeft();
        } else {
            View view = (View) getParent();
            top = this.animateFromY - view.getTop();
            left = this.animateFromX - view.getLeft();
        }
        this.clipVertical = 0.0f;
        this.clipHorizontal = 0.0f;
        if (this.animateFromHeight != getMeasuredHeight()) {
            float measuredHeight2 = (getMeasuredHeight() - this.animateFromHeight) / 2.0f;
            this.clipVertical = measuredHeight2;
            top -= measuredHeight2;
        }
        final float f2 = top;
        if (this.animateFromWidth != getMeasuredWidth()) {
            float measuredWidth2 = (getMeasuredWidth() - this.animateFromWidth) / 2.0f;
            this.clipHorizontal = measuredWidth2;
            left -= measuredWidth2;
        }
        final float f3 = left;
        setTranslationY(f2);
        setTranslationX(f3);
        ValueAnimator valueAnimator = this.currentAnimation;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.currentAnimation.cancel();
        }
        this.renderer.setScaleX(this.aninateFromScale);
        this.renderer.setScaleY(this.aninateFromScale);
        TextureView textureView3 = this.blurRenderer;
        if (textureView3 != null) {
            textureView3.setScaleX(this.aninateFromScaleBlur);
            this.blurRenderer.setScaleY(this.aninateFromScaleBlur);
        }
        this.currentClipVertical = this.clipVertical;
        this.currentClipHorizontal = this.clipHorizontal;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
        invalidate();
        final float f4 = this.aninateFromScale;
        final float f5 = this.aninateFromScaleBlur;
        final float f6 = this.animateFromThumbScale;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.currentAnimation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                VoIPTextureView.this.lambda$onLayout$0(f4, f5, f3, f2, f6, valueAnimator2);
            }
        });
        long j = this.animateNextDuration;
        if (j != 0) {
            this.currentAnimation.setDuration(j);
        } else {
            this.currentAnimation.setDuration(350L);
        }
        this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                VoIPTextureView voIPTextureView = VoIPTextureView.this;
                voIPTextureView.currentClipVertical = 0.0f;
                voIPTextureView.currentClipHorizontal = 0.0f;
                voIPTextureView.renderer.setScaleX(voIPTextureView.scaleTextureToFill);
                VoIPTextureView voIPTextureView2 = VoIPTextureView.this;
                voIPTextureView2.renderer.setScaleY(voIPTextureView2.scaleTextureToFill);
                VoIPTextureView voIPTextureView3 = VoIPTextureView.this;
                TextureView textureView4 = voIPTextureView3.blurRenderer;
                if (textureView4 != null) {
                    textureView4.setScaleX(voIPTextureView3.scaleTextureToFillBlur);
                    VoIPTextureView voIPTextureView4 = VoIPTextureView.this;
                    voIPTextureView4.blurRenderer.setScaleY(voIPTextureView4.scaleTextureToFillBlur);
                }
                VoIPTextureView.this.setTranslationY(0.0f);
                VoIPTextureView.this.setTranslationX(0.0f);
                VoIPTextureView voIPTextureView5 = VoIPTextureView.this;
                voIPTextureView5.currentThumbScale = voIPTextureView5.scaleThumb;
                VoIPTextureView.this.currentAnimation = null;
            }
        });
        this.currentAnimation.start();
        if (!this.animateOnNextLayoutAnimations.isEmpty()) {
            for (int i6 = 0; i6 < this.animateOnNextLayoutAnimations.size(); i6++) {
                ((Animator) this.animateOnNextLayoutAnimations.get(i6)).start();
            }
        }
        this.animateOnNextLayoutAnimations.clear();
        this.animateNextDuration = 0L;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        if (!this.applyRotation) {
            this.ignoreLayout = true;
            this.renderer.setScreenRotation(((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation());
            this.ignoreLayout = false;
        }
        super.onMeasure(i, i2);
        updateRendererSize();
        super.onMeasure(i, i2);
        this.renderer.updateRotation();
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void saveCameraLastBitmap() {
        Bitmap bitmap = this.renderer.getBitmap(150, 150);
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            return;
        }
        Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg"));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Throwable unused) {
        }
    }

    public void setAnimateNextDuration(long j) {
        this.animateNextDuration = j;
    }

    public void setAnimateWithParent(boolean z) {
        this.animateWithParent = z;
    }

    public void setIsScreencast(boolean z) {
        this.screencast = z;
        this.screencastView.setVisibility(z ? 0 : 8);
        if (this.screencast) {
            this.renderer.setVisibility(8);
            TextureView textureView = this.blurRenderer;
            if (textureView != null) {
                textureView.setVisibility(8);
            }
            this.imageView.setVisibility(8);
            return;
        }
        this.renderer.setVisibility(0);
        TextureView textureView2 = this.blurRenderer;
        if (textureView2 != null) {
            textureView2.setVisibility(0);
        }
    }

    public void setRoundCorners(float f) {
        if (this.roundRadius != f) {
            this.roundRadius = f;
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline();
            } else {
                invalidate();
            }
        }
    }

    public void setScreenshareMiniProgress(float f, boolean z) {
        float f2;
        if (this.screencast) {
            float scaleX = ((View) getParent()).getScaleX();
            float f3 = 1.0f;
            this.screencastText.setAlpha(1.0f - f);
            if (z) {
                f2 = 0.4f * f;
            } else {
                f3 = 1.0f / scaleX;
                f2 = (0.4f / scaleX) * f;
            }
            float f4 = f3 - f2;
            this.screencastImage.setScaleX(f4);
            this.screencastImage.setScaleY(f4);
            this.screencastImage.setTranslationY(AndroidUtilities.dp(60.0f) * f);
        }
    }

    public void setStub(VoIPTextureView voIPTextureView) {
        if (this.screencast) {
            return;
        }
        Bitmap bitmap = voIPTextureView.renderer.getBitmap();
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            this.imageView.setImageDrawable(voIPTextureView.imageView.getDrawable());
        } else {
            this.imageView.setImageBitmap(bitmap);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        this.stubVisibleProgress = 1.0f;
        this.imageView.setVisibility(0);
        this.imageView.setAlpha(1.0f);
    }

    public void setThumb(Bitmap bitmap) {
        this.thumb = bitmap;
    }

    public void synchOrRunAnimation(Animator animator) {
        if (this.animateOnNextLayout) {
            this.animateOnNextLayoutAnimations.add(animator);
        } else {
            animator.start();
        }
    }

    public void updateRendererSize() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            textureView.getLayoutParams().width = this.renderer.getMeasuredWidth();
            this.blurRenderer.getLayoutParams().height = this.renderer.getMeasuredHeight();
        }
    }

    public void updateRotation() {
        if (this.applyRotation) {
            return;
        }
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
    }
}
