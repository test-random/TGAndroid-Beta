package org.telegram.messenger;

import android.text.TextUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.LaunchActivity;

public class UserNameResolver {
    private static final long CACHE_TIME = 3600000;
    private final int currentAccount;
    android.util.LruCache<String, CachedPeer> resolvedCache = new android.util.LruCache<>(100);
    HashMap<String, ArrayList<Consumer>> resolvingConsumers = new HashMap<>();

    public class CachedPeer {
        final long peerId;
        final long time = System.currentTimeMillis();

        public CachedPeer(long j) {
            this.peerId = j;
        }
    }

    public UserNameResolver(int i) {
        this.currentAccount = i;
    }

    public void lambda$resolve$0(String str, TLRPC.TL_error tL_error, TLObject tLObject) {
        BaseFragment lastFragment;
        ArrayList<Consumer> remove = this.resolvingConsumers.remove(str);
        if (remove == null) {
            return;
        }
        int i = 0;
        if (tL_error == null) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
            long peerId = MessageObject.getPeerId(tL_contacts_resolvedPeer.peer);
            this.resolvedCache.put(str, new CachedPeer(peerId));
            while (i < remove.size()) {
                remove.get(i).accept(Long.valueOf(peerId));
                i++;
            }
            return;
        }
        String str2 = tL_error.text;
        if (str2 != null && "STARREF_EXPIRED".equals(str2)) {
            while (i < remove.size()) {
                remove.get(i).accept(Long.MAX_VALUE);
                i++;
            }
            return;
        }
        while (i < remove.size()) {
            remove.get(i).accept(null);
            i++;
        }
        String str3 = tL_error.text;
        if (str3 == null || !str3.contains("FLOOD_WAIT") || (lastFragment = LaunchActivity.getLastFragment()) == null) {
            return;
        }
        BulletinFactory.of(lastFragment).createErrorBulletin(LocaleController.getString(R.string.FloodWait)).show();
    }

    public void lambda$resolve$1(final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                UserNameResolver.this.lambda$resolve$0(str, tL_error, tLObject);
            }
        }, 2L);
    }

    public int resolve(String str, Consumer consumer) {
        return resolve(str, null, consumer);
    }

    public int resolve(final String str, String str2, Consumer consumer) {
        TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername;
        CachedPeer cachedPeer;
        if (TextUtils.isEmpty(str2) && (cachedPeer = this.resolvedCache.get(str)) != null) {
            if (System.currentTimeMillis() - cachedPeer.time < 3600000) {
                consumer.accept(Long.valueOf(cachedPeer.peerId));
                FileLog.d("resolve username from cache " + str + " " + cachedPeer.peerId);
                return -1;
            }
            this.resolvedCache.remove(str);
        }
        ArrayList<Consumer> arrayList = this.resolvingConsumers.get(str);
        if (arrayList != null) {
            arrayList.add(consumer);
            return -1;
        }
        ArrayList<Consumer> arrayList2 = new ArrayList<>();
        arrayList2.add(consumer);
        this.resolvingConsumers.put(str, arrayList2);
        if (AndroidUtilities.isNumeric(str)) {
            TLRPC.TL_contacts_resolvePhone tL_contacts_resolvePhone = new TLRPC.TL_contacts_resolvePhone();
            tL_contacts_resolvePhone.phone = str;
            tL_contacts_resolveUsername = tL_contacts_resolvePhone;
        } else {
            TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername2 = new TLRPC.TL_contacts_resolveUsername();
            tL_contacts_resolveUsername2.username = str;
            if (!TextUtils.isEmpty(str2)) {
                tL_contacts_resolveUsername2.flags |= 1;
                tL_contacts_resolveUsername2.referer = str2;
            }
            tL_contacts_resolveUsername = tL_contacts_resolveUsername2;
        }
        return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                UserNameResolver.this.lambda$resolve$1(str, tLObject, tL_error);
            }
        });
    }

    public void update(TLRPC.Chat chat, TLRPC.Chat chat2) {
        String str;
        if (chat == null || chat2 == null || (str = chat.username) == null || TextUtils.equals(str, chat2.username)) {
            return;
        }
        this.resolvedCache.remove(chat.username);
        String str2 = chat2.username;
        if (str2 != null) {
            this.resolvedCache.put(str2, new CachedPeer(-chat2.id));
        }
    }

    public void update(TLRPC.User user, TLRPC.User user2) {
        String str;
        if (user == null || user2 == null || (str = user.username) == null || TextUtils.equals(str, user2.username)) {
            return;
        }
        this.resolvedCache.remove(user.username);
        String str2 = user2.username;
        if (str2 != null) {
            this.resolvedCache.put(str2, new CachedPeer(user2.id));
        }
    }
}
