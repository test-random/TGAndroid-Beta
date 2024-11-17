package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import com.google.zxing.common.detector.MathUtils;
import java.util.Arrays;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CacheChart;
import org.telegram.ui.Components.Premium.StarParticlesView;

public abstract class CacheChart extends View {
    private static final int[] DEFAULT_COLORS;
    private static final int[] DEFAULT_PARTICLES;
    private static Long loadedStart;
    private static long particlesStart;
    private static Long start;
    private AnimatedTextView.AnimatedTextDrawable bottomCompleteText;
    private AnimatedTextView.AnimatedTextDrawable bottomText;
    private RectF chartBounds;
    private RectF chartInnerBounds;
    private RectF chartMeasureBounds;
    private final int[] colorKeys;
    private boolean complete;
    private StarParticlesView.Drawable completeDrawable;
    private AnimatedFloat completeFloat;
    private LinearGradient completeGradient;
    private Matrix completeGradientMatrix;
    private Paint completePaint;
    private Paint completePaintStroke;
    private Path completePath;
    private RectF completePathBounds;
    private LinearGradient completeTextGradient;
    private Matrix completeTextGradientMatrix;
    private boolean interceptTouch;
    private boolean isAttached;
    private boolean loading;
    private Paint loadingBackgroundPaint;
    public AnimatedFloat loadingFloat;
    private final int[] particles;
    private RectF roundingRect;
    private final int sectionsCount;
    private Sector[] sectors;
    private float[] segmentsTmp;
    private int selectedIndex;
    private final boolean svgParticles;
    private float[] tempFloat;
    private int[] tempPercents;
    private AnimatedTextView.AnimatedTextDrawable topCompleteText;
    private AnimatedTextView.AnimatedTextDrawable topText;
    private final int type;

    public class Sector {
        float angleCenter;
        AnimatedFloat angleCenterAnimated;
        float angleSize;
        AnimatedFloat angleSizeAnimated;
        Paint cut;
        RadialGradient gradient;
        Matrix gradientMatrix;
        int gradientWidth;
        private float lastAngleCenter;
        private float lastAngleSize;
        private float lastCx;
        private float lastCy;
        private float lastRounding;
        private float lastThickness;
        private float lastWidth;
        Paint paint;
        Bitmap particle;
        Paint particlePaint;
        float particlesAlpha;
        AnimatedFloat particlesAlphaAnimated;
        Path path;
        RectF pathBounds;
        RectF rectF;
        boolean selected;
        AnimatedFloat selectedAnimated;
        AnimatedTextView.AnimatedTextDrawable text;
        float textAlpha;
        AnimatedFloat textAlphaAnimated;
        float textScale;
        AnimatedFloat textScaleAnimated;
        Paint uncut;

        Sector() {
            Paint paint = new Paint(3);
            this.particlePaint = paint;
            paint.setColor(-1);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.angleCenterAnimated = new AnimatedFloat(CacheChart.this, 650L, cubicBezierInterpolator);
            this.angleSizeAnimated = new AnimatedFloat(CacheChart.this, 650L, cubicBezierInterpolator);
            CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.EASE_OUT;
            this.textAlphaAnimated = new AnimatedFloat(CacheChart.this, 0L, 150L, cubicBezierInterpolator2);
            this.textScale = 1.0f;
            this.textScaleAnimated = new AnimatedFloat(CacheChart.this, 0L, 150L, cubicBezierInterpolator2);
            this.text = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            this.particlesAlphaAnimated = new AnimatedFloat(CacheChart.this, 0L, 150L, cubicBezierInterpolator2);
            this.selectedAnimated = new AnimatedFloat(CacheChart.this, 0L, 200L, cubicBezierInterpolator);
            this.text.setTextColor(-1);
            this.text.setAnimationProperties(0.35f, 0L, 200L, cubicBezierInterpolator);
            this.text.setTypeface(AndroidUtilities.bold());
            this.text.setTextSize(AndroidUtilities.dp(15.0f));
            this.text.setGravity(17);
            this.path = new Path();
            this.paint = new Paint(1);
            this.pathBounds = new RectF();
            this.uncut = new Paint(1);
            Paint paint2 = new Paint(1);
            this.cut = paint2;
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            this.particlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            this.rectF = new RectF();
        }

