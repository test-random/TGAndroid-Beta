package kotlinx.coroutines;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.SequenceScope;

final class JobSupport$children$1 extends RestrictedSuspendLambda implements Function2 {
    private Object L$0;
    Object L$1;
    Object L$2;
    int label;
    final JobSupport this$0;

    public JobSupport$children$1(JobSupport jobSupport, Continuation continuation) {
        super(2, continuation);
        this.this$0 = jobSupport;
    }

    @Override
    public final Continuation create(Object obj, Continuation continuation) {
        JobSupport$children$1 jobSupport$children$1 = new JobSupport$children$1(this.this$0, continuation);
        jobSupport$children$1.L$0 = obj;
        return jobSupport$children$1;
    }

    @Override
    public final Object invoke(SequenceScope sequenceScope, Continuation continuation) {
        return ((JobSupport$children$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override
    public final java.lang.Object invokeSuspend(java.lang.Object r7) {
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.JobSupport$children$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
