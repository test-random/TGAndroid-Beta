package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.Paint.Brush;

public class ShapeDetector {
    private static final double diagonal;
    private static final double halfDiagonal;
    private static DispatchQueue queue = new DispatchQueue("ShapeDetector");
    Context context;
    private boolean isLearning;
    private Utilities.Callback onShapeDetected;
    SharedPreferences preferences;
    private boolean shapeDetected;
    private int templatesUsageScore;
    private final int MIN_POINTS = 8;
    private final long TIMEOUT = 150;
    private ArrayList points = new ArrayList();
    private ArrayList templates = new ArrayList();
    private ArrayList toSave = null;
    private AtomicBoolean busy = new AtomicBoolean(false);
    private AtomicBoolean scheduled = new AtomicBoolean(false);
    private Runnable detect = new Runnable() {
        @Override
        public final void run() {
            ShapeDetector.this.lambda$new$2();
        }
    };

    public static class Point {
        public double x;
        public double y;

        public Point(double d, double d2) {
            set(d, d2);
        }

        public double distance(double d, double d2) {
            return Math.sqrt(Math.pow(d - this.x, 2.0d) + Math.pow(d2 - this.y, 2.0d));
        }

        public double distance(Point point) {
            return distance(point.x, point.y);
        }

        public void set(double d, double d2) {
            this.x = d;
            this.y = d2;
        }
    }

    public static class RectD {
        public double bottom;
        public double left;
        public double right;
        public double top;

        public RectD(double d, double d2, double d3, double d4) {
            this.left = d;
            this.top = d2;
            this.right = d3;
            this.bottom = d4;
        }

        public String toString() {
            return "RectD{left=" + this.left + ", top=" + this.top + ", right=" + this.right + ", bottom=" + this.bottom + '}';
        }

        public void union(double d, double d2) {
            if (this.left >= d) {
                this.left = d;
            }
            if (this.top >= d2) {
                this.top = d2;
            }
            if (this.right <= d) {
                this.right = d;
            }
            if (this.bottom <= d2) {
                this.bottom = d2;
            }
        }
    }

    public static class Template {
        public ArrayList points;
        public int score;
        public int shapeType;

        private Template() {
            this.points = new ArrayList();
        }
    }

    static {
        double sqrt = Math.sqrt(125000.0d);
        diagonal = sqrt;
        halfDiagonal = sqrt / 2.0d;
    }

    public ShapeDetector(Context context, Utilities.Callback callback) {
        this.context = context;
        this.onShapeDetected = callback;
        SharedPreferences sharedPreferences = context.getSharedPreferences("shapedetector_conf", 0);
        this.preferences = sharedPreferences;
        this.isLearning = sharedPreferences.getBoolean("learning", false);
        this.templatesUsageScore = this.preferences.getInt("scoreall", 0);
        parseTemplates();
    }

    private RectD boundingBox(ArrayList arrayList) {
        if (arrayList.size() <= 0) {
            return null;
        }
        double d = ((Point) arrayList.get(0)).x;
        double d2 = ((Point) arrayList.get(0)).y;
        RectD rectD = new RectD(d, d2, d, d2);
        for (int i = 1; i < arrayList.size(); i++) {
            Point point = (Point) arrayList.get(i);
            rectD.union(point.x, point.y);
        }
        return rectD;
    }

    private Point centroid(ArrayList arrayList) {
        Point point = new Point(0.0d, 0.0d);
        for (int i = 0; i < arrayList.size(); i++) {
            Point point2 = (Point) arrayList.get(i);
            point.x += point2.x;
            point.y += point2.y;
        }
        double d = point.x;
        double size = arrayList.size();
        Double.isNaN(size);
        point.x = d / size;
        double d2 = point.y;
        double size2 = arrayList.size();
        Double.isNaN(size2);
        point.y = d2 / size2;
        return point;
    }