        private void drawParticles(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10) {
            if (f10 <= 0.0f || !LiteMode.isEnabled(98784)) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            float sqrt = (float) Math.sqrt(2.0d);
            if (CacheChart.particlesStart < 0) {
                long unused = CacheChart.particlesStart = currentTimeMillis;
            }
            float f11 = ((float) (currentTimeMillis - CacheChart.particlesStart)) / 10000.0f;
            Bitmap bitmap = this.particle;
            if (bitmap != null) {
                int width = bitmap.getWidth();
                float f12 = width;
                float dpf2 = AndroidUtilities.dpf2(15.0f) / f12;
                float f13 = 7.0f;
                int floor = (int) Math.floor((f5 % 360.0f) / 7.0f);
                int ceil = (int) Math.ceil((f6 % 360.0f) / 7.0f);
                while (floor <= ceil) {
                    float f14 = floor * f13;
                    double d = 100.0f + f11;
                    double sin = ((Math.sin(2000.0f * f14) + 1.0d) * 0.25d) + 1.0d;
                    Double.isNaN(d);
                    float f15 = (float) ((d * sin) % 1.0d);
                    float f16 = f12 * sqrt;
                    double d2 = f;
                    float f17 = f12;
                    int i = ceil;
                    double lerp = AndroidUtilities.lerp(f7 - f16, f8 + f16, f15);
                    double cos = Math.cos(CacheChart.toRad(f14));
                    Double.isNaN(lerp);
                    Double.isNaN(d2);
                    float f18 = (float) (d2 + (cos * lerp));
                    double d3 = f2;
                    double sin2 = Math.sin(CacheChart.toRad(f14));
                    Double.isNaN(lerp);
                    Double.isNaN(d3);
                    float f19 = (float) (d3 + (lerp * sin2));
                    float abs = 0.65f * f10 * ((Math.abs(f15 - 0.5f) * (-1.75f)) + 1.0f);
                    double d4 = f15;
                    Double.isNaN(d4);
                    double d5 = d4 * 3.141592653589793d;
                    this.particlePaint.setAlpha((int) (Math.max(0.0f, Math.min(1.0f, abs * ((((float) (Math.sin(d5) - 1.0d)) * 0.25f) + 1.0f) * AndroidUtilities.lerp(1.0f, Math.min(MathUtils.distance(f18, f19, f3, f4) / AndroidUtilities.dpf2(64.0f), 1.0f), f9))) * 255.0f));
                    double sin3 = ((((float) (Math.sin(d5) - 1.0d)) * 0.25f) + 1.0f) * 0.75f;
                    double sin4 = ((Math.sin(f14) + 1.0d) * 0.25d) + 0.800000011920929d;
                    Double.isNaN(sin3);
                    float f20 = ((float) (sin3 * sin4)) * dpf2;
                    canvas.save();
                    canvas.translate(f18, f19);
                    canvas.scale(f20, f20);
                    float f21 = -(width >> 1);
                    canvas.drawBitmap(this.particle, f21, f21, this.particlePaint);
                    canvas.restore();
                    floor++;
                    ceil = i;
                    f11 = f11;
                    f12 = f17;
                    f13 = 7.0f;
                }
            }
        }

        private void setGradientBounds(float f, float f2, float f3, float f4) {
            this.gradientMatrix.reset();
            this.gradientMatrix.setTranslate(f, f2);
            this.gradient.setLocalMatrix(this.gradientMatrix);
        }

