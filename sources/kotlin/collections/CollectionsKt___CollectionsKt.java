package kotlin.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;
import kotlin.TuplesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt___RangesKt;
import kotlin.text.StringsKt__AppendableKt;

public abstract class CollectionsKt___CollectionsKt extends CollectionsKt___CollectionsJvmKt {
    public static final Object first(Iterable iterable) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        if (iterable instanceof List) {
            return first((List) iterable);
        }
        Iterator it = iterable.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        throw new NoSuchElementException("Collection is empty.");
    }

    public static final Object first(List list) {
        Intrinsics.checkNotNullParameter(list, "<this>");
        if (list.isEmpty()) {
            throw new NoSuchElementException("List is empty.");
        }
        return list.get(0);
    }

    public static final Appendable joinTo(Iterable iterable, Appendable buffer, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        buffer.append(prefix);
        int i2 = 0;
        for (Object obj : iterable) {
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

    public static final String joinToString(Iterable iterable, CharSequence separator, CharSequence prefix, CharSequence postfix, int i, CharSequence truncated, Function1 function1) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        Intrinsics.checkNotNullParameter(separator, "separator");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        Intrinsics.checkNotNullParameter(postfix, "postfix");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        String sb = ((StringBuilder) joinTo(iterable, new StringBuilder(), separator, prefix, postfix, i, truncated, function1)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "joinTo(StringBuilder(), …ed, transform).toString()");
        return sb;
    }

    public static String joinToString$default(Iterable iterable, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, CharSequence charSequence4, Function1 function1, int i2, Object obj) {
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
        return joinToString(iterable, charSequence, charSequence5, charSequence6, i3, charSequence7, function1);
    }

    public static List plus(Collection collection, Iterable elements) {
        Intrinsics.checkNotNullParameter(collection, "<this>");
        Intrinsics.checkNotNullParameter(elements, "elements");
        if (!(elements instanceof Collection)) {
            ArrayList arrayList = new ArrayList(collection);
            CollectionsKt__MutableCollectionsKt.addAll(arrayList, elements);
            return arrayList;
        }
        Collection collection2 = (Collection) elements;
        ArrayList arrayList2 = new ArrayList(collection.size() + collection2.size());
        arrayList2.addAll(collection);
        arrayList2.addAll(collection2);
        return arrayList2;
    }

    public static List take(Iterable iterable, int i) {
        List optimizeReadOnlyList;
        List listOf;
        List list;
        List emptyList;
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        if (i < 0) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        }
        if (i == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (iterable instanceof Collection) {
            if (i >= ((Collection) iterable).size()) {
                list = toList(iterable);
                return list;
            }
            if (i == 1) {
                listOf = CollectionsKt__CollectionsJVMKt.listOf(first(iterable));
                return listOf;
            }
        }
        ArrayList arrayList = new ArrayList(i);
        Iterator it = iterable.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            arrayList.add(it.next());
            i2++;
            if (i2 == i) {
                break;
            }
        }
        optimizeReadOnlyList = CollectionsKt__CollectionsKt.optimizeReadOnlyList(arrayList);
        return optimizeReadOnlyList;
    }

    public static byte[] toByteArray(Collection collection) {
        Intrinsics.checkNotNullParameter(collection, "<this>");
        byte[] bArr = new byte[collection.size()];
        Iterator it = collection.iterator();
        int i = 0;
        while (it.hasNext()) {
            bArr[i] = ((Number) it.next()).byteValue();
            i++;
        }
        return bArr;
    }

    public static final Collection toCollection(Iterable iterable, Collection destination) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        Intrinsics.checkNotNullParameter(destination, "destination");
        Iterator it = iterable.iterator();
        while (it.hasNext()) {
            destination.add(it.next());
        }
        return destination;
    }

    public static int[] toIntArray(Collection collection) {
        Intrinsics.checkNotNullParameter(collection, "<this>");
        int[] iArr = new int[collection.size()];
        Iterator it = collection.iterator();
        int i = 0;
        while (it.hasNext()) {
            iArr[i] = ((Number) it.next()).intValue();
            i++;
        }
        return iArr;
    }

    public static List toList(Iterable iterable) {
        List optimizeReadOnlyList;
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        if (!(iterable instanceof Collection)) {
            optimizeReadOnlyList = CollectionsKt__CollectionsKt.optimizeReadOnlyList(toMutableList(iterable));
            return optimizeReadOnlyList;
        }
        Collection collection = (Collection) iterable;
        int size = collection.size();
        if (size == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        }
        if (size != 1) {
            return toMutableList(collection);
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next());
        return listOf;
    }

    public static long[] toLongArray(Collection collection) {
        Intrinsics.checkNotNullParameter(collection, "<this>");
        long[] jArr = new long[collection.size()];
        Iterator it = collection.iterator();
        int i = 0;
        while (it.hasNext()) {
            jArr[i] = ((Number) it.next()).longValue();
            i++;
        }
        return jArr;
    }

    public static final List toMutableList(Iterable iterable) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        return iterable instanceof Collection ? toMutableList((Collection) iterable) : (List) toCollection(iterable, new ArrayList());
    }

    public static final List toMutableList(Collection collection) {
        Intrinsics.checkNotNullParameter(collection, "<this>");
        return new ArrayList(collection);
    }

    public static Set toSet(Iterable iterable) {
        int mapCapacity;
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        if (!(iterable instanceof Collection)) {
            return SetsKt__SetsKt.optimizeReadOnlySet((Set) toCollection(iterable, new LinkedHashSet()));
        }
        Collection collection = (Collection) iterable;
        int size = collection.size();
        if (size == 0) {
            return SetsKt__SetsKt.emptySet();
        }
        if (size == 1) {
            return SetsKt__SetsJVMKt.setOf(iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next());
        }
        mapCapacity = MapsKt__MapsJVMKt.mapCapacity(collection.size());
        return (Set) toCollection(iterable, new LinkedHashSet(mapCapacity));
    }

    public static List windowed(Iterable iterable, int i, int i2, boolean z) {
        int coerceAtMost;
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        SlidingWindowKt.checkWindowSizeStep(i, i2);
        if (!(iterable instanceof RandomAccess) || !(iterable instanceof List)) {
            ArrayList arrayList = new ArrayList();
            Iterator windowedIterator = SlidingWindowKt.windowedIterator(iterable.iterator(), i, i2, z, false);
            while (windowedIterator.hasNext()) {
                arrayList.add((List) windowedIterator.next());
            }
            return arrayList;
        }
        List list = (List) iterable;
        int size = list.size();
        ArrayList arrayList2 = new ArrayList((size / i2) + (size % i2 == 0 ? 0 : 1));
        int i3 = 0;
        while (i3 >= 0 && i3 < size) {
            coerceAtMost = RangesKt___RangesKt.coerceAtMost(i, size - i3);
            if (coerceAtMost < i && !z) {
                break;
            }
            ArrayList arrayList3 = new ArrayList(coerceAtMost);
            for (int i4 = 0; i4 < coerceAtMost; i4++) {
                arrayList3.add(list.get(i4 + i3));
            }
            arrayList2.add(arrayList3);
            i3 += i2;
        }
        return arrayList2;
    }

    public static List zip(Iterable iterable, Iterable other) {
        int collectionSizeOrDefault;
        int collectionSizeOrDefault2;
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        Iterator it = iterable.iterator();
        Iterator it2 = other.iterator();
        collectionSizeOrDefault = CollectionsKt__IterablesKt.collectionSizeOrDefault(iterable, 10);
        collectionSizeOrDefault2 = CollectionsKt__IterablesKt.collectionSizeOrDefault(other, 10);
        ArrayList arrayList = new ArrayList(Math.min(collectionSizeOrDefault, collectionSizeOrDefault2));
        while (it.hasNext() && it2.hasNext()) {
            arrayList.add(TuplesKt.to(it.next(), it2.next()));
        }
        return arrayList;
    }
}
