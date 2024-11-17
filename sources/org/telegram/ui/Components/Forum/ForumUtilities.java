package org.telegram.ui.Components.Forum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.TopicsFragment;

public abstract class ForumUtilities {
    static SparseArray dialogForumDrawables = new SparseArray();
    static Drawable dialogGeneralIcon;

    public static class GeneralTopicDrawable extends Drawable {
        int color;
        Drawable icon;
        float scale;

        public GeneralTopicDrawable(Context context, float f, int i, boolean z, boolean z2) {
            Drawable mutate;
            if (z) {
                if (ForumUtilities.dialogGeneralIcon == null) {
                    ForumUtilities.dialogGeneralIcon = context.getResources().getDrawable(z2 ? R.drawable.msg_filled_general_large : R.drawable.msg_filled_general).mutate();
                }
                mutate = ForumUtilities.dialogGeneralIcon;
            } else {
                mutate = context.getResources().getDrawable(z2 ? R.drawable.msg_filled_general_large : R.drawable.msg_filled_general).mutate();
            }
            this.icon = mutate;
            this.scale = f;
            setColor(i);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            if (this.scale == 1.0f) {
                this.icon.setBounds(bounds);
            } else {
                this.icon.setBounds((int) (bounds.centerX() - ((bounds.width() / 2.0f) * this.scale)), (int) (bounds.centerY() - ((bounds.height() / 2.0f) * this.scale)), (int) (bounds.centerX() + ((bounds.width() / 2.0f) * this.scale)), (int) (bounds.centerY() + ((bounds.height() / 2.0f) * this.scale)));
            }
            this.icon.draw(canvas);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.icon.setAlpha(i);
        }

        public void setColor(int i) {
            if (this.color != i) {
                this.color = i;
                setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.icon.setColorFilter(colorFilter);
        }
    }

    public static void applyTopic(ChatActivity chatActivity, MessagesStorage.TopicKey topicKey) {
        TLRPC.TL_forumTopic findTopic;
        if (topicKey.topicId == 0 || (findTopic = chatActivity.getMessagesController().getTopicsController().findTopic(-topicKey.dialogId, topicKey.topicId)) == null) {
            return;
        }
        TLRPC.Chat chat = chatActivity.getMessagesController().getChat(Long.valueOf(-topicKey.dialogId));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(chatActivity.getCurrentAccount(), findTopic.topicStartMessage, false, false));
        chatActivity.setThreadMessages(arrayList, chat, findTopic.id, findTopic.read_inbox_max_id, findTopic.read_outbox_max_id, findTopic);
    }

    public static void applyTopicToMessage(MessageObject messageObject) {
        TLRPC.TL_forumTopic findTopic;
        if (messageObject.getDialogId() <= 0 && (findTopic = MessagesController.getInstance(messageObject.currentAccount).getTopicsController().findTopic(-messageObject.getDialogId(), MessageObject.getTopicId(messageObject.currentAccount, messageObject.messageOwner, true))) != null) {
            Drawable drawable = messageObject.topicIconDrawable[0];
            if (drawable instanceof ForumBubbleDrawable) {
                ((ForumBubbleDrawable) drawable).setColor(findTopic.icon_color);
            }
        }
    }

