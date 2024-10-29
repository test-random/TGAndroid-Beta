package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils;
import androidx.emoji2.text.UnprecomputeTextOnModificationSpannable$CharSequenceHelper_API24$$ExternalSyntheticAPIConversion0;
import androidx.emoji2.text.UnprecomputeTextOnModificationSpannable$CharSequenceHelper_API24$$ExternalSyntheticAPIConversion1;
import j$.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;

public class AnimatedTextView extends View {
    public boolean adaptWidth;
    private final AnimatedTextDrawable drawable;
    private boolean first;
    private int lastMaxWidth;
    private int maxWidth;
    private boolean toSetMoveDown;
    private CharSequence toSetText;

    public static class AnimatedTextDrawable extends Drawable {
        private boolean allowCancel;
        private int alpha;
        private long animateDelay;
        private long animateDuration;
        private TimeInterpolator animateInterpolator;
        private float animateWave;
        private ValueAnimator animator;
        private final android.graphics.Rect bounds;
        public boolean centerY;
        private ValueAnimator colorAnimator;
        private float currentHeight;
        private Part[] currentParts;
        private CharSequence currentText;
        private float currentWidth;
        private boolean ellipsizeByGradient;
        private LinearGradient ellipsizeGradient;
        private Matrix ellipsizeGradientMatrix;
        private Paint ellipsizePaint;
        private int emojiCacheType;
        private int emojiColor;
        private ColorFilter emojiColorFilter;
        private boolean enforceByLetter;
        private int gravity;
        public boolean ignoreRTL;
        private boolean includeFontPadding;
        private boolean isRTL;
        private float moveAmplitude;
        private boolean moveDown;
        private float oldHeight;
        private Part[] oldParts;
        private CharSequence oldText;
        private float oldWidth;
        private Runnable onAnimationFinishListener;
        private int overrideFullWidth;
        private boolean preserveIndex;
        private float rightPadding;
        private float scaleAmplitude;
        private int shadowColor;
        private float shadowDx;
        private float shadowDy;
        private float shadowRadius;
        private boolean shadowed;
        private boolean splitByWords;
        private boolean startFromEnd;
        private float t;
        private final TextPaint textPaint;
        private CharSequence toSetText;
        private boolean toSetTextMoveDown;
        public boolean updateAll;
        private Runnable widthUpdatedListener;

        public class Part {
            AnimatedEmojiSpan.EmojiGroupedSpans emoji;
            StaticLayout layout;
            float left;
            float offset;
            int toOppositeIndex;
            float width;

            public Part(StaticLayout staticLayout, float f, int i) {
                this.layout = staticLayout;
                this.toOppositeIndex = i;
                layout(f);
                if (AnimatedTextDrawable.this.getCallback() instanceof View) {
                    this.emoji = AnimatedEmojiSpan.update(AnimatedTextDrawable.this.emojiCacheType, (View) AnimatedTextDrawable.this.getCallback(), this.emoji, staticLayout);
                }
            }

            public void detach() {
                if (AnimatedTextDrawable.this.getCallback() instanceof View) {
                    AnimatedEmojiSpan.release((View) AnimatedTextDrawable.this.getCallback(), this.emoji);
                }
            }

            public void draw(Canvas canvas, float f) {
                this.layout.draw(canvas);
                AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.layout, this.emoji, 0.0f, null, 0.0f, 0.0f, 0.0f, f, AnimatedTextDrawable.this.emojiColorFilter);
            }

            public void layout(float f) {
                this.offset = f;
                StaticLayout staticLayout = this.layout;
                float f2 = 0.0f;
                this.left = (staticLayout == null || staticLayout.getLineCount() <= 0) ? 0.0f : this.layout.getLineLeft(0);
                StaticLayout staticLayout2 = this.layout;
                if (staticLayout2 != null && staticLayout2.getLineCount() > 0) {
                    f2 = this.layout.getLineWidth(0);
                }
                this.width = f2;
            }
        }

        public interface RegionCallback {
            void run(CharSequence charSequence, int i, int i2);
        }

        public static class WordSequence implements CharSequence {
            private final int length;
            private final CharSequence[] words;

