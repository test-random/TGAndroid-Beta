package kotlinx.coroutines;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.ExceptionsKt__ExceptionsKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsJvmKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt__IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt__SequenceBuilderKt;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.internal.LockFreeLinkedListKt;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;
import kotlinx.coroutines.internal.OpDescriptor;
import kotlinx.coroutines.internal.Symbol;

public class JobSupport implements Job, ChildJob, ParentJob {
    private volatile Object _parentHandle;
    private volatile Object _state;
    private static final AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(JobSupport.class, Object.class, "_state");
    private static final AtomicReferenceFieldUpdater _parentHandle$FU = AtomicReferenceFieldUpdater.newUpdater(JobSupport.class, Object.class, "_parentHandle");

    public static final class AwaitContinuation extends CancellableContinuationImpl {
        private final JobSupport job;

        public AwaitContinuation(Continuation continuation, JobSupport jobSupport) {
            super(continuation, 1);
            this.job = jobSupport;
        }

        @Override
        public Throwable getContinuationCancellationCause(Job job) {
            Throwable rootCause;
            Object state$kotlinx_coroutines_core = this.job.getState$kotlinx_coroutines_core();
            return (!(state$kotlinx_coroutines_core instanceof Finishing) || (rootCause = ((Finishing) state$kotlinx_coroutines_core).getRootCause()) == null) ? state$kotlinx_coroutines_core instanceof CompletedExceptionally ? ((CompletedExceptionally) state$kotlinx_coroutines_core).cause : job.getCancellationException() : rootCause;
        }

        @Override
        protected String nameString() {
            return "AwaitContinuation";
        }
    }

    public static final class ChildCompletion extends JobNode {
        private final ChildHandleNode child;
        private final JobSupport parent;
        private final Object proposedUpdate;
        private final Finishing state;

        public ChildCompletion(JobSupport jobSupport, Finishing finishing, ChildHandleNode childHandleNode, Object obj) {
            this.parent = jobSupport;
            this.state = finishing;
            this.child = childHandleNode;
            this.proposedUpdate = obj;
        }

        @Override
        public Object invoke(Object obj) {
            invoke((Throwable) obj);
            return Unit.INSTANCE;
        }

        @Override
        public void invoke(Throwable th) {
            this.parent.continueCompleting(this.state, this.child, this.proposedUpdate);
        }
    }

    public static final class Finishing implements Incomplete {
        private volatile Object _exceptionsHolder;
        private volatile int _isCompleting;
        private volatile Object _rootCause;
        private final NodeList list;
        private static final AtomicIntegerFieldUpdater _isCompleting$FU = AtomicIntegerFieldUpdater.newUpdater(Finishing.class, "_isCompleting");
        private static final AtomicReferenceFieldUpdater _rootCause$FU = AtomicReferenceFieldUpdater.newUpdater(Finishing.class, Object.class, "_rootCause");
        private static final AtomicReferenceFieldUpdater _exceptionsHolder$FU = AtomicReferenceFieldUpdater.newUpdater(Finishing.class, Object.class, "_exceptionsHolder");

        public Finishing(NodeList nodeList, boolean z, Throwable th) {
            this.list = nodeList;
            this._isCompleting = z ? 1 : 0;
            this._rootCause = th;
        }

        private final ArrayList allocateList() {
            return new ArrayList(4);
        }

        private final Object getExceptionsHolder() {
            return _exceptionsHolder$FU.get(this);
        }

        private final void setExceptionsHolder(Object obj) {
            _exceptionsHolder$FU.set(this, obj);
        }

        public final void addExceptionLocked(Throwable th) {
            Throwable rootCause = getRootCause();
            if (rootCause == null) {
                setRootCause(th);
                return;
            }
            if (th == rootCause) {
                return;
            }
            Object exceptionsHolder = getExceptionsHolder();
            if (exceptionsHolder == null) {
                setExceptionsHolder(th);
                return;
            }
            if (exceptionsHolder instanceof Throwable) {
                if (th == exceptionsHolder) {
                    return;
                }
                ArrayList allocateList = allocateList();
                allocateList.add(exceptionsHolder);
                allocateList.add(th);
                setExceptionsHolder(allocateList);
                return;
            }
            if (exceptionsHolder instanceof ArrayList) {
                ((ArrayList) exceptionsHolder).add(th);
                return;
            }
            throw new IllegalStateException(("State is " + exceptionsHolder).toString());
        }

        @Override
        public NodeList getList() {
            return this.list;
        }

        public final Throwable getRootCause() {
            return (Throwable) _rootCause$FU.get(this);
        }

        @Override
        public boolean isActive() {
            return getRootCause() == null;
        }

        public final boolean isCancelling() {
            return getRootCause() != null;
        }

        public final boolean isCompleting() {
            return _isCompleting$FU.get(this) != 0;
        }

        public final boolean isSealed() {
            Symbol symbol;
            Object exceptionsHolder = getExceptionsHolder();
            symbol = JobSupportKt.SEALED;
            return exceptionsHolder == symbol;
        }

