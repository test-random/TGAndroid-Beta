package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.ThreadContextElement;

public final class ThreadState {
    public final CoroutineContext context;
    private final ThreadContextElement[] elements;
    private int i;
    private final Object[] values;

    public ThreadState(CoroutineContext coroutineContext, int i) {
        this.context = coroutineContext;
        this.values = new Object[i];
        this.elements = new ThreadContextElement[i];
    }

    public final void append(ThreadContextElement threadContextElement, Object obj) {
        Object[] objArr = this.values;
        int i = this.i;
        objArr[i] = obj;
        ThreadContextElement[] threadContextElementArr = this.elements;
        this.i = i + 1;
        Intrinsics.checkNotNull(threadContextElement, "null cannot be cast to non-null type kotlinx.coroutines.ThreadContextElement<kotlin.Any?>");
        threadContextElementArr[i] = threadContextElement;
    }

    public final void restore(CoroutineContext coroutineContext) {
        int length = this.elements.length - 1;
        if (length < 0) {
            return;
        }
        while (true) {
            int i = length - 1;
            ThreadContextElement threadContextElement = this.elements[length];
            Intrinsics.checkNotNull(threadContextElement);
            threadContextElement.restoreThreadContext(coroutineContext, this.values[length]);
            if (i < 0) {
                return;
            } else {
                length = i;
            }
        }
    }
}
