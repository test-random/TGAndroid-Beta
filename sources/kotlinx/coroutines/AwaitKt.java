package kotlinx.coroutines;

import java.util.List;
import kotlin.collections.CollectionsKt__CollectionsKt;
import kotlin.coroutines.Continuation;

public abstract class AwaitKt {
    public static final Object awaitAll(Deferred[] deferredArr, Continuation continuation) {
        List emptyList;
        if (deferredArr.length != 0) {
            return new AwaitAll(deferredArr).await(continuation);
        }
        emptyList = CollectionsKt__CollectionsKt.emptyList();
        return emptyList;
    }

    public static final java.lang.Object joinAll(java.util.Collection r4, kotlin.coroutines.Continuation r5) {
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.AwaitKt.joinAll(java.util.Collection, kotlin.coroutines.Continuation):java.lang.Object");
    }
}
