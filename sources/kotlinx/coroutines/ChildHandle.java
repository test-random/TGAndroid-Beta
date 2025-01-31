package kotlinx.coroutines;

public interface ChildHandle extends DisposableHandle {
    boolean childCancelled(Throwable th);
}
