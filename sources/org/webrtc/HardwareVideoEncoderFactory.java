package org.webrtc;

import android.media.MediaCodecInfo;
import android.os.Build;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.VoIPService;
import org.webrtc.EglBase;
import org.webrtc.EglBase14;
import org.webrtc.VideoEncoderFactory;

public class HardwareVideoEncoderFactory implements VideoEncoderFactory {
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "HardwareVideoEncoderFactory";
    private final Predicate<MediaCodecInfo> codecAllowedPredicate;
    private final boolean enableH264HighProfile;
    private final boolean enableIntelVp8Encoder;
    private final EglBase14.Context sharedContext;

    public static class AnonymousClass1 {
        static final int[] $SwitchMap$org$webrtc$VideoCodecMimeType;

        static {
            int[] iArr = new int[VideoCodecMimeType.values().length];
            $SwitchMap$org$webrtc$VideoCodecMimeType = iArr;
            try {
                iArr[VideoCodecMimeType.VP8.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.VP9.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.H264.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$webrtc$VideoCodecMimeType[VideoCodecMimeType.H265.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public HardwareVideoEncoderFactory(EglBase.Context context, boolean z, boolean z2) {
        this(context, z, z2, null);
    }

    public HardwareVideoEncoderFactory(EglBase.Context context, boolean z, boolean z2, Predicate<MediaCodecInfo> predicate) {
        EglBase14.Context context2;
        if (context instanceof EglBase14.Context) {
            context2 = (EglBase14.Context) context;
        } else {
            Logging.w("HardwareVideoEncoderFactory", "No shared EglBase.Context.  Encoders will not use texture mode.");
            context2 = null;
        }
        this.sharedContext = context2;
        this.enableIntelVp8Encoder = z;
        this.enableH264HighProfile = z2;
        this.codecAllowedPredicate = predicate;
    }

    @Deprecated
    public HardwareVideoEncoderFactory(boolean z, boolean z2) {
        this(null, z, z2);
    }

    private BitrateAdjuster createBitrateAdjuster(VideoCodecMimeType videoCodecMimeType, String str) {
        return str.startsWith("OMX.Exynos.") ? videoCodecMimeType == VideoCodecMimeType.VP8 ? new DynamicBitrateAdjuster() : new FramerateBitrateAdjuster() : new BaseBitrateAdjuster();
    }

    private MediaCodecInfo findCodecForType(VideoCodecMimeType videoCodecMimeType) {
        ArrayList<MediaCodecInfo> sortedCodecsList = MediaCodecUtils.getSortedCodecsList();
        int size = sortedCodecsList.size();
        MediaCodecInfo mediaCodecInfo = null;
        for (int i = 0; i < size; i++) {
            MediaCodecInfo mediaCodecInfo2 = sortedCodecsList.get(i);
            if (mediaCodecInfo2 != null && mediaCodecInfo2.isEncoder()) {
                if (isSupportedCodec(mediaCodecInfo2, videoCodecMimeType, true)) {
                    return mediaCodecInfo2;
                }
                if (mediaCodecInfo == null && isSupportedCodec(mediaCodecInfo2, videoCodecMimeType, false)) {
                    mediaCodecInfo = mediaCodecInfo2;
                }
            }
        }
        if (mediaCodecInfo == null) {
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < size; i2++) {
                MediaCodecInfo mediaCodecInfo3 = sortedCodecsList.get(i2);
                if (mediaCodecInfo3 != null && mediaCodecInfo3.isEncoder() && MediaCodecUtils.codecSupportsType(mediaCodecInfo3, videoCodecMimeType)) {
                    sb.append(mediaCodecInfo3.getName());
                    sb.append(", ");
                }
            }
            FileLog.e("can't create video encoder " + videoCodecMimeType.mimeType() + ", supported codecs" + ((Object) sb));
        }
        return mediaCodecInfo;
    }

    private int getForcedKeyFrameIntervalMs(VideoCodecMimeType videoCodecMimeType, String str) {
        if (videoCodecMimeType != VideoCodecMimeType.VP8 || !str.startsWith("OMX.qcom.")) {
            return 0;
        }
        int i = Build.VERSION.SDK_INT;
        if (i != 21 && i != 22) {
            if (i == 23) {
                return 20000;
            }
            if (i <= 23) {
                return 0;
            }
        }
        return 15000;
    }

    private int getKeyFrameIntervalSec(VideoCodecMimeType videoCodecMimeType) {
        int i = AnonymousClass1.$SwitchMap$org$webrtc$VideoCodecMimeType[videoCodecMimeType.ordinal()];
        if (i == 1 || i == 2) {
            return 100;
        }
        if (i == 3 || i == 4) {
            return 20;
        }
        throw new IllegalArgumentException("Unsupported VideoCodecMimeType " + videoCodecMimeType);
    }

    private boolean isH264HighProfileSupported(MediaCodecInfo mediaCodecInfo) {
        return this.enableH264HighProfile && Build.VERSION.SDK_INT > 23 && mediaCodecInfo.getName().startsWith("OMX.Exynos.");
    }

    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType, boolean z) {
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null) {
            return false;
        }
        Instance.ServerConfig globalServerConfig = Instance.getGlobalServerConfig();
        if (!globalServerConfig.enable_h264_encoder && !globalServerConfig.enable_h265_encoder && !globalServerConfig.enable_vp8_encoder && !globalServerConfig.enable_vp9_encoder) {
            return false;
        }
        int i = AnonymousClass1.$SwitchMap$org$webrtc$VideoCodecMimeType[videoCodecMimeType.ordinal()];
        if (i == 1) {
            return isHardwareSupportedInCurrentSdkVp8(mediaCodecInfo, z);
        }
        if (i == 2) {
            return isHardwareSupportedInCurrentSdkVp9(mediaCodecInfo, z);
        }
        if (i == 3) {
            return isHardwareSupportedInCurrentSdkH264(mediaCodecInfo);
        }
        if (i != 4) {
            return false;
        }
        return isHardwareSupportedInCurrentSdkH265(mediaCodecInfo);
    }

    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo mediaCodecInfo) {
        if (!Instance.getGlobalServerConfig().enable_h264_encoder) {
            return false;
        }
        String name = mediaCodecInfo.getName();
        return name.startsWith("OMX.qcom.") || (name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 21);
    }

