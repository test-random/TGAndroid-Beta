package kotlinx.coroutines.internal;

public interface ThreadSafeHeapNode {
    ThreadSafeHeap getHeap();

    int getIndex();

    void setHeap(ThreadSafeHeap threadSafeHeap);

    void setIndex(int i);
}
