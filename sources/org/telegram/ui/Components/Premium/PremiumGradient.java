package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class PremiumGradient {
    private static PremiumGradient instance;
    private final PremiumGradientTools goldGradient;
    public InternalDrawable goldenStarMenuDrawable;
    private int lastStarColor;
    Paint lockedPremiumPaint;
    private final PremiumGradientTools mainGradient;
    private final Paint mainGradientPaint;
    public Drawable premiumStarColoredDrawable;
    public Drawable premiumStarDrawableMini;
    public InternalDrawable premiumStarMenuDrawable;
    public InternalDrawable premiumStarMenuDrawable2;

    public static class InternalDrawable extends BitmapDrawable {
        public int[] colors;
        Drawable originDrawable;

        public InternalDrawable(Drawable drawable, Bitmap bitmap, int[] iArr) {
            super(ApplicationLoader.applicationContext.getResources(), bitmap);
            this.originDrawable = drawable;
            int[] iArr2 = new int[iArr.length];
            this.colors = iArr2;
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        }

        @Override
        public void setColorFilter(int i, PorterDuff.Mode mode) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public static class PremiumGradientTools {
        final int colorKey1;
        final int colorKey2;
        final int colorKey3;
        final int colorKey4;
        final int colorKey5;
        final int[] colors;
        public float cx;
        public float cy;
        public boolean darkColors;
        public boolean exactly;
        Matrix matrix;
        public final Paint paint;
        private final Theme.ResourcesProvider resourcesProvider;
        Shader shader;
        public float x1;
        public float x2;
        public float y1;
        public float y2;

        public PremiumGradientTools(int i, int i2, int i3) {
            this(i, i2, i3, -1, -1);
        }

        public PremiumGradientTools(int i, int i2, int i3, int i4) {
            this(i, i2, i3, i4, -1);
        }

        public PremiumGradientTools(int i, int i2, int i3, int i4, int i5) {
            this(i, i2, i3, i4, -1, null);
        }

        public PremiumGradientTools(int i, int i2, int i3, int i4, int i5, Theme.ResourcesProvider resourcesProvider) {
            this.cx = 0.5f;
            this.cy = 0.5f;
            this.matrix = new Matrix();
            this.paint = new Paint(1);
            this.colors = new int[5];
            this.x1 = 0.0f;
            this.y1 = 1.0f;
            this.x2 = 1.5f;
            this.y2 = 0.0f;
            this.resourcesProvider = resourcesProvider;
            this.colorKey1 = i;
            this.colorKey2 = i2;
            this.colorKey3 = i3;
            this.colorKey4 = i4;
            this.colorKey5 = i5;
        }

        public void chekColors() {
            LinearGradient linearGradient;
            int color = getColor(this.colorKey1);
            int color2 = getColor(this.colorKey2);
            int i = this.colorKey3;
            int color3 = i < 0 ? 0 : getColor(i);
            int i2 = this.colorKey4;
            int color4 = i2 < 0 ? 0 : getColor(i2);
            int i3 = this.colorKey5;
            int color5 = i3 < 0 ? 0 : getColor(i3);
            int[] iArr = this.colors;
            if (iArr[0] == color && iArr[1] == color2 && iArr[2] == color3 && iArr[3] == color4 && iArr[4] == color5) {
                return;
            }
            iArr[0] = color;
            iArr[1] = color2;
            iArr[2] = color3;
            iArr[3] = color4;
            iArr[4] = color5;
            if (color3 == 0) {
                float f = this.x1 * 100.0f;
                float f2 = this.y1 * 100.0f;
                float f3 = this.x2 * 100.0f;
                float f4 = this.y2 * 100.0f;
                int[] iArr2 = this.colors;
                this.shader = new LinearGradient(f, f2, f3, f4, new int[]{iArr2[0], iArr2[1]}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            } else if (color4 == 0) {
                float f5 = this.x1 * 100.0f;
                float f6 = this.y1 * 100.0f;
                float f7 = this.x2 * 100.0f;
                float f8 = this.y2 * 100.0f;
                int[] iArr3 = this.colors;
                this.shader = new LinearGradient(f5, f6, f7, f8, new int[]{iArr3[0], iArr3[1], iArr3[2]}, new float[]{0.0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
            } else {
                float f9 = this.x1;
                if (color5 == 0) {
                    float f10 = this.y1 * 100.0f;
                    float f11 = this.x2 * 100.0f;
                    float f12 = this.y2 * 100.0f;
                    int[] iArr4 = this.colors;
                    linearGradient = new LinearGradient(f9 * 100.0f, f10, f11, f12, new int[]{iArr4[0], iArr4[1], iArr4[2], iArr4[3]}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP);
                } else {
                    float f13 = this.y1 * 100.0f;
                    float f14 = this.x2 * 100.0f;
                    float f15 = this.y2 * 100.0f;
                    int[] iArr5 = this.colors;
                    linearGradient = new LinearGradient(f9 * 100.0f, f13, f14, f15, new int[]{iArr5[0], iArr5[1], iArr5[2], iArr5[3], iArr5[4]}, new float[]{0.0f, 0.425f, 0.655f, 0.78f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.shader = linearGradient;
            }
            this.shader.setLocalMatrix(this.matrix);
            this.paint.setShader(this.shader);
        }

        private int getColor(int i) {
            int themeColorByKey = getThemeColorByKey(i);
            if (!this.darkColors) {
                return themeColorByKey;
            }
            return Color.argb(Color.alpha(themeColorByKey), Color.red(themeColorByKey) - 15, Color.green(themeColorByKey) - 15, Color.blue(themeColorByKey) - 15);
        }

        protected int getThemeColorByKey(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        public void gradientMatrix(int i, int i2, int i3, int i4, float f, float f2) {
            chekColors();
            int i5 = i4 - i2;
            if (this.exactly) {
                this.matrix.reset();
                this.matrix.postScale((i3 - i) / 100.0f, i5 / 100.0f, this.cx * 100.0f, this.cy * 100.0f);
                this.matrix.postTranslate(f, f2);
            } else {
                chekColors();
                this.matrix.reset();
                this.matrix.postScale((i3 - i) / 100.0f, (i5 + i5) / 100.0f, 75.0f, 50.0f);
                this.matrix.postTranslate(f, (-r6) + f2);
            }
            this.shader.setLocalMatrix(this.matrix);
        }

        public void gradientMatrix(Rect rect) {
            gradientMatrix(rect.left, rect.top, rect.right, rect.bottom, 0.0f, 0.0f);
        }

        public void gradientMatrix(RectF rectF) {
            gradientMatrix((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom, 0.0f, 0.0f);
        }

        public void gradientMatrixLinear(float f, float f2) {
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(1.0f, f / 100.0f, 0.0f, 0.0f);
            this.matrix.postTranslate(0.0f, f2);
            this.shader.setLocalMatrix(this.matrix);
        }
    }

    private PremiumGradient() {
        PremiumGradientTools premiumGradientTools = new PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, Theme.key_premiumGradient3, Theme.key_premiumGradient4);
        this.mainGradient = premiumGradientTools;
        PremiumGradientTools premiumGradientTools2 = new PremiumGradientTools(Theme.key_starsGradient1, Theme.key_starsGradient2, -1);
        this.goldGradient = premiumGradientTools2;
        this.mainGradientPaint = premiumGradientTools.paint;
        Context context = ApplicationLoader.applicationContext;
        int i = R.drawable.msg_premium_liststar;
        this.premiumStarDrawableMini = ContextCompat.getDrawable(context, i).mutate();
        Context context2 = ApplicationLoader.applicationContext;
        int i2 = R.drawable.msg_settings_premium;
        this.premiumStarMenuDrawable = createGradientDrawable(ContextCompat.getDrawable(context2, i2));
        this.goldenStarMenuDrawable = createGradientDrawable(ContextCompat.getDrawable(ApplicationLoader.applicationContext, i2), premiumGradientTools2);
        this.premiumStarMenuDrawable2 = createGradientDrawable(ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_normal));
        this.premiumStarColoredDrawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
        premiumGradientTools.chekColors();
        checkIconColors();
    }

    private InternalDrawable checkColors(InternalDrawable internalDrawable) {
        int[] iArr = this.mainGradient.colors;
        int i = iArr[0];
        int[] iArr2 = internalDrawable.colors;
        return (i == iArr2[0] && iArr[1] == iArr2[1] && iArr[2] == iArr2[2] && iArr[3] == iArr2[3]) ? internalDrawable : createGradientDrawable(internalDrawable.originDrawable);
    }

    public static PremiumGradient getInstance() {
        if (instance == null) {
            instance = new PremiumGradient();
        }
        return instance;
    }

    public void checkIconColors() {
        int i = Theme.key_chats_verifiedBackground;
        if (Theme.getColor(i) != this.lastStarColor) {
            this.lastStarColor = Theme.getColor(i);
            this.premiumStarDrawableMini.setColorFilter(new PorterDuffColorFilter(this.lastStarColor, PorterDuff.Mode.MULTIPLY));
        }
        this.premiumStarMenuDrawable = checkColors(this.premiumStarMenuDrawable);
        this.premiumStarMenuDrawable2 = checkColors(this.premiumStarMenuDrawable2);
    }

    public InternalDrawable createGradientDrawable(Drawable drawable) {
        return createGradientDrawable(drawable, this.mainGradient);
    }

    public InternalDrawable createGradientDrawable(Drawable drawable, PremiumGradientTools premiumGradientTools) {
        if (drawable == null) {
            return null;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int minimumHeight = drawable.getMinimumHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, minimumHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, minimumHeight);
        drawable.draw(canvas);
        premiumGradientTools.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        premiumGradientTools.gradientMatrix(0, 0, intrinsicWidth, minimumHeight, -intrinsicWidth, 0.0f);
        canvas.drawRect(0.0f, 0.0f, intrinsicWidth, minimumHeight, premiumGradientTools.paint);
        premiumGradientTools.paint.setXfermode(null);
        return new InternalDrawable(drawable, createBitmap, premiumGradientTools.colors);
    }

    public Paint getMainGradientPaint() {
        if (!MessagesController.getInstance(UserConfig.selectedAccount).premiumFeaturesBlocked()) {
            return this.mainGradientPaint;
        }
        if (this.lockedPremiumPaint == null) {
            this.lockedPremiumPaint = new Paint(1);
        }
        this.lockedPremiumPaint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        return this.lockedPremiumPaint;
    }

    public Paint getPremiumLocakedPaint() {
        if (this.lockedPremiumPaint == null) {
            this.lockedPremiumPaint = new Paint(1);
        }
        this.lockedPremiumPaint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        return this.lockedPremiumPaint;
    }

    public void updateMainGradientMatrix(int i, int i2, int i3, int i4, float f, float f2) {
        this.mainGradient.gradientMatrix(i, i2, i3, i4, f, f2);
    }
}
