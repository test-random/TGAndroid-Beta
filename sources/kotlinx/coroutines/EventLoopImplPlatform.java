package kotlinx.coroutines;

import java.util.concurrent.locks.LockSupport;
import kotlinx.coroutines.EventLoopImplBase;

public abstract class EventLoopImplPlatform extends EventLoop {
    protected abstract Thread getThread();

    public void reschedule(long j, EventLoopImplBase.DelayedTask delayedTask) {
        DefaultExecutor.INSTANCE.schedule(j, delayedTask);
    }

    public final void unpark() {
        Thread thread = getThread();
        if (Thread.currentThread() != thread) {
            AbstractTimeSourceKt.getTimeSource();
            LockSupport.unpark(thread);
        }
    }
}
