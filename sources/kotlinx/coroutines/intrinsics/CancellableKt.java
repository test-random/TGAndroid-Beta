package kotlinx.coroutines.intrinsics;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.internal.DispatchedContinuationKt;

public abstract class CancellableKt {
    private static final void dispatcherFailure(Continuation continuation, Throwable th) {
        Result.Companion companion = Result.Companion;
        continuation.resumeWith(Result.m160constructorimpl(ResultKt.createFailure(th)));
        throw th;
    }

    public static final void startCoroutineCancellable(Continuation continuation, Continuation continuation2) {
        Continuation intercepted;
        try {
            intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(continuation);
            Result.Companion companion = Result.Companion;
            DispatchedContinuationKt.resumeCancellableWith$default(intercepted, Result.m160constructorimpl(Unit.INSTANCE), null, 2, null);
        } catch (Throwable th) {
            dispatcherFailure(continuation2, th);
        }
    }

    public static final void startCoroutineCancellable(Function2 function2, Object obj, Continuation continuation, Function1 function1) {
        Continuation createCoroutineUnintercepted;
        Continuation intercepted;
        try {
            createCoroutineUnintercepted = IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted(function2, obj, continuation);
            intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(createCoroutineUnintercepted);
            Result.Companion companion = Result.Companion;
            DispatchedContinuationKt.resumeCancellableWith(intercepted, Result.m160constructorimpl(Unit.INSTANCE), function1);
        } catch (Throwable th) {
            dispatcherFailure(continuation, th);
        }
    }

    public static void startCoroutineCancellable$default(Function2 function2, Object obj, Continuation continuation, Function1 function1, int i, Object obj2) {
        if ((i & 4) != 0) {
            function1 = null;
        }
        startCoroutineCancellable(function2, obj, continuation, function1);
    }
}
