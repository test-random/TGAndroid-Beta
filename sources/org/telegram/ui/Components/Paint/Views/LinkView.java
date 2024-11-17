package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.LinkPreview;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;

public class LinkView extends EntityView {
    private int currentColor;
    private int currentType;
    private boolean hasColor;
    public LinkPreview.WebPagePreview link;
    public final LinkPreview marker;
    public TL_stories.MediaArea mediaArea;

    public class TextViewSelectionView extends EntityView.SelectionView {
        private final Paint clearPaint;
        private Path path;

        public TextViewSelectionView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.clearPaint = paint;
            this.path = new Path();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int saveCount = canvas.getSaveCount();
            float showAlpha = getShowAlpha();
            if (showAlpha <= 0.0f) {
                return;
            }
            if (showAlpha < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (showAlpha * 255.0f), 31);
            }
            float dp = AndroidUtilities.dp(2.0f);
            float dpf2 = AndroidUtilities.dpf2(5.66f);
            float dp2 = dp + dpf2 + AndroidUtilities.dp(15.0f);
            float f = dp2 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f;
            float measuredHeight = getMeasuredHeight() - f;
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = dp2 + measuredWidth;
            float f3 = dp2 + measuredHeight;
            rectF.set(dp2, dp2, f2, f3);
            float dp3 = AndroidUtilities.dp(12.0f);
            float min = Math.min(dp3, measuredWidth / 2.0f);
            float f4 = measuredHeight / 2.0f;
            float min2 = Math.min(dp3, f4);
            this.path.rewind();
            float f5 = min * 2.0f;
            float f6 = dp2 + f5;
            float f7 = 2.0f * min2;
            float f8 = dp2 + f7;
            rectF.set(dp2, dp2, f6, f8);
            this.path.arcTo(rectF, 180.0f, 90.0f);
            float f9 = f2 - f5;
            rectF.set(f9, dp2, f2, f8);
            this.path.arcTo(rectF, 270.0f, 90.0f);
            canvas.drawPath(this.path, this.paint);
            this.path.rewind();
            float f10 = f3 - f7;
            rectF.set(dp2, f10, f6, f3);
            this.path.arcTo(rectF, 180.0f, -90.0f);
            rectF.set(f9, f10, f2, f3);
            this.path.arcTo(rectF, 90.0f, -90.0f);
            canvas.drawPath(this.path, this.paint);
            float f11 = dp2 + f4;
            canvas.drawCircle(dp2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(dp2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.drawCircle(f2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(f2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
            float f12 = dp2 + min2;
            float f13 = f3 - min2;
            canvas.drawLine(dp2, f12, dp2, f13, this.paint);
            canvas.drawLine(f2, f12, f2, f13, this.paint);
            canvas.drawCircle(f2, f11, (AndroidUtilities.dp(1.0f) + dpf2) - 1.0f, this.clearPaint);
            canvas.drawCircle(dp2, f11, (dpf2 + AndroidUtilities.dp(1.0f)) - 1.0f, this.clearPaint);
            canvas.restoreToCount(saveCount);
        }

        @Override
        protected int pointInsideHandle(float f, float f2) {
            float dp = AndroidUtilities.dp(1.0f);
            float dp2 = AndroidUtilities.dp(19.5f);
            float f3 = dp + dp2;
            float f4 = f3 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f4;
            float measuredHeight = ((getMeasuredHeight() - f4) / 2.0f) + f3;
            if (f > f3 - dp2 && f2 > measuredHeight - dp2 && f < f3 + dp2 && f2 < measuredHeight + dp2) {
                return 1;
            }
            float f5 = f3 + measuredWidth;
            return (f <= f5 - dp2 || f2 <= measuredHeight - dp2 || f >= f5 + dp2 || f2 >= measuredHeight + dp2) ? 0 : 2;
        }
    }

    public LinkView(Context context, Point point, int i, LinkPreview.WebPagePreview webPagePreview, TL_stories.MediaArea mediaArea, float f, int i2, int i3) {
        super(context, point);
        LinkPreview linkPreview = new LinkPreview(context, f);
        this.marker = linkPreview;
        linkPreview.setMaxWidth(i2);
        setLink(i, webPagePreview, mediaArea);
        this.currentType = i3;
        linkPreview.setType(i3, this.currentColor);
        addView(linkPreview, LayoutHelper.createFrame(-2, -2, 51));
        setClipChildren(false);
        setClipToPadding(false);
        updatePosition();
    }

    @Override
    public TextViewSelectionView createSelectionView() {
        return new TextViewSelectionView(getContext());
    }

    public int getColor() {
        return this.currentColor;
    }

    @Override
    protected float getMaxScale() {
        return 1.5f;
    }

    public int getNextType() {
        int i = this.currentType + 1;
        return i == 4 ? !this.hasColor ? 1 : 0 : i;
    }

    @Override
    public Rect getSelectionBounds() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup == null) {
            return new Rect();
        }
        float scaleX = viewGroup.getScaleX();
        float measuredWidth = (getMeasuredWidth() * getScale()) + (AndroidUtilities.dp(64.0f) / scaleX);
        float measuredHeight = (getMeasuredHeight() * getScale()) + (AndroidUtilities.dp(64.0f) / scaleX);
        float positionX = (getPositionX() - (measuredWidth / 2.0f)) * scaleX;
        return new Rect(positionX, (getPositionY() - (measuredHeight / 2.0f)) * scaleX, ((measuredWidth * scaleX) + positionX) - positionX, measuredHeight * scaleX);
    }

    @Override
    protected float getStickyPaddingBottom() {
        return this.marker.pady;
    }

    @Override
    protected float getStickyPaddingLeft() {
        return this.marker.padx;
    }

    @Override
    protected float getStickyPaddingRight() {
        return this.marker.padx;
    }

    @Override
    protected float getStickyPaddingTop() {
        return this.marker.pady;
    }

    public int getType() {
        return this.currentType;
    }

    public boolean hasColor() {
        return this.hasColor;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updatePosition();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        updatePosition();
    }

    public void setColor(int i) {
        this.hasColor = true;
        this.currentColor = i;
    }

    public void setLink(int i, LinkPreview.WebPagePreview webPagePreview, TL_stories.MediaArea mediaArea) {
        this.link = webPagePreview;
        this.mediaArea = mediaArea;
        this.marker.set(i, webPagePreview);
        updateSelectionView();
    }

    public void setMaxWidth(int i) {
        this.marker.setMaxWidth(i);
    }

    public void setType(int i) {
        LinkPreview linkPreview = this.marker;
        this.currentType = i;
        linkPreview.setType(i, this.currentColor);
    }
}
