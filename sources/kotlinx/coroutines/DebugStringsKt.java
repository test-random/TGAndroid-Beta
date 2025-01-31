package kotlinx.coroutines;

import kotlin.Result;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.internal.DispatchedContinuation;

public abstract class DebugStringsKt {
    public static final String getClassSimpleName(Object obj) {
        return obj.getClass().getSimpleName();
    }

    public static final String getHexAddress(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    public static final String toDebugString(Continuation continuation) {
        Object m160constructorimpl;
        if (continuation instanceof DispatchedContinuation) {
            return continuation.toString();
        }
        try {
            Result.Companion companion = Result.Companion;
            m160constructorimpl = Result.m160constructorimpl(continuation + '@' + getHexAddress(continuation));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.Companion;
            m160constructorimpl = Result.m160constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m162exceptionOrNullimpl(m160constructorimpl) != null) {
            m160constructorimpl = continuation.getClass().getName() + '@' + getHexAddress(continuation);
        }
        return (String) m160constructorimpl;
    }
}
