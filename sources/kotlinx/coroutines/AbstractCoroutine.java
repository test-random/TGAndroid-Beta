package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;

public abstract class AbstractCoroutine extends JobSupport implements Job, Continuation, CoroutineScope {
    private final CoroutineContext context;

    public AbstractCoroutine(CoroutineContext coroutineContext, boolean z, boolean z2) {
        super(z2);
        if (z) {
            initParentJob((Job) coroutineContext.get(Job.Key));
        }
        this.context = coroutineContext.plus(this);
    }

    protected void afterResume(Object obj) {
        afterCompletion(obj);
    }

    @Override
    public String cancellationExceptionMessage() {
        return DebugStringsKt.getClassSimpleName(this) + " was cancelled";
    }

    @Override
    public final CoroutineContext getContext() {
        return this.context;
    }

    @Override
    public CoroutineContext getCoroutineContext() {
        return this.context;
    }

    @Override
    public final void handleOnCompletionException$kotlinx_coroutines_core(Throwable th) {
        CoroutineExceptionHandlerKt.handleCoroutineException(this.context, th);
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public String nameString$kotlinx_coroutines_core() {
        String coroutineName = CoroutineContextKt.getCoroutineName(this.context);
        if (coroutineName == null) {
            return super.nameString$kotlinx_coroutines_core();
        }
        return '\"' + coroutineName + "\":" + super.nameString$kotlinx_coroutines_core();
    }

    protected void onCancelled(Throwable th, boolean z) {
    }

    protected void onCompleted(Object obj) {
    }

    @Override
    protected final void onCompletionInternal(Object obj) {
        if (!(obj instanceof CompletedExceptionally)) {
            onCompleted(obj);
        } else {
            CompletedExceptionally completedExceptionally = (CompletedExceptionally) obj;
            onCancelled(completedExceptionally.cause, completedExceptionally.getHandled());
        }
    }

    @Override
    public final void resumeWith(Object obj) {
        Object makeCompletingOnce$kotlinx_coroutines_core = makeCompletingOnce$kotlinx_coroutines_core(CompletionStateKt.toState$default(obj, null, 1, null));
        if (makeCompletingOnce$kotlinx_coroutines_core == JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            return;
        }
        afterResume(makeCompletingOnce$kotlinx_coroutines_core);
    }

    public final void start(CoroutineStart coroutineStart, Object obj, Function2 function2) {
        coroutineStart.invoke(function2, obj, this);
    }
}
