package org.telegram.ui.Components.Premium.boosts.cells.msg;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.StateSet;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.Premium.boosts.BoostDialogs;
import org.telegram.ui.Components.StaticLayoutEx;

public class GiveawayMessageCell {
    private static final Map monthsToEmoticon;
    private int additionPrizeHeight;
    private StaticLayout additionPrizeLayout;
    private AvatarDrawable[] avatarDrawables;
    private ImageReceiver[] avatarImageReceivers;
    private boolean[] avatarVisible;
    private int bottomHeight;
    private StaticLayout bottomLayout;
    private Paint chatBgPaint;
    private RectF chatRect;
    private TextPaint chatTextPaint;
    private float[] chatTitleWidths;
    private CharSequence[] chatTitles;
    private TLRPC.Chat[] chats;
    private Rect[] clickRect;
    private Paint clipRectPaint;
    private Rect containerRect;
    private RectF countRect;
    private Paint counterBgPaint;
    private Drawable counterIcon;
    private TextPaint counterStarsTextPaint;
    private String counterStr;
    private Rect counterTextBounds;
    private TextPaint counterTextPaint;
    private int countriesHeight;
    private StaticLayout countriesLayout;
    private TextPaint countriesTextPaint;
    private int diffTextWidth;
    private ImageReceiver giftReceiver;
    private boolean isStars;
    private Paint lineDividerPaint;
    private MessageObject messageObject;
    private boolean[] needNewRow;
    private final ChatMessageCell parentView;
    private int[] pressedState;
    private Paint saveLayerPaint;
    private int selectorColor;
    private Drawable selectorDrawable;
    private String textDivider;
    private TextPaint textDividerPaint;
    private float textDividerWidth;
    private TextPaint textPaint;
    private int titleHeight;
    private StaticLayout titleLayout;
    private int topHeight;
    private StaticLayout topLayout;
    private int measuredHeight = 0;
    private int measuredWidth = 0;
    private int pressedPos = -1;
    private boolean isButtonPressed = false;
    private boolean isContainerPressed = false;

    static {
        HashMap hashMap = new HashMap();
        monthsToEmoticon = hashMap;
        hashMap.put(1, "1⃣");
        hashMap.put(3, "2⃣");
        hashMap.put(6, "3⃣");
        hashMap.put(12, "4⃣");
        hashMap.put(24, "5⃣");
    }

    public GiveawayMessageCell(ChatMessageCell chatMessageCell) {
        this.parentView = chatMessageCell;
    }

    private void checkArraysLimits(int i) {
        ImageReceiver[] imageReceiverArr = this.avatarImageReceivers;
        if (imageReceiverArr.length < i) {
            int length = imageReceiverArr.length;
            this.avatarImageReceivers = (ImageReceiver[]) Arrays.copyOf(imageReceiverArr, i);
            this.avatarDrawables = (AvatarDrawable[]) Arrays.copyOf(this.avatarDrawables, i);
            this.avatarVisible = Arrays.copyOf(this.avatarVisible, i);
            this.chatTitles = (CharSequence[]) Arrays.copyOf(this.chatTitles, i);
            this.chatTitleWidths = Arrays.copyOf(this.chatTitleWidths, i);
            this.needNewRow = Arrays.copyOf(this.needNewRow, i);
            this.clickRect = (Rect[]) Arrays.copyOf(this.clickRect, i);
            this.chats = (TLRPC.Chat[]) Arrays.copyOf(this.chats, i);
            for (int i2 = length - 1; i2 < i; i2++) {
                this.avatarImageReceivers[i2] = new ImageReceiver(this.parentView);
                this.avatarImageReceivers[i2].setAllowLoadingOnAttachedOnly(true);
                this.avatarImageReceivers[i2].setRoundRadius(AndroidUtilities.dp(12.0f));
                this.avatarDrawables[i2] = new AvatarDrawable();
                this.avatarDrawables[i2].setTextSize(AndroidUtilities.dp(18.0f));
                this.clickRect[i2] = new Rect();
            }
        }
    }

