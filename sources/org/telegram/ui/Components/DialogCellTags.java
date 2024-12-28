package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.SpannableStringBuilder;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.Theme;

public class DialogCellTags {
    private final View parentView;
    private final ArrayList filters = new ArrayList();
    private final ArrayList tags = new ArrayList();
    private Tag moreTags = null;

    public static class Tag {
        int color;
        public int colorId;
        public int filterId;
        Text text;
        private int textHeight;
        int width;

        private Tag() {
        }

        public static Tag asMore(View view, int i) {
            Tag tag = new Tag();
            tag.filterId = i;
            tag.text = new Text("+" + i, 10.0f, AndroidUtilities.bold()).supportAnimatedEmojis(view);
            tag.width = AndroidUtilities.dp(9.32f) + ((int) tag.text.getCurrentWidth());
            tag.textHeight = (int) tag.text.getHeight();
            tag.color = Theme.getColor(Theme.key_avatar_nameInMessageBlue);
            return tag;
        }

        public static Tag fromFilter(View view, int i, MessagesController.DialogFilter dialogFilter) {
            Tag tag = new Tag();
            tag.filterId = dialogFilter.id;
            tag.colorId = dialogFilter.color;
            String str = dialogFilter.name;
            if (str == null) {
                str = "";
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str.toUpperCase());
            Text supportAnimatedEmojis = new Text(spannableStringBuilder, 10.0f, AndroidUtilities.bold()).supportAnimatedEmojis(view);
            tag.text = supportAnimatedEmojis;
            tag.text.setText(MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji(spannableStringBuilder, supportAnimatedEmojis.getFontMetricsInt(), false), dialogFilter.entities, tag.text.getFontMetricsInt()));
            tag.text.setEmojiCacheType(26);
            tag.width = AndroidUtilities.dp(9.32f) + ((int) tag.text.getCurrentWidth());
            tag.textHeight = (int) tag.text.getHeight();
            int[] iArr = Theme.keys_avatar_nameInMessage;
            tag.color = Theme.getColor(iArr[dialogFilter.color % iArr.length]);
            return tag;
        }

        public void draw(Canvas canvas) {
            Theme.dialogs_tagPaint.setColor(Theme.multAlpha(this.color, Theme.isCurrentThemeDark() ? 0.2f : 0.1f));
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, this.width, AndroidUtilities.dp(14.66f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.dialogs_tagPaint);
            this.text.draw(canvas, AndroidUtilities.dp(4.66f), AndroidUtilities.dp(14.66f) / 2.0f, this.color, 1.0f);
        }
    }

    public DialogCellTags(View view) {
        this.parentView = view;
    }

    public void draw(Canvas canvas, int i) {
        int dp;
        int dp2;
        int i2 = 0;
        canvas.clipRect(0, 0, i, AndroidUtilities.dp(14.66f));
        RectF rectF = AndroidUtilities.rectTmp;
        float f = i;
        rectF.set(0.0f, 0.0f, f, AndroidUtilities.dp(14.66f));
        canvas.saveLayerAlpha(rectF, 255, 31);
        if (LocaleController.isRTL) {
            canvas.translate(f, 0.0f);
        }
        int dp3 = i - AndroidUtilities.dp(25.0f);
        while (i2 < this.tags.size()) {
            Tag tag = (Tag) this.tags.get(i2);
            dp3 -= tag.width + AndroidUtilities.dp(4.0f);
            if (dp3 < 0) {
                break;
            }
            if (LocaleController.isRTL) {
                canvas.translate(-tag.width, 0.0f);
                tag.draw(canvas);
                dp2 = -AndroidUtilities.dp(4.0f);
            } else {
                tag.draw(canvas);
                dp2 = tag.width + AndroidUtilities.dp(4.0f);
            }
            canvas.translate(dp2, 0.0f);
            i2++;
        }
        if (i2 < this.tags.size()) {
            int size = this.tags.size() - i2;
            Tag tag2 = this.moreTags;
            if (tag2 == null || tag2.filterId != size) {
                this.moreTags = Tag.asMore(this.parentView, size);
            }
            if (LocaleController.isRTL) {
                canvas.translate(-this.moreTags.width, 0.0f);
                this.moreTags.draw(canvas);
                dp = -AndroidUtilities.dp(4.0f);
            } else {
                this.moreTags.draw(canvas);
                dp = this.moreTags.width + AndroidUtilities.dp(4.0f);
            }
            canvas.translate(dp, 0.0f);
        }
        canvas.restore();
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    public boolean update(int i, int i2, long j) {
        Tag tag;
        MessagesController.DialogFilter dialogFilter;
        String str;
        AccountInstance accountInstance = AccountInstance.getInstance(i);
        MessagesController messagesController = MessagesController.getInstance(i);
        if (!messagesController.folderTags || !accountInstance.getUserConfig().isPremium()) {
            boolean isEmpty = this.tags.isEmpty();
            this.tags.clear();
            return !isEmpty;
        }
        ArrayList<MessagesController.DialogFilter> arrayList = messagesController.dialogFilters;
        MessagesController.DialogFilter dialogFilter2 = i2 == 7 ? messagesController.selectedDialogFilter[0] : i2 == 8 ? messagesController.selectedDialogFilter[1] : null;
        this.filters.clear();
        if (i2 == 0 || i2 == 7 || i2 == 8) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                MessagesController.DialogFilter dialogFilter3 = arrayList.get(i3);
                if (dialogFilter3 != null && dialogFilter3 != dialogFilter2 && dialogFilter3.color >= 0 && dialogFilter3.includesDialog(accountInstance, j)) {
                    this.filters.add(dialogFilter3);
                }
            }
        }
        int i4 = 0;
        boolean z = false;
        while (i4 < this.tags.size()) {
            Tag tag2 = (Tag) this.tags.get(i4);
            int i5 = 0;
            while (true) {
                if (i5 >= this.filters.size()) {
                    dialogFilter = null;
                    break;
                }
                if (((MessagesController.DialogFilter) this.filters.get(i5)).id == tag2.filterId) {
                    dialogFilter = (MessagesController.DialogFilter) this.filters.get(i5);
                    break;
                }
                i5++;
            }
            if (dialogFilter == null) {
                this.tags.remove(i4);
                i4--;
            } else {
                if (dialogFilter.color != tag2.colorId || ((str = dialogFilter.name) != null && tag2.text != null && str.length() != tag2.text.getText().length())) {
                    this.tags.set(i4, Tag.fromFilter(this.parentView, i, dialogFilter));
                }
                i4++;
            }
            z = true;
            i4++;
        }
        for (int i6 = 0; i6 < this.filters.size(); i6++) {
            MessagesController.DialogFilter dialogFilter4 = (MessagesController.DialogFilter) this.filters.get(i6);
            int i7 = 0;
            while (true) {
                if (i7 >= this.tags.size()) {
                    tag = null;
                    break;
                }
                if (((Tag) this.tags.get(i7)).filterId == dialogFilter4.id) {
                    tag = (Tag) this.tags.get(i7);
                    break;
                }
                i7++;
            }
            if (tag == null) {
                this.tags.add(i6, Tag.fromFilter(this.parentView, i, dialogFilter4));
                z = true;
            }
        }
        this.filters.clear();
        return z;
    }
}
