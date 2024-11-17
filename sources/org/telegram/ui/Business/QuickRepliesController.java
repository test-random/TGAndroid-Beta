package org.telegram.ui.Business;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Business.QuickRepliesController;

public class QuickRepliesController {
    private static volatile QuickRepliesController[] Instance = new QuickRepliesController[4];
    private static final Object[] lockObjects = new Object[4];
    public final int currentAccount;
    private boolean loaded;
    private boolean loading;
    public final ArrayList replies = new ArrayList();
    public final ArrayList localReplies = new ArrayList();
    private ArrayList filtered = new ArrayList();

    public class QuickReply {
        public int id;
        public boolean local;
        public HashSet localIds = new HashSet();
        public int messagesCount;
        public String name;
        public int order;
        public MessageObject topMessage;
        public int topMessageId;

        public QuickReply() {
        }

        public int getMessagesCount() {
            return this.local ? this.localIds.size() : this.messagesCount;
        }

        public int getTopMessageId() {
            MessageObject messageObject = this.topMessage;
            return messageObject != null ? messageObject.getId() : this.topMessageId;
        }

        public boolean isSpecial() {
            return QuickRepliesController.isSpecial(this.name);
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private QuickRepliesController(int i) {
        this.currentAccount = i;
    }

    private void addReply(final QuickReply quickReply) {
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.lambda$addReply$5(MessagesStorage.this, quickReply);
            }
        });
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    private void ensureLoaded(Runnable runnable) {
        if (this.loaded) {
            runnable.run();
        } else {
            load(true, runnable);
        }
    }

    public static QuickRepliesController getInstance(int i) {
        QuickRepliesController quickRepliesController = Instance[i];
        if (quickRepliesController == null) {
            synchronized (lockObjects[i]) {
                try {
                    quickRepliesController = Instance[i];
                    if (quickRepliesController == null) {
                        QuickRepliesController[] quickRepliesControllerArr = Instance;
                        QuickRepliesController quickRepliesController2 = new QuickRepliesController(i);
                        quickRepliesControllerArr[i] = quickRepliesController2;
                        quickRepliesController = quickRepliesController2;
                    }
                } finally {
                }
            }
        }
        return quickRepliesController;
    }

    public static boolean isSpecial(String str) {
        return "hello".equalsIgnoreCase(str) || "away".equalsIgnoreCase(str);
    }

