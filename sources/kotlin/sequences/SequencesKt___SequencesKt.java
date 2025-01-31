package kotlin.sequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public abstract class SequencesKt___SequencesKt extends SequencesKt___SequencesJvmKt {
    public static Iterable asIterable(final Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return new Iterable() {
            @Override
            public Iterator iterator() {
                return Sequence.this.iterator();
            }
        };
    }

    public static final Collection toCollection(Sequence sequence, Collection destination) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Intrinsics.checkNotNullParameter(destination, "destination");
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            destination.add(it.next());
        }
        return destination;
    }

    public static List toList(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return CollectionsKt.optimizeReadOnlyList(toMutableList(sequence));
    }

    public static final List toMutableList(Sequence sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        return (List) toCollection(sequence, new ArrayList());
    }
}
