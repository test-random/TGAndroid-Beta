package kotlinx.coroutines.internal;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CompletedWithCancellation;
import kotlinx.coroutines.CompletionStateKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.DebugStringsKt;
import kotlinx.coroutines.DispatchedTask;
import kotlinx.coroutines.EventLoop;
import kotlinx.coroutines.ThreadLocalEventLoop;

public final class DispatchedContinuation extends DispatchedTask implements CoroutineStackFrame, Continuation {
    private static final AtomicReferenceFieldUpdater _reusableCancellableContinuation$FU = AtomicReferenceFieldUpdater.newUpdater(DispatchedContinuation.class, Object.class, "_reusableCancellableContinuation");
    private volatile Object _reusableCancellableContinuation;
    public Object _state;
    public final Continuation continuation;
    public final Object countOrElement;
    public final CoroutineDispatcher dispatcher;

    public DispatchedContinuation(CoroutineDispatcher coroutineDispatcher, Continuation continuation) {
        super(-1);
        this.dispatcher = coroutineDispatcher;
        this.continuation = continuation;
        this._state = DispatchedContinuationKt.access$getUNDEFINED$p();
        this.countOrElement = ThreadContextKt.threadContextElements(getContext());
    }

    private final CancellableContinuationImpl getReusableCancellableContinuation() {
        Object obj = _reusableCancellableContinuation$FU.get(this);
        if (obj instanceof CancellableContinuationImpl) {
            return (CancellableContinuationImpl) obj;
        }
        return null;
    }

    public final void awaitReusability$kotlinx_coroutines_core() {
        do {
        } while (_reusableCancellableContinuation$FU.get(this) == DispatchedContinuationKt.REUSABLE_CLAIMED);
    }

    @Override
    public void cancelCompletedResult$kotlinx_coroutines_core(Object obj, Throwable th) {
        if (obj instanceof CompletedWithCancellation) {
            ((CompletedWithCancellation) obj).onCancellation.invoke(th);
        }
    }

    public final CancellableContinuationImpl claimReusableCancellableContinuation$kotlinx_coroutines_core() {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _reusableCancellableContinuation$FU;
        while (true) {
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (obj == null) {
                _reusableCancellableContinuation$FU.set(this, DispatchedContinuationKt.REUSABLE_CLAIMED);
                return null;
            }
            if (obj instanceof CancellableContinuationImpl) {
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_reusableCancellableContinuation$FU, this, obj, DispatchedContinuationKt.REUSABLE_CLAIMED)) {
                    return (CancellableContinuationImpl) obj;
                }
            } else if (obj != DispatchedContinuationKt.REUSABLE_CLAIMED && !(obj instanceof Throwable)) {
                throw new IllegalStateException(("Inconsistent state " + obj).toString());
            }
        }
    }

    @Override
    public CoroutineStackFrame getCallerFrame() {
        Continuation continuation = this.continuation;
        if (continuation instanceof CoroutineStackFrame) {
            return (CoroutineStackFrame) continuation;
        }
        return null;
    }

    @Override
    public CoroutineContext getContext() {
        return this.continuation.getContext();
    }

    @Override
    public Continuation getDelegate$kotlinx_coroutines_core() {
        return this;
    }

    public final boolean isReusable$kotlinx_coroutines_core() {
        return _reusableCancellableContinuation$FU.get(this) != null;
    }

    public final boolean postponeCancellation$kotlinx_coroutines_core(Throwable th) {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _reusableCancellableContinuation$FU;
        while (true) {
            Object obj = atomicReferenceFieldUpdater.get(this);
            Symbol symbol = DispatchedContinuationKt.REUSABLE_CLAIMED;
            if (Intrinsics.areEqual(obj, symbol)) {
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_reusableCancellableContinuation$FU, this, symbol, th)) {
                    return true;
                }
            } else {
                if (obj instanceof Throwable) {
                    return true;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_reusableCancellableContinuation$FU, this, obj, null)) {
                    return false;
                }
            }
        }
    }

    public final void release$kotlinx_coroutines_core() {
        awaitReusability$kotlinx_coroutines_core();
        CancellableContinuationImpl reusableCancellableContinuation = getReusableCancellableContinuation();
        if (reusableCancellableContinuation != null) {
            reusableCancellableContinuation.detachChild$kotlinx_coroutines_core();
        }
    }

    @Override
    public void resumeWith(Object obj) {
        CoroutineContext context = this.continuation.getContext();
        Object state$default = CompletionStateKt.toState$default(obj, null, 1, null);
        if (this.dispatcher.isDispatchNeeded(context)) {
            this._state = state$default;
            this.resumeMode = 0;
            this.dispatcher.dispatch(context, this);
            return;
        }
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            this._state = state$default;
            this.resumeMode = 0;
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(this);
            return;
        }
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            CoroutineContext context2 = getContext();
            Object updateThreadContext = ThreadContextKt.updateThreadContext(context2, this.countOrElement);
            try {
                this.continuation.resumeWith(obj);
                Unit unit = Unit.INSTANCE;
                do {
                } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
            } finally {
                ThreadContextKt.restoreThreadContext(context2, updateThreadContext);
            }
        } catch (Throwable th) {
            try {
                handleFatalException$kotlinx_coroutines_core(th, null);
            } finally {
                eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
            }
        }
    }

    @Override
    public Object takeState$kotlinx_coroutines_core() {
        Object obj = this._state;
        this._state = DispatchedContinuationKt.access$getUNDEFINED$p();
        return obj;
    }

    public String toString() {
        return "DispatchedContinuation[" + this.dispatcher + ", " + DebugStringsKt.toDebugString(this.continuation) + ']';
    }

    public final Throwable tryReleaseClaimedContinuation$kotlinx_coroutines_core(CancellableContinuation cancellableContinuation) {
        Symbol symbol;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _reusableCancellableContinuation$FU;
        do {
            Object obj = atomicReferenceFieldUpdater.get(this);
            symbol = DispatchedContinuationKt.REUSABLE_CLAIMED;
            if (obj != symbol) {
                if (obj instanceof Throwable) {
                    if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_reusableCancellableContinuation$FU, this, obj, null)) {
                        return (Throwable) obj;
                    }
                    throw new IllegalArgumentException("Failed requirement.".toString());
                }
                throw new IllegalStateException(("Inconsistent state " + obj).toString());
            }
        } while (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_reusableCancellableContinuation$FU, this, symbol, cancellableContinuation));
        return null;
    }
}
