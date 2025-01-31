package kotlinx.coroutines;

import kotlin.Unit;

public final class ChildHandleNode extends JobCancellingNode implements ChildHandle {
    public final ChildJob childJob;

    public ChildHandleNode(ChildJob childJob) {
        this.childJob = childJob;
    }

    @Override
    public boolean childCancelled(Throwable th) {
        return getJob().childCancelled(th);
    }

    @Override
    public Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    @Override
    public void invoke(Throwable th) {
        this.childJob.parentCancelled(getJob());
    }
}
