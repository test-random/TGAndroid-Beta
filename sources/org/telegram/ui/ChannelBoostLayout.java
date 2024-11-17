package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.FixedHeightEmptyCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.LimitPreviewView;
import org.telegram.ui.Components.Premium.boosts.cells.statistics.GiftedUserCell;
import org.telegram.ui.Components.Premium.boosts.cells.statistics.GiveawayCell;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.StatisticActivity;

public class ChannelBoostLayout extends FrameLayout {
    AdapterWithDiffUtils adapter;
    private final ArrayList boosters;
    TL_stories.TL_premium_boostsStatus boostsStatus;
    private ScrollSlidingTextTabStrip boostsTabs;
    int currentAccount;
    private TLRPC.Chat currentChat;
    private final long dialogId;
    BaseFragment fragment;
    private final ArrayList gifts;
    private boolean hasBoostsNext;
    private boolean hasGiftsNext;
    private final ArrayList items;
    private String lastBoostsOffset;
    private String lastGiftsOffset;
    private int limitBoosts;
    private int limitGifts;
    RecyclerListView listView;
    private int nextBoostRemaining;
    private int nextGiftsRemaining;
    private LinearLayout progressLayout;
    private final Theme.ResourcesProvider resourcesProvider;
    private int selectedTab;
    private int totalBoosts;
    private int totalGifts;
    boolean usersLoading;

    public class ItemInternal extends AdapterWithDiffUtils.Item {
        TL_stories.Boost booster;
        boolean isLast;
        TL_stories.PrepaidGiveaway prepaidGiveaway;
        int tab;
        String title;

        public ItemInternal(int i, String str) {
            super(i, false);
            this.title = str;
        }

        public ItemInternal(int i, TL_stories.Boost boost, boolean z, int i2) {
            super(i, true);
            this.booster = boost;
            this.isLast = z;
            this.tab = i2;
        }

        public ItemInternal(int i, TL_stories.PrepaidGiveaway prepaidGiveaway, boolean z) {
            super(i, true);
            this.prepaidGiveaway = prepaidGiveaway;
            this.isLast = z;
        }

        public ItemInternal(int i, boolean z) {
            super(i, z);
        }

        public boolean equals(Object obj) {
            TL_stories.PrepaidGiveaway prepaidGiveaway;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ItemInternal itemInternal = (ItemInternal) obj;
            TL_stories.PrepaidGiveaway prepaidGiveaway2 = this.prepaidGiveaway;
            if (prepaidGiveaway2 != null && (prepaidGiveaway = itemInternal.prepaidGiveaway) != null) {
                return prepaidGiveaway2.id == prepaidGiveaway.id && this.isLast == itemInternal.isLast;
            }
            TL_stories.Boost boost = this.booster;
            if (boost == null || itemInternal.booster == null) {
                return true;
            }
            return boost.id.hashCode() == itemInternal.booster.id.hashCode() && this.isLast == itemInternal.isLast && this.tab == itemInternal.tab;
        }

        public int hashCode() {
            return Objects.hash(this.title, this.booster, this.prepaidGiveaway, Boolean.valueOf(this.isLast), Integer.valueOf(this.tab));
        }
    }

