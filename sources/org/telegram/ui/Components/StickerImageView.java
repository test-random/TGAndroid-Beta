package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.NotificationCenter;

public class StickerImageView extends BackupImageView implements NotificationCenter.NotificationCenterDelegate {
    int currentAccount;
    int stickerNum;
    String stickerPackName;

    public StickerImageView(Context context, int i) {
        super(context);
        this.stickerPackName = "tg_placeholders_android";
        this.currentAccount = i;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad) {
            if (this.stickerPackName.equals((String) objArr[0])) {
                setSticker();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    public void setSticker() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerImageView.setSticker():void");
    }

    public void setStickerNum(int i) {
        if (this.stickerNum != i) {
            this.stickerNum = i;
            setSticker();
        }
    }

    public void setStickerPackName(String str) {
        this.stickerPackName = str;
    }
}
