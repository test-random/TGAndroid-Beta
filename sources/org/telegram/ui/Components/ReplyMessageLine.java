package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;

public class ReplyMessageLine {
    public int backgroundColor;
    public final AnimatedColor backgroundColorAnimated;
    private LoadingDrawable backgroundLoadingDrawable;
    public int color1;
    public final AnimatedColor color1Animated;
    public int color2;
    public final AnimatedFloat color2Alpha;
    public final AnimatedColor color2Animated;
    public int color3;
    public final AnimatedFloat color3Alpha;
    public final AnimatedColor color3Animated;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji;
    private long emojiDocumentId;
    private boolean emojiLoaded;
    public final AnimatedFloat emojiLoadedT;
    private float emojiOffsetX;
    private float emojiOffsetY;
    public boolean hasColor2;
    public boolean hasColor3;
    private IconCoords[] iconCoords;
    private boolean lastHasColor3;
    private float lastHeight;
    private long lastLoadingTTime;
    private boolean loading;
    public final AnimatedFloat loadingStateT;
    private float loadingT;
    private float loadingTranslationT;
    public int nameColor;
    public final AnimatedColor nameColorAnimated;
    private final View parentView;
    private boolean reversedOut;
    private boolean sponsored;
    public final AnimatedFloat switchStateT;
    private int wasColorId;
    private int wasMessageId;
    private final RectF rectF = new RectF();
    private final Path clipPath = new Path();
    private final Paint color1Paint = new Paint(1);
    private final Paint color2Paint = new Paint(1);
    private final Paint color3Paint = new Paint(1);
    public final float[] radii = new float[8];
    private final Path lineClipPath = new Path();
    private final Path backgroundPath = new Path();
    public final Paint backgroundPaint = new Paint();
    private Path color2Path = new Path();
    private Path color3Path = new Path();
    private int switchedCount = 0;
    private float emojiAlpha = 1.0f;

    public static class IconCoords {
        public float a;
        public boolean q;
        public float s;
        public float x;
        public float y;

        public IconCoords(float f, float f2, float f3, float f4) {
            this.x = f;
            this.y = f2;
            this.s = f3;
            this.a = f4;
        }

        public IconCoords(float f, float f2, float f3, float f4, boolean z) {
            this(f, f2, f3, f4);
            this.q = z;
        }
    }

