package kotlinx.coroutines.sync;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugStringsKt;
import kotlinx.coroutines.Waiter;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.Symbol;

public class MutexImpl extends SemaphoreImpl implements Mutex {
    private static final AtomicReferenceFieldUpdater owner$FU = AtomicReferenceFieldUpdater.newUpdater(MutexImpl.class, Object.class, "owner");
    private final Function3 onSelectCancellationUnlockConstructor;
    private volatile Object owner;

    public final class CancellableContinuationWithOwner implements CancellableContinuation, Waiter {
        public final CancellableContinuationImpl cont;
        public final Object owner;

        public CancellableContinuationWithOwner(CancellableContinuationImpl cancellableContinuationImpl, Object obj) {
            this.cont = cancellableContinuationImpl;
            this.owner = obj;
        }

        @Override
        public void completeResume(Object obj) {
            this.cont.completeResume(obj);
        }

        @Override
        public CoroutineContext getContext() {
            return this.cont.getContext();
        }

        @Override
        public void invokeOnCancellation(Function1 function1) {
            this.cont.invokeOnCancellation(function1);
        }

        @Override
        public void invokeOnCancellation(Segment segment, int i) {
            this.cont.invokeOnCancellation(segment, i);
        }

        @Override
        public boolean isCompleted() {
            return this.cont.isCompleted();
        }

        @Override
        public void resume(Unit unit, Function1 function1) {
            MutexImpl.owner$FU.set(MutexImpl.this, this.owner);
            CancellableContinuationImpl cancellableContinuationImpl = this.cont;
            final MutexImpl mutexImpl = MutexImpl.this;
            cancellableContinuationImpl.resume(unit, new Function1() {
                {
                    super(1);
                }

                @Override
                public Object invoke(Object obj) {
                    invoke((Throwable) obj);
                    return Unit.INSTANCE;
                }

                public final void invoke(Throwable th) {
                    MutexImpl.this.unlock(this.owner);
                }
            });
        }

        @Override
        public void resumeWith(Object obj) {
            this.cont.resumeWith(obj);
        }

        @Override
        public Object tryResume(Unit unit, Object obj, Function1 function1) {
            final MutexImpl mutexImpl = MutexImpl.this;
            Object tryResume = this.cont.tryResume(unit, obj, new Function1() {
                {
                    super(1);
                }

                @Override
                public Object invoke(Object obj2) {
                    invoke((Throwable) obj2);
                    return Unit.INSTANCE;
                }

                public final void invoke(Throwable th) {
                    MutexImpl.owner$FU.set(MutexImpl.this, this.owner);
                    MutexImpl.this.unlock(this.owner);
                }
            });
            if (tryResume != null) {
                MutexImpl.owner$FU.set(MutexImpl.this, this.owner);
            }
            return tryResume;
        }

        @Override
        public Object tryResumeWithException(Throwable th) {
            return this.cont.tryResumeWithException(th);
        }
    }

    public MutexImpl(boolean z) {
        super(1, z ? 1 : 0);
        this.owner = z ? null : MutexKt.NO_OWNER;
        this.onSelectCancellationUnlockConstructor = new Function3() {
            {
                super(3);
            }
        };
    }

    private final int holdsLockImpl(Object obj) {
        Symbol symbol;
        while (isLocked()) {
            Object obj2 = owner$FU.get(this);
            symbol = MutexKt.NO_OWNER;
            if (obj2 != symbol) {
                return obj2 == obj ? 1 : 2;
            }
        }
        return 0;
    }

    static Object lock$suspendImpl(MutexImpl mutexImpl, Object obj, Continuation continuation) {
        Object coroutine_suspended;
        if (mutexImpl.tryLock(obj)) {
            return Unit.INSTANCE;
        }
        Object lockSuspend = mutexImpl.lockSuspend(obj, continuation);
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        return lockSuspend == coroutine_suspended ? lockSuspend : Unit.INSTANCE;
    }

    private final Object lockSuspend(Object obj, Continuation continuation) {
        Continuation intercepted;
        Object coroutine_suspended;
        Object coroutine_suspended2;
        intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(continuation);
        CancellableContinuationImpl orCreateCancellableContinuation = CancellableContinuationKt.getOrCreateCancellableContinuation(intercepted);
        try {
            acquire(new CancellableContinuationWithOwner(orCreateCancellableContinuation, obj));
            Object result = orCreateCancellableContinuation.getResult();
            coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (result == coroutine_suspended) {
                DebugProbesKt.probeCoroutineSuspended(continuation);
            }
            coroutine_suspended2 = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
            return result == coroutine_suspended2 ? result : Unit.INSTANCE;
        } catch (Throwable th) {
            orCreateCancellableContinuation.releaseClaimedReusableContinuation$kotlinx_coroutines_core();
            throw th;
        }
    }

    private final int tryLockImpl(Object obj) {
        while (!tryAcquire()) {
            if (obj == null) {
                return 1;
            }
            int holdsLockImpl = holdsLockImpl(obj);
            if (holdsLockImpl == 1) {
                return 2;
            }
            if (holdsLockImpl == 2) {
                return 1;
            }
        }
        owner$FU.set(this, obj);
        return 0;
    }

    public boolean isLocked() {
        return getAvailablePermits() == 0;
    }

    @Override
    public Object lock(Object obj, Continuation continuation) {
        return lock$suspendImpl(this, obj, continuation);
    }

    public String toString() {
        return "Mutex@" + DebugStringsKt.getHexAddress(this) + "[isLocked=" + isLocked() + ",owner=" + owner$FU.get(this) + ']';
    }

    public boolean tryLock(Object obj) {
        int tryLockImpl = tryLockImpl(obj);
        if (tryLockImpl == 0) {
            return true;
        }
        if (tryLockImpl == 1) {
            return false;
        }
        if (tryLockImpl != 2) {
            throw new IllegalStateException("unexpected".toString());
        }
        throw new IllegalStateException(("This mutex is already locked by the specified owner: " + obj).toString());
    }

    @Override
    public void unlock(Object obj) {
        Symbol symbol;
        Symbol symbol2;
        while (isLocked()) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = owner$FU;
            Object obj2 = atomicReferenceFieldUpdater.get(this);
            symbol = MutexKt.NO_OWNER;
            if (obj2 != symbol) {
                if (obj2 != obj && obj != null) {
                    throw new IllegalStateException(("This mutex is locked by " + obj2 + ", but " + obj + " is expected").toString());
                }
                symbol2 = MutexKt.NO_OWNER;
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, obj2, symbol2)) {
                    release();
                    return;
                }
            }
        }
        throw new IllegalStateException("This mutex is not locked".toString());
    }
}
