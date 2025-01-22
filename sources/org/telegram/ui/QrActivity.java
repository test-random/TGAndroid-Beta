package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.ThemeSmallPreviewView;
import org.telegram.ui.QrActivity;

public class QrActivity extends BaseFragment {
    private static List cachedThemes;
    private static boolean firstOpen;
    private static final ArrayMap qrColorsMap;
    private BackupImageView avatarImageView;
    private View backgroundView;
    private long chatId;
    private ImageView closeImageView;
    private MotionBackgroundDrawable currMotionDrawable;
    private EmojiThemes currentTheme;
    private final ArrayMap emojiThemeDarkIcons;
    private Bitmap emojiThemeIcon;
    private final EmojiThemes homeTheme;
    private boolean isCurrentThemeDark;
    private RLottieImageView logoImageView;
    private final Rect logoRect;
    private ValueAnimator patternAlphaAnimator;
    private ValueAnimator patternIntensityAnimator;
    private MotionBackgroundDrawable prevMotionDrawable;
    private int[] prevQrColors;
    private int prevSystemUiVisibility;
    private QrView qrView;
    private final ThemeResourcesProvider resourcesProvider;
    private int selectedPosition;
    private MotionBackgroundDrawable tempMotionDrawable;
    private FrameLayout themeLayout;
    private ThemeListViewController themesViewController;
    private long userId;

    public interface OnItemSelectedListener {
        void onItemSelected(EmojiThemes emojiThemes, int i);
    }

    public static class QrView extends View {
        private Bitmap backgroundBitmap;
        private final Paint bitmapGradientPaint;
        private QrCenterChangedListener centerChangedListener;
        private Runnable checkTimerToken;
        private Bitmap contentBitmap;
        private AnimatedFloat contentBitmapAlpha;
        private Paint crossfadeFromPaint;
        private Paint crossfadeToPaint;
        private final int crossfadeWidthDp;
        private boolean firstPrepare;
        private final MotionBackgroundDrawable gradientDrawable;
        private final BitmapShader gradientShader;
        private final BitmapShader gradientTextShader;
        private Integer hadHeight;
        private String hadLink;
        private String hadUserText;
        private Integer hadWidth;
        private boolean hasTimer;
        private boolean isPhone;
        private String link;
        private int linkExpires;
        private RLottieDrawable loadingMatrix;
        private boolean logoCenterSet;
        private Bitmap oldContentBitmap;
        private float[] radii;
        private boolean setData;
        private StaticLayout shareUsernameLayout;
        private TextPaint shareUsernameLayoutPaint;
        private AnimatedTextView.AnimatedTextDrawable timerTextDrawable;
        private String username;
        private static final float SHADOW_SIZE = AndroidUtilities.dp(2.0f);
        private static final float RADIUS = AndroidUtilities.dp(20.0f);

        public interface QrCenterChangedListener {
            void onCenterChanged(int i, int i2, int i3, int i4);
        }

