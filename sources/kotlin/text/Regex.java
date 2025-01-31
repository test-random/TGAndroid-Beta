package kotlin.text;

import java.io.Serializable;
import java.util.regex.Pattern;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class Regex implements Serializable {
    public static final Companion Companion = new Companion(null);
    private final Pattern nativePattern;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    public Regex(java.lang.String r2) {
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.Regex.<init>(java.lang.String):void");
    }

    public Regex(Pattern nativePattern) {
        Intrinsics.checkNotNullParameter(nativePattern, "nativePattern");
        this.nativePattern = nativePattern;
    }

    public final boolean matches(CharSequence input) {
        Intrinsics.checkNotNullParameter(input, "input");
        return this.nativePattern.matcher(input).matches();
    }

    public String toString() {
        String pattern = this.nativePattern.toString();
        Intrinsics.checkNotNullExpressionValue(pattern, "nativePattern.toString()");
        return pattern;
    }
}
