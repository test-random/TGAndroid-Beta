package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.ContinuationInterceptor;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref$ObjectRef;

public abstract class CoroutineContextKt {
    private static final CoroutineContext foldCopies(CoroutineContext coroutineContext, CoroutineContext coroutineContext2, final boolean z) {
        boolean hasCopyableElements = hasCopyableElements(coroutineContext);
        boolean hasCopyableElements2 = hasCopyableElements(coroutineContext2);
        if (!hasCopyableElements && !hasCopyableElements2) {
            return coroutineContext.plus(coroutineContext2);
        }
        final Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        ref$ObjectRef.element = coroutineContext2;
        EmptyCoroutineContext emptyCoroutineContext = EmptyCoroutineContext.INSTANCE;
        CoroutineContext coroutineContext3 = (CoroutineContext) coroutineContext.fold(emptyCoroutineContext, new Function2() {
            {
                super(2);
            }

            @Override
            public final CoroutineContext invoke(CoroutineContext coroutineContext4, CoroutineContext.Element element) {
                return coroutineContext4.plus(element);
            }
        });
        if (hasCopyableElements2) {
            ref$ObjectRef.element = ((CoroutineContext) ref$ObjectRef.element).fold(emptyCoroutineContext, new Function2() {
                @Override
                public final CoroutineContext invoke(CoroutineContext coroutineContext4, CoroutineContext.Element element) {
                    return coroutineContext4.plus(element);
                }
            });
        }
        return coroutineContext3.plus((CoroutineContext) ref$ObjectRef.element);
    }

    public static final String getCoroutineName(CoroutineContext coroutineContext) {
        return null;
    }

    private static final boolean hasCopyableElements(CoroutineContext coroutineContext) {
        return ((Boolean) coroutineContext.fold(Boolean.FALSE, new Function2() {
            public final Boolean invoke(boolean z, CoroutineContext.Element element) {
                return Boolean.valueOf(z);
            }

            @Override
            public Object invoke(Object obj, Object obj2) {
                return invoke(((Boolean) obj).booleanValue(), (CoroutineContext.Element) obj2);
            }
        })).booleanValue();
    }

    public static final CoroutineContext newCoroutineContext(CoroutineContext coroutineContext, CoroutineContext coroutineContext2) {
        return !hasCopyableElements(coroutineContext2) ? coroutineContext.plus(coroutineContext2) : foldCopies(coroutineContext, coroutineContext2, false);
    }

    public static final CoroutineContext newCoroutineContext(CoroutineScope coroutineScope, CoroutineContext coroutineContext) {
        CoroutineContext foldCopies = foldCopies(coroutineScope.getCoroutineContext(), coroutineContext, true);
        return (foldCopies == Dispatchers.getDefault() || foldCopies.get(ContinuationInterceptor.Key) != null) ? foldCopies : foldCopies.plus(Dispatchers.getDefault());
    }

    public static final UndispatchedCoroutine undispatchedCompletion(CoroutineStackFrame coroutineStackFrame) {
        while (!(coroutineStackFrame instanceof DispatchedCoroutine) && (coroutineStackFrame = coroutineStackFrame.getCallerFrame()) != null) {
            if (coroutineStackFrame instanceof UndispatchedCoroutine) {
                return (UndispatchedCoroutine) coroutineStackFrame;
            }
        }
        return null;
    }

    public static final UndispatchedCoroutine updateUndispatchedCompletion(Continuation continuation, CoroutineContext coroutineContext, Object obj) {
        if (!(continuation instanceof CoroutineStackFrame) || coroutineContext.get(UndispatchedMarker.INSTANCE) == null) {
            return null;
        }
        UndispatchedCoroutine undispatchedCompletion = undispatchedCompletion((CoroutineStackFrame) continuation);
        if (undispatchedCompletion != null) {
            undispatchedCompletion.saveThreadContext(coroutineContext, obj);
        }
        return undispatchedCompletion;
    }
}
