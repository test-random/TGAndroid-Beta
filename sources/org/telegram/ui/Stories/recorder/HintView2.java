package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;

public class HintView2 extends View {
    private float arrowHalfWidth;
    private float arrowHeight;
    private float arrowX;
    private float arrowY;
    protected final Paint backgroundPaint;
    private float blurAlpha;
    private Paint blurBackgroundPaint;
    private int blurBitmapHeight;
    private Matrix blurBitmapMatrix;
    private BitmapShader blurBitmapShader;
    private int blurBitmapWidth;
    private Paint blurCutPaint;
    private int[] blurPos;
    private float blurScale;
    private final ButtonBounce bounce;
    private ValueAnimator bounceAnimator;
    private float bounceT;
    private float bounceX;
    private float bounceY;
    private final RectF bounds;
    private final Rect boundsWithArrow;
    private boolean closeButton;
    private Drawable closeButtonDrawable;
    private float closeButtonMargin;
    private Paint cutSelectorPaint;
    private int direction;
    private boolean drawingMyBlur;
    private long duration;
    private AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans;
    private boolean firstDraw;
    private boolean flicker;
    private final RectF flickerBounds;
    private Paint flickerFillPaint;
    private LinearGradient flickerGradient;
    private Matrix flickerGradientMatrix;
    private long flickerStart;
    private LinearGradient flickerStrokeGradient;
    private Paint flickerStrokePaint;
    private Path flickerStrokePath;
    private float flickerStrokePathExtrude;
    private boolean hideByTouch;
    private final Runnable hideRunnable;
    private Drawable icon;
    private int iconHeight;
    private boolean iconLeft;
    private int iconMargin;
    private float iconTx;
    private float iconTy;
    private int iconWidth;
    private final RectF innerPadding;
    private float joint;
    private float jointTranslate;
    private LinkSpanDrawable.LinkCollector links;
    private boolean multiline;
    private Runnable onHidden;
    private LinkSpanDrawable.LinksTextView.OnLinkPress onLongPressListener;
    private LinkSpanDrawable.LinksTextView.OnLinkPress onPressListener;
    private final RectF oval;
    protected final Path path;
    private float pathLastHeight;
    private float pathLastWidth;
    private boolean pathSet;
    private LinkSpanDrawable pressedLink;
    private boolean repeatedBounce;
    private boolean roundWithCornerEffect;
    protected float rounding;
    private Drawable selectorDrawable;
    private AnimatedFloat show;
    private boolean shown;
    private AnimatedTextView.AnimatedTextDrawable textDrawable;
    private StaticLayout textLayout;
    private Layout.Alignment textLayoutAlignment;
    private float textLayoutHeight;
    private float textLayoutLeft;
    private float textLayoutWidth;
    private int textMaxWidth;
    private final TextPaint textPaint;
    private CharSequence textToSet;
    private float textX;
    private float textY;
    private boolean useAlpha;
    private boolean useBlur;
    private boolean useScale;
    private boolean useTranslate;

    public HintView2(Context context) {
        this(context, 0);
    }

