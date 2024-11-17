package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.Crop.CropGestureDetector;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.VideoEditTextureView;

public class CropView extends FrameLayout implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener {
    private boolean animating;
    public CropAreaView areaView;
    private Bitmap bitmap;
    private int bitmapRotation;
    private float bottomPadding;
    RectF cropRect;
    private CropTransform cropTransform;
    private CropGestureDetector detector;
    private boolean freeform;
    private boolean hasAspectRatioDialog;
    private ImageView imageView;
    private boolean inBubbleMode;
    private RectF initialAreaRect;
    private boolean isVisible;
    private CropViewListener listener;
    private Matrix overlayMatrix;
    private PaintingOverlay paintingOverlay;
    private RectF previousAreaRect;
    private float rotationStartScale;
    RectF sizeRect;
    public CropState state;
    private Matrix tempMatrix;
    private CropRectangle tempRect;
    float[] values;
    private VideoEditTextureView videoEditTextureView;

    public static class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        void applyMatrix(Matrix matrix) {
            matrix.mapPoints(this.coords);
        }

        void getRect(RectF rectF) {
            float[] fArr = this.coords;
            rectF.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }

        void setRect(RectF rectF) {
            float[] fArr = this.coords;
            float f = rectF.left;
            fArr[0] = f;
            float f2 = rectF.top;
            fArr[1] = f2;
            float f3 = rectF.right;
            fArr[2] = f3;
            fArr[3] = f2;
            fArr[4] = f3;
            float f4 = rectF.bottom;
            fArr[5] = f4;
            fArr[6] = f;
            fArr[7] = f4;
        }
    }

    public class CropState {
        public float baseRotation;
        public float height;
        public Matrix matrix;
        public float minimumScale;
        public boolean mirrored;
        public float orientation;
        public float rotation;
        public float scale;
        public float width;
        public float x;
        public float y;

        private CropState(int i, int i2, int i3) {
            this.width = i;
            this.height = i2;
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = i3;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        public float getBaseRotation() {
            return this.baseRotation;
        }

        public void getConcatMatrix(Matrix matrix) {
            matrix.postConcat(this.matrix);
        }

        public float getHeight() {
            return this.height;
        }

        public Matrix getMatrix() {
            Matrix matrix = new Matrix();
            matrix.set(this.matrix);
            return matrix;
        }

        public float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        public int getOrientationOnly() {
            return (int) this.orientation;
        }

        public float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        public float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        public float getRotation() {
            return this.rotation;
        }

        public float getScale() {
            return this.scale;
        }

        public float getWidth() {
            return this.width;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public boolean hasChanges() {
            return Math.abs(this.x) > 1.0E-5f || Math.abs(this.y) > 1.0E-5f || Math.abs(this.scale - this.minimumScale) > 1.0E-5f || Math.abs(this.rotation) > 1.0E-5f || Math.abs(this.orientation) > 1.0E-5f;
        }

        public void mirror() {
            this.mirrored = !this.mirrored;
        }

        public void reset(CropAreaView cropAreaView, float f, boolean z) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = f;
            updateMinimumScale();
            float f2 = this.minimumScale;
            this.scale = f2;
            this.matrix.postScale(f2, f2);
        }

        public void rotate(float f, float f2, float f3) {
            this.rotation += f;
            this.matrix.postRotate(f, f2, f3);
        }

        public void scale(float f, float f2, float f3) {
            this.scale *= f;
            this.matrix.postScale(f, f, f2, f3);
        }

        public void translate(float f, float f2) {
            this.x += f;
            this.y += f2;
            this.matrix.postTranslate(f, f2);
        }

        public void update(int i, int i2, int i3) {
            float f = i;
            this.scale *= this.width / f;
            this.width = f;
            this.height = i2;
            updateMinimumScale();
            this.matrix.getValues(CropView.this.values);
            this.matrix.reset();
            Matrix matrix = this.matrix;
            float f2 = this.scale;
            matrix.postScale(f2, f2);
            Matrix matrix2 = this.matrix;
            float[] fArr = CropView.this.values;
            matrix2.postTranslate(fArr[2], fArr[5]);
            CropView.this.updateMatrix();
        }

        private void updateMinimumScale() {
            float f = (this.orientation + this.baseRotation) % 180.0f;
            float f2 = f != 0.0f ? this.height : this.width;
            this.minimumScale = CropView.this.freeform ? CropView.this.areaView.getCropWidth() / f2 : Math.max(CropView.this.areaView.getCropWidth() / f2, CropView.this.areaView.getCropHeight() / (f != 0.0f ? this.width : this.height));
        }
    }

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();
    }

    public CropView(Context context) {
        super(context);
        this.values = new float[9];
        this.cropRect = new RectF();
        this.sizeRect = new RectF(0.0f, 0.0f, 1280.0f, 1280.0f);
        this.inBubbleMode = context instanceof BubbleActivity;
        this.previousAreaRect = new RectF();
        this.initialAreaRect = new RectF();
        this.overlayMatrix = new Matrix();
        this.tempRect = new CropRectangle();
        this.tempMatrix = new Matrix();
        this.animating = false;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        CropAreaView cropAreaView = new CropAreaView(context);
        this.areaView = cropAreaView;
        cropAreaView.setListener(this);
        addView(this.areaView);
    }

    public static void editBitmap(Context context, String str, Bitmap bitmap, Canvas canvas, Bitmap bitmap2, Bitmap.CompressFormat compressFormat, Matrix matrix, int i, int i2, float f, float f2, float f3, float f4, boolean z, ArrayList arrayList, boolean z2) {
        float f5;
        float f6;
        int i3 = i;
        if (z2) {
            try {
                bitmap2.eraseColor(0);
            } catch (Throwable th) {
                FileLog.e(th);
                return;
            }
        }
        Bitmap decodeFile = bitmap == null ? BitmapFactory.decodeFile(str) : bitmap;
        float max = Math.max(decodeFile.getWidth(), decodeFile.getHeight()) / Math.max(i, i2);
        Matrix matrix2 = new Matrix();
        matrix2.postTranslate((-decodeFile.getWidth()) / 2, (-decodeFile.getHeight()) / 2);
        if (z) {
            matrix2.postScale(-1.0f, 1.0f);
        }
        float f7 = 1.0f / max;
        matrix2.postScale(f7, f7);
        matrix2.postRotate(f3);
        matrix2.postConcat(matrix);
        matrix2.postScale(f4, f4);
        matrix2.postTranslate(bitmap2.getWidth() / 2, bitmap2.getHeight() / 2);
        canvas.drawBitmap(decodeFile, matrix2, new Paint(2));
        FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
        bitmap2.compress(compressFormat, 87, fileOutputStream);
        fileOutputStream.close();
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            int i4 = 0;
            while (i4 < size) {
                VideoEditedInfo.MediaEntity mediaEntity = (VideoEditedInfo.MediaEntity) arrayList.get(i4);
                float[] fArr = {(mediaEntity.x + (mediaEntity.width / 2.0f)) * decodeFile.getWidth(), (mediaEntity.y + (mediaEntity.height / 2.0f)) * decodeFile.getHeight(), mediaEntity.textViewX * decodeFile.getWidth(), mediaEntity.textViewY * decodeFile.getHeight()};
                matrix2.mapPoints(fArr);
                int width = decodeFile.getWidth();
                int height = decodeFile.getHeight();
                if (f3 == 90.0f || f3 == 270.0f) {
                    width = decodeFile.getHeight();
                    height = decodeFile.getWidth();
                }
                if (mediaEntity.type == 1) {
                    mediaEntity.width = ((mediaEntity.width * i3) / bitmap2.getWidth()) * f4 * f;
                    f6 = mediaEntity.height * i2;
                    f5 = bitmap2.getHeight();
                } else {
                    float f8 = i3;
                    float f9 = width;
                    mediaEntity.viewWidth = (int) ((mediaEntity.viewWidth / f8) * f9);
                    float f10 = i2;
                    f5 = height;
                    mediaEntity.viewHeight = (int) ((mediaEntity.viewHeight / f10) * f5);
                    mediaEntity.width = ((mediaEntity.width * f8) / f9) * f4 * f;
                    f6 = mediaEntity.height * f10;
                }
                mediaEntity.height = (f6 / f5) * f4 * f;
                mediaEntity.x = (fArr[0] / bitmap2.getWidth()) - (mediaEntity.width / 2.0f);
                mediaEntity.y = (fArr[1] / bitmap2.getHeight()) - (mediaEntity.height / 2.0f);
                mediaEntity.textViewX = fArr[2] / bitmap2.getWidth();
                mediaEntity.textViewY = fArr[3] / bitmap2.getHeight();
                double d = mediaEntity.rotation;
                double d2 = f2 + f3;
                Double.isNaN(d2);
                Double.isNaN(d);
                mediaEntity.rotation = (float) (d - (d2 * 0.017453292519943295d));
                i4++;
                i3 = i;
            }
        }
        decodeFile.recycle();
    }

    private void fillAreaView(RectF rectF, boolean z) {
        final float f;
        final boolean z2;
        int i = 0;
        if (this.state == null) {
            return;
        }
        final float[] fArr = {1.0f};
        float max = Math.max(rectF.width() / this.areaView.getCropWidth(), rectF.height() / this.areaView.getCropHeight());
        if (this.state.getScale() * max > 30.0f) {
            f = 30.0f / this.state.getScale();
            z2 = true;
        } else {
            f = max;
            z2 = false;
        }
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            i = AndroidUtilities.statusBarHeight;
        }
        final float centerX = ((rectF.centerX() - (this.imageView.getWidth() / 2)) / this.areaView.getCropWidth()) * this.state.getOrientedWidth();
        final float centerY = ((rectF.centerY() - (((this.imageView.getHeight() - this.bottomPadding) + i) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.lambda$fillAreaView$0(f, fArr, centerX, centerY, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (z2) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, ofFloat, true);
        this.initialAreaRect.set(rectF);
    }

    public void fitContentInBounds(boolean z, boolean z2, boolean z3) {
        fitContentInBounds(z, z2, z3, false);
    }

    public void fitContentInBounds(final boolean r15, final boolean r16, final boolean r17, final boolean r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Crop.CropView.fitContentInBounds(boolean, boolean, boolean, boolean):void");
    }

    private float fitScale(RectF rectF, float f, float f2) {
        float width = rectF.width() * f2;
        float height = rectF.height() * f2;
        float width2 = (rectF.width() - width) / 2.0f;
        float height2 = (rectF.height() - height) / 2.0f;
        float f3 = rectF.left + width2;
        float f4 = rectF.top + height2;
        rectF.set(f3, f4, width + f3, height + f4);
        return f * f2;
    }

    private void fitTranslation(RectF rectF, RectF rectF2, PointF pointF, float f) {
        float f2 = rectF2.left;
        float f3 = rectF2.top;
        float f4 = rectF2.right;
        float f5 = rectF2.bottom;
        float f6 = rectF.left;
        if (f6 > f2) {
            f4 += f6 - f2;
            f2 = f6;
        }
        float f7 = rectF.top;
        if (f7 > f3) {
            f5 += f7 - f3;
            f3 = f7;
        }
        float f8 = rectF.right;
        if (f8 < f4) {
            f2 += f8 - f4;
        }
        float f9 = rectF.bottom;
        if (f9 < f5) {
            f3 += f9 - f5;
        }
        float centerX = rectF2.centerX() - (f2 + (rectF2.width() / 2.0f));
        float centerY = rectF2.centerY() - (f3 + (rectF2.height() / 2.0f));
        double d = f;
        Double.isNaN(d);
        double d2 = 1.5707963267948966d - d;
        double sin = Math.sin(d2);
        double d3 = centerX;
        Double.isNaN(d3);
        float f10 = (float) (sin * d3);
        double cos = Math.cos(d2);
        Double.isNaN(d3);
        float f11 = (float) (cos * d3);
        Double.isNaN(d);
        double d4 = d + 1.5707963267948966d;
        double cos2 = Math.cos(d4);
        double d5 = centerY;
        Double.isNaN(d5);
        double sin2 = Math.sin(d4);
        Double.isNaN(d5);
        pointF.set(pointF.x + f10 + ((float) (cos2 * d5)), pointF.y + f11 + ((float) (sin2 * d5)));
    }

    public static String getCopy(String str) {
        File file = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_temp.jpg");
        try {
            AndroidUtilities.copyFile(new File(str), file);
        } catch (Exception e) {
            FileLog.e(e);
        }
        return file.getAbsolutePath();
    }

    public int getCurrentHeight() {
        VideoEditTextureView videoEditTextureView = this.videoEditTextureView;
        if (videoEditTextureView != null) {
            return videoEditTextureView.getVideoHeight();
        }
        Bitmap bitmap = this.bitmap;
        if (bitmap == null) {
            return 1;
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? bitmap.getWidth() : bitmap.getHeight();
    }

    public int getCurrentWidth() {
        VideoEditTextureView videoEditTextureView = this.videoEditTextureView;
        if (videoEditTextureView != null) {
            return videoEditTextureView.getVideoWidth();
        }
        Bitmap bitmap = this.bitmap;
        if (bitmap == null) {
            return 1;
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? bitmap.getHeight() : bitmap.getWidth();
    }

    public void lambda$fillAreaView$0(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((f - 1.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f;
        float f4 = fArr[0];
        float f5 = floatValue / f4;
        fArr[0] = f4 * f5;
        this.state.scale(f5, f2, f3);
        updateMatrix();
    }

    public void lambda$fitContentInBounds$1(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f4 = fArr[1];
        float f5 = (f * floatValue) - f4;
        fArr[1] = f4 + f5;
        float f6 = fArr[2];
        float f7 = (f2 * floatValue) - f6;
        fArr[2] = f6 + f7;
        CropState cropState = this.state;
        float f8 = fArr[0];
        cropState.translate(f5 * f8, f7 * f8);
        float f9 = fArr[0];
        float f10 = (((f3 - 1.0f) * floatValue) + 1.0f) / f9;
        fArr[0] = f9 * f10;
        this.state.scale(f10, 0.0f, 0.0f);
        updateMatrix();
    }

    public void lambda$maximize$2(RectF rectF, RectF rectF2, float f, float f2, float f3, float f4, float f5, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        AndroidUtilities.lerp(rectF, this.initialAreaRect, floatValue, rectF2);
        this.areaView.setActualRect(rectF2);
        CropState cropState = this.state;
        float f6 = 1.0f - floatValue;
        float f7 = cropState.x - (f * f6);
        float f8 = cropState.y - (f2 * f6);
        float f9 = cropState.rotation - (f3 * f6);
        float lerp = AndroidUtilities.lerp(f4, f5, floatValue);
        CropState cropState2 = this.state;
        float f10 = lerp / cropState2.scale;
        cropState2.translate(-f7, -f8);
        this.state.scale(f10, 0.0f, 0.0f);
        this.state.rotate(-f9, 0.0f, 0.0f);
        fitContentInBounds(true, false, false);
    }

    public void lambda$showAspectRatioDialog$3(Integer[][] numArr, DialogInterface dialogInterface, int i) {
        this.hasAspectRatioDialog = false;
        if (i == 0) {
            setLockedAspectRatio((this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth()) / (this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight()));
            return;
        }
        if (i == 1) {
            setLockedAspectRatio(1.0f);
            return;
        }
        Integer[] numArr2 = numArr[i - 2];
        if (this.areaView.getAspectRatio() > 1.0f) {
            setLockedAspectRatio(numArr2[0].intValue() / numArr2[1].intValue());
        } else {
            setLockedAspectRatio(numArr2[1].intValue() / numArr2[0].intValue());
        }
    }

    public void lambda$showAspectRatioDialog$4(DialogInterface dialogInterface) {
        this.hasAspectRatioDialog = false;
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    private void setLockedAspectRatio(float f) {
        this.areaView.setLockedAspectRatio(f);
        RectF rectF = new RectF();
        this.areaView.calculateRect(rectF, f);
        fillAreaView(rectF, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    private void updateCropTransform() {
        int i;
        float f;
        if (this.cropTransform == null || this.state == null) {
            return;
        }
        this.areaView.getCropRect(this.cropRect);
        int ceil = (int) Math.ceil(scaleWidthToMaxSize(this.cropRect, this.sizeRect));
        int ceil2 = (int) Math.ceil(r2 / this.areaView.getAspectRatio());
        float cropWidth = ceil / this.areaView.getCropWidth();
        this.state.matrix.getValues(this.values);
        CropState cropState = this.state;
        float f2 = cropState.minimumScale * cropWidth;
        int orientationOnly = cropState.getOrientationOnly();
        while (orientationOnly < 0) {
            orientationOnly += 360;
        }
        if (orientationOnly == 90 || orientationOnly == 270) {
            CropState cropState2 = this.state;
            i = (int) cropState2.height;
            f = cropState2.width;
        } else {
            CropState cropState3 = this.state;
            i = (int) cropState3.width;
            f = cropState3.height;
        }
        int i2 = (int) f;
        double d = ceil;
        float f3 = i;
        double ceil3 = Math.ceil(f3 * f2);
        Double.isNaN(d);
        float f4 = (float) (d / ceil3);
        double d2 = ceil2;
        float f5 = i2;
        double ceil4 = Math.ceil(f2 * f5);
        Double.isNaN(d2);
        float f6 = (float) (d2 / ceil4);
        if (f4 > 1.0f || f6 > 1.0f) {
            float max = Math.max(f4, f6);
            f4 /= max;
            f6 /= max;
        }
        float f7 = f6;
        float f8 = f4;
        RectF targetRectToFill = this.areaView.getTargetRectToFill(f3 / f5);
        float width = this.freeform ? targetRectToFill.width() / f3 : Math.max(targetRectToFill.width() / f3, targetRectToFill.height() / f5);
        CropState cropState4 = this.state;
        float f9 = cropState4.scale;
        float f10 = f9 / width;
        float f11 = f9 / cropState4.minimumScale;
        float[] fArr = this.values;
        float f12 = (fArr[2] / f3) / f9;
        float f13 = (fArr[5] / f5) / f9;
        float f14 = cropState4.rotation;
        RectF targetRectToFill2 = this.areaView.getTargetRectToFill();
        float cropCenterX = this.areaView.getCropCenterX() - targetRectToFill2.centerX();
        float cropCenterY = this.areaView.getCropCenterY() - targetRectToFill2.centerY();
        CropTransform cropTransform = this.cropTransform;
        CropState cropState5 = this.state;
        boolean z = cropState5.mirrored || cropState5.hasChanges() || this.state.getBaseRotation() >= 1.0E-5f;
        int orientationOnly2 = this.state.getOrientationOnly();
        CropState cropState6 = this.state;
        cropTransform.setViewTransform(z, f12, f13, f14, orientationOnly2, f10, f11, cropState6.minimumScale / width, f8, f7, cropCenterX, cropCenterY, cropState6.mirrored);
    }

    public RectF calculateBoundingBox(float f, float f2, float f3) {
        RectF rectF = new RectF(0.0f, 0.0f, f, f2);
        Matrix matrix = new Matrix();
        matrix.postRotate(f3, f / 2.0f, f2 / 2.0f);
        matrix.mapRect(rectF);
        return rectF;
    }

    public RectF getActualRect() {
        this.areaView.getCropRect(this.cropRect);
        return this.cropRect;
    }

    public float getCropHeight() {
        return this.areaView.getCropHeight();
    }

    public float getCropLeft() {
        return this.areaView.getCropLeft();
    }

    public float getCropTop() {
        return this.areaView.getCropTop();
    }

    public float getCropWidth() {
        return this.areaView.getCropWidth();
    }

    public float getStateFullOrientation() {
        CropState cropState = this.state;
        if (cropState == null) {
            return 0.0f;
        }
        return cropState.orientation + cropState.baseRotation;
    }

    public boolean getStateMirror() {
        CropState cropState = this.state;
        return cropState != null && cropState.mirrored;
    }

    public float getStateOrientation() {
        CropState cropState = this.state;
        if (cropState == null) {
            return 0.0f;
        }
        return cropState.orientation;
    }

    public void hide() {
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false, false);
        this.areaView.invalidate();
    }

    public boolean isMirrored() {
        CropState cropState = this.state;
        if (cropState == null) {
            return false;
        }
        return cropState.mirrored;
    }

    public boolean isReady() {
        return (this.detector.isScaling() || this.detector.isDragging() || this.areaView.isDragging()) ? false : true;
    }

    public void makeCrop(MediaController.MediaEditState mediaEditState) {
        MediaController.CropState cropState;
        int i;
        int i2;
        float f;
        if (this.state == null) {
            return;
        }
        this.areaView.getCropRect(this.cropRect);
        int ceil = (int) Math.ceil(scaleWidthToMaxSize(this.cropRect, this.sizeRect));
        int ceil2 = (int) Math.ceil(r3 / this.areaView.getAspectRatio());
        float cropWidth = ceil / this.areaView.getCropWidth();
        if (mediaEditState.paintPath != null) {
            Bitmap createBitmap = Bitmap.createBitmap(ceil, ceil2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            String copy = getCopy(mediaEditState.paintPath);
            if (mediaEditState.croppedPaintPath != null) {
                new File(mediaEditState.croppedPaintPath).delete();
                mediaEditState.croppedPaintPath = null;
            }
            mediaEditState.croppedPaintPath = copy;
            ArrayList<VideoEditedInfo.MediaEntity> arrayList = mediaEditState.mediaEntities;
            if (arrayList == null || arrayList.isEmpty()) {
                mediaEditState.croppedMediaEntities = null;
            } else {
                mediaEditState.croppedMediaEntities = new ArrayList<>(mediaEditState.mediaEntities.size());
                int size = mediaEditState.mediaEntities.size();
                for (int i3 = 0; i3 < size; i3++) {
                    mediaEditState.croppedMediaEntities.add(mediaEditState.mediaEntities.get(i3).copy());
                }
            }
            Context context = getContext();
            Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
            Matrix matrix = this.state.matrix;
            int currentWidth = getCurrentWidth();
            int currentHeight = getCurrentHeight();
            CropState cropState2 = this.state;
            editBitmap(context, copy, null, canvas, createBitmap, compressFormat, matrix, currentWidth, currentHeight, cropState2.scale, cropState2.rotation, cropState2.getOrientationOnly(), cropWidth, false, mediaEditState.croppedMediaEntities, false);
        }
        if (mediaEditState.cropState == null) {
            mediaEditState.cropState = new MediaController.CropState();
        }
        this.state.matrix.getValues(this.values);
        CropState cropState3 = this.state;
        float f2 = cropState3.minimumScale * cropWidth;
        mediaEditState.cropState.transformRotation = cropState3.getOrientationOnly();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set transformRotation = " + mediaEditState.cropState.transformRotation);
        }
        while (true) {
            cropState = mediaEditState.cropState;
            i = cropState.transformRotation;
            if (i >= 0) {
                break;
            } else {
                cropState.transformRotation = i + 360;
            }
        }
        if (i == 90 || i == 270) {
            CropState cropState4 = this.state;
            i2 = (int) cropState4.height;
            f = cropState4.width;
        } else {
            CropState cropState5 = this.state;
            i2 = (int) cropState5.width;
            f = cropState5.height;
        }
        int i4 = (int) f;
        double d = ceil;
        float f3 = i2;
        double ceil3 = Math.ceil(f3 * f2);
        Double.isNaN(d);
        cropState.cropPw = (float) (d / ceil3);
        MediaController.CropState cropState6 = mediaEditState.cropState;
        double d2 = ceil2;
        float f4 = i4;
        double ceil4 = Math.ceil(f2 * f4);
        Double.isNaN(d2);
        cropState6.cropPh = (float) (d2 / ceil4);
        MediaController.CropState cropState7 = mediaEditState.cropState;
        float f5 = cropState7.cropPw;
        if (f5 > 1.0f || cropState7.cropPh > 1.0f) {
            float max = Math.max(f5, cropState7.cropPh);
            MediaController.CropState cropState8 = mediaEditState.cropState;
            cropState8.cropPw /= max;
            cropState8.cropPh /= max;
        }
        mediaEditState.cropState.cropScale = this.state.scale * Math.min(f3 / this.areaView.getCropWidth(), f4 / this.areaView.getCropHeight());
        MediaController.CropState cropState9 = mediaEditState.cropState;
        float[] fArr = this.values;
        float f6 = fArr[2] / f3;
        CropState cropState10 = this.state;
        float f7 = cropState10.scale;
        cropState9.cropPx = f6 / f7;
        cropState9.cropPy = (fArr[5] / f4) / f7;
        cropState9.cropRotate = cropState10.rotation;
        cropState9.stateScale = f7;
        cropState9.mirrored = cropState10.mirrored;
        cropState9.scale = cropWidth;
        cropState9.matrix = cropState10.matrix;
        cropState9.width = ceil;
        cropState9.height = ceil2;
        cropState9.freeform = this.freeform;
        cropState9.lockedAspectRatio = this.areaView.getLockAspectRatio();
        mediaEditState.cropState.initied = true;
    }

    public void maximize(boolean z) {
        float currentWidth;
        int currentHeight;
        CropState cropState = this.state;
        if (cropState == null) {
            return;
        }
        final float f = cropState.minimumScale;
        this.areaView.resetAnimator();
        if (this.state.getOrientation() % 180.0f != 0.0f) {
            currentWidth = getCurrentHeight();
            currentHeight = getCurrentWidth();
        } else {
            currentWidth = getCurrentWidth();
            currentHeight = getCurrentHeight();
        }
        float f2 = currentWidth / currentHeight;
        if (!this.freeform) {
            f2 = 1.0f;
        }
        this.areaView.calculateRect(this.initialAreaRect, f2);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        resetRotationStartScale();
        if (!z) {
            this.areaView.setActualRect(this.initialAreaRect);
            CropState cropState2 = this.state;
            cropState2.translate(-cropState2.x, -cropState2.y);
            CropState cropState3 = this.state;
            cropState3.scale(cropState3.minimumScale / cropState3.scale, 0.0f, 0.0f);
            CropState cropState4 = this.state;
            cropState4.rotate(-cropState4.rotation, 0.0f, 0.0f);
            updateMatrix();
            resetRotationStartScale();
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        final RectF rectF = new RectF();
        final RectF rectF2 = new RectF();
        this.areaView.getCropRect(rectF);
        CropState cropState5 = this.state;
        final float f3 = cropState5.x;
        final float f4 = cropState5.y;
        final float f5 = cropState5.scale;
        final float f6 = cropState5.rotation;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.lambda$maximize$2(rectF, rectF2, f3, f4, f6, f5, f, valueAnimator);
            }
        });
        ofFloat.setInterpolator(this.areaView.getInterpolator());
        ofFloat.setDuration(250L);
        ofFloat.start();
    }

    public boolean mirror() {
        CropState cropState = this.state;
        boolean z = false;
        if (cropState == null) {
            return false;
        }
        cropState.mirror();
        updateMatrix();
        if (this.listener != null) {
            float orientation = (this.state.getOrientation() - this.state.getBaseRotation()) % 360.0f;
            CropViewListener cropViewListener = this.listener;
            if (!this.state.hasChanges() && orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored) {
                z = true;
            }
            cropViewListener.onChange(z);
        }
        return this.state.mirrored;
    }

    @Override
    public void onAreaChange() {
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
        float centerX = this.previousAreaRect.centerX() - this.areaView.getCropCenterX();
        float centerY = this.previousAreaRect.centerY() - this.areaView.getCropCenterY();
        CropState cropState = this.state;
        if (cropState != null) {
            cropState.translate(centerX, centerY);
        }
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    @Override
    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    @Override
    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    @Override
    public void onDrag(float f, float f2) {
        if (this.animating) {
            return;
        }
        this.state.translate(f, f2);
        updateMatrix();
    }

    @Override
    public void onFling(float f, float f2, float f3, float f4) {
    }

    public void onHide() {
        this.videoEditTextureView = null;
        this.paintingOverlay = null;
        this.isVisible = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public void onRotationBegan() {
        this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
        if (this.rotationStartScale < 1.0E-5f) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    }

    @Override
    public void onScale(float f, float f2, float f3) {
        if (this.animating) {
            return;
        }
        if (this.state.getScale() * f > 30.0f) {
            f = 30.0f / this.state.getScale();
        }
        this.state.scale(f, ((f2 - (this.imageView.getWidth() / 2)) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((f3 - (((this.imageView.getHeight() - this.bottomPadding) - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
        updateMatrix();
    }

    public void onScrollChangeBegan() {
        if (this.animating) {
            return;
        }
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onShow() {
        this.isVisible = true;
    }

    @Override
    public void onTapUp() {
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onTapUp();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animating || this.areaView.onTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            onScrollChangeBegan();
        } else if (action == 1 || action == 3) {
            onScrollChangeEnded();
        }
        try {
            return this.detector.onTouchEvent(motionEvent);
        } catch (Exception unused) {
            return false;
        }
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean z) {
        this.areaView.resetAnimator();
        CropAreaView cropAreaView = this.areaView;
        int currentWidth = getCurrentWidth();
        int currentHeight = getCurrentHeight();
        CropState cropState = this.state;
        cropAreaView.setBitmap(currentWidth, currentHeight, (cropState == null || cropState.getBaseRotation() % 180.0f == 0.0f) ? false : true, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        CropState cropState2 = this.state;
        if (cropState2 != null) {
            cropState2.reset(this.areaView, 0.0f, this.freeform);
            this.state.mirrored = false;
        }
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix(z);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public boolean rotate(float f) {
        if (this.state == null) {
            return false;
        }
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float orientation = ((this.state.getOrientation() - this.state.getBaseRotation()) + f) % 360.0f;
        boolean z = this.freeform;
        if (!z || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), (this.state.getBaseRotation() + orientation) % 180.0f != 0.0f, this.freeform);
        } else {
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
            CropAreaView cropAreaView2 = this.areaView;
            cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
            z = false;
        }
        this.state.reset(this.areaView, orientation, z);
        updateMatrix();
        fitContentInBounds(true, false, false);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored);
        }
        return this.state.getOrientationOnly() != 0;
    }

    public float scaleWidthToMaxSize(RectF rectF, RectF rectF2) {
        float width = rectF2.width();
        return ((float) Math.floor((double) ((rectF.height() * width) / rectF.width()))) > rectF2.height() ? (float) Math.floor((rectF2.height() * rectF.width()) / rectF.height()) : width;
    }

    public void setAspectRatio(float f) {
        this.areaView.setActualRect(f);
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z, boolean z2, PaintingOverlay paintingOverlay, CropTransform cropTransform, VideoEditTextureView videoEditTextureView, final MediaController.CropState cropState) {
        this.freeform = z;
        this.paintingOverlay = paintingOverlay;
        this.videoEditTextureView = videoEditTextureView;
        this.cropTransform = cropTransform;
        this.bitmapRotation = i;
        this.bitmap = bitmap;
        this.areaView.setIsVideo(videoEditTextureView != null);
        if (bitmap == null && videoEditTextureView == null) {
            this.state = null;
            this.imageView.setImageDrawable(null);
            return;
        }
        final int currentWidth = getCurrentWidth();
        final int currentHeight = getCurrentHeight();
        CropState cropState2 = this.state;
        if (cropState2 == null || !z2) {
            this.state = new CropState(currentWidth, currentHeight, 0);
            this.areaView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    float f;
                    float f2;
                    int i2;
                    int i3;
                    CropView.this.reset();
                    MediaController.CropState cropState3 = cropState;
                    if (cropState3 != null) {
                        float f3 = cropState3.lockedAspectRatio;
                        if (f3 > 1.0E-4f) {
                            CropView.this.areaView.setLockedAspectRatio(f3);
                            if (CropView.this.listener != null) {
                                CropView.this.listener.onAspectLock(true);
                            }
                        }
                        CropView.this.setFreeform(cropState.freeform);
                        float aspectRatio = CropView.this.areaView.getAspectRatio();
                        int i4 = cropState.transformRotation;
                        if (i4 == 90 || i4 == 270) {
                            aspectRatio = 1.0f / aspectRatio;
                            CropState cropState4 = CropView.this.state;
                            f = cropState4.height;
                            f2 = cropState4.width;
                            i2 = currentHeight;
                            i3 = currentWidth;
                        } else {
                            CropState cropState5 = CropView.this.state;
                            f = cropState5.width;
                            f2 = cropState5.height;
                            i2 = currentWidth;
                            i3 = currentHeight;
                        }
                        boolean z3 = CropView.this.freeform;
                        if (!CropView.this.freeform || CropView.this.areaView.getLockAspectRatio() <= 0.0f) {
                            CropView cropView = CropView.this;
                            cropView.areaView.setBitmap(cropView.getCurrentWidth(), CropView.this.getCurrentHeight(), (((float) i4) + CropView.this.state.getBaseRotation()) % 180.0f != 0.0f, CropView.this.freeform);
                        } else {
                            CropAreaView cropAreaView = CropView.this.areaView;
                            cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
                            CropAreaView cropAreaView2 = CropView.this.areaView;
                            cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
                            z3 = false;
                        }
                        CropView cropView2 = CropView.this;
                        cropView2.state.reset(cropView2.areaView, i4, z3);
                        CropAreaView cropAreaView3 = CropView.this.areaView;
                        MediaController.CropState cropState6 = cropState;
                        cropAreaView3.setActualRect((aspectRatio * cropState6.cropPw) / cropState6.cropPh);
                        CropState cropState7 = CropView.this.state;
                        MediaController.CropState cropState8 = cropState;
                        cropState7.mirrored = cropState8.mirrored;
                        cropState7.rotate(cropState8.cropRotate, 0.0f, 0.0f);
                        CropState cropState9 = CropView.this.state;
                        MediaController.CropState cropState10 = cropState;
                        float f4 = cropState10.cropPx * i2;
                        float f5 = cropState9.minimumScale;
                        cropState9.translate(f4 * f5, cropState10.cropPy * i3 * f5);
                        float max = Math.max(CropView.this.areaView.getCropWidth() / f, CropView.this.areaView.getCropHeight() / f2);
                        CropState cropState11 = CropView.this.state;
                        cropState11.scale(cropState.cropScale * (max / cropState11.minimumScale), 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                        if (CropView.this.listener != null) {
                            CropView.this.listener.onChange(false);
                        }
                    }
                    CropView.this.areaView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            cropState2.update(currentWidth, currentHeight, i);
        }
        this.imageView.setImageBitmap(videoEditTextureView == null ? this.bitmap : null);
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
        this.areaView.setBottomPadding(f);
    }

    public void setFreeform(boolean z) {
        this.areaView.setFreeform(z);
        this.freeform = z;
    }

    public void setListener(CropViewListener cropViewListener) {
        this.listener = cropViewListener;
    }

    @Override
    public void setRotation(float f) {
        this.state.rotate(f - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public void setSubtitle(String str) {
        this.areaView.setSubtitle(str);
    }

    public void show() {
        updateCropTransform();
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true, true);
        this.areaView.invalidate();
    }

    public void showAspectRatioDialog() {
        if (this.state == null || this.hasAspectRatioDialog) {
            return;
        }
        this.hasAspectRatioDialog = true;
        String[] strArr = new String[8];
        final Integer[][] numArr = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
        strArr[0] = LocaleController.getString(R.string.CropOriginal);
        strArr[1] = LocaleController.getString(R.string.CropSquare);
        int i = 2;
        for (int i2 = 0; i2 < 6; i2++) {
            Integer[] numArr2 = numArr[i2];
            if (this.areaView.getAspectRatio() > 1.0f) {
                strArr[i] = String.format("%d:%d", numArr2[0], numArr2[1]);
            } else {
                strArr[i] = String.format("%d:%d", numArr2[1], numArr2[0]);
            }
            i++;
        }
        AlertDialog create = new AlertDialog.Builder(getContext()).setItems(strArr, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                CropView.this.lambda$showAspectRatioDialog$3(numArr, dialogInterface, i3);
            }
        }).create();
        create.setCanceledOnTouchOutside(true);
        create.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                CropView.this.lambda$showAspectRatioDialog$4(dialogInterface);
            }
        });
        create.show();
    }

    public void updateLayout() {
        CropState cropState;
        float cropWidth = this.areaView.getCropWidth();
        if (cropWidth == 0.0f || (cropState = this.state) == null) {
            return;
        }
        this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
        CropAreaView cropAreaView = this.areaView;
        cropAreaView.setActualRect(cropAreaView.getAspectRatio());
        this.areaView.getCropRect(this.previousAreaRect);
        this.state.scale(this.areaView.getCropWidth() / cropWidth, 0.0f, 0.0f);
        updateMatrix();
    }

    public void updateMatrix() {
        updateMatrix(false);
    }

    public void updateMatrix(boolean z) {
        Matrix matrix;
        float f;
        float width;
        if (this.state == null) {
            return;
        }
        this.overlayMatrix.reset();
        if (this.state.getBaseRotation() == 90.0f || this.state.getBaseRotation() == 270.0f) {
            matrix = this.overlayMatrix;
            f = (-this.state.getHeight()) / 2.0f;
            width = this.state.getWidth();
        } else {
            matrix = this.overlayMatrix;
            f = (-this.state.getWidth()) / 2.0f;
            width = this.state.getHeight();
        }
        matrix.postTranslate(f, (-width) / 2.0f);
        this.overlayMatrix.postRotate(this.state.getOrientationOnly());
        this.state.getConcatMatrix(this.overlayMatrix);
        this.overlayMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        if (!this.freeform || this.isVisible || z) {
            updateCropTransform();
            this.listener.onUpdate();
        }
        invalidate();
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true, false);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }
}
