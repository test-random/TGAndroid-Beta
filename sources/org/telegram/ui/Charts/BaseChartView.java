package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.ChartPickerDelegate;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.ChartBottomSignatureData;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;
import org.telegram.ui.Components.CubicBezierInterpolator;

public abstract class BaseChartView extends View implements ChartPickerDelegate.Listener {
    protected static final boolean ANIMATE_PICKER_SIZES;
    public static FastOutSlowInInterpolator INTERPOLATOR;
    public static final boolean USE_LINES;
    private final int ANIM_DURATION;
    ValueAnimator alphaAnimator;
    ValueAnimator alphaBottomAnimator;
    public boolean animateLegentTo;
    float animateToMaxHeight;
    float animateToMinHeight;
    protected float animatedToPickerMaxHeight;
    protected float animatedToPickerMinHeight;
    private Bitmap bottomChartBitmap;
    private Canvas bottomChartCanvas;
    ArrayList bottomSignatureDate;
    protected int bottomSignatureOffset;
    Paint bottomSignaturePaint;
    float bottomSignaturePaintAlpha;
    protected boolean canCaptureChartSelection;
    long capturedTime;
    int capturedX;
    int capturedY;
    int chartActiveLineAlpha;
    public RectF chartArea;
    int chartBottom;
    protected boolean chartCaptured;
    ChartData chartData;
    public float chartEnd;
    public float chartFullWidth;
    ChartHeaderView chartHeaderView;
    public float chartStart;
    public float chartWidth;
    ChartBottomSignatureData currentBottomSignatures;
    public float currentMaxHeight;
    public float currentMinHeight;
    protected DateSelectionListener dateSelectionListener;
    protected boolean drawPointOnSelection;
    Paint emptyPaint;
    public boolean enabled;
    int endXIndex;
    private Rect exclusionRect;
    private List exclusionRects;
    private ValueAnimator.AnimatorUpdateListener heightUpdateListener;
    int hintLinePaintAlpha;
    ArrayList horizontalLines;
    boolean invalidatePickerChart;
    boolean landscape;
    int lastH;
    long lastTime;
    int lastW;
    int lastX;
    int lastY;
    public boolean legendShowing;
    public LegendSignatureView legendSignatureView;
    Paint linePaint;
    public ArrayList lines;
    Animator maxValueAnimator;
    private ValueAnimator.AnimatorUpdateListener minHeightUpdateListener;
    private float minMaxUpdateStep;
    Path pathTmp;
    Animator pickerAnimator;
    public ChartPickerDelegate pickerDelegate;
    private ValueAnimator.AnimatorUpdateListener pickerHeightUpdateListener;
    protected float pickerMaxHeight;
    protected float pickerMinHeight;
    private ValueAnimator.AnimatorUpdateListener pickerMinHeightUpdateListener;
    Rect pickerRect;
    Paint pickerSelectorPaint;
    public float pickerWidth;
    public int pikerHeight;
    boolean postTransition;
    protected Theme.ResourcesProvider resourcesProvider;
    Paint ripplePaint;
    protected float selectedCoordinate;
    protected int selectedIndex;
    Paint selectedLinePaint;
    public float selectionA;
    ValueAnimator selectionAnimator;
    private ValueAnimator.AnimatorUpdateListener selectionAnimatorListener;
    Paint selectionBackgroundPaint;
    private Animator.AnimatorListener selectorAnimatorEndListener;
    public SharedUiComponents sharedUiComponents;
    TextPaint signaturePaint;
    TextPaint signaturePaint2;
    float signaturePaintAlpha;
    private float startFromMax;
    private float startFromMaxH;
    private float startFromMin;
    private float startFromMinH;
    int startXIndex;
    boolean superDraw;
    float thresholdMaxHeight;
    protected int tmpI;
    protected int tmpN;
    private final int touchSlop;
    public int transitionMode;
    public TransitionParams transitionParams;
    Paint unactiveBottomChartPaint;
    boolean useAlphaSignature;
    protected boolean useMinHeight;
    VibrationEffect vibrationEffect;
    Paint whiteLinePaint;
    public static final float HORIZONTAL_PADDING = AndroidUtilities.dpf2(16.0f);
    private static final float SELECTED_LINE_WIDTH = AndroidUtilities.dpf2(1.5f);
    public static final float SIGNATURE_TEXT_SIZE = AndroidUtilities.dpf2(12.0f);
    public static final int SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(18.0f);
    private static final int BOTTOM_SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(14.0f);
    public static final int BOTTOM_SIGNATURE_START_ALPHA = AndroidUtilities.dp(10.0f);
    protected static final int PICKER_PADDING = AndroidUtilities.dp(16.0f);
    private static final int PICKER_CAPTURE_WIDTH = AndroidUtilities.dp(24.0f);
    private static final int LANDSCAPE_END_PADDING = AndroidUtilities.dp(16.0f);
    private static final int BOTTOM_SIGNATURE_OFFSET = AndroidUtilities.dp(10.0f);
    private static final int DP_12 = AndroidUtilities.dp(12.0f);
    private static final int DP_8 = AndroidUtilities.dp(8.0f);
    private static final int DP_6 = AndroidUtilities.dp(6.0f);
    private static final int DP_5 = AndroidUtilities.dp(5.0f);
    private static final int DP_2 = AndroidUtilities.dp(2.0f);
    private static final int DP_1 = AndroidUtilities.dp(1.0f);

    public interface DateSelectionListener {
        void onDateSelected(long j);
    }

    public static class SharedUiComponents {
        private Canvas canvas;
        private boolean invalidate;
        int k;
        private Bitmap pickerRoundBitmap;
        private RectF rectF;
        private Theme.ResourcesProvider resourcesProvider;
        private Paint xRefP;

        public SharedUiComponents() {
            this(null);
        }

        public SharedUiComponents(Theme.ResourcesProvider resourcesProvider) {
            this.rectF = new RectF();
            Paint paint = new Paint(1);
            this.xRefP = paint;
            this.k = 0;
            this.invalidate = true;
            paint.setColor(0);
            this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            this.resourcesProvider = resourcesProvider;
        }

