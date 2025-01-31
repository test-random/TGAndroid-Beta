package kotlin.enums;

import java.io.Serializable;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt___ArraysKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public final class EnumEntriesList extends AbstractList implements EnumEntries, Serializable {
    private volatile Enum[] _entries;
    private final Function0 entriesProvider;

    public EnumEntriesList(Function0 entriesProvider) {
        Intrinsics.checkNotNullParameter(entriesProvider, "entriesProvider");
        this.entriesProvider = entriesProvider;
    }

    private final Enum[] getEntries() {
        Enum[] enumArr = this._entries;
        if (enumArr != null) {
            return enumArr;
        }
        Enum[] enumArr2 = (Enum[]) this.entriesProvider.invoke();
        this._entries = enumArr2;
        return enumArr2;
    }

    public boolean contains(Enum element) {
        Object orNull;
        Intrinsics.checkNotNullParameter(element, "element");
        orNull = ArraysKt___ArraysKt.getOrNull(getEntries(), element.ordinal());
        return ((Enum) orNull) == element;
    }

    @Override
    public final boolean contains(Object obj) {
        if (obj instanceof Enum) {
            return contains((Enum) obj);
        }
        return false;
    }

    @Override
    public Enum get(int i) {
        Enum[] entries = getEntries();
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(i, entries.length);
        return entries[i];
    }

    @Override
    public int getSize() {
        return getEntries().length;
    }

    public int indexOf(Enum element) {
        Object orNull;
        Intrinsics.checkNotNullParameter(element, "element");
        int ordinal = element.ordinal();
        orNull = ArraysKt___ArraysKt.getOrNull(getEntries(), ordinal);
        if (((Enum) orNull) == element) {
            return ordinal;
        }
        return -1;
    }

    @Override
    public final int indexOf(Object obj) {
        if (obj instanceof Enum) {
            return indexOf((Enum) obj);
        }
        return -1;
    }

    public int lastIndexOf(Enum element) {
        Intrinsics.checkNotNullParameter(element, "element");
        return indexOf((Object) element);
    }

    @Override
    public final int lastIndexOf(Object obj) {
        if (obj instanceof Enum) {
            return lastIndexOf((Enum) obj);
        }
        return -1;
    }
}
