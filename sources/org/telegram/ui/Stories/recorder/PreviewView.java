package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.zxing.common.detector.MathUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Paint.Views.RoundView;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Stories.recorder.PreviewView;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.TimelineView;

public abstract class PreviewView extends FrameLayout {
    private boolean allowCropping;
    private boolean allowRotation;
    private boolean allowWithSingleTouch;
    private float angle;
    private VideoPlayer audioPlayer;
    private Bitmap bitmap;
    private final Paint bitmapPaint;
    private final BlurringShader.BlurManager blurManager;
    private CollageLayoutView2 collage;
    private float cx;
    private float cy;
    private boolean doNotSpanRotation;
    private boolean draw;
    public boolean drawForThemeToggle;
    private StoryEntry entry;
    public TextureView filterTextureView;
    private Matrix finalMatrix;
    private long finalSeekPosition;
    private int gradientBottom;
    private final Paint gradientPaint;
    private int gradientTop;
    private float h;
    public Runnable invalidateBlur;
    public boolean isMuted;
    private long lastPos;
    private final PointF lastTouch;
    private float lastTouchDistance;
    private double lastTouchRotation;
    private Drawable lastWallpaperDrawable;
    private final Matrix matrix;
    private boolean moving;
    private boolean multitouch;
    private Runnable onErrorListener;
    private Runnable onTap;
    private final HashSet pauseLinks;
    private PhotoFilterView photoFilterView;
    private float rotationDiff;
    private VideoPlayer roundPlayer;
    private int roundPlayerHeight;
    private int roundPlayerWidth;
    private RoundView roundView;
    private long seekedLastTime;
    private Runnable slowerSeek;
    private boolean slowerSeekScheduled;
    private final Paint snapPaint;
    private boolean snappedRotation;
    private boolean snappedToCenterAndScaled;
    private long tapTime;
    private VideoEditTextureView textureView;
    private final TextureViewHolder textureViewHolder;
    private final AnimatedFloat thumbAlpha;
    private Bitmap thumbBitmap;
    private TimelineView timelineView;
    private final PointF touch;
    private Matrix touchMatrix;
    private final Runnable updateAudioProgressRunnable;
    private final Runnable updateProgressRunnable;
    private final Runnable updateRoundProgressRunnable;
    private final float[] vertices;
    private int videoHeight;
    private VideoPlayer videoPlayer;
    private int videoWidth;
    private float w;
    private Drawable wallpaperDrawable;
    private AnimatedFloat wallpaperDrawableCrossfade;

    public class AnonymousClass1 implements VideoPlayer.VideoPlayerDelegate {
        AnonymousClass1() {
        }

        @Override
        public void onError(VideoPlayer videoPlayer, Exception exc) {
        }

        @Override
        public void onRenderedFirstFrame() {
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override
        public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override
        public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        @Override
        public void onStateChanged(boolean z, int i) {
            AndroidUtilities.cancelRunOnUIThread(PreviewView.this.updateAudioProgressRunnable);
            if (PreviewView.this.audioPlayer == null || !PreviewView.this.audioPlayer.isPlaying()) {
                return;
            }
            AndroidUtilities.runOnUIThread(PreviewView.this.updateAudioProgressRunnable);
        }

        @Override
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            PreviewView.this.invalidateTextureViewHolder();
        }

