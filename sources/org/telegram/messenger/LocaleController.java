package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Xml;
import j$.util.DesugarTimeZone;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.RestrictedLanguagesSelectActivity;
import org.xmlpull.v1.XmlPullParser;

public class LocaleController {
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    public static boolean is24HourFormat = false;
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    private static Boolean useImperialSystemType;
    private volatile FastDateFormat chatDate;
    private volatile FastDateFormat chatFullDate;
    private boolean checkingUpdateForCurrentRemoteLocale;
    private HashMap<String, String> currencyValues;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private String currentSystemLocale;
    private volatile FastDateFormat formatterBannedUntil;
    private volatile FastDateFormat formatterBannedUntilThisYear;
    private volatile FastDateFormat formatterBoostExpired;
    private volatile FastDateFormat formatterConstDay;
    private volatile FastDateFormat formatterDay;
    private volatile FastDateFormat formatterDayMonth;
    private volatile FastDateFormat formatterGiveawayCard;
    private volatile FastDateFormat formatterGiveawayMonthDay;
    private volatile FastDateFormat formatterGiveawayMonthDayYear;
    private volatile FastDateFormat formatterMonthYear;
    private volatile FastDateFormat formatterScheduleDay;
    private volatile FastDateFormat formatterScheduleYear;
    private volatile FastDateFormat formatterStats;
    private volatile FastDateFormat formatterWeek;
    private volatile FastDateFormat formatterWeekLong;
    private volatile FastDateFormat formatterYear;
    private volatile FastDateFormat formatterYearMax;
    private String languageOverride;
    private boolean loadingRemoteLanguages;
    private boolean reloadLastFile;
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    private static HashMap<Integer, String> resourcesCacheMap = new HashMap<>();
    private static volatile LocaleController Instance = null;
    private static char[] defaultNumbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static char[][] otherNumbers = {new char[]{1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641}, new char[]{1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785}, new char[]{2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415}, new char[]{2790, 2791, 2792, 2793, 2794, 2795, 2796, 2797, 2798, 2799}, new char[]{2662, 2663, 2664, 2665, 2666, 2667, 2668, 2669, 2670, 2671}, new char[]{2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543}, new char[]{3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311}, new char[]{2918, 2919, 2920, 2921, 2922, 2923, 2924, 2925, 2926, 2927}, new char[]{3430, 3431, 3432, 3433, 3434, 3435, 3436, 3437, 3438, 3439}, new char[]{3046, 3047, 3048, 3049, 3050, 3051, 3052, 3053, 3054, 3055}, new char[]{3174, 3175, 3176, 3177, 3178, 3179, 3180, 3181, 3182, 3183}, new char[]{4160, 4161, 4162, 4163, 4164, 4165, 4166, 4167, 4168, 4169}, new char[]{3872, 3873, 3874, 3875, 3876, 3877, 3878, 3879, 3880, 3881}, new char[]{6160, 6161, 6162, 6163, 6164, 6165, 6166, 6167, 6168, 6169}, new char[]{6112, 6113, 6114, 6115, 6116, 6117, 6118, 6119, 6120, 6121}, new char[]{3664, 3665, 3666, 3667, 3668, 3669, 3670, 3671, 3672, 3673}, new char[]{3792, 3793, 3794, 3795, 3796, 3797, 3798, 3799, 3800, 3801}, new char[]{43472, 43473, 43474, 43475, 43476, 43477, 43478, 43479, 43480, 43481}};
    private final FastDateFormat[] formatterScheduleSend = new FastDateFormat[15];
    private HashMap<String, PluralRules> allRules = new HashMap<>();
    private HashMap<String, String> localeValues = new HashMap<>();
    private boolean changingConfiguration = false;
    public ArrayList<LocaleInfo> languages = new ArrayList<>();
    public ArrayList<LocaleInfo> unofficialLanguages = new ArrayList<>();
    public ArrayList<LocaleInfo> remoteLanguages = new ArrayList<>();
    public HashMap<String, LocaleInfo> remoteLanguagesDict = new HashMap<>();
    public HashMap<String, LocaleInfo> languagesDict = new HashMap<>();
    private ArrayList<LocaleInfo> otherLanguages = new ArrayList<>();
    private boolean patching = false;

    public static class LocaleInfo {
        public String baseLangCode;
        public int baseVersion;
        public boolean builtIn;
        public boolean isRtl;
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String pluralLangCode;
        public int serverIndex;
        public String shortName;
        public int version;

        public static LocaleInfo createWithString(String str) {
            LocaleInfo localeInfo = null;
            if (str != null && str.length() != 0) {
                String[] split = str.split("\\|");
                if (split.length >= 4) {
                    localeInfo = new LocaleInfo();
                    localeInfo.name = split[0];
                    localeInfo.nameEnglish = split[1];
                    localeInfo.shortName = split[2].toLowerCase();
                    localeInfo.pathToFile = split[3];
                    if (split.length >= 5) {
                        localeInfo.version = Utilities.parseInt((CharSequence) split[4]).intValue();
                    }
                    localeInfo.baseLangCode = split.length >= 6 ? split[5] : "";
                    localeInfo.pluralLangCode = split.length >= 7 ? split[6] : localeInfo.shortName;
                    if (split.length >= 8) {
                        localeInfo.isRtl = Utilities.parseInt((CharSequence) split[7]).intValue() == 1;
                    }
                    if (split.length >= 9) {
                        localeInfo.baseVersion = Utilities.parseInt((CharSequence) split[8]).intValue();
                    }
                    localeInfo.serverIndex = split.length >= 10 ? Utilities.parseInt((CharSequence) split[9]).intValue() : Integer.MAX_VALUE;
                    if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                        localeInfo.baseLangCode = localeInfo.baseLangCode.replace("-", "_");
                    }
                }
            }
            return localeInfo;
        }

        public String getBaseLangCode() {
            String str = this.baseLangCode;
            return str == null ? "" : str.replace("_", "-");
        }

        public String getKey() {
            StringBuilder sb;
            String str;
            if (this.pathToFile != null && !isRemote() && !isUnofficial()) {
                sb = new StringBuilder();
                str = "local_";
            } else {
                if (!isUnofficial()) {
                    return this.shortName;
                }
                sb = new StringBuilder();
                str = "unofficial_";
            }
            sb.append(str);
            sb.append(this.shortName);
            return sb.toString();
        }

        public String getLangCode() {
            return this.shortName.replace("_", "-");
        }

        public File getPathToBaseFile() {
            if (!isUnofficial()) {
                return null;
            }
            return new File(ApplicationLoader.getFilesDirFixed(), "unofficial_base_" + this.shortName + ".xml");
        }

        public File getPathToFile() {
            if (isRemote()) {
                return new File(ApplicationLoader.getFilesDirFixed(), "remote_" + this.shortName + ".xml");
            }
            if (!isUnofficial()) {
                if (TextUtils.isEmpty(this.pathToFile)) {
                    return null;
                }
                return new File(this.pathToFile);
            }
            return new File(ApplicationLoader.getFilesDirFixed(), "unofficial_" + this.shortName + ".xml");
        }

        public String getSaveString() {
            String str = this.baseLangCode;
            if (str == null) {
                str = "";
            }
            TextUtils.isEmpty(this.pluralLangCode);
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile + "|" + this.version + "|" + str + "|" + this.pluralLangCode + "|" + (this.isRtl ? 1 : 0) + "|" + this.baseVersion + "|" + this.serverIndex;
        }

        public boolean hasBaseLang() {
            return (!isUnofficial() || TextUtils.isEmpty(this.baseLangCode) || this.baseLangCode.equals(this.shortName)) ? false : true;
        }

        public boolean isBuiltIn() {
            return this.builtIn;
        }

        public boolean isLocal() {
            return (TextUtils.isEmpty(this.pathToFile) || isRemote() || isUnofficial()) ? false : true;
        }

        public boolean isRemote() {
            return "remote".equals(this.pathToFile);
        }

