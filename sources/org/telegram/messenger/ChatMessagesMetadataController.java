package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Stories.StoriesStorage;

public class ChatMessagesMetadataController {
    final ChatActivity chatActivity;
    private ArrayList<MessageObject> reactionsToCheck = new ArrayList<>(10);
    private ArrayList<MessageObject> extendedMediaToCheck = new ArrayList<>(10);
    private ArrayList<MessageObject> storiesToCheck = new ArrayList<>(10);
    ArrayList<Integer> reactionsRequests = new ArrayList<>();
    ArrayList<Integer> extendedMediaRequests = new ArrayList<>();

    public ChatMessagesMetadataController(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }

    public void lambda$loadExtendedMediaForMessages$5(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.chatActivity.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$loadReactionsForMessages$3(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            for (int i = 0; i < updates.updates.size(); i++) {
                if (updates.updates.get(i) instanceof TLRPC.TL_updateMessageReactions) {
                    ((TLRPC.TL_updateMessageReactions) updates.updates.get(i)).updateUnreadState = false;
                }
            }
            this.chatActivity.getMessagesController().processUpdates(updates, false);
        }
    }

    public void lambda$loadReactionsForMessages$4(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatMessagesMetadataController.this.lambda$loadReactionsForMessages$3(tL_error, tLObject);
            }
        });
    }

    public void lambda$loadStoriesForMessages$0(ArrayList arrayList) {
        this.chatActivity.getMessagesController().getStoriesController().getStoriesStorage().lambda$fillMessagesWithStories$13(arrayList);
    }

    public void lambda$loadStoriesForMessages$1(MessageObject messageObject, long j, TL_stories.StoryItem storyItem) {
        ChatActivity chatActivity;
        boolean isExpiredStory = messageObject.isExpiredStory();
        StoriesStorage.applyStory(this.chatActivity.getCurrentAccount(), j, messageObject, storyItem);
        final ArrayList arrayList = new ArrayList();
        boolean z = true;
        messageObject.forceUpdate = true;
        arrayList.add(messageObject);
        this.chatActivity.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                ChatMessagesMetadataController.this.lambda$loadStoriesForMessages$0(arrayList);
            }
        });
        if (!isExpiredStory && messageObject.isExpiredStory() && messageObject.type == 24) {
            chatActivity = this.chatActivity;
        } else {
            chatActivity = this.chatActivity;
            z = false;
        }
        chatActivity.updateMessages(arrayList, z);
    }

    public void lambda$loadStoriesForMessages$2(int i, final MessageObject messageObject, final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TL_stories.TL_stories_stories tL_stories_stories = (TL_stories.TL_stories_stories) tLObject;
            TL_stories.StoryItem storyItem = tL_stories_stories.stories.size() > 0 ? tL_stories_stories.stories.get(0) : null;
            if (storyItem == null) {
                storyItem = new TL_stories.TL_storyItemDeleted();
            }
            final TL_stories.StoryItem storyItem2 = storyItem;
            storyItem2.lastUpdateTime = System.currentTimeMillis();
            storyItem2.id = i;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatMessagesMetadataController.this.lambda$loadStoriesForMessages$1(messageObject, j, storyItem2);
                }
            });
        }
    }

    private void loadStoriesForMessages(long j, ArrayList<MessageObject> arrayList) {
        TL_stories.StoryItem storyItem;
        if (arrayList.isEmpty()) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TL_stories.TL_stories_getStoriesByID tL_stories_getStoriesByID = new TL_stories.TL_stories_getStoriesByID();
            final MessageObject messageObject = arrayList.get(i);
            new TL_stories.TL_storyItem();
            int i2 = messageObject.type;
            if (i2 == 23 || i2 == 24) {
                TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                TL_stories.StoryItem storyItem2 = messageMedia.storyItem;
                storyItem2.dialogId = messageMedia.user_id;
                storyItem = storyItem2;
            } else {
                TLRPC.Message message = messageObject.messageOwner;
                TLRPC.MessageReplyHeader messageReplyHeader = message.reply_to;
                if (messageReplyHeader != null) {
                    storyItem = message.replyStory;
                    storyItem.dialogId = DialogObject.getPeerDialogId(messageReplyHeader.peer);
                }
            }
            final long j2 = storyItem.dialogId;
            tL_stories_getStoriesByID.peer = this.chatActivity.getMessagesController().getInputPeer(j2);
            tL_stories_getStoriesByID.id.add(Integer.valueOf(storyItem.id));
            final int i3 = storyItem.id;
            this.extendedMediaRequests.add(Integer.valueOf(this.chatActivity.getConnectionsManager().sendRequest(tL_stories_getStoriesByID, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatMessagesMetadataController.this.lambda$loadStoriesForMessages$2(i3, messageObject, j2, tLObject, tL_error);
                }
            })));
        }
        if (this.extendedMediaRequests.size() > 10) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.extendedMediaRequests.remove(0).intValue(), false);
        }
    }

    public void checkMessages(ChatActivity.ChatActivityAdapter chatActivityAdapter, int i, int i2, long j) {
        ArrayList messages = chatActivityAdapter.getMessages();
        if (this.chatActivity.isInScheduleMode() || i < 0 || i2 < 0) {
            return;
        }
        int i3 = chatActivityAdapter.messagesStartRow;
        int i4 = (i2 - i3) - 10;
        int i5 = (i - i3) + 10;
        if (i4 < 0) {
            i4 = 0;
        }
        if (i5 > messages.size()) {
            i5 = messages.size();
        }
        this.reactionsToCheck.clear();
        this.extendedMediaToCheck.clear();
        this.storiesToCheck.clear();
        while (i4 < i5) {
            MessageObject messageObject = (MessageObject) messages.get(i4);
            if (this.chatActivity.getThreadMessage() != messageObject && messageObject.getId() > 0 && messageObject.messageOwner.action == null && j - messageObject.reactionsLastCheckTime > 15000) {
                messageObject.reactionsLastCheckTime = j;
                this.reactionsToCheck.add(messageObject);
            }
            if (this.chatActivity.getThreadMessage() != messageObject && messageObject.getId() > 0 && ((messageObject.hasExtendedMediaPreview() || messageObject.hasPaidMediaPreview()) && j - messageObject.extendedMediaLastCheckTime > 30000)) {
                messageObject.extendedMediaLastCheckTime = j;
                this.extendedMediaToCheck.add(messageObject);
            }
            int i6 = messageObject.type;
            if (i6 == 23 || i6 == 24 || messageObject.messageOwner.replyStory != null) {
                TL_stories.StoryItem storyItem = (i6 == 23 || i6 == 24) ? messageObject.messageOwner.media.storyItem : messageObject.messageOwner.replyStory;
                if (storyItem != null && !(storyItem instanceof TL_stories.TL_storyItemDeleted) && j - storyItem.lastUpdateTime > 300000) {
                    storyItem.lastUpdateTime = j;
                    this.storiesToCheck.add(messageObject);
                }
            }
            i4++;
        }
        loadReactionsForMessages(this.chatActivity.getDialogId(), this.reactionsToCheck);
        loadExtendedMediaForMessages(this.chatActivity.getDialogId(), this.extendedMediaToCheck);
        loadStoriesForMessages(this.chatActivity.getDialogId(), this.storiesToCheck);
    }

    public void loadExtendedMediaForMessages(long j, ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        TLRPC.TL_messages_getExtendedMedia tL_messages_getExtendedMedia = new TLRPC.TL_messages_getExtendedMedia();
        tL_messages_getExtendedMedia.peer = this.chatActivity.getMessagesController().getInputPeer(j);
        for (int i = 0; i < arrayList.size(); i++) {
            tL_messages_getExtendedMedia.id.add(Integer.valueOf(arrayList.get(i).getId()));
        }
        this.extendedMediaRequests.add(Integer.valueOf(this.chatActivity.getConnectionsManager().sendRequest(tL_messages_getExtendedMedia, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatMessagesMetadataController.this.lambda$loadExtendedMediaForMessages$5(tLObject, tL_error);
            }
        })));
        if (this.extendedMediaRequests.size() > 10) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.extendedMediaRequests.remove(0).intValue(), false);
        }
    }

    public void loadReactionsForMessages(long j, ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        TLRPC.TL_messages_getMessagesReactions tL_messages_getMessagesReactions = new TLRPC.TL_messages_getMessagesReactions();
        tL_messages_getMessagesReactions.peer = this.chatActivity.getMessagesController().getInputPeer(j);
        for (int i = 0; i < arrayList.size(); i++) {
            tL_messages_getMessagesReactions.id.add(Integer.valueOf(arrayList.get(i).getId()));
        }
        this.reactionsRequests.add(Integer.valueOf(this.chatActivity.getConnectionsManager().sendRequest(tL_messages_getMessagesReactions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatMessagesMetadataController.this.lambda$loadReactionsForMessages$4(tLObject, tL_error);
            }
        })));
        if (this.reactionsRequests.size() > 5) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.reactionsRequests.remove(0).intValue(), true);
        }
    }

    public void onFragmentDestroy() {
        for (int i = 0; i < this.reactionsRequests.size(); i++) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.reactionsRequests.get(i).intValue(), false);
        }
        this.reactionsRequests.clear();
        for (int i2 = 0; i2 < this.extendedMediaRequests.size(); i2++) {
            this.chatActivity.getConnectionsManager().cancelRequest(this.extendedMediaRequests.get(i2).intValue(), false);
        }
        this.extendedMediaRequests.clear();
    }
}
