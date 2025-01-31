package kotlinx.coroutines;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public final class CompletedWithCancellation {
    public final Function1 onCancellation;
    public final Object result;

    public CompletedWithCancellation(Object obj, Function1 function1) {
        this.result = obj;
        this.onCancellation = function1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CompletedWithCancellation)) {
            return false;
        }
        CompletedWithCancellation completedWithCancellation = (CompletedWithCancellation) obj;
        return Intrinsics.areEqual(this.result, completedWithCancellation.result) && Intrinsics.areEqual(this.onCancellation, completedWithCancellation.onCancellation);
    }

    public int hashCode() {
        Object obj = this.result;
        return ((obj == null ? 0 : obj.hashCode()) * 31) + this.onCancellation.hashCode();
    }

    public String toString() {
        return "CompletedWithCancellation(result=" + this.result + ", onCancellation=" + this.onCancellation + ')';
    }
}
