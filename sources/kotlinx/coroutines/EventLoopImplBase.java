package kotlinx.coroutines;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt___RangesKt;
import kotlinx.coroutines.Delay;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.ThreadSafeHeap;
import kotlinx.coroutines.internal.ThreadSafeHeapNode;

public abstract class EventLoopImplBase extends EventLoopImplPlatform implements Delay {
    private volatile Object _delayed;
    private volatile int _isCompleted = 0;
    private volatile Object _queue;
    private static final AtomicReferenceFieldUpdater _queue$FU = AtomicReferenceFieldUpdater.newUpdater(EventLoopImplBase.class, Object.class, "_queue");
    private static final AtomicReferenceFieldUpdater _delayed$FU = AtomicReferenceFieldUpdater.newUpdater(EventLoopImplBase.class, Object.class, "_delayed");
    private static final AtomicIntegerFieldUpdater _isCompleted$FU = AtomicIntegerFieldUpdater.newUpdater(EventLoopImplBase.class, "_isCompleted");

    private static final class DelayedRunnableTask extends DelayedTask {
        private final Runnable block;

        public DelayedRunnableTask(long j, Runnable runnable) {
            super(j);
            this.block = runnable;
        }

        @Override
        public void run() {
            this.block.run();
        }

        @Override
        public String toString() {
            return super.toString() + this.block;
        }
    }

    public static abstract class DelayedTask implements Runnable, Comparable, DisposableHandle, ThreadSafeHeapNode {
        private volatile Object _heap;
        private int index = -1;
        public long nanoTime;

        public DelayedTask(long j) {
            this.nanoTime = j;
        }

        @Override
        public int compareTo(DelayedTask delayedTask) {
            long j = this.nanoTime - delayedTask.nanoTime;
            if (j > 0) {
                return 1;
            }
            return j < 0 ? -1 : 0;
        }

