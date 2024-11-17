package org.telegram.ui.Components.voip;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class GroupCallStatusIcon {
    Callback callback;
    RLottieImageView iconView;
    boolean isSpeaking;
    boolean lastMuted;
    boolean lastRaisedHand;
    RLottieDrawable micDrawable;
    private boolean mutedByMe;
    TLRPC.TL_groupCallParticipant participant;
    RLottieDrawable shakeHandDrawable;
    boolean updateRunnableScheduled;
    private Runnable shakeHandCallback = new Runnable() {
        @Override
        public final void run() {
            GroupCallStatusIcon.this.lambda$new$0();
        }
    };
    private Runnable raiseHandCallback = new Runnable() {
        @Override
        public final void run() {
            GroupCallStatusIcon.this.lambda$new$1();
        }
    };
    private Runnable updateRunnable = new Runnable() {
        @Override
        public final void run() {
            GroupCallStatusIcon.this.lambda$new$2();
        }
    };
    private Runnable checkRaiseRunnable = new Runnable() {
        @Override
        public final void run() {
            GroupCallStatusIcon.this.lambda$new$3();
        }
    };

    public interface Callback {
        void onStatusChanged();
    }

    public GroupCallStatusIcon() {
        int i = R.raw.voice_mini;
        this.micDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
        int i2 = R.raw.hand_2;
        this.shakeHandDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), true, null);
    }

    public void lambda$new$0() {
        this.shakeHandDrawable.setOnFinishCallback(null, 0);
        this.micDrawable.setOnFinishCallback(null, 0);
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setAnimation(this.micDrawable);
        }
    }

    public void lambda$new$1() {
        int i;
        int nextInt = Utilities.random.nextInt(100);
        int i2 = 120;
        if (nextInt < 32) {
            i = 0;
        } else {
            i = 240;
            if (nextInt < 64) {
                i2 = 240;
                i = 120;
            } else {
                i2 = 420;
                if (nextInt >= 97) {
                    i = 540;
                    if (nextInt == 98) {
                        i2 = 540;
                        i = 420;
                    } else {
                        i2 = 720;
                    }
                }
            }
        }
        this.shakeHandDrawable.setCustomEndFrame(i2);
        this.shakeHandDrawable.setOnFinishCallback(this.shakeHandCallback, i2 - 1);
        this.shakeHandDrawable.setCurrentFrame(i);
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setAnimation(this.shakeHandDrawable);
            this.iconView.playAnimation();
        }
    }

    public void lambda$new$2() {
        this.isSpeaking = false;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onStatusChanged();
        }
        this.updateRunnableScheduled = false;
    }

    public void lambda$new$3() {
        updateIcon(true);
    }

    public boolean isMutedByAdmin() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participant;
        return (tL_groupCallParticipant == null || !tL_groupCallParticipant.muted || tL_groupCallParticipant.can_self_unmute) ? false : true;
    }

    public boolean isMutedByMe() {
        return this.mutedByMe;
    }

    public boolean isSpeaking() {
        return this.isSpeaking;
    }

    public void setAmplitude(double d) {
        if (d > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                Callback callback = this.callback;
                if (callback != null) {
                    callback.onStatusChanged();
                }
            }
            AndroidUtilities.runOnUIThread(this.updateRunnable, 500L);
            this.updateRunnableScheduled = true;
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
        if (callback == null) {
            this.isSpeaking = false;
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            AndroidUtilities.cancelRunOnUIThread(this.raiseHandCallback);
            AndroidUtilities.cancelRunOnUIThread(this.checkRaiseRunnable);
            this.micDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setImageView(RLottieImageView rLottieImageView) {
        this.iconView = rLottieImageView;
        updateIcon(false);
    }

    public void setParticipant(TLRPC.TL_groupCallParticipant tL_groupCallParticipant, boolean z) {
        this.participant = tL_groupCallParticipant;
        updateIcon(z);
    }

    public void updateIcon(boolean r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallStatusIcon.updateIcon(boolean):void");
    }
}
