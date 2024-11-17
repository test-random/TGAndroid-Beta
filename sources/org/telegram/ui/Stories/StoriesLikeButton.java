package org.telegram.ui.Stories;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Stories.PeerStoriesView;

public class StoriesLikeButton extends View {
    private boolean allowDrawReaction;
    ImageReceiver animateReactionImageReceiver;
    private boolean attachedToWindow;
    ReactionsLayoutInBubble.VisibleReaction currentReaction;
    private boolean drawAnimateImageReciever;
    AnimatedEmojiDrawable emojiDrawable;
    private boolean isLike;
    boolean liked;
    AnimatedFloat progressToLiked;
    ImageReceiver reactionImageReceiver;
    PeerStoriesView.SharedResources sharedResources;

    public StoriesLikeButton(Context context, PeerStoriesView.SharedResources sharedResources) {
        super(context);
        this.progressToLiked = new AnimatedFloat(this);
        this.reactionImageReceiver = new ImageReceiver(this);
        this.animateReactionImageReceiver = new ImageReceiver(this);
        this.allowDrawReaction = true;
        this.sharedResources = sharedResources;
        this.reactionImageReceiver.setAllowLoadingOnAttachedOnly(true);
        this.reactionImageReceiver.ignoreNotifications = true;
    }

    public void animateVisibleReaction() {
        this.drawAnimateImageReciever = true;
        if (this.animateReactionImageReceiver.getLottieAnimation() != null) {
            this.animateReactionImageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.reactionImageReceiver.onAttachedToWindow();
        this.animateReactionImageReceiver.onAttachedToWindow();
        this.attachedToWindow = true;
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.addView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.reactionImageReceiver.onDetachedFromWindow();
        this.animateReactionImageReceiver.onDetachedFromWindow();
        this.attachedToWindow = false;
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.isLike) {
            float f = this.progressToLiked.set(this.liked ? 1.0f : 0.0f);
            if (f < 1.0f) {
                this.sharedResources.likeDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
                this.sharedResources.likeDrawable.setAlpha(255);
                this.sharedResources.likeDrawable.draw(canvas);
            }
            if (f > 0.0f) {
                this.sharedResources.likeDrawableFilled.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
                this.sharedResources.likeDrawableFilled.setAlpha((int) (f * 255.0f));
                this.sharedResources.likeDrawableFilled.draw(canvas);
                return;
            }
            return;
        }
        if (this.allowDrawReaction) {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
            ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.reactionImageReceiver;
            if (this.drawAnimateImageReciever && this.animateReactionImageReceiver.getBitmap() != null) {
                imageReceiver = this.animateReactionImageReceiver;
                int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                float f2 = measuredWidth / 2.0f;
                float f3 = measuredWidth * 2;
                imageReceiver.setImageCoords(getPaddingLeft() - f2, getPaddingTop() - f2, f3, f3);
                if (this.animateReactionImageReceiver.getLottieAnimation() != null && this.animateReactionImageReceiver.getLottieAnimation().isLastFrame()) {
                    this.drawAnimateImageReciever = false;
                    this.reactionImageReceiver.setCrossfadeAlpha((byte) 0);
                }
            } else if (imageReceiver != null) {
                imageReceiver.setImageCoords(getPaddingLeft(), getPaddingTop(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            }
            if (imageReceiver != null) {
                imageReceiver.draw(canvas);
            }
        }
    }

    public void prepareAnimateReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        TLRPC.TL_availableReaction tL_availableReaction;
        if (visibleReaction.documentId != 0 || (tL_availableReaction = MediaDataController.getInstance(UserConfig.selectedAccount).getReactionsMap().get(visibleReaction.emojicon)) == null) {
            return;
        }
        this.animateReactionImageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_nolimit", null, "tgs", tL_availableReaction, 1);
        this.animateReactionImageReceiver.setAutoRepeat(0);
    }

    public void setAllowDrawReaction(boolean z) {
        if (this.allowDrawReaction == z) {
            return;
        }
        this.allowDrawReaction = z;
        invalidate();
    }

    public void setReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        String str;
        String str2;
        this.isLike = visibleReaction == null || ((str2 = visibleReaction.emojicon) != null && str2.equals("❤"));
        if (visibleReaction == null || (str = visibleReaction.emojicon) == null || !str.equals("❤")) {
            this.liked = false;
        } else {
            this.liked = true;
        }
        this.currentReaction = visibleReaction;
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this);
        }
        this.emojiDrawable = null;
        if (visibleReaction != null) {
            if (visibleReaction.documentId != 0) {
                AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(3, UserConfig.selectedAccount, visibleReaction.documentId);
                this.emojiDrawable = animatedEmojiDrawable2;
                if (this.attachedToWindow) {
                    animatedEmojiDrawable2.addView(this);
                }
            } else {
                TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(UserConfig.selectedAccount).getReactionsMap().get(visibleReaction.emojicon);
                if (tL_availableReaction != null) {
                    this.reactionImageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_lastreactframe", DocumentObject.getSvgThumb(tL_availableReaction.static_icon, Theme.key_windowBackgroundGray, 1.0f), "webp", tL_availableReaction, 1);
                }
            }
        }
        invalidate();
    }
}
