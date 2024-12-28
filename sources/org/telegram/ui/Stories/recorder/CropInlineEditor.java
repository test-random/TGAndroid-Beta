package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.Crop.CropRotationWheel;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.Crop.CropView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.PhotoView;

public abstract class CropInlineEditor extends FrameLayout {
    private final AnimatedFloat animatedMirror;
    private final AnimatedFloat animatedOrientation;
    private float appearProgress;
    public boolean applied;
    public final FrameLayout buttonsLayout;
    public final TextView cancelButton;
    public boolean closing;
    public final ContentView contentView;
    public final FrameLayout controlsLayout;
    public final TextView cropButton;
    private final CropTransform cropTransform;
    public final CropView cropView;
    private int lastOrientation;
    private PhotoView photoView;
    private final int[] photoViewLocation;
    private final PreviewView previewContainer;
    private final int[] previewLocation;
    public final TextView resetButton;
    private final Theme.ResourcesProvider resourcesProvider;
    public final LinearLayout shapesLayout;
    private final int[] thisLocation;
    public final CropRotationWheel wheel;

    public class ContentView extends View {
        private final Paint dimPaint;
        private final Matrix identityMatrix;
        private final Matrix matrix;
        private final Path previewClipPath;
        private final RectF previewClipRect;
        private final Matrix previewMatrix;

        public ContentView(Context context) {
            super(context);
            this.dimPaint = new Paint(1);
            this.previewClipPath = new Path();
            this.previewClipRect = new RectF();
            this.previewMatrix = new Matrix();
            this.identityMatrix = new Matrix();
            this.matrix = new Matrix();
        }

        private void applyCrop(Canvas canvas, float f, float f2, float f3) {
            int currentWidth = CropInlineEditor.this.getCurrentWidth();
            int currentHeight = CropInlineEditor.this.getCurrentHeight();
            int orientation = CropInlineEditor.this.cropTransform.getOrientation();
            if (orientation == 90 || orientation == 270) {
                currentHeight = currentWidth;
                currentWidth = currentHeight;
            }
            float trueCropScale = ((CropInlineEditor.this.cropTransform.getTrueCropScale() - 1.0f) * (1.0f - f)) + 1.0f;
            float f4 = currentWidth;
            float containerWidth = getContainerWidth() / f4;
            float f5 = currentHeight;
            if (containerWidth * f5 > getContainerHeight()) {
                containerWidth = getContainerHeight() / f5;
            }
            canvas.translate(CropInlineEditor.this.cropTransform.getCropAreaX() * f3, CropInlineEditor.this.cropTransform.getCropAreaY() * f3);
            float scale = (CropInlineEditor.this.cropTransform.getScale() / trueCropScale) * containerWidth;
            float lerp = (CropInlineEditor.this.photoView == null || CropInlineEditor.this.photoView.crop == null) ? AndroidUtilities.lerp(1.0f, scale, f) : AndroidUtilities.lerp(CropInlineEditor.this.photoView.crop.cropScale, scale, f);
            canvas.scale(lerp, lerp);
            canvas.translate(CropInlineEditor.this.cropTransform.getCropPx() * f4 * f3, CropInlineEditor.this.cropTransform.getCropPy() * f5 * f3);
            float orientation2 = CropInlineEditor.this.photoView.getOrientation() + CropInlineEditor.this.cropTransform.getRotation() + CropInlineEditor.this.animatedOrientation.set(((CropInlineEditor.this.lastOrientation / 360) * 360) + orientation);
            canvas.rotate(CropInlineEditor.this.photoView.crop == null ? AndroidUtilities.lerp(0.0f, orientation2, CropInlineEditor.this.appearProgress) : AndroidUtilities.lerp(CropInlineEditor.this.photoView.crop.cropRotate + CropInlineEditor.this.photoView.crop.transformRotation, orientation2, CropInlineEditor.this.appearProgress));
        }

        private float getContainerHeight() {
            boolean z = getContext() instanceof BubbleActivity;
            CropView cropView = CropInlineEditor.this.cropView;
            return ((getHeight() - (cropView.topPadding + ((Build.VERSION.SDK_INT < 21 || z) ? 0 : AndroidUtilities.statusBarHeight))) - cropView.bottomPadding) - AndroidUtilities.dp(32.0f);
        }

