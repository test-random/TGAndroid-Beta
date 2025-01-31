package kotlinx.coroutines;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;

public final class AwaitAll {
    private static final AtomicIntegerFieldUpdater notCompletedCount$FU = AtomicIntegerFieldUpdater.newUpdater(AwaitAll.class, "notCompletedCount");
    private final Deferred[] deferreds;
    private volatile int notCompletedCount;

    public final class AwaitAllNode extends JobNode {
        private static final AtomicReferenceFieldUpdater _disposer$FU = AtomicReferenceFieldUpdater.newUpdater(AwaitAllNode.class, Object.class, "_disposer");
        private volatile Object _disposer;
        private final CancellableContinuation continuation;
        public DisposableHandle handle;

        public AwaitAllNode(CancellableContinuation cancellableContinuation) {
            this.continuation = cancellableContinuation;
        }

        public final DisposeHandlersOnCancel getDisposer() {
            return (DisposeHandlersOnCancel) _disposer$FU.get(this);
        }

        public final DisposableHandle getHandle() {
            DisposableHandle disposableHandle = this.handle;
            if (disposableHandle != null) {
                return disposableHandle;
            }
            Intrinsics.throwUninitializedPropertyAccessException("handle");
            return null;
        }

        @Override
        public Object invoke(Object obj) {
            invoke((Throwable) obj);
            return Unit.INSTANCE;
        }

        @Override
        public void invoke(Throwable th) {
            if (th != null) {
                Object tryResumeWithException = this.continuation.tryResumeWithException(th);
                if (tryResumeWithException != null) {
                    this.continuation.completeResume(tryResumeWithException);
                    DisposeHandlersOnCancel disposer = getDisposer();
                    if (disposer != null) {
                        disposer.disposeAll();
                        return;
                    }
                    return;
                }
                return;
            }
            if (AwaitAll.notCompletedCount$FU.decrementAndGet(AwaitAll.this) == 0) {
                CancellableContinuation cancellableContinuation = this.continuation;
                Deferred[] deferredArr = AwaitAll.this.deferreds;
                ArrayList arrayList = new ArrayList(deferredArr.length);
                for (Deferred deferred : deferredArr) {
                    arrayList.add(deferred.getCompleted());
                }
                cancellableContinuation.resumeWith(Result.m160constructorimpl(arrayList));
            }
        }

        public final void setDisposer(DisposeHandlersOnCancel disposeHandlersOnCancel) {
            _disposer$FU.set(this, disposeHandlersOnCancel);
        }

        public final void setHandle(DisposableHandle disposableHandle) {
            this.handle = disposableHandle;
        }
    }

    public final class DisposeHandlersOnCancel extends CancelHandler {
        private final AwaitAllNode[] nodes;

        public DisposeHandlersOnCancel(AwaitAllNode[] awaitAllNodeArr) {
            this.nodes = awaitAllNodeArr;
        }

        public final void disposeAll() {
            for (AwaitAllNode awaitAllNode : this.nodes) {
                awaitAllNode.getHandle().dispose();
            }
        }

        @Override
        public Object invoke(Object obj) {
            invoke((Throwable) obj);
            return Unit.INSTANCE;
        }

        @Override
        public void invoke(Throwable th) {
            disposeAll();
        }

        public String toString() {
            return "DisposeHandlersOnCancel[" + this.nodes + ']';
        }
    }

    public AwaitAll(Deferred[] deferredArr) {
        this.deferreds = deferredArr;
        this.notCompletedCount = deferredArr.length;
    }

    public final Object await(Continuation continuation) {
        Continuation intercepted;
        Object coroutine_suspended;
        intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(continuation);
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(intercepted, 1);
        cancellableContinuationImpl.initCancellability();
        int length = this.deferreds.length;
        AwaitAllNode[] awaitAllNodeArr = new AwaitAllNode[length];
        for (int i = 0; i < length; i++) {
            Deferred deferred = this.deferreds[i];
            deferred.start();
            AwaitAllNode awaitAllNode = new AwaitAllNode(cancellableContinuationImpl);
            awaitAllNode.setHandle(deferred.invokeOnCompletion(awaitAllNode));
            Unit unit = Unit.INSTANCE;
            awaitAllNodeArr[i] = awaitAllNode;
        }
        DisposeHandlersOnCancel disposeHandlersOnCancel = new DisposeHandlersOnCancel(awaitAllNodeArr);
        for (int i2 = 0; i2 < length; i2++) {
            awaitAllNodeArr[i2].setDisposer(disposeHandlersOnCancel);
        }
        if (cancellableContinuationImpl.isCompleted()) {
            disposeHandlersOnCancel.disposeAll();
        } else {
            cancellableContinuationImpl.invokeOnCancellation(disposeHandlersOnCancel);
        }
        Object result = cancellableContinuationImpl.getResult();
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (result == coroutine_suspended) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }
}
