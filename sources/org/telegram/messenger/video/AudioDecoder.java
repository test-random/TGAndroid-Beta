package org.telegram.messenger.video;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;

public class AudioDecoder {
    private static final int TIMEOUT_USEC = 0;
    private boolean allInputExtracted;
    private int audioIndex;
    private MediaCodec decoder;
    private boolean decodingDone;
    private long endTimeUs;
    private final MediaExtractor extractor;
    private boolean loopingEnabled;
    private long startTimeUs;
    private int trackIndex;

    public static class DecodedBufferData {
        public ByteBuffer byteBuffer = null;
        public int index = -1;
        public int size = 0;
        public long presentationTimeUs = 0;
        public int flags = 0;
        public int offset = 0;
    }

    public AudioDecoder(String str) {
        this.audioIndex = -1;
        MediaExtractor mediaExtractor = new MediaExtractor();
        this.extractor = mediaExtractor;
        mediaExtractor.setDataSource(str);
        init();
    }

    public AudioDecoder(String str, int i) {
        this.audioIndex = -1;
        MediaExtractor mediaExtractor = new MediaExtractor();
        this.extractor = mediaExtractor;
        mediaExtractor.setDataSource(str);
        this.audioIndex = i;
        init();
    }

    private void init() {
        selectTrack();
        MediaFormat trackFormat = this.extractor.getTrackFormat(this.trackIndex);
        MediaCodec createDecoderByType = MediaCodec.createDecoderByType(trackFormat.getString("mime"));
        this.decoder = createDecoderByType;
        createDecoderByType.configure(trackFormat, (Surface) null, (MediaCrypto) null, 0);
        this.startTimeUs = 0L;
        try {
            this.endTimeUs = this.extractor.getTrackFormat(this.trackIndex).getLong("durationUs");
        } catch (Exception e) {
            FileLog.e(e);
            this.endTimeUs = -1L;
        }
    }

    private void selectTrack() {
        int i = this.audioIndex;
        this.trackIndex = i;
        if (i == -1) {
            int trackCount = this.extractor.getTrackCount();
            int i2 = 0;
            while (true) {
                if (i2 < trackCount) {
                    String string = this.extractor.getTrackFormat(i2).getString("mime");
                    if (string != null && string.startsWith("audio/")) {
                        this.trackIndex = i2;
                        break;
                    }
                    i2++;
                } else {
                    break;
                }
            }
        }
        int i3 = this.trackIndex;
        if (i3 < 0) {
            throw new RuntimeException("No audio track found in source");
        }
        this.extractor.selectTrack(i3);
    }

    public DecodedBufferData decode() {
        int usToBytes;
        int limit;
        int dequeueInputBuffer;
        DecodedBufferData decodedBufferData = new DecodedBufferData();
        boolean z = false;
        while (!z && !this.decodingDone) {
            if (!this.allInputExtracted && (dequeueInputBuffer = this.decoder.dequeueInputBuffer(0L)) >= 0) {
                int readSampleData = this.extractor.readSampleData(Build.VERSION.SDK_INT >= 21 ? this.decoder.getInputBuffer(dequeueInputBuffer) : this.decoder.getInputBuffers()[dequeueInputBuffer], 0);
                if (readSampleData >= 0 && this.extractor.getSampleTime() <= this.endTimeUs) {
                    this.decoder.queueInputBuffer(dequeueInputBuffer, 0, readSampleData, this.extractor.getSampleTime(), this.extractor.getSampleFlags());
                    this.extractor.advance();
                } else if (this.loopingEnabled) {
                    this.decoder.flush();
                    this.extractor.seekTo(this.startTimeUs, 0);
                } else {
                    this.decoder.queueInputBuffer(dequeueInputBuffer, 0, 0, 0L, 4);
                    this.allInputExtracted = true;
                }
            }
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int dequeueOutputBuffer = this.decoder.dequeueOutputBuffer(bufferInfo, 0L);
            if (dequeueOutputBuffer >= 0) {
                decodedBufferData.byteBuffer = Build.VERSION.SDK_INT >= 21 ? this.decoder.getOutputBuffer(dequeueOutputBuffer) : this.decoder.getOutputBuffers()[dequeueOutputBuffer];
                decodedBufferData.index = dequeueOutputBuffer;
                decodedBufferData.size = bufferInfo.size;
                long j = bufferInfo.presentationTimeUs;
                decodedBufferData.presentationTimeUs = j;
                decodedBufferData.flags = bufferInfo.flags;
                decodedBufferData.offset = bufferInfo.offset;
                long j2 = this.startTimeUs;
                if (j < j2) {
                    int position = decodedBufferData.byteBuffer.position() + AudioConversions.usToBytes(j2 - j, getSampleRate(), getChannelCount());
                    if (position <= decodedBufferData.byteBuffer.limit()) {
                        decodedBufferData.byteBuffer.position(position);
                    }
                }
                long bytesToUs = decodedBufferData.presentationTimeUs + AudioConversions.bytesToUs(decodedBufferData.size, getSampleRate(), getChannelCount());
                long j3 = this.endTimeUs;
                if (bytesToUs > j3 && (usToBytes = AudioConversions.usToBytes(bytesToUs - j3, getSampleRate(), getChannelCount())) > 0 && (limit = decodedBufferData.byteBuffer.limit() - usToBytes) >= decodedBufferData.byteBuffer.position()) {
                    decodedBufferData.byteBuffer.limit(limit);
                }
                if ((bufferInfo.flags & 4) != 0) {
                    this.decodingDone = true;
                }
                if (decodedBufferData.byteBuffer.remaining() > 0) {
                    z = true;
                }
            }
        }
        return decodedBufferData;
    }

