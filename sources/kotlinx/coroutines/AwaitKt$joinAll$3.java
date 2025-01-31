package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;

public final class AwaitKt$joinAll$3 extends ContinuationImpl {
    Object L$0;
    int label;
    Object result;

    public AwaitKt$joinAll$3(Continuation continuation) {
        super(continuation);
    }

    @Override
    public final Object invokeSuspend(Object obj) {
        this.result = obj;
        this.label |= Integer.MIN_VALUE;
        return AwaitKt.joinAll(null, this);
    }
}
