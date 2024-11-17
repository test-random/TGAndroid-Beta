package org.telegram.messenger.camera;

import android.graphics.Rect;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;

public class CameraSessionWrapper {
    public CameraSession camera1Session;
    public Camera2Session camera2Session;

    public static CameraSessionWrapper of(Camera2Session camera2Session) {
        CameraSessionWrapper cameraSessionWrapper = new CameraSessionWrapper();
        cameraSessionWrapper.camera2Session = camera2Session;
        return cameraSessionWrapper;
    }

    public static CameraSessionWrapper of(CameraSession cameraSession) {
        CameraSessionWrapper cameraSessionWrapper = new CameraSessionWrapper();
        cameraSessionWrapper.camera1Session = cameraSession;
        return cameraSessionWrapper;
    }

    public void destroy(boolean z, Runnable runnable, Runnable runnable2) {
        if (this.camera2Session != null) {
            if (runnable != null) {
                runnable.run();
            }
            this.camera2Session.destroy(z, runnable2);
        } else if (this.camera1Session != null) {
            CameraController.getInstance().close(this.camera1Session, !z ? new CountDownLatch(1) : null, runnable, runnable2);
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof CameraSession) {
            return obj == this.camera1Session;
        }
        if (obj instanceof Camera2Session) {
            return obj == this.camera2Session;
        }
        if (!(obj instanceof CameraSessionWrapper)) {
            return false;
        }
        CameraSessionWrapper cameraSessionWrapper = (CameraSessionWrapper) obj;
        if (cameraSessionWrapper != this) {
            return cameraSessionWrapper.camera1Session == this.camera1Session && cameraSessionWrapper.camera2Session == this.camera2Session;
        }
        return true;
    }

    public void focusToRect(Rect rect, Rect rect2) {
        CameraSession cameraSession;
        if (this.camera2Session == null && (cameraSession = this.camera1Session) != null) {
            cameraSession.focusToRect(rect, rect2);
        }
    }

    @Deprecated
    public int getCameraId() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            return camera2Session.cameraId.hashCode();
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.cameraInfo.cameraId;
        }
        return 0;
    }

    public String getCurrentFlashMode() {
        if (this.camera2Session != null) {
            return "off";
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.getCurrentFlashMode();
        }
        return null;
    }

    public int getCurrentOrientation() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            return camera2Session.getCurrentOrientation();
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.getCurrentOrientation();
        }
        return 0;
    }

    public int getDisplayOrientation() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            return camera2Session.getDisplayOrientation();
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.getDisplayOrientation();
        }
        return 0;
    }

    public String getNextFlashMode() {
        if (this.camera2Session != null) {
            return "off";
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.getNextFlashMode();
        }
        return null;
    }

    public Object getObject() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            return camera2Session;
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession;
        }
        return null;
    }

    public int getWorldAngle() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            return camera2Session.getWorldAngle();
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.getWorldAngle();
        }
        return 0;
    }

    public boolean hasFlashModes() {
        CameraSession cameraSession;
        if (this.camera2Session == null && (cameraSession = this.camera1Session) != null) {
            return !cameraSession.availableFlashModes.isEmpty();
        }
        return false;
    }

    public boolean isInitiated() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            return camera2Session.isInitiated();
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            return cameraSession.isInitied();
        }
        return false;
    }

    public boolean isSameTakePictureOrientation() {
        CameraSession cameraSession;
        if (this.camera2Session == null && (cameraSession = this.camera1Session) != null) {
            return cameraSession.isSameTakePictureOrientation();
        }
        return true;
    }

    public void setCurrentFlashMode(String str) {
        CameraSession cameraSession;
        if (this.camera2Session == null && (cameraSession = this.camera1Session) != null) {
            cameraSession.setCurrentFlashMode(str);
        }
    }

    public void setFlipFront(boolean z) {
        CameraSession cameraSession;
        if (this.camera2Session == null && (cameraSession = this.camera1Session) != null) {
            cameraSession.setFlipFront(z);
        }
    }

    public void setOptimizeForBarcode(boolean z) {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            camera2Session.setScanningBarcode(z);
            return;
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            cameraSession.setOptimizeForBarcode(z);
        }
    }

    public void setZoom(float f) {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            camera2Session.setZoom(AndroidUtilities.lerp(camera2Session.getMinZoom(), this.camera2Session.getMaxZoom(), f));
            return;
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            cameraSession.setZoom(f);
        }
    }

    public void stopVideoRecording() {
        Camera2Session camera2Session = this.camera2Session;
        if (camera2Session != null) {
            camera2Session.setRecordingVideo(false);
            return;
        }
        CameraSession cameraSession = this.camera1Session;
        if (cameraSession != null) {
            cameraSession.stopVideoRecording();
        }
    }

    public void updateRotation() {
        CameraSession cameraSession;
        if (this.camera2Session == null && (cameraSession = this.camera1Session) != null) {
            cameraSession.updateRotation();
        }
    }
}
