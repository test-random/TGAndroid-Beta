package org.telegram.ui.Stars;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChannelMonetizationLayout;
import org.telegram.ui.Stars.BotStarsController;

public class BotStarsController {
    private static volatile BotStarsController[] Instance = new BotStarsController[4];
    private static final Object[] lockObjects = new Object[4];
    public ArrayList adminedBots;
    public ArrayList adminedChannels;
    public final int currentAccount;
    private boolean loadingAdminedBots;
    private boolean loadingAdminedChannels;
    private final HashMap lastLoadedBotStarsStats = new HashMap();
    private final HashMap botStarsStats = new HashMap();
    private final HashMap lastLoadedTonStats = new HashMap();
    private final HashMap tonStats = new HashMap();
    private final HashMap transactions = new HashMap();
    private final HashMap connectedBots = new HashMap();
    private final HashMap suggestedBots = new HashMap();

    public static class ChannelConnectedBots {
        public int count;
        public final int currentAccount;
        public final long dialogId;
        public boolean endReached;
        public long lastRequestTime;
        private int reqId;
        public final ArrayList bots = new ArrayList();
        private boolean loading = false;
        private boolean error = false;

        public ChannelConnectedBots(int i, long j) {
            this.currentAccount = i;
            this.dialogId = j;
            check();
        }

        public void lambda$load$0(TLObject tLObject) {
            this.reqId = 0;
            if (tLObject instanceof TL_payments.connectedStarRefBots) {
                TL_payments.connectedStarRefBots connectedstarrefbots = (TL_payments.connectedStarRefBots) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(connectedstarrefbots.users, false);
                if (this.count <= 0) {
                    this.bots.clear();
                }
                this.count = connectedstarrefbots.count;
                this.bots.addAll(connectedstarrefbots.connected_bots);
                this.endReached = connectedstarrefbots.connected_bots.isEmpty() || this.bots.size() >= this.count;
            } else {
                this.error = true;
                this.endReached = true;
            }
            this.loading = false;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelConnectedBotsUpdate, Long.valueOf(this.dialogId));
        }

