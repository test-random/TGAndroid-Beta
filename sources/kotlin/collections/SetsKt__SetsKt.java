package kotlin.collections;

import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

abstract class SetsKt__SetsKt extends SetsKt__SetsJVMKt {
    public static final Set emptySet() {
        return EmptySet.INSTANCE;
    }

    public static final Set optimizeReadOnlySet(Set set) {
        Intrinsics.checkNotNullParameter(set, "<this>");
        int size = set.size();
        return size != 0 ? size != 1 ? set : SetsKt__SetsJVMKt.setOf(set.iterator().next()) : emptySet();
    }
}
