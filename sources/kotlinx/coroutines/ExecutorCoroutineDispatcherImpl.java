package kotlinx.coroutines;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.internal.ConcurrentKt;

public final class ExecutorCoroutineDispatcherImpl extends ExecutorCoroutineDispatcher implements Delay {
    private final Executor executor;

    public ExecutorCoroutineDispatcherImpl(Executor executor) {
        this.executor = executor;
        ConcurrentKt.removeFutureOnCancel(getExecutor());
    }

    private final void cancelJobOnRejection(CoroutineContext coroutineContext, RejectedExecutionException rejectedExecutionException) {
        JobKt.cancel(coroutineContext, ExceptionsKt.CancellationException("The task was rejected", rejectedExecutionException));
    }

    private final ScheduledFuture scheduleBlock(ScheduledExecutorService scheduledExecutorService, Runnable runnable, CoroutineContext coroutineContext, long j) {
        try {
            return scheduledExecutorService.schedule(runnable, j, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            cancelJobOnRejection(coroutineContext, e);
            return null;
        }
    }

    @Override
    public void close() {
        Executor executor = getExecutor();
        ExecutorService executorService = executor instanceof ExecutorService ? (ExecutorService) executor : null;
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void dispatch(CoroutineContext coroutineContext, Runnable runnable) {
        try {
            Executor executor = getExecutor();
            AbstractTimeSourceKt.getTimeSource();
            executor.execute(runnable);
        } catch (RejectedExecutionException e) {
            AbstractTimeSourceKt.getTimeSource();
            cancelJobOnRejection(coroutineContext, e);
            Dispatchers.getIO().dispatch(coroutineContext, runnable);
        }
    }

    public boolean equals(Object obj) {
        return (obj instanceof ExecutorCoroutineDispatcherImpl) && ((ExecutorCoroutineDispatcherImpl) obj).getExecutor() == getExecutor();
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public int hashCode() {
        return System.identityHashCode(getExecutor());
    }

    @Override
    public DisposableHandle invokeOnTimeout(long j, Runnable runnable, CoroutineContext coroutineContext) {
        Executor executor = getExecutor();
        ScheduledExecutorService scheduledExecutorService = executor instanceof ScheduledExecutorService ? (ScheduledExecutorService) executor : null;
        ScheduledFuture scheduleBlock = scheduledExecutorService != null ? scheduleBlock(scheduledExecutorService, runnable, coroutineContext, j) : null;
        return scheduleBlock != null ? new DisposableFutureHandle(scheduleBlock) : DefaultExecutor.INSTANCE.invokeOnTimeout(j, runnable, coroutineContext);
    }

    @Override
    public String toString() {
        return getExecutor().toString();
    }
}
