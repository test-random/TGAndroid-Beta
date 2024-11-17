package org.telegram.ui.Components.spoilers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES31;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

public class SpoilerEffect2 {
    private static HashMap instance;
    private final double MAX_DELTA;
    public final int MAX_FPS;
    private final double MIN_DELTA;
    public boolean destroyed;
    private int height;
    private final TextureView textureView;
    private final ViewGroup textureViewContainer;
    private SpoilerThread thread;
    public final int type;
    private int width;
    private final ArrayList holders = new ArrayList();
    private final HashMap holdersToIndex = new HashMap();
    private int holdersIndex = 0;
    private final Runnable checkDestroy = new Runnable() {
        @Override
        public final void run() {
            SpoilerEffect2.this.lambda$new$0();
        }
    };

    public class SpoilerThread extends Thread {
        private int deltaTimeHandle;
        private int drawProgram;
        private EGL10 egl;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private int height;
        private final Runnable invalidate;
        private int[] particlesData;
        private int radiusHandle;
        private int resetHandle;
        private boolean resize;
        private int seedHandle;
        private int sizeHandle;
        private final SurfaceTexture surfaceTexture;
        private float t;
        private int timeHandle;
        private int width;
        private volatile boolean running = true;
        private volatile boolean paused = false;
        private final Object resizeLock = new Object();
        private float radius = AndroidUtilities.dpf2(1.2f);
        private boolean reset = true;
        private int currentBuffer = 0;
        private final float timeScale = 0.65f;
        private int particlesCount = particlesCount();

        public SpoilerThread(SurfaceTexture surfaceTexture, int i, int i2, Runnable runnable) {
            this.invalidate = runnable;
            this.surfaceTexture = surfaceTexture;
            this.width = i;
            this.height = i2;
        }

        private void checkGlErrors() {
            int glGetError;
            while (true) {
                glGetError = GLES31.glGetError();
                if (glGetError == 0) {
                    return;
                }
                FileLog.e("spoiler gles error " + glGetError);
            }
        }

