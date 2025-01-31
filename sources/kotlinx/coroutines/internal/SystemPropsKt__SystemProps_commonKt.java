package kotlinx.coroutines.internal;

import kotlin.text.StringsKt__StringNumberConversionsKt;

public abstract class SystemPropsKt__SystemProps_commonKt {
    public static final int systemProp(String str, int i, int i2, int i3) {
        return (int) SystemPropsKt.systemProp(str, i, i2, i3);
    }

    public static final long systemProp(String str, long j, long j2, long j3) {
        Long longOrNull;
        String systemProp = SystemPropsKt.systemProp(str);
        if (systemProp == null) {
            return j;
        }
        longOrNull = StringsKt__StringNumberConversionsKt.toLongOrNull(systemProp);
        if (longOrNull == null) {
            throw new IllegalStateException(("System property '" + str + "' has unrecognized value '" + systemProp + '\'').toString());
        }
        long longValue = longOrNull.longValue();
        if (j2 <= longValue && longValue <= j3) {
            return longValue;
        }
        throw new IllegalStateException(("System property '" + str + "' should be in range " + j2 + ".." + j3 + ", but is '" + longValue + '\'').toString());
    }

    public static final String systemProp(String str, String str2) {
        String systemProp = SystemPropsKt.systemProp(str);
        return systemProp == null ? str2 : systemProp;
    }

    public static final boolean systemProp(String str, boolean z) {
        String systemProp = SystemPropsKt.systemProp(str);
        return systemProp != null ? Boolean.parseBoolean(systemProp) : z;
    }

    public static int systemProp$default(String str, int i, int i2, int i3, int i4, Object obj) {
        if ((i4 & 4) != 0) {
            i2 = 1;
        }
        if ((i4 & 8) != 0) {
            i3 = Integer.MAX_VALUE;
        }
        return SystemPropsKt.systemProp(str, i, i2, i3);
    }

    public static long systemProp$default(String str, long j, long j2, long j3, int i, Object obj) {
        if ((i & 4) != 0) {
            j2 = 1;
        }
        long j4 = j2;
        if ((i & 8) != 0) {
            j3 = Long.MAX_VALUE;
        }
        return SystemPropsKt.systemProp(str, j, j4, j3);
    }
}
