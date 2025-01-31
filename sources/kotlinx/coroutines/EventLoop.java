package kotlinx.coroutines;

import kotlin.collections.ArrayDeque;

public abstract class EventLoop extends CoroutineDispatcher {
    private boolean shared;
    private ArrayDeque unconfinedQueue;
    private long useCount;

    private final long delta(boolean z) {
        return z ? 4294967296L : 1L;
    }

    public static void incrementUseCount$default(EventLoop eventLoop, boolean z, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: incrementUseCount");
        }
        if ((i & 1) != 0) {
            z = false;
        }
        eventLoop.incrementUseCount(z);
    }

    public final void decrementUseCount(boolean z) {
        long delta = this.useCount - delta(z);
        this.useCount = delta;
        if (delta <= 0 && this.shared) {
            shutdown();
        }
    }

    public final void dispatchUnconfined(DispatchedTask dispatchedTask) {
        ArrayDeque arrayDeque = this.unconfinedQueue;
        if (arrayDeque == null) {
            arrayDeque = new ArrayDeque();
            this.unconfinedQueue = arrayDeque;
        }
        arrayDeque.addLast(dispatchedTask);
    }

    public long getNextTime() {
        ArrayDeque arrayDeque = this.unconfinedQueue;
        return (arrayDeque == null || arrayDeque.isEmpty()) ? Long.MAX_VALUE : 0L;
    }

    public final void incrementUseCount(boolean z) {
        this.useCount += delta(z);
        if (z) {
            return;
        }
        this.shared = true;
    }

    public final boolean isUnconfinedLoopActive() {
        return this.useCount >= delta(true);
    }

    public final boolean isUnconfinedQueueEmpty() {
        ArrayDeque arrayDeque = this.unconfinedQueue;
        if (arrayDeque != null) {
            return arrayDeque.isEmpty();
        }
        return true;
    }

    public final boolean processUnconfinedEvent() {
        DispatchedTask dispatchedTask;
        ArrayDeque arrayDeque = this.unconfinedQueue;
        if (arrayDeque == null || (dispatchedTask = (DispatchedTask) arrayDeque.removeFirstOrNull()) == null) {
            return false;
        }
        dispatchedTask.run();
        return true;
    }

    public abstract void shutdown();
}
