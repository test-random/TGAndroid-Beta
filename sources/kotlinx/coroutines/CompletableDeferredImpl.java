package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;

final class CompletableDeferredImpl extends JobSupport implements CompletableDeferred {
    public CompletableDeferredImpl(Job job) {
        super(true);
        initParentJob(job);
    }

    @Override
    public Object await(Continuation continuation) {
        Object awaitInternal = awaitInternal(continuation);
        IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        return awaitInternal;
    }

    @Override
    public boolean complete(Object obj) {
        return makeCompleting$kotlinx_coroutines_core(obj);
    }

    @Override
    public boolean completeExceptionally(Throwable th) {
        return makeCompleting$kotlinx_coroutines_core(new CompletedExceptionally(th, false, 2, null));
    }

    @Override
    public Object getCompleted() {
        return getCompletedInternal$kotlinx_coroutines_core();
    }

    @Override
    public boolean getOnCancelComplete$kotlinx_coroutines_core() {
        return true;
    }
}
