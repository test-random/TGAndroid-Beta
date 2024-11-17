package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.GradientClip;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stars.StarsIntroActivity;

public class StarReactionsOverlay extends View {
    private float accumulatedRippleIntensity;
    private ChatMessageCell cell;
    private final ChatActivity chatActivity;
    private final RectF clickBounds;
    private final GradientClip clip;
    private final AnimatedTextView.AnimatedTextDrawable counter;
    private final AnimatedFloat counterAlpha;
    private boolean counterShown;
    private final int[] effectAssets;
    private final ArrayList effects;
    private float focus;
    private ValueAnimator focusAnimator;
    public boolean hidden;
    private Runnable hideCounterRunnable;
    private long lastRippleTime;
    private final Runnable longPressRunnable;
    private int messageId;
    private final int[] pos;
    private final int[] pos2;
    private boolean pressed;
    private final RectF reactionBounds;
    private final Paint redPaint;
    private final Paint shadowPaint;

    public StarReactionsOverlay(final ChatActivity chatActivity) {
        super(chatActivity.getContext());
        this.pos = new int[2];
        this.pos2 = new int[2];
        this.reactionBounds = new RectF();
        this.clickBounds = new RectF();
        this.shadowPaint = new Paint();
        this.redPaint = new Paint();
        this.counterAlpha = new AnimatedFloat(this, 0L, 420L, CubicBezierInterpolator.EASE_OUT_QUINT);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
        this.counter = animatedTextDrawable;
        this.clip = new GradientClip();
        this.effects = new ArrayList();
        this.effectAssets = new int[]{R.raw.star_reaction_effect1, R.raw.star_reaction_effect2, R.raw.star_reaction_effect3, R.raw.star_reaction_effect4, R.raw.star_reaction_effect5};
        this.chatActivity = chatActivity;
        animatedTextDrawable.setCallback(this);
        animatedTextDrawable.setHacks(false, true, true);
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(40.0f));
        animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
        animatedTextDrawable.setShadowLayer(AndroidUtilities.dp(12.0f), 0.0f, AndroidUtilities.dp(3.5f), 0);
        animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        animatedTextDrawable.setTextColor(-1);
        animatedTextDrawable.setGravity(17);
        this.hideCounterRunnable = new Runnable() {
            @Override
            public final void run() {
                StarReactionsOverlay.this.lambda$new$0();
            }
        };
        this.longPressRunnable = new Runnable() {
            @Override
            public final void run() {
                StarReactionsOverlay.this.lambda$new$1(chatActivity);
            }
        };
    }

    private void checkBalance() {
        String str;
        ChatMessageCell chatMessageCell = this.cell;
        if (chatMessageCell == null || chatMessageCell.getPrimaryMessageObject() == null) {
            return;
        }
        final MessageObject primaryMessageObject = this.cell.getPrimaryMessageObject();
        final StarsController starsController = StarsController.getInstance(this.chatActivity.getCurrentAccount());
        final long pendingPaidReactions = starsController.getPendingPaidReactions(primaryMessageObject);
        if (!starsController.balanceAvailable() || starsController.getBalance(false) >= pendingPaidReactions) {
            return;
        }
        StarsController.getInstance(this.chatActivity.getCurrentAccount()).undoPaidReaction();
        long dialogId = this.chatActivity.getDialogId();
        if (dialogId >= 0) {
            str = UserObject.getForcedFirstName(this.chatActivity.getMessagesController().getUser(Long.valueOf(dialogId)));
        } else {
            TLRPC.Chat chat = this.chatActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
            str = chat == null ? "" : chat.title;
        }
        new StarsIntroActivity.StarsNeededSheet(this.chatActivity.getContext(), this.chatActivity.getResourceProvider(), pendingPaidReactions, 5, str, new Runnable() {
            @Override
            public final void run() {
                StarReactionsOverlay.this.lambda$checkBalance$2(starsController, primaryMessageObject, pendingPaidReactions);
            }
        }).show();
    }

    public void lambda$checkBalance$2(StarsController starsController, MessageObject messageObject, long j) {
        starsController.sendPaidReaction(messageObject, this.chatActivity, j, true, true, null);
    }

    public void lambda$focusTo$3(ValueAnimator valueAnimator) {
        this.focus = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$hide$4() {
        setMessageCell(null);
        clearEffects();
    }

    public void lambda$new$0() {
        this.counterShown = false;
        invalidate();
        checkBalance();
        hide();
    }

    public void lambda$new$1(ChatActivity chatActivity) {
        TLRPC.TL_messageReactions tL_messageReactions;
        ChatMessageCell chatMessageCell = this.cell;
        if (chatMessageCell == null) {
            return;
        }
        chatMessageCell.performHapticFeedback(0);
        onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
        MessageObject primaryMessageObject = this.cell.getPrimaryMessageObject();
        if (primaryMessageObject == null) {
            return;
        }
        TLRPC.Message message = primaryMessageObject.messageOwner;
        ArrayList<TLRPC.MessageReactor> arrayList = (message == null || (tL_messageReactions = message.reactions) == null) ? null : tL_messageReactions.top_reactors;
        StarsController.getInstance(primaryMessageObject.currentAccount).commitPaidReaction();
        TLRPC.ChatFull currentChatInfo = chatActivity.getCurrentChatInfo();
        StarsReactionsSheet starsReactionsSheet = new StarsReactionsSheet(getContext(), chatActivity.getCurrentAccount(), chatActivity.getDialogId(), chatActivity, primaryMessageObject, arrayList, currentChatInfo == null || currentChatInfo.paid_reactions_available, chatActivity.getResourceProvider());
        starsReactionsSheet.setMessageCell(chatActivity, primaryMessageObject.getId(), this.cell);
        starsReactionsSheet.show();
    }

    public void clearEffects() {
        Iterator it = this.effects.iterator();
        while (it.hasNext()) {
            ((RLottieDrawable) it.next()).recycle(true);
        }
        this.effects.clear();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Integer num;
        float f;
        float f2;
        ChatMessageCell chatMessageCell = this.cell;
        if (chatMessageCell == null || !chatMessageCell.isCellAttachedToWindow()) {
            return;
        }
        MessageObject primaryMessageObject = this.cell.getPrimaryMessageObject();
        int i = 0;
        if ((primaryMessageObject != null ? primaryMessageObject.getId() : 0) != this.messageId) {
            setMessageCell(null);
            return;
        }
        float lerp = AndroidUtilities.lerp(1.0f, 1.8f, this.focus);
        float clipTop = this.chatActivity.getClipTop();
        float clipBottom = this.chatActivity.getClipBottom();
        canvas.save();
        canvas.clipRect(0.0f, clipTop * (1.0f - this.focus), getWidth(), getHeight() - (clipBottom * (1.0f - this.focus)));
        getLocationInWindow(this.pos2);
        this.cell.getLocationInWindow(this.pos);
        int[] iArr = this.pos;
        iArr[1] = iArr[1] + ((int) this.chatActivity.drawingChatListViewYoffset);
        canvas.save();
        ReactionsLayoutInBubble.ReactionButton reactionButton = this.cell.reactionsLayoutInBubble.getReactionButton("stars");
        if (reactionButton != null) {
            int[] iArr2 = this.pos;
            int i2 = iArr2[0];
            int[] iArr3 = this.pos2;
            int i3 = i2 - iArr3[0];
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.cell.reactionsLayoutInBubble;
            int i4 = i3 + reactionsLayoutInBubble.x + reactionButton.x;
            int i5 = (iArr2[1] - iArr3[1]) + reactionsLayoutInBubble.y + reactionButton.y;
            float f3 = i4;
            float f4 = i5;
            this.reactionBounds.set(f3, f4, i4 + reactionButton.width, i5 + reactionButton.height);
            AndroidUtilities.scaleRect(this.reactionBounds, lerp, (reactionButton.width * 0.1f) + f3, (reactionButton.height / 2.0f) + f4);
            this.shadowPaint.setColor(0);
            this.shadowPaint.setShadowLayer(AndroidUtilities.dp(12.0f), 0.0f, AndroidUtilities.dp(3.0f), Theme.multAlpha(1426063360, this.focus));
            RectF rectF = this.reactionBounds;
            canvas.drawRoundRect(rectF, rectF.height() / 2.0f, this.reactionBounds.height() / 2.0f, this.shadowPaint);
            canvas.scale(lerp, lerp, f3 + (reactionButton.width * 0.1f), f4 + (reactionButton.height / 2.0f));
            num = Integer.valueOf(reactionButton.reaction.hashCode());
        } else {
            num = null;
        }
        int i6 = this.pos[0];
        int[] iArr4 = this.pos2;
        canvas.translate(i6 - iArr4[0], r5[1] - iArr4[1]);
        this.cell.setScrimReaction(null);
        this.cell.drawReactionsLayout(canvas, 1.0f, num);
        this.cell.drawReactionsLayoutOverlay(canvas, 1.0f);
        this.cell.setScrimReaction(num);
        canvas.restore();
        canvas.restore();
        if (reactionButton != null) {
            this.clickBounds.set(this.reactionBounds);
            this.clickBounds.inset(-AndroidUtilities.dp(42.0f), -AndroidUtilities.dp(42.0f));
            int dp = (int) (AndroidUtilities.dp(90.0f) * lerp);
            while (i < this.effects.size()) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) this.effects.get(i);
                if (rLottieDrawable.getCurrentFrame() >= rLottieDrawable.getFramesCount()) {
                    this.effects.remove(i);
                    i--;
                } else {
                    float f5 = dp / 2.0f;
                    rLottieDrawable.setBounds((int) ((this.reactionBounds.left + (AndroidUtilities.dp(15.0f) * lerp)) - f5), (int) (this.reactionBounds.centerY() - f5), (int) (this.reactionBounds.left + (AndroidUtilities.dp(15.0f) * lerp) + f5), (int) (this.reactionBounds.centerY() + f5));
                    rLottieDrawable.setAlpha((int) (this.focus * 255.0f));
                    rLottieDrawable.draw(canvas);
                }
                i++;
            }
            float centerX = this.reactionBounds.centerX();
            float dp2 = this.reactionBounds.top - AndroidUtilities.dp(36.0f);
            canvas.save();
            float f6 = this.counterAlpha.set(this.counterShown);
            if (this.counterShown) {
                f2 = AndroidUtilities.dp(60.0f);
                f = 1.0f;
            } else {
                f = 1.0f;
                f2 = -AndroidUtilities.dp(30.0f);
            }
            canvas.translate(0.0f, f2 * (f - f6));
            float lerp2 = AndroidUtilities.lerp(this.counterShown ? 1.8f : 1.3f, f, f6);
            canvas.scale(lerp2, lerp2, centerX, dp2);
            this.counter.setAlpha((int) (255.0f * f6));
            this.counter.setShadowLayer(AndroidUtilities.dp(12.0f), 0.0f, AndroidUtilities.dp(3.5f), Theme.multAlpha(-1442840576, f6));
            this.counter.setBounds(centerX - AndroidUtilities.dp(100.0f), this.reactionBounds.top - AndroidUtilities.dp(48.0f), centerX + AndroidUtilities.dp(100.0f), this.reactionBounds.top - AndroidUtilities.dp(24.0f));
            this.counter.draw(canvas);
            canvas.restore();
        }
        if (!this.counterShown) {
            checkBalance();
        }
        invalidate();
    }

    public void focusTo(final float f, final Runnable runnable) {
        ValueAnimator valueAnimator = this.focusAnimator;
        if (valueAnimator != null) {
            this.focusAnimator = null;
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.focus, f);
        this.focusAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                StarReactionsOverlay.this.lambda$focusTo$3(valueAnimator2);
            }
        });
        this.focusAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Runnable runnable2;
                StarReactionsOverlay.this.focus = f;
                StarReactionsOverlay.this.invalidate();
                if (animator != StarReactionsOverlay.this.focusAnimator || (runnable2 = runnable) == null) {
                    return;
                }
                runnable2.run();
            }
        });
        this.focusAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.focusAnimator.setDuration(320L);
        this.focusAnimator.start();
    }

    public void hide() {
        this.hidden = true;
        AndroidUtilities.cancelRunOnUIThread(this.hideCounterRunnable);
        this.counter.setText("");
        this.counterShown = false;
        invalidate();
        focusTo(0.0f, new Runnable() {
            @Override
            public final void run() {
                StarReactionsOverlay.this.lambda$hide$4();
            }
        });
    }

    public boolean isShowing(MessageObject messageObject) {
        return messageObject != null && messageObject.getId() == this.messageId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.cell == null || this.hidden) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            if (this.clickBounds.contains(motionEvent.getX(), motionEvent.getY())) {
                this.pressed = true;
                ReactionsLayoutInBubble.ReactionButton reactionButton = this.cell.reactionsLayoutInBubble.getReactionButton("stars");
                if (reactionButton != null) {
                    reactionButton.bounce.setPressed(true);
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                AndroidUtilities.runOnUIThread(this.longPressRunnable, ViewConfiguration.getLongPressTimeout());
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            ReactionsLayoutInBubble.ReactionButton reactionButton2 = this.cell.reactionsLayoutInBubble.getReactionButton("stars");
            if (motionEvent.getAction() == 1) {
                tap(motionEvent.getX(), motionEvent.getY(), true, true);
            }
            if (reactionButton2 != null) {
                reactionButton2.bounce.setPressed(false);
            }
            this.pressed = false;
            AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
        }
        return this.pressed;
    }

    public void playEffect() {
        while (this.effects.size() > 4) {
            ((RLottieDrawable) this.effects.remove(0)).recycle(true);
        }
        int[] iArr = this.effectAssets;
        int i = iArr[Utilities.fastRandom.nextInt(iArr.length)];
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
        rLottieDrawable.setMasterParent(this);
        rLottieDrawable.setAllowDecodeSingleFrame(true);
        rLottieDrawable.setAutoRepeat(0);
        rLottieDrawable.start();
        this.effects.add(rLottieDrawable);
        invalidate();
    }

    public void setMessageCell(ChatMessageCell chatMessageCell) {
        ChatMessageCell chatMessageCell2 = this.cell;
        if (chatMessageCell2 == chatMessageCell) {
            return;
        }
        if (chatMessageCell2 != null) {
            chatMessageCell2.setScrimReaction(null);
            this.cell.setInvalidateListener(null);
            this.cell.invalidate();
        }
        this.cell = chatMessageCell;
        this.messageId = (chatMessageCell == null || chatMessageCell.getPrimaryMessageObject() == null) ? 0 : chatMessageCell.getPrimaryMessageObject().getId();
        ChatMessageCell chatMessageCell3 = this.cell;
        if (chatMessageCell3 != null) {
            chatMessageCell3.invalidate();
            this.cell.setInvalidateListener(new Runnable() {
                @Override
                public final void run() {
                    StarReactionsOverlay.this.invalidate();
                }
            });
        }
        invalidate();
    }

    public void show() {
        this.hidden = false;
        focusTo(1.0f, null);
    }

    public void tap(float f, float f2, boolean z, boolean z2) {
        ChatMessageCell chatMessageCell = this.cell;
        if (chatMessageCell == null || this.hidden) {
            return;
        }
        MessageObject primaryMessageObject = chatMessageCell.getPrimaryMessageObject();
        StarsController starsController = StarsController.getInstance(this.chatActivity.getCurrentAccount());
        playEffect();
        ReactionsLayoutInBubble.ReactionButton reactionButton = this.cell.reactionsLayoutInBubble.getReactionButton("stars");
        if (reactionButton != null) {
            reactionButton.startAnimation();
        }
        if (z) {
            try {
                performHapticFeedback(3, 1);
            } catch (Exception unused) {
            }
            StarsController.getInstance(this.chatActivity.getCurrentAccount()).sendPaidReaction(this.cell.getPrimaryMessageObject(), this.chatActivity, 1L, true, false, null);
        }
        this.counter.cancelAnimation();
        this.counter.setText("+" + starsController.getPendingPaidReactions(primaryMessageObject));
        this.counterShown = true;
        AndroidUtilities.cancelRunOnUIThread(this.hideCounterRunnable);
        AndroidUtilities.runOnUIThread(this.hideCounterRunnable, 1500L);
        if (z2) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastRippleTime;
            if (j < 100) {
                this.accumulatedRippleIntensity += 0.5f;
                return;
            }
            this.accumulatedRippleIntensity *= Utilities.clamp(1.0f - (((float) (j - 100)) / 200.0f), 1.0f, 0.0f);
            if (getMeasuredWidth() != 0 || this.chatActivity.getLayoutContainer() == null) {
                getLocationInWindow(this.pos2);
            } else {
                this.chatActivity.getLayoutContainer().getLocationInWindow(this.pos2);
            }
            int[] iArr = this.pos2;
            LaunchActivity.makeRipple(iArr[0] + f, iArr[1] + f2, Utilities.clamp(this.accumulatedRippleIntensity, 0.9f, 0.3f));
            this.accumulatedRippleIntensity = 0.0f;
            this.lastRippleTime = currentTimeMillis;
        }
    }
}
