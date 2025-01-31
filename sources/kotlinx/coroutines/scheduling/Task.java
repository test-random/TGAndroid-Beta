package kotlinx.coroutines.scheduling;

public abstract class Task implements Runnable {
    public long submissionTime;
    public TaskContext taskContext;

    public Task() {
        this(0L, TasksKt.NonBlockingContext);
    }

    public Task(long j, TaskContext taskContext) {
        this.submissionTime = j;
        this.taskContext = taskContext;
    }
}
