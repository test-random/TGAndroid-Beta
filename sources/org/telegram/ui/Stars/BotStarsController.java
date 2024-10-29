package org.telegram.ui.Stars;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChannelMonetizationLayout;

public class BotStarsController {
    private static volatile BotStarsController[] Instance = new BotStarsController[4];
    private static final Object[] lockObjects = new Object[4];
    public final int currentAccount;
    private final HashMap lastLoadedBotStarsStats = new HashMap();
    private final HashMap botStarsStats = new HashMap();
    private final HashMap lastLoadedTonStats = new HashMap();
    private final HashMap tonStats = new HashMap();
    private final HashMap transactions = new HashMap();

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

    public void lambda$loadTransactions$4(TransactionsState transactionsState, int i, TLObject tLObject, long j) {
        transactionsState.loading[i] = false;
        if (tLObject instanceof TL_stars.TL_payments_starsStatus) {
            TL_stars.TL_payments_starsStatus tL_payments_starsStatus = (TL_stars.TL_payments_starsStatus) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_starsStatus.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_payments_starsStatus.chats, false);
            transactionsState.transactions[i].addAll(tL_payments_starsStatus.history);
            transactionsState.transactionsExist[i] = !transactionsState.transactions[i].isEmpty() || transactionsState.transactionsExist[i];
            transactionsState.endReached[i] = (tL_payments_starsStatus.flags & 1) == 0;
            transactionsState.offset[i] = transactionsState.endReached[i] ? null : tL_payments_starsStatus.next_offset;
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
        return (starsRevenueStats == null || (tL_starsRevenueStatus = starsRevenueStats.status) == null || (tL_starsRevenueStatus.available_balance <= 0 && tL_starsRevenueStatus.overall_revenue <= 0 && tL_starsRevenueStatus.current_balance <= 0)) ? false : true;
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

    public long getAvailableBalance(long j) {
        TLRPC.TL_payments_starsRevenueStats starsRevenueStats = getStarsRevenueStats(j);
        if (starsRevenueStats == null) {
            return 0L;
        }
        return starsRevenueStats.status.available_balance;
    }

    public long getBotStarsBalance(long j) {
        TLRPC.TL_payments_starsRevenueStats starsRevenueStats = getStarsRevenueStats(j);
        if (starsRevenueStats == null) {
            return 0L;
        }
        return starsRevenueStats.status.current_balance;
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
