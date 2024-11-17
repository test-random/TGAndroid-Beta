package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.PieLegendView;
import org.telegram.ui.Charts.view_data.TransitionParams;

public class PieChartView extends StackLinearChartView {
    float MAX_TEXT_SIZE;
    float MIN_TEXT_SIZE;
    int currentSelection;
    float[] darawingValuesPercentage;
    float emptyDataAlpha;
    boolean isEmpty;
    int lastEndIndex;
    int lastStartIndex;
    String[] lookupTable;
    int oldW;
    PieLegendView pieLegendView;
    RectF rectF;
    float sum;
    TextPaint textPaint;
    float[] values;

    public PieChartView(Context context) {
        super(context);
        this.currentSelection = -1;
        this.rectF = new RectF();
        this.MIN_TEXT_SIZE = AndroidUtilities.dp(9.0f);
        this.MAX_TEXT_SIZE = AndroidUtilities.dp(13.0f);
        this.lookupTable = new String[101];
        this.emptyDataAlpha = 1.0f;
        this.oldW = 0;
        this.lastStartIndex = -1;
        this.lastEndIndex = -1;
        for (int i = 1; i <= 100; i++) {
            this.lookupTable[i] = i + "%";
        }
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setColor(-1);
        this.textPaint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.canCaptureChartSelection = true;
    }

