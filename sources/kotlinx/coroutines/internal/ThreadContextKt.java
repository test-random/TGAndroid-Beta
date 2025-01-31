package kotlinx.coroutines.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.ThreadContextElement;

public abstract class ThreadContextKt {
    public static final Symbol NO_THREAD_ELEMENTS = new Symbol("NO_THREAD_ELEMENTS");
    private static final Function2 countAll = new Function2() {
        @Override
        public final Object invoke(Object obj, CoroutineContext.Element element) {
            if (!(element instanceof ThreadContextElement)) {
                return obj;
            }
            Integer num = obj instanceof Integer ? (Integer) obj : null;
            int intValue = num != null ? num.intValue() : 1;
            return intValue == 0 ? element : Integer.valueOf(intValue + 1);
        }
    };
    private static final Function2 findOne = new Function2() {
        @Override
        public final ThreadContextElement invoke(ThreadContextElement threadContextElement, CoroutineContext.Element element) {
            if (threadContextElement != null) {
                return threadContextElement;
            }
            if (element instanceof ThreadContextElement) {
                return (ThreadContextElement) element;
            }
            return null;
        }
    };
    private static final Function2 updateState = new Function2() {
        @Override
        public final ThreadState invoke(ThreadState threadState, CoroutineContext.Element element) {
            if (element instanceof ThreadContextElement) {
                ThreadContextElement threadContextElement = (ThreadContextElement) element;
                threadState.append(threadContextElement, threadContextElement.updateThreadContext(threadState.context));
            }
            return threadState;
        }
    };

    public static final void restoreThreadContext(CoroutineContext coroutineContext, Object obj) {
        if (obj == NO_THREAD_ELEMENTS) {
            return;
        }
        if (obj instanceof ThreadState) {
            ((ThreadState) obj).restore(coroutineContext);
            return;
        }
        Object fold = coroutineContext.fold(null, findOne);
        Intrinsics.checkNotNull(fold, "null cannot be cast to non-null type kotlinx.coroutines.ThreadContextElement<kotlin.Any?>");
        ((ThreadContextElement) fold).restoreThreadContext(coroutineContext, obj);
    }

    public static final Object threadContextElements(CoroutineContext coroutineContext) {
        Object fold = coroutineContext.fold(0, countAll);
        Intrinsics.checkNotNull(fold);
        return fold;
    }

    public static final Object updateThreadContext(CoroutineContext coroutineContext, Object obj) {
        if (obj == null) {
            obj = threadContextElements(coroutineContext);
        }
        if (obj == 0) {
            return NO_THREAD_ELEMENTS;
        }
        if (obj instanceof Integer) {
            return coroutineContext.fold(new ThreadState(coroutineContext, ((Number) obj).intValue()), updateState);
        }
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.ThreadContextElement<kotlin.Any?>");
        return ((ThreadContextElement) obj).updateThreadContext(coroutineContext);
    }
}
