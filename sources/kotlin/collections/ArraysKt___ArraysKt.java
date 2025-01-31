package kotlin.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__AppendableKt;

public abstract class ArraysKt___ArraysKt extends ArraysKt___ArraysJvmKt {
    public static final boolean contains(Object[] objArr, Object obj) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        return indexOf(objArr, obj) >= 0;
    }

    public static List filterNotNull(Object[] objArr) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        return (List) filterNotNullTo(objArr, new ArrayList());
    }

    public static final Collection filterNotNullTo(Object[] objArr, Collection destination) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        Intrinsics.checkNotNullParameter(destination, "destination");
        for (Object obj : objArr) {
            if (obj != null) {
                destination.add(obj);
            }
        }
        return destination;
    }

    public static final int getLastIndex(Object[] objArr) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        return objArr.length - 1;
    }

    public static Object getOrNull(Object[] objArr, int i) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        if (i < 0 || i > getLastIndex(objArr)) {
            return null;
        }
        return objArr[i];
    }

    public static final int indexOf(Object[] objArr, Object obj) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        int i = 0;
        if (obj == null) {
            int length = objArr.length;
            while (i < length) {
                if (objArr[i] == null) {
                    return i;
                }
                i++;
            }
            return -1;
        }
        int length2 = objArr.length;
        while (i < length2) {
            if (Intrinsics.areEqual(obj, objArr[i])) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static final Appendable joinTo(double[] dArr, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(dArr, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (double d : dArr) {
            i2++;
            if (i2 > 1) {
                buffer.append(separator);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            buffer.append(function1 != null ? (CharSequence) function1.invoke(Double.valueOf(d)) : String.valueOf(d));
        }
        if (i >= 0 && i2 > i) {
            buffer.append(truncated);
        }
        buffer.append(postfix);
        return buffer;
    }

    public static final Appendable joinTo(float[] fArr, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(fArr, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (float f : fArr) {
            i2++;
            if (i2 > 1) {
                buffer.append(separator);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            buffer.append(function1 != null ? (CharSequence) function1.invoke(Float.valueOf(f)) : String.valueOf(f));
        }
        if (i >= 0 && i2 > i) {
            buffer.append(truncated);
        }
        buffer.append(postfix);
        return buffer;
    }

    public static final Appendable joinTo(int[] iArr, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(iArr, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (int i3 : iArr) {
            i2++;
            if (i2 > 1) {
                buffer.append(separator);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            buffer.append(function1 != null ? (CharSequence) function1.invoke(Integer.valueOf(i3)) : String.valueOf(i3));
        }
        if (i >= 0 && i2 > i) {
            buffer.append(truncated);
        }
        buffer.append(postfix);
        return buffer;
    }

    public static final Appendable joinTo(long[] jArr, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(jArr, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (long j : jArr) {
            i2++;
            if (i2 > 1) {
                buffer.append(separator);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            buffer.append(function1 != null ? (CharSequence) function1.invoke(Long.valueOf(j)) : String.valueOf(j));
        }
        if (i >= 0 && i2 > i) {
            buffer.append(truncated);
        }
        buffer.append(postfix);
        return buffer;
    }

    public static final Appendable joinTo(Object[] objArr, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (Object obj : objArr) {
            i2++;
            if (i2 > 1) {
                buffer.append(separator);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            StringsKt__AppendableKt.appendElement(buffer, obj, function1);
        }
        if (i >= 0 && i2 > i) {
            buffer.append(truncated);
        }
        buffer.append(postfix);
        return buffer;
    }

    public static final Appendable joinTo(short[] sArr, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(sArr, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (short s : sArr) {
            i2++;
            if (i2 > 1) {
                buffer.append(separator);
            }
            if (i >= 0 && i2 > i) {
                break;
            }
            buffer.append(function1 != null ? (CharSequence) function1.invoke(Short.valueOf(s)) : String.valueOf((int) s));
        }
        if (i >= 0 && i2 > i) {
            buffer.append(truncated);
        }
        buffer.append(postfix);
        return buffer;
    }

    public static final String joinToString(double[] dArr, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(dArr, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(dArr, new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static final String joinToString(float[] fArr, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(fArr, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(fArr, (Appendable) new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static final String joinToString(int[] iArr, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(iArr, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(iArr, (Appendable) new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static final String joinToString(long[] jArr, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(jArr, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(jArr, (Appendable) new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static final String joinToString(Object[] objArr, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(objArr, new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static final String joinToString(short[] sArr, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(sArr, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(sArr, (Appendable) new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static String joinToString$default(double[] dArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = (i2 & 2) != 0 ? "" : charSequence2;
        CharSequence charSequence6 = (i2 & 4) == 0 ? charSequence3 : "";
        int i3 = (i2 & 8) != 0 ? -1 : i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(dArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static String joinToString$default(float[] fArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = (i2 & 2) != 0 ? "" : charSequence2;
        CharSequence charSequence6 = (i2 & 4) == 0 ? charSequence3 : "";
        int i3 = (i2 & 8) != 0 ? -1 : i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(fArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static String joinToString$default(int[] iArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = (i2 & 2) != 0 ? "" : charSequence2;
        CharSequence charSequence6 = (i2 & 4) == 0 ? charSequence3 : "";
        int i3 = (i2 & 8) != 0 ? -1 : i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(iArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static String joinToString$default(long[] jArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = (i2 & 2) != 0 ? "" : charSequence2;
        CharSequence charSequence6 = (i2 & 4) == 0 ? charSequence3 : "";
        int i3 = (i2 & 8) != 0 ? -1 : i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(jArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static String joinToString$default(Object[] objArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = (i2 & 2) != 0 ? "" : charSequence2;
        CharSequence charSequence6 = (i2 & 4) == 0 ? charSequence3 : "";
        int i3 = (i2 & 8) != 0 ? -1 : i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(objArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static String joinToString$default(short[] sArr, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = ", ";
        }
        CharSequence charSequence5 = (i2 & 2) != 0 ? "" : charSequence2;
        CharSequence charSequence6 = (i2 & 4) == 0 ? charSequence3 : "";
        int i3 = (i2 & 8) != 0 ? -1 : i;
        if ((i2 & 16) != 0) {
            charSequence4 = "...";
        }
        CharSequence charSequence7 = charSequence4;
        if ((i2 & 32) != 0) {
            function1 = null;
        }
        return joinToString(sArr, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static char single(char[] cArr) {
        Intrinsics.checkNotNullParameter(cArr, "<this>");
        int length = cArr.length;
        if (length == 0) {
            throw new NoSuchElementException("Array is empty.");
        }
        if (length == 1) {
            return cArr[0];
        }
        throw new IllegalArgumentException("Array has more than one element.");
    }

    public static Object singleOrNull(Object[] objArr) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        if (objArr.length == 1) {
            return objArr[0];
        }
        return null;
    }

    public static List toList(byte[] bArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        int length = bArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(bArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(Byte.valueOf(bArr[0]));
        return listOf;
    }

    public static List toList(double[] dArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(dArr, "<this>");
        int length = dArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(dArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(Double.valueOf(dArr[0]));
        return listOf;
    }

    public static List toList(float[] fArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(fArr, "<this>");
        int length = fArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(fArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(Float.valueOf(fArr[0]));
        return listOf;
    }

    public static List toList(int[] iArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(iArr, "<this>");
        int length = iArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(iArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(Integer.valueOf(iArr[0]));
        return listOf;
    }

    public static List toList(long[] jArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(jArr, "<this>");
        int length = jArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(jArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(Long.valueOf(jArr[0]));
        return listOf;
    }

    public static List toList(Object[] objArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        int length = objArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(objArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(objArr[0]);
        return listOf;
    }

    public static List toList(short[] sArr) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(sArr, "<this>");
        int length = sArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (length != 1) {
            return toMutableList(sArr);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(Short.valueOf(sArr[0]));
        return listOf;
    }

    public static final List toMutableList(byte[] bArr) {
        Intrinsics.checkNotNullParameter(bArr, "<this>");
        ArrayList arrayList = new ArrayList(bArr.length);
        for (byte b : bArr) {
            arrayList.add(Byte.valueOf(b));
        }
        return arrayList;
    }

    public static final List toMutableList(double[] dArr) {
        Intrinsics.checkNotNullParameter(dArr, "<this>");
        ArrayList arrayList = new ArrayList(dArr.length);
        for (double d : dArr) {
            arrayList.add(Double.valueOf(d));
        }
        return arrayList;
    }

    public static final List toMutableList(float[] fArr) {
        Intrinsics.checkNotNullParameter(fArr, "<this>");
        ArrayList arrayList = new ArrayList(fArr.length);
        for (float f : fArr) {
            arrayList.add(Float.valueOf(f));
        }
        return arrayList;
    }

    public static final List toMutableList(int[] iArr) {
        Intrinsics.checkNotNullParameter(iArr, "<this>");
        ArrayList arrayList = new ArrayList(iArr.length);
        for (int i : iArr) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    public static final List toMutableList(long[] jArr) {
        Intrinsics.checkNotNullParameter(jArr, "<this>");
        ArrayList arrayList = new ArrayList(jArr.length);
        for (long j : jArr) {
            arrayList.add(Long.valueOf(j));
        }
        return arrayList;
    }

    public static final List toMutableList(Object[] objArr) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        return new ArrayList(CollectionsKt__CollectionsKt.asCollection(objArr));
    }

    public static final List toMutableList(short[] sArr) {
        Intrinsics.checkNotNullParameter(sArr, "<this>");
        ArrayList arrayList = new ArrayList(sArr.length);
        for (short s : sArr) {
            arrayList.add(Short.valueOf(s));
        }
        return arrayList;
    }
}
