package org.telegram.ui.Business;

import android.text.TextUtils;
import java.util.ArrayList;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;

public class BusinessLinksController {
    private static volatile BusinessLinksController[] Instance = new BusinessLinksController[4];
    private static final Object[] lockObjects = new Object[4];
    public final int currentAccount;
    public final ArrayList links = new ArrayList();
    private boolean loading = false;
    private boolean loaded = false;

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private BusinessLinksController(int i) {
        this.currentAccount = i;
    }

    private void editLink(final TLRPC.TL_businessChatLink tL_businessChatLink, TLRPC.TL_inputBusinessChatLink tL_inputBusinessChatLink, final Runnable runnable) {
        TLRPC.TL_account_editBusinessChatLink tL_account_editBusinessChatLink = new TLRPC.TL_account_editBusinessChatLink();
        tL_account_editBusinessChatLink.slug = tL_businessChatLink.link;
        if (!tL_inputBusinessChatLink.entities.isEmpty()) {
            tL_inputBusinessChatLink.flags |= 1;
        }
        if (!TextUtils.isEmpty(tL_inputBusinessChatLink.title)) {
            tL_inputBusinessChatLink.flags |= 2;
        }
        tL_account_editBusinessChatLink.link = tL_inputBusinessChatLink;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_editBusinessChatLink, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BusinessLinksController.this.lambda$editLink$12(tL_businessChatLink, runnable, tLObject, tL_error);
            }
        });
    }

    public static BusinessLinksController getInstance(int i) {
        BusinessLinksController businessLinksController = Instance[i];
        if (businessLinksController == null) {
            synchronized (lockObjects[i]) {
                try {
                    businessLinksController = Instance[i];
                    if (businessLinksController == null) {
                        BusinessLinksController[] businessLinksControllerArr = Instance;
                        BusinessLinksController businessLinksController2 = new BusinessLinksController(i);
                        businessLinksControllerArr[i] = businessLinksController2;
                        businessLinksController = businessLinksController2;
                    }
                } finally {
                }
            }
        }
        return businessLinksController;
    }

    public void lambda$createEmptyLink$4(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_businessChatLink) {
            TLRPC.TL_businessChatLink tL_businessChatLink = (TLRPC.TL_businessChatLink) tLObject;
            this.links.add(tL_businessChatLink);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinksUpdated, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinkCreated, tL_businessChatLink);
            saveToCache();
        }
    }

    public void lambda$createEmptyLink$5(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BusinessLinksController.this.lambda$createEmptyLink$4(tLObject);
            }
        });
    }

    public void lambda$deleteLinkUndoable$6(int i, TLRPC.TL_businessChatLink tL_businessChatLink) {
        this.links.add(i, tL_businessChatLink);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinksUpdated, new Object[0]);
    }

    public void lambda$deleteLinkUndoable$7(TLObject tLObject, TLRPC.TL_businessChatLink tL_businessChatLink) {
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            FileLog.e(new RuntimeException("Unexpected response from server!"));
            return;
        }
        if (this.links.contains(tL_businessChatLink)) {
            this.links.remove(tL_businessChatLink);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinksUpdated, new Object[0]);
        }
        saveToCache();
    }

    public void lambda$deleteLinkUndoable$8(final TLRPC.TL_businessChatLink tL_businessChatLink, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BusinessLinksController.this.lambda$deleteLinkUndoable$7(tLObject, tL_businessChatLink);
            }
        });
    }

    public void lambda$deleteLinkUndoable$9(String str, final TLRPC.TL_businessChatLink tL_businessChatLink) {
        TLRPC.TL_account_deleteBusinessChatLink tL_account_deleteBusinessChatLink = new TLRPC.TL_account_deleteBusinessChatLink();
        tL_account_deleteBusinessChatLink.slug = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_deleteBusinessChatLink, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BusinessLinksController.this.lambda$deleteLinkUndoable$8(tL_businessChatLink, tLObject, tL_error);
            }
        });
    }

    public void lambda$editLink$11(TLObject tLObject, TLRPC.TL_businessChatLink tL_businessChatLink, Runnable runnable) {
        if (tLObject instanceof TLRPC.TL_businessChatLink) {
            TLRPC.TL_businessChatLink tL_businessChatLink2 = (TLRPC.TL_businessChatLink) tLObject;
            int indexOf = this.links.indexOf(tL_businessChatLink);
            if (indexOf != -1) {
                this.links.set(indexOf, tL_businessChatLink2);
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinksUpdated, new Object[0]);
                if (runnable != null) {
                    runnable.run();
                }
                saveToCache();
            }
        }
    }

    public void lambda$editLink$12(final TLRPC.TL_businessChatLink tL_businessChatLink, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BusinessLinksController.this.lambda$editLink$11(tLObject, tL_businessChatLink, runnable);
            }
        });
    }

    public void lambda$load$0(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, boolean z) {
        this.links.clear();
        this.links.addAll(arrayList);
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList3, true);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.quickRepliesUpdated, new Object[0]);
        this.loading = false;
        load(false, z);
    }

    public void lambda$load$1(org.telegram.messenger.MessagesStorage r13, final boolean r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Business.BusinessLinksController.lambda$load$1(org.telegram.messenger.MessagesStorage, boolean):void");
    }

    public void lambda$load$2(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_account_businessChatLinks) {
            TLRPC.TL_account_businessChatLinks tL_account_businessChatLinks = (TLRPC.TL_account_businessChatLinks) tLObject;
            this.links.clear();
            this.links.addAll(tL_account_businessChatLinks.links);
            MessagesController.getInstance(this.currentAccount).putUsers(tL_account_businessChatLinks.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_account_businessChatLinks.chats, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_account_businessChatLinks.users, tL_account_businessChatLinks.chats, true, true);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinksUpdated, new Object[0]);
            saveToCache();
        } else {
            FileLog.e(new RuntimeException("Unexpected response from server!"));
        }
        this.loading = false;
        this.loaded = true;
    }

    public void lambda$load$3(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BusinessLinksController.this.lambda$load$2(tLObject);
            }
        });
    }

    public static void lambda$saveToCache$10(MessagesStorage messagesStorage, ArrayList arrayList) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                SQLiteDatabase database = messagesStorage.getDatabase();
                database.executeFast("DELETE FROM business_links").stepThis().dispose();
                sQLitePreparedStatement = database.executeFast("REPLACE INTO business_links VALUES(?, ?)");
                for (int i = 0; i < arrayList.size(); i++) {
                    TLRPC.TL_businessChatLink tL_businessChatLink = (TLRPC.TL_businessChatLink) arrayList.get(i);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_businessChatLink.getObjectSize());
                    tL_businessChatLink.serializeToStream(nativeByteBuffer);
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                    sQLitePreparedStatement.bindInteger(2, i);
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

    private void load(boolean z, final boolean z2) {
        if (this.loading) {
            return;
        }
        if (!this.loaded || (z2 && !z)) {
            this.loading = true;
            if (z) {
                final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
                messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        BusinessLinksController.this.lambda$load$1(messagesStorage, z2);
                    }
                });
            } else {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getBusinessChatLinks(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        BusinessLinksController.this.lambda$load$3(tLObject, tL_error);
                    }
                });
            }
        }
    }

    private void saveToCache() {
        final ArrayList arrayList = new ArrayList(this.links);
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                BusinessLinksController.lambda$saveToCache$10(MessagesStorage.this, arrayList);
            }
        });
    }

    public static String stripHttps(String str) {
        return str.startsWith("https://") ? str.substring(8) : str;
    }

    public boolean canAddNew() {
        return this.links.size() < MessagesController.getInstance(this.currentAccount).businessChatLinksLimit;
    }

    public void createEmptyLink() {
        TLRPC.TL_account_createBusinessChatLink tL_account_createBusinessChatLink = new TLRPC.TL_account_createBusinessChatLink();
        TLRPC.TL_inputBusinessChatLink tL_inputBusinessChatLink = new TLRPC.TL_inputBusinessChatLink();
        tL_account_createBusinessChatLink.link = tL_inputBusinessChatLink;
        tL_inputBusinessChatLink.message = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_createBusinessChatLink, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BusinessLinksController.this.lambda$createEmptyLink$5(tLObject, tL_error);
            }
        });
    }

    public void deleteLinkUndoable(BaseFragment baseFragment, final String str) {
        final TLRPC.TL_businessChatLink findLink = findLink(str);
        if (findLink != null) {
            final int indexOf = this.links.indexOf(findLink);
            this.links.remove(findLink);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.businessLinksUpdated, new Object[0]);
            BulletinFactory.of(baseFragment).createUndoBulletin(LocaleController.getString(R.string.BusinessLinkDeleted), true, new Runnable() {
                @Override
                public final void run() {
                    BusinessLinksController.this.lambda$deleteLinkUndoable$6(indexOf, findLink);
                }
            }, new Runnable() {
                @Override
                public final void run() {
                    BusinessLinksController.this.lambda$deleteLinkUndoable$9(str, findLink);
                }
            }).show();
        }
    }

    public void editLinkMessage(String str, String str2, ArrayList arrayList, Runnable runnable) {
        TLRPC.TL_businessChatLink findLink = findLink(str);
        if (findLink == null) {
            return;
        }
        TLRPC.TL_inputBusinessChatLink tL_inputBusinessChatLink = new TLRPC.TL_inputBusinessChatLink();
        tL_inputBusinessChatLink.message = str2;
        tL_inputBusinessChatLink.entities = arrayList;
        tL_inputBusinessChatLink.title = findLink.title;
        editLink(findLink, tL_inputBusinessChatLink, runnable);
    }

    public void editLinkTitle(String str, String str2) {
        TLRPC.TL_businessChatLink findLink = findLink(str);
        if (findLink == null) {
            return;
        }
        TLRPC.TL_inputBusinessChatLink tL_inputBusinessChatLink = new TLRPC.TL_inputBusinessChatLink();
        tL_inputBusinessChatLink.message = findLink.message;
        tL_inputBusinessChatLink.entities = findLink.entities;
        tL_inputBusinessChatLink.title = str2;
        editLink(findLink, tL_inputBusinessChatLink, null);
    }

    public TLRPC.TL_businessChatLink findLink(String str) {
        for (int i = 0; i < this.links.size(); i++) {
            TLRPC.TL_businessChatLink tL_businessChatLink = (TLRPC.TL_businessChatLink) this.links.get(i);
            if (!TextUtils.equals(tL_businessChatLink.link, str)) {
                if (!TextUtils.equals(tL_businessChatLink.link, "https://" + str)) {
                    if (!TextUtils.equals(tL_businessChatLink.link, "https://t.me/m/" + str)) {
                        if (!TextUtils.equals(tL_businessChatLink.link, "tg://message?slug=" + str)) {
                        }
                    }
                }
            }
            return tL_businessChatLink;
        }
        return null;
    }

    public void load(boolean z) {
        if (!this.loaded) {
            load(true, z);
        } else if (z) {
            load(false, true);
        }
    }
}
