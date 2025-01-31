package kotlinx.coroutines.internal;

public final class Removed {
    public final LockFreeLinkedListNode ref;

    public Removed(LockFreeLinkedListNode lockFreeLinkedListNode) {
        this.ref = lockFreeLinkedListNode;
    }

    public String toString() {
        return "Removed[" + this.ref + ']';
    }
}
