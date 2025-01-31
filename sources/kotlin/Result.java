package kotlin;

import java.io.Serializable;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class Result implements Serializable {
    public static final Companion Companion = new Companion(null);
    private final Object value;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    public static final class Failure implements Serializable {
        public final Throwable exception;

        public Failure(Throwable exception) {
            Intrinsics.checkNotNullParameter(exception, "exception");
            this.exception = exception;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Failure) && Intrinsics.areEqual(this.exception, ((Failure) obj).exception);
        }

        public int hashCode() {
            return this.exception.hashCode();
        }

        public String toString() {
            return "Failure(" + this.exception + ')';
        }
    }

    private Result(Object obj) {
        this.value = obj;
    }

    public static final Result m159boximpl(Object obj) {
        return new Result(obj);
    }

    public static Object m160constructorimpl(Object obj) {
        return obj;
    }

    public static boolean m161equalsimpl(Object obj, Object obj2) {
        return (obj2 instanceof Result) && Intrinsics.areEqual(obj, ((Result) obj2).m167unboximpl());
    }

    public static final Throwable m162exceptionOrNullimpl(Object obj) {
        if (obj instanceof Failure) {
            return ((Failure) obj).exception;
        }
        return null;
    }

    public static int m163hashCodeimpl(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public static final boolean m164isFailureimpl(Object obj) {
        return obj instanceof Failure;
    }

    public static final boolean m165isSuccessimpl(Object obj) {
        return !(obj instanceof Failure);
    }

    public static String m166toStringimpl(Object obj) {
        if (obj instanceof Failure) {
            return ((Failure) obj).toString();
        }
        return "Success(" + obj + ')';
    }

    public boolean equals(Object obj) {
        return m161equalsimpl(this.value, obj);
    }

    public int hashCode() {
        return m163hashCodeimpl(this.value);
    }

    public String toString() {
        return m166toStringimpl(this.value);
    }

    public final Object m167unboximpl() {
        return this.value;
    }
}
