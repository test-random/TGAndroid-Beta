package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.PaintTypeface;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;

public class TextPaintView extends EntityView {
    private int align;
    private int baseFontSize;
    private int currentType;
    private boolean disableAutoresize;
    private EditTextOutline editText;
    private String lastTypefaceKey;
    private int maxFontSize;
    private int minFontSize;
    private Runnable onFontChange;
    private Swatch swatch;
    private PaintTypeface typeface;

    public class TextViewSelectionView extends EntityView.SelectionView {
        private final Paint clearPaint;
        private Path path;

        public TextViewSelectionView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.clearPaint = paint;
            this.path = new Path();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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
            float dp = AndroidUtilities.dp(2.0f);
            float dpf2 = AndroidUtilities.dpf2(5.66f);
            float dp2 = dp + dpf2 + AndroidUtilities.dp(15.0f);
            float f = dp2 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f;
            float measuredHeight = getMeasuredHeight() - f;
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = dp2 + measuredWidth;
            float f3 = dp2 + measuredHeight;
            rectF.set(dp2, dp2, f2, f3);
            float dp3 = AndroidUtilities.dp(12.0f);
            float min = Math.min(dp3, measuredWidth / 2.0f);
            float f4 = measuredHeight / 2.0f;
            float min2 = Math.min(dp3, f4);
            this.path.rewind();
            float f5 = min * 2.0f;
            float f6 = dp2 + f5;
            float f7 = 2.0f * min2;
            float f8 = dp2 + f7;
            rectF.set(dp2, dp2, f6, f8);
            this.path.arcTo(rectF, 180.0f, 90.0f);
            float f9 = f2 - f5;
            rectF.set(f9, dp2, f2, f8);
            this.path.arcTo(rectF, 270.0f, 90.0f);
            canvas.drawPath(this.path, this.paint);
            this.path.rewind();
            float f10 = f3 - f7;
            rectF.set(dp2, f10, f6, f3);
            this.path.arcTo(rectF, 180.0f, -90.0f);
            rectF.set(f9, f10, f2, f3);
            this.path.arcTo(rectF, 90.0f, -90.0f);
            canvas.drawPath(this.path, this.paint);
            float f11 = dp2 + f4;
            canvas.drawCircle(dp2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(dp2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.drawCircle(f2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(f2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
            float f12 = dp2 + min2;
            float f13 = f3 - min2;
            canvas.drawLine(dp2, f12, dp2, f13, this.paint);
            canvas.drawLine(f2, f12, f2, f13, this.paint);
            canvas.drawCircle(f2, f11, (AndroidUtilities.dp(1.0f) + dpf2) - 1.0f, this.clearPaint);
            canvas.drawCircle(dp2, f11, (dpf2 + AndroidUtilities.dp(1.0f)) - 1.0f, this.clearPaint);
            canvas.restoreToCount(saveCount);
        }

        @Override
        protected int pointInsideHandle(float f, float f2) {
            float dp = AndroidUtilities.dp(1.0f);
            float dp2 = AndroidUtilities.dp(19.5f);
            float f3 = dp + dp2;
            float f4 = f3 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f4;
            float measuredHeight = ((getMeasuredHeight() - f4) / 2.0f) + f3;
            if (f > f3 - dp2 && f2 > measuredHeight - dp2 && f < f3 + dp2 && f2 < measuredHeight + dp2) {
                return 1;
            }
            float f5 = f3 + measuredWidth;
            return (f <= f5 - dp2 || f2 <= measuredHeight - dp2 || f >= f5 + dp2 || f2 >= measuredHeight + dp2) ? 0 : 2;
        }
    }

    public TextPaintView(Context context, TextPaintView textPaintView, Point point) {
        this(context, point, textPaintView.baseFontSize, textPaintView.getText(), textPaintView.getSwatch(), textPaintView.currentType);
        setRotation(textPaintView.getRotation());
        setScale(textPaintView.getScale());
        setTypeface(textPaintView.getTypeface());
        setAlign(textPaintView.getAlign());
        int align = getAlign();
        int i = 2;
        this.editText.setGravity(align != 1 ? align != 2 ? 19 : 21 : 17);
        int align2 = getAlign();
        if (align2 == 1) {
            i = 4;
        } else if (align2 == 2 ? !LocaleController.isRTL : LocaleController.isRTL) {
            i = 3;
        }
        this.editText.setTextAlignment(i);
    }

    public TextPaintView(Context context, Point point, int i, CharSequence charSequence, Swatch swatch, int i2) {
        super(context, point);
        EditTextOutline editTextOutline;
        int i3;
        this.typeface = PaintTypeface.ROBOTO_MEDIUM;
        this.baseFontSize = i;
        EditTextOutline editTextOutline2 = new EditTextOutline(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                EntityView.SelectionView selectionView = TextPaintView.this.selectionView;
                if (selectionView == null || selectionView.getVisibility() != 0) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            public void onLayout(boolean z, int i4, int i5, int i6, int i7) {
                super.onLayout(z, i4, i5, i6, i7);
                TextPaintView.this.updateSelectionView();
            }

            @Override
            public void onMeasure(int i4, int i5) {
                super.onMeasure(i4, i5);
                TextPaintView.this.updateSelectionView();
            }
        };
        this.editText = editTextOutline2;
        NotificationCenter.listenEmojiLoading(editTextOutline2);
        this.editText.setGravity(19);
        this.editText.setBackgroundColor(0);
        this.editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
        this.editText.setClickable(false);
        this.editText.setEnabled(false);
        this.editText.setCursorColor(-1);
        this.editText.setTextSize(0, this.baseFontSize);
        this.editText.setCursorSize(AndroidUtilities.dp(this.baseFontSize * 0.4f));
        this.editText.setText(charSequence);
        updateHint();
        this.editText.setTextColor(swatch.color);
        this.editText.setTypeface(null, 1);
        this.editText.setHorizontallyScrolling(false);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 26) {
            editTextOutline = this.editText;
            i3 = 285212672;
        } else {
            editTextOutline = this.editText;
            i3 = 268435456;
        }
        editTextOutline.setImeOptions(i3);
        this.editText.setFocusableInTouchMode(true);
        this.editText.setInputType(16384);
        this.editText.setSingleLine(false);
        addView(this.editText, LayoutHelper.createFrame(-2, -2, 51));
        if (i4 >= 29 || i4 >= 23) {
            this.editText.setBreakStrategy(0);
        }
        setSwatch(swatch);
        setType(i2);
        updatePosition();
        this.editText.addTextChangedListener(new TextWatcher() {
            boolean pasted;

            @Override
            public void afterTextChanged(Editable editable) {
                int clamp;
                if (this.pasted && TextPaintView.this.minFontSize > 0 && TextPaintView.this.maxFontSize > 0 && !TextPaintView.this.disableAutoresize && TextPaintView.this.editText.getLayout() != null) {
                    float f = AndroidUtilities.displaySize.y / 3.0f;
                    float height = TextPaintView.this.editText.getLayout().getHeight();
                    if (height > f && (clamp = Utilities.clamp((int) ((f / height) * TextPaintView.this.getBaseFontSize()), TextPaintView.this.maxFontSize, TextPaintView.this.minFontSize)) != TextPaintView.this.getBaseFontSize()) {
                        TextPaintView.this.setBaseFontSize(clamp);
                        if (TextPaintView.this.onFontChange != null) {
                            TextPaintView.this.onFontChange.run();
                        }
                    }
                }
                TextPaintView.this.updateHint();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence2, int i5, int i6, int i7) {
                this.pasted = i7 > 3;
            }

            @Override
            public void onTextChanged(CharSequence charSequence2, int i5, int i6, int i7) {
            }
        });
    }

    public void lambda$beginEditing$0() {
        AndroidUtilities.showKeyboard(this.editText);
    }

    public void updateHint() {
        if (this.editText.getText().length() > 0) {
            this.editText.setHint((CharSequence) null);
        } else {
            this.editText.setHint(LocaleController.getString(R.string.TextPlaceholder));
            this.editText.setHintTextColor(1627389951);
        }
    }

    public void beginEditing() {
        this.editText.setEnabled(true);
        this.editText.setClickable(true);
        this.editText.requestFocus();
        EditTextOutline editTextOutline = this.editText;
        editTextOutline.setSelection(editTextOutline.getText().length());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TextPaintView.this.lambda$beginEditing$0();
            }
        }, 300L);
    }

    @Override
    public TextViewSelectionView createSelectionView() {
        return new TextViewSelectionView(getContext());
    }

    public void disableAutoresize(boolean z) {
        this.disableAutoresize = z;
    }

    public void endEditing() {
        this.editText.clearFocus();
        this.editText.setEnabled(false);
        this.editText.setClickable(false);
        updateSelectionView();
    }

    public int getAlign() {
        return this.align;
    }

    public int getBaseFontSize() {
        return this.baseFontSize;
    }

    public EditTextOutline getEditText() {
        return this.editText;
    }

    public View getFocusedView() {
        return this.editText;
    }

    public Paint.FontMetricsInt getFontMetricsInt() {
        return this.editText.getPaint().getFontMetricsInt();
    }

    public float getFontSize() {
        return this.editText.getTextSize();
    }

    @Override
    public Rect getSelectionBounds() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup == null) {
            return new Rect();
        }
        float scaleX = viewGroup.getScaleX();
        float measuredWidth = (getMeasuredWidth() * getScale()) + (AndroidUtilities.dp(64.0f) / scaleX);
        float measuredHeight = (getMeasuredHeight() * getScale()) + (AndroidUtilities.dp(52.0f) / scaleX);
        float positionX = (getPositionX() - (measuredWidth / 2.0f)) * scaleX;
        return new Rect(positionX, (getPositionY() - (((measuredHeight - this.editText.getExtendedPaddingTop()) - AndroidUtilities.dpf2(4.0f)) / 2.0f)) * scaleX, ((measuredWidth * scaleX) + positionX) - positionX, (measuredHeight - this.editText.getExtendedPaddingBottom()) * scaleX);
    }

    @Override
    protected float getStickyPaddingBottom() {
        RectF rectF = this.editText.framePadding;
        if (rectF == null) {
            return 0.0f;
        }
        return rectF.bottom;
    }

    @Override
    protected float getStickyPaddingLeft() {
        RectF rectF = this.editText.framePadding;
        if (rectF == null) {
            return 0.0f;
        }
        return rectF.left;
    }

    @Override
    protected float getStickyPaddingRight() {
        RectF rectF = this.editText.framePadding;
        if (rectF == null) {
            return 0.0f;
        }
        return rectF.right;
    }

    @Override
    protected float getStickyPaddingTop() {
        RectF rectF = this.editText.framePadding;
        if (rectF == null) {
            return 0.0f;
        }
        return rectF.top;
    }

    public Swatch getSwatch() {
        return this.swatch;
    }

    public CharSequence getText() {
        return this.editText.getText();
    }

    public int getTextSize() {
        return (int) this.editText.getTextSize();
    }

    public int getType() {
        return this.currentType;
    }

    public PaintTypeface getTypeface() {
        return this.typeface;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updatePosition();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        updatePosition();
    }

    public void setAlign(int i) {
        this.align = i;
    }

    public void setBaseFontSize(int i) {
        this.baseFontSize = i;
        float f = i;
        this.editText.setTextSize(0, f);
        this.editText.setCursorSize(AndroidUtilities.dp(f * 0.4f));
        if (this.editText.getText() instanceof Spanned) {
            Editable text = this.editText.getText();
            Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) text.getSpans(0, text.length(), Emoji.EmojiSpan.class);
            for (int i2 = 0; i2 < emojiSpanArr.length; i2++) {
                emojiSpanArr[i2].replaceFontMetrics(getFontMetricsInt());
                emojiSpanArr[i2].scale = 0.85f;
            }
            for (AnimatedEmojiSpan animatedEmojiSpan : (AnimatedEmojiSpan[]) text.getSpans(0, text.length(), AnimatedEmojiSpan.class)) {
                animatedEmojiSpan.replaceFontMetrics(getFontMetricsInt());
            }
            this.editText.invalidateForce();
        }
    }

    public void setMaxWidth(int i) {
        this.editText.setMaxWidth(i);
    }

    public void setMinMaxFontSize(int i, int i2, Runnable runnable) {
        this.minFontSize = i;
        this.maxFontSize = i2;
        this.onFontChange = runnable;
    }

    public void setSwatch(Swatch swatch) {
        this.swatch = swatch.clone();
        updateColor();
    }

    public void setText(CharSequence charSequence) {
        this.editText.setText(charSequence);
        updateHint();
    }

    public void setType(int i) {
        this.currentType = i;
        updateColor();
    }

    public void setTypeface(String str) {
        Iterator it = PaintTypeface.get().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            PaintTypeface paintTypeface = (PaintTypeface) it.next();
            if (paintTypeface.getKey().equals(str)) {
                setTypeface(paintTypeface);
                str = null;
                break;
            }
        }
        this.lastTypefaceKey = str;
        updateSelectionView();
    }

    public void setTypeface(PaintTypeface paintTypeface) {
        this.typeface = paintTypeface;
        if (paintTypeface != null) {
            this.editText.setTypeface(paintTypeface.getTypeface());
        }
        updateSelectionView();
    }

    public void updateColor() {
        EditTextOutline editTextOutline;
        this.editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        int i = this.swatch.color;
        int i2 = this.currentType;
        int i3 = -1;
        if (i2 == 0) {
            this.editText.setFrameColor(i);
            i = AndroidUtilities.computePerceivedBrightness(this.swatch.color) >= 0.721f ? -16777216 : -1;
        } else {
            if (i2 == 1) {
                editTextOutline = this.editText;
                i3 = AndroidUtilities.computePerceivedBrightness(i) >= 0.25f ? -1728053248 : -1711276033;
            } else if (i2 == 2) {
                editTextOutline = this.editText;
                if (AndroidUtilities.computePerceivedBrightness(i) >= 0.25f) {
                    i3 = -16777216;
                }
            } else {
                this.editText.setFrameColor(0);
            }
            editTextOutline.setFrameColor(i3);
        }
        this.editText.setTextColor(i);
        this.editText.setCursorColor(i);
        this.editText.setHandlesColor(i);
        this.editText.setHighlightColor(Theme.multAlpha(i, 0.4f));
    }

    public void updateTypeface() {
        String str = this.lastTypefaceKey;
        if (str != null) {
            setTypeface(str);
        }
    }
}
