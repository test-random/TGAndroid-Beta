package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.internal.ContextScope;
import kotlinx.coroutines.internal.ScopeCoroutine;
import kotlinx.coroutines.intrinsics.UndispatchedKt;

public abstract class CoroutineScopeKt {
    public static final CoroutineScope CoroutineScope(CoroutineContext coroutineContext) {
        CompletableJob Job$default;
        if (coroutineContext.get(Job.Key) == null) {
            Job$default = JobKt__JobKt.Job$default(null, 1, null);
            coroutineContext = coroutineContext.plus(Job$default);
        }
        return new ContextScope(coroutineContext);
    }

    public static final CoroutineScope MainScope() {
        return new ContextScope(SupervisorKt.SupervisorJob$default(null, 1, null).plus(Dispatchers.getMain()));
    }

    public static final Object coroutineScope(Function2 function2, Continuation continuation) {
        Object coroutine_suspended;
        ScopeCoroutine scopeCoroutine = new ScopeCoroutine(continuation.getContext(), continuation);
        Object startUndispatchedOrReturn = UndispatchedKt.startUndispatchedOrReturn(scopeCoroutine, scopeCoroutine, function2);
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (startUndispatchedOrReturn == coroutine_suspended) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return startUndispatchedOrReturn;
    }

    public static final boolean isActive(CoroutineScope coroutineScope) {
        Job job = (Job) coroutineScope.getCoroutineContext().get(Job.Key);
        if (job != null) {
            return job.isActive();
        }
        return true;
    }
}
