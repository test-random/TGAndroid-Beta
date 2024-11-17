package org.telegram.ui.Components.Forum;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;

public class ForumBubbleDrawable extends Drawable {
    static final SparseArray colorsMap;
    private static SvgHelper.SvgDrawable mainDrawable;
    public static final int[] serverSupportedColor = {7322096, 16766590, 13338331, 9367192, 16749490, 16478047};
    int colorIndex;
    private int[] currentColors;
    LinearGradient gradient;
    private final Paint strokePaint;
    SvgHelper.SvgDrawable svgDrawable;
    private final Paint topPaint;
    Matrix gradientMatrix = new Matrix();
    ArrayList parents = new ArrayList();
    int color = -1;

    static {
        SparseArray sparseArray = new SparseArray();
        colorsMap = sparseArray;
        sparseArray.put(7322096, new int[]{-16687423, -11814913});
        sparseArray.put(16766590, new int[]{-1419264, -9380});
        sparseArray.put(13338331, new int[]{-6014789, -1737985});
        sparseArray.put(9367192, new int[]{-15617007, -6823116});
        sparseArray.put(16749490, new int[]{-1826470, -34407});
        sparseArray.put(16478047, new int[]{-3795707, -36532});
    }

    public ForumBubbleDrawable(int i) {
        if (mainDrawable == null) {
            mainDrawable = SvgHelper.getDrawable(R.raw.topic_bubble, -1);
        }
        SvgHelper.SvgDrawable clone = mainDrawable.clone();
        this.svgDrawable = clone;
        clone.copyCommandFromPosition(0);
        Paint paint = new Paint(1);
        this.topPaint = paint;
        Paint paint2 = new Paint(1);
        this.strokePaint = paint2;
        paint2.setStrokeWidth(AndroidUtilities.dp(1.0f));
        paint2.setStyle(Paint.Style.STROKE);
        this.svgDrawable.setPaint(paint, 1);
        this.svgDrawable.setPaint(paint2, 2);
        setColor(i);
    }

    public void lambda$moveNexColor$0(int[] iArr, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        Paint paint = new Paint(1);
        LinearGradient linearGradient = new LinearGradient(0.0f, 100.0f, 0.0f, 0.0f, new int[]{ColorUtils.blendARGB(iArr[0], this.currentColors[0], floatValue), ColorUtils.blendARGB(iArr[1], this.currentColors[1], floatValue)}, (float[]) null, Shader.TileMode.CLAMP);
        this.gradient = linearGradient;
        linearGradient.setLocalMatrix(this.gradientMatrix);
        paint.setShader(this.gradient);
        this.svgDrawable.setPaint(paint, 0);
        this.topPaint.setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(iArr[1], this.currentColors[1], floatValue), -1, 0.1f));
        this.strokePaint.setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(iArr[0], this.currentColors[0], floatValue), -16777216, 0.1f));
        invalidateSelf();
    }

    public void addParent(View view) {
        this.parents.add(view);
    }

    public int colorDistance(int i, int i2) {
        return Math.abs(Color.red(i) - Color.red(i2)) + Math.abs(Color.green(i) - Color.green(i2)) + Math.abs(Color.blue(i) - Color.blue(i2));
    }

    @Override
    public void draw(Canvas canvas) {
        this.gradientMatrix.reset();
        this.gradientMatrix.setScale(1.0f, getBounds().height() / 100.0f);
        this.gradient.setLocalMatrix(this.gradientMatrix);
        this.svgDrawable.setBounds(getBounds());
        this.svgDrawable.draw(canvas);
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        for (int i = 0; i < this.parents.size(); i++) {
            ((View) this.parents.get(i)).invalidate();
        }
    }

    public int moveNexColor() {
        int i = this.colorIndex + 1;
        this.colorIndex = i;
        int[] iArr = serverSupportedColor;
        if (i > iArr.length - 1) {
            this.colorIndex = 0;
        }
        final int[] iArr2 = this.currentColors;
        int i2 = iArr[this.colorIndex];
        this.color = i2;
        this.currentColors = (int[]) colorsMap.get(i2);
        if (Theme.isCurrentThemeDark()) {
            this.currentColors = new int[]{ColorUtils.blendARGB(this.currentColors[0], -1, 0.2f), ColorUtils.blendARGB(this.currentColors[1], -1, 0.2f)};
        }
        invalidateSelf();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ForumBubbleDrawable.this.lambda$moveNexColor$0(iArr2, valueAnimator);
            }
        });
        ofFloat.setDuration(200L);
        ofFloat.start();
        return iArr[this.colorIndex];
    }

    @Override
    public void setAlpha(int i) {
    }

    public void setColor(int i) {
        int[] iArr;
        int i2 = this.color;
        if (i2 == i && i2 == -1) {
            return;
        }
        this.color = i;
        int colorDistance = colorDistance(serverSupportedColor[0], i);
        this.colorIndex = 0;
        int i3 = 0;
        while (true) {
            iArr = serverSupportedColor;
            if (i3 >= iArr.length) {
                break;
            }
            int colorDistance2 = colorDistance(iArr[i3], i);
            if (colorDistance2 < colorDistance) {
                this.colorIndex = i3;
                colorDistance = colorDistance2;
            }
            i3++;
        }
        int[] iArr2 = (int[]) colorsMap.get(iArr[this.colorIndex]);
        if (Theme.isCurrentThemeDark()) {
            iArr2 = new int[]{ColorUtils.blendARGB(iArr2[0], -1, 0.2f), ColorUtils.blendARGB(iArr2[1], -1, 0.2f)};
        }
        this.currentColors = iArr2;
        Paint paint = new Paint(1);
        LinearGradient linearGradient = new LinearGradient(0.0f, 100.0f, 0.0f, 0.0f, iArr2, (float[]) null, Shader.TileMode.CLAMP);
        this.gradient = linearGradient;
        linearGradient.setLocalMatrix(this.gradientMatrix);
        paint.setShader(this.gradient);
        this.svgDrawable.setPaint(paint, 0);
        this.topPaint.setColor(ColorUtils.blendARGB(iArr2[1], -1, 0.1f));
        this.strokePaint.setColor(ColorUtils.blendARGB(iArr2[0], -16777216, 0.1f));
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
