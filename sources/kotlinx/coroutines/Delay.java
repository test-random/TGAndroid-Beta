package kotlinx.coroutines;

import kotlin.coroutines.CoroutineContext;

public interface Delay {

    public static final class DefaultImpls {
        public static DisposableHandle invokeOnTimeout(Delay delay, long j, Runnable runnable, CoroutineContext coroutineContext) {
            return DefaultExecutorKt.getDefaultDelay().invokeOnTimeout(j, runnable, coroutineContext);
        }
    }

    DisposableHandle invokeOnTimeout(long j, Runnable runnable, CoroutineContext coroutineContext);
}
