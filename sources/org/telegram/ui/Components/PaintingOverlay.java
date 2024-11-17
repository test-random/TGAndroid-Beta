package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;

public class PaintingOverlay extends FrameLayout {
    private Drawable backgroundDrawable;
    public boolean drawChildren;
    private boolean ignoreLayout;
    private HashMap mediaEntityViews;
    private Bitmap paintBitmap;

    public PaintingOverlay(Context context) {
        super(context);
        this.drawChildren = true;
    }

    public static void lambda$setEntities$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        RLottieDrawable lottieAnimation;
        if (!z || z2 || (lottieAnimation = imageReceiver.getLottieAnimation()) == null) {
            return;
        }
        lottieAnimation.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (this.drawChildren) {
            return super.drawChild(canvas, view, j);
        }
        return false;
    }

    public Bitmap getBitmap() {
        return this.paintBitmap;
    }

    public Bitmap getThumb() {
        float measuredWidth = getMeasuredWidth();
        float measuredHeight = getMeasuredHeight();
        float max = Math.max(measuredWidth / AndroidUtilities.dp(120.0f), measuredHeight / AndroidUtilities.dp(120.0f));
        Bitmap createBitmap = Bitmap.createBitmap((int) (measuredWidth / max), (int) (measuredHeight / max), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        float f = 1.0f / max;
        canvas.scale(f, f);
        draw(canvas);
        return createBitmap;
    }

    public void hideBitmap() {
        setBackground(null);
    }

    public void hideEntities() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(4);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        float f;
        if (this.mediaEntityViews != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int childCount = getChildCount();
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt = getChildAt(i7);
                VideoEditedInfo.MediaEntity mediaEntity = (VideoEditedInfo.MediaEntity) this.mediaEntityViews.get(childAt);
                if (mediaEntity != null) {
                    int measuredWidth2 = childAt.getMeasuredWidth();
                    int measuredHeight2 = childAt.getMeasuredHeight();
                    if (childAt instanceof EditTextOutline) {
                        if (mediaEntity.customTextView) {
                            i5 = ((int) (measuredWidth * (mediaEntity.x + (mediaEntity.width / 2.0f)))) - (childAt.getMeasuredWidth() / 2);
                            f = measuredHeight * (mediaEntity.y + (mediaEntity.height / 2.0f));
                        } else {
                            i5 = ((int) (measuredWidth * mediaEntity.textViewX)) - (childAt.getMeasuredWidth() / 2);
                            f = measuredHeight * mediaEntity.textViewY;
                        }
                        i6 = ((int) f) - (childAt.getMeasuredHeight() / 2);
                    } else {
                        i5 = (int) (measuredWidth * mediaEntity.x);
                        i6 = (int) (measuredHeight * mediaEntity.y);
                    }
                    childAt.layout(i5, i6, measuredWidth2 + i5, measuredHeight2 + i6);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        this.ignoreLayout = true;
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        if (this.mediaEntityViews != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                VideoEditedInfo.MediaEntity mediaEntity = (VideoEditedInfo.MediaEntity) this.mediaEntityViews.get(childAt);
                if (mediaEntity != null) {
                    if (childAt instanceof EditTextOutline) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(mediaEntity.viewWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                        float measuredWidth2 = mediaEntity.customTextView ? (mediaEntity.width * getMeasuredWidth()) / mediaEntity.viewWidth : mediaEntity.scale * ((mediaEntity.textViewWidth * measuredWidth) / mediaEntity.viewWidth);
                        childAt.setScaleX(measuredWidth2);
                        childAt.setScaleY(measuredWidth2);
                    } else {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec((int) (measuredWidth * mediaEntity.width), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (measuredHeight * mediaEntity.height), 1073741824));
                    }
                }
            }
        }
        this.ignoreLayout = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void reset() {
        this.paintBitmap = null;
        this.backgroundDrawable = null;
        setBackground(null);
        HashMap hashMap = this.mediaEntityViews;
        if (hashMap != null) {
            hashMap.clear();
        }
        removeAllViews();
    }

    @Override
    public void setAlpha(float f) {
        super.setAlpha(f);
        Drawable drawable = this.backgroundDrawable;
        if (drawable != null) {
            drawable.setAlpha((int) (255.0f * f));
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null && childAt.getParent() == this) {
                childAt.setAlpha(f);
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.paintBitmap = bitmap;
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        this.backgroundDrawable = bitmapDrawable;
        setBackground(bitmapDrawable);
    }

    public void setData(String str, ArrayList arrayList, boolean z, boolean z2, boolean z3) {
        BitmapDrawable bitmapDrawable;
        setEntities(arrayList, z, z2, z3);
        if (str != null) {
            this.paintBitmap = BitmapFactory.decodeFile(str);
            bitmapDrawable = new BitmapDrawable(this.paintBitmap);
        } else {
            bitmapDrawable = null;
            this.paintBitmap = null;
        }
        this.backgroundDrawable = bitmapDrawable;
        setBackground(bitmapDrawable);
    }

    public void setEntities(java.util.ArrayList r24, boolean r25, boolean r26, boolean r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PaintingOverlay.setEntities(java.util.ArrayList, boolean, boolean, boolean):void");
    }

    public void showAll() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(0);
        }
        setBackground(this.backgroundDrawable);
    }
}
