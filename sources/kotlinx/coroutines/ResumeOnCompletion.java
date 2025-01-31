package kotlinx.coroutines;

import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

public final class ResumeOnCompletion extends JobNode {
    private final Continuation continuation;

    public ResumeOnCompletion(Continuation continuation) {
        this.continuation = continuation;
    }

    @Override
    public Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    @Override
    public void invoke(Throwable th) {
        Continuation continuation = this.continuation;
        Result.Companion companion = Result.Companion;
        continuation.resumeWith(Result.m160constructorimpl(Unit.INSTANCE));
    }
}