    public int getBitrateRate() {
        try {
            try {
                return getOutputMediaFormat().getInteger("bitrate");
            } catch (Exception unused) {
                return getInputMediaFormat().getInteger("bitrate");
            }
        } catch (Exception unused2) {
            return -1;
        }
    }

    public int getChannelCount() {
        try {
            return getOutputMediaFormat().getInteger("channel-count");
        } catch (Exception e) {
            FileLog.e(e);
            try {
                return getInputMediaFormat().getInteger("channel-count");
            } catch (Exception e2) {
                FileLog.e(e2);
                return -1;
            }
        }
    }

    public long getDurationUs() {
        try {
            return getOutputMediaFormat().getLong("durationUs");
        } catch (Exception e) {
            FileLog.e(e);
            try {
                return getInputMediaFormat().getLong("durationUs");
            } catch (Exception e2) {
                FileLog.e(e2);
                return -1L;
            }
        }
    }

    public long getEndTimeUs() {
        return this.endTimeUs;
    }

    public MediaFormat getInputMediaFormat() {
        try {
            return this.extractor.getTrackFormat(this.trackIndex);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public MediaFormat getMediaFormat() {
        try {
            return getOutputMediaFormat() != null ? getOutputMediaFormat() : getInputMediaFormat();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public MediaFormat getOutputMediaFormat() {
        try {
            return this.decoder.getOutputFormat();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public int getSampleRate() {
        try {
            return getOutputMediaFormat().getInteger("sample-rate");
        } catch (Exception e) {
            FileLog.e(e);
            try {
                return getInputMediaFormat().getInteger("sample-rate");
            } catch (Exception e2) {
                FileLog.e(e2);
                return -1;
            }
        }
    }

    public long getStartTimeUs() {
        return this.startTimeUs;
    }

    public boolean isDecodingDone() {
        return this.decodingDone;
    }

    public boolean isLoopingEnabled() {
        return this.loopingEnabled;
    }

    public void release() {
        stop();
        this.decoder.release();
        this.extractor.release();
    }

    public void releaseOutputBuffer(int i) {
        this.decoder.releaseOutputBuffer(i, false);
    }

    public void setEndTimeUs(long j) {
        this.endTimeUs = j;
        long durationUs = getDurationUs();
        if (j < 0) {
            this.endTimeUs = 0L;
        } else if (j > durationUs) {
            this.endTimeUs = durationUs;
        }
    }

    public void setLoopingEnabled(boolean z) {
        this.loopingEnabled = z;
    }

    public void setStartTimeUs(long j) {
        this.startTimeUs = j;
        long durationUs = getDurationUs();
        if (j < 0) {
            this.startTimeUs = 0L;
        } else if (j > durationUs) {
            this.startTimeUs = durationUs;
        }
    }

    public void start() {
        long j = this.startTimeUs;
        if (j <= this.endTimeUs) {
            this.extractor.seekTo(j, 0);
            this.decoder.start();
            this.allInputExtracted = false;
            this.decodingDone = false;
            return;
        }
        throw new RuntimeException("StartTimeUs(" + this.startTimeUs + ") must be less than or equal to EndTimeUs(" + this.endTimeUs + ")");
    }

    public void stop() {
        this.decoder.stop();
        this.decodingDone = true;
    }
}
