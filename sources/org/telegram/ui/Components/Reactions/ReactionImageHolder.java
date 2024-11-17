package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.view.View;
import java.util.Objects;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;

public class ReactionImageHolder {
    public AnimatedEmojiDrawable animatedEmojiDrawable;
    private boolean attached;
    ColorFilter colorFilter;
    ReactionsLayoutInBubble.VisibleReaction currentReaction;
    final ImageReceiver imageReceiver;
    private boolean isStatic;
    int lastColorForFilter;
    private View parent;
    private final Rect bounds = new Rect();
    private final int currentAccount = UserConfig.selectedAccount;
    float alpha = 1.0f;

    public ReactionImageHolder(View view) {
        this.parent = view;
        ImageReceiver imageReceiver = new ImageReceiver(view);
        this.imageReceiver = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
    }

    public static void preload(int i, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        if (visibleReaction == null) {
            return;
        }
        if (visibleReaction.emojicon == null) {
            new AnimatedEmojiDrawable(1, i, visibleReaction.documentId).preload();
            return;
        }
        TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(i).getReactionsMap().get(visibleReaction.emojicon);
        if (tL_availableReaction != null) {
            FileLoader.getInstance(i).loadFile(tL_availableReaction.select_animation, visibleReaction, 0, 0);
        }
    }

    public void draw(Canvas canvas) {
        AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
        if (animatedEmojiDrawable == null) {
            ImageReceiver imageReceiver = this.imageReceiver;
            Rect rect = this.bounds;
            imageReceiver.setImageCoords(rect.left, rect.top, rect.width(), this.bounds.height());
            this.imageReceiver.setAlpha(this.alpha);
            this.imageReceiver.draw(canvas);
            return;
        }
        if (animatedEmojiDrawable.getImageReceiver() != null) {
            this.animatedEmojiDrawable.getImageReceiver().setRoundRadius((int) (this.bounds.width() * 0.1f));
        }
        this.animatedEmojiDrawable.setColorFilter(this.colorFilter);
        this.animatedEmojiDrawable.setBounds(this.bounds);
        this.animatedEmojiDrawable.setAlpha((int) (this.alpha * 255.0f));
        this.animatedEmojiDrawable.draw(canvas);
    }

    public boolean isLoaded() {
        AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
        ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
        if (imageReceiver == null || !imageReceiver.hasImageSet() || !imageReceiver.hasImageLoaded()) {
            return false;
        }
        RLottieDrawable lottieAnimation = imageReceiver.getLottieAnimation();
        return lottieAnimation == null || !lottieAnimation.isGeneratingCache();
    }

    public void onAttachedToWindow(boolean z) {
        this.attached = z;
        if (z) {
            this.imageReceiver.onAttachedToWindow();
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(this.parent);
                return;
            }
            return;
        }
        this.imageReceiver.onDetachedFromWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
        if (animatedEmojiDrawable2 != null) {
            animatedEmojiDrawable2.removeView(this.parent);
        }
    }

    public void play() {
        this.imageReceiver.startAnimation();
    }

    public void setAlpha(float f) {
        this.alpha = f;
    }

    public void setBounds(Rect rect) {
        this.bounds.set(rect);
    }

    public void setColor(int i) {
        if (this.lastColorForFilter != i) {
            this.lastColorForFilter = i;
            this.colorFilter = new PorterDuffColorFilter(this.lastColorForFilter, PorterDuff.Mode.SRC_ATOP);
            View view = this.parent;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public void setParent(View view) {
        if (this.parent == view) {
            return;
        }
        if (!this.attached) {
            this.parent = view;
            return;
        }
        onAttachedToWindow(false);
        this.parent = view;
        onAttachedToWindow(true);
    }

    public void setStatic() {
        this.isStatic = true;
    }

    public void setVisibleReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        if (Objects.equals(this.currentReaction, visibleReaction)) {
            return;
        }
        this.imageReceiver.clearImage();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this.parent);
            this.animatedEmojiDrawable = null;
        }
        this.currentReaction = visibleReaction;
        String str = "60_60";
        if (this.isStatic) {
            str = "60_60_firstframe";
        }
        String str2 = str;
        if (visibleReaction.emojicon != null) {
            TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(visibleReaction.emojicon);
            if (tL_availableReaction != null) {
                this.imageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.select_animation), str2, null, null, DocumentObject.getSvgThumb(tL_availableReaction.select_animation, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f), 0L, "tgs", visibleReaction, 0);
                return;
            }
            return;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(this.isStatic ? 13 : 1, UserConfig.selectedAccount, visibleReaction.documentId);
        this.animatedEmojiDrawable = animatedEmojiDrawable2;
        if (this.attached) {
            animatedEmojiDrawable2.addView(this.parent);
        }
        AnimatedEmojiDrawable animatedEmojiDrawable3 = this.animatedEmojiDrawable;
        this.lastColorForFilter = -16777216;
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(-16777216, PorterDuff.Mode.SRC_ATOP);
        this.colorFilter = porterDuffColorFilter;
        animatedEmojiDrawable3.setColorFilter(porterDuffColorFilter);
    }
}
