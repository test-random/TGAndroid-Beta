package kotlinx.coroutines.sync;

import kotlinx.coroutines.internal.Symbol;

public abstract class MutexKt {
    private static final Symbol NO_OWNER = new Symbol("NO_OWNER");
    private static final Symbol ON_LOCK_ALREADY_LOCKED_BY_OWNER = new Symbol("ALREADY_LOCKED_BY_OWNER");

    public static final Mutex Mutex(boolean z) {
        return new MutexImpl(z);
    }

    public static Mutex Mutex$default(boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        return Mutex(z);
    }
}
