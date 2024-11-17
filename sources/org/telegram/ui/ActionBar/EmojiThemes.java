package org.telegram.ui.ActionBar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class EmojiThemes {
    private static final int[] previewColorKeys = {Theme.key_chat_inBubble, Theme.key_chat_outBubble, Theme.key_featuredStickers_addButton, Theme.key_chat_wallpaper, Theme.key_chat_wallpaper_gradient_to1, Theme.key_chat_wallpaper_gradient_to2, Theme.key_chat_wallpaper_gradient_to3, Theme.key_chat_wallpaper_gradient_rotation};
    private final int currentAccount;
    public String emoji;
    public boolean showAsDefaultStub;
    public boolean showAsRemovedStub;
    public TLRPC.WallPaper wallpaper;
    int currentIndex = 0;
    public ArrayList items = new ArrayList();

    public static class ThemeItem {
        public int accentId = -1;
        public SparseIntArray currentPreviewColors;
        public int inBubbleColor;
        public int outBubbleColor;
        public int outLineColor;
        public int patternBgColor;
        public int patternBgGradientColor1;
        public int patternBgGradientColor2;
        public int patternBgGradientColor3;
        public int patternBgRotation;
        int settingsIndex;
        public Theme.ThemeInfo themeInfo;
        TLRPC.TL_theme tlTheme;
        private String wallpaperLink;
    }

    public EmojiThemes(int i) {
        this.currentAccount = i;
    }

    public EmojiThemes(int i, TLRPC.TL_theme tL_theme, boolean z) {
        this.currentAccount = i;
        this.showAsDefaultStub = z;
        this.emoji = tL_theme.emoticon;
        if (z) {
            return;
        }
        ThemeItem themeItem = new ThemeItem();
        themeItem.tlTheme = tL_theme;
        themeItem.settingsIndex = 0;
        this.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.tlTheme = tL_theme;
        themeItem2.settingsIndex = 1;
        this.items.add(themeItem2);
    }

    public static EmojiThemes createChatThemesDefault(int i) {
        EmojiThemes emojiThemes = new EmojiThemes(i);
        emojiThemes.emoji = "❌";
        emojiThemes.showAsDefaultStub = true;
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = getDefaultThemeInfo(true);
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = getDefaultThemeInfo(false);
        emojiThemes.items.add(themeItem2);
        return emojiThemes;
    }

    public static EmojiThemes createChatThemesRemoved(int i) {
        EmojiThemes emojiThemes = new EmojiThemes(i);
        emojiThemes.emoji = "❌";
        emojiThemes.showAsRemovedStub = true;
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = getDefaultThemeInfo(true);
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = getDefaultThemeInfo(false);
        emojiThemes.items.add(themeItem2);
        return emojiThemes;
    }

    public static EmojiThemes createHomePreviewTheme(int i) {
        EmojiThemes emojiThemes = new EmojiThemes(i);
        emojiThemes.emoji = "🏠";
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme("Blue");
        themeItem.accentId = 99;
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme("Day");
        themeItem2.accentId = 9;
        emojiThemes.items.add(themeItem2);
        ThemeItem themeItem3 = new ThemeItem();
        themeItem3.themeInfo = Theme.getTheme("Night");
        themeItem3.accentId = 0;
        emojiThemes.items.add(themeItem3);
        ThemeItem themeItem4 = new ThemeItem();
        themeItem4.themeInfo = Theme.getTheme("Dark Blue");
        themeItem4.accentId = 0;
        emojiThemes.items.add(themeItem4);
        return emojiThemes;
    }

    public static EmojiThemes createHomeQrTheme(int i) {
        EmojiThemes emojiThemes = new EmojiThemes(i);
        emojiThemes.emoji = "🏠";
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme("Blue");
        themeItem.accentId = 99;
        emojiThemes.items.add(themeItem);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme("Dark Blue");
        themeItem2.accentId = 0;
        emojiThemes.items.add(themeItem2);
        return emojiThemes;
    }

    public static EmojiThemes createPreviewCustom(int i) {
        EmojiThemes emojiThemes = new EmojiThemes(i);
        emojiThemes.emoji = "🎨";
        int i2 = 0;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String string = sharedPreferences.getString("lastDayCustomTheme", null);
        int i3 = sharedPreferences.getInt("lastDayCustomThemeAccentId", -1);
        int i4 = 99;
        String str = "Blue";
        if (string == null || Theme.getTheme(string) == null) {
            string = sharedPreferences.getString("lastDayTheme", "Blue");
            Theme.ThemeInfo theme = Theme.getTheme(string);
            if (theme == null) {
                string = "Blue";
                i3 = 99;
            } else {
                i3 = theme.currentAccentId;
            }
            sharedPreferences.edit().putString("lastDayCustomTheme", string).apply();
        } else if (i3 == -1) {
            i3 = Theme.getTheme(string).lastAccentId;
        }
        if (i3 != -1) {
            str = string;
            i4 = i3;
        }
        String string2 = sharedPreferences.getString("lastDarkCustomTheme", null);
        int i5 = sharedPreferences.getInt("lastDarkCustomThemeAccentId", -1);
        String str2 = "Dark Blue";
        if (string2 == null || Theme.getTheme(string2) == null) {
            string2 = sharedPreferences.getString("lastDarkTheme", "Dark Blue");
            Theme.ThemeInfo theme2 = Theme.getTheme(string2);
            if (theme2 == null) {
                string2 = "Dark Blue";
                i5 = 0;
            } else {
                i5 = theme2.currentAccentId;
            }
            sharedPreferences.edit().putString("lastDarkCustomTheme", string2).apply();
        } else if (i5 == -1) {
            i5 = Theme.getTheme(str).lastAccentId;
        }
        if (i5 != -1) {
            str2 = string2;
            i2 = i5;
        }
        ThemeItem themeItem = new ThemeItem();
        themeItem.themeInfo = Theme.getTheme(str);
        themeItem.accentId = i4;
        emojiThemes.items.add(themeItem);
        emojiThemes.items.add(null);
        ThemeItem themeItem2 = new ThemeItem();
        themeItem2.themeInfo = Theme.getTheme(str2);
        themeItem2.accentId = i2;
        emojiThemes.items.add(themeItem2);
        emojiThemes.items.add(null);
        return emojiThemes;
    }

    public static EmojiThemes createPreviewFullTheme(int i, TLRPC.TL_theme tL_theme) {
        EmojiThemes emojiThemes = new EmojiThemes(i);
        emojiThemes.emoji = tL_theme.emoticon;
        for (int i2 = 0; i2 < tL_theme.settings.size(); i2++) {
            ThemeItem themeItem = new ThemeItem();
            themeItem.tlTheme = tL_theme;
            themeItem.settingsIndex = i2;
            emojiThemes.items.add(themeItem);
        }
        return emojiThemes;
    }

    public static Theme.ThemeInfo getDefaultThemeInfo(boolean z) {
        Theme.ThemeInfo currentNightTheme = z ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        if (z != currentNightTheme.isDark()) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            currentNightTheme = Theme.getTheme(z ? sharedPreferences.getString("lastDarkTheme", "Dark Blue") : sharedPreferences.getString("lastDayTheme", "Blue"));
            if (currentNightTheme == null) {
                currentNightTheme = Theme.getTheme(z ? "Dark Blue" : "Blue");
            }
        }
        return new Theme.ThemeInfo(currentNightTheme);
    }

    private int getOrDefault(SparseIntArray sparseIntArray, int i) {
        if (sparseIntArray == null) {
            return Theme.getDefaultColor(i);
        }
        try {
            int indexOfKey = sparseIntArray.indexOfKey(i);
            if (indexOfKey >= 0) {
                return sparseIntArray.valueAt(indexOfKey);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return Theme.getDefaultColor(i);
    }

    private File getWallpaperThumbFile(long j) {
        return new File(ApplicationLoader.getFilesDirFixed(), "wallpaper_thumb_" + j + ".png");
    }

    public static void lambda$loadWallpaperImage$0(ResultCallback resultCallback, long j, int i, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (!z || bitmapSafe == null) {
            return;
        }
        Bitmap bitmap = bitmapSafe.bitmap;
        if (bitmap == null) {
            Drawable drawable = bitmapSafe.drawable;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        if (resultCallback != null) {
            resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
        }
        ChatThemeController.getInstance(i).saveWallpaperBitmap(bitmap, j);
    }

    public static void lambda$loadWallpaperImage$1(final ResultCallback resultCallback, final long j, TLRPC.WallPaper wallPaper, final int i, Bitmap bitmap) {
        if (bitmap != null && resultCallback != null) {
            resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
            return;
        }
        ImageLocation forDocument = ImageLocation.getForDocument(wallPaper.document);
        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setAllowLoadingOnAttachedOnly(false);
        Point point = AndroidUtilities.displaySize;
        int min = Math.min(point.x, point.y);
        Point point2 = AndroidUtilities.displaySize;
        imageReceiver.setImage(forDocument, ((int) (min / AndroidUtilities.density)) + "_" + ((int) (Math.max(point2.x, point2.y) / AndroidUtilities.density)) + "_f", null, ".jpg", wallPaper, 1);
        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                EmojiThemes.lambda$loadWallpaperImage$0(ResultCallback.this, j, i, imageReceiver2, z, z2, z3);
            }

            @Override
            public void didSetImageBitmap(int i2, String str, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i2, str, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
    }

    public static void lambda$loadWallpaperThumb$2(File file, Bitmap bitmap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 87, fileOutputStream);
                fileOutputStream.close();
            } finally {
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$loadWallpaperThumb$3(ResultCallback resultCallback, long j, final File file, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (!z || bitmapSafe == null || bitmapSafe.bitmap.isRecycled()) {
            return;
        }
        final Bitmap bitmap = bitmapSafe.bitmap;
        if (bitmap == null) {
            Drawable drawable = bitmapSafe.drawable;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        if (bitmap != null) {
            if (resultCallback != null) {
                resultCallback.onComplete(new Pair(Long.valueOf(j), bitmap));
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    EmojiThemes.lambda$loadWallpaperThumb$2(file, bitmap);
                }
            });
        } else if (resultCallback != null) {
            resultCallback.onComplete(null);
        }
    }

    public static void loadWallpaperImage(final int i, final long j, final TLRPC.WallPaper wallPaper, final ResultCallback resultCallback) {
        ChatThemeController.getInstance(i).getWallpaperBitmap(j, new ResultCallback() {
            @Override
            public final void onComplete(Object obj) {
                EmojiThemes.lambda$loadWallpaperImage$1(ResultCallback.this, j, wallPaper, i, (Bitmap) obj);
            }

            @Override
            public void onError(Throwable th) {
                ResultCallback.CC.$default$onError(this, th);
            }

            @Override
            public void onError(TLRPC.TL_error tL_error) {
                ResultCallback.CC.$default$onError(this, tL_error);
            }
        });
    }

    public static void saveCustomTheme(Theme.ThemeInfo themeInfo, int i) {
        SparseArray sparseArray;
        Theme.ThemeAccent themeAccent;
        if (themeInfo == null) {
            return;
        }
        if (i < 0 || (sparseArray = themeInfo.themeAccentsMap) == null || !((themeAccent = (Theme.ThemeAccent) sparseArray.get(i)) == null || themeAccent.isDefault)) {
            if (themeInfo.getKey().equals("Blue") && i == 99) {
                return;
            }
            if (themeInfo.getKey().equals("Day") && i == 9) {
                return;
            }
            if (themeInfo.getKey().equals("Night") && i == 0) {
                return;
            }
            if (themeInfo.getKey().equals("Dark Blue") && i == 0) {
                return;
            }
            boolean isDark = themeInfo.isDark();
            ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit().putString(isDark ? "lastDarkCustomTheme" : "lastDayCustomTheme", themeInfo.getKey()).putInt(isDark ? "lastDarkCustomThemeAccentId" : "lastDayCustomThemeAccentId", i).apply();
        }
    }

    public SparseIntArray createColors(int i, int i2) {
        Theme.ThemeAccent themeAccent;
        SparseIntArray themeFileValues;
        int indexOfKey;
        Theme.ThemeInfo themeInfo = getThemeInfo(i2);
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(i2);
            TLRPC.TL_theme tlTheme = getTlTheme(i2);
            Theme.ThemeInfo themeInfo2 = new Theme.ThemeInfo(Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings.get(settingsIndex))));
            themeAccent = themeInfo2.createNewAccent(tlTheme, i, true, settingsIndex);
            themeInfo2.setCurrentAccentId(themeAccent.id);
            themeInfo = themeInfo2;
        } else {
            SparseArray sparseArray = themeInfo.themeAccentsMap;
            themeAccent = sparseArray != null ? (Theme.ThemeAccent) sparseArray.get(((ThemeItem) this.items.get(i2)).accentId) : null;
        }
        String[] strArr = new String[1];
        if (themeInfo.pathToFile != null) {
            themeFileValues = Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, strArr);
        } else {
            String str = themeInfo.assetName;
            themeFileValues = str != null ? Theme.getThemeFileValues(null, str, strArr) : new SparseIntArray();
        }
        ((ThemeItem) this.items.get(i2)).wallpaperLink = strArr[0];
        if (themeAccent != null) {
            SparseIntArray clone = themeFileValues.clone();
            themeAccent.fillAccentColors(themeFileValues, clone);
            themeFileValues = clone;
        }
        SparseIntArray fallbackKeys = Theme.getFallbackKeys();
        for (int i3 = 0; i3 < fallbackKeys.size(); i3++) {
            int keyAt = fallbackKeys.keyAt(i3);
            int valueAt = fallbackKeys.valueAt(i3);
            if (themeFileValues.indexOfKey(keyAt) < 0 && (indexOfKey = themeFileValues.indexOfKey(valueAt)) >= 0) {
                themeFileValues.put(keyAt, themeFileValues.valueAt(indexOfKey));
            }
        }
        int[] defaultColors = Theme.getDefaultColors();
        for (int i4 = 0; i4 < defaultColors.length; i4++) {
            if (themeFileValues.indexOfKey(i4) < 0) {
                themeFileValues.put(i4, defaultColors[i4]);
            }
        }
        return themeFileValues;
    }

    public int getAccentId(int i) {
        return ((ThemeItem) this.items.get(i)).accentId;
    }

    public String getEmoticon() {
        return this.emoji;
    }

    public SparseIntArray getPreviewColors(int i, int i2) {
        Theme.ThemeAccent themeAccent;
        SparseIntArray themeFileValues;
        SparseIntArray sparseIntArray = ((ThemeItem) this.items.get(i2)).currentPreviewColors;
        if (sparseIntArray != null) {
            return sparseIntArray;
        }
        Theme.ThemeInfo themeInfo = getThemeInfo(i2);
        if (themeInfo == null) {
            int settingsIndex = getSettingsIndex(i2);
            TLRPC.TL_theme tlTheme = getTlTheme(i2);
            Theme.ThemeInfo theme = Theme.getTheme(tlTheme != null ? Theme.getBaseThemeKey(tlTheme.settings.get(settingsIndex)) : "Blue");
            if (theme != null) {
                themeInfo = new Theme.ThemeInfo(theme);
                themeAccent = themeInfo.createNewAccent(tlTheme, i, true, settingsIndex);
                if (themeAccent != null) {
                    themeInfo.setCurrentAccentId(themeAccent.id);
                }
            }
            themeAccent = null;
        } else {
            SparseArray sparseArray = themeInfo.themeAccentsMap;
            if (sparseArray != null) {
                themeAccent = (Theme.ThemeAccent) sparseArray.get(((ThemeItem) this.items.get(i2)).accentId);
            }
            themeAccent = null;
        }
        if (themeInfo == null) {
            return sparseIntArray;
        }
        String[] strArr = new String[1];
        if (themeInfo.pathToFile != null) {
            themeFileValues = Theme.getThemeFileValues(new File(themeInfo.pathToFile), null, strArr);
        } else {
            String str = themeInfo.assetName;
            themeFileValues = str != null ? Theme.getThemeFileValues(null, str, strArr) : new SparseIntArray();
        }
        int i3 = 0;
        ((ThemeItem) this.items.get(i2)).wallpaperLink = strArr[0];
        if (themeAccent != null) {
            SparseIntArray clone = themeFileValues.clone();
            themeAccent.fillAccentColors(themeFileValues, clone);
            themeFileValues = clone;
        }
        SparseIntArray fallbackKeys = Theme.getFallbackKeys();
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        ((ThemeItem) this.items.get(i2)).currentPreviewColors = sparseIntArray2;
        while (true) {
            try {
                int[] iArr = previewColorKeys;
                if (i3 >= iArr.length) {
                    break;
                }
                int i4 = iArr[i3];
                int indexOfKey = themeFileValues.indexOfKey(i4);
                if (indexOfKey < 0) {
                    int i5 = fallbackKeys.get(i4, -1);
                    if (i5 >= 0 && (indexOfKey = themeFileValues.indexOfKey(i5)) >= 0) {
                    }
                    i3++;
                }
                sparseIntArray2.put(i4, themeFileValues.valueAt(indexOfKey));
                i3++;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return sparseIntArray2;
    }

    public int getSettingsIndex(int i) {
        return ((ThemeItem) this.items.get(i)).settingsIndex;
    }

    public Theme.ThemeInfo getThemeInfo(int i) {
        return ((ThemeItem) this.items.get(i)).themeInfo;
    }

    public ThemeItem getThemeItem(int i) {
        return (ThemeItem) this.items.get(i);
    }

    public TLRPC.TL_theme getTlTheme(int i) {
        return ((ThemeItem) this.items.get(i)).tlTheme;
    }

    public TLRPC.WallPaper getWallpaper(int i) {
        TLRPC.TL_theme tlTheme;
        int i2 = ((ThemeItem) this.items.get(i)).settingsIndex;
        if (i2 < 0 || (tlTheme = getTlTheme(i)) == null) {
            return null;
        }
        return tlTheme.settings.get(i2).wallpaper;
    }

    public String getWallpaperLink(int i) {
        return ((ThemeItem) this.items.get(i)).wallpaperLink;
    }

    public void initColors() {
        getPreviewColors(0, 0);
        getPreviewColors(0, 1);
    }

    public boolean isAnyStub() {
        return this.showAsDefaultStub || this.showAsRemovedStub;
    }

    public void loadPreviewColors(int i) {
        for (int i2 = 0; i2 < this.items.size(); i2++) {
            if (this.items.get(i2) != null) {
                SparseIntArray previewColors = getPreviewColors(i, i2);
                ((ThemeItem) this.items.get(i2)).inBubbleColor = getOrDefault(previewColors, Theme.key_chat_inBubble);
                ((ThemeItem) this.items.get(i2)).outBubbleColor = getOrDefault(previewColors, Theme.key_chat_outBubble);
                ((ThemeItem) this.items.get(i2)).outLineColor = getOrDefault(previewColors, Theme.key_featuredStickers_addButton);
                ((ThemeItem) this.items.get(i2)).patternBgColor = previewColors.get(Theme.key_chat_wallpaper, 0);
                ((ThemeItem) this.items.get(i2)).patternBgGradientColor1 = previewColors.get(Theme.key_chat_wallpaper_gradient_to1, 0);
                ((ThemeItem) this.items.get(i2)).patternBgGradientColor2 = previewColors.get(Theme.key_chat_wallpaper_gradient_to2, 0);
                ((ThemeItem) this.items.get(i2)).patternBgGradientColor3 = previewColors.get(Theme.key_chat_wallpaper_gradient_to3, 0);
                ((ThemeItem) this.items.get(i2)).patternBgRotation = previewColors.get(Theme.key_chat_wallpaper_gradient_rotation, 0);
                if (((ThemeItem) this.items.get(i2)).themeInfo != null && ((ThemeItem) this.items.get(i2)).themeInfo.getKey().equals("Blue")) {
                    if ((((ThemeItem) this.items.get(i2)).accentId >= 0 ? ((ThemeItem) this.items.get(i2)).accentId : ((ThemeItem) this.items.get(i2)).themeInfo.currentAccentId) == 99) {
                        ((ThemeItem) this.items.get(i2)).patternBgColor = -2368069;
                        ((ThemeItem) this.items.get(i2)).patternBgGradientColor1 = -9722489;
                        ((ThemeItem) this.items.get(i2)).patternBgGradientColor2 = -2762611;
                        ((ThemeItem) this.items.get(i2)).patternBgGradientColor3 = -7817084;
                    }
                }
            }
        }
    }

    public void loadWallpaper(int i, ResultCallback resultCallback) {
        TLRPC.WallPaper wallpaper = getWallpaper(i);
        if (wallpaper != null) {
            loadWallpaperImage(this.currentAccount, getTlTheme(i).id, wallpaper, resultCallback);
        } else if (resultCallback != null) {
            resultCallback.onComplete(null);
        }
    }

    public void loadWallpaperThumb(int i, final ResultCallback resultCallback) {
        TLRPC.WallPaper wallpaper = getWallpaper(i);
        if (wallpaper == null) {
            if (resultCallback != null) {
                resultCallback.onComplete(null);
                return;
            }
            return;
        }
        final long j = getTlTheme(i).id;
        Bitmap wallpaperThumbBitmap = ChatThemeController.getInstance(this.currentAccount).getWallpaperThumbBitmap(j);
        final File wallpaperThumbFile = getWallpaperThumbFile(j);
        if (wallpaperThumbBitmap == null && wallpaperThumbFile.exists() && wallpaperThumbFile.length() > 0) {
            try {
                wallpaperThumbBitmap = BitmapFactory.decodeFile(wallpaperThumbFile.getAbsolutePath());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (wallpaperThumbBitmap != null) {
            if (resultCallback != null) {
                resultCallback.onComplete(new Pair(Long.valueOf(j), wallpaperThumbBitmap));
                return;
            }
            return;
        }
        TLRPC.Document document = wallpaper.document;
        if (document == null) {
            if (resultCallback != null) {
                resultCallback.onComplete(new Pair(Long.valueOf(j), null));
            }
        } else {
            ImageLocation forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 140), wallpaper.document);
            ImageReceiver imageReceiver = new ImageReceiver();
            imageReceiver.setAllowLoadingOnAttachedOnly(false);
            imageReceiver.setImage(forDocument, "120_140", null, null, null, 1);
            imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                @Override
                public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                    EmojiThemes.lambda$loadWallpaperThumb$3(ResultCallback.this, j, wallpaperThumbFile, imageReceiver2, z, z2, z3);
                }

                @Override
                public void didSetImageBitmap(int i2, String str, Drawable drawable) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i2, str, drawable);
                }

                @Override
                public void onAnimationReady(ImageReceiver imageReceiver2) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
                }
            });
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
        }
    }

    public void preloadWallpaper() {
        loadWallpaperThumb(0, null);
        loadWallpaperThumb(1, null);
        loadWallpaper(0, null);
        loadWallpaper(1, null);
    }
}
