package org.telegram.ui.Components.Paint;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.google.zxing.common.detector.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Size;

public class ShapeInput {
    private Point center;
    private Runnable invalidate;
    private Matrix invertMatrix;
    private Point movingPoint;
    private RenderView renderView;
    private Shape shape;
    private float touchOffsetX;
    private float touchOffsetY;
    private Paint linePaint = new Paint(1);
    private Paint centerPointPaint = new Paint(1);
    private Paint centerPointStrokePaint = new Paint(1);
    private Paint controlPointPaint = new Paint(1);
    private Paint controlPointStrokePaint = new Paint(1);
    private ArrayList allPoints = new ArrayList();
    private ArrayList movingPoints = new ArrayList();
    private float[] tempPoint = new float[2];

    private class CornerPoint extends Point {
        public float rx;
        public float ry;
        public Shape shape;

        public CornerPoint(Shape shape, boolean z, boolean z2) {
            super();
            this.rotate = false;
            this.shape = shape;
            this.rx = z ? -1.0f : 1.0f;
            this.ry = z2 ? -1.0f : 1.0f;
            set();
        }

        @Override
        void set() {
            Shape shape = this.shape;
            if (shape != null) {
                ShapeInput.this.rotate(shape.centerX + (this.rx * shape.radiusX), shape.centerY + (this.ry * shape.radiusY), true);
                set(ShapeInput.this.tempPoint[0], ShapeInput.this.tempPoint[1]);
            }
        }

        @Override
        protected void update(float f, float f2) {
            super.update(f, f2);
            Shape shape = this.shape;
            float f3 = shape.centerX + ((-this.rx) * shape.radiusX);
            float f4 = shape.centerY + ((-this.ry) * shape.radiusY);
            ShapeInput.this.rotate(f, f2, false);
            ShapeInput.this.rotate(f3, f4, true);
            float f5 = ShapeInput.this.tempPoint[0];
            float f6 = ShapeInput.this.tempPoint[1];
            double atan2 = 3.141592653589793d - Math.atan2(f2 - f6, f - f5);
            double d = this.shape.rotation;
            Double.isNaN(d);
            double d2 = atan2 - d;
            double cos = Math.cos(d2);
            double distance = MathUtils.distance(f, f2, f5, f6);
            Double.isNaN(distance);
            double d3 = cos * distance;
            double sin = Math.sin(d2);
            double distance2 = MathUtils.distance(f, f2, f5, f6);
            Double.isNaN(distance2);
            this.shape.radiusX = ((float) Math.abs(d3)) / 2.0f;
            this.shape.radiusY = ((float) Math.abs(sin * distance2)) / 2.0f;
            Shape shape2 = this.shape;
            shape2.centerX = (f + f5) / 2.0f;
            shape2.centerY = (f2 + f6) / 2.0f;
            for (int i = 0; i < ShapeInput.this.allPoints.size(); i++) {
                ((Point) ShapeInput.this.allPoints.get(i)).set();
            }
        }
    }

    public class Point {
        boolean green;
        float x;
        float y;
        boolean rotate = true;
        boolean draw = true;

        public Point() {
            set();
        }

        public Point(boolean z) {
            this.green = z;
            set();
        }

        abstract void set();

        void set(float f, float f2) {
            this.x = f;
            this.y = f2;
        }

        protected void update(float f, float f2) {
            this.x = f;
            this.y = f2;
        }
    }

