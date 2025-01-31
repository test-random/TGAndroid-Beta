package kotlin.sequences;

import java.util.Iterator;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

public abstract class SequencesKt__SequenceBuilderKt {
    public static Iterator iterator(Function2 block) {
        Continuation createCoroutineUnintercepted;
        Intrinsics.checkNotNullParameter(block, "block");
        SequenceBuilderIterator sequenceBuilderIterator = new SequenceBuilderIterator();
        createCoroutineUnintercepted = IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted(block, sequenceBuilderIterator, sequenceBuilderIterator);
        sequenceBuilderIterator.setNextStep(createCoroutineUnintercepted);
        return sequenceBuilderIterator;
    }

    public static Sequence sequence(final Function2 block) {
        Intrinsics.checkNotNullParameter(block, "block");
        return new Sequence() {
            @Override
            public Iterator iterator() {
                return SequencesKt__SequenceBuilderKt.iterator(Function2.this);
            }
        };
    }
}
