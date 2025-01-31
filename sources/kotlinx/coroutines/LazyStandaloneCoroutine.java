package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.intrinsics.CancellableKt;

public final class LazyStandaloneCoroutine extends StandaloneCoroutine {
    private final Continuation continuation;

    public LazyStandaloneCoroutine(CoroutineContext coroutineContext, Function2 function2) {
        super(coroutineContext, false);
        Continuation createCoroutineUnintercepted;
        createCoroutineUnintercepted = IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted(function2, this, this);
        this.continuation = createCoroutineUnintercepted;
    }

    @Override
    protected void onStart() {
        CancellableKt.startCoroutineCancellable(this.continuation, this);
    }
}
