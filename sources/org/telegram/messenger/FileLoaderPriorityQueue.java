package org.telegram.messenger;

import java.util.ArrayList;

public class FileLoaderPriorityQueue {
    public static final int PRIORITY_VALUE_LOW = 0;
    public static final int PRIORITY_VALUE_MAX = 1048576;
    public static final int PRIORITY_VALUE_NORMAL = 65536;
    public static final int TYPE_LARGE = 1;
    public static final int TYPE_SMALL = 0;
    int currentAccount;
    String name;
    int type;
    final DispatchQueue workerQueue;
    public ArrayList<FileLoadOperation> allOperations = new ArrayList<>();
    public ArrayList<FileLoadOperation> tmpListOperations = new ArrayList<>();
    boolean checkOperationsScheduled = false;
    Runnable checkOperationsRunnable = new Runnable() {
        @Override
        public final void run() {
            FileLoaderPriorityQueue.this.lambda$new$0();
        }
    };

    public FileLoaderPriorityQueue(int i, String str, int i2, DispatchQueue dispatchQueue) {
        this.currentAccount = i;
        this.name = str;
        this.type = i2;
        this.workerQueue = dispatchQueue;
    }

    private void checkLoadingOperationInternal() {
        int i = this.type == 1 ? MessagesController.getInstance(this.currentAccount).largeQueueMaxActiveOperations : MessagesController.getInstance(this.currentAccount).smallQueueMaxActiveOperations;
        this.tmpListOperations.clear();
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        while (i2 < this.allOperations.size()) {
            FileLoadOperation fileLoadOperation = i2 > 0 ? this.allOperations.get(i2 - 1) : null;
            FileLoadOperation fileLoadOperation2 = this.allOperations.get(i2);
            if (i2 > 0 && !z) {
                if (this.type == 1 && fileLoadOperation != null && fileLoadOperation.isStory && fileLoadOperation.getPriority() >= 1048576 && fileLoadOperation2.getPriority() <= 0) {
                    z = true;
                }
                if (i3 > 0 && fileLoadOperation2.getPriority() == 0) {
                    z = true;
                }
            }
            if (fileLoadOperation2.preFinished) {
                i++;
            } else {
                if (!z && i2 < i) {
                    this.tmpListOperations.add(fileLoadOperation2);
                } else if (fileLoadOperation2.wasStarted()) {
                    fileLoadOperation2.pause();
                }
                i3 = fileLoadOperation2.getPriority();
            }
            i2++;
        }
        for (int i4 = 0; i4 < this.tmpListOperations.size(); i4++) {
            this.tmpListOperations.get(i4).start();
        }
    }

    public void lambda$new$0() {
        checkLoadingOperationInternal();
        this.checkOperationsScheduled = false;
    }

    public void add(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation == null) {
            return;
        }
        int i = 0;
        int i2 = 0;
        while (i2 < this.allOperations.size()) {
            if (this.allOperations.get(i2) == fileLoadOperation) {
                this.allOperations.remove(i2);
                i2--;
            }
            i2++;
        }
        while (true) {
            if (i >= this.allOperations.size()) {
                i = -1;
                break;
            } else if (fileLoadOperation.getPriority() > this.allOperations.get(i).getPriority()) {
                break;
            } else {
                i++;
            }
        }
        if (i >= 0) {
            this.allOperations.add(i, fileLoadOperation);
        } else {
            this.allOperations.add(fileLoadOperation);
        }
    }

    public void cancel(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation != null && this.allOperations.remove(fileLoadOperation)) {
            fileLoadOperation.cancel();
        }
    }

    public void checkLoadingOperations() {
        checkLoadingOperations(false);
    }

    public void checkLoadingOperations(boolean z) {
        if (z) {
            this.workerQueue.cancelRunnable(this.checkOperationsRunnable);
            this.checkOperationsRunnable.run();
        } else {
            if (this.checkOperationsScheduled) {
                return;
            }
            this.checkOperationsScheduled = true;
            this.workerQueue.cancelRunnable(this.checkOperationsRunnable);
            this.workerQueue.postRunnable(this.checkOperationsRunnable, 20L);
        }
    }

    public int getCount() {
        return this.allOperations.size();
    }

    public int getPosition(FileLoadOperation fileLoadOperation) {
        return this.allOperations.indexOf(fileLoadOperation);
    }

    public boolean remove(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation == null) {
            return false;
        }
        return this.allOperations.remove(fileLoadOperation);
    }
}