        @Override
        public final void dispose() {
            Symbol symbol;
            Symbol symbol2;
            synchronized (this) {
                try {
                    Object obj = this._heap;
                    symbol = EventLoop_commonKt.DISPOSED_TASK;
                    if (obj == symbol) {
                        return;
                    }
                    DelayedTaskQueue delayedTaskQueue = obj instanceof DelayedTaskQueue ? (DelayedTaskQueue) obj : null;
                    if (delayedTaskQueue != null) {
                        delayedTaskQueue.remove(this);
                    }
                    symbol2 = EventLoop_commonKt.DISPOSED_TASK;
                    this._heap = symbol2;
                    Unit unit = Unit.INSTANCE;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override
        public ThreadSafeHeap getHeap() {
            Object obj = this._heap;
            if (obj instanceof ThreadSafeHeap) {
                return (ThreadSafeHeap) obj;
            }
            return null;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        public final int scheduleTask(long r8, kotlinx.coroutines.EventLoopImplBase.DelayedTaskQueue r10, kotlinx.coroutines.EventLoopImplBase r11) {
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.EventLoopImplBase.DelayedTask.scheduleTask(long, kotlinx.coroutines.EventLoopImplBase$DelayedTaskQueue, kotlinx.coroutines.EventLoopImplBase):int");
        }

        @Override
        public void setHeap(ThreadSafeHeap threadSafeHeap) {
            Symbol symbol;
            Object obj = this._heap;
            symbol = EventLoop_commonKt.DISPOSED_TASK;
            if (obj == symbol) {
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            this._heap = threadSafeHeap;
        }

        @Override
        public void setIndex(int i) {
            this.index = i;
        }

        public final boolean timeToExecute(long j) {
            return j - this.nanoTime >= 0;
        }

        public String toString() {
            return "Delayed[nanos=" + this.nanoTime + ']';
        }
    }

    public static final class DelayedTaskQueue extends ThreadSafeHeap {
        public long timeNow;

        public DelayedTaskQueue(long j) {
            this.timeNow = j;
        }
    }

    private final void closeQueue() {
        Symbol symbol;
        Symbol symbol2;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _queue$FU;
        while (true) {
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (obj == null) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = _queue$FU;
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater2, this, null, symbol)) {
                    return;
                }
            } else {
                if (obj instanceof LockFreeTaskQueueCore) {
                    ((LockFreeTaskQueueCore) obj).close();
                    return;
                }
                symbol2 = EventLoop_commonKt.CLOSED_EMPTY;
                if (obj == symbol2) {
                    return;
                }
                LockFreeTaskQueueCore lockFreeTaskQueueCore = new LockFreeTaskQueueCore(8, true);
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }");
                lockFreeTaskQueueCore.addLast((Runnable) obj);
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_queue$FU, this, obj, lockFreeTaskQueueCore)) {
                    return;
                }
            }
        }
    }

    private final Runnable dequeue() {
        Symbol symbol;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _queue$FU;
        while (true) {
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (obj == null) {
                return null;
            }
            if (obj instanceof LockFreeTaskQueueCore) {
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeTaskQueueCore<java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }>{ kotlinx.coroutines.EventLoop_commonKt.Queue<java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }> }");
                LockFreeTaskQueueCore lockFreeTaskQueueCore = (LockFreeTaskQueueCore) obj;
                Object removeFirstOrNull = lockFreeTaskQueueCore.removeFirstOrNull();
                if (removeFirstOrNull != LockFreeTaskQueueCore.REMOVE_FROZEN) {
                    return (Runnable) removeFirstOrNull;
                }
                AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_queue$FU, this, obj, lockFreeTaskQueueCore.next());
            } else {
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (obj == symbol) {
                    return null;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_queue$FU, this, obj, null)) {
                    Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }");
                    return (Runnable) obj;
                }
            }
        }
    }

    private final boolean enqueueImpl(Runnable runnable) {
        Symbol symbol;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _queue$FU;
        while (true) {
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (isCompleted()) {
                return false;
            }
            if (obj == null) {
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_queue$FU, this, null, runnable)) {
                    return true;
                }
            } else if (obj instanceof LockFreeTaskQueueCore) {
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeTaskQueueCore<java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }>{ kotlinx.coroutines.EventLoop_commonKt.Queue<java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }> }");
                LockFreeTaskQueueCore lockFreeTaskQueueCore = (LockFreeTaskQueueCore) obj;
                int addLast = lockFreeTaskQueueCore.addLast(runnable);
                if (addLast == 0) {
                    return true;
                }
                if (addLast == 1) {
                    AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_queue$FU, this, obj, lockFreeTaskQueueCore.next());
                } else if (addLast == 2) {
                    return false;
                }
            } else {
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (obj == symbol) {
                    return false;
                }
                LockFreeTaskQueueCore lockFreeTaskQueueCore2 = new LockFreeTaskQueueCore(8, true);
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type java.lang.Runnable{ kotlinx.coroutines.RunnableKt.Runnable }");
                lockFreeTaskQueueCore2.addLast((Runnable) obj);
                lockFreeTaskQueueCore2.addLast(runnable);
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_queue$FU, this, obj, lockFreeTaskQueueCore2)) {
                    return true;
                }
            }
        }
    }

    public final boolean isCompleted() {
        return _isCompleted$FU.get(this) != 0;
    }

    private final void rescheduleAllDelayed() {
        DelayedTask delayedTask;
        AbstractTimeSourceKt.getTimeSource();
        long nanoTime = System.nanoTime();
        while (true) {
            DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) _delayed$FU.get(this);
            if (delayedTaskQueue == null || (delayedTask = (DelayedTask) delayedTaskQueue.removeFirstOrNull()) == null) {
                return;
            } else {
                reschedule(nanoTime, delayedTask);
            }
        }
    }

    private final int scheduleImpl(long j, DelayedTask delayedTask) {
        if (isCompleted()) {
            return 1;
        }
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _delayed$FU;
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) atomicReferenceFieldUpdater.get(this);
        if (delayedTaskQueue == null) {
            AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, null, new DelayedTaskQueue(j));
            Object obj = atomicReferenceFieldUpdater.get(this);
            Intrinsics.checkNotNull(obj);
            delayedTaskQueue = (DelayedTaskQueue) obj;
        }
        return delayedTask.scheduleTask(j, delayedTaskQueue, this);
    }

    private final void setCompleted(boolean z) {
        _isCompleted$FU.set(this, z ? 1 : 0);
    }

    private final boolean shouldUnpark(DelayedTask delayedTask) {
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) _delayed$FU.get(this);
        return (delayedTaskQueue != null ? (DelayedTask) delayedTaskQueue.peek() : null) == delayedTask;
    }

    @Override
    public final void dispatch(CoroutineContext coroutineContext, Runnable runnable) {
        enqueue(runnable);
    }

    public void enqueue(Runnable runnable) {
        if (enqueueImpl(runnable)) {
            unpark();
        } else {
            DefaultExecutor.INSTANCE.enqueue(runnable);
        }
    }

    @Override
    protected long getNextTime() {
        DelayedTask delayedTask;
        long coerceAtLeast;
        Symbol symbol;
        if (super.getNextTime() == 0) {
            return 0L;
        }
        Object obj = _queue$FU.get(this);
        if (obj != null) {
            if (!(obj instanceof LockFreeTaskQueueCore)) {
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                return obj == symbol ? Long.MAX_VALUE : 0L;
            }
            if (!((LockFreeTaskQueueCore) obj).isEmpty()) {
                return 0L;
            }
        }
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) _delayed$FU.get(this);
        if (delayedTaskQueue == null || (delayedTask = (DelayedTask) delayedTaskQueue.peek()) == null) {
            return Long.MAX_VALUE;
        }
        long j = delayedTask.nanoTime;
        AbstractTimeSourceKt.getTimeSource();
        coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(j - System.nanoTime(), 0L);
        return coerceAtLeast;
    }

    public DisposableHandle invokeOnTimeout(long j, Runnable runnable, CoroutineContext coroutineContext) {
        return Delay.DefaultImpls.invokeOnTimeout(this, j, runnable, coroutineContext);
    }

    public boolean isEmpty() {
        Symbol symbol;
        if (!isUnconfinedQueueEmpty()) {
            return false;
        }
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) _delayed$FU.get(this);
        if (delayedTaskQueue != null && !delayedTaskQueue.isEmpty()) {
            return false;
        }
        Object obj = _queue$FU.get(this);
        if (obj != null) {
            if (obj instanceof LockFreeTaskQueueCore) {
                return ((LockFreeTaskQueueCore) obj).isEmpty();
            }
            symbol = EventLoop_commonKt.CLOSED_EMPTY;
            if (obj != symbol) {
                return false;
            }
        }
        return true;
    }

    public long processNextEvent() {
        ThreadSafeHeapNode threadSafeHeapNode;
        if (processUnconfinedEvent()) {
            return 0L;
        }
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) _delayed$FU.get(this);
        if (delayedTaskQueue != null && !delayedTaskQueue.isEmpty()) {
            AbstractTimeSourceKt.getTimeSource();
            long nanoTime = System.nanoTime();
            do {
                synchronized (delayedTaskQueue) {
                    try {
                        ThreadSafeHeapNode firstImpl = delayedTaskQueue.firstImpl();
                        threadSafeHeapNode = null;
                        if (firstImpl != null) {
                            DelayedTask delayedTask = (DelayedTask) firstImpl;
                            if (delayedTask.timeToExecute(nanoTime) ? enqueueImpl(delayedTask) : false) {
                                threadSafeHeapNode = delayedTaskQueue.removeAtImpl(0);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            } while (((DelayedTask) threadSafeHeapNode) != null);
        }
        Runnable dequeue = dequeue();
        if (dequeue == null) {
            return getNextTime();
        }
        dequeue.run();
        return 0L;
    }

    public final void resetAll() {
        _queue$FU.set(this, null);
        _delayed$FU.set(this, null);
    }

    public final void schedule(long j, DelayedTask delayedTask) {
        int scheduleImpl = scheduleImpl(j, delayedTask);
        if (scheduleImpl == 0) {
            if (shouldUnpark(delayedTask)) {
                unpark();
            }
        } else if (scheduleImpl == 1) {
            reschedule(j, delayedTask);
        } else if (scheduleImpl != 2) {
            throw new IllegalStateException("unexpected result".toString());
        }
    }

    public final DisposableHandle scheduleInvokeOnTimeout(long j, Runnable runnable) {
        long delayToNanos = EventLoop_commonKt.delayToNanos(j);
        if (delayToNanos >= 4611686018427387903L) {
            return NonDisposableHandle.INSTANCE;
        }
        AbstractTimeSourceKt.getTimeSource();
        long nanoTime = System.nanoTime();
        DelayedRunnableTask delayedRunnableTask = new DelayedRunnableTask(delayToNanos + nanoTime, runnable);
        schedule(nanoTime, delayedRunnableTask);
        return delayedRunnableTask;
    }

    @Override
    public void shutdown() {
        ThreadLocalEventLoop.INSTANCE.resetEventLoop$kotlinx_coroutines_core();
        setCompleted(true);
        closeQueue();
        do {
        } while (processNextEvent() <= 0);
        rescheduleAllDelayed();
    }
}
