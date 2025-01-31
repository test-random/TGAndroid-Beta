package kotlin.coroutines;

import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;

public interface ContinuationInterceptor extends CoroutineContext.Element {
    public static final Key Key = Key.$$INSTANCE;

    public static final class DefaultImpls {
        public static CoroutineContext.Element get(ContinuationInterceptor continuationInterceptor, CoroutineContext.Key key) {
            Intrinsics.checkNotNullParameter(key, "key");
            if (!(key instanceof AbstractCoroutineContextKey)) {
                if (ContinuationInterceptor.Key != key) {
                    return null;
                }
                Intrinsics.checkNotNull(continuationInterceptor, "null cannot be cast to non-null type E of kotlin.coroutines.ContinuationInterceptor.get");
                return continuationInterceptor;
            }
            AbstractCoroutineContextKey abstractCoroutineContextKey = (AbstractCoroutineContextKey) key;
            if (!abstractCoroutineContextKey.isSubKey$kotlin_stdlib(continuationInterceptor.getKey())) {
                return null;
            }
            CoroutineContext.Element tryCast$kotlin_stdlib = abstractCoroutineContextKey.tryCast$kotlin_stdlib(continuationInterceptor);
            if (tryCast$kotlin_stdlib instanceof CoroutineContext.Element) {
                return tryCast$kotlin_stdlib;
            }
            return null;
        }

        public static CoroutineContext minusKey(ContinuationInterceptor continuationInterceptor, CoroutineContext.Key key) {
            Intrinsics.checkNotNullParameter(key, "key");
            if (!(key instanceof AbstractCoroutineContextKey)) {
                return ContinuationInterceptor.Key == key ? EmptyCoroutineContext.INSTANCE : continuationInterceptor;
            }
            AbstractCoroutineContextKey abstractCoroutineContextKey = (AbstractCoroutineContextKey) key;
            return (!abstractCoroutineContextKey.isSubKey$kotlin_stdlib(continuationInterceptor.getKey()) || abstractCoroutineContextKey.tryCast$kotlin_stdlib(continuationInterceptor) == null) ? continuationInterceptor : EmptyCoroutineContext.INSTANCE;
        }
    }

    public static final class Key implements CoroutineContext.Key {
        static final Key $$INSTANCE = new Key();

        private Key() {
        }
    }

    Continuation interceptContinuation(Continuation continuation);

    void releaseInterceptedContinuation(Continuation continuation);
}
