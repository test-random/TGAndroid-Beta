package kotlinx.coroutines;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

final class InvokeOnCancelling extends JobCancellingNode {
    private static final AtomicIntegerFieldUpdater _invoked$FU = AtomicIntegerFieldUpdater.newUpdater(InvokeOnCancelling.class, "_invoked");
    private volatile int _invoked;
    private final Function1 handler;

    public InvokeOnCancelling(Function1 function1) {
        this.handler = function1;
    }

    @Override
    public Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    @Override
    public void invoke(Throwable th) {
        if (_invoked$FU.compareAndSet(this, 0, 1)) {
            this.handler.invoke(th);
        }
    }
}
