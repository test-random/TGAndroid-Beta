package org.telegram.messenger.video;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.VideoPlayer;

public class OldVideoPlayerRewinder {
    private long rewindBackSeekPlayerPosition;
    public boolean rewindByBackSeek;
    public int rewindCount;
    private boolean rewindForward;
    private long rewindLastTime;
    private long rewindLastUpdatePlayerTime;
    private long startRewindFrom;
    private Runnable updateRewindRunnable;
    private VideoPlayer videoPlayer;
    private PhotoViewerWebView webView;
    private float playSpeed = 1.0f;
    private final Runnable backSeek = new Runnable() {
        @Override
        public void run() {
            if (OldVideoPlayerRewinder.this.videoPlayer == null && OldVideoPlayerRewinder.this.webView == null) {
                return;
            }
            long duration = OldVideoPlayerRewinder.this.getDuration();
            if (duration == 0 || duration == -9223372036854775807L) {
                OldVideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - OldVideoPlayerRewinder.this.rewindLastTime;
            OldVideoPlayerRewinder.this.rewindLastTime = currentTimeMillis;
            OldVideoPlayerRewinder oldVideoPlayerRewinder = OldVideoPlayerRewinder.this;
            int i = oldVideoPlayerRewinder.rewindCount;
            long j2 = j * (i == 1 ? 3L : i == 2 ? 6L : 12L);
            if (oldVideoPlayerRewinder.rewindForward) {
                OldVideoPlayerRewinder.access$514(OldVideoPlayerRewinder.this, j2);
            } else {
                OldVideoPlayerRewinder.access$522(OldVideoPlayerRewinder.this, j2);
            }
            long j3 = OldVideoPlayerRewinder.this.rewindBackSeekPlayerPosition;
            OldVideoPlayerRewinder oldVideoPlayerRewinder2 = OldVideoPlayerRewinder.this;
            if (j3 < 0) {
                oldVideoPlayerRewinder2.rewindBackSeekPlayerPosition = 0L;
            } else if (oldVideoPlayerRewinder2.rewindBackSeekPlayerPosition > duration) {
                OldVideoPlayerRewinder.this.rewindBackSeekPlayerPosition = duration;
            }
            OldVideoPlayerRewinder oldVideoPlayerRewinder3 = OldVideoPlayerRewinder.this;
            if (oldVideoPlayerRewinder3.rewindByBackSeek && oldVideoPlayerRewinder3.rewindLastTime - OldVideoPlayerRewinder.this.rewindLastUpdatePlayerTime > 350) {
                OldVideoPlayerRewinder oldVideoPlayerRewinder4 = OldVideoPlayerRewinder.this;
                oldVideoPlayerRewinder4.rewindLastUpdatePlayerTime = oldVideoPlayerRewinder4.rewindLastTime;
                OldVideoPlayerRewinder oldVideoPlayerRewinder5 = OldVideoPlayerRewinder.this;
                oldVideoPlayerRewinder5.seekTo(oldVideoPlayerRewinder5.rewindBackSeekPlayerPosition);
            }
            long j4 = OldVideoPlayerRewinder.this.rewindBackSeekPlayerPosition - OldVideoPlayerRewinder.this.startRewindFrom;
            float duration2 = ((float) OldVideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) OldVideoPlayerRewinder.this.getDuration());
            OldVideoPlayerRewinder oldVideoPlayerRewinder6 = OldVideoPlayerRewinder.this;
            oldVideoPlayerRewinder6.updateRewindProgressUi(j4, duration2, oldVideoPlayerRewinder6.rewindByBackSeek);
            if (OldVideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || OldVideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= duration) {
                OldVideoPlayerRewinder oldVideoPlayerRewinder7 = OldVideoPlayerRewinder.this;
                if (oldVideoPlayerRewinder7.rewindByBackSeek) {
                    oldVideoPlayerRewinder7.rewindLastUpdatePlayerTime = oldVideoPlayerRewinder7.rewindLastTime;
                    OldVideoPlayerRewinder oldVideoPlayerRewinder8 = OldVideoPlayerRewinder.this;
                    oldVideoPlayerRewinder8.seekTo(oldVideoPlayerRewinder8.rewindBackSeekPlayerPosition);
                }
                OldVideoPlayerRewinder.this.cancelRewind();
            }
            OldVideoPlayerRewinder oldVideoPlayerRewinder9 = OldVideoPlayerRewinder.this;
            if (oldVideoPlayerRewinder9.rewindCount > 0) {
                AndroidUtilities.runOnUIThread(oldVideoPlayerRewinder9.backSeek, 16L);
            }
        }
    };

    static long access$514(OldVideoPlayerRewinder oldVideoPlayerRewinder, long j) {
        long j2 = oldVideoPlayerRewinder.rewindBackSeekPlayerPosition + j;
        oldVideoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    static long access$522(OldVideoPlayerRewinder oldVideoPlayerRewinder, long j) {
        long j2 = oldVideoPlayerRewinder.rewindBackSeekPlayerPosition - j;
        oldVideoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    private long getCurrentPosition() {
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

    private void incrementRewindCount() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.OldVideoPlayerRewinder.incrementRewindCount():void");
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

    public void lambda$incrementRewindCount$0() {
        this.updateRewindRunnable = null;
        incrementRewindCount();
    }

    public void seekTo(long j) {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            photoViewerWebView.seekTo(j);
            return;
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.seekTo(j);
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
        if (this.rewindCount != 0) {
            this.rewindCount = 0;
            if (this.videoPlayer != null || this.webView != null) {
                seekTo(this.rewindByBackSeek ? this.rewindBackSeekPlayerPosition : getCurrentPosition());
                setPlaybackSpeed(this.playSpeed);
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.backSeek);
        Runnable runnable = this.updateRewindRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRewindRunnable = null;
        }
        onRewindCanceled();
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) getDuration());
    }

    protected void onRewindCanceled() {
    }

    protected void onRewindStart(boolean z) {
    }

    public void startRewind(PhotoViewerWebView photoViewerWebView, boolean z, float f) {
        this.webView = photoViewerWebView;
        this.playSpeed = f;
        this.rewindForward = z;
        cancelRewind();
        incrementRewindCount();
    }

    public void startRewind(VideoPlayer videoPlayer, boolean z, float f) {
        this.videoPlayer = videoPlayer;
        this.playSpeed = f;
        this.rewindForward = z;
        cancelRewind();
        incrementRewindCount();
    }

    protected void updateRewindProgressUi(long j, float f, boolean z) {
    }
}