        Bitmap getPickerMaskBitmap(int i, int i2) {
            int i3 = (i + i2) << 10;
            if (i3 != this.k || this.invalidate) {
                this.invalidate = false;
                this.k = i3;
                this.pickerRoundBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                this.canvas = new Canvas(this.pickerRoundBitmap);
                this.rectF.set(0.0f, 0.0f, i2, i);
                this.canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
                this.canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.xRefP);
            }
            return this.pickerRoundBitmap;
        }

        public void invalidate() {
            this.invalidate = true;
        }
    }

    static {
        int i = Build.VERSION.SDK_INT;
        USE_LINES = i < 28;
        ANIMATE_PICKER_SIZES = i > 21;
        INTERPOLATOR = new FastOutSlowInInterpolator();
    }

    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.horizontalLines = new ArrayList(10);
        this.bottomSignatureDate = new ArrayList(25);
        this.lines = new ArrayList();
        this.ANIM_DURATION = 400;
        this.drawPointOnSelection = true;
        this.currentMaxHeight = 250.0f;
        this.currentMinHeight = 0.0f;
        this.animateToMaxHeight = 0.0f;
        this.animateToMinHeight = 0.0f;
        this.thresholdMaxHeight = 0.0f;
        this.invalidatePickerChart = true;
        this.landscape = false;
        this.enabled = true;
        this.emptyPaint = new Paint();
        this.linePaint = new Paint();
        this.selectedLinePaint = new Paint();
        this.signaturePaint = new TextPaint(1);
        this.signaturePaint2 = new TextPaint(1);
        this.bottomSignaturePaint = new TextPaint(1);
        this.pickerSelectorPaint = new Paint(1);
        this.unactiveBottomChartPaint = new Paint();
        this.selectionBackgroundPaint = new Paint(1);
        this.ripplePaint = new Paint(1);
        this.whiteLinePaint = new Paint(1);
        this.pickerRect = new Rect();
        this.pathTmp = new Path();
        this.postTransition = false;
        this.pickerDelegate = new ChartPickerDelegate(this);
        this.chartCaptured = false;
        this.selectedIndex = -1;
        this.selectedCoordinate = -1.0f;
        this.legendShowing = false;
        this.selectionA = 0.0f;
        this.superDraw = false;
        this.useAlphaSignature = false;
        this.transitionMode = 0;
        this.pikerHeight = AndroidUtilities.dp(46.0f);
        this.chartArea = new RectF();
        this.pickerHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.pickerMaxHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BaseChartView baseChartView = BaseChartView.this;
                baseChartView.invalidatePickerChart = true;
                baseChartView.invalidate();
            }
        };
        this.pickerMinHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.pickerMinHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BaseChartView baseChartView = BaseChartView.this;
                baseChartView.invalidatePickerChart = true;
                baseChartView.invalidate();
            }
        };
        this.heightUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.lambda$new$0(valueAnimator);
            }
        };
        this.minHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.lambda$new$1(valueAnimator);
            }
        };
        this.selectionAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.selectionA = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BaseChartView baseChartView = BaseChartView.this;
                baseChartView.legendSignatureView.setAlpha(baseChartView.selectionA);
                BaseChartView.this.invalidate();
            }
        };
        this.selectorAnimatorEndListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                BaseChartView baseChartView = BaseChartView.this;
                if (!baseChartView.animateLegentTo) {
                    baseChartView.legendShowing = false;
                    baseChartView.legendSignatureView.setVisibility(8);
                    BaseChartView.this.invalidate();
                }
                BaseChartView.this.postTransition = false;
            }
        };
        this.useMinHeight = false;
        this.lastW = 0;
        this.lastH = 0;
        this.exclusionRect = new Rect();
        ArrayList arrayList = new ArrayList();
        this.exclusionRects = arrayList;
        arrayList.add(this.exclusionRect);
        this.lastTime = 0L;
        this.animateLegentTo = false;
        this.resourcesProvider = resourcesProvider;
        init();
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public static Path RoundedRect(Path path, float f, float f2, float f3, float f4, float f5, float f6, boolean z, boolean z2, boolean z3, boolean z4) {
        path.reset();
        if (f5 < 0.0f) {
            f5 = 0.0f;
        }
        if (f6 < 0.0f) {
            f6 = 0.0f;
        }
        float f7 = f3 - f;
        float f8 = f4 - f2;
        float f9 = f7 / 2.0f;
        if (f5 > f9) {
            f5 = f9;
        }
        float f10 = f8 / 2.0f;
        if (f6 > f10) {
            f6 = f10;
        }
        float f11 = f7 - (f5 * 2.0f);
        float f12 = f8 - (2.0f * f6);
        path.moveTo(f3, f2 + f6);
        float f13 = -f6;
        if (z2) {
            path.rQuadTo(0.0f, f13, -f5, f13);
        } else {
            path.rLineTo(0.0f, f13);
            path.rLineTo(-f5, 0.0f);
        }
        path.rLineTo(-f11, 0.0f);
        float f14 = -f5;
        if (z) {
            path.rQuadTo(f14, 0.0f, f14, f6);
        } else {
            path.rLineTo(f14, 0.0f);
            path.rLineTo(0.0f, f6);
        }
        path.rLineTo(0.0f, f12);
        if (z4) {
            path.rQuadTo(0.0f, f6, f5, f6);
        } else {
            path.rLineTo(0.0f, f6);
            path.rLineTo(f5, 0.0f);
        }
        path.rLineTo(f11, 0.0f);
        if (z3) {
            path.rQuadTo(f5, 0.0f, f5, -f6);
        } else {
            path.rLineTo(f5, 0.0f);
            path.rLineTo(0.0f, -f6);
        }
        path.rLineTo(0.0f, -f12);
        path.close();
        return path;
    }

    public void lambda$new$0(ValueAnimator valueAnimator) {
        this.currentMaxHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$new$1(ValueAnimator valueAnimator) {
        this.currentMinHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$onCheckChanged$4(LineViewData lineViewData, ValueAnimator valueAnimator) {
        lineViewData.alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    public void lambda$onCheckChanged$5(LineViewData lineViewData, ValueAnimator valueAnimator) {
        lineViewData.alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    public void lambda$setMaxMinValue$2(ChartHorizontalLinesData chartHorizontalLinesData, ValueAnimator valueAnimator) {
        chartHorizontalLinesData.alpha = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
        Iterator it = this.horizontalLines.iterator();
        while (it.hasNext()) {
            ChartHorizontalLinesData chartHorizontalLinesData2 = (ChartHorizontalLinesData) it.next();
            if (chartHorizontalLinesData2 != chartHorizontalLinesData) {
                chartHorizontalLinesData2.alpha = (int) ((chartHorizontalLinesData2.fixedAlpha / 255.0f) * (255 - chartHorizontalLinesData.alpha));
            }
        }
        invalidate();
    }

    public void lambda$updateDates$3(ChartBottomSignatureData chartBottomSignatureData, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        Iterator it = this.bottomSignatureDate.iterator();
        while (it.hasNext()) {
            ChartBottomSignatureData chartBottomSignatureData2 = (ChartBottomSignatureData) it.next();
            if (chartBottomSignatureData2 == chartBottomSignatureData) {
                chartBottomSignatureData.alpha = (int) (255.0f * floatValue);
            } else {
                chartBottomSignatureData2.alpha = (int) ((1.0f - floatValue) * chartBottomSignatureData2.fixedAlpha);
            }
        }
        invalidate();
    }

    private void measureHeightThreshold() {
        int measuredHeight = getMeasuredHeight() - this.chartBottom;
        float f = this.animateToMaxHeight;
        if (f == 0.0f || measuredHeight == 0) {
            return;
        }
        this.thresholdMaxHeight = (f / measuredHeight) * SIGNATURE_TEXT_SIZE;
    }

    private void measureSizes() {
        if (getMeasuredHeight() <= 0 || getMeasuredWidth() <= 0) {
            return;
        }
        float measuredWidth = getMeasuredWidth();
        float f = HORIZONTAL_PADDING;
        this.pickerWidth = measuredWidth - (2.0f * f);
        this.chartStart = f;
        float measuredWidth2 = getMeasuredWidth() - (this.landscape ? LANDSCAPE_END_PADDING : f);
        this.chartEnd = measuredWidth2;
        float f2 = measuredWidth2 - this.chartStart;
        this.chartWidth = f2;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        this.chartFullWidth = f2 / (chartPickerDelegate.pickerEnd - chartPickerDelegate.pickerStart);
        updateLineSignature();
        this.chartBottom = AndroidUtilities.dp(100.0f);
        this.chartArea.set(this.chartStart - f, 0.0f, this.chartEnd + f, getMeasuredHeight() - this.chartBottom);
        if (this.chartData != null) {
            this.bottomSignatureOffset = (int) (AndroidUtilities.dp(20.0f) / (this.pickerWidth / this.chartData.x.length));
        }
        measureHeightThreshold();
    }

    private void setMaxMinValue(long j, long j2, boolean z) {
        setMaxMinValue(j, j2, z, false, false);
    }

    private void updateDates(int i) {
        ChartBottomSignatureData chartBottomSignatureData = this.currentBottomSignatures;
        if (chartBottomSignatureData == null || i >= chartBottomSignatureData.stepMax || i <= chartBottomSignatureData.stepMin) {
            int highestOneBit = Integer.highestOneBit(i) << 1;
            ChartBottomSignatureData chartBottomSignatureData2 = this.currentBottomSignatures;
            if (chartBottomSignatureData2 == null || chartBottomSignatureData2.step != highestOneBit) {
                ValueAnimator valueAnimator = this.alphaBottomAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.alphaBottomAnimator.cancel();
                }
                double d = highestOneBit;
                Double.isNaN(d);
                double d2 = 0.2d * d;
                Double.isNaN(d);
                Double.isNaN(d);
                final ChartBottomSignatureData chartBottomSignatureData3 = new ChartBottomSignatureData(highestOneBit, (int) (d + d2), (int) (d - d2));
                chartBottomSignatureData3.alpha = 255;
                ChartBottomSignatureData chartBottomSignatureData4 = this.currentBottomSignatures;
                this.currentBottomSignatures = chartBottomSignatureData3;
                if (chartBottomSignatureData4 == null) {
                    chartBottomSignatureData3.alpha = 255;
                    this.bottomSignatureDate.add(chartBottomSignatureData3);
                    return;
                }
                this.tmpN = this.bottomSignatureDate.size();
                for (int i2 = 0; i2 < this.tmpN; i2++) {
                    ChartBottomSignatureData chartBottomSignatureData5 = (ChartBottomSignatureData) this.bottomSignatureDate.get(i2);
                    chartBottomSignatureData5.fixedAlpha = chartBottomSignatureData5.alpha;
                }
                this.bottomSignatureDate.add(chartBottomSignatureData3);
                if (this.bottomSignatureDate.size() > 2) {
                    this.bottomSignatureDate.remove(0);
                }
                ValueAnimator duration = createAnimator(0.0f, 1.0f, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        BaseChartView.this.lambda$updateDates$3(chartBottomSignatureData3, valueAnimator2);
                    }
                }).setDuration(200L);
                this.alphaBottomAnimator = duration;
                duration.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        BaseChartView.this.bottomSignatureDate.clear();
                        BaseChartView.this.bottomSignatureDate.add(chartBottomSignatureData3);
                    }
                });
                this.alphaBottomAnimator.start();
            }
        }
    }

    private void updateLineSignature() {
        ChartData chartData = this.chartData;
        if (chartData != null) {
            float f = this.chartWidth;
            if (f == 0.0f) {
                return;
            }
            updateDates((int) ((f / (this.chartFullWidth * chartData.oneDayPercentage)) / 6.0f));
        }
    }

    public void animateLegend(boolean z) {
        moveLegend();
        if (this.animateLegentTo == z) {
            return;
        }
        this.animateLegentTo = z;
        ValueAnimator valueAnimator = this.selectionAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.selectionAnimator.cancel();
        }
        ValueAnimator duration = createAnimator(this.selectionA, z ? 1.0f : 0.0f, this.selectionAnimatorListener).setDuration(200L);
        this.selectionAnimator = duration;
        duration.addListener(this.selectorAnimatorEndListener);
        this.selectionAnimator.start();
    }

    public void clearSelection() {
        this.selectedIndex = -1;
        this.legendShowing = false;
        this.animateLegentTo = false;
        this.legendSignatureView.setVisibility(8);
        this.selectionA = 0.0f;
    }

    public ValueAnimator createAnimator(float f, float f2, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
        ofFloat.setDuration(400L);
        ofFloat.setInterpolator(INTERPOLATOR);
        ofFloat.addUpdateListener(animatorUpdateListener);
        return ofFloat;
    }

    protected ChartHorizontalLinesData createHorizontalLinesData(long j, long j2, int i) {
        return new ChartHorizontalLinesData(j, j2, this.useMinHeight, this.chartData.yRate, i, this.signaturePaint, this.signaturePaint2);
    }

    protected LegendSignatureView createLegendView() {
        return new LegendSignatureView(getContext(), this.resourcesProvider);
    }

    public abstract LineViewData createLineViewData(ChartData.Line line);

    public void drawBottomLine(Canvas canvas) {
        if (this.chartData == null) {
            return;
        }
        int i = this.transitionMode;
        float f = 1.0f;
        if (i == 2) {
            f = 1.0f - this.transitionParams.progress;
        } else if (i == 1 || i == 3) {
            f = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (this.hintLinePaintAlpha * f));
        this.signaturePaint.setAlpha((int) (this.signaturePaintAlpha * 255.0f * f));
        this.signaturePaint2.setAlpha((int) (this.signaturePaintAlpha * 255.0f * f));
        int textSize = (int) (SIGNATURE_TEXT_HEIGHT - this.signaturePaint.getTextSize());
        float measuredHeight = (getMeasuredHeight() - this.chartBottom) - 1;
        canvas.drawLine(this.chartStart, measuredHeight, this.chartEnd, measuredHeight, this.linePaint);
        if (this.useMinHeight) {
            return;
        }
        canvas.drawText("0", HORIZONTAL_PADDING, r1 - textSize, this.signaturePaint);
    }

    public void drawBottomSignature(Canvas canvas) {
        float f;
        if (this.chartData == null) {
            return;
        }
        this.tmpN = this.bottomSignatureDate.size();
        int i = this.transitionMode;
        float f2 = i == 2 ? 1.0f - this.transitionParams.progress : (i == 1 || i == 3) ? this.transitionParams.progress : 1.0f;
        this.tmpI = 0;
        while (true) {
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                return;
            }
            int i3 = ((ChartBottomSignatureData) this.bottomSignatureDate.get(i2)).alpha;
            int i4 = ((ChartBottomSignatureData) this.bottomSignatureDate.get(this.tmpI)).step;
            if (i4 == 0) {
                i4 = 1;
            }
            int i5 = this.startXIndex - this.bottomSignatureOffset;
            while (i5 % i4 != 0) {
                i5--;
            }
            int i6 = this.endXIndex - this.bottomSignatureOffset;
            while (true) {
                if (i6 % i4 == 0 && i6 >= this.chartData.x.length - 1) {
                    break;
                } else {
                    i6++;
                }
            }
            int i7 = this.bottomSignatureOffset;
            int i8 = i6 + i7;
            float f3 = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
            for (int i9 = i5 + i7; i9 < i8; i9 += i4) {
                if (i9 >= 0) {
                    long[] jArr = this.chartData.x;
                    if (i9 < jArr.length - 1) {
                        long j = jArr[i9];
                        long j2 = jArr[0];
                        float f4 = ((((float) (j - j2)) / ((float) (jArr[jArr.length - 1] - j2))) * this.chartFullWidth) - f3;
                        float f5 = f4 - BOTTOM_SIGNATURE_OFFSET;
                        if (f5 > 0.0f) {
                            float f6 = this.chartWidth;
                            float f7 = HORIZONTAL_PADDING;
                            if (f5 <= f6 + f7) {
                                float f8 = BOTTOM_SIGNATURE_START_ALPHA;
                                if (f5 < f8) {
                                    f = (f8 - f5) / f8;
                                } else if (f5 > f6) {
                                    f = (f5 - f6) / f7;
                                } else {
                                    this.bottomSignaturePaint.setAlpha((int) (i3 * this.bottomSignaturePaintAlpha * f2));
                                    canvas.drawText(this.chartData.getDayString(i9), f4, (getMeasuredHeight() - this.chartBottom) + BOTTOM_SIGNATURE_TEXT_HEIGHT + AndroidUtilities.dp(3.0f), this.bottomSignaturePaint);
                                }
                                this.bottomSignaturePaint.setAlpha((int) (i3 * (1.0f - f) * this.bottomSignaturePaintAlpha * f2));
                                canvas.drawText(this.chartData.getDayString(i9), f4, (getMeasuredHeight() - this.chartBottom) + BOTTOM_SIGNATURE_TEXT_HEIGHT + AndroidUtilities.dp(3.0f), this.bottomSignaturePaint);
                            }
                        }
                    }
                }
            }
            this.tmpI++;
        }
    }

    protected abstract void drawChart(Canvas canvas);

    public void drawHorizontalLines(android.graphics.Canvas r12, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawHorizontalLines(android.graphics.Canvas, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData):void");
    }

    public void drawPicker(android.graphics.Canvas r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawPicker(android.graphics.Canvas):void");
    }

    protected abstract void drawPickerChart(Canvas canvas);

    public void drawSelection(Canvas canvas) {
        ChartData chartData;
        int i = this.selectedIndex;
        if (i < 0 || !this.legendShowing || (chartData = this.chartData) == null) {
            return;
        }
        int i2 = (int) (this.chartActiveLineAlpha * this.selectionA);
        float f = this.chartWidth;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        float f2 = chartPickerDelegate.pickerEnd;
        float f3 = chartPickerDelegate.pickerStart;
        float f4 = f / (f2 - f3);
        float f5 = (f3 * f4) - HORIZONTAL_PADDING;
        float[] fArr = chartData.xPercentage;
        if (i >= fArr.length) {
            return;
        }
        float f6 = (fArr[i] * f4) - f5;
        this.selectedLinePaint.setAlpha(i2);
        canvas.drawLine(f6, 0.0f, f6, this.chartArea.bottom, this.selectedLinePaint);
        if (!this.drawPointOnSelection) {
            return;
        }
        this.tmpN = this.lines.size();
        int i3 = 0;
        while (true) {
            this.tmpI = i3;
            int i4 = this.tmpI;
            if (i4 >= this.tmpN) {
                return;
            }
            LineViewData lineViewData = (LineViewData) this.lines.get(i4);
            if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                float f7 = (float) lineViewData.line.y[this.selectedIndex];
                float f8 = this.currentMinHeight;
                float measuredHeight = (getMeasuredHeight() - this.chartBottom) - (((f7 - f8) / (this.currentMaxHeight - f8)) * ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT));
                lineViewData.selectionPaint.setAlpha((int) (lineViewData.alpha * 255.0f * this.selectionA));
                this.selectionBackgroundPaint.setAlpha((int) (lineViewData.alpha * 255.0f * this.selectionA));
                canvas.drawPoint(f6, measuredHeight, lineViewData.selectionPaint);
                canvas.drawPoint(f6, measuredHeight, this.selectionBackgroundPaint);
            }
            i3 = this.tmpI + 1;
        }
    }

    public void drawSignaturesToHorizontalLines(android.graphics.Canvas r14, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawSignaturesToHorizontalLines(android.graphics.Canvas, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData):void");
    }

    public void fillTransitionParams(TransitionParams transitionParams) {
    }

    public long findMaxValue(int i, int i2) {
        int size = this.lines.size();
        long j = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (((LineViewData) this.lines.get(i3)).enabled) {
                long rMaxQ = ((LineViewData) this.lines.get(i3)).line.segmentTree.rMaxQ(i, i2);
                if (rMaxQ > j) {
                    j = rMaxQ;
                }
            }
        }
        return j;
    }

    public long findMinValue(int i, int i2) {
        int size = this.lines.size();
        long j = Long.MAX_VALUE;
        for (int i3 = 0; i3 < size; i3++) {
            if (((LineViewData) this.lines.get(i3)).enabled) {
                long rMinQ = ((LineViewData) this.lines.get(i3)).line.segmentTree.rMinQ(i, i2);
                if (rMinQ < j) {
                    j = rMinQ;
                }
            }
        }
        return j;
    }

    public long getEndDate() {
        return this.chartData.x[this.endXIndex];
    }

    protected float getMinDistance() {
        ChartData chartData = this.chartData;
        if (chartData == null) {
            return 0.1f;
        }
        int length = chartData.x.length;
        if (length < 5) {
            return 1.0f;
        }
        float f = 5.0f / length;
        if (f < 0.1f) {
            return 0.1f;
        }
        return f;
    }

    public long getSelectedDate() {
        int i = this.selectedIndex;
        if (i < 0) {
            return -1L;
        }
        return this.chartData.x[i];
    }

    public long getStartDate() {
        return this.chartData.x[this.startXIndex];
    }

    public void init() {
        this.linePaint.setStrokeWidth(1.0f);
        this.selectedLinePaint.setStrokeWidth(SELECTED_LINE_WIDTH);
        TextPaint textPaint = this.signaturePaint;
        float f = SIGNATURE_TEXT_SIZE;
        textPaint.setTextSize(f);
        this.signaturePaint2.setTextSize(f);
        this.signaturePaint2.setTextAlign(Paint.Align.RIGHT);
        this.bottomSignaturePaint.setTextSize(f);
        this.bottomSignaturePaint.setTextAlign(Paint.Align.CENTER);
        this.selectionBackgroundPaint.setStrokeWidth(AndroidUtilities.dpf2(6.0f));
        Paint paint = this.selectionBackgroundPaint;
        Paint.Cap cap = Paint.Cap.ROUND;
        paint.setStrokeCap(cap);
        setLayerType(2, null);
        setWillNotDraw(false);
        LegendSignatureView createLegendView = createLegendView();
        this.legendSignatureView = createLegendView;
        createLegendView.setVisibility(8);
        this.whiteLinePaint.setColor(-1);
        this.whiteLinePaint.setStrokeWidth(AndroidUtilities.dpf2(3.0f));
        this.whiteLinePaint.setStrokeCap(cap);
        updateColors();
    }

    public void initPickerMaxHeight() {
        Iterator it = this.lines.iterator();
        while (it.hasNext()) {
            LineViewData lineViewData = (LineViewData) it.next();
            boolean z = lineViewData.enabled;
            if (z) {
                float f = (float) lineViewData.line.maxValue;
                if (f > this.pickerMaxHeight) {
                    this.pickerMaxHeight = f;
                }
            }
            if (z) {
                float f2 = (float) lineViewData.line.minValue;
                if (f2 < this.pickerMinHeight) {
                    this.pickerMinHeight = f2;
                }
            }
            float f3 = this.pickerMaxHeight;
            float f4 = this.pickerMinHeight;
            if (f3 == f4) {
                this.pickerMaxHeight = f3 + 1.0f;
                this.pickerMinHeight = f4 - 1.0f;
            }
        }
    }

    public void moveLegend() {
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
    }

    public void moveLegend(float f) {
        int i;
        ChartData chartData = this.chartData;
        if (chartData == null || (i = this.selectedIndex) < 0) {
            return;
        }
        long[] jArr = chartData.x;
        if (i >= jArr.length || !this.legendShowing) {
            return;
        }
        this.legendSignatureView.setData(i, jArr[i], this.lines, false, chartData.yTooltipFormatter, chartData.yRate);
        this.legendSignatureView.setVisibility(0);
        this.legendSignatureView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
        float f2 = (this.chartData.xPercentage[this.selectedIndex] * this.chartFullWidth) - f;
        float width = f2 > (this.chartStart + this.chartWidth) / 2.0f ? f2 - (this.legendSignatureView.getWidth() + DP_5) : f2 + DP_5;
        if (width < 0.0f) {
            width = 0.0f;
        } else if (this.legendSignatureView.getMeasuredWidth() + width > getMeasuredWidth()) {
            width = getMeasuredWidth() - this.legendSignatureView.getMeasuredWidth();
        }
        this.legendSignatureView.setTranslationX(width);
    }

    protected void onActionUp() {
    }

    public void onCheckChanged() {
        ValueAnimator valueAnimator;
        ValueAnimator valueAnimator2;
        ValueAnimator valueAnimator3;
        onPickerDataChanged(true, true, true);
        this.tmpN = this.lines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            final LineViewData lineViewData = (LineViewData) this.lines.get(i2);
            if (lineViewData.enabled && (valueAnimator3 = lineViewData.animatorOut) != null) {
                valueAnimator3.cancel();
            }
            if (!lineViewData.enabled && (valueAnimator2 = lineViewData.animatorIn) != null) {
                valueAnimator2.cancel();
            }
            if (lineViewData.enabled && lineViewData.alpha != 1.0f) {
                ValueAnimator valueAnimator4 = lineViewData.animatorIn;
                if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                    ValueAnimator createAnimator = createAnimator(lineViewData.alpha, 1.0f, new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator5) {
                            BaseChartView.this.lambda$onCheckChanged$4(lineViewData, valueAnimator5);
                        }
                    });
                    lineViewData.animatorIn = createAnimator;
                    createAnimator.start();
                } else {
                    i = this.tmpI + 1;
                }
            }
            if (!lineViewData.enabled && lineViewData.alpha != 0.0f && ((valueAnimator = lineViewData.animatorOut) == null || !valueAnimator.isRunning())) {
                ValueAnimator createAnimator2 = createAnimator(lineViewData.alpha, 0.0f, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator5) {
                        BaseChartView.this.lambda$onCheckChanged$5(lineViewData, valueAnimator5);
                    }
                });
                lineViewData.animatorOut = createAnimator2;
                createAnimator2.start();
            }
            i = this.tmpI + 1;
        }
        updatePickerMinMaxHeight();
        if (this.legendShowing) {
            LegendSignatureView legendSignatureView = this.legendSignatureView;
            int i3 = this.selectedIndex;
            ChartData chartData = this.chartData;
            legendSignatureView.setData(i3, chartData.x[i3], this.lines, true, chartData.yTooltipFormatter, chartData.yRate);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (this.superDraw) {
            super.onDraw(canvas);
            return;
        }
        tick();
        int save = canvas.save();
        canvas.clipRect(0.0f, this.chartArea.top, getMeasuredWidth(), this.chartArea.bottom);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        this.tmpI = 0;
        while (true) {
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            drawHorizontalLines(canvas, (ChartHorizontalLinesData) this.horizontalLines.get(i2));
            this.tmpI++;
        }
        drawChart(canvas);
        while (true) {
            this.tmpI = i;
            int i3 = this.tmpI;
            if (i3 >= this.tmpN) {
                canvas.restoreToCount(save);
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
            drawSignaturesToHorizontalLines(canvas, (ChartHorizontalLinesData) this.horizontalLines.get(i3));
            i = this.tmpI + 1;
        }
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.landscape) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.displaySize.y - AndroidUtilities.dp(56.0f));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        }
        if (getMeasuredWidth() != this.lastW || getMeasuredHeight() != this.lastH) {
            this.lastW = getMeasuredWidth();
            this.lastH = getMeasuredHeight();
            float measuredWidth = getMeasuredWidth();
            float f = HORIZONTAL_PADDING;
            float f2 = 2.0f * f;
            this.bottomChartBitmap = Bitmap.createBitmap((int) (measuredWidth - f2), this.pikerHeight, Bitmap.Config.ARGB_4444);
            this.bottomChartCanvas = new Canvas(this.bottomChartBitmap);
            this.sharedUiComponents.getPickerMaskBitmap(this.pikerHeight, (int) (getMeasuredWidth() - f2));
            measureSizes();
            if (this.legendShowing) {
                moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - f);
            }
            onPickerDataChanged(false, true, false);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            Rect rect = this.exclusionRect;
            int measuredHeight = getMeasuredHeight();
            int i3 = PICKER_PADDING;
            rect.set(0, measuredHeight - ((this.pikerHeight + i3) + i3), getMeasuredWidth(), getMeasuredHeight());
            setSystemGestureExclusionRects(this.exclusionRects);
        }
    }

    @Override
    public void onPickerDataChanged() {
        onPickerDataChanged(true, false, false);
    }

    public void onPickerDataChanged(boolean z, boolean z2, boolean z3) {
        if (this.chartData == null) {
            return;
        }
        float f = this.chartWidth;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        this.chartFullWidth = f / (chartPickerDelegate.pickerEnd - chartPickerDelegate.pickerStart);
        updateIndexes();
        setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0L, z, z2, z3);
        if (this.legendShowing && !z2) {
            animateLegend(false);
            moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
        }
        invalidate();
    }

    @Override
    public void onPickerJumpTo(float f, float f2, boolean z) {
        ChartData chartData = this.chartData;
        if (chartData == null) {
            return;
        }
        if (!z) {
            updateIndexes();
            invalidate();
        } else {
            int findStartIndex = chartData.findStartIndex(Math.max(f, 0.0f));
            int findEndIndex = this.chartData.findEndIndex(findStartIndex, Math.min(f2, 1.0f));
            setMaxMinValue(findMaxValue(findStartIndex, findEndIndex), findMinValue(findStartIndex, findEndIndex), true, true, false);
            animateLegend(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.chartData == null) {
            return false;
        }
        if (!this.enabled) {
            this.pickerDelegate.uncapture(motionEvent, motionEvent.getActionIndex());
            getParent().requestDisallowInterceptTouchEvent(false);
            this.chartCaptured = false;
            return false;
        }
        int x = (int) motionEvent.getX(motionEvent.getActionIndex());
        int y = (int) motionEvent.getY(motionEvent.getActionIndex());
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.capturedTime = System.currentTimeMillis();
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.pickerDelegate.capture(x, y, motionEvent.getActionIndex())) {
                return true;
            }
            this.lastX = x;
            this.capturedX = x;
            this.lastY = y;
            this.capturedY = y;
            if (!this.chartArea.contains(x, y)) {
                return false;
            }
            if (this.selectedIndex < 0 || !this.animateLegentTo) {
                this.chartCaptured = true;
                selectXOnChart(x, y);
            }
            return true;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                int i = x - this.lastX;
                int i2 = y - this.lastY;
                if (this.pickerDelegate.captured()) {
                    boolean move = this.pickerDelegate.move(x, y, motionEvent.getActionIndex());
                    if (motionEvent.getPointerCount() > 1) {
                        this.pickerDelegate.move((int) motionEvent.getX(1), (int) motionEvent.getY(1), 1);
                    }
                    getParent().requestDisallowInterceptTouchEvent(move);
                    return true;
                }
                if (!this.chartCaptured) {
                    if (this.chartArea.contains(this.capturedX, this.capturedY)) {
                        int i3 = this.capturedX - x;
                        int i4 = this.capturedY - y;
                        if (Math.sqrt((i3 * i3) + (i4 * i4)) > this.touchSlop || System.currentTimeMillis() - this.capturedTime > 200) {
                            this.chartCaptured = true;
                        }
                    }
                    return true;
                }
                boolean z = (this.canCaptureChartSelection && System.currentTimeMillis() - this.capturedTime > 200) || Math.abs(i) > Math.abs(i2) || Math.abs(i2) < this.touchSlop;
                this.lastX = x;
                this.lastY = y;
                getParent().requestDisallowInterceptTouchEvent(z);
                selectXOnChart(x, y);
                return true;
            }
            if (actionMasked != 3) {
                if (actionMasked == 5) {
                    return this.pickerDelegate.capture(x, y, motionEvent.getActionIndex());
                }
                if (actionMasked != 6) {
                    return false;
                }
                this.pickerDelegate.uncapture(motionEvent, motionEvent.getActionIndex());
                return true;
            }
        }
        if (this.pickerDelegate.uncapture(motionEvent, motionEvent.getActionIndex())) {
            return true;
        }
        if (this.chartArea.contains(this.capturedX, this.capturedY) && !this.chartCaptured) {
            animateLegend(false);
        }
        this.pickerDelegate.uncapture();
        updateLineSignature();
        getParent().requestDisallowInterceptTouchEvent(false);
        this.chartCaptured = false;
        onActionUp();
        invalidate();
        setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0L, true, true, false);
        return true;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    public void runSmoothHaptic() {
        VibrationEffect createWaveform;
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
            if (this.vibrationEffect == null) {
                createWaveform = VibrationEffect.createWaveform(new long[]{0, 2}, -1);
                this.vibrationEffect = createWaveform;
            }
            vibrator.cancel();
            vibrator.vibrate(this.vibrationEffect);
        }
    }

    public void selectDate(long j) {
        this.selectedIndex = Arrays.binarySearch(this.chartData.x, j);
        this.legendShowing = true;
        this.legendSignatureView.setVisibility(0);
        this.selectionA = 1.0f;
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    protected void selectXOnChart(int r7, int r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.selectXOnChart(int, int):void");
    }

    public boolean setData(org.telegram.ui.Charts.data.ChartData r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.setData(org.telegram.ui.Charts.data.ChartData):boolean");
    }

    public void setDateSelectionListener(DateSelectionListener dateSelectionListener) {
        this.dateSelectionListener = dateSelectionListener;
    }

    public void setHeader(ChartHeaderView chartHeaderView) {
        this.chartHeaderView = chartHeaderView;
    }

    public void setLandscape(boolean z) {
        this.landscape = z;
    }

    protected void setMaxMinValue(long j, long j2, boolean z, boolean z2, boolean z3) {
        if ((Math.abs(((float) ChartHorizontalLinesData.lookupHeight(j)) - this.animateToMaxHeight) < this.thresholdMaxHeight || j == 0) && ((float) j) == this.animateToMinHeight) {
            return;
        }
        final ChartHorizontalLinesData createHorizontalLinesData = createHorizontalLinesData(j, j2, this.chartData.yTickFormatter);
        long[] jArr = createHorizontalLinesData.values;
        long j3 = jArr[jArr.length - 1];
        long j4 = jArr[0];
        if (!z3) {
            float f = this.currentMaxHeight - this.currentMinHeight;
            float f2 = (float) (j3 - j4);
            float f3 = f / f2;
            if (f3 > 1.0f) {
                f3 = f2 / f;
            }
            double d = f3;
            float f4 = d > 0.7d ? 0.1f : d < 0.1d ? 0.03f : 0.045f;
            boolean z4 = ((float) j3) != this.animateToMaxHeight;
            if (this.useMinHeight && ((float) j4) != this.animateToMinHeight) {
                z4 = true;
            }
            if (z4) {
                Animator animator = this.maxValueAnimator;
                if (animator != null) {
                    animator.removeAllListeners();
                    this.maxValueAnimator.cancel();
                }
                this.startFromMaxH = this.currentMaxHeight;
                this.startFromMinH = this.currentMinHeight;
                this.startFromMax = 0.0f;
                this.startFromMin = 0.0f;
                this.minMaxUpdateStep = f4;
            }
        }
        float f5 = (float) j3;
        this.animateToMaxHeight = f5;
        float f6 = (float) j4;
        this.animateToMinHeight = f6;
        measureHeightThreshold();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastTime >= 320 || z2) {
            this.lastTime = currentTimeMillis;
            ValueAnimator valueAnimator = this.alphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.alphaAnimator.cancel();
            }
            if (!z) {
                this.currentMaxHeight = f5;
                this.currentMinHeight = f6;
                this.horizontalLines.clear();
                this.horizontalLines.add(createHorizontalLinesData);
                createHorizontalLinesData.alpha = 255;
                return;
            }
            this.horizontalLines.add(createHorizontalLinesData);
            if (z3) {
                Animator animator2 = this.maxValueAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.maxValueAnimator.cancel();
                }
                this.minMaxUpdateStep = 0.0f;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(createAnimator(this.currentMaxHeight, f5, this.heightUpdateListener));
                if (this.useMinHeight) {
                    animatorSet.playTogether(createAnimator(this.currentMinHeight, f6, this.minHeightUpdateListener));
                }
                this.maxValueAnimator = animatorSet;
                animatorSet.start();
            }
            int size = this.horizontalLines.size();
            for (int i = 0; i < size; i++) {
                ChartHorizontalLinesData chartHorizontalLinesData = (ChartHorizontalLinesData) this.horizontalLines.get(i);
                if (chartHorizontalLinesData != createHorizontalLinesData) {
                    chartHorizontalLinesData.fixedAlpha = chartHorizontalLinesData.alpha;
                }
            }
            ValueAnimator createAnimator = createAnimator(0.0f, 255.0f, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BaseChartView.this.lambda$setMaxMinValue$2(createHorizontalLinesData, valueAnimator2);
                }
            });
            this.alphaAnimator = createAnimator;
            createAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator3) {
                    BaseChartView.this.horizontalLines.clear();
                    BaseChartView.this.horizontalLines.add(createHorizontalLinesData);
                }
            });
            this.alphaAnimator.start();
        }
    }

    public void tick() {
        float f = this.minMaxUpdateStep;
        if (f == 0.0f) {
            return;
        }
        float f2 = this.currentMaxHeight;
        float f3 = this.animateToMaxHeight;
        if (f2 != f3) {
            float f4 = this.startFromMax + f;
            this.startFromMax = f4;
            if (f4 > 1.0f) {
                this.startFromMax = 1.0f;
                this.currentMaxHeight = f3;
            } else {
                float f5 = this.startFromMaxH;
                this.currentMaxHeight = f5 + ((f3 - f5) * CubicBezierInterpolator.EASE_OUT.getInterpolation(f4));
            }
            invalidate();
        }
        if (this.useMinHeight) {
            float f6 = this.currentMinHeight;
            float f7 = this.animateToMinHeight;
            if (f6 != f7) {
                float f8 = this.startFromMin + this.minMaxUpdateStep;
                this.startFromMin = f8;
                if (f8 > 1.0f) {
                    this.startFromMin = 1.0f;
                    this.currentMinHeight = f7;
                } else {
                    float f9 = this.startFromMinH;
                    this.currentMinHeight = f9 + ((f7 - f9) * CubicBezierInterpolator.EASE_OUT.getInterpolation(f8));
                }
                invalidate();
            }
        }
    }

    public void updateColors() {
        this.signaturePaint.setColor(Theme.getColor(this.useAlphaSignature ? Theme.key_statisticChartSignatureAlpha : Theme.key_statisticChartSignature, this.resourcesProvider));
        this.signaturePaint2.setColor(Theme.getColor(this.useAlphaSignature ? Theme.key_statisticChartSignatureAlpha : Theme.key_statisticChartSignature, this.resourcesProvider));
        this.bottomSignaturePaint.setColor(Theme.getColor(Theme.key_statisticChartSignature, this.resourcesProvider));
        this.linePaint.setColor(Theme.getColor(Theme.key_statisticChartHintLine, this.resourcesProvider));
        this.selectedLinePaint.setColor(Theme.getColor(Theme.key_statisticChartActiveLine, this.resourcesProvider));
        this.pickerSelectorPaint.setColor(Theme.getColor(Theme.key_statisticChartActivePickerChart, this.resourcesProvider));
        this.unactiveBottomChartPaint.setColor(Theme.getColor(Theme.key_statisticChartInactivePickerChart, this.resourcesProvider));
        this.selectionBackgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
        this.ripplePaint.setColor(Theme.getColor(Theme.key_statisticChartRipple, this.resourcesProvider));
        this.legendSignatureView.recolor();
        this.hintLinePaintAlpha = this.linePaint.getAlpha();
        this.chartActiveLineAlpha = this.selectedLinePaint.getAlpha();
        this.signaturePaintAlpha = this.signaturePaint.getAlpha() / 255.0f;
        this.bottomSignaturePaintAlpha = this.bottomSignaturePaint.getAlpha() / 255.0f;
        Iterator it = this.lines.iterator();
        while (it.hasNext()) {
            ((LineViewData) it.next()).updateColors();
        }
        if (this.legendShowing) {
            int i = this.selectedIndex;
            ChartData chartData = this.chartData;
            long[] jArr = chartData.x;
            if (i < jArr.length) {
                this.legendSignatureView.setData(i, jArr[i], this.lines, false, chartData.yTooltipFormatter, chartData.yRate);
            }
        }
        this.invalidatePickerChart = true;
    }

    public void updateIndexes() {
        ChartData chartData = this.chartData;
        if (chartData == null) {
            return;
        }
        int findStartIndex = chartData.findStartIndex(Math.max(this.pickerDelegate.pickerStart, 0.0f));
        this.startXIndex = findStartIndex;
        int findEndIndex = this.chartData.findEndIndex(findStartIndex, Math.min(this.pickerDelegate.pickerEnd, 1.0f));
        this.endXIndex = findEndIndex;
        int i = this.startXIndex;
        if (findEndIndex < i) {
            this.endXIndex = i;
        }
        ChartHeaderView chartHeaderView = this.chartHeaderView;
        if (chartHeaderView != null) {
            long[] jArr = this.chartData.x;
            chartHeaderView.setDates(jArr[i], jArr[this.endXIndex]);
        }
        updateLineSignature();
    }

    public void updatePicker(ChartData chartData, long j) {
        int length = chartData.x.length;
        long j2 = j - (j % 86400000);
        long j3 = 86399999 + j2;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            long j4 = chartData.x[i3];
            if (j2 > j4) {
                i2 = i3;
            }
            if (j3 > j4) {
                i = i3;
            }
        }
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        float[] fArr = chartData.xPercentage;
        chartPickerDelegate.pickerStart = fArr[i2];
        chartPickerDelegate.pickerEnd = fArr[i];
    }

    public void updatePickerMinMaxHeight() {
        if (ANIMATE_PICKER_SIZES) {
            Iterator it = this.lines.iterator();
            long j = Long.MAX_VALUE;
            long j2 = 0;
            while (it.hasNext()) {
                LineViewData lineViewData = (LineViewData) it.next();
                boolean z = lineViewData.enabled;
                if (z) {
                    long j3 = lineViewData.line.maxValue;
                    if (j3 > j2) {
                        j2 = j3;
                    }
                }
                if (z) {
                    long j4 = lineViewData.line.minValue;
                    if (j4 < j) {
                        j = j4;
                    }
                }
            }
            if ((j == 2147483647L || ((float) j) == this.animatedToPickerMinHeight) && (j2 <= 0 || ((float) j2) == this.animatedToPickerMaxHeight)) {
                return;
            }
            this.animatedToPickerMaxHeight = (float) j2;
            Animator animator = this.pickerAnimator;
            if (animator != null) {
                animator.cancel();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, this.pickerHeightUpdateListener), createAnimator(this.pickerMinHeight, this.animatedToPickerMinHeight, this.pickerMinHeightUpdateListener));
            this.pickerAnimator = animatorSet;
            animatorSet.start();
        }
    }
}
