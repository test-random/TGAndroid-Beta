package kotlinx.coroutines.sync;

import kotlin.coroutines.Continuation;

public interface Mutex {
    Object lock(Object obj, Continuation continuation);

    void unlock(Object obj);
}
