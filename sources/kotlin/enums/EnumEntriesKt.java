package kotlin.enums;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public abstract class EnumEntriesKt {
    public static final EnumEntries enumEntries(final Enum[] entries) {
        Intrinsics.checkNotNullParameter(entries, "entries");
        EnumEntriesList enumEntriesList = new EnumEntriesList(new Function0() {
            {
                super(0);
            }

            @Override
            public final Enum[] invoke() {
                return entries;
            }
        });
        enumEntriesList.size();
        return enumEntriesList;
    }
}
