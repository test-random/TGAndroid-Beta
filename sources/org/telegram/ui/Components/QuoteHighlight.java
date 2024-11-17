package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.StaticLayout;
import android.view.View;
import android.view.ViewParent;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;

public class QuoteHighlight extends Path {
    private float currentOffsetX;
    private float currentOffsetY;
    public final int end;
    public final int id;
    private Rect lastRect;
    private float minX;
    public final Paint paint;
    private final CornerPath path;
    public final ArrayList quotesToExpand;
    private final ArrayList rectangles;
    public final int start;
    private final AnimatedFloat t;

    public static class Rect {
        public float bottom;
        public boolean first;
        public boolean last;
        public float left;
        public float nextBottom;
        public float prevTop;
        public float right;
        public float top;

        private Rect() {
        }
    }

    public QuoteHighlight(final View view, final ViewParent viewParent, int i, ArrayList arrayList, int i2, int i3, float f) {
        int i4;
        Paint paint = new Paint(1);
        this.paint = paint;
        this.path = new CornerPath();
        this.rectangles = new ArrayList();
        this.quotesToExpand = new ArrayList();
        this.t = new AnimatedFloat(0.0f, new Runnable() {
            @Override
            public final void run() {
                QuoteHighlight.lambda$new$0(view, viewParent);
            }
        }, 350L, 420L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.id = i;
        this.start = i2;
        this.end = i3;
        if (arrayList == null) {
            return;
        }
        paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(4.0f)));
        boolean z = false;
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            MessageObject.TextLayoutBlock textLayoutBlock = (MessageObject.TextLayoutBlock) arrayList.get(i5);
            if (textLayoutBlock != null && i2 <= textLayoutBlock.charactersEnd && i3 >= (i4 = textLayoutBlock.charactersOffset)) {
                int max = Math.max(0, i2 - i4);
                int i6 = textLayoutBlock.charactersOffset;
                int min = Math.min(i3 - i6, textLayoutBlock.charactersEnd - i6);
                float f2 = -f;
                this.currentOffsetX = f2;
                if (textLayoutBlock.code && !textLayoutBlock.quote) {
                    this.currentOffsetX = f2 + AndroidUtilities.dp(10.0f);
                }
                this.currentOffsetY = textLayoutBlock.textYOffset(arrayList) + textLayoutBlock.padTop;
                this.minX = textLayoutBlock.quote ? AndroidUtilities.dp(10.0f) : 0.0f;
                z = z || AndroidUtilities.isRTL(textLayoutBlock.textLayout.getText());
                StaticLayout staticLayout = textLayoutBlock.textLayout;
                if (z) {
                    staticLayout.getSelectionPath(max, min, this);
                } else {
                    getSelectionPath(staticLayout, max, min);
                }
                if (textLayoutBlock.quoteCollapse && textLayoutBlock.collapsed()) {
                    this.quotesToExpand.add(Integer.valueOf(textLayoutBlock.index));
                }
            }
        }
        if (this.rectangles.size() > 0) {
            Rect rect = (Rect) this.rectangles.get(0);
            ArrayList arrayList2 = this.rectangles;
            Rect rect2 = (Rect) arrayList2.get(arrayList2.size() - 1);
            rect.first = true;
            rect.top -= AndroidUtilities.dp(0.66f);
            rect2.last = true;
            rect2.bottom += AndroidUtilities.dp(0.66f);
        }
    }

    private void getSelectionPath(Layout layout, int i, int i2) {
        if (i == i2) {
            return;
        }
        if (i2 < i) {
            i2 = i;
            i = i2;
        }
        int lineForOffset = layout.getLineForOffset(i);
        int lineForOffset2 = layout.getLineForOffset(i2);
        int i3 = lineForOffset;
        while (i3 <= lineForOffset2) {
            int lineStart = layout.getLineStart(i3);
            int lineEnd = layout.getLineEnd(i3);
            if (lineEnd != lineStart && (lineStart + 1 != lineEnd || !Character.isWhitespace(layout.getText().charAt(lineStart)))) {
                float lineLeft = (i3 != lineForOffset || i <= lineStart) ? layout.getLineLeft(i3) : layout.getPrimaryHorizontal(i);
                float lineRight = (i3 != lineForOffset2 || i2 >= lineEnd) ? layout.getLineRight(i3) : layout.getPrimaryHorizontal(i2);
                addRect(Math.min(lineLeft, lineRight), layout.getLineTop(i3), Math.max(lineLeft, lineRight), layout.getLineBottom(i3));
            }
            i3++;
        }
    }

    public static void lambda$new$0(View view, ViewParent viewParent) {
        if (view != null) {
            view.invalidate();
        }
        if (viewParent instanceof View) {
            ((View) viewParent).invalidate();
        }
    }

    public void addRect(float f, float f2, float f3, float f4) {
        if (f >= f3) {
            return;
        }
        float max = Math.max(this.minX, f);
        float max2 = Math.max(this.minX, f3);
        float f5 = this.currentOffsetX;
        float f6 = max + f5;
        float f7 = this.currentOffsetY;
        float f8 = f2 + f7;
        float f9 = max2 + f5;
        Rect rect = new Rect();
        rect.left = f6 - AndroidUtilities.dp(3.0f);
        rect.right = f9 + AndroidUtilities.dp(3.0f);
        rect.top = f8;
        rect.bottom = f4 + f7;
        Rect rect2 = this.lastRect;
        if (rect2 != null) {
            float f10 = (rect2.bottom + f8) / 2.0f;
            rect2.nextBottom = f10;
            rect.prevTop = f10;
        }
        this.rectangles.add(rect);
        this.lastRect = rect;
    }

    @Override
    public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
        addRect(f, f2, f3, f4);
    }

    public boolean done() {
        return this.t.get() >= 1.0f;
    }

    public void draw(Canvas canvas, float f, float f2, android.graphics.Rect rect, float f3) {
        float f4 = this.t.set(1.0f);
        canvas.save();
        canvas.translate(f, f2);
        this.path.rewind();
        for (int i = 0; i < this.rectangles.size(); i++) {
            Rect rect2 = (Rect) this.rectangles.get(i);
            this.path.addRect(AndroidUtilities.lerp(rect.left - f, rect2.left, f4), AndroidUtilities.lerp(rect2.first ? rect.top - f2 : rect2.prevTop, rect2.top, f4), AndroidUtilities.lerp(rect.right - f, rect2.right, f4), AndroidUtilities.lerp(rect2.last ? rect.bottom - f2 : rect2.nextBottom, rect2.bottom, f4), Path.Direction.CW);
        }
        this.path.closeRects();
        int alpha = this.paint.getAlpha();
        this.paint.setAlpha((int) (alpha * f3));
        canvas.drawPath(this.path, this.paint);
        this.paint.setAlpha(alpha);
        canvas.restore();
    }

    public float getT() {
        return this.t.set(1.0f);
    }
}
