package kotlinx.coroutines;

import kotlin.Unit;

final class DisposeOnCancel extends CancelHandler {
    private final DisposableHandle handle;

    public DisposeOnCancel(DisposableHandle disposableHandle) {
        this.handle = disposableHandle;
    }

    @Override
    public Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    @Override
    public void invoke(Throwable th) {
        this.handle.dispose();
    }

    public String toString() {
        return "DisposeOnCancel[" + this.handle + ']';
    }
}
