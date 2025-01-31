package kotlin.collections;

import java.util.List;

public abstract class AbstractMutableList extends java.util.AbstractList implements List {
    public abstract int getSize();

    @Override
    public final Object remove(int i) {
        return removeAt(i);
    }

    public abstract Object removeAt(int i);

    @Override
    public final int size() {
        return getSize();
    }
}
