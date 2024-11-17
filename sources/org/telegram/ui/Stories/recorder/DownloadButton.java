package org.telegram.ui.Stories.recorder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.VideoEncodingService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Stories.recorder.DownloadButton;

public class DownloadButton extends ImageView {
    private BuildingVideo buildingVideo;
    private FrameLayout container;
    private int currentAccount;
    private StoryEntry currentEntry;
    private boolean downloading;
    private boolean downloadingVideo;
    private Utilities.Callback prepare;
    private boolean preparing;
    private CircularProgressDrawable progressDrawable;
    private Theme.ResourcesProvider resourcesProvider;
    private Uri savedToGalleryUri;
    private PreparingVideoToast toast;
    private boolean wasImageDownloading;
    private boolean wasVideoDownloading;

    public static class BuildingVideo implements NotificationCenter.NotificationCenterDelegate {
        final int currentAccount;
        final StoryEntry entry;
        final File file;
        private MessageObject messageObject;
        private final Runnable onCancel;
        private final Runnable onDone;
        private final Utilities.Callback onProgress;

        public BuildingVideo(int i, StoryEntry storyEntry, File file, Runnable runnable, Utilities.Callback callback, Runnable runnable2) {
            this.currentAccount = i;
            this.entry = storyEntry;
            this.file = file;
            this.onDone = runnable;
            this.onProgress = callback;
            this.onCancel = runnable2;
            start();
        }

        public void lambda$start$0(VideoEditedInfo videoEditedInfo) {
            MessageObject messageObject = this.messageObject;
            if (messageObject == null) {
                return;
            }
            messageObject.videoEditedInfo = videoEditedInfo;
            MediaController.getInstance().scheduleVideoConvert(this.messageObject);
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.filePreparingStarted) {
                return;
            }
            if (i != NotificationCenter.fileNewChunkAvailable) {
                if (i == NotificationCenter.filePreparingFailed && ((MessageObject) objArr[0]) == this.messageObject) {
                    stop(false);
                    try {
                        File file = this.file;
                        if (file != null) {
                            file.delete();
                        }
                    } catch (Exception unused) {
                    }
                    this.onCancel.run();
                    return;
                }
                return;
            }
            if (((MessageObject) objArr[0]) == this.messageObject) {
                ((Long) objArr[2]).longValue();
                long longValue = ((Long) objArr[3]).longValue();
                Float f = (Float) objArr[4];
                f.floatValue();
                Utilities.Callback callback = this.onProgress;
                if (callback != null) {
                    callback.run(f);
                }
                if (longValue > 0) {
                    this.onDone.run();
                    VideoEncodingService.stop();
                    stop(false);
                }
            }
        }

