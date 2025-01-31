package kotlinx.coroutines;

public final class BlockingEventLoop extends EventLoopImplBase {
    private final Thread thread;

    public BlockingEventLoop(Thread thread) {
        this.thread = thread;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }
}
