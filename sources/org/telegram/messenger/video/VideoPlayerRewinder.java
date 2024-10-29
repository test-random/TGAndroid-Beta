package org.telegram.messenger.video;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.SeekSpeedDrawable;
import org.telegram.ui.Components.VideoPlayer;

public class VideoPlayerRewinder {
    private boolean fastSeeking;
    private long rewindBackSeekLastPlayerPosition;
    private long rewindBackSeekPlayerPosition;
    public boolean rewindByBackSeek;
    public int rewindCount;
    private boolean rewindForward;
    private long rewindLastTime;
    private long rewindLastUpdatePlayerTime;
    public boolean rewinding;
    private SeekSpeedDrawable seekSpeedDrawable;
    private long startRewindFrom;
    private Runnable updateRewindRunnable;
    private float value;
    private VideoPlayer videoPlayer;
    private boolean wasMuted;
    private boolean wasPaused;
    private PhotoViewerWebView webView;
    private float x;
    private float playSpeed = 1.0f;
    private final Runnable backSeek = new Runnable() {
        @Override
        public void run() {
            if (VideoPlayerRewinder.this.videoPlayer == null && VideoPlayerRewinder.this.webView == null) {
                return;
            }
            long duration = VideoPlayerRewinder.this.getDuration();
            if (duration == 0 || duration == -9223372036854775807L) {
                VideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - VideoPlayerRewinder.this.rewindLastTime;
            VideoPlayerRewinder.this.rewindLastTime = currentTimeMillis;
            VideoPlayerRewinder.access$522(VideoPlayerRewinder.this, ((float) j) * Math.max(0.0f, (-VideoPlayerRewinder.this.getRewindSpeed()) * VideoPlayerRewinder.this.playSpeed));
            VideoPlayerRewinder videoPlayerRewinder = VideoPlayerRewinder.this;
            videoPlayerRewinder.rewindBackSeekPlayerPosition = Utilities.clamp(videoPlayerRewinder.rewindBackSeekPlayerPosition, duration, 0L);
            boolean z = Math.abs((-VideoPlayerRewinder.this.getRewindSpeed()) * VideoPlayerRewinder.this.playSpeed) > 2.0f;
            VideoPlayerRewinder videoPlayerRewinder2 = VideoPlayerRewinder.this;
            if (videoPlayerRewinder2.rewindByBackSeek && videoPlayerRewinder2.getCurrentPosition() > VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) {
                if (VideoPlayerRewinder.this.rewindLastTime - VideoPlayerRewinder.this.rewindLastUpdatePlayerTime > (z ? 50 : 350)) {
                    VideoPlayerRewinder videoPlayerRewinder3 = VideoPlayerRewinder.this;
                    videoPlayerRewinder3.rewindLastUpdatePlayerTime = videoPlayerRewinder3.rewindLastTime;
                    VideoPlayerRewinder videoPlayerRewinder4 = VideoPlayerRewinder.this;
                    videoPlayerRewinder4.seekTo(videoPlayerRewinder4.rewindBackSeekPlayerPosition, z);
                }
            }
            long j2 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition - VideoPlayerRewinder.this.startRewindFrom;
            float duration2 = ((float) VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) VideoPlayerRewinder.this.getDuration());
            VideoPlayerRewinder videoPlayerRewinder5 = VideoPlayerRewinder.this;
            videoPlayerRewinder5.updateRewindProgressUi(j2, duration2, videoPlayerRewinder5.rewindByBackSeek);
            if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || VideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= duration) {
                VideoPlayerRewinder videoPlayerRewinder6 = VideoPlayerRewinder.this;
                if (videoPlayerRewinder6.rewindByBackSeek) {
                    videoPlayerRewinder6.rewindLastUpdatePlayerTime = videoPlayerRewinder6.rewindLastTime;
                    VideoPlayerRewinder videoPlayerRewinder7 = VideoPlayerRewinder.this;
                    videoPlayerRewinder7.seekTo(videoPlayerRewinder7.rewindBackSeekPlayerPosition, false);
                }
                VideoPlayerRewinder.this.cancelRewind();
            }
            VideoPlayerRewinder videoPlayerRewinder8 = VideoPlayerRewinder.this;
            if (!videoPlayerRewinder8.rewinding || videoPlayerRewinder8.getRewindSpeed() >= 0.0f) {
                return;
            }
            AndroidUtilities.runOnUIThread(VideoPlayerRewinder.this.backSeek, 16L);
        }
    };

    static long access$522(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition - j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    public long getCurrentPosition() {
        if (this.webView != null) {
            return r0.getCurrentPosition();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        if (this.webView != null) {
            return r0.getVideoDuration();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getDuration();
    }

    private boolean isPlaying() {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            return photoViewerWebView.isPlaying();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return false;
        }
        return videoPlayer.isPlaying();
    }

    public void seekTo(long j, boolean z) {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            photoViewerWebView.seekTo(j);
        } else {
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.seekTo(j, z);
            }
        }
        this.rewindBackSeekLastPlayerPosition = j;
    }

    private void setMuted(boolean z) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setMute(z);
        }
    }

    private void setPaused(boolean z) {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            if (z) {
                photoViewerWebView.pauseVideo();
                return;
            } else {
                photoViewerWebView.playVideo();
                return;
            }
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            if (z) {
                videoPlayer.pause();
            } else {
                videoPlayer.play();
            }
        }
    }

    private void setPlaybackSpeed(float f) {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            photoViewerWebView.setPlaybackSpeed(f);
            return;
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setPlaybackSpeed(f);
    }

    public void cancelRewind() {
        if (this.rewinding) {
            this.rewinding = false;
            this.fastSeeking = false;
            if (this.videoPlayer != null || this.webView != null) {
                seekTo(this.rewindByBackSeek ? this.rewindBackSeekPlayerPosition : getCurrentPosition(), false);
                setPlaybackSpeed(this.playSpeed);
            }
            setMuted(this.wasMuted);
            setPaused(this.wasPaused);
            AndroidUtilities.cancelRunOnUIThread(this.backSeek);
            Runnable runnable = this.updateRewindRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.updateRewindRunnable = null;
            }
            onRewindCanceled();
            SeekSpeedDrawable seekSpeedDrawable = this.seekSpeedDrawable;
            if (seekSpeedDrawable != null) {
                seekSpeedDrawable.setShown(false, true);
            }
        }
    }

    public float getRewindSpeed() {
        float f = this.value;
        if (f < 0.4f) {
            f -= 1.9f;
        }
        return Utilities.clamp(f, 10.0f, -10.0f);
    }

    public float getValueBySpeed(float f) {
        return f < -1.5f ? f + 1.9f : f;
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) getDuration());
    }

    protected void onRewindCanceled() {
    }

    protected void onRewindStart(boolean z) {
    }

    public void setX(float f) {
        this.value -= (this.x - f) / AndroidUtilities.dp(35.0f);
        this.x = f;
        SeekSpeedDrawable seekSpeedDrawable = this.seekSpeedDrawable;
        if (seekSpeedDrawable != null) {
            seekSpeedDrawable.setSpeed(getRewindSpeed(), true);
        }
        updateRewindSpeed();
    }

    public void startRewind(PhotoViewerWebView photoViewerWebView, boolean z, float f, float f2, SeekSpeedDrawable seekSpeedDrawable) {
        cancelRewind();
        this.videoPlayer = null;
        this.rewindByBackSeek = z;
        this.rewinding = true;
        this.webView = photoViewerWebView;
        this.seekSpeedDrawable = seekSpeedDrawable;
        this.playSpeed = f2;
        this.wasMuted = false;
        this.wasPaused = (photoViewerWebView == null || photoViewerWebView.isPlaying()) ? false : true;
        this.fastSeeking = false;
        this.rewindLastUpdatePlayerTime = 0L;
        this.x = f;
        this.value = getValueBySpeed(z ? 2.0f : -2.0f);
        this.rewindBackSeekLastPlayerPosition = -100L;
        if (seekSpeedDrawable != null) {
            seekSpeedDrawable.setSpeed(getRewindSpeed(), false);
            seekSpeedDrawable.setShown(true, true);
        }
    }

    public void startRewind(VideoPlayer videoPlayer, boolean z, float f, float f2, SeekSpeedDrawable seekSpeedDrawable) {
        cancelRewind();
        this.webView = null;
        this.rewindByBackSeek = z;
        this.rewinding = true;
        this.videoPlayer = videoPlayer;
        this.seekSpeedDrawable = seekSpeedDrawable;
        this.playSpeed = f2;
        this.wasMuted = videoPlayer != null && videoPlayer.isMuted();
        this.wasPaused = (videoPlayer == null || videoPlayer.isPlaying()) ? false : true;
        this.fastSeeking = false;
        this.rewindLastUpdatePlayerTime = 0L;
        this.x = f;
        this.value = getValueBySpeed(z ? 2.0f : -2.0f);
        this.rewindBackSeekLastPlayerPosition = -100L;
        if (seekSpeedDrawable != null) {
            seekSpeedDrawable.setSpeed(getRewindSpeed(), false);
            seekSpeedDrawable.setShown(true, true);
        }
        updateRewindSpeed();
    }

    protected void updateRewindProgressUi(long j, float f, boolean z) {
    }

    public void updateRewindSpeed() {
        float rewindSpeed = getRewindSpeed();
        boolean z = true;
        if (rewindSpeed < 0.0f) {
            if (this.rewindByBackSeek) {
                return;
            }
            this.rewindByBackSeek = true;
            this.rewindBackSeekPlayerPosition = getCurrentPosition();
            this.rewindLastTime = System.currentTimeMillis();
            AndroidUtilities.runOnUIThread(this.backSeek);
            setMuted(true);
            setPaused(true);
            setPlaybackSpeed(this.playSpeed);
            return;
        }
        if (this.rewindByBackSeek) {
            this.rewindByBackSeek = false;
            AndroidUtilities.cancelRunOnUIThread(this.backSeek);
            if (!this.wasMuted && !this.wasPaused) {
                z = false;
            }
            setMuted(z);
            setPaused(false);
        }
        setPlaybackSpeed(this.playSpeed * rewindSpeed);
    }
}
