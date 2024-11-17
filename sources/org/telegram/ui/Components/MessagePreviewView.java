package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatMessageSharedResources;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagePreviewParams;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.MessagePreviewView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.PinchToZoomHelper;

public abstract class MessagePreviewView extends FrameLayout {
    Runnable changeBoundsRunnable;
    final ChatActivity chatActivity;
    private final int currentAccount;
    TLRPC.Chat currentChat;
    TLRPC.User currentUser;
    private final ArrayList drawingGroups;
    boolean isLandscapeMode;
    final MessagePreviewParams messagePreviewParams;
    ValueAnimator offsetsAnimator;
    private final ResourcesDelegate resourcesProvider;
    boolean returnSendersNames;
    TLRPC.Peer sendAsPeer;
    final boolean showOutdatedQuote;
    boolean showing;
    TabsView tabsView;
    ViewPagerFixed viewPager;

    public class ActionBar extends FrameLayout {
        private Theme.ResourcesProvider resourcesProvider;
        private final AnimatedTextView.AnimatedTextDrawable subtitle;
        private final AnimatedTextView.AnimatedTextDrawable title;

        public ActionBar(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
            this.title = animatedTextDrawable;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            animatedTextDrawable.setAnimationProperties(0.3f, 0L, 430L, cubicBezierInterpolator);
            animatedTextDrawable.setTypeface(AndroidUtilities.bold());
            animatedTextDrawable.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle, resourcesProvider));
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(18.0f));
            animatedTextDrawable.setEllipsizeByGradient(!LocaleController.isRTL);
            animatedTextDrawable.setCallback(this);
            animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
            this.subtitle = animatedTextDrawable2;
            animatedTextDrawable2.setAnimationProperties(0.3f, 0L, 430L, cubicBezierInterpolator);
            animatedTextDrawable2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle, resourcesProvider));
            animatedTextDrawable2.setTextSize(AndroidUtilities.dp(14.0f));
            animatedTextDrawable2.setEllipsizeByGradient(true ^ LocaleController.isRTL);
            animatedTextDrawable2.setCallback(this);
            animatedTextDrawable2.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        }

        private void setBounds(Drawable drawable, float f) {
            int i = (int) f;
            drawable.setBounds(getPaddingLeft(), i - AndroidUtilities.dp(32.0f), getMeasuredWidth() - getPaddingRight(), i + AndroidUtilities.dp(32.0f));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            setBounds(this.title, AndroidUtilities.lerp(AndroidUtilities.dp(29.0f), AndroidUtilities.dp(18.83f), this.subtitle.isNotEmpty()));
            this.title.draw(canvas);
            setBounds(this.subtitle, AndroidUtilities.dp(39.5f));
            this.subtitle.draw(canvas);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824));
            setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        }

        public void setSubtitle(CharSequence charSequence, boolean z) {
            this.subtitle.setText(charSequence, z && !LocaleController.isRTL);
        }

        public void setTitle(CharSequence charSequence, boolean z) {
            this.title.setText(charSequence, z && !LocaleController.isRTL);
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return this.title == drawable || this.subtitle == drawable || super.verifyDrawable(drawable);
        }
    }

    public class Page extends FrameLayout {
        ActionBar actionBar;
        Adapter adapter;
        private int buttonsHeight;
        ToggleButton changePositionBtn;
        ToggleButton changeSizeBtn;
        FrameLayout changeSizeBtnContainer;
        GridLayoutManagerFixed chatLayoutManager;
        RecyclerListView chatListView;
        SizeNotifierFrameLayout chatPreviewContainer;
        int chatTopOffset;
        ActionBarMenuSubItem clearQuoteButton;
        public int currentTab;
        int currentTopOffset;
        float currentYOffset;
        ActionBarMenuSubItem deleteReplyButton;
        private boolean firstAttach;
        private boolean firstLayout;
        ChatListItemAnimator itemAnimator;
        int lastSize;
        ActionBarPopupWindow.ActionBarPopupWindowLayout menu;
        int menuBack;
        MessagePreviewParams.Messages messages;
        ActionBarMenuSubItem quoteAnotherChatButton;
        ActionBarMenuSubItem quoteButton;
        private AnimatorSet quoteSwitcher;
        android.graphics.Rect rect;
        ActionBarMenuSubItem replyAnotherChatButton;
        int scrollToOffset;
        ChatMessageSharedResources sharedResources;
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper;
        View textSelectionOverlay;
        boolean toQuote;
        boolean updateAfterAnimations;
        private boolean updateScroll;
        ToggleButton videoChangeSizeBtn;
        float yOffset;

        public class AnonymousClass10 extends GridLayoutManagerFixed {
            final MessagePreviewView val$this$0;

            AnonymousClass10(Context context, int i, int i2, boolean z, MessagePreviewView messagePreviewView) {
                super(context, i, i2, z);
                this.val$this$0 = messagePreviewView;
            }

            public void lambda$onLayoutChildren$0() {
                Page.this.adapter.notifyDataSetChanged();
            }

            @Override
            protected boolean hasSiblingChild(int i) {
                byte b;
                MessageObject messageObject = Page.this.messages.previewMessages.get(i);
                MessageObject.GroupedMessages validGroupedMessage = Page.this.getValidGroupedMessage(messageObject);
                if (validGroupedMessage != null) {
                    MessageObject.GroupedMessagePosition position = validGroupedMessage.getPosition(messageObject);
                    if (position.minX != position.maxX && (b = position.minY) == position.maxY && b != 0) {
                        int size = validGroupedMessage.posArray.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition = validGroupedMessage.posArray.get(i2);
                            if (groupedMessagePosition != position) {
                                byte b2 = groupedMessagePosition.minY;
                                byte b3 = position.minY;
                                if (b2 <= b3 && groupedMessagePosition.maxY >= b3) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    super.onLayoutChildren(recycler, state);
                    return;
                }
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            MessagePreviewView.Page.AnonymousClass10.this.lambda$onLayoutChildren$0();
                        }
                    });
                }
            }

            @Override
            public boolean shouldLayoutChildFromOpositeSide(View view) {
                return false;
            }
        }

        public class AnonymousClass7 extends ChatListItemAnimator {
            Runnable finishRunnable;
            int scrollAnimationIndex;
            final MessagePreviewView val$this$0;

            AnonymousClass7(ChatActivity chatActivity, RecyclerListView recyclerListView, Theme.ResourcesProvider resourcesProvider, MessagePreviewView messagePreviewView) {
                super(chatActivity, recyclerListView, resourcesProvider);
                this.val$this$0 = messagePreviewView;
                this.scrollAnimationIndex = -1;
            }

            public void lambda$endAnimations$2() {
                if (this.scrollAnimationIndex != -1) {
                    NotificationCenter.getInstance(MessagePreviewView.this.currentAccount).onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
            }

            public void lambda$onAllAnimationsDone$0() {
                if (this.scrollAnimationIndex != -1) {
                    NotificationCenter.getInstance(MessagePreviewView.this.currentAccount).onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
            }

            public void lambda$onAllAnimationsDone$1() {
                Page.this.updateMessages();
            }

            @Override
            public void endAnimations() {
                super.endAnimations();
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        MessagePreviewView.Page.AnonymousClass7.this.lambda$endAnimations$2();
                    }
                };
                this.finishRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2);
            }

            @Override
            protected void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        MessagePreviewView.Page.AnonymousClass7.this.lambda$onAllAnimationsDone$0();
                    }
                };
                this.finishRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2);
                Page page = Page.this;
                if (page.updateAfterAnimations) {
                    page.updateAfterAnimations = false;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            MessagePreviewView.Page.AnonymousClass7.this.lambda$onAllAnimationsDone$1();
                        }
                    });
                }
            }

            @Override
            public void onAnimationStart() {
                super.onAnimationStart();
                AndroidUtilities.cancelRunOnUIThread(MessagePreviewView.this.changeBoundsRunnable);
                MessagePreviewView.this.changeBoundsRunnable.run();
                if (this.scrollAnimationIndex == -1) {
                    this.scrollAnimationIndex = NotificationCenter.getInstance(MessagePreviewView.this.currentAccount).setAnimationInProgress(this.scrollAnimationIndex, null, false);
                }
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.finishRunnable = null;
                }
            }
        }

        public class Adapter extends RecyclerView.Adapter {
            private Adapter() {
            }

            private int offset(ChatMessageCell chatMessageCell, int i) {
                MessageObject messageObject;
                int i2;
                ArrayList<MessageObject.TextLayoutBlock> arrayList;
                CharSequence charSequence;
                MessageObject.TextLayoutBlocks textLayoutBlocks;
                if (chatMessageCell == null || (messageObject = chatMessageCell.getMessageObject()) == null || messageObject.getGroupId() != 0) {
                    return 0;
                }
                if (TextUtils.isEmpty(messageObject.caption) || (textLayoutBlocks = chatMessageCell.captionLayout) == null) {
                    chatMessageCell.layoutTextXY(true);
                    i2 = chatMessageCell.textY;
                    CharSequence charSequence2 = messageObject.messageText;
                    ArrayList<MessageObject.TextLayoutBlock> arrayList2 = messageObject.textLayoutBlocks;
                    if (chatMessageCell.linkPreviewAbove) {
                        i2 += chatMessageCell.linkPreviewHeight + AndroidUtilities.dp(10.0f);
                    }
                    arrayList = arrayList2;
                    charSequence = charSequence2;
                } else {
                    i2 = (int) chatMessageCell.captionY;
                    charSequence = messageObject.caption;
                    arrayList = textLayoutBlocks.textLayoutBlocks;
                }
                if (arrayList != null && charSequence != null) {
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i3);
                        String charSequence3 = textLayoutBlock.textLayout.getText().toString();
                        int i4 = textLayoutBlock.charactersOffset;
                        if (i > i4) {
                            return (int) (i - i4 > charSequence3.length() - 1 ? i2 + ((int) (textLayoutBlock.textYOffset(arrayList, chatMessageCell.transitionParams) + textLayoutBlock.padTop + textLayoutBlock.height)) : r6.getLineTop(r6.getLineForOffset(i - textLayoutBlock.charactersOffset)) + i2 + textLayoutBlock.textYOffset(arrayList, chatMessageCell.transitionParams) + textLayoutBlock.padTop);
                        }
                    }
                }
                return 0;
            }

            @Override
            public int getItemCount() {
                MessagePreviewParams.Messages messages = Page.this.messages;
                if (messages == null) {
                    return 0;
                }
                return messages.previewMessages.size();
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                MessagePreviewParams.Messages messages = Page.this.messages;
                if (messages == null) {
                    return;
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
                chatMessageCell.setInvalidateSpoilersParent(messages.hasSpoilers);
                chatMessageCell.setParentViewSize(Page.this.chatListView.getMeasuredWidth(), Page.this.chatListView.getMeasuredHeight());
                int id = chatMessageCell.getMessageObject() != null ? chatMessageCell.getMessageObject().getId() : 0;
                Page page = Page.this;
                if (page.currentTab == 2) {
                    MessagePreviewView.this.messagePreviewParams.checkCurrentLink(page.messages.previewMessages.get(i));
                }
                MessageObject messageObject = Page.this.messages.previewMessages.get(i);
                MessagePreviewParams.Messages messages2 = Page.this.messages;
                chatMessageCell.setMessageObject(messageObject, messages2.groupedMessagesMap.get(messages2.previewMessages.get(i).getGroupId()), true, true);
                if (Page.this.currentTab == 1) {
                    chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                        @Override
                        public boolean canDrawOutboundsContent() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                        }

                        @Override
                        public boolean canPerformActions() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                        }

                        @Override
                        public boolean canPerformReply() {
                            return canPerformActions();
                        }

                        @Override
                        public void didLongPress(ChatMessageCell chatMessageCell2, float f, float f2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell2, f, f2);
                        }

                        @Override
                        public void didLongPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell2, keyboardButton);
                        }

                        @Override
                        public boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i2, float f, float f2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, chat, i2, f, f2);
                        }

                        @Override
                        public boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, user, f, f2);
                        }

                        @Override
                        public void didPressAboutRevenueSharingAds() {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAboutRevenueSharingAds(this);
                        }

                        @Override
                        public boolean didPressAnimatedEmoji(ChatMessageCell chatMessageCell2, AnimatedEmojiSpan animatedEmojiSpan) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAnimatedEmoji(this, chatMessageCell2, animatedEmojiSpan);
                        }

                        @Override
                        public void didPressBoostCounter(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBoostCounter(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell2, keyboardButton);
                        }

                        @Override
                        public void didPressCancelSendButton(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i2, float f, float f2, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, chat, i2, f, f2, z);
                        }

                        @Override
                        public void didPressChannelRecommendation(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelRecommendation(this, chatMessageCell2, chat, z);
                        }

                        @Override
                        public void didPressChannelRecommendationsClose(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelRecommendationsClose(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressCodeCopy(ChatMessageCell chatMessageCell2, MessageObject.TextLayoutBlock textLayoutBlock) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCodeCopy(this, chatMessageCell2, textLayoutBlock);
                        }

                        @Override
                        public void didPressCommentButton(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressDialogButton(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressDialogButton(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressEffect(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressEffect(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressExtendedMediaPreview(this, chatMessageCell2, keyboardButton);
                        }

                        @Override
                        public void didPressFactCheck(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheck(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressFactCheckWhat(ChatMessageCell chatMessageCell2, int i2, int i3) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheckWhat(this, chatMessageCell2, i2, i3);
                        }

                        @Override
                        public void didPressGiveawayChatButton(ChatMessageCell chatMessageCell2, int i2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGiveawayChatButton(this, chatMessageCell2, i2);
                        }

                        @Override
                        public void didPressGroupImage(ChatMessageCell chatMessageCell2, ImageReceiver imageReceiver, TLRPC.MessageExtendedMedia messageExtendedMedia, float f, float f2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGroupImage(this, chatMessageCell2, imageReceiver, messageExtendedMedia, f, f2);
                        }

                        @Override
                        public void didPressHiddenForward(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressHint(ChatMessageCell chatMessageCell2, int i2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i2);
                        }

                        @Override
                        public void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                        }

                        @Override
                        public void didPressInstantButton(ChatMessageCell chatMessageCell2, int i2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i2);
                        }

                        @Override
                        public void didPressMoreChannelRecommendations(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressMoreChannelRecommendations(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressOther(ChatMessageCell chatMessageCell2, float f, float f2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell2, f, f2);
                        }

                        @Override
                        public void didPressReaction(ChatMessageCell chatMessageCell2, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell2, reactionCount, z, f, f2);
                        }

                        @Override
                        public void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i2, float f, float f2, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i2, f, f2, z);
                        }

                        @Override
                        public void didPressRevealSensitiveContent(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressRevealSensitiveContent(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressSideButton(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressSponsoredClose(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSponsoredClose(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressSponsoredInfo(ChatMessageCell chatMessageCell2, float f, float f2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSponsoredInfo(this, chatMessageCell2, f, f2);
                        }

                        @Override
                        public void didPressTime(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressTopicButton(ChatMessageCell chatMessageCell2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTopicButton(this, chatMessageCell2);
                        }

                        @Override
                        public void didPressUrl(ChatMessageCell chatMessageCell2, CharacterStyle characterStyle, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell2, characterStyle, z);
                        }

                        @Override
                        public void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, user, f, f2, z);
                        }

                        @Override
                        public void didPressUserStatus(ChatMessageCell chatMessageCell2, TLRPC.User user, TLRPC.Document document) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserStatus(this, chatMessageCell2, user, document);
                        }

                        @Override
                        public void didPressViaBot(ChatMessageCell chatMessageCell2, String str) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell2, str);
                        }

                        @Override
                        public void didPressViaBotNotInline(ChatMessageCell chatMessageCell2, long j) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell2, j);
                        }

                        @Override
                        public void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i2, int i3, int i4) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i2, i3, i4);
                        }

                        @Override
                        public void didPressWebPage(ChatMessageCell chatMessageCell2, TLRPC.WebPage webPage, String str, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressWebPage(this, chatMessageCell2, webPage, str, z);
                        }

                        @Override
                        public void didStartVideoStream(MessageObject messageObject2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject2);
                        }

                        @Override
                        public boolean doNotShowLoadingReply(MessageObject messageObject2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$doNotShowLoadingReply(this, messageObject2);
                        }

                        @Override
                        public void forceUpdate(ChatMessageCell chatMessageCell2, boolean z) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$forceUpdate(this, chatMessageCell2, z);
                        }

                        @Override
                        public String getAdminRank(long j) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                        }

                        @Override
                        public PinchToZoomHelper getPinchToZoomHelper() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                        }

                        @Override
                        public String getProgressLoadingBotButtonUrl(ChatMessageCell chatMessageCell2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getProgressLoadingBotButtonUrl(this, chatMessageCell2);
                        }

                        @Override
                        public CharacterStyle getProgressLoadingLink(ChatMessageCell chatMessageCell2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getProgressLoadingLink(this, chatMessageCell2);
                        }

                        @Override
                        public TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                        }

                        @Override
                        public boolean hasSelectedMessages() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                        }

                        @Override
                        public void invalidateBlur() {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                        }

                        @Override
                        public boolean isLandscape() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                        }

                        @Override
                        public boolean isProgressLoading(ChatMessageCell chatMessageCell2, int i2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isProgressLoading(this, chatMessageCell2, i2);
                        }

                        @Override
                        public boolean isReplyOrSelf() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isReplyOrSelf(this);
                        }

                        @Override
                        public boolean keyboardIsOpened() {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                        }

                        @Override
                        public void needOpenWebView(MessageObject messageObject2, String str, String str2, String str3, String str4, int i2, int i3) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject2, str, str2, str3, str4, i2, i3);
                        }

                        @Override
                        public boolean needPlayMessage(ChatMessageCell chatMessageCell2, MessageObject messageObject2, boolean z) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, chatMessageCell2, messageObject2, z);
                        }

                        @Override
                        public void needReloadPolls() {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                        }

                        @Override
                        public void needShowPremiumBulletin(int i2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumBulletin(this, i2);
                        }

                        @Override
                        public boolean onAccessibilityAction(int i2, Bundle bundle) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i2, bundle);
                        }

                        @Override
                        public void onDiceFinished() {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                        }

                        @Override
                        public void setShouldNotRepeatSticker(MessageObject messageObject2) {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject2);
                        }

                        @Override
                        public boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2, boolean z) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2, z);
                        }

                        @Override
                        public boolean shouldRepeatSticker(MessageObject messageObject2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject2);
                        }

                        @Override
                        public boolean shouldShowDialogButton(ChatMessageCell chatMessageCell2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldShowDialogButton(this, chatMessageCell2);
                        }

                        @Override
                        public boolean shouldShowTopicButton(ChatMessageCell chatMessageCell2) {
                            return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldShowTopicButton(this, chatMessageCell2);
                        }

                        @Override
                        public void videoTimerReached() {
                            ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                        }
                    });
                }
                if (Page.this.messages.previewMessages.size() > 1) {
                    chatMessageCell.setCheckBoxVisible(Page.this.currentTab == 1, false);
                    boolean z = id == Page.this.messages.previewMessages.get(i).getId();
                    MessagePreviewParams.Messages messages3 = Page.this.messages;
                    boolean z2 = messages3.selectedIds.get(messages3.previewMessages.get(i).getId(), false);
                    chatMessageCell.setChecked(z2, z2, z);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                Context context = viewGroup.getContext();
                int i2 = MessagePreviewView.this.currentAccount;
                Page page = Page.this;
                ChatMessageCell chatMessageCell = new ChatMessageCell(context, i2, false, page.sharedResources, MessagePreviewView.this.resourcesProvider) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                        Page.this.chatListView.invalidate();
                    }

                    @Override
                    public void invalidate(int i3, int i4, int i5, int i6) {
                        super.invalidate(i3, i4, i5, i6);
                        Page.this.chatListView.invalidate();
                    }

                    @Override
                    protected void onLayout(boolean z, int i3, int i4, int i5, int i6) {
                        super.onLayout(z, i3, i4, i5, i6);
                        Page.this.updateLinkHighlight(this);
                    }

                    @Override
                    public void setMessageObject(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean z, boolean z2) {
                        super.setMessageObject(messageObject, groupedMessages, z, z2);
                        Page.this.updateLinkHighlight(this);
                    }
                };
                chatMessageCell.setClipChildren(false);
                chatMessageCell.setClipToPadding(false);
                chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    @Override
                    public boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    @Override
                    public boolean canPerformActions() {
                        Page page2 = Page.this;
                        if (page2.currentTab == 2) {
                            MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                            if (!messagePreviewParams.singleLink && !messagePreviewParams.isSecret) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean canPerformReply() {
                        return canPerformActions();
                    }

                    @Override
                    public void didLongPress(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didLongPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell2, keyboardButton);
                    }

                    @Override
                    public boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i3, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, chat, i3, f, f2);
                    }

                    @Override
                    public boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, user, f, f2);
                    }

                    @Override
                    public void didPressAboutRevenueSharingAds() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAboutRevenueSharingAds(this);
                    }

                    @Override
                    public boolean didPressAnimatedEmoji(ChatMessageCell chatMessageCell2, AnimatedEmojiSpan animatedEmojiSpan) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAnimatedEmoji(this, chatMessageCell2, animatedEmojiSpan);
                    }

                    @Override
                    public void didPressBoostCounter(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBoostCounter(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell2, keyboardButton);
                    }

                    @Override
                    public void didPressCancelSendButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i3, float f, float f2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, chat, i3, f, f2, z);
                    }

                    @Override
                    public void didPressChannelRecommendation(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelRecommendation(this, chatMessageCell2, chat, z);
                    }

                    @Override
                    public void didPressChannelRecommendationsClose(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelRecommendationsClose(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressCodeCopy(ChatMessageCell chatMessageCell2, MessageObject.TextLayoutBlock textLayoutBlock) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCodeCopy(this, chatMessageCell2, textLayoutBlock);
                    }

                    @Override
                    public void didPressCommentButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressDialogButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressDialogButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressEffect(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressEffect(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressExtendedMediaPreview(this, chatMessageCell2, keyboardButton);
                    }

                    @Override
                    public void didPressFactCheck(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheck(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressFactCheckWhat(ChatMessageCell chatMessageCell2, int i3, int i4) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheckWhat(this, chatMessageCell2, i3, i4);
                    }

                    @Override
                    public void didPressGiveawayChatButton(ChatMessageCell chatMessageCell2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGiveawayChatButton(this, chatMessageCell2, i3);
                    }

                    @Override
                    public void didPressGroupImage(ChatMessageCell chatMessageCell2, ImageReceiver imageReceiver, TLRPC.MessageExtendedMedia messageExtendedMedia, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGroupImage(this, chatMessageCell2, imageReceiver, messageExtendedMedia, f, f2);
                    }

                    @Override
                    public void didPressHiddenForward(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressHint(ChatMessageCell chatMessageCell2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i3);
                    }

                    @Override
                    public void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didPressInstantButton(ChatMessageCell chatMessageCell2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i3);
                    }

                    @Override
                    public void didPressMoreChannelRecommendations(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressMoreChannelRecommendations(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressOther(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didPressReaction(ChatMessageCell chatMessageCell2, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell2, reactionCount, z, f, f2);
                    }

                    @Override
                    public void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i3, float f, float f2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i3, f, f2, z);
                    }

                    @Override
                    public void didPressRevealSensitiveContent(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressRevealSensitiveContent(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressSideButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressSponsoredClose(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSponsoredClose(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressSponsoredInfo(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSponsoredInfo(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didPressTime(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressTopicButton(ChatMessageCell chatMessageCell2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTopicButton(this, chatMessageCell2);
                    }

                    @Override
                    public void didPressUrl(ChatMessageCell chatMessageCell2, CharacterStyle characterStyle, boolean z) {
                        Page page2 = Page.this;
                        if (page2.currentTab != 2 || MessagePreviewView.this.messagePreviewParams.currentLink == characterStyle || chatMessageCell2.getMessageObject() == null || !(characterStyle instanceof URLSpan)) {
                            return;
                        }
                        String url = ((URLSpan) characterStyle).getURL();
                        MessagePreviewView messagePreviewView = MessagePreviewView.this;
                        MessagePreviewParams messagePreviewParams = messagePreviewView.messagePreviewParams;
                        messagePreviewParams.currentLink = characterStyle;
                        messagePreviewParams.webpage = null;
                        ChatActivity chatActivity = messagePreviewView.chatActivity;
                        if (chatActivity != null && url != null) {
                            chatActivity.searchLinks(url, true);
                        }
                        Page.this.updateLinkHighlight(chatMessageCell2);
                    }

                    @Override
                    public void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, user, f, f2, z);
                    }

                    @Override
                    public void didPressUserStatus(ChatMessageCell chatMessageCell2, TLRPC.User user, TLRPC.Document document) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserStatus(this, chatMessageCell2, user, document);
                    }

                    @Override
                    public void didPressViaBot(ChatMessageCell chatMessageCell2, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell2, str);
                    }

                    @Override
                    public void didPressViaBotNotInline(ChatMessageCell chatMessageCell2, long j) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell2, j);
                    }

                    @Override
                    public void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i3, int i4, int i5) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i3, i4, i5);
                    }

                    @Override
                    public void didPressWebPage(ChatMessageCell chatMessageCell2, TLRPC.WebPage webPage, String str, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressWebPage(this, chatMessageCell2, webPage, str, z);
                    }

                    @Override
                    public void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    @Override
                    public boolean doNotShowLoadingReply(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$doNotShowLoadingReply(this, messageObject);
                    }

                    @Override
                    public void forceUpdate(ChatMessageCell chatMessageCell2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$forceUpdate(this, chatMessageCell2, z);
                    }

                    @Override
                    public String getAdminRank(long j) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                    }

                    @Override
                    public PinchToZoomHelper getPinchToZoomHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                    }

                    @Override
                    public String getProgressLoadingBotButtonUrl(ChatMessageCell chatMessageCell2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getProgressLoadingBotButtonUrl(this, chatMessageCell2);
                    }

                    @Override
                    public CharacterStyle getProgressLoadingLink(ChatMessageCell chatMessageCell2) {
                        Page page2 = Page.this;
                        if (page2.currentTab != 2) {
                            return null;
                        }
                        MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                        if (messagePreviewParams.singleLink) {
                            return null;
                        }
                        return messagePreviewParams.currentLink;
                    }

                    @Override
                    public TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return Page.this.textSelectionHelper;
                    }

                    @Override
                    public boolean hasSelectedMessages() {
                        return true;
                    }

                    @Override
                    public void invalidateBlur() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                    }

                    @Override
                    public boolean isLandscape() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                    }

                    @Override
                    public boolean isProgressLoading(ChatMessageCell chatMessageCell2, int i3) {
                        Page page2 = Page.this;
                        if (page2.currentTab != 2 || i3 != 1) {
                            return false;
                        }
                        MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                        if (messagePreviewParams.singleLink) {
                            return false;
                        }
                        TLRPC.WebPage webPage = messagePreviewParams.webpage;
                        return webPage == null || (webPage instanceof TLRPC.TL_webPagePending);
                    }

                    @Override
                    public boolean isReplyOrSelf() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isReplyOrSelf(this);
                    }

                    @Override
                    public boolean keyboardIsOpened() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                    }

                    @Override
                    public void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i3, int i4) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i3, i4);
                    }

                    @Override
                    public boolean needPlayMessage(ChatMessageCell chatMessageCell2, MessageObject messageObject, boolean z) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, chatMessageCell2, messageObject, z);
                    }

                    @Override
                    public void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    @Override
                    public void needShowPremiumBulletin(int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumBulletin(this, i3);
                    }

                    @Override
                    public boolean onAccessibilityAction(int i3, Bundle bundle) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i3, bundle);
                    }

                    @Override
                    public void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    @Override
                    public void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    @Override
                    public boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2, boolean z) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2, z);
                    }

                    @Override
                    public boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    @Override
                    public boolean shouldShowDialogButton(ChatMessageCell chatMessageCell2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldShowDialogButton(this, chatMessageCell2);
                    }

                    @Override
                    public boolean shouldShowTopicButton(ChatMessageCell chatMessageCell2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldShowTopicButton(this, chatMessageCell2);
                    }

                    @Override
                    public void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
                return new RecyclerListView.Holder(chatMessageCell);
            }

            @Override
            public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
                int i;
                Page page = Page.this;
                if (page.messages == null || (i = page.currentTab) == 1) {
                    return;
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
                if (i != 0) {
                    chatMessageCell.setDrawSelectionBackground(false);
                    return;
                }
                MessageObject.GroupedMessages validGroupedMessage = page.getValidGroupedMessage(chatMessageCell.getMessageObject());
                chatMessageCell.setDrawSelectionBackground(validGroupedMessage == null);
                chatMessageCell.setChecked(true, validGroupedMessage == null, false);
                Page page2 = Page.this;
                MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                if (messagePreviewParams.isSecret || messagePreviewParams.quote == null || !page2.isReplyMessageCell(chatMessageCell) || Page.this.textSelectionHelper.isInSelectionMode()) {
                    return;
                }
                Page page3 = Page.this;
                TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper = page3.textSelectionHelper;
                MessagePreviewParams messagePreviewParams2 = MessagePreviewView.this.messagePreviewParams;
                chatListTextSelectionHelper.select(chatMessageCell, messagePreviewParams2.quoteStart, messagePreviewParams2.quoteEnd);
                if (Page.this.firstAttach) {
                    Page page4 = Page.this;
                    page4.scrollToOffset = offset(chatMessageCell, MessagePreviewView.this.messagePreviewParams.quoteStart);
                    Page.this.firstAttach = false;
                }
            }
        }

        public Page(android.content.Context r28, int r29) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessagePreviewView.Page.<init>(org.telegram.ui.Components.MessagePreviewView, android.content.Context, int):void");
        }

        public void checkScroll() {
            if (this.updateScroll) {
                if (this.chatListView.computeVerticalScrollRange() > this.chatListView.computeVerticalScrollExtent()) {
                    postDelayed(new Runnable() {
                        @Override
                        public final void run() {
                            MessagePreviewView.Page.this.lambda$checkScroll$18();
                        }
                    }, 0L);
                }
                this.updateScroll = false;
            }
        }

        public MessageObject.GroupedMessages getValidGroupedMessage(MessageObject messageObject) {
            if (messageObject.getGroupId() == 0) {
                return null;
            }
            MessageObject.GroupedMessages groupedMessages = this.messages.groupedMessagesMap.get(messageObject.getGroupId());
            if (groupedMessages == null || (groupedMessages.messages.size() > 1 && groupedMessages.getPosition(messageObject) != null)) {
                return groupedMessages;
            }
            return null;
        }

        public void lambda$checkScroll$18() {
            RecyclerListView recyclerListView;
            int computeVerticalScrollRange;
            if (MessagePreviewView.this.messagePreviewParams.webpageTop) {
                recyclerListView = this.chatListView;
                computeVerticalScrollRange = -recyclerListView.computeVerticalScrollOffset();
            } else {
                recyclerListView = this.chatListView;
                computeVerticalScrollRange = recyclerListView.computeVerticalScrollRange() - (this.chatListView.computeVerticalScrollOffset() + this.chatListView.computeVerticalScrollExtent());
            }
            recyclerListView.smoothScrollBy(0, computeVerticalScrollRange, 250, ChatListItemAnimator.DEFAULT_INTERPOLATOR);
        }

        public boolean lambda$new$0(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1) {
                MessagePreviewView.this.dismiss(true);
            }
            return true;
        }

        public void lambda$new$1() {
            switchToQuote(true, false);
        }

        public void lambda$new$10(View view) {
            MessagePreviewView.this.dismiss(true);
        }

        public void lambda$new$11(View view) {
            MessagePreviewView.this.removeForward();
        }

        public void lambda$new$12(ToggleButton toggleButton, ToggleButton toggleButton2, View view) {
            MessagePreviewView messagePreviewView = MessagePreviewView.this;
            MessagePreviewParams messagePreviewParams = messagePreviewView.messagePreviewParams;
            boolean z = !messagePreviewParams.hideForwardSendersName;
            messagePreviewParams.hideForwardSendersName = z;
            messagePreviewView.returnSendersNames = false;
            if (!z) {
                messagePreviewParams.hideCaption = false;
                if (toggleButton != null) {
                    toggleButton.setState(false, true);
                }
            }
            toggleButton2.setState(MessagePreviewView.this.messagePreviewParams.hideForwardSendersName, true);
            updateMessages();
            updateSubtitle(true);
        }

        public void lambda$new$13(ToggleButton toggleButton, ToggleButton toggleButton2, View view) {
            MessagePreviewView messagePreviewView = MessagePreviewView.this;
            MessagePreviewParams messagePreviewParams = messagePreviewView.messagePreviewParams;
            boolean z = !messagePreviewParams.hideCaption;
            messagePreviewParams.hideCaption = z;
            if (!z) {
                if (messagePreviewView.returnSendersNames) {
                    messagePreviewParams.hideForwardSendersName = false;
                }
                messagePreviewView.returnSendersNames = false;
            } else if (!messagePreviewParams.hideForwardSendersName) {
                messagePreviewParams.hideForwardSendersName = true;
                messagePreviewView.returnSendersNames = true;
            }
            toggleButton.setState(z, true);
            toggleButton2.setState(MessagePreviewView.this.messagePreviewParams.hideForwardSendersName, true);
            updateMessages();
            updateSubtitle(true);
        }

        public void lambda$new$14(View view) {
            MessagePreviewView.this.dismiss(true);
        }

        public void lambda$new$15(View view) {
            MessagePreviewView.this.removeLink();
        }

        public void lambda$new$16(View view) {
            TLRPC.Message message;
            TLRPC.MessageMedia messageMedia;
            TLRPC.Message message2;
            TLRPC.MessageMedia messageMedia2;
            MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
            if (messagePreviewParams.hasMedia) {
                boolean z = !messagePreviewParams.webpageSmall;
                messagePreviewParams.webpageSmall = z;
                this.changeSizeBtn.setState(z, true);
                this.videoChangeSizeBtn.setState(MessagePreviewView.this.messagePreviewParams.webpageSmall, true);
                if (this.messages.messages.size() > 0 && (message2 = this.messages.messages.get(0).messageOwner) != null && (messageMedia2 = message2.media) != null) {
                    boolean z2 = MessagePreviewView.this.messagePreviewParams.webpageSmall;
                    messageMedia2.force_small_media = z2;
                    messageMedia2.force_large_media = !z2;
                }
                if (this.messages.previewMessages.size() > 0 && (message = this.messages.previewMessages.get(0).messageOwner) != null && (messageMedia = message.media) != null) {
                    boolean z3 = MessagePreviewView.this.messagePreviewParams.webpageSmall;
                    messageMedia.force_small_media = z3;
                    messageMedia.force_large_media = !z3;
                }
                updateMessages();
                this.updateScroll = true;
            }
        }

        public void lambda$new$17(View view) {
            TLRPC.Message message;
            TLRPC.Message message2;
            MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
            boolean z = messagePreviewParams.webpageTop;
            messagePreviewParams.webpageTop = !z;
            this.changePositionBtn.setState(z, true);
            if (this.messages.messages.size() > 0 && (message2 = this.messages.messages.get(0).messageOwner) != null) {
                message2.invert_media = MessagePreviewView.this.messagePreviewParams.webpageTop;
            }
            if (this.messages.previewMessages.size() > 0 && (message = this.messages.previewMessages.get(0).messageOwner) != null) {
                message.invert_media = MessagePreviewView.this.messagePreviewParams.webpageTop;
            }
            updateMessages();
            this.updateScroll = true;
        }

        public void lambda$new$2(View view) {
            MessagePreviewView.this.messagePreviewParams.quote = null;
            this.textSelectionHelper.clear();
            switchToQuote(false, false);
            this.menu.getSwipeBack().closeForeground();
        }

        public void lambda$new$3(View view) {
            if (getReplyMessage() != null) {
                TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper = this.textSelectionHelper;
                if (chatListTextSelectionHelper.selectionEnd - chatListTextSelectionHelper.selectionStart > MessagesController.getInstance(MessagePreviewView.this.currentAccount).quoteLengthMax) {
                    showQuoteLengthError();
                    return;
                }
                MessageObject replyMessage = getReplyMessage(this.textSelectionHelper.getSelectedCell() != null ? ((ChatMessageCell) this.textSelectionHelper.getSelectedCell()).getMessageObject() : null);
                MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper2 = this.textSelectionHelper;
                int i = chatListTextSelectionHelper2.selectionStart;
                messagePreviewParams.quoteStart = i;
                int i2 = chatListTextSelectionHelper2.selectionEnd;
                messagePreviewParams.quoteEnd = i2;
                messagePreviewParams.quote = ChatActivity.ReplyQuote.from(replyMessage, i, i2);
                MessagePreviewView.this.onQuoteSelectedPart();
                MessagePreviewView.this.dismiss(true);
            }
        }

        public void lambda$new$4(View view) {
            MessagePreviewView messagePreviewView = MessagePreviewView.this;
            MessagePreviewParams messagePreviewParams = messagePreviewView.messagePreviewParams;
            if (messagePreviewParams.quote != null && !messagePreviewView.showOutdatedQuote) {
                messagePreviewParams.quote = null;
                this.textSelectionHelper.clear();
                switchToQuote(false, true);
                updateSubtitle(true);
                return;
            }
            TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper = this.textSelectionHelper;
            if (chatListTextSelectionHelper.selectionEnd - chatListTextSelectionHelper.selectionStart > MessagesController.getInstance(messagePreviewView.currentAccount).quoteLengthMax) {
                showQuoteLengthError();
                return;
            }
            MessageObject replyMessage = getReplyMessage();
            if (replyMessage != null) {
                if (this.textSelectionHelper.isInSelectionMode()) {
                    MessagePreviewParams messagePreviewParams2 = MessagePreviewView.this.messagePreviewParams;
                    TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper2 = this.textSelectionHelper;
                    messagePreviewParams2.quoteStart = chatListTextSelectionHelper2.selectionStart;
                    messagePreviewParams2.quoteEnd = chatListTextSelectionHelper2.selectionEnd;
                    MessageObject replyMessage2 = getReplyMessage(chatListTextSelectionHelper2.getSelectedCell() != null ? ((ChatMessageCell) this.textSelectionHelper.getSelectedCell()).getMessageObject() : null);
                    MessagePreviewParams messagePreviewParams3 = MessagePreviewView.this.messagePreviewParams;
                    messagePreviewParams3.quote = ChatActivity.ReplyQuote.from(replyMessage2, messagePreviewParams3.quoteStart, messagePreviewParams3.quoteEnd);
                    MessagePreviewView.this.onQuoteSelectedPart();
                    MessagePreviewView.this.dismiss(true);
                    return;
                }
                MessagePreviewView messagePreviewView2 = MessagePreviewView.this;
                MessagePreviewParams messagePreviewParams4 = messagePreviewView2.messagePreviewParams;
                messagePreviewParams4.quoteStart = 0;
                messagePreviewParams4.quoteEnd = Math.min(MessagesController.getInstance(messagePreviewView2.currentAccount).quoteLengthMax, replyMessage.messageOwner.message.length());
                MessagePreviewParams messagePreviewParams5 = MessagePreviewView.this.messagePreviewParams;
                messagePreviewParams5.quote = ChatActivity.ReplyQuote.from(replyMessage, messagePreviewParams5.quoteStart, messagePreviewParams5.quoteEnd);
                TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper3 = this.textSelectionHelper;
                ChatMessageCell replyMessageCell = getReplyMessageCell();
                MessagePreviewParams messagePreviewParams6 = MessagePreviewView.this.messagePreviewParams;
                chatListTextSelectionHelper3.select(replyMessageCell, messagePreviewParams6.quoteStart, messagePreviewParams6.quoteEnd);
                if (!MessagePreviewView.this.showOutdatedQuote) {
                    this.menu.getSwipeBack().openForeground(this.menuBack);
                }
                switchToQuote(true, true);
            }
        }

        public void lambda$new$5(View view) {
            MessagePreviewView.this.selectAnotherChat(false);
        }

        public void lambda$new$6(View view) {
            MessagePreviewView.this.selectAnotherChat(false);
        }

        public void lambda$new$7(View view) {
            MessagePreviewView.this.dismiss(true);
        }

        public void lambda$new$8(View view) {
            MessagePreviewView messagePreviewView = MessagePreviewView.this;
            if (messagePreviewView.showOutdatedQuote) {
                messagePreviewView.removeQuote();
            } else {
                messagePreviewView.removeReply();
            }
        }

        public void lambda$new$9(View view) {
            MessagePreviewView.this.selectAnotherChat(true);
        }

        public void lambda$onAttachedToWindow$19(View view) {
            this.adapter.onViewAttachedToWindow(this.chatListView.getChildViewHolder(view));
        }

        public void lambda$updatePositions$20(int i, float f, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f2 = 1.0f - floatValue;
            int i2 = (int) ((i * f2) + (this.chatTopOffset * floatValue));
            this.currentTopOffset = i2;
            float f3 = (f * f2) + (this.yOffset * floatValue);
            this.currentYOffset = f3;
            setOffset(f3, i2);
        }

        public void setOffset(float f, int i) {
            if (MessagePreviewView.this.isLandscapeMode) {
                this.actionBar.setTranslationY(0.0f);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.chatPreviewContainer.invalidateOutline();
                }
                this.chatPreviewContainer.setTranslationY(0.0f);
                this.menu.setTranslationY(0.0f);
            } else {
                this.actionBar.setTranslationY(i);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.chatPreviewContainer.invalidateOutline();
                }
                this.chatPreviewContainer.setTranslationY(f);
                this.menu.setTranslationY((f + this.chatPreviewContainer.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            }
            this.textSelectionOverlay.setTranslationX(this.chatPreviewContainer.getX());
            this.textSelectionOverlay.setTranslationY(this.chatPreviewContainer.getY());
        }

        public void showQuoteLengthError() {
            MessagePreviewView messagePreviewView = MessagePreviewView.this;
            BulletinFactory.of(messagePreviewView, messagePreviewView.resourcesProvider).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.QuoteMaxError), LocaleController.getString(R.string.QuoteMaxErrorMessage)).show();
        }

        public void switchToQuote(final boolean z, boolean z2) {
            if (MessagePreviewView.this.showOutdatedQuote) {
                z = false;
            }
            if (z2 && this.toQuote == z) {
                return;
            }
            this.toQuote = z;
            AnimatorSet animatorSet = this.quoteSwitcher;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.quoteSwitcher = null;
            }
            if (!z2) {
                ActionBarMenuSubItem actionBarMenuSubItem = this.quoteButton;
                if (actionBarMenuSubItem != null) {
                    actionBarMenuSubItem.setAlpha(!z ? 1.0f : 0.0f);
                    this.quoteButton.setVisibility(!z ? 0 : 4);
                }
                ActionBarMenuSubItem actionBarMenuSubItem2 = this.clearQuoteButton;
                if (actionBarMenuSubItem2 != null) {
                    actionBarMenuSubItem2.setAlpha(z ? 1.0f : 0.0f);
                    this.clearQuoteButton.setVisibility(z ? 0 : 4);
                }
                ActionBarMenuSubItem actionBarMenuSubItem3 = this.replyAnotherChatButton;
                if (actionBarMenuSubItem3 != null) {
                    actionBarMenuSubItem3.setAlpha(!z ? 1.0f : 0.0f);
                    this.replyAnotherChatButton.setVisibility(!z ? 0 : 4);
                }
                ActionBarMenuSubItem actionBarMenuSubItem4 = this.quoteAnotherChatButton;
                if (actionBarMenuSubItem4 != null) {
                    actionBarMenuSubItem4.setAlpha(z ? 1.0f : 0.0f);
                    this.quoteAnotherChatButton.setVisibility(z ? 0 : 4);
                    return;
                }
                return;
            }
            this.quoteSwitcher = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            ActionBarMenuSubItem actionBarMenuSubItem5 = this.quoteButton;
            if (actionBarMenuSubItem5 != null) {
                actionBarMenuSubItem5.setVisibility(0);
                arrayList.add(ObjectAnimator.ofFloat(this.quoteButton, (Property<ActionBarMenuSubItem, Float>) View.ALPHA, !z ? 1.0f : 0.0f));
            }
            ActionBarMenuSubItem actionBarMenuSubItem6 = this.clearQuoteButton;
            if (actionBarMenuSubItem6 != null) {
                actionBarMenuSubItem6.setVisibility(0);
                arrayList.add(ObjectAnimator.ofFloat(this.clearQuoteButton, (Property<ActionBarMenuSubItem, Float>) View.ALPHA, z ? 1.0f : 0.0f));
            }
            ActionBarMenuSubItem actionBarMenuSubItem7 = this.replyAnotherChatButton;
            if (actionBarMenuSubItem7 != null) {
                actionBarMenuSubItem7.setVisibility(0);
                arrayList.add(ObjectAnimator.ofFloat(this.replyAnotherChatButton, (Property<ActionBarMenuSubItem, Float>) View.ALPHA, !z ? 1.0f : 0.0f));
            }
            ActionBarMenuSubItem actionBarMenuSubItem8 = this.quoteAnotherChatButton;
            if (actionBarMenuSubItem8 != null) {
                actionBarMenuSubItem8.setVisibility(0);
                arrayList.add(ObjectAnimator.ofFloat(this.quoteAnotherChatButton, (Property<ActionBarMenuSubItem, Float>) View.ALPHA, z ? 1.0f : 0.0f));
            }
            this.quoteSwitcher.playTogether(arrayList);
            this.quoteSwitcher.setDuration(360L);
            this.quoteSwitcher.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.quoteSwitcher.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    Page.this.quoteSwitcher = null;
                    Page.this.switchToQuote(z, false);
                }
            });
            this.quoteSwitcher.start();
        }

        public void updateLinkHighlight(ChatMessageCell chatMessageCell) {
            CharacterStyle characterStyle;
            TLRPC.WebPage webPage;
            if (this.currentTab == 2) {
                MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                if (!messagePreviewParams.singleLink && (characterStyle = messagePreviewParams.currentLink) != null && (webPage = messagePreviewParams.webpage) != null && !(webPage instanceof TLRPC.TL_webPagePending)) {
                    chatMessageCell.setHighlightedSpan(characterStyle);
                    return;
                }
            }
            chatMessageCell.setHighlightedSpan(null);
        }

        public void updateMessages() {
            TLRPC.Message message;
            TLRPC.MessageMedia messageMedia;
            if (this.itemAnimator.isRunning()) {
                this.updateAfterAnimations = true;
                return;
            }
            for (int i = 0; i < this.messages.previewMessages.size(); i++) {
                MessageObject messageObject = this.messages.previewMessages.get(i);
                messageObject.forceUpdate = true;
                MessagePreviewView messagePreviewView = MessagePreviewView.this;
                messageObject.sendAsPeer = messagePreviewView.sendAsPeer;
                MessagePreviewParams messagePreviewParams = messagePreviewView.messagePreviewParams;
                if (messagePreviewParams.hideForwardSendersName) {
                    messageObject.messageOwner.flags &= -5;
                    messageObject.hideSendersName = true;
                } else {
                    messageObject.messageOwner.flags |= 4;
                    messageObject.hideSendersName = false;
                }
                if (this.currentTab == 2) {
                    TLRPC.WebPage webPage = messagePreviewParams.webpage;
                    if (webPage != null && ((messageMedia = (message = messageObject.messageOwner).media) == null || messageMedia.webpage != webPage)) {
                        message.flags |= 512;
                        message.media = new TLRPC.TL_messageMediaWebPage();
                        TLRPC.MessageMedia messageMedia2 = messageObject.messageOwner.media;
                        MessagePreviewParams messagePreviewParams2 = MessagePreviewView.this.messagePreviewParams;
                        messageMedia2.webpage = messagePreviewParams2.webpage;
                        boolean z = messagePreviewParams2.webpageSmall;
                        messageMedia2.force_large_media = !z;
                        messageMedia2.force_small_media = z;
                        messageMedia2.manual = true;
                        messageObject.linkDescription = null;
                        messageObject.generateLinkDescription();
                        messageObject.photoThumbs = null;
                        messageObject.photoThumbs2 = null;
                        messageObject.photoThumbsObject = null;
                        messageObject.photoThumbsObject2 = null;
                        messageObject.generateThumbs(true);
                        messageObject.checkMediaExistance();
                    } else if (webPage == null) {
                        TLRPC.Message message2 = messageObject.messageOwner;
                        message2.flags &= -513;
                        message2.media = null;
                    }
                }
                if (MessagePreviewView.this.messagePreviewParams.hideCaption) {
                    messageObject.caption = null;
                } else {
                    messageObject.generateCaption();
                }
                if (messageObject.isPoll()) {
                    MessagePreviewParams.PreviewMediaPoll previewMediaPoll = (MessagePreviewParams.PreviewMediaPoll) messageObject.messageOwner.media;
                    previewMediaPoll.results.total_voters = MessagePreviewView.this.messagePreviewParams.hideCaption ? 0 : previewMediaPoll.totalVotersCached;
                }
            }
            for (int i2 = 0; i2 < this.messages.pollChosenAnswers.size(); i2++) {
                this.messages.pollChosenAnswers.get(i2).chosen = !MessagePreviewView.this.messagePreviewParams.hideForwardSendersName;
            }
            for (int i3 = 0; i3 < this.messages.groupedMessagesMap.size(); i3++) {
                this.itemAnimator.groupWillChanged(this.messages.groupedMessagesMap.valueAt(i3));
            }
            this.adapter.notifyItemRangeChanged(0, this.messages.previewMessages.size());
        }

        public void updatePositions() {
            final int i = this.chatTopOffset;
            final float f = this.yOffset;
            if (MessagePreviewView.this.isLandscapeMode) {
                this.yOffset = 0.0f;
                this.chatTopOffset = 0;
                this.menu.setTranslationX(this.chatListView.getMeasuredWidth() + AndroidUtilities.dp(8.0f));
            } else {
                int measuredHeight = this.chatListView.getMeasuredHeight();
                int i2 = 0;
                for (int i3 = 0; i3 < this.chatListView.getChildCount(); i3++) {
                    View childAt = this.chatListView.getChildAt(i3);
                    if (this.chatListView.getChildAdapterPosition(childAt) != -1) {
                        measuredHeight = Math.min(measuredHeight, childAt.getTop());
                        i2++;
                    }
                }
                MessagePreviewParams.Messages messages = this.messages;
                if (messages == null || i2 == 0 || i2 > messages.previewMessages.size()) {
                    this.chatTopOffset = 0;
                } else {
                    int max = Math.max(0, measuredHeight - AndroidUtilities.dp(4.0f));
                    this.chatTopOffset = max;
                    this.chatTopOffset = Math.min((max + (this.chatListView.getMeasuredHeight() - this.chatTopOffset)) - ((int) (((AndroidUtilities.displaySize.y * 0.8f) - this.buttonsHeight) - AndroidUtilities.dp(8.0f))), this.chatTopOffset);
                }
                float dp = (AndroidUtilities.dp(8.0f) + (((getMeasuredHeight() - AndroidUtilities.dp(16.0f)) - ((this.buttonsHeight - AndroidUtilities.dp(8.0f)) + (this.chatPreviewContainer.getMeasuredHeight() - this.chatTopOffset))) / 2.0f)) - this.chatTopOffset;
                this.yOffset = dp;
                if (dp > AndroidUtilities.dp(8.0f)) {
                    this.yOffset = AndroidUtilities.dp(8.0f);
                }
                this.menu.setTranslationX(getMeasuredWidth() - this.menu.getMeasuredWidth());
            }
            boolean z = this.firstLayout;
            if (z || (this.chatTopOffset == i && this.yOffset == f)) {
                if (z) {
                    float f2 = this.yOffset;
                    this.currentYOffset = f2;
                    int i4 = this.chatTopOffset;
                    this.currentTopOffset = i4;
                    setOffset(f2, i4);
                    return;
                }
                return;
            }
            ValueAnimator valueAnimator = MessagePreviewView.this.offsetsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            MessagePreviewView.this.offsetsAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            MessagePreviewView.this.offsetsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    MessagePreviewView.Page.this.lambda$updatePositions$20(i, f, valueAnimator2);
                }
            });
            MessagePreviewView.this.offsetsAnimator.setDuration(250L);
            MessagePreviewView.this.offsetsAnimator.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
            MessagePreviewView.this.offsetsAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    Page page = Page.this;
                    MessagePreviewView.this.offsetsAnimator = null;
                    page.setOffset(page.yOffset, page.chatTopOffset);
                }
            });
            AndroidUtilities.runOnUIThread(MessagePreviewView.this.changeBoundsRunnable, 50L);
            this.currentTopOffset = i;
            this.currentYOffset = f;
            setOffset(f, i);
        }

        public void updateSubtitle(boolean r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessagePreviewView.Page.updateSubtitle(boolean):void");
        }

        public void bind() {
            updateMessages();
            updateSubtitle(false);
        }

        public MessageObject getReplyMessage() {
            return getReplyMessage(null);
        }

        public MessageObject getReplyMessage(MessageObject messageObject) {
            MessageObject.GroupedMessages valueAt;
            MessagePreviewParams.Messages messages = MessagePreviewView.this.messagePreviewParams.replyMessage;
            if (messages == null) {
                return null;
            }
            LongSparseArray<MessageObject.GroupedMessages> longSparseArray = messages.groupedMessagesMap;
            if (longSparseArray == null || longSparseArray.size() <= 0 || (valueAt = MessagePreviewView.this.messagePreviewParams.replyMessage.groupedMessagesMap.valueAt(0)) == null) {
                return MessagePreviewView.this.messagePreviewParams.replyMessage.messages.get(0);
            }
            if (valueAt.isDocuments) {
                if (messageObject != null) {
                    return messageObject;
                }
                ChatActivity.ReplyQuote replyQuote = MessagePreviewView.this.messagePreviewParams.quote;
                if (replyQuote != null) {
                    return replyQuote.message;
                }
            }
            return valueAt.captionMessage;
        }

        public ChatMessageCell getReplyMessageCell() {
            MessageObject replyMessage = getReplyMessage();
            if (replyMessage == null) {
                return null;
            }
            for (int i = 0; i < this.chatListView.getChildCount(); i++) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) this.chatListView.getChildAt(i);
                if (chatMessageCell.getMessageObject() != null && (chatMessageCell.getMessageObject() == replyMessage || chatMessageCell.getMessageObject().getId() == replyMessage.getId())) {
                    return chatMessageCell;
                }
            }
            return null;
        }

        public boolean isReplyMessageCell(ChatMessageCell chatMessageCell) {
            MessageObject replyMessage;
            if (chatMessageCell == null || chatMessageCell.getMessageObject() == null || (replyMessage = getReplyMessage()) == null) {
                return false;
            }
            return chatMessageCell.getMessageObject() == replyMessage || chatMessageCell.getMessageObject().getId() == replyMessage.getId();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.currentTab == 0) {
                AndroidUtilities.forEachViews((RecyclerView) this.chatListView, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        MessagePreviewView.Page.this.lambda$onAttachedToWindow$19((View) obj);
                    }
                });
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            updateSelection();
            this.firstAttach = true;
            this.firstLayout = true;
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            updatePositions();
            this.firstLayout = false;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            ViewGroup.LayoutParams layoutParams;
            MessageObject messageObject;
            int size;
            MessagePreviewView.this.isLandscapeMode = View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2);
            this.buttonsHeight = 0;
            this.menu.measure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
            int i3 = this.buttonsHeight;
            int measuredHeight = this.menu.getMeasuredHeight();
            android.graphics.Rect rect = this.rect;
            this.buttonsHeight = Math.max(i3, measuredHeight + rect.top + rect.bottom);
            ((ViewGroup.MarginLayoutParams) this.chatListView.getLayoutParams()).topMargin = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
            int i4 = -1;
            if (MessagePreviewView.this.isLandscapeMode) {
                this.chatPreviewContainer.getLayoutParams().height = -1;
                ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = AndroidUtilities.dp(8.0f);
                ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = AndroidUtilities.dp(8.0f);
                this.chatPreviewContainer.getLayoutParams().width = (int) Math.min(View.MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(340.0f), View.MeasureSpec.getSize(i) * 0.6f));
                layoutParams = this.menu.getLayoutParams();
            } else {
                ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = 0;
                ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = 0;
                this.chatPreviewContainer.getLayoutParams().height = (View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(6.0f)) - this.buttonsHeight;
                if (this.chatPreviewContainer.getLayoutParams().height < View.MeasureSpec.getSize(i2) * 0.5f) {
                    this.chatPreviewContainer.getLayoutParams().height = (int) (View.MeasureSpec.getSize(i2) * 0.5f);
                }
                this.chatPreviewContainer.getLayoutParams().width = -1;
                layoutParams = this.menu.getLayoutParams();
                i4 = View.MeasureSpec.getSize(i2) - this.chatPreviewContainer.getLayoutParams().height;
            }
            layoutParams.height = i4;
            int size2 = (View.MeasureSpec.getSize(i) + View.MeasureSpec.getSize(i2)) << 16;
            if (this.lastSize != size2) {
                for (int i5 = 0; i5 < this.messages.previewMessages.size(); i5++) {
                    if (MessagePreviewView.this.isLandscapeMode) {
                        messageObject = this.messages.previewMessages.get(i5);
                        size = this.chatPreviewContainer.getLayoutParams().width;
                    } else {
                        messageObject = this.messages.previewMessages.get(i5);
                        size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(16.0f);
                    }
                    messageObject.parentWidth = size;
                    this.messages.previewMessages.get(i5).resetLayout();
                    this.messages.previewMessages.get(i5).forceUpdate = true;
                    Adapter adapter = this.adapter;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                this.firstLayout = true;
            }
            this.lastSize = size2;
            super.onMeasure(i, i2);
        }

        public void updateSelection() {
            MessageObject messageObject;
            if (this.currentTab == 0) {
                TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper = this.textSelectionHelper;
                if (chatListTextSelectionHelper.selectionEnd - chatListTextSelectionHelper.selectionStart > MessagesController.getInstance(MessagePreviewView.this.currentAccount).quoteLengthMax) {
                    return;
                }
                MessageObject replyMessage = getReplyMessage(this.textSelectionHelper.getSelectedCell() != null ? ((ChatMessageCell) this.textSelectionHelper.getSelectedCell()).getMessageObject() : null);
                if (MessagePreviewView.this.messagePreviewParams.quote != null && this.textSelectionHelper.isInSelectionMode()) {
                    MessagePreviewParams messagePreviewParams = MessagePreviewView.this.messagePreviewParams;
                    TextSelectionHelper.ChatListTextSelectionHelper chatListTextSelectionHelper2 = this.textSelectionHelper;
                    messagePreviewParams.quoteStart = chatListTextSelectionHelper2.selectionStart;
                    messagePreviewParams.quoteEnd = chatListTextSelectionHelper2.selectionEnd;
                    if (replyMessage != null && ((messageObject = messagePreviewParams.quote.message) == null || messageObject.getId() != replyMessage.getId())) {
                        MessagePreviewParams messagePreviewParams2 = MessagePreviewView.this.messagePreviewParams;
                        messagePreviewParams2.quote = ChatActivity.ReplyQuote.from(replyMessage, messagePreviewParams2.quoteStart, messagePreviewParams2.quoteEnd);
                        MessagePreviewView.this.onQuoteSelectedPart();
                    }
                }
                this.textSelectionHelper.clear();
            }
        }
    }

    public static class RLottieToggleDrawable extends Drawable {
        private RLottieDrawable currentState;
        private boolean detached;
        private boolean isState1;
        private RLottieDrawable state1;
        private RLottieDrawable state2;

        public RLottieToggleDrawable(View view, int i, int i2) {
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.state1 = rLottieDrawable;
            rLottieDrawable.setMasterParent(view);
            this.state1.setAllowDecodeSingleFrame(true);
            this.state1.setPlayInDirectionOfCustomEndFrame(true);
            this.state1.setAutoRepeat(0);
            RLottieDrawable rLottieDrawable2 = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.state2 = rLottieDrawable2;
            rLottieDrawable2.setMasterParent(view);
            this.state2.setAllowDecodeSingleFrame(true);
            this.state2.setPlayInDirectionOfCustomEndFrame(true);
            this.state2.setAutoRepeat(0);
            this.currentState = this.state1;
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.detached) {
                return;
            }
            android.graphics.Rect rect = AndroidUtilities.rectTmp2;
            rect.set(getBounds().centerX() - AndroidUtilities.dp(12.0f), getBounds().centerY() - AndroidUtilities.dp(12.0f), getBounds().centerX() + AndroidUtilities.dp(12.0f), getBounds().centerY() + AndroidUtilities.dp(12.0f));
            if (this.currentState.isLastFrame()) {
                RLottieDrawable rLottieDrawable = this.currentState;
                boolean z = this.isState1;
                if (rLottieDrawable != (z ? this.state1 : this.state2)) {
                    RLottieDrawable rLottieDrawable2 = z ? this.state1 : this.state2;
                    this.currentState = rLottieDrawable2;
                    rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getFramesCount() - 1);
                }
            }
            this.currentState.setBounds(rect);
            this.currentState.draw(canvas);
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.state1.setAlpha(i);
            this.state2.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.state1.setColorFilter(colorFilter);
            this.state2.setColorFilter(colorFilter);
        }

        public void setState(boolean z, boolean z2) {
            this.isState1 = z;
            if (!z2) {
                RLottieDrawable rLottieDrawable = z ? this.state1 : this.state2;
                this.currentState = rLottieDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1);
            } else {
                this.currentState = z ? this.state1 : this.state2;
                this.state1.setCurrentFrame(0);
                this.state2.setCurrentFrame(0);
                this.currentState.start();
            }
        }
    }

    public interface ResourcesDelegate extends Theme.ResourcesProvider {
        Drawable getWallpaperDrawable();

        boolean isWallpaperMotion();
    }

    public static class TabsView extends View {
        private final Paint bgPaint;
        private int color;
        private float marginBetween;
        private Utilities.Callback onTabClick;
        private final Theme.ResourcesProvider resourcesProvider;
        private RectF selectRect;
        private int selectedColor;
        private float selectedTab;
        private float tabInnerPadding;
        public final ArrayList tabs;

        public static class Tab {
            final RectF bounds = new RectF();
            final RectF clickBounds = new RectF();
            final int id;
            final Text text;

            public Tab(int i, String str) {
                this.id = i;
                this.text = new Text(str, 14.0f, AndroidUtilities.bold());
            }
        }

        public TabsView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            int adaptHue;
            int[] colors;
            this.tabs = new ArrayList();
            Paint paint = new Paint(1);
            this.bgPaint = paint;
            this.tabInnerPadding = AndroidUtilities.dp(12.0f);
            this.marginBetween = AndroidUtilities.dp(13.0f);
            this.selectRect = new RectF();
            this.resourcesProvider = resourcesProvider;
            if (Theme.isCurrentThemeDark()) {
                this.color = -1862270977;
                this.selectedColor = -1325400065;
                adaptHue = 285212671;
            } else {
                int color = Theme.getColor(Theme.key_chat_wallpaper, resourcesProvider);
                if (resourcesProvider instanceof ChatActivity.ThemeDelegate) {
                    ChatActivity.ThemeDelegate themeDelegate = (ChatActivity.ThemeDelegate) resourcesProvider;
                    if ((themeDelegate.getWallpaperDrawable() instanceof MotionBackgroundDrawable) && (colors = ((MotionBackgroundDrawable) themeDelegate.getWallpaperDrawable()).getColors()) != null) {
                        color = AndroidUtilities.getAverageColor(AndroidUtilities.getAverageColor(colors[0], colors[1]), AndroidUtilities.getAverageColor(colors[2], colors[3]));
                    }
                }
                this.color = Theme.adaptHue(-1606201797, color);
                this.selectedColor = Theme.adaptHue(-448573893, color);
                adaptHue = Theme.adaptHue(814980216, color);
            }
            paint.setColor(adaptHue);
        }

        private int getHitTab(float f, float f2) {
            for (int i = 0; i < this.tabs.size(); i++) {
                if (((Tab) this.tabs.get(i)).clickBounds.contains(f, f2)) {
                    return ((Tab) this.tabs.get(i)).id;
                }
            }
            return -1;
        }

        public void addTab(int i, String str) {
            this.tabs.add(new Tab(i, str));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.tabs.size() <= 1) {
                return;
            }
            float f = this.selectedTab;
            double d = f;
            int floor = (int) Math.floor(d);
            boolean z = floor >= 0 && floor < this.tabs.size();
            int ceil = (int) Math.ceil(d);
            boolean z2 = ceil >= 0 && ceil < this.tabs.size();
            if (z && z2) {
                AndroidUtilities.lerp(((Tab) this.tabs.get(floor)).bounds, ((Tab) this.tabs.get(ceil)).bounds, f - floor, this.selectRect);
            } else if (z) {
                this.selectRect.set(((Tab) this.tabs.get(floor)).bounds);
            } else if (z2) {
                this.selectRect.set(((Tab) this.tabs.get(ceil)).bounds);
            }
            if (z || z2) {
                canvas.drawRoundRect(this.selectRect, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), this.bgPaint);
            }
            for (int i = 0; i < this.tabs.size(); i++) {
                Tab tab = (Tab) this.tabs.get(i);
                tab.text.draw(canvas, tab.bounds.left + this.tabInnerPadding, getMeasuredHeight() / 2.0f, ColorUtils.blendARGB(this.color, this.selectedColor, 1.0f - Math.abs(f - i)), 1.0f);
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            Utilities.Callback callback;
            if (this.tabs.size() <= 1) {
                return false;
            }
            int hitTab = getHitTab(motionEvent.getX(), motionEvent.getY());
            if (motionEvent.getAction() == 0) {
                return hitTab != -1;
            }
            if (motionEvent.getAction() == 1 && hitTab != -1 && (callback = this.onTabClick) != null) {
                callback.run(Integer.valueOf(hitTab));
            }
            return false;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.tabInnerPadding = AndroidUtilities.dp(12.0f);
            this.marginBetween = AndroidUtilities.dp(13.0f);
            float f = 0.0f;
            for (int i3 = 0; i3 < this.tabs.size(); i3++) {
                if (i3 > 0) {
                    f += this.marginBetween;
                }
                f += this.tabInnerPadding + ((Tab) this.tabs.get(i3)).text.getWidth() + this.tabInnerPadding;
            }
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            float dp = (measuredHeight - AndroidUtilities.dp(26.0f)) / 2.0f;
            float dp2 = (measuredHeight + AndroidUtilities.dp(26.0f)) / 2.0f;
            float f2 = (measuredWidth - f) / 2.0f;
            for (int i4 = 0; i4 < this.tabs.size(); i4++) {
                float width = this.tabInnerPadding + ((Tab) this.tabs.get(i4)).text.getWidth() + this.tabInnerPadding;
                ((Tab) this.tabs.get(i4)).bounds.set(f2, dp, f2 + width, dp2);
                ((Tab) this.tabs.get(i4)).clickBounds.set(((Tab) this.tabs.get(i4)).bounds);
                ((Tab) this.tabs.get(i4)).clickBounds.inset((-this.marginBetween) / 2.0f, -dp);
                f2 += width + this.marginBetween;
            }
        }

        public void setOnTabClick(Utilities.Callback callback) {
            this.onTabClick = callback;
        }

        public void setSelectedTab(float f) {
            this.selectedTab = f;
            invalidate();
        }
    }

    public static class ToggleButton extends View {
        private boolean first;
        RLottieToggleDrawable iconDrawable;
        private boolean isState1;
        final int minWidth;
        final String text1;
        final String text2;
        AnimatedTextView.AnimatedTextDrawable textDrawable;

        public ToggleButton(Context context, int i, String str, int i2, String str2, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.first = true;
            this.text1 = str;
            this.text2 = str2;
            setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 2));
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
            this.textDrawable = animatedTextDrawable;
            animatedTextDrawable.setAnimationProperties(0.35f, 0L, 300L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.textDrawable.setTextSize(AndroidUtilities.dp(16.0f));
            this.textDrawable.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
            this.textDrawable.setCallback(this);
            this.textDrawable.setEllipsizeByGradient(true ^ LocaleController.isRTL);
            if (LocaleController.isRTL) {
                this.textDrawable.setGravity(5);
            }
            int dp = (int) (AndroidUtilities.dp(77.0f) + Math.max(this.textDrawable.getPaint().measureText(str), this.textDrawable.getPaint().measureText(str2)));
            this.minWidth = dp;
            this.textDrawable.setOverrideFullWidth(dp);
            RLottieToggleDrawable rLottieToggleDrawable = new RLottieToggleDrawable(this, i, i2);
            this.iconDrawable = rLottieToggleDrawable;
            rLottieToggleDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, resourcesProvider), PorterDuff.Mode.SRC_IN));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (LocaleController.isRTL) {
                this.iconDrawable.setBounds(getMeasuredWidth() - AndroidUtilities.dp(41.0f), (getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2, getMeasuredWidth() - AndroidUtilities.dp(17.0f), (getMeasuredHeight() + AndroidUtilities.dp(24.0f)) / 2);
                this.textDrawable.setBounds(0, 0, getMeasuredWidth() - AndroidUtilities.dp(59.0f), getMeasuredHeight());
            } else {
                this.iconDrawable.setBounds(AndroidUtilities.dp(17.0f), (getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2, AndroidUtilities.dp(41.0f), (getMeasuredHeight() + AndroidUtilities.dp(24.0f)) / 2);
                this.textDrawable.setBounds(AndroidUtilities.dp(59.0f), 0, getMeasuredWidth(), getMeasuredHeight());
            }
            this.textDrawable.draw(canvas);
            this.iconDrawable.draw(canvas);
        }

        public boolean getState() {
            return this.isState1;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            int i3 = this.minWidth;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(mode == 1073741824 ? Math.max(size, i3) : Math.min(size, i3), mode), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (getVisibility() != 0 || getAlpha() < 0.5f) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setState(boolean z, boolean z2) {
            if (this.first || z != this.isState1) {
                this.isState1 = z;
                this.textDrawable.setText(z ? this.text1 : this.text2, z2 && !LocaleController.isRTL);
                this.iconDrawable.setState(z, z2);
                this.first = false;
            }
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return drawable == this.textDrawable || super.verifyDrawable(drawable);
        }
    }

    public MessagePreviewView(final Context context, ChatActivity chatActivity, MessagePreviewParams messagePreviewParams, TLRPC.User user, TLRPC.Chat chat, int i, ResourcesDelegate resourcesDelegate, int i2, final boolean z) {
        super(context);
        this.changeBoundsRunnable = new Runnable() {
            @Override
            public void run() {
                ValueAnimator valueAnimator = MessagePreviewView.this.offsetsAnimator;
                if (valueAnimator == null || valueAnimator.isRunning()) {
                    return;
                }
                MessagePreviewView.this.offsetsAnimator.start();
            }
        };
        this.drawingGroups = new ArrayList(10);
        this.showOutdatedQuote = z;
        this.chatActivity = chatActivity;
        this.currentAccount = i;
        this.currentUser = user;
        this.currentChat = chat;
        this.messagePreviewParams = messagePreviewParams;
        this.resourcesProvider = resourcesDelegate;
        this.viewPager = new ViewPagerFixed(context, resourcesDelegate) {
            @Override
            protected void onScrollEnd() {
                View view = this.viewPages[0];
                if (view instanceof Page) {
                    ((Page) view).textSelectionHelper.stopScrolling();
                }
            }

            @Override
            protected void onTabAnimationUpdate(boolean z2) {
                MessagePreviewView messagePreviewView = MessagePreviewView.this;
                messagePreviewView.tabsView.setSelectedTab(messagePreviewView.viewPager.getPositionAnimated());
                View view = this.viewPages[0];
                if (view instanceof Page) {
                    ((Page) view).textSelectionHelper.onParentScrolled();
                }
                View view2 = this.viewPages[1];
                if (view2 instanceof Page) {
                    ((Page) view2).textSelectionHelper.onParentScrolled();
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (MessagePreviewView.this.isTouchedHandle()) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.tabsView = new TabsView(context, resourcesDelegate);
        int i3 = 0;
        for (int i4 = 0; i4 < 3; i4++) {
            if (i4 == 0 && messagePreviewParams.replyMessage != null) {
                this.tabsView.addTab(0, LocaleController.getString(R.string.MessageOptionsReply));
            } else if (i4 == 1 && messagePreviewParams.forwardMessages != null && !z) {
                this.tabsView.addTab(1, LocaleController.getString(R.string.MessageOptionsForward));
            } else if (i4 == 2 && messagePreviewParams.linkMessage != null && !z) {
                this.tabsView.addTab(2, LocaleController.getString(R.string.MessageOptionsLink));
            }
            if (i4 == i2) {
                i3 = this.tabsView.tabs.size() - 1;
            }
        }
        this.viewPager.setAdapter(new ViewPagerFixed.Adapter() {
            @Override
            public void bindView(View view, int i5, int i6) {
                ((Page) view).bind();
            }

            @Override
            public View createView(int i5) {
                return new Page(context, i5);
            }

            @Override
            public int getItemCount() {
                return MessagePreviewView.this.tabsView.tabs.size();
            }

            @Override
            public int getItemViewType(int i5) {
                return ((TabsView.Tab) MessagePreviewView.this.tabsView.tabs.get(i5)).id;
            }
        });
        this.viewPager.setPosition(i3);
        this.tabsView.setSelectedTab(i3);
        addView(this.tabsView, LayoutHelper.createFrame(-1, 66, 87));
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 0.0f, 0.0f, 66.0f));
        this.tabsView.setOnTabClick(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                MessagePreviewView.this.lambda$new$0((Integer) obj);
            }
        });
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$1;
                lambda$new$1 = MessagePreviewView.this.lambda$new$1(z, view, motionEvent);
                return lambda$new$1;
            }
        });
        this.showing = true;
        setAlpha(0.0f);
        setScaleX(0.95f);
        setScaleY(0.95f);
        animate().alpha(1.0f).scaleX(1.0f).setDuration(250L).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).scaleY(1.0f);
        updateColors();
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$new$0(Integer num) {
        if (((TabsView.Tab) this.tabsView.tabs.get(this.viewPager.getCurrentPosition())).id == num.intValue()) {
            return;
        }
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= this.tabsView.tabs.size()) {
                break;
            }
            if (((TabsView.Tab) this.tabsView.tabs.get(i2)).id == num.intValue()) {
                i = i2;
                break;
            }
            i2++;
        }
        if (this.viewPager.getCurrentPosition() == i) {
            return;
        }
        this.viewPager.scrollToPosition(i);
    }

    public boolean lambda$new$1(boolean z, View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1 && !z) {
            dismiss(true);
        }
        return true;
    }

    private void updateColors() {
    }

    public void dismiss(final boolean z) {
        if (this.showing) {
            int i = 0;
            this.showing = false;
            animate().alpha(0.0f).scaleX(0.95f).scaleY(0.95f).setDuration(250L).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (MessagePreviewView.this.getParent() != null) {
                        ((ViewGroup) MessagePreviewView.this.getParent()).removeView(MessagePreviewView.this);
                    }
                    MessagePreviewView.this.onFullDismiss(z);
                }
            });
            while (true) {
                View[] viewArr = this.viewPager.viewPages;
                if (i >= viewArr.length) {
                    break;
                }
                View view = viewArr[i];
                if (view instanceof Page) {
                    Page page = (Page) view;
                    if (page.currentTab == 0) {
                        page.updateSelection();
                        break;
                    }
                }
                i++;
            }
            onDismiss(z);
        }
    }

    public boolean isShowing() {
        return this.showing;
    }

    public boolean isTouchedHandle() {
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPager.viewPages;
            if (i >= viewArr.length) {
                return false;
            }
            View view = viewArr[i];
            if (view != null) {
                Page page = (Page) view;
                if (page.currentTab == 0) {
                    return page.textSelectionHelper.isTouched();
                }
            }
            i++;
        }
    }

    protected abstract void onDismiss(boolean z);

    protected abstract void onFullDismiss(boolean z);

    protected abstract void onQuoteSelectedPart();

    public void removeForward() {
    }

    protected abstract void removeLink();

    protected abstract void removeQuote();

    protected abstract void removeReply();

    protected abstract void selectAnotherChat(boolean z);

    public void setSendAsPeer(TLRPC.Peer peer) {
        this.sendAsPeer = peer;
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPager.viewPages;
            if (i >= viewArr.length) {
                return;
            }
            View view = viewArr[i];
            if (view != null) {
                Page page = (Page) view;
                if (page.currentTab == 1) {
                    page.updateMessages();
                }
            }
            i++;
        }
    }

    public void updateAll() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MessagePreviewView.updateAll():void");
    }

    public void updateLink() {
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPager.viewPages;
            if (i >= viewArr.length) {
                return;
            }
            View view = viewArr[i];
            if (view != null) {
                Page page = (Page) view;
                if (page.currentTab == 2) {
                    FrameLayout frameLayout = page.changeSizeBtnContainer;
                    MessagePreviewParams messagePreviewParams = this.messagePreviewParams;
                    frameLayout.setVisibility((!messagePreviewParams.singleLink || messagePreviewParams.hasMedia) ? 0 : 8);
                    page.changeSizeBtn.setVisibility(this.messagePreviewParams.isVideo ? 4 : 0);
                    page.videoChangeSizeBtn.setVisibility(this.messagePreviewParams.isVideo ? 0 : 4);
                    page.changeSizeBtnContainer.animate().alpha(this.messagePreviewParams.hasMedia ? 1.0f : 0.5f).start();
                    page.changeSizeBtn.setState(this.messagePreviewParams.webpageSmall, true);
                    page.videoChangeSizeBtn.setState(this.messagePreviewParams.webpageSmall, true);
                    page.changePositionBtn.setState(!this.messagePreviewParams.webpageTop, true);
                    page.updateMessages();
                }
            }
            i++;
        }
    }
}
