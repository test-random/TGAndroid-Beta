package org.telegram.ui.Components.Reactions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Reactions.ChatSelectionReactionMenuOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;

public class ChatSelectionReactionMenuOverlay extends FrameLayout {
    private float currentOffsetY;
    private MessageObject currentPrimaryObject;
    private boolean hiddenByScroll;
    private boolean isVisible;
    private long lastUpdate;
    private int mPadding;
    private int mSidePadding;
    private boolean messageSet;
    private ChatActivity parentFragment;
    private int[] pos;
    private ReactionsContainerLayout reactionsContainerLayout;
    private List selectedMessages;
    private float toOffsetY;
    private float translationOffsetY;

    public class AnonymousClass3 implements ReactionsContainerLayout.ReactionsContainerDelegate {
        AnonymousClass3() {
        }

        public void lambda$onReactionClicked$0() {
            if (ChatSelectionReactionMenuOverlay.this.reactionsContainerLayout != null) {
                ChatSelectionReactionMenuOverlay.this.reactionsContainerLayout.dismissParent(true);
            }
            hideMenu();
        }

        @Override
        public boolean drawBackground() {
            return ReactionsContainerLayout.ReactionsContainerDelegate.CC.$default$drawBackground(this);
        }

        @Override
        public void drawRoundRect(Canvas canvas, RectF rectF, float f, float f2, float f3, int i, boolean z) {
            ReactionsContainerLayout.ReactionsContainerDelegate.CC.$default$drawRoundRect(this, canvas, rectF, f, f2, f3, i, z);
        }

        public void hideMenu() {
            ChatSelectionReactionMenuOverlay.this.parentFragment.clearSelectionMode(true);
        }

        @Override
        public boolean needEnterText() {
            return ReactionsContainerLayout.ReactionsContainerDelegate.CC.$default$needEnterText(this);
        }

        @Override
        public void onEmojiWindowDismissed() {
            ReactionsContainerLayout.ReactionsContainerDelegate.CC.$default$onEmojiWindowDismissed(this);
        }

