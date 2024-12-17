package org.telegram.messenger.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.TextUtils;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public class CameraSession {
    public static final int ORIENTATION_HYSTERESIS = 5;
    public CameraInfo cameraInfo;
    private String currentFlashMode;
    private int currentOrientation;
    private float currentZoom;
    private boolean destroyed;
    private int diffOrientation;
    private int displayOrientation;
    private boolean initied;
    private boolean isRound;
    private boolean isVideo;
    private int jpegOrientation;
    private int maxZoom;
    private boolean meteringAreaSupported;
    private boolean optimizeForBarcode;
    private OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;
    private boolean useTorch;
    private int lastOrientation = -1;
    private int lastDisplayOrientation = -1;
    private boolean flipFront = true;
    public ArrayList<String> availableFlashModes = new ArrayList<>();
    private int infoCameraId = -1;
    Camera.CameraInfo info = new Camera.CameraInfo();
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public final void onAutoFocus(boolean z, Camera camera) {
            CameraSession.lambda$new$0(z, camera);
        }
    };

    public CameraSession(CameraInfo cameraInfo, Size size, Size size2, int i, boolean z) {
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo;
        this.isRound = z;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        OrientationEventListener orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            @Override
            public void onOrientationChanged(int i2) {
                if (CameraSession.this.orientationEventListener == null || !CameraSession.this.initied || i2 == -1) {
                    return;
                }
                CameraSession cameraSession = CameraSession.this;
                cameraSession.jpegOrientation = cameraSession.roundOrientation(i2, cameraSession.jpegOrientation);
                int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                if (CameraSession.this.lastOrientation == CameraSession.this.jpegOrientation && rotation == CameraSession.this.lastDisplayOrientation) {
                    return;
                }
                if (!CameraSession.this.isVideo) {
                    CameraSession.this.configurePhotoCamera();
                }
                CameraSession.this.lastDisplayOrientation = rotation;
                CameraSession cameraSession2 = CameraSession.this;
                cameraSession2.lastOrientation = cameraSession2.jpegOrientation;
            }
        };
        this.orientationEventListener = orientationEventListener;
        if (orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
        } else {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }

    private int getDisplayOrientation(Camera.CameraInfo cameraInfo, boolean z) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        int i = 0;
        if (rotation != 0) {
            if (rotation == 1) {
                i = 90;
            } else if (rotation == 2) {
                i = 180;
            } else if (rotation == 3) {
                i = 270;
            }
        }
        int i2 = cameraInfo.facing;
        int i3 = cameraInfo.orientation;
        if (i2 != 1) {
            return ((i3 - i) + 360) % 360;
        }
        int i4 = (360 - ((i3 + i) % 360)) % 360;
        if (!z && i4 == 90) {
            i4 = 270;
        }
        if (!z && "Huawei".equals(Build.MANUFACTURER) && "angler".equals(Build.PRODUCT) && i4 == 270) {
            return 90;
        }
        return i4;
    }

    private int getHigh() {
        return ("LGE".equals(Build.MANUFACTURER) && "g3_tmo_us".equals(Build.PRODUCT)) ? 4 : 1;
    }

    public static void lambda$new$0(boolean z, Camera camera) {
    }

    public int roundOrientation(int i, int i2) {
        if (i2 != -1) {
            int abs = Math.abs(i - i2);
            if (Math.min(abs, 360 - abs) < 50) {
                return i2;
            }
        }
        return (((i + 45) / 90) * 90) % 360;
    }

    private void updateCameraInfo() {
        if (this.infoCameraId != this.cameraInfo.getCameraId()) {
            int cameraId = this.cameraInfo.getCameraId();
            this.infoCameraId = cameraId;
            Camera.getCameraInfo(cameraId, this.info);
        }
    }

    public void checkFlashMode(String str) {
        if (this.availableFlashModes.contains(this.currentFlashMode)) {
            return;
        }
        this.currentFlashMode = str;
        if (this.isRound) {
            configureRoundCamera(false);
        } else {
            configurePhotoCamera();
            ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", str).commit();
        }
    }

    public void configurePhotoCamera() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configurePhotoCamera():void");
    }

    public void configureRecorder(int i, MediaRecorder mediaRecorder) {
        int i2;
        CamcorderProfile camcorderProfile;
        updateCameraInfo();
        int i3 = this.jpegOrientation;
        if (i3 != -1) {
            Camera.CameraInfo cameraInfo = this.info;
            int i4 = cameraInfo.facing;
            int i5 = cameraInfo.orientation;
            i2 = (i4 == 1 ? (i5 - i3) + 360 : i5 + i3) % 360;
        } else {
            i2 = 0;
        }
        mediaRecorder.setOrientationHint(i2);
        int high = getHigh();
        boolean hasProfile = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, high);
        boolean hasProfile2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (hasProfile && (i == 1 || !hasProfile2)) {
            camcorderProfile = CamcorderProfile.get(this.cameraInfo.cameraId, high);
        } else {
            if (!hasProfile2) {
                throw new IllegalStateException("cannot find valid CamcorderProfile");
            }
            camcorderProfile = CamcorderProfile.get(this.cameraInfo.cameraId, 0);
        }
        mediaRecorder.setProfile(camcorderProfile);
        this.isVideo = true;
    }

    public boolean configureRoundCamera(boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configureRoundCamera(boolean):boolean");
    }

    public void destroy() {
        this.initied = false;
        this.destroyed = true;
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }

    public void focusToRect(Rect rect, Rect rect2) {
        Camera.Parameters parameters;
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                camera.cancelAutoFocus();
                try {
                    parameters = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                    parameters = null;
                }
                if (parameters != null) {
                    parameters.setFocusMode("auto");
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(new Camera.Area(rect, 1000));
                    parameters.setFocusAreas(arrayList);
                    if (this.meteringAreaSupported) {
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(new Camera.Area(rect2, 1000));
                        parameters.setMeteringAreas(arrayList2);
                    }
                    try {
                        camera.setParameters(parameters);
                        camera.autoFocus(this.autoFocusCallback);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public int getCurrentOrientation() {
        return this.currentOrientation;
    }

    public Camera.Size getCurrentPictureSize() {
        return this.cameraInfo.camera.getParameters().getPictureSize();
    }

    public Camera.Size getCurrentPreviewSize() {
        return this.cameraInfo.camera.getParameters().getPreviewSize();
    }

    public int getDisplayOrientation() {
        try {
            updateCameraInfo();
            return getDisplayOrientation(this.info, true);
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    protected int getMaxZoom() {
        return this.maxZoom;
    }

    public String getNextFlashMode() {
        ArrayList<String> arrayList = this.availableFlashModes;
        int i = 0;
        while (i < arrayList.size()) {
            if (arrayList.get(i).equals(this.currentFlashMode)) {
                return i < arrayList.size() + (-1) ? arrayList.get(i + 1) : arrayList.get(0);
            }
            i++;
        }
        return this.currentFlashMode;
    }

    public int getWorldAngle() {
        return this.diffOrientation;
    }

    public boolean isFlipFront() {
        return this.flipFront;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public boolean isSameTakePictureOrientation() {
        return this.sameTakePictureOrientation;
    }

    public void onStartRecord() {
        this.isVideo = true;
    }

    public void setCurrentFlashMode(String str) {
        this.currentFlashMode = str;
        if (this.isRound) {
            configureRoundCamera(false);
        } else {
            configurePhotoCamera();
            ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", str).commit();
        }
    }

    public void setFlipFront(boolean z) {
        this.flipFront = z;
    }

    public void setInitied() {
        this.initied = true;
    }

    public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
        Camera camera;
        CameraInfo cameraInfo = this.cameraInfo;
        if (cameraInfo == null || (camera = cameraInfo.camera) == null) {
            return;
        }
        try {
            camera.setOneShotPreviewCallback(previewCallback);
        } catch (Exception unused) {
        }
    }

    public void setOptimizeForBarcode(boolean z) {
        this.optimizeForBarcode = z;
        configurePhotoCamera();
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.cameraInfo.camera.setPreviewCallback(previewCallback);
    }

    public void setTorchEnabled(boolean z) {
        try {
            String str = this.currentFlashMode;
            String str2 = z ? "torch" : "off";
            this.currentFlashMode = str2;
            if (!TextUtils.equals(str, str2)) {
                if (this.isRound) {
                    configureRoundCamera(false);
                } else {
                    configurePhotoCamera();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setZoom(float f) {
        this.currentZoom = f;
        if (this.isVideo && "on".equals(this.currentFlashMode)) {
            this.useTorch = true;
        }
        if (this.isRound) {
            configureRoundCamera(false);
        } else {
            configurePhotoCamera();
        }
    }

    public void stopVideoRecording() {
        this.isVideo = false;
        this.useTorch = false;
        configurePhotoCamera();
    }

    public void updateRotation() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.updateRotation():void");
    }
}
