package org.webrtc;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.os.Build;
import android.view.Surface;
import org.webrtc.EglBase;
import org.webrtc.EglBase14;

class EglBase14Impl implements EglBase14 {
    private static final int CURRENT_SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int EGLExt_SDK_VERSION = 18;
    private static final String TAG = "EglBase14Impl";
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private EGLSurface eglSurfaceBackground;

    public static class Context implements EglBase14.Context {
        private final EGLContext egl14Context;

        public Context(EGLContext eGLContext) {
            this.egl14Context = eGLContext;
        }

        @Override
        public long getNativeEglContext() {
            long nativeHandle;
            if (EglBase14Impl.CURRENT_SDK_VERSION < 21) {
                return this.egl14Context.getHandle();
            }
            nativeHandle = this.egl14Context.getNativeHandle();
            return nativeHandle;
        }

        @Override
        public EGLContext getRawContext() {
            return this.egl14Context;
        }
    }

    public EglBase14Impl(EGLContext eGLContext, int[] iArr) {
        EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
        this.eglSurface = eGLSurface;
        this.eglSurfaceBackground = eGLSurface;
        EGLDisplay eglDisplay = getEglDisplay();
        this.eglDisplay = eglDisplay;
        this.eglConfig = getEglConfig(eglDisplay, iArr);
        int openGlesVersionFromConfig = EglBase.CC.getOpenGlesVersionFromConfig(iArr);
        Logging.d("EglBase14Impl", "Using OpenGL ES version " + openGlesVersionFromConfig);
        this.eglContext = createEglContext(eGLContext, this.eglDisplay, this.eglConfig, openGlesVersionFromConfig);
    }

    private void checkIsNotReleased() {
        if (this.eglDisplay == EGL14.EGL_NO_DISPLAY || this.eglContext == EGL14.EGL_NO_CONTEXT || this.eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }

    private static EGLContext createEglContext(EGLContext eGLContext, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i) {
        EGLContext eglCreateContext;
        if (eGLContext != null && eGLContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("Invalid sharedContext");
        }
        int[] iArr = {12440, i, 12344};
        if (eGLContext == null) {
            eGLContext = EGL14.EGL_NO_CONTEXT;
        }
        synchronized (EglBase.lock) {
            eglCreateContext = EGL14.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr, 0);
        }
        if (eglCreateContext != EGL14.EGL_NO_CONTEXT) {
            return eglCreateContext;
        }
        throw new RuntimeException("Failed to create EGL context: 0x" + Integer.toHexString(EGL14.eglGetError()));
    }

