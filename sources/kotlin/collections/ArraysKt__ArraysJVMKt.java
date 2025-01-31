package kotlin.collections;

import java.lang.reflect.Array;
import kotlin.jvm.internal.Intrinsics;

abstract class ArraysKt__ArraysJVMKt {
    public static final Object[] arrayOfNulls(Object[] reference, int i) {
        Intrinsics.checkNotNullParameter(reference, "reference");
        Object newInstance = Array.newInstance(reference.getClass().getComponentType(), i);
        Intrinsics.checkNotNull(newInstance, "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.arrayOfNulls>");
        return (Object[]) newInstance;
    }
}
