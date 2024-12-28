package org.telegram.ui.Business;

import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;

public class BusinessChatbotController {
    private static volatile BusinessChatbotController[] Instance = new BusinessChatbotController[4];
    private static final Object[] lockObjects = new Object[4];
    private ArrayList callbacks = new ArrayList();
    private final int currentAccount;
    private long lastTime;
    private boolean loaded;
    private boolean loading;
    private TL_account.connectedBots value;

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private BusinessChatbotController(int i) {
        this.currentAccount = i;
    }

    public static BusinessChatbotController getInstance(int i) {
        BusinessChatbotController businessChatbotController = Instance[i];
        if (businessChatbotController == null) {
            synchronized (lockObjects[i]) {
                try {
                    businessChatbotController = Instance[i];
                    if (businessChatbotController == null) {
                        BusinessChatbotController[] businessChatbotControllerArr = Instance;
                        BusinessChatbotController businessChatbotController2 = new BusinessChatbotController(i);
                        businessChatbotControllerArr[i] = businessChatbotController2;
                        businessChatbotController = businessChatbotController2;
                    }
                } finally {
                }
            }
        }
        return businessChatbotController;
    }

    public void lambda$load$0(TLObject tLObject) {
        this.loading = false;
        TL_account.connectedBots connectedbots = tLObject instanceof TL_account.connectedBots ? (TL_account.connectedBots) tLObject : null;
        this.value = connectedbots;
        if (connectedbots != null) {
            MessagesController.getInstance(this.currentAccount).putUsers(this.value.users, false);
        }
        this.lastTime = System.currentTimeMillis();
        this.loaded = true;
        for (int i = 0; i < this.callbacks.size(); i++) {
            if (this.callbacks.get(i) != null) {
                ((Utilities.Callback) this.callbacks.get(i)).run(this.value);
            }
        }
        this.callbacks.clear();
    }

    public void lambda$load$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BusinessChatbotController.this.lambda$load$0(tLObject);
            }
        });
    }

    public void invalidate(boolean z) {
        this.loaded = false;
        if (z) {
            load(null);
        }
    }

    public void load(Utilities.Callback callback) {
        boolean z;
        this.callbacks.add(callback);
        if (this.loading) {
            return;
        }
        if (System.currentTimeMillis() - this.lastTime > 60000 || !(z = this.loaded)) {
            this.loading = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account.getConnectedBots(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BusinessChatbotController.this.lambda$load$1(tLObject, tL_error);
                }
            });
        } else if (z) {
            for (int i = 0; i < this.callbacks.size(); i++) {
                if (this.callbacks.get(i) != null) {
                    ((Utilities.Callback) this.callbacks.get(i)).run(this.value);
                }
            }
            this.callbacks.clear();
        }
    }
}
