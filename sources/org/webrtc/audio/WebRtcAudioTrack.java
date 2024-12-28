package org.webrtc.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import java.nio.ByteBuffer;
import org.webrtc.Logging;
import org.webrtc.ThreadUtils;
import org.webrtc.audio.JavaAudioDeviceModule;

public class WebRtcAudioTrack {
    private static final int AUDIO_TRACK_START = 0;
    private static final int AUDIO_TRACK_STOP = 1;
    private static final long AUDIO_TRACK_THREAD_JOIN_TIMEOUT_MS = 2000;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final int DEFAULT_USAGE = getDefaultUsageAttribute();
    private static final String TAG = "WebRtcAudioTrackExternal";
    private final AudioAttributes audioAttributes;
    private final AudioManager audioManager;
    private AudioTrackThread audioThread;
    private AudioTrack audioTrack;
    private ByteBuffer byteBuffer;
    private final Context context;
    private byte[] emptyBytes;
    private final JavaAudioDeviceModule.AudioTrackErrorCallback errorCallback;
    private int initialBufferSizeInFrames;
    private long nativeAudioTrack;
    private volatile boolean speakerMute;
    private final JavaAudioDeviceModule.AudioTrackStateCallback stateCallback;
    private final ThreadUtils.ThreadChecker threadChecker;
    private boolean useLowLatency;
    private final VolumeLogger volumeLogger;

    private class AudioTrackThread extends Thread {
        private LowLatencyAudioBufferManager bufferManager;
        private volatile boolean keepAlive;

        public AudioTrackThread(String str) {
            super(str);
            this.keepAlive = true;
            this.bufferManager = new LowLatencyAudioBufferManager();
        }

