package kotlinx.coroutines;

import java.util.concurrent.Future;

final class DisposableFutureHandle implements DisposableHandle {
    private final Future future;

    public DisposableFutureHandle(Future future) {
        this.future = future;
    }

    @Override
    public void dispose() {
        this.future.cancel(false);
    }

    public String toString() {
        return "DisposableFutureHandle[" + this.future + ']';
    }
}
