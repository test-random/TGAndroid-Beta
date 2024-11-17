package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarsDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Stars.StarsReactionsSheet;

public class ReactionsLayoutInBubble {
    private static int animationUniq;
    private int animateFromTotalHeight;
    private boolean animateHeight;
    private boolean animateMove;
    private boolean animateWidth;
    boolean attached;
    int availableWidth;
    public float drawServiceShaderBackground;
    public int fromWidth;
    private float fromX;
    private float fromY;
    public boolean hasPaidReaction;
    public boolean hasUnreadReactions;
    public int height;
    public boolean isEmpty;
    public boolean isSmall;
    private int lastDrawTotalHeight;
    private int lastDrawnWidth;
    private float lastDrawnX;
    private float lastDrawnY;
    public int lastLineX;
    ReactionButton lastSelectedButton;
    float lastX;
    float lastY;
    Runnable longPressRunnable;
    MessageObject messageObject;
    ChatMessageCell parentView;
    public int positionOffsetY;
    boolean pressed;
    Theme.ResourcesProvider resourcesProvider;
    private boolean scrimDirection;
    private float scrimProgress;
    private Integer scrimViewReaction;
    public boolean tags;
    public int totalHeight;
    private float touchSlop;
    private boolean wasDrawn;
    public int width;
    public int x;
    public int y;
    private static Paint paint = new Paint(1);
    private static Paint tagPaint = new Paint(1);
    private static Paint cutTagPaint = new Paint(1);
    private static TextPaint textPaint = new TextPaint(1);
    private static final ButtonsComparator comparator = new ButtonsComparator();
    private static int pointer = 1;
    private static final Comparator usersComparator = new Comparator() {
        @Override
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = ReactionsLayoutInBubble.lambda$static$0((TLObject) obj, (TLObject) obj2);
            return lambda$static$0;
        }
    };
    public ArrayList reactionButtons = new ArrayList();
    ArrayList outButtons = new ArrayList();
    HashMap lastDrawingReactionButtons = new HashMap();
    HashMap lastDrawingReactionButtonsTmp = new HashMap();
    HashMap animatedReactions = new HashMap();
    private final RectF scrimRect = new RectF();
    int currentAccount = UserConfig.selectedAccount;

    private static class ButtonsComparator implements Comparator {
        int currentAccount;
        long dialogId;

        private ButtonsComparator() {
        }

        @Override
        public int compare(ReactionButton reactionButton, ReactionButton reactionButton2) {
            int i;
            int i2;
            long j = this.dialogId;
            boolean z = reactionButton.paid;
            boolean z2 = reactionButton2.paid;
            if (j >= 0) {
                if (z != z2) {
                    return z ? -1 : 1;
                }
                boolean z3 = reactionButton.isSelected;
                if (z3 != reactionButton2.isSelected) {
                    return z3 ? -1 : 1;
                }
                if (z3 && (i = reactionButton.choosenOrder) != (i2 = reactionButton2.choosenOrder)) {
                    return i - i2;
                }
            } else {
                if (z != z2) {
                    return z ? -1 : 1;
                }
                int i3 = reactionButton.realCount;
                int i4 = reactionButton2.realCount;
                if (i3 != i4) {
                    return i4 - i3;
                }
            }
            return reactionButton.reactionCount.lastDrawnPosition - reactionButton2.reactionCount.lastDrawnPosition;
        }
    }

    public static class ReactionButton {
        public int animateFromWidth;
        public int animateFromX;
        public int animateFromY;
        public AnimatedEmojiDrawable animatedEmojiDrawable;
        int animatedEmojiDrawableColor;
        public int animationType;
        public boolean attached;
        AvatarsDrawable avatarsDrawable;
        int backgroundColor;
        public final ButtonBounce bounce;
        public boolean choosen;
        public int choosenOrder;
        public int count;
        public String countText;
        public CounterView.CounterDrawable counterDrawable;
        private final int currentAccount;
        public int fromBackgroundColor;
        public int fromTagDotColor;
        public int fromTextColor;
        public boolean hasName;
        public int height;
        public ImageReceiver imageReceiver;
        public boolean inGroup;
        boolean isSelected;
        private final boolean isSmall;
        public boolean isTag;
        public String key;
        public int lastDrawnBackgroundColor;
        public int lastDrawnTagDotColor;
        public int lastDrawnTextColor;
        public boolean lastImageDrawn;
        private boolean lastScrimProgressDirection;
        public String name;
        public boolean paid;
        private final View parentView;
        private StarsReactionsSheet.Particles particles;
        public AnimatedEmojiDrawable previewAnimatedEmojiDrawable;
        public ImageReceiver previewImageReceiver;
        public TLRPC.Reaction reaction;
        private final TLRPC.ReactionCount reactionCount;
        public int realCount;
        private final Theme.ResourcesProvider resourcesProvider;
        public AnimatedTextView.AnimatedTextDrawable scrimPreviewCounterDrawable;
        int serviceBackgroundColor;
        int serviceTextColor;
        private final Drawable.Callback supercallback;
        int textColor;
        public AnimatedTextView.AnimatedTextDrawable textDrawable;
        ArrayList users;
        VisibleReaction visibleReaction;
        public boolean wasDrawn;
        public int width;
        public int x;
        public int y;
        public boolean drawImage = true;
        Rect drawingImageRect = new Rect();
        private RectF bounds = new RectF();
        private RectF rect2 = new RectF();
        private final Path tagPath = new Path();
        private final Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(Drawable drawable) {
                if (ReactionButton.this.parentView != null) {
                    ReactionButton.this.parentView.invalidate();
                    ReactionButton reactionButton = ReactionButton.this;
                    if (reactionButton.inGroup && (reactionButton.parentView.getParent() instanceof View)) {
                        ((View) ReactionButton.this.parentView.getParent()).invalidate();
                    }
                }
            }

            @Override
            public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                if (ReactionButton.this.parentView != null) {
                    ReactionButton.this.parentView.scheduleDrawable(drawable, runnable, j);
                }
            }

            @Override
            public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                if (ReactionButton.this.parentView != null) {
                    ReactionButton.this.parentView.unscheduleDrawable(drawable, runnable);
                }
            }
        };

        public ReactionButton(ReactionButton reactionButton, int i, View view, TLRPC.ReactionCount reactionCount, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
            String l;
            StarsReactionsSheet.Particles particles;
            Drawable.Callback callback = new Drawable.Callback() {
                @Override
                public void invalidateDrawable(Drawable drawable) {
                    if (ReactionButton.this.parentView != null) {
                        ReactionButton.this.parentView.invalidate();
                        ReactionButton reactionButton2 = ReactionButton.this;
                        if (reactionButton2.inGroup && reactionButton2.parentView.getParent() != null && (ReactionButton.this.parentView.getParent().getParent() instanceof View)) {
                            ((View) ReactionButton.this.parentView.getParent().getParent()).invalidate();
                        }
                    }
                }

                @Override
                public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                    if (ReactionButton.this.parentView != null) {
                        ReactionButton.this.parentView.scheduleDrawable(drawable, runnable, j);
                    }
                }

                @Override
                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                    if (ReactionButton.this.parentView != null) {
                        ReactionButton.this.parentView.unscheduleDrawable(drawable, runnable);
                    }
                }
            };
            this.supercallback = callback;
            this.currentAccount = i;
            this.parentView = view;
            this.bounce = new ButtonBounce(view);
            this.resourcesProvider = resourcesProvider;
            this.isTag = z2;
            if (reactionButton != null) {
                this.counterDrawable = reactionButton.counterDrawable;
            }
            if (this.imageReceiver == null) {
                this.imageReceiver = new ImageReceiver();
            }
            if (this.counterDrawable == null) {
                this.counterDrawable = new CounterView.CounterDrawable(view, false, null);
            }
            if (this.textDrawable == null) {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
                this.textDrawable = animatedTextDrawable;
                animatedTextDrawable.setAnimationProperties(0.4f, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
                this.textDrawable.setTextSize(AndroidUtilities.dp(13.0f));
                this.textDrawable.setCallback(callback);
                this.textDrawable.setTypeface(AndroidUtilities.bold());
                this.textDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            }
            if (this.scrimPreviewCounterDrawable == null) {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(false, false, false, true);
                this.scrimPreviewCounterDrawable = animatedTextDrawable2;
                animatedTextDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
                this.scrimPreviewCounterDrawable.setCallback(callback);
                this.scrimPreviewCounterDrawable.setTypeface(AndroidUtilities.bold());
                this.scrimPreviewCounterDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
                this.scrimPreviewCounterDrawable.setScaleProperty(0.35f);
            }
            this.reactionCount = reactionCount;
            TLRPC.Reaction reaction = reactionCount.reaction;
            this.reaction = reaction;
            this.visibleReaction = VisibleReaction.fromTL(reaction);
            int i2 = reactionCount.count;
            this.count = i2;
            this.choosen = reactionCount.chosen;
            this.realCount = i2;
            this.choosenOrder = reactionCount.chosen_order;
            this.isSmall = z;
            TLRPC.Reaction reaction2 = this.reaction;
            if (reaction2 instanceof TLRPC.TL_reactionPaid) {
                l = "stars";
            } else if (reaction2 instanceof TLRPC.TL_reactionEmoji) {
                l = ((TLRPC.TL_reactionEmoji) reaction2).emoticon;
            } else {
                if (!(reaction2 instanceof TLRPC.TL_reactionCustomEmoji)) {
                    throw new RuntimeException("unsupported");
                }
                l = Long.toString(((TLRPC.TL_reactionCustomEmoji) reaction2).document_id);
            }
            this.key = l;
            this.imageReceiver.setParentView(view);
            this.isSelected = reactionCount.chosen;
            CounterView.CounterDrawable counterDrawable = this.counterDrawable;
            counterDrawable.updateVisibility = false;
            counterDrawable.shortFormat = true;
            if (this.reaction != null) {
                VisibleReaction visibleReaction = this.visibleReaction;
                if (visibleReaction.isStar) {
                    this.paid = true;
                    if (LiteMode.isEnabled(8200)) {
                        this.imageReceiver.setImageBitmap(new RLottieDrawable(R.raw.star_reaction_click, "star_reaction_click", AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f)));
                    } else {
                        this.imageReceiver.setImageBitmap(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.star_reaction).mutate());
                    }
                    if (reactionButton == null || (particles = reactionButton.particles) == null) {
                        particles = new StarsReactionsSheet.Particles(1, SharedConfig.getDevicePerformanceClass() == 2 ? 25 : 8);
                    }
                    this.particles = particles;
                } else if (visibleReaction.emojicon != null) {
                    TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(i).getReactionsMap().get(this.visibleReaction.emojicon);
                    if (tL_availableReaction != null) {
                        this.imageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_lastreactframe", DocumentObject.getSvgThumb(tL_availableReaction.static_icon, Theme.key_windowBackgroundGray, 1.0f), "webp", tL_availableReaction, 1);
                    }
                } else if (visibleReaction.documentId != 0) {
                    this.animatedEmojiDrawable = new AnimatedEmojiDrawable(getCacheType(), i, this.visibleReaction.documentId);
                }
            }
            this.counterDrawable.setSize(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(100.0f));
            this.counterDrawable.textPaint = ReactionsLayoutInBubble.textPaint;
            if (z2) {
                this.name = MessagesController.getInstance(i).getSavedTagName(this.reaction);
                this.hasName = !TextUtils.isEmpty(r1);
            }
            if (this.hasName) {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable3 = this.textDrawable;
                animatedTextDrawable3.setText(Emoji.replaceEmoji(this.name, animatedTextDrawable3.getPaint().getFontMetricsInt(), false), !LocaleController.isRTL);
                if (!drawTextWithCounter()) {
                    this.countText = "";
                    this.counterDrawable.setCount(0, false);
                    this.counterDrawable.setType(2);
                    this.counterDrawable.gravity = 3;
                }
            } else {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable4 = this.textDrawable;
                if (animatedTextDrawable4 != null) {
                    animatedTextDrawable4.setText("", false);
                }
            }
            this.countText = Integer.toString(reactionCount.count);
            this.counterDrawable.setCount(this.count, false);
            this.counterDrawable.setType(2);
            this.counterDrawable.gravity = 3;
        }

        public void drawImage(Canvas canvas, Rect rect, float f) {
            boolean z;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver = (animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null) ? this.imageReceiver : this.animatedEmojiDrawable.getImageReceiver();
            if (imageReceiver != null && rect != null) {
                imageReceiver.setImageCoords(rect);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null && this.animatedEmojiDrawableColor != this.lastDrawnTextColor) {
                int i = this.lastDrawnTextColor;
                this.animatedEmojiDrawableColor = i;
                animatedEmojiDrawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
            }
            if (!this.drawImage || (!this.paid && this.realCount <= 1 && isPlaying() && this.isSelected)) {
                imageReceiver.setAlpha(0.0f);
                imageReceiver.draw(canvas);
                this.lastImageDrawn = false;
                return;
            }
            ImageReceiver imageReceiver2 = getImageReceiver();
            if (imageReceiver2 != null) {
                z = imageReceiver2.getLottieAnimation() == null || !imageReceiver2.getLottieAnimation().hasBitmap();
                if (f != 1.0f) {
                    imageReceiver2.setAlpha(f);
                    if (f <= 0.0f) {
                        imageReceiver2.onDetachedFromWindow();
                        removeImageReceiver();
                    }
                } else if (imageReceiver2.getLottieAnimation() != null && !imageReceiver2.getLottieAnimation().isRunning()) {
                    float alpha = imageReceiver2.getAlpha() - 0.08f;
                    if (alpha <= 0.0f) {
                        imageReceiver2.onDetachedFromWindow();
                        removeImageReceiver();
                    } else {
                        imageReceiver2.setAlpha(alpha);
                    }
                    this.parentView.invalidate();
                    z = true;
                }
                imageReceiver2.setImageCoords(imageReceiver.getImageX() - (imageReceiver.getImageWidth() / 2.0f), imageReceiver.getImageY() - (imageReceiver.getImageWidth() / 2.0f), imageReceiver.getImageWidth() * 2.0f, imageReceiver.getImageHeight() * 2.0f);
                imageReceiver2.draw(canvas);
            } else {
                z = true;
            }
            if (z) {
                imageReceiver.draw(canvas);
            }
            this.lastImageDrawn = true;
        }

        private void drawRoundRect(Canvas canvas, RectF rectF, float f, Paint paint) {
            if (!this.isTag) {
                canvas.drawRoundRect(rectF, f, f, paint);
                return;
            }
            RectF rectF2 = this.bounds;
            if (rectF2.left != rectF.left || rectF2.top != rectF.top || rectF2.right != rectF.right || rectF2.bottom != rectF.bottom) {
                rectF2.set(rectF);
                ReactionsLayoutInBubble.fillTagPath(this.bounds, this.rect2, this.tagPath);
            }
            canvas.drawPath(this.tagPath, paint);
        }

        public void attach() {
            this.attached = true;
            ImageReceiver imageReceiver = this.imageReceiver;
            if (imageReceiver != null) {
                imageReceiver.onAttachedToWindow();
            }
            AvatarsDrawable avatarsDrawable = this.avatarsDrawable;
            if (avatarsDrawable != null) {
                avatarsDrawable.onAttachedToWindow();
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(this.parentView);
            }
        }

        public void attachPreview(View view) {
            if (this.previewImageReceiver == null && this.previewAnimatedEmojiDrawable == null) {
                View view2 = this.parentView;
                View view3 = (view2 == null || !(view2.getParent() instanceof View)) ? this.parentView : (View) this.parentView.getParent();
                if (this.reaction != null) {
                    VisibleReaction visibleReaction = this.visibleReaction;
                    if (visibleReaction.isStar) {
                        return;
                    }
                    if (visibleReaction.emojicon == null) {
                        if (visibleReaction.documentId != 0) {
                            AnimatedEmojiDrawable animatedEmojiDrawable = new AnimatedEmojiDrawable(24, this.currentAccount, this.visibleReaction.documentId);
                            this.previewAnimatedEmojiDrawable = animatedEmojiDrawable;
                            animatedEmojiDrawable.addView(view3);
                            return;
                        }
                        return;
                    }
                    TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(this.visibleReaction.emojicon);
                    if (tL_availableReaction == null || tL_availableReaction.activate_animation == null) {
                        return;
                    }
                    SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tL_availableReaction.static_icon, Theme.key_windowBackgroundGray, 1.0f);
                    ImageReceiver imageReceiver = new ImageReceiver(view3);
                    this.previewImageReceiver = imageReceiver;
                    imageReceiver.setLayerNum(7);
                    this.previewImageReceiver.onAttachedToWindow();
                    this.previewImageReceiver.setRoundRadius(AndroidUtilities.dp(14.0f));
                    this.previewImageReceiver.setAllowStartLottieAnimation(true);
                    this.previewImageReceiver.setAllowStartAnimation(true);
                    this.previewImageReceiver.setAutoRepeat(1);
                    this.previewImageReceiver.setAllowDecodeSingleFrame(true);
                    this.previewImageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.activate_animation), "140_140", svgThumb, null, tL_availableReaction, 1);
                }
            }
        }

        public void detach() {
            this.attached = false;
            ImageReceiver imageReceiver = this.imageReceiver;
            if (imageReceiver != null) {
                imageReceiver.onDetachedFromWindow();
            }
            AvatarsDrawable avatarsDrawable = this.avatarsDrawable;
            if (avatarsDrawable != null) {
                avatarsDrawable.onDetachedFromWindow();
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.removeView(this.parentView);
            }
            detachPreview();
        }

        public void detachPreview() {
            ImageReceiver imageReceiver = this.previewImageReceiver;
            if (imageReceiver == null && this.previewAnimatedEmojiDrawable == null) {
                return;
            }
            if (imageReceiver != null) {
                imageReceiver.onDetachedFromWindow();
                this.previewImageReceiver = null;
            } else if (this.previewAnimatedEmojiDrawable != null) {
                View view = this.parentView;
                this.previewAnimatedEmojiDrawable.removeView((view == null || !(view.getParent() instanceof View)) ? this.parentView : (View) this.parentView.getParent());
                this.previewAnimatedEmojiDrawable = null;
            }
        }

        public void draw(android.graphics.Canvas r26, float r27, float r28, float r29, float r30, boolean r31, boolean r32, float r33) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton.draw(android.graphics.Canvas, float, float, float, float, boolean, boolean, float):void");
        }

        protected boolean drawCounter() {
            int i = this.count;
            return ((i == 0 || (this.isTag && !this.hasName && i == 1)) && this.counterDrawable.countChangeProgress == 1.0f) ? false : true;
        }

        public void drawOverlay(Canvas canvas, float f, float f2, float f3, float f4, boolean z) {
            if (this.particles != null && LiteMode.isEnabled(8200)) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(f, f2, this.width + f, this.height + f2);
                float f5 = this.height / 2.0f;
                this.tagPath.rewind();
                this.tagPath.addRoundRect(rectF, f5, f5, Path.Direction.CW);
                this.particles.bounds.set(rectF);
                this.particles.bounds.inset(-AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(4.0f));
                StarsReactionsSheet.Particles particles = this.particles;
                particles.setBounds(particles.bounds);
                this.particles.process();
                View view = this.parentView;
                if (view != null) {
                    view.invalidate();
                }
                this.particles.draw(canvas, ColorUtils.blendARGB(ColorUtils.setAlphaComponent(this.backgroundColor, 255), ColorUtils.blendARGB(this.serviceTextColor, ColorUtils.setAlphaComponent(this.backgroundColor, 255), 0.4f), getDrawServiceShaderBackground()));
                canvas.save();
                canvas.clipPath(this.tagPath);
                this.particles.draw(canvas, this.textColor);
                canvas.restore();
            }
        }

        public void drawPreview(View view, Canvas canvas, RectF rectF, float f) {
            if (f <= 0.0f) {
                return;
            }
            ImageReceiver imageReceiver = this.previewImageReceiver;
            if (imageReceiver != null) {
                imageReceiver.setImageCoords(rectF);
                this.previewImageReceiver.setAlpha(f);
                this.previewImageReceiver.draw(canvas);
            } else {
                AnimatedEmojiDrawable animatedEmojiDrawable = this.previewAnimatedEmojiDrawable;
                if (animatedEmojiDrawable != null) {
                    animatedEmojiDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    this.previewAnimatedEmojiDrawable.setAlpha((int) (f * 255.0f));
                    this.previewAnimatedEmojiDrawable.draw(canvas);
                }
            }
            if (view != null) {
                view.invalidate();
            }
        }

        protected boolean drawTagDot() {
            return true;
        }

        protected boolean drawTextWithCounter() {
            return false;
        }

        protected int getCacheType() {
            return this.isTag ? 18 : 3;
        }

        protected float getDrawServiceShaderBackground() {
            return 0.0f;
        }

        protected ImageReceiver getImageReceiver() {
            return null;
        }

        protected boolean isOutOwner() {
            return false;
        }

        protected boolean isPlaying() {
            return false;
        }

        protected void removeImageReceiver() {
        }

        public void setUsers(ArrayList arrayList) {
            this.users = arrayList;
            if (arrayList != null) {
                Collections.sort(arrayList, ReactionsLayoutInBubble.usersComparator);
                if (this.avatarsDrawable == null) {
                    AvatarsDrawable avatarsDrawable = new AvatarsDrawable(this.parentView, false);
                    this.avatarsDrawable = avatarsDrawable;
                    avatarsDrawable.transitionDuration = 250L;
                    avatarsDrawable.transitionInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
                    avatarsDrawable.setSize(AndroidUtilities.dp(20.0f));
                    this.avatarsDrawable.width = AndroidUtilities.dp(100.0f);
                    AvatarsDrawable avatarsDrawable2 = this.avatarsDrawable;
                    avatarsDrawable2.height = this.height;
                    avatarsDrawable2.setAvatarsTextSize(AndroidUtilities.dp(22.0f));
                }
                if (this.attached) {
                    this.avatarsDrawable.onAttachedToWindow();
                }
                for (int i = 0; i < arrayList.size() && i != 3; i++) {
                    this.avatarsDrawable.setObject(i, this.currentAccount, (TLObject) arrayList.get(i));
                }
                this.avatarsDrawable.commitTransition(false);
            }
        }

        public void startAnimation() {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver = (animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null) ? this.imageReceiver : this.animatedEmojiDrawable.getImageReceiver();
            if (imageReceiver != null) {
                RLottieDrawable lottieAnimation = imageReceiver.getLottieAnimation();
                if (lottieAnimation != null) {
                    lottieAnimation.restart(true);
                    return;
                }
                AnimatedFileDrawable animation = imageReceiver.getAnimation();
                if (animation != null) {
                    animation.start();
                }
            }
        }

        public void stopAnimation() {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver = (animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null) ? this.imageReceiver : this.animatedEmojiDrawable.getImageReceiver();
            if (imageReceiver != null) {
                RLottieDrawable lottieAnimation = imageReceiver.getLottieAnimation();
                if (lottieAnimation != null) {
                    lottieAnimation.stop();
                    return;
                }
                AnimatedFileDrawable animation = imageReceiver.getAnimation();
                if (animation != null) {
                    animation.stop();
                }
            }
        }

        protected void updateColors(float f) {
            this.lastDrawnTextColor = ColorUtils.blendARGB(this.fromTextColor, ColorUtils.blendARGB(this.textColor, this.serviceTextColor, getDrawServiceShaderBackground()), f);
            int blendARGB = ColorUtils.blendARGB(this.fromBackgroundColor, ColorUtils.blendARGB(this.backgroundColor, this.serviceBackgroundColor, getDrawServiceShaderBackground()), f);
            this.lastDrawnBackgroundColor = blendARGB;
            this.lastDrawnTagDotColor = ColorUtils.blendARGB(this.fromTagDotColor, AndroidUtilities.computePerceivedBrightness(blendARGB) > 0.8f ? 0 : 1526726655, f);
        }
    }

    public class ReactionLayoutButton extends ReactionButton {
        public ReactionLayoutButton(ReactionButton reactionButton, TLRPC.ReactionCount reactionCount, boolean z, boolean z2) {
            super(reactionButton, ReactionsLayoutInBubble.this.currentAccount, ReactionsLayoutInBubble.this.parentView, reactionCount, z, z2, ReactionsLayoutInBubble.this.resourcesProvider);
        }

        @Override
        protected float getDrawServiceShaderBackground() {
            return ReactionsLayoutInBubble.this.drawServiceShaderBackground;
        }

        @Override
        protected ImageReceiver getImageReceiver() {
            return (ImageReceiver) ReactionsLayoutInBubble.this.animatedReactions.get(this.visibleReaction);
        }

        @Override
        protected boolean isOutOwner() {
            return ReactionsLayoutInBubble.this.messageObject.isOutOwner();
        }

        @Override
        protected boolean isPlaying() {
            return ReactionsEffectOverlay.isPlaying(ReactionsLayoutInBubble.this.messageObject.getId(), ReactionsLayoutInBubble.this.messageObject.getGroupId(), this.visibleReaction);
        }

        @Override
        protected void removeImageReceiver() {
            ReactionsLayoutInBubble.this.animatedReactions.remove(this.visibleReaction);
        }
    }

    public static class VisibleReaction {
        public long documentId;
        public long effectId;
        public String emojicon;
        public long hash;
        public boolean isEffect;
        public boolean isStar;
        public boolean premium;
        public boolean sticker;

        public static VisibleReaction asStar() {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.isStar = true;
            return visibleReaction;
        }

        public static VisibleReaction fromCustomEmoji(Long l) {
            VisibleReaction visibleReaction = new VisibleReaction();
            long longValue = l.longValue();
            visibleReaction.documentId = longValue;
            visibleReaction.hash = longValue;
            return visibleReaction;
        }

        public static VisibleReaction fromEmojicon(String str) {
            if (str == null) {
                str = "";
            }
            VisibleReaction visibleReaction = new VisibleReaction();
            if (str.startsWith("animated_")) {
                try {
                    long parseLong = Long.parseLong(str.substring(9));
                    visibleReaction.documentId = parseLong;
                    visibleReaction.hash = parseLong;
                } catch (Exception unused) {
                }
                return visibleReaction;
            }
            visibleReaction.emojicon = str;
            visibleReaction.hash = str.hashCode();
            return visibleReaction;
        }

        public static VisibleReaction fromEmojicon(TLRPC.TL_availableReaction tL_availableReaction) {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.emojicon = tL_availableReaction.reaction;
            visibleReaction.hash = r3.hashCode();
            return visibleReaction;
        }

        public static VisibleReaction fromTL(TLRPC.Reaction reaction) {
            long j;
            VisibleReaction visibleReaction = new VisibleReaction();
            if (reaction instanceof TLRPC.TL_reactionPaid) {
                visibleReaction.isStar = true;
            } else {
                if (reaction instanceof TLRPC.TL_reactionEmoji) {
                    String str = ((TLRPC.TL_reactionEmoji) reaction).emoticon;
                    visibleReaction.emojicon = str;
                    j = str.hashCode();
                } else if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                    j = ((TLRPC.TL_reactionCustomEmoji) reaction).document_id;
                    visibleReaction.documentId = j;
                }
                visibleReaction.hash = j;
            }
            return visibleReaction;
        }

        public static VisibleReaction fromTL(TLRPC.TL_availableEffect tL_availableEffect) {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.isEffect = true;
            long j = tL_availableEffect.id;
            visibleReaction.effectId = j;
            visibleReaction.sticker = tL_availableEffect.effect_animation_id == 0;
            visibleReaction.documentId = tL_availableEffect.effect_sticker_id;
            visibleReaction.hash = j;
            visibleReaction.premium = tL_availableEffect.premium_required;
            visibleReaction.emojicon = tL_availableEffect.emoticon;
            return visibleReaction;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            VisibleReaction visibleReaction = (VisibleReaction) obj;
            return this.documentId == visibleReaction.documentId && Objects.equals(this.emojicon, visibleReaction.emojicon);
        }

        public VisibleReaction flatten() {
            String findAnimatedEmojiEmoticon;
            long j = this.documentId;
            return (j == 0 || (findAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, j), null)) == null) ? this : fromEmojicon(findAnimatedEmojiEmoticon);
        }

        public int hashCode() {
            return Objects.hash(this.emojicon, Long.valueOf(this.documentId));
        }

        public boolean isSame(TLRPC.Reaction reaction) {
            return reaction instanceof TLRPC.TL_reactionEmoji ? TextUtils.equals(((TLRPC.TL_reactionEmoji) reaction).emoticon, this.emojicon) : (reaction instanceof TLRPC.TL_reactionCustomEmoji) && ((TLRPC.TL_reactionCustomEmoji) reaction).document_id == this.documentId;
        }

        public CharSequence toCharSequence(int i) {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(AndroidUtilities.dp(i));
            if (!TextUtils.isEmpty(this.emojicon)) {
                return Emoji.replaceEmoji(this.emojicon, textPaint.getFontMetricsInt(), false);
            }
            SpannableString spannableString = new SpannableString("😀");
            spannableString.setSpan(new AnimatedEmojiSpan(this.documentId, textPaint.getFontMetricsInt()), 0, spannableString.length(), 17);
            return spannableString;
        }

        public CharSequence toCharSequence(Paint.FontMetricsInt fontMetricsInt) {
            if (!TextUtils.isEmpty(this.emojicon)) {
                return this.emojicon;
            }
            SpannableString spannableString = new SpannableString("😀");
            spannableString.setSpan(new AnimatedEmojiSpan(this.documentId, fontMetricsInt), 0, spannableString.length(), 17);
            return spannableString;
        }

        public String toString() {
            TLRPC.Document findDocument;
            if (!TextUtils.isEmpty(this.emojicon)) {
                return this.emojicon;
            }
            long j = this.documentId;
            if (j != 0 && (findDocument = AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, j)) != null) {
                return MessageObject.findAnimatedEmojiEmoticon(findDocument, null);
            }
            return "VisibleReaction{" + this.documentId + ", " + this.emojicon + "}";
        }

        public TLRPC.Reaction toTLReaction() {
            if (this.isStar) {
                return new TLRPC.TL_reactionPaid();
            }
            if (this.emojicon != null) {
                TLRPC.TL_reactionEmoji tL_reactionEmoji = new TLRPC.TL_reactionEmoji();
                tL_reactionEmoji.emoticon = this.emojicon;
                return tL_reactionEmoji;
            }
            TLRPC.TL_reactionCustomEmoji tL_reactionCustomEmoji = new TLRPC.TL_reactionCustomEmoji();
            tL_reactionCustomEmoji.document_id = this.documentId;
            return tL_reactionCustomEmoji;
        }
    }

    public ReactionsLayoutInBubble(ChatMessageCell chatMessageCell) {
        this.parentView = chatMessageCell;
        initPaints(this.resourcesProvider);
        this.touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
    }

    public static boolean equalsTLReaction(TLRPC.Reaction reaction, TLRPC.Reaction reaction2) {
        return ((reaction instanceof TLRPC.TL_reactionEmoji) && (reaction2 instanceof TLRPC.TL_reactionEmoji)) ? TextUtils.equals(((TLRPC.TL_reactionEmoji) reaction).emoticon, ((TLRPC.TL_reactionEmoji) reaction2).emoticon) : (reaction instanceof TLRPC.TL_reactionCustomEmoji) && (reaction2 instanceof TLRPC.TL_reactionCustomEmoji) && ((TLRPC.TL_reactionCustomEmoji) reaction).document_id == ((TLRPC.TL_reactionCustomEmoji) reaction2).document_id;
    }

    private boolean equalsUsersList(ArrayList arrayList, ArrayList arrayList2) {
        if (arrayList == null || arrayList2 == null || arrayList.size() != arrayList2.size()) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TLObject tLObject = (TLObject) arrayList.get(i);
            TLObject tLObject2 = (TLObject) arrayList2.get(i);
            if (tLObject == null || tLObject2 == null || getPeerId(tLObject) != getPeerId(tLObject2)) {
                return false;
            }
        }
        return true;
    }

    public static void fillTagPath(RectF rectF, Path path) {
        fillTagPath(rectF, AndroidUtilities.rectTmp, path);
    }

    public static void fillTagPath(RectF rectF, RectF rectF2, Path path) {
        path.rewind();
        float f = rectF.left;
        rectF2.set(f, rectF.top, AndroidUtilities.dp(12.0f) + f, rectF.top + AndroidUtilities.dp(12.0f));
        path.arcTo(rectF2, -90.0f, -90.0f, false);
        rectF2.set(rectF.left, rectF.bottom - AndroidUtilities.dp(12.0f), rectF.left + AndroidUtilities.dp(12.0f), rectF.bottom);
        path.arcTo(rectF2, -180.0f, -90.0f, false);
        float f2 = rectF.height() > ((float) AndroidUtilities.dp(26.0f)) ? 1.4f : 0.0f;
        float dpf2 = rectF.right - AndroidUtilities.dpf2(9.09f);
        float dpf22 = dpf2 - AndroidUtilities.dpf2(0.056f);
        float dpf23 = dpf2 + AndroidUtilities.dpf2(1.22f);
        float dpf24 = dpf2 + AndroidUtilities.dpf2(3.07f);
        float dpf25 = dpf2 + AndroidUtilities.dpf2(2.406f);
        float dpf26 = dpf2 + AndroidUtilities.dpf2(8.27f + f2);
        float dpf27 = dpf2 + AndroidUtilities.dpf2(8.923f + f2);
        float dpf28 = rectF.top + AndroidUtilities.dpf2(1.753f);
        float dpf29 = rectF.bottom - AndroidUtilities.dpf2(1.753f);
        float dpf210 = rectF.top + AndroidUtilities.dpf2(0.663f);
        float dpf211 = rectF.bottom - AndroidUtilities.dpf2(0.663f);
        float f3 = 10.263f + f2;
        float dpf212 = rectF.top + AndroidUtilities.dpf2(f3);
        float dpf213 = rectF.bottom - AndroidUtilities.dpf2(f3);
        float f4 = f2 + 11.333f;
        float dpf214 = rectF.top + AndroidUtilities.dpf2(f4);
        float dpf215 = rectF.bottom - AndroidUtilities.dpf2(f4);
        path.lineTo(dpf22, rectF.bottom);
        path.cubicTo(dpf23, rectF.bottom, dpf25, dpf211, dpf24, dpf29);
        path.lineTo(dpf26, dpf213);
        path.cubicTo(dpf27, dpf215, dpf27, dpf214, dpf26, dpf212);
        path.lineTo(dpf24, dpf28);
        float f5 = rectF.top;
        path.cubicTo(dpf25, dpf210, dpf23, f5, dpf22, f5);
        path.close();
    }

    private static long getPeerId(TLObject tLObject) {
        if (tLObject instanceof TLRPC.User) {
            return ((TLRPC.User) tLObject).id;
        }
        if (tLObject instanceof TLRPC.Chat) {
            return ((TLRPC.Chat) tLObject).id;
        }
        return 0L;
    }

    public static void initPaints(Theme.ResourcesProvider resourcesProvider) {
        paint.setColor(Theme.getColor(Theme.key_chat_inLoader, resourcesProvider));
        textPaint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
        textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.bold());
        cutTagPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void lambda$checkTouchEvent$1(ReactionButton reactionButton) {
        this.parentView.getDelegate().didPressReaction(this.parentView, reactionButton.reactionCount, true, 0.0f, 0.0f);
        reactionButton.bounce.setPressed(false);
        this.lastSelectedButton = null;
        this.pressed = false;
        this.longPressRunnable = null;
    }

    public static int lambda$static$0(TLObject tLObject, TLObject tLObject2) {
        return (int) (getPeerId(tLObject) - getPeerId(tLObject2));
    }

    public static boolean reactionsEqual(TLRPC.Reaction reaction, TLRPC.Reaction reaction2) {
        if (!(reaction instanceof TLRPC.TL_reactionEmoji)) {
            return (reaction instanceof TLRPC.TL_reactionCustomEmoji) && (reaction2 instanceof TLRPC.TL_reactionCustomEmoji) && ((TLRPC.TL_reactionCustomEmoji) reaction).document_id == ((TLRPC.TL_reactionCustomEmoji) reaction2).document_id;
        }
        if (reaction2 instanceof TLRPC.TL_reactionEmoji) {
            return TextUtils.equals(((TLRPC.TL_reactionEmoji) reaction).emoticon, ((TLRPC.TL_reactionEmoji) reaction2).emoticon);
        }
        return false;
    }

    public boolean animateChange() {
        AvatarsDrawable avatarsDrawable;
        CounterView.CounterDrawable counterDrawable;
        if (this.messageObject == null) {
            return false;
        }
        this.lastDrawingReactionButtonsTmp.clear();
        for (int i = 0; i < this.outButtons.size(); i++) {
            ((ReactionButton) this.outButtons.get(i)).detach();
        }
        this.outButtons.clear();
        this.lastDrawingReactionButtonsTmp.putAll(this.lastDrawingReactionButtons);
        boolean z = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton reactionButton = (ReactionButton) this.reactionButtons.get(i2);
            ReactionButton reactionButton2 = (ReactionButton) this.lastDrawingReactionButtonsTmp.get(reactionButton.key);
            if (reactionButton2 != null && reactionButton.isSmall != reactionButton2.isSmall) {
                reactionButton2 = null;
            }
            if (reactionButton2 != null) {
                this.lastDrawingReactionButtonsTmp.remove(reactionButton.key);
                int i3 = reactionButton.x;
                int i4 = reactionButton2.x;
                if (i3 == i4 && reactionButton.y == reactionButton2.y && reactionButton.width == reactionButton2.width && reactionButton.count == reactionButton2.count && reactionButton.choosen == reactionButton2.choosen && reactionButton.avatarsDrawable == null && reactionButton2.avatarsDrawable == null) {
                    reactionButton.animationType = 0;
                } else {
                    reactionButton.animateFromX = i4;
                    reactionButton.animateFromY = reactionButton2.y;
                    reactionButton.animateFromWidth = reactionButton2.width;
                    reactionButton.fromTextColor = reactionButton2.lastDrawnTextColor;
                    reactionButton.fromBackgroundColor = reactionButton2.lastDrawnBackgroundColor;
                    reactionButton.fromTagDotColor = reactionButton2.lastDrawnTagDotColor;
                    reactionButton.animationType = 3;
                    int i5 = reactionButton.count;
                    int i6 = reactionButton2.count;
                    if (i5 != i6 && (counterDrawable = reactionButton.counterDrawable) != null) {
                        counterDrawable.setCount(i6, false);
                        reactionButton.counterDrawable.setCount(reactionButton.count, true);
                    }
                    AvatarsDrawable avatarsDrawable2 = reactionButton.avatarsDrawable;
                    if (avatarsDrawable2 != null || reactionButton2.avatarsDrawable != null) {
                        if (avatarsDrawable2 == null) {
                            reactionButton.setUsers(new ArrayList());
                        }
                        if (reactionButton2.avatarsDrawable == null) {
                            reactionButton2.setUsers(new ArrayList());
                        }
                        if (!equalsUsersList(reactionButton2.users, reactionButton.users) && (avatarsDrawable = reactionButton.avatarsDrawable) != null) {
                            avatarsDrawable.animateFromState(reactionButton2.avatarsDrawable, this.currentAccount, false);
                        }
                    }
                }
            } else {
                reactionButton.animationType = 1;
            }
            z = true;
        }
        if (!this.lastDrawingReactionButtonsTmp.isEmpty()) {
            this.outButtons.addAll(this.lastDrawingReactionButtonsTmp.values());
            for (int i7 = 0; i7 < this.outButtons.size(); i7++) {
                ((ReactionButton) this.outButtons.get(i7)).drawImage = ((ReactionButton) this.outButtons.get(i7)).lastImageDrawn;
                ((ReactionButton) this.outButtons.get(i7)).attach();
            }
            z = true;
        }
        if (this.wasDrawn) {
            float f = this.lastDrawnX;
            if (f != this.x || this.lastDrawnY != this.y) {
                this.animateMove = true;
                this.fromX = f;
                this.fromY = this.lastDrawnY;
                z = true;
            }
        }
        int i8 = this.lastDrawnWidth;
        if (i8 != this.width) {
            this.animateWidth = true;
            this.fromWidth = i8;
            z = true;
        }
        int i9 = this.lastDrawTotalHeight;
        if (i9 == this.totalHeight) {
            return z;
        }
        this.animateHeight = true;
        this.animateFromTotalHeight = i9;
        return true;
    }

    public void animateReaction(VisibleReaction visibleReaction) {
        if (visibleReaction.documentId != 0 || this.animatedReactions.get(visibleReaction) != null) {
            if (!this.tags || visibleReaction.documentId == 0) {
                return;
            }
            for (int i = 0; i < this.reactionButtons.size(); i++) {
                if (visibleReaction.isSame(((ReactionButton) this.reactionButtons.get(i)).reaction)) {
                    ((ReactionButton) this.reactionButtons.get(i)).startAnimation();
                    return;
                }
            }
            return;
        }
        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setParentView(this.parentView);
        int i2 = animationUniq;
        animationUniq = i2 + 1;
        imageReceiver.setUniqKeyPrefix(Integer.toString(i2));
        TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(visibleReaction.emojicon);
        if (tL_availableReaction != null) {
            imageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_nolimit", null, "tgs", tL_availableReaction, 1);
        }
        imageReceiver.setAutoRepeat(0);
        imageReceiver.onAttachedToWindow();
        this.animatedReactions.put(visibleReaction, imageReceiver);
    }

    public boolean checkTouchEvent(MotionEvent motionEvent) {
        MessageObject messageObject;
        TLRPC.Message message;
        int i = 0;
        if (this.isEmpty || this.isSmall || (messageObject = this.messageObject) == null || (message = messageObject.messageOwner) == null || message.reactions == null) {
            return false;
        }
        float x = motionEvent.getX() - this.x;
        float y = motionEvent.getY() - this.y;
        if (motionEvent.getAction() == 0) {
            int size = this.reactionButtons.size();
            while (true) {
                if (i >= size) {
                    break;
                }
                if (x <= ((ReactionButton) this.reactionButtons.get(i)).x || x >= ((ReactionButton) this.reactionButtons.get(i)).x + ((ReactionButton) this.reactionButtons.get(i)).width || y <= ((ReactionButton) this.reactionButtons.get(i)).y || y >= ((ReactionButton) this.reactionButtons.get(i)).y + ((ReactionButton) this.reactionButtons.get(i)).height) {
                    i++;
                } else {
                    this.lastX = motionEvent.getX();
                    this.lastY = motionEvent.getY();
                    this.lastSelectedButton = (ReactionButton) this.reactionButtons.get(i);
                    Runnable runnable = this.longPressRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.longPressRunnable = null;
                    }
                    this.lastSelectedButton.bounce.setPressed(true);
                    final ReactionButton reactionButton = this.lastSelectedButton;
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public final void run() {
                            ReactionsLayoutInBubble.this.lambda$checkTouchEvent$1(reactionButton);
                        }
                    };
                    this.longPressRunnable = runnable2;
                    AndroidUtilities.runOnUIThread(runnable2, ViewConfiguration.getLongPressTimeout());
                    this.pressed = true;
                }
            }
        } else if (motionEvent.getAction() == 2) {
            if ((this.pressed && Math.abs(motionEvent.getX() - this.lastX) > this.touchSlop) || Math.abs(motionEvent.getY() - this.lastY) > this.touchSlop) {
                this.pressed = false;
                ReactionButton reactionButton2 = this.lastSelectedButton;
                if (reactionButton2 != null) {
                    reactionButton2.bounce.setPressed(false);
                }
                this.lastSelectedButton = null;
                Runnable runnable3 = this.longPressRunnable;
                if (runnable3 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable3);
                    this.longPressRunnable = null;
                }
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            Runnable runnable4 = this.longPressRunnable;
            if (runnable4 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable4);
                this.longPressRunnable = null;
            }
            if (this.pressed && this.lastSelectedButton != null && motionEvent.getAction() == 1 && this.parentView.getDelegate() != null) {
                this.parentView.getDelegate().didPressReaction(this.parentView, this.lastSelectedButton.reactionCount, false, motionEvent.getX(), motionEvent.getY());
            }
            this.pressed = false;
            ReactionButton reactionButton3 = this.lastSelectedButton;
            if (reactionButton3 != null) {
                reactionButton3.bounce.setPressed(false);
            }
            this.lastSelectedButton = null;
        }
        return this.pressed;
    }

    public void draw(Canvas canvas, float f, Integer num) {
        float f2;
        if (this.isEmpty && this.outButtons.isEmpty()) {
            return;
        }
        float f3 = this.x;
        float f4 = this.y;
        if (this.isEmpty) {
            f3 = this.lastDrawnX;
            f4 = this.lastDrawnY;
        } else if (this.animateMove) {
            float f5 = 1.0f - f;
            f3 = (f3 * f) + (this.fromX * f5);
            f4 = (f4 * f) + (this.fromY * f5);
        }
        float f6 = f3;
        float f7 = f4;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            ReactionButton reactionButton = (ReactionButton) this.reactionButtons.get(i);
            if (this.scrimViewReaction == null && num == null && this.scrimProgress < 0.5f) {
                reactionButton.detachPreview();
            }
            if (!Integer.valueOf(reactionButton.reaction.hashCode()).equals(this.scrimViewReaction) && (num == null || reactionButton.reaction.hashCode() == num.intValue())) {
                canvas.save();
                float f8 = reactionButton.x;
                float f9 = reactionButton.y;
                if (f != 1.0f && reactionButton.animationType == 3) {
                    float f10 = 1.0f - f;
                    f8 = (f8 * f) + (reactionButton.animateFromX * f10);
                    f9 = (f9 * f) + (reactionButton.animateFromY * f10);
                }
                if (f == 1.0f || reactionButton.animationType != 1) {
                    f2 = 1.0f;
                } else {
                    float f11 = (f * 0.5f) + 0.5f;
                    canvas.scale(f11, f11, f6 + f8 + (reactionButton.width / 2.0f), f7 + f9 + (reactionButton.height / 2.0f));
                    f2 = f;
                }
                reactionButton.draw(canvas, f6 + f8, f7 + f9, reactionButton.animationType == 3 ? f : 1.0f, f2, num != null, this.scrimDirection, this.scrimProgress);
                canvas.restore();
            }
        }
        for (int i2 = 0; i2 < this.outButtons.size(); i2++) {
            ReactionButton reactionButton2 = (ReactionButton) this.outButtons.get(i2);
            float f12 = 1.0f - f;
            float f13 = (f12 * 0.5f) + 0.5f;
            canvas.save();
            canvas.scale(f13, f13, reactionButton2.x + f6 + (reactionButton2.width / 2.0f), reactionButton2.y + f7 + (reactionButton2.height / 2.0f));
            ((ReactionButton) this.outButtons.get(i2)).draw(canvas, reactionButton2.x + f6, f7 + reactionButton2.y, 1.0f, f12, false, this.scrimDirection, this.scrimProgress);
            canvas.restore();
        }
    }

    public void drawOverlay(Canvas canvas, float f) {
        float f2;
        if (this.isEmpty && this.outButtons.isEmpty()) {
            return;
        }
        float f3 = this.x;
        float f4 = this.y;
        if (this.isEmpty) {
            f3 = this.lastDrawnX;
            f4 = this.lastDrawnY;
        } else if (this.animateMove) {
            float f5 = 1.0f - f;
            f3 = (f3 * f) + (this.fromX * f5);
            f4 = (f4 * f) + (this.fromY * f5);
        }
        float f6 = f3;
        float f7 = f4;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            ReactionButton reactionButton = (ReactionButton) this.reactionButtons.get(i);
            if (reactionButton.paid) {
                canvas.save();
                float f8 = reactionButton.x;
                float f9 = reactionButton.y;
                if (f != 1.0f && reactionButton.animationType == 3) {
                    float f10 = 1.0f - f;
                    f8 = (f8 * f) + (reactionButton.animateFromX * f10);
                    f9 = (f9 * f) + (reactionButton.animateFromY * f10);
                }
                if (f == 1.0f || reactionButton.animationType != 1) {
                    f2 = 1.0f;
                } else {
                    float f11 = (f * 0.5f) + 0.5f;
                    canvas.scale(f11, f11, f6 + f8 + (reactionButton.width / 2.0f), f7 + f9 + (reactionButton.height / 2.0f));
                    f2 = f;
                }
                reactionButton.drawOverlay(canvas, f6 + f8, f7 + f9, reactionButton.animationType == 3 ? f : 1.0f, f2, false);
                canvas.restore();
            }
        }
        for (int i2 = 0; i2 < this.outButtons.size(); i2++) {
            if (((ReactionButton) this.outButtons.get(i2)).paid) {
                float f12 = 1.0f - f;
                float f13 = (f12 * 0.5f) + 0.5f;
                canvas.save();
                canvas.scale(f13, f13, r1.x + f6 + (r1.width / 2.0f), r1.y + f7 + (r1.height / 2.0f));
                ((ReactionButton) this.outButtons.get(i2)).drawOverlay(canvas, r1.x + f6, f7 + r1.y, 1.0f, f12, false);
                canvas.restore();
            }
        }
    }

    public void drawPreview(View view, Canvas canvas, int i, Integer num) {
        if (this.isEmpty && this.outButtons.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton reactionButton = (ReactionButton) this.reactionButtons.get(i2);
            if ((num == null || reactionButton.reaction.hashCode() == num.intValue()) && num != null) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(reactionButton.drawingImageRect);
                float dp = AndroidUtilities.dp(140.0f);
                float dp2 = AndroidUtilities.dp(14.0f);
                float clamp = Utilities.clamp(rectF.left - AndroidUtilities.dp(12.0f), ((this.parentView != null ? r5.getParentWidth() : AndroidUtilities.displaySize.x) - dp) - AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                RectF rectF2 = this.scrimRect;
                float f = rectF.top - dp2;
                float f2 = i;
                rectF2.set(clamp, (f - dp) + f2, dp + clamp, f + f2);
                float interpolation = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.scrimProgress);
                RectF rectF3 = this.scrimRect;
                AndroidUtilities.lerp(rectF, rectF3, interpolation, rectF3);
                reactionButton.attachPreview(view);
                Rect rect = AndroidUtilities.rectTmp2;
                RectF rectF4 = this.scrimRect;
                rect.set((int) rectF4.left, (int) rectF4.top, (int) rectF4.right, (int) rectF4.bottom);
                float f3 = 1.0f - interpolation;
                if (f3 > 0.0f) {
                    canvas.saveLayerAlpha(this.scrimRect, (int) (f3 * 255.0f), 31);
                    reactionButton.drawImage(canvas, rect, 1.0f);
                    canvas.restore();
                }
                reactionButton.drawPreview(view, canvas, this.scrimRect, interpolation);
            }
        }
    }

    public float getCurrentTotalHeight(float f) {
        return this.animateHeight ? (this.animateFromTotalHeight * (1.0f - f)) + (this.totalHeight * f) : this.totalHeight;
    }

    public float getCurrentWidth(float f) {
        return this.animateWidth ? (this.fromWidth * (1.0f - f)) + (this.width * f) : this.width;
    }

    public ReactionButton getReactionButton(String str) {
        if (this.isSmall) {
            ReactionButton reactionButton = (ReactionButton) this.lastDrawingReactionButtons.get(str + "_");
            if (reactionButton != null) {
                return reactionButton;
            }
        }
        return (ReactionButton) this.lastDrawingReactionButtons.get(str);
    }

    public ReactionButton getReactionButton(VisibleReaction visibleReaction) {
        String l;
        if (visibleReaction.isStar) {
            l = "stars";
        } else {
            String str = visibleReaction.emojicon;
            l = str != null ? str : Long.toString(visibleReaction.documentId);
        }
        return getReactionButton(l);
    }

    public boolean hasOverlay() {
        return this.hasPaidReaction && !(this.isEmpty && this.outButtons.isEmpty()) && LiteMode.isEnabled(8200);
    }

    public void measure(int r14, int r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.measure(int, int):void");
    }

    public void onAttachToWindow() {
        this.attached = true;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            ((ReactionButton) this.reactionButtons.get(i)).attach();
        }
    }

    public void onDetachFromWindow() {
        this.attached = false;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            ((ReactionButton) this.reactionButtons.get(i)).detach();
        }
        if (!this.animatedReactions.isEmpty()) {
            Iterator it = this.animatedReactions.values().iterator();
            while (it.hasNext()) {
                ((ImageReceiver) it.next()).onDetachedFromWindow();
            }
        }
        this.animatedReactions.clear();
    }

    public void recordDrawingState() {
        this.lastDrawingReactionButtons.clear();
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.lastDrawingReactionButtons.put(((ReactionButton) this.reactionButtons.get(i)).key, (ReactionButton) this.reactionButtons.get(i));
        }
        this.wasDrawn = !this.isEmpty;
        this.lastDrawnX = this.x;
        this.lastDrawnY = this.y;
        this.lastDrawnWidth = this.width;
        this.lastDrawTotalHeight = this.totalHeight;
    }

    public void resetAnimation() {
        for (int i = 0; i < this.outButtons.size(); i++) {
            ((ReactionButton) this.outButtons.get(i)).detach();
        }
        this.outButtons.clear();
        this.animateMove = false;
        this.animateWidth = false;
        this.animateHeight = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ((ReactionButton) this.reactionButtons.get(i2)).animationType = 0;
        }
    }

    public void setMessage(org.telegram.messenger.MessageObject r20, boolean r21, boolean r22, org.telegram.ui.ActionBar.Theme.ResourcesProvider r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.setMessage(org.telegram.messenger.MessageObject, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public void setScrimProgress(float f) {
        this.scrimProgress = f;
    }

    public void setScrimProgress(float f, boolean z) {
        this.scrimProgress = f;
        this.scrimDirection = z;
    }

    public void setScrimReaction(Integer num) {
        this.scrimViewReaction = num;
    }

    public boolean verifyDrawable(Drawable drawable) {
        return drawable instanceof AnimatedTextView.AnimatedTextDrawable;
    }
}
