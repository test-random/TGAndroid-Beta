package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.ArrayList;

public class DispatchQueuePoolBackground {
    public static final String THREAD_PREFIX = "DispatchQueuePoolThreadSafety_";
    private static DispatchQueuePoolBackground backgroundQueue;
    static ArrayList<Runnable> updateTaskCollection;
    private boolean cleanupScheduled;
    private int createdCount;
    private int maxCount;
    private int totalTasksCount;
    private static final ArrayList<ArrayList<Runnable>> freeCollections = new ArrayList<>();
    private static final Runnable finishCollectUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            DispatchQueuePoolBackground.finishCollectUpdateRunnables();
        }
    };
    private ArrayList<DispatchQueue> queues = new ArrayList<>(10);
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private ArrayList<DispatchQueue> busyQueues = new ArrayList<>(10);
    private Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            if (!DispatchQueuePoolBackground.this.queues.isEmpty()) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                int i = 0;
                while (i < DispatchQueuePoolBackground.this.queues.size()) {
                    DispatchQueue dispatchQueue = (DispatchQueue) DispatchQueuePoolBackground.this.queues.get(i);
                    if (dispatchQueue.getLastTaskTime() < elapsedRealtime - 30000) {
                        dispatchQueue.recycle();
                        DispatchQueuePoolBackground.this.queues.remove(i);
                        DispatchQueuePoolBackground.access$110(DispatchQueuePoolBackground.this);
                        i--;
                    }
                    i++;
                }
            }
            if (DispatchQueuePoolBackground.this.queues.isEmpty() && DispatchQueuePoolBackground.this.busyQueues.isEmpty()) {
                DispatchQueuePoolBackground.this.cleanupScheduled = false;
            } else {
                Utilities.globalQueue.postRunnable(this, 30000L);
                DispatchQueuePoolBackground.this.cleanupScheduled = true;
            }
        }
    };
    private int guid = Utilities.random.nextInt();

    private DispatchQueuePoolBackground(int i) {
        this.maxCount = i;
    }

    static int access$110(DispatchQueuePoolBackground dispatchQueuePoolBackground) {
        int i = dispatchQueuePoolBackground.createdCount;
        dispatchQueuePoolBackground.createdCount = i - 1;
        return i;
    }

    public static void execute(Runnable runnable) {
        execute(runnable, false);
    }

    public static void execute(Runnable runnable, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e(new RuntimeException("wrong thread"));
                return;
            }
            return;
        }
        if (updateTaskCollection == null) {
            ArrayList<ArrayList<Runnable>> arrayList = freeCollections;
            updateTaskCollection = !arrayList.isEmpty() ? arrayList.remove(arrayList.size() - 1) : new ArrayList<>(100);
            if (!z) {
                AndroidUtilities.runOnUIThread(finishCollectUpdateRunnable);
            }
        }
        updateTaskCollection.add(runnable);
        if (z) {
            Runnable runnable2 = finishCollectUpdateRunnable;
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            runnable2.run();
        }
    }

    private void execute(java.util.ArrayList<java.lang.Runnable> r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DispatchQueuePoolBackground.execute(java.util.ArrayList):void");
    }

    public static void finishCollectUpdateRunnables() {
        ArrayList<Runnable> arrayList = updateTaskCollection;
        if (arrayList == null || arrayList.isEmpty()) {
            updateTaskCollection = null;
            return;
        }
        final ArrayList<Runnable> arrayList2 = updateTaskCollection;
        updateTaskCollection = null;
        if (backgroundQueue == null) {
            backgroundQueue = new DispatchQueuePoolBackground(Math.max(1, Runtime.getRuntime().availableProcessors()));
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                DispatchQueuePoolBackground.lambda$finishCollectUpdateRunnables$3(arrayList2);
            }
        });
    }

    public void lambda$execute$0(DispatchQueue dispatchQueue) {
        this.totalTasksCount--;
        int i = this.busyQueuesMap.get(dispatchQueue.index) - 1;
        if (i != 0) {
            this.busyQueuesMap.put(dispatchQueue.index, i);
            return;
        }
        this.busyQueuesMap.delete(dispatchQueue.index);
        this.busyQueues.remove(dispatchQueue);
        this.queues.add(dispatchQueue);
    }

    public void lambda$execute$1(Runnable runnable, final DispatchQueue dispatchQueue) {
        runnable.run();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                DispatchQueuePoolBackground.this.lambda$execute$0(dispatchQueue);
            }
        });
    }

    public static void lambda$finishCollectUpdateRunnables$2(ArrayList arrayList) {
        freeCollections.add(arrayList);
    }

    public static void lambda$finishCollectUpdateRunnables$3(final ArrayList arrayList) {
        backgroundQueue.execute((ArrayList<Runnable>) arrayList);
        arrayList.clear();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DispatchQueuePoolBackground.lambda$finishCollectUpdateRunnables$2(arrayList);
            }
        });
    }
}
