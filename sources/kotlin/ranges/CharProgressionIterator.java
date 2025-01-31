package kotlin.ranges;

import java.util.NoSuchElementException;
import kotlin.collections.CharIterator;
import kotlin.jvm.internal.Intrinsics;

public final class CharProgressionIterator extends CharIterator {
    private final int finalElement;
    private boolean hasNext;
    private int next;
    private final int step;

    public CharProgressionIterator(char c, char c2, int i) {
        this.step = i;
        this.finalElement = c2;
        boolean z = false;
        if (i <= 0 ? Intrinsics.compare(c, c2) >= 0 : Intrinsics.compare(c, c2) <= 0) {
            z = true;
        }
        this.hasNext = z;
        this.next = z ? c : c2;
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public char nextChar() {
        int i = this.next;
        if (i != this.finalElement) {
            this.next = this.step + i;
        } else {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            this.hasNext = false;
        }
        return (char) i;
    }
}
