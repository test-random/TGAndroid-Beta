package org.telegram.messenger.video;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.view.SurfaceView;
import android.view.TextureView;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileStreamLoadOperation;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.video.VideoPlayerHolderBase;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.VideoPlayer;

public class VideoPlayerHolderBase {
    boolean audioDisabled;
    Uri contentUri;
    private int currentAccount;
    public long currentPosition;
    public TLRPC.Document document;
    private volatile long duration;
    public boolean firstFrameRendered;
    Runnable initRunnable;
    int lastState;
    private Runnable onReadyListener;
    private Runnable onSeekUpdate;
    public boolean paused;
    public long pendingSeekTo;
    long playerDuration;
    public Bitmap playerStubBitmap;
    public Paint playerStubPaint;
    public float progress;
    public volatile boolean released;
    private volatile boolean seeking;
    long startTime;
    public boolean stubAvailable;
    private SurfaceView surfaceView;
    private TextureView textureView;
    public Uri uri;
    VideoPlayer videoPlayer;
    final DispatchQueue dispatchQueue = Utilities.getOrCreatePlayerQueue();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            VideoPlayerHolderBase videoPlayerHolderBase = VideoPlayerHolderBase.this;
            VideoPlayer videoPlayer = videoPlayerHolderBase.videoPlayer;
            if (videoPlayer != null) {
                if (videoPlayerHolderBase.lastState == 4) {
                    videoPlayerHolderBase.progress = 1.0f;
                } else {
                    videoPlayerHolderBase.currentPosition = videoPlayer.getCurrentPosition();
                    VideoPlayerHolderBase videoPlayerHolderBase2 = VideoPlayerHolderBase.this;
                    videoPlayerHolderBase2.playerDuration = videoPlayerHolderBase2.videoPlayer.getDuration();
                }
                VideoPlayerHolderBase videoPlayerHolderBase3 = VideoPlayerHolderBase.this;
                if (videoPlayerHolderBase3.lastState == 3) {
                    videoPlayerHolderBase3.dispatchQueue.cancelRunnable(videoPlayerHolderBase3.progressRunnable);
                    VideoPlayerHolderBase videoPlayerHolderBase4 = VideoPlayerHolderBase.this;
                    videoPlayerHolderBase4.dispatchQueue.postRunnable(videoPlayerHolderBase4.progressRunnable, 16L);
                }
            }
        }
    };
    private volatile int triesCount = 3;
    private volatile boolean firstSeek = true;
    private volatile long lastSeek = -1;
    private long lastBetterSeek = -1;
    public float currentSeek = 0.0f;
    public volatile float currentSeekThread = 0.0f;
    private final Runnable betterSeek = new Runnable() {
        @Override
        public final void run() {
            VideoPlayerHolderBase.this.lambda$new$12();
        }
    };
    private final Runnable updateSeek = new Runnable() {
        @Override
        public final void run() {
            VideoPlayerHolderBase.this.lambda$new$13();
        }
    };

    public class AnonymousClass2 implements VideoPlayer.VideoPlayerDelegate {
        AnonymousClass2() {
        }

        public void lambda$onError$0(long j) {
            VideoPlayerHolderBase videoPlayerHolderBase;
            Uri uri;
            if (VideoPlayerHolderBase.this.released || (uri = (videoPlayerHolderBase = VideoPlayerHolderBase.this).uri) == null) {
                return;
            }
            videoPlayerHolderBase.videoPlayer.preparePlayer(uri, "other");
            VideoPlayerHolderBase.this.videoPlayer.seekTo(j);
        }

        public void lambda$onRenderedFirstFrame$1() {
            if (VideoPlayerHolderBase.this.released) {
                return;
            }
            VideoPlayerHolderBase.this.onRenderedFirstFrame();
            if (VideoPlayerHolderBase.this.onReadyListener != null) {
                VideoPlayerHolderBase.this.onReadyListener.run();
                VideoPlayerHolderBase.this.onReadyListener = null;
            }
        }

        @Override
        public void onError(VideoPlayer videoPlayer, Exception exc) {
            FileLog.e(exc);
            final long currentPosition = VideoPlayerHolderBase.this.getCurrentPosition();
            VideoPlayerHolderBase.access$010(VideoPlayerHolderBase.this);
            if (VideoPlayerHolderBase.this.triesCount > 0) {
                VideoPlayerHolderBase videoPlayerHolderBase = VideoPlayerHolderBase.this;
                DispatchQueue dispatchQueue = videoPlayerHolderBase.dispatchQueue;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        VideoPlayerHolderBase.AnonymousClass2.this.lambda$onError$0(currentPosition);
                    }
                };
                videoPlayerHolderBase.initRunnable = runnable;
                dispatchQueue.postRunnable(runnable);
            }
        }

        @Override
        public void onRenderedFirstFrame() {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VideoPlayerHolderBase.AnonymousClass2.this.lambda$onRenderedFirstFrame$1();
                }
            }, VideoPlayerHolderBase.this.surfaceView == null ? 16L : 32L);
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
            VideoPlayerHolderBase videoPlayerHolderBase = VideoPlayerHolderBase.this;
            videoPlayerHolderBase.lastState = i;
            if (i == 3 || i == 2) {
                videoPlayerHolderBase.dispatchQueue.cancelRunnable(videoPlayerHolderBase.progressRunnable);
                VideoPlayerHolderBase videoPlayerHolderBase2 = VideoPlayerHolderBase.this;
                videoPlayerHolderBase2.dispatchQueue.postRunnable(videoPlayerHolderBase2.progressRunnable);
            } else if (i == 4) {
                if (videoPlayerHolderBase.needRepeat()) {
                    VideoPlayerHolderBase videoPlayerHolderBase3 = VideoPlayerHolderBase.this;
                    videoPlayerHolderBase3.progress = 0.0f;
                    videoPlayerHolderBase3.videoPlayer.seekTo(0L);
                    VideoPlayerHolderBase.this.videoPlayer.play();
                } else {
                    VideoPlayerHolderBase.this.progress = 1.0f;
                }
            }
            VideoPlayerHolderBase.this.onStateChanged(z, i);
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
        }
    }

    static int access$010(VideoPlayerHolderBase videoPlayerHolderBase) {
        int i = videoPlayerHolderBase.triesCount;
        videoPlayerHolderBase.triesCount = i - 1;
        return i;
    }

    private void ensurePlayerCreated(boolean z) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.releasePlayer(true);
        }
        VideoPlayer videoPlayer2 = new VideoPlayer(false, z);
        this.videoPlayer = videoPlayer2;
        videoPlayer2.setDelegate(new AnonymousClass2());
        this.videoPlayer.setIsStory();
    }

    public void lambda$loopBack$9() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.seekTo(0L);
        }
        this.progress = 0.0f;
        this.currentPosition = 0L;
    }

    public void lambda$new$12() {
    }

    public void lambda$new$13() {
        if (this.videoPlayer == null) {
            return;
        }
        long j = this.currentSeekThread * ((float) this.duration);
        if (this.lastSeek <= -1) {
            this.lastSeek = j;
        }
        if (Math.abs(j - this.lastSeek) >= (this.firstSeek ? 350 : 40)) {
            this.firstSeek = false;
            this.lastBetterSeek = j;
            this.dispatchQueue.cancelRunnable(this.betterSeek);
            this.dispatchQueue.postRunnable(this.betterSeek, 300L);
            VideoPlayer videoPlayer = this.videoPlayer;
            this.lastSeek = j;
            videoPlayer.seekTo(j, true);
        }
    }

    public void lambda$pause$4() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    public void lambda$play$6() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            SurfaceView surfaceView = this.surfaceView;
            if (surfaceView != null) {
                videoPlayer.setSurfaceView(surfaceView);
            } else {
                videoPlayer.setTextureView(this.textureView);
            }
            long j = this.pendingSeekTo;
            if (j > 0) {
                this.videoPlayer.seekTo(j);
                this.pendingSeekTo = 0L;
            }
            this.videoPlayer.setPlayWhenReady(true);
        }
    }

    public void lambda$play$7(float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            SurfaceView surfaceView = this.surfaceView;
            if (surfaceView != null) {
                videoPlayer.setSurfaceView(surfaceView);
            } else {
                videoPlayer.setTextureView(this.textureView);
            }
            long j = this.pendingSeekTo;
            if (j > 0) {
                this.videoPlayer.seekTo(j);
                this.pendingSeekTo = 0L;
            }
            this.videoPlayer.setPlaybackSpeed(f);
            this.videoPlayer.setPlayWhenReady(true);
        }
    }

    public void lambda$preparePlayer$0(boolean z, float f, Uri uri) {
        if (this.released) {
            return;
        }
        ensurePlayerCreated(z);
        this.videoPlayer.setPlaybackSpeed(f);
        FileLog.d("videoplayerholderbase.preparePlayer(): preparePlayer new player as preload uri=" + uri);
        this.videoPlayer.preparePlayer(uri, "other", 0);
        this.videoPlayer.setPlayWhenReady(false);
        this.videoPlayer.setWorkerQueue(this.dispatchQueue);
    }

    public void lambda$release$3(TLRPC.Document document, Runnable runnable) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setTextureView(null);
            this.videoPlayer.setSurfaceView(null);
            this.videoPlayer.releasePlayer(false);
        }
        if (document != null) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
        this.videoPlayer = null;
    }

    public void lambda$seekTo$11(long j) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            this.pendingSeekTo = j;
        } else {
            videoPlayer.seekTo(j);
        }
    }

    public void lambda$setAudioEnabled$8(boolean z, boolean z2) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        boolean isPlaying = videoPlayer.isPlaying();
        if (!z || this.videoPlayer.createdWithAudioTrack()) {
            this.videoPlayer.setVolume(z ? 1.0f : 0.0f);
            return;
        }
        this.videoPlayer.pause();
        long currentPosition = this.videoPlayer.getCurrentPosition();
        this.videoPlayer.releasePlayer(false);
        this.videoPlayer = null;
        ensurePlayerCreated(this.audioDisabled);
        FileLog.d("videoplayerholderbase.setAudioEnabled(): repreparePlayer as audio track is enabled back uri=" + this.uri);
        this.videoPlayer.preparePlayer(this.uri, "other");
        this.videoPlayer.setWorkerQueue(this.dispatchQueue);
        if (!z2) {
            SurfaceView surfaceView = this.surfaceView;
            if (surfaceView != null) {
                this.videoPlayer.setSurfaceView(surfaceView);
            } else {
                this.videoPlayer.setTextureView(this.textureView);
            }
        }
        this.videoPlayer.seekTo(currentPosition + 50);
        if (!isPlaying || z2) {
            this.videoPlayer.setPlayWhenReady(false);
            this.videoPlayer.pause();
        } else {
            this.videoPlayer.setPlayWhenReady(true);
            this.videoPlayer.play();
        }
    }

    public void lambda$setSpeed$5(float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setPlaybackSpeed(f);
        }
    }

    public void lambda$setVolume$10(float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setVolume(f);
        }
    }

    public void lambda$start$1() {
        this.initRunnable = null;
    }

    public void lambda$start$2(boolean z, float f, Uri uri, boolean z2, long j) {
        if (this.released) {
            FileLog.d("videoplayerholderbase returned from start: released");
            return;
        }
        if (this.videoPlayer == null) {
            ensurePlayerCreated(z);
            this.videoPlayer.setPlaybackSpeed(f);
            FileLog.d("videoplayerholderbase.start(): preparePlayer new player uri=" + uri);
            this.videoPlayer.preparePlayer(uri, "other");
            this.videoPlayer.setWorkerQueue(this.dispatchQueue);
            if (!z2) {
                SurfaceView surfaceView = this.surfaceView;
                if (surfaceView != null) {
                    this.videoPlayer.setSurfaceView(surfaceView);
                } else {
                    this.videoPlayer.setTextureView(this.textureView);
                }
                this.videoPlayer.setPlayWhenReady(true);
            }
        } else {
            FileLog.d("videoplayerholderbase.start(): player already exist");
            if (!z2) {
                SurfaceView surfaceView2 = this.surfaceView;
                if (surfaceView2 != null) {
                    this.videoPlayer.setSurfaceView(surfaceView2);
                } else {
                    this.videoPlayer.setTextureView(this.textureView);
                }
                this.videoPlayer.play();
            }
        }
        if (j > 0) {
            this.videoPlayer.seekTo(j);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$start$1();
            }
        });
    }

    public long getCurrentPosition() {
        return this.currentPosition;
    }

    public Uri getCurrentUri() {
        return this.contentUri;
    }

    public long getDuration() {
        return this.playerDuration;
    }

    public float getPlaybackProgress(long j) {
        if (this.lastState == 4) {
            this.progress = 1.0f;
        } else {
            this.progress = j != 0 ? ((float) this.currentPosition) / ((float) j) : ((float) this.currentPosition) / ((float) this.playerDuration);
            if (!this.seeking) {
                this.currentSeek = this.progress;
                this.lastSeek = this.currentPosition;
            }
        }
        return this.progress;
    }

    public boolean isBuffering() {
        return !this.released && this.lastState == 2;
    }

    public boolean isPlaying() {
        return !this.paused;
    }

    public void loopBack() {
        this.progress = 0.0f;
        this.lastState = 1;
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$loopBack$9();
            }
        });
    }

    public boolean needRepeat() {
        return false;
    }

    public void onRenderedFirstFrame() {
    }

    public void onStateChanged(boolean z, int i) {
    }

    public void pause() {
        if (this.released || this.paused) {
            return;
        }
        this.paused = true;
        prepareStub();
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$pause$4();
            }
        });
    }

    public void play() {
        if (!this.released && this.paused) {
            this.paused = false;
            this.dispatchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    VideoPlayerHolderBase.this.lambda$play$6();
                }
            });
        }
    }

    public void play(final float f) {
        if (!this.released && this.paused) {
            this.paused = false;
            this.dispatchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    VideoPlayerHolderBase.this.lambda$play$7(f);
                }
            });
        }
    }

    public void preparePlayer(final Uri uri, final boolean z, final float f) {
        this.audioDisabled = z;
        this.currentAccount = this.currentAccount;
        this.contentUri = uri;
        this.paused = true;
        Runnable runnable = this.initRunnable;
        if (runnable != null) {
            this.dispatchQueue.cancelRunnable(runnable);
        }
        DispatchQueue dispatchQueue = this.dispatchQueue;
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$preparePlayer$0(z, f, uri);
            }
        };
        this.initRunnable = runnable2;
        dispatchQueue.postRunnable(runnable2);
    }

    public void prepareStub() {
        SurfaceView surfaceView = this.surfaceView;
        if (surfaceView != null && this.firstFrameRendered && surfaceView.getHolder().getSurface().isValid()) {
            this.stubAvailable = true;
            if (this.playerStubBitmap == null) {
                this.playerStubBitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
                this.playerStubPaint = new Paint(1);
            }
            if (Build.VERSION.SDK_INT >= 24) {
                AndroidUtilities.getBitmapFromSurface(this.surfaceView, this.playerStubBitmap);
                if (this.playerStubBitmap.getPixel(0, 0) == 0) {
                    this.stubAvailable = false;
                }
            }
        }
    }

    public boolean release(final Runnable runnable) {
        final TLRPC.Document document = this.document;
        if (document != null && FileStreamLoadOperation.getStreamPrioriy(document) != 0) {
            FileStreamLoadOperation.setPriorityForDocument(document, 0);
            FileLoader.getInstance(this.currentAccount).changePriority(0, document, null, null, null, null, null);
        }
        this.released = true;
        this.dispatchQueue.cancelRunnable(this.initRunnable);
        this.initRunnable = null;
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$release$3(document, runnable);
            }
        });
        Bitmap bitmap = this.playerStubBitmap;
        if (bitmap != null) {
            AndroidUtilities.recycleBitmap(bitmap);
            this.playerStubBitmap = null;
        }
        return true;
    }

    public float seek(float f, long j) {
        if (this.videoPlayer == null) {
            return this.currentSeek;
        }
        this.duration = j;
        float clamp = Utilities.clamp(this.currentSeek + f, 1.0f, 0.0f);
        this.currentSeek = clamp;
        this.currentSeekThread = clamp;
        this.dispatchQueue.cancelRunnable(this.updateSeek);
        this.dispatchQueue.postRunnable(this.updateSeek);
        return this.currentSeek;
    }

    public void seekTo(final long j) {
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$seekTo$11(j);
            }
        });
    }

    public void setAudioEnabled(final boolean z, final boolean z2) {
        boolean z3 = !z;
        if (this.audioDisabled == z3) {
            return;
        }
        this.audioDisabled = z3;
        this.triesCount = 3;
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$setAudioEnabled$8(z, z2);
            }
        });
    }

    public void setOnReadyListener(Runnable runnable) {
        this.onReadyListener = runnable;
    }

    public void setOnSeekUpdate(Runnable runnable) {
        this.onSeekUpdate = runnable;
    }

    public void setSeeking(boolean z) {
        if (z && !this.seeking) {
            this.firstSeek = true;
        }
        this.seeking = z;
        if (z) {
            return;
        }
        this.dispatchQueue.cancelRunnable(this.betterSeek);
    }

    public void setSpeed(final float f) {
        if (this.released) {
            return;
        }
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$setSpeed$5(f);
            }
        });
    }

    public void setVolume(final float f) {
        this.dispatchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$setVolume$10(f);
            }
        });
    }

    public void start(final boolean z, final Uri uri, final long j, final boolean z2, final float f) {
        this.startTime = System.currentTimeMillis();
        this.audioDisabled = z2;
        this.paused = z;
        this.triesCount = 3;
        if (j > 0) {
            this.currentPosition = j;
        }
        DispatchQueue dispatchQueue = this.dispatchQueue;
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                VideoPlayerHolderBase.this.lambda$start$2(z2, f, uri, z, j);
            }
        };
        this.initRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public VideoPlayerHolderBase with(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        this.textureView = null;
        return this;
    }

    public VideoPlayerHolderBase with(TextureView textureView) {
        this.surfaceView = null;
        this.textureView = textureView;
        return this;
    }
}
