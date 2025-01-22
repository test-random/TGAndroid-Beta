package org.telegram.ui.Components.spoilers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextStyleSpan;

public class SpoilerEffect extends Drawable {
    private static WeakHashMap lazyLayoutLines;
    private static Paint xRefPaint;
    private int bitmapSize;
    public boolean drawPoints;
    private boolean enableAlpha;
    public boolean insideQuote;
    private boolean invalidateParent;
    private boolean isLowDevice;
    private int lastColor;
    private long lastDrawTime;
    private int mAlpha;
    private View mParent;
    private int maxParticles;
    private Runnable onRippleEndCallback;
    private final Paint[] particlePaints;
    float[][] particlePoints;
    private final float[] particleRands;
    private final ArrayList particles;
    private final Stack particlesPool;
    private final int[] renderCount;
    private boolean reverseAnimator;
    private ValueAnimator rippleAnimator;
    private TimeInterpolator rippleInterpolator;
    private float rippleMaxRadius;
    private float rippleProgress;
    private float rippleX;
    private float rippleY;
    private boolean shouldInvalidateColor;
    private List spaces;
    private boolean suppressUpdates;
    private RectF visibleRect;
    public static final int MAX_PARTICLES_PER_ENTITY = measureMaxParticlesCount();
    public static final int PARTICLES_PER_CHARACTER = measureParticlesPerCharacter();
    public static final float[] ALPHAS = {0.3f, 0.6f, 1.0f};
    private static Path tempPath = new Path();

    public static class Particle {
        private int alpha;
        private float currentTime;
        private float lifeTime;
        private float vecX;
        private float vecY;
        private float velocity;
        private float x;
        private float y;

        private Particle() {
        }

        static float access$516(Particle particle, float f) {
            float f2 = particle.x + f;
            particle.x = f2;
            return f2;
        }

        static float access$616(Particle particle, float f) {
            float f2 = particle.y + f;
            particle.y = f2;
            return f2;
        }
    }

    public SpoilerEffect() {
        Paint paint;
        float[] fArr = ALPHAS;
        this.particlePaints = new Paint[fArr.length];
        this.particlesPool = new Stack();
        this.particlePoints = (float[][]) Array.newInstance((Class<?>) Float.TYPE, fArr.length, MAX_PARTICLES_PER_ENTITY * 5);
        this.particleRands = new float[14];
        this.renderCount = new int[fArr.length];
        this.particles = new ArrayList();
        this.rippleProgress = -1.0f;
        this.spaces = new ArrayList();
        this.mAlpha = 255;
        this.rippleInterpolator = new TimeInterpolator() {
            @Override
            public final float getInterpolation(float f) {
                float lambda$new$0;
                lambda$new$0 = SpoilerEffect.lambda$new$0(f);
                return lambda$new$0;
            }
        };
        for (int i = 0; i < ALPHAS.length; i++) {
            this.particlePaints[i] = new Paint();
            Paint[] paintArr = this.particlePaints;
            if (i == 0) {
                paintArr[i].setStrokeWidth(AndroidUtilities.dp(1.4f));
                this.particlePaints[i].setStyle(Paint.Style.STROKE);
                paint = this.particlePaints[i];
            } else {
                paintArr[i].setStrokeWidth(AndroidUtilities.dp(1.2f));
                this.particlePaints[i].setStyle(Paint.Style.STROKE);
                paint = this.particlePaints[i];
            }
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
        this.isLowDevice = SharedConfig.getDevicePerformanceClass() == 0;
        this.enableAlpha = true;
        setColor(0);
    }

    public static void addSpoilerRangeInternal(View view, Layout layout, float f, float f2, float f3, float f4, Stack stack, List list, int i, int i2, ArrayList arrayList) {
        int i3 = 0;
        SpoilerEffect spoilerEffect = (stack == null || stack.isEmpty()) ? new SpoilerEffect() : (SpoilerEffect) stack.remove(0);
        spoilerEffect.insideQuote = false;
        if (arrayList != null) {
            float f5 = (f2 + f4) / 2.0f;
            while (true) {
                if (i3 >= arrayList.size()) {
                    break;
                }
                QuoteSpan.Block block = (QuoteSpan.Block) arrayList.get(i3);
                if (f5 >= block.top && f5 <= block.bottom) {
                    spoilerEffect.insideQuote = true;
                    break;
                }
                i3++;
            }
        }
        spoilerEffect.setRippleProgress(-1.0f);
        spoilerEffect.setBounds((int) Math.max(f, i), (int) f2, (int) Math.min(f3, i2 <= 0 ? 2.1474836E9f : i2), (int) f4);
        spoilerEffect.setColor(layout.getPaint().getColor());
        spoilerEffect.setRippleInterpolator(Easings.easeInQuad);
        spoilerEffect.updateMaxParticles();
        if (view != null) {
            spoilerEffect.setParentView(view);
        }
        list.add(spoilerEffect);
    }

    private static void addSpoilerRangesInternal(final View view, final Layout layout, final int i, final int i2, int i3, int i4, final Stack stack, final List list, final ArrayList arrayList) {
        layout.getSelectionPath(i3, i4, new Path() {
            @Override
            public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
                SpoilerEffect.addSpoilerRangeInternal(view, layout, f, f2, f3, f4, stack, list, i, i2, arrayList);
            }
        });
    }

