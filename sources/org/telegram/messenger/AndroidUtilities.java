package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.view.inspector.WindowInspector;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView$ItemAnimator$$ExternalSyntheticThrowCCEIfNotNull0;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.CustomHtml;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestTimeDelegate;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatBackgroundDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.PeerStoriesView;
import org.telegram.ui.Stories.StoryMediaAreasView;
import org.telegram.ui.WallpapersListActivity;

public class AndroidUtilities {
    public static Pattern BAD_CHARS_MESSAGE_LONG_PATTERN = null;
    public static Pattern BAD_CHARS_MESSAGE_PATTERN = null;
    public static Pattern BAD_CHARS_PATTERN = null;
    public static final int DARK_STATUS_BAR_OVERLAY = 855638016;
    public static final int FLAG_TAG_ALL = 11;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    public static final int LIGHT_STATUS_BAR_OVERLAY = 251658240;
    public static Pattern LONG_BAD_CHARS_PATTERN = null;
    public static Pattern REMOVE_MULTIPLE_DIACRITICS = null;
    public static final int REPLACING_TAG_TYPE_BOLD = 1;
    public static final int REPLACING_TAG_TYPE_LINK = 0;
    public static final int REPLACING_TAG_TYPE_LINKBOLD = 2;
    public static final int REPLACING_TAG_TYPE_LINK_NBSP = 3;
    public static final int REPLACING_TAG_TYPE_UNDERLINE = 4;
    public static final String STICKERS_PLACEHOLDER_PACK_NAME = "tg_placeholders_android";
    public static final String STICKERS_PLACEHOLDER_PACK_NAME_2 = "tg_superplaceholders_android_2";
    public static final String TYPEFACE_COURIER_NEW_BOLD = "fonts/courier_new_bold.ttf";
    public static final String TYPEFACE_MERRIWEATHER_BOLD = "fonts/mw_bold.ttf";
    public static final String TYPEFACE_ROBOTO_MEDIUM = "fonts/rmedium.ttf";
    public static final String TYPEFACE_ROBOTO_MEDIUM_ITALIC = "fonts/rmediumitalic.ttf";
    public static final String TYPEFACE_ROBOTO_MONO = "fonts/rmono.ttf";
    public static Pattern WEB_URL;
    private static AccessibilityManager accessibilityManager;
    private static CallReceiver callReceiver;
    private static char[] characters;
    private static HashSet<Character> charactersMap;
    private static int[] documentIcons;
    private static int[] documentMediaIcons;
    public static boolean firstConfigurationWas;
    private static SimpleDateFormat generatingVideoPathFormat;
    private static boolean hasCallPermissions;
    public static boolean incorrectDisplaySizeFix;
    private static Boolean isHonor;
    public static boolean isInMultiwindow;
    public static int leftBaseline;
    private static Pattern linksPattern;
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    public static boolean makingGlobalBlurBitmap;
    private static Typeface mediumTypeface;
    private static HashMap<Window, ValueAnimator> navigationBarColorAnimators;
    public static final String[] numbersSignatureArray;
    public static int roundMessageInset;
    public static int roundMessageSize;
    public static int roundPlayingMessageSize;
    public static final Linkify.MatchFilter sUrlMatchFilter;
    private static final float[] tempFloats;
    private static final float[] tempFloats2;
    public static float touchSlop;
    private static Runnable unregisterRunnable;
    private static Pattern uriParse;
    public static boolean usingHardwareInput;
    private static Vibrator vibrator;
    public static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    public static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();
    private static int prevOrientation = -10;
    private static boolean waitingForSms = false;
    private static boolean waitingForCall = false;
    private static final Object smsLock = new Object();
    private static final Object callLock = new Object();
    public static int statusBarHeight = 0;
    public static int navigationBarHeight = 0;
    public static float density = 1.0f;
    public static Point displaySize = new Point();
    public static float screenRefreshRate = 60.0f;
    public static float screenMaxRefreshRate = 60.0f;
    public static float screenRefreshTime = 16.666666f;
    public static Integer photoSize = null;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private static Boolean isTablet = null;
    private static Boolean wasTablet = null;
    private static Boolean isSmallScreen = null;
    private static int adjustOwnerClassGuid = 0;
    private static int altFocusableClassGuid = 0;
    public static final RectF rectTmp = new RectF();
    public static final Rect rectTmp2 = new Rect();
    public static final int[] pointTmp2 = new int[2];
    public static Pattern REMOVE_RTL = null;
    private static Pattern singleTagPatter = null;

