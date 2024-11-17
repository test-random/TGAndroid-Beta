package org.telegram.ui.Components.Premium.boosts;

import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;
import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.json.JSONObject;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.bots.BotWebViewSheet;

public abstract class BoostRepository {
    private static HashMap cachedGiftOptions;

    public static void applyBoost(long j, List list, final Utilities.Callback callback, final Utilities.Callback callback2) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        TL_stories.TL_premium_applyBoost tL_premium_applyBoost = new TL_stories.TL_premium_applyBoost();
        tL_premium_applyBoost.peer = messagesController.getInputPeer(-j);
        tL_premium_applyBoost.flags |= 1;
        tL_premium_applyBoost.slots.addAll(list);
        connectionsManager.sendRequest(tL_premium_applyBoost, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$applyBoost$45(Utilities.Callback.this, messagesController, callback, tLObject, tL_error);
            }
        }, 66);
    }

    public static void applyGiftCode(String str, final Utilities.Callback callback, final Utilities.Callback callback2) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_payments_applyGiftCode tL_payments_applyGiftCode = new TLRPC.TL_payments_applyGiftCode();
        tL_payments_applyGiftCode.slug = str;
        connectionsManager.sendRequest(tL_payments_applyGiftCode, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$applyGiftCode$39(Utilities.Callback.this, callback, tLObject, tL_error);
            }
        }, 2);
    }

    public static int boostsPerSentGift() {
        return (int) MessagesController.getInstance(UserConfig.selectedAccount).boostsPerSentGift;
    }

    public static void checkGiftCode(String str, final Utilities.Callback callback, final Utilities.Callback callback2) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_payments_checkGiftCode tL_payments_checkGiftCode = new TLRPC.TL_payments_checkGiftCode();
        tL_payments_checkGiftCode.slug = str;
        connectionsManager.sendRequest(tL_payments_checkGiftCode, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$checkGiftCode$37(MessagesController.this, callback, callback2, tLObject, tL_error);
            }
        });
    }

    public static List filterGiftOptions(List list, int i) {
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption = (TLRPC.TL_premiumGiftCodeOption) it.next();
            String str = tL_premiumGiftCodeOption.store_product;
            if (tL_premiumGiftCodeOption.users == i) {
                arrayList.add(tL_premiumGiftCodeOption);
            }
        }
        if (arrayList.isEmpty()) {
            Iterator it2 = list.iterator();
            while (it2.hasNext()) {
                TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption2 = (TLRPC.TL_premiumGiftCodeOption) it2.next();
                if (tL_premiumGiftCodeOption2.users == 1) {
                    arrayList.add(tL_premiumGiftCodeOption2);
                }
            }
        }
        return arrayList;
    }

    public static List filterGiftOptionsByBilling(List list) {
        if (!isGoogleBillingAvailable()) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption = (TLRPC.TL_premiumGiftCodeOption) it.next();
            if (tL_premiumGiftCodeOption.store_product != null) {
                arrayList.add(tL_premiumGiftCodeOption);
            }
        }
        return arrayList;
    }

    public static List getCachedGiftOptions(int i) {
        Pair pair;
        HashMap hashMap = cachedGiftOptions;
        if (hashMap == null || (pair = (Pair) hashMap.get(Integer.valueOf(i))) == null || System.currentTimeMillis() - ((Long) pair.first).longValue() >= 1800000) {
            return null;
        }
        return (List) pair.second;
    }

    public static void getGiveawayInfo(MessageObject messageObject, final Utilities.Callback callback, final Utilities.Callback callback2) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_payments_getGiveawayInfo tL_payments_getGiveawayInfo = new TLRPC.TL_payments_getGiveawayInfo();
        tL_payments_getGiveawayInfo.msg_id = messageObject.getId();
        tL_payments_getGiveawayInfo.peer = messagesController.getInputPeer(MessageObject.getPeerId(messageObject.messageOwner.peer_id));
        connectionsManager.sendRequest(tL_payments_getGiveawayInfo, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$getGiveawayInfo$41(Utilities.Callback.this, callback, tLObject, tL_error);
            }
        });
    }

    public static void getMyBoosts(final Utilities.Callback callback, final Utilities.Callback callback2) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        connectionsManager.sendRequest(new TL_stories.TL_premium_getMyBoosts(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$getMyBoosts$43(Utilities.Callback.this, messagesController, callback, tLObject, tL_error);
            }
        });
    }

    public static ArrayList getMyChannels(long j) {
        ArrayList arrayList = new ArrayList();
        MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        ArrayList<TLRPC.Dialog> allDialogs = messagesController.getAllDialogs();
        for (int i = 0; i < allDialogs.size(); i++) {
            TLRPC.Dialog dialog = allDialogs.get(i);
            if (DialogObject.isChatDialog(dialog.id) && ChatObject.isBoostSupported(messagesController.getChat(Long.valueOf(-dialog.id)))) {
                long j2 = dialog.id;
                if ((-j2) != j) {
                    arrayList.add(messagesController.getInputPeer(j2));
                }
            }
        }
        return arrayList;
    }

    public static long giveawayAddPeersMax() {
        return MessagesController.getInstance(UserConfig.selectedAccount).giveawayAddPeersMax;
    }

    public static int giveawayBoostsPerPremium() {
        return (int) MessagesController.getInstance(UserConfig.selectedAccount).giveawayBoostsPerPremium;
    }

    public static long giveawayCountriesMax() {
        return MessagesController.getInstance(UserConfig.selectedAccount).giveawayCountriesMax;
    }

    public static long giveawayPeriodMax() {
        return MessagesController.getInstance(UserConfig.selectedAccount).giveawayPeriodMax;
    }

    public static void invalidateGiftOptionsToCache(int i) {
        HashMap hashMap = cachedGiftOptions;
        if (hashMap != null) {
            hashMap.remove(Integer.valueOf(i));
        }
    }

    public static boolean isGoogleBillingAvailable() {
        if (BuildVars.useInvoiceBilling()) {
            return false;
        }
        return BillingController.getInstance().isReady();
    }

    public static boolean isMultiBoostsAvailable() {
        return MessagesController.getInstance(UserConfig.selectedAccount).boostsPerSentGift > 0;
    }

    public static void lambda$applyBoost$44(TLRPC.TL_error tL_error, Utilities.Callback callback, TLObject tLObject, MessagesController messagesController, Utilities.Callback callback2) {
        if (tL_error != null) {
            callback.run(tL_error);
        } else if (tLObject instanceof TL_stories.TL_premium_myBoosts) {
            TL_stories.TL_premium_myBoosts tL_premium_myBoosts = (TL_stories.TL_premium_myBoosts) tLObject;
            messagesController.putUsers(tL_premium_myBoosts.users, false);
            messagesController.putChats(tL_premium_myBoosts.chats, false);
            callback2.run(tL_premium_myBoosts);
        }
    }

    public static void lambda$applyBoost$45(final Utilities.Callback callback, final MessagesController messagesController, final Utilities.Callback callback2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$applyBoost$44(TLRPC.TL_error.this, callback, tLObject, messagesController, callback2);
            }
        });
    }

    public static void lambda$applyGiftCode$38(TLRPC.TL_error tL_error, Utilities.Callback callback, Utilities.Callback callback2) {
        if (tL_error != null) {
            callback.run(tL_error);
        } else {
            callback2.run(null);
        }
    }

    public static void lambda$applyGiftCode$39(final Utilities.Callback callback, final Utilities.Callback callback2, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$applyGiftCode$38(TLRPC.TL_error.this, callback, callback2);
            }
        });
    }

    public static void lambda$checkGiftCode$36(TLObject tLObject, MessagesController messagesController, Utilities.Callback callback, Utilities.Callback callback2, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_payments_checkedGiftCode) {
            TLRPC.TL_payments_checkedGiftCode tL_payments_checkedGiftCode = (TLRPC.TL_payments_checkedGiftCode) tLObject;
            messagesController.putChats(tL_payments_checkedGiftCode.chats, false);
            messagesController.putUsers(tL_payments_checkedGiftCode.users, false);
            callback.run(tL_payments_checkedGiftCode);
        }
        callback2.run(tL_error);
    }

    public static void lambda$checkGiftCode$37(final MessagesController messagesController, final Utilities.Callback callback, final Utilities.Callback callback2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$checkGiftCode$36(TLObject.this, messagesController, callback, callback2, tL_error);
            }
        });
    }

    public static void lambda$getGiveawayInfo$40(TLRPC.TL_error tL_error, Utilities.Callback callback, TLObject tLObject, Utilities.Callback callback2) {
        if (tL_error != null) {
            callback.run(tL_error);
        } else if (tLObject instanceof TLRPC.payments_GiveawayInfo) {
            callback2.run((TLRPC.payments_GiveawayInfo) tLObject);
        }
    }

    public static void lambda$getGiveawayInfo$41(final Utilities.Callback callback, final Utilities.Callback callback2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$getGiveawayInfo$40(TLRPC.TL_error.this, callback, tLObject, callback2);
            }
        });
    }

    public static void lambda$getMyBoosts$42(TLRPC.TL_error tL_error, Utilities.Callback callback, TLObject tLObject, MessagesController messagesController, Utilities.Callback callback2) {
        if (tL_error != null) {
            callback.run(tL_error);
        } else if (tLObject instanceof TL_stories.TL_premium_myBoosts) {
            TL_stories.TL_premium_myBoosts tL_premium_myBoosts = (TL_stories.TL_premium_myBoosts) tLObject;
            messagesController.putUsers(tL_premium_myBoosts.users, false);
            messagesController.putChats(tL_premium_myBoosts.chats, false);
            callback2.run(tL_premium_myBoosts);
        }
    }

    public static void lambda$getMyBoosts$43(final Utilities.Callback callback, final MessagesController messagesController, final Utilities.Callback callback2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$getMyBoosts$42(TLRPC.TL_error.this, callback, tLObject, messagesController, callback2);
            }
        });
    }

    public static void lambda$launchPreparedGiveaway$14(final Utilities.Callback callback, MessagesController messagesController, final Utilities.Callback callback2, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(tL_error);
                }
            });
        } else if (tLObject != null) {
            messagesController.processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(null);
                }
            });
        }
    }

    public static void lambda$loadChatParticipants$34(TLObject tLObject, MessagesController messagesController, Utilities.Callback callback) {
        TLRPC.User user;
        if (tLObject instanceof TLRPC.TL_channels_channelParticipants) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            messagesController.putUsers(tL_channels_channelParticipants.users, false);
            messagesController.putChats(tL_channels_channelParticipants.chats, false);
            long clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                TLRPC.Peer peer = tL_channels_channelParticipants.participants.get(i).peer;
                if (MessageObject.getPeerId(peer) != clientUserId && (user = messagesController.getUser(Long.valueOf(peer.user_id))) != null && !UserObject.isDeleted(user) && !user.bot) {
                    arrayList.add(messagesController.getInputPeer(peer));
                }
            }
            callback.run(arrayList);
        }
    }

    public static void lambda$loadChatParticipants$35(final MessagesController messagesController, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$loadChatParticipants$34(TLObject.this, messagesController, callback);
            }
        });
    }

    public static int lambda$loadCountries$25(Comparator comparator, TLRPC.TL_help_country tL_help_country, TLRPC.TL_help_country tL_help_country2) {
        return comparator.compare(tL_help_country.default_name, tL_help_country2.default_name);
    }

    public static void lambda$loadCountries$26(Utilities.Callback callback, Map map, List list) {
        callback.run(new Pair(map, list));
    }

    public static void lambda$loadCountries$27(final Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        final Comparator boostRepository$$ExternalSyntheticLambda31;
        if (tLObject != null) {
            TLRPC.TL_help_countriesList tL_help_countriesList = (TLRPC.TL_help_countriesList) tLObject;
            final HashMap hashMap = new HashMap();
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_help_countriesList.countries.size(); i++) {
                TLRPC.TL_help_country tL_help_country = tL_help_countriesList.countries.get(i);
                String str = tL_help_country.name;
                if (str != null) {
                    tL_help_country.default_name = str;
                }
                if (!tL_help_country.iso2.equalsIgnoreCase("FT")) {
                    String upperCase = tL_help_country.default_name.substring(0, 1).toUpperCase();
                    List list = (List) hashMap.get(upperCase);
                    if (list == null) {
                        list = new ArrayList();
                        hashMap.put(upperCase, list);
                        arrayList.add(upperCase);
                    }
                    list.add(tL_help_country);
                }
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Collator collator = Collator.getInstance(LocaleController.getInstance().getCurrentLocale() != null ? LocaleController.getInstance().getCurrentLocale() : Locale.getDefault());
                Objects.requireNonNull(collator);
                boostRepository$$ExternalSyntheticLambda31 = new BoostRepository$$ExternalSyntheticLambda30(collator);
            } else {
                boostRepository$$ExternalSyntheticLambda31 = new BoostRepository$$ExternalSyntheticLambda31();
            }
            Collections.sort(arrayList, boostRepository$$ExternalSyntheticLambda31);
            Iterator it = hashMap.values().iterator();
            while (it.hasNext()) {
                Collections.sort((List) it.next(), new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$loadCountries$25;
                        lambda$loadCountries$25 = BoostRepository.lambda$loadCountries$25(boostRepository$$ExternalSyntheticLambda31, (TLRPC.TL_help_country) obj, (TLRPC.TL_help_country) obj2);
                        return lambda$loadCountries$25;
                    }
                });
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BoostRepository.lambda$loadCountries$26(Utilities.Callback.this, hashMap, arrayList);
                }
            });
        }
    }

    public static void lambda$loadGiftOptions$28(TLRPC.Chat chat, int i, List list, Utilities.Callback callback) {
        if (chat == null) {
            saveGiftOptionsToCache(i, list);
        }
        callback.run(list);
    }

    public static void lambda$loadGiftOptions$29(TLRPC.Chat chat, int i, List list, Utilities.Callback callback) {
        if (chat == null) {
            saveGiftOptionsToCache(i, list);
        }
        callback.run(list);
    }

    public static void lambda$loadGiftOptions$30(final List list, final TLRPC.Chat chat, final int i, final Utilities.Callback callback, BillingResult billingResult, List list2) {
        Iterator it = list2.iterator();
        while (it.hasNext()) {
            ProductDetails productDetails = (ProductDetails) it.next();
            ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = productDetails.getOneTimePurchaseOfferDetails();
            Iterator it2 = list.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption = (TLRPC.TL_premiumGiftCodeOption) it2.next();
                String str = tL_premiumGiftCodeOption.store_product;
                if (str != null && str.equals(productDetails.getProductId())) {
                    double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
                    double pow = Math.pow(10.0d, 6.0d);
                    Double.isNaN(priceAmountMicros);
                    tL_premiumGiftCodeOption.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_premiumGiftCodeOption.currency)));
                    tL_premiumGiftCodeOption.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
                    break;
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$loadGiftOptions$29(TLRPC.Chat.this, i, list, callback);
            }
        });
    }

    public static void lambda$loadGiftOptions$31(final TLRPC.Chat chat, final int i, final Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            final ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < vector.objects.size(); i2++) {
                TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption = (TLRPC.TL_premiumGiftCodeOption) vector.objects.get(i2);
                arrayList.add(tL_premiumGiftCodeOption);
                if (tL_premiumGiftCodeOption.store_product != null) {
                    arrayList2.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(tL_premiumGiftCodeOption.store_product).build());
                }
            }
            if (arrayList2.isEmpty() || !isGoogleBillingAvailable()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        BoostRepository.lambda$loadGiftOptions$28(TLRPC.Chat.this, i, arrayList, callback);
                    }
                });
            } else {
                BillingController.getInstance().queryProductDetails(arrayList2, new ProductDetailsResponseListener() {
                    @Override
                    public final void onProductDetailsResponse(BillingResult billingResult, List list) {
                        BoostRepository.lambda$loadGiftOptions$30(arrayList, chat, i, callback, billingResult, list);
                    }
                });
            }
        }
    }

    public static void lambda$loadParticipantsCount$1(MessagesStorage messagesStorage, final Utilities.Callback callback) {
        final HashMap<Long, Integer> smallGroupsParticipantsCount = messagesStorage.getSmallGroupsParticipantsCount();
        if (smallGroupsParticipantsCount == null || smallGroupsParticipantsCount.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(smallGroupsParticipantsCount);
            }
        });
    }

    public static void lambda$payGiftCodeByGoogle$10(final Utilities.Callback callback, final List list, final BillingResult billingResult, final Utilities.Callback callback2, final BaseFragment baseFragment, final TLRPC.TL_inputStorePaymentPremiumGiftCode tL_inputStorePaymentPremiumGiftCode, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$payGiftCodeByGoogle$9(TLRPC.TL_error.this, callback, tLObject, list, billingResult, callback2, baseFragment, tL_inputStorePaymentPremiumGiftCode);
            }
        });
    }

    public static void lambda$payGiftCodeByGoogle$11(final TLRPC.TL_inputStorePaymentPremiumGiftCode tL_inputStorePaymentPremiumGiftCode, TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, ConnectionsManager connectionsManager, final Utilities.Callback callback, final Utilities.Callback callback2, final BaseFragment baseFragment, final BillingResult billingResult, final List list) {
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = ((ProductDetails) list.get(0)).getOneTimePurchaseOfferDetails();
        tL_inputStorePaymentPremiumGiftCode.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
        double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
        double pow = Math.pow(10.0d, 6.0d);
        Double.isNaN(priceAmountMicros);
        tL_inputStorePaymentPremiumGiftCode.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_premiumGiftCodeOption.currency)));
        TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium = new TLRPC.TL_payments_canPurchasePremium();
        tL_payments_canPurchasePremium.purpose = tL_inputStorePaymentPremiumGiftCode;
        connectionsManager.sendRequest(tL_payments_canPurchasePremium, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$payGiftCodeByGoogle$10(Utilities.Callback.this, list, billingResult, callback2, baseFragment, tL_inputStorePaymentPremiumGiftCode, tLObject, tL_error);
            }
        });
    }

    public static void lambda$payGiftCodeByGoogle$6(BillingResult billingResult, final Utilities.Callback callback, BillingResult billingResult2) {
        if (billingResult.getResponseCode() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(null);
                }
            });
        }
    }

    public static void lambda$payGiftCodeByGoogle$8(final Utilities.Callback callback) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(null);
            }
        });
    }

    public static void lambda$payGiftCodeByGoogle$9(TLRPC.TL_error tL_error, final Utilities.Callback callback, TLObject tLObject, List list, final BillingResult billingResult, final Utilities.Callback callback2, BaseFragment baseFragment, TLRPC.TL_inputStorePaymentPremiumGiftCode tL_inputStorePaymentPremiumGiftCode) {
        if (tL_error != null) {
            callback.run(tL_error);
        } else if (tLObject != null) {
            BillingController.getInstance().addResultListener(((ProductDetails) list.get(0)).getProductId(), new Consumer() {
                @Override
                public final void accept(Object obj) {
                    BoostRepository.lambda$payGiftCodeByGoogle$6(BillingResult.this, callback2, (BillingResult) obj);
                }
            });
            BillingController.getInstance().setOnCanceled(new Runnable() {
                @Override
                public final void run() {
                    BoostRepository.lambda$payGiftCodeByGoogle$8(Utilities.Callback.this);
                }
            });
            BillingController.getInstance().launchBillingFlow(baseFragment.getParentActivity(), AccountInstance.getInstance(UserConfig.selectedAccount), tL_inputStorePaymentPremiumGiftCode, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails((ProductDetails) list.get(0)).build()));
        }
    }

    public static void lambda$payGiftCodeByInvoice$2(Utilities.Callback callback, Utilities.Callback callback2, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            callback.run(null);
        } else if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
            callback2.run(null);
        }
    }

    public static void lambda$payGiftCodeByInvoice$3(TLRPC.TL_error tL_error, final Utilities.Callback callback, TLObject tLObject, MessagesController messagesController, TLRPC.TL_inputInvoicePremiumGiftCode tL_inputInvoicePremiumGiftCode, BaseFragment baseFragment, final Utilities.Callback callback2) {
        PaymentFormActivity paymentFormActivity;
        if (tL_error != null) {
            callback.run(tL_error);
            return;
        }
        if (tLObject instanceof TLRPC.PaymentForm) {
            TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
            paymentForm.invoice.recurring = true;
            messagesController.putUsers(paymentForm.users, false);
            paymentFormActivity = new PaymentFormActivity(paymentForm, tL_inputInvoicePremiumGiftCode, baseFragment);
        } else {
            paymentFormActivity = tLObject instanceof TLRPC.PaymentReceipt ? new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject) : null;
        }
        if (paymentFormActivity == null) {
            callback.run(null);
        } else {
            paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                @Override
                public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                    BoostRepository.lambda$payGiftCodeByInvoice$2(Utilities.Callback.this, callback, invoiceStatus);
                }
            });
            LaunchActivity.getLastFragment().showAsSheet(paymentFormActivity, new BaseFragment.BottomSheetParams());
        }
    }

    public static void lambda$payGiftCodeByInvoice$4(final Utilities.Callback callback, final MessagesController messagesController, final TLRPC.TL_inputInvoicePremiumGiftCode tL_inputInvoicePremiumGiftCode, final BaseFragment baseFragment, final Utilities.Callback callback2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$payGiftCodeByInvoice$3(TLRPC.TL_error.this, callback, tLObject, messagesController, tL_inputInvoicePremiumGiftCode, baseFragment, callback2);
            }
        });
    }

    public static void lambda$payGiveAwayByGoogle$19(BillingResult billingResult, final Utilities.Callback callback, BillingResult billingResult2) {
        if (billingResult.getResponseCode() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(null);
                }
            });
        }
    }

    public static void lambda$payGiveAwayByGoogle$21(final Utilities.Callback callback) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(null);
            }
        });
    }

    public static void lambda$payGiveAwayByGoogle$22(TLRPC.TL_error tL_error, final Utilities.Callback callback, TLObject tLObject, List list, final BillingResult billingResult, final Utilities.Callback callback2, BaseFragment baseFragment, TLRPC.TL_inputStorePaymentPremiumGiveaway tL_inputStorePaymentPremiumGiveaway) {
        if (tL_error != null) {
            callback.run(tL_error);
        } else if (tLObject != null) {
            BillingController.getInstance().addResultListener(((ProductDetails) list.get(0)).getProductId(), new Consumer() {
                @Override
                public final void accept(Object obj) {
                    BoostRepository.lambda$payGiveAwayByGoogle$19(BillingResult.this, callback2, (BillingResult) obj);
                }
            });
            BillingController.getInstance().setOnCanceled(new Runnable() {
                @Override
                public final void run() {
                    BoostRepository.lambda$payGiveAwayByGoogle$21(Utilities.Callback.this);
                }
            });
            BillingController.getInstance().launchBillingFlow(baseFragment.getParentActivity(), AccountInstance.getInstance(UserConfig.selectedAccount), tL_inputStorePaymentPremiumGiveaway, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails((ProductDetails) list.get(0)).build()));
        }
    }

    public static void lambda$payGiveAwayByGoogle$23(final Utilities.Callback callback, final List list, final BillingResult billingResult, final Utilities.Callback callback2, final BaseFragment baseFragment, final TLRPC.TL_inputStorePaymentPremiumGiveaway tL_inputStorePaymentPremiumGiveaway, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$payGiveAwayByGoogle$22(TLRPC.TL_error.this, callback, tLObject, list, billingResult, callback2, baseFragment, tL_inputStorePaymentPremiumGiveaway);
            }
        });
    }

    public static void lambda$payGiveAwayByGoogle$24(final TLRPC.TL_inputStorePaymentPremiumGiveaway tL_inputStorePaymentPremiumGiveaway, TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, ConnectionsManager connectionsManager, final Utilities.Callback callback, final Utilities.Callback callback2, final BaseFragment baseFragment, final BillingResult billingResult, final List list) {
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = ((ProductDetails) list.get(0)).getOneTimePurchaseOfferDetails();
        tL_inputStorePaymentPremiumGiveaway.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
        double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
        double pow = Math.pow(10.0d, 6.0d);
        Double.isNaN(priceAmountMicros);
        tL_inputStorePaymentPremiumGiveaway.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_premiumGiftCodeOption.currency)));
        TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium = new TLRPC.TL_payments_canPurchasePremium();
        tL_payments_canPurchasePremium.purpose = tL_inputStorePaymentPremiumGiveaway;
        connectionsManager.sendRequest(tL_payments_canPurchasePremium, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$payGiveAwayByGoogle$23(Utilities.Callback.this, list, billingResult, callback2, baseFragment, tL_inputStorePaymentPremiumGiveaway, tLObject, tL_error);
            }
        });
    }

    public static void lambda$payGiveAwayByInvoice$15(Utilities.Callback callback, Utilities.Callback callback2, PaymentFormActivity.InvoiceStatus invoiceStatus) {
        if (invoiceStatus == PaymentFormActivity.InvoiceStatus.PAID) {
            callback.run(null);
        } else if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
            callback2.run(null);
        }
    }

    public static void lambda$payGiveAwayByInvoice$16(TLRPC.TL_error tL_error, final Utilities.Callback callback, TLObject tLObject, MessagesController messagesController, TLRPC.TL_inputInvoicePremiumGiftCode tL_inputInvoicePremiumGiftCode, BaseFragment baseFragment, final Utilities.Callback callback2) {
        PaymentFormActivity paymentFormActivity;
        if (tL_error != null) {
            callback.run(tL_error);
            return;
        }
        if (tLObject instanceof TLRPC.PaymentForm) {
            TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
            paymentForm.invoice.recurring = true;
            messagesController.putUsers(paymentForm.users, false);
            paymentFormActivity = new PaymentFormActivity(paymentForm, tL_inputInvoicePremiumGiftCode, baseFragment);
        } else {
            paymentFormActivity = tLObject instanceof TLRPC.PaymentReceipt ? new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject) : null;
        }
        if (paymentFormActivity == null) {
            callback.run(null);
        } else {
            paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                @Override
                public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                    BoostRepository.lambda$payGiveAwayByInvoice$15(Utilities.Callback.this, callback, invoiceStatus);
                }
            });
            LaunchActivity.getLastFragment().showAsSheet(paymentFormActivity, new BaseFragment.BottomSheetParams());
        }
    }

    public static void lambda$payGiveAwayByInvoice$17(final Utilities.Callback callback, final MessagesController messagesController, final TLRPC.TL_inputInvoicePremiumGiftCode tL_inputInvoicePremiumGiftCode, final BaseFragment baseFragment, final Utilities.Callback callback2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$payGiveAwayByInvoice$16(TLRPC.TL_error.this, callback, tLObject, messagesController, tL_inputInvoicePremiumGiftCode, baseFragment, callback2);
            }
        });
    }

    public static void lambda$searchChats$33(MessagesController messagesController, long j, final Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_contacts_found) {
            TLRPC.TL_contacts_found tL_contacts_found = (TLRPC.TL_contacts_found) tLObject;
            messagesController.putChats(tL_contacts_found.chats, false);
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_contacts_found.chats.size(); i++) {
                TLRPC.Chat chat = tL_contacts_found.chats.get(i);
                TLRPC.InputPeer inputPeer = MessagesController.getInputPeer(chat);
                if (chat.id != j && ChatObject.isBoostSupported(chat)) {
                    arrayList.add(inputPeer);
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(arrayList);
                }
            });
        }
    }

    public static void launchPreparedGiveaway(TL_stories.PrepaidGiveaway prepaidGiveaway, List list, List list2, TLRPC.Chat chat, int i, boolean z, boolean z2, boolean z3, int i2, String str, final Utilities.Callback callback, final Utilities.Callback callback2) {
        ?? tL_inputStorePaymentStarsGiveaway;
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        if (prepaidGiveaway instanceof TL_stories.TL_prepaidGiveaway) {
            tL_inputStorePaymentStarsGiveaway = new TLRPC.TL_inputStorePaymentPremiumGiveaway();
            tL_inputStorePaymentStarsGiveaway.only_new_subscribers = z;
            tL_inputStorePaymentStarsGiveaway.winners_are_visible = z2;
            tL_inputStorePaymentStarsGiveaway.prize_description = str;
            tL_inputStorePaymentStarsGiveaway.until_date = i;
            int i3 = tL_inputStorePaymentStarsGiveaway.flags;
            tL_inputStorePaymentStarsGiveaway.flags = i3 | 6;
            if (z3) {
                tL_inputStorePaymentStarsGiveaway.flags = i3 | 22;
            }
            tL_inputStorePaymentStarsGiveaway.random_id = System.currentTimeMillis();
            tL_inputStorePaymentStarsGiveaway.additional_peers = new ArrayList<>();
            tL_inputStorePaymentStarsGiveaway.boost_peer = messagesController.getInputPeer(-chat.id);
            tL_inputStorePaymentStarsGiveaway.currency = "";
            Iterator it = list2.iterator();
            while (it.hasNext()) {
                tL_inputStorePaymentStarsGiveaway.countries_iso2.add(((TLRPC.TL_help_country) ((TLObject) it.next())).iso2);
            }
            Iterator it2 = list.iterator();
            while (it2.hasNext()) {
                TLObject tLObject = (TLObject) it2.next();
                if (tLObject instanceof TLRPC.Chat) {
                    tL_inputStorePaymentStarsGiveaway.additional_peers.add(messagesController.getInputPeer(-((TLRPC.Chat) tLObject).id));
                }
            }
        } else {
            if (!(prepaidGiveaway instanceof TL_stories.TL_prepaidStarsGiveaway)) {
                return;
            }
            tL_inputStorePaymentStarsGiveaway = new TLRPC.TL_inputStorePaymentStarsGiveaway();
            tL_inputStorePaymentStarsGiveaway.only_new_subscribers = z;
            tL_inputStorePaymentStarsGiveaway.winners_are_visible = z2;
            tL_inputStorePaymentStarsGiveaway.prize_description = str;
            tL_inputStorePaymentStarsGiveaway.until_date = i;
            int i4 = tL_inputStorePaymentStarsGiveaway.flags;
            tL_inputStorePaymentStarsGiveaway.flags = i4 | 6;
            if (z3) {
                tL_inputStorePaymentStarsGiveaway.flags = i4 | 22;
            }
            tL_inputStorePaymentStarsGiveaway.random_id = System.currentTimeMillis();
            tL_inputStorePaymentStarsGiveaway.additional_peers = new ArrayList<>();
            tL_inputStorePaymentStarsGiveaway.boost_peer = messagesController.getInputPeer(-chat.id);
            tL_inputStorePaymentStarsGiveaway.currency = "";
            tL_inputStorePaymentStarsGiveaway.stars = ((TL_stories.TL_prepaidStarsGiveaway) prepaidGiveaway).stars;
            tL_inputStorePaymentStarsGiveaway.users = prepaidGiveaway.quantity;
            Iterator it3 = list2.iterator();
            while (it3.hasNext()) {
                tL_inputStorePaymentStarsGiveaway.countries_iso2.add(((TLRPC.TL_help_country) ((TLObject) it3.next())).iso2);
            }
            Iterator it4 = list.iterator();
            while (it4.hasNext()) {
                TLObject tLObject2 = (TLObject) it4.next();
                if (tLObject2 instanceof TLRPC.Chat) {
                    tL_inputStorePaymentStarsGiveaway.additional_peers.add(messagesController.getInputPeer(-((TLRPC.Chat) tLObject2).id));
                }
            }
        }
        TLRPC.TL_payments_launchPrepaidGiveaway tL_payments_launchPrepaidGiveaway = new TLRPC.TL_payments_launchPrepaidGiveaway();
        tL_payments_launchPrepaidGiveaway.giveaway_id = prepaidGiveaway.id;
        tL_payments_launchPrepaidGiveaway.peer = messagesController.getInputPeer(-chat.id);
        tL_payments_launchPrepaidGiveaway.purpose = tL_inputStorePaymentStarsGiveaway;
        connectionsManager.sendRequest(tL_payments_launchPrepaidGiveaway, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject3, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$launchPreparedGiveaway$14(Utilities.Callback.this, messagesController, callback, tLObject3, tL_error);
            }
        });
    }

    public static void loadChatParticipants(long j, int i, String str, int i2, int i3, final Utilities.Callback callback) {
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = messagesController.getInputChannel(j);
        TLRPC.ChannelParticipantsFilter tL_channelParticipantsRecent = str == null ? new TLRPC.TL_channelParticipantsRecent() : new TLRPC.TL_channelParticipantsSearch();
        tL_channels_getParticipants.filter = tL_channelParticipantsRecent;
        if (str == null) {
            str = "";
        }
        tL_channelParticipantsRecent.q = str;
        tL_channels_getParticipants.offset = i2;
        tL_channels_getParticipants.limit = i3;
        connectionsManager.sendRequest(tL_channels_getParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$loadChatParticipants$35(MessagesController.this, callback, tLObject, tL_error);
            }
        });
    }

    public static void loadCountries(final Utilities.Callback callback) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_help_getCountriesList tL_help_getCountriesList = new TLRPC.TL_help_getCountriesList();
        tL_help_getCountriesList.lang_code = LocaleController.getInstance().getCurrentLocaleInfo() != null ? LocaleController.getInstance().getCurrentLocaleInfo().getLangCode() : Locale.getDefault().getCountry();
        connectionsManager.sendRequest(tL_help_getCountriesList, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$loadCountries$27(Utilities.Callback.this, tLObject, tL_error);
            }
        });
    }

    public static int loadGiftOptions(final int i, final TLRPC.Chat chat, final Utilities.Callback callback) {
        List cachedGiftOptions2;
        if (chat == null && (cachedGiftOptions2 = getCachedGiftOptions(i)) != null) {
            callback.run(cachedGiftOptions2);
            return -1;
        }
        MessagesController messagesController = MessagesController.getInstance(i);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(i);
        TLRPC.TL_payments_getPremiumGiftCodeOptions tL_payments_getPremiumGiftCodeOptions = new TLRPC.TL_payments_getPremiumGiftCodeOptions();
        if (chat != null) {
            tL_payments_getPremiumGiftCodeOptions.flags = 1;
            tL_payments_getPremiumGiftCodeOptions.boost_peer = messagesController.getInputPeer(-chat.id);
        }
        return connectionsManager.sendRequest(tL_payments_getPremiumGiftCodeOptions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$loadGiftOptions$31(TLRPC.Chat.this, i, callback, tLObject, tL_error);
            }
        });
    }

    public static void loadParticipantsCount(final Utilities.Callback callback) {
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(UserConfig.selectedAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                BoostRepository.lambda$loadParticipantsCount$1(MessagesStorage.this, callback);
            }
        });
    }

    public static void payGiftCode(List list, TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, TLRPC.Chat chat, TLRPC.TL_textWithEntities tL_textWithEntities, BaseFragment baseFragment, Utilities.Callback callback, Utilities.Callback callback2) {
        invalidateGiftOptionsToCache(UserConfig.selectedAccount);
        if (isGoogleBillingAvailable()) {
            payGiftCodeByGoogle(list, tL_premiumGiftCodeOption, chat, tL_textWithEntities, baseFragment, callback, callback2);
        } else {
            payGiftCodeByInvoice(list, tL_premiumGiftCodeOption, chat, tL_textWithEntities, baseFragment, callback, callback2);
        }
    }

    public static void payGiftCodeByGoogle(List list, final TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, TLRPC.Chat chat, TLRPC.TL_textWithEntities tL_textWithEntities, final BaseFragment baseFragment, final Utilities.Callback callback, final Utilities.Callback callback2) {
        MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        final ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        final TLRPC.TL_inputStorePaymentPremiumGiftCode tL_inputStorePaymentPremiumGiftCode = new TLRPC.TL_inputStorePaymentPremiumGiftCode();
        tL_inputStorePaymentPremiumGiftCode.users = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLObject tLObject = (TLObject) it.next();
            if (tLObject instanceof TLRPC.User) {
                tL_inputStorePaymentPremiumGiftCode.users.add(messagesController.getInputUser((TLRPC.User) tLObject));
            }
        }
        if (chat != null) {
            tL_inputStorePaymentPremiumGiftCode.flags = 1;
            tL_inputStorePaymentPremiumGiftCode.boost_peer = messagesController.getInputPeer(-chat.id);
        }
        if (tL_textWithEntities != null && !TextUtils.isEmpty(tL_textWithEntities.text)) {
            tL_inputStorePaymentPremiumGiftCode.flags |= 2;
            tL_inputStorePaymentPremiumGiftCode.message = tL_textWithEntities;
        }
        BillingController.getInstance().queryProductDetails(Arrays.asList(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(tL_premiumGiftCodeOption.store_product).build()), new ProductDetailsResponseListener() {
            @Override
            public final void onProductDetailsResponse(BillingResult billingResult, List list2) {
                BoostRepository.lambda$payGiftCodeByGoogle$11(TLRPC.TL_inputStorePaymentPremiumGiftCode.this, tL_premiumGiftCodeOption, connectionsManager, callback2, callback, baseFragment, billingResult, list2);
            }
        });
    }

    public static void payGiftCodeByInvoice(List list, TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, TLRPC.Chat chat, TLRPC.TL_textWithEntities tL_textWithEntities, final BaseFragment baseFragment, final Utilities.Callback callback, final Utilities.Callback callback2) {
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        final TLRPC.TL_inputInvoicePremiumGiftCode tL_inputInvoicePremiumGiftCode = new TLRPC.TL_inputInvoicePremiumGiftCode();
        TLRPC.TL_inputStorePaymentPremiumGiftCode tL_inputStorePaymentPremiumGiftCode = new TLRPC.TL_inputStorePaymentPremiumGiftCode();
        tL_inputStorePaymentPremiumGiftCode.users = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLObject tLObject = (TLObject) it.next();
            if (tLObject instanceof TLRPC.User) {
                tL_inputStorePaymentPremiumGiftCode.users.add(messagesController.getInputUser((TLRPC.User) tLObject));
            }
        }
        if (tL_textWithEntities != null && !TextUtils.isEmpty(tL_textWithEntities.text)) {
            tL_inputStorePaymentPremiumGiftCode.flags |= 2;
            tL_inputStorePaymentPremiumGiftCode.message = tL_textWithEntities;
        }
        if (chat != null) {
            tL_inputStorePaymentPremiumGiftCode.flags |= 1;
            tL_inputStorePaymentPremiumGiftCode.boost_peer = messagesController.getInputPeer(-chat.id);
        }
        tL_inputStorePaymentPremiumGiftCode.currency = tL_premiumGiftCodeOption.currency;
        tL_inputStorePaymentPremiumGiftCode.amount = tL_premiumGiftCodeOption.amount;
        tL_inputInvoicePremiumGiftCode.purpose = tL_inputStorePaymentPremiumGiftCode;
        tL_inputInvoicePremiumGiftCode.option = tL_premiumGiftCodeOption;
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(baseFragment.getResourceProvider());
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoicePremiumGiftCode;
        connectionsManager.sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$payGiftCodeByInvoice$4(Utilities.Callback.this, messagesController, tL_inputInvoicePremiumGiftCode, baseFragment, callback, tLObject2, tL_error);
            }
        });
    }

    public static void payGiveAway(List list, List list2, TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, TLRPC.Chat chat, int i, boolean z, BaseFragment baseFragment, boolean z2, boolean z3, String str, Utilities.Callback callback, Utilities.Callback callback2) {
        if (isGoogleBillingAvailable()) {
            payGiveAwayByGoogle(list, list2, tL_premiumGiftCodeOption, chat, i, z, baseFragment, z2, z3, str, callback, callback2);
        } else {
            payGiveAwayByInvoice(list, list2, tL_premiumGiftCodeOption, chat, i, z, baseFragment, z2, z3, str, callback, callback2);
        }
    }

    public static void payGiveAwayByGoogle(List list, List list2, final TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, TLRPC.Chat chat, int i, boolean z, final BaseFragment baseFragment, boolean z2, boolean z3, String str, final Utilities.Callback callback, final Utilities.Callback callback2) {
        MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        final ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        final TLRPC.TL_inputStorePaymentPremiumGiveaway tL_inputStorePaymentPremiumGiveaway = new TLRPC.TL_inputStorePaymentPremiumGiveaway();
        tL_inputStorePaymentPremiumGiveaway.only_new_subscribers = z;
        tL_inputStorePaymentPremiumGiveaway.winners_are_visible = z2;
        tL_inputStorePaymentPremiumGiveaway.prize_description = str;
        tL_inputStorePaymentPremiumGiveaway.until_date = i;
        int i2 = tL_inputStorePaymentPremiumGiveaway.flags;
        tL_inputStorePaymentPremiumGiveaway.flags = i2 | 6;
        if (z3) {
            tL_inputStorePaymentPremiumGiveaway.flags = i2 | 22;
        }
        tL_inputStorePaymentPremiumGiveaway.random_id = System.currentTimeMillis();
        tL_inputStorePaymentPremiumGiveaway.additional_peers = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLObject tLObject = (TLObject) it.next();
            if (tLObject instanceof TLRPC.Chat) {
                tL_inputStorePaymentPremiumGiveaway.additional_peers.add(messagesController.getInputPeer(-((TLRPC.Chat) tLObject).id));
            }
        }
        tL_inputStorePaymentPremiumGiveaway.boost_peer = messagesController.getInputPeer(-chat.id);
        Iterator it2 = list2.iterator();
        while (it2.hasNext()) {
            tL_inputStorePaymentPremiumGiveaway.countries_iso2.add(((TLRPC.TL_help_country) ((TLObject) it2.next())).iso2);
        }
        BillingController.getInstance().queryProductDetails(Arrays.asList(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(tL_premiumGiftCodeOption.store_product).build()), new ProductDetailsResponseListener() {
            @Override
            public final void onProductDetailsResponse(BillingResult billingResult, List list3) {
                BoostRepository.lambda$payGiveAwayByGoogle$24(TLRPC.TL_inputStorePaymentPremiumGiveaway.this, tL_premiumGiftCodeOption, connectionsManager, callback2, callback, baseFragment, billingResult, list3);
            }
        });
    }

    public static void payGiveAwayByInvoice(List list, List list2, TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption, TLRPC.Chat chat, int i, boolean z, final BaseFragment baseFragment, boolean z2, boolean z3, String str, final Utilities.Callback callback, final Utilities.Callback callback2) {
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        final TLRPC.TL_inputInvoicePremiumGiftCode tL_inputInvoicePremiumGiftCode = new TLRPC.TL_inputInvoicePremiumGiftCode();
        TLRPC.TL_inputStorePaymentPremiumGiveaway tL_inputStorePaymentPremiumGiveaway = new TLRPC.TL_inputStorePaymentPremiumGiveaway();
        tL_inputStorePaymentPremiumGiveaway.only_new_subscribers = z;
        tL_inputStorePaymentPremiumGiveaway.winners_are_visible = z2;
        tL_inputStorePaymentPremiumGiveaway.prize_description = str;
        tL_inputStorePaymentPremiumGiveaway.until_date = i;
        int i2 = tL_inputStorePaymentPremiumGiveaway.flags;
        tL_inputStorePaymentPremiumGiveaway.flags = i2 | 6;
        if (z3) {
            tL_inputStorePaymentPremiumGiveaway.flags = i2 | 22;
        }
        tL_inputStorePaymentPremiumGiveaway.random_id = System.currentTimeMillis();
        tL_inputStorePaymentPremiumGiveaway.additional_peers = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLObject tLObject = (TLObject) it.next();
            if (tLObject instanceof TLRPC.Chat) {
                tL_inputStorePaymentPremiumGiveaway.additional_peers.add(messagesController.getInputPeer(-((TLRPC.Chat) tLObject).id));
            }
        }
        tL_inputStorePaymentPremiumGiveaway.boost_peer = messagesController.getInputPeer(-chat.id);
        tL_inputStorePaymentPremiumGiveaway.boost_peer = messagesController.getInputPeer(-chat.id);
        tL_inputStorePaymentPremiumGiveaway.currency = tL_premiumGiftCodeOption.currency;
        tL_inputStorePaymentPremiumGiveaway.amount = tL_premiumGiftCodeOption.amount;
        Iterator it2 = list2.iterator();
        while (it2.hasNext()) {
            tL_inputStorePaymentPremiumGiveaway.countries_iso2.add(((TLRPC.TL_help_country) ((TLObject) it2.next())).iso2);
        }
        tL_inputInvoicePremiumGiftCode.purpose = tL_inputStorePaymentPremiumGiveaway;
        tL_inputInvoicePremiumGiftCode.option = tL_premiumGiftCodeOption;
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(baseFragment.getResourceProvider());
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        tL_payments_getPaymentForm.invoice = tL_inputInvoicePremiumGiftCode;
        connectionsManager.sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$payGiveAwayByInvoice$17(Utilities.Callback.this, messagesController, tL_inputInvoicePremiumGiftCode, baseFragment, callback, tLObject2, tL_error);
            }
        });
    }

    public static int prepareServerDate(long j) {
        if (j < System.currentTimeMillis() + 120000) {
            j = System.currentTimeMillis() + 120000;
        }
        return (int) (j / 1000);
    }

    public static void saveGiftOptionsToCache(int i, List list) {
        if (cachedGiftOptions == null) {
            cachedGiftOptions = new HashMap();
        }
        cachedGiftOptions.put(Integer.valueOf(i), new Pair(Long.valueOf(System.currentTimeMillis()), list));
    }

    public static void searchChats(final long j, int i, String str, int i2, final Utilities.Callback callback) {
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_contacts_search tL_contacts_search = new TLRPC.TL_contacts_search();
        tL_contacts_search.q = str;
        tL_contacts_search.limit = 50;
        connectionsManager.sendRequest(tL_contacts_search, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BoostRepository.lambda$searchChats$33(MessagesController.this, j, callback, tLObject, tL_error);
            }
        });
    }

    public static int searchContacts(int i, String str, Utilities.Callback callback) {
        TLRPC.User user;
        int i2 = UserConfig.selectedAccount;
        ArrayList arrayList = new ArrayList();
        ArrayList<TLRPC.TL_contact> arrayList2 = ContactsController.getInstance(i2).contacts;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            ContactsController.getInstance(i2).loadContacts(false, 0L);
        }
        MessagesController messagesController = MessagesController.getInstance(i2);
        String lowerCase = str.toLowerCase();
        String translitSafe = AndroidUtilities.translitSafe(lowerCase);
        if (arrayList2 != null) {
            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                TLRPC.TL_contact tL_contact = arrayList2.get(i3);
                if (tL_contact != null && (user = messagesController.getUser(Long.valueOf(tL_contact.user_id))) != null && !user.bot && !UserObject.isService(user.id) && !UserObject.isUserSelf(user)) {
                    String lowerCase2 = UserObject.getUserName(user).toLowerCase();
                    String translitSafe2 = AndroidUtilities.translitSafe(lowerCase2);
                    if (!lowerCase2.startsWith(lowerCase)) {
                        if (!lowerCase2.contains(" " + lowerCase) && !translitSafe2.startsWith(translitSafe)) {
                            if (!translitSafe2.contains(" " + translitSafe)) {
                                if (user.usernames != null) {
                                    for (int i4 = 0; i4 < user.usernames.size(); i4++) {
                                        TLRPC.TL_username tL_username = user.usernames.get(i4);
                                        if (tL_username != null && tL_username.active) {
                                            String lowerCase3 = tL_username.username.toLowerCase();
                                            if (!lowerCase3.startsWith(lowerCase)) {
                                                if (!lowerCase3.contains("_" + lowerCase) && !lowerCase3.startsWith(translitSafe)) {
                                                    if (lowerCase3.contains(" " + translitSafe)) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    String str2 = user.username;
                                    if (str2 != null) {
                                        String lowerCase4 = str2.toLowerCase();
                                        if (!lowerCase4.startsWith(lowerCase)) {
                                            if (!lowerCase4.contains("_" + lowerCase) && !lowerCase4.startsWith(translitSafe)) {
                                                if (!lowerCase4.contains(" " + translitSafe)) {
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    arrayList.add(user);
                    break;
                }
            }
        }
        callback.run(arrayList);
        return -1;
    }
}
