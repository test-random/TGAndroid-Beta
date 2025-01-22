package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.text.TextPaint;
import android.view.View;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class FlickerLoadingView extends View implements Theme.Colorable {
    private Paint backgroundPaint;
    private int color0;
    private int color1;
    private int colorKey1;
    private int colorKey2;
    private int colorKey3;
    FlickerLoadingView globalGradientView;
    private LinearGradient gradient;
    private int gradientWidth;
    private Paint headerPaint;
    private boolean ignoreHeightCheck;
    private boolean isSingleCell;
    private int itemsCount;
    private long lastUpdateTime;
    private Matrix matrix;
    private float memberRequestButtonWidth;
    private int paddingLeft;
    private int paddingTop;
    private Paint paint;
    private int parentHeight;
    private int parentWidth;
    private float parentXOffset;
    float[] randomParams;
    private RectF rectF;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean showDate;
    private int skipDrawItemsCount;
    private int totalTranslation;
    private boolean useHeaderOffset;
    private int viewType;

    public FlickerLoadingView(Context context) {
        this(context, null);
    }

    public FlickerLoadingView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.paint = new Paint();
        this.headerPaint = new Paint();
        this.rectF = new RectF();
        this.showDate = true;
        this.colorKey1 = Theme.key_actionBarDefaultSubmenuBackground;
        this.colorKey2 = Theme.key_listSelector;
        this.colorKey3 = -1;
        this.itemsCount = 1;
        this.resourcesProvider = resourcesProvider;
        this.matrix = new Matrix();
    }

    private float checkRtl(float f) {
        return LocaleController.isRTL ? getMeasuredWidth() - f : f;
    }

    private void checkRtl(RectF rectF) {
        if (LocaleController.isRTL) {
            rectF.left = getMeasuredWidth() - rectF.left;
            rectF.right = getMeasuredWidth() - rectF.right;
        }
    }

    private int getCellHeight(int i) {
        switch (getViewType()) {
            case 1:
                return AndroidUtilities.dp(78.0f) + 1;
            case 2:
                return ((i - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount()) + AndroidUtilities.dp(2.0f);
            case 3:
            case 4:
                return AndroidUtilities.dp(56.0f);
            case 5:
                return AndroidUtilities.dp(80.0f);
            case 6:
            case 18:
                return AndroidUtilities.dp(64.0f);
            case 7:
                return AndroidUtilities.dp((SharedConfig.useThreeLinesLayout ? 78 : 72) + 1);
            case 8:
                return AndroidUtilities.dp(61.0f);
            case 9:
                return AndroidUtilities.dp(66.0f);
            case 10:
                return AndroidUtilities.dp(58.0f);
            case 11:
                return AndroidUtilities.dp(36.0f);
            case 12:
                return AndroidUtilities.dp(103.0f);
            case 13:
            case 14:
            case 17:
            case 20:
            case 27:
            default:
                return 0;
            case 15:
                return AndroidUtilities.dp(107.0f);
            case 16:
            case 23:
                return AndroidUtilities.dp(50.0f);
            case 19:
                return AndroidUtilities.dp(58.0f);
            case 21:
                return AndroidUtilities.dp(58.0f);
            case 22:
                return AndroidUtilities.dp(60.0f);
            case 24:
                return AndroidUtilities.dp((SharedConfig.useThreeLinesLayout ? 76 : 64) + 1);
            case 25:
                return AndroidUtilities.dp(51.0f);
            case 26:
                return AndroidUtilities.dp(50.0f) + 1;
            case 28:
                return AndroidUtilities.dp(58.0f);
            case 29:
                return AndroidUtilities.dp(60.0f) + 1;
            case 30:
                return AndroidUtilities.dp(32.0f);
            case 31:
                return AndroidUtilities.dp(48.0f) + 1;
            case 32:
                return AndroidUtilities.dp(56.0f) + 1;
            case 33:
                return AndroidUtilities.dp(58.0f);
            case 34:
                return AndroidUtilities.dp(140.0f);
            case 35:
                return AndroidUtilities.dp(112.0f);
        }
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public int getAdditionalHeight() {
        return 0;
    }

    public int getColumnsCount() {
        return 2;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public int getViewType() {
        return this.viewType;
    }

    @Override
    public void onDraw(Canvas canvas) {
        RectF rectF;
        float dp;
        int dp2;
        Paint paint = this.paint;
        if (this.globalGradientView != null) {
            if (getParent() != null) {
                View view = (View) getParent();
                this.globalGradientView.setParentSize(view.getMeasuredWidth(), view.getMeasuredHeight(), -getX());
            }
            paint = this.globalGradientView.paint;
        }
        Paint paint2 = paint;
        if (getViewType() == 34 || getViewType() == 35) {
            this.parentXOffset = -getX();
        }
        updateColors();
        updateGradient();
        int i = this.paddingTop;
        float f = 32.0f;
        if (this.useHeaderOffset) {
            int dp3 = i + AndroidUtilities.dp(32.0f);
            int i2 = this.colorKey3;
            if (i2 >= 0) {
                this.headerPaint.setColor(getThemedColor(i2));
            }
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(32.0f), this.colorKey3 >= 0 ? this.headerPaint : paint2);
            i = dp3;
        }
        float f2 = 10.0f;
        float f3 = 28.0f;
        int i3 = 0;
        int i4 = 1;
        if (getViewType() == 7) {
            while (i <= getMeasuredHeight()) {
                int cellHeight = getCellHeight(getMeasuredWidth());
                canvas.drawCircle(checkRtl(AndroidUtilities.dp(f2) + r3), (cellHeight >> 1) + i, AndroidUtilities.dp(f3), paint2);
                this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(16.0f) + i, AndroidUtilities.dp(148.0f), i + AndroidUtilities.dp(24.0f));
                checkRtl(this.rectF);
                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(38.0f) + i, AndroidUtilities.dp(268.0f), AndroidUtilities.dp(46.0f) + i);
                checkRtl(this.rectF);
                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(54.0f) + i, AndroidUtilities.dp(220.0f), AndroidUtilities.dp(62.0f) + i);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                }
                if (this.showDate) {
                    this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(16.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(24.0f) + i);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                }
                i += getCellHeight(getMeasuredWidth());
                int i5 = i3 + 1;
                if (this.isSingleCell && i5 >= this.itemsCount) {
                    break;
                }
                i3 = i5;
                f2 = 10.0f;
                f3 = 28.0f;
            }
        } else {
            float f4 = 14.0f;
            if (getViewType() == 24) {
                while (i <= getMeasuredHeight()) {
                    canvas.drawCircle(checkRtl(AndroidUtilities.dp(10.0f) + r2), AndroidUtilities.dp(10.0f) + i + r2, AndroidUtilities.dp(f4), paint2);
                    canvas.save();
                    canvas.translate(0.0f, -AndroidUtilities.dp(4.0f));
                    this.rectF.set(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(16.0f) + i, AndroidUtilities.dp(148.0f), i + AndroidUtilities.dp(24.0f));
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                    this.rectF.set(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(38.0f) + i, AndroidUtilities.dp(268.0f), AndroidUtilities.dp(46.0f) + i);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                    if (SharedConfig.useThreeLinesLayout) {
                        this.rectF.set(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(54.0f) + i, AndroidUtilities.dp(220.0f), AndroidUtilities.dp(62.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                    }
                    if (this.showDate) {
                        this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(16.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(24.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                    }
                    canvas.restore();
                    i += getCellHeight(getMeasuredWidth());
                    int i6 = i3 + 1;
                    if (this.isSingleCell && i6 >= this.itemsCount) {
                        break;
                    }
                    i3 = i6;
                    f4 = 14.0f;
                }
            } else {
                float f5 = 42.0f;
                if (getViewType() == 18) {
                    int i7 = i;
                    while (i7 <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r1), AndroidUtilities.dp(32.0f) + i7, AndroidUtilities.dp(25.0f), paint2);
                        int i8 = i3 % 2 == 0 ? 52 : 72;
                        float f6 = 76;
                        this.rectF.set(AndroidUtilities.dp(f6), AndroidUtilities.dp(20.0f) + i7, AndroidUtilities.dp(i8 + 76), AndroidUtilities.dp(28.0f) + i7);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(i8 + 84), AndroidUtilities.dp(20.0f) + i7, AndroidUtilities.dp(i8 + 168), AndroidUtilities.dp(28.0f) + i7);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(f6), AndroidUtilities.dp(42.0f) + i7, AndroidUtilities.dp(140), AndroidUtilities.dp(50.0f) + i7);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        canvas.drawLine(AndroidUtilities.dp(f6), getCellHeight(getMeasuredWidth()) + i7, getMeasuredWidth(), getCellHeight(getMeasuredWidth()) + i7, paint2);
                        i7 += getCellHeight(getMeasuredWidth());
                        int i9 = i3 + 1;
                        if (this.isSingleCell && i9 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i9;
                        }
                    }
                } else if (getViewType() == 19) {
                    int i10 = i;
                    while (i10 <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r1), AndroidUtilities.dp(29.0f) + i10, AndroidUtilities.dp(20.0f), paint2);
                        float f7 = 76;
                        this.rectF.set(AndroidUtilities.dp(f7), AndroidUtilities.dp(16.0f) + i10, AndroidUtilities.dp((i3 % 2 == 0 ? 92 : 128) + 76), AndroidUtilities.dp(24.0f) + i10);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(f7), AndroidUtilities.dp(38.0f) + i10, AndroidUtilities.dp(240), AndroidUtilities.dp(46.0f) + i10);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        canvas.drawLine(AndroidUtilities.dp(f7), getCellHeight(getMeasuredWidth()) + i10, getMeasuredWidth(), getCellHeight(getMeasuredWidth()) + i10, paint2);
                        i10 += getCellHeight(getMeasuredWidth());
                        int i11 = i3 + 1;
                        if (this.isSingleCell && i11 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i11;
                        }
                    }
                } else if (getViewType() == 1) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(AndroidUtilities.dp(9.0f) + r2), (AndroidUtilities.dp(78.0f) >> 1) + i, AndroidUtilities.dp(25.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(20.0f) + i, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(28.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(42.0f) + i, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(50.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(28.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i12 = i3 + 1;
                        if (this.isSingleCell && i12 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i12;
                        }
                    }
                } else if (getViewType() == 2 || getViewType() == 27) {
                    int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
                    int i13 = getViewType() == 27 ? (int) (measuredWidth * 1.25f) : measuredWidth;
                    int i14 = i;
                    int i15 = 0;
                    while (true) {
                        if (i14 >= getMeasuredHeight() && !this.isSingleCell) {
                            break;
                        }
                        for (int i16 = 0; i16 < getColumnsCount(); i16++) {
                            if (i15 != 0 || i16 >= this.skipDrawItemsCount) {
                                canvas.drawRect((AndroidUtilities.dp(2.0f) + measuredWidth) * i16, i14, r1 + measuredWidth, i14 + i13, paint2);
                            }
                        }
                        i14 += AndroidUtilities.dp(2.0f) + i13;
                        i15++;
                        if (this.isSingleCell && i15 >= 2) {
                            break;
                        }
                    }
                } else if (getViewType() == 3) {
                    while (i <= getMeasuredHeight()) {
                        this.rectF.set(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f) + i, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(48.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(12.0f) + i, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(20.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(34.0f) + i, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(42.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(12.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i17 = i3 + 1;
                        if (this.isSingleCell && i17 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i17;
                        }
                    }
                } else if (getViewType() == 4) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(AndroidUtilities.dp(12.0f) + r2), AndroidUtilities.dp(6.0f) + i + r2, AndroidUtilities.dp(44.0f) >> 1, paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(12.0f) + i, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(20.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(34.0f) + i, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(42.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(12.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i18 = i3 + 1;
                        if (this.isSingleCell && i18 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i18;
                        }
                    }
                } else if (getViewType() == 5) {
                    while (i <= getMeasuredHeight()) {
                        this.rectF.set(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(11.0f) + i, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(63.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(12.0f) + i, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(20.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(34.0f) + i, AndroidUtilities.dp(268.0f), AndroidUtilities.dp(42.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(54.0f) + i, AndroidUtilities.dp(188.0f), AndroidUtilities.dp(62.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(12.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i19 = i3 + 1;
                        if (this.isSingleCell && i19 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i19;
                        }
                    }
                } else if (getViewType() == 6 || getViewType() == 10) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r2), (AndroidUtilities.dp(64.0f) >> 1) + i, AndroidUtilities.dp(23.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(17.0f) + i, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(25.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(39.0f) + i, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(47.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(28.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i20 = i3 + 1;
                        if (this.isSingleCell && i20 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i20;
                        }
                    }
                } else if (getViewType() == 29) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r2), (AndroidUtilities.dp(64.0f) >> 1) + i, AndroidUtilities.dp(23.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(17.0f) + i, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(25.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(39.0f) + i, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(47.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        i += getCellHeight(getMeasuredWidth());
                        int i21 = i3 + 1;
                        if (this.isSingleCell && i21 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i21;
                        }
                    }
                } else if (getViewType() == 33) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(13.0f) + r2), (AndroidUtilities.dp(58.0f) >> 1) + i, AndroidUtilities.dp(23.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(72.0f), AndroidUtilities.dp(17.0f) + i, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(25.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(72.0f), AndroidUtilities.dp(39.0f) + i, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(47.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        i += getCellHeight(getMeasuredWidth());
                        int i22 = i3 + 1;
                        if (this.isSingleCell && i22 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i22;
                        }
                    }
                } else if (getViewType() == 30) {
                    while (i <= getMeasuredHeight()) {
                        i += getCellHeight(getMeasuredWidth());
                        this.rectF.set(0.0f, i, getMeasuredWidth(), i);
                        checkRtl(this.rectF);
                        canvas.drawRect(this.rectF, paint2);
                        int i23 = i3 + 1;
                        if (this.isSingleCell && i23 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i23;
                        }
                    }
                } else if (getViewType() == 8) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(11.0f) + r2), (AndroidUtilities.dp(64.0f) >> 1) + i, AndroidUtilities.dp(23.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(17.0f) + i, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(25.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(39.0f) + i, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(47.0f) + i);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(28.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i24 = i3 + 1;
                        if (this.isSingleCell && i24 >= this.itemsCount) {
                            break;
                        } else {
                            i3 = i24;
                        }
                    }
                } else if (getViewType() == 9) {
                    while (i <= getMeasuredHeight()) {
                        canvas.drawCircle(checkRtl(AndroidUtilities.dp(35.0f)), (getCellHeight(getMeasuredWidth()) >> 1) + i, AndroidUtilities.dp(f) / 2, paint2);
                        this.rectF.set(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(16.0f) + i, AndroidUtilities.dp(268.0f), i + AndroidUtilities.dp(24.0f));
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(38.0f) + i, AndroidUtilities.dp(140.0f), i + AndroidUtilities.dp(46.0f));
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(16.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(24.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        int i25 = i3 + 1;
                        if (this.isSingleCell && i25 >= this.itemsCount) {
                            break;
                        }
                        i3 = i25;
                        f = 32.0f;
                    }
                } else if (getViewType() == 11) {
                    int i26 = 0;
                    while (i <= getMeasuredHeight()) {
                        this.rectF.set(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), (getMeasuredWidth() * 0.5f) + AndroidUtilities.dp(this.randomParams[0] * 40.0f), AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f));
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), (getMeasuredWidth() - (getMeasuredWidth() * 0.2f)) - AndroidUtilities.dp(this.randomParams[0] * 20.0f), AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f));
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        i += getCellHeight(getMeasuredWidth());
                        i26++;
                        if (this.isSingleCell && i26 >= this.itemsCount) {
                            break;
                        }
                    }
                } else if (getViewType() == 12) {
                    int dp4 = i + AndroidUtilities.dp(14.0f);
                    while (dp4 <= getMeasuredHeight()) {
                        int measuredWidth2 = getMeasuredWidth() / 4;
                        for (int i27 = 0; i27 < 4; i27++) {
                            float f8 = (measuredWidth2 * i27) + (measuredWidth2 / 2.0f);
                            canvas.drawCircle(f8, AndroidUtilities.dp(7.0f) + dp4 + (AndroidUtilities.dp(56.0f) / 2.0f), AndroidUtilities.dp(28.0f), paint2);
                            float dp5 = AndroidUtilities.dp(7.0f) + dp4 + AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(16.0f);
                            RectF rectF2 = AndroidUtilities.rectTmp;
                            rectF2.set(f8 - AndroidUtilities.dp(24.0f), dp5 - AndroidUtilities.dp(4.0f), f8 + AndroidUtilities.dp(24.0f), dp5 + AndroidUtilities.dp(4.0f));
                            canvas.drawRoundRect(rectF2, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                        }
                        dp4 += getCellHeight(getMeasuredWidth());
                        if (this.isSingleCell) {
                            break;
                        }
                    }
                } else if (getViewType() == 13) {
                    float measuredHeight = getMeasuredHeight() / 2.0f;
                    RectF rectF3 = AndroidUtilities.rectTmp;
                    rectF3.set(AndroidUtilities.dp(40.0f), measuredHeight - AndroidUtilities.dp(4.0f), getMeasuredWidth() - AndroidUtilities.dp(120.0f), AndroidUtilities.dp(4.0f) + measuredHeight);
                    canvas.drawRoundRect(rectF3, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                    if (this.backgroundPaint == null) {
                        Paint paint3 = new Paint(1);
                        this.backgroundPaint = paint3;
                        paint3.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
                    }
                    for (int i28 = 0; i28 < 3; i28++) {
                        canvas.drawCircle((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i28), measuredHeight, AndroidUtilities.dp(13.0f), this.backgroundPaint);
                        canvas.drawCircle((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i28), measuredHeight, AndroidUtilities.dp(12.0f), paint2);
                    }
                } else {
                    float f9 = 21.0f;
                    if (getViewType() == 14 || getViewType() == 17) {
                        int dp6 = AndroidUtilities.dp(12.0f);
                        int dp7 = AndroidUtilities.dp(77.0f);
                        int dp8 = AndroidUtilities.dp(4.0f);
                        float dp9 = AndroidUtilities.dp(21.0f);
                        float dp10 = AndroidUtilities.dp(41.0f);
                        while (dp6 < getMeasuredWidth()) {
                            if (this.backgroundPaint == null) {
                                this.backgroundPaint = new Paint(i4);
                            }
                            this.backgroundPaint.setColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
                            RectF rectF4 = AndroidUtilities.rectTmp;
                            int i29 = dp6 + dp7;
                            rectF4.set(AndroidUtilities.dp(4.0f) + dp6, AndroidUtilities.dp(4.0f), i29 - AndroidUtilities.dp(4.0f), getMeasuredHeight() - AndroidUtilities.dp(4.0f));
                            canvas.drawRoundRect(rectF4, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint2);
                            if (getViewType() == 14) {
                                float dp11 = AndroidUtilities.dp(8.0f) + dp8;
                                float f10 = dp6;
                                float dp12 = AndroidUtilities.dp(22.0f) + dp8 + f10;
                                this.rectF.set(dp12, dp11, dp12 + dp10, dp11 + dp9);
                                RectF rectF5 = this.rectF;
                                canvas.drawRoundRect(rectF5, rectF5.height() * 0.5f, this.rectF.height() * 0.5f, this.backgroundPaint);
                                float dp13 = dp11 + AndroidUtilities.dp(4.0f) + dp9;
                                float dp14 = f10 + AndroidUtilities.dp(5.0f) + dp8;
                                this.rectF.set(dp14, dp13, dp14 + dp10, dp13 + dp9);
                                RectF rectF6 = this.rectF;
                                canvas.drawRoundRect(rectF6, rectF6.height() * 0.5f, this.rectF.height() * 0.5f, this.backgroundPaint);
                            } else if (getViewType() == 17) {
                                float dp15 = AndroidUtilities.dp(5.0f);
                                float dp16 = AndroidUtilities.dp(32.0f);
                                float f11 = dp6 + ((dp7 - dp16) / 2.0f);
                                rectF4.set(f11, AndroidUtilities.dp(f9), dp16 + f11, r15 + AndroidUtilities.dp(32.0f));
                                canvas.drawRoundRect(rectF4, dp15, dp15, this.backgroundPaint);
                                canvas.drawCircle(dp6 + (dp7 / 2), getMeasuredHeight() - AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), this.backgroundPaint);
                                dp6 = i29;
                                i4 = 1;
                                f9 = 21.0f;
                            }
                            canvas.drawCircle(dp6 + (dp7 / 2), getMeasuredHeight() - AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), this.backgroundPaint);
                            dp6 = i29;
                            i4 = 1;
                            f9 = 21.0f;
                        }
                    } else if (getViewType() == 15) {
                        int dp17 = AndroidUtilities.dp(23.0f);
                        int dp18 = AndroidUtilities.dp(4.0f);
                        while (i <= getMeasuredHeight()) {
                            canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(12.0f) + dp17), AndroidUtilities.dp(8.0f) + i + dp17, dp17, paint2);
                            this.rectF.set(this.paddingLeft + AndroidUtilities.dp(74.0f), AndroidUtilities.dp(12.0f) + i, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(20.0f) + i);
                            checkRtl(this.rectF);
                            float f12 = dp18;
                            canvas.drawRoundRect(this.rectF, f12, f12, paint2);
                            this.rectF.set(this.paddingLeft + AndroidUtilities.dp(74.0f), AndroidUtilities.dp(36.0f) + i, this.paddingLeft + AndroidUtilities.dp(140.0f), i + AndroidUtilities.dp(f5));
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, f12, f12, paint2);
                            if (this.memberRequestButtonWidth > 0.0f) {
                                this.rectF.set(this.paddingLeft + AndroidUtilities.dp(73.0f), AndroidUtilities.dp(62.0f) + i, this.paddingLeft + AndroidUtilities.dp(73.0f) + this.memberRequestButtonWidth, AndroidUtilities.dp(94.0f) + i);
                                checkRtl(this.rectF);
                                canvas.drawRoundRect(this.rectF, f12, f12, paint2);
                            }
                            i += getCellHeight(getMeasuredWidth());
                            int i30 = i3 + 1;
                            if (this.isSingleCell && i30 >= this.itemsCount) {
                                break;
                            }
                            i3 = i30;
                            f5 = 42.0f;
                        }
                    } else {
                        if (getViewType() == 16 || getViewType() == 23) {
                            int i31 = 0;
                            while (i <= getMeasuredHeight()) {
                                canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(8.0f) + r3), AndroidUtilities.dp(24.0f) + i, AndroidUtilities.dp(18.0f), paint2);
                                this.rectF.set(this.paddingLeft + AndroidUtilities.dp(58.0f), AndroidUtilities.dp(20.0f) + i, getWidth() - AndroidUtilities.dp(53.0f), AndroidUtilities.dp(28.0f) + i);
                                checkRtl(this.rectF);
                                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), paint2);
                                if (i31 < 4) {
                                    canvas.drawCircle(checkRtl((getWidth() - AndroidUtilities.dp(12.0f)) - r3), AndroidUtilities.dp(24.0f) + i, AndroidUtilities.dp(12.0f), paint2);
                                }
                                i += getCellHeight(getMeasuredWidth());
                                i31++;
                                if (this.isSingleCell && i31 >= this.itemsCount) {
                                    break;
                                }
                            }
                            this.rectF.set(this.paddingLeft + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f) + i, getWidth() - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(28.0f) + i);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), paint2);
                            this.rectF.set(this.paddingLeft + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(36.0f) + i, getWidth() - AndroidUtilities.dp(53.0f), i + AndroidUtilities.dp(44.0f));
                            checkRtl(this.rectF);
                            rectF = this.rectF;
                            dp = AndroidUtilities.dp(8.0f);
                            dp2 = AndroidUtilities.dp(8.0f);
                        } else {
                            int i32 = this.viewType;
                            if (i32 == 21) {
                                while (i <= getMeasuredHeight()) {
                                    canvas.drawCircle(checkRtl(AndroidUtilities.dp(20.0f) + r2), (AndroidUtilities.dp(58.0f) >> 1) + i, AndroidUtilities.dp(46.0f) >> 1, paint2);
                                    this.rectF.set(AndroidUtilities.dp(74.0f), AndroidUtilities.dp(16.0f) + i, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(24.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    this.rectF.set(AndroidUtilities.dp(74.0f), AndroidUtilities.dp(38.0f) + i, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(46.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    i += getCellHeight(getMeasuredWidth());
                                    int i33 = i3 + 1;
                                    if (this.isSingleCell && i33 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i33;
                                    }
                                }
                            } else if (i32 == 22) {
                                while (i <= getMeasuredHeight()) {
                                    canvas.drawCircle(checkRtl(AndroidUtilities.dp(20.0f) + r2), AndroidUtilities.dp(6.0f) + i + r2, AndroidUtilities.dp(48.0f) >> 1, paint2);
                                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(16.0f) + i, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(24.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(38.0f) + i, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(46.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    i += getCellHeight(getMeasuredWidth());
                                    int i34 = i3 + 1;
                                    if (this.isSingleCell && i34 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i34;
                                    }
                                }
                            } else if (i32 == 25) {
                                while (i <= getMeasuredHeight()) {
                                    canvas.drawCircle(AndroidUtilities.dp(17.0f) + r2, AndroidUtilities.dp(6.0f) + i + r2, AndroidUtilities.dp(38.0f) >> 1, paint2);
                                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(21.0f) + i, AndroidUtilities.dp(220.0f), AndroidUtilities.dp(29.0f) + i);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    i += getCellHeight(getMeasuredWidth());
                                    int i35 = i3 + 1;
                                    if (this.isSingleCell && i35 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i35;
                                    }
                                }
                            } else if (i32 == 26) {
                                while (i <= getMeasuredHeight()) {
                                    canvas.drawCircle(LocaleController.isRTL ? (getMeasuredWidth() - AndroidUtilities.dp(21.0f)) - r2 : AndroidUtilities.dp(21.0f) + r2, AndroidUtilities.dp(16.0f) + i + r2, AndroidUtilities.dp(21.0f) >> 1, paint2);
                                    this.rectF.set(AndroidUtilities.dp(60.0f), AndroidUtilities.dp(21.0f) + i, AndroidUtilities.dp(190.0f), AndroidUtilities.dp(29.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(16.0f), AndroidUtilities.dp(21.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(62.0f), AndroidUtilities.dp(29.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    i += getCellHeight(getMeasuredWidth());
                                    int i36 = i3 + 1;
                                    if (this.isSingleCell && i36 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i36;
                                    }
                                }
                            } else if (getViewType() == 28) {
                                while (i <= getMeasuredHeight()) {
                                    canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(10.0f) + r2), (AndroidUtilities.dp(58.0f) >> 1) + i, AndroidUtilities.dp(24.0f), paint2);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(17.0f) + i, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(25.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(39.0f) + i, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(47.0f) + i);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    if (this.showDate) {
                                        this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + i, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(28.0f) + i);
                                        checkRtl(this.rectF);
                                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    }
                                    i += getCellHeight(getMeasuredWidth());
                                    int i37 = i3 + 1;
                                    if (this.isSingleCell && i37 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i37;
                                    }
                                }
                            } else if (getViewType() == 31) {
                                while (i <= getMeasuredHeight()) {
                                    int cellHeight2 = getCellHeight(getMeasuredWidth());
                                    float f13 = i;
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(18.0f), ((cellHeight2 - AndroidUtilities.dp(22.0f)) / 2.0f) + f13, this.paddingLeft + AndroidUtilities.dp(40.0f), ((AndroidUtilities.dp(22.0f) + cellHeight2) / 2.0f) + f13);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), paint2);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(58.0f), ((cellHeight2 - AndroidUtilities.dp(8.0f)) / 2.0f) + f13, Math.min(this.paddingLeft + AndroidUtilities.dp(132.0f), getMeasuredWidth() - AndroidUtilities.dp(19.0f)), f13 + ((AndroidUtilities.dp(8.0f) + cellHeight2) / 2.0f));
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    i += cellHeight2;
                                    int i38 = i3 + 1;
                                    if (this.isSingleCell && i38 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i38;
                                    }
                                }
                            } else if (getViewType() == 32) {
                                while (i <= getMeasuredHeight()) {
                                    int cellHeight3 = getCellHeight(getMeasuredWidth());
                                    float f14 = i;
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(10.0f), ((cellHeight3 - AndroidUtilities.dp(32.0f)) / 2.0f) + f14, this.paddingLeft + AndroidUtilities.dp(42.0f), ((AndroidUtilities.dp(32.0f) + cellHeight3) / 2.0f) + f14);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint2);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(64.0f), (((cellHeight3 - AndroidUtilities.dp(14.0f)) - AndroidUtilities.dp(10.0f)) / 2.0f) + f14, Math.min(this.paddingLeft + AndroidUtilities.dp(118.0f), getMeasuredWidth() - AndroidUtilities.dp(19.0f)), (((cellHeight3 - AndroidUtilities.dp(14.0f)) + AndroidUtilities.dp(10.0f)) / 2.0f) + f14);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(64.0f), (((AndroidUtilities.dp(14.0f) + cellHeight3) - AndroidUtilities.dp(8.0f)) / 2.0f) + f14, Math.min(this.paddingLeft + AndroidUtilities.dp(144.0f), getMeasuredWidth() - AndroidUtilities.dp(19.0f)), f14 + (((AndroidUtilities.dp(14.0f) + cellHeight3) + AndroidUtilities.dp(8.0f)) / 2.0f));
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint2);
                                    i += cellHeight3;
                                    int i39 = i3 + 1;
                                    if (this.isSingleCell && i39 >= this.itemsCount) {
                                        break;
                                    } else {
                                        i3 = i39;
                                    }
                                }
                            } else if (getViewType() == 34 || getViewType() == 35) {
                                this.rectF.set(this.paddingLeft, this.paddingTop, getMeasuredWidth() - this.paddingLeft, getMeasuredHeight() - this.paddingTop);
                                this.rectF.inset(AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f));
                                rectF = this.rectF;
                                dp = AndroidUtilities.dp(11.0f);
                                dp2 = AndroidUtilities.dp(11.0f);
                            }
                        }
                        canvas.drawRoundRect(rectF, dp, dp2, paint2);
                    }
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        if (this.isSingleCell) {
            int i3 = this.itemsCount;
            i2 = View.MeasureSpec.makeMeasureSpec(((i3 <= 1 || !this.ignoreHeightCheck) ? (i3 <= 1 || View.MeasureSpec.getSize(i2) <= 0) ? getCellHeight(View.MeasureSpec.getSize(i)) : Math.min(View.MeasureSpec.getSize(i2), getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount) : getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount) + getAdditionalHeight(), 1073741824);
        }
        super.onMeasure(i, i2);
    }

    public void setColors(int i, int i2, int i3) {
        this.colorKey1 = i;
        this.colorKey2 = i2;
        this.colorKey3 = i3;
        invalidate();
    }

    public void setGlobalGradientView(FlickerLoadingView flickerLoadingView) {
        this.globalGradientView = flickerLoadingView;
    }

    public void setIgnoreHeightCheck(boolean z) {
        this.ignoreHeightCheck = z;
    }

    public void setIsSingleCell(boolean z) {
        this.isSingleCell = z;
    }

    public void setItemsCount(int i) {
        this.itemsCount = i;
    }

    public void setMemberRequestButton(boolean z) {
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTypeface(AndroidUtilities.bold());
        textPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.memberRequestButtonWidth = AndroidUtilities.dp(34.0f) + textPaint.measureText(LocaleController.getString(z ? R.string.AddToChannel : R.string.AddToGroup));
    }

    public void setPaddingLeft(int i) {
        this.paddingLeft = i;
        invalidate();
    }

    public void setPaddingTop(int i) {
        this.paddingTop = i;
        invalidate();
    }

    public void setParentSize(int i, int i2, float f) {
        this.parentWidth = i;
        this.parentHeight = i2;
        this.parentXOffset = f;
    }

    public void setUseHeaderOffset(boolean z) {
        this.useHeaderOffset = z;
    }

    public void setViewType(int i) {
        this.viewType = i;
        if (i == 11) {
            Random random = new Random();
            this.randomParams = new float[2];
            for (int i2 = 0; i2 < 2; i2++) {
                this.randomParams[i2] = Math.abs(random.nextInt() % 1000) / 1000.0f;
            }
        }
        invalidate();
    }

    public void showDate(boolean z) {
        this.showDate = z;
    }

    public void skipDrawItemsCount(int i) {
        this.skipDrawItemsCount = i;
    }

    @Override
    public void updateColors() {
        int i;
        int i2;
        FlickerLoadingView flickerLoadingView = this.globalGradientView;
        if (flickerLoadingView != null) {
            flickerLoadingView.updateColors();
            return;
        }
        int themedColor = getThemedColor(this.colorKey1);
        int themedColor2 = getThemedColor(this.colorKey2);
        if (this.color1 == themedColor2 && this.color0 == themedColor) {
            return;
        }
        this.color0 = themedColor;
        this.color1 = themedColor2;
        int i3 = this.viewType;
        if (i3 == 34 || i3 == 35) {
            i = AndroidUtilities.displaySize.x;
        } else {
            i = AndroidUtilities.dp((this.isSingleCell || i3 == 13 || i3 == 14 || i3 == 17) ? 200.0f : 600.0f);
        }
        this.gradientWidth = i;
        this.gradient = (this.isSingleCell || (i2 = this.viewType) == 13 || i2 == 14 || i2 == 17) ? new LinearGradient(0.0f, 0.0f, this.gradientWidth, 0.0f, new int[]{themedColor2, themedColor, themedColor, themedColor2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP) : new LinearGradient(0.0f, 0.0f, 0.0f, this.gradientWidth, new int[]{themedColor2, themedColor, themedColor, themedColor2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradient);
    }

    public void updateGradient() {
        Matrix matrix;
        float f;
        float f2;
        FlickerLoadingView flickerLoadingView = this.globalGradientView;
        if (flickerLoadingView != null) {
            flickerLoadingView.updateGradient();
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
        if (abs > 17) {
            abs = 16;
        }
        if (abs < 4) {
            abs = 0;
        }
        int i = this.parentWidth;
        if (i == 0) {
            i = getMeasuredWidth();
        }
        int i2 = this.viewType;
        if (i2 == 34 || i2 == 35) {
            i = Math.max(i, AndroidUtilities.displaySize.x);
        }
        int i3 = this.parentHeight;
        if (i3 == 0) {
            i3 = getMeasuredHeight();
        }
        this.lastUpdateTime = elapsedRealtime;
        if (this.isSingleCell || this.viewType == 13 || getViewType() == 14 || getViewType() == 17) {
            int i4 = (int) (this.totalTranslation + (((float) (abs * i)) / 400.0f));
            this.totalTranslation = i4;
            if (i4 >= i * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            matrix = this.matrix;
            f = this.totalTranslation + this.parentXOffset;
            f2 = 0.0f;
        } else {
            int i5 = (int) (this.totalTranslation + (((float) (abs * i3)) / 400.0f));
            this.totalTranslation = i5;
            if (i5 >= i3 * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            matrix = this.matrix;
            f = this.parentXOffset;
            f2 = this.totalTranslation;
        }
        matrix.setTranslate(f, f2);
        LinearGradient linearGradient = this.gradient;
        if (linearGradient != null) {
            linearGradient.setLocalMatrix(this.matrix);
        }
    }
}
