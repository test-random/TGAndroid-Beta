package org.telegram.ui.Stars;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
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
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChannelMonetizationLayout;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.ChatEditActivity;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Stars.BotStarsActivity;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.TwoStepVerificationActivity;
import org.telegram.ui.TwoStepVerificationSetupActivity;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;

public class BotStarsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ButtonWithCounterView adsButton;
    private ChatAvatarContainer avatarContainer;
    private int balanceBlockedUntil;
    private ButtonWithCounterView balanceButton;
    private LinearLayout balanceButtonsLayout;
    private EditTextBoldCursor balanceEditText;
    private OutlineTextContainerView balanceEditTextContainer;
    private long balanceEditTextValue;
    private CharSequence balanceInfo;
    private LinearLayout balanceLayout;
    private AnimatedTextView balanceSubtitle;
    private AnimatedTextView balanceTitle;
    private RelativeSizeSpan balanceTitleSizeSpan;
    public final long bot_id;
    private DecimalFormat formatter;
    private StatisticActivity.ChartViewData impressionsChart;
    private TLRPC.TL_payments_starsRevenueStats lastStats;
    private TLRPC.TL_starsRevenueStatus lastStatsStatus;
    private UniversalRecyclerView listView;
    private SpannableStringBuilder lock;
    private boolean proceedsAvailable;
    private CharSequence proceedsInfo;
    private double rate;
    private StatisticActivity.ChartViewData revenueChart;
    private StatisticActivity.ChartViewData revenueChartData;
    private CharSequence titleInfo;
    private ButtonWithCounterView tonBalanceButton;
    private LinearLayout tonBalanceLayout;
    private AnimatedTextView tonBalanceSubtitle;
    private AnimatedTextView tonBalanceTitle;
    private RelativeSizeSpan tonBalanceTitleSizeSpan;
    private StarsIntroActivity.StarsTransactionsLayout transactionsLayout;
    public final int type;
    private final CharSequence withdrawInfo;
    private Bulletin withdrawalBulletin;
    private final ChannelMonetizationLayout.ProceedOverview availableValue = ChannelMonetizationLayout.ProceedOverview.as("XTR", LocaleController.getString(R.string.BotStarsOverviewAvailableBalance));
    private final ChannelMonetizationLayout.ProceedOverview totalValue = ChannelMonetizationLayout.ProceedOverview.as("XTR", LocaleController.getString(R.string.BotStarsOverviewTotalBalance));
    private final ChannelMonetizationLayout.ProceedOverview totalProceedsValue = ChannelMonetizationLayout.ProceedOverview.as("XTR", LocaleController.getString(R.string.BotStarsOverviewTotalProceeds));
    private final ChannelMonetizationLayout.ProceedOverview tonAvailableValue = ChannelMonetizationLayout.ProceedOverview.as("TON", LocaleController.getString(R.string.BotMonetizationOverviewAvailable));
    private final ChannelMonetizationLayout.ProceedOverview tonLastWithdrawalValue = ChannelMonetizationLayout.ProceedOverview.as("TON", LocaleController.getString(R.string.BotMonetizationOverviewLastWithdrawal));
    private final ChannelMonetizationLayout.ProceedOverview tonLifetimeValue = ChannelMonetizationLayout.ProceedOverview.as("TON", LocaleController.getString(R.string.BotMonetizationOverviewTotal));
    private boolean balanceEditTextIgnore = false;
    private boolean balanceEditTextAll = true;
    private ColoredImageSpan[] starRef = new ColoredImageSpan[1];
    private int shakeDp = 4;
    private final int BALANCE = 1;
    private final int BUTTON_AFFILIATE = 2;
    private boolean tonTransactionsLoading = false;
    private boolean tonTransactionsEndReached = false;
    private int tonTransactionsCount = 0;
    private final ArrayList tonTransactions = new ArrayList();
    private Runnable setBalanceButtonText = new Runnable() {
        @Override
        public final void run() {
            BotStarsActivity.this.lambda$new$19();
        }
    };
    private int stats_dc = -1;

    public class NestedFrameLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {
        private NestedScrollingParentHelper nestedScrollingParentHelper;

        public NestedFrameLayout(Context context) {
            super(context);
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        public void lambda$onNestedScroll$0() {
            try {
                RecyclerListView currentListView = BotStarsActivity.this.transactionsLayout.getCurrentListView();
                if (currentListView == null || currentListView.getAdapter() == null) {
                    return;
                }
                currentListView.getAdapter().notifyDataSetChanged();
            } catch (Throwable unused) {
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
        }

        @Override
        public boolean onNestedPreFling(View view, float f, float f2) {
            return super.onNestedPreFling(view, f, f2);
        }

        @Override
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
            int i4;
            if (view == BotStarsActivity.this.listView && BotStarsActivity.this.transactionsLayout.isAttachedToWindow()) {
                boolean isSearchFieldVisible = ((BaseFragment) BotStarsActivity.this).actionBar.isSearchFieldVisible();
                int top = (((View) BotStarsActivity.this.transactionsLayout.getParent()).getTop() - AndroidUtilities.statusBarHeight) - ActionBar.getCurrentActionBarHeight();
                int bottom = ((View) BotStarsActivity.this.transactionsLayout.getParent()).getBottom();
                boolean z = false;
                if (i2 >= 0) {
                    if (isSearchFieldVisible) {
                        RecyclerListView currentListView = BotStarsActivity.this.transactionsLayout.getCurrentListView();
                        iArr[1] = i2;
                        if (top > 0) {
                            iArr[1] = i2 - i2;
                        }
                        if (currentListView == null || (i4 = iArr[1]) <= 0) {
                            return;
                        }
                        currentListView.scrollBy(0, i4);
                        return;
                    }
                    if (i2 > 0) {
                        RecyclerListView currentListView2 = BotStarsActivity.this.transactionsLayout.getCurrentListView();
                        if (BotStarsActivity.this.listView.getHeight() - bottom < 0 || currentListView2 == null || currentListView2.canScrollVertically(1)) {
                            return;
                        }
                        iArr[1] = i2;
                        BotStarsActivity.this.listView.stopScroll();
                        return;
                    }
                    return;
                }
                ((BaseFragment) BotStarsActivity.this).actionBar.setCastShadows(BotStarsActivity.this.listView.getHeight() - bottom < 0);
                if (BotStarsActivity.this.listView.getHeight() - bottom >= 0) {
                    RecyclerListView currentListView3 = BotStarsActivity.this.transactionsLayout.getCurrentListView();
                    int findFirstVisibleItemPosition = ((LinearLayoutManager) currentListView3.getLayoutManager()).findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition != -1) {
                        RecyclerView.ViewHolder findViewHolderForAdapterPosition = currentListView3.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                        int top2 = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() : -1;
                        int paddingTop = currentListView3.getPaddingTop();
                        if (top2 != paddingTop || findFirstVisibleItemPosition != 0) {
                            iArr[1] = findFirstVisibleItemPosition != 0 ? i2 : Math.max(i2, top2 - paddingTop);
                            currentListView3.scrollBy(0, i2);
                            z = true;
                        }
                    }
                }
                if (isSearchFieldVisible) {
                    if (z || top >= 0) {
                        iArr[1] = i2;
                    } else {
                        iArr[1] = i2 - Math.max(top, i2);
                    }
                }
            }
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            try {
                if (view == BotStarsActivity.this.listView && BotStarsActivity.this.transactionsLayout.isAttachedToWindow()) {
                    RecyclerListView currentListView = BotStarsActivity.this.transactionsLayout.getCurrentListView();
                    int bottom = ((View) BotStarsActivity.this.transactionsLayout.getParent()).getBottom();
                    ((BaseFragment) BotStarsActivity.this).actionBar.setCastShadows(BotStarsActivity.this.listView.getHeight() - bottom < 0);
                    if (BotStarsActivity.this.listView.getHeight() - bottom >= 0) {
                        iArr[1] = i4;
                        currentListView.scrollBy(0, i4);
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        BotStarsActivity.NestedFrameLayout.this.lambda$onNestedScroll$0();
                    }
                });
            }
        }

        @Override
        public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        }

        @Override
        public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
            return i == 2;
        }

        @Override
        public void onStopNestedScroll(View view) {
        }

        @Override
        public void onStopNestedScroll(View view, int i) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
        }
    }

    public BotStarsActivity(int i, long j) {
        this.type = i;
        this.bot_id = j;
        if (i == 0) {
            BotStarsController.getInstance(this.currentAccount).preloadStarsStats(j);
            BotStarsController.getInstance(this.currentAccount).invalidateTransactions(j, true);
        } else if (i == 1) {
            BotStarsController.getInstance(this.currentAccount).preloadTonStats(j);
        }
        this.withdrawInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.BotStarsWithdrawInfo), new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$new$0();
            }
        }), true);
    }

    private void checkStats() {
        ChartData chartData;
        ArrayList arrayList;
        TLRPC.TL_payments_starsRevenueStats starsRevenueStats = BotStarsController.getInstance(this.currentAccount).getStarsRevenueStats(this.bot_id);
        if (starsRevenueStats == this.lastStats) {
            if ((starsRevenueStats == null ? null : starsRevenueStats.status) == this.lastStatsStatus) {
                return;
            }
        }
        this.lastStats = starsRevenueStats;
        this.lastStatsStatus = starsRevenueStats != null ? starsRevenueStats.status : null;
        if (starsRevenueStats != null) {
            this.rate = starsRevenueStats.usd_rate;
            StatisticActivity.ChartViewData createViewData = StatisticActivity.createViewData(starsRevenueStats.revenue_graph, LocaleController.getString(R.string.BotStarsChartRevenue), 2);
            this.revenueChartData = createViewData;
            if (createViewData != null && (chartData = createViewData.chartData) != null && (arrayList = chartData.lines) != null && !arrayList.isEmpty() && this.revenueChartData.chartData.lines.get(0) != null) {
                StatisticActivity.ChartViewData chartViewData = this.revenueChartData;
                chartViewData.showAll = true;
                ((ChartData.Line) chartViewData.chartData.lines.get(0)).colorKey = Theme.key_color_yellow;
                this.revenueChartData.chartData.yRate = (float) ((1.0d / this.rate) / 100.0d);
            }
            TLRPC.TL_starsRevenueStatus tL_starsRevenueStatus = starsRevenueStats.status;
            setStarsBalance(tL_starsRevenueStatus.available_balance, tL_starsRevenueStatus.next_withdrawal_at);
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        UItem asShadow;
        TLRPC.BroadcastRevenueBalances broadcastRevenueBalances;
        TLRPC.TL_starsRevenueStatus tL_starsRevenueStatus;
        BotStarsController botStarsController = BotStarsController.getInstance(this.currentAccount);
        int i = this.type;
        if (i == 0) {
            arrayList.add(UItem.asChart(2, this.stats_dc, this.revenueChartData));
            arrayList.add(UItem.asShadow(-1, null));
            arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.BotStarsOverview)));
            TLRPC.TL_payments_starsRevenueStats starsRevenueStats = botStarsController.getStarsRevenueStats(this.bot_id);
            if (starsRevenueStats != null && (tL_starsRevenueStatus = starsRevenueStats.status) != null) {
                ChannelMonetizationLayout.ProceedOverview proceedOverview = this.availableValue;
                long j = tL_starsRevenueStatus.available_balance;
                proceedOverview.crypto_amount = j;
                proceedOverview.currency = "USD";
                double d = j;
                double d2 = this.rate;
                Double.isNaN(d);
                proceedOverview.amount = (long) (d * d2 * 100.0d);
                ChannelMonetizationLayout.ProceedOverview proceedOverview2 = this.totalValue;
                long j2 = tL_starsRevenueStatus.current_balance;
                proceedOverview2.crypto_amount = j2;
                proceedOverview2.currency = "USD";
                double d3 = j2;
                Double.isNaN(d3);
                proceedOverview2.amount = (long) (d3 * d2 * 100.0d);
                ChannelMonetizationLayout.ProceedOverview proceedOverview3 = this.totalProceedsValue;
                long j3 = tL_starsRevenueStatus.overall_revenue;
                proceedOverview3.crypto_amount = j3;
                proceedOverview3.currency = "USD";
                double d4 = j3;
                Double.isNaN(d4);
                proceedOverview3.amount = (long) (d4 * d2 * 100.0d);
                setStarsBalance(j, tL_starsRevenueStatus.next_withdrawal_at);
                this.balanceButtonsLayout.setVisibility(starsRevenueStats.status.withdrawal_enabled ? 0 : 8);
            }
            arrayList.add(UItem.asProceedOverview(this.availableValue));
            arrayList.add(UItem.asProceedOverview(this.totalValue));
            arrayList.add(UItem.asProceedOverview(this.totalProceedsValue));
            arrayList.add(UItem.asShadow(-2, LocaleController.getString(R.string.BotStarsOverviewInfo)));
            arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.BotStarsAvailableBalance)));
            arrayList.add(UItem.asCustom(1, this.balanceLayout));
            arrayList.add(UItem.asShadow(-3, this.withdrawInfo));
            if (getMessagesController().starrefConnectAllowed) {
                arrayList.add(AffiliateProgramFragment.ColorfulTextCell.Factory.as(2, Theme.getColor(Theme.key_color_green, this.resourceProvider), R.drawable.filled_earn_stars, ChatEditActivity.applyNewSpan(LocaleController.getString(R.string.BotAffiliateProgramRowTitle)), LocaleController.getString(R.string.BotAffiliateProgramRowText)));
                arrayList.add(UItem.asShadow(-4, null));
            }
            asShadow = UItem.asFullscreenCustom(this.transactionsLayout, 0);
        } else {
            if (i != 1) {
                return;
            }
            TL_stats.TL_broadcastRevenueStats tONRevenueStats = botStarsController.getTONRevenueStats(this.bot_id, true);
            if (this.titleInfo == null) {
                this.titleInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.BotMonetizationInfo, 50), -1, 3, new Runnable() {
                    @Override
                    public final void run() {
                        BotStarsActivity.this.lambda$fillItems$14();
                    }
                }, this.resourceProvider), true);
            }
            arrayList.add(UItem.asCenterShadow(this.titleInfo));
            if (this.impressionsChart == null && tONRevenueStats != null) {
                StatisticActivity.ChartViewData createViewData = StatisticActivity.createViewData(tONRevenueStats.top_hours_graph, LocaleController.getString(R.string.BotMonetizationGraphImpressions), 0);
                this.impressionsChart = createViewData;
                if (createViewData != null) {
                    createViewData.useHourFormat = true;
                }
            }
            StatisticActivity.ChartViewData chartViewData = this.impressionsChart;
            if (chartViewData != null && !chartViewData.isEmpty) {
                arrayList.add(UItem.asChart(5, this.stats_dc, chartViewData));
                arrayList.add(UItem.asShadow(-1, null));
            }
            if (this.revenueChart == null && tONRevenueStats != null) {
                TL_stats.StatsGraph statsGraph = tONRevenueStats.revenue_graph;
                if (statsGraph != null) {
                    statsGraph.rate = (float) (1.0E7d / tONRevenueStats.usd_rate);
                }
                this.revenueChart = StatisticActivity.createViewData(statsGraph, LocaleController.getString(R.string.BotMonetizationGraphRevenue), 2);
            }
            StatisticActivity.ChartViewData chartViewData2 = this.revenueChart;
            if (chartViewData2 != null && !chartViewData2.isEmpty) {
                arrayList.add(UItem.asChart(2, this.stats_dc, chartViewData2));
                arrayList.add(UItem.asShadow(-2, null));
            }
            if (!this.proceedsAvailable && tONRevenueStats != null && (broadcastRevenueBalances = tONRevenueStats.balances) != null) {
                double d5 = tONRevenueStats.usd_rate;
                ChannelMonetizationLayout.ProceedOverview proceedOverview4 = this.tonAvailableValue;
                long j4 = broadcastRevenueBalances.available_balance;
                proceedOverview4.crypto_amount = j4;
                double d6 = j4;
                Double.isNaN(d6);
                long j5 = (long) ((d6 / 1.0E9d) * d5 * 100.0d);
                proceedOverview4.amount = j5;
                setBalance(j4, j5);
                this.tonAvailableValue.currency = "USD";
                ChannelMonetizationLayout.ProceedOverview proceedOverview5 = this.tonLastWithdrawalValue;
                TLRPC.BroadcastRevenueBalances broadcastRevenueBalances2 = tONRevenueStats.balances;
                long j6 = broadcastRevenueBalances2.current_balance;
                proceedOverview5.crypto_amount = j6;
                double d7 = j6;
                Double.isNaN(d7);
                proceedOverview5.amount = (long) ((d7 / 1.0E9d) * d5 * 100.0d);
                proceedOverview5.currency = "USD";
                ChannelMonetizationLayout.ProceedOverview proceedOverview6 = this.tonLifetimeValue;
                proceedOverview6.contains1 = true;
                long j7 = broadcastRevenueBalances2.overall_revenue;
                proceedOverview6.crypto_amount = j7;
                double d8 = j7;
                Double.isNaN(d8);
                proceedOverview6.amount = (long) ((d8 / 1.0E9d) * d5 * 100.0d);
                proceedOverview6.currency = "USD";
                this.proceedsAvailable = true;
                this.tonBalanceButton.setVisibility((broadcastRevenueBalances2.available_balance <= 0 || !broadcastRevenueBalances2.withdrawal_enabled) ? 8 : 0);
            }
            if (this.proceedsAvailable) {
                arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.BotMonetizationOverview)));
                arrayList.add(UItem.asProceedOverview(this.tonAvailableValue));
                arrayList.add(UItem.asProceedOverview(this.tonLastWithdrawalValue));
                arrayList.add(UItem.asProceedOverview(this.tonLifetimeValue));
                if (this.proceedsInfo == null) {
                    int i2 = R.string.BotMonetizationProceedsTONInfo;
                    final int i3 = R.string.BotMonetizationProceedsTONInfoLink;
                    this.proceedsInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(i2), -1, 3, new Runnable() {
                        @Override
                        public final void run() {
                            BotStarsActivity.this.lambda$fillItems$15(i3);
                        }
                    }, this.resourceProvider), true);
                }
                arrayList.add(UItem.asShadow(-4, this.proceedsInfo));
            }
            arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.BotMonetizationBalance)));
            arrayList.add(UItem.asCustom(this.tonBalanceLayout));
            if (this.balanceInfo == null) {
                this.balanceInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(MessagesController.getInstance(this.currentAccount).channelRevenueWithdrawalEnabled ? R.string.BotMonetizationBalanceInfo : R.string.BotMonetizationBalanceInfoNotAvailable), -1, 3, new Runnable() {
                    @Override
                    public final void run() {
                        BotStarsActivity.this.lambda$fillItems$16();
                    }
                }), true);
            }
            arrayList.add(UItem.asShadow(-5, this.balanceInfo));
            if (!this.tonTransactionsEndReached || !this.tonTransactions.isEmpty()) {
                arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.BotMonetizationTransactions)));
                Iterator it = this.tonTransactions.iterator();
                while (it.hasNext()) {
                    arrayList.add(UItem.asTransaction((TL_stats.BroadcastRevenueTransaction) it.next()));
                }
                if (!this.tonTransactionsEndReached) {
                    arrayList.add(UItem.asFlicker(1, 7));
                    arrayList.add(UItem.asFlicker(2, 7));
                    arrayList.add(UItem.asFlicker(3, 7));
                }
            }
            asShadow = UItem.asShadow(-6, null);
        }
        arrayList.add(asShadow);
    }

    private void initWithdraw(final boolean z, final long j, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, final TwoStepVerificationActivity twoStepVerificationActivity) {
        TL_stats.TL_getBroadcastRevenueWithdrawalUrl tL_getBroadcastRevenueWithdrawalUrl;
        final Activity parentActivity = getParentActivity();
        TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (parentActivity == null || currentUser == null) {
            return;
        }
        if (z) {
            TLRPC.TL_payments_getStarsRevenueWithdrawalUrl tL_payments_getStarsRevenueWithdrawalUrl = new TLRPC.TL_payments_getStarsRevenueWithdrawalUrl();
            tL_payments_getStarsRevenueWithdrawalUrl.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.bot_id);
            if (inputCheckPasswordSRP == null) {
                inputCheckPasswordSRP = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            tL_payments_getStarsRevenueWithdrawalUrl.password = inputCheckPasswordSRP;
            tL_payments_getStarsRevenueWithdrawalUrl.stars = j;
            tL_getBroadcastRevenueWithdrawalUrl = tL_payments_getStarsRevenueWithdrawalUrl;
        } else {
            TL_stats.TL_getBroadcastRevenueWithdrawalUrl tL_getBroadcastRevenueWithdrawalUrl2 = new TL_stats.TL_getBroadcastRevenueWithdrawalUrl();
            tL_getBroadcastRevenueWithdrawalUrl2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.bot_id);
            if (inputCheckPasswordSRP == null) {
                inputCheckPasswordSRP = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            tL_getBroadcastRevenueWithdrawalUrl2.password = inputCheckPasswordSRP;
            tL_getBroadcastRevenueWithdrawalUrl = tL_getBroadcastRevenueWithdrawalUrl2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getBroadcastRevenueWithdrawalUrl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotStarsActivity.this.lambda$initWithdraw$24(twoStepVerificationActivity, parentActivity, z, j, tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$1(View view, boolean z) {
        this.balanceEditTextContainer.animateSelection(z ? 1.0f : 0.0f);
    }

    public void lambda$createView$10(View view) {
        if (!view.isEnabled() || this.tonBalanceButton.isLoading()) {
            return;
        }
        final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(1, new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() {
            @Override
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                BotStarsActivity.this.lambda$createView$8(twoStepVerificationActivity, inputCheckPasswordSRP);
            }
        });
        this.tonBalanceButton.setLoading(true);
        twoStepVerificationActivity.preload(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$createView$9(twoStepVerificationActivity);
            }
        });
    }

    public boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        withdraw();
        return true;
    }

    public void lambda$createView$3(View view) {
        withdraw();
    }

    public void lambda$createView$4() {
        this.adsButton.setLoading(false);
    }

    public void lambda$createView$5(TLObject tLObject, Context context) {
        if (tLObject instanceof TLRPC.TL_payments_starsRevenueAdsAccountUrl) {
            Browser.openUrl(context, ((TLRPC.TL_payments_starsRevenueAdsAccountUrl) tLObject).url);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$createView$4();
            }
        }, 1000L);
    }

    public void lambda$createView$6(final Context context, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$createView$5(tLObject, context);
            }
        });
    }

    public void lambda$createView$7(final Context context, View view) {
        if (!view.isEnabled() || this.adsButton.isLoading()) {
            return;
        }
        this.adsButton.setLoading(true);
        TLRPC.TL_payments_getStarsRevenueAdsAccountUrl tL_payments_getStarsRevenueAdsAccountUrl = new TLRPC.TL_payments_getStarsRevenueAdsAccountUrl();
        tL_payments_getStarsRevenueAdsAccountUrl.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.bot_id);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getStarsRevenueAdsAccountUrl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotStarsActivity.this.lambda$createView$6(context, tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$8(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        initWithdraw(false, 0L, inputCheckPasswordSRP, twoStepVerificationActivity);
    }

    public void lambda$createView$9(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.tonBalanceButton.setLoading(false);
        presentFragment(twoStepVerificationActivity);
    }

    public void lambda$fillItems$14() {
        showDialog(ChannelMonetizationLayout.makeLearnSheet(getContext(), true, this.resourceProvider));
    }

    public void lambda$fillItems$15(int i) {
        Browser.openUrl(getContext(), LocaleController.getString(i));
    }

    public void lambda$fillItems$16() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.BotMonetizationBalanceInfoLink));
    }

    public void lambda$initWithdraw$20(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, null));
    }

    public void lambda$initWithdraw$21(TLRPC.TL_error tL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, long j) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo(null, account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(account_password);
            initWithdraw(z, j, twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity);
        }
    }

    public void lambda$initWithdraw$22(final TwoStepVerificationActivity twoStepVerificationActivity, final boolean z, final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$initWithdraw$21(tL_error, tLObject, twoStepVerificationActivity, z, j);
            }
        });
    }

    public void lambda$initWithdraw$23(TLRPC.TL_error tL_error, final TwoStepVerificationActivity twoStepVerificationActivity, Activity activity, final boolean z, final long j, TLObject tLObject) {
        Context context;
        String str;
        int i;
        int i2;
        if (tL_error == null) {
            twoStepVerificationActivity.needHideProgress();
            twoStepVerificationActivity.lambda$onBackPressed$321();
            if (tLObject instanceof TL_stats.TL_broadcastRevenueWithdrawalUrl) {
                context = getContext();
                str = ((TL_stats.TL_broadcastRevenueWithdrawalUrl) tLObject).url;
            } else {
                if (!(tLObject instanceof TLRPC.TL_payments_starsRevenueWithdrawalUrl)) {
                    return;
                }
                this.balanceEditTextAll = true;
                context = getContext();
                str = ((TLRPC.TL_payments_starsRevenueWithdrawalUrl) tLObject).url;
            }
            Browser.openUrl(context, str);
            return;
        }
        if (!"PASSWORD_MISSING".equals(tL_error.text) && !tL_error.text.startsWith("PASSWORD_TOO_FRESH_") && !tL_error.text.startsWith("SESSION_TOO_FRESH_")) {
            if ("SRP_ID_INVALID".equals(tL_error.text)) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                        BotStarsActivity.this.lambda$initWithdraw$22(twoStepVerificationActivity, z, j, tLObject2, tL_error2);
                    }
                }, 8);
                return;
            }
            if (twoStepVerificationActivity != null) {
                twoStepVerificationActivity.needHideProgress();
                twoStepVerificationActivity.lambda$onBackPressed$321();
            }
            BulletinFactory.showError(tL_error);
            return;
        }
        if (twoStepVerificationActivity != null) {
            twoStepVerificationActivity.needHideProgress();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(LocaleController.getString(R.string.EditAdminTransferAlertTitle));
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView textView = new TextView(activity);
        int i3 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i3));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.WithdrawChannelAlertText)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout2 = new LinearLayout(activity);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(activity);
        int i4 = R.drawable.list_circle;
        imageView.setImageResource(i4);
        imageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
        int color = Theme.getColor(i3);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
        TextView textView2 = new TextView(activity);
        textView2.setTextColor(Theme.getColor(i3));
        textView2.setTextSize(1, 16.0f);
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EditAdminTransferAlertText1)));
        if (LocaleController.isRTL) {
            linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
            linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 5));
        } else {
            linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2));
            linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
        }
        LinearLayout linearLayout3 = new LinearLayout(activity);
        linearLayout3.setOrientation(0);
        linearLayout.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
        ImageView imageView2 = new ImageView(activity);
        imageView2.setImageResource(i4);
        imageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
        imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i3), mode));
        TextView textView3 = new TextView(activity);
        textView3.setTextColor(Theme.getColor(i3));
        textView3.setTextSize(1, 16.0f);
        textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EditAdminTransferAlertText2)));
        if (LocaleController.isRTL) {
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
            i = 5;
            linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2, 5));
        } else {
            i = 5;
            linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2));
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
        }
        if ("PASSWORD_MISSING".equals(tL_error.text)) {
            builder.setPositiveButton(LocaleController.getString(R.string.EditAdminTransferSetPassword), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i5) {
                    BotStarsActivity.this.lambda$initWithdraw$20(dialogInterface, i5);
                }
            });
            i2 = R.string.Cancel;
        } else {
            TextView textView4 = new TextView(activity);
            textView4.setTextColor(Theme.getColor(i3));
            textView4.setTextSize(1, 16.0f);
            if (!LocaleController.isRTL) {
                i = 3;
            }
            textView4.setGravity(i | 48);
            textView4.setText(LocaleController.getString(R.string.EditAdminTransferAlertText3));
            linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
            i2 = R.string.OK;
        }
        builder.setNegativeButton(LocaleController.getString(i2), null);
        AlertDialog create = builder.create();
        if (twoStepVerificationActivity != null) {
            twoStepVerificationActivity.showDialog(create);
        } else {
            showDialog(create);
        }
    }

    public void lambda$initWithdraw$24(final TwoStepVerificationActivity twoStepVerificationActivity, final Activity activity, final boolean z, final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$initWithdraw$23(tL_error, twoStepVerificationActivity, activity, z, j, tLObject);
            }
        });
    }

    public void lambda$loadTonTransactions$17(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_stats.TL_broadcastRevenueTransactions) {
            TL_stats.TL_broadcastRevenueTransactions tL_broadcastRevenueTransactions = (TL_stats.TL_broadcastRevenueTransactions) tLObject;
            this.tonTransactionsCount = tL_broadcastRevenueTransactions.count;
            this.tonTransactions.addAll(tL_broadcastRevenueTransactions.transactions);
            this.tonTransactionsEndReached = this.tonTransactions.size() >= this.tonTransactionsCount || tL_broadcastRevenueTransactions.transactions.isEmpty();
        } else if (tL_error != null) {
            BulletinFactory.showError(tL_error);
            this.tonTransactionsEndReached = true;
        }
        this.tonTransactionsLoading = false;
        UniversalAdapter universalAdapter = this.listView.adapter;
        if (universalAdapter != null) {
            universalAdapter.update(true);
        }
    }

    public void lambda$loadTonTransactions$18(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$loadTonTransactions$17(tLObject, tL_error);
            }
        });
    }

    public void lambda$new$0() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.BotStarsWithdrawInfoLink));
    }

    public void lambda$new$19() {
        int currentTime = getConnectionsManager().getCurrentTime();
        this.balanceButton.setEnabled(this.balanceEditTextValue > 0 || this.balanceBlockedUntil > currentTime);
        if (currentTime >= this.balanceBlockedUntil) {
            this.balanceButton.setSubText(null, true);
            this.balanceButton.setText(StarsIntroActivity.replaceStars(this.balanceEditTextAll ? LocaleController.getString(R.string.BotStarsButtonWithdrawShortAll) : LocaleController.formatPluralStringSpaced("BotStarsButtonWithdrawShort", (int) this.balanceEditTextValue), this.starRef), true);
            return;
        }
        this.balanceButton.setText(LocaleController.getString(R.string.BotStarsButtonWithdrawShortUntil), true);
        if (this.lock == null) {
            this.lock = new SpannableStringBuilder("l");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_switch_lock);
            coloredImageSpan.setTopOffset(1);
            this.lock.setSpan(coloredImageSpan, 0, 1, 33);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) this.lock).append((CharSequence) untilString(this.balanceBlockedUntil - currentTime));
        this.balanceButton.setSubText(spannableStringBuilder, true);
        Bulletin bulletin = this.withdrawalBulletin;
        if (bulletin != null && (bulletin.getLayout() instanceof Bulletin.LottieLayout) && this.withdrawalBulletin.getLayout().isAttachedToWindow()) {
            ((Bulletin.LottieLayout) this.withdrawalBulletin.getLayout()).textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotStarsWithdrawalToast, untilString(this.balanceBlockedUntil - currentTime))));
        }
        AndroidUtilities.cancelRunOnUIThread(this.setBalanceButtonText);
        AndroidUtilities.runOnUIThread(this.setBalanceButtonText, 1000L);
    }

    public void lambda$withdraw$11() {
        Bulletin.hideVisible();
        long availableBalance = BotStarsController.getInstance(this.currentAccount).getAvailableBalance(this.bot_id);
        if (availableBalance < getMessagesController().starsRevenueWithdrawalMin) {
            this.balanceEditTextAll = true;
        } else {
            this.balanceEditTextAll = false;
            availableBalance = getMessagesController().starsRevenueWithdrawalMin;
        }
        this.balanceEditTextValue = availableBalance;
        this.balanceEditTextIgnore = true;
        this.balanceEditText.setText(Long.toString(this.balanceEditTextValue));
        EditTextBoldCursor editTextBoldCursor = this.balanceEditText;
        editTextBoldCursor.setSelection(editTextBoldCursor.getText().length());
        this.balanceEditTextIgnore = false;
        AndroidUtilities.cancelRunOnUIThread(this.setBalanceButtonText);
        this.setBalanceButtonText.run();
    }

    public void lambda$withdraw$12(long j, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        initWithdraw(true, j, inputCheckPasswordSRP, twoStepVerificationActivity);
    }

    public void lambda$withdraw$13(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.balanceButton.setLoading(false);
        presentFragment(twoStepVerificationActivity);
    }

    public void loadTonTransactions() {
        if (this.tonTransactionsLoading || this.tonTransactionsEndReached) {
            return;
        }
        this.tonTransactionsLoading = true;
        TL_stats.TL_getBroadcastRevenueTransactions tL_getBroadcastRevenueTransactions = new TL_stats.TL_getBroadcastRevenueTransactions();
        tL_getBroadcastRevenueTransactions.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.bot_id);
        tL_getBroadcastRevenueTransactions.offset = this.tonTransactions.size();
        tL_getBroadcastRevenueTransactions.limit = this.tonTransactions.isEmpty() ? 5 : 20;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getBroadcastRevenueTransactions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotStarsActivity.this.lambda$loadTonTransactions$18(tLObject, tL_error);
            }
        });
    }

    public void onItemClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.instanceOf(StarsIntroActivity.StarsTransactionView.Factory.class)) {
            StarsIntroActivity.showTransactionSheet(getContext(), true, this.bot_id, this.currentAccount, (TL_stars.StarsTransaction) uItem.object, getResourceProvider());
        } else if (uItem.object instanceof TL_stats.BroadcastRevenueTransaction) {
            ChannelMonetizationLayout.showTransactionSheet(getContext(), this.currentAccount, (TL_stats.BroadcastRevenueTransaction) uItem.object, this.bot_id, this.resourceProvider);
        } else if (uItem.id == 2) {
            presentFragment(new ChannelAffiliateProgramsFragment(this.bot_id));
        }
    }

    public boolean onItemLongClick(UItem uItem, View view, int i, float f, float f2) {
        return false;
    }

    private void setBalance(long j, long j2) {
        if (this.formatter == null) {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
            decimalFormatSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
            this.formatter = decimalFormat;
            decimalFormat.setMinimumFractionDigits(2);
            this.formatter.setMaximumFractionDigits(6);
            this.formatter.setGroupingUsed(false);
        }
        DecimalFormat decimalFormat2 = this.formatter;
        double d = j;
        Double.isNaN(d);
        double d2 = d / 1.0E9d;
        decimalFormat2.setMaximumFractionDigits(d2 > 1.5d ? 2 : 6);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(ChannelMonetizationLayout.replaceTON("TON " + this.formatter.format(d2), this.tonBalanceTitle.getPaint(), 0.9f, true));
        int indexOf = TextUtils.indexOf(spannableStringBuilder, ".");
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(this.tonBalanceTitleSizeSpan, indexOf, spannableStringBuilder.length(), 33);
        }
        this.tonBalanceTitle.setText(spannableStringBuilder);
        this.tonBalanceSubtitle.setText("≈" + BillingController.getInstance().formatCurrency(j2, "USD"));
    }

    private void setStarsBalance(long j, int i) {
        if (this.balanceTitle == null || this.balanceSubtitle == null) {
            return;
        }
        double d = this.rate;
        double d2 = j;
        Double.isNaN(d2);
        long j2 = (long) (d * d2 * 100.0d);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(StarsIntroActivity.replaceStarsWithPlain("XTR " + LocaleController.formatNumber(j, ' '), 1.0f));
        int indexOf = TextUtils.indexOf(spannableStringBuilder, ".");
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(this.balanceTitleSizeSpan, indexOf, spannableStringBuilder.length(), 33);
        }
        this.balanceTitle.setText(spannableStringBuilder);
        this.balanceSubtitle.setText("≈" + BillingController.getInstance().formatCurrency(j2, "USD"));
        this.balanceEditTextContainer.setVisibility(j2 > 0 ? 0 : 8);
        if (this.balanceEditTextAll) {
            this.balanceEditTextIgnore = true;
            EditTextBoldCursor editTextBoldCursor = this.balanceEditText;
            this.balanceEditTextValue = j;
            editTextBoldCursor.setText(Long.toString(j));
            EditTextBoldCursor editTextBoldCursor2 = this.balanceEditText;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.getText().length());
            this.balanceEditTextIgnore = false;
            this.balanceButton.setEnabled(this.balanceEditTextValue > 0);
        }
        this.balanceBlockedUntil = i;
        AndroidUtilities.cancelRunOnUIThread(this.setBalanceButtonText);
        this.setBalanceButtonText.run();
    }

    public static String untilString(int i) {
        int i2 = i / 86400;
        int i3 = i - (86400 * i2);
        int i4 = i3 / 3600;
        int i5 = i3 - (i4 * 3600);
        int i6 = i5 / 60;
        int i7 = i5 - (i6 * 60);
        if (i2 == 0) {
            return i4 == 0 ? String.format(Locale.ENGLISH, "%02d:%02d", Integer.valueOf(i6), Integer.valueOf(i7)) : String.format(Locale.ENGLISH, "%02d:%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i6), Integer.valueOf(i7));
        }
        int i8 = R.string.PeriodDHM;
        Locale locale = Locale.ENGLISH;
        return LocaleController.formatString(i8, String.format(locale, "%02d", Integer.valueOf(i2)), String.format(locale, "%02d", Integer.valueOf(i4)), String.format(locale, "%02d", Integer.valueOf(i6)));
    }

    private void withdraw() {
        if (!this.balanceButton.isEnabled() || this.balanceButton.isLoading()) {
            return;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        if (this.balanceBlockedUntil > currentTime) {
            this.withdrawalBulletin = BulletinFactory.of(this).createSimpleBulletin(R.raw.timer_3, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotStarsWithdrawalToast, untilString(this.balanceBlockedUntil - currentTime)))).show();
            return;
        }
        if (this.balanceEditTextValue < getMessagesController().starsRevenueWithdrawalMin) {
            BulletinFactory.of(this).createSimpleBulletin(getContext().getResources().getDrawable(R.drawable.star_small_inner).mutate(), AndroidUtilities.replaceSingleTag(LocaleController.formatPluralString("BotStarsWithdrawMinLimit", (int) getMessagesController().starsRevenueWithdrawalMin, new Object[0]), new Runnable() {
                @Override
                public final void run() {
                    BotStarsActivity.this.lambda$withdraw$11();
                }
            })).show();
            return;
        }
        final long j = this.balanceEditTextValue;
        final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(1, new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() {
            @Override
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                BotStarsActivity.this.lambda$withdraw$12(j, twoStepVerificationActivity, inputCheckPasswordSRP);
            }
        });
        this.balanceButton.setLoading(true);
        twoStepVerificationActivity.preload(new Runnable() {
            @Override
            public final void run() {
                BotStarsActivity.this.lambda$withdraw$13(twoStepVerificationActivity);
            }
        });
    }

    @Override
    public View createView(final Context context) {
        ChatAvatarContainer chatAvatarContainer;
        int i;
        NestedFrameLayout nestedFrameLayout = new NestedFrameLayout(context);
        ChatAvatarContainer chatAvatarContainer2 = new ChatAvatarContainer(context, null, false);
        this.avatarContainer = chatAvatarContainer2;
        chatAvatarContainer2.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.avatarContainer.getAvatarImageView().setScaleX(0.9f);
        this.avatarContainer.getAvatarImageView().setScaleY(0.9f);
        this.avatarContainer.setRightAvatarPadding(-AndroidUtilities.dp(3.0f));
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 50.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.bot_id));
        this.avatarContainer.setUserAvatar(user, true);
        this.avatarContainer.setTitle(UserObject.getUserName(user));
        if (this.type == 0) {
            chatAvatarContainer = this.avatarContainer;
            i = R.string.BotStatsStars;
        } else {
            chatAvatarContainer = this.avatarContainer;
            i = R.string.BotStatsTON;
        }
        chatAvatarContainer.setSubtitle(LocaleController.getString(i));
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    BotStarsActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer3 = this.avatarContainer;
        int i2 = Theme.key_player_actionBarTitle;
        chatAvatarContainer3.setTitleColors(Theme.getColor(i2), Theme.getColor(Theme.key_player_actionBarSubtitle));
        this.actionBar.setItemsColor(Theme.getColor(i2), false);
        this.actionBar.setItemsColor(Theme.getColor(i2), true);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), false);
        ActionBar actionBar = this.actionBar;
        int i3 = Theme.key_windowBackgroundWhite;
        actionBar.setBackgroundColor(Theme.getColor(i3));
        this.transactionsLayout = new StarsIntroActivity.StarsTransactionsLayout(context, this.currentAccount, this.bot_id, getClassGuid(), getResourceProvider());
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            protected void onMeasure(int i4, int i5) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i4), 1073741824), i5);
            }
        };
        this.balanceLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.balanceLayout.setBackgroundColor(Theme.getColor(i3, getResourceProvider()));
        this.balanceLayout.setPadding(0, 0, 0, AndroidUtilities.dp(17.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, true);
        this.balanceTitle = animatedTextView;
        animatedTextView.setTypeface(AndroidUtilities.bold());
        AnimatedTextView animatedTextView2 = this.balanceTitle;
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        animatedTextView2.setTextColor(Theme.getColor(i4, getResourceProvider()));
        this.balanceTitle.setTextSize(AndroidUtilities.dp(32.0f));
        this.balanceTitle.setGravity(17);
        this.balanceTitleSizeSpan = new RelativeSizeSpan(0.6770833f);
        this.balanceLayout.addView(this.balanceTitle, LayoutHelper.createLinear(-1, 38, 49, 22, 15, 22, 0));
        AnimatedTextView animatedTextView3 = new AnimatedTextView(context, true, true, true);
        this.balanceSubtitle = animatedTextView3;
        animatedTextView3.setGravity(17);
        AnimatedTextView animatedTextView4 = this.balanceSubtitle;
        int i5 = Theme.key_windowBackgroundWhiteGrayText;
        animatedTextView4.setTextColor(Theme.getColor(i5, getResourceProvider()));
        this.balanceSubtitle.setTextSize(AndroidUtilities.dp(14.0f));
        this.balanceLayout.addView(this.balanceSubtitle, LayoutHelper.createFrame(-1, 17.0f, 49, 22.0f, 4.0f, 22.0f, 0.0f));
        OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (BotStarsActivity.this.balanceEditText != null && !BotStarsActivity.this.balanceEditText.isFocusable()) {
                    BotStarsActivity.this.balanceEditText.setFocusable(true);
                    BotStarsActivity.this.balanceEditText.setFocusableInTouchMode(true);
                    int findPositionByItemId = BotStarsActivity.this.listView.findPositionByItemId(1);
                    if (findPositionByItemId >= 0 && findPositionByItemId < BotStarsActivity.this.listView.adapter.getItemCount()) {
                        BotStarsActivity.this.listView.stopScroll();
                        BotStarsActivity.this.listView.smoothScrollToPosition(findPositionByItemId);
                    }
                    BotStarsActivity.this.balanceEditText.requestFocus();
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.balanceEditTextContainer = outlineTextContainerView;
        outlineTextContainerView.setText(LocaleController.getString(R.string.BotStarsWithdrawPlaceholder));
        this.balanceEditTextContainer.setLeftPadding(AndroidUtilities.dp(36.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AndroidUtilities.hideKeyboard(this);
            }
        };
        this.balanceEditText = editTextBoldCursor;
        editTextBoldCursor.setFocusable(false);
        this.balanceEditText.setTextColor(getThemedColor(i4));
        this.balanceEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.balanceEditText.setCursorWidth(1.5f);
        this.balanceEditText.setBackground(null);
        this.balanceEditText.setTextSize(1, 18.0f);
        this.balanceEditText.setMaxLines(1);
        int dp = AndroidUtilities.dp(16.0f);
        this.balanceEditText.setPadding(AndroidUtilities.dp(6.0f), dp, dp, dp);
        this.balanceEditText.setInputType(2);
        this.balanceEditText.setTypeface(Typeface.DEFAULT);
        this.balanceEditText.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        this.balanceEditText.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        this.balanceEditText.setGravity(LocaleController.isRTL ? 5 : 3);
        this.balanceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public final void onFocusChange(View view, boolean z) {
                BotStarsActivity.this.lambda$createView$1(view, z);
            }
        });
        this.balanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                long availableBalance = BotStarsController.getInstance(((BaseFragment) BotStarsActivity.this).currentAccount).getAvailableBalance(BotStarsActivity.this.bot_id);
                BotStarsActivity.this.balanceEditTextValue = TextUtils.isEmpty(editable) ? 0L : Long.parseLong(editable.toString());
                if (BotStarsActivity.this.balanceEditTextValue > availableBalance) {
                    BotStarsActivity.this.balanceEditTextValue = availableBalance;
                    BotStarsActivity.this.balanceEditTextIgnore = true;
                    BotStarsActivity.this.balanceEditText.setText(Long.toString(BotStarsActivity.this.balanceEditTextValue));
                    BotStarsActivity.this.balanceEditText.setSelection(BotStarsActivity.this.balanceEditText.getText().length());
                    BotStarsActivity.this.balanceEditTextIgnore = false;
                }
                BotStarsActivity botStarsActivity = BotStarsActivity.this;
                botStarsActivity.balanceEditTextAll = botStarsActivity.balanceEditTextValue == availableBalance;
                AndroidUtilities.cancelRunOnUIThread(BotStarsActivity.this.setBalanceButtonText);
                BotStarsActivity.this.setBalanceButtonText.run();
                if (BotStarsActivity.this.balanceEditTextIgnore) {
                    return;
                }
                BotStarsActivity.this.balanceEditTextAll = false;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i6, int i7, int i8) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i6, int i7, int i8) {
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.star_small_inner);
        linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 19, 14, 0, 0, 0));
        linearLayout2.addView(this.balanceEditText, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
        this.balanceEditTextContainer.attachEditText(this.balanceEditText);
        this.balanceEditTextContainer.addView(linearLayout2, LayoutHelper.createFrame(-1, -2, 48));
        this.balanceEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i6, KeyEvent keyEvent) {
                boolean lambda$createView$2;
                lambda$createView$2 = BotStarsActivity.this.lambda$createView$2(textView, i6, keyEvent);
                return lambda$createView$2;
            }
        });
        this.balanceLayout.addView(this.balanceEditTextContainer, LayoutHelper.createLinear(-1, -2, 1, 18, 14, 18, 2));
        this.balanceEditTextContainer.setVisibility(8);
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.balanceButtonsLayout = linearLayout3;
        linearLayout3.setOrientation(0);
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, getResourceProvider()) {
            @Override
            protected boolean subTextSplitToWords() {
                return false;
            }
        };
        this.balanceButton = buttonWithCounterView;
        buttonWithCounterView.setEnabled(MessagesController.getInstance(this.currentAccount).channelRevenueWithdrawalEnabled);
        this.balanceButton.setText(LocaleController.getString(R.string.BotStarsButtonWithdrawShortAll), false);
        this.balanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BotStarsActivity.this.lambda$createView$3(view);
            }
        });
        ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, getResourceProvider());
        this.adsButton = buttonWithCounterView2;
        buttonWithCounterView2.setEnabled(true);
        this.adsButton.setText(LocaleController.getString(R.string.MonetizationStarsAds), false);
        this.adsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BotStarsActivity.this.lambda$createView$7(context, view);
            }
        });
        this.balanceButtonsLayout.addView(this.balanceButton, LayoutHelper.createLinear(-1, 48, 1.0f, 119));
        this.balanceButtonsLayout.addView(new Space(context), LayoutHelper.createLinear(8, 48, 0.0f, 119));
        this.balanceButtonsLayout.addView(this.adsButton, LayoutHelper.createLinear(-1, 48, 1.0f, 119));
        this.balanceLayout.addView(this.balanceButtonsLayout, LayoutHelper.createFrame(-1, 48.0f, 55, 18.0f, 13.0f, 18.0f, 0.0f));
        LinearLayout linearLayout4 = new LinearLayout(context) {
            @Override
            protected void onMeasure(int i6, int i7) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i6), 1073741824), i7);
            }
        };
        this.tonBalanceLayout = linearLayout4;
        linearLayout4.setOrientation(1);
        this.tonBalanceLayout.setBackgroundColor(Theme.getColor(i3, this.resourceProvider));
        this.tonBalanceLayout.setPadding(0, 0, 0, AndroidUtilities.dp(17.0f));
        AnimatedTextView animatedTextView5 = new AnimatedTextView(context, false, true, true);
        this.tonBalanceTitle = animatedTextView5;
        animatedTextView5.setTypeface(AndroidUtilities.bold());
        this.tonBalanceTitle.setTextColor(Theme.getColor(i4, this.resourceProvider));
        this.tonBalanceTitle.setTextSize(AndroidUtilities.dp(32.0f));
        this.tonBalanceTitle.setGravity(17);
        this.tonBalanceTitleSizeSpan = new RelativeSizeSpan(0.6770833f);
        this.tonBalanceLayout.addView(this.tonBalanceTitle, LayoutHelper.createLinear(-1, 38, 49, 22, 15, 22, 0));
        AnimatedTextView animatedTextView6 = new AnimatedTextView(context, true, true, true);
        this.tonBalanceSubtitle = animatedTextView6;
        animatedTextView6.setGravity(17);
        this.tonBalanceSubtitle.setTextColor(Theme.getColor(i5, this.resourceProvider));
        this.tonBalanceSubtitle.setTextSize(AndroidUtilities.dp(14.0f));
        this.tonBalanceLayout.addView(this.tonBalanceSubtitle, LayoutHelper.createFrame(-1, 17.0f, 49, 22.0f, 4.0f, 22.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView3 = new ButtonWithCounterView(context, this.resourceProvider);
        this.tonBalanceButton = buttonWithCounterView3;
        buttonWithCounterView3.setEnabled(MessagesController.getInstance(this.currentAccount).channelRevenueWithdrawalEnabled);
        this.tonBalanceButton.setText(LocaleController.getString(R.string.MonetizationWithdraw), false);
        this.tonBalanceButton.setVisibility(8);
        this.tonBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BotStarsActivity.this.lambda$createView$10(view);
            }
        });
        this.tonBalanceLayout.addView(this.tonBalanceButton, LayoutHelper.createFrame(-1, 48.0f, 55, 18.0f, 13.0f, 18.0f, 0.0f));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                BotStarsActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                BotStarsActivity.this.onItemClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                boolean onItemLongClick;
                onItemLongClick = BotStarsActivity.this.onItemLongClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                return Boolean.valueOf(onItemLongClick);
            }
        });
        this.listView = universalRecyclerView;
        universalRecyclerView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundGray));
        nestedFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i6, int i7) {
                if (!BotStarsActivity.this.listView.canScrollVertically(1) || BotStarsActivity.this.isLoadingVisible()) {
                    BotStarsActivity.this.loadTonTransactions();
                }
            }
        });
        this.fragmentView = nestedFrameLayout;
        return nestedFrameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.botStarsUpdated && ((Long) objArr[0]).longValue() == this.bot_id) {
            checkStats();
        }
    }

    @Override
    public boolean isLightStatusBar() {
        return AndroidUtilities.computePerceivedBrightness(Theme.getColor(Theme.key_windowBackgroundWhite)) > 0.721f;
    }

    public boolean isLoadingVisible() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botStarsUpdated);
        checkStats();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botStarsUpdated);
        super.onFragmentDestroy();
    }
}
