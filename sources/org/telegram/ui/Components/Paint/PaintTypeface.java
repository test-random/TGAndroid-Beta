package org.telegram.ui.Components.Paint;

import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.SystemFonts;
import android.os.Build;
import android.text.TextUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;

public class PaintTypeface {
    public static final List BUILT_IN_FONTS;
    public static final PaintTypeface COURIER_NEW_BOLD;
    public static final PaintTypeface MW_BOLD;
    public static final PaintTypeface ROBOTO_CONDENSED;
    public static final PaintTypeface ROBOTO_ITALIC;
    public static final PaintTypeface ROBOTO_MEDIUM;
    public static final PaintTypeface ROBOTO_MONO;
    public static final PaintTypeface ROBOTO_SERIF;
    public static boolean loadingTypefaces;
    private static final List preferable;
    private static List typefaces;
    private final Font font;
    private final String key;
    private final LazyTypeface lazyTypeface;
    private final String name;
    private final String nameKey;
    private final Typeface typeface;

    public static class Family {
        String family;
        ArrayList fonts = new ArrayList();

        Family() {
        }

        public FontData getBold() {
            for (int i = 0; i < this.fonts.size(); i++) {
                if ("Bold".equalsIgnoreCase(((FontData) this.fonts.get(i)).subfamily)) {
                    return (FontData) this.fonts.get(i);
                }
            }
            return null;
        }

        public FontData getRegular() {
            FontData fontData;
            int i = 0;
            while (true) {
                if (i >= this.fonts.size()) {
                    fontData = null;
                    break;
                }
                if ("Regular".equalsIgnoreCase(((FontData) this.fonts.get(i)).subfamily)) {
                    fontData = (FontData) this.fonts.get(i);
                    break;
                }
                i++;
            }
            return (fontData != null || this.fonts.isEmpty()) ? fontData : (FontData) this.fonts.get(0);
        }
    }

    public static class FontData {
        String family;
        Font font;
        String subfamily;

        FontData() {
        }

        public String getName() {
            if ("Regular".equals(this.subfamily) || TextUtils.isEmpty(this.subfamily)) {
                return this.family;
            }
            return this.family + " " + this.subfamily;
        }
    }

    public static class LazyTypeface {
        private final LazyTypefaceLoader loader;
        private Typeface typeface;

        public interface LazyTypefaceLoader {
            Typeface load();
        }

        public LazyTypeface(LazyTypefaceLoader lazyTypefaceLoader) {
            this.loader = lazyTypefaceLoader;
        }

        public Typeface get() {
            if (this.typeface == null) {
                this.typeface = this.loader.load();
            }
            return this.typeface;
        }
    }

    public static class NameRecord {
        final int encodingID;
        final int languageID;
        final int nameID;
        final int nameLength;
        final int platformID;
        final int stringOffset;

        public NameRecord(RandomAccessFile randomAccessFile) {
            this.platformID = randomAccessFile.readUnsignedShort();
            this.encodingID = randomAccessFile.readUnsignedShort();
            this.languageID = randomAccessFile.readUnsignedShort();
            this.nameID = randomAccessFile.readUnsignedShort();
            this.nameLength = randomAccessFile.readUnsignedShort();
            this.stringOffset = randomAccessFile.readUnsignedShort();
        }

        public String read(RandomAccessFile randomAccessFile, int i) {
            randomAccessFile.seek(i + this.stringOffset);
            byte[] bArr = new byte[this.nameLength];
            randomAccessFile.read(bArr);
            return new String(bArr, this.encodingID == 1 ? StandardCharsets.UTF_16BE : StandardCharsets.UTF_8);
        }
    }

