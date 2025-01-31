package kotlin.sequences;

import kotlin.coroutines.Continuation;

public abstract class SequenceScope {
    public abstract Object yield(Object obj, Continuation continuation);
}
