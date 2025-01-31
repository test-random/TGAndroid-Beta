package kotlin.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.RandomAccess;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt___RangesKt;

final class RingBuffer extends AbstractList implements RandomAccess {
    private final Object[] buffer;
    private final int capacity;
    private int size;
    private int startIndex;

    public RingBuffer(int i) {
        this(new Object[i], 0);
    }

    public RingBuffer(Object[] buffer, int i) {
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        this.buffer = buffer;
        if (i < 0) {
            throw new IllegalArgumentException(("ring buffer filled size should not be negative but it is " + i).toString());
        }
        if (i <= buffer.length) {
            this.capacity = buffer.length;
            this.size = i;
            return;
        }
        throw new IllegalArgumentException(("ring buffer filled size: " + i + " cannot be larger than the buffer size: " + buffer.length).toString());
    }

    @Override
    public final void add(Object obj) {
        if (isFull()) {
            throw new IllegalStateException("ring buffer is full");
        }
        this.buffer[(this.startIndex + size()) % this.capacity] = obj;
        this.size = size() + 1;
    }

    public final RingBuffer expanded(int i) {
        int coerceAtMost;
        Object[] array;
        int i2 = this.capacity;
        coerceAtMost = RangesKt___RangesKt.coerceAtMost(i2 + (i2 >> 1) + 1, i);
        if (this.startIndex == 0) {
            array = Arrays.copyOf(this.buffer, coerceAtMost);
            Intrinsics.checkNotNullExpressionValue(array, "copyOf(this, newSize)");
        } else {
            array = toArray(new Object[coerceAtMost]);
        }
        return new RingBuffer(array, size());
    }

    @Override
    public Object get(int i) {
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(i, size());
        return this.buffer[(this.startIndex + i) % this.capacity];
    }

    @Override
    public int getSize() {
        return this.size;
    }

    public final boolean isFull() {
        return size() == this.capacity;
    }

    @Override
    public Iterator iterator() {
        return new AbstractIterator() {
            private int count;
            private int index;

            {
                int i;
                this.count = RingBuffer.this.size();
                i = RingBuffer.this.startIndex;
                this.index = i;
            }

            @Override
            protected void computeNext() {
                Object[] objArr;
                if (this.count == 0) {
                    done();
                    return;
                }
                objArr = RingBuffer.this.buffer;
                setNext(objArr[this.index]);
                this.index = (this.index + 1) % RingBuffer.this.capacity;
                this.count--;
            }
        };
    }

    public final void removeFirst(int i) {
        if (i < 0) {
            throw new IllegalArgumentException(("n shouldn't be negative but it is " + i).toString());
        }
        if (i > size()) {
            throw new IllegalArgumentException(("n shouldn't be greater than the buffer size: n = " + i + ", size = " + size()).toString());
        }
        if (i > 0) {
            int i2 = this.startIndex;
            int i3 = (i2 + i) % this.capacity;
            Object[] objArr = this.buffer;
            if (i2 > i3) {
                ArraysKt___ArraysJvmKt.fill(objArr, null, i2, this.capacity);
                ArraysKt___ArraysJvmKt.fill(this.buffer, null, 0, i3);
            } else {
                ArraysKt___ArraysJvmKt.fill(objArr, null, i2, i3);
            }
            this.startIndex = i3;
            this.size = size() - i;
        }
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @Override
    public Object[] toArray(Object[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        if (array.length < size()) {
            array = Arrays.copyOf(array, size());
            Intrinsics.checkNotNullExpressionValue(array, "copyOf(this, newSize)");
        }
        int size = size();
        int i = 0;
        int i2 = 0;
        for (int i3 = this.startIndex; i2 < size && i3 < this.capacity; i3++) {
            array[i2] = this.buffer[i3];
            i2++;
        }
        while (i2 < size) {
            array[i2] = this.buffer[i];
            i2++;
            i++;
        }
        if (array.length > size()) {
            array[size()] = null;
        }
        return array;
    }
}
