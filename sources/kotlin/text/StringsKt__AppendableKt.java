package kotlin.text;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public abstract class StringsKt__AppendableKt {
    public static void appendElement(Appendable appendable, Object obj, Function1 function1) {
        CharSequence valueOf;
        Intrinsics.checkNotNullParameter(appendable, "<this>");
        if (function1 != null) {
            obj = function1.invoke(obj);
        } else {
            if (!(obj == null ? true : obj instanceof CharSequence)) {
                if (obj instanceof Character) {
                    appendable.append(((Character) obj).charValue());
                    return;
                } else {
                    valueOf = String.valueOf(obj);
                    appendable.append(valueOf);
                }
            }
        }
        valueOf = (CharSequence) obj;
        appendable.append(valueOf);
    }
}
