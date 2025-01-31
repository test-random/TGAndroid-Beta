package kotlinx.coroutines.sync;

import java.util.concurrent.atomic.AtomicReferenceArray;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.Symbol;

public final class SemaphoreSegment extends Segment {
    private final AtomicReferenceArray acquirers;

    public SemaphoreSegment(long j, SemaphoreSegment semaphoreSegment, int i) {
        super(j, semaphoreSegment, i);
        int i2;
        i2 = SemaphoreKt.SEGMENT_SIZE;
        this.acquirers = new AtomicReferenceArray(i2);
    }

    public final AtomicReferenceArray getAcquirers() {
        return this.acquirers;
    }

    @Override
    public int getNumberOfSlots() {
        int i;
        i = SemaphoreKt.SEGMENT_SIZE;
        return i;
    }

    @Override
    public void onCancellation(int i, Throwable th, CoroutineContext coroutineContext) {
        Symbol symbol;
        symbol = SemaphoreKt.CANCELLED;
        getAcquirers().set(i, symbol);
        onSlotCleaned();
    }

    public String toString() {
        return "SemaphoreSegment[id=" + this.id + ", hashCode=" + hashCode() + ']';
    }
}