    public ReplyMessageLine(View view) {
        this.parentView = view;
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view2) {
                if (ReplyMessageLine.this.emoji != null) {
                    ReplyMessageLine.this.emoji.attach();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View view2) {
                if (ReplyMessageLine.this.emoji != null) {
                    ReplyMessageLine.this.emoji.detach();
                }
            }
        });
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.backgroundColorAnimated = new AnimatedColor(view, 0L, 400L, cubicBezierInterpolator);
        this.color1Animated = new AnimatedColor(view, 0L, 400L, cubicBezierInterpolator);
        this.color2Animated = new AnimatedColor(view, 0L, 400L, cubicBezierInterpolator);
        this.color3Animated = new AnimatedColor(view, 0L, 400L, cubicBezierInterpolator);
        this.nameColorAnimated = new AnimatedColor(view, 0L, 400L, cubicBezierInterpolator);
        this.color2Alpha = new AnimatedFloat(view, 0L, 400L, cubicBezierInterpolator);
        this.color3Alpha = new AnimatedFloat(view, 0L, 400L, cubicBezierInterpolator);
        this.emojiLoadedT = new AnimatedFloat(view, 0L, 440L, cubicBezierInterpolator);
        this.loadingStateT = new AnimatedFloat(view, 0L, 320L, cubicBezierInterpolator);
        this.switchStateT = new AnimatedFloat(view, 0L, 320L, cubicBezierInterpolator);
    }

    private void checkColorPathes(float f) {
        if (Math.abs(this.lastHeight - f) > 3.0f || this.lastHasColor3 != this.hasColor3) {
            float dpf2 = AndroidUtilities.dpf2(3.0f);
            float dpf22 = AndroidUtilities.dpf2(6.33f);
            float dpf23 = AndroidUtilities.dpf2(3.0f);
            float dpf24 = AndroidUtilities.dpf2(3.33f);
            float f2 = dpf24 + dpf23;
            this.color2Path.rewind();
            float f3 = f2;
            while (f3 < f) {
                float f4 = dpf2 + 1.0f;
                this.color2Path.moveTo(f4, f3 - 1.0f);
                float f5 = f3 + dpf22;
                this.color2Path.lineTo(f4, f5);
                this.color2Path.lineTo(0.0f, f5 + dpf23);
                this.color2Path.lineTo(0.0f, f3 + dpf23);
                this.color2Path.close();
                f3 += dpf22 + dpf23 + dpf24;
                if (this.hasColor3) {
                    f3 += dpf22;
                }
            }
            if (this.hasColor3) {
                this.color3Path.rewind();
                for (float f6 = f2 + dpf22; f6 < f; f6 += dpf22 + dpf23 + dpf24 + dpf22) {
                    float f7 = dpf2 + 1.0f;
                    this.color3Path.moveTo(f7, f6 - 1.0f);
                    float f8 = f6 + dpf22;
                    this.color3Path.lineTo(f7, f8);
                    this.color3Path.lineTo(0.0f, f8 + dpf23);
                    this.color3Path.lineTo(0.0f, f6 + dpf23);
                    this.color3Path.close();
                }
            }
            this.lastHeight = f;
            this.lastHasColor3 = this.hasColor3;
        }
    }

    private void incrementLoadingT() {
        long currentTimeMillis = System.currentTimeMillis();
        float f = this.loadingStateT.set(this.loading);
        this.loadingT += ((float) Math.min(30L, currentTimeMillis - this.lastLoadingTTime)) * f;
        this.loadingTranslationT += ((float) Math.min(30L, currentTimeMillis - this.lastLoadingTTime)) * f;
        this.lastLoadingTTime = currentTimeMillis;
    }

    private boolean isEmojiLoaded() {
        if (this.emojiLoaded) {
            return true;
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
        if (swapAnimatedEmojiDrawable == null || !(swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) {
            return false;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) this.emoji.getDrawable();
        if (animatedEmojiDrawable.getImageReceiver() == null || !animatedEmojiDrawable.getImageReceiver().hasImageLoaded()) {
            return false;
        }
        this.emojiLoaded = true;
        return true;
    }

    private void resolveColor(MessageObject messageObject, int i, Theme.ResourcesProvider resourcesProvider) {
        int i2;
        if (resourcesProvider != null) {
            resourcesProvider.isDark();
        } else {
            Theme.isCurrentThemeDark();
        }
        if (this.wasColorId != i) {
            int id = messageObject != null ? messageObject.getId() : 0;
            if (id == this.wasMessageId) {
                this.switchedCount++;
            }
            this.wasColorId = i;
            this.wasMessageId = id;
        }
        if (i < 7) {
            i2 = Theme.keys_avatar_nameInMessage[i];
        } else {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(messageObject != null ? messageObject.currentAccount : UserConfig.selectedAccount).peerColors;
            MessagesController.PeerColor color = peerColors != null ? peerColors.getColor(i) : null;
            if (color != null) {
                this.color1 = color.getColor(0, resourcesProvider);
                this.color2 = color.getColor(1, resourcesProvider);
                int color2 = color.getColor(2, resourcesProvider);
                this.color3 = color2;
                int i3 = this.color2;
                int i4 = this.color1;
                this.hasColor2 = i3 != i4;
                boolean z = color2 != i4;
                this.hasColor3 = z;
                if (z) {
                    this.color3 = i3;
                    this.color2 = color2;
                    return;
                }
                return;
            }
            i2 = (messageObject == null || !messageObject.isOutOwner()) ? Theme.key_chat_inReplyLine : Theme.key_chat_outReplyLine;
        }
        int color3 = Theme.getColor(i2, resourcesProvider);
        this.color3 = color3;
        this.color2 = color3;
        this.color1 = color3;
        this.hasColor3 = false;
        this.hasColor2 = false;
    }

    public int check(org.telegram.messenger.MessageObject r18, org.telegram.tgnet.TLRPC.User r19, org.telegram.tgnet.TLRPC.Chat r20, org.telegram.ui.ActionBar.Theme.ResourcesProvider r21, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReplyMessageLine.check(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.ui.ActionBar.Theme$ResourcesProvider, int):int");
    }

    public void drawBackground(Canvas canvas, RectF rectF, float f, float f2, float f3, float f4) {
        drawBackground(canvas, rectF, f, f2, f3, f4, false, false);
    }

    public void drawBackground(Canvas canvas, RectF rectF, float f, float f2, float f3, float f4, boolean z, boolean z2) {
        float[] fArr = this.radii;
        float max = Math.max(AndroidUtilities.dp((int) Math.floor(SharedConfig.bubbleRadius / 3.0f)), AndroidUtilities.dp(f));
        fArr[1] = max;
        fArr[0] = max;
        float[] fArr2 = this.radii;
        float dp = AndroidUtilities.dp(f2);
        fArr2[3] = dp;
        fArr2[2] = dp;
        float[] fArr3 = this.radii;
        float dp2 = AndroidUtilities.dp(f3);
        fArr3[5] = dp2;
        fArr3[4] = dp2;
        float[] fArr4 = this.radii;
        float max2 = Math.max(AndroidUtilities.dp((int) Math.floor(SharedConfig.bubbleRadius / 3.0f)), AndroidUtilities.dp(f3));
        fArr4[7] = max2;
        fArr4[6] = max2;
        drawBackground(canvas, rectF, f4, z, z2);
    }

    public void drawBackground(Canvas canvas, RectF rectF, float f, boolean z, boolean z2) {
        int i;
        if (!z2) {
            this.backgroundPath.rewind();
            this.backgroundPath.addRoundRect(rectF, this.radii, Path.Direction.CW);
            this.backgroundPaint.setColor(this.backgroundColorAnimated.set(this.backgroundColor));
            this.backgroundPaint.setAlpha((int) (r5.getAlpha() * f));
            canvas.drawPath(this.backgroundPath, this.backgroundPaint);
        }
        if (this.emoji == null) {
            return;
        }
        float f2 = this.emojiLoadedT.set(isEmojiLoaded());
        if (f2 <= 0.0f || this.emojiAlpha <= 0.0f) {
            return;
        }
        if (this.iconCoords == null) {
            i = 0;
            this.iconCoords = new IconCoords[]{new IconCoords(4.0f, -6.33f, 1.0f, 1.0f), new IconCoords(30.0f, 3.0f, 0.78f, 0.9f), new IconCoords(46.0f, -17.0f, 0.6f, 0.6f), new IconCoords(69.66f, -0.666f, 0.87f, 0.7f), new IconCoords(98.0f, -12.6f, 1.03f, 0.3f), new IconCoords(51.0f, 24.0f, 1.0f, 0.5f), new IconCoords(6.33f, 20.0f, 0.77f, 0.7f), new IconCoords(-19.0f, 12.0f, 0.8f, 0.6f, true), new IconCoords(-22.0f, 36.0f, 0.7f, 0.5f, true)};
        } else {
            i = 0;
        }
        canvas.save();
        canvas.clipRect(rectF);
        canvas.translate(this.emojiOffsetX, this.emojiOffsetY);
        float max = Math.max(rectF.right - AndroidUtilities.dp(15.0f), rectF.centerX());
        if (z) {
            max -= AndroidUtilities.dp(12.0f);
        }
        float min = Math.min(rectF.centerY(), rectF.top + AndroidUtilities.dp(21.0f));
        this.emoji.setColor(Integer.valueOf(getColor()));
        this.emoji.setAlpha((int) (255.0f * f * (rectF.width() >= ((float) AndroidUtilities.dp(140.0f)) ? 0.5f : 0.3f)));
        while (true) {
            IconCoords[] iconCoordsArr = this.iconCoords;
            if (i >= iconCoordsArr.length) {
                canvas.restore();
                return;
            }
            IconCoords iconCoords = iconCoordsArr[i];
            if (!iconCoords.q || z) {
                this.emoji.setAlpha((int) (iconCoords.a * 76.5f * this.emojiAlpha));
                float dp = max - AndroidUtilities.dp(iconCoords.x);
                float dp2 = AndroidUtilities.dp(iconCoords.y) + min;
                float dp3 = AndroidUtilities.dp(10.0f) * iconCoords.s * f2;
                this.emoji.setBounds((int) (dp - dp3), (int) (dp2 - dp3), (int) (dp + dp3), (int) (dp2 + dp3));
                this.emoji.draw(canvas);
            }
            i++;
        }
    }

    public void drawLine(Canvas canvas, RectF rectF) {
        drawLine(canvas, rectF, 1.0f);
    }

    public void drawLine(Canvas canvas, RectF rectF, float f) {
        boolean z;
        float height;
        int height2;
        float f2;
        canvas.save();
        this.clipPath.rewind();
        int floor = (int) Math.floor(SharedConfig.bubbleRadius / (this.sponsored ? 2.0f : 3.0f));
        RectF rectF2 = this.rectF;
        float f3 = rectF.left;
        rectF2.set(f3, rectF.top, Math.max(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(floor * 2)) + f3, rectF.bottom);
        Path path = this.clipPath;
        RectF rectF3 = this.rectF;
        float f4 = floor;
        float dp = AndroidUtilities.dp(f4);
        float dp2 = AndroidUtilities.dp(f4);
        Path.Direction direction = Path.Direction.CW;
        path.addRoundRect(rectF3, dp, dp2, direction);
        canvas.clipPath(this.clipPath);
        float f5 = rectF.left;
        canvas.clipRect(f5, rectF.top, AndroidUtilities.dp(3.0f) + f5, rectF.bottom);
        this.color1Paint.setColor(Theme.multAlpha(this.color1Animated.set(this.color1), f));
        this.color2Paint.setColor(Theme.multAlpha(this.color2Animated.set(this.color2), f));
        this.color3Paint.setColor(Theme.multAlpha(this.color3Animated.set(this.color3), f));
        float f6 = this.loadingStateT.set(this.loading);
        if (f6 <= 0.0f || this.hasColor2) {
            z = false;
        } else {
            canvas.save();
            int alpha = this.color1Paint.getAlpha();
            this.color1Paint.setAlpha((int) (alpha * 0.3f));
            canvas.drawPaint(this.color1Paint);
            this.color1Paint.setAlpha(alpha);
            incrementLoadingT();
            float pow = ((float) Math.pow((this.loadingT / 240.0f) / 4.0f, 0.8500000238418579d)) * 4.0f;
            this.rectF.set(rectF.left, rectF.top + (rectF.height() * AndroidUtilities.lerp(0.0f, 1.0f - CubicBezierInterpolator.EASE_IN.getInterpolation(MathUtils.clamp(((Math.max(pow, 0.5f) + 1.5f) % 3.5f) * 0.5f, 0.0f, 1.0f)), f6)), rectF.left + AndroidUtilities.dp(6.0f), rectF.top + (rectF.height() * AndroidUtilities.lerp(1.0f, 1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(MathUtils.clamp((((pow + 1.5f) % 3.5f) - 1.5f) * 0.5f, 0.0f, 1.0f)), f6)));
            this.lineClipPath.rewind();
            this.lineClipPath.addRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), direction);
            canvas.clipPath(this.lineClipPath);
            this.parentView.invalidate();
            z = true;
        }
        canvas.drawPaint(this.color1Paint);
        float f7 = this.color2Alpha.set(this.hasColor2);
        if (f7 > 0.0f) {
            canvas.save();
            canvas.translate(rectF.left, rectF.top);
            incrementLoadingT();
            float f8 = this.color3Alpha.set(this.hasColor3);
            if (this.hasColor3) {
                height = rectF.height();
                height2 = (int) rectF.height();
                f2 = 18.99f;
            } else {
                height = rectF.height();
                height2 = (int) rectF.height();
                f2 = 12.66f;
            }
            canvas.translate(0.0f, -(((((this.loadingTranslationT + this.switchStateT.set(this.switchedCount * 425)) + (this.reversedOut ? 100 : 0)) / 1000.0f) * AndroidUtilities.dp(30.0f)) % (height - ReplyMessageLine$$ExternalSyntheticBackport0.m(height2, AndroidUtilities.dp(f2)))));
            checkColorPathes(rectF.height() * 2.0f);
            int alpha2 = this.color2Paint.getAlpha();
            this.color2Paint.setAlpha((int) (alpha2 * f7));
            canvas.drawPath(this.color2Path, this.color2Paint);
            this.color2Paint.setAlpha(alpha2);
            int alpha3 = this.color3Paint.getAlpha();
            this.color3Paint.setAlpha((int) (alpha3 * f8));
            canvas.drawPath(this.color3Path, this.color3Paint);
            this.color3Paint.setAlpha(alpha3);
            canvas.restore();
        }
        if (z) {
            canvas.restore();
        }
        canvas.restore();
    }

    public void drawLoadingBackground(Canvas canvas, RectF rectF, float f, float f2, float f3, float f4) {
        LoadingDrawable loadingDrawable;
        float[] fArr = this.radii;
        float max = Math.max(AndroidUtilities.dp((int) Math.floor(SharedConfig.bubbleRadius / 3.0f)), AndroidUtilities.dp(f));
        fArr[1] = max;
        fArr[0] = max;
        float[] fArr2 = this.radii;
        float dp = AndroidUtilities.dp(f2);
        fArr2[3] = dp;
        fArr2[2] = dp;
        float[] fArr3 = this.radii;
        float dp2 = AndroidUtilities.dp(f3);
        fArr3[5] = dp2;
        fArr3[4] = dp2;
        float[] fArr4 = this.radii;
        float max2 = Math.max(AndroidUtilities.dp((int) Math.floor(SharedConfig.bubbleRadius / 3.0f)), AndroidUtilities.dp(f3));
        fArr4[7] = max2;
        fArr4[6] = max2;
        if (!this.loading && ((loadingDrawable = this.backgroundLoadingDrawable) == null || !loadingDrawable.isDisappearing())) {
            LoadingDrawable loadingDrawable2 = this.backgroundLoadingDrawable;
            if (loadingDrawable2 != null) {
                loadingDrawable2.reset();
                return;
            }
            return;
        }
        if (this.backgroundLoadingDrawable == null) {
            LoadingDrawable loadingDrawable3 = new LoadingDrawable();
            this.backgroundLoadingDrawable = loadingDrawable3;
            loadingDrawable3.setAppearByGradient(true);
            this.backgroundLoadingDrawable.setGradientScale(3.5f);
            this.backgroundLoadingDrawable.setSpeed(0.5f);
        }
        this.backgroundLoadingDrawable.setColors(Theme.multAlpha(this.color1, 0.1f), Theme.multAlpha(this.color1, 0.3f), Theme.multAlpha(this.color1, 0.3f), Theme.multAlpha(this.color1, 1.25f));
        this.backgroundLoadingDrawable.setBounds(rectF);
        this.backgroundLoadingDrawable.setRadii(this.radii);
        this.backgroundLoadingDrawable.strokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.backgroundLoadingDrawable.setAlpha((int) (f4 * 255.0f));
        this.backgroundLoadingDrawable.draw(canvas);
        this.parentView.invalidate();
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getColor() {
        return this.reversedOut ? this.color2 : this.color1;
    }

    public ReplyMessageLine offsetEmoji(float f, float f2) {
        this.emojiOffsetX = f;
        this.emojiOffsetY = f2;
        return this;
    }

    public void resetAnimation() {
        this.color1Animated.set(this.color1, true);
        this.color2Animated.set(this.color2, true);
        this.color2Alpha.set(this.hasColor2, true);
        this.nameColorAnimated.set(this.nameColor, true);
        this.backgroundColorAnimated.set(this.backgroundColor, true);
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.resetAnimation();
        }
    }

    public void setBackgroundColor(int i) {
        this.backgroundColor = i;
    }

    public void setEmojiAlpha(float f) {
        this.emojiAlpha = f;
    }

    public int setFactCheck(Theme.ResourcesProvider resourcesProvider) {
        int i = Theme.key_text_RedBold;
        this.nameColor = Theme.getColor(i, resourcesProvider);
        this.color1 = Theme.getColor(i, resourcesProvider);
        this.hasColor2 = false;
        this.hasColor3 = false;
        this.backgroundColor = Theme.multAlpha(Theme.getColor(i, resourcesProvider), 0.1f);
        if (this.emojiDocumentId != 0 && this.emoji == null) {
            this.emoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this.parentView, false, AndroidUtilities.dp(20.0f), 13);
            View view = this.parentView;
            if (!(view instanceof ChatMessageCell) ? view.isAttachedToWindow() : ((ChatMessageCell) view).isCellAttachedToWindow()) {
                this.emoji.attach();
            }
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
        if (swapAnimatedEmojiDrawable != null && swapAnimatedEmojiDrawable.set(this.emojiDocumentId, true)) {
            this.emojiLoaded = false;
        }
        return this.nameColorAnimated.set(this.nameColor);
    }

    public void setLoading(boolean z) {
        LoadingDrawable loadingDrawable;
        if (!z && this.loading) {
            this.loadingT = 0.0f;
            LoadingDrawable loadingDrawable2 = this.backgroundLoadingDrawable;
            if (loadingDrawable2 != null) {
                loadingDrawable2.disappear();
            }
        } else if (z && !this.loading && (loadingDrawable = this.backgroundLoadingDrawable) != null) {
            loadingDrawable.resetDisappear();
            this.backgroundLoadingDrawable.reset();
        }
        this.loading = z;
    }
}
