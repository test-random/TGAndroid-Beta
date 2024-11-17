package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.TextSelectionHelper;

public class TableLayout extends View {
    public static final Alignment BASELINE;
    public static final Alignment BOTTOM;
    public static final Alignment CENTER;
    public static final Alignment END;
    public static final Alignment FILL;
    private static final Alignment LEADING;
    public static final Alignment LEFT;
    public static final Alignment RIGHT;
    public static final Alignment START;
    public static final Alignment TOP;
    private static final Alignment TRAILING;
    static final Alignment UNDEFINED_ALIGNMENT = new Alignment() {
        @Override
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        @Override
        int getGravityOffset(Child child, int i) {
            return Integer.MIN_VALUE;
        }
    };
    private Path backgroundPath;
    private ArrayList cellsToFixHeight;
    private ArrayList childrens;
    private int colCount;
    private TableLayoutDelegate delegate;
    private boolean drawLines;
    private boolean isRtl;
    private boolean isStriped;
    private int itemPaddingLeft;
    private int itemPaddingTop;
    private Path linePath;
    private int mAlignmentMode;
    private int mDefaultGap;
    private final Axis mHorizontalAxis;
    private int mLastLayoutParamsHashCode;
    private int mOrientation;
    private boolean mUseDefaultMargins;
    private final Axis mVerticalAxis;
    private float[] radii;
    private RectF rect;
    private ArrayList rowSpans;
    private TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;

    public static abstract class Alignment {
        Alignment() {
        }

        abstract int getAlignmentValue(Child child, int i);

        Bounds getBounds() {
            return new Bounds();
        }

        abstract int getGravityOffset(Child child, int i);

        int getSizeInCell(Child child, int i, int i2) {
            return i;
        }
    }

