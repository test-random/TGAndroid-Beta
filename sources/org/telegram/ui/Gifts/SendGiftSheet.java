package org.telegram.ui.Gifts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
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
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextSuggestionsFix;
import org.telegram.ui.Components.LayoutHelper;
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
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.PreviewView;

public class SendGiftSheet extends BottomSheetWithRecyclerListView {
    private final TLRPC.MessageAction action;
    private final ChatActionCell actionCell;
    private UniversalAdapter adapter;
    public final AnimationNotificationsLocker animationsLock;
    public boolean anonymous;
    private final ButtonWithCounterView button;
    private final LinearLayout buttonContainer;
    private final ColoredImageSpan[] cachedStarSpan;
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
    private final boolean self;
    private final TextView soldTextView;
    private final TextView soldTextView2;
    private final TL_stars.StarGift starGift;
    public boolean upgrade;
    private final FrameLayout valueContainerView;

    public SendGiftSheet(Context context, int i, TL_stars.StarGift starGift, long j, Runnable runnable) {
        this(context, i, starGift, null, j, runnable);
    }

    private SendGiftSheet(Context context, final int i, final TL_stars.StarGift starGift, GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier, long j, Runnable runnable) {
        super(context, null, true, false, false, false, BottomSheetWithRecyclerListView.ActionBarType.SLIDING, null);
        ChatActionCell chatActionCell;
        boolean z;
        CharSequence formatString;
        this.upgrade = false;
        this.animationsLock = new AnimationNotificationsLocker();
        ColoredImageSpan[] coloredImageSpanArr = new ColoredImageSpan[1];
        this.cachedStarSpan = coloredImageSpanArr;
        boolean z2 = j == UserConfig.getInstance(i).getClientUserId();
        this.self = z2;
        setImageReceiverNumLevel(0, 4);
        fixNavigationBar();
        this.headerPaddingTop = AndroidUtilities.dp(4.0f);
        this.headerPaddingBottom = AndroidUtilities.dp(-10.0f);
        if (z2) {
            this.anonymous = true;
        }
        this.currentAccount = i;
        this.dialogId = j;
        this.starGift = starGift;
        this.premiumTier = giftPremiumBottomSheet$GiftTier;
        this.closeParentSheet = runnable;
        this.topPadding = 0.2f;
        this.name = UserObject.getForcedFirstName(MessagesController.getInstance(i).getUser(Long.valueOf(j)));
        ChatActionCell chatActionCell2 = new ChatActionCell(context, false, this.resourcesProvider);
        this.actionCell = chatActionCell2;
        chatActionCell2.setDelegate(new ChatActionCell.ChatActionCellDelegate() {
            @Override
            public boolean canDrawOutboundsContent() {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$canDrawOutboundsContent(this);
            }

            @Override
            public void didClickButton(ChatActionCell chatActionCell3) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didClickButton(this, chatActionCell3);
            }

            @Override
            public void didClickImage(ChatActionCell chatActionCell3) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didClickImage(this, chatActionCell3);
            }

            @Override
            public boolean didLongPress(ChatActionCell chatActionCell3, float f, float f2) {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$didLongPress(this, chatActionCell3, f, f2);
            }

            @Override
            public void didOpenPremiumGift(ChatActionCell chatActionCell3, TLRPC.TL_premiumGiftOption tL_premiumGiftOption, String str, boolean z3) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didOpenPremiumGift(this, chatActionCell3, tL_premiumGiftOption, str, z3);
            }

