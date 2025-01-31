package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.FunctionBase;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

public abstract class SuspendLambda extends ContinuationImpl implements FunctionBase {
    private final int arity;

    public SuspendLambda(int i, Continuation continuation) {
        super(continuation);
        this.arity = i;
    }

    @Override
    public int getArity() {
        return this.arity;
    }

    @Override
    public String toString() {
        if (getCompletion() != null) {
            return super.toString();
        }
        String renderLambdaToString = Reflection.renderLambdaToString(this);
        Intrinsics.checkNotNullExpressionValue(renderLambdaToString, "renderLambdaToString(this)");
        return renderLambdaToString;
    }
}
