package kotlin;

import kotlin.jvm.internal.DefaultConstructorMarker;

public abstract class UInt implements Comparable {
    public static final Companion Companion = new Companion(null);

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    public static int m168constructorimpl(int i) {
        return i;
    }
}
