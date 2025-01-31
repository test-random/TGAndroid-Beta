package kotlin;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public abstract class LazyKt__LazyJVMKt {
    public static Lazy lazy(Function0 initializer) {
        Intrinsics.checkNotNullParameter(initializer, "initializer");
        DefaultConstructorMarker defaultConstructorMarker = null;
        return new SynchronizedLazyImpl(initializer, defaultConstructorMarker, 2, defaultConstructorMarker);
    }
}
