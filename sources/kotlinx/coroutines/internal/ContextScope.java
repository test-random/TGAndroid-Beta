package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;

public final class ContextScope implements CoroutineScope {
    private final CoroutineContext coroutineContext;

    public ContextScope(CoroutineContext coroutineContext) {
        this.coroutineContext = coroutineContext;
    }

    @Override
    public CoroutineContext getCoroutineContext() {
        return this.coroutineContext;
    }

    public String toString() {
        return "CoroutineScope(coroutineContext=" + getCoroutineContext() + ')';
    }
}