    public ShapeInput(RenderView renderView, Runnable runnable) {
        this.renderView = renderView;
        this.invalidate = runnable;
        this.centerPointPaint.setColor(-13840296);
        Paint paint = this.centerPointStrokePaint;
        Paint.Style style = Paint.Style.STROKE;
        paint.setStyle(style);
        this.centerPointStrokePaint.setColor(-1);
        this.centerPointStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.controlPointPaint.setColor(-16745729);
        this.controlPointStrokePaint.setStyle(style);
        this.controlPointStrokePaint.setColor(-1);
        this.controlPointStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(style);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(0.8f));
        this.linePaint.setPathEffect(new DashPathEffect(new float[]{AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f)}, 0.0f));
        this.linePaint.setShadowLayer(4.0f, 0.0f, 1.5f, 1073741824);
    }

    public float distToLine(float f, float f2, float f3, float f4, double d) {
        double cos = Math.cos(d);
        double d2 = f4 - f2;
        Double.isNaN(d2);
        double sin = Math.sin(d);
        double d3 = f3 - f;
        Double.isNaN(d3);
        return (float) ((cos * d2) - (sin * d3));
    }

    private float distToLine(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = f5 - f3;
        float f8 = f6 - f4;
        float max = Math.max(Math.min((((f - f3) * f7) + ((f2 - f4) * f8)) / ((f7 * f7) + (f8 * f8)), 1.0f), 0.0f);
        float f9 = (f3 + (f7 * max)) - f;
        float f10 = (f4 + (max * f8)) - f2;
        return (float) Math.sqrt((f9 * f9) + (f10 * f10));
    }

    private void drawPoint(Canvas canvas, Size size, Point point) {
        canvas.drawCircle((point.x / size.width) * canvas.getWidth(), (point.y / size.height) * canvas.getHeight(), AndroidUtilities.dp(5.0f), point.green ? this.centerPointPaint : this.controlPointPaint);
        canvas.drawCircle((point.x / size.width) * canvas.getWidth(), (point.y / size.height) * canvas.getHeight(), AndroidUtilities.dp(5.0f), point.green ? this.centerPointStrokePaint : this.controlPointStrokePaint);
    }

    private boolean isInsideShape(float f, float f2) {
        float sqrt;
        float f3;
        Shape shape = this.shape;
        if (shape == null) {
            return false;
        }
        if (shape.getType() == 0 || this.shape.getType() == 2) {
            double sqrt2 = Math.sqrt(Math.pow(f - this.shape.centerX, 2.0d) + Math.pow(f2 - this.shape.centerY, 2.0d));
            Shape shape2 = this.shape;
            double min = Math.min(shape2.radiusX, shape2.radiusY);
            Double.isNaN(min);
            double d = sqrt2 - min;
            double d2 = this.shape.thickness / 2.0f;
            Double.isNaN(d2);
            return d - d2 < ((double) AndroidUtilities.dp(30.0f));
        }
        if (this.shape.getType() != 1 && this.shape.getType() != 3) {
            if (this.shape.getType() != 4) {
                return false;
            }
            Size size = this.renderView.getPainting().getSize();
            Shape shape3 = this.shape;
            float distToLine = distToLine(f, f2, shape3.centerX, shape3.centerY, shape3.middleX, shape3.middleY);
            Shape shape4 = this.shape;
            return Math.min(distToLine, distToLine(f, f2, shape4.radiusX, shape4.radiusY, shape4.middleX, shape4.middleY)) - (this.shape.thickness / 2.0f) < Math.min(size.width, size.height) * 0.1f;
        }
        Shape shape5 = this.shape;
        float f4 = shape5.centerX;
        float f5 = shape5.radiusX;
        float f6 = shape5.thickness / 2.0f;
        float f7 = (f4 - f5) - f6;
        float f8 = shape5.centerY;
        float f9 = shape5.radiusY;
        float f10 = (f8 - f9) - f6;
        float f11 = f4 + f5 + f6;
        float f12 = f8 + f9 + f6;
        if (f2 <= f10 || f2 >= f12) {
            if (f >= f7 || f <= f11) {
                double d3 = f - f7;
                double d4 = f2 - f10;
                double d5 = f - f11;
                double d6 = f2 - f12;
                sqrt = (float) Math.sqrt(Math.min(Math.min(Math.pow(d3, 2.0d) + Math.pow(d4, 2.0d), Math.pow(d5, 2.0d) + Math.pow(d4, 2.0d)), Math.min(Math.pow(d3, 2.0d) + Math.pow(d6, 2.0d), Math.pow(d5, 2.0d) + Math.pow(d6, 2.0d))));
            } else if (f2 < f10) {
                sqrt = f10 - f2;
            } else {
                if (f2 > f12) {
                    sqrt = f2 - f12;
                }
                f3 = 0.0f;
            }
            f3 = sqrt;
        } else {
            if (f < f7) {
                sqrt = f7 - f;
            } else {
                if (f > f11) {
                    sqrt = f - f11;
                }
                f3 = 0.0f;
            }
            f3 = sqrt;
        }
        if (this.shape.getType() == 3) {
            Shape shape6 = this.shape;
            f3 = Math.min(f3, distToLine(f, f2, shape6.centerX, shape6.centerY, shape6.middleX, shape6.middleY));
        }
        return f3 < ((float) AndroidUtilities.dp(30.0f));
    }

    public void rotate(float f, float f2, boolean z) {
        float[] fArr = this.tempPoint;
        fArr[0] = f;
        fArr[1] = f2;
        rotate(z);
    }

    private void rotate(boolean z) {
        Shape shape = this.shape;
        if (shape != null) {
            float f = shape.rotation;
            if (f != 0.0f) {
                float[] fArr = this.tempPoint;
                float f2 = fArr[0] - shape.centerX;
                fArr[0] = f2;
                fArr[1] = fArr[1] - shape.centerY;
                double d = f2;
                double d2 = f * (z ? -1 : 1);
                double cos = Math.cos(d2);
                Double.isNaN(d);
                double d3 = d * cos;
                double d4 = this.tempPoint[1];
                double sin = Math.sin(d2);
                Double.isNaN(d4);
                float f3 = (float) (d3 - (d4 * sin));
                double d5 = this.tempPoint[0];
                double sin2 = Math.sin(d2);
                Double.isNaN(d5);
                double d6 = d5 * sin2;
                double d7 = this.tempPoint[1];
                double cos2 = Math.cos(d2);
                Double.isNaN(d7);
                float[] fArr2 = this.tempPoint;
                Shape shape2 = this.shape;
                fArr2[0] = f3 + shape2.centerX;
                fArr2[1] = ((float) (d6 + (d7 * cos2))) + shape2.centerY;
            }
        }
    }

    public void clear() {
        RenderView renderView = this.renderView;
        if (renderView == null || renderView.getPainting() == null || this.shape == null) {
            return;
        }
        this.renderView.getPainting().clearShape();
        this.allPoints.clear();
        this.movingPoints.clear();
        this.shape = null;
    }

    public void dispatchDraw(Canvas canvas) {
        RenderView renderView = this.renderView;
        if (renderView == null || renderView.getPainting() == null) {
            return;
        }
        Size size = this.renderView.getPainting().getSize();
        for (int i = 0; i < this.allPoints.size(); i++) {
            Point point = (Point) this.allPoints.get(i);
            if (point.draw && !point.rotate) {
                drawPoint(canvas, size, point);
            }
        }
        Shape shape = this.shape;
        if (shape != null && shape.rotation != 0.0f) {
            canvas.save();
            Shape shape2 = this.shape;
            double d = -shape2.rotation;
            Double.isNaN(d);
            canvas.rotate((float) ((d / 3.141592653589793d) * 180.0d), (shape2.centerX / size.width) * canvas.getWidth(), (this.shape.centerY / size.height) * canvas.getHeight());
        }
        Shape shape3 = this.shape;
        if (shape3 != null && shape3.getType() == 4) {
            canvas.drawLine((this.shape.centerX / size.width) * canvas.getWidth(), (this.shape.centerY / size.height) * canvas.getHeight(), (this.shape.middleX / size.width) * canvas.getWidth(), (this.shape.middleY / size.height) * canvas.getHeight(), this.linePaint);
            canvas.drawLine((this.shape.radiusX / size.width) * canvas.getWidth(), (this.shape.radiusY / size.height) * canvas.getHeight(), (this.shape.middleX / size.width) * canvas.getWidth(), (this.shape.middleY / size.height) * canvas.getHeight(), this.linePaint);
        }
        for (int i2 = 0; i2 < this.allPoints.size(); i2++) {
            Point point2 = (Point) this.allPoints.get(i2);
            if (point2.draw && point2.rotate) {
                drawPoint(canvas, size, point2);
            }
        }
        Shape shape4 = this.shape;
        if (shape4 == null || shape4.rotation == 0.0f) {
            return;
        }
        canvas.restore();
    }

    public void onColorChange() {
        if (this.shape != null) {
            this.renderView.getPainting().paintShape(this.shape, null);
        }
    }

    public void onWeightChange() {
        Shape shape = this.shape;
        if (shape == null || shape.thickness == this.renderView.getCurrentWeight()) {
            return;
        }
        this.shape.thickness = this.renderView.getCurrentWeight();
        this.renderView.getPainting().paintShape(this.shape, null);
    }

    public void process(MotionEvent motionEvent, float f) {
        float f2;
        float f3;
        RenderView renderView = this.renderView;
        if (renderView == null || renderView.getPainting() == null || this.shape == null) {
            return;
        }
        int actionMasked = motionEvent.getActionMasked();
        float x = motionEvent.getX();
        float height = this.renderView.getHeight() - motionEvent.getY();
        float[] fArr = this.tempPoint;
        fArr[0] = x;
        fArr[1] = height;
        this.invertMatrix.mapPoints(fArr);
        float[] fArr2 = this.tempPoint;
        float f4 = fArr2[0];
        float f5 = fArr2[1];
        this.invalidate.run();
        Point point = null;
        if (actionMasked != 0) {
            if (actionMasked != 2) {
                if (actionMasked == 1 || actionMasked == 3) {
                    this.movingPoint = null;
                    return;
                }
                return;
            }
            Point point2 = this.movingPoint;
            if (point2 == null) {
                Point point3 = this.center;
                if (point3 != null) {
                    if (point3.rotate) {
                        rotate(f4, f5, false);
                    }
                    float[] fArr3 = this.tempPoint;
                    float f6 = fArr3[0] + this.touchOffsetX;
                    Point point4 = this.center;
                    float f7 = f6 - point4.x;
                    float f8 = (fArr3[1] + this.touchOffsetY) - point4.y;
                    for (int i = 0; i < this.movingPoints.size(); i++) {
                        Point point5 = (Point) this.movingPoints.get(i);
                        point5.update(point5.x + f7, point5.y + f8);
                    }
                }
            } else {
                if (point2.rotate) {
                    rotate(false);
                }
                Point point6 = this.movingPoint;
                float[] fArr4 = this.tempPoint;
                point6.update(fArr4[0] + this.touchOffsetX, fArr4[1] + this.touchOffsetY);
            }
            this.renderView.getPainting().paintShape(this.shape, null);
            this.invalidate.run();
            return;
        }
        double d = Double.MAX_VALUE;
        for (int i2 = 0; i2 < this.allPoints.size(); i2++) {
            Point point7 = (Point) this.allPoints.get(i2);
            if (point7.draw) {
                float[] fArr5 = this.tempPoint;
                fArr5[0] = f4;
                fArr5[1] = f5;
                if (point7.rotate) {
                    rotate(f4, f5, false);
                }
                float f9 = point7.x;
                float f10 = point7.y;
                float[] fArr6 = this.tempPoint;
                double distance = MathUtils.distance(f9, f10, fArr6[0], fArr6[1]);
                if (distance < AndroidUtilities.dp(40.0f) && (point == null || distance < d)) {
                    point = point7;
                    d = distance;
                }
            }
        }
        float[] fArr7 = this.tempPoint;
        fArr7[0] = f4;
        fArr7[1] = f5;
        rotate(f4, f5, false);
        if (point == null && !isInsideShape(f4, f5)) {
            stop();
            return;
        }
        float[] fArr8 = this.tempPoint;
        fArr8[0] = f4;
        fArr8[1] = f5;
        this.movingPoint = point;
        if (point != null) {
            if (point.rotate) {
                rotate(f4, f5, false);
            }
            Point point8 = this.movingPoint;
            float f11 = point8.x;
            float[] fArr9 = this.tempPoint;
            this.touchOffsetX = f11 - fArr9[0];
            f2 = point8.y;
            f3 = fArr9[1];
        } else {
            Point point9 = this.center;
            if (point9 == null) {
                return;
            }
            if (point9.rotate) {
                rotate(f4, f5, false);
            }
            Point point10 = this.center;
            float f12 = point10.x;
            float[] fArr10 = this.tempPoint;
            this.touchOffsetX = f12 - fArr10[0];
            f2 = point10.y;
            f3 = fArr10[1];
        }
        this.touchOffsetY = f2 - f3;
    }

    public void setMatrix(Matrix matrix) {
        Matrix matrix2 = new Matrix();
        this.invertMatrix = matrix2;
        matrix.invert(matrix2);
    }

    public void start(int i) {
        RenderView renderView = this.renderView;
        if (renderView == null || renderView.getPainting() == null) {
            return;
        }
        this.allPoints.clear();
        this.movingPoints.clear();
        this.shape = new Shape(Brush.Shape.make(i));
        Size size = this.renderView.getPainting().getSize();
        Shape shape = this.shape;
        float f = size.width;
        shape.centerX = f / 2.0f;
        float f2 = size.height;
        shape.centerY = f2 / 2.0f;
        float min = Math.min(f, f2) / 5.0f;
        shape.radiusY = min;
        shape.radiusX = min;
        this.shape.thickness = this.renderView.getCurrentWeight();
        this.shape.rounding = AndroidUtilities.dp(32.0f);
        this.shape.fill = PersistColorPalette.getInstance(UserConfig.selectedAccount).getFillShapes();
        if (this.shape.getType() == 4) {
            Shape shape2 = this.shape;
            float f3 = size.width / 2.0f;
            shape2.radiusX = f3;
            shape2.centerX = f3;
            shape2.middleX = f3 + 1.0f;
            float f4 = size.height;
            float f5 = f4 / 3.0f;
            float f6 = 1.0f * f5;
            shape2.centerY = f6;
            float f7 = f4 / 2.0f;
            shape2.middleY = f7;
            shape2.radiusY = f5 * 2.0f;
            shape2.arrowTriangleLength = Math.abs(f6 - f7);
            ArrayList arrayList = this.allPoints;
            final Point point = new Point() {
                @Override
                void set() {
                    double atan2 = Math.atan2(ShapeInput.this.shape.centerY - ShapeInput.this.shape.middleY, ShapeInput.this.shape.centerX - ShapeInput.this.shape.middleX) + 3.141592653589793d;
                    double d = ShapeInput.this.shape.arrowTriangleLength / 5.5f;
                    float f8 = ShapeInput.this.shape.centerX;
                    double cos = Math.cos(atan2);
                    Double.isNaN(d);
                    float f9 = f8 + ((float) (cos * d));
                    float f10 = ShapeInput.this.shape.centerY;
                    double sin = Math.sin(atan2);
                    Double.isNaN(d);
                    set(f9, f10 + ((float) (sin * d)));
                }

                @Override
                protected void update(float f8, float f9) {
                    double atan2 = Math.atan2(ShapeInput.this.shape.centerY - ShapeInput.this.shape.middleY, ShapeInput.this.shape.centerX - ShapeInput.this.shape.middleX) + 1.5707963267948966d;
                    float distance = (MathUtils.distance(ShapeInput.this.shape.centerX, ShapeInput.this.shape.centerY, ShapeInput.this.shape.middleX, ShapeInput.this.shape.middleY) * 5.5f) / 2.0f;
                    Shape shape3 = ShapeInput.this.shape;
                    ShapeInput shapeInput = ShapeInput.this;
                    shape3.arrowTriangleLength = Math.min(distance, Math.max(100.0f, (-shapeInput.distToLine(f8, f9, shapeInput.shape.centerX, ShapeInput.this.shape.centerY, atan2)) * 5.5f));
                    set();
                }
            };
            arrayList.add(point);
            ArrayList arrayList2 = this.allPoints;
            Point point2 = new Point() {
                {
                    super();
                }

                @Override
                void set() {
                    set(ShapeInput.this.shape.middleX, ShapeInput.this.shape.middleY);
                }

                @Override
                protected void update(float f8, float f9) {
                    ShapeInput.this.shape.middleX = f8;
                    ShapeInput.this.shape.middleY = f9;
                    super.update(f8, f9);
                    point.set();
                }
            };
            arrayList2.add(point2);
            this.movingPoints.add(point2);
            ArrayList arrayList3 = this.allPoints;
            Point point3 = new Point() {
                {
                    super();
                }

                @Override
                void set() {
                    set(ShapeInput.this.shape.radiusX, ShapeInput.this.shape.radiusY);
                }

                @Override
                protected void update(float f8, float f9) {
                    ShapeInput.this.shape.radiusX = f8;
                    ShapeInput.this.shape.radiusY = f9;
                    super.update(f8, f9);
                    point.set();
                }
            };
            arrayList3.add(point3);
            this.movingPoints.add(point3);
        }
        if (this.shape.getType() == 0) {
            this.allPoints.add(new Point() {
                @Override
                void set() {
                    set(ShapeInput.this.shape.centerX + ShapeInput.this.shape.radiusX, ShapeInput.this.shape.centerY);
                }

                @Override
                protected void update(float f8, float f9) {
                    super.update(f8, f9);
                    Shape shape3 = ShapeInput.this.shape;
                    Shape shape4 = ShapeInput.this.shape;
                    float distance = MathUtils.distance(ShapeInput.this.shape.centerX, ShapeInput.this.shape.centerY, f8, f9);
                    shape4.radiusY = distance;
                    shape3.radiusX = distance;
                }
            });
        }
        if (this.shape.getType() == 2) {
            this.allPoints.add(new Point() {
                final int n = 5;

                @Override
                void set() {
                    float min2 = Math.min(ShapeInput.this.shape.radiusX, ShapeInput.this.shape.radiusY);
                    set(ShapeInput.this.shape.centerX + (((float) Math.cos(-0.3141592653589793d)) * min2), ShapeInput.this.shape.centerY + (((float) Math.sin(-0.3141592653589793d)) * min2));
                }

                @Override
                protected void update(float f8, float f9) {
                    Shape shape3 = ShapeInput.this.shape;
                    Shape shape4 = ShapeInput.this.shape;
                    float distance = MathUtils.distance(ShapeInput.this.shape.centerX, ShapeInput.this.shape.centerY, f8, f9);
                    shape4.radiusY = distance;
                    shape3.radiusX = distance;
                    Shape shape5 = ShapeInput.this.shape;
                    double d = shape5.rotation;
                    double atan2 = (float) Math.atan2(ShapeInput.this.shape.centerY - f9, f8 - ShapeInput.this.shape.centerX);
                    Double.isNaN(atan2);
                    Double.isNaN(d);
                    shape5.rotation = (float) (d + (atan2 - 0.3141592653589793d));
                    set();
                }
            });
        }
        boolean z = true;
        if (this.shape.getType() == 1 || this.shape.getType() == 3) {
            this.allPoints.add(new CornerPoint(this.shape, false, false));
            this.allPoints.add(new CornerPoint(this.shape, true, false));
            this.allPoints.add(new CornerPoint(this.shape, false, true));
            this.allPoints.add(new CornerPoint(this.shape, true, true));
            this.allPoints.add(new Point(z) {
                @Override
                void set() {
                    set(ShapeInput.this.shape.centerX, ShapeInput.this.shape.centerY - Math.abs(ShapeInput.this.shape.radiusY));
                }

                @Override
                protected void update(float f8, float f9) {
                    Shape shape3 = ShapeInput.this.shape;
                    double d = shape3.rotation;
                    double atan2 = (float) Math.atan2(ShapeInput.this.shape.centerY - f9, f8 - ShapeInput.this.shape.centerX);
                    Double.isNaN(atan2);
                    Double.isNaN(d);
                    shape3.rotation = (float) (d + (atan2 - 1.5707963267948966d));
                    for (int i2 = 0; i2 < ShapeInput.this.allPoints.size(); i2++) {
                        Point point4 = (Point) ShapeInput.this.allPoints.get(i2);
                        if (point4 instanceof CornerPoint) {
                            point4.set();
                        }
                    }
                }
            });
        }
        if (this.shape.getType() == 3) {
            Shape shape3 = this.shape;
            shape3.middleX = shape3.centerX + (shape3.radiusX * 0.8f);
            shape3.middleY = shape3.centerY + (shape3.radiusY * 1.2f) + shape3.thickness;
            ArrayList arrayList4 = this.allPoints;
            Point point4 = new Point() {
                private void limit() {
                    float f8;
                    float f9;
                    if (this.y > ShapeInput.this.shape.centerY - ShapeInput.this.shape.radiusY && this.y < ShapeInput.this.shape.centerY + ShapeInput.this.shape.radiusY) {
                        if (this.x <= ShapeInput.this.shape.centerX && this.x > ShapeInput.this.shape.centerX - ShapeInput.this.shape.radiusX) {
                            f9 = ShapeInput.this.shape.centerX - ShapeInput.this.shape.radiusX;
                        } else if (this.x > ShapeInput.this.shape.centerY && this.x < ShapeInput.this.shape.centerX + ShapeInput.this.shape.radiusX) {
                            f9 = ShapeInput.this.shape.centerX + ShapeInput.this.shape.radiusX;
                        }
                        this.x = f9;
                    }
                    if (this.x <= ShapeInput.this.shape.centerX - ShapeInput.this.shape.radiusX || this.x >= ShapeInput.this.shape.centerX + ShapeInput.this.shape.radiusX) {
                        return;
                    }
                    if (this.y <= ShapeInput.this.shape.centerY && this.y > ShapeInput.this.shape.centerY - ShapeInput.this.shape.radiusY) {
                        f8 = ShapeInput.this.shape.centerY - ShapeInput.this.shape.radiusY;
                    } else if (this.y <= ShapeInput.this.shape.centerY || this.y >= ShapeInput.this.shape.centerY + ShapeInput.this.shape.radiusY) {
                        return;
                    } else {
                        f8 = ShapeInput.this.shape.centerY + ShapeInput.this.shape.radiusY;
                    }
                    this.y = f8;
                }

                @Override
                void set() {
                    set(ShapeInput.this.shape.middleX, ShapeInput.this.shape.middleY);
                }

                @Override
                void set(float f8, float f9) {
                    ShapeInput.this.shape.middleX = f8;
                    ShapeInput.this.shape.middleY = f9;
                    super.set(f8, f9);
                }

                @Override
                protected void update(float f8, float f9) {
                    set(f8, f9);
                    limit();
                    ShapeInput.this.shape.middleX = this.x;
                    ShapeInput.this.shape.middleY = this.y;
                }
            };
            arrayList4.add(point4);
            point4.rotate = false;
            this.movingPoints.add(point4);
        }
        this.center = new Point(z) {
            @Override
            void set() {
                this.x = ShapeInput.this.shape.centerX;
                this.y = ShapeInput.this.shape.centerY;
            }

            @Override
            protected void update(float f8, float f9) {
                for (int i2 = 0; i2 < ShapeInput.this.allPoints.size(); i2++) {
                    Point point5 = (Point) ShapeInput.this.allPoints.get(i2);
                    if (point5 != this) {
                        point5.set();
                    }
                }
                ShapeInput.this.shape.centerX = f8;
                ShapeInput.this.shape.centerY = f9;
                super.update(f8, f9);
            }
        };
        if (this.shape.getType() != 4) {
            this.center.draw = false;
        }
        Point point5 = this.center;
        point5.rotate = false;
        this.movingPoints.add(point5);
        this.allPoints.add(this.center);
        this.renderView.getPainting().paintShape(this.shape, null);
    }

    public void stop() {
        Shape shape;
        RenderView renderView = this.renderView;
        if (renderView == null || renderView.getPainting() == null || (shape = this.shape) == null) {
            return;
        }
        shape.thickness = this.renderView.getCurrentWeight();
        this.renderView.getPainting().commitShape(this.shape, this.renderView.getCurrentColor());
        this.allPoints.clear();
        this.movingPoints.clear();
        this.shape = null;
        this.renderView.resetBrush();
    }
}
