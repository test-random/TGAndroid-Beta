package kotlinx.coroutines;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;

public abstract class CompletionStateKt {
    public static final Object recoverResult(Object obj, Continuation continuation) {
        if (obj instanceof CompletedExceptionally) {
            Result.Companion companion = Result.Companion;
            obj = ResultKt.createFailure(((CompletedExceptionally) obj).cause);
        }
        return Result.m160constructorimpl(obj);
    }

    public static final Object toState(Object obj, Function1 function1) {
        Throwable m162exceptionOrNullimpl = Result.m162exceptionOrNullimpl(obj);
        return m162exceptionOrNullimpl == null ? function1 != null ? new CompletedWithCancellation(obj, function1) : obj : new CompletedExceptionally(m162exceptionOrNullimpl, false, 2, null);
    }

    public static final Object toState(Object obj, CancellableContinuation cancellableContinuation) {
        Throwable m162exceptionOrNullimpl = Result.m162exceptionOrNullimpl(obj);
        return m162exceptionOrNullimpl == null ? obj : new CompletedExceptionally(m162exceptionOrNullimpl, false, 2, null);
    }

    public static Object toState$default(Object obj, Function1 function1, int i, Object obj2) {
        if ((i & 1) != 0) {
            function1 = null;
        }
        return toState(obj, function1);
    }
}
