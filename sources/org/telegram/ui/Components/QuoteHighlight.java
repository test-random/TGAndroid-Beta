package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.view.View;
import android.view.ViewParent;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
public class QuoteHighlight extends Path {
    private float currentOffsetX;
    private float currentOffsetY;
    public final int id;
    private Rect lastRect;
    public final Paint paint;
    private final Path path;
    private final ArrayList<Rect> rectangles;
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

    public QuoteHighlight(final View view, final ViewParent viewParent, int i, ArrayList<MessageObject.TextLayoutBlock> arrayList, int i2, int i3, float f) {
        int i4;
        Paint paint = new Paint(1);
        this.paint = paint;
        this.path = new Path();
        this.rectangles = new ArrayList<>();
        this.t = new AnimatedFloat(0.0f, new Runnable() {
            @Override
            public final void run() {
                QuoteHighlight.lambda$new$0(view, viewParent);
            }
        }, 350L, 420L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.id = i;
        if (arrayList == null) {
            return;
        }
        paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(4.0f)));
        boolean z = false;
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i5);
            if (textLayoutBlock != null && i2 <= textLayoutBlock.charactersEnd && i3 >= (i4 = textLayoutBlock.charactersOffset)) {
                int max = Math.max(0, i2 - i4);
                int i6 = textLayoutBlock.charactersOffset;
                int min = Math.min(i3 - i6, textLayoutBlock.charactersEnd - i6);
                this.currentOffsetX = -f;
                this.currentOffsetY = textLayoutBlock.textYOffset + textLayoutBlock.padTop;
                z = z || textLayoutBlock.isRtl();
                if (z) {
                    textLayoutBlock.textLayout.getSelectionPath(max, min, this);
                } else {
                    getSelectionPath(textLayoutBlock.textLayout, max, min);
                }
            }
        }
        if (this.rectangles.size() > 0) {
            Rect rect = this.rectangles.get(0);
            ArrayList<Rect> arrayList2 = this.rectangles;
            Rect rect2 = arrayList2.get(arrayList2.size() - 1);
            rect.first = true;
            rect.top -= AndroidUtilities.dp(0.66f);
            rect2.last = true;
            rect2.bottom += AndroidUtilities.dp(0.66f);
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

    private void getSelectionPath(Layout layout, int i, int i2) {
        float lineLeft;
        float lineRight;
        if (i == i2) {
            return;
        }
        if (i2 < i) {
            i2 = i;
            i = i2;
        }
        int lineForOffset = layout.getLineForOffset(i);
        int lineForOffset2 = layout.getLineForOffset(i2);
        for (int i3 = lineForOffset; i3 <= lineForOffset2; i3++) {
            int lineStart = layout.getLineStart(i3);
            int lineEnd = layout.getLineEnd(i3);
            if (lineEnd != lineStart && (lineStart + 1 != lineEnd || !Character.isWhitespace(layout.getText().charAt(lineStart)))) {
                if (i3 == lineForOffset && i > lineStart) {
                    lineLeft = layout.getPrimaryHorizontal(i);
                } else {
                    lineLeft = layout.getLineLeft(i3);
                }
                if (i3 == lineForOffset2 && i2 < lineEnd) {
                    lineRight = layout.getPrimaryHorizontal(i2);
                } else {
                    lineRight = layout.getLineRight(i3);
                }
                addRect(Math.min(lineLeft, lineRight), layout.getLineTop(i3), Math.max(lineLeft, lineRight), layout.getLineBottom(i3));
            }
        }
    }

    public float getT() {
        return this.t.set(1.0f);
    }

    public void draw(Canvas canvas, float f, float f2, android.graphics.Rect rect, float f3) {
        float f4 = this.t.set(1.0f);
        canvas.save();
        canvas.translate(f, f2);
        this.path.rewind();
        for (int i = 0; i < this.rectangles.size(); i++) {
            Rect rect2 = this.rectangles.get(i);
            this.path.addRect(AndroidUtilities.lerp(rect.left - f, rect2.left, f4), AndroidUtilities.lerp(rect2.first ? rect.top - f2 : rect2.prevTop, rect2.top, f4), AndroidUtilities.lerp(rect.right - f, rect2.right, f4), AndroidUtilities.lerp(rect2.last ? rect.bottom - f2 : rect2.nextBottom, rect2.bottom, f4), Path.Direction.CW);
        }
        int alpha = this.paint.getAlpha();
        this.paint.setAlpha((int) (alpha * f3));
        canvas.drawPath(this.path, this.paint);
        this.paint.setAlpha(alpha);
        canvas.restore();
    }

    @Override
    public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
        addRect(f, f2, f3, f4);
    }

    public void addRect(float f, float f2, float f3, float f4) {
        if (f >= f3) {
            return;
        }
        Rect rect = this.lastRect;
        if (rect != null && Math.abs(rect.top - f2) < 1.0f) {
            Rect rect2 = this.lastRect;
            rect2.left = Math.min(rect2.left, f);
            Rect rect3 = this.lastRect;
            rect3.right = Math.min(rect3.right, f3);
            return;
        }
        float f5 = this.currentOffsetX;
        float f6 = f + f5;
        float f7 = this.currentOffsetY;
        float f8 = f2 + f7;
        float f9 = f3 + f5;
        Rect rect4 = new Rect();
        rect4.left = f6 - AndroidUtilities.dp(3.0f);
        rect4.right = f9 + AndroidUtilities.dp(3.0f);
        rect4.top = f8;
        rect4.bottom = f4 + f7;
        Rect rect5 = this.lastRect;
        if (rect5 != null) {
            float f10 = rect5.bottom;
            rect5.nextBottom = (f10 + f8) / 2.0f;
            rect4.prevTop = (f10 + f8) / 2.0f;
        }
        this.rectangles.add(rect4);
        this.lastRect = rect4;
    }
}