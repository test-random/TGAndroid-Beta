package kotlinx.coroutines;

import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.ThreadLocalKt;

public final class ThreadLocalEventLoop {
    public static final ThreadLocalEventLoop INSTANCE = new ThreadLocalEventLoop();
    private static final ThreadLocal ref = ThreadLocalKt.commonThreadLocal(new Symbol("ThreadLocalEventLoop"));

    private ThreadLocalEventLoop() {
    }

    public final EventLoop getEventLoop$kotlinx_coroutines_core() {
        ThreadLocal threadLocal = ref;
        EventLoop eventLoop = (EventLoop) threadLocal.get();
        if (eventLoop != null) {
            return eventLoop;
        }
        EventLoop createEventLoop = EventLoopKt.createEventLoop();
        threadLocal.set(createEventLoop);
        return createEventLoop;
    }

    public final void resetEventLoop$kotlinx_coroutines_core() {
        ref.set(null);
    }

    public final void setEventLoop$kotlinx_coroutines_core(EventLoop eventLoop) {
        ref.set(eventLoop);
    }
}
