package kotlinx.coroutines.sync;

import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReferenceImpl;

class SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1 extends FunctionReferenceImpl implements Function2 {
    public static final SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1 INSTANCE = new SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1();

    SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1() {
        super(2, SemaphoreKt.class, "createSegment", "createSegment(JLkotlinx/coroutines/sync/SemaphoreSegment;)Lkotlinx/coroutines/sync/SemaphoreSegment;", 1);
    }

    @Override
    public Object invoke(Object obj, Object obj2) {
        return invoke(((Number) obj).longValue(), (SemaphoreSegment) obj2);
    }

    public final SemaphoreSegment invoke(long j, SemaphoreSegment semaphoreSegment) {
        SemaphoreSegment createSegment;
        createSegment = SemaphoreKt.createSegment(j, semaphoreSegment);
        return createSegment;
    }
}