    private void createSurfaceInternal(Object obj, boolean z) {
        if (!(obj instanceof Surface) && !(obj instanceof SurfaceTexture)) {
            throw new IllegalStateException("Input must be either a Surface or SurfaceTexture");
        }
        checkIsNotReleased();
        if (z) {
            if (this.eglSurfaceBackground != EGL14.EGL_NO_SURFACE) {
                throw new RuntimeException("Already has an EGLSurface");
            }
            EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, obj, new int[]{12344}, 0);
            this.eglSurfaceBackground = eglCreateWindowSurface;
            if (eglCreateWindowSurface != EGL14.EGL_NO_SURFACE) {
                return;
            }
            throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
        if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        EGLSurface eglCreateWindowSurface2 = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, obj, new int[]{12344}, 0);
        this.eglSurface = eglCreateWindowSurface2;
        if (eglCreateWindowSurface2 != EGL14.EGL_NO_SURFACE) {
            return;
        }
        throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(EGL14.eglGetError()));
    }

    private static EGLConfig getEglConfig(EGLDisplay eGLDisplay, int[] iArr) {
        EGLConfig[] eGLConfigArr = new EGLConfig[1];
        int[] iArr2 = new int[1];
        if (!EGL14.eglChooseConfig(eGLDisplay, iArr, 0, eGLConfigArr, 0, 1, iArr2, 0)) {
            throw new RuntimeException("eglChooseConfig failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
        if (iArr2[0] <= 0) {
            throw new RuntimeException("Unable to find any matching EGL config");
        }
        EGLConfig eGLConfig = eGLConfigArr[0];
        if (eGLConfig != null) {
            return eGLConfig;
        }
        throw new RuntimeException("eglChooseConfig returned null");
    }

    private static EGLDisplay getEglDisplay() {
        EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
        if (eglGetDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL14 display: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
        int[] iArr = new int[2];
        if (EGL14.eglInitialize(eglGetDisplay, iArr, 0, iArr, 1)) {
            return eglGetDisplay;
        }
        throw new RuntimeException("Unable to initialize EGL14: 0x" + Integer.toHexString(EGL14.eglGetError()));
    }

    public static boolean isEGL14Supported() {
        StringBuilder sb = new StringBuilder();
        sb.append("SDK version: ");
        int i = CURRENT_SDK_VERSION;
        sb.append(i);
        sb.append(". isEGL14Supported: ");
        sb.append(i >= 18);
        Logging.d("EglBase14Impl", sb.toString());
        return i >= 18;
    }

    @Override
    public void createBackgroundSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture, true);
    }

    @Override
    public void createDummyPbufferSurface() {
        createPbufferSurface(1, 1);
    }

    @Override
    public void createPbufferSurface(int i, int i2) {
        checkIsNotReleased();
        if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        EGLSurface eglCreatePbufferSurface = EGL14.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, new int[]{12375, i, 12374, i2, 12344}, 0);
        this.eglSurface = eglCreatePbufferSurface;
        if (eglCreatePbufferSurface != EGL14.EGL_NO_SURFACE) {
            return;
        }
        throw new RuntimeException("Failed to create pixel buffer surface with size " + i + "x" + i2 + ": 0x" + Integer.toHexString(EGL14.eglGetError()));
    }

    @Override
    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture, false);
    }

    @Override
    public void createSurface(Surface surface) {
        createSurfaceInternal(surface, false);
    }

    @Override
    public void detachCurrent() {
        synchronized (EglBase.lock) {
            try {
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
                if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL14.EGL_NO_CONTEXT)) {
                    throw new RuntimeException("eglDetachCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override
    public Context getEglBaseContext() {
        return new Context(this.eglContext);
    }

    @Override
    public boolean hasBackgroundSurface() {
        return this.eglSurfaceBackground != EGL14.EGL_NO_SURFACE;
    }

    @Override
    public boolean hasSurface() {
        return this.eglSurface != EGL14.EGL_NO_SURFACE;
    }

    @Override
    public void makeBackgroundCurrent() {
        checkIsNotReleased();
        if (this.eglSurfaceBackground == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't make current");
        }
        synchronized (EglBase.lock) {
            try {
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurfaceBackground;
                if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override
    public void makeCurrent() {
        checkIsNotReleased();
        if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't make current");
        }
        synchronized (EglBase.lock) {
            try {
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override
    public void release() {
        checkIsNotReleased();
        releaseSurface(false);
        releaseSurface(true);
        detachCurrent();
        synchronized (EglBase.lock) {
            EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
        }
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(this.eglDisplay);
        this.eglContext = EGL14.EGL_NO_CONTEXT;
        this.eglDisplay = EGL14.EGL_NO_DISPLAY;
        this.eglConfig = null;
    }

    @Override
    public void releaseSurface(boolean z) {
        if (z) {
            EGLSurface eGLSurface = this.eglSurfaceBackground;
            if (eGLSurface != EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroySurface(this.eglDisplay, eGLSurface);
                this.eglSurfaceBackground = EGL14.EGL_NO_SURFACE;
                return;
            }
            return;
        }
        EGLSurface eGLSurface2 = this.eglSurface;
        if (eGLSurface2 != EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroySurface(this.eglDisplay, eGLSurface2);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    @Override
    public int surfaceHeight() {
        int[] iArr = new int[1];
        EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, iArr, 0);
        return iArr[0];
    }

    @Override
    public int surfaceWidth() {
        int[] iArr = new int[1];
        EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, iArr, 0);
        return iArr[0];
    }

    @Override
    public void swapBuffers(long j, boolean z) {
        checkIsNotReleased();
        EGLSurface eGLSurface = z ? this.eglSurfaceBackground : this.eglSurface;
        if (eGLSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't swap buffers");
        }
        synchronized (EglBase.lock) {
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, eGLSurface, j);
            EGL14.eglSwapBuffers(this.eglDisplay, eGLSurface);
        }
    }

    @Override
    public void swapBuffers(boolean z) {
        checkIsNotReleased();
        EGLSurface eGLSurface = z ? this.eglSurfaceBackground : this.eglSurface;
        if (eGLSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't swap buffers");
        }
        synchronized (EglBase.lock) {
            EGL14.eglSwapBuffers(this.eglDisplay, eGLSurface);
        }
    }
}