        private void setupPath(RectF rectF, RectF rectF2, float f, float f2, float f3) {
            float f4;
            float f5;
            float min = Math.min(f3, (rectF.width() - rectF2.width()) / 4.0f);
            double d = f2 / 180.0f;
            Double.isNaN(d);
            double width = rectF2.width() / 2.0f;
            Double.isNaN(width);
            float min2 = Math.min(min, (float) (d * 3.141592653589793d * width));
            float width2 = (rectF.width() - rectF2.width()) / 2.0f;
            if (this.lastAngleCenter == f && this.lastAngleSize == f2 && this.lastRounding == min2 && this.lastThickness == width2 && this.lastWidth == rectF.width() && this.lastCx == rectF.centerX() && this.lastCy == rectF.centerY()) {
                return;
            }
            this.lastAngleCenter = f;
            this.lastAngleSize = f2;
            this.lastRounding = min2;
            this.lastThickness = width2;
            this.lastWidth = rectF.width();
            this.lastCx = rectF.centerX();
            this.lastCy = rectF.centerY();
            float f6 = f - f2;
            float f7 = f + f2;
            boolean z = min2 > 0.0f;
            float f8 = min2 * 2.0f;
            double width3 = rectF.width() - f8;
            Double.isNaN(width3);
            float f9 = (min2 / ((float) (width3 * 3.141592653589793d))) * 360.0f;
            double width4 = rectF2.width() + f8;
            Double.isNaN(width4);
            float f10 = ((min2 / ((float) (width4 * 3.141592653589793d))) * 360.0f) + ((f2 > 175.0f ? 0 : 1) * 0.5f);
            float width5 = (rectF.width() / 2.0f) - min2;
            float width6 = (rectF2.width() / 2.0f) + min2;
            this.path.rewind();
            float f11 = f7 - f6;
            if (f11 < 0.5f) {
                return;
            }
            if (z) {
                RectF rectF3 = CacheChart.this.roundingRect;
                double centerX = rectF.centerX();
                double d2 = width5;
                f4 = width6;
                double cos = Math.cos(CacheChart.toRad(r19));
                Double.isNaN(d2);
                Double.isNaN(centerX);
                double d3 = centerX + (cos * d2);
                double centerY = rectF.centerY();
                double sin = Math.sin(CacheChart.toRad(r19));
                Double.isNaN(d2);
                Double.isNaN(centerY);
                CacheChart.setCircleBounds(rectF3, d3, (d2 * sin) + centerY, min2);
                this.path.arcTo(CacheChart.this.roundingRect, (f6 + f9) - 90.0f, 90.0f);
            } else {
                f4 = width6;
            }
            this.path.arcTo(rectF, f6 + f9, f11 - (f9 * 2.0f));
            if (z) {
                RectF rectF4 = CacheChart.this.roundingRect;
                double centerX2 = rectF.centerX();
                double d4 = width5;
                float f12 = f7 - f9;
                double cos2 = Math.cos(CacheChart.toRad(f12));
                Double.isNaN(d4);
                Double.isNaN(centerX2);
                double d5 = centerX2 + (cos2 * d4);
                double centerY2 = rectF.centerY();
                f5 = f6;
                double sin2 = Math.sin(CacheChart.toRad(f12));
                Double.isNaN(d4);
                Double.isNaN(centerY2);
                CacheChart.setCircleBounds(rectF4, d5, centerY2 + (d4 * sin2), min2);
                this.path.arcTo(CacheChart.this.roundingRect, f12, 90.0f);
                RectF rectF5 = CacheChart.this.roundingRect;
                double centerX3 = rectF2.centerX();
                double d6 = f4;
                double cos3 = Math.cos(CacheChart.toRad(r2));
                Double.isNaN(d6);
                Double.isNaN(centerX3);
                double d7 = centerX3 + (cos3 * d6);
                double centerY3 = rectF2.centerY();
                double sin3 = Math.sin(CacheChart.toRad(r2));
                Double.isNaN(d6);
                Double.isNaN(centerY3);
                CacheChart.setCircleBounds(rectF5, d7, centerY3 + (d6 * sin3), min2);
                this.path.arcTo(CacheChart.this.roundingRect, (f7 - f10) + 90.0f, 90.0f);
            } else {
                f5 = f6;
            }
            this.path.arcTo(rectF2, f7 - f10, -(f11 - (f10 * 2.0f)));
            if (z) {
                RectF rectF6 = CacheChart.this.roundingRect;
                double centerX4 = rectF2.centerX();
                double d8 = f4;
                double cos4 = Math.cos(CacheChart.toRad(r4));
                Double.isNaN(d8);
                Double.isNaN(centerX4);
                double d9 = centerX4 + (cos4 * d8);
                double centerY4 = rectF2.centerY();
                double sin4 = Math.sin(CacheChart.toRad(r4));
                Double.isNaN(d8);
                Double.isNaN(centerY4);
                CacheChart.setCircleBounds(rectF6, d9, centerY4 + (d8 * sin4), min2);
                this.path.arcTo(CacheChart.this.roundingRect, f5 + f10 + 180.0f, 90.0f);
            }
            this.path.close();
            this.path.computeBounds(this.pathBounds, false);
        }

