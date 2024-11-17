package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.StackLinearViewData;

public class StackLinearChartView extends BaseChartView {
    private float[] mapPoints;
    private Matrix matrix;
    Path ovalPath;
    boolean[] skipPoints;
    float[] startFromY;

    public StackLinearChartView(Context context) {
        super(context);
        this.matrix = new Matrix();
        this.mapPoints = new float[2];
        this.ovalPath = new Path();
        this.superDraw = true;
        this.useAlphaSignature = true;
        this.drawPointOnSelection = false;
    }

    private int quarterForPoint(float f, float f2) {
        float centerX = this.chartArea.centerX();
        float centerY = this.chartArea.centerY() + AndroidUtilities.dp(16.0f);
        if (f >= centerX && f2 <= centerY) {
            return 0;
        }
        if (f < centerX || f2 < centerY) {
            return (f >= centerX || f2 < centerY) ? 3 : 2;
        }
        return 1;
    }

    @Override
    public StackLinearViewData createLineViewData(ChartData.Line line) {
        return new StackLinearViewData(line);
    }

    @Override
    protected void drawChart(android.graphics.Canvas r41) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawChart(android.graphics.Canvas):void");
    }

    @Override
    protected void drawPickerChart(Canvas canvas) {
        long j;
        LineViewData lineViewData;
        float f;
        float f2;
        int i;
        boolean z;
        long j2;
        if (this.chartData != null) {
            int size = this.lines.size();
            for (int i2 = 0; i2 < size; i2++) {
                ((StackLinearViewData) this.lines.get(i2)).chartPathPicker.reset();
            }
            StackLinearChartData stackLinearChartData = (StackLinearChartData) this.chartData;
            int i3 = stackLinearChartData.simplifiedSize;
            boolean[] zArr = this.skipPoints;
            if (zArr == null || zArr.length < stackLinearChartData.lines.size()) {
                this.skipPoints = new boolean[((StackLinearChartData) this.chartData).lines.size()];
            }
            boolean z2 = false;
            for (int i4 = 0; i4 < i3; i4++) {
                float f3 = 0.0f;
                int i5 = 0;
                float f4 = 0.0f;
                int i6 = 0;
                int i7 = 0;
                while (true) {
                    j = 0;
                    if (i5 >= this.lines.size()) {
                        break;
                    }
                    LineViewData lineViewData2 = (LineViewData) this.lines.get(i5);
                    if (lineViewData2.enabled || lineViewData2.alpha != f3) {
                        long j3 = ((StackLinearChartData) this.chartData).simplifiedY[i5][i4];
                        if (j3 > 0) {
                            f4 += ((float) j3) * lineViewData2.alpha;
                            i6++;
                        }
                        i7 = i5;
                    }
                    i5++;
                    f3 = 0.0f;
                }
                int i8 = i3 - 1;
                float f5 = (i4 / i8) * this.pickerWidth;
                int i9 = 0;
                float f6 = 0.0f;
                while (i9 < this.lines.size()) {
                    LineViewData lineViewData3 = (LineViewData) this.lines.get(i9);
                    if (lineViewData3.enabled || lineViewData3.alpha != 0.0f) {
                        if (i6 == 1) {
                            lineViewData = lineViewData3;
                            if (((StackLinearChartData) this.chartData).simplifiedY[i9][i4] != j) {
                                f = lineViewData.alpha;
                                f2 = 0.0f;
                            }
                            f2 = 0.0f;
                            f = 0.0f;
                        } else {
                            lineViewData = lineViewData3;
                            if (f4 != 0.0f) {
                                f = (((float) ((StackLinearChartData) this.chartData).simplifiedY[i9][i4]) * lineViewData.alpha) / f4;
                                f2 = 0.0f;
                            }
                            f2 = 0.0f;
                            f = 0.0f;
                        }
                        if (f == f2 && i9 == i7) {
                            z2 = true;
                        }
                        float f7 = this.pikerHeight;
                        float f8 = f * f7;
                        float f9 = (f7 - f8) - f6;
                        i = i3;
                        if (i4 == 0) {
                            z = z2;
                            lineViewData.chartPathPicker.moveTo(0.0f, f7);
                            this.skipPoints[i9] = false;
                        } else {
                            z = z2;
                        }
                        long[] jArr = ((StackLinearChartData) this.chartData).simplifiedY[i9];
                        j2 = 0;
                        if (jArr[i4] == 0 && i4 > 0 && jArr[i4 - 1] == 0 && i4 < i8 && jArr[i4 + 1] == 0) {
                            if (!this.skipPoints[i9]) {
                                lineViewData.chartPathPicker.lineTo(f5, this.pikerHeight);
                            }
                            this.skipPoints[i9] = true;
                        } else {
                            if (this.skipPoints[i9]) {
                                lineViewData.chartPathPicker.lineTo(f5, this.pikerHeight);
                            }
                            lineViewData.chartPathPicker.lineTo(f5, f9);
                            this.skipPoints[i9] = false;
                        }
                        if (i4 == i8) {
                            lineViewData.chartPathPicker.lineTo(this.pickerWidth, this.pikerHeight);
                        }
                        f6 += f8;
                        z2 = z;
                    } else {
                        i = i3;
                        j2 = j;
                    }
                    i9++;
                    j = j2;
                    i3 = i;
                }
            }
            if (z2) {
                canvas.drawColor(Theme.getColor(Theme.key_statisticChartLineEmpty));
            }
            for (int size2 = this.lines.size() - 1; size2 >= 0; size2--) {
                LineViewData lineViewData4 = (LineViewData) this.lines.get(size2);
                canvas.drawPath(lineViewData4.chartPathPicker, lineViewData4.paint);
            }
        }
    }

    @Override
    public void fillTransitionParams(org.telegram.ui.Charts.view_data.TransitionParams r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.fillTransitionParams(org.telegram.ui.Charts.view_data.TransitionParams):void");
    }

    @Override
    public long findMaxValue(int i, int i2) {
        return 100L;
    }

    @Override
    protected float getMinDistance() {
        return 0.1f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        tick();
        drawChart(canvas);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
            drawHorizontalLines(canvas, (ChartHorizontalLinesData) this.horizontalLines.get(i2));
            drawSignaturesToHorizontalLines(canvas, (ChartHorizontalLinesData) this.horizontalLines.get(this.tmpI));
            i = this.tmpI + 1;
        }
    }
}
