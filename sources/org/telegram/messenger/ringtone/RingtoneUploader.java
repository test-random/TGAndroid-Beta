package org.telegram.messenger.ringtone;

import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class RingtoneUploader implements NotificationCenter.NotificationCenterDelegate {
    private boolean canceled;
    private int currentAccount;
    public final String filePath;

    public RingtoneUploader(String str, int i) {
        this.currentAccount = i;
        this.filePath = str;
        subscribe();
        FileLoader.getInstance(i).uploadFile(str, false, true, 50331648);
    }

    public void lambda$didReceivedNotification$0(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            onComplete((TLRPC.Document) tLObject);
        } else {
            error(tL_error);
        }
        unsubscribe();
    }

    public void lambda$didReceivedNotification$1(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                RingtoneUploader.this.lambda$didReceivedNotification$0(tLObject, tL_error);
            }
        });
    }

    public void lambda$error$2(TLRPC.TL_error tL_error) {
        if (tL_error.text.equals("RINGTONE_DURATION_TOO_LONG")) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneDurationMax)));
        } else if (tL_error.text.equals("RINGTONE_SIZE_TOO_BIG")) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneSizeMax / 1024)));
        } else {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 4, LocaleController.formatString("InvalidFormatError", R.string.InvalidFormatError, new Object[0]), LocaleController.getString(R.string.ErrorRingtoneInvalidFormat));
        }
    }

    private void onComplete(TLRPC.Document document) {
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, document, false);
    }

    private void subscribe() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
    }

    private void unsubscribe() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
    }

    public void cancel() {
        this.canceled = true;
        unsubscribe();
        FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.filePath, false);
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, null, true);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileUploaded) {
            String str = (String) objArr[0];
            if (!this.canceled && str.equals(this.filePath)) {
                TLRPC.InputFile inputFile = (TLRPC.InputFile) objArr[1];
                TLRPC.TL_account_uploadRingtone tL_account_uploadRingtone = new TLRPC.TL_account_uploadRingtone();
                tL_account_uploadRingtone.file = inputFile;
                tL_account_uploadRingtone.file_name = inputFile.name;
                String fileExtension = FileLoader.getFileExtension(new File(inputFile.name));
                tL_account_uploadRingtone.mime_type = fileExtension;
                tL_account_uploadRingtone.mime_type = "ogg".equals(fileExtension) ? "audio/ogg" : "audio/mpeg";
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_uploadRingtone, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        RingtoneUploader.this.lambda$didReceivedNotification$1(tLObject, tL_error);
                    }
                });
            }
        }
    }

    public void error(final TLRPC.TL_error tL_error) {
        unsubscribe();
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, null, true);
        if (tL_error != null) {
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() {
                @Override
                public final void run() {
                    RingtoneUploader.this.lambda$error$2(tL_error);
                }
            });
        }
    }
}