    private Shape constructShape(int i, ArrayList arrayList) {
        int findAnglePoint;
        if (i < 0 || i >= Brush.Shape.SHAPES_LIST.size() || arrayList.size() < 1) {
            return null;
        }
        Shape shape = new Shape(Brush.Shape.make(i));
        if (i == 4) {
            int findAnglePoint2 = findAnglePoint(arrayList);
            if (findAnglePoint2 <= 0) {
                return null;
            }
            if (findAnglePoint2 > 10) {
                findAnglePoint2 -= 2;
            }
            Point point = (Point) arrayList.get(findAnglePoint2);
            Point point2 = (Point) arrayList.get(findAnglePoint2 / 2);
            Point point3 = (Point) arrayList.get(0);
            shape.centerX = (float) point.x;
            shape.centerY = (float) point.y;
            shape.middleX = (float) point2.x;
            shape.middleY = (float) point2.y;
            shape.radiusX = (float) point3.x;
            shape.radiusY = (float) point3.y;
            shape.arrowTriangleLength = 16.0f;
        } else {
            Point centroid = centroid(arrayList);
            shape.centerX = (float) centroid.x;
            shape.centerY = (float) centroid.y;
            RectD boundingBox = boundingBox(arrayList);
            shape.radiusX = ((float) (boundingBox.right - boundingBox.left)) / 2.0f;
            shape.radiusY = ((float) (boundingBox.bottom - boundingBox.top)) / 2.0f;
            if (i == 2 && (findAnglePoint = findAnglePoint(arrayList, 1)) > 0) {
                Point point4 = (Point) arrayList.get(findAnglePoint);
                double d = point4.y;
                double d2 = shape.centerY;
                Double.isNaN(d2);
                double d3 = d - d2;
                double d4 = point4.x;
                double d5 = shape.centerX;
                Double.isNaN(d5);
                shape.rotation = (float) Math.atan2(d3, d4 - d5);
            }
        }
        return shape;
    }

    private double distanceAtAngle(ArrayList arrayList, Point point, ArrayList arrayList2, double d) {
        double cos = Math.cos(d);
        double sin = Math.sin(d);
        int min = Math.min(arrayList.size(), arrayList2.size());
        double d2 = 0.0d;
        int i = 0;
        while (i < min) {
            Point point2 = (Point) arrayList.get(i);
            Point point3 = (Point) arrayList2.get(i);
            double d3 = point2.x;
            int i2 = i;
            double d4 = point.x;
            double d5 = d3 - d4;
            double d6 = point2.y;
            int i3 = min;
            double d7 = point.y;
            double d8 = d6 - d7;
            d2 += point3.distance(((d5 * cos) - (d8 * sin)) + d4, (d5 * sin) + (d8 * cos) + d7);
            i = i2 + 1;
            min = i3;
        }
        double size = arrayList.size();
        Double.isNaN(size);
        return d2 / size;
    }

    private double distanceAtBestAngle(ArrayList arrayList, Point point, ArrayList arrayList2, double d, double d2, double d3) {
        double sqrt = (Math.sqrt(5.0d) - 1.0d) * 0.5d;
        double d4 = 1.0d - sqrt;
        double d5 = (sqrt * d) + (d4 * d2);
        double d6 = (d4 * d) + (sqrt * d2);
        double distanceAtAngle = distanceAtAngle(arrayList, point, arrayList2, d5);
        double distanceAtAngle2 = distanceAtAngle(arrayList, point, arrayList2, d6);
        double d7 = d5;
        double d8 = d6;
        double d9 = d;
        double d10 = d2;
        while (Math.abs(d10 - d9) > d3) {
            if (distanceAtAngle < distanceAtAngle2) {
                double d11 = (sqrt * d9) + (d4 * d8);
                double d12 = distanceAtAngle;
                distanceAtAngle = distanceAtAngle(arrayList, point, arrayList2, d11);
                distanceAtAngle2 = d12;
                d10 = d8;
                d8 = d7;
                d7 = d11;
            } else {
                double d13 = (d4 * d7) + (sqrt * d10);
                distanceAtAngle = distanceAtAngle2;
                distanceAtAngle2 = distanceAtAngle(arrayList, point, arrayList2, d13);
                double d14 = d7;
                d7 = d8;
                d8 = d13;
                d9 = d14;
            }
        }
        return Math.min(distanceAtAngle, distanceAtAngle2);
    }