        @Override
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        }
    }

    public class AnonymousClass2 implements TimelineView.TimelineDelegate {
        AnonymousClass2() {
        }

        @Override
        public void onAudioLeftChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.audioLeft = f;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.updateAudioPlayer(true);
        }

        @Override
        public void onAudioOffsetChange(long j) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.audioOffset = j;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.updateAudioPlayer(true);
        }

        @Override
        public void onAudioRemove() {
            PreviewView.this.setupAudio((MessageObject) null, true);
        }

        @Override
        public void onAudioRightChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.audioRight = f;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.updateAudioPlayer(true);
        }

        @Override
        public void onAudioVolumeChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.audioVolume = f;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.checkVolumes();
        }

        @Override
        public void onProgressChange(long j, boolean z) {
            VideoPlayer videoPlayer;
            if (!z) {
                PreviewView.this.seekTo(j);
                return;
            }
            boolean z2 = true;
            if (PreviewView.this.videoPlayer != null) {
                videoPlayer = PreviewView.this.videoPlayer;
            } else if (PreviewView.this.isCollage()) {
                PreviewView.this.collage.seekTo(j, true);
                return;
            } else {
                if (PreviewView.this.audioPlayer == null) {
                    return;
                }
                videoPlayer = PreviewView.this.audioPlayer;
                z2 = false;
            }
            videoPlayer.seekTo(j, z2);
        }

        @Override
        public void onProgressDragChange(boolean z) {
            if (PreviewView.this.isCollage()) {
                PreviewView.this.collage.forceNotRestorePosition();
            }
            PreviewView.this.updatePauseReason(-4, z);
        }

        @Override
        public void onRoundLeftChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.roundLeft = f;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.updateRoundPlayer(true);
        }

        @Override
        public void onRoundOffsetChange(long j) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.roundOffset = j;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.updateRoundPlayer(true);
        }

        @Override
        public void onRoundRemove() {
            PreviewView.this.setupRound(null, null, true);
            PreviewView.this.onRoundRemove();
        }

        @Override
        public void onRoundRightChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.roundRight = f;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.updateRoundPlayer(true);
        }

        @Override
        public void onRoundSelectChange(boolean z) {
            PreviewView.this.onRoundSelectChange(z);
        }

        @Override
        public void onRoundVolumeChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.roundVolume = f;
            PreviewView.this.entry.editedMedia = true;
            PreviewView.this.checkVolumes();
        }

        @Override
        public void onVideoLeftChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.left = f;
            PreviewView.this.entry.editedMedia = true;
            if (PreviewView.this.videoPlayer == null || PreviewView.this.videoPlayer.getDuration() == -9223372036854775807L) {
                return;
            }
            PreviewView.this.seekTo(f * ((float) r0.videoPlayer.getDuration()));
        }

        @Override
        public void onVideoLeftChange(int i, float f) {
            if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                return;
            }
            ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoLeft = f;
        }

        @Override
        public void onVideoOffsetChange(int i, long j) {
            if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                return;
            }
            ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoOffset = j;
        }

        @Override
        public void onVideoRightChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.right = f;
            PreviewView.this.entry.editedMedia = true;
        }

        @Override
        public void onVideoRightChange(int i, float f) {
            if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                return;
            }
            ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoRight = f;
        }

        @Override
        public void onVideoSelected(int i) {
            if (PreviewView.this.collage != null) {
                PreviewView.this.collage.highlight(i);
            }
        }

        @Override
        public void onVideoVolumeChange(float f) {
            if (PreviewView.this.entry == null) {
                return;
            }
            PreviewView.this.entry.videoVolume = f;
            PreviewView.this.checkVolumes();
        }

        @Override
        public void onVideoVolumeChange(int i, float f) {
            if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                return;
            }
            ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoVolume = f;
        }
    }

    public class AnonymousClass3 implements VideoPlayer.VideoPlayerDelegate {
        final StoryEntry val$entry;
        final Runnable[] val$whenReadyFinal;

        AnonymousClass3(StoryEntry storyEntry, Runnable[] runnableArr) {
            this.val$entry = storyEntry;
            this.val$whenReadyFinal = runnableArr;
        }

        public void lambda$onRenderedFirstFrame$0(StoryEntry storyEntry) {
            if (PreviewView.this.bitmap != null) {
                PreviewView.this.bitmap.recycle();
                if (storyEntry.blurredVideoThumb == PreviewView.this.bitmap) {
                    storyEntry.blurredVideoThumb = null;
                }
                PreviewView.this.bitmap = null;
                PreviewView.this.invalidate();
            }
        }

        @Override
        public void onError(VideoPlayer videoPlayer, Exception exc) {
            if (PreviewView.this.onErrorListener != null) {
                PreviewView.this.onErrorListener.run();
            }
        }

        @Override
        public void onRenderedFirstFrame() {
            if (PreviewView.this.textureViewHolder != null && PreviewView.this.textureViewHolder.active) {
                PreviewView.this.textureViewHolder.activateTextureView(PreviewView.this.videoWidth, PreviewView.this.videoHeight);
            }
            Runnable runnable = this.val$whenReadyFinal[0];
            if (runnable == null) {
                if (PreviewView.this.textureView != null) {
                    if (PreviewView.this.textureViewHolder == null || !PreviewView.this.textureViewHolder.active) {
                        ViewPropertyAnimator duration = PreviewView.this.textureView.animate().alpha(1.0f).setDuration(180L);
                        final StoryEntry storyEntry = this.val$entry;
                        duration.withEndAction(new Runnable() {
                            @Override
                            public final void run() {
                                PreviewView.AnonymousClass3.this.lambda$onRenderedFirstFrame$0(storyEntry);
                            }
                        }).start();
                        return;
                    }
                    return;
                }
                return;
            }
            PreviewView.this.post(runnable);
            this.val$whenReadyFinal[0] = null;
            if (PreviewView.this.bitmap != null) {
                PreviewView.this.bitmap.recycle();
                if (this.val$entry.blurredVideoThumb == PreviewView.this.bitmap) {
                    this.val$entry.blurredVideoThumb = null;
                }
                PreviewView.this.bitmap = null;
                PreviewView.this.invalidate();
            }
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override
        public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override
        public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        @Override
        public void onStateChanged(boolean z, int i) {
            if (PreviewView.this.videoPlayer == null) {
                return;
            }
            if (PreviewView.this.videoPlayer == null || !PreviewView.this.videoPlayer.isPlaying()) {
                AndroidUtilities.cancelRunOnUIThread(PreviewView.this.updateProgressRunnable);
            } else {
                AndroidUtilities.runOnUIThread(PreviewView.this.updateProgressRunnable);
            }
        }

        @Override
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            PreviewView.this.invalidateTextureViewHolder();
        }

        @Override
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            StoryEntry storyEntry = this.val$entry;
            if (storyEntry != null) {
                storyEntry.hdrInfo = PreviewView.this.videoPlayer.getHDRStaticInfo(this.val$entry.hdrInfo);
                if (PreviewView.this.textureView != null) {
                    PreviewView.this.textureView.setHDRInfo(this.val$entry.hdrInfo);
                }
            }
            PreviewView.this.videoWidth = (int) (i * f);
            PreviewView.this.videoHeight = (int) (i2 * f);
            StoryEntry storyEntry2 = this.val$entry;
            if (storyEntry2 != null && (storyEntry2.width != PreviewView.this.videoWidth || this.val$entry.height != PreviewView.this.videoHeight)) {
                this.val$entry.width = PreviewView.this.videoWidth;
                this.val$entry.height = PreviewView.this.videoHeight;
                this.val$entry.setupMatrix();
            }
            PreviewView.this.applyMatrix();
            if (PreviewView.this.textureView != null) {
                PreviewView.this.textureView.setVideoSize(PreviewView.this.videoWidth, PreviewView.this.videoHeight);
            }
        }
    }

    public class AnonymousClass4 implements VideoPlayer.VideoPlayerDelegate {
        AnonymousClass4() {
        }

        @Override
        public void onError(VideoPlayer videoPlayer, Exception exc) {
        }

        @Override
        public void onRenderedFirstFrame() {
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override
        public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override
        public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        @Override
        public void onStateChanged(boolean z, int i) {
            if (PreviewView.this.roundPlayer == null) {
                return;
            }
            if (PreviewView.this.roundPlayer == null || !PreviewView.this.roundPlayer.isPlaying()) {
                AndroidUtilities.cancelRunOnUIThread(PreviewView.this.updateRoundProgressRunnable);
            } else {
                AndroidUtilities.runOnUIThread(PreviewView.this.updateRoundProgressRunnable);
            }
        }

        @Override
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            PreviewView.this.roundPlayerWidth = i;
            PreviewView.this.roundPlayerHeight = i2;
            if (PreviewView.this.roundView != null) {
                PreviewView.this.roundView.resizeTextureView(i, i2);
            }
        }
    }

    public static class TextureViewHolder {
        public boolean active;
        private TextureView textureView;
        public boolean textureViewActive;
        public int videoHeight;
        public int videoWidth;
        private Utilities.Callback2 whenTextureViewActive;
        private Utilities.Callback whenTextureViewReceived;

        public void activateTextureView(int i, int i2) {
            this.textureViewActive = true;
            this.videoWidth = i;
            this.videoHeight = i2;
            Utilities.Callback2 callback2 = this.whenTextureViewActive;
            if (callback2 != null) {
                callback2.run(Integer.valueOf(i), Integer.valueOf(this.videoHeight));
            }
        }

        public void setTextureView(TextureView textureView) {
            TextureView textureView2 = this.textureView;
            if (textureView2 == textureView) {
                return;
            }
            if (textureView2 != null) {
                ViewParent parent = textureView2.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.textureView);
                }
                this.textureView = null;
            }
            this.textureViewActive = false;
            this.textureView = textureView;
            Utilities.Callback callback = this.whenTextureViewReceived;
            if (callback != null) {
                callback.run(textureView);
            }
        }

        public void takeTextureView(Utilities.Callback callback, Utilities.Callback2 callback2) {
            Utilities.Callback2 callback22;
            this.whenTextureViewReceived = callback;
            this.whenTextureViewActive = callback2;
            TextureView textureView = this.textureView;
            if (textureView != null && callback != null) {
                callback.run(textureView);
            }
            if (!this.textureViewActive || (callback22 = this.whenTextureViewActive) == null) {
                return;
            }
            callback22.run(Integer.valueOf(this.videoWidth), Integer.valueOf(this.videoHeight));
        }
    }

    public PreviewView(Context context, BlurringShader.BlurManager blurManager, TextureViewHolder textureViewHolder) {
        super(context);
        Paint paint = new Paint(1);
        this.snapPaint = paint;
        this.slowerSeek = new Runnable() {
            @Override
            public final void run() {
                PreviewView.this.lambda$new$0();
            }
        };
        this.updateProgressRunnable = new Runnable() {
            @Override
            public final void run() {
                PreviewView.this.lambda$new$8();
            }
        };
        this.updateAudioProgressRunnable = new Runnable() {
            @Override
            public final void run() {
                PreviewView.this.lambda$new$9();
            }
        };
        this.updateRoundProgressRunnable = new Runnable() {
            @Override
            public final void run() {
                PreviewView.this.lambda$new$10();
            }
        };
        this.wallpaperDrawableCrossfade = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.bitmapPaint = new Paint(7);
        this.gradientPaint = new Paint(1);
        this.matrix = new Matrix();
        this.vertices = new float[2];
        this.draw = true;
        this.thumbAlpha = new AnimatedFloat(this, 0L, 320L, CubicBezierInterpolator.EASE_OUT);
        this.drawForThemeToggle = false;
        this.allowCropping = true;
        this.lastTouch = new PointF();
        this.touch = new PointF();
        this.touchMatrix = new Matrix();
        this.finalMatrix = new Matrix();
        this.pauseLinks = new HashSet();
        this.blurManager = blurManager;
        this.textureViewHolder = textureViewHolder;
        paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(-1);
        paint.setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(1.0f), 1073741824);
    }

    private void extractPointsData(Matrix matrix) {
        if (this.entry == null) {
            return;
        }
        float[] fArr = this.vertices;
        fArr[0] = r0.width / 2.0f;
        fArr[1] = r0.height / 2.0f;
        matrix.mapPoints(fArr);
        float[] fArr2 = this.vertices;
        this.cx = fArr2[0];
        this.cy = fArr2[1];
        StoryEntry storyEntry = this.entry;
        fArr2[0] = storyEntry.width;
        fArr2[1] = storyEntry.height / 2.0f;
        matrix.mapPoints(fArr2);
        float[] fArr3 = this.vertices;
        this.angle = (float) Math.toDegrees(Math.atan2(fArr3[1] - this.cy, fArr3[0] - this.cx));
        float f = this.cx;
        float f2 = this.cy;
        float[] fArr4 = this.vertices;
        this.w = MathUtils.distance(f, f2, fArr4[0], fArr4[1]) * 2.0f;
        float[] fArr5 = this.vertices;
        StoryEntry storyEntry2 = this.entry;
        fArr5[0] = storyEntry2.width / 2.0f;
        fArr5[1] = storyEntry2.height;
        matrix.mapPoints(fArr5);
        float f3 = this.cx;
        float f4 = this.cy;
        float[] fArr6 = this.vertices;
        this.h = MathUtils.distance(f3, f4, fArr6[0], fArr6[1]) * 2.0f;
    }

    public static Drawable getBackgroundDrawable(Drawable drawable, int i, long j, boolean z) {
        TLRPC.WallPaper wallPaper = null;
        if (j == Long.MIN_VALUE) {
            return null;
        }
        MessagesController messagesController = MessagesController.getInstance(i);
        if (j >= 0) {
            TLRPC.UserFull userFull = messagesController.getUserFull(j);
            if (userFull != null) {
                wallPaper = userFull.wallpaper;
            }
        } else {
            TLRPC.ChatFull chatFull = messagesController.getChatFull(-j);
            if (chatFull != null) {
                wallPaper = chatFull.wallpaper;
            }
        }
        return getBackgroundDrawable(drawable, i, wallPaper, z);
    }

    public static android.graphics.drawable.Drawable getBackgroundDrawable(android.graphics.drawable.Drawable r5, int r6, org.telegram.tgnet.TLRPC.WallPaper r7, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.PreviewView.getBackgroundDrawable(android.graphics.drawable.Drawable, int, org.telegram.tgnet.TLRPC$WallPaper, boolean):android.graphics.drawable.Drawable");
    }

    public static Drawable getBackgroundDrawableFromTheme(int i, String str, boolean z) {
        return getBackgroundDrawableFromTheme(i, str, z, false);
    }

    public static Drawable getBackgroundDrawableFromTheme(int i, String str, boolean z, boolean z2) {
        EmojiThemes theme = ChatThemeController.getInstance(i).getTheme(str);
        return theme == null ? Theme.getCachedWallpaper() : getBackgroundDrawableFromTheme(i, theme, 0, z, z2);
    }

    public static Drawable getBackgroundDrawableFromTheme(int i, EmojiThemes emojiThemes, int i2, boolean z) {
        return getBackgroundDrawableFromTheme(i, emojiThemes, i2, z, false);
    }

    public static Drawable getBackgroundDrawableFromTheme(int i, final EmojiThemes emojiThemes, int i2, final boolean z, boolean z2) {
        if (emojiThemes.isAnyStub()) {
            Drawable drawable = Theme.createBackgroundDrawable(EmojiThemes.getDefaultThemeInfo(z), emojiThemes.getPreviewColors(i, z ? 1 : 0), emojiThemes.getWallpaperLink(z ? 1 : 0), i2, false).wallpaper;
            return new ColorDrawable(-16777216);
        }
        SparseIntArray previewColors = emojiThemes.getPreviewColors(i, z ? 1 : 0);
        int i3 = Theme.key_chat_wallpaper;
        int i4 = previewColors.get(i3, Theme.getColor(i3));
        int i5 = Theme.key_chat_wallpaper_gradient_to1;
        int i6 = previewColors.get(i5, Theme.getColor(i5));
        int i7 = Theme.key_chat_wallpaper_gradient_to2;
        int i8 = previewColors.get(i7, Theme.getColor(i7));
        int i9 = Theme.key_chat_wallpaper_gradient_to3;
        int i10 = previewColors.get(i9, Theme.getColor(i9));
        final MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
        motionBackgroundDrawable.isPreview = z2;
        motionBackgroundDrawable.setPatternBitmap(emojiThemes.getWallpaper(z ? 1 : 0).settings.intensity);
        motionBackgroundDrawable.setColors(i4, i6, i8, i10, 0, true);
        motionBackgroundDrawable.setPhase(i2);
        final int patternColor = motionBackgroundDrawable.getPatternColor();
        emojiThemes.loadWallpaper(z ? 1 : 0, new ResultCallback() {
            @Override
            public final void onComplete(Object obj) {
                PreviewView.lambda$getBackgroundDrawableFromTheme$11(EmojiThemes.this, z, z, motionBackgroundDrawable, patternColor, (Pair) obj);
            }

            @Override
            public void onError(Throwable th) {
                ResultCallback.CC.$default$onError(this, th);
            }

            @Override
            public void onError(TLRPC.TL_error tL_error) {
                ResultCallback.CC.$default$onError(this, tL_error);
            }
        });
        return motionBackgroundDrawable;
    }

    public static void lambda$getBackgroundDrawableFromTheme$11(EmojiThemes emojiThemes, boolean z, boolean z2, MotionBackgroundDrawable motionBackgroundDrawable, int i, Pair pair) {
        if (pair == null) {
            return;
        }
        long longValue = ((Long) pair.first).longValue();
        Bitmap bitmap = (Bitmap) pair.second;
        if (longValue != emojiThemes.getTlTheme(z ? 1 : 0).id || bitmap == null) {
            return;
        }
        motionBackgroundDrawable.setPatternBitmap(emojiThemes.getWallpaper(z2 ? 1 : 0).settings.intensity, bitmap);
        motionBackgroundDrawable.setPatternColorFilter(i);
        motionBackgroundDrawable.setPatternAlpha(1.0f);
    }

    public static void lambda$getCoverBitmap$2(int i, int i2, int i3, Bitmap[] bitmapArr, final Utilities.Callback callback) {
        final Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Path path = new Path();
        RectF rectF = new RectF();
        rectF.set(0.0f, 0.0f, createBitmap.getWidth(), createBitmap.getHeight());
        float f = i3;
        path.addRoundRect(rectF, f, f, Path.Direction.CW);
        canvas.clipPath(path);
        for (int i4 = 0; i4 < bitmapArr.length; i4++) {
            if (bitmapArr[i4] != null) {
                canvas.save();
                canvas.translate(createBitmap.getWidth() / 2.0f, createBitmap.getHeight() / 2.0f);
                float max = Math.max(createBitmap.getWidth() / bitmapArr[i4].getWidth(), createBitmap.getHeight() / bitmapArr[i4].getHeight());
                canvas.scale(max, max);
                canvas.translate((-bitmapArr[i4].getWidth()) / 2.0f, (-bitmapArr[i4].getHeight()) / 2.0f);
                canvas.drawBitmap(bitmapArr[i4], 0.0f, 0.0f, (Paint) null);
                canvas.restore();
                AndroidUtilities.recycleBitmap(bitmapArr[i4]);
            }
        }
        Utilities.stackBlurBitmap(createBitmap, 1);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(createBitmap);
            }
        });
    }

    public void lambda$new$0() {
        seekTo(this.finalSeekPosition);
        this.slowerSeekScheduled = false;
    }

    public void lambda$new$10() {
        if (this.roundPlayer == null || this.videoPlayer != null || isCollage() || this.timelineView == null) {
            return;
        }
        long currentPosition = this.roundPlayer.getCurrentPosition();
        StoryEntry storyEntry = this.entry;
        if (storyEntry != null) {
            float f = (float) currentPosition;
            float f2 = storyEntry.roundLeft;
            float f3 = (float) storyEntry.roundDuration;
            if ((f < f2 * f3 || f > storyEntry.roundRight * f3) && System.currentTimeMillis() - this.seekedLastTime > 500) {
                this.seekedLastTime = System.currentTimeMillis();
                VideoPlayer videoPlayer = this.roundPlayer;
                StoryEntry storyEntry2 = this.entry;
                long j = storyEntry2.roundLeft * ((float) storyEntry2.roundDuration);
                videoPlayer.seekTo(j);
                updateAudioPlayer(true);
                currentPosition = j;
            }
        }
        this.timelineView.setProgress(currentPosition);
        if (this.roundPlayer.isPlaying()) {
            AndroidUtilities.cancelRunOnUIThread(this.updateRoundProgressRunnable);
            AndroidUtilities.runOnUIThread(this.updateRoundProgressRunnable, 1000.0f / AndroidUtilities.screenRefreshRate);
        }
    }

    public void lambda$new$8() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null || this.timelineView == null) {
            return;
        }
        long currentPosition = videoPlayer.getCurrentPosition();
        if (getDuration() > 1) {
            float duration = ((float) currentPosition) / ((float) getDuration());
            if (!this.timelineView.isDragging()) {
                StoryEntry storyEntry = this.entry;
                if ((duration < storyEntry.left || duration > storyEntry.right) && System.currentTimeMillis() - this.seekedLastTime > 500) {
                    this.seekedLastTime = System.currentTimeMillis();
                    VideoPlayer videoPlayer2 = this.videoPlayer;
                    long duration2 = this.entry.left * ((float) getDuration());
                    videoPlayer2.seekTo(duration2);
                    updateAudioPlayer(true);
                    updateRoundPlayer(true);
                    currentPosition = duration2;
                }
            }
            updateAudioPlayer(currentPosition < this.lastPos);
            updateRoundPlayer(currentPosition < this.lastPos);
        }
        this.timelineView.setProgress(this.videoPlayer.getCurrentPosition());
        if (this.videoPlayer.isPlaying()) {
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
            AndroidUtilities.runOnUIThread(this.updateProgressRunnable, 1000.0f / AndroidUtilities.screenRefreshRate);
        }
        this.lastPos = currentPosition;
    }

    public void lambda$new$9() {
        if (this.audioPlayer == null || this.videoPlayer != null || this.roundPlayer != null || this.timelineView == null || isCollage()) {
            return;
        }
        long currentPosition = this.audioPlayer.getCurrentPosition();
        StoryEntry storyEntry = this.entry;
        if (storyEntry != null) {
            float f = (float) currentPosition;
            float f2 = storyEntry.audioLeft;
            float f3 = (float) storyEntry.audioDuration;
            if ((f < f2 * f3 || f > storyEntry.audioRight * f3) && System.currentTimeMillis() - this.seekedLastTime > 500) {
                this.seekedLastTime = System.currentTimeMillis();
                VideoPlayer videoPlayer = this.audioPlayer;
                StoryEntry storyEntry2 = this.entry;
                long j = storyEntry2.audioLeft * ((float) storyEntry2.audioDuration);
                videoPlayer.seekTo(j);
                currentPosition = j;
            }
        }
        this.timelineView.setProgress(currentPosition);
        if (this.audioPlayer.isPlaying()) {
            AndroidUtilities.cancelRunOnUIThread(this.updateAudioProgressRunnable);
            AndroidUtilities.runOnUIThread(this.updateAudioProgressRunnable, 1000.0f / AndroidUtilities.screenRefreshRate);
        }
    }

    public void lambda$setupGradient$4(int i, int[] iArr) {
        StoryEntry storyEntry = this.entry;
        int i2 = iArr[0];
        this.gradientTop = i2;
        storyEntry.gradientTopColor = i2;
        int i3 = iArr[1];
        this.gradientBottom = i3;
        storyEntry.gradientBottomColor = i3;
        this.gradientPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, i, iArr, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        invalidate();
        VideoEditTextureView videoEditTextureView = this.textureView;
        if (videoEditTextureView != null) {
            videoEditTextureView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
        }
        PhotoFilterView photoFilterView = this.photoFilterView;
        if (photoFilterView != null) {
            photoFilterView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
        }
    }

    public void lambda$setupGradient$5(int i, int[] iArr) {
        StoryEntry storyEntry = this.entry;
        int i2 = iArr[0];
        this.gradientTop = i2;
        storyEntry.gradientTopColor = i2;
        int i3 = iArr[1];
        this.gradientBottom = i3;
        storyEntry.gradientBottomColor = i3;
        this.gradientPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, i, iArr, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        invalidate();
        VideoEditTextureView videoEditTextureView = this.textureView;
        if (videoEditTextureView != null) {
            videoEditTextureView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
        }
        PhotoFilterView photoFilterView = this.photoFilterView;
        if (photoFilterView != null) {
            photoFilterView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
        }
    }

    public Bitmap lambda$setupImage$3(StoryEntry storyEntry, long j, String str, BitmapFactory.Options options) {
        if (!storyEntry.isVideo) {
            return BitmapFactory.decodeFile(str, options);
        }
        String str2 = storyEntry.thumbPath;
        if (str2 != null) {
            return BitmapFactory.decodeFile(str2, options);
        }
        try {
            return MediaStore.Video.Thumbnails.getThumbnail(getContext().getContentResolver(), j, 1, options);
        } catch (Throwable unused) {
            invalidate();
            return null;
        }
    }

    public void lambda$setupVideoPlayer$6() {
        VideoEditTextureView videoEditTextureView = this.textureView;
        if (videoEditTextureView != null) {
            videoEditTextureView.release();
            removeView(this.textureView);
            this.textureView = null;
        }
    }

    public void lambda$setupVideoPlayer$7(StoryEntry.HDRInfo hDRInfo) {
        VideoEditTextureView videoEditTextureView = this.textureView;
        if (videoEditTextureView != null) {
            videoEditTextureView.setHDRInfo(hDRInfo);
        }
    }

    public void seekTo(long j) {
        seekTo(j, false);
    }

    private void setupCollage(StoryEntry storyEntry) {
        TimelineView timelineView = this.timelineView;
        if (timelineView != null) {
            timelineView.setCollage(storyEntry != null ? storyEntry.collageContent : null);
        }
    }

    public void setupGradient() {
        Utilities.Callback callback;
        final int measuredHeight = getMeasuredHeight() > 0 ? getMeasuredHeight() : AndroidUtilities.displaySize.y;
        StoryEntry storyEntry = this.entry;
        if (storyEntry.gradientTopColor == 0 || storyEntry.gradientBottomColor == 0) {
            Bitmap bitmap = this.bitmap;
            if (bitmap != null) {
                callback = new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        PreviewView.this.lambda$setupGradient$4(measuredHeight, (int[]) obj);
                    }
                };
            } else {
                bitmap = this.thumbBitmap;
                if (bitmap != null) {
                    callback = new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            PreviewView.this.lambda$setupGradient$5(measuredHeight, (int[]) obj);
                        }
                    };
                } else {
                    this.gradientPaint.setShader(null);
                }
            }
            DominantColors.getColors(true, bitmap, true, callback);
        } else {
            Paint paint = this.gradientPaint;
            float f = measuredHeight;
            StoryEntry storyEntry2 = this.entry;
            int i = storyEntry2.gradientTopColor;
            this.gradientTop = i;
            int i2 = storyEntry2.gradientBottomColor;
            this.gradientBottom = i2;
            paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, f, new int[]{i, i2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            VideoEditTextureView videoEditTextureView = this.textureView;
            if (videoEditTextureView != null) {
                videoEditTextureView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
            }
            PhotoFilterView photoFilterView = this.photoFilterView;
            if (photoFilterView != null) {
                photoFilterView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
            }
        }
        invalidate();
    }

    private void setupImage(final org.telegram.ui.Stories.recorder.StoryEntry r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.PreviewView.setupImage(org.telegram.ui.Stories.recorder.StoryEntry):void");
    }

    private boolean tapTouchEvent(MotionEvent motionEvent) {
        Runnable runnable;
        if (motionEvent.getAction() == 0) {
            this.tapTime = System.currentTimeMillis();
            return true;
        }
        if (motionEvent.getAction() != 1) {
            if (motionEvent.getAction() != 3) {
                return false;
            }
            this.tapTime = 0L;
            return false;
        }
        if (System.currentTimeMillis() - this.tapTime <= ViewConfiguration.getTapTimeout() && (runnable = this.onTap) != null) {
            runnable.run();
        }
        this.tapTime = 0L;
        return true;
    }

    private boolean touchEvent(MotionEvent motionEvent) {
        double d;
        float f;
        if (!this.allowCropping) {
            return false;
        }
        boolean z = motionEvent.getPointerCount() > 1;
        PointF pointF = this.touch;
        float x = motionEvent.getX(0);
        if (z) {
            pointF.x = (x + motionEvent.getX(1)) / 2.0f;
            this.touch.y = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
            f = MathUtils.distance(motionEvent.getX(0), motionEvent.getY(0), motionEvent.getX(1), motionEvent.getY(1));
            d = Math.atan2(motionEvent.getY(1) - motionEvent.getY(0), motionEvent.getX(1) - motionEvent.getX(0));
        } else {
            pointF.x = x;
            this.touch.y = motionEvent.getY(0);
            d = 0.0d;
            f = 0.0f;
        }
        if (this.multitouch != z) {
            PointF pointF2 = this.lastTouch;
            PointF pointF3 = this.touch;
            pointF2.x = pointF3.x;
            pointF2.y = pointF3.y;
            this.lastTouchDistance = f;
            this.lastTouchRotation = d;
            this.multitouch = z;
        }
        if (this.entry == null) {
            return false;
        }
        float width = r2.resultWidth / getWidth();
        if (motionEvent.getActionMasked() == 0) {
            this.rotationDiff = 0.0f;
            this.snappedRotation = false;
            this.snappedToCenterAndScaled = false;
            this.doNotSpanRotation = false;
            invalidate();
            this.moving = true;
            this.touchMatrix.set(this.entry.matrix);
        }
        if (motionEvent.getActionMasked() == 2 && this.moving && this.entry != null) {
            PointF pointF4 = this.touch;
            float f2 = pointF4.x * width;
            float f3 = pointF4.y * width;
            PointF pointF5 = this.lastTouch;
            float f4 = pointF5.x * width;
            float f5 = pointF5.y * width;
            if (motionEvent.getPointerCount() > 1) {
                float f6 = this.lastTouchDistance;
                if (f6 != 0.0f) {
                    float f7 = f / f6;
                    this.touchMatrix.postScale(f7, f7, f2, f3);
                }
                float degrees = (float) Math.toDegrees(d - this.lastTouchRotation);
                float f8 = this.rotationDiff + degrees;
                this.rotationDiff = f8;
                if (!this.allowRotation) {
                    boolean z2 = Math.abs(f8) > 20.0f;
                    this.allowRotation = z2;
                    if (!z2) {
                        extractPointsData(this.touchMatrix);
                        this.allowRotation = (((float) Math.round(this.angle / 90.0f)) * 90.0f) - this.angle > 20.0f;
                    }
                    if (!this.snappedRotation) {
                        AndroidUtilities.vibrateCursor(this);
                        this.snappedRotation = true;
                    }
                }
                if (this.allowRotation) {
                    this.touchMatrix.postRotate(degrees, f2, f3);
                }
                this.allowWithSingleTouch = true;
            }
            if (motionEvent.getPointerCount() > 1 || this.allowWithSingleTouch) {
                this.touchMatrix.postTranslate(f2 - f4, f3 - f5);
            }
            this.finalMatrix.set(this.touchMatrix);
            this.matrix.set(this.touchMatrix);
            extractPointsData(this.matrix);
            float round = (Math.round(this.angle / 90.0f) * 90.0f) - this.angle;
            if (this.allowRotation && !this.doNotSpanRotation) {
                if (Math.abs(round) < 3.5f) {
                    this.finalMatrix.postRotate(round, this.cx, this.cy);
                    if (!this.snappedRotation) {
                        AndroidUtilities.vibrateCursor(this);
                        this.snappedRotation = true;
                    }
                } else {
                    this.snappedRotation = false;
                }
            }
            this.entry.matrix.set(this.finalMatrix);
            this.entry.editedMedia = true;
            applyMatrix();
            invalidate();
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getPointerCount() <= 1) {
                this.allowWithSingleTouch = false;
                onEntityDraggedTop(false);
                onEntityDraggedBottom(false);
            }
            this.moving = false;
            this.allowRotation = false;
            this.rotationDiff = 0.0f;
            this.snappedToCenterAndScaled = false;
            this.snappedRotation = false;
            invalidate();
        }
        PointF pointF6 = this.lastTouch;
        PointF pointF7 = this.touch;
        pointF6.x = pointF7.x;
        pointF6.y = pointF7.y;
        this.lastTouchDistance = f;
        this.lastTouchRotation = d;
        return true;
    }

    public abstract boolean additionalTouchEvent(MotionEvent motionEvent);

    public void applyMatrix() {
        StoryEntry storyEntry = this.entry;
        if (storyEntry == null || storyEntry.isRepostMessage) {
            return;
        }
        if (this.textureView != null) {
            this.matrix.set(storyEntry.matrix);
            Matrix matrix = this.matrix;
            float width = 1.0f / getWidth();
            int i = this.entry.width;
            if (i < 0) {
                i = this.videoWidth;
            }
            float f = width * i;
            float height = 1.0f / getHeight();
            int i2 = this.entry.height;
            if (i2 < 0) {
                i2 = this.videoHeight;
            }
            matrix.preScale(f, height * i2);
            this.matrix.postScale(getWidth() / this.entry.resultWidth, getHeight() / this.entry.resultHeight);
            this.textureView.setTransform(this.matrix);
            this.textureView.invalidate();
        }
        invalidate();
    }

    public void attachRoundView(RoundView roundView) {
        VideoPlayer videoPlayer;
        this.roundView = roundView;
        if (roundView == null || (videoPlayer = this.roundPlayer) == null) {
            return;
        }
        videoPlayer.setTextureView(roundView.textureView);
    }

    public void checkVolumes() {
        float f;
        StoryEntry storyEntry;
        VideoPlayer videoPlayer = this.videoPlayer;
        float f2 = 0.0f;
        if (videoPlayer != null) {
            videoPlayer.setVolume((this.isMuted || ((storyEntry = this.entry) != null && storyEntry.muted)) ? 0.0f : storyEntry != null ? storyEntry.videoVolume : 1.0f);
        }
        VideoPlayer videoPlayer2 = this.roundPlayer;
        if (videoPlayer2 != null) {
            if (this.isMuted) {
                f = 0.0f;
            } else {
                StoryEntry storyEntry2 = this.entry;
                f = storyEntry2 != null ? storyEntry2.roundVolume : 1.0f;
            }
            videoPlayer2.setVolume(f);
        }
        VideoPlayer videoPlayer3 = this.audioPlayer;
        if (videoPlayer3 != null) {
            if (!this.isMuted) {
                StoryEntry storyEntry3 = this.entry;
                f2 = storyEntry3 != null ? storyEntry3.audioVolume : 1.0f;
            }
            videoPlayer3.setVolume(f2);
        }
        CollageLayoutView2 collageLayoutView2 = this.collage;
        if (collageLayoutView2 != null) {
            collageLayoutView2.setMuted(this.isMuted);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.wallpaperDrawable != null) {
            if (this.drawForThemeToggle) {
                Path path = new Path();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                path.addRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
                canvas.save();
                canvas.clipPath(path);
            }
            Drawable drawable = this.wallpaperDrawable;
            float f = ((drawable instanceof MotionBackgroundDrawable) && ((MotionBackgroundDrawable) drawable).getPatternBitmap() == null) ? 0.0f : this.wallpaperDrawableCrossfade.set(1.0f);
            Drawable drawable2 = this.lastWallpaperDrawable;
            if (drawable2 != null && f < 1.0f) {
                drawable2.setAlpha((int) ((1.0f - f) * 255.0f));
                StoryEntry.drawBackgroundDrawable(canvas, this.lastWallpaperDrawable, getWidth(), getHeight());
            }
            this.wallpaperDrawable.setAlpha((int) (f * 255.0f));
            StoryEntry.drawBackgroundDrawable(canvas, this.wallpaperDrawable, getWidth(), getHeight());
            if (this.drawForThemeToggle) {
                canvas.restore();
            }
        } else {
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.gradientPaint);
        }
        if (this.draw && this.entry != null && !isCollage()) {
            float f2 = this.thumbAlpha.set(this.bitmap != null);
            if (this.thumbBitmap != null && 1.0f - f2 > 0.0f) {
                this.matrix.set(this.entry.matrix);
                this.matrix.preScale(this.entry.width / this.thumbBitmap.getWidth(), this.entry.height / this.thumbBitmap.getHeight());
                this.matrix.postScale(getWidth() / this.entry.resultWidth, getHeight() / this.entry.resultHeight);
                this.bitmapPaint.setAlpha(255);
                canvas.drawBitmap(this.thumbBitmap, this.matrix, this.bitmapPaint);
            }
            if (this.bitmap != null) {
                this.matrix.set(this.entry.matrix);
                this.matrix.preScale(this.entry.width / this.bitmap.getWidth(), this.entry.height / this.bitmap.getHeight());
                this.matrix.postScale(getWidth() / this.entry.resultWidth, getHeight() / this.entry.resultHeight);
                this.bitmapPaint.setAlpha((int) (f2 * 255.0f));
                canvas.drawBitmap(this.bitmap, this.matrix, this.bitmapPaint);
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean z = additionalTouchEvent(motionEvent) || touchEvent(motionEvent);
        tapTouchEvent(motionEvent);
        if (!z) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (motionEvent.getPointerCount() <= 1) {
            super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        StoryEntry storyEntry;
        if (view == this.textureView && (storyEntry = this.entry) != null && storyEntry.isRepostMessage) {
            return false;
        }
        return super.drawChild(canvas, view, j);
    }

    public void getCoverBitmap(final Utilities.Callback callback, View... viewArr) {
        VideoEditTextureView videoEditTextureView;
        final int dp = (int) (AndroidUtilities.dp(26.0f) * AndroidUtilities.density);
        final int dp2 = (int) (AndroidUtilities.dp(30.33f) * AndroidUtilities.density);
        final int dp3 = (int) (AndroidUtilities.dp(4.0f) * AndroidUtilities.density);
        final Bitmap[] bitmapArr = new Bitmap[viewArr.length];
        for (int i = 0; i < viewArr.length; i++) {
            View view = viewArr[i];
            if (view != null && view.getWidth() >= 0 && viewArr[i].getHeight() > 0) {
                View view2 = viewArr[i];
                if (view2 == this && (videoEditTextureView = this.textureView) != null) {
                    bitmapArr[i] = videoEditTextureView.getBitmap();
                } else if (view2 instanceof TextureView) {
                    bitmapArr[i] = ((TextureView) view2).getBitmap();
                } else if ((view2 instanceof ViewGroup) && ((ViewGroup) view2).getChildCount() > 0) {
                    bitmapArr[i] = Bitmap.createBitmap(dp, dp2, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmapArr[i]);
                    canvas.save();
                    float max = Math.max(dp / viewArr[i].getWidth(), dp2 / viewArr[i].getHeight());
                    canvas.scale(max, max);
                    viewArr[i].draw(canvas);
                    canvas.restore();
                }
            }
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                PreviewView.lambda$getCoverBitmap$2(dp, dp2, dp3, bitmapArr, callback);
            }
        });
    }

    public long getCurrentPosition() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            return videoPlayer.getCurrentPosition();
        }
        VideoPlayer videoPlayer2 = this.roundPlayer;
        if (videoPlayer2 != null) {
            return videoPlayer2.getCurrentPosition();
        }
        VideoPlayer videoPlayer3 = this.audioPlayer;
        if (videoPlayer3 != null) {
            return videoPlayer3.getCurrentPosition();
        }
        return 0L;
    }

    public long getDuration() {
        StoryEntry storyEntry = this.entry;
        if (storyEntry != null) {
            double d = storyEntry.fileDuration;
            if (d >= 0.0d) {
                return (long) (d * 1000.0d);
            }
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null || videoPlayer.getDuration() == -9223372036854775807L) {
            return 1L;
        }
        return this.videoPlayer.getDuration();
    }

    public int getOrientation() {
        StoryEntry storyEntry = this.entry;
        if (storyEntry == null) {
            return 0;
        }
        return storyEntry.orientation;
    }

    public Pair<Integer, Integer> getPaintSize() {
        return this.entry == null ? new Pair<>(1080, 1920) : new Pair<>(Integer.valueOf(this.entry.resultWidth), Integer.valueOf(this.entry.resultHeight));
    }

    public Bitmap getPhotoBitmap() {
        return this.bitmap;
    }

    public VideoEditTextureView getTextureView() {
        return this.textureView;
    }

    protected abstract void invalidateTextureViewHolder();

    public boolean isCollage() {
        StoryEntry storyEntry;
        return (this.collage == null || (storyEntry = this.entry) == null || !storyEntry.isCollage()) ? false : true;
    }

    public boolean isPlaying() {
        return !this.pauseLinks.contains(-9982);
    }

    public void mute(boolean z) {
        this.isMuted = z;
        checkVolumes();
    }

    public abstract void onAudioChanged();

    public abstract void onEntityDraggedBottom(boolean z);

    public abstract void onEntityDraggedTop(boolean z);

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.allowCropping) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        touchEvent(motionEvent);
        return true;
    }

    public abstract void onRoundRemove();

    public abstract void onRoundSelectChange(boolean z);

    public void play(boolean z) {
        updatePauseReason(-9982, !z);
    }

    public void preset(StoryEntry storyEntry) {
        this.entry = storyEntry;
        if (storyEntry == null) {
            setupImage(null);
            setupWallpaper(null, false);
            this.gradientPaint.setShader(null);
            setupAudio((StoryEntry) null, false);
            setupRound(null, null, false);
            return;
        }
        boolean z = storyEntry.isVideo;
        setupImage(storyEntry);
        if (z && storyEntry.gradientTopColor == 0 && storyEntry.gradientBottomColor == 0) {
            storyEntry.setupGradient(new PreviewView$$ExternalSyntheticLambda2(this));
        } else {
            setupGradient();
        }
        applyMatrix();
        setupWallpaper(storyEntry, false);
        setupAudio(storyEntry, false);
        setupRound(storyEntry, null, false);
    }

    public long release() {
        long j;
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            videoPlayer.pause();
            this.audioPlayer.releasePlayer(true);
            this.audioPlayer = null;
        }
        VideoPlayer videoPlayer2 = this.roundPlayer;
        if (videoPlayer2 != null) {
            j = videoPlayer2.getCurrentPosition();
            this.roundPlayer.pause();
            this.roundPlayer.releasePlayer(true);
            this.roundPlayer = null;
        } else {
            j = 0;
        }
        VideoPlayer videoPlayer3 = this.videoPlayer;
        if (videoPlayer3 == null) {
            return j;
        }
        long currentPosition = videoPlayer3.getCurrentPosition();
        this.videoPlayer.pause();
        this.videoPlayer.releasePlayer(true);
        this.videoPlayer = null;
        return currentPosition;
    }

    public void seek(long j) {
        seekTo(j);
        TimelineView timelineView = this.timelineView;
        if (timelineView != null) {
            timelineView.setProgress(0L);
        }
    }

    public void seekTo(long r6, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.PreviewView.seekTo(long, boolean):void");
    }

    public void set(StoryEntry storyEntry) {
        set(storyEntry, null, 0L);
    }

    public void set(StoryEntry storyEntry, Runnable runnable, long j) {
        this.entry = storyEntry;
        if (storyEntry == null) {
            setupVideoPlayer(null, runnable, j);
            setupImage(null);
            setupCollage(null);
            setupWallpaper(null, false);
            this.gradientPaint.setShader(null);
            setupAudio((StoryEntry) null, false);
            setupRound(null, null, false);
            return;
        }
        if (storyEntry.isCollage()) {
            setupImage(null);
            setupVideoPlayer(null, runnable, j);
            setupCollage(storyEntry);
        } else {
            if (storyEntry.isVideo) {
                setupImage(storyEntry);
                setupCollage(null);
                setupVideoPlayer(storyEntry, runnable, j);
                if (storyEntry.gradientTopColor == 0 && storyEntry.gradientBottomColor == 0) {
                    storyEntry.setupGradient(new PreviewView$$ExternalSyntheticLambda2(this));
                }
            } else {
                setupCollage(null);
                setupVideoPlayer(null, runnable, 0L);
                setupImage(storyEntry);
            }
            setupGradient();
        }
        applyMatrix();
        setupWallpaper(storyEntry, false);
        setupAudio(storyEntry, false);
        setupRound(storyEntry, null, false);
    }

    public void setAllowCropping(boolean z) {
        this.allowCropping = z;
    }

    public void setCollageView(CollageLayoutView2 collageLayoutView2) {
        this.collage = collageLayoutView2;
    }

    public void setDraw(boolean z) {
        this.draw = z;
        invalidate();
    }

    public void setFilterTextureView(TextureView textureView, PhotoFilterView photoFilterView) {
        TextureView textureView2 = this.filterTextureView;
        if (textureView2 != null) {
            removeView(textureView2);
            this.filterTextureView = null;
        }
        this.photoFilterView = photoFilterView;
        this.filterTextureView = textureView;
        if (photoFilterView != null) {
            photoFilterView.updateUiBlurGradient(this.gradientTop, this.gradientBottom);
        }
        TextureView textureView3 = this.filterTextureView;
        if (textureView3 != null) {
            addView(textureView3);
        }
    }

    public void setOnTapListener(Runnable runnable) {
        this.onTap = runnable;
    }

    public void setVideoTimelineView(TimelineView timelineView) {
        this.timelineView = timelineView;
        if (timelineView != null) {
            timelineView.setDelegate(new TimelineView.TimelineDelegate() {
                AnonymousClass2() {
                }

                @Override
                public void onAudioLeftChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.audioLeft = f;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.updateAudioPlayer(true);
                }

                @Override
                public void onAudioOffsetChange(long j) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.audioOffset = j;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.updateAudioPlayer(true);
                }

                @Override
                public void onAudioRemove() {
                    PreviewView.this.setupAudio((MessageObject) null, true);
                }

                @Override
                public void onAudioRightChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.audioRight = f;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.updateAudioPlayer(true);
                }

                @Override
                public void onAudioVolumeChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.audioVolume = f;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.checkVolumes();
                }

                @Override
                public void onProgressChange(long j, boolean z) {
                    VideoPlayer videoPlayer;
                    if (!z) {
                        PreviewView.this.seekTo(j);
                        return;
                    }
                    boolean z2 = true;
                    if (PreviewView.this.videoPlayer != null) {
                        videoPlayer = PreviewView.this.videoPlayer;
                    } else if (PreviewView.this.isCollage()) {
                        PreviewView.this.collage.seekTo(j, true);
                        return;
                    } else {
                        if (PreviewView.this.audioPlayer == null) {
                            return;
                        }
                        videoPlayer = PreviewView.this.audioPlayer;
                        z2 = false;
                    }
                    videoPlayer.seekTo(j, z2);
                }

                @Override
                public void onProgressDragChange(boolean z) {
                    if (PreviewView.this.isCollage()) {
                        PreviewView.this.collage.forceNotRestorePosition();
                    }
                    PreviewView.this.updatePauseReason(-4, z);
                }

                @Override
                public void onRoundLeftChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.roundLeft = f;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.updateRoundPlayer(true);
                }

                @Override
                public void onRoundOffsetChange(long j) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.roundOffset = j;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.updateRoundPlayer(true);
                }

                @Override
                public void onRoundRemove() {
                    PreviewView.this.setupRound(null, null, true);
                    PreviewView.this.onRoundRemove();
                }

                @Override
                public void onRoundRightChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.roundRight = f;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.updateRoundPlayer(true);
                }

                @Override
                public void onRoundSelectChange(boolean z) {
                    PreviewView.this.onRoundSelectChange(z);
                }

                @Override
                public void onRoundVolumeChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.roundVolume = f;
                    PreviewView.this.entry.editedMedia = true;
                    PreviewView.this.checkVolumes();
                }

                @Override
                public void onVideoLeftChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.left = f;
                    PreviewView.this.entry.editedMedia = true;
                    if (PreviewView.this.videoPlayer == null || PreviewView.this.videoPlayer.getDuration() == -9223372036854775807L) {
                        return;
                    }
                    PreviewView.this.seekTo(f * ((float) r0.videoPlayer.getDuration()));
                }

                @Override
                public void onVideoLeftChange(int i, float f) {
                    if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                        return;
                    }
                    ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoLeft = f;
                }

                @Override
                public void onVideoOffsetChange(int i, long j) {
                    if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                        return;
                    }
                    ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoOffset = j;
                }

                @Override
                public void onVideoRightChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.right = f;
                    PreviewView.this.entry.editedMedia = true;
                }

                @Override
                public void onVideoRightChange(int i, float f) {
                    if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                        return;
                    }
                    ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoRight = f;
                }

                @Override
                public void onVideoSelected(int i) {
                    if (PreviewView.this.collage != null) {
                        PreviewView.this.collage.highlight(i);
                    }
                }

                @Override
                public void onVideoVolumeChange(float f) {
                    if (PreviewView.this.entry == null) {
                        return;
                    }
                    PreviewView.this.entry.videoVolume = f;
                    PreviewView.this.checkVolumes();
                }

                @Override
                public void onVideoVolumeChange(int i, float f) {
                    if (PreviewView.this.entry == null || PreviewView.this.entry.collageContent == null || i < 0 || i >= PreviewView.this.entry.collageContent.size()) {
                        return;
                    }
                    ((StoryEntry) PreviewView.this.entry.collageContent.get(i)).videoVolume = f;
                }
            });
        }
    }

    @Override
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 8) {
            set(null);
        }
    }

    public void setupAudio(MessageObject messageObject, boolean z) {
        TLRPC.Message message;
        long duration;
        StoryEntry storyEntry = this.entry;
        if (storyEntry != null) {
            storyEntry.editedMedia = true;
            if (messageObject == null || (message = messageObject.messageOwner) == null) {
                storyEntry.audioPath = null;
                storyEntry.audioAuthor = null;
                storyEntry.audioTitle = null;
                storyEntry.audioOffset = 0L;
                storyEntry.audioDuration = 0L;
                storyEntry.audioLeft = 0.0f;
                storyEntry.audioRight = 1.0f;
            } else {
                storyEntry.audioPath = message.attachPath;
                storyEntry.audioAuthor = null;
                storyEntry.audioTitle = null;
                TLRPC.Document document = messageObject.getDocument();
                if (document != null) {
                    Iterator<TLRPC.DocumentAttribute> it = document.attributes.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        TLRPC.DocumentAttribute next = it.next();
                        if (next instanceof TLRPC.TL_documentAttributeAudio) {
                            this.entry.audioAuthor = next.performer;
                            if (!TextUtils.isEmpty(next.title)) {
                                this.entry.audioTitle = next.title;
                            }
                            this.entry.audioDuration = (long) (next.duration * 1000.0d);
                        } else if (next instanceof TLRPC.TL_documentAttributeFilename) {
                            this.entry.audioTitle = next.file_name;
                        }
                    }
                }
                StoryEntry storyEntry2 = this.entry;
                storyEntry2.audioOffset = 0L;
                if (storyEntry2.isVideo) {
                    storyEntry2.audioOffset = storyEntry2.left * ((float) getDuration());
                }
                this.entry.audioLeft = 0.0f;
                if (isCollage() && this.collage.hasVideo()) {
                    duration = this.collage.getDuration();
                } else {
                    StoryEntry storyEntry3 = this.entry;
                    duration = storyEntry3.isVideo ? getDuration() : storyEntry3.audioDuration;
                }
                StoryEntry storyEntry4 = this.entry;
                storyEntry4.audioRight = storyEntry4.audioDuration != 0 ? Math.min(1.0f, ((float) Math.min(duration, 59000L)) / ((float) this.entry.audioDuration)) : 1.0f;
            }
        }
        setupAudio(this.entry, z);
    }

    public void setupAudio(StoryEntry storyEntry, boolean z) {
        VideoPlayer videoPlayer = this.audioPlayer;
        if (videoPlayer != null) {
            videoPlayer.pause();
            this.audioPlayer.releasePlayer(true);
            this.audioPlayer = null;
        }
        if (storyEntry == null) {
            return;
        }
        TimelineView timelineView = this.timelineView;
        if (timelineView != null) {
            timelineView.setAudio(storyEntry.audioPath, storyEntry.audioAuthor, storyEntry.audioTitle, storyEntry.audioDuration, storyEntry.audioOffset, storyEntry.audioLeft, storyEntry.audioRight, storyEntry.audioVolume, z);
        }
        if (storyEntry.audioPath != null) {
            VideoPlayer videoPlayer2 = new VideoPlayer();
            this.audioPlayer = videoPlayer2;
            videoPlayer2.allowMultipleInstances = true;
            videoPlayer2.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                AnonymousClass1() {
                }

                @Override
                public void onError(VideoPlayer videoPlayer3, Exception exc) {
                }

                @Override
                public void onRenderedFirstFrame() {
                }

                @Override
                public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                @Override
                public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                @Override
                public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                @Override
                public void onStateChanged(boolean z2, int i) {
                    AndroidUtilities.cancelRunOnUIThread(PreviewView.this.updateAudioProgressRunnable);
                    if (PreviewView.this.audioPlayer == null || !PreviewView.this.audioPlayer.isPlaying()) {
                        return;
                    }
                    AndroidUtilities.runOnUIThread(PreviewView.this.updateAudioProgressRunnable);
                }

                @Override
                public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    PreviewView.this.invalidateTextureViewHolder();
                }

                @Override
                public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                }
            });
            this.audioPlayer.preparePlayer(Uri.fromFile(new File(storyEntry.audioPath)), "other");
            checkVolumes();
            if (this.videoPlayer != null && getDuration() > 0) {
                long duration = storyEntry.left * ((float) getDuration());
                this.videoPlayer.seekTo(duration);
                this.timelineView.setProgress(duration);
            }
            updateAudioPlayer(true);
        }
        onAudioChanged();
    }

    public void setupRound(StoryEntry storyEntry, RoundView roundView, boolean z) {
        if (storyEntry == null || storyEntry.round == null) {
            VideoPlayer videoPlayer = this.roundPlayer;
            if (videoPlayer != null) {
                videoPlayer.pause();
                this.roundPlayer.releasePlayer(true);
                this.roundPlayer = null;
            }
            TimelineView timelineView = this.timelineView;
            if (timelineView != null) {
                timelineView.setRoundNull(z);
            }
            this.roundView = null;
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
            return;
        }
        VideoPlayer videoPlayer2 = this.roundPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.releasePlayer(true);
            this.roundPlayer = null;
        }
        VideoPlayer videoPlayer3 = new VideoPlayer();
        this.roundPlayer = videoPlayer3;
        videoPlayer3.allowMultipleInstances = true;
        videoPlayer3.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
            AnonymousClass4() {
            }

            @Override
            public void onError(VideoPlayer videoPlayer4, Exception exc) {
            }

            @Override
            public void onRenderedFirstFrame() {
            }

            @Override
            public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
            }

            @Override
            public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
            }

            @Override
            public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
            }

            @Override
            public void onStateChanged(boolean z2, int i) {
                if (PreviewView.this.roundPlayer == null) {
                    return;
                }
                if (PreviewView.this.roundPlayer == null || !PreviewView.this.roundPlayer.isPlaying()) {
                    AndroidUtilities.cancelRunOnUIThread(PreviewView.this.updateRoundProgressRunnable);
                } else {
                    AndroidUtilities.runOnUIThread(PreviewView.this.updateRoundProgressRunnable);
                }
            }

            @Override
            public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override
            public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                PreviewView.this.roundPlayerWidth = i;
                PreviewView.this.roundPlayerHeight = i2;
                if (PreviewView.this.roundView != null) {
                    PreviewView.this.roundView.resizeTextureView(i, i2);
                }
            }
        });
        this.roundPlayer.preparePlayer(Uri.fromFile(storyEntry.round), "other");
        checkVolumes();
        attachRoundView(roundView);
        this.timelineView.setRound(storyEntry.round.getAbsolutePath(), storyEntry.roundDuration, storyEntry.roundOffset, storyEntry.roundLeft, storyEntry.roundRight, storyEntry.roundVolume, z);
        updateRoundPlayer(true);
    }

    public void setupVideoPlayer(StoryEntry storyEntry, Runnable runnable, long j) {
        ArrayList arrayList;
        if (storyEntry == null || storyEntry.isCollage()) {
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.pause();
                this.videoPlayer.releasePlayer(true);
                this.videoPlayer = null;
            }
            TextureViewHolder textureViewHolder = this.textureViewHolder;
            if (textureViewHolder == null || !textureViewHolder.active) {
                VideoEditTextureView videoEditTextureView = this.textureView;
                if (videoEditTextureView != null) {
                    videoEditTextureView.clearAnimation();
                    this.textureView.animate().alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public final void run() {
                            PreviewView.this.lambda$setupVideoPlayer$6();
                        }
                    }).start();
                }
            } else {
                textureViewHolder.setTextureView(null);
            }
            TimelineView timelineView = this.timelineView;
            if (timelineView != null) {
                timelineView.setVideo(false, null, 1L, 0.0f);
            }
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
                return;
            }
            return;
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.releasePlayer(true);
            this.videoPlayer = null;
        }
        VideoPlayer videoPlayer3 = new VideoPlayer();
        this.videoPlayer = videoPlayer3;
        videoPlayer3.allowMultipleInstances = true;
        videoPlayer3.setDelegate(new AnonymousClass3(storyEntry, new Runnable[]{runnable}));
        VideoEditTextureView videoEditTextureView2 = this.textureView;
        if (videoEditTextureView2 != null) {
            videoEditTextureView2.clearAnimation();
            this.textureView.release();
            removeView(this.textureView);
            this.textureView = null;
        }
        this.textureView = new VideoEditTextureView(getContext(), this.videoPlayer);
        this.blurManager.resetBitmap();
        this.textureView.updateUiBlurManager(storyEntry.isRepostMessage ? null : this.blurManager);
        this.textureView.setOpaque(false);
        applyMatrix();
        TextureViewHolder textureViewHolder2 = this.textureViewHolder;
        if (textureViewHolder2 == null || !textureViewHolder2.active) {
            this.textureView.setAlpha(runnable != null ? 1.0f : 0.0f);
            addView(this.textureView, LayoutHelper.createFrame(-2, -2, 51));
        } else {
            textureViewHolder2.setTextureView(this.textureView);
        }
        storyEntry.detectHDR(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                PreviewView.this.lambda$setupVideoPlayer$7((StoryEntry.HDRInfo) obj);
            }
        });
        this.videoPlayer.preparePlayer(Uri.fromFile(storyEntry.getOriginalFile()), "other");
        this.videoPlayer.setPlayWhenReady(this.pauseLinks.isEmpty());
        this.videoPlayer.setLooping(true);
        if (storyEntry.isEditSaved) {
            j = (storyEntry.left * ((float) storyEntry.duration)) + ((float) j);
        }
        if (j > 0) {
            this.videoPlayer.seekTo(j);
        }
        checkVolumes();
        updateAudioPlayer(true);
        this.timelineView.setVideo(storyEntry.isRepostMessage && (arrayList = storyEntry.messageObjects) != null && arrayList.size() == 1 && ((MessageObject) storyEntry.messageObjects.get(0)).type == 5, storyEntry.getOriginalFile().getAbsolutePath(), getDuration(), storyEntry.videoVolume);
        this.timelineView.setVideoLeft(storyEntry.left);
        this.timelineView.setVideoRight(storyEntry.right);
        TimelineView timelineView2 = this.timelineView;
        if (timelineView2 == null || j <= 0) {
            return;
        }
        timelineView2.setProgress(j);
    }

    public void setupWallpaper(StoryEntry storyEntry, boolean z) {
        Drawable backgroundDrawable;
        Drawable drawable = this.wallpaperDrawable;
        this.lastWallpaperDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        if (storyEntry == null) {
            this.wallpaperDrawable = null;
            return;
        }
        long j = storyEntry.backgroundWallpaperPeerId;
        String str = storyEntry.backgroundWallpaperEmoticon;
        if (str != null) {
            backgroundDrawable = getBackgroundDrawableFromTheme(storyEntry.currentAccount, str, storyEntry.isDark);
        } else {
            if (j == Long.MIN_VALUE) {
                this.wallpaperDrawable = null;
                return;
            }
            backgroundDrawable = getBackgroundDrawable(this.wallpaperDrawable, storyEntry.currentAccount, j, storyEntry.isDark);
        }
        storyEntry.backgroundDrawable = backgroundDrawable;
        this.wallpaperDrawable = backgroundDrawable;
        if (this.lastWallpaperDrawable != this.wallpaperDrawable) {
            if (z) {
                this.wallpaperDrawableCrossfade.set(0.0f, true);
            } else {
                this.lastWallpaperDrawable = null;
            }
        }
        Drawable drawable2 = this.wallpaperDrawable;
        if (drawable2 != null) {
            drawable2.setCallback(this);
        }
        BlurringShader.BlurManager blurManager = this.blurManager;
        if (blurManager != null) {
            Drawable drawable3 = this.wallpaperDrawable;
            if (drawable3 == null) {
                blurManager.setFallbackBlur(null, 0);
            } else if (drawable3 instanceof BitmapDrawable) {
                blurManager.setFallbackBlur(((BitmapDrawable) drawable3).getBitmap(), 0);
            } else {
                int intrinsicWidth = drawable3.getIntrinsicWidth();
                int intrinsicHeight = this.wallpaperDrawable.getIntrinsicHeight();
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    intrinsicWidth = 1080;
                    intrinsicHeight = 1920;
                }
                float f = intrinsicWidth;
                float f2 = intrinsicHeight;
                float max = Math.max(100.0f / f, 100.0f / f2);
                if (max > 1.0f) {
                    intrinsicWidth = (int) (f * max);
                    intrinsicHeight = (int) (f2 * max);
                }
                Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
                this.wallpaperDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                this.wallpaperDrawable.draw(new Canvas(createBitmap));
                this.blurManager.setFallbackBlur(createBitmap, 0, true);
            }
        }
        invalidate();
    }

    public void updateAudioPlayer(boolean r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.PreviewView.updateAudioPlayer(boolean):void");
    }

    public void updatePauseReason(int i, boolean z) {
        if (z) {
            this.pauseLinks.add(Integer.valueOf(i));
        } else {
            this.pauseLinks.remove(Integer.valueOf(i));
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(this.pauseLinks.isEmpty());
        }
        CollageLayoutView2 collageLayoutView2 = this.collage;
        if (collageLayoutView2 != null) {
            collageLayoutView2.setPlaying(this.pauseLinks.isEmpty());
        }
        updateAudioPlayer(true);
        updateRoundPlayer(true);
    }

    public void updateRoundPlayer(boolean z) {
        long currentPosition;
        boolean isPlaying;
        if (this.roundPlayer == null || this.entry == null) {
            return;
        }
        boolean z2 = false;
        if (this.videoPlayer == null && !isCollage()) {
            this.roundPlayer.setPlayWhenReady(this.pauseLinks.isEmpty());
            this.roundPlayer.setLooping(true);
            RoundView roundView = this.roundView;
            if (roundView != null) {
                roundView.setShown(true, false);
            }
            long currentPosition2 = this.roundPlayer.getCurrentPosition();
            if (!z || this.roundPlayer.getDuration() == -9223372036854775807L) {
                return;
            }
            float duration = ((float) currentPosition2) / ((float) this.roundPlayer.getDuration());
            StoryEntry storyEntry = this.entry;
            if ((duration < storyEntry.roundLeft || duration > storyEntry.roundRight) && System.currentTimeMillis() - this.seekedLastTime > 500) {
                this.seekedLastTime = System.currentTimeMillis();
                this.roundPlayer.seekTo(-this.entry.roundOffset);
                return;
            }
            return;
        }
        if (isCollage()) {
            currentPosition = this.collage.getPositionWithOffset();
            isPlaying = this.collage.isPlaying();
        } else {
            currentPosition = this.videoPlayer.getCurrentPosition();
            isPlaying = this.videoPlayer.isPlaying();
        }
        StoryEntry storyEntry2 = this.entry;
        float f = storyEntry2.roundRight;
        float f2 = storyEntry2.roundLeft;
        long j = (f - f2) * ((float) storyEntry2.roundDuration);
        long j2 = storyEntry2.roundOffset;
        boolean z3 = currentPosition >= j2 && currentPosition <= j + j2;
        if (isPlaying && z3) {
            z2 = true;
        }
        long j3 = (currentPosition - j2) + (f2 * r8);
        RoundView roundView2 = this.roundView;
        if (roundView2 != null) {
            roundView2.setShown(z3, true);
        }
        if (this.roundPlayer.isPlaying() != z2) {
            this.roundPlayer.setPlayWhenReady(z2);
        } else {
            if (!z) {
                return;
            }
            if (Math.abs(this.roundPlayer.getCurrentPosition() - j3) <= (isCollage() ? 300 : 120)) {
                return;
            }
        }
        this.roundPlayer.seekTo(j3);
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return this.wallpaperDrawable == drawable || super.verifyDrawable(drawable);
    }

    public void whenError(Runnable runnable) {
        this.onErrorListener = runnable;
    }
}
