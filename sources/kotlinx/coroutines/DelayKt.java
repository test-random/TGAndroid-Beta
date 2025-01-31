package kotlinx.coroutines;

import kotlin.coroutines.ContinuationInterceptor;
import kotlin.coroutines.CoroutineContext;

public abstract class DelayKt {
    public static final Delay getDelay(CoroutineContext coroutineContext) {
        CoroutineContext.Element element = coroutineContext.get(ContinuationInterceptor.Key);
        Delay delay = element instanceof Delay ? (Delay) element : null;
        return delay == null ? DefaultExecutorKt.getDefaultDelay() : delay;
    }
}
