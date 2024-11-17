package org.telegram.messenger;

import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.MessagePreviewView;

public class MessagePreviewParams {
    public CharacterStyle currentLink;
    public Messages forwardMessages;
    public boolean hasCaption;
    public boolean hasMedia;
    public boolean hasSecretMessages;
    public boolean hasSenders;
    public boolean hideCaption;
    public boolean hideForwardSendersName;
    public boolean isSecret;
    public boolean isVideo;
    public TLRPC.WebPage linkMedia;
    public Messages linkMessage;
    public boolean multipleUsers;
    public boolean noforwards;
    private MessagePreviewView previewView;
    public ChatActivity.ReplyQuote quote;
    public int quoteEnd;
    public int quoteStart;
    public Messages replyMessage;
    public boolean singleLink;
    public TLRPC.WebPage webpage;
    public boolean webpagePhoto;
    public boolean webpageSmall;
    public boolean webpageTop;
    public boolean willSeeSenders;

    public class Messages {
        private long dialogId;
        public LongSparseArray<MessageObject.GroupedMessages> groupedMessagesMap;
        public boolean hasSpoilers;
        public boolean hasText;
        public ArrayList<MessageObject> messages;
        private Boolean out;
        public ArrayList<TLRPC.TL_pollAnswerVoters> pollChosenAnswers;
        public ArrayList<MessageObject> previewMessages;
        public SparseBooleanArray selectedIds;
        private int type;

