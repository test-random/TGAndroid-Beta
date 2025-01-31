package kotlinx.coroutines.internal;

public abstract class ThreadLocalKt {
    public static final ThreadLocal commonThreadLocal(Symbol symbol) {
        return new ThreadLocal();
    }
}
