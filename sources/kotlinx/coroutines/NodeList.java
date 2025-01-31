package kotlinx.coroutines;

import kotlinx.coroutines.internal.LockFreeLinkedListHead;

public final class NodeList extends LockFreeLinkedListHead implements Incomplete {
    @Override
    public NodeList getList() {
        return this;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
