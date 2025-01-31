package kotlinx.coroutines;

import java.util.Iterator;
import java.util.concurrent.CancellationException;
import kotlin.coroutines.CoroutineContext;
import kotlin.sequences.Sequence;

public abstract class JobKt__JobKt {
    public static final CompletableJob Job(Job job) {
        return new JobImpl(job);
    }

    public static CompletableJob Job$default(Job job, int i, Object obj) {
        if ((i & 1) != 0) {
            job = null;
        }
        return JobKt.Job(job);
    }

    public static final void cancel(CoroutineContext coroutineContext, CancellationException cancellationException) {
        Job job = (Job) coroutineContext.get(Job.Key);
        if (job != null) {
            job.cancel(cancellationException);
        }
    }

    public static final void cancelChildren(CoroutineContext coroutineContext, CancellationException cancellationException) {
        Sequence children;
        Job job = (Job) coroutineContext.get(Job.Key);
        if (job == null || (children = job.getChildren()) == null) {
            return;
        }
        Iterator it = children.iterator();
        while (it.hasNext()) {
            ((Job) it.next()).cancel(cancellationException);
        }
    }

    public static void cancelChildren$default(CoroutineContext coroutineContext, CancellationException cancellationException, int i, Object obj) {
        if ((i & 1) != 0) {
            cancellationException = null;
        }
        JobKt.cancelChildren(coroutineContext, cancellationException);
    }

    public static final DisposableHandle disposeOnCompletion(Job job, DisposableHandle disposableHandle) {
        return job.invokeOnCompletion(new DisposeOnCompletion(disposableHandle));
    }

    public static final void ensureActive(CoroutineContext coroutineContext) {
        Job job = (Job) coroutineContext.get(Job.Key);
        if (job != null) {
            JobKt.ensureActive(job);
        }
    }

    public static final void ensureActive(Job job) {
        if (!job.isActive()) {
            throw job.getCancellationException();
        }
    }

    public static final Job getJob(CoroutineContext coroutineContext) {
        Job job = (Job) coroutineContext.get(Job.Key);
        if (job != null) {
            return job;
        }
        throw new IllegalStateException(("Current context doesn't contain Job in it: " + coroutineContext).toString());
    }
}
