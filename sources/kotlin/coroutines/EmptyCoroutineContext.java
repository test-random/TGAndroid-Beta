package kotlin.coroutines;

import java.io.Serializable;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

public final class EmptyCoroutineContext implements CoroutineContext, Serializable {
    public static final EmptyCoroutineContext INSTANCE = new EmptyCoroutineContext();

    private EmptyCoroutineContext() {
    }

    @Override
    public Object fold(Object obj, Function2 operation) {
        Intrinsics.checkNotNullParameter(operation, "operation");
        return obj;
    }

    @Override
    public CoroutineContext.Element get(CoroutineContext.Key key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return null;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    public CoroutineContext minusKey(CoroutineContext.Key key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return this;
    }

    @Override
    public CoroutineContext plus(CoroutineContext context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return context;
    }

    public String toString() {
        return "EmptyCoroutineContext";
    }
}