    public static CharSequence createActionTextWithTopic(TLRPC.TL_forumTopic tL_forumTopic, MessageObject messageObject) {
        TLRPC.Chat chat;
        TLRPC.User user;
        if (tL_forumTopic == null) {
            return null;
        }
        TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
        if (messageAction instanceof TLRPC.TL_messageActionTopicCreate) {
            return AndroidUtilities.replaceCharSequence("%s", LocaleController.getString(R.string.TopicWasCreatedAction), getTopicSpannedName(tL_forumTopic, null, false));
        }
        if (messageAction instanceof TLRPC.TL_messageActionTopicEdit) {
            TLRPC.TL_messageActionTopicEdit tL_messageActionTopicEdit = (TLRPC.TL_messageActionTopicEdit) messageAction;
            long fromChatId = messageObject.getFromChatId();
            boolean isUserDialog = DialogObject.isUserDialog(fromChatId);
            MessagesController messagesController = MessagesController.getInstance(messageObject.currentAccount);
            if (isUserDialog) {
                user = messagesController.getUser(Long.valueOf(fromChatId));
                chat = null;
            } else {
                chat = messagesController.getChat(Long.valueOf(-fromChatId));
                user = null;
            }
            String formatName = user != null ? ContactsController.formatName(user.first_name, user.last_name) : chat != null ? chat.title : null;
            int i = tL_messageActionTopicEdit.flags;
            if ((i & 8) != 0) {
                return AndroidUtilities.replaceCharSequence("%s", LocaleController.getString(tL_messageActionTopicEdit.hidden ? R.string.TopicHidden2 : R.string.TopicShown2), formatName);
            }
            if ((i & 4) != 0) {
                return AndroidUtilities.replaceCharSequence("%1$s", AndroidUtilities.replaceCharSequence("%2$s", LocaleController.getString(tL_messageActionTopicEdit.closed ? R.string.TopicWasClosedAction : R.string.TopicWasReopenedAction), getTopicSpannedName(tL_forumTopic, null, false)), formatName);
            }
            int i2 = i & 1;
            if (i2 != 0 && (i & 2) != 0) {
                TLRPC.TL_forumTopic tL_forumTopic2 = new TLRPC.TL_forumTopic();
                tL_forumTopic2.icon_emoji_id = tL_messageActionTopicEdit.icon_emoji_id;
                tL_forumTopic2.title = tL_messageActionTopicEdit.title;
                return AndroidUtilities.replaceCharSequence("%1$s", AndroidUtilities.replaceCharSequence("%2$s", LocaleController.getString(R.string.TopicWasRenamedToAction2), getTopicSpannedName(tL_forumTopic2, null, false)), formatName);
            }
            if (i2 != 0) {
                return AndroidUtilities.replaceCharSequence("%1$s", AndroidUtilities.replaceCharSequence("%2$s", LocaleController.getString(R.string.TopicWasRenamedToAction), tL_messageActionTopicEdit.title), formatName);
            }
            if ((i & 2) != 0) {
                TLRPC.TL_forumTopic tL_forumTopic3 = new TLRPC.TL_forumTopic();
                tL_forumTopic3.icon_emoji_id = tL_messageActionTopicEdit.icon_emoji_id;
                tL_forumTopic3.title = "";
                return AndroidUtilities.replaceCharSequence("%1$s", AndroidUtilities.replaceCharSequence("%2$s", LocaleController.getString(R.string.TopicWasIconChangedToAction), getTopicSpannedName(tL_forumTopic3, null, false)), formatName);
            }
        }
        return null;
    }

    public static GeneralTopicDrawable createGeneralTopicDrawable(Context context, float f, int i, boolean z) {
        return createGeneralTopicDrawable(context, f, i, z, false);
    }

    public static GeneralTopicDrawable createGeneralTopicDrawable(Context context, float f, int i, boolean z, boolean z2) {
        if (context == null) {
            return null;
        }
        return new GeneralTopicDrawable(context, f, i, z, z2);
    }

    public static Drawable createSmallTopicDrawable(String str, int i) {
        ForumBubbleDrawable forumBubbleDrawable = new ForumBubbleDrawable(i);
        LetterDrawable letterDrawable = new LetterDrawable(null, 2);
        String upperCase = str.trim().toUpperCase();
        letterDrawable.setTitle(upperCase.length() >= 1 ? upperCase.substring(0, 1) : "");
        CombinedDrawable combinedDrawable = new CombinedDrawable(forumBubbleDrawable, letterDrawable, 0, 0);
        combinedDrawable.setFullsize(true);
        return combinedDrawable;
    }

    public static Drawable createTopicDrawable(String str, int i, boolean z) {
        Drawable forumBubbleDrawable;
        if (z) {
            forumBubbleDrawable = (Drawable) dialogForumDrawables.get(i);
            if (forumBubbleDrawable == null) {
                forumBubbleDrawable = new ForumBubbleDrawable(i);
                dialogForumDrawables.put(i, forumBubbleDrawable);
            }
        } else {
            forumBubbleDrawable = new ForumBubbleDrawable(i);
        }
        LetterDrawable letterDrawable = new LetterDrawable(null, 1);
        String trim = str.trim();
        letterDrawable.setTitle(trim.length() >= 1 ? trim.substring(0, 1).toUpperCase() : "");
        CombinedDrawable combinedDrawable = new CombinedDrawable(forumBubbleDrawable, letterDrawable, 0, 0);
        combinedDrawable.setFullsize(true);
        return combinedDrawable;
    }

    public static Drawable createTopicDrawable(TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
        if (tL_forumTopic == null) {
            return null;
        }
        return createTopicDrawable(tL_forumTopic.title, tL_forumTopic.icon_color, z);
    }

