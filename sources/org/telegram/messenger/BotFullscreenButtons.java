package org.telegram.messenger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import org.telegram.messenger.BotFullscreenButtons;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Text;
import org.telegram.ui.GradientClip;

public class BotFullscreenButtons extends View {
    private final AnimatedFloat animatedBack;
    private final AnimatedFloat animatedDownloading;
    private final AnimatedFloat animatedPreview;
    private boolean back;
    private final Text backText;
    private final Paint backgroundPaint;
    private final Path backgroundPath;
    private RenderNode blurNode;
    private final ButtonBounce closeBounce;
    private final RectF closeRect;
    private final RectF closeRectArea;
    private final Text closeText;
    private final ButtonBounce collapseBounce;
    private final RectF collapseClickRect;
    private final RectF collapseRect;
    private final Paint downloadPaint;
    private final Path downloadPath;
    private boolean downloading;
    private final Runnable hidePreview;
    private final Paint iconPaint;
    private final Paint iconStrokePaint;
    private final RectF insets;
    private final RectF leftMenu;
    private final ButtonBounce menuBounce;
    private final RectF menuClickRect;
    private final RectF menuRect;
    private final ButtonBounce nullBounce;
    public Runnable onCloseClickListener;
    public Runnable onCollapseClickListener;
    public Runnable onMenuClickListener;
    public Object parentRenderNode;
    int pressed;
    private boolean preview;
    private final GradientClip previewClip;
    private Text previewText;
    private final RectF rightMenu;
    private final long start;
    private Drawable verifiedBackground;
    private Drawable verifiedForeground;
    public WebView webView;

    public static class OptionsIcon extends Drawable {
        private final AnimatedFloat animatedDownloading;
        private final Paint downloadPaint;
        private final Path downloadPath;
        private boolean downloading;
        private final Drawable drawable;
        private final long start;

        public OptionsIcon(Context context) {
            Paint paint = new Paint(1);
            this.downloadPaint = paint;
            Path path = new Path();
            this.downloadPath = path;
            this.downloading = false;
            this.animatedDownloading = new AnimatedFloat(new Runnable() {
                @Override
                public final void run() {
                    BotFullscreenButtons.OptionsIcon.this.invalidateSelf();
                }
            }, 0L, 420L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.start = System.currentTimeMillis();
            this.drawable = context.getResources().getDrawable(R.drawable.ic_ab_other).mutate();
            paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(1.0f)));
            path.rewind();
            path.moveTo(-AndroidUtilities.dpf2(1.33f), AndroidUtilities.dpf2(0.16f));
            path.lineTo(-AndroidUtilities.dpf2(1.33f), -AndroidUtilities.dpf2(3.5f));
            path.lineTo(AndroidUtilities.dpf2(1.33f), -AndroidUtilities.dpf2(3.5f));
            path.lineTo(AndroidUtilities.dpf2(1.33f), AndroidUtilities.dpf2(0.16f));
            path.lineTo(AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(0.16f));
            path.lineTo(0.0f, AndroidUtilities.dpf2(3.5f));
            path.lineTo(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(0.16f));
            path.close();
        }

