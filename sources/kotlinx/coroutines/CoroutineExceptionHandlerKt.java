package kotlinx.coroutines;

import kotlin.ExceptionsKt__ExceptionsKt;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.internal.CoroutineExceptionHandlerImpl_commonKt;

public abstract class CoroutineExceptionHandlerKt {
    public static final void handleCoroutineException(CoroutineContext coroutineContext, Throwable th) {
        try {
            CoroutineExceptionHandler coroutineExceptionHandler = (CoroutineExceptionHandler) coroutineContext.get(CoroutineExceptionHandler.Key);
            if (coroutineExceptionHandler != null) {
                coroutineExceptionHandler.handleException(coroutineContext, th);
            } else {
                CoroutineExceptionHandlerImpl_commonKt.handleUncaughtCoroutineException(coroutineContext, th);
            }
        } catch (Throwable th2) {
            CoroutineExceptionHandlerImpl_commonKt.handleUncaughtCoroutineException(coroutineContext, handlerException(th, th2));
        }
    }

    public static final Throwable handlerException(Throwable th, Throwable th2) {
        if (th == th2) {
            return th;
        }
        RuntimeException runtimeException = new RuntimeException("Exception while trying to handle coroutine exception", th2);
        ExceptionsKt__ExceptionsKt.addSuppressed(runtimeException, th);
        return runtimeException;
    }
}
