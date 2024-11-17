package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;

public class VideoEncodingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private static VideoEncodingService instance;
    private NotificationCompat.Builder builder;
    int currentAccount;
    private MediaController.VideoConvertMessage currentMessage;
    String currentPath;

    public static boolean isRunning() {
        return instance != null;
    }

    public void lambda$didReceivedNotification$0() {
        MediaController.VideoConvertMessage currentForegroundConverMessage = MediaController.getInstance().getCurrentForegroundConverMessage();
        if (currentForegroundConverMessage != null) {
            setCurrentMessage(currentForegroundConverMessage);
        } else {
            stopSelf();
        }
    }

    private void setCurrentMessage(MediaController.VideoConvertMessage videoConvertMessage) {
        MediaController.VideoConvertMessage videoConvertMessage2 = this.currentMessage;
        if (videoConvertMessage2 == videoConvertMessage) {
            return;
        }
        if (videoConvertMessage2 != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        }
        updateBuilderForMessage(videoConvertMessage);
        this.currentMessage = videoConvertMessage;
        int i = videoConvertMessage.currentAccount;
        this.currentAccount = i;
        this.currentPath = videoConvertMessage.messageObject.messageOwner.attachPath;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileUploadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        if (isRunning()) {
            updateNotification();
        }
    }

    public static void start(boolean z) {
        if (instance == null) {
            try {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, (Class<?>) VideoEncodingService.class));
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (z) {
            MediaController.VideoConvertMessage currentForegroundConverMessage = MediaController.getInstance().getCurrentForegroundConverMessage();
            VideoEncodingService videoEncodingService = instance;
            if (videoEncodingService.currentMessage != currentForegroundConverMessage) {
                if (currentForegroundConverMessage != null) {
                    videoEncodingService.setCurrentMessage(currentForegroundConverMessage);
                } else {
                    videoEncodingService.stopSelf();
                }
            }
        }
    }

    public static void stop() {
        VideoEncodingService videoEncodingService = instance;
        if (videoEncodingService != null) {
            videoEncodingService.stopSelf();
        }
    }

    private void updateBuilderForMessage(MediaController.VideoConvertMessage videoConvertMessage) {
        NotificationCompat.Builder builder;
        int i;
        if (videoConvertMessage == null) {
            return;
        }
        MessageObject messageObject = videoConvertMessage.messageObject;
        boolean z = messageObject != null && MessageObject.isGifMessage(messageObject.messageOwner);
        if (videoConvertMessage.foregroundConversion) {
            builder = this.builder;
            i = R.string.ConvertingVideo;
        } else {
            builder = this.builder;
            i = z ? R.string.SendingGif : R.string.SendingVideo;
        }
        builder.setTicker(LocaleController.getString(i));
        this.builder.setContentText(LocaleController.getString(i));
        this.builder.setProgress(100, 0, true);
    }

    public void updateNotification() {
        try {
            if (MediaController.getInstance().getCurrentForegroundConverMessage() == null) {
                return;
            }
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        String str2;
        if (i != NotificationCenter.fileUploadProgressChanged) {
            if (i == NotificationCenter.fileUploaded || i == NotificationCenter.fileUploadFailed) {
                String str3 = (String) objArr[0];
                if (i2 == this.currentAccount && (str = this.currentPath) != null && str.equals(str3)) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            VideoEncodingService.this.lambda$didReceivedNotification$0();
                        }
                    });
                    return;
                }
                return;
            }
            return;
        }
        String str4 = (String) objArr[0];
        if (i2 == this.currentAccount && (str2 = this.currentPath) != null && str2.equals(str4)) {
            float min = Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue()));
            int i3 = (int) (min * 100.0f);
            this.builder.setProgress(100, i3, i3 == 0);
            updateNotification();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        try {
            stopForeground(true);
        } catch (Throwable unused) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        this.currentMessage = null;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("VideoEncodingService: destroy video service");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        MediaController.VideoConvertMessage currentForegroundConverMessage;
        if (isRunning() || (currentForegroundConverMessage = MediaController.getInstance().getCurrentForegroundConverMessage()) == null) {
            return 2;
        }
        instance = this;
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder = builder;
            builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString(R.string.AppName));
        }
        setCurrentMessage(currentForegroundConverMessage);
        try {
            startForeground(4, this.builder.build());
        } catch (Throwable th) {
            FileLog.e(th);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VideoEncodingService.this.updateNotification();
            }
        });
        return 2;
    }
}
