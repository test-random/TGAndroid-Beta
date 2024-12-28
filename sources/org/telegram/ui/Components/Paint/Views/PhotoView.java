package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Stories.recorder.StoryEntry;

public class PhotoView extends EntityView {
    private int anchor;
    public Size baseSize;
    public Bitmap bitmap;
    private final Paint bitmapPaint;
    public final FrameLayoutDrawer containerView;
    public MediaController.CropState crop;
    private final RectF dest;
    private LinearGradient highlightGradient;
    private Matrix highlightGradientMatrix;
    private Paint highlightPaint;
    private long highlightStart;
    private int invert;
    private final AnimatedFloat mirrorT;
    private boolean mirrored;
    private boolean needHighlight;
    private TLObject object;
    private int orientation;
    private boolean overridenSegmented;
    private String path;
    private Path roundRectPath;
    private final Paint segmentPaint;
    private boolean segmented;
    private File segmentedFile;
    public Bitmap segmentedImage;
    private AnimatedFloat segmentedT;
    private boolean segmentingLoaded;
    private boolean segmentingLoading;
    private final Rect src;

    public class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            PhotoView.this.stickerDraw(canvas);
        }
    }

    public class PhotoViewSelectionView extends EntityView.SelectionView {
        private final Paint clearPaint;
        private Path path;

        public PhotoViewSelectionView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.clearPaint = paint;
            this.path = new Path();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int saveCount = canvas.getSaveCount();
            float showAlpha = getShowAlpha();
            if (showAlpha <= 0.0f) {
                return;
            }
            if (showAlpha < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (showAlpha * 255.0f), 31);
            }
            float dp = AndroidUtilities.dp(2.0f);
            float dpf2 = AndroidUtilities.dpf2(5.66f);
            float dp2 = dp + dpf2 + AndroidUtilities.dp(15.0f);
            float f = dp2 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f;
            float measuredHeight = getMeasuredHeight() - f;
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = dp2 + measuredWidth;
            float f3 = dp2 + measuredHeight;
            rectF.set(dp2, dp2, f2, f3);
            float dp3 = AndroidUtilities.dp(12.0f);
            float min = Math.min(dp3, measuredWidth / 2.0f);
            float f4 = measuredHeight / 2.0f;
            float min2 = Math.min(dp3, f4);
            this.path.rewind();
            float f5 = min * 2.0f;
            float f6 = dp2 + f5;
            float f7 = 2.0f * min2;
            float f8 = dp2 + f7;
            rectF.set(dp2, dp2, f6, f8);
            this.path.arcTo(rectF, 180.0f, 90.0f);
            float f9 = f2 - f5;
            rectF.set(f9, dp2, f2, f8);
            this.path.arcTo(rectF, 270.0f, 90.0f);
            canvas.drawPath(this.path, this.paint);
            this.path.rewind();
            float f10 = f3 - f7;
            rectF.set(dp2, f10, f6, f3);
            this.path.arcTo(rectF, 180.0f, -90.0f);
            rectF.set(f9, f10, f2, f3);
            this.path.arcTo(rectF, 90.0f, -90.0f);
            canvas.drawPath(this.path, this.paint);
            float f11 = dp2 + f4;
            canvas.drawCircle(dp2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(dp2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.drawCircle(f2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(f2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
            float f12 = dp2 + min2;
            float f13 = f3 - min2;
            canvas.drawLine(dp2, f12, dp2, f13, this.paint);
            canvas.drawLine(f2, f12, f2, f13, this.paint);
            canvas.drawCircle(f2, f11, (AndroidUtilities.dp(1.0f) + dpf2) - 1.0f, this.clearPaint);
            canvas.drawCircle(dp2, f11, (dpf2 + AndroidUtilities.dp(1.0f)) - 1.0f, this.clearPaint);
            canvas.restoreToCount(saveCount);
        }

        @Override
        protected int pointInsideHandle(float f, float f2) {
            float dp = AndroidUtilities.dp(1.0f);
            float dp2 = AndroidUtilities.dp(19.5f);
            float f3 = dp + dp2;
            float f4 = f3 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f4;
            float measuredHeight = getMeasuredHeight() - f4;
            float f5 = (measuredHeight / 2.0f) + f3;
            if (f > f3 - dp2 && f2 > f5 - dp2 && f < f3 + dp2 && f2 < f5 + dp2) {
                return 1;
            }
            float f6 = f3 + measuredWidth;
            if (f <= f6 - dp2 || f2 <= f5 - dp2 || f >= f6 + dp2 || f2 >= f5 + dp2) {
                return (f <= f3 || f >= measuredWidth || f2 <= f3 || f2 >= measuredHeight) ? 0 : 3;
            }
            return 2;
        }
    }

    public PhotoView(Context context, Point point, float f, float f2, Size size, String str, int i, int i2) {
        super(context, point);
        this.anchor = -1;
        this.mirrored = false;
        this.overridenSegmented = false;
        this.segmented = false;
        this.src = new Rect();
        this.dest = new RectF();
        this.segmentPaint = new Paint(3);
        this.highlightStart = -1L;
        this.bitmapPaint = new Paint(3);
        setRotation(f);
        setScale(f2);
        this.path = str;
        this.baseSize = size;
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(context);
        this.containerView = frameLayoutDrawer;
        addView(frameLayoutDrawer, LayoutHelper.createFrame(-1, -1.0f));
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.mirrorT = new AnimatedFloat(frameLayoutDrawer, 0L, 500L, cubicBezierInterpolator);
        this.segmentedT = new AnimatedFloat(frameLayoutDrawer, 0L, 350L, cubicBezierInterpolator);
        this.orientation = i;
        this.invert = i2;
        Bitmap decodeFile = BitmapFactory.decodeFile(str);
        this.bitmap = decodeFile;
        if (decodeFile != null) {
            lambda$segmentImage$1(decodeFile);
        }
        updatePosition();
    }

    public PhotoView(Context context, Point point, float f, float f2, Size size, TLObject tLObject) {
        super(context, point);
        this.anchor = -1;
        this.mirrored = false;
        this.overridenSegmented = false;
        this.segmented = false;
        this.src = new Rect();
        this.dest = new RectF();
        this.segmentPaint = new Paint(3);
        this.highlightStart = -1L;
        this.bitmapPaint = new Paint(3);
        setRotation(f);
        setScale(f2);
        this.object = tLObject;
        this.baseSize = size;
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(context);
        this.containerView = frameLayoutDrawer;
        addView(frameLayoutDrawer, LayoutHelper.createFrame(-1, -1.0f));
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.mirrorT = new AnimatedFloat(frameLayoutDrawer, 0L, 500L, cubicBezierInterpolator);
        this.segmentedT = new AnimatedFloat(frameLayoutDrawer, 0L, 350L, cubicBezierInterpolator);
        updatePosition();
    }

    private void drawSegmented(Canvas canvas) {
        Bitmap bitmap = this.segmentedImage;
        if (bitmap == null) {
            return;
        }
        this.src.set(0, 0, bitmap.getWidth(), this.segmentedImage.getHeight());
        int width = this.segmentedImage.getWidth();
        int height = this.segmentedImage.getHeight();
        int i = this.orientation;
        if (i == 90 || i == 270 || i == -90 || i == -270) {
            width = this.segmentedImage.getHeight();
            height = this.segmentedImage.getWidth();
        }
        Size size = this.baseSize;
        float max = Math.max(width / size.width, height / size.height);
        float width2 = this.segmentedImage.getWidth() / max;
        float height2 = this.segmentedImage.getHeight() / max;
        RectF rectF = this.dest;
        Size size2 = this.baseSize;
        float f = size2.width;
        float f2 = size2.height;
        rectF.set((f - width2) / 2.0f, (f2 - height2) / 2.0f, (f + width2) / 2.0f, (f2 + height2) / 2.0f);
        canvas.save();
        int i2 = this.orientation;
        if (i2 != 0) {
            canvas.rotate(i2, this.dest.centerX(), this.dest.centerY());
        }
        if (this.roundRectPath == null) {
            this.roundRectPath = new Path();
        }
        this.roundRectPath.rewind();
        this.roundRectPath.addRoundRect(this.dest, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
        canvas.clipPath(this.roundRectPath);
        canvas.drawBitmap(this.segmentedImage, this.src, this.dest, this.segmentPaint);
        canvas.restore();
    }

    private String getImageFilter() {
        android.graphics.Point point = AndroidUtilities.displaySize;
        int round = Math.round((Math.min(point.x, point.y) * 0.8f) / AndroidUtilities.density);
        return round + "_" + round;
    }

    public static boolean isWaitingMlKitError(Exception exc) {
        return Build.VERSION.SDK_INT >= 24 && (exc instanceof MlKitException) && exc.getMessage() != null && exc.getMessage().contains("segmentation optional module to be downloaded");
    }

    public void lambda$segmentImage$0(SubjectSegmentationResult subjectSegmentationResult) {
        this.segmentingLoaded = true;
        this.segmentingLoading = false;
    }

    public void lambda$segmentImage$2(final Bitmap bitmap, Exception exc) {
        this.segmentingLoading = false;
        FileLog.e(exc);
        if (isWaitingMlKitError(exc) && isAttachedToWindow()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PhotoView.this.lambda$segmentImage$1(bitmap);
                }
            }, 2000L);
        } else {
            this.segmentingLoaded = true;
        }
    }

    @Override
    protected EntityView.SelectionView createSelectionView() {
        return new PhotoViewSelectionView(getContext());
    }

    public void deleteSegmentedFile() {
        File file = this.segmentedFile;
        if (file != null) {
            try {
                file.delete();
            } catch (Exception unused) {
            }
            this.segmentedFile = null;
        }
    }

    public void drawContent(Canvas canvas) {
        if (this.bitmap == null) {
            return;
        }
        this.bitmapPaint.setAlpha(255);
        canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, this.bitmapPaint);
    }

    public int getAnchor() {
        return this.anchor;
    }

    public Size getBaseSize() {
        return this.baseSize;
    }

    public int getContentHeight() {
        Bitmap bitmap = this.bitmap;
        if (bitmap == null) {
            return 1;
        }
        return bitmap.getHeight();
    }

    public int getContentWidth() {
        Bitmap bitmap = this.bitmap;
        if (bitmap == null) {
            return 1;
        }
        return bitmap.getWidth();
    }

    public int getOrientation() {
        return this.orientation;
    }

    public String getPath(int i) {
        TLObject tLObject = this.object;
        if (tLObject instanceof TLRPC.Photo) {
            try {
                return FileLoader.getInstance(i).getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) tLObject).sizes, 1000), true).getAbsolutePath();
            } catch (Exception unused) {
            }
        }
        return this.path;
    }

    public Bitmap getSegmentedOutBitmap() {
        Bitmap bitmap;
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 == null || (bitmap = this.segmentedImage) == null) {
            return null;
        }
        int width = bitmap2.getWidth();
        int height = bitmap2.getHeight();
        if ((this.orientation / 90) % 2 == 1) {
            width = bitmap2.getHeight();
            height = bitmap2.getWidth();
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.roundRectPath.rewind();
        RectF rectF = AndroidUtilities.rectTmp;
        float f = width;
        float f2 = height;
        rectF.set(0.0f, 0.0f, f, f2);
        float f3 = this.mirrorT.get();
        float f4 = f / 2.0f;
        canvas.scale(1.0f - (f3 * 2.0f), 1.0f, f4, 0.0f);
        canvas.skew(0.0f, 4.0f * f3 * (1.0f - f3) * 0.25f);
        this.roundRectPath.addRoundRect(rectF, AndroidUtilities.dp(12.0f) * getScaleX(), AndroidUtilities.dp(12.0f) * getScaleY(), Path.Direction.CW);
        canvas.clipPath(this.roundRectPath);
        canvas.translate(f4, f2 / 2.0f);
        canvas.rotate(this.orientation);
        canvas.translate((-bitmap2.getWidth()) / 2.0f, (-bitmap2.getHeight()) / 2.0f);
        rectF.set(0.0f, 0.0f, bitmap2.getWidth(), bitmap2.getHeight());
        canvas.saveLayerAlpha(rectF, 255, 31);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, (Paint) null);
        Paint paint = new Paint(3);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.save();
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        canvas.restore();
        canvas.restore();
        return createBitmap;
    }

    @Override
    public org.telegram.ui.Components.Rect getSelectionBounds() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup == null) {
            return new org.telegram.ui.Components.Rect();
        }
        float scaleX = viewGroup.getScaleX();
        float measuredWidth = (getMeasuredWidth() * getScale()) + (AndroidUtilities.dp(64.0f) / scaleX);
        float measuredHeight = (getMeasuredHeight() * getScale()) + (AndroidUtilities.dp(64.0f) / scaleX);
        float measuredWidth2 = (getMeasuredWidth() * getScale()) + (AndroidUtilities.dp(64.0f) / scaleX);
        getMeasuredHeight();
        getScale();
        AndroidUtilities.dp(64.0f);
        float positionX = (getPositionX() - (measuredWidth / 2.0f)) * scaleX;
        return new org.telegram.ui.Components.Rect(positionX, (getPositionY() - (measuredHeight / 2.0f)) * scaleX, ((measuredWidth2 * scaleX) + positionX) - positionX, measuredHeight * scaleX);
    }

    public boolean hasSegmentedImage() {
        return this.segmentedImage != null;
    }

    public void highlightSegmented() {
        this.needHighlight = true;
        if (this.highlightStart <= 0 || System.currentTimeMillis() - this.highlightStart >= 1000) {
            this.highlightStart = System.currentTimeMillis();
        }
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            frameLayoutDrawer.invalidate();
        }
    }

    public boolean isMirrored() {
        return this.mirrored;
    }

    public boolean isSegmented() {
        return this.segmented;
    }

    public void mirror() {
        mirror(false);
    }

    public void mirror(boolean z) {
        boolean z2 = !this.mirrored;
        this.mirrored = z2;
        if (!z) {
            this.mirrorT.set(z2, true);
        }
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            frameLayoutDrawer.invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        Size size = this.baseSize;
        float f = size.width;
        float f2 = size.height;
        MediaController.CropState cropState = this.crop;
        if (cropState != null) {
            f *= cropState.cropPw;
            f2 *= cropState.cropPh;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) f, 1073741824), View.MeasureSpec.makeMeasureSpec((int) f2, 1073741824));
    }

    public void onSwitchSegmentedAnimationStarted(boolean z) {
        this.overridenSegmented = true;
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            frameLayoutDrawer.invalidate();
        }
    }

    public void preloadSegmented(String str) {
        this.segmentingLoading = false;
    }

    public File saveSegmentedImage(int i) {
        if (this.segmentedImage == null) {
            return null;
        }
        if (this.segmentedFile == null) {
            this.segmentedFile = StoryEntry.makeCacheFile(i, "webp");
            try {
                this.segmentedImage.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(this.segmentedFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this.segmentedFile;
    }

    public void lambda$segmentImage$1(final Bitmap bitmap) {
        if (this.segmentingLoaded || this.segmentingLoading || bitmap == null || Build.VERSION.SDK_INT < 24) {
            return;
        }
        SubjectSegmenter client = SubjectSegmentation.getClient(new SubjectSegmenterOptions.Builder().enableForegroundBitmap().build());
        this.segmentingLoading = true;
        client.process(InputImage.fromBitmap(bitmap, this.orientation)).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public final void onSuccess(Object obj) {
                PhotoView.this.lambda$segmentImage$0((SubjectSegmentationResult) obj);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public final void onFailure(Exception exc) {
                PhotoView.this.lambda$segmentImage$2(bitmap, exc);
            }
        });
    }

    protected void stickerDraw(Canvas canvas) {
        if (this.containerView == null) {
            return;
        }
        canvas.save();
        float f = this.mirrorT.set(this.mirrored);
        canvas.scale(1.0f - (f * 2.0f), 1.0f, this.baseSize.width / 2.0f, 0.0f);
        canvas.skew(0.0f, 4.0f * f * (1.0f - f) * 0.25f);
        float f2 = this.segmentedT.set(this.segmented);
        if (this.segmented) {
            this.highlightStart = -1L;
            this.needHighlight = false;
            drawSegmented(canvas);
        } else {
            canvas.save();
            this.bitmapPaint.setAlpha((int) ((1.0f - f2) * 255.0f));
            if (this.bitmap != null) {
                canvas.translate(this.containerView.getWidth() / 2.0f, this.containerView.getHeight() / 2.0f);
                canvas.rotate(this.orientation);
                float max = Math.max(this.baseSize.width / this.bitmap.getWidth(), this.baseSize.height / this.bitmap.getHeight());
                canvas.scale(max, max);
                if (this.crop != null) {
                    canvas.rotate(-getOrientation());
                    int contentWidth = getContentWidth();
                    int contentHeight = getContentHeight();
                    if (((getOrientation() + this.crop.transformRotation) / 90) % 2 == 1) {
                        contentWidth = getContentHeight();
                        contentHeight = getContentWidth();
                    }
                    MediaController.CropState cropState = this.crop;
                    float f3 = cropState.cropPw;
                    float f4 = cropState.cropPh;
                    float f5 = contentWidth;
                    float f6 = contentHeight;
                    canvas.clipRect(((-contentWidth) * f3) / 2.0f, ((-contentHeight) * f4) / 2.0f, (f3 * f5) / 2.0f, (f4 * f6) / 2.0f);
                    float f7 = this.crop.cropScale;
                    canvas.scale(f7, f7);
                    MediaController.CropState cropState2 = this.crop;
                    canvas.translate(cropState2.cropPx * f5, cropState2.cropPy * f6);
                    canvas.rotate(this.crop.cropRotate + r2.transformRotation);
                    if (this.crop.mirrored) {
                        canvas.scale(-1.0f, 1.0f);
                    }
                    canvas.rotate(getOrientation());
                }
                canvas.translate((-this.bitmap.getWidth()) / 2.0f, (-this.bitmap.getHeight()) / 2.0f);
                canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, this.bitmapPaint);
            }
            canvas.restore();
            if (f2 > 0.0f) {
                drawSegmented(canvas);
            }
            if (this.segmentedImage != null) {
                Size size = this.baseSize;
                canvas.saveLayerAlpha(0.0f, 0.0f, size.width, size.height, 255, 31);
                drawSegmented(canvas);
                canvas.save();
                long currentTimeMillis = System.currentTimeMillis();
                if (this.highlightStart <= 0) {
                    this.highlightStart = currentTimeMillis;
                }
                float f8 = this.baseSize.width;
                float f9 = f8 * 0.8f;
                float f10 = ((float) (currentTimeMillis - this.highlightStart)) / 1000.0f;
                float f11 = (((2.0f * f9) + f8) * f10) - f9;
                if (this.highlightPaint == null) {
                    Paint paint = new Paint(1);
                    this.highlightPaint = paint;
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    this.highlightGradient = new LinearGradient(0.0f, 0.0f, f9, 0.0f, new int[]{16707212, 1727983244, 1727983244, 16707212}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
                    Matrix matrix = new Matrix();
                    this.highlightGradientMatrix = matrix;
                    this.highlightGradient.setLocalMatrix(matrix);
                    this.highlightPaint.setShader(this.highlightGradient);
                }
                this.highlightGradientMatrix.reset();
                this.highlightGradientMatrix.postTranslate(f11, 0.0f);
                this.highlightGradient.setLocalMatrix(this.highlightGradientMatrix);
                Size size2 = this.baseSize;
                canvas.drawRect(0.0f, 0.0f, (int) size2.width, (int) size2.height, this.highlightPaint);
                canvas.restore();
                canvas.restore();
                if ((f10 > 0.0f || this.needHighlight) && f10 < 1.0f) {
                    this.needHighlight = false;
                    this.containerView.invalidate();
                }
            }
        }
        canvas.restore();
    }

    public void toggleSegmented(boolean z) {
        boolean z2 = !this.segmented;
        this.segmented = z2;
        if (z && z2) {
            this.overridenSegmented = false;
        }
        if (!z) {
            this.segmentedT.set(z2, true);
        }
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            frameLayoutDrawer.invalidate();
        }
    }

    @Override
    public void updatePosition() {
        Size size = this.baseSize;
        float f = size.width / 2.0f;
        float f2 = size.height / 2.0f;
        MediaController.CropState cropState = this.crop;
        if (cropState != null) {
            f *= cropState.cropPw;
            f2 *= cropState.cropPh;
        }
        setX(getPositionX() - f);
        setY(getPositionY() - f2);
        updateSelectionView();
    }
}
