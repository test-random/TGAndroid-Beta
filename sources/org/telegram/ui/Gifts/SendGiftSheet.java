package org.telegram.ui.Gifts;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.EditEmojiTextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$GiftTier;
import org.telegram.ui.Components.Premium.boosts.BoostDialogs;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.Premium.boosts.PremiumPreviewGiftSentBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class SendGiftSheet extends BottomSheetWithRecyclerListView {
    private final TLRPC.MessageAction action;
    private final ChatActionCell actionCell;
    private UniversalAdapter adapter;
    public final AnimationNotificationsLocker animationsLock;
    public boolean anonymous;
    private final ButtonWithCounterView button;
    private final LinearLayout buttonContainer;
    private final SizeNotifierFrameLayout chatView;
    private final Runnable closeParentSheet;
    private final int currentAccount;
    private final long dialogId;
    private final TextView leftTextView;
    private final TextView leftTextView2;
    private final FrameLayout limitContainer;
    private final View limitProgressView;
    private EditEmojiTextCell messageEdit;
    private final MessageObject messageObject;
    private final String name;
    private final GiftPremiumBottomSheet$GiftTier premiumTier;
    private final TextView soldTextView;
    private final TextView soldTextView2;
    private final TL_stars.StarGift starGift;
    private final FrameLayout valueContainerView;

    public SendGiftSheet(Context context, int i, TL_stars.StarGift starGift, long j, Runnable runnable) {
        this(context, i, starGift, null, j, runnable);
    }

    private SendGiftSheet(android.content.Context r30, final int r31, final org.telegram.tgnet.tl.TL_stars.StarGift r32, org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$GiftTier r33, long r34, java.lang.Runnable r36) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Gifts.SendGiftSheet.<init>(android.content.Context, int, org.telegram.tgnet.tl.TL_stars$StarGift, org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$GiftTier, long, java.lang.Runnable):void");
    }

    public SendGiftSheet(Context context, int i, GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier, long j, Runnable runnable) {
        this(context, i, null, giftPremiumBottomSheet$GiftTier, j, runnable);
    }

    private void buyPremiumTier() {
        final TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialogId));
        if (user == null) {
            this.button.setLoading(false);
            return;
        }
        if (this.premiumTier.giftCodeOption != null) {
            BoostRepository.payGiftCode(new ArrayList(Arrays.asList(user)), this.premiumTier.giftCodeOption, null, getMessage(), new BaseFragment() {
                @Override
                public Activity getParentActivity() {
                    Activity ownerActivity = SendGiftSheet.this.getOwnerActivity();
                    if (ownerActivity == null) {
                        ownerActivity = LaunchActivity.instance;
                    }
                    return ownerActivity == null ? AndroidUtilities.findActivity(SendGiftSheet.this.getContext()) : ownerActivity;
                }

                @Override
                public Theme.ResourcesProvider getResourceProvider() {
                    return ((BottomSheet) SendGiftSheet.this).resourcesProvider;
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    SendGiftSheet.this.lambda$buyPremiumTier$4(user, (Void) obj);
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    SendGiftSheet.this.lambda$buyPremiumTier$5((TLRPC.TL_error) obj);
                }
            });
            return;
        }
        if (BuildVars.useInvoiceBilling()) {
            LaunchActivity launchActivity = LaunchActivity.instance;
            if (launchActivity != null) {
                Uri parse = Uri.parse(this.premiumTier.giftOption.bot_url);
                if (parse.getHost().equals("t.me")) {
                    if (parse.getPath().startsWith("/$") || parse.getPath().startsWith("/invoice/")) {
                        launchActivity.setNavigateToPremiumGiftCallback(new Runnable() {
                            @Override
                            public final void run() {
                                SendGiftSheet.this.lambda$buyPremiumTier$6();
                            }
                        });
                    } else {
                        launchActivity.setNavigateToPremiumBot(true);
                    }
                }
                Browser.openUrl(launchActivity, this.premiumTier.giftOption.bot_url);
                dismiss();
                return;
            }
            return;
        }
        if (!BillingController.getInstance().isReady() || this.premiumTier.googlePlayProductDetails == null) {
            return;
        }
        final TLRPC.TL_inputStorePaymentGiftPremium tL_inputStorePaymentGiftPremium = new TLRPC.TL_inputStorePaymentGiftPremium();
        tL_inputStorePaymentGiftPremium.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
        ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = this.premiumTier.googlePlayProductDetails.getOneTimePurchaseOfferDetails();
        tL_inputStorePaymentGiftPremium.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
        double priceAmountMicros = oneTimePurchaseOfferDetails.getPriceAmountMicros();
        double pow = Math.pow(10.0d, 6.0d);
        Double.isNaN(priceAmountMicros);
        tL_inputStorePaymentGiftPremium.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_inputStorePaymentGiftPremium.currency)));
        BillingController.getInstance().addResultListener(this.premiumTier.giftOption.store_product, new Consumer() {
            @Override
            public final void accept(Object obj) {
                SendGiftSheet.this.lambda$buyPremiumTier$8((BillingResult) obj);
            }
        });
        final TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium = new TLRPC.TL_payments_canPurchasePremium();
        tL_payments_canPurchasePremium.purpose = tL_inputStorePaymentGiftPremium;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_canPurchasePremium, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendGiftSheet.this.lambda$buyPremiumTier$10(tL_inputStorePaymentGiftPremium, tL_payments_canPurchasePremium, tLObject, tL_error);
            }
        });
    }

    private void buyStarGift() {
        StarsController.getInstance(this.currentAccount).buyStarGift(AndroidUtilities.getActivity(getContext()), this.starGift, this.anonymous, this.dialogId, getMessage(), new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                SendGiftSheet.this.lambda$buyStarGift$2((Boolean) obj, (String) obj2);
            }
        });
    }

    private TLRPC.TL_textWithEntities getMessage() {
        TLRPC.MessageAction messageAction = this.action;
        if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
            return ((TLRPC.TL_messageActionStarGift) messageAction).message;
        }
        if (messageAction instanceof TLRPC.TL_messageActionGiftCode) {
            return ((TLRPC.TL_messageActionGiftCode) messageAction).message;
        }
        if (messageAction instanceof TLRPC.TL_messageActionGiftPremium) {
            return ((TLRPC.TL_messageActionGiftPremium) messageAction).message;
        }
        return null;
    }

    public void lambda$buyPremiumTier$10(final TLRPC.TL_inputStorePaymentGiftPremium tL_inputStorePaymentGiftPremium, final TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SendGiftSheet.this.lambda$buyPremiumTier$9(tLObject, tL_inputStorePaymentGiftPremium, tL_error, tL_payments_canPurchasePremium);
            }
        });
    }

    public static void lambda$buyPremiumTier$3(TLRPC.User user) {
        PremiumPreviewGiftSentBottomSheet.show(new ArrayList(Arrays.asList(user)));
    }

    public void lambda$buyPremiumTier$4(final TLRPC.User user, Void r6) {
        Runnable runnable = this.closeParentSheet;
        if (runnable != null) {
            runnable.run();
        }
        dismiss();
        NotificationCenter.getInstance(UserConfig.selectedAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.giftsToUserSent, new Object[0]);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SendGiftSheet.lambda$buyPremiumTier$3(TLRPC.User.this);
            }
        }, 250L);
        MessagesController.getInstance(this.currentAccount).getMainSettings().edit().putBoolean("show_gift_for_" + this.dialogId, true).putBoolean(Calendar.getInstance().get(1) + "show_gift_for_" + this.dialogId, true).apply();
    }

    public void lambda$buyPremiumTier$5(TLRPC.TL_error tL_error) {
        BoostDialogs.showToastError(getContext(), tL_error);
    }

    public void lambda$buyPremiumTier$6() {
        onGiftSuccess(false);
    }

    public void lambda$buyPremiumTier$7() {
        onGiftSuccess(true);
    }

    public void lambda$buyPremiumTier$8(BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SendGiftSheet.this.lambda$buyPremiumTier$7();
                }
            });
        }
    }

    public void lambda$buyPremiumTier$9(TLObject tLObject, TLRPC.TL_inputStorePaymentGiftPremium tL_inputStorePaymentGiftPremium, TLRPC.TL_error tL_error, TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(getBaseFragment().getParentActivity(), AccountInstance.getInstance(this.currentAccount), tL_inputStorePaymentGiftPremium, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(this.premiumTier.googlePlayProductDetails).build()));
        } else if (tL_error != null) {
            AlertsCreator.processError(this.currentAccount, tL_error, getBaseFragment(), tL_payments_canPurchasePremium, new Object[0]);
        }
    }

    public void lambda$buyStarGift$2(Boolean bool, String str) {
        if (bool.booleanValue()) {
            Runnable runnable = this.closeParentSheet;
            if (runnable != null) {
                runnable.run();
            }
            AndroidUtilities.hideKeyboard(this.messageEdit);
            dismiss();
        } else if ("STARGIFT_USAGE_LIMITED".equalsIgnoreCase(str)) {
            AndroidUtilities.hideKeyboard(this.messageEdit);
            dismiss();
            StarsController.getInstance(this.currentAccount).makeStarGiftSoldOut(this.starGift);
            return;
        }
        this.button.setLoading(false);
    }

    public void lambda$new$0(View view, int i) {
        UniversalAdapter universalAdapter = this.adapter;
        if (!this.reverseLayout) {
            i--;
        }
        UItem item = universalAdapter.getItem(i);
        if (item != null && item.id == 1) {
            boolean z = !this.anonymous;
            this.anonymous = z;
            TLRPC.MessageAction messageAction = this.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                ((TLRPC.TL_messageActionStarGift) messageAction).name_hidden = z;
            }
            this.messageObject.updateMessageText();
            this.actionCell.setMessageObject(this.messageObject, true);
            this.adapter.update(true);
        }
    }

    public void lambda$new$1(TL_stars.StarGift starGift, View view) {
        if (this.button.isLoading()) {
            return;
        }
        this.button.setLoading(true);
        if (this.messageEdit.editTextEmoji.getEmojiPadding() > 0) {
            this.messageEdit.editTextEmoji.hidePopup(true);
        } else if (this.messageEdit.editTextEmoji.isKeyboardVisible()) {
            this.messageEdit.editTextEmoji.closeKeyboard();
        }
        if (starGift != null) {
            buyStarGift();
        } else {
            buyPremiumTier();
        }
    }

    private void onGiftSuccess(boolean z) {
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.dialogId);
        TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(this.dialogId);
        if (userFull != null && (userOrChat instanceof TLRPC.User)) {
            TLRPC.User user = (TLRPC.User) userOrChat;
            user.premium = true;
            MessagesController.getInstance(this.currentAccount).putUser(user, true);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(user.id), userFull);
        }
        if (getBaseFragment() != null) {
            ArrayList<BaseFragment> arrayList = new ArrayList(((LaunchActivity) getBaseFragment().getParentActivity()).getActionBarLayout().getFragmentStack());
            INavigationLayout parentLayout = getBaseFragment().getParentLayout();
            ChatActivity chatActivity = null;
            for (BaseFragment baseFragment : arrayList) {
                if (baseFragment instanceof ChatActivity) {
                    chatActivity = (ChatActivity) baseFragment;
                    if (chatActivity.getDialogId() != this.dialogId) {
                        baseFragment.removeSelfFromStack();
                    }
                } else if (baseFragment instanceof ProfileActivity) {
                    if (z && parentLayout.getLastFragment() == baseFragment) {
                        baseFragment.lambda$onBackPressed$321();
                    }
                    baseFragment.removeSelfFromStack();
                }
            }
            if (chatActivity == null || chatActivity.getDialogId() != this.dialogId) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", this.dialogId);
                parentLayout.presentFragment(new ChatActivity(bundle), true);
            }
        }
        dismiss();
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        UniversalAdapter universalAdapter = new UniversalAdapter(this.recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                SendGiftSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, this.resourcesProvider);
        this.adapter = universalAdapter;
        universalAdapter.setApplyBackground(false);
        return this.adapter;
    }

    @Override
    public void dismiss() {
        if (this.messageEdit.editTextEmoji.getEmojiPadding() > 0) {
            this.messageEdit.editTextEmoji.hidePopup(true);
            return;
        }
        if (this.messageEdit.editTextEmoji.isKeyboardVisible()) {
            this.messageEdit.editTextEmoji.closeKeyboard();
            return;
        }
        EditEmojiTextCell editEmojiTextCell = this.messageEdit;
        if (editEmojiTextCell != null) {
            editEmojiTextCell.editTextEmoji.onPause();
        }
        super.dismiss();
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        UItem asShadow;
        arrayList.add(UItem.asCustom(-1, this.chatView));
        arrayList.add(UItem.asCustom(-2, this.messageEdit));
        if (this.starGift != null) {
            arrayList.add(UItem.asShadow(-3, null));
            arrayList.add(UItem.asCheck(1, LocaleController.getString(R.string.Gift2Hide)).setChecked(this.anonymous));
            asShadow = UItem.asShadow(-4, LocaleController.formatString(R.string.Gift2HideInfo, this.name));
        } else {
            asShadow = UItem.asShadow(-3, LocaleController.formatString(R.string.Gift2MessagePremiumInfo, this.name));
        }
        arrayList.add(asShadow);
        if (this.reverseLayout) {
            Collections.reverse(arrayList);
        }
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.Gift2Title);
    }

    @Override
    public void onBackPressed() {
        if (this.messageEdit.editTextEmoji.getEmojiPadding() > 0) {
            this.messageEdit.editTextEmoji.hidePopup(true);
        } else if (this.messageEdit.editTextEmoji.isKeyboardVisible()) {
            this.messageEdit.editTextEmoji.closeKeyboard();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void show() {
        EditEmojiTextCell editEmojiTextCell = this.messageEdit;
        if (editEmojiTextCell != null) {
            editEmojiTextCell.editTextEmoji.onResume();
        }
        super.show();
    }
}
