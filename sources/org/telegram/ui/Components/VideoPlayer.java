package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGLContext;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LongSparseArray;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.mediacodec.MediaCodecDecoderException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.video.ColorInfo;
import com.google.android.exoplayer2.video.SurfaceNotValidException;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoSize;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FourierTransform;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Stories.recorder.StoryEntry;

public class VideoPlayer implements Player.Listener, VideoListener, AnalyticsListener, NotificationCenter.NotificationCenterDelegate {
    private static HashMap cachedSupportedCodec;
    private static int lastPlayerId;
    public boolean allowMultipleInstances;
    boolean audioDisabled;
    private ExoPlayer audioPlayer;
    private boolean audioPlayerReady;
    private String audioType;
    Handler audioUpdateHandler;
    private Uri audioUri;
    private AudioVisualizerDelegate audioVisualizerDelegate;
    private boolean autoIsOriginal;
    private boolean autoplay;
    private boolean currentStreamIsHls;
    private Uri currentUri;
    MediaSource.Factory dashMediaSourceFactory;
    private VideoPlayerDelegate delegate;
    private EGLContext eglParentContext;
    private long fallbackDuration;
    private long fallbackPosition;
    private boolean handleAudioFocus;
    HlsMediaSource.Factory hlsMediaSourceFactory;
    private boolean isStory;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private Looper looper;
    private boolean looping;
    private boolean loopingMediaSource;
    private ArrayList manifestUris;
    private ExtendedDefaultDataSourceFactory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private Runnable onQualityChangeListener;
    public ExoPlayer player;
    private int playerId;
    ProgressiveMediaSource.Factory progressiveMediaSourceFactory;
    private int repeatCount;
    private final ArrayList seekFinishedListeners;
    private int selectedQualityIndex;
    private boolean shouldPauseOther;
    SsMediaSource.Factory ssMediaSourceFactory;
    private Surface surface;
    private SurfaceView surfaceView;
    private TextureView textureView;
    private MappingTrackSelector trackSelector;
    private boolean triedReinit;
    private boolean videoPlayerReady;
    private ArrayList videoQualities;
    private Quality videoQualityToSelect;
    private String videoType;
    private Uri videoUri;
    private DispatchQueue workerQueue;
    public static final HashSet activePlayers = new HashSet();
    static int playerCounter = 0;

    public class AnonymousClass1 implements Player.Listener {
        AnonymousClass1() {
        }

        @Override
        public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
            Player.Listener.CC.$default$onAudioAttributesChanged(this, audioAttributes);
        }

        @Override
        public void onAvailableCommandsChanged(Player.Commands commands) {
            Player.Listener.CC.$default$onAvailableCommandsChanged(this, commands);
        }

        @Override
        public void onCues(CueGroup cueGroup) {
            Player.Listener.CC.$default$onCues(this, cueGroup);
        }

        @Override
        public void onCues(List list) {
            Player.Listener.CC.$default$onCues(this, list);
        }

