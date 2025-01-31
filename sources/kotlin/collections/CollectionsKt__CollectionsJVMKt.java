package kotlin.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public abstract class CollectionsKt__CollectionsJVMKt {
    public static final Object[] copyToArrayOfAny(Object[] objArr, boolean z) {
        Intrinsics.checkNotNullParameter(objArr, "<this>");
        if (z && Intrinsics.areEqual(objArr.getClass(), Object[].class)) {
            return objArr;
        }
        Object[] copyOf = Arrays.copyOf(objArr, objArr.length, Object[].class);
        Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, this.size, Array<Any?>::class.java)");
        return copyOf;
    }

    public static List listOf(Object obj) {
        List singletonList = Collections.singletonList(obj);
        Intrinsics.checkNotNullExpressionValue(singletonList, "singletonList(element)");
        return singletonList;
    }

    public static List shuffled(Iterable iterable) {
        Intrinsics.checkNotNullParameter(iterable, "<this>");
        List mutableList = CollectionsKt___CollectionsKt.toMutableList(iterable);
        Collections.shuffle(mutableList);
        return mutableList;
    }
}
