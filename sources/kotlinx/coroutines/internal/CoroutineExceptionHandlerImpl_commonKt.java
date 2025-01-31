package kotlinx.coroutines.internal;

import java.util.Iterator;
import kotlin.ExceptionsKt__ExceptionsKt;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineExceptionHandler;
import kotlinx.coroutines.CoroutineExceptionHandlerKt;

public abstract class CoroutineExceptionHandlerImpl_commonKt {
    public static final void handleUncaughtCoroutineException(CoroutineContext coroutineContext, Throwable th) {
        Iterator it = CoroutineExceptionHandlerImplKt.getPlatformExceptionHandlers().iterator();
        while (it.hasNext()) {
            try {
                ((CoroutineExceptionHandler) it.next()).handleException(coroutineContext, th);
            } catch (Throwable th2) {
                CoroutineExceptionHandlerImplKt.propagateExceptionFinalResort(CoroutineExceptionHandlerKt.handlerException(th, th2));
            }
        }
        try {
            ExceptionsKt__ExceptionsKt.addSuppressed(th, new DiagnosticCoroutineContextException(coroutineContext));
        } catch (Throwable unused) {
        }
        CoroutineExceptionHandlerImplKt.propagateExceptionFinalResort(th);
    }
}
