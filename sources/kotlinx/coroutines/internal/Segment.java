package kotlinx.coroutines.internal;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.NotCompleted;

public abstract class Segment extends ConcurrentLinkedListNode implements NotCompleted {
    private static final AtomicIntegerFieldUpdater cleanedAndPointers$FU = AtomicIntegerFieldUpdater.newUpdater(Segment.class, "cleanedAndPointers");
    private volatile int cleanedAndPointers;
    public final long id;

    public Segment(long j, Segment segment, int i) {
        super(segment);
        this.id = j;
        this.cleanedAndPointers = i << 16;
    }

    public final boolean decPointers$kotlinx_coroutines_core() {
        return cleanedAndPointers$FU.addAndGet(this, -65536) == getNumberOfSlots() && !isTail();
    }

    public abstract int getNumberOfSlots();

    @Override
    public boolean isRemoved() {
        return cleanedAndPointers$FU.get(this) == getNumberOfSlots() && !isTail();
    }

    public abstract void onCancellation(int i, Throwable th, CoroutineContext coroutineContext);

    public final void onSlotCleaned() {
        if (cleanedAndPointers$FU.incrementAndGet(this) == getNumberOfSlots()) {
            remove();
        }
    }

    public final boolean tryIncPointers$kotlinx_coroutines_core() {
        int i;
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = cleanedAndPointers$FU;
        do {
            i = atomicIntegerFieldUpdater.get(this);
            if (i == getNumberOfSlots() && !isTail()) {
                return false;
            }
        } while (!atomicIntegerFieldUpdater.compareAndSet(this, i, 65536 + i));
        return true;
    }
}
