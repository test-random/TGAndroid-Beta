package kotlinx.coroutines;

import java.util.concurrent.CancellationException;

public interface ParentJob extends Job {
    CancellationException getChildJobCancellationCause();
}
