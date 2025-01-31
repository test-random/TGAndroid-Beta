package kotlinx.coroutines.internal;

public abstract class LockFreeLinkedListHead extends LockFreeLinkedListNode {
    @Override
    public boolean isRemoved() {
        return false;
    }
}
