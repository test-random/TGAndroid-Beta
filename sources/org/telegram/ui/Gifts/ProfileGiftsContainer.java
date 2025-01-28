package org.telegram.ui.Gifts;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class ProfileGiftsContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final FrameLayout bulletinContainer;
    private final ButtonWithCounterView button;
    private final FrameLayout buttonContainer;
    private int buttonContainerHeightDp;
    private final View buttonShadow;
    private final CheckBox2 checkbox;
    private final LinearLayout checkboxLayout;
    private int checkboxRequestId;
    private final TextView checkboxTextView;
    private final int currentAccount;
    private final long dialogId;
    private final FrameLayout emptyView;
    private final TextView emptyViewButton;
    private final TextView emptyViewTitle;
    private final BaseFragment fragment;
    private final StarsController.GiftsList list;
    private final UniversalRecyclerView listView;
    private final Theme.ResourcesProvider resourcesProvider;
    private int visibleHeight;

    public static class TextFactory extends UItem.UItemFactory {
        static {
            UItem.UItemFactory.setup(new TextFactory());
        }

        public static UItem asText(int i, int i2, float f, CharSequence charSequence, boolean z, int i3) {
            UItem ofFactory = UItem.ofFactory(TextFactory.class);
            ofFactory.text = charSequence;
            ofFactory.intValue = i2;
            ofFactory.longValue = i;
            ofFactory.floatValue = f;
            ofFactory.pad = i3;
            ofFactory.checked = z;
            return ofFactory;
        }

        @Override
        public void bindView(View view, UItem uItem, boolean z) {
            LinkSpanDrawable.LinksTextView linksTextView = (LinkSpanDrawable.LinksTextView) view;
            linksTextView.setGravity(uItem.intValue);
            linksTextView.setTextColor((int) uItem.longValue);
            linksTextView.setTextSize(1, uItem.floatValue);
            linksTextView.setTypeface(uItem.checked ? null : AndroidUtilities.bold());
            int i = uItem.pad;
            linksTextView.setPadding(i, 0, i, 0);
            linksTextView.setText(uItem.text);
        }

        @Override
        public LinkSpanDrawable.LinksTextView createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            return new LinkSpanDrawable.LinksTextView(context) {
                @Override
                public void onMeasure(int i3, int i4) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824), i4);
                }
            };
        }
    }

    public ProfileGiftsContainer(BaseFragment baseFragment, Context context, final int i, final long j, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.checkboxRequestId = -1;
        this.visibleHeight = AndroidUtilities.displaySize.y;
        this.fragment = baseFragment;
        this.currentAccount = i;
        this.dialogId = j;
        StarsController.getInstance(i).invalidateProfileGifts(j);
        StarsController.GiftsList profileGiftsList = StarsController.getInstance(i).getProfileGiftsList(j);
        this.list = profileGiftsList;
        profileGiftsList.shown = true;
        profileGiftsList.resetFilters();
        profileGiftsList.load();
        this.resourcesProvider = resourcesProvider;
        int i2 = Theme.key_windowBackgroundWhite;
        int color = Theme.getColor(i2, resourcesProvider);
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        setBackgroundColor(Theme.blendOver(color, Theme.multAlpha(Theme.getColor(i3, resourcesProvider), 0.04f)));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, i, 0, false, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ProfileGiftsContainer.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                ProfileGiftsContainer.this.onItemClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                return Boolean.valueOf(ProfileGiftsContainer.this.onItemLongPress((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue()));
            }
        }, resourcesProvider, 3);
        this.listView = universalRecyclerView;
        universalRecyclerView.adapter.setApplyBackground(false);
        universalRecyclerView.setSelectorType(9);
        universalRecyclerView.setSelectorDrawableColor(0);
        universalRecyclerView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
        addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1, 119));
        universalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i4, int i5) {
                if (!ProfileGiftsContainer.this.listView.canScrollVertically(1) || ProfileGiftsContainer.this.isLoadingVisible()) {
                    ProfileGiftsContainer.this.list.load();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.emptyView = frameLayout;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setImageDrawable(new RLottieDrawable(R.raw.utyan_empty, "utyan_empty", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f)));
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(120, 120, 1, 0, 0, 0, 0));
        TextView textView = new TextView(context);
        this.emptyViewTitle = textView;
        textView.setTextSize(1, 17.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextColor(Theme.getColor(i3, resourcesProvider));
        textView.setText(LocaleController.getString(R.string.ProfileGiftsNotFoundTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 12, 0, 0));
        TextView textView2 = new TextView(context);
        this.emptyViewButton = textView2;
        textView2.setTextSize(1, 14.0f);
        int i4 = Theme.key_featuredStickers_addButton;
        textView2.setTextColor(Theme.getColor(i4, resourcesProvider));
        textView2.setText(LocaleController.getString(R.string.ProfileGiftsNotFoundButton));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ProfileGiftsContainer.this.lambda$new$0(view);
            }
        });
        textView2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(4.0f));
        textView2.setBackground(Theme.createRadSelectorDrawable(Theme.multAlpha(Theme.getColor(i4, resourcesProvider), 0.1f), 4, 4));
        ScaleStateListAnimator.apply(textView2);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
        addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
        universalRecyclerView.setEmptyView(frameLayout);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.buttonContainer = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(i2, resourcesProvider));
        addView(frameLayout2, LayoutHelper.createFrame(-1, -2, 87));
        View view = new View(context);
        this.buttonShadow = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine, resourcesProvider));
        frameLayout2.addView(view, LayoutHelper.createFrame(-1.0f, 1.0f / AndroidUtilities.density, 55));
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.bulletinContainer = frameLayout3;
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.checkboxLayout = linearLayout2;
        linearLayout2.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f));
        linearLayout2.setClipToPadding(false);
        linearLayout2.setOrientation(0);
        linearLayout2.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 6, 6));
        CheckBox2 checkBox2 = new CheckBox2(context, 24, resourcesProvider);
        this.checkbox = checkBox2;
        checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(false, false);
        checkBox2.setDrawBackgroundAsArc(10);
        linearLayout2.addView(checkBox2, LayoutHelper.createLinear(26, 26, 16, 0, 0, 0, 0));
        TextView textView3 = new TextView(context);
        this.checkboxTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView3.setTextSize(1, 14.0f);
        textView3.setText(LocaleController.getString(R.string.Gift2ChannelNotify));
        linearLayout2.addView(textView3, LayoutHelper.createLinear(-2, -2, 16, 9, 0, 0, 0));
        frameLayout2.addView(linearLayout2, LayoutHelper.createFrame(-2, 38.0f, 17, 0.0f, (1.0f / AndroidUtilities.density) + 6.0f, 0.0f, 6.0f));
        ScaleStateListAnimator.apply(linearLayout2, 0.025f, 1.5f);
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ProfileGiftsContainer.this.lambda$new$3(resourcesProvider, i, j, view2);
            }
        });
        Boolean bool = profileGiftsList.chat_notifications_enabled;
        if (bool != null) {
            checkBox2.setChecked(bool.booleanValue(), false);
        }
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.button = buttonWithCounterView;
        StringBuilder sb = new StringBuilder();
        sb.append("G ");
        sb.append(LocaleController.getString(j < 0 ? R.string.ProfileGiftsSendChannel : R.string.ProfileGiftsSend));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
        spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.filled_gift_simple), 0, 1, 33);
        buttonWithCounterView.setText(spannableStringBuilder, false);
        frameLayout2.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 119, 10.0f, (1.0f / AndroidUtilities.density) + 10.0f, 10.0f, 10.0f));
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ProfileGiftsContainer.lambda$new$4(i, view2);
            }
        });
        buttonWithCounterView.setVisibility(canSwitchNotify() ? 8 : 0);
        linearLayout2.setVisibility(canSwitchNotify() ? 0 : 8);
        this.buttonContainerHeightDp = canSwitchNotify() ? 50 : 68;
        frameLayout2.setVisibility((j >= 0 || ChatObject.canUserDoAction(MessagesController.getInstance(i).getChat(Long.valueOf(-j)), 5)) ? 0 : 8);
        addView(frameLayout3, LayoutHelper.createFrame(-1, 200, 87));
    }

    public boolean isLoadingVisible() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                return true;
            }
        }
        return false;
    }

    public void lambda$new$0(View view) {
        this.list.resetFilters();
    }

    public void lambda$new$1(TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider) {
        this.checkboxRequestId = -1;
        if (tL_error != null) {
            BulletinFactory.of(this.bulletinContainer, resourcesProvider).showForError(tL_error);
        }
    }

    public void lambda$new$2(final Theme.ResourcesProvider resourcesProvider, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ProfileGiftsContainer.this.lambda$new$1(tL_error, resourcesProvider);
            }
        });
    }

    public void lambda$new$3(final Theme.ResourcesProvider resourcesProvider, int i, long j, View view) {
        this.checkbox.setChecked(!r9.isChecked(), true);
        boolean isChecked = this.checkbox.isChecked();
        BulletinFactory.of(this.bulletinContainer, resourcesProvider).createSimpleBulletinDetail(isChecked ? R.raw.silent_unmute : R.raw.silent_mute, LocaleController.getString(isChecked ? R.string.Gift2ChannelNotifyChecked : R.string.Gift2ChannelNotifyNotChecked)).show();
        this.list.chat_notifications_enabled = Boolean.valueOf(isChecked);
        if (this.checkboxRequestId >= 0) {
            ConnectionsManager.getInstance(i).cancelRequest(this.checkboxRequestId, true);
            this.checkboxRequestId = -1;
        }
        TL_stars.toggleChatStarGiftNotifications togglechatstargiftnotifications = new TL_stars.toggleChatStarGiftNotifications();
        togglechatstargiftnotifications.peer = MessagesController.getInstance(i).getInputPeer(j);
        togglechatstargiftnotifications.enabled = isChecked;
        ConnectionsManager.getInstance(i).sendRequest(togglechatstargiftnotifications, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileGiftsContainer.this.lambda$new$2(resourcesProvider, tLObject, tL_error);
            }
        });
    }

    public static void lambda$new$4(int i, View view) {
        UserSelectorBottomSheet.open(2, 0L, BirthdayController.getInstance(i).getState());
    }

    public void lambda$onItemLongPress$5(String str) {
        AndroidUtilities.addToClipboard(str);
        BulletinFactory.of(this.fragment).createCopyLinkBulletin(false).show();
    }

    public void lambda$onItemLongPress$6(TL_stars.SavedStarGift savedStarGift) {
        new StarGiftSheet(getContext(), this.currentAccount, this.dialogId, this.resourcesProvider) {
            @Override
            protected BulletinFactory getBulletinFactory() {
                return BulletinFactory.of(ProfileGiftsContainer.this.fragment);
            }
        }.set(savedStarGift, (StarsController.GiftsList) null).onSharePressed(null);
    }

    public void lambda$onItemLongPress$7(TL_stars.SavedStarGift savedStarGift) {
        new StarGiftSheet(getContext(), this.currentAccount, this.dialogId, this.resourcesProvider) {
            @Override
            protected BulletinFactory getBulletinFactory() {
                return BulletinFactory.of(ProfileGiftsContainer.this.fragment);
            }
        }.set(savedStarGift, (StarsController.GiftsList) null).openTransfer();
    }

    public boolean canFilter() {
        return this.dialogId < 0;
    }

    public boolean canFilterHidden() {
        if (this.dialogId >= 0) {
            return false;
        }
        return ChatObject.canUserDoAction(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId)), 5);
    }

    public boolean canSwitchNotify() {
        return this.dialogId < 0 && this.list.chat_notifications_enabled != null;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.starUserGiftsLoaded) {
            if (i == NotificationCenter.updateInterfaces) {
                this.button.setVisibility(canSwitchNotify() ? 8 : 0);
                this.checkboxLayout.setVisibility(canSwitchNotify() ? 0 : 8);
                this.buttonContainerHeightDp = canSwitchNotify() ? 50 : 68;
                setVisibleHeight(this.visibleHeight);
                return;
            }
            return;
        }
        if (((Long) objArr[0]).longValue() == this.dialogId) {
            this.button.setVisibility(canSwitchNotify() ? 8 : 0);
            this.checkboxLayout.setVisibility(canSwitchNotify() ? 0 : 8);
            this.buttonContainerHeightDp = canSwitchNotify() ? 50 : 68;
            Boolean bool = this.list.chat_notifications_enabled;
            if (bool != null) {
                this.checkbox.setChecked(bool.booleanValue(), true);
            }
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
                universalAdapter.update(true);
            }
            if (!this.listView.canScrollVertically(1) || isLoadingVisible()) {
                this.list.load();
            }
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        int i;
        int i2;
        int i3;
        if (this.list.hasFilters() && this.list.gifts.size() <= 0) {
            StarsController.GiftsList giftsList = this.list;
            if (giftsList.endReached && !giftsList.loading) {
                return;
            }
        }
        StarsController.GiftsList giftsList2 = this.list;
        int max = Math.max(1, (giftsList2 == null || (i3 = giftsList2.totalCount) == 0) ? 3 : Math.min(3, i3));
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.setSpanCount(max);
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(12.0f)));
        StarsController.GiftsList giftsList3 = this.list;
        if (giftsList3 != null) {
            Iterator it = giftsList3.gifts.iterator();
            loop0: while (true) {
                i = 3;
                do {
                    i2 = 0;
                    if (!it.hasNext()) {
                        break loop0;
                    }
                    arrayList.add(GiftSheet.GiftCell.Factory.asStarGift(0, (TL_stars.SavedStarGift) it.next(), true));
                    i--;
                } while (i != 0);
            }
            StarsController.GiftsList giftsList4 = this.list;
            if (giftsList4.loading || !giftsList4.endReached) {
                while (true) {
                    if (i2 >= (i <= 0 ? 3 : i)) {
                        break;
                    }
                    i2++;
                    arrayList.add(UItem.asFlicker(i2, 34).setSpanCount(1));
                }
            }
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(20.0f)));
        if (this.dialogId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            arrayList.add(TextFactory.asText(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider), 17, 14.0f, LocaleController.getString(R.string.ProfileGiftsInfo), true, AndroidUtilities.dp(24.0f)));
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(82.0f)));
    }

    public RecyclerListView getCurrentListView() {
        return this.listView;
    }

    public int getGiftsCount() {
        int i;
        StarsController.GiftsList giftsList = this.list;
        if (giftsList != null && (i = giftsList.totalCount) > 0) {
            return i;
        }
        long j = this.dialogId;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        long j2 = this.dialogId;
        if (j >= 0) {
            TLRPC.UserFull userFull = messagesController.getUserFull(j2);
            if (userFull != null) {
                return userFull.stargifts_count;
            }
            return 0;
        }
        TLRPC.ChatFull chatFull = messagesController.getChatFull(-j2);
        if (chatFull != null) {
            return chatFull.stargifts_count;
        }
        return 0;
    }

    public CharSequence getLastEmojis(Paint.FontMetricsInt fontMetricsInt) {
        StarsController.GiftsList giftsList = this.list;
        if (giftsList == null || giftsList.gifts.isEmpty()) {
            return "";
        }
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; arrayList.size() < 3 && i < this.list.gifts.size(); i++) {
            TLRPC.Document document = ((TL_stars.SavedStarGift) this.list.gifts.get(i)).gift.getDocument();
            if (document != null && !hashSet.contains(Long.valueOf(document.id))) {
                hashSet.add(Long.valueOf(document.id));
                arrayList.add(document);
            }
        }
        if (arrayList.isEmpty()) {
            return "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" ");
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("x");
            spannableStringBuilder2.setSpan(new AnimatedEmojiSpan((TLRPC.Document) arrayList.get(i2), 0.9f, fontMetricsInt), 0, 1, 33);
            spannableStringBuilder.append((CharSequence) spannableStringBuilder2);
        }
        return spannableStringBuilder;
    }

    public long getLastEmojisHash() {
        StarsController.GiftsList giftsList = this.list;
        long j = 0;
        if (giftsList != null && !giftsList.gifts.isEmpty()) {
            HashSet hashSet = new HashSet();
            int i = 0;
            for (int i2 = 0; i < 3 && i2 < this.list.gifts.size(); i2++) {
                TLRPC.Document document = ((TL_stars.SavedStarGift) this.list.gifts.get(i2)).gift.getDocument();
                if (document != null) {
                    hashSet.add(Long.valueOf(document.id));
                    j = Objects.hash(Long.valueOf(j), Long.valueOf(document.id));
                    i++;
                }
            }
        }
        return j;
    }

    public StarsController.GiftsList getList() {
        return this.list;
    }

    @Override
    protected void onAttachedToWindow() {
        UniversalAdapter universalAdapter;
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starUserGiftsLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(false);
        }
        StarsController.GiftsList giftsList = this.list;
        if (giftsList != null) {
            giftsList.shown = true;
            giftsList.load();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starUserGiftsLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        StarsController.GiftsList giftsList = this.list;
        if (giftsList != null) {
            giftsList.shown = false;
        }
    }

    public void onItemClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof TL_stars.SavedStarGift) {
            new StarGiftSheet(getContext(), this.currentAccount, this.dialogId, this.resourcesProvider).set((TL_stars.SavedStarGift) obj, this.list).show();
        }
    }

    public boolean onItemLongPress(UItem uItem, View view, int i, float f, float f2) {
        final String str;
        Object obj = uItem.object;
        if (obj instanceof TL_stars.SavedStarGift) {
            final TL_stars.SavedStarGift savedStarGift = (TL_stars.SavedStarGift) obj;
            TL_stars.StarGift starGift = savedStarGift.gift;
            if (starGift instanceof TL_stars.TL_starGiftUnique) {
                if (starGift.slug != null) {
                    str = MessagesController.getInstance(this.currentAccount).linkPrefix + "/nft/" + savedStarGift.gift.slug;
                } else {
                    str = null;
                }
                boolean z = DialogObject.getPeerDialogId(((TL_stars.TL_starGiftUnique) savedStarGift.gift).owner_id) == UserConfig.getInstance(this.currentAccount).getClientUserId();
                ItemOptions makeOptions = ItemOptions.makeOptions(this.fragment, view);
                makeOptions.addIf(str != null, R.drawable.msg_link, LocaleController.getString(R.string.CopyLink), new Runnable() {
                    @Override
                    public final void run() {
                        ProfileGiftsContainer.this.lambda$onItemLongPress$5(str);
                    }
                });
                makeOptions.addIf(str != null, R.drawable.msg_share, LocaleController.getString(R.string.ShareFile), new Runnable() {
                    @Override
                    public final void run() {
                        ProfileGiftsContainer.this.lambda$onItemLongPress$6(savedStarGift);
                    }
                });
                makeOptions.addIf(z, R.drawable.menu_feature_transfer, LocaleController.getString(R.string.Gift2TransferOption), new Runnable() {
                    @Override
                    public final void run() {
                        ProfileGiftsContainer.this.lambda$onItemLongPress$7(savedStarGift);
                    }
                });
                if (makeOptions.getItemsCount() <= 0) {
                    return false;
                }
                makeOptions.setGravity(3);
                makeOptions.setBlur(true);
                makeOptions.show();
                return true;
            }
        }
        return false;
    }

    protected abstract int processColor(int i);

    public void setVisibleHeight(int i) {
        this.visibleHeight = i;
        if (!canSwitchNotify()) {
            this.bulletinContainer.setTranslationY(0.0f);
            this.buttonContainer.setTranslationY(0.0f);
        } else {
            this.bulletinContainer.setTranslationY(((((-r0.getTop()) + i) - AndroidUtilities.dp(this.buttonContainerHeightDp)) - 1) - AndroidUtilities.dp(200.0f));
            this.buttonContainer.setTranslationY((((-r0.getTop()) + i) - AndroidUtilities.dp(this.buttonContainerHeightDp)) - 1);
        }
    }

    public void updateColors() {
        this.button.updateColors();
        ButtonWithCounterView buttonWithCounterView = this.button;
        int dp = AndroidUtilities.dp(8.0f);
        int i = Theme.key_featuredStickers_addButton;
        buttonWithCounterView.setBackground(Theme.createRoundRectDrawable(dp, processColor(Theme.getColor(i, this.resourcesProvider))));
        this.emptyViewTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
        this.emptyViewButton.setTextColor(Theme.getColor(i, this.resourcesProvider));
        this.emptyViewButton.setBackground(Theme.createRadSelectorDrawable(Theme.multAlpha(Theme.getColor(i, this.resourcesProvider), 0.1f), 4, 4));
        this.buttonContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
        this.buttonShadow.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine, this.resourcesProvider));
        this.checkboxTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
        this.checkboxLayout.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, this.resourcesProvider), 6, 6));
    }
}
