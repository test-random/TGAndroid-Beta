package kotlin.coroutines.intrinsics;

import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.jvm.internal.BaseContinuationImpl;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.jvm.internal.RestrictedContinuationImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;

public abstract class IntrinsicsKt__IntrinsicsJvmKt {
    public static Continuation createCoroutineUnintercepted(final Function2 function2, final Object obj, Continuation completion) {
        Intrinsics.checkNotNullParameter(function2, "<this>");
        Intrinsics.checkNotNullParameter(completion, "completion");
        final Continuation probeCoroutineCreated = DebugProbesKt.probeCoroutineCreated(completion);
        if (function2 instanceof BaseContinuationImpl) {
            return ((BaseContinuationImpl) function2).create(obj, probeCoroutineCreated);
        }
        final CoroutineContext context = probeCoroutineCreated.getContext();
        return context == EmptyCoroutineContext.INSTANCE ? new RestrictedContinuationImpl(probeCoroutineCreated, function2, obj) {
            final Object $receiver$inlined;
            final Function2 $this_createCoroutineUnintercepted$inlined;
            private int label;

            {
                super(probeCoroutineCreated);
                this.$this_createCoroutineUnintercepted$inlined = function2;
                this.$receiver$inlined = obj;
                Intrinsics.checkNotNull(probeCoroutineCreated, "null cannot be cast to non-null type kotlin.coroutines.Continuation<kotlin.Any?>");
            }

            @Override
            protected Object invokeSuspend(Object obj2) {
                int i = this.label;
                if (i == 0) {
                    this.label = 1;
                    ResultKt.throwOnFailure(obj2);
                    Intrinsics.checkNotNull(this.$this_createCoroutineUnintercepted$inlined, "null cannot be cast to non-null type kotlin.Function2<R of kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted$lambda$1, kotlin.coroutines.Continuation<T of kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted$lambda$1>, kotlin.Any?>");
                    return ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(this.$this_createCoroutineUnintercepted$inlined, 2)).invoke(this.$receiver$inlined, this);
                }
                if (i != 1) {
                    throw new IllegalStateException("This coroutine had already completed".toString());
                }
                this.label = 2;
                ResultKt.throwOnFailure(obj2);
                return obj2;
            }
        } : new ContinuationImpl(probeCoroutineCreated, context, function2, obj) {
            final Object $receiver$inlined;
            final Function2 $this_createCoroutineUnintercepted$inlined;
            private int label;

            {
                super(probeCoroutineCreated, context);
                this.$this_createCoroutineUnintercepted$inlined = function2;
                this.$receiver$inlined = obj;
                Intrinsics.checkNotNull(probeCoroutineCreated, "null cannot be cast to non-null type kotlin.coroutines.Continuation<kotlin.Any?>");
            }

            @Override
            protected Object invokeSuspend(Object obj2) {
                int i = this.label;
                if (i == 0) {
                    this.label = 1;
                    ResultKt.throwOnFailure(obj2);
                    Intrinsics.checkNotNull(this.$this_createCoroutineUnintercepted$inlined, "null cannot be cast to non-null type kotlin.Function2<R of kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted$lambda$1, kotlin.coroutines.Continuation<T of kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt.createCoroutineUnintercepted$lambda$1>, kotlin.Any?>");
                    return ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(this.$this_createCoroutineUnintercepted$inlined, 2)).invoke(this.$receiver$inlined, this);
                }
                if (i != 1) {
                    throw new IllegalStateException("This coroutine had already completed".toString());
                }
                this.label = 2;
                ResultKt.throwOnFailure(obj2);
                return obj2;
            }
        };
    }

    public static Continuation intercepted(Continuation continuation) {
        Continuation intercepted;
        Intrinsics.checkNotNullParameter(continuation, "<this>");
        ContinuationImpl continuationImpl = continuation instanceof ContinuationImpl ? (ContinuationImpl) continuation : null;
        return (continuationImpl == null || (intercepted = continuationImpl.intercepted()) == null) ? continuation : intercepted;
    }
}
