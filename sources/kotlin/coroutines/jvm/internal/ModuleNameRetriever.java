package kotlin.coroutines.jvm.internal;

import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;

public final class ModuleNameRetriever {
    private static Cache cache;
    public static final ModuleNameRetriever INSTANCE = new ModuleNameRetriever();
    private static final Cache notOnJava9 = new Cache(null, null, null);

    public static final class Cache {
        public final Method getDescriptorMethod;
        public final Method getModuleMethod;
        public final Method nameMethod;

        public Cache(Method method, Method method2, Method method3) {
            this.getModuleMethod = method;
            this.getDescriptorMethod = method2;
            this.nameMethod = method3;
        }
    }

    private ModuleNameRetriever() {
    }

    private final Cache buildCache(BaseContinuationImpl baseContinuationImpl) {
        try {
            Cache cache2 = new Cache(Class.class.getDeclaredMethod("getModule", null), baseContinuationImpl.getClass().getClassLoader().loadClass("java.lang.Module").getDeclaredMethod("getDescriptor", null), baseContinuationImpl.getClass().getClassLoader().loadClass("java.lang.module.ModuleDescriptor").getDeclaredMethod("name", null));
            cache = cache2;
            return cache2;
        } catch (Exception unused) {
            Cache cache3 = notOnJava9;
            cache = cache3;
            return cache3;
        }
    }

    public final String getModuleName(BaseContinuationImpl continuation) {
        Intrinsics.checkNotNullParameter(continuation, "continuation");
        Cache cache2 = cache;
        if (cache2 == null) {
            cache2 = buildCache(continuation);
        }
        if (cache2 == notOnJava9) {
            return null;
        }
        Method method = cache2.getModuleMethod;
        Object invoke = method != null ? method.invoke(continuation.getClass(), null) : null;
        if (invoke == null) {
            return null;
        }
        Method method2 = cache2.getDescriptorMethod;
        Object invoke2 = method2 != null ? method2.invoke(invoke, null) : null;
        if (invoke2 == null) {
            return null;
        }
        Method method3 = cache2.nameMethod;
        Object invoke3 = method3 != null ? method3.invoke(invoke2, null) : null;
        if (invoke3 instanceof String) {
            return (String) invoke3;
        }
        return null;
    }
}
