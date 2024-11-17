package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;

public class SlotsDrawable extends RLottieDrawable {
    private ReelValue center;
    private int[] frameCounts;
    private int[] frameNums;
    private ReelValue left;
    private long[] nativePtrs;
    private boolean playWinAnimation;
    private ReelValue right;
    private int[] secondFrameCounts;
    private int[] secondFrameNums;
    private long[] secondNativePtrs;

    public enum ReelValue {
        bar,
        berries,
        lemon,
        seven,
        sevenWin
    }

    public SlotsDrawable(String str, int i, int i2) {
        super(str, i, i2);
        this.nativePtrs = new long[5];
        this.frameCounts = new int[5];
        this.frameNums = new int[5];
        this.secondNativePtrs = new long[3];
        this.secondFrameCounts = new int[3];
        this.secondFrameNums = new int[3];
        this.loadFrameRunnable = new Runnable() {
            @Override
            public final void run() {
                SlotsDrawable.this.lambda$new$0();
            }
        };
    }

    private void init(int i) {
        int i2 = i - 1;
        ReelValue reelValue = reelValue(i2 & 3);
        ReelValue reelValue2 = reelValue((i2 >> 2) & 3);
        ReelValue reelValue3 = reelValue(i2 >> 4);
        ReelValue reelValue4 = ReelValue.seven;
        if (reelValue == reelValue4 && reelValue2 == reelValue4 && reelValue3 == reelValue4) {
            reelValue = ReelValue.sevenWin;
            reelValue3 = reelValue;
            reelValue2 = reelValue3;
        }
        this.left = reelValue;
        this.center = reelValue2;
        this.right = reelValue3;
    }