    public static void filterMessagesByTopic(long j, ArrayList arrayList) {
        if (arrayList == null) {
            return;
        }
        int i = 0;
        while (i < arrayList.size()) {
            if (j != MessageObject.getTopicId(((MessageObject) arrayList.get(i)).currentAccount, ((MessageObject) arrayList.get(i)).messageOwner, true)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
    }

    public static ChatActivity getChatActivityForTopic(BaseFragment baseFragment, long j, TLRPC.TL_forumTopic tL_forumTopic, int i, Bundle bundle) {
        TLRPC.TL_forumTopic tL_forumTopic2;
        TLRPC.TL_forumTopic findTopic;
        if (baseFragment == null || tL_forumTopic == null) {
            return null;
        }
        TLRPC.Chat chat = baseFragment.getMessagesController().getChat(Long.valueOf(j));
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putLong("chat_id", j);
        if (i != 0) {
            bundle.putInt("message_id", i);
        } else if (tL_forumTopic.read_inbox_max_id == 0) {
            bundle.putInt("message_id", tL_forumTopic.id);
        }
        bundle.putInt("unread_count", tL_forumTopic.unread_count);
        bundle.putBoolean("historyPreloaded", false);
        ChatActivity chatActivity = new ChatActivity(bundle);
        TLRPC.Message message = tL_forumTopic.topicStartMessage;
        if (message != null || (findTopic = baseFragment.getMessagesController().getTopicsController().findTopic(j, tL_forumTopic.id)) == null) {
            tL_forumTopic2 = tL_forumTopic;
        } else {
            message = findTopic.topicStartMessage;
            tL_forumTopic2 = findTopic;
        }
        if (message == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(baseFragment.getCurrentAccount(), message, false, false));
        chatActivity.setThreadMessages(arrayList, chat, tL_forumTopic2.id, tL_forumTopic2.read_inbox_max_id, tL_forumTopic2.read_outbox_max_id, tL_forumTopic2);
        if (i != 0) {
            chatActivity.highlightMessageId = i;
        }
        return chatActivity;
    }

    public static CharSequence getTopicSpannedName(TLRPC.ForumTopic forumTopic, Paint paint, boolean z) {
        return getTopicSpannedName(forumTopic, paint, null, z);
    }

    public static CharSequence getTopicSpannedName(TLRPC.ForumTopic forumTopic, Paint paint, Drawable[] drawableArr, boolean z) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!(forumTopic instanceof TLRPC.TL_forumTopic)) {
            return "DELETED";
        }
        TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) forumTopic;
        if (tL_forumTopic.id == 1) {
            try {
                GeneralTopicDrawable createGeneralTopicDrawable = createGeneralTopicDrawable(ApplicationLoader.applicationContext, 1.0f, paint == null ? Theme.getColor(Theme.key_chat_inMenu) : paint.getColor(), z);
                createGeneralTopicDrawable.setBounds(0, 0, paint == null ? AndroidUtilities.dp(14.0f) : (int) paint.getTextSize(), paint == null ? AndroidUtilities.dp(14.0f) : (int) paint.getTextSize());
                spannableStringBuilder.append((CharSequence) " ");
                if (drawableArr != null) {
                    drawableArr[0] = createGeneralTopicDrawable;
                }
                spannableStringBuilder.setSpan(new ImageSpan(createGeneralTopicDrawable, 2), 0, 1, 33);
            } catch (Exception unused) {
            }
        } else {
            long j = tL_forumTopic.icon_emoji_id;
            spannableStringBuilder.append((CharSequence) " ");
            if (j != 0) {
                AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(tL_forumTopic.icon_emoji_id, 0.95f, paint == null ? null : paint.getFontMetricsInt());
                spannableStringBuilder.setSpan(animatedEmojiSpan, 0, 1, 33);
                animatedEmojiSpan.top = true;
                animatedEmojiSpan.cacheType = 13;
            } else {
                Drawable createTopicDrawable = createTopicDrawable(tL_forumTopic, z);
                if (drawableArr != null) {
                    drawableArr[0] = ((CombinedDrawable) createTopicDrawable).getBackgroundDrawable();
                }
                createTopicDrawable.setBounds(0, 0, (int) (createTopicDrawable.getIntrinsicWidth() * 0.65f), (int) (createTopicDrawable.getIntrinsicHeight() * 0.65f));
                if (createTopicDrawable instanceof CombinedDrawable) {
                    CombinedDrawable combinedDrawable = (CombinedDrawable) createTopicDrawable;
                    if (combinedDrawable.getIcon() instanceof LetterDrawable) {
                        ((LetterDrawable) combinedDrawable.getIcon()).scale = 0.7f;
                    }
                }
                if (paint != null) {
                    ColoredImageSpan coloredImageSpan = new ColoredImageSpan(createTopicDrawable);
                    coloredImageSpan.setSize((int) (Math.abs(paint.getFontMetrics().descent) + Math.abs(paint.getFontMetrics().ascent)));
                    spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 33);
                } else {
                    spannableStringBuilder.setSpan(new ImageSpan(createTopicDrawable), 0, 1, 33);
                }
            }
        }
        if (!TextUtils.isEmpty(tL_forumTopic.title)) {
            spannableStringBuilder.append((CharSequence) " ");
            spannableStringBuilder.append((CharSequence) tL_forumTopic.title);
        }
        return spannableStringBuilder;
    }

    public static boolean isTopicCreateMessage(MessageObject messageObject) {
        return messageObject != null && (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionTopicCreate);
    }

    public static void lambda$switchAllFragmentsInStackToForum$0(ChatActivity chatActivity) {
        if (chatActivity.getParentLayout() != null) {
            TopicsFragment.prepareToSwitchAnimation(chatActivity);
        }
    }

    public static void lambda$switchAllFragmentsInStackToForum$1(TopicsFragment topicsFragment) {
        if (topicsFragment.getParentLayout() != null) {
            topicsFragment.switchToChat(true);
        }
    }

    public static void openTopic(BaseFragment baseFragment, long j, TLRPC.TL_forumTopic tL_forumTopic, int i) {
        ChatActivity chatActivityForTopic = getChatActivityForTopic(baseFragment, j, tL_forumTopic, i, new Bundle());
        if (chatActivityForTopic != null) {
            baseFragment.presentFragment(chatActivityForTopic);
        }
    }

    public static void setTopicIcon(BackupImageView backupImageView, TLRPC.TL_forumTopic tL_forumTopic) {
        setTopicIcon(backupImageView, tL_forumTopic, false, false, null);
    }

    public static void setTopicIcon(BackupImageView backupImageView, TLRPC.TL_forumTopic tL_forumTopic, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        Drawable createTopicDrawable;
        if (tL_forumTopic == null || backupImageView == null) {
            return;
        }
        if (tL_forumTopic.id == 1) {
            backupImageView.setAnimatedEmojiDrawable(null);
            createTopicDrawable = createGeneralTopicDrawable(backupImageView.getContext(), 0.75f, Theme.getColor(Theme.key_actionBarDefaultIcon, resourcesProvider), false, z2);
        } else {
            if (tL_forumTopic.icon_emoji_id != 0) {
                backupImageView.setImageDrawable(null);
                AnimatedEmojiDrawable animatedEmojiDrawable = backupImageView.animatedEmojiDrawable;
                if (animatedEmojiDrawable == null || tL_forumTopic.icon_emoji_id != animatedEmojiDrawable.getDocumentId()) {
                    AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(z2 ? 11 : 10, UserConfig.selectedAccount, tL_forumTopic.icon_emoji_id);
                    animatedEmojiDrawable2.setColorFilter(z ? new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), PorterDuff.Mode.SRC_IN) : Theme.getAnimatedEmojiColorFilter(resourcesProvider));
                    backupImageView.setAnimatedEmojiDrawable(animatedEmojiDrawable2);
                    return;
                }
                return;
            }
            backupImageView.setAnimatedEmojiDrawable(null);
            createTopicDrawable = createTopicDrawable(tL_forumTopic, false);
        }
        backupImageView.setImageDrawable(createTopicDrawable);
    }

    public static void switchAllFragmentsInStackToForum(long j, INavigationLayout iNavigationLayout) {
        BaseFragment lastFragment = iNavigationLayout.getLastFragment();
        if (lastFragment instanceof ChatActivity) {
            final ChatActivity chatActivity = (ChatActivity) lastFragment;
            if ((-chatActivity.getDialogId()) == j && chatActivity.getMessagesController().getChat(Long.valueOf(j)).forum && chatActivity.getParentLayout() != null) {
                if (chatActivity.getParentLayout().checkTransitionAnimation()) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            ForumUtilities.lambda$switchAllFragmentsInStackToForum$0(ChatActivity.this);
                        }
                    }, 500L);
                } else {
                    TopicsFragment.prepareToSwitchAnimation(chatActivity);
                }
            }
        }
        if (lastFragment instanceof TopicsFragment) {
            final TopicsFragment topicsFragment = (TopicsFragment) lastFragment;
            if ((-topicsFragment.getDialogId()) != j || topicsFragment.getMessagesController().getChat(Long.valueOf(j)).forum) {
                return;
            }
            if (topicsFragment.getParentLayout() == null || !topicsFragment.getParentLayout().checkTransitionAnimation()) {
                topicsFragment.switchToChat(true);
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ForumUtilities.lambda$switchAllFragmentsInStackToForum$1(TopicsFragment.this);
                    }
                }, 500L);
            }
        }
    }
}
