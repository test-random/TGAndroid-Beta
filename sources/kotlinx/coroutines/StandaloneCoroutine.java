package kotlinx.coroutines;

import kotlin.coroutines.CoroutineContext;

public class StandaloneCoroutine extends AbstractCoroutine {
    public StandaloneCoroutine(CoroutineContext coroutineContext, boolean z) {
        super(coroutineContext, true, z);
    }

    @Override
    protected boolean handleJobException(Throwable th) {
        CoroutineExceptionHandlerKt.handleCoroutineException(getContext(), th);
        return true;
    }
}
