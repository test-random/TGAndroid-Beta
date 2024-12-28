package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ReplacementSpan;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class AnimatedEmojiSpan extends ReplacementSpan {
    private static boolean lockPositionChanging;
    private boolean animateChanges;
    public int cacheType;
    public TLRPC.Document document;
    public String documentAbsolutePath;
    public long documentId;
    public String emoji;
    public float extraScale;
    private Paint.FontMetricsInt fontMetrics;
    public boolean fromEmojiKeyboard;
    public boolean full;
    public boolean invert;
    private boolean isAdded;
    private boolean isRemoved;
    float lastDrawnCx;
    float lastDrawnCy;
    protected int measuredSize;
    private ValueAnimator moveAnimator;
    boolean positionChanged;
    private boolean recordPositions;
    private Runnable removedAction;
    private float scale;
    private ValueAnimator scaleAnimator;
    public float size;
    boolean spanDrawn;
    public boolean standard;
    public boolean top;

    public static class AnimatedEmojiHolder implements InvalidateHolder {
        public float alpha;
        private ImageReceiver.BackgroundThreadDrawHolder[] backgroundDrawHolder = new ImageReceiver.BackgroundThreadDrawHolder[2];
        public AnimatedEmojiDrawable drawable;
        public android.graphics.Rect drawableBounds;
        public float drawingYOffset;
        public boolean insideSpoiler;
        private final boolean invalidateInParent;
        public Layout layout;
        public boolean skipDraw;
        public AnimatedEmojiSpan span;
        public SpansChunk spansChunk;
        public Drawable thumbDrawable;
        private final View view;

        public AnimatedEmojiHolder(View view, boolean z) {
            this.view = view;
            this.invalidateInParent = z;
        }

        public void draw(Canvas canvas, long j, float f, float f2, float f3, ColorFilter colorFilter) {
            if (!(f == 0.0f && f2 == 0.0f) && outOfBounds(f, f2)) {
                this.skipDraw = true;
                return;
            }
            this.skipDraw = false;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.drawable;
            if (animatedEmojiDrawable == null) {
                if (this.thumbDrawable != null) {
                    float extraScale = this.span.getExtraScale();
                    this.thumbDrawable.setAlpha((int) (f3 * 255.0f * this.alpha));
                    this.thumbDrawable.setBounds(this.drawableBounds);
                    if (extraScale == 1.0f && !this.span.invert) {
                        this.thumbDrawable.draw(canvas);
                        return;
                    }
                    canvas.save();
                    canvas.scale((this.span.invert ? -1 : 1) * extraScale, extraScale, this.drawableBounds.centerX(), this.drawableBounds.centerY());
                    this.thumbDrawable.draw(canvas);
                    canvas.restore();
                    return;
                }
                return;
            }
            if (animatedEmojiDrawable.getImageReceiver() != null) {
                AnimatedEmojiDrawable animatedEmojiDrawable2 = this.drawable;
                if (colorFilter == null) {
                    colorFilter = Theme.chat_animatedEmojiTextColorFilter;
                }
                animatedEmojiDrawable2.setColorFilter(colorFilter);
                this.drawable.setTime(j);
                float extraScale2 = this.span.getExtraScale();
                if (extraScale2 != 1.0f || this.span.invert) {
                    canvas.save();
                    canvas.scale((this.span.invert ? -1 : 1) * extraScale2, extraScale2, this.drawableBounds.centerX(), this.drawableBounds.centerY());
                    this.drawable.draw(canvas, this.drawableBounds, f3 * this.alpha);
                    canvas.restore();
                } else {
                    this.drawable.draw(canvas, this.drawableBounds, f3 * this.alpha);
                }
                if (this.span.isAnimating()) {
                    invalidate();
                }
            }
        }

        @Override
        public void invalidate() {
            View view = this.view;
            if (view != null) {
                ((!this.invalidateInParent || view.getParent() == null) ? this.view : (View) this.view.getParent()).invalidate();
            }
        }

        public boolean outOfBounds(float f, float f2) {
            android.graphics.Rect rect = this.drawableBounds;
            return ((float) rect.bottom) < f || ((float) rect.top) > f2;
        }

        public void prepareForBackgroundDraw(long j, int i) {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.drawable;
            if (animatedEmojiDrawable == null) {
                return;
            }
            ImageReceiver imageReceiver = animatedEmojiDrawable.getImageReceiver();
            this.drawable.update(j);
            this.drawable.setBounds(this.drawableBounds);
            if (imageReceiver != null) {
                AnimatedEmojiSpan animatedEmojiSpan = this.span;
                if (animatedEmojiSpan != null && animatedEmojiSpan.document == null && this.drawable.getDocument() != null) {
                    this.span.document = this.drawable.getDocument();
                }
                imageReceiver.setAlpha(this.alpha);
                imageReceiver.setImageCoords(this.drawableBounds);
                ImageReceiver.BackgroundThreadDrawHolder[] backgroundThreadDrawHolderArr = this.backgroundDrawHolder;
                backgroundThreadDrawHolderArr[i] = imageReceiver.setDrawInBackgroundThread(backgroundThreadDrawHolderArr[i], i);
                ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder = this.backgroundDrawHolder[i];
                backgroundThreadDrawHolder.overrideAlpha = this.alpha;
                backgroundThreadDrawHolder.setBounds(this.drawableBounds);
                this.backgroundDrawHolder[i].time = j;
            }
        }

        public void releaseDrawInBackground(int i) {
            ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder = this.backgroundDrawHolder[i];
            if (backgroundThreadDrawHolder != null) {
                backgroundThreadDrawHolder.release();
            }
        }
    }

    public static class EmojiGroupedSpans {
        public ArrayList holders = new ArrayList();
        HashMap groupedByLayout = new HashMap();
        ArrayList backgroundDrawingArray = new ArrayList();

        public void add(Layout layout, AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.add(animatedEmojiHolder);
            SpansChunk spansChunk = (SpansChunk) this.groupedByLayout.get(layout);
            if (spansChunk == null) {
                spansChunk = new SpansChunk(animatedEmojiHolder.view, layout, animatedEmojiHolder.invalidateInParent);
                this.groupedByLayout.put(layout, spansChunk);
                this.backgroundDrawingArray.add(spansChunk);
            }
            spansChunk.add(animatedEmojiHolder);
            AnimatedEmojiDrawable animatedEmojiDrawable = animatedEmojiHolder.drawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(animatedEmojiHolder);
            }
        }

        public void clearPositions() {
            for (int i = 0; i < this.holders.size(); i++) {
                ((AnimatedEmojiHolder) this.holders.get(i)).span.spanDrawn = false;
            }
        }

        public void recordPositions(boolean z) {
            for (int i = 0; i < this.holders.size(); i++) {
                ((AnimatedEmojiHolder) this.holders.get(i)).span.recordPositions = z;
            }
        }

        public void release() {
            while (this.holders.size() > 0) {
                remove(0);
            }
        }

        public void remove(int i) {
            AnimatedEmojiHolder animatedEmojiHolder = (AnimatedEmojiHolder) this.holders.remove(i);
            SpansChunk spansChunk = (SpansChunk) this.groupedByLayout.get(animatedEmojiHolder.layout);
            if (spansChunk == null) {
                throw new RuntimeException("!!!");
            }
            spansChunk.remove(animatedEmojiHolder);
            if (spansChunk.holders.isEmpty()) {
                this.groupedByLayout.remove(animatedEmojiHolder.layout);
                this.backgroundDrawingArray.remove(spansChunk);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = animatedEmojiHolder.drawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.removeView(animatedEmojiHolder);
            }
        }

        public void replaceLayout(Layout layout, Layout layout2) {
            SpansChunk spansChunk;
            if (layout2 == null || (spansChunk = (SpansChunk) this.groupedByLayout.remove(layout2)) == null) {
                return;
            }
            spansChunk.layout = layout;
            for (int i = 0; i < spansChunk.holders.size(); i++) {
                ((AnimatedEmojiHolder) spansChunk.holders.get(i)).layout = layout;
            }
            this.groupedByLayout.put(layout, spansChunk);
        }
    }

    public interface InvalidateHolder {
        void invalidate();
    }

    public static class SpansChunk {
        private boolean allowBackgroundRendering;
        DrawingInBackgroundThreadDrawable backgroundThreadDrawable;
        ArrayList holders = new ArrayList();
        Layout layout;
        final View view;

        public SpansChunk(View view, Layout layout, boolean z) {
            this.layout = layout;
            this.view = view;
            this.allowBackgroundRendering = z;
        }

        private void checkBackgroundRendering() {
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable;
            if (this.allowBackgroundRendering && this.holders.size() >= 10 && this.backgroundThreadDrawable == null && LiteMode.isEnabled(16388)) {
                DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable2 = new DrawingInBackgroundThreadDrawable() {
                    private final ArrayList backgroundHolders = new ArrayList();

                    @Override
                    public void drawInBackground(Canvas canvas) {
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            AnimatedEmojiHolder animatedEmojiHolder = (AnimatedEmojiHolder) this.backgroundHolders.get(i);
                            if (animatedEmojiHolder != null && animatedEmojiHolder.drawable != null && animatedEmojiHolder.backgroundDrawHolder[this.threadIndex] != null) {
                                animatedEmojiHolder.drawable.draw(canvas, animatedEmojiHolder.backgroundDrawHolder[this.threadIndex], true);
                            }
                        }
                    }

                    @Override
                    public void drawInUiThread(Canvas canvas, float f) {
                        long currentTimeMillis = System.currentTimeMillis();
                        for (int i = 0; i < SpansChunk.this.holders.size(); i++) {
                            AnimatedEmojiHolder animatedEmojiHolder = (AnimatedEmojiHolder) SpansChunk.this.holders.get(i);
                            if (animatedEmojiHolder.span.spanDrawn) {
                                animatedEmojiHolder.draw(canvas, currentTimeMillis, 0.0f, 0.0f, f, null);
                            }
                        }
                    }

                    @Override
                    public void onFrameReady() {
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            if (this.backgroundHolders.get(i) != null) {
                                ((AnimatedEmojiHolder) this.backgroundHolders.get(i)).releaseDrawInBackground(this.threadIndex);
                            }
                        }
                        this.backgroundHolders.clear();
                        View view = SpansChunk.this.view;
                        if (view == null || view.getParent() == null) {
                            return;
                        }
                        ((View) SpansChunk.this.view.getParent()).invalidate();
                    }

                    @Override
                    public void onPaused() {
                        super.onPaused();
                    }

                    @Override
                    public void onResume() {
                        View view = SpansChunk.this.view;
                        if (view == null || view.getParent() == null) {
                            return;
                        }
                        ((View) SpansChunk.this.view.getParent()).invalidate();
                    }

                    @Override
                    public void prepareDraw(long j) {
                        this.backgroundHolders.clear();
                        this.backgroundHolders.addAll(SpansChunk.this.holders);
                        int i = 0;
                        while (i < this.backgroundHolders.size()) {
                            AnimatedEmojiHolder animatedEmojiHolder = (AnimatedEmojiHolder) this.backgroundHolders.get(i);
                            if (animatedEmojiHolder.span.spanDrawn) {
                                animatedEmojiHolder.prepareForBackgroundDraw(j, this.threadIndex);
                            } else {
                                this.backgroundHolders.remove(i);
                                i--;
                            }
                            i++;
                        }
                    }
                };
                this.backgroundThreadDrawable = drawingInBackgroundThreadDrawable2;
                drawingInBackgroundThreadDrawable2.padding = AndroidUtilities.dp(3.0f);
                this.backgroundThreadDrawable.onAttachToWindow();
                return;
            }
            if (this.holders.size() >= 10 || (drawingInBackgroundThreadDrawable = this.backgroundThreadDrawable) == null) {
                return;
            }
            drawingInBackgroundThreadDrawable.onDetachFromWindow();
            this.backgroundThreadDrawable = null;
        }

        public void add(AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.add(animatedEmojiHolder);
            animatedEmojiHolder.spansChunk = this;
            checkBackgroundRendering();
        }

        public void draw(Canvas canvas, List list, long j, float f, float f2, float f3, float f4, ColorFilter colorFilter) {
            for (int i = 0; i < this.holders.size(); i++) {
                AnimatedEmojiHolder animatedEmojiHolder = (AnimatedEmojiHolder) this.holders.get(i);
                if (animatedEmojiHolder != null) {
                    AnimatedEmojiDrawable animatedEmojiDrawable = animatedEmojiHolder.drawable;
                    if (animatedEmojiDrawable != null) {
                        animatedEmojiDrawable.setColorFilter(colorFilter);
                    }
                    AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiHolder.span;
                    if (animatedEmojiSpan.spanDrawn) {
                        float f5 = animatedEmojiSpan.measuredSize / 2.0f;
                        float f6 = animatedEmojiSpan.lastDrawnCx;
                        float f7 = animatedEmojiSpan.lastDrawnCy;
                        animatedEmojiHolder.drawableBounds.set((int) (f6 - f5), (int) (f7 - f5), (int) (f6 + f5), (int) (f7 + f5));
                        float max = (list == null || list.isEmpty() || !animatedEmojiHolder.insideSpoiler) ? 1.0f : Math.max(0.0f, ((SpoilerEffect) list.get(0)).getRippleProgress());
                        animatedEmojiHolder.drawingYOffset = f3;
                        animatedEmojiHolder.alpha = max;
                        if (this.backgroundThreadDrawable == null) {
                            animatedEmojiHolder.draw(canvas, j, f, f2, f4, colorFilter);
                        }
                    }
                }
            }
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = this.backgroundThreadDrawable;
            if (drawingInBackgroundThreadDrawable != null) {
                drawingInBackgroundThreadDrawable.draw(canvas, j, this.layout.getWidth(), this.layout.getHeight() + AndroidUtilities.dp(2.0f), f4);
            }
        }

        public void remove(AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.remove(animatedEmojiHolder);
            animatedEmojiHolder.spansChunk = null;
            checkBackgroundRendering();
        }
    }

    public static class TextViewEmojis extends TextView {
        private int cacheType;
        private ColorFilter emojiColorFilter;
        EmojiGroupedSpans stack;

        public TextViewEmojis(Context context) {
            super(context);
            this.cacheType = 0;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.stack = AnimatedEmojiSpan.update(this.cacheType, this, this.stack, getLayout());
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            AnimatedEmojiSpan.release(this, this.stack);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float paddingTop = ((getGravity() & 16) == 0 || getLayout() == null) ? 0.0f : getPaddingTop() + ((((getHeight() - getPaddingTop()) - getPaddingBottom()) - getLayout().getHeight()) / 2.0f);
            float paddingRight = LocaleController.isRTL ? getPaddingRight() : getPaddingLeft();
            if (paddingTop != 0.0f || paddingRight != 0.0f) {
                canvas.save();
                canvas.translate(paddingRight, paddingTop);
            }
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, getLayout(), this.stack, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f, this.emojiColorFilter);
            if (paddingTop == 0.0f && paddingRight == 0.0f) {
                return;
            }
            canvas.restore();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.stack = AnimatedEmojiSpan.update(this.cacheType, this, this.stack, getLayout());
        }

        public void setCacheType(int i) {
            if (this.cacheType == i) {
                return;
            }
            this.cacheType = i;
            this.stack = AnimatedEmojiSpan.update(i, this, this.stack, getLayout());
        }

        public void setEmojiColor(int i) {
            this.emojiColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
            super.setText(charSequence, bufferType);
            this.stack = AnimatedEmojiSpan.update(this.cacheType, this, this.stack, getLayout());
        }
    }

    public AnimatedEmojiSpan(long j, float f, Paint.FontMetricsInt fontMetricsInt) {
        this.extraScale = 1.0f;
        this.full = false;
        this.top = false;
        this.invert = false;
        this.size = AndroidUtilities.dp(20.0f);
        this.cacheType = -1;
        this.recordPositions = true;
        this.documentId = j;
        this.scale = f;
        this.fontMetrics = fontMetricsInt;
        if (fontMetricsInt != null) {
            float abs = Math.abs(fontMetricsInt.descent) + Math.abs(fontMetricsInt.ascent);
            this.size = abs;
            if (abs == 0.0f) {
                this.size = AndroidUtilities.dp(20.0f);
            }
        }
    }

    public AnimatedEmojiSpan(long j, Paint.FontMetricsInt fontMetricsInt) {
        this(j, 1.2f, fontMetricsInt);
    }

    public AnimatedEmojiSpan(TLRPC.Document document, float f, Paint.FontMetricsInt fontMetricsInt) {
        this(document.id, f, fontMetricsInt);
        this.document = document;
    }

    public AnimatedEmojiSpan(TLRPC.Document document, Paint.FontMetricsInt fontMetricsInt) {
        this(document.id, 1.2f, fontMetricsInt);
        this.document = document;
    }

    private boolean animateChanges(final float f, final float f2) {
        if (this.moveAnimator != null) {
            return true;
        }
        if (!this.animateChanges) {
            return false;
        }
        this.animateChanges = false;
        final float f3 = this.lastDrawnCx;
        final float f4 = this.lastDrawnCy;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.moveAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AnimatedEmojiSpan.this.lambda$animateChanges$2(f4, f2, f3, f, valueAnimator);
            }
        });
        this.moveAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatedEmojiSpan.this.moveAnimator = null;
            }
        });
        this.moveAnimator.setDuration(140L);
        this.moveAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.moveAnimator.start();
        return true;
    }

    public static void applyFontMetricsForString(CharSequence charSequence, Paint paint) {
        if (charSequence instanceof Spannable) {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), AnimatedEmojiSpan.class);
            if (animatedEmojiSpanArr != null) {
                for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                    animatedEmojiSpan.applyFontMetrics(paint.getFontMetricsInt());
                }
            }
        }
    }

    public static AnimatedEmojiSpan cloneSpan(AnimatedEmojiSpan animatedEmojiSpan, Paint.FontMetricsInt fontMetricsInt) {
        AnimatedEmojiSpan animatedEmojiSpan2;
        TLRPC.Document document = animatedEmojiSpan.document;
        if (document != null) {
            animatedEmojiSpan2 = new AnimatedEmojiSpan(document, fontMetricsInt != null ? fontMetricsInt : animatedEmojiSpan.fontMetrics);
        } else {
            animatedEmojiSpan2 = new AnimatedEmojiSpan(animatedEmojiSpan.documentId, animatedEmojiSpan.scale, fontMetricsInt != null ? fontMetricsInt : animatedEmojiSpan.fontMetrics);
        }
        if (fontMetricsInt != null) {
            animatedEmojiSpan2.size = animatedEmojiSpan.size;
        }
        animatedEmojiSpan2.fromEmojiKeyboard = animatedEmojiSpan.fromEmojiKeyboard;
        animatedEmojiSpan2.isAdded = animatedEmojiSpan.isAdded;
        animatedEmojiSpan2.isRemoved = animatedEmojiSpan.isRemoved;
        return animatedEmojiSpan2;
    }

    public static CharSequence cloneSpans(CharSequence charSequence) {
        return cloneSpans(charSequence, -1, null);
    }

    public static CharSequence cloneSpans(CharSequence charSequence, int i) {
        return cloneSpans(charSequence, i, null);
    }

    public static CharSequence cloneSpans(CharSequence charSequence, int i, Paint.FontMetricsInt fontMetricsInt) {
        return cloneSpans(charSequence, i, fontMetricsInt, 1.0f);
    }

    public static CharSequence cloneSpans(CharSequence charSequence, int i, Paint.FontMetricsInt fontMetricsInt, float f) {
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        Spanned spanned = (Spanned) charSequence;
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) spanned.getSpans(0, spanned.length(), CharacterStyle.class);
        if (characterStyleArr != null && characterStyleArr.length > 0) {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(0, spanned.length(), AnimatedEmojiSpan.class);
            if (animatedEmojiSpanArr != null && animatedEmojiSpanArr.length <= 0) {
                return charSequence;
            }
            charSequence = new SpannableString(spanned);
            for (int i2 = 0; i2 < characterStyleArr.length; i2++) {
                CharacterStyle characterStyle = characterStyleArr[i2];
                if (characterStyle != null && (characterStyle instanceof AnimatedEmojiSpan)) {
                    int spanStart = spanned.getSpanStart(characterStyle);
                    int spanEnd = spanned.getSpanEnd(characterStyleArr[i2]);
                    AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) characterStyleArr[i2];
                    charSequence.removeSpan(animatedEmojiSpan);
                    AnimatedEmojiSpan cloneSpan = cloneSpan(animatedEmojiSpan, fontMetricsInt);
                    if (i != -1) {
                        cloneSpan.cacheType = i;
                    }
                    cloneSpan.scale = animatedEmojiSpan.scale * f;
                    charSequence.setSpan(cloneSpan, spanStart, spanEnd, 33);
                }
            }
        }
        return charSequence;
    }

    public static void drawAnimatedEmojis(Canvas canvas, Layout layout, EmojiGroupedSpans emojiGroupedSpans, float f, List<SpoilerEffect> list, float f2, float f3, float f4, float f5) {
        drawAnimatedEmojis(canvas, layout, emojiGroupedSpans, f, list, f2, f3, f4, f5, null);
    }

    public static void drawAnimatedEmojis(Canvas canvas, Layout layout, EmojiGroupedSpans emojiGroupedSpans, float f, List<SpoilerEffect> list, float f2, float f3, float f4, float f5, ColorFilter colorFilter) {
        boolean z;
        if (canvas == null || layout == null || emojiGroupedSpans == null) {
            return;
        }
        int i = 0;
        if (Emoji.emojiDrawingYOffset == 0.0f && f == 0.0f) {
            z = false;
        } else {
            canvas.save();
            canvas.translate(0.0f, Emoji.emojiDrawingYOffset + AndroidUtilities.dp(20.0f * f));
            z = true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (i >= emojiGroupedSpans.backgroundDrawingArray.size()) {
                break;
            }
            SpansChunk spansChunk = (SpansChunk) emojiGroupedSpans.backgroundDrawingArray.get(i);
            if (spansChunk.layout == layout) {
                spansChunk.draw(canvas, list, currentTimeMillis, f2, f3, f4, f5, colorFilter);
                break;
            }
            i++;
        }
        if (z) {
            canvas.restore();
        }
    }

    public boolean isAnimating() {
        return (this.moveAnimator == null && this.scaleAnimator == null) ? false : true;
    }

    private static boolean isInsideSpoiler(Layout layout, int i, int i2) {
        if (layout != null && (layout.getText() instanceof Spanned)) {
            TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) ((Spanned) layout.getText()).getSpans(Math.max(0, i), Math.min(layout.getText().length() - 1, i2), TextStyleSpan.class);
            for (int i3 = 0; textStyleSpanArr != null && i3 < textStyleSpanArr.length; i3++) {
                TextStyleSpan textStyleSpan = textStyleSpanArr[i3];
                if (textStyleSpan != null && textStyleSpan.isSpoiler()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void lambda$animateChanges$2(float f, float f2, float f3, float f4, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.lastDrawnCy = AndroidUtilities.lerp(f, f2, floatValue);
        this.lastDrawnCx = AndroidUtilities.lerp(f3, f4, floatValue);
    }

    public void lambda$getExtraScale$0(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.extraScale = floatValue;
        this.scale = AndroidUtilities.lerp(0.2f, 1.0f, floatValue);
        lockPositionChanging = false;
    }

    public void lambda$getExtraScale$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.extraScale = floatValue;
        this.scale = AndroidUtilities.lerp(0.0f, 1.0f, floatValue);
    }

    public static CharSequence onlyEmojiSpans(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        for (CharacterStyle characterStyle : (CharacterStyle[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), CharacterStyle.class)) {
            if (!(characterStyle instanceof AnimatedEmojiSpan) && !(characterStyle instanceof Emoji.EmojiSpan)) {
                spannableStringBuilder.removeSpan(characterStyle);
            }
        }
        return spannableStringBuilder;
    }

    public static void release(View view, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        if (longSparseArray == null) {
            return;
        }
        for (int i = 0; i < longSparseArray.size(); i++) {
            AnimatedEmojiDrawable valueAt = longSparseArray.valueAt(i);
            if (valueAt != null) {
                valueAt.removeView(view);
            }
        }
        longSparseArray.clear();
    }

    public static void release(View view, EmojiGroupedSpans emojiGroupedSpans) {
        if (emojiGroupedSpans == null) {
            return;
        }
        emojiGroupedSpans.release();
    }

    public static LongSparseArray<AnimatedEmojiDrawable> update(int i, View view, ArrayList<AnimatedEmojiSpan> arrayList, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        int i2;
        int i3;
        if (arrayList == null) {
            return longSparseArray;
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
        }
        int i4 = 0;
        while (i4 < longSparseArray.size()) {
            long keyAt = longSparseArray.keyAt(i4);
            AnimatedEmojiDrawable animatedEmojiDrawable = longSparseArray.get(keyAt);
            if (animatedEmojiDrawable != null) {
                while (i3 < arrayList.size()) {
                    i3 = (arrayList.get(i3) == null || arrayList.get(i3).getDocumentId() != keyAt) ? i3 + 1 : 0;
                }
                animatedEmojiDrawable.addView(view);
            }
            longSparseArray.remove(keyAt);
            i4--;
            i4++;
        }
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            AnimatedEmojiSpan animatedEmojiSpan = arrayList.get(i5);
            if (animatedEmojiSpan != null && longSparseArray.get(animatedEmojiSpan.getDocumentId()) == null) {
                if (animatedEmojiSpan.standard) {
                    i2 = 8;
                } else {
                    i2 = animatedEmojiSpan.cacheType;
                    if (i2 < 0) {
                        i2 = i;
                    }
                }
                AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, animatedEmojiSpan.documentId);
                make.addView(view);
                longSparseArray.put(animatedEmojiSpan.getDocumentId(), make);
            }
        }
        return longSparseArray;
    }

    public static LongSparseArray<AnimatedEmojiDrawable> update(int i, View view, AnimatedEmojiSpan[] animatedEmojiSpanArr, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        int i2;
        int i3;
        if (animatedEmojiSpanArr == null) {
            return longSparseArray;
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
        }
        int i4 = 0;
        while (i4 < longSparseArray.size()) {
            long keyAt = longSparseArray.keyAt(i4);
            AnimatedEmojiDrawable animatedEmojiDrawable = longSparseArray.get(keyAt);
            if (animatedEmojiDrawable != null) {
                while (i3 < animatedEmojiSpanArr.length) {
                    AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiSpanArr[i3];
                    i3 = (animatedEmojiSpan == null || animatedEmojiSpan.getDocumentId() != keyAt) ? i3 + 1 : 0;
                }
                animatedEmojiDrawable.removeView(view);
            }
            longSparseArray.remove(keyAt);
            i4--;
            i4++;
        }
        for (AnimatedEmojiSpan animatedEmojiSpan2 : animatedEmojiSpanArr) {
            if (animatedEmojiSpan2 != null && longSparseArray.get(animatedEmojiSpan2.getDocumentId()) == null) {
                if (animatedEmojiSpan2.standard) {
                    i2 = 8;
                } else {
                    i2 = animatedEmojiSpan2.cacheType;
                    if (i2 < 0) {
                        i2 = i;
                    }
                }
                TLRPC.Document document = animatedEmojiSpan2.document;
                AnimatedEmojiDrawable make = document != null ? AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, document) : AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, animatedEmojiSpan2.documentId);
                make.addView(view);
                longSparseArray.put(animatedEmojiSpan2.getDocumentId(), make);
            }
        }
        return longSparseArray;
    }

    public static LongSparseArray<AnimatedEmojiDrawable> update(View view, ArrayList<AnimatedEmojiSpan> arrayList, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        return update(0, view, arrayList, longSparseArray);
    }

    public static LongSparseArray<AnimatedEmojiDrawable> update(View view, AnimatedEmojiSpan[] animatedEmojiSpanArr, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        return update(0, view, animatedEmojiSpanArr, longSparseArray);
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        return update(i, view, emojiGroupedSpans, arrayList, false);
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList, boolean z) {
        return update(i, view, false, emojiGroupedSpans, arrayList, z);
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, Layout... layoutArr) {
        return update(i, view, false, emojiGroupedSpans, layoutArr);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        return update(i, view, z, emojiGroupedSpans, arrayList, false);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList, boolean z2) {
        Layout[] layoutArr = new Layout[arrayList == null ? 0 : arrayList.size()];
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                layoutArr[i2] = arrayList.get(i2).textLayout;
            }
        }
        return update(i, view, z, emojiGroupedSpans, z2, layoutArr);
    }

    public static org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans update(int r17, android.view.View r18, boolean r19, org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans r20, boolean r21, android.text.Layout... r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiSpan.update(int, android.view.View, boolean, org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans, boolean, android.text.Layout[]):org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans");
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, Layout... layoutArr) {
        return update(i, view, z, emojiGroupedSpans, false, layoutArr);
    }

    public void applyFontMetrics(Paint.FontMetricsInt fontMetricsInt) {
        this.fontMetrics = fontMetricsInt;
    }

    public void applyFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i) {
        this.fontMetrics = fontMetricsInt;
        this.cacheType = i;
    }

    @Override
    public void draw(android.graphics.Canvas r1, java.lang.CharSequence r2, int r3, int r4, float r5, int r6, int r7, int r8, android.graphics.Paint r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiSpan.draw(android.graphics.Canvas, java.lang.CharSequence, int, int, float, int, int, int, android.graphics.Paint):void");
    }

    public long getDocumentId() {
        TLRPC.Document document = this.document;
        return document != null ? document.id : this.documentId;
    }

    public float getExtraScale() {
        if (!this.isAdded) {
            if (this.isRemoved) {
                this.isRemoved = false;
                this.extraScale = 1.0f;
                ValueAnimator valueAnimator = this.scaleAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.scaleAnimator.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.extraScale, 0.0f);
                this.scaleAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        AnimatedEmojiSpan.this.lambda$getExtraScale$1(valueAnimator2);
                    }
                });
                this.scaleAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        AnimatedEmojiSpan.this.scaleAnimator = null;
                        if (AnimatedEmojiSpan.this.removedAction != null) {
                            AnimatedEmojiSpan.this.removedAction.run();
                            AnimatedEmojiSpan.this.removedAction = null;
                        }
                    }
                });
                this.scaleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.scaleAnimator.setDuration(130L);
            }
            return this.extraScale;
        }
        lockPositionChanging = true;
        this.isAdded = false;
        this.extraScale = 0.0f;
        ValueAnimator valueAnimator2 = this.scaleAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.removeAllListeners();
            this.scaleAnimator.cancel();
        }
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.extraScale, 1.0f);
        this.scaleAnimator = ofFloat2;
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                AnimatedEmojiSpan.this.lambda$getExtraScale$0(valueAnimator3);
            }
        });
        this.scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatedEmojiSpan.this.scaleAnimator = null;
                boolean unused = AnimatedEmojiSpan.lockPositionChanging = false;
            }
        });
        this.scaleAnimator.setDuration(130L);
        this.scaleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.scaleAnimator.start();
        return this.extraScale;
    }

    @Override
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        int ceil;
        if (fontMetricsInt == null && this.top) {
            fontMetricsInt = paint.getFontMetricsInt();
        }
        int i3 = fontMetricsInt == null ? 0 : fontMetricsInt.ascent;
        int i4 = fontMetricsInt == null ? 0 : fontMetricsInt.descent;
        Paint.FontMetricsInt fontMetricsInt2 = this.fontMetrics;
        if (fontMetricsInt2 == null) {
            int i5 = (int) this.size;
            int dp = AndroidUtilities.dp(8.0f);
            int dp2 = AndroidUtilities.dp(10.0f);
            if (fontMetricsInt != null) {
                float f = this.scale;
                int i6 = (int) (((-dp2) - dp) * f);
                fontMetricsInt.top = i6;
                int i7 = (int) ((dp2 - dp) * f);
                fontMetricsInt.bottom = i7;
                fontMetricsInt.ascent = i6;
                fontMetricsInt.descent = i7;
                fontMetricsInt.leading = 0;
            }
            this.measuredSize = (int) (i5 * this.scale);
        } else {
            this.measuredSize = (int) (this.size * this.scale);
            if (fontMetricsInt != null) {
                if (this.full) {
                    float abs = Math.abs(fontMetricsInt2.bottom) + Math.abs(this.fontMetrics.top);
                    fontMetricsInt.ascent = (int) Math.ceil((this.fontMetrics.top / abs) * this.measuredSize);
                    fontMetricsInt.descent = (int) Math.ceil((this.fontMetrics.bottom / abs) * this.measuredSize);
                    fontMetricsInt.top = (int) Math.ceil((this.fontMetrics.top / abs) * this.measuredSize);
                    ceil = (int) Math.ceil((this.fontMetrics.bottom / abs) * this.measuredSize);
                } else {
                    fontMetricsInt.ascent = fontMetricsInt2.ascent;
                    fontMetricsInt.descent = fontMetricsInt2.descent;
                    fontMetricsInt.top = fontMetricsInt2.top;
                    ceil = fontMetricsInt2.bottom;
                }
                fontMetricsInt.bottom = ceil;
            }
        }
        if (fontMetricsInt != null && this.top) {
            int i8 = fontMetricsInt.ascent;
            int i9 = fontMetricsInt.descent;
            int i10 = ((i3 - i8) + (i4 - i9)) / 2;
            fontMetricsInt.ascent = i8 + i10;
            fontMetricsInt.descent = i9 - i10;
        }
        return Math.max(0, this.measuredSize - 1);
    }

    public void replaceFontMetrics(Paint.FontMetricsInt fontMetricsInt) {
        this.fontMetrics = fontMetricsInt;
        if (fontMetricsInt != null) {
            float abs = Math.abs(fontMetricsInt.descent) + Math.abs(this.fontMetrics.ascent);
            this.size = abs;
            if (abs == 0.0f) {
                this.size = AndroidUtilities.dp(20.0f);
            }
        }
    }

    public void replaceFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i, int i2) {
        this.fontMetrics = fontMetricsInt;
        this.size = i;
        this.cacheType = i2;
    }

    public void setAdded() {
        this.isAdded = true;
        this.extraScale = 0.0f;
    }

    public void setAnimateChanges() {
        this.animateChanges = true;
    }

    public void setRemoved(Runnable runnable) {
        this.removedAction = runnable;
        this.isRemoved = true;
        this.extraScale = 1.0f;
    }
}