        private void checkResize() {
            synchronized (this.resizeLock) {
                try {
                    if (this.resize) {
                        GLES31.glUniform2f(this.sizeHandle, this.width, this.height);
                        GLES31.glViewport(0, 0, this.width, this.height);
                        int particlesCount = particlesCount();
                        if (particlesCount > this.particlesCount) {
                            this.reset = true;
                            genParticlesData();
                        }
                        this.particlesCount = particlesCount;
                        this.resize = false;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        private void die() {
            int[] iArr = this.particlesData;
            if (iArr != null) {
                try {
                    GLES31.glDeleteBuffers(2, iArr, 0);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.particlesData = null;
            }
            int i = this.drawProgram;
            if (i != 0) {
                try {
                    GLES31.glDeleteProgram(i);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                this.drawProgram = 0;
            }
            EGL10 egl10 = this.egl;
            if (egl10 != null) {
                try {
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                    egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
                try {
                    this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
                try {
                    this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
            try {
                this.surfaceTexture.release();
            } catch (Exception e6) {
                FileLog.e(e6);
            }
            checkGlErrors();
        }

        private void drawFrame(float f) {
            EGL10 egl10 = this.egl;
            EGLDisplay eGLDisplay = this.eglDisplay;
            EGLSurface eGLSurface = this.eglSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                this.running = false;
                return;
            }
            float f2 = 0.65f * f;
            float f3 = this.t + f2;
            this.t = f3;
            if (f3 > 1000.0f) {
                this.t = 0.0f;
            }
            GLES31.glClear(16384);
            GLES31.glBindBuffer(34962, this.particlesData[this.currentBuffer]);
            GLES31.glVertexAttribPointer(0, 2, 5126, false, 24, 0);
            GLES31.glEnableVertexAttribArray(0);
            GLES31.glVertexAttribPointer(1, 2, 5126, false, 24, 8);
            GLES31.glEnableVertexAttribArray(1);
            GLES31.glVertexAttribPointer(2, 1, 5126, false, 24, 16);
            GLES31.glEnableVertexAttribArray(2);
            GLES31.glVertexAttribPointer(3, 1, 5126, false, 24, 20);
            GLES31.glEnableVertexAttribArray(3);
            GLES31.glBindBufferBase(35982, 0, this.particlesData[1 - this.currentBuffer]);
            GLES31.glVertexAttribPointer(0, 2, 5126, false, 24, 0);
            GLES31.glEnableVertexAttribArray(0);
            GLES31.glVertexAttribPointer(1, 2, 5126, false, 24, 8);
            GLES31.glEnableVertexAttribArray(1);
            GLES31.glVertexAttribPointer(2, 1, 5126, false, 24, 16);
            GLES31.glEnableVertexAttribArray(2);
            GLES31.glVertexAttribPointer(3, 1, 5126, false, 24, 20);
            GLES31.glEnableVertexAttribArray(3);
            GLES31.glUniform1f(this.timeHandle, this.t);
            GLES31.glUniform1f(this.deltaTimeHandle, f2);
            GLES31.glBeginTransformFeedback(0);
            GLES31.glDrawArrays(0, 0, this.particlesCount);
            GLES31.glEndTransformFeedback();
            if (this.reset) {
                this.reset = false;
                GLES31.glUniform1f(this.resetHandle, 0.0f);
            }
            this.currentBuffer = 1 - this.currentBuffer;
            this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            checkGlErrors();
        }

        private void genParticlesData() {
            int[] iArr = this.particlesData;
            if (iArr != null) {
                GLES31.glDeleteBuffers(2, iArr, 0);
            }
            int[] iArr2 = new int[2];
            this.particlesData = iArr2;
            GLES31.glGenBuffers(2, iArr2, 0);
            for (int i = 0; i < 2; i++) {
                GLES31.glBindBuffer(34962, this.particlesData[i]);
                GLES31.glBufferData(34962, this.particlesCount * 24, null, 35048);
            }
            checkGlErrors();
        }

        private void init() {
            int glCreateShader;
            int glCreateShader2;
            int glCreateProgram;
            int glGetUniformLocation;
            int glGetUniformLocation2;
            int glGetUniformLocation3;
            int glGetUniformLocation4;
            int glGetUniformLocation5;
            int glGetUniformLocation6;
            String glGetProgramInfoLog;
            String glGetShaderInfoLog;
            String glGetShaderInfoLog2;
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(0);
            this.eglDisplay = eglGetDisplay;
            EGL10 egl102 = this.egl;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                this.running = false;
                return;
            }
            if (!egl102.eglInitialize(eglGetDisplay, new int[2])) {
                this.running = false;
                return;
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 64, 12344}, eGLConfigArr, 1, new int[1])) {
                this.running = false;
                return;
            }
            EGLConfig eGLConfig = eGLConfigArr[0];
            this.eglConfig = eGLConfig;
            EGLContext eglCreateContext = this.egl.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 3, 12344});
            this.eglContext = eglCreateContext;
            if (eglCreateContext == null) {
                this.running = false;
                return;
            }
            EGLSurface eglCreateWindowSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
            this.eglSurface = eglCreateWindowSurface;
            if (eglCreateWindowSurface == null) {
                this.running = false;
                return;
            }
            if (!this.egl.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                this.running = false;
                return;
            }
            genParticlesData();
            glCreateShader = GLES31.glCreateShader(35633);
            glCreateShader2 = GLES31.glCreateShader(35632);
            if (glCreateShader == 0 || glCreateShader2 == 0) {
                this.running = false;
                return;
            }
            GLES31.glShaderSource(glCreateShader, AndroidUtilities.readRes(R.raw.spoiler_vertex) + "\n// " + Math.random());
            GLES31.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES31.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("SpoilerEffect2, compile vertex shader error: ");
                glGetShaderInfoLog2 = GLES31.glGetShaderInfoLog(glCreateShader);
                sb.append(glGetShaderInfoLog2);
                FileLog.e(sb.toString());
                GLES31.glDeleteShader(glCreateShader);
                this.running = false;
                return;
            }
            GLES31.glShaderSource(glCreateShader2, AndroidUtilities.readRes(R.raw.spoiler_fragment) + "\n// " + Math.random());
            GLES31.glCompileShader(glCreateShader2);
            GLES31.glGetShaderiv(glCreateShader2, 35713, iArr, 0);
            if (iArr[0] == 0) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("SpoilerEffect2, compile fragment shader error: ");
                glGetShaderInfoLog = GLES31.glGetShaderInfoLog(glCreateShader2);
                sb2.append(glGetShaderInfoLog);
                FileLog.e(sb2.toString());
                GLES31.glDeleteShader(glCreateShader2);
                this.running = false;
                return;
            }
            glCreateProgram = GLES31.glCreateProgram();
            this.drawProgram = glCreateProgram;
            if (glCreateProgram == 0) {
                this.running = false;
                return;
            }
            GLES31.glAttachShader(glCreateProgram, glCreateShader);
            GLES31.glAttachShader(this.drawProgram, glCreateShader2);
            GLES31.glTransformFeedbackVaryings(this.drawProgram, new String[]{"outPosition", "outVelocity", "outTime", "outDuration"}, 35980);
            GLES31.glLinkProgram(this.drawProgram);
            GLES31.glGetProgramiv(this.drawProgram, 35714, iArr, 0);
            if (iArr[0] == 0) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("SpoilerEffect2, link draw program error: ");
                glGetProgramInfoLog = GLES31.glGetProgramInfoLog(this.drawProgram);
                sb3.append(glGetProgramInfoLog);
                FileLog.e(sb3.toString());
                this.running = false;
                return;
            }
            glGetUniformLocation = GLES31.glGetUniformLocation(this.drawProgram, "reset");
            this.resetHandle = glGetUniformLocation;
            glGetUniformLocation2 = GLES31.glGetUniformLocation(this.drawProgram, "time");
            this.timeHandle = glGetUniformLocation2;
            glGetUniformLocation3 = GLES31.glGetUniformLocation(this.drawProgram, "deltaTime");
            this.deltaTimeHandle = glGetUniformLocation3;
            glGetUniformLocation4 = GLES31.glGetUniformLocation(this.drawProgram, "size");
            this.sizeHandle = glGetUniformLocation4;
            glGetUniformLocation5 = GLES31.glGetUniformLocation(this.drawProgram, "r");
            this.radiusHandle = glGetUniformLocation5;
            glGetUniformLocation6 = GLES31.glGetUniformLocation(this.drawProgram, "seed");
            this.seedHandle = glGetUniformLocation6;
            GLES31.glViewport(0, 0, this.width, this.height);
            GLES31.glEnable(3042);
            GLES31.glBlendFunc(770, 771);
            GLES31.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES31.glUseProgram(this.drawProgram);
            GLES31.glUniform2f(this.sizeHandle, this.width, this.height);
            GLES31.glUniform1f(this.resetHandle, this.reset ? 1.0f : 0.0f);
            GLES31.glUniform1f(this.radiusHandle, this.radius);
            GLES31.glUniform1f(this.seedHandle, Utilities.fastRandom.nextInt(256) / 256.0f);
        }

        private int particlesCount() {
            return (int) Utilities.clamp(((this.width * this.height) / 250000.0f) * 1000.0f, 10000.0f, 500.0f);
        }

        public void halt() {
            this.running = false;
        }

        public void pause(boolean z) {
            this.paused = z;
        }

        @Override
        public void run() {
            init();
            long nanoTime = System.nanoTime();
            while (this.running) {
                long nanoTime2 = System.nanoTime();
                double d = nanoTime2 - nanoTime;
                Double.isNaN(d);
                double d2 = d / 1.0E9d;
                if (d2 < SpoilerEffect2.this.MIN_DELTA) {
                    double d3 = SpoilerEffect2.this.MIN_DELTA - d2;
                    long j = (long) (d3 * 1000.0d);
                    double d4 = j;
                    Double.isNaN(d4);
                    try {
                        Thread.sleep(j, (int) ((d3 - (d4 / 1000.0d)) * 1.0E9d));
                    } catch (Exception unused) {
                    }
                    d2 = SpoilerEffect2.this.MIN_DELTA;
                } else if (d2 > SpoilerEffect2.this.MAX_DELTA) {
                    d2 = SpoilerEffect2.this.MAX_DELTA;
                }
                while (this.paused) {
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception unused2) {
                    }
                }
                checkResize();
                drawFrame((float) d2);
                AndroidUtilities.cancelRunOnUIThread(this.invalidate);
                AndroidUtilities.runOnUIThread(this.invalidate);
                nanoTime = nanoTime2;
            }
            die();
        }

