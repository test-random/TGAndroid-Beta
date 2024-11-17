package org.telegram.ui.Components.Forum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;

public abstract class MessageTopicButton {
    private AvatarDrawable avatarDrawable;
    private int avatarSize;
    private Context context;
    private ImageReceiver imageReceiver;
    private boolean isGeneralTopic;
    private MessageObject lastMessageObject;
    private Theme.ResourcesProvider resourcesProvider;
    private int topicArrowColor;
    private Drawable topicArrowDrawable;
    private boolean topicArrowDrawableVisible;
    private int topicBackgroundColor;
    private AnimatedColor topicBackgroundColorAnimated;
    private boolean topicClosed;
    private Drawable topicClosedDrawable;
    private float[] topicHSV;
    private int topicHeight;
    private RectF topicHitRect;
    private Drawable topicIconDrawable;
    private Rect topicIconDrawableBounds;
    private boolean topicIconWaiting;
    private int topicNameColor;
    private AnimatedColor topicNameColorAnimated;
    private StaticLayout topicNameLayout;
    private float topicNameLeft;
    private Paint topicPaint;
    private Path topicPath;
    private boolean topicPressed;
    private Drawable topicSelectorDrawable;
    private int topicWidth;
    private static final float[] lightHueRanges = {0.0f, 43.0f, 56.0f, 86.0f, 169.0f, 183.0f, 249.0f, 289.0f, 360.0f};
    private static final float[] lightSatValues = {0.6f, 1.0f, 0.95f, 0.98f, 0.8f, 0.88f, 0.51f, 0.55f, 0.6f};
    private static final float[] lightValValues = {0.79f, 0.77f, 0.6f, 0.62f, 0.6f, 0.61f, 0.8f, 0.7f, 0.79f};
    private static final float[] darkHueRanges = {0.0f, 43.0f, 56.0f, 63.0f, 86.0f, 122.0f, 147.0f, 195.0f, 205.0f, 249.0f, 270.0f, 312.0f, 388.0f, 360.0f};
    private static final float[] darkSatValues = {0.64f, 0.89f, 0.84f, 0.87f, 0.74f, 0.66f, 0.81f, 0.81f, 0.71f, 0.51f, 0.61f, 0.55f, 0.62f, 0.64f};
    private static final float[] darkValValues = {0.92f, 0.9f, 0.82f, 0.82f, 0.84f, 0.84f, 0.82f, 0.88f, 0.96f, 0.1f, 0.93f, 0.88f, 0.96f, 0.92f};
    private static final int[] idleState = new int[0];
    private static final int[] pressedState = {16842910, 16842919};

    public MessageTopicButton(Context context, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.resourcesProvider = resourcesProvider;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }

    private int setInternal(org.telegram.ui.Cells.ChatMessageCell r27, org.telegram.messenger.MessageObject r28, int r29, java.lang.String r30, int r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Forum.MessageTopicButton.setInternal(org.telegram.ui.Cells.ChatMessageCell, org.telegram.messenger.MessageObject, int, java.lang.String, int):int");
    }