    static {
        PaintTypeface paintTypeface = new PaintTypeface("roboto", "PhotoEditorTypefaceRoboto", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$0;
                lambda$static$0 = PaintTypeface.lambda$static$0();
                return lambda$static$0;
            }
        }));
        ROBOTO_MEDIUM = paintTypeface;
        PaintTypeface paintTypeface2 = new PaintTypeface("italic", "PhotoEditorTypefaceItalic", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$1;
                lambda$static$1 = PaintTypeface.lambda$static$1();
                return lambda$static$1;
            }
        }));
        ROBOTO_ITALIC = paintTypeface2;
        PaintTypeface paintTypeface3 = new PaintTypeface("serif", "PhotoEditorTypefaceSerif", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$2;
                lambda$static$2 = PaintTypeface.lambda$static$2();
                return lambda$static$2;
            }
        }));
        ROBOTO_SERIF = paintTypeface3;
        PaintTypeface paintTypeface4 = new PaintTypeface("condensed", "PhotoEditorTypefaceCondensed", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$3;
                lambda$static$3 = PaintTypeface.lambda$static$3();
                return lambda$static$3;
            }
        }));
        ROBOTO_CONDENSED = paintTypeface4;
        PaintTypeface paintTypeface5 = new PaintTypeface("mono", "PhotoEditorTypefaceMono", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$4;
                lambda$static$4 = PaintTypeface.lambda$static$4();
                return lambda$static$4;
            }
        }));
        ROBOTO_MONO = paintTypeface5;
        PaintTypeface paintTypeface6 = new PaintTypeface("mw_bold", "PhotoEditorTypefaceMerriweather", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$5;
                lambda$static$5 = PaintTypeface.lambda$static$5();
                return lambda$static$5;
            }
        }));
        MW_BOLD = paintTypeface6;
        PaintTypeface paintTypeface7 = new PaintTypeface("courier_new_bold", "PhotoEditorTypefaceCourierNew", new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$static$6;
                lambda$static$6 = PaintTypeface.lambda$static$6();
                return lambda$static$6;
            }
        }));
        COURIER_NEW_BOLD = paintTypeface7;
        BUILT_IN_FONTS = Arrays.asList(paintTypeface, paintTypeface2, paintTypeface3, paintTypeface4, paintTypeface5, paintTypeface6, paintTypeface7);
        preferable = Arrays.asList("Google Sans", "Dancing Script", "Carrois Gothic SC", "Cutive Mono", "Droid Sans Mono", "Coming Soon");
    }

    PaintTypeface(final Font font, String str) {
        this.key = str;
        this.name = str;
        this.nameKey = null;
        this.typeface = null;
        this.lazyTypeface = new LazyTypeface(new LazyTypeface.LazyTypefaceLoader() {
            @Override
            public final Typeface load() {
                Typeface lambda$new$7;
                lambda$new$7 = PaintTypeface.lambda$new$7(font);
                return lambda$new$7;
            }
        });
        this.font = font;
    }

    PaintTypeface(String str, String str2, LazyTypeface lazyTypeface) {
        this.key = str;
        this.nameKey = str2;
        this.name = null;
        this.typeface = null;
        this.lazyTypeface = lazyTypeface;
        this.font = null;
    }

    public static PaintTypeface find(String str) {
        if (str != null && !TextUtils.isEmpty(str)) {
            List list = get();
            for (int i = 0; i < list.size(); i++) {
                PaintTypeface paintTypeface = (PaintTypeface) list.get(i);
                if (paintTypeface != null && TextUtils.equals(str, paintTypeface.key)) {
                    return paintTypeface;
                }
            }
        }
        return null;
    }

    public static List get() {
        List list = typefaces;
        if (list != null) {
            return list;
        }
        load();
        return BUILT_IN_FONTS;
    }

    public static void lambda$load$8(ArrayList arrayList) {
        typefaces = arrayList;
        loadingTypefaces = false;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.customTypefacesLoaded, new Object[0]);
    }

    public static void lambda$load$9() {
        Set availableFonts;
        File file;
        FontData parseFont;
        final ArrayList arrayList = new ArrayList(BUILT_IN_FONTS);
        if (Build.VERSION.SDK_INT >= 29) {
            availableFonts = SystemFonts.getAvailableFonts();
            Iterator it = availableFonts.iterator();
            HashMap hashMap = new HashMap();
            while (it.hasNext()) {
                Font m = PaintTypeface$$ExternalSyntheticApiModelOutline1.m(it.next());
                file = m.getFile();
                if (!file.getName().contains("Noto") && (parseFont = parseFont(m)) != null) {
                    Family family = (Family) hashMap.get(parseFont.family);
                    if (family == null) {
                        family = new Family();
                        String str = parseFont.family;
                        family.family = str;
                        hashMap.put(str, family);
                    }
                    family.fonts.add(parseFont);
                }
            }
            Iterator it2 = preferable.iterator();
            while (it2.hasNext()) {
                Family family2 = (Family) hashMap.get((String) it2.next());
                if (family2 != null) {
                    FontData bold = family2.getBold();
                    if (bold == null) {
                        bold = family2.getRegular();
                    }
                    if (bold != null) {
                        arrayList.add(new PaintTypeface(bold.font, bold.getName()));
                    }
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PaintTypeface.lambda$load$8(arrayList);
            }
        });
    }

    public static Typeface lambda$new$7(Font font) {
        File file;
        file = font.getFile();
        return Typeface.createFromFile(file);
    }

    public static Typeface lambda$static$0() {
        return AndroidUtilities.getTypeface("fonts/rmedium.ttf");
    }

    public static Typeface lambda$static$1() {
        return AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
    }

    public static Typeface lambda$static$2() {
        return Typeface.create("serif", 1);
    }

    public static Typeface lambda$static$3() {
        return AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf");
    }

    public static Typeface lambda$static$4() {
        return AndroidUtilities.getTypeface("fonts/rmono.ttf");
    }

    public static Typeface lambda$static$5() {
        return AndroidUtilities.getTypeface("fonts/mw_bold.ttf");
    }

    public static Typeface lambda$static$6() {
        return AndroidUtilities.getTypeface("fonts/courier_new_bold.ttf");
    }

    private static void load() {
        if (typefaces != null || loadingTypefaces) {
            return;
        }
        loadingTypefaces = true;
        Utilities.themeQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                PaintTypeface.lambda$load$9();
            }
        });
    }

    public static org.telegram.ui.Components.Paint.PaintTypeface.FontData parseFont(android.graphics.fonts.Font r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.PaintTypeface.parseFont(android.graphics.fonts.Font):org.telegram.ui.Components.Paint.PaintTypeface$FontData");
    }

    private static String parseString(RandomAccessFile randomAccessFile, int i, NameRecord nameRecord) {
        if (nameRecord == null) {
            return null;
        }
        return nameRecord.read(randomAccessFile, i);
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        String str = this.name;
        return str != null ? str : LocaleController.getString(this.nameKey);
    }

    public Typeface getTypeface() {
        LazyTypeface lazyTypeface = this.lazyTypeface;
        return lazyTypeface != null ? lazyTypeface.get() : this.typeface;
    }
}
