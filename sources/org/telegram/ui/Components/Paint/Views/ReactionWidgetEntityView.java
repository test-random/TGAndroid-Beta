package org.telegram.ui.Components.Paint.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Reactions.ReactionImageHolder;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Stories.StoryReactionWidgetBackground;

public class ReactionWidgetEntityView extends EntityView {
    Size baseSize;
    AnimatedFloat crossfadeBackgrounds;
    ReactionsLayoutInBubble.VisibleReaction currentReaction;
    private float drawScale;
    boolean mirror;
    ReactionImageHolder nextReactionHolder;
    StoryReactionWidgetBackground outBackground;
    AnimatedFloat progressToNext;
    ReactionImageHolder reactionHolder;
    StoryReactionWidgetBackground storyReactionWidgetBackground;

    public class StickerViewSelectionView extends EntityView.SelectionView {
        private RectF arcRect;

        public StickerViewSelectionView(Context context) {
            super(context);
            this.arcRect = new RectF();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int saveCount = canvas.getSaveCount();
            float showAlpha = getShowAlpha();
            if (showAlpha <= 0.0f) {
                return;
            }
            if (showAlpha < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (showAlpha * 255.0f), 31);
            }
            float dp = AndroidUtilities.dp(1.0f);
            float dpf2 = AndroidUtilities.dpf2(5.66f);
            float dp2 = dp + dpf2 + AndroidUtilities.dp(15.0f);
            float measuredWidth = (getMeasuredWidth() / 2) - dp2;
            float f = dp2 + (2.0f * measuredWidth);
            this.arcRect.set(dp2, dp2, f, f);
            canvas.drawArc(this.arcRect, 0.0f, 180.0f, false, this.paint);
            canvas.drawArc(this.arcRect, 180.0f, 180.0f, false, this.paint);
            float f2 = measuredWidth + dp2;
            canvas.drawCircle(dp2, f2, dpf2, this.dotStrokePaint);
            canvas.drawCircle(dp2, f2, dpf2 - AndroidUtilities.dp(1.0f), this.dotPaint);
            canvas.drawCircle(f, f2, dpf2, this.dotStrokePaint);
            canvas.drawCircle(f, f2, dpf2 - AndroidUtilities.dp(1.0f), this.dotPaint);
            canvas.restoreToCount(saveCount);
        }

