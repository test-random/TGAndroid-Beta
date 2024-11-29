package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline2;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Text;
import org.telegram.ui.GradientClip;

public class BottomSheetTabsOverlay extends FrameLayout {
    private View actionBarLayout;
    private final AnimatedFloat animatedCount;
    private ValueAnimator animator;
    private Bitmap blurBitmap;
    private Paint blurBitmapPaint;
    private BitmapShader blurBitmapShader;
    private Matrix blurMatrix;
    private final Path clipPath;
    private final RectF clipRect;
    private Drawable closeAllButtonBackground;
    private boolean closeAllButtonBackgroundDark;
    private Text closeAllButtonText;
    private float dismissProgress;
    private Sheet dismissingSheet;
    private BottomSheetTabs.TabDrawable dismissingTab;
    private GradientClip gradientClip;
    private boolean hitCloseAllButton;
    private boolean horizontallySwiping;
    private boolean isOpen;
    private float lastY;
    private final int maximumVelocity;
    private final int minimumVelocity;
    public float offset;
    private ValueAnimator openAnimator;
    private float openProgress;
    private float openingProgress;
    private Sheet openingSheet;
    private BottomSheetTabs.TabDrawable openingTab;
    private float openingTabScroll;
    private final int[] pos;
    private final int[] pos2;
    private final int[] pos3;
    private TabPreview pressTab;
    private boolean pressTabClose;
    private final RectF rect;
    private final RectF rect2;
    private ValueAnimator scrollAnimator;
    private final OverScroller scroller;
    private boolean slowerDismiss;
    private long startTime;
    private float startX;
    private float startY;
    private final ArrayList tabs;
    private BottomSheetTabs tabsView;
    private final RectF tabsViewBounds;
    private VelocityTracker velocityTracker;
    private boolean verticallyScrolling;

    public class AnonymousClass2 extends AnimatorListenerAdapter {
        final Sheet val$sheet;
        final BottomSheetTabs.WebTabData val$tab;

        AnonymousClass2(BottomSheetTabs.WebTabData webTabData, Sheet sheet) {
            this.val$tab = webTabData;
            this.val$sheet = sheet;
        }

