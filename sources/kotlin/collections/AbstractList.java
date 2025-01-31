package kotlin.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public abstract class AbstractList extends AbstractCollection implements List {
    public static final Companion Companion = new Companion(null);

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final void checkElementIndex$kotlin_stdlib(int i, int i2) {
            if (i < 0 || i >= i2) {
                throw new IndexOutOfBoundsException("index: " + i + ", size: " + i2);
            }
        }

        public final void checkPositionIndex$kotlin_stdlib(int i, int i2) {
            if (i < 0 || i > i2) {
                throw new IndexOutOfBoundsException("index: " + i + ", size: " + i2);
            }
        }

        public final void checkRangeIndexes$kotlin_stdlib(int i, int i2, int i3) {
            if (i < 0 || i2 > i3) {
                throw new IndexOutOfBoundsException("fromIndex: " + i + ", toIndex: " + i2 + ", size: " + i3);
            }
            if (i <= i2) {
                return;
            }
            throw new IllegalArgumentException("fromIndex: " + i + " > toIndex: " + i2);
        }

        public final boolean orderedEquals$kotlin_stdlib(Collection c, Collection other) {
            Intrinsics.checkNotNullParameter(c, "c");
            Intrinsics.checkNotNullParameter(other, "other");
            if (c.size() != other.size()) {
                return false;
            }
            Iterator it = other.iterator();
            Iterator it2 = c.iterator();
            while (it2.hasNext()) {
                if (!Intrinsics.areEqual(it2.next(), it.next())) {
                    return false;
                }
            }
            return true;
        }

        public final int orderedHashCode$kotlin_stdlib(Collection c) {
            Intrinsics.checkNotNullParameter(c, "c");
            Iterator it = c.iterator();
            int i = 1;
            while (it.hasNext()) {
                Object next = it.next();
                i = (i * 31) + (next != null ? next.hashCode() : 0);
            }
            return i;
        }
    }

    public class IteratorImpl implements Iterator {
        private int index;

        public IteratorImpl() {
        }

        protected final int getIndex() {
            return this.index;
        }

        @Override
        public boolean hasNext() {
            return this.index < AbstractList.this.size();
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            AbstractList abstractList = AbstractList.this;
            int i = this.index;
            this.index = i + 1;
            return abstractList.get(i);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }

        protected final void setIndex(int i) {
            this.index = i;
        }
    }

    private class ListIteratorImpl extends IteratorImpl implements ListIterator {
        public ListIteratorImpl(int i) {
            super();
            AbstractList.Companion.checkPositionIndex$kotlin_stdlib(i, AbstractList.this.size());
            setIndex(i);
        }

        @Override
        public void add(Object obj) {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }

        @Override
        public boolean hasPrevious() {
            return getIndex() > 0;
        }

        @Override
        public int nextIndex() {
            return getIndex();
        }

        @Override
        public Object previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            AbstractList abstractList = AbstractList.this;
            setIndex(getIndex() - 1);
            return abstractList.get(getIndex());
        }

        @Override
        public int previousIndex() {
            return getIndex() - 1;
        }

        @Override
        public void set(Object obj) {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }
    }

    private static final class SubList extends AbstractList implements RandomAccess {
        private int _size;
        private final int fromIndex;
        private final AbstractList list;

        public SubList(AbstractList list, int i, int i2) {
            Intrinsics.checkNotNullParameter(list, "list");
            this.list = list;
            this.fromIndex = i;
            AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(i, i2, list.size());
            this._size = i2 - i;
        }

        @Override
        public Object get(int i) {
            AbstractList.Companion.checkElementIndex$kotlin_stdlib(i, this._size);
            return this.list.get(this.fromIndex + i);
        }

        @Override
        public int getSize() {
            return this._size;
        }
    }

    @Override
    public void add(int i, Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override
    public boolean addAll(int i, Collection collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof List) {
            return Companion.orderedEquals$kotlin_stdlib(this, (Collection) obj);
        }
        return false;
    }

    @Override
    public abstract Object get(int i);

    @Override
    public int hashCode() {
        return Companion.orderedHashCode$kotlin_stdlib(this);
    }

    @Override
    public int indexOf(Object obj) {
        Iterator it = iterator();
        int i = 0;
        while (it.hasNext()) {
            if (Intrinsics.areEqual(it.next(), obj)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public Iterator iterator() {
        return new IteratorImpl();
    }

    @Override
    public int lastIndexOf(Object obj) {
        ListIterator listIterator = listIterator(size());
        while (listIterator.hasPrevious()) {
            if (Intrinsics.areEqual(listIterator.previous(), obj)) {
                return listIterator.nextIndex();
            }
        }
        return -1;
    }

    @Override
    public ListIterator listIterator() {
        return new ListIteratorImpl(0);
    }

    @Override
    public ListIterator listIterator(int i) {
        return new ListIteratorImpl(i);
    }

    @Override
    public Object remove(int i) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override
    public Object set(int i, Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override
    public List subList(int i, int i2) {
        return new SubList(this, i, i2);
    }
}
