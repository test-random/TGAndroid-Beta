package kotlinx.coroutines;

import java.io.Closeable;
import kotlin.coroutines.AbstractCoroutineContextKey;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;

public abstract class ExecutorCoroutineDispatcher extends CoroutineDispatcher implements Closeable {
    public static final Key Key = new Key(null);

    public static final class Key extends AbstractCoroutineContextKey {
        private Key() {
            super(CoroutineDispatcher.Key, new Function1() {
                @Override
                public final ExecutorCoroutineDispatcher invoke(CoroutineContext.Element element) {
                    if (element instanceof ExecutorCoroutineDispatcher) {
                        return (ExecutorCoroutineDispatcher) element;
                    }
                    return null;
                }
            });
        }

        public Key(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }
}
