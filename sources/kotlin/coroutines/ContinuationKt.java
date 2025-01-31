package kotlin.coroutines;

import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

public abstract class ContinuationKt {
    public static final void startCoroutine(Function2 function2, Object obj, Continuation completion) {
        Continuation createCoroutineUnintercepted;
        Continuation intercepted;
        Intrinsics.checkNotNullParameter(function2, "<this>");
        Intrinsics.checkNotNullParameter(completion, "completion");
        createCoroutineUnintercepted = IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted(function2, obj, completion);
        intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(createCoroutineUnintercepted);
        Result.Companion companion = Result.Companion;
        intercepted.resumeWith(Result.m160constructorimpl(Unit.INSTANCE));
    }
}
