package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class SimpleTextView extends View implements Drawable.Callback {
    private boolean attachedToWindow;
    private boolean buildFullLayout;
    private boolean canHideRightDrawable;
    private int currentScrollDelay;
    private int drawablePadding;
    private boolean ellipsizeByGradient;
    private boolean ellipsizeByGradientLeft;
    private int ellipsizeByGradientWidthDp;
    private int emojiCacheType;
    private AnimatedEmojiSpan.EmojiGroupedSpans emojiStack;
    private ColorFilter emojiStackColorFilter;
    private Paint fadeEllpsizePaint;
    private int fadeEllpsizePaintWidth;
    private Paint fadePaint;
    private Paint fadePaintBack;
    private Layout firstLineLayout;
    private Boolean forceEllipsizeByGradientLeft;
    private float fullAlpha;
    private Layout fullLayout;
    private int fullLayoutAdditionalWidth;
    private float fullLayoutLeftCharactersOffset;
    private int fullLayoutLeftOffset;
    private int fullTextMaxLines;
    private int gravity;
    private long lastUpdateTime;
    private int lastWidth;
    private Layout layout;
    private float layoutX;
    private float layoutY;
    private Drawable leftDrawable;
    private boolean leftDrawableOutside;
    private int leftDrawableTopPadding;
    private Layout.Alignment mAlignment;
    private int maxLines;
    private boolean maybeClick;
    private int minWidth;
    private int minusWidth;
    private int offsetX;
    private int offsetY;
    private int paddingRight;
    private Layout partLayout;
    private Path path;
    private Drawable replacedDrawable;
    private String replacedText;
    private int replacingDrawableTextIndex;
    private float replacingDrawableTextOffset;
    private Drawable rightDrawable;
    private Drawable rightDrawable2;
    private boolean rightDrawableHidden;
    private boolean rightDrawableInside;
    private View.OnClickListener rightDrawableOnClickListener;
    private boolean rightDrawableOutside;
    private float rightDrawableScale;
    private int rightDrawableTopPadding;
    public int rightDrawableX;
    public int rightDrawableY;
    private boolean scrollNonFitText;
    private float scrollingOffset;
    private List spoilers;
    private Stack spoilersPool;
    private CharSequence text;
    private boolean textDoesNotFit;
    private int textHeight;
    private TextPaint textPaint;
    private int textWidth;
    private int totalWidth;
    private float touchDownX;
    private float touchDownY;
    private boolean usaAlphaForEmoji;
    private boolean wasLayout;
    private boolean widthWrapContent;
    private Drawable wrapBackgroundDrawable;

    public interface PressableDrawable {
        void setPressed(boolean z);
    }

    public SimpleTextView(Context context) {
        super(context);
        this.gravity = 51;
        this.maxLines = 1;
        this.rightDrawableScale = 1.0f;
        this.drawablePadding = AndroidUtilities.dp(4.0f);
        this.ellipsizeByGradientWidthDp = 16;
        this.fullTextMaxLines = 3;
        this.spoilers = new ArrayList();
        this.spoilersPool = new Stack();
        this.path = new Path();
        this.emojiCacheType = 0;
        this.mAlignment = Layout.Alignment.ALIGN_NORMAL;
        this.textPaint = new TextPaint(1);
        setImportantForAccessibility(1);
    }

    private void calcOffset(int r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.SimpleTextView.calcOffset(int):void");
    }

    private void clipOutSpoilers(Canvas canvas) {
        this.path.rewind();
        Iterator it = this.spoilers.iterator();
        while (it.hasNext()) {
            Rect bounds = ((SpoilerEffect) it.next()).getBounds();
            this.path.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
        canvas.clipPath(this.path, Region.Op.DIFFERENCE);
    }

    private void drawLayout(Canvas canvas) {
        if (this.fullAlpha <= 0.0f || this.fullLayoutLeftOffset == 0) {
            canvas.save();
            clipOutSpoilers(canvas);
            AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans = this.emojiStack;
            if (emojiGroupedSpans != null) {
                emojiGroupedSpans.clearPositions();
            }
            this.layout.draw(canvas);
            canvas.restore();
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.layout, this.emojiStack, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f, this.emojiStackColorFilter);
            drawSpoilers(canvas);
            return;
        }
        canvas.save();
        float f = -this.fullLayoutLeftOffset;
        float f2 = this.fullAlpha;
        canvas.translate((f * f2) + (this.fullLayoutLeftCharactersOffset * f2), 0.0f);
        float f3 = this.layoutX;
        float f4 = -this.fullLayoutLeftOffset;
        float f5 = this.fullAlpha;
        this.layoutX = f3 + (f4 * f5) + (this.fullLayoutLeftCharactersOffset * f5);
        canvas.save();
        clipOutSpoilers(canvas);
        AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans2 = this.emojiStack;
        if (emojiGroupedSpans2 != null) {
            emojiGroupedSpans2.clearPositions();
        }
        this.layout.draw(canvas);
        canvas.restore();
        AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.layout, this.emojiStack, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f, this.emojiStackColorFilter);
        drawSpoilers(canvas);
        canvas.restore();
    }

    private void drawSpoilers(Canvas canvas) {
        Iterator it = this.spoilers.iterator();
        while (it.hasNext()) {
            ((SpoilerEffect) it.next()).draw(canvas);
        }
    }

    private Layout.Alignment getAlignment() {
        return this.mAlignment;
    }

    private boolean recreateLayoutMaybe() {
        if (!this.wasLayout || getMeasuredHeight() == 0 || this.buildFullLayout) {
            requestLayout();
            return true;
        }
        boolean createLayout = createLayout(((getMaxTextWidth() - getPaddingLeft()) - getPaddingRight()) - this.minusWidth);
        this.offsetY = (this.gravity & 112) == 16 ? (getMeasuredHeight() - this.textHeight) / 2 : getPaddingTop();
        return createLayout;
    }

    private void updateFadePaints() {
        Paint paint;
        LinearGradient linearGradient;
        if ((this.fadePaint == null || this.fadePaintBack == null) && this.scrollNonFitText) {
            Paint paint2 = new Paint();
            this.fadePaint = paint2;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            paint2.setShader(new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(6.0f), 0.0f, new int[]{-1, 0}, new float[]{0.0f, 1.0f}, tileMode));
            Paint paint3 = this.fadePaint;
            PorterDuff.Mode mode = PorterDuff.Mode.DST_OUT;
            paint3.setXfermode(new PorterDuffXfermode(mode));
            Paint paint4 = new Paint();
            this.fadePaintBack = paint4;
            paint4.setShader(new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(6.0f), 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, tileMode));
            this.fadePaintBack.setXfermode(new PorterDuffXfermode(mode));
        }
        Boolean bool = this.forceEllipsizeByGradientLeft;
        boolean booleanValue = bool != null ? bool.booleanValue() : false;
        if (!(this.fadeEllpsizePaint != null && this.fadeEllpsizePaintWidth == AndroidUtilities.dp(this.ellipsizeByGradientWidthDp) && this.ellipsizeByGradientLeft == booleanValue) && this.ellipsizeByGradient) {
            if (this.fadeEllpsizePaint == null) {
                this.fadeEllpsizePaint = new Paint();
            }
            this.ellipsizeByGradientLeft = booleanValue;
            if (booleanValue) {
                paint = this.fadeEllpsizePaint;
                int dp = AndroidUtilities.dp(this.ellipsizeByGradientWidthDp);
                this.fadeEllpsizePaintWidth = dp;
                linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            } else {
                paint = this.fadeEllpsizePaint;
                int dp2 = AndroidUtilities.dp(this.ellipsizeByGradientWidthDp);
                this.fadeEllpsizePaintWidth = dp2;
                linearGradient = new LinearGradient(0.0f, 0.0f, dp2, 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            }
            paint.setShader(linearGradient);
            this.fadeEllpsizePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }
    }

    private void updateScrollAnimation() {
        if (this.scrollNonFitText) {
            if (this.textDoesNotFit || this.scrollingOffset != 0.0f) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastUpdateTime;
                if (j > 17) {
                    j = 17;
                }
                int i = this.currentScrollDelay;
                if (i > 0) {
                    this.currentScrollDelay = (int) (i - j);
                } else {
                    int dp = this.totalWidth + AndroidUtilities.dp(16.0f);
                    float dp2 = this.scrollingOffset + ((((float) j) / 1000.0f) * AndroidUtilities.dp(this.scrollingOffset < ((float) AndroidUtilities.dp(100.0f)) ? ((this.scrollingOffset / AndroidUtilities.dp(100.0f)) * 20.0f) + 30.0f : this.scrollingOffset >= ((float) (dp - AndroidUtilities.dp(100.0f))) ? 50.0f - (((this.scrollingOffset - (dp - AndroidUtilities.dp(100.0f))) / AndroidUtilities.dp(100.0f)) * 20.0f) : 50.0f));
                    this.scrollingOffset = dp2;
                    this.lastUpdateTime = elapsedRealtime;
                    if (dp2 > dp) {
                        this.scrollingOffset = 0.0f;
                        this.currentScrollDelay = 500;
                    }
                }
                invalidate();
            }
        }
    }

    public void copyScrolling(SimpleTextView simpleTextView) {
        this.scrollingOffset = simpleTextView.scrollingOffset;
    }

    public boolean createLayout(int i) {
        int i2;
        int i3;
        StaticLayout staticLayout;
        int dp;
        int i4;
        int dp2;
        CharSequence charSequence = this.text;
        this.replacingDrawableTextIndex = -1;
        this.rightDrawableHidden = false;
        if (charSequence != null) {
            try {
                Drawable drawable = this.leftDrawable;
                if (drawable == null || this.leftDrawableOutside) {
                    i2 = i;
                } else {
                    drawable.getIntrinsicWidth();
                    i2 = (i - this.leftDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.rightDrawableInside) {
                    i3 = 0;
                } else {
                    if (this.rightDrawable == null || this.rightDrawableOutside) {
                        i3 = 0;
                    } else {
                        i3 = (int) (r3.getIntrinsicWidth() * this.rightDrawableScale);
                        i2 = (i2 - i3) - this.drawablePadding;
                    }
                    if (this.rightDrawable2 != null && !this.rightDrawableOutside) {
                        i3 += (int) (r4.getIntrinsicWidth() * this.rightDrawableScale);
                        i2 = (i2 - i3) - this.drawablePadding;
                    }
                }
                CharSequence charSequence2 = charSequence;
                if (this.replacedText != null) {
                    charSequence2 = charSequence;
                    if (this.replacedDrawable != null) {
                        int indexOf = charSequence.toString().indexOf(this.replacedText);
                        this.replacingDrawableTextIndex = indexOf;
                        if (indexOf >= 0) {
                            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
                            DialogCell.FixedWidthSpan fixedWidthSpan = new DialogCell.FixedWidthSpan(this.replacedDrawable.getIntrinsicWidth());
                            int i5 = this.replacingDrawableTextIndex;
                            valueOf.setSpan(fixedWidthSpan, i5, this.replacedText.length() + i5, 0);
                            charSequence2 = valueOf;
                        } else {
                            i2 = (i2 - this.replacedDrawable.getIntrinsicWidth()) - this.drawablePadding;
                            charSequence2 = charSequence;
                        }
                    }
                }
                if (this.canHideRightDrawable && i3 != 0 && !this.rightDrawableOutside && !charSequence2.equals(TextUtils.ellipsize(charSequence2, this.textPaint, i2, TextUtils.TruncateAt.END))) {
                    this.rightDrawableHidden = true;
                    i2 = i2 + i3 + this.drawablePadding;
                }
                int i6 = i2;
                if (this.buildFullLayout) {
                    CharSequence ellipsize = !this.ellipsizeByGradient ? TextUtils.ellipsize(charSequence2, this.textPaint, i6, TextUtils.TruncateAt.END) : charSequence2;
                    if (this.ellipsizeByGradient || ellipsize.equals(charSequence2)) {
                        int length = ellipsize.length();
                        TextPaint textPaint = this.textPaint;
                        if (!this.scrollNonFitText && !this.ellipsizeByGradient) {
                            dp2 = AndroidUtilities.dp(8.0f) + i6;
                            this.layout = new StaticLayout(ellipsize, 0, length, textPaint, dp2, getAlignment(), 1.0f, 0.0f, false);
                            this.fullLayout = null;
                            this.partLayout = null;
                            this.firstLineLayout = null;
                        }
                        dp2 = AndroidUtilities.dp(2000.0f);
                        this.layout = new StaticLayout(ellipsize, 0, length, textPaint, dp2, getAlignment(), 1.0f, 0.0f, false);
                        this.fullLayout = null;
                        this.partLayout = null;
                        this.firstLineLayout = null;
                    } else {
                        TextPaint textPaint2 = this.textPaint;
                        Layout.Alignment alignment = getAlignment();
                        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
                        StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(charSequence2, textPaint2, i6, alignment, 1.0f, 0.0f, false, truncateAt, i6, this.fullTextMaxLines, false);
                        this.fullLayout = createStaticLayout;
                        if (createStaticLayout != null) {
                            int lineEnd = createStaticLayout.getLineEnd(0);
                            int lineStart = this.fullLayout.getLineStart(1);
                            CharSequence subSequence = charSequence2.subSequence(0, lineEnd);
                            SpannableStringBuilder valueOf2 = SpannableStringBuilder.valueOf(charSequence2);
                            valueOf2.setSpan(new EmptyStubSpan(), 0, lineStart, 0);
                            String subSequence2 = lineEnd < ellipsize.length() ? ellipsize.subSequence(lineEnd, ellipsize.length()) : "…";
                            this.firstLineLayout = new StaticLayout(ellipsize, 0, ellipsize.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + i6, getAlignment(), 1.0f, 0.0f, false);
                            StaticLayout staticLayout2 = new StaticLayout(subSequence, 0, subSequence.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + i6, getAlignment(), 1.0f, 0.0f, false);
                            this.layout = staticLayout2;
                            if (staticLayout2.getLineLeft(0) != 0.0f) {
                                subSequence2 = "\u200f" + ((Object) subSequence2);
                            }
                            CharSequence charSequence3 = subSequence2;
                            this.partLayout = new StaticLayout(charSequence3, 0, charSequence3.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + i6, getAlignment(), 1.0f, 0.0f, false);
                            this.fullLayout = StaticLayoutEx.createStaticLayout(valueOf2, this.textPaint, AndroidUtilities.dp(8.0f) + i6 + this.fullLayoutAdditionalWidth, getAlignment(), 1.0f, 0.0f, false, truncateAt, i6 + this.fullLayoutAdditionalWidth, this.fullTextMaxLines, false);
                        }
                    }
                } else {
                    if (this.maxLines > 1) {
                        staticLayout = StaticLayoutEx.createStaticLayout(charSequence2, this.textPaint, i6, getAlignment(), 1.0f, 0.0f, false, TextUtils.TruncateAt.END, i6, this.maxLines, false);
                    } else {
                        CharSequence charSequence4 = charSequence2;
                        if (!this.scrollNonFitText) {
                            charSequence4 = this.ellipsizeByGradient ? charSequence2 : TextUtils.ellipsize(charSequence2, this.textPaint, i6, TextUtils.TruncateAt.END);
                        }
                        CharSequence charSequence5 = charSequence4;
                        int length2 = charSequence5.length();
                        TextPaint textPaint3 = this.textPaint;
                        if (!this.scrollNonFitText && !this.ellipsizeByGradient) {
                            dp = AndroidUtilities.dp(8.0f) + i6;
                            staticLayout = new StaticLayout(charSequence5, 0, length2, textPaint3, dp, getAlignment(), 1.0f, 0.0f, false);
                        }
                        dp = AndroidUtilities.dp(2000.0f);
                        staticLayout = new StaticLayout(charSequence5, 0, length2, textPaint3, dp, getAlignment(), 1.0f, 0.0f, false);
                    }
                    this.layout = staticLayout;
                }
                this.spoilersPool.addAll(this.spoilers);
                this.spoilers.clear();
                Layout layout = this.layout;
                if (layout == null || !(layout.getText() instanceof Spannable)) {
                    i4 = i6;
                } else {
                    i4 = i6;
                    SpoilerEffect.addSpoilers(this, this.layout, -2, -2, this.spoilersPool, this.spoilers);
                }
                calcOffset(i4);
            } catch (Exception unused) {
            }
        } else {
            this.layout = null;
            this.textWidth = 0;
            this.textHeight = 0;
        }
        AnimatedEmojiSpan.release(this, this.emojiStack);
        if (this.attachedToWindow) {
            this.emojiStack = AnimatedEmojiSpan.update(this.emojiCacheType, this, this.emojiStack, this.layout);
        }
        invalidate();
        return true;
    }

    @Override
    public Drawable getBackground() {
        Drawable drawable = this.wrapBackgroundDrawable;
        return drawable != null ? drawable : super.getBackground();
    }

    public float getFullAlpha() {
        return this.fullAlpha;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public float getLayoutX() {
        return this.layoutX;
    }

    public float getLayoutY() {
        return this.layoutY;
    }

    public Drawable getLeftDrawable() {
        return this.leftDrawable;
    }

    public int getLineCount() {
        Layout layout = this.layout;
        int lineCount = layout != null ? layout.getLineCount() : 0;
        Layout layout2 = this.fullLayout;
        return layout2 != null ? lineCount + layout2.getLineCount() : lineCount;
    }

    public int getMaxTextWidth() {
        Drawable drawable;
        Drawable drawable2;
        int i = 0;
        int measuredWidth = getMeasuredWidth() - ((!this.rightDrawableOutside || (drawable2 = this.rightDrawable) == null) ? 0 : drawable2.getIntrinsicWidth() + this.drawablePadding);
        if (this.rightDrawableOutside && (drawable = this.rightDrawable2) != null) {
            i = this.drawablePadding + drawable.getIntrinsicWidth();
        }
        return measuredWidth - i;
    }

    public TextPaint getPaint() {
        return this.textPaint;
    }

    public Drawable getRightDrawable() {
        return this.rightDrawable;
    }

    public Drawable getRightDrawable2() {
        return this.rightDrawable2;
    }

    public boolean getRightDrawableOutside() {
        return this.rightDrawableOutside;
    }

    public int getRightDrawableWidth() {
        if (this.rightDrawable == null) {
            return 0;
        }
        return (int) (this.drawablePadding + (r0.getIntrinsicWidth() * this.rightDrawableScale));
    }

    public int getRightDrawableX() {
        return this.rightDrawableX;
    }

    public int getRightDrawableY() {
        return this.rightDrawableY;
    }

    public int getSideDrawablesSize() {
        Drawable drawable = this.leftDrawable;
        int intrinsicWidth = drawable != null ? drawable.getIntrinsicWidth() + this.drawablePadding : 0;
        if (this.rightDrawable != null) {
            intrinsicWidth += ((int) (r1.getIntrinsicWidth() * this.rightDrawableScale)) + this.drawablePadding;
        }
        return this.rightDrawable2 != null ? intrinsicWidth + ((int) (r1.getIntrinsicWidth() * this.rightDrawableScale)) + this.drawablePadding : intrinsicWidth;
    }

    public CharSequence getText() {
        CharSequence charSequence = this.text;
        return charSequence == null ? "" : charSequence;
    }

    public int getTextColor() {
        return this.textPaint.getColor();
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public int getTextStartX() {
        int i = 0;
        if (this.layout == null) {
            return 0;
        }
        Drawable drawable = this.leftDrawable;
        if (drawable != null && (this.gravity & 7) == 3) {
            i = this.drawablePadding + drawable.getIntrinsicWidth();
        }
        Drawable drawable2 = this.replacedDrawable;
        if (drawable2 != null && this.replacingDrawableTextIndex < 0 && (this.gravity & 7) == 3) {
            i += this.drawablePadding + drawable2.getIntrinsicWidth();
        }
        return ((int) getX()) + this.offsetX + i;
    }

    public int getTextStartY() {
        if (this.layout == null) {
            return 0;
        }
        return (int) getY();
    }

    public int getTextWidth() {
        int i = this.textWidth;
        if (this.rightDrawableInside) {
            r2 = (this.rightDrawable2 != null ? (int) (r3.getIntrinsicWidth() * this.rightDrawableScale) : 0) + (this.rightDrawable != null ? (int) (r1.getIntrinsicWidth() * this.rightDrawableScale) : 0);
        }
        return i + r2;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable == drawable2 || drawable == (drawable2 = this.rightDrawable) || drawable == (drawable2 = this.rightDrawable2) || drawable == (drawable2 = this.replacedDrawable)) {
            invalidate(drawable2.getBounds());
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        this.emojiStack = AnimatedEmojiSpan.update(this.emojiCacheType, this, this.emojiStack, this.layout);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        AnimatedEmojiSpan.release(this, this.emojiStack);
        this.wasLayout = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int intrinsicWidth;
        float f;
        float f2;
        float measuredHeight;
        Paint paint;
        float f3;
        Paint paint2;
        int i;
        float f4;
        super.onDraw(canvas);
        this.layoutX = 0.0f;
        this.layoutY = 0.0f;
        boolean z = this.scrollNonFitText && (this.textDoesNotFit || this.scrollingOffset != 0.0f);
        int saveLayerAlpha = (z || this.ellipsizeByGradient) ? canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31) : Integer.MIN_VALUE;
        this.totalWidth = this.textWidth;
        Drawable drawable = this.leftDrawable;
        if (drawable == null || this.leftDrawableOutside) {
            intrinsicWidth = (!this.leftDrawableOutside || drawable == null) ? 0 : drawable.getIntrinsicWidth() + this.drawablePadding;
        } else {
            int i2 = (int) (-this.scrollingOffset);
            int i3 = this.gravity;
            if ((i3 & 7) == 1) {
                i2 += this.offsetX;
            }
            int measuredHeight2 = ((i3 & 112) == 16 ? (getMeasuredHeight() - this.leftDrawable.getIntrinsicHeight()) / 2 : getPaddingTop() + ((this.textHeight - this.leftDrawable.getIntrinsicHeight()) / 2)) + this.leftDrawableTopPadding;
            Drawable drawable2 = this.leftDrawable;
            drawable2.setBounds(i2, measuredHeight2, drawable2.getIntrinsicWidth() + i2, this.leftDrawable.getIntrinsicHeight() + measuredHeight2);
            this.leftDrawable.draw(canvas);
            int i4 = this.gravity & 7;
            intrinsicWidth = (i4 == 3 || i4 == 1) ? this.drawablePadding + this.leftDrawable.getIntrinsicWidth() : 0;
            this.totalWidth += this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
        }
        Drawable drawable3 = this.replacedDrawable;
        if (drawable3 != null && this.replacedText != null) {
            int i5 = (int) ((-this.scrollingOffset) + this.replacingDrawableTextOffset);
            int i6 = this.gravity;
            if ((i6 & 7) == 1) {
                i5 += this.offsetX;
            }
            int measuredHeight3 = (i6 & 112) == 16 ? ((getMeasuredHeight() - this.replacedDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding : this.leftDrawableTopPadding + ((this.textHeight - drawable3.getIntrinsicHeight()) / 2);
            Drawable drawable4 = this.replacedDrawable;
            drawable4.setBounds(i5, measuredHeight3, drawable4.getIntrinsicWidth() + i5, this.replacedDrawable.getIntrinsicHeight() + measuredHeight3);
            this.replacedDrawable.draw(canvas);
            if (this.replacingDrawableTextIndex < 0) {
                int i7 = this.gravity & 7;
                if (i7 == 3 || i7 == 1) {
                    intrinsicWidth += this.drawablePadding + this.replacedDrawable.getIntrinsicWidth();
                }
                this.totalWidth += this.drawablePadding + this.replacedDrawable.getIntrinsicWidth();
            }
        }
        int i8 = intrinsicWidth;
        if (this.rightDrawable != null && !this.rightDrawableHidden && this.rightDrawableScale > 0.0f && !this.rightDrawableOutside && !this.rightDrawableInside) {
            int i9 = this.textWidth + i8 + this.drawablePadding + ((int) (-this.scrollingOffset));
            int i10 = this.gravity & 7;
            if (i10 == 1 || i10 == 5) {
                i9 += this.offsetX;
            }
            int intrinsicWidth2 = (int) (r1.getIntrinsicWidth() * this.rightDrawableScale);
            int intrinsicHeight = (int) (this.rightDrawable.getIntrinsicHeight() * this.rightDrawableScale);
            int measuredHeight4 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight) / 2)) + this.rightDrawableTopPadding;
            this.rightDrawable.setBounds(i9, measuredHeight4, i9 + intrinsicWidth2, measuredHeight4 + intrinsicHeight);
            this.rightDrawableX = i9 + (intrinsicWidth2 >> 1);
            this.rightDrawableY = measuredHeight4 + (intrinsicHeight >> 1);
            this.rightDrawable.draw(canvas);
            this.totalWidth += this.drawablePadding + intrinsicWidth2;
        }
        if (this.rightDrawable2 != null && !this.rightDrawableHidden && this.rightDrawableScale > 0.0f && !this.rightDrawableOutside && !this.rightDrawableInside) {
            int i11 = this.textWidth + i8 + this.drawablePadding + ((int) (-this.scrollingOffset));
            if (this.rightDrawable != null) {
                i11 += ((int) (r3.getIntrinsicWidth() * this.rightDrawableScale)) + this.drawablePadding;
            }
            int i12 = this.gravity & 7;
            if (i12 == 1 || i12 == 5) {
                i11 += this.offsetX;
            }
            int intrinsicWidth3 = (int) (this.rightDrawable2.getIntrinsicWidth() * this.rightDrawableScale);
            int intrinsicHeight2 = (int) (this.rightDrawable2.getIntrinsicHeight() * this.rightDrawableScale);
            int measuredHeight5 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight2) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight2) / 2)) + this.rightDrawableTopPadding;
            this.rightDrawable2.setBounds(i11, measuredHeight5, i11 + intrinsicWidth3, intrinsicHeight2 + measuredHeight5);
            this.rightDrawable2.draw(canvas);
            this.totalWidth += this.drawablePadding + intrinsicWidth3;
        }
        int dp = this.totalWidth + AndroidUtilities.dp(16.0f);
        float f5 = this.scrollingOffset;
        if (f5 != 0.0f) {
            if (this.leftDrawable != null && !this.leftDrawableOutside) {
                int i13 = ((int) (-f5)) + dp;
                int measuredHeight6 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - this.leftDrawable.getIntrinsicHeight()) / 2 : getPaddingTop() + ((this.textHeight - this.leftDrawable.getIntrinsicHeight()) / 2)) + this.leftDrawableTopPadding;
                Drawable drawable5 = this.leftDrawable;
                drawable5.setBounds(i13, measuredHeight6, drawable5.getIntrinsicWidth() + i13, this.leftDrawable.getIntrinsicHeight() + measuredHeight6);
                this.leftDrawable.draw(canvas);
            }
            if (this.rightDrawable != null && !this.rightDrawableOutside) {
                int intrinsicWidth4 = (int) (r1.getIntrinsicWidth() * this.rightDrawableScale);
                int intrinsicHeight3 = (int) (this.rightDrawable.getIntrinsicHeight() * this.rightDrawableScale);
                int i14 = this.textWidth + i8 + this.drawablePadding + ((int) (-this.scrollingOffset)) + dp;
                int measuredHeight7 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight3) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight3) / 2)) + this.rightDrawableTopPadding;
                this.rightDrawable.setBounds(i14, measuredHeight7, i14 + intrinsicWidth4, measuredHeight7 + intrinsicHeight3);
                this.rightDrawableX = i14 + (intrinsicWidth4 >> 1);
                this.rightDrawableY = measuredHeight7 + (intrinsicHeight3 >> 1);
                this.rightDrawable.draw(canvas);
            }
            if (this.rightDrawable2 != null && !this.rightDrawableOutside) {
                int intrinsicWidth5 = (int) (r1.getIntrinsicWidth() * this.rightDrawableScale);
                int intrinsicHeight4 = (int) (this.rightDrawable2.getIntrinsicHeight() * this.rightDrawableScale);
                int i15 = this.textWidth + i8 + this.drawablePadding + ((int) (-this.scrollingOffset)) + dp;
                if (this.rightDrawable != null) {
                    i15 += ((int) (r4.getIntrinsicWidth() * this.rightDrawableScale)) + this.drawablePadding;
                }
                int measuredHeight8 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight4) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight4) / 2)) + this.rightDrawableTopPadding;
                this.rightDrawable2.setBounds(i15, measuredHeight8, intrinsicWidth5 + i15, intrinsicHeight4 + measuredHeight8);
                this.rightDrawable2.draw(canvas);
            }
        }
        if (this.layout != null) {
            if (this.leftDrawableOutside || this.rightDrawableOutside || this.ellipsizeByGradient || this.paddingRight > 0) {
                canvas.save();
                int maxTextWidth = getMaxTextWidth() - this.paddingRight;
                Drawable drawable6 = this.rightDrawable;
                canvas.clipRect(i8, 0, maxTextWidth - AndroidUtilities.dp((drawable6 == null || (drawable6 instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) || !this.rightDrawableOutside) ? 0.0f : 2.0f), getMeasuredHeight());
            }
            Emoji.emojiDrawingUseAlpha = this.usaAlphaForEmoji;
            if (this.wrapBackgroundDrawable != null) {
                int i16 = (int) ((this.offsetX + i8) - this.scrollingOffset);
                int i17 = this.textWidth;
                int i18 = i16 + (i17 / 2);
                int max = Math.max(i17 + getPaddingLeft() + getPaddingRight(), this.minWidth);
                int i19 = i18 - (max / 2);
                this.wrapBackgroundDrawable.setBounds(i19, 0, max + i19, getMeasuredHeight());
                this.wrapBackgroundDrawable.draw(canvas);
            }
            if (this.offsetX + i8 != 0 || this.offsetY != 0 || this.scrollingOffset != 0.0f) {
                canvas.save();
                canvas.translate((this.offsetX + i8) - this.scrollingOffset, this.offsetY);
                this.layoutX += (this.offsetX + i8) - this.scrollingOffset;
                this.layoutY += this.offsetY;
            }
            drawLayout(canvas);
            if (this.partLayout != null && this.fullAlpha < 1.0f) {
                int alpha = this.textPaint.getAlpha();
                this.textPaint.setAlpha((int) ((1.0f - this.fullAlpha) * 255.0f));
                canvas.save();
                if (this.partLayout.getText().length() == 1) {
                    f4 = AndroidUtilities.dp(this.fullTextMaxLines == 1 ? 0.5f : 4.0f);
                } else {
                    f4 = 0.0f;
                }
                canvas.translate(this.layout.getLineLeft(0) != 0.0f ? (-this.layout.getLineWidth(0)) + f4 : this.layout.getLineWidth(0) - f4, 0.0f);
                float f6 = -this.fullLayoutLeftOffset;
                float f7 = this.fullAlpha;
                canvas.translate((f6 * f7) + (this.fullLayoutLeftCharactersOffset * f7), 0.0f);
                this.partLayout.draw(canvas);
                canvas.restore();
                this.textPaint.setAlpha(alpha);
            }
            if (this.fullLayout != null && this.fullAlpha > 0.0f) {
                int alpha2 = this.textPaint.getAlpha();
                this.textPaint.setAlpha((int) (this.fullAlpha * 255.0f));
                float f8 = -this.fullLayoutLeftOffset;
                float f9 = this.fullAlpha;
                float f10 = this.fullLayoutLeftCharactersOffset;
                canvas.translate(((f8 * f9) + (f9 * f10)) - f10, 0.0f);
                this.fullLayout.draw(canvas);
                this.textPaint.setAlpha(alpha2);
            }
            if (this.scrollingOffset != 0.0f) {
                canvas.translate(dp, 0.0f);
                drawLayout(canvas);
            }
            if (this.offsetX + i8 != 0 || this.offsetY != 0 || this.scrollingOffset != 0.0f) {
                canvas.restore();
            }
            if (this.rightDrawable != null && !this.rightDrawableHidden && this.rightDrawableScale > 0.0f && !this.rightDrawableOutside && this.rightDrawableInside) {
                int i20 = this.textWidth + i8 + this.drawablePadding + ((int) (-this.scrollingOffset));
                int i21 = this.gravity & 7;
                if (i21 == 1 || i21 == 5) {
                    i20 += this.offsetX;
                }
                int intrinsicWidth6 = (int) (r1.getIntrinsicWidth() * this.rightDrawableScale);
                int intrinsicHeight5 = (int) (this.rightDrawable.getIntrinsicHeight() * this.rightDrawableScale);
                int measuredHeight9 = (this.gravity & 112) == 16 ? ((getMeasuredHeight() - intrinsicHeight5) / 2) + this.rightDrawableTopPadding : this.rightDrawableTopPadding + getPaddingTop() + ((this.textHeight - intrinsicHeight5) / 2);
                this.rightDrawable.setBounds(i20, measuredHeight9, i20 + intrinsicWidth6, measuredHeight9 + intrinsicHeight5);
                this.rightDrawableX = i20 + (intrinsicWidth6 >> 1);
                this.rightDrawableY = measuredHeight9 + (intrinsicHeight5 >> 1);
                this.rightDrawable.draw(canvas);
                this.totalWidth += this.drawablePadding + intrinsicWidth6;
            }
            if (this.rightDrawable2 != null && !this.rightDrawableHidden && this.rightDrawableScale > 0.0f && !this.rightDrawableOutside && this.rightDrawableInside) {
                int i22 = this.textWidth + i8 + this.drawablePadding + ((int) (-this.scrollingOffset));
                if (this.rightDrawable != null) {
                    i22 += ((int) (r2.getIntrinsicWidth() * this.rightDrawableScale)) + this.drawablePadding;
                }
                int i23 = this.gravity & 7;
                if (i23 == 1 || i23 == 5) {
                    i22 += this.offsetX;
                }
                int intrinsicWidth7 = (int) (this.rightDrawable2.getIntrinsicWidth() * this.rightDrawableScale);
                int intrinsicHeight6 = (int) (this.rightDrawable2.getIntrinsicHeight() * this.rightDrawableScale);
                int measuredHeight10 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight6) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight6) / 2)) + this.rightDrawableTopPadding;
                this.rightDrawable2.setBounds(i22, measuredHeight10, i22 + intrinsicWidth7, intrinsicHeight6 + measuredHeight10);
                this.rightDrawable2.draw(canvas);
                this.totalWidth += this.drawablePadding + intrinsicWidth7;
            }
            if (z) {
                if (this.scrollingOffset < AndroidUtilities.dp(10.0f)) {
                    paint2 = this.fadePaint;
                    i = (int) ((this.scrollingOffset / AndroidUtilities.dp(10.0f)) * 255.0f);
                } else if (this.scrollingOffset > (this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)) {
                    this.fadePaint.setAlpha((int) ((1.0f - ((this.scrollingOffset - ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f))) / AndroidUtilities.dp(10.0f))) * 255.0f));
                    f3 = 0.0f;
                    canvas.drawRect(i8, 0.0f, AndroidUtilities.dp(6.0f) + i8, getMeasuredHeight(), this.fadePaint);
                    canvas.save();
                    canvas.translate((getMaxTextWidth() - this.paddingRight) - AndroidUtilities.dp(6.0f), 0.0f);
                    f2 = AndroidUtilities.dp(6.0f);
                    measuredHeight = getMeasuredHeight();
                    paint = this.fadePaintBack;
                    f = 0.0f;
                } else {
                    paint2 = this.fadePaint;
                    i = 255;
                }
                paint2.setAlpha(i);
                f3 = 0.0f;
                canvas.drawRect(i8, 0.0f, AndroidUtilities.dp(6.0f) + i8, getMeasuredHeight(), this.fadePaint);
                canvas.save();
                canvas.translate((getMaxTextWidth() - this.paddingRight) - AndroidUtilities.dp(6.0f), 0.0f);
                f2 = AndroidUtilities.dp(6.0f);
                measuredHeight = getMeasuredHeight();
                paint = this.fadePaintBack;
                f = 0.0f;
            } else {
                if (this.ellipsizeByGradient && this.textDoesNotFit && this.fadeEllpsizePaint != null) {
                    canvas.save();
                    updateFadePaints();
                    if (!this.ellipsizeByGradientLeft) {
                        int maxTextWidth2 = (getMaxTextWidth() - this.paddingRight) - this.fadeEllpsizePaintWidth;
                        Drawable drawable7 = this.rightDrawable;
                        canvas.translate(maxTextWidth2 - AndroidUtilities.dp((drawable7 == null || (drawable7 instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) || !this.rightDrawableOutside) ? 0.0f : 2.0f), 0.0f);
                    }
                    f = i8;
                    f2 = this.fadeEllpsizePaintWidth;
                    measuredHeight = getMeasuredHeight();
                    paint = this.fadeEllpsizePaint;
                    f3 = 0.0f;
                }
                updateScrollAnimation();
                Emoji.emojiDrawingUseAlpha = true;
                if (!this.leftDrawableOutside || this.rightDrawableOutside || this.ellipsizeByGradient || this.paddingRight > 0) {
                    canvas.restore();
                }
            }
            canvas.drawRect(f, f3, f2, measuredHeight, paint);
            canvas.restore();
            updateScrollAnimation();
            Emoji.emojiDrawingUseAlpha = true;
            if (!this.leftDrawableOutside) {
            }
            canvas.restore();
        }
        if (z || this.ellipsizeByGradient) {
            canvas.restoreToCount(saveLayerAlpha);
        }
        Drawable drawable8 = this.leftDrawable;
        if (drawable8 != null && this.leftDrawableOutside) {
            int intrinsicWidth8 = drawable8.getIntrinsicWidth();
            int intrinsicHeight7 = this.leftDrawable.getIntrinsicHeight();
            int measuredHeight11 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight7) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight7) / 2)) + this.leftDrawableTopPadding;
            this.leftDrawable.setBounds(0, measuredHeight11, intrinsicWidth8, intrinsicHeight7 + measuredHeight11);
            this.leftDrawable.draw(canvas);
        }
        if (this.rightDrawable != null && this.rightDrawableOutside) {
            int i24 = this.textWidth + i8 + this.drawablePadding;
            float f11 = this.scrollingOffset;
            int min = Math.min(i24 + (f11 == 0.0f ? -dp : (int) (-f11)) + dp, (getMaxTextWidth() - this.paddingRight) + this.drawablePadding);
            int intrinsicWidth9 = (int) (this.rightDrawable.getIntrinsicWidth() * this.rightDrawableScale);
            int intrinsicHeight8 = (int) (this.rightDrawable.getIntrinsicHeight() * this.rightDrawableScale);
            int measuredHeight12 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight8) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight8) / 2)) + this.rightDrawableTopPadding;
            this.rightDrawable.setBounds(min, measuredHeight12, min + intrinsicWidth9, measuredHeight12 + intrinsicHeight8);
            this.rightDrawableX = min + (intrinsicWidth9 >> 1);
            this.rightDrawableY = measuredHeight12 + (intrinsicHeight8 >> 1);
            this.rightDrawable.draw(canvas);
        }
        if (this.rightDrawable2 == null || !this.rightDrawableOutside) {
            return;
        }
        int i25 = i8 + this.textWidth + this.drawablePadding;
        float f12 = this.scrollingOffset;
        int min2 = Math.min(i25 + (f12 == 0.0f ? -dp : (int) (-f12)) + dp, (getMaxTextWidth() - this.paddingRight) + this.drawablePadding);
        if (this.rightDrawable != null) {
            min2 += ((int) (r2.getIntrinsicWidth() * this.rightDrawableScale)) + this.drawablePadding;
        }
        int intrinsicWidth10 = (int) (this.rightDrawable2.getIntrinsicWidth() * this.rightDrawableScale);
        int intrinsicHeight9 = (int) (this.rightDrawable2.getIntrinsicHeight() * this.rightDrawableScale);
        int measuredHeight13 = ((this.gravity & 112) == 16 ? (getMeasuredHeight() - intrinsicHeight9) / 2 : getPaddingTop() + ((this.textHeight - intrinsicHeight9) / 2)) + this.rightDrawableTopPadding;
        this.rightDrawable2.setBounds(min2, measuredHeight13, intrinsicWidth10 + min2, intrinsicHeight9 + measuredHeight13);
        this.rightDrawable2.draw(canvas);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setVisibleToUser(true);
        accessibilityNodeInfo.setClassName("android.widget.TextView");
        accessibilityNodeInfo.setText(this.text);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.wasLayout = true;
    }

    @Override
    public void onMeasure(int i, int i2) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        Drawable drawable4;
        Drawable drawable5;
        Drawable drawable6;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int i3 = this.lastWidth;
        int i4 = AndroidUtilities.displaySize.x;
        if (i3 != i4) {
            this.lastWidth = i4;
            this.scrollingOffset = 0.0f;
            this.currentScrollDelay = 500;
        }
        int i5 = 0;
        createLayout((((((size - getPaddingLeft()) - getPaddingRight()) - this.minusWidth) - ((!this.leftDrawableOutside || (drawable6 = this.leftDrawable) == null) ? 0 : drawable6.getIntrinsicWidth() + this.drawablePadding)) - ((!this.rightDrawableOutside || (drawable5 = this.rightDrawable) == null) ? 0 : drawable5.getIntrinsicWidth() + this.drawablePadding)) - ((!this.rightDrawableOutside || (drawable4 = this.rightDrawable2) == null) ? 0 : drawable4.getIntrinsicWidth() + this.drawablePadding));
        if (View.MeasureSpec.getMode(i2) != 1073741824) {
            size2 = getPaddingBottom() + getPaddingTop() + this.textHeight;
        }
        if (this.widthWrapContent) {
            int paddingLeft = getPaddingLeft() + this.textWidth + getPaddingRight() + this.minusWidth + ((!this.leftDrawableOutside || (drawable3 = this.leftDrawable) == null) ? 0 : drawable3.getIntrinsicWidth() + this.drawablePadding) + ((!this.rightDrawableOutside || (drawable2 = this.rightDrawable) == null) ? 0 : drawable2.getIntrinsicWidth() + this.drawablePadding);
            if (this.rightDrawableOutside && (drawable = this.rightDrawable2) != null) {
                i5 = drawable.getIntrinsicWidth() + this.drawablePadding;
            }
            size = Math.min(size, paddingLeft + i5);
        }
        setMeasuredDimension(size, size2);
        this.offsetY = (this.gravity & 112) == 16 ? getPaddingTop() + ((((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - this.textHeight) / 2) : getPaddingTop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.rightDrawableOnClickListener != null && this.rightDrawable != null) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.rightDrawableX - AndroidUtilities.dp(16.0f), this.rightDrawableY - AndroidUtilities.dp(16.0f), this.rightDrawableX + AndroidUtilities.dp(16.0f), this.rightDrawableY + AndroidUtilities.dp(16.0f));
            if (motionEvent.getAction() == 0 && rectF.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                this.maybeClick = true;
                this.touchDownX = motionEvent.getX();
                this.touchDownY = motionEvent.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                Object obj = this.rightDrawable;
                if (obj instanceof PressableDrawable) {
                    ((PressableDrawable) obj).setPressed(true);
                }
            } else if (motionEvent.getAction() == 2 && this.maybeClick) {
                if (Math.abs(motionEvent.getX() - this.touchDownX) >= AndroidUtilities.touchSlop || Math.abs(motionEvent.getY() - this.touchDownY) >= AndroidUtilities.touchSlop) {
                    this.maybeClick = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    Object obj2 = this.rightDrawable;
                    if (obj2 instanceof PressableDrawable) {
                        ((PressableDrawable) obj2).setPressed(false);
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (this.maybeClick && motionEvent.getAction() == 1) {
                    this.rightDrawableOnClickListener.onClick(this);
                    Object obj3 = this.rightDrawable;
                    if (obj3 instanceof PressableDrawable) {
                        ((PressableDrawable) obj3).setPressed(false);
                    }
                }
                this.maybeClick = false;
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.onTouchEvent(motionEvent) || this.maybeClick;
    }

    public void replaceTextWithDrawable(Drawable drawable, String str) {
        Drawable drawable2 = this.replacedDrawable;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.replacedDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
        this.replacedText = str;
    }

    public void resetScrolling() {
        this.scrollingOffset = 0.0f;
    }

    public void setAlignment(Layout.Alignment alignment) {
        this.mAlignment = alignment;
        requestLayout();
    }

    @Override
    public void setBackgroundDrawable(Drawable drawable) {
        if (this.maxLines > 1) {
            super.setBackgroundDrawable(drawable);
        } else {
            this.wrapBackgroundDrawable = drawable;
        }
    }

    public void setBuildFullLayout(boolean z) {
        this.buildFullLayout = z;
    }

    public void setCanHideRightDrawable(boolean z) {
        this.canHideRightDrawable = z;
    }

    public void setDrawablePadding(int i) {
        if (this.drawablePadding == i) {
            return;
        }
        this.drawablePadding = i;
        if (recreateLayoutMaybe()) {
            return;
        }
        invalidate();
    }

    public void setEllipsizeByGradient(int i) {
        setEllipsizeByGradient(i, (Boolean) null);
    }

    public void setEllipsizeByGradient(int i, Boolean bool) {
        setEllipsizeByGradient(true, bool);
        this.ellipsizeByGradientWidthDp = i;
        updateFadePaints();
    }

    public void setEllipsizeByGradient(boolean z) {
        setEllipsizeByGradient(z, (Boolean) null);
    }

    public void setEllipsizeByGradient(boolean z, Boolean bool) {
        if (this.scrollNonFitText == z) {
            return;
        }
        this.ellipsizeByGradient = z;
        this.forceEllipsizeByGradientLeft = bool;
        updateFadePaints();
    }

    public void setEmojiCacheType(int i) {
        if (i != this.emojiCacheType) {
            AnimatedEmojiSpan.release(this, this.emojiStack);
            this.emojiCacheType = i;
            if (this.attachedToWindow) {
                this.emojiStack = AnimatedEmojiSpan.update(i, this, this.emojiStack, this.layout);
            }
        }
    }

    public void setEmojiColor(int i) {
        this.emojiStackColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN);
    }

    public void setFullAlpha(float f) {
        this.fullAlpha = f;
        invalidate();
    }

    public void setFullLayoutAdditionalWidth(int i, int i2) {
        if (this.fullLayoutAdditionalWidth == i && this.fullLayoutLeftOffset == i2) {
            return;
        }
        this.fullLayoutAdditionalWidth = i;
        this.fullLayoutLeftOffset = i2;
        createLayout(((getMaxTextWidth() - getPaddingLeft()) - getPaddingRight()) - this.minusWidth);
    }

    public void setFullTextMaxLines(int i) {
        this.fullTextMaxLines = i;
    }

    public void setGravity(int i) {
        this.gravity = i;
    }

    public void setLeftDrawable(int i) {
        setLeftDrawable(i == 0 ? null : getContext().getResources().getDrawable(i));
    }

    public void setLeftDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.leftDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (recreateLayoutMaybe()) {
            return;
        }
        invalidate();
    }

    public void setLeftDrawableOutside(boolean z) {
        this.leftDrawableOutside = z;
    }

    public void setLeftDrawableTopPadding(int i) {
        this.leftDrawableTopPadding = i;
    }

    public void setLinkTextColor(int i) {
        this.textPaint.linkColor = i;
        invalidate();
    }

    public void setMaxLines(int i) {
        this.maxLines = i;
    }

    public void setMinWidth(int i) {
        this.minWidth = i;
    }

    public void setMinusWidth(int i) {
        if (i == this.minusWidth) {
            return;
        }
        this.minusWidth = i;
        if (recreateLayoutMaybe()) {
            return;
        }
        invalidate();
    }

    public void setRightDrawable(int i) {
        setRightDrawable(i == 0 ? null : getContext().getResources().getDrawable(i));
    }

    public void setRightDrawable(Drawable drawable) {
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.rightDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (recreateLayoutMaybe()) {
            return;
        }
        invalidate();
    }

    public void setRightDrawable2(Drawable drawable) {
        Drawable drawable2 = this.rightDrawable2;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.rightDrawable2 = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (recreateLayoutMaybe()) {
            return;
        }
        invalidate();
    }

    public void setRightDrawableInside(boolean z) {
        this.rightDrawableInside = z;
    }

    public void setRightDrawableOnClick(View.OnClickListener onClickListener) {
        this.rightDrawableOnClickListener = onClickListener;
    }

    public void setRightDrawableOutside(boolean z) {
        this.rightDrawableOutside = z;
    }

    public void setRightDrawableScale(float f) {
        this.rightDrawableScale = f;
    }

    public void setRightDrawableTopPadding(int i) {
        this.rightDrawableTopPadding = i;
    }

    public void setRightPadding(int i) {
        if (this.paddingRight != i) {
            this.paddingRight = i;
            int maxTextWidth = ((getMaxTextWidth() - getPaddingLeft()) - getPaddingRight()) - this.minusWidth;
            Drawable drawable = this.leftDrawable;
            if (drawable != null && !this.leftDrawableOutside) {
                maxTextWidth = (maxTextWidth - drawable.getIntrinsicWidth()) - this.drawablePadding;
            }
            int i2 = 0;
            if (!this.rightDrawableInside) {
                if (this.rightDrawable != null && !this.rightDrawableOutside) {
                    i2 = (int) (r0.getIntrinsicWidth() * this.rightDrawableScale);
                    maxTextWidth = (maxTextWidth - i2) - this.drawablePadding;
                }
                if (this.rightDrawable2 != null && !this.rightDrawableOutside) {
                    i2 = (int) (r0.getIntrinsicWidth() * this.rightDrawableScale);
                    maxTextWidth = (maxTextWidth - i2) - this.drawablePadding;
                }
            }
            if (this.replacedText != null && this.replacedDrawable != null) {
                int indexOf = this.text.toString().indexOf(this.replacedText);
                this.replacingDrawableTextIndex = indexOf;
                if (indexOf < 0) {
                    maxTextWidth = (maxTextWidth - this.replacedDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
            }
            if (this.canHideRightDrawable && i2 != 0 && !this.rightDrawableOutside) {
                if (!this.text.equals(TextUtils.ellipsize(this.text, this.textPaint, maxTextWidth, TextUtils.TruncateAt.END))) {
                    this.rightDrawableHidden = true;
                    maxTextWidth = maxTextWidth + i2 + this.drawablePadding;
                }
            }
            calcOffset(maxTextWidth);
            invalidate();
        }
    }

    public void setScrollNonFitText(boolean z) {
        if (this.scrollNonFitText == z) {
            return;
        }
        this.scrollNonFitText = z;
        updateFadePaints();
        requestLayout();
    }

    public void setSideDrawablesColor(int i) {
        Theme.setDrawableColor(this.rightDrawable, i);
        Theme.setDrawableColor(this.leftDrawable, i);
    }

    public boolean setText(CharSequence charSequence) {
        return setText(charSequence, false);
    }

    public boolean setText(CharSequence charSequence, boolean z) {
        CharSequence charSequence2 = this.text;
        if (charSequence2 == null && charSequence == null) {
            return false;
        }
        if (!z && charSequence2 != null && charSequence2.equals(charSequence)) {
            return false;
        }
        this.text = charSequence;
        this.currentScrollDelay = 500;
        recreateLayoutMaybe();
        return true;
    }

    public void setTextColor(int i) {
        this.textPaint.setColor(i);
        invalidate();
    }

    public void setTextSize(int i) {
        float dp = AndroidUtilities.dp(i);
        if (dp == this.textPaint.getTextSize()) {
            return;
        }
        this.textPaint.setTextSize(dp);
        if (recreateLayoutMaybe()) {
            return;
        }
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public void setWidthWrapContent(boolean z) {
        this.widthWrapContent = z;
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.rightDrawable || drawable == this.rightDrawable2 || drawable == this.leftDrawable || super.verifyDrawable(drawable);
    }
}
