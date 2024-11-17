package org.telegram.messenger;

import com.google.android.exoplayer2.util.Consumer;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.LaunchActivity;

public class ChannelBoostsController {
    public static final int BOOSTS_FOR_LEVEL_1 = 1;
    public static final int BOOSTS_FOR_LEVEL_2 = 1;
    private final ConnectionsManager connectionsManager;
    private final int currentAccount;
    private final MessagesController messagesController;

    public static class CanApplyBoost {
        public boolean alreadyActive;
        public int boostCount = 0;
        public boolean boostedNow;
        public boolean canApply;
        public TLRPC.Chat currentChat;
        public long currentDialogId;
        public TLRPC.Peer currentPeer;
        public boolean empty;
        public int floodWait;
        public boolean isMaxLvl;
        public TL_stories.TL_premium_myBoosts myBoosts;
        public boolean needSelector;
        public long replaceDialogId;
        public int slot;

        public CanApplyBoost copy() {
            CanApplyBoost canApplyBoost = new CanApplyBoost();
            canApplyBoost.canApply = this.canApply;
            canApplyBoost.empty = this.empty;
            canApplyBoost.replaceDialogId = this.replaceDialogId;
            canApplyBoost.alreadyActive = this.alreadyActive;
            canApplyBoost.needSelector = this.needSelector;
            canApplyBoost.slot = this.slot;
            canApplyBoost.myBoosts = this.myBoosts;
            canApplyBoost.boostCount = this.boostCount;
            canApplyBoost.currentPeer = this.currentPeer;
            canApplyBoost.currentDialogId = this.currentDialogId;
            canApplyBoost.currentChat = this.currentChat;
            canApplyBoost.isMaxLvl = this.isMaxLvl;
            return canApplyBoost;
        }

        public void setMyBoosts(org.telegram.tgnet.tl.TL_stories.TL_premium_myBoosts r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChannelBoostsController.CanApplyBoost.setMyBoosts(org.telegram.tgnet.tl.TL_stories$TL_premium_myBoosts):void");
        }
    }

    public ChannelBoostsController(int i) {
        this.currentAccount = i;
        this.messagesController = MessagesController.getInstance(i);
        this.connectionsManager = ConnectionsManager.getInstance(i);
    }

    public static void lambda$getBoostsStats$0(TLObject tLObject, Consumer consumer, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            consumer.accept((TL_stories.TL_premium_boostsStatus) tLObject);
            return;
        }
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (tL_error == null || lastFragment == null || !"CHANNEL_PRIVATE".equals(tL_error.text)) {
            BulletinFactory.global().showForError(tL_error);
        } else {
            LaunchActivity launchActivity = LaunchActivity.instance;
            if (launchActivity == null || !launchActivity.isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(lastFragment.getContext(), lastFragment.getResourceProvider());
                builder.setTitle(LocaleController.getString(R.string.AppName));
                HashMap hashMap = new HashMap();
                int i = Theme.key_dialogTopBackground;
                hashMap.put("info1.**", Integer.valueOf(Theme.getColor(i)));
                hashMap.put("info2.**", Integer.valueOf(Theme.getColor(i)));
                builder.setTopAnimation(R.raw.not_available, 52, false, Theme.getColor(i), hashMap);
                builder.setTopAnimationIsNew(true);
                builder.setTitle(LocaleController.getString(R.string.ChannelPrivate));
                builder.setMessage(LocaleController.getString(R.string.ChannelCantOpenPrivate2));
                builder.setPositiveButton(LocaleController.getString(R.string.Close), null);
                builder.show();
            }
        }
        consumer.accept(null);
    }

    public static void lambda$getBoostsStats$1(final Consumer consumer, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelBoostsController.lambda$getBoostsStats$0(TLObject.this, consumer, tL_error);
            }
        });
    }

    public static void lambda$userCanBoostChannel$2(CanApplyBoost canApplyBoost, TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, Consumer consumer, TL_stories.TL_premium_myBoosts tL_premium_myBoosts) {
        canApplyBoost.isMaxLvl = tL_premium_boostsStatus.next_level_boosts <= 0;
        canApplyBoost.setMyBoosts(tL_premium_myBoosts);
        consumer.accept(canApplyBoost);
    }

    public static void lambda$userCanBoostChannel$3(CanApplyBoost canApplyBoost, Consumer consumer, TLRPC.TL_error tL_error) {
        boolean startsWith = tL_error.text.startsWith("FLOOD_WAIT");
        String str = tL_error.text;
        if (startsWith) {
            canApplyBoost.floodWait = Utilities.parseInt((CharSequence) str).intValue();
        } else if (str.startsWith("BOOSTS_EMPTY")) {
            canApplyBoost.empty = true;
        }
        canApplyBoost.canApply = false;
        consumer.accept(canApplyBoost);
    }

    public void applyBoost(long j, int i, Utilities.Callback<TL_stories.TL_premium_myBoosts> callback, Utilities.Callback<TLRPC.TL_error> callback2) {
        BoostRepository.applyBoost(-j, Arrays.asList(Integer.valueOf(i)), callback, callback2);
    }

    public void getBoostsStats(long j, final Consumer consumer) {
        TL_stories.TL_premium_getBoostsStatus tL_premium_getBoostsStatus = new TL_stories.TL_premium_getBoostsStatus();
        tL_premium_getBoostsStatus.peer = this.messagesController.getInputPeer(j);
        this.connectionsManager.sendRequest(tL_premium_getBoostsStatus, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelBoostsController.lambda$getBoostsStats$1(Consumer.this, tLObject, tL_error);
            }
        });
    }

    public void userCanBoostChannel(long j, final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, final Consumer consumer) {
        final CanApplyBoost canApplyBoost = new CanApplyBoost();
        canApplyBoost.currentPeer = this.messagesController.getPeer(j);
        canApplyBoost.currentDialogId = j;
        canApplyBoost.currentChat = this.messagesController.getChat(Long.valueOf(-j));
        BoostRepository.getMyBoosts(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChannelBoostsController.lambda$userCanBoostChannel$2(ChannelBoostsController.CanApplyBoost.this, tL_premium_boostsStatus, consumer, (TL_stories.TL_premium_myBoosts) obj);
            }
        }, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChannelBoostsController.lambda$userCanBoostChannel$3(ChannelBoostsController.CanApplyBoost.this, consumer, (TLRPC.TL_error) obj);
            }
        });
    }
}
