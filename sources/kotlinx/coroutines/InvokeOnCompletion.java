package kotlinx.coroutines;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public final class InvokeOnCompletion extends JobNode {
    private final Function1 handler;

    public InvokeOnCompletion(Function1 function1) {
        this.handler = function1;
    }

    @Override
    public Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    @Override
    public void invoke(Throwable th) {
        this.handler.invoke(th);
    }
}
