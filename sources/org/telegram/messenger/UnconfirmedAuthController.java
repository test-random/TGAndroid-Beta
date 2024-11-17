package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.UnconfirmedAuthController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class UnconfirmedAuthController {
    private final int currentAccount;
    private boolean fetchedCache;
    private boolean fetchingCache;
    private boolean saveAfterFetch;
    private boolean savingCache;
    public final ArrayList<UnconfirmedAuth> auths = new ArrayList<>();
    private final Runnable checkExpiration = new Runnable() {
        @Override
        public final void run() {
            UnconfirmedAuthController.this.lambda$new$2();
        }
    };
    private boolean debug = false;

    public class UnconfirmedAuth extends TLObject {
        public int date;
        public String device;
        public long hash;
        public String location;

        public UnconfirmedAuth(AbstractSerializedData abstractSerializedData) {
            int readInt32 = abstractSerializedData.readInt32(true);
            if (readInt32 != 2058772876) {
                throw new RuntimeException("UnconfirmedAuth can't parse magic " + Integer.toHexString(readInt32));
            }
            this.hash = abstractSerializedData.readInt64(true);
            this.date = abstractSerializedData.readInt32(true);
            this.device = abstractSerializedData.readString(true);
            this.location = abstractSerializedData.readString(true);
        }

        public UnconfirmedAuth(TLRPC.TL_updateNewAuthorization tL_updateNewAuthorization) {
            this.hash = tL_updateNewAuthorization.hash;
            this.date = tL_updateNewAuthorization.date;
            this.device = tL_updateNewAuthorization.device;
            this.location = tL_updateNewAuthorization.location;
        }

        public void lambda$confirm$0(Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (callback != null) {
                callback.run(Boolean.valueOf(((tLObject instanceof TLRPC.TL_boolTrue) && tL_error == null) || UnconfirmedAuthController.this.debug));
                UnconfirmedAuthController.this.debug = false;
            }
        }

        public void lambda$confirm$1(final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    UnconfirmedAuthController.UnconfirmedAuth.this.lambda$confirm$0(callback, tLObject, tL_error);
                }
            });
        }

        public void lambda$deny$2(Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (callback != null) {
                callback.run(Boolean.valueOf(((tLObject instanceof TLRPC.TL_boolTrue) && tL_error == null) || UnconfirmedAuthController.this.debug));
                UnconfirmedAuthController.this.debug = false;
            }
        }

        public void lambda$deny$3(final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    UnconfirmedAuthController.UnconfirmedAuth.this.lambda$deny$2(callback, tLObject, tL_error);
                }
            });
        }

        public void confirm(final Utilities.Callback<Boolean> callback) {
            TLRPC.TL_account_changeAuthorizationSettings tL_account_changeAuthorizationSettings = new TLRPC.TL_account_changeAuthorizationSettings();
            tL_account_changeAuthorizationSettings.hash = this.hash;
            tL_account_changeAuthorizationSettings.confirmed = true;
            ConnectionsManager.getInstance(UnconfirmedAuthController.this.currentAccount).sendRequest(tL_account_changeAuthorizationSettings, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    UnconfirmedAuthController.UnconfirmedAuth.this.lambda$confirm$1(callback, tLObject, tL_error);
                }
            });
        }

        public void deny(final Utilities.Callback<Boolean> callback) {
            TLRPC.TL_account_resetAuthorization tL_account_resetAuthorization = new TLRPC.TL_account_resetAuthorization();
            tL_account_resetAuthorization.hash = this.hash;
            ConnectionsManager.getInstance(UnconfirmedAuthController.this.currentAccount).sendRequest(tL_account_resetAuthorization, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    UnconfirmedAuthController.UnconfirmedAuth.this.lambda$deny$3(callback, tLObject, tL_error);
                }
            });
        }

        public boolean expired() {
            return expiresAfter() <= 0;
        }

        public long expiresAfter() {
            return (ConnectionsManager.getInstance(UnconfirmedAuthController.this.currentAccount).getCurrentTime() + MessagesController.getInstance(UnconfirmedAuthController.this.currentAccount).authorizationAutoconfirmPeriod) - this.date;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2058772876);
            abstractSerializedData.writeInt64(this.hash);
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeString(this.device);
            abstractSerializedData.writeString(this.location);
        }
    }

    public UnconfirmedAuthController(int i) {
        this.currentAccount = i;
        readCache();
    }

    public void lambda$new$2() {
        int i = 0;
        while (i < this.auths.size()) {
            if (this.auths.get(i).expired()) {
                this.auths.remove(i);
                i--;
            }
            i++;
        }
        saveCache();
    }

    public void lambda$readCache$0(HashSet hashSet, ArrayList arrayList) {
        boolean isEmpty = this.auths.isEmpty();
        int i = 0;
        while (i < this.auths.size()) {
            UnconfirmedAuth unconfirmedAuth = this.auths.get(i);
            if (unconfirmedAuth == null || unconfirmedAuth.expired() || hashSet.contains(Long.valueOf(unconfirmedAuth.hash))) {
                this.auths.remove(i);
                i--;
            }
            i++;
        }
        this.auths.addAll(arrayList);
        boolean isEmpty2 = this.auths.isEmpty();
        this.fetchedCache = true;
        this.fetchingCache = false;
        if (isEmpty != isEmpty2) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.unconfirmedAuthUpdate, new Object[0]);
        }
        scheduleAuthExpireCheck();
        if (this.saveAfterFetch) {
            this.saveAfterFetch = false;
            saveCache();
        }
    }

    public void lambda$readCache$1() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UnconfirmedAuthController.lambda$readCache$1():void");
    }

    public void lambda$saveCache$3() {
        this.savingCache = false;
    }

    public void lambda$saveCache$4() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UnconfirmedAuthController.lambda$saveCache$4():void");
    }

    public static void lambda$updateList$5(boolean[] zArr, int i, Runnable runnable, Boolean bool) {
        zArr[i] = bool.booleanValue();
        runnable.run();
    }

    public static void lambda$updateList$6(final boolean[] zArr, final int i, boolean z, UnconfirmedAuth unconfirmedAuth, final Runnable runnable) {
        Utilities.Callback<Boolean> callback = new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                UnconfirmedAuthController.lambda$updateList$5(zArr, i, runnable, (Boolean) obj);
            }
        };
        if (z) {
            unconfirmedAuth.confirm(callback);
        } else {
            unconfirmedAuth.deny(callback);
        }
    }

    public void lambda$updateList$7(boolean[] zArr, ArrayList arrayList, boolean z, Utilities.Callback callback) {
        HashSet hashSet = new HashSet();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < zArr.length; i++) {
            if (zArr[i]) {
                UnconfirmedAuth unconfirmedAuth = (UnconfirmedAuth) arrayList.get(i);
                arrayList2.add(unconfirmedAuth);
                hashSet.add(Long.valueOf(unconfirmedAuth.hash));
            }
        }
        if (!z) {
            int i2 = 0;
            while (i2 < this.auths.size()) {
                if (hashSet.contains(Long.valueOf(this.auths.get(i2).hash))) {
                    this.auths.remove(i2);
                    i2--;
                }
                i2++;
            }
            if (!hashSet.isEmpty()) {
                saveCache();
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.unconfirmedAuthUpdate, new Object[0]);
                scheduleAuthExpireCheck();
            }
        }
        callback.run(arrayList2);
    }

    private void scheduleAuthExpireCheck() {
        AndroidUtilities.cancelRunOnUIThread(this.checkExpiration);
        if (this.auths.isEmpty()) {
            return;
        }
        Iterator<UnconfirmedAuth> it = this.auths.iterator();
        long j = Long.MAX_VALUE;
        while (it.hasNext()) {
            j = Math.min(j, it.next().expiresAfter());
        }
        if (j == Long.MAX_VALUE) {
            return;
        }
        AndroidUtilities.runOnUIThread(this.checkExpiration, Math.max(0L, j * 1000));
    }

    private void updateList(final boolean z, ArrayList<UnconfirmedAuth> arrayList, final Utilities.Callback<ArrayList<UnconfirmedAuth>> callback) {
        final ArrayList arrayList2 = new ArrayList(arrayList);
        final boolean[] zArr = new boolean[arrayList2.size()];
        Utilities.Callback[] callbackArr = new Utilities.Callback[arrayList2.size()];
        for (final int i = 0; i < arrayList2.size(); i++) {
            final UnconfirmedAuth unconfirmedAuth = (UnconfirmedAuth) arrayList2.get(i);
            callbackArr[i] = new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    UnconfirmedAuthController.lambda$updateList$6(zArr, i, z, unconfirmedAuth, (Runnable) obj);
                }
            };
        }
        Utilities.raceCallbacks(new Runnable() {
            @Override
            public final void run() {
                UnconfirmedAuthController.this.lambda$updateList$7(zArr, arrayList2, z, callback);
            }
        }, callbackArr);
        if (z) {
            HashSet hashSet = new HashSet();
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                hashSet.add(Long.valueOf(((UnconfirmedAuth) arrayList2.get(i2)).hash));
            }
            int i3 = 0;
            while (i3 < this.auths.size()) {
                if (hashSet.contains(Long.valueOf(this.auths.get(i3).hash))) {
                    this.auths.remove(i3);
                    i3--;
                }
                i3++;
            }
            if (hashSet.isEmpty()) {
                return;
            }
            saveCache();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.unconfirmedAuthUpdate, new Object[0]);
            scheduleAuthExpireCheck();
        }
    }

    public void cleanup() {
        this.auths.clear();
        saveCache();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.unconfirmedAuthUpdate, new Object[0]);
        scheduleAuthExpireCheck();
    }

    public void confirm(ArrayList<UnconfirmedAuth> arrayList, Utilities.Callback<ArrayList<UnconfirmedAuth>> callback) {
        updateList(true, arrayList, callback);
    }

    public void deny(ArrayList<UnconfirmedAuth> arrayList, Utilities.Callback<ArrayList<UnconfirmedAuth>> callback) {
        updateList(false, arrayList, callback);
    }

    public void processUpdate(TLRPC.TL_updateNewAuthorization tL_updateNewAuthorization) {
        int i = 0;
        while (i < this.auths.size()) {
            UnconfirmedAuth unconfirmedAuth = this.auths.get(i);
            if (unconfirmedAuth != null && unconfirmedAuth.hash == tL_updateNewAuthorization.hash) {
                this.auths.remove(i);
                i--;
            }
            i++;
        }
        if (tL_updateNewAuthorization.unconfirmed) {
            this.auths.add(new UnconfirmedAuth(tL_updateNewAuthorization));
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.unconfirmedAuthUpdate, new Object[0]);
        scheduleAuthExpireCheck();
        saveCache();
    }

    public void putDebug() {
        this.debug = true;
        TLRPC.TL_updateNewAuthorization tL_updateNewAuthorization = new TLRPC.TL_updateNewAuthorization();
        tL_updateNewAuthorization.unconfirmed = true;
        tL_updateNewAuthorization.device = "device";
        tL_updateNewAuthorization.location = "location";
        tL_updateNewAuthorization.hash = 123L;
        processUpdate(tL_updateNewAuthorization);
    }

    public void readCache() {
        if (this.fetchedCache || this.fetchingCache) {
            return;
        }
        this.fetchingCache = true;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                UnconfirmedAuthController.this.lambda$readCache$1();
            }
        });
    }

    public void saveCache() {
        if (this.savingCache) {
            return;
        }
        if (this.fetchingCache) {
            this.saveAfterFetch = true;
        } else {
            this.savingCache = true;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    UnconfirmedAuthController.this.lambda$saveCache$4();
                }
            });
        }
    }
}
