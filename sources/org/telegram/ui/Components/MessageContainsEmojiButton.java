package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class MessageContainsEmojiButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public boolean checkWidth;
    private int currentAccount;
    private AnimatedEmojiDrawable emojiDrawable;
    private android.graphics.Rect emojiDrawableBounds;
    private CharSequence endText;
    private TLRPC.InputStickerSet inputStickerSet;
    private int lastLineHeight;
    private int lastLineMargin;
    private int lastLineTop;
    private CharSequence lastMainTextText;
    private int lastMainTextWidth;
    private CharSequence lastSecondPartText;
    private int lastSecondPartTextWidth;
    private int lastWidth;
    private ValueAnimator loadAnimator;
    private float loadT;
    private android.graphics.Rect loadingBoundsFrom;
    private android.graphics.Rect loadingBoundsTo;
    private LoadingDrawable loadingDrawable;
    private boolean loadingDrawableBoundsSet;
    private CharSequence mainText;
    private StaticLayout mainTextLayout;
    private Theme.ResourcesProvider resourcesProvider;
    private CharSequence secondPartText;
    private StaticLayout secondPartTextLayout;
    private TextPaint textPaint;
    int type;

    private class BoldAndAccent extends CharacterStyle {
        private BoldAndAccent() {
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTypeface(AndroidUtilities.bold());
            int alpha = textPaint.getAlpha();
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, MessageContainsEmojiButton.this.resourcesProvider));
            textPaint.setAlpha(alpha);
        }
    }

    public MessageContainsEmojiButton(int i, Context context, Theme.ResourcesProvider resourcesProvider, ArrayList arrayList, int i2) {
        super(context);
        String str;
        TLRPC.Document document;
        TLRPC.TL_messages_stickerSet stickerSet;
        TLRPC.StickerSet stickerSet2;
        ArrayList<TLRPC.Document> arrayList2;
        this.emojiDrawableBounds = new android.graphics.Rect();
        this.loadingDrawableBoundsSet = false;
        this.lastWidth = -1;
        this.checkWidth = true;
        this.loadT = 0.0f;
        this.currentAccount = i;
        this.type = i2;
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 0, 6));
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
        if (arrayList.size() > 1) {
            int size = arrayList.size();
            Object[] objArr = new Object[0];
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(i2 == 0 ? LocaleController.formatPluralString("MessageContainsEmojiPacks", size, objArr) : LocaleController.formatPluralString("MessageContainsReactionsPacks", size, objArr));
            this.mainText = replaceTags;
            TypefaceSpan[] typefaceSpanArr = (TypefaceSpan[]) replaceTags.getSpans(0, replaceTags.length(), TypefaceSpan.class);
            for (int i3 = 0; typefaceSpanArr != null && i3 < typefaceSpanArr.length; i3++) {
                int spanStart = replaceTags.getSpanStart(typefaceSpanArr[i3]);
                int spanEnd = replaceTags.getSpanEnd(typefaceSpanArr[i3]);
                replaceTags.removeSpan(typefaceSpanArr[i3]);
                replaceTags.setSpan(new BoldAndAccent(), spanStart, spanEnd, 33);
            }
            return;
        }
        if (arrayList.size() != 1) {
            if (i2 == 4) {
                this.mainText = AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StickersCheckStickersBotForMoreOptions), Theme.key_chat_messageLinkIn, 2, null, resourcesProvider);
                return;
            }
            return;
        }
        String string = LocaleController.getString(i2 == 0 ? R.string.MessageContainsEmojiPack : i2 == 3 ? R.string.MessageContainsReactionPack : R.string.MessageContainsReactionsPack);
        String[] split = string.split("%s");
        if (split.length <= 1) {
            this.mainText = string;
            return;
        }
        TLRPC.InputStickerSet inputStickerSet = (TLRPC.InputStickerSet) arrayList.get(0);
        this.inputStickerSet = inputStickerSet;
        if (inputStickerSet == null || (stickerSet = MediaDataController.getInstance(i).getStickerSet(this.inputStickerSet, false)) == null || (stickerSet2 = stickerSet.set) == null) {
            str = null;
            document = null;
        } else {
            str = stickerSet2.title;
            int i4 = 0;
            while (true) {
                ArrayList<TLRPC.Document> arrayList3 = stickerSet.documents;
                if (arrayList3 == null || i4 >= arrayList3.size()) {
                    break;
                }
                if (stickerSet.documents.get(i4).id == stickerSet.set.thumb_document_id) {
                    document = stickerSet.documents.get(i4);
                    break;
                }
                i4++;
            }
            document = null;
            if (document == null && (arrayList2 = stickerSet.documents) != null && arrayList2.size() > 0) {
                document = stickerSet.documents.get(0);
            }
        }
        if (str == null || document == null) {
            this.mainText = split[0];
            this.endText = split[1];
            LoadingDrawable loadingDrawable = new LoadingDrawable(resourcesProvider);
            this.loadingDrawable = loadingDrawable;
            loadingDrawable.colorKey1 = Theme.key_actionBarDefaultSubmenuBackground;
            loadingDrawable.colorKey2 = Theme.key_listSelector;
            loadingDrawable.setRadiiDp(4.0f);
            return;
        }
        SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(document));
        spannableString.setSpan(new AnimatedEmojiSpan(document, this.textPaint.getFontMetricsInt()) {
            @Override
            public void draw(Canvas canvas, CharSequence charSequence, int i5, int i6, float f, int i7, int i8, int i9, Paint paint) {
                int i10 = i9 + i7;
                int i11 = this.measuredSize;
                MessageContainsEmojiButton.this.emojiDrawableBounds.set((int) f, (i10 - i11) / 2, (int) (f + i11), (i10 + i11) / 2);
            }
        }, 0, spannableString.length(), 33);
        AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(i, 0, document);
        this.emojiDrawable = make;
        make.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider), PorterDuff.Mode.SRC_IN));
        this.emojiDrawable.addView(this);
        SpannableString spannableString2 = new SpannableString(str);
        spannableString2.setSpan(new BoldAndAccent(), 0, spannableString2.length(), 33);
        this.mainText = new SpannableStringBuilder().append((CharSequence) split[0]).append((CharSequence) spannableString).append(' ').append((CharSequence) spannableString2).append((CharSequence) split[1]);
        this.loadT = 1.0f;
        this.inputStickerSet = null;
    }

    public void lambda$didReceivedNotification$0(boolean z, ValueAnimator valueAnimator) {
        this.loadT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (z) {
            requestLayout();
        }
    }

    private int updateLayout(int i, boolean z) {
        float f;
        if (i <= 0) {
            return 0;
        }
        CharSequence charSequence = this.mainText;
        if (charSequence != this.lastMainTextText || this.lastMainTextWidth != i) {
            if (charSequence != null) {
                CharSequence charSequence2 = this.mainText;
                StaticLayout staticLayout = new StaticLayout(charSequence2, 0, charSequence2.length(), this.textPaint, Math.max(i, 0), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.mainTextLayout = staticLayout;
                if (this.loadingDrawable != null && this.loadingBoundsTo == null) {
                    int lineCount = staticLayout.getLineCount() - 1;
                    this.lastLineMargin = ((int) this.mainTextLayout.getPrimaryHorizontal(this.mainText.length())) + AndroidUtilities.dp(2.0f);
                    this.lastLineTop = this.mainTextLayout.getLineTop(lineCount);
                    this.lastLineHeight = r1 - this.lastLineTop;
                    float min = Math.min(AndroidUtilities.dp(100.0f), this.mainTextLayout.getWidth() - this.lastLineMargin);
                    if (this.loadingBoundsFrom == null) {
                        this.loadingBoundsFrom = new android.graphics.Rect();
                    }
                    android.graphics.Rect rect = this.loadingBoundsFrom;
                    int i2 = this.lastLineMargin;
                    rect.set(i2, this.lastLineTop, (int) (i2 + min), r1);
                    this.loadingDrawable.setBounds(this.loadingBoundsFrom);
                    this.loadingDrawableBoundsSet = true;
                }
            } else {
                this.mainTextLayout = null;
                this.loadingDrawableBoundsSet = false;
            }
            this.lastMainTextText = this.mainText;
            this.lastMainTextWidth = i;
        }
        CharSequence charSequence3 = this.secondPartText;
        if (charSequence3 != this.lastSecondPartText || this.lastSecondPartTextWidth != i) {
            if (charSequence3 != null) {
                CharSequence charSequence4 = this.secondPartText;
                this.secondPartTextLayout = new StaticLayout(charSequence4, 0, charSequence4.length(), this.textPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.secondPartTextLayout = null;
            }
            this.lastSecondPartText = this.secondPartText;
            this.lastSecondPartTextWidth = i;
        }
        StaticLayout staticLayout2 = this.mainTextLayout;
        int height = staticLayout2 != null ? staticLayout2.getHeight() : 0;
        if (this.secondPartTextLayout != null) {
            f = (r1.getHeight() - this.lastLineHeight) * (z ? 1.0f : this.loadT);
        } else {
            f = 0.0f;
        }
        return height + ((int) f);
    }

    @Override
    public void didReceivedNotification(int r9, int r10, java.lang.Object... r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessageContainsEmojiButton.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.addView(this);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        android.graphics.Rect rect;
        super.onDraw(canvas);
        if (this.mainTextLayout != null) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            this.textPaint.setAlpha(255);
            this.mainTextLayout.draw(canvas);
            LoadingDrawable loadingDrawable = this.loadingDrawable;
            if (loadingDrawable != null && this.loadingDrawableBoundsSet) {
                loadingDrawable.setAlpha((int) ((1.0f - this.loadT) * 255.0f));
                android.graphics.Rect rect2 = this.loadingBoundsFrom;
                if (rect2 != null && (rect = this.loadingBoundsTo) != null) {
                    float f = this.loadT;
                    android.graphics.Rect rect3 = AndroidUtilities.rectTmp2;
                    AndroidUtilities.lerp(rect2, rect, f, rect3);
                    this.loadingDrawable.setBounds(rect3);
                }
                this.loadingDrawable.draw(canvas);
                invalidate();
            }
            if (this.secondPartTextLayout != null) {
                canvas.save();
                canvas.translate(0.0f, this.lastLineTop);
                this.textPaint.setAlpha((int) (this.loadT * 255.0f));
                this.secondPartTextLayout.draw(canvas);
                canvas.restore();
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.emojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.setAlpha((int) (this.loadT * 255.0f));
                this.emojiDrawable.setBounds(this.emojiDrawableBounds);
                this.emojiDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3;
        setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        int size = View.MeasureSpec.getSize(i);
        if (this.checkWidth && (i3 = this.lastWidth) > 0) {
            size = Math.min(size, i3);
        }
        this.lastWidth = size;
        int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
        if (paddingLeft < 0) {
            paddingLeft = 0;
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(updateLayout(paddingLeft, false) + getPaddingTop() + getPaddingBottom(), 1073741824));
    }
}
