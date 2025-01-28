package org.telegram.ui.Stars;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import j$.util.Comparator$CC;
import j$.util.Comparator$EL;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.ToIntFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.bots.BotWebViewSheet;

public class StarsController {
    private static volatile StarsController[] Instance = new StarsController[4];
    private static final Object[] lockObjects = new Object[4];
    private boolean balanceLoaded;
    private boolean balanceLoading;
    public final int currentAccount;
    public PendingPaidReactions currentPendingReactions;
    private ArrayList giftOptions;
    private boolean giftOptionsLoaded;
    private boolean giftOptionsLoading;
    private boolean giftsCacheLoaded;
    public int giftsHash;
    public boolean giftsLoaded;
    public boolean giftsLoading;
    public long giftsRemoteTime;
    private ArrayList giveawayOptions;
    private boolean giveawayOptionsLoaded;
    private boolean giveawayOptionsLoading;
    private boolean insufficientSubscriptionsLoading;
    private long lastBalanceLoaded;
    public long minus;
    private ArrayList options;
    private boolean optionsLoaded;
    private boolean optionsLoading;
    private boolean paymentFormOpened;
    public boolean subscriptionsEndReached;
    public boolean subscriptionsLoading;
    public String subscriptionsOffset;
    public TL_stars.StarsAmount balance = new TL_stars.StarsAmount(0);
    public final ArrayList[] transactions = {new ArrayList(), new ArrayList(), new ArrayList()};
    public final boolean[] transactionsExist = new boolean[3];
    private final String[] offset = new String[3];
    private final boolean[] loading = new boolean[3];
    private final boolean[] endReached = new boolean[3];
    public final ArrayList subscriptions = new ArrayList();
    public final ArrayList insufficientSubscriptions = new ArrayList();
    public final ArrayList gifts = new ArrayList();
    public final ArrayList sortedGifts = new ArrayList();
    public final ArrayList birthdaySortedGifts = new ArrayList();
    public final LongSparseArray giftLists = new LongSparseArray();
    private ConcurrentHashMap giftPreviews = new ConcurrentHashMap();

    public static class GiftsList {
        public Boolean chat_notifications_enabled;
        public final int currentAccount;
        public int currentRequestId;
        public final long dialogId;
        public boolean endReached;
        public ArrayList gifts;
        public boolean include_displayed;
        public boolean include_hidden;
        public boolean include_limited;
        public boolean include_unique;
        public boolean include_unlimited;
        public String lastOffset;
        public boolean loading;
        public boolean shown;
        public boolean sort_by_date;
        public int totalCount;

        public GiftsList(int i, long j) {
            this(i, j, true);
        }

        public GiftsList(int i, long j, boolean z) {
            this.sort_by_date = true;
            this.include_unlimited = true;
            this.include_limited = true;
            this.include_unique = true;
            this.include_displayed = true;
            this.include_hidden = true;
            this.gifts = new ArrayList();
            this.currentRequestId = -1;
            this.currentAccount = i;
            this.dialogId = j;
            if (z) {
                load();
            }
        }

        public void lambda$load$0(int[] iArr, TLObject tLObject, boolean z) {
            if (iArr[0] != this.currentRequestId) {
                return;
            }
            this.loading = false;
            this.currentRequestId = -1;
            if (tLObject instanceof TL_stars.TL_payments_savedStarGifts) {
                TL_stars.TL_payments_savedStarGifts tL_payments_savedStarGifts = (TL_stars.TL_payments_savedStarGifts) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_savedStarGifts.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(tL_payments_savedStarGifts.chats, false);
                if (z) {
                    this.gifts.clear();
                }
                this.gifts.addAll(tL_payments_savedStarGifts.gifts);
                this.lastOffset = tL_payments_savedStarGifts.next_offset;
                this.totalCount = tL_payments_savedStarGifts.count;
                this.chat_notifications_enabled = (tL_payments_savedStarGifts.flags & 2) != 0 ? Boolean.valueOf(tL_payments_savedStarGifts.chat_notifications_enabled) : null;
                this.endReached = this.gifts.size() > this.totalCount || this.lastOffset == null;
            } else {
                this.endReached = true;
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starUserGiftsLoaded, Long.valueOf(this.dialogId), this);
        }

