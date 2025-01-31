package kotlin.collections;

import java.util.Collection;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public abstract class CollectionsKt__CollectionsKt extends CollectionsKt__CollectionsJVMKt {
    public static final Collection asCollection(Object[] objArr) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        return new ArrayAsCollection(objArr, false);
    }

    public static List emptyList() {
        return EmptyList.INSTANCE;
    }

    public static final int getLastIndex(List list) {
        Intrinsics.checkNotNullParameter(list, "<this>");
        return list.size() - 1;
    }

    public static List listOf(Object... elements) {
        List emptyList;
        List asList;
        Intrinsics.checkNotNullParameter(elements, "elements");
        if (elements.length > 0) {
            asList = ArraysKt___ArraysJvmKt.asList(elements);
            return asList;
        }
        emptyList = emptyList();
        return emptyList;
    }

    public static List optimizeReadOnlyList(List list) {
        List emptyList;
        List listOf;
        Intrinsics.checkNotNullParameter(list, "<this>");
        int size = list.size();
        if (size == 0) {
            emptyList = emptyList();
            return emptyList;
        }
        if (size != 1) {
            return list;
        }
        listOf = CollectionsKt__CollectionsJVMKt.listOf(list.get(0));
        return listOf;
    }

    public static void throwIndexOverflow() {
        throw new ArithmeticException("Index overflow has happened.");
    }
}
