package kotlinx.coroutines;

public interface CompletableDeferred extends Deferred {
    boolean complete(Object obj);

    boolean completeExceptionally(Throwable th);
}
