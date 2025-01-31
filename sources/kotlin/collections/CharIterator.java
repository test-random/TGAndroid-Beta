package kotlin.collections;

import java.util.Iterator;

public abstract class CharIterator implements Iterator {
    @Override
    public Object next() {
        return Character.valueOf(nextChar());
    }

    public abstract char nextChar();

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