    public interface IntColorCallback {
        void run(int i);
    }

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() != 1) {
                    if (motionEvent.getAction() == 3) {
                    }
                    return onTouchEvent;
                }
                Selection.removeSelection(spannable);
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public static class LinkSpec {
        int end;
        int start;
        String url;

        private LinkSpec() {
        }
    }

    private static class VcardData {
        String name;
        ArrayList<String> phones;
        StringBuilder vcard;

        private VcardData() {
            this.phones = new ArrayList<>();
            this.vcard = new StringBuilder();
        }
    }

    public static class VcardItem {
        public int type;
        public ArrayList<String> vcardData = new ArrayList<>();
        public String fullData = "";
        public boolean checked = true;

        public String getRawType(boolean z) {
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return "";
            }
            String substring = this.fullData.substring(0, indexOf);
            if (this.type == 20) {
                String[] split = substring.substring(2).split(";");
                return z ? split[0] : split.length > 1 ? split[split.length - 1] : "";
            }
            String[] split2 = substring.split(";");
            for (int i = 0; i < split2.length; i++) {
                if (split2[i].indexOf(61) < 0) {
                    substring = split2[i];
                }
            }
            return substring;
        }

        public String[] getRawValue() {
            byte[] decodeQuotedPrintable;
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return new String[0];
            }
            String substring = this.fullData.substring(0, indexOf);
            String substring2 = this.fullData.substring(indexOf + 1);
            String str = null;
            String str2 = "UTF-8";
            for (String str3 : substring.split(";")) {
                String[] split = str3.split("=");
                if (split.length == 2) {
                    if (split[0].equals("CHARSET")) {
                        str2 = split[1];
                    } else if (split[0].equals("ENCODING")) {
                        str = split[1];
                    }
                }
            }
            String[] split2 = substring2.split(";");
            for (int i = 0; i < split2.length; i++) {
                if (!TextUtils.isEmpty(split2[i]) && str != null && str.equalsIgnoreCase("QUOTED-PRINTABLE") && (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(split2[i]))) != null && decodeQuotedPrintable.length != 0) {
                    try {
                        split2[i] = new String(decodeQuotedPrintable, str2);
                    } catch (Exception unused) {
                    }
                }
            }
            return split2;
        }

        public java.lang.String getType() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.VcardItem.getType():java.lang.String");
        }

        public String getValue(boolean z) {
            byte[] decodeQuotedPrintable;
            StringBuilder sb = new StringBuilder();
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return "";
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String substring = this.fullData.substring(0, indexOf);
            String substring2 = this.fullData.substring(indexOf + 1);
            String str = null;
            String str2 = "UTF-8";
            for (String str3 : substring.split(";")) {
                String[] split = str3.split("=");
                if (split.length == 2) {
                    if (split[0].equals("CHARSET")) {
                        str2 = split[1];
                    } else if (split[0].equals("ENCODING")) {
                        str = split[1];
                    }
                }
            }
            String[] split2 = substring2.split(";");
            boolean z2 = false;
            for (int i = 0; i < split2.length; i++) {
                if (!TextUtils.isEmpty(split2[i])) {
                    if (str != null && str.equalsIgnoreCase("QUOTED-PRINTABLE") && (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(split2[i]))) != null && decodeQuotedPrintable.length != 0) {
                        try {
                            split2[i] = new String(decodeQuotedPrintable, str2);
                        } catch (Exception unused) {
                        }
                    }
                    if (z2 && sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(split2[i]);
                    if (!z2) {
                        z2 = split2[i].length() > 0;
                    }
                }
            }
            if (z) {
                int i2 = this.type;
                if (i2 == 0) {
                    return PhoneFormat.getInstance().format(sb.toString());
                }
                if (i2 == 5) {
                    String[] split3 = sb.toString().split("T");
                    if (split3.length > 0) {
                        String[] split4 = split3[0].split("-");
                        if (split4.length == 3) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1, Utilities.parseInt((CharSequence) split4[0]).intValue());
                            calendar.set(2, Utilities.parseInt((CharSequence) split4[1]).intValue() - 1);
                            calendar.set(5, Utilities.parseInt((CharSequence) split4[2]).intValue());
                            return LocaleController.getInstance().getFormatterYearMax().format(calendar.getTime());
                        }
                    }
                }
            }
            return sb.toString();
        }
    }

    static {
        WEB_URL = null;
        BAD_CHARS_PATTERN = null;
        LONG_BAD_CHARS_PATTERN = null;
        BAD_CHARS_MESSAGE_PATTERN = null;
        BAD_CHARS_MESSAGE_LONG_PATTERN = null;
        REMOVE_MULTIPLE_DIACRITICS = null;
        try {
            BAD_CHARS_PATTERN = Pattern.compile("[─-◿]");
            LONG_BAD_CHARS_PATTERN = Pattern.compile("[一-鿿]");
            BAD_CHARS_MESSAGE_LONG_PATTERN = Pattern.compile("[̀-ͯ\u2066-\u2067]");
            BAD_CHARS_MESSAGE_PATTERN = Pattern.compile("[\u2066-\u2067]+");
            REMOVE_MULTIPLE_DIACRITICS = Pattern.compile("([\\u0300-\\u036f]{1,2})[\\u0300-\\u036f]+");
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https|ton|tg|tonsite):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + Pattern.compile("(([a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef]([a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\-]{0,61}[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef]){0,1}\\.)+[a-zA-Z -\ud7ff豈-﷏ﷰ-\uffef]{2,63}|" + Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))") + ")") + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e(e);
        }
        leftBaseline = isTablet() ? 80 : 72;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        documentIcons = new int[]{R.drawable.media_doc_blue, R.drawable.media_doc_green, R.drawable.media_doc_red, R.drawable.media_doc_yellow};
        documentMediaIcons = new int[]{R.drawable.media_doc_blue_b, R.drawable.media_doc_green_b, R.drawable.media_doc_red_b, R.drawable.media_doc_yellow_b};
        sUrlMatchFilter = new Linkify.MatchFilter() {
            @Override
            public final boolean acceptMatch(CharSequence charSequence, int i, int i2) {
                boolean lambda$static$6;
                lambda$static$6 = AndroidUtilities.lambda$static$6(charSequence, i, i2);
                return lambda$static$6;
            }
        };
        hasCallPermissions = Build.VERSION.SDK_INT >= 23;
        numbersSignatureArray = new String[]{"", "K", "M", "B", "T", "P"};
        tempFloats = new float[9];
        tempFloats2 = new float[9];
        characters = new char[]{160, ' ', '!', '\"', '#', '%', '&', '\'', '(', ')', '*', ',', '-', '.', '/', ':', ';', '?', '@', '[', '\\', ']', '_', '{', '}', 161, 167, 171, 182, 183, 187, 191, 894, 903, 1370, 1371, 1372, 1373, 1374, 1375, 1417, 1418, 1470, 1472, 1475, 1478, 1523, 1524, 1545, 1546, 1548, 1549, 1563, 1566, 1567, 1642, 1643, 1644, 1645, 1748, 1792, 1793, 1794, 1795, 1796, 1797, 1798, 1799, 1800, 1801, 1802, 1803, 1804, 1805, 2039, 2040, 2041, 2096, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2142, 2404, 2405, 2416, 2557, 2678, 2800, 3191, 3204, 3572, 3663, 3674, 3675, 3844, 3845, 3846, 3847, 3848, 3849, 3850, 3851, 3852, 3853, 3854, 3855, 3856, 3857, 3858, 3860, 3898, 3899, 3900, 3901, 3973, 4048, 4049, 4050, 4051, 4052, 4057, 4058, 4170, 4171, 4172, 4173, 4174, 4175, 4347, 4960, 4961, 4962, 4963, 4964, 4965, 4966, 4967, 4968, 5120, 5742, 5787, 5788, 5867, 5868, 5869, 5941, 5942, 6100, 6101, 6102, 6104, 6105, 6106, 6144, 6145, 6146, 6147, 6148, 6149, 6150, 6151, 6152, 6153, 6154, 6468, 6469, 6686, 6687, 6816, 6817, 6818, 6819, 6820, 6821, 6822, 6824, 6825, 6826, 6827, 6828, 6829, 7002, 7003, 7004, 7005, 7006, 7007, 7008, 7164, 7165, 7166, 7167, 7227, 7228, 7229, 7230, 7231, 7294, 7295, 7360, 7361, 7362, 7363, 7364, 7365, 7366, 7367, 7379, 8208, 8209, 8210, 8211, 8212, 8213, 8214, 8215, 8216, 8217, 8218, 8219, 8220, 8221, 8222, 8223, 8224, 8225, 8226, 8227, 8228, 8229, 8230, 8231, 8240, 8241, 8242, 8243, 8244, 8245, 8246, 8247, 8248, 8249, 8250, 8251, 8252, 8253, 8254, 8255, 8256, 8257, 8258, 8259, 8261, 8262, 8263, 8264, 8265, 8266, 8267, 8268, 8269, 8270, 8271, 8272, 8273, 8275, 8276, 8277, 8278, 8279, 8280, 8281, 8282, 8283, 8284, 8285, 8286, 8317, 8318, 8333, 8334, 8968, 8969, 8970, 8971, 9001, 9002, 10088, 10089, 10090, 10091, 10092, 10093, 10094, 10095, 10096, 10097, 10098, 10099, 10100, 10101, 10181, 10182, 10214, 10215, 10216, 10217, 10218, 10219, 10220, 10221, 10222, 10223, 10627, 10628, 10629, 10630, 10631, 10632, 10633, 10634, 10635, 10636, 10637, 10638, 10639, 10640, 10641, 10642, 10643, 10644, 10645, 10646, 10647, 10648, 10712, 10713, 10714, 10715, 10748, 10749, 11513, 11514, 11515, 11516, 11518, 11519, 11632, 11776, 11777, 11778, 11779, 11780, 11781, 11782, 11783, 11784, 11785, 11786, 11787, 11788, 11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796, 11797, 11798, 11799, 11800, 11801, 11802, 11803, 11804, 11805, 11806, 11807, 11808, 11809, 11810, 11811, 11812, 11813, 11814, 11815, 11816, 11817, 11818, 11819, 11820, 11821, 11822, 11824, 11825, 11826, 11827, 11828, 11829, 11830, 11831, 11832, 11833, 11834, 11835, 11836, 11837, 11838, 11839, 11840, 11841, 11842, 11843, 11844, 11845, 11846, 11847, 11848, 11849, 11850, 11851, 11852, 11853, 11854, 11855, 12289, 12290, 12291, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315, 12316, 12317, 12318, 12319, 12336, 12349, 12448, 12539, 42238, 42239, 42509, 42510, 42511, 42611, 42622, 42738, 42739, 42740, 42741, 42742, 42743, 43124, 43125, 43126, 43127, 43214, 43215, 43256, 43257, 43258, 43260, 43310, 43311, 43359, 43457, 43458, 43459, 43460, 43461, 43462, 43463, 43464, 43465, 43466, 43467, 43468, 43469, 43486, 43487, 43612, 43613, 43614, 43615, 43742, 43743, 43760, 43761, 44011, 64830, 64831, 65040, 65041, 65042, 65043, 65044, 65045, 65046, 65047, 65048, 65049, 65072, 65073, 65074, 65075, 65076, 65077, 65078, 65079, 65080, 65081, 65082, 65083, 65084, 65085, 65086, 65087, 65088, 65089, 65090, 65091, 65092, 65093, 65094, 65095, 65096, 65097, 65098, 65099, 65100, 65101, 65102, 65103, 65104, 65105, 65106, 65108, 65109, 65110, 65111, 65112, 65113, 65114, 65115, 65116, 65117, 65118, 65119, 65120, 65121, 65123, 65128, 65130, 65131, 65281, 65282, 65283, 65285, 65286, 65287, 65288, 65289, 65290, 65292, 65293, 65294, 65295, 65306, 65307, 65311, 65312, 65339, 65340, 65341, 65343, 65371, 65373, 65375, 65376, 65377, 65378, 65379, 65380, 65381};
    }

    public static int HSBtoRGB(float f, float f2, float f3) {
        int i;
        int i2;
        int i3;
        float f4;
        if (f2 == 0.0f) {
            i = (int) ((f3 * 255.0f) + 0.5f);
            i2 = i;
            i3 = i2;
        } else {
            float floor = (f - ((float) Math.floor(f))) * 6.0f;
            float floor2 = floor - ((float) Math.floor(floor));
            float f5 = (1.0f - f2) * f3;
            float f6 = (1.0f - (f2 * floor2)) * f3;
            float f7 = (1.0f - (f2 * (1.0f - floor2))) * f3;
            int i4 = (int) floor;
            if (i4 == 0) {
                f5 = f7;
                f7 = f5;
            } else if (i4 != 1) {
                if (i4 == 2) {
                    f4 = f5;
                    f5 = f3;
                } else if (i4 == 3) {
                    f7 = f3;
                    f3 = f5;
                    f5 = f6;
                } else if (i4 == 4) {
                    f4 = f7;
                    f7 = f3;
                } else if (i4 != 5) {
                    i = 0;
                    i2 = 0;
                    i3 = 0;
                } else {
                    f7 = f6;
                }
                f3 = f4;
            } else {
                f7 = f5;
                f5 = f3;
                f3 = f6;
            }
            i = (int) ((f3 * 255.0f) + 0.5f);
            i2 = (int) ((f5 * 255.0f) + 0.5f);
            i3 = (int) ((f7 * 255.0f) + 0.5f);
        }
        return ((i & 255) << 16) | (-16777216) | ((i2 & 255) << 8) | (i3 & 255);
    }

    public static float[] RGBtoHSB(int i, int i2, int i3) {
        int max = Math.max(i, i2);
        if (i3 > max) {
            max = i3;
        }
        int min = Math.min(i, i2);
        if (i3 < min) {
            min = i3;
        }
        float f = max;
        float f2 = f / 255.0f;
        float f3 = 0.0f;
        float f4 = max != 0 ? (max - min) / f : 0.0f;
        if (f4 != 0.0f) {
            float f5 = max - min;
            float f6 = (max - i) / f5;
            float f7 = (max - i2) / f5;
            float f8 = (max - i3) / f5;
            float f9 = (i == max ? f8 - f7 : i2 == max ? (f6 + 2.0f) - f8 : (f7 + 4.0f) - f6) / 6.0f;
            f3 = f9 < 0.0f ? f9 + 1.0f : f9;
        }
        return new float[]{f3, f4, f2};
    }

    @Deprecated
    public static boolean addLinks(Spannable spannable, int i) {
        return addLinks(spannable, i, false);
    }

    @Deprecated
    public static boolean addLinks(Spannable spannable, int i, boolean z) {
        return addLinks(spannable, i, z, true);
    }

    @Deprecated
    public static boolean addLinks(Spannable spannable, int i, boolean z, boolean z2) {
        if (spannable == null || containsUnsupportedCharacters(spannable.toString()) || i == 0) {
            return false;
        }
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (int length = uRLSpanArr.length - 1; length >= 0; length--) {
            URLSpan uRLSpan = uRLSpanArr[length];
            if (!(uRLSpan instanceof URLSpanReplacement) || z2) {
                spannable.removeSpan(uRLSpan);
            }
        }
        ArrayList arrayList = new ArrayList();
        if (!z && (i & 4) != 0) {
            Linkify.addLinks(spannable, 4);
        }
        if ((i & 1) != 0) {
            gatherLinks(arrayList, spannable, LinkifyPort.WEB_URL, new String[]{"http://", "https://", "tg://", "tonsite://"}, sUrlMatchFilter, z);
        }
        pruneOverlaps(arrayList);
        if (arrayList.size() == 0) {
            return false;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            LinkSpec linkSpec = (LinkSpec) arrayList.get(i2);
            URLSpan[] uRLSpanArr2 = (URLSpan[]) spannable.getSpans(linkSpec.start, linkSpec.end, URLSpan.class);
            if (uRLSpanArr2 != null && uRLSpanArr2.length > 0) {
                for (URLSpan uRLSpan2 : uRLSpanArr2) {
                    spannable.removeSpan(uRLSpan2);
                    if (!(uRLSpan2 instanceof URLSpanReplacement) || z2) {
                        spannable.removeSpan(uRLSpan2);
                    }
                }
            }
            String str = linkSpec.url;
            if (str != null) {
                str = str.replaceAll("∕|⁄|%E2%81%84|%E2%88%95", "/");
            }
            if (!Browser.isTonsitePunycode(str)) {
                spannable.setSpan(new URLSpan(str), linkSpec.start, linkSpec.end, 33);
            }
        }
        return true;
    }

    public static boolean addLinksSafe(Spannable spannable, final int i, final boolean z, final boolean z2) {
        if (spannable == null) {
            return false;
        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannable);
        boolean doSafe = doSafe(new Utilities.Callback0Return() {
            @Override
            public final Object run() {
                Boolean lambda$addLinksSafe$7;
                lambda$addLinksSafe$7 = AndroidUtilities.lambda$addLinksSafe$7(spannableStringBuilder, i, z, z2);
                return lambda$addLinksSafe$7;
            }
        });
        if (doSafe) {
            for (URLSpan uRLSpan : (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                spannable.removeSpan(uRLSpan);
            }
            URLSpan[] uRLSpanArr = (URLSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);
            for (int i2 = 0; i2 < uRLSpanArr.length; i2++) {
                URLSpan uRLSpan2 = uRLSpanArr[i2];
                spannable.setSpan(uRLSpan2, spannableStringBuilder.getSpanStart(uRLSpan2), spannableStringBuilder.getSpanEnd(uRLSpanArr[i2]), 33);
            }
        }
        return doSafe;
    }

    public static void addMediaToGallery(File file) {
        Uri fromFile = Uri.fromFile(file);
        if (fromFile == null) {
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(fromFile);
            ApplicationLoader.applicationContext.sendBroadcast(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void addMediaToGallery(String str) {
        if (str == null) {
            return;
        }
        addMediaToGallery(new File(str));
    }

    public static boolean addToClipboard(CharSequence charSequence) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            if (charSequence instanceof Spanned) {
                clipboardManager.setPrimaryClip(ClipData.newHtmlText("label", charSequence, CustomHtml.toHtml((Spanned) charSequence)));
                return true;
            }
            clipboardManager.setPrimaryClip(ClipData.newPlainText("label", charSequence));
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static void adjustBrightnessColorMatrix(ColorMatrix colorMatrix, float f) {
        if (colorMatrix == null) {
            return;
        }
        float f2 = f * 255.0f;
        colorMatrix.postConcat(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, f2, 0.0f, 1.0f, 0.0f, 0.0f, f2, 0.0f, 0.0f, 1.0f, 0.0f, f2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
    }

    public static void adjustHueColorMatrix(ColorMatrix colorMatrix, float f) {
        float cleanValue = (cleanValue(f, 180.0f) / 180.0f) * 3.1415927f;
        if (cleanValue == 0.0f) {
            return;
        }
        double d = cleanValue;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        float f2 = (cos * (-0.715f)) + 0.715f;
        float f3 = ((-0.072f) * cos) + 0.072f;
        float f4 = ((-0.213f) * cos) + 0.213f;
        colorMatrix.postConcat(new ColorMatrix(new float[]{(0.787f * cos) + 0.213f + (sin * (-0.213f)), ((-0.715f) * sin) + f2, (sin * 0.928f) + f3, 0.0f, 0.0f, (0.143f * sin) + f4, (0.28500003f * cos) + 0.715f + (0.14f * sin), f3 + ((-0.283f) * sin), 0.0f, 0.0f, f4 + ((-0.787f) * sin), f2 + (0.715f * sin), (cos * 0.928f) + 0.072f + (sin * 0.072f), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f}));
    }

    public static void adjustSaturationColorMatrix(ColorMatrix colorMatrix, float f) {
        if (colorMatrix == null) {
            return;
        }
        float f2 = f + 1.0f;
        float f3 = 1.0f - f2;
        float f4 = 0.3086f * f3;
        float f5 = 0.6094f * f3;
        float f6 = f3 * 0.082f;
        colorMatrix.postConcat(new ColorMatrix(new float[]{f4 + f2, f5, f6, 0.0f, 0.0f, f4, f5 + f2, f6, 0.0f, 0.0f, f4, f5, f2 + f6, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
    }

    public static List<View> allGlobalViews() {
        List<View> globalWindowViews;
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                globalWindowViews = WindowInspector.getGlobalWindowViews();
                return globalWindowViews;
            }
            Class<?> cls = Class.forName("android.view.WindowManagerGlobal");
            Object invoke = cls.getMethod("getInstance", null).invoke(null, null);
            Method method = cls.getMethod("getViewRootNames", null);
            Method method2 = cls.getMethod("getRootView", String.class);
            String[] strArr = (String[]) method.invoke(invoke, null);
            ArrayList arrayList = new ArrayList();
            for (String str : strArr) {
                arrayList.add((View) method2.invoke(invoke, str));
            }
            return arrayList;
        } catch (Exception e) {
            FileLog.e("allGlobalViews()", e);
            return null;
        }
    }

    public static boolean allowScreenCapture() {
        return SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture;
    }

    public static void appCenterLog(Throwable th) {
        ApplicationLoader.appCenterLog(th);
    }

    public static void applySpring(Animator animator, double d, double d2) {
        applySpring(animator, d, d2, 1.0d, 0.0d);
    }

    public static void applySpring(Animator animator, double d, double d2, double d3) {
        applySpring(animator, d, d2, d3, 0.0d);
    }

    public static void applySpring(Animator animator, double d, double d2, double d3, double d4) {
        final double d5;
        final double d6;
        final double sqrt = Math.sqrt(d / d3);
        final double sqrt2 = d2 / (Math.sqrt(d * d3) * 2.0d);
        if (sqrt2 < 1.0d) {
            double sqrt3 = Math.sqrt(1.0d - (sqrt2 * sqrt2)) * sqrt;
            d6 = sqrt3;
            d5 = ((sqrt2 * sqrt) + (-d4)) / sqrt3;
        } else {
            d5 = (-d4) + sqrt;
            d6 = 0.0d;
        }
        animator.setDuration((long) ((Math.log(0.0025d) / ((-sqrt2) * sqrt)) * 1000.0d));
        final double d7 = 1.0d;
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float f) {
                double d8;
                double exp;
                double d9 = sqrt2;
                if (d9 < 1.0d) {
                    double d10 = -f;
                    Double.isNaN(d10);
                    d8 = Math.exp(d10 * d9 * sqrt);
                    double d11 = d7;
                    double d12 = d6;
                    double d13 = f;
                    Double.isNaN(d13);
                    double cos = d11 * Math.cos(d12 * d13);
                    double d14 = d5;
                    double d15 = d6;
                    Double.isNaN(d13);
                    exp = cos + (d14 * Math.sin(d15 * d13));
                } else {
                    double d16 = d7;
                    double d17 = d5;
                    double d18 = f;
                    Double.isNaN(d18);
                    d8 = d16 + (d17 * d18);
                    double d19 = -f;
                    double d20 = sqrt;
                    Double.isNaN(d19);
                    exp = Math.exp(d19 * d20);
                }
                return (float) (1.0d - (d8 * exp));
            }
        });
    }

    public static void applySpring(Animator animator, float f, float f2, float f3, long j) {
        double d = f2;
        double sqrt = Math.sqrt(f * f3) * 2.0d;
        Double.isNaN(d);
        final double d2 = d / sqrt;
        final double sqrt2 = Math.sqrt(f / f3);
        animator.setDuration(j);
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float f4) {
                double exp;
                double d3 = d2;
                if (d3 < 1.0d) {
                    double sqrt3 = sqrt2 * Math.sqrt(1.0d - (d3 * d3));
                    double d4 = (-d2) * sqrt2;
                    double d5 = f4;
                    Double.isNaN(d5);
                    double exp2 = Math.exp(d4 * d5);
                    Double.isNaN(d5);
                    double d6 = d5 * sqrt3;
                    exp = 1.0d - (exp2 * (Math.cos(d6) + (((d2 * sqrt2) / sqrt3) * Math.sin(d6))));
                } else {
                    double d7 = (-d3) * sqrt2;
                    double d8 = f4;
                    Double.isNaN(d8);
                    double d9 = d7 * d8;
                    exp = 1.0d - ((d9 + 1.0d) * Math.exp(d9));
                }
                return (float) exp;
            }
        });
    }

    public static Typeface bold() {
        Typeface typeface = mediumTypeface;
        if (typeface == null && typeface == null) {
            mediumTypeface = getTypeface("fonts/rmedium.ttf");
        }
        return mediumTypeface;
    }

    public static byte[] calcAuthKeyHash(byte[] bArr) {
        byte[] bArr2 = new byte[16];
        System.arraycopy(Utilities.computeSHA1(bArr), 0, bArr2, 0, 16);
        return bArr2;
    }

    public static int calcBitmapColor(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        try {
            Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
            if (createScaledBitmap != null) {
                int pixel = createScaledBitmap.getPixel(0, 0);
                if (bitmap != createScaledBitmap) {
                    createScaledBitmap.recycle();
                }
                return pixel;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return 0;
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int i;
        if (drawable instanceof ChatBackgroundDrawable) {
            return calcDrawableColor(((ChatBackgroundDrawable) drawable).getDrawable(true));
        }
        int[] iArr = new int[4];
        try {
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (drawable instanceof BitmapDrawable) {
            i = calcBitmapColor(((BitmapDrawable) drawable).getBitmap());
        } else if (drawable instanceof ColorDrawable) {
            i = ((ColorDrawable) drawable).getColor();
        } else {
            if (drawable instanceof BackgroundGradientDrawable) {
                int[] colorsList = ((BackgroundGradientDrawable) drawable).getColorsList();
                if (colorsList != null) {
                    if (colorsList.length > 1) {
                        i = getAverageColor(colorsList[0], colorsList[1]);
                    } else if (colorsList.length > 0) {
                        i = colorsList[0];
                    }
                }
            } else if (drawable instanceof MotionBackgroundDrawable) {
                int argb = Color.argb(45, 0, 0, 0);
                iArr[2] = argb;
                iArr[0] = argb;
                int argb2 = Color.argb(61, 0, 0, 0);
                iArr[3] = argb2;
                iArr[1] = argb2;
                return iArr;
            }
            i = -16777216;
        }
        double[] rgbToHsv = rgbToHsv((i >> 16) & 255, (i >> 8) & 255, i & 255);
        double d = rgbToHsv[1];
        rgbToHsv[1] = Math.min(1.0d, 0.05d + d + ((1.0d - d) * 0.1d));
        int[] hsvToRgb = hsvToRgb(rgbToHsv[0], rgbToHsv[1], Math.max(0.0d, rgbToHsv[2] * 0.65d));
        iArr[0] = Color.argb(102, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
        iArr[1] = Color.argb(136, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
        int[] hsvToRgb2 = hsvToRgb(rgbToHsv[0], rgbToHsv[1], Math.max(0.0d, rgbToHsv[2] * 0.72d));
        iArr[2] = Color.argb(102, hsvToRgb2[0], hsvToRgb2[1], hsvToRgb2[2]);
        iArr[3] = Color.argb(136, hsvToRgb2[0], hsvToRgb2[1], hsvToRgb2[2]);
        return iArr;
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        if (ApplicationLoader.applicationHandler == null) {
            return;
        }
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static float cascade(float f, float f2, float f3, float f4) {
        if (f3 <= 0.0f) {
            return f;
        }
        float min = (1.0f / f3) * Math.min(f4, f3);
        return MathUtils.clamp((f - ((f2 / f3) * (1.0f - min))) / min, 0.0f, 1.0f);
    }

    public static boolean charSequenceContains(CharSequence charSequence, CharSequence charSequence2) {
        return charSequenceIndexOf(charSequence, charSequence2) != -1;
    }

    public static int charSequenceIndexOf(CharSequence charSequence, CharSequence charSequence2) {
        return charSequenceIndexOf(charSequence, charSequence2, 0);
    }

    public static int charSequenceIndexOf(java.lang.CharSequence r3, java.lang.CharSequence r4, int r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.charSequenceIndexOf(java.lang.CharSequence, java.lang.CharSequence, int):int");
    }

    public static void checkAndroidTheme(Context context, boolean z) {
        if (context == null) {
            return;
        }
        context.setTheme((Theme.isCurrentThemeDark() && z) ? R.style.Theme_TMessages_Dark : R.style.Theme_TMessages);
    }

    public static void checkDisplaySize(android.content.Context r6, android.content.res.Configuration r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.checkDisplaySize(android.content.Context, android.content.res.Configuration):void");
    }

    public static boolean checkHostForPunycode(String str) {
        boolean z;
        boolean z2;
        if (str == null) {
            return false;
        }
        try {
            int length = str.length();
            z = false;
            z2 = false;
            for (int i = 0; i < length; i++) {
                try {
                    char charAt = str.charAt(i);
                    if (charAt != '.' && charAt != '-' && charAt != '/' && charAt != '+' && (charAt < '0' || charAt > '9')) {
                        if ((charAt < 'a' || charAt > 'z') && (charAt < 'A' || charAt > 'Z')) {
                            z2 = true;
                        } else {
                            z = true;
                        }
                        if (z && z2) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e = e;
                    FileLog.e(e);
                    return !z ? false : false;
                }
            }
        } catch (Exception e2) {
            e = e2;
            z = false;
            z2 = false;
        }
        if (!z && z2) {
            return true;
        }
    }

    public static boolean checkInlinePermissions(Context context) {
        boolean canDrawOverlays;
        if (Build.VERSION.SDK_INT >= 23) {
            canDrawOverlays = Settings.canDrawOverlays(context);
            if (!canDrawOverlays) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPhonePattern(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !str.equals("*")) {
            String[] split = str.split("\\*");
            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str2);
            int i = 0;
            for (String str3 : split) {
                if (!TextUtils.isEmpty(str3)) {
                    int indexOf = stripExceptNumbers.indexOf(str3, i);
                    if (indexOf == -1) {
                        return false;
                    }
                    i = indexOf + str3.length();
                }
            }
        }
        return true;
    }

    protected static float cleanValue(float f, float f2) {
        return Math.min(f2, Math.max(-f2, f));
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT < 21 || view == null) {
            return;
        }
        if (view instanceof ListView) {
            Drawable selector = ((ListView) view).getSelector();
            if (selector != null) {
                selector.setState(StateSet.NOTHING);
                return;
            }
            return;
        }
        Drawable background = view.getBackground();
        if (background != null) {
            background.setState(StateSet.NOTHING);
            background.jumpToCurrentState();
        }
    }

    public static ByteBuffer cloneByteBuffer(ByteBuffer byteBuffer) {
        ByteBuffer allocate;
        try {
            allocate = ByteBuffer.allocate(byteBuffer.capacity());
        } catch (OutOfMemoryError unused) {
            System.gc();
            allocate = ByteBuffer.allocate(byteBuffer.capacity());
        }
        int position = byteBuffer.position();
        byteBuffer.rewind();
        allocate.put(byteBuffer);
        byteBuffer.rewind();
        allocate.flip();
        allocate.position(position);
        return allocate;
    }

    public static int compare(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        return i > i2 ? 1 : -1;
    }

    public static int compare(long j, long j2) {
        if (j == j2) {
            return 0;
        }
        return j > j2 ? 1 : -1;
    }

    public static float computeDampingRatio(float f, float f2, float f3) {
        return f2 / (((float) Math.sqrt(f3 * f)) * 2.0f);
    }

    public static float computePerceivedBrightness(int i) {
        return (((Color.red(i) * 0.2126f) + (Color.green(i) * 0.7152f)) + (Color.blue(i) * 0.0722f)) / 255.0f;
    }

    public static CharSequence concat(CharSequence... charSequenceArr) {
        if (charSequenceArr.length == 0) {
            return "";
        }
        int i = 0;
        if (charSequenceArr.length == 1) {
            return charSequenceArr[0];
        }
        for (CharSequence charSequence : charSequenceArr) {
            if (charSequence instanceof Spanned) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                int length = charSequenceArr.length;
                while (i < length) {
                    CharSequence charSequence2 = charSequenceArr[i];
                    if (charSequence2 == null) {
                        charSequence2 = "null";
                    }
                    spannableStringBuilder.append(charSequence2);
                    i++;
                }
                return new SpannedString(spannableStringBuilder);
            }
        }
        StringBuilder sb = new StringBuilder();
        int length2 = charSequenceArr.length;
        while (i < length2) {
            sb.append(charSequenceArr[i]);
            i++;
        }
        return sb.toString();
    }

    private static boolean containsUnsupportedCharacters(String str) {
        if (str.contains("\u202c") || str.contains("\u202d") || str.contains("\u202e")) {
            return true;
        }
        try {
            return BAD_CHARS_PATTERN.matcher(str).find();
        } catch (Throwable unused) {
            return true;
        }
    }

    public static boolean copyFile(File file, File file2) {
        if (file.equals(file2)) {
            return true;
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, fileInputStream.getChannel().size());
                    fileOutputStream.close();
                    fileInputStream.close();
                    return true;
                } finally {
                }
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static boolean copyFile(InputStream inputStream, File file) {
        return copyFile(inputStream, new FileOutputStream(file));
    }

    public static boolean copyFile(InputStream inputStream, OutputStream outputStream) {
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read <= 0) {
                outputStream.close();
                return true;
            }
            Thread.yield();
            outputStream.write(bArr, 0, read);
        }
    }

    public static void createEmptyFile(File file) {
        try {
            if (file.exists()) {
                return;
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.flush();
            fileWriter.close();
        } catch (Throwable th) {
            FileLog.e(th, false);
        }
    }

    public static byte[] decodeQuotedPrintable(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            if (b == 61) {
                try {
                    int digit = Character.digit((char) bArr[i + 1], 16);
                    i += 2;
                    byteArrayOutputStream.write((char) ((digit << 4) + Character.digit((char) bArr[i], 16)));
                } catch (Exception e) {
                    FileLog.e(e);
                    return null;
                }
            } else {
                byteArrayOutputStream.write(b);
            }
            i++;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return byteArray;
    }

    public static float distance(float f, float f2, float f3, float f4) {
        float f5 = f3 - f;
        float f6 = f4 - f2;
        return (float) Math.sqrt((f5 * f5) + (f6 * f6));
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((f - 0.5f) * 0.47123894f);
    }

    public static void doOnLayout(final View view, final Runnable runnable) {
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                runnable.run();
            }
        });
    }

    public static boolean doSafe(Utilities.Callback0Return<Boolean> callback0Return) {
        return doSafe(callback0Return, 200);
    }

    public static boolean doSafe(final Utilities.Callback0Return<Boolean> callback0Return, int i) {
        Future future;
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        try {
            try {
                try {
                    future = newSingleThreadExecutor.submit(new Callable() {
                        @Override
                        public final Object call() {
                            Boolean lambda$doSafe$8;
                            lambda$doSafe$8 = AndroidUtilities.lambda$doSafe$8(Utilities.Callback0Return.this);
                            return lambda$doSafe$8;
                        }
                    });
                } catch (Exception e) {
                    FileLog.e(e);
                    newSingleThreadExecutor.shutdownNow();
                    return false;
                }
            } catch (TimeoutException unused) {
                future = null;
            }
            try {
                return ((Boolean) future.get(i, TimeUnit.MILLISECONDS)).booleanValue();
            } catch (TimeoutException unused2) {
                if (future != null) {
                    future.cancel(true);
                }
                newSingleThreadExecutor.shutdownNow();
                return false;
            }
        } finally {
            newSingleThreadExecutor.shutdownNow();
        }
    }

    public static int dp(float f) {
        if (f == 0.0f) {
            return 0;
        }
        return (int) Math.ceil(density * f);
    }

    public static int dp2(float f) {
        if (f == 0.0f) {
            return 0;
        }
        return (int) Math.floor(density * f);
    }

    public static float dpf2(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        return density * f;
    }

    public static int dpr(float f) {
        if (f == 0.0f) {
            return 0;
        }
        return Math.round(density * f);
    }

    public static CharSequence ellipsizeCenterEnd(CharSequence charSequence, String str, int i, TextPaint textPaint, int i2) {
        int indexOf;
        StaticLayout staticLayout;
        float lineWidth;
        float f;
        CharSequence subSequence;
        try {
            int length = charSequence.length();
            indexOf = charSequence.toString().toLowerCase().indexOf(str);
            if (length > i2) {
                charSequence = charSequence.subSequence(Math.max(0, indexOf - (i2 / 2)), Math.min(length, (i2 / 2) + indexOf));
                indexOf -= Math.max(0, indexOf - (i2 / 2));
                charSequence.length();
            }
            staticLayout = new StaticLayout(charSequence, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            lineWidth = staticLayout.getLineWidth(0);
            f = i;
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (textPaint.measureText("...") + lineWidth < f) {
            return charSequence;
        }
        int i3 = indexOf + 1;
        int i4 = i3;
        while (i4 < charSequence.length() - 1 && !Character.isWhitespace(charSequence.charAt(i4))) {
            i4++;
        }
        float primaryHorizontal = staticLayout.getPrimaryHorizontal(i4);
        if (staticLayout.isRtlCharAt(i4)) {
            primaryHorizontal = lineWidth - primaryHorizontal;
        }
        if (primaryHorizontal < f) {
            return charSequence;
        }
        float measureText = (primaryHorizontal - f) + (textPaint.measureText("...") * 2.0f);
        float f2 = 0.1f * f;
        float f3 = measureText + f2;
        if (charSequence.length() - i4 > 20) {
            f3 += f2;
        }
        if (f3 > 0.0f) {
            int offsetForHorizontal = staticLayout.getOffsetForHorizontal(0, f3);
            if (offsetForHorizontal > charSequence.length() - 1) {
                offsetForHorizontal = charSequence.length() - 1;
            }
            int i5 = 0;
            while (true) {
                if (Character.isWhitespace(charSequence.charAt(offsetForHorizontal)) || i5 >= 10) {
                    break;
                }
                i5++;
                offsetForHorizontal++;
                if (offsetForHorizontal > charSequence.length() - 1) {
                    offsetForHorizontal = staticLayout.getOffsetForHorizontal(0, f3);
                    break;
                }
            }
            if (i5 >= 10) {
                subSequence = charSequence.subSequence(staticLayout.getOffsetForHorizontal(0, staticLayout.getPrimaryHorizontal(i3) - (f * 0.3f)), charSequence.length());
            } else {
                if (offsetForHorizontal > 0 && offsetForHorizontal < charSequence.length() - 2 && Character.isWhitespace(charSequence.charAt(offsetForHorizontal))) {
                    offsetForHorizontal++;
                }
                subSequence = charSequence.subSequence(offsetForHorizontal, charSequence.length());
            }
            return SpannableStringBuilder.valueOf("...").append(subSequence);
        }
        return charSequence;
    }

    public static MotionEvent emptyMotionEvent() {
        return MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
    }

    public static void endIncomingCall() {
        if (hasCallPermissions) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                Method declaredMethod = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", null);
                declaredMethod.setAccessible(true);
                RecyclerView$ItemAnimator$$ExternalSyntheticThrowCCEIfNotNull0.m(declaredMethod.invoke(telephonyManager, null));
                RecyclerView$ItemAnimator$$ExternalSyntheticThrowCCEIfNotNull0.m(declaredMethod.invoke(telephonyManager, null));
                throw null;
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public static String escape(String str) {
        return removeRTL(removeDiacritics(str));
    }

    public static void fillStatusBarHeight(Context context, boolean z) {
        if (context != null) {
            if (statusBarHeight <= 0 || z) {
                statusBarHeight = getStatusBarHeight(context);
                navigationBarHeight = getNavigationBarHeight(context);
            }
        }
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static View findChildViewUnder(ViewGroup viewGroup, float f, float f2) {
        if (viewGroup == null || viewGroup.getVisibility() != 0) {
            return null;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt != null && childAt.getVisibility() == 0) {
                if (childAt instanceof ViewGroup) {
                    View findChildViewUnder = findChildViewUnder((ViewGroup) childAt, f - childAt.getLeft(), f2 - childAt.getTop());
                    if (findChildViewUnder != null) {
                        return findChildViewUnder;
                    }
                } else if (f >= childAt.getX() && f <= childAt.getX() + childAt.getWidth() && f2 >= childAt.getY() && f <= childAt.getY() + childAt.getHeight()) {
                    return childAt;
                }
            }
        }
        return null;
    }

    public static boolean findClickableView(ViewGroup viewGroup, float f, float f2) {
        return findClickableView(viewGroup, f, f2, null);
    }

    public static boolean findClickableView(ViewGroup viewGroup, float f, float f2, View view) {
        if (viewGroup == null) {
            return false;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0 && (!(childAt instanceof PeerStoriesView) || childAt == view)) {
                if (childAt instanceof StoryMediaAreasView.AreaView) {
                    StoryMediaAreasView storyMediaAreasView = (StoryMediaAreasView) viewGroup;
                    if ((!storyMediaAreasView.hasSelected() || (f >= dp(60.0f) && f <= viewGroup.getWidth() - dp(60.0f))) && !storyMediaAreasView.hasAreaAboveAt(f, f2)) {
                    }
                }
                Rect rect = rectTmp2;
                childAt.getHitRect(rect);
                if (rect.contains((int) f, (int) f2) && childAt.isClickable()) {
                    return true;
                }
                if ((childAt instanceof ViewGroup) && findClickableView((ViewGroup) childAt, f - childAt.getX(), f2 - childAt.getY(), view)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void fixGoogleMapsBug() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("google_bug_154855417", 0);
        if (sharedPreferences.contains("fixed")) {
            return;
        }
        new File(ApplicationLoader.getFilesDirFixed(), "ZoomTables.data").delete();
        sharedPreferences.edit().putBoolean("fixed", true).apply();
    }

    public static double fixLocationCoord(double d) {
        double d2 = (long) (d * 1000000.0d);
        Double.isNaN(d2);
        return d2 / 1000000.0d;
    }

    public static void forEachViews(View view, Consumer consumer) {
        if (!(view instanceof ViewGroup)) {
            consumer.accept(view);
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            consumer.accept(view);
            forEachViews(viewGroup.getChildAt(i), consumer);
        }
    }

    public static void forEachViews(RecyclerView recyclerView, Consumer consumer) {
        if (recyclerView == null) {
            return;
        }
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            consumer.accept(recyclerView.getChildAt(i));
        }
        for (int i2 = 0; i2 < recyclerView.getCachedChildCount(); i2++) {
            consumer.accept(recyclerView.getCachedChildAt(i2));
        }
        for (int i3 = 0; i3 < recyclerView.getHiddenChildCount(); i3++) {
            consumer.accept(recyclerView.getHiddenChildAt(i3));
        }
        for (int i4 = 0; i4 < recyclerView.getAttachedScrapChildCount(); i4++) {
            consumer.accept(recyclerView.getAttachedScrapChildAt(i4));
        }
    }

    public static String formapMapUrl(int i, double d, double d2, int i2, int i3, boolean z, int i4, int i5) {
        int min = Math.min(2, (int) Math.ceil(density));
        int i6 = i5 == -1 ? MessagesController.getInstance(i).mapProvider : i5;
        if (i6 != 1 && i6 != 3) {
            String str = MessagesController.getInstance(i).mapKey;
            if (!TextUtils.isEmpty(str)) {
                return z ? String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false&key=%s", Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), Double.valueOf(d), Double.valueOf(d2), str) : String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&key=%s", Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), str);
            }
            Locale locale = Locale.US;
            return z ? String.format(locale, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false", Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), Double.valueOf(d), Double.valueOf(d2)) : String.format(locale, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d", Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min));
        }
        String[] strArr = {"ru_RU", "tr_TR"};
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String str2 = null;
        for (int i7 = 0; i7 < 2; i7++) {
            if (strArr[i7].toLowerCase().contains(currentLocaleInfo.shortName)) {
                str2 = strArr[i7];
            }
        }
        if (str2 == null) {
            str2 = "en_US";
        }
        return z ? String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&pt=%.6f,%.6f,vkbkm&lang=%s", Double.valueOf(d2), Double.valueOf(d), Integer.valueOf(i4), Integer.valueOf(i2 * min), Integer.valueOf(i3 * min), Integer.valueOf(min), Double.valueOf(d2), Double.valueOf(d), str2) : String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&lang=%s", Double.valueOf(d2), Double.valueOf(d), Integer.valueOf(i4), Integer.valueOf(i2 * min), Integer.valueOf(i3 * min), Integer.valueOf(min), str2);
    }

    public static String formatCount(int i) {
        if (i < 1000) {
            return Integer.toString(i);
        }
        ArrayList arrayList = new ArrayList();
        while (i != 0) {
            int i2 = i % 1000;
            i /= 1000;
            arrayList.add(i > 0 ? String.format(Locale.ENGLISH, "%03d", Integer.valueOf(i2)) : Integer.toString(i2));
        }
        StringBuilder sb = new StringBuilder();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            sb.append((String) arrayList.get(size));
            if (size != 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static String formatDuration(int i, int i2, boolean z) {
        int i3 = i2 / 3600;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 % 60;
        int i6 = i / 3600;
        int i7 = (i / 60) % 60;
        int i8 = i % 60;
        if (i2 == 0) {
            Locale locale = Locale.US;
            return i6 == 0 ? z ? String.format(locale, "%02d:%02d / -:--", Integer.valueOf(i7), Integer.valueOf(i8)) : String.format(locale, "%d:%02d / -:--", Integer.valueOf(i7), Integer.valueOf(i8)) : String.format(locale, "%d:%02d:%02d / -:--", Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8));
        }
        if (i6 != 0 || i3 != 0) {
            return String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5));
        }
        Locale locale2 = Locale.US;
        return z ? String.format(locale2, "%02d:%02d / %02d:%02d", Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)) : String.format(locale2, "%d:%02d / %d:%02d", Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5));
    }

    public static String formatDuration(int i, boolean z) {
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        return i2 == 0 ? z ? String.format(Locale.US, "%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i4)) : String.format(Locale.US, "%d:%02d", Integer.valueOf(i3), Integer.valueOf(i4)) : String.format(Locale.US, "%d:%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static String formatDurationNoHours(int i, boolean z) {
        int i2 = i / 60;
        int i3 = i % 60;
        return z ? String.format(Locale.US, "%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3)) : String.format(Locale.US, "%d:%02d", Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static String formatFileSize(long j) {
        return formatFileSize(j, false, false);
    }

    public static String formatFileSize(long j, boolean z, boolean z2) {
        if (j == 0) {
            return String.format("%d KB", 0);
        }
        if (j < 1024) {
            return String.format("%d B", Long.valueOf(j));
        }
        if (j < 1048576) {
            float f = ((float) j) / 1024.0f;
            if (z) {
                int i = (int) f;
                if ((f - i) * 10.0f == 0.0f) {
                    return String.format("%d KB", Integer.valueOf(i));
                }
            }
            return String.format("%.1f KB", Float.valueOf(f));
        }
        if (j < 1048576000) {
            float f2 = (((float) j) / 1024.0f) / 1024.0f;
            if (z) {
                int i2 = (int) f2;
                if ((f2 - i2) * 10.0f == 0.0f) {
                    return String.format("%d MB", Integer.valueOf(i2));
                }
            }
            return String.format("%.1f MB", Float.valueOf(f2));
        }
        float f3 = ((int) ((j / 1024) / 1024)) / 1000.0f;
        if (z) {
            int i3 = (int) f3;
            if ((f3 - i3) * 10.0f == 0.0f) {
                return String.format("%d GB", Integer.valueOf(i3));
            }
        }
        Float valueOf = Float.valueOf(f3);
        Object[] objArr = new Object[1];
        if (z2) {
            objArr[0] = valueOf;
            return String.format("%.1f GB", objArr);
        }
        objArr[0] = valueOf;
        return String.format("%.2f GB", objArr);
    }

    public static String formatFullDuration(int i) {
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        return i < 0 ? String.format(Locale.US, "-%02d:%02d:%02d", Integer.valueOf(Math.abs(i2)), Integer.valueOf(Math.abs(i3)), Integer.valueOf(Math.abs(i4))) : String.format(Locale.US, "%02d:%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static String formatLongDuration(int i) {
        return formatDuration(i, true);
    }

    public static String formatLongDuration(int i, int i2) {
        return formatDuration(i, i2, true);
    }

    public static String formatShortDuration(int i) {
        return formatDuration(i, false);
    }

    public static String formatShortDuration(int i, int i2) {
        return formatDuration(i, i2, false);
    }

    public static SpannableStringBuilder formatSpannable(CharSequence charSequence, GenericProvider<Integer, String> genericProvider, CharSequence... charSequenceArr) {
        String charSequence2 = charSequence.toString();
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
        for (int i = 0; i < charSequenceArr.length; i++) {
            String provide = genericProvider.provide(Integer.valueOf(i));
            int indexOf = charSequence2.indexOf(provide);
            if (indexOf != -1) {
                valueOf.replace(indexOf, provide.length() + indexOf, charSequenceArr[i]);
                charSequence2 = charSequence2.substring(0, indexOf) + charSequenceArr[i].toString() + charSequence2.substring(indexOf + provide.length());
            }
        }
        return valueOf;
    }

    public static SpannableStringBuilder formatSpannable(CharSequence charSequence, CharSequence... charSequenceArr) {
        return charSequence.toString().contains("%s") ? formatSpannableSimple(charSequence, charSequenceArr) : formatSpannable(charSequence, new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                String lambda$formatSpannable$15;
                lambda$formatSpannable$15 = AndroidUtilities.lambda$formatSpannable$15((Integer) obj);
                return lambda$formatSpannable$15;
            }
        }, charSequenceArr);
    }

    public static SpannableStringBuilder formatSpannableSimple(CharSequence charSequence, CharSequence... charSequenceArr) {
        return formatSpannable(charSequence, new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                String lambda$formatSpannableSimple$14;
                lambda$formatSpannableSimple$14 = AndroidUtilities.lambda$formatSpannableSimple$14((Integer) obj);
                return lambda$formatSpannableSimple$14;
            }
        }, charSequenceArr);
    }

    public static String formatTimerDurationFast(long j, int i) {
        StringBuilder sb = new StringBuilder();
        long j2 = j / 60;
        if (j2 >= 60) {
            sb.append(j2 / 60);
            sb.append(":");
            normalizeTimePart(sb, j2 % 60);
        } else {
            sb.append(j2);
        }
        sb.append(":");
        normalizeTimePart(sb, j % 60);
        sb.append(",");
        sb.append(i / 10);
        return sb.toString();
    }

    public static String formatVideoDuration(int i, int i2) {
        int i3 = i2 / 3600;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 % 60;
        int i6 = i / 3600;
        int i7 = (i / 60) % 60;
        int i8 = i % 60;
        return (i6 == 0 && i3 == 0) ? String.format(Locale.US, "%02d:%02d / %02d:%02d", Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)) : i3 == 0 ? String.format(Locale.US, "%d:%02d:%02d / %02d:%02d", Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)) : i6 == 0 ? String.format(Locale.US, "%02d:%02d / %d:%02d:%02d", Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)) : String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5));
    }

    public static String formatVideoDurationFast(int i, int i2) {
        StringBuilder sb = new StringBuilder();
        if (i >= 60) {
            normalizeTimePart(sb, i / 60);
            sb.append(":");
            i %= 60;
        }
        normalizeTimePart(sb, i);
        sb.append(":");
        normalizeTimePart(sb, i2);
        return sb.toString();
    }

    public static String formatWholeNumber(int i, int i2) {
        if (i == 0) {
            return "0";
        }
        float f = i;
        if (i2 == 0) {
            i2 = i;
        }
        if (i2 < 1000) {
            return formatCount(i);
        }
        int i3 = 0;
        while (i2 >= 1000 && i3 < numbersSignatureArray.length - 1) {
            i2 /= 1000;
            f /= 1000.0f;
            i3++;
        }
        if (f < 0.1d) {
            return "0";
        }
        float f2 = f * 10.0f;
        float f3 = (int) f2;
        return f2 == f3 ? String.format(Locale.ENGLISH, "%s%s", formatCount((int) f), numbersSignatureArray[i3]) : String.format(Locale.ENGLISH, "%.1f%s", Float.valueOf(f3 / 10.0f), numbersSignatureArray[i3]);
    }

    private static void gatherLinks(ArrayList<LinkSpec> arrayList, Spannable spannable, Pattern pattern, String[] strArr, Linkify.MatchFilter matchFilter, boolean z) {
        if (TextUtils.indexOf((CharSequence) spannable, (char) 9472) >= 0) {
            spannable = new SpannableStringBuilder(spannable.toString().replace((char) 9472, ' '));
        }
        if (!TextUtils.isEmpty(spannable) && TextUtils.lastIndexOf(spannable, '_') == spannable.length() - 1) {
            spannable = new SpannableStringBuilder(spannable.toString()).replace(spannable.length() - 1, spannable.length(), (CharSequence) "a");
        }
        Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(spannable, start, end)) {
                LinkSpec linkSpec = new LinkSpec();
                String makeUrl = makeUrl(matcher.group(0), strArr, matcher);
                if (!z || Browser.isInternalUrl(makeUrl, true, null)) {
                    linkSpec.url = makeUrl;
                    linkSpec.start = start;
                    linkSpec.end = end;
                    arrayList.add(linkSpec);
                }
            }
        }
    }

    public static String generateFileName(int i, String str) {
        StringBuilder sb;
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + Utilities.random.nextInt(1000) + 1);
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
        if (i == 0) {
            sb = new StringBuilder();
            sb.append("IMG_");
            sb.append(format);
            sb.append(".");
            if (TextUtils.isEmpty(str)) {
                str = "jpg";
            }
        } else {
            sb = new StringBuilder();
            sb.append("VID_");
            sb.append(format);
            str = ".mp4";
        }
        sb.append(str);
        return sb.toString();
    }

    public static File generatePicturePath() {
        return generatePicturePath(false, null);
    }

    public static File generatePicturePath(boolean z, String str) {
        try {
            File directory = FileLoader.getDirectory(100);
            if (!z && directory != null) {
                return new File(directory, generateFileName(0, str));
            }
            return new File(ApplicationLoader.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), generateFileName(0, str));
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static java.lang.CharSequence generateSearchName(java.lang.String r8, java.lang.String r9, java.lang.String r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.generateSearchName(java.lang.String, java.lang.String, java.lang.String):java.lang.CharSequence");
    }

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean z) {
        try {
            File albumDir = getAlbumDir(z);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + Utilities.random.nextInt(1000) + 1);
            if (generatingVideoPathFormat == null) {
                generatingVideoPathFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US);
            }
            return new File(albumDir, "VID_" + generatingVideoPathFormat.format(date) + ".mp4");
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static Activity getActivity() {
        return getActivity(null);
    }

    public static Activity getActivity(Context context) {
        Activity findActivity = findActivity(context);
        if (findActivity == null || findActivity.isFinishing()) {
            findActivity = LaunchActivity.instance;
        }
        return (findActivity == null || findActivity.isFinishing()) ? findActivity(ApplicationLoader.applicationContext) : findActivity;
    }

    private static java.io.File getAlbumDir(boolean r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getAlbumDir(boolean):java.io.File");
    }

    public static int getAverageColor(int i, int i2) {
        return Color.argb(255, (Color.red(i) / 2) + (Color.red(i2) / 2), (Color.green(i) / 2) + (Color.green(i2) / 2), (Color.blue(i) / 2) + (Color.blue(i2) / 2));
    }

    public static void getBitmapFromSurface(Surface surface, Bitmap bitmap) {
        if (surface == null || !surface.isValid()) {
            return;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PixelCopy.request(surface, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public final void onPixelCopyFinished(int i) {
                countDownLatch.countDown();
            }
        }, Utilities.searchQueue.getHandler());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getBitmapFromSurface(SurfaceView surfaceView, Bitmap bitmap) {
        if (surfaceView == null || !surfaceView.getHolder().getSurface().isValid()) {
            return;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PixelCopy.request(surfaceView, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public final void onPixelCopyFinished(int i) {
                countDownLatch.countDown();
            }
        }, Utilities.searchQueue.getHandler());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getBitmapFromSurface(SurfaceView surfaceView, Bitmap bitmap, final Runnable runnable) {
        if (surfaceView == null || ApplicationLoader.applicationHandler == null || !surfaceView.getHolder().getSurface().isValid()) {
            return;
        }
        PixelCopy.request(surfaceView, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public final void onPixelCopyFinished(int i) {
                runnable.run();
            }
        }, ApplicationLoader.applicationHandler);
    }

    public static Uri getBitmapShareUri(Bitmap bitmap, String str, Bitmap.CompressFormat compressFormat) {
        File cacheDir = getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Exception e) {
                FileLog.e(e);
                return null;
            }
        }
        File file = new File(cacheDir, str);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                bitmap.compress(compressFormat, 87, fileOutputStream);
                fileOutputStream.close();
                Uri uriForFile = FileProvider.getUriForFile(ApplicationLoader.applicationContext, ApplicationLoader.getApplicationId() + ".provider", file);
                fileOutputStream.close();
                return uriForFile;
            } finally {
            }
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
    }

    public static File getCacheDir() {
        String str;
        String str2 = null;
        try {
            str = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e(e);
            str = null;
        }
        if (str == null || str.startsWith("mounted")) {
            FileLog.d("external dir mounted");
            try {
                File[] externalCacheDirs = ApplicationLoader.applicationContext.getExternalCacheDirs();
                int i = 0;
                File file = externalCacheDirs[0];
                if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                    while (true) {
                        if (i < externalCacheDirs.length) {
                            File file2 = externalCacheDirs[i];
                            if (file2 != null && file2.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                                file = externalCacheDirs[i];
                                break;
                            }
                            i++;
                        } else {
                            break;
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("check dir ");
                if (file != null) {
                    str2 = file.getPath();
                }
                sb.append(str2);
                sb.append(" ");
                FileLog.d(sb.toString());
                if (file != null && ((file.exists() || file.mkdirs()) && file.canWrite())) {
                    return file;
                }
                if (file != null) {
                    FileLog.d("check dir file exist " + file.exists() + " can write " + file.canWrite());
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        try {
            File cacheDir = ApplicationLoader.applicationContext.getCacheDir();
            if (cacheDir != null) {
                return cacheDir;
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        try {
            File filesDir = ApplicationLoader.applicationContext.getFilesDir();
            if (filesDir != null) {
                File file3 = new File(filesDir, "cache/");
                file3.mkdirs();
                if (filesDir.exists() || filesDir.mkdirs()) {
                    if (filesDir.canWrite()) {
                        return file3;
                    }
                }
            }
        } catch (Exception unused) {
        }
        return new File("");
    }

    public static String getCertificateSHA1Fingerprint() {
        try {
            return Utilities.bytesToHex(Utilities.computeSHA1(((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 64).signatures[0].toByteArray()))).getEncoded()));
        } catch (Throwable unused) {
            return "";
        }
    }

    public static String getCertificateSHA256Fingerprint() {
        try {
            return Utilities.bytesToHex(Utilities.computeSHA256(((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 64).signatures[0].toByteArray()))).getEncoded()));
        } catch (Throwable unused) {
            return "";
        }
    }

    public static int getColorDistance(int i, int i2) {
        int red = Color.red(i);
        int green = Color.green(i);
        int blue = Color.blue(i);
        int red2 = Color.red(i2);
        int i3 = (red + red2) / 2;
        int i4 = red - red2;
        int green2 = green - Color.green(i2);
        int blue2 = blue - Color.blue(i2);
        return ((((i3 + 512) * i4) * i4) >> 8) + (green2 * 4 * green2) + ((((767 - i3) * blue2) * blue2) >> 8);
    }

    public static float[] getCoordinateInParent(ViewGroup viewGroup, View view) {
        float f;
        float f2 = 0.0f;
        if (view != null && viewGroup != null) {
            f = 0.0f;
            float f3 = 0.0f;
            while (view != viewGroup) {
                if (view != null) {
                    f += view.getY();
                    f3 += view.getX();
                    if (view instanceof NestedScrollView) {
                        f -= view.getScrollY();
                        f3 -= view.getScrollX();
                    }
                    if (view.getParent() instanceof View) {
                        view = (View) view.getParent();
                    }
                }
            }
            f2 = f3;
            return new float[]{f2, f};
        }
        f = 0.0f;
        return new float[]{f2, f};
    }

    public static java.lang.String[] getCurrentKeyboardLanguage() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage():java.lang.String[]");
    }

    public static java.lang.String getDataColumn(android.content.Context r8, android.net.Uri r9, java.lang.String r10, java.lang.String[] r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getDataColumn(android.content.Context, android.net.Uri, java.lang.String, java.lang.String[]):java.lang.String");
    }

    public static ArrayList<File> getDataDirs() {
        ArrayList<File> arrayList = null;
        File[] externalFilesDirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null);
        if (externalFilesDirs != null) {
            for (int i = 0; i < externalFilesDirs.length; i++) {
                File file = externalFilesDirs[i];
                if (file != null) {
                    file.getAbsolutePath();
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(externalFilesDirs[i]);
                }
            }
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        if (arrayList.isEmpty()) {
            arrayList.add(Environment.getExternalStorageDirectory());
        }
        return arrayList;
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            return -1;
        }
        float height = (bitmap.getHeight() - 1) / 10.0f;
        float width = (bitmap.getWidth() - 1) / 10.0f;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < 10; i5++) {
            for (int i6 = 0; i6 < 10; i6++) {
                int pixel = bitmap.getPixel((int) (i5 * width), (int) (i6 * height));
                if (Color.alpha(pixel) > 200) {
                    i4 += Color.red(pixel);
                    i3 += Color.green(pixel);
                    i2 += Color.blue(pixel);
                    i++;
                }
            }
        }
        if (i == 0) {
            return 0;
        }
        return Color.argb(255, i4 / i, i3 / i, i2 / i);
    }

    public static String getHostAuthority(Uri uri) {
        if (uri == null) {
            return null;
        }
        return getHostAuthority(uri.toString());
    }

    public static String getHostAuthority(Uri uri, boolean z) {
        if (uri == null) {
            return null;
        }
        return getHostAuthority(uri.toString(), z);
    }

    public static String getHostAuthority(String str) {
        return getHostAuthority(str, false);
    }

    public static String getHostAuthority(String str, boolean z) {
        if (str == null) {
            return null;
        }
        Matcher matcher = getURIParsePattern().matcher(str);
        if (!matcher.matches()) {
            return null;
        }
        String group = matcher.group(4);
        if (group != null) {
            group = group.toLowerCase();
        }
        return (z && group != null && group.startsWith("www.")) ? group.substring(4) : group;
    }

    public static Pair<Integer, Integer> getImageOrientation(ExifInterface exifInterface) {
        try {
            int i = 1;
            int i2 = 270;
            switch (exifInterface.getAttributeInt("Orientation", 1)) {
                case 2:
                    i2 = 0;
                    break;
                case 3:
                    i2 = 180;
                    i = 0;
                    break;
                case 4:
                    i = 2;
                    i2 = 0;
                    break;
                case 5:
                    i = 2;
                    break;
                case 6:
                    i2 = 90;
                    i = 0;
                    break;
                case 7:
                    break;
                case 8:
                    i = 0;
                    break;
                default:
                    i = 0;
                    i2 = 0;
                    break;
            }
            return new Pair<>(Integer.valueOf(i2), Integer.valueOf(i));
        } catch (Exception e) {
            FileLog.e(e);
            return new Pair<>(0, 0);
        }
    }

    public static Pair<Integer, Integer> getImageOrientation(File file) {
        try {
            return getImageOrientation(new ExifInterface(file));
        } catch (Exception e) {
            FileLog.e(e);
            return new Pair<>(0, 0);
        }
    }

    public static Pair<Integer, Integer> getImageOrientation(InputStream inputStream) {
        try {
            return getImageOrientation(new ExifInterface(inputStream));
        } catch (Exception e) {
            FileLog.e(e);
            return new Pair<>(0, 0);
        }
    }

    public static Pair<Integer, Integer> getImageOrientation(String str) {
        try {
            return getImageOrientation(new ExifInterface(str));
        } catch (Exception unused) {
            return new Pair<>(0, 0);
        }
    }

    public static boolean getLightNavigationBar(Window window) {
        return Build.VERSION.SDK_INT >= 26 && (window.getDecorView().getSystemUiVisibility() & 16) > 0;
    }

    public static File getLogsDir() {
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File file = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null).getAbsolutePath() + "/logs");
                file.mkdirs();
                return file;
            }
        } catch (Exception unused) {
        }
        try {
            try {
                File file2 = new File(ApplicationLoader.applicationContext.getCacheDir() + "/logs");
                file2.mkdirs();
                return file2;
            } catch (Exception unused2) {
                File file3 = new File(ApplicationLoader.applicationContext.getFilesDir() + "/logs");
                file3.mkdirs();
                return file3;
            }
        } catch (Exception unused3) {
            ApplicationLoader.appCenterLog(new RuntimeException("can't create logs directory"));
            return null;
        }
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            Point point = displaySize;
            int min = Math.min(point.x, point.y);
            int i = (min * 35) / 100;
            if (i < dp(320.0f)) {
                i = dp(320.0f);
            }
            return min - i;
        }
        Point point2 = displaySize;
        int min2 = Math.min(point2.x, point2.y);
        Point point3 = displaySize;
        int max = Math.max(point3.x, point3.y);
        int i2 = (max * 35) / 100;
        if (i2 < dp(320.0f)) {
            i2 = dp(320.0f);
        }
        return Math.min(min2, max - i2);
    }

    public static int getMyLayerVersion(int i) {
        return i & 65535;
    }

    private static int getNavigationBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getOffsetColor(int i, int i2, float f, float f2) {
        int red = Color.red(i2);
        int green = Color.green(i2);
        int blue = Color.blue(i2);
        int alpha = Color.alpha(i2);
        int red2 = Color.red(i);
        int green2 = Color.green(i);
        int blue2 = Color.blue(i);
        return Color.argb((int) ((Color.alpha(i) + ((alpha - r7) * f)) * f2), (int) (red2 + ((red - red2) * f)), (int) (green2 + ((green - green2) * f)), (int) (blue2 + ((blue - blue2) * f)));
    }

    public static java.lang.String getPath(android.net.Uri r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getPath(android.net.Uri):java.lang.String");
    }

    public static int getPatternColor(int i) {
        return getPatternColor(i, false);
    }

    public static int getPatternColor(int r7, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getPatternColor(int, boolean):int");
    }

    public static int getPatternSideColor(int i) {
        float[] RGBtoHSB = RGBtoHSB(Color.red(i), Color.green(i), Color.blue(i));
        RGBtoHSB[1] = Math.min(1.0f, RGBtoHSB[1] + 0.05f);
        float f = RGBtoHSB[2];
        float max = Math.max(0.0f, f * 0.9f);
        if (f > 0.5f) {
            RGBtoHSB[2] = max;
        } else {
            RGBtoHSB[2] = max;
        }
        return HSBtoRGB(RGBtoHSB[0], RGBtoHSB[1], RGBtoHSB[2]) | (-16777216);
    }

    public static int getPeerLayerVersion(int i) {
        return Math.max(73, (i >> 16) & 65535);
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            photoSize = 1280;
        }
        return photoSize.intValue();
    }

    public static float getPixelsInCM(float f, boolean z) {
        return (f / 2.54f) * (z ? displayMetrics.xdpi : displayMetrics.ydpi);
    }

    public static long getPrefIntOrLong(SharedPreferences sharedPreferences, String str, long j) {
        try {
            return sharedPreferences.getLong(str, j);
        } catch (Exception unused) {
            return sharedPreferences.getInt(str, (int) j);
        }
    }

    public static Point getRealScreenSize() {
        Point point = new Point();
        try {
            ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRealSize(point);
        } catch (Exception e) {
            FileLog.e(e);
        }
        return point;
    }

    public static ArrayList<File> getRootDirs() {
        File externalStorageDirectory;
        String absolutePath;
        int indexOf;
        HashSet hashSet = new HashSet();
        ArrayList<File> arrayList = null;
        File[] externalFilesDirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null);
        if (externalFilesDirs != null) {
            for (File file : externalFilesDirs) {
                if (file != null && (indexOf = (absolutePath = file.getAbsolutePath()).indexOf("/Android")) >= 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    File file2 = new File(absolutePath.substring(0, indexOf));
                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayList.get(i).getPath().equals(file2.getPath());
                    }
                    if (!hashSet.contains(file2.getAbsolutePath())) {
                        hashSet.add(file2.getAbsolutePath());
                        arrayList.add(file2);
                    }
                }
            }
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        if (arrayList.isEmpty() && (externalStorageDirectory = Environment.getExternalStorageDirectory()) != null && !hashSet.contains(externalStorageDirectory.getAbsolutePath())) {
            arrayList.add(externalStorageDirectory);
        }
        return arrayList;
    }

    public static View getRootView(View view) {
        while (view != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
        }
        return view;
    }

    public static String getSafeString(String str) {
        try {
            return BAD_CHARS_MESSAGE_PATTERN.matcher(str).replaceAll("\u200c");
        } catch (Throwable unused) {
            return str;
        }
    }

    public static android.graphics.Bitmap getScaledBitmap(float r7, float r8, java.lang.String r9, java.lang.String r10, int r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getScaledBitmap(float, float, java.lang.String, java.lang.String, int):android.graphics.Bitmap");
    }

    public static int getShadowHeight() {
        float f = density;
        if (f >= 4.0f) {
            return 3;
        }
        return f >= 2.0f ? 2 : 1;
    }

    public static File getSharingDirectory() {
        return new File(FileLoader.getDirectory(4), "sharing/");
    }

    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static byte[] getStringBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception unused) {
            return new byte[0];
        }
    }

    public static Long getSysInfoLong(String str) {
        String sysInfoString = getSysInfoString(str);
        if (sysInfoString == null) {
            return null;
        }
        try {
            return Utilities.parseLong(sysInfoString);
        } catch (Exception unused) {
            return null;
        }
    }

    public static java.lang.String getSysInfoString(java.lang.String r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getSysInfoString(java.lang.String):java.lang.String");
    }

    public static String getSystemProperty(String str) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class).invoke(null, str);
        } catch (Exception unused) {
            return null;
        }
    }

    public static int getThumbForNameOrMime(String str, String str2, boolean z) {
        char charAt;
        int length;
        if (str == null || str.length() == 0) {
            return z ? documentMediaIcons[0] : documentIcons[0];
        }
        int i = (str.contains(".doc") || str.contains(".txt") || str.contains(".psd")) ? 0 : (str.contains(".xls") || str.contains(".csv")) ? 1 : (str.contains(".pdf") || str.contains(".ppt") || str.contains(".key")) ? 2 : (str.contains(".zip") || str.contains(".rar") || str.contains(".ai") || str.contains(".mp3") || str.contains(".mov") || str.contains(".avi")) ? 3 : -1;
        if (i == -1) {
            int lastIndexOf = str.lastIndexOf(46);
            String substring = lastIndexOf == -1 ? "" : str.substring(lastIndexOf + 1);
            if (substring.length() != 0) {
                charAt = substring.charAt(0);
                length = documentIcons.length;
            } else {
                charAt = str.charAt(0);
                length = documentIcons.length;
            }
            i = charAt % length;
        }
        return z ? documentMediaIcons[i] : documentIcons[i];
    }

    public static CharSequence getTrimmedString(CharSequence charSequence) {
        if (charSequence != null && charSequence.length() != 0) {
            while (charSequence.length() > 0 && (charSequence.charAt(0) == '\n' || charSequence.charAt(0) == ' ')) {
                charSequence = charSequence.subSequence(1, charSequence.length());
            }
            while (charSequence.length() > 0 && (charSequence.charAt(charSequence.length() - 1) == '\n' || charSequence.charAt(charSequence.length() - 1) == ' ')) {
                charSequence = charSequence.subSequence(0, charSequence.length() - 1);
            }
        }
        return charSequence;
    }

    public static Typeface getTypeface(String str) {
        Typeface createFromAsset;
        Typeface typeface;
        Hashtable<String, Typeface> hashtable = typefaceCache;
        synchronized (hashtable) {
            try {
                if (!hashtable.containsKey(str)) {
                    try {
                        if (Build.VERSION.SDK_INT >= 26) {
                            Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), str);
                            if (str.contains("medium")) {
                                builder.setWeight(700);
                            }
                            if (str.contains("italic")) {
                                builder.setItalic(true);
                            }
                            createFromAsset = builder.build();
                        } else {
                            createFromAsset = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str);
                        }
                        hashtable.put(str, createFromAsset);
                    } catch (Exception e) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("Could not get typeface '" + str + "' because " + e.getMessage());
                        }
                        return null;
                    }
                }
                typeface = hashtable.get(str);
            } catch (Throwable th) {
                throw th;
            }
        }
        return typeface;
    }

    public static Pattern getURIParsePattern() {
        if (uriParse == null) {
            uriParse = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
        }
        return uriParse;
    }

    public static Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
        }
        return vibrator;
    }

    public static int getViewInset(View view) {
        int i;
        WindowInsets rootWindowInsets;
        int stableInsetBottom;
        if (view != null && (i = Build.VERSION.SDK_INT) >= 21 && view.getHeight() != displaySize.y && view.getHeight() != displaySize.y - statusBarHeight) {
            try {
                if (i >= 23) {
                    rootWindowInsets = view.getRootWindowInsets();
                    if (rootWindowInsets == null) {
                        return 0;
                    }
                    stableInsetBottom = rootWindowInsets.getStableInsetBottom();
                    return stableInsetBottom;
                }
                if (mAttachInfoField == null) {
                    Field declaredField = View.class.getDeclaredField("mAttachInfo");
                    mAttachInfoField = declaredField;
                    declaredField.setAccessible(true);
                }
                Object obj = mAttachInfoField.get(view);
                if (obj != null) {
                    if (mStableInsetsField == null) {
                        Field declaredField2 = obj.getClass().getDeclaredField("mStableInsets");
                        mStableInsetsField = declaredField2;
                        declaredField2.setAccessible(true);
                    }
                    return ((Rect) mStableInsetsField.get(obj)).bottom;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return 0;
    }

    public static void getViewPositionInParent(View view, ViewGroup viewGroup, float[] fArr) {
        fArr[0] = 0.0f;
        fArr[1] = 0.0f;
        if (view == null || viewGroup == null) {
            return;
        }
        while (view != viewGroup) {
            if (!(view.getParent() instanceof ViewPager)) {
                fArr[0] = fArr[0] + view.getX();
                fArr[1] = fArr[1] + view.getY();
            }
            view = (View) view.getParent();
        }
    }

    public static String getWallPaperUrl(Object obj) {
        if (!(obj instanceof TLRPC.TL_wallPaper)) {
            if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                return ((WallpapersListActivity.ColorWallpaper) obj).getUrl();
            }
            return null;
        }
        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
        String str = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + tL_wallPaper.slug;
        StringBuilder sb = new StringBuilder();
        TLRPC.WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
        if (wallPaperSettings != null) {
            if (wallPaperSettings.blur) {
                sb.append("blur");
            }
            if (tL_wallPaper.settings.motion) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                sb.append("motion");
            }
        }
        if (sb.length() <= 0) {
            return str;
        }
        return str + "?mode=" + sb.toString();
    }

    public static int getWallpaperRotation(int i, boolean z) {
        int i2 = z ? i + 180 : i - 180;
        while (i2 >= 360) {
            i2 -= 360;
        }
        while (i2 < 0) {
            i2 += 360;
        }
        return i2;
    }

    public static Boolean getWasTablet() {
        return wasTablet;
    }

    public static void googleVoiceClientService_performAction(final Intent intent, boolean z, Bundle bundle) {
        if (z) {
            runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    AndroidUtilities.lambda$googleVoiceClientService_performAction$2(intent);
                }
            });
        }
    }

    public static boolean handleProxyIntent(android.app.Activity r13, android.content.Intent r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static boolean hasDialogOnTop(BaseFragment baseFragment) {
        List<View> allGlobalViews;
        if (baseFragment == null) {
            return false;
        }
        Dialog dialog = baseFragment.visibleDialog;
        if (dialog != null && !(dialog instanceof AlertDialog) && (!(dialog instanceof BottomSheet) || ((BottomSheet) dialog).attachedFragment == null)) {
            return true;
        }
        if (baseFragment.getParentLayout() == null || (allGlobalViews = allGlobalViews()) == null || allGlobalViews.isEmpty()) {
            return false;
        }
        View view = null;
        for (int size = allGlobalViews.size() - 1; size >= 0; size--) {
            view = allGlobalViews.get(size);
            Dialog dialog2 = baseFragment.visibleDialog;
            if ((!(dialog2 instanceof AlertDialog) || view != getRootView(((AlertDialog) dialog2).getContainerView())) && !(view instanceof AlertDialog.AlertDialogView) && !(view instanceof PipRoundVideoView.PipFrameLayout)) {
                break;
            }
        }
        return view != getRootView(baseFragment.getParentLayout().getView());
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static CharSequence highlightText(CharSequence charSequence, String str, Theme.ResourcesProvider resourcesProvider) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(charSequence)) {
            return null;
        }
        String lowerCase = charSequence.toString().toLowerCase();
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
        int indexOf = lowerCase.indexOf(str);
        while (indexOf >= 0) {
            try {
                valueOf.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), indexOf, Math.min(str.length() + indexOf, charSequence.length()), 0);
            } catch (Exception e) {
                FileLog.e(e);
            }
            indexOf = lowerCase.indexOf(str, indexOf + 1);
        }
        return valueOf;
    }

    public static CharSequence highlightText(CharSequence charSequence, ArrayList<String> arrayList, Theme.ResourcesProvider resourcesProvider) {
        if (arrayList == null) {
            return null;
        }
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            CharSequence highlightText = highlightText(charSequence, arrayList.get(i2), resourcesProvider);
            if (highlightText != null) {
                charSequence = highlightText;
            } else {
                i++;
            }
        }
        if (i == arrayList.size()) {
            return null;
        }
        return charSequence;
    }

    public static int hsvToColor(double d, double d2, double d3) {
        int[] hsvToRgb = hsvToRgb(d, d2, d3);
        return Color.argb(255, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
    }

    public static int[] hsvToRgb(double d, double d2, double d3) {
        double d4 = d * 6.0d;
        double floor = (int) Math.floor(d4);
        Double.isNaN(floor);
        double d5 = d4 - floor;
        double d6 = (1.0d - d2) * d3;
        double d7 = (1.0d - (d5 * d2)) * d3;
        double d8 = (1.0d - ((1.0d - d5) * d2)) * d3;
        int i = ((int) floor) % 6;
        if (i == 0) {
            d3 = d6;
            d6 = d8;
            d8 = d3;
        } else if (i == 1) {
            d8 = d7;
            d3 = d6;
            d6 = d3;
        } else if (i == 2) {
            d8 = d6;
            d6 = d3;
            d3 = d8;
        } else if (i == 3) {
            d8 = d6;
            d6 = d7;
        } else if (i != 4) {
            if (i != 5) {
                d3 = 0.0d;
                d8 = 0.0d;
                d6 = 0.0d;
            } else {
                d8 = d3;
                d3 = d7;
            }
        }
        return new int[]{(int) (d8 * 255.0d), (int) (d6 * 255.0d), (int) (d3 * 255.0d)};
    }

    public static int indexOfIgnoreCase(String str, String str2) {
        if (str2.isEmpty() || str.isEmpty()) {
            return str.indexOf(str2);
        }
        for (int i = 0; i < str.length() && str2.length() + i <= str.length(); i++) {
            int i2 = 0;
            for (int i3 = i; i3 < str.length() && i2 < str2.length() && Character.toLowerCase(str.charAt(i3)) == Character.toLowerCase(str2.charAt(i2)); i3++) {
                i2++;
            }
            if (i2 == str2.length()) {
                return i;
            }
        }
        return -1;
    }

    public static boolean intersect1d(float f, float f2, float f3, float f4) {
        return Math.max(f, f2) > Math.min(f3, f4) && Math.max(f3, f4) > Math.min(f, f2);
    }

    public static boolean intersect1d(int i, int i2, int i3, int i4) {
        return Math.max(i, i2) > Math.min(i3, i4) && Math.max(i3, i4) > Math.min(i, i2);
    }

    public static boolean intersect1dInclusive(int i, int i2, int i3, int i4) {
        return Math.max(i, i2) >= Math.min(i3, i4) && Math.max(i3, i4) >= Math.min(i, i2);
    }

    public static boolean isAccessibilityScreenReaderEnabled() {
        return isAccessibilityTouchExplorationEnabled();
    }

    public static boolean isAccessibilityTouchExplorationEnabled() {
        if (accessibilityManager == null) {
            accessibilityManager = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        }
        return accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    public static boolean isActivityRunning(Activity activity) {
        return (activity == null || activity.isDestroyed() || activity.isFinishing()) ? false : true;
    }

    public static boolean isAirplaneModeOn() {
        return Settings.Global.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static boolean isBannedForever(TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        return tL_chatBannedRights == null || Math.abs(((long) tL_chatBannedRights.until_date) - (System.currentTimeMillis() / 1000)) > 157680000;
    }

    public static boolean isDarkColor(int i) {
        return computePerceivedBrightness(i) < 0.721f;
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isENOSPC(java.lang.Exception r2) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isENOSPC(java.lang.Exception):boolean");
    }

    public static boolean isEROFS(java.lang.Exception r2) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isEROFS(java.lang.Exception):boolean");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isFilNotFoundException(Throwable th) {
        return (th instanceof FileNotFoundException) || (th instanceof EOFException);
    }

    public static boolean isHonor() {
        boolean z;
        if (isHonor == null) {
            try {
                String lowerCase = Build.BRAND.toLowerCase();
                if (!lowerCase.contains("huawei") && !lowerCase.contains("honor")) {
                    z = false;
                    isHonor = Boolean.valueOf(z);
                }
                z = true;
                isHonor = Boolean.valueOf(z);
            } catch (Exception e) {
                FileLog.e(e);
                isHonor = Boolean.FALSE;
            }
        }
        return isHonor.booleanValue();
    }

    public static boolean isInternalUri(int i) {
        return isInternalUri(null, i);
    }

    public static boolean isInternalUri(Uri uri) {
        return isInternalUri(uri, 0);
    }

    private static boolean isInternalUri(Uri uri, int i) {
        String str;
        if (uri == null) {
            str = "";
            int i2 = 0;
            while (str.length() <= 4096) {
                try {
                    String readlinkFd = Utilities.readlinkFd(i);
                    if (readlinkFd != null && !readlinkFd.equals(str)) {
                        i2++;
                        if (i2 >= 10) {
                            return true;
                        }
                        str = readlinkFd;
                    }
                } catch (Throwable unused) {
                    return true;
                }
            }
            return true;
        }
        str = uri.getPath();
        if (str == null) {
            return false;
        }
        if (str.matches(Pattern.quote(new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
            return false;
        }
        int i3 = 0;
        while (str.length() <= 4096) {
            try {
                String readlink = Utilities.readlink(str);
                if (readlink != null && !readlink.equals(str)) {
                    i3++;
                    if (i3 >= 10) {
                        return true;
                    }
                    str = readlink;
                }
            } catch (Throwable unused2) {
                return true;
            }
        }
        return true;
        try {
            String canonicalPath = new File(str).getCanonicalPath();
            if (canonicalPath != null) {
                str = canonicalPath;
            }
        } catch (Exception unused3) {
            str.replace("/./", "/");
        }
        if (str.endsWith(".attheme")) {
            return false;
        }
        return str.toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName());
    }

    public static boolean isKeyguardSecure() {
        return ((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).isKeyguardSecure();
    }

    public static boolean isMapsInstalled(final BaseFragment baseFragment) {
        final String mapsAppPackageName = ApplicationLoader.getMapsProvider().getMapsAppPackageName();
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo(mapsAppPackageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            if (baseFragment.getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
            builder.setMessage(LocaleController.getString(ApplicationLoader.getMapsProvider().getInstallMapsString()));
            builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AndroidUtilities.lambda$isMapsInstalled$10(mapsAppPackageName, baseFragment, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            baseFragment.showDialog(builder.create());
            return false;
        }
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    public static boolean isPunctuationCharacter(char c) {
        if (charactersMap == null) {
            charactersMap = new HashSet<>();
            int i = 0;
            while (true) {
                char[] cArr = characters;
                if (i >= cArr.length) {
                    break;
                }
                charactersMap.add(Character.valueOf(cArr[i]));
                i++;
            }
        }
        return charactersMap.contains(Character.valueOf(c));
    }

    public static boolean isRTL(CharSequence charSequence) {
        if (charSequence != null && charSequence.length() > 0) {
            for (int i = 0; i < charSequence.length(); i++) {
                char charAt = charSequence.charAt(i);
                if (charAt >= 1424 && charAt <= 1791) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSafeToShow(Context context) {
        Activity findActivity = findActivity(context);
        if (findActivity == null) {
            return true;
        }
        return isActivityRunning(findActivity);
    }

    public static boolean isSimAvailable() {
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        int simState = telephonyManager.getSimState();
        return (simState == 1 || simState == 0 || telephonyManager.getPhoneType() == 0 || isAirplaneModeOn()) ? false : true;
    }

    public static boolean isSmallScreen() {
        if (isSmallScreen == null) {
            Point point = displaySize;
            isSmallScreen = Boolean.valueOf(((float) ((Math.max(point.x, point.y) - statusBarHeight) - navigationBarHeight)) / density <= 650.0f);
        }
        return isSmallScreen.booleanValue();
    }

    public static boolean isSmallTablet() {
        Point point = displaySize;
        return ((float) Math.min(point.x, point.y)) / density <= 690.0f;
    }

    public static boolean isTablet() {
        return isTabletInternal() && !SharedConfig.forceDisableTabletMode;
    }

    public static boolean isTabletForce() {
        return ApplicationLoader.applicationContext != null && ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isTabletInternal() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(isTabletForce());
        }
        return isTablet.booleanValue();
    }

    public static boolean isValidWallChar(char c) {
        return c == '-' || c == '~';
    }

    public static boolean isWaitingForCall() {
        boolean z;
        synchronized (callLock) {
            z = waitingForCall;
        }
        return z;
    }

    public static boolean isWaitingForSms() {
        boolean z;
        synchronized (smsLock) {
            z = waitingForSms;
        }
        return z;
    }

    public static boolean isWebAppLink(java.lang.String r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isWebAppLink(java.lang.String):boolean");
    }

    public static Boolean lambda$addLinksSafe$7(SpannableStringBuilder spannableStringBuilder, int i, boolean z, boolean z2) {
        return Boolean.valueOf(addLinks(spannableStringBuilder, i, z, z2));
    }

    public static Boolean lambda$doSafe$8(Utilities.Callback0Return callback0Return) {
        try {
            return (Boolean) callback0Return.run();
        } catch (Exception e) {
            FileLog.e(e);
            return Boolean.FALSE;
        }
    }

    public static String lambda$formatSpannable$15(Integer num) {
        return "%" + (num.intValue() + 1) + "$s";
    }

    public static String lambda$formatSpannableSimple$14(Integer num) {
        return "%s";
    }

    public static void lambda$googleVoiceClientService_performAction$2(Intent intent) {
        try {
            int i = UserConfig.selectedAccount;
            ApplicationLoader.postInitApplication();
            if (!needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
                String stringExtra = intent.getStringExtra("android.intent.extra.TEXT");
                if (TextUtils.isEmpty(stringExtra)) {
                    return;
                }
                String stringExtra2 = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                long parseLong = Long.parseLong(intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
                TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(parseLong));
                if (user == null && (user = MessagesStorage.getInstance(i).getUserSync(parseLong)) != null) {
                    MessagesController.getInstance(i).putUser(user, true);
                }
                if (user != null) {
                    ContactsController.getInstance(i).markAsContacted(stringExtra2);
                    SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(stringExtra, user.id, null, null, null, true, null, null, null, true, 0, null, false));
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$isMapsInstalled$10(String str, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        try {
            baseFragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str)), 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$notifyDataSetChanged$23(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public static int lambda$pruneOverlaps$9(LinkSpec linkSpec, LinkSpec linkSpec2) {
        int i;
        int i2;
        int i3 = linkSpec.start;
        int i4 = linkSpec2.start;
        if (i3 < i4) {
            return -1;
        }
        if (i3 <= i4 && (i = linkSpec.end) >= (i2 = linkSpec2.end)) {
            return i > i2 ? -1 : 0;
        }
        return 1;
    }

    public static void lambda$recycleBitmaps$0(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            Bitmap bitmap = (Bitmap) ((WeakReference) arrayList.get(i)).get();
            ((WeakReference) arrayList.get(i)).clear();
            if (bitmap != null && !bitmap.isRecycled()) {
                try {
                    bitmap.recycle();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public static void lambda$recycleBitmaps$1(final ArrayList arrayList) {
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                AndroidUtilities.lambda$recycleBitmaps$0(arrayList);
            }
        });
    }

    public static int lambda$scrollToFragmentRow$21(BaseFragment baseFragment, String str, RecyclerListView recyclerListView) {
        try {
            Field declaredField = baseFragment.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerListView.getLayoutManager();
            int i = declaredField.getInt(baseFragment);
            try {
                linearLayoutManager.scrollToPositionWithOffset(i, dp(60.0f));
                declaredField.setAccessible(false);
                return i;
            } catch (Throwable unused) {
                return i;
            }
        } catch (Throwable unused2) {
            return -1;
        }
    }

    public static void lambda$setNavigationBarColor$20(IntColorCallback intColorCallback, Window window, ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        if (intColorCallback != null) {
            intColorCallback.run(intValue);
        }
        try {
            window.setNavigationBarColor(intValue);
        } catch (Exception unused) {
        }
    }

    public static void lambda$setWaitingForSms$11(Void r0) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("sms listener registered");
        }
    }

    public static void lambda$shakeView$12(View view, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        double d = floatValue * 4.0f * (1.0f - floatValue);
        double d2 = floatValue;
        Double.isNaN(d2);
        double sin = Math.sin(d2 * 3.141592653589793d * 4.0d);
        Double.isNaN(d);
        double d3 = d * sin;
        double dp = dp(4.0f);
        Double.isNaN(dp);
        view.setTranslationX((float) (d3 * dp));
    }

    public static void lambda$shakeViewSpring$13(Runnable runnable, View view, float f, DynamicAnimation dynamicAnimation, boolean z, float f2, float f3) {
        if (runnable != null) {
            runnable.run();
        }
        view.setTranslationX(f);
        view.setTag(R.id.spring_tag, null);
        view.setTag(R.id.spring_was_translation_x_tag, null);
    }

    public static void lambda$showProxyAlert$16(long j, TextDetailSettingsCell textDetailSettingsCell) {
        TextView textView;
        int i;
        if (j == -1) {
            textDetailSettingsCell.getTextView().setText(LocaleController.getString(R.string.Unavailable));
            textView = textDetailSettingsCell.getTextView();
            i = Theme.key_text_RedRegular;
        } else {
            textDetailSettingsCell.getTextView().setText(LocaleController.getString(R.string.Available) + ", " + LocaleController.formatString(R.string.Ping, Long.valueOf(j)));
            textView = textDetailSettingsCell.getTextView();
            i = Theme.key_windowBackgroundWhiteGreenText;
        }
        textView.setTextColor(Theme.getColor(i));
    }

    public static void lambda$showProxyAlert$17(final TextDetailSettingsCell textDetailSettingsCell, final long j) {
        runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AndroidUtilities.lambda$showProxyAlert$16(j, textDetailSettingsCell);
            }
        });
    }

    public static void lambda$showProxyAlert$19(String str, String str2, String str3, String str4, String str5, Activity activity, Runnable runnable, View view) {
        SharedConfig.ProxyInfo proxyInfo;
        UndoView undoView;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("proxy_enabled", true);
        edit.putString("proxy_ip", str);
        int intValue = Utilities.parseInt((CharSequence) str2).intValue();
        edit.putInt("proxy_port", intValue);
        if (TextUtils.isEmpty(str3)) {
            edit.remove("proxy_secret");
            if (TextUtils.isEmpty(str4)) {
                edit.remove("proxy_pass");
            } else {
                edit.putString("proxy_pass", str4);
            }
            if (TextUtils.isEmpty(str5)) {
                edit.remove("proxy_user");
            } else {
                edit.putString("proxy_user", str5);
            }
            proxyInfo = new SharedConfig.ProxyInfo(str, intValue, str5, str4, "");
        } else {
            edit.remove("proxy_pass");
            edit.remove("proxy_user");
            edit.putString("proxy_secret", str3);
            proxyInfo = new SharedConfig.ProxyInfo(str, intValue, "", "", str3);
        }
        edit.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(proxyInfo);
        ConnectionsManager.setProxySettings(true, str, intValue, str5, str4, str3);
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxySettingsChanged, new Object[0]);
        if (activity instanceof LaunchActivity) {
            BaseFragment lastFragment = ((LaunchActivity) activity).getActionBarLayout().getLastFragment();
            if (!(lastFragment instanceof ChatActivity) || (undoView = ((ChatActivity) lastFragment).getUndoView()) == null) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 6, LocaleController.getString(R.string.ProxyAddedSuccess));
            } else {
                undoView.showWithAction(0L, 87, (Runnable) null);
            }
        } else {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 6, LocaleController.getString(R.string.ProxyAddedSuccess));
        }
        runnable.run();
    }

    public static boolean lambda$static$6(CharSequence charSequence, int i, int i2) {
        return i == 0 || charSequence.charAt(i - 1) != '@';
    }

    public static void lambda$updateImageViewImageAnimated$22(ImageView imageView, AtomicBoolean atomicBoolean, Drawable drawable, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float abs = Math.abs(floatValue - 0.5f) + 0.5f;
        imageView.setScaleX(abs);
        imageView.setScaleY(abs);
        if (floatValue < 0.5f || atomicBoolean.get()) {
            return;
        }
        atomicBoolean.set(true);
        imageView.setImageDrawable(drawable);
    }

    public static double lerp(double d, double d2, float f) {
        double d3 = f;
        Double.isNaN(d3);
        return d + (d3 * (d2 - d));
    }

    public static float lerp(float f, float f2, float f3) {
        return f + (f3 * (f2 - f));
    }

    public static float lerp(boolean z, boolean z2, float f) {
        return (z ? 1.0f : 0.0f) + (f * ((z2 ? 1.0f : 0.0f) - (z ? 1.0f : 0.0f)));
    }

    public static float lerp(float[] fArr, float f) {
        return lerp(fArr[0], fArr[1], f);
    }

    public static int lerp(int i, int i2, float f) {
        return (int) (i + (f * (i2 - i)));
    }

    public static void lerp(Matrix matrix, Matrix matrix2, float f, Matrix matrix3) {
        if (matrix == null || matrix2 == null) {
            return;
        }
        float[] fArr = tempFloats;
        matrix.getValues(fArr);
        float[] fArr2 = tempFloats2;
        matrix2.getValues(fArr2);
        lerp(fArr, fArr2, f, fArr2);
        matrix3.setValues(fArr2);
    }

    public static void lerp(Rect rect, Rect rect2, float f, Rect rect3) {
        if (rect3 != null) {
            rect3.set(lerp(rect.left, rect2.left, f), lerp(rect.top, rect2.top, f), lerp(rect.right, rect2.right, f), lerp(rect.bottom, rect2.bottom, f));
        }
    }

    public static void lerp(RectF rectF, RectF rectF2, float f, RectF rectF3) {
        if (rectF3 != null) {
            rectF3.set(lerp(rectF.left, rectF2.left, f), lerp(rectF.top, rectF2.top, f), lerp(rectF.right, rectF2.right, f), lerp(rectF.bottom, rectF2.bottom, f));
        }
    }

    public static void lerp(float[] fArr, float[] fArr2, float f, float[] fArr3) {
        if (fArr3 == null) {
            return;
        }
        int i = 0;
        while (i < fArr3.length) {
            float f2 = 0.0f;
            float f3 = (fArr == null || i >= fArr.length) ? 0.0f : fArr[i];
            if (fArr2 != null && i < fArr2.length) {
                f2 = fArr2[i];
            }
            fArr3[i] = lerp(f3, f2, f);
            i++;
        }
    }

    public static void lerp(int[] iArr, int[] iArr2, float f, int[] iArr3) {
        if (iArr3 == null) {
            return;
        }
        int i = 0;
        while (i < iArr3.length) {
            iArr3[i] = lerp((iArr == null || i >= iArr.length) ? 0 : iArr[i], (iArr2 == null || i >= iArr2.length) ? 0 : iArr2[i], f);
            i++;
        }
    }

    public static float lerpAngle(float f, float f2, float f3) {
        return ((f + ((((((f2 - f) + 360.0f) + 180.0f) % 360.0f) - 180.0f) * f3)) + 360.0f) % 360.0f;
    }

    public static void lerpCentered(RectF rectF, RectF rectF2, float f, RectF rectF3) {
        if (rectF3 == null) {
            return;
        }
        float lerp = lerp(rectF.centerX(), rectF2.centerX(), f);
        float lerp2 = lerp(rectF.centerY(), rectF2.centerY(), f);
        float lerp3 = lerp(rectF.width(), rectF2.width(), Math.min(1.0f, f)) / 2.0f;
        float lerp4 = lerp(rectF.height(), rectF2.height(), Math.min(1.0f, f)) / 2.0f;
        rectF3.set(lerp - lerp3, lerp2 - lerp4, lerp + lerp3, lerp2 + lerp4);
    }

    public static ArrayList<TLRPC.User> loadVCardFromStream(Uri uri, int i, boolean z, ArrayList<VcardItem> arrayList, String str) {
        InputStream createInputStream;
        String str2;
        byte[] decodeQuotedPrintable;
        VcardItem vcardItem;
        ArrayList<VcardItem> arrayList2 = arrayList;
        ArrayList<TLRPC.User> arrayList3 = 0;
        arrayList3 = 0;
        if (z) {
            try {
                createInputStream = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri, "r").createInputStream();
            } catch (Throwable th) {
                th = th;
                FileLog.e(th);
                return arrayList3;
            }
        } else {
            try {
                createInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            } catch (Throwable th2) {
                th = th2;
                arrayList3 = 0;
                FileLog.e(th);
                return arrayList3;
            }
        }
        ArrayList arrayList4 = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(createInputStream, "UTF-8"));
        int i2 = 0;
        VcardData vcardData = null;
        String str3 = null;
        VcardItem vcardItem2 = null;
        boolean z2 = false;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                try {
                    break;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (readLine.startsWith("PHOTO")) {
                z2 = true;
            } else {
                if (readLine.indexOf(58) >= 0) {
                    if (readLine.startsWith("BEGIN:VCARD")) {
                        vcardData = new VcardData();
                        arrayList4.add(vcardData);
                        vcardData.name = str;
                    } else if (!readLine.startsWith("END:VCARD") && arrayList2 != null) {
                        if (readLine.startsWith("TEL")) {
                            vcardItem = new VcardItem();
                            vcardItem.type = i2;
                        } else if (readLine.startsWith("EMAIL")) {
                            vcardItem = new VcardItem();
                            vcardItem.type = 1;
                        } else {
                            if (!readLine.startsWith("ADR") && !readLine.startsWith("LABEL") && !readLine.startsWith("GEO")) {
                                if (readLine.startsWith("URL")) {
                                    vcardItem = new VcardItem();
                                    vcardItem.type = 3;
                                } else if (readLine.startsWith("NOTE")) {
                                    vcardItem = new VcardItem();
                                    vcardItem.type = 4;
                                } else if (readLine.startsWith("BDAY")) {
                                    vcardItem = new VcardItem();
                                    vcardItem.type = 5;
                                } else {
                                    if (!readLine.startsWith("ORG") && !readLine.startsWith("TITLE") && !readLine.startsWith("ROLE")) {
                                        if (readLine.startsWith("X-ANDROID")) {
                                            vcardItem = new VcardItem();
                                            vcardItem.type = -1;
                                        } else if (!readLine.startsWith("X-PHONETIC") && readLine.startsWith("X-")) {
                                            vcardItem = new VcardItem();
                                            vcardItem.type = 20;
                                        } else {
                                            vcardItem = arrayList3;
                                        }
                                    }
                                    vcardItem = new VcardItem();
                                    vcardItem.type = 6;
                                }
                            }
                            vcardItem = new VcardItem();
                            vcardItem.type = 2;
                        }
                        if (vcardItem != null && vcardItem.type >= 0) {
                            arrayList2.add(vcardItem);
                        }
                        vcardItem2 = vcardItem;
                        z2 = false;
                    }
                    vcardItem = arrayList3;
                    vcardItem2 = vcardItem;
                    z2 = false;
                }
                if (!z2 && vcardData != null) {
                    if (vcardItem2 == null) {
                        if (vcardData.vcard.length() > 0) {
                            vcardData.vcard.append('\n');
                        }
                        vcardData.vcard.append(readLine);
                    } else {
                        vcardItem2.vcardData.add(readLine);
                    }
                }
                if (str3 != null) {
                    readLine = str3 + readLine;
                    str3 = null;
                }
                if (readLine.contains("=QUOTED-PRINTABLE") && readLine.endsWith("=")) {
                    str3 = readLine.substring(i2, readLine.length() - 1);
                    arrayList3 = 0;
                } else {
                    if (!z2 && vcardData != null && vcardItem2 != null) {
                        vcardItem2.fullData = readLine;
                    }
                    int indexOf = readLine.indexOf(":");
                    String[] strArr = indexOf >= 0 ? new String[]{readLine.substring(i2, indexOf), readLine.substring(indexOf + 1).trim()} : new String[]{readLine.trim()};
                    if (strArr.length >= 2 && vcardData != null) {
                        if (!strArr[i2].startsWith("FN") && !strArr[i2].startsWith("N") && (!strArr[i2].startsWith("ORG") || !TextUtils.isEmpty(vcardData.name))) {
                            if (strArr[i2].startsWith("TEL")) {
                                vcardData.phones.add(strArr[1]);
                            }
                        }
                        String[] split = strArr[i2].split(";");
                        int length = split.length;
                        int i3 = 0;
                        String str4 = null;
                        String str5 = null;
                        while (i3 < length) {
                            String[] split2 = split[i3].split("=");
                            String[] strArr2 = split;
                            String str6 = str3;
                            if (split2.length == 2) {
                                if (split2[0].equals("CHARSET")) {
                                    str5 = split2[1];
                                } else if (split2[0].equals("ENCODING")) {
                                    str4 = split2[1];
                                }
                            }
                            i3++;
                            split = strArr2;
                            str3 = str6;
                        }
                        str2 = str3;
                        vcardData.name = strArr[0].startsWith("N") ? strArr[1].replace(';', ' ').trim() : strArr[1];
                        String str7 = str4;
                        if (str7 != null && str7.equalsIgnoreCase("QUOTED-PRINTABLE") && (decodeQuotedPrintable = decodeQuotedPrintable(getStringBytes(vcardData.name))) != null && decodeQuotedPrintable.length != 0) {
                            vcardData.name = new String(decodeQuotedPrintable, str5);
                        }
                        arrayList2 = arrayList;
                        str3 = str2;
                        arrayList3 = 0;
                        i2 = 0;
                    }
                    str2 = str3;
                    arrayList2 = arrayList;
                    str3 = str2;
                    arrayList3 = 0;
                    i2 = 0;
                }
            }
        }
        bufferedReader.close();
        createInputStream.close();
        ArrayList<TLRPC.User> arrayList5 = null;
        for (int i4 = 0; i4 < arrayList4.size(); i4++) {
            try {
                VcardData vcardData2 = (VcardData) arrayList4.get(i4);
                if (vcardData2.name != null && !vcardData2.phones.isEmpty()) {
                    ArrayList<TLRPC.User> arrayList6 = arrayList5 == null ? new ArrayList<>() : arrayList5;
                    try {
                        String str8 = vcardData2.phones.get(0);
                        int i5 = 0;
                        while (true) {
                            if (i5 >= vcardData2.phones.size()) {
                                break;
                            }
                            String str9 = vcardData2.phones.get(i5);
                            if (ContactsController.getInstance(i).contactsByShortPhone.get(str9.substring(Math.max(0, str9.length() - 7))) != null) {
                                str8 = str9;
                                break;
                            }
                            i5++;
                        }
                        TLRPC.TL_userContact_old2 tL_userContact_old2 = new TLRPC.TL_userContact_old2();
                        tL_userContact_old2.phone = str8;
                        tL_userContact_old2.first_name = vcardData2.name;
                        tL_userContact_old2.last_name = "";
                        tL_userContact_old2.id = 0L;
                        TLRPC.RestrictionReason restrictionReason = new TLRPC.RestrictionReason();
                        restrictionReason.text = vcardData2.vcard.toString();
                        restrictionReason.platform = "";
                        restrictionReason.reason = "";
                        tL_userContact_old2.restriction_reason.add(restrictionReason);
                        arrayList6.add(tL_userContact_old2);
                        arrayList5 = arrayList6;
                    } catch (Throwable th3) {
                        th = th3;
                        arrayList3 = arrayList6;
                        FileLog.e(th);
                        return arrayList3;
                    }
                }
            } catch (Throwable th4) {
                th = th4;
                arrayList3 = arrayList5;
            }
        }
        return arrayList5;
    }

    public static void lockOrientation(android.app.Activity r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.lockOrientation(android.app.Activity):void");
    }

    public static void lockOrientation(Activity activity, int i) {
        if (activity == null) {
            return;
        }
        try {
            prevOrientation = activity.getRequestedOrientation();
            activity.setRequestedOrientation(i);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void makeAccessibilityAnnouncement(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        AccessibilityManager accessibilityManager2 = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (accessibilityManager2.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(16384);
            obtain.getText().add(charSequence);
            accessibilityManager2.sendAccessibilityEvent(obtain);
        }
    }

    public static Bitmap makeBlurBitmap(View view) {
        return makeBlurBitmap(view, 6.0f, 7);
    }

    public static Bitmap makeBlurBitmap(View view, float f, int i) {
        if (view == null) {
            return null;
        }
        int width = (int) (view.getWidth() / f);
        int height = (int) (view.getHeight() / f);
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        float f2 = 1.0f / f;
        canvas.scale(f2, f2);
        canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        view.draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(i, Math.max(width, height) / 180));
        return createBitmap;
    }

    public static SpannableStringBuilder makeClickable(String str, final int i, final Runnable runnable, final Theme.ResourcesProvider resourcesProvider) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        if (i == 0 || i == 3 || i == 2 || i == 4) {
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(i == 4);
                    if (i == 2) {
                        textPaint.setTypeface(AndroidUtilities.bold());
                    }
                }
            }, 0, spannableStringBuilder.length(), 0);
        } else {
            spannableStringBuilder.setSpan(new CharacterStyle() {
                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setTypeface(AndroidUtilities.bold());
                    int alpha = textPaint.getAlpha();
                    textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, Theme.ResourcesProvider.this));
                    textPaint.setAlpha(alpha);
                }
            }, 0, spannableStringBuilder.length(), 0);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder makeClickable(String str, Runnable runnable) {
        return makeClickable(str, 0, runnable, null);
    }

    public static void makeGlobalBlurBitmap(Utilities.Callback<Bitmap> callback, float f) {
        makeGlobalBlurBitmap(callback, f, (int) f, null, null);
    }

    public static void makeGlobalBlurBitmap(Utilities.Callback<Bitmap> callback, float f, int i, View view, List<View> list) {
        int width;
        int height;
        if (callback == null) {
            return;
        }
        List<View> allGlobalViews = allGlobalViews();
        if (allGlobalViews == null) {
            callback.run(null);
            return;
        }
        makingGlobalBlurBitmap = true;
        try {
            try {
                if (view == null) {
                    width = (int) (r7.x / f);
                    height = displaySize.y + statusBarHeight;
                } else {
                    width = (int) (view.getWidth() / f);
                    height = view.getHeight();
                }
                int i2 = (int) (height / f);
                int[] iArr = new int[2];
                Bitmap createBitmap = Bitmap.createBitmap(width, i2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                if (view != null) {
                    view.getLocationOnScreen(iArr);
                    canvas.translate((-iArr[0]) / f, (-iArr[1]) / f);
                }
                float f2 = 1.0f / f;
                canvas.scale(f2, f2);
                canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                for (int i3 = 0; i3 < allGlobalViews.size(); i3++) {
                    View view2 = allGlobalViews.get(i3);
                    if (!(view2 instanceof PipRoundVideoView.PipFrameLayout) && (list == null || !list.contains(view2))) {
                        ViewGroup.LayoutParams layoutParams = view2.getLayoutParams();
                        if (layoutParams instanceof WindowManager.LayoutParams) {
                            WindowManager.LayoutParams layoutParams2 = (WindowManager.LayoutParams) layoutParams;
                            if ((layoutParams2.flags & 2) != 0) {
                                canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (layoutParams2.dimAmount * 255.0f)));
                            }
                        }
                        canvas.save();
                        view2.getLocationOnScreen(iArr);
                        canvas.translate(iArr[0] / f, iArr[1] / f);
                        try {
                            view2.draw(canvas);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        canvas.restore();
                    }
                }
                Utilities.stackBlurBitmap(createBitmap, Math.max(i, Math.max(width, i2) / 180));
                callback.run(createBitmap);
            } catch (Exception e2) {
                FileLog.e(e2);
                callback.run(null);
            }
            makingGlobalBlurBitmap = false;
        } catch (Throwable th) {
            makingGlobalBlurBitmap = false;
            throw th;
        }
    }

    private static String makeUrl(String str, String[] strArr, Matcher matcher) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= strArr.length) {
                z = false;
                break;
            }
            String str2 = strArr[i];
            if (str.regionMatches(true, 0, str2, 0, str2.length())) {
                String str3 = strArr[i];
                z = true;
                if (!str.regionMatches(false, 0, str3, 0, str3.length())) {
                    str = strArr[i] + str.substring(strArr[i].length());
                }
            } else {
                i++;
            }
        }
        if (z || strArr.length <= 0) {
            return str;
        }
        return strArr[0] + str;
    }

    public static int multiplyAlphaComponent(int i, float f) {
        return ColorUtils.setAlphaComponent(i, (int) (Color.alpha(i) * f));
    }

    public static void multiplyBrightnessColorMatrix(ColorMatrix colorMatrix, float f) {
        if (colorMatrix == null) {
            return;
        }
        colorMatrix.postConcat(new ColorMatrix(new float[]{f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
    }

    public static boolean needShowPasscode() {
        return needShowPasscode(false);
    }

    public static boolean needShowPasscode(boolean z) {
        boolean isWasInBackground = ForegroundDetector.getInstance().isWasInBackground(z);
        if (z) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        int elapsedRealtime = (int) (SystemClock.elapsedRealtime() / 1000);
        if (BuildVars.LOGS_ENABLED && z && SharedConfig.passcodeHash.length() > 0) {
            FileLog.d("wasInBackground = " + isWasInBackground + " appLocked = " + SharedConfig.appLocked + " autoLockIn = " + SharedConfig.autoLockIn + " lastPauseTime = " + SharedConfig.lastPauseTime + " uptime = " + elapsedRealtime);
        }
        return SharedConfig.passcodeHash.length() > 0 && isWasInBackground && (SharedConfig.appLocked || (!(SharedConfig.autoLockIn == 0 || SharedConfig.lastPauseTime == 0 || SharedConfig.appLocked || SharedConfig.lastPauseTime + SharedConfig.autoLockIn > elapsedRealtime) || elapsedRealtime + 5 < SharedConfig.lastPauseTime));
    }

    public static void normalizeTimePart(StringBuilder sb, int i) {
        if (i < 10) {
            sb.append("0");
        }
        sb.append(i);
    }

    public static void normalizeTimePart(StringBuilder sb, long j) {
        if (j < 10) {
            sb.append("0");
        }
        sb.append(j);
    }

    public static void notifyDataSetChanged(final RecyclerView recyclerView) {
        if (recyclerView == null || recyclerView.getAdapter() == null) {
            return;
        }
        if (recyclerView.isComputingLayout()) {
            recyclerView.post(new Runnable() {
                @Override
                public final void run() {
                    AndroidUtilities.lambda$notifyDataSetChanged$23(RecyclerView.this);
                }
            });
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public static String obtainLoginPhoneCall(String str) {
        if (!hasCallPermissions) {
            return null;
        }
        try {
            Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"number", "date"}, "type IN (3,1,5)", null, Build.VERSION.SDK_INT >= 26 ? "date DESC" : "date DESC LIMIT 5");
            while (query.moveToNext()) {
                try {
                    String string = query.getString(0);
                    long j = query.getLong(1);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("number = " + string);
                    }
                    if (Math.abs(System.currentTimeMillis() - j) < 3600000 && checkPhonePattern(str, string)) {
                        query.close();
                        return string;
                    }
                } finally {
                }
            }
            query.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    public static void openDocument(org.telegram.messenger.MessageObject r20, android.app.Activity r21, org.telegram.ui.ActionBar.BaseFragment r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openDocument(org.telegram.messenger.MessageObject, android.app.Activity, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    public static boolean openForView(java.io.File r6, java.lang.String r7, java.lang.String r8, android.app.Activity r9, org.telegram.ui.ActionBar.Theme.ResourcesProvider r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(java.io.File, java.lang.String, java.lang.String, android.app.Activity, org.telegram.ui.ActionBar.Theme$ResourcesProvider, boolean):boolean");
    }

    public static boolean openForView(MessageObject messageObject, Activity activity, Theme.ResourcesProvider resourcesProvider, boolean z) {
        String str = messageObject.messageOwner.attachPath;
        File file = (str == null || str.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
        if (file == null || !file.exists()) {
            file = FileLoader.getInstance(messageObject.currentAccount).getPathToMessage(messageObject.messageOwner);
        }
        File file2 = file;
        int i = messageObject.type;
        return openForView(file2, messageObject.getFileName(), (i == 9 || i == 0) ? messageObject.getMimeType() : null, activity, resourcesProvider, z);
    }

    public static boolean openForView(org.telegram.tgnet.TLObject r8, android.app.Activity r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(org.telegram.tgnet.TLObject, android.app.Activity):boolean");
    }

    public static boolean openForView(TLRPC.Document document, boolean z, Activity activity) {
        return openForView(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true), FileLoader.getAttachFileName(document), document.mime_type, activity, null, false);
    }

    public static void openSharing(BaseFragment baseFragment, String str) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        baseFragment.showDialog(new ShareAlert(baseFragment.getParentActivity(), null, str, false, str, false));
    }

    public static SpannableStringBuilder premiumText(String str, Runnable runnable) {
        return replaceSingleTag(str, -1, 2, runnable);
    }

    private static void pruneOverlaps(ArrayList<LinkSpec> arrayList) {
        int i;
        int i2;
        int i3;
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$pruneOverlaps$9;
                lambda$pruneOverlaps$9 = AndroidUtilities.lambda$pruneOverlaps$9((AndroidUtilities.LinkSpec) obj, (AndroidUtilities.LinkSpec) obj2);
                return lambda$pruneOverlaps$9;
            }
        });
        int size = arrayList.size();
        int i4 = 0;
        while (i4 < size - 1) {
            LinkSpec linkSpec = arrayList.get(i4);
            int i5 = i4 + 1;
            LinkSpec linkSpec2 = arrayList.get(i5);
            int i6 = linkSpec.start;
            int i7 = linkSpec2.start;
            if (i6 <= i7 && (i = linkSpec.end) > i7) {
                int i8 = linkSpec2.end;
                int i9 = (i8 > i && (i2 = i - i6) <= (i3 = i8 - i7)) ? i2 < i3 ? i4 : -1 : i5;
                if (i9 != -1) {
                    arrayList.remove(i9);
                    size--;
                }
            }
            i4 = i5;
        }
    }

    public static void quietSleep(long j) {
        try {
            Thread.sleep(j);
        } catch (InterruptedException unused) {
        }
    }

    public static String readRes(int i) {
        return readRes(null, i);
    }

    public static String readRes(File file) {
        return readRes(file, 0);
    }

    public static String readRes(File file, int i) {
        InputStream inputStream;
        byte[] bArr = readBufferLocal.get();
        if (bArr == null) {
            bArr = new byte[65536];
            readBufferLocal.set(bArr);
        }
        try {
            inputStream = file != null ? new FileInputStream(file) : ApplicationLoader.applicationContext.getResources().openRawResource(i);
        } catch (Throwable unused) {
            inputStream = null;
        }
        try {
            byte[] bArr2 = bufferLocal.get();
            if (bArr2 == null) {
                bArr2 = new byte[4096];
                bufferLocal.set(bArr2);
            }
            int i2 = 0;
            while (true) {
                int read = inputStream.read(bArr2, 0, bArr2.length);
                if (read >= 0) {
                    int i3 = i2 + read;
                    if (bArr.length < i3) {
                        byte[] bArr3 = new byte[bArr.length * 2];
                        System.arraycopy(bArr, 0, bArr3, 0, i2);
                        readBufferLocal.set(bArr3);
                        bArr = bArr3;
                    }
                    if (read > 0) {
                        System.arraycopy(bArr2, 0, bArr, i2, read);
                        i2 = i3;
                    }
                } else {
                    try {
                        break;
                    } catch (Throwable unused2) {
                    }
                }
            }
            inputStream.close();
            return new String(bArr, 0, i2);
        } catch (Throwable unused3) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable unused4) {
                }
            }
            return null;
        }
    }

    public static void recycleBitmap(Bitmap bitmap) {
        recycleBitmaps(Collections.singletonList(bitmap));
    }

    public static void recycleBitmaps(List<Bitmap> list) {
        if (Build.VERSION.SDK_INT <= 23 || list == null || list.isEmpty()) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = list.get(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                arrayList.add(new WeakReference(bitmap));
            }
        }
        runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AndroidUtilities.lambda$recycleBitmaps$1(arrayList);
            }
        }, 36L);
    }

    public static void removeAdjustResize(Activity activity, int i) {
        if (activity == null || isTablet() || adjustOwnerClassGuid != i) {
            return;
        }
        activity.getWindow().setSoftInputMode(32);
    }

    public static void removeAltFocusable(Activity activity, int i) {
        if (activity != null && altFocusableClassGuid == i) {
            activity.getWindow().clearFlags(131072);
        }
    }

    public static String removeDiacritics(String str) {
        Matcher matcher;
        if (str == null) {
            return null;
        }
        Pattern pattern = REMOVE_MULTIPLE_DIACRITICS;
        return (pattern == null || (matcher = pattern.matcher(str)) == null) ? str : matcher.replaceAll("$1");
    }

    public static void removeFromParent(View view) {
        if (view == null || view.getParent() == null) {
            return;
        }
        ((ViewGroup) view.getParent()).removeView(view);
    }

    public static String removeRTL(String str) {
        if (str == null) {
            return null;
        }
        if (REMOVE_RTL == null) {
            REMOVE_RTL = Pattern.compile("[\\u200E\\u200F\\u202A-\\u202E]");
        }
        Matcher matcher = REMOVE_RTL.matcher(str);
        return matcher == null ? str : matcher.replaceAll("");
    }

    public static CharSequence removeSpans(CharSequence charSequence, Class cls) {
        if (!(charSequence instanceof Spannable)) {
            return charSequence;
        }
        Spannable spannable = (Spannable) charSequence;
        for (Object obj : spannable.getSpans(0, spannable.length(), cls)) {
            spannable.removeSpan(obj);
        }
        return spannable;
    }

    public static CharSequence replaceArrows(CharSequence charSequence, boolean z) {
        return replaceArrows(charSequence, z, dp(2.6666667f), 0.0f);
    }

    public static CharSequence replaceArrows(CharSequence charSequence, boolean z, float f, float f2) {
        int i = R.drawable.msg_mini_forumarrow;
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(i, 0);
        coloredImageSpan.setScale(0.88f, 0.88f);
        coloredImageSpan.translate(-f, f2);
        coloredImageSpan.spaceScaleX = 0.8f;
        if (z) {
            coloredImageSpan.useLinkPaintColor = z;
        }
        SpannableString spannableString = new SpannableString(" >");
        spannableString.setSpan(coloredImageSpan, spannableString.length() - 1, spannableString.length(), 33);
        CharSequence replaceMultipleCharSequence = replaceMultipleCharSequence(" >", charSequence, spannableString);
        SpannableString spannableString2 = new SpannableString(">");
        spannableString2.setSpan(coloredImageSpan, 0, 1, 33);
        CharSequence replaceMultipleCharSequence2 = replaceMultipleCharSequence(">", replaceMultipleCharSequence, spannableString2);
        ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(i, 0);
        coloredImageSpan2.setScale(0.88f, 0.88f);
        coloredImageSpan2.translate(f, f2);
        coloredImageSpan2.rotate(180.0f);
        coloredImageSpan2.spaceScaleX = 0.8f;
        if (z) {
            coloredImageSpan2.useLinkPaintColor = z;
        }
        SpannableString spannableString3 = new SpannableString("<");
        spannableString3.setSpan(coloredImageSpan2, 0, 1, 33);
        return replaceMultipleCharSequence("<", replaceMultipleCharSequence2, spannableString3);
    }

    public static SpannableStringBuilder replaceCharSequence(String str, CharSequence charSequence, CharSequence charSequence2) {
        SpannableStringBuilder spannableStringBuilder = charSequence instanceof SpannableStringBuilder ? (SpannableStringBuilder) charSequence : new SpannableStringBuilder(charSequence);
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf >= 0) {
            spannableStringBuilder.replace(indexOf, str.length() + indexOf, charSequence2);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceLinks(String str, Theme.ResourcesProvider resourcesProvider) {
        return replaceLinks(str, resourcesProvider, null);
    }

    public static SpannableStringBuilder replaceLinks(String str, final Theme.ResourcesProvider resourcesProvider, final Runnable runnable) {
        if (linksPattern == null) {
            linksPattern = Pattern.compile("\\[(.+?)\\]\\((.+?)\\)");
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        Matcher matcher = linksPattern.matcher(str);
        int i = 0;
        while (matcher.find()) {
            spannableStringBuilder.append((CharSequence) str, i, matcher.start());
            String group = matcher.group(1);
            final String group2 = matcher.group(2);
            spannableStringBuilder.append((CharSequence) group);
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                    Browser.openUrl(ApplicationLoader.applicationContext, group2);
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                    textPaint.setUnderlineText(false);
                }
            }, spannableStringBuilder.length() - group.length(), spannableStringBuilder.length(), 33);
            i = matcher.end();
        }
        spannableStringBuilder.append((CharSequence) str, i, str.length());
        return spannableStringBuilder;
    }

    public static CharSequence replaceMultipleCharSequence(String str, CharSequence charSequence, CharSequence charSequence2) {
        SpannableStringBuilder spannableStringBuilder = charSequence instanceof SpannableStringBuilder ? (SpannableStringBuilder) charSequence : new SpannableStringBuilder(charSequence);
        int indexOf = TextUtils.indexOf(charSequence, str, 0);
        while (indexOf >= 0) {
            spannableStringBuilder.replace(indexOf, str.length() + indexOf, charSequence2);
            indexOf = TextUtils.indexOf(spannableStringBuilder, str, indexOf + 1);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceMultipleTags(String str, Runnable... runnableArr) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        for (final Runnable runnable : runnableArr) {
            int charSequenceIndexOf = charSequenceIndexOf(spannableStringBuilder, "**");
            int i = charSequenceIndexOf + 2;
            int charSequenceIndexOf2 = charSequenceIndexOf(spannableStringBuilder, "**", i);
            if (charSequenceIndexOf < 0 || charSequenceIndexOf2 < 0) {
                break;
            }
            spannableStringBuilder.delete(charSequenceIndexOf, i);
            int i2 = charSequenceIndexOf2 - 2;
            spannableStringBuilder.delete(i2, charSequenceIndexOf2);
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(false);
                }
            }, charSequenceIndexOf, i2, 33);
        }
        return spannableStringBuilder;
    }

    public static CharSequence replaceNewLines(CharSequence charSequence) {
        int i = 0;
        if (charSequence instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) charSequence;
            int length = charSequence.length();
            while (i < length) {
                if (charSequence.charAt(i) == '\n') {
                    sb.setCharAt(i, ' ');
                }
                i++;
            }
            return charSequence;
        }
        if (charSequence instanceof SpannableStringBuilder) {
            SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
            int length2 = charSequence.length();
            while (i < length2) {
                if (charSequence.charAt(i) == '\n') {
                    spannableStringBuilder.replace(i, i + 1, (CharSequence) " ");
                }
                i++;
            }
            return spannableStringBuilder;
        }
        if (!(charSequence instanceof SpannableString)) {
            return charSequence.toString().replace('\n', ' ');
        }
        if (TextUtils.indexOf(charSequence, '\n') < 0) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(charSequence);
        int length3 = charSequence.length();
        while (i < length3) {
            if (charSequence.charAt(i) == '\n') {
                spannableStringBuilder2.replace(i, i + 1, (CharSequence) " ");
            }
            i++;
        }
        return spannableStringBuilder2;
    }

    public static SpannableStringBuilder replaceSingleLink(String str, int i) {
        return replaceSingleLink(str, i, null);
    }

    public static SpannableStringBuilder replaceSingleLink(String str, final int i, final Runnable runnable) {
        int i2;
        int i3;
        int indexOf = str.indexOf("**");
        int indexOf2 = str.indexOf("**", indexOf + 1);
        String replace = str.replace("**", "");
        if (indexOf < 0 || indexOf2 < 0 || (i3 = indexOf2 - indexOf) <= 2) {
            indexOf = -1;
            i2 = 0;
        } else {
            i2 = i3 - 2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replace);
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(runnable != null ? new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(false);
                    textPaint.setColor(i);
                }
            } : new CharacterStyle() {
                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setUnderlineText(false);
                    textPaint.setColor(i);
                }
            }, indexOf, i2 + indexOf, 0);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceSingleTag(String str, int i, int i2, Runnable runnable) {
        return replaceSingleTag(str, i, i2, runnable, null);
    }

    public static SpannableStringBuilder replaceSingleTag(String str, final int i, final int i2, final Runnable runnable, final Theme.ResourcesProvider resourcesProvider) {
        int i3;
        int i4;
        int indexOf = str.indexOf("**");
        int indexOf2 = str.indexOf("**", indexOf + 1);
        String replace = str.replace("**", "");
        if (indexOf < 0 || indexOf2 < 0 || (i4 = indexOf2 - indexOf) <= 2) {
            indexOf = -1;
            i3 = 0;
        } else {
            i3 = i4 - 2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replace);
        if (runnable != null && indexOf >= 0) {
            if (i2 == 3) {
                int i5 = indexOf + i3;
                spannableStringBuilder.replace(indexOf, i5, replaceMultipleCharSequence(" ", spannableStringBuilder.subSequence(indexOf, i5), " "));
            }
            spannableStringBuilder.setSpan((i2 == 0 || i2 == 3 || i2 == 2 || i2 == 4) ? new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(i2 == 4);
                    int i6 = i;
                    if (i6 >= 0) {
                        textPaint.setColor(Theme.getColor(i6, resourcesProvider));
                    }
                    if (i2 == 2) {
                        textPaint.setTypeface(AndroidUtilities.bold());
                    }
                }
            } : new CharacterStyle() {
                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setTypeface(AndroidUtilities.bold());
                    int alpha = textPaint.getAlpha();
                    textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, Theme.ResourcesProvider.this));
                    textPaint.setAlpha(alpha);
                }
            }, indexOf, i3 + indexOf, 0);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceSingleTag(String str, Runnable runnable) {
        return replaceSingleTag(str, -1, 0, runnable);
    }

    public static SpannableStringBuilder replaceTags(SpannableStringBuilder spannableStringBuilder) {
        try {
            ArrayList arrayList = new ArrayList();
            while (true) {
                int charSequenceIndexOf = charSequenceIndexOf(spannableStringBuilder, "**");
                if (charSequenceIndexOf == -1) {
                    break;
                }
                spannableStringBuilder.replace(charSequenceIndexOf, charSequenceIndexOf + 2, "");
                int charSequenceIndexOf2 = charSequenceIndexOf(spannableStringBuilder, "**");
                if (charSequenceIndexOf2 >= 0) {
                    spannableStringBuilder.replace(charSequenceIndexOf2, charSequenceIndexOf2 + 2, "");
                    arrayList.add(Integer.valueOf(charSequenceIndexOf));
                    arrayList.add(Integer.valueOf(charSequenceIndexOf2));
                }
            }
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(spannableStringBuilder);
            for (int i = 0; i < arrayList.size() / 2; i++) {
                int i2 = i * 2;
                spannableStringBuilder2.setSpan(new TypefaceSpan(bold()), ((Integer) arrayList.get(i2)).intValue(), ((Integer) arrayList.get(i2 + 1)).intValue(), 33);
            }
            return spannableStringBuilder2;
        } catch (Exception e) {
            FileLog.e(e);
            return spannableStringBuilder;
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int i, Object... objArr) {
        try {
            StringBuilder sb = new StringBuilder(str);
            if ((i & 1) != 0) {
                while (true) {
                    int indexOf = sb.indexOf("<br>");
                    if (indexOf == -1) {
                        break;
                    }
                    sb.replace(indexOf, indexOf + 4, "\n");
                }
                while (true) {
                    int indexOf2 = sb.indexOf("<br/>");
                    if (indexOf2 == -1) {
                        break;
                    }
                    sb.replace(indexOf2, indexOf2 + 5, "\n");
                }
            }
            ArrayList arrayList = new ArrayList();
            if ((i & 2) != 0) {
                while (true) {
                    int indexOf3 = sb.indexOf("<b>");
                    if (indexOf3 == -1) {
                        break;
                    }
                    sb.replace(indexOf3, indexOf3 + 3, "");
                    int indexOf4 = sb.indexOf("</b>");
                    if (indexOf4 == -1) {
                        indexOf4 = sb.indexOf("<b>");
                    }
                    sb.replace(indexOf4, indexOf4 + 4, "");
                    arrayList.add(Integer.valueOf(indexOf3));
                    arrayList.add(Integer.valueOf(indexOf4));
                }
                while (true) {
                    int indexOf5 = sb.indexOf("**");
                    if (indexOf5 == -1) {
                        break;
                    }
                    sb.replace(indexOf5, indexOf5 + 2, "");
                    int indexOf6 = sb.indexOf("**");
                    if (indexOf6 >= 0) {
                        sb.replace(indexOf6, indexOf6 + 2, "");
                        arrayList.add(Integer.valueOf(indexOf5));
                        arrayList.add(Integer.valueOf(indexOf6));
                    }
                }
            }
            if ((i & 8) != 0) {
                while (true) {
                    int indexOf7 = sb.indexOf("**");
                    if (indexOf7 == -1) {
                        break;
                    }
                    sb.replace(indexOf7, indexOf7 + 2, "");
                    int indexOf8 = sb.indexOf("**");
                    if (indexOf8 >= 0) {
                        sb.replace(indexOf8, indexOf8 + 2, "");
                        arrayList.add(Integer.valueOf(indexOf7));
                        arrayList.add(Integer.valueOf(indexOf8));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb);
            for (int i2 = 0; i2 < arrayList.size() / 2; i2++) {
                int i3 = i2 * 2;
                spannableStringBuilder.setSpan(new TypefaceSpan(bold()), ((Integer) arrayList.get(i3)).intValue(), ((Integer) arrayList.get(i3 + 1)).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e(e);
            return new SpannableStringBuilder(str);
        }
    }

    public static CharSequence replaceTags(CharSequence charSequence) {
        return charSequence instanceof SpannableStringBuilder ? replaceTags((SpannableStringBuilder) charSequence) : replaceTags(new SpannableStringBuilder(charSequence));
    }

    public static CharSequence replaceTwoNewLinesToOne(CharSequence charSequence) {
        char[] cArr = new char[2];
        if (charSequence instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) charSequence;
            int length = charSequence.length();
            int i = 0;
            while (i < length - 2) {
                int i2 = i + 2;
                sb.getChars(i, i2, cArr, 0);
                if (cArr[0] == '\n' && cArr[1] == '\n') {
                    sb = sb.replace(i, i2, "\n");
                    i--;
                    length--;
                }
                i++;
            }
            return charSequence;
        }
        if (!(charSequence instanceof SpannableStringBuilder)) {
            return charSequence.toString().replace("\n\n", "\n");
        }
        SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
        int length2 = charSequence.length();
        int i3 = 0;
        while (i3 < length2 - 2) {
            int i4 = i3 + 2;
            spannableStringBuilder.getChars(i3, i4, cArr, 0);
            if (cArr[0] == '\n' && cArr[1] == '\n') {
                spannableStringBuilder = spannableStringBuilder.replace(i3, i4, (CharSequence) "\n");
                i3--;
                length2--;
            }
            i3++;
        }
        return charSequence;
    }

    public static void requestAdjustNothing(Activity activity, int i) {
        if (activity == null || isTablet()) {
            return;
        }
        activity.getWindow().setSoftInputMode(48);
        adjustOwnerClassGuid = i;
    }

    public static void requestAdjustResize(Activity activity, int i) {
        if (activity == null) {
            return;
        }
        requestAdjustResize(activity.getWindow(), i);
    }

    public static void requestAdjustResize(Window window, int i) {
        if (window == null || isTablet()) {
            return;
        }
        window.setSoftInputMode(16);
        adjustOwnerClassGuid = i;
    }

    public static void requestAltFocusable(Activity activity, int i) {
        if (activity == null) {
            return;
        }
        activity.getWindow().setFlags(131072, 131072);
        altFocusableClassGuid = i;
    }

    public static void resetTabletFlag() {
        if (wasTablet == null) {
            wasTablet = Boolean.valueOf(isTabletInternal());
        }
        isTablet = null;
        SharedConfig.updateTabletConfig();
    }

    public static void resetWasTabletFlag() {
        wasTablet = null;
    }

    public static double[] rgbToHsv(int i) {
        return rgbToHsv(Color.red(i), Color.green(i), Color.blue(i));
    }

    public static double[] rgbToHsv(int i, int i2, int i3) {
        double d;
        double d2;
        double d3;
        double d4 = i;
        Double.isNaN(d4);
        double d5 = d4 / 255.0d;
        double d6 = i2;
        Double.isNaN(d6);
        double d7 = d6 / 255.0d;
        double d8 = i3;
        Double.isNaN(d8);
        double d9 = d8 / 255.0d;
        double max = (d5 <= d7 || d5 <= d9) ? Math.max(d7, d9) : d5;
        double min = (d5 >= d7 || d5 >= d9) ? Math.min(d7, d9) : d5;
        double d10 = max - min;
        double d11 = 0.0d;
        double d12 = max == 0.0d ? 0.0d : d10 / max;
        if (max != min) {
            if (d5 > d7 && d5 > d9) {
                d = (d7 - d9) / d10;
                d2 = d7 < d9 ? 6 : 0;
                Double.isNaN(d2);
            } else if (d7 > d9) {
                d3 = 2.0d + ((d9 - d5) / d10);
                d11 = d3 / 6.0d;
            } else {
                d = (d5 - d7) / d10;
                d2 = 4.0d;
            }
            d3 = d + d2;
            d11 = d3 / 6.0d;
        }
        return new double[]{d11, d12, max};
    }

    public static int[] roundPercents(float[] fArr, int[] iArr) {
        if (fArr == null) {
            throw new NullPointerException("percents or output is null");
        }
        if (iArr == null) {
            iArr = new int[fArr.length];
        }
        if (fArr.length != iArr.length) {
            throw new IndexOutOfBoundsException("percents.length != output.length");
        }
        float f = 0.0f;
        for (float f2 : fArr) {
            f += f2;
        }
        int i = 0;
        for (int i2 = 0; i2 < fArr.length; i2++) {
            int floor = (int) Math.floor((fArr[i2] / f) * 100.0f);
            iArr[i2] = floor;
            i += floor;
        }
        while (i < 100) {
            int i3 = -1;
            float f3 = 0.0f;
            for (int i4 = 0; i4 < fArr.length; i4++) {
                float f4 = fArr[i4];
                float f5 = (f4 / f) - (iArr[i4] / 100.0f);
                if (f4 > 0.0f && f5 >= f3) {
                    i3 = i4;
                    f3 = f5;
                }
            }
            if (i3 < 0) {
                break;
            }
            iArr[i3] = iArr[i3] + 1;
            i++;
        }
        return iArr;
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0L);
    }

    public static void runOnUIThread(Runnable runnable, long j) {
        if (ApplicationLoader.applicationHandler == null) {
            return;
        }
        if (j == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, j);
        }
    }

    public static void scaleRect(RectF rectF, float f) {
        scaleRect(rectF, f, rectF.centerX(), rectF.centerY());
    }

    public static void scaleRect(RectF rectF, float f, float f2, float f3) {
        float f4 = f2 - rectF.left;
        float f5 = rectF.right - f2;
        rectF.set(f2 - (f4 * f), f3 - ((f3 - rectF.top) * f), f2 + (f5 * f), f3 + ((rectF.bottom - f3) * f));
    }

    public static void scrollToFragmentRow(INavigationLayout iNavigationLayout, final String str) {
        if (iNavigationLayout == null || str == null) {
            return;
        }
        final BaseFragment baseFragment = (BaseFragment) iNavigationLayout.getFragmentStack().get(iNavigationLayout.getFragmentStack().size() - 1);
        try {
            Field declaredField = baseFragment.getClass().getDeclaredField("listView");
            declaredField.setAccessible(true);
            final RecyclerListView recyclerListView = (RecyclerListView) declaredField.get(baseFragment);
            recyclerListView.highlightRow(new RecyclerListView.IntReturnCallback() {
                @Override
                public final int run() {
                    int lambda$scrollToFragmentRow$21;
                    lambda$scrollToFragmentRow$21 = AndroidUtilities.lambda$scrollToFragmentRow$21(BaseFragment.this, str, recyclerListView);
                    return lambda$scrollToFragmentRow$21;
                }
            });
            declaredField.setAccessible(false);
        } catch (Throwable unused) {
        }
    }

    public static void setAdjustResizeToNothing(Activity activity, int i) {
        if (activity == null || isTablet()) {
            return;
        }
        int i2 = adjustOwnerClassGuid;
        if (i2 == 0 || i2 == i) {
            activity.getWindow().setSoftInputMode(48);
        }
    }

    public static void setEnabled(View view, boolean z) {
        if (view == null) {
            return;
        }
        view.setEnabled(z);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setEnabled(viewGroup.getChildAt(i), z);
            }
        }
    }

    public static void setLightNavigationBar(View view, boolean z) {
        if (view == null || Build.VERSION.SDK_INT < 26) {
            return;
        }
        int systemUiVisibility = view.getSystemUiVisibility();
        if (((systemUiVisibility & 16) > 0) != z) {
            view.setSystemUiVisibility(z ? systemUiVisibility | 16 : systemUiVisibility & (-17));
        }
    }

    public static void setLightNavigationBar(Window window, boolean z) {
        if (window != null) {
            setLightNavigationBar(window.getDecorView(), z);
        }
    }

    public static void setLightStatusBar(View view, boolean z) {
        if (view == null || Build.VERSION.SDK_INT < 26) {
            return;
        }
        int systemUiVisibility = view.getSystemUiVisibility();
        if (((systemUiVisibility & 8192) > 0) != z) {
            view.setSystemUiVisibility(z ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
        }
    }

    public static void setLightStatusBar(Window window, boolean z) {
        setLightStatusBar(window, z, false);
    }

    public static void setLightStatusBar(Window window, boolean z, boolean z2) {
        int statusBarColor;
        int statusBarColor2;
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (z) {
                if ((systemUiVisibility & 8192) == 0) {
                    decorView.setSystemUiVisibility(systemUiVisibility | 8192);
                }
                statusBarColor2 = window.getStatusBarColor();
                if (statusBarColor2 == 0) {
                    return;
                }
            } else {
                if ((systemUiVisibility & 8192) != 0) {
                    decorView.setSystemUiVisibility(systemUiVisibility & (-8193));
                }
                statusBarColor = window.getStatusBarColor();
                if (statusBarColor == 0) {
                    return;
                }
            }
            window.setStatusBarColor(0);
        }
    }

    public static int setMyLayerVersion(int i, int i2) {
        return (i & (-65536)) | i2;
    }

    public static void setNavigationBarColor(Window window, int i) {
        setNavigationBarColor(window, i, true);
    }

    public static void setNavigationBarColor(Window window, int i, boolean z) {
        setNavigationBarColor(window, i, z, null);
    }

    public static void setNavigationBarColor(final Window window, int i, boolean z, final IntColorCallback intColorCallback) {
        int navigationBarColor;
        ValueAnimator ofArgb;
        ValueAnimator valueAnimator;
        if (window == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        HashMap<Window, ValueAnimator> hashMap = navigationBarColorAnimators;
        if (hashMap != null && (valueAnimator = hashMap.get(window)) != null) {
            valueAnimator.cancel();
            navigationBarColorAnimators.remove(window);
        }
        if (z) {
            navigationBarColor = window.getNavigationBarColor();
            ofArgb = ValueAnimator.ofArgb(navigationBarColor, i);
            ofArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    AndroidUtilities.lambda$setNavigationBarColor$20(AndroidUtilities.IntColorCallback.this, window, valueAnimator2);
                }
            });
            ofArgb.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (AndroidUtilities.navigationBarColorAnimators != null) {
                        AndroidUtilities.navigationBarColorAnimators.remove(window);
                    }
                }
            });
            ofArgb.setDuration(200L);
            ofArgb.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofArgb.start();
            if (navigationBarColorAnimators == null) {
                navigationBarColorAnimators = new HashMap<>();
            }
            navigationBarColorAnimators.put(window, ofArgb);
            return;
        }
        if (intColorCallback != null) {
            intColorCallback.run(i);
        }
        try {
            window.setNavigationBarColor(i);
        } catch (Exception unused) {
        }
    }

    public static int setPeerLayerVersion(int i, int i2) {
        return (i & 65535) | (i2 << 16);
    }

    public static void setPreferredMaxRefreshRate(Window window) {
        WindowManager windowManager;
        if (Build.VERSION.SDK_INT < 21 || window == null || (windowManager = window.getWindowManager()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.preferredRefreshRate = screenMaxRefreshRate;
        try {
            windowManager.updateViewLayout(window.getDecorView(), attributes);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void setPreferredMaxRefreshRate(WindowManager windowManager, View view, WindowManager.LayoutParams layoutParams) {
        float f;
        if (Build.VERSION.SDK_INT >= 21 && windowManager != null) {
            f = layoutParams.preferredRefreshRate;
            if (Math.abs(f - screenMaxRefreshRate) > 0.2d) {
                layoutParams.preferredRefreshRate = screenMaxRefreshRate;
                if (view.isAttachedToWindow()) {
                    try {
                        windowManager.updateViewLayout(view, layoutParams);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        }
    }

    public static void setRectToRect(Matrix matrix, RectF rectF, RectF rectF2, int i, int i2, boolean z) {
        float height;
        float width;
        boolean z2;
        float f;
        float f2;
        float height2;
        float height3;
        if (i == 90 || i == 270) {
            height = rectF2.height() / rectF.width();
            width = rectF2.width();
        } else {
            height = rectF2.width() / rectF.width();
            width = rectF2.height();
        }
        float height4 = width / rectF.height();
        if (height < height4) {
            height = height4;
            z2 = true;
        } else {
            z2 = false;
        }
        if (z) {
            matrix.setTranslate(rectF2.left, rectF2.top);
        }
        if (i == 90) {
            matrix.preRotate(90.0f);
            if (i2 == 1) {
                matrix.preScale(-1.0f, 1.0f);
            } else if (i2 == 2) {
                matrix.preScale(1.0f, -1.0f);
            }
            matrix.preTranslate(0.0f, -rectF2.width());
        } else if (i == 180) {
            matrix.preRotate(180.0f);
            if (i2 == 1) {
                matrix.preScale(-1.0f, 1.0f);
            } else if (i2 == 2) {
                matrix.preScale(1.0f, -1.0f);
            }
            matrix.preTranslate(-rectF2.width(), -rectF2.height());
        } else if (i == 270) {
            matrix.preRotate(270.0f);
            if (i2 == 1) {
                matrix.preScale(-1.0f, 1.0f);
            } else if (i2 == 2) {
                matrix.preScale(1.0f, -1.0f);
            }
            matrix.preTranslate(-rectF2.height(), 0.0f);
        }
        if (z) {
            f = (-rectF.left) * height;
            f2 = (-rectF.top) * height;
        } else {
            f = rectF2.left - (rectF.left * height);
            f2 = rectF2.top - (rectF.top * height);
        }
        if (z2) {
            height2 = rectF2.width();
            height3 = rectF.width();
        } else {
            height2 = rectF2.height();
            height3 = rectF.height();
        }
        float f3 = (height2 - (height3 * height)) / 2.0f;
        if (z2) {
            f += f3;
        } else {
            f2 += f3;
        }
        matrix.preScale(height, height);
        if (z) {
            matrix.preTranslate(f, f2);
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView horizontalScrollView, int i) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 29) {
            horizontalScrollView.setEdgeEffectColor(i);
            return;
        }
        if (i2 >= 21) {
            try {
                Field declaredField = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(horizontalScrollView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                Field declaredField2 = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                declaredField2.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField2.get(horizontalScrollView);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int i) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 29) {
            scrollView.setTopEdgeEffectColor(i);
            scrollView.setBottomEdgeEffectColor(i);
            return;
        }
        if (i2 >= 21) {
            try {
                Field declaredField = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(scrollView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                Field declaredField2 = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                declaredField2.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField2.get(scrollView);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = ViewPager.class.getDeclaredField("mLeftEdge");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(viewPager);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                Field declaredField2 = ViewPager.class.getDeclaredField("mRightEdge");
                declaredField2.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField2.get(viewPager);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void setWaitingForCall(boolean z) {
        synchronized (callLock) {
            try {
                try {
                    if (z) {
                        if (callReceiver == null) {
                            IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
                            if (Build.VERSION.SDK_INT >= 33) {
                                Context context = ApplicationLoader.applicationContext;
                                CallReceiver callReceiver2 = new CallReceiver();
                                callReceiver = callReceiver2;
                                context.registerReceiver(callReceiver2, intentFilter, 4);
                            } else {
                                Context context2 = ApplicationLoader.applicationContext;
                                CallReceiver callReceiver3 = new CallReceiver();
                                callReceiver = callReceiver3;
                                context2.registerReceiver(callReceiver3, intentFilter);
                            }
                        }
                    } else if (callReceiver != null) {
                        ApplicationLoader.applicationContext.unregisterReceiver(callReceiver);
                        callReceiver = null;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } catch (Exception unused) {
            }
            waitingForCall = z;
        }
    }

    public static void setWaitingForSms(boolean z) {
        synchronized (smsLock) {
            try {
                waitingForSms = z;
                if (z) {
                    try {
                        SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public final void onSuccess(Object obj) {
                                AndroidUtilities.lambda$setWaitingForSms$11((Void) obj);
                            }
                        });
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public static void shakeView(final View view) {
        if (view == null) {
            return;
        }
        int i = R.id.shake_animation;
        Object tag = view.getTag(i);
        if (tag instanceof ValueAnimator) {
            ((ValueAnimator) tag).cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AndroidUtilities.lambda$shakeView$12(view, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                view.setTranslationX(0.0f);
            }
        });
        ofFloat.setDuration(300L);
        ofFloat.start();
        view.setTag(i, ofFloat);
    }

    public static void shakeViewSpring(View view) {
        shakeViewSpring(view, 10.0f, null);
    }

    public static void shakeViewSpring(View view, float f) {
        shakeViewSpring(view, f, null);
    }

    public static void shakeViewSpring(final View view, float f, final Runnable runnable) {
        if (view == null) {
            return;
        }
        int dp = dp(f);
        int i = R.id.spring_tag;
        if (view.getTag(i) != null) {
            ((SpringAnimation) view.getTag(i)).cancel();
        }
        int i2 = R.id.spring_was_translation_x_tag;
        Float f2 = (Float) view.getTag(i2);
        if (f2 != null) {
            view.setTranslationX(f2.floatValue());
        }
        view.setTag(i2, Float.valueOf(view.getTranslationX()));
        final float translationX = view.getTranslationX();
        SpringAnimation springAnimation = (SpringAnimation) ((SpringAnimation) new SpringAnimation(view, DynamicAnimation.TRANSLATION_X, translationX).setSpring(new SpringForce(translationX).setStiffness(600.0f)).setStartVelocity((-dp) * 100)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
                AndroidUtilities.lambda$shakeViewSpring$13(runnable, view, translationX, dynamicAnimation, z, f3, f4);
            }
        });
        view.setTag(i, springAnimation);
        springAnimation.start();
    }

    public static void shakeViewSpring(View view, Runnable runnable) {
        shakeViewSpring(view, 10.0f, runnable);
    }

    public static boolean shouldEnableAnimation() {
        boolean isPowerSaveMode;
        int i = Build.VERSION.SDK_INT;
        if (i >= 26 && i < 28) {
            isPowerSaveMode = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).isPowerSaveMode();
            if (isPowerSaveMode || Settings.Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f) <= 0.0f) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldShowClipboardToast() {
        int i = Build.VERSION.SDK_INT;
        return (i < 31 || !OneUIUtilities.hasBuiltInClipboardToasts()) && i < 32;
    }

    public static boolean shouldShowUrlInAlert(String str) {
        try {
            return checkHostForPunycode(Uri.parse(str).getHost());
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static boolean showKeyboard(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static void showProxyAlert(final Activity activity, final String str, final String str2, final String str3, final String str4, final String str5) {
        String str6;
        String str7;
        BottomSheet.Builder builder = new BottomSheet.Builder(activity);
        final Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(activity);
        builder.setCustomView(linearLayout);
        linearLayout.setOrientation(1);
        int i = 3;
        int i2 = 5;
        if (!TextUtils.isEmpty(str5)) {
            TextView textView = new TextView(activity);
            textView.setText(LocaleController.getString(R.string.UseProxyTelegramInfo2));
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            textView.setTextSize(1, 14.0f);
            textView.setGravity(49);
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View view = new View(activity);
            view.setBackgroundColor(Theme.getColor(Theme.key_divider));
            linearLayout.addView(view, new LinearLayout.LayoutParams(-1, 1));
        }
        int i3 = 0;
        while (i3 < 6) {
            if (i3 == 0) {
                str7 = LocaleController.getString("UseProxyAddress", R.string.UseProxyAddress);
                str6 = str;
            } else if (i3 == 1) {
                str7 = LocaleController.getString("UseProxyPort", R.string.UseProxyPort);
                str6 = "" + str2;
            } else if (i3 == 2) {
                str7 = LocaleController.getString("UseProxySecret", R.string.UseProxySecret);
                str6 = str5;
            } else if (i3 == i) {
                str7 = LocaleController.getString("UseProxyUsername", R.string.UseProxyUsername);
                str6 = str3;
            } else if (i3 == 4) {
                str7 = LocaleController.getString("UseProxyPassword", R.string.UseProxyPassword);
                str6 = str4;
            } else if (i3 == i2) {
                String string = LocaleController.getString(R.string.ProxyBottomSheetChecking);
                str7 = LocaleController.getString(R.string.ProxyStatus);
                str6 = string;
            } else {
                str6 = null;
                str7 = null;
            }
            if (!TextUtils.isEmpty(str6)) {
                final AtomicReference atomicReference = new AtomicReference();
                final TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(activity) {
                    @Override
                    protected void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        if (atomicReference.get() != null) {
                            ((EllipsizeSpanAnimator) atomicReference.get()).onAttachedToWindow();
                        }
                    }

                    @Override
                    protected void onDetachedFromWindow() {
                        super.onDetachedFromWindow();
                        if (atomicReference.get() != null) {
                            ((EllipsizeSpanAnimator) atomicReference.get()).onDetachedFromWindow();
                        }
                    }
                };
                String str8 = str6;
                if (i3 == i2) {
                    SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(str6);
                    EllipsizeSpanAnimator ellipsizeSpanAnimator = new EllipsizeSpanAnimator(textDetailSettingsCell);
                    ellipsizeSpanAnimator.addView(textDetailSettingsCell);
                    SpannableString spannableString = new SpannableString("...");
                    ellipsizeSpanAnimator.wrap(spannableString, 0);
                    valueOf.append((CharSequence) spannableString);
                    atomicReference.set(ellipsizeSpanAnimator);
                    str8 = valueOf;
                }
                textDetailSettingsCell.setTextAndValue(str8, str7, true);
                textDetailSettingsCell.getTextView().setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                textDetailSettingsCell.getValueTextView().setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
                linearLayout.addView(textDetailSettingsCell, LayoutHelper.createLinear(-1, -2));
                if (i3 == 5) {
                    try {
                        ConnectionsManager.getInstance(UserConfig.selectedAccount).checkProxy(str, Integer.parseInt(str2), str3, str4, str5, new RequestTimeDelegate() {
                            @Override
                            public final void run(long j) {
                                AndroidUtilities.lambda$showProxyAlert$17(TextDetailSettingsCell.this, j);
                            }
                        });
                    } catch (NumberFormatException unused) {
                        textDetailSettingsCell.getTextView().setText(LocaleController.getString(R.string.Unavailable));
                        textDetailSettingsCell.getTextView().setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                    }
                }
            }
            i3++;
            i = 3;
            i2 = 5;
        }
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(activity, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        TextView textView2 = pickerBottomLayout.cancelButton;
        int i4 = Theme.key_dialogTextBlue2;
        textView2.setTextColor(Theme.getColor(i4));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString(R.string.Cancel).toUpperCase());
        pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                dismissRunnable.run();
            }
        });
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor(i4));
        pickerBottomLayout.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString(R.string.ConnectingConnectProxy).toUpperCase());
        pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                AndroidUtilities.lambda$showProxyAlert$19(str, str2, str5, str4, str3, activity, dismissRunnable, view2);
            }
        });
        builder.show();
    }

    private static void snapshotTextureViews(int i, int i2, int[] iArr, Canvas canvas, View view) {
        if (view instanceof TextureView) {
            TextureView textureView = (TextureView) view;
            textureView.getLocationInWindow(iArr);
            Bitmap bitmap = textureView.getBitmap();
            if (bitmap != null) {
                canvas.save();
                canvas.drawBitmap(bitmap, iArr[0] - i, iArr[1] - i2, (Paint) null);
                canvas.restore();
                bitmap.recycle();
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                snapshotTextureViews(i, i2, iArr, canvas, viewGroup.getChildAt(i3));
            }
        }
    }

    public static Bitmap snapshotView(View view) {
        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        view.draw(canvas);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        snapshotTextureViews(iArr[0], iArr[1], iArr, canvas, view);
        return createBitmap;
    }

    public static int[] toIntArray(List<Integer> list) {
        int size = list.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = list.get(i).intValue();
        }
        return iArr;
    }

    public static String translitSafe(String str) {
        if (str != null) {
            try {
                str = str.toLowerCase();
            } catch (Exception unused) {
                return "";
            }
        }
        String translitString = LocaleController.getInstance().getTranslitString(str, false);
        return translitString == null ? "" : translitString;
    }

    public static CharSequence trim(CharSequence charSequence, int[] iArr) {
        if (charSequence == null) {
            return null;
        }
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) <= ' ') {
            i++;
        }
        while (i < length && charSequence.charAt(length - 1) <= ' ') {
            length--;
        }
        if (iArr != null) {
            iArr[0] = i;
        }
        return (i > 0 || length < charSequence.length()) ? charSequence.subSequence(i, length) : charSequence;
    }

    public static void unlockOrientation(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            int i = prevOrientation;
            if (i != -10) {
                activity.setRequestedOrientation(i);
                prevOrientation = -10;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void updateImageViewImageAnimated(ImageView imageView, int i) {
        updateImageViewImageAnimated(imageView, ContextCompat.getDrawable(imageView.getContext(), i));
    }

    public static void updateImageViewImageAnimated(final ImageView imageView, final Drawable drawable) {
        if (imageView.getDrawable() == drawable) {
            return;
        }
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
        final AtomicBoolean atomicBoolean = new AtomicBoolean();
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AndroidUtilities.lambda$updateImageViewImageAnimated$22(imageView, atomicBoolean, drawable, valueAnimator);
            }
        });
        duration.start();
    }

    public static void updateViewShow(View view, boolean z) {
        updateViewShow(view, z, true, true);
    }

    public static void updateViewShow(View view, boolean z, boolean z2, float f, boolean z3, Runnable runnable) {
        ViewPropertyAnimator withEndAction;
        if (view == null) {
            return;
        }
        if (view.getParent() == null) {
            z3 = false;
        }
        view.animate().setListener(null).cancel();
        if (!z3) {
            view.setVisibility(z ? 0 : 8);
            view.setTag(z ? 1 : null);
            view.setAlpha(1.0f);
            view.setScaleX((!z2 || z) ? 1.0f : 0.0f);
            if (z2 && !z) {
                r3 = 0.0f;
            }
            view.setScaleY(r3);
            if (f != 0.0f) {
                view.setTranslationY(z ? 0.0f : dp(-16.0f) * f);
            }
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (z) {
            if (view.getVisibility() != 0) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(z2 ? 0.0f : 1.0f);
                view.setScaleY(z2 ? 0.0f : 1.0f);
                if (f != 0.0f) {
                    view.setTranslationY(dp(-16.0f) * f);
                }
            }
            withEndAction = view.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(340L).withEndAction(runnable);
            if (f != 0.0f) {
                withEndAction.translationY(0.0f);
            }
        } else {
            withEndAction = view.animate().alpha(0.0f).scaleY(z2 ? 0.0f : 1.0f).scaleX(z2 ? 0.0f : 1.0f).setListener(new HideViewAfterAnimation(view)).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(340L).withEndAction(runnable);
            if (f != 0.0f) {
                withEndAction.translationY(dp(-16.0f) * f);
            }
        }
        withEndAction.start();
    }

    public static void updateViewShow(View view, boolean z, boolean z2, boolean z3) {
        updateViewShow(view, z, z2, 0.0f, z3, null);
    }

    public static void updateViewShow(View view, boolean z, boolean z2, boolean z3, Runnable runnable) {
        updateViewShow(view, z, z2, 0.0f, z3, runnable);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z) {
        updateViewVisibilityAnimated(view, z, 1.0f, true, true);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2) {
        updateViewVisibilityAnimated(view, z, f, true, z2);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2, float f2, boolean z3) {
        if (view == null) {
            return;
        }
        if (view.getParent() == null) {
            z3 = false;
        }
        if (!z3) {
            view.animate().setListener(null).cancel();
            view.setVisibility(z ? 0 : z2 ? 8 : 4);
            view.setTag(z ? 1 : null);
            view.setAlpha(f2);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            return;
        }
        if (!z || view.getTag() != null) {
            if (z || view.getTag() == null) {
                return;
            }
            view.animate().setListener(null).cancel();
            view.animate().alpha(0.0f).scaleY(f).scaleX(f).setListener(new HideViewAfterAnimation(view, z2)).setDuration(150L).start();
            view.setTag(null);
            return;
        }
        view.animate().setListener(null).cancel();
        if (view.getVisibility() != 0) {
            view.setVisibility(0);
            view.setAlpha(0.0f);
            view.setScaleX(f);
            view.setScaleY(f);
        }
        view.animate().alpha(f2).scaleY(1.0f).scaleX(1.0f).setDuration(150L).start();
        view.setTag(1);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2, boolean z3) {
        updateViewVisibilityAnimated(view, z, f, z2, 1.0f, z3);
    }

    public static void updateVisibleRows(RecyclerListView recyclerListView) {
        RecyclerView.Adapter adapter;
        RecyclerView.ViewHolder childViewHolder;
        if (recyclerListView == null || (adapter = recyclerListView.getAdapter()) == null) {
            return;
        }
        for (int i = 0; i < recyclerListView.getChildCount(); i++) {
            View childAt = recyclerListView.getChildAt(i);
            int childAdapterPosition = recyclerListView.getChildAdapterPosition(childAt);
            if (childAdapterPosition >= 0 && (childViewHolder = recyclerListView.getChildViewHolder(childAt)) != null && !childViewHolder.shouldIgnore()) {
                adapter.onBindViewHolder(childViewHolder, childAdapterPosition);
            }
        }
    }

    public static void vibrate(View view) {
        boolean hasAmplitudeControl;
        if (view != null) {
            try {
                if (view.getContext() != null && Build.VERSION.SDK_INT >= 26) {
                    hasAmplitudeControl = ((Vibrator) view.getContext().getSystemService("vibrator")).hasAmplitudeControl();
                    if (hasAmplitudeControl) {
                        view.performHapticFeedback(3, 1);
                    }
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void vibrateCursor(View view) {
        boolean hasAmplitudeControl;
        if (view != null) {
            try {
                if (view.getContext() != null && Build.VERSION.SDK_INT >= 26) {
                    hasAmplitudeControl = ((Vibrator) view.getContext().getSystemService("vibrator")).hasAmplitudeControl();
                    if (hasAmplitudeControl) {
                        view.performHapticFeedback(9, 1);
                    }
                }
            } catch (Exception unused) {
            }
        }
    }

    public static CharSequence withLearnMore(CharSequence charSequence, final Runnable runnable) {
        SpannableString spannableString = new SpannableString(LocaleController.getString(R.string.LearnMoreArrow));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
                textPaint.setColor(textPaint.linkColor);
            }
        }, 0, spannableString.length(), 33);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.append((CharSequence) " ");
        spannableStringBuilder.append((CharSequence) spannableString);
        return replaceArrows(spannableStringBuilder, true);
    }
}
