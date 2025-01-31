package kotlinx.coroutines;

import kotlin.coroutines.Continuation;

public interface Deferred extends Job {
    Object await(Continuation continuation);

    Object getCompleted();

    Throwable getCompletionExceptionOrNull();
}
