package kotlinx.coroutines;

import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;

public abstract class CompletionHandlerBase extends LockFreeLinkedListNode implements Function1 {
    public abstract void invoke(Throwable th);
}
