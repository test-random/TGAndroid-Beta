package kotlinx.coroutines.scheduling;

public final class DefaultScheduler extends SchedulerCoroutineDispatcher {
    public static final DefaultScheduler INSTANCE = new DefaultScheduler();

    private DefaultScheduler() {
        super(TasksKt.CORE_POOL_SIZE, TasksKt.MAX_POOL_SIZE, TasksKt.IDLE_WORKER_KEEP_ALIVE_NS, TasksKt.DEFAULT_SCHEDULER_NAME);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Dispatchers.Default cannot be closed");
    }

    @Override
    public String toString() {
        return "Dispatchers.Default";
    }
}
