package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.net.URLEncoder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.video.VideoPlayerHolderBase;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Premium.HelloParticles;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.PremiumPreviewFragment;

public class VideoScreenPreview extends FrameLayout implements PagerHeaderView, NotificationCenter.NotificationCenterDelegate {
    private static final float[] speedScaleVideoTimestamps = {0.02f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.02f};
    boolean allowPlay;
    float aspectRatio;
    AspectRatioFrameLayout aspectRatioFrameLayout;
    String attachFileName;
    boolean attached;
    CellFlickerDrawable.DrawableInterface cellFlickerDrawable;
    int currentAccount;
    private TLRPC.Document document;
    File file;
    boolean firstFrameRendered;
    boolean fromTop;
    HelloParticles.Drawable helloParticlesDrawable;
    ImageReceiver imageReceiver;
    long lastFrameTime;
    private MatrixParticlesDrawable matrixParticlesDrawable;
    Runnable nextCheck;
    Paint phoneFrame1;
    Paint phoneFrame2;
    boolean play;
    float progress;
    private float roundRadius;
    RoundedBitmapDrawable roundedBitmapDrawable;
    int size;
    SpeedLineParticles$Drawable speedLinesDrawable;
    StarParticlesView.Drawable starDrawable;
    private final SvgHelper.SvgDrawable svgIcon;
    TextureView textureView;
    int type;
    VideoPlayerHolderBase videoPlayerBase;
    boolean visible;

