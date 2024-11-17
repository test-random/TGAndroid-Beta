package org.telegram.ui.Stories;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.Reactions.ReactionImageHolder;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Reactions.ReactionsUtils;
import org.telegram.ui.EmojiAnimationsOverlay;
import org.telegram.ui.Stories.StoryMediaAreasView;

public class StoryReactionWidgetView extends StoryMediaAreasView.AreaView {
    AnimatedTextView.AnimatedTextDrawable animatedTextDrawable;
    boolean hasCounter;
    ReactionImageHolder holder;
    ImageReceiver preloadSmallReaction;
    AnimatedFloat progressToCount;
    StoryReactionWidgetBackground storyReactionWidgetBackground;
    private final ReactionsLayoutInBubble.VisibleReaction visibleReaction;

    public StoryReactionWidgetView(Context context, View view, TL_stories.TL_mediaAreaSuggestedReaction tL_mediaAreaSuggestedReaction, EmojiAnimationsOverlay emojiAnimationsOverlay) {
        super(context, view, tL_mediaAreaSuggestedReaction);
        TLRPC.TL_availableReaction tL_availableReaction;
        this.storyReactionWidgetBackground = new StoryReactionWidgetBackground(this);
        this.holder = new ReactionImageHolder(this);
        this.preloadSmallReaction = new ImageReceiver(this);
        this.progressToCount = new AnimatedFloat(this);
        this.animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
        ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_mediaAreaSuggestedReaction.reaction);
        this.visibleReaction = fromTL;
        if (tL_mediaAreaSuggestedReaction.flipped) {
            this.storyReactionWidgetBackground.setMirror(true, false);
        }
        this.storyReactionWidgetBackground.updateShadowLayer(getScaleX());
        this.holder.setVisibleReaction(fromTL);
        emojiAnimationsOverlay.preload(fromTL);
        if (fromTL.emojicon != null && (tL_availableReaction = MediaDataController.getInstance(UserConfig.selectedAccount).getReactionsMap().get(fromTL.emojicon)) != null) {
            this.preloadSmallReaction.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_lastreactframe", null, "webp", tL_availableReaction, 1);
        }
        this.animatedTextDrawable.setGravity(17);
        this.animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        this.animatedTextDrawable.setTextSize(AndroidUtilities.dp(18.0f));
        this.animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        if (tL_mediaAreaSuggestedReaction.dark) {
            this.storyReactionWidgetBackground.nextStyle();
            this.animatedTextDrawable.setTextColor(-1);
        }
    }

    @Override
    public void customDraw(Canvas canvas) {
        this.storyReactionWidgetBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.storyReactionWidgetBackground.draw(canvas);
        float measuredWidth = ((int) (getMeasuredWidth() * 0.61f)) / 2.0f;
        float centerX = this.storyReactionWidgetBackground.getBounds().centerX() - measuredWidth;
        float centerY = this.storyReactionWidgetBackground.getBounds().centerY() - measuredWidth;
        float centerX2 = this.storyReactionWidgetBackground.getBounds().centerX() + measuredWidth;
        float centerY2 = this.storyReactionWidgetBackground.getBounds().centerY() + measuredWidth;
        float height = this.storyReactionWidgetBackground.getBounds().top + (this.storyReactionWidgetBackground.getBounds().height() * 0.427f);
        float f = height - measuredWidth;
        float f2 = height + measuredWidth;
        float f3 = this.progressToCount.set(this.hasCounter ? 1.0f : 0.0f);
        Rect rect = AndroidUtilities.rectTmp2;
        rect.set((int) centerX, (int) AndroidUtilities.lerp(centerY, f, f3), (int) centerX2, (int) AndroidUtilities.lerp(centerY2, f2, f3));
        this.holder.setColor(this.storyReactionWidgetBackground.isDarkStyle() ? -1 : -16777216);
        this.holder.setBounds(rect);
        this.holder.draw(canvas);
        float height2 = this.storyReactionWidgetBackground.getBounds().top + (this.storyReactionWidgetBackground.getBounds().height() * 0.839f);
        this.animatedTextDrawable.setBounds(this.storyReactionWidgetBackground.getBounds().left, (int) (height2 - AndroidUtilities.dp(10.0f)), this.storyReactionWidgetBackground.getBounds().right, (int) (AndroidUtilities.dp(10.0f) + height2));
        canvas.save();
        canvas.scale(f3, f3, this.storyReactionWidgetBackground.getBounds().centerX(), height2);
        this.animatedTextDrawable.draw(canvas);
        canvas.restore();
    }

    public AnimatedEmojiDrawable getAnimatedEmojiDrawable() {
        return this.holder.animatedEmojiDrawable;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (getParent() instanceof View) {
            ((View) getParent()).invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.holder.onAttachedToWindow(true);
        this.preloadSmallReaction.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.holder.onAttachedToWindow(false);
        this.preloadSmallReaction.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.animatedTextDrawable.setTextSize(Math.min(AndroidUtilities.dp(18.0f), getMeasuredHeight() * 0.156f));
    }

    public void playAnimation() {
        this.holder.play();
    }

    @Override
    public void setScaleX(float f) {
        if (getScaleX() != f) {
            this.storyReactionWidgetBackground.updateShadowLayer(f);
            super.setScaleX(f);
        }
    }

    public void setViews(TL_stories.StoryViews storyViews, boolean z) {
        if (storyViews != null) {
            for (int i = 0; i < storyViews.reactions.size(); i++) {
                if (ReactionsUtils.compare(storyViews.reactions.get(i).reaction, this.visibleReaction)) {
                    boolean z2 = z && this.hasCounter;
                    this.hasCounter = storyViews.reactions.get(i).count > 0;
                    this.animatedTextDrawable.setText(AndroidUtilities.formatWholeNumber(storyViews.reactions.get(i).count, 0), z2);
                    if (z) {
                        return;
                    }
                    this.progressToCount.set(this.hasCounter ? 1.0f : 0.0f, true);
                    return;
                }
            }
        }
        this.hasCounter = false;
        invalidate();
        if (z) {
            return;
        }
        this.progressToCount.set(this.hasCounter ? 1.0f : 0.0f, true);
    }
}
