package kotlinx.coroutines;

import kotlinx.coroutines.internal.Segment;

public interface Waiter {
    void invokeOnCancellation(Segment segment, int i);
}