        public final List sealLocked(Throwable th) {
            ArrayList arrayList;
            Symbol symbol;
            Object exceptionsHolder = getExceptionsHolder();
            if (exceptionsHolder == null) {
                arrayList = allocateList();
            } else if (exceptionsHolder instanceof Throwable) {
                ArrayList allocateList = allocateList();
                allocateList.add(exceptionsHolder);
                arrayList = allocateList;
            } else {
                if (!(exceptionsHolder instanceof ArrayList)) {
                    throw new IllegalStateException(("State is " + exceptionsHolder).toString());
                }
                arrayList = (ArrayList) exceptionsHolder;
            }
            Throwable rootCause = getRootCause();
            if (rootCause != null) {
                arrayList.add(0, rootCause);
            }
            if (th != null && !Intrinsics.areEqual(th, rootCause)) {
                arrayList.add(th);
            }
            symbol = JobSupportKt.SEALED;
            setExceptionsHolder(symbol);
            return arrayList;
        }

        public final void setCompleting(boolean z) {
            _isCompleting$FU.set(this, z ? 1 : 0);
        }

        public final void setRootCause(Throwable th) {
            _rootCause$FU.set(this, th);
        }

        public String toString() {
            return "Finishing[cancelling=" + isCancelling() + ", completing=" + isCompleting() + ", rootCause=" + getRootCause() + ", exceptions=" + getExceptionsHolder() + ", list=" + getList() + ']';
        }
    }

    public JobSupport(boolean z) {
        this._state = z ? JobSupportKt.EMPTY_ACTIVE : JobSupportKt.EMPTY_NEW;
    }

    private final boolean addLastAtomic(final Object obj, NodeList nodeList, final JobNode jobNode) {
        int tryCondAddNext;
        LockFreeLinkedListNode.CondAddOp condAddOp = new LockFreeLinkedListNode.CondAddOp(jobNode) {
            @Override
            public Object prepare(LockFreeLinkedListNode lockFreeLinkedListNode) {
                if (this.getState$kotlinx_coroutines_core() == obj) {
                    return null;
                }
                return LockFreeLinkedListKt.getCONDITION_FALSE();
            }
        };
        do {
            tryCondAddNext = nodeList.getPrevNode().tryCondAddNext(jobNode, nodeList, condAddOp);
            if (tryCondAddNext == 1) {
                return true;
            }
        } while (tryCondAddNext != 2);
        return false;
    }