    private boolean isHardwareSupportedInCurrentSdkH265(MediaCodecInfo mediaCodecInfo) {
        if (!Instance.getGlobalServerConfig().enable_h265_encoder) {
            return false;
        }
        String name = mediaCodecInfo.getName();
        return name.startsWith("OMX.qcom.") || (name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 21);
    }

    private boolean isHardwareSupportedInCurrentSdkVp8(MediaCodecInfo mediaCodecInfo, boolean z) {
        if (!Instance.getGlobalServerConfig().enable_vp8_encoder) {
            return false;
        }
        String name = mediaCodecInfo.getName();
        if (name.startsWith("OMX.qcom.") || name.startsWith("OMX.hisi.") || ((name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 23) || ((name.startsWith("OMX.Intel.") && Build.VERSION.SDK_INT >= 21 && this.enableIntelVp8Encoder) || (name.startsWith("c2.exynos.") && Build.VERSION.SDK_INT >= 23)))) {
            return true;
        }
        if (!z) {
            int i = 0;
            while (true) {
                String[] strArr = MediaCodecUtils.SOFTWARE_IMPLEMENTATION_PREFIXES;
                if (i >= strArr.length) {
                    break;
                }
                if (name.startsWith(strArr[i])) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    private boolean isHardwareSupportedInCurrentSdkVp9(MediaCodecInfo mediaCodecInfo, boolean z) {
        if (!Instance.getGlobalServerConfig().enable_vp9_encoder) {
            return false;
        }
        String name = mediaCodecInfo.getName();
        if ((name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.") || name.startsWith("OMX.hisi.")) && Build.VERSION.SDK_INT >= 24) {
            return true;
        }
        if (!z) {
            int i = 0;
            while (true) {
                String[] strArr = MediaCodecUtils.SOFTWARE_IMPLEMENTATION_PREFIXES;
                if (i >= strArr.length) {
                    break;
                }
                if (name.startsWith(strArr[i])) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    private boolean isMediaCodecAllowed(MediaCodecInfo mediaCodecInfo) {
        Predicate<MediaCodecInfo> predicate = this.codecAllowedPredicate;
        if (predicate == null) {
            return true;
        }
        return predicate.test(mediaCodecInfo);
    }

    private boolean isSupportedCodec(MediaCodecInfo mediaCodecInfo, VideoCodecMimeType videoCodecMimeType, boolean z) {
        return MediaCodecUtils.codecSupportsType(mediaCodecInfo, videoCodecMimeType) && MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, mediaCodecInfo.getCapabilitiesForType(videoCodecMimeType.mimeType())) != null && isHardwareSupportedInCurrentSdk(mediaCodecInfo, videoCodecMimeType, z) && isMediaCodecAllowed(mediaCodecInfo);
    }

    @Override
    public VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo) {
        VideoCodecMimeType valueOf = VideoCodecMimeType.valueOf(videoCodecInfo.name);
        MediaCodecInfo findCodecForType = findCodecForType(valueOf);
        if (findCodecForType == null) {
            return null;
        }
        String name = findCodecForType.getName();
        String mimeType = valueOf.mimeType();
        Integer selectColorFormat = MediaCodecUtils.selectColorFormat(MediaCodecUtils.TEXTURE_COLOR_FORMATS, findCodecForType.getCapabilitiesForType(mimeType));
        Integer selectColorFormat2 = MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, findCodecForType.getCapabilitiesForType(mimeType));
        if (valueOf == VideoCodecMimeType.H264) {
            boolean isSameH264Profile = H264Utils.isSameH264Profile(videoCodecInfo.params, MediaCodecUtils.getCodecProperties(valueOf, true));
            boolean isSameH264Profile2 = H264Utils.isSameH264Profile(videoCodecInfo.params, MediaCodecUtils.getCodecProperties(valueOf, false));
            if (!isSameH264Profile && !isSameH264Profile2) {
                return null;
            }
            if (isSameH264Profile && !isH264HighProfileSupported(findCodecForType)) {
                return null;
            }
        }
        return new HardwareVideoEncoder(new MediaCodecWrapperFactoryImpl(), name, valueOf, selectColorFormat, selectColorFormat2, videoCodecInfo.params, getKeyFrameIntervalSec(valueOf), getForcedKeyFrameIntervalMs(valueOf, name), createBitrateAdjuster(valueOf, name), this.sharedContext);
    }

    @Override
    public VideoEncoderFactory.VideoEncoderSelector getEncoderSelector() {
        return VideoEncoderFactory.CC.$default$getEncoderSelector(this);
    }

    @Override
    public VideoCodecInfo[] getImplementations() {
        VideoCodecInfo[] supportedCodecs;
        supportedCodecs = getSupportedCodecs();
        return supportedCodecs;
    }

    @Override
    public VideoCodecInfo[] getSupportedCodecs() {
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null) {
            return new VideoCodecInfo[0];
        }
        ArrayList arrayList = new ArrayList();
        VideoCodecMimeType[] videoCodecMimeTypeArr = {VideoCodecMimeType.VP8, VideoCodecMimeType.VP9, VideoCodecMimeType.H264, VideoCodecMimeType.H265};
        for (int i = 0; i < 4; i++) {
            VideoCodecMimeType videoCodecMimeType = videoCodecMimeTypeArr[i];
            MediaCodecInfo findCodecForType = findCodecForType(videoCodecMimeType);
            if (findCodecForType != null) {
                String name = videoCodecMimeType.name();
                if (videoCodecMimeType == VideoCodecMimeType.H264 && isH264HighProfileSupported(findCodecForType)) {
                    arrayList.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(videoCodecMimeType, true)));
                }
                arrayList.add(new VideoCodecInfo(name, MediaCodecUtils.getCodecProperties(videoCodecMimeType, false)));
            }
        }
        return (VideoCodecInfo[]) arrayList.toArray(new VideoCodecInfo[arrayList.size()]);
    }
}
