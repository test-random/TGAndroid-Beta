package kotlinx.coroutines;

import java.util.concurrent.CancellationException;

public final class TimeoutCancellationException extends CancellationException {
    public final transient Job coroutine;

    public TimeoutCancellationException(String str) {
        this(str, null);
    }

    public TimeoutCancellationException(String str, Job job) {
        super(str);
        this.coroutine = job;
    }
}
