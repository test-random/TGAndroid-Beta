package kotlin.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntProgression;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;

public abstract class StringsKt__StringsKt extends StringsKt__StringsJVMKt {
    public static final int getLastIndex(CharSequence charSequence) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        return charSequence.length() - 1;
    }

    public static final int indexOf(CharSequence charSequence, char c, int i, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        return (z || !(charSequence instanceof String)) ? indexOfAny(charSequence, new char[]{c}, i, z) : ((String) charSequence).indexOf(c, i);
    }

    public static final int indexOf(CharSequence charSequence, String string, int i, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Intrinsics.checkNotNullParameter(string, "string");
        return (z || !(charSequence instanceof String)) ? indexOf$StringsKt__StringsKt$default(charSequence, string, i, charSequence.length(), z, false, 16, null) : ((String) charSequence).indexOf(string, i);
    }

    private static final int indexOf$StringsKt__StringsKt(CharSequence charSequence, CharSequence charSequence2, int i, int i2, boolean z, boolean z2) {
        IntProgression intRange = !z2 ? new IntRange(RangesKt.coerceAtLeast(i, 0), RangesKt.coerceAtMost(i2, charSequence.length())) : RangesKt.downTo(RangesKt.coerceAtMost(i, getLastIndex(charSequence)), RangesKt.coerceAtLeast(i2, 0));
        if ((charSequence instanceof String) && (charSequence2 instanceof String)) {
            int first = intRange.getFirst();
            int last = intRange.getLast();
            int step = intRange.getStep();
            if ((step <= 0 || first > last) && (step >= 0 || last > first)) {
                return -1;
            }
            while (!StringsKt__StringsJVMKt.regionMatches((String) charSequence2, 0, (String) charSequence, first, charSequence2.length(), z)) {
                if (first == last) {
                    return -1;
                }
                first += step;
            }
            return first;
        }
        int first2 = intRange.getFirst();
        int last2 = intRange.getLast();
        int step2 = intRange.getStep();
        if ((step2 <= 0 || first2 > last2) && (step2 >= 0 || last2 > first2)) {
            return -1;
        }
        while (!regionMatchesImpl(charSequence2, 0, charSequence, first2, charSequence2.length(), z)) {
            if (first2 == last2) {
                return -1;
            }
            first2 += step2;
        }
        return first2;
    }

    static int indexOf$StringsKt__StringsKt$default(CharSequence charSequence, CharSequence charSequence2, int i, int i2, boolean z, boolean z2, int i3, Object obj) {
        return indexOf$StringsKt__StringsKt(charSequence, charSequence2, i, i2, z, (i3 & 16) != 0 ? false : z2);
    }

    public static int indexOf$default(CharSequence charSequence, char c, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return indexOf(charSequence, c, i, z);
    }

    public static int indexOf$default(CharSequence charSequence, String str, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return indexOf(charSequence, str, i, z);
    }

    public static final int indexOfAny(CharSequence charSequence, char[] chars, int i, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Intrinsics.checkNotNullParameter(chars, "chars");
        if (!z && chars.length == 1 && (charSequence instanceof String)) {
            return ((String) charSequence).indexOf(ArraysKt.single(chars), i);
        }
        IntIterator it = new IntRange(RangesKt.coerceAtLeast(i, 0), getLastIndex(charSequence)).iterator();
        while (it.hasNext()) {
            int nextInt = it.nextInt();
            char charAt = charSequence.charAt(nextInt);
            for (char c : chars) {
                if (CharsKt__CharKt.equals(c, charAt, z)) {
                    return nextInt;
                }
            }
        }
        return -1;
    }

    public static final int lastIndexOf(CharSequence charSequence, char c, int i, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        return (z || !(charSequence instanceof String)) ? lastIndexOfAny(charSequence, new char[]{c}, i, z) : ((String) charSequence).lastIndexOf(c, i);
    }

    public static int lastIndexOf$default(CharSequence charSequence, char c, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return lastIndexOf(charSequence, c, i, z);
    }

    public static final int lastIndexOfAny(CharSequence charSequence, char[] chars, int i, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Intrinsics.checkNotNullParameter(chars, "chars");
        if (!z && chars.length == 1 && (charSequence instanceof String)) {
            return ((String) charSequence).lastIndexOf(ArraysKt.single(chars), i);
        }
        for (int coerceAtMost = RangesKt.coerceAtMost(i, getLastIndex(charSequence)); -1 < coerceAtMost; coerceAtMost--) {
            char charAt = charSequence.charAt(coerceAtMost);
            for (char c : chars) {
                if (CharsKt__CharKt.equals(c, charAt, z)) {
                    return coerceAtMost;
                }
            }
        }
        return -1;
    }

    public static final CharSequence padEnd(CharSequence charSequence, int i, char c) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        if (i < 0) {
            throw new IllegalArgumentException("Desired length " + i + " is less than zero.");
        }
        if (i <= charSequence.length()) {
            return charSequence.subSequence(0, charSequence.length());
        }
        StringBuilder sb = new StringBuilder(i);
        sb.append(charSequence);
        IntIterator it = new IntRange(1, i - charSequence.length()).iterator();
        while (it.hasNext()) {
            it.nextInt();
            sb.append(c);
        }
        return sb;
    }

    public static final String padEnd(String str, int i, char c) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return padEnd((CharSequence) str, i, c).toString();
    }

    public static String padEnd$default(String str, int i, char c, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            c = ' ';
        }
        return padEnd(str, i, c);
    }

    private static final Sequence rangesDelimitedBy$StringsKt__StringsKt(CharSequence charSequence, final char[] cArr, int i, final boolean z, int i2) {
        requireNonNegativeLimit(i2);
        return new DelimitedRangesSequence(charSequence, i, i2, new Function2() {
            {
                super(2);
            }

            @Override
            public Object invoke(Object obj, Object obj2) {
                return invoke((CharSequence) obj, ((Number) obj2).intValue());
            }

            public final Pair invoke(CharSequence $receiver, int i3) {
                Intrinsics.checkNotNullParameter($receiver, "$this$$receiver");
                int indexOfAny = StringsKt__StringsKt.indexOfAny($receiver, cArr, i3, z);
                if (indexOfAny < 0) {
                    return null;
                }
                return TuplesKt.to(Integer.valueOf(indexOfAny), 1);
            }
        });
    }

    static Sequence rangesDelimitedBy$StringsKt__StringsKt$default(CharSequence charSequence, char[] cArr, int i, boolean z, int i2, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            z = false;
        }
        if ((i3 & 8) != 0) {
            i2 = 0;
        }
        return rangesDelimitedBy$StringsKt__StringsKt(charSequence, cArr, i, z, i2);
    }

    public static final boolean regionMatchesImpl(CharSequence charSequence, int i, CharSequence other, int i2, int i3, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        if (i2 < 0 || i < 0 || i > charSequence.length() - i3 || i2 > other.length() - i3) {
            return false;
        }
        for (int i4 = 0; i4 < i3; i4++) {
            if (!CharsKt__CharKt.equals(charSequence.charAt(i + i4), other.charAt(i2 + i4), z)) {
                return false;
            }
        }
        return true;
    }

    public static final void requireNonNegativeLimit(int i) {
        if (i >= 0) {
            return;
        }
        throw new IllegalArgumentException(("Limit must be non-negative, but was " + i).toString());
    }

    public static final List split(CharSequence charSequence, char[] delimiters, boolean z, int i) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Intrinsics.checkNotNullParameter(delimiters, "delimiters");
        if (delimiters.length == 1) {
            return split$StringsKt__StringsKt(charSequence, String.valueOf(delimiters[0]), z, i);
        }
        Iterable asIterable = SequencesKt.asIterable(rangesDelimitedBy$StringsKt__StringsKt$default(charSequence, delimiters, 0, z, i, 2, null));
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(asIterable, 10));
        Iterator it = asIterable.iterator();
        while (it.hasNext()) {
            arrayList.add(substring(charSequence, (IntRange) it.next()));
        }
        return arrayList;
    }

    private static final List split$StringsKt__StringsKt(CharSequence charSequence, String str, boolean z, int i) {
        requireNonNegativeLimit(i);
        int i2 = 0;
        int indexOf = indexOf(charSequence, str, 0, z);
        if (indexOf == -1 || i == 1) {
            return CollectionsKt.listOf(charSequence.toString());
        }
        boolean z2 = i > 0;
        ArrayList arrayList = new ArrayList(z2 ? RangesKt.coerceAtMost(i, 10) : 10);
        do {
            arrayList.add(charSequence.subSequence(i2, indexOf).toString());
            i2 = str.length() + indexOf;
            if (z2 && arrayList.size() == i - 1) {
                break;
            }
            indexOf = indexOf(charSequence, str, i2, z);
        } while (indexOf != -1);
        arrayList.add(charSequence.subSequence(i2, charSequence.length()).toString());
        return arrayList;
    }

    public static List split$default(CharSequence charSequence, char[] cArr, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return split(charSequence, cArr, z, i);
    }

    public static final String substring(CharSequence charSequence, IntRange range) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Intrinsics.checkNotNullParameter(range, "range");
        return charSequence.subSequence(range.getStart().intValue(), range.getEndInclusive().intValue() + 1).toString();
    }

    public static final String substringAfter(String str, char c, String missingDelimiterValue) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        Intrinsics.checkNotNullParameter(missingDelimiterValue, "missingDelimiterValue");
        int indexOf$default = indexOf$default((CharSequence) str, c, 0, false, 6, (Object) null);
        if (indexOf$default == -1) {
            return missingDelimiterValue;
        }
        String substring = str.substring(indexOf$default + 1, str.length());
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public static final String substringAfter(String str, String delimiter, String missingDelimiterValue) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        Intrinsics.checkNotNullParameter(delimiter, "delimiter");
        Intrinsics.checkNotNullParameter(missingDelimiterValue, "missingDelimiterValue");
        int indexOf$default = indexOf$default((CharSequence) str, delimiter, 0, false, 6, (Object) null);
        if (indexOf$default == -1) {
            return missingDelimiterValue;
        }
        String substring = str.substring(indexOf$default + delimiter.length(), str.length());
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public static String substringAfter$default(String str, char c, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = str;
        }
        return substringAfter(str, c, str2);
    }

    public static String substringAfter$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 2) != 0) {
            str3 = str;
        }
        return substringAfter(str, str2, str3);
    }

    public static final String substringAfterLast(String str, char c, String missingDelimiterValue) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        Intrinsics.checkNotNullParameter(missingDelimiterValue, "missingDelimiterValue");
        int lastIndexOf$default = lastIndexOf$default(str, c, 0, false, 6, null);
        if (lastIndexOf$default == -1) {
            return missingDelimiterValue;
        }
        String substring = str.substring(lastIndexOf$default + 1, str.length());
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        return substring;
    }

    public static String substringAfterLast$default(String str, char c, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = str;
        }
        return substringAfterLast(str, c, str2);
    }
}