        QrView(Context context) {
            super(context);
            MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
            this.gradientDrawable = motionBackgroundDrawable;
            Paint paint = new Paint(1);
            this.bitmapGradientPaint = paint;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.contentBitmapAlpha = new AnimatedFloat(1.0f, this, 0L, 2000L, cubicBezierInterpolator);
            this.crossfadeFromPaint = new Paint(1);
            this.crossfadeToPaint = new Paint(1);
            this.crossfadeWidthDp = 120;
            this.radii = new float[8];
            this.checkTimerToken = new Runnable() {
                @Override
                public final void run() {
                    QrActivity.QrView.this.lambda$new$5();
                }
            };
            this.firstPrepare = true;
            motionBackgroundDrawable.setIndeterminateAnimation(true);
            motionBackgroundDrawable.setParentView(this);
            Bitmap bitmap = motionBackgroundDrawable.getBitmap();
            Shader.TileMode tileMode = Shader.TileMode.MIRROR;
            BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
            this.gradientShader = bitmapShader;
            BitmapShader bitmapShader2 = new BitmapShader(motionBackgroundDrawable.getBitmap(), tileMode, tileMode);
            this.gradientTextShader = bitmapShader2;
            paint.setShader(bitmapShader);
            boolean z = false;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(z, true, z) {
                @Override
                public void invalidateSelf() {
                    QrView.this.invalidate();
                }
            };
            this.timerTextDrawable = animatedTextDrawable;
            animatedTextDrawable.setAnimationProperties(0.35f, 0L, 300L, cubicBezierInterpolator);
            this.timerTextDrawable.setCallback(this);
            this.timerTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
            this.timerTextDrawable.getPaint().setShader(bitmapShader2);
            this.timerTextDrawable.setGravity(17);
            this.timerTextDrawable.setTextSize(AndroidUtilities.dp(35.0f));
            this.timerTextDrawable.setText("");
            Paint paint2 = this.crossfadeFromPaint;
            Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
            paint2.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(120.0f), new int[]{-1, 0}, new float[]{0.0f, 1.0f}, tileMode2));
            Paint paint3 = this.crossfadeFromPaint;
            PorterDuff.Mode mode = PorterDuff.Mode.DST_OUT;
            paint3.setXfermode(new PorterDuffXfermode(mode));
            this.crossfadeToPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(120.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, tileMode2));
            this.crossfadeToPaint.setXfermode(new PorterDuffXfermode(mode));
        }

        private void drawLoading(Canvas canvas) {
            QrCenterChangedListener qrCenterChangedListener;
            if (this.loadingMatrix != null) {
                int width = (getWidth() - AndroidUtilities.dp(60.0f)) / 33;
                int i = (width * 33) + 32;
                int width2 = (getWidth() - i) / 2;
                int height = (int) (getHeight() * 0.15f);
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    height = (int) (getHeight() * 0.09f);
                }
                int i2 = height;
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                canvas.saveLayerAlpha(rectF, 255, 31);
                int i3 = width2 + 16;
                int i4 = i2 + 16;
                canvas.drawRect(i3, i4, (getWidth() - width2) - 16, (((getWidth() + i2) - width2) - width2) - 16, this.bitmapGradientPaint);
                canvas.save();
                this.loadingMatrix.setBounds(i3, i4, (getWidth() - width2) - 16, (((getWidth() + i2) - width2) - width2) - 16);
                this.loadingMatrix.draw(canvas);
                canvas.restore();
                canvas.restore();
                float width3 = getWidth() / 2.0f;
                float f = i2;
                float f2 = width2;
                float width4 = ((getWidth() / 2.0f) + f) - f2;
                float round = ((Math.round((r9 / 4.65f) / r6) * width) / 2) * 0.75f;
                canvas.drawCircle(width3, width4, round, this.bitmapGradientPaint);
                QRCodeWriter.drawSideQuads(canvas, f2, f, this.bitmapGradientPaint, 7.0f, width, 16, i, 0.75f, this.radii, true);
                if (this.logoCenterSet || (qrCenterChangedListener = this.centerChangedListener) == null) {
                    return;
                }
                qrCenterChangedListener.onCenterChanged((int) (width3 - round), (int) (width4 - round), (int) (width3 + round), (int) (width4 + round));
                this.logoCenterSet = true;
            }
        }

        public void lambda$new$3(TLRPC.TL_exportedContactToken tL_exportedContactToken) {
            if (tL_exportedContactToken == null) {
                return;
            }
            int i = this.linkExpires;
            if (i != 0 && i < tL_exportedContactToken.expires) {
                try {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(100L);
                    }
                } catch (Exception unused) {
                    try {
                        performHapticFeedback(0, 2);
                    } catch (Exception unused2) {
                    }
                }
            }
            this.linkExpires = tL_exportedContactToken.expires;
            setData(tL_exportedContactToken.url, null, false, true);
        }

        public void lambda$new$4(final TLRPC.TL_exportedContactToken tL_exportedContactToken) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    QrActivity.QrView.this.lambda$new$3(tL_exportedContactToken);
                }
            });
        }

        public void lambda$new$5() {
            AndroidUtilities.cancelRunOnUIThread(this.checkTimerToken);
            boolean z = this.hasTimer;
            if (z) {
                if (z && this.loadingMatrix == null) {
                    RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.qr_matrix, "qr_matrix", AndroidUtilities.dp(200.0f), AndroidUtilities.dp(200.0f));
                    this.loadingMatrix = rLottieDrawable;
                    rLottieDrawable.setMasterParent(this);
                    this.loadingMatrix.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    this.loadingMatrix.setAutoRepeat(1);
                    this.loadingMatrix.start();
                }
                if (this.linkExpires == 0 || System.currentTimeMillis() / 1000 >= this.linkExpires) {
                    if (this.linkExpires != 0) {
                        this.link = null;
                        final int width = getWidth();
                        final int height = getHeight();
                        Utilities.themeQueue.postRunnable(new Runnable() {
                            @Override
                            public final void run() {
                                QrActivity.QrView.this.lambda$new$2(width, height);
                            }
                        });
                        this.timerTextDrawable.setText("");
                    }
                    MessagesController.getInstance(UserConfig.selectedAccount).requestContactToken(this.linkExpires == 0 ? 750L : 1750L, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            QrActivity.QrView.this.lambda$new$4((TLRPC.TL_exportedContactToken) obj);
                        }
                    });
                }
                int i = this.linkExpires;
                if (i > 0 && this.link != null) {
                    long max = Math.max(0L, (i - (System.currentTimeMillis() / 1000)) - 1);
                    int i2 = (int) (max % 60);
                    int min = Math.min(99, (int) (max / 60));
                    AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.timerTextDrawable;
                    StringBuilder sb = new StringBuilder();
                    sb.append(min < 10 ? "0" : "");
                    sb.append(min);
                    sb.append(":");
                    sb.append(i2 < 10 ? "0" : "");
                    sb.append(i2);
                    animatedTextDrawable.setText(sb.toString(), true, false);
                }
                if (isAttachedToWindow()) {
                    AndroidUtilities.runOnUIThread(this.checkTimerToken, 1000L);
                }
            }
        }

        public void lambda$prepareContent$6() {
            this.firstPrepare = false;
            Bitmap bitmap = this.contentBitmap;
            if (bitmap != null) {
                this.contentBitmap = null;
                this.contentBitmapAlpha.set(0.0f, true);
                Bitmap bitmap2 = this.oldContentBitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.oldContentBitmap = bitmap;
                invalidate();
            }
        }

        public void lambda$prepareContent$7(Bitmap bitmap, float f, int i, float f2) {
            Bitmap bitmap2 = this.contentBitmap;
            this.contentBitmap = bitmap.extractAlpha();
            if (!this.firstPrepare) {
                this.contentBitmapAlpha.set(0.0f, true);
            }
            this.firstPrepare = false;
            Bitmap bitmap3 = this.oldContentBitmap;
            if (bitmap3 != null) {
                bitmap3.recycle();
            }
            this.oldContentBitmap = bitmap2;
            QrCenterChangedListener qrCenterChangedListener = this.centerChangedListener;
            if (qrCenterChangedListener != null) {
                float f3 = i * 0.5f;
                qrCenterChangedListener.onCenterChanged((int) (f - f3), (int) (f2 - f3), (int) (f + f3), (int) (f2 + f3));
                this.logoCenterSet = true;
            }
            invalidate();
        }

        public void lambda$setData$1(int r29, int r30) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.QrActivity.QrView.lambda$setData$1(int, int):void");
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.checkTimerToken.run();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            RLottieDrawable rLottieDrawable = this.loadingMatrix;
            if (rLottieDrawable != null) {
                rLottieDrawable.stop();
                this.loadingMatrix.recycle(false);
                this.loadingMatrix = null;
            }
        }

        @Override
        protected void onDraw(android.graphics.Canvas r19) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.QrActivity.QrView.onDraw(android.graphics.Canvas):void");
        }

        @Override
        protected void onSizeChanged(final int i, final int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            if (i == i3 && i2 == i4) {
                return;
            }
            Bitmap bitmap = this.backgroundBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.backgroundBitmap = null;
            }
            Paint paint = new Paint(1);
            paint.setColor(-1);
            float dp = AndroidUtilities.dp(4.0f);
            float f = SHADOW_SIZE;
            paint.setShadowLayer(dp, 0.0f, f, 251658240);
            this.backgroundBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.backgroundBitmap);
            float f2 = i;
            RectF rectF = new RectF(f, f, f2 - f, getHeight() - f);
            float f3 = RADIUS;
            canvas.drawRoundRect(rectF, f3, f3, paint);
            if (this.setData) {
                Utilities.themeQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        QrActivity.QrView.this.lambda$onSizeChanged$0(i, i2);
                    }
                });
            }
            float max = Math.max((getWidth() * 1.0f) / this.gradientDrawable.getBitmap().getWidth(), (getHeight() * 1.0f) / this.gradientDrawable.getBitmap().getHeight());
            Matrix matrix = new Matrix();
            matrix.setScale(max, max);
            this.gradientShader.setLocalMatrix(matrix);
            Matrix matrix2 = new Matrix();
            matrix2.setScale(max, max);
            matrix2.postTranslate(f2 / 2.0f, getWidth() + AndroidUtilities.dp(6.0f));
            this.gradientTextShader.setLocalMatrix(matrix2);
        }

        void setCenterChangedListener(QrCenterChangedListener qrCenterChangedListener) {
            this.centerChangedListener = qrCenterChangedListener;
        }

        void setColors(int i, int i2, int i3, int i4) {
            this.gradientDrawable.setColors(i, i2, i3, i4);
            invalidate();
        }

        void setData(String str, String str2, boolean z, boolean z2) {
            this.setData = true;
            this.username = str2;
            this.isPhone = z;
            if (z2) {
                TLRPC.TL_exportedContactToken cachedContactToken = MessagesController.getInstance(UserConfig.selectedAccount).getCachedContactToken();
                if (cachedContactToken != null) {
                    this.link = cachedContactToken.url;
                    this.linkExpires = cachedContactToken.expires;
                    this.hasTimer = z2;
                    final int width = getWidth();
                    final int height = getHeight();
                    Utilities.themeQueue.postRunnable(new Runnable() {
                        @Override
                        public final void run() {
                            QrActivity.QrView.this.lambda$setData$1(width, height);
                        }
                    });
                    invalidate();
                    this.checkTimerToken.run();
                }
                str = null;
            }
            this.link = str;
            this.hasTimer = z2;
            final int width2 = getWidth();
            final int height2 = getHeight();
            Utilities.themeQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    QrActivity.QrView.this.lambda$setData$1(width2, height2);
                }
            });
            invalidate();
            this.checkTimerToken.run();
        }

        public void setForShare(boolean z) {
            StaticLayout staticLayout;
            if (this.hasTimer) {
                if (z) {
                    if (this.shareUsernameLayoutPaint == null) {
                        this.shareUsernameLayoutPaint = new TextPaint(1);
                    }
                    this.shareUsernameLayoutPaint.setShader(this.gradientTextShader);
                    this.shareUsernameLayoutPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
                    this.shareUsernameLayoutPaint.setTextSize(AndroidUtilities.dp(25.0f));
                    String str = this.username;
                    if (str == null) {
                        str = "";
                    }
                    staticLayout = StaticLayoutEx.createStaticLayout(Emoji.replaceEmoji(str, this.shareUsernameLayoutPaint.getFontMetricsInt(), false), this.shareUsernameLayoutPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, getWidth() - AndroidUtilities.dp(60.0f), 1);
                } else {
                    staticLayout = null;
                }
                this.shareUsernameLayout = staticLayout;
            }
        }

        void setPosAnimationProgress(float f) {
            this.gradientDrawable.posAnimationProgress = f;
        }
    }

    public class ThemeListViewController implements NotificationCenter.NotificationCenterDelegate {
        public final ChatThemeBottomSheet.Adapter adapter;
        private final Drawable backgroundDrawable;
        private final View bottomShadow;
        private View changeDayNightView;
        private ValueAnimator changeDayNightViewAnimator;
        private float changeDayNightViewProgress;
        private final RLottieDrawable darkThemeDrawable;
        private final RLottieImageView darkThemeView;
        private boolean forceDark;
        private final BaseFragment fragment;
        protected boolean isLightDarkChangeAnimation;
        private OnItemSelectedListener itemSelectedListener;
        private LinearLayoutManager layoutManager;
        private boolean prevIsPortrait;
        public final FlickerLoadingView progressView;
        private final RecyclerListView recyclerView;
        public final FrameLayout rootLayout;
        private final LinearSmoothScroller scroller;
        public ChatThemeBottomSheet.ChatThemeItem selectedItem;
        public final TextView shareButton;
        public final TextView titleView;
        private final View topShadow;
        private final Window window;
        private final Paint backgroundPaint = new Paint(1);
        public int prevSelectedPosition = -1;

        public ThemeListViewController(BaseFragment baseFragment, Window window) {
            this.fragment = baseFragment;
            this.window = window;
            Activity parentActivity = baseFragment.getParentActivity();
            this.scroller = new LinearSmoothScroller(parentActivity) {
                @Override
                public int calculateTimeForScrolling(int i) {
                    return super.calculateTimeForScrolling(i) * 6;
                }
            };
            Drawable mutate = parentActivity.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
            this.backgroundDrawable = mutate;
            int themedColor = baseFragment.getThemedColor(Theme.key_dialogBackground);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            mutate.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
            FrameLayout frameLayout = new FrameLayout(parentActivity, QrActivity.this, baseFragment) {
                private final Rect backgroundPadding;
                final BaseFragment val$fragment;
                final QrActivity val$this$0;

                {
                    this.val$fragment = baseFragment;
                    Rect rect = new Rect();
                    this.backgroundPadding = rect;
                    ThemeListViewController.this.backgroundPaint.setColor(baseFragment.getThemedColor(Theme.key_windowBackgroundWhite));
                    ThemeListViewController.this.backgroundDrawable.setCallback(this);
                    ThemeListViewController.this.backgroundDrawable.getPadding(rect);
                    setPadding(0, rect.top + AndroidUtilities.dp(8.0f), 0, rect.bottom);
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    if (ThemeListViewController.this.prevIsPortrait) {
                        ThemeListViewController.this.backgroundDrawable.setBounds(-this.backgroundPadding.left, 0, getWidth() + this.backgroundPadding.right, getHeight());
                        ThemeListViewController.this.backgroundDrawable.draw(canvas);
                    } else {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(0.0f, 0.0f, getWidth() + AndroidUtilities.dp(14.0f), getHeight());
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), ThemeListViewController.this.backgroundPaint);
                    }
                    super.dispatchDraw(canvas);
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    float f;
                    TextView textView;
                    float f2;
                    float f3;
                    int i3;
                    float f4;
                    int i4;
                    float f5;
                    Point point = AndroidUtilities.displaySize;
                    boolean z = point.x < point.y;
                    int dp = AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView = ThemeListViewController.this.recyclerView;
                    if (z) {
                        recyclerListView.setLayoutParams(LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 44.0f, 0.0f, 0.0f));
                        ThemeListViewController.this.recyclerView.setPadding(dp, 0, dp, 0);
                        textView = ThemeListViewController.this.shareButton;
                        f2 = 16.0f;
                        f3 = 16.0f;
                        i3 = -1;
                        f4 = 48.0f;
                        i4 = 8388611;
                        f5 = 16.0f;
                        f = 162.0f;
                    } else {
                        f = 0.0f;
                        recyclerListView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 8388611, 0.0f, 44.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.recyclerView.setPadding(dp, dp / 2, dp, dp);
                        textView = ThemeListViewController.this.shareButton;
                        f2 = 16.0f;
                        f3 = 16.0f;
                        i3 = -1;
                        f4 = 48.0f;
                        i4 = 80;
                        f5 = 16.0f;
                    }
                    textView.setLayoutParams(LayoutHelper.createFrame(i3, f4, i4, f5, f, f2, f3));
                    View view = ThemeListViewController.this.bottomShadow;
                    if (z) {
                        view.setVisibility(8);
                        ThemeListViewController.this.topShadow.setVisibility(8);
                    } else {
                        view.setVisibility(0);
                        ThemeListViewController.this.bottomShadow.setLayoutParams(LayoutHelper.createFrame(-1, AndroidUtilities.dp(2.0f), 80, 0.0f, 0.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.topShadow.setVisibility(0);
                        ThemeListViewController.this.topShadow.setLayoutParams(LayoutHelper.createFrame(-1, AndroidUtilities.dp(2.0f), 48, 0.0f, 44.0f, 0.0f, 0.0f));
                    }
                    if (ThemeListViewController.this.prevIsPortrait != z) {
                        RecyclerListView recyclerListView2 = ThemeListViewController.this.recyclerView;
                        ThemeListViewController themeListViewController = ThemeListViewController.this;
                        recyclerListView2.setLayoutManager(themeListViewController.layoutManager = themeListViewController.getLayoutManager(z));
                        ThemeListViewController.this.recyclerView.requestLayout();
                        ThemeListViewController themeListViewController2 = ThemeListViewController.this;
                        int i5 = themeListViewController2.prevSelectedPosition;
                        if (i5 != -1) {
                            themeListViewController2.setSelectedPosition(i5);
                        }
                        ThemeListViewController.this.prevIsPortrait = z;
                    }
                    super.onMeasure(i, i2);
                }

                @Override
                protected boolean verifyDrawable(Drawable drawable) {
                    return drawable == ThemeListViewController.this.backgroundDrawable || super.verifyDrawable(drawable);
                }
            };
            this.rootLayout = frameLayout;
            TextView textView = new TextView(parentActivity);
            this.titleView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setTextColor(baseFragment.getThemedColor(Theme.key_dialogTextBlack));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 0.0f, 62.0f, 0.0f));
            int i = Theme.key_featuredStickers_addButton;
            int themedColor2 = baseFragment.getThemedColor(i);
            int dp = AndroidUtilities.dp(28.0f);
            int i2 = R.raw.sun_outline;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, dp, dp, false, null);
            this.darkThemeDrawable = rLottieDrawable;
            this.forceDark = Theme.getActiveTheme().isDark() ^ true;
            setForceDark(Theme.getActiveTheme().isDark(), false);
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            rLottieDrawable.setColorFilter(new PorterDuffColorFilter(themedColor2, mode));
            RLottieImageView rLottieImageView = new RLottieImageView(parentActivity) {
                @Override
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    accessibilityNodeInfo.setText(LocaleController.getString(QrActivity.this.isCurrentThemeDark ? R.string.AccDescrSwitchToDayTheme : R.string.AccDescrSwitchToNightTheme));
                }
            };
            this.darkThemeView = rLottieImageView;
            rLottieImageView.setAnimation(rLottieDrawable);
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            rLottieImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    QrActivity.ThemeListViewController.this.lambda$new$0(view);
                }
            });
            rLottieImageView.setAlpha(0.0f);
            rLottieImageView.setVisibility(4);
            frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(44, 44.0f, 8388661, 0.0f, -2.0f, 7.0f, 0.0f));
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(parentActivity, baseFragment.getResourceProvider());
            this.progressView = flickerLoadingView;
            flickerLoadingView.setVisibility(0);
            frameLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 44.0f, 0.0f, 0.0f));
            Point point = AndroidUtilities.displaySize;
            this.prevIsPortrait = point.x < point.y;
            RecyclerListView recyclerListView = new RecyclerListView(parentActivity);
            this.recyclerView = recyclerListView;
            ChatThemeBottomSheet.Adapter adapter = new ChatThemeBottomSheet.Adapter(((BaseFragment) QrActivity.this).currentAccount, QrActivity.this.resourcesProvider, 2);
            this.adapter = adapter;
            recyclerListView.setAdapter(adapter);
            recyclerListView.setClipChildren(false);
            recyclerListView.setClipToPadding(false);
            recyclerListView.setItemAnimator(null);
            recyclerListView.setNestedScrollingEnabled(false);
            LinearLayoutManager layoutManager = getLayoutManager(this.prevIsPortrait);
            this.layoutManager = layoutManager;
            recyclerListView.setLayoutManager(layoutManager);
            recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i3) {
                    QrActivity.ThemeListViewController.this.onItemClicked(view, i3);
                }
            });
            recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                private int yScroll = 0;

                @Override
                public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                    super.onScrolled(recyclerView, i3, i4);
                    this.yScroll += i4;
                    ThemeListViewController.this.topShadow.setAlpha((this.yScroll * 1.0f) / AndroidUtilities.dp(6.0f));
                }
            });
            frameLayout.addView(recyclerListView);
            View view = new View(parentActivity);
            this.topShadow = view;
            view.setAlpha(0.0f);
            int i3 = R.drawable.shadowdown;
            view.setBackground(ContextCompat.getDrawable(parentActivity, i3));
            view.setRotation(180.0f);
            frameLayout.addView(view);
            View view2 = new View(parentActivity);
            this.bottomShadow = view2;
            view2.setBackground(ContextCompat.getDrawable(parentActivity, i3));
            frameLayout.addView(view2);
            TextView textView2 = new TextView(parentActivity);
            this.shareButton = textView2;
            textView2.setBackground(Theme.AdaptiveRipple.filledRect(baseFragment.getThemedColor(i), 6.0f));
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setGravity(17);
            textView2.setLines(1);
            textView2.setSingleLine(true);
            textView2.setText(LocaleController.getString(R.string.ShareQrCode));
            textView2.setTextColor(baseFragment.getThemedColor(Theme.key_featuredStickers_buttonText));
            textView2.setTextSize(1, 15.0f);
            textView2.setTypeface(AndroidUtilities.bold());
            frameLayout.addView(textView2);
        }

        public LinearLayoutManager getLayoutManager(boolean z) {
            return z ? new LinearLayoutManager(this.fragment.getParentActivity(), 0, false) : new GridLayoutManager(this.fragment.getParentActivity(), 3, 1, false);
        }

        public void lambda$new$0(View view) {
            if (this.changeDayNightViewAnimator != null) {
                return;
            }
            setupLightDarkTheme(!this.forceDark);
        }

        public void lambda$onItemClicked$1(int i) {
            RecyclerView.LayoutManager layoutManager = this.recyclerView.getLayoutManager();
            if (layoutManager != null) {
                this.scroller.setTargetPosition(i > this.prevSelectedPosition ? Math.min(i + 1, this.adapter.items.size() - 1) : Math.max(i - 1, 0));
                layoutManager.startSmoothScroll(this.scroller);
            }
            this.prevSelectedPosition = i;
        }

        public void lambda$setupLightDarkTheme$2(ValueAnimator valueAnimator) {
            this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.changeDayNightView.invalidate();
        }

        public void lambda$setupLightDarkTheme$3(boolean z) {
            ChatThemeBottomSheet.Adapter adapter = this.adapter;
            if (adapter == null || adapter.items == null) {
                return;
            }
            setForceDark(z, true);
            if (this.selectedItem != null) {
                this.isLightDarkChangeAnimation = true;
                setDarkTheme(z);
            }
            if (this.adapter.items != null) {
                for (int i = 0; i < this.adapter.items.size(); i++) {
                    ((ChatThemeBottomSheet.ChatThemeItem) this.adapter.items.get(i)).themeIndex = z ? 1 : 0;
                    ((ChatThemeBottomSheet.ChatThemeItem) this.adapter.items.get(i)).icon = QrActivity.this.getEmojiThemeIcon(((ChatThemeBottomSheet.ChatThemeItem) this.adapter.items.get(i)).chatTheme, z);
                }
                QrActivity.this.tempMotionDrawable = null;
                this.adapter.notifyDataSetChanged();
            }
        }

        public void onAnimationEnd() {
            this.isLightDarkChangeAnimation = false;
        }

        public void onAnimationStart() {
            List list;
            ChatThemeBottomSheet.Adapter adapter = this.adapter;
            if (adapter != null && (list = adapter.items) != null) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    ((ChatThemeBottomSheet.ChatThemeItem) it.next()).themeIndex = this.forceDark ? 1 : 0;
                }
            }
            if (this.isLightDarkChangeAnimation) {
                return;
            }
            setItemsAnimationProgress(1.0f);
        }

        public void setItemsAnimationProgress(float f) {
            for (int i = 0; i < this.adapter.getItemCount(); i++) {
                ((ChatThemeBottomSheet.ChatThemeItem) this.adapter.items.get(i)).animationProgress = f;
            }
        }

        private void setupLightDarkTheme(final boolean z) {
            ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            FrameLayout frameLayout = (FrameLayout) this.fragment.getParentActivity().getWindow().getDecorView();
            FrameLayout frameLayout2 = (FrameLayout) this.window.getDecorView();
            final Bitmap createBitmap = Bitmap.createBitmap(frameLayout2.getWidth(), frameLayout2.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(createBitmap);
            this.darkThemeView.setAlpha(0.0f);
            frameLayout.draw(canvas);
            frameLayout2.draw(canvas);
            this.darkThemeView.setAlpha(1.0f);
            final Paint paint = new Paint(1);
            paint.setColor(-16777216);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            final Paint paint2 = new Paint(1);
            paint2.setFilterBitmap(true);
            int[] iArr = new int[2];
            this.darkThemeView.getLocationInWindow(iArr);
            final float f = iArr[0];
            final float f2 = iArr[1];
            final float measuredWidth = f + (this.darkThemeView.getMeasuredWidth() / 2.0f);
            final float measuredHeight = f2 + (this.darkThemeView.getMeasuredHeight() / 2.0f);
            final float max = Math.max(createBitmap.getHeight(), createBitmap.getWidth()) * 0.9f;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            paint2.setShader(new BitmapShader(createBitmap, tileMode, tileMode));
            this.changeDayNightView = new View(this.fragment.getParentActivity()) {
                @Override
                protected void onDraw(Canvas canvas2) {
                    super.onDraw(canvas2);
                    if (z) {
                        if (ThemeListViewController.this.changeDayNightViewProgress > 0.0f) {
                            canvas.drawCircle(measuredWidth, measuredHeight, max * ThemeListViewController.this.changeDayNightViewProgress, paint);
                        }
                        canvas2.drawBitmap(createBitmap, 0.0f, 0.0f, paint2);
                    } else {
                        canvas2.drawCircle(measuredWidth, measuredHeight, max * (1.0f - ThemeListViewController.this.changeDayNightViewProgress), paint2);
                    }
                    canvas2.save();
                    canvas2.translate(f, f2);
                    ThemeListViewController.this.darkThemeView.draw(canvas2);
                    canvas2.restore();
                }
            };
            this.changeDayNightViewProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.changeDayNightViewAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    QrActivity.ThemeListViewController.this.lambda$setupLightDarkTheme$2(valueAnimator2);
                }
            });
            this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (ThemeListViewController.this.changeDayNightView != null) {
                        if (ThemeListViewController.this.changeDayNightView.getParent() != null) {
                            ((ViewGroup) ThemeListViewController.this.changeDayNightView.getParent()).removeView(ThemeListViewController.this.changeDayNightView);
                        }
                        ThemeListViewController.this.changeDayNightView = null;
                    }
                    ThemeListViewController.this.changeDayNightViewAnimator = null;
                    super.onAnimationEnd(animator);
                }
            });
            this.changeDayNightViewAnimator.setDuration(400L);
            this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
            this.changeDayNightViewAnimator.start();
            frameLayout2.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    QrActivity.ThemeListViewController.this.lambda$setupLightDarkTheme$3(z);
                }
            });
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.emojiLoaded) {
                this.adapter.notifyDataSetChanged();
            }
        }

        public ArrayList getThemeDescriptions() {
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
                private boolean isAnimationStarted = false;

                @Override
                public void didSetColor() {
                }

                @Override
                public void onAnimationProgress(float f) {
                    if (f == 0.0f && !this.isAnimationStarted) {
                        ThemeListViewController.this.onAnimationStart();
                        this.isAnimationStarted = true;
                    }
                    ThemeListViewController.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ThemeListViewController.this.fragment.getThemedColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
                    ThemeListViewController themeListViewController = ThemeListViewController.this;
                    if (themeListViewController.isLightDarkChangeAnimation) {
                        themeListViewController.setItemsAnimationProgress(f);
                    }
                    if (f == 1.0f && this.isAnimationStarted) {
                        ThemeListViewController themeListViewController2 = ThemeListViewController.this;
                        themeListViewController2.isLightDarkChangeAnimation = false;
                        themeListViewController2.onAnimationEnd();
                        this.isAnimationStarted = false;
                    }
                }
            };
            ArrayList arrayList = new ArrayList();
            int i = ThemeDescription.FLAG_BACKGROUND;
            Paint paint = this.backgroundPaint;
            int i2 = Theme.key_dialogBackground;
            arrayList.add(new ThemeDescription(null, i, null, paint, null, null, i2));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{this.backgroundDrawable}, themeDescriptionDelegate, i2));
            arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
            arrayList.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, null, null, null, Theme.key_dialogBackgroundGray));
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((ThemeDescription) it.next()).resourcesProvider = this.fragment.getResourceProvider();
            }
            return arrayList;
        }

        public void onCreate() {
            ChatThemeController chatThemeController = ChatThemeController.getInstance(((BaseFragment) QrActivity.this).currentAccount);
            chatThemeController.preloadAllWallpaperThumbs(true);
            chatThemeController.preloadAllWallpaperThumbs(false);
            chatThemeController.preloadAllWallpaperImages(true);
            chatThemeController.preloadAllWallpaperImages(false);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        public void onDataLoaded() {
            this.darkThemeView.setAlpha(0.0f);
            this.darkThemeView.animate().alpha(1.0f).setDuration(150L).start();
            this.darkThemeView.setVisibility(0);
            this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150L).start();
            this.recyclerView.setAlpha(0.0f);
            this.recyclerView.animate().alpha(1.0f).setDuration(150L).start();
        }

        public void onDestroy() {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }

        public void onItemClicked(View view, final int i) {
            if (this.adapter.items.get(i) == this.selectedItem || this.changeDayNightView != null) {
                return;
            }
            this.isLightDarkChangeAnimation = false;
            this.selectedItem = (ChatThemeBottomSheet.ChatThemeItem) this.adapter.items.get(i);
            this.adapter.setSelectedItem(i);
            this.rootLayout.postDelayed(new Runnable() {
                @Override
                public final void run() {
                    QrActivity.ThemeListViewController.this.lambda$onItemClicked$1(i);
                }
            }, 100L);
            for (int i2 = 0; i2 < this.recyclerView.getChildCount(); i2++) {
                ThemeSmallPreviewView themeSmallPreviewView = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i2);
                if (themeSmallPreviewView != view) {
                    themeSmallPreviewView.cancelAnimation();
                }
            }
            if (!((ChatThemeBottomSheet.ChatThemeItem) this.adapter.items.get(i)).chatTheme.showAsDefaultStub) {
                ((ThemeSmallPreviewView) view).playEmojiAnimation();
            }
            OnItemSelectedListener onItemSelectedListener = this.itemSelectedListener;
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(this.selectedItem.chatTheme, i);
            }
        }

        protected void setDarkTheme(boolean z) {
        }

        public void setForceDark(boolean z, boolean z2) {
            if (this.forceDark == z) {
                return;
            }
            this.forceDark = z;
            int framesCount = z ? this.darkThemeDrawable.getFramesCount() - 1 : 0;
            if (z2) {
                this.darkThemeDrawable.setCustomEndFrame(framesCount);
                RLottieImageView rLottieImageView = this.darkThemeView;
                if (rLottieImageView != null) {
                    rLottieImageView.playAnimation();
                    return;
                }
                return;
            }
            this.darkThemeDrawable.setCustomEndFrame(framesCount);
            this.darkThemeDrawable.setCurrentFrame(framesCount, false, true);
            RLottieImageView rLottieImageView2 = this.darkThemeView;
            if (rLottieImageView2 != null) {
                rLottieImageView2.invalidate();
            }
        }

        public void setItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
            this.itemSelectedListener = onItemSelectedListener;
        }

        public void setSelectedPosition(int i) {
            this.prevSelectedPosition = i;
            this.adapter.setSelectedItem(i);
            if (i > 0 && i < this.adapter.items.size() / 2) {
                i--;
            }
            this.layoutManager.scrollToPositionWithOffset(Math.min(i, this.adapter.items.size() - 1), 0);
        }
    }

    public class ThemeResourcesProvider implements Theme.ResourcesProvider {
        private SparseIntArray colors;

        private ThemeResourcesProvider() {
        }

        @Override
        public void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }

        @Override
        public ColorFilter getAnimatedEmojiColorFilter() {
            ColorFilter colorFilter;
            colorFilter = Theme.chat_animatedEmojiTextColorFilter;
            return colorFilter;
        }

        @Override
        public int getColor(int i) {
            SparseIntArray sparseIntArray = this.colors;
            return sparseIntArray != null ? sparseIntArray.get(i) : Theme.getColor(i);
        }

        @Override
        public int getColorOrDefault(int i) {
            int color;
            color = getColor(i);
            return color;
        }

        @Override
        public int getCurrentColor(int i) {
            int color;
            color = getColor(i);
            return color;
        }

        @Override
        public Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
        }

        @Override
        public Paint getPaint(String str) {
            Paint themePaint;
            themePaint = Theme.getThemePaint(str);
            return themePaint;
        }

        @Override
        public boolean hasGradientService() {
            return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
        }

        void initColors(EmojiThemes emojiThemes, boolean z) {
            this.colors = emojiThemes.createColors(((BaseFragment) QrActivity.this).currentAccount, z ? 1 : 0);
        }

        @Override
        public boolean isDark() {
            boolean isCurrentThemeDark;
            isCurrentThemeDark = Theme.isCurrentThemeDark();
            return isCurrentThemeDark;
        }

        @Override
        public void setAnimatedColor(int i, int i2) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i, i2);
        }
    }

    static {
        ArrayMap arrayMap = new ArrayMap();
        qrColorsMap = arrayMap;
        arrayMap.put("🏠d", new int[]{-9324972, -13856649, -6636738, -9915042});
        arrayMap.put("🐥d", new int[]{-12344463, -7684788, -6442695, -8013488});
        arrayMap.put("⛄d", new int[]{-10051073, -10897938, -12469550, -7694337});
        arrayMap.put("💎d", new int[]{-11429643, -11814958, -5408261, -2128185});
        arrayMap.put("👨\u200d🏫d", new int[]{-6637227, -12015466, -13198627, -10631557});
        arrayMap.put("🌷d", new int[]{-1146812, -1991901, -1745517, -3443241});
        arrayMap.put("💜d", new int[]{-1156738, -1876046, -5412366, -28073});
        arrayMap.put("🎄d", new int[]{-1281978, -551386, -1870308, -742870});
        arrayMap.put("🎮d", new int[]{-15092782, -2333964, -1684365, -1269214});
        arrayMap.put("🏠n", new int[]{-15368239, -11899662, -15173939, -13850930});
        arrayMap.put("🐥n", new int[]{-11033320, -14780848, -9594089, -12604587});
        arrayMap.put("⛄n", new int[]{-13930790, -13665098, -14833975, -9732865});
        arrayMap.put("💎n", new int[]{-5089608, -9481473, -14378302, -13337899});
        arrayMap.put("👨\u200d🏫n", new int[]{-14447768, -9199261, -15356801, -15823723});
        arrayMap.put("🌷n", new int[]{-2534316, -2984177, -3258783, -5480504});
        arrayMap.put("💜n", new int[]{-3123030, -2067394, -2599576, -6067757});
        arrayMap.put("🎄n", new int[]{-2725857, -3242459, -3248848, -3569123});
        arrayMap.put("🎮n", new int[]{-3718333, -1278154, -16338695, -6076417});
        firstOpen = true;
    }

    public QrActivity(Bundle bundle) {
        super(bundle);
        this.resourcesProvider = new ThemeResourcesProvider();
        EmojiThemes createHomeQrTheme = EmojiThemes.createHomeQrTheme(this.currentAccount);
        this.homeTheme = createHomeQrTheme;
        this.logoRect = new Rect();
        this.emojiThemeDarkIcons = new ArrayMap();
        this.prevQrColors = null;
        this.currMotionDrawable = new MotionBackgroundDrawable();
        this.currentTheme = createHomeQrTheme;
        this.selectedPosition = -1;
    }

    private void applyScreenSettings() {
        if (getParentActivity() != null) {
            getParentActivity().getWindow().getDecorView().setSystemUiVisibility(this.prevSystemUiVisibility | 1028);
        }
    }

    public Bitmap getEmojiThemeIcon(EmojiThemes emojiThemes, boolean z) {
        if (!z) {
            return this.emojiThemeIcon;
        }
        Bitmap bitmap = (Bitmap) this.emojiThemeDarkIcons.get(emojiThemes.emoji);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.emojiThemeIcon.getWidth(), this.emojiThemeIcon.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            int[] iArr = (int[]) qrColorsMap.get(emojiThemes.emoji + "n");
            if (iArr != null) {
                if (this.tempMotionDrawable == null) {
                    this.tempMotionDrawable = new MotionBackgroundDrawable(0, 0, 0, 0, true);
                }
                this.tempMotionDrawable.setColors(iArr[0], iArr[1], iArr[2], iArr[3]);
                this.tempMotionDrawable.setBounds(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), canvas.getWidth() - AndroidUtilities.dp(6.0f), canvas.getHeight() - AndroidUtilities.dp(6.0f));
                this.tempMotionDrawable.draw(canvas);
            }
            canvas.drawBitmap(this.emojiThemeIcon, 0.0f, 0.0f, (Paint) null);
            canvas.setBitmap(null);
            this.emojiThemeDarkIcons.put(emojiThemes.emoji, bitmap);
        }
        return bitmap;
    }

    public void lambda$createView$0(int i, int i2, int i3, int i4) {
        this.logoRect.set(i, i2, i3, i4);
        this.qrView.requestLayout();
    }

    public void lambda$createView$1(View view) {
        lambda$onBackPressed$323();
    }

    public void lambda$createView$2(EmojiThemes emojiThemes, int i) {
        onItemSelected(emojiThemes, i, true);
    }

    public void lambda$createView$3(View view) {
        this.themesViewController.shareButton.setClickable(false);
        performShare();
    }

    public void lambda$createView$4() {
        onItemSelected(this.currentTheme, 0, true);
        this.logoImageView.getAnimatedDrawable().cacheFrame(33);
    }

    public void lambda$createView$5() {
        this.homeTheme.loadPreviewColors(this.currentAccount);
        View view = this.fragmentView;
        if (view == null) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$createView$4();
            }
        }, 17L);
    }

    public void lambda$createView$6() {
        firstOpen = false;
        List list = cachedThemes;
        if (list == null || list.isEmpty()) {
            ChatThemeController.getInstance(this.currentAccount).requestAllChatThemes(new ResultCallback() {
                @Override
                public void onComplete(List list2) {
                    QrActivity.this.onDataLoaded(list2);
                    List unused = QrActivity.cachedThemes = list2;
                }

                @Override
                public void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                @Override
                public void onError(TLRPC.TL_error tL_error) {
                    Toast.makeText(QrActivity.this.getParentActivity(), tL_error.text, 0).show();
                }
            }, true);
        } else {
            onDataLoaded(cachedThemes);
        }
    }

    public void lambda$getThemeDescriptions$15() {
        setNavigationBarColor(getThemedColor(Theme.key_windowBackgroundGray));
    }

    public void lambda$onItemSelected$10() {
        final Bitmap bitmap = SvgHelper.getBitmap(R.raw.default_pattern, this.backgroundView.getWidth(), this.backgroundView.getHeight(), -16777216);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$onItemSelected$9(bitmap);
            }
        });
    }

    public void lambda$onItemSelected$11(int[] iArr, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        MotionBackgroundDrawable motionBackgroundDrawable = this.prevMotionDrawable;
        if (motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setBackgroundAlpha(1.0f);
            this.prevMotionDrawable.setPatternAlpha(1.0f - floatValue);
        }
        this.currMotionDrawable.setBackgroundAlpha(floatValue);
        this.currMotionDrawable.setPatternAlpha(floatValue);
        if (iArr != null) {
            this.qrView.setColors(ColorUtils.blendARGB(this.prevQrColors[0], iArr[0], floatValue), ColorUtils.blendARGB(this.prevQrColors[1], iArr[1], floatValue), ColorUtils.blendARGB(this.prevQrColors[2], iArr[2], floatValue), ColorUtils.blendARGB(this.prevQrColors[3], iArr[3], floatValue));
        }
        this.backgroundView.invalidate();
    }

    public void lambda$onItemSelected$12() {
        this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
    }

    public void lambda$onItemSelected$13(boolean z, EmojiThemes emojiThemes, INavigationLayout.ThemeAnimationSettings themeAnimationSettings) {
        ThemeResourcesProvider themeResourcesProvider;
        if (z) {
            themeResourcesProvider = this.resourcesProvider;
        } else {
            themeResourcesProvider = this.resourcesProvider;
            emojiThemes = this.currentTheme;
        }
        themeResourcesProvider.initColors(emojiThemes, this.isCurrentThemeDark);
        themeAnimationSettings.afterStartDescriptionsAddedRunnable = new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$onItemSelected$12();
            }
        };
        this.parentLayout.animateThemedValues(themeAnimationSettings, null);
    }

    public void lambda$onItemSelected$8(boolean z, long j, Pair pair) {
        if (pair == null || this.currentTheme.getTlTheme(z ? 1 : 0) == null) {
            return;
        }
        long longValue = ((Long) pair.first).longValue();
        Bitmap bitmap = (Bitmap) pair.second;
        if (longValue != this.currentTheme.getTlTheme(z ? 1 : 0).id || bitmap == null) {
            return;
        }
        onPatternLoaded(bitmap, this.currMotionDrawable.getIntensity(), SystemClock.elapsedRealtime() - j > 150);
    }

    public void lambda$onItemSelected$9(Bitmap bitmap) {
        onPatternLoaded(bitmap, 34, true);
    }

    public void lambda$onPatternLoaded$7(ValueAnimator valueAnimator) {
        this.currMotionDrawable.setPatternAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$performShare$14() {
        ThemeListViewController themeListViewController = this.themesViewController;
        if (themeListViewController == null) {
            return;
        }
        themeListViewController.shareButton.setClickable(true);
    }

    public void onDataLoaded(List list) {
        if (list == null || list.isEmpty() || this.themesViewController == null) {
            return;
        }
        int i = 0;
        list.set(0, this.homeTheme);
        ArrayList arrayList = new ArrayList(list.size());
        for (int i2 = 0; i2 < list.size(); i2++) {
            EmojiThemes emojiThemes = (EmojiThemes) list.get(i2);
            emojiThemes.loadPreviewColors(this.currentAccount);
            ChatThemeBottomSheet.ChatThemeItem chatThemeItem = new ChatThemeBottomSheet.ChatThemeItem(emojiThemes);
            boolean z = this.isCurrentThemeDark;
            chatThemeItem.themeIndex = z ? 1 : 0;
            chatThemeItem.icon = getEmojiThemeIcon(emojiThemes, z);
            arrayList.add(chatThemeItem);
        }
        this.themesViewController.adapter.setItems(arrayList);
        while (true) {
            if (i == arrayList.size()) {
                i = -1;
                break;
            } else {
                if (((ChatThemeBottomSheet.ChatThemeItem) arrayList.get(i)).chatTheme.getEmoticon().equals(this.currentTheme.getEmoticon())) {
                    this.themesViewController.selectedItem = (ChatThemeBottomSheet.ChatThemeItem) arrayList.get(i);
                    break;
                }
                i++;
            }
        }
        if (i != -1) {
            this.themesViewController.setSelectedPosition(i);
        }
        this.themesViewController.onDataLoaded();
    }

    public void onItemSelected(EmojiThemes emojiThemes, int i, final boolean z) {
        float f;
        this.selectedPosition = i;
        final EmojiThemes emojiThemes2 = this.currentTheme;
        final boolean z2 = this.isCurrentThemeDark;
        this.currentTheme = emojiThemes;
        EmojiThemes.ThemeItem themeItem = emojiThemes.getThemeItem(z2 ? 1 : 0);
        ValueAnimator valueAnimator = this.patternAlphaAnimator;
        if (valueAnimator != null) {
            f = Math.max(0.5f, 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue()) * 1.0f;
            this.patternAlphaAnimator.cancel();
        } else {
            f = 1.0f;
        }
        MotionBackgroundDrawable motionBackgroundDrawable = this.currMotionDrawable;
        this.prevMotionDrawable = motionBackgroundDrawable;
        motionBackgroundDrawable.setIndeterminateAnimation(false);
        this.prevMotionDrawable.setAlpha(255);
        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable();
        this.currMotionDrawable = motionBackgroundDrawable2;
        motionBackgroundDrawable2.setCallback(this.backgroundView);
        this.currMotionDrawable.setColors(themeItem.patternBgColor, themeItem.patternBgGradientColor1, themeItem.patternBgGradientColor2, themeItem.patternBgGradientColor3);
        this.currMotionDrawable.setParentView(this.backgroundView);
        this.currMotionDrawable.setPatternAlpha(1.0f);
        this.currMotionDrawable.setIndeterminateAnimation(true);
        MotionBackgroundDrawable motionBackgroundDrawable3 = this.prevMotionDrawable;
        if (motionBackgroundDrawable3 != null) {
            this.currMotionDrawable.posAnimationProgress = motionBackgroundDrawable3.posAnimationProgress;
        }
        this.qrView.setPosAnimationProgress(this.currMotionDrawable.posAnimationProgress);
        TLRPC.WallPaper wallpaper = this.currentTheme.getWallpaper(z2 ? 1 : 0);
        if (wallpaper != null) {
            this.currMotionDrawable.setPatternBitmap(wallpaper.settings.intensity);
            final long elapsedRealtime = SystemClock.elapsedRealtime();
            this.currentTheme.loadWallpaper(z2 ? 1 : 0, new ResultCallback() {
                @Override
                public final void onComplete(Object obj) {
                    QrActivity.this.lambda$onItemSelected$8(z2, elapsedRealtime, (Pair) obj);
                }

                @Override
                public void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                @Override
                public void onError(TLRPC.TL_error tL_error) {
                    ResultCallback.CC.$default$onError(this, tL_error);
                }
            });
        } else {
            Utilities.themeQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    QrActivity.this.lambda$onItemSelected$10();
                }
            }, 35L);
        }
        MotionBackgroundDrawable motionBackgroundDrawable4 = this.currMotionDrawable;
        motionBackgroundDrawable4.setPatternColorFilter(motionBackgroundDrawable4.getPatternColor());
        ArrayMap arrayMap = qrColorsMap;
        StringBuilder sb = new StringBuilder();
        sb.append(emojiThemes.emoji);
        sb.append(z2 ? "n" : "d");
        final int[] iArr = (int[]) arrayMap.get(sb.toString());
        if (z) {
            if (this.prevQrColors == null) {
                int[] iArr2 = new int[4];
                this.prevQrColors = iArr2;
                System.arraycopy(iArr, 0, iArr2, 0, 4);
            }
            this.currMotionDrawable.setAlpha(255);
            this.currMotionDrawable.setBackgroundAlpha(0.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.patternAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    QrActivity.this.lambda$onItemSelected$11(iArr, valueAnimator2);
                }
            });
            this.patternAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    float floatValue = ((Float) ((ValueAnimator) animator).getAnimatedValue()).floatValue();
                    if (iArr != null) {
                        System.arraycopy(new int[]{ColorUtils.blendARGB(QrActivity.this.prevQrColors[0], iArr[0], floatValue), ColorUtils.blendARGB(QrActivity.this.prevQrColors[1], iArr[1], floatValue), ColorUtils.blendARGB(QrActivity.this.prevQrColors[2], iArr[2], floatValue), ColorUtils.blendARGB(QrActivity.this.prevQrColors[3], iArr[3], floatValue)}, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    int[] iArr3 = iArr;
                    if (iArr3 != null) {
                        System.arraycopy(iArr3, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                    QrActivity.this.prevMotionDrawable = null;
                    QrActivity.this.patternAlphaAnimator = null;
                    QrActivity.this.currMotionDrawable.setBackgroundAlpha(1.0f);
                    QrActivity.this.currMotionDrawable.setPatternAlpha(1.0f);
                }
            });
            this.patternAlphaAnimator.setDuration((int) (f * 250.0f));
            this.patternAlphaAnimator.start();
        } else {
            if (iArr != null) {
                this.qrView.setColors(iArr[0], iArr[1], iArr[2], iArr[3]);
                System.arraycopy(iArr, 0, this.prevQrColors, 0, 4);
            }
            this.prevMotionDrawable = null;
            this.backgroundView.invalidate();
        }
        final INavigationLayout.ThemeAnimationSettings themeAnimationSettings = new INavigationLayout.ThemeAnimationSettings(null, (this.isCurrentThemeDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()).currentAccentId, this.isCurrentThemeDark, !z);
        themeAnimationSettings.applyTheme = false;
        themeAnimationSettings.onlyTopFragment = true;
        themeAnimationSettings.resourcesProvider = getResourceProvider();
        themeAnimationSettings.duration = (int) (f * 250.0f);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$onItemSelected$13(z, emojiThemes2, themeAnimationSettings);
            }
        });
    }

    private void onPatternLoaded(Bitmap bitmap, int i, boolean z) {
        if (bitmap != null) {
            this.currMotionDrawable.setPatternBitmap(i, bitmap, true);
            ValueAnimator valueAnimator = this.patternIntensityAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!z) {
                this.currMotionDrawable.setPatternAlpha(1.0f);
                return;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.patternIntensityAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    QrActivity.this.lambda$onPatternLoaded$7(valueAnimator2);
                }
            });
            this.patternIntensityAnimator.setDuration(250L);
            this.patternIntensityAnimator.start();
        }
    }

    private void performShare() {
        Point point = AndroidUtilities.displaySize;
        int min = Math.min(point.x, point.y);
        Point point2 = AndroidUtilities.displaySize;
        int max = Math.max(point2.x, point2.y);
        float f = min;
        if ((max * 1.0f) / f > 1.92f) {
            max = (int) (f * 1.92f);
        }
        Bitmap createBitmap = Bitmap.createBitmap(min, max, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.themeLayout.setVisibility(8);
        this.closeImageView.setVisibility(8);
        this.logoImageView.setVisibility(8);
        RLottieDrawable animatedDrawable = this.logoImageView.getAnimatedDrawable();
        QrView qrView = this.qrView;
        if (qrView != null) {
            qrView.setForShare(true);
        }
        this.fragmentView.measure(View.MeasureSpec.makeMeasureSpec(min, 1073741824), View.MeasureSpec.makeMeasureSpec(max, 1073741824));
        this.fragmentView.layout(0, 0, min, max);
        this.fragmentView.draw(canvas);
        animatedDrawable.setBounds(this.logoImageView.getLeft(), this.logoImageView.getTop(), this.logoImageView.getRight(), this.logoImageView.getBottom());
        animatedDrawable.drawFrame(canvas, 33);
        canvas.setBitmap(null);
        this.themeLayout.setVisibility(0);
        this.closeImageView.setVisibility(0);
        this.logoImageView.setVisibility(0);
        ViewGroup viewGroup = (ViewGroup) this.fragmentView.getParent();
        this.fragmentView.layout(0, 0, viewGroup.getWidth(), viewGroup.getHeight());
        QrView qrView2 = this.qrView;
        if (qrView2 != null) {
            qrView2.setForShare(false);
        }
        Uri bitmapShareUri = AndroidUtilities.getBitmapShareUri(createBitmap, "qr_tmp.jpg", Bitmap.CompressFormat.JPEG);
        if (bitmapShareUri != null) {
            try {
                getParentActivity().startActivityForResult(Intent.createChooser(new Intent("android.intent.action.SEND").setType("image/*").putExtra("android.intent.extra.STREAM", bitmapShareUri), LocaleController.getString(R.string.InviteByQRCode)), 500);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$performShare$14();
            }
        }, 500L);
    }

    private boolean phoneIsPublic() {
        char c;
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(6);
        if (privacyRules == null) {
            return false;
        }
        for (int i = 0; i < privacyRules.size(); i++) {
            TLRPC.PrivacyRule privacyRule = privacyRules.get(i);
            if (privacyRule instanceof TLRPC.TL_privacyValueAllowAll) {
                c = 0;
                break;
            }
            if (privacyRule instanceof TLRPC.TL_privacyValueDisallowAll) {
                break;
            }
            if (privacyRule instanceof TLRPC.TL_privacyValueAllowContacts) {
                c = 1;
                break;
            }
        }
        c = 2;
        if (c == 2) {
            ArrayList<TLRPC.PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules2 == null || privacyRules2.size() == 0) {
                return true;
            }
            for (int i2 = 0; i2 < privacyRules2.size(); i2++) {
                TLRPC.PrivacyRule privacyRule2 = privacyRules2.get(i2);
                if (privacyRule2 instanceof TLRPC.TL_privacyValueAllowAll) {
                    return true;
                }
                if ((privacyRule2 instanceof TLRPC.TL_privacyValueDisallowAll) || (privacyRule2 instanceof TLRPC.TL_privacyValueAllowContacts)) {
                    return false;
                }
            }
        }
        return c == 0 || c == 1;
    }

    private void restoreScreenSettings() {
        if (getParentActivity() != null) {
            getParentActivity().getWindow().getDecorView().setSystemUiVisibility(this.prevSystemUiVisibility);
        }
    }

    @Override
    public View createView(Context context) {
        TLRPC.Chat chat;
        String publicUsername;
        String str;
        AvatarDrawable avatarDrawable;
        ImageLocation forChat;
        boolean z;
        ImageLocation forChat2;
        String str2;
        boolean z2;
        this.isCurrentThemeDark = Theme.getActiveTheme().isDark();
        boolean z3 = false;
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackground(null);
        this.actionBar.setItemsColor(-1, false);
        FrameLayout frameLayout = new FrameLayout(context) {
            private boolean prevIsPortrait;

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                super.dispatchTouchEvent(motionEvent);
                return true;
            }

            @Override
            protected void onLayout(boolean z4, int i, int i2, int i3, int i4) {
                boolean z5 = getWidth() < getHeight();
                QrActivity.this.backgroundView.layout(0, 0, getWidth(), getHeight());
                int measuredHeight = QrActivity.this.themeLayout.getVisibility() == 0 ? QrActivity.this.themeLayout.getMeasuredHeight() : 0;
                int width = getWidth();
                QrActivity qrActivity = QrActivity.this;
                if (!z5) {
                    width -= qrActivity.themeLayout.getMeasuredWidth();
                    qrActivity = QrActivity.this;
                }
                int measuredWidth = (width - qrActivity.qrView.getMeasuredWidth()) / 2;
                int height = getHeight();
                int measuredHeight2 = z5 ? ((((height - measuredHeight) - QrActivity.this.qrView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f)) / 2) + AndroidUtilities.dp(52.0f) : (height - QrActivity.this.qrView.getMeasuredHeight()) / 2;
                QrActivity.this.qrView.layout(measuredWidth, measuredHeight2, QrActivity.this.qrView.getMeasuredWidth() + measuredWidth, QrActivity.this.qrView.getMeasuredHeight() + measuredHeight2);
                if (z5) {
                    int width2 = (getWidth() - QrActivity.this.avatarImageView.getMeasuredWidth()) / 2;
                    int dp = measuredHeight2 - AndroidUtilities.dp(48.0f);
                    QrActivity.this.avatarImageView.layout(width2, dp, QrActivity.this.avatarImageView.getMeasuredWidth() + width2, QrActivity.this.avatarImageView.getMeasuredHeight() + dp);
                }
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    if (z5) {
                        int width3 = (getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) / 2;
                        QrActivity.this.themeLayout.layout(width3, i4 - measuredHeight, QrActivity.this.themeLayout.getMeasuredWidth() + width3, i4);
                    } else {
                        int height2 = (getHeight() - QrActivity.this.themeLayout.getMeasuredHeight()) / 2;
                        QrActivity.this.themeLayout.layout(i3 - QrActivity.this.themeLayout.getMeasuredWidth(), height2, i3, QrActivity.this.themeLayout.getMeasuredHeight() + height2);
                    }
                }
                QrActivity.this.logoImageView.layout(QrActivity.this.logoRect.left + measuredWidth, QrActivity.this.logoRect.top + measuredHeight2, measuredWidth + QrActivity.this.logoRect.right, measuredHeight2 + QrActivity.this.logoRect.bottom);
                int dp2 = AndroidUtilities.dp(z5 ? 14.0f : 17.0f);
                int dp3 = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(z5 ? 10.0f : 5.0f);
                QrActivity.this.closeImageView.layout(dp2, dp3, QrActivity.this.closeImageView.getMeasuredWidth() + dp2, QrActivity.this.closeImageView.getMeasuredHeight() + dp3);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                QrView qrView;
                int makeMeasureSpec;
                float f;
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                boolean z4 = size < size2;
                QrActivity.this.avatarImageView.setVisibility(z4 ? 0 : 8);
                super.onMeasure(i, i2);
                if (z4) {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
                    qrView = QrActivity.this.qrView;
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), 1073741824);
                    f = 330.0f;
                } else {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(256.0f), 1073741824), i2);
                    qrView = QrActivity.this.qrView;
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), 1073741824);
                    f = 310.0f;
                }
                qrView.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f), 1073741824));
                if (this.prevIsPortrait != z4) {
                    QrActivity.this.qrView.onSizeChanged(QrActivity.this.qrView.getMeasuredWidth(), QrActivity.this.qrView.getMeasuredHeight(), 0, 0);
                }
                this.prevIsPortrait = z4;
            }
        };
        View view = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(QrActivity.this.isCurrentThemeDark ? -15590870 : -6569073);
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.setBounds(0, 0, getWidth(), getHeight());
                }
                QrActivity.this.currMotionDrawable.setBounds(0, 0, getWidth(), getHeight());
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.drawBackground(canvas);
                }
                QrActivity.this.currMotionDrawable.drawBackground(canvas);
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.drawPattern(canvas);
                }
                QrActivity.this.currMotionDrawable.drawPattern(canvas);
                super.onDraw(canvas);
            }
        };
        this.backgroundView = view;
        frameLayout.addView(view);
        if (this.userId != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user != null) {
                publicUsername = UserObject.getPublicUsername(user);
                if (publicUsername == null) {
                    str = UserObject.getUserName(user);
                    if (phoneIsPublic()) {
                        publicUsername = user.phone;
                        if (publicUsername != null && !publicUsername.startsWith("+")) {
                            publicUsername = "+" + publicUsername;
                        }
                        z2 = true;
                    } else {
                        z2 = false;
                        z = true;
                        avatarDrawable = new AvatarDrawable(user);
                        forChat = ImageLocation.getForUser(user, 1);
                        forChat2 = ImageLocation.getForUser(user, 0);
                        z3 = z2;
                    }
                } else {
                    str = null;
                    z2 = false;
                }
                z = false;
                avatarDrawable = new AvatarDrawable(user);
                forChat = ImageLocation.getForUser(user, 1);
                forChat2 = ImageLocation.getForUser(user, 0);
                z3 = z2;
            }
            publicUsername = null;
            str = null;
            forChat2 = null;
            forChat = null;
            avatarDrawable = null;
            z = false;
        } else {
            if (this.chatId != 0 && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
                publicUsername = ChatObject.getPublicUsername(chat);
                str = null;
                avatarDrawable = new AvatarDrawable(chat);
                forChat = ImageLocation.getForChat(chat, 1);
                z = false;
                forChat2 = ImageLocation.getForChat(chat, 0);
            }
            publicUsername = null;
            str = null;
            forChat2 = null;
            forChat = null;
            avatarDrawable = null;
            z = false;
        }
        QrView qrView = new QrView(context);
        this.qrView = qrView;
        qrView.setColors(-9324972, -13856649, -6636738, -9915042);
        if (publicUsername != null) {
            str2 = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + publicUsername;
        } else {
            str2 = null;
        }
        QrView qrView2 = this.qrView;
        if (str != null) {
            publicUsername = str;
        }
        qrView2.setData(str2, publicUsername, z3, z);
        this.qrView.setCenterChangedListener(new QrView.QrCenterChangedListener() {
            @Override
            public final void onCenterChanged(int i, int i2, int i3, int i4) {
                QrActivity.this.lambda$createView$0(i, i2, i3, i4);
            }
        });
        frameLayout.addView(this.qrView);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.logoImageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.logoImageView.setAnimation(R.raw.qr_code_logo_2, 60, 60);
        this.logoImageView.playAnimation();
        frameLayout.addView(this.logoImageView);
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(42.0f));
        this.avatarImageView.setSize(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
        frameLayout.addView(this.avatarImageView, LayoutHelper.createFrame(84, 84, 51));
        this.avatarImageView.setImage(forChat2, "84_84", forChat, "50_50", avatarDrawable, (Bitmap) null, (String) null, 0, (Object) null);
        ImageView imageView = new ImageView(context);
        this.closeImageView = imageView;
        imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(34.0f), 671088640, 687865855));
        this.closeImageView.setImageResource(R.drawable.ic_ab_back);
        this.closeImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                QrActivity.this.lambda$createView$1(view2);
            }
        });
        frameLayout.addView(this.closeImageView, LayoutHelper.createFrame(34, 34.0f));
        this.emojiThemeIcon = Bitmap.createBitmap(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.emojiThemeIcon);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, 0.0f, this.emojiThemeIcon.getWidth(), this.emojiThemeIcon.getHeight());
        Paint paint = new Paint(1);
        paint.setColor(-1);
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawBitmap(BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), R.drawable.msg_qr_mini), (this.emojiThemeIcon.getWidth() - r3.getWidth()) * 0.5f, (this.emojiThemeIcon.getHeight() - r3.getHeight()) * 0.5f, paint);
        canvas.setBitmap(null);
        ThemeListViewController themeListViewController = new ThemeListViewController(this, getParentActivity().getWindow()) {
            @Override
            protected void setDarkTheme(boolean z4) {
                super.setDarkTheme(z4);
                QrActivity.this.isCurrentThemeDark = z4;
                QrActivity qrActivity = QrActivity.this;
                qrActivity.onItemSelected(qrActivity.currentTheme, QrActivity.this.selectedPosition, false);
            }
        };
        this.themesViewController = themeListViewController;
        this.themeLayout = themeListViewController.rootLayout;
        themeListViewController.onCreate();
        this.themesViewController.setItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public final void onItemSelected(EmojiThemes emojiThemes, int i) {
                QrActivity.this.lambda$createView$2(emojiThemes, i);
            }
        });
        this.themesViewController.titleView.setText(LocaleController.getString(R.string.QrCode));
        this.themesViewController.progressView.setViewType(17);
        this.themesViewController.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                QrActivity.this.lambda$createView$3(view2);
            }
        });
        frameLayout.addView(this.themeLayout, LayoutHelper.createFrame(-1, -2, 80));
        this.currMotionDrawable.setIndeterminateAnimation(true);
        this.fragmentView = frameLayout;
        Utilities.themeQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$createView$5();
            }
        }, 25L);
        this.fragmentView.postDelayed(new Runnable() {
            @Override
            public final void run() {
                QrActivity.this.lambda$createView$6();
            }
        }, firstOpen ? 250L : 0L);
        this.prevSystemUiVisibility = getParentActivity().getWindow().getDecorView().getSystemUiVisibility();
        applyScreenSettings();
        return this.fragmentView;
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList themeDescriptions = super.getThemeDescriptions();
        themeDescriptions.addAll(this.themesViewController.getThemeDescriptions());
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                QrActivity.this.lambda$getThemeDescriptions$15();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_featuredStickers_addButtonPressed));
        Iterator it = themeDescriptions.iterator();
        while (it.hasNext()) {
            ((ThemeDescription) it.next()).resourcesProvider = getResourceProvider();
        }
        return themeDescriptions;
    }

    @Override
    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id");
        this.chatId = this.arguments.getLong("chat_id");
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        this.themesViewController.onDestroy();
        this.themesViewController = null;
        this.emojiThemeIcon.recycle();
        this.emojiThemeIcon = null;
        for (int i = 0; i < this.emojiThemeDarkIcons.size(); i++) {
            Bitmap bitmap = (Bitmap) this.emojiThemeDarkIcons.valueAt(i);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.emojiThemeDarkIcons.clear();
        restoreScreenSettings();
        super.onFragmentDestroy();
    }

    @Override
    public void onPause() {
        restoreScreenSettings();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyScreenSettings();
    }
}
