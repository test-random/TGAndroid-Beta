package kotlin.ranges;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class CharRange extends CharProgression {
    public static final Companion Companion = new Companion(null);
    private static final CharRange EMPTY = new CharRange(1, 0);

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    public CharRange(char c, char c2) {
        super(c, c2, 1);
    }

    public boolean equals(Object obj) {
        if (obj instanceof CharRange) {
            if (!isEmpty() || !((CharRange) obj).isEmpty()) {
                CharRange charRange = (CharRange) obj;
                if (getFirst() != charRange.getFirst() || getLast() != charRange.getLast()) {
                }
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        return (getFirst() * 31) + getLast();
    }

    public boolean isEmpty() {
        return Intrinsics.compare(getFirst(), getLast()) > 0;
    }

    public String toString() {
        return getFirst() + ".." + getLast();
    }
}
