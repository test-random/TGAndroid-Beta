package kotlin.collections;

import java.util.Collections;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

abstract class SetsKt__SetsJVMKt {
    public static final Set setOf(Object obj) {
        Set singleton = Collections.singleton(obj);
        Intrinsics.checkNotNullExpressionValue(singleton, "singleton(element)");
        return singleton;
    }
}
