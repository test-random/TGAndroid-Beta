package kotlinx.coroutines;

abstract class ThreadPoolDispatcherKt__MultithreadedDispatchers_commonKt {
    public static final ExecutorCoroutineDispatcher newSingleThreadContext(String str) {
        return ThreadPoolDispatcherKt.newFixedThreadPoolContext(1, str);
    }
}
