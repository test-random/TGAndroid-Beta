package org.telegram.messenger.voip;

import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Display;
import android.view.WindowManager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.voiceengine.WebRtcAudioRecord;

public class VideoCapturerDevice {
    private static final int CAPTURE_FPS = 30;
    private static final int CAPTURE_HEIGHT;
    private static final int CAPTURE_WIDTH;
    public static EglBase eglBase;
    private static VideoCapturerDevice[] instance;
    public static Intent mediaProjectionPermissionResultData;
    private int currentHeight;
    private int currentWidth;
    private Handler handler;
    private CapturerObserver nativeCapturerObserver;
    private long nativePtr;
    private HandlerThread thread;
    private VideoCapturer videoCapturer;
    private SurfaceTextureHelper videoCapturerSurfaceTextureHelper;

    public class AnonymousClass1 extends MediaProjection.Callback {
        AnonymousClass1() {
        }

        public static void lambda$onStop$0() {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().stopScreenCapture();
            }
        }

        @Override
        public void onStop() {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VideoCapturerDevice.AnonymousClass1.lambda$onStop$0();
                }
            });
        }
    }

    public class AnonymousClass2 implements CameraVideoCapturer.CameraEventsHandler {
        AnonymousClass2() {
        }

        public static void lambda$onFirstFrameAvailable$0() {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().onCameraFirstFrameAvailable();
            }
        }

        @Override
        public void onCameraClosed() {
        }

        @Override
        public void onCameraDisconnected() {
        }

        @Override
        public void onCameraError(String str) {
        }

        @Override
        public void onCameraFreezed(String str) {
        }

        @Override
        public void onCameraOpening(String str) {
        }

        @Override
        public void onFirstFrameAvailable() {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VideoCapturerDevice.AnonymousClass2.lambda$onFirstFrameAvailable$0();
                }
            });
        }
    }

    public class AnonymousClass3 implements CameraVideoCapturer.CameraSwitchHandler {
        AnonymousClass3() {
        }

        public static void lambda$onCameraSwitchDone$0(boolean z) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().setSwitchingCamera(false, z);
            }
        }

        @Override
        public void onCameraSwitchDone(final boolean z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VideoCapturerDevice.AnonymousClass3.lambda$onCameraSwitchDone$0(z);
                }
            });
        }

        @Override
        public void onCameraSwitchError(String str) {
        }
    }

    static {
        int i = Build.VERSION.SDK_INT;
        CAPTURE_WIDTH = i <= 19 ? 480 : 1280;
        CAPTURE_HEIGHT = i <= 19 ? 320 : 720;
        instance = new VideoCapturerDevice[2];
    }

    public VideoCapturerDevice(final boolean z) {
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
        Logging.d("VideoCapturerDevice", "device model = " + Build.MANUFACTURER + Build.MODEL);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$new$0(z);
            }
        });
    }

    public static void checkScreenCapturerSize() {
        if (instance[1] == null) {
            return;
        }
        final Point screenCaptureSize = getScreenCaptureSize();
        final VideoCapturerDevice videoCapturerDevice = instance[1];
        int i = videoCapturerDevice.currentWidth;
        int i2 = screenCaptureSize.x;
        if (i == i2 && videoCapturerDevice.currentHeight == screenCaptureSize.y) {
            return;
        }
        videoCapturerDevice.currentWidth = i2;
        videoCapturerDevice.currentHeight = screenCaptureSize.y;
        videoCapturerDevice.handler.post(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.lambda$checkScreenCapturerSize$1(VideoCapturerDevice.this, screenCaptureSize);
            }
        });
    }

    public static EglBase getEglBase() {
        if (eglBase == null) {
            eglBase = EglBase.CC.create(null, EglBase.CONFIG_PLAIN);
        }
        return eglBase;
    }

    public static MediaProjection getMediaProjection() {
        VideoCapturerDevice videoCapturerDevice = instance[1];
        if (videoCapturerDevice == null) {
            return null;
        }
        return ((ScreenCapturerAndroid) videoCapturerDevice.videoCapturer).getMediaProjection();
    }

    private static Point getScreenCaptureSize() {
        int i;
        int i2;
        Display defaultDisplay = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int i3 = point.x;
        int i4 = point.y;
        float f = i3 > i4 ? i4 / i3 : i3 / i4;
        int i5 = 1;
        while (true) {
            if (i5 > 100) {
                i5 = -1;
                i = -1;
                break;
            }
            float f2 = i5 * f;
            i = (int) f2;
            if (f2 != i) {
                i5++;
            } else if (point.x <= point.y) {
                i = i5;
                i5 = i;
            }
        }
        if (i5 != -1 && f != 1.0f) {
            while (true) {
                int i6 = point.x;
                if (i6 <= 1000 && (i2 = point.y) <= 1000 && i6 % 4 == 0 && i2 % 4 == 0) {
                    break;
                }
                int i7 = i6 - i5;
                point.x = i7;
                int i8 = point.y - i;
                point.y = i8;
                if (i7 < 800 && i8 < 800) {
                    i5 = -1;
                    break;
                }
            }
        }
        if (i5 == -1 || f == 1.0f) {
            float max = Math.max(point.x / 970.0f, point.y / 970.0f);
            point.x = ((int) Math.ceil((point.x / max) / 4.0f)) * 4;
            point.y = ((int) Math.ceil((point.y / max) / 4.0f)) * 4;
        }
        return point;
    }

    private EglBase.Context getSharedEGLContext() {
        if (eglBase == null) {
            eglBase = EglBase.CC.create(null, EglBase.CONFIG_PLAIN);
        }
        EglBase eglBase2 = eglBase;
        if (eglBase2 != null) {
            return eglBase2.getEglBaseContext();
        }
        return null;
    }

    private void init(final long j, final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$init$5(j, str);
            }
        });
    }

    public static void lambda$checkScreenCapturerSize$1(VideoCapturerDevice videoCapturerDevice, Point point) {
        VideoCapturer videoCapturer = videoCapturerDevice.videoCapturer;
        if (videoCapturer != null) {
            videoCapturer.changeCaptureFormat(point.x, point.y, 30);
        }
    }

    public void lambda$init$2(Point point) {
        if (this.videoCapturerSurfaceTextureHelper != null) {
            long j = this.nativePtr;
            if (j == 0) {
                return;
            }
            this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(j);
            this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
            this.videoCapturer.startCapture(point.x, point.y, 30);
            WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.Instance;
            if (webRtcAudioRecord != null) {
                webRtcAudioRecord.initDeviceAudioRecord(((ScreenCapturerAndroid) this.videoCapturer).getMediaProjection());
            }
        }
    }

    public void lambda$init$3() {
        if (this.videoCapturerSurfaceTextureHelper == null) {
            return;
        }
        this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
        this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
        this.videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
    }

    public void lambda$init$4(String str) {
        ((CameraVideoCapturer) this.videoCapturer).switchCamera(new AnonymousClass3(), str);
    }

    public void lambda$init$5(long j, String str) {
        if (eglBase == null) {
            return;
        }
        this.nativePtr = j;
        if ("screen".equals(str)) {
            if (Build.VERSION.SDK_INT >= 21 && this.videoCapturer == null) {
                this.videoCapturer = new ScreenCapturerAndroid(mediaProjectionPermissionResultData, new AnonymousClass1());
                final Point screenCaptureSize = getScreenCaptureSize();
                this.currentWidth = screenCaptureSize.x;
                this.currentHeight = screenCaptureSize.y;
                this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("ScreenCapturerThread", eglBase.getEglBaseContext());
                this.handler.post(new Runnable() {
                    @Override
                    public final void run() {
                        VideoCapturerDevice.this.lambda$init$2(screenCaptureSize);
                    }
                });
                return;
            }
            return;
        }
        CameraEnumerator camera2Enumerator = Camera2Enumerator.isSupported(ApplicationLoader.applicationContext) ? new Camera2Enumerator(ApplicationLoader.applicationContext) : new Camera1Enumerator();
        String[] deviceNames = camera2Enumerator.getDeviceNames();
        int i = 0;
        while (true) {
            if (i >= deviceNames.length) {
                i = -1;
                break;
            } else if (camera2Enumerator.isFrontFacing(deviceNames[i]) == "front".equals(str)) {
                break;
            } else {
                i++;
            }
        }
        if (i == -1) {
            return;
        }
        final String str2 = deviceNames[i];
        if (this.videoCapturer != null) {
            this.handler.post(new Runnable() {
                @Override
                public final void run() {
                    VideoCapturerDevice.this.lambda$init$4(str2);
                }
            });
            return;
        }
        this.videoCapturer = camera2Enumerator.createCapturer(str2, new AnonymousClass2());
        this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("VideoCapturerThread", eglBase.getEglBaseContext());
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$init$3();
            }
        });
    }

    public void lambda$new$0(boolean z) {
        if (eglBase == null) {
            eglBase = EglBase.CC.create(null, EglBase.CONFIG_PLAIN);
        }
        instance[z ? 1 : 0] = this;
        HandlerThread handlerThread = new HandlerThread("CallThread");
        this.thread = handlerThread;
        handlerThread.start();
        this.handler = new Handler(this.thread.getLooper());
    }

    public void lambda$onDestroy$8() {
        WebRtcAudioRecord webRtcAudioRecord;
        if ((this.videoCapturer instanceof ScreenCapturerAndroid) && (webRtcAudioRecord = WebRtcAudioRecord.Instance) != null) {
            webRtcAudioRecord.stopDeviceAudioRecord();
        }
        VideoCapturer videoCapturer = this.videoCapturer;
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
                this.videoCapturer.dispose();
                this.videoCapturer = null;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        SurfaceTextureHelper surfaceTextureHelper = this.videoCapturerSurfaceTextureHelper;
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            this.videoCapturerSurfaceTextureHelper = null;
        }
    }

    public void lambda$onDestroy$9() {
        int i = 0;
        while (true) {
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            if (i >= videoCapturerDeviceArr.length) {
                break;
            }
            if (videoCapturerDeviceArr[i] == this) {
                videoCapturerDeviceArr[i] = null;
                break;
            }
            i++;
        }
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$onDestroy$8();
            }
        });
        try {
            this.thread.quitSafely();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$onStateChanged$6(int i) {
        VideoCapturer videoCapturer = this.videoCapturer;
        if (videoCapturer == null) {
            return;
        }
        if (i == 2) {
            videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
            return;
        }
        try {
            videoCapturer.stopCapture();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void lambda$onStateChanged$7(long j, final int i) {
        if (this.nativePtr != j) {
            return;
        }
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$onStateChanged$6(i);
            }
        });
    }

    private static native CapturerObserver nativeGetJavaVideoCapturerObserver(long j);

    private void onAspectRatioRequested(float f) {
    }

    private void onDestroy() {
        this.nativePtr = 0L;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$onDestroy$9();
            }
        });
    }

    private void onStateChanged(final long j, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoCapturerDevice.this.lambda$onStateChanged$7(j, i);
            }
        });
    }
}
