package kotlin.collections;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt__SequenceBuilderKt;

public abstract class SlidingWindowKt {
    public static final void checkWindowSizeStep(int i, int i2) {
        String str;
        if (i <= 0 || i2 <= 0) {
            if (i != i2) {
                str = "Both size " + i + " and step " + i2 + " must be greater than zero.";
            } else {
                str = "size " + i + " must be greater than zero.";
            }
            throw new IllegalArgumentException(str.toString());
        }
    }

    public static final Iterator windowedIterator(Iterator iterator, int i, int i2, boolean z, boolean z2) {
        Iterator it;
        Intrinsics.checkNotNullParameter(iterator, "iterator");
        if (!iterator.hasNext()) {
            return EmptyIterator.INSTANCE;
        }
        it = SequencesKt__SequenceBuilderKt.iterator(new SlidingWindowKt$windowedIterator$1(i, i2, iterator, z2, z, null));
        return it;
    }
}