    private void setupColors(int i) {
        int HSVToColor;
        int i2;
        MessageObject messageObject = this.lastMessageObject;
        if (messageObject != null && messageObject.shouldDrawWithoutBackground()) {
            this.topicNameColor = getThemedColor(Theme.key_chat_stickerReplyNameText);
            return;
        }
        MessageObject messageObject2 = this.lastMessageObject;
        if (messageObject2 == null || !messageObject2.isOutOwner()) {
            if (this.topicHSV == null) {
                this.topicHSV = new float[3];
            }
            Color.colorToHSV(i, this.topicHSV);
            float[] fArr = this.topicHSV;
            float f = fArr[0];
            if (fArr[1] > 0.02f) {
                Color.colorToHSV(getThemedColor(Theme.key_chat_inReactionButtonText), this.topicHSV);
                this.topicHSV[0] = f;
                float[] fArr2 = Theme.isCurrentThemeDark() ? darkHueRanges : lightHueRanges;
                float[] fArr3 = Theme.isCurrentThemeDark() ? darkSatValues : lightSatValues;
                float[] fArr4 = Theme.isCurrentThemeDark() ? darkValValues : lightValValues;
                int i3 = 1;
                while (true) {
                    if (i3 >= fArr2.length) {
                        break;
                    }
                    float f2 = fArr2[i3];
                    if (f <= f2) {
                        int i4 = i3 - 1;
                        float f3 = fArr2[i4];
                        float f4 = (f - f3) / (f2 - f3);
                        this.topicHSV[1] = AndroidUtilities.lerp(fArr3[i4], fArr3[i3], f4);
                        this.topicHSV[2] = AndroidUtilities.lerp(fArr4[i4], fArr4[i3], f4);
                        break;
                    }
                    i3++;
                }
                this.topicNameColor = Color.HSVToColor(Color.alpha(getThemedColor(Theme.key_chat_inReactionButtonText)), this.topicHSV);
                HSVToColor = Color.HSVToColor(38, this.topicHSV);
                this.topicBackgroundColor = HSVToColor;
            }
            this.topicNameColor = getThemedColor(Theme.key_chat_inReactionButtonText);
            i2 = Theme.key_chat_inReactionButtonBackground;
        } else {
            this.topicNameColor = getThemedColor(Theme.key_chat_outReactionButtonText);
            i2 = Theme.key_chat_outReactionButtonBackground;
        }
        HSVToColor = ColorUtils.setAlphaComponent(getThemedColor(i2), 38);
        this.topicBackgroundColor = HSVToColor;
    }

