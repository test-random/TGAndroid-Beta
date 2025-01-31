package kotlin.ranges;

import kotlin.collections.CharIterator;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.DefaultConstructorMarker;

public abstract class CharProgression implements Iterable {
    public static final Companion Companion = new Companion(null);
    private final char first;
    private final char last;
    private final int step;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    public CharProgression(char c, char c2, int i) {
        if (i == 0) {
            throw new IllegalArgumentException("Step must be non-zero.");
        }
        if (i == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
        }
        this.first = c;
        this.last = (char) ProgressionUtilKt.getProgressionLastElement(c, c2, i);
        this.step = i;
    }

    public final char getFirst() {
        return this.first;
    }

    public final char getLast() {
        return this.last;
    }

    @Override
    public CharIterator iterator() {
        return new CharProgressionIterator(this.first, this.last, this.step);
    }
}