        public boolean isUnofficial() {
            return "unofficial".equals(this.pathToFile);
        }
    }

    public static abstract class PluralRules {
        abstract int quantityForNumber(int i);
    }

    public static class PluralRules_Arabic extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i2 < 3 || i2 > 10) {
                return (i2 < 11 || i2 > 99) ? 0 : 16;
            }
            return 8;
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && i2 != 11) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 == 0) {
                return 16;
            }
            if (i3 < 5 || i3 > 9) {
                return (i2 < 11 || i2 > 14) ? 0 : 16;
            }
            return 16;
        }
    }

    public static class PluralRules_Breton extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return (i < 2 || i > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_French extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            return (i < 0 || i >= 2) ? 0 : 2;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return i == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return (i % 10 != 1 || i % 100 == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && (i2 < 11 || i2 > 19)) {
                return 2;
            }
            if (i3 < 2 || i3 > 9) {
                return 0;
            }
            return (i2 < 11 || i2 > 19) ? 8 : 0;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            return (i % 10 != 1 || i == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i == 0) {
                return 8;
            }
            if (i2 < 2 || i2 > 10) {
                return (i2 < 11 || i2 > 19) ? 0 : 16;
            }
            return 8;
        }
    }

    public static class PluralRules_None extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            return 0;
        }
    }

    public static class PluralRules_One extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            return i == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i == 1) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 >= 0 && i3 <= 1) {
                return 16;
            }
            if (i3 < 5 || i3 > 9) {
                return (i2 < 12 || i2 > 14) ? 0 : 16;
            }
            return 16;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i != 0) {
                return (i2 < 1 || i2 > 19) ? 0 : 8;
            }
            return 8;
        }
    }

    public static class PluralRules_Serbian extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && i2 != 11) {
                return 2;
            }
            if (i3 < 2 || i3 > 4) {
                return 0;
            }
            return (i2 < 12 || i2 > 14) ? 8 : 0;
        }
    }

    public static class PluralRules_Slovenian extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i2 == 1) {
                return 2;
            }
            if (i2 == 2) {
                return 4;
            }
            return (i2 < 3 || i2 > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i < 0 || i > 1) {
                return (i < 2 || i > 10) ? 0 : 8;
            }
            return 2;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return i == 2 ? 4 : 0;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Zero extends PluralRules {
        @Override
        public int quantityForNumber(int i) {
            return (i == 0 || i == 1) ? 2 : 0;
        }
    }

    public class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
        }

        public void lambda$onReceive$0() {
            if (LocaleController.this.getFormatterDayMonth().getTimeZone().equals(TimeZone.getDefault())) {
                return;
            }
            LocaleController.getInstance().recreateFormatters();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.TimeZoneChangedReceiver.this.lambda$onReceive$0();
                }
            });
        }
    }

    public LocaleController() {
        LocaleInfo localeInfo;
        boolean z = false;
        addRules(new String[]{"bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"}, new PluralRules_One());
        addRules(new String[]{"cs", "sk"}, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"ru", "uk", "be"}, new PluralRules_Balkan());
        addRules(new String[]{"sr", "hr", "bs", "sh"}, new PluralRules_Serbian());
        addRules(new String[]{"lv"}, new PluralRules_Latvian());
        addRules(new String[]{"lt"}, new PluralRules_Lithuanian());
        addRules(new String[]{"pl"}, new PluralRules_Polish());
        addRules(new String[]{"ro", "mo"}, new PluralRules_Romanian());
        addRules(new String[]{"sl"}, new PluralRules_Slovenian());
        addRules(new String[]{"ar"}, new PluralRules_Arabic());
        addRules(new String[]{"mk"}, new PluralRules_Macedonian());
        addRules(new String[]{"cy"}, new PluralRules_Welsh());
        addRules(new String[]{"br"}, new PluralRules_Breton());
        addRules(new String[]{"lag"}, new PluralRules_Langi());
        addRules(new String[]{"shi"}, new PluralRules_Tachelhit());
        addRules(new String[]{"mt"}, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "jw", "ka", "km", "kn", "ms", "th", "in"}, new PluralRules_None());
        LocaleInfo localeInfo2 = new LocaleInfo();
        localeInfo2.name = "English";
        localeInfo2.nameEnglish = "English";
        localeInfo2.pluralLangCode = "en";
        localeInfo2.shortName = "en";
        localeInfo2.pathToFile = null;
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        LocaleInfo localeInfo3 = new LocaleInfo();
        localeInfo3.name = "Italiano";
        localeInfo3.nameEnglish = "Italian";
        localeInfo3.pluralLangCode = "it";
        localeInfo3.shortName = "it";
        localeInfo3.pathToFile = null;
        localeInfo3.builtIn = true;
        this.languages.add(localeInfo3);
        this.languagesDict.put(localeInfo3.shortName, localeInfo3);
        LocaleInfo localeInfo4 = new LocaleInfo();
        localeInfo4.name = "Español";
        localeInfo4.nameEnglish = "Spanish";
        localeInfo4.pluralLangCode = "es";
        localeInfo4.shortName = "es";
        localeInfo4.builtIn = true;
        this.languages.add(localeInfo4);
        this.languagesDict.put(localeInfo4.shortName, localeInfo4);
        LocaleInfo localeInfo5 = new LocaleInfo();
        localeInfo5.name = "Deutsch";
        localeInfo5.nameEnglish = "German";
        localeInfo5.pluralLangCode = "de";
        localeInfo5.shortName = "de";
        localeInfo5.pathToFile = null;
        localeInfo5.builtIn = true;
        this.languages.add(localeInfo5);
        this.languagesDict.put(localeInfo5.shortName, localeInfo5);
        LocaleInfo localeInfo6 = new LocaleInfo();
        localeInfo6.name = "Nederlands";
        localeInfo6.nameEnglish = "Dutch";
        localeInfo6.pluralLangCode = "nl";
        localeInfo6.shortName = "nl";
        localeInfo6.pathToFile = null;
        localeInfo6.builtIn = true;
        this.languages.add(localeInfo6);
        this.languagesDict.put(localeInfo6.shortName, localeInfo6);
        LocaleInfo localeInfo7 = new LocaleInfo();
        localeInfo7.name = "العربية";
        localeInfo7.nameEnglish = "Arabic";
        localeInfo7.pluralLangCode = "ar";
        localeInfo7.shortName = "ar";
        localeInfo7.pathToFile = null;
        localeInfo7.builtIn = true;
        localeInfo7.isRtl = true;
        this.languages.add(localeInfo7);
        this.languagesDict.put(localeInfo7.shortName, localeInfo7);
        LocaleInfo localeInfo8 = new LocaleInfo();
        localeInfo8.name = "Português (Brasil)";
        localeInfo8.nameEnglish = "Portuguese (Brazil)";
        localeInfo8.pluralLangCode = "pt_br";
        localeInfo8.shortName = "pt_br";
        localeInfo8.pathToFile = null;
        localeInfo8.builtIn = true;
        this.languages.add(localeInfo8);
        this.languagesDict.put(localeInfo8.shortName, localeInfo8);
        LocaleInfo localeInfo9 = new LocaleInfo();
        localeInfo9.name = "한국어";
        localeInfo9.nameEnglish = "Korean";
        localeInfo9.pluralLangCode = "ko";
        localeInfo9.shortName = "ko";
        localeInfo9.pathToFile = null;
        localeInfo9.builtIn = true;
        this.languages.add(localeInfo9);
        this.languagesDict.put(localeInfo9.shortName, localeInfo9);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$new$0();
                }
            });
        }
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            LocaleInfo localeInfo10 = this.otherLanguages.get(i);
            this.languages.add(localeInfo10);
            this.languagesDict.put(localeInfo10.getKey(), localeInfo10);
        }
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            LocaleInfo localeInfo11 = this.remoteLanguages.get(i2);
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo11.getKey());
            if (languageFromDict != null) {
                languageFromDict.pathToFile = localeInfo11.pathToFile;
                languageFromDict.version = localeInfo11.version;
                languageFromDict.baseVersion = localeInfo11.baseVersion;
                languageFromDict.serverIndex = localeInfo11.serverIndex;
                this.remoteLanguages.set(i2, languageFromDict);
            } else {
                this.languages.add(localeInfo11);
                this.languagesDict.put(localeInfo11.getKey(), localeInfo11);
            }
        }
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            LocaleInfo localeInfo12 = this.unofficialLanguages.get(i3);
            LocaleInfo languageFromDict2 = getLanguageFromDict(localeInfo12.getKey());
            if (languageFromDict2 != null) {
                languageFromDict2.pathToFile = localeInfo12.pathToFile;
                languageFromDict2.version = localeInfo12.version;
                languageFromDict2.baseVersion = localeInfo12.baseVersion;
                languageFromDict2.serverIndex = localeInfo12.serverIndex;
                this.unofficialLanguages.set(i3, languageFromDict2);
            } else {
                this.languagesDict.put(localeInfo12.getKey(), localeInfo12);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        try {
            String string = MessagesController.getGlobalMainSettings().getString("language", null);
            if (string != null) {
                localeInfo = getLanguageFromDict(string);
                if (localeInfo != null) {
                    z = true;
                }
            } else {
                localeInfo = null;
            }
            if (localeInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                localeInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo == null && (localeInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale))) == null) {
                localeInfo = getLanguageFromDict("en");
            }
            applyLanguage(localeInfo, z, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
            if (Build.VERSION.SDK_INT >= 33) {
                ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), intentFilter, 4);
            } else {
                ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), intentFilter);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LocaleController.this.lambda$new$1();
            }
        });
    }

    public static String addNbsp(String str) {
        return str.replace(' ', (char) 160);
    }

    private void addRules(String[] strArr, PluralRules pluralRules) {
        for (String str : strArr) {
            this.allRules.put(str, pluralRules);
        }
    }

    private int applyRemoteLanguage(final LocaleInfo localeInfo, String str, boolean z, final int i, final Runnable runnable) {
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack;
        RequestDelegate requestDelegate2;
        TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack2;
        ConnectionsManager connectionsManager2;
        if (localeInfo == null || !(localeInfo.isRemote() || localeInfo.isUnofficial())) {
            return 0;
        }
        FileLog.d("applyRemoteLanguage " + str + " force=" + z + " currentAccount=" + i);
        final int[] iArr = {0};
        final int[] iArr2 = {0};
        final Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                LocaleController.lambda$applyRemoteLanguage$13(iArr, iArr2, runnable);
            }
        };
        if (z) {
            patched(localeInfo.shortName);
        }
        if (localeInfo.hasBaseLang() && (str == null || str.equals(localeInfo.baseLangCode))) {
            if (localeInfo.baseVersion == 0 || z) {
                FileLog.d("applyRemoteLanguage getLangPack of base");
                TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack3 = new TLRPC.TL_langpack_getLangPack();
                tL_langpack_getLangPack3.lang_code = localeInfo.getBaseLangCode();
                iArr2[0] = iArr2[0] + 1;
                ConnectionsManager connectionsManager3 = ConnectionsManager.getInstance(i);
                requestDelegate2 = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocaleController.this.lambda$applyRemoteLanguage$17(localeInfo, i, runnable2, tLObject, tL_error);
                    }
                };
                connectionsManager2 = connectionsManager3;
                tL_langpack_getLangPack2 = tL_langpack_getLangPack3;
            } else if (localeInfo.hasBaseLang()) {
                FileLog.d("applyRemoteLanguage getDifference of base");
                TLRPC.TL_langpack_getDifference tL_langpack_getDifference = new TLRPC.TL_langpack_getDifference();
                tL_langpack_getDifference.from_version = localeInfo.baseVersion;
                tL_langpack_getDifference.lang_code = localeInfo.getBaseLangCode();
                tL_langpack_getDifference.lang_pack = "";
                iArr2[0] = iArr2[0] + 1;
                ConnectionsManager connectionsManager4 = ConnectionsManager.getInstance(i);
                requestDelegate2 = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocaleController.this.lambda$applyRemoteLanguage$15(localeInfo, i, runnable2, tLObject, tL_error);
                    }
                };
                connectionsManager2 = connectionsManager4;
                tL_langpack_getLangPack2 = tL_langpack_getDifference;
            }
            connectionsManager2.sendRequest(tL_langpack_getLangPack2, requestDelegate2, 8);
        }
        if (str != null && !str.equals(localeInfo.shortName)) {
            return 0;
        }
        if (localeInfo.version == 0 || z) {
            for (int i2 = 0; i2 < 4; i2++) {
                ConnectionsManager.setLangCode(localeInfo.getLangCode());
            }
            FileLog.d("applyRemoteLanguage getLangPack");
            TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack4 = new TLRPC.TL_langpack_getLangPack();
            tL_langpack_getLangPack4.lang_code = localeInfo.getLangCode();
            iArr2[0] = iArr2[0] + 1;
            ConnectionsManager connectionsManager5 = ConnectionsManager.getInstance(i);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocaleController.this.lambda$applyRemoteLanguage$21(localeInfo, i, runnable2, tLObject, tL_error);
                }
            };
            tL_langpack_getLangPack = tL_langpack_getLangPack4;
            connectionsManager = connectionsManager5;
        } else {
            FileLog.d("applyRemoteLanguage getDifference");
            TLRPC.TL_langpack_getDifference tL_langpack_getDifference2 = new TLRPC.TL_langpack_getDifference();
            tL_langpack_getDifference2.from_version = localeInfo.version;
            tL_langpack_getDifference2.lang_code = localeInfo.getLangCode();
            tL_langpack_getDifference2.lang_pack = "";
            iArr2[0] = iArr2[0] + 1;
            ConnectionsManager connectionsManager6 = ConnectionsManager.getInstance(i);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocaleController.this.lambda$applyRemoteLanguage$19(localeInfo, i, runnable2, tLObject, tL_error);
                }
            };
            tL_langpack_getLangPack = tL_langpack_getDifference2;
            connectionsManager = connectionsManager6;
        }
        return connectionsManager.sendRequest(tL_langpack_getLangPack, requestDelegate, 8);
    }

    private FastDateFormat createFormatter(Locale locale, String str, String str2) {
        if (str == null || str.length() == 0) {
            str = str2;
        }
        try {
            return FastDateFormat.getInstance(str, locale);
        } catch (Exception unused) {
            return FastDateFormat.getInstance(str2, locale);
        }
    }

    public static void ensureImperialSystemInit() {
        if (useImperialSystemType != null) {
            return;
        }
        int i = SharedConfig.distanceSystemType;
        if (i != 0) {
            useImperialSystemType = Boolean.valueOf(i == 2);
            return;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                String upperCase = telephonyManager.getSimCountryIso().toUpperCase();
                if (!"US".equals(upperCase) && !"GB".equals(upperCase) && !"MM".equals(upperCase) && !"LR".equals(upperCase)) {
                    r1 = false;
                }
                useImperialSystemType = Boolean.valueOf(r1);
            }
        } catch (Exception e) {
            useImperialSystemType = Boolean.FALSE;
            FileLog.e(e);
        }
    }

    private String escapeString(String str) {
        return str.contains("[CDATA") ? str : str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public static String fixNumbers(CharSequence charSequence) {
        StringBuilder sb = new StringBuilder(charSequence);
        int length = sb.length();
        for (int i = 0; i < length; i++) {
            char charAt = sb.charAt(i);
            if ((charAt < '0' || charAt > '9') && charAt != '.' && charAt != ',') {
                int i2 = 0;
                while (i2 < otherNumbers.length) {
                    int i3 = 0;
                    while (true) {
                        char[] cArr = otherNumbers[i2];
                        if (i3 >= cArr.length) {
                            break;
                        }
                        if (charAt == cArr[i3]) {
                            sb.setCharAt(i, defaultNumbers[i3]);
                            i2 = otherNumbers.length;
                            break;
                        }
                        i3++;
                    }
                    i2++;
                }
            }
        }
        return sb.toString();
    }

    public static String formatCallDuration(int i) {
        if (i <= 3600) {
            return i > 60 ? formatPluralString("Minutes", i / 60, new Object[0]) : formatPluralString("Seconds", i, new Object[0]);
        }
        String formatPluralString = formatPluralString("Hours", i / 3600, new Object[0]);
        int i2 = (i % 3600) / 60;
        if (i2 <= 0) {
            return formatPluralString;
        }
        return formatPluralString + ", " + formatPluralString("Minutes", i2, new Object[0]);
    }

    public static String formatDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? getInstance().getFormatterDay().format(new Date(j2)) : (i3 + 1 == i && i2 == i4) ? getString("Yesterday", R.string.Yesterday) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? getInstance().getFormatterDayMonth().format(new Date(j2)) : getInstance().getFormatterYear().format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long j, boolean z) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? z ? formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : formatString("TodayAtFormattedWithToday", R.string.TodayAtFormattedWithToday, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? getInstance().getFormatterDay().format(new Date(j2)) : (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getChatDate().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getChatFullDate().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateChat(long j) {
        return formatDateChat(j, false);
    }

    public static String formatDateChat(long j, boolean z) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i = calendar.get(1);
            long j2 = j * 1000;
            calendar.setTimeInMillis(j2);
            if (z) {
                if (i != calendar.get(1)) {
                }
                return getInstance().getChatDate().format(j2);
            }
            if (z || Math.abs(System.currentTimeMillis() - j2) >= 31536000000L) {
                return getInstance().getChatFullDate().format(j2);
            }
            return getInstance().getChatDate().format(j2);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDateForBan(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(1);
            calendar.setTimeInMillis(j2);
            return i == calendar.get(1) ? getInstance().getFormatterBannedUntilThisYear().format(new Date(j2)) : getInstance().getFormatterBannedUntil().format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateOnline(long j, boolean[] zArr) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            int i3 = calendar.get(11);
            calendar.setTimeInMillis(j2);
            int i4 = calendar.get(6);
            int i5 = calendar.get(1);
            int i6 = calendar.get(11);
            if (i4 == i && i2 == i5) {
                return formatString("LastSeenFormatted", R.string.LastSeenFormatted, formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))));
            }
            if (i4 + 1 != i || i2 != i5) {
                if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                    return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))));
                }
                return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))));
            }
            if (zArr == null) {
                return formatString("LastSeenFormatted", R.string.LastSeenFormatted, formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))));
            }
            zArr[0] = true;
            return (i3 > 6 || i6 <= 18 || !is24HourFormat) ? formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : formatString("LastSeenFormatted", R.string.LastSeenFormatted, getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDateTime(long j, boolean z) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4 && z) ? formatString(R.string.TodayAtFormattedWithToday, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4 && z) ? formatString(R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString(R.string.formatDateAtTime, getInstance().getChatDate().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString(R.string.formatDateAtTime, getInstance().getChatFullDate().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatDistance(float f, int i) {
        return formatDistance(f, i, null);
    }

    public static String formatDistance(float f, int i, Boolean bool) {
        ensureImperialSystemInit();
        if ((bool == null || !bool.booleanValue()) && !(bool == null && useImperialSystemType.booleanValue())) {
            if (f < 1000.0f) {
                return i != 0 ? i != 1 ? formatString("MetersShort", R.string.MetersShort, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f)))) : formatString("MetersFromYou2", R.string.MetersFromYou2, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f)))) : formatString("MetersAway2", R.string.MetersAway2, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f))));
            }
            float f2 = f % 1000.0f;
            float f3 = f / 1000.0f;
            String format = f2 == 0.0f ? String.format("%d", Integer.valueOf((int) f3)) : String.format("%.2f", Float.valueOf(f3));
            return i != 0 ? i != 1 ? formatString("KMetersShort", R.string.KMetersShort, format) : formatString("KMetersFromYou2", R.string.KMetersFromYou2, format) : formatString("KMetersAway2", R.string.KMetersAway2, format);
        }
        float f4 = f * 3.28084f;
        if (f4 < 1000.0f) {
            return i != 0 ? i != 1 ? formatString("FootsShort", R.string.FootsShort, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f4)))) : formatString("FootsFromYou", R.string.FootsFromYou, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f4)))) : formatString("FootsAway", R.string.FootsAway, String.format("%d", Integer.valueOf((int) Math.max(1.0f, f4))));
        }
        float f5 = f4 % 5280.0f;
        float f6 = f4 / 5280.0f;
        String format2 = f5 == 0.0f ? String.format("%d", Integer.valueOf((int) f6)) : String.format("%.2f", Float.valueOf(f6));
        return i != 0 ? i != 1 ? formatString("MilesShort", R.string.MilesShort, format2) : formatString("MilesFromYou", R.string.MilesFromYou, format2) : formatString("MilesAway", R.string.MilesAway, format2);
    }

    public static String formatDuration(int i) {
        if (i <= 0) {
            return formatPluralString("Seconds", 0, new Object[0]);
        }
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        StringBuilder sb = new StringBuilder();
        if (i2 > 0) {
            sb.append(formatPluralString("Hours", i2, new Object[0]));
        }
        if (i3 > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatPluralString("Minutes", i3, new Object[0]));
        }
        if (i4 > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatPluralString("Seconds", i4, new Object[0]));
        }
        return sb.toString();
    }

    public static String formatImportedDate(long j) {
        try {
            Date date = new Date(j * 1000);
            return String.format("%1$s, %2$s", getInstance().getFormatterYear().format(date), getInstance().getFormatterDay().format(date));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatJoined(long j) {
        long j2 = j * 1000;
        try {
            return formatString("ChannelOtherSubscriberJoined", R.string.ChannelOtherSubscriberJoined, Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int i) {
        int i2 = (i / 60) / 60;
        int i3 = i - (i2 * 3600);
        int i4 = i3 / 60;
        int i5 = i3 - (i4 * 60);
        if (i2 != 0) {
            return String.format("%dh", Integer.valueOf(i2 + (i4 > 30 ? 1 : 0)));
        }
        if (i4 != 0) {
            return String.format("%d", Integer.valueOf(i4 + (i5 > 30 ? 1 : 0)));
        }
        return String.format("%d", Integer.valueOf(i5));
    }

    public static String formatLocationUpdateDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            if (i3 == i && i2 == i4) {
                int currentTime = ((int) (ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() - (j2 / 1000))) / 60;
                return currentTime < 1 ? getString(R.string.LocationUpdatedJustNow) : currentTime < 60 ? formatPluralString("UpdatedMinutes", currentTime, new Object[0]) : formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))));
            }
            if (i3 + 1 == i && i2 == i4) {
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))));
            }
            if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))));
            }
            return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatNumber(long j, char c) {
        StringBuilder sb = new StringBuilder(String.format("%d", Long.valueOf(j)));
        for (int length = sb.length() - 3; length > 0; length -= 3) {
            sb.insert(length, c);
        }
        return sb.toString();
    }

    public static String formatPluralString(String str, int i, Object... objArr) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        int identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName());
        int identifier2 = ApplicationLoader.applicationContext.getResources().getIdentifier(str + "_other", "string", ApplicationLoader.applicationContext.getPackageName());
        Object[] objArr2 = new Object[objArr.length + 1];
        objArr2[0] = Integer.valueOf(i);
        System.arraycopy(objArr, 0, objArr2, 1, objArr.length);
        return formatString(str2, str + "_other", identifier, identifier2, objArr2);
    }

    public static String formatPluralStringComma(String str, int i) {
        return formatPluralStringComma(str, i, ',');
    }

    public static String formatPluralStringComma(String str, int i, char c) {
        return formatPluralStringComma(str, i, c, new Object[0]);
    }

    public static String formatPluralStringComma(String str, int i, char c, Object... objArr) {
        int i2 = 0;
        if (str != null) {
            try {
                if (str.length() != 0 && getInstance().currentPluralRules != null) {
                    String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
                    StringBuilder sb = new StringBuilder(String.format("%d", Integer.valueOf(i)));
                    for (int length = sb.length() - 3; length > 0; length -= 3) {
                        sb.insert(length, c);
                    }
                    String str3 = null;
                    String str4 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str2) : null;
                    if (str4 != null) {
                        str3 = str4;
                    } else if (BuildVars.USE_CLOUD_STRINGS) {
                        str3 = getInstance().localeValues.get(str + "_other");
                    }
                    if (str3 == null) {
                        try {
                            str3 = ApplicationLoader.applicationContext.getString(ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()));
                        } catch (Exception unused) {
                        }
                    }
                    if (str3 == null) {
                        str3 = ApplicationLoader.applicationContext.getString(ApplicationLoader.applicationContext.getResources().getIdentifier(str + "_other", "string", ApplicationLoader.applicationContext.getPackageName()));
                    }
                    String replace = str3.replace("%d", "%1$s").replace("%1$d", "%1$s");
                    int length2 = (objArr == null ? 0 : objArr.length) + 1;
                    Object[] objArr2 = new Object[length2];
                    while (i2 < length2) {
                        objArr2[i2] = i2 == 0 ? sb : objArr[i2 - 1];
                        i2++;
                    }
                    return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, replace, objArr2) : String.format(replace, objArr2);
                }
            } catch (Exception e) {
                FileLog.e(e);
                return "LOC_ERR: " + str;
            }
        }
        return "LOC_ERR:" + str;
    }

    public static String formatPluralStringComma(String str, int i, Object... objArr) {
        return formatPluralStringComma(str, i, ',', objArr);
    }

    public static String formatPluralStringSpaced(String str, int i) {
        return formatPluralStringComma(str, i, ' ');
    }

    public static String formatPluralStringSpaced(String str, int i, Object... objArr) {
        return formatPluralStringComma(str, i, ' ', objArr);
    }

    public static String formatPmEditedDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? formatString(R.string.PmEditedTodayAt, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString(R.string.PmEditedYesterdayAt, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString(R.string.PmEditedDateTimeAt, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString(R.string.PmEditedDateTimeAt, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatPmFwdDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? formatString(R.string.PmFwdOriginalTodayAt, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString(R.string.PmFwdOriginalYesterdayAt, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString(R.string.PmFwdOriginalDateTimeAt, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString(R.string.PmFwdOriginalDateTimeAt, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatPmSeenDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? formatString(R.string.PmReadTodayAt, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString(R.string.PmReadYesterdayAt, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString(R.string.PmReadDateTimeAt, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString(R.string.PmReadDateTimeAt, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatSectionDate(long j) {
        return formatYearMont(j, false);
    }

    public static String formatSeenDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            return (i3 == i && i2 == i4) ? formatString("TodayAtFormattedWithToday", R.string.TodayAtFormattedWithToday, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            long timeInMillis = calendar.getTimeInMillis();
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            long j3 = timeInMillis - j2;
            return j3 < 60000 ? getString(R.string.ShortNow) : j3 < 3600000 ? formatPluralString("ShortMinutesAgo", (int) (j3 / 60000), new Object[0]) : (i3 == i && i2 == i4) ? j3 < 43200000 ? formatPluralString("ShortHoursAgo", (int) (j3 / 3600000), new Object[0]) : getString(R.string.ShortToday) : (i3 + 1 == i && i2 == i4) ? getString(R.string.ShortYesterday) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? getInstance().getFormatterDayMonth().format(new Date(j2)) : formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortDateTime(long j) {
        long j2 = j * 1000;
        try {
            if (Math.abs(System.currentTimeMillis() - j2) < 31536000000L) {
                return getInstance().getFormatterScheduleDay().format(new Date(j2)) + ", " + getInstance().getFormatterDay().format(new Date(j2));
            }
            return getInstance().getFormatterScheduleYear().format(new Date(j2)) + ", " + getInstance().getFormatterDay().format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int i, int[] iArr) {
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        while (true) {
            int i3 = i / 1000;
            if (i3 <= 0) {
                break;
            }
            sb.append("K");
            i2 = (i % 1000) / 100;
            i = i3;
        }
        if (iArr != null) {
            double d = i;
            double d2 = i2;
            Double.isNaN(d2);
            Double.isNaN(d);
            double d3 = d + (d2 / 10.0d);
            for (int i4 = 0; i4 < sb.length(); i4++) {
                d3 *= 1000.0d;
            }
            iArr[0] = (int) d3;
        }
        return (i2 == 0 || sb.length() <= 0) ? sb.length() == 2 ? String.format(Locale.US, "%dM", Integer.valueOf(i)) : String.format(Locale.US, "%d%s", Integer.valueOf(i), sb.toString()) : sb.length() == 2 ? String.format(Locale.US, "%d.%dM", Integer.valueOf(i), Integer.valueOf(i2)) : String.format(Locale.US, "%d.%d%s", Integer.valueOf(i), Integer.valueOf(i2), sb.toString());
    }

    public static String formatSmallDateChat(long j) {
        return formatSmallDateChat(j, false);
    }

    public static String formatSmallDateChat(long j, boolean z) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i = calendar.get(1);
            long j2 = j * 1000;
            calendar.setTimeInMillis(j2);
            if (!z && i == calendar.get(1)) {
                return getInstance().getFormatterDayMonth().format(j2);
            }
            return getInstance().getFormatterDayMonth().format(j2) + ", " + calendar.get(1);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatStartsTime(long j, int i) {
        return formatStartsTime(j, i, true);
    }

    public static String formatStartsTime(long j, int i, boolean z) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i2 = calendar.get(1);
        int i3 = calendar.get(6);
        calendar.setTimeInMillis(j * 1000);
        int i4 = i2 == calendar.get(1) ? (z && calendar.get(6) == i3) ? 0 : 1 : 2;
        if (i == 1) {
            i4 += 3;
        } else if (i == 2) {
            i4 += 6;
        } else if (i == 3) {
            i4 += 9;
        } else if (i == 4) {
            i4 += 12;
        }
        return getInstance().getFormatterScheduleSend(i4).format(calendar.getTimeInMillis());
    }

    public static String formatStatusExpireDateTime(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            return (calendar.get(6) == i && i2 == calendar.get(1)) ? formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? getInstance().getFormatterScheduleDay().format(new Date(j2)) : getInstance().getChatFullDate().format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatStoryDate(long j) {
        long j2 = j * 1000;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            int i2 = calendar.get(1);
            long timeInMillis = calendar.getTimeInMillis();
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(6);
            int i4 = calendar.get(1);
            long j3 = timeInMillis - j2;
            if (j3 < 60000) {
                return getString(R.string.RightNow);
            }
            if (j3 >= 3600000) {
                return (i3 == i && i2 == i4) ? formatString("TodayAtFormattedWithToday", R.string.TodayAtFormattedWithToday, getInstance().getFormatterDay().format(new Date(j2))) : (i3 + 1 == i && i2 == i4) ? formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().getFormatterDay().format(new Date(j2))) : Math.abs(System.currentTimeMillis() - j2) < 31536000000L ? formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterDayMonth().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2))) : formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().getFormatterYear().format(new Date(j2)), getInstance().getFormatterDay().format(new Date(j2)));
            }
            int i5 = (int) (j3 / 60000);
            return formatPluralString("MinutesAgo", i5, Integer.valueOf(i5));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static String formatString(int i, Object... objArr) {
        String str = resourcesCacheMap.get(Integer.valueOf(i));
        if (str == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(i);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(i);
            hashMap.put(valueOf, resourceEntryName);
            str = resourceEntryName;
        }
        return formatString(str, i, objArr);
    }

    public static String formatString(String str, int i, Object... objArr) {
        return formatString(str, null, i, 0, objArr);
    }

    public static String formatString(String str, String str2, int i, int i2, Object... objArr) {
        String string;
        try {
            String str3 = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(str) : null;
            if (str3 == null) {
                if (BuildVars.USE_CLOUD_STRINGS && str2 != null) {
                    str3 = getInstance().localeValues.get(str2);
                }
                if (str3 == null) {
                    try {
                        if (i != 0) {
                            try {
                                string = ApplicationLoader.applicationContext.getString(i);
                            } catch (Exception unused) {
                                if (i2 != 0) {
                                    str3 = ApplicationLoader.applicationContext.getString(i2);
                                }
                            }
                        } else if (i2 != 0) {
                            string = ApplicationLoader.applicationContext.getString(i2);
                        }
                        str3 = string;
                    } catch (Exception unused2) {
                    }
                }
            }
            return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, str3, objArr) : String.format(str3, objArr);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatStringSimple(String str, Object... objArr) {
        try {
            return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, str, objArr) : String.format(str, objArr);
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatTTLString(int i) {
        if (i < 60) {
            return formatPluralString("Seconds", i, new Object[0]);
        }
        if (i < 3600) {
            return formatPluralString("Minutes", i / 60, new Object[0]);
        }
        if (i < 86400) {
            return formatPluralString("Hours", (i / 60) / 60, new Object[0]);
        }
        if (i < 604800) {
            return formatPluralString("Days", ((i / 60) / 60) / 24, new Object[0]);
        }
        if (i >= 2678400) {
            return formatPluralString("Months", (((i / 60) / 60) / 24) / 30, new Object[0]);
        }
        int i2 = ((i / 60) / 60) / 24;
        return i % 7 == 0 ? formatPluralString("Weeks", i2 / 7, new Object[0]) : String.format("%s %s", formatPluralString("Weeks", i2 / 7, new Object[0]), formatPluralString("Days", i2 % 7, new Object[0]));
    }

    public static String formatUserStatus(int i, TLRPC.User user) {
        return formatUserStatus(i, user, null);
    }

    public static String formatUserStatus(int i, TLRPC.User user, boolean[] zArr) {
        return formatUserStatus(i, user, zArr, null);
    }

    public static String formatUserStatus(int i, TLRPC.User user, boolean[] zArr, boolean[] zArr2) {
        TLRPC.UserStatus userStatus;
        TLRPC.UserStatus userStatus2;
        TLRPC.UserStatus userStatus3;
        int i2;
        if (user != null && (userStatus3 = user.status) != null && userStatus3.expires == 0) {
            if (userStatus3 instanceof TLRPC.TL_userStatusRecently) {
                i2 = userStatus3.by_me ? -1000 : -100;
            } else if (userStatus3 instanceof TLRPC.TL_userStatusLastWeek) {
                i2 = userStatus3.by_me ? -1001 : -101;
            } else if (userStatus3 instanceof TLRPC.TL_userStatusLastMonth) {
                i2 = userStatus3.by_me ? -1002 : -102;
            }
            userStatus3.expires = i2;
        }
        if (user != null && (userStatus2 = user.status) != null && userStatus2.expires <= 0 && MessagesController.getInstance(i).onlinePrivacy.containsKey(Long.valueOf(user.id))) {
            if (zArr != null) {
                zArr[0] = true;
            }
            return getString("Online", R.string.Online);
        }
        if (user == null || (userStatus = user.status) == null || userStatus.expires == 0 || UserObject.isDeleted(user) || (user instanceof TLRPC.TL_userEmpty)) {
            return getString("ALongTimeAgo", R.string.ALongTimeAgo);
        }
        int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
        int i3 = user.status.expires;
        if (i3 <= currentTime) {
            return i3 == -1 ? getString("Invisible", R.string.Invisible) : (i3 == -100 || i3 == -1000) ? getString("Lately", R.string.Lately) : (i3 == -101 || i3 == -1001) ? getString("WithinAWeek", R.string.WithinAWeek) : (i3 == -102 || i3 == -1002) ? getString("WithinAMonth", R.string.WithinAMonth) : formatDateOnline(i3, zArr2);
        }
        if (zArr != null) {
            zArr[0] = true;
        }
        return getString("Online", R.string.Online);
    }

    public static String formatYearMont(long j, boolean z) {
        long j2 = 1000 * j;
        try {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i2 = calendar.get(1);
            int i3 = calendar.get(2);
            String[] strArr = {getString(R.string.January), getString(R.string.February), getString(R.string.March), getString(R.string.April), getString(R.string.May), getString(R.string.June), getString(R.string.July), getString(R.string.August), getString(R.string.September), getString(R.string.October), getString(R.string.November), getString(R.string.December)};
            if (i == i2 && !z) {
                return strArr[i3];
            }
            return strArr[i3] + " " + i2;
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    public static int getCurrencyExpDivider(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 65726:
                if (str.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (str.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (str.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (str.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (str.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (str.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (str.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (str.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (str.equals("IQD")) {
                    c = '\b';
                    break;
                }
                break;
            case 72801:
                if (str.equals("ISK")) {
                    c = '\t';
                    break;
                }
                break;
            case 73631:
                if (str.equals("JOD")) {
                    c = '\n';
                    break;
                }
                break;
            case 73683:
                if (str.equals("JPY")) {
                    c = 11;
                    break;
                }
                break;
            case 74532:
                if (str.equals("KMF")) {
                    c = '\f';
                    break;
                }
                break;
            case 74704:
                if (str.equals("KRW")) {
                    c = '\r';
                    break;
                }
                break;
            case 74840:
                if (str.equals("KWD")) {
                    c = 14;
                    break;
                }
                break;
            case 75863:
                if (str.equals("LYD")) {
                    c = 15;
                    break;
                }
                break;
            case 76263:
                if (str.equals("MGA")) {
                    c = 16;
                    break;
                }
                break;
            case 76618:
                if (str.equals("MRO")) {
                    c = 17;
                    break;
                }
                break;
            case 78388:
                if (str.equals("OMR")) {
                    c = 18;
                    break;
                }
                break;
            case 79710:
                if (str.equals("PYG")) {
                    c = 19;
                    break;
                }
                break;
            case 81569:
                if (str.equals("RWF")) {
                    c = 20;
                    break;
                }
                break;
            case 83210:
                if (str.equals("TND")) {
                    c = 21;
                    break;
                }
                break;
            case 83974:
                if (str.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (str.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (str.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (str.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (str.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (str.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (str.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case '\b':
            case '\n':
            case 14:
            case 15:
            case 18:
            case 21:
                return 1000;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\t':
            case 11:
            case '\f':
            case '\r':
            case 16:
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
                return 1;
            case 3:
                return 10000;
            case 17:
                return 10;
            default:
                return 100;
        }
    }

    public static String getCurrentLanguageName() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        return (localeInfo == null || TextUtils.isEmpty(localeInfo.name)) ? getString("LanguageName", R.string.LanguageName) : localeInfo.name;
    }

    public static LocaleController getInstance() {
        LocaleController localeController = Instance;
        if (localeController == null) {
            synchronized (LocaleController.class) {
                try {
                    localeController = Instance;
                    if (localeController == null) {
                        localeController = new LocaleController();
                        Instance = localeController;
                    }
                } finally {
                }
            }
        }
        return localeController;
    }

    public static String getLanguageFlag(String str) {
        if (str.length() != 2 || str.equals("YL")) {
            return null;
        }
        if (str.equals("FT")) {
            return "🏴\u200d☠️";
        }
        if (str.equals("XG")) {
            return "🛰";
        }
        if (str.equals("XV")) {
            return "🌍";
        }
        char[] charArray = str.toCharArray();
        return new String(new char[]{CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(charArray[0] + 61861), CharacterCompat.highSurrogate(127397), CharacterCompat.lowSurrogate(charArray[1] + 61861)});
    }

    public static String getLocaleAlias(String str) {
        if (str == null) {
            return null;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case 3325:
                if (str.equals("he")) {
                    c = 0;
                    break;
                }
                break;
            case 3355:
                if (str.equals("id")) {
                    c = 1;
                    break;
                }
                break;
            case 3365:
                if (str.equals("in")) {
                    c = 2;
                    break;
                }
                break;
            case 3374:
                if (str.equals("iw")) {
                    c = 3;
                    break;
                }
                break;
            case 3391:
                if (str.equals("ji")) {
                    c = 4;
                    break;
                }
                break;
            case 3404:
                if (str.equals("jv")) {
                    c = 5;
                    break;
                }
                break;
            case 3405:
                if (str.equals("jw")) {
                    c = 6;
                    break;
                }
                break;
            case 3508:
                if (str.equals("nb")) {
                    c = 7;
                    break;
                }
                break;
            case 3521:
                if (str.equals("no")) {
                    c = '\b';
                    break;
                }
                break;
            case 3704:
                if (str.equals("tl")) {
                    c = '\t';
                    break;
                }
                break;
            case 3856:
                if (str.equals("yi")) {
                    c = '\n';
                    break;
                }
                break;
            case 101385:
                if (str.equals("fil")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "iw";
            case 1:
                return "in";
            case 2:
                return "id";
            case 3:
                return "he";
            case 4:
                return "yi";
            case 5:
                return "jw";
            case 6:
                return "jv";
            case 7:
                return "no";
            case '\b':
                return "nb";
            case '\t':
                return "fil";
            case '\n':
                return "ji";
            case 11:
                return "tl";
            default:
                return null;
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean z) {
        FileInputStream fileInputStream;
        this.reloadLastFile = false;
        FileLog.d("getLocaleFileStrings: reloadLastFile = false");
        FileInputStream fileInputStream2 = null;
        try {
            try {
                if (!file.exists()) {
                    return new HashMap<>();
                }
                HashMap<String, String> hashMap = new HashMap<>(10000);
                XmlPullParser newPullParser = Xml.newPullParser();
                fileInputStream = new FileInputStream(file);
                try {
                    newPullParser.setInput(fileInputStream, "UTF-8");
                    String str = null;
                    String str2 = null;
                    String str3 = null;
                    for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                        if (eventType == 2) {
                            str2 = newPullParser.getName();
                            if (newPullParser.getAttributeCount() > 0) {
                                str = newPullParser.getAttributeValue(0);
                            }
                        } else if (eventType == 4) {
                            if (str != null && (str3 = newPullParser.getText()) != null) {
                                String trim = str3.trim();
                                if (z) {
                                    str3 = trim.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                                } else {
                                    String replace = trim.replace("\\n", "\n").replace("\\", "");
                                    str3 = replace.replace("&lt;", "<");
                                    if (!this.reloadLastFile && !str3.equals(replace)) {
                                        this.reloadLastFile = true;
                                        FileLog.d("getLocaleFileStrings: value != old, reloadLastFile = true;");
                                    }
                                }
                            }
                        } else if (eventType == 3) {
                            str = null;
                            str2 = null;
                            str3 = null;
                        }
                        if (str2 != null && str2.equals("string") && str3 != null && str != null && str3.length() != 0 && str.length() != 0) {
                            hashMap.put(str, str3);
                            str = null;
                            str2 = null;
                            str3 = null;
                        }
                    }
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    return hashMap;
                } catch (Exception e2) {
                    e = e2;
                    fileInputStream2 = fileInputStream;
                    try {
                        File file2 = new File(ApplicationLoader.getFilesDirFixed(), "malformed_locales/");
                        file2.mkdirs();
                        AndroidUtilities.copyFile(file, new File(file2, file.getName()));
                    } catch (Exception unused) {
                    }
                    FileLog.e(e);
                    FileLog.d("getLocaleFileStrings: error, reloadLastFile = true;");
                    this.reloadLastFile = true;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Exception e3) {
                            FileLog.e(e3);
                        }
                    }
                    return new HashMap<>();
                } catch (Throwable th) {
                    th = th;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e4) {
                            FileLog.e(e4);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = null;
            }
        } catch (Exception e5) {
            e = e5;
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('_');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        if (localeInfo != null) {
            return localeInfo.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('-');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        return getString(str2, str + "_other", ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName()), ApplicationLoader.applicationContext.getResources().getIdentifier(str + "_other", "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String getServerString(String str) {
        int identifier;
        String str2 = getInstance().localeValues.get(str);
        return (str2 != null || (identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName())) == 0) ? str2 : ApplicationLoader.applicationContext.getString(identifier);
    }

    public static String getString(int i) {
        String str = resourcesCacheMap.get(Integer.valueOf(i));
        if (str == null) {
            HashMap<Integer, String> hashMap = resourcesCacheMap;
            Integer valueOf = Integer.valueOf(i);
            String resourceEntryName = ApplicationLoader.applicationContext.getResources().getResourceEntryName(i);
            hashMap.put(valueOf, resourceEntryName);
            str = resourceEntryName;
        }
        return getString(str, i);
    }

    @Deprecated
    public static String getString(String str) {
        if (!TextUtils.isEmpty(str)) {
            int stringResId = getStringResId(str);
            return stringResId != 0 ? getString(str, stringResId) : getServerString(str);
        }
        return "LOC_ERR:" + str;
    }

    @Deprecated
    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    @Deprecated
    public static String getString(String str, String str2, int i) {
        return getInstance().getStringInternal(str, str2, 0, i);
    }

    @Deprecated
    public static String getString(String str, String str2, int i, int i2) {
        return getInstance().getStringInternal(str, str2, i, i2);
    }

    private String getStringInternal(String str, int i) {
        return getStringInternal(str, null, 0, i);
    }

    private String getStringInternal(String str, String str2, int i, int i2) {
        String str3 = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(str) : null;
        if (str3 == null) {
            if (BuildVars.USE_CLOUD_STRINGS && str2 != null) {
                str3 = this.localeValues.get(str2);
            }
            if (str3 == null) {
                try {
                    str3 = ApplicationLoader.applicationContext.getString(i2);
                } catch (Exception e) {
                    if (i != 0) {
                        try {
                            str3 = ApplicationLoader.applicationContext.getString(i);
                        } catch (Exception unused) {
                        }
                    }
                    FileLog.e(e);
                }
            }
        }
        if (str3 != null) {
            return str3;
        }
        return "LOC_ERR:" + str;
    }

    public static String getStringParamForNumber(int i) {
        return getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
    }

    public static int getStringResId(String str) {
        return ApplicationLoader.applicationContext.getResources().getIdentifier(str, "string", ApplicationLoader.applicationContext.getPackageName());
    }

    public static String getSystemLocaleStringIso639() {
        Locale systemDefaultLocale = getInstance().getSystemDefaultLocale();
        if (systemDefaultLocale == null) {
            return "en";
        }
        String language = systemDefaultLocale.getLanguage();
        String country = systemDefaultLocale.getCountry();
        String variant = systemDefaultLocale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('-');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getTimeZoneName(String str, boolean z) {
        TimeZone timeZone = DesugarTimeZone.getTimeZone(str);
        if (timeZone == null) {
            return "";
        }
        String displayName = timeZone.getDisplayName(true, 0, getInstance().getCurrentLocale());
        if (!z) {
            return displayName;
        }
        String displayName2 = timeZone.getDisplayName(true, 1, getInstance().getCurrentLocale());
        if (TextUtils.equals(displayName2, displayName)) {
            return displayName;
        }
        return displayName2 + ", " + displayName;
    }

    public static boolean getUseImperialSystemType() {
        ensureImperialSystemInit();
        return useImperialSystemType.booleanValue();
    }

    public static boolean isRTLCharacter(char c) {
        return Character.getDirectionality(c) == 1 || Character.getDirectionality(c) == 2 || Character.getDirectionality(c) == 16 || Character.getDirectionality(c) == 17;
    }

    public void lambda$applyLanguage$7(LocaleInfo localeInfo, int i, Runnable runnable) {
        applyRemoteLanguage(localeInfo, null, true, i, runnable);
    }

    public void lambda$applyLanguage$8(int i) {
        reloadCurrentRemoteLocale(i, null, true, null);
    }

    public static void lambda$applyLanguage$9() {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadInterface, new Object[0]);
    }

    public static void lambda$applyRemoteLanguage$13(int[] iArr, int[] iArr2, Runnable runnable) {
        int i = iArr[0] + 1;
        iArr[0] = i;
        if (i < iArr2[0] || runnable == null) {
            return;
        }
        runnable.run();
    }

    public void lambda$applyRemoteLanguage$15(final LocaleInfo localeInfo, final int i, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$14(localeInfo, tLObject, i, runnable);
                }
            });
        }
    }

    public void lambda$applyRemoteLanguage$17(final LocaleInfo localeInfo, final int i, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$16(localeInfo, tLObject, i, runnable);
                }
            });
        }
    }

    public void lambda$applyRemoteLanguage$19(final LocaleInfo localeInfo, final int i, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$18(localeInfo, tLObject, i, runnable);
                }
            });
        }
    }

    public void lambda$applyRemoteLanguage$21(final LocaleInfo localeInfo, final int i, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$applyRemoteLanguage$20(localeInfo, tLObject, i, runnable);
                }
            });
        }
    }

    public void lambda$checkForcePatchLangpack$5(String str, Runnable runnable) {
        MessagesController.getGlobalMainSettings().edit().putBoolean("langpack_patched" + str, true).apply();
        if (runnable != null) {
            runnable.run();
        }
        this.patching = false;
    }

    public void lambda$checkForcePatchLangpack$6(final String str, final Runnable runnable) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LocaleController.this.lambda$checkForcePatchLangpack$5(str, runnable);
            }
        });
    }

    public void lambda$checkPatchLangpack$4(int i) {
        reloadCurrentRemoteLocale(i, null, true, null);
    }

    public void lambda$checkUpdateForCurrentRemoteLocale$2(int i) {
        this.checkingUpdateForCurrentRemoteLocale = false;
        checkPatchLangpack(i);
    }

    public void lambda$checkUpdateForCurrentRemoteLocale$3(int i) {
        this.checkingUpdateForCurrentRemoteLocale = false;
        checkPatchLangpack(i);
    }

    public void lambda$loadRemoteLanguages$11(TLObject tLObject, boolean z, int i) {
        this.loadingRemoteLanguages = false;
        TLRPC.Vector vector = (TLRPC.Vector) tLObject;
        int size = this.remoteLanguages.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.remoteLanguages.get(i2).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = vector.objects.size();
        for (int i3 = 0; i3 < size2; i3++) {
            TLRPC.TL_langPackLanguage tL_langPackLanguage = (TLRPC.TL_langPackLanguage) vector.objects.get(i3);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + tL_langPackLanguage.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = tL_langPackLanguage.name;
            localeInfo.name = tL_langPackLanguage.native_name;
            localeInfo.shortName = tL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
            String str = tL_langPackLanguage.base_lang_code;
            localeInfo.baseLangCode = str != null ? str.replace('-', '_').toLowerCase() : "";
            localeInfo.pluralLangCode = tL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
            localeInfo.isRtl = tL_langPackLanguage.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = i3;
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                languageFromDict.nameEnglish = localeInfo.nameEnglish;
                languageFromDict.name = localeInfo.name;
                languageFromDict.baseLangCode = localeInfo.baseLangCode;
                languageFromDict.pluralLangCode = localeInfo.pluralLangCode;
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.serverIndex = localeInfo.serverIndex;
                localeInfo = languageFromDict;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int i4 = 0;
        while (i4 < this.remoteLanguages.size()) {
            LocaleInfo localeInfo2 = this.remoteLanguages.get(i4);
            if (localeInfo2.serverIndex == Integer.MAX_VALUE && localeInfo2 != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + localeInfo2.getKey());
                }
                this.remoteLanguages.remove(i4);
                this.remoteLanguagesDict.remove(localeInfo2.getKey());
                this.languages.remove(localeInfo2);
                this.languagesDict.remove(localeInfo2.getKey());
                i4--;
            }
            i4++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.suggestedLangpack, new Object[0]);
        if (z) {
            applyLanguage(this.currentLocaleInfo, true, false, i);
        }
    }

    public void lambda$loadRemoteLanguages$12(final boolean z, final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$loadRemoteLanguages$11(tLObject, z, i);
                }
            });
        }
    }

    public void lambda$new$0() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    public void lambda$new$1() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public void lambda$saveRemoteLocaleStrings$10(int i, LocaleInfo localeInfo, TLRPC.TL_langPackDifference tL_langPackDifference, HashMap hashMap, Runnable runnable) {
        if (i == 0) {
            localeInfo.version = tL_langPackDifference.version;
        } else {
            localeInfo.baseVersion = tL_langPackDifference.version;
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                String[] split = (!TextUtils.isEmpty(localeInfo.pluralLangCode) ? localeInfo.pluralLangCode : !TextUtils.isEmpty(localeInfo.baseLangCode) ? localeInfo.baseLangCode : localeInfo.shortName).split("_");
                Locale locale = split.length == 1 ? new Locale(split[0]) : new Locale(split[0], split[1]);
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putString("language", localeInfo.getKey());
                edit.commit();
                this.localeValues = hashMap;
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration configuration = new Configuration();
                configuration.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                RestrictedLanguagesSelectActivity.invalidateRestrictedLanguages();
            } else {
                FileLog.d("saveRemoteLocaleStrings: currentLocaleInfo != localeInfo, do nothing");
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadInterface, new Object[0]);
        if (runnable != null) {
            runnable.run();
        }
    }

    private void loadOtherLanguages() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String string = sharedPreferences.getString("locales", null);
        if (!TextUtils.isEmpty(string)) {
            for (String str : string.split("&")) {
                LocaleInfo createWithString = LocaleInfo.createWithString(str);
                if (createWithString != null) {
                    this.otherLanguages.add(createWithString);
                }
            }
        }
        String string2 = sharedPreferences.getString("remote", null);
        if (!TextUtils.isEmpty(string2)) {
            for (String str2 : string2.split("&")) {
                LocaleInfo createWithString2 = LocaleInfo.createWithString(str2);
                createWithString2.shortName = createWithString2.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(createWithString2.getKey())) {
                    this.remoteLanguages.add(createWithString2);
                    this.remoteLanguagesDict.put(createWithString2.getKey(), createWithString2);
                }
            }
        }
        String string3 = sharedPreferences.getString("unofficial", null);
        if (TextUtils.isEmpty(string3)) {
            return;
        }
        for (String str3 : string3.split("&")) {
            LocaleInfo createWithString3 = LocaleInfo.createWithString(str3);
            if (createWithString3 != null) {
                createWithString3.shortName = createWithString3.shortName.replace("-", "_");
                this.unofficialLanguages.add(createWithString3);
            }
        }
    }

    public static String nullable(String str) {
        if (str == null || str.startsWith("LOC_ERR")) {
            return null;
        }
        return str;
    }

    private void patched(String str) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set as patched " + str + " langpack");
        }
        MessagesController.getGlobalMainSettings().edit().putBoolean("lngpack_patched_" + str, true).apply();
    }

    public static void resetImperialSystemType() {
        useImperialSystemType = null;
    }

    private void saveOtherLanguages() {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            String saveString = this.otherLanguages.get(i).getSaveString();
            if (saveString != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString);
            }
        }
        edit.putString("locales", sb.toString());
        sb.setLength(0);
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            String saveString2 = this.remoteLanguages.get(i2).getSaveString();
            if (saveString2 != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString2);
            }
        }
        edit.putString("remote", sb.toString());
        sb.setLength(0);
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            String saveString3 = this.unofficialLanguages.get(i3).getSaveString();
            if (saveString3 != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString3);
            }
        }
        edit.putString("unofficial", sb.toString());
        edit.commit();
    }

    private boolean shouldReinstallLangpack(String str) {
        int calculateTranslatedCount;
        int i = MessagesController.getInstance(UserConfig.selectedAccount).checkResetLangpack;
        if (i <= 0) {
            return false;
        }
        if (MessagesController.getGlobalMainSettings().getBoolean("lngpack_patched_" + str, false) || (calculateTranslatedCount = calculateTranslatedCount(this.localeValues)) >= i) {
            return false;
        }
        FileLog.e("reinstalling " + str + " langpack because of patch (" + calculateTranslatedCount + " keys, must be at least " + i + ")");
        patched(str);
        return true;
    }

    public static String stringForMessageListDate(long j) {
        long j2 = j * 1000;
        try {
            if (Math.abs(System.currentTimeMillis() - j2) >= 31536000000L) {
                return getInstance().getFormatterYear().format(new Date(j2));
            }
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(6);
            calendar.setTimeInMillis(j2);
            int i2 = calendar.get(6) - i;
            if (i2 != 0 && (i2 != -1 || System.currentTimeMillis() - j2 >= 28800000)) {
                return (i2 <= -7 || i2 > -1) ? getInstance().getFormatterDayMonth().format(new Date(j2)) : getInstance().getFormatterWeek().format(new Date(j2));
            }
            return getInstance().getFormatterDay().format(new Date(j2));
        } catch (Exception e) {
            FileLog.e(e);
            return "LOC_ERR";
        }
    }

    private String stringForQuantity(int i) {
        return i != 1 ? i != 2 ? i != 4 ? i != 8 ? i != 16 ? "other" : "many" : "few" : "two" : "one" : "zero";
    }

    public int applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        return applyLanguage(localeInfo, z, z2, false, false, i, null);
    }

    public int applyLanguage(final org.telegram.messenger.LocaleController.LocaleInfo r22, boolean r23, boolean r24, boolean r25, boolean r26, final int r27, final java.lang.Runnable r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.applyLanguage(org.telegram.messenger.LocaleController$LocaleInfo, boolean, boolean, boolean, boolean, int, java.lang.Runnable):int");
    }

    public boolean applyLanguageFile(File file, int i) {
        try {
            HashMap<String, String> localeFileStrings = getLocaleFileStrings(file);
            String str = localeFileStrings.get("LanguageName");
            String str2 = localeFileStrings.get("LanguageNameInEnglish");
            String str3 = localeFileStrings.get("LanguageCode");
            if (str != null && str.length() > 0 && str2 != null && str2.length() > 0 && str3 != null && str3.length() > 0 && !str.contains("&") && !str.contains("|") && !str2.contains("&") && !str2.contains("|") && !str3.contains("&") && !str3.contains("|") && !str3.contains("/") && !str3.contains("\\")) {
                File file2 = new File(ApplicationLoader.getFilesDirFixed(), str3 + ".xml");
                if (!AndroidUtilities.copyFile(file, file2)) {
                    return false;
                }
                LocaleInfo languageFromDict = getLanguageFromDict("local_" + str3.toLowerCase());
                if (languageFromDict == null) {
                    languageFromDict = new LocaleInfo();
                    languageFromDict.name = str;
                    languageFromDict.nameEnglish = str2;
                    String lowerCase = str3.toLowerCase();
                    languageFromDict.shortName = lowerCase;
                    languageFromDict.pluralLangCode = lowerCase;
                    languageFromDict.pathToFile = file2.getAbsolutePath();
                    this.languages.add(languageFromDict);
                    this.languagesDict.put(languageFromDict.getKey(), languageFromDict);
                    this.otherLanguages.add(languageFromDict);
                    saveOtherLanguages();
                }
                LocaleInfo localeInfo = languageFromDict;
                this.localeValues = localeFileStrings;
                applyLanguage(localeInfo, true, false, true, false, i, null);
                return true;
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public int calculateTranslatedCount(java.util.HashMap<java.lang.String, java.lang.String> r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocaleController.calculateTranslatedCount(java.util.HashMap):int");
    }

    public void checkForcePatchLangpack(int i, final Runnable runnable) {
        final String currentLanguageName = getCurrentLanguageName();
        if (MessagesController.getInstance(i).checkResetLangpack > 0) {
            if (MessagesController.getGlobalMainSettings().getBoolean("langpack_patched" + currentLanguageName, false) || this.patching) {
                return;
            }
            this.patching = true;
            reloadCurrentRemoteLocale(i, null, true, new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$checkForcePatchLangpack$6(currentLanguageName, runnable);
                }
            });
        }
    }

    public void checkPatchLangpack(final int i) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo == null || this.checkingUpdateForCurrentRemoteLocale || !shouldReinstallLangpack(localeInfo.shortName)) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("reload locale because locale file is not enough");
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LocaleController.this.lambda$checkPatchLangpack$4(i);
            }
        });
    }

    public void checkUpdateForCurrentRemoteLocale(final int i, int i2, int i3) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo != null) {
            if (localeInfo.isRemote() || this.currentLocaleInfo.isUnofficial()) {
                if (this.currentLocaleInfo.hasBaseLang()) {
                    LocaleInfo localeInfo2 = this.currentLocaleInfo;
                    if (localeInfo2.baseVersion < i3) {
                        this.checkingUpdateForCurrentRemoteLocale = true;
                        applyRemoteLanguage(localeInfo2, localeInfo2.baseLangCode, false, i, new Runnable() {
                            @Override
                            public final void run() {
                                LocaleController.this.lambda$checkUpdateForCurrentRemoteLocale$2(i);
                            }
                        });
                    }
                }
                LocaleInfo localeInfo3 = this.currentLocaleInfo;
                if (localeInfo3.version < i2) {
                    this.checkingUpdateForCurrentRemoteLocale = true;
                    applyRemoteLanguage(localeInfo3, localeInfo3.shortName, false, i, new Runnable() {
                        @Override
                        public final void run() {
                            LocaleController.this.lambda$checkUpdateForCurrentRemoteLocale$3(i);
                        }
                    });
                }
            }
        }
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int i) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo languageFromDict = this.systemDefaultLocale.getLanguage() != null ? getLanguageFromDict(this.systemDefaultLocale.getLanguage()) : null;
            if (languageFromDict == null) {
                languageFromDict = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (languageFromDict == null) {
                languageFromDict = getLanguageFromDict("en");
            }
            applyLanguage(languageFromDict, true, false, i);
        }
        this.unofficialLanguages.remove(localeInfo);
        this.remoteLanguages.remove(localeInfo);
        this.remoteLanguagesDict.remove(localeInfo.getKey());
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.getKey());
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    public String formatCurrencyDecimalString(long j, String str, boolean z) {
        String str2;
        double d;
        String upperCase = str.toUpperCase();
        long abs = Math.abs(j);
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = '\b';
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = '\t';
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = '\n';
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 11;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = '\f';
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = '\r';
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 14;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 15;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 16;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 17;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 18;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 19;
                    break;
                }
                break;
            case 79710:
                if (upperCase.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (upperCase.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (upperCase.equals("TND")) {
                    c = 22;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 23;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 24;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 25;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 26;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 27;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 28;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 29;
                    break;
                }
                break;
        }
        str2 = " %.0f";
        switch (c) {
            case 0:
            case '\b':
            case 11:
            case 15:
            case 16:
            case 19:
            case 22:
                double d2 = abs;
                Double.isNaN(d2);
                d = d2 / 1000.0d;
                str2 = " %.3f";
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\n':
            case '\f':
            case '\r':
            case 14:
            case 17:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
                d = abs;
                break;
            case 3:
                double d3 = abs;
                Double.isNaN(d3);
                d = d3 / 10000.0d;
                str2 = " %.4f";
                break;
            case '\t':
                double d4 = ((float) abs) / 100.0f;
                str2 = abs % 100 != 0 ? " %.2f" : " %.0f";
                d = d4;
                break;
            case 18:
                double d5 = abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                str2 = " %.2f";
                break;
        }
        Locale locale = Locale.US;
        if (!z) {
            upperCase = "" + str2;
        }
        return String.format(locale, upperCase, Double.valueOf(d)).trim();
    }

    public String formatCurrencyString(long j, String str) {
        return formatCurrencyString(j, true, true, false, str);
    }

    public String formatCurrencyString(long j, boolean z, boolean z2, boolean z3, String str) {
        double d;
        int length;
        String upperCase = str.toUpperCase();
        boolean z4 = j < 0;
        long abs = Math.abs(j);
        Currency currency = Currency.getInstance(upperCase);
        upperCase.hashCode();
        char c = 65535;
        switch (upperCase.hashCode()) {
            case 65726:
                if (upperCase.equals("BHD")) {
                    c = 0;
                    break;
                }
                break;
            case 65759:
                if (upperCase.equals("BIF")) {
                    c = 1;
                    break;
                }
                break;
            case 66267:
                if (upperCase.equals("BYR")) {
                    c = 2;
                    break;
                }
                break;
            case 66813:
                if (upperCase.equals("CLF")) {
                    c = 3;
                    break;
                }
                break;
            case 66823:
                if (upperCase.equals("CLP")) {
                    c = 4;
                    break;
                }
                break;
            case 67122:
                if (upperCase.equals("CVE")) {
                    c = 5;
                    break;
                }
                break;
            case 67712:
                if (upperCase.equals("DJF")) {
                    c = 6;
                    break;
                }
                break;
            case 70719:
                if (upperCase.equals("GNF")) {
                    c = 7;
                    break;
                }
                break;
            case 72732:
                if (upperCase.equals("IQD")) {
                    c = '\b';
                    break;
                }
                break;
            case 72777:
                if (upperCase.equals("IRR")) {
                    c = '\t';
                    break;
                }
                break;
            case 72801:
                if (upperCase.equals("ISK")) {
                    c = '\n';
                    break;
                }
                break;
            case 73631:
                if (upperCase.equals("JOD")) {
                    c = 11;
                    break;
                }
                break;
            case 73683:
                if (upperCase.equals("JPY")) {
                    c = '\f';
                    break;
                }
                break;
            case 74532:
                if (upperCase.equals("KMF")) {
                    c = '\r';
                    break;
                }
                break;
            case 74704:
                if (upperCase.equals("KRW")) {
                    c = 14;
                    break;
                }
                break;
            case 74840:
                if (upperCase.equals("KWD")) {
                    c = 15;
                    break;
                }
                break;
            case 75863:
                if (upperCase.equals("LYD")) {
                    c = 16;
                    break;
                }
                break;
            case 76263:
                if (upperCase.equals("MGA")) {
                    c = 17;
                    break;
                }
                break;
            case 76618:
                if (upperCase.equals("MRO")) {
                    c = 18;
                    break;
                }
                break;
            case 78388:
                if (upperCase.equals("OMR")) {
                    c = 19;
                    break;
                }
                break;
            case 79710:
                if (upperCase.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (upperCase.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (upperCase.equals("TND")) {
                    c = 22;
                    break;
                }
                break;
            case 83974:
                if (upperCase.equals("UGX")) {
                    c = 23;
                    break;
                }
                break;
            case 84517:
                if (upperCase.equals("UYI")) {
                    c = 24;
                    break;
                }
                break;
            case 85132:
                if (upperCase.equals("VND")) {
                    c = 25;
                    break;
                }
                break;
            case 85367:
                if (upperCase.equals("VUV")) {
                    c = 26;
                    break;
                }
                break;
            case 86653:
                if (upperCase.equals("XAF")) {
                    c = 27;
                    break;
                }
                break;
            case 87087:
                if (upperCase.equals("XOF")) {
                    c = 28;
                    break;
                }
                break;
            case 87118:
                if (upperCase.equals("XPF")) {
                    c = 29;
                    break;
                }
                break;
            case 87254:
                if (upperCase.equals("XTR")) {
                    c = 30;
                    break;
                }
                break;
        }
        String str2 = " %.2f";
        switch (c) {
            case 0:
            case '\b':
            case 11:
            case 15:
            case 16:
            case 19:
            case 22:
                double d2 = abs;
                Double.isNaN(d2);
                d = d2 / 1000.0d;
                str2 = " %.3f";
                break;
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\n':
            case '\f':
            case '\r':
            case 14:
            case 17:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                d = abs;
                str2 = " %.0f";
                break;
            case 3:
                double d3 = abs;
                Double.isNaN(d3);
                d = d3 / 10000.0d;
                str2 = " %.4f";
                break;
            case '\t':
                double d4 = ((float) abs) / 100.0f;
                if (z && abs % 100 == 0) {
                    str2 = " %.0f";
                }
                d = d4;
                break;
            case 18:
                double d5 = abs;
                Double.isNaN(d5);
                d = d5 / 10.0d;
                str2 = " %.1f";
                break;
            default:
                double d6 = abs;
                Double.isNaN(d6);
                d = d6 / 100.0d;
                break;
        }
        String str3 = z2 ? str2 : " %.0f";
        if (currency == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(z4 ? "-" : "");
            sb.append(String.format(Locale.US, upperCase + str3, Double.valueOf(d)));
            return sb.toString();
        }
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = this.systemDefaultLocale;
        }
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
        currencyInstance.setCurrency(currency);
        if (z3) {
            currencyInstance.setGroupingUsed(false);
        }
        if (!z2 || (z && upperCase.equals("IRR"))) {
            currencyInstance.setMaximumFractionDigits(0);
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(z4 ? "-" : "");
        sb2.append(currencyInstance.format(d));
        String sb3 = sb2.toString();
        int indexOf = sb3.indexOf(upperCase);
        if (indexOf < 0 || (length = indexOf + upperCase.length()) >= sb3.length() || sb3.charAt(length) == ' ') {
            return sb3;
        }
        return sb3.substring(0, length) + " " + sb3.substring(length);
    }

    public LocaleInfo getBuiltinLanguageByPlural(String str) {
        String str2;
        for (LocaleInfo localeInfo : this.languagesDict.values()) {
            String str3 = localeInfo.pathToFile;
            if (str3 != null && str3.equals("remote") && ((str2 = localeInfo.shortName) == null || !str2.endsWith("_raw"))) {
                String str4 = localeInfo.pluralLangCode;
                if (str4 != null && str4.equals(str)) {
                    return localeInfo;
                }
            }
        }
        return null;
    }

    public FastDateFormat getChatDate() {
        if (this.chatDate == null) {
            synchronized (this) {
                try {
                    if (this.chatDate == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.chatDate = createFormatter(locale, getStringInternal("chatDate", R.string.chatDate), "d MMMM");
                    }
                } finally {
                }
            }
        }
        return this.chatDate;
    }

    public FastDateFormat getChatFullDate() {
        if (this.chatFullDate == null) {
            synchronized (this) {
                try {
                    if (this.chatFullDate == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", R.string.chatFullDate), "d MMMM yyyy");
                    }
                } finally {
                }
            }
        }
        return this.chatFullDate;
    }

    public Locale getCurrentLocale() {
        return this.currentLocale;
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public FastDateFormat getFormatterBannedUntil() {
        String str;
        int i;
        if (this.formatterBannedUntil == null) {
            synchronized (this) {
                try {
                    if (this.formatterBannedUntil == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        if (is24HourFormat) {
                            str = "formatterBannedUntil24H";
                            i = R.string.formatterBannedUntil24H;
                        } else {
                            str = "formatterBannedUntil12H";
                            i = R.string.formatterBannedUntil12H;
                        }
                        this.formatterBannedUntil = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                    }
                } finally {
                }
            }
        }
        return this.formatterBannedUntil;
    }

    public FastDateFormat getFormatterBannedUntilThisYear() {
        String str;
        int i;
        if (this.formatterBannedUntilThisYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterBannedUntilThisYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        if (is24HourFormat) {
                            str = "formatterBannedUntilThisYear24H";
                            i = R.string.formatterBannedUntilThisYear24H;
                        } else {
                            str = "formatterBannedUntilThisYear12H";
                            i = R.string.formatterBannedUntilThisYear12H;
                        }
                        this.formatterBannedUntilThisYear = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
                    }
                } finally {
                }
            }
        }
        return this.formatterBannedUntilThisYear;
    }

    public FastDateFormat getFormatterBoostExpired() {
        if (this.formatterBoostExpired == null) {
            synchronized (this) {
                try {
                    if (this.formatterBoostExpired == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterBoostExpired = createFormatter(locale, getStringInternal("formatterBoostExpired", R.string.formatterBoostExpired), "MMM dd, yyyy");
                    }
                } finally {
                }
            }
        }
        return this.formatterBoostExpired;
    }

    public FastDateFormat getFormatterConstDay() {
        if (this.formatterConstDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterConstDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        String language = locale.getLanguage();
                        if (language == null) {
                            language = "en";
                        }
                        String lowerCase = language.toLowerCase();
                        if (!lowerCase.toLowerCase().equals("ar") && !lowerCase.toLowerCase().equals("ko")) {
                            locale = Locale.US;
                        }
                        boolean z = is24HourFormat;
                        this.formatterConstDay = createFormatter(locale, z ? "HH:mm" : "h:mm a", z ? "HH:mm" : "h:mm a");
                    }
                } finally {
                }
            }
        }
        return this.formatterConstDay;
    }

    public FastDateFormat getFormatterDay() {
        String str;
        int i;
        if (this.formatterDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        String language = locale.getLanguage();
                        if (language == null) {
                            language = "en";
                        }
                        String lowerCase = language.toLowerCase();
                        if (!lowerCase.toLowerCase().equals("ar") && !lowerCase.toLowerCase().equals("ko")) {
                            locale = Locale.US;
                        }
                        if (is24HourFormat) {
                            str = "formatterDay24H";
                            i = R.string.formatterDay24H;
                        } else {
                            str = "formatterDay12H";
                            i = R.string.formatterDay12H;
                        }
                        this.formatterDay = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "HH:mm" : "h:mm a");
                    }
                } finally {
                }
            }
        }
        return this.formatterDay;
    }

    public FastDateFormat getFormatterDayMonth() {
        if (this.formatterDayMonth == null) {
            synchronized (this) {
                try {
                    if (this.formatterDayMonth == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterDayMonth = createFormatter(locale, getStringInternal("formatterMonth", R.string.formatterMonth), "dd MMM");
                    }
                } finally {
                }
            }
        }
        return this.formatterDayMonth;
    }

    public FastDateFormat getFormatterGiveawayCard() {
        if (this.formatterGiveawayCard == null) {
            synchronized (this) {
                try {
                    if (this.formatterGiveawayCard == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterGiveawayCard = createFormatter(locale, getStringInternal("formatterGiveawayCard", R.string.formatterGiveawayCard), "dd MMM yyyy");
                    }
                } finally {
                }
            }
        }
        return this.formatterGiveawayCard;
    }

    public FastDateFormat getFormatterGiveawayMonthDay() {
        if (this.formatterGiveawayMonthDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterGiveawayMonthDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterGiveawayMonthDay = createFormatter(locale, getStringInternal("formatterGiveawayMonthDay", R.string.formatterGiveawayMonthDay), "MMMM dd");
                    }
                } finally {
                }
            }
        }
        return this.formatterGiveawayMonthDay;
    }

    public FastDateFormat getFormatterGiveawayMonthDayYear() {
        if (this.formatterGiveawayMonthDayYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterGiveawayMonthDayYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterGiveawayMonthDayYear = createFormatter(locale, getStringInternal("formatterGiveawayMonthDayYear", R.string.formatterGiveawayMonthDayYear), "MMMM dd, yyyy");
                    }
                } finally {
                }
            }
        }
        return this.formatterGiveawayMonthDayYear;
    }

    public FastDateFormat getFormatterMonthYear() {
        if (this.formatterMonthYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterMonthYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", R.string.formatterMonthYear), "MMM yyyy");
                    }
                } finally {
                }
            }
        }
        return this.formatterMonthYear;
    }

    public FastDateFormat getFormatterScheduleDay() {
        if (this.formatterScheduleDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterScheduleDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterScheduleDay = createFormatter(locale, getStringInternal("formatDateSchedule", R.string.formatDateSchedule), "MMM d");
                    }
                } finally {
                }
            }
        }
        return this.formatterScheduleDay;
    }

    public FastDateFormat getFormatterScheduleSend(int i) {
        if (i < 0) {
            return null;
        }
        FastDateFormat[] fastDateFormatArr = this.formatterScheduleSend;
        if (i >= fastDateFormatArr.length) {
            return null;
        }
        if (fastDateFormatArr[i] == null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = Locale.getDefault();
            }
            switch (i) {
                case 0:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("SendTodayAt", R.string.SendTodayAt), "'Send today at' HH:mm");
                    break;
                case 1:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("SendDayAt", R.string.SendDayAt), "'Send on' MMM d 'at' HH:mm");
                    break;
                case 2:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("SendDayYearAt", R.string.SendDayYearAt), "'Send on' MMM d yyyy 'at' HH:mm");
                    break;
                case 3:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("RemindTodayAt", R.string.RemindTodayAt), "'Remind today at' HH:mm");
                    break;
                case 4:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("RemindDayAt", R.string.RemindDayAt), "'Remind on' MMM d 'at' HH:mm");
                    break;
                case 5:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("RemindDayYearAt", R.string.RemindDayYearAt), "'Remind on' MMM d yyyy 'at' HH:mm");
                    break;
                case 6:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartTodayAt", R.string.StartTodayAt), "'Start today at' HH:mm");
                    break;
                case 7:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartDayAt", R.string.StartDayAt), "'Start on' MMM d 'at' HH:mm");
                    break;
                case 8:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartDayYearAt", R.string.StartDayYearAt), "'Start on' MMM d yyyy 'at' HH:mm");
                    break;
                case 9:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartShortTodayAt", R.string.StartShortTodayAt), "'Today,' HH:mm");
                    break;
                case 10:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartShortDayAt", R.string.StartShortDayAt), "MMM d',' HH:mm");
                    break;
                case 11:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartShortDayYearAt", R.string.StartShortDayYearAt), "MMM d yyyy, HH:mm");
                    break;
                case 12:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartsTodayAt", R.string.StartsTodayAt), "'Starts today at' HH:mm");
                    break;
                case 13:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartsDayAt", R.string.StartsDayAt), "'Starts on' MMM d 'at' HH:mm");
                    break;
                case 14:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartsDayYearAt", R.string.StartsDayYearAt), "'Starts on' MMM d yyyy 'at' HH:mm");
                    break;
            }
        }
        return this.formatterScheduleSend[i];
    }

    public FastDateFormat getFormatterScheduleYear() {
        if (this.formatterScheduleYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterScheduleYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterScheduleYear = createFormatter(locale, getStringInternal("formatDateScheduleYear", R.string.formatDateScheduleYear), "MMM d yyyy");
                    }
                } finally {
                }
            }
        }
        return this.formatterScheduleYear;
    }

    public FastDateFormat getFormatterStats() {
        String str;
        int i;
        if (this.formatterStats == null) {
            synchronized (this) {
                try {
                    if (this.formatterStats == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        if (is24HourFormat) {
                            str = "formatterStats24H";
                            i = R.string.formatterStats24H;
                        } else {
                            str = "formatterStats12H";
                            i = R.string.formatterStats12H;
                        }
                        this.formatterStats = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                    }
                } finally {
                }
            }
        }
        return this.formatterStats;
    }

    public FastDateFormat getFormatterWeek() {
        if (this.formatterWeek == null) {
            synchronized (this) {
                try {
                    if (this.formatterWeek == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", R.string.formatterWeek), "EEE");
                    }
                } finally {
                }
            }
        }
        return this.formatterWeek;
    }

    public FastDateFormat getFormatterWeekLong() {
        if (this.formatterWeekLong == null) {
            synchronized (this) {
                try {
                    if (this.formatterWeekLong == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterWeekLong = createFormatter(locale, getStringInternal("formatterWeekLong", R.string.formatterWeekLong), "EEEE");
                    }
                } finally {
                }
            }
        }
        return this.formatterWeekLong;
    }

    public FastDateFormat getFormatterYear() {
        if (this.formatterYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", R.string.formatterYear), "dd.MM.yy");
                    }
                } finally {
                }
            }
        }
        return this.formatterYear;
    }

    public FastDateFormat getFormatterYearMax() {
        if (this.formatterYearMax == null) {
            synchronized (this) {
                try {
                    if (this.formatterYearMax == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", R.string.formatterYearMax), "dd.MM.yyyy");
                    }
                } finally {
                }
            }
        }
        return this.formatterYearMax;
    }

    public LocaleInfo getLanguageFromDict(String str) {
        if (str == null) {
            return null;
        }
        return this.languagesDict.get(str.toLowerCase().replace("-", "_"));
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public String getTranslitString(String str) {
        return getTranslitString(str, true, false);
    }

    public String getTranslitString(String str, boolean z) {
        return getTranslitString(str, true, z);
    }

    public String getTranslitString(String str, boolean z, boolean z2) {
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        String str10;
        if (str == null) {
            return null;
        }
        if (this.ruTranslitChars == null) {
            HashMap<String, String> hashMap = new HashMap<>(33);
            this.ruTranslitChars = hashMap;
            hashMap.put("а", "a");
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", "v");
            this.ruTranslitChars.put("г", "g");
            this.ruTranslitChars.put("д", "d");
            this.ruTranslitChars.put("е", "e");
            str3 = "g";
            this.ruTranslitChars.put("ё", "yo");
            this.ruTranslitChars.put("ж", "zh");
            this.ruTranslitChars.put("з", "z");
            this.ruTranslitChars.put("и", "i");
            this.ruTranslitChars.put("й", "i");
            this.ruTranslitChars.put("к", "k");
            this.ruTranslitChars.put("л", "l");
            this.ruTranslitChars.put("м", "m");
            this.ruTranslitChars.put("н", "n");
            this.ruTranslitChars.put("о", "o");
            this.ruTranslitChars.put("п", "p");
            str2 = "m";
            str4 = "r";
            this.ruTranslitChars.put("р", str4);
            str5 = "z";
            this.ruTranslitChars.put("с", "s");
            str8 = "v";
            this.ruTranslitChars.put("т", "t");
            str9 = "u";
            this.ruTranslitChars.put("у", str9);
            str10 = "s";
            this.ruTranslitChars.put("ф", "f");
            str6 = "h";
            this.ruTranslitChars.put("х", str6);
            str7 = "p";
            this.ruTranslitChars.put("ц", "ts");
            this.ruTranslitChars.put("ч", "ch");
            this.ruTranslitChars.put("ш", "sh");
            this.ruTranslitChars.put("щ", "sch");
            this.ruTranslitChars.put("ы", "i");
            this.ruTranslitChars.put("ь", "");
            this.ruTranslitChars.put("ъ", "");
            this.ruTranslitChars.put("э", "e");
            this.ruTranslitChars.put("ю", "yu");
            this.ruTranslitChars.put("я", "ya");
        } else {
            str2 = "m";
            str3 = "g";
            str4 = "r";
            str5 = "z";
            str6 = "h";
            str7 = "p";
            str8 = "v";
            str9 = "u";
            str10 = "s";
        }
        if (this.translitChars == null) {
            HashMap<String, String> hashMap2 = new HashMap<>(488);
            this.translitChars = hashMap2;
            hashMap2.put("ȼ", "c");
            this.translitChars.put("ᶇ", "n");
            this.translitChars.put("ɖ", "d");
            this.translitChars.put("ỿ", "y");
            this.translitChars.put("ᴓ", "o");
            this.translitChars.put("ø", "o");
            this.translitChars.put("ḁ", "a");
            this.translitChars.put("ʯ", str6);
            this.translitChars.put("ŷ", "y");
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", str9);
            String str11 = str9;
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", "l");
            this.translitChars.put("ɪ", "i");
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", str4);
            this.translitChars.put("ě", "e");
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", "o");
            this.translitChars.put("ⱹ", str4);
            this.translitChars.put("ồ", "o");
            this.translitChars.put("ǐ", "i");
            String str12 = str7;
            this.translitChars.put("ꝕ", str12);
            this.translitChars.put("ý", "y");
            this.translitChars.put("ḝ", "e");
            this.translitChars.put("ₒ", "o");
            this.translitChars.put("ⱥ", "a");
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", "e");
            this.translitChars.put("ƈ", "c");
            this.translitChars.put("ɦ", str6);
            this.translitChars.put("ᵬ", "b");
            String str13 = str6;
            String str14 = str10;
            this.translitChars.put("ṣ", str14);
            this.translitChars.put("đ", "d");
            this.translitChars.put("ỗ", "o");
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", "a");
            this.translitChars.put("ɏ", "y");
            this.translitChars.put("ʌ", str8);
            this.translitChars.put("ꝓ", str12);
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", "d");
            this.translitChars.put("ᴌ", "l");
            this.translitChars.put("ė", "e");
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", "c");
            this.translitChars.put("ʁ", str4);
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", "o");
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            String str15 = str3;
            this.translitChars.put("ᶃ", str15);
            this.translitChars.put("ṋ", "n");
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", str15);
            this.translitChars.put("ǳ", "dz");
            String str16 = str5;
            this.translitChars.put("ź", str16);
            this.translitChars.put("ꜷ", "au");
            this.translitChars.put("ǖ", str11);
            this.translitChars.put("ᵹ", str15);
            this.translitChars.put("ȯ", "o");
            this.translitChars.put("ɐ", "a");
            this.translitChars.put("ą", "a");
            this.translitChars.put("õ", "o");
            this.translitChars.put("ɻ", str4);
            this.translitChars.put("ꝍ", "o");
            this.translitChars.put("ǟ", "a");
            this.translitChars.put("ȴ", "l");
            this.translitChars.put("ʂ", str14);
            this.translitChars.put("ﬂ", "fl");
            this.translitChars.put("ȉ", "i");
            this.translitChars.put("ⱻ", "e");
            this.translitChars.put("ṉ", "n");
            this.translitChars.put("ï", "i");
            this.translitChars.put("ñ", "n");
            this.translitChars.put("ᴉ", "i");
            this.translitChars.put("ʇ", "t");
            this.translitChars.put("ẓ", str16);
            this.translitChars.put("ỷ", "y");
            this.translitChars.put("ȳ", "y");
            this.translitChars.put("ṩ", str14);
            this.translitChars.put("ɽ", str4);
            this.translitChars.put("ĝ", str15);
            this.translitChars.put("ᴝ", str11);
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", "i");
            this.translitChars.put("ť", "t");
            this.translitChars.put("ꜿ", "c");
            this.translitChars.put("ʟ", "l");
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", str11);
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", "a");
            this.translitChars.put("ǘ", str11);
            this.translitChars.put("ꞅ", str14);
            this.translitChars.put("ᵣ", str4);
            this.translitChars.put("ᴀ", "a");
            this.translitChars.put("ƃ", "b");
            this.translitChars.put("ḩ", str13);
            this.translitChars.put("ṧ", str14);
            this.translitChars.put("ₑ", "e");
            this.translitChars.put("ʜ", str13);
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            this.translitChars.put("ḋ", "d");
            this.translitChars.put("ƣ", "oi");
            this.translitChars.put("ꝑ", str12);
            this.translitChars.put("ħ", str13);
            String str17 = str8;
            this.translitChars.put("ⱴ", str17);
            this.translitChars.put("ẇ", "w");
            this.translitChars.put("ǹ", "n");
            String str18 = str2;
            this.translitChars.put("ɯ", str18);
            this.translitChars.put("ɡ", str15);
            this.translitChars.put("ɴ", "n");
            this.translitChars.put("ᴘ", str12);
            this.translitChars.put("ᵥ", str17);
            this.translitChars.put("ū", str11);
            this.translitChars.put("ḃ", "b");
            this.translitChars.put("ṗ", str12);
            this.translitChars.put("å", "a");
            this.translitChars.put("ɕ", "c");
            this.translitChars.put("ọ", "o");
            this.translitChars.put("ắ", "a");
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", str4);
            this.translitChars.put("ô", "o");
            this.translitChars.put("ǿ", "o");
            this.translitChars.put("ṳ", str11);
            this.translitChars.put("ȥ", str16);
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", "d");
            this.translitChars.put("ȇ", "e");
            this.translitChars.put("ȕ", str11);
            this.translitChars.put("ȵ", "n");
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", "a");
            this.translitChars.put("ǩ", "k");
            this.translitChars.put("ĩ", "i");
            this.translitChars.put("ṵ", str11);
            this.translitChars.put("ŧ", "t");
            this.translitChars.put("ɾ", str4);
            this.translitChars.put("ƙ", "k");
            this.translitChars.put("ṫ", "t");
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", "a");
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", "l");
            this.translitChars.put("ᶂ", "f");
            this.translitChars.put("ᵴ", str14);
            this.translitChars.put("ꞃ", str4);
            this.translitChars.put("ᶌ", str17);
            this.translitChars.put("ɵ", "o");
            this.translitChars.put("ḉ", "c");
            this.translitChars.put("ᵤ", str11);
            this.translitChars.put("ẑ", str16);
            this.translitChars.put("ṹ", str11);
            this.translitChars.put("ň", "n");
            this.translitChars.put("ʍ", "w");
            this.translitChars.put("ầ", "a");
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", "b");
            this.translitChars.put("ɼ", str4);
            this.translitChars.put("ò", "o");
            this.translitChars.put("ẘ", "w");
            this.translitChars.put("ɗ", "d");
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", str11);
            this.translitChars.put("ᶀ", "b");
            this.translitChars.put("ǜ", str11);
            this.translitChars.put("ẹ", "e");
            this.translitChars.put("ǡ", "a");
            this.translitChars.put("ɥ", str13);
            this.translitChars.put("ṏ", "o");
            this.translitChars.put("ǔ", str11);
            this.translitChars.put("ʎ", "y");
            this.translitChars.put("ȱ", "o");
            this.translitChars.put("ệ", "e");
            this.translitChars.put("ế", "e");
            this.translitChars.put("ĭ", "i");
            this.translitChars.put("ⱸ", "e");
            this.translitChars.put("ṯ", "t");
            this.translitChars.put("ᶑ", "d");
            this.translitChars.put("ḧ", str13);
            this.translitChars.put("ṥ", str14);
            this.translitChars.put("ë", "e");
            this.translitChars.put("ᴍ", str18);
            this.translitChars.put("ö", "o");
            this.translitChars.put("é", "e");
            this.translitChars.put("ı", "i");
            this.translitChars.put("ď", "d");
            this.translitChars.put("ᵯ", str18);
            this.translitChars.put("ỵ", "y");
            this.translitChars.put("ŵ", "w");
            this.translitChars.put("ề", "e");
            this.translitChars.put("ứ", str11);
            this.translitChars.put("ƶ", str16);
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", "d");
            this.translitChars.put("ŭ", str11);
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", "e");
            this.translitChars.put("ǚ", str11);
            this.translitChars.put("ġ", str15);
            this.translitChars.put("ṙ", str4);
            this.translitChars.put("ƞ", "n");
            this.translitChars.put("ḗ", "e");
            this.translitChars.put("ẝ", str14);
            this.translitChars.put("ᶁ", "d");
            this.translitChars.put("ķ", "k");
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", "e");
            this.translitChars.put("ợ", "o");
            this.translitChars.put("ḿ", str18);
            this.translitChars.put("ꜰ", "f");
            this.translitChars.put("ẵ", "a");
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", str18);
            this.translitChars.put("ᵽ", str12);
            this.translitChars.put("ữ", str11);
            this.translitChars.put("ⱪ", "k");
            this.translitChars.put("ḥ", str13);
            this.translitChars.put("ţ", "t");
            this.translitChars.put("ᵱ", str12);
            this.translitChars.put("ṁ", str18);
            this.translitChars.put("á", "a");
            this.translitChars.put("ᴎ", "n");
            this.translitChars.put("ꝟ", str17);
            this.translitChars.put("è", "e");
            this.translitChars.put("ᶎ", str16);
            this.translitChars.put("ꝺ", "d");
            this.translitChars.put("ᶈ", str12);
            this.translitChars.put("ɫ", "l");
            this.translitChars.put("ᴢ", str16);
            this.translitChars.put("ɱ", str18);
            this.translitChars.put("ṝ", str4);
            this.translitChars.put("ṽ", str17);
            this.translitChars.put("ũ", str11);
            this.translitChars.put("ß", "ss");
            this.translitChars.put("ĥ", str13);
            this.translitChars.put("ᵵ", "t");
            this.translitChars.put("ʐ", str16);
            this.translitChars.put("ṟ", str4);
            this.translitChars.put("ɲ", "n");
            this.translitChars.put("à", "a");
            this.translitChars.put("ẙ", "y");
            this.translitChars.put("ỳ", "y");
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", str11);
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", "a");
            this.translitChars.put("ʑ", str16);
            this.translitChars.put("ẛ", str14);
            this.translitChars.put("ḭ", "i");
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", str16);
            this.translitChars.put("ÿ", "y");
            this.translitChars.put("ǝ", "e");
            this.translitChars.put("ǭ", "o");
            this.translitChars.put("ᴅ", "d");
            this.translitChars.put("ᶅ", "l");
            this.translitChars.put("ù", str11);
            this.translitChars.put("ạ", "a");
            this.translitChars.put("ḅ", "b");
            this.translitChars.put("ụ", str11);
            this.translitChars.put("ằ", "a");
            this.translitChars.put("ᴛ", "t");
            this.translitChars.put("ƴ", "y");
            this.translitChars.put("ⱦ", "t");
            this.translitChars.put("ⱡ", "l");
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", str16);
            this.translitChars.put("ḫ", str13);
            this.translitChars.put("ⱳ", "w");
            this.translitChars.put("ḵ", "k");
            this.translitChars.put("ờ", "o");
            this.translitChars.put("î", "i");
            this.translitChars.put("ģ", str15);
            this.translitChars.put("ȅ", "e");
            this.translitChars.put("ȧ", "a");
            this.translitChars.put("ẳ", "a");
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", "t");
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", "c");
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", str11);
            this.translitChars.put("ỉ", "i");
            this.translitChars.put("ᴚ", str4);
            this.translitChars.put("ś", str14);
            this.translitChars.put("ꝋ", "o");
            this.translitChars.put("ỹ", "y");
            this.translitChars.put("ṡ", str14);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", "a");
            this.translitChars.put("ẗ", "t");
            this.translitChars.put("ĺ", "l");
            this.translitChars.put("ž", str16);
            this.translitChars.put("ᵺ", "th");
            this.translitChars.put("ƌ", "d");
            this.translitChars.put("ș", str14);
            this.translitChars.put("š", str14);
            this.translitChars.put("ᶙ", str11);
            this.translitChars.put("ẽ", "e");
            this.translitChars.put("ẜ", str14);
            this.translitChars.put("ɇ", "e");
            this.translitChars.put("ṷ", str11);
            this.translitChars.put("ố", "o");
            this.translitChars.put("ȿ", str14);
            this.translitChars.put("ᴠ", str17);
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", "o");
            this.translitChars.put("ɛ", "e");
            this.translitChars.put("ǻ", "a");
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", "o");
            this.translitChars.put("ȋ", "i");
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", "d");
            this.translitChars.put("ⱬ", str16);
            this.translitChars.put("ẁ", "w");
            this.translitChars.put("ᶏ", "a");
            this.translitChars.put("ꞇ", "t");
            this.translitChars.put("ğ", str15);
            this.translitChars.put("ɳ", "n");
            this.translitChars.put("ʛ", str15);
            this.translitChars.put("ᴜ", str11);
            this.translitChars.put("ẩ", "a");
            this.translitChars.put("ṅ", "n");
            this.translitChars.put("ɨ", "i");
            this.translitChars.put("ᴙ", str4);
            this.translitChars.put("ǎ", "a");
            this.translitChars.put("ſ", str14);
            this.translitChars.put("ȫ", "o");
            this.translitChars.put("ɿ", str4);
            this.translitChars.put("ƭ", "t");
            this.translitChars.put("ḯ", "i");
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", str17);
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", str18);
            this.translitChars.put("ż", str16);
            this.translitChars.put("ĕ", "e");
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", "o");
            this.translitChars.put("ễ", "e");
            this.translitChars.put("ɬ", "l");
            this.translitChars.put("ị", "i");
            this.translitChars.put("ᵭ", "d");
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", "l");
            this.translitChars.put("ŕ", str4);
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", "t");
            this.translitChars.put("ā", "a");
            this.translitChars.put("ḙ", "e");
            this.translitChars.put("ᴑ", "o");
            this.translitChars.put("ç", "c");
            this.translitChars.put("ᶊ", str14);
            this.translitChars.put("ặ", "a");
            this.translitChars.put("ų", str11);
            this.translitChars.put("ả", "a");
            this.translitChars.put("ǥ", str15);
            this.translitChars.put("ꝁ", "k");
            this.translitChars.put("ẕ", str16);
            this.translitChars.put("ŝ", str14);
            this.translitChars.put("ḕ", "e");
            this.translitChars.put("ɠ", str15);
            this.translitChars.put("ꝉ", "l");
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", "o");
            this.translitChars.put("ę", "e");
            this.translitChars.put("ổ", "o");
            this.translitChars.put("ƫ", "t");
            this.translitChars.put("ǫ", "o");
            this.translitChars.put("i̇", "i");
            this.translitChars.put("ṇ", "n");
            this.translitChars.put("ć", "c");
            this.translitChars.put("ᵷ", str15);
            this.translitChars.put("ẅ", "w");
            this.translitChars.put("ḑ", "d");
            this.translitChars.put("ḹ", "l");
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", str4);
            this.translitChars.put("ļ", "l");
            this.translitChars.put("ȑ", str4);
            this.translitChars.put("ȭ", "o");
            this.translitChars.put("ᵰ", "n");
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", "l");
            this.translitChars.put("ä", "a");
            this.translitChars.put("ƥ", str12);
            this.translitChars.put("ỏ", "o");
            this.translitChars.put("į", "i");
            this.translitChars.put("ȓ", str4);
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", str15);
            this.translitChars.put("ṻ", str11);
            this.translitChars.put("ō", "o");
            this.translitChars.put("ľ", "l");
            this.translitChars.put("ẃ", "w");
            this.translitChars.put("ț", "t");
            this.translitChars.put("ń", "n");
            this.translitChars.put("ɍ", str4);
            this.translitChars.put("ȃ", "a");
            this.translitChars.put("ü", str11);
            this.translitChars.put("ꞁ", "l");
            this.translitChars.put("ᴐ", "o");
            this.translitChars.put("ớ", "o");
            this.translitChars.put("ᴃ", "b");
            this.translitChars.put("ɹ", str4);
            this.translitChars.put("ᵲ", str4);
            this.translitChars.put("ʏ", "y");
            this.translitChars.put("ᵮ", "f");
            this.translitChars.put("ⱨ", str13);
            this.translitChars.put("ŏ", "o");
            this.translitChars.put("ú", str11);
            this.translitChars.put("ṛ", str4);
            this.translitChars.put("ʮ", str13);
            this.translitChars.put("ó", "o");
            this.translitChars.put("ů", str11);
            this.translitChars.put("ỡ", "o");
            this.translitChars.put("ṕ", str12);
            this.translitChars.put("ᶖ", "i");
            this.translitChars.put("ự", str11);
            this.translitChars.put("ã", "a");
            this.translitChars.put("ᵢ", "i");
            this.translitChars.put("ṱ", "t");
            this.translitChars.put("ể", "e");
            this.translitChars.put("ử", str11);
            this.translitChars.put("í", "i");
            this.translitChars.put("ɔ", "o");
            this.translitChars.put("ɺ", str4);
            this.translitChars.put("ɢ", str15);
            this.translitChars.put("ř", str4);
            this.translitChars.put("ẖ", str13);
            this.translitChars.put("ű", str11);
            this.translitChars.put("ȍ", "o");
            this.translitChars.put("ḻ", "l");
            this.translitChars.put("ḣ", str13);
            this.translitChars.put("ȶ", "t");
            this.translitChars.put("ņ", "n");
            this.translitChars.put("ᶒ", "e");
            this.translitChars.put("ì", "i");
            this.translitChars.put("ẉ", "w");
            this.translitChars.put("ē", "e");
            this.translitChars.put("ᴇ", "e");
            this.translitChars.put("ł", "l");
            this.translitChars.put("ộ", "o");
            this.translitChars.put("ɭ", "l");
            this.translitChars.put("ẏ", "y");
            this.translitChars.put("ᴊ", "j");
            this.translitChars.put("ḱ", "k");
            this.translitChars.put("ṿ", str17);
            this.translitChars.put("ȩ", "e");
            this.translitChars.put("â", "a");
            this.translitChars.put("ş", str14);
            this.translitChars.put("ŗ", str4);
            this.translitChars.put("ʋ", str17);
            this.translitChars.put("ₐ", "a");
            this.translitChars.put("ↄ", "c");
            this.translitChars.put("ᶓ", "e");
            this.translitChars.put("ɰ", str18);
            this.translitChars.put("ᴡ", "w");
            this.translitChars.put("ȏ", "o");
            this.translitChars.put("č", "c");
            this.translitChars.put("ǵ", str15);
            this.translitChars.put("ĉ", "c");
            this.translitChars.put("ᶗ", "o");
            this.translitChars.put("ꝃ", "k");
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", "o");
            this.translitChars.put("ꜱ", str14);
            this.translitChars.put("ṓ", "o");
            this.translitChars.put("ȟ", str13);
            this.translitChars.put("ő", "o");
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", "e");
            this.translitChars.put("і", "i");
            this.translitChars.put("ї", "i");
        }
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        boolean z3 = false;
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            String substring = str.substring(i, i2);
            if (z2) {
                String lowerCase = substring.toLowerCase();
                boolean z4 = !substring.equals(lowerCase);
                substring = lowerCase;
                z3 = z4;
            }
            String str19 = this.translitChars.get(substring);
            if (str19 == null && z) {
                str19 = this.ruTranslitChars.get(substring);
            }
            if (str19 != null) {
                if (z2 && z3) {
                    if (str19.length() > 1) {
                        str19 = str19.substring(0, 1).toUpperCase() + str19.substring(1);
                    } else {
                        str19 = str19.toUpperCase();
                    }
                }
                sb.append(str19);
            } else {
                if (z2) {
                    char charAt = substring.charAt(0);
                    if ((charAt < 'a' || charAt > 'z' || charAt < '0' || charAt > '9') && charAt != ' ' && charAt != '\'' && charAt != ',' && charAt != '.' && charAt != '&' && charAt != '-' && charAt != '/') {
                        return null;
                    }
                    if (z3) {
                        substring = substring.toUpperCase();
                    }
                }
                sb.append(substring);
            }
            i = i2;
        }
        return sb.toString();
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public void loadRemoteLanguages(int i) {
        loadRemoteLanguages(i, true);
    }

    public void loadRemoteLanguages(final int i, final boolean z) {
        if (this.loadingRemoteLanguages) {
            return;
        }
        this.loadingRemoteLanguages = true;
        ConnectionsManager.getInstance(i).sendRequest(new TLRPC.TL_langpack_getLanguages(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocaleController.this.lambda$loadRemoteLanguages$12(z, i, tLObject, tL_error);
            }
        }, 8);
    }

    public void onDeviceConfigurationChange(Configuration configuration) {
        if (this.changingConfiguration) {
            return;
        }
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        Locale locale = configuration.locale;
        this.systemDefaultLocale = locale;
        if (this.languageOverride != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            this.currentLocaleInfo = null;
            applyLanguage(localeInfo, false, false, UserConfig.selectedAccount);
        } else if (locale != null) {
            String displayName = locale.getDisplayName();
            String displayName2 = this.currentLocale.getDisplayName();
            if (displayName != null && displayName2 != null && !displayName.equals(displayName2)) {
                recreateFormatters();
            }
            this.currentLocale = locale;
            LocaleInfo localeInfo2 = this.currentLocaleInfo;
            if (localeInfo2 != null && !TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
            }
            if (this.currentPluralRules == null) {
                PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                this.currentPluralRules = pluralRules;
                if (pluralRules == null) {
                    this.currentPluralRules = this.allRules.get("en");
                }
            }
        }
        String systemLocaleStringIso639 = getSystemLocaleStringIso639();
        String str = this.currentSystemLocale;
        if (str == null || systemLocaleStringIso639.equals(str)) {
            return;
        }
        this.currentSystemLocale = systemLocaleStringIso639;
        ConnectionsManager.setSystemLangCode(systemLocaleStringIso639);
    }

    public void recreateFormatters() {
        LocaleInfo localeInfo;
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (language == null) {
            language = "en";
        }
        String lowerCase = language.toLowerCase();
        int i = 0;
        isRTL = (lowerCase.length() == 2 && (lowerCase.equals("ar") || lowerCase.equals("fa") || lowerCase.equals("he") || lowerCase.equals("iw"))) || lowerCase.startsWith("ar_") || lowerCase.startsWith("fa_") || lowerCase.startsWith("he_") || lowerCase.startsWith("iw_") || ((localeInfo = this.currentLocaleInfo) != null && localeInfo.isRtl);
        nameDisplayOrder = lowerCase.equals("ko") ? 2 : 1;
        this.formatterBoostExpired = null;
        this.formatterGiveawayCard = null;
        this.formatterGiveawayMonthDay = null;
        this.formatterGiveawayMonthDayYear = null;
        this.formatterMonthYear = null;
        this.formatterDayMonth = null;
        this.formatterYear = null;
        this.formatterYearMax = null;
        this.chatDate = null;
        this.chatFullDate = null;
        this.formatterWeek = null;
        this.formatterWeekLong = null;
        this.formatterScheduleDay = null;
        this.formatterScheduleYear = null;
        this.formatterDay = null;
        this.formatterConstDay = null;
        this.formatterStats = null;
        this.formatterBannedUntil = null;
        this.formatterBannedUntilThisYear = null;
        while (true) {
            FastDateFormat[] fastDateFormatArr = this.formatterScheduleSend;
            if (i >= fastDateFormatArr.length) {
                return;
            }
            fastDateFormatArr[i] = null;
            i++;
        }
    }

    public void reloadCurrentRemoteLocale(int i, String str, boolean z, Runnable runnable) {
        if (str != null) {
            str = str.replace("-", "_");
        }
        String str2 = str;
        if (str2 != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!str2.equals(localeInfo.shortName) && !str2.equals(this.currentLocaleInfo.baseLangCode)) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, str2, z, i, runnable);
    }

    public void lambda$applyRemoteLanguage$20(final LocaleInfo localeInfo, final TLRPC.TL_langPackDifference tL_langPackDifference, int i, final Runnable runnable) {
        HashMap<String, String> localeFileStrings;
        boolean z = true;
        if (tL_langPackDifference == null || tL_langPackDifference.strings.isEmpty() || localeInfo == null || localeInfo.isLocal()) {
            StringBuilder sb = new StringBuilder();
            sb.append("saveRemoteLocaleStrings: empty difference=");
            sb.append(tL_langPackDifference == null || tL_langPackDifference.strings.isEmpty());
            sb.append("; locale is local or null=");
            if (localeInfo != null && !localeInfo.isLocal()) {
                z = false;
            }
            sb.append(z);
            FileLog.d(sb.toString());
            recreateFormatters();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadInterface, new Object[0]);
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        String lowerCase = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
        final int i2 = lowerCase.equals(localeInfo.shortName) ? 0 : lowerCase.equals(localeInfo.baseLangCode) ? 1 : -1;
        if (i2 == -1) {
            FileLog.d("saveRemoteLocaleStrings: unknown language " + lowerCase + " (locale short=" + localeInfo.shortName + ", base=" + localeInfo.baseLangCode + ")");
            return;
        }
        File pathToFile = i2 == 0 ? localeInfo.getPathToFile() : localeInfo.getPathToBaseFile();
        try {
            if (tL_langPackDifference.from_version == 0) {
                FileLog.d("saveRemoteLocaleStrings: difference is straight from the beginning");
                localeFileStrings = new HashMap<>();
            } else {
                FileLog.d("saveRemoteLocaleStrings: difference is from version " + tL_langPackDifference.from_version + " ours " + localeInfo.version + " (base version " + localeInfo.baseLangCode + ")");
                localeFileStrings = getLocaleFileStrings(pathToFile, true);
            }
            for (int i3 = 0; i3 < tL_langPackDifference.strings.size(); i3++) {
                TLRPC.LangPackString langPackString = tL_langPackDifference.strings.get(i3);
                if (langPackString instanceof TLRPC.TL_langPackString) {
                    localeFileStrings.put(langPackString.key, escapeString(langPackString.value));
                } else if (langPackString instanceof TLRPC.TL_langPackStringPluralized) {
                    String str = langPackString.key + "_zero";
                    String str2 = langPackString.zero_value;
                    localeFileStrings.put(str, str2 != null ? escapeString(str2) : "");
                    String str3 = langPackString.key + "_one";
                    String str4 = langPackString.one_value;
                    localeFileStrings.put(str3, str4 != null ? escapeString(str4) : "");
                    String str5 = langPackString.key + "_two";
                    String str6 = langPackString.two_value;
                    localeFileStrings.put(str5, str6 != null ? escapeString(str6) : "");
                    String str7 = langPackString.key + "_few";
                    String str8 = langPackString.few_value;
                    localeFileStrings.put(str7, str8 != null ? escapeString(str8) : "");
                    String str9 = langPackString.key + "_many";
                    String str10 = langPackString.many_value;
                    localeFileStrings.put(str9, str10 != null ? escapeString(str10) : "");
                    String str11 = langPackString.key + "_other";
                    String str12 = langPackString.other_value;
                    localeFileStrings.put(str11, str12 != null ? escapeString(str12) : "");
                } else if (langPackString instanceof TLRPC.TL_langPackStringDeleted) {
                    localeFileStrings.remove(langPackString.key);
                }
            }
            FileLog.d("save locale file to " + pathToFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToFile));
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            bufferedWriter.write("<resources>\n");
            for (Map.Entry<String, String> entry : localeFileStrings.entrySet()) {
                bufferedWriter.write(String.format("<string name=\"%1$s\">%2$s</string>\n", entry.getKey(), entry.getValue()));
            }
            bufferedWriter.write("</resources>");
            bufferedWriter.close();
            boolean hasBaseLang = localeInfo.hasBaseLang();
            final HashMap<String, String> localeFileStrings2 = getLocaleFileStrings(hasBaseLang ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
            if (hasBaseLang) {
                localeFileStrings2.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
            }
            FileLog.d("saved locale file to " + pathToFile);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    LocaleController.this.lambda$saveRemoteLocaleStrings$10(i2, localeInfo, tL_langPackDifference, localeFileStrings2, runnable);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC.TL_langPackDifference tL_langPackDifference, int i) {
        if (this.currentLocaleInfo == null) {
            return;
        }
        String lowerCase = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
        if (lowerCase.equals(this.currentLocaleInfo.shortName) || lowerCase.equals(this.currentLocaleInfo.baseLangCode)) {
            lambda$applyRemoteLanguage$20(this.currentLocaleInfo, tL_langPackDifference, i, null);
        }
    }
}