        void draw(Canvas canvas, RectF rectF, RectF rectF2, float f, float f2, float f3, float f4, float f5) {
            float f6;
            float f7;
            float f8 = this.selectedAnimated.set(this.selected ? 1.0f : 0.0f);
            this.rectF.set(rectF);
            this.rectF.inset((-AndroidUtilities.dp(9.0f)) * f8, f8 * (-AndroidUtilities.dp(9.0f)));
            double centerX = this.rectF.centerX();
            double cos = Math.cos(CacheChart.toRad(f));
            double width = this.rectF.width() + rectF2.width();
            Double.isNaN(width);
            Double.isNaN(centerX);
            float f9 = (float) (centerX + ((cos * width) / 4.0d));
            double centerY = this.rectF.centerY();
            double sin = Math.sin(CacheChart.toRad(f));
            double width2 = this.rectF.width() + rectF2.width();
            Double.isNaN(width2);
            Double.isNaN(centerY);
            float f10 = (float) (centerY + ((sin * width2) / 4.0d));
            float f11 = f5 * this.textAlphaAnimated.set(this.textAlpha) * f4;
            float f12 = this.particlesAlphaAnimated.set(this.particlesAlpha);
            this.paint.setAlpha((int) (f4 * 255.0f));
            if (f2 * 2.0f >= 359.0f) {
                canvas.saveLayerAlpha(this.rectF, 255, 31);
                canvas.drawCircle(this.rectF.centerX(), this.rectF.centerY(), this.rectF.width() / 2.0f, this.uncut);
                canvas.drawRect(this.rectF, this.paint);
                f6 = f10;
                f7 = f9;
                drawParticles(canvas, this.rectF.centerX(), this.rectF.centerY(), f9, f10, 0.0f, 359.0f, rectF2.width() / 2.0f, this.rectF.width() / 2.0f, f11, Math.max(0.0f, (f5 / 0.75f) - 0.75f) * f12);
                canvas.drawCircle(rectF2.centerX(), rectF2.centerY(), rectF2.width() / 2.0f, this.cut);
            } else {
                f6 = f10;
                f7 = f9;
                setupPath(this.rectF, rectF2, f, f2, f3);
                setGradientBounds(this.rectF.centerX(), rectF.centerY(), this.rectF.width() / 2.0f, f);
                canvas.saveLayerAlpha(this.rectF, 255, 31);
                canvas.drawPath(this.path, this.uncut);
                canvas.drawRect(this.rectF, this.paint);
                drawParticles(canvas, this.rectF.centerX(), this.rectF.centerY(), f7, f6, f - f2, f + f2, rectF2.width() / 2.0f, this.rectF.width() / 2.0f, f11, Math.max(0.0f, (f5 / 0.75f) - 0.75f) * f12);
            }
            canvas.restore();
            float f13 = this.textScaleAnimated.set(this.textScale);
            CacheChart.setCircleBounds(CacheChart.this.roundingRect, f7, f6, 0.0f);
            if (f13 != 1.0f) {
                canvas.save();
                canvas.scale(f13, f13, CacheChart.this.roundingRect.centerX(), CacheChart.this.roundingRect.centerY());
            }
            this.text.setAlpha((int) (f11 * 255.0f));
            this.text.setBounds((int) CacheChart.this.roundingRect.left, (int) CacheChart.this.roundingRect.top, (int) CacheChart.this.roundingRect.right, (int) CacheChart.this.roundingRect.bottom);
            this.text.draw(canvas);
            if (f13 != 1.0f) {
                canvas.restore();
            }
        }
    }

    public static class SegmentSize {
        int index;
        public boolean selected;
        public long size;

        public static SegmentSize of(long j, boolean z) {
            SegmentSize segmentSize = new SegmentSize();
            segmentSize.size = j;
            segmentSize.selected = z;
            return segmentSize;
        }
    }

    static {
        int i = Theme.key_statisticChartLine_lightblue;
        int i2 = Theme.key_statisticChartLine_blue;
        int i3 = Theme.key_statisticChartLine_green;
        int i4 = Theme.key_statisticChartLine_purple;
        int i5 = Theme.key_statisticChartLine_lightgreen;
        int i6 = Theme.key_statisticChartLine_red;
        int i7 = Theme.key_statisticChartLine_orange;
        int i8 = Theme.key_statisticChartLine_cyan;
        int i9 = Theme.key_statisticChartLine_golden;
        DEFAULT_COLORS = new int[]{i, i2, i3, i4, i5, i6, i7, i8, i4, i9, i9};
        int i10 = R.raw.cache_photos;
        int i11 = R.raw.cache_videos;
        int i12 = R.raw.cache_documents;
        int i13 = R.raw.cache_music;
        int i14 = R.raw.cache_stickers;
        int i15 = R.raw.cache_profile_photos;
        int i16 = R.raw.cache_other;
        DEFAULT_PARTICLES = new int[]{i10, i11, i12, i13, i11, i13, i14, i15, i16, i16, i12};
        particlesStart = -1L;
    }

    public CacheChart(Context context) {
        this(context, 11, DEFAULT_COLORS, 0, DEFAULT_PARTICLES);
    }