        @Override
        public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
            Player.Listener.CC.$default$onDeviceInfoChanged(this, deviceInfo);
        }

        @Override
        public void onDeviceVolumeChanged(int i, boolean z) {
            Player.Listener.CC.$default$onDeviceVolumeChanged(this, i, z);
        }

        @Override
        public void onEvents(Player player, Player.Events events) {
            Player.Listener.CC.$default$onEvents(this, player, events);
        }

        @Override
        public void onIsLoadingChanged(boolean z) {
            Player.Listener.CC.$default$onIsLoadingChanged(this, z);
        }

        @Override
        public void onIsPlayingChanged(boolean z) {
            Player.Listener.CC.$default$onIsPlayingChanged(this, z);
        }

        @Override
        public void onLoadingChanged(boolean z) {
            Player.Listener.CC.$default$onLoadingChanged(this, z);
        }

        @Override
        public void onMediaItemTransition(MediaItem mediaItem, int i) {
            Player.Listener.CC.$default$onMediaItemTransition(this, mediaItem, i);
        }

        @Override
        public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
            Player.Listener.CC.$default$onMediaMetadataChanged(this, mediaMetadata);
        }

        @Override
        public void onMetadata(Metadata metadata) {
            Player.Listener.CC.$default$onMetadata(this, metadata);
        }

        @Override
        public void onPlayWhenReadyChanged(boolean z, int i) {
            Player.Listener.CC.$default$onPlayWhenReadyChanged(this, z, i);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Player.Listener.CC.$default$onPlaybackParametersChanged(this, playbackParameters);
        }

        @Override
        public void onPlaybackStateChanged(int i) {
            Player.Listener.CC.$default$onPlaybackStateChanged(this, i);
        }

        @Override
        public void onPlaybackSuppressionReasonChanged(int i) {
            Player.Listener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
        }

        @Override
        public void onPlayerError(PlaybackException playbackException) {
            Player.Listener.CC.$default$onPlayerError(this, playbackException);
        }

        @Override
        public void onPlayerErrorChanged(PlaybackException playbackException) {
            Player.Listener.CC.$default$onPlayerErrorChanged(this, playbackException);
        }

        @Override
        public void onPlayerStateChanged(boolean z, int i) {
            if (VideoPlayer.this.audioPlayerReady || i != 3) {
                return;
            }
            VideoPlayer.this.audioPlayerReady = true;
            VideoPlayer.this.checkPlayersReady();
        }

        @Override
        public void onPositionDiscontinuity(int i) {
            Player.Listener.CC.$default$onPositionDiscontinuity(this, i);
        }

        @Override
        public void onPositionDiscontinuity(Player.PositionInfo positionInfo, Player.PositionInfo positionInfo2, int i) {
            Player.Listener.CC.$default$onPositionDiscontinuity(this, positionInfo, positionInfo2, i);
        }

        @Override
        public void onRenderedFirstFrame() {
            Player.Listener.CC.$default$onRenderedFirstFrame(this);
        }

        @Override
        public void onRepeatModeChanged(int i) {
            Player.Listener.CC.$default$onRepeatModeChanged(this, i);
        }

        @Override
        public void onSeekProcessed() {
            Player.Listener.CC.$default$onSeekProcessed(this);
        }

        @Override
        public void onSkipSilenceEnabledChanged(boolean z) {
            Player.Listener.CC.$default$onSkipSilenceEnabledChanged(this, z);
        }

        @Override
        public void onSurfaceSizeChanged(int i, int i2) {
            Player.Listener.CC.$default$onSurfaceSizeChanged(this, i, i2);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, int i) {
            Player.Listener.CC.$default$onTimelineChanged(this, timeline, i);
        }

        @Override
        public void onTracksChanged(Tracks tracks) {
            Player.Listener.CC.$default$onTracksChanged(this, tracks);
        }

        @Override
        public void onVideoSizeChanged(VideoSize videoSize) {
            Player.Listener.CC.$default$onVideoSizeChanged(this, videoSize);
        }

        @Override
        public void onVolumeChanged(float f) {
            Player.Listener.CC.$default$onVolumeChanged(this, f);
        }
    }

    public interface AudioVisualizerDelegate {
        boolean needUpdate();

        void onVisualizerUpdate(boolean z, boolean z2, float[] fArr);
    }

    public class AudioVisualizerRenderersFactory extends DefaultRenderersFactory {
        public AudioVisualizerRenderersFactory(Context context) {
            super(context);
        }

        @Override
        protected AudioSink buildAudioSink(Context context, boolean z, boolean z2, boolean z3) {
            return new DefaultAudioSink.Builder().setAudioCapabilities(AudioCapabilities.getCapabilities(context)).setEnableFloatOutput(z).setEnableAudioTrackPlaybackParams(z2).setAudioProcessors(new AudioProcessor[]{new TeeAudioProcessor(new VisualizerBufferSink())}).setOffloadMode(z3 ? 1 : 0).build();
        }
    }

    public static class Quality {
        public int height;
        public boolean original;
        public final ArrayList uris;
        public int width;

        public Quality(VideoUri videoUri) {
            ArrayList arrayList = new ArrayList();
            this.uris = arrayList;
            this.original = videoUri.original;
            this.width = videoUri.width;
            this.height = videoUri.height;
            arrayList.add(videoUri);
        }

        public static ArrayList filterByCodec(ArrayList arrayList) {
            if (arrayList == null) {
                return null;
            }
            int i = 0;
            while (i < arrayList.size()) {
                Quality quality = (Quality) arrayList.get(i);
                int i2 = 0;
                while (i2 < quality.uris.size()) {
                    VideoUri videoUri = (VideoUri) quality.uris.get(i2);
                    if (!TextUtils.isEmpty(videoUri.codec) && !VideoPlayer.supportsHardwareDecoder(videoUri.codec)) {
                        quality.uris.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                if (quality.uris.isEmpty()) {
                    arrayList.remove(i);
                    i--;
                }
                i++;
            }
            return arrayList;
        }

        public static ArrayList group(ArrayList arrayList) {
            Quality quality;
            Quality quality2;
            ArrayList arrayList2 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                VideoUri videoUri = (VideoUri) it.next();
                if (videoUri.original) {
                    quality = new Quality(videoUri);
                } else {
                    Iterator it2 = arrayList2.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            quality2 = null;
                            break;
                        }
                        quality2 = (Quality) it2.next();
                        if (!quality2.original && quality2.width == videoUri.width && quality2.height == videoUri.height) {
                            break;
                        }
                    }
                    if (quality2 == null || SharedConfig.debugVideoQualities) {
                        quality = new Quality(videoUri);
                    } else {
                        quality2.uris.add(videoUri);
                    }
                }
                arrayList2.add(quality);
            }
            if (BuildVars.LOGS_ENABLED) {
                Iterator it3 = arrayList2.iterator();
                while (it3.hasNext()) {
                    Quality quality3 = (Quality) it3.next();
                    StringBuilder sb = new StringBuilder();
                    sb.append("debug_loading_player: Quality ");
                    sb.append(quality3.p());
                    sb.append("p (");
                    sb.append(quality3.width);
                    sb.append("x");
                    sb.append(quality3.height);
                    sb.append(")");
                    sb.append(quality3.original ? " (source)" : "");
                    sb.append(":");
                    FileLog.d(sb.toString());
                    Iterator it4 = quality3.uris.iterator();
                    while (it4.hasNext()) {
                        VideoUri videoUri2 = (VideoUri) it4.next();
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("debug_loading_player: - video ");
                        sb2.append(videoUri2.width);
                        sb2.append("x");
                        sb2.append(videoUri2.height);
                        sb2.append(", codec=");
                        sb2.append(videoUri2.codec);
                        sb2.append(", bitrate=");
                        sb2.append((int) (videoUri2.bitrate * 8.0d));
                        sb2.append(", doc#");
                        sb2.append(videoUri2.docId);
                        String str = " (cached)";
                        sb2.append(videoUri2.isCached() ? " (cached)" : "");
                        sb2.append(", manifest#");
                        sb2.append(videoUri2.manifestDocId);
                        if (!videoUri2.isManifestCached()) {
                            str = "";
                        }
                        sb2.append(str);
                        FileLog.d(sb2.toString());
                    }
                }
                FileLog.d("debug_loading_player: ");
            }
            return arrayList2;
        }

        public TLRPC.Document getDownloadDocument() {
            VideoUri videoUri = null;
            if (this.uris.isEmpty()) {
                return null;
            }
            Iterator it = this.uris.iterator();
            while (it.hasNext()) {
                VideoUri videoUri2 = (VideoUri) it.next();
                if (videoUri2.isCached()) {
                    return videoUri2.document;
                }
            }
            long j = Long.MAX_VALUE;
            for (int i = 0; i < this.uris.size(); i++) {
                VideoUri videoUri3 = (VideoUri) this.uris.get(i);
                if (videoUri3.size < j && VideoPlayer.supportsHardwareDecoder(videoUri3.codec)) {
                    j = videoUri3.size;
                    videoUri = videoUri3;
                }
            }
            return videoUri != null ? videoUri.document : ((VideoUri) this.uris.get(0)).document;
        }

        public VideoUri getDownloadUri() {
            VideoUri videoUri = null;
            if (this.uris.isEmpty()) {
                return null;
            }
            Iterator it = this.uris.iterator();
            while (it.hasNext()) {
                VideoUri videoUri2 = (VideoUri) it.next();
                if (videoUri2.isCached()) {
                    return videoUri2;
                }
            }
            long j = Long.MAX_VALUE;
            for (int i = 0; i < this.uris.size(); i++) {
                VideoUri videoUri3 = (VideoUri) this.uris.get(i);
                if (videoUri3.size < j && VideoPlayer.supportsHardwareDecoder(videoUri3.codec)) {
                    j = videoUri3.size;
                    videoUri = videoUri3;
                }
            }
            return videoUri != null ? videoUri : (VideoUri) this.uris.get(0);
        }

        public int p() {
            int min = Math.min(this.width, this.height);
            if (Math.abs(min - 2160) < 55) {
                return 2160;
            }
            if (Math.abs(min - 1440) < 55) {
                return 1440;
            }
            if (Math.abs(min - 1080) < 55) {
                return 1080;
            }
            if (Math.abs(min - 720) < 55) {
                return 720;
            }
            if (Math.abs(min - 480) < 55) {
                return 480;
            }
            if (Math.abs(min - 360) < 55) {
                return 360;
            }
            if (Math.abs(min - 240) < 55) {
                return 240;
            }
            if (Math.abs(min - 144) < 55) {
                return 144;
            }
            return min;
        }

        public String toString() {
            StringBuilder sb;
            String str;
            String str2 = "";
            if (SharedConfig.debugVideoQualities) {
                sb = new StringBuilder();
                sb.append(this.width);
                sb.append("x");
                sb.append(this.height);
                if (this.original) {
                    str = " (" + LocaleController.getString(R.string.QualitySource) + ")";
                } else {
                    str = "";
                }
                sb.append(str);
                sb.append("\n");
                sb.append(AndroidUtilities.formatFileSize((long) ((VideoUri) this.uris.get(0)).bitrate).replace(" ", ""));
                sb.append("/s");
                if (((VideoUri) this.uris.get(0)).codec != null) {
                    str2 = ", " + ((VideoUri) this.uris.get(0)).codec;
                }
            } else {
                sb = new StringBuilder();
                sb.append(p());
                sb.append("p");
                if (this.original) {
                    str2 = " (" + LocaleController.getString(R.string.QualitySource) + ")";
                }
            }
            sb.append(str2);
            return sb.toString();
        }
    }

    public interface VideoPlayerDelegate {

        public abstract class CC {
            public static void $default$onRenderedFirstFrame(VideoPlayerDelegate videoPlayerDelegate, AnalyticsListener.EventTime eventTime) {
            }

            public static void $default$onSeekFinished(VideoPlayerDelegate videoPlayerDelegate, AnalyticsListener.EventTime eventTime) {
            }

            public static void $default$onSeekStarted(VideoPlayerDelegate videoPlayerDelegate, AnalyticsListener.EventTime eventTime) {
            }
        }

        void onError(VideoPlayer videoPlayer, Exception exc);

        void onRenderedFirstFrame();

        void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime);

        void onSeekFinished(AnalyticsListener.EventTime eventTime);

        void onSeekStarted(AnalyticsListener.EventTime eventTime);

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public static class VideoUri {
        public double bitrate;
        public String codec;
        public int currentAccount;
        public long docId;
        public TLRPC.Document document;
        public double duration;
        public int height;
        public Uri m3u8uri;
        public long manifestDocId;
        public TLRPC.Document manifestDocument;
        public boolean original;
        public long size;
        public Uri uri;
        public int width;

        public static Uri getUri(int i, TLRPC.Document document, int i2) {
            StringBuilder sb = new StringBuilder();
            sb.append("?account=");
            sb.append(i);
            sb.append("&id=");
            sb.append(document.id);
            sb.append("&hash=");
            sb.append(document.access_hash);
            sb.append("&dc=");
            sb.append(document.dc_id);
            sb.append("&size=");
            sb.append(document.size);
            sb.append("&mime=");
            sb.append(URLEncoder.encode(document.mime_type, "UTF-8"));
            sb.append("&rid=");
            sb.append(i2);
            sb.append("&name=");
            sb.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), "UTF-8"));
            sb.append("&reference=");
            byte[] bArr = document.file_reference;
            if (bArr == null) {
                bArr = new byte[0];
            }
            sb.append(Utilities.bytesToHex(bArr));
            return Uri.parse("tg://" + MessageObject.getFileName(document) + sb.toString());
        }

        public static VideoUri of(int i, TLRPC.Document document, TLRPC.Document document2, int i2) {
            TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo;
            Uri fromFile;
            Uri fromFile2;
            String str;
            VideoUri videoUri = new VideoUri();
            int i3 = 0;
            while (true) {
                if (i3 >= document.attributes.size()) {
                    tL_documentAttributeVideo = null;
                    break;
                }
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    tL_documentAttributeVideo = (TLRPC.TL_documentAttributeVideo) documentAttribute;
                    break;
                }
                i3++;
            }
            String lowerCase = (tL_documentAttributeVideo == null || (str = tL_documentAttributeVideo.video_codec) == null) ? null : str.toLowerCase();
            videoUri.currentAccount = i;
            videoUri.document = document;
            videoUri.docId = document.id;
            videoUri.uri = getUri(i, document, i2);
            if (document2 != null) {
                videoUri.manifestDocument = document2;
                videoUri.manifestDocId = document2.id;
                videoUri.m3u8uri = getUri(i, document2, i2);
                File pathToAttach = FileLoader.getInstance(i).getPathToAttach(document2, null, false, true);
                if (pathToAttach == null || !pathToAttach.exists()) {
                    File pathToAttach2 = FileLoader.getInstance(i).getPathToAttach(document2, null, true, true);
                    if (pathToAttach2 != null && pathToAttach2.exists()) {
                        fromFile2 = Uri.fromFile(pathToAttach2);
                    }
                } else {
                    fromFile2 = Uri.fromFile(pathToAttach);
                }
                videoUri.m3u8uri = fromFile2;
            }
            videoUri.codec = lowerCase;
            long j = document.size;
            videoUri.size = j;
            if (tL_documentAttributeVideo != null) {
                double d = tL_documentAttributeVideo.duration;
                videoUri.duration = d;
                videoUri.width = tL_documentAttributeVideo.w;
                videoUri.height = tL_documentAttributeVideo.h;
                double d2 = j;
                Double.isNaN(d2);
                videoUri.bitrate = d2 / d;
            }
            File pathToAttach3 = FileLoader.getInstance(i).getPathToAttach(document, null, false, true);
            if (pathToAttach3 == null || !pathToAttach3.exists()) {
                File pathToAttach4 = FileLoader.getInstance(i).getPathToAttach(document, null, true, true);
                if (pathToAttach4 != null && pathToAttach4.exists()) {
                    fromFile = Uri.fromFile(pathToAttach4);
                }
                return videoUri;
            }
            fromFile = Uri.fromFile(pathToAttach3);
            videoUri.uri = fromFile;
            return videoUri;
        }

        public boolean isCached() {
            Uri uri = this.uri;
            return uri != null && "file".equalsIgnoreCase(uri.getScheme());
        }

        public boolean isManifestCached() {
            Uri uri = this.m3u8uri;
            return uri != null && "file".equalsIgnoreCase(uri.getScheme());
        }

        public void updateCached(boolean z) {
            Uri fromFile;
            File pathToAttach;
            if (!isCached() && this.document != null && (((pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.document, null, false, z)) != null && pathToAttach.exists()) || ((pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.document, null, true, z)) != null && pathToAttach.exists()))) {
                this.uri = Uri.fromFile(pathToAttach);
            }
            if (isManifestCached() || this.manifestDocument == null) {
                return;
            }
            File pathToAttach2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.manifestDocument, null, false, z);
            if (pathToAttach2 == null || !pathToAttach2.exists()) {
                File pathToAttach3 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.manifestDocument, null, true, z);
                if (pathToAttach3 == null || !pathToAttach3.exists()) {
                    return;
                } else {
                    fromFile = Uri.fromFile(pathToAttach3);
                }
            } else {
                fromFile = Uri.fromFile(pathToAttach2);
            }
            this.m3u8uri = fromFile;
        }
    }

    public class VisualizerBufferSink implements TeeAudioProcessor.AudioBufferSink {
        ByteBuffer byteBuffer;
        long lastUpdateTime;
        private final int BUFFER_SIZE = 1024;
        private final int MAX_BUFFER_SIZE = 8192;
        FourierTransform.FFT fft = new FourierTransform.FFT(1024, 48000.0f);
        float[] real = new float[1024];
        int position = 0;

        public VisualizerBufferSink() {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(8192);
            this.byteBuffer = allocateDirect;
            allocateDirect.position(0);
        }

        public void lambda$handleBuffer$0() {
            VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages(null);
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
        }

        public void lambda$handleBuffer$1(float[] fArr) {
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(true, true, fArr);
        }

        @Override
        public void flush(int i, int i2, int i3) {
        }

        @Override
        public void handleBuffer(ByteBuffer byteBuffer) {
            if (VideoPlayer.this.audioVisualizerDelegate == null) {
                return;
            }
            if (byteBuffer == AudioProcessor.EMPTY_BUFFER || !VideoPlayer.this.mixedPlayWhenReady) {
                VideoPlayer.this.audioUpdateHandler.postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        VideoPlayer.VisualizerBufferSink.this.lambda$handleBuffer$0();
                    }
                }, 80L);
                return;
            }
            if (VideoPlayer.this.audioVisualizerDelegate.needUpdate()) {
                int limit = byteBuffer.limit();
                int i = 0;
                if (limit > 8192) {
                    VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages(null);
                    VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
                    return;
                }
                this.byteBuffer.put(byteBuffer);
                int i2 = this.position + limit;
                this.position = i2;
                if (i2 >= 1024) {
                    this.byteBuffer.position(0);
                    for (int i3 = 0; i3 < 1024; i3++) {
                        this.real[i3] = this.byteBuffer.getShort() / 32768.0f;
                    }
                    this.byteBuffer.rewind();
                    this.position = 0;
                    this.fft.forward(this.real);
                    int i4 = 0;
                    float f = 0.0f;
                    while (true) {
                        float f2 = 1.0f;
                        if (i4 >= 1024) {
                            break;
                        }
                        float f3 = this.fft.getSpectrumReal()[i4];
                        float f4 = this.fft.getSpectrumImaginary()[i4];
                        float sqrt = ((float) Math.sqrt((f3 * f3) + (f4 * f4))) / 30.0f;
                        if (sqrt <= 1.0f) {
                            f2 = sqrt < 0.0f ? 0.0f : sqrt;
                        }
                        f += f2 * f2;
                        i4++;
                    }
                    float sqrt2 = (float) Math.sqrt(f / 1024);
                    final float[] fArr = new float[7];
                    fArr[6] = sqrt2;
                    if (sqrt2 < 0.4f) {
                        while (i < 7) {
                            fArr[i] = 0.0f;
                            i++;
                        }
                    } else {
                        while (i < 6) {
                            int i5 = 170 * i;
                            float f5 = this.fft.getSpectrumReal()[i5];
                            float f6 = this.fft.getSpectrumImaginary()[i5];
                            float sqrt3 = (float) (Math.sqrt((f5 * f5) + (f6 * f6)) / 30.0d);
                            fArr[i] = sqrt3;
                            if (sqrt3 > 1.0f) {
                                fArr[i] = 1.0f;
                            } else if (sqrt3 < 0.0f) {
                                fArr[i] = 0.0f;
                            }
                            i++;
                        }
                    }
                    if (System.currentTimeMillis() - this.lastUpdateTime < 64) {
                        return;
                    }
                    this.lastUpdateTime = System.currentTimeMillis();
                    VideoPlayer.this.audioUpdateHandler.postDelayed(new Runnable() {
                        @Override
                        public final void run() {
                            VideoPlayer.VisualizerBufferSink.this.lambda$handleBuffer$1(fArr);
                        }
                    }, 130L);
                }
            }
        }
    }

    public VideoPlayer() {
        this(true, false);
    }

    public VideoPlayer(boolean z, boolean z2) {
        int i = lastPlayerId;
        lastPlayerId = i + 1;
        this.playerId = i;
        this.audioUpdateHandler = new Handler(Looper.getMainLooper());
        this.autoIsOriginal = false;
        this.selectedQualityIndex = -1;
        this.fallbackDuration = -9223372036854775807L;
        this.fallbackPosition = -9223372036854775807L;
        this.seekFinishedListeners = new ArrayList();
        this.handleAudioFocus = false;
        this.audioDisabled = z2;
        this.mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(ApplicationLoader.applicationContext, "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(ApplicationLoader.applicationContext, new AdaptiveTrackSelection.Factory());
        this.trackSelector = defaultTrackSelector;
        if (z2) {
            defaultTrackSelector.setParameters(defaultTrackSelector.getParameters().buildUpon().setTrackTypeDisabled(1, true).build());
        }
        this.lastReportedPlaybackState = 1;
        this.shouldPauseOther = z;
        if (z) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
        playerCounter++;
    }

    public void checkPlayersReady() {
        if (this.audioPlayerReady && this.videoPlayerReady && this.mixedPlayWhenReady) {
            play();
        }
    }

    private void ensurePlayerCreated() {
        DefaultLoadControl defaultLoadControl = this.isStory ? new DefaultLoadControl(new DefaultAllocator(true, 65536), 50000, 50000, 1000, 1000, -1, false, 0, false) : new DefaultLoadControl(new DefaultAllocator(true, 65536), 50000, 50000, 100, 5000, -1, false, 0, false);
        if (this.player == null) {
            DefaultRenderersFactory audioVisualizerRenderersFactory = this.audioVisualizerDelegate != null ? new AudioVisualizerRenderersFactory(ApplicationLoader.applicationContext) : new DefaultRenderersFactory(ApplicationLoader.applicationContext);
            audioVisualizerRenderersFactory.setExtensionRendererMode(2);
            ExoPlayer.Builder loadControl = new ExoPlayer.Builder(ApplicationLoader.applicationContext).setRenderersFactory(audioVisualizerRenderersFactory).setTrackSelector(this.trackSelector).setLoadControl(defaultLoadControl);
            Looper looper = this.looper;
            if (looper != null) {
                loadControl.setLooper(looper);
            }
            EGLContext eGLContext = this.eglParentContext;
            if (eGLContext != null) {
                loadControl.eglContext = eGLContext;
            }
            ExoPlayer build = loadControl.build();
            this.player = build;
            build.addAnalyticsListener(this);
            this.player.addListener(this);
            this.player.addVideoListener(this);
            TextureView textureView = this.textureView;
            if (textureView != null) {
                this.player.setVideoTextureView(textureView);
            } else {
                Surface surface = this.surface;
                if (surface != null) {
                    this.player.setVideoSurface(surface);
                } else {
                    SurfaceView surfaceView = this.surfaceView;
                    if (surfaceView != null) {
                        this.player.setVideoSurfaceView(surfaceView);
                    }
                }
            }
            this.player.setPlayWhenReady(this.autoplay);
            this.player.setRepeatMode(this.looping ? 2 : 0);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            SimpleExoPlayer buildSimpleExoPlayer = new ExoPlayer.Builder(ApplicationLoader.applicationContext).setTrackSelector(this.trackSelector).setLoadControl(defaultLoadControl).buildSimpleExoPlayer();
            this.audioPlayer = buildSimpleExoPlayer;
            buildSimpleExoPlayer.addListener(new Player.Listener() {
                AnonymousClass1() {
                }

                @Override
                public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
                    Player.Listener.CC.$default$onAudioAttributesChanged(this, audioAttributes);
                }

                @Override
                public void onAvailableCommandsChanged(Player.Commands commands) {
                    Player.Listener.CC.$default$onAvailableCommandsChanged(this, commands);
                }

                @Override
                public void onCues(CueGroup cueGroup) {
                    Player.Listener.CC.$default$onCues(this, cueGroup);
                }

                @Override
                public void onCues(List list) {
                    Player.Listener.CC.$default$onCues(this, list);
                }

                @Override
                public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
                    Player.Listener.CC.$default$onDeviceInfoChanged(this, deviceInfo);
                }

                @Override
                public void onDeviceVolumeChanged(int i, boolean z) {
                    Player.Listener.CC.$default$onDeviceVolumeChanged(this, i, z);
                }

                @Override
                public void onEvents(Player player, Player.Events events) {
                    Player.Listener.CC.$default$onEvents(this, player, events);
                }

                @Override
                public void onIsLoadingChanged(boolean z) {
                    Player.Listener.CC.$default$onIsLoadingChanged(this, z);
                }

                @Override
                public void onIsPlayingChanged(boolean z) {
                    Player.Listener.CC.$default$onIsPlayingChanged(this, z);
                }

                @Override
                public void onLoadingChanged(boolean z) {
                    Player.Listener.CC.$default$onLoadingChanged(this, z);
                }

                @Override
                public void onMediaItemTransition(MediaItem mediaItem, int i) {
                    Player.Listener.CC.$default$onMediaItemTransition(this, mediaItem, i);
                }

                @Override
                public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                    Player.Listener.CC.$default$onMediaMetadataChanged(this, mediaMetadata);
                }

                @Override
                public void onMetadata(Metadata metadata) {
                    Player.Listener.CC.$default$onMetadata(this, metadata);
                }

                @Override
                public void onPlayWhenReadyChanged(boolean z, int i) {
                    Player.Listener.CC.$default$onPlayWhenReadyChanged(this, z, i);
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    Player.Listener.CC.$default$onPlaybackParametersChanged(this, playbackParameters);
                }

                @Override
                public void onPlaybackStateChanged(int i) {
                    Player.Listener.CC.$default$onPlaybackStateChanged(this, i);
                }

                @Override
                public void onPlaybackSuppressionReasonChanged(int i) {
                    Player.Listener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
                }

                @Override
                public void onPlayerError(PlaybackException playbackException) {
                    Player.Listener.CC.$default$onPlayerError(this, playbackException);
                }

                @Override
                public void onPlayerErrorChanged(PlaybackException playbackException) {
                    Player.Listener.CC.$default$onPlayerErrorChanged(this, playbackException);
                }

                @Override
                public void onPlayerStateChanged(boolean z, int i) {
                    if (VideoPlayer.this.audioPlayerReady || i != 3) {
                        return;
                    }
                    VideoPlayer.this.audioPlayerReady = true;
                    VideoPlayer.this.checkPlayersReady();
                }

                @Override
                public void onPositionDiscontinuity(int i) {
                    Player.Listener.CC.$default$onPositionDiscontinuity(this, i);
                }

                @Override
                public void onPositionDiscontinuity(Player.PositionInfo positionInfo, Player.PositionInfo positionInfo2, int i) {
                    Player.Listener.CC.$default$onPositionDiscontinuity(this, positionInfo, positionInfo2, i);
                }

                @Override
                public void onRenderedFirstFrame() {
                    Player.Listener.CC.$default$onRenderedFirstFrame(this);
                }

                @Override
                public void onRepeatModeChanged(int i) {
                    Player.Listener.CC.$default$onRepeatModeChanged(this, i);
                }

                @Override
                public void onSeekProcessed() {
                    Player.Listener.CC.$default$onSeekProcessed(this);
                }

                @Override
                public void onSkipSilenceEnabledChanged(boolean z) {
                    Player.Listener.CC.$default$onSkipSilenceEnabledChanged(this, z);
                }

                @Override
                public void onSurfaceSizeChanged(int i, int i2) {
                    Player.Listener.CC.$default$onSurfaceSizeChanged(this, i, i2);
                }

                @Override
                public void onTimelineChanged(Timeline timeline, int i) {
                    Player.Listener.CC.$default$onTimelineChanged(this, timeline, i);
                }

                @Override
                public void onTracksChanged(Tracks tracks) {
                    Player.Listener.CC.$default$onTracksChanged(this, tracks);
                }

                @Override
                public void onVideoSizeChanged(VideoSize videoSize) {
                    Player.Listener.CC.$default$onVideoSizeChanged(this, videoSize);
                }

                @Override
                public void onVolumeChanged(float f) {
                    Player.Listener.CC.$default$onVolumeChanged(this, f);
                }
            });
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    public static TLRPC.Document getDocumentForThumb(int i, TLRPC.MessageMedia messageMedia) {
        VideoUri qualityForThumb;
        if ((messageMedia instanceof TLRPC.TL_messageMediaDocument) && (qualityForThumb = getQualityForThumb(getQualities(i, messageMedia.document, messageMedia.alt_documents, 0, true))) != null) {
            return qualityForThumb.document;
        }
        return null;
    }

    public static Boolean getLooping(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0);
        String str = messageObject.getDialogId() + "_" + messageObject.getId() + "loop";
        if (sharedPreferences.contains(str)) {
            return Boolean.valueOf(sharedPreferences.getBoolean(str, false));
        }
        return null;
    }

    public static ArrayList getQualities(int i, TLRPC.Document document, ArrayList arrayList, int i2, boolean z) {
        String str;
        ArrayList arrayList2 = new ArrayList();
        if (document != null) {
            arrayList2.add(document);
        }
        if (!MessagesController.getInstance(i).videoIgnoreAltDocuments && arrayList != null) {
            arrayList2.addAll(arrayList);
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        int i3 = 0;
        while (i3 < arrayList2.size()) {
            TLRPC.Document document2 = (TLRPC.Document) arrayList2.get(i3);
            if ("application/x-mpegurl".equalsIgnoreCase(document2.mime_type) && (str = document2.file_name_fixed) != null && str.startsWith("mtproto")) {
                try {
                    longSparseArray.put(Long.parseLong(document2.file_name_fixed.substring(7)), document2);
                    arrayList2.remove(i3);
                    i3--;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            i3++;
        }
        ArrayList arrayList3 = new ArrayList();
        for (int i4 = 0; i4 < arrayList2.size(); i4++) {
            try {
                TLRPC.Document document3 = (TLRPC.Document) arrayList2.get(i4);
                if (!"application/x-mpegurl".equalsIgnoreCase(document3.mime_type)) {
                    VideoUri of = VideoUri.of(i, document3, (TLRPC.Document) longSparseArray.get(document3.id), i2);
                    if (of.width > 0 && of.height > 0) {
                        if (document3 == document) {
                            of.original = true;
                        }
                        arrayList3.add(of);
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        ArrayList arrayList4 = new ArrayList();
        for (int i5 = 0; i5 < arrayList3.size(); i5++) {
            VideoUri videoUri = (VideoUri) arrayList3.get(i5);
            String str2 = videoUri.codec;
            if (str2 != null) {
                if (z) {
                    if (!"avc".equals(str2)) {
                        if (!"h264".equals(videoUri.codec)) {
                            if (!"vp9".equals(videoUri.codec)) {
                                if (!"vp8".equals(videoUri.codec)) {
                                    if (!"av1".equals(videoUri.codec)) {
                                        if (!"av01".equals(videoUri.codec)) {
                                        }
                                    }
                                    if (!supportsHardwareDecoder(videoUri.codec)) {
                                    }
                                }
                            }
                        }
                    }
                } else if (("av1".equals(str2) || "av01".equals(videoUri.codec) || "hevc".equals(videoUri.codec) || "h265".equals(videoUri.codec) || "vp9".equals(videoUri.codec)) && !supportsHardwareDecoder(videoUri.codec)) {
                }
            }
            arrayList4.add(videoUri);
        }
        ArrayList arrayList5 = new ArrayList();
        if (arrayList4.isEmpty()) {
            arrayList5.addAll(arrayList3);
        } else {
            arrayList5.addAll(arrayList4);
        }
        return Quality.group(arrayList5);
    }

    public static ArrayList getQualities(int i, TLRPC.MessageMedia messageMedia) {
        return !(messageMedia instanceof TLRPC.TL_messageMediaDocument) ? new ArrayList() : getQualities(i, messageMedia.document, messageMedia.alt_documents, 0, false);
    }

    public static VideoUri getQualityForPlayer(ArrayList arrayList) {
        int i;
        int i2;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Iterator it2 = ((Quality) it.next()).uris.iterator();
            while (it2.hasNext()) {
                VideoUri videoUri = (VideoUri) it2.next();
                if (videoUri.original && videoUri.isCached()) {
                    return videoUri;
                }
            }
        }
        Iterator it3 = arrayList.iterator();
        VideoUri videoUri2 = null;
        while (it3.hasNext()) {
            Iterator it4 = ((Quality) it3.next()).uris.iterator();
            while (it4.hasNext()) {
                VideoUri videoUri3 = (VideoUri) it4.next();
                if (!videoUri3.original && supportsHardwareDecoder(videoUri3.codec) && (videoUri2 == null || (i = videoUri3.width * videoUri3.height) > (i2 = videoUri2.width * videoUri2.height) || (i == i2 && videoUri3.bitrate < videoUri2.bitrate))) {
                    videoUri2 = videoUri3;
                }
            }
        }
        if (videoUri2 == null) {
            Iterator it5 = arrayList.iterator();
            while (it5.hasNext()) {
                Iterator it6 = ((Quality) it5.next()).uris.iterator();
                while (it6.hasNext()) {
                    VideoUri videoUri4 = (VideoUri) it6.next();
                    if (videoUri2 == null || videoUri2.width * videoUri2.height > videoUri4.width * videoUri4.height || videoUri4.bitrate < videoUri2.bitrate) {
                        videoUri2 = videoUri4;
                    }
                }
            }
        }
        return videoUri2;
    }

    public static VideoUri getQualityForThumb(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Iterator it2 = ((Quality) it.next()).uris.iterator();
            while (it2.hasNext()) {
                VideoUri videoUri = (VideoUri) it2.next();
                if (videoUri.isCached()) {
                    return videoUri;
                }
            }
        }
        Iterator it3 = arrayList.iterator();
        VideoUri videoUri2 = null;
        while (it3.hasNext()) {
            Iterator it4 = ((Quality) it3.next()).uris.iterator();
            while (it4.hasNext()) {
                VideoUri videoUri3 = (VideoUri) it4.next();
                if (!videoUri3.original && (videoUri2 == null || videoUri2.width * videoUri2.height > videoUri3.width * videoUri3.height || videoUri3.bitrate < videoUri2.bitrate)) {
                    if (videoUri3.width <= 900 && videoUri3.height <= 900) {
                        videoUri2 = videoUri3;
                    }
                }
            }
        }
        if (videoUri2 == null) {
            Iterator it5 = arrayList.iterator();
            while (it5.hasNext()) {
                Iterator it6 = ((Quality) it5.next()).uris.iterator();
                while (it6.hasNext()) {
                    VideoUri videoUri4 = (VideoUri) it6.next();
                    if (videoUri2 == null || videoUri2.width * videoUri2.height > videoUri4.width * videoUri4.height || videoUri4.bitrate < videoUri2.bitrate) {
                        videoUri2 = videoUri4;
                    }
                }
            }
        }
        return videoUri2;
    }

    private TrackSelectionOverride getQualityTrackSelection(VideoUri videoUri) {
        int i;
        try {
            int indexOf = this.manifestUris.indexOf(videoUri);
            MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = this.trackSelector.getCurrentMappedTrackInfo();
            for (int i2 = 0; i2 < currentMappedTrackInfo.getRendererCount(); i2++) {
                TrackGroupArray trackGroups = currentMappedTrackInfo.getTrackGroups(i2);
                for (int i3 = 0; i3 < trackGroups.length; i3++) {
                    TrackGroup trackGroup = trackGroups.get(i3);
                    for (int i4 = 0; i4 < trackGroup.length; i4++) {
                        Format format = trackGroup.getFormat(i4);
                        try {
                            i = Integer.parseInt(format.id);
                        } catch (Exception unused) {
                            i = -1;
                        }
                        if (i >= 0 && indexOf == i) {
                            return new TrackSelectionOverride(trackGroup, i4);
                        }
                        if (format.width == videoUri.width && format.height == videoUri.height) {
                            return new TrackSelectionOverride(trackGroup, i4);
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static Quality getSavedQuality(ArrayList arrayList, long j, int i) {
        String string = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getString(j + "_" + i + "q2", "");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Quality quality = (Quality) it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(quality.width);
            sb.append("x");
            sb.append(quality.height);
            sb.append(quality.original ? "s" : "");
            if (TextUtils.equals(string, sb.toString())) {
                return quality;
            }
        }
        return null;
    }

    public static Quality getSavedQuality(ArrayList arrayList, MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        return getSavedQuality(arrayList, messageObject.getDialogId(), messageObject.getId());
    }

    public void lambda$onPlayerError$0() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.clearVideoTextureView(this.textureView);
            this.player.setVideoTextureView(this.textureView);
            ArrayList arrayList = this.videoQualities;
            if (arrayList != null) {
                preparePlayer(arrayList, this.videoQualityToSelect);
            } else if (this.loopingMediaSource) {
                preparePlayerLoop(this.videoUri, this.videoType, this.audioUri, this.audioType);
            } else {
                preparePlayer(this.videoUri, this.videoType);
            }
            play();
        }
    }

    public void lambda$onPlayerError$1(PlaybackException playbackException) {
        Throwable cause = playbackException.getCause();
        if ((cause instanceof MediaCodecDecoderException) && (cause.toString().contains("av1") || cause.toString().contains("av01"))) {
            FileLog.e(playbackException);
            FileLog.e("av1 codec failed, we think this codec is not supported");
            MessagesController.getGlobalMainSettings().edit().putBoolean("unsupport_video/av01", true).commit();
            HashMap hashMap = cachedSupportedCodec;
            if (hashMap != null) {
                hashMap.clear();
            }
            ArrayList filterByCodec = Quality.filterByCodec(this.videoQualities);
            this.videoQualities = filterByCodec;
            if (filterByCodec != null) {
                preparePlayer(filterByCodec, this.videoQualityToSelect);
                return;
            }
            return;
        }
        TextureView textureView = this.textureView;
        if (textureView == null || ((this.triedReinit || !(cause instanceof MediaCodecRenderer.DecoderInitializationException)) && !(cause instanceof SurfaceNotValidException))) {
            this.delegate.onError(this, playbackException);
            return;
        }
        this.triedReinit = true;
        if (this.player != null) {
            ViewGroup viewGroup = (ViewGroup) textureView.getParent();
            if (viewGroup != null) {
                int indexOfChild = viewGroup.indexOfChild(this.textureView);
                viewGroup.removeView(this.textureView);
                viewGroup.addView(this.textureView, indexOfChild);
            }
            DispatchQueue dispatchQueue = this.workerQueue;
            if (dispatchQueue != null) {
                dispatchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        VideoPlayer.this.lambda$onPlayerError$0();
                    }
                });
                return;
            }
            this.player.clearVideoTextureView(this.textureView);
            this.player.setVideoTextureView(this.textureView);
            ArrayList arrayList = this.videoQualities;
            if (arrayList != null) {
                preparePlayer(arrayList, this.videoQualityToSelect);
            } else if (this.loopingMediaSource) {
                preparePlayerLoop(this.videoUri, this.videoType, this.audioUri, this.audioType);
            } else {
                preparePlayer(this.videoUri, this.videoType);
            }
            play();
        }
    }

    private void maybeReportPlayerState() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        boolean playWhenReady = exoPlayer.getPlayWhenReady();
        int playbackState = this.player.getPlaybackState();
        if (this.lastReportedPlayWhenReady == playWhenReady && this.lastReportedPlaybackState == playbackState) {
            return;
        }
        this.delegate.onStateChanged(playWhenReady, playbackState);
        this.lastReportedPlayWhenReady = playWhenReady;
        this.lastReportedPlaybackState = playbackState;
    }

    private MediaSource mediaSourceFromUri(Uri uri, String str) {
        MediaItem build = new MediaItem.Builder().setUri(uri).build();
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 3680:
                if (str.equals("ss")) {
                    c = 0;
                    break;
                }
                break;
            case 103407:
                if (str.equals("hls")) {
                    c = 1;
                    break;
                }
                break;
            case 3075986:
                if (str.equals("dash")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (this.ssMediaSourceFactory == null) {
                    this.ssMediaSourceFactory = new SsMediaSource.Factory(this.mediaDataSourceFactory);
                }
                return this.ssMediaSourceFactory.createMediaSource(build);
            case 1:
                if (this.hlsMediaSourceFactory == null) {
                    this.hlsMediaSourceFactory = new HlsMediaSource.Factory(this.mediaDataSourceFactory);
                }
                return this.hlsMediaSourceFactory.createMediaSource(build);
            case 2:
                if (this.dashMediaSourceFactory == null) {
                    this.dashMediaSourceFactory = new DashMediaSource.Factory(this.mediaDataSourceFactory);
                }
                return this.dashMediaSourceFactory.createMediaSource(build);
            default:
                if (this.progressiveMediaSourceFactory == null) {
                    this.progressiveMediaSourceFactory = new ProgressiveMediaSource.Factory(this.mediaDataSourceFactory);
                }
                return this.progressiveMediaSourceFactory.createMediaSource(build);
        }
    }

    public static void saveLooping(boolean z, MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putBoolean(messageObject.getDialogId() + "_" + messageObject.getId() + "loop", z).apply();
    }

    public static void saveQuality(Quality quality, long j, int i) {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit();
        if (quality == null) {
            edit.remove(j + "_" + i + "q2");
        } else {
            String str = j + "_" + i + "q2";
            StringBuilder sb = new StringBuilder();
            sb.append(quality.width);
            sb.append("x");
            sb.append(quality.height);
            sb.append(quality.original ? "s" : "");
            edit.putString(str, sb.toString());
        }
        edit.apply();
    }

    public static void saveQuality(Quality quality, MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        saveQuality(quality, messageObject.getDialogId(), messageObject.getId());
    }

    private void setSelectedQuality(boolean z, Quality quality) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        boolean isPlaying = exoPlayer.isPlaying();
        long currentPosition = this.player.getCurrentPosition();
        if (!z) {
            this.fallbackPosition = currentPosition;
            this.fallbackDuration = this.player.getDuration();
        }
        this.videoQualityToSelect = quality;
        boolean z2 = true;
        if (quality == null) {
            Uri makeManifest = makeManifest(this.videoQualities);
            Quality originalQuality = getOriginalQuality();
            if (originalQuality != null && originalQuality.uris.size() == 1 && ((VideoUri) originalQuality.uris.get(0)).isCached()) {
                this.currentStreamIsHls = false;
                this.autoIsOriginal = true;
                this.videoQualityToSelect = originalQuality;
                this.player.setMediaSource(mediaSourceFromUri(originalQuality.getDownloadUri().uri, "other"), false);
            } else if (makeManifest != null) {
                this.autoIsOriginal = false;
                MappingTrackSelector mappingTrackSelector = this.trackSelector;
                mappingTrackSelector.setParameters(mappingTrackSelector.getParameters().buildUpon().clearOverrides().build());
                if (this.currentStreamIsHls) {
                    z2 = false;
                } else {
                    this.currentStreamIsHls = true;
                    this.player.setMediaSource(mediaSourceFromUri(makeManifest, "hls"), false);
                }
            } else {
                quality = getHighestQuality(Boolean.TRUE);
                if (quality == null) {
                    quality = getHighestQuality(Boolean.FALSE);
                }
                if (quality == null || quality.uris.isEmpty()) {
                    return;
                }
                this.currentStreamIsHls = false;
                this.videoQualityToSelect = quality;
                this.autoIsOriginal = quality.original;
                this.player.setMediaSource(mediaSourceFromUri(quality.getDownloadUri().uri, "other"), false);
            }
        } else {
            this.autoIsOriginal = false;
            if (quality.uris.isEmpty()) {
                return;
            }
            Uri makeManifest2 = quality.uris.size() > 1 ? makeManifest(this.videoQualities) : null;
            if (makeManifest2 == null || quality.uris.size() == 1 || this.trackSelector.getCurrentMappedTrackInfo() == null) {
                this.currentStreamIsHls = false;
                this.player.setMediaSource(mediaSourceFromUri(quality.getDownloadUri().uri, "other"), false);
            } else {
                if (this.currentStreamIsHls) {
                    z2 = false;
                } else {
                    this.currentStreamIsHls = true;
                    this.player.setMediaSource(mediaSourceFromUri(makeManifest2, "hls"), false);
                }
                TrackSelectionParameters.Builder clearOverrides = this.trackSelector.getParameters().buildUpon().clearOverrides();
                Iterator it = quality.uris.iterator();
                while (it.hasNext()) {
                    TrackSelectionOverride qualityTrackSelection = getQualityTrackSelection((VideoUri) it.next());
                    if (qualityTrackSelection != null) {
                        clearOverrides.addOverride(qualityTrackSelection);
                    }
                }
                this.trackSelector.setParameters(clearOverrides.build());
            }
        }
        if (z2) {
            this.player.prepare();
            if (!z) {
                this.player.seekTo(currentPosition);
                if (isPlaying) {
                    this.player.play();
                }
            }
            Runnable runnable = this.onQualityChangeListener;
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
            activePlayers.add(Integer.valueOf(this.playerId));
        }
    }

    public static boolean supportsHardwareDecoder(String str) {
        try {
            String mime = toMime(str);
            if (mime == null) {
                return false;
            }
            if (cachedSupportedCodec == null) {
                cachedSupportedCodec = new HashMap();
            }
            Boolean bool = (Boolean) cachedSupportedCodec.get(mime);
            if (bool != null) {
                return bool.booleanValue();
            }
            if (MessagesController.getGlobalMainSettings().getBoolean("unsupport_" + mime, false)) {
                return false;
            }
            int codecCount = MediaCodecList.getCodecCount();
            for (int i = 0; i < codecCount; i++) {
                MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
                if (!codecInfoAt.isEncoder() && MediaCodecUtil.isHardwareAccelerated(codecInfoAt, mime)) {
                    for (String str2 : codecInfoAt.getSupportedTypes()) {
                        if (str2.equalsIgnoreCase(mime)) {
                            cachedSupportedCodec.put(mime, Boolean.TRUE);
                            return true;
                        }
                    }
                }
            }
            cachedSupportedCodec.put(mime, Boolean.FALSE);
            return false;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static String toMime(String str) {
        if (str == null) {
            return null;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case 96924:
                if (str.equals("av1")) {
                    c = 0;
                    break;
                }
                break;
            case 96974:
                if (str.equals("avc")) {
                    c = 1;
                    break;
                }
                break;
            case 116926:
                if (str.equals("vp8")) {
                    c = 2;
                    break;
                }
                break;
            case 116927:
                if (str.equals("vp9")) {
                    c = 3;
                    break;
                }
                break;
            case 3004662:
                if (str.equals("av01")) {
                    c = 4;
                    break;
                }
                break;
            case 3148040:
                if (str.equals("h264")) {
                    c = 5;
                    break;
                }
                break;
            case 3148041:
                if (str.equals("h265")) {
                    c = 6;
                    break;
                }
                break;
            case 3199082:
                if (str.equals("hevc")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 4:
                return "video/av01";
            case 1:
            case 5:
                return "video/avc";
            case 2:
                return "video/x-vnd.on2.vp8";
            case 3:
                return "video/x-vnd.on2.vp9";
            case 6:
            case 7:
                return "video/hevc";
            default:
                return "video/" + str;
        }
    }

    public boolean createdWithAudioTrack() {
        return !this.audioDisabled;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.playerDidStartPlaying || ((VideoPlayer) objArr[0]) == this || !isPlaying() || this.allowMultipleInstances) {
            return;
        }
        pause();
    }

    public long getBufferedPosition() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            return this.isStreaming ? exoPlayer.getBufferedPosition() : exoPlayer.getDuration();
        }
        return 0L;
    }

    public long getCurrentPosition() {
        long j = this.fallbackPosition;
        if (j != -9223372036854775807L) {
            return j;
        }
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            return exoPlayer.getCurrentPosition();
        }
        return 0L;
    }

    public Quality getCurrentQuality() {
        int currentQualityIndex = getCurrentQualityIndex();
        if (currentQualityIndex < 0 || currentQualityIndex >= getQualitiesCount()) {
            return null;
        }
        return getQuality(currentQualityIndex);
    }

    public int getCurrentQualityIndex() {
        Format videoFormat;
        if (this.selectedQualityIndex == -1) {
            try {
                if (this.autoIsOriginal) {
                    for (int i = 0; i < getQualitiesCount(); i++) {
                        if (getQuality(i).original) {
                            return i;
                        }
                    }
                }
                ExoPlayer exoPlayer = this.player;
                if (exoPlayer == null || (videoFormat = exoPlayer.getVideoFormat()) == null) {
                    return -1;
                }
                for (int i2 = 0; i2 < getQualitiesCount(); i2++) {
                    Quality quality = getQuality(i2);
                    if (!quality.original && videoFormat.width == quality.width && videoFormat.height == quality.height && videoFormat.bitrate == ((int) Math.floor(((VideoUri) quality.uris.get(0)).bitrate * 8.0d))) {
                        return i2;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
                return -1;
            }
        }
        return this.selectedQualityIndex;
    }

    public Uri getCurrentUri() {
        return this.currentUri;
    }

    public long getDuration() {
        long j = this.fallbackDuration;
        if (j != -9223372036854775807L) {
            return j;
        }
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            return exoPlayer.getDuration();
        }
        return 0L;
    }

    public StoryEntry.HDRInfo getHDRStaticInfo(StoryEntry.HDRInfo hDRInfo) {
        if (hDRInfo == null) {
            hDRInfo = new StoryEntry.HDRInfo();
        }
        try {
            MediaFormat mediaFormat = ((MediaCodecRenderer) this.player.getRenderer(0)).codecOutputMediaFormat;
            ByteBuffer byteBuffer = mediaFormat.getByteBuffer("hdr-static-info");
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (byteBuffer.get() == 0) {
                hDRInfo.maxlum = byteBuffer.getShort(17);
                hDRInfo.minlum = byteBuffer.getShort(19) * 1.0E-4f;
            }
            if (Build.VERSION.SDK_INT >= 24) {
                if (mediaFormat.containsKey("color-transfer")) {
                    hDRInfo.colorTransfer = mediaFormat.getInteger("color-transfer");
                }
                if (mediaFormat.containsKey("color-standard")) {
                    hDRInfo.colorStandard = mediaFormat.getInteger("color-standard");
                }
                if (mediaFormat.containsKey("color-range")) {
                    hDRInfo.colorRange = mediaFormat.getInteger("color-range");
                }
            }
        } catch (Exception unused) {
            hDRInfo.minlum = 0.0f;
            hDRInfo.maxlum = 0.0f;
        }
        return hDRInfo;
    }

    public Quality getHighestQuality(Boolean bool) {
        Quality quality = null;
        for (int i = 0; i < getQualitiesCount(); i++) {
            Quality quality2 = getQuality(i);
            if ((bool == null || quality2.original == bool.booleanValue()) && (quality == null || quality.width * quality.height < quality2.width * quality2.height)) {
                quality = quality2;
            }
        }
        return quality;
    }

    public File getLowestFile() {
        ArrayList arrayList = this.videoQualities;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                Iterator it = ((Quality) this.videoQualities.get(size)).uris.iterator();
                while (it.hasNext()) {
                    VideoUri videoUri = (VideoUri) it.next();
                    if (!videoUri.isCached()) {
                        videoUri.updateCached(true);
                    }
                    if (videoUri.isCached()) {
                        return new File(videoUri.uri.getPath());
                    }
                }
            }
        }
        Uri uri = this.videoUri;
        if (uri == null || !"file".equalsIgnoreCase(uri.getScheme())) {
            return null;
        }
        return new File(this.videoUri.getPath());
    }

    public Quality getOriginalQuality() {
        for (int i = 0; i < getQualitiesCount(); i++) {
            Quality quality = getQuality(i);
            if (quality.original) {
                return quality;
            }
        }
        return null;
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    public int getPlaybackState() {
        return this.player.getPlaybackState();
    }

    public int getQualitiesCount() {
        ArrayList arrayList = this.videoQualities;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public Quality getQuality(int i) {
        ArrayList arrayList = this.videoQualities;
        return arrayList == null ? getHighestQuality(Boolean.FALSE) : (i < 0 || i >= arrayList.size()) ? getHighestQuality(Boolean.FALSE) : (Quality) this.videoQualities.get(i);
    }

    public int getSelectedQuality() {
        return this.selectedQualityIndex;
    }

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
    }

    public boolean isHDR() {
        ColorInfo colorInfo;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return false;
        }
        try {
            Format videoFormat = exoPlayer.getVideoFormat();
            if (videoFormat != null && (colorInfo = videoFormat.colorInfo) != null) {
                int i = colorInfo.colorTransfer;
                return i == 6 || i == 7;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public boolean isLooping() {
        return this.looping;
    }

    public boolean isMuted() {
        ExoPlayer exoPlayer = this.player;
        return exoPlayer != null && exoPlayer.getVolume() == 0.0f;
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public boolean isPlaying() {
        ExoPlayer exoPlayer;
        return (this.mixedAudio && this.mixedPlayWhenReady) || ((exoPlayer = this.player) != null && exoPlayer.getPlayWhenReady());
    }

    public Uri makeManifest(ArrayList arrayList) {
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:6\n");
        sb.append("#EXT-X-INDEPENDENT-SEGMENTS\n\n");
        this.manifestUris = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Iterator it2 = ((Quality) it.next()).uris.iterator();
            while (it2.hasNext()) {
                VideoUri videoUri = (VideoUri) it2.next();
                this.mediaDataSourceFactory.putDocumentUri(videoUri.docId, videoUri.uri);
                this.mediaDataSourceFactory.putDocumentUri(videoUri.manifestDocId, videoUri.m3u8uri);
                if (videoUri.m3u8uri != null) {
                    this.manifestUris.add(videoUri);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("#EXT-X-STREAM-INF:BANDWIDTH=");
                    sb2.append((int) Math.floor(videoUri.bitrate * 8.0d));
                    sb2.append(",RESOLUTION=");
                    sb2.append(videoUri.width);
                    sb2.append("x");
                    sb2.append(videoUri.height);
                    String mime = toMime(videoUri.codec);
                    if (mime != null) {
                        sb2.append(",MIME=\"");
                        sb2.append(mime);
                        sb2.append("\"");
                    }
                    if (videoUri.isCached() && videoUri.isManifestCached()) {
                        sb2.append(",CACHED=\"true\"");
                    }
                    sb2.append(",DOCID=\"");
                    sb2.append(videoUri.docId);
                    sb2.append("\"");
                    sb2.append(",ACCOUNT=\"");
                    sb2.append(videoUri.currentAccount);
                    sb2.append("\"");
                    sb2.append("\n");
                    if (videoUri.isManifestCached()) {
                        sb2.append(videoUri.m3u8uri);
                    } else {
                        sb2.append("mtproto:");
                        sb2.append(videoUri.manifestDocId);
                    }
                    sb2.append("\n\n");
                    arrayList2.add(sb2.toString());
                    z = true;
                }
            }
        }
        if (!z) {
            return null;
        }
        Collections.reverse(arrayList2);
        sb.append(TextUtils.join("", arrayList2));
        return Uri.parse("data:application/x-mpegurl;base64," + Base64.encodeToString(sb.toString().getBytes(), 2));
    }

    @Override
    public void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
        AnalyticsListener.CC.$default$onAudioAttributesChanged(this, eventTime, audioAttributes);
    }

    @Override
    public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
        Player.Listener.CC.$default$onAudioAttributesChanged(this, audioAttributes);
    }

    @Override
    public void onAudioCodecError(AnalyticsListener.EventTime eventTime, Exception exc) {
        AnalyticsListener.CC.$default$onAudioCodecError(this, eventTime, exc);
    }

    @Override
    public void onAudioDecoderInitialized(AnalyticsListener.EventTime eventTime, String str, long j) {
        AnalyticsListener.CC.$default$onAudioDecoderInitialized(this, eventTime, str, j);
    }

    @Override
    public void onAudioDecoderInitialized(AnalyticsListener.EventTime eventTime, String str, long j, long j2) {
        AnalyticsListener.CC.$default$onAudioDecoderInitialized(this, eventTime, str, j, j2);
    }

    @Override
    public void onAudioDecoderReleased(AnalyticsListener.EventTime eventTime, String str) {
        AnalyticsListener.CC.$default$onAudioDecoderReleased(this, eventTime, str);
    }

    @Override
    public void onAudioDisabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onAudioDisabled(this, eventTime, decoderCounters);
    }

    @Override
    public void onAudioEnabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onAudioEnabled(this, eventTime, decoderCounters);
    }

    @Override
    public void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format) {
        AnalyticsListener.CC.$default$onAudioInputFormatChanged(this, eventTime, format);
    }

    @Override
    public void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
        AnalyticsListener.CC.$default$onAudioInputFormatChanged(this, eventTime, format, decoderReuseEvaluation);
    }

    @Override
    public void onAudioPositionAdvancing(AnalyticsListener.EventTime eventTime, long j) {
        AnalyticsListener.CC.$default$onAudioPositionAdvancing(this, eventTime, j);
    }

    @Override
    public void onAudioSinkError(AnalyticsListener.EventTime eventTime, Exception exc) {
        AnalyticsListener.CC.$default$onAudioSinkError(this, eventTime, exc);
    }

    @Override
    public void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        AnalyticsListener.CC.$default$onAudioUnderrun(this, eventTime, i, j, j2);
    }

    @Override
    public void onAvailableCommandsChanged(Player.Commands commands) {
        Player.Listener.CC.$default$onAvailableCommandsChanged(this, commands);
    }

    @Override
    public void onAvailableCommandsChanged(AnalyticsListener.EventTime eventTime, Player.Commands commands) {
        AnalyticsListener.CC.$default$onAvailableCommandsChanged(this, eventTime, commands);
    }

    @Override
    public void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        AnalyticsListener.CC.$default$onBandwidthEstimate(this, eventTime, i, j, j2);
    }

    @Override
    public void onCues(AnalyticsListener.EventTime eventTime, CueGroup cueGroup) {
        AnalyticsListener.CC.$default$onCues(this, eventTime, cueGroup);
    }

    @Override
    public void onCues(AnalyticsListener.EventTime eventTime, List list) {
        AnalyticsListener.CC.$default$onCues(this, eventTime, list);
    }

    @Override
    public void onCues(CueGroup cueGroup) {
        Player.Listener.CC.$default$onCues(this, cueGroup);
    }

    @Override
    public void onCues(List list) {
        Player.Listener.CC.$default$onCues(this, list);
    }

    @Override
    public void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderDisabled(this, eventTime, i, decoderCounters);
    }

    @Override
    public void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderEnabled(this, eventTime, i, decoderCounters);
    }

    @Override
    public void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str, long j) {
        AnalyticsListener.CC.$default$onDecoderInitialized(this, eventTime, i, str, j);
    }

    @Override
    public void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int i, Format format) {
        AnalyticsListener.CC.$default$onDecoderInputFormatChanged(this, eventTime, i, format);
    }

    @Override
    public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
        Player.Listener.CC.$default$onDeviceInfoChanged(this, deviceInfo);
    }

    @Override
    public void onDeviceInfoChanged(AnalyticsListener.EventTime eventTime, DeviceInfo deviceInfo) {
        AnalyticsListener.CC.$default$onDeviceInfoChanged(this, eventTime, deviceInfo);
    }

    @Override
    public void onDeviceVolumeChanged(int i, boolean z) {
        Player.Listener.CC.$default$onDeviceVolumeChanged(this, i, z);
    }

    @Override
    public void onDeviceVolumeChanged(AnalyticsListener.EventTime eventTime, int i, boolean z) {
        AnalyticsListener.CC.$default$onDeviceVolumeChanged(this, eventTime, i, z);
    }

    @Override
    public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onDownstreamFormatChanged(this, eventTime, mediaLoadData);
    }

    @Override
    public void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysLoaded(this, eventTime);
    }

    @Override
    public void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRemoved(this, eventTime);
    }

    @Override
    public void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRestored(this, eventTime);
    }

    @Override
    public void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionAcquired(this, eventTime);
    }

    @Override
    public void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onDrmSessionAcquired(this, eventTime, i);
    }

    @Override
    public void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception exc) {
        AnalyticsListener.CC.$default$onDrmSessionManagerError(this, eventTime, exc);
    }

    @Override
    public void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionReleased(this, eventTime);
    }

    @Override
    public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
        AnalyticsListener.CC.$default$onDroppedVideoFrames(this, eventTime, i, j);
    }

    @Override
    public void onEvents(Player player, Player.Events events) {
        Player.Listener.CC.$default$onEvents(this, player, events);
    }

    @Override
    public void onEvents(Player player, AnalyticsListener.Events events) {
        AnalyticsListener.CC.$default$onEvents(this, player, events);
    }

    @Override
    public void onIsLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onIsLoadingChanged(this, eventTime, z);
    }

    @Override
    public void onIsLoadingChanged(boolean z) {
        Player.Listener.CC.$default$onIsLoadingChanged(this, z);
    }

    @Override
    public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onIsPlayingChanged(this, eventTime, z);
    }

    @Override
    public void onIsPlayingChanged(boolean z) {
        Player.Listener.CC.$default$onIsPlayingChanged(this, z);
    }

    @Override
    public void onLoadCanceled(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCanceled(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override
    public void onLoadCompleted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCompleted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override
    public void onLoadError(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException iOException, boolean z) {
        AnalyticsListener.CC.$default$onLoadError(this, eventTime, loadEventInfo, mediaLoadData, iOException, z);
    }

    @Override
    public void onLoadStarted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadStarted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    @Override
    public void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onLoadingChanged(this, eventTime, z);
    }

    @Override
    public void onLoadingChanged(boolean z) {
        Player.Listener.CC.$default$onLoadingChanged(this, z);
    }

    @Override
    public void onMediaItemTransition(MediaItem mediaItem, int i) {
        Player.Listener.CC.$default$onMediaItemTransition(this, mediaItem, i);
    }

    @Override
    public void onMediaItemTransition(AnalyticsListener.EventTime eventTime, MediaItem mediaItem, int i) {
        AnalyticsListener.CC.$default$onMediaItemTransition(this, eventTime, mediaItem, i);
    }

    @Override
    public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
        Player.Listener.CC.$default$onMediaMetadataChanged(this, mediaMetadata);
    }

    @Override
    public void onMediaMetadataChanged(AnalyticsListener.EventTime eventTime, MediaMetadata mediaMetadata) {
        AnalyticsListener.CC.$default$onMediaMetadataChanged(this, eventTime, mediaMetadata);
    }

    @Override
    public void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
        AnalyticsListener.CC.$default$onMetadata(this, eventTime, metadata);
    }

    @Override
    public void onMetadata(Metadata metadata) {
        Player.Listener.CC.$default$onMetadata(this, metadata);
    }

    @Override
    public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
        AnalyticsListener.CC.$default$onPlayWhenReadyChanged(this, eventTime, z, i);
    }

    @Override
    public void onPlayWhenReadyChanged(boolean z, int i) {
        Player.Listener.CC.$default$onPlayWhenReadyChanged(this, z, i);
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        AnalyticsListener.CC.$default$onPlaybackParametersChanged(this, eventTime, playbackParameters);
    }

    @Override
    public void onPlaybackStateChanged(int i) {
        Player.Listener.CC.$default$onPlaybackStateChanged(this, i);
    }

    @Override
    public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPlaybackStateChanged(this, eventTime, i);
    }

    @Override
    public void onPlaybackSuppressionReasonChanged(int i) {
        Player.Listener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
    }

    @Override
    public void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPlaybackSuppressionReasonChanged(this, eventTime, i);
    }

    @Override
    public void onPlayerError(final PlaybackException playbackException) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoPlayer.this.lambda$onPlayerError$1(playbackException);
            }
        });
    }

    @Override
    public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException playbackException) {
        AnalyticsListener.CC.$default$onPlayerError(this, eventTime, playbackException);
    }

    @Override
    public void onPlayerErrorChanged(PlaybackException playbackException) {
        Player.Listener.CC.$default$onPlayerErrorChanged(this, playbackException);
    }

    @Override
    public void onPlayerErrorChanged(AnalyticsListener.EventTime eventTime, PlaybackException playbackException) {
        AnalyticsListener.CC.$default$onPlayerErrorChanged(this, eventTime, playbackException);
    }

    @Override
    public void onPlayerReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onPlayerReleased(this, eventTime);
    }

    @Override
    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
        AnalyticsListener.CC.$default$onPlayerStateChanged(this, eventTime, z, i);
    }

    @Override
    public void onPlayerStateChanged(boolean z, int i) {
        maybeReportPlayerState();
        if (z && i == 3 && !isMuted() && this.shouldPauseOther) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.playerDidStartPlaying, this);
        }
        if (!this.videoPlayerReady && i == 3) {
            this.videoPlayerReady = true;
            checkPlayersReady();
        }
        if (i != 3) {
            this.audioUpdateHandler.removeCallbacksAndMessages(null);
            AudioVisualizerDelegate audioVisualizerDelegate = this.audioVisualizerDelegate;
            if (audioVisualizerDelegate != null) {
                audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
            }
        }
    }

    @Override
    public void onPositionDiscontinuity(int i) {
        Player.Listener.CC.$default$onPositionDiscontinuity(this, i);
    }

    @Override
    public void onPositionDiscontinuity(Player.PositionInfo positionInfo, Player.PositionInfo positionInfo2, int i) {
        if (i == 0) {
            this.repeatCount++;
        }
    }

    @Override
    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPositionDiscontinuity(this, eventTime, i);
    }

    @Override
    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, Player.PositionInfo positionInfo, Player.PositionInfo positionInfo2, int i) {
        AnalyticsListener.CC.$default$onPositionDiscontinuity(this, eventTime, positionInfo, positionInfo2, i);
    }

    @Override
    public void onRenderedFirstFrame() {
        this.delegate.onRenderedFirstFrame();
    }

    @Override
    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object obj, long j) {
        this.fallbackPosition = -9223372036854775807L;
        this.fallbackDuration = -9223372036854775807L;
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onRenderedFirstFrame(eventTime);
        }
    }

    @Override
    public void onRepeatModeChanged(int i) {
    }

    @Override
    public void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onRepeatModeChanged(this, eventTime, i);
    }

    @Override
    public void onSeekProcessed() {
        Player.Listener.CC.$default$onSeekProcessed(this);
    }

    @Override
    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onSeekFinished(eventTime);
        }
        Iterator it = this.seekFinishedListeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        this.seekFinishedListeners.clear();
    }

    @Override
    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onSeekStarted(eventTime);
        }
    }

    @Override
    public void onSkipSilenceEnabledChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onSkipSilenceEnabledChanged(this, eventTime, z);
    }

    @Override
    public void onSkipSilenceEnabledChanged(boolean z) {
        Player.Listener.CC.$default$onSkipSilenceEnabledChanged(this, z);
    }

    @Override
    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        return this.delegate.onSurfaceDestroyed(surfaceTexture);
    }

    @Override
    public void onSurfaceSizeChanged(int i, int i2) {
    }

    @Override
    public void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2) {
        AnalyticsListener.CC.$default$onSurfaceSizeChanged(this, eventTime, i, i2);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.delegate.onSurfaceTextureUpdated(surfaceTexture);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, int i) {
        Player.Listener.CC.$default$onTimelineChanged(this, timeline, i);
    }

    @Override
    public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onTimelineChanged(this, eventTime, i);
    }

    @Override
    public void onTracksChanged(Tracks tracks) {
        Player.Listener.CC.$default$onTracksChanged(this, tracks);
        Runnable runnable = this.onQualityChangeListener;
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    @Override
    public void onTracksChanged(AnalyticsListener.EventTime eventTime, Tracks tracks) {
        AnalyticsListener.CC.$default$onTracksChanged(this, eventTime, tracks);
    }

    @Override
    public void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onUpstreamDiscarded(this, eventTime, mediaLoadData);
    }

    @Override
    public void onVideoCodecError(AnalyticsListener.EventTime eventTime, Exception exc) {
        AnalyticsListener.CC.$default$onVideoCodecError(this, eventTime, exc);
    }

    @Override
    public void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str, long j) {
        AnalyticsListener.CC.$default$onVideoDecoderInitialized(this, eventTime, str, j);
    }

    @Override
    public void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str, long j, long j2) {
        AnalyticsListener.CC.$default$onVideoDecoderInitialized(this, eventTime, str, j, j2);
    }

    @Override
    public void onVideoDecoderReleased(AnalyticsListener.EventTime eventTime, String str) {
        AnalyticsListener.CC.$default$onVideoDecoderReleased(this, eventTime, str);
    }

    @Override
    public void onVideoDisabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onVideoDisabled(this, eventTime, decoderCounters);
    }

    @Override
    public void onVideoEnabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onVideoEnabled(this, eventTime, decoderCounters);
    }

    @Override
    public void onVideoFrameProcessingOffset(AnalyticsListener.EventTime eventTime, long j, int i) {
        AnalyticsListener.CC.$default$onVideoFrameProcessingOffset(this, eventTime, j, i);
    }

    @Override
    public void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format) {
        AnalyticsListener.CC.$default$onVideoInputFormatChanged(this, eventTime, format);
    }

    @Override
    public void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
        AnalyticsListener.CC.$default$onVideoInputFormatChanged(this, eventTime, format, decoderReuseEvaluation);
    }

    @Override
    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
        AnalyticsListener.CC.$default$onVideoSizeChanged(this, eventTime, i, i2, i3, f);
    }

    @Override
    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
        AnalyticsListener.CC.$default$onVideoSizeChanged(this, eventTime, videoSize);
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        this.delegate.onVideoSizeChanged(videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees, videoSize.pixelWidthHeightRatio);
        Player.Listener.CC.$default$onVideoSizeChanged(this, videoSize);
    }

    @Override
    public void onVolumeChanged(float f) {
        Player.Listener.CC.$default$onVolumeChanged(this, f);
    }

    @Override
    public void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
        AnalyticsListener.CC.$default$onVolumeChanged(this, eventTime, f);
    }

    public void pause() {
        this.mixedPlayWhenReady = false;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
        ExoPlayer exoPlayer2 = this.audioPlayer;
        if (exoPlayer2 != null) {
            exoPlayer2.setPlayWhenReady(false);
        }
        if (this.audioVisualizerDelegate != null) {
            this.audioUpdateHandler.removeCallbacksAndMessages(null);
            this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
        }
    }

    public void play() {
        this.mixedPlayWhenReady = true;
        if (!this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            ExoPlayer exoPlayer = this.player;
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(true);
            }
            ExoPlayer exoPlayer2 = this.audioPlayer;
            if (exoPlayer2 != null) {
                exoPlayer2.setPlayWhenReady(true);
                return;
            }
            return;
        }
        ExoPlayer exoPlayer3 = this.player;
        if (exoPlayer3 != null) {
            exoPlayer3.setPlayWhenReady(false);
        }
        ExoPlayer exoPlayer4 = this.audioPlayer;
        if (exoPlayer4 != null) {
            exoPlayer4.setPlayWhenReady(false);
        }
    }

    public void preparePlayer(Uri uri, String str) {
        preparePlayer(uri, str, 3);
    }

    public void preparePlayer(Uri uri, String str, int i) {
        this.videoQualities = null;
        this.videoQualityToSelect = null;
        this.videoUri = uri;
        this.videoType = str;
        this.audioUri = null;
        this.audioType = null;
        boolean z = false;
        this.loopingMediaSource = false;
        this.autoIsOriginal = false;
        this.currentStreamIsHls = false;
        this.videoPlayerReady = false;
        this.mixedAudio = false;
        this.currentUri = uri;
        String scheme = uri != null ? uri.getScheme() : null;
        if (scheme != null && !scheme.startsWith("file")) {
            z = true;
        }
        this.isStreaming = z;
        ensurePlayerCreated();
        this.player.setMediaSource(mediaSourceFromUri(uri, str), true);
        this.player.prepare();
    }

    public void preparePlayer(ArrayList arrayList, Quality quality) {
        ArrayList arrayList2;
        this.videoQualities = arrayList;
        this.videoQualityToSelect = quality;
        this.videoUri = null;
        this.videoType = "hls";
        this.audioUri = null;
        this.audioType = null;
        this.loopingMediaSource = false;
        this.autoIsOriginal = false;
        this.videoPlayerReady = false;
        this.mixedAudio = false;
        this.currentUri = null;
        this.isStreaming = true;
        ensurePlayerCreated();
        this.currentStreamIsHls = false;
        this.selectedQualityIndex = (quality == null || (arrayList2 = this.videoQualities) == null) ? -1 : arrayList2.indexOf(quality);
        setSelectedQuality(true, quality);
        if (this.autoIsOriginal) {
            this.selectedQualityIndex = -1;
        }
    }

    public void preparePlayerLoop(Uri uri, String str, Uri uri2, String str2) {
        Uri uri3;
        String str3;
        LoopingMediaSource loopingMediaSource = null;
        this.videoQualities = null;
        this.videoQualityToSelect = null;
        this.videoUri = uri;
        this.audioUri = uri2;
        this.videoType = str;
        this.audioType = str2;
        this.loopingMediaSource = true;
        this.currentStreamIsHls = false;
        this.mixedAudio = true;
        this.audioPlayerReady = false;
        this.videoPlayerReady = false;
        ensurePlayerCreated();
        LoopingMediaSource loopingMediaSource2 = null;
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                uri3 = uri;
                str3 = str;
            } else {
                uri3 = uri2;
                str3 = str2;
            }
            LoopingMediaSource loopingMediaSource3 = new LoopingMediaSource(mediaSourceFromUri(uri3, str3));
            if (i == 0) {
                loopingMediaSource = loopingMediaSource3;
            } else {
                loopingMediaSource2 = loopingMediaSource3;
            }
        }
        this.player.setMediaSource(loopingMediaSource, true);
        this.player.prepare();
        this.audioPlayer.setMediaSource(loopingMediaSource2, true);
        this.audioPlayer.prepare();
        activePlayers.add(Integer.valueOf(this.playerId));
    }

    public void releasePlayer(boolean z) {
        activePlayers.remove(Integer.valueOf(this.playerId));
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.release();
            this.player = null;
        }
        ExoPlayer exoPlayer2 = this.audioPlayer;
        if (exoPlayer2 != null) {
            exoPlayer2.release();
            this.audioPlayer = null;
        }
        if (this.shouldPauseOther) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
        }
        playerCounter--;
    }

    public void seekTo(long j) {
        seekTo(j, false);
    }

    public void seekTo(long j, boolean z) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.setSeekParameters(z ? SeekParameters.CLOSEST_SYNC : SeekParameters.EXACT);
            this.player.seekTo(j);
        }
    }

    public void seekTo(long j, boolean z, Runnable runnable) {
        if (this.player != null) {
            if (runnable != null) {
                this.seekFinishedListeners.add(runnable);
            }
            this.player.setSeekParameters(z ? SeekParameters.CLOSEST_SYNC : SeekParameters.EXACT);
            this.player.seekTo(j);
        }
    }

    public void setAudioVisualizerDelegate(AudioVisualizerDelegate audioVisualizerDelegate) {
        this.audioVisualizerDelegate = audioVisualizerDelegate;
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public void setIsStory() {
        this.isStory = true;
    }

    public void setLooping(boolean z) {
        if (this.looping != z) {
            this.looping = z;
            ExoPlayer exoPlayer = this.player;
            if (exoPlayer != null) {
                exoPlayer.setRepeatMode(z ? 2 : 0);
            }
        }
    }

    public void setMute(boolean z) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.setVolume(z ? 0.0f : 1.0f);
        }
        ExoPlayer exoPlayer2 = this.audioPlayer;
        if (exoPlayer2 != null) {
            exoPlayer2.setVolume(z ? 0.0f : 1.0f);
        }
    }

    public void setOnQualityChangeListener(Runnable runnable) {
        this.onQualityChangeListener = runnable;
    }

    public void setPlayWhenReady(boolean z) {
        this.mixedPlayWhenReady = z;
        if (z && this.mixedAudio && (!this.audioPlayerReady || !this.videoPlayerReady)) {
            ExoPlayer exoPlayer = this.player;
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
            }
            ExoPlayer exoPlayer2 = this.audioPlayer;
            if (exoPlayer2 != null) {
                exoPlayer2.setPlayWhenReady(false);
                return;
            }
            return;
        }
        this.autoplay = z;
        ExoPlayer exoPlayer3 = this.player;
        if (exoPlayer3 != null) {
            exoPlayer3.setPlayWhenReady(z);
        }
        ExoPlayer exoPlayer4 = this.audioPlayer;
        if (exoPlayer4 != null) {
            exoPlayer4.setPlayWhenReady(z);
        }
    }

    public void setPlaybackSpeed(float f) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.setPlaybackParameters(new PlaybackParameters(f, f > 1.0f ? 0.98f : 1.0f));
        }
    }

    public void setSelectedQuality(int i) {
        if (this.player == null || i == this.selectedQualityIndex) {
            return;
        }
        this.selectedQualityIndex = i;
        ArrayList arrayList = this.videoQualities;
        setSelectedQuality(false, (arrayList == null || i < 0 || i >= arrayList.size()) ? null : (Quality) this.videoQualities.get(i));
    }

    public void setStreamType(int i) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(i == 0 ? 2 : 1).build(), this.handleAudioFocus);
        }
        ExoPlayer exoPlayer2 = this.audioPlayer;
        if (exoPlayer2 != null) {
            exoPlayer2.setAudioAttributes(new AudioAttributes.Builder().setUsage(i != 0 ? 1 : 2).build(), true);
        }
    }

    public void setSurface(Surface surface) {
        if (this.surface == surface) {
            return;
        }
        this.surface = surface;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        exoPlayer.setVideoSurface(surface);
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        if (this.surfaceView == surfaceView) {
            return;
        }
        this.surfaceView = surfaceView;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        exoPlayer.setVideoSurfaceView(surfaceView);
    }

    public void setTextureView(TextureView textureView) {
        if (this.textureView == textureView) {
            return;
        }
        this.textureView = textureView;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        exoPlayer.setVideoTextureView(textureView);
    }

    public void setVolume(float f) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.setVolume(f);
        }
        ExoPlayer exoPlayer2 = this.audioPlayer;
        if (exoPlayer2 != null) {
            exoPlayer2.setVolume(f);
        }
    }

    public void setWorkerQueue(DispatchQueue dispatchQueue) {
        this.workerQueue = dispatchQueue;
        this.player.setWorkerQueue(dispatchQueue);
    }
}