    private void createImages() {
        if (this.avatarImageReceivers != null) {
            return;
        }
        this.avatarImageReceivers = new ImageReceiver[10];
        this.avatarDrawables = new AvatarDrawable[10];
        this.avatarVisible = new boolean[10];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.avatarImageReceivers;
            if (i >= imageReceiverArr.length) {
                return;
            }
            imageReceiverArr[i] = new ImageReceiver(this.parentView);
            this.avatarImageReceivers[i].setAllowLoadingOnAttachedOnly(true);
            this.avatarImageReceivers[i].setRoundRadius(AndroidUtilities.dp(12.0f));
            this.avatarDrawables[i] = new AvatarDrawable();
            this.avatarDrawables[i].setTextSize(AndroidUtilities.dp(18.0f));
            this.clickRect[i] = new Rect();
            i++;
        }
    }

    private int getChatColor(TLRPC.Chat chat, Theme.ResourcesProvider resourcesProvider) {
        int i;
        if (this.messageObject.isOutOwner()) {
            return Theme.getColor(Theme.key_chat_outPreviewInstantText, resourcesProvider);
        }
        int colorId = ChatObject.getColorId(chat);
        if (colorId < 7) {
            i = Theme.keys_avatar_nameInMessage[colorId];
        } else {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(UserConfig.selectedAccount).peerColors;
            MessagesController.PeerColor color = peerColors == null ? null : peerColors.getColor(colorId);
            if (color != null) {
                return color.getColor(0, resourcesProvider);
            }
            i = Theme.keys_avatar_nameInMessage[0];
        }
        return Theme.getColor(i, resourcesProvider);
    }

    private void init() {
        if (this.counterTextPaint != null) {
            return;
        }
        this.counterTextPaint = new TextPaint(1);
        this.counterStarsTextPaint = new TextPaint(1);
        this.chatTextPaint = new TextPaint(1);
        this.textPaint = new TextPaint(1);
        this.textDividerPaint = new TextPaint(1);
        this.lineDividerPaint = new Paint(1);
        this.countriesTextPaint = new TextPaint(1);
        this.counterBgPaint = new Paint(1);
        this.chatBgPaint = new Paint(1);
        this.saveLayerPaint = new Paint();
        this.clipRectPaint = new Paint();
        this.countRect = new RectF();
        this.chatRect = new RectF();
        this.counterTextBounds = new Rect();
        this.containerRect = new Rect();
        this.pressedState = new int[]{16842910, 16842919};
        this.chatTitles = new CharSequence[10];
        this.chats = new TLRPC.Chat[10];
        this.chatTitleWidths = new float[10];
        this.needNewRow = new boolean[10];
        this.clickRect = new Rect[10];
        ImageReceiver imageReceiver = new ImageReceiver(this.parentView);
        this.giftReceiver = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
        Paint paint = this.clipRectPaint;
        PorterDuff.Mode mode = PorterDuff.Mode.DST_OUT;
        paint.setXfermode(new PorterDuffXfermode(mode));
        this.counterTextPaint.setTypeface(AndroidUtilities.bold());
        this.counterTextPaint.setXfermode(new PorterDuffXfermode(mode));
        this.counterTextPaint.setTextSize(AndroidUtilities.dp(12.0f));
        TextPaint textPaint = this.counterTextPaint;
        Paint.Align align = Paint.Align.CENTER;
        textPaint.setTextAlign(align);
        this.counterStarsTextPaint.setTypeface(AndroidUtilities.bold());
        this.counterStarsTextPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.counterStarsTextPaint.setTextAlign(align);
        this.counterStarsTextPaint.setColor(-1);
        this.chatTextPaint.setTypeface(AndroidUtilities.bold());
        this.chatTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.countriesTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.textDividerPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.textDividerPaint.setTextAlign(align);
    }

    private void setGiftImage(MessageObject messageObject) {
        TLRPC.TL_messageMediaGiveaway tL_messageMediaGiveaway = (TLRPC.TL_messageMediaGiveaway) messageObject.messageOwner.media;
        String str = UserConfig.getInstance(UserConfig.selectedAccount).premiumGiftsStickerPack;
        if (str == null) {
            MediaDataController.getInstance(UserConfig.selectedAccount).checkPremiumGiftStickers();
            return;
        }
        TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSetByName(str);
        if (stickerSetByName == null) {
            stickerSetByName = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSetByEmojiOrName(str);
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSetByName;
        TLRPC.Document document = null;
        if (tL_messages_stickerSet != null) {
            String str2 = (String) monthsToEmoticon.get(Integer.valueOf(tL_messageMediaGiveaway.months));
            Iterator<TLRPC.TL_stickerPack> it = tL_messages_stickerSet.packs.iterator();
            while (it.hasNext()) {
                TLRPC.TL_stickerPack next = it.next();
                if (Objects.equals(next.emoticon, str2)) {
                    Iterator<Long> it2 = next.documents.iterator();
                    while (it2.hasNext()) {
                        long longValue = it2.next().longValue();
                        Iterator<TLRPC.Document> it3 = tL_messages_stickerSet.documents.iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                break;
                            }
                            TLRPC.Document next2 = it3.next();
                            if (next2.id == longValue) {
                                document = next2;
                                break;
                            }
                        }
                        if (document != null) {
                            break;
                        }
                    }
                }
                if (document != null) {
                    break;
                }
            }
            if (document == null && !tL_messages_stickerSet.documents.isEmpty()) {
                document = tL_messages_stickerSet.documents.get(0);
            }
        }
        if (document == null) {
            MediaDataController.getInstance(UserConfig.selectedAccount).loadStickersByEmojiOrName(str, false, tL_messages_stickerSet == null);
            return;
        }
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document.thumbs, Theme.key_emptyListPlaceholder, 0.2f);
        if (svgThumb != null) {
            svgThumb.overrideWidthAndHeight(512, 512);
        }
        this.giftReceiver.setImage(ImageLocation.getForDocument(document), "160_160_firstframe", svgThumb, "tgs", tL_messages_stickerSet, 1);
    }

    public boolean checkMotionEvent(MotionEvent motionEvent) {
        MessageObject messageObject = this.messageObject;
        if (messageObject != null && messageObject.isGiveaway()) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 0) {
                int i = 0;
                while (true) {
                    Rect[] rectArr = this.clickRect;
                    if (i < rectArr.length) {
                        if (rectArr[i].contains(x, y)) {
                            this.pressedPos = i;
                            if (Build.VERSION.SDK_INT >= 21) {
                                this.selectorDrawable.setHotspot(x, y);
                            }
                            this.isButtonPressed = true;
                            setButtonPressed(true);
                            return true;
                        }
                        i++;
                    } else if (this.containerRect.contains(x, y)) {
                        this.isContainerPressed = true;
                        return true;
                    }
                }
            } else if (motionEvent.getAction() == 1) {
                if (this.isButtonPressed) {
                    if (this.parentView.getDelegate() != null) {
                        this.parentView.getDelegate().didPressGiveawayChatButton(this.parentView, this.pressedPos);
                    }
                    this.parentView.playSoundEffect(0);
                    setButtonPressed(false);
                    this.isButtonPressed = false;
                }
                if (this.isContainerPressed) {
                    this.isContainerPressed = false;
                    BoostDialogs.showBulletinAbout(this.messageObject);
                }
            } else if (motionEvent.getAction() != 2 && motionEvent.getAction() == 3) {
                if (this.isButtonPressed) {
                    setButtonPressed(false);
                }
                this.isButtonPressed = false;
                this.isContainerPressed = false;
            }
        }
        return false;
    }

    public void draw(Canvas canvas, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        Paint paint;
        int i3;
        float f;
        boolean[] zArr;
        int i4;
        MessageObject messageObject = this.messageObject;
        if (messageObject == null || !messageObject.isGiveaway()) {
            return;
        }
        if (this.selectorDrawable == null) {
            int color = Theme.getColor(Theme.key_listSelector);
            this.selectorColor = color;
            Drawable createRadSelectorDrawable = Theme.createRadSelectorDrawable(color, 12, 12);
            this.selectorDrawable = createRadSelectorDrawable;
            createRadSelectorDrawable.setCallback(this.parentView);
        }
        this.textPaint.setColor(Theme.chat_msgTextPaint.getColor());
        this.textDividerPaint.setColor(Theme.multAlpha(Theme.chat_msgTextPaint.getColor(), 0.45f));
        this.lineDividerPaint.setColor(Theme.multAlpha(Theme.chat_msgTextPaint.getColor(), 0.15f));
        this.countriesTextPaint.setColor(Theme.chat_msgTextPaint.getColor());
        if (this.messageObject.isOutOwner()) {
            TextPaint textPaint = this.chatTextPaint;
            int i5 = Theme.key_chat_outPreviewInstantText;
            textPaint.setColor(Theme.getColor(i5, resourcesProvider));
            this.counterBgPaint.setColor(Theme.getColor(i5, resourcesProvider));
            paint = this.chatBgPaint;
            i3 = Theme.key_chat_outReplyLine;
        } else {
            TextPaint textPaint2 = this.chatTextPaint;
            int i6 = Theme.key_chat_inPreviewInstantText;
            textPaint2.setColor(Theme.getColor(i6, resourcesProvider));
            this.counterBgPaint.setColor(Theme.getColor(i6, resourcesProvider));
            paint = this.chatBgPaint;
            i3 = Theme.key_chat_inReplyLine;
        }
        paint.setColor(Theme.getColor(i3, resourcesProvider));
        if (this.isStars) {
            this.counterBgPaint.setColor(Theme.getColor(Theme.key_starsGradient1, resourcesProvider));
        }
        canvas.save();
        int dp = i2 - AndroidUtilities.dp(4.0f);
        canvas.translate(dp, i);
        this.containerRect.set(dp, i, getMeasuredWidth() + dp, getMeasuredHeight() + i);
        canvas.saveLayer(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.saveLayerPaint, 31);
        this.giftReceiver.draw(canvas);
        float measuredWidth = getMeasuredWidth() / 2.0f;
        float dp2 = AndroidUtilities.dp(106.0f);
        int width = this.counterTextBounds.width() + AndroidUtilities.dp(12.0f);
        int height = this.counterTextBounds.height() + AndroidUtilities.dp(10.0f);
        this.countRect.set(measuredWidth - ((AndroidUtilities.dp(2.0f) + width) / 2.0f), dp2 - ((AndroidUtilities.dp(2.0f) + height) / 2.0f), ((width + AndroidUtilities.dp(2.0f)) / 2.0f) + measuredWidth, ((height + AndroidUtilities.dp(2.0f)) / 2.0f) + dp2);
        canvas.drawRoundRect(this.countRect, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), this.clipRectPaint);
        float f2 = width / 2.0f;
        float f3 = height / 2.0f;
        this.countRect.set(measuredWidth - f2, dp2 - f3, f2 + measuredWidth, dp2 + f3);
        canvas.drawRoundRect(this.countRect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.counterBgPaint);
        Drawable drawable = this.counterIcon;
        if (drawable != null) {
            drawable.setBounds(((int) this.countRect.left) + AndroidUtilities.dp(5.0f), ((int) this.countRect.centerY()) - AndroidUtilities.dp(6.96f), ((int) this.countRect.left) + AndroidUtilities.dp(21.24f), ((int) this.countRect.centerY()) + AndroidUtilities.dp(6.96f));
            this.counterIcon.draw(canvas);
        }
        canvas.drawText(this.counterStr, this.countRect.centerX() + AndroidUtilities.dp(this.isStars ? 8.0f : 0.0f), this.countRect.centerY() + AndroidUtilities.dp(4.0f), this.isStars ? this.counterStarsTextPaint : this.counterTextPaint);
        canvas.restore();
        canvas.translate(0.0f, AndroidUtilities.dp(128.0f));
        int dp3 = i + AndroidUtilities.dp(128.0f);
        canvas.save();
        canvas.translate(this.diffTextWidth / 2.0f, 0.0f);
        this.titleLayout.draw(canvas);
        canvas.translate(0.0f, this.titleHeight);
        float f4 = 16.0f;
        if (this.additionPrizeLayout != null) {
            canvas.restore();
            canvas.save();
            float dp4 = (this.titleHeight + this.additionPrizeHeight) - AndroidUtilities.dp(6.0f);
            float f5 = this.measuredWidth / 2.0f;
            canvas.drawText(this.textDivider, f5, dp4, this.textDividerPaint);
            canvas.drawLine(AndroidUtilities.dp(17.0f), dp4 - AndroidUtilities.dp(4.0f), (f5 - (this.textDividerWidth / 2.0f)) - AndroidUtilities.dp(6.0f), dp4 - AndroidUtilities.dp(4.0f), this.lineDividerPaint);
            canvas.drawLine(f5 + (this.textDividerWidth / 2.0f) + AndroidUtilities.dp(6.0f), dp4 - AndroidUtilities.dp(4.0f), this.measuredWidth - AndroidUtilities.dp(16.0f), dp4 - AndroidUtilities.dp(4.0f), this.lineDividerPaint);
            canvas.translate((this.measuredWidth - this.additionPrizeLayout.getWidth()) / 2.0f, this.titleHeight);
            this.additionPrizeLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.translate(this.diffTextWidth / 2.0f, this.additionPrizeHeight + this.titleHeight);
        }
        this.topLayout.draw(canvas);
        canvas.restore();
        canvas.translate(0.0f, this.topHeight + AndroidUtilities.dp(6.0f));
        int i7 = 0;
        int dp5 = dp3 + this.topHeight + AndroidUtilities.dp(6.0f);
        int i8 = 0;
        while (true) {
            boolean[] zArr2 = this.avatarVisible;
            if (i7 >= zArr2.length) {
                break;
            }
            if (zArr2[i7]) {
                canvas.save();
                int i9 = i7;
                float f6 = 0.0f;
                do {
                    f6 += this.chatTitleWidths[i9] + AndroidUtilities.dp(40.0f);
                    i9++;
                    zArr = this.avatarVisible;
                    if (i9 >= zArr.length || this.needNewRow[i9]) {
                        break;
                    }
                } while (zArr[i9]);
                float f7 = measuredWidth - (f6 / 2.0f);
                canvas.translate(f7, 0.0f);
                int i10 = i7;
                int i11 = ((int) f7) + dp;
                while (true) {
                    int chatColor = getChatColor(this.chats[i10], resourcesProvider);
                    int i12 = this.pressedPos;
                    i4 = (i12 < 0 || i12 != i10) ? i8 : chatColor;
                    this.chatTextPaint.setColor(chatColor);
                    this.chatBgPaint.setColor(chatColor);
                    this.chatBgPaint.setAlpha(25);
                    this.avatarImageReceivers[i10].draw(canvas);
                    CharSequence charSequence = this.chatTitles[i10];
                    int i13 = i11;
                    int i14 = i10;
                    canvas.drawText(charSequence, 0, charSequence.length(), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(f4), this.chatTextPaint);
                    this.chatRect.set(0.0f, 0.0f, this.chatTitleWidths[i14] + AndroidUtilities.dp(40.0f), AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.chatRect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), this.chatBgPaint);
                    float f8 = i13;
                    this.clickRect[i14].set(i13, dp5, (int) (this.chatRect.width() + f8), AndroidUtilities.dp(24.0f) + dp5);
                    canvas.translate(this.chatRect.width() + AndroidUtilities.dp(6.0f), 0.0f);
                    i11 = (int) (f8 + this.chatRect.width() + AndroidUtilities.dp(6.0f));
                    i10 = i14 + 1;
                    boolean[] zArr3 = this.avatarVisible;
                    if (i10 >= zArr3.length || this.needNewRow[i10] || !zArr3[i10]) {
                        break;
                    }
                    i8 = i4;
                    f4 = 16.0f;
                }
                canvas.restore();
                canvas.translate(0.0f, AndroidUtilities.dp(30.0f));
                dp5 += AndroidUtilities.dp(30.0f);
                i7 = i10;
                i8 = i4;
            } else {
                i7++;
            }
            f4 = 16.0f;
        }
        if (this.countriesLayout != null) {
            canvas.save();
            canvas.translate((this.measuredWidth - this.countriesLayout.getWidth()) / 2.0f, AndroidUtilities.dp(4.0f));
            this.countriesLayout.draw(canvas);
            canvas.restore();
            f = 0.0f;
            canvas.translate(0.0f, this.countriesHeight);
        } else {
            f = 0.0f;
        }
        canvas.translate(f, AndroidUtilities.dp(6.0f));
        canvas.save();
        canvas.translate(this.diffTextWidth / 2.0f, f);
        this.bottomLayout.draw(canvas);
        canvas.restore();
        canvas.restore();
        if (this.pressedPos >= 0) {
            int multAlpha = Theme.multAlpha(i8, Theme.isCurrentThemeDark() ? 0.12f : 0.1f);
            if (this.selectorColor != multAlpha) {
                Drawable drawable2 = this.selectorDrawable;
                this.selectorColor = multAlpha;
                Theme.setSelectorDrawableColor(drawable2, multAlpha, true);
            }
            this.selectorDrawable.setBounds(this.clickRect[this.pressedPos]);
            this.selectorDrawable.setCallback(this.parentView);
        }
    }

    public int getMeasuredHeight() {
        return this.measuredHeight;
    }

    public int getMeasuredWidth() {
        return this.measuredWidth;
    }

    public void onAttachedToWindow() {
        ImageReceiver imageReceiver = this.giftReceiver;
        if (imageReceiver != null) {
            imageReceiver.onAttachedToWindow();
        }
        ImageReceiver[] imageReceiverArr = this.avatarImageReceivers;
        if (imageReceiverArr != null) {
            for (ImageReceiver imageReceiver2 : imageReceiverArr) {
                imageReceiver2.onAttachedToWindow();
            }
        }
    }

    public void onDetachedFromWindow() {
        ImageReceiver imageReceiver = this.giftReceiver;
        if (imageReceiver != null) {
            imageReceiver.onDetachedFromWindow();
        }
        ImageReceiver[] imageReceiverArr = this.avatarImageReceivers;
        if (imageReceiverArr != null) {
            for (ImageReceiver imageReceiver2 : imageReceiverArr) {
                imageReceiver2.onDetachedFromWindow();
            }
        }
    }

    public void setButtonPressed(boolean z) {
        Drawable drawable;
        MessageObject messageObject = this.messageObject;
        if (messageObject == null || !messageObject.isGiveaway() || (drawable = this.selectorDrawable) == null) {
            return;
        }
        if (z) {
            drawable.setCallback(new Drawable.Callback() {
                @Override
                public void invalidateDrawable(Drawable drawable2) {
                    GiveawayMessageCell.this.parentView.invalidate();
                }

                @Override
                public void scheduleDrawable(Drawable drawable2, Runnable runnable, long j) {
                    GiveawayMessageCell.this.parentView.invalidate();
                }

                @Override
                public void unscheduleDrawable(Drawable drawable2, Runnable runnable) {
                    GiveawayMessageCell.this.parentView.invalidate();
                }
            });
            this.selectorDrawable.setState(this.pressedState);
        } else {
            drawable.setState(StateSet.NOTHING);
        }
        this.parentView.invalidate();
    }

    public void setMessageContent(MessageObject messageObject, int i, int i2) {
        String str;
        this.messageObject = null;
        this.titleLayout = null;
        this.additionPrizeLayout = null;
        this.topLayout = null;
        this.bottomLayout = null;
        this.countriesLayout = null;
        this.measuredHeight = 0;
        this.measuredWidth = 0;
        this.additionPrizeHeight = 0;
        this.textDividerWidth = 0.0f;
        if (messageObject.isGiveaway()) {
            this.messageObject = messageObject;
            init();
            createImages();
            setGiftImage(messageObject);
            TLRPC.TL_messageMediaGiveaway tL_messageMediaGiveaway = (TLRPC.TL_messageMediaGiveaway) messageObject.messageOwner.media;
            this.isStars = (tL_messageMediaGiveaway.flags & 32) != 0;
            checkArraysLimits(tL_messageMediaGiveaway.channels.size());
            int dp = AndroidUtilities.dp(148.0f);
            int minTabletSide = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(80.0f) : i - AndroidUtilities.dp(80.0f);
            MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
            boolean isForwarded = messageObject.isForwarded();
            TLRPC.Message message = messageObject.messageOwner;
            boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(messagesController.getChat(Long.valueOf(-MessageObject.getPeerId(isForwarded ? message.fwd_from.from_id : message.peer_id))));
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.BoostingGiveawayPrizes));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replaceTags);
            spannableStringBuilder.setSpan(new RelativeSizeSpan(1.05f), 0, replaceTags.length(), 33);
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
            if (this.isStars) {
                spannableStringBuilder2.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatPluralStringComma("BoostingStarsGiveawayMsgInfoPlural1", (int) tL_messageMediaGiveaway.stars)));
                spannableStringBuilder2.append((CharSequence) "\n");
                spannableStringBuilder2.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatPluralString("BoostingStarsGiveawayMsgInfoPlural2", tL_messageMediaGiveaway.quantity, new Object[0])));
            } else {
                spannableStringBuilder2.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatPluralStringComma("BoostingGiveawayMsgInfoPlural1", tL_messageMediaGiveaway.quantity)));
                spannableStringBuilder2.append((CharSequence) "\n");
                spannableStringBuilder2.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatPluralString("BoostingGiveawayMsgInfoPlural2", tL_messageMediaGiveaway.quantity, LocaleController.formatPluralString("BoldMonths", tL_messageMediaGiveaway.months, new Object[0]))));
            }
            SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
            spannableStringBuilder3.append((CharSequence) spannableStringBuilder2);
            spannableStringBuilder3.append((CharSequence) "\n\n");
            spannableStringBuilder3.setSpan(new RelativeSizeSpan(0.4f), spannableStringBuilder3.length() - 1, spannableStringBuilder3.length(), 33);
            SpannableStringBuilder replaceTags2 = AndroidUtilities.replaceTags(LocaleController.getString("BoostingGiveawayMsgParticipants", R.string.BoostingGiveawayMsgParticipants));
            spannableStringBuilder3.append((CharSequence) replaceTags2);
            spannableStringBuilder3.setSpan(new RelativeSizeSpan(1.05f), spannableStringBuilder2.length() + 2, spannableStringBuilder2.length() + 2 + replaceTags2.length(), 33);
            spannableStringBuilder3.append((CharSequence) "\n");
            spannableStringBuilder3.append((CharSequence) (tL_messageMediaGiveaway.only_new_subscribers ? LocaleController.formatPluralString(isChannelAndNotMegaGroup ? "BoostingGiveawayMsgNewSubsPlural" : "BoostingGiveawayMsgNewSubsGroupPlural", tL_messageMediaGiveaway.channels.size(), new Object[0]) : LocaleController.formatPluralString(isChannelAndNotMegaGroup ? "BoostingGiveawayMsgAllSubsPlural" : "BoostingGiveawayMsgAllSubsGroupPlural", tL_messageMediaGiveaway.channels.size(), new Object[0])));
            SpannableStringBuilder replaceTags3 = AndroidUtilities.replaceTags(LocaleController.getString("BoostingWinnersDate", R.string.BoostingWinnersDate));
            SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(replaceTags3);
            spannableStringBuilder4.setSpan(new RelativeSizeSpan(1.05f), 0, replaceTags3.length(), 33);
            Date date = new Date(tL_messageMediaGiveaway.until_date * 1000);
            String format = LocaleController.getInstance().getFormatterGiveawayCard().format(date);
            String format2 = LocaleController.getInstance().getFormatterDay().format(date);
            spannableStringBuilder4.append((CharSequence) "\n");
            spannableStringBuilder4.append((CharSequence) LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, format, format2));
            TextPaint textPaint = this.textPaint;
            Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
            float dp2 = AndroidUtilities.dp(2.0f);
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            int i3 = minTabletSide;
            int i4 = minTabletSide;
            this.titleLayout = StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i3, alignment, 1.0f, dp2, false, truncateAt, i4, 10);
            this.topLayout = StaticLayoutEx.createStaticLayout(spannableStringBuilder3, this.textPaint, i3, alignment, 1.0f, AndroidUtilities.dp(2.0f), false, truncateAt, i4, 10);
            this.bottomLayout = StaticLayoutEx.createStaticLayout(spannableStringBuilder4, this.textPaint, i3, alignment, 1.0f, AndroidUtilities.dp(3.0f), false, truncateAt, i4, 10);
            int i5 = 0;
            for (int i6 = 0; i6 < this.titleLayout.getLineCount(); i6++) {
                i5 = (int) Math.max(i5, Math.ceil(this.titleLayout.getLineWidth(i6)));
            }
            for (int i7 = 0; i7 < this.topLayout.getLineCount(); i7++) {
                i5 = (int) Math.max(i5, Math.ceil(this.topLayout.getLineWidth(i7)));
            }
            for (int i8 = 0; i8 < this.bottomLayout.getLineCount(); i8++) {
                i5 = (int) Math.max(i5, Math.ceil(this.bottomLayout.getLineWidth(i8)));
            }
            if (i5 < AndroidUtilities.dp(180.0f)) {
                i5 = AndroidUtilities.dp(180.0f);
            }
            String str2 = tL_messageMediaGiveaway.prize_description;
            if (str2 != null && !str2.isEmpty()) {
                StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatPluralString("BoostingGiveawayMsgPrizes", tL_messageMediaGiveaway.quantity, tL_messageMediaGiveaway.prize_description)), this.countriesTextPaint.getFontMetricsInt(), false), this.textPaint, i5, Layout.Alignment.ALIGN_CENTER, 1.0f, AndroidUtilities.dp(2.0f), false, TextUtils.TruncateAt.END, i5, 20);
                this.additionPrizeLayout = createStaticLayout;
                this.additionPrizeHeight = createStaticLayout.getLineBottom(createStaticLayout.getLineCount() - 1) + AndroidUtilities.dp(22.0f);
                String string = LocaleController.getString(R.string.BoostingGiveawayMsgWithDivider);
                this.textDivider = string;
                this.textDividerWidth = this.textDividerPaint.measureText(string, 0, string.length());
            }
            if (tL_messageMediaGiveaway.countries_iso2.size() > 0) {
                ArrayList arrayList = new ArrayList();
                Iterator<String> it = tL_messageMediaGiveaway.countries_iso2.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    String displayCountry = new Locale("", next).getDisplayCountry(Locale.getDefault());
                    String languageFlag = LocaleController.getLanguageFlag(next);
                    SpannableStringBuilder spannableStringBuilder5 = new SpannableStringBuilder();
                    if (languageFlag != null) {
                        spannableStringBuilder5.append((CharSequence) languageFlag).append((CharSequence) " ");
                    }
                    spannableStringBuilder5.append((CharSequence) displayCountry);
                    arrayList.add(spannableStringBuilder5);
                }
                if (!arrayList.isEmpty()) {
                    this.countriesLayout = StaticLayoutEx.createStaticLayout(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatString("BoostingGiveAwayFromCountries", R.string.BoostingGiveAwayFromCountries, TextUtils.join(", ", arrayList))), this.countriesTextPaint.getFontMetricsInt(), false), this.countriesTextPaint, i5, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, i5, 10);
                }
            }
            int max = Math.max(i2, Math.min(i5 + AndroidUtilities.dp(38.0f), minTabletSide));
            this.diffTextWidth = max - minTabletSide;
            float f = max;
            float f2 = dp;
            float f3 = f2 / 2.0f;
            this.giftReceiver.setImageCoords((f / 2.0f) - f3, AndroidUtilities.dp(42.0f) - f3, f2, f2);
            StaticLayout staticLayout = this.titleLayout;
            int lineBottom = staticLayout.getLineBottom(staticLayout.getLineCount() - 1) + AndroidUtilities.dp(5.0f);
            this.titleHeight = lineBottom;
            int i9 = lineBottom + this.additionPrizeHeight;
            StaticLayout staticLayout2 = this.topLayout;
            this.topHeight = i9 + staticLayout2.getLineBottom(staticLayout2.getLineCount() - 1);
            StaticLayout staticLayout3 = this.bottomLayout;
            this.bottomHeight = staticLayout3.getLineBottom(staticLayout3.getLineCount() - 1);
            StaticLayout staticLayout4 = this.countriesLayout;
            int lineBottom2 = staticLayout4 != null ? staticLayout4.getLineBottom(staticLayout4.getLineCount() - 1) + AndroidUtilities.dp(12.0f) : 0;
            this.countriesHeight = lineBottom2;
            int i10 = this.measuredHeight + this.topHeight + lineBottom2 + this.bottomHeight;
            this.measuredHeight = i10;
            this.measuredHeight = i10 + AndroidUtilities.dp(128.0f);
            this.measuredWidth = max;
            if (this.isStars) {
                if (this.counterIcon == null) {
                    this.counterIcon = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.filled_giveaway_stars).mutate();
                }
                str = LocaleController.formatNumber((int) tL_messageMediaGiveaway.stars, ',');
            } else {
                this.counterIcon = null;
                str = "x" + tL_messageMediaGiveaway.quantity;
            }
            this.counterStr = str;
            TextPaint textPaint2 = this.counterTextPaint;
            String str3 = this.counterStr;
            textPaint2.getTextBounds(str3, 0, str3.length(), this.counterTextBounds);
            if (tL_messageMediaGiveaway.stars != 0) {
                this.counterTextBounds.right += AndroidUtilities.dp(20.0f);
            }
            Arrays.fill(this.avatarVisible, false);
            this.measuredHeight += AndroidUtilities.dp(30.0f);
            ArrayList arrayList2 = new ArrayList(tL_messageMediaGiveaway.channels.size());
            Iterator<Long> it2 = tL_messageMediaGiveaway.channels.iterator();
            while (it2.hasNext()) {
                Long next2 = it2.next();
                if (MessagesController.getInstance(UserConfig.selectedAccount).getChat(next2) != null) {
                    arrayList2.add(next2);
                }
            }
            float f4 = 0.0f;
            for (int i11 = 0; i11 < arrayList2.size(); i11++) {
                Long l = (Long) arrayList2.get(i11);
                long longValue = l.longValue();
                TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(l);
                if (chat != null) {
                    this.avatarVisible[i11] = true;
                    this.chats[i11] = chat;
                    this.chatTitles[i11] = TextUtils.ellipsize(Emoji.replaceEmoji(chat.title, this.chatTextPaint.getFontMetricsInt(), false), this.chatTextPaint, 0.8f * f, TextUtils.TruncateAt.END);
                    float[] fArr = this.chatTitleWidths;
                    TextPaint textPaint3 = this.chatTextPaint;
                    CharSequence charSequence = this.chatTitles[i11];
                    fArr[i11] = textPaint3.measureText(charSequence, 0, charSequence.length());
                    float dp3 = this.chatTitleWidths[i11] + AndroidUtilities.dp(40.0f);
                    f4 += dp3;
                    if (i11 > 0) {
                        boolean[] zArr = this.needNewRow;
                        boolean z = f4 > 0.9f * f;
                        zArr[i11] = z;
                        if (z) {
                            this.measuredHeight += AndroidUtilities.dp(30.0f);
                            f4 = dp3;
                        }
                    } else {
                        this.needNewRow[i11] = false;
                    }
                    this.avatarDrawables[i11].setInfo(chat);
                    this.avatarImageReceivers[i11].setForUserOrChat(chat, this.avatarDrawables[i11]);
                    this.avatarImageReceivers[i11].setImageCoords(0.0f, 0.0f, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                } else {
                    this.chats[i11] = null;
                    this.avatarVisible[i11] = false;
                    this.chatTitles[i11] = "";
                    this.needNewRow[i11] = false;
                    this.chatTitleWidths[i11] = AndroidUtilities.dp(20.0f);
                    this.avatarDrawables[i11].setInfo(longValue, "", "");
                }
            }
        }
    }
}
