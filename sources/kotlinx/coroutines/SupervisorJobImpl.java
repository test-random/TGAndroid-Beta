package kotlinx.coroutines;

final class SupervisorJobImpl extends JobImpl {
    public SupervisorJobImpl(Job job) {
        super(job);
    }

    @Override
    public boolean childCancelled(Throwable th) {
        return false;
    }
}