        public void lambda$load$1(final int[] iArr, final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.GiftsList.this.lambda$load$0(iArr, tLObject, z);
                }
            });
        }

        public boolean hasFilters() {
            return (this.sort_by_date && this.include_unlimited && this.include_limited && this.include_unique && this.include_displayed && this.include_hidden) ? false : true;
        }

        public void invalidate(boolean z) {
            if (this.currentRequestId != -1) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestId, true);
                this.currentRequestId = -1;
            }
            this.loading = false;
            this.gifts.clear();
            this.lastOffset = null;
            this.endReached = false;
            if (z || this.shown) {
                load();
            }
        }

        public void load() {
            if (this.loading || this.endReached) {
                return;
            }
            final boolean z = this.lastOffset == null;
            this.loading = true;
            TL_stars.getSavedStarGifts getsavedstargifts = new TL_stars.getSavedStarGifts();
            getsavedstargifts.sort_by_value = !this.sort_by_date;
            getsavedstargifts.exclude_limited = !this.include_limited;
            getsavedstargifts.exclude_unlimited = !this.include_unlimited;
            getsavedstargifts.exclude_unique = !this.include_unique;
            getsavedstargifts.exclude_saved = !this.include_displayed;
            getsavedstargifts.exclude_unsaved = !this.include_hidden;
            getsavedstargifts.peer = this.dialogId == 0 ? new TLRPC.TL_inputPeerSelf() : MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            getsavedstargifts.offset = z ? "" : this.lastOffset;
            getsavedstargifts.limit = z ? 15 : 30;
            int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(getsavedstargifts, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarsController.GiftsList.this.lambda$load$1(r2, z, tLObject, tL_error);
                }
            });
            this.currentRequestId = sendRequest;
            final int[] iArr = {sendRequest};
        }

        public void resetFilters() {
            if (hasFilters()) {
                this.sort_by_date = true;
                this.include_unlimited = true;
                this.include_limited = true;
                this.include_unique = true;
                this.include_displayed = true;
                this.include_hidden = true;
                invalidate(true);
            }
        }
    }

    public static class MessageId {
        public long did;
        public int mid;

        private MessageId(long j, int i) {
            this.did = j;
            this.mid = i;
        }

        public static MessageId from(long j, int i) {
            return new MessageId(j, i);
        }

        public static MessageId from(MessageObject messageObject) {
            if (messageObject == null) {
                return null;
            }
            TLRPC.Message message = messageObject.messageOwner;
            return (message == null || !(message.isThreadMessage || messageObject.isForwardedChannelPost()) || messageObject.messageOwner.fwd_from == null) ? new MessageId(messageObject.getDialogId(), messageObject.getId()) : new MessageId(messageObject.getFromChatId(), messageObject.messageOwner.fwd_from.saved_from_msg_id);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MessageId)) {
                return false;
            }
            MessageId messageId = (MessageId) obj;
            return messageId.did == this.did && messageId.mid == this.mid;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.did), Integer.valueOf(this.mid));
        }
    }

    public class PendingPaidReactions {
        public long amount;
        public boolean applied;
        public Bulletin bulletin;
        public Bulletin.UndoButton bulletinButton;
        public Bulletin.TwoLineAnimatedLottieLayout bulletinLayout;
        public final Runnable cancelRunnable;
        public ChatActivity chatActivity;
        public final Runnable closeRunnable;
        public long lastTime;
        public MessageId message;
        public MessageObject messageObject;
        public long not_added;
        public StarReactionsOverlay overlay;
        public long random_id;
        public boolean shownBulletin;
        public Bulletin.TimerView timerView;
        public boolean wasChosen;
        public boolean committed = false;
        public boolean cancelled = false;
        public Boolean anonymous = null;

        public PendingPaidReactions(MessageId messageId, MessageObject messageObject, ChatActivity chatActivity, long j, boolean z) {
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    StarsController.PendingPaidReactions.this.close();
                }
            };
            this.closeRunnable = runnable;
            this.cancelRunnable = new Runnable() {
                @Override
                public final void run() {
                    StarsController.PendingPaidReactions.this.cancel();
                }
            };
            this.message = messageId;
            this.messageObject = messageObject;
            this.random_id = (Utilities.random.nextLong() & 4294967295L) | (j << 32);
            this.chatActivity = chatActivity;
            Context context = StarsController.this.getContext(chatActivity);
            Bulletin.TwoLineAnimatedLottieLayout twoLineAnimatedLottieLayout = new Bulletin.TwoLineAnimatedLottieLayout(context, chatActivity.themeDelegate);
            this.bulletinLayout = twoLineAnimatedLottieLayout;
            twoLineAnimatedLottieLayout.setAnimation(R.raw.stars_topup, new String[0]);
            this.bulletinLayout.titleTextView.setText(LocaleController.getString(isAnonymous() ? R.string.StarsSentAnonymouslyTitle : R.string.StarsSentTitle));
            Bulletin.UndoButton undoButton = new Bulletin.UndoButton(context, true, false, chatActivity.themeDelegate);
            this.bulletinButton = undoButton;
            undoButton.setText(LocaleController.getString(R.string.StarsSentUndo));
            this.bulletinButton.setUndoAction(new Runnable() {
                @Override
                public final void run() {
                    StarsController.PendingPaidReactions.this.cancel();
                }
            });
            Bulletin.TimerView timerView = new Bulletin.TimerView(context, chatActivity.themeDelegate);
            this.timerView = timerView;
            timerView.timeLeft = 5000L;
            timerView.setColor(Theme.getColor(Theme.key_undo_cancelColor, chatActivity.themeDelegate));
            this.bulletinButton.addView(this.timerView, LayoutHelper.createFrame(20, 20.0f, 21, 0.0f, 0.0f, 12.0f, 0.0f));
            this.bulletinButton.undoTextView.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(8.0f));
            this.bulletinLayout.setButton(this.bulletinButton);
            Bulletin create = BulletinFactory.of(chatActivity).create(this.bulletinLayout, -1);
            this.bulletin = create;
            create.hideAfterBottomSheet = false;
            if (z) {
                create.show(true);
                this.shownBulletin = true;
            }
            this.bulletin.setOnHideListener(runnable);
            this.amount = 0L;
            this.lastTime = System.currentTimeMillis();
            this.wasChosen = messageObject.isPaidReactionChosen();
        }

        public void lambda$commit$0(long j) {
            StarsController.this.sendPaidReaction(this.messageObject, this.chatActivity, j, true, true, this.anonymous);
        }

        public static void lambda$commit$1(MessagesController messagesController, TLObject tLObject) {
            messagesController.processUpdates((TLRPC.Updates) tLObject, false);
        }

        public void lambda$commit$2(long j) {
            StarsController.this.sendPaidReaction(this.messageObject, this.chatActivity, j, true, true, this.anonymous);
        }

        public void lambda$commit$3(final TLObject tLObject, final MessagesController messagesController, TLRPC.TL_error tL_error, final long j) {
            String str;
            if (tLObject != null) {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        StarsController.PendingPaidReactions.lambda$commit$1(MessagesController.this, tLObject);
                    }
                });
                return;
            }
            if (tL_error != null) {
                this.messageObject.addPaidReactions((int) (-this.amount), this.wasChosen, isAnonymous());
                NotificationCenter.getInstance(StarsController.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(this.messageObject.getDialogId()), Integer.valueOf(this.messageObject.getId()), this.messageObject.messageOwner.reactions);
                if ("BALANCE_TOO_LOW".equals(tL_error.text)) {
                    long j2 = this.message.did;
                    MessagesController messagesController2 = this.chatActivity.getMessagesController();
                    MessageId messageId = this.message;
                    if (j2 >= 0) {
                        str = UserObject.getForcedFirstName(messagesController2.getUser(Long.valueOf(messageId.did)));
                    } else {
                        TLRPC.Chat chat = messagesController2.getChat(Long.valueOf(-messageId.did));
                        str = chat == null ? "" : chat.title;
                    }
                    String str2 = str;
                    Context context = this.chatActivity.getContext();
                    if (context == null) {
                        context = LaunchActivity.instance;
                    }
                    if (context == null) {
                        context = ApplicationLoader.applicationContext;
                    }
                    new StarsIntroActivity.StarsNeededSheet(context, this.chatActivity.getResourceProvider(), j, 5, str2, new Runnable() {
                        @Override
                        public final void run() {
                            StarsController.PendingPaidReactions.this.lambda$commit$2(j);
                        }
                    }).show();
                }
                StarsController.this.invalidateTransactions(false);
                StarsController.this.invalidateBalance();
            }
        }

        public void lambda$commit$4(final MessagesController messagesController, final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.PendingPaidReactions.this.lambda$commit$3(tLObject, messagesController, tL_error, j);
                }
            });
        }

        public void add(long j, boolean z) {
            if (this.committed || this.cancelled) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    throw new RuntimeException("adding more amount to committed reactions");
                }
                return;
            }
            this.amount += j;
            this.lastTime = System.currentTimeMillis();
            this.bulletinLayout.subtitleTextView.cancelAnimation();
            this.bulletinLayout.subtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSentText", (int) this.amount, new Object[0])), true);
            if (this.shownBulletin) {
                this.timerView.timeLeft = 5000L;
                AndroidUtilities.cancelRunOnUIThread(this.closeRunnable);
                AndroidUtilities.runOnUIThread(this.closeRunnable, 5000L);
            }
            if (z) {
                this.applied = true;
                this.messageObject.addPaidReactions((int) j, true, isAnonymous());
                StarsController starsController = StarsController.this;
                starsController.minus += j;
                NotificationCenter.getInstance(starsController.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(this.messageObject.getDialogId()), Integer.valueOf(this.messageObject.getId()), this.messageObject.messageOwner.reactions);
                NotificationCenter.getInstance(StarsController.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
            } else {
                this.applied = false;
                if (this.messageObject.ensurePaidReactionsExist(true)) {
                    this.not_added--;
                }
                NotificationCenter.getInstance(StarsController.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(this.messageObject.getDialogId()), Integer.valueOf(this.messageObject.getId()), this.messageObject.messageOwner.reactions);
                this.not_added += j;
            }
            this.bulletinLayout.titleTextView.setText(LocaleController.getString(isAnonymous() ? R.string.StarsSentAnonymouslyTitle : R.string.StarsSentTitle));
        }

        public void apply() {
            if (!this.applied) {
                this.applied = true;
                this.messageObject.addPaidReactions((int) this.not_added, true, isAnonymous());
                StarsController starsController = StarsController.this;
                starsController.minus += this.not_added;
                NotificationCenter.getInstance(starsController.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
                this.not_added = 0L;
                NotificationCenter.getInstance(StarsController.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(this.messageObject.getDialogId()), Integer.valueOf(this.messageObject.getId()), this.messageObject.messageOwner.reactions);
            }
            if (!this.shownBulletin) {
                this.shownBulletin = true;
                this.timerView.timeLeft = 5000L;
                AndroidUtilities.cancelRunOnUIThread(this.closeRunnable);
                AndroidUtilities.runOnUIThread(this.closeRunnable, 5000L);
                this.bulletin.show(true);
                this.bulletin.setOnHideListener(this.closeRunnable);
            }
            this.bulletinLayout.titleTextView.setText(LocaleController.getString(isAnonymous() ? R.string.StarsSentAnonymouslyTitle : R.string.StarsSentTitle));
        }

        public void cancel() {
            AndroidUtilities.cancelRunOnUIThread(this.closeRunnable);
            this.cancelled = true;
            this.bulletin.hide();
            StarReactionsOverlay starReactionsOverlay = this.overlay;
            if (starReactionsOverlay != null) {
                starReactionsOverlay.hide();
            }
            this.messageObject.addPaidReactions((int) (-this.amount), this.wasChosen, isAnonymous());
            StarsController starsController = StarsController.this;
            starsController.minus -= this.amount;
            NotificationCenter.getInstance(starsController.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
            NotificationCenter.getInstance(StarsController.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(this.messageObject.getDialogId()), Integer.valueOf(this.messageObject.getId()), this.messageObject.messageOwner.reactions);
            StarsController starsController2 = StarsController.this;
            if (starsController2.currentPendingReactions == this) {
                starsController2.currentPendingReactions = null;
            }
        }

        public void close() {
            AndroidUtilities.cancelRunOnUIThread(this.closeRunnable);
            if (this.applied) {
                commit();
            } else {
                this.cancelled = true;
                this.messageObject.addPaidReactions((int) (-this.amount), this.wasChosen, isAnonymous());
                StarsController starsController = StarsController.this;
                starsController.minus -= this.amount;
                NotificationCenter.getInstance(starsController.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
            }
            this.bulletin.hide();
            StarReactionsOverlay starReactionsOverlay = this.overlay;
            if (starReactionsOverlay != null && starReactionsOverlay.isShowing(this.messageObject)) {
                this.overlay.hide();
            }
            StarsController starsController2 = StarsController.this;
            if (starsController2.currentPendingReactions == this) {
                starsController2.currentPendingReactions = null;
            }
        }

        public void commit() {
            String str;
            if (this.committed || this.cancelled) {
                return;
            }
            StarsController starsController = StarsController.getInstance(StarsController.this.currentAccount);
            final MessagesController messagesController = MessagesController.getInstance(StarsController.this.currentAccount);
            ConnectionsManager connectionsManager = ConnectionsManager.getInstance(StarsController.this.currentAccount);
            final long j = this.amount;
            if (!starsController.balanceAvailable() || starsController.getBalance(false) >= j) {
                this.committed = true;
                TLRPC.TL_messages_sendPaidReaction tL_messages_sendPaidReaction = new TLRPC.TL_messages_sendPaidReaction();
                tL_messages_sendPaidReaction.peer = messagesController.getInputPeer(this.message.did);
                tL_messages_sendPaidReaction.msg_id = this.message.mid;
                tL_messages_sendPaidReaction.random_id = this.random_id;
                tL_messages_sendPaidReaction.count = (int) this.amount;
                tL_messages_sendPaidReaction.isPrivate = Boolean.valueOf(isAnonymous());
                StarsController.this.invalidateBalance();
                connectionsManager.sendRequest(tL_messages_sendPaidReaction, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StarsController.PendingPaidReactions.this.lambda$commit$4(messagesController, j, tLObject, tL_error);
                    }
                });
                return;
            }
            this.cancelled = true;
            this.messageObject.addPaidReactions((int) (-this.amount), this.wasChosen, isAnonymous());
            StarsController starsController2 = StarsController.this;
            starsController2.minus = 0L;
            NotificationCenter.getInstance(starsController2.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
            NotificationCenter.getInstance(StarsController.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(this.messageObject.getDialogId()), Integer.valueOf(this.messageObject.getId()), this.messageObject.messageOwner.reactions);
            long j2 = this.message.did;
            MessagesController messagesController2 = this.chatActivity.getMessagesController();
            MessageId messageId = this.message;
            if (j2 >= 0) {
                str = UserObject.getForcedFirstName(messagesController2.getUser(Long.valueOf(messageId.did)));
            } else {
                TLRPC.Chat chat = messagesController2.getChat(Long.valueOf(-messageId.did));
                str = chat == null ? "" : chat.title;
            }
            String str2 = str;
            Context context = this.chatActivity.getContext();
            if (context == null) {
                context = LaunchActivity.instance;
            }
            if (context == null) {
                context = ApplicationLoader.applicationContext;
            }
            new StarsIntroActivity.StarsNeededSheet(context, this.chatActivity.getResourceProvider(), j, 5, str2, new Runnable() {
                @Override
                public final void run() {
                    StarsController.PendingPaidReactions.this.lambda$commit$0(j);
                }
            }).show();
        }

        public boolean isAnonymous() {
            Boolean bool = this.anonymous;
            return bool != null ? bool.booleanValue() : StarsController.this.arePaidReactionsAnonymous(this.messageObject);
        }

        public void setOverlay(StarReactionsOverlay starReactionsOverlay) {
            this.overlay = starReactionsOverlay;
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private StarsController(int i) {
        this.currentAccount = i;
    }

    private void bulletinError(String str) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        ((lastFragment == null || lastFragment.visibleDialog != null) ? BulletinFactory.global() : BulletinFactory.of(lastFragment)).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, str)).show();
    }

    private void bulletinError(TLRPC.TL_error tL_error, String str) {
        if (tL_error != null) {
            str = tL_error.text;
        }
        bulletinError(str);
    }

    public static TL_stars.StarGiftAttribute findAttribute(ArrayList arrayList, Class cls) {
        if (arrayList == null) {
            return null;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TL_stars.StarGiftAttribute starGiftAttribute = (TL_stars.StarGiftAttribute) it.next();
            if (cls.isInstance(starGiftAttribute)) {
                return (TL_stars.StarGiftAttribute) cls.cast(starGiftAttribute);
            }
        }
        return null;
    }

    public static ArrayList findAttributes(ArrayList arrayList, Class cls) {
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TL_stars.StarGiftAttribute starGiftAttribute = (TL_stars.StarGiftAttribute) it.next();
            if (cls.isInstance(starGiftAttribute)) {
                arrayList2.add((TL_stars.StarGiftAttribute) cls.cast(starGiftAttribute));
            }
        }
        return arrayList2;
    }

    public static StarsController getInstance(int i) {
        StarsController starsController = Instance[i];
        if (starsController == null) {
            synchronized (lockObjects[i]) {
                try {
                    starsController = Instance[i];
                    if (starsController == null) {
                        StarsController[] starsControllerArr = Instance;
                        StarsController starsController2 = new StarsController(i);
                        starsControllerArr[i] = starsController2;
                        starsController = starsController2;
                    }
                } finally {
                }
            }
        }
        return starsController;
    }

    private void getStarGiftsCached(final Utilities.Callback3 callback3) {
        if (callback3 == null) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$getStarGiftsCached$109(MessagesStorage.this, arrayList, callback3);
            }
        });
    }

    private void getStarGiftsRemote(int i, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        TL_stars.getStarGifts getstargifts = new TL_stars.getStarGifts();
        getstargifts.hash = i;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getstargifts, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.lambda$getStarGiftsRemote$112(Utilities.Callback.this, tLObject, tL_error);
            }
        });
    }

    public static void lambda$buy$26(Utilities.Callback2 callback2, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        Boolean bool;
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            if (callback2 == null) {
                return;
            } else {
                bool = Boolean.TRUE;
            }
        } else if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PENDING || callback2 == null) {
            return;
        } else {
            bool = Boolean.FALSE;
        }
        callback2.run(bool, null);
    }

    public void lambda$buy$27(TLRPC.TL_error tL_error, final Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars) {
        if (tL_error != null) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, tL_error.text);
                return;
            }
            return;
        }
        PaymentFormActivity paymentFormActivity = null;
        if (tLObject instanceof TLRPC.PaymentForm) {
            TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
            paymentForm.invoice.recurring = true;
            MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
            paymentFormActivity = new PaymentFormActivity(paymentForm, tL_inputInvoiceStars, (BaseFragment) null);
        } else if (tLObject instanceof TLRPC.PaymentReceipt) {
            paymentFormActivity = new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject);
        }
        if (paymentFormActivity == null) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, "UNKNOWN_RESPONSE");
                return;
            }
            return;
        }
        paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
            @Override
            public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                StarsController.lambda$buy$26(Utilities.Callback2.this, invoiceStatus);
            }
        });
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null) {
            return;
        }
        if (!AndroidUtilities.hasDialogOnTop(lastFragment)) {
            lastFragment.presentFragment(paymentFormActivity);
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(paymentFormActivity, bottomSheetParams);
    }

    public void lambda$buy$28(final Utilities.Callback2 callback2, final TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buy$27(tL_error, callback2, tLObject, tL_inputInvoiceStars);
            }
        });
    }

    public static void lambda$buy$29(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, "PRODUCT_NOT_FOUND");
    }

    public static void lambda$buy$30(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, "PRODUCT_NO_ONETIME_OFFER_DETAILS");
    }

    public static void lambda$buy$31(Utilities.Callback2 callback2, boolean z, String str) {
        callback2.run(Boolean.valueOf(z), str);
    }

    public static void lambda$buy$32(BillingResult billingResult, final Utilities.Callback2 callback2, BillingResult billingResult2) {
        final boolean z = billingResult.getResponseCode() == 0;
        final String responseCodeString = z ? null : BillingController.getResponseCodeString(billingResult.getResponseCode());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buy$31(Utilities.Callback2.this, z, responseCodeString);
            }
        });
    }

    public static void lambda$buy$33(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, null);
    }

    public static void lambda$buy$34(final Utilities.Callback2 callback2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buy$33(Utilities.Callback2.this);
            }
        });
    }

    public static void lambda$buy$35(List list, final Utilities.Callback2 callback2, TLRPC.TL_inputStorePaymentStarsTopup tL_inputStorePaymentStarsTopup, TL_stars.TL_starsTopupOption tL_starsTopupOption, final BillingResult billingResult, Activity activity) {
        if (list.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buy$29(Utilities.Callback2.this);
                }
            });
            return;
        }
        ProductDetails productDetails = (ProductDetails) list.get(0);
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails();
        if (oneTimePurchaseOfferDetails == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buy$30(Utilities.Callback2.this);
                }
            });
            return;
        }
        tL_inputStorePaymentStarsTopup.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
        double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
        double pow = Math.pow(10.0d, 6.0d);
        Double.isNaN(priceAmountMicros);
        tL_inputStorePaymentStarsTopup.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_starsTopupOption.currency)));
        BillingController.getInstance().addResultListener(productDetails.getProductId(), new Consumer() {
            @Override
            public final void accept(Object obj) {
                StarsController.lambda$buy$32(BillingResult.this, callback2, (BillingResult) obj);
            }
        });
        BillingController.getInstance().setOnCanceled(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buy$34(Utilities.Callback2.this);
            }
        });
        BillingController.getInstance().launchBillingFlow(activity, AccountInstance.getInstance(UserConfig.selectedAccount), tL_inputStorePaymentStarsTopup, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails((ProductDetails) list.get(0)).build()));
    }

    public static void lambda$buy$36(final Utilities.Callback2 callback2, final TLRPC.TL_inputStorePaymentStarsTopup tL_inputStorePaymentStarsTopup, final TL_stars.TL_starsTopupOption tL_starsTopupOption, final Activity activity, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buy$35(list, callback2, tL_inputStorePaymentStarsTopup, tL_starsTopupOption, billingResult, activity);
            }
        });
    }

    public static void lambda$buyGift$37(Utilities.Callback2 callback2, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        Boolean bool;
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            if (callback2 == null) {
                return;
            } else {
                bool = Boolean.TRUE;
            }
        } else if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PENDING || callback2 == null) {
            return;
        } else {
            bool = Boolean.FALSE;
        }
        callback2.run(bool, null);
    }

    public void lambda$buyGift$38(TLRPC.TL_error tL_error, final Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars) {
        if (tL_error != null) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, tL_error.text);
                return;
            }
            return;
        }
        PaymentFormActivity paymentFormActivity = null;
        if (tLObject instanceof TLRPC.PaymentForm) {
            TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
            paymentForm.invoice.recurring = true;
            MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
            paymentFormActivity = new PaymentFormActivity(paymentForm, tL_inputInvoiceStars, (BaseFragment) null);
        } else if (tLObject instanceof TLRPC.PaymentReceipt) {
            paymentFormActivity = new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject);
        }
        if (paymentFormActivity == null) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, "UNKNOWN_RESPONSE");
                return;
            }
            return;
        }
        paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
            @Override
            public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                StarsController.lambda$buyGift$37(Utilities.Callback2.this, invoiceStatus);
            }
        });
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null) {
            return;
        }
        if (!AndroidUtilities.hasDialogOnTop(lastFragment)) {
            lastFragment.presentFragment(paymentFormActivity);
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(paymentFormActivity, bottomSheetParams);
    }

    public void lambda$buyGift$39(final Utilities.Callback2 callback2, final TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyGift$38(tL_error, callback2, tLObject, tL_inputInvoiceStars);
            }
        });
    }

    public static void lambda$buyGift$40(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, "PRODUCT_NOT_FOUND");
    }

    public static void lambda$buyGift$41(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, "PRODUCT_NO_ONETIME_OFFER_DETAILS");
    }

    public static void lambda$buyGift$42(Utilities.Callback2 callback2, boolean z, String str) {
        callback2.run(Boolean.valueOf(z), str);
    }

    public static void lambda$buyGift$43(BillingResult billingResult, final Utilities.Callback2 callback2, BillingResult billingResult2) {
        final boolean z = billingResult.getResponseCode() == 0;
        final String responseCodeString = z ? null : BillingController.getResponseCodeString(billingResult.getResponseCode());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyGift$42(Utilities.Callback2.this, z, responseCodeString);
            }
        });
    }

    public static void lambda$buyGift$44(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, null);
    }

    public static void lambda$buyGift$45(final Utilities.Callback2 callback2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyGift$44(Utilities.Callback2.this);
            }
        });
    }

    public static void lambda$buyGift$46(TLObject tLObject, ProductDetails productDetails, final BillingResult billingResult, final Utilities.Callback2 callback2, Activity activity, TLRPC.TL_inputStorePaymentStarsGift tL_inputStorePaymentStarsGift, List list, TLRPC.TL_error tL_error) {
        Boolean bool;
        String str;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            BillingController.getInstance().addResultListener(productDetails.getProductId(), new Consumer() {
                @Override
                public final void accept(Object obj) {
                    StarsController.lambda$buyGift$43(BillingResult.this, callback2, (BillingResult) obj);
                }
            });
            BillingController.getInstance().setOnCanceled(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyGift$45(Utilities.Callback2.this);
                }
            });
            BillingController.getInstance().launchBillingFlow(activity, AccountInstance.getInstance(UserConfig.selectedAccount), tL_inputStorePaymentStarsGift, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails((ProductDetails) list.get(0)).build()));
            return;
        }
        if (tLObject instanceof TLRPC.TL_boolFalse) {
            if (callback2 == null) {
                return;
            }
            bool = Boolean.FALSE;
            str = "PURCHASE_FORBIDDEN";
        } else {
            if (callback2 == null) {
                return;
            }
            bool = Boolean.FALSE;
            str = tL_error != null ? tL_error.text : "SERVER_ERROR";
        }
        callback2.run(bool, str);
    }

    public static void lambda$buyGift$47(final ProductDetails productDetails, final BillingResult billingResult, final Utilities.Callback2 callback2, final Activity activity, final TLRPC.TL_inputStorePaymentStarsGift tL_inputStorePaymentStarsGift, final List list, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyGift$46(TLObject.this, productDetails, billingResult, callback2, activity, tL_inputStorePaymentStarsGift, list, tL_error);
            }
        });
    }

    public void lambda$buyGift$48(final List list, final Utilities.Callback2 callback2, final TLRPC.TL_inputStorePaymentStarsGift tL_inputStorePaymentStarsGift, TL_stars.TL_starsGiftOption tL_starsGiftOption, final BillingResult billingResult, final Activity activity) {
        if (list.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyGift$40(Utilities.Callback2.this);
                }
            });
            return;
        }
        final ProductDetails productDetails = (ProductDetails) list.get(0);
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails();
        if (oneTimePurchaseOfferDetails == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyGift$41(Utilities.Callback2.this);
                }
            });
            return;
        }
        tL_inputStorePaymentStarsGift.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
        double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
        double pow = Math.pow(10.0d, 6.0d);
        Double.isNaN(priceAmountMicros);
        tL_inputStorePaymentStarsGift.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_starsGiftOption.currency)));
        TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium = new TLRPC.TL_payments_canPurchasePremium();
        tL_payments_canPurchasePremium.purpose = tL_inputStorePaymentStarsGift;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_canPurchasePremium, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.lambda$buyGift$47(ProductDetails.this, billingResult, callback2, activity, tL_inputStorePaymentStarsGift, list, tLObject, tL_error);
            }
        });
    }

    public void lambda$buyGift$49(final Utilities.Callback2 callback2, final TLRPC.TL_inputStorePaymentStarsGift tL_inputStorePaymentStarsGift, final TL_stars.TL_starsGiftOption tL_starsGiftOption, final Activity activity, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyGift$48(list, callback2, tL_inputStorePaymentStarsGift, tL_starsGiftOption, billingResult, activity);
            }
        });
    }

    public static void lambda$buyGiveaway$50(Utilities.Callback2 callback2, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        Boolean bool;
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            if (callback2 == null) {
                return;
            } else {
                bool = Boolean.TRUE;
            }
        } else if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PENDING || callback2 == null) {
            return;
        } else {
            bool = Boolean.FALSE;
        }
        callback2.run(bool, null);
    }

    public void lambda$buyGiveaway$51(TLRPC.TL_error tL_error, final Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars) {
        if (tL_error != null) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, tL_error.text);
                return;
            }
            return;
        }
        PaymentFormActivity paymentFormActivity = null;
        if (tLObject instanceof TLRPC.PaymentForm) {
            TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
            paymentForm.invoice.recurring = true;
            MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
            paymentFormActivity = new PaymentFormActivity(paymentForm, tL_inputInvoiceStars, (BaseFragment) null);
        } else if (tLObject instanceof TLRPC.PaymentReceipt) {
            paymentFormActivity = new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject);
        }
        if (paymentFormActivity == null) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, "UNKNOWN_RESPONSE");
                return;
            }
            return;
        }
        paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
            @Override
            public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                StarsController.lambda$buyGiveaway$50(Utilities.Callback2.this, invoiceStatus);
            }
        });
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null) {
            return;
        }
        if (!AndroidUtilities.hasDialogOnTop(lastFragment)) {
            lastFragment.presentFragment(paymentFormActivity);
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(paymentFormActivity, bottomSheetParams);
    }

    public void lambda$buyGiveaway$52(final Utilities.Callback2 callback2, final TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyGiveaway$51(tL_error, callback2, tLObject, tL_inputInvoiceStars);
            }
        });
    }

    public static void lambda$buyGiveaway$53(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, "PRODUCT_NOT_FOUND");
    }

    public static void lambda$buyGiveaway$54(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, "PRODUCT_NO_ONETIME_OFFER_DETAILS");
    }

    public static void lambda$buyGiveaway$55(Utilities.Callback2 callback2, boolean z, String str) {
        callback2.run(Boolean.valueOf(z), str);
    }

    public static void lambda$buyGiveaway$56(BillingResult billingResult, final Utilities.Callback2 callback2, BillingResult billingResult2) {
        final boolean z = billingResult.getResponseCode() == 0;
        final String responseCodeString = z ? null : BillingController.getResponseCodeString(billingResult.getResponseCode());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyGiveaway$55(Utilities.Callback2.this, z, responseCodeString);
            }
        });
    }

    public static void lambda$buyGiveaway$57(Utilities.Callback2 callback2) {
        callback2.run(Boolean.FALSE, null);
    }

    public static void lambda$buyGiveaway$58(final Utilities.Callback2 callback2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyGiveaway$57(Utilities.Callback2.this);
            }
        });
    }

    public static void lambda$buyGiveaway$59(TLObject tLObject, ProductDetails productDetails, final BillingResult billingResult, final Utilities.Callback2 callback2, Activity activity, TLRPC.TL_inputStorePaymentStarsGiveaway tL_inputStorePaymentStarsGiveaway, List list, TLRPC.TL_error tL_error) {
        Boolean bool;
        String str;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            BillingController.getInstance().addResultListener(productDetails.getProductId(), new Consumer() {
                @Override
                public final void accept(Object obj) {
                    StarsController.lambda$buyGiveaway$56(BillingResult.this, callback2, (BillingResult) obj);
                }
            });
            BillingController.getInstance().setOnCanceled(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyGiveaway$58(Utilities.Callback2.this);
                }
            });
            BillingController.getInstance().launchBillingFlow(activity, AccountInstance.getInstance(UserConfig.selectedAccount), tL_inputStorePaymentStarsGiveaway, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails((ProductDetails) list.get(0)).build()));
            return;
        }
        if (tLObject instanceof TLRPC.TL_boolFalse) {
            if (callback2 == null) {
                return;
            }
            bool = Boolean.FALSE;
            str = "PURCHASE_FORBIDDEN";
        } else {
            if (callback2 == null) {
                return;
            }
            bool = Boolean.FALSE;
            str = tL_error != null ? tL_error.text : "SERVER_ERROR";
        }
        callback2.run(bool, str);
    }

    public static void lambda$buyGiveaway$60(final ProductDetails productDetails, final BillingResult billingResult, final Utilities.Callback2 callback2, final Activity activity, final TLRPC.TL_inputStorePaymentStarsGiveaway tL_inputStorePaymentStarsGiveaway, final List list, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyGiveaway$59(TLObject.this, productDetails, billingResult, callback2, activity, tL_inputStorePaymentStarsGiveaway, list, tL_error);
            }
        });
    }

    public void lambda$buyGiveaway$61(final List list, final Utilities.Callback2 callback2, final TLRPC.TL_inputStorePaymentStarsGiveaway tL_inputStorePaymentStarsGiveaway, final BillingResult billingResult, final Activity activity) {
        if (list.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyGiveaway$53(Utilities.Callback2.this);
                }
            });
            return;
        }
        final ProductDetails productDetails = (ProductDetails) list.get(0);
        if (productDetails.getOneTimePurchaseOfferDetails() == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyGiveaway$54(Utilities.Callback2.this);
                }
            });
            return;
        }
        TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium = new TLRPC.TL_payments_canPurchasePremium();
        tL_payments_canPurchasePremium.purpose = tL_inputStorePaymentStarsGiveaway;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_canPurchasePremium, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.lambda$buyGiveaway$60(ProductDetails.this, billingResult, callback2, activity, tL_inputStorePaymentStarsGiveaway, list, tLObject, tL_error);
            }
        });
    }

    public void lambda$buyGiveaway$62(final Utilities.Callback2 callback2, final TLRPC.TL_inputStorePaymentStarsGiveaway tL_inputStorePaymentStarsGiveaway, final Activity activity, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyGiveaway$61(list, callback2, tL_inputStorePaymentStarsGiveaway, billingResult, activity);
            }
        });
    }

    public void lambda$buyStarGift$115(Utilities.Callback2 callback2, TL_stars.StarGift starGift, boolean z, boolean z2, long j, TLRPC.TL_textWithEntities tL_textWithEntities) {
        if (balanceAvailable()) {
            buyStarGift(starGift, z, z2, j, tL_textWithEntities, callback2);
            return;
        }
        bulletinError("NO_BALANCE");
        if (callback2 != null) {
            callback2.run(Boolean.FALSE, null);
        }
    }

    public void lambda$buyStarGift$116(boolean[] zArr, TL_stars.StarGift starGift, boolean z, boolean z2, long j, TLRPC.TL_textWithEntities tL_textWithEntities, Utilities.Callback2 callback2) {
        zArr[0] = true;
        buyStarGift(starGift, z, z2, j, tL_textWithEntities, callback2);
    }

    public static void lambda$buyStarGift$117(Utilities.Callback2 callback2, boolean[] zArr, DialogInterface dialogInterface) {
        if (callback2 == null || zArr[0]) {
            return;
        }
        callback2.run(Boolean.FALSE, null);
    }

    public void lambda$buyStarGift$118(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public static void lambda$buyStarGift$119(ProfileActivity profileActivity) {
        SharedMediaLayout sharedMediaLayout = profileActivity.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.scrollToPage(14);
            profileActivity.scrollToSharedMedia();
        }
    }

    public static void lambda$buyStarGift$120(final ProfileActivity profileActivity, TL_stars.StarGift starGift, long j, String str) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$buyStarGift$119(ProfileActivity.this);
            }
        }, 200L);
        BulletinFactory.of(profileActivity).createEmojiBulletin(starGift.sticker, LocaleController.getString(R.string.StarsGiftCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsGiftCompletedChannelText", (int) j, str))).show(false);
    }

    public static void lambda$buyStarGift$121(ChatActivity chatActivity, TL_stars.StarGift starGift, long j) {
        BulletinFactory.of(chatActivity).createEmojiBulletin(starGift.sticker, LocaleController.getString(R.string.StarsGiftCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsGiftCompletedText", (int) j, new Object[0]))).show(true);
    }

    public void lambda$buyStarGift$122(TLObject tLObject, TLRPC.TL_error tL_error, final Utilities.Callback2 callback2, Context context, Theme.ResourcesProvider resourcesProvider, final long j, final String str, final TL_stars.StarGift starGift, final boolean z, final boolean z2, final long j2, final TLRPC.TL_textWithEntities tL_textWithEntities) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        BulletinFactory global = (lastFragment == null || lastFragment.visibleDialog != null) ? BulletinFactory.global() : BulletinFactory.of(lastFragment);
        if (!(tLObject instanceof TLRPC.TL_payments_paymentResult)) {
            if (tL_error == null || !"BALANCE_TOO_LOW".equals(tL_error.text)) {
                if (tL_error == null || !"STARGIFT_USAGE_LIMITED".equals(tL_error.text)) {
                    if (callback2 != null) {
                        callback2.run(Boolean.FALSE, null);
                    }
                    global.createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, tL_error != null ? tL_error.text : "FAILED_SEND_STARS")).show();
                    return;
                } else {
                    if (callback2 != null) {
                        callback2.run(Boolean.FALSE, "STARGIFT_USAGE_LIMITED");
                        return;
                    }
                    return;
                }
            }
            if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                if (callback2 != null) {
                    callback2.run(Boolean.FALSE, null);
                }
                showNoSupportDialog(context, resourcesProvider);
                return;
            } else {
                final boolean[] zArr = {false};
                StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(context, resourcesProvider, j, 6, str, new Runnable() {
                    @Override
                    public final void run() {
                        StarsController.this.lambda$buyStarGift$116(zArr, starGift, z, z2, j2, tL_textWithEntities, callback2);
                    }
                });
                starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        StarsController.lambda$buyStarGift$117(Utilities.Callback2.this, zArr, dialogInterface);
                    }
                });
                starsNeededSheet.show();
                return;
            }
        }
        final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyStarGift$118(tL_payments_paymentResult);
            }
        });
        invalidateStarGifts();
        invalidateProfileGifts(j2);
        invalidateTransactions(true);
        if (callback2 != null) {
            callback2.run(Boolean.TRUE, null);
        }
        if (BirthdayController.getInstance(this.currentAccount).contains(j2)) {
            MessagesController.getInstance(this.currentAccount).getMainSettings().edit().putBoolean(Calendar.getInstance().get(1) + "bdayhint_" + j2, false).apply();
        }
        if (j2 < 0) {
            if (lastFragment instanceof ProfileActivity) {
                ProfileActivity profileActivity = (ProfileActivity) lastFragment;
                if (profileActivity.getDialogId() == j2) {
                    SharedMediaLayout sharedMediaLayout = profileActivity.sharedMediaLayout;
                    if (sharedMediaLayout != null) {
                        sharedMediaLayout.updateTabs(true);
                        profileActivity.sharedMediaLayout.scrollToPage(14);
                        profileActivity.scrollToSharedMedia();
                    }
                    BulletinFactory.of(lastFragment).createEmojiBulletin(starGift.sticker, LocaleController.getString(R.string.StarsGiftCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsGiftCompletedChannelText", (int) j, str))).show(false);
                }
            }
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", -j2);
            bundle.putBoolean("open_gifts", true);
            final ProfileActivity profileActivity2 = new ProfileActivity(bundle);
            profileActivity2.whenFullyVisible(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyStarGift$120(ProfileActivity.this, starGift, j, str);
                }
            });
            lastFragment.presentFragment(profileActivity2);
        } else if ((lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).getDialogId() == j2) {
            BulletinFactory.of(lastFragment).createEmojiBulletin(starGift.sticker, LocaleController.getString(R.string.StarsGiftCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsGiftCompletedText", (int) j, new Object[0]))).show(true);
        } else {
            final ChatActivity of = ChatActivity.of(j2);
            of.whenFullyVisible(new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$buyStarGift$121(ChatActivity.this, starGift, j);
                }
            });
            lastFragment.presentFragment(of);
        }
        MessagesController.getInstance(this.currentAccount).getMainSettings().edit().putBoolean("show_gift_for_" + j2, true).putBoolean(Calendar.getInstance().get(1) + "show_gift_for_" + j2, true).apply();
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity == null || launchActivity.getFireworksOverlay() == null) {
            return;
        }
        LaunchActivity.instance.getFireworksOverlay().start(true);
    }

    public void lambda$buyStarGift$123(final Utilities.Callback2 callback2, final Context context, final Theme.ResourcesProvider resourcesProvider, final long j, final String str, final TL_stars.StarGift starGift, final boolean z, final boolean z2, final long j2, final TLRPC.TL_textWithEntities tL_textWithEntities, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyStarGift$122(tLObject, tL_error, callback2, context, resourcesProvider, j, str, starGift, z, z2, j2, tL_textWithEntities);
            }
        });
    }

    public void lambda$buyStarGift$124(TLObject tLObject, TLRPC.TL_error tL_error, final Utilities.Callback2 callback2, TLRPC.TL_inputInvoiceStarGift tL_inputInvoiceStarGift, final Context context, final Theme.ResourcesProvider resourcesProvider, final String str, final TL_stars.StarGift starGift, final boolean z, final boolean z2, final long j, final TLRPC.TL_textWithEntities tL_textWithEntities) {
        if (!(tLObject instanceof TLRPC.TL_payments_paymentFormStarGift)) {
            bulletinError(tL_error, "NO_PAYMENT_FORM");
            callback2.run(Boolean.FALSE, null);
            return;
        }
        TLRPC.TL_payments_paymentFormStarGift tL_payments_paymentFormStarGift = (TLRPC.TL_payments_paymentFormStarGift) tLObject;
        TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
        tL_payments_sendStarsForm.form_id = tL_payments_paymentFormStarGift.form_id;
        tL_payments_sendStarsForm.invoice = tL_inputInvoiceStarGift;
        Iterator<TLRPC.TL_labeledPrice> it = tL_payments_paymentFormStarGift.invoice.prices.iterator();
        final long j2 = 0;
        while (it.hasNext()) {
            j2 += it.next().amount;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                StarsController.this.lambda$buyStarGift$123(callback2, context, resourcesProvider, j2, str, starGift, z, z2, j, tL_textWithEntities, tLObject2, tL_error2);
            }
        });
    }

    public void lambda$buyStarGift$125(final Utilities.Callback2 callback2, final TLRPC.TL_inputInvoiceStarGift tL_inputInvoiceStarGift, final Context context, final Theme.ResourcesProvider resourcesProvider, final String str, final TL_stars.StarGift starGift, final boolean z, final boolean z2, final long j, final TLRPC.TL_textWithEntities tL_textWithEntities, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$buyStarGift$124(tLObject, tL_error, callback2, tL_inputInvoiceStarGift, context, resourcesProvider, str, starGift, z, z2, j, tL_textWithEntities);
            }
        });
    }

    public void lambda$getBalance$0(TLObject tLObject, Runnable runnable) {
        boolean z;
        boolean z2;
        boolean z3 = !this.balanceLoaded;
        this.lastBalanceLoaded = System.currentTimeMillis();
        if (tLObject instanceof TL_stars.StarsStatus) {
            TL_stars.StarsStatus starsStatus = (TL_stars.StarsStatus) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(starsStatus.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(starsStatus.chats, false);
            if (this.transactions[0].isEmpty()) {
                Iterator<TL_stars.StarsTransaction> it = starsStatus.history.iterator();
                while (it.hasNext()) {
                    TL_stars.StarsTransaction next = it.next();
                    this.transactions[0].add(next);
                    this.transactions[next.stars.amount > 0 ? (char) 1 : (char) 2].add(next);
                }
                for (int i = 0; i < 3; i++) {
                    this.transactionsExist[i] = !this.transactions[i].isEmpty() || this.transactionsExist[i];
                    boolean[] zArr = this.endReached;
                    boolean z4 = (starsStatus.flags & 1) == 0;
                    zArr[i] = z4;
                    if (z4) {
                        this.loading[i] = false;
                    }
                    this.offset[i] = zArr[i] ? null : starsStatus.next_offset;
                }
                z = true;
            } else {
                z = false;
            }
            if (this.subscriptions.isEmpty()) {
                this.subscriptions.addAll(starsStatus.subscriptions);
                this.subscriptionsLoading = false;
                this.subscriptionsOffset = starsStatus.subscriptions_next_offset;
                this.subscriptionsEndReached = (starsStatus.flags & 4) == 0;
                z2 = true;
            } else {
                z2 = false;
            }
            long j = this.balance.amount;
            TL_stars.StarsAmount starsAmount = starsStatus.balance;
            if (j != starsAmount.amount) {
                z3 = true;
            }
            this.balance = starsAmount;
            this.minus = 0L;
        } else {
            z = false;
            z2 = false;
        }
        this.balanceLoading = false;
        this.balanceLoaded = true;
        if (z3) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
        }
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starTransactionsLoaded, new Object[0]);
        }
        if (z2) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starSubscriptionsLoaded, new Object[0]);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$getBalance$1(final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getBalance$0(tLObject, runnable);
            }
        });
    }

    public void lambda$getGiftOptions$10(TLObject tLObject) {
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        if (tLObject instanceof Vector) {
            Iterator it = ((Vector) tLObject).objects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof TL_stars.TL_starsGiftOption) {
                    TL_stars.TL_starsGiftOption tL_starsGiftOption = (TL_stars.TL_starsGiftOption) next;
                    arrayList.add(tL_starsGiftOption);
                    if (tL_starsGiftOption.store_product != null && !BuildVars.useInvoiceBilling()) {
                        arrayList2.add(tL_starsGiftOption);
                        tL_starsGiftOption.loadingStorePrice = true;
                    }
                }
            }
            this.giftOptionsLoaded = true;
        }
        this.giftOptions = arrayList;
        this.giftOptionsLoading = false;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiftOptionsLoaded, new Object[0]);
        if (arrayList2.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getGiftOptions$9(arrayList2);
            }
        };
        if (BillingController.getInstance().isReady()) {
            runnable.run();
        } else {
            BillingController.getInstance().whenSetuped(runnable);
        }
    }

    public void lambda$getGiftOptions$11(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getGiftOptions$10(tLObject);
            }
        });
    }

    public void lambda$getGiftOptions$7(BillingResult billingResult, List list, ArrayList arrayList) {
        TL_stars.TL_starsGiftOption tL_starsGiftOption;
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails;
        if (billingResult.getResponseCode() != 0) {
            bulletinError("BILLING_" + BillingController.getResponseCodeString(billingResult.getResponseCode()));
            return;
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ProductDetails productDetails = (ProductDetails) list.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        tL_starsGiftOption = null;
                        break;
                    } else {
                        if (((TL_stars.TL_starsGiftOption) arrayList.get(i2)).store_product.equals(productDetails.getProductId())) {
                            tL_starsGiftOption = (TL_stars.TL_starsGiftOption) arrayList.get(i2);
                            break;
                        }
                        i2++;
                    }
                }
                if (tL_starsGiftOption != null && (oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails()) != null) {
                    tL_starsGiftOption.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
                    double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
                    double pow = Math.pow(10.0d, 6.0d);
                    Double.isNaN(priceAmountMicros);
                    tL_starsGiftOption.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_starsGiftOption.currency)));
                    tL_starsGiftOption.loadingStorePrice = false;
                }
            }
        }
        if (this.giftOptions != null) {
            for (int i3 = 0; i3 < this.giftOptions.size(); i3++) {
                TL_stars.TL_starsGiftOption tL_starsGiftOption2 = (TL_stars.TL_starsGiftOption) this.giftOptions.get(i3);
                if (tL_starsGiftOption2 != null && tL_starsGiftOption2.loadingStorePrice) {
                    tL_starsGiftOption2.missingStorePrice = true;
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiftOptionsLoaded, new Object[0]);
    }

    public void lambda$getGiftOptions$8(final ArrayList arrayList, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getGiftOptions$7(billingResult, list, arrayList);
            }
        });
    }

    public void lambda$getGiftOptions$9(final ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList2.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(((TL_stars.TL_starsGiftOption) arrayList.get(i)).store_product).build());
        }
        BillingController.getInstance().queryProductDetails(arrayList2, new ProductDetailsResponseListener() {
            @Override
            public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                StarsController.this.lambda$getGiftOptions$8(arrayList, billingResult, list);
            }
        });
    }

    public void lambda$getGiveawayOptions$12(BillingResult billingResult, List list, ArrayList arrayList) {
        TL_stars.TL_starsGiveawayOption tL_starsGiveawayOption;
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails;
        if (billingResult.getResponseCode() != 0) {
            bulletinError("BILLING_" + BillingController.getResponseCodeString(billingResult.getResponseCode()));
            return;
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ProductDetails productDetails = (ProductDetails) list.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        tL_starsGiveawayOption = null;
                        break;
                    } else {
                        if (((TL_stars.TL_starsGiveawayOption) arrayList.get(i2)).store_product.equals(productDetails.getProductId())) {
                            tL_starsGiveawayOption = (TL_stars.TL_starsGiveawayOption) arrayList.get(i2);
                            break;
                        }
                        i2++;
                    }
                }
                if (tL_starsGiveawayOption != null && (oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails()) != null) {
                    tL_starsGiveawayOption.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
                    double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
                    double pow = Math.pow(10.0d, 6.0d);
                    Double.isNaN(priceAmountMicros);
                    tL_starsGiveawayOption.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_starsGiveawayOption.currency)));
                    tL_starsGiveawayOption.loadingStorePrice = false;
                }
            }
        }
        if (this.giveawayOptions != null) {
            for (int i3 = 0; i3 < this.giveawayOptions.size(); i3++) {
                TL_stars.TL_starsGiveawayOption tL_starsGiveawayOption2 = (TL_stars.TL_starsGiveawayOption) this.giveawayOptions.get(i3);
                if (tL_starsGiveawayOption2 != null && tL_starsGiveawayOption2.loadingStorePrice) {
                    tL_starsGiveawayOption2.missingStorePrice = true;
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiveawayOptionsLoaded, new Object[0]);
    }

    public void lambda$getGiveawayOptions$13(final ArrayList arrayList, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getGiveawayOptions$12(billingResult, list, arrayList);
            }
        });
    }

    public void lambda$getGiveawayOptions$14(final ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList2.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(((TL_stars.TL_starsGiveawayOption) arrayList.get(i)).store_product).build());
        }
        BillingController.getInstance().queryProductDetails(arrayList2, new ProductDetailsResponseListener() {
            @Override
            public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                StarsController.this.lambda$getGiveawayOptions$13(arrayList, billingResult, list);
            }
        });
    }

    public void lambda$getGiveawayOptions$15(TLObject tLObject) {
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        if (tLObject instanceof Vector) {
            Iterator it = ((Vector) tLObject).objects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof TL_stars.TL_starsGiveawayOption) {
                    TL_stars.TL_starsGiveawayOption tL_starsGiveawayOption = (TL_stars.TL_starsGiveawayOption) next;
                    arrayList.add(tL_starsGiveawayOption);
                    if (tL_starsGiveawayOption.store_product != null && !BuildVars.useInvoiceBilling()) {
                        arrayList2.add(tL_starsGiveawayOption);
                        tL_starsGiveawayOption.loadingStorePrice = true;
                    }
                }
            }
            this.giveawayOptionsLoaded = true;
        }
        this.giveawayOptions = arrayList;
        this.giveawayOptionsLoading = false;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiveawayOptionsLoaded, new Object[0]);
        if (arrayList2.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getGiveawayOptions$14(arrayList2);
            }
        };
        if (BillingController.getInstance().isReady()) {
            runnable.run();
        } else {
            BillingController.getInstance().whenSetuped(runnable);
        }
    }

    public void lambda$getGiveawayOptions$16(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getGiveawayOptions$15(tLObject);
            }
        });
    }

    public void lambda$getOptions$2(BillingResult billingResult, List list, ArrayList arrayList) {
        TL_stars.TL_starsTopupOption tL_starsTopupOption;
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails;
        if (billingResult.getResponseCode() != 0) {
            bulletinError("BILLING_" + BillingController.getResponseCodeString(billingResult.getResponseCode()));
            return;
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ProductDetails productDetails = (ProductDetails) list.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        tL_starsTopupOption = null;
                        break;
                    } else {
                        if (((TL_stars.TL_starsTopupOption) arrayList.get(i2)).store_product.equals(productDetails.getProductId())) {
                            tL_starsTopupOption = (TL_stars.TL_starsTopupOption) arrayList.get(i2);
                            break;
                        }
                        i2++;
                    }
                }
                if (tL_starsTopupOption != null && (oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails()) != null) {
                    tL_starsTopupOption.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
                    double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
                    double pow = Math.pow(10.0d, 6.0d);
                    Double.isNaN(priceAmountMicros);
                    tL_starsTopupOption.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_starsTopupOption.currency)));
                    tL_starsTopupOption.loadingStorePrice = false;
                }
            }
        }
        if (this.options != null) {
            for (int i3 = 0; i3 < this.options.size(); i3++) {
                TL_stars.TL_starsTopupOption tL_starsTopupOption2 = (TL_stars.TL_starsTopupOption) this.options.get(i3);
                if (tL_starsTopupOption2 != null && tL_starsTopupOption2.loadingStorePrice) {
                    tL_starsTopupOption2.missingStorePrice = true;
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starOptionsLoaded, new Object[0]);
    }

    public void lambda$getOptions$3(final ArrayList arrayList, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getOptions$2(billingResult, list, arrayList);
            }
        });
    }

    public void lambda$getOptions$4(final ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList2.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(((TL_stars.TL_starsTopupOption) arrayList.get(i)).store_product).build());
        }
        BillingController.getInstance().queryProductDetails(arrayList2, new ProductDetailsResponseListener() {
            @Override
            public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                StarsController.this.lambda$getOptions$3(arrayList, billingResult, list);
            }
        });
    }

    public void lambda$getOptions$5(TLObject tLObject) {
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        if (tLObject instanceof Vector) {
            Iterator it = ((Vector) tLObject).objects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof TL_stars.TL_starsTopupOption) {
                    TL_stars.TL_starsTopupOption tL_starsTopupOption = (TL_stars.TL_starsTopupOption) next;
                    arrayList.add(tL_starsTopupOption);
                    if (tL_starsTopupOption.store_product != null && !BuildVars.useInvoiceBilling()) {
                        arrayList2.add(tL_starsTopupOption);
                        tL_starsTopupOption.loadingStorePrice = true;
                    }
                }
            }
            this.optionsLoaded = true;
        }
        this.options = arrayList;
        this.optionsLoading = false;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starOptionsLoaded, new Object[0]);
        if (arrayList2.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getOptions$4(arrayList2);
            }
        };
        if (BillingController.getInstance().isReady()) {
            runnable.run();
        } else {
            BillingController.getInstance().whenSetuped(runnable);
        }
    }

    public void lambda$getOptions$6(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getOptions$5(tLObject);
            }
        });
    }

    public void lambda$getStarGift$113(boolean[] zArr, long j, NotificationCenter.NotificationCenterDelegate[] notificationCenterDelegateArr, Utilities.Callback callback, int i, int i2, Object[] objArr) {
        int i3;
        TL_stars.StarGift starGift;
        if (zArr[0] || i != (i3 = NotificationCenter.starGiftsLoaded) || (starGift = getStarGift(j)) == null) {
            return;
        }
        zArr[0] = true;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegateArr[0], i3);
        callback.run(starGift);
    }

    public void lambda$getStarGift$114(boolean[] zArr, NotificationCenter.NotificationCenterDelegate[] notificationCenterDelegateArr) {
        zArr[0] = true;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegateArr[0], NotificationCenter.starGiftsLoaded);
    }

    public void lambda$getStarGiftPreview$126(TLObject tLObject, long j, Utilities.Callback callback) {
        TL_stars.starGiftUpgradePreview stargiftupgradepreview;
        if (tLObject instanceof TL_stars.starGiftUpgradePreview) {
            stargiftupgradepreview = (TL_stars.starGiftUpgradePreview) tLObject;
            this.giftPreviews.put(Long.valueOf(j), stargiftupgradepreview);
        } else {
            stargiftupgradepreview = null;
        }
        callback.run(stargiftupgradepreview);
    }

    public void lambda$getStarGiftPreview$127(final long j, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getStarGiftPreview$126(tLObject, j, callback);
            }
        });
    }

    public static void lambda$getStarGiftsCached$108(Utilities.Callback3 callback3, ArrayList arrayList, int i, long j) {
        callback3.run(arrayList, Integer.valueOf(i), Long.valueOf(j));
    }

    public static void lambda$getStarGiftsCached$109(org.telegram.messenger.MessagesStorage r10, final java.util.ArrayList r11, final org.telegram.messenger.Utilities.Callback3 r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsController.lambda$getStarGiftsCached$109(org.telegram.messenger.MessagesStorage, java.util.ArrayList, org.telegram.messenger.Utilities$Callback3):void");
    }

    public static void lambda$getStarGiftsRemote$111(TLObject tLObject, Utilities.Callback callback) {
        callback.run(tLObject instanceof TL_stars.StarGifts ? (TL_stars.StarGifts) tLObject : null);
    }

    public static void lambda$getStarGiftsRemote$112(final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$getStarGiftsRemote$111(TLObject.this, callback);
            }
        });
    }

    public void lambda$getUserStarGift$128(AlertDialog alertDialog, TLObject tLObject, TL_stars.InputSavedStarGift inputSavedStarGift, Utilities.Callback callback) {
        TL_stars.SavedStarGift savedStarGift;
        alertDialog.dismiss();
        if (tLObject instanceof TL_stars.TL_payments_savedStarGifts) {
            TL_stars.TL_payments_savedStarGifts tL_payments_savedStarGifts = (TL_stars.TL_payments_savedStarGifts) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_savedStarGifts.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_payments_savedStarGifts.chats, false);
            for (int i = 0; i < tL_payments_savedStarGifts.gifts.size(); i++) {
                savedStarGift = tL_payments_savedStarGifts.gifts.get(i);
                if (((inputSavedStarGift instanceof TL_stars.TL_inputSavedStarGiftUser) && ((TL_stars.TL_inputSavedStarGiftUser) inputSavedStarGift).msg_id == savedStarGift.msg_id) || ((inputSavedStarGift instanceof TL_stars.TL_inputSavedStarGiftChat) && ((TL_stars.TL_inputSavedStarGiftChat) inputSavedStarGift).saved_id == savedStarGift.saved_id)) {
                    break;
                }
            }
        }
        savedStarGift = null;
        callback.run(savedStarGift);
    }

    public void lambda$getUserStarGift$129(final AlertDialog alertDialog, final TL_stars.InputSavedStarGift inputSavedStarGift, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getUserStarGift$128(alertDialog, tLObject, inputSavedStarGift, callback);
            }
        });
    }

    public void lambda$loadInsufficientSubscriptions$21(TLObject tLObject) {
        this.insufficientSubscriptionsLoading = false;
        if (tLObject instanceof TL_stars.StarsStatus) {
            TL_stars.StarsStatus starsStatus = (TL_stars.StarsStatus) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(starsStatus.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(starsStatus.chats, false);
            this.insufficientSubscriptions.addAll(starsStatus.subscriptions);
            updateBalance(starsStatus.balance);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starSubscriptionsLoaded, new Object[0]);
        }
    }

    public void lambda$loadInsufficientSubscriptions$22(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$loadInsufficientSubscriptions$21(tLObject);
            }
        });
    }

    public static int lambda$loadStarGifts$100(TL_stars.StarGift starGift) {
        return starGift.birthday ? -1 : 0;
    }

    public static int lambda$loadStarGifts$101(TL_stars.StarGift starGift) {
        return starGift.sold_out ? 1 : 0;
    }

    public static int lambda$loadStarGifts$102(TL_stars.StarGift starGift) {
        return starGift.sold_out ? 1 : 0;
    }

    public void lambda$loadStarGifts$103(ArrayList arrayList, Integer num, Long l) {
        this.giftsCacheLoaded = true;
        this.gifts.clear();
        this.gifts.addAll(arrayList);
        this.birthdaySortedGifts.clear();
        this.birthdaySortedGifts.addAll(this.gifts);
        Collections.sort(this.birthdaySortedGifts, Comparator$EL.thenComparingInt(Comparator$CC.comparingInt(new ToIntFunction() {
            @Override
            public final int applyAsInt(Object obj) {
                int lambda$loadStarGifts$100;
                lambda$loadStarGifts$100 = StarsController.lambda$loadStarGifts$100((TL_stars.StarGift) obj);
                return lambda$loadStarGifts$100;
            }
        }), new ToIntFunction() {
            @Override
            public final int applyAsInt(Object obj) {
                int lambda$loadStarGifts$101;
                lambda$loadStarGifts$101 = StarsController.lambda$loadStarGifts$101((TL_stars.StarGift) obj);
                return lambda$loadStarGifts$101;
            }
        }));
        this.sortedGifts.clear();
        this.sortedGifts.addAll(this.gifts);
        Collections.sort(this.sortedGifts, Comparator$CC.comparingInt(new ToIntFunction() {
            @Override
            public final int applyAsInt(Object obj) {
                int lambda$loadStarGifts$102;
                lambda$loadStarGifts$102 = StarsController.lambda$loadStarGifts$102((TL_stars.StarGift) obj);
                return lambda$loadStarGifts$102;
            }
        }));
        this.giftsHash = num.intValue();
        this.giftsRemoteTime = l.longValue();
        this.giftsLoading = false;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiftsLoaded, new Object[0]);
        loadStarGifts();
    }

    public static int lambda$loadStarGifts$104(TL_stars.StarGift starGift) {
        return starGift.birthday ? -1 : 0;
    }

    public static int lambda$loadStarGifts$105(TL_stars.StarGift starGift) {
        return starGift.sold_out ? 1 : 0;
    }

    public static int lambda$loadStarGifts$106(TL_stars.StarGift starGift) {
        return starGift.sold_out ? 1 : 0;
    }

    public void lambda$loadStarGifts$107(TL_stars.StarGifts starGifts) {
        ArrayList<TL_stars.StarGift> arrayList;
        int i;
        long currentTimeMillis;
        this.giftsLoading = false;
        this.giftsLoaded = true;
        if (starGifts instanceof TL_stars.TL_starGifts) {
            TL_stars.TL_starGifts tL_starGifts = (TL_stars.TL_starGifts) starGifts;
            this.gifts.clear();
            this.gifts.addAll(tL_starGifts.gifts);
            this.birthdaySortedGifts.clear();
            this.birthdaySortedGifts.addAll(this.gifts);
            Collections.sort(this.birthdaySortedGifts, Comparator$EL.thenComparingInt(Comparator$CC.comparingInt(new ToIntFunction() {
                @Override
                public final int applyAsInt(Object obj) {
                    int lambda$loadStarGifts$104;
                    lambda$loadStarGifts$104 = StarsController.lambda$loadStarGifts$104((TL_stars.StarGift) obj);
                    return lambda$loadStarGifts$104;
                }
            }), new ToIntFunction() {
                @Override
                public final int applyAsInt(Object obj) {
                    int lambda$loadStarGifts$105;
                    lambda$loadStarGifts$105 = StarsController.lambda$loadStarGifts$105((TL_stars.StarGift) obj);
                    return lambda$loadStarGifts$105;
                }
            }));
            this.sortedGifts.clear();
            this.sortedGifts.addAll(this.gifts);
            Collections.sort(this.sortedGifts, Comparator$CC.comparingInt(new ToIntFunction() {
                @Override
                public final int applyAsInt(Object obj) {
                    int lambda$loadStarGifts$106;
                    lambda$loadStarGifts$106 = StarsController.lambda$loadStarGifts$106((TL_stars.StarGift) obj);
                    return lambda$loadStarGifts$106;
                }
            }));
            this.giftsHash = tL_starGifts.hash;
            this.giftsRemoteTime = System.currentTimeMillis();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiftsLoaded, new Object[0]);
            arrayList = tL_starGifts.gifts;
            i = this.giftsHash;
            currentTimeMillis = this.giftsRemoteTime;
        } else {
            if (!(starGifts instanceof TL_stars.TL_starGiftsNotModified)) {
                return;
            }
            arrayList = this.gifts;
            i = this.giftsHash;
            currentTimeMillis = System.currentTimeMillis();
            this.giftsRemoteTime = currentTimeMillis;
        }
        saveStarGiftsCached(arrayList, i, currentTimeMillis);
    }

    public void lambda$loadSubscriptions$19(TLObject tLObject) {
        this.subscriptionsLoading = false;
        if (tLObject instanceof TL_stars.StarsStatus) {
            TL_stars.StarsStatus starsStatus = (TL_stars.StarsStatus) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(starsStatus.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(starsStatus.chats, false);
            this.subscriptions.addAll(starsStatus.subscriptions);
            this.subscriptionsEndReached = (starsStatus.flags & 4) == 0;
            this.subscriptionsOffset = starsStatus.subscriptions_next_offset;
            updateBalance(starsStatus.balance);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starSubscriptionsLoaded, new Object[0]);
        }
    }

    public void lambda$loadSubscriptions$20(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$loadSubscriptions$19(tLObject);
            }
        });
    }

    public void lambda$loadTransactions$17(int i, TLObject tLObject) {
        this.loading[i] = false;
        if (tLObject instanceof TL_stars.StarsStatus) {
            TL_stars.StarsStatus starsStatus = (TL_stars.StarsStatus) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(starsStatus.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(starsStatus.chats, false);
            this.transactions[i].addAll(starsStatus.history);
            this.transactionsExist[i] = !this.transactions[i].isEmpty() || this.transactionsExist[i];
            boolean[] zArr = this.endReached;
            boolean z = (starsStatus.flags & 1) == 0;
            zArr[i] = z;
            this.offset[i] = z ? null : starsStatus.next_offset;
            updateBalance(starsStatus.balance);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starTransactionsLoaded, new Object[0]);
        }
    }

    public void lambda$loadTransactions$18(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$loadTransactions$17(i, tLObject);
            }
        });
    }

    public void lambda$openPaymentForm$66(Runnable runnable, MessageObject messageObject, TLRPC.InputInvoice inputInvoice, TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, Utilities.Callback callback) {
        if (balanceAvailable()) {
            openPaymentForm(messageObject, inputInvoice, tL_payments_paymentFormStars, runnable, callback);
            return;
        }
        bulletinError("NO_BALANCE");
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$openPaymentForm$67(boolean[] zArr, int i, Utilities.Callback callback, Utilities.Callback callback2, Boolean bool) {
        zArr[0] = true;
        if (i > 0) {
            invalidateSubscriptions(true);
        }
        if (callback != null) {
            callback.run(bool.booleanValue() ? "paid" : "failed");
        }
        if (callback2 != null) {
            callback2.run(Boolean.TRUE);
        }
    }

    public void lambda$openPaymentForm$68(boolean[] zArr, MessageObject messageObject, TLRPC.InputInvoice inputInvoice, TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, final boolean[] zArr2, final int i, final Utilities.Callback callback, final Utilities.Callback callback2) {
        zArr[0] = true;
        payAfterConfirmed(messageObject, inputInvoice, tL_payments_paymentFormStars, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsController.this.lambda$openPaymentForm$67(zArr2, i, callback, callback2, (Boolean) obj);
            }
        });
    }

    public void lambda$openPaymentForm$69(Utilities.Callback callback, boolean[] zArr, boolean[] zArr2, Utilities.Callback callback2, DialogInterface dialogInterface) {
        if (callback == null || zArr[0]) {
            return;
        }
        callback.run(Boolean.FALSE);
        this.paymentFormOpened = false;
        if (zArr2[0] || callback2 == null) {
            return;
        }
        callback2.run("cancelled");
        zArr2[0] = true;
    }

    public void lambda$openPaymentForm$70(int i, Utilities.Callback callback, boolean[] zArr, Utilities.Callback callback2, Boolean bool) {
        if (i > 0) {
            invalidateSubscriptions(true);
        }
        if (callback != null) {
            callback.run(Boolean.TRUE);
        }
        zArr[0] = true;
        if (callback2 != null) {
            callback2.run(bool.booleanValue() ? "paid" : "failed");
        }
    }

    public void lambda$openPaymentForm$71(long j, final boolean[] zArr, final Utilities.Callback callback, Context context, Theme.ResourcesProvider resourcesProvider, boolean z, String str, final MessageObject messageObject, final TLRPC.InputInvoice inputInvoice, final TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, final int i, final Utilities.Callback callback2) {
        if (this.balance.amount >= j) {
            payAfterConfirmed(messageObject, inputInvoice, tL_payments_paymentFormStars, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    StarsController.this.lambda$openPaymentForm$70(i, callback2, zArr, callback, (Boolean) obj);
                }
            });
            return;
        }
        if (MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
            final boolean[] zArr2 = {false};
            StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(context, resourcesProvider, j, z ? 9 : 0, str, new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$openPaymentForm$68(zArr2, messageObject, inputInvoice, tL_payments_paymentFormStars, zArr, i, callback, callback2);
                }
            });
            starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    StarsController.this.lambda$openPaymentForm$69(callback2, zArr2, zArr, callback, dialogInterface);
                }
            });
            starsNeededSheet.show();
            return;
        }
        this.paymentFormOpened = false;
        if (callback2 != null) {
            callback2.run(Boolean.FALSE);
        }
        if (!zArr[0] && callback != null) {
            callback.run("cancelled");
            zArr[0] = true;
        }
        showNoSupportDialog(context, resourcesProvider);
    }

    public void lambda$openPaymentForm$72(boolean[] zArr, Utilities.Callback callback) {
        this.paymentFormOpened = false;
        if (zArr[0] || callback == null) {
            return;
        }
        callback.run("cancelled");
        zArr[0] = true;
    }

    public void lambda$pay$63(TLObject tLObject, MessageObject messageObject, TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage, Runnable runnable, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_payments_paymentFormStars) {
            openPaymentForm(messageObject, tL_inputInvoiceMessage, (TLRPC.TL_payments_paymentFormStars) tLObject, runnable, null);
        } else {
            bulletinError(tL_error, "NO_PAYMENT_FORM");
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$pay$64(final MessageObject messageObject, final TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage, final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$pay$63(tLObject, messageObject, tL_inputInvoiceMessage, runnable, tL_error);
            }
        });
    }

    public void lambda$pay$65(int i) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void lambda$payAfterConfirmed$79(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public static void lambda$payAfterConfirmed$80(Utilities.Callback callback, Boolean bool) {
        if (callback != null) {
            callback.run(bool);
        }
    }

    public void lambda$payAfterConfirmed$81(boolean[] zArr, MessageObject messageObject, TLRPC.InputInvoice inputInvoice, TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, final Utilities.Callback callback) {
        zArr[0] = true;
        payAfterConfirmed(messageObject, inputInvoice, tL_payments_paymentFormStars, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsController.lambda$payAfterConfirmed$80(Utilities.Callback.this, (Boolean) obj);
            }
        });
    }

    public static void lambda$payAfterConfirmed$82(Utilities.Callback callback, boolean[] zArr, DialogInterface dialogInterface) {
        if (callback == null || zArr[0]) {
            return;
        }
        callback.run(Boolean.FALSE);
    }

    public void lambda$payAfterConfirmed$83(TLObject tLObject, MessageObject messageObject, TLRPC.InputInvoice inputInvoice, Utilities.Callback callback, BulletinFactory bulletinFactory, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_payments_paymentFormStars) {
            payAfterConfirmed(messageObject, inputInvoice, (TLRPC.TL_payments_paymentFormStars) tLObject, callback);
            return;
        }
        if (callback != null) {
            callback.run(Boolean.FALSE);
        }
        bulletinFactory.createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, tL_error != null ? tL_error.text : "FAILED_GETTING_FORM")).show();
    }

    public void lambda$payAfterConfirmed$84(final MessageObject messageObject, final TLRPC.InputInvoice inputInvoice, final Utilities.Callback callback, final BulletinFactory bulletinFactory, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$payAfterConfirmed$83(tLObject, messageObject, inputInvoice, callback, bulletinFactory, tL_error);
            }
        });
    }

    public void lambda$payAfterConfirmed$85(TLObject tLObject, final Utilities.Callback callback, final MessageObject messageObject, Context context, long j, String str, int i, String str2, final TLRPC.InputInvoice inputInvoice, long j2, TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider, final TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars) {
        TLRPC.TL_messages_getExtendedMedia tL_messages_getExtendedMedia;
        String string;
        String formatPluralString;
        Bulletin createSimpleBulletin;
        TLRPC.Message message;
        this.paymentFormOpened = false;
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        BulletinFactory global = (lastFragment == null || lastFragment.visibleDialog != null) ? BulletinFactory.global() : BulletinFactory.of(lastFragment);
        if (tLObject instanceof TLRPC.TL_payments_paymentResult) {
            if (callback != null) {
                callback.run(Boolean.TRUE);
            }
            final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
            Utilities.stageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$payAfterConfirmed$79(tL_payments_paymentResult);
                }
            });
            if (messageObject == null || (message = messageObject.messageOwner) == null || !(message.media instanceof TLRPC.TL_messageMediaPaidMedia)) {
                int i2 = R.raw.stars_send;
                if (i > 0) {
                    string = LocaleController.getString(R.string.StarsBotSubscriptionCompleted);
                    formatPluralString = LocaleController.formatPluralString("StarsBotSubscriptionCompletedInfo", (int) j, str2, str);
                } else {
                    string = LocaleController.getString(R.string.StarsPurchaseCompleted);
                    formatPluralString = LocaleController.formatPluralString("StarsPurchaseCompletedInfo", (int) j, str2, str);
                }
                createSimpleBulletin = global.createSimpleBulletin(i2, string, AndroidUtilities.replaceTags(formatPluralString));
            } else {
                createSimpleBulletin = global.createSimpleBulletin(context.getResources().getDrawable(R.drawable.star_small_inner).mutate(), LocaleController.getString(R.string.StarsMediaPurchaseCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsMediaPurchaseCompletedInfo", (int) j, str)));
            }
            createSimpleBulletin.show();
            LaunchActivity launchActivity = LaunchActivity.instance;
            if (launchActivity != null && launchActivity.getFireworksOverlay() != null) {
                LaunchActivity.instance.getFireworksOverlay().start(true);
            }
            if (!(inputInvoice instanceof TLRPC.TL_inputInvoiceStars) || !(((TLRPC.TL_inputInvoiceStars) inputInvoice).purpose instanceof TLRPC.TL_inputStorePaymentStarsGift)) {
                invalidateTransactions(true);
            }
            if (messageObject == null) {
                return;
            } else {
                tL_messages_getExtendedMedia = new TLRPC.TL_messages_getExtendedMedia();
            }
        } else {
            if (tL_error != null && "BALANCE_TOO_LOW".equals(tL_error.text)) {
                if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                    if (callback != null) {
                        callback.run(Boolean.FALSE);
                    }
                    showNoSupportDialog(context, resourcesProvider);
                    return;
                } else {
                    final boolean[] zArr = {false};
                    StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(context, resourcesProvider, j, 0, str, new Runnable() {
                        @Override
                        public final void run() {
                            StarsController.this.lambda$payAfterConfirmed$81(zArr, messageObject, inputInvoice, tL_payments_paymentFormStars, callback);
                        }
                    });
                    starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public final void onDismiss(DialogInterface dialogInterface) {
                            StarsController.lambda$payAfterConfirmed$82(Utilities.Callback.this, zArr, dialogInterface);
                        }
                    });
                    starsNeededSheet.show();
                    return;
                }
            }
            if (tL_error != null && "FORM_EXPIRED".equals(tL_error.text)) {
                TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
                JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(resourcesProvider);
                if (makeThemeParams != null) {
                    TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                    tL_payments_getPaymentForm.theme_params = tL_dataJSON;
                    tL_dataJSON.data = makeThemeParams.toString();
                    tL_payments_getPaymentForm.flags |= 1;
                }
                tL_payments_getPaymentForm.invoice = inputInvoice;
                final BulletinFactory bulletinFactory = global;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                        StarsController.this.lambda$payAfterConfirmed$84(messageObject, inputInvoice, callback, bulletinFactory, tLObject2, tL_error2);
                    }
                });
                return;
            }
            if (callback != null) {
                callback.run(Boolean.FALSE);
            }
            global.createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, tL_error != null ? tL_error.text : "FAILED_SEND_STARS")).show();
            if (messageObject == null) {
                return;
            } else {
                tL_messages_getExtendedMedia = new TLRPC.TL_messages_getExtendedMedia();
            }
        }
        tL_messages_getExtendedMedia.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j2);
        tL_messages_getExtendedMedia.id.add(Integer.valueOf(messageObject.getId()));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getExtendedMedia, null);
    }

    public void lambda$payAfterConfirmed$86(final Utilities.Callback callback, final MessageObject messageObject, final Context context, final long j, final String str, final int i, final String str2, final TLRPC.InputInvoice inputInvoice, final long j2, final Theme.ResourcesProvider resourcesProvider, final TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$payAfterConfirmed$85(tLObject, callback, messageObject, context, j, str, i, str2, inputInvoice, j2, tL_error, resourcesProvider, tL_payments_paymentFormStars);
            }
        });
    }

    public void lambda$payAfterConfirmed$87(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public static void lambda$payAfterConfirmed$88(Utilities.Callback2 callback2, Long l, Boolean bool) {
        if (callback2 != null) {
            callback2.run(l, bool);
        }
    }

    public void lambda$payAfterConfirmed$89(boolean[] zArr, String str, TLRPC.ChatInvite chatInvite, final Utilities.Callback2 callback2) {
        zArr[0] = true;
        payAfterConfirmed(str, chatInvite, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                StarsController.lambda$payAfterConfirmed$88(Utilities.Callback2.this, (Long) obj, (Boolean) obj2);
            }
        });
    }

    public static void lambda$payAfterConfirmed$90(Utilities.Callback2 callback2, boolean[] zArr, DialogInterface dialogInterface) {
        if (callback2 == null || zArr[0]) {
            return;
        }
        callback2.run(0L, Boolean.FALSE);
    }

    public void lambda$payAfterConfirmed$91(TLObject tLObject, final Utilities.Callback2 callback2, long j, String str, TLRPC.TL_error tL_error, Context context, Theme.ResourcesProvider resourcesProvider, final TLRPC.ChatInvite chatInvite, final String str2) {
        this.paymentFormOpened = false;
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        BulletinFactory of = !AndroidUtilities.hasDialogOnTop(lastFragment) ? BulletinFactory.of(lastFragment) : BulletinFactory.global();
        if (!(tLObject instanceof TLRPC.TL_payments_paymentResult)) {
            if (tL_error == null || !"BALANCE_TOO_LOW".equals(tL_error.text)) {
                if (callback2 != null) {
                    callback2.run(0L, Boolean.FALSE);
                }
                of.createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, tL_error != null ? tL_error.text : "FAILED_SEND_STARS")).show();
                return;
            } else if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                if (callback2 != null) {
                    callback2.run(0L, Boolean.FALSE);
                }
                showNoSupportDialog(context, resourcesProvider);
                return;
            } else {
                final boolean[] zArr = {false};
                StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(context, resourcesProvider, j, 1, chatInvite.title, new Runnable() {
                    @Override
                    public final void run() {
                        StarsController.this.lambda$payAfterConfirmed$89(zArr, str2, chatInvite, callback2);
                    }
                });
                starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        StarsController.lambda$payAfterConfirmed$90(Utilities.Callback2.this, zArr, dialogInterface);
                    }
                });
                starsNeededSheet.show();
                return;
            }
        }
        final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$payAfterConfirmed$87(tL_payments_paymentResult);
            }
        });
        TLRPC.Updates updates = tL_payments_paymentResult.updates;
        TLRPC.Update update = updates.update;
        long j2 = update instanceof TLRPC.TL_updateChannel ? -((TLRPC.TL_updateChannel) update).channel_id : 0L;
        if (updates.updates != null) {
            for (int i = 0; i < tL_payments_paymentResult.updates.updates.size(); i++) {
                if (tL_payments_paymentResult.updates.updates.get(i) instanceof TLRPC.TL_updateChannel) {
                    j2 = -((TLRPC.TL_updateChannel) tL_payments_paymentResult.updates.updates.get(i)).channel_id;
                }
            }
        }
        if (callback2 != null) {
            callback2.run(Long.valueOf(j2), Boolean.TRUE);
        }
        if (j2 == 0) {
            of.createSimpleBulletin(R.raw.stars_send, LocaleController.getString(R.string.StarsSubscriptionCompleted), AndroidUtilities.replaceTags(LocaleController.formatPluralString("StarsSubscriptionCompletedText", (int) j, str))).show();
        }
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity != null && launchActivity.getFireworksOverlay() != null) {
            LaunchActivity.instance.getFireworksOverlay().start(true);
        }
        invalidateTransactions(true);
        invalidateSubscriptions(true);
    }

    public void lambda$payAfterConfirmed$92(final Utilities.Callback2 callback2, final long j, final String str, final Context context, final Theme.ResourcesProvider resourcesProvider, final TLRPC.ChatInvite chatInvite, final String str2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$payAfterConfirmed$91(tLObject, callback2, j, str, tL_error, context, resourcesProvider, chatInvite, str2);
            }
        });
    }

    public static void lambda$saveStarGiftsCached$110(MessagesStorage messagesStorage, ArrayList arrayList, int i, long j) {
        SQLiteDatabase database = messagesStorage.getDatabase();
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                database.executeFast("DELETE FROM star_gifts2").stepThis().dispose();
                if (arrayList != null) {
                    sQLitePreparedStatement = database.executeFast("REPLACE INTO star_gifts2 VALUES(?, ?, ?, ?, ?)");
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        TL_stars.StarGift starGift = (TL_stars.StarGift) arrayList.get(i2);
                        sQLitePreparedStatement.requery();
                        sQLitePreparedStatement.bindLong(1, starGift.id);
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(starGift.getObjectSize());
                        starGift.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer);
                        sQLitePreparedStatement.bindLong(3, i);
                        sQLitePreparedStatement.bindLong(4, j);
                        sQLitePreparedStatement.bindInteger(5, i2);
                        sQLitePreparedStatement.step();
                        nativeByteBuffer.reuse();
                    }
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

    public void lambda$sendPaidReaction$98(MessageObject messageObject, ChatActivity chatActivity, long j, Boolean bool) {
        sendPaidReaction(messageObject, chatActivity, j, true, true, bool);
    }

    public void lambda$sendPaidReaction$99(MessageObject messageObject, ChatActivity chatActivity, long j, Boolean bool) {
        sendPaidReaction(messageObject, chatActivity, j, true, true, bool);
    }

    public static void lambda$showStarsTopupInternal$24() {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        safeLastFragment.presentFragment(new StarsIntroActivity());
    }

    public static void lambda$showStarsTopupInternal$25() {
    }

    public static void lambda$subscribeTo$73(boolean[] zArr, Utilities.Callback2 callback2, Utilities.Callback callback, Long l, Boolean bool) {
        zArr[0] = true;
        if (callback2 != null) {
            callback2.run(bool.booleanValue() ? "paid" : "failed", l);
        }
        if (callback != null) {
            callback.run(Boolean.TRUE);
        }
    }

    public void lambda$subscribeTo$74(boolean[] zArr, String str, TLRPC.ChatInvite chatInvite, final boolean[] zArr2, final Utilities.Callback2 callback2, final Utilities.Callback callback) {
        zArr[0] = true;
        payAfterConfirmed(str, chatInvite, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                StarsController.lambda$subscribeTo$73(zArr2, callback2, callback, (Long) obj, (Boolean) obj2);
            }
        });
    }

    public void lambda$subscribeTo$75(Utilities.Callback callback, boolean[] zArr, boolean[] zArr2, Utilities.Callback2 callback2, DialogInterface dialogInterface) {
        if (callback == null || zArr[0]) {
            return;
        }
        callback.run(Boolean.FALSE);
        this.paymentFormOpened = false;
        if (zArr2[0] || callback2 == null) {
            return;
        }
        callback2.run("cancelled", 0L);
        zArr2[0] = true;
    }

    public static void lambda$subscribeTo$76(Utilities.Callback callback, boolean[] zArr, Utilities.Callback2 callback2, Long l, Boolean bool) {
        if (callback != null) {
            callback.run(Boolean.TRUE);
        }
        zArr[0] = true;
        if (callback2 != null) {
            callback2.run(bool.booleanValue() ? "paid" : "failed", l);
        }
    }

    public void lambda$subscribeTo$77(long j, int i, final boolean[] zArr, final Utilities.Callback2 callback2, Context context, Theme.ResourcesProvider resourcesProvider, final TLRPC.ChatInvite chatInvite, final String str, final Utilities.Callback callback) {
        if (this.balance.amount >= j) {
            payAfterConfirmed(str, chatInvite, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    StarsController.lambda$subscribeTo$76(Utilities.Callback.this, zArr, callback2, (Long) obj, (Boolean) obj2);
                }
            });
            return;
        }
        if (MessagesController.getInstance(i).starsPurchaseAvailable()) {
            final boolean[] zArr2 = {false};
            StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(context, resourcesProvider, j, 1, chatInvite.title, new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$subscribeTo$74(zArr2, str, chatInvite, zArr, callback2, callback);
                }
            });
            starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    StarsController.this.lambda$subscribeTo$75(callback, zArr2, zArr, callback2, dialogInterface);
                }
            });
            starsNeededSheet.show();
            return;
        }
        this.paymentFormOpened = false;
        if (callback != null) {
            callback.run(Boolean.FALSE);
        }
        if (!zArr[0] && callback2 != null) {
            callback2.run("cancelled", 0L);
            zArr[0] = true;
        }
        showNoSupportDialog(context, resourcesProvider);
    }

    public void lambda$subscribeTo$78(boolean[] zArr, Utilities.Callback2 callback2) {
        this.paymentFormOpened = false;
        if (zArr[0] || callback2 == null) {
            return;
        }
        callback2.run("cancelled", 0L);
        zArr[0] = true;
    }

    public void lambda$updateMediaPrice$93(TLObject tLObject) {
        MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
    }

    public void lambda$updateMediaPrice$94(TLObject tLObject, MessageObject messageObject, long j, Runnable runnable) {
        if (tLObject instanceof TLRPC.TL_messages_messages) {
            TLRPC.TL_messages_messages tL_messages_messages = (TLRPC.TL_messages_messages) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_messages_messages.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_messages_messages.chats, false);
            if (tL_messages_messages.messages.size() == 1 && (tL_messages_messages.messages.get(0) instanceof TLRPC.TL_message) && (tL_messages_messages.messages.get(0).media instanceof TLRPC.TL_messageMediaPaidMedia)) {
                messageObject.messageOwner = tL_messages_messages.messages.get(0);
                updateMediaPrice(messageObject, j, runnable, true);
                return;
            }
        }
        runnable.run();
    }

    public void lambda$updateMediaPrice$95(final MessageObject messageObject, final long j, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$updateMediaPrice$94(tLObject, messageObject, j, runnable);
            }
        });
    }

    public void lambda$updateMediaPrice$96(final TLObject tLObject, final Runnable runnable, TLRPC.TL_error tL_error, boolean z, long j, int i, final MessageObject messageObject, final long j2) {
        if (tLObject instanceof TLRPC.Updates) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$updateMediaPrice$93(tLObject);
                }
            });
        } else if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && !z) {
            TLRPC.TL_messages_getScheduledMessages tL_messages_getScheduledMessages = new TLRPC.TL_messages_getScheduledMessages();
            tL_messages_getScheduledMessages.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            tL_messages_getScheduledMessages.id.add(Integer.valueOf(i));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getScheduledMessages, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    StarsController.this.lambda$updateMediaPrice$95(messageObject, j2, runnable, tLObject2, tL_error2);
                }
            });
            return;
        }
        runnable.run();
    }

    public void lambda$updateMediaPrice$97(final Runnable runnable, final boolean z, final long j, final int i, final MessageObject messageObject, final long j2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$updateMediaPrice$96(tLObject, runnable, tL_error, z, j, i, messageObject, j2);
            }
        });
    }

    private void payAfterConfirmed(final String str, final TLRPC.ChatInvite chatInvite, final Utilities.Callback2 callback2) {
        if (chatInvite == null || chatInvite.subscription_pricing == null) {
            return;
        }
        final Context context = ApplicationLoader.applicationContext;
        final Theme.ResourcesProvider resourceProvider = getResourceProvider();
        if (context == null) {
            return;
        }
        final long j = chatInvite.subscription_pricing.amount;
        final String str2 = chatInvite.title;
        TLRPC.TL_inputInvoiceChatInviteSubscription tL_inputInvoiceChatInviteSubscription = new TLRPC.TL_inputInvoiceChatInviteSubscription();
        tL_inputInvoiceChatInviteSubscription.hash = str;
        TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
        tL_payments_sendStarsForm.form_id = chatInvite.subscription_form_id;
        tL_payments_sendStarsForm.invoice = tL_inputInvoiceChatInviteSubscription;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$payAfterConfirmed$92(callback2, j, str2, context, resourceProvider, chatInvite, str, tLObject, tL_error);
            }
        });
    }

    private void saveStarGiftsCached(final ArrayList arrayList, final int i, final long j) {
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$saveStarGiftsCached$110(MessagesStorage.this, arrayList, i, j);
            }
        });
    }

    public static void showNoSupportDialog(Context context, Theme.ResourcesProvider resourcesProvider) {
        new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString(R.string.StarsNotAvailableTitle)).setMessage(LocaleController.getString(R.string.StarsNotAvailableText)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
    }

    public void lambda$showStarsTopup$23(Activity activity, long j, String str) {
        if (getBalance().amount < j && j > 0) {
            new StarsIntroActivity.StarsNeededSheet(activity, null, j, 4, str, new Runnable() {
                @Override
                public final void run() {
                    StarsController.lambda$showStarsTopupInternal$25();
                }
            }).show();
            return;
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.StarsTopupLinkEnough), LocaleController.getString(R.string.StarsTopupLinkTopupAnyway), new Runnable() {
            @Override
            public final void run() {
                StarsController.lambda$showStarsTopupInternal$24();
            }
        }).setDuration(5000).show(true);
    }

    private void updateMediaPrice(final MessageObject messageObject, final long j, final Runnable runnable, final boolean z) {
        TLRPC.TL_inputMediaDocument tL_inputMediaDocument;
        if (messageObject == null) {
            runnable.run();
            return;
        }
        final long dialogId = messageObject.getDialogId();
        final int id = messageObject.getId();
        TLRPC.TL_messageMediaPaidMedia tL_messageMediaPaidMedia = (TLRPC.TL_messageMediaPaidMedia) messageObject.messageOwner.media;
        TLRPC.TL_messages_editMessage tL_messages_editMessage = new TLRPC.TL_messages_editMessage();
        tL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(dialogId);
        int i = tL_messages_editMessage.flags;
        tL_messages_editMessage.flags = 32768 | i;
        tL_messages_editMessage.schedule_date = messageObject.messageOwner.date;
        tL_messages_editMessage.id = id;
        tL_messages_editMessage.flags = i | 49152;
        TLRPC.TL_inputMediaPaidMedia tL_inputMediaPaidMedia = new TLRPC.TL_inputMediaPaidMedia();
        tL_inputMediaPaidMedia.stars_amount = j;
        for (int i2 = 0; i2 < tL_messageMediaPaidMedia.extended_media.size(); i2++) {
            TLRPC.MessageExtendedMedia messageExtendedMedia = tL_messageMediaPaidMedia.extended_media.get(i2);
            if (!(messageExtendedMedia instanceof TLRPC.TL_messageExtendedMedia)) {
                runnable.run();
                return;
            }
            TLRPC.MessageMedia messageMedia = ((TLRPC.TL_messageExtendedMedia) messageExtendedMedia).media;
            if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto = new TLRPC.TL_inputMediaPhoto();
                TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
                TLRPC.Photo photo = ((TLRPC.TL_messageMediaPhoto) messageMedia).photo;
                tL_inputPhoto.id = photo.id;
                tL_inputPhoto.access_hash = photo.access_hash;
                tL_inputPhoto.file_reference = photo.file_reference;
                tL_inputMediaPhoto.id = tL_inputPhoto;
                tL_inputMediaDocument = tL_inputMediaPhoto;
            } else if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.TL_inputMediaDocument tL_inputMediaDocument2 = new TLRPC.TL_inputMediaDocument();
                TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
                TLRPC.Document document = ((TLRPC.TL_messageMediaDocument) messageMedia).document;
                tL_inputDocument.id = document.id;
                tL_inputDocument.access_hash = document.access_hash;
                tL_inputDocument.file_reference = document.file_reference;
                tL_inputMediaDocument2.id = tL_inputDocument;
                tL_inputMediaDocument = tL_inputMediaDocument2;
            }
            tL_inputMediaPaidMedia.extended_media.add(tL_inputMediaDocument);
        }
        tL_messages_editMessage.media = tL_inputMediaPaidMedia;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editMessage, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$updateMediaPrice$97(runnable, z, dialogId, id, messageObject, j, tLObject, tL_error);
            }
        });
    }

    public boolean arePaidReactionsAnonymous(MessageObject messageObject) {
        Boolean bool;
        PendingPaidReactions pendingPaidReactions = this.currentPendingReactions;
        if (pendingPaidReactions != null && pendingPaidReactions.message.equals(MessageId.from(messageObject)) && (bool = this.currentPendingReactions.anonymous) != null) {
            return bool.booleanValue();
        }
        Boolean isMyPaidReactionAnonymous = messageObject == null ? null : messageObject.isMyPaidReactionAnonymous();
        if (isMyPaidReactionAnonymous != null) {
            return isMyPaidReactionAnonymous.booleanValue();
        }
        Boolean arePaidReactionsAnonymous = MessagesController.getInstance(this.currentAccount).arePaidReactionsAnonymous();
        if (arePaidReactionsAnonymous != null) {
            return arePaidReactionsAnonymous.booleanValue();
        }
        return false;
    }

    public boolean arePaidReactionsAnonymous(MessageId messageId, TLRPC.MessageReactions messageReactions) {
        Boolean bool;
        PendingPaidReactions pendingPaidReactions = this.currentPendingReactions;
        if (pendingPaidReactions != null && pendingPaidReactions.message.equals(messageId) && (bool = this.currentPendingReactions.anonymous) != null) {
            return bool.booleanValue();
        }
        Boolean isMyPaidReactionAnonymous = MessageObject.isMyPaidReactionAnonymous(messageReactions);
        if (isMyPaidReactionAnonymous != null) {
            return isMyPaidReactionAnonymous.booleanValue();
        }
        Boolean arePaidReactionsAnonymous = MessagesController.getInstance(this.currentAccount).arePaidReactionsAnonymous();
        if (arePaidReactionsAnonymous != null) {
            return arePaidReactionsAnonymous.booleanValue();
        }
        return false;
    }

    public boolean balanceAvailable() {
        return this.balanceLoaded;
    }

    public void buy(final Activity activity, final TL_stars.TL_starsTopupOption tL_starsTopupOption, final Utilities.Callback2 callback2) {
        Theme.ResourcesProvider resourcesProvider;
        Activity activity2;
        if (activity == null) {
            return;
        }
        if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null || lastFragment.getContext() == null) {
                resourcesProvider = null;
                activity2 = activity;
            } else {
                ?? context = lastFragment.getContext();
                resourcesProvider = lastFragment.getResourceProvider();
                activity2 = context;
            }
            showNoSupportDialog(activity2, resourcesProvider);
            return;
        }
        if (!BuildVars.useInvoiceBilling() && BillingController.getInstance().isReady()) {
            final TLRPC.TL_inputStorePaymentStarsTopup tL_inputStorePaymentStarsTopup = new TLRPC.TL_inputStorePaymentStarsTopup();
            tL_inputStorePaymentStarsTopup.stars = tL_starsTopupOption.stars;
            tL_inputStorePaymentStarsTopup.currency = tL_starsTopupOption.currency;
            tL_inputStorePaymentStarsTopup.amount = tL_starsTopupOption.amount;
            BillingController.getInstance().queryProductDetails(Arrays.asList(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(tL_starsTopupOption.store_product).build()), new ProductDetailsResponseListener() {
                @Override
                public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                    StarsController.lambda$buy$36(Utilities.Callback2.this, tL_inputStorePaymentStarsTopup, tL_starsTopupOption, activity, billingResult, list);
                }
            });
            return;
        }
        TLRPC.TL_inputStorePaymentStarsTopup tL_inputStorePaymentStarsTopup2 = new TLRPC.TL_inputStorePaymentStarsTopup();
        tL_inputStorePaymentStarsTopup2.stars = tL_starsTopupOption.stars;
        tL_inputStorePaymentStarsTopup2.currency = tL_starsTopupOption.currency;
        tL_inputStorePaymentStarsTopup2.amount = tL_starsTopupOption.amount;
        TLRPC.TL_inputStorePaymentStarsTopup tL_inputStorePaymentStarsTopup3 = new TLRPC.TL_inputStorePaymentStarsTopup();
        tL_inputStorePaymentStarsTopup3.stars = tL_starsTopupOption.stars;
        tL_inputStorePaymentStarsTopup3.amount = tL_starsTopupOption.amount;
        tL_inputStorePaymentStarsTopup3.currency = tL_starsTopupOption.currency;
        final TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars = new TLRPC.TL_inputInvoiceStars();
        tL_inputInvoiceStars.purpose = tL_inputStorePaymentStarsTopup3;
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(getResourceProvider());
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags = 1 | tL_payments_getPaymentForm.flags;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoiceStars;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$buy$28(callback2, tL_inputInvoiceStars, tLObject, tL_error);
            }
        });
    }

    public void buyGift(final Activity activity, final TL_stars.TL_starsGiftOption tL_starsGiftOption, long j, final Utilities.Callback2 callback2) {
        Theme.ResourcesProvider resourcesProvider;
        Activity activity2;
        if (activity == null) {
            return;
        }
        if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null || lastFragment.getContext() == null) {
                resourcesProvider = null;
                activity2 = activity;
            } else {
                ?? context = lastFragment.getContext();
                resourcesProvider = lastFragment.getResourceProvider();
                activity2 = context;
            }
            showNoSupportDialog(activity2, resourcesProvider);
            return;
        }
        if (!BuildVars.useInvoiceBilling() && BillingController.getInstance().isReady()) {
            final TLRPC.TL_inputStorePaymentStarsGift tL_inputStorePaymentStarsGift = new TLRPC.TL_inputStorePaymentStarsGift();
            tL_inputStorePaymentStarsGift.stars = tL_starsGiftOption.stars;
            tL_inputStorePaymentStarsGift.currency = tL_starsGiftOption.currency;
            tL_inputStorePaymentStarsGift.amount = tL_starsGiftOption.amount;
            tL_inputStorePaymentStarsGift.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(j);
            BillingController.getInstance().queryProductDetails(Arrays.asList(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(tL_starsGiftOption.store_product).build()), new ProductDetailsResponseListener() {
                @Override
                public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                    StarsController.this.lambda$buyGift$49(callback2, tL_inputStorePaymentStarsGift, tL_starsGiftOption, activity, billingResult, list);
                }
            });
            return;
        }
        TLRPC.TL_inputStorePaymentStarsGift tL_inputStorePaymentStarsGift2 = new TLRPC.TL_inputStorePaymentStarsGift();
        tL_inputStorePaymentStarsGift2.stars = tL_starsGiftOption.stars;
        tL_inputStorePaymentStarsGift2.amount = tL_starsGiftOption.amount;
        tL_inputStorePaymentStarsGift2.currency = tL_starsGiftOption.currency;
        tL_inputStorePaymentStarsGift2.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(j);
        final TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars = new TLRPC.TL_inputInvoiceStars();
        tL_inputInvoiceStars.purpose = tL_inputStorePaymentStarsGift2;
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(getResourceProvider());
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoiceStars;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$buyGift$39(callback2, tL_inputInvoiceStars, tLObject, tL_error);
            }
        });
    }

    public void buyGiveaway(final Activity activity, TLRPC.Chat chat, List list, TL_stars.TL_starsGiveawayOption tL_starsGiveawayOption, int i, List list2, int i2, boolean z, boolean z2, boolean z3, String str, final Utilities.Callback2 callback2) {
        Theme.ResourcesProvider resourcesProvider;
        Activity activity2;
        if (activity == null) {
            return;
        }
        if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null || lastFragment.getContext() == null) {
                resourcesProvider = null;
                activity2 = activity;
            } else {
                ?? context = lastFragment.getContext();
                resourcesProvider = lastFragment.getResourceProvider();
                activity2 = context;
            }
            showNoSupportDialog(activity2, resourcesProvider);
            return;
        }
        final TLRPC.TL_inputStorePaymentStarsGiveaway tL_inputStorePaymentStarsGiveaway = new TLRPC.TL_inputStorePaymentStarsGiveaway();
        tL_inputStorePaymentStarsGiveaway.only_new_subscribers = z2;
        tL_inputStorePaymentStarsGiveaway.winners_are_visible = z;
        tL_inputStorePaymentStarsGiveaway.stars = tL_starsGiveawayOption.stars;
        MessagesController.getInstance(this.currentAccount);
        tL_inputStorePaymentStarsGiveaway.boost_peer = MessagesController.getInputPeer(chat);
        if (list != null && !list.isEmpty()) {
            tL_inputStorePaymentStarsGiveaway.flags |= 2;
            Iterator it = list.iterator();
            while (it.hasNext()) {
                TLObject tLObject = (TLObject) it.next();
                ArrayList<TLRPC.InputPeer> arrayList = tL_inputStorePaymentStarsGiveaway.additional_peers;
                MessagesController.getInstance(this.currentAccount);
                arrayList.add(MessagesController.getInputPeer(tLObject));
            }
        }
        Iterator it2 = list2.iterator();
        while (it2.hasNext()) {
            tL_inputStorePaymentStarsGiveaway.countries_iso2.add(((TLRPC.TL_help_country) ((TLObject) it2.next())).iso2);
        }
        if (!tL_inputStorePaymentStarsGiveaway.countries_iso2.isEmpty()) {
            tL_inputStorePaymentStarsGiveaway.flags |= 4;
        }
        if (z3) {
            tL_inputStorePaymentStarsGiveaway.flags |= 16;
            tL_inputStorePaymentStarsGiveaway.prize_description = str;
        }
        tL_inputStorePaymentStarsGiveaway.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
        tL_inputStorePaymentStarsGiveaway.until_date = i2;
        tL_inputStorePaymentStarsGiveaway.currency = tL_starsGiveawayOption.currency;
        tL_inputStorePaymentStarsGiveaway.amount = tL_starsGiveawayOption.amount;
        tL_inputStorePaymentStarsGiveaway.users = i;
        if (!BuildVars.useInvoiceBilling() && BillingController.getInstance().isReady() && tL_starsGiveawayOption.store_product != null) {
            BillingController.getInstance().queryProductDetails(Arrays.asList(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(tL_starsGiveawayOption.store_product).build()), new ProductDetailsResponseListener() {
                @Override
                public final void onProductDetailsResponse(BillingResult billingResult, List list3) {
                    StarsController.this.lambda$buyGiveaway$62(callback2, tL_inputStorePaymentStarsGiveaway, activity, billingResult, list3);
                }
            });
            return;
        }
        final TLRPC.TL_inputInvoiceStars tL_inputInvoiceStars = new TLRPC.TL_inputInvoiceStars();
        tL_inputInvoiceStars.purpose = tL_inputStorePaymentStarsGiveaway;
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(getResourceProvider());
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoiceStars;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$buyGiveaway$52(callback2, tL_inputInvoiceStars, tLObject2, tL_error);
            }
        });
    }

    public void buyStarGift(final TL_stars.StarGift starGift, final boolean z, final boolean z2, final long j, final TLRPC.TL_textWithEntities tL_textWithEntities, final Utilities.Callback2 callback2) {
        String str;
        Context context = LaunchActivity.instance;
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        final Context context2 = context;
        final Theme.ResourcesProvider resourceProvider = getResourceProvider();
        if (starGift == null || context2 == null) {
            return;
        }
        if (!balanceAvailable()) {
            getBalance(new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$buyStarGift$115(callback2, starGift, z, z2, j, tL_textWithEntities);
                }
            });
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        if (j >= 0) {
            str = UserObject.getUserName(messagesController.getUser(Long.valueOf(j)));
        } else {
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
            str = chat == null ? "" : chat.title;
        }
        final String str2 = str;
        final TLRPC.TL_inputInvoiceStarGift tL_inputInvoiceStarGift = new TLRPC.TL_inputInvoiceStarGift();
        tL_inputInvoiceStarGift.hide_name = z;
        tL_inputInvoiceStarGift.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        tL_inputInvoiceStarGift.gift_id = starGift.id;
        tL_inputInvoiceStarGift.include_upgrade = z2;
        if (tL_textWithEntities != null && !TextUtils.isEmpty(tL_textWithEntities.text)) {
            tL_inputInvoiceStarGift.flags |= 2;
            tL_inputInvoiceStarGift.message = tL_textWithEntities;
        }
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(resourceProvider);
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoiceStarGift;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$buyStarGift$125(callback2, tL_inputInvoiceStarGift, context2, resourceProvider, str2, starGift, z, z2, j, tL_textWithEntities, tLObject, tL_error);
            }
        });
    }

    public void commitPaidReaction() {
        PendingPaidReactions pendingPaidReactions = this.currentPendingReactions;
        if (pendingPaidReactions != null) {
            pendingPaidReactions.close();
        }
    }

    public boolean didFullyLoadSubscriptions() {
        return this.subscriptionsEndReached;
    }

    public boolean didFullyLoadTransactions(int i) {
        return this.endReached[i];
    }

    public TL_stars.SavedStarGift findUserStarGift(long j) {
        TL_stars.StarGift starGift;
        for (int i = 0; i < this.giftLists.size(); i++) {
            GiftsList giftsList = (GiftsList) this.giftLists.valueAt(i);
            for (int i2 = 0; i2 < giftsList.gifts.size(); i2++) {
                TL_stars.SavedStarGift savedStarGift = (TL_stars.SavedStarGift) giftsList.gifts.get(i2);
                if (savedStarGift != null && (starGift = savedStarGift.gift) != null && starGift.id == j) {
                    return savedStarGift;
                }
            }
        }
        return null;
    }

    public long getBalance(boolean z) {
        return getBalance(z, null, false).amount;
    }

    public TL_stars.StarsAmount getBalance() {
        return getBalance((Runnable) null);
    }

    public TL_stars.StarsAmount getBalance(Runnable runnable) {
        return getBalance(true, runnable, false);
    }

    public TL_stars.StarsAmount getBalance(boolean z, final Runnable runnable, boolean z2) {
        if (((!this.balanceLoaded || System.currentTimeMillis() - this.lastBalanceLoaded > 60000) && !this.balanceLoading) || z2) {
            this.balanceLoading = true;
            TL_stars.TL_payments_getStarsStatus tL_payments_getStarsStatus = new TL_stars.TL_payments_getStarsStatus();
            tL_payments_getStarsStatus.peer = new TLRPC.TL_inputPeerSelf();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getStarsStatus, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarsController.this.lambda$getBalance$1(runnable, tLObject, tL_error);
                }
            });
        }
        if (!z || this.minus <= 0) {
            return this.balance;
        }
        TL_stars.StarsAmount starsAmount = new TL_stars.StarsAmount();
        starsAmount.amount = Math.max(0L, this.balance.amount - this.minus);
        starsAmount.nanos = this.balance.nanos;
        return starsAmount;
    }

    public Context getContext(ChatActivity chatActivity) {
        if (chatActivity != null && chatActivity.getContext() != null) {
            return chatActivity.getContext();
        }
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity != null && !launchActivity.isFinishing()) {
            return LaunchActivity.instance;
        }
        if (ApplicationLoader.applicationContext != null) {
            return ApplicationLoader.applicationContext;
        }
        return null;
    }

    public ArrayList getGiftOptions() {
        if (this.giftOptionsLoading || this.giftOptionsLoaded) {
            return this.giftOptions;
        }
        this.giftOptionsLoading = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_stars.TL_payments_getStarsGiftOptions(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$getGiftOptions$11(tLObject, tL_error);
            }
        });
        return this.giftOptions;
    }

    public ArrayList getGiveawayOptions() {
        if (this.giveawayOptionsLoading || this.giveawayOptionsLoaded) {
            return this.giveawayOptions;
        }
        this.giveawayOptionsLoading = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_stars.TL_payments_getStarsGiveawayOptions(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$getGiveawayOptions$16(tLObject, tL_error);
            }
        });
        return this.giveawayOptions;
    }

    public ArrayList getOptions() {
        if (this.optionsLoading || this.optionsLoaded) {
            return this.options;
        }
        this.optionsLoading = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_stars.TL_payments_getStarsTopupOptions(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$getOptions$6(tLObject, tL_error);
            }
        });
        return this.options;
    }

    public long getPendingPaidReactions(long j, int i) {
        PendingPaidReactions pendingPaidReactions = this.currentPendingReactions;
        if (pendingPaidReactions == null) {
            return 0L;
        }
        MessageId messageId = pendingPaidReactions.message;
        if (messageId.did == j && messageId.mid == i && pendingPaidReactions.applied) {
            return pendingPaidReactions.amount;
        }
        return 0L;
    }

    public long getPendingPaidReactions(MessageObject messageObject) {
        TLRPC.Message message;
        long fromChatId;
        int i;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            return 0L;
        }
        if ((message.isThreadMessage || messageObject.isForwardedChannelPost()) && messageObject.messageOwner.fwd_from != null) {
            fromChatId = messageObject.getFromChatId();
            i = messageObject.messageOwner.fwd_from.saved_from_msg_id;
        } else {
            fromChatId = messageObject.getDialogId();
            i = messageObject.getId();
        }
        return getPendingPaidReactions(fromChatId, i);
    }

    public GiftsList getProfileGiftsList(long j) {
        return getProfileGiftsList(j, true);
    }

    public GiftsList getProfileGiftsList(long j, boolean z) {
        GiftsList giftsList = (GiftsList) this.giftLists.get(j);
        if (giftsList != null || !z) {
            return giftsList;
        }
        LongSparseArray longSparseArray = this.giftLists;
        GiftsList giftsList2 = new GiftsList(this.currentAccount, j);
        longSparseArray.put(j, giftsList2);
        return giftsList2;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            return safeLastFragment.getResourceProvider();
        }
        return null;
    }

    public Runnable getStarGift(final long j, final Utilities.Callback callback) {
        final boolean[] zArr = {false};
        final NotificationCenter.NotificationCenterDelegate[] notificationCenterDelegateArr = {new NotificationCenter.NotificationCenterDelegate() {
            @Override
            public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                StarsController.this.lambda$getStarGift$113(zArr, j, notificationCenterDelegateArr, callback, i, i2, objArr);
            }
        }};
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = notificationCenterDelegateArr[0];
        int i = NotificationCenter.starGiftsLoaded;
        notificationCenter.addObserver(notificationCenterDelegate, i);
        TL_stars.StarGift starGift = getStarGift(j);
        if (starGift != null) {
            zArr[0] = true;
            NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegateArr[0], i);
            callback.run(starGift);
        }
        return new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$getStarGift$114(zArr, notificationCenterDelegateArr);
            }
        };
    }

    public TL_stars.StarGift getStarGift(long j) {
        loadStarGifts();
        for (int i = 0; i < this.gifts.size(); i++) {
            TL_stars.StarGift starGift = (TL_stars.StarGift) this.gifts.get(i);
            if (starGift.id == j) {
                return starGift;
            }
        }
        return null;
    }

    public void getStarGiftPreview(final long j, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        TL_stars.starGiftUpgradePreview stargiftupgradepreview = (TL_stars.starGiftUpgradePreview) this.giftPreviews.get(Long.valueOf(j));
        if (stargiftupgradepreview != null) {
            callback.run(stargiftupgradepreview);
            return;
        }
        TL_stars.getStarGiftUpgradePreview getstargiftupgradepreview = new TL_stars.getStarGiftUpgradePreview();
        getstargiftupgradepreview.gift_id = j;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getstargiftupgradepreview, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$getStarGiftPreview$127(j, callback, tLObject, tL_error);
            }
        });
    }

    public void getUserStarGift(final TL_stars.InputSavedStarGift inputSavedStarGift, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(ApplicationLoader.applicationContext, 3);
        alertDialog.showDelayed(200L);
        TL_stars.getSavedStarGift getsavedstargift = new TL_stars.getSavedStarGift();
        getsavedstargift.stargift.add(inputSavedStarGift);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getsavedstargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$getUserStarGift$129(alertDialog, inputSavedStarGift, callback, tLObject, tL_error);
            }
        });
    }

    public boolean hasInsufficientSubscriptions() {
        return !this.insufficientSubscriptions.isEmpty();
    }

    public boolean hasSubscriptions() {
        return balanceAvailable() && !this.subscriptions.isEmpty();
    }

    public boolean hasTransactions() {
        return hasTransactions(0);
    }

    public boolean hasTransactions(int i) {
        return balanceAvailable() && !this.transactions[i].isEmpty();
    }

    public void invalidateBalance() {
        this.balanceLoaded = false;
        getBalance();
        this.balanceLoaded = true;
    }

    public void invalidateBalance(Runnable runnable) {
        this.balanceLoaded = false;
        getBalance(false, runnable, true);
        this.balanceLoaded = true;
    }

    public void invalidateProfileGifts(long j) {
        GiftsList profileGiftsList = getProfileGiftsList(j, false);
        if (profileGiftsList != null) {
            profileGiftsList.invalidate(false);
        }
    }

    public void invalidateProfileGifts(TLRPC.UserFull userFull) {
        GiftsList profileGiftsList;
        if (userFull == null || (profileGiftsList = getProfileGiftsList(userFull.id, false)) == null || profileGiftsList.totalCount == userFull.stargifts_count) {
            return;
        }
        profileGiftsList.invalidate(false);
    }

    public void invalidateStarGifts() {
        this.giftsLoaded = false;
        this.giftsCacheLoaded = true;
        this.giftsRemoteTime = 0L;
        loadStarGifts();
    }

    public void invalidateSubscriptions(boolean z) {
        if (this.subscriptionsLoading) {
            return;
        }
        this.subscriptions.clear();
        this.subscriptionsOffset = null;
        this.subscriptionsLoading = false;
        this.subscriptionsEndReached = false;
        if (z) {
            loadSubscriptions();
        }
    }

    public void invalidateTransactions(boolean z) {
        for (int i = 0; i < 3; i++) {
            if (!this.loading[i]) {
                this.transactions[i].clear();
                this.offset[i] = null;
                this.loading[i] = false;
                this.endReached[i] = false;
                if (z) {
                    loadTransactions(i);
                }
            }
        }
    }

    public boolean isLoadingSubscriptions() {
        return this.subscriptionsLoading;
    }

    public void loadInsufficientSubscriptions() {
        if (this.insufficientSubscriptionsLoading) {
            return;
        }
        this.insufficientSubscriptionsLoading = true;
        TL_stars.TL_getStarsSubscriptions tL_getStarsSubscriptions = new TL_stars.TL_getStarsSubscriptions();
        tL_getStarsSubscriptions.peer = new TLRPC.TL_inputPeerSelf();
        tL_getStarsSubscriptions.missing_balance = true;
        tL_getStarsSubscriptions.offset = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getStarsSubscriptions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$loadInsufficientSubscriptions$22(tLObject, tL_error);
            }
        });
    }

    public void loadStarGifts() {
        if (this.giftsLoading) {
            return;
        }
        if (!this.giftsLoaded || System.currentTimeMillis() - this.giftsRemoteTime >= 300000) {
            this.giftsLoading = true;
            if (this.giftsCacheLoaded) {
                getStarGiftsRemote(this.giftsHash, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        StarsController.this.lambda$loadStarGifts$107((TL_stars.StarGifts) obj);
                    }
                });
            } else {
                getStarGiftsCached(new Utilities.Callback3() {
                    @Override
                    public final void run(Object obj, Object obj2, Object obj3) {
                        StarsController.this.lambda$loadStarGifts$103((ArrayList) obj, (Integer) obj2, (Long) obj3);
                    }
                });
            }
        }
    }

    public void loadSubscriptions() {
        if (this.subscriptionsLoading || this.subscriptionsEndReached) {
            return;
        }
        this.subscriptionsLoading = true;
        TL_stars.TL_getStarsSubscriptions tL_getStarsSubscriptions = new TL_stars.TL_getStarsSubscriptions();
        tL_getStarsSubscriptions.peer = new TLRPC.TL_inputPeerSelf();
        String str = this.subscriptionsOffset;
        tL_getStarsSubscriptions.offset = str;
        if (str == null) {
            tL_getStarsSubscriptions.offset = "";
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getStarsSubscriptions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$loadSubscriptions$20(tLObject, tL_error);
            }
        });
    }

    public void loadTransactions(final int i) {
        boolean[] zArr = this.loading;
        if (zArr[i] || this.endReached[i]) {
            return;
        }
        zArr[i] = true;
        TL_stars.TL_payments_getStarsTransactions tL_payments_getStarsTransactions = new TL_stars.TL_payments_getStarsTransactions();
        tL_payments_getStarsTransactions.peer = new TLRPC.TL_inputPeerSelf();
        tL_payments_getStarsTransactions.inbound = i == 1;
        tL_payments_getStarsTransactions.outbound = i == 2;
        String str = this.offset[i];
        tL_payments_getStarsTransactions.offset = str;
        if (str == null) {
            tL_payments_getStarsTransactions.offset = "";
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getStarsTransactions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$loadTransactions$18(i, tLObject, tL_error);
            }
        });
    }

    public void makeStarGiftSoldOut(TL_stars.StarGift starGift) {
        if (starGift == null || !this.giftsLoaded) {
            return;
        }
        starGift.availability_remains = 0;
        saveStarGiftsCached(this.gifts, this.giftsHash, this.giftsRemoteTime);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starGiftSoldOut, starGift);
    }

    public void openPaymentForm(final MessageObject messageObject, final TLRPC.InputInvoice inputInvoice, final TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, final Runnable runnable, final Utilities.Callback callback) {
        long j;
        final String str;
        final boolean z;
        TLRPC.Peer peer;
        if (tL_payments_paymentFormStars == null || tL_payments_paymentFormStars.invoice == null || this.paymentFormOpened) {
            return;
        }
        MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_paymentFormStars.users, false);
        Context context = LaunchActivity.instance;
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        final Context context2 = context;
        final Theme.ResourcesProvider resourceProvider = getResourceProvider();
        if (context2 == null) {
            return;
        }
        if (!balanceAvailable()) {
            getBalance(new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$openPaymentForm$66(runnable, messageObject, inputInvoice, tL_payments_paymentFormStars, callback);
                }
            });
            return;
        }
        Iterator<TLRPC.TL_labeledPrice> it = tL_payments_paymentFormStars.invoice.prices.iterator();
        long j2 = 0;
        while (it.hasNext()) {
            j2 += it.next().amount;
        }
        if (messageObject == null || messageObject.type != 29) {
            j = tL_payments_paymentFormStars.bot_id;
        } else {
            TLRPC.MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
            j = (messageFwdHeader == null || (peer = messageFwdHeader.from_id) == null) ? messageObject.getDialogId() : DialogObject.getPeerDialogId(peer);
        }
        long j3 = j;
        if (j3 >= 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j3));
            String userName = UserObject.getUserName(user);
            UserObject.isBot(user);
            z = !UserObject.isBot(user);
            str = userName;
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j3));
            str = chat == null ? "" : chat.title;
            z = false;
        }
        String str2 = tL_payments_paymentFormStars.title;
        if (runnable != null) {
            runnable.run();
        }
        final int i = tL_payments_paymentFormStars.invoice.subscription_period;
        final boolean[] zArr = {false};
        final long j4 = j2;
        StarsIntroActivity.openConfirmPurchaseSheet(context2, resourceProvider, this.currentAccount, messageObject, j3, str2, j2, tL_payments_paymentFormStars.photo, i, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsController.this.lambda$openPaymentForm$71(j4, zArr, callback, context2, resourceProvider, z, str, messageObject, inputInvoice, tL_payments_paymentFormStars, i, (Utilities.Callback) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$openPaymentForm$72(zArr, callback);
            }
        });
    }

    public Runnable pay(final MessageObject messageObject, final Runnable runnable) {
        Context context = LaunchActivity.instance;
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        Theme.ResourcesProvider resourceProvider = getResourceProvider();
        if (messageObject == null || context == null) {
            return null;
        }
        long dialogId = messageObject.getDialogId();
        int id = messageObject.getId();
        final TLRPC.TL_inputInvoiceMessage tL_inputInvoiceMessage = new TLRPC.TL_inputInvoiceMessage();
        tL_inputInvoiceMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(dialogId);
        tL_inputInvoiceMessage.msg_id = id;
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(resourceProvider);
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoiceMessage;
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$pay$64(messageObject, tL_inputInvoiceMessage, runnable, tLObject, tL_error);
            }
        });
        return new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$pay$65(sendRequest);
            }
        };
    }

    public void payAfterConfirmed(final MessageObject messageObject, final TLRPC.InputInvoice inputInvoice, final TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars, final Utilities.Callback callback) {
        long j;
        String str;
        TLRPC.User user;
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Peer peer;
        if (tL_payments_paymentFormStars == null) {
            return;
        }
        final Context context = ApplicationLoader.applicationContext;
        final Theme.ResourcesProvider resourceProvider = getResourceProvider();
        if (context == null) {
            return;
        }
        Iterator<TLRPC.TL_labeledPrice> it = tL_payments_paymentFormStars.invoice.prices.iterator();
        final long j2 = 0;
        while (it.hasNext()) {
            j2 += it.next().amount;
        }
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            j = (message == null || (messageFwdHeader = message.fwd_from) == null || (peer = messageFwdHeader.from_id) == null) ? messageObject.getDialogId() : DialogObject.getPeerDialogId(peer);
            if (j < 0 && messageObject.getFromChatId() > 0 && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.getFromChatId()))) != null && user.bot) {
                j = user.id;
            }
        } else {
            j = tL_payments_paymentFormStars.bot_id;
        }
        final long j3 = j;
        if (j3 >= 0) {
            str = UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j3)));
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j3));
            str = chat == null ? "" : chat.title;
        }
        final String str2 = str;
        final String str3 = tL_payments_paymentFormStars.title;
        final int i = tL_payments_paymentFormStars.invoice.subscription_period;
        TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
        tL_payments_sendStarsForm.form_id = tL_payments_paymentFormStars.form_id;
        tL_payments_sendStarsForm.invoice = inputInvoice;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarsController.this.lambda$payAfterConfirmed$86(callback, messageObject, context, j2, str2, i, str3, inputInvoice, j3, resourceProvider, tL_payments_paymentFormStars, tLObject, tL_error);
            }
        });
    }

    public PendingPaidReactions sendPaidReaction(final MessageObject messageObject, final ChatActivity chatActivity, final long j, boolean z, boolean z2, final Boolean bool) {
        Context context;
        boolean z3;
        String str;
        String str2;
        MessageId from = MessageId.from(messageObject);
        StarsController starsController = getInstance(this.currentAccount);
        Context context2 = getContext(chatActivity);
        if (context2 == null) {
            return null;
        }
        String str3 = "";
        if (z2 && starsController.balanceAvailable() && starsController.getBalance(false) <= 0) {
            long dialogId = chatActivity.getDialogId();
            if (dialogId < 0) {
                TLRPC.Chat chat = chatActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
                if (chat != null) {
                    str2 = chat.title;
                }
                new StarsIntroActivity.StarsNeededSheet(context2, chatActivity.getResourceProvider(), j, 5, str3, new Runnable() {
                    @Override
                    public final void run() {
                        StarsController.this.lambda$sendPaidReaction$98(messageObject, chatActivity, j, bool);
                    }
                }).show();
                return null;
            }
            str2 = UserObject.getForcedFirstName(chatActivity.getMessagesController().getUser(Long.valueOf(dialogId)));
            str3 = str2;
            new StarsIntroActivity.StarsNeededSheet(context2, chatActivity.getResourceProvider(), j, 5, str3, new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$sendPaidReaction$98(messageObject, chatActivity, j, bool);
                }
            }).show();
            return null;
        }
        PendingPaidReactions pendingPaidReactions = this.currentPendingReactions;
        if (pendingPaidReactions == null || !pendingPaidReactions.message.equals(from)) {
            PendingPaidReactions pendingPaidReactions2 = this.currentPendingReactions;
            if (pendingPaidReactions2 != null) {
                pendingPaidReactions2.close();
            }
            context = context2;
            z3 = false;
            PendingPaidReactions pendingPaidReactions3 = new PendingPaidReactions(from, messageObject, chatActivity, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), z);
            this.currentPendingReactions = pendingPaidReactions3;
            pendingPaidReactions3.anonymous = bool;
        } else {
            context = context2;
            z3 = false;
        }
        if (this.currentPendingReactions.amount + j > MessagesController.getInstance(this.currentAccount).starsPaidReactionAmountMax) {
            this.currentPendingReactions.close();
            this.currentPendingReactions = new PendingPaidReactions(from, messageObject, chatActivity, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), z);
        }
        final long j2 = this.currentPendingReactions.amount + j;
        if (!z2 || !starsController.balanceAvailable() || starsController.getBalance(z3) >= j2) {
            this.currentPendingReactions.add(j, !(messageObject == null || messageObject.doesPaidReactionExist()) || z);
            PendingPaidReactions pendingPaidReactions4 = this.currentPendingReactions;
            pendingPaidReactions4.anonymous = bool;
            return pendingPaidReactions4;
        }
        this.currentPendingReactions.cancel();
        long dialogId2 = chatActivity.getDialogId();
        if (dialogId2 < 0) {
            TLRPC.Chat chat2 = chatActivity.getMessagesController().getChat(Long.valueOf(-dialogId2));
            if (chat2 != null) {
                str = chat2.title;
            }
            new StarsIntroActivity.StarsNeededSheet(context, chatActivity.getResourceProvider(), j2, 5, str3, new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$sendPaidReaction$99(messageObject, chatActivity, j2, bool);
                }
            }).show();
            return null;
        }
        str = UserObject.getForcedFirstName(chatActivity.getMessagesController().getUser(Long.valueOf(dialogId2)));
        str3 = str;
        new StarsIntroActivity.StarsNeededSheet(context, chatActivity.getResourceProvider(), j2, 5, str3, new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$sendPaidReaction$99(messageObject, chatActivity, j2, bool);
            }
        }).show();
        return null;
    }

    public void showStarsTopup(final Activity activity, final long j, final String str) {
        if (balanceAvailable()) {
            lambda$showStarsTopup$23(activity, j, str);
        } else {
            getBalance(new Runnable() {
                @Override
                public final void run() {
                    StarsController.this.lambda$showStarsTopup$23(activity, j, str);
                }
            });
        }
    }

    public void subscribeTo(final String str, final TLRPC.ChatInvite chatInvite, final Utilities.Callback2 callback2) {
        if (chatInvite == null || chatInvite.subscription_pricing == null) {
            return;
        }
        Context context = LaunchActivity.instance;
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        final Context context2 = context;
        final Theme.ResourcesProvider resourceProvider = getResourceProvider();
        final long j = chatInvite.subscription_pricing.amount;
        if (context2 == null) {
            return;
        }
        final int i = UserConfig.selectedAccount;
        final boolean[] zArr = {false};
        StarsIntroActivity.openStarsChannelInviteSheet(context2, resourceProvider, i, chatInvite, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarsController.this.lambda$subscribeTo$77(j, i, zArr, callback2, context2, resourceProvider, chatInvite, str, (Utilities.Callback) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                StarsController.this.lambda$subscribeTo$78(zArr, callback2);
            }
        });
    }

    public void undoPaidReaction() {
        PendingPaidReactions pendingPaidReactions = this.currentPendingReactions;
        if (pendingPaidReactions != null) {
            pendingPaidReactions.cancel();
        }
    }

    public void updateBalance(TL_stars.StarsAmount starsAmount) {
        if (!this.balance.equals(starsAmount)) {
            this.balance = starsAmount;
            this.minus = 0L;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
        } else if (this.minus != 0) {
            this.minus = 0L;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starBalanceUpdated, new Object[0]);
        }
    }

    public void updateMediaPrice(MessageObject messageObject, long j, Runnable runnable) {
        updateMediaPrice(messageObject, j, runnable, false);
    }
}
