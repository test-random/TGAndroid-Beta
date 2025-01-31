package kotlinx.coroutines;

import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.internal.ScopeCoroutine;
import kotlinx.coroutines.internal.ThreadContextKt;

public final class UndispatchedCoroutine extends ScopeCoroutine {
    private volatile boolean threadLocalIsSet;
    private final ThreadLocal threadStateToRecover;

    public UndispatchedCoroutine(kotlin.coroutines.CoroutineContext r3, kotlin.coroutines.Continuation r4) {
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.UndispatchedCoroutine.<init>(kotlin.coroutines.CoroutineContext, kotlin.coroutines.Continuation):void");
    }

    @Override
    protected void afterResume(Object obj) {
        if (this.threadLocalIsSet) {
            Pair pair = (Pair) this.threadStateToRecover.get();
            if (pair != null) {
                ThreadContextKt.restoreThreadContext((CoroutineContext) pair.component1(), pair.component2());
            }
            this.threadStateToRecover.remove();
        }
        Object recoverResult = CompletionStateKt.recoverResult(obj, this.uCont);
        Continuation continuation = this.uCont;
        CoroutineContext context = continuation.getContext();
        Object updateThreadContext = ThreadContextKt.updateThreadContext(context, null);
        UndispatchedCoroutine updateUndispatchedCompletion = updateThreadContext != ThreadContextKt.NO_THREAD_ELEMENTS ? CoroutineContextKt.updateUndispatchedCompletion(continuation, context, updateThreadContext) : null;
        try {
            this.uCont.resumeWith(recoverResult);
            Unit unit = Unit.INSTANCE;
        } finally {
            if (updateUndispatchedCompletion == null || updateUndispatchedCompletion.clearThreadContext()) {
                ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            }
        }
    }

    public final boolean clearThreadContext() {
        boolean z = this.threadLocalIsSet && this.threadStateToRecover.get() == null;
        this.threadStateToRecover.remove();
        return !z;
    }

    public final void saveThreadContext(CoroutineContext coroutineContext, Object obj) {
        this.threadLocalIsSet = true;
        this.threadStateToRecover.set(TuplesKt.to(coroutineContext, obj));
    }
}
