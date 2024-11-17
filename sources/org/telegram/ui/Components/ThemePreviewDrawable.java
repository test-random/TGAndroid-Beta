package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;

public class ThemePreviewDrawable extends BitmapDrawable {
    private DocumentObject.ThemeDocument themeDocument;

    public ThemePreviewDrawable(File file, DocumentObject.ThemeDocument themeDocument) {
        super(createPreview(file, themeDocument));
        this.themeDocument = themeDocument;
    }

    private static Bitmap createPreview(File file, DocumentObject.ThemeDocument themeDocument) {
        BitmapDrawable createDitheredGradientBitmapDrawable;
        MotionBackgroundDrawable motionBackgroundDrawable;
        boolean z;
        Bitmap bitmap;
        Bitmap decodeFile;
        int i;
        new RectF();
        Paint paint = new Paint();
        Bitmap createBitmap = Bitmaps.createBitmap(560, 678, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        SparseIntArray themeFileValues = Theme.getThemeFileValues(null, themeDocument.baseTheme.assetName, null);
        final SparseIntArray clone = themeFileValues.clone();
        themeDocument.accent.fillAccentColors(themeFileValues, clone);
        int previewColor = Theme.getPreviewColor(clone, Theme.key_actionBarDefault);
        int previewColor2 = Theme.getPreviewColor(clone, Theme.key_actionBarDefaultIcon);
        int previewColor3 = Theme.getPreviewColor(clone, Theme.key_chat_messagePanelBackground);
        int previewColor4 = Theme.getPreviewColor(clone, Theme.key_chat_messagePanelIcons);
        int previewColor5 = Theme.getPreviewColor(clone, Theme.key_chat_inBubble);
        int previewColor6 = Theme.getPreviewColor(clone, Theme.key_chat_outBubble);
        int i2 = clone.get(Theme.key_chat_wallpaper);
        int i3 = clone.get(Theme.key_chat_wallpaper_gradient_to1);
        int i4 = clone.get(Theme.key_chat_wallpaper_gradient_to2);
        int i5 = clone.get(Theme.key_chat_wallpaper_gradient_to3);
        int i6 = clone.get(Theme.key_chat_wallpaper_gradient_rotation);
        Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_back).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        Drawable mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_dots).mutate();
        Theme.setDrawableColor(mutate2, previewColor2);
        Drawable mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_smile).mutate();
        Theme.setDrawableColor(mutate3, previewColor4);
        Drawable mutate4 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_mic).mutate();
        Theme.setDrawableColor(mutate4, previewColor4);
        Theme.MessageDrawable[] messageDrawableArr = new Theme.MessageDrawable[2];
        int i7 = 0;
        while (i7 < 2) {
            Drawable drawable = mutate4;
            Drawable drawable2 = mutate;
            Drawable drawable3 = mutate2;
            Theme.MessageDrawable messageDrawable = new Theme.MessageDrawable(2, i7 == 1, false) {
                @Override
                protected int getColor(int i8) {
                    return clone.get(i8);
                }

                @Override
                protected int getCurrentColor(int i8) {
                    return clone.get(i8);
                }
            };
            messageDrawableArr[i7] = messageDrawable;
            Theme.setDrawableColor(messageDrawable, i7 == 1 ? previewColor6 : previewColor5);
            i7++;
            mutate4 = drawable;
            mutate2 = drawable3;
            mutate = drawable2;
        }
        Drawable drawable4 = mutate4;
        Drawable drawable5 = mutate;
        Drawable drawable6 = mutate2;
        if (i4 != 0) {
            motionBackgroundDrawable = new MotionBackgroundDrawable(i2, i3, i4, i5, true);
            createDitheredGradientBitmapDrawable = null;
        } else {
            createDitheredGradientBitmapDrawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(i6, new int[]{i2, i3}, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            motionBackgroundDrawable = null;
        }
        int patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(i2, i3));
        if (createDitheredGradientBitmapDrawable != null) {
            z = false;
            createDitheredGradientBitmapDrawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            createDitheredGradientBitmapDrawable.draw(canvas);
        } else {
            z = false;
        }
        if (file != null) {
            if ("application/x-tgwallpattern".equals(themeDocument.mime_type)) {
                decodeFile = SvgHelper.getBitmap(file, 560, 678, z);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                float f = options.outWidth;
                float f2 = options.outHeight;
                float f3 = 560;
                float f4 = 678;
                float min = Math.min(f / f3, f2 / f4);
                if (min < 1.2f) {
                    min = 1.0f;
                }
                options.inJustDecodeBounds = false;
                if (min <= 1.0f || (f <= f3 && f2 <= f4)) {
                    options.inSampleSize = (int) min;
                } else {
                    int i8 = 1;
                    while (true) {
                        i = i8 * 2;
                        if (i8 * 4 >= min) {
                            break;
                        }
                        i8 = i;
                    }
                    options.inSampleSize = i;
                }
                decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }
            if (decodeFile != null) {
                if (motionBackgroundDrawable != null) {
                    motionBackgroundDrawable.setPatternBitmap((int) (themeDocument.accent.patternIntensity * 100.0f), decodeFile);
                    motionBackgroundDrawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                    motionBackgroundDrawable.draw(canvas);
                } else {
                    Paint paint2 = new Paint(2);
                    if (themeDocument.accent.patternIntensity >= 0.0f) {
                        paint2.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                    }
                    paint2.setAlpha(255);
                    float max = Math.max(560 / decodeFile.getWidth(), 678 / decodeFile.getHeight());
                    int width = (int) (decodeFile.getWidth() * max);
                    canvas.save();
                    canvas.translate((560 - width) / 2, (678 - ((int) (decodeFile.getHeight() * max))) / 2);
                    canvas.scale(max, max);
                    canvas.drawBitmap(decodeFile, 0.0f, 0.0f, paint2);
                    canvas.restore();
                }
            }
            bitmap = decodeFile;
        } else {
            bitmap = null;
        }
        if (bitmap == null && motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            motionBackgroundDrawable.draw(canvas);
        }
        paint.setColor(previewColor);
        canvas.drawRect(0.0f, 0.0f, createBitmap.getWidth(), 120.0f, paint);
        if (drawable5 != null) {
            int intrinsicHeight = (120 - drawable5.getIntrinsicHeight()) / 2;
            drawable5.setBounds(13, intrinsicHeight, drawable5.getIntrinsicWidth() + 13, drawable5.getIntrinsicHeight() + intrinsicHeight);
            drawable5.draw(canvas);
        }
        if (drawable6 != null) {
            int width2 = (createBitmap.getWidth() - drawable6.getIntrinsicWidth()) - 10;
            int intrinsicHeight2 = (120 - drawable6.getIntrinsicHeight()) / 2;
            drawable6.setBounds(width2, intrinsicHeight2, drawable6.getIntrinsicWidth() + width2, drawable6.getIntrinsicHeight() + intrinsicHeight2);
            drawable6.draw(canvas);
        }
        messageDrawableArr[1].setBounds(161, 216, createBitmap.getWidth() - 20, 308);
        messageDrawableArr[1].setTop(0, 560, 522, false, false);
        messageDrawableArr[1].draw(canvas);
        messageDrawableArr[1].setBounds(161, 430, createBitmap.getWidth() - 20, 522);
        messageDrawableArr[1].setTop(430, 560, 522, false, false);
        messageDrawableArr[1].draw(canvas);
        messageDrawableArr[0].setBounds(20, 323, 399, 415);
        messageDrawableArr[0].setTop(323, 560, 522, false, false);
        messageDrawableArr[0].draw(canvas);
        paint.setColor(previewColor3);
        canvas.drawRect(0.0f, createBitmap.getHeight() - 120, createBitmap.getWidth(), createBitmap.getHeight(), paint);
        if (mutate3 != null) {
            int height = (createBitmap.getHeight() - 120) + ((120 - mutate3.getIntrinsicHeight()) / 2);
            mutate3.setBounds(22, height, mutate3.getIntrinsicWidth() + 22, mutate3.getIntrinsicHeight() + height);
            mutate3.draw(canvas);
        }
        if (drawable4 != null) {
            int width3 = (createBitmap.getWidth() - drawable4.getIntrinsicWidth()) - 22;
            int height2 = (createBitmap.getHeight() - 120) + ((120 - drawable4.getIntrinsicHeight()) / 2);
            drawable4.setBounds(width3, height2, drawable4.getIntrinsicWidth() + width3, drawable4.getIntrinsicHeight() + height2);
            drawable4.draw(canvas);
        }
        return createBitmap;
    }
}
