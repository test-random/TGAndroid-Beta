package kotlinx.coroutines;

public final class CoroutinesInternalError extends Error {
    public CoroutinesInternalError(String str, Throwable th) {
        super(str, th);
    }
}