        public void lambda$load$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotStarsController.ChannelConnectedBots.this.lambda$load$0(tLObject);
                }
            });
        }

        public void apply(TL_payments.connectedStarRefBots connectedstarrefbots) {
            MessagesController.getInstance(this.currentAccount).putUsers(connectedstarrefbots.users, false);
            clear();
            this.bots.clear();
            cancel();
            this.count = connectedstarrefbots.count;
            this.bots.addAll(connectedstarrefbots.connected_bots);
            this.endReached = connectedstarrefbots.connected_bots.isEmpty() || this.bots.size() >= this.count;
            this.error = false;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelConnectedBotsUpdate, Long.valueOf(this.dialogId));
            load();
        }

        public void applyEdit(TL_payments.connectedStarRefBots connectedstarrefbots) {
            MessagesController.getInstance(this.currentAccount).putUsers(connectedstarrefbots.users, false);
            for (int i = 0; i < connectedstarrefbots.connected_bots.size(); i++) {
                TL_payments.connectedBotStarRef connectedbotstarref = connectedstarrefbots.connected_bots.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= this.bots.size()) {
                        break;
                    }
                    if (((TL_payments.connectedBotStarRef) this.bots.get(i2)).bot_id != connectedbotstarref.bot_id) {
                        i2++;
                    } else if (connectedbotstarref.revoked) {
                        this.bots.remove(i2);
                        this.count = Math.max(this.count - 1, 0);
                    } else {
                        this.bots.set(i2, connectedbotstarref);
                    }
                }
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelConnectedBotsUpdate, Long.valueOf(this.dialogId));
            load();
        }

        public void cancel() {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            this.loading = false;
        }

        public void check() {
            if (System.currentTimeMillis() - this.lastRequestTime > 900000) {
                clear();
                cancel();
                load();
            }
        }

        public void clear() {
            this.count = 0;
            this.error = false;
            this.endReached = false;
        }

        public boolean isLoading() {
            return this.loading;
        }

        public void load() {
            if (this.loading || this.error || this.endReached) {
                return;
            }
            this.lastRequestTime = System.currentTimeMillis();
            this.loading = true;
            TL_payments.getConnectedStarRefBots getconnectedstarrefbots = new TL_payments.getConnectedStarRefBots();
            getconnectedstarrefbots.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            getconnectedstarrefbots.limit = 20;
            if (!this.bots.isEmpty()) {
                ArrayList arrayList = this.bots;
                TL_payments.connectedBotStarRef connectedbotstarref = (TL_payments.connectedBotStarRef) arrayList.get(arrayList.size() - 1);
                getconnectedstarrefbots.flags |= 4;
                getconnectedstarrefbots.offset_date = connectedbotstarref.date;
                getconnectedstarrefbots.offset_link = connectedbotstarref.url;
            }
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(getconnectedstarrefbots, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotStarsController.ChannelConnectedBots.this.lambda$load$1(tLObject, tL_error);
                }
            });
        }
    }

    public static class ChannelSuggestedBots {
        public int count;
        public final int currentAccount;
        public final long dialogId;
        public boolean endReached;
        public long lastRequestTime;
        private int reqId;
        public final ArrayList bots = new ArrayList();
        private Sort sorting = Sort.BY_PROFITABILITY;
        private boolean loading = false;
        private boolean error = false;
        private String lastOffset = null;

        public enum Sort {
            BY_PROFITABILITY,
            BY_REVENUE,
            BY_DATE
        }

        public ChannelSuggestedBots(int i, long j) {
            this.currentAccount = i;
            this.dialogId = j;
            check();
        }

        public void lambda$load$0(TLObject tLObject) {
            if (tLObject instanceof TL_payments.suggestedStarRefBots) {
                TL_payments.suggestedStarRefBots suggestedstarrefbots = (TL_payments.suggestedStarRefBots) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(suggestedstarrefbots.users, false);
                if (this.count <= 0) {
                    this.bots.clear();
                }
                this.count = suggestedstarrefbots.count;
                this.bots.addAll(suggestedstarrefbots.suggested_bots);
                this.lastOffset = suggestedstarrefbots.next_offset;
                this.endReached = suggestedstarrefbots.suggested_bots.isEmpty() || this.bots.size() >= this.count;
            } else {
                this.error = true;
                this.endReached = true;
            }
            this.loading = false;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelSuggestedBotsUpdate, Long.valueOf(this.dialogId));
        }

        public void lambda$load$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotStarsController.ChannelSuggestedBots.this.lambda$load$0(tLObject);
                }
            });
        }

        public void cancel() {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            this.loading = false;
        }

        public void check() {
            if (System.currentTimeMillis() - this.lastRequestTime > 900000) {
                clear();
                cancel();
                load();
            }
        }

        public void clear() {
            this.count = 0;
            this.endReached = false;
            this.error = false;
            this.lastRequestTime = 0L;
            this.lastOffset = null;
        }

        public Sort getSort() {
            return this.sorting;
        }

        public boolean isLoading() {
            return this.loading;
        }

        public void load() {
            if (this.loading || this.error || this.endReached) {
                return;
            }
            this.lastRequestTime = System.currentTimeMillis();
            this.loading = true;
            TL_payments.getSuggestedStarRefBots getsuggestedstarrefbots = new TL_payments.getSuggestedStarRefBots();
            getsuggestedstarrefbots.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            getsuggestedstarrefbots.limit = 20;
            Sort sort = this.sorting;
            getsuggestedstarrefbots.order_by_date = sort == Sort.BY_DATE;
            getsuggestedstarrefbots.order_by_revenue = sort == Sort.BY_REVENUE;
            getsuggestedstarrefbots.offset = !TextUtils.isEmpty(this.lastOffset) ? this.lastOffset : "";
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getsuggestedstarrefbots, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotStarsController.ChannelSuggestedBots.this.lambda$load$1(tLObject, tL_error);
                }
            });
        }

        public void reload() {
            clear();
            cancel();
            load();
        }

        public void remove(long j) {
            for (int i = 0; i < this.bots.size(); i++) {
                if (((TL_payments.starRefProgram) this.bots.get(i)).bot_id == j) {
                    this.bots.remove(i);
                    this.count--;
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelSuggestedBotsUpdate, Long.valueOf(this.dialogId));
                    return;
                }
            }
        }

        public void setSort(Sort sort) {
            if (this.sorting != sort) {
                this.sorting = sort;
                reload();
            }
        }
    }

    public class TransactionsState {
        private final boolean[] endReached;
        private final boolean[] loading;
        private final String[] offset;
        public final ArrayList[] transactions;
        public final boolean[] transactionsExist;

        private TransactionsState() {
            this.transactions = new ArrayList[]{new ArrayList(), new ArrayList(), new ArrayList()};
            this.transactionsExist = new boolean[3];
            this.offset = new String[3];
            this.loading = new boolean[3];
            this.endReached = new boolean[3];
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private BotStarsController(int i) {
        this.currentAccount = i;
    }

    public static BotStarsController getInstance(int i) {
        BotStarsController botStarsController = Instance[i];
        if (botStarsController == null) {
            synchronized (lockObjects[i]) {
                try {
                    botStarsController = Instance[i];
                    if (botStarsController == null) {
                        BotStarsController[] botStarsControllerArr = Instance;
                        BotStarsController botStarsController2 = new BotStarsController(i);
                        botStarsControllerArr[i] = botStarsController2;
                        botStarsController = botStarsController2;
                    }
                } finally {
                }
            }
        }
        return botStarsController;
    }

    private TransactionsState getTransactionsState(long j) {
        TransactionsState transactionsState = (TransactionsState) this.transactions.get(Long.valueOf(j));
        if (transactionsState != null) {
            return transactionsState;
        }
        HashMap hashMap = this.transactions;
        Long valueOf = Long.valueOf(j);
        TransactionsState transactionsState2 = new TransactionsState();
        hashMap.put(valueOf, transactionsState2);
        return transactionsState2;
    }

    public void lambda$getConnectedBot$10(AlertDialog alertDialog, TLObject tLObject, long j, Utilities.Callback callback) {
        TL_payments.connectedBotStarRef connectedbotstarref;
        alertDialog.dismiss();
        if (tLObject instanceof TL_payments.connectedStarRefBots) {
            TL_payments.connectedStarRefBots connectedstarrefbots = (TL_payments.connectedStarRefBots) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(connectedstarrefbots.users, false);
            for (int i = 0; i < connectedstarrefbots.connected_bots.size(); i++) {
                if (connectedstarrefbots.connected_bots.get(i).bot_id == j && !connectedstarrefbots.connected_bots.get(i).revoked) {
                    connectedbotstarref = connectedstarrefbots.connected_bots.get(i);
                    break;
                }
            }
        }
        connectedbotstarref = null;
        callback.run(connectedbotstarref);
    }

    public void lambda$getConnectedBot$11(final AlertDialog alertDialog, final long j, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsController.this.lambda$getConnectedBot$10(alertDialog, tLObject, j, callback);
            }
        });
    }

    public void lambda$getConnectedBot$12(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void lambda$getStarsRevenueStats$0(TLObject tLObject, long j) {
        if (tLObject instanceof TLRPC.TL_payments_starsRevenueStats) {
            this.botStarsStats.put(Long.valueOf(j), (TLRPC.TL_payments_starsRevenueStats) tLObject);
        } else {
            this.botStarsStats.put(Long.valueOf(j), null);
        }
        this.lastLoadedBotStarsStats.put(Long.valueOf(j), Long.valueOf(System.currentTimeMillis()));
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.botStarsUpdated, Long.valueOf(j));
    }

    public void lambda$getStarsRevenueStats$1(final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsController.this.lambda$getStarsRevenueStats$0(tLObject, j);
            }
        });
    }

    public void lambda$getTONRevenueStats$2(TLObject tLObject, long j) {
        if (tLObject instanceof TL_stats.TL_broadcastRevenueStats) {
            this.tonStats.put(Long.valueOf(j), (TL_stats.TL_broadcastRevenueStats) tLObject);
        } else {
            this.tonStats.put(Long.valueOf(j), null);
        }
        this.lastLoadedTonStats.put(Long.valueOf(j), Long.valueOf(System.currentTimeMillis()));
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.botStarsUpdated, Long.valueOf(j));
    }

    public void lambda$getTONRevenueStats$3(final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsController.this.lambda$getTONRevenueStats$2(tLObject, j);
            }
        });
    }

    public void lambda$loadAdmined$6(TLObject tLObject) {
        this.adminedBots = new ArrayList();
        this.loadingAdminedBots = false;
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                this.adminedBots.add((TLRPC.User) vector.objects.get(i));
            }
            MessagesController.getInstance(this.currentAccount).putUsers(this.adminedBots, false);
        }
    }

    public void lambda$loadAdmined$7(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsController.this.lambda$loadAdmined$6(tLObject);
            }
        });
    }

    public void lambda$loadAdmined$8(TLObject tLObject) {
        this.adminedChannels = new ArrayList();
        this.loadingAdminedBots = false;
        if (tLObject instanceof TLRPC.messages_Chats) {
            TLRPC.messages_Chats messages_chats = (TLRPC.messages_Chats) tLObject;
            MessagesController.getInstance(this.currentAccount).putChats(messages_chats.chats, false);
            this.adminedChannels.addAll(messages_chats.chats);
        }
    }

    public void lambda$loadAdmined$9(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsController.this.lambda$loadAdmined$8(tLObject);
            }
        });
    }

    public void lambda$loadTransactions$4(TransactionsState transactionsState, int i, TLObject tLObject, long j) {
        transactionsState.loading[i] = false;
        if (tLObject instanceof TL_stars.StarsStatus) {
            TL_stars.StarsStatus starsStatus = (TL_stars.StarsStatus) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(starsStatus.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(starsStatus.chats, false);
            transactionsState.transactions[i].addAll(starsStatus.history);
            transactionsState.transactionsExist[i] = !transactionsState.transactions[i].isEmpty() || transactionsState.transactionsExist[i];
            transactionsState.endReached[i] = (starsStatus.flags & 1) == 0;
            transactionsState.offset[i] = transactionsState.endReached[i] ? null : starsStatus.next_offset;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.botStarsTransactionsLoaded, Long.valueOf(j));
        }
    }

    public void lambda$loadTransactions$5(final TransactionsState transactionsState, final int i, final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsController.this.lambda$loadTransactions$4(transactionsState, i, tLObject, j);
            }
        });
    }

    public boolean botHasStars(long j) {
        TLRPC.TL_starsRevenueStatus tL_starsRevenueStatus;
        TLRPC.TL_payments_starsRevenueStats starsRevenueStats = getStarsRevenueStats(j);
        return (starsRevenueStats == null || (tL_starsRevenueStatus = starsRevenueStats.status) == null || (tL_starsRevenueStatus.available_balance.amount <= 0 && tL_starsRevenueStatus.overall_revenue.amount <= 0 && tL_starsRevenueStatus.current_balance.amount <= 0)) ? false : true;
    }

    public boolean botHasTON(long j) {
        TL_stats.TL_broadcastRevenueStats tONRevenueStats = getTONRevenueStats(j, false);
        if (tONRevenueStats == null) {
            return false;
        }
        TLRPC.BroadcastRevenueBalances broadcastRevenueBalances = tONRevenueStats.balances;
        return broadcastRevenueBalances.current_balance > 0 || broadcastRevenueBalances.available_balance > 0 || broadcastRevenueBalances.overall_revenue > 0;
    }

    public boolean didFullyLoadTransactions(long j, int i) {
        return getTransactionsState(j).endReached[i];
    }

    public ArrayList getAdmined() {
        loadAdmined();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = this.adminedBots;
        if (arrayList2 != null) {
            arrayList.addAll(arrayList2);
        }
        ArrayList arrayList3 = this.adminedChannels;
        if (arrayList3 != null) {
            arrayList.addAll(arrayList3);
        }
        return arrayList;
    }

    public long getAvailableBalance(long j) {
        TLRPC.TL_payments_starsRevenueStats starsRevenueStats = getStarsRevenueStats(j);
        if (starsRevenueStats == null) {
            return 0L;
        }
        return starsRevenueStats.status.available_balance.amount;
    }

    public TL_stars.StarsAmount getBotStarsBalance(long j) {
        TLRPC.TL_payments_starsRevenueStats starsRevenueStats = getStarsRevenueStats(j);
        return starsRevenueStats == null ? new TL_stars.StarsAmount(0L) : starsRevenueStats.status.current_balance;
    }

    public ChannelConnectedBots getChannelConnectedBots(long j) {
        ChannelConnectedBots channelConnectedBots = (ChannelConnectedBots) this.connectedBots.get(Long.valueOf(j));
        if (channelConnectedBots != null) {
            return channelConnectedBots;
        }
        HashMap hashMap = this.connectedBots;
        Long valueOf = Long.valueOf(j);
        ChannelConnectedBots channelConnectedBots2 = new ChannelConnectedBots(this.currentAccount, j);
        hashMap.put(valueOf, channelConnectedBots2);
        return channelConnectedBots2;
    }

    public ChannelSuggestedBots getChannelSuggestedBots(long j) {
        ChannelSuggestedBots channelSuggestedBots = (ChannelSuggestedBots) this.suggestedBots.get(Long.valueOf(j));
        if (channelSuggestedBots != null) {
            return channelSuggestedBots;
        }
        HashMap hashMap = this.suggestedBots;
        Long valueOf = Long.valueOf(j);
        ChannelSuggestedBots channelSuggestedBots2 = new ChannelSuggestedBots(this.currentAccount, j);
        hashMap.put(valueOf, channelSuggestedBots2);
        return channelSuggestedBots2;
    }

    public void getConnectedBot(Context context, long j, final long j2, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        ChannelConnectedBots channelConnectedBots = (ChannelConnectedBots) this.connectedBots.get(Long.valueOf(j));
        if (channelConnectedBots != null) {
            for (int i = 0; i < channelConnectedBots.bots.size(); i++) {
                if (!((TL_payments.connectedBotStarRef) channelConnectedBots.bots.get(i)).revoked && ((TL_payments.connectedBotStarRef) channelConnectedBots.bots.get(i)).bot_id == j2) {
                    callback.run((TL_payments.connectedBotStarRef) channelConnectedBots.bots.get(i));
                    return;
                }
            }
        }
        final AlertDialog alertDialog = new AlertDialog(context, 3);
        TL_payments.getConnectedStarRefBot getconnectedstarrefbot = new TL_payments.getConnectedStarRefBot();
        getconnectedstarrefbot.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        getconnectedstarrefbot.bot = MessagesController.getInstance(this.currentAccount).getInputUser(j2);
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(getconnectedstarrefbot, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotStarsController.this.lambda$getConnectedBot$11(alertDialog, j2, callback, tLObject, tL_error);
            }
        });
        alertDialog.setCanCancel(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                BotStarsController.this.lambda$getConnectedBot$12(sendRequest, dialogInterface);
            }
        });
        alertDialog.showDelayed(200L);
    }

    public TLRPC.TL_payments_starsRevenueStats getStarsRevenueStats(long j) {
        return getStarsRevenueStats(j, false);
    }

    public TLRPC.TL_payments_starsRevenueStats getStarsRevenueStats(final long j, boolean z) {
        Long l = (Long) this.lastLoadedBotStarsStats.get(Long.valueOf(j));
        TLRPC.TL_payments_starsRevenueStats tL_payments_starsRevenueStats = (TLRPC.TL_payments_starsRevenueStats) this.botStarsStats.get(Long.valueOf(j));
        if (l == null || System.currentTimeMillis() - l.longValue() > 300000 || z) {
            TLRPC.TL_payments_getStarsRevenueStats tL_payments_getStarsRevenueStats = new TLRPC.TL_payments_getStarsRevenueStats();
            tL_payments_getStarsRevenueStats.dark = Theme.isCurrentThemeDark();
            tL_payments_getStarsRevenueStats.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getStarsRevenueStats, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotStarsController.this.lambda$getStarsRevenueStats$1(j, tLObject, tL_error);
                }
            });
        }
        return tL_payments_starsRevenueStats;
    }

    public long getTONBalance(long j) {
        TLRPC.BroadcastRevenueBalances broadcastRevenueBalances;
        TL_stats.TL_broadcastRevenueStats tONRevenueStats = getTONRevenueStats(j, false);
        if (tONRevenueStats == null || (broadcastRevenueBalances = tONRevenueStats.balances) == null) {
            return 0L;
        }
        return broadcastRevenueBalances.current_balance;
    }

    public TL_stats.TL_broadcastRevenueStats getTONRevenueStats(final long j, boolean z) {
        Long l = (Long) this.lastLoadedTonStats.get(Long.valueOf(j));
        TL_stats.TL_broadcastRevenueStats tL_broadcastRevenueStats = (TL_stats.TL_broadcastRevenueStats) this.tonStats.get(Long.valueOf(j));
        if (l == null || System.currentTimeMillis() - l.longValue() > 300000 || z) {
            TL_stats.TL_getBroadcastRevenueStats tL_getBroadcastRevenueStats = new TL_stats.TL_getBroadcastRevenueStats();
            tL_getBroadcastRevenueStats.dark = Theme.isCurrentThemeDark();
            tL_getBroadcastRevenueStats.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-j);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getBroadcastRevenueStats, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotStarsController.this.lambda$getTONRevenueStats$3(j, tLObject, tL_error);
                }
            }, null, null, 0, chatFull != null ? chatFull.stats_dc : Integer.MAX_VALUE, 1, true);
        }
        return tL_broadcastRevenueStats;
    }

    public ArrayList getTransactions(long j, int i) {
        return getTransactionsState(j).transactions[i];
    }

    public boolean hasTransactions(long j) {
        return hasTransactions(j, 0);
    }

    public boolean hasTransactions(long j, int i) {
        return !getTransactionsState(j).transactions[i].isEmpty();
    }

    public void invalidateTransactions(long j, boolean z) {
        TransactionsState transactionsState = getTransactionsState(j);
        for (int i = 0; i < 3; i++) {
            if (!transactionsState.loading[i]) {
                transactionsState.transactions[i].clear();
                transactionsState.offset[i] = null;
                transactionsState.loading[i] = false;
                transactionsState.endReached[i] = false;
                if (z) {
                    loadTransactions(j, i);
                }
            }
        }
    }

    public boolean isStarsBalanceAvailable(long j) {
        return getStarsRevenueStats(j) != null;
    }

    public boolean isTONBalanceAvailable(long j) {
        return getTONRevenueStats(j, false) != null;
    }

    public void loadAdmined() {
        if (!this.loadingAdminedBots || this.adminedBots != null) {
            this.loadingAdminedBots = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_bots.getAdminedBots(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotStarsController.this.lambda$loadAdmined$7(tLObject, tL_error);
                }
            });
        }
        if (this.loadingAdminedChannels && this.adminedChannels == null) {
            return;
        }
        this.loadingAdminedChannels = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotStarsController.this.lambda$loadAdmined$9(tLObject, tL_error);
            }
        });
    }

    public void loadTransactions(final long j, final int i) {
        final TransactionsState transactionsState = getTransactionsState(j);
        if (transactionsState.loading[i] || transactionsState.endReached[i]) {
            return;
        }
        transactionsState.loading[i] = true;
        TL_stars.TL_payments_getStarsTransactions tL_payments_getStarsTransactions = new TL_stars.TL_payments_getStarsTransactions();
        tL_payments_getStarsTransactions.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        tL_payments_getStarsTransactions.inbound = i == 1;
        tL_payments_getStarsTransactions.outbound = i == 2;
        String str = transactionsState.offset[i];
        tL_payments_getStarsTransactions.offset = str;
        if (str == null) {
            tL_payments_getStarsTransactions.offset = "";
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getStarsTransactions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotStarsController.this.lambda$loadTransactions$5(transactionsState, i, j, tLObject, tL_error);
            }
        });
    }

    public void onUpdate(TLRPC.TL_updateStarsRevenueStatus tL_updateStarsRevenueStatus) {
        if (tL_updateStarsRevenueStatus == null) {
            return;
        }
        long peerDialogId = DialogObject.getPeerDialogId(tL_updateStarsRevenueStatus.peer);
        if (peerDialogId >= 0) {
            TLRPC.TL_payments_starsRevenueStats starsRevenueStats = getStarsRevenueStats(peerDialogId, true);
            if (starsRevenueStats != null) {
                starsRevenueStats.status = tL_updateStarsRevenueStatus.status;
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.botStarsUpdated, Long.valueOf(peerDialogId));
            }
            invalidateTransactions(peerDialogId, true);
            return;
        }
        ChannelMonetizationLayout channelMonetizationLayout = ChannelMonetizationLayout.instance;
        if (channelMonetizationLayout == null || channelMonetizationLayout.dialogId != DialogObject.getPeerDialogId(tL_updateStarsRevenueStatus.peer)) {
            return;
        }
        ChannelMonetizationLayout.instance.setupBalances(tL_updateStarsRevenueStatus.status);
        ChannelMonetizationLayout.instance.reloadTransactions();
    }

    public void preloadStarsStats(long j) {
        Long l = (Long) this.lastLoadedBotStarsStats.get(Long.valueOf(j));
        getStarsRevenueStats(j, l == null || System.currentTimeMillis() - l.longValue() > 30000);
    }

    public void preloadTonStats(long j) {
        Long l = (Long) this.lastLoadedTonStats.get(Long.valueOf(j));
        getTONRevenueStats(j, l == null || System.currentTimeMillis() - l.longValue() > 30000);
    }
}
