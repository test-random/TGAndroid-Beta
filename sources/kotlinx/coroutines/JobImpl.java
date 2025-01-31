package kotlinx.coroutines;

public class JobImpl extends JobSupport implements CompletableJob {
    private final boolean handlesException;

    public JobImpl(Job job) {
        super(true);
        initParentJob(job);
        this.handlesException = handlesException();
    }

    private final boolean handlesException() {
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.JobImpl.handlesException():boolean");
    }

    @Override
    public boolean getHandlesException$kotlinx_coroutines_core() {
        return this.handlesException;
    }

    @Override
    public boolean getOnCancelComplete$kotlinx_coroutines_core() {
        return true;
    }
}