    public boolean checkTouchEvent(MotionEvent motionEvent) {
        Drawable drawable;
        RectF rectF = this.topicHitRect;
        if (rectF == null) {
            this.topicPressed = false;
            return false;
        }
        boolean contains = rectF.contains(motionEvent.getX(), motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            if (contains) {
                Drawable drawable2 = this.topicSelectorDrawable;
                if (drawable2 != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable2.setHotspot(motionEvent.getX() - this.topicHitRect.left, motionEvent.getY() - this.topicHitRect.top);
                    }
                    this.topicSelectorDrawable.setState(pressedState);
                }
                this.topicPressed = true;
            } else {
                this.topicPressed = false;
            }
            return this.topicPressed;
        }
        if (motionEvent.getAction() == 2) {
            boolean z = this.topicPressed;
            if (z != contains) {
                if (z && (drawable = this.topicSelectorDrawable) != null) {
                    drawable.setState(idleState);
                }
                this.topicPressed = contains;
            }
            return this.topicPressed;
        }
        if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.topicPressed) {
            this.topicPressed = false;
            Drawable drawable3 = this.topicSelectorDrawable;
            if (drawable3 != null) {
                drawable3.setState(idleState);
            }
            if (motionEvent.getAction() == 1) {
                onClick();
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas, float f, float f2, float f3) {
        Paint paint;
        int i;
        int dominantColor;
        if (this.topicIconWaiting) {
            Drawable drawable = this.topicIconDrawable;
            if ((drawable instanceof AnimatedEmojiDrawable) && (dominantColor = AnimatedEmojiDrawable.getDominantColor((AnimatedEmojiDrawable) drawable)) != 0) {
                this.topicIconWaiting = false;
                setupColors(dominantColor);
            }
        }
        canvas.save();
        MessageObject messageObject = this.lastMessageObject;
        if (messageObject == null || !messageObject.shouldDrawWithoutBackground()) {
            canvas.translate(f, f2);
            if (this.topicPath != null && (paint = this.topicPaint) != null) {
                AnimatedColor animatedColor = this.topicBackgroundColorAnimated;
                paint.setColor(animatedColor != null ? animatedColor.set(this.topicBackgroundColor) : this.topicBackgroundColor);
                int alpha = this.topicPaint.getAlpha();
                this.topicPaint.setAlpha((int) (alpha * f3));
                canvas.drawPath(this.topicPath, this.topicPaint);
                this.topicPaint.setAlpha(alpha);
            }
        } else {
            this.topicPath.offset(f, f2);
            int i2 = -1;
            if (f3 < 1.0f) {
                i = getThemedPaint("paintChatActionBackground").getAlpha();
                getThemedPaint("paintChatActionBackground").setAlpha((int) (i * f3));
            } else {
                i = -1;
            }
            canvas.drawPath(this.topicPath, getThemedPaint("paintChatActionBackground"));
            if (hasGradientService()) {
                if (f3 < 1.0f) {
                    i2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (i2 * f3));
                }
                canvas.drawPath(this.topicPath, Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (i >= 0) {
                getThemedPaint("paintChatActionBackground").setAlpha(i);
            }
            if (i2 >= 0) {
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(i2);
            }
            this.topicPath.offset(-f, -f2);
            canvas.translate(f, f2);
        }
        if (this.topicHitRect == null) {
            this.topicHitRect = new RectF();
        }
        this.topicHitRect.set(f, f2, this.topicWidth + f, this.topicHeight + f2);
        if (this.topicSelectorDrawable != null) {
            canvas.save();
            canvas.clipPath(this.topicPath);
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set(0, 0, this.topicWidth, this.topicHeight);
            this.topicSelectorDrawable.setBounds(rect);
            this.topicSelectorDrawable.draw(canvas);
            canvas.restore();
        }
        int i3 = this.topicNameColor;
        if (this.topicNameLayout != null) {
            canvas.save();
            canvas.translate((AndroidUtilities.dp(this.isGeneralTopic ? 13.0f : 17.0f) + Theme.chat_topicTextPaint.getTextSize()) - this.topicNameLeft, AndroidUtilities.dp(4.5f));
            AnimatedColor animatedColor2 = this.topicNameColorAnimated;
            if (animatedColor2 != null) {
                TextPaint textPaint = Theme.chat_topicTextPaint;
                i3 = animatedColor2.set(this.topicNameColor);
                textPaint.setColor(i3);
            } else {
                TextPaint textPaint2 = Theme.chat_topicTextPaint;
                int i4 = this.topicNameColor;
                textPaint2.setColor(i4);
                i3 = i4;
            }
            Theme.chat_topicTextPaint.setAlpha((int) (r10.getAlpha() * f3 * (this.topicClosed ? 0.7f : 1.0f)));
            this.topicNameLayout.draw(canvas);
            canvas.restore();
        }
        if (this.topicClosedDrawable != null && this.topicClosed) {
            int alphaComponent = ColorUtils.setAlphaComponent(i3, 140);
            if (this.topicArrowColor != alphaComponent) {
                Drawable drawable2 = this.topicClosedDrawable;
                this.topicArrowColor = alphaComponent;
                drawable2.setColorFilter(new PorterDuffColorFilter(alphaComponent, PorterDuff.Mode.MULTIPLY));
            }
            this.topicClosedDrawable.draw(canvas);
        }
        if (this.topicArrowDrawable != null && this.topicArrowDrawableVisible) {
            int alphaComponent2 = ColorUtils.setAlphaComponent(i3, 140);
            if (this.topicArrowColor != alphaComponent2) {
                Drawable drawable3 = this.topicArrowDrawable;
                this.topicArrowColor = alphaComponent2;
                drawable3.setColorFilter(new PorterDuffColorFilter(alphaComponent2, PorterDuff.Mode.MULTIPLY));
            }
            this.topicArrowDrawable.draw(canvas);
        }
        canvas.restore();
    }

    public void drawOutbounds(Canvas canvas, float f) {
        if (this.topicHitRect != null) {
            canvas.save();
            RectF rectF = this.topicHitRect;
            canvas.translate(rectF.left, rectF.top);
            Drawable drawable = this.topicIconDrawable;
            if (drawable != null) {
                drawable.setAlpha((int) (f * 255.0f));
                this.topicIconDrawable.setBounds(this.topicIconDrawableBounds);
                this.topicIconDrawable.draw(canvas);
            } else {
                ImageReceiver imageReceiver = this.imageReceiver;
                if (imageReceiver != null) {
                    float f2 = this.avatarSize;
                    imageReceiver.setImageCoords(0.0f, 0.0f, f2, f2);
                    this.imageReceiver.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    public int height() {
        return this.topicHeight;
    }

    public void onAttached(ChatMessageCell chatMessageCell) {
        Drawable drawable = this.topicIconDrawable;
        if (!(drawable instanceof AnimatedEmojiDrawable) || chatMessageCell == null) {
            return;
        }
        ((AnimatedEmojiDrawable) drawable).addView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
    }

    protected abstract void onClick();

    public void onDetached(ChatMessageCell chatMessageCell) {
        Drawable drawable = this.topicIconDrawable;
        if (!(drawable instanceof AnimatedEmojiDrawable) || chatMessageCell == null) {
            return;
        }
        ((AnimatedEmojiDrawable) drawable).removeView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
    }

    public void resetClick() {
        Drawable drawable = this.topicSelectorDrawable;
        if (drawable != null) {
            drawable.setState(idleState);
        }
    }

    public int set(org.telegram.ui.Cells.ChatMessageCell r16, org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLObject r18, int r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Forum.MessageTopicButton.set(org.telegram.ui.Cells.ChatMessageCell, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLObject, int):int");
    }

    public int set(ChatMessageCell chatMessageCell, MessageObject messageObject, TLRPC.TL_forumTopic tL_forumTopic, int i) {
        int i2;
        Drawable createSmallTopicDrawable;
        if (chatMessageCell == null || messageObject == null) {
            return 0;
        }
        boolean z = tL_forumTopic.id == 1;
        this.isGeneralTopic = z;
        this.topicClosed = tL_forumTopic.closed;
        String str = tL_forumTopic.title;
        if (str == null) {
            str = "";
        }
        String str2 = str;
        if (z) {
            i2 = getThemedColor(messageObject.isOutOwner() ? Theme.key_chat_outReactionButtonText : Theme.key_chat_inReactionButtonText);
            createSmallTopicDrawable = ForumUtilities.createGeneralTopicDrawable(this.context, 0.65f, i2, false);
        } else {
            long j = tL_forumTopic.icon_emoji_id;
            if (j != 0) {
                Drawable drawable = this.topicIconDrawable;
                if (!(drawable instanceof AnimatedEmojiDrawable) || j != ((AnimatedEmojiDrawable) drawable).getDocumentId()) {
                    Drawable drawable2 = this.topicIconDrawable;
                    if (drawable2 instanceof AnimatedEmojiDrawable) {
                        ((AnimatedEmojiDrawable) drawable2).removeView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
                        this.topicIconDrawable = null;
                    }
                    AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(messageObject.currentAccount, 0, tL_forumTopic.icon_emoji_id);
                    this.topicIconDrawable = make;
                    make.addView(new MessageTopicButton$$ExternalSyntheticLambda0(chatMessageCell));
                }
                this.topicIconWaiting = false;
                Drawable drawable3 = this.topicIconDrawable;
                int dominantColor = drawable3 instanceof AnimatedEmojiDrawable ? AnimatedEmojiDrawable.getDominantColor((AnimatedEmojiDrawable) drawable3) : 0;
                if (dominantColor == 0) {
                    this.topicIconWaiting = true;
                    i2 = getThemedColor(messageObject.isOutOwner() ? Theme.key_chat_outReactionButtonText : Theme.key_chat_inReactionButtonText);
                } else {
                    i2 = dominantColor;
                }
                setupColors(i2);
                return setInternal(chatMessageCell, messageObject, i, str2, 2);
            }
            i2 = tL_forumTopic.icon_color;
            createSmallTopicDrawable = ForumUtilities.createSmallTopicDrawable(str2, i2);
        }
        this.topicIconDrawable = createSmallTopicDrawable;
        setupColors(i2);
        return setInternal(chatMessageCell, messageObject, i, str2, 2);
    }

    public int width() {
        return this.topicWidth;
    }
}
