package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;

public abstract class BuildersKt {
    public static final Deferred async(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2) {
        return BuildersKt__Builders_commonKt.async(coroutineScope, coroutineContext, coroutineStart, function2);
    }

    public static final Job launch(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2) {
        return BuildersKt__Builders_commonKt.launch(coroutineScope, coroutineContext, coroutineStart, function2);
    }

    public static final Object withContext(CoroutineContext coroutineContext, Function2 function2, Continuation continuation) {
        return BuildersKt__Builders_commonKt.withContext(coroutineContext, function2, continuation);
    }
}