    public VideoScreenPreview(Context context, SvgHelper.SvgDrawable svgDrawable, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        int i3;
        this.phoneFrame1 = new Paint(1);
        this.phoneFrame2 = new Paint(1);
        this.fromTop = false;
        this.imageReceiver = new ImageReceiver(this);
        this.currentAccount = i;
        this.type = i2;
        this.svgIcon = svgDrawable;
        this.phoneFrame1.setColor(-16777216);
        this.phoneFrame2.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_premiumGradient2, resourcesProvider), -16777216, 0.5f));
        this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        setVideo();
        if (i2 == 1) {
            MatrixParticlesDrawable matrixParticlesDrawable = new MatrixParticlesDrawable();
            this.matrixParticlesDrawable = matrixParticlesDrawable;
            matrixParticlesDrawable.init();
        } else if (i2 == 6 || i2 == 9 || i2 == 3 || i2 == 7 || i2 == 11 || i2 == 4 || i2 == 24) {
            StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(40);
            this.starDrawable = drawable;
            drawable.speedScale = 3.0f;
            drawable.type = i2;
            if (i2 == 3 || i2 == 24) {
                drawable.size1 = 14;
                i3 = 18;
                drawable.size2 = 18;
            } else {
                drawable.size1 = 14;
                drawable.size2 = 16;
                i3 = 15;
            }
            drawable.size3 = i3;
            drawable.k3 = 0.98f;
            drawable.k2 = 0.98f;
            drawable.k1 = 0.98f;
            drawable.speedScale = 4.0f;
            drawable.resourcesProvider = resourcesProvider;
            drawable.colorKey = Theme.key_premiumStartSmallStarsColor2;
            drawable.init();
        } else if (i2 == 2) {
            SpeedLineParticles$Drawable speedLineParticles$Drawable = new SpeedLineParticles$Drawable(200);
            this.speedLinesDrawable = speedLineParticles$Drawable;
            speedLineParticles$Drawable.init();
        } else if (i2 == 13) {
            HelloParticles.Drawable drawable2 = new HelloParticles.Drawable(25);
            this.helloParticlesDrawable = drawable2;
            drawable2.init();
        } else {
            StarParticlesView.Drawable drawable3 = new StarParticlesView.Drawable(SharedConfig.getDevicePerformanceClass() == 2 ? 800 : SharedConfig.getDevicePerformanceClass() == 1 ? 400 : 100);
            this.starDrawable = drawable3;
            drawable3.resourcesProvider = resourcesProvider;
            drawable3.colorKey = Theme.key_premiumStartSmallStarsColor2;
            drawable3.size1 = 4;
            drawable3.k3 = 0.98f;
            drawable3.k2 = 0.98f;
            drawable3.k1 = 0.98f;
            drawable3.useRotate = true;
            drawable3.speedScale = 4.0f;
            drawable3.checkBounds = true;
            drawable3.checkTime = true;
            drawable3.useBlur = true;
            drawable3.roundEffect = false;
            drawable3.init();
        }
        if (i2 == 1 || i2 == 3 || i2 == 11) {
            this.fromTop = true;
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(context) {
            Path clipPath = new Path();

            @Override
            protected void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(this.clipPath);
                super.dispatchDraw(canvas);
                canvas.restore();
            }

            @Override
            public void onMeasure(int i4, int i5) {
                super.onMeasure(i4, i5);
                this.clipPath.reset();
                VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                if (videoScreenPreview.fromTop) {
                    AndroidUtilities.rectTmp.set(0.0f, -videoScreenPreview.roundRadius, getMeasuredWidth(), getMeasuredHeight());
                } else {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), (int) (getMeasuredHeight() + VideoScreenPreview.this.roundRadius));
                }
                float dp = VideoScreenPreview.this.roundRadius - AndroidUtilities.dp(3.0f);
                this.clipPath.addRoundRect(AndroidUtilities.rectTmp, dp, dp, Path.Direction.CW);
            }
        };
        this.aspectRatioFrameLayout = aspectRatioFrameLayout;
        aspectRatioFrameLayout.setResizeMode(0);
        TextureView textureView = new TextureView(context);
        this.textureView = textureView;
        this.aspectRatioFrameLayout.addView(textureView);
        setWillNotDraw(false);
        addView(this.aspectRatioFrameLayout);
    }

    public void checkVideo() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.VideoScreenPreview.checkVideo():void");
    }

    public void lambda$setVideo$0(File file) {
        this.file = file;
        checkVideo();
    }

    public void lambda$setVideo$1(TLRPC.Document document) {
        final File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(document);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoScreenPreview.this.lambda$setVideo$0(pathToAttach);
            }
        });
    }

    public static void lambda$stopVideoPlayer$2() {
    }

    private void runVideoPlayer() {
        Uri uri;
        if ((this.file != null || SharedConfig.streamMedia) && this.videoPlayerBase == null) {
            this.aspectRatioFrameLayout.setAspectRatio(this.aspectRatio, 0);
            VideoPlayerHolderBase videoPlayerHolderBase = new VideoPlayerHolderBase() {
                @Override
                public void onRenderedFirstFrame() {
                    VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                    TextureView textureView = videoScreenPreview.textureView;
                    if (textureView == null || videoScreenPreview.firstFrameRendered) {
                        return;
                    }
                    textureView.setAlpha(0.0f);
                    VideoScreenPreview.this.textureView.animate().alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            VideoScreenPreview videoScreenPreview2 = VideoScreenPreview.this;
                            videoScreenPreview2.firstFrameRendered = true;
                            videoScreenPreview2.invalidate();
                        }
                    }).setDuration(200L);
                }

                @Override
                public void onStateChanged(boolean z, int i) {
                    VideoPlayerHolderBase videoPlayerHolderBase2 = VideoScreenPreview.this.videoPlayerBase;
                    if (videoPlayerHolderBase2 == null) {
                        return;
                    }
                    if (i == 4) {
                        videoPlayerHolderBase2.seekTo(0L);
                        videoPlayerHolderBase2 = VideoScreenPreview.this.videoPlayerBase;
                    } else if (i != 1) {
                        return;
                    }
                    videoPlayerHolderBase2.play();
                }
            };
            this.videoPlayerBase = videoPlayerHolderBase;
            videoPlayerHolderBase.with(this.textureView);
            File file = this.file;
            if (file == null || !file.exists()) {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("?account=");
                    sb.append(this.currentAccount);
                    sb.append("&id=");
                    sb.append(this.document.id);
                    sb.append("&hash=");
                    sb.append(this.document.access_hash);
                    sb.append("&dc=");
                    sb.append(this.document.dc_id);
                    sb.append("&size=");
                    sb.append(this.document.size);
                    sb.append("&mime=");
                    sb.append(URLEncoder.encode(this.document.mime_type, "UTF-8"));
                    sb.append("&rid=");
                    sb.append(FileLoader.getInstance(this.currentAccount).getFileReference(MediaDataController.getInstance(this.currentAccount).getPremiumPromo()));
                    sb.append("&name=");
                    sb.append(URLEncoder.encode(FileLoader.getDocumentFileName(this.document), "UTF-8"));
                    sb.append("&reference=");
                    byte[] bArr = this.document.file_reference;
                    if (bArr == null) {
                        bArr = new byte[0];
                    }
                    sb.append(Utilities.bytesToHex(bArr));
                    uri = Uri.parse("tg://" + this.attachFileName + sb.toString());
                } catch (Exception unused) {
                    uri = null;
                }
            } else {
                uri = Uri.fromFile(this.file);
            }
            if (uri == null) {
                return;
            }
            this.videoPlayerBase.preparePlayer(uri, false, 1.0f);
            if (!this.firstFrameRendered) {
                this.imageReceiver.stopAnimation();
                this.textureView.setAlpha(0.0f);
            }
            this.videoPlayerBase.seekTo(this.lastFrameTime + 60);
            this.videoPlayerBase.play();
        }
    }

    private void setVideo() {
        TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(this.currentAccount).getPremiumPromo();
        String featureTypeToServerString = PremiumPreviewFragment.featureTypeToServerString(this.type);
        if (premiumPromo != null) {
            int i = 0;
            while (true) {
                if (i >= premiumPromo.video_sections.size()) {
                    i = -1;
                    break;
                } else if (premiumPromo.video_sections.get(i).equals(featureTypeToServerString)) {
                    break;
                } else {
                    i++;
                }
            }
            if (i >= 0) {
                final TLRPC.Document document = premiumPromo.videos.get(i);
                CombinedDrawable combinedDrawable = null;
                for (int i2 = 0; i2 < document.thumbs.size(); i2++) {
                    if (document.thumbs.get(i2) instanceof TLRPC.TL_photoStrippedSize) {
                        this.roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), ImageLoader.getStrippedPhotoBitmap(document.thumbs.get(i2).bytes, "b"));
                        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
                        cellFlickerDrawable.repeatProgress = 4.0f;
                        cellFlickerDrawable.progress = 3.5f;
                        cellFlickerDrawable.frameInside = true;
                        this.cellFlickerDrawable = cellFlickerDrawable.getDrawableInterface(this, this.svgIcon);
                        combinedDrawable = new CombinedDrawable(this.roundedBitmapDrawable, this.cellFlickerDrawable) {
                            @Override
                            public void setBounds(int i3, int i4, int i5, int i6) {
                                VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                                if (videoScreenPreview.fromTop) {
                                    i4 = (int) (i4 - videoScreenPreview.roundRadius);
                                } else {
                                    i6 = (int) (i6 + videoScreenPreview.roundRadius);
                                }
                                super.setBounds(i3, i4, i5, i6);
                            }
                        };
                        combinedDrawable.setFullsize(true);
                    }
                }
                this.attachFileName = FileLoader.getAttachFileName(document);
                this.imageReceiver.setImage(null, null, combinedDrawable, null, premiumPromo, 1);
                FileLoader.getInstance(this.currentAccount).loadFile(document, premiumPromo, 3, 0);
                this.document = document;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        VideoScreenPreview.this.lambda$setVideo$1(document);
                    }
                });
            }
        }
    }

    private void stopVideoPlayer() {
        VideoPlayerHolderBase videoPlayerHolderBase = this.videoPlayerBase;
        if (videoPlayerHolderBase != null) {
            this.lastFrameTime = videoPlayerHolderBase.getCurrentPosition();
            this.videoPlayerBase.release(new Runnable() {
                @Override
                public final void run() {
                    VideoScreenPreview.lambda$stopVideoPlayer$2();
                }
            });
            this.videoPlayerBase = null;
        }
    }

    private void updateAttachState() {
        boolean z = this.visible && this.attached;
        if (this.play != z) {
            this.play = z;
            if (z) {
                this.imageReceiver.onAttachedToWindow();
            } else {
                this.imageReceiver.onDetachedFromWindow();
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoaded) {
            String str = (String) objArr[0];
            String str2 = this.attachFileName;
            if (str2 == null || !str2.equals(str)) {
                return;
            }
            this.file = (File) objArr[1];
            checkVideo();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float f;
        if (this.starDrawable != null || this.speedLinesDrawable != null || this.helloParticlesDrawable != null || this.matrixParticlesDrawable != null) {
            if (this.progress < 0.5f) {
                float pow = (float) Math.pow(1.0f - r0, 2.0d);
                canvas.save();
                canvas.scale(pow, pow, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
                MatrixParticlesDrawable matrixParticlesDrawable = this.matrixParticlesDrawable;
                if (matrixParticlesDrawable != null) {
                    matrixParticlesDrawable.onDraw(canvas);
                } else {
                    StarParticlesView.Drawable drawable = this.starDrawable;
                    if (drawable != null) {
                        drawable.onDraw(canvas);
                    } else if (this.speedLinesDrawable != null) {
                        VideoPlayerHolderBase videoPlayerHolderBase = this.videoPlayerBase;
                        if (videoPlayerHolderBase != null) {
                            float clamp = Utilities.clamp(((float) videoPlayerHolderBase.getCurrentPosition()) / ((float) this.videoPlayerBase.getDuration()), 1.0f, 0.0f);
                            float[] fArr = speedScaleVideoTimestamps;
                            float length = 1.0f / (fArr.length - 1);
                            int i = (int) (clamp / length);
                            int i2 = i + 1;
                            float f2 = (clamp - (i * length)) / length;
                            f = i2 < fArr.length ? (fArr[i] * (1.0f - f2)) + (fArr[i2] * f2) : fArr[i];
                        } else {
                            f = 0.2f;
                        }
                        float clamp2 = ((1.0f - Utilities.clamp(this.progress / 0.1f, 1.0f, 0.0f)) * 0.9f) + 0.1f;
                        SpeedLineParticles$Drawable speedLineParticles$Drawable = this.speedLinesDrawable;
                        speedLineParticles$Drawable.speedScale = clamp2 * 150.0f * f;
                        speedLineParticles$Drawable.onDraw(canvas);
                    } else {
                        HelloParticles.Drawable drawable2 = this.helloParticlesDrawable;
                        if (drawable2 != null) {
                            drawable2.onDraw(canvas);
                        }
                    }
                }
                canvas.restore();
                invalidate();
            }
        }
        float measuredHeight = (int) (getMeasuredHeight() * 0.9f);
        float measuredWidth = (getMeasuredWidth() - (0.671f * measuredHeight)) / 2.0f;
        float f3 = 0.0671f * measuredHeight;
        this.roundRadius = f3;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(measuredWidth, -f3, getMeasuredWidth() - measuredWidth, measuredHeight);
        } else {
            AndroidUtilities.rectTmp.set(measuredWidth, getMeasuredHeight() - measuredHeight, getMeasuredWidth() - measuredWidth, getMeasuredHeight() + this.roundRadius);
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.inset(-AndroidUtilities.dp(3.0f), -AndroidUtilities.dp(3.0f));
        rectF.inset(-AndroidUtilities.dp(3.0f), -AndroidUtilities.dp(3.0f));
        canvas.drawRoundRect(rectF, this.roundRadius + AndroidUtilities.dp(3.0f), this.roundRadius + AndroidUtilities.dp(3.0f), this.phoneFrame2);
        rectF.inset(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        float f4 = this.roundRadius;
        canvas.drawRoundRect(rectF, f4, f4, this.phoneFrame1);
        if (this.fromTop) {
            rectF.set(measuredWidth, 0.0f, getMeasuredWidth() - measuredWidth, measuredHeight);
        } else {
            rectF.set(measuredWidth, getMeasuredHeight() - measuredHeight, getMeasuredWidth() - measuredWidth, getMeasuredHeight());
        }
        float dp = this.roundRadius - AndroidUtilities.dp(3.0f);
        this.roundRadius = dp;
        RoundedBitmapDrawable roundedBitmapDrawable = this.roundedBitmapDrawable;
        if (roundedBitmapDrawable != null) {
            roundedBitmapDrawable.setCornerRadius(dp);
        }
        CellFlickerDrawable.DrawableInterface drawableInterface = this.cellFlickerDrawable;
        if (drawableInterface != null) {
            drawableInterface.radius = this.roundRadius;
        }
        if (this.fromTop) {
            ImageReceiver imageReceiver = this.imageReceiver;
            int i3 = (int) this.roundRadius;
            imageReceiver.setRoundRadius(0, 0, i3, i3);
        } else {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            int i4 = (int) this.roundRadius;
            imageReceiver2.setRoundRadius(i4, i4, 0, 0);
        }
        if (!this.firstFrameRendered) {
            this.imageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
            this.imageReceiver.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if (this.fromTop) {
            return;
        }
        canvas.drawCircle(this.imageReceiver.getCenterX(), this.imageReceiver.getImageY() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f), this.phoneFrame1);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        updateAttachState();
        if (!this.firstFrameRendered) {
            checkVideo();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        updateAttachState();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        HelloParticles.Drawable drawable = this.helloParticlesDrawable;
        if (drawable != null) {
            drawable.recycle();
            this.helloParticlesDrawable = null;
        }
        stopVideoPlayer();
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int measuredWidth = getMeasuredWidth() << (getMeasuredHeight() + 16);
        float measuredHeight = (int) (getMeasuredHeight() * 0.9f);
        float measuredWidth2 = (getMeasuredWidth() - (0.671f * measuredHeight)) / 2.0f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(measuredWidth2, -this.roundRadius, getMeasuredWidth() - measuredWidth2, measuredHeight);
        } else {
            AndroidUtilities.rectTmp.set(measuredWidth2, getMeasuredHeight() - measuredHeight, getMeasuredWidth() - measuredWidth2, getMeasuredHeight() + this.roundRadius);
        }
        if (this.size != measuredWidth) {
            this.size = measuredWidth;
            MatrixParticlesDrawable matrixParticlesDrawable = this.matrixParticlesDrawable;
            if (matrixParticlesDrawable != null) {
                matrixParticlesDrawable.drawingRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.matrixParticlesDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.matrixParticlesDrawable.excludeRect.inset(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            }
            StarParticlesView.Drawable drawable = this.starDrawable;
            if (drawable != null) {
                int i5 = this.type;
                if (i5 == 6 || i5 == 9 || i5 == 3 || i5 == 7 || i5 == 24 || i5 == 11 || i5 == 4) {
                    drawable.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    this.starDrawable.rect.inset(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
                } else {
                    RectF rectF = AndroidUtilities.rectTmp;
                    float width = (int) (rectF.width() * 0.4f);
                    this.starDrawable.rect.set(rectF.centerX() - width, rectF.centerY() - width, rectF.centerX() + width, rectF.centerY() + width);
                    this.starDrawable.rect2.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                }
                this.starDrawable.resetPositions();
                this.starDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.starDrawable.excludeRect.inset(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            }
            SpeedLineParticles$Drawable speedLineParticles$Drawable = this.speedLinesDrawable;
            if (speedLineParticles$Drawable != null) {
                speedLineParticles$Drawable.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.speedLinesDrawable.screenRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.speedLinesDrawable.rect.inset(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
                this.speedLinesDrawable.rect.offset(0.0f, getMeasuredHeight() * 0.1f);
                this.speedLinesDrawable.resetPositions();
            }
            HelloParticles.Drawable drawable2 = this.helloParticlesDrawable;
            if (drawable2 != null) {
                drawable2.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.helloParticlesDrawable.screenRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.helloParticlesDrawable.rect.inset(AndroidUtilities.dp(0.0f), getMeasuredHeight() * 0.1f);
                this.helloParticlesDrawable.resetPositions();
            }
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        float size3 = (int) (View.MeasureSpec.getSize(i2) * 0.9f);
        float f = size;
        float f2 = (f - (0.671f * size3)) / 2.0f;
        this.roundRadius = 0.0671f * size3;
        if (Build.VERSION.SDK_INT >= 21) {
            this.aspectRatioFrameLayout.invalidateOutline();
        }
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(f2, 0.0f, f - f2, size3);
        } else {
            float f3 = size2;
            AndroidUtilities.rectTmp.set(f2, f3 - size3, f - f2, f3);
        }
        ViewGroup.LayoutParams layoutParams = this.aspectRatioFrameLayout.getLayoutParams();
        RectF rectF = AndroidUtilities.rectTmp;
        layoutParams.width = (int) rectF.width();
        this.aspectRatioFrameLayout.getLayoutParams().height = (int) rectF.height();
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).leftMargin = (int) rectF.left;
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).topMargin = (int) rectF.top;
        super.onMeasure(i, i2);
    }

    @Override
    public void setOffset(float f) {
        boolean z;
        float f2 = -f;
        if (f < 0.0f) {
            float measuredWidth = f2 / getMeasuredWidth();
            setAlpha((Utilities.clamp(1.0f - measuredWidth, 1.0f, 0.0f) * 0.5f) + 0.5f);
            setRotationY(50.0f * measuredWidth);
            invalidate();
            setTranslationY((this.fromTop ? -getMeasuredHeight() : getMeasuredHeight()) * 0.3f * measuredWidth);
            this.progress = Math.abs(measuredWidth);
            z = measuredWidth < 1.0f;
            if (measuredWidth < 0.1f) {
                r1 = true;
            }
        } else {
            float measuredWidth2 = f2 / getMeasuredWidth();
            invalidate();
            setRotationY(50.0f * measuredWidth2);
            setTranslationY((this.fromTop ? getMeasuredHeight() : -getMeasuredHeight()) * 0.3f * measuredWidth2);
            z = measuredWidth2 > -1.0f;
            r1 = measuredWidth2 > -0.1f;
            this.progress = Math.abs(measuredWidth2);
        }
        if (z != this.visible) {
            this.visible = z;
            updateAttachState();
        }
        if (r1 != this.allowPlay) {
            this.allowPlay = r1;
            this.imageReceiver.setAllowStartAnimation(r1);
            if (this.allowPlay) {
                this.imageReceiver.startAnimation();
                runVideoPlayer();
            } else {
                stopVideoPlayer();
                this.imageReceiver.stopAnimation();
            }
        }
    }
}
