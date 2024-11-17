package org.telegram.messenger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.RemoteControlClient;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import java.io.File;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

public class MusicPlayerService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private static final int ID_NOTIFICATION = 5;
    public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
    public static final String NOTIFY_SEEK = "org.telegram.android.musicplayer.seek";
    private static boolean supportBigNotifications;
    private static boolean supportLockScreenControls;
    private Bitmap albumArtPlaceholder;
    private AudioManager audioManager;
    private boolean foregroundServiceIsStarted;
    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction())) {
                MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    };
    private ImageReceiver imageReceiver;
    private String loadingFilePath;
    private MediaSession mediaSession;
    private int notificationMessageID;
    private PlaybackState.Builder playbackState;
    private RemoteControlClient remoteControlClient;

    static {
        int i = Build.VERSION.SDK_INT;
        boolean z = true;
        supportBigNotifications = true;
        if (i >= 21 && TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.miui.ui.version.code"))) {
            z = false;
        }
        supportLockScreenControls = z;
    }

    private void createNotification(MessageObject messageObject, boolean z) {
        StringBuilder sb;
        String str;
        Bitmap bitmap;
        Bitmap bitmap2;
        Bitmap bitmap3;
        TLRPC.Chat chat;
        Bitmap bitmap4;
        String str2;
        Bitmap bitmap5;
        String str3;
        int i;
        MediaSession.Token sessionToken;
        Notification.MediaStyle mediaSession;
        Notification.Builder category;
        int i2;
        String str4;
        String str5;
        PlaybackState.Builder state;
        Notification.Action build;
        Notification.Action build2;
        Notification.Action build3;
        PlaybackState build4;
        MediaMetadata.Builder putBitmap;
        MediaMetadata.Builder putString;
        MediaMetadata.Builder putString2;
        MediaMetadata.Builder putLong;
        MediaMetadata.Builder putString3;
        MediaMetadata.Builder putString4;
        MediaMetadata build5;
        int i3;
        PlaybackState.Builder state2;
        Notification.Action build6;
        Notification.Action build7;
        Notification.Action build8;
        String musicTitle = messageObject.getMusicTitle();
        String musicAuthor = messageObject.getMusicAuthor();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, (Class<?>) LaunchActivity.class);
        if (messageObject.isMusic()) {
            intent.setAction("com.tmessages.openplayer");
            intent.addCategory("android.intent.category.LAUNCHER");
        } else if (messageObject.isVoice() || messageObject.isRoundVideo()) {
            intent.setAction("android.intent.action.VIEW");
            TLRPC.Peer peer = messageObject.messageOwner.peer_id;
            boolean z2 = peer instanceof TLRPC.TL_peerUser;
            long j = z2 ? peer.user_id : peer instanceof TLRPC.TL_peerChat ? peer.chat_id : peer instanceof TLRPC.TL_peerChannel ? peer.channel_id : 0L;
            if (j != 0) {
                if (z2) {
                    sb = new StringBuilder();
                    str = "tg://openmessage?user_id=";
                } else {
                    sb = new StringBuilder();
                    str = "tg://openmessage?chat_id=";
                }
                sb.append(str);
                sb.append(j);
                sb.append("&message_id=");
                sb.append(messageObject.getId());
                intent.setData(Uri.parse(sb.toString()));
            }
        }
        PendingIntent activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, fixIntentFlags(33554432));
        long duration = (long) (messageObject.getDuration() * 1000.0d);
        if (messageObject.isMusic()) {
            String artworkUrl = messageObject.getArtworkUrl(true);
            String artworkUrl2 = messageObject.getArtworkUrl(false);
            bitmap2 = audioInfo != null ? audioInfo.getSmallCover() : null;
            bitmap3 = audioInfo != null ? audioInfo.getCover() : null;
            this.loadingFilePath = null;
            this.imageReceiver.setImageBitmap((Drawable) null);
            if (bitmap2 != null || TextUtils.isEmpty(artworkUrl)) {
                this.loadingFilePath = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(messageObject.getDocument()).getAbsolutePath();
            } else {
                boolean z3 = !z;
                bitmap3 = loadArtworkFromUrl(artworkUrl2, true, z3);
                if (bitmap3 == null) {
                    bitmap2 = loadArtworkFromUrl(artworkUrl, false, z3);
                    bitmap4 = bitmap2;
                } else {
                    bitmap2 = loadArtworkFromUrl(artworkUrl2, false, z3);
                }
            }
            bitmap4 = bitmap3;
        } else if (messageObject.isVoice() || messageObject.isRoundVideo()) {
            long senderId = messageObject.getSenderId();
            if (messageObject.isFromUser()) {
                TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(senderId));
                if (user != 0) {
                    musicTitle = UserObject.getUserName(user);
                    chat = user;
                    boolean z4 = !z;
                    bitmap = getAvatarBitmap(chat, true, z4);
                    bitmap2 = getAvatarBitmap(chat, false, z4);
                }
                bitmap = null;
                bitmap2 = null;
            } else {
                TLRPC.Chat chat2 = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-senderId));
                if (chat2 != null) {
                    musicTitle = chat2.title;
                    chat = chat2;
                    boolean z42 = !z;
                    bitmap = getAvatarBitmap(chat, true, z42);
                    bitmap2 = getAvatarBitmap(chat, false, z42);
                }
                bitmap = null;
                bitmap2 = null;
            }
            bitmap3 = (bitmap != null || bitmap2 == null) ? bitmap : bitmap2;
            musicAuthor = LocaleController.getString(messageObject.isVoice() ? R.string.AttachAudio : R.string.AttachRound);
            bitmap4 = bitmap3;
        } else {
            bitmap4 = null;
            bitmap2 = null;
        }
        int i4 = Build.VERSION.SDK_INT;
        Bitmap bitmap6 = bitmap2;
        if (i4 >= 21) {
            boolean z5 = !MediaController.getInstance().isMessagePaused();
            PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous").setComponent(new ComponentName(this, (Class<?>) MusicPlayerReceiver.class)), fixIntentFlags(301989888));
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 0, new Intent(this, getClass()).setAction(getPackageName() + ".STOP_PLAYER"), fixIntentFlags(301989888));
            PendingIntent broadcast2 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(z5 ? "org.telegram.android.musicplayer.pause" : "org.telegram.android.musicplayer.play").setComponent(new ComponentName(this, (Class<?>) MusicPlayerReceiver.class)), fixIntentFlags(301989888));
            Bitmap bitmap7 = bitmap4;
            PendingIntent broadcast3 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next").setComponent(new ComponentName(this, (Class<?>) MusicPlayerReceiver.class)), fixIntentFlags(301989888));
            PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.seek").setComponent(new ComponentName(this, (Class<?>) MusicPlayerReceiver.class)), fixIntentFlags(301989888));
            Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
            sessionToken = this.mediaSession.getSessionToken();
            mediaSession = mediaStyle.setMediaSession(sessionToken);
            if (messageObject.isMusic()) {
                mediaSession.setShowActionsInCompactView(0, 1, 2);
            } else if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                mediaSession.setShowActionsInCompactView(0);
            }
            Notification.Builder builder = new Notification.Builder(this);
            category = builder.setSmallIcon(R.drawable.player).setOngoing(z5).setContentTitle(musicTitle).setContentText(musicAuthor).setSubText((audioInfo == null || !messageObject.isMusic()) ? null : audioInfo.getAlbum()).setContentIntent(activity).setDeleteIntent(service).setShowWhen(false).setCategory("transport");
            category.setPriority(2).setStyle(mediaSession);
            if (i4 >= 26) {
                NotificationsController.checkOtherNotificationsChannel();
                builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (bitmap6 != null) {
                builder.setLargeIcon(bitmap6);
            } else {
                builder.setLargeIcon(this.albumArtPlaceholder);
            }
            String string = LocaleController.getString(R.string.Next);
            String string2 = LocaleController.getString(R.string.AccDescrPrevious);
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                i2 = i4;
                state2 = this.playbackState.setState(6, 0L, 1.0f);
                state2.setActions(0L);
                if (messageObject.isMusic()) {
                    build8 = new Notification.Action.Builder(R.drawable.ic_action_previous, string2, broadcast).build();
                    builder.addAction(build8);
                }
                build6 = new Notification.Action.Builder(R.drawable.loading_animation2, LocaleController.getString(R.string.Loading), (PendingIntent) null).build();
                builder.addAction(build6);
                if (messageObject.isMusic()) {
                    build7 = new Notification.Action.Builder(R.drawable.ic_action_next, string, broadcast3).build();
                    builder.addAction(build7);
                }
                str4 = musicTitle;
                str5 = musicAuthor;
            } else {
                i2 = i4;
                long j2 = messageObject.isMusic() ? 822L : 774L;
                str4 = musicTitle;
                str5 = musicAuthor;
                state = this.playbackState.setState(z5 ? 3 : 2, MediaController.getInstance().getPlayingMessageObject().audioProgressSec * 1000, getPlaybackSpeed(z5, messageObject));
                state.setActions(j2);
                String string3 = LocaleController.getString(z5 ? R.string.AccActionPause : R.string.AccActionPlay);
                if (messageObject.isMusic()) {
                    build3 = new Notification.Action.Builder(R.drawable.ic_action_previous, string2, broadcast).build();
                    builder.addAction(build3);
                }
                build = new Notification.Action.Builder(z5 ? R.drawable.ic_action_pause : R.drawable.ic_action_play, string3, broadcast2).build();
                builder.addAction(build);
                if (messageObject.isMusic()) {
                    build2 = new Notification.Action.Builder(R.drawable.ic_action_next, string, broadcast3).build();
                    builder.addAction(build2);
                }
            }
            MediaSession mediaSession2 = this.mediaSession;
            build4 = this.playbackState.build();
            mediaSession2.setPlaybackState(build4);
            bitmap5 = bitmap7;
            putBitmap = new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", bitmap5);
            str3 = str5;
            putString = putBitmap.putString("android.media.metadata.ALBUM_ARTIST", str3);
            putString2 = putString.putString("android.media.metadata.ARTIST", str3);
            putLong = putString2.putLong("android.media.metadata.DURATION", duration);
            str2 = str4;
            putString3 = putLong.putString("android.media.metadata.TITLE", str2);
            putString4 = putString3.putString("android.media.metadata.ALBUM", (audioInfo == null || !messageObject.isMusic()) ? null : audioInfo.getAlbum());
            MediaSession mediaSession3 = this.mediaSession;
            build5 = putString4.build();
            mediaSession3.setMetadata(build5);
            builder.setVisibility(1);
            Notification build9 = builder.build();
            if (i2 < 31) {
                i3 = 5;
                if (!z5) {
                    stopForeground(false);
                    ((NotificationManager) getSystemService("notification")).notify(i3, build9);
                }
                startForeground(i3, build9);
            } else if (this.foregroundServiceIsStarted) {
                i3 = 5;
                ((NotificationManager) getSystemService("notification")).notify(i3, build9);
            } else {
                this.foregroundServiceIsStarted = true;
                i3 = 5;
                startForeground(i3, build9);
            }
        } else {
            str2 = musicTitle;
            bitmap5 = bitmap4;
            str3 = musicAuthor;
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_small_notification);
            RemoteViews remoteViews2 = supportBigNotifications ? new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_big_notification) : null;
            Notification build10 = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.player).setContentIntent(activity).setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(str2).build();
            build10.contentView = remoteViews;
            if (supportBigNotifications) {
                build10.bigContentView = remoteViews2;
            }
            setListeners(remoteViews);
            if (supportBigNotifications) {
                setListeners(remoteViews2);
            }
            RemoteViews remoteViews3 = build10.contentView;
            int i5 = R.id.player_album_art;
            if (bitmap6 != null) {
                remoteViews3.setImageViewBitmap(i5, bitmap6);
                if (supportBigNotifications) {
                    build10.bigContentView.setImageViewBitmap(i5, bitmap6);
                }
            } else {
                remoteViews3.setImageViewResource(i5, R.drawable.nocover_small);
                if (supportBigNotifications) {
                    build10.bigContentView.setImageViewResource(i5, R.drawable.nocover_big);
                }
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                RemoteViews remoteViews4 = build10.contentView;
                int i6 = R.id.player_pause;
                remoteViews4.setViewVisibility(i6, 8);
                RemoteViews remoteViews5 = build10.contentView;
                int i7 = R.id.player_play;
                remoteViews5.setViewVisibility(i7, 8);
                RemoteViews remoteViews6 = build10.contentView;
                int i8 = R.id.player_next;
                remoteViews6.setViewVisibility(i8, 8);
                RemoteViews remoteViews7 = build10.contentView;
                int i9 = R.id.player_previous;
                remoteViews7.setViewVisibility(i9, 8);
                RemoteViews remoteViews8 = build10.contentView;
                int i10 = R.id.player_progress_bar;
                remoteViews8.setViewVisibility(i10, 0);
                if (supportBigNotifications) {
                    build10.bigContentView.setViewVisibility(i6, 8);
                    build10.bigContentView.setViewVisibility(i7, 8);
                    build10.bigContentView.setViewVisibility(i8, 8);
                    build10.bigContentView.setViewVisibility(i9, 8);
                    build10.bigContentView.setViewVisibility(i10, 0);
                }
            } else {
                RemoteViews remoteViews9 = build10.contentView;
                int i11 = R.id.player_progress_bar;
                remoteViews9.setViewVisibility(i11, 8);
                if (messageObject.isMusic()) {
                    build10.contentView.setViewVisibility(R.id.player_next, 0);
                    build10.contentView.setViewVisibility(R.id.player_previous, 0);
                } else {
                    build10.bigContentView.setViewVisibility(R.id.player_next, 8);
                    build10.bigContentView.setViewVisibility(R.id.player_previous, 8);
                }
                if (supportBigNotifications) {
                    if (messageObject.isMusic()) {
                        build10.bigContentView.setViewVisibility(R.id.player_next, 0);
                        build10.bigContentView.setViewVisibility(R.id.player_previous, 0);
                        i = 8;
                    } else {
                        i = 8;
                        build10.bigContentView.setViewVisibility(R.id.player_next, 8);
                        build10.bigContentView.setViewVisibility(R.id.player_previous, 8);
                    }
                    build10.bigContentView.setViewVisibility(i11, i);
                } else {
                    i = 8;
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    RemoteViews remoteViews10 = build10.contentView;
                    int i12 = R.id.player_pause;
                    remoteViews10.setViewVisibility(i12, i);
                    RemoteViews remoteViews11 = build10.contentView;
                    int i13 = R.id.player_play;
                    remoteViews11.setViewVisibility(i13, 0);
                    if (supportBigNotifications) {
                        build10.bigContentView.setViewVisibility(i12, i);
                        build10.bigContentView.setViewVisibility(i13, 0);
                    }
                } else {
                    RemoteViews remoteViews12 = build10.contentView;
                    int i14 = R.id.player_pause;
                    remoteViews12.setViewVisibility(i14, 0);
                    RemoteViews remoteViews13 = build10.contentView;
                    int i15 = R.id.player_play;
                    remoteViews13.setViewVisibility(i15, i);
                    if (supportBigNotifications) {
                        build10.bigContentView.setViewVisibility(i14, 0);
                        build10.bigContentView.setViewVisibility(i15, i);
                    }
                }
            }
            RemoteViews remoteViews14 = build10.contentView;
            int i16 = R.id.player_song_name;
            remoteViews14.setTextViewText(i16, str2);
            RemoteViews remoteViews15 = build10.contentView;
            int i17 = R.id.player_author_name;
            remoteViews15.setTextViewText(i17, str3);
            if (supportBigNotifications) {
                build10.bigContentView.setTextViewText(i16, str2);
                build10.bigContentView.setTextViewText(i17, str3);
                build10.bigContentView.setTextViewText(R.id.player_album_title, (audioInfo == null || TextUtils.isEmpty(audioInfo.getAlbum())) ? "" : audioInfo.getAlbum());
            }
            build10.flags |= 2;
            startForeground(5, build10);
        }
        if (this.remoteControlClient != null) {
            int id = MediaController.getInstance().getPlayingMessageObject().getId();
            if (this.notificationMessageID != id) {
                this.notificationMessageID = id;
                RemoteControlClient.MetadataEditor editMetadata = this.remoteControlClient.editMetadata(true);
                editMetadata.putString(2, str3);
                editMetadata.putString(7, str2);
                if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getAlbum())) {
                    editMetadata.putString(1, audioInfo.getAlbum());
                }
                editMetadata.putLong(9, MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration * 1000);
                if (bitmap5 != null) {
                    try {
                        editMetadata.putBitmap(100, bitmap5);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                editMetadata.apply();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (MusicPlayerService.this.remoteControlClient == null || MediaController.getInstance().getPlayingMessageObject() == null) {
                            return;
                        }
                        if (MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration == -9223372036854775807L) {
                            AndroidUtilities.runOnUIThread(this, 500L);
                            return;
                        }
                        RemoteControlClient.MetadataEditor editMetadata2 = MusicPlayerService.this.remoteControlClient.editMetadata(false);
                        editMetadata2.putLong(9, MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration * 1000);
                        editMetadata2.apply();
                        MusicPlayerService.this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, Math.max(MediaController.getInstance().getPlayingMessageObject().audioProgressSec * 1000, 100L), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                    }
                }, 1000L);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.remoteControlClient.setPlaybackState(8);
                return;
            }
            RemoteControlClient.MetadataEditor editMetadata2 = this.remoteControlClient.editMetadata(false);
            editMetadata2.putLong(9, MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration * 1000);
            editMetadata2.apply();
            this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, Math.max(MediaController.getInstance().getPlayingMessageObject().audioProgressSec * 1000, 100L), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
        }
    }

    private int fixIntentFlags(int i) {
        return (Build.VERSION.SDK_INT >= 31 || !XiaomiUtilities.isMIUI()) ? i : i & (-100663297);
    }

    private android.graphics.Bitmap getAvatarBitmap(org.telegram.tgnet.TLObject r12, boolean r13, boolean r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MusicPlayerService.getAvatarBitmap(org.telegram.tgnet.TLObject, boolean, boolean):android.graphics.Bitmap");
    }

    private float getPlaybackSpeed(boolean z, MessageObject messageObject) {
        if (!z) {
            return 0.0f;
        }
        if (messageObject == null) {
            return 1.0f;
        }
        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
            return MediaController.getInstance().getPlaybackSpeed(false);
        }
        return 1.0f;
    }

    public void lambda$onCreate$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (!z || TextUtils.isEmpty(this.loadingFilePath)) {
            return;
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            createNotification(playingMessageObject, true);
        }
        this.loadingFilePath = null;
    }

    private Bitmap loadArtworkFromUrl(String str, boolean z, boolean z2) {
        ImageLoader.getHttpFileName(str);
        File httpFilePath = ImageLoader.getHttpFilePath(str, "jpg");
        if (httpFilePath.exists()) {
            return ImageLoader.loadBitmap(httpFilePath.getAbsolutePath(), null, z ? 600.0f : 100.0f, z ? 600.0f : 100.0f, false);
        }
        if (z2) {
            this.loadingFilePath = httpFilePath.getAbsolutePath();
            if (!z) {
                this.imageReceiver.setImage(str, "48_48", null, null, 0L);
            }
        } else {
            this.loadingFilePath = null;
        }
        return null;
    }

    public void updatePlaybackState(long j) {
        PlaybackState.Builder state;
        PlaybackState build;
        PlaybackState.Builder state2;
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        boolean z = !MediaController.getInstance().isMessagePaused();
        if (MediaController.getInstance().isDownloadingCurrentMessage()) {
            state2 = this.playbackState.setState(6, 0L, 1.0f);
            state2.setActions(0L);
        } else {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            long j2 = (playingMessageObject == null || !playingMessageObject.isMusic()) ? 774L : 822L;
            state = this.playbackState.setState(z ? 3 : 2, j, getPlaybackSpeed(z, playingMessageObject));
            state.setActions(j2);
        }
        MediaSession mediaSession = this.mediaSession;
        build = this.playbackState.build();
        mediaSession.setPlaybackState(build);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject playingMessageObject;
        String str;
        String str2;
        if (i == NotificationCenter.messagePlayingPlayStateChanged) {
            MessageObject playingMessageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject2 != null) {
                createNotification(playingMessageObject2, false);
                return;
            } else {
                stopSelf();
                return;
            }
        }
        if (i == NotificationCenter.messagePlayingDidSeek) {
            if (MediaController.getInstance().getPlayingMessageObject() == null) {
                return;
            }
            long round = Math.round(r3.audioPlayerDuration * ((Float) objArr[1]).floatValue()) * 1000;
            updatePlaybackState(round);
            RemoteControlClient remoteControlClient = this.remoteControlClient;
            if (remoteControlClient != null) {
                remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, round, MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                return;
            }
            return;
        }
        if (i == NotificationCenter.httpFileDidLoad) {
            String str3 = (String) objArr[0];
            playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject == null || (str2 = this.loadingFilePath) == null || !str2.equals(str3)) {
                return;
            }
        } else {
            if (i != NotificationCenter.fileLoaded) {
                return;
            }
            String str4 = (String) objArr[0];
            playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject == null || (str = this.loadingFilePath) == null || !str.equals(str4)) {
                return;
            }
        }
        createNotification(playingMessageObject, false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService("audio");
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
        }
        ImageReceiver imageReceiver = new ImageReceiver(null);
        this.imageReceiver = imageReceiver;
        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                MusicPlayerService.this.lambda$onCreate$0(imageReceiver2, z, z2, z3);
            }

            @Override
            public void didSetImageBitmap(int i2, String str, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i2, str, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
            this.playbackState = new PlaybackState.Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Bitmap.Config.ARGB_8888);
            Drawable drawable = getResources().getDrawable(R.drawable.nocover_big);
            drawable.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            drawable.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new MediaSession.Callback() {
                @Override
                public void onPause() {
                    MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
                }

                @Override
                public void onPlay() {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                }

                @Override
                public void onSeekTo(long j) {
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject != null) {
                        MediaController.getInstance().seekToProgress(playingMessageObject, ((float) (j / 1000)) / ((float) playingMessageObject.getDuration()));
                        MusicPlayerService.this.updatePlaybackState(j);
                    }
                }

                @Override
                public void onSkipToNext() {
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject == null || !playingMessageObject.isMusic()) {
                        return;
                    }
                    MediaController.getInstance().playNextMessage();
                }

                @Override
                public void onSkipToPrevious() {
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject == null || !playingMessageObject.isMusic()) {
                        return;
                    }
                    MediaController.getInstance().playPreviousMessage();
                }

                @Override
                public void onStop() {
                }
            });
            this.mediaSession.setActive(true);
        }
        registerReceiver(this.headsetPlugReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(this.headsetPlugReceiver);
        super.onDestroy();
        stopForeground(true);
        RemoteControlClient remoteControlClient = this.remoteControlClient;
        if (remoteControlClient != null) {
            RemoteControlClient.MetadataEditor editMetadata = remoteControlClient.editMetadata(true);
            editMetadata.clear();
            editMetadata.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession.release();
        }
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoaded);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            try {
                if ((getPackageName() + ".STOP_PLAYER").equals(intent.getAction())) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    MusicPlayerService.this.stopSelf();
                }
            });
            return 1;
        }
        if (supportLockScreenControls) {
            ComponentName componentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
            try {
                if (this.remoteControlClient == null) {
                    this.audioManager.registerMediaButtonEventReceiver(componentName);
                    Intent intent2 = new Intent("android.intent.action.MEDIA_BUTTON");
                    intent2.setComponent(componentName);
                    RemoteControlClient remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(this, 0, intent2, fixIntentFlags(33554432)));
                    this.remoteControlClient = remoteControlClient;
                    this.audioManager.registerRemoteControlClient(remoteControlClient);
                }
                this.remoteControlClient.setTransportControlFlags(189);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        createNotification(playingMessageObject, false);
        return 1;
    }

    public void setListeners(RemoteViews remoteViews) {
        remoteViews.setOnClickPendingIntent(R.id.player_previous, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous"), fixIntentFlags(167772160)));
        remoteViews.setOnClickPendingIntent(R.id.player_close, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.close"), fixIntentFlags(167772160)));
        remoteViews.setOnClickPendingIntent(R.id.player_pause, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.pause"), fixIntentFlags(167772160)));
        remoteViews.setOnClickPendingIntent(R.id.player_next, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next"), fixIntentFlags(167772160)));
        remoteViews.setOnClickPendingIntent(R.id.player_play, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.play"), fixIntentFlags(167772160)));
    }
}