        public Messages(Boolean bool, int i, ArrayList<MessageObject> arrayList, long j, SparseBooleanArray sparseBooleanArray) {
            this.groupedMessagesMap = new LongSparseArray<>();
            this.previewMessages = new ArrayList<>();
            this.selectedIds = new SparseBooleanArray();
            this.pollChosenAnswers = new ArrayList<>();
            this.out = bool;
            this.type = i;
            this.dialogId = j;
            this.messages = arrayList;
            if (sparseBooleanArray != null) {
                this.selectedIds = sparseBooleanArray;
            }
            int i2 = 0;
            int i3 = 0;
            while (i3 < arrayList.size()) {
                MessageObject messageObject = arrayList.get(i3);
                if (i == 0 && sparseBooleanArray == null) {
                    this.selectedIds.put(messageObject.getId(), true);
                }
                MessageObject previewMessage = MessagePreviewParams.this.toPreviewMessage(messageObject, bool, i);
                if (!this.hasSpoilers) {
                    Iterator<TLRPC.MessageEntity> it = previewMessage.messageOwner.entities.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (it.next() instanceof TLRPC.TL_messageEntitySpoiler) {
                                this.hasSpoilers = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                previewMessage.messageOwner.dialog_id = j;
                if (previewMessage.getGroupId() != 0) {
                    MessageObject.GroupedMessages groupedMessages = this.groupedMessagesMap.get(previewMessage.getGroupId(), null);
                    if (groupedMessages == null) {
                        groupedMessages = new MessageObject.GroupedMessages();
                        this.groupedMessagesMap.put(previewMessage.getGroupId(), groupedMessages);
                    }
                    groupedMessages.messages.add(previewMessage);
                }
                this.previewMessages.add(i2, previewMessage);
                if (messageObject.isPoll()) {
                    TLRPC.TL_messageMediaPoll tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) messageObject.messageOwner.media;
                    PreviewMediaPoll previewMediaPoll = new PreviewMediaPoll();
                    previewMediaPoll.poll = tL_messageMediaPoll.poll;
                    previewMediaPoll.provider = tL_messageMediaPoll.provider;
                    TLRPC.TL_pollResults tL_pollResults = new TLRPC.TL_pollResults();
                    previewMediaPoll.results = tL_pollResults;
                    int i4 = tL_messageMediaPoll.results.total_voters;
                    tL_pollResults.total_voters = i4;
                    previewMediaPoll.totalVotersCached = i4;
                    previewMessage.messageOwner.media = previewMediaPoll;
                    if (messageObject.canUnvote()) {
                        int size = tL_messageMediaPoll.results.results.size();
                        for (int i5 = 0; i5 < size; i5++) {
                            TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters = tL_messageMediaPoll.results.results.get(i5);
                            if (tL_pollAnswerVoters.chosen) {
                                TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters2 = new TLRPC.TL_pollAnswerVoters();
                                tL_pollAnswerVoters2.chosen = tL_pollAnswerVoters.chosen;
                                tL_pollAnswerVoters2.correct = tL_pollAnswerVoters.correct;
                                tL_pollAnswerVoters2.flags = tL_pollAnswerVoters.flags;
                                tL_pollAnswerVoters2.option = tL_pollAnswerVoters.option;
                                tL_pollAnswerVoters2.voters = tL_pollAnswerVoters.voters;
                                this.pollChosenAnswers.add(tL_pollAnswerVoters2);
                                previewMediaPoll.results.results.add(tL_pollAnswerVoters2);
                            } else {
                                previewMediaPoll.results.results.add(tL_pollAnswerVoters);
                            }
                        }
                    }
                }
                i3++;
                i2 = 0;
            }
            for (int i6 = 0; i6 < this.groupedMessagesMap.size(); i6++) {
                this.groupedMessagesMap.valueAt(i6).calculate();
            }
            LongSparseArray<MessageObject.GroupedMessages> longSparseArray = this.groupedMessagesMap;
            if (longSparseArray != null && longSparseArray.size() > 0) {
                this.hasText = this.groupedMessagesMap.valueAt(0).findCaptionMessageObject() != null;
            } else if (arrayList.size() == 1) {
                int i7 = arrayList.get(0).type;
                this.hasText = !TextUtils.isEmpty((i7 == 0 || i7 == 19) ? r1.messageText : r1.caption);
            }
        }

        public Messages(MessagePreviewParams messagePreviewParams, Boolean bool, int i, MessageObject messageObject) {
            this(bool, i, MessagePreviewParams.singletonArrayList(messageObject), messageObject.getDialogId(), null);
        }

        public Messages(MessagePreviewParams messagePreviewParams, Boolean bool, int i, MessageObject messageObject, long j) {
            this(bool, i, MessagePreviewParams.singletonArrayList(messageObject), j, null);
        }

        public Messages checkEdits(ArrayList<MessageObject> arrayList) {
            ArrayList<MessageObject> arrayList2 = this.messages;
            if (arrayList2 != null && arrayList2.size() <= 1 && arrayList != null) {
                boolean z = false;
                for (int i = 0; i < this.messages.size(); i++) {
                    MessageObject messageObject = this.messages.get(i);
                    if (messageObject != null) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= arrayList.size()) {
                                break;
                            }
                            MessageObject messageObject2 = arrayList.get(i2);
                            if (messageObject2 != null && messageObject.getId() == messageObject2.getId() && messageObject.getDialogId() == messageObject2.getDialogId()) {
                                this.messages.set(i, messageObject2);
                                z = true;
                                break;
                            }
                            i2++;
                        }
                    }
                }
                if (z) {
                    return new Messages(this.out, this.type, this.messages, this.dialogId, null);
                }
            }
            return null;
        }

        public void getSelectedMessages(ArrayList<MessageObject> arrayList) {
            arrayList.clear();
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject messageObject = this.messages.get(i);
                if (this.selectedIds.get(messageObject.getId(), false)) {
                    arrayList.add(messageObject);
                }
            }
        }
    }

    public static class PreviewMediaPoll extends TLRPC.TL_messageMediaPoll {
        public int totalVotersCached;
    }

    public MessagePreviewParams(boolean z, boolean z2) {
        this.isSecret = z;
        this.noforwards = z || z2;
    }

    public static boolean areUrlsEqual(String str, String str2) {
        if (str == null || str2 == null) {
            return str == null;
        }
        Uri parse = Uri.parse(str);
        Uri parse2 = Uri.parse(str2);
        if (parse == parse2) {
            return true;
        }
        if (parse != null && parse2 != null && parse.getHost() != null && parse.getHost().equalsIgnoreCase(parse2.getHost()) && parse.getPort() == parse2.getPort() && normalizePath(parse.getPath()).equals(normalizePath(parse2.getPath()))) {
            if (parse.getQuery() == null) {
                if (parse2.getQuery() == null) {
                    return true;
                }
            } else if (parse.getQuery().equals(parse2.getQuery())) {
                return true;
            }
        }
        return false;
    }

    private static String normalizePath(String str) {
        if (str == null) {
            return "";
        }
        if (str.endsWith("/")) {
            return str;
        }
        return str + "/";
    }

    public static ArrayList<MessageObject> singletonArrayList(MessageObject messageObject) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(messageObject);
        return arrayList;
    }

    public org.telegram.messenger.MessageObject toPreviewMessage(org.telegram.messenger.MessageObject r12, java.lang.Boolean r13, final int r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagePreviewParams.toPreviewMessage(org.telegram.messenger.MessageObject, java.lang.Boolean, int):org.telegram.messenger.MessageObject");
    }

    public void attach(MessagePreviewView messagePreviewView) {
        this.previewView = messagePreviewView;
    }

    public void checkCurrentLink(MessageObject messageObject) {
        TLRPC.WebPage webPage;
        this.currentLink = null;
        if (messageObject != null) {
            CharSequence charSequence = messageObject.messageText;
            if (!(charSequence instanceof Spanned) || (webPage = this.webpage) == null || webPage.url == null) {
                return;
            }
            Spanned spanned = (Spanned) charSequence;
            URLSpan[] uRLSpanArr = (URLSpan[]) spanned.getSpans(0, spanned.length(), URLSpan.class);
            for (int i = 0; i < uRLSpanArr.length; i++) {
                if (areUrlsEqual(uRLSpanArr[i].getURL(), this.webpage.url)) {
                    this.currentLink = uRLSpanArr[i];
                    return;
                }
            }
        }
    }

    public void checkEdits(ArrayList<MessageObject> arrayList) {
        boolean z;
        MessagePreviewView messagePreviewView;
        Messages checkEdits;
        Messages checkEdits2;
        Messages checkEdits3;
        Messages messages = this.forwardMessages;
        boolean z2 = true;
        if (messages == null || (checkEdits3 = messages.checkEdits(arrayList)) == null) {
            z = false;
        } else {
            this.forwardMessages = checkEdits3;
            z = true;
        }
        Messages messages2 = this.replyMessage;
        if (messages2 != null && (checkEdits2 = messages2.checkEdits(arrayList)) != null) {
            this.replyMessage = checkEdits2;
            z = true;
        }
        Messages messages3 = this.linkMessage;
        if (messages3 == null || (checkEdits = messages3.checkEdits(arrayList)) == null) {
            z2 = z;
        } else {
            this.linkMessage = checkEdits;
        }
        if (!z2 || (messagePreviewView = this.previewView) == null) {
            return;
        }
        messagePreviewView.updateAll();
    }

    public boolean hasLink(CharSequence charSequence, String str) {
        if (str != null) {
            try {
                SpannableString valueOf = SpannableString.valueOf(charSequence);
                try {
                    AndroidUtilities.addLinksSafe(valueOf, 1, false, true);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                for (URLSpan uRLSpan : (URLSpan[]) valueOf.getSpans(0, valueOf.length(), URLSpan.class)) {
                    if (areUrlsEqual(uRLSpan.getURL(), str)) {
                        return true;
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return false;
    }

    public boolean isEmpty() {
        Messages messages;
        Messages messages2;
        ArrayList<MessageObject> arrayList;
        ArrayList<MessageObject> arrayList2;
        ArrayList<MessageObject> arrayList3;
        Messages messages3 = this.forwardMessages;
        return (messages3 == null || (arrayList3 = messages3.messages) == null || arrayList3.isEmpty()) && ((messages = this.replyMessage) == null || (arrayList2 = messages.messages) == null || arrayList2.isEmpty()) && ((messages2 = this.linkMessage) == null || (arrayList = messages2.messages) == null || arrayList.isEmpty());
    }

    public void updateForward(ArrayList<MessageObject> arrayList, long j) {
        long j2;
        TLRPC.MessageFwdHeader messageFwdHeader;
        this.hasCaption = false;
        this.hasSenders = false;
        this.isSecret = DialogObject.isEncryptedDialog(j);
        this.multipleUsers = false;
        if (arrayList == null) {
            this.forwardMessages = null;
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = arrayList.get(i);
            if (!TextUtils.isEmpty(messageObject.caption)) {
                this.hasCaption = true;
            }
            if (!this.isSecret && (messageFwdHeader = messageObject.messageOwner.fwd_from) != null && messageFwdHeader.from_id == null && !arrayList2.contains(messageFwdHeader.from_name)) {
                arrayList2.add(messageFwdHeader.from_name);
            }
        }
        Boolean bool = Boolean.TRUE;
        Messages messages = this.forwardMessages;
        Messages messages2 = new Messages(bool, 0, arrayList, j, messages != null ? messages.selectedIds : null);
        this.forwardMessages = messages2;
        if (messages2.messages.isEmpty()) {
            this.forwardMessages = null;
        }
        ArrayList arrayList3 = new ArrayList();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            MessageObject messageObject2 = arrayList.get(i2);
            if (messageObject2.isFromUser()) {
                j2 = messageObject2.messageOwner.from_id.user_id;
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(messageObject2.currentAccount).getChat(Long.valueOf(messageObject2.messageOwner.peer_id.channel_id));
                j2 = -((ChatObject.isChannel(chat) && chat.megagroup && messageObject2.isForwardedChannelPost()) ? messageObject2.messageOwner.fwd_from.from_id : messageObject2.messageOwner.peer_id).channel_id;
            }
            if (!arrayList3.contains(Long.valueOf(j2))) {
                arrayList3.add(Long.valueOf(j2));
            }
        }
        if (arrayList3.size() + arrayList2.size() > 1) {
            this.multipleUsers = true;
        }
    }

    public void updateLink(int r8, org.telegram.tgnet.TLRPC.WebPage r9, java.lang.CharSequence r10, org.telegram.messenger.MessageObject r11, org.telegram.ui.ChatActivity.ReplyQuote r12, org.telegram.messenger.MessageObject r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagePreviewParams.updateLink(int, org.telegram.tgnet.TLRPC$WebPage, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.ui.ChatActivity$ReplyQuote, org.telegram.messenger.MessageObject):void");
    }

    public void updateLinkInvertMedia(boolean z) {
        this.webpageTop = z;
    }

    public void updateReply(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, long j, ChatActivity.ReplyQuote replyQuote) {
        ChatActivity.ReplyQuote replyQuote2;
        int i;
        MessageObject messageObject2 = messageObject;
        if (this.isSecret || messageObject2 == null || (i = messageObject2.type) == 10 || i == 11 || i == 22 || i == 21 || i == 18 || i == 25 || i == 16) {
            messageObject2 = null;
            replyQuote2 = null;
        } else {
            replyQuote2 = replyQuote;
        }
        this.hasSecretMessages = messageObject2 != null && (messageObject2.isVoiceOnce() || messageObject2.isRoundOnce() || messageObject2.type == 30);
        if (messageObject2 == null && replyQuote2 == null) {
            this.replyMessage = null;
            this.quote = null;
            return;
        }
        if (groupedMessages != null) {
            this.replyMessage = new Messages(null, 1, groupedMessages.messages, j, null);
        } else {
            if (messageObject2 == null) {
                messageObject2 = replyQuote2.message;
            }
            this.replyMessage = new Messages(this, null, 1, messageObject2, j);
        }
        if (this.replyMessage.messages.isEmpty()) {
            this.replyMessage = null;
            return;
        }
        this.quote = replyQuote2;
        if (replyQuote2 != null) {
            this.quoteStart = replyQuote2.start;
            this.quoteEnd = replyQuote2.end;
        }
    }
}
