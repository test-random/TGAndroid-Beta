package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.StateSet;
import androidx.appcompat.widget.AppCompatImageHelper$$ExternalSyntheticApiModelOutline0;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;

public abstract class TranscribeButton {
    private static final int[] pressedState = {16842910, 16842919};
    private static HashMap transcribeOperationsByDialogPosition;
    private static HashMap transcribeOperationsById;
    private static ArrayList videoTranscriptionsOpen;
    private float a;
    private final AnimatedFloat animatedDrawLock;
    private float b;
    private float backgroundBack;
    private int backgroundColor;
    private Paint backgroundPaint;
    private Path boundsPath;
    private Paint clipLockPaint;
    private int color;
    private int diameter;
    private boolean drawLock;
    private int iconColor;
    private RLottieDrawable inIconDrawable;
    private int inIconDrawableAlpha;
    private boolean isOpen;
    private boolean loading;
    private final AnimatedFloat loadingFloat;
    private Path lockHandlePath;
    private float lockHandlePathDensity;
    private Paint lockPaint;
    private Paint lockStrokePaint;
    private RLottieDrawable outIconDrawable;
    private int outIconDrawableAlpha;
    private ChatMessageCell parent;
    private boolean premium;
    private android.graphics.Rect pressBounds;
    private Path progressClipPath;
    private int radius;
    private int rippleColor;
    private SeekBarWaveform seekBar;
    private float[] segments;
    private Drawable selectorDrawable;
    private boolean shouldBeOpen;
    private Paint strokePaint;
    private boolean clickedToOpen = false;
    private boolean pressed = false;
    private long pressId = 0;
    private final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
    private long start = SystemClock.elapsedRealtime();
    private android.graphics.Rect bounds = new android.graphics.Rect(0, 0, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));

    private static class LoadingPointsDrawable extends Drawable {
        private int lastColor;
        private RLottieDrawable lottie;
        private Paint paint;

        public LoadingPointsDrawable(TextPaint textPaint) {
            this.paint = textPaint;
            float textSize = textPaint.getTextSize() * 0.89f;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.dots_loading, "dots_loading", (int) textSize, (int) (textSize * 1.25f)) {
                @Override
                public boolean hasParentView() {
                    return true;
                }
            };
            this.lottie = rLottieDrawable;
            rLottieDrawable.setAutoRepeat(1);
            this.lottie.setCurrentFrame((int) ((((float) SystemClock.elapsedRealtime()) / 16.0f) % 60.0f));
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.start();
        }

        @Override
        public void draw(Canvas canvas) {
            int color = this.paint.getColor();
            if (color != this.lastColor) {
                setColor(color);
                this.lastColor = color;
            }
            this.lottie.draw(canvas);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
        }

        public void setColor(int i) {
            this.lottie.beginApplyLayerColors();
            this.lottie.setLayerColor("Comp 1.**", i);
            this.lottie.commitApplyLayerColors();
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.updateCurrentFrame(0L, false);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public static class LoadingPointsSpan extends ImageSpan {
        private static LoadingPointsDrawable drawable;

        public LoadingPointsSpan() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranscribeButton.LoadingPointsSpan.<init>():void");
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            float textSize = textPaint.getTextSize() * 0.89f;
            int i = (int) (0.02f * textSize);
            getDrawable().setBounds(0, i, (int) textSize, ((int) (textSize * 1.25f)) + i);
            super.updateDrawState(textPaint);
        }
    }

    public TranscribeButton(ChatMessageCell chatMessageCell, SeekBarWaveform seekBarWaveform) {
        boolean z = false;
        this.parent = chatMessageCell;
        this.seekBar = seekBarWaveform;
        android.graphics.Rect rect = new android.graphics.Rect(this.bounds);
        this.pressBounds = rect;
        rect.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.transcribe_out, "transcribe_out", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.outIconDrawable = rLottieDrawable;
        rLottieDrawable.setCurrentFrame(0);
        this.outIconDrawable.setCallback(chatMessageCell);
        this.outIconDrawable.setOnFinishCallback(new Runnable() {
            @Override
            public final void run() {
                TranscribeButton.this.lambda$new$0();
            }
        }, 19);
        this.outIconDrawable.setAllowDecodeSingleFrame(true);
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(R.raw.transcribe_in, "transcribe_in", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.inIconDrawable = rLottieDrawable2;
        rLottieDrawable2.setCurrentFrame(0);
        this.inIconDrawable.setCallback(chatMessageCell);
        this.inIconDrawable.setMasterParent(chatMessageCell);
        this.inIconDrawable.setOnFinishCallback(new Runnable() {
            @Override
            public final void run() {
                TranscribeButton.this.lambda$new$1();
            }
        }, 19);
        this.inIconDrawable.setAllowDecodeSingleFrame(true);
        this.isOpen = false;
        this.shouldBeOpen = false;
        if (chatMessageCell.getMessageObject() != null && UserConfig.getInstance(chatMessageCell.getMessageObject().currentAccount).isPremium()) {
            z = true;
        }
        this.premium = z;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.loadingFloat = new AnimatedFloat(chatMessageCell, 250L, cubicBezierInterpolator);
        this.animatedDrawLock = new AnimatedFloat(chatMessageCell, 250L, cubicBezierInterpolator);
    }

    private void addCorner(Path path, int i, int i2, int i3, int i4, float f, float f2) {
        RectF rectF;
        float f3;
        float f4;
        float f5;
        float clamp = MathUtils.clamp(f, 0.0f, 1.0f);
        float clamp2 = MathUtils.clamp(f2, 0.0f, 1.0f) - clamp;
        if (clamp2 <= 0.0f) {
            return;
        }
        if (i4 != 1) {
            if (i4 == 2) {
                AndroidUtilities.rectTmp.set(i - i3, i2 - i3, i, i2);
            } else if (i4 == 3) {
                rectF = AndroidUtilities.rectTmp;
                f3 = i;
                f4 = i2 - i3;
                f5 = i + i3;
                rectF.set(f3, f4, f5, i2);
            } else if (i4 == 4) {
                rectF = AndroidUtilities.rectTmp;
                f3 = i;
                f4 = i2;
                i += i3;
            }
            path.addArc(AndroidUtilities.rectTmp, ((i4 * 90) - 180) + (clamp * 90.0f), clamp2 * 90.0f);
        }
        rectF = AndroidUtilities.rectTmp;
        f3 = i - i3;
        f4 = i2;
        f5 = i;
        i2 += i3;
        rectF.set(f3, f4, f5, i2);
        path.addArc(AndroidUtilities.rectTmp, ((i4 * 90) - 180) + (clamp * 90.0f), clamp2 * 90.0f);
    }

    private void addCorner(Path path, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
        if (f <= f2) {
            float f5 = f4 - f3;
            addCorner(path, i, i2, i3, i4, Math.max(0.0f, f - f3) / f5, (Math.min(f2, f4) - f3) / f5);
        } else {
            float f6 = f4 - f3;
            addCorner(path, i, i2, i3, i4, (f - f3) / f6, 1.0f);
            addCorner(path, i, i2, i3, i4, 0.0f, (f2 - f3) / f6);
        }
    }

    private void addLine(Path path, int i, int i2, int i3, int i4, float f, float f2) {
        if (i == i3 && i2 == i4) {
            return;
        }
        float clamp = MathUtils.clamp(f, 0.0f, 1.0f);
        if (MathUtils.clamp(f2, 0.0f, 1.0f) - clamp <= 0.0f) {
            return;
        }
        path.moveTo(AndroidUtilities.lerp(i, i3, clamp), AndroidUtilities.lerp(i2, i4, clamp));
        path.lineTo(AndroidUtilities.lerp(i, i3, r9), AndroidUtilities.lerp(i2, i4, r9));
    }

    private void addLine(Path path, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
        float max;
        float min;
        if (i == i3 && i2 == i4) {
            return;
        }
        if (f > f2) {
            float f5 = f4 - f3;
            addLine(path, i, i2, i3, i4, (f - f3) / f5, 1.0f);
            min = (f2 - f3) / f5;
            max = 0.0f;
        } else {
            float f6 = f4 - f3;
            max = Math.max(0.0f, f - f3) / f6;
            min = (Math.min(f2, f4) - f3) / f6;
        }
        addLine(path, i, i2, i3, i4, max, min);
    }

    public static boolean canTranscribeTrial(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return false;
        }
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(messageObject.currentAccount);
        MessagesController messagesController = MessagesController.getInstance(messageObject.currentAccount);
        if (isFreeTranscribeInChat(messageObject)) {
            return true;
        }
        if (messagesController.transcribeAudioTrialWeeklyNumber <= 0 || messageObject.getDuration() > messagesController.transcribeAudioTrialDurationMax) {
            return false;
        }
        return messagesController.transcribeAudioTrialCooldownUntil == 0 || connectionsManager.getCurrentTime() > messagesController.transcribeAudioTrialCooldownUntil || messagesController.transcribeAudioTrialCurrentNumber > 0;
    }

    private void drawLock(Canvas canvas) {
        float f = this.animatedDrawLock.set((!this.drawLock || this.isOpen || this.loading) ? false : true);
        if (f <= 0.0f) {
            return;
        }
        canvas.save();
        canvas.translate(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(12.0f));
        if (this.clipLockPaint == null) {
            Paint paint = new Paint(1);
            this.clipLockPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, -AndroidUtilities.dp(0.4f), AndroidUtilities.dp(6.666f), AndroidUtilities.dp(8.733f));
        canvas.scale(f, f, rectF.centerX(), rectF.centerY());
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.clipLockPaint);
        if (this.lockPaint == null) {
            this.lockPaint = new Paint(1);
        }
        this.lockPaint.setColor(this.iconColor);
        int i = (int) (f * 255.0f);
        this.lockPaint.setAlpha(i);
        rectF.set(0.0f, AndroidUtilities.dp(3.33f), AndroidUtilities.dp(6.666f), AndroidUtilities.dp(8.33f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(1.33f), AndroidUtilities.dp(1.33f), this.lockPaint);
        if (this.lockHandlePath == null || Math.abs(this.lockHandlePathDensity - AndroidUtilities.density) > 0.1f) {
            this.lockHandlePathDensity = AndroidUtilities.density;
            Path path = new Path();
            this.lockHandlePath = path;
            path.moveTo(AndroidUtilities.dp(1.66f), AndroidUtilities.dp(3.33f));
            this.lockHandlePath.lineTo(AndroidUtilities.dp(1.66f), AndroidUtilities.dp(2.0f));
            rectF.set(AndroidUtilities.dp(1.66f), AndroidUtilities.dp(0.33f), AndroidUtilities.dp(4.99f), AndroidUtilities.dp(3.6599998f));
            this.lockHandlePath.arcTo(rectF, -180.0f, 180.0f, false);
            this.lockHandlePath.lineTo(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(3.33f));
        }
        if (this.lockStrokePaint == null) {
            Paint paint2 = new Paint(1);
            this.lockStrokePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
        }
        this.lockStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.lockStrokePaint.setColor(this.iconColor);
        this.lockStrokePaint.setAlpha(i);
        canvas.drawPath(this.lockHandlePath, this.lockStrokePaint);
        canvas.restore();
    }

    public static boolean finishTranscription(final MessageObject messageObject, final long j, final String str) {
        try {
            HashMap hashMap = transcribeOperationsById;
            MessageObject messageObject2 = (hashMap == null || !hashMap.containsKey(Long.valueOf(j))) ? null : (MessageObject) transcribeOperationsById.remove(Long.valueOf(j));
            if (messageObject == null) {
                messageObject = messageObject2;
            }
            if (messageObject != null && messageObject.messageOwner != null) {
                HashMap hashMap2 = transcribeOperationsByDialogPosition;
                if (hashMap2 != null) {
                    hashMap2.remove(Integer.valueOf(reqInfoHash(messageObject)));
                }
                messageObject.messageOwner.voiceTranscriptionFinal = true;
                MessagesStorage.getInstance(messageObject.currentAccount).updateMessageVoiceTranscription(messageObject.getDialogId(), messageObject.getId(), str, messageObject.messageOwner);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TranscribeButton.lambda$finishTranscription$8(MessageObject.this, j, str);
                    }
                });
                return true;
            }
        } catch (Exception unused) {
        }
        return false;
    }

    private float[] getSegments(long j) {
        if (this.segments == null) {
            this.segments = new float[2];
        }
        long j2 = j % 5400;
        float[] fArr = this.segments;
        float f = ((float) (1520 * j2)) / 5400.0f;
        fArr[0] = f - 20.0f;
        fArr[1] = f;
        for (int i = 0; i < 4; i++) {
            float[] fArr2 = this.segments;
            fArr2[1] = fArr2[1] + (this.interpolator.getInterpolation(((float) (j2 - (i * 1350))) / 667.0f) * 250.0f);
            float[] fArr3 = this.segments;
            fArr3[0] = fArr3[0] + (this.interpolator.getInterpolation(((float) (j2 - (r6 + 667))) / 667.0f) * 250.0f);
        }
        return this.segments;
    }

    public static int getTranscribeTrialCount(int i) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(i);
        MessagesController messagesController = MessagesController.getInstance(i);
        if (messagesController.transcribeAudioTrialWeeklyNumber <= 0) {
            return 0;
        }
        return (messagesController.transcribeAudioTrialCooldownUntil == 0 || connectionsManager.getCurrentTime() > messagesController.transcribeAudioTrialCooldownUntil) ? messagesController.transcribeAudioTrialWeeklyNumber : messagesController.transcribeAudioTrialCurrentNumber;
    }

    public static boolean isFreeTranscribeInChat(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return false;
        }
        MessagesController messagesController = MessagesController.getInstance(messageObject.currentAccount);
        TLRPC.Chat chat = messagesController.getChat(Long.valueOf(messageObject.getChatId()));
        return ChatObject.isMegagroup(chat) && chat.level >= messagesController.groupTranscribeLevelMin;
    }

    public static boolean isTranscribing(MessageObject messageObject) {
        HashMap hashMap;
        TLRPC.Message message;
        HashMap hashMap2 = transcribeOperationsByDialogPosition;
        return (hashMap2 != null && (hashMap2.containsValue(messageObject) || transcribeOperationsByDialogPosition.containsKey(Integer.valueOf(reqInfoHash(messageObject))))) || !((hashMap = transcribeOperationsById) == null || messageObject == null || (message = messageObject.messageOwner) == null || !hashMap.containsKey(Long.valueOf(message.voiceTranscriptionId)));
    }

    public static boolean isVideoTranscriptionOpen(MessageObject messageObject) {
        return videoTranscriptionsOpen != null && (!messageObject.isRoundVideo() || videoTranscriptionsOpen.contains(Integer.valueOf(reqInfoHash(messageObject))));
    }

    public static void lambda$finishTranscription$8(MessageObject messageObject, long j, String str) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(messageObject.currentAccount);
        int i = NotificationCenter.voiceTranscriptionUpdate;
        Long valueOf = Long.valueOf(j);
        Boolean bool = Boolean.TRUE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, messageObject, valueOf, str, bool, bool);
    }

    public void lambda$new$0() {
        this.outIconDrawable.stop();
        this.inIconDrawable.stop();
        this.shouldBeOpen = true;
        this.isOpen = true;
        this.inIconDrawable.setCurrentFrame(0);
    }

    public void lambda$new$1() {
        this.inIconDrawable.stop();
        this.outIconDrawable.stop();
        this.shouldBeOpen = false;
        this.isOpen = false;
        this.outIconDrawable.setCurrentFrame(0);
    }

    public static void lambda$showOffTranscribe$9(MessageObject messageObject) {
        NotificationCenter.getInstance(messageObject.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.voiceTranscriptionUpdate, messageObject);
    }

    public static void lambda$transcribePressed$2(int i, MessageObject messageObject) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
        int i2 = NotificationCenter.voiceTranscriptionUpdate;
        Boolean bool = Boolean.TRUE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, messageObject, null, null, bool, bool);
    }

    public static void lambda$transcribePressed$3(ChatMessageCell.ChatMessageCellDelegate chatMessageCellDelegate, TLRPC.TL_messages_transcribedAudio tL_messages_transcribedAudio) {
        if (chatMessageCellDelegate != null) {
            chatMessageCellDelegate.needShowPremiumBulletin(tL_messages_transcribedAudio.trial_remains_num > 0 ? 1 : 2);
        }
    }

    public static void lambda$transcribePressed$4(MessageObject messageObject, ChatMessageCell.ChatMessageCellDelegate chatMessageCellDelegate, int i) {
        HashMap hashMap = transcribeOperationsByDialogPosition;
        if (hashMap != null) {
            hashMap.remove(Integer.valueOf(reqInfoHash(messageObject)));
        }
        if (chatMessageCellDelegate != null) {
            chatMessageCellDelegate.needShowPremiumBulletin(3);
        }
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.voiceTranscriptionUpdate, messageObject);
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateTranscriptionLock, new Object[0]);
    }

    public static void lambda$transcribePressed$6(final int i, final ChatMessageCell.ChatMessageCellDelegate chatMessageCellDelegate, final MessageObject messageObject, long j, long j2, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        long j3;
        boolean z;
        String str;
        final String str2 = "";
        if (tLObject instanceof TLRPC.TL_messages_transcribedAudio) {
            final TLRPC.TL_messages_transcribedAudio tL_messages_transcribedAudio = (TLRPC.TL_messages_transcribedAudio) tLObject;
            String str3 = tL_messages_transcribedAudio.text;
            long j4 = tL_messages_transcribedAudio.transcription_id;
            z = !tL_messages_transcribedAudio.pending;
            if (!TextUtils.isEmpty(str3)) {
                str2 = str3;
            } else if (!z) {
                str2 = null;
            }
            if ((tL_messages_transcribedAudio.flags & 2) != 0) {
                MessagesController.getInstance(i).updateTranscribeAudioTrialCurrentNumber(tL_messages_transcribedAudio.trial_remains_num);
                MessagesController.getInstance(i).updateTranscribeAudioTrialCooldownUntil(tL_messages_transcribedAudio.trial_remains_until_date);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TranscribeButton.lambda$transcribePressed$3(ChatMessageCell.ChatMessageCellDelegate.this, tL_messages_transcribedAudio);
                    }
                });
            }
            if (transcribeOperationsById == null) {
                transcribeOperationsById = new HashMap();
            }
            transcribeOperationsById.put(Long.valueOf(j4), messageObject);
            messageObject.messageOwner.voiceTranscriptionId = j4;
            j3 = j4;
        } else {
            if (tL_error != null && (str = tL_error.text) != null && str.startsWith("FLOOD_WAIT_")) {
                MessagesController.getInstance(i).updateTranscribeAudioTrialCurrentNumber(0);
                MessagesController.getInstance(i).updateTranscribeAudioTrialCooldownUntil(ConnectionsManager.getInstance(i).getCurrentTime() + Utilities.parseInt((CharSequence) tL_error.text).intValue());
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TranscribeButton.lambda$transcribePressed$4(MessageObject.this, chatMessageCellDelegate, i);
                    }
                });
                return;
            }
            j3 = 0;
            z = true;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime() - j;
        openVideoTranscription(messageObject);
        TLRPC.Message message = messageObject.messageOwner;
        message.voiceTranscriptionOpen = true;
        message.voiceTranscriptionFinal = z;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Transcription request sent, received final=" + z + " id=" + j3 + " text=" + str2);
        }
        final long j5 = j3;
        MessagesStorage.getInstance(i).updateMessageVoiceTranscription(j2, i2, str2, messageObject.messageOwner);
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TranscribeButton.finishTranscription(MessageObject.this, j5, str2);
                }
            }, Math.max(0L, 350 - elapsedRealtime));
        }
    }

    public static void lambda$transcribePressed$7(int i, MessageObject messageObject) {
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.voiceTranscriptionUpdate, messageObject, null, null, Boolean.FALSE, null);
    }

    public static void openVideoTranscription(MessageObject messageObject) {
        if (messageObject == null || isVideoTranscriptionOpen(messageObject)) {
            return;
        }
        if (videoTranscriptionsOpen == null) {
            videoTranscriptionsOpen = new ArrayList(1);
        }
        videoTranscriptionsOpen.add(Integer.valueOf(reqInfoHash(messageObject)));
    }

    private static int reqInfoHash(MessageObject messageObject) {
        if (messageObject == null) {
            return 0;
        }
        return Objects.hash(Integer.valueOf(messageObject.currentAccount), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId()));
    }

    public static void resetVideoTranscriptionsOpen() {
        ArrayList arrayList = videoTranscriptionsOpen;
        if (arrayList != null) {
            arrayList.clear();
        }
    }

    public static void showOffTranscribe(MessageObject messageObject) {
        showOffTranscribe(messageObject, true);
    }

    public static void showOffTranscribe(final MessageObject messageObject, boolean z) {
        TLRPC.Message message;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            return;
        }
        message.voiceTranscriptionForce = true;
        MessagesStorage.getInstance(messageObject.currentAccount).updateMessageVoiceTranscriptionOpen(messageObject.getDialogId(), messageObject.getId(), messageObject.messageOwner);
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TranscribeButton.lambda$showOffTranscribe$9(MessageObject.this);
                }
            });
        }
    }

    public static boolean showTranscribeLock(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null || isFreeTranscribeInChat(messageObject) || !TextUtils.isEmpty(messageObject.messageOwner.voiceTranscription)) {
            return false;
        }
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(messageObject.currentAccount);
        MessagesController messagesController = MessagesController.getInstance(messageObject.currentAccount);
        return !UserConfig.getInstance(messageObject.currentAccount).isPremium() && messagesController.transcribeAudioTrialCooldownUntil != 0 && connectionsManager.getCurrentTime() <= messagesController.transcribeAudioTrialCooldownUntil && messagesController.transcribeAudioTrialCurrentNumber <= 0;
    }

    private static void transcribePressed(final MessageObject messageObject, boolean z, final ChatMessageCell.ChatMessageCellDelegate chatMessageCellDelegate) {
        Runnable runnable;
        if (messageObject == null || messageObject.messageOwner == null || !messageObject.isSent()) {
            return;
        }
        final int i = messageObject.currentAccount;
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        TLRPC.InputPeer inputPeer = MessagesController.getInstance(i).getInputPeer(messageObject.messageOwner.peer_id);
        final long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
        TLRPC.Message message = messageObject.messageOwner;
        final int i2 = message.id;
        if (!z) {
            HashMap hashMap = transcribeOperationsByDialogPosition;
            if (hashMap != null) {
                hashMap.remove(Integer.valueOf(reqInfoHash(messageObject)));
            }
            messageObject.messageOwner.voiceTranscriptionOpen = false;
            MessagesStorage.getInstance(i).updateMessageVoiceTranscriptionOpen(peerDialogId, i2, messageObject.messageOwner);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    TranscribeButton.lambda$transcribePressed$7(i, messageObject);
                }
            };
        } else {
            if (message.voiceTranscription == null || !message.voiceTranscriptionFinal) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("sending Transcription request, msg_id=" + i2 + " dialog_id=" + peerDialogId);
                }
                TLRPC.TL_messages_transcribeAudio tL_messages_transcribeAudio = new TLRPC.TL_messages_transcribeAudio();
                tL_messages_transcribeAudio.peer = inputPeer;
                tL_messages_transcribeAudio.msg_id = i2;
                if (transcribeOperationsByDialogPosition == null) {
                    transcribeOperationsByDialogPosition = new HashMap();
                }
                transcribeOperationsByDialogPosition.put(Integer.valueOf(reqInfoHash(messageObject)), messageObject);
                ConnectionsManager.getInstance(i).sendRequest(tL_messages_transcribeAudio, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TranscribeButton.lambda$transcribePressed$6(i, chatMessageCellDelegate, messageObject, elapsedRealtime, peerDialogId, i2, tLObject, tL_error);
                    }
                }, !UserConfig.getInstance(i).isPremium() ? 1024 : 0);
                return;
            }
            openVideoTranscription(messageObject);
            messageObject.messageOwner.voiceTranscriptionOpen = true;
            MessagesStorage.getInstance(i).updateMessageVoiceTranscriptionOpen(peerDialogId, i2, messageObject.messageOwner);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    TranscribeButton.lambda$transcribePressed$2(i, messageObject);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void draw(Canvas canvas, float f) {
        RLottieDrawable rLottieDrawable;
        this.pressBounds.set(this.bounds.left - AndroidUtilities.dp(8.0f), this.bounds.top - AndroidUtilities.dp(8.0f), this.bounds.right + AndroidUtilities.dp(8.0f), this.bounds.bottom + AndroidUtilities.dp(8.0f));
        Path path = this.boundsPath;
        if (path == null) {
            this.boundsPath = new Path();
        } else {
            path.rewind();
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(this.bounds);
        Path path2 = this.boundsPath;
        float f2 = this.radius;
        path2.addRoundRect(rectF, f2, f2, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.boundsPath);
        float f3 = this.backgroundBack * f;
        if (f3 > 0.0f) {
            drawGradientBackground(canvas, this.bounds, f3);
        }
        Paint paint = this.backgroundPaint;
        if (paint != null) {
            int alpha = paint.getAlpha();
            this.backgroundPaint.setAlpha((int) (alpha * f));
            canvas.drawRect(this.bounds, this.backgroundPaint);
            this.backgroundPaint.setAlpha(alpha);
        }
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setBounds(this.bounds);
            this.selectorDrawable.draw(canvas);
        }
        canvas.restore();
        float f4 = this.loadingFloat.set(this.loading ? 1.0f : 0.0f);
        if (f4 > 0.0f) {
            float[] segments = getSegments(((float) (SystemClock.elapsedRealtime() - this.start)) * 0.75f);
            Path path3 = this.progressClipPath;
            if (path3 == null) {
                this.progressClipPath = new Path();
            } else {
                path3.rewind();
            }
            float max = Math.max(40.0f * f4, segments[1] - segments[0]);
            float f5 = segments[0] + ((1.0f - f4) * max * (this.loading ? 0.0f : 1.0f));
            float f6 = (max * f4) + f5;
            float f7 = f5 % 360.0f;
            float f8 = f6 % 360.0f;
            if (f7 < 0.0f) {
                f7 += 360.0f;
            }
            float f9 = f7;
            if (f8 < 0.0f) {
                f8 += 360.0f;
            }
            float f10 = f8;
            Path path4 = this.progressClipPath;
            int centerX = this.bounds.centerX();
            android.graphics.Rect rect = this.bounds;
            int i = rect.top;
            addLine(path4, centerX, i, rect.right - this.radius, i, f9, f10, 0.0f, this.a);
            Path path5 = this.progressClipPath;
            android.graphics.Rect rect2 = this.bounds;
            addCorner(path5, rect2.right, rect2.top, this.diameter, 1, f9, f10, this.a, this.b);
            Path path6 = this.progressClipPath;
            android.graphics.Rect rect3 = this.bounds;
            int i2 = rect3.right;
            int i3 = rect3.top;
            int i4 = this.radius;
            int i5 = rect3.bottom - i4;
            float f11 = this.b;
            addLine(path6, i2, i3 + i4, i2, i5, f9, f10, f11, 180.0f - f11);
            Path path7 = this.progressClipPath;
            android.graphics.Rect rect4 = this.bounds;
            addCorner(path7, rect4.right, rect4.bottom, this.diameter, 2, f9, f10, 180.0f - this.b, 180.0f - this.a);
            Path path8 = this.progressClipPath;
            android.graphics.Rect rect5 = this.bounds;
            int i6 = rect5.right;
            int i7 = this.radius;
            int i8 = rect5.bottom;
            int i9 = rect5.left + i7;
            float f12 = this.a;
            addLine(path8, i6 - i7, i8, i9, i8, f9, f10, 180.0f - f12, f12 + 180.0f);
            Path path9 = this.progressClipPath;
            android.graphics.Rect rect6 = this.bounds;
            addCorner(path9, rect6.left, rect6.bottom, this.diameter, 3, f9, f10, this.a + 180.0f, this.b + 180.0f);
            Path path10 = this.progressClipPath;
            android.graphics.Rect rect7 = this.bounds;
            int i10 = rect7.left;
            int i11 = rect7.bottom;
            int i12 = this.radius;
            int i13 = rect7.top + i12;
            float f13 = this.b;
            addLine(path10, i10, i11 - i12, i10, i13, f9, f10, f13 + 180.0f, 360.0f - f13);
            Path path11 = this.progressClipPath;
            android.graphics.Rect rect8 = this.bounds;
            addCorner(path11, rect8.left, rect8.top, this.diameter, 4, f9, f10, 360.0f - this.b, 360.0f - this.a);
            Path path12 = this.progressClipPath;
            android.graphics.Rect rect9 = this.bounds;
            addLine(path12, rect9.left + this.radius, rect9.top, rect9.centerX(), this.bounds.top, f9, f10, 360.0f - this.a, 360.0f);
            this.strokePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
            int alpha2 = this.strokePaint.getAlpha();
            this.strokePaint.setAlpha((int) (alpha2 * f));
            canvas.drawPath(this.progressClipPath, this.strokePaint);
            this.strokePaint.setAlpha(alpha2);
            this.parent.invalidate();
        }
        canvas.save();
        canvas.translate(this.bounds.centerX() + AndroidUtilities.dp(-13.0f), this.bounds.centerY() + AndroidUtilities.dp(-13.0f));
        canvas.saveLayerAlpha(0.0f, 0.0f, AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f), 255, 31);
        if (this.isOpen) {
            this.inIconDrawable.setAlpha((int) (this.inIconDrawableAlpha * f));
            rLottieDrawable = this.inIconDrawable;
        } else {
            this.outIconDrawable.setAlpha((int) (this.outIconDrawableAlpha * f));
            rLottieDrawable = this.outIconDrawable;
        }
        rLottieDrawable.draw(canvas);
        drawLock(canvas);
        canvas.restore();
        canvas.restore();
    }

    public abstract void drawGradientBackground(Canvas canvas, android.graphics.Rect rect, float f);

    public int height() {
        return this.bounds.height();
    }

    protected abstract void onOpen();

    public void onTap() {
        boolean z;
        ChatMessageCell.ChatMessageCellDelegate delegate;
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell == null) {
            return;
        }
        int i = 0;
        this.clickedToOpen = false;
        boolean z2 = this.shouldBeOpen;
        boolean z3 = !z2;
        if (z2) {
            setOpen(false, true);
            setLoading(false, true);
            z = true;
        } else {
            z = !this.loading;
            if ((this.premium || canTranscribeTrial(chatMessageCell.getMessageObject())) && this.parent.getMessageObject().isSent()) {
                setLoading(true, true);
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.selectorDrawable;
            if (AppCompatImageHelper$$ExternalSyntheticApiModelOutline0.m(drawable)) {
                drawable.setState(StateSet.NOTHING);
                this.parent.invalidate();
            }
        }
        this.pressed = false;
        if (z) {
            if (this.premium || !z3) {
                if (z3) {
                    this.clickedToOpen = true;
                }
            } else if (!canTranscribeTrial(this.parent.getMessageObject()) && (this.parent.getMessageObject() == null || this.parent.getMessageObject().messageOwner == null || TextUtils.isEmpty(this.parent.getMessageObject().messageOwner.voiceTranscription))) {
                if (this.parent.getDelegate() != null) {
                    if (MessagesController.getInstance(this.parent.currentAccount).transcribeAudioTrialWeeklyNumber > 0) {
                        delegate = this.parent.getDelegate();
                        i = 3;
                    } else {
                        delegate = this.parent.getDelegate();
                    }
                    delegate.needShowPremiumBulletin(i);
                    return;
                }
                return;
            }
            transcribePressed(this.parent.getMessageObject(), z3, this.parent.getDelegate());
        }
    }

    public boolean onTouch(int i, float f, float f2) {
        if (i == 1 || i == 3) {
            if (this.pressed && i == 1) {
                onTap();
                return true;
            }
            this.pressed = false;
            return false;
        }
        if (!this.pressBounds.contains((int) f, (int) f2)) {
            return false;
        }
        if (i == 0) {
            this.pressed = true;
        }
        if (this.pressed && Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.selectorDrawable;
            if (AppCompatImageHelper$$ExternalSyntheticApiModelOutline0.m(drawable)) {
                drawable.setHotspot(f, f2);
                this.selectorDrawable.setState(pressedState);
                this.parent.invalidate();
            }
        }
        return true;
    }

    public void setBounds(int i, int i2, int i3, int i4, int i5) {
        if (i3 != this.bounds.width() || i4 != this.bounds.height()) {
            float f = i3 / 2.0f;
            float f2 = i5;
            float f3 = i4 / 2.0f;
            this.a = (float) ((Math.atan((f - f2) / f3) * 180.0d) / 3.141592653589793d);
            this.b = (float) ((Math.atan(f / (f3 - f2)) * 180.0d) / 3.141592653589793d);
        }
        this.bounds.set(i, i2, i + i3, i2 + i4);
        int min = Math.min(Math.min(i3, i4) / 2, i5);
        this.radius = min;
        this.diameter = min * 2;
    }

    public void setColor(int i, int i2, boolean z, float f) {
        boolean z2 = this.color != i;
        this.color = i;
        this.iconColor = i;
        int alphaComponent = ColorUtils.setAlphaComponent(i, (int) (Color.alpha(i) * 0.156f));
        this.backgroundColor = alphaComponent;
        this.backgroundBack = f;
        this.rippleColor = Theme.blendOver(alphaComponent, ColorUtils.setAlphaComponent(i, (int) (Color.alpha(i) * (Theme.isCurrentThemeDark() ? 0.3f : 0.2f))));
        if (this.backgroundPaint == null) {
            this.backgroundPaint = new Paint();
        }
        this.backgroundPaint.setColor(this.backgroundColor);
        this.backgroundPaint.setAlpha((int) (r1.getAlpha() * (1.0f - f)));
        if (z2 || this.selectorDrawable == null) {
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, this.rippleColor);
            this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
            createSimpleSelectorRoundRectDrawable.setCallback(this.parent);
        }
        if (z2) {
            this.inIconDrawable.beginApplyLayerColors();
            this.inIconDrawable.setLayerColor("Artboard Outlines.**", this.iconColor);
            this.inIconDrawable.commitApplyLayerColors();
            this.inIconDrawable.setAllowDecodeSingleFrame(true);
            this.inIconDrawable.updateCurrentFrame(0L, false);
            RLottieDrawable rLottieDrawable = this.inIconDrawable;
            int alpha = Color.alpha(i);
            this.inIconDrawableAlpha = alpha;
            rLottieDrawable.setAlpha(alpha);
            this.outIconDrawable.beginApplyLayerColors();
            this.outIconDrawable.setLayerColor("Artboard Outlines.**", this.iconColor);
            this.outIconDrawable.commitApplyLayerColors();
            this.outIconDrawable.setAllowDecodeSingleFrame(true);
            this.outIconDrawable.updateCurrentFrame(0L, false);
            RLottieDrawable rLottieDrawable2 = this.outIconDrawable;
            int alpha2 = Color.alpha(i);
            this.outIconDrawableAlpha = alpha2;
            rLottieDrawable2.setAlpha(alpha2);
        }
        if (this.strokePaint == null) {
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.strokePaint.setStrokeCap(Paint.Cap.ROUND);
        }
        this.strokePaint.setColor(i);
    }

    public void setLoading(boolean z, boolean z2) {
        this.loading = z;
        this.seekBar.setLoading(z);
        AnimatedFloat animatedFloat = this.loadingFloat;
        if (!z2) {
            animatedFloat.set(this.loading, true);
        } else if (animatedFloat.get() <= 0.0f) {
            this.start = SystemClock.elapsedRealtime();
        }
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell != null) {
            chatMessageCell.invalidate();
        }
    }

    public void setLock(boolean z, boolean z2) {
        ChatMessageCell chatMessageCell;
        if (this.drawLock != z && (chatMessageCell = this.parent) != null) {
            chatMessageCell.invalidate();
        }
        this.drawLock = z;
        if (z2) {
            return;
        }
        this.animatedDrawLock.set(z, true);
    }

    public void setOpen(boolean z, boolean z2) {
        RLottieDrawable rLottieDrawable;
        if (!this.shouldBeOpen && z && this.clickedToOpen) {
            this.clickedToOpen = false;
            onOpen();
        }
        boolean z3 = this.shouldBeOpen;
        this.shouldBeOpen = z;
        if (z2) {
            if (z && !z3) {
                this.isOpen = false;
                this.inIconDrawable.setCurrentFrame(0);
                this.outIconDrawable.setCurrentFrame(0);
                rLottieDrawable = this.outIconDrawable;
            } else if (!z && z3) {
                this.isOpen = true;
                this.outIconDrawable.setCurrentFrame(0);
                this.inIconDrawable.setCurrentFrame(0);
                rLottieDrawable = this.inIconDrawable;
            }
            rLottieDrawable.start();
        } else {
            this.isOpen = z;
            this.inIconDrawable.stop();
            this.outIconDrawable.stop();
            this.inIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.setCurrentFrame(0);
        }
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell != null) {
            chatMessageCell.invalidate();
        }
    }

    public int width() {
        return this.bounds.width();
    }
}