        @Override
        public void draw(Canvas canvas) {
            this.drawable.setBounds(getBounds());
            this.drawable.draw(canvas);
            float f = this.animatedDownloading.set(this.downloading);
            if (f > 0.0f) {
                canvas.save();
                canvas.translate(getBounds().centerX(), getBounds().centerY());
                canvas.translate(-AndroidUtilities.dpf2(8.166f), AndroidUtilities.dpf2(5.0f));
                float f2 = (f * 0.5f) + 0.5f;
                canvas.scale(f2, f2);
                this.downloadPaint.setColor(Theme.multAlpha(-1, 0.4f));
                canvas.drawPath(this.downloadPath, this.downloadPaint);
                float currentTimeMillis = ((float) ((System.currentTimeMillis() - this.start) % 450)) / 450.0f;
                float f3 = 0.5f + currentTimeMillis;
                canvas.save();
                canvas.clipRect(-AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), currentTimeMillis), AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), f3));
                this.downloadPaint.setColor(Theme.multAlpha(-1, 1.0f));
                canvas.drawPath(this.downloadPath, this.downloadPaint);
                canvas.restore();
                if (f3 > 1.0f) {
                    canvas.save();
                    canvas.clipRect(-AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), 0.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), f3 - 1.0f));
                    this.downloadPaint.setColor(Theme.multAlpha(-1, 1.0f));
                    canvas.drawPath(this.downloadPath, this.downloadPaint);
                    canvas.restore();
                }
                canvas.restore();
                invalidateSelf();
            }
        }

        @Override
        public int getIntrinsicHeight() {
            return this.drawable.getIntrinsicHeight();
        }

        @Override
        public int getIntrinsicWidth() {
            return this.drawable.getIntrinsicWidth();
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.drawable.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.downloadPaint.setColorFilter(colorFilter);
            this.drawable.setColorFilter(colorFilter);
        }

        public void setDownloading(boolean z) {
            if (this.downloading == z) {
                return;
            }
            this.downloading = z;
            invalidateSelf();
        }
    }

    public BotFullscreenButtons(Context context) {
        super(context);
        this.backgroundPaint = new Paint(1);
        this.iconPaint = new Paint(1);
        Paint paint = new Paint(1);
        this.iconStrokePaint = paint;
        this.backgroundPath = new Path();
        Paint paint2 = new Paint(1);
        this.downloadPaint = paint2;
        Path path = new Path();
        this.downloadPath = path;
        this.insets = new RectF();
        this.leftMenu = new RectF();
        this.nullBounce = new ButtonBounce(null);
        this.closeRect = new RectF();
        this.closeRectArea = new RectF();
        this.closeBounce = new ButtonBounce(this);
        this.rightMenu = new RectF();
        this.collapseRect = new RectF();
        this.collapseClickRect = new RectF();
        this.collapseBounce = new ButtonBounce(this);
        this.menuRect = new RectF();
        this.menuClickRect = new RectF();
        this.menuBounce = new ButtonBounce(this);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatedBack = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
        this.preview = true;
        this.animatedPreview = new AnimatedFloat(this, 0L, 420L, cubicBezierInterpolator);
        this.downloading = false;
        this.animatedDownloading = new AnimatedFloat(this, 0L, 420L, cubicBezierInterpolator);
        this.previewClip = new GradientClip();
        this.hidePreview = new Runnable() {
            @Override
            public final void run() {
                BotFullscreenButtons.this.lambda$new$0();
            }
        };
        this.start = System.currentTimeMillis();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        this.backText = new Text(LocaleController.getString(R.string.BotFullscreenBack), 13.0f, AndroidUtilities.bold());
        this.closeText = new Text(LocaleController.getString(R.string.BotFullscreenClose), 13.0f, AndroidUtilities.bold());
        paint2.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(1.0f)));
        path.rewind();
        path.moveTo(-AndroidUtilities.dpf2(1.33f), AndroidUtilities.dpf2(0.16f));
        path.lineTo(-AndroidUtilities.dpf2(1.33f), -AndroidUtilities.dpf2(3.5f));
        path.lineTo(AndroidUtilities.dpf2(1.33f), -AndroidUtilities.dpf2(3.5f));
        path.lineTo(AndroidUtilities.dpf2(1.33f), AndroidUtilities.dpf2(0.16f));
        path.lineTo(AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(0.16f));
        path.lineTo(0.0f, AndroidUtilities.dpf2(3.5f));
        path.lineTo(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(0.16f));
        path.close();
    }

    private ButtonBounce getBounce(int i) {
        return i != 1 ? i != 2 ? i != 3 ? this.nullBounce : this.menuBounce : this.collapseBounce : this.closeBounce;
    }

    private int getButton(MotionEvent motionEvent) {
        if (this.closeRectArea.contains(motionEvent.getX(), motionEvent.getY())) {
            return 1;
        }
        if (this.collapseClickRect.contains(motionEvent.getX(), motionEvent.getY())) {
            return 2;
        }
        return this.menuClickRect.contains(motionEvent.getX(), motionEvent.getY()) ? 3 : 0;
    }

    public void lambda$new$0() {
        setPreview(false, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float currentWidth;
        float f;
        float f2;
        WebView webView;
        int width;
        int height;
        RecordingCanvas beginRecording;
        RenderEffect createBlurEffect;
        super.onDraw(canvas);
        this.iconPaint.setColor(-1);
        this.iconStrokePaint.setColor(-1);
        this.iconStrokePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.backgroundPath.rewind();
        this.rightMenu.set((getWidth() - this.insets.right) - AndroidUtilities.dp(79.66f), this.insets.top + AndroidUtilities.dp(8.0f), (getWidth() - this.insets.right) - AndroidUtilities.dp(8.0f), this.insets.top + AndroidUtilities.dp(38.0f));
        RectF rectF = this.collapseRect;
        RectF rectF2 = this.rightMenu;
        rectF.set(rectF2.left, rectF2.top, rectF2.centerX(), this.rightMenu.bottom);
        RectF rectF3 = this.collapseClickRect;
        float dp = this.collapseRect.left - AndroidUtilities.dp(8.0f);
        float dp2 = this.collapseRect.top - AndroidUtilities.dp(8.0f);
        RectF rectF4 = this.collapseRect;
        rectF3.set(dp, dp2, rectF4.right, rectF4.bottom + AndroidUtilities.dp(8.0f));
        RectF rectF5 = this.menuRect;
        float centerX = this.rightMenu.centerX();
        RectF rectF6 = this.rightMenu;
        rectF5.set(centerX, rectF6.top, rectF6.right, rectF6.bottom);
        RectF rectF7 = this.menuClickRect;
        RectF rectF8 = this.menuRect;
        rectF7.set(rectF8.left, rectF8.top - AndroidUtilities.dp(8.0f), this.menuRect.right + AndroidUtilities.dp(8.0f), this.menuRect.bottom + AndroidUtilities.dp(8.0f));
        Path path = this.backgroundPath;
        RectF rectF9 = this.rightMenu;
        float dp3 = AndroidUtilities.dp(15.0f);
        float dp4 = AndroidUtilities.dp(15.0f);
        Path.Direction direction = Path.Direction.CW;
        path.addRoundRect(rectF9, dp3, dp4, direction);
        float f3 = this.animatedBack.set(this.back);
        float f4 = this.animatedPreview.set(this.preview);
        float dp5 = (this.rightMenu.left - AndroidUtilities.dp(18.0f)) - (this.insets.left + AndroidUtilities.dp(38.0f));
        Text text = this.previewText;
        if (text == null) {
            currentWidth = 0.0f;
        } else {
            currentWidth = text.getCurrentWidth() + AndroidUtilities.dp(this.verifiedBackground != null ? 30.0f : 12.0f);
        }
        float min = Math.min(dp5, currentWidth);
        this.leftMenu.set(this.insets.left + AndroidUtilities.dp(8.0f), this.insets.top + AndroidUtilities.dp(8.0f), this.insets.left + AndroidUtilities.dp(38.0f) + AndroidUtilities.lerp(AndroidUtilities.lerp(this.closeText.getCurrentWidth(), this.backText.getCurrentWidth(), f3) + AndroidUtilities.dp(12.0f), min, f4), this.insets.top + AndroidUtilities.dp(38.0f));
        RectF rectF10 = this.closeRect;
        RectF rectF11 = this.leftMenu;
        float f5 = rectF11.left;
        rectF10.set(f5, rectF11.top, AndroidUtilities.dp(30.0f) + f5, this.leftMenu.bottom);
        this.closeRectArea.set(this.closeRect);
        this.closeRectArea.right = AndroidUtilities.lerp(this.leftMenu.right, this.closeRect.left + AndroidUtilities.dp(30.0f), f4);
        this.closeRectArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
        this.backgroundPath.addRoundRect(this.leftMenu, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), direction);
        if (this.parentRenderNode == null || Build.VERSION.SDK_INT < 31 || !canvas.isHardwareAccelerated() || !((webView = this.webView) == null || webView.getLayerType() == 2)) {
            this.backgroundPaint.setColor(Theme.multAlpha(-16777216, 0.35f));
            canvas.drawPath(this.backgroundPath, this.backgroundPaint);
        } else {
            if (this.blurNode == null) {
                RenderNode renderNode = new RenderNode("bot_fullscreen_blur");
                this.blurNode = renderNode;
                createBlurEffect = RenderEffect.createBlurEffect(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Shader.TileMode.CLAMP);
                renderNode.setRenderEffect(createBlurEffect);
            }
            RenderNode m = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(this.parentRenderNode);
            width = m.getWidth();
            int max = Math.max(1, width - AndroidUtilities.dp(16.0f));
            float dp6 = this.insets.top + AndroidUtilities.dp(46.0f);
            height = m.getHeight();
            this.blurNode.setPosition(0, 0, max, Math.max(1, (int) Math.min(dp6, height)));
            beginRecording = this.blurNode.beginRecording();
            beginRecording.translate(-AndroidUtilities.dp(8.0f), 0.0f);
            beginRecording.drawRenderNode(m);
            this.blurNode.endRecording();
            canvas.save();
            canvas.clipPath(this.backgroundPath);
            canvas.save();
            canvas.translate(AndroidUtilities.dp(8.0f), 0.0f);
            canvas.drawRenderNode(this.blurNode);
            canvas.restore();
            this.backgroundPaint.setColor(Theme.multAlpha(-16777216, 0.22f));
            canvas.drawPaint(this.backgroundPaint);
            canvas.restore();
        }
        canvas.save();
        canvas.translate(this.closeRect.centerX(), this.closeRect.centerY());
        float scale = this.closeBounce.getScale(0.1f);
        canvas.scale(scale, scale);
        canvas.translate((-AndroidUtilities.dp(6.5f)) * f3, 0.0f);
        float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(4.66f), AndroidUtilities.dp(5.5f), f3);
        float f6 = -lerp;
        canvas.drawLine(AndroidUtilities.lerp(f6, 0.0f, f3), AndroidUtilities.lerp(f6, 0.0f, f3), lerp, lerp, this.iconStrokePaint);
        canvas.drawLine(AndroidUtilities.lerp(f6, 0.0f, f3), AndroidUtilities.lerp(lerp, 0.0f, f3), lerp, f6, this.iconStrokePaint);
        if (f3 > 0.0f) {
            canvas.drawLine(0.0f, 0.0f, AndroidUtilities.dp(11.6f) * f3, 0.0f, this.iconStrokePaint);
        }
        canvas.restore();
        float dp7 = (this.leftMenu.left + AndroidUtilities.dp(30.0f)) - AndroidUtilities.dp(10.0f);
        RectF rectF12 = this.leftMenu;
        canvas.saveLayerAlpha(dp7, rectF12.top, rectF12.right, rectF12.bottom, 255, 31);
        if (f4 <= 0.0f || this.previewText == null) {
            f = 1.0f;
        } else {
            canvas.save();
            canvas.translate((this.leftMenu.left + AndroidUtilities.dp(30.0f)) - (min * (1.0f - f4)), this.leftMenu.centerY());
            f = 1.0f;
            this.previewText.ellipsize(((this.leftMenu.right - AndroidUtilities.dp(this.verifiedBackground != null ? 30.0f : 12.0f)) - (this.leftMenu.left + AndroidUtilities.dp(30.0f))) + 2.0f).draw(canvas, 0.0f, 0.0f, -1, f4);
            canvas.translate(this.previewText.getWidth() + AndroidUtilities.dp(5.0f), 0.0f);
            int dp8 = AndroidUtilities.dp(16.0f);
            Drawable drawable = this.verifiedBackground;
            if (drawable != null) {
                drawable.setBounds(0, (-dp8) / 2, dp8, dp8 / 2);
                this.verifiedBackground.setAlpha((int) (75.0f * f4));
                this.verifiedBackground.draw(canvas);
            }
            Drawable drawable2 = this.verifiedForeground;
            if (drawable2 != null) {
                drawable2.setBounds(0, (-dp8) / 2, dp8, dp8 / 2);
                this.verifiedForeground.setAlpha((int) (255.0f * f4));
                this.verifiedForeground.draw(canvas);
            }
            RectF rectF13 = AndroidUtilities.rectTmp;
            float dp9 = (this.leftMenu.left + AndroidUtilities.dp(30.0f)) - AndroidUtilities.dp(10.0f);
            RectF rectF14 = this.leftMenu;
            rectF13.set(dp9, rectF14.top, rectF14.left + AndroidUtilities.dp(30.0f), this.leftMenu.bottom);
            this.previewClip.draw(canvas, rectF13, 2, 1.0f);
            canvas.restore();
        }
        if (f4 < f) {
            canvas.save();
            f2 = 0.1f;
            float scale2 = this.closeBounce.getScale(0.1f);
            canvas.scale(scale2, scale2, this.closeRect.centerX(), this.closeRect.centerY());
            float f7 = f - f3;
            if (f7 > 0.0f) {
                this.closeText.draw(canvas, (AndroidUtilities.dp(32.0f) * f4) + ((this.closeRect.left + AndroidUtilities.dp(30.0f)) - (AndroidUtilities.dp(12.0f) * f3)), this.closeRect.centerY(), -1, (f - f4) * f7);
            }
            if (f3 > 0.0f) {
                this.backText.draw(canvas, (AndroidUtilities.dp(32.0f) * f4) + this.closeRect.left + AndroidUtilities.dp(30.0f) + (AndroidUtilities.dp(12.0f) * f7), this.closeRect.centerY(), -1, (f - f4) * f3);
            }
            canvas.restore();
        } else {
            f2 = 0.1f;
        }
        canvas.restore();
        canvas.save();
        canvas.translate(this.collapseRect.centerX() + AndroidUtilities.dp(2.0f), this.collapseRect.centerY());
        float scale3 = this.collapseBounce.getScale(f2);
        canvas.scale(scale3, scale3);
        float dp10 = AndroidUtilities.dp(6.0f);
        float dp11 = AndroidUtilities.dp(3.0f);
        float f8 = -dp11;
        canvas.drawLine(-dp10, f8, 0.0f, dp11, this.iconStrokePaint);
        canvas.drawLine(0.0f, dp11, dp10, f8, this.iconStrokePaint);
        canvas.restore();
        canvas.save();
        canvas.translate(this.menuRect.centerX() + AndroidUtilities.dp(f), this.menuRect.centerY());
        float scale4 = this.menuBounce.getScale(f2);
        canvas.scale(scale4, scale4);
        canvas.drawCircle(0.0f, -AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.66f), this.iconPaint);
        canvas.drawCircle(0.0f, 0.0f, AndroidUtilities.dp(1.66f), this.iconPaint);
        canvas.drawCircle(0.0f, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.66f), this.iconPaint);
        float f9 = this.animatedDownloading.set(this.downloading);
        if (f9 > 0.0f) {
            canvas.translate(-AndroidUtilities.dpf2(8.166f), AndroidUtilities.dpf2(3.5f));
            float f10 = (f9 * 0.5f) + 0.5f;
            canvas.scale(f10, f10);
            this.downloadPaint.setColor(Theme.multAlpha(-1, 0.4f));
            canvas.drawPath(this.downloadPath, this.downloadPaint);
            float currentTimeMillis = ((float) ((System.currentTimeMillis() - this.start) % 450)) / 450.0f;
            float f11 = 0.5f + currentTimeMillis;
            canvas.save();
            canvas.clipRect(-AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), currentTimeMillis), AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), f11));
            this.downloadPaint.setColor(Theme.multAlpha(-1, f));
            canvas.drawPath(this.downloadPath, this.downloadPaint);
            canvas.restore();
            if (f11 > f) {
                canvas.save();
                canvas.clipRect(-AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), 0.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.lerp(-AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(3.5f), f11 - f));
                this.downloadPaint.setColor(Theme.multAlpha(-1, f));
                canvas.drawPath(this.downloadPath, this.downloadPaint);
                canvas.restore();
            }
            invalidate();
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.BotFullscreenButtons.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setBack(boolean z) {
        setBack(z, true);
    }

    public void setBack(boolean z, boolean z2) {
        this.back = z;
        if (!z2) {
            this.animatedBack.set(z);
        }
        invalidate();
    }

    public void setDownloading(boolean z) {
        if (this.downloading == z) {
            return;
        }
        this.downloading = z;
        invalidate();
    }

    public void setInsets(Rect rect) {
        this.insets.set(rect);
    }

    public void setInsets(RectF rectF) {
        this.insets.set(rectF);
    }

    public void setName(String str, boolean z) {
        Drawable mutate;
        this.previewText = new Text(str, 13.0f, AndroidUtilities.bold());
        if (z) {
            this.verifiedBackground = getContext().getResources().getDrawable(R.drawable.verified_area).mutate();
            mutate = getContext().getResources().getDrawable(R.drawable.verified_check).mutate();
        } else {
            mutate = null;
            this.verifiedBackground = null;
        }
        this.verifiedForeground = mutate;
    }

    public void setOnCloseClickListener(Runnable runnable) {
        this.onCloseClickListener = runnable;
    }

    public void setOnCollapseClickListener(Runnable runnable) {
        this.onCollapseClickListener = runnable;
    }

    public void setOnMenuClickListener(Runnable runnable) {
        this.onMenuClickListener = runnable;
    }

    public void setParentRenderNode(Object obj) {
        this.parentRenderNode = obj;
    }

    public void setPreview(boolean z, boolean z2) {
        AndroidUtilities.cancelRunOnUIThread(this.hidePreview);
        this.preview = z;
        if (!z2) {
            this.animatedPreview.set(z, true);
        }
        invalidate();
        if (z) {
            AndroidUtilities.runOnUIThread(this.hidePreview, 2500L);
        }
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }
}