        private float getContainerWidth() {
            return getWidth() - AndroidUtilities.dp(32.0f);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float f;
            float f2;
            if (CropInlineEditor.this.photoView == null) {
                return;
            }
            canvas.save();
            this.dimPaint.setColor(-16777216);
            this.dimPaint.setAlpha((int) (CropInlineEditor.this.appearProgress * 255.0f));
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.dimPaint);
            boolean z = true;
            if (CropInlineEditor.this.appearProgress < 1.0f) {
                this.previewClipPath.rewind();
                this.previewClipRect.set(0.0f, 0.0f, CropInlineEditor.this.previewContainer.getWidth(), CropInlineEditor.this.previewContainer.getHeight());
                this.previewClipRect.offset(CropInlineEditor.this.previewLocation[0], CropInlineEditor.this.previewLocation[1]);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                AndroidUtilities.lerp(this.previewClipRect, rectF, CropInlineEditor.this.appearProgress, this.previewClipRect);
                float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(12.0f), 0, CropInlineEditor.this.appearProgress);
                this.previewClipPath.addRoundRect(this.previewClipRect, lerp, lerp, Path.Direction.CW);
                canvas.clipPath(this.previewClipPath);
            }
            float f3 = 1.0f - CropInlineEditor.this.appearProgress;
            float f4 = CropInlineEditor.this.appearProgress;
            canvas.translate((-CropInlineEditor.this.thisLocation[0]) * f3, (-CropInlineEditor.this.thisLocation[1]) * f3);
            if (f3 > 0.0f) {
                CropInlineEditor cropInlineEditor = CropInlineEditor.this;
                if (cropInlineEditor.closing) {
                    cropInlineEditor.photoView.getLocationOnScreen(CropInlineEditor.this.photoViewLocation);
                }
                canvas.translate(CropInlineEditor.this.photoViewLocation[0] * f3, CropInlineEditor.this.photoViewLocation[1] * f3);
                if (CropInlineEditor.this.photoView.crop != null) {
                    f = CropInlineEditor.this.photoView.crop.cropPw;
                    f2 = CropInlineEditor.this.photoView.crop.cropPh;
                } else {
                    f = 1.0f;
                    f2 = 1.0f;
                }
                float lerp2 = AndroidUtilities.lerp(1.0f, ((CropInlineEditor.this.photoView.getWidth() / f) * CropInlineEditor.this.photoView.getScaleX()) / CropInlineEditor.this.previewContainer.getWidth(), f3);
                canvas.scale(lerp2, lerp2);
                canvas.rotate(CropInlineEditor.this.photoView.getRotation() * f3);
                canvas.translate(((CropInlineEditor.this.photoView.getContentWidth() * f) / 2.0f) * f3, ((CropInlineEditor.this.photoView.getContentHeight() * f2) / 2.0f) * f3);
            }
            canvas.translate((AndroidUtilities.dp(16.0f) + (getContainerWidth() / 2.0f)) * f4, (CropInlineEditor.this.cropView.topPadding + ((Build.VERSION.SDK_INT < 21 || (getContext() instanceof BubbleActivity)) ? 0 : AndroidUtilities.statusBarHeight) + ((getContainerHeight() + AndroidUtilities.dp(32.0f)) / 2.0f)) * f4);
            if (f3 > 0.0f) {
                float contentWidth = CropInlineEditor.this.photoView.getContentWidth();
                float contentHeight = CropInlineEditor.this.photoView.getContentHeight();
                float f5 = CropInlineEditor.this.photoView.crop != null ? CropInlineEditor.this.photoView.crop.cropPw : 1.0f;
                float f6 = CropInlineEditor.this.photoView.crop != null ? CropInlineEditor.this.photoView.crop.cropPh : 1.0f;
                float lerp3 = (contentWidth * AndroidUtilities.lerp(1.0f, f5, f3)) / 2.0f;
                float lerp4 = (contentHeight * AndroidUtilities.lerp(1.0f, f6, f3)) / 2.0f;
                float lerp5 = AndroidUtilities.lerp(1.0f, 4.0f, f4);
                canvas.clipRect((-lerp3) * lerp5, (-lerp4) * lerp5, lerp3 * lerp5, lerp4 * lerp5);
            }
            applyCrop(canvas, f4, f3, 1.0f);
            canvas.rotate(CropInlineEditor.this.photoView.getOrientation());
            AnimatedFloat animatedFloat = CropInlineEditor.this.animatedMirror;
            CropInlineEditor cropInlineEditor2 = CropInlineEditor.this;
            if (!cropInlineEditor2.closing) {
                z = cropInlineEditor2.cropView.isMirrored();
            } else if (cropInlineEditor2.photoView.crop == null || !CropInlineEditor.this.photoView.crop.mirrored) {
                z = false;
            }
            canvas.scale(AndroidUtilities.lerp(1.0f, -1.0f, animatedFloat.set(z)), 1.0f);
            canvas.translate((-CropInlineEditor.this.photoView.getContentWidth()) / 2.0f, (-CropInlineEditor.this.photoView.getContentHeight()) / 2.0f);
            CropInlineEditor.this.photoView.drawContent(canvas);
            canvas.restore();
        }
    }

    public CropInlineEditor(Context context, PreviewView previewView, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.lastOrientation = 0;
        this.appearProgress = 0.0f;
        this.thisLocation = new int[2];
        this.previewLocation = new int[2];
        this.photoViewLocation = new int[2];
        this.cropTransform = new CropTransform();
        this.previewContainer = previewView;
        this.resourcesProvider = resourcesProvider;
        ContentView contentView = new ContentView(context);
        this.contentView = contentView;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatedMirror = new AnimatedFloat(contentView, 0L, 320L, cubicBezierInterpolator);
        this.animatedOrientation = new AnimatedFloat(contentView, 0L, 320L, cubicBezierInterpolator);
        CropView cropView = new CropView(context) {
            @Override
            public int getCurrentHeight() {
                return CropInlineEditor.this.getCurrentHeight();
            }

            @Override
            public int getCurrentWidth() {
                return CropInlineEditor.this.getCurrentWidth();
            }
        };
        this.cropView = cropView;
        cropView.setListener(new CropView.CropViewListener() {
            @Override
            public void onAspectLock(boolean z) {
            }

            @Override
            public void onChange(boolean z) {
            }

            @Override
            public void onTapUp() {
            }

            @Override
            public void onUpdate() {
                CropInlineEditor.this.contentView.invalidate();
            }
        });
        addView(cropView);
        FrameLayout frameLayout = new FrameLayout(context);
        this.controlsLayout = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
        CropRotationWheel cropRotationWheel = new CropRotationWheel(context);
        this.wheel = cropRotationWheel;
        cropRotationWheel.setListener(new CropRotationWheel.RotationWheelListener() {
            @Override
            public void aspectRatioPressed() {
                CropInlineEditor.this.cropView.showAspectRatioDialog();
            }

            @Override
            public boolean mirror() {
                CropInlineEditor.this.contentView.invalidate();
                return CropInlineEditor.this.cropView.mirror();
            }

            @Override
            public void onChange(float f) {
                CropInlineEditor.this.cropView.setRotation(f);
            }

            @Override
            public void onEnd(float f) {
                CropInlineEditor.this.cropView.onRotationEnded();
            }

            @Override
            public void onStart() {
                CropInlineEditor.this.cropView.onRotationBegan();
            }

            @Override
            public boolean rotate90Pressed() {
                boolean rotate = CropInlineEditor.this.cropView.rotate(-90.0f);
                CropInlineEditor.this.cropView.maximize(true);
                CropInlineEditor.this.contentView.invalidate();
                return rotate;
            }
        });
        frameLayout.addView(cropRotationWheel, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 52.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.buttonsLayout = frameLayout2;
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 52.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.cancelButton = textView;
        textView.setTextSize(1, 14.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setBackground(Theme.createSelectorDrawable(-12763843, 0));
        textView.setTextColor(-1);
        textView.setText(LocaleController.getString(R.string.Cancel));
        textView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -1, 115));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CropInlineEditor.this.lambda$new$0(view);
            }
        });
        TextView textView2 = new TextView(context);
        this.resetButton = textView2;
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setBackground(Theme.createSelectorDrawable(-12763843, 0));
        textView2.setTextColor(-1);
        textView2.setText(LocaleController.getString(R.string.CropReset));
        textView2.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        frameLayout2.addView(textView2, LayoutHelper.createFrame(-2, -1, 113));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CropInlineEditor.this.lambda$new$1(view);
            }
        });
        TextView textView3 = new TextView(context);
        this.cropButton = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setBackground(Theme.createSelectorDrawable(-12763843, 0));
        textView3.setTextColor(-15098625);
        textView3.setText(LocaleController.getString(R.string.StoryCrop));
        textView3.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        frameLayout2.addView(textView3, LayoutHelper.createFrame(-2, -1, 117));
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CropInlineEditor.this.lambda$new$2(view);
            }
        });
        this.shapesLayout = new LinearLayout(context);
    }

    public int getCurrentHeight() {
        PhotoView photoView = this.photoView;
        if (photoView == null) {
            return 1;
        }
        return (photoView.getOrientation() == 90 || this.photoView.getOrientation() == 270) ? this.photoView.getContentWidth() : this.photoView.getContentHeight();
    }

    public int getCurrentWidth() {
        PhotoView photoView = this.photoView;
        if (photoView == null) {
            return 1;
        }
        return (photoView.getOrientation() == 90 || this.photoView.getOrientation() == 270) ? this.photoView.getContentHeight() : this.photoView.getContentWidth();
    }

    public void lambda$apply$3() {
        EntityView.SelectionView selectionView = this.photoView.selectionView;
        if (selectionView != null) {
            selectionView.updatePosition();
        }
        this.photoView.updatePosition();
    }

    public void lambda$new$0(View view) {
        close();
    }

    public void lambda$new$1(View view) {
        this.cropView.reset(true);
        this.wheel.setRotated(false);
        this.wheel.setMirrored(false);
        this.wheel.setRotation(0.0f, true);
    }

    public void lambda$new$2(View view) {
        apply();
        close();
    }

    public void apply() {
        PhotoView photoView = this.photoView;
        if (photoView == null) {
            return;
        }
        this.applied = true;
        photoView.crop = new MediaController.CropState();
        this.cropView.applyToCropState(this.photoView.crop);
        PhotoView photoView2 = this.photoView;
        photoView2.crop.orientation = photoView2.getOrientation();
        this.photoView.updatePosition();
        this.photoView.requestLayout();
        this.photoView.containerView.requestLayout();
        this.photoView.containerView.invalidate();
        this.photoView.containerView.post(new Runnable() {
            @Override
            public final void run() {
                CropInlineEditor.this.lambda$apply$3();
            }
        });
    }

    protected abstract void close();

    public void disappearStarts() {
        this.closing = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public float getAppearProgress() {
        return this.appearProgress;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.cropView.setTopPadding(AndroidUtilities.dp(52.0f));
        this.cropView.setBottomPadding(this.controlsLayout.getPaddingBottom() + AndroidUtilities.dp(116.0f));
        super.onLayout(z, i, i2, i3, i4);
    }

    public void set(PhotoView photoView) {
        if (photoView == null) {
            return;
        }
        this.photoView = photoView;
        setVisibility(0);
        this.applied = false;
        this.closing = false;
        this.cropView.onShow();
        getLocationOnScreen(this.thisLocation);
        this.previewContainer.getLocationOnScreen(this.previewLocation);
        photoView.getLocationOnScreen(this.photoViewLocation);
        MediaController.CropState cropState = photoView.crop;
        if (cropState == null) {
            cropState = null;
        }
        this.cropView.start(photoView.getOrientation(), true, false, this.cropTransform, cropState);
        this.wheel.setRotation(this.cropView.getRotation());
        CropRotationWheel cropRotationWheel = this.wheel;
        if (cropState != null) {
            cropRotationWheel.setRotation(cropState.cropRotate, false);
            this.wheel.setRotated(cropState.transformRotation != 0);
            this.wheel.setMirrored(cropState.mirrored);
            this.animatedMirror.set(cropState.mirrored, false);
        } else {
            cropRotationWheel.setRotation(0.0f, false);
            this.wheel.setRotated(false);
            this.wheel.setMirrored(false);
            this.animatedMirror.set(false, false);
        }
        this.cropView.updateMatrix();
        this.contentView.setVisibility(0);
        this.contentView.invalidate();
    }

    public void setAppearProgress(float f) {
        if (Math.abs(this.appearProgress - f) < 0.001f) {
            return;
        }
        this.appearProgress = f;
        this.contentView.invalidate();
        this.cropView.areaView.setDimAlpha(0.5f * f);
        this.cropView.areaView.setFrameAlpha(f);
        this.cropView.areaView.invalidate();
    }

    public void stop() {
        this.photoView = null;
        this.cropView.stop();
        this.cropView.onHide();
        this.contentView.setVisibility(8);
        setVisibility(8);
    }
}
