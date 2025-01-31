package kotlin.coroutines.jvm.internal;

public interface CoroutineStackFrame {
    CoroutineStackFrame getCallerFrame();
}
