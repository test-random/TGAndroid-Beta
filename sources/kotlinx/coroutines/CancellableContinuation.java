package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;

public interface CancellableContinuation extends Continuation {
    void completeResume(Object obj);

    void invokeOnCancellation(Function1 function1);

    boolean isCompleted();

    void resume(Object obj, Function1 function1);

    Object tryResume(Object obj, Object obj2, Function1 function1);

    Object tryResumeWithException(Throwable th);
}
