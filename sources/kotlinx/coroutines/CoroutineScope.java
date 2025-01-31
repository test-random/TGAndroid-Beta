package kotlinx.coroutines;

import kotlin.coroutines.CoroutineContext;

public interface CoroutineScope {
    CoroutineContext getCoroutineContext();
}