    public static void addSpoilers(View view, Layout layout, int i, int i2, Spanned spanned, Stack stack, List list, ArrayList arrayList) {
        int i3;
        int i4;
        if (layout == null) {
            return;
        }
        TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) spanned.getSpans(0, layout.getText().length(), TextStyleSpan.class);
        for (int i5 = 0; i5 < Math.min(100, textStyleSpanArr.length); i5++) {
            if (textStyleSpanArr[i5].isSpoiler()) {
                int spanStart = spanned.getSpanStart(textStyleSpanArr[i5]);
                int spanEnd = spanned.getSpanEnd(textStyleSpanArr[i5]);
                if (i == -1 && i2 == -1) {
                    int lineForOffset = layout.getLineForOffset(spanEnd);
                    i3 = Integer.MAX_VALUE;
                    i4 = Integer.MIN_VALUE;
                    for (int lineForOffset2 = layout.getLineForOffset(spanStart); lineForOffset2 <= lineForOffset; lineForOffset2++) {
                        i3 = Math.min(i3, (int) layout.getLineLeft(lineForOffset2));
                        i4 = Math.max(i4, (int) layout.getLineRight(lineForOffset2));
                    }
                } else {
                    i3 = i;
                    i4 = i2;
                }
                addSpoilerRangesInternal(view, layout, i3, i4, spanStart, spanEnd, stack, list, arrayList);
            }
        }
        if (!(view instanceof TextView) || stack == null) {
            return;
        }
        stack.clear();
    }

    public static void addSpoilers(View view, Layout layout, int i, int i2, Stack stack, List list) {
        if (layout.getText() instanceof Spanned) {
            addSpoilers(view, layout, i, i2, (Spanned) layout.getText(), stack, list, null);
        }
    }

    public static void addSpoilers(View view, Layout layout, Spanned spanned, Stack stack, List list) {
        if (layout == null) {
            return;
        }
        addSpoilers(view, layout, -1, -1, spanned, stack, list, null);
    }

    public static void addSpoilers(View view, Layout layout, Stack stack, List list) {
        if (layout.getText() instanceof Spanned) {
            addSpoilers(view, layout, (Spanned) layout.getText(), stack, list);
        }
    }

    public static void addSpoilers(TextView textView, Stack stack, List list) {
        int measuredWidth = textView.getMeasuredWidth();
        addSpoilers(textView, textView.getLayout(), 0, measuredWidth > 0 ? measuredWidth : -2, (Spanned) textView.getText(), stack, list, null);
    }

    public static void addSpoilers(TextView textView, Stack stack, List list, ArrayList arrayList) {
        int measuredWidth = textView.getMeasuredWidth();
        addSpoilers(textView, textView.getLayout(), 0, measuredWidth > 0 ? measuredWidth : -2, (Spanned) textView.getText(), stack, list, arrayList);
    }

    public static void clipOutCanvas(Canvas canvas, List list) {
        tempPath.rewind();
        for (int i = 0; i < list.size(); i++) {
            Rect bounds = ((SpoilerEffect) list.get(i)).getBounds();
            tempPath.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
        canvas.clipPath(tempPath, Region.Op.DIFFERENCE);
    }

    private void generateRandomLocation(Particle particle, int i) {
        particle.x = getBounds().left + (Utilities.fastRandom.nextFloat() * getBounds().width());
        particle.y = getBounds().top + (Utilities.fastRandom.nextFloat() * getBounds().height());
    }

    private boolean isOutOfBounds(int i, int i2, int i3, int i4, float f, float f2) {
        if (f < i || f > i3 || f2 < i2 + AndroidUtilities.dp(2.5f) || f2 > i4 - AndroidUtilities.dp(2.5f)) {
            return true;
        }
        for (int i5 = 0; i5 < this.spaces.size(); i5++) {
            if (((RectF) this.spaces.get(i5)).contains(f, f2)) {
                return true;
            }
        }
        return false;
    }

    public static float lambda$new$0(float f) {
        return f;
    }

    public void lambda$startRipple$1(int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.rippleProgress = floatValue;
        setAlpha((int) (i * (1.0f - floatValue)));
        this.shouldInvalidateColor = true;
        invalidateSelf();
    }

    public static void layoutDrawMaybe(Layout layout, Canvas canvas) {
        if (!(canvas instanceof SizeNotifierFrameLayout.SimplerCanvas)) {
            layout.draw(canvas);
            return;
        }
        int alpha = layout.getPaint().getAlpha();
        layout.getPaint().setAlpha((int) (alpha * 0.4f));
        if (lazyLayoutLines == null) {
            lazyLayoutLines = new WeakHashMap();
        }
        ArrayList arrayList = (ArrayList) lazyLayoutLines.get(layout);
        if (arrayList == null) {
            arrayList = new ArrayList();
            int lineCount = layout.getLineCount();
            for (int i = 0; i < lineCount; i++) {
                arrayList.add(new RectF(layout.getLineLeft(i), layout.getLineTop(i), layout.getLineRight(i), layout.getLineBottom(i)));
            }
            lazyLayoutLines.put(layout, arrayList);
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            canvas.drawRect((RectF) arrayList.get(i2), layout.getPaint());
        }
        layout.getPaint().setAlpha(alpha);
    }

    private static int measureMaxParticlesCount() {
        return SharedConfig.getDevicePerformanceClass() != 2 ? 100 : 150;
    }

    private static int measureParticlesPerCharacter() {
        return SharedConfig.getDevicePerformanceClass() != 2 ? 10 : 30;
    }

    public static void renderWithRipple(View view, boolean z, int i, int i2, AtomicReference atomicReference, int i3, Layout layout, List list, Canvas canvas, boolean z2) {
        StaticLayout staticLayout;
        StaticLayout.Builder obtain;
        StaticLayout.Builder breakStrategy;
        StaticLayout.Builder hyphenationFrequency;
        StaticLayout.Builder alignment;
        StaticLayout.Builder lineSpacing;
        TextStyleSpan[] textStyleSpanArr;
        if (list == null || list.isEmpty()) {
            layoutDrawMaybe(layout, canvas);
            return;
        }
        Layout layout2 = (Layout) atomicReference.get();
        int i4 = 0;
        if (layout2 == null || !layout.getText().toString().equals(layout2.getText().toString()) || layout.getWidth() != layout2.getWidth() || layout.getHeight() != layout2.getHeight()) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(layout.getText());
            if (layout.getText() instanceof Spanned) {
                Spanned spanned = (Spanned) layout.getText();
                TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) spanned.getSpans(0, spanned.length(), TextStyleSpan.class);
                int i5 = 0;
                while (i5 < Math.min(100, textStyleSpanArr2.length)) {
                    TextStyleSpan textStyleSpan = textStyleSpanArr2[i5];
                    if (textStyleSpan.isSpoiler()) {
                        int spanStart = spanned.getSpanStart(textStyleSpan);
                        int spanEnd = spanned.getSpanEnd(textStyleSpan);
                        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spanned.getSpans(spanStart, spanEnd, Emoji.EmojiSpan.class);
                        int length = emojiSpanArr.length;
                        while (true) {
                            textStyleSpanArr = textStyleSpanArr2;
                            if (i4 >= length) {
                                break;
                            }
                            final Emoji.EmojiSpan emojiSpan = emojiSpanArr[i4];
                            spannableStringBuilder.setSpan(new ReplacementSpan() {
                                @Override
                                public void draw(Canvas canvas2, CharSequence charSequence, int i6, int i7, float f, int i8, int i9, int i10, Paint paint) {
                                }

                                @Override
                                public int getSize(Paint paint, CharSequence charSequence, int i6, int i7, Paint.FontMetricsInt fontMetricsInt) {
                                    return Emoji.EmojiSpan.this.getSize(paint, charSequence, i6, i7, fontMetricsInt);
                                }
                            }, spanned.getSpanStart(emojiSpan), spanned.getSpanEnd(emojiSpan), spanned.getSpanFlags(textStyleSpan));
                            spannableStringBuilder.removeSpan(emojiSpan);
                            i4++;
                            textStyleSpanArr2 = textStyleSpanArr;
                            length = length;
                            emojiSpanArr = emojiSpanArr;
                        }
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(0), spanStart, spanEnd, spanned.getSpanFlags(textStyleSpan));
                        spannableStringBuilder.removeSpan(textStyleSpan);
                    } else {
                        textStyleSpanArr = textStyleSpanArr2;
                    }
                    i5++;
                    textStyleSpanArr2 = textStyleSpanArr;
                    i4 = 0;
                }
            }
            if (i3 == 1) {
                staticLayout = new StaticLayout(spannableStringBuilder, layout.getPaint(), layout.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, AndroidUtilities.dp(1.66f), false);
            } else if (Build.VERSION.SDK_INT >= 24) {
                obtain = StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), layout.getPaint(), layout.getWidth());
                breakStrategy = obtain.setBreakStrategy(1);
                hyphenationFrequency = breakStrategy.setHyphenationFrequency(0);
                alignment = hyphenationFrequency.setAlignment(layout.getAlignment());
                lineSpacing = alignment.setLineSpacing(layout.getSpacingAdd(), layout.getSpacingMultiplier());
                staticLayout = lineSpacing.build();
            } else {
                staticLayout = new StaticLayout(spannableStringBuilder, layout.getPaint(), layout.getWidth(), layout.getAlignment(), layout.getSpacingMultiplier(), layout.getSpacingAdd(), false);
            }
            layout2 = staticLayout;
            atomicReference.set(layout2);
        }
        if (list.isEmpty()) {
            layoutDrawMaybe(layout, canvas);
        } else {
            canvas.save();
            canvas.translate(0.0f, i2);
            layout2.draw(canvas);
            canvas.restore();
        }
        if (list.isEmpty()) {
            return;
        }
        tempPath.rewind();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Rect bounds = ((SpoilerEffect) it.next()).getBounds();
            tempPath.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
        if (!list.isEmpty() && ((SpoilerEffect) list.get(0)).rippleProgress != -1.0f) {
            canvas.save();
            canvas.clipPath(tempPath);
            tempPath.rewind();
            if (!list.isEmpty()) {
                ((SpoilerEffect) list.get(0)).getRipplePath(tempPath);
            }
            canvas.clipPath(tempPath);
            canvas.translate(0.0f, -view.getPaddingTop());
            layoutDrawMaybe(layout, canvas);
            canvas.restore();
        }
        boolean z3 = ((SpoilerEffect) list.get(0)).rippleProgress != -1.0f;
        if (z3) {
            int measuredWidth = view.getMeasuredWidth();
            if (z2 && (view.getParent() instanceof View)) {
                measuredWidth = ((View) view.getParent()).getMeasuredWidth();
            }
            canvas.saveLayer(0.0f, 0.0f, measuredWidth, view.getMeasuredHeight(), null, 31);
        } else {
            canvas.save();
        }
        canvas.translate(0.0f, -view.getPaddingTop());
        Iterator it2 = list.iterator();
        while (it2.hasNext()) {
            SpoilerEffect spoilerEffect = (SpoilerEffect) it2.next();
            spoilerEffect.setInvalidateParent(z);
            if (spoilerEffect.getParentView() != view) {
                spoilerEffect.setParentView(view);
            }
            if (spoilerEffect.shouldInvalidateColor()) {
                spoilerEffect.setColor(ColorUtils.blendARGB(i, (i3 == 1 ? layout.getPaint() : Theme.chat_msgTextPaint).getColor(), Math.max(0.0f, spoilerEffect.getRippleProgress())));
            } else {
                spoilerEffect.setColor(i);
            }
            spoilerEffect.draw(canvas);
        }
        if (z3) {
            tempPath.rewind();
            ((SpoilerEffect) list.get(0)).getRipplePath(tempPath);
            if (xRefPaint == null) {
                Paint paint = new Paint(1);
                xRefPaint = paint;
                paint.setColor(-16777216);
                xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            canvas.drawPath(tempPath, xRefPaint);
        }
        canvas.restore();
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rectF;
        Particle particle;
        int i;
        float f;
        if (!this.drawPoints) {
            SpoilerEffectBitmapFactory.getInstance().getPaint().setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.SRC_IN));
            canvas.drawRect(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom, SpoilerEffectBitmapFactory.getInstance().getPaint());
            if (LiteMode.isEnabled(128)) {
                invalidateSelf();
                SpoilerEffectBitmapFactory.getInstance().checkUpdate();
                return;
            }
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int min = (int) Math.min(currentTimeMillis - this.lastDrawTime, 34L);
        this.lastDrawTime = currentTimeMillis;
        int i2 = getBounds().left;
        int i3 = getBounds().top;
        int i4 = getBounds().right;
        int i5 = getBounds().bottom;
        for (int i6 = 0; i6 < ALPHAS.length; i6++) {
            this.renderCount[i6] = 0;
        }
        int i7 = 0;
        while (i7 < this.particles.size()) {
            Particle particle2 = (Particle) this.particles.get(i7);
            float f2 = min;
            particle2.currentTime = Math.min(particle2.currentTime + f2, particle2.lifeTime);
            if (particle2.currentTime >= particle2.lifeTime || isOutOfBounds(i2, i3, i4, i5, particle2.x, particle2.y)) {
                if (this.particlesPool.size() < this.maxParticles) {
                    this.particlesPool.push(particle2);
                }
                this.particles.remove(i7);
                i7--;
            } else {
                float f3 = (particle2.velocity * f2) / 500.0f;
                Particle.access$516(particle2, particle2.vecX * f3);
                Particle.access$616(particle2, particle2.vecY * f3);
            }
            i7++;
        }
        int size = this.particles.size();
        int i8 = this.maxParticles;
        if (size < i8) {
            int size2 = i8 - this.particles.size();
            float f4 = -1.0f;
            Arrays.fill(this.particleRands, -1.0f);
            int i9 = 0;
            while (i9 < size2) {
                float[] fArr = this.particleRands;
                int i10 = i9 % 14;
                float f5 = fArr[i10];
                if (f5 == f4) {
                    f5 = Utilities.fastRandom.nextFloat();
                    fArr[i10] = f5;
                }
                float f6 = f5;
                Particle particle3 = !this.particlesPool.isEmpty() ? (Particle) this.particlesPool.pop() : new Particle();
                int i11 = 0;
                while (true) {
                    generateRandomLocation(particle3, i9);
                    int i12 = i11 + 1;
                    particle = particle3;
                    i = size2;
                    f = f6;
                    if (isOutOfBounds(i2, i3, i4, i5, particle3.x, particle3.y) && i12 < 4) {
                        f6 = f;
                        particle3 = particle;
                        i11 = i12;
                        size2 = i;
                    }
                }
                double d = f;
                Double.isNaN(d);
                double d2 = ((d * 3.141592653589793d) * 2.0d) - 3.141592653589793d;
                float cos = (float) Math.cos(d2);
                float sin = (float) Math.sin(d2);
                particle.vecX = cos;
                particle.vecY = sin;
                particle.currentTime = 0.0f;
                particle.lifeTime = Math.abs(Utilities.fastRandom.nextInt(2000)) + 1000;
                particle.velocity = (f * 6.0f) + 4.0f;
                particle.alpha = Utilities.fastRandom.nextInt(ALPHAS.length);
                this.particles.add(particle);
                i9++;
                size2 = i;
                f4 = -1.0f;
            }
        }
        for (int length = this.enableAlpha ? 0 : ALPHAS.length - 1; length < ALPHAS.length; length++) {
            float strokeWidth = this.particlePaints[length].getStrokeWidth() / 2.0f;
            int i13 = 0;
            for (int i14 = 0; i14 < this.particles.size(); i14++) {
                Particle particle4 = (Particle) this.particles.get(i14);
                if (particle4 != null && (((rectF = this.visibleRect) == null || rectF.contains(particle4.x, particle4.y)) && (particle4.alpha == length || !this.enableAlpha))) {
                    float[] fArr2 = this.particlePoints[length];
                    if (i13 < fArr2.length - 2) {
                        fArr2[i13] = particle4.x;
                        this.particlePoints[length][i13 + 1] = particle4.y;
                        int i15 = i13 + 2;
                        if (particle4.x < strokeWidth) {
                            float[] fArr3 = this.particlePoints[length];
                            if (i15 < fArr3.length - 2) {
                                fArr3[i15] = particle4.x + this.bitmapSize;
                                this.particlePoints[length][i13 + 3] = particle4.y;
                                i15 = i13 + 4;
                            }
                            i13 = i15;
                        }
                        if (particle4.x > this.bitmapSize - strokeWidth) {
                            float[] fArr4 = this.particlePoints[length];
                            if (i15 < fArr4.length - 2) {
                                fArr4[i15] = particle4.x - this.bitmapSize;
                                this.particlePoints[length][i15 + 1] = particle4.y;
                                i15 += 2;
                            }
                            i13 = i15;
                        }
                        if (particle4.y < strokeWidth) {
                            float[] fArr5 = this.particlePoints[length];
                            if (i15 < fArr5.length - 2) {
                                fArr5[i15] = particle4.x;
                                this.particlePoints[length][i15 + 1] = particle4.y + this.bitmapSize;
                                i15 += 2;
                            }
                            i13 = i15;
                        }
                        if (particle4.y > this.bitmapSize - strokeWidth) {
                            float[] fArr6 = this.particlePoints[length];
                            if (i15 < fArr6.length - 2) {
                                fArr6[i15] = particle4.x;
                                this.particlePoints[length][i15 + 1] = particle4.y - this.bitmapSize;
                                i13 = i15 + 2;
                            }
                        }
                        i13 = i15;
                    }
                }
            }
            canvas.drawPoints(this.particlePoints[length], 0, i13, this.particlePaints[length]);
        }
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public View getParentView() {
        return this.mParent;
    }

    public void getRipplePath(Path path) {
        path.addCircle(this.rippleX, this.rippleY, this.rippleMaxRadius * MathUtils.clamp(this.rippleProgress, 0.0f, 1.0f), Path.Direction.CW);
    }

    public float getRippleProgress() {
        return this.rippleProgress;
    }

    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        View view = this.mParent;
        if (view != null) {
            if (view.getParent() != null && this.invalidateParent) {
                view = (View) view.getParent();
            } else if (view instanceof BaseCell) {
                ((BaseCell) view).invalidateLite();
                return;
            }
            view.invalidate();
        }
    }

    @Override
    public void setAlpha(int i) {
        this.mAlpha = i;
        int i2 = 0;
        while (true) {
            float[] fArr = ALPHAS;
            if (i2 >= fArr.length) {
                return;
            }
            this.particlePaints[i2].setAlpha((int) (fArr[i2] * i));
            i2++;
        }
    }

    @Override
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        Iterator it = this.particles.iterator();
        while (it.hasNext()) {
            Particle particle = (Particle) it.next();
            if (!getBounds().contains((int) particle.x, (int) particle.y)) {
                it.remove();
            }
            if (this.particlesPool.size() < this.maxParticles) {
                this.particlesPool.push(particle);
            }
        }
    }

    public void setColor(int i) {
        if (this.lastColor == i) {
            return;
        }
        int i2 = 0;
        while (true) {
            float[] fArr = ALPHAS;
            if (i2 >= fArr.length) {
                this.lastColor = i;
                return;
            } else {
                this.particlePaints[i2].setColor(ColorUtils.setAlphaComponent(i, (int) (this.mAlpha * fArr[i2])));
                i2++;
            }
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        for (Paint paint : this.particlePaints) {
            paint.setColorFilter(colorFilter);
        }
    }

    public void setInvalidateParent(boolean z) {
        this.invalidateParent = z;
    }

    public void setMaxParticlesCount(int i) {
        this.maxParticles = i;
        while (this.particlesPool.size() + this.particles.size() < i) {
            this.particlesPool.push(new Particle());
        }
    }

    public void setOnRippleEndCallback(Runnable runnable) {
        this.onRippleEndCallback = runnable;
    }

    public void setParentView(View view) {
        this.mParent = view;
    }

    public void setRippleInterpolator(TimeInterpolator timeInterpolator) {
        this.rippleInterpolator = timeInterpolator;
    }

    public void setRippleProgress(float f) {
        ValueAnimator valueAnimator;
        this.rippleProgress = f;
        if (f == -1.0f && (valueAnimator = this.rippleAnimator) != null) {
            valueAnimator.cancel();
        }
        this.shouldInvalidateColor = true;
    }

    public void setSize(int i) {
        this.bitmapSize = i;
    }

    public void setSuppressUpdates(boolean z) {
        this.suppressUpdates = z;
        invalidateSelf();
    }

    public void setVisibleBounds(float f, float f2, float f3, float f4) {
        if (this.visibleRect == null) {
            this.visibleRect = new RectF();
        }
        RectF rectF = this.visibleRect;
        if (rectF.left == f && rectF.right == f3 && rectF.top == f2 && rectF.bottom == f4) {
            return;
        }
        rectF.left = f;
        rectF.top = f2;
        rectF.right = f3;
        rectF.bottom = f4;
        invalidateSelf();
    }

    public boolean shouldInvalidateColor() {
        boolean z = this.shouldInvalidateColor;
        this.shouldInvalidateColor = false;
        return z;
    }

    public void startRipple(float f, float f2, float f3) {
        startRipple(f, f2, f3, false);
    }

    public void startRipple(float f, float f2, float f3, boolean z) {
        this.rippleX = f;
        this.rippleY = f2;
        this.rippleMaxRadius = f3;
        this.rippleProgress = z ? 1.0f : 0.0f;
        this.reverseAnimator = z;
        ValueAnimator valueAnimator = this.rippleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final int alpha = this.reverseAnimator ? 255 : this.particlePaints[ALPHAS.length - 1].getAlpha();
        ValueAnimator duration = ValueAnimator.ofFloat(this.rippleProgress, z ? 0.0f : 1.0f).setDuration(MathUtils.clamp(this.rippleMaxRadius * 0.3f, 250.0f, 550.0f));
        this.rippleAnimator = duration;
        duration.setInterpolator(this.rippleInterpolator);
        this.rippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                SpoilerEffect.this.lambda$startRipple$1(alpha, valueAnimator2);
            }
        });
        this.rippleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Iterator it = SpoilerEffect.this.particles.iterator();
                while (it.hasNext()) {
                    Particle particle = (Particle) it.next();
                    if (SpoilerEffect.this.particlesPool.size() < SpoilerEffect.this.maxParticles) {
                        SpoilerEffect.this.particlesPool.push(particle);
                    }
                    it.remove();
                }
                if (SpoilerEffect.this.onRippleEndCallback != null) {
                    SpoilerEffect.this.onRippleEndCallback.run();
                    SpoilerEffect.this.onRippleEndCallback = null;
                }
                SpoilerEffect.this.rippleAnimator = null;
                SpoilerEffect.this.invalidateSelf();
            }
        });
        this.rippleAnimator.start();
        invalidateSelf();
    }

    public void updateMaxParticles() {
        int width = getBounds().width() / AndroidUtilities.dp(6.0f);
        int i = PARTICLES_PER_CHARACTER;
        setMaxParticlesCount(MathUtils.clamp(width * i, i, MAX_PARTICLES_PER_ENTITY));
    }
}
