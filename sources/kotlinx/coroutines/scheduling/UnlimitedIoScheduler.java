package kotlinx.coroutines.scheduling;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.internal.LimitedDispatcherKt;

final class UnlimitedIoScheduler extends CoroutineDispatcher {
    public static final UnlimitedIoScheduler INSTANCE = new UnlimitedIoScheduler();

    private UnlimitedIoScheduler() {
    }

    @Override
    public void dispatch(CoroutineContext coroutineContext, Runnable runnable) {
        DefaultScheduler.INSTANCE.dispatchWithContext$kotlinx_coroutines_core(runnable, TasksKt.BlockingContext, false);
    }

    @Override
    public CoroutineDispatcher limitedParallelism(int i) {
        LimitedDispatcherKt.checkParallelism(i);
        return i >= TasksKt.MAX_POOL_SIZE ? this : super.limitedParallelism(i);
    }
}
