package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function2;

public abstract class SequencesKt extends SequencesKt___SequencesKt {
    public static Iterable asIterable(Sequence sequence) {
        return SequencesKt___SequencesKt.asIterable(sequence);
    }

    public static Iterator iterator(Function2 function2) {
        return SequencesKt__SequenceBuilderKt.iterator(function2);
    }
}
