package kotlin.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt___RangesKt;

public final class ArrayDeque extends AbstractMutableList {
    public static final Companion Companion = new Companion(null);
    private static final Object[] emptyElementData = new Object[0];
    private Object[] elementData = emptyElementData;
    private int head;
    private int size;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final int newCapacity$kotlin_stdlib(int i, int i2) {
            int i3 = i + (i >> 1);
            if (i3 - i2 < 0) {
                i3 = i2;
            }
            return i3 - 2147483639 > 0 ? i2 > 2147483639 ? Integer.MAX_VALUE : 2147483639 : i3;
        }
    }

    private final void copyCollectionElements(int i, Collection collection) {
        Iterator it = collection.iterator();
        int length = this.elementData.length;
        while (i < length && it.hasNext()) {
            this.elementData[i] = it.next();
            i++;
        }
        int i2 = this.head;
        for (int i3 = 0; i3 < i2 && it.hasNext(); i3++) {
            this.elementData[i3] = it.next();
        }
        this.size = size() + collection.size();
    }

    private final void copyElements(int i) {
        Object[] objArr = new Object[i];
        Object[] objArr2 = this.elementData;
        ArraysKt___ArraysJvmKt.copyInto(objArr2, objArr, 0, this.head, objArr2.length);
        Object[] objArr3 = this.elementData;
        int length = objArr3.length;
        int i2 = this.head;
        ArraysKt___ArraysJvmKt.copyInto(objArr3, objArr, length - i2, 0, i2);
        this.head = 0;
        this.elementData = objArr;
    }

    private final int decremented(int i) {
        return i == 0 ? ArraysKt___ArraysKt.getLastIndex(this.elementData) : i - 1;
    }

    private final void ensureCapacity(int i) {
        int coerceAtLeast;
        if (i < 0) {
            throw new IllegalStateException("Deque is too big.");
        }
        Object[] objArr = this.elementData;
        if (i <= objArr.length) {
            return;
        }
        if (objArr != emptyElementData) {
            copyElements(Companion.newCapacity$kotlin_stdlib(objArr.length, i));
        } else {
            coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(i, 10);
            this.elementData = new Object[coerceAtLeast];
        }
    }

    private final int incremented(int i) {
        if (i == ArraysKt___ArraysKt.getLastIndex(this.elementData)) {
            return 0;
        }
        return i + 1;
    }

    private final int negativeMod(int i) {
        return i < 0 ? i + this.elementData.length : i;
    }

    private final int positiveMod(int i) {
        Object[] objArr = this.elementData;
        return i >= objArr.length ? i - objArr.length : i;
    }

    @Override
    public void add(int i, Object obj) {
        AbstractList.Companion.checkPositionIndex$kotlin_stdlib(i, size());
        if (i == size()) {
            addLast(obj);
            return;
        }
        if (i == 0) {
            addFirst(obj);
            return;
        }
        ensureCapacity(size() + 1);
        int positiveMod = positiveMod(this.head + i);
        if (i < ((size() + 1) >> 1)) {
            int decremented = decremented(positiveMod);
            int decremented2 = decremented(this.head);
            int i2 = this.head;
            if (decremented >= i2) {
                Object[] objArr = this.elementData;
                objArr[decremented2] = objArr[i2];
                ArraysKt___ArraysJvmKt.copyInto(objArr, objArr, i2, i2 + 1, decremented + 1);
            } else {
                Object[] objArr2 = this.elementData;
                ArraysKt___ArraysJvmKt.copyInto(objArr2, objArr2, i2 - 1, i2, objArr2.length);
                Object[] objArr3 = this.elementData;
                objArr3[objArr3.length - 1] = objArr3[0];
                ArraysKt___ArraysJvmKt.copyInto(objArr3, objArr3, 0, 1, decremented + 1);
            }
            this.elementData[decremented] = obj;
            this.head = decremented2;
        } else {
            int positiveMod2 = positiveMod(this.head + size());
            Object[] objArr4 = this.elementData;
            if (positiveMod < positiveMod2) {
                ArraysKt___ArraysJvmKt.copyInto(objArr4, objArr4, positiveMod + 1, positiveMod, positiveMod2);
            } else {
                ArraysKt___ArraysJvmKt.copyInto(objArr4, objArr4, 1, 0, positiveMod2);
                Object[] objArr5 = this.elementData;
                objArr5[0] = objArr5[objArr5.length - 1];
                ArraysKt___ArraysJvmKt.copyInto(objArr5, objArr5, positiveMod + 1, positiveMod, objArr5.length - 1);
            }
            this.elementData[positiveMod] = obj;
        }
        this.size = size() + 1;
    }

    @Override
    public boolean add(Object obj) {
        addLast(obj);
        return true;
    }

    @Override
    public boolean addAll(int i, Collection elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        AbstractList.Companion.checkPositionIndex$kotlin_stdlib(i, size());
        if (elements.isEmpty()) {
            return false;
        }
        if (i == size()) {
            return addAll(elements);
        }
        ensureCapacity(size() + elements.size());
        int positiveMod = positiveMod(this.head + size());
        int positiveMod2 = positiveMod(this.head + i);
        int size = elements.size();
        if (i < ((size() + 1) >> 1)) {
            int i2 = this.head;
            int i3 = i2 - size;
            if (positiveMod2 < i2) {
                Object[] objArr = this.elementData;
                ArraysKt___ArraysJvmKt.copyInto(objArr, objArr, i3, i2, objArr.length);
                Object[] objArr2 = this.elementData;
                if (size >= positiveMod2) {
                    ArraysKt___ArraysJvmKt.copyInto(objArr2, objArr2, objArr2.length - size, 0, positiveMod2);
                } else {
                    ArraysKt___ArraysJvmKt.copyInto(objArr2, objArr2, objArr2.length - size, 0, size);
                    Object[] objArr3 = this.elementData;
                    ArraysKt___ArraysJvmKt.copyInto(objArr3, objArr3, 0, size, positiveMod2);
                }
            } else if (i3 >= 0) {
                Object[] objArr4 = this.elementData;
                ArraysKt___ArraysJvmKt.copyInto(objArr4, objArr4, i3, i2, positiveMod2);
            } else {
                Object[] objArr5 = this.elementData;
                i3 += objArr5.length;
                int i4 = positiveMod2 - i2;
                int length = objArr5.length - i3;
                if (length >= i4) {
                    ArraysKt___ArraysJvmKt.copyInto(objArr5, objArr5, i3, i2, positiveMod2);
                } else {
                    ArraysKt___ArraysJvmKt.copyInto(objArr5, objArr5, i3, i2, i2 + length);
                    Object[] objArr6 = this.elementData;
                    ArraysKt___ArraysJvmKt.copyInto(objArr6, objArr6, 0, this.head + length, positiveMod2);
                }
            }
            this.head = i3;
            copyCollectionElements(negativeMod(positiveMod2 - size), elements);
        } else {
            int i5 = positiveMod2 + size;
            if (positiveMod2 < positiveMod) {
                int i6 = size + positiveMod;
                Object[] objArr7 = this.elementData;
                if (i6 > objArr7.length) {
                    if (i5 >= objArr7.length) {
                        i5 -= objArr7.length;
                    } else {
                        int length2 = positiveMod - (i6 - objArr7.length);
                        ArraysKt___ArraysJvmKt.copyInto(objArr7, objArr7, 0, length2, positiveMod);
                        Object[] objArr8 = this.elementData;
                        ArraysKt___ArraysJvmKt.copyInto(objArr8, objArr8, i5, positiveMod2, length2);
                    }
                }
                ArraysKt___ArraysJvmKt.copyInto(objArr7, objArr7, i5, positiveMod2, positiveMod);
            } else {
                Object[] objArr9 = this.elementData;
                ArraysKt___ArraysJvmKt.copyInto(objArr9, objArr9, size, 0, positiveMod);
                Object[] objArr10 = this.elementData;
                if (i5 >= objArr10.length) {
                    ArraysKt___ArraysJvmKt.copyInto(objArr10, objArr10, i5 - objArr10.length, positiveMod2, objArr10.length);
                } else {
                    ArraysKt___ArraysJvmKt.copyInto(objArr10, objArr10, 0, objArr10.length - size, objArr10.length);
                    Object[] objArr11 = this.elementData;
                    ArraysKt___ArraysJvmKt.copyInto(objArr11, objArr11, i5, positiveMod2, objArr11.length - size);
                }
            }
            copyCollectionElements(positiveMod2, elements);
        }
        return true;
    }

    @Override
    public boolean addAll(Collection elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        if (elements.isEmpty()) {
            return false;
        }
        ensureCapacity(size() + elements.size());
        copyCollectionElements(positiveMod(this.head + size()), elements);
        return true;
    }

    public final void addFirst(Object obj) {
        ensureCapacity(size() + 1);
        int decremented = decremented(this.head);
        this.head = decremented;
        this.elementData[decremented] = obj;
        this.size = size() + 1;
    }

    public final void addLast(Object obj) {
        ensureCapacity(size() + 1);
        this.elementData[positiveMod(this.head + size())] = obj;
        this.size = size() + 1;
    }

    @Override
    public void clear() {
        int positiveMod = positiveMod(this.head + size());
        int i = this.head;
        if (i < positiveMod) {
            ArraysKt___ArraysJvmKt.fill(this.elementData, null, i, positiveMod);
        } else if (!isEmpty()) {
            Object[] objArr = this.elementData;
            ArraysKt___ArraysJvmKt.fill(objArr, null, this.head, objArr.length);
            ArraysKt___ArraysJvmKt.fill(this.elementData, null, 0, positiveMod);
        }
        this.head = 0;
        this.size = 0;
    }

    @Override
    public boolean contains(Object obj) {
        return indexOf(obj) != -1;
    }

    @Override
    public Object get(int i) {
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(i, size());
        return this.elementData[positiveMod(this.head + i)];
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int indexOf(Object obj) {
        int positiveMod = positiveMod(this.head + size());
        int i = this.head;
        if (i < positiveMod) {
            while (i < positiveMod) {
                if (!Intrinsics.areEqual(obj, this.elementData[i])) {
                    i++;
                }
            }
            return -1;
        }
        if (i < positiveMod) {
            return -1;
        }
        int length = this.elementData.length;
        while (true) {
            if (i >= length) {
                for (int i2 = 0; i2 < positiveMod; i2++) {
                    if (Intrinsics.areEqual(obj, this.elementData[i2])) {
                        i = i2 + this.elementData.length;
                    }
                }
                return -1;
            }
            if (Intrinsics.areEqual(obj, this.elementData[i])) {
                break;
            }
            i++;
        }
        return i - this.head;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int lastIndexOf(Object obj) {
        int lastIndex;
        int positiveMod = positiveMod(this.head + size());
        int i = this.head;
        if (i < positiveMod) {
            lastIndex = positiveMod - 1;
            if (i <= lastIndex) {
                while (!Intrinsics.areEqual(obj, this.elementData[lastIndex])) {
                    if (lastIndex != i) {
                        lastIndex--;
                    }
                }
                return lastIndex - this.head;
            }
            return -1;
        }
        if (i > positiveMod) {
            int i2 = positiveMod - 1;
            while (true) {
                if (-1 >= i2) {
                    lastIndex = ArraysKt___ArraysKt.getLastIndex(this.elementData);
                    int i3 = this.head;
                    if (i3 <= lastIndex) {
                        while (!Intrinsics.areEqual(obj, this.elementData[lastIndex])) {
                            if (lastIndex != i3) {
                                lastIndex--;
                            }
                        }
                    }
                } else {
                    if (Intrinsics.areEqual(obj, this.elementData[i2])) {
                        lastIndex = i2 + this.elementData.length;
                        break;
                    }
                    i2--;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean remove(Object obj) {
        int indexOf = indexOf(obj);
        if (indexOf == -1) {
            return false;
        }
        remove(indexOf);
        return true;
    }

    @Override
    public boolean removeAll(Collection elements) {
        int positiveMod;
        Intrinsics.checkNotNullParameter(elements, "elements");
        boolean z = false;
        z = false;
        z = false;
        if (!isEmpty() && this.elementData.length != 0) {
            int positiveMod2 = positiveMod(this.head + size());
            int i = this.head;
            if (i < positiveMod2) {
                positiveMod = i;
                while (i < positiveMod2) {
                    Object obj = this.elementData[i];
                    if (!elements.contains(obj)) {
                        this.elementData[positiveMod] = obj;
                        positiveMod++;
                    } else {
                        z = true;
                    }
                    i++;
                }
                ArraysKt___ArraysJvmKt.fill(this.elementData, null, positiveMod, positiveMod2);
            } else {
                int length = this.elementData.length;
                int i2 = i;
                boolean z2 = false;
                while (i < length) {
                    Object[] objArr = this.elementData;
                    Object obj2 = objArr[i];
                    objArr[i] = null;
                    if (!elements.contains(obj2)) {
                        this.elementData[i2] = obj2;
                        i2++;
                    } else {
                        z2 = true;
                    }
                    i++;
                }
                positiveMod = positiveMod(i2);
                for (int i3 = 0; i3 < positiveMod2; i3++) {
                    Object[] objArr2 = this.elementData;
                    Object obj3 = objArr2[i3];
                    objArr2[i3] = null;
                    if (!elements.contains(obj3)) {
                        this.elementData[positiveMod] = obj3;
                        positiveMod = incremented(positiveMod);
                    } else {
                        z2 = true;
                    }
                }
                z = z2;
            }
            if (z) {
                this.size = negativeMod(positiveMod - this.head);
            }
        }
        return z;
    }

    @Override
    public Object removeAt(int i) {
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(i, size());
        if (i == CollectionsKt__CollectionsKt.getLastIndex(this)) {
            return removeLast();
        }
        if (i == 0) {
            return removeFirst();
        }
        int positiveMod = positiveMod(this.head + i);
        Object obj = this.elementData[positiveMod];
        if (i < (size() >> 1)) {
            int i2 = this.head;
            if (positiveMod >= i2) {
                Object[] objArr = this.elementData;
                ArraysKt___ArraysJvmKt.copyInto(objArr, objArr, i2 + 1, i2, positiveMod);
            } else {
                Object[] objArr2 = this.elementData;
                ArraysKt___ArraysJvmKt.copyInto(objArr2, objArr2, 1, 0, positiveMod);
                Object[] objArr3 = this.elementData;
                objArr3[0] = objArr3[objArr3.length - 1];
                int i3 = this.head;
                ArraysKt___ArraysJvmKt.copyInto(objArr3, objArr3, i3 + 1, i3, objArr3.length - 1);
            }
            Object[] objArr4 = this.elementData;
            int i4 = this.head;
            objArr4[i4] = null;
            this.head = incremented(i4);
        } else {
            int positiveMod2 = positiveMod(this.head + CollectionsKt__CollectionsKt.getLastIndex(this));
            Object[] objArr5 = this.elementData;
            if (positiveMod <= positiveMod2) {
                ArraysKt___ArraysJvmKt.copyInto(objArr5, objArr5, positiveMod, positiveMod + 1, positiveMod2 + 1);
            } else {
                ArraysKt___ArraysJvmKt.copyInto(objArr5, objArr5, positiveMod, positiveMod + 1, objArr5.length);
                Object[] objArr6 = this.elementData;
                objArr6[objArr6.length - 1] = objArr6[0];
                ArraysKt___ArraysJvmKt.copyInto(objArr6, objArr6, 0, 1, positiveMod2 + 1);
            }
            this.elementData[positiveMod2] = null;
        }
        this.size = size() - 1;
        return obj;
    }

    public final Object removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArrayDeque is empty.");
        }
        Object[] objArr = this.elementData;
        int i = this.head;
        Object obj = objArr[i];
        objArr[i] = null;
        this.head = incremented(i);
        this.size = size() - 1;
        return obj;
    }

    public final Object removeFirstOrNull() {
        if (isEmpty()) {
            return null;
        }
        return removeFirst();
    }

    public final Object removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArrayDeque is empty.");
        }
        int positiveMod = positiveMod(this.head + CollectionsKt__CollectionsKt.getLastIndex(this));
        Object[] objArr = this.elementData;
        Object obj = objArr[positiveMod];
        objArr[positiveMod] = null;
        this.size = size() - 1;
        return obj;
    }

    @Override
    public boolean retainAll(Collection elements) {
        int positiveMod;
        Intrinsics.checkNotNullParameter(elements, "elements");
        boolean z = false;
        z = false;
        z = false;
        if (!isEmpty() && this.elementData.length != 0) {
            int positiveMod2 = positiveMod(this.head + size());
            int i = this.head;
            if (i < positiveMod2) {
                positiveMod = i;
                while (i < positiveMod2) {
                    Object obj = this.elementData[i];
                    if (elements.contains(obj)) {
                        this.elementData[positiveMod] = obj;
                        positiveMod++;
                    } else {
                        z = true;
                    }
                    i++;
                }
                ArraysKt___ArraysJvmKt.fill(this.elementData, null, positiveMod, positiveMod2);
            } else {
                int length = this.elementData.length;
                int i2 = i;
                boolean z2 = false;
                while (i < length) {
                    Object[] objArr = this.elementData;
                    Object obj2 = objArr[i];
                    objArr[i] = null;
                    if (elements.contains(obj2)) {
                        this.elementData[i2] = obj2;
                        i2++;
                    } else {
                        z2 = true;
                    }
                    i++;
                }
                positiveMod = positiveMod(i2);
                for (int i3 = 0; i3 < positiveMod2; i3++) {
                    Object[] objArr2 = this.elementData;
                    Object obj3 = objArr2[i3];
                    objArr2[i3] = null;
                    if (elements.contains(obj3)) {
                        this.elementData[positiveMod] = obj3;
                        positiveMod = incremented(positiveMod);
                    } else {
                        z2 = true;
                    }
                }
                z = z2;
            }
            if (z) {
                this.size = negativeMod(positiveMod - this.head);
            }
        }
        return z;
    }

    @Override
    public Object set(int i, Object obj) {
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(i, size());
        int positiveMod = positiveMod(this.head + i);
        Object[] objArr = this.elementData;
        Object obj2 = objArr[positiveMod];
        objArr[positiveMod] = obj;
        return obj2;
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @Override
    public Object[] toArray(Object[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        if (array.length < size()) {
            array = ArraysKt__ArraysJVMKt.arrayOfNulls(array, size());
        }
        int positiveMod = positiveMod(this.head + size());
        int i = this.head;
        if (i < positiveMod) {
            ArraysKt___ArraysJvmKt.copyInto$default(this.elementData, array, 0, i, positiveMod, 2, null);
        } else if (!isEmpty()) {
            Object[] objArr = this.elementData;
            ArraysKt___ArraysJvmKt.copyInto(objArr, array, 0, this.head, objArr.length);
            Object[] objArr2 = this.elementData;
            ArraysKt___ArraysJvmKt.copyInto(objArr2, array, objArr2.length - this.head, 0, positiveMod);
        }
        if (array.length > size()) {
            array[size()] = null;
        }
        return array;
    }
}