    private int findAnglePoint(ArrayList arrayList) {
        return findAnglePoint(arrayList, 0);
    }

    private int findAnglePoint(ArrayList arrayList, int i) {
        int max = Math.max(1, arrayList.size() / 4);
        int i2 = i;
        while (max < arrayList.size() - 1) {
            Point point = (Point) arrayList.get(max - 1);
            Point point2 = (Point) arrayList.get(max);
            int i3 = max + 1;
            Point point3 = (Point) arrayList.get(i3);
            double distance = point.distance(point2);
            double distance2 = point.distance(point3);
            double distance3 = point2.distance(point3);
            if ((Math.acos((((distance * distance) + (distance2 * distance2)) - (distance3 * distance3)) / ((distance * 2.0d) * distance2)) / 3.141592653589793d) * 180.0d > 18.0d) {
                if (i2 <= 0) {
                    return max;
                }
                i2--;
            }
            max = i3;
        }
        return -1;
    }

    private ArrayList fullClone(ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            Point point = (Point) arrayList.get(i);
            arrayList2.add(new Point(point.x, point.y));
        }
        return arrayList2;
    }

    private double indicativeAngle(ArrayList arrayList) {
        Point centroid = centroid(arrayList);
        return Math.atan2(centroid.y - ((Point) arrayList.get(0)).y, centroid.x - ((Point) arrayList.get(0)).x);
    }

    public static boolean isLearning(Context context) {
        return context.getSharedPreferences("shapedetector_conf", 0).getBoolean("learning", false);
    }

    public void lambda$new$1(Shape shape, int i, ArrayList arrayList) {
        boolean z = shape != null;
        this.shapeDetected = z;
        if (!z || i < 0 || i >= this.templates.size()) {
            this.toSave = arrayList;
        } else {
            this.templatesUsageScore++;
            ((Template) this.templates.get(i)).score++;
            this.preferences.edit().putInt("score" + i, ((Template) this.templates.get(i)).score).putInt("scoreall", this.templatesUsageScore).apply();
            this.toSave = null;
        }
        this.onShapeDetected.run(shape);
    }

    public void lambda$new$2() {
        String str;
        if (this.busy.get()) {
            return;
        }
        this.scheduled.set(false);
        this.busy.set(true);
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this) {
            try {
                if (this.points.size() < 8) {
                    this.busy.set(false);
                    return;
                }
                ArrayList resample = resample(fullClone(this.points), 48);
                ArrayList fullClone = fullClone(resample);
                rotate(fullClone, indicativeAngle(fullClone));
                Point centroid = centroid(fullClone);
                translate(fullClone, -centroid.x, -centroid.y);
                scale(fullClone, 250.0d);
                Point centroid2 = centroid(fullClone);
                double d = Double.MAX_VALUE;
                int i = 0;
                int i2 = -1;
                int i3 = -1;
                while (i < this.templates.size()) {
                    ArrayList arrayList = fullClone;
                    int i4 = i;
                    ArrayList arrayList2 = fullClone;
                    int i5 = i2;
                    double distanceAtBestAngle = distanceAtBestAngle(arrayList, centroid2, ((Template) this.templates.get(i)).points, -1.5707963267948966d, 1.5707963267948966d, 0.06981317007977318d);
                    if (distanceAtBestAngle < d) {
                        d = distanceAtBestAngle;
                        i3 = ((Template) this.templates.get(i4)).shapeType;
                        i2 = i4;
                    } else {
                        i2 = i5;
                    }
                    i = i4 + 1;
                    fullClone = arrayList2;
                }
                final ArrayList arrayList3 = fullClone;
                final int i6 = i2;
                int i7 = 1.0d - (d / halfDiagonal) < 0.8d ? -1 : i3;
                final Shape constructShape = constructShape(i7, resample);
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("took ");
                    sb.append(System.currentTimeMillis() - currentTimeMillis);
                    sb.append("ms to ");
                    sb.append(constructShape != null ? "" : "not ");
                    sb.append("detect a shape");
                    if (constructShape != null) {
                        str = " (template#" + i6 + " shape#" + i7 + ")";
                    } else {
                        str = "";
                    }
                    sb.append(str);
                    Log.i("shapedetector", sb.toString());
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ShapeDetector.this.lambda$new$1(constructShape, i6, arrayList3);
                    }
                });
                this.busy.set(false);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void lambda$parseTemplates$0() {
        AnonymousClass1 anonymousClass1;
        char c;
        String string;
        try {
            InputStream open = ApplicationLoader.applicationContext.getAssets().open("shapes.dat");
            while (true) {
                anonymousClass1 = null;
                c = 0;
                if (open.available() <= 5) {
                    break;
                }
                Template template = new Template();
                template.shapeType = open.read();
                int read = open.read();
                int read2 = open.read() - 64;
                int read3 = open.read() - 64;
                if (open.available() < read * 2) {
                    break;
                }
                for (int i = 0; i < read; i++) {
                    template.points.add(new Point((open.read() - read2) - 127, (open.read() - read3) - 127));
                }
                template.score = this.preferences.getInt("score" + this.templates.size(), 0);
                this.templates.add(template);
            }
            if (this.isLearning && (string = this.preferences.getString("moretemplates", null)) != null) {
                String[] split = string.split("\\|");
                int size = this.templates.size();
                int i2 = 0;
                while (i2 < split.length) {
                    Template template2 = new Template();
                    String[] split2 = split[i2].split(",");
                    if (split2.length > 1) {
                        template2.shapeType = Integer.parseInt(split2[c]);
                        for (int i3 = 1; i3 < split2.length; i3 += 2) {
                            template2.points.add(new Point(Double.parseDouble(split2[i3]), Double.parseDouble(split2[i3 + 1])));
                        }
                        template2.score = this.preferences.getInt("score" + (size + i2), 0);
                        this.templates.add(template2);
                    }
                    i2++;
                    anonymousClass1 = null;
                    c = 0;
                }
            }
            open.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$showSaveLearnDialog$3(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        if (i != 0) {
            Template template = new Template();
            template.shapeType = i - 1;
            template.points = arrayList;
            this.templates.add(template);
            String string = this.preferences.getString("moretemplates", null);
            String str = string == null ? "" + template.shapeType : string + "|" + template.shapeType;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                str = str + "," + Math.round(((Point) arrayList.get(i2)).x) + "," + Math.round(((Point) arrayList.get(i2)).y);
            }
            this.preferences.edit().putString("moretemplates", str).apply();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i3 = 0; i3 < this.templates.size(); i3++) {
            Template template2 = (Template) this.templates.get(i3);
            if (i3 > 0) {
                sb.append(",\n");
            }
            sb.append("\t{\n\t\t\"shape\": ");
            sb.append(template2.shapeType);
            sb.append(",\n\t\t\"points\": [");
            for (int i4 = 0; i4 < template2.points.size(); i4++) {
                if (i4 > 0) {
                    sb.append(",");
                }
                Point point = (Point) template2.points.get(i4);
                sb.append("[");
                sb.append(Math.round(point.x));
                sb.append(",");
                sb.append(Math.round(point.y));
                sb.append("]");
            }
            sb.append("],\n\t\t\"freq\": ");
            sb.append(Math.round(((template2.score / this.templatesUsageScore) * 100.0f) * 100.0f) / 100.0f);
            sb.append("\n\t}");
        }
        sb.append("\n]");
        Log.i("shapedetector", sb.toString());
    }

    private void parseTemplates() {
        queue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                ShapeDetector.this.lambda$parseTemplates$0();
            }
        });
    }

    private double pathLength(ArrayList arrayList) {
        double d = 0.0d;
        for (int i = 1; i < arrayList.size(); i++) {
            d += ((Point) arrayList.get(i - 1)).distance((Point) arrayList.get(i));
        }
        return d;
    }

    private ArrayList resample(ArrayList arrayList, int i) {
        double d;
        int i2;
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add((Point) arrayList.get(0));
        double pathLength = pathLength(arrayList);
        int i3 = i - 1;
        double d2 = i3;
        Double.isNaN(d2);
        double d3 = pathLength / d2;
        int i4 = 1;
        double d4 = 0.0d;
        while (i4 < arrayList.size()) {
            int i5 = i4 - 1;
            double distance = ((Point) arrayList.get(i5)).distance((Point) arrayList.get(i4));
            double d5 = d4 + distance;
            if (d5 >= d3) {
                double d6 = (d3 - d4) / distance;
                i2 = i3;
                d = d3;
                Point point = new Point(((Point) arrayList.get(i5)).x + ((((Point) arrayList.get(i4)).x - ((Point) arrayList.get(i5)).x) * d6), ((Point) arrayList.get(i5)).y + (d6 * (((Point) arrayList.get(i4)).y - ((Point) arrayList.get(i5)).y)));
                arrayList2.add(point);
                arrayList.add(i4, point);
                d4 = 0.0d;
            } else {
                d = d3;
                i2 = i3;
                d4 = d5;
            }
            i4++;
            i3 = i2;
            d3 = d;
        }
        if (arrayList2.size() == i3) {
            arrayList2.add((Point) arrayList.get(arrayList.size() - 1));
        }
        return arrayList2;
    }

    private void rotate(ArrayList arrayList, double d) {
        rotate(arrayList, d, centroid(arrayList));
    }

    private void rotate(ArrayList arrayList, double d, Point point) {
        double cos = Math.cos(d);
        double sin = Math.sin(d);
        int i = 0;
        while (i < arrayList.size()) {
            Point point2 = (Point) arrayList.get(i);
            double d2 = point2.x;
            double d3 = point.x;
            double d4 = d2 - d3;
            double d5 = point2.y;
            int i2 = i;
            double d6 = point.y;
            double d7 = d5 - d6;
            point2.y = (d4 * sin) + (d7 * cos) + d6;
            point2.x = ((d4 * cos) - (d7 * sin)) + d3;
            i = i2 + 1;
        }
    }

    private void scale(ArrayList arrayList, double d) {
        RectD boundingBox = boundingBox(arrayList);
        double d2 = boundingBox.right - boundingBox.left;
        double d3 = boundingBox.bottom - boundingBox.top;
        for (int i = 0; i < arrayList.size(); i++) {
            Point point = (Point) arrayList.get(i);
            point.x *= d / d2;
            point.y *= d / d3;
        }
    }

    public static void setLearning(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences("shapedetector_conf", 0).edit();
        if (z) {
            edit.putBoolean("learning", true);
        } else {
            edit.clear();
        }
        edit.apply();
    }

    private void showSaveLearnDialog() {
        final ArrayList arrayList = this.toSave;
        new AlertDialog.Builder(this.context).setTitle("Shape?").setItems(new String[]{"Log all", "Circle", "Rectangle", "Star", "Bubble", "Arrow", "None"}, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ShapeDetector.this.lambda$showSaveLearnDialog$3(arrayList, dialogInterface, i);
            }
        }).show();
        this.toSave = null;
    }

    private void translate(ArrayList arrayList, double d, double d2) {
        for (int i = 0; i < arrayList.size(); i++) {
            Point point = (Point) arrayList.get(i);
            point.x += d;
            point.y += d2;
        }
    }

    public void append(double d, double d2, boolean z) {
        boolean z2;
        synchronized (this) {
            this.points.add(new Point(d, d2));
            z2 = this.points.size() >= 8;
        }
        if (z2) {
            scheduleDetect(z);
        }
    }

    public void clear() {
        synchronized (this) {
            this.points.clear();
        }
        queue.cancelRunnable(this.detect);
        this.scheduled.set(false);
        this.shapeDetected = false;
        if (!this.isLearning || this.toSave == null) {
            return;
        }
        showSaveLearnDialog();
    }

    public void scheduleDetect(boolean z) {
        if (this.busy.get()) {
            return;
        }
        if (this.scheduled.get() && !this.shapeDetected && z) {
            queue.cancelRunnable(this.detect);
            queue.postRunnable(this.detect, 150L);
        }
        if (this.scheduled.get()) {
            return;
        }
        this.scheduled.set(true);
        queue.postRunnable(this.detect, 150L);
    }
}
