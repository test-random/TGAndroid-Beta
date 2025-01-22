package org.telegram.ui.Stars;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public abstract class StarGiftPatterns {
    private static final float[][] patternLocations = {new float[]{83.33f, 24.0f, 27.33f, 0.22f, 68.66f, 75.33f, 25.33f, 0.21f, 0.0f, 86.0f, 25.33f, 0.12f, -68.66f, 75.33f, 25.33f, 0.21f, -82.66f, 13.66f, 27.33f, 0.22f, -80.0f, -33.33f, 20.0f, 0.24f, -46.5f, -63.16f, 27.0f, 0.21f, 1.0f, -82.66f, 20.0f, 0.15f, 46.5f, -63.16f, 27.0f, 0.21f, 80.0f, -33.33f, 19.33f, 0.24f, 115.66f, -63.0f, 20.0f, 0.15f, 134.0f, -10.66f, 20.0f, 0.18f, 118.66f, 55.66f, 20.0f, 0.15f, 124.33f, 98.33f, 20.0f, 0.11f, -128.0f, 98.33f, 20.0f, 0.11f, -108.0f, 55.66f, 20.0f, 0.15f, -123.33f, -10.66f, 20.0f, 0.18f, -116.0f, -63.33f, 20.0f, 0.15f}, new float[]{27.33f, -57.66f, 20.0f, 0.12f, 59.0f, -32.0f, 19.33f, 0.22f, 77.0f, 4.33f, 22.66f, 0.2f, 100.0f, 40.33f, 18.0f, 0.12f, 58.66f, 59.0f, 20.0f, 0.18f, 73.33f, 100.33f, 22.66f, 0.15f, 75.0f, 155.0f, 22.0f, 0.11f, -27.33f, -57.33f, 20.0f, 0.12f, -59.0f, -32.33f, 19.33f, 0.2f, -77.0f, 4.66f, 23.33f, 0.2f, -98.66f, 41.0f, 18.66f, 0.12f, -58.0f, 59.33f, 19.33f, 0.18f, -73.33f, 100.0f, 22.0f, 0.15f, -75.66f, 155.0f, 22.0f, 0.11f}, new float[]{-0.83f, -52.16f, 12.33f, 0.2f, 26.66f, -40.33f, 16.0f, 0.2f, 44.16f, -20.5f, 12.33f, 0.2f, 53.0f, 7.33f, 16.0f, 0.2f, 31.0f, 23.66f, 14.66f, 0.2f, 0.0f, 32.0f, 13.33f, 0.2f, -29.0f, 23.66f, 14.0f, 0.2f, -53.0f, 7.33f, 16.0f, 0.2f, -44.5f, -20.16f, 12.33f, 0.2f, -27.33f, -40.33f, 16.0f, 0.2f, 43.66f, 50.0f, 14.66f, 0.2f, -41.66f, 48.0f, 14.66f, 0.2f}, new float[]{-0.16f, -103.5f, 20.33f, 0.15f, 39.66f, -77.33f, 26.66f, 0.15f, 70.66f, -46.33f, 21.33f, 0.15f, 84.5f, -3.83f, 29.66f, 0.15f, 65.33f, 56.33f, 24.66f, 0.15f, 0.0f, 67.66f, 24.66f, 0.15f, -65.66f, 56.66f, 24.66f, 0.15f, -85.0f, -4.0f, 29.33f, 0.15f, -70.66f, -46.33f, 21.33f, 0.15f, -40.33f, -77.66f, 26.66f, 0.15f, 62.66f, -109.66f, 21.33f, 0.11f, 103.166f, -67.5f, 20.33f, 0.11f, 110.33f, 37.66f, 20.66f, 0.11f, 94.166f, 91.16f, 20.33f, 0.11f, 38.83f, 91.16f, 20.33f, 0.11f, 0.0f, 112.5f, 20.33f, 0.11f, -38.83f, 91.16f, 20.33f, 0.11f, -94.166f, 91.16f, 20.33f, 0.11f, -110.33f, 37.66f, 20.66f, 0.11f, -103.166f, -67.5f, 20.33f, 0.11f, -62.66f, -109.66f, 21.33f, 0.11f}};
    private static final float[] profileRight = {-35.66f, -5.0f, 24.0f, 0.2388f, -14.33f, -29.33f, 20.66f, 0.32f, -15.0f, -73.66f, 19.33f, 0.32f, -2.0f, -99.66f, 18.0f, 0.1476f, -64.33f, -24.66f, 23.33f, 0.3235f, -40.66f, -53.33f, 24.0f, 0.3654f, -50.33f, -85.66f, 20.0f, 0.172f, -96.0f, -1.33f, 19.33f, 0.3343f, -136.66f, -13.0f, 18.66f, 0.2569f, -104.66f, -33.66f, 20.66f, 0.2216f, -82.0f, -62.33f, 22.66f, 0.2562f, -131.66f, -60.0f, 18.0f, 0.1316f, -105.66f, -88.33f, 18.0f, 0.1487f};
    private static final float[] profileLeft = {0.0f, -107.33f, 16.0f, 0.1505f, 14.33f, -84.0f, 18.0f, 0.1988f, 0.0f, -50.66f, 18.66f, 0.3225f, 13.0f, -15.0f, 18.66f, 0.37f, 43.33f, 1.0f, 18.66f, 0.3186f};

    public static void drawPattern(Canvas canvas, int i, Drawable drawable, float f, float f2, float f3, float f4) {
        if (f3 <= 0.0f) {
            return;
        }
        int i2 = 0;
        while (true) {
            float[] fArr = patternLocations[i];
            if (i2 >= fArr.length) {
                return;
            }
            float f5 = fArr[i2];
            float f6 = fArr[i2 + 1];
            float f7 = fArr[i2 + 2];
            float f8 = fArr[i2 + 3];
            if (f >= f2 || i != 0) {
                f6 = f5;
                f5 = f6;
            }
            float f9 = f6 * f4;
            float f10 = f5 * f4;
            float f11 = f7 * f4;
            drawable.setBounds((int) (AndroidUtilities.dp(f9) - (AndroidUtilities.dp(f11) / 2.0f)), (int) (AndroidUtilities.dp(f10) - (AndroidUtilities.dp(f11) / 2.0f)), (int) (AndroidUtilities.dp(f9) + (AndroidUtilities.dp(f11) / 2.0f)), (int) (AndroidUtilities.dp(f10) + (AndroidUtilities.dp(f11) / 2.0f)));
            drawable.setAlpha((int) (255.0f * f3 * f8));
            drawable.draw(canvas);
            i2 += 4;
        }
    }

    public static void drawPattern(Canvas canvas, Drawable drawable, float f, float f2, float f3, float f4) {
        drawPattern(canvas, 0, drawable, f, f2, f3, f4);
    }

    public static void drawProfilePattern(Canvas canvas, Drawable drawable, float f, float f2, float f3, float f4) {
        if (f3 <= 0.0f) {
            return;
        }
        int i = 0;
        if (f4 > 0.0f) {
            int i2 = 0;
            while (true) {
                float[] fArr = profileLeft;
                if (i2 >= fArr.length) {
                    break;
                }
                float f5 = fArr[i2];
                float f6 = fArr[i2 + 1];
                float f7 = fArr[i2 + 2];
                float f8 = fArr[i2 + 3];
                drawable.setBounds((int) ((AndroidUtilities.dpf2(f5) + 0.0f) - (AndroidUtilities.dpf2(f7) / 2.0f)), (int) ((f2 + AndroidUtilities.dpf2(f6)) - (AndroidUtilities.dpf2(f7) / 2.0f)), (int) (AndroidUtilities.dpf2(f5) + 0.0f + (AndroidUtilities.dpf2(f7) / 2.0f)), (int) (f2 + AndroidUtilities.dpf2(f6) + (AndroidUtilities.dpf2(f7) / 2.0f)));
                drawable.setAlpha((int) (f3 * 255.0f * f8 * f4));
                drawable.draw(canvas);
                i2 += 4;
            }
            float f9 = ((f / AndroidUtilities.density) - 77.5f) - 173.33f;
            int max = Math.max(0, Math.round(f9 / 27.25f));
            if (max % 2 == 0) {
                max++;
            }
            for (int i3 = 0; i3 < max; i3++) {
                float f10 = ((i3 / (max - 1)) * f9) + 77.5f;
                float f11 = i3 % 2 == 0 ? 0.0f : -12.5f;
                drawable.setBounds((int) ((AndroidUtilities.dpf2(f10) + 0.0f) - (AndroidUtilities.dpf2(17.0f) / 2.0f)), (int) ((f2 + AndroidUtilities.dpf2(f11)) - (AndroidUtilities.dpf2(17.0f) / 2.0f)), (int) (AndroidUtilities.dpf2(f10) + 0.0f + (AndroidUtilities.dpf2(17.0f) / 2.0f)), (int) (f2 + AndroidUtilities.dpf2(f11) + (AndroidUtilities.dpf2(17.0f) / 2.0f)));
                drawable.setAlpha((int) (f3 * 255.0f * 0.21f * f4));
                drawable.draw(canvas);
            }
        }
        while (true) {
            float[] fArr2 = profileRight;
            if (i >= fArr2.length) {
                return;
            }
            float f12 = fArr2[i];
            float f13 = fArr2[i + 1];
            float f14 = fArr2[i + 2];
            float f15 = fArr2[i + 3];
            drawable.setBounds((int) ((f + AndroidUtilities.dpf2(f12)) - (AndroidUtilities.dpf2(f14) / 2.0f)), (int) ((f2 + AndroidUtilities.dpf2(f13)) - (AndroidUtilities.dpf2(f14) / 2.0f)), (int) (f + AndroidUtilities.dpf2(f12) + (AndroidUtilities.dpf2(f14) / 2.0f)), (int) (f2 + AndroidUtilities.dpf2(f13) + (AndroidUtilities.dpf2(f14) / 2.0f)));
            drawable.setAlpha((int) (f3 * 255.0f * f15));
            drawable.draw(canvas);
            i += 4;
        }
    }
}