        private int writeBytes(AudioTrack audioTrack, ByteBuffer byteBuffer, int i) {
            int write;
            if (Build.VERSION.SDK_INT < 21) {
                return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), i);
            }
            write = audioTrack.write(byteBuffer, i, 0);
            return write;
        }

        @Override
        public void run() {
            Process.setThreadPriority(-19);
            Logging.d("WebRtcAudioTrackExternal", "AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
            WebRtcAudioTrack.assertTrue(WebRtcAudioTrack.this.audioTrack.getPlayState() == 3);
            WebRtcAudioTrack.this.doAudioTrackStateCallback(0);
            int capacity = WebRtcAudioTrack.this.byteBuffer.capacity();
            while (this.keepAlive) {
                WebRtcAudioTrack.nativeGetPlayoutData(WebRtcAudioTrack.this.nativeAudioTrack, capacity);
                WebRtcAudioTrack.assertTrue(capacity <= WebRtcAudioTrack.this.byteBuffer.remaining());
                if (WebRtcAudioTrack.this.speakerMute) {
                    WebRtcAudioTrack.this.byteBuffer.clear();
                    WebRtcAudioTrack.this.byteBuffer.put(WebRtcAudioTrack.this.emptyBytes);
                    WebRtcAudioTrack.this.byteBuffer.position(0);
                }
                int writeBytes = writeBytes(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, capacity);
                if (writeBytes != capacity) {
                    Logging.e("WebRtcAudioTrackExternal", "AudioTrack.write played invalid number of bytes: " + writeBytes);
                    if (writeBytes < 0) {
                        this.keepAlive = false;
                        WebRtcAudioTrack.this.reportWebRtcAudioTrackError("AudioTrack.write failed: " + writeBytes);
                    }
                }
                if (WebRtcAudioTrack.this.useLowLatency) {
                    this.bufferManager.maybeAdjustBufferSize(WebRtcAudioTrack.this.audioTrack);
                }
                WebRtcAudioTrack.this.byteBuffer.rewind();
            }
        }

        public void stopThread() {
            Logging.d("WebRtcAudioTrackExternal", "stopThread");
            this.keepAlive = false;
        }
    }

    WebRtcAudioTrack(Context context, AudioManager audioManager) {
        this(context, audioManager, null, null, null, false);
    }

    public WebRtcAudioTrack(Context context, AudioManager audioManager, AudioAttributes audioAttributes, JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback, JavaAudioDeviceModule.AudioTrackStateCallback audioTrackStateCallback, boolean z) {
        ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();
        this.threadChecker = threadChecker;
        threadChecker.detachThread();
        this.context = context;
        this.audioManager = audioManager;
        this.audioAttributes = audioAttributes;
        this.errorCallback = audioTrackErrorCallback;
        this.stateCallback = audioTrackStateCallback;
        this.volumeLogger = new VolumeLogger(audioManager);
        this.useLowLatency = z;
        Logging.d("WebRtcAudioTrackExternal", "ctor" + WebRtcAudioUtils.getThreadInfo());
    }

    private int GetPlayoutUnderrunCount() {
        int underrunCount;
        if (Build.VERSION.SDK_INT < 24) {
            return -2;
        }
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack == null) {
            return -1;
        }
        underrunCount = audioTrack.getUnderrunCount();
        return underrunCount;
    }

    private static AudioAttributes.Builder applyAttributesOnQOrHigher(AudioAttributes.Builder builder, AudioAttributes audioAttributes) {
        int allowedCapturePolicy;
        AudioAttributes.Builder allowedCapturePolicy2;
        allowedCapturePolicy = audioAttributes.getAllowedCapturePolicy();
        allowedCapturePolicy2 = builder.setAllowedCapturePolicy(allowedCapturePolicy);
        return allowedCapturePolicy2;
    }

    public static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private int channelCountToConfiguration(int i) {
        return i == 1 ? 4 : 12;
    }

    private static AudioTrack createAudioTrackOnLollipopOrHigher(int i, int i2, int i3, AudioAttributes audioAttributes) {
        AudioFormat.Builder encoding;
        AudioFormat.Builder sampleRate;
        AudioFormat.Builder channelMask;
        AudioFormat build;
        Logging.d("WebRtcAudioTrackExternal", "createAudioTrackOnLollipopOrHigher");
        logNativeOutputSampleRate(i);
        AudioAttributes audioAttributes2 = getAudioAttributes(audioAttributes);
        encoding = new AudioFormat.Builder().setEncoding(2);
        sampleRate = encoding.setSampleRate(i);
        channelMask = sampleRate.setChannelMask(i2);
        build = channelMask.build();
        return new AudioTrack(audioAttributes2, build, i3, 1, 0);
    }

    private static AudioTrack createAudioTrackOnLowerThanLollipop(int i, int i2, int i3) {
        return new AudioTrack(0, i, i2, 2, i3, 1);
    }

    private static AudioTrack createAudioTrackOnOreoOrHigher(int i, int i2, int i3, AudioAttributes audioAttributes) {
        AudioTrack.Builder audioAttributes2;
        AudioFormat.Builder encoding;
        AudioFormat.Builder sampleRate;
        AudioFormat.Builder channelMask;
        AudioFormat build;
        AudioTrack.Builder audioFormat;
        AudioTrack.Builder bufferSizeInBytes;
        AudioTrack.Builder performanceMode;
        AudioTrack.Builder transferMode;
        AudioTrack.Builder sessionId;
        AudioTrack build2;
        Logging.d("WebRtcAudioTrackExternal", "createAudioTrackOnOreoOrHigher");
        logNativeOutputSampleRate(i);
        audioAttributes2 = new AudioTrack.Builder().setAudioAttributes(getAudioAttributes(audioAttributes));
        encoding = new AudioFormat.Builder().setEncoding(2);
        sampleRate = encoding.setSampleRate(i);
        channelMask = sampleRate.setChannelMask(i2);
        build = channelMask.build();
        audioFormat = audioAttributes2.setAudioFormat(build);
        bufferSizeInBytes = audioFormat.setBufferSizeInBytes(i3);
        performanceMode = bufferSizeInBytes.setPerformanceMode(1);
        transferMode = performanceMode.setTransferMode(1);
        sessionId = transferMode.setSessionId(0);
        build2 = sessionId.build();
        return build2;
    }

    public void doAudioTrackStateCallback(int i) {
        Logging.d("WebRtcAudioTrackExternal", "doAudioTrackStateCallback: " + i);
        JavaAudioDeviceModule.AudioTrackStateCallback audioTrackStateCallback = this.stateCallback;
        if (audioTrackStateCallback != null) {
            if (i == 0) {
                audioTrackStateCallback.onWebRtcAudioTrackStart();
            } else if (i == 1) {
                audioTrackStateCallback.onWebRtcAudioTrackStop();
            } else {
                Logging.e("WebRtcAudioTrackExternal", "Invalid audio state");
            }
        }
    }

    private static AudioAttributes getAudioAttributes(AudioAttributes audioAttributes) {
        AudioAttributes.Builder usage;
        AudioAttributes.Builder contentType;
        AudioAttributes build;
        int usage2;
        int contentType2;
        int contentType3;
        int usage3;
        usage = new AudioAttributes.Builder().setUsage(DEFAULT_USAGE);
        contentType = usage.setContentType(1);
        if (audioAttributes != null) {
            usage2 = audioAttributes.getUsage();
            if (usage2 != 0) {
                usage3 = audioAttributes.getUsage();
                contentType.setUsage(usage3);
            }
            contentType2 = audioAttributes.getContentType();
            if (contentType2 != 0) {
                contentType3 = audioAttributes.getContentType();
                contentType.setContentType(contentType3);
            }
            contentType.setFlags(WebRtcAudioTrack$$ExternalSyntheticApiModelOutline6.m(audioAttributes));
            if (Build.VERSION.SDK_INT >= 29) {
                contentType = applyAttributesOnQOrHigher(contentType, audioAttributes);
            }
        }
        build = contentType.build();
        return build;
    }

    private int getBufferSizeInFrames() {
        int bufferSizeInFrames;
        if (Build.VERSION.SDK_INT < 23) {
            return -1;
        }
        bufferSizeInFrames = this.audioTrack.getBufferSizeInFrames();
        return bufferSizeInFrames;
    }

    private static int getDefaultUsageAttribute() {
        return Build.VERSION.SDK_INT >= 21 ? 2 : 0;
    }

    private int getInitialBufferSizeInFrames() {
        return this.initialBufferSizeInFrames;
    }

    private int getStreamMaxVolume() {
        this.threadChecker.checkIsOnValidThread();
        Logging.d("WebRtcAudioTrackExternal", "getStreamMaxVolume");
        return this.audioManager.getStreamMaxVolume(0);
    }

    private int getStreamVolume() {
        this.threadChecker.checkIsOnValidThread();
        Logging.d("WebRtcAudioTrackExternal", "getStreamVolume");
        return this.audioManager.getStreamVolume(0);
    }

    private int initPlayout(int i, int i2, double d) {
        int bufferSizeInFrames;
        this.threadChecker.checkIsOnValidThread();
        Logging.d("WebRtcAudioTrackExternal", "initPlayout(sampleRate=" + i + ", channels=" + i2 + ", bufferSizeFactor=" + d + ")");
        this.byteBuffer = ByteBuffer.allocateDirect(i2 * 2 * (i / 100));
        StringBuilder sb = new StringBuilder();
        sb.append("byteBuffer.capacity: ");
        sb.append(this.byteBuffer.capacity());
        Logging.d("WebRtcAudioTrackExternal", sb.toString());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.nativeAudioTrack, this.byteBuffer);
        int channelCountToConfiguration = channelCountToConfiguration(i2);
        double minBufferSize = AudioTrack.getMinBufferSize(i, channelCountToConfiguration, 2);
        Double.isNaN(minBufferSize);
        int i3 = (int) (minBufferSize * d);
        Logging.d("WebRtcAudioTrackExternal", "minBufferSizeInBytes: " + i3);
        if (i3 < this.byteBuffer.capacity()) {
            reportWebRtcAudioTrackInitError("AudioTrack.getMinBufferSize returns an invalid value.");
            return -1;
        }
        if (d > 1.0d) {
            this.useLowLatency = false;
        }
        if (this.audioTrack != null) {
            reportWebRtcAudioTrackInitError("Conflict with existing AudioTrack.");
            return -1;
        }
        try {
            AudioTrack createAudioTrackOnLollipopOrHigher = (!this.useLowLatency || Build.VERSION.SDK_INT < 26) ? Build.VERSION.SDK_INT >= 21 ? createAudioTrackOnLollipopOrHigher(i, channelCountToConfiguration, i3, this.audioAttributes) : createAudioTrackOnLowerThanLollipop(i, channelCountToConfiguration, i3) : createAudioTrackOnOreoOrHigher(i, channelCountToConfiguration, i3, this.audioAttributes);
            this.audioTrack = createAudioTrackOnLollipopOrHigher;
            if (createAudioTrackOnLollipopOrHigher == null || createAudioTrackOnLollipopOrHigher.getState() != 1) {
                reportWebRtcAudioTrackInitError("Initialization of audio track failed.");
                releaseAudioResources();
                return -1;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                bufferSizeInFrames = this.audioTrack.getBufferSizeInFrames();
                this.initialBufferSizeInFrames = bufferSizeInFrames;
            } else {
                this.initialBufferSizeInFrames = -1;
            }
            logMainParameters();
            logMainParametersExtended();
            return i3;
        } catch (IllegalArgumentException e) {
            reportWebRtcAudioTrackInitError(e.getMessage());
            releaseAudioResources();
            return -1;
        }
    }

    private boolean isVolumeFixed() {
        boolean isVolumeFixed;
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        isVolumeFixed = this.audioManager.isVolumeFixed();
        return isVolumeFixed;
    }

    private void logBufferCapacityInFrames() {
        int bufferCapacityInFrames;
        if (Build.VERSION.SDK_INT >= 24) {
            StringBuilder sb = new StringBuilder();
            sb.append("AudioTrack: buffer capacity in frames: ");
            bufferCapacityInFrames = this.audioTrack.getBufferCapacityInFrames();
            sb.append(bufferCapacityInFrames);
            Logging.d("WebRtcAudioTrackExternal", sb.toString());
        }
    }

    private void logBufferSizeInFrames() {
        int bufferSizeInFrames;
        if (Build.VERSION.SDK_INT >= 23) {
            StringBuilder sb = new StringBuilder();
            sb.append("AudioTrack: buffer size in frames: ");
            bufferSizeInFrames = this.audioTrack.getBufferSizeInFrames();
            sb.append(bufferSizeInFrames);
            Logging.d("WebRtcAudioTrackExternal", sb.toString());
        }
    }

    private void logMainParameters() {
        Logging.d("WebRtcAudioTrackExternal", "AudioTrack: session ID: " + this.audioTrack.getAudioSessionId() + ", channels: " + this.audioTrack.getChannelCount() + ", sample rate: " + this.audioTrack.getSampleRate() + ", max gain: " + AudioTrack.getMaxVolume());
    }

    private void logMainParametersExtended() {
        logBufferSizeInFrames();
        logBufferCapacityInFrames();
    }

    private static void logNativeOutputSampleRate(int i) {
        int nativeOutputSampleRate = AudioTrack.getNativeOutputSampleRate(0);
        Logging.d("WebRtcAudioTrackExternal", "nativeOutputSampleRate: " + nativeOutputSampleRate);
        if (i != nativeOutputSampleRate) {
            Logging.w("WebRtcAudioTrackExternal", "Unable to use fast mode since requested sample rate is not native");
        }
    }

    private void logUnderrunCount() {
        int underrunCount;
        if (Build.VERSION.SDK_INT >= 24) {
            StringBuilder sb = new StringBuilder();
            sb.append("underrun count: ");
            underrunCount = this.audioTrack.getUnderrunCount();
            sb.append(underrunCount);
            Logging.d("WebRtcAudioTrackExternal", sb.toString());
        }
    }

    private static native void nativeCacheDirectBufferAddress(long j, ByteBuffer byteBuffer);

    public static native void nativeGetPlayoutData(long j, int i);

    private void releaseAudioResources() {
        Logging.d("WebRtcAudioTrackExternal", "releaseAudioResources");
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            audioTrack.release();
            this.audioTrack = null;
        }
    }

    public void reportWebRtcAudioTrackError(String str) {
        Logging.e("WebRtcAudioTrackExternal", "Run-time playback error: " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = this.errorCallback;
        if (audioTrackErrorCallback != null) {
            audioTrackErrorCallback.onWebRtcAudioTrackError(str);
        }
    }

    private void reportWebRtcAudioTrackInitError(String str) {
        Logging.e("WebRtcAudioTrackExternal", "Init playout error: " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = this.errorCallback;
        if (audioTrackErrorCallback != null) {
            audioTrackErrorCallback.onWebRtcAudioTrackInitError(str);
        }
    }

    private void reportWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode audioTrackStartErrorCode, String str) {
        Logging.e("WebRtcAudioTrackExternal", "Start playout error: " + audioTrackStartErrorCode + ". " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = this.errorCallback;
        if (audioTrackErrorCallback != null) {
            audioTrackErrorCallback.onWebRtcAudioTrackStartError(audioTrackStartErrorCode, str);
        }
    }

    private boolean setStreamVolume(int i) {
        this.threadChecker.checkIsOnValidThread();
        Logging.d("WebRtcAudioTrackExternal", "setStreamVolume(" + i + ")");
        if (isVolumeFixed()) {
            Logging.e("WebRtcAudioTrackExternal", "The device implements a fixed volume policy.");
            return false;
        }
        this.audioManager.setStreamVolume(0, i, 0);
        return true;
    }

    private boolean startPlayout() {
        this.threadChecker.checkIsOnValidThread();
        this.volumeLogger.start();
        Logging.d("WebRtcAudioTrackExternal", "startPlayout");
        assertTrue(this.audioTrack != null);
        assertTrue(this.audioThread == null);
        try {
            this.audioTrack.play();
        } catch (IllegalStateException e) {
            reportWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode.AUDIO_TRACK_START_EXCEPTION, "AudioTrack.play failed: " + e.getMessage());
        }
        if (this.audioTrack.getPlayState() == 3) {
            AudioTrackThread audioTrackThread = new AudioTrackThread("AudioTrackJavaThread");
            this.audioThread = audioTrackThread;
            audioTrackThread.start();
            return true;
        }
        reportWebRtcAudioTrackStartError(JavaAudioDeviceModule.AudioTrackStartErrorCode.AUDIO_TRACK_START_STATE_MISMATCH, "AudioTrack.play failed - incorrect state :" + this.audioTrack.getPlayState());
        releaseAudioResources();
        return false;
    }

    private boolean stopPlayout() {
        this.threadChecker.checkIsOnValidThread();
        this.volumeLogger.stop();
        Logging.d("WebRtcAudioTrackExternal", "stopPlayout");
        assertTrue(this.audioThread != null);
        logUnderrunCount();
        this.audioThread.stopThread();
        Logging.d("WebRtcAudioTrackExternal", "Stopping the AudioTrackThread...");
        this.audioThread.interrupt();
        if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000L)) {
            Logging.e("WebRtcAudioTrackExternal", "Join of AudioTrackThread timed out.");
            WebRtcAudioUtils.logAudioState("WebRtcAudioTrackExternal", this.context, this.audioManager);
        }
        Logging.d("WebRtcAudioTrackExternal", "AudioTrackThread has now been stopped.");
        this.audioThread = null;
        if (this.audioTrack != null) {
            Logging.d("WebRtcAudioTrackExternal", "Calling AudioTrack.stop...");
            try {
                this.audioTrack.stop();
                Logging.d("WebRtcAudioTrackExternal", "AudioTrack.stop is done.");
                doAudioTrackStateCallback(1);
            } catch (IllegalStateException e) {
                Logging.e("WebRtcAudioTrackExternal", "AudioTrack.stop failed: " + e.getMessage());
            }
        }
        releaseAudioResources();
        return true;
    }

    public void setNativeAudioTrack(long j) {
        this.nativeAudioTrack = j;
    }

    public void setSpeakerMute(boolean z) {
        Logging.w("WebRtcAudioTrackExternal", "setSpeakerMute(" + z + ")");
        this.speakerMute = z;
    }
}