    public void lambda$new$0() {
        int frame;
        if (this.isRecycled) {
            return;
        }
        if (this.nativePtr == 0 || (this.isDice == 2 && this.secondNativePtr == 0)) {
            CountDownLatch countDownLatch = this.frameWaitSync;
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            RLottieDrawable.uiHandler.post(this.uiRunnableNoFrame);
            return;
        }
        if (this.backgroundBitmap == null) {
            try {
                this.backgroundBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        if (this.backgroundBitmap != null) {
            try {
                if (this.isDice == 1) {
                    int i = 0;
                    frame = -1;
                    while (true) {
                        long[] jArr = this.nativePtrs;
                        if (i >= jArr.length) {
                            break;
                        }
                        frame = RLottieDrawable.getFrame(jArr[i], this.frameNums[i], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), i == 0);
                        if (i != 0) {
                            int[] iArr = this.frameNums;
                            int i2 = iArr[i] + 1;
                            if (i2 < this.frameCounts[i]) {
                                iArr[i] = i2;
                            } else if (i != 4) {
                                iArr[i] = 0;
                                this.nextFrameIsLast = false;
                                if (this.secondNativePtr != 0) {
                                    this.isDice = 2;
                                }
                            }
                        }
                        i++;
                    }
                } else {
                    if (this.setLastFrame) {
                        int i3 = 0;
                        while (true) {
                            int[] iArr2 = this.secondFrameNums;
                            if (i3 >= iArr2.length) {
                                break;
                            }
                            iArr2[i3] = this.secondFrameCounts[i3] - 1;
                            i3++;
                        }
                    }
                    if (this.playWinAnimation) {
                        int[] iArr3 = this.frameNums;
                        int i4 = iArr3[0] + 1;
                        if (i4 < this.frameCounts[0]) {
                            iArr3[0] = i4;
                        } else {
                            iArr3[0] = -1;
                        }
                    }
                    RLottieDrawable.getFrame(this.nativePtrs[0], Math.max(this.frameNums[0], 0), this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), true);
                    int i5 = 0;
                    while (true) {
                        long[] jArr2 = this.secondNativePtrs;
                        if (i5 >= jArr2.length) {
                            break;
                        }
                        long j = jArr2[i5];
                        int i6 = this.secondFrameNums[i5];
                        if (i6 < 0) {
                            i6 = this.secondFrameCounts[i5] - 1;
                        }
                        RLottieDrawable.getFrame(j, i6, this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                        if (!this.nextFrameIsLast) {
                            int[] iArr4 = this.secondFrameNums;
                            int i7 = iArr4[i5] + 1;
                            if (i7 < this.secondFrameCounts[i5]) {
                                iArr4[i5] = i7;
                            } else {
                                iArr4[i5] = -1;
                            }
                        }
                        i5++;
                    }
                    frame = RLottieDrawable.getFrame(this.nativePtrs[4], this.frameNums[4], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                    int[] iArr5 = this.frameNums;
                    int i8 = iArr5[4] + 1;
                    if (i8 < this.frameCounts[4]) {
                        iArr5[4] = i8;
                    }
                    int[] iArr6 = this.secondFrameNums;
                    if (iArr6[0] == -1 && iArr6[1] == -1 && iArr6[2] == -1) {
                        this.nextFrameIsLast = true;
                        this.autoRepeatPlayCount++;
                    }
                    ReelValue reelValue = this.left;
                    ReelValue reelValue2 = this.right;
                    if (reelValue != reelValue2 || reelValue2 != this.center) {
                        this.frameNums[0] = -1;
                    } else if (this.secondFrameNums[0] == this.secondFrameCounts[0] - 100) {
                        this.playWinAnimation = true;
                        if (reelValue == ReelValue.sevenWin) {
                            WeakReference weakReference = this.onFinishCallback;
                            Runnable runnable = weakReference == null ? null : (Runnable) weakReference.get();
                            if (runnable != null) {
                                AndroidUtilities.runOnUIThread(runnable);
                            }
                        }
                    }
                }
                if (frame == -1) {
                    RLottieDrawable.uiHandler.post(this.uiRunnableNoFrame);
                    CountDownLatch countDownLatch2 = this.frameWaitSync;
                    if (countDownLatch2 != null) {
                        countDownLatch2.countDown();
                        return;
                    }
                    return;
                }
                this.nextRenderingBitmap = this.backgroundBitmap;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        RLottieDrawable.uiHandler.post(this.uiRunnable);
        CountDownLatch countDownLatch3 = this.frameWaitSync;
        if (countDownLatch3 != null) {
            countDownLatch3.countDown();
        }
    }

    public void lambda$setBaseDice$1() {
        this.loadingInBackground = false;
        if (this.secondLoadingInBackground || !this.destroyAfterLoading) {
            return;
        }
        recycle(true);
    }

    public static void lambda$setBaseDice$2(TLRPC.Document document, int i, MessageObject messageObject, ChatMessageCell chatMessageCell, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        DownloadController.getInstance(i).addLoadingFileObserver(FileLoader.getAttachFileName(document), messageObject, chatMessageCell);
        FileLoader.getInstance(i).loadFile(document, tL_messages_stickerSet, 1, 1);
    }

    public void lambda$setBaseDice$3() {
        this.loadingInBackground = false;
    }

    public void lambda$setBaseDice$4(int i, ChatMessageCell chatMessageCell) {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle(true);
            return;
        }
        this.nativePtr = this.nativePtrs[0];
        DownloadController.getInstance(i).removeLoadingFileObserver(chatMessageCell);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public void lambda$setBaseDice$5(final TLRPC.TL_messages_stickerSet tL_messages_stickerSet, final int i, final MessageObject messageObject, final ChatMessageCell chatMessageCell) {
        Runnable runnable;
        int i2;
        if (this.destroyAfterLoading) {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$1();
                }
            };
        } else {
            int i3 = 0;
            boolean z = false;
            while (true) {
                long[] jArr = this.nativePtrs;
                if (i3 >= jArr.length) {
                    break;
                }
                if (jArr[i3] == 0) {
                    if (i3 == 0) {
                        i2 = 1;
                    } else if (i3 == 1) {
                        i2 = 8;
                    } else {
                        i2 = 2;
                        if (i3 == 2) {
                            i2 = 14;
                        } else if (i3 == 3) {
                            i2 = 20;
                        }
                    }
                    final TLRPC.Document document = tL_messages_stickerSet.documents.get(i2);
                    String readRes = AndroidUtilities.readRes(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true), 0);
                    if (TextUtils.isEmpty(readRes)) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                SlotsDrawable.lambda$setBaseDice$2(TLRPC.Document.this, i, messageObject, chatMessageCell, tL_messages_stickerSet);
                            }
                        });
                        z = true;
                    } else {
                        this.nativePtrs[i3] = RLottieDrawable.createWithJson(readRes, "dice", this.metaData, null);
                        this.frameCounts[i3] = this.metaData[0];
                    }
                }
                i3++;
            }
            runnable = z ? new Runnable() {
                @Override
                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$3();
                }
            } : new Runnable() {
                @Override
                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$4(i, chatMessageCell);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$setDiceNumber$10(final org.telegram.tgnet.TLRPC.TL_messages_stickerSet r18, final int r19, final org.telegram.messenger.MessageObject r20, final org.telegram.ui.Cells.ChatMessageCell r21, final boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SlotsDrawable.lambda$setDiceNumber$10(org.telegram.tgnet.TLRPC$TL_messages_stickerSet, int, org.telegram.messenger.MessageObject, org.telegram.ui.Cells.ChatMessageCell, boolean):void");
    }

    public void lambda$setDiceNumber$6() {
        this.secondLoadingInBackground = false;
        if (this.loadingInBackground || !this.destroyAfterLoading) {
            return;
        }
        recycle(true);
    }

    public static void lambda$setDiceNumber$7(TLRPC.Document document, int i, MessageObject messageObject, ChatMessageCell chatMessageCell, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        DownloadController.getInstance(i).addLoadingFileObserver(FileLoader.getAttachFileName(document), messageObject, chatMessageCell);
        FileLoader.getInstance(i).loadFile(document, tL_messages_stickerSet, 1, 1);
    }

    public void lambda$setDiceNumber$8() {
        this.secondLoadingInBackground = false;
    }

    public void lambda$setDiceNumber$9(boolean z, int i, ChatMessageCell chatMessageCell) {
        if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
            this.isDice = 2;
            this.setLastFrame = true;
        }
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle(true);
            return;
        }
        this.secondNativePtr = this.secondNativePtrs[0];
        DownloadController.getInstance(i).removeLoadingFileObserver(chatMessageCell);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    private ReelValue reelValue(int i) {
        return i != 0 ? i != 1 ? i != 2 ? ReelValue.seven : ReelValue.lemon : ReelValue.berries : ReelValue.bar;
    }

    @Override
    protected void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
                int i = 0;
                int i2 = 0;
                while (true) {
                    long[] jArr = this.nativePtrs;
                    if (i2 >= jArr.length) {
                        break;
                    }
                    long j = jArr[i2];
                    if (j != 0) {
                        RLottieDrawable.destroy(j);
                        this.nativePtrs[i2] = 0;
                    }
                    i2++;
                }
                while (true) {
                    long[] jArr2 = this.secondNativePtrs;
                    if (i >= jArr2.length) {
                        break;
                    }
                    long j2 = jArr2[i];
                    if (j2 != 0) {
                        RLottieDrawable.destroy(j2);
                        this.secondNativePtrs[i] = 0;
                    }
                    i++;
                }
            }
        }
        if (this.nativePtr == 0 && this.secondNativePtr == 0) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }

    @Override
    public void recycle(boolean z) {
        int i = 0;
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
            return;
        }
        if (this.loadFrameTask != null || this.cacheGenerateTask != null) {
            this.destroyWhenDone = true;
            return;
        }
        int i2 = 0;
        while (true) {
            long[] jArr = this.nativePtrs;
            if (i2 >= jArr.length) {
                break;
            }
            long j = jArr[i2];
            if (j != 0) {
                if (j == this.nativePtr) {
                    this.nativePtr = 0L;
                }
                RLottieDrawable.destroy(this.nativePtrs[i2]);
                this.nativePtrs[i2] = 0;
            }
            i2++;
        }
        while (true) {
            long[] jArr2 = this.secondNativePtrs;
            if (i >= jArr2.length) {
                recycleResources();
                return;
            }
            long j2 = jArr2[i];
            if (j2 != 0) {
                if (j2 == this.secondNativePtr) {
                    this.secondNativePtr = 0L;
                }
                RLottieDrawable.destroy(this.secondNativePtrs[i]);
                this.secondNativePtrs[i] = 0;
            }
            i++;
        }
    }

    public boolean setBaseDice(final ChatMessageCell chatMessageCell, final TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        if (this.nativePtr == 0 && !this.loadingInBackground) {
            this.loadingInBackground = true;
            final MessageObject messageObject = chatMessageCell.getMessageObject();
            final int i = chatMessageCell.getMessageObject().currentAccount;
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$5(tL_messages_stickerSet, i, messageObject, chatMessageCell);
                }
            });
        }
        return true;
    }

    public boolean setDiceNumber(final ChatMessageCell chatMessageCell, int i, final TLRPC.TL_messages_stickerSet tL_messages_stickerSet, final boolean z) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            init(i);
            final MessageObject messageObject = chatMessageCell.getMessageObject();
            final int i2 = chatMessageCell.getMessageObject().currentAccount;
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    SlotsDrawable.this.lambda$setDiceNumber$10(tL_messages_stickerSet, i2, messageObject, chatMessageCell, z);
                }
            });
        }
        return true;
    }
}
