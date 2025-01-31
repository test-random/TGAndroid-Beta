package kotlinx.coroutines;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

final class InvokeOnCancel extends CancelHandler {
    private final Function1 handler;

    public InvokeOnCancel(Function1 function1) {
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

    public String toString() {
        return "InvokeOnCancel[" + DebugStringsKt.getClassSimpleName(this.handler) + '@' + DebugStringsKt.getHexAddress(this) + ']';
    }
}
