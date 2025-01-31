package kotlin.text;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt___RangesKt;
import kotlin.sequences.Sequence;

public final class DelimitedRangesSequence implements Sequence {
    private final Function2 getNextMatch;
    private final CharSequence input;
    private final int limit;
    private final int startIndex;

    public DelimitedRangesSequence(CharSequence input, int i, int i2, Function2 getNextMatch) {
        Intrinsics.checkNotNullParameter(input, "input");
        Intrinsics.checkNotNullParameter(getNextMatch, "getNextMatch");
        this.input = input;
        this.startIndex = i;
        this.limit = i2;
        this.getNextMatch = getNextMatch;
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            private int counter;
            private int currentStartIndex;
            private IntRange nextItem;
            private int nextSearchIndex;
            private int nextState = -1;

            {
                int i;
                CharSequence charSequence;
                int coerceIn;
                i = DelimitedRangesSequence.this.startIndex;
                charSequence = DelimitedRangesSequence.this.input;
                coerceIn = RangesKt___RangesKt.coerceIn(i, 0, charSequence.length());
                this.currentStartIndex = coerceIn;
                this.nextSearchIndex = coerceIn;
            }

            private final void calcNext() {
                throw new UnsupportedOperationException("Method not decompiled: kotlin.text.DelimitedRangesSequence$iterator$1.calcNext():void");
            }

            @Override
            public boolean hasNext() {
                if (this.nextState == -1) {
                    calcNext();
                }
                return this.nextState == 1;
            }

            @Override
            public IntRange next() {
                if (this.nextState == -1) {
                    calcNext();
                }
                if (this.nextState == 0) {
                    throw new NoSuchElementException();
                }
                IntRange intRange = this.nextItem;
                Intrinsics.checkNotNull(intRange, "null cannot be cast to non-null type kotlin.ranges.IntRange");
                this.nextItem = null;
                this.nextState = -1;
                return intRange;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Operation is not supported for read-only collection");
            }
        };
    }
}
