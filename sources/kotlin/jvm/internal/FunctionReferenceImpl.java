package kotlin.jvm.internal;

public abstract class FunctionReferenceImpl extends FunctionReference {
    public FunctionReferenceImpl(int i, Class cls, String str, String str2, int i2) {
        super(i, CallableReference.NO_RECEIVER, cls, str, str2, i2);
    }
}