            public WordSequence(CharSequence charSequence) {
                if (charSequence == null) {
                    this.words = new CharSequence[0];
                    this.length = 0;
                    return;
                }
                this.length = charSequence.length();
                int i = 0;
                for (int i2 = 0; i2 < this.length; i2++) {
                    if (charSequence.charAt(i2) == ' ') {
                        i++;
                    }
                }
                this.words = new CharSequence[i + 1];
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                while (true) {
                    int i6 = this.length;
                    if (i3 > i6) {
                        return;
                    }
                    if (i3 == i6 || charSequence.charAt(i3) == ' ') {
                        int i7 = i4 + 1;
                        this.words[i4] = charSequence.subSequence(i5, (i3 < this.length ? 1 : 0) + i3);
                        i5 = i3 + 1;
                        i4 = i7;
                    }
                    i3++;
                }
            }

            @Override
            public char charAt(int i) {
                int i2 = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i2 >= charSequenceArr.length) {
                        return (char) 0;
                    }
                    if (i < charSequenceArr[i2].length()) {
                        return this.words[i2].charAt(i);
                    }
                    i -= this.words[i2].length();
                    i2++;
                }
            }

            @Override
            public IntStream chars() {
                if (Build.VERSION.SDK_INT >= 24) {
                    return UnprecomputeTextOnModificationSpannable$CharSequenceHelper_API24$$ExternalSyntheticAPIConversion1.m(toCharSequence());
                }
                return null;
            }

            @Override
            public java.util.stream.IntStream chars() {
                return IntStream.Wrapper.convert(chars());
            }

            @Override
            public IntStream codePoints() {
                if (Build.VERSION.SDK_INT >= 24) {
                    return UnprecomputeTextOnModificationSpannable$CharSequenceHelper_API24$$ExternalSyntheticAPIConversion0.m(toCharSequence());
                }
                return null;
            }

            @Override
            public java.util.stream.IntStream codePoints() {
                return IntStream.Wrapper.convert(codePoints());
            }

            @Override
            public int length() {
                return this.words.length;
            }

            @Override
            public CharSequence subSequence(int i, int i2) {
                return TextUtils.concat((CharSequence[]) Arrays.copyOfRange(this.words, i, i2));
            }

            public CharSequence toCharSequence() {
                return TextUtils.concat(this.words);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i >= charSequenceArr.length) {
                        return sb.toString();
                    }
                    sb.append(charSequenceArr[i]);
                    i++;
                }
            }

            public CharSequence wordAt(int i) {
                if (i < 0) {
                    return null;
                }
                CharSequence[] charSequenceArr = this.words;
                if (i >= charSequenceArr.length) {
                    return null;
                }
                return charSequenceArr[i];
            }
        }

        public AnimatedTextDrawable() {
            this(false, false, false);
        }

        public AnimatedTextDrawable(boolean z, boolean z2, boolean z3) {
            this(z, z2, z3, false);
        }

        public AnimatedTextDrawable(boolean z, boolean z2, boolean z3, boolean z4) {
            this.textPaint = new TextPaint(1);
            this.gravity = 0;
            this.isRTL = false;
            this.emojiCacheType = 0;
            this.t = 0.0f;
            this.moveDown = true;
            this.animateDelay = 0L;
            this.animateDuration = 320L;
            this.animateInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.animateWave = -1.0f;
            this.moveAmplitude = 0.3f;
            this.scaleAmplitude = 0.0f;
            this.alpha = 255;
            this.bounds = new android.graphics.Rect();
            this.includeFontPadding = true;
            this.centerY = true;
            this.shadowed = false;
            this.splitByWords = z;
            this.preserveIndex = z2;
            this.startFromEnd = z3;
            this.enforceByLetter = z4;
        }

        private void applyAlphaInternal(float f) {
            this.textPaint.setAlpha((int) (this.alpha * f));
            if (this.shadowed) {
                this.textPaint.setShadowLayer(this.shadowRadius, this.shadowDx, this.shadowDy, Theme.multAlpha(this.shadowColor, f));
            }
        }

        private void clearCurrentParts() {
            if (this.oldParts != null) {
                int i = 0;
                while (true) {
                    Part[] partArr = this.oldParts;
                    if (i >= partArr.length) {
                        break;
                    }
                    partArr[i].detach();
                    i++;
                }
            }
            this.oldParts = null;
        }

        public void clearOldParts() {
            if (this.oldParts != null) {
                int i = 0;
                while (true) {
                    Part[] partArr = this.oldParts;
                    if (i >= partArr.length) {
                        break;
                    }
                    partArr[i].detach();
                    i++;
                }
            }
            this.oldParts = null;
        }

        private void diff(CharSequence charSequence, CharSequence charSequence2, RegionCallback regionCallback, RegionCallback regionCallback2, RegionCallback regionCallback3) {
            if (this.updateAll) {
                part(regionCallback3, charSequence, 0, charSequence.length());
                part(regionCallback2, charSequence2, 0, charSequence2.length());
                return;
            }
            if (!this.preserveIndex) {
                int min = Math.min(charSequence2.length(), charSequence.length());
                int i = 0;
                int i2 = 0;
                boolean z = true;
                int i3 = 0;
                int i4 = 0;
                while (i <= min) {
                    boolean z2 = i < min && partEquals(charSequence2, charSequence, i, i2);
                    if (z != z2 || i == min) {
                        if (i == min) {
                            i = charSequence2.length();
                            i2 = charSequence.length();
                        }
                        int i5 = i - i3;
                        int i6 = i2 - i4;
                        if (i5 > 0 || i6 > 0) {
                            if (i5 == i6 && z) {
                                regionCallback.run(charSequence2.subSequence(i3, i), i3, i);
                            } else {
                                if (i5 > 0) {
                                    part(regionCallback2, charSequence2.subSequence(i3, i), i3, i);
                                }
                                if (i6 > 0) {
                                    part(regionCallback3, charSequence.subSequence(i4, i2), i4, i2);
                                }
                            }
                        }
                        i3 = i;
                        i4 = i2;
                        z = z2;
                    }
                    if (z2) {
                        i2++;
                    }
                    i++;
                }
                return;
            }
            int min2 = Math.min(charSequence2.length(), charSequence.length());
            if (!this.startFromEnd) {
                int i7 = 0;
                boolean z3 = true;
                int i8 = 0;
                while (i7 <= min2) {
                    boolean z4 = i7 < min2 && partEquals(charSequence2, charSequence, i7, i7);
                    if (z3 != z4 || i7 == min2) {
                        if (i7 - i8 > 0) {
                            if (z3) {
                                part(regionCallback, charSequence2.subSequence(i8, i7), i8, i7);
                            } else {
                                part(regionCallback2, charSequence2.subSequence(i8, i7), i8, i7);
                                part(regionCallback3, charSequence.subSequence(i8, i7), i8, i7);
                            }
                        }
                        i8 = i7;
                        z3 = z4;
                    }
                    i7++;
                }
                if (charSequence2.length() - min2 > 0) {
                    part(regionCallback2, charSequence2.subSequence(min2, charSequence2.length()), min2, charSequence2.length());
                }
                if (charSequence.length() - min2 > 0) {
                    part(regionCallback3, charSequence.subSequence(min2, charSequence.length()), min2, charSequence.length());
                    return;
                }
                return;
            }
            ArrayList arrayList = new ArrayList();
            boolean z5 = true;
            int i9 = 0;
            boolean z6 = true;
            for (int i10 = 0; i10 <= min2; i10++) {
                int length = (charSequence2.length() - i10) - 1;
                int length2 = (charSequence.length() - i10) - 1;
                boolean z7 = length >= 0 && length2 >= 0 && partEquals(charSequence2, charSequence, length, length2);
                if (z5 != z7 || i10 == min2) {
                    int i11 = i10 - i9;
                    if (i11 > 0) {
                        if (arrayList.size() != 0) {
                            z5 = z6;
                        }
                        arrayList.add(Integer.valueOf(i11));
                        z6 = z5;
                    }
                    i9 = i10;
                    z5 = z7;
                }
            }
            int length3 = charSequence2.length() - min2;
            int length4 = charSequence.length() - min2;
            if (length3 > 0) {
                part(regionCallback2, charSequence2.subSequence(0, length3), 0, length3);
            }
            if (length4 > 0) {
                part(regionCallback3, charSequence.subSequence(0, length4), 0, length4);
            }
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                int intValue = ((Integer) arrayList.get(size)).intValue();
                if ((size % 2 == 0) != z6) {
                    int i12 = length3 + intValue;
                    part(regionCallback2, charSequence2.subSequence(length3, i12), length3, i12);
                    int i13 = length4 + intValue;
                    part(regionCallback3, charSequence.subSequence(length4, i13), length4, i13);
                } else if (charSequence2.length() > charSequence.length()) {
                    int i14 = length3 + intValue;
                    regionCallback.run(charSequence2.subSequence(length3, i14), length3, i14);
                } else {
                    int i15 = length4 + intValue;
                    regionCallback.run(charSequence.subSequence(length4, i15), length4, i15);
                }
                length3 += intValue;
                length4 += intValue;
            }
        }

        public void lambda$setText$0(int i, ArrayList arrayList, ArrayList arrayList2, CharSequence charSequence, int i2, int i3) {
            StaticLayout makeLayout = makeLayout(charSequence, i - ((int) Math.ceil(Math.min(this.currentWidth, this.oldWidth))));
            Part part = new Part(makeLayout, this.currentWidth, arrayList.size());
            Part part2 = new Part(makeLayout, this.oldWidth, arrayList.size());
            arrayList2.add(part);
            arrayList.add(part2);
            float f = part.width;
            this.currentWidth += f;
            this.oldWidth += f;
            this.currentHeight = Math.max(this.currentHeight, makeLayout.getHeight());
            this.oldHeight = Math.max(this.oldHeight, makeLayout.getHeight());
        }

        public void lambda$setText$1(int i, ArrayList arrayList, CharSequence charSequence, int i2, int i3) {
            Part part = new Part(makeLayout(charSequence, i - ((int) Math.ceil(this.currentWidth))), this.currentWidth, -1);
            arrayList.add(part);
            this.currentWidth += part.width;
            this.currentHeight = Math.max(this.currentHeight, r1.getHeight());
        }

        public void lambda$setText$2(int i, ArrayList arrayList, CharSequence charSequence, int i2, int i3) {
            Part part = new Part(makeLayout(charSequence, i - ((int) Math.ceil(this.oldWidth))), this.oldWidth, -1);
            arrayList.add(part);
            this.oldWidth += part.width;
            this.oldHeight = Math.max(this.oldHeight, r1.getHeight());
        }

        public void lambda$setText$3(ValueAnimator valueAnimator) {
            this.t = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidateSelf();
            Runnable runnable = this.widthUpdatedListener;
            if (runnable != null) {
                runnable.run();
            }
        }

        public void lambda$setTextColor$9(int i, int i2, ValueAnimator valueAnimator) {
            setTextColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
            invalidateSelf();
        }

        private StaticLayout makeLayout(CharSequence charSequence, int i) {
            StaticLayout.Builder obtain;
            StaticLayout.Builder maxLines;
            StaticLayout.Builder lineSpacing;
            StaticLayout.Builder alignment;
            StaticLayout.Builder ellipsize;
            StaticLayout.Builder ellipsizedWidth;
            StaticLayout.Builder includePad;
            StaticLayout build;
            if (i <= 0) {
                android.graphics.Point point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
            }
            int i2 = i;
            if (Build.VERSION.SDK_INT < 23) {
                return new StaticLayout(charSequence, 0, charSequence.length(), this.textPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, this.includeFontPadding, TextUtils.TruncateAt.END, i2);
            }
            obtain = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.textPaint, i2);
            maxLines = obtain.setMaxLines(1);
            lineSpacing = maxLines.setLineSpacing(0.0f, 1.0f);
            alignment = lineSpacing.setAlignment(Layout.Alignment.ALIGN_NORMAL);
            ellipsize = alignment.setEllipsize(TextUtils.TruncateAt.END);
            ellipsizedWidth = ellipsize.setEllipsizedWidth(i2);
            includePad = ellipsizedWidth.setIncludePad(this.includeFontPadding);
            build = includePad.build();
            return build;
        }

        private void part(RegionCallback regionCallback, CharSequence charSequence, int i, int i2) {
            if (!this.enforceByLetter || charSequence.length() <= 1) {
                regionCallback.run(charSequence, i, i2);
                return;
            }
            int i3 = 0;
            while (i3 < charSequence.length()) {
                int i4 = i3 + 1;
                CharSequence subSequence = charSequence.subSequence(i3, i4);
                int i5 = i3 + i;
                regionCallback.run(subSequence, i5, i5 + 1);
                i3 = i4;
            }
        }

        public static boolean partEquals(CharSequence charSequence, CharSequence charSequence2, int i, int i2) {
            if (!(charSequence instanceof WordSequence) || !(charSequence2 instanceof WordSequence)) {
                return (charSequence == null && charSequence2 == null) || !(charSequence == null || charSequence2 == null || charSequence.charAt(i) != charSequence2.charAt(i2));
            }
            CharSequence wordAt = ((WordSequence) charSequence).wordAt(i);
            CharSequence wordAt2 = ((WordSequence) charSequence2).wordAt(i2);
            return (wordAt == null && wordAt2 == null) || (wordAt != null && wordAt.equals(wordAt2));
        }

        public void cancelAnimation() {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }

        @Override
        public void draw(android.graphics.Canvas r26) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedTextView.AnimatedTextDrawable.draw(android.graphics.Canvas):void");
        }

        public float getAnimateToWidth() {
            return this.currentWidth;
        }

        public float getCurrentWidth() {
            return (this.currentParts == null || this.oldParts == null) ? this.currentWidth : AndroidUtilities.lerp(this.oldWidth, this.currentWidth, this.t);
        }

        @Override
        public android.graphics.Rect getDirtyBounds() {
            return this.bounds;
        }

        public int getGravity() {
            return this.gravity;
        }

        public float getHeight() {
            return this.currentHeight;
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        public TextPaint getPaint() {
            return this.textPaint;
        }

        public float getRightPadding() {
            return this.rightPadding;
        }

        public CharSequence getText() {
            return this.currentText;
        }

        public int getTextColor() {
            return this.textPaint.getColor();
        }

        public float getTextSize() {
            return this.textPaint.getTextSize();
        }

        public float getWidth() {
            return Math.max(this.currentWidth, this.oldWidth);
        }

        public boolean isAnimating() {
            ValueAnimator valueAnimator = this.animator;
            return valueAnimator != null && valueAnimator.isRunning();
        }

        public float isNotEmpty() {
            CharSequence charSequence = this.oldText;
            float f = 0.0f;
            float f2 = (charSequence == null || charSequence.length() <= 0) ? 0.0f : 1.0f;
            CharSequence charSequence2 = this.currentText;
            if (charSequence2 != null && charSequence2.length() > 0) {
                f = 1.0f;
            }
            return AndroidUtilities.lerp(f2, f, this.oldText != null ? this.t : 1.0f);
        }

        public void setAllowCancel(boolean z) {
            this.allowCancel = z;
        }

        @Override
        public void setAlpha(int i) {
            this.alpha = i;
        }

        public void setAnimationProperties(float f, long j, long j2, float f2, TimeInterpolator timeInterpolator) {
            this.moveAmplitude = f;
            this.animateDelay = j;
            this.animateDuration = j2;
            this.animateWave = f2;
            this.animateInterpolator = timeInterpolator;
        }

        public void setAnimationProperties(float f, long j, long j2, TimeInterpolator timeInterpolator) {
            setAnimationProperties(f, j, j2, 1.0f, timeInterpolator);
        }

        public void setBounds(float f, float f2, float f3, float f4) {
            int i = (int) f;
            int i2 = (int) f2;
            int i3 = (int) f3;
            int i4 = (int) f4;
            super.setBounds(i, i2, i3, i4);
            this.bounds.set(i, i2, i3, i4);
        }

        @Override
        public void setBounds(int i, int i2, int i3, int i4) {
            super.setBounds(i, i2, i3, i4);
            this.bounds.set(i, i2, i3, i4);
        }

        @Override
        public void setBounds(android.graphics.Rect rect) {
            super.setBounds(rect);
            this.bounds.set(rect);
        }

        public void setBounds(RectF rectF) {
            setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.textPaint.setColorFilter(colorFilter);
        }

        public void setEllipsizeByGradient(boolean z) {
            this.ellipsizeByGradient = z;
            invalidateSelf();
        }

        public void setEmojiCacheType(int i) {
            this.emojiCacheType = i;
        }

        public void setEmojiColor(int i) {
            if (this.emojiColor != i) {
                this.emojiColor = i;
                this.emojiColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
            }
        }

        public void setEmojiColorFilter(ColorFilter colorFilter) {
            this.emojiColorFilter = colorFilter;
        }

        public void setGravity(int i) {
            this.gravity = i;
        }

        public void setHacks(boolean z, boolean z2, boolean z3) {
            setHacks(z, z2, z3, false);
        }

        public void setHacks(boolean z, boolean z2, boolean z3, boolean z4) {
            this.splitByWords = z;
            this.preserveIndex = z2;
            this.startFromEnd = z3;
            this.enforceByLetter = z4;
        }

        public void setIncludeFontPadding(boolean z) {
            this.includeFontPadding = z;
        }

        public void setOnAnimationFinishListener(Runnable runnable) {
            this.onAnimationFinishListener = runnable;
        }

        public void setOnWidthUpdatedListener(Runnable runnable) {
            this.widthUpdatedListener = runnable;
        }

        public void setOverrideFullWidth(int i) {
            this.overrideFullWidth = i;
        }

        public void setRightPadding(float f) {
            this.rightPadding = f;
            invalidateSelf();
        }

        public void setScaleProperty(float f) {
            this.scaleAmplitude = f;
        }

        public void setShadowLayer(float f, float f2, float f3, int i) {
            this.shadowed = true;
            TextPaint textPaint = this.textPaint;
            this.shadowRadius = f;
            this.shadowDx = f2;
            this.shadowDy = f3;
            this.shadowColor = i;
            textPaint.setShadowLayer(f, f2, f3, i);
        }

        public void setSplitByWords(boolean z) {
            this.splitByWords = z;
        }

        public void setText(CharSequence charSequence) {
            setText(charSequence, true);
        }

        public void setText(CharSequence charSequence, boolean z) {
            setText(charSequence, z, true);
        }

        public void setText(CharSequence charSequence, boolean z, boolean z2) {
            if (this.currentText == null || charSequence == null) {
                z = false;
            }
            if (charSequence == null) {
                charSequence = "";
            }
            final int i = this.overrideFullWidth;
            if (i <= 0) {
                i = this.bounds.width();
            }
            if (!z) {
                ValueAnimator valueAnimator = this.animator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.animator = null;
                this.toSetText = null;
                this.toSetTextMoveDown = false;
                this.t = 0.0f;
                if (!charSequence.equals(this.currentText)) {
                    clearCurrentParts();
                    this.currentParts = r12;
                    this.currentText = charSequence;
                    Part[] partArr = {new Part(makeLayout(charSequence, i), 0.0f, -1)};
                    this.currentWidth = this.currentParts[0].width;
                    this.currentHeight = r11.layout.getHeight();
                    this.isRTL = AndroidUtilities.isRTL(this.currentText);
                }
                clearOldParts();
                this.oldText = null;
                this.oldWidth = 0.0f;
                this.oldHeight = 0.0f;
                invalidateSelf();
                Runnable runnable = this.widthUpdatedListener;
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
            if (this.allowCancel) {
                ValueAnimator valueAnimator2 = this.animator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                    this.animator = null;
                }
            } else if (isAnimating()) {
                this.toSetText = charSequence;
                this.toSetTextMoveDown = z2;
                return;
            }
            if (charSequence.equals(this.currentText)) {
                return;
            }
            this.oldText = this.currentText;
            this.currentText = charSequence;
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            this.currentHeight = 0.0f;
            this.currentWidth = 0.0f;
            this.oldHeight = 0.0f;
            this.oldWidth = 0.0f;
            this.isRTL = AndroidUtilities.isRTL(this.currentText);
            diff(this.splitByWords ? new WordSequence(this.oldText) : this.oldText, this.splitByWords ? new WordSequence(this.currentText) : this.currentText, new RegionCallback() {
                @Override
                public final void run(CharSequence charSequence2, int i2, int i3) {
                    AnimatedTextView.AnimatedTextDrawable.this.lambda$setText$0(i, arrayList2, arrayList, charSequence2, i2, i3);
                }
            }, new RegionCallback() {
                @Override
                public final void run(CharSequence charSequence2, int i2, int i3) {
                    AnimatedTextView.AnimatedTextDrawable.this.lambda$setText$1(i, arrayList, charSequence2, i2, i3);
                }
            }, new RegionCallback() {
                @Override
                public final void run(CharSequence charSequence2, int i2, int i3) {
                    AnimatedTextView.AnimatedTextDrawable.this.lambda$setText$2(i, arrayList2, charSequence2, i2, i3);
                }
            });
            clearCurrentParts();
            Part[] partArr2 = this.currentParts;
            if (partArr2 == null || partArr2.length != arrayList.size()) {
                this.currentParts = new Part[arrayList.size()];
            }
            arrayList.toArray(this.currentParts);
            clearOldParts();
            Part[] partArr3 = this.oldParts;
            if (partArr3 == null || partArr3.length != arrayList2.size()) {
                this.oldParts = new Part[arrayList2.size()];
            }
            arrayList2.toArray(this.oldParts);
            ValueAnimator valueAnimator3 = this.animator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
            }
            this.moveDown = z2;
            this.t = 0.0f;
            this.animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            Runnable runnable2 = this.widthUpdatedListener;
            if (runnable2 != null) {
                runnable2.run();
            }
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                    AnimatedTextView.AnimatedTextDrawable.this.lambda$setText$3(valueAnimator4);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    AnimatedTextDrawable.this.clearOldParts();
                    AnimatedTextDrawable.this.oldText = null;
                    AnimatedTextDrawable.this.oldWidth = 0.0f;
                    AnimatedTextDrawable.this.t = 0.0f;
                    AnimatedTextDrawable.this.invalidateSelf();
                    if (AnimatedTextDrawable.this.widthUpdatedListener != null) {
                        AnimatedTextDrawable.this.widthUpdatedListener.run();
                    }
                    AnimatedTextDrawable.this.animator = null;
                    if (AnimatedTextDrawable.this.toSetText == null) {
                        if (AnimatedTextDrawable.this.onAnimationFinishListener != null) {
                            AnimatedTextDrawable.this.onAnimationFinishListener.run();
                        }
                    } else {
                        AnimatedTextDrawable animatedTextDrawable = AnimatedTextDrawable.this;
                        animatedTextDrawable.setText(animatedTextDrawable.toSetText, true, AnimatedTextDrawable.this.toSetTextMoveDown);
                        AnimatedTextDrawable.this.toSetText = null;
                        AnimatedTextDrawable.this.toSetTextMoveDown = false;
                    }
                }
            });
            this.animator.setStartDelay(this.animateDelay);
            this.animator.setDuration(this.animateDuration);
            this.animator.setInterpolator(this.animateInterpolator);
            this.animator.start();
        }

        public void setTextColor(int i) {
            this.textPaint.setColor(i);
            this.alpha = Color.alpha(i);
        }

        public void setTextColor(final int i, boolean z) {
            ValueAnimator valueAnimator = this.colorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.colorAnimator = null;
            }
            if (!z) {
                setTextColor(i);
                return;
            }
            final int textColor = getTextColor();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.colorAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    AnimatedTextView.AnimatedTextDrawable.this.lambda$setTextColor$9(textColor, i, valueAnimator2);
                }
            });
            this.colorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    AnimatedTextDrawable.this.setTextColor(i);
                }
            });
            this.colorAnimator.setDuration(240L);
            this.colorAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.colorAnimator.start();
        }

        public void setTextSize(float f) {
            float textSize = this.textPaint.getTextSize();
            this.textPaint.setTextSize(f);
            if (Math.abs(textSize - f) > 0.5f) {
                int i = this.overrideFullWidth;
                if (i <= 0) {
                    i = this.bounds.width();
                }
                int i2 = 0;
                if (this.currentParts != null) {
                    this.currentWidth = 0.0f;
                    this.currentHeight = 0.0f;
                    int i3 = 0;
                    while (true) {
                        Part[] partArr = this.currentParts;
                        if (i3 >= partArr.length) {
                            break;
                        }
                        StaticLayout makeLayout = makeLayout(partArr[i3].layout.getText(), i - ((int) Math.ceil(Math.min(this.currentWidth, this.oldWidth))));
                        Part[] partArr2 = this.currentParts;
                        Part part = partArr2[i3];
                        partArr2[i3] = new Part(makeLayout, part.offset, part.toOppositeIndex);
                        this.currentWidth = this.currentWidth + this.currentParts[i3].width;
                        this.currentHeight = Math.max(this.currentHeight, r4.layout.getHeight());
                        i3++;
                    }
                }
                if (this.oldParts != null) {
                    this.oldWidth = 0.0f;
                    this.oldHeight = 0.0f;
                    while (true) {
                        Part[] partArr3 = this.oldParts;
                        if (i2 >= partArr3.length) {
                            break;
                        }
                        StaticLayout makeLayout2 = makeLayout(partArr3[i2].layout.getText(), i - ((int) Math.ceil(Math.min(this.currentWidth, this.oldWidth))));
                        Part[] partArr4 = this.oldParts;
                        Part part2 = partArr4[i2];
                        partArr4[i2] = new Part(makeLayout2, part2.offset, part2.toOppositeIndex);
                        this.oldWidth = this.oldWidth + this.oldParts[i2].width;
                        this.oldHeight = Math.max(this.oldHeight, r2.layout.getHeight());
                        i2++;
                    }
                }
                invalidateSelf();
            }
        }

        public void setTypeface(Typeface typeface) {
            this.textPaint.setTypeface(typeface);
        }
    }

    public AnimatedTextView(Context context) {
        this(context, false, false, false);
    }

    public AnimatedTextView(Context context, boolean z, boolean z2, boolean z3) {
        super(context);
        this.adaptWidth = true;
        this.first = true;
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable(z, z2, z3);
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
        animatedTextDrawable.setOnAnimationFinishListener(new Runnable() {
            @Override
            public final void run() {
                AnimatedTextView.this.lambda$new$0();
            }
        });
    }

    public void lambda$new$0() {
        CharSequence charSequence = this.toSetText;
        if (charSequence != null) {
            setText(charSequence, this.toSetMoveDown, true);
            this.toSetText = null;
            this.toSetMoveDown = false;
        }
    }

    public void cancelAnimation() {
        this.drawable.cancelAnimation();
    }

    public AnimatedTextDrawable getDrawable() {
        return this.drawable;
    }

    public TextPaint getPaint() {
        return this.drawable.getPaint();
    }

    public float getRightPadding() {
        return this.drawable.getRightPadding();
    }

    public CharSequence getText() {
        return this.drawable.getText();
    }

    public int getTextColor() {
        return this.drawable.getTextColor();
    }

    public int getTextHeight() {
        return getPaint().getFontMetricsInt().descent - getPaint().getFontMetricsInt().ascent;
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidate();
    }

    public boolean isAnimating() {
        return this.drawable.isAnimating();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        this.drawable.draw(canvas);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.TextView");
        accessibilityNodeInfo.setText(getText());
    }

    @Override
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int i3 = this.maxWidth;
        if (i3 > 0) {
            size = Math.min(size, i3);
        }
        if (this.lastMaxWidth != size && getLayoutParams().width != 0) {
            this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), size - getPaddingRight(), size2 - getPaddingBottom());
            AnimatedTextDrawable animatedTextDrawable = this.drawable;
            animatedTextDrawable.setText(animatedTextDrawable.getText(), false, true);
        }
        this.lastMaxWidth = size;
        if (this.adaptWidth && View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
            size = getPaddingRight() + getPaddingLeft() + ((int) Math.ceil(this.drawable.getWidth()));
        }
        setMeasuredDimension(size, size2);
    }

    public void setAnimationProperties(float f, long j, long j2, TimeInterpolator timeInterpolator) {
        this.drawable.setAnimationProperties(f, j, j2, timeInterpolator);
    }

    public void setEllipsizeByGradient(boolean z) {
        this.drawable.setEllipsizeByGradient(z);
    }

    public void setEmojiCacheType(int i) {
        this.drawable.setEmojiCacheType(i);
    }

    public void setEmojiColor(int i) {
        this.drawable.setEmojiColor(i);
        invalidate();
    }

    public void setEmojiColorFilter(ColorFilter colorFilter) {
        this.drawable.setEmojiColorFilter(colorFilter);
        invalidate();
    }

    public void setGravity(int i) {
        this.drawable.setGravity(i);
    }

    public void setIgnoreRTL(boolean z) {
        this.drawable.ignoreRTL = z;
    }

    public void setIncludeFontPadding(boolean z) {
        this.drawable.setIncludeFontPadding(z);
    }

    public void setMaxWidth(int i) {
        this.maxWidth = i;
    }

    public void setOnWidthUpdatedListener(Runnable runnable) {
        this.drawable.setOnWidthUpdatedListener(runnable);
    }

    public void setRightPadding(float f) {
        this.drawable.setRightPadding(f);
    }

    public void setScaleProperty(float f) {
        this.drawable.setScaleProperty(f);
    }

    public void setText(CharSequence charSequence) {
        setText(charSequence, true, true);
    }

    public void setText(CharSequence charSequence, boolean z) {
        setText(charSequence, z, true);
    }

    public void setText(CharSequence charSequence, boolean z, boolean z2) {
        boolean z3 = !this.first && z;
        this.first = false;
        if (z3) {
            if (this.drawable.allowCancel) {
                if (this.drawable.animator != null) {
                    this.drawable.animator.cancel();
                    this.drawable.animator = null;
                }
            } else if (this.drawable.isAnimating()) {
                this.toSetText = charSequence;
                this.toSetMoveDown = z2;
                return;
            }
        }
        int width = (int) this.drawable.getWidth();
        this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), this.lastMaxWidth - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        this.drawable.setText(charSequence, z3, z2);
        float f = width;
        if (f < this.drawable.getWidth() || !(z3 || f == this.drawable.getWidth())) {
            requestLayout();
        }
    }

    public void setTextColor(int i) {
        this.drawable.setTextColor(i);
        invalidate();
    }

    public void setTextColor(int i, boolean z) {
        this.drawable.setTextColor(i, z);
        invalidate();
    }

    public void setTextSize(float f) {
        this.drawable.setTextSize(f);
    }

    public void setTypeface(Typeface typeface) {
        this.drawable.setTypeface(typeface);
    }

    public int width() {
        return getPaddingLeft() + ((int) Math.ceil(this.drawable.getCurrentWidth())) + getPaddingRight();
    }
}