    public ChannelBoostLayout(final BaseFragment baseFragment, final long j, final Theme.ResourcesProvider resourcesProvider) {
        super(baseFragment.getContext());
        this.currentAccount = UserConfig.selectedAccount;
        this.boosters = new ArrayList();
        this.gifts = new ArrayList();
        this.items = new ArrayList();
        this.selectedTab = 0;
        this.adapter = new AdapterWithDiffUtils() {
            private int remTotalBoosts = -1;
            private int remTotalGifts = -1;

            @Override
            public int getItemCount() {
                return ChannelBoostLayout.this.items.size();
            }

            @Override
            public int getItemViewType(int i) {
                return ((ItemInternal) ChannelBoostLayout.this.items.get(i)).viewType;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return ((ItemInternal) ChannelBoostLayout.this.items.get(viewHolder.getAdapterPosition())).selectable;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                boolean z;
                String str;
                String str2;
                GiveawayCell giveawayCell;
                if (viewHolder.getItemViewType() == 4) {
                    return;
                }
                if (viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 12) {
                    ChartHeaderView chartHeaderView = (ChartHeaderView) viewHolder.itemView;
                    chartHeaderView.setTitle(((ItemInternal) ChannelBoostLayout.this.items.get(i)).title);
                    chartHeaderView.showDate(false);
                    if (viewHolder.getItemViewType() == 12) {
                        chartHeaderView.setPadding(AndroidUtilities.dp(3.0f), chartHeaderView.getPaddingTop(), chartHeaderView.getPaddingRight(), chartHeaderView.getPaddingBottom());
                        return;
                    }
                    return;
                }
                if (viewHolder.getItemViewType() == 0) {
                    StatisticActivity.OverviewCell overviewCell = (StatisticActivity.OverviewCell) viewHolder.itemView;
                    overviewCell.setData(0, Integer.toString(ChannelBoostLayout.this.boostsStatus.level), null, LocaleController.getString(R.string.BoostsLevel2));
                    ChannelBoostLayout channelBoostLayout = ChannelBoostLayout.this;
                    TL_stats.TL_statsPercentValue tL_statsPercentValue = channelBoostLayout.boostsStatus.premium_audience;
                    if (tL_statsPercentValue != null) {
                        double d = tL_statsPercentValue.total;
                        if (d != 0.0d) {
                            float f = (((float) tL_statsPercentValue.part) / ((float) d)) * 100.0f;
                            overviewCell.setData(1, "≈" + ((int) ChannelBoostLayout.this.boostsStatus.premium_audience.part), String.format(Locale.US, "%.1f", Float.valueOf(f)) + "%", LocaleController.getString(ChannelBoostLayout.this.isChannel() ? R.string.PremiumSubscribers : R.string.PremiumMembers));
                            overviewCell.setData(2, String.valueOf(ChannelBoostLayout.this.boostsStatus.boosts), null, LocaleController.getString(R.string.BoostsExisting));
                            TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus = ChannelBoostLayout.this.boostsStatus;
                            overviewCell.setData(3, String.valueOf(Math.max(0, tL_premium_boostsStatus.next_level_boosts - tL_premium_boostsStatus.boosts)), null, LocaleController.getString(R.string.BoostsToLevel));
                            return;
                        }
                    }
                    overviewCell.setData(1, "≈0", "0%", LocaleController.getString(channelBoostLayout.isChannel() ? R.string.PremiumSubscribers : R.string.PremiumMembers));
                    overviewCell.setData(2, String.valueOf(ChannelBoostLayout.this.boostsStatus.boosts), null, LocaleController.getString(R.string.BoostsExisting));
                    TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus2 = ChannelBoostLayout.this.boostsStatus;
                    overviewCell.setData(3, String.valueOf(Math.max(0, tL_premium_boostsStatus2.next_level_boosts - tL_premium_boostsStatus2.boosts)), null, LocaleController.getString(R.string.BoostsToLevel));
                    return;
                }
                if (viewHolder.getItemViewType() == 5) {
                    TL_stories.Boost boost = ((ItemInternal) ChannelBoostLayout.this.items.get(i)).booster;
                    TLRPC.User user = MessagesController.getInstance(ChannelBoostLayout.this.currentAccount).getUser(Long.valueOf(boost.user_id));
                    GiftedUserCell giftedUserCell = (GiftedUserCell) viewHolder.itemView;
                    giftedUserCell.setData(user, ContactsController.formatName(user), boost.multiplier > 1 ? LocaleController.formatString("BoostsExpireOn", R.string.BoostsExpireOn, LocaleController.formatDate(boost.expires)) : LocaleController.formatString("BoostExpireOn", R.string.BoostExpireOn, LocaleController.formatDate(boost.expires)), 0, !((ItemInternal) ChannelBoostLayout.this.items.get(i)).isLast);
                    giftedUserCell.setStatus(boost);
                    giveawayCell = giftedUserCell;
                } else {
                    if (viewHolder.getItemViewType() == 6) {
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(((ItemInternal) ChannelBoostLayout.this.items.get(i)).title);
                        return;
                    }
                    if (viewHolder.getItemViewType() == 9) {
                        ((ManageChatTextCell) viewHolder.itemView).setText(ChannelBoostLayout.this.selectedTab == 0 ? LocaleController.formatPluralString("BoostingShowMoreBoosts", ChannelBoostLayout.this.nextBoostRemaining, new Object[0]) : LocaleController.formatPluralString("BoostingShowMoreGifts", ChannelBoostLayout.this.nextGiftsRemaining, new Object[0]), null, R.drawable.arrow_more, false);
                        return;
                    }
                    if (viewHolder.getItemViewType() == 3) {
                        ((LinkActionView) viewHolder.itemView).setLink(((ItemInternal) ChannelBoostLayout.this.items.get(i)).title);
                        return;
                    }
                    if (viewHolder.getItemViewType() != 11) {
                        if (viewHolder.getItemViewType() == 13) {
                            if (this.remTotalBoosts == ChannelBoostLayout.this.totalBoosts && this.remTotalGifts == ChannelBoostLayout.this.totalGifts) {
                                return;
                            }
                            this.remTotalBoosts = ChannelBoostLayout.this.totalBoosts;
                            this.remTotalGifts = ChannelBoostLayout.this.totalGifts;
                            ChannelBoostLayout.this.boostsTabs.removeTabs();
                            ChannelBoostLayout.this.boostsTabs.addTextTab(0, LocaleController.formatPluralString("BoostingBoostsCount", ChannelBoostLayout.this.totalBoosts, new Object[0]));
                            if (MessagesController.getInstance(ChannelBoostLayout.this.currentAccount).giveawayGiftsPurchaseAvailable && ChannelBoostLayout.this.totalGifts > 0 && ChannelBoostLayout.this.totalGifts != ChannelBoostLayout.this.totalBoosts) {
                                ChannelBoostLayout.this.boostsTabs.addTextTab(1, LocaleController.formatPluralString("BoostingGiftsCount", ChannelBoostLayout.this.totalGifts, new Object[0]));
                            }
                            ChannelBoostLayout.this.boostsTabs.setInitialTabId(ChannelBoostLayout.this.selectedTab);
                            ChannelBoostLayout.this.boostsTabs.finishAddingTabs();
                            return;
                        }
                        return;
                    }
                    ItemInternal itemInternal = (ItemInternal) ChannelBoostLayout.this.items.get(i);
                    TL_stories.PrepaidGiveaway prepaidGiveaway = itemInternal.prepaidGiveaway;
                    GiveawayCell giveawayCell2 = (GiveawayCell) viewHolder.itemView;
                    if (prepaidGiveaway instanceof TL_stories.TL_prepaidGiveaway) {
                        String formatPluralString = LocaleController.formatPluralString("BoostingTelegramPremiumCountPlural", prepaidGiveaway.quantity, new Object[0]);
                        String formatPluralString2 = LocaleController.formatPluralString("BoostingSubscriptionsCountPlural", prepaidGiveaway.quantity, LocaleController.formatPluralString("PrepaidGiveawayMonths", ((TL_stories.TL_prepaidGiveaway) prepaidGiveaway).months, new Object[0]));
                        z = itemInternal.isLast;
                        str = formatPluralString2;
                        str2 = formatPluralString;
                    } else {
                        if (prepaidGiveaway instanceof TL_stories.TL_prepaidStarsGiveaway) {
                            TL_stories.TL_prepaidStarsGiveaway tL_prepaidStarsGiveaway = (TL_stories.TL_prepaidStarsGiveaway) prepaidGiveaway;
                            String formatPluralStringComma = LocaleController.formatPluralStringComma("BoostingStarsCountPlural", (int) tL_prepaidStarsGiveaway.stars);
                            String formatPluralString3 = LocaleController.formatPluralString("AmongWinners", tL_prepaidStarsGiveaway.quantity, new Object[0]);
                            z = itemInternal.isLast;
                            str = formatPluralString3;
                            str2 = formatPluralStringComma;
                        }
                        giveawayCell2.setImage(prepaidGiveaway);
                        giveawayCell = giveawayCell2;
                    }
                    giveawayCell2.setData(prepaidGiveaway, str2, str, 0, !z);
                    giveawayCell2.setImage(prepaidGiveaway);
                    giveawayCell = giveawayCell2;
                }
                giveawayCell.setAvatarPadding(5);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View shadowSectionCell;
                View view;
                ViewGroup.LayoutParams createFrame;
                View view2;
                ViewGroup viewGroup2;
                switch (i) {
                    case 0:
                        view = new StatisticActivity.OverviewCell(ChannelBoostLayout.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 1:
                        View chartHeaderView = new ChartHeaderView(ChannelBoostLayout.this.getContext());
                        chartHeaderView.setPadding(chartHeaderView.getPaddingLeft(), AndroidUtilities.dp(16.0f), chartHeaderView.getRight(), AndroidUtilities.dp(16.0f));
                        view = chartHeaderView;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 2:
                        shadowSectionCell = new ShadowSectionCell(viewGroup.getContext(), 12, Theme.getColor(Theme.key_windowBackgroundGray));
                        view = shadowSectionCell;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 3:
                        LinkActionView linkActionView = new LinkActionView(ChannelBoostLayout.this.getContext(), ChannelBoostLayout.this.fragment, null, 0L, false, false);
                        linkActionView.hideOptions();
                        linkActionView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(24.0f));
                        view = linkActionView;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 4:
                        LimitPreviewView limitPreviewView = new LimitPreviewView(ChannelBoostLayout.this.getContext(), R.drawable.filled_limit_boost, 0, 0, ChannelBoostLayout.this.resourcesProvider);
                        limitPreviewView.isStatistic = true;
                        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(ChannelBoostLayout.this.getContext(), R.drawable.greydivider, Theme.getColor(Theme.key_windowBackgroundGrayShadow, ChannelBoostLayout.this.resourcesProvider)), 0, 0);
                        combinedDrawable.setFullsize(true);
                        limitPreviewView.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
                        limitPreviewView.setBackground(combinedDrawable);
                        limitPreviewView.setBoosts(ChannelBoostLayout.this.boostsStatus, false);
                        view = limitPreviewView;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 5:
                        view = new GiftedUserCell(ChannelBoostLayout.this.getContext(), 0, 0, false);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 6:
                        shadowSectionCell = new TextInfoPrivacyCell(viewGroup.getContext(), 20, ChannelBoostLayout.this.resourcesProvider);
                        CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(ChannelBoostLayout.this.getContext(), R.drawable.greydivider, Theme.getColor(Theme.key_windowBackgroundGrayShadow, ChannelBoostLayout.this.resourcesProvider)), 0, 0);
                        combinedDrawable2.setFullsize(true);
                        shadowSectionCell.setBackground(combinedDrawable2);
                        view = shadowSectionCell;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 7:
                        view = new FixedHeightEmptyCell(ChannelBoostLayout.this.getContext(), 8);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 8:
                        ViewGroup viewGroup3 = new FrameLayout(ChannelBoostLayout.this.getContext()) {
                            @Override
                            protected void onMeasure(int i2, int i3) {
                                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
                            }
                        };
                        TextView textView = new TextView(ChannelBoostLayout.this.getContext());
                        textView.setText(LocaleController.getString(ChannelBoostLayout.this.isChannel() ? R.string.NoBoostersHint : R.string.NoBoostersGroupHint));
                        textView.setTextSize(1, 14.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                        textView.setGravity(17);
                        createFrame = LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 16.0f, 0.0f, 0.0f);
                        viewGroup2 = viewGroup3;
                        view2 = textView;
                        viewGroup2.addView(view2, createFrame);
                        view = viewGroup2;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 9:
                        ManageChatTextCell manageChatTextCell = new ManageChatTextCell(ChannelBoostLayout.this.getContext()) {
                            @Override
                            protected int getFullHeight() {
                                return AndroidUtilities.dp(50.0f);
                            }
                        };
                        manageChatTextCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        view = manageChatTextCell;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 10:
                        TextCell textCell = new TextCell(ChannelBoostLayout.this.getContext());
                        textCell.setTextAndIcon((CharSequence) LocaleController.formatString("BoostingGetBoostsViaGifts", R.string.BoostingGetBoostsViaGifts, new Object[0]), R.drawable.msg_gift_premium, false);
                        textCell.offsetFromImage = 64;
                        int i2 = Theme.key_windowBackgroundWhiteBlueText4;
                        textCell.setColors(i2, i2);
                        view = textCell;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 11:
                        view = new GiveawayCell(ChannelBoostLayout.this.getContext(), 0, 0, false);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 12:
                        View chartHeaderView2 = new ChartHeaderView(ChannelBoostLayout.this.getContext());
                        chartHeaderView2.setPadding(chartHeaderView2.getPaddingLeft(), AndroidUtilities.dp(16.0f), chartHeaderView2.getRight(), AndroidUtilities.dp(8.0f));
                        view = chartHeaderView2;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    case 13:
                        ChannelBoostLayout.this.boostsTabs = new ScrollSlidingTextTabStrip(ChannelBoostLayout.this.fragment.getContext(), ChannelBoostLayout.this.resourcesProvider);
                        ChannelBoostLayout.this.boostsTabs.setColors(Theme.key_profile_tabSelectedLine, Theme.key_profile_tabSelectedText, Theme.key_profile_tabText, Theme.key_profile_tabSelector);
                        ViewGroup viewGroup4 = new FrameLayout(ChannelBoostLayout.this.fragment.getContext()) {
                            private final Paint dividerPaint = new Paint(1);

                            @Override
                            protected void dispatchDraw(Canvas canvas) {
                                super.dispatchDraw(canvas);
                                this.dividerPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray, ChannelBoostLayout.this.resourcesProvider));
                                canvas.drawRect(0.0f, getHeight() - 2, getWidth(), getHeight(), this.dividerPaint);
                            }
                        };
                        ChannelBoostLayout.this.boostsTabs.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
                            @Override
                            public void onPageScrolled(float f) {
                            }