    private final void addSuppressedExceptions(Throwable th, List list) {
        if (list.size() <= 1) {
            return;
        }
        Set newSetFromMap = Collections.newSetFromMap(new IdentityHashMap(list.size()));
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Throwable th2 = (Throwable) it.next();
            if (th2 != th && th2 != th && !(th2 instanceof CancellationException) && newSetFromMap.add(th2)) {
                ExceptionsKt__ExceptionsKt.addSuppressed(th, th2);
            }
        }
    }

    private final Object awaitSuspend(Continuation continuation) {
        Continuation intercepted;
        Object coroutine_suspended;
        intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(continuation);
        AwaitContinuation awaitContinuation = new AwaitContinuation(intercepted, this);
        awaitContinuation.initCancellability();
        CancellableContinuationKt.disposeOnCancellation(awaitContinuation, invokeOnCompletion(new ResumeAwaitOnCompletion(awaitContinuation)));
        Object result = awaitContinuation.getResult();
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (result == coroutine_suspended) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }

    private final Object cancelMakeCompleting(Object obj) {
        Symbol symbol;
        Object tryMakeCompleting;
        Symbol symbol2;
        do {
            Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (!(state$kotlinx_coroutines_core instanceof Incomplete) || ((state$kotlinx_coroutines_core instanceof Finishing) && ((Finishing) state$kotlinx_coroutines_core).isCompleting())) {
                symbol = JobSupportKt.COMPLETING_ALREADY;
                return symbol;
            }
            tryMakeCompleting = tryMakeCompleting(state$kotlinx_coroutines_core, new CompletedExceptionally(createCauseException(obj), false, 2, null));
            symbol2 = JobSupportKt.COMPLETING_RETRY;
        } while (tryMakeCompleting == symbol2);
        return tryMakeCompleting;
    }

    private final boolean cancelParent(Throwable th) {
        if (isScopedCoroutine()) {
            return true;
        }
        boolean z = th instanceof CancellationException;
        ChildHandle parentHandle$kotlinx_coroutines_core = getParentHandle$kotlinx_coroutines_core();
        return (parentHandle$kotlinx_coroutines_core == null || parentHandle$kotlinx_coroutines_core == NonDisposableHandle.INSTANCE) ? z : parentHandle$kotlinx_coroutines_core.childCancelled(th) || z;
    }

    private final void completeStateFinalization(Incomplete incomplete, Object obj) {
        ChildHandle parentHandle$kotlinx_coroutines_core = getParentHandle$kotlinx_coroutines_core();
        if (parentHandle$kotlinx_coroutines_core != null) {
            parentHandle$kotlinx_coroutines_core.dispose();
            setParentHandle$kotlinx_coroutines_core(NonDisposableHandle.INSTANCE);
        }
        CompletedExceptionally completedExceptionally = obj instanceof CompletedExceptionally ? (CompletedExceptionally) obj : null;
        Throwable th = completedExceptionally != null ? completedExceptionally.cause : null;
        if (!(incomplete instanceof JobNode)) {
            NodeList list = incomplete.getList();
            if (list != null) {
                notifyCompletion(list, th);
                return;
            }
            return;
        }
        try {
            ((JobNode) incomplete).invoke(th);
        } catch (Throwable th2) {
            handleOnCompletionException$kotlinx_coroutines_core(new CompletionHandlerException("Exception in completion handler " + incomplete + " for " + this, th2));
        }
    }

    public final void continueCompleting(Finishing finishing, ChildHandleNode childHandleNode, Object obj) {
        ChildHandleNode nextChild = nextChild(childHandleNode);
        if (nextChild == null || !tryWaitForChild(finishing, nextChild, obj)) {
            afterCompletion(finalizeFinishingState(finishing, obj));
        }
    }

    private final Throwable createCauseException(Object obj) {
        if (obj == null ? true : obj instanceof Throwable) {
            Throwable th = (Throwable) obj;
            return th == null ? new JobCancellationException(cancellationExceptionMessage(), null, this) : th;
        }
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.ParentJob");
        return ((ParentJob) obj).getChildJobCancellationCause();
    }

    private final Object finalizeFinishingState(Finishing finishing, Object obj) {
        boolean isCancelling;
        Throwable finalRootCause;
        CompletedExceptionally completedExceptionally = obj instanceof CompletedExceptionally ? (CompletedExceptionally) obj : null;
        Throwable th = completedExceptionally != null ? completedExceptionally.cause : null;
        synchronized (finishing) {
            isCancelling = finishing.isCancelling();
            List sealLocked = finishing.sealLocked(th);
            finalRootCause = getFinalRootCause(finishing, sealLocked);
            if (finalRootCause != null) {
                addSuppressedExceptions(finalRootCause, sealLocked);
            }
        }
        if (finalRootCause != null && finalRootCause != th) {
            obj = new CompletedExceptionally(finalRootCause, false, 2, null);
        }
        if (finalRootCause != null && (cancelParent(finalRootCause) || handleJobException(finalRootCause))) {
            Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.CompletedExceptionally");
            ((CompletedExceptionally) obj).makeHandled();
        }
        if (!isCancelling) {
            onCancelling(finalRootCause);
        }
        onCompletionInternal(obj);
        AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, finishing, JobSupportKt.boxIncomplete(obj));
        completeStateFinalization(finishing, obj);
        return obj;
    }

    private final ChildHandleNode firstChild(Incomplete incomplete) {
        ChildHandleNode childHandleNode = incomplete instanceof ChildHandleNode ? (ChildHandleNode) incomplete : null;
        if (childHandleNode != null) {
            return childHandleNode;
        }
        NodeList list = incomplete.getList();
        if (list != null) {
            return nextChild(list);
        }
        return null;
    }

    private final Throwable getExceptionOrNull(Object obj) {
        CompletedExceptionally completedExceptionally = obj instanceof CompletedExceptionally ? (CompletedExceptionally) obj : null;
        if (completedExceptionally != null) {
            return completedExceptionally.cause;
        }
        return null;
    }

    private final Throwable getFinalRootCause(Finishing finishing, List list) {
        Object obj;
        Object obj2 = null;
        if (list.isEmpty()) {
            if (finishing.isCancelling()) {
                return new JobCancellationException(cancellationExceptionMessage(), null, this);
            }
            return null;
        }
        List list2 = list;
        Iterator it = list2.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (!(((Throwable) obj) instanceof CancellationException)) {
                break;
            }
        }
        Throwable th = (Throwable) obj;
        if (th != null) {
            return th;
        }
        Throwable th2 = (Throwable) list.get(0);
        if (th2 instanceof TimeoutCancellationException) {
            Iterator it2 = list2.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                Object next = it2.next();
                Throwable th3 = (Throwable) next;
                if (th3 != th2 && (th3 instanceof TimeoutCancellationException)) {
                    obj2 = next;
                    break;
                }
            }
            Throwable th4 = (Throwable) obj2;
            if (th4 != null) {
                return th4;
            }
        }
        return th2;
    }

    private final NodeList getOrPromoteCancellingList(Incomplete incomplete) {
        NodeList list = incomplete.getList();
        if (list != null) {
            return list;
        }
        if (incomplete instanceof Empty) {
            return new NodeList();
        }
        if (incomplete instanceof JobNode) {
            promoteSingleToNodeList((JobNode) incomplete);
            return null;
        }
        throw new IllegalStateException(("State should have list: " + incomplete).toString());
    }

    private final boolean joinInternal() {
        Object state$kotlinx_coroutines_core;
        do {
            state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
                return false;
            }
        } while (startInternal(state$kotlinx_coroutines_core) < 0);
        return true;
    }

    private final Object joinSuspend(Continuation continuation) {
        Continuation intercepted;
        Object coroutine_suspended;
        Object coroutine_suspended2;
        intercepted = IntrinsicsKt__IntrinsicsJvmKt.intercepted(continuation);
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(intercepted, 1);
        cancellableContinuationImpl.initCancellability();
        CancellableContinuationKt.disposeOnCancellation(cancellableContinuationImpl, invokeOnCompletion(new ResumeOnCompletion(cancellableContinuationImpl)));
        Object result = cancellableContinuationImpl.getResult();
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (result == coroutine_suspended) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        coroutine_suspended2 = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        return result == coroutine_suspended2 ? result : Unit.INSTANCE;
    }

    private final Object makeCancelling(Object obj) {
        Symbol symbol;
        Symbol symbol2;
        Symbol symbol3;
        Symbol symbol4;
        Symbol symbol5;
        Symbol symbol6;
        Throwable th = null;
        while (true) {
            Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (state$kotlinx_coroutines_core instanceof Finishing) {
                synchronized (state$kotlinx_coroutines_core) {
                    if (((Finishing) state$kotlinx_coroutines_core).isSealed()) {
                        symbol2 = JobSupportKt.TOO_LATE_TO_CANCEL;
                        return symbol2;
                    }
                    boolean isCancelling = ((Finishing) state$kotlinx_coroutines_core).isCancelling();
                    if (obj != null || !isCancelling) {
                        if (th == null) {
                            th = createCauseException(obj);
                        }
                        ((Finishing) state$kotlinx_coroutines_core).addExceptionLocked(th);
                    }
                    Throwable rootCause = isCancelling ^ true ? ((Finishing) state$kotlinx_coroutines_core).getRootCause() : null;
                    if (rootCause != null) {
                        notifyCancelling(((Finishing) state$kotlinx_coroutines_core).getList(), rootCause);
                    }
                    symbol = JobSupportKt.COMPLETING_ALREADY;
                    return symbol;
                }
            }
            if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
                symbol3 = JobSupportKt.TOO_LATE_TO_CANCEL;
                return symbol3;
            }
            if (th == null) {
                th = createCauseException(obj);
            }
            Incomplete incomplete = (Incomplete) state$kotlinx_coroutines_core;
            if (!incomplete.isActive()) {
                Object tryMakeCompleting = tryMakeCompleting(state$kotlinx_coroutines_core, new CompletedExceptionally(th, false, 2, null));
                symbol5 = JobSupportKt.COMPLETING_ALREADY;
                if (tryMakeCompleting == symbol5) {
                    throw new IllegalStateException(("Cannot happen in " + state$kotlinx_coroutines_core).toString());
                }
                symbol6 = JobSupportKt.COMPLETING_RETRY;
                if (tryMakeCompleting != symbol6) {
                    return tryMakeCompleting;
                }
            } else if (tryMakeCancelling(incomplete, th)) {
                symbol4 = JobSupportKt.COMPLETING_ALREADY;
                return symbol4;
            }
        }
    }

    private final JobNode makeNode(Function1 function1, boolean z) {
        JobNode jobNode;
        if (z) {
            jobNode = function1 instanceof JobCancellingNode ? (JobCancellingNode) function1 : null;
            if (jobNode == null) {
                jobNode = new InvokeOnCancelling(function1);
            }
        } else {
            jobNode = function1 instanceof JobNode ? (JobNode) function1 : null;
            if (jobNode == null) {
                jobNode = new InvokeOnCompletion(function1);
            }
        }
        jobNode.setJob(this);
        return jobNode;
    }

    private final ChildHandleNode nextChild(LockFreeLinkedListNode lockFreeLinkedListNode) {
        while (lockFreeLinkedListNode.isRemoved()) {
            lockFreeLinkedListNode = lockFreeLinkedListNode.getPrevNode();
        }
        while (true) {
            lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode();
            if (!lockFreeLinkedListNode.isRemoved()) {
                if (lockFreeLinkedListNode instanceof ChildHandleNode) {
                    return (ChildHandleNode) lockFreeLinkedListNode;
                }
                if (lockFreeLinkedListNode instanceof NodeList) {
                    return null;
                }
            }
        }
    }

    private final void notifyCancelling(NodeList nodeList, Throwable th) {
        onCancelling(th);
        Object next = nodeList.getNext();
        Intrinsics.checkNotNull(next, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
        CompletionHandlerException completionHandlerException = null;
        for (LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) next; !Intrinsics.areEqual(lockFreeLinkedListNode, nodeList); lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode()) {
            if (lockFreeLinkedListNode instanceof JobCancellingNode) {
                JobNode jobNode = (JobNode) lockFreeLinkedListNode;
                try {
                    jobNode.invoke(th);
                } catch (Throwable th2) {
                    if (completionHandlerException != null) {
                        ExceptionsKt__ExceptionsKt.addSuppressed(completionHandlerException, th2);
                    } else {
                        completionHandlerException = new CompletionHandlerException("Exception in completion handler " + jobNode + " for " + this, th2);
                        Unit unit = Unit.INSTANCE;
                    }
                }
            }
        }
        if (completionHandlerException != null) {
            handleOnCompletionException$kotlinx_coroutines_core(completionHandlerException);
        }
        cancelParent(th);
    }

    private final void notifyCompletion(NodeList nodeList, Throwable th) {
        Object next = nodeList.getNext();
        Intrinsics.checkNotNull(next, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
        CompletionHandlerException completionHandlerException = null;
        for (LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) next; !Intrinsics.areEqual(lockFreeLinkedListNode, nodeList); lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode()) {
            if (lockFreeLinkedListNode instanceof JobNode) {
                JobNode jobNode = (JobNode) lockFreeLinkedListNode;
                try {
                    jobNode.invoke(th);
                } catch (Throwable th2) {
                    if (completionHandlerException != null) {
                        ExceptionsKt__ExceptionsKt.addSuppressed(completionHandlerException, th2);
                    } else {
                        completionHandlerException = new CompletionHandlerException("Exception in completion handler " + jobNode + " for " + this, th2);
                        Unit unit = Unit.INSTANCE;
                    }
                }
            }
        }
        if (completionHandlerException != null) {
            handleOnCompletionException$kotlinx_coroutines_core(completionHandlerException);
        }
    }

    private final void promoteEmptyToNodeList(Empty empty) {
        NodeList nodeList = new NodeList();
        if (!empty.isActive()) {
            nodeList = new InactiveNodeList(nodeList);
        }
        AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, empty, nodeList);
    }

    private final void promoteSingleToNodeList(JobNode jobNode) {
        jobNode.addOneIfEmpty(new NodeList());
        AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, jobNode, jobNode.getNextNode());
    }

    private final int startInternal(Object obj) {
        Empty empty;
        if (!(obj instanceof Empty)) {
            if (!(obj instanceof InactiveNodeList)) {
                return 0;
            }
            if (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, obj, ((InactiveNodeList) obj).getList())) {
                return -1;
            }
            onStart();
            return 1;
        }
        if (((Empty) obj).isActive()) {
            return 0;
        }
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _state$FU;
        empty = JobSupportKt.EMPTY_ACTIVE;
        if (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, obj, empty)) {
            return -1;
        }
        onStart();
        return 1;
    }

    private final String stateString(Object obj) {
        if (!(obj instanceof Finishing)) {
            return obj instanceof Incomplete ? ((Incomplete) obj).isActive() ? "Active" : "New" : obj instanceof CompletedExceptionally ? "Cancelled" : "Completed";
        }
        Finishing finishing = (Finishing) obj;
        return finishing.isCancelling() ? "Cancelling" : finishing.isCompleting() ? "Completing" : "Active";
    }

    public static CancellationException toCancellationException$default(JobSupport jobSupport, Throwable th, String str, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: toCancellationException");
        }
        if ((i & 1) != 0) {
            str = null;
        }
        return jobSupport.toCancellationException(th, str);
    }

    private final boolean tryFinalizeSimpleState(Incomplete incomplete, Object obj) {
        if (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, incomplete, JobSupportKt.boxIncomplete(obj))) {
            return false;
        }
        onCancelling(null);
        onCompletionInternal(obj);
        completeStateFinalization(incomplete, obj);
        return true;
    }

    private final boolean tryMakeCancelling(Incomplete incomplete, Throwable th) {
        NodeList orPromoteCancellingList = getOrPromoteCancellingList(incomplete);
        if (orPromoteCancellingList == null) {
            return false;
        }
        if (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, incomplete, new Finishing(orPromoteCancellingList, false, th))) {
            return false;
        }
        notifyCancelling(orPromoteCancellingList, th);
        return true;
    }

    private final Object tryMakeCompleting(Object obj, Object obj2) {
        Symbol symbol;
        Symbol symbol2;
        if (!(obj instanceof Incomplete)) {
            symbol2 = JobSupportKt.COMPLETING_ALREADY;
            return symbol2;
        }
        if ((!(obj instanceof Empty) && !(obj instanceof JobNode)) || (obj instanceof ChildHandleNode) || (obj2 instanceof CompletedExceptionally)) {
            return tryMakeCompletingSlowPath((Incomplete) obj, obj2);
        }
        if (tryFinalizeSimpleState((Incomplete) obj, obj2)) {
            return obj2;
        }
        symbol = JobSupportKt.COMPLETING_RETRY;
        return symbol;
    }

    private final Object tryMakeCompletingSlowPath(Incomplete incomplete, Object obj) {
        Symbol symbol;
        Symbol symbol2;
        Symbol symbol3;
        NodeList orPromoteCancellingList = getOrPromoteCancellingList(incomplete);
        if (orPromoteCancellingList == null) {
            symbol3 = JobSupportKt.COMPLETING_RETRY;
            return symbol3;
        }
        Finishing finishing = incomplete instanceof Finishing ? (Finishing) incomplete : null;
        if (finishing == null) {
            finishing = new Finishing(orPromoteCancellingList, false, null);
        }
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        synchronized (finishing) {
            if (finishing.isCompleting()) {
                symbol2 = JobSupportKt.COMPLETING_ALREADY;
                return symbol2;
            }
            finishing.setCompleting(true);
            if (finishing != incomplete && !AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, incomplete, finishing)) {
                symbol = JobSupportKt.COMPLETING_RETRY;
                return symbol;
            }
            boolean isCancelling = finishing.isCancelling();
            CompletedExceptionally completedExceptionally = obj instanceof CompletedExceptionally ? (CompletedExceptionally) obj : null;
            if (completedExceptionally != null) {
                finishing.addExceptionLocked(completedExceptionally.cause);
            }
            Throwable rootCause = true ^ isCancelling ? finishing.getRootCause() : null;
            ref$ObjectRef.element = rootCause;
            Unit unit = Unit.INSTANCE;
            if (rootCause != null) {
                notifyCancelling(orPromoteCancellingList, rootCause);
            }
            ChildHandleNode firstChild = firstChild(incomplete);
            return (firstChild == null || !tryWaitForChild(finishing, firstChild, obj)) ? finalizeFinishingState(finishing, obj) : JobSupportKt.COMPLETING_WAITING_CHILDREN;
        }
    }

    private final boolean tryWaitForChild(Finishing finishing, ChildHandleNode childHandleNode, Object obj) {
        while (Job.DefaultImpls.invokeOnCompletion$default(childHandleNode.childJob, false, false, new ChildCompletion(this, finishing, childHandleNode, obj), 1, null) == NonDisposableHandle.INSTANCE) {
            childHandleNode = nextChild(childHandleNode);
            if (childHandleNode == null) {
                return false;
            }
        }
        return true;
    }

    public void afterCompletion(Object obj) {
    }

    @Override
    public final ChildHandle attachChild(ChildJob childJob) {
        DisposableHandle invokeOnCompletion$default = Job.DefaultImpls.invokeOnCompletion$default(this, true, false, new ChildHandleNode(childJob), 2, null);
        Intrinsics.checkNotNull(invokeOnCompletion$default, "null cannot be cast to non-null type kotlinx.coroutines.ChildHandle");
        return (ChildHandle) invokeOnCompletion$default;
    }

    public final Object awaitInternal(Continuation continuation) {
        Object state$kotlinx_coroutines_core;
        do {
            state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
                if (state$kotlinx_coroutines_core instanceof CompletedExceptionally) {
                    throw ((CompletedExceptionally) state$kotlinx_coroutines_core).cause;
                }
                return JobSupportKt.unboxState(state$kotlinx_coroutines_core);
            }
        } while (startInternal(state$kotlinx_coroutines_core) < 0);
        return awaitSuspend(continuation);
    }

    @Override
    public void cancel(CancellationException cancellationException) {
        if (cancellationException == null) {
            cancellationException = new JobCancellationException(cancellationExceptionMessage(), null, this);
        }
        cancelInternal(cancellationException);
    }

    public final boolean cancelCoroutine(Throwable th) {
        return cancelImpl$kotlinx_coroutines_core(th);
    }

    public final boolean cancelImpl$kotlinx_coroutines_core(Object obj) {
        Object obj2;
        Symbol symbol;
        Symbol symbol2;
        Symbol symbol3;
        obj2 = JobSupportKt.COMPLETING_ALREADY;
        if (getOnCancelComplete$kotlinx_coroutines_core() && (obj2 = cancelMakeCompleting(obj)) == JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            return true;
        }
        symbol = JobSupportKt.COMPLETING_ALREADY;
        if (obj2 == symbol) {
            obj2 = makeCancelling(obj);
        }
        symbol2 = JobSupportKt.COMPLETING_ALREADY;
        if (obj2 == symbol2 || obj2 == JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            return true;
        }
        symbol3 = JobSupportKt.TOO_LATE_TO_CANCEL;
        if (obj2 == symbol3) {
            return false;
        }
        afterCompletion(obj2);
        return true;
    }

    public void cancelInternal(Throwable th) {
        cancelImpl$kotlinx_coroutines_core(th);
    }

    public String cancellationExceptionMessage() {
        return "Job was cancelled";
    }

    public boolean childCancelled(Throwable th) {
        if (th instanceof CancellationException) {
            return true;
        }
        return cancelImpl$kotlinx_coroutines_core(th) && getHandlesException$kotlinx_coroutines_core();
    }

    @Override
    public Object fold(Object obj, Function2 function2) {
        return Job.DefaultImpls.fold(this, obj, function2);
    }

    @Override
    public CoroutineContext.Element get(CoroutineContext.Key key) {
        return Job.DefaultImpls.get(this, key);
    }

    @Override
    public final CancellationException getCancellationException() {
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        if (!(state$kotlinx_coroutines_core instanceof Finishing)) {
            if (state$kotlinx_coroutines_core instanceof Incomplete) {
                throw new IllegalStateException(("Job is still new or active: " + this).toString());
            }
            if (state$kotlinx_coroutines_core instanceof CompletedExceptionally) {
                return toCancellationException$default(this, ((CompletedExceptionally) state$kotlinx_coroutines_core).cause, null, 1, null);
            }
            return new JobCancellationException(DebugStringsKt.getClassSimpleName(this) + " has completed normally", null, this);
        }
        Throwable rootCause = ((Finishing) state$kotlinx_coroutines_core).getRootCause();
        if (rootCause != null) {
            CancellationException cancellationException = toCancellationException(rootCause, DebugStringsKt.getClassSimpleName(this) + " is cancelling");
            if (cancellationException != null) {
                return cancellationException;
            }
        }
        throw new IllegalStateException(("Job is still new or active: " + this).toString());
    }

    @Override
    public CancellationException getChildJobCancellationCause() {
        CancellationException cancellationException;
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        if (state$kotlinx_coroutines_core instanceof Finishing) {
            cancellationException = ((Finishing) state$kotlinx_coroutines_core).getRootCause();
        } else if (state$kotlinx_coroutines_core instanceof CompletedExceptionally) {
            cancellationException = ((CompletedExceptionally) state$kotlinx_coroutines_core).cause;
        } else {
            if (state$kotlinx_coroutines_core instanceof Incomplete) {
                throw new IllegalStateException(("Cannot be cancelling child in this state: " + state$kotlinx_coroutines_core).toString());
            }
            cancellationException = null;
        }
        CancellationException cancellationException2 = cancellationException instanceof CancellationException ? cancellationException : null;
        if (cancellationException2 != null) {
            return cancellationException2;
        }
        return new JobCancellationException("Parent job is " + stateString(state$kotlinx_coroutines_core), cancellationException, this);
    }

    @Override
    public final Sequence getChildren() {
        Sequence sequence;
        sequence = SequencesKt__SequenceBuilderKt.sequence(new JobSupport$children$1(this, null));
        return sequence;
    }

    public final Object getCompletedInternal$kotlinx_coroutines_core() {
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        if (!(!(state$kotlinx_coroutines_core instanceof Incomplete))) {
            throw new IllegalStateException("This job has not completed yet".toString());
        }
        if (state$kotlinx_coroutines_core instanceof CompletedExceptionally) {
            throw ((CompletedExceptionally) state$kotlinx_coroutines_core).cause;
        }
        return JobSupportKt.unboxState(state$kotlinx_coroutines_core);
    }

    public final Throwable getCompletionExceptionOrNull() {
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
            return getExceptionOrNull(state$kotlinx_coroutines_core);
        }
        throw new IllegalStateException("This job has not completed yet".toString());
    }

    public boolean getHandlesException$kotlinx_coroutines_core() {
        return true;
    }

    @Override
    public final CoroutineContext.Key getKey() {
        return Job.Key;
    }

    public boolean getOnCancelComplete$kotlinx_coroutines_core() {
        return false;
    }

    public final ChildHandle getParentHandle$kotlinx_coroutines_core() {
        return (ChildHandle) _parentHandle$FU.get(this);
    }

    public final Object getState$kotlinx_coroutines_core() {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _state$FU;
        while (true) {
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (!(obj instanceof OpDescriptor)) {
                return obj;
            }
            ((OpDescriptor) obj).perform(this);
        }
    }

    protected boolean handleJobException(Throwable th) {
        return false;
    }

    public void handleOnCompletionException$kotlinx_coroutines_core(Throwable th) {
        throw th;
    }

    public final void initParentJob(Job job) {
        if (job == null) {
            setParentHandle$kotlinx_coroutines_core(NonDisposableHandle.INSTANCE);
            return;
        }
        job.start();
        ChildHandle attachChild = job.attachChild(this);
        setParentHandle$kotlinx_coroutines_core(attachChild);
        if (isCompleted()) {
            attachChild.dispose();
            setParentHandle$kotlinx_coroutines_core(NonDisposableHandle.INSTANCE);
        }
    }

    @Override
    public final DisposableHandle invokeOnCompletion(Function1 function1) {
        return invokeOnCompletion(false, true, function1);
    }

    @Override
    public final DisposableHandle invokeOnCompletion(boolean z, boolean z2, Function1 function1) {
        JobNode makeNode = makeNode(function1, z);
        while (true) {
            Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (state$kotlinx_coroutines_core instanceof Empty) {
                Empty empty = (Empty) state$kotlinx_coroutines_core;
                if (!empty.isActive()) {
                    promoteEmptyToNodeList(empty);
                } else if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, state$kotlinx_coroutines_core, makeNode)) {
                    return makeNode;
                }
            } else {
                if (!(state$kotlinx_coroutines_core instanceof Incomplete)) {
                    if (z2) {
                        CompletedExceptionally completedExceptionally = state$kotlinx_coroutines_core instanceof CompletedExceptionally ? (CompletedExceptionally) state$kotlinx_coroutines_core : null;
                        function1.invoke(completedExceptionally != null ? completedExceptionally.cause : null);
                    }
                    return NonDisposableHandle.INSTANCE;
                }
                NodeList list = ((Incomplete) state$kotlinx_coroutines_core).getList();
                if (list == null) {
                    Intrinsics.checkNotNull(state$kotlinx_coroutines_core, "null cannot be cast to non-null type kotlinx.coroutines.JobNode");
                    promoteSingleToNodeList((JobNode) state$kotlinx_coroutines_core);
                } else {
                    DisposableHandle disposableHandle = NonDisposableHandle.INSTANCE;
                    if (z && (state$kotlinx_coroutines_core instanceof Finishing)) {
                        synchronized (state$kotlinx_coroutines_core) {
                            try {
                                r3 = ((Finishing) state$kotlinx_coroutines_core).getRootCause();
                                if (r3 != null) {
                                    if ((function1 instanceof ChildHandleNode) && !((Finishing) state$kotlinx_coroutines_core).isCompleting()) {
                                    }
                                    Unit unit = Unit.INSTANCE;
                                }
                                if (addLastAtomic(state$kotlinx_coroutines_core, list, makeNode)) {
                                    if (r3 == null) {
                                        return makeNode;
                                    }
                                    disposableHandle = makeNode;
                                    Unit unit2 = Unit.INSTANCE;
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    }
                    if (r3 != null) {
                        if (z2) {
                            function1.invoke(r3);
                        }
                        return disposableHandle;
                    }
                    if (addLastAtomic(state$kotlinx_coroutines_core, list, makeNode)) {
                        return makeNode;
                    }
                }
            }
        }
    }

    @Override
    public boolean isActive() {
        Object state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
        return (state$kotlinx_coroutines_core instanceof Incomplete) && ((Incomplete) state$kotlinx_coroutines_core).isActive();
    }

    public final boolean isCompleted() {
        return !(getState$kotlinx_coroutines_core() instanceof Incomplete);
    }

    protected boolean isScopedCoroutine() {
        return false;
    }

    @Override
    public final Object join(Continuation continuation) {
        Object coroutine_suspended;
        if (!joinInternal()) {
            JobKt.ensureActive(continuation.getContext());
            return Unit.INSTANCE;
        }
        Object joinSuspend = joinSuspend(continuation);
        coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        return joinSuspend == coroutine_suspended ? joinSuspend : Unit.INSTANCE;
    }

    public final boolean makeCompleting$kotlinx_coroutines_core(Object obj) {
        Object tryMakeCompleting;
        Symbol symbol;
        Symbol symbol2;
        do {
            tryMakeCompleting = tryMakeCompleting(getState$kotlinx_coroutines_core(), obj);
            symbol = JobSupportKt.COMPLETING_ALREADY;
            if (tryMakeCompleting == symbol) {
                return false;
            }
            if (tryMakeCompleting == JobSupportKt.COMPLETING_WAITING_CHILDREN) {
                return true;
            }
            symbol2 = JobSupportKt.COMPLETING_RETRY;
        } while (tryMakeCompleting == symbol2);
        afterCompletion(tryMakeCompleting);
        return true;
    }

    public final Object makeCompletingOnce$kotlinx_coroutines_core(Object obj) {
        Object tryMakeCompleting;
        Symbol symbol;
        Symbol symbol2;
        do {
            tryMakeCompleting = tryMakeCompleting(getState$kotlinx_coroutines_core(), obj);
            symbol = JobSupportKt.COMPLETING_ALREADY;
            if (tryMakeCompleting == symbol) {
                throw new IllegalStateException("Job " + this + " is already complete or completing, but is being completed with " + obj, getExceptionOrNull(obj));
            }
            symbol2 = JobSupportKt.COMPLETING_RETRY;
        } while (tryMakeCompleting == symbol2);
        return tryMakeCompleting;
    }

    @Override
    public CoroutineContext minusKey(CoroutineContext.Key key) {
        return Job.DefaultImpls.minusKey(this, key);
    }

    public String nameString$kotlinx_coroutines_core() {
        return DebugStringsKt.getClassSimpleName(this);
    }

    protected void onCancelling(Throwable th) {
    }

    protected void onCompletionInternal(Object obj) {
    }

    protected void onStart() {
    }

    @Override
    public final void parentCancelled(ParentJob parentJob) {
        cancelImpl$kotlinx_coroutines_core(parentJob);
    }

    @Override
    public CoroutineContext plus(CoroutineContext coroutineContext) {
        return Job.DefaultImpls.plus(this, coroutineContext);
    }

    public final void removeNode$kotlinx_coroutines_core(JobNode jobNode) {
        Object state$kotlinx_coroutines_core;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        Empty empty;
        do {
            state$kotlinx_coroutines_core = getState$kotlinx_coroutines_core();
            if (!(state$kotlinx_coroutines_core instanceof JobNode)) {
                if (!(state$kotlinx_coroutines_core instanceof Incomplete) || ((Incomplete) state$kotlinx_coroutines_core).getList() == null) {
                    return;
                }
                jobNode.remove();
                return;
            }
            if (state$kotlinx_coroutines_core != jobNode) {
                return;
            }
            atomicReferenceFieldUpdater = _state$FU;
            empty = JobSupportKt.EMPTY_ACTIVE;
        } while (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, state$kotlinx_coroutines_core, empty));
    }

    public final void setParentHandle$kotlinx_coroutines_core(ChildHandle childHandle) {
        _parentHandle$FU.set(this, childHandle);
    }

    @Override
    public final boolean start() {
        int startInternal;
        do {
            startInternal = startInternal(getState$kotlinx_coroutines_core());
            if (startInternal == 0) {
                return false;
            }
        } while (startInternal != 1);
        return true;
    }

    protected final CancellationException toCancellationException(Throwable th, String str) {
        CancellationException cancellationException = th instanceof CancellationException ? (CancellationException) th : null;
        if (cancellationException == null) {
            if (str == null) {
                str = cancellationExceptionMessage();
            }
            cancellationException = new JobCancellationException(str, th, this);
        }
        return cancellationException;
    }

    public final String toDebugString() {
        return nameString$kotlinx_coroutines_core() + '{' + stateString(getState$kotlinx_coroutines_core()) + '}';
    }

    public String toString() {
        return toDebugString() + '@' + DebugStringsKt.getHexAddress(this);
    }
}