    public void lambda$updateCharValues$0(PieChartViewData pieChartViewData, ValueAnimator valueAnimator) {
        pieChartViewData.drawingPart = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void updateCharValues(float f, float f2, boolean z) {
        if (this.values == null) {
            return;
        }
        int length = ((StackLinearChartData) this.chartData).xPercentage.length;
        int size = this.lines.size();
        int i = 0;
        int i2 = -1;
        int i3 = -1;
        for (int i4 = 0; i4 < length; i4++) {
            float f3 = ((StackLinearChartData) this.chartData).xPercentage[i4];
            if (f3 >= f && i3 == -1) {
                i3 = i4;
            }
            if (f3 <= f2) {
                i2 = i4;
            }
        }
        if (i2 < i3) {
            i3 = i2;
        }
        if (!z && this.lastEndIndex == i2 && this.lastStartIndex == i3) {
            return;
        }
        this.lastEndIndex = i2;
        this.lastStartIndex = i3;
        this.isEmpty = true;
        this.sum = 0.0f;
        for (int i5 = 0; i5 < size; i5++) {
            this.values[i5] = 0.0f;
        }
        while (i3 <= i2) {
            for (int i6 = 0; i6 < size; i6++) {
                float[] fArr = this.values;
                fArr[i6] = fArr[i6] + ((float) ((ChartData.Line) ((StackLinearChartData) this.chartData).lines.get(i6)).y[i3]);
                this.sum += (float) ((ChartData.Line) ((StackLinearChartData) this.chartData).lines.get(i6)).y[i3];
                if (this.isEmpty && ((PieChartViewData) this.lines.get(i6)).enabled && ((ChartData.Line) ((StackLinearChartData) this.chartData).lines.get(i6)).y[i3] > 0) {
                    this.isEmpty = false;
                }
            }
            i3++;
        }
        if (z) {
            while (i < size) {
                if (this.sum == 0.0f) {
                    ((PieChartViewData) this.lines.get(i)).drawingPart = 0.0f;
                } else {
                    ((PieChartViewData) this.lines.get(i)).drawingPart = this.values[i] / this.sum;
                }
                i++;
            }
            return;
        }
        while (i < size) {
            final PieChartViewData pieChartViewData = (PieChartViewData) this.lines.get(i);
            Animator animator = pieChartViewData.animator;
            if (animator != null) {
                animator.cancel();
            }
            float f4 = this.sum;
            ValueAnimator createAnimator = createAnimator(pieChartViewData.drawingPart, f4 == 0.0f ? 0.0f : this.values[i] / f4, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PieChartView.this.lambda$updateCharValues$0(pieChartViewData, valueAnimator);
                }
            });
            pieChartViewData.animator = createAnimator;
            createAnimator.start();
            i++;
        }
    }

    @Override
    protected LegendSignatureView createLegendView() {
        PieLegendView pieLegendView = new PieLegendView(getContext());
        this.pieLegendView = pieLegendView;
        return pieLegendView;
    }

    @Override
    public PieChartViewData createLineViewData(ChartData.Line line) {
        return new PieChartViewData(line);
    }

    @Override
    public void drawBottomLine(Canvas canvas) {
    }

    @Override
    public void drawBottomSignature(Canvas canvas) {
    }

    @Override
    protected void drawChart(android.graphics.Canvas r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.PieChartView.drawChart(android.graphics.Canvas):void");
    }

    @Override
    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
    }

    @Override
    protected void drawPickerChart(Canvas canvas) {
        int i;
        float f;
        float f2;
        float f3;
        int i2;
        ChartData chartData = this.chartData;
        if (chartData != null) {
            int length = ((StackLinearChartData) chartData).xPercentage.length;
            int size = this.lines.size();
            for (int i3 = 0; i3 < this.lines.size(); i3++) {
                ((LineViewData) this.lines.get(i3)).linesPathBottomSize = 0;
            }
            float length2 = (1.0f / ((StackLinearChartData) this.chartData).xPercentage.length) * this.pickerWidth;
            for (int i4 = 0; i4 < length; i4++) {
                float f4 = (length2 / 2.0f) + (((StackLinearChartData) this.chartData).xPercentage[i4] * (this.pickerWidth - length2));
                float f5 = 0.0f;
                int i5 = 0;
                float f6 = 0.0f;
                int i6 = 0;
                boolean z = true;
                while (i5 < size) {
                    LineViewData lineViewData = (LineViewData) this.lines.get(i5);
                    boolean z2 = lineViewData.enabled;
                    if (z2 || lineViewData.alpha != 0.0f) {
                        i2 = i5;
                        float f7 = ((float) lineViewData.line.y[i4]) * lineViewData.alpha;
                        f6 += f7;
                        if (f7 > 0.0f) {
                            i6++;
                            if (z2) {
                                z = false;
                            }
                        }
                    } else {
                        i2 = i5;
                    }
                    i5 = i2 + 1;
                }
                int i7 = 0;
                float f8 = 0.0f;
                while (i7 < size) {
                    LineViewData lineViewData2 = (LineViewData) this.lines.get(i7);
                    if (lineViewData2.enabled || lineViewData2.alpha != f5) {
                        long[] jArr = lineViewData2.line.y;
                        if (i6 == 1) {
                            if (jArr[i4] != 0) {
                                f3 = lineViewData2.alpha;
                                i = i7;
                                float f9 = this.pikerHeight;
                                float f10 = f3 * f9;
                                float[] fArr = lineViewData2.linesPath;
                                int i8 = lineViewData2.linesPathBottomSize;
                                fArr[i8] = f4;
                                fArr[i8 + 1] = (f9 - f10) - f8;
                                fArr[i8 + 2] = f4;
                                lineViewData2.linesPathBottomSize = i8 + 4;
                                fArr[i8 + 3] = f9 - f8;
                                f8 += f10;
                            }
                            i = i7;
                            f3 = 0.0f;
                            float f92 = this.pikerHeight;
                            float f102 = f3 * f92;
                            float[] fArr2 = lineViewData2.linesPath;
                            int i82 = lineViewData2.linesPathBottomSize;
                            fArr2[i82] = f4;
                            fArr2[i82 + 1] = (f92 - f102) - f8;
                            fArr2[i82 + 2] = f4;
                            lineViewData2.linesPathBottomSize = i82 + 4;
                            fArr2[i82 + 3] = f92 - f8;
                            f8 += f102;
                        } else {
                            if (f6 != f5) {
                                i = i7;
                                if (z) {
                                    float f11 = ((float) jArr[i4]) / f6;
                                    f2 = lineViewData2.alpha;
                                    f = f11 * f2;
                                } else {
                                    f = ((float) jArr[i4]) / f6;
                                    f2 = lineViewData2.alpha;
                                }
                                f3 = f * f2;
                                float f922 = this.pikerHeight;
                                float f1022 = f3 * f922;
                                float[] fArr22 = lineViewData2.linesPath;
                                int i822 = lineViewData2.linesPathBottomSize;
                                fArr22[i822] = f4;
                                fArr22[i822 + 1] = (f922 - f1022) - f8;
                                fArr22[i822 + 2] = f4;
                                lineViewData2.linesPathBottomSize = i822 + 4;
                                fArr22[i822 + 3] = f922 - f8;
                                f8 += f1022;
                            }
                            i = i7;
                            f3 = 0.0f;
                            float f9222 = this.pikerHeight;
                            float f10222 = f3 * f9222;
                            float[] fArr222 = lineViewData2.linesPath;
                            int i8222 = lineViewData2.linesPathBottomSize;
                            fArr222[i8222] = f4;
                            fArr222[i8222 + 1] = (f9222 - f10222) - f8;
                            fArr222[i8222 + 2] = f4;
                            lineViewData2.linesPathBottomSize = i8222 + 4;
                            fArr222[i8222 + 3] = f9222 - f8;
                            f8 += f10222;
                        }
                    } else {
                        i = i7;
                    }
                    i7 = i + 1;
                    f5 = 0.0f;
                }
            }
            for (int i9 = 0; i9 < size; i9++) {
                LineViewData lineViewData3 = (LineViewData) this.lines.get(i9);
                lineViewData3.paint.setStrokeWidth(length2);
                lineViewData3.paint.setAlpha(255);
                lineViewData3.paint.setAntiAlias(false);
                canvas.drawLines(lineViewData3.linesPath, 0, lineViewData3.linesPathBottomSize, lineViewData3.paint);
            }
        }
    }

    @Override
    public void drawSelection(Canvas canvas) {
    }

    @Override
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
    }

    @Override
    public void fillTransitionParams(TransitionParams transitionParams) {
        drawChart(null);
        float f = 0.0f;
        int i = 0;
        while (true) {
            float[] fArr = this.darawingValuesPercentage;
            if (i >= fArr.length) {
                return;
            }
            f += fArr[i];
            transitionParams.angle[i] = (360.0f * f) - 180.0f;
            i++;
        }
    }

    @Override
    protected void onActionUp() {
        this.currentSelection = -1;
        this.pieLegendView.setVisibility(8);
        invalidate();
    }

    @Override
    public void onDraw(android.graphics.Canvas r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.PieChartView.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredWidth() != this.oldW) {
            this.oldW = getMeasuredWidth();
            int height = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
            this.MIN_TEXT_SIZE = height / 13;
            this.MAX_TEXT_SIZE = height / 7;
        }
    }

    @Override
    public void onPickerDataChanged(boolean z, boolean z2, boolean z3) {
        super.onPickerDataChanged(z, z2, z3);
        ChartData chartData = this.chartData;
        if (chartData == null || ((StackLinearChartData) chartData).xPercentage == null) {
            return;
        }
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        updateCharValues(chartPickerDelegate.pickerStart, chartPickerDelegate.pickerEnd, z2);
    }

    @Override
    public void onPickerJumpTo(float f, float f2, boolean z) {
        if (this.chartData == null) {
            return;
        }
        if (z) {
            updateCharValues(f, f2, false);
        } else {
            updateIndexes();
            invalidate();
        }
    }

    @Override
    protected void selectXOnChart(int i, int i2) {
        if (this.chartData == null || this.isEmpty) {
            return;
        }
        float degrees = (float) (Math.toDegrees(Math.atan2((this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - i2, this.chartArea.centerX() - i)) - 90.0d);
        float f = 0.0f;
        if (degrees < 0.0f) {
            double d = degrees;
            Double.isNaN(d);
            degrees = (float) (d + 360.0d);
        }
        float f2 = degrees / 360.0f;
        int i3 = 0;
        float f3 = 0.0f;
        while (true) {
            if (i3 >= this.lines.size()) {
                i3 = -1;
                f3 = 0.0f;
                break;
            }
            if (((PieChartViewData) this.lines.get(i3)).enabled || ((PieChartViewData) this.lines.get(i3)).alpha != 0.0f) {
                if (f2 > f3) {
                    float f4 = this.darawingValuesPercentage[i3] + f3;
                    if (f2 < f4) {
                        f = f4;
                        break;
                    }
                }
                f3 += this.darawingValuesPercentage[i3];
            }
            i3++;
        }
        if (this.currentSelection != i3 && i3 >= 0) {
            this.currentSelection = i3;
            invalidate();
            this.pieLegendView.setVisibility(0);
            LineViewData lineViewData = (LineViewData) this.lines.get(i3);
            this.pieLegendView.setData(lineViewData.line.name, (int) this.values[this.currentSelection], lineViewData.lineColor);
            this.pieLegendView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
            float width = this.rectF.width() / 2.0f;
            double centerX = this.rectF.centerX();
            double d2 = width;
            double d3 = (f * 360.0f) - 90.0f;
            double cos = Math.cos(Math.toRadians(d3));
            Double.isNaN(d2);
            Double.isNaN(centerX);
            double d4 = centerX + (cos * d2);
            double centerX2 = this.rectF.centerX();
            double d5 = (f3 * 360.0f) - 90.0f;
            double cos2 = Math.cos(Math.toRadians(d5));
            Double.isNaN(d2);
            Double.isNaN(centerX2);
            int min = (int) Math.min(d4, centerX2 + (cos2 * d2));
            int i4 = min >= 0 ? min : 0;
            if (this.pieLegendView.getMeasuredWidth() + i4 > getMeasuredWidth() - AndroidUtilities.dp(16.0f)) {
                i4 -= (this.pieLegendView.getMeasuredWidth() + i4) - (getMeasuredWidth() - AndroidUtilities.dp(16.0f));
            }
            double centerY = this.rectF.centerY();
            double sin = Math.sin(Math.toRadians(d5));
            Double.isNaN(d2);
            Double.isNaN(centerY);
            double d6 = centerY + (sin * d2);
            double centerY2 = this.rectF.centerY();
            double sin2 = Math.sin(Math.toRadians(d3));
            Double.isNaN(d2);
            Double.isNaN(centerY2);
            int min2 = ((int) Math.min(this.rectF.centerY(), (int) Math.min(d6, centerY2 + (d2 * sin2)))) - AndroidUtilities.dp(50.0f);
            this.pieLegendView.setTranslationX(i4);
            this.pieLegendView.setTranslationY(min2);
            AndroidUtilities.vibrateCursor(this);
        }
        moveLegend();
    }

    @Override
    public boolean setData(StackLinearChartData stackLinearChartData) {
        boolean data = super.setData((ChartData) stackLinearChartData);
        if (stackLinearChartData != null) {
            this.values = new float[stackLinearChartData.lines.size()];
            this.darawingValuesPercentage = new float[stackLinearChartData.lines.size()];
            onPickerDataChanged(false, true, false);
        }
        return data;
    }

    @Override
    public void updatePicker(ChartData chartData, long j) {
        int length = chartData.x.length;
        long j2 = j - (j % 86400000);
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (j2 >= chartData.x[i2]) {
                i = i2;
            }
        }
        float length2 = chartData.xPercentage.length < 2 ? 0.5f : 1.0f / chartData.x.length;
        if (i == 0) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            chartPickerDelegate.pickerStart = 0.0f;
            chartPickerDelegate.pickerEnd = length2;
        } else {
            if (i >= chartData.x.length - 1) {
                ChartPickerDelegate chartPickerDelegate2 = this.pickerDelegate;
                chartPickerDelegate2.pickerStart = 1.0f - length2;
                chartPickerDelegate2.pickerEnd = 1.0f;
                return;
            }
            ChartPickerDelegate chartPickerDelegate3 = this.pickerDelegate;
            float f = i * length2;
            chartPickerDelegate3.pickerStart = f;
            float f2 = f + length2;
            chartPickerDelegate3.pickerEnd = f2;
            if (f2 > 1.0f) {
                chartPickerDelegate3.pickerEnd = 1.0f;
            }
            onPickerDataChanged(true, true, false);
        }
    }
}
