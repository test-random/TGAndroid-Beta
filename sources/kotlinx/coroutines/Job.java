package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.Sequence;

public interface Job extends CoroutineContext.Element {
    public static final Key Key = Key.$$INSTANCE;

    public static final class DefaultImpls {
        public static Object fold(Job job, Object obj, Function2 function2) {
            return CoroutineContext.Element.DefaultImpls.fold(job, obj, function2);
        }

        public static CoroutineContext.Element get(Job job, CoroutineContext.Key key) {
            return CoroutineContext.Element.DefaultImpls.get(job, key);
        }

        public static DisposableHandle invokeOnCompletion$default(Job job, boolean z, boolean z2, Function1 function1, int i, Object obj) {
            if (obj != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: invokeOnCompletion");
            }
            if ((i & 1) != 0) {
                z = false;
            }
            if ((i & 2) != 0) {
                z2 = true;
            }
            return job.invokeOnCompletion(z, z2, function1);
        }

        public static CoroutineContext minusKey(Job job, CoroutineContext.Key key) {
            return CoroutineContext.Element.DefaultImpls.minusKey(job, key);
        }

        public static CoroutineContext plus(Job job, CoroutineContext coroutineContext) {
            return CoroutineContext.Element.DefaultImpls.plus(job, coroutineContext);
        }
    }

    public static final class Key implements CoroutineContext.Key {
        static final Key $$INSTANCE = new Key();

        private Key() {
        }
    }

    ChildHandle attachChild(ChildJob childJob);

    void cancel(CancellationException cancellationException);

    CancellationException getCancellationException();

    Sequence getChildren();

    DisposableHandle invokeOnCompletion(Function1 function1);

    DisposableHandle invokeOnCompletion(boolean z, boolean z2, Function1 function1);

    boolean isActive();

    Object join(Continuation continuation);

    boolean start();
}
