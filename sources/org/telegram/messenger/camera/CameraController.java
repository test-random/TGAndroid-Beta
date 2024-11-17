package org.telegram.messenger.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;

public class CameraController implements MediaRecorder.OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = 1;
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private ArrayList<ErrorCallback> errorCallbacks;
    private boolean loadingCameras;
    private boolean mirrorRecorderVideo;
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    ICameraView recordingCurrentCameraView;
    private ArrayList<Runnable> onFinishCameraInitRunnables = new ArrayList<>();
    protected ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    public static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        @Override
        public int compare(Size size, Size size2) {
            return Long.signum((size.getWidth() * size.getHeight()) - (size2.getWidth() * size2.getHeight()));
        }
    }

    public interface ErrorCallback {

        public abstract class CC {
            public static void $default$onError(ErrorCallback errorCallback, int i, Camera camera, CameraSessionWrapper cameraSessionWrapper) {
            }
        }

        void onError(int i, Camera camera, CameraSessionWrapper cameraSessionWrapper);
    }

    public interface ICameraView {
        boolean startRecording(File file, Runnable runnable);

        void stopRecording();
    }

    public interface VideoTakeCallback {
        void onFinishVideoRecording(String str, long j);
    }

    public static Size chooseOptimalSize(List<Size> list, int i, int i2, Size size, boolean z) {
        ArrayList arrayList = new ArrayList(list.size());
        ArrayList arrayList2 = new ArrayList(list.size());
        int width = size.getWidth();
        int height = size.getHeight();
        for (int i3 = 0; i3 < list.size(); i3++) {
            Size size2 = list.get(i3);
            if (!z || (size2.getHeight() <= i2 && size2.getWidth() <= i)) {
                if (size2.getHeight() == (size2.getWidth() * height) / width && size2.getWidth() >= i && size2.getHeight() >= i2) {
                    arrayList.add(size2);
                } else if (size2.getHeight() * size2.getWidth() <= i * i2 * 4) {
                    arrayList2.add(size2);
                }
            }
        }
        return (Size) (arrayList.size() > 0 ? Collections.min(arrayList, new CompareSizesByArea()) : arrayList2.size() > 0 ? Collections.min(arrayList2, new CompareSizesByArea()) : Collections.max(list, new CompareSizesByArea()));
    }

    public void lambda$recordVideo$11(boolean r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.lambda$recordVideo$11(boolean):void");
    }

    public static CameraController getInstance() {
        CameraController cameraController = Instance;
        if (cameraController == null) {
            synchronized (CameraController.class) {
                try {
                    cameraController = Instance;
                    if (cameraController == null) {
                        cameraController = new CameraController();
                        Instance = cameraController;
                    }
                } finally {
                }
            }
        }
        return cameraController;
    }

    private static int getOrientation(byte[] bArr) {
        int i;
        int pack;
        if (bArr == null) {
            return -1;
        }
        int i2 = 0;
        while (i2 + 3 < bArr.length) {
            int i3 = i2 + 1;
            if ((bArr[i2] & 255) == 255) {
                int i4 = bArr[i3] & 255;
                if (i4 != 255) {
                    i3 = i2 + 2;
                    if (i4 != 216 && i4 != 1) {
                        if (i4 != 217 && i4 != 218) {
                            int pack2 = pack(bArr, i3, 2, false);
                            if (pack2 >= 2 && (i3 = i3 + pack2) <= bArr.length) {
                                if (i4 == 225 && pack2 >= 8 && pack(bArr, i2 + 4, 4, false) == 1165519206 && pack(bArr, i2 + 8, 2, false) == 0) {
                                    i2 += 10;
                                    i = pack2 - 8;
                                    break;
                                }
                            } else {
                                return -1;
                            }
                        }
                    }
                }
                i2 = i3;
            }
            i2 = i3;
        }
        i = 0;
        if (i <= 8 || !((pack = pack(bArr, i2, 4, false)) == 1229531648 || pack == 1296891946)) {
            return -1;
        }
        boolean z = pack == 1229531648;
        int pack3 = pack(bArr, i2 + 4, 4, z) + 2;
        if (pack3 >= 10 && pack3 <= i) {
            int i5 = i2 + pack3;
            int i6 = i - pack3;
            int pack4 = pack(bArr, i5 - 2, 2, z);
            while (true) {
                int i7 = pack4 - 1;
                if (pack4 <= 0 || i6 < 12) {
                    break;
                }
                if (pack(bArr, i5, 2, z) == 274) {
                    int pack5 = pack(bArr, i5 + 8, 2, z);
                    if (pack5 == 1) {
                        return 0;
                    }
                    if (pack5 == 3) {
                        return 180;
                    }
                    if (pack5 != 6) {
                        return pack5 != 8 ? -1 : 270;
                    }
                    return 90;
                }
                i5 += 12;
                i6 -= 12;
                pack4 = i7;
            }
        }
        return -1;
    }

    private void initCamera(final Runnable runnable, final boolean z) {
        if (this.cameraInitied) {
            return;
        }
        if (runnable != null && !this.onFinishCameraInitRunnables.contains(runnable)) {
            this.onFinishCameraInitRunnables.add(runnable);
        }
        if (this.loadingCameras || this.cameraInitied) {
            return;
        }
        this.loadingCameras = true;
        this.threadPool.execute(new Runnable() {
            @Override
            public final void run() {
                CameraController.this.lambda$initCamera$4(z, runnable);
            }
        });
    }

    public static void lambda$close$5(Runnable runnable, CameraSession cameraSession, CountDownLatch countDownLatch, Runnable runnable2) {
        if (runnable != null) {
            runnable.run();
        }
        Camera camera = cameraSession.cameraInfo.camera;
        if (camera != null) {
            try {
                camera.stopPreview();
                cameraSession.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                cameraSession.cameraInfo.camera.release();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            cameraSession.cameraInfo.camera = null;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        if (runnable2 != null) {
            AndroidUtilities.runOnUIThread(runnable2);
        }
    }

    public void lambda$finishRecordingVideo$15(File file, Bitmap bitmap, long j) {
        String str;
        if (this.onVideoTakeCallback != null) {
            if (file != null) {
                str = file.getAbsolutePath();
                if (bitmap != null) {
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), Utilities.MD5(str), false);
                }
            } else {
                str = null;
            }
            this.onVideoTakeCallback.onFinishVideoRecording(str, j);
            this.onVideoTakeCallback = null;
        }
    }

    public void lambda$getErrorListener$18(CameraSession cameraSession, int i, Camera camera) {
        if (this.errorCallbacks != null) {
            for (int i2 = 0; i2 < this.errorCallbacks.size(); i2++) {
                ErrorCallback errorCallback = this.errorCallbacks.get(i2);
                if (errorCallback != null) {
                    errorCallback.onError(i, camera, CameraSessionWrapper.of(cameraSession));
                }
            }
        }
    }

    public static int lambda$initCamera$0(Size size, Size size2) {
        int i = size.mWidth;
        int i2 = size2.mWidth;
        if (i < i2) {
            return 1;
        }
        if (i > i2) {
            return -1;
        }
        int i3 = size.mHeight;
        int i4 = size2.mHeight;
        if (i3 < i4) {
            return 1;
        }
        return i3 > i4 ? -1 : 0;
    }

    public void lambda$initCamera$1() {
        this.loadingCameras = false;
        this.cameraInitied = true;
        if (!this.onFinishCameraInitRunnables.isEmpty()) {
            for (int i = 0; i < this.onFinishCameraInitRunnables.size(); i++) {
                this.onFinishCameraInitRunnables.get(i).run();
            }
            this.onFinishCameraInitRunnables.clear();
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.cameraInitied, new Object[0]);
    }

    public void lambda$initCamera$2(Runnable runnable) {
        initCamera(runnable, true);
    }

    public void lambda$initCamera$3(boolean z, Exception exc, final Runnable runnable) {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
        if (z || !"APP_PAUSED".equals(exc.getMessage()) || runnable == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                CameraController.this.lambda$initCamera$2(runnable);
            }
        }, 1000L);
    }

    public void lambda$initCamera$4(final boolean z, final Runnable runnable) {
        String str;
        Camera.CameraInfo cameraInfo;
        String str2;
        final CameraController cameraController = this;
        String str3 = "APP_PAUSED";
        String str4 = "cameraCache";
        try {
            if (cameraController.cameraInfos == null) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                String string = globalMainSettings.getString("cameraCache", null);
                Comparator comparator = new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$initCamera$0;
                        lambda$initCamera$0 = CameraController.lambda$initCamera$0((Size) obj, (Size) obj2);
                        return lambda$initCamera$0;
                    }
                };
                ArrayList<CameraInfo> arrayList = new ArrayList<>();
                if (string != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(string, 0));
                    int readInt32 = serializedData.readInt32(false);
                    for (int i = 0; i < readInt32; i++) {
                        CameraInfo cameraInfo2 = new CameraInfo(serializedData.readInt32(false), serializedData.readInt32(false));
                        int readInt322 = serializedData.readInt32(false);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            cameraInfo2.previewSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        int readInt323 = serializedData.readInt32(false);
                        for (int i3 = 0; i3 < readInt323; i3++) {
                            cameraInfo2.pictureSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        arrayList.add(cameraInfo2);
                        Collections.sort(cameraInfo2.previewSizes, comparator);
                        Collections.sort(cameraInfo2.pictureSizes, comparator);
                    }
                    serializedData.cleanup();
                    str = "APP_PAUSED";
                } else {
                    int numberOfCameras = Camera.getNumberOfCameras();
                    Camera.CameraInfo cameraInfo3 = new Camera.CameraInfo();
                    int i4 = 4;
                    int i5 = 0;
                    while (i5 < numberOfCameras) {
                        try {
                            Camera.getCameraInfo(i5, cameraInfo3);
                            CameraInfo cameraInfo4 = new CameraInfo(i5, cameraInfo3.facing);
                            if (ApplicationLoader.mainInterfacePaused && ApplicationLoader.externalInterfacePaused) {
                                throw new RuntimeException(str3);
                            }
                            Camera open = Camera.open(cameraInfo4.getCameraId());
                            Camera.Parameters parameters = open.getParameters();
                            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                            int i6 = 0;
                            while (true) {
                                cameraInfo = cameraInfo3;
                                if (i6 >= supportedPreviewSizes.size()) {
                                    break;
                                }
                                Camera.Size size = supportedPreviewSizes.get(i6);
                                List<Camera.Size> list = supportedPreviewSizes;
                                int i7 = size.height;
                                str = str3;
                                if (i7 < 2160) {
                                    try {
                                        int i8 = size.width;
                                        if (i8 < 2160) {
                                            str2 = str4;
                                            cameraInfo4.previewSizes.add(new Size(i8, i7));
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.d("preview size = " + size.width + " " + size.height);
                                            }
                                            i6++;
                                            cameraInfo3 = cameraInfo;
                                            supportedPreviewSizes = list;
                                            str3 = str;
                                            str4 = str2;
                                        }
                                    } catch (Exception e) {
                                        e = e;
                                        cameraController = this;
                                        FileLog.e(e, !str.equals(e.getMessage()));
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                CameraController.this.lambda$initCamera$3(z, e, runnable);
                                            }
                                        });
                                        return;
                                    }
                                }
                                str2 = str4;
                                i6++;
                                cameraInfo3 = cameraInfo;
                                supportedPreviewSizes = list;
                                str3 = str;
                                str4 = str2;
                            }
                            String str5 = str4;
                            String str6 = str3;
                            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
                            for (int i9 = 0; i9 < supportedPictureSizes.size(); i9++) {
                                Camera.Size size2 = supportedPictureSizes.get(i9);
                                if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size2.width < 2048) {
                                    cameraInfo4.pictureSizes.add(new Size(size2.width, size2.height));
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("picture size = " + size2.width + " " + size2.height);
                                    }
                                }
                            }
                            open.release();
                            arrayList.add(cameraInfo4);
                            Collections.sort(cameraInfo4.previewSizes, comparator);
                            Collections.sort(cameraInfo4.pictureSizes, comparator);
                            i4 += ((cameraInfo4.previewSizes.size() + cameraInfo4.pictureSizes.size()) * 8) + 8;
                            i5++;
                            cameraInfo3 = cameraInfo;
                            str3 = str6;
                            str4 = str5;
                        } catch (Exception e2) {
                            e = e2;
                            str = str3;
                            cameraController = this;
                            FileLog.e(e, !str.equals(e.getMessage()));
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                @Override
                                public final void run() {
                                    CameraController.this.lambda$initCamera$3(z, e, runnable);
                                }
                            });
                            return;
                        }
                    }
                    String str7 = str4;
                    str = str3;
                    SerializedData serializedData2 = new SerializedData(i4);
                    serializedData2.writeInt32(arrayList.size());
                    for (int i10 = 0; i10 < numberOfCameras; i10++) {
                        CameraInfo cameraInfo5 = arrayList.get(i10);
                        serializedData2.writeInt32(cameraInfo5.cameraId);
                        serializedData2.writeInt32(cameraInfo5.frontCamera);
                        int size3 = cameraInfo5.previewSizes.size();
                        serializedData2.writeInt32(size3);
                        for (int i11 = 0; i11 < size3; i11++) {
                            Size size4 = cameraInfo5.previewSizes.get(i11);
                            serializedData2.writeInt32(size4.mWidth);
                            serializedData2.writeInt32(size4.mHeight);
                        }
                        int size5 = cameraInfo5.pictureSizes.size();
                        serializedData2.writeInt32(size5);
                        for (int i12 = 0; i12 < size5; i12++) {
                            Size size6 = cameraInfo5.pictureSizes.get(i12);
                            serializedData2.writeInt32(size6.mWidth);
                            serializedData2.writeInt32(size6.mHeight);
                        }
                    }
                    globalMainSettings.edit().putString(str7, Base64.encodeToString(serializedData2.toByteArray(), 0)).commit();
                    serializedData2.cleanup();
                    cameraController = this;
                }
                try {
                    cameraController.cameraInfos = arrayList;
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e, !str.equals(e.getMessage()));
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            CameraController.this.lambda$initCamera$3(z, e, runnable);
                        }
                    });
                    return;
                }
            } else {
                str = "APP_PAUSED";
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    CameraController.this.lambda$initCamera$1();
                }
            });
        } catch (Exception e4) {
            e = e4;
            str = "APP_PAUSED";
        }
    }

    public void lambda$open$10(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        String str;
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        Camera camera = cameraInfo.camera;
        if (camera == null) {
            try {
                Camera open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                cameraSession.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e(e);
                return;
            }
        }
        camera.setErrorCallback(getErrorListener(cameraSession));
        Camera.Parameters parameters = camera.getParameters();
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        cameraSession.availableFlashModes.clear();
        if (supportedFlashModes != null) {
            for (int i = 0; i < supportedFlashModes.size(); i++) {
                String str2 = supportedFlashModes.get(i);
                if (str2.equals("off") || str2.equals("on") || str2.equals("auto")) {
                    cameraSession.availableFlashModes.add(str2);
                }
            }
            if (TextUtils.equals(cameraSession.getCurrentFlashMode(), parameters.getFlashMode()) && cameraSession.availableFlashModes.contains(cameraSession.getCurrentFlashMode())) {
                str = cameraSession.getCurrentFlashMode();
                cameraSession.checkFlashMode(str);
            }
            str = cameraSession.availableFlashModes.get(0);
            cameraSession.checkFlashMode(str);
        }
        if (runnable != null) {
            runnable.run();
        }
        cameraSession.configurePhotoCamera();
        camera.setPreviewTexture(surfaceTexture);
        camera.startPreview();
        if (runnable2 != null) {
            AndroidUtilities.runOnUIThread(runnable2);
        }
    }

    public static void lambda$openRound$9(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        String str;
        Camera camera = cameraSession.cameraInfo.camera;
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start creating round camera session");
            }
            if (camera == null) {
                CameraInfo cameraInfo = cameraSession.cameraInfo;
                Camera open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            }
            Camera.Parameters parameters = camera.getParameters();
            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            cameraSession.availableFlashModes.clear();
            if (supportedFlashModes != null) {
                for (int i = 0; i < supportedFlashModes.size(); i++) {
                    String str2 = supportedFlashModes.get(i);
                    if (str2.equals("off") || str2.equals("on") || str2.equals("auto")) {
                        cameraSession.availableFlashModes.add(str2);
                    }
                }
                if (TextUtils.equals(cameraSession.getCurrentFlashMode(), parameters.getFlashMode()) && cameraSession.availableFlashModes.contains(cameraSession.getCurrentFlashMode())) {
                    str = cameraSession.getCurrentFlashMode();
                    cameraSession.checkFlashMode(str);
                }
                str = cameraSession.availableFlashModes.get(0);
                cameraSession.checkFlashMode(str);
            }
            cameraSession.configureRoundCamera(true);
            if (runnable != null) {
                runnable.run();
            }
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            if (runnable2 != null) {
                AndroidUtilities.runOnUIThread(runnable2);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("round camera session created");
            }
        } catch (Exception e) {
            cameraSession.cameraInfo.camera = null;
            if (camera != null) {
                camera.release();
            }
            FileLog.e(e);
        }
    }

    public void lambda$recordVideo$12(ICameraView iCameraView, File file, final boolean z, Runnable runnable) {
        iCameraView.startRecording(file, new Runnable() {
            @Override
            public final void run() {
                CameraController.this.lambda$recordVideo$11(z);
            }
        });
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$recordVideo$13(Object obj, final ICameraView iCameraView, final File file, final boolean z, final Runnable runnable) {
        try {
            if (obj instanceof CameraSession) {
                CameraSession cameraSession = (CameraSession) obj;
                Camera camera = cameraSession.cameraInfo.camera;
                if (camera != null) {
                    try {
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFlashMode(cameraSession.getCurrentFlashMode().equals("on") ? "torch" : "off");
                        camera.setParameters(parameters);
                        cameraSession.onStartRecord();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            } else if (obj instanceof Camera2Session) {
                ((Camera2Session) obj).setRecordingVideo(true);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    CameraController.this.lambda$recordVideo$12(iCameraView, file, z, runnable);
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void lambda$recordVideo$14(Camera camera, CameraSession cameraSession, boolean z, File file, CameraInfo cameraInfo, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        if (camera != null) {
            try {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(cameraSession.getCurrentFlashMode().equals("on") ? "torch" : "off");
                    camera.setParameters(parameters);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                camera.unlock();
                try {
                    this.mirrorRecorderVideo = z;
                    MediaRecorder mediaRecorder = new MediaRecorder();
                    this.recorder = mediaRecorder;
                    mediaRecorder.setCamera(camera);
                    this.recorder.setVideoSource(1);
                    this.recorder.setAudioSource(5);
                    cameraSession.configureRecorder(1, this.recorder);
                    this.recorder.setOutputFile(file.getAbsolutePath());
                    this.recorder.setMaxFileSize(1073741824L);
                    this.recorder.setVideoFrameRate(30);
                    this.recorder.setMaxDuration(0);
                    Size chooseOptimalSize = chooseOptimalSize(cameraInfo.getPictureSizes(), 720, 480, new Size(16, 9), false);
                    this.recorder.setVideoEncodingBitRate(Math.min(chooseOptimalSize.mHeight, chooseOptimalSize.mWidth) >= 720 ? 3500000 : 1800000);
                    this.recorder.setVideoSize(chooseOptimalSize.getWidth(), chooseOptimalSize.getHeight());
                    this.recorder.setOnInfoListener(this);
                    this.recorder.prepare();
                    this.recorder.start();
                    this.onVideoTakeCallback = videoTakeCallback;
                    this.recordedFile = file.getAbsolutePath();
                    if (runnable != null) {
                        AndroidUtilities.runOnUIThread(runnable);
                    }
                } catch (Exception e2) {
                    this.recorder.release();
                    this.recorder = null;
                    FileLog.e(e2);
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
    }

    public void lambda$startPreview$7(CameraSession cameraSession) {
        Camera open;
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        Camera camera = cameraInfo.camera;
        if (camera == null) {
            try {
                open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
            } catch (Exception e) {
                e = e;
                open = camera;
            }
            try {
                open.setErrorCallback(getErrorListener(cameraSession));
                camera = open;
            } catch (Exception e2) {
                e = e2;
                cameraSession.cameraInfo.camera = null;
                if (open != null) {
                    open.release();
                }
                FileLog.e(e);
                return;
            }
        }
        camera.startPreview();
    }

    public void lambda$stopPreview$8(CameraSession cameraSession) {
        Camera open;
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        Camera camera = cameraInfo.camera;
        if (camera == null) {
            try {
                open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
            } catch (Exception e) {
                e = e;
                open = camera;
            }
            try {
                open.setErrorCallback(getErrorListener(cameraSession));
                camera = open;
            } catch (Exception e2) {
                e = e2;
                cameraSession.cameraInfo.camera = null;
                if (open != null) {
                    open.release();
                }
                FileLog.e(e);
                return;
            }
        }
        camera.stopPreview();
    }

    public static void lambda$stopVideoRecording$16(Camera camera, CameraSession cameraSession) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(cameraSession.getCurrentFlashMode());
            camera.setParameters(parameters);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$stopVideoRecording$17(Object obj, boolean z, boolean z2) {
        try {
            MediaRecorder mediaRecorder = this.recorder;
            if (mediaRecorder != null) {
                this.recorder = null;
                try {
                    mediaRecorder.stop();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    mediaRecorder.release();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            if (obj instanceof CameraSession) {
                final CameraSession cameraSession = (CameraSession) obj;
                final Camera camera = cameraSession.cameraInfo.camera;
                if (camera != null) {
                    try {
                        camera.reconnect();
                        camera.startPreview();
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                    try {
                        cameraSession.stopVideoRecording();
                    } catch (Exception e4) {
                        FileLog.e(e4);
                    }
                }
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode("off");
                    camera.setParameters(parameters);
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
                this.threadPool.execute(new Runnable() {
                    @Override
                    public final void run() {
                        CameraController.lambda$stopVideoRecording$16(camera, cameraSession);
                    }
                });
            } else if (obj instanceof Camera2Session) {
                ((Camera2Session) obj).setRecordingVideo(false);
            }
            if (z || this.onVideoTakeCallback == null) {
                this.onVideoTakeCallback = null;
            } else {
                lambda$recordVideo$11(z2);
            }
        } catch (Exception unused) {
        }
    }

    public static void lambda$takePicture$6(java.io.File r16, org.telegram.messenger.camera.CameraInfo r17, boolean r18, boolean r19, org.telegram.messenger.Utilities.Callback r20, byte[] r21, android.hardware.Camera r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.lambda$takePicture$6(java.io.File, org.telegram.messenger.camera.CameraInfo, boolean, boolean, org.telegram.messenger.Utilities$Callback, byte[], android.hardware.Camera):void");
    }

    private static int pack(byte[] bArr, int i, int i2, boolean z) {
        int i3;
        if (z) {
            i += i2 - 1;
            i3 = -1;
        } else {
            i3 = 1;
        }
        int i4 = 0;
        while (true) {
            int i5 = i2 - 1;
            if (i2 <= 0) {
                return i4;
            }
            i4 = (bArr[i] & 255) | (i4 << 8);
            i += i3;
            i2 = i5;
        }
    }

    public void addOnErrorListener(ErrorCallback errorCallback) {
        if (this.errorCallbacks == null) {
            this.errorCallbacks = new ArrayList<>();
        }
        this.errorCallbacks.remove(errorCallback);
        this.errorCallbacks.add(errorCallback);
    }

    public void cancelOnInitRunnable(Runnable runnable) {
        this.onFinishCameraInitRunnables.remove(runnable);
    }

    public void close(CameraSession cameraSession, CountDownLatch countDownLatch, Runnable runnable) {
        close(cameraSession, countDownLatch, runnable, null);
    }

    public void close(final CameraSession cameraSession, final CountDownLatch countDownLatch, final Runnable runnable, final Runnable runnable2) {
        cameraSession.destroy();
        this.threadPool.execute(new Runnable() {
            @Override
            public final void run() {
                CameraController.lambda$close$5(runnable, cameraSession, countDownLatch, runnable2);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    public Camera.ErrorCallback getErrorListener(final CameraSession cameraSession) {
        return new Camera.ErrorCallback() {
            @Override
            public final void onError(int i, Camera camera) {
                CameraController.this.lambda$getErrorListener$18(cameraSession, i, camera);
            }
        };
    }

    public void initCamera(Runnable runnable) {
        initCamera(runnable, false);
    }

    public boolean isCameraInitied() {
        return (!this.cameraInitied || this.cameraInfos == null || this.cameraInfos.isEmpty()) ? false : true;
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        if (i == 800 || i == 801 || i == 1) {
            MediaRecorder mediaRecorder2 = this.recorder;
            this.recorder = null;
            if (mediaRecorder2 != null) {
                mediaRecorder2.stop();
                mediaRecorder2.release();
            }
            if (this.onVideoTakeCallback != null) {
                lambda$recordVideo$11(true);
            }
        }
    }

    public void open(final CameraSession cameraSession, final SurfaceTexture surfaceTexture, final Runnable runnable, final Runnable runnable2) {
        if (cameraSession == null || surfaceTexture == null) {
            return;
        }
        this.threadPool.execute(new Runnable() {
            @Override
            public final void run() {
                CameraController.this.lambda$open$10(cameraSession, runnable2, surfaceTexture, runnable);
            }
        });
    }

    public void openRound(final CameraSession cameraSession, final SurfaceTexture surfaceTexture, final Runnable runnable, final Runnable runnable2) {
        if (cameraSession != null && surfaceTexture != null) {
            this.threadPool.execute(new Runnable() {
                @Override
                public final void run() {
                    CameraController.lambda$openRound$9(CameraSession.this, runnable2, surfaceTexture, runnable);
                }
            });
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("failed to open round " + cameraSession + " tex = " + surfaceTexture);
        }
    }

    public void recordVideo(Object obj, File file, boolean z, VideoTakeCallback videoTakeCallback, Runnable runnable, ICameraView iCameraView) {
        recordVideo(obj, file, z, videoTakeCallback, runnable, iCameraView, true);
    }

    public void recordVideo(final Object obj, final File file, final boolean z, final VideoTakeCallback videoTakeCallback, final Runnable runnable, final ICameraView iCameraView, final boolean z2) {
        if (obj == null) {
            return;
        }
        if (iCameraView != null) {
            this.recordingCurrentCameraView = iCameraView;
            this.onVideoTakeCallback = videoTakeCallback;
            this.recordedFile = file.getAbsolutePath();
            this.threadPool.execute(new Runnable() {
                @Override
                public final void run() {
                    CameraController.this.lambda$recordVideo$13(obj, iCameraView, file, z2, runnable);
                }
            });
            return;
        }
        if (obj instanceof CameraSession) {
            final CameraSession cameraSession = (CameraSession) obj;
            final CameraInfo cameraInfo = cameraSession.cameraInfo;
            final Camera camera = cameraInfo.camera;
            this.threadPool.execute(new Runnable() {
                @Override
                public final void run() {
                    CameraController.this.lambda$recordVideo$14(camera, cameraSession, z, file, cameraInfo, videoTakeCallback, runnable);
                }
            });
        }
    }

    public void removeOnErrorListener(ErrorCallback errorCallback) {
        ArrayList<ErrorCallback> arrayList = this.errorCallbacks;
        if (arrayList != null) {
            arrayList.remove(errorCallback);
        }
    }

    public void startPreview(Object obj) {
        if (obj == null || !(obj instanceof CameraSession)) {
            return;
        }
        final CameraSession cameraSession = (CameraSession) obj;
        this.threadPool.execute(new Runnable() {
            @Override
            public final void run() {
                CameraController.this.lambda$startPreview$7(cameraSession);
            }
        });
    }

    public void stopPreview(Object obj) {
        if (obj == null || !(obj instanceof CameraSession)) {
            return;
        }
        final CameraSession cameraSession = (CameraSession) obj;
        this.threadPool.execute(new Runnable() {
            @Override
            public final void run() {
                CameraController.this.lambda$stopPreview$8(cameraSession);
            }
        });
    }

    public void stopVideoRecording(Object obj, boolean z) {
        stopVideoRecording(obj, z, true);
    }

    public void stopVideoRecording(final Object obj, final boolean z, final boolean z2) {
        ICameraView iCameraView = this.recordingCurrentCameraView;
        if (iCameraView == null) {
            this.threadPool.execute(new Runnable() {
                @Override
                public final void run() {
                    CameraController.this.lambda$stopVideoRecording$17(obj, z, z2);
                }
            });
        } else {
            iCameraView.stopRecording();
            this.recordingCurrentCameraView = null;
        }
    }

    public boolean takePicture(final File file, final boolean z, Object obj, final Utilities.Callback<Integer> callback) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CameraSession)) {
            if (obj instanceof Camera2Session) {
                return ((Camera2Session) obj).takePicture(file, callback);
            }
            return false;
        }
        CameraSession cameraSession = (CameraSession) obj;
        final CameraInfo cameraInfo = cameraSession.cameraInfo;
        final boolean isFlipFront = cameraSession.isFlipFront();
        try {
            cameraInfo.camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public final void onPictureTaken(byte[] bArr, Camera camera) {
                    CameraController.lambda$takePicture$6(file, cameraInfo, isFlipFront, z, callback, bArr, camera);
                }
            });
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }
}
