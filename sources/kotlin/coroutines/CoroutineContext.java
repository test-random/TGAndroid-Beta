package kotlin.coroutines;

import kotlin.coroutines.ContinuationInterceptor;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

public interface CoroutineContext {

    public static final class DefaultImpls {
        public static CoroutineContext plus(CoroutineContext coroutineContext, CoroutineContext context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return context == EmptyCoroutineContext.INSTANCE ? coroutineContext : (CoroutineContext) context.fold(coroutineContext, new Function2() {
                @Override
                public final CoroutineContext invoke(CoroutineContext acc, CoroutineContext.Element element) {
                    CombinedContext combinedContext;
                    Intrinsics.checkNotNullParameter(acc, "acc");
                    Intrinsics.checkNotNullParameter(element, "element");
                    CoroutineContext minusKey = acc.minusKey(element.getKey());
                    EmptyCoroutineContext emptyCoroutineContext = EmptyCoroutineContext.INSTANCE;
                    if (minusKey == emptyCoroutineContext) {
                        return element;
                    }
                    ContinuationInterceptor.Key key = ContinuationInterceptor.Key;
                    ContinuationInterceptor continuationInterceptor = (ContinuationInterceptor) minusKey.get(key);
                    if (continuationInterceptor == null) {
                        combinedContext = new CombinedContext(minusKey, element);
                    } else {
                        CoroutineContext minusKey2 = minusKey.minusKey(key);
                        if (minusKey2 == emptyCoroutineContext) {
                            return new CombinedContext(element, continuationInterceptor);
                        }
                        combinedContext = new CombinedContext(new CombinedContext(minusKey2, element), continuationInterceptor);
                    }
                    return combinedContext;
                }
            });
        }
    }

    public interface Element extends CoroutineContext {

        public static final class DefaultImpls {
            public static Object fold(Element element, Object obj, Function2 operation) {
                Intrinsics.checkNotNullParameter(operation, "operation");
                return operation.invoke(obj, element);
            }

            public static Element get(Element element, Key key) {
                Intrinsics.checkNotNullParameter(key, "key");
                if (!Intrinsics.areEqual(element.getKey(), key)) {
                    return null;
                }
                Intrinsics.checkNotNull(element, "null cannot be cast to non-null type E of kotlin.coroutines.CoroutineContext.Element.get");
                return element;
            }

            public static CoroutineContext minusKey(Element element, Key key) {
                Intrinsics.checkNotNullParameter(key, "key");
                return Intrinsics.areEqual(element.getKey(), key) ? EmptyCoroutineContext.INSTANCE : element;
            }

            public static CoroutineContext plus(Element element, CoroutineContext context) {
                Intrinsics.checkNotNullParameter(context, "context");
                return DefaultImpls.plus(element, context);
            }
        }

        @Override
        Element get(Key key);

        Key getKey();
    }

    public interface Key {
    }

    Object fold(Object obj, Function2 function2);

    Element get(Key key);

    CoroutineContext minusKey(Key key);

    CoroutineContext plus(CoroutineContext coroutineContext);
}