        @Override
        public void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2) {
            ChatSelectionReactionMenuOverlay.this.parentFragment.selectReaction(null, ChatSelectionReactionMenuOverlay.this.currentPrimaryObject, ChatSelectionReactionMenuOverlay.this.reactionsContainerLayout, view, 0.0f, 0.0f, visibleReaction, false, z, z2, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatSelectionReactionMenuOverlay.AnonymousClass3.this.lambda$onReactionClicked$0();
                }
            });
        }
    }

    public ChatSelectionReactionMenuOverlay(ChatActivity chatActivity, Context context) {
        super(context);
        this.selectedMessages = Collections.emptyList();
        this.mPadding = 22;
        this.mSidePadding = 24;
        this.pos = new int[2];
        setVisibility(8);
        this.parentFragment = chatActivity;
        setClipToPadding(false);
        setClipChildren(false);
        chatActivity.getChatListView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatSelectionReactionMenuOverlay.this.invalidatePosition();
            }
        });
    }

    private void animateVisible(boolean z) {
        if (z) {
            setVisibility(0);
            post(new Runnable() {
                @Override
                public final void run() {
                    ChatSelectionReactionMenuOverlay.this.lambda$animateVisible$0();
                }
            });
            return;
        }
        this.messageSet = false;
        ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(150L);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatSelectionReactionMenuOverlay.this.lambda$animateVisible$1(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ChatSelectionReactionMenuOverlay.this.setVisibility(8);
                if (ChatSelectionReactionMenuOverlay.this.reactionsContainerLayout != null) {
                    ChatSelectionReactionMenuOverlay chatSelectionReactionMenuOverlay = ChatSelectionReactionMenuOverlay.this;
                    chatSelectionReactionMenuOverlay.removeView(chatSelectionReactionMenuOverlay.reactionsContainerLayout);
                    ChatSelectionReactionMenuOverlay.this.reactionsContainerLayout = null;
                }
                ChatSelectionReactionMenuOverlay.this.currentPrimaryObject = null;
            }
        });
        duration.start();
    }

    private void checkCreateReactionsLayout() {
        if (this.reactionsContainerLayout == null) {
            ReactionsContainerLayout reactionsContainerLayout = new ReactionsContainerLayout((this.parentFragment.getUserConfig().getClientUserId() > this.parentFragment.getDialogId() ? 1 : (this.parentFragment.getUserConfig().getClientUserId() == this.parentFragment.getDialogId() ? 0 : -1)) == 0 ? 3 : 0, this.parentFragment, getContext(), this.parentFragment.getCurrentAccount(), this.parentFragment.getResourceProvider()) {
                float enabledAlpha = 1.0f;
                long lastUpdate;

                {
                    setWillNotDraw(false);
                }

                @Override
                public void draw(Canvas canvas) {
                    long min = Math.min(16L, System.currentTimeMillis() - this.lastUpdate);
                    this.lastUpdate = System.currentTimeMillis();
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                    canvas.saveLayerAlpha(rectF, (int) (this.enabledAlpha * 255.0f), 31);
                    super.draw(canvas);
                    canvas.restore();
                    if (!isEnabled()) {
                        float f = this.enabledAlpha;
                        if (f != 0.0f) {
                            this.enabledAlpha = Math.max(0.0f, f - (((float) min) / 150.0f));
                            invalidate();
                            if (this.enabledAlpha == 0.0f) {
                                setVisibility(8);
                                return;
                            }
                            return;
                        }
                    }
                    if (isEnabled()) {
                        float f2 = this.enabledAlpha;
                        if (f2 != 1.0f) {
                            this.enabledAlpha = Math.min(1.0f, f2 + (((float) min) / 150.0f));
                            invalidate();
                        }
                    }
                }

                @Override
                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (i != 8 || this.enabledAlpha == 0.0f) {
                        return;
                    }
                    this.enabledAlpha = 0.0f;
                }
            };
            this.reactionsContainerLayout = reactionsContainerLayout;
            reactionsContainerLayout.setPadding(AndroidUtilities.dp(4.0f) + (LocaleController.isRTL ? 0 : this.mSidePadding), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + (LocaleController.isRTL ? this.mSidePadding : 0), AndroidUtilities.dp(this.mPadding));
            this.reactionsContainerLayout.setDelegate(new AnonymousClass3());
            this.reactionsContainerLayout.setClipChildren(false);
            this.reactionsContainerLayout.setClipToPadding(false);
            addView(this.reactionsContainerLayout, LayoutHelper.createFrame(-2, this.mPadding + 70, 5));
        }
    }

    private MessageObject findPrimaryObject() {
        MessageObject.GroupedMessages group;
        ArrayList<MessageObject> arrayList;
        TLRPC.TL_messageReactions tL_messageReactions;
        ArrayList<TLRPC.ReactionCount> arrayList2;
        if (!this.isVisible || this.selectedMessages.isEmpty()) {
            return null;
        }
        MessageObject messageObject = (MessageObject) this.selectedMessages.get(0);
        if (messageObject.getGroupId() != 0 && (group = this.parentFragment.getGroup(messageObject.getGroupId())) != null && (arrayList = group.messages) != null) {
            Iterator<MessageObject> it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject next = it.next();
                TLRPC.Message message = next.messageOwner;
                if (message != null && (tL_messageReactions = message.reactions) != null && (arrayList2 = tL_messageReactions.results) != null && !arrayList2.isEmpty()) {
                    return next;
                }
            }
        }
        return messageObject;
    }

    private boolean isMessageTypeAllowed(MessageObject messageObject) {
        return (messageObject == null || messageObject.needDrawBluredPreview() || ((!MessageObject.isPhoto(messageObject.messageOwner) || MessageObject.getMedia(messageObject.messageOwner).webpage != null) && (messageObject.getDocument() == null || (!MessageObject.isVideoDocument(messageObject.getDocument()) && !MessageObject.isGifDocument(messageObject.getDocument()))))) ? false : true;
    }

    public void lambda$animateVisible$0() {
        this.currentPrimaryObject = findPrimaryObject();
        checkCreateReactionsLayout();
        invalidatePosition(false);
        if (!this.reactionsContainerLayout.isEnabled()) {
            this.messageSet = false;
            this.reactionsContainerLayout.setTransitionProgress(1.0f);
        } else {
            this.messageSet = true;
            this.reactionsContainerLayout.setMessage(this.currentPrimaryObject, this.parentFragment.getCurrentChatInfo(), true);
            this.reactionsContainerLayout.startEnterAnimation(false);
        }
    }

    public void lambda$animateVisible$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        ReactionsContainerLayout reactionsContainerLayout = this.reactionsContainerLayout;
        if (reactionsContainerLayout != null) {
            reactionsContainerLayout.setAlpha(floatValue);
        }
    }

    public void invalidatePosition() {
        invalidatePosition(true);
    }

    public void invalidatePosition(boolean r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ChatSelectionReactionMenuOverlay.invalidatePosition(boolean):void");
    }

    public boolean isVisible() {
        return this.isVisible && !this.hiddenByScroll;
    }

    public boolean onBackPressed() {
        ReactionsContainerLayout reactionsContainerLayout = this.reactionsContainerLayout;
        if (reactionsContainerLayout == null || reactionsContainerLayout.getReactionsWindow() == null) {
            return true;
        }
        this.reactionsContainerLayout.dismissWindow();
        return false;
    }

    public void setHiddenByScroll(boolean z) {
        this.hiddenByScroll = z;
        if (z) {
            animateVisible(false);
        }
    }

    public void setSelectedMessages(List<MessageObject> list) {
        boolean z;
        this.selectedMessages = list;
        if (!this.parentFragment.isSecretChat() && ((this.parentFragment.getCurrentChatInfo() == null || !(this.parentFragment.getCurrentChatInfo().available_reactions instanceof TLRPC.TL_chatReactionsNone)) && !list.isEmpty())) {
            Iterator<MessageObject> it = list.iterator();
            long j = 0;
            boolean z2 = false;
            while (true) {
                z = true;
                if (!it.hasNext()) {
                    break;
                }
                MessageObject next = it.next();
                if (!isMessageTypeAllowed(next)) {
                    break;
                }
                if (!z2) {
                    j = next.getGroupId();
                    z2 = true;
                } else if (j != next.getGroupId() || j == 0) {
                    break;
                }
            }
        }
        z = false;
        if (z != this.isVisible) {
            this.isVisible = z;
            this.hiddenByScroll = false;
            animateVisible(z);
        } else if (z) {
            this.currentPrimaryObject = findPrimaryObject();
        }
    }
}