    public static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval interval, MutableInt mutableInt) {
            this.span = interval;
            this.value = mutableInt;
        }
    }

    public static final class Assoc extends ArrayList {
        private final Class keyType;
        private final Class valueType;

        private Assoc(Class cls, Class cls2) {
            this.keyType = cls;
            this.valueType = cls2;
        }

        public static Assoc of(Class cls, Class cls2) {
            return new Assoc(cls, cls2);
        }

        public PackedMap pack() {
            int size = size();
            Object[] objArr = (Object[]) Array.newInstance((Class<?>) this.keyType, size);
            Object[] objArr2 = (Object[]) Array.newInstance((Class<?>) this.valueType, size);
            for (int i = 0; i < size; i++) {
                objArr[i] = ((Pair) get(i)).first;
                objArr2[i] = ((Pair) get(i)).second;
            }
            return new PackedMap(objArr, objArr2);
        }

        public void put(Object obj, Object obj2) {
            add(Pair.create(obj, obj2));
        }
    }

    public final class Axis {
        public Arc[] arcs;
        public boolean arcsValid;
        PackedMap backwardLinks;
        public boolean backwardLinksValid;
        public int definedCount;
        public int[] deltas;
        PackedMap forwardLinks;
        public boolean forwardLinksValid;
        PackedMap groupBounds;
        public boolean groupBoundsValid;
        public boolean hasWeights;
        public boolean hasWeightsValid;
        public final boolean horizontal;
        public int[] leadingMargins;
        public boolean leadingMarginsValid;
        public int[] locations;
        public boolean locationsValid;
        private int maxIndex;
        boolean orderPreserved;
        private MutableInt parentMax;
        private MutableInt parentMin;
        public int[] trailingMargins;
        public boolean trailingMarginsValid;

        private Axis(boolean z) {
            this.definedCount = Integer.MIN_VALUE;
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
            this.hasWeightsValid = false;
            this.orderPreserved = true;
            this.parentMin = new MutableInt(0);
            this.parentMax = new MutableInt(-100000);
            this.horizontal = z;
        }

        private void addComponentSizes(List list, PackedMap packedMap) {
            int i = 0;
            while (true) {
                Interval[] intervalArr = (Interval[]) packedMap.keys;
                if (i >= intervalArr.length) {
                    return;
                }
                include(list, intervalArr[i], ((MutableInt[]) packedMap.values)[i], false);
                i++;
            }
        }

        private int calculateMaxIndex() {
            int childCount = TableLayout.this.getChildCount();
            int i = -1;
            for (int i2 = 0; i2 < childCount; i2++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i2).getLayoutParams();
                Interval interval = (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).span;
                i = Math.max(Math.max(Math.max(i, interval.min), interval.max), interval.size());
            }
            if (i == -1) {
                return Integer.MIN_VALUE;
            }
            return i;
        }

        private float calculateTotalWeight() {
            int childCount = TableLayout.this.getChildCount();
            float f = 0.0f;
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                f += (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight;
            }
            return f;
        }

        private void computeArcs() {
            getForwardLinks();
            getBackwardLinks();
        }

        private void computeGroupBounds() {
            for (Bounds bounds : (Bounds[]) this.groupBounds.values) {
                bounds.reset();
            }
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Child childAt = TableLayout.this.getChildAt(i);
                LayoutParams layoutParams = childAt.getLayoutParams();
                boolean z = this.horizontal;
                Spec spec = z ? layoutParams.columnSpec : layoutParams.rowSpec;
                ((Bounds) this.groupBounds.getValue(i)).include(TableLayout.this, childAt, spec, this, TableLayout.this.getMeasurementIncludingMargin(childAt, z) + (spec.weight == 0.0f ? 0 : this.deltas[i]));
            }
        }

        private boolean computeHasWeights() {
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                if ((this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight != 0.0f) {
                    return true;
                }
            }
            return false;
        }

        private void computeLinks(PackedMap packedMap, boolean z) {
            for (MutableInt mutableInt : (MutableInt[]) packedMap.values) {
                mutableInt.reset();
            }
            Bounds[] boundsArr = (Bounds[]) getGroupBounds().values;
            for (int i = 0; i < boundsArr.length; i++) {
                int size = boundsArr[i].size(z);
                MutableInt mutableInt2 = (MutableInt) packedMap.getValue(i);
                int i2 = mutableInt2.value;
                if (!z) {
                    size = -size;
                }
                mutableInt2.value = Math.max(i2, size);
            }
        }

        private void computeLocations(int[] iArr) {
            if (hasWeights()) {
                solveAndDistributeSpace(iArr);
            } else {
                solve(iArr);
            }
            if (this.orderPreserved) {
                return;
            }
            int i = iArr[0];
            int length = iArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                iArr[i2] = iArr[i2] - i;
            }
        }

        private void computeMargins(boolean z) {
            int[] iArr = z ? this.leadingMargins : this.trailingMargins;
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Child childAt = TableLayout.this.getChildAt(i);
                LayoutParams layoutParams = childAt.getLayoutParams();
                boolean z2 = this.horizontal;
                Interval interval = (z2 ? layoutParams.columnSpec : layoutParams.rowSpec).span;
                int i2 = z ? interval.min : interval.max;
                iArr[i2] = Math.max(iArr[i2], TableLayout.this.getMargin1(childAt, z2, z));
            }
        }

        private Arc[] createArcs() {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            addComponentSizes(arrayList, getForwardLinks());
            addComponentSizes(arrayList2, getBackwardLinks());
            if (this.orderPreserved) {
                int i = 0;
                while (i < getCount()) {
                    int i2 = i + 1;
                    include(arrayList, new Interval(i, i2), new MutableInt(0));
                    i = i2;
                }
            }
            int count = getCount();
            include(arrayList, new Interval(0, count), this.parentMin, false);
            include(arrayList2, new Interval(count, 0), this.parentMax, false);
            return (Arc[]) TableLayout.append(topologicalSort(arrayList), topologicalSort(arrayList2));
        }

        private PackedMap createGroupBounds() {
            Assoc of = Assoc.of(Spec.class, Bounds.class);
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                boolean z = this.horizontal;
                Spec spec = z ? layoutParams.columnSpec : layoutParams.rowSpec;
                of.put(spec, spec.getAbsoluteAlignment(z).getBounds());
            }
            return of.pack();
        }

        private PackedMap createLinks(boolean z) {
            Assoc of = Assoc.of(Interval.class, MutableInt.class);
            Spec[] specArr = (Spec[]) getGroupBounds().keys;
            int length = specArr.length;
            for (int i = 0; i < length; i++) {
                of.put(z ? specArr[i].span : specArr[i].span.inverse(), new MutableInt());
            }
            return of.pack();
        }

        private PackedMap getBackwardLinks() {
            if (this.backwardLinks == null) {
                this.backwardLinks = createLinks(false);
            }
            if (!this.backwardLinksValid) {
                computeLinks(this.backwardLinks, false);
                this.backwardLinksValid = true;
            }
            return this.backwardLinks;
        }

        private PackedMap getForwardLinks() {
            if (this.forwardLinks == null) {
                this.forwardLinks = createLinks(true);
            }
            if (!this.forwardLinksValid) {
                computeLinks(this.forwardLinks, true);
                this.forwardLinksValid = true;
            }
            return this.forwardLinks;
        }

        private int getMaxIndex() {
            if (this.maxIndex == Integer.MIN_VALUE) {
                this.maxIndex = Math.max(0, calculateMaxIndex());
            }
            return this.maxIndex;
        }

        private int getMeasure(int i, int i2) {
            setParentConstraints(i, i2);
            return size(getLocations());
        }

        private boolean hasWeights() {
            if (!this.hasWeightsValid) {
                this.hasWeights = computeHasWeights();
                this.hasWeightsValid = true;
            }
            return this.hasWeights;
        }

        private void include(List list, Interval interval, MutableInt mutableInt) {
            include(list, interval, mutableInt, true);
        }

        private void include(List list, Interval interval, MutableInt mutableInt, boolean z) {
            if (interval.size() == 0) {
                return;
            }
            if (z) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    if (((Arc) it.next()).span.equals(interval)) {
                        return;
                    }
                }
            }
            list.add(new Arc(interval, mutableInt));
        }

        private void init(int[] iArr) {
            Arrays.fill(iArr, 0);
        }

        private boolean relax(int[] iArr, Arc arc) {
            if (!arc.valid) {
                return false;
            }
            Interval interval = arc.span;
            int i = interval.min;
            int i2 = interval.max;
            int i3 = iArr[i] + arc.value.value;
            if (i3 <= iArr[i2]) {
                return false;
            }
            iArr[i2] = i3;
            return true;
        }

        private void setParentConstraints(int i, int i2) {
            this.parentMin.value = i;
            this.parentMax.value = -i2;
            this.locationsValid = false;
        }

        private void shareOutDelta(int i, float f) {
            Arrays.fill(this.deltas, 0);
            int childCount = TableLayout.this.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i2).getLayoutParams();
                float f2 = (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight;
                if (f2 != 0.0f) {
                    int round = Math.round((i * f2) / f);
                    this.deltas[i2] = round;
                    i -= round;
                    f -= f2;
                }
            }
        }

        private int size(int[] iArr) {
            return iArr[getCount()];
        }

        private boolean solve(int[] iArr) {
            return solve(getArcs(), iArr);
        }

        private boolean solve(Arc[] arcArr, int[] iArr) {
            return solve(arcArr, iArr, true);
        }

        private boolean solve(Arc[] arcArr, int[] iArr, boolean z) {
            int count = getCount() + 1;
            for (int i = 0; i < arcArr.length; i++) {
                init(iArr);
                for (int i2 = 0; i2 < count; i2++) {
                    boolean z2 = false;
                    for (Arc arc : arcArr) {
                        z2 |= relax(iArr, arc);
                    }
                    if (!z2) {
                        return true;
                    }
                }
                if (!z) {
                    return false;
                }
                boolean[] zArr = new boolean[arcArr.length];
                for (int i3 = 0; i3 < count; i3++) {
                    int length = arcArr.length;
                    for (int i4 = 0; i4 < length; i4++) {
                        zArr[i4] = zArr[i4] | relax(iArr, arcArr[i4]);
                    }
                }
                int i5 = 0;
                while (true) {
                    if (i5 >= arcArr.length) {
                        break;
                    }
                    if (zArr[i5]) {
                        Arc arc2 = arcArr[i5];
                        Interval interval = arc2.span;
                        if (interval.min >= interval.max) {
                            arc2.valid = false;
                            break;
                        }
                    }
                    i5++;
                }
            }
            return true;
        }

        private void solveAndDistributeSpace(int[] iArr) {
            Arrays.fill(getDeltas(), 0);
            solve(iArr);
            boolean z = true;
            int childCount = (this.parentMin.value * TableLayout.this.getChildCount()) + 1;
            if (childCount < 2) {
                return;
            }
            float calculateTotalWeight = calculateTotalWeight();
            int i = -1;
            int i2 = 0;
            while (i2 < childCount) {
                int i3 = (int) ((i2 + childCount) / 2);
                invalidateValues();
                shareOutDelta(i3, calculateTotalWeight);
                boolean solve = solve(getArcs(), iArr, false);
                if (solve) {
                    i2 = i3 + 1;
                    i = i3;
                } else {
                    childCount = i3;
                }
                z = solve;
            }
            if (i <= 0 || z) {
                return;
            }
            invalidateValues();
            shareOutDelta(i, calculateTotalWeight);
            solve(iArr);
        }

        private Arc[] topologicalSort(List list) {
            return topologicalSort((Arc[]) list.toArray(new Arc[0]));
        }

        private Arc[] topologicalSort(Arc[] arcArr) {
            return new Object(arcArr) {
                Arc[][] arcsByVertex;
                int cursor;
                Arc[] result;
                final Arc[] val$arcs;
                int[] visited;

                {
                    this.val$arcs = arcArr;
                    int length = arcArr.length;
                    this.result = new Arc[length];
                    this.cursor = length - 1;
                    this.arcsByVertex = Axis.this.groupArcsByFirstVertex(arcArr);
                    this.visited = new int[Axis.this.getCount() + 1];
                }

                Arc[] sort() {
                    int length = this.arcsByVertex.length;
                    for (int i = 0; i < length; i++) {
                        walk(i);
                    }
                    return this.result;
                }

                void walk(int i) {
                    int[] iArr = this.visited;
                    if (iArr[i] != 0) {
                        return;
                    }
                    iArr[i] = 1;
                    for (Arc arc : this.arcsByVertex[i]) {
                        walk(arc.span.max);
                        Arc[] arcArr2 = this.result;
                        int i2 = this.cursor;
                        this.cursor = i2 - 1;
                        arcArr2[i2] = arc;
                    }
                    this.visited[i] = 2;
                }
            }.sort();
        }

        public Arc[] getArcs() {
            if (this.arcs == null) {
                this.arcs = createArcs();
            }
            if (!this.arcsValid) {
                computeArcs();
                this.arcsValid = true;
            }
            return this.arcs;
        }

        public int getCount() {
            return Math.max(this.definedCount, getMaxIndex());
        }

        public int[] getDeltas() {
            if (this.deltas == null) {
                this.deltas = new int[TableLayout.this.getChildCount()];
            }
            return this.deltas;
        }

        public PackedMap getGroupBounds() {
            if (this.groupBounds == null) {
                this.groupBounds = createGroupBounds();
            }
            if (!this.groupBoundsValid) {
                computeGroupBounds();
                this.groupBoundsValid = true;
            }
            return this.groupBounds;
        }

        public int[] getLeadingMargins() {
            if (this.leadingMargins == null) {
                this.leadingMargins = new int[getCount() + 1];
            }
            if (!this.leadingMarginsValid) {
                computeMargins(true);
                this.leadingMarginsValid = true;
            }
            return this.leadingMargins;
        }

        public int[] getLocations() {
            if (this.locations == null) {
                this.locations = new int[getCount() + 1];
            }
            if (!this.locationsValid) {
                computeLocations(this.locations);
                this.locationsValid = true;
            }
            return this.locations;
        }

        public int getMeasure(int i) {
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            if (mode == Integer.MIN_VALUE) {
                return getMeasure(0, size);
            }
            if (mode == 0) {
                return getMeasure(0, 100000);
            }
            if (mode != 1073741824) {
                return 0;
            }
            return getMeasure(size, size);
        }

        public int[] getTrailingMargins() {
            if (this.trailingMargins == null) {
                this.trailingMargins = new int[getCount() + 1];
            }
            if (!this.trailingMarginsValid) {
                computeMargins(false);
                this.trailingMarginsValid = true;
            }
            return this.trailingMargins;
        }

        Arc[][] groupArcsByFirstVertex(Arc[] arcArr) {
            int count = getCount() + 1;
            Arc[][] arcArr2 = new Arc[count];
            int[] iArr = new int[count];
            for (Arc arc : arcArr) {
                int i = arc.span.min;
                iArr[i] = iArr[i] + 1;
            }
            for (int i2 = 0; i2 < count; i2++) {
                arcArr2[i2] = new Arc[iArr[i2]];
            }
            Arrays.fill(iArr, 0);
            for (Arc arc2 : arcArr) {
                int i3 = arc2.span.min;
                Arc[] arcArr3 = arcArr2[i3];
                int i4 = iArr[i3];
                iArr[i3] = i4 + 1;
                arcArr3[i4] = arc2;
            }
            return arcArr2;
        }

        public void invalidateStructure() {
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBounds = null;
            this.forwardLinks = null;
            this.backwardLinks = null;
            this.leadingMargins = null;
            this.trailingMargins = null;
            this.arcs = null;
            this.locations = null;
            this.deltas = null;
            this.hasWeightsValid = false;
            invalidateValues();
        }

        public void invalidateValues() {
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
        }

        public void layout(int i) {
            setParentConstraints(i, i);
            getLocations();
        }

        public void setCount(int i) {
            if (i != Integer.MIN_VALUE && i < getMaxIndex()) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.horizontal ? "column" : "row");
                sb.append("Count must be greater than or equal to the maximum of all grid indices (and spans) defined in the LayoutParams of each child");
                TableLayout.handleInvalidParams(sb.toString());
            }
            this.definedCount = i;
        }

        public void setOrderPreserved(boolean z) {
            this.orderPreserved = z;
            invalidateStructure();
        }
    }

    public static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        private Bounds() {
            reset();
        }

        protected int getOffset(TableLayout tableLayout, Child child, Alignment alignment, int i, boolean z) {
            return this.before - alignment.getAlignmentValue(child, i);
        }

        protected void include(int i, int i2) {
            this.before = Math.max(this.before, i);
            this.after = Math.max(this.after, i2);
        }

        protected final void include(TableLayout tableLayout, Child child, Spec spec, Axis axis, int i) {
            this.flexibility &= spec.getFlexibility();
            int alignmentValue = spec.getAbsoluteAlignment(axis.horizontal).getAlignmentValue(child, i);
            include(alignmentValue, i - alignmentValue);
        }

        protected void reset() {
            this.before = Integer.MIN_VALUE;
            this.after = Integer.MIN_VALUE;
            this.flexibility = 2;
        }

        protected int size(boolean z) {
            if (z || !TableLayout.canStretch(this.flexibility)) {
                return this.before + this.after;
            }
            return 100000;
        }
    }

    public class Child {
        private TLRPC.TL_pageTableCell cell;
        private int fixedHeight;
        private int index;
        private LayoutParams layoutParams;
        private int measuredHeight;
        private int measuredWidth;
        public int rowspan;
        private int selectionIndex = -1;
        public int textHeight;
        public ArticleViewer.DrawingText textLayout;
        public int textLeft;
        public int textWidth;
        public int textX;
        public int textY;
        public int x;
        public int y;

        public Child(int i) {
            this.index = i;
        }

        static int access$1520(Child child, int i) {
            int i2 = child.measuredHeight - i;
            child.measuredHeight = i2;
            return i2;
        }

        public void draw(Canvas canvas, View view) {
            float f;
            float f2;
            float f3;
            float f4;
            Paint stripPaint;
            Path path;
            Paint stripPaint2;
            float f5;
            float f6;
            float f7;
            Canvas canvas2;
            float f8;
            Paint paint;
            float f9;
            float f10;
            Canvas canvas3;
            float f11;
            float f12;
            Paint paint2;
            int i;
            if (this.cell == null) {
                return;
            }
            boolean z = false;
            boolean z2 = true;
            boolean z3 = this.x + this.measuredWidth == TableLayout.this.getMeasuredWidth();
            boolean z4 = this.y + this.measuredHeight == TableLayout.this.getMeasuredHeight();
            int dp = AndroidUtilities.dp(3.0f);
            if (this.cell.header || (TableLayout.this.isStriped && this.layoutParams.rowSpec.span.min % 2 == 0)) {
                if (this.x == 0 && this.y == 0) {
                    float[] fArr = TableLayout.this.radii;
                    float f13 = dp;
                    TableLayout.this.radii[1] = f13;
                    fArr[0] = f13;
                    z = true;
                } else {
                    float[] fArr2 = TableLayout.this.radii;
                    TableLayout.this.radii[1] = 0.0f;
                    fArr2[0] = 0.0f;
                }
                if (z3 && this.y == 0) {
                    float[] fArr3 = TableLayout.this.radii;
                    float f14 = dp;
                    TableLayout.this.radii[3] = f14;
                    fArr3[2] = f14;
                    z = true;
                } else {
                    float[] fArr4 = TableLayout.this.radii;
                    TableLayout.this.radii[3] = 0.0f;
                    fArr4[2] = 0.0f;
                }
                if (z3 && z4) {
                    float[] fArr5 = TableLayout.this.radii;
                    float f15 = dp;
                    TableLayout.this.radii[5] = f15;
                    fArr5[4] = f15;
                    z = true;
                } else {
                    float[] fArr6 = TableLayout.this.radii;
                    TableLayout.this.radii[5] = 0.0f;
                    fArr6[4] = 0.0f;
                }
                if (this.x == 0 && z4) {
                    float[] fArr7 = TableLayout.this.radii;
                    float f16 = dp;
                    TableLayout.this.radii[7] = f16;
                    fArr7[6] = f16;
                } else {
                    float[] fArr8 = TableLayout.this.radii;
                    TableLayout.this.radii[7] = 0.0f;
                    fArr8[6] = 0.0f;
                    z2 = z;
                }
                if (z2) {
                    TableLayout.this.rect.set(this.x, this.y, r3 + this.measuredWidth, r7 + this.measuredHeight);
                    TableLayout.this.backgroundPath.reset();
                    TableLayout.this.backgroundPath.addRoundRect(TableLayout.this.rect, TableLayout.this.radii, Path.Direction.CW);
                    if (this.cell.header) {
                        path = TableLayout.this.backgroundPath;
                        stripPaint2 = TableLayout.this.delegate.getHeaderPaint();
                    } else {
                        path = TableLayout.this.backgroundPath;
                        stripPaint2 = TableLayout.this.delegate.getStripPaint();
                    }
                    canvas.drawPath(path, stripPaint2);
                } else {
                    if (this.cell.header) {
                        int i2 = this.x;
                        f = i2;
                        int i3 = this.y;
                        f2 = i3;
                        f3 = i2 + this.measuredWidth;
                        f4 = i3 + this.measuredHeight;
                        stripPaint = TableLayout.this.delegate.getHeaderPaint();
                    } else {
                        int i4 = this.x;
                        f = i4;
                        int i5 = this.y;
                        f2 = i5;
                        f3 = i4 + this.measuredWidth;
                        f4 = i5 + this.measuredHeight;
                        stripPaint = TableLayout.this.delegate.getStripPaint();
                    }
                    canvas.drawRect(f, f2, f3, f4, stripPaint);
                }
            }
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(getTextX(), getTextY());
                if (this.selectionIndex >= 0) {
                    TableLayout.this.textSelectionHelper.draw(canvas, (TextSelectionHelper.ArticleSelectableView) TableLayout.this.getParent().getParent(), this.selectionIndex);
                }
                this.textLayout.draw(canvas, view);
                canvas.restore();
            }
            if (TableLayout.this.drawLines) {
                Paint linePaint = TableLayout.this.delegate.getLinePaint();
                Paint linePaint2 = TableLayout.this.delegate.getLinePaint();
                float strokeWidth = linePaint.getStrokeWidth() / 2.0f;
                float strokeWidth2 = linePaint2.getStrokeWidth() / 2.0f;
                int i6 = this.x;
                if (i6 == 0) {
                    int i7 = this.y;
                    float f17 = i7;
                    float f18 = this.measuredHeight + i7;
                    if (i7 == 0) {
                        f17 += dp;
                    }
                    float f19 = f17;
                    if (f18 == TableLayout.this.getMeasuredHeight()) {
                        f18 -= dp;
                    }
                    f7 = f18;
                    float f20 = this.x + strokeWidth;
                    canvas2 = canvas;
                    f8 = f20;
                    f6 = f19;
                    f5 = f20;
                    paint = linePaint;
                } else {
                    f5 = i6 - strokeWidth2;
                    int i8 = this.y;
                    f6 = i8;
                    f7 = i8 + this.measuredHeight;
                    canvas2 = canvas;
                    f8 = f5;
                    paint = linePaint2;
                }
                canvas2.drawLine(f8, f6, f5, f7, paint);
                int i9 = this.y;
                if (i9 == 0) {
                    int i10 = this.x;
                    float f21 = i10;
                    float f22 = this.measuredWidth + i10;
                    if (i10 == 0) {
                        f21 += dp;
                    }
                    f11 = f21;
                    if (f22 == TableLayout.this.getMeasuredWidth()) {
                        f22 -= dp;
                    }
                    f10 = f22;
                    f9 = this.y + strokeWidth;
                    canvas3 = canvas;
                    f12 = f9;
                    paint2 = linePaint;
                } else {
                    int i11 = this.x;
                    f9 = i9 - strokeWidth2;
                    f10 = i11 + this.measuredWidth;
                    canvas3 = canvas;
                    f11 = i11;
                    f12 = f9;
                    paint2 = linePaint2;
                }
                canvas3.drawLine(f11, f12, f10, f9, paint2);
                float f23 = (this.x + this.measuredWidth) - strokeWidth;
                canvas.drawLine(f23, (z3 && (i = this.y) == 0) ? i + dp : this.y - strokeWidth, f23, (z3 && z4) ? (this.y + this.measuredHeight) - dp : (this.y + this.measuredHeight) - strokeWidth, linePaint);
                int i12 = this.x;
                float f24 = (this.y + this.measuredHeight) - strokeWidth;
                canvas.drawLine((i12 == 0 && z4) ? i12 + dp : i12 - strokeWidth, f24, (z3 && z4) ? (i12 + this.measuredWidth) - dp : (i12 + this.measuredWidth) - strokeWidth, f24, linePaint);
                if (this.x == 0 && this.y == 0) {
                    float f25 = this.x + strokeWidth;
                    float f26 = this.y + strokeWidth;
                    float f27 = dp * 2;
                    TableLayout.this.rect.set(f25, f26, f25 + f27, f27 + f26);
                    canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                }
                if (z3 && this.y == 0) {
                    float f28 = (this.x + this.measuredWidth) - strokeWidth;
                    float f29 = dp * 2;
                    float f30 = this.y + strokeWidth;
                    TableLayout.this.rect.set(f28 - f29, f30, f28, f29 + f30);
                    canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                }
                if (this.x == 0 && z4) {
                    float f31 = this.x + strokeWidth;
                    float f32 = (this.y + this.measuredHeight) - strokeWidth;
                    float f33 = dp * 2;
                    TableLayout.this.rect.set(f31, f32 - f33, f33 + f31, f32);
                    canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                }
                if (z3 && z4) {
                    float f34 = (this.x + this.measuredWidth) - strokeWidth;
                    float f35 = dp * 2;
                    float f36 = (this.y + this.measuredHeight) - strokeWidth;
                    TableLayout.this.rect.set(f34 - f35, f36 - f35, f34, f36);
                    canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                }
            }
        }

        public LayoutParams getLayoutParams() {
            return this.layoutParams;
        }

        public int getMeasuredHeight() {
            return this.measuredHeight;
        }

        public int getMeasuredWidth() {
            return this.measuredWidth;
        }

        public int getRow() {
            return this.rowspan + 10;
        }

        public int getTextX() {
            return this.x + this.textX;
        }

        public int getTextY() {
            return this.y + this.textY;
        }

        public void layout(int i, int i2, int i3, int i4) {
            this.x = i;
            this.y = i2;
        }

        public void measure(int r2, int r3, boolean r4) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TableLayout.Child.measure(int, int, boolean):void");
        }

        public void setFixedHeight(int i) {
            int i2;
            int i3 = this.fixedHeight;
            this.measuredHeight = i3;
            TLRPC.TL_pageTableCell tL_pageTableCell = this.cell;
            if (tL_pageTableCell.valign_middle) {
                i2 = (i3 - this.textHeight) / 2;
            } else if (!tL_pageTableCell.valign_bottom) {
                return;
            } else {
                i2 = (i3 - this.textHeight) - TableLayout.this.itemPaddingTop;
            }
            this.textY = i2;
        }

        public void setSelectionIndex(int i) {
            this.selectionIndex = i;
        }

        public void setTextLayout(ArticleViewer.DrawingText drawingText) {
            this.textLayout = drawingText;
            int i = 0;
            if (drawingText == null) {
                this.textLeft = 0;
                this.textWidth = 0;
                this.textHeight = 0;
                return;
            }
            this.textWidth = 0;
            this.textLeft = 0;
            int lineCount = drawingText.getLineCount();
            while (i < lineCount) {
                float lineLeft = drawingText.getLineLeft(i);
                this.textLeft = i == 0 ? (int) Math.ceil(lineLeft) : Math.min(this.textLeft, (int) Math.ceil(lineLeft));
                this.textWidth = (int) Math.ceil(Math.max(drawingText.getLineWidth(i), this.textWidth));
                i++;
            }
            this.textHeight = drawingText.getHeight();
        }
    }

    public static final class Interval {
        public final int max;
        public final int min;

        public Interval(int i, int i2) {
            this.min = i;
            this.max = i2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Interval.class != obj.getClass()) {
                return false;
            }
            Interval interval = (Interval) obj;
            return this.max == interval.max && this.min == interval.min;
        }

        public int hashCode() {
            return (this.min * 31) + this.max;
        }

        Interval inverse() {
            return new Interval(this.max, this.min);
        }

        int size() {
            return this.max - this.min;
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final Interval DEFAULT_SPAN;
        private static final int DEFAULT_SPAN_SIZE;
        public Spec columnSpec;
        public Spec rowSpec;

        static {
            Interval interval = new Interval(Integer.MIN_VALUE, -2147483647);
            DEFAULT_SPAN = interval;
            DEFAULT_SPAN_SIZE = interval.size();
        }

        public LayoutParams() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TableLayout.LayoutParams.<init>():void");
        }

        private LayoutParams(int i, int i2, int i3, int i4, int i5, int i6, Spec spec, Spec spec2) {
            super(i, i2);
            Spec spec3 = Spec.UNDEFINED;
            this.rowSpec = spec3;
            this.columnSpec = spec3;
            setMargins(i3, i4, i5, i6);
            this.rowSpec = spec;
            this.columnSpec = spec2;
        }

        public LayoutParams(Spec spec, Spec spec2) {
            this(-2, -2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, spec, spec2);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            LayoutParams layoutParams = (LayoutParams) obj;
            return this.columnSpec.equals(layoutParams.columnSpec) && this.rowSpec.equals(layoutParams.rowSpec);
        }

        public int hashCode() {
            return (this.rowSpec.hashCode() * 31) + this.columnSpec.hashCode();
        }

        final void setColumnSpecSpan(Interval interval) {
            this.columnSpec = this.columnSpec.copyWriteSpan(interval);
        }

        final void setRowSpecSpan(Interval interval) {
            this.rowSpec = this.rowSpec.copyWriteSpan(interval);
        }
    }

    public static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int i) {
            this.value = i;
        }

        public void reset() {
            this.value = Integer.MIN_VALUE;
        }
    }

    public static final class PackedMap {
        public final int[] index;
        public final Object[] keys;
        public final Object[] values;

        private PackedMap(Object[] objArr, Object[] objArr2) {
            int[] createIndex = createIndex(objArr);
            this.index = createIndex;
            this.keys = compact(objArr, createIndex);
            this.values = compact(objArr2, createIndex);
        }

        private static Object[] compact(Object[] objArr, int[] iArr) {
            int length = objArr.length;
            Object[] objArr2 = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), TableLayout.max2(iArr, -1) + 1);
            for (int i = 0; i < length; i++) {
                objArr2[iArr[i]] = objArr[i];
            }
            return objArr2;
        }

        private static int[] createIndex(Object[] objArr) {
            int length = objArr.length;
            int[] iArr = new int[length];
            HashMap hashMap = new HashMap();
            for (int i = 0; i < length; i++) {
                Object obj = objArr[i];
                Integer num = (Integer) hashMap.get(obj);
                if (num == null) {
                    num = Integer.valueOf(hashMap.size());
                    hashMap.put(obj, num);
                }
                iArr[i] = num.intValue();
            }
            return iArr;
        }

        public Object getValue(int i) {
            return this.values[this.index[i]];
        }
    }

    public static class Spec {
        static final Spec UNDEFINED = TableLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        float weight;

        private Spec(boolean z, int i, int i2, Alignment alignment, float f) {
            this(z, new Interval(i, i2 + i), alignment, f);
        }

        private Spec(boolean z, Interval interval, Alignment alignment, float f) {
            this.startDefined = z;
            this.span = interval;
            this.alignment = alignment;
            this.weight = f;
        }

        public Alignment getAbsoluteAlignment(boolean z) {
            Alignment alignment = this.alignment;
            return alignment != TableLayout.UNDEFINED_ALIGNMENT ? alignment : this.weight == 0.0f ? z ? TableLayout.START : TableLayout.BASELINE : TableLayout.FILL;
        }

        final Spec copyWriteSpan(Interval interval) {
            return new Spec(this.startDefined, interval, this.alignment, this.weight);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Spec spec = (Spec) obj;
            return this.alignment.equals(spec.alignment) && this.span.equals(spec.span);
        }

        final int getFlexibility() {
            return (this.alignment == TableLayout.UNDEFINED_ALIGNMENT && this.weight == 0.0f) ? 0 : 2;
        }

        public int hashCode() {
            return (this.span.hashCode() * 31) + this.alignment.hashCode();
        }
    }

    public interface TableLayoutDelegate {
        ArticleViewer.DrawingText createTextLayout(TLRPC.TL_pageTableCell tL_pageTableCell, int i);

        Paint getHeaderPaint();

        Paint getLinePaint();

        Paint getStripPaint();

        void onLayoutChild(ArticleViewer.DrawingText drawingText, int i, int i2);
    }

    static {
        Alignment alignment = new Alignment() {
            @Override
            public int getAlignmentValue(Child child, int i) {
                return 0;
            }

            @Override
            int getGravityOffset(Child child, int i) {
                return 0;
            }
        };
        LEADING = alignment;
        Alignment alignment2 = new Alignment() {
            @Override
            public int getAlignmentValue(Child child, int i) {
                return i;
            }

            @Override
            int getGravityOffset(Child child, int i) {
                return i;
            }
        };
        TRAILING = alignment2;
        TOP = alignment;
        BOTTOM = alignment2;
        START = alignment;
        END = alignment2;
        LEFT = createSwitchingAlignment(alignment);
        RIGHT = createSwitchingAlignment(alignment2);
        CENTER = new Alignment() {
            @Override
            public int getAlignmentValue(Child child, int i) {
                return i >> 1;
            }

            @Override
            int getGravityOffset(Child child, int i) {
                return i >> 1;
            }
        };
        BASELINE = new Alignment() {
            @Override
            public int getAlignmentValue(Child child, int i) {
                return Integer.MIN_VALUE;
            }

            @Override
            public Bounds getBounds() {
                return new Bounds() {
                    private int size;

                    @Override
                    protected int getOffset(TableLayout tableLayout, Child child, Alignment alignment3, int i, boolean z) {
                        return Math.max(0, super.getOffset(tableLayout, child, alignment3, i, z));
                    }

                    @Override
                    protected void include(int i, int i2) {
                        super.include(i, i2);
                        this.size = Math.max(this.size, i + i2);
                    }

                    @Override
                    protected void reset() {
                        super.reset();
                        this.size = Integer.MIN_VALUE;
                    }

                    @Override
                    protected int size(boolean z) {
                        return Math.max(super.size(z), this.size);
                    }
                };
            }

            @Override
            int getGravityOffset(Child child, int i) {
                return 0;
            }
        };
        FILL = new Alignment() {
            @Override
            public int getAlignmentValue(Child child, int i) {
                return Integer.MIN_VALUE;
            }

            @Override
            int getGravityOffset(Child child, int i) {
                return 0;
            }

            @Override
            public int getSizeInCell(Child child, int i, int i2) {
                return i2;
            }
        };
    }

    public TableLayout(Context context, TableLayoutDelegate tableLayoutDelegate, TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper) {
        super(context);
        this.mHorizontalAxis = new Axis(true);
        this.mVerticalAxis = new Axis(false);
        this.mOrientation = 0;
        this.mUseDefaultMargins = false;
        this.mAlignmentMode = 1;
        this.mLastLayoutParamsHashCode = 0;
        this.itemPaddingTop = AndroidUtilities.dp(7.0f);
        this.itemPaddingLeft = AndroidUtilities.dp(8.0f);
        this.cellsToFixHeight = new ArrayList();
        this.rowSpans = new ArrayList();
        this.linePath = new Path();
        this.backgroundPath = new Path();
        this.rect = new RectF();
        this.radii = new float[8];
        this.childrens = new ArrayList();
        this.textSelectionHelper = articleTextSelectionHelper;
        setRowCount(Integer.MIN_VALUE);
        setColumnCount(Integer.MIN_VALUE);
        setOrientation(0);
        setUseDefaultMargins(false);
        setAlignmentMode(1);
        setRowOrderPreserved(true);
        setColumnOrderPreserved(true);
        this.delegate = tableLayoutDelegate;
    }

    static Object[] append(Object[] objArr, Object[] objArr2) {
        Object[] objArr3 = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), objArr.length + objArr2.length);
        System.arraycopy(objArr, 0, objArr3, 0, objArr.length);
        System.arraycopy(objArr2, 0, objArr3, objArr.length, objArr2.length);
        return objArr3;
    }

    static boolean canStretch(int i) {
        return (i & 2) != 0;
    }

    private static int clip(Interval interval, boolean z, int i) {
        int size = interval.size();
        if (i == 0) {
            return size;
        }
        return Math.min(size, i - (z ? Math.min(interval.min, i) : 0));
    }

    private int computeLayoutParamsHashCode() {
        int childCount = getChildCount();
        int i = 1;
        for (int i2 = 0; i2 < childCount; i2++) {
            i = (i * 31) + getChildAt(i2).getLayoutParams().hashCode();
        }
        return i;
    }

    private void consistencyCheck() {
        int i = this.mLastLayoutParamsHashCode;
        if (i == 0) {
            validateLayoutParams();
            this.mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (i != computeLayoutParamsHashCode()) {
            invalidateStructure();
            consistencyCheck();
        }
    }

    private static Alignment createSwitchingAlignment(final Alignment alignment) {
        return new Alignment() {
            @Override
            public int getAlignmentValue(Child child, int i) {
                return Alignment.this.getAlignmentValue(child, i);
            }

            @Override
            int getGravityOffset(Child child, int i) {
                return Alignment.this.getGravityOffset(child, i);
            }
        };
    }

    private static boolean fits(int[] iArr, int i, int i2, int i3) {
        if (i3 > iArr.length) {
            return false;
        }
        while (i2 < i3) {
            if (iArr[i2] > i) {
                return false;
            }
            i2++;
        }
        return true;
    }

    private int getDefaultMargin(Child child, LayoutParams layoutParams, boolean z, boolean z2) {
        boolean z3 = false;
        if (!this.mUseDefaultMargins) {
            return 0;
        }
        Spec spec = z ? layoutParams.columnSpec : layoutParams.rowSpec;
        Axis axis = z ? this.mHorizontalAxis : this.mVerticalAxis;
        Interval interval = spec.span;
        if ((z && this.isRtl) == z2 ? interval.max == axis.getCount() : interval.min == 0) {
            z3 = true;
        }
        return getDefaultMargin(child, z3, z, z2);
    }

    private int getDefaultMargin(Child child, boolean z, boolean z2) {
        return this.mDefaultGap / 2;
    }

    private int getDefaultMargin(Child child, boolean z, boolean z2, boolean z3) {
        return getDefaultMargin(child, z2, z3);
    }

    private int getMargin(Child child, boolean z, boolean z2) {
        if (this.mAlignmentMode == 1) {
            return getMargin1(child, z, z2);
        }
        Axis axis = z ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] leadingMargins = z2 ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams layoutParams = child.getLayoutParams();
        Interval interval = (z ? layoutParams.columnSpec : layoutParams.rowSpec).span;
        return leadingMargins[z2 ? interval.min : interval.max];
    }

    private int getMeasurement(Child child, boolean z) {
        return z ? child.getMeasuredWidth() : child.getMeasuredHeight();
    }

    private int getTotalMargin(Child child, boolean z) {
        return getMargin(child, z, true) + getMargin(child, z, false);
    }

    public static void handleInvalidParams(String str) {
        throw new IllegalArgumentException(str + ". ");
    }

    private void invalidateStructure() {
        this.mLastLayoutParamsHashCode = 0;
        this.mHorizontalAxis.invalidateStructure();
        this.mVerticalAxis.invalidateStructure();
        invalidateValues();
    }

    private void invalidateValues() {
        Axis axis = this.mHorizontalAxis;
        if (axis == null || this.mVerticalAxis == null) {
            return;
        }
        axis.invalidateValues();
        this.mVerticalAxis.invalidateValues();
    }

    static int max2(int[] iArr, int i) {
        for (int i2 : iArr) {
            i = Math.max(i, i2);
        }
        return i;
    }

    private void measureChildWithMargins2(Child child, int i, int i2, int i3, int i4, boolean z) {
        child.measure(getTotalMargin(child, true) + i3, getTotalMargin(child, false) + i4, z);
    }

    private void measureChildrenWithMargins(int i, int i2, boolean z) {
        int totalMargin;
        int i3;
        boolean z2;
        TableLayout tableLayout;
        int i4;
        int i5;
        int childCount = getChildCount();
        for (int i6 = 0; i6 < childCount; i6++) {
            Child childAt = getChildAt(i6);
            LayoutParams layoutParams = childAt.getLayoutParams();
            if (z) {
                float size = View.MeasureSpec.getSize(i);
                childAt.setTextLayout(this.delegate.createTextLayout(childAt.cell, this.colCount == 2 ? ((int) (size / 2.0f)) - (this.itemPaddingLeft * 4) : (int) (size / 1.5f)));
                if (childAt.textLayout != null) {
                    ((ViewGroup.MarginLayoutParams) layoutParams).width = childAt.textWidth + (this.itemPaddingLeft * 2);
                    ((ViewGroup.MarginLayoutParams) layoutParams).height = childAt.textHeight + (this.itemPaddingTop * 2);
                } else {
                    ((ViewGroup.MarginLayoutParams) layoutParams).width = 0;
                    ((ViewGroup.MarginLayoutParams) layoutParams).height = 0;
                }
                i3 = ((ViewGroup.MarginLayoutParams) layoutParams).width;
                totalMargin = ((ViewGroup.MarginLayoutParams) layoutParams).height;
                z2 = true;
            } else {
                boolean z3 = this.mOrientation == 0;
                Spec spec = z3 ? layoutParams.columnSpec : layoutParams.rowSpec;
                if (spec.getAbsoluteAlignment(z3) == FILL) {
                    Interval interval = spec.span;
                    int[] locations = (z3 ? this.mHorizontalAxis : this.mVerticalAxis).getLocations();
                    totalMargin = (locations[interval.max] - locations[interval.min]) - getTotalMargin(childAt, z3);
                    if (z3) {
                        int i7 = ((ViewGroup.MarginLayoutParams) layoutParams).height;
                        tableLayout = this;
                        i4 = i;
                        i5 = i2;
                        i3 = totalMargin;
                        totalMargin = i7;
                        z2 = false;
                        tableLayout.measureChildWithMargins2(childAt, i4, i5, i3, totalMargin, z2);
                    } else {
                        i3 = ((ViewGroup.MarginLayoutParams) layoutParams).width;
                        z2 = false;
                    }
                }
            }
            tableLayout = this;
            i4 = i;
            i5 = i2;
            tableLayout.measureChildWithMargins2(childAt, i4, i5, i3, totalMargin, z2);
        }
    }

    private static void procrusteanFill(int[] iArr, int i, int i2, int i3) {
        int length = iArr.length;
        Arrays.fill(iArr, Math.min(i, length), Math.min(i2, length), i3);
    }

    private static void setCellGroup(LayoutParams layoutParams, int i, int i2, int i3, int i4) {
        layoutParams.setRowSpecSpan(new Interval(i, i2 + i));
        layoutParams.setColumnSpecSpan(new Interval(i3, i4 + i3));
    }

    public static Spec spec(int i) {
        return spec(i, 1);
    }

    public static Spec spec(int i, int i2) {
        return spec(i, i2, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int i, int i2, Alignment alignment) {
        return spec(i, i2, alignment, 0.0f);
    }

    public static Spec spec(int i, int i2, Alignment alignment, float f) {
        return new Spec(i != Integer.MIN_VALUE, i, i2, alignment, f);
    }

    private void validateLayoutParams() {
        boolean z = this.mOrientation == 0;
        int i = (z ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (i == Integer.MIN_VALUE) {
            i = 0;
        }
        int[] iArr = new int[i];
        int childCount = getChildCount();
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            LayoutParams layoutParams = getChildAt(i4).getLayoutParams();
            Spec spec = z ? layoutParams.rowSpec : layoutParams.columnSpec;
            Interval interval = spec.span;
            boolean z2 = spec.startDefined;
            int size = interval.size();
            if (z2) {
                i2 = interval.min;
            }
            Spec spec2 = z ? layoutParams.columnSpec : layoutParams.rowSpec;
            Interval interval2 = spec2.span;
            boolean z3 = spec2.startDefined;
            int clip = clip(interval2, z3, i);
            if (z3) {
                i3 = interval2.min;
            }
            if (i != 0) {
                if (!z2 || !z3) {
                    while (true) {
                        int i5 = i3 + clip;
                        if (fits(iArr, i2, i3, i5)) {
                            break;
                        }
                        if (z3) {
                            i2++;
                        } else if (i5 <= i) {
                            i3++;
                        } else {
                            i2++;
                            i3 = 0;
                        }
                    }
                }
                procrusteanFill(iArr, i3, i3 + clip, i2 + size);
            }
            if (z) {
                setCellGroup(layoutParams, i2, size, i3, clip);
            } else {
                setCellGroup(layoutParams, i3, clip, i2, size);
            }
            i3 += clip;
        }
    }

    public void addChild(int i, int i2, int i3, int i4) {
        Child child = new Child(this.childrens.size());
        LayoutParams layoutParams = new LayoutParams();
        Interval interval = new Interval(i2, i2 + i4);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(i, i + i3), alignment, 0.0f);
        child.layoutParams = layoutParams;
        child.rowspan = i2;
        this.childrens.add(child);
        invalidateStructure();
    }

    public void addChild(TLRPC.TL_pageTableCell tL_pageTableCell, int i, int i2, int i3) {
        int i4 = i3 == 0 ? 1 : i3;
        Child child = new Child(this.childrens.size());
        child.cell = tL_pageTableCell;
        LayoutParams layoutParams = new LayoutParams();
        int i5 = tL_pageTableCell.rowspan;
        if (i5 == 0) {
            i5 = 1;
        }
        Interval interval = new Interval(i2, i5 + i2);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(i, i4 + i), alignment, 1.0f);
        child.layoutParams = layoutParams;
        child.rowspan = i2;
        this.childrens.add(child);
        if (tL_pageTableCell.rowspan > 1) {
            this.rowSpans.add(new Point(i2, r1 + i2));
        }
        invalidateStructure();
    }

    public int getAlignmentMode() {
        return this.mAlignmentMode;
    }

    public Child getChildAt(int i) {
        if (i < 0 || i >= this.childrens.size()) {
            return null;
        }
        return (Child) this.childrens.get(i);
    }

    public int getChildCount() {
        return this.childrens.size();
    }

    public int getColumnCount() {
        return this.mHorizontalAxis.getCount();
    }

    int getMargin1(Child child, boolean z, boolean z2) {
        LayoutParams layoutParams = child.getLayoutParams();
        int i = z ? z2 ? ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin : ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin : z2 ? ((ViewGroup.MarginLayoutParams) layoutParams).topMargin : ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        return i == Integer.MIN_VALUE ? getDefaultMargin(child, layoutParams, z, z2) : i;
    }

    final int getMeasurementIncludingMargin(Child child, boolean z) {
        return getMeasurement(child, z) + getTotalMargin(child, z);
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public int getRowCount() {
        return this.mVerticalAxis.getCount();
    }

    public boolean getUseDefaultMargins() {
        return this.mUseDefaultMargins;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).draw(canvas, this);
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        consistencyCheck();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int measure;
        int i3;
        boolean z;
        int i4;
        consistencyCheck();
        invalidateValues();
        boolean z2 = false;
        this.colCount = 0;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            this.colCount = Math.max(this.colCount, getChildAt(i5).layoutParams.columnSpec.span.max);
        }
        boolean z3 = true;
        measureChildrenWithMargins(i, i2, true);
        if (this.mOrientation == 0) {
            measure = this.mHorizontalAxis.getMeasure(i);
            measureChildrenWithMargins(i, i2, false);
            i3 = this.mVerticalAxis.getMeasure(i2);
        } else {
            int measure2 = this.mVerticalAxis.getMeasure(i2);
            measureChildrenWithMargins(i, i2, false);
            measure = this.mHorizontalAxis.getMeasure(i);
            i3 = measure2;
        }
        int max = Math.max(measure, View.MeasureSpec.getSize(i));
        int max2 = Math.max(i3, getSuggestedMinimumHeight());
        setMeasuredDimension(max, max2);
        this.mHorizontalAxis.layout(max);
        this.mVerticalAxis.layout(max2);
        int[] locations = this.mHorizontalAxis.getLocations();
        int[] locations2 = this.mVerticalAxis.getLocations();
        this.cellsToFixHeight.clear();
        int i6 = locations[locations.length - 1];
        int childCount2 = getChildCount();
        int i7 = 0;
        while (i7 < childCount2) {
            Child childAt = getChildAt(i7);
            LayoutParams layoutParams = childAt.getLayoutParams();
            Spec spec = layoutParams.columnSpec;
            Spec spec2 = layoutParams.rowSpec;
            Interval interval = spec.span;
            Interval interval2 = spec2.span;
            int i8 = locations[interval.min];
            int i9 = locations2[interval2.min];
            int i10 = locations[interval.max] - i8;
            int i11 = locations2[interval2.max] - i9;
            int measurement = getMeasurement(childAt, z3);
            int measurement2 = getMeasurement(childAt, z2);
            Alignment absoluteAlignment = spec.getAbsoluteAlignment(z3);
            Alignment absoluteAlignment2 = spec2.getAbsoluteAlignment(z2);
            Bounds bounds = (Bounds) this.mHorizontalAxis.getGroupBounds().getValue(i7);
            Bounds bounds2 = (Bounds) this.mVerticalAxis.getGroupBounds().getValue(i7);
            int gravityOffset = absoluteAlignment.getGravityOffset(childAt, i10 - bounds.size(z3));
            int gravityOffset2 = absoluteAlignment2.getGravityOffset(childAt, i11 - bounds2.size(z3));
            int margin = getMargin(childAt, z3, z3);
            int margin2 = getMargin(childAt, false, z3);
            int margin3 = getMargin(childAt, z3, false);
            int i12 = margin + margin3;
            int margin4 = margin2 + getMargin(childAt, false, false);
            int i13 = max2;
            int offset = bounds.getOffset(this, childAt, absoluteAlignment, measurement + i12, true);
            int offset2 = bounds2.getOffset(this, childAt, absoluteAlignment2, measurement2 + margin4, false);
            int sizeInCell = absoluteAlignment.getSizeInCell(childAt, measurement, i10 - i12);
            int sizeInCell2 = absoluteAlignment2.getSizeInCell(childAt, measurement2, i11 - margin4);
            int i14 = i8 + gravityOffset + offset;
            int i15 = !this.isRtl ? margin + i14 : ((i6 - sizeInCell) - margin3) - i14;
            int i16 = i9 + gravityOffset2 + offset2 + margin2;
            if (childAt.cell != null) {
                if (sizeInCell != childAt.getMeasuredWidth() || sizeInCell2 != childAt.getMeasuredHeight()) {
                    childAt.measure(sizeInCell, sizeInCell2, false);
                }
                if (childAt.fixedHeight != 0 && childAt.fixedHeight != sizeInCell2 && childAt.layoutParams.rowSpec.span.max - childAt.layoutParams.rowSpec.span.min <= 1) {
                    int size = this.rowSpans.size();
                    while (true) {
                        if (i4 >= size) {
                            this.cellsToFixHeight.add(childAt);
                            break;
                        } else {
                            Point point = (Point) this.rowSpans.get(i4);
                            i4 = (point.x > ((float) childAt.layoutParams.rowSpec.span.min) || point.y <= ((float) childAt.layoutParams.rowSpec.span.min)) ? i4 + 1 : 0;
                        }
                    }
                }
            }
            childAt.layout(i15, i16, sizeInCell + i15, sizeInCell2 + i16);
            i7++;
            max2 = i13;
            z2 = false;
            z3 = true;
        }
        int size2 = this.cellsToFixHeight.size();
        int i17 = 0;
        while (i17 < size2) {
            Child child = (Child) this.cellsToFixHeight.get(i17);
            int i18 = child.measuredHeight - child.fixedHeight;
            int size3 = this.childrens.size();
            for (int i19 = child.index + 1; i19 < size3; i19++) {
                Child child2 = (Child) this.childrens.get(i19);
                if (child.layoutParams.rowSpec.span.min != child2.layoutParams.rowSpec.span.min) {
                    break;
                }
                if (child.fixedHeight < child2.fixedHeight) {
                    z = true;
                    break;
                }
                int i20 = child2.measuredHeight - child2.fixedHeight;
                if (i20 > 0) {
                    i18 = Math.min(i18, i20);
                }
            }
            z = false;
            if (!z) {
                int i21 = child.index - 1;
                while (true) {
                    if (i21 < 0) {
                        break;
                    }
                    Child child3 = (Child) this.childrens.get(i21);
                    if (child.layoutParams.rowSpec.span.min != child3.layoutParams.rowSpec.span.min) {
                        break;
                    }
                    if (child.fixedHeight < child3.fixedHeight) {
                        z = true;
                        break;
                    }
                    int i22 = child3.measuredHeight - child3.fixedHeight;
                    if (i22 > 0) {
                        i18 = Math.min(i18, i22);
                    }
                    i21--;
                }
            }
            if (!z) {
                child.setFixedHeight(child.fixedHeight);
                max2 -= i18;
                int size4 = this.childrens.size();
                int i23 = i17;
                for (int i24 = 0; i24 < size4; i24++) {
                    Child child4 = (Child) this.childrens.get(i24);
                    if (child != child4) {
                        if (child.layoutParams.rowSpec.span.min == child4.layoutParams.rowSpec.span.min) {
                            if (child4.fixedHeight != child4.measuredHeight) {
                                this.cellsToFixHeight.remove(child4);
                                if (child4.index < child.index) {
                                    i23--;
                                }
                                size2--;
                            }
                            Child.access$1520(child4, i18);
                            child4.measure(child4.measuredWidth, child4.measuredHeight, true);
                        } else if (child.layoutParams.rowSpec.span.min < child4.layoutParams.rowSpec.span.min) {
                            child4.y -= i18;
                        }
                    }
                }
                i17 = i23;
            }
            i17++;
        }
        int childCount3 = getChildCount();
        for (int i25 = 0; i25 < childCount3; i25++) {
            Child childAt2 = getChildAt(i25);
            this.delegate.onLayoutChild(childAt2.textLayout, childAt2.getTextX(), childAt2.getTextY());
        }
        setMeasuredDimension(i6, max2);
    }

    public void removeAllChildrens() {
        this.childrens.clear();
        this.rowSpans.clear();
        invalidateStructure();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    public void setAlignmentMode(int i) {
        this.mAlignmentMode = i;
        requestLayout();
    }

    public void setColumnCount(int i) {
        this.mHorizontalAxis.setCount(i);
        invalidateStructure();
        requestLayout();
    }

    public void setColumnOrderPreserved(boolean z) {
        this.mHorizontalAxis.setOrderPreserved(z);
        invalidateStructure();
        requestLayout();
    }

    public void setDrawLines(boolean z) {
        this.drawLines = z;
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            invalidateStructure();
            requestLayout();
        }
    }

    public void setRowCount(int i) {
        this.mVerticalAxis.setCount(i);
        invalidateStructure();
        requestLayout();
    }

    public void setRowOrderPreserved(boolean z) {
        this.mVerticalAxis.setOrderPreserved(z);
        invalidateStructure();
        requestLayout();
    }

    public void setRtl(boolean z) {
        this.isRtl = z;
    }

    public void setStriped(boolean z) {
        this.isStriped = z;
    }

    public void setUseDefaultMargins(boolean z) {
        this.mUseDefaultMargins = z;
        requestLayout();
    }
}