        @Override
        protected int pointInsideHandle(float f, float f2) {
            float dp = AndroidUtilities.dp(1.0f);
            float dp2 = AndroidUtilities.dp(19.5f);
            float f3 = dp + dp2;
            float f4 = f3 * 2.0f;
            float measuredHeight = ((getMeasuredHeight() - f4) / 2.0f) + f3;
            if (f > f3 - dp2 && f2 > measuredHeight - dp2 && f < f3 + dp2 && f2 < measuredHeight + dp2) {
                return 1;
            }
            if (f > ((getMeasuredWidth() - f4) + f3) - dp2 && f2 > measuredHeight - dp2 && f < f3 + (getMeasuredWidth() - f4) + dp2 && f2 < measuredHeight + dp2) {
                return 2;
            }
            float measuredWidth = getMeasuredWidth() / 2.0f;
            return Math.pow((double) (f - measuredWidth), 2.0d) + Math.pow((double) (f2 - measuredWidth), 2.0d) < Math.pow((double) measuredWidth, 2.0d) ? 3 : 0;
        }
    }

    public ReactionWidgetEntityView(Context context, Point point, Size size) {
        super(context, point);
        this.storyReactionWidgetBackground = new StoryReactionWidgetBackground(this);
        this.outBackground = new StoryReactionWidgetBackground(this);
        this.reactionHolder = new ReactionImageHolder(this);
        this.nextReactionHolder = new ReactionImageHolder(this);
        this.progressToNext = new AnimatedFloat(this);
        AnimatedFloat animatedFloat = new AnimatedFloat(this);
        this.crossfadeBackgrounds = animatedFloat;
        this.drawScale = 1.0f;
        this.baseSize = size;
        animatedFloat.set(1.0f, true);
        this.progressToNext.set(1.0f, true);
        List<TLRPC.TL_availableReaction> reactionsList = MediaDataController.getInstance(UserConfig.selectedAccount).getReactionsList();
        ReactionImageHolder reactionImageHolder = this.reactionHolder;
        ReactionsLayoutInBubble.VisibleReaction fromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(findHeartReaction(reactionsList));
        this.currentReaction = fromEmojicon;
        reactionImageHolder.setVisibleReaction(fromEmojicon);
        updatePosition();
    }

    private String findHeartReaction(List list) {
        Object obj;
        int i = 0;
        while (true) {
            if (i >= list.size()) {
                obj = list.get(0);
                break;
            }
            if (((TLRPC.TL_availableReaction) list.get(i)).title.equals("Red Heart")) {
                obj = list.get(i);
                break;
            }
            i++;
        }
        return ((TLRPC.TL_availableReaction) obj).reaction;
    }

    public void lambda$mirror$0(boolean[] zArr, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (floatValue < 0.5f) {
            float f = floatValue / 0.5f;
            setRotationY(90.0f * f);
            this.drawScale = ((1.0f - f) * 0.3f) + 0.7f;
        } else {
            if (!zArr[0]) {
                zArr[0] = true;
                this.storyReactionWidgetBackground.setMirror(this.mirror, false);
            }
            float f2 = (floatValue - 0.5f) / 0.5f;
            setRotationY((1.0f - f2) * (-90.0f));
            this.drawScale = (f2 * 0.3f) + 0.7f;
        }
        invalidate();
    }

    @Override
    protected boolean allowHaptic() {
        return false;
    }

    @Override
    public boolean allowLongPressOnSelected() {
        return true;
    }

    public void changeStyle(boolean z) {
        if (z) {
            this.outBackground = this.storyReactionWidgetBackground;
            this.storyReactionWidgetBackground = new StoryReactionWidgetBackground(this);
            if (!this.outBackground.isDarkStyle()) {
                this.storyReactionWidgetBackground.nextStyle();
            }
            this.storyReactionWidgetBackground.setMirror(this.mirror, false);
            this.storyReactionWidgetBackground.updateShadowLayer(getScaleX());
            this.crossfadeBackgrounds.set(0.0f, true);
        } else {
            this.storyReactionWidgetBackground.nextStyle();
        }
        invalidate();
    }

    @Override
    protected EntityView.SelectionView createSelectionView() {
        return new StickerViewSelectionView(getContext());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int padding = getPadding();
        float f = this.crossfadeBackgrounds.set(1.0f);
        if (f == 1.0f) {
            this.outBackground = null;
        }
        canvas.save();
        float f2 = this.drawScale;
        canvas.scale(f2, f2, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
        StoryReactionWidgetBackground storyReactionWidgetBackground = this.outBackground;
        if (storyReactionWidgetBackground != null) {
            storyReactionWidgetBackground.setAlpha((int) ((1.0f - f) * 255.0f));
            StoryReactionWidgetBackground storyReactionWidgetBackground2 = this.outBackground;
            Size size = this.baseSize;
            storyReactionWidgetBackground2.setBounds(padding, padding, ((int) size.width) - padding, ((int) size.height) - padding);
            this.outBackground.draw(canvas);
        }
        this.storyReactionWidgetBackground.setAlpha((int) (f * 255.0f));
        StoryReactionWidgetBackground storyReactionWidgetBackground3 = this.storyReactionWidgetBackground;
        Size size2 = this.baseSize;
        storyReactionWidgetBackground3.setBounds(padding, padding, ((int) size2.width) - padding, ((int) size2.height) - padding);
        this.storyReactionWidgetBackground.draw(canvas);
        Rect rect = AndroidUtilities.rectTmp2;
        float width = (this.storyReactionWidgetBackground.getBounds().width() * 0.61f) / 2.0f;
        rect.set((int) (this.storyReactionWidgetBackground.getBounds().centerX() - width), (int) (this.storyReactionWidgetBackground.getBounds().centerY() - width), (int) (this.storyReactionWidgetBackground.getBounds().centerX() + width), (int) (this.storyReactionWidgetBackground.getBounds().centerY() + width));
        float f3 = this.progressToNext.set(1.0f);
        this.reactionHolder.setBounds(rect);
        this.nextReactionHolder.setBounds(rect);
        this.reactionHolder.setColor(this.storyReactionWidgetBackground.isDarkStyle() ? -1 : -16777216);
        if (f3 == 1.0f) {
            this.reactionHolder.draw(canvas);
        } else {
            canvas.save();
            float f4 = 1.0f - f3;
            canvas.scale(f4, f4, rect.centerX(), rect.top);
            this.nextReactionHolder.setAlpha(f4);
            this.nextReactionHolder.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.scale(f3, f3, rect.centerX(), rect.bottom);
            this.reactionHolder.setAlpha(f3);
            this.reactionHolder.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    public ReactionsLayoutInBubble.VisibleReaction getCurrentReaction() {
        return this.currentReaction;
    }

    @Override
    protected float getMaxScale() {
        return 1.8f;
    }

    @Override
    protected float getMinScale() {
        return 0.5f;
    }

    public int getPadding() {
        return (int) ((this.baseSize.height - AndroidUtilities.dp(84.0f)) / 2.0f);
    }

    @Override
    public org.telegram.ui.Components.Rect getSelectionBounds() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup == null) {
            return new org.telegram.ui.Components.Rect();
        }
        float scaleX = viewGroup.getScaleX();
        float measuredWidth = getMeasuredWidth() * (getScale() + 0.4f);
        float f = measuredWidth / 2.0f;
        float f2 = measuredWidth * scaleX;
        return new org.telegram.ui.Components.Rect((getPositionX() - f) * scaleX, (getPositionY() - f) * scaleX, f2, f2);
    }

    public boolean isDark() {
        return this.storyReactionWidgetBackground.isDarkStyle();
    }

    public boolean isMirrored() {
        return this.mirror;
    }

    public void mirror(boolean z) {
        boolean z2 = !this.mirror;
        this.mirror = z2;
        if (!z) {
            this.storyReactionWidgetBackground.setMirror(z2, z);
            return;
        }
        final boolean[] zArr = {false};
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ReactionWidgetEntityView.this.lambda$mirror$0(zArr, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                boolean[] zArr2 = zArr;
                if (!zArr2[0]) {
                    zArr2[0] = true;
                    ReactionWidgetEntityView reactionWidgetEntityView = ReactionWidgetEntityView.this;
                    reactionWidgetEntityView.storyReactionWidgetBackground.setMirror(reactionWidgetEntityView.mirror, false);
                }
                ReactionWidgetEntityView.this.setRotationY(0.0f);
                ReactionWidgetEntityView.this.drawScale = 1.0f;
            }
        });
        ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        ofFloat.setDuration(350L);
        ofFloat.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.reactionHolder.onAttachedToWindow(true);
        this.nextReactionHolder.onAttachedToWindow(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.reactionHolder.onAttachedToWindow(false);
        this.nextReactionHolder.onAttachedToWindow(false);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) this.baseSize.width, 1073741824), View.MeasureSpec.makeMeasureSpec((int) this.baseSize.height, 1073741824));
    }

    public void setCurrentReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z) {
        if (Objects.equals(this.currentReaction, visibleReaction)) {
            return;
        }
        this.currentReaction = visibleReaction;
        if (z) {
            this.nextReactionHolder.setVisibleReaction(visibleReaction);
            ReactionImageHolder reactionImageHolder = this.reactionHolder;
            this.reactionHolder = this.nextReactionHolder;
            this.nextReactionHolder = reactionImageHolder;
            this.progressToNext.set(0.0f, true);
        } else {
            this.reactionHolder.setVisibleReaction(visibleReaction);
        }
        invalidate();
    }

    @Override
    public void setScaleX(float f) {
        if (getScaleX() != f) {
            super.setScaleX(f);
            this.storyReactionWidgetBackground.updateShadowLayer(f);
            invalidate();
        }
    }

    @Override
    public void updatePosition() {
        Size size = this.baseSize;
        float f = size.width / 2.0f;
        float f2 = size.height / 2.0f;
        setX(getPositionX() - f);
        setY(getPositionY() - f2);
        updateSelectionView();
    }
}