        public void updateSize(int i, int i2) {
            synchronized (this.resizeLock) {
                this.resize = true;
                this.width = i;
                this.height = i2;
            }
        }
    }

    private SpoilerEffect2(int i, ViewGroup viewGroup, int i2, int i3) {
        int i4 = (int) AndroidUtilities.screenRefreshRate;
        this.MAX_FPS = i4;
        double d = i4;
        Double.isNaN(d);
        double d2 = 1.0d / d;
        this.MIN_DELTA = d2;
        this.MAX_DELTA = d2 * 4.0d;
        this.type = i;
        this.width = i2;
        this.height = i3;
        this.textureViewContainer = viewGroup;
        TextureView textureView = new TextureView(viewGroup.getContext()) {
            @Override
            protected void onMeasure(int i5, int i6) {
                setMeasuredDimension(SpoilerEffect2.this.width, SpoilerEffect2.this.height);
            }
        };
        this.textureView = textureView;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i5, int i6) {
                if (SpoilerEffect2.this.thread == null) {
                    SpoilerEffect2 spoilerEffect2 = SpoilerEffect2.this;
                    final SpoilerEffect2 spoilerEffect22 = SpoilerEffect2.this;
                    spoilerEffect2.thread = new SpoilerThread(surfaceTexture, i5, i6, new Runnable() {
                        @Override
                        public final void run() {
                            SpoilerEffect2.this.invalidate();
                        }
                    });
                    SpoilerEffect2.this.thread.start();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (SpoilerEffect2.this.thread == null) {
                    return true;
                }
                SpoilerEffect2.this.thread.halt();
                SpoilerEffect2.this.thread = null;
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i5, int i6) {
                if (SpoilerEffect2.this.thread != null) {
                    SpoilerEffect2.this.thread.updateSize(i5, i6);
                }
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        textureView.setOpaque(false);
        viewGroup.addView(textureView);
    }

    private void destroy() {
        this.destroyed = true;
        instance = null;
        SpoilerThread spoilerThread = this.thread;
        if (spoilerThread != null) {
            spoilerThread.halt();
            this.thread = null;
        }
        this.textureViewContainer.removeView(this.textureView);
        if (this.textureViewContainer.getParent() instanceof ViewGroup) {
            ((ViewGroup) this.textureViewContainer.getParent()).removeView(this.textureViewContainer);
        }
    }

    public static SpoilerEffect2 getInstance(int i, View view) {
        return getInstance(i, view, getRootView(view));
    }

    public static SpoilerEffect2 getInstance(int i, View view, ViewGroup viewGroup) {
        if (view == null || !supports()) {
            return null;
        }
        if (instance == null) {
            instance = new HashMap();
        }
        SpoilerEffect2 spoilerEffect2 = (SpoilerEffect2) instance.get(Integer.valueOf(i));
        if (spoilerEffect2 == null) {
            int size = getSize();
            if (viewGroup == null) {
                return null;
            }
            HashMap hashMap = instance;
            Integer valueOf = Integer.valueOf(i);
            SpoilerEffect2 spoilerEffect22 = new SpoilerEffect2(i, makeTextureViewContainer(viewGroup), size, size);
            hashMap.put(valueOf, spoilerEffect22);
            spoilerEffect2 = spoilerEffect22;
        }
        spoilerEffect2.attach(view);
        return spoilerEffect2;
    }

    public static SpoilerEffect2 getInstance(View view) {
        return getInstance(0, view);
    }

    private static ViewGroup getRootView(View view) {
        Activity findActivity = AndroidUtilities.findActivity(view.getContext());
        if (findActivity == null) {
            return null;
        }
        View rootView = findActivity.findViewById(16908290).getRootView();
        if (rootView instanceof ViewGroup) {
            return (ViewGroup) rootView;
        }
        return null;
    }

    private static int getSize() {
        int i;
        int i2;
        int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
        if (devicePerformanceClass == 1) {
            Point point = AndroidUtilities.displaySize;
            i = (int) (((point.x + point.y) / 2.0f) * 0.8f);
            i2 = 900;
        } else if (devicePerformanceClass != 2) {
            Point point2 = AndroidUtilities.displaySize;
            i = (int) (((point2.x + point2.y) / 2.0f) * 0.7f);
            i2 = 720;
        } else {
            Point point3 = AndroidUtilities.displaySize;
            i = (int) (((point3.x + point3.y) / 2.0f) * 1.0f);
            i2 = 1280;
        }
        return Math.min(i2, i);
    }

    public void lambda$new$0() {
        if (this.holders.isEmpty()) {
            destroy();
        }
    }

    private static FrameLayout makeTextureViewContainer(ViewGroup viewGroup) {
        FrameLayout frameLayout = new FrameLayout(viewGroup.getContext()) {
            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                return false;
            }
        };
        viewGroup.addView(frameLayout);
        return frameLayout;
    }

