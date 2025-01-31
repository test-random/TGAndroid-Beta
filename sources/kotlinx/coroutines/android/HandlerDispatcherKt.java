package kotlinx.coroutines.android;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;

public abstract class HandlerDispatcherKt {
    public static final HandlerDispatcher Main;
    private static volatile Choreographer choreographer;

    static {
        Object m160constructorimpl;
        try {
            Result.Companion companion = Result.Companion;
            m160constructorimpl = Result.m160constructorimpl(new HandlerContext(asHandler(Looper.getMainLooper(), true), null, 2, null));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.Companion;
            m160constructorimpl = Result.m160constructorimpl(ResultKt.createFailure(th));
        }
        Main = (HandlerDispatcher) (Result.m164isFailureimpl(m160constructorimpl) ? null : m160constructorimpl);
    }

    public static final Handler asHandler(Looper looper, boolean z) {
        Object newInstance;
        if (!z) {
            return new Handler(looper);
        }
        if (Build.VERSION.SDK_INT >= 28) {
            newInstance = Handler.class.getDeclaredMethod("createAsync", Looper.class).invoke(null, looper);
            Intrinsics.checkNotNull(newInstance, "null cannot be cast to non-null type android.os.Handler");
        } else {
            try {
                newInstance = Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, Boolean.TYPE).newInstance(looper, null, Boolean.TRUE);
            } catch (NoSuchMethodException unused) {
                return new Handler(looper);
            }
        }
        return (Handler) newInstance;
    }
}
