package kotlinx.coroutines;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.DispatchedContinuation;
import kotlinx.coroutines.internal.ThreadContextKt;

public abstract class DispatchedTaskKt {
    public static final void dispatch(DispatchedTask dispatchedTask, int i) {
        Continuation delegate$kotlinx_coroutines_core = dispatchedTask.getDelegate$kotlinx_coroutines_core();
        boolean z = i == 4;
        if (z || !(delegate$kotlinx_coroutines_core instanceof DispatchedContinuation) || isCancellableMode(i) != isCancellableMode(dispatchedTask.resumeMode)) {
            resume(dispatchedTask, delegate$kotlinx_coroutines_core, z);
            return;
        }
        CoroutineDispatcher coroutineDispatcher = ((DispatchedContinuation) delegate$kotlinx_coroutines_core).dispatcher;
        CoroutineContext context = delegate$kotlinx_coroutines_core.getContext();
        if (coroutineDispatcher.isDispatchNeeded(context)) {
            coroutineDispatcher.dispatch(context, dispatchedTask);
        } else {
            resumeUnconfined(dispatchedTask);
        }
    }

    public static final boolean isCancellableMode(int i) {
        return i == 1 || i == 2;
    }

    public static final boolean isReusableMode(int i) {
        return i == 2;
    }

    public static final void resume(DispatchedTask dispatchedTask, Continuation continuation, boolean z) {
        Object successfulResult$kotlinx_coroutines_core;
        Object takeState$kotlinx_coroutines_core = dispatchedTask.takeState$kotlinx_coroutines_core();
        Throwable exceptionalResult$kotlinx_coroutines_core = dispatchedTask.getExceptionalResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core);
        if (exceptionalResult$kotlinx_coroutines_core != null) {
            Result.Companion companion = Result.Companion;
            successfulResult$kotlinx_coroutines_core = ResultKt.createFailure(exceptionalResult$kotlinx_coroutines_core);
        } else {
            Result.Companion companion2 = Result.Companion;
            successfulResult$kotlinx_coroutines_core = dispatchedTask.getSuccessfulResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core);
        }
        Object m160constructorimpl = Result.m160constructorimpl(successfulResult$kotlinx_coroutines_core);
        if (!z) {
            continuation.resumeWith(m160constructorimpl);
            return;
        }
        Intrinsics.checkNotNull(continuation, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<T of kotlinx.coroutines.DispatchedTaskKt.resume>");
        DispatchedContinuation dispatchedContinuation = (DispatchedContinuation) continuation;
        Continuation continuation2 = dispatchedContinuation.continuation;
        Object obj = dispatchedContinuation.countOrElement;
        CoroutineContext context = continuation2.getContext();
        Object updateThreadContext = ThreadContextKt.updateThreadContext(context, obj);
        UndispatchedCoroutine updateUndispatchedCompletion = updateThreadContext != ThreadContextKt.NO_THREAD_ELEMENTS ? CoroutineContextKt.updateUndispatchedCompletion(continuation2, context, updateThreadContext) : null;
        try {
            dispatchedContinuation.continuation.resumeWith(m160constructorimpl);
            Unit unit = Unit.INSTANCE;
        } finally {
            if (updateUndispatchedCompletion == null || updateUndispatchedCompletion.clearThreadContext()) {
                ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            }
        }
    }

    private static final void resumeUnconfined(DispatchedTask dispatchedTask) {
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(dispatchedTask);
            return;
        }
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            resume(dispatchedTask, dispatchedTask.getDelegate$kotlinx_coroutines_core(), true);
            do {
            } while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent());
        } finally {
            try {
            } finally {
            }
        }
    }
}
