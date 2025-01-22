package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.VideoPlayer;

public abstract class VideoSeekPreviewImage extends View {
    private Paint bitmapPaint;
    private RectF bitmapRect;
    private BitmapShader bitmapShader;
    private Bitmap bitmapToDraw;
    private Bitmap bitmapToRecycle;
    private int currentPixel;
    private VideoSeekPreviewImageDelegate delegate;
    private RectF dstR;
    private long duration;
    private AnimatedFileDrawable fileDrawable;
    private Drawable frameDrawable;
    private String frameTime;
    private boolean isQualities;
    private boolean isYoutube;
    private int lastYoutubePosition;
    private Runnable loadRunnable;
    private Matrix matrix;
    private boolean open;
    private Paint paint;
    private float pendingProgress;
    private int pixelWidth;
    private Runnable progressRunnable;
    private boolean ready;
    private final TextPaint textPaint;
    private int timeWidth;
    private Uri videoUri;
    private PhotoViewerWebView webView;
    private ImageReceiver youtubeBoardsReceiver;
    private int ytImageHeight;
    private int ytImageWidth;
    private int ytImageX;
    private int ytImageY;
    private final Path ytPath;

    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        this.currentPixel = -1;
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        this.dstR = new RectF();
        this.paint = new Paint(2);
        this.bitmapPaint = new Paint(2);
        this.bitmapRect = new RectF();
        this.matrix = new Matrix();
        this.ytPath = new Path();
        setVisibility(4);
        this.frameDrawable = context.getResources().getDrawable(R.drawable.videopreview);
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        textPaint.setColor(-1);
        this.delegate = videoSeekPreviewImageDelegate;
        ImageReceiver imageReceiver = new ImageReceiver();
        this.youtubeBoardsReceiver = imageReceiver;
        imageReceiver.setParentView(this);
        this.youtubeBoardsReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                VideoSeekPreviewImage.this.lambda$new$0(imageReceiver2, z, z2, z3);
            }

            @Override
            public void didSetImageBitmap(int i, String str, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i, str, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
    }

    public void lambda$close$7() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }

    public void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        int i;
        if (!z || this.webView == null) {
            return;
        }
        int dp = AndroidUtilities.dp(150.0f);
        int youtubeStoryboardImageCount = this.webView.getYoutubeStoryboardImageCount(this.lastYoutubePosition);
        float bitmapWidth = this.youtubeBoardsReceiver.getBitmapWidth() / Math.min(youtubeStoryboardImageCount, 5);
        float bitmapHeight = this.youtubeBoardsReceiver.getBitmapHeight() / ((int) Math.ceil(youtubeStoryboardImageCount / 5.0f));
        int min = Math.min(this.webView.getYoutubeStoryboardImageIndex(this.lastYoutubePosition), youtubeStoryboardImageCount - 1);
        this.ytImageX = (int) ((min % 5) * bitmapWidth);
        this.ytImageY = (int) ((min / 5) * bitmapHeight);
        this.ytImageWidth = (int) bitmapWidth;
        this.ytImageHeight = (int) bitmapHeight;
        float f = bitmapWidth / bitmapHeight;
        if (f > 1.0f) {
            i = (int) (dp / f);
        } else {
            i = dp;
            dp = (int) (dp * f);
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (getVisibility() == 0 && layoutParams.width == dp && layoutParams.height == i) {
            return;
        }
        layoutParams.width = dp;
        layoutParams.height = i;
        setVisibility(0);
        requestLayout();
    }

    public void lambda$open$3() {
        this.open = true;
        this.loadRunnable = null;
        if (this.fileDrawable != null) {
            this.ready = true;
            this.delegate.onReady();
        }
    }

    public void lambda$open$4(VideoPlayer.VideoUri videoUri) {
        Object obj;
        File pathToAttach;
        if (videoUri.isCached()) {
            this.fileDrawable = new AnimatedFileDrawable(new File(videoUri.uri.getPath()), true, 0L, 0, null, null, null, 0L, 0, true, null);
        } else {
            int i = UserConfig.selectedAccount;
            try {
                i = Utilities.parseInt((CharSequence) videoUri.uri.getQueryParameter("account")).intValue();
            } catch (Exception e) {
                FileLog.e(e);
            }
            int i2 = i;
            try {
                obj = FileLoader.getInstance(i2).getParentObject(Utilities.parseInt((CharSequence) videoUri.uri.getQueryParameter("rid")).intValue());
            } catch (Exception e2) {
                FileLog.e(e2);
                obj = null;
            }
            Object obj2 = obj;
            TLRPC.Document document = videoUri.document;
            if (FileLoader.getInstance(i2).isLoadingFile(FileLoader.getAttachFileName(document))) {
                pathToAttach = new File(FileLoader.getDirectory(4), document.dc_id + "_" + document.id + ".temp");
            } else {
                pathToAttach = FileLoader.getInstance(i2).getPathToAttach(document, false);
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(pathToAttach.getAbsolutePath()), true, document.size, 1, document, null, obj2, 0L, i2, true, null);
        }
        this.duration = this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$open$3();
            }
        });
    }

    public void lambda$open$5() {
        this.open = true;
        this.loadRunnable = null;
        if (this.fileDrawable != null) {
            this.ready = true;
            this.delegate.onReady();
        }
    }

    public void lambda$open$6(Uri uri) {
        File pathToAttach;
        if ("tg".equals(uri.getScheme())) {
            int intValue = Utilities.parseInt((CharSequence) uri.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt((CharSequence) uri.getQueryParameter("rid")).intValue());
            TLRPC.TL_document tL_document = new TLRPC.TL_document();
            tL_document.access_hash = Utilities.parseLong(uri.getQueryParameter("hash")).longValue();
            tL_document.id = Utilities.parseLong(uri.getQueryParameter("id")).longValue();
            tL_document.size = Utilities.parseLong(uri.getQueryParameter("size")).longValue();
            tL_document.dc_id = Utilities.parseInt((CharSequence) uri.getQueryParameter("dc")).intValue();
            tL_document.mime_type = uri.getQueryParameter("mime");
            tL_document.file_reference = Utilities.hexToBytes(uri.getQueryParameter("reference"));
            TLRPC.TL_documentAttributeFilename tL_documentAttributeFilename = new TLRPC.TL_documentAttributeFilename();
            tL_documentAttributeFilename.file_name = uri.getQueryParameter("name");
            tL_document.attributes.add(tL_documentAttributeFilename);
            tL_document.attributes.add(new TLRPC.TL_documentAttributeVideo());
            if (FileLoader.getInstance(intValue).isLoadingFile(FileLoader.getAttachFileName(tL_document))) {
                pathToAttach = new File(FileLoader.getDirectory(4), tL_document.dc_id + "_" + tL_document.id + ".temp");
            } else {
                pathToAttach = FileLoader.getInstance(intValue).getPathToAttach(tL_document, false);
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(pathToAttach.getAbsolutePath()), true, tL_document.size, 1, tL_document, null, parentObject, 0L, intValue, true, null);
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0L, 0, null, null, null, 0L, 0, true, null);
        }
        this.duration = this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$open$5();
            }
        });
    }

    public void lambda$setProgress$1(Bitmap bitmap) {
        int i;
        if (bitmap != null) {
            if (this.bitmapToDraw != null) {
                Bitmap bitmap2 = this.bitmapToRecycle;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.bitmapToRecycle = this.bitmapToDraw;
            }
            this.bitmapToDraw = bitmap;
            Bitmap bitmap3 = this.bitmapToDraw;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader = new BitmapShader(bitmap3, tileMode, tileMode);
            this.bitmapShader = bitmapShader;
            bitmapShader.setLocalMatrix(this.matrix);
            this.bitmapPaint.setShader(this.bitmapShader);
            invalidate();
            int dp = AndroidUtilities.dp(150.0f);
            float width = bitmap.getWidth() / bitmap.getHeight();
            if (width > 1.0f) {
                i = (int) (dp / width);
            } else {
                dp = (int) (dp * width);
                i = dp;
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (getVisibility() != 0 || layoutParams.width != dp || layoutParams.height != i) {
                layoutParams.width = dp;
                layoutParams.height = i;
                setVisibility(0);
                requestLayout();
            }
        }
        this.progressRunnable = null;
    }

    public void lambda$setProgress$2(float f, long j) {
        int i;
        if (this.fileDrawable == null) {
            this.pendingProgress = f;
            return;
        }
        int max = Math.max(200, AndroidUtilities.dp(100.0f));
        final Bitmap frameAtTime = this.fileDrawable.getFrameAtTime(j, false);
        if (frameAtTime != null) {
            int width = frameAtTime.getWidth();
            int height = frameAtTime.getHeight();
            if (width > height) {
                i = (int) (height / (width / max));
            } else {
                int i2 = (int) (width / (height / max));
                i = max;
                max = i2;
            }
            try {
                Bitmap createBitmap = Bitmaps.createBitmap(max, i, Bitmap.Config.ARGB_8888);
                this.dstR.set(0.0f, 0.0f, max, i);
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawBitmap(frameAtTime, (android.graphics.Rect) null, this.dstR, this.paint);
                canvas.setBitmap(null);
                frameAtTime = createBitmap;
            } catch (Throwable unused) {
                frameAtTime = null;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$1(frameAtTime);
            }
        });
    }

    public void close() {
        if (this.loadRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.loadRunnable);
            this.loadRunnable = null;
        }
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
            this.progressRunnable = null;
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$close$7();
            }
        });
        setVisibility(4);
        this.bitmapToDraw = null;
        this.bitmapShader = null;
        invalidate();
        this.currentPixel = -1;
        this.videoUri = null;
        this.ready = false;
        this.open = false;
    }

    public boolean isReady() {
        return this.ready;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.youtubeBoardsReceiver.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.youtubeBoardsReceiver.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmapToRecycle;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmapToRecycle = null;
        }
        if (this.bitmapToDraw != null && this.bitmapShader != null) {
            this.matrix.reset();
            float measuredWidth = getMeasuredWidth() / this.bitmapToDraw.getWidth();
            this.matrix.preScale(measuredWidth, measuredWidth);
            this.bitmapRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRoundRect(this.bitmapRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.bitmapPaint);
        } else {
            if (!this.isYoutube) {
                return;
            }
            canvas.save();
            this.ytPath.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.ytPath.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
            canvas.clipPath(this.ytPath);
            canvas.scale(getWidth() / this.ytImageWidth, getHeight() / this.ytImageHeight);
            canvas.translate(-this.ytImageX, -this.ytImageY);
            this.youtubeBoardsReceiver.setImageCoords(0.0f, 0.0f, r0.getBitmapWidth(), this.youtubeBoardsReceiver.getBitmapHeight());
            this.youtubeBoardsReceiver.draw(canvas);
            canvas.restore();
        }
        this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.frameDrawable.draw(canvas);
        canvas.drawText(this.frameTime, (getMeasuredWidth() - this.timeWidth) / 2.0f, getMeasuredHeight() - AndroidUtilities.dp(9.0f), this.textPaint);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setPivotY(getMeasuredHeight());
    }

    public void open(final Uri uri) {
        if (uri == null || uri.equals(this.videoUri)) {
            return;
        }
        if (this.open) {
            close();
        }
        this.isQualities = false;
        this.videoUri = uri;
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$open$6(uri);
            }
        };
        this.loadRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public void open(final VideoPlayer.VideoUri videoUri) {
        if (videoUri == null || videoUri.uri.equals(this.videoUri)) {
            return;
        }
        if (this.open) {
            close();
        }
        this.isQualities = true;
        this.videoUri = videoUri.uri;
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$open$4(videoUri);
            }
        };
        this.loadRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public void open(VideoPlayer videoPlayer) {
        VideoPlayer.Quality currentQuality;
        if (videoPlayer == null) {
            return;
        }
        if (videoPlayer.getQualitiesCount() <= 0) {
            open(videoPlayer.getCurrentUri());
            return;
        }
        VideoPlayer.VideoUri videoUri = null;
        for (int i = 0; i < videoPlayer.getQualitiesCount(); i++) {
            Iterator it = videoPlayer.getQuality(i).uris.iterator();
            while (it.hasNext()) {
                VideoPlayer.VideoUri videoUri2 = (VideoPlayer.VideoUri) it.next();
                if (videoUri == null || ((!videoUri.isCached() && videoUri2.isCached()) || (videoUri.isCached() == videoUri2.isCached() && videoUri2.width * videoUri2.height < videoUri.width * videoUri.height))) {
                    videoUri = videoUri2;
                }
            }
        }
        if (videoUri != null && !videoUri.isCached() && (currentQuality = videoPlayer.getCurrentQuality()) != null) {
            videoUri = currentQuality.getDownloadUri();
        }
        if (videoUri == null || videoUri.isCached()) {
            open(videoUri);
        } else {
            close();
        }
    }

    public void setProgress(final float f, int i) {
        this.webView = null;
        this.isYoutube = false;
        this.youtubeBoardsReceiver.setImageBitmap((Drawable) null);
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (i * f)) / 5;
            if (this.currentPixel == i2) {
                return;
            } else {
                this.currentPixel = i2;
            }
        }
        final long j = ((float) this.duration) * f;
        this.frameTime = AndroidUtilities.formatShortDuration((int) (j / 1000));
        this.timeWidth = (int) Math.ceil(this.textPaint.measureText(r8));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$2(f, j);
            }
        };
        this.progressRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public void setProgressForYouTube(PhotoViewerWebView photoViewerWebView, float f, int i) {
        this.webView = photoViewerWebView;
        this.isYoutube = true;
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (i * f)) / 5;
            if (this.currentPixel == i2) {
                return;
            } else {
                this.currentPixel = i2;
            }
        }
        this.frameTime = AndroidUtilities.formatShortDuration((int) ((photoViewerWebView.getVideoDuration() * f) / 1000));
        this.timeWidth = (int) Math.ceil(this.textPaint.measureText(r10));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        int videoDuration = (int) ((f * photoViewerWebView.getVideoDuration()) / 1000.0f);
        this.lastYoutubePosition = videoDuration;
        String youtubeStoryboard = photoViewerWebView.getYoutubeStoryboard(videoDuration);
        if (youtubeStoryboard != null) {
            this.youtubeBoardsReceiver.setImage(youtubeStoryboard, null, null, null, 0L);
        }
    }
}
