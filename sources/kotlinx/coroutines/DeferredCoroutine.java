package kotlinx.coroutines;

import kotlin.coroutines.CoroutineContext;

public class DeferredCoroutine extends AbstractCoroutine implements Deferred {
    public DeferredCoroutine(CoroutineContext coroutineContext, boolean z) {
        super(coroutineContext, true, z);
    }

    @Override
    public Object getCompleted() {
        return getCompletedInternal$kotlinx_coroutines_core();
    }
}