        public void start() {
            if (this.messageObject != null) {
                return;
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.id = 1;
            tL_message.attachPath = this.file.getAbsolutePath();
            this.messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) tL_message, (MessageObject) null, false, false);
            this.entry.getVideoEditedInfo(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DownloadButton.BuildingVideo.this.lambda$start$0((VideoEditedInfo) obj);
                }
            });
        }

        public void stop(boolean z) {
            if (this.messageObject == null) {
                return;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
            if (z) {
                MediaController.getInstance().cancelVideoConvert(this.messageObject);
            }
            this.messageObject = null;
        }
    }

    public static class PreparingVideoToast extends View {
        private final Paint backgroundPaint;
        private final ButtonBounce cancelButton;
        private final RectF currentRect;
        private boolean deleted;
        private final Paint dimPaint;
        private StaticLayout doneLayout;
        private float doneLayoutLeft;
        private float doneLayoutWidth;
        private final Paint greyPaint;
        private final RectF hiddenRect;
        private Runnable hideRunnable;
        private RLottieDrawable lottieDrawable;
        private Runnable onCancel;
        private final RectF prepareRect;
        private boolean preparing;
        private final StaticLayout preparingLayout;
        private final float preparingLayoutLeft;
        private final float preparingLayoutWidth;
        private float progress;
        private final AnimatedFloat progressT;
        private final AnimatedFloat showT;
        private boolean shown;
        private final AnimatedFloat t;
        private final TextPaint textPaint;
        private final TextPaint textPaint2;
        private final RectF toastRect;
        private final Paint whitePaint;

        public PreparingVideoToast(Context context) {
            this(context, LocaleController.getString(R.string.PreparingVideo));
        }

        public PreparingVideoToast(Context context, String str) {
            super(context);
            int i;
            float f;
            Paint paint = new Paint(1);
            this.dimPaint = paint;
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint2 = textPaint2;
            Paint paint2 = new Paint(1);
            this.backgroundPaint = paint2;
            Paint paint3 = new Paint(1);
            this.whitePaint = paint3;
            Paint paint4 = new Paint(1);
            this.greyPaint = paint4;
            this.cancelButton = new ButtonBounce(this);
            this.shown = false;
            this.showT = new AnimatedFloat(0.0f, this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.preparing = true;
            this.progress = 0.0f;
            this.t = new AnimatedFloat(this);
            this.progressT = new AnimatedFloat(this);
            this.prepareRect = new RectF();
            this.toastRect = new RectF();
            this.currentRect = new RectF();
            this.hiddenRect = new RectF();
            paint.setColor(1509949440);
            textPaint.setColor(-1);
            textPaint2.setColor(-1);
            paint2.setColor(-869783512);
            paint3.setColor(-1);
            paint4.setColor(872415231);
            Paint.Style style = Paint.Style.STROKE;
            paint3.setStyle(style);
            Paint.Cap cap = Paint.Cap.ROUND;
            paint3.setStrokeCap(cap);
            paint3.setStrokeWidth(AndroidUtilities.dp(4.0f));
            paint4.setStyle(style);
            paint4.setStrokeCap(cap);
            paint4.setStrokeWidth(AndroidUtilities.dp(4.0f));
            textPaint.setTextSize(AndroidUtilities.dp(14.0f));
            textPaint2.setTextSize(AndroidUtilities.dpf2(14.66f));
            StaticLayout staticLayout = new StaticLayout(str, textPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.preparingLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                i = 0;
                f = staticLayout.getLineWidth(0);
            } else {
                i = 0;
                f = 0.0f;
            }
            this.preparingLayoutWidth = f;
            this.preparingLayoutLeft = staticLayout.getLineCount() > 0 ? staticLayout.getLineLeft(i) : 0.0f;
            show();
        }

        private void drawPreparing(Canvas canvas, float f) {
            float f2 = this.progressT.set(this.progress);
            float centerX = this.prepareRect.centerX();
            float dp = this.prepareRect.top + AndroidUtilities.dp(48.0f);
            float dp2 = AndroidUtilities.dp(25.0f);
            this.greyPaint.setAlpha((int) (51.0f * f));
            canvas.drawCircle(centerX, dp, dp2, this.greyPaint);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(centerX - dp2, dp - dp2, centerX + dp2, dp2 + dp);
            int i = (int) (f * 255.0f);
            this.whitePaint.setAlpha(i);
            this.whitePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
            canvas.drawArc(rectF, -90.0f, f2 * 360.0f, false, this.whitePaint);
            float scale = this.cancelButton.getScale(0.15f);
            canvas.save();
            canvas.scale(scale, scale, centerX, dp);
            this.whitePaint.setStrokeWidth(AndroidUtilities.dp(3.4f));
            canvas.drawLine(centerX - AndroidUtilities.dp(7.0f), dp - AndroidUtilities.dp(7.0f), centerX + AndroidUtilities.dp(7.0f), dp + AndroidUtilities.dp(7.0f), this.whitePaint);
            canvas.drawLine(centerX - AndroidUtilities.dp(7.0f), dp + AndroidUtilities.dp(7.0f), centerX + AndroidUtilities.dp(7.0f), dp - AndroidUtilities.dp(7.0f), this.whitePaint);
            canvas.restore();
            canvas.save();
            canvas.translate((this.prepareRect.left + AndroidUtilities.dp(21.0f)) - this.preparingLayoutLeft, (this.prepareRect.bottom - AndroidUtilities.dp(18.0f)) - this.preparingLayout.getHeight());
            this.textPaint.setAlpha(i);
            this.preparingLayout.draw(canvas);
            canvas.restore();
        }

        private void drawToast(Canvas canvas, float f) {
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setAlpha((int) (f * 255.0f));
                this.lottieDrawable.setBounds((int) (this.toastRect.left + AndroidUtilities.dp(9.0f)), (int) (this.toastRect.top + AndroidUtilities.dp(6.0f)), (int) (this.toastRect.left + AndroidUtilities.dp(45.0f)), (int) (this.toastRect.top + AndroidUtilities.dp(42.0f)));
                this.lottieDrawable.draw(canvas);
            }
            if (this.doneLayout != null) {
                canvas.save();
                canvas.translate((this.toastRect.left + AndroidUtilities.dp(52.0f)) - this.doneLayoutLeft, this.toastRect.centerY() - (this.doneLayout.getHeight() / 2.0f));
                this.textPaint2.setAlpha((int) (f * 255.0f));
                this.doneLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void lambda$onDraw$0() {
            if (getParent() instanceof ViewGroup) {
                ((ViewGroup) getParent()).removeView(this);
            }
        }

        public void hide() {
            Runnable runnable = this.hideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.hideRunnable = null;
            }
            this.shown = false;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int saveCount = canvas.getSaveCount();
            float f = this.showT.set(this.shown ? 1.0f : 0.0f);
            float f2 = this.t.set(this.preparing ? 0.0f : 1.0f);
            float f3 = 1.0f - f2;
            this.dimPaint.setAlpha((int) (90.0f * f3 * f));
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.dimPaint);
            float max = Math.max(this.preparingLayoutWidth, AndroidUtilities.dp(54.0f)) + AndroidUtilities.dp(42.0f);
            float dp = AndroidUtilities.dp(111.0f) + this.preparingLayout.getHeight();
            this.prepareRect.set((getWidth() - max) / 2.0f, (getHeight() - dp) / 2.0f, (getWidth() + max) / 2.0f, (getHeight() + dp) / 2.0f);
            float dp2 = AndroidUtilities.dp(74.0f) + this.doneLayoutWidth;
            float dp3 = AndroidUtilities.dp(48.0f);
            this.toastRect.set((getWidth() - dp2) / 2.0f, (getHeight() - dp3) / 2.0f, (getWidth() + dp2) / 2.0f, (getHeight() + dp3) / 2.0f);
            AndroidUtilities.lerp(this.prepareRect, this.toastRect, f2, this.currentRect);
            if (f < 1.0f && this.preparing) {
                this.hiddenRect.set(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.0f, getHeight() / 2.0f);
                RectF rectF = this.hiddenRect;
                RectF rectF2 = this.currentRect;
                AndroidUtilities.lerp(rectF, rectF2, f, rectF2);
            }
            if (f < 1.0f && !this.preparing) {
                canvas.scale(AndroidUtilities.lerp(0.8f, 1.0f, f), AndroidUtilities.lerp(0.8f, 1.0f, f), this.currentRect.centerX(), this.currentRect.centerY());
            }
            this.backgroundPaint.setAlpha((int) (204.0f * f));
            canvas.drawRoundRect(this.currentRect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.backgroundPaint);
            canvas.save();
            canvas.clipRect(this.currentRect);
            if (f2 < 1.0f) {
                drawPreparing(canvas, f3 * f);
            }
            if (f2 > 0.0f) {
                drawToast(canvas, f2 * f);
            }
            canvas.restoreToCount(saveCount);
            if (f > 0.0f || this.shown || this.deleted) {
                return;
            }
            this.deleted = true;
            post(new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.PreparingVideoToast.this.lambda$onDraw$0();
                }
            });
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean contains = this.currentRect.contains(motionEvent.getX(), motionEvent.getY());
            if (motionEvent.getAction() == 0 && (this.preparing || contains)) {
                this.cancelButton.setPressed(contains);
                return true;
            }
            if (motionEvent.getAction() == 1) {
                if (this.cancelButton.isPressed()) {
                    if (contains) {
                        if (this.preparing) {
                            Runnable runnable = this.onCancel;
                            if (runnable != null) {
                                runnable.run();
                            }
                        } else {
                            hide();
                        }
                    }
                    this.cancelButton.setPressed(false);
                    return true;
                }
            } else if (motionEvent.getAction() == 3) {
                this.cancelButton.setPressed(false);
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setDone(int i, CharSequence charSequence, int i2) {
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCallback(null);
                this.lottieDrawable.recycle(true);
            }
            RLottieDrawable rLottieDrawable2 = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            this.lottieDrawable = rLottieDrawable2;
            rLottieDrawable2.setCallback(this);
            this.lottieDrawable.start();
            StaticLayout staticLayout = new StaticLayout(charSequence, this.textPaint2, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.doneLayout = staticLayout;
            this.doneLayoutWidth = staticLayout.getLineCount() > 0 ? this.doneLayout.getLineWidth(0) : 0.0f;
            this.doneLayoutLeft = this.doneLayout.getLineCount() > 0 ? this.doneLayout.getLineLeft(0) : 0.0f;
            this.preparing = false;
            invalidate();
            Runnable runnable = this.hideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.PreparingVideoToast.this.hide();
                }
            };
            this.hideRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, i2);
        }

        public void setOnCancelListener(Runnable runnable) {
            this.onCancel = runnable;
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        public void show() {
            this.shown = true;
            invalidate();
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return drawable == this.lottieDrawable || super.verifyDrawable(drawable);
        }
    }

    public DownloadButton(Context context, Utilities.Callback callback, int i, FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.wasImageDownloading = true;
        this.wasVideoDownloading = true;
        this.prepare = callback;
        this.currentAccount = i;
        this.container = frameLayout;
        this.resourcesProvider = resourcesProvider;
        setScaleType(ImageView.ScaleType.CENTER);
        setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        setBackground(Theme.createSelectorDrawable(553648127));
        setVisibility(8);
        setAlpha(0.0f);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                DownloadButton.this.lambda$new$0(view);
            }
        });
        this.progressDrawable = new CircularProgressDrawable(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(2.0f), -1);
        updateImage();
    }

    public void lambda$new$0(View view) {
        onClick();
    }

    public void lambda$onClickInternal$1() {
        this.preparing = false;
        BuildingVideo buildingVideo = this.buildingVideo;
        if (buildingVideo != null) {
            buildingVideo.stop(true);
            this.buildingVideo = null;
        }
        PreparingVideoToast preparingVideoToast = this.toast;
        if (preparingVideoToast != null) {
            preparingVideoToast.hide();
        }
        this.downloading = false;
        updateImage();
    }

    public void lambda$onClickInternal$2(Uri uri) {
        if (!this.downloading || this.currentEntry == null) {
            return;
        }
        this.toast.setDone(R.raw.ic_save_to_gallery, LocaleController.getString("VideoSavedHint"), 3500);
        this.downloading = false;
        updateImage();
        this.savedToGalleryUri = uri;
    }

    public void lambda$onClickInternal$3(File file) {
        if (!this.downloading || this.currentEntry == null) {
            return;
        }
        MediaController.saveFile(file.getAbsolutePath(), getContext(), 1, null, null, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DownloadButton.this.lambda$onClickInternal$2((Uri) obj);
            }
        }, false);
    }

    public void lambda$onClickInternal$4(Float f) {
        PreparingVideoToast preparingVideoToast = this.toast;
        if (preparingVideoToast != null) {
            preparingVideoToast.setProgress(f.floatValue());
        }
    }

    public void lambda$onClickInternal$5() {
        if (!this.downloading || this.currentEntry == null) {
            return;
        }
        this.toast.setDone(R.raw.error, LocaleController.getString("VideoConvertFail"), 3500);
        this.downloading = false;
        updateImage();
    }

    public void lambda$onClickInternal$6(Uri uri) {
        this.downloading = false;
        updateImage();
        PreparingVideoToast preparingVideoToast = this.toast;
        if (preparingVideoToast != null) {
            preparingVideoToast.hide();
            this.toast = null;
        }
        PreparingVideoToast preparingVideoToast2 = new PreparingVideoToast(getContext());
        this.toast = preparingVideoToast2;
        preparingVideoToast2.setDone(R.raw.ic_save_to_gallery, LocaleController.getString("PhotoSavedHint"), 2500);
        this.container.addView(this.toast);
        this.savedToGalleryUri = uri;
    }

    public void lambda$onClickInternal$7(File file) {
        MediaController.saveFile(file.getAbsolutePath(), getContext(), 0, null, null, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DownloadButton.this.lambda$onClickInternal$6((Uri) obj);
            }
        }, false);
    }

    public void lambda$onClickInternal$8(final File file) {
        this.currentEntry.buildPhoto(file);
        if (!this.downloading || this.currentEntry == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DownloadButton.this.lambda$onClickInternal$7(file);
            }
        });
    }

    private void onClick() {
        int checkSelfPermission;
        int i = Build.VERSION.SDK_INT;
        if (i >= 23 && (i <= 28 || BuildVars.NO_SCOPED_STORAGE)) {
            checkSelfPermission = getContext().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
            if (checkSelfPermission != 0) {
                Activity findActivity = AndroidUtilities.findActivity(getContext());
                if (findActivity != null) {
                    findActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 113);
                    return;
                }
                return;
            }
        }
        if (this.downloading || this.currentEntry == null) {
            return;
        }
        if (this.savedToGalleryUri != null) {
            if (i >= 30) {
                getContext().getContentResolver().delete(this.savedToGalleryUri, null);
            } else if (i < 29) {
                try {
                    new File(this.savedToGalleryUri.toString()).delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.savedToGalleryUri = null;
        }
        this.downloading = true;
        PreparingVideoToast preparingVideoToast = this.toast;
        if (preparingVideoToast != null) {
            preparingVideoToast.hide();
            this.toast = null;
        }
        BuildingVideo buildingVideo = this.buildingVideo;
        if (buildingVideo != null) {
            buildingVideo.stop(true);
            this.buildingVideo = null;
        }
        Utilities.Callback callback = this.prepare;
        if (callback != null) {
            this.preparing = true;
            callback.run(new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.this.onClickInternal();
                }
            });
        }
        updateImage();
        if (this.prepare == null) {
            onClickInternal();
        }
    }

    public void onClickInternal() {
        StoryEntry storyEntry;
        if (!this.preparing || (storyEntry = this.currentEntry) == null) {
            return;
        }
        this.preparing = false;
        if (storyEntry.wouldBeVideo()) {
            this.downloadingVideo = true;
            PreparingVideoToast preparingVideoToast = new PreparingVideoToast(getContext());
            this.toast = preparingVideoToast;
            preparingVideoToast.setOnCancelListener(new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.this.lambda$onClickInternal$1();
                }
            });
            this.container.addView(this.toast);
            final File generateVideoPath = AndroidUtilities.generateVideoPath();
            this.buildingVideo = new BuildingVideo(this.currentAccount, this.currentEntry, generateVideoPath, new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.this.lambda$onClickInternal$3(generateVideoPath);
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    DownloadButton.this.lambda$onClickInternal$4((Float) obj);
                }
            }, new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.this.lambda$onClickInternal$5();
                }
            });
        } else {
            this.downloadingVideo = false;
            final File generatePicturePath = AndroidUtilities.generatePicturePath(false, "png");
            if (generatePicturePath == null) {
                this.toast.setDone(R.raw.error, LocaleController.getString("UnknownError"), 3500);
                this.downloading = false;
                updateImage();
                return;
            }
            Utilities.themeQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    DownloadButton.this.lambda$onClickInternal$8(generatePicturePath);
                }
            });
        }
        updateImage();
    }

    private void updateImage() {
        boolean z = this.wasImageDownloading;
        boolean z2 = this.downloading;
        boolean z3 = false;
        if (z != (z2 && !this.downloadingVideo)) {
            boolean z4 = z2 && !this.downloadingVideo;
            this.wasImageDownloading = z4;
            if (z4) {
                AndroidUtilities.updateImageViewImageAnimated(this, this.progressDrawable);
            } else {
                AndroidUtilities.updateImageViewImageAnimated(this, R.drawable.media_download);
            }
        }
        if (this.wasVideoDownloading != (this.downloading && this.downloadingVideo)) {
            clearAnimation();
            ViewPropertyAnimator animate = animate();
            if (this.downloading && this.downloadingVideo) {
                z3 = true;
            }
            this.wasVideoDownloading = z3;
            animate.alpha(z3 ? 0.4f : 1.0f).start();
        }
    }

    public void setEntry(StoryEntry storyEntry) {
        this.savedToGalleryUri = null;
        this.currentEntry = storyEntry;
        BuildingVideo buildingVideo = this.buildingVideo;
        if (buildingVideo != null) {
            buildingVideo.stop(true);
            this.buildingVideo = null;
        }
        PreparingVideoToast preparingVideoToast = this.toast;
        if (preparingVideoToast != null) {
            preparingVideoToast.hide();
            this.toast = null;
        }
        if (storyEntry == null) {
            this.downloading = false;
            updateImage();
        }
    }

    public void showFailedVideo() {
        showToast(R.raw.error, LocaleController.getString("VideoConvertFail"));
    }

    public void showToast(int i, CharSequence charSequence) {
        PreparingVideoToast preparingVideoToast = this.toast;
        if (preparingVideoToast != null) {
            preparingVideoToast.hide();
            this.toast = null;
        }
        PreparingVideoToast preparingVideoToast2 = new PreparingVideoToast(getContext());
        this.toast = preparingVideoToast2;
        preparingVideoToast2.setDone(i, charSequence, 3500);
        this.container.addView(this.toast);
    }
}
