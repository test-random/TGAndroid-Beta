package kotlin.ranges;

import kotlin.jvm.internal.DefaultConstructorMarker;

public final class IntRange extends IntProgression {
    public static final Companion Companion = new Companion(null);
    private static final IntRange EMPTY = new IntRange(1, 0);

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final IntRange getEMPTY() {
            return IntRange.EMPTY;
        }
    }

    public IntRange(int i, int i2) {
        super(i, i2, 1);
    }

    public boolean contains(int i) {
        return getFirst() <= i && i <= getLast();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntRange) {
            if (!isEmpty() || !((IntRange) obj).isEmpty()) {
                IntRange intRange = (IntRange) obj;
                if (getFirst() != intRange.getFirst() || getLast() != intRange.getLast()) {
                }
            }
            return true;
        }
        return false;
    }

    public Integer getEndInclusive() {
        return Integer.valueOf(getLast());
    }

    public Integer getStart() {
        return Integer.valueOf(getFirst());
    }

    @Override
    public int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        return (getFirst() * 31) + getLast();
    }

    @Override
    public boolean isEmpty() {
        return getFirst() > getLast();
    }

    @Override
    public String toString() {
        return getFirst() + ".." + getLast();
    }
}
