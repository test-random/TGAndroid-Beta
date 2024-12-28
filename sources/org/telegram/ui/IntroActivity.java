package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.Intro;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.IntroActivity;

public class IntroActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private BottomPagesView bottomPages;
    private long currentDate;
    private int currentViewPagerPage;
    private RLottieDrawable darkThemeDrawable;
    private boolean destroyed;
    private boolean dragging;
    private EGLThread eglThread;
    private FrameLayout frameContainerView;
    private FrameLayout frameLayout2;
    private boolean isOnLogout;
    private boolean justEndDragging;
    private LocaleController.LocaleInfo localeInfo;
    private String[] messages;
    private int startDragX;
    private TextView startMessagingButton;
    private TextView switchLanguageTextView;
    private String[] titles;
    private ViewPager viewPager;
    private final Object pagerHeaderTag = new Object();
    private final Object pagerMessageTag = new Object();
    private int currentAccount = UserConfig.selectedAccount;
    private int lastPage = 0;
    private boolean justCreated = false;
    private boolean startPressed = false;

    public class AnonymousClass2 implements TextureView.SurfaceTextureListener {
        AnonymousClass2() {
        }

        public void lambda$onSurfaceTextureAvailable$0() {
            float currentTimeMillis = ((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f;
            Intro.setPage(IntroActivity.this.currentViewPagerPage);
            Intro.setDate(currentTimeMillis);
            Intro.onDrawFrame(0);
            if (IntroActivity.this.eglThread == null || !IntroActivity.this.eglThread.isAlive() || IntroActivity.this.eglThread.eglDisplay == null || IntroActivity.this.eglThread.eglSurface == null) {
                return;
            }
            try {
                IntroActivity.this.eglThread.egl10.eglSwapBuffers(IntroActivity.this.eglThread.eglDisplay, IntroActivity.this.eglThread.eglSurface);
            } catch (Exception unused) {
            }
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (IntroActivity.this.eglThread != null || surfaceTexture == null) {
                return;
            }
            IntroActivity.this.eglThread = new EGLThread(surfaceTexture);
            IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
            IntroActivity.this.eglThread.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    IntroActivity.AnonymousClass2.this.lambda$onSurfaceTextureAvailable$0();
                }
            });
            IntroActivity.this.eglThread.postRunnable(IntroActivity.this.eglThread.drawRunnable);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (IntroActivity.this.eglThread == null) {
                return true;
            }
            IntroActivity.this.eglThread.shutdown();
            IntroActivity.this.eglThread = null;
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            if (IntroActivity.this.eglThread != null) {
                IntroActivity.this.eglThread.setSurfaceTextureSize(i, i2);
            }
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    public class AnonymousClass5 implements NotificationCenter.NotificationCenterDelegate {
        final AlertDialog val$loaderDialog;

        AnonymousClass5(AlertDialog alertDialog) {
            this.val$loaderDialog = alertDialog;
        }

        public void lambda$didReceivedNotification$0() {
            IntroActivity.this.presentFragment(new LoginActivity().setIntroView(IntroActivity.this.frameContainerView, IntroActivity.this.startMessagingButton), true);
            IntroActivity.this.destroyed = true;
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.reloadInterface) {
                this.val$loaderDialog.dismiss();
                NotificationCenter.getGlobalInstance().removeObserver(this, i);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        IntroActivity.AnonymousClass5.this.lambda$didReceivedNotification$0();
                    }
                }, 100L);
            }
        }
    }

    public class EGLThread extends DispatchQueue {
        private Runnable drawRunnable;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initied;
        private long lastDrawFrame;
        private float maxRefreshRate;
        private SurfaceTexture surfaceTexture;
        private GenericProvider telegramMaskProvider;
        private int[] textures;

        public EGLThread(SurfaceTexture surfaceTexture) {
            super("EGLThread");
            this.textures = new int[24];
            this.telegramMaskProvider = new GenericProvider() {
                @Override
                public final Object provide(Object obj) {
                    Bitmap lambda$new$0;
                    lambda$new$0 = IntroActivity.EGLThread.lambda$new$0((Void) obj);
                    return lambda$new$0;
                }
            };
            this.drawRunnable = new Runnable() {
                @Override
                public void run() {
                    EGLThread eGLThread;
                    float[] supportedRefreshRates;
                    if (EGLThread.this.initied) {
                        long currentTimeMillis = System.currentTimeMillis();
                        if ((!EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) || !EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) && !EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                                return;
                            }
                            return;
                        }
                        int min = (int) Math.min(currentTimeMillis - EGLThread.this.lastDrawFrame, 16L);
                        float f = ((float) (currentTimeMillis - IntroActivity.this.currentDate)) / 1000.0f;
                        Intro.setPage(IntroActivity.this.currentViewPagerPage);
                        Intro.setDate(f);
                        Intro.onDrawFrame(min);
                        EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                        EGLThread.this.lastDrawFrame = currentTimeMillis;
                        float f2 = 0.0f;
                        if (EGLThread.this.maxRefreshRate == 0.0f) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                supportedRefreshRates = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getSupportedRefreshRates();
                                for (float f3 : supportedRefreshRates) {
                                    if (f3 > f2) {
                                        f2 = f3;
                                    }
                                }
                                eGLThread = EGLThread.this;
                            } else {
                                eGLThread = EGLThread.this;
                                f2 = 60.0f;
                            }
                            eGLThread.maxRefreshRate = f2;
                        }
                        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                        EGLThread eGLThread2 = EGLThread.this;
                        eGLThread2.postRunnable(eGLThread2.drawRunnable, Math.max((1000.0f / EGLThread.this.maxRefreshRate) - currentTimeMillis2, 0L));
                    }
                }
            };
            this.surfaceTexture = surfaceTexture;
        }

        private boolean initGL() {
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eglGetDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, EmuDetector.with(IntroActivity.this.getParentActivity()).detect() ? new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12344} : new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344}, eGLConfigArr, 1, iArr)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (iArr[0] <= 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
            EGLConfig eGLConfig = eGLConfigArr[0];
            this.eglConfig = eGLConfig;
            EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
            this.eglContext = eglCreateContext;
            if (eglCreateContext == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            SurfaceTexture surfaceTexture = this.surfaceTexture;
            if (!(surfaceTexture instanceof SurfaceTexture)) {
                finish();
                return false;
            }
            EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture, null);
            this.eglSurface = eglCreateWindowSurface;
            if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            GLES20.glGenTextures(23, this.textures, 0);
            loadTexture(R.drawable.intro_fast_arrow_shadow, 0);
            loadTexture(R.drawable.intro_fast_arrow, 1);
            loadTexture(R.drawable.intro_fast_body, 2);
            loadTexture(R.drawable.intro_fast_spiral, 3);
            loadTexture(R.drawable.intro_ic_bubble_dot, 4);
            loadTexture(R.drawable.intro_ic_bubble, 5);
            loadTexture(R.drawable.intro_ic_cam_lens, 6);
            loadTexture(R.drawable.intro_ic_cam, 7);
            loadTexture(R.drawable.intro_ic_pencil, 8);
            loadTexture(R.drawable.intro_ic_pin, 9);
            loadTexture(R.drawable.intro_ic_smile_eye, 10);
            loadTexture(R.drawable.intro_ic_smile, 11);
            loadTexture(R.drawable.intro_ic_videocam, 12);
            loadTexture(R.drawable.intro_knot_down, 13);
            loadTexture(R.drawable.intro_knot_up, 14);
            loadTexture(R.drawable.intro_powerful_infinity_white, 15);
            loadTexture(R.drawable.intro_powerful_infinity, 16);
            loadTexture(R.drawable.intro_powerful_mask, 17, Theme.getColor(Theme.key_windowBackgroundWhite), false);
            loadTexture(R.drawable.intro_powerful_star, 18);
            loadTexture(R.drawable.intro_private_door, 19);
            loadTexture(R.drawable.intro_private_screw, 20);
            loadTexture(R.drawable.intro_tg_plane, 21);
            loadTexture(new GenericProvider() {
                @Override
                public final Object provide(Object obj) {
                    Bitmap lambda$initGL$1;
                    lambda$initGL$1 = IntroActivity.EGLThread.lambda$initGL$1((Void) obj);
                    return lambda$initGL$1;
                }
            }, 22);
            loadTexture(this.telegramMaskProvider, 23);
            updateTelegramTextures();
            updatePowerfulTextures();
            int[] iArr2 = this.textures;
            Intro.setPrivateTextures(iArr2[19], iArr2[20]);
            int[] iArr3 = this.textures;
            Intro.setFreeTextures(iArr3[14], iArr3[13]);
            int[] iArr4 = this.textures;
            Intro.setFastTextures(iArr4[2], iArr4[3], iArr4[1], iArr4[0]);
            int[] iArr5 = this.textures;
            Intro.setIcTextures(iArr5[4], iArr5[5], iArr5[6], iArr5[7], iArr5[8], iArr5[9], iArr5[10], iArr5[11], iArr5[12]);
            Intro.onSurfaceCreated();
            IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
            return true;
        }

        public static Bitmap lambda$initGL$1(Void r4) {
            Paint paint = new Paint(1);
            paint.setColor(-13851168);
            int dp = AndroidUtilities.dp(150.0f);
            Bitmap createBitmap = Bitmap.createBitmap(dp, dp, Bitmap.Config.ARGB_8888);
            float f = dp / 2.0f;
            new Canvas(createBitmap).drawCircle(f, f, f, paint);
            return createBitmap;
        }

        public static Bitmap lambda$new$0(Void r6) {
            int dp = AndroidUtilities.dp(150.0f);
            Bitmap createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), dp, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            Paint paint = new Paint(1);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawCircle(createBitmap.getWidth() / 2.0f, createBitmap.getHeight() / 2.0f, dp / 2.0f, paint);
            return createBitmap;
        }

        public void lambda$shutdown$2() {
            finish();
            Looper myLooper = Looper.myLooper();
            if (myLooper != null) {
                myLooper.quit();
            }
        }

        private void loadTexture(int i, int i2) {
            loadTexture(i, i2, 0, false);
        }

        public void loadTexture(int i, int i2, int i3, boolean z) {
            Drawable drawable = IntroActivity.this.getParentActivity().getResources().getDrawable(i);
            if (drawable instanceof BitmapDrawable) {
                if (z) {
                    GLES20.glDeleteTextures(1, this.textures, i2);
                    GLES20.glGenTextures(1, this.textures, i2);
                }
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                GLES20.glBindTexture(3553, this.textures[i2]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                if (i3 == 0) {
                    GLUtils.texImage2D(3553, 0, bitmap, 0);
                    return;
                }
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint(5);
                paint.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                GLUtils.texImage2D(3553, 0, createBitmap, 0);
                createBitmap.recycle();
            }
        }

        private void loadTexture(GenericProvider genericProvider, int i) {
            loadTexture(genericProvider, i, false);
        }

        public void loadTexture(GenericProvider genericProvider, int i, boolean z) {
            if (z) {
                GLES20.glDeleteTextures(1, this.textures, i);
                GLES20.glGenTextures(1, this.textures, i);
            }
            Bitmap bitmap = (Bitmap) genericProvider.provide(null);
            GLES20.glBindTexture(3553, this.textures[i]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, bitmap, 0);
            bitmap.recycle();
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl10 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay2 = this.eglDisplay;
            if (eGLDisplay2 != null) {
                this.egl10.eglTerminate(eGLDisplay2);
                this.eglDisplay = null;
            }
        }

        @Override
        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void setSurfaceTextureSize(int i, int i2) {
            Intro.onSurfaceChanged(i, i2, Math.min(i / 150.0f, i2 / 150.0f), 0);
        }

        public void shutdown() {
            postRunnable(new Runnable() {
                @Override
                public final void run() {
                    IntroActivity.EGLThread.this.lambda$shutdown$2();
                }
            });
        }

        public void updatePowerfulTextures() {
            int[] iArr = this.textures;
            Intro.setPowerfulTextures(iArr[17], iArr[18], iArr[16], iArr[15]);
        }

        public void updateTelegramTextures() {
            int[] iArr = this.textures;
            Intro.setTelegramTextures(iArr[22], iArr[21], iArr[23]);
        }
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override
        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            final TextView textView = new TextView(viewGroup.getContext());
            textView.setTag(IntroActivity.this.pagerHeaderTag);
            final TextView textView2 = new TextView(viewGroup.getContext());
            textView2.setTag(IntroActivity.this.pagerMessageTag);
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext()) {
                @Override
                protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                    int dp = (((((i5 - i3) / 4) * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(16.0f);
                    int dp2 = AndroidUtilities.dp(18.0f);
                    TextView textView3 = textView;
                    textView3.layout(dp2, dp, textView3.getMeasuredWidth() + dp2, textView.getMeasuredHeight() + dp);
                    int textSize = ((int) (dp + textView.getTextSize())) + AndroidUtilities.dp(16.0f);
                    int dp3 = AndroidUtilities.dp(16.0f);
                    TextView textView4 = textView2;
                    textView4.layout(dp3, textSize, textView4.getMeasuredWidth() + dp3, textView2.getMeasuredHeight() + textSize);
                }
            };
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView.setTextSize(1, 26.0f);
            textView.setGravity(17);
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            textView2.setTextSize(1, 15.0f);
            textView2.setGravity(17);
            frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 286.0f, 16.0f, 0.0f));
            viewGroup.addView(frameLayout, 0);
            textView.setText(IntroActivity.this.titles[i]);
            textView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[i]));
            return frameLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }

        @Override
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
            IntroActivity.this.bottomPages.setCurrentPage(i);
            IntroActivity.this.currentViewPagerPage = i;
        }
    }

    private void checkContinueText() {
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        final String str = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
        if ((str == null || (str.equals("en") && LocaleController.getInstance().getSystemDefaultLocale().getLanguage() != null && !LocaleController.getInstance().getSystemDefaultLocale().getLanguage().equals("en"))) && (str = LocaleController.getInstance().getSystemDefaultLocale().getLanguage()) == null) {
            str = "en";
        }
        String str2 = str.contains("-") ? str.split("-")[0] : str;
        String localeAlias = LocaleController.getLocaleAlias(str2);
        LocaleController.LocaleInfo localeInfo = null;
        LocaleController.LocaleInfo localeInfo2 = null;
        for (int i = 0; i < LocaleController.getInstance().languages.size(); i++) {
            LocaleController.LocaleInfo localeInfo3 = LocaleController.getInstance().languages.get(i);
            if (localeInfo3.shortName.equals("en")) {
                localeInfo = localeInfo3;
            }
            if (localeInfo3.shortName.replace("_", "-").equals(str) || localeInfo3.shortName.equals(str2) || localeInfo3.shortName.equals(localeAlias)) {
                localeInfo2 = localeInfo3;
            }
            if (localeInfo != null && localeInfo2 != null) {
                break;
            }
        }
        if (localeInfo == null || localeInfo2 == null || localeInfo == localeInfo2) {
            return;
        }
        TLRPC.TL_langpack_getStrings tL_langpack_getStrings = new TLRPC.TL_langpack_getStrings();
        if (localeInfo2 != currentLocaleInfo) {
            tL_langpack_getStrings.lang_code = localeInfo2.getLangCode();
            this.localeInfo = localeInfo2;
        } else {
            tL_langpack_getStrings.lang_code = localeInfo.getLangCode();
            this.localeInfo = localeInfo;
        }
        tL_langpack_getStrings.keys.add("ContinueOnThisLanguage");
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                IntroActivity.this.lambda$checkContinueText$4(str, tLObject, tL_error);
            }
        }, 8);
    }

    public void lambda$checkContinueText$3(TLRPC.LangPackString langPackString, String str) {
        if (this.destroyed) {
            return;
        }
        this.switchLanguageTextView.setText(langPackString.value);
        MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str.toLowerCase()).apply();
    }

    public void lambda$checkContinueText$4(final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            if (vector.objects.isEmpty()) {
                return;
            }
            final TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(0);
            if (langPackString instanceof TLRPC.TL_langPackString) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        IntroActivity.this.lambda$checkContinueText$3(langPackString, str);
                    }
                });
            }
        }
    }

    public void lambda$createView$0(RLottieImageView rLottieImageView, View view) {
        if (DrawerProfileCell.switchingTheme) {
            return;
        }
        DrawerProfileCell.switchingTheme = true;
        boolean z = !Theme.isCurrentThemeDark();
        Theme.ThemeInfo theme = Theme.getTheme(z ? "Night" : "Blue");
        Theme.selectedAutoNightType = 0;
        Theme.saveAutoNightThemeConfig();
        Theme.cancelAutoNightThemeCallbacks();
        RLottieDrawable rLottieDrawable = this.darkThemeDrawable;
        rLottieDrawable.setCustomEndFrame(z ? rLottieDrawable.getFramesCount() - 1 : 0);
        rLottieImageView.playAnimation();
        rLottieImageView.getLocationInWindow(r4);
        int[] iArr = {iArr[0] + (rLottieImageView.getMeasuredWidth() / 2), iArr[1] + (rLottieImageView.getMeasuredHeight() / 2)};
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, theme, Boolean.FALSE, iArr, -1, Boolean.valueOf(z), rLottieImageView);
        rLottieImageView.setContentDescription(LocaleController.getString(z ? R.string.AccDescrSwitchToDayTheme : R.string.AccDescrSwitchToNightTheme));
    }

    public void lambda$createView$1(View view) {
        if (this.startPressed) {
            return;
        }
        this.startPressed = true;
        presentFragment(new LoginActivity().setIntroView(this.frameContainerView, this.startMessagingButton), true);
        this.destroyed = true;
    }

    public void lambda$createView$2(View view) {
        if (this.startPressed || this.localeInfo == null) {
            return;
        }
        this.startPressed = true;
        AlertDialog alertDialog = new AlertDialog(view.getContext(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.showDelayed(1000L);
        NotificationCenter.getGlobalInstance().addObserver(new AnonymousClass5(alertDialog), NotificationCenter.reloadInterface);
        LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
    }

    public void lambda$getThemeDescriptions$5() {
        updateColors(true);
    }

    public void lambda$updateColors$6() {
        EGLThread eGLThread = this.eglThread;
        int i = R.drawable.intro_powerful_mask;
        int i2 = Theme.key_windowBackgroundWhite;
        eGLThread.loadTexture(i, 17, Theme.getColor(i2), true);
        this.eglThread.updatePowerfulTextures();
        EGLThread eGLThread2 = this.eglThread;
        eGLThread2.loadTexture(eGLThread2.telegramMaskProvider, 23, true);
        this.eglThread.updateTelegramTextures();
        Intro.setBackgroundColor(Theme.getColor(i2));
    }

    private void updateColors(boolean z) {
        View view = this.fragmentView;
        int i = Theme.key_windowBackgroundWhite;
        view.setBackgroundColor(Theme.getColor(i));
        this.switchLanguageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.startMessagingButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        TextView textView = this.startMessagingButton;
        int dp = AndroidUtilities.dp(6.0f);
        int i2 = Theme.key_changephoneinfo_image2;
        textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(dp, Theme.getColor(i2), Theme.getColor(Theme.key_chats_actionPressedBackground)));
        this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), PorterDuff.Mode.SRC_IN));
        this.bottomPages.invalidate();
        if (!z) {
            Intro.setBackgroundColor(Theme.getColor(i));
            return;
        }
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    IntroActivity.this.lambda$updateColors$6();
                }
            });
        }
        for (int i3 = 0; i3 < this.viewPager.getChildCount(); i3++) {
            View childAt = this.viewPager.getChildAt(i3);
            ((TextView) childAt.findViewWithTag(this.pagerHeaderTag)).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            ((TextView) childAt.findViewWithTag(this.pagerMessageTag)).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        }
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setAddToContainer(false);
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        final RLottieImageView rLottieImageView = new RLottieImageView(context);
        final FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(28, 28, 17));
        final int i = 4;
        FrameLayout frameLayout2 = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                int i6 = (i5 - i3) / 4;
                int i7 = i6 * 3;
                int dp = (i7 - AndroidUtilities.dp(275.0f)) / 2;
                IntroActivity.this.frameLayout2.layout(0, dp, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + dp);
                int dp2 = dp + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(122.0f);
                int measuredWidth = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(measuredWidth, dp2, IntroActivity.this.bottomPages.getMeasuredWidth() + measuredWidth, IntroActivity.this.bottomPages.getMeasuredHeight() + dp2);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                int measuredHeight = i7 + ((i6 - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2);
                int measuredWidth2 = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(measuredWidth2, measuredHeight, IntroActivity.this.startMessagingButton.getMeasuredWidth() + measuredWidth2, IntroActivity.this.startMessagingButton.getMeasuredHeight() + measuredHeight);
                int dp3 = measuredHeight - AndroidUtilities.dp(30.0f);
                int measuredWidth3 = (getMeasuredWidth() - IntroActivity.this.switchLanguageTextView.getMeasuredWidth()) / 2;
                IntroActivity.this.switchLanguageTextView.layout(measuredWidth3, dp3 - IntroActivity.this.switchLanguageTextView.getMeasuredHeight(), IntroActivity.this.switchLanguageTextView.getMeasuredWidth() + measuredWidth3, dp3);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
                int dp4 = AndroidUtilities.dp(i) + (AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight);
                if (marginLayoutParams.topMargin != dp4) {
                    marginLayoutParams.topMargin = dp4;
                    frameLayout.requestLayout();
                }
            }
        };
        this.frameContainerView = frameLayout2;
        scrollView.addView(frameLayout2, LayoutHelper.createScroll(-1, -2, 51));
        int i2 = R.raw.sun;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, String.valueOf(i2), AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
        this.darkThemeDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        this.darkThemeDrawable.beginApplyLayerColors();
        this.darkThemeDrawable.commitApplyLayerColors();
        this.darkThemeDrawable.setCustomEndFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0);
        this.darkThemeDrawable.setCurrentFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0, false);
        rLottieImageView.setContentDescription(LocaleController.getString(Theme.getCurrentTheme().isDark() ? R.string.AccDescrSwitchToDayTheme : R.string.AccDescrSwitchToNightTheme));
        rLottieImageView.setAnimation(this.darkThemeDrawable);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                IntroActivity.this.lambda$createView$0(rLottieImageView, view);
            }
        });
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.frameLayout2 = frameLayout3;
        this.frameContainerView.addView(frameLayout3, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(context);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new AnonymousClass2());
        ViewPager viewPager = new ViewPager(context);
        this.viewPager = viewPager;
        viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.frameContainerView.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i3) {
                if (i3 == 1) {
                    IntroActivity.this.dragging = true;
                    IntroActivity introActivity = IntroActivity.this;
                    introActivity.startDragX = introActivity.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
                } else if (i3 == 0 || i3 == 2) {
                    if (IntroActivity.this.dragging) {
                        IntroActivity.this.justEndDragging = true;
                        IntroActivity.this.dragging = false;
                    }
                    if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                        IntroActivity introActivity2 = IntroActivity.this;
                        introActivity2.lastPage = introActivity2.viewPager.getCurrentItem();
                    }
                }
            }

            @Override
            public void onPageScrolled(int i3, float f, int i4) {
                IntroActivity.this.bottomPages.setPageOffset(i3, f);
                float measuredWidth = IntroActivity.this.viewPager.getMeasuredWidth();
                if (measuredWidth == 0.0f) {
                    return;
                }
                Intro.setScrollOffset((((i3 * measuredWidth) + i4) - (IntroActivity.this.currentViewPagerPage * measuredWidth)) / measuredWidth);
            }

            @Override
            public void onPageSelected(int i3) {
                IntroActivity.this.currentViewPagerPage = i3;
            }
        });
        TextView textView = new TextView(context) {
            CellFlickerDrawable cellFlickerDrawable;

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (this.cellFlickerDrawable == null) {
                    CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
                    this.cellFlickerDrawable = cellFlickerDrawable;
                    cellFlickerDrawable.drawFrame = false;
                    cellFlickerDrawable.repeatProgress = 2.0f;
                }
                this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.cellFlickerDrawable.draw(canvas, rectF, AndroidUtilities.dp(4.0f), null);
                invalidate();
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                if (View.MeasureSpec.getSize(i3) > AndroidUtilities.dp(260.0f)) {
                    i3 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), 1073741824);
                }
                super.onMeasure(i3, i4);
            }
        };
        this.startMessagingButton = textView;
        textView.setText(LocaleController.getString(R.string.StartMessaging));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTypeface(AndroidUtilities.bold());
        this.startMessagingButton.setTextSize(1, 15.0f);
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.frameContainerView.addView(this.startMessagingButton, LayoutHelper.createFrame(-1, 50.0f, 81, 16.0f, 0.0f, 16.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                IntroActivity.this.lambda$createView$1(view);
            }
        });
        BottomPagesView bottomPagesView = new BottomPagesView(context, this.viewPager, 6);
        this.bottomPages = bottomPagesView;
        this.frameContainerView.addView(bottomPagesView, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.switchLanguageTextView = textView2;
        textView2.setGravity(17);
        this.switchLanguageTextView.setTextSize(1, 16.0f);
        this.frameContainerView.addView(this.switchLanguageTextView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.switchLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                IntroActivity.this.lambda$createView$2(view);
            }
        });
        float f = 4;
        this.frameContainerView.addView(frameLayout, LayoutHelper.createFrame(64, 64.0f, 53, 0.0f, f, f, 0.0f));
        this.fragmentView = scrollView;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.configLoaded);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        checkContinueText();
        this.justCreated = true;
        updateColors(false);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack || i == NotificationCenter.configLoaded) {
            checkContinueText();
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                IntroActivity.this.lambda$getThemeDescriptions$5();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhiteBlueText4, Theme.key_chats_actionBackground, Theme.key_chats_actionPressedBackground, Theme.key_featuredStickers_buttonText, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText3);
    }

    @Override
    public boolean hasForceLightStatusBar() {
        return true;
    }

    @Override
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) > 0.699999988079071d;
    }

    @Override
    public AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        if (!this.isOnLogout) {
            return null;
        }
        AnimatorSet duration = new AnimatorSet().setDuration(50L);
        duration.playTogether(ValueAnimator.ofFloat(new float[0]));
        return duration;
    }

    @Override
    public boolean onFragmentCreate() {
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).apply();
        this.titles = new String[]{LocaleController.getString(R.string.Page1Title), LocaleController.getString(R.string.Page2Title), LocaleController.getString(R.string.Page3Title), LocaleController.getString(R.string.Page5Title), LocaleController.getString(R.string.Page4Title), LocaleController.getString(R.string.Page6Title)};
        this.messages = new String[]{LocaleController.getString(R.string.Page1Message), LocaleController.getString(R.string.Page2Message), LocaleController.getString(R.string.Page3Message), LocaleController.getString(R.string.Page5Message), LocaleController.getString(R.string.Page4Message), LocaleController.getString(R.string.Page6Message)};
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.configLoaded);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0L).apply();
    }

    @Override
    public void onPause() {
        Activity parentActivity;
        super.onPause();
        if (AndroidUtilities.isTablet() || (parentActivity = getParentActivity()) == null) {
            return;
        }
        parentActivity.setRequestedOrientation(-1);
    }

    @Override
    public void onResume() {
        Activity parentActivity;
        super.onResume();
        if (this.justCreated) {
            if (LocaleController.isRTL) {
                this.viewPager.setCurrentItem(6);
                this.lastPage = 6;
            } else {
                this.viewPager.setCurrentItem(0);
                this.lastPage = 0;
            }
            this.justCreated = false;
        }
        if (AndroidUtilities.isTablet() || (parentActivity = getParentActivity()) == null) {
            return;
        }
        parentActivity.setRequestedOrientation(1);
    }

    public IntroActivity setOnLogout() {
        this.isOnLogout = true;
        return this;
    }
}
