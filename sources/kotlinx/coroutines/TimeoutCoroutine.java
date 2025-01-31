package kotlinx.coroutines;

import kotlin.coroutines.Continuation;
import kotlinx.coroutines.internal.ScopeCoroutine;

public final class TimeoutCoroutine extends ScopeCoroutine implements Runnable {
    public final long time;

    public TimeoutCoroutine(long j, Continuation continuation) {
        super(continuation.getContext(), continuation);
        this.time = j;
    }

    @Override
    public String nameString$kotlinx_coroutines_core() {
        return super.nameString$kotlinx_coroutines_core() + "(timeMillis=" + this.time + ')';
    }

    @Override
    public void run() {
        cancelCoroutine(TimeoutKt.TimeoutCancellationException(this.time, DelayKt.getDelay(getContext()), this));
    }
}
