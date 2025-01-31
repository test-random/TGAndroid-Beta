package kotlinx.coroutines.sync;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.Waiter;
import kotlinx.coroutines.internal.ConcurrentLinkedListKt;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.SegmentOrClosed;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.scheduling.WorkQueue$$ExternalSyntheticBackportWithForwarding0;

public class SemaphoreImpl {
    private volatile int _availablePermits;
    private volatile long deqIdx;
    private volatile long enqIdx;
    private volatile Object head;
    private final Function1 onCancellationRelease;
    private final int permits;
    private volatile Object tail;
    private static final AtomicReferenceFieldUpdater head$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "head");
    private static final AtomicLongFieldUpdater deqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx");
    private static final AtomicReferenceFieldUpdater tail$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "tail");
    private static final AtomicLongFieldUpdater enqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx");
    private static final AtomicIntegerFieldUpdater _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits");

    public SemaphoreImpl(int i, int i2) {
        this.permits = i;
        if (i <= 0) {
            throw new IllegalArgumentException(("Semaphore should have at least 1 permit, but had " + i).toString());
        }
        if (i2 < 0 || i2 > i) {
            throw new IllegalArgumentException(("The number of acquired permits should be in 0.." + i).toString());
        }
        SemaphoreSegment semaphoreSegment = new SemaphoreSegment(0L, null, 2);
        this.head = semaphoreSegment;
        this.tail = semaphoreSegment;
        this._availablePermits = i - i2;
        this.onCancellationRelease = new Function1() {
            {
                super(1);
            }

            @Override
            public Object invoke(Object obj) {
                invoke((Throwable) obj);
                return Unit.INSTANCE;
            }

            public final void invoke(Throwable th) {
                SemaphoreImpl.this.release();
            }
        };
    }

    private final boolean addAcquireToQueue(Waiter waiter) {
        int i;
        Object findSegmentInternal;
        int i2;
        Symbol symbol;
        Symbol symbol2;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = tail$FU;
        SemaphoreSegment semaphoreSegment = (SemaphoreSegment) atomicReferenceFieldUpdater.get(this);
        long andIncrement = enqIdx$FU.getAndIncrement(this);
        SemaphoreImpl$addAcquireToQueue$createNewSegment$1 semaphoreImpl$addAcquireToQueue$createNewSegment$1 = SemaphoreImpl$addAcquireToQueue$createNewSegment$1.INSTANCE;
        i = SemaphoreKt.SEGMENT_SIZE;
        long j = andIncrement / i;
        loop0: while (true) {
            findSegmentInternal = ConcurrentLinkedListKt.findSegmentInternal(semaphoreSegment, j, semaphoreImpl$addAcquireToQueue$createNewSegment$1);
            if (!SegmentOrClosed.m171isClosedimpl(findSegmentInternal)) {
                Segment m170getSegmentimpl = SegmentOrClosed.m170getSegmentimpl(findSegmentInternal);
                while (true) {
                    Segment segment = (Segment) atomicReferenceFieldUpdater.get(this);
                    if (segment.id >= m170getSegmentimpl.id) {
                        break loop0;
                    }
                    if (!m170getSegmentimpl.tryIncPointers$kotlinx_coroutines_core()) {
                        break;
                    }
                    if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, segment, m170getSegmentimpl)) {
                        if (segment.decPointers$kotlinx_coroutines_core()) {
                            segment.remove();
                        }
                    } else if (m170getSegmentimpl.decPointers$kotlinx_coroutines_core()) {
                        m170getSegmentimpl.remove();
                    }
                }
            } else {
                break;
            }
        }
        SemaphoreSegment semaphoreSegment2 = (SemaphoreSegment) SegmentOrClosed.m170getSegmentimpl(findSegmentInternal);
        i2 = SemaphoreKt.SEGMENT_SIZE;
        int i3 = (int) (andIncrement % i2);
        if (WorkQueue$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment2.getAcquirers(), i3, null, waiter)) {
            waiter.invokeOnCancellation(semaphoreSegment2, i3);
            return true;
        }
        symbol = SemaphoreKt.PERMIT;
        symbol2 = SemaphoreKt.TAKEN;
        if (!WorkQueue$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment2.getAcquirers(), i3, symbol, symbol2)) {
            return false;
        }
        if (waiter instanceof CancellableContinuation) {
            Intrinsics.checkNotNull(waiter, "null cannot be cast to non-null type kotlinx.coroutines.CancellableContinuation<kotlin.Unit>");
            ((CancellableContinuation) waiter).resume(Unit.INSTANCE, this.onCancellationRelease);
            return true;
        }
        throw new IllegalStateException(("unexpected: " + waiter).toString());
    }

    private final void coerceAvailablePermitsAtMaximum() {
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater;
        int i;
        int i2;
        do {
            atomicIntegerFieldUpdater = _availablePermits$FU;
            i = atomicIntegerFieldUpdater.get(this);
            i2 = this.permits;
            if (i <= i2) {
                return;
            }
        } while (!atomicIntegerFieldUpdater.compareAndSet(this, i, i2));
    }

    private final int decPermits() {
        int andDecrement;
        do {
            andDecrement = _availablePermits$FU.getAndDecrement(this);
        } while (andDecrement > this.permits);
        return andDecrement;
    }

    private final boolean tryResumeAcquire(Object obj) {
        if (!(obj instanceof CancellableContinuation)) {
            throw new IllegalStateException(("unexpected: " + obj).toString());
        }
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.CancellableContinuation<kotlin.Unit>");
        CancellableContinuation cancellableContinuation = (CancellableContinuation) obj;
        Object tryResume = cancellableContinuation.tryResume(Unit.INSTANCE, null, this.onCancellationRelease);
        if (tryResume == null) {
            return false;
        }
        cancellableContinuation.completeResume(tryResume);
        return true;
    }

    private final boolean tryResumeNextFromQueue() {
        int i;
        Object findSegmentInternal;
        int i2;
        Symbol symbol;
        Symbol symbol2;
        int i3;
        Symbol symbol3;
        Symbol symbol4;
        Symbol symbol5;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = head$FU;
        SemaphoreSegment semaphoreSegment = (SemaphoreSegment) atomicReferenceFieldUpdater.get(this);
        long andIncrement = deqIdx$FU.getAndIncrement(this);
        i = SemaphoreKt.SEGMENT_SIZE;
        long j = andIncrement / i;
        SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1 semaphoreImpl$tryResumeNextFromQueue$createNewSegment$1 = SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1.INSTANCE;
        loop0: while (true) {
            findSegmentInternal = ConcurrentLinkedListKt.findSegmentInternal(semaphoreSegment, j, semaphoreImpl$tryResumeNextFromQueue$createNewSegment$1);
            if (SegmentOrClosed.m171isClosedimpl(findSegmentInternal)) {
                break;
            }
            Segment m170getSegmentimpl = SegmentOrClosed.m170getSegmentimpl(findSegmentInternal);
            while (true) {
                Segment segment = (Segment) atomicReferenceFieldUpdater.get(this);
                if (segment.id >= m170getSegmentimpl.id) {
                    break loop0;
                }
                if (!m170getSegmentimpl.tryIncPointers$kotlinx_coroutines_core()) {
                    break;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, segment, m170getSegmentimpl)) {
                    if (segment.decPointers$kotlinx_coroutines_core()) {
                        segment.remove();
                    }
                } else if (m170getSegmentimpl.decPointers$kotlinx_coroutines_core()) {
                    m170getSegmentimpl.remove();
                }
            }
        }
        SemaphoreSegment semaphoreSegment2 = (SemaphoreSegment) SegmentOrClosed.m170getSegmentimpl(findSegmentInternal);
        semaphoreSegment2.cleanPrev();
        if (semaphoreSegment2.id > j) {
            return false;
        }
        i2 = SemaphoreKt.SEGMENT_SIZE;
        int i4 = (int) (andIncrement % i2);
        symbol = SemaphoreKt.PERMIT;
        Object andSet = semaphoreSegment2.getAcquirers().getAndSet(i4, symbol);
        if (andSet != null) {
            symbol2 = SemaphoreKt.CANCELLED;
            if (andSet == symbol2) {
                return false;
            }
            return tryResumeAcquire(andSet);
        }
        i3 = SemaphoreKt.MAX_SPIN_CYCLES;
        for (int i5 = 0; i5 < i3; i5++) {
            Object obj = semaphoreSegment2.getAcquirers().get(i4);
            symbol5 = SemaphoreKt.TAKEN;
            if (obj == symbol5) {
                return true;
            }
        }
        symbol3 = SemaphoreKt.PERMIT;
        symbol4 = SemaphoreKt.BROKEN;
        return !WorkQueue$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment2.getAcquirers(), i4, symbol3, symbol4);
    }

    public final void acquire(CancellableContinuation cancellableContinuation) {
        while (decPermits() <= 0) {
            Intrinsics.checkNotNull(cancellableContinuation, "null cannot be cast to non-null type kotlinx.coroutines.Waiter");
            if (addAcquireToQueue((Waiter) cancellableContinuation)) {
                return;
            }
        }
        cancellableContinuation.resume(Unit.INSTANCE, this.onCancellationRelease);
    }

    public int getAvailablePermits() {
        return Math.max(_availablePermits$FU.get(this), 0);
    }

    public void release() {
        do {
            int andIncrement = _availablePermits$FU.getAndIncrement(this);
            if (andIncrement >= this.permits) {
                coerceAvailablePermitsAtMaximum();
                throw new IllegalStateException(("The number of released permits cannot be greater than " + this.permits).toString());
            }
            if (andIncrement >= 0) {
                return;
            }
        } while (!tryResumeNextFromQueue());
    }

    public boolean tryAcquire() {
        while (true) {
            AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = _availablePermits$FU;
            int i = atomicIntegerFieldUpdater.get(this);
            if (i > this.permits) {
                coerceAvailablePermitsAtMaximum();
            } else {
                if (i <= 0) {
                    return false;
                }
                if (atomicIntegerFieldUpdater.compareAndSet(this, i, i - 1)) {
                    return true;
                }
            }
        }
    }
}
