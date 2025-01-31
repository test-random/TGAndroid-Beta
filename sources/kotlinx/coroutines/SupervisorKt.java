package kotlinx.coroutines;

public abstract class SupervisorKt {
    public static final CompletableJob SupervisorJob(Job job) {
        return new SupervisorJobImpl(job);
    }

    public static CompletableJob SupervisorJob$default(Job job, int i, Object obj) {
        if ((i & 1) != 0) {
            job = null;
        }
        return SupervisorJob(job);
    }
}
