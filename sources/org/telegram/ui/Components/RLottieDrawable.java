package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DispatchQueuePool;
import org.telegram.messenger.DispatchQueuePoolBackground;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.BitmapsCache;
import org.telegram.ui.Components.RLottieDrawable;

public class RLottieDrawable extends BitmapDrawable implements Animatable, BitmapsCache.Cacheable {
    public static DispatchQueue lottieCacheGenerateQueue;
    private boolean allowDrawFramesWhileCacheGenerating;
    private boolean allowVibration;
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    NativePtrArgs args;
    protected int autoRepeat;
    protected int autoRepeatCount;
    protected int autoRepeatPlayCount;
    protected long autoRepeatTimeout;
    protected volatile Bitmap backgroundBitmap;
    private Paint[] backgroundPaint;
    BitmapsCache bitmapsCache;
    protected Runnable cacheGenerateTask;
    private boolean createdForFirstFrame;
    protected int currentFrame;
    private View currentParentView;
    protected int customEndFrame;
    private boolean decodeSingleFrame;
    protected boolean destroyAfterLoading;
    protected boolean destroyWhenDone;
    protected int diceSwitchFramesCount;
    private boolean doNotRemoveInvalidOnFrameReady;
    private final RectF dstRect;
    private RectF[] dstRectBackground;
    private boolean fallbackCache;
    File file;
    private int finishFrame;
    private boolean forceFrameRedraw;
    protected CountDownLatch frameWaitSync;
    private boolean genCacheSend;
    int generateCacheFramePointer;
    long generateCacheNativePtr;
    boolean generatingCache;
    protected final int height;
    private boolean invalidateOnProgressSet;
    protected int isDice;
    private boolean isInvalid;
    protected volatile boolean isRecycled;
    protected volatile boolean isRunning;
    private long lastFrameTime;
    protected Runnable loadFrameRunnable;
    protected Runnable loadFrameTask;
    protected boolean loadingInBackground;
    private View masterParent;
    protected final int[] metaData;
    protected volatile long nativePtr;
    private boolean needScale;
    private HashMap newColorUpdates;
    private int[] newReplaceColors;
    protected volatile boolean nextFrameIsLast;
    protected volatile Bitmap nextRenderingBitmap;
    private Runnable onAnimationEndListener;
    protected WeakReference onFinishCallback;
    private Runnable onFrameReadyRunnable;
    private final ArrayList parentViews;
    private volatile HashMap pendingColorUpdates;
    private int[] pendingReplaceColors;
    protected boolean playInDirectionOfCustomEndFrame;
    boolean precache;
    private Bitmap rawBackgroundBitmap;
    private int rawBackgroundBitmapFrame;
    protected volatile Bitmap renderingBitmap;
    private boolean resetVibrationAfterRestart;
    public boolean scaleByCanvas;
    private float scaleX;
    private float scaleY;
    protected int secondFramesCount;
    protected boolean secondLoadingInBackground;
    protected volatile long secondNativePtr;
    protected volatile boolean setLastFrame;
    private boolean shouldLimitFps;
    private boolean singleFrameDecoded;
    public boolean skipFrameUpdate;
    public android.graphics.Rect srcRect;
    protected int timeBetweenFrames;
    protected Runnable uiRunnable;
    private Runnable uiRunnableCacheFinished;
    private Runnable uiRunnableGenerateCache;
    protected Runnable uiRunnableNoFrame;
    private HashMap vibrationPattern;
    protected boolean waitingForNextTask;
    public Runnable whenCacheDone;
    protected final int width;
    protected static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final DispatchQueuePool loadFrameRunnableQueue = new DispatchQueuePool(4);

    public class AnonymousClass3 implements Runnable {
        AnonymousClass3() {
        }

        public void lambda$run$0() {
            try {
                BitmapsCache bitmapsCache = RLottieDrawable.this.bitmapsCache;
                if (bitmapsCache != null) {
                    bitmapsCache.createCache();
                }
            } catch (Throwable unused) {
            }
            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
        }

