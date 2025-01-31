package kotlin.text;

import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt___RangesKt;

public abstract class StringsKt__StringsJVMKt extends StringsKt__StringNumberConversionsKt {
    public static final boolean regionMatches(String str, int i, String other, int i2, int i3, boolean z) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return !z ? str.regionMatches(i, other, i2, i3) : str.regionMatches(z, i, other, i2, i3);
    }

    public static final String replace(String str, String oldValue, String newValue, boolean z) {
        int coerceAtLeast;
        Intrinsics.checkNotNullParameter(str, "<this>");
        Intrinsics.checkNotNullParameter(oldValue, "oldValue");
        Intrinsics.checkNotNullParameter(newValue, "newValue");
        int i = 0;
        int indexOf = StringsKt__StringsKt.indexOf(str, oldValue, 0, z);
        if (indexOf < 0) {
            return str;
        }
        int length = oldValue.length();
        coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(length, 1);
        int length2 = (str.length() - length) + newValue.length();
        if (length2 < 0) {
            throw new OutOfMemoryError();
        }
        StringBuilder sb = new StringBuilder(length2);
        do {
            sb.append((CharSequence) str, i, indexOf);
            sb.append(newValue);
            i = indexOf + length;
            if (indexOf >= str.length()) {
                break;
            }
            indexOf = StringsKt__StringsKt.indexOf(str, oldValue, indexOf + coerceAtLeast, z);
        } while (indexOf > 0);
        sb.append((CharSequence) str, i, str.length());
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "stringBuilder.append(this, i, length).toString()");
        return sb2;
    }

    public static String replace$default(String str, String str2, String str3, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        return replace(str, str2, str3, z);
    }

    public static final boolean startsWith(String str, String prefix, boolean z) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return !z ? str.startsWith(prefix) : regionMatches(str, 0, prefix, 0, prefix.length(), z);
    }

    public static boolean startsWith$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return startsWith(str, str2, z);
    }
}
