package kotlinx.coroutines.intrinsics;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlinx.coroutines.CompletedExceptionally;
import kotlinx.coroutines.JobSupportKt;
import kotlinx.coroutines.TimeoutCancellationException;
import kotlinx.coroutines.internal.ScopeCoroutine;
import kotlinx.coroutines.internal.ThreadContextKt;

public abstract class UndispatchedKt {
    public static final void startCoroutineUndispatched(Function2 function2, Object obj, Continuation continuation) {
        Object createFailure;
        Object coroutine_suspended;
        Continuation probeCoroutineCreated = DebugProbesKt.probeCoroutineCreated(continuation);
        try {
            CoroutineContext context = continuation.getContext();
            Object updateThreadContext = ThreadContextKt.updateThreadContext(context, null);
            try {
                createFailure = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(obj, probeCoroutineCreated);
                coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
                if (createFailure == coroutine_suspended) {
                    return;
                }
            } finally {
                ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            }
        } catch (Throwable th) {
            Result.Companion companion = Result.Companion;
            createFailure = ResultKt.createFailure(th);
        }
        probeCoroutineCreated.resumeWith(Result.m160constructorimpl(createFailure));
    }

    public static final Object startUndispatchedOrReturn(ScopeCoroutine scopeCoroutine, Object obj, Function2 function2) {
        Object completedExceptionally;
        Object coroutine_suspended;
        Object makeCompletingOnce$kotlinx_coroutines_core;
        Object coroutine_suspended2;
        try {
            completedExceptionally = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(obj, scopeCoroutine);
        } catch (Throwable th) {
            completedExceptionally = new CompletedExceptionally(th, false, 2, null);
        }
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (completedExceptionally == coroutine_suspended || (makeCompletingOnce$kotlinx_coroutines_core = scopeCoroutine.makeCompletingOnce$kotlinx_coroutines_core(completedExceptionally)) == JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            coroutine_suspended2 = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
            return coroutine_suspended2;
        }
        if (makeCompletingOnce$kotlinx_coroutines_core instanceof CompletedExceptionally) {
            throw ((CompletedExceptionally) makeCompletingOnce$kotlinx_coroutines_core).cause;
        }
        return JobSupportKt.unboxState(makeCompletingOnce$kotlinx_coroutines_core);
    }

    public static final Object startUndispatchedOrReturnIgnoreTimeout(ScopeCoroutine scopeCoroutine, Object obj, Function2 function2) {
        Object completedExceptionally;
        Object coroutine_suspended;
        Object makeCompletingOnce$kotlinx_coroutines_core;
        Object coroutine_suspended2;
        try {
            completedExceptionally = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(obj, scopeCoroutine);
        } catch (Throwable th) {
            completedExceptionally = new CompletedExceptionally(th, false, 2, null);
        }
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (completedExceptionally == coroutine_suspended || (makeCompletingOnce$kotlinx_coroutines_core = scopeCoroutine.makeCompletingOnce$kotlinx_coroutines_core(completedExceptionally)) == JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            coroutine_suspended2 = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
            return coroutine_suspended2;
        }
        if (makeCompletingOnce$kotlinx_coroutines_core instanceof CompletedExceptionally) {
            Throwable th2 = ((CompletedExceptionally) makeCompletingOnce$kotlinx_coroutines_core).cause;
            if (!(th2 instanceof TimeoutCancellationException)) {
                throw th2;
            }
            if (((TimeoutCancellationException) th2).coroutine != scopeCoroutine) {
                throw th2;
            }
            if (completedExceptionally instanceof CompletedExceptionally) {
                throw ((CompletedExceptionally) completedExceptionally).cause;
            }
        } else {
            completedExceptionally = JobSupportKt.unboxState(makeCompletingOnce$kotlinx_coroutines_core);
        }
        return completedExceptionally;
    }
}