    public static void pause(int i, boolean z) {
        SpoilerThread spoilerThread;
        HashMap hashMap = instance;
        if (hashMap == null) {
            return;
        }
        for (SpoilerEffect2 spoilerEffect2 : hashMap.values()) {
            if (spoilerEffect2.type == i && (spoilerThread = spoilerEffect2.thread) != null) {
                spoilerThread.pause(z);
            }
        }
    }

    public static void pause(boolean z) {
        HashMap hashMap = instance;
        if (hashMap == null) {
            return;
        }
        Iterator it = hashMap.values().iterator();
        while (it.hasNext()) {
            SpoilerThread spoilerThread = ((SpoilerEffect2) it.next()).thread;
            if (spoilerThread != null) {
                spoilerThread.pause(z);
            }
        }
    }

    public static boolean supports() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public void attach(View view) {
        if (this.destroyed || this.holders.contains(view)) {
            return;
        }
        this.holders.add(view);
        HashMap hashMap = this.holdersToIndex;
        int i = this.holdersIndex;
        this.holdersIndex = i + 1;
        hashMap.put(view, Integer.valueOf(i));
    }

    public void detach(View view) {
        this.holders.remove(view);
        this.holdersToIndex.remove(view);
        if (this.destroyed) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkDestroy);
        AndroidUtilities.runOnUIThread(this.checkDestroy, 30L);
    }

    public void draw(Canvas canvas, View view, int i, int i2) {
        draw(canvas, view, i, i2, 1.0f);
    }

    public void draw(Canvas canvas, View view, int i, int i2, float f) {
        draw(canvas, view, i, i2, f, false);
    }

    public void draw(Canvas canvas, View view, int i, int i2, float f, boolean z) {
        if (canvas == null || view == null) {
            return;
        }
        canvas.save();
        int i3 = this.width;
        int i4 = this.height;
        Integer num = (Integer) this.holdersToIndex.get(view);
        if (num == null) {
            num = 0;
        }
        if (num.intValue() % 4 == 1) {
            canvas.rotate(180.0f, i3 / 2.0f, i4 / 2.0f);
        }
        if (num.intValue() % 4 == 2) {
            canvas.scale(-1.0f, 1.0f, i3 / 2.0f, i4 / 2.0f);
        }
        if (num.intValue() % 4 == 3) {
            canvas.scale(1.0f, -1.0f, i3 / 2.0f, i4 / 2.0f);
        }
        if (i > i3 || i2 > i4) {
            float max = Math.max(i / i3, i2 / i4);
            canvas.scale(max, max);
        }
        TextureView textureView = this.textureView;
        if (z) {
            Bitmap bitmap = textureView.getBitmap();
            if (bitmap != null) {
                Paint paint = new Paint(7);
                paint.setColor(-1);
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                bitmap.recycle();
            }
        } else {
            textureView.setAlpha(f);
            this.textureView.draw(canvas);
        }
        canvas.restore();
    }

    public int getAttachIndex(View view) {
        Integer num = (Integer) this.holdersToIndex.get(view);
        if (num == null) {
            num = 0;
        }
        return num.intValue();
    }

    public void invalidate() {
        for (int i = 0; i < this.holders.size(); i++) {
            ((View) this.holders.get(i)).invalidate();
        }
    }

    public void reassignAttach(View view, int i) {
        this.holdersToIndex.put(view, Integer.valueOf(i));
    }
}