                            @Override
                            public void onPageSelected(int i3, boolean z) {
                                ChannelBoostLayout.this.selectedTab = i3;
                                ChannelBoostLayout.this.updateRows(true);
                            }

                            @Override
                            public void onSamePageSelected() {
                            }
                        });
                        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = ChannelBoostLayout.this.boostsTabs;
                        createFrame = LayoutHelper.createFrame(-2, 48.0f);
                        viewGroup2 = viewGroup4;
                        view2 = scrollSlidingTextTabStrip;
                        viewGroup2.addView(view2, createFrame);
                        view = viewGroup2;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        return new RecyclerListView.Holder(view);
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        };
        this.lastBoostsOffset = "";
        this.lastGiftsOffset = "";
        this.limitGifts = 5;
        this.limitBoosts = 5;
        this.fragment = baseFragment;
        final Context context = baseFragment.getContext();
        this.resourcesProvider = resourcesProvider;
        this.dialogId = j;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                ChannelBoostLayout.this.lambda$new$0(context, j, resourcesProvider, baseFragment, view, i);
            }
        });
        addView(this.listView);
        loadStatistic();
        this.listView.setAdapter(this.adapter);
        updateRows(false);
        createEmptyView(getContext());
        this.progressLayout.setAlpha(0.0f);
        this.progressLayout.animate().alpha(1.0f).setDuration(200L).setStartDelay(500L).start();
        StarsController.getInstance(this.currentAccount).getGiveawayOptions();
    }

    public boolean isChannel() {
        return ChatObject.isChannelAndNotMegaGroup(this.currentChat);
    }

    public void lambda$loadOnlyBoosts$7(CountDownLatch countDownLatch, TLObject tLObject, Runnable runnable) {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        if (tLObject != null) {
            this.limitBoosts = 20;
            TL_stories.TL_premium_boostsList tL_premium_boostsList = (TL_stories.TL_premium_boostsList) tLObject;
            boolean z = false;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_premium_boostsList.users, false);
            this.lastBoostsOffset = tL_premium_boostsList.next_offset;
            this.boosters.addAll(tL_premium_boostsList.boosts);
            Iterator it = this.boosters.iterator();
            int i = 0;
            while (true) {
                int i2 = 1;
                if (!it.hasNext()) {
                    break;
                }
                int i3 = ((TL_stories.Boost) it.next()).multiplier;
                if (i3 > 0) {
                    i2 = i3;
                }
                i += i2;
            }
            this.nextBoostRemaining = Math.max(0, tL_premium_boostsList.count - i);
            if (!TextUtils.isEmpty(tL_premium_boostsList.next_offset) && this.nextBoostRemaining > 0) {
                z = true;
            }
            this.hasBoostsNext = z;
            this.totalBoosts = tL_premium_boostsList.count;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public void lambda$loadOnlyBoosts$8(final CountDownLatch countDownLatch, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelBoostLayout.this.lambda$loadOnlyBoosts$7(countDownLatch, tLObject, runnable);
            }
        });
    }

    public void lambda$loadOnlyGifts$10(final CountDownLatch countDownLatch, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelBoostLayout.this.lambda$loadOnlyGifts$9(countDownLatch, tLObject, runnable);
            }
        });
    }

    public void lambda$loadOnlyGifts$9(CountDownLatch countDownLatch, TLObject tLObject, Runnable runnable) {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        if (tLObject != null) {
            this.limitGifts = 20;
            TL_stories.TL_premium_boostsList tL_premium_boostsList = (TL_stories.TL_premium_boostsList) tLObject;
            boolean z = false;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_premium_boostsList.users, false);
            this.lastGiftsOffset = tL_premium_boostsList.next_offset;
            this.gifts.addAll(tL_premium_boostsList.boosts);
            Iterator it = this.gifts.iterator();
            int i = 0;
            while (true) {
                int i2 = 1;
                if (!it.hasNext()) {
                    break;
                }
                int i3 = ((TL_stories.Boost) it.next()).multiplier;
                if (i3 > 0) {
                    i2 = i3;
                }
                i += i2;
            }
            this.nextGiftsRemaining = Math.max(0, tL_premium_boostsList.count - i);
            if (!TextUtils.isEmpty(tL_premium_boostsList.next_offset) && this.nextGiftsRemaining > 0) {
                z = true;
            }
            this.hasGiftsNext = z;
            this.totalGifts = tL_premium_boostsList.count;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public void lambda$loadStatistic$1(TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        this.boostsStatus = tL_premium_boostsStatus;
        this.progressLayout.animate().cancel();
        this.progressLayout.animate().alpha(0.0f).setDuration(100L).setStartDelay(0L).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ChannelBoostLayout.this.progressLayout.setVisibility(8);
            }
        });
        updateRows(true);
        loadUsers(null);
    }

    public void lambda$loadStatistic$2(final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelBoostLayout.this.lambda$loadStatistic$1(tL_premium_boostsStatus);
            }
        });
    }

    public void lambda$loadUsers$3() {
        this.usersLoading = false;
        updateRows(true);
    }

    public void lambda$loadUsers$4() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        loadOnlyBoosts(countDownLatch, null);
        loadOnlyGifts(countDownLatch, null);
        try {
            countDownLatch.await();
        } catch (InterruptedException unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelBoostLayout.this.lambda$loadUsers$3();
            }
        });
    }

    public void lambda$loadUsers$5() {
        this.usersLoading = false;
        updateRows(true);
    }

    public void lambda$loadUsers$6() {
        this.usersLoading = false;
        updateRows(true);
    }

    public void lambda$new$0(android.content.Context r16, long r17, org.telegram.ui.ActionBar.Theme.ResourcesProvider r19, org.telegram.ui.ActionBar.BaseFragment r20, android.view.View r21, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelBoostLayout.lambda$new$0(android.content.Context, long, org.telegram.ui.ActionBar.Theme$ResourcesProvider, org.telegram.ui.ActionBar.BaseFragment, android.view.View, int):void");
    }

    private void loadOnlyBoosts(final CountDownLatch countDownLatch, final Runnable runnable) {
        TL_stories.TL_premium_getBoostsList tL_premium_getBoostsList = new TL_stories.TL_premium_getBoostsList();
        tL_premium_getBoostsList.limit = this.limitBoosts;
        tL_premium_getBoostsList.offset = this.lastBoostsOffset;
        tL_premium_getBoostsList.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_premium_getBoostsList, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelBoostLayout.this.lambda$loadOnlyBoosts$8(countDownLatch, runnable, tLObject, tL_error);
            }
        }, 2);
    }

    private void loadOnlyGifts(final CountDownLatch countDownLatch, final Runnable runnable) {
        TL_stories.TL_premium_getBoostsList tL_premium_getBoostsList = new TL_stories.TL_premium_getBoostsList();
        tL_premium_getBoostsList.limit = this.limitGifts;
        tL_premium_getBoostsList.gifts = true;
        tL_premium_getBoostsList.offset = this.lastGiftsOffset;
        tL_premium_getBoostsList.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_premium_getBoostsList, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelBoostLayout.this.lambda$loadOnlyGifts$10(countDownLatch, runnable, tLObject, tL_error);
            }
        }, 2);
    }

    private void loadStatistic() {
        MessagesController.getInstance(this.currentAccount).getBoostsController().getBoostsStats(this.dialogId, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChannelBoostLayout.this.lambda$loadStatistic$2((TL_stories.TL_premium_boostsStatus) obj);
            }
        });
    }

    private void loadUsers(Boolean bool) {
        if (this.usersLoading) {
            return;
        }
        this.usersLoading = true;
        if (bool == null) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ChannelBoostLayout.this.lambda$loadUsers$4();
                }
            });
        } else if (bool.booleanValue()) {
            loadOnlyGifts(null, new Runnable() {
                @Override
                public final void run() {
                    ChannelBoostLayout.this.lambda$loadUsers$5();
                }
            });
        } else {
            loadOnlyBoosts(null, new Runnable() {
                @Override
                public final void run() {
                    ChannelBoostLayout.this.lambda$loadUsers$6();
                }
            });
        }
    }

    public void createEmptyView(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        this.progressLayout = linearLayout;
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAutoRepeat(true);
        rLottieImageView.setAnimation(R.raw.statistic_preload, 120, 120);
        rLottieImageView.playAnimation();
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i = Theme.key_player_actionBarTitle;
        textView.setTextColor(Theme.getColor(i));
        textView.setTag(Integer.valueOf(i));
        textView.setText(LocaleController.getString(R.string.LoadingStats));
        textView.setGravity(1);
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 15.0f);
        int i2 = Theme.key_player_actionBarSubtitle;
        textView2.setTextColor(Theme.getColor(i2));
        textView2.setTag(Integer.valueOf(i2));
        textView2.setText(LocaleController.getString(R.string.LoadingStatsDescription));
        textView2.setGravity(1);
        this.progressLayout.addView(rLottieImageView, LayoutHelper.createLinear(120, 120, 1, 0, 0, 0, 20));
        this.progressLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 10));
        this.progressLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1));
        addView(this.progressLayout, LayoutHelper.createFrame(240, -2.0f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
    }

    public void updateRows(boolean z) {
        ArrayList arrayList;
        ItemInternal itemInternal;
        ArrayList arrayList2;
        ItemInternal itemInternal2;
        ArrayList arrayList3;
        ItemInternal itemInternal3;
        ArrayList arrayList4 = new ArrayList(this.items);
        this.items.clear();
        if (this.boostsStatus != null) {
            this.items.add(new ItemInternal(4, false));
            this.items.add(new ItemInternal(1, LocaleController.getString(R.string.StatisticOverview)));
            this.items.add(new ItemInternal(0, false));
            this.items.add(new ItemInternal(2, false));
            if (this.boostsStatus.prepaid_giveaways.size() > 0) {
                this.items.add(new ItemInternal(12, LocaleController.getString(R.string.BoostingPreparedGiveaways)));
                int i = 0;
                while (i < this.boostsStatus.prepaid_giveaways.size()) {
                    this.items.add(new ItemInternal(11, this.boostsStatus.prepaid_giveaways.get(i), i == this.boostsStatus.prepaid_giveaways.size() - 1));
                    i++;
                }
                this.items.add(new ItemInternal(6, LocaleController.getString(R.string.BoostingSelectPaidGiveaway)));
            }
            this.items.add(new ItemInternal(13, LocaleController.getString(R.string.Boosters)));
            if (this.selectedTab == 0) {
                if (this.boosters.isEmpty()) {
                    this.items.add(new ItemInternal(8, false));
                    arrayList2 = this.items;
                    itemInternal2 = new ItemInternal(2, false);
                } else {
                    int i2 = 0;
                    while (i2 < this.boosters.size()) {
                        this.items.add(new ItemInternal(5, (TL_stories.Boost) this.boosters.get(i2), i2 == this.boosters.size() - 1 && !this.hasBoostsNext, this.selectedTab));
                        i2++;
                    }
                    if (this.hasBoostsNext) {
                        arrayList3 = this.items;
                        itemInternal3 = new ItemInternal(9, true);
                    } else {
                        arrayList3 = this.items;
                        itemInternal3 = new ItemInternal(7, false);
                    }
                    arrayList3.add(itemInternal3);
                    arrayList2 = this.items;
                    itemInternal2 = new ItemInternal(6, LocaleController.getString(isChannel() ? R.string.BoostersInfoDescription : R.string.BoostersInfoGroupDescription));
                }
            } else if (this.gifts.isEmpty()) {
                this.items.add(new ItemInternal(8, false));
                arrayList2 = this.items;
                itemInternal2 = new ItemInternal(2, false);
            } else {
                int i3 = 0;
                while (i3 < this.gifts.size()) {
                    this.items.add(new ItemInternal(5, (TL_stories.Boost) this.gifts.get(i3), i3 == this.gifts.size() - 1 && !this.hasGiftsNext, this.selectedTab));
                    i3++;
                }
                if (this.hasGiftsNext) {
                    arrayList = this.items;
                    itemInternal = new ItemInternal(9, true);
                } else {
                    arrayList = this.items;
                    itemInternal = new ItemInternal(7, false);
                }
                arrayList.add(itemInternal);
                arrayList2 = this.items;
                itemInternal2 = new ItemInternal(6, LocaleController.getString(isChannel() ? R.string.BoostersInfoDescription : R.string.BoostersInfoGroupDescription));
            }
            arrayList2.add(itemInternal2);
            this.items.add(new ItemInternal(1, LocaleController.getString(R.string.LinkForBoosting)));
            this.items.add(new ItemInternal(3, this.boostsStatus.boost_url));
            if (MessagesController.getInstance(this.currentAccount).giveawayGiftsPurchaseAvailable && ChatObject.hasAdminRights(this.currentChat)) {
                this.items.add(new ItemInternal(6, LocaleController.getString(isChannel() ? R.string.BoostingShareThisLink : R.string.BoostingShareThisLinkGroup)));
                this.items.add(new ItemInternal(10, true));
                this.items.add(new ItemInternal(6, LocaleController.getString(isChannel() ? R.string.BoostingGetMoreBoosts2 : R.string.BoostingGetMoreBoostsGroup)));
            }
        }
        AdapterWithDiffUtils adapterWithDiffUtils = this.adapter;
        if (z) {
            adapterWithDiffUtils.setItems(arrayList4, this.items);
        } else {
            adapterWithDiffUtils.notifyDataSetChanged();
        }
    }
}