        @Override
        public void run() {
            if (RLottieDrawable.this.isRecycled) {
                return;
            }
            RLottieDrawable rLottieDrawable = RLottieDrawable.this;
            if (rLottieDrawable.destroyWhenDone || !rLottieDrawable.canLoadFrames()) {
                return;
            }
            RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
            if (rLottieDrawable2.cacheGenerateTask == null) {
                rLottieDrawable2.generatingCache = true;
                if (RLottieDrawable.lottieCacheGenerateQueue == null) {
                    RLottieDrawable.createCacheGenQueue();
                }
                BitmapsCache.incrementTaskCounter();
                DispatchQueue dispatchQueue = RLottieDrawable.lottieCacheGenerateQueue;
                RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        RLottieDrawable.AnonymousClass3.this.lambda$run$0();
                    }
                };
                rLottieDrawable3.cacheGenerateTask = runnable;
                dispatchQueue.postRunnable(runnable);
            }
        }
    }

    public class NativePtrArgs {
        public int[] colorReplacement;
        File file;
        public int fitzModifier;
        String json;

        private NativePtrArgs() {
        }
    }

    public RLottieDrawable(int i, String str, int i2, int i3) {
        this(i, str, i2, i3, true, null);
    }

    public RLottieDrawable(int i, String str, int i2, int i3, boolean z, int[] iArr) {
        int[] iArr2 = new int[3];
        this.metaData = iArr2;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap();
        this.pendingColorUpdates = new HashMap();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.dstRectBackground = new RectF[2];
        this.backgroundPaint = new Paint[2];
        this.uiRunnableNoFrame = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() {
            @Override
            public void run() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    BitmapsCache.decrementTaskCounter();
                    RLottieDrawable.this.cacheGenerateTask = null;
                }
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
                Runnable runnable = RLottieDrawable.this.whenCacheDone;
                if (runnable != null) {
                    runnable.run();
                    RLottieDrawable.this.whenCacheDone = null;
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            private long lastUpdate = 0;

            @Override
            public void run() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.AnonymousClass5.run():void");
            }
        };
        this.srcRect = new android.graphics.Rect();
        this.rawBackgroundBitmapFrame = -1;
        this.width = i2;
        this.height = i3;
        this.autoRepeat = 0;
        String readRes = AndroidUtilities.readRes(i);
        if (TextUtils.isEmpty(readRes)) {
            return;
        }
        getPaint().setFlags(2);
        this.nativePtr = createWithJson(readRes, str, iArr2, iArr);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr2[1]));
        if (z) {
            setAllowDecodeSingleFrame(true);
        }
    }

    public RLottieDrawable(File file, int i, int i2, BitmapsCache.CacheOptions cacheOptions, boolean z, int[] iArr, int i3) {
        char c;
        int[] iArr2;
        int[] iArr3 = new int[3];
        this.metaData = iArr3;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap();
        this.pendingColorUpdates = new HashMap();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.dstRectBackground = new RectF[2];
        this.backgroundPaint = new Paint[2];
        this.uiRunnableNoFrame = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() {
            @Override
            public void run() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    BitmapsCache.decrementTaskCounter();
                    RLottieDrawable.this.cacheGenerateTask = null;
                }
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
                Runnable runnable = RLottieDrawable.this.whenCacheDone;
                if (runnable != null) {
                    runnable.run();
                    RLottieDrawable.this.whenCacheDone = null;
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            private long lastUpdate = 0;

            @Override
            public void run() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.AnonymousClass5.run():void");
            }
        };
        this.srcRect = new android.graphics.Rect();
        this.rawBackgroundBitmapFrame = -1;
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z;
        this.precache = cacheOptions != null;
        this.fallbackCache = cacheOptions != null && cacheOptions.fallback;
        this.createdForFirstFrame = cacheOptions != null && cacheOptions.firstFrame;
        getPaint().setFlags(2);
        this.file = file;
        if (this.precache && lottieCacheGenerateQueue == null) {
            createCacheGenQueue();
        }
        if (this.precache) {
            NativePtrArgs nativePtrArgs = new NativePtrArgs();
            this.args = nativePtrArgs;
            nativePtrArgs.file = file.getAbsoluteFile();
            NativePtrArgs nativePtrArgs2 = this.args;
            nativePtrArgs2.json = null;
            nativePtrArgs2.colorReplacement = iArr;
            nativePtrArgs2.fitzModifier = i3;
            if (this.createdForFirstFrame) {
                return;
            }
            parseLottieMetadata(file, null, iArr3);
            if (this.shouldLimitFps && iArr3[1] < 60) {
                this.shouldLimitFps = false;
            }
            this.bitmapsCache = new BitmapsCache(file, this, cacheOptions, i, i2, !z);
            iArr2 = iArr3;
            c = 1;
        } else {
            c = 1;
            iArr2 = iArr3;
            this.nativePtr = create(file.getAbsolutePath(), null, i, i2, iArr3, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr == 0) {
                FileLog.d("RLottieDrawable nativePtr == 0 " + file.getAbsolutePath() + " remove file");
                file.delete();
            }
            if (this.shouldLimitFps && iArr2[1] < 60) {
                this.shouldLimitFps = false;
            }
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / iArr2[c]));
    }

    public RLottieDrawable(File file, String str, int i, int i2, BitmapsCache.CacheOptions cacheOptions, boolean z, int[] iArr, int i3) {
        char c;
        int[] iArr2;
        int[] iArr3 = new int[3];
        this.metaData = iArr3;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap();
        this.pendingColorUpdates = new HashMap();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.dstRectBackground = new RectF[2];
        this.backgroundPaint = new Paint[2];
        this.uiRunnableNoFrame = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() {
            @Override
            public void run() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    BitmapsCache.decrementTaskCounter();
                    RLottieDrawable.this.cacheGenerateTask = null;
                }
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
                Runnable runnable = RLottieDrawable.this.whenCacheDone;
                if (runnable != null) {
                    runnable.run();
                    RLottieDrawable.this.whenCacheDone = null;
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            private long lastUpdate = 0;

            @Override
            public void run() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.AnonymousClass5.run():void");
            }
        };
        this.srcRect = new android.graphics.Rect();
        this.rawBackgroundBitmapFrame = -1;
        this.width = i;
        this.height = i2;
        this.shouldLimitFps = z;
        this.precache = cacheOptions != null;
        this.createdForFirstFrame = cacheOptions != null && cacheOptions.firstFrame;
        getPaint().setFlags(2);
        if (this.precache && lottieCacheGenerateQueue == null) {
            createCacheGenQueue();
        }
        if (this.precache) {
            NativePtrArgs nativePtrArgs = new NativePtrArgs();
            this.args = nativePtrArgs;
            nativePtrArgs.file = file.getAbsoluteFile();
            NativePtrArgs nativePtrArgs2 = this.args;
            nativePtrArgs2.json = str;
            nativePtrArgs2.colorReplacement = iArr;
            nativePtrArgs2.fitzModifier = i3;
            if (this.createdForFirstFrame) {
                return;
            }
            parseLottieMetadata(file, str, iArr3);
            if (this.shouldLimitFps && iArr3[1] < 60) {
                this.shouldLimitFps = false;
            }
            this.bitmapsCache = new BitmapsCache(file, this, cacheOptions, i, i2, !z);
            iArr2 = iArr3;
            c = 1;
        } else {
            c = 1;
            iArr2 = iArr3;
            this.nativePtr = create(file.getAbsolutePath(), str, i, i2, iArr3, this.precache, iArr, this.shouldLimitFps, i3);
            if (this.nativePtr == 0) {
                FileLog.d("RLottieDrawable nativePtr == 0 " + file.getAbsolutePath() + " remove file");
                file.delete();
            }
            if (this.shouldLimitFps && iArr2[1] < 60) {
                this.shouldLimitFps = false;
            }
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / iArr2[c]));
    }

    public RLottieDrawable(String str, int i, int i2) {
        String readRes;
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap();
        this.pendingColorUpdates = new HashMap();
        this.resetVibrationAfterRestart = false;
        this.allowVibration = true;
        this.parentViews = new ArrayList();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new RectF();
        this.dstRectBackground = new RectF[2];
        this.backgroundPaint = new Paint[2];
        this.uiRunnableNoFrame = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.loadFrameTask = null;
                rLottieDrawable.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() {
            @Override
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass3();
        this.uiRunnableCacheFinished = new Runnable() {
            @Override
            public void run() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    BitmapsCache.decrementTaskCounter();
                    RLottieDrawable.this.cacheGenerateTask = null;
                }
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                rLottieDrawable.generatingCache = false;
                rLottieDrawable.decodeFrameFinishedInternal();
                Runnable runnable = RLottieDrawable.this.whenCacheDone;
                if (runnable != null) {
                    runnable.run();
                    RLottieDrawable.this.whenCacheDone = null;
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            private long lastUpdate = 0;

            @Override
            public void run() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.AnonymousClass5.run():void");
            }
        };
        this.srcRect = new android.graphics.Rect();
        this.rawBackgroundBitmapFrame = -1;
        this.width = i;
        this.height = i2;
        this.isDice = 1;
        if ("🎲".equals(str)) {
            readRes = AndroidUtilities.readRes(R.raw.diceloop);
            this.diceSwitchFramesCount = 60;
        } else {
            readRes = "🎯".equals(str) ? AndroidUtilities.readRes(R.raw.dartloop) : null;
        }
        getPaint().setFlags(2);
        if (TextUtils.isEmpty(readRes)) {
            this.timeBetweenFrames = 16;
        } else {
            this.nativePtr = createWithJson(readRes, "dice", iArr, null);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr[1]));
        }
    }

    public void checkDispatchOnAnimationEnd() {
        Runnable runnable = this.onAnimationEndListener;
        if (runnable != null) {
            runnable.run();
            this.onAnimationEndListener = null;
        }
    }

    public static native long create(String str, String str2, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2, int i3);

    public static void createCacheGenQueue() {
        lottieCacheGenerateQueue = new DispatchQueue("cache generator queue");
    }

    public static native long createWithJson(String str, String str2, int[] iArr, int[] iArr2);

    public static native void destroy(long j);

    public static native double getDuration(String str, String str2);

    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4, boolean z);

    public static native long getFramesCount(String str, String str2);

    public static void lambda$recycleNativePtr$0(long j, long j2) {
        if (j != 0) {
            destroy(j);
        }
        if (j2 != 0) {
            destroy(j2);
        }
    }

    public static void lambda$recycleNativePtr$1(long j, long j2) {
        if (j != 0) {
            destroy(j);
        }
        if (j2 != 0) {
            destroy(j2);
        }
    }

    public void lambda$setBaseDice$2() {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle(true);
            return;
        }
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public void lambda$setBaseDice$3(String str) {
        this.nativePtr = createWithJson(str, "dice", this.metaData, null);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                RLottieDrawable.this.lambda$setBaseDice$2();
            }
        });
    }

    public void lambda$setDiceNumber$4() {
        this.secondLoadingInBackground = false;
        if (this.loadingInBackground || !this.destroyAfterLoading) {
            return;
        }
        recycle(true);
    }

    public void lambda$setDiceNumber$5(int[] iArr) {
        this.secondLoadingInBackground = false;
        if (this.destroyAfterLoading) {
            recycle(true);
            return;
        }
        this.secondFramesCount = iArr[0];
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public void lambda$setDiceNumber$6(String str) {
        Runnable runnable;
        if (this.destroyAfterLoading) {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    RLottieDrawable.this.lambda$setDiceNumber$4();
                }
            };
        } else {
            final int[] iArr = new int[3];
            this.secondNativePtr = createWithJson(str, "dice", iArr, null);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    RLottieDrawable.this.lambda$setDiceNumber$5(iArr);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    private void parseLottieMetadata(java.io.File r15, java.lang.String r16, int[] r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RLottieDrawable.parseLottieMetadata(java.io.File, java.lang.String, int[]):void");
    }

    private void recycleNativePtr(boolean z) {
        final long j = this.nativePtr;
        final long j2 = this.secondNativePtr;
        this.nativePtr = 0L;
        this.secondNativePtr = 0L;
        if (j == 0 && j2 == 0) {
            return;
        }
        if (z) {
            DispatchQueuePoolBackground.execute(new Runnable() {
                @Override
                public final void run() {
                    RLottieDrawable.lambda$recycleNativePtr$0(j, j2);
                }
            });
        } else {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    RLottieDrawable.lambda$recycleNativePtr$1(j, j2);
                }
            });
        }
    }

    public static native void replaceColors(long j, int[] iArr);

    private void requestRedrawColors() {
        if (!this.applyingLayerColors && !this.isRunning && this.decodeSingleFrame) {
            if (this.currentFrame <= 2) {
                this.currentFrame = 0;
            }
            this.nextFrameIsLast = false;
            this.singleFrameDecoded = false;
            if (!scheduleNextGetFrame()) {
                this.forceFrameRedraw = true;
            }
        }
        invalidateInternal();
    }

    private void setCurrentFrame(long j, long j2, long j3, boolean z) {
        WeakReference weakReference;
        Runnable runnable;
        WeakReference weakReference2;
        this.backgroundBitmap = this.renderingBitmap;
        this.renderingBitmap = this.nextRenderingBitmap;
        this.nextRenderingBitmap = null;
        if (this.isDice == 2 && (weakReference2 = this.onFinishCallback) != null && this.currentFrame - 1 >= this.finishFrame) {
            Runnable runnable2 = (Runnable) weakReference2.get();
            if (runnable2 != null) {
                runnable2.run();
            }
            this.onFinishCallback = null;
        }
        if (this.nextFrameIsLast || (this.autoRepeatCount == 0 && this.autoRepeat == 1)) {
            stop();
        }
        this.loadFrameTask = null;
        if (this.doNotRemoveInvalidOnFrameReady) {
            this.doNotRemoveInvalidOnFrameReady = false;
        } else if (this.isInvalid) {
            this.isInvalid = false;
        }
        this.singleFrameDecoded = true;
        this.waitingForNextTask = false;
        if (AndroidUtilities.screenRefreshRate > 60.0f) {
            j -= Math.min(16L, j2 - j3);
        }
        this.lastFrameTime = j;
        if (z && this.forceFrameRedraw) {
            this.singleFrameDecoded = false;
            this.forceFrameRedraw = false;
        }
        if (this.isDice == 0 && (weakReference = this.onFinishCallback) != null && this.currentFrame >= this.finishFrame && (runnable = (Runnable) weakReference.get()) != null) {
            runnable.run();
        }
        scheduleNextGetFrame();
    }

    public static native void setLayerColor(long j, String str, int i);

    public void addParentView(ImageReceiver imageReceiver) {
        if (imageReceiver == null) {
            return;
        }
        this.parentViews.add(imageReceiver);
    }

    public void beginApplyLayerColors() {
        this.applyingLayerColors = true;
    }

    public void cacheFrame(int i) {
        if (this.rawBackgroundBitmapFrame != i || this.rawBackgroundBitmap == null) {
            if (this.rawBackgroundBitmap == null) {
                this.rawBackgroundBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
            }
            long j = this.nativePtr;
            this.rawBackgroundBitmapFrame = i;
            Bitmap bitmap = this.rawBackgroundBitmap;
            getFrame(j, i, bitmap, this.width, this.height, bitmap.getRowBytes(), true);
        }
    }

    public boolean canLoadFrames() {
        return this.precache ? this.bitmapsCache != null || this.fallbackCache : this.nativePtr != 0;
    }

    public void checkCacheCancel() {
        if (this.bitmapsCache == null || lottieCacheGenerateQueue == null || this.cacheGenerateTask == null) {
            return;
        }
        if (this.parentViews.isEmpty() && getCallback() == null) {
            View view = this.masterParent;
            if (view == null || !view.isAttachedToWindow()) {
                Runnable runnable = this.cacheGenerateTask;
                if (runnable != null) {
                    lottieCacheGenerateQueue.cancelRunnable(runnable);
                    BitmapsCache.decrementTaskCounter();
                    this.cacheGenerateTask = null;
                }
                this.generatingCache = false;
                this.genCacheSend = false;
            }
        }
    }

    public void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null) {
            lottieCacheGenerateQueue.cancelRunnable(runnable);
            BitmapsCache.decrementTaskCounter();
            this.cacheGenerateTask = null;
        }
        if (hasParentView() || this.nextRenderingBitmap == null || this.loadFrameTask == null) {
            return;
        }
        this.loadFrameTask = null;
        this.nextRenderingBitmap = null;
    }

    public void commitApplyLayerColors() {
        if (this.applyingLayerColors) {
            this.applyingLayerColors = false;
            if (!this.isRunning && this.decodeSingleFrame) {
                if (this.currentFrame <= 2) {
                    this.currentFrame = 0;
                }
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (!scheduleNextGetFrame()) {
                    this.forceFrameRedraw = true;
                }
            }
            invalidateInternal();
        }
    }

    protected void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null && this.nativePtr != 0) {
                recycleNativePtr(true);
            }
        }
        if ((this.nativePtr == 0 || this.fallbackCache) && this.secondNativePtr == 0 && this.bitmapsCache == null) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        if (this.isRunning) {
            scheduleNextGetFrame();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawInternal(canvas, null, false, 0L, 0);
    }

    public void draw(Canvas canvas, Paint paint) {
        drawInternal(canvas, paint, false, 0L, 0);
    }

    public void drawFrame(Canvas canvas, int i) {
        cacheFrame(i);
        if (this.rawBackgroundBitmap != null) {
            android.graphics.Rect rect = AndroidUtilities.rectTmp2;
            rect.set(0, 0, this.width, this.height);
            canvas.drawBitmap(this.rawBackgroundBitmap, rect, getBounds(), getPaint());
        }
    }

    public void drawInBackground(Canvas canvas, float f, float f2, float f3, float f4, int i, ColorFilter colorFilter, int i2) {
        RectF[] rectFArr = this.dstRectBackground;
        if (rectFArr[i2] == null) {
            rectFArr[i2] = new RectF();
            this.backgroundPaint[i2] = new Paint(1);
            this.backgroundPaint[i2].setFilterBitmap(true);
        }
        this.backgroundPaint[i2].setAlpha(i);
        this.backgroundPaint[i2].setColorFilter(colorFilter);
        this.dstRectBackground[i2].set(f, f2, f3 + f, f4 + f2);
        drawInternal(canvas, null, true, 0L, i2);
    }

    public void drawInternal(Canvas canvas, Paint paint, boolean z, long j, int i) {
        float width;
        float f;
        if (!canLoadFrames() || this.destroyWhenDone) {
            return;
        }
        if (!z) {
            updateCurrentFrame(j, false);
        }
        RectF rectF = z ? this.dstRectBackground[i] : this.dstRect;
        if (paint == null) {
            paint = z ? this.backgroundPaint[i] : getPaint();
        }
        if (paint.getAlpha() == 0 || this.isInvalid || this.renderingBitmap == null) {
            return;
        }
        boolean z2 = true;
        if (z) {
            width = rectF.width() / this.width;
            float height = rectF.height() / this.height;
            if (Math.abs(rectF.width() - this.width) >= AndroidUtilities.dp(1.0f) || Math.abs(rectF.height() - this.height) >= AndroidUtilities.dp(1.0f)) {
                f = height;
            } else {
                f = height;
                z2 = false;
            }
        } else {
            rectF.set(getBounds());
            if (this.applyTransformation) {
                this.scaleX = rectF.width() / this.width;
                this.scaleY = rectF.height() / this.height;
                this.applyTransformation = false;
                if (Math.abs(rectF.width() - this.width) < AndroidUtilities.dp(1.0f) && Math.abs(rectF.height() - this.height) < AndroidUtilities.dp(1.0f)) {
                    z2 = false;
                }
                this.needScale = z2;
            }
            float f2 = this.scaleX;
            f = this.scaleY;
            width = f2;
            z2 = this.needScale;
        }
        if (!z2) {
            canvas.drawBitmap(this.renderingBitmap, rectF.left, rectF.top, paint);
        } else if (this.scaleByCanvas) {
            this.srcRect.set(0, 0, this.renderingBitmap.getWidth(), this.renderingBitmap.getHeight());
            canvas.drawBitmap(this.renderingBitmap, this.srcRect, rectF, paint);
        } else {
            canvas.save();
            canvas.translate(rectF.left, rectF.top);
            canvas.scale(width, f);
            canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, paint);
            canvas.restore();
        }
        if (!this.isRunning || z) {
            return;
        }
        invalidateInternal();
    }

    protected void finalize() {
        try {
            recycle(false);
        } finally {
            super.finalize();
        }
    }

    public Bitmap getAnimatedBitmap() {
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public int getCurrentFrame() {
        return this.currentFrame;
    }

    public int getCustomEndFrame() {
        return this.customEndFrame;
    }

    public long getDuration() {
        int[] iArr = this.metaData;
        return (iArr[0] / iArr[1]) * 1000.0f;
    }

    public int getFramesCount() {
        return this.metaData[0];
    }

    @Override
    public int getIntrinsicHeight() {
        return this.height;
    }

    @Override
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override
    public int getMinimumHeight() {
        return this.height;
    }

    @Override
    public int getMinimumWidth() {
        return this.width;
    }

    @Override
    public int getNextFrame(Bitmap bitmap) {
        long j = this.generateCacheNativePtr;
        if (j == 0) {
            return -1;
        }
        int i = this.shouldLimitFps ? 2 : 1;
        if (getFrame(j, this.generateCacheFramePointer, bitmap, this.width, this.height, bitmap.getRowBytes(), true) != -5) {
            int i2 = this.generateCacheFramePointer + i;
            this.generateCacheFramePointer = i2;
            return i2 > this.metaData[0] ? 0 : 1;
        }
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getNextFrame(bitmap);
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public boolean hasBaseDice() {
        return this.nativePtr != 0 || this.loadingInBackground;
    }

    public boolean hasBitmap() {
        return (this.isRecycled || (this.renderingBitmap == null && this.nextRenderingBitmap == null) || this.isInvalid) ? false : true;
    }

    public boolean hasParentView() {
        return (this.parentViews.isEmpty() && this.masterParent == null && getCallback() == null) ? false : true;
    }

    public boolean hasVibrationPattern() {
        return this.vibrationPattern != null;
    }

    public void invalidateInternal() {
        if (this.isRecycled) {
            return;
        }
        int size = this.parentViews.size();
        for (int i = 0; i < size; i++) {
            ((ImageReceiver) this.parentViews.get(i)).invalidate();
        }
        View view = this.masterParent;
        if (view != null) {
            view.invalidate();
        }
        if (getCallback() != null) {
            invalidateSelf();
        }
    }

    public boolean isGeneratingCache() {
        return this.cacheGenerateTask != null;
    }

    public boolean isHeavyDrawable() {
        return this.isDice == 0;
    }

    public boolean isLastFrame() {
        return this.currentFrame == getFramesCount() - 1;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    public void multiplySpeed(float f) {
        this.timeBetweenFrames = (int) (this.timeBetweenFrames * (1.0f / f));
    }

    @Override
    protected void onBoundsChange(android.graphics.Rect rect) {
        super.onBoundsChange(rect);
        this.applyTransformation = true;
    }

    @Override
    public void prepareForGenerateCache() {
        File file;
        String file2 = this.args.file.toString();
        NativePtrArgs nativePtrArgs = this.args;
        long create = create(file2, nativePtrArgs.json, this.width, this.height, this.createdForFirstFrame ? this.metaData : new int[3], false, nativePtrArgs.colorReplacement, false, nativePtrArgs.fitzModifier);
        this.generateCacheNativePtr = create;
        if (create != 0 || (file = this.file) == null) {
            return;
        }
        file.delete();
    }

    public void recycle(boolean z) {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
            return;
        }
        if (this.loadFrameTask != null || this.cacheGenerateTask != null || this.generatingCache) {
            this.destroyWhenDone = true;
            return;
        }
        recycleNativePtr(z);
        BitmapsCache bitmapsCache = this.bitmapsCache;
        if (bitmapsCache != null) {
            bitmapsCache.recycle();
            this.bitmapsCache = null;
        }
        recycleResources();
    }

    public void recycleResources() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.renderingBitmap);
        arrayList.add(this.backgroundBitmap);
        arrayList.add(this.nextRenderingBitmap);
        this.nextRenderingBitmap = null;
        this.renderingBitmap = null;
        this.backgroundBitmap = null;
        AndroidUtilities.recycleBitmaps(arrayList);
        if (this.onAnimationEndListener != null) {
            this.onAnimationEndListener = null;
        }
    }

    @Override
    public void releaseForGenerateCache() {
        long j = this.generateCacheNativePtr;
        if (j != 0) {
            destroy(j);
            this.generateCacheNativePtr = 0L;
        }
    }

    public void removeParentView(ImageReceiver imageReceiver) {
        if (imageReceiver == null) {
            return;
        }
        this.parentViews.remove(imageReceiver);
        checkCacheCancel();
    }

    public void replaceColors(int[] iArr) {
        this.newReplaceColors = iArr;
        requestRedrawColors();
    }

    public void resetVibrationAfterRestart(boolean z) {
        this.resetVibrationAfterRestart = z;
    }

    public boolean restart() {
        return restart(false);
    }

    public boolean restart(boolean z) {
        if (!z && ((this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) && this.autoRepeatCount < 0)) {
            return false;
        }
        this.autoRepeatPlayCount = 0;
        this.autoRepeat = 2;
        start();
        return true;
    }

    public boolean scheduleNextGetFrame() {
        boolean z;
        if (this.loadFrameTask != null || this.nextRenderingBitmap != null || !canLoadFrames() || this.loadingInBackground || this.destroyWhenDone || (!this.isRunning && (!(z = this.decodeSingleFrame) || (z && this.singleFrameDecoded)))) {
            return false;
        }
        if (this.generatingCache && !this.allowDrawFramesWhileCacheGenerating) {
            return false;
        }
        if (!this.newColorUpdates.isEmpty()) {
            this.pendingColorUpdates.putAll(this.newColorUpdates);
            this.newColorUpdates.clear();
        }
        int[] iArr = this.newReplaceColors;
        if (iArr != null) {
            this.pendingReplaceColors = iArr;
            this.newReplaceColors = null;
        }
        this.loadFrameTask = this.loadFrameRunnable;
        if (this.shouldLimitFps && Thread.currentThread() == ApplicationLoader.applicationHandler.getLooper().getThread()) {
            DispatchQueuePoolBackground.execute(this.loadFrameTask, this.frameWaitSync != null);
        } else {
            loadFrameRunnableQueue.lambda$execute$0(this.loadFrameTask);
        }
        return true;
    }

    public void setAllowDecodeSingleFrame(boolean z) {
        this.decodeSingleFrame = z;
        if (z) {
            scheduleNextGetFrame();
        }
    }

    public void setAllowDrawFramesWhileCacheGenerating(boolean z) {
        this.allowDrawFramesWhileCacheGenerating = z;
    }

    public void setAllowVibration(boolean z) {
        this.allowVibration = z;
    }

    public void setAutoRepeat(int i) {
        if (this.autoRepeat == 2 && i == 3 && this.currentFrame != 0) {
            return;
        }
        this.autoRepeat = i;
    }

    public void setAutoRepeatCount(int i) {
        this.autoRepeatCount = i;
    }

    public void setAutoRepeatTimeout(long j) {
        this.autoRepeatTimeout = j;
    }

    public boolean setBaseDice(File file) {
        if (this.nativePtr == 0 && !this.loadingInBackground) {
            final String readRes = AndroidUtilities.readRes(file);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            this.loadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    RLottieDrawable.this.lambda$setBaseDice$3(readRes);
                }
            });
        }
        return true;
    }

    public void setCurrentFrame(int i) {
        setCurrentFrame(i, true);
    }

    public void setCurrentFrame(int i, boolean z) {
        setCurrentFrame(i, z, false);
    }

    public void setCurrentFrame(int i, boolean z, boolean z2) {
        if (i < 0 || i > this.metaData[0]) {
            return;
        }
        if (this.currentFrame != i || z2) {
            this.currentFrame = i;
            this.nextFrameIsLast = false;
            this.singleFrameDecoded = false;
            if (this.invalidateOnProgressSet) {
                this.isInvalid = true;
                if (this.loadFrameTask != null) {
                    this.doNotRemoveInvalidOnFrameReady = true;
                }
            }
            if ((!z || z2) && this.waitingForNextTask && this.nextRenderingBitmap != null) {
                this.backgroundBitmap = this.nextRenderingBitmap;
                this.nextRenderingBitmap = null;
                this.loadFrameTask = null;
                this.waitingForNextTask = false;
            }
            if (!z && this.loadFrameTask == null) {
                this.frameWaitSync = new CountDownLatch(1);
            }
            if (z2 && !this.isRunning) {
                this.isRunning = true;
            }
            if (!scheduleNextGetFrame()) {
                this.forceFrameRedraw = true;
            } else if (!z) {
                try {
                    this.frameWaitSync.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.frameWaitSync = null;
            }
            invalidateSelf();
        }
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    public boolean setCustomEndFrame(int i) {
        if (this.customEndFrame == i || i > this.metaData[0]) {
            return false;
        }
        this.customEndFrame = i;
        return true;
    }

    public boolean setDiceNumber(File file, boolean z) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            final String readRes = AndroidUtilities.readRes(file);
            if (TextUtils.isEmpty(readRes)) {
                return false;
            }
            if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
                this.isDice = 2;
                this.setLastFrame = true;
            }
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    RLottieDrawable.this.lambda$setDiceNumber$6(readRes);
                }
            });
        }
        return true;
    }

    public void setGeneratingFrame(int i) {
        this.generateCacheFramePointer = i;
    }

    public void setInvalidateOnProgressSet(boolean z) {
        this.invalidateOnProgressSet = z;
    }

    public void setLayerColor(String str, int i) {
        this.newColorUpdates.put(str, Integer.valueOf(i));
        requestRedrawColors();
    }

    public void setMasterParent(View view) {
        this.masterParent = view;
    }

    public void setOnAnimationEndListener(Runnable runnable) {
        this.onAnimationEndListener = runnable;
    }

    public void setOnFinishCallback(Runnable runnable, int i) {
        if (runnable != null) {
            this.onFinishCallback = new WeakReference(runnable);
            this.finishFrame = i;
        } else if (this.onFinishCallback != null) {
            this.onFinishCallback = null;
        }
    }

    public void setPlayInDirectionOfCustomEndFrame(boolean z) {
        this.playInDirectionOfCustomEndFrame = z;
    }

    public void setProgress(float f) {
        setProgress(f, true);
    }

    public void setProgress(float f, boolean z) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        setCurrentFrame((int) (this.metaData[0] * f), z);
    }

    public void setProgressMs(long j) {
        setCurrentFrame((int) ((Math.max(0L, j) / this.timeBetweenFrames) % this.metaData[0]), true, true);
    }

    public void setVibrationPattern(HashMap hashMap) {
        this.vibrationPattern = hashMap;
    }

    @Override
    public void start() {
        if (this.isRunning) {
            return;
        }
        if ((this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) && this.customEndFrame != this.currentFrame) {
            this.isRunning = true;
            if (this.invalidateOnProgressSet) {
                this.isInvalid = true;
                if (this.loadFrameTask != null) {
                    this.doNotRemoveInvalidOnFrameReady = true;
                }
            }
            scheduleNextGetFrame();
            invalidateInternal();
        }
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    public void updateCurrentFrame(long j, boolean z) {
        int i;
        long j2;
        boolean z2;
        Integer num;
        if (j == 0) {
            j = System.currentTimeMillis();
        }
        long j3 = j;
        long j4 = j3 - this.lastFrameTime;
        if (!z || this.shouldLimitFps) {
            float f = AndroidUtilities.screenRefreshRate;
            i = (f <= 60.0f || (z && f <= 80.0f)) ? this.timeBetweenFrames - 6 : this.timeBetweenFrames;
        } else {
            i = this.timeBetweenFrames - 16;
        }
        if (this.isRunning) {
            if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame();
                return;
            }
            if (this.nextRenderingBitmap == null) {
                return;
            }
            if (this.renderingBitmap != null && (j4 < i || this.skipFrameUpdate)) {
                return;
            }
            HashMap hashMap = this.vibrationPattern;
            if (hashMap != null && this.currentParentView != null && this.allowVibration && (num = (Integer) hashMap.get(Integer.valueOf(this.currentFrame - 1))) != null) {
                try {
                    this.currentParentView.performHapticFeedback(num.intValue() == 1 ? 0 : 3, 2);
                } catch (Exception unused) {
                }
            }
            j2 = i;
            z2 = false;
        } else {
            if ((!this.forceFrameRedraw && (!this.decodeSingleFrame || j4 < i)) || this.nextRenderingBitmap == null) {
                return;
            }
            j2 = i;
            z2 = true;
        }
        setCurrentFrame(j3, j4, j2, z2);
    }
}