            @Override
            public void didOpenPremiumGiftChannel(ChatActionCell chatActionCell3, String str, boolean z3) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didOpenPremiumGiftChannel(this, chatActionCell3, str, z3);
            }

            @Override
            public void didPressReaction(ChatActionCell chatActionCell3, TLRPC.ReactionCount reactionCount, boolean z3, float f, float f2) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReaction(this, chatActionCell3, reactionCount, z3, f, f2);
            }

            @Override
            public void didPressReplyMessage(ChatActionCell chatActionCell3, int i2) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReplyMessage(this, chatActionCell3, i2);
            }

            @Override
            public void forceUpdate(ChatActionCell chatActionCell3, boolean z3) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$forceUpdate(this, chatActionCell3, z3);
            }

            @Override
            public BaseFragment getBaseFragment() {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$getBaseFragment(this);
            }

            @Override
            public long getDialogId() {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$getDialogId(this);
            }

            @Override
            public long getTopicId() {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$getTopicId(this);
            }

            @Override
            public void needOpenInviteLink(TLRPC.TL_chatInviteExported tL_chatInviteExported) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenInviteLink(this, tL_chatInviteExported);
            }

            @Override
            public void needOpenUserProfile(long j2) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenUserProfile(this, j2);
            }

            @Override
            public void needShowEffectOverlay(ChatActionCell chatActionCell3, TLRPC.Document document, TLRPC.VideoSize videoSize) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$needShowEffectOverlay(this, chatActionCell3, document, videoSize);
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
            int maxHeight = -1;

            @Override
            protected boolean isActionBarVisible() {
                return false;
            }

            @Override
            public boolean isStatusBarVisible() {
                return false;
            }

            @Override
            public void onLayout(boolean z3, int i2, int i3, int i4, int i5) {
                super.onLayout(z3, i2, i3, i4, i5);
                SendGiftSheet.this.actionCell.setTranslationY((((i5 - i3) - SendGiftSheet.this.actionCell.getMeasuredHeight()) / 2.0f) - AndroidUtilities.dp(8.0f));
                SendGiftSheet.this.actionCell.setVisiblePart(SendGiftSheet.this.actionCell.getY(), getBackgroundSizeY());
            }

            @Override
            protected void onMeasure(int i2, int i3) {
                if (this.maxHeight != -1) {
                    super.onMeasure(i2, i3);
                    int measuredHeight = getMeasuredHeight();
                    int i4 = this.maxHeight;
                    if (measuredHeight < i4) {
                        i3 = View.MeasureSpec.makeMeasureSpec(Math.max(i4, getMeasuredHeight()), Integer.MIN_VALUE);
                    }
                }
                super.onMeasure(i2, i3);
                int i5 = this.maxHeight;
                if (i5 == -1) {
                    this.maxHeight = Math.max(i5, getMeasuredHeight());
                }
            }

            @Override
            protected boolean useRootView() {
                return false;
            }
        };
        this.chatView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setBackgroundImage(PreviewView.getBackgroundDrawable((Drawable) null, i, j, Theme.isCurrentThemeDark()), false);
        if (starGift != null) {
            TLRPC.TL_messageActionStarGift tL_messageActionStarGift = new TLRPC.TL_messageActionStarGift();
            tL_messageActionStarGift.gift = starGift;
            tL_messageActionStarGift.flags |= 2;
            tL_messageActionStarGift.message = new TLRPC.TL_textWithEntities();
            tL_messageActionStarGift.convert_stars = starGift.convert_stars;
            tL_messageActionStarGift.forceIn = true;
            this.action = tL_messageActionStarGift;
            chatActionCell = chatActionCell2;
        } else if (giftPremiumBottomSheet$GiftTier == null || giftPremiumBottomSheet$GiftTier.giftCodeOption == null) {
            chatActionCell = chatActionCell2;
            if (giftPremiumBottomSheet$GiftTier == null || giftPremiumBottomSheet$GiftTier.giftOption == null) {
                throw new RuntimeException("SendGiftSheet with no star gift and no premium tier");
            }
            TLRPC.TL_messageActionGiftPremium tL_messageActionGiftPremium = new TLRPC.TL_messageActionGiftPremium();
            tL_messageActionGiftPremium.months = giftPremiumBottomSheet$GiftTier.getMonths();
            tL_messageActionGiftPremium.currency = giftPremiumBottomSheet$GiftTier.getCurrency();
            long price = giftPremiumBottomSheet$GiftTier.getPrice();
            tL_messageActionGiftPremium.amount = price;
            if (giftPremiumBottomSheet$GiftTier.googlePlayProductDetails != null) {
                double d = price;
                double pow = Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_messageActionGiftPremium.currency) - 6);
                Double.isNaN(d);
                tL_messageActionGiftPremium.amount = (long) (d * pow);
            }
            tL_messageActionGiftPremium.flags |= 2;
            tL_messageActionGiftPremium.message = new TLRPC.TL_textWithEntities();
            this.action = tL_messageActionGiftPremium;
        } else {
            TLRPC.TL_messageActionGiftCode tL_messageActionGiftCode = new TLRPC.TL_messageActionGiftCode();
            tL_messageActionGiftCode.unclaimed = true;
            tL_messageActionGiftCode.via_giveaway = false;
            tL_messageActionGiftCode.months = giftPremiumBottomSheet$GiftTier.getMonths();
            tL_messageActionGiftCode.flags = 4 | tL_messageActionGiftCode.flags;
            tL_messageActionGiftCode.currency = giftPremiumBottomSheet$GiftTier.getCurrency();
            long price2 = giftPremiumBottomSheet$GiftTier.getPrice();
            tL_messageActionGiftCode.amount = price2;
            if (giftPremiumBottomSheet$GiftTier.googlePlayProductDetails != null) {
                double d2 = price2;
                chatActionCell = chatActionCell2;
                double pow2 = Math.pow(10.0d, BillingController.getInstance().getCurrencyExp(tL_messageActionGiftCode.currency) - 6);
                Double.isNaN(d2);
                tL_messageActionGiftCode.amount = (long) (d2 * pow2);
            } else {
                chatActionCell = chatActionCell2;
            }
            tL_messageActionGiftCode.flags |= 16;
            tL_messageActionGiftCode.message = new TLRPC.TL_textWithEntities();
            this.action = tL_messageActionGiftCode;
        }
        TLRPC.MessageAction messageAction = this.action;
        if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
            TLRPC.TL_messageActionStarGift tL_messageActionStarGift2 = (TLRPC.TL_messageActionStarGift) messageAction;
            boolean z3 = this.upgrade;
            tL_messageActionStarGift2.can_upgrade = z3 || (z2 && starGift != null && starGift.can_upgrade);
            tL_messageActionStarGift2.upgrade_stars = (!z2 && z3) ? starGift.upgrade_stars : 0L;
            tL_messageActionStarGift2.convert_stars = z3 ? 0L : starGift.convert_stars;
        }
        TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
        tL_messageService.id = 1;
        tL_messageService.dialog_id = j;
        tL_messageService.from_id = MessagesController.getInstance(i).getPeer(UserConfig.getInstance(i).getClientUserId());
        tL_messageService.peer_id = MessagesController.getInstance(i).getPeer(UserConfig.getInstance(i).getClientUserId());
        tL_messageService.action = this.action;
        MessageObject messageObject = new MessageObject(i, tL_messageService, false, false);
        this.messageObject = messageObject;
        ChatActionCell chatActionCell3 = chatActionCell;
        chatActionCell3.setMessageObject(messageObject, true);
        sizeNotifierFrameLayout.addView(chatActionCell3, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 8.0f, 0.0f, 8.0f));
        boolean z4 = z2;
        EditEmojiTextCell editEmojiTextCell = new EditEmojiTextCell(context, (SizeNotifierFrameLayout) this.containerView, LocaleController.getString(starGift != null ? R.string.Gift2Message : R.string.Gift2MessageOptional), true, MessagesController.getInstance(i).stargiftsMessageLengthMax, 4, this.resourcesProvider) {
            @Override
            protected void onFocusChanged(boolean z5) {
            }

            @Override
            protected void onTextChanged(CharSequence charSequence) {
                TLRPC.TL_textWithEntities tL_textWithEntities;
                if (SendGiftSheet.this.action instanceof TLRPC.TL_messageActionStarGift) {
                    TLRPC.TL_messageActionStarGift tL_messageActionStarGift3 = (TLRPC.TL_messageActionStarGift) SendGiftSheet.this.action;
                    tL_textWithEntities = new TLRPC.TL_textWithEntities();
                    tL_messageActionStarGift3.message = tL_textWithEntities;
                } else if (SendGiftSheet.this.action instanceof TLRPC.TL_messageActionGiftCode) {
                    ((TLRPC.TL_messageActionGiftCode) SendGiftSheet.this.action).flags |= 16;
                    TLRPC.TL_messageActionGiftCode tL_messageActionGiftCode2 = (TLRPC.TL_messageActionGiftCode) SendGiftSheet.this.action;
                    tL_textWithEntities = new TLRPC.TL_textWithEntities();
                    tL_messageActionGiftCode2.message = tL_textWithEntities;
                } else {
                    if (!(SendGiftSheet.this.action instanceof TLRPC.TL_messageActionGiftPremium)) {
                        return;
                    }
                    ((TLRPC.TL_messageActionGiftPremium) SendGiftSheet.this.action).flags |= 16;
                    TLRPC.TL_messageActionGiftPremium tL_messageActionGiftPremium2 = (TLRPC.TL_messageActionGiftPremium) SendGiftSheet.this.action;
                    tL_textWithEntities = new TLRPC.TL_textWithEntities();
                    tL_messageActionGiftPremium2.message = tL_textWithEntities;
                }
                CharSequence[] charSequenceArr = {SendGiftSheet.this.messageEdit.getText()};
                tL_textWithEntities.entities = MediaDataController.getInstance(i).getEntities(charSequenceArr, true);
                tL_textWithEntities.text = charSequenceArr[0].toString();
                SendGiftSheet.this.messageObject.setType();
                SendGiftSheet.this.actionCell.setMessageObject(SendGiftSheet.this.messageObject, true);
                SendGiftSheet.this.adapter.update(true);
            }
        };
        this.messageEdit = editEmojiTextCell;
        editEmojiTextCell.editTextEmoji.getEditText().addTextChangedListener(new EditTextSuggestionsFix());
        this.messageEdit.editTextEmoji.allowEmojisForNonPremium(true);
        this.messageEdit.setShowLimitWhenNear(50);
        setEditTextEmoji(this.messageEdit.editTextEmoji);
        this.messageEdit.setShowLimitOnFocus(true);
        EditEmojiTextCell editEmojiTextCell2 = this.messageEdit;
        int i2 = Theme.key_dialogBackground;
        editEmojiTextCell2.setBackgroundColor(Theme.getColor(i2, this.resourcesProvider));
        this.messageEdit.setDivider(false);
        this.messageEdit.hideKeyboardOnEnter();
        EditEmojiTextCell editEmojiTextCell3 = this.messageEdit;
        int i3 = this.backgroundPaddingLeft;
        editEmojiTextCell3.setPadding(i3, 0, i3, 0);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() {
            @Override
            protected float animateByScale(View view) {
                return 0.3f;
            }
        };
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayIncrement(40L);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        RecyclerListView recyclerListView = this.recyclerListView;
        int i4 = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i4, 0, i4, AndroidUtilities.dp(68 + ((starGift == null || !starGift.limited) ? 0 : 40)));
        this.adapter.update(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.buttonContainer = linearLayout;
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(Theme.getColor(i2, this.resourcesProvider));
        int i5 = this.backgroundPaddingLeft;
        linearLayout.setPadding(i5, 0, i5, 0);
        this.containerView.addView(linearLayout, LayoutHelper.createFrame(-1, -2, 87));
        View view = new View(context);
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine, this.resourcesProvider));
        linearLayout.addView(view, LayoutHelper.createLinear(-1.0f, 1.0f / AndroidUtilities.density, 55));
        final float clamp = Utilities.clamp(starGift == null ? 0.0f : starGift.availability_remains / starGift.availability_total, 0.97f, 0.0f);
        FrameLayout frameLayout = new FrameLayout(context);
        this.limitContainer = frameLayout;
        frameLayout.setVisibility((starGift == null || !starGift.limited) ? 8 : 0);
        frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider)));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 30, 10.0f, 10.0f, 10.0f, 0.0f));
        TextView textView = new TextView(context);
        this.leftTextView = textView;
        textView.setTextSize(1, 13.0f);
        textView.setGravity(19);
        textView.setTypeface(AndroidUtilities.bold());
        int i6 = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i6, this.resourcesProvider));
        if (starGift != null) {
            textView.setText(LocaleController.formatPluralStringComma("Gift2AvailabilityLeft", starGift.availability_remains));
        }
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -1.0f, 3, 11.0f, 0.0f, 11.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.soldTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        textView2.setGravity(21);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setTextColor(Theme.getColor(i6, this.resourcesProvider));
        if (starGift != null) {
            textView2.setText(LocaleController.formatPluralStringComma("Gift2AvailabilitySold", starGift.availability_total - starGift.availability_remains));
        }
        frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -1.0f, 5, 11.0f, 0.0f, 11.0f, 0.0f));
        View view2 = new View(context) {
            @Override
            protected void onMeasure(int i7, int i8) {
                if (starGift == null) {
                    super.onMeasure(i7, i8);
                } else {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (View.MeasureSpec.getSize(i7) * clamp), 1073741824), i8);
                }
            }
        };
        this.limitProgressView = view2;
        view2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider)));
        frameLayout.addView(view2, LayoutHelper.createFrame(-1, -1, 119));
        FrameLayout frameLayout2 = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(0.0f, 0.0f, getWidth() * clamp, getHeight());
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.valueContainerView = frameLayout2;
        frameLayout2.setWillNotDraw(false);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1, 119));
        TextView textView3 = new TextView(context);
        this.leftTextView2 = textView3;
        textView3.setTextSize(1, 13.0f);
        textView3.setGravity(19);
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setTextColor(-1);
        if (starGift != null) {
            textView3.setText(LocaleController.formatPluralStringComma("Gift2AvailabilityLeft", starGift.availability_remains));
        }
        frameLayout2.addView(textView3, LayoutHelper.createFrame(-1, -1.0f, 3, 11.0f, 0.0f, 11.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.soldTextView2 = textView4;
        textView4.setTextSize(1, 13.0f);
        textView4.setGravity(21);
        textView4.setTypeface(AndroidUtilities.bold());
        textView4.setTextColor(-1);
        if (starGift != null) {
            textView4.setText(LocaleController.formatPluralStringComma("Gift2AvailabilitySold", starGift.availability_total - starGift.availability_remains));
        }
        frameLayout2.addView(textView4, LayoutHelper.createFrame(-1, -1.0f, 5, 11.0f, 0.0f, 11.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, this.resourcesProvider);
        this.button = buttonWithCounterView;
        if (starGift == null) {
            z = false;
            formatString = giftPremiumBottomSheet$GiftTier != null ? LocaleController.formatString(R.string.Gift2SendPremium, giftPremiumBottomSheet$GiftTier.getFormattedPrice()) : formatString;
            linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 119, 10, 10, 10, 10));
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view3) {
                    SendGiftSheet.this.lambda$new$0(starGift, view3);
                }
            });
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            this.reverseLayout = true;
            linearLayoutManager.setReverseLayout(true);
            this.adapter.update(false);
            this.layoutManager.scrollToPositionWithOffset(this.adapter.getItemCount(), AndroidUtilities.dp(200.0f));
            this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view3, int i7) {
                    SendGiftSheet.this.lambda$new$1(starGift, view3, i7);
                }
            });
            this.actionBar.setTitle(getTitle());
        }
        formatString = StarsIntroActivity.replaceStars(LocaleController.formatPluralStringComma(z4 ? "Gift2SendSelf" : "Gift2Send", (int) (starGift.stars + (this.upgrade ? starGift.upgrade_stars : 0L))), coloredImageSpanArr);
        z = false;
        buttonWithCounterView.setText(formatString, z);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 119, 10, 10, 10, 10));
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                SendGiftSheet.this.lambda$new$0(starGift, view3);
            }
        });
        LinearLayoutManager linearLayoutManager2 = this.layoutManager;
        this.reverseLayout = true;
        linearLayoutManager2.setReverseLayout(true);
        this.adapter.update(false);
        this.layoutManager.scrollToPositionWithOffset(this.adapter.getItemCount(), AndroidUtilities.dp(200.0f));
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view3, int i7) {
                SendGiftSheet.this.lambda$new$1(starGift, view3, i7);
            }
        });
        this.actionBar.setTitle(getTitle());
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
        StarsController.getInstance(this.currentAccount).buyStarGift(this.starGift, this.anonymous, this.upgrade, this.dialogId, getMessage(), new Utilities.Callback2() {
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

    public void lambda$fillItems$11() {
        new StarGiftSheet(getContext(), this.currentAccount, this.dialogId, this.resourcesProvider).openAsLearnMore(this.starGift.id, this.name);
    }

    public void lambda$new$0(TL_stars.StarGift starGift, View view) {
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

    public void lambda$new$1(TL_stars.StarGift starGift, View view, int i) {
        UniversalAdapter universalAdapter = this.adapter;
        if (!this.reverseLayout) {
            i--;
        }
        UItem item = universalAdapter.getItem(i);
        if (item == null) {
            return;
        }
        int i2 = item.id;
        if (i2 == 1) {
            boolean z = !this.anonymous;
            this.anonymous = z;
            TLRPC.MessageAction messageAction = this.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                ((TLRPC.TL_messageActionStarGift) messageAction).name_hidden = z;
            }
            this.messageObject.updateMessageText();
            this.actionCell.setMessageObject(this.messageObject, true);
            this.adapter.update(true);
            return;
        }
        if (i2 == 2) {
            boolean z2 = !this.upgrade;
            this.upgrade = z2;
            TLRPC.MessageAction messageAction2 = this.action;
            if (messageAction2 instanceof TLRPC.TL_messageActionStarGift) {
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction2;
                tL_messageActionStarGift.can_upgrade = z2 || (this.self && starGift != null && starGift.can_upgrade);
                tL_messageActionStarGift.upgrade_stars = (!this.self && z2) ? this.starGift.upgrade_stars : 0L;
                tL_messageActionStarGift.convert_stars = z2 ? 0L : this.starGift.convert_stars;
            }
            this.messageObject.updateMessageText();
            this.actionCell.setMessageObject(this.messageObject, true);
            this.adapter.update(true);
            ButtonWithCounterView buttonWithCounterView = this.button;
            String str = this.self ? "Gift2SendSelf" : "Gift2Send";
            TL_stars.StarGift starGift2 = this.starGift;
            buttonWithCounterView.setText(StarsIntroActivity.replaceStars(LocaleController.formatPluralStringComma(str, (int) (starGift2.stars + (this.upgrade ? starGift2.upgrade_stars : 0L))), this.cachedStarSpan), true);
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
        UItem asShadow2;
        arrayList.add(UItem.asCustom(-1, this.chatView));
        arrayList.add(UItem.asCustom(-2, this.messageEdit));
        TL_stars.StarGift starGift = this.starGift;
        if (starGift != null) {
            if (!starGift.can_upgrade || this.self) {
                asShadow2 = UItem.asShadow(-5, null);
            } else {
                arrayList.add(UItem.asShadow(-3, null));
                arrayList.add(UItem.asCheck(2, StarsIntroActivity.replaceStarsWithPlain(LocaleController.formatString(this.self ? R.string.Gift2UpgradeSelf : R.string.Gift2Upgrade, Integer.valueOf((int) this.starGift.upgrade_stars)), 0.78f)).setChecked(this.upgrade));
                asShadow2 = UItem.asShadow(-5, AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(this.self ? LocaleController.getString(R.string.Gift2UpgradeSelfInfo) : LocaleController.formatString(R.string.Gift2UpgradeInfo, this.name), new Runnable() {
                    @Override
                    public final void run() {
                        SendGiftSheet.this.lambda$fillItems$11();
                    }
                }), true));
            }
            arrayList.add(asShadow2);
            arrayList.add(UItem.asCheck(1, LocaleController.getString(this.self ? R.string.Gift2HideSelf : R.string.Gift2Hide)).setChecked(this.anonymous));
            asShadow = UItem.asShadow(-6, this.self ? LocaleController.getString(R.string.Gift2HideSelfInfo) : LocaleController.formatString(R.string.Gift2HideInfo, this.name));
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
        return LocaleController.getString(this.self ? R.string.Gift2TitleSelf2 : R.string.Gift2Title);
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