        public static void lambda$onAnimationEnd$0(BottomSheetTabs.WebTabData webTabData, Sheet sheet, Bitmap bitmap) {
            webTabData.previewBitmap = bitmap;
            sheet.mo1002getWindowView().setDrawingFromOverlay(false);
            sheet.release();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            int i;
            int i2;
            final BottomSheetTabs.WebTabData webTabData = this.val$tab;
            View view = webTabData.webView;
            if (view == null) {
                view = webTabData.view2;
            }
            if (view != null && webTabData.previewBitmap == null && (i = webTabData.viewWidth) > 0 && (i2 = webTabData.viewHeight) > 0) {
                if (Build.VERSION.SDK_INT >= 26) {
                    float f = -webTabData.viewScroll;
                    final Sheet sheet = this.val$sheet;
                    BottomSheetTabsOverlay.renderHardwareViewToBitmap(view, f, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            BottomSheetTabsOverlay.AnonymousClass2.lambda$onAnimationEnd$0(BottomSheetTabs.WebTabData.this, sheet, (Bitmap) obj);
                        }
                    });
                    BottomSheetTabsOverlay.this.dismissingSheet = null;
                    BottomSheetTabsOverlay.this.invalidate();
                }
                webTabData.previewBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(this.val$tab.previewBitmap);
                canvas.translate(0.0f, -this.val$tab.viewScroll);
                view.draw(canvas);
            }
            this.val$sheet.mo1002getWindowView().setDrawingFromOverlay(false);
            this.val$sheet.release();
            BottomSheetTabsOverlay.this.dismissingSheet = null;
            BottomSheetTabsOverlay.this.invalidate();
        }
    }

    public interface Sheet {

        public abstract class CC {
            public static void $default$setLastVisible(Sheet sheet, boolean z) {
            }
        }

        void dismiss(boolean z);

        int getNavigationBarColor(int i);

        SheetView mo1002getWindowView();

        boolean hadDialog();

        void release();

        BottomSheetTabs.WebTabData saveState();

        boolean setDialog(BottomSheetTabDialog bottomSheetTabDialog);

        void setLastVisible(boolean z);
    }

    public interface SheetView {
        float drawInto(Canvas canvas, RectF rectF, float f, RectF rectF2, float f2, boolean z);

        Context getContext();

        RectF getRect();

        void setDrawingFromOverlay(boolean z);
    }

    public static class TabPreview {
        private final Paint backgroundPaint;
        private final Paint bitmapPaint;
        public final ButtonBounce bounce;
        public final RectF clickBounds = new RectF();
        private final Path clipPath;
        private ValueAnimator dismissAnimator;
        public float dismissProgress;
        private final float[] dst;
        private final RadialGradient gradient;
        private final Matrix gradientMatrix;
        private final Paint gradientPaint;
        private final Matrix matrix;
        public final View parentView;
        private final Paint shadowPaint;
        private final float[] src;
        private final RectF tabBounds;
        public final BottomSheetTabs.WebTabData tabData;
        public final BottomSheetTabs.TabDrawable tabDrawable;
        public WebView webView;

        public TabPreview(View view, BottomSheetTabs.WebTabData webTabData, BottomSheetTabs.TabDrawable tabDrawable) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            this.matrix = new Matrix();
            this.src = new float[8];
            this.dst = new float[8];
            this.dismissProgress = 0.0f;
            this.shadowPaint = new Paint(1);
            this.tabBounds = new RectF();
            this.clipPath = new Path();
            this.bitmapPaint = new Paint(3);
            this.gradient = new RadialGradient(0.0f, 0.0f, 255.0f, new int[]{0, 805306368}, new float[]{0.5f, 1.0f}, Shader.TileMode.CLAMP);
            this.gradientMatrix = new Matrix();
            this.gradientPaint = new Paint(1);
            this.parentView = view;
            this.tabData = webTabData;
            this.tabDrawable = tabDrawable;
            this.webView = null;
            this.bounce = new ButtonBounce(view);
            paint.setColor(webTabData.backgroundColor);
        }

        public void lambda$animateDismiss$0(ValueAnimator valueAnimator) {
            this.dismissProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }

        public void animateDismiss(final float f) {
            cancelDismissAnimator();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.dismissProgress, f);
            this.dismissAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BottomSheetTabsOverlay.TabPreview.this.lambda$animateDismiss$0(valueAnimator);
                }
            });
            this.dismissAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    TabPreview tabPreview = TabPreview.this;
                    tabPreview.dismissProgress = f;
                    View view = tabPreview.parentView;
                    if (view != null) {
                        view.invalidate();
                    }
                }
            });
            if (Math.abs(f) < 0.1f) {
                AndroidUtilities.applySpring(this.dismissAnimator, 285.0d, 20.0d);
            } else {
                this.dismissAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            }
            this.dismissAnimator.start();
        }

        public void cancelDismissAnimator() {
            ValueAnimator valueAnimator = this.dismissAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }

        public void draw(Canvas canvas, RectF rectF, boolean z, float f, float f2, float f3, float f4) {
            float f5;
            float f6;
            Object obj;
            boolean hasDisplayList;
            int width;
            float clamp = f * Utilities.clamp(1.0f - ((Math.abs(this.dismissProgress) - 0.3f) / 0.7f), 1.0f, 0.0f);
            if (clamp <= 0.0f) {
                return;
            }
            float f7 = 1.0f - f3;
            float f8 = f2 * f7;
            float lerp = AndroidUtilities.lerp(1.0f, 1.3f, f8);
            float currentActionBarHeight = f3 * ((AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(50.0f));
            canvas.save();
            canvas.rotate(this.dismissProgress * 20.0f, rectF.centerX() + (AndroidUtilities.dp(50.0f) * this.dismissProgress), rectF.bottom + AndroidUtilities.dp(350.0f));
            float scale = this.bounce.getScale(0.01f);
            canvas.scale(scale, scale, rectF.centerX(), rectF.centerY());
            float lerp2 = AndroidUtilities.lerp(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f), f2);
            if (z) {
                this.shadowPaint.setColor(0);
                this.shadowPaint.setShadowLayer(AndroidUtilities.dp(30.0f), 0.0f, AndroidUtilities.dp(10.0f), Theme.multAlpha(536870912, clamp * f2 * f7));
                canvas.drawRoundRect(rectF, lerp2, lerp2, this.shadowPaint);
                this.backgroundPaint.setAlpha((int) (clamp * 255.0f));
                canvas.drawRoundRect(rectF, lerp2, lerp2, this.backgroundPaint);
            } else {
                this.clipPath.rewind();
                this.clipPath.addRoundRect(rectF, lerp2, lerp2, Path.Direction.CW);
                canvas.save();
                this.shadowPaint.setColor(0);
                float f9 = clamp * f2;
                this.shadowPaint.setShadowLayer(AndroidUtilities.dp(30.0f), 0.0f, AndroidUtilities.dp(10.0f), Theme.multAlpha(536870912, f9 * f7));
                canvas.drawPath(this.clipPath, this.shadowPaint);
                canvas.clipPath(this.clipPath);
                float f10 = clamp * 255.0f * f2;
                int i = (int) f10;
                this.backgroundPaint.setAlpha(i);
                canvas.drawRoundRect(rectF, lerp2, lerp2, this.backgroundPaint);
                canvas.save();
                canvas.translate(rectF.left, rectF.top + (AndroidUtilities.dp(50.0f) * lerp) + currentActionBarHeight);
                canvas.scale(1.0f, AndroidUtilities.lerp(1.0f, 1.25f, f8));
                BottomSheetTabs.WebTabData webTabData = this.tabData;
                if (webTabData != null && (obj = webTabData.previewNode) != null && Build.VERSION.SDK_INT >= 29) {
                    hasDisplayList = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(obj).hasDisplayList();
                    if (hasDisplayList) {
                        RenderNode m = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(this.tabData.previewNode);
                        float width2 = rectF.width();
                        width = m.getWidth();
                        float f11 = width2 / width;
                        canvas.scale(f11, f11);
                        m.setAlpha(f9);
                        canvas.drawRenderNode(m);
                        f5 = lerp2;
                        f6 = currentActionBarHeight;
                        canvas.restore();
                        canvas.save();
                        this.gradientPaint.setAlpha((int) (f10 * f7));
                        this.gradientMatrix.reset();
                        float height = rectF.height() / 255.0f;
                        this.gradientMatrix.postScale(height, height);
                        this.gradientMatrix.postTranslate(rectF.centerX(), rectF.top);
                        this.gradient.setLocalMatrix(this.gradientMatrix);
                        this.gradientPaint.setShader(this.gradient);
                        canvas.drawRect(rectF, this.gradientPaint);
                        canvas.restore();
                        this.tabBounds.set(rectF);
                        RectF rectF2 = this.tabBounds;
                        rectF2.bottom = rectF2.top + Math.min(rectF.height(), AndroidUtilities.dp(50.0f));
                        this.tabBounds.offset(0.0f, f6);
                        this.tabDrawable.setExpandProgress(f2);
                        canvas.scale(1.0f, lerp, this.tabBounds.centerX(), this.tabBounds.top);
                        this.tabDrawable.draw(canvas, this.tabBounds, f5, clamp * clamp, f4);
                        canvas.restore();
                    }
                }
                BottomSheetTabs.WebTabData webTabData2 = this.tabData;
                if (webTabData2 != null && webTabData2.previewBitmap != null) {
                    float width3 = rectF.width() / this.tabData.previewBitmap.getWidth();
                    canvas.scale(width3, width3);
                    this.bitmapPaint.setAlpha(i);
                    canvas.drawBitmap(this.tabData.previewBitmap, 0.0f, 0.0f, this.bitmapPaint);
                } else if (this.webView != null) {
                    float width4 = rectF.width() / this.webView.getWidth();
                    canvas.scale(width4, width4);
                    f5 = lerp2;
                    f6 = currentActionBarHeight;
                    canvas.saveLayerAlpha(0.0f, 0.0f, this.webView.getWidth(), this.webView.getHeight(), i, 31);
                    this.webView.draw(canvas);
                    canvas.restore();
                    canvas.restore();
                    canvas.save();
                    this.gradientPaint.setAlpha((int) (f10 * f7));
                    this.gradientMatrix.reset();
                    float height2 = rectF.height() / 255.0f;
                    this.gradientMatrix.postScale(height2, height2);
                    this.gradientMatrix.postTranslate(rectF.centerX(), rectF.top);
                    this.gradient.setLocalMatrix(this.gradientMatrix);
                    this.gradientPaint.setShader(this.gradient);
                    canvas.drawRect(rectF, this.gradientPaint);
                    canvas.restore();
                    this.tabBounds.set(rectF);
                    RectF rectF22 = this.tabBounds;
                    rectF22.bottom = rectF22.top + Math.min(rectF.height(), AndroidUtilities.dp(50.0f));
                    this.tabBounds.offset(0.0f, f6);
                    this.tabDrawable.setExpandProgress(f2);
                    canvas.scale(1.0f, lerp, this.tabBounds.centerX(), this.tabBounds.top);
                    this.tabDrawable.draw(canvas, this.tabBounds, f5, clamp * clamp, f4);
                    canvas.restore();
                }
                f5 = lerp2;
                f6 = currentActionBarHeight;
                canvas.restore();
                canvas.save();
                this.gradientPaint.setAlpha((int) (f10 * f7));
                this.gradientMatrix.reset();
                float height22 = rectF.height() / 255.0f;
                this.gradientMatrix.postScale(height22, height22);
                this.gradientMatrix.postTranslate(rectF.centerX(), rectF.top);
                this.gradient.setLocalMatrix(this.gradientMatrix);
                this.gradientPaint.setShader(this.gradient);
                canvas.drawRect(rectF, this.gradientPaint);
                canvas.restore();
                this.tabBounds.set(rectF);
                RectF rectF222 = this.tabBounds;
                rectF222.bottom = rectF222.top + Math.min(rectF.height(), AndroidUtilities.dp(50.0f));
                this.tabBounds.offset(0.0f, f6);
                this.tabDrawable.setExpandProgress(f2);
                canvas.scale(1.0f, lerp, this.tabBounds.centerX(), this.tabBounds.top);
                this.tabDrawable.draw(canvas, this.tabBounds, f5, clamp * clamp, f4);
                canvas.restore();
            }
            canvas.restore();
        }

        public boolean isPressed() {
            return this.bounce.isPressed();
        }

        public void setPressed(boolean z) {
            this.bounce.setPressed(z);
        }
    }

    public BottomSheetTabsOverlay(Context context) {
        super(context);
        this.animatedCount = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.tabsViewBounds = new RectF();
        this.tabs = new ArrayList();
        this.pos = new int[2];
        this.pos2 = new int[2];
        this.pos3 = new int[2];
        this.rect = new RectF();
        this.rect2 = new RectF();
        this.clipRect = new RectF();
        this.clipPath = new Path();
        setWillNotDraw(false);
        this.scroller = new OverScroller(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.maximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.minimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    }

    private void animateOpen(boolean z) {
        if (this.isOpen == z) {
            return;
        }
        ValueAnimator valueAnimator = this.openAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.isOpen = z;
        BottomSheetTabs bottomSheetTabs = this.tabsView;
        if (bottomSheetTabs != null) {
            bottomSheetTabs.drawTabs = false;
            bottomSheetTabs.invalidate();
        }
        invalidate();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.openProgress, z ? 1.0f : 0.0f);
        this.openAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BottomSheetTabsOverlay.this.lambda$animateOpen$6(valueAnimator2);
            }
        });
        this.openAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (BottomSheetTabsOverlay.this.tabsView != null) {
                    BottomSheetTabsOverlay.this.tabsView.drawTabs = true;
                    BottomSheetTabsOverlay.this.tabsView.invalidate();
                }
                BottomSheetTabsOverlay bottomSheetTabsOverlay = BottomSheetTabsOverlay.this;
                bottomSheetTabsOverlay.openProgress = bottomSheetTabsOverlay.isOpen ? 1.0f : 0.0f;
                BottomSheetTabsOverlay.this.invalidate();
                if (BottomSheetTabsOverlay.this.isOpen || BottomSheetTabsOverlay.this.openingSheet != null) {
                    return;
                }
                BottomSheetTabsOverlay.this.clearTabs();
            }
        });
        this.openAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.openAnimator.setDuration(320L);
        this.openAnimator.start();
    }

    public void clearTabs() {
        this.tabs.clear();
    }

    private void drawDismissingTab(Canvas canvas) {
        if (this.dismissingSheet != null) {
            getLocationOnScreen(this.pos2);
            this.tabsView.getLocationOnScreen(this.pos);
            this.tabsView.getTabBounds(this.rect, 0.0f);
            RectF rectF = this.rect;
            int i = this.pos[0];
            int[] iArr = this.pos2;
            rectF.offset(i - iArr[0], r1[1] - iArr[1]);
            SheetView mo1002getWindowView = this.dismissingSheet.mo1002getWindowView();
            RectF rectF2 = this.rect;
            float f = this.dismissProgress;
            float drawInto = mo1002getWindowView.drawInto(canvas, rectF2, f, this.clipRect, f, false);
            if (this.dismissingTab != null) {
                this.clipPath.rewind();
                this.clipPath.addRoundRect(this.clipRect, drawInto, drawInto, Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.clipPath);
                float dp = this.clipRect.top - (AndroidUtilities.dp(50.0f) * (1.0f - this.dismissProgress));
                RectF rectF3 = this.rect;
                RectF rectF4 = this.clipRect;
                rectF3.set(rectF4.left, dp, rectF4.right, AndroidUtilities.dp(50.0f) + dp);
                this.tabsView.setupTab(this.dismissingTab);
                this.dismissingTab.draw(canvas, this.rect, drawInto, this.dismissProgress, 1.0f);
                canvas.restore();
            }
        }
    }

    private void drawTabsPreview(android.graphics.Canvas r38) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheetTabsOverlay.drawTabsPreview(android.graphics.Canvas):void");
    }

    private float getScrollStep() {
        return AndroidUtilities.dp(200.0f);
    }

    private TabPreview getTabAt(float f, float f2) {
        if (this.openProgress < 1.0f) {
            return null;
        }
        for (int size = this.tabs.size() - 1; size >= 0; size--) {
            TabPreview tabPreview = (TabPreview) this.tabs.get(size);
            if (Math.abs(tabPreview.dismissProgress) < 0.4f && tabPreview.clickBounds.contains(f, f2)) {
                return tabPreview;
            }
        }
        return null;
    }

    public void lambda$animateOpen$6(ValueAnimator valueAnimator) {
        this.openProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public static void lambda$dismissSheet$3(Sheet sheet) {
        if (sheet == null || sheet.mo1002getWindowView() == null) {
            return;
        }
        sheet.mo1002getWindowView().setDrawingFromOverlay(true);
    }

    public void lambda$dismissSheet$4(ValueAnimator valueAnimator) {
        this.dismissProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$dispatchTouchEvent$0(TabPreview tabPreview, Boolean bool) {
        if (!bool.booleanValue()) {
            tabPreview.animateDismiss(0.0f);
            return;
        }
        tabPreview.animateDismiss(tabPreview.dismissProgress < 0.0f ? -1.0f : 1.0f);
        scrollTo(Utilities.clamp(this.offset, getScrollMax(false), getScrollMin(false)));
        if (this.tabsView.getTabs().isEmpty()) {
            closeTabsView();
        }
    }

    public void lambda$dispatchTouchEvent$1(TabPreview tabPreview, Boolean bool) {
        if (!bool.booleanValue()) {
            tabPreview.animateDismiss(0.0f);
            return;
        }
        tabPreview.animateDismiss(1.0f);
        scrollTo(Utilities.clamp(this.offset, getScrollMax(false), getScrollMin(false)));
        if (this.tabsView.getTabs().isEmpty()) {
            closeTabsView();
        }
    }

    public void lambda$scrollTo$5(ValueAnimator valueAnimator) {
        this.offset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    private void prepareBlur(View view) {
        AndroidUtilities.makingGlobalBlurBitmap = true;
        this.blurBitmap = AndroidUtilities.makeBlurBitmap(view, 14.0f, 14);
        AndroidUtilities.makingGlobalBlurBitmap = false;
        Paint paint = new Paint(1);
        this.blurBitmapPaint = paint;
        Bitmap bitmap = this.blurBitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
        this.blurBitmapShader = bitmapShader;
        paint.setShader(bitmapShader);
        ColorMatrix colorMatrix = new ColorMatrix();
        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, Theme.isCurrentThemeDark() ? 0.08f : 0.25f);
        this.blurBitmapPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        this.blurMatrix = new Matrix();
    }

    private void prepareTabs() {
        BottomSheetTabs.TabDrawable tabDrawable;
        ArrayList<BottomSheetTabs.WebTabData> tabs = this.tabsView.getTabs();
        ArrayList<BottomSheetTabs.TabDrawable> tabDrawables = this.tabsView.getTabDrawables();
        for (int size = tabs.size() - 1; size >= 0; size--) {
            BottomSheetTabs.WebTabData webTabData = tabs.get(size);
            int i = 0;
            while (true) {
                if (i >= tabDrawables.size()) {
                    tabDrawable = null;
                    break;
                }
                tabDrawable = tabDrawables.get(i);
                if (tabDrawable.tab == webTabData) {
                    break;
                } else {
                    i++;
                }
            }
            if (tabDrawable != null) {
                this.tabs.add(new TabPreview(this, webTabData, tabDrawable));
            }
        }
        this.animatedCount.set(this.tabs.size(), true);
        setScrollOffset(getScrollMax());
    }

    public static void renderHardwareViewToBitmap(View view, float f, final Utilities.Callback callback) {
        Canvas lockHardwareCanvas;
        if (view == null || callback == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
            if (callback != null) {
                callback.run(null);
                return;
            }
            return;
        }
        final SurfaceTexture surfaceTexture = new SurfaceTexture(false);
        surfaceTexture.setDefaultBufferSize(view.getWidth(), view.getHeight());
        final Surface surface = new Surface(surfaceTexture);
        final Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        lockHardwareCanvas = surface.lockHardwareCanvas();
        lockHardwareCanvas.translate(0.0f, f);
        view.draw(lockHardwareCanvas);
        surface.unlockCanvasAndPost(lockHardwareCanvas);
        PixelCopy.request(surface, createBitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int i) {
                Utilities.Callback callback2;
                Bitmap bitmap;
                if (i == 0) {
                    callback2 = Utilities.Callback.this;
                    bitmap = createBitmap;
                } else {
                    createBitmap.recycle();
                    callback2 = Utilities.Callback.this;
                    bitmap = null;
                }
                callback2.run(bitmap);
                surface.release();
                surfaceTexture.release();
            }
        }, new Handler());
    }

    private void scrollTo(float f) {
        ValueAnimator valueAnimator = this.scrollAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.scrollAnimator = null;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.offset, f);
        this.scrollAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BottomSheetTabsOverlay.this.lambda$scrollTo$5(valueAnimator2);
            }
        });
        this.scrollAnimator.setDuration(250L);
        this.scrollAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.scrollAnimator.start();
    }

    public void closeTabsView() {
        animateOpen(false);
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            setScrollOffset(this.scroller.getCurrY() / getScrollStep());
            postInvalidateOnAnimation();
        }
    }

    public boolean dismissSheet(final Sheet sheet) {
        ValueAnimator valueAnimator;
        if (sheet == null || this.tabsView == null) {
            return false;
        }
        if ((this.dismissingSheet != null || this.openingSheet != null) && (valueAnimator = this.animator) != null) {
            valueAnimator.end();
            this.animator = null;
        }
        this.dismissingSheet = sheet;
        sheet.setLastVisible(false);
        ValueAnimator valueAnimator2 = this.animator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        BottomSheetTabs.WebTabData saveState = sheet.saveState();
        this.dismissingTab = this.tabsView.pushTab(saveState);
        post(new Runnable() {
            @Override
            public final void run() {
                BottomSheetTabsOverlay.lambda$dismissSheet$3(BottomSheetTabsOverlay.Sheet.this);
            }
        });
        invalidate();
        this.dismissProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                BottomSheetTabsOverlay.this.lambda$dismissSheet$4(valueAnimator3);
            }
        });
        this.animator.addListener(new AnonymousClass2(saveState, sheet));
        AndroidUtilities.applySpring(this.animator, 220.0d, 30.0d, 1.0d);
        this.animator.setDuration(((float) r12.getDuration()) * 1.1f);
        this.animator.start();
        this.slowerDismiss = false;
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawDismissingTab(canvas);
        drawTabsPreview(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheetTabsOverlay.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        return false;
    }

    public float getScrollMax() {
        return getScrollMax(true);
    }

    public float getScrollMax(boolean z) {
        return (getScrollRange(z) - getScrollWindow(z)) - ((getScrollWindow(z) / 3.0f) * Utilities.clamp(4.0f - getScrollRange(z), 0.5f, 0.0f));
    }

    public float getScrollMin() {
        return getScrollMin(true);
    }

    public float getScrollMin(boolean z) {
        return ((-getScrollWindow()) / 3.0f) * Utilities.clamp(getScrollRange(z), 1.0f, 0.0f);
    }

    public float getScrollOffset() {
        return this.offset;
    }

    public float getScrollRange() {
        return getScrollRange(true);
    }

    public float getScrollRange(boolean z) {
        float f = 0.0f;
        for (int i = 0; i < this.tabs.size(); i++) {
            f += ((TabPreview) this.tabs.get(i)).tabDrawable.index >= 0 ? 1.0f : 0.0f;
        }
        return z ? this.animatedCount.set(f) : f;
    }

    public float getScrollWindow() {
        return Math.min(3.0f, getScrollRange());
    }

    public float getScrollWindow(boolean z) {
        return Math.min(3.0f, getScrollRange(z));
    }

    public boolean onBackPressed() {
        if (!this.isOpen) {
            return false;
        }
        closeTabsView();
        return true;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2) + AndroidUtilities.navigationBarHeight, 1073741824));
    }

    public void openTabsView() {
        BottomSheetTabs bottomSheetTabs = this.tabsView;
        if (bottomSheetTabs == null || !(bottomSheetTabs.getParent() instanceof View)) {
            return;
        }
        stopAnimations();
        View view = (View) this.tabsView.getParent();
        this.actionBarLayout = view;
        if (view != null) {
            view.getLocationOnScreen(this.pos);
        } else {
            int[] iArr = this.pos;
            iArr[1] = 0;
            iArr[0] = 0;
        }
        getLocationOnScreen(this.pos2);
        this.tabsViewBounds.set(this.pos[0] - this.pos2[0], r3[1] - r5[1], r4 + this.actionBarLayout.getWidth(), (this.pos[1] - this.pos2[1]) + this.actionBarLayout.getHeight());
        prepareBlur(this.actionBarLayout);
        clearTabs();
        prepareTabs();
        animateOpen(true);
    }

    public void setScrollOffset(float f) {
        this.offset = f;
    }

    public void setSlowerDismiss(boolean z) {
        this.slowerDismiss = z;
    }

    public void setTabsView(BottomSheetTabs bottomSheetTabs) {
        this.tabsView = bottomSheetTabs;
    }

    public void stopAnimations() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.animator = null;
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.closeAllButtonBackground || super.verifyDrawable(drawable);
    }
}
