package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.ContinuationInterceptor;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.ScopeCoroutine;
import kotlinx.coroutines.internal.ThreadContextKt;
import kotlinx.coroutines.intrinsics.CancellableKt;
import kotlinx.coroutines.intrinsics.UndispatchedKt;

public abstract class BuildersKt__Builders_commonKt {
    public static final Deferred async(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2) {
        CoroutineContext newCoroutineContext = CoroutineContextKt.newCoroutineContext(coroutineScope, coroutineContext);
        DeferredCoroutine lazyDeferredCoroutine = coroutineStart.isLazy() ? new LazyDeferredCoroutine(newCoroutineContext, function2) : new DeferredCoroutine(newCoroutineContext, true);
        lazyDeferredCoroutine.start(coroutineStart, lazyDeferredCoroutine, function2);
        return lazyDeferredCoroutine;
    }

    public static Deferred async$default(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = EmptyCoroutineContext.INSTANCE;
        }
        if ((i & 2) != 0) {
            coroutineStart = CoroutineStart.DEFAULT;
        }
        return BuildersKt.async(coroutineScope, coroutineContext, coroutineStart, function2);
    }

    public static final Job launch(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2) {
        CoroutineContext newCoroutineContext = CoroutineContextKt.newCoroutineContext(coroutineScope, coroutineContext);
        StandaloneCoroutine lazyStandaloneCoroutine = coroutineStart.isLazy() ? new LazyStandaloneCoroutine(newCoroutineContext, function2) : new StandaloneCoroutine(newCoroutineContext, true);
        lazyStandaloneCoroutine.start(coroutineStart, lazyStandaloneCoroutine, function2);
        return lazyStandaloneCoroutine;
    }

    public static Job launch$default(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = EmptyCoroutineContext.INSTANCE;
        }
        if ((i & 2) != 0) {
            coroutineStart = CoroutineStart.DEFAULT;
        }
        return BuildersKt.launch(coroutineScope, coroutineContext, coroutineStart, function2);
    }

    public static final Object withContext(CoroutineContext coroutineContext, Function2 function2, Continuation continuation) {
        Object result$kotlinx_coroutines_core;
        Object coroutine_suspended;
        CoroutineContext context = continuation.getContext();
        CoroutineContext newCoroutineContext = CoroutineContextKt.newCoroutineContext(context, coroutineContext);
        JobKt.ensureActive(newCoroutineContext);
        if (newCoroutineContext == context) {
            ScopeCoroutine scopeCoroutine = new ScopeCoroutine(newCoroutineContext, continuation);
            result$kotlinx_coroutines_core = UndispatchedKt.startUndispatchedOrReturn(scopeCoroutine, scopeCoroutine, function2);
        } else {
            ContinuationInterceptor.Key key = ContinuationInterceptor.Key;
            if (Intrinsics.areEqual(newCoroutineContext.get(key), context.get(key))) {
                UndispatchedCoroutine undispatchedCoroutine = new UndispatchedCoroutine(newCoroutineContext, continuation);
                CoroutineContext context2 = undispatchedCoroutine.getContext();
                Object updateThreadContext = ThreadContextKt.updateThreadContext(context2, null);
                try {
                    Object startUndispatchedOrReturn = UndispatchedKt.startUndispatchedOrReturn(undispatchedCoroutine, undispatchedCoroutine, function2);
                    ThreadContextKt.restoreThreadContext(context2, updateThreadContext);
                    result$kotlinx_coroutines_core = startUndispatchedOrReturn;
                } catch (Throwable th) {
                    ThreadContextKt.restoreThreadContext(context2, updateThreadContext);
                    throw th;
                }
            } else {
                DispatchedCoroutine dispatchedCoroutine = new DispatchedCoroutine(newCoroutineContext, continuation);
                CancellableKt.startCoroutineCancellable$default(function2, dispatchedCoroutine, dispatchedCoroutine, null, 4, null);
                result$kotlinx_coroutines_core = dispatchedCoroutine.getResult$kotlinx_coroutines_core();
            }
        }
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (result$kotlinx_coroutines_core == coroutine_suspended) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result$kotlinx_coroutines_core;
    }
}