    public static void lambda$addReply$5(MessagesStorage messagesStorage, QuickReply quickReply) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = messagesStorage.getDatabase().executeFast("REPLACE INTO business_replies VALUES(?, ?, ?, ?);");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, quickReply.id);
                sQLitePreparedStatement.bindString(2, quickReply.name);
                sQLitePreparedStatement.bindInteger(3, quickReply.order);
                sQLitePreparedStatement.bindInteger(4, quickReply.messagesCount);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$checkLocalMessages$23() {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public static void lambda$deleteReplies$12() {
    }

    public static void lambda$deleteReplies$13(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.lambda$deleteReplies$12();
            }
        });
    }

    public static void lambda$deleteReplies$14(MessagesStorage messagesStorage, ArrayList arrayList) {
        try {
            messagesStorage.getDatabase().executeFast(String.format("DELETE FROM quick_replies_messages WHERE topic_id IN (%s)", TextUtils.join(", ", arrayList))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$load$0(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, Runnable runnable) {
        this.loading = false;
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        this.replies.clear();
        this.replies.addAll(arrayList3);
        if (runnable != null) {
            runnable.run();
        } else {
            load(false);
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public void lambda$load$1(org.telegram.messenger.MessagesStorage r18, long r19, final java.lang.Runnable r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Business.QuickRepliesController.lambda$load$1(org.telegram.messenger.MessagesStorage, long, java.lang.Runnable):void");
    }

    public void lambda$load$2(TLObject tLObject) {
        TLRPC.Message message;
        ArrayList arrayList = null;
        if (tLObject instanceof TLRPC.TL_messages_quickReplies) {
            TLRPC.TL_messages_quickReplies tL_messages_quickReplies = (TLRPC.TL_messages_quickReplies) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_messages_quickReplies.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_messages_quickReplies.chats, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_messages_quickReplies.users, tL_messages_quickReplies.chats, true, true);
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < tL_messages_quickReplies.quick_replies.size(); i++) {
                TLRPC.TL_quickReply tL_quickReply = tL_messages_quickReplies.quick_replies.get(i);
                QuickReply quickReply = new QuickReply();
                quickReply.id = tL_quickReply.shortcut_id;
                quickReply.name = tL_quickReply.shortcut;
                quickReply.messagesCount = tL_quickReply.count;
                quickReply.topMessageId = tL_quickReply.top_message;
                quickReply.order = i;
                int i2 = 0;
                while (true) {
                    if (i2 >= tL_messages_quickReplies.messages.size()) {
                        message = null;
                        break;
                    }
                    message = tL_messages_quickReplies.messages.get(i2);
                    if (message.id == tL_quickReply.top_message) {
                        break;
                    } else {
                        i2++;
                    }
                }
                if (message != null) {
                    MessageObject messageObject = new MessageObject(this.currentAccount, message, false, true);
                    quickReply.topMessage = messageObject;
                    messageObject.generateThumbs(false);
                    quickReply.topMessage.applyQuickReply(tL_quickReply.shortcut, tL_quickReply.shortcut_id);
                }
                arrayList2.add(quickReply);
            }
            arrayList = arrayList2;
        } else {
            boolean z = tLObject instanceof TLRPC.TL_messages_quickRepliesNotModified;
        }
        this.loading = false;
        if (arrayList != null) {
            this.replies.clear();
            this.replies.addAll(arrayList);
        }
        this.loaded = true;
        saveToCache();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public void lambda$load$3(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.this.lambda$load$2(tLObject);
            }
        });
    }

    public void lambda$processUpdate$17(TLRPC.Message message, String str, int i) {
        if ((message.flags & 1073741824) != 0) {
            QuickReply findReply = findReply(message.quick_reply_shortcut_id);
            if (findReply == null) {
                QuickReply quickReply = new QuickReply();
                quickReply.id = message.quick_reply_shortcut_id;
                quickReply.topMessageId = message.id;
                MessageObject messageObject = new MessageObject(this.currentAccount, message, false, true);
                quickReply.topMessage = messageObject;
                messageObject.generateThumbs(false);
                if (str != null) {
                    quickReply.name = str;
                    deleteLocalReply(str);
                }
                quickReply.topMessage.applyQuickReply(str, i);
                quickReply.messagesCount = 1;
                this.replies.add(quickReply);
                updateOrder();
                addReply(quickReply);
            } else {
                int i2 = findReply.topMessageId;
                int i3 = message.id;
                if (i2 == i3) {
                    findReply.topMessageId = i3;
                    MessageObject messageObject2 = new MessageObject(this.currentAccount, message, false, true);
                    findReply.topMessage = messageObject2;
                    messageObject2.generateThumbs(false);
                    saveToCache();
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
                } else if ((message.flags & 32768) == 0) {
                    findReply.messagesCount++;
                    saveToCache();
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
                }
            }
        }
        if (str == null && i == 0) {
            ArrayList<TLRPC.Message> arrayList = new ArrayList<>();
            arrayList.add(message);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, true, true, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask(), 5, message.quick_reply_shortcut_id);
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            ArrayList<MessageObject> arrayList2 = new ArrayList<>();
            arrayList2.add(new MessageObject(this.currentAccount, message, true, true));
            MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(clientUserId, arrayList2, 5);
        }
    }

    public void lambda$processUpdate$18(TLRPC.Update update) {
        QuickReply quickReply;
        ArrayList<TLRPC.TL_quickReply> arrayList = ((TLRPC.TL_updateQuickReplies) update).quick_replies;
        ArrayList arrayList2 = new ArrayList(this.replies);
        this.replies.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.TL_quickReply tL_quickReply = arrayList.get(i);
            int i2 = 0;
            while (true) {
                if (i2 >= arrayList2.size()) {
                    quickReply = null;
                    break;
                } else {
                    if (((QuickReply) arrayList2.get(i2)).id == tL_quickReply.shortcut_id) {
                        quickReply = (QuickReply) arrayList2.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (quickReply == null) {
                quickReply = new QuickReply();
            }
            quickReply.id = tL_quickReply.shortcut_id;
            quickReply.name = tL_quickReply.shortcut;
            quickReply.messagesCount = tL_quickReply.count;
            quickReply.order = i;
            quickReply.topMessageId = tL_quickReply.top_message;
            MessageObject messageObject = quickReply.topMessage;
            if (messageObject != null && messageObject.getId() != tL_quickReply.top_message) {
                quickReply.topMessage = null;
            }
            this.replies.add(quickReply);
            deleteLocalReply(quickReply.name);
        }
        saveToCache();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public void lambda$processUpdate$19(TLRPC.Update update) {
        TLRPC.TL_quickReply tL_quickReply = ((TLRPC.TL_updateNewQuickReply) update).quick_reply;
        QuickReply findReply = findReply(tL_quickReply.shortcut_id);
        if (findReply != null) {
            findReply.name = tL_quickReply.shortcut;
            findReply.messagesCount = tL_quickReply.count;
            findReply.topMessageId = tL_quickReply.top_message;
            MessageObject messageObject = findReply.topMessage;
            if (messageObject != null && messageObject.getId() != tL_quickReply.top_message) {
                findReply.topMessage = null;
                updateTopMessage(findReply);
                return;
            }
        } else {
            QuickReply quickReply = new QuickReply();
            quickReply.id = tL_quickReply.shortcut_id;
            quickReply.name = tL_quickReply.shortcut;
            quickReply.messagesCount = tL_quickReply.count;
            quickReply.topMessageId = tL_quickReply.top_message;
            updateOrder();
            this.replies.add(quickReply);
            deleteLocalReply(quickReply.name);
        }
        saveToCache();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public static void lambda$processUpdate$20(MessagesStorage messagesStorage, int i) {
        try {
            SQLiteDatabase database = messagesStorage.getDatabase();
            database.executeFast("DELETE FROM business_replies WHERE topic_id = " + i).stepThis().dispose();
            database.executeFast("DELETE FROM quick_replies_messages WHERE topic_id = " + i).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$processUpdate$21(TLRPC.Update update) {
        QuickReply findReply = findReply(((TLRPC.TL_updateDeleteQuickReply) update).shortcut_id);
        if (findReply != null) {
            this.replies.remove(findReply);
            deleteLocalReply(findReply.name);
            final int i = findReply.id;
            final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    QuickRepliesController.lambda$processUpdate$20(MessagesStorage.this, i);
                }
            });
            saveToCache();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
        }
    }

    public void lambda$processUpdate$22(TLRPC.Update update) {
        TLRPC.TL_updateDeleteQuickReplyMessages tL_updateDeleteQuickReplyMessages = (TLRPC.TL_updateDeleteQuickReplyMessages) update;
        QuickReply findReply = findReply(tL_updateDeleteQuickReplyMessages.shortcut_id);
        if (findReply != null) {
            int size = findReply.messagesCount - tL_updateDeleteQuickReplyMessages.messages.size();
            findReply.messagesCount = size;
            if (size <= 0) {
                this.replies.remove(findReply);
            }
            if (tL_updateDeleteQuickReplyMessages.messages.contains(Integer.valueOf(findReply.getTopMessageId())) || findReply.topMessage == null) {
                findReply.topMessage = null;
                updateTopMessage(findReply);
            } else {
                saveToCache();
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
            }
        }
    }

    public static void lambda$renameReply$10() {
    }

    public static void lambda$renameReply$11(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.lambda$renameReply$10();
            }
        });
    }

    public static int lambda$reorder$7(QuickReply quickReply, QuickReply quickReply2) {
        return quickReply.order - quickReply2.order;
    }

    public static void lambda$reorder$8() {
    }

    public static void lambda$reorder$9(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.lambda$reorder$8();
            }
        });
    }

    public void lambda$saveToCache$4(MessagesStorage messagesStorage) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                SQLiteDatabase database = messagesStorage.getDatabase();
                database.executeFast("DELETE FROM business_replies").stepThis().dispose();
                sQLitePreparedStatement = database.executeFast("REPLACE INTO business_replies VALUES(?, ?, ?, ?)");
                for (int i = 0; i < this.replies.size(); i++) {
                    QuickReply quickReply = (QuickReply) this.replies.get(i);
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindInteger(1, quickReply.id);
                    sQLitePreparedStatement.bindString(2, quickReply.name);
                    sQLitePreparedStatement.bindInteger(3, quickReply.order);
                    sQLitePreparedStatement.bindInteger(4, quickReply.messagesCount);
                    sQLitePreparedStatement.step();
                }
                if (sQLitePreparedStatement == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$sendQuickReplyTo$24(TLObject tLObject, ArrayList arrayList, TLRPC.TL_messages_sendQuickReplyMessages tL_messages_sendQuickReplyMessages, TLRPC.TL_messages_getQuickReplyMessages tL_messages_getQuickReplyMessages, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_messages_messages)) {
            FileLog.e("received " + tLObject + " " + tL_error + " on getQuickReplyMessages when trying to send quick reply");
            return;
        }
        ArrayList<TLRPC.Message> arrayList2 = ((TLRPC.TL_messages_messages) tLObject).messages;
        arrayList.clear();
        Iterator<TLRPC.Message> it = arrayList2.iterator();
        while (it.hasNext()) {
            arrayList.add(Integer.valueOf(it.next().id));
        }
        tL_messages_sendQuickReplyMessages.id = arrayList;
        for (int i = 0; i < arrayList.size(); i++) {
            tL_messages_sendQuickReplyMessages.random_id.add(Long.valueOf(Utilities.random.nextLong()));
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getQuickReplyMessages, null);
    }

    public void lambda$sendQuickReplyTo$25(final ArrayList arrayList, final TLRPC.TL_messages_sendQuickReplyMessages tL_messages_sendQuickReplyMessages, final TLRPC.TL_messages_getQuickReplyMessages tL_messages_getQuickReplyMessages, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.this.lambda$sendQuickReplyTo$24(tLObject, arrayList, tL_messages_sendQuickReplyMessages, tL_messages_getQuickReplyMessages, tL_error);
            }
        });
    }

    public void lambda$sendQuickReplyTo$26(final ArrayList arrayList, QuickReply quickReply, final TLRPC.TL_messages_sendQuickReplyMessages tL_messages_sendQuickReplyMessages) {
        if (arrayList.isEmpty() || arrayList.size() < quickReply.getMessagesCount()) {
            final TLRPC.TL_messages_getQuickReplyMessages tL_messages_getQuickReplyMessages = new TLRPC.TL_messages_getQuickReplyMessages();
            tL_messages_getQuickReplyMessages.shortcut_id = quickReply.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getQuickReplyMessages, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    QuickRepliesController.this.lambda$sendQuickReplyTo$25(arrayList, tL_messages_sendQuickReplyMessages, tL_messages_getQuickReplyMessages, tLObject, tL_error);
                }
            });
        } else {
            tL_messages_sendQuickReplyMessages.id = arrayList;
            for (int i = 0; i < arrayList.size(); i++) {
                tL_messages_sendQuickReplyMessages.random_id.add(Long.valueOf(Utilities.random.nextLong()));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_sendQuickReplyMessages, null);
        }
    }

    public void lambda$sendQuickReplyTo$27(org.telegram.messenger.MessagesStorage r7, final org.telegram.ui.Business.QuickRepliesController.QuickReply r8, final org.telegram.tgnet.TLRPC.TL_messages_sendQuickReplyMessages r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Business.QuickRepliesController.lambda$sendQuickReplyTo$27(org.telegram.messenger.MessagesStorage, org.telegram.ui.Business.QuickRepliesController$QuickReply, org.telegram.tgnet.TLRPC$TL_messages_sendQuickReplyMessages):void");
    }

    public void lambda$updateTopMessage$15(ArrayList arrayList, ArrayList arrayList2, QuickReply quickReply, MessageObject messageObject) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        quickReply.topMessage = messageObject;
        if (messageObject != null) {
            messageObject.applyQuickReply(quickReply.name, quickReply.id);
        }
        saveToCache();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public void lambda$updateTopMessage$16(org.telegram.messenger.MessagesStorage r15, final org.telegram.ui.Business.QuickRepliesController.QuickReply r16, long r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Business.QuickRepliesController.lambda$updateTopMessage$16(org.telegram.messenger.MessagesStorage, org.telegram.ui.Business.QuickRepliesController$QuickReply, long):void");
    }

    private void load(boolean z) {
        load(z, null);
    }

    private void load(boolean z, final Runnable runnable) {
        TLRPC.Message message;
        if (this.loading || this.loaded) {
            return;
        }
        this.loading = true;
        if (z) {
            final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    QuickRepliesController.this.lambda$load$1(messagesStorage, clientUserId, runnable);
                }
            });
            return;
        }
        TLRPC.TL_messages_getQuickReplies tL_messages_getQuickReplies = new TLRPC.TL_messages_getQuickReplies();
        tL_messages_getQuickReplies.hash = 0L;
        for (int i = 0; i < this.replies.size(); i++) {
            QuickReply quickReply = (QuickReply) this.replies.get(i);
            long calcHash = MediaDataController.calcHash(tL_messages_getQuickReplies.hash, quickReply.id);
            tL_messages_getQuickReplies.hash = calcHash;
            String str = quickReply.name;
            long calcHash2 = MediaDataController.calcHash(calcHash, str == null ? 0L : QuickRepliesController$$ExternalSyntheticBackport1.m(r6, 0, Utilities.MD5(str).substring(0, 16).length(), 16));
            tL_messages_getQuickReplies.hash = calcHash2;
            long calcHash3 = MediaDataController.calcHash(calcHash2, quickReply.topMessage == null ? 0L : r6.getId());
            tL_messages_getQuickReplies.hash = calcHash3;
            MessageObject messageObject = quickReply.topMessage;
            tL_messages_getQuickReplies.hash = (messageObject == null || (message = messageObject.messageOwner) == null || (message.flags & 32768) == 0) ? MediaDataController.calcHash(calcHash3, 0L) : MediaDataController.calcHash(calcHash3, message.edit_date);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getQuickReplies, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                QuickRepliesController.this.lambda$load$3(tLObject, tL_error);
            }
        });
    }

    private void saveToCache() {
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.this.lambda$saveToCache$4(messagesStorage);
            }
        });
    }

    private void updateOrder() {
        for (int i = 0; i < this.replies.size(); i++) {
            ((QuickReply) this.replies.get(i)).order = i;
        }
    }

    private void updateTopMessage(final QuickReply quickReply) {
        if (quickReply == null) {
            return;
        }
        final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.this.lambda$updateTopMessage$16(messagesStorage, quickReply, clientUserId);
            }
        });
    }

    public boolean canAddNew() {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.replies.size(); i3++) {
            i = (i != 0 || "hello".equalsIgnoreCase(((QuickReply) this.replies.get(i3)).name)) ? 1 : 0;
            i2 = (i2 != 0 || "away".equalsIgnoreCase(((QuickReply) this.replies.get(i3)).name)) ? 1 : 0;
            if (i != 0 && i2 != 0) {
                break;
            }
        }
        return (this.replies.size() + (i ^ 1)) + (i2 ^ 1) < MessagesController.getInstance(this.currentAccount).quickRepliesLimit;
    }

    public void checkLocalMessages(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (messageObject.isSending() && findReply(messageObject.getQuickReplyId()) == null && messageObject.getQuickReplyName() != null && findReply(messageObject.getQuickReplyName()) == null) {
                QuickReply findLocalReply = findLocalReply(messageObject.getQuickReplyName());
                if (findLocalReply == null) {
                    findLocalReply = new QuickReply();
                    findLocalReply.local = true;
                    findLocalReply.name = messageObject.getQuickReplyName();
                    findLocalReply.id = -1;
                    findLocalReply.topMessage = messageObject;
                    findLocalReply.topMessageId = messageObject.getId();
                    this.localReplies.add(findLocalReply);
                }
                findLocalReply.localIds.add(Integer.valueOf(messageObject.getId()));
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        QuickRepliesController.this.lambda$checkLocalMessages$23();
                    }
                });
            }
        }
    }

    public void deleteLocalMessage(int i) {
        for (int i2 = 0; i2 < this.localReplies.size(); i2++) {
            QuickReply quickReply = (QuickReply) this.localReplies.get(i2);
            if (quickReply.localIds.contains(Integer.valueOf(i))) {
                quickReply.localIds.remove(Integer.valueOf(i));
                if (quickReply.getMessagesCount() <= 0) {
                    this.localReplies.remove(quickReply);
                }
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
                return;
            }
        }
    }

    public void deleteLocalMessages(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            deleteLocalMessage(((Integer) it.next()).intValue());
        }
    }

    public void deleteLocalReply(String str) {
        QuickReply findLocalReply = findLocalReply(str);
        if (findLocalReply != null) {
            this.localReplies.remove(findLocalReply);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
        }
    }

    public void deleteReplies(final ArrayList arrayList) {
        TLRPC.UserFull userFull;
        int i = 0;
        while (i < arrayList.size()) {
            if (findReply(((Integer) arrayList.get(i)).intValue()) == null) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
        if (arrayList.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            QuickReply findReply = findReply(((Integer) arrayList.get(i2)).intValue());
            this.replies.remove(findReply);
            deleteLocalReply(findReply.name);
            TLRPC.TL_messages_deleteQuickReplyShortcut tL_messages_deleteQuickReplyShortcut = new TLRPC.TL_messages_deleteQuickReplyShortcut();
            tL_messages_deleteQuickReplyShortcut.shortcut_id = findReply.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_deleteQuickReplyShortcut, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    QuickRepliesController.lambda$deleteReplies$13(tLObject, tL_error);
                }
            });
            if ("hello".equals(findReply.name)) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_updateBusinessGreetingMessage(), null);
                userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
                if (userFull != null) {
                    userFull.flags2 &= -5;
                    userFull.business_greeting_message = null;
                    MessagesStorage.getInstance(this.currentAccount).updateUserInfo(userFull, true);
                }
            } else {
                if ("away".equals(findReply.name)) {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_updateBusinessAwayMessage(), null);
                    userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
                    if (userFull != null) {
                        userFull.flags2 &= -9;
                        userFull.business_away_message = null;
                        MessagesStorage.getInstance(this.currentAccount).updateUserInfo(userFull, true);
                    }
                }
            }
        }
        saveToCache();
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.lambda$deleteReplies$14(MessagesStorage.this, arrayList);
            }
        });
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public QuickReply findLocalReply(String str) {
        Iterator it = this.localReplies.iterator();
        while (it.hasNext()) {
            QuickReply quickReply = (QuickReply) it.next();
            if (TextUtils.equals(str, quickReply.name)) {
                return quickReply;
            }
        }
        return null;
    }

    public QuickReply findReply(long j) {
        Iterator it = this.replies.iterator();
        while (it.hasNext()) {
            QuickReply quickReply = (QuickReply) it.next();
            if (quickReply.id == j) {
                return quickReply;
            }
        }
        return null;
    }

    public QuickReply findReply(String str) {
        Iterator it = this.replies.iterator();
        while (it.hasNext()) {
            QuickReply quickReply = (QuickReply) it.next();
            if (TextUtils.equals(str, quickReply.name)) {
                return quickReply;
            }
        }
        return null;
    }

    public ArrayList getFilteredReplies() {
        this.filtered.clear();
        for (int i = 0; i < this.replies.size(); i++) {
            if (!((QuickReply) this.replies.get(i)).isSpecial()) {
                this.filtered.add((QuickReply) this.replies.get(i));
            }
        }
        return this.filtered;
    }

    public boolean hasReplies() {
        return !this.replies.isEmpty();
    }

    public boolean isNameBusy(String str, int i) {
        QuickReply findReply = findReply(str);
        return (findReply == null || findReply.id == i) ? false : true;
    }

    public void load() {
        load(true, null);
    }

    public boolean processUpdate(final TLRPC.Update update, final String str, final int i) {
        if (update instanceof TLRPC.TL_updateQuickReplyMessage) {
            final TLRPC.Message message = ((TLRPC.TL_updateQuickReplyMessage) update).message;
            ensureLoaded(new Runnable() {
                @Override
                public final void run() {
                    QuickRepliesController.this.lambda$processUpdate$17(message, str, i);
                }
            });
            return true;
        }
        if (update instanceof TLRPC.TL_updateQuickReplies) {
            ensureLoaded(new Runnable() {
                @Override
                public final void run() {
                    QuickRepliesController.this.lambda$processUpdate$18(update);
                }
            });
            return true;
        }
        if (update instanceof TLRPC.TL_updateNewQuickReply) {
            ensureLoaded(new Runnable() {
                @Override
                public final void run() {
                    QuickRepliesController.this.lambda$processUpdate$19(update);
                }
            });
            return true;
        }
        if (update instanceof TLRPC.TL_updateDeleteQuickReply) {
            ensureLoaded(new Runnable() {
                @Override
                public final void run() {
                    QuickRepliesController.this.lambda$processUpdate$21(update);
                }
            });
            return true;
        }
        if (!(update instanceof TLRPC.TL_updateDeleteQuickReplyMessages)) {
            return false;
        }
        ensureLoaded(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.this.lambda$processUpdate$22(update);
            }
        });
        return true;
    }

    public void renameReply(int i, String str) {
        QuickReply findReply = findReply(i);
        if (findReply == null) {
            return;
        }
        findReply.name = str;
        TLRPC.TL_messages_editQuickReplyShortcut tL_messages_editQuickReplyShortcut = new TLRPC.TL_messages_editQuickReplyShortcut();
        tL_messages_editQuickReplyShortcut.shortcut_id = i;
        tL_messages_editQuickReplyShortcut.shortcut = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editQuickReplyShortcut, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                QuickRepliesController.lambda$renameReply$11(tLObject, tL_error);
            }
        });
        saveToCache();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
    }

    public void reorder() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.replies.size(); i++) {
            arrayList.add(Integer.valueOf(((QuickReply) this.replies.get(i)).id));
        }
        Collections.sort(this.replies, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$reorder$7;
                lambda$reorder$7 = QuickRepliesController.lambda$reorder$7((QuickRepliesController.QuickReply) obj, (QuickRepliesController.QuickReply) obj2);
                return lambda$reorder$7;
            }
        });
        for (int i2 = 0; i2 < this.replies.size(); i2++) {
            if (((QuickReply) this.replies.get(i2)).id != ((Integer) arrayList.get(i2)).intValue()) {
                TLRPC.TL_messages_reorderQuickReplies tL_messages_reorderQuickReplies = new TLRPC.TL_messages_reorderQuickReplies();
                for (int i3 = 0; i3 < this.replies.size(); i3++) {
                    tL_messages_reorderQuickReplies.order.add(Integer.valueOf(((QuickReply) this.replies.get(i3)).id));
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderQuickReplies, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        QuickRepliesController.lambda$reorder$9(tLObject, tL_error);
                    }
                });
                saveToCache();
                return;
            }
        }
    }

    public void sendQuickReplyTo(long j, final QuickReply quickReply) {
        if (quickReply == null) {
            return;
        }
        final TLRPC.TL_messages_sendQuickReplyMessages tL_messages_sendQuickReplyMessages = new TLRPC.TL_messages_sendQuickReplyMessages();
        TLRPC.InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        tL_messages_sendQuickReplyMessages.peer = inputPeer;
        if (inputPeer == null) {
            return;
        }
        tL_messages_sendQuickReplyMessages.shortcut_id = quickReply.id;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                QuickRepliesController.this.lambda$sendQuickReplyTo$27(messagesStorage, quickReply, tL_messages_sendQuickReplyMessages);
            }
        });
    }
}