    public CacheChart(Context context, int i, int[] iArr, int i2, int[] iArr2) {
        super(context);
        this.chartMeasureBounds = new RectF();
        this.chartBounds = new RectF();
        this.chartInnerBounds = new RectF();
        this.loading = true;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.loadingFloat = new AnimatedFloat(this, 750L, cubicBezierInterpolator);
        int i3 = 0;
        this.complete = false;
        this.completeFloat = new AnimatedFloat(this, 650L, cubicBezierInterpolator);
        this.segmentsTmp = new float[2];
        this.roundingRect = new RectF();
        this.loadingBackgroundPaint = new Paint(1);
        this.completePath = new Path();
        this.completePaintStroke = new Paint(1);
        this.completePaint = new Paint(1);
        this.topText = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.bottomText = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.topCompleteText = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.bottomCompleteText = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.interceptTouch = true;
        this.selectedIndex = -1;
        setLayerType(2, null);
        this.sectionsCount = i;
        this.colorKeys = iArr;
        this.particles = iArr2;
        this.type = i2;
        this.svgParticles = i2 == 0;
        this.sectors = new Sector[i];
        Paint paint = this.loadingBackgroundPaint;
        Paint.Style style = Paint.Style.STROKE;
        paint.setStyle(style);
        this.loadingBackgroundPaint.setColor(Theme.getColor(Theme.key_listSelector));
        this.completePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        this.completeGradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(200.0f), new int[]{7263574, -9513642, -12469647, 4307569}, new float[]{0.0f, 0.07f, 0.93f, 1.0f}, tileMode);
        this.completeTextGradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(200.0f), new int[]{7263574, -9513642, -12469647, 4307569}, new float[]{0.0f, 0.07f, 0.93f, 1.0f}, tileMode);
        this.completeGradientMatrix = new Matrix();
        this.completeTextGradientMatrix = new Matrix();
        this.completePaintStroke.setShader(this.completeGradient);
        this.completePaint.setShader(this.completeGradient);
        this.completePaintStroke.setStyle(style);
        this.completePaintStroke.setStrokeCap(Paint.Cap.ROUND);
        this.completePaintStroke.setStrokeJoin(Paint.Join.ROUND);
        this.topText.setAnimationProperties(0.2f, 0L, 450L, cubicBezierInterpolator);
        this.topText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.topText.setTypeface(AndroidUtilities.bold());
        this.topText.setTextSize(AndroidUtilities.dp(32.0f));
        this.topText.setGravity(17);
        this.bottomText.setAnimationProperties(0.6f, 0L, 450L, cubicBezierInterpolator);
        this.bottomText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.bottomText.setTextSize(AndroidUtilities.dp(12.0f));
        this.bottomText.setGravity(17);
        this.topCompleteText.setAnimationProperties(0.2f, 0L, 450L, cubicBezierInterpolator);
        this.topCompleteText.getPaint().setShader(this.completeTextGradient);
        this.topCompleteText.setTypeface(AndroidUtilities.bold());
        this.topCompleteText.setTextSize(AndroidUtilities.dp(32.0f));
        this.topCompleteText.setGravity(17);
        this.bottomCompleteText.setAnimationProperties(0.6f, 0L, 450L, cubicBezierInterpolator);
        this.bottomCompleteText.getPaint().setShader(this.completeTextGradient);
        this.bottomCompleteText.setTypeface(AndroidUtilities.bold());
        this.bottomCompleteText.setTextSize(AndroidUtilities.dp(12.0f));
        this.bottomCompleteText.setGravity(17);
        while (true) {
            Sector[] sectorArr = this.sectors;
            if (i3 >= sectorArr.length) {
                return;
            }
            Sector sector = new Sector();
            sectorArr[i3] = sector;
            int blendOver = Theme.blendOver(Theme.getColor(iArr[i3]), 50331648);
            int blendOver2 = Theme.blendOver(Theme.getColor(iArr[i3]), 822083583);
            sector.gradientWidth = AndroidUtilities.dp(50.0f);
            RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dp(86.0f), new int[]{blendOver2, blendOver}, new float[]{0.3f, 1.0f}, Shader.TileMode.CLAMP);
            sector.gradient = radialGradient;
            Matrix matrix = new Matrix();
            sector.gradientMatrix = matrix;
            radialGradient.setLocalMatrix(matrix);
            sector.paint.setShader(sector.gradient);
            i3++;
        }
    }

    private boolean drawAnimatedText(Canvas canvas, AnimatedTextView.AnimatedTextDrawable animatedTextDrawable, float f, float f2, float f3, float f4) {
        if (f4 <= 0.0f) {
            return false;
        }
        animatedTextDrawable.setAlpha((int) (f4 * 255.0f));
        animatedTextDrawable.setBounds(0, 0, 0, 0);
        canvas.save();
        canvas.translate(f, f2);
        canvas.scale(f3, f3);
        animatedTextDrawable.draw(canvas);
        canvas.restore();
        return animatedTextDrawable.isAnimating();
    }

    public static int lambda$setSegments$0(SegmentSize segmentSize, SegmentSize segmentSize2) {
        return Long.compare(segmentSize.size, segmentSize2.size);
    }

    public static void setCircleBounds(RectF rectF, double d, double d2, float f) {
        setCircleBounds(rectF, (float) d, (float) d2, f);
    }

    public static void setCircleBounds(RectF rectF, float f, float f2, float f3) {
        rectF.set(f - f3, f2 - f3, f + f3, f2 + f3);
    }

    public static float toRad(float f) {
        double d = f / 180.0f;
        Double.isNaN(d);
        return (float) (d * 3.141592653589793d);
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CacheChart.dispatchDraw(android.graphics.Canvas):void");
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CacheChart.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    protected int heightDp() {
        return 200;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttached = true;
        int i = 0;
        while (true) {
            Sector[] sectorArr = this.sectors;
            if (i >= sectorArr.length) {
                return;
            }
            Sector sector = sectorArr[i];
            if (sector.particle == null) {
                sector.particle = this.svgParticles ? SvgHelper.getBitmap(this.particles[i], AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), -1) : BitmapFactory.decodeResource(getContext().getResources(), this.particles[i]);
            }
            i++;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        int i = 0;
        this.isAttached = false;
        while (true) {
            Sector[] sectorArr = this.sectors;
            if (i >= sectorArr.length) {
                return;
            }
            Bitmap bitmap = sectorArr[i].particle;
            if (bitmap != null) {
                bitmap.recycle();
                this.sectors[i].particle = null;
            }
            i++;
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int dp = AndroidUtilities.dp(heightDp());
        int dp2 = AndroidUtilities.dp(172.0f);
        this.chartMeasureBounds.set((size - dp2) / 2.0f, (dp - dp2) / 2.0f, (size + dp2) / 2.0f, (dp2 + dp) / 2.0f);
        this.completeGradientMatrix.reset();
        this.completeGradientMatrix.setTranslate(this.chartMeasureBounds.left, 0.0f);
        this.completeGradient.setLocalMatrix(this.completeGradientMatrix);
        this.completeTextGradientMatrix.reset();
        Matrix matrix = this.completeTextGradientMatrix;
        RectF rectF = this.chartMeasureBounds;
        matrix.setTranslate(rectF.left, -rectF.centerY());
        this.completeTextGradient.setLocalMatrix(this.completeTextGradientMatrix);
        StarParticlesView.Drawable drawable = this.completeDrawable;
        if (drawable != null) {
            drawable.rect.set(0.0f, 0.0f, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(140.0f));
            this.completeDrawable.rect.offset((getMeasuredWidth() - this.completeDrawable.rect.width()) / 2.0f, (getMeasuredHeight() - this.completeDrawable.rect.height()) / 2.0f);
            this.completeDrawable.rect2.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.completeDrawable.resetPositions();
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(dp, 1073741824));
    }

    protected void onSectionClick(int i) {
    }

    protected abstract void onSectionDown(int i, boolean z);

    protected int padInsideDp() {
        return 0;
    }

    public void setInterceptTouch(boolean z) {
        this.interceptTouch = z;
    }

    public void setSegments(long j, boolean z, SegmentSize... segmentSizeArr) {
        int i;
        char c;
        String str;
        float f;
        float f2;
        int i2;
        int i3;
        int i4;
        SegmentSize[] segmentSizeArr2 = segmentSizeArr;
        if (segmentSizeArr2 == null || segmentSizeArr2.length == 0) {
            this.loading = false;
            this.complete = j == 0;
            if (!z) {
                this.loadingFloat.set(0.0f, true);
                this.completeFloat.set(this.complete ? 1.0f : 0.0f, true);
            }
            this.topCompleteText.setText(this.topText.getText(), false);
            this.topText.setText("0", z);
            this.topCompleteText.setText("0", z);
            this.bottomCompleteText.setText(this.bottomText.getText(), false);
            this.bottomText.setText("KB", z);
            this.bottomCompleteText.setText("KB", z);
            int i5 = 0;
            while (true) {
                Sector[] sectorArr = this.sectors;
                if (i5 >= sectorArr.length) {
                    invalidate();
                    return;
                }
                Sector sector = sectorArr[i5];
                sector.textAlpha = 0.0f;
                if (z) {
                    i = 1;
                } else {
                    i = 1;
                    sector.textAlphaAnimated.set(0.0f, true);
                }
                i5 += i;
            }
        } else {
            this.loading = false;
            if (!z) {
                this.loadingFloat.set(0.0f, true);
            }
            SpannableString spannableString = new SpannableString("%");
            int length = segmentSizeArr2.length;
            long j2 = 0;
            for (int i6 = 0; i6 < segmentSizeArr2.length; i6++) {
                if (segmentSizeArr2[i6] == null) {
                    SegmentSize segmentSize = new SegmentSize();
                    segmentSizeArr2[i6] = segmentSize;
                    segmentSize.size = 0L;
                }
                SegmentSize segmentSize2 = segmentSizeArr2[i6];
                segmentSize2.index = i6;
                boolean z2 = segmentSize2.selected;
                if (z2) {
                    j2 += segmentSize2.size;
                }
                if (segmentSize2.size <= 0 || !z2) {
                    length--;
                }
            }
            if (j2 > 0) {
                float f3 = 0.0f;
                int i7 = 0;
                int i8 = 0;
                while (i7 < segmentSizeArr2.length) {
                    SegmentSize segmentSize3 = segmentSizeArr2[i7];
                    float f4 = (segmentSize3 == null || !segmentSize3.selected) ? 0.0f : ((float) segmentSize3.size) / ((float) j2);
                    if (f4 <= 0.0f || f4 >= 0.02f) {
                        i3 = 1;
                    } else {
                        i3 = 1;
                        i8++;
                        f3 += f4;
                    }
                    i7 += i3;
                }
                Math.min(segmentSizeArr2.length, this.sectors.length);
                int[] iArr = this.tempPercents;
                if (iArr == null || iArr.length != segmentSizeArr2.length) {
                    this.tempPercents = new int[segmentSizeArr2.length];
                }
                float[] fArr = this.tempFloat;
                if (fArr == null || fArr.length != segmentSizeArr2.length) {
                    this.tempFloat = new float[segmentSizeArr2.length];
                }
                for (int i9 = 0; i9 < segmentSizeArr2.length; i9++) {
                    float[] fArr2 = this.tempFloat;
                    SegmentSize segmentSize4 = segmentSizeArr2[i9];
                    fArr2[i9] = (segmentSize4 == null || !segmentSize4.selected) ? 0.0f : ((float) segmentSize4.size) / ((float) j2);
                }
                AndroidUtilities.roundPercents(this.tempFloat, this.tempPercents);
                if (this.type == 0) {
                    Arrays.sort(segmentSizeArr2, new Comparator() {
                        @Override
                        public final int compare(Object obj, Object obj2) {
                            int lambda$setSegments$0;
                            lambda$setSegments$0 = CacheChart.lambda$setSegments$0((CacheChart.SegmentSize) obj, (CacheChart.SegmentSize) obj2);
                            return lambda$setSegments$0;
                        }
                    });
                    int i10 = 0;
                    while (true) {
                        if (i10 > segmentSizeArr2.length) {
                            break;
                        }
                        SegmentSize segmentSize5 = segmentSizeArr2[i10];
                        if (segmentSize5.index == segmentSizeArr2.length - 1) {
                            SegmentSize segmentSize6 = segmentSizeArr2[0];
                            segmentSizeArr2[0] = segmentSize5;
                            segmentSizeArr2[i10] = segmentSize6;
                            break;
                        }
                        i10++;
                    }
                }
                if (length < 2) {
                    length = 0;
                }
                float f5 = 360.0f - (length * 2.0f);
                float f6 = 0.0f;
                int i11 = 0;
                int i12 = 0;
                while (i11 < segmentSizeArr2.length) {
                    SegmentSize segmentSize7 = segmentSizeArr2[i11];
                    int i13 = segmentSize7.index;
                    if (segmentSize7.selected) {
                        f = f5;
                        f2 = ((float) segmentSize7.size) / ((float) j2);
                    } else {
                        f = f5;
                        f2 = 0.0f;
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    long j3 = j2;
                    spannableStringBuilder.append((CharSequence) String.format("%d", Integer.valueOf(this.tempPercents[i13])));
                    spannableStringBuilder.append((CharSequence) spannableString);
                    Sector sector2 = this.sectors[i13];
                    float f7 = (((double) f2) <= 0.05d || f2 >= 1.0f) ? 0.0f : 1.0f;
                    sector2.textAlpha = f7;
                    sector2.textScale = (f2 < 0.08f || this.tempPercents[i13] >= 100) ? 0.85f : 1.0f;
                    sector2.particlesAlpha = 1.0f;
                    if (!z) {
                        sector2.textAlphaAnimated.set(f7, true);
                        Sector sector3 = this.sectors[i13];
                        sector3.textScaleAnimated.set(sector3.textScale, true);
                        Sector sector4 = this.sectors[i13];
                        sector4.particlesAlphaAnimated.set(sector4.particlesAlpha, true);
                    }
                    Sector sector5 = this.sectors[i13];
                    if (sector5.textAlpha > 0.0f) {
                        sector5.text.setText(spannableStringBuilder, z);
                    }
                    float f8 = (f2 >= 0.02f || f2 <= 0.0f) ? f2 * (1.0f - ((i8 * 0.02f) - f3)) : 0.02f;
                    float f9 = (f6 * f) + (i12 * 2.0f);
                    float f10 = (f8 * f) + f9;
                    if (f8 <= 0.0f) {
                        Sector sector6 = this.sectors[i13];
                        sector6.angleCenter = (f9 + f10) / 2.0f;
                        sector6.angleSize = Math.abs(f10 - f9) / 2.0f;
                        Sector sector7 = this.sectors[i13];
                        sector7.textAlpha = 0.0f;
                        if (!z) {
                            sector7.angleCenterAnimated.set(sector7.angleCenter, true);
                            Sector sector8 = this.sectors[i13];
                            sector8.angleSizeAnimated.set(sector8.angleSize, true);
                            Sector sector9 = this.sectors[i13];
                            sector9.textAlphaAnimated.set(sector9.textAlpha, true);
                        }
                        i2 = 1;
                    } else {
                        Sector sector10 = this.sectors[i13];
                        sector10.angleCenter = (f9 + f10) / 2.0f;
                        sector10.angleSize = Math.abs(f10 - f9) / 2.0f;
                        if (z) {
                            i2 = 1;
                        } else {
                            Sector sector11 = this.sectors[i13];
                            i2 = 1;
                            sector11.angleCenterAnimated.set(sector11.angleCenter, true);
                            Sector sector12 = this.sectors[i13];
                            sector12.angleSizeAnimated.set(sector12.angleSize, true);
                        }
                        f6 += f8;
                        i12 += i2;
                    }
                    i11 += i2;
                    segmentSizeArr2 = segmentSizeArr;
                    f5 = f;
                    j2 = j3;
                }
                long j4 = j2;
                String[] split = AndroidUtilities.formatFileSize(j4, true, true).split(" ");
                if (split.length > 0) {
                    c = 0;
                    str = split[0];
                } else {
                    c = 0;
                    str = "";
                }
                if (str.length() >= 4 && j4 < 1073741824) {
                    str = str.split("\\.")[c];
                }
                this.topText.setText(str, z);
                this.bottomText.setText(split.length > 1 ? split[1] : "", z);
                if (this.completeFloat.get() > 0.0f) {
                    this.topCompleteText.setText(this.topText.getText(), z);
                    this.bottomCompleteText.setText(this.bottomText.getText(), z);
                }
                this.complete = false;
                if (!z) {
                    this.completeFloat.set(0.0f, true);
                }
                invalidate();
                return;
            }
            this.loading = false;
            this.complete = j <= 0;
            if (!z) {
                this.loadingFloat.set(0.0f, true);
                this.completeFloat.set(this.complete ? 1.0f : 0.0f, true);
            }
            this.topCompleteText.setText(this.topText.getText(), false);
            this.topText.setText("0", z);
            this.topCompleteText.setText("0", z);
            this.bottomCompleteText.setText(this.bottomText.getText(), false);
            this.bottomText.setText("KB", z);
            this.bottomCompleteText.setText("KB", z);
            int i14 = 0;
            while (true) {
                Sector[] sectorArr2 = this.sectors;
                if (i14 >= sectorArr2.length) {
                    invalidate();
                    return;
                }
                Sector sector13 = sectorArr2[i14];
                sector13.textAlpha = 0.0f;
                if (z) {
                    i4 = 1;
                } else {
                    i4 = 1;
                    sector13.textAlphaAnimated.set(0.0f, true);
                }
                i14 += i4;
            }
        }
    }

    public void setSelected(int i) {
        if (i == this.selectedIndex) {
            return;
        }
        int i2 = 0;
        while (true) {
            Sector[] sectorArr = this.sectors;
            if (i2 >= sectorArr.length) {
                this.selectedIndex = i;
                invalidate();
                return;
            }
            if (i == i2 && sectorArr[i2].angleSize <= 0.0f) {
                i = -1;
            }
            sectorArr[i2].selected = i == i2;
            i2++;
        }
    }
}