    public HintView2(Context context, int i) {
        super(context);
        this.joint = 0.5f;
        this.jointTranslate = 0.0f;
        this.duration = 3500L;
        this.useScale = true;
        this.useTranslate = true;
        this.useAlpha = true;
        this.useBlur = false;
        this.textMaxWidth = -1;
        this.roundWithCornerEffect = true;
        this.rounding = AndroidUtilities.dp(8.0f);
        this.innerPadding = new RectF(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(7.0f));
        this.closeButtonMargin = AndroidUtilities.dp(2.0f);
        this.arrowHalfWidth = AndroidUtilities.dp(7.0f);
        this.arrowHeight = AndroidUtilities.dp(6.0f);
        Paint paint = new Paint(1);
        this.backgroundPaint = paint;
        this.blurScale = 12.0f;
        this.blurAlpha = 0.25f;
        this.textPaint = new TextPaint(1);
        this.textLayoutAlignment = Layout.Alignment.ALIGN_NORMAL;
        this.links = new LinkSpanDrawable.LinkCollector();
        this.hideByTouch = true;
        this.repeatedBounce = true;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.show = new AnimatedFloat(this, 350L, cubicBezierInterpolator);
        this.iconMargin = AndroidUtilities.dp(2.0f);
        this.hideRunnable = new Runnable() {
            @Override
            public final void run() {
                HintView2.this.hide();
            }
        };
        this.bounceT = 1.0f;
        this.bounce = new ButtonBounce(this, 2.0f, 5.0f);
        this.boundsWithArrow = new Rect();
        this.bounds = new RectF();
        this.flickerBounds = new RectF();
        this.path = new Path();
        this.firstDraw = true;
        this.oval = new RectF();
        this.direction = i;
        paint.setColor(-433575896);
        paint.setPathEffect(new CornerPathEffect(this.rounding));
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, false);
        this.textDrawable = animatedTextDrawable;
        animatedTextDrawable.setAnimationProperties(0.4f, 0L, 320L, cubicBezierInterpolator);
        this.textDrawable.setCallback(this);
        setTextSize(14.0f);
        setTextColor(-1);
    }

    private void bounceShow() {
        if (this.repeatedBounce) {
            ValueAnimator valueAnimator = this.bounceAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.bounceAnimator = null;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.bounceAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    HintView2.this.lambda$bounceShow$0(valueAnimator2);
                }
            });
            this.bounceAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    HintView2.this.bounceT = 1.0f;
                    HintView2.this.invalidate();
                }
            });
            this.bounceAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_BACK);
            this.bounceAnimator.setDuration(300L);
            this.bounceAnimator.start();
        }
    }

    private boolean checkTouchLinks(MotionEvent motionEvent) {
        if (this.textLayout != null) {
            final ClickableSpan hitLink = hitLink((int) motionEvent.getX(), (int) motionEvent.getY());
            if (hitLink != null && motionEvent.getAction() == 0) {
                final LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(hitLink, null, motionEvent.getX(), motionEvent.getY());
                this.pressedLink = linkSpanDrawable;
                this.links.addLink(linkSpanDrawable);
                SpannableString spannableString = new SpannableString(this.textLayout.getText());
                int spanStart = spannableString.getSpanStart(this.pressedLink.getSpan());
                int spanEnd = spannableString.getSpanEnd(this.pressedLink.getSpan());
                LinkPath obtainNewPath = this.pressedLink.obtainNewPath();
                obtainNewPath.setCurrentLayout(this.textLayout, spanStart, 0.0f);
                this.textLayout.getSelectionPath(spanStart, spanEnd, obtainNewPath);
                invalidate();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        HintView2.this.lambda$checkTouchLinks$1(linkSpanDrawable, hitLink);
                    }
                }, ViewConfiguration.getLongPressTimeout());
                pause();
                return true;
            }
            if (motionEvent.getAction() == 1) {
                this.links.clear();
                invalidate();
                unpause();
                LinkSpanDrawable linkSpanDrawable2 = this.pressedLink;
                if (linkSpanDrawable2 != null && linkSpanDrawable2.getSpan() == hitLink) {
                    LinkSpanDrawable.LinksTextView.OnLinkPress onLinkPress = this.onPressListener;
                    if (onLinkPress != null) {
                        onLinkPress.run((ClickableSpan) this.pressedLink.getSpan());
                    } else if (this.pressedLink.getSpan() != null) {
                        ((ClickableSpan) this.pressedLink.getSpan()).onClick(this);
                    }
                    this.pressedLink = null;
                    return true;
                }
                this.pressedLink = null;
            }
            if (motionEvent.getAction() == 3) {
                this.links.clear();
                invalidate();
                unpause();
                this.pressedLink = null;
            }
        }
        return this.pressedLink != null;
    }

    private boolean checkTouchTap(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (motionEvent.getAction() == 0 && containsTouch(motionEvent, 0.0f, 0.0f)) {
            this.bounceX = x;
            this.bounceY = y;
            this.bounce.setPressed(true);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null && Build.VERSION.SDK_INT >= 21) {
                drawable.setHotspot(x, y);
                this.selectorDrawable.setState(new int[]{16842919, 16842910});
            }
            return true;
        }
        if (motionEvent.getAction() != 1) {
            if (motionEvent.getAction() != 3) {
                return false;
            }
            this.bounce.setPressed(false);
            Drawable drawable2 = this.selectorDrawable;
            if (drawable2 != null) {
                drawable2.setState(new int[0]);
            }
            return true;
        }
        if (hasOnClickListeners()) {
            performClick();
        } else if (this.hideByTouch) {
            hide();
        }
        this.bounce.setPressed(false);
        Drawable drawable3 = this.selectorDrawable;
        if (drawable3 != null) {
            drawable3.setState(new int[0]);
        }
        return true;
    }

    public static int cutInFancyHalf(CharSequence charSequence, TextPaint textPaint) {
        int length = charSequence.length() / 2;
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = Float.MAX_VALUE;
        int i = 0;
        int i2 = -1;
        while (i < 10) {
            while (length > 0 && length < charSequence.length() && charSequence.charAt(length) != ' ') {
                length += i2;
            }
            f2 = measureCorrectly(charSequence.subSequence(0, length), textPaint);
            f3 = measureCorrectly(AndroidUtilities.getTrimmedString(charSequence.subSequence(length, charSequence.length())), textPaint);
            if (f2 != f || f3 != f4) {
                if (f2 < f3) {
                    length++;
                    i2 = 1;
                } else {
                    length--;
                    i2 = -1;
                }
                if (length <= 0 || length >= charSequence.length()) {
                    break;
                }
                i++;
                f = f2;
                f4 = f3;
            } else {
                break;
            }
        }
        return (int) Math.ceil(Math.max(f2, f3));
    }

    private void fillPath(Path path, float f, float f2, float f3, RectF rectF, Rect rect) {
        float f4;
        float f5 = f / 2.0f;
        float f6 = f2 / 2.0f;
        float min = Math.min(this.rounding, Math.min(f5, f6));
        int i = this.direction;
        if (i == 1 || i == 3) {
            float clamp = Utilities.clamp((this.roundWithCornerEffect ? AndroidUtilities.lerp(getPaddingLeft(), getMeasuredWidth() - getPaddingRight(), this.joint) : AndroidUtilities.lerp(getPaddingLeft() + min + this.arrowHalfWidth, ((getMeasuredWidth() - getPaddingRight()) - min) - this.arrowHalfWidth, this.joint)) + this.jointTranslate, getMeasuredWidth() - getPaddingRight(), getPaddingLeft());
            float min2 = Math.min(Math.max(getPaddingLeft(), clamp - f5) + f, getMeasuredWidth() - getPaddingRight());
            float f7 = min2 - f;
            float f8 = this.arrowHalfWidth;
            float clamp2 = Utilities.clamp(clamp, (min2 - min) - f8, f7 + min + f8);
            if (this.direction == 1) {
                rectF.set(f7, getPaddingTop() + this.arrowHeight, min2, getPaddingTop() + this.arrowHeight + f2);
            } else {
                rectF.set(f7, ((getMeasuredHeight() - this.arrowHeight) - getPaddingBottom()) - f2, min2, (getMeasuredHeight() - this.arrowHeight) - getPaddingBottom());
            }
            f4 = clamp2;
        } else {
            float clamp3 = Utilities.clamp((this.roundWithCornerEffect ? AndroidUtilities.lerp(getPaddingTop(), getMeasuredHeight() - getPaddingBottom(), this.joint) : AndroidUtilities.lerp(getPaddingTop() + min + this.arrowHalfWidth, ((getMeasuredHeight() - getPaddingBottom()) - min) - this.arrowHalfWidth, this.joint)) + this.jointTranslate, getMeasuredHeight() - getPaddingBottom(), getPaddingTop());
            float min3 = Math.min(Math.max(getPaddingTop(), clamp3 - f6) + f2, getMeasuredHeight() - getPaddingBottom());
            float f9 = min3 - f2;
            float f10 = this.arrowHalfWidth;
            f4 = Utilities.clamp(clamp3, (min3 - min) - f10, f9 + min + f10);
            if (this.direction == 0) {
                rectF.set(getPaddingLeft() + this.arrowHeight, f9, getPaddingLeft() + this.arrowHeight + f, min3);
            } else {
                rectF.set(((getMeasuredWidth() - getPaddingRight()) - this.arrowHeight) - f, f9, (getMeasuredWidth() - getPaddingRight()) - this.arrowHeight, min3);
            }
        }
        float f11 = -f3;
        rectF.inset(f11, f11);
        if (rect != null) {
            rect.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        }
        path.rewind();
        if (this.roundWithCornerEffect) {
            path.moveTo(rectF.left, rectF.bottom);
        } else {
            RectF rectF2 = this.oval;
            float f12 = rectF.left;
            float f13 = rectF.bottom;
            float f14 = min * 2.0f;
            rectF2.set(f12, f13 - f14, f14 + f12, f13);
            path.arcTo(this.oval, 90.0f, 90.0f);
        }
        if (this.direction == 0) {
            path.lineTo(rectF.left, this.arrowHalfWidth + f4 + AndroidUtilities.dp(2.0f));
            path.lineTo(rectF.left, this.arrowHalfWidth + f4);
            path.lineTo(rectF.left - this.arrowHeight, AndroidUtilities.dp(1.0f) + f4);
            float f15 = rectF.left - this.arrowHeight;
            this.arrowX = f15;
            this.arrowY = f4;
            path.lineTo(f15, f4 - AndroidUtilities.dp(1.0f));
            path.lineTo(rectF.left, f4 - this.arrowHalfWidth);
            path.lineTo(rectF.left, (f4 - this.arrowHalfWidth) - AndroidUtilities.dp(2.0f));
            if (rect != null) {
                rect.left = (int) (rect.left - this.arrowHeight);
            }
        }
        if (this.roundWithCornerEffect) {
            path.lineTo(rectF.left, rectF.top);
        } else {
            RectF rectF3 = this.oval;
            float f16 = rectF.left;
            float f17 = rectF.top;
            float f18 = min * 2.0f;
            rectF3.set(f16, f17, f16 + f18, f18 + f17);
            path.arcTo(this.oval, 180.0f, 90.0f);
        }
        if (this.direction == 1) {
            path.lineTo((f4 - this.arrowHalfWidth) - AndroidUtilities.dp(2.0f), rectF.top);
            path.lineTo(f4 - this.arrowHalfWidth, rectF.top);
            path.lineTo(f4 - AndroidUtilities.dp(1.0f), rectF.top - this.arrowHeight);
            this.arrowX = f4;
            this.arrowY = rectF.top - this.arrowHeight;
            path.lineTo(AndroidUtilities.dp(1.0f) + f4, rectF.top - this.arrowHeight);
            path.lineTo(this.arrowHalfWidth + f4, rectF.top);
            path.lineTo(this.arrowHalfWidth + f4 + AndroidUtilities.dp(2.0f), rectF.top);
            if (rect != null) {
                rect.top = (int) (rect.top - this.arrowHeight);
            }
        }
        if (this.roundWithCornerEffect) {
            path.lineTo(rectF.right, rectF.top);
        } else {
            RectF rectF4 = this.oval;
            float f19 = rectF.right;
            float f20 = min * 2.0f;
            float f21 = rectF.top;
            rectF4.set(f19 - f20, f21, f19, f20 + f21);
            path.arcTo(this.oval, 270.0f, 90.0f);
        }
        if (this.direction == 2) {
            path.lineTo(rectF.right, (f4 - this.arrowHalfWidth) - AndroidUtilities.dp(2.0f));
            path.lineTo(rectF.right, f4 - this.arrowHalfWidth);
            path.lineTo(rectF.right + this.arrowHeight, f4 - AndroidUtilities.dp(1.0f));
            float f22 = rectF.right + this.arrowHeight;
            this.arrowX = f22;
            this.arrowY = f4;
            path.lineTo(f22, AndroidUtilities.dp(1.0f) + f4);
            path.lineTo(rectF.right, this.arrowHalfWidth + f4);
            path.lineTo(rectF.right, this.arrowHalfWidth + f4 + AndroidUtilities.dp(2.0f));
            if (rect != null) {
                rect.right = (int) (rect.right + this.arrowHeight);
            }
        }
        if (this.roundWithCornerEffect) {
            path.lineTo(rectF.right, rectF.bottom);
        } else {
            RectF rectF5 = this.oval;
            float f23 = rectF.right;
            float f24 = min * 2.0f;
            float f25 = rectF.bottom;
            rectF5.set(f23 - f24, f25 - f24, f23, f25);
            path.arcTo(this.oval, 0.0f, 90.0f);
        }
        if (this.direction == 3) {
            path.lineTo(this.arrowHalfWidth + f4 + AndroidUtilities.dp(2.0f), rectF.bottom);
            path.lineTo(this.arrowHalfWidth + f4, rectF.bottom);
            path.lineTo(AndroidUtilities.dp(1.0f) + f4, rectF.bottom + this.arrowHeight);
            this.arrowX = f4;
            this.arrowY = rectF.bottom + this.arrowHeight;
            path.lineTo(f4 - AndroidUtilities.dp(1.0f), rectF.bottom + this.arrowHeight);
            path.lineTo(f4 - this.arrowHalfWidth, rectF.bottom);
            path.lineTo((f4 - this.arrowHalfWidth) - AndroidUtilities.dp(2.0f), rectF.bottom);
            if (rect != null) {
                rect.bottom = (int) (rect.bottom + this.arrowHeight);
            }
        }
        path.close();
        this.pathSet = true;
    }

    private int getTextMaxWidth() {
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        RectF rectF = this.innerPadding;
        int i = measuredWidth - ((int) (rectF.left + rectF.right));
        int i2 = this.textMaxWidth;
        if (i2 > 0) {
            i = Math.min(i2, i);
        }
        return Math.max(0, i);
    }

    private ClickableSpan hitLink(int i, int i2) {
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout == null) {
            return null;
        }
        int i3 = (int) (i - this.textX);
        int i4 = (int) (i2 - this.textY);
        int lineForVertical = staticLayout.getLineForVertical(i4);
        float f = i3;
        int offsetForHorizontal = this.textLayout.getOffsetForHorizontal(lineForVertical, f);
        float lineLeft = this.textLayout.getLineLeft(lineForVertical);
        if (lineLeft <= f && lineLeft + this.textLayout.getLineWidth(lineForVertical) >= f && i4 >= 0 && i4 <= this.textLayout.getHeight()) {
            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) new SpannableString(this.textLayout.getText()).getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
            if (clickableSpanArr.length != 0 && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                return clickableSpanArr[0];
            }
        }
        return null;
    }

    public void lambda$bounceShow$0(ValueAnimator valueAnimator) {
        this.bounceT = Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    public void lambda$checkTouchLinks$1(LinkSpanDrawable linkSpanDrawable, ClickableSpan clickableSpan) {
        LinkSpanDrawable.LinksTextView.OnLinkPress onLinkPress = this.onLongPressListener;
        if (onLinkPress == null || this.pressedLink != linkSpanDrawable) {
            return;
        }
        onLinkPress.run(clickableSpan);
        this.pressedLink = null;
        this.links.clear();
    }

    public void lambda$prepareBlur$2(Bitmap bitmap) {
        this.drawingMyBlur = false;
        this.blurBitmapWidth = bitmap.getWidth();
        this.blurBitmapHeight = bitmap.getHeight();
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        this.blurBitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
        this.blurBitmapMatrix = new Matrix();
        Paint paint = new Paint(1);
        this.blurBackgroundPaint = paint;
        paint.setShader(this.blurBitmapShader);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1.5f);
        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, Theme.isCurrentThemeDark() ? 0.12f : -0.08f);
        this.blurBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        Paint paint2 = new Paint(1);
        this.blurCutPaint = paint2;
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.blurCutPaint.setPathEffect(new CornerPathEffect(this.rounding));
    }

    private void makeLayout(CharSequence charSequence, int i) {
        this.textLayout = new StaticLayout(charSequence, this.textPaint, i, this.textLayoutAlignment, 1.0f, 0.0f, false);
        float f = i;
        float f2 = 0.0f;
        for (int i2 = 0; i2 < this.textLayout.getLineCount(); i2++) {
            f = Math.min(f, this.textLayout.getLineLeft(i2));
            f2 = Math.max(f2, this.textLayout.getLineRight(i2));
        }
        this.textLayoutWidth = Math.max(0.0f, f2 - f);
        this.textLayoutHeight = this.textLayout.getHeight();
        this.textLayoutLeft = f;
        this.emojiGroupedSpans = AnimatedEmojiSpan.update(0, this, this.emojiGroupedSpans, this.textLayout);
    }

    public static float measureCorrectly(CharSequence charSequence, TextPaint textPaint) {
        float f = 0.0f;
        if (charSequence == null) {
            return 0.0f;
        }
        if (!(charSequence instanceof Spanned)) {
            return textPaint.measureText(charSequence.toString());
        }
        Spanned spanned = (Spanned) charSequence;
        TypefaceSpan[] typefaceSpanArr = (TypefaceSpan[]) spanned.getSpans(0, charSequence.length(), TypefaceSpan.class);
        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(0, charSequence.length(), AnimatedEmojiSpan.class);
        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spanned.getSpans(0, charSequence.length(), Emoji.EmojiSpan.class);
        ColoredImageSpan[] coloredImageSpanArr = (ColoredImageSpan[]) spanned.getSpans(0, charSequence.length(), ColoredImageSpan.class);
        int i = 0;
        for (Emoji.EmojiSpan emojiSpan : emojiSpanArr) {
            i = (int) (i + Math.max(0.0f, emojiSpan.size - textPaint.measureText(spanned, spanned.getSpanStart(emojiSpan), spanned.getSpanEnd(emojiSpan))));
        }
        for (ColoredImageSpan coloredImageSpan : coloredImageSpanArr) {
            i = (int) (i + Math.max(0.0f, coloredImageSpan.getSize(textPaint, charSequence, r15, r5, textPaint.getFontMetricsInt()) - textPaint.measureText(spanned, spanned.getSpanStart(coloredImageSpan), spanned.getSpanEnd(coloredImageSpan))));
        }
        for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
            i = (int) (i + Math.max(0.0f, animatedEmojiSpan.getSize(textPaint, charSequence, r13, r14, textPaint.getFontMetricsInt()) - textPaint.measureText(spanned, spanned.getSpanStart(animatedEmojiSpan), spanned.getSpanEnd(animatedEmojiSpan))));
        }
        if (typefaceSpanArr == null || typefaceSpanArr.length == 0) {
            return textPaint.measureText(charSequence.toString()) + i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < typefaceSpanArr.length; i3++) {
            int spanStart = spanned.getSpanStart(typefaceSpanArr[i3]);
            int spanEnd = spanned.getSpanEnd(typefaceSpanArr[i3]);
            int max = Math.max(i2, spanStart);
            if (max - i2 > 0) {
                f += textPaint.measureText(spanned, i2, max);
            }
            i2 = Math.max(max, spanEnd);
            if (i2 - max > 0) {
                Typeface typeface = textPaint.getTypeface();
                textPaint.setTypeface(typefaceSpanArr[i3].getTypeface());
                f += textPaint.measureText(spanned, max, i2);
                textPaint.setTypeface(typeface);
            }
        }
        int max2 = Math.max(i2, charSequence.length());
        if (max2 - i2 > 0) {
            f += textPaint.measureText(spanned, i2, max2);
        }
        return f + i;
    }

    private void prepareBlur() {
        if (this.useBlur) {
            this.drawingMyBlur = true;
            AndroidUtilities.makeGlobalBlurBitmap(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    HintView2.this.lambda$prepareBlur$2((Bitmap) obj);
                }
            }, this.blurScale);
        }
    }

    private void updateBlurBounds() {
        if (!this.useBlur || this.blurBitmapShader == null || this.blurBitmapMatrix == null) {
            return;
        }
        if (this.blurPos == null) {
            this.blurPos = new int[2];
        }
        getLocationOnScreen(this.blurPos);
        this.blurBitmapMatrix.reset();
        Matrix matrix = this.blurBitmapMatrix;
        Point point = AndroidUtilities.displaySize;
        matrix.postScale(point.x / this.blurBitmapWidth, (point.y + AndroidUtilities.statusBarHeight) / this.blurBitmapHeight);
        Matrix matrix2 = this.blurBitmapMatrix;
        int[] iArr = this.blurPos;
        matrix2.postTranslate(-iArr[0], -iArr[1]);
        if (this.show.get() < 1.0f && this.useScale) {
            float lerp = 1.0f / AndroidUtilities.lerp(0.5f, 1.0f, this.show.get());
            this.blurBitmapMatrix.postScale(lerp, lerp, this.arrowX, this.arrowY);
        }
        this.blurBitmapShader.setLocalMatrix(this.blurBitmapMatrix);
    }

    public boolean containsTouch(MotionEvent motionEvent, float f, float f2) {
        return this.bounds.contains(motionEvent.getX() - f, motionEvent.getY() - f2);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        float f;
        Canvas canvas2;
        int i;
        if (this.drawingMyBlur) {
            return;
        }
        if (this.multiline && this.textLayout == null) {
            return;
        }
        float f2 = this.show.set(this.shown && !this.firstDraw);
        if (this.firstDraw) {
            this.firstDraw = false;
            invalidate();
        }
        float f3 = 0.0f;
        if (f2 <= 0.0f) {
            return;
        }
        float currentWidth = this.multiline ? this.textLayoutWidth : this.textDrawable.getCurrentWidth();
        float height = this.multiline ? this.textLayoutHeight : this.textDrawable.getHeight();
        if (this.closeButton) {
            if (this.closeButtonDrawable == null) {
                Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_mini_close_tooltip).mutate();
                this.closeButtonDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(2113929215, PorterDuff.Mode.MULTIPLY));
            }
            currentWidth += this.closeButtonMargin + this.closeButtonDrawable.getIntrinsicWidth();
            height = Math.max(this.closeButtonDrawable.getIntrinsicHeight(), height);
        }
        if (this.icon != null) {
            currentWidth += this.iconWidth + this.iconMargin;
            height = Math.max(this.iconHeight, height);
        }
        float f4 = currentWidth;
        RectF rectF = this.innerPadding;
        float f5 = rectF.left + f4 + rectF.right;
        float f6 = rectF.top + height + rectF.bottom;
        if (!this.pathSet || Math.abs(f5 - this.pathLastWidth) > 0.1f || Math.abs(f6 - this.pathLastHeight) > 0.1f) {
            Path path = this.path;
            this.pathLastWidth = f5;
            this.pathLastHeight = f6;
            fillPath(path, f5, f6, 0.0f, this.bounds, this.boundsWithArrow);
            if (this.flicker) {
                fillPath(this.flickerStrokePath, f5, f6, this.flickerStrokePathExtrude, this.flickerBounds, null);
            }
        }
        float f7 = this.useAlpha ? f2 : 1.0f;
        canvas.save();
        if (f2 < 1.0f && this.useScale) {
            float lerp = AndroidUtilities.lerp(0.5f, 1.0f, f2);
            canvas.scale(lerp, lerp, this.arrowX, this.arrowY);
        }
        float scale = this.bounce.getScale(0.025f);
        if (scale != 1.0f) {
            canvas.scale(scale, scale, this.arrowX, this.arrowY);
        }
        if (this.bounceT != 1.0f) {
            int i2 = this.direction;
            if (i2 == 3 || i2 == 1) {
                canvas.translate(0.0f, (this.bounceT - 1.0f) * Math.max(i2 == 3 ? getPaddingBottom() : getPaddingTop(), AndroidUtilities.dp(24.0f)) * (this.direction == 1 ? -1 : 1));
            } else {
                canvas.translate((this.bounceT - 1.0f) * Math.max(i2 == 0 ? getPaddingLeft() : getPaddingRight(), AndroidUtilities.dp(24.0f)) * (this.direction == 0 ? -1 : 1), 0.0f);
            }
        }
        updateBlurBounds();
        int alpha = this.backgroundPaint.getAlpha();
        RectF rectF2 = AndroidUtilities.rectTmp;
        rectF2.set(this.bounds);
        float f8 = -this.arrowHeight;
        rectF2.inset(f8, f8);
        Paint paint = this.blurBackgroundPaint;
        if (paint == null || !this.useBlur) {
            f = f7;
        } else {
            f = (1.0f - this.blurAlpha) * f7;
            paint.setAlpha((int) (f7 * 255.0f));
        }
        this.backgroundPaint.setAlpha((int) (alpha * f));
        drawBgPath(canvas);
        this.backgroundPaint.setAlpha(alpha);
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setAlpha((int) (f7 * 255.0f));
            this.selectorDrawable.setBounds(this.boundsWithArrow);
            this.selectorDrawable.draw(canvas);
        }
        RectF rectF3 = this.bounds;
        float f9 = rectF3.bottom;
        RectF rectF4 = this.innerPadding;
        float f10 = ((f9 - rectF4.bottom) + (rectF3.top + rectF4.top)) / 2.0f;
        Drawable drawable2 = this.icon;
        if (drawable2 != null) {
            if (this.iconLeft) {
                float f11 = this.iconTx + rectF3.left + (rectF4.left / 2.0f);
                float f12 = this.iconTy + f10;
                float f13 = this.iconHeight / 2.0f;
                drawable2.setBounds((int) f11, (int) (f12 - f13), (int) (f11 + this.iconWidth), (int) (f12 + f13));
                f3 = 0.0f + this.iconWidth + this.iconMargin;
            } else {
                float f14 = (this.iconTx + rectF3.right) - (rectF4.right / 2.0f);
                float f15 = this.iconTy + f10;
                float f16 = this.iconHeight / 2.0f;
                drawable2.setBounds((int) (f14 - this.iconWidth), (int) (f15 - f16), (int) f14, (int) (f15 + f16));
            }
            this.icon.setAlpha((int) (f7 * 255.0f));
            this.icon.draw(canvas);
        }
        if (this.multiline) {
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), Math.max(getHeight(), f6), (int) (f7 * 255.0f), 31);
            float f17 = ((f3 + this.bounds.left) + this.innerPadding.left) - this.textLayoutLeft;
            this.textX = f17;
            float f18 = f10 - (this.textLayoutHeight / 2.0f);
            this.textY = f18;
            canvas.translate(f17, f18);
            if (this.links.draw(canvas)) {
                invalidate();
            }
            this.textLayout.draw(canvas);
            i = 2113929215;
            canvas2 = canvas;
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.textLayout, this.emojiGroupedSpans, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
            canvas.restore();
        } else {
            canvas2 = canvas;
            i = 2113929215;
            CharSequence charSequence = this.textToSet;
            if (charSequence != null) {
                this.textDrawable.setText(charSequence, this.shown);
                this.textToSet = null;
            }
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.textDrawable;
            float f19 = this.bounds.left;
            float f20 = this.innerPadding.left;
            float f21 = this.textLayoutHeight / 2.0f;
            animatedTextDrawable.setBounds((int) (f3 + f19 + f20), (int) (f10 - f21), (int) (f19 + f20 + f4), (int) (f10 + f21));
            this.textDrawable.setAlpha((int) (f7 * 255.0f));
            this.textDrawable.draw(canvas2);
        }
        if (this.closeButton) {
            if (this.closeButtonDrawable == null) {
                Drawable mutate2 = getContext().getResources().getDrawable(R.drawable.msg_mini_close_tooltip).mutate();
                this.closeButtonDrawable = mutate2;
                mutate2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
            this.closeButtonDrawable.setAlpha((int) (f7 * 255.0f));
            Drawable drawable3 = this.closeButtonDrawable;
            int intrinsicWidth = (int) ((this.bounds.right - (this.innerPadding.right * 0.66f)) - drawable3.getIntrinsicWidth());
            int centerY = (int) (this.bounds.centerY() - (this.closeButtonDrawable.getIntrinsicHeight() / 2.0f));
            RectF rectF5 = this.bounds;
            drawable3.setBounds(intrinsicWidth, centerY, (int) (rectF5.right - (this.innerPadding.right * 0.66f)), (int) (rectF5.centerY() + (this.closeButtonDrawable.getIntrinsicHeight() / 2.0f)));
            this.closeButtonDrawable.draw(canvas2);
        }
        canvas.restore();
    }

    protected void drawBgPath(Canvas canvas) {
        if (this.blurBackgroundPaint != null) {
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
            canvas.drawPath(this.path, this.blurBackgroundPaint);
            canvas.drawPath(this.path, this.blurCutPaint);
            canvas.restore();
        }
        canvas.drawPath(this.path, this.backgroundPaint);
        if (this.flicker) {
            int dp = AndroidUtilities.dp(64.0f);
            float currentTimeMillis = (-dp) + ((((float) ((System.currentTimeMillis() - this.flickerStart) % 4000)) / 4000.0f) * ((this.pathLastWidth * 4.0f) + (dp * 2)));
            this.flickerGradientMatrix.reset();
            this.flickerGradientMatrix.postTranslate(this.bounds.left + currentTimeMillis, 0.0f);
            this.flickerGradient.setLocalMatrix(this.flickerGradientMatrix);
            this.flickerStrokeGradient.setLocalMatrix(this.flickerGradientMatrix);
            canvas.drawPath(this.path, this.flickerFillPaint);
            canvas.drawPath(this.flickerStrokePath, this.flickerStrokePaint);
            invalidate();
        }
    }

    public CharSequence getText() {
        CharSequence charSequence = this.textToSet;
        if (charSequence != null) {
            return charSequence;
        }
        if (!this.multiline) {
            return this.textDrawable.getText();
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            return staticLayout.getText();
        }
        return null;
    }

    public TextPaint getTextPaint() {
        return this.multiline ? this.textPaint : this.textDrawable.getPaint();
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean z) {
        AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
        Runnable runnable = this.onHidden;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.shown = false;
        if (!z) {
            this.show.set(false, false);
        }
        invalidate();
        Runnable runnable2 = this.onHidden;
        if (runnable2 != null) {
            AndroidUtilities.runOnUIThread(runnable2, this.show.get() * ((float) this.show.getDuration()));
        }
        this.links.clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatedEmojiSpan.release(this, this.emojiGroupedSpans);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        this.pathSet = false;
        int textMaxWidth = getTextMaxWidth();
        this.textDrawable.setOverrideFullWidth(textMaxWidth);
        if (this.multiline) {
            CharSequence charSequence = this.textToSet;
            if (charSequence == null) {
                StaticLayout staticLayout = this.textLayout;
                if (staticLayout == null) {
                    return;
                } else {
                    charSequence = staticLayout.getText();
                }
            }
            StaticLayout staticLayout2 = this.textLayout;
            if (staticLayout2 == null || staticLayout2.getWidth() != textMaxWidth) {
                makeLayout(charSequence, textMaxWidth);
            }
        } else {
            CharSequence charSequence2 = this.textToSet;
            if (charSequence2 != null) {
                this.textDrawable.setText(charSequence2, false);
            }
        }
        this.textToSet = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((this.hideByTouch || hasOnClickListeners()) && this.shown) {
            return checkTouchLinks(motionEvent) || checkTouchTap(motionEvent);
        }
        return false;
    }

    public void pause() {
        AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
    }

    public HintView2 setAnimatedTextHacks(boolean z, boolean z2, boolean z3) {
        this.textDrawable.setHacks(z, z2, z3);
        return this;
    }

    public HintView2 setArrowSize(float f, float f2) {
        this.arrowHalfWidth = AndroidUtilities.dpf2(f);
        this.arrowHeight = AndroidUtilities.dpf2(f2);
        return this;
    }

    public HintView2 setBgColor(int i) {
        if (this.backgroundPaint.getColor() != i) {
            this.backgroundPaint.setColor(i);
            invalidate();
        }
        return this;
    }

    public HintView2 setBounce(boolean z) {
        this.repeatedBounce = z;
        return this;
    }

    public HintView2 setCloseButton(boolean z) {
        this.closeButton = z;
        if (!this.multiline) {
            this.innerPadding.set(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(this.closeButton ? 15.0f : 11.0f), AndroidUtilities.dp(7.0f));
        }
        return this;
    }

    public HintView2 setDirection(int i) {
        this.direction = i;
        return this;
    }

    public HintView2 setDuration(long j) {
        this.duration = j;
        return this;
    }

    public HintView2 setFlicker(float f, int i) {
        this.flicker = true;
        this.flickerStart = System.currentTimeMillis();
        this.flickerStrokePath = new Path();
        this.flickerStrokePathExtrude = AndroidUtilities.dpf2(f) / 2.0f;
        this.flickerFillPaint = new Paint(1);
        this.flickerStrokePaint = new Paint(1);
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(64.0f), 0.0f, new int[]{Theme.multAlpha(i, 0.0f), Theme.multAlpha(i, 1.0f), Theme.multAlpha(i, 0.0f)}, new float[]{0.0f, 0.5f, 1.0f}, tileMode);
        this.flickerStrokeGradient = linearGradient;
        this.flickerStrokePaint.setShader(linearGradient);
        this.flickerGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(64.0f), 0.0f, new int[]{Theme.multAlpha(i, 0.0f), Theme.multAlpha(i, 0.5f), Theme.multAlpha(i, 0.0f)}, new float[]{0.0f, 0.5f, 1.0f}, tileMode);
        this.flickerGradientMatrix = new Matrix();
        this.flickerFillPaint.setShader(this.flickerGradient);
        this.flickerStrokePaint.setStyle(Paint.Style.STROKE);
        this.flickerStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        this.flickerStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        this.flickerStrokePaint.setStrokeWidth(AndroidUtilities.dpf2(f));
        return this;
    }

    public HintView2 setHideByTouch(boolean z) {
        this.hideByTouch = z;
        return this;
    }

    public HintView2 setIcon(int i) {
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(34.0f), AndroidUtilities.dp(34.0f));
        rLottieDrawable.start();
        return setIcon(rLottieDrawable);
    }

    public HintView2 setIcon(Drawable drawable) {
        Drawable drawable2 = this.icon;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.icon = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            Drawable drawable3 = this.icon;
            if (drawable3 instanceof RLottieDrawable) {
                this.duration = Math.max(this.duration, ((RLottieDrawable) drawable3).getDuration());
            }
            this.iconWidth = this.icon.getIntrinsicWidth();
            this.iconHeight = this.icon.getIntrinsicHeight();
            this.iconLeft = true;
        }
        return this;
    }

    public HintView2 setIconMargin(int i) {
        this.iconMargin = AndroidUtilities.dp(i);
        return this;
    }

    public HintView2 setIconTranslate(float f, float f2) {
        this.iconTx = f;
        this.iconTy = f2;
        return this;
    }

    public HintView2 setInnerPadding(float f, float f2, float f3, float f4) {
        this.innerPadding.set(AndroidUtilities.dpf2(f), AndroidUtilities.dpf2(f2), AndroidUtilities.dpf2(f3), AndroidUtilities.dpf2(f4));
        return this;
    }

    public HintView2 setJoint(float f, float f2) {
        if (Math.abs(this.joint - f) >= 1.0f || Math.abs(this.jointTranslate - AndroidUtilities.dp(f2)) >= 1.0f) {
            this.pathSet = false;
            invalidate();
        }
        this.joint = f;
        this.jointTranslate = AndroidUtilities.dp(f2);
        return this;
    }

    public HintView2 setJointPx(float f, float f2) {
        if (Math.abs(this.joint - f) >= 1.0f || Math.abs(this.jointTranslate - f2) >= 1.0f) {
            this.pathSet = false;
            invalidate();
        }
        this.joint = f;
        this.jointTranslate = f2;
        return this;
    }

    public HintView2 setMaxWidth(float f) {
        this.textMaxWidth = AndroidUtilities.dp(f);
        return this;
    }

    public HintView2 setMaxWidthPx(int i) {
        this.textMaxWidth = i;
        return this;
    }

    public HintView2 setMultilineText(boolean z) {
        int dp;
        this.multiline = z;
        if (z) {
            this.innerPadding.set(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(8.0f));
            dp = AndroidUtilities.dp(6.0f);
        } else {
            this.innerPadding.set(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(this.closeButton ? 15.0f : 11.0f), AndroidUtilities.dp(7.0f));
            dp = AndroidUtilities.dp(2.0f);
        }
        this.closeButtonMargin = dp;
        return this;
    }

    public HintView2 setOnHiddenListener(Runnable runnable) {
        this.onHidden = runnable;
        return this;
    }

    public HintView2 setRounding(float f) {
        this.rounding = AndroidUtilities.dp(f);
        this.backgroundPaint.setPathEffect(this.roundWithCornerEffect ? new CornerPathEffect(this.rounding) : null);
        Paint paint = this.cutSelectorPaint;
        if (paint != null) {
            paint.setPathEffect(this.roundWithCornerEffect ? new CornerPathEffect(this.rounding) : null);
        }
        Paint paint2 = this.blurCutPaint;
        if (paint2 != null) {
            paint2.setPathEffect(this.roundWithCornerEffect ? new CornerPathEffect(this.rounding) : null);
        }
        return this;
    }

    public HintView2 setRoundingWithCornerEffect(boolean z) {
        this.roundWithCornerEffect = z;
        this.backgroundPaint.setPathEffect(z ? new CornerPathEffect(this.rounding) : null);
        return this;
    }

    public HintView2 setSelectorColor(int i) {
        if (Build.VERSION.SDK_INT < 21) {
            return this;
        }
        Paint paint = new Paint(1);
        this.cutSelectorPaint = paint;
        paint.setPathEffect(new CornerPathEffect(this.rounding));
        BaseCell.RippleDrawableSafe rippleDrawableSafe = new BaseCell.RippleDrawableSafe(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), null, new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.save();
                HintView2 hintView2 = HintView2.this;
                canvas.drawPath(hintView2.path, hintView2.cutSelectorPaint);
                canvas.restore();
            }

            @Override
            public int getOpacity() {
                return -2;
            }

            @Override
            public void setAlpha(int i2) {
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }
        });
        this.selectorDrawable = rippleDrawableSafe;
        rippleDrawableSafe.setCallback(this);
        return this;
    }

    public HintView2 setText(CharSequence charSequence) {
        if (getMeasuredWidth() < 0) {
            this.textToSet = charSequence;
        } else if (this.multiline) {
            makeLayout(charSequence, getTextMaxWidth());
        } else {
            this.textDrawable.setText(charSequence, false);
        }
        return this;
    }

    public HintView2 setText(CharSequence charSequence, boolean z) {
        if (getMeasuredWidth() < 0) {
            this.textToSet = charSequence;
        } else {
            this.textDrawable.setText(charSequence, !LocaleController.isRTL && z);
        }
        return this;
    }

    public HintView2 setTextAlign(Layout.Alignment alignment) {
        this.textLayoutAlignment = alignment;
        return this;
    }

    public HintView2 setTextColor(int i) {
        this.textDrawable.setTextColor(i);
        this.textPaint.setColor(i);
        return this;
    }

    public HintView2 setTextSize(float f) {
        this.textDrawable.setTextSize(AndroidUtilities.dpf2(f));
        this.textPaint.setTextSize(AndroidUtilities.dpf2(f));
        return this;
    }

    public HintView2 setTextTypeface(Typeface typeface) {
        this.textDrawable.setTypeface(typeface);
        this.textPaint.setTypeface(typeface);
        return this;
    }

    public HintView2 show() {
        prepareBlur();
        if (this.shown) {
            bounceShow();
        }
        AndroidUtilities.makeAccessibilityAnnouncement(getText());
        this.shown = true;
        invalidate();
        AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
        long j = this.duration;
        if (j > 0) {
            AndroidUtilities.runOnUIThread(this.hideRunnable, j);
        }
        Runnable runnable = this.onHidden;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        return this;
    }

    public void show(boolean z) {
        if (z) {
            show();
        } else {
            hide();
        }
    }

    public boolean shown() {
        return this.shown;
    }

    public void unpause() {
        AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
        long j = this.duration;
        if (j > 0) {
            AndroidUtilities.runOnUIThread(this.hideRunnable, j);
        }
    }

    public HintView2 useScale(boolean z) {
        this.useScale = z;
        return this;
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.textDrawable || drawable == this.selectorDrawable || drawable == this.icon || super.verifyDrawable(drawable);
    }
}
