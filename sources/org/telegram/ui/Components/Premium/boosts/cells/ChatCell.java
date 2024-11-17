package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ChatCell extends BaseCell {
    private TLRPC.Chat chat;
    private ChatDeleteListener chatDeleteListener;
    private final ImageView deleteImageView;
    private boolean removable;

    public interface ChatDeleteListener {
        void onChatDeleted(TLRPC.Chat chat);
    }

    public ChatCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.titleTextView.setTypeface(AndroidUtilities.bold());
        ImageView imageView = new ImageView(context);
        this.deleteImageView = imageView;
        imageView.setFocusable(false);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
        imageView.setImageResource(R.drawable.poll_remove);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        imageView.setContentDescription(LocaleController.getString(R.string.Delete));
        boolean z = LocaleController.isRTL;
        addView(imageView, LayoutHelper.createFrame(48, 50.0f, (z ? 3 : 5) | 17, z ? 3.0f : 0.0f, 0.0f, z ? 0.0f : 3.0f, 0.0f));
        this.titleTextView.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 0.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 24.0f), 0);
    }

    public void lambda$setChat$0(TLRPC.Chat chat, View view) {
        ChatDeleteListener chatDeleteListener = this.chatDeleteListener;
        if (chatDeleteListener != null) {
            chatDeleteListener.onChatDeleted(chat);
        }
    }

    public TLRPC.Chat getChat() {
        return this.chat;
    }

    @Override
    protected boolean needCheck() {
        return false;
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.deleteImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
    }

    public void setChat(final TLRPC.Chat chat, int i, boolean z, int i2) {
        String formatPluralString;
        this.removable = z;
        this.chat = chat;
        this.avatarDrawable.setInfo(chat);
        this.imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        this.imageView.setForUserOrChat(chat, this.avatarDrawable);
        this.titleTextView.setText(Emoji.replaceEmoji(chat.title, this.titleTextView.getPaint().getFontMetricsInt(), false));
        boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
        if (!z) {
            formatPluralString = LocaleController.formatPluralString(isChannelAndNotMegaGroup ? "BoostingChannelWillReceiveBoost" : "BoostingGroupWillReceiveBoost", i, new Object[0]);
        } else if (i2 >= 1) {
            formatPluralString = LocaleController.formatPluralString(isChannelAndNotMegaGroup ? "Subscribers" : "Members", i2, new Object[0]);
        } else {
            formatPluralString = LocaleController.getString(isChannelAndNotMegaGroup ? R.string.DiscussChannel : R.string.AccDescrGroup);
        }
        setSubtitle(formatPluralString);
        this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3, this.resourcesProvider));
        setDivider(true);
        ImageView imageView = this.deleteImageView;
        if (z) {
            imageView.setVisibility(0);
        } else {
            imageView.setVisibility(4);
        }
        this.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatCell.this.lambda$setChat$0(chat, view);
            }
        });
    }

    public void setChatDeleteListener(ChatDeleteListener chatDeleteListener) {
        this.chatDeleteListener = chatDeleteListener;
    }

    public void setCounter(int i, int i2) {
        String formatPluralString;
        boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(this.chat);
        if (!this.removable) {
            formatPluralString = LocaleController.formatPluralString(isChannelAndNotMegaGroup ? "BoostingChannelWillReceiveBoost" : "BoostingGroupWillReceiveBoost", i, new Object[0]);
        } else if (i2 >= 1) {
            formatPluralString = LocaleController.formatPluralString(isChannelAndNotMegaGroup ? "Subscribers" : "Members", i2, new Object[0]);
        } else {
            formatPluralString = LocaleController.getString(isChannelAndNotMegaGroup ? R.string.DiscussChannel : R.string.AccDescrGroup);
        }
        setSubtitle(formatPluralString);
    }
}
