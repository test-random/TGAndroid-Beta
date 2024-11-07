package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
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
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChannelMonetizationLayout;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Stars.BotStarsActivity;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.TwoStepVerificationActivity;

public class ChannelMonetizationLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {
    public static ChannelMonetizationLayout instance;
    private static HashMap tonString;
    private ActionBar actionBar;
    private final ProceedOverview availableValue;
    private final ButtonWithCounterView balanceButton;
    private final CharSequence balanceInfo;
    private final LinearLayout balanceLayout;
    private final AnimatedTextView balanceSubtitle;
    private final AnimatedTextView balanceTitle;
    private final RelativeSizeSpan balanceTitleSizeSpan;
    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private final int currentAccount;
    private int currentBoostLevel;
    public final long dialogId;
    private DecimalFormat formatter;
    private final BaseFragment fragment;
    private StatisticActivity.ChartViewData impressionsChart;
    private boolean initialSwitchOffValue;
    private final ProceedOverview lastWithdrawalValue;
    private final ProceedOverview lifetimeValue;
    private final UniversalRecyclerView listView;
    private SpannableStringBuilder lock;
    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private boolean proceedsAvailable;
    private final CharSequence proceedsInfo;
    private final FrameLayout progress;
    private final Theme.ResourcesProvider resourcesProvider;
    private StatisticActivity.ChartViewData revenueChart;
    private final Runnable sendCpmUpdateRunnable;
    private Runnable setStarsBalanceButtonText;
    private int shakeDp;
    private ColoredImageSpan[] starRef;
    private final ButtonWithCounterView starsAdsButton;
    private long starsBalance;
    private int starsBalanceBlockedUntil;
    private final ButtonWithCounterView starsBalanceButton;
    private final LinearLayout starsBalanceButtonsLayout;
    private EditTextBoldCursor starsBalanceEditText;
    private boolean starsBalanceEditTextAll;
    private OutlineTextContainerView starsBalanceEditTextContainer;
    private boolean starsBalanceEditTextIgnore;
    private long starsBalanceEditTextValue;
    private final CharSequence starsBalanceInfo;
    private final LinearLayout starsBalanceLayout;
    private final AnimatedTextView starsBalanceSubtitle;
    private final AnimatedTextView starsBalanceTitle;
    private final RelativeSizeSpan starsBalanceTitleSizeSpan;
    public final boolean starsRevenueAvailable;
    private StatisticActivity.ChartViewData starsRevenueChart;
    private double stars_rate;
    private boolean switchOffValue;
    private final CharSequence titleInfo;
    public final boolean tonRevenueAvailable;
    private double ton_rate;
    private final ChannelTransactionsView transactionsLayout;
    private Bulletin withdrawalBulletin;

    public class ChannelTransactionsView extends LinearLayout {
        private final PageAdapter adapter;
        private final int currentAccount;
        private final long dialogId;
        private boolean[] loadingTransactions;
        private String starsLastOffset;
        private final ArrayList starsTransactions;
        private final ViewPagerFixed.TabsView tabsView;
        private final ArrayList tonTransactions;
        private int tonTransactionsTotalCount;
        private final Runnable updateParentList;
        private final ViewPagerFixed viewPager;

        public class Page extends FrameLayout {
            private final long bot_id;
            private final int currentAccount;
            private final UniversalRecyclerView listView;
            private final Runnable loadMore;
            private final Theme.ResourcesProvider resourcesProvider;
            private final int type;

            public Page(Context context, long j, int i, int i2, int i3, final Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
                super(context);
                this.type = i;
                this.currentAccount = i2;
                this.bot_id = j;
                this.resourcesProvider = resourcesProvider;
                this.loadMore = runnable;
                UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, i2, i3, true, new Utilities.Callback2() {
                    @Override
                    public final void run(Object obj, Object obj2) {
                        ChannelMonetizationLayout.ChannelTransactionsView.Page.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
                    }
                }, new Utilities.Callback5() {
                    @Override
                    public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                        ChannelMonetizationLayout.ChannelTransactionsView.Page.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                    }
                }, null, resourcesProvider);
                this.listView = universalRecyclerView;
                addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
                universalRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int i4, int i5) {
                        if (!Page.this.listView.canScrollVertically(1) || Page.this.isLoadingVisible()) {
                            runnable.run();
                        }
                    }
                });
            }

            public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
                int i = this.type;
                if (i == 0) {
                    Iterator it = ChannelTransactionsView.this.starsTransactions.iterator();
                    while (it.hasNext()) {
                        arrayList.add(StarsIntroActivity.StarsTransactionView.Factory.asTransaction((TL_stars.StarsTransaction) it.next(), true));
                    }
                    if (TextUtils.isEmpty(ChannelTransactionsView.this.starsLastOffset)) {
                        return;
                    }
                } else {
                    if (i != 1) {
                        return;
                    }
                    Iterator it2 = ChannelTransactionsView.this.tonTransactions.iterator();
                    while (it2.hasNext()) {
                        arrayList.add(UItem.asTransaction((TL_stats.BroadcastRevenueTransaction) it2.next()));
                    }
                    if (ChannelTransactionsView.this.tonTransactionsTotalCount - ChannelTransactionsView.this.tonTransactions.size() <= 0) {
                        return;
                    }
                }
                arrayList.add(UItem.asFlicker(arrayList.size(), 7));
                arrayList.add(UItem.asFlicker(arrayList.size(), 7));
                arrayList.add(UItem.asFlicker(arrayList.size(), 7));
            }

            public void onClick(UItem uItem, View view, int i, float f, float f2) {
                Object obj = uItem.object;
                if (obj instanceof TL_stars.StarsTransaction) {
                    StarsIntroActivity.showTransactionSheet(getContext(), true, ChannelTransactionsView.this.dialogId, this.currentAccount, (TL_stars.StarsTransaction) uItem.object, this.resourcesProvider);
                } else if (obj instanceof TL_stats.BroadcastRevenueTransaction) {
                    ChannelMonetizationLayout.showTransactionSheet(getContext(), this.currentAccount, (TL_stats.BroadcastRevenueTransaction) uItem.object, ChannelTransactionsView.this.dialogId, this.resourcesProvider);
                }
            }

            public void checkMore() {
                if (!this.listView.canScrollVertically(1) || isLoadingVisible()) {
                    this.loadMore.run();
                }
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
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.listView.adapter.update(false);
            }
        }

        public class PageAdapter extends ViewPagerFixed.Adapter {
            private final int classGuid;
            private final Context context;
            private final int currentAccount;
            private final long dialogId;
            private final ArrayList items = new ArrayList();
            private final Theme.ResourcesProvider resourcesProvider;

            public PageAdapter(Context context, int i, long j, int i2, Theme.ResourcesProvider resourcesProvider) {
                this.context = context;
                this.currentAccount = i;
                this.classGuid = i2;
                this.resourcesProvider = resourcesProvider;
                this.dialogId = j;
                fill();
            }

            public void lambda$createView$0(int i) {
                ChannelTransactionsView.this.loadTransactions(i);
            }

            @Override
            public void bindView(View view, int i, int i2) {
            }

            @Override
            public View createView(final int i) {
                return new Page(this.context, this.dialogId, i, this.currentAccount, this.classGuid, new Runnable() {
                    @Override
                    public final void run() {
                        ChannelMonetizationLayout.ChannelTransactionsView.PageAdapter.this.lambda$createView$0(i);
                    }
                }, this.resourcesProvider);
            }

            public void fill() {
                this.items.clear();
                if (!ChannelTransactionsView.this.tonTransactions.isEmpty()) {
                    this.items.add(UItem.asSpace(1));
                }
                if (ChannelTransactionsView.this.starsTransactions.isEmpty()) {
                    return;
                }
                this.items.add(UItem.asSpace(0));
            }

            @Override
            public int getItemCount() {
                return this.items.size();
            }

            @Override
            public String getItemTitle(int i) {
                int i2;
                int itemViewType = getItemViewType(i);
                if (itemViewType == 0) {
                    i2 = R.string.MonetizationTransactionsStars;
                } else {
                    if (itemViewType != 1) {
                        return "";
                    }
                    i2 = R.string.MonetizationTransactionsTON;
                }
                return LocaleController.getString(i2);
            }

            @Override
            public int getItemViewType(int i) {
                if (i < 0 || i >= this.items.size()) {
                    return 1;
                }
                return ((UItem) this.items.get(i)).intValue;
            }
        }

        public ChannelTransactionsView(Context context, int i, long j, int i2, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.tonTransactions = new ArrayList();
            this.starsTransactions = new ArrayList();
            this.starsLastOffset = "";
            this.loadingTransactions = new boolean[]{false, false};
            this.currentAccount = i;
            this.dialogId = j;
            this.updateParentList = runnable;
            setOrientation(1);
            ViewPagerFixed viewPagerFixed = new ViewPagerFixed(context);
            this.viewPager = viewPagerFixed;
            PageAdapter pageAdapter = new PageAdapter(context, i, j, i2, resourcesProvider);
            this.adapter = pageAdapter;
            viewPagerFixed.setAdapter(pageAdapter);
            ViewPagerFixed.TabsView createTabsView = viewPagerFixed.createTabsView(true, 3);
            this.tabsView = createTabsView;
            View view = new View(context);
            view.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
            addView(createTabsView, LayoutHelper.createLinear(-1, 48));
            addView(view, LayoutHelper.createLinear(-1.0f, 1.0f / AndroidUtilities.density));
            addView(viewPagerFixed, LayoutHelper.createLinear(-1, -1));
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
            loadTransactions(1);
            loadTransactions(0);
        }

        public void lambda$loadTransactions$0(TLObject tLObject, int i, TLRPC.TL_error tL_error, boolean z, boolean z2) {
            Runnable runnable;
            if (tLObject instanceof TL_stats.TL_broadcastRevenueTransactions) {
                TL_stats.TL_broadcastRevenueTransactions tL_broadcastRevenueTransactions = (TL_stats.TL_broadcastRevenueTransactions) tLObject;
                this.tonTransactionsTotalCount = tL_broadcastRevenueTransactions.count;
                this.tonTransactions.addAll(tL_broadcastRevenueTransactions.transactions);
                updateLists(true, true);
                this.loadingTransactions[i] = false;
            } else if (tL_error != null) {
                BulletinFactory.showError(tL_error);
            }
            if (hasTransactions() != z && (runnable = this.updateParentList) != null) {
                runnable.run();
            }
            if (hasTransactions(i) != z2) {
                updateTabs();
            }
        }

        public void lambda$loadTransactions$1(final int i, final boolean z, final boolean z2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.ChannelTransactionsView.this.lambda$loadTransactions$0(tLObject, i, tL_error, z, z2);
                }
            });
        }

        public void lambda$loadTransactions$2(TLObject tLObject, int i, TLRPC.TL_error tL_error, boolean z, boolean z2) {
            Runnable runnable;
            if (tLObject instanceof TL_stars.TL_payments_starsStatus) {
                TL_stars.TL_payments_starsStatus tL_payments_starsStatus = (TL_stars.TL_payments_starsStatus) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_starsStatus.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(tL_payments_starsStatus.chats, false);
                this.starsTransactions.addAll(tL_payments_starsStatus.history);
                this.starsLastOffset = tL_payments_starsStatus.next_offset;
                updateLists(true, true);
                this.loadingTransactions[i] = false;
            } else if (tL_error != null) {
                BulletinFactory.showError(tL_error);
            }
            if (hasTransactions() != z && (runnable = this.updateParentList) != null) {
                runnable.run();
            }
            if (hasTransactions(i) != z2) {
                updateTabs();
            }
        }

        public void lambda$loadTransactions$3(final int i, final boolean z, final boolean z2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.ChannelTransactionsView.this.lambda$loadTransactions$2(tLObject, i, tL_error, z, z2);
                }
            });
        }

        public void loadTransactions(final int i) {
            RequestDelegate requestDelegate;
            ConnectionsManager connectionsManager;
            TL_stars.TL_payments_getStarsTransactions tL_payments_getStarsTransactions;
            if (this.loadingTransactions[i]) {
                return;
            }
            final boolean hasTransactions = hasTransactions();
            final boolean hasTransactions2 = hasTransactions(i);
            if (i == 1) {
                int size = this.tonTransactions.size();
                int i2 = this.tonTransactionsTotalCount;
                if ((size >= i2 && i2 != 0) || !ChannelMonetizationLayout.this.tonRevenueAvailable) {
                    return;
                }
                this.loadingTransactions[i] = true;
                TL_stats.TL_getBroadcastRevenueTransactions tL_getBroadcastRevenueTransactions = new TL_stats.TL_getBroadcastRevenueTransactions();
                tL_getBroadcastRevenueTransactions.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                tL_getBroadcastRevenueTransactions.offset = this.tonTransactions.size();
                tL_getBroadcastRevenueTransactions.limit = this.tonTransactions.isEmpty() ? 5 : 20;
                ConnectionsManager connectionsManager2 = ConnectionsManager.getInstance(this.currentAccount);
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChannelMonetizationLayout.ChannelTransactionsView.this.lambda$loadTransactions$1(i, hasTransactions, hasTransactions2, tLObject, tL_error);
                    }
                };
                tL_payments_getStarsTransactions = tL_getBroadcastRevenueTransactions;
                connectionsManager = connectionsManager2;
            } else {
                if (i != 0 || this.starsLastOffset == null || !ChannelMonetizationLayout.this.starsRevenueAvailable) {
                    return;
                }
                this.loadingTransactions[i] = true;
                TL_stars.TL_payments_getStarsTransactions tL_payments_getStarsTransactions2 = new TL_stars.TL_payments_getStarsTransactions();
                tL_payments_getStarsTransactions2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                tL_payments_getStarsTransactions2.offset = this.starsLastOffset;
                ConnectionsManager connectionsManager3 = ConnectionsManager.getInstance(this.currentAccount);
                requestDelegate = new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChannelMonetizationLayout.ChannelTransactionsView.this.lambda$loadTransactions$3(i, hasTransactions, hasTransactions2, tLObject, tL_error);
                    }
                };
                tL_payments_getStarsTransactions = tL_payments_getStarsTransactions2;
                connectionsManager = connectionsManager3;
            }
            connectionsManager.sendRequest(tL_payments_getStarsTransactions, requestDelegate);
        }

        private void updateLists(boolean z, boolean z2) {
            for (int i = 0; i < this.viewPager.getViewPages().length; i++) {
                View view = this.viewPager.getViewPages()[i];
                if (view instanceof Page) {
                    Page page = (Page) view;
                    page.listView.adapter.update(z);
                    if (z2) {
                        page.checkMore();
                    }
                }
            }
        }

        private void updateTabs() {
            this.adapter.fill();
            this.viewPager.fillTabs(false);
            this.viewPager.updateCurrent();
        }

        public RecyclerListView getCurrentListView() {
            View currentView = this.viewPager.getCurrentView();
            if (currentView instanceof Page) {
                return ((Page) currentView).listView;
            }
            return null;
        }

        public boolean hasTransactions() {
            return (this.tonTransactions.isEmpty() && this.starsTransactions.isEmpty()) ? false : true;
        }

        public boolean hasTransactions(int i) {
            ArrayList arrayList;
            if (i == 1) {
                arrayList = this.tonTransactions;
            } else {
                if (i != 0) {
                    return false;
                }
                arrayList = this.starsTransactions;
            }
            return !arrayList.isEmpty();
        }

        public void reloadTransactions() {
            boolean hasTransactions = hasTransactions();
            for (int i = 0; i < 2; i++) {
                if (this.loadingTransactions[i]) {
                    return;
                }
                if (i == 1) {
                    this.tonTransactions.clear();
                    this.tonTransactionsTotalCount = 3;
                } else {
                    this.starsTransactions.clear();
                    this.starsLastOffset = "";
                }
                this.loadingTransactions[i] = false;
                loadTransactions(i);
            }
            if (hasTransactions() == hasTransactions || this.updateParentList == null) {
                return;
            }
            updateTabs();
            this.updateParentList.run();
        }
    }

    public static class FeatureCell extends FrameLayout {
        public FeatureCell(Context context, int i, CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, resourcesProvider), PorterDuff.Mode.SRC_IN));
            imageView.setImageResource(i);
            addView(imageView, LayoutHelper.createFrame(24, 24.0f, 51, 0.0f, 5.0f, 18.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 42.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView.setText(charSequence);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 55, 0, 0, 0, 2));
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
            textView2.setText(charSequence2);
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 55, 0, 0, 0, 0));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), AndroidUtilities.dp(325.0f)), View.MeasureSpec.getMode(i)), i2);
        }
    }

    public static class ProceedOverview {
        public long amount;
        public long amount2;
        public boolean contains1 = true;
        public boolean contains2;
        public long crypto_amount;
        public long crypto_amount2;
        public String crypto_currency;
        public String crypto_currency2;
        public String currency;
        public CharSequence text;

        public static ProceedOverview as(String str, CharSequence charSequence) {
            ProceedOverview proceedOverview = new ProceedOverview();
            proceedOverview.crypto_currency = str;
            proceedOverview.text = charSequence;
            return proceedOverview;
        }

        public static ProceedOverview as(String str, String str2, CharSequence charSequence) {
            ProceedOverview proceedOverview = new ProceedOverview();
            proceedOverview.contains1 = false;
            proceedOverview.crypto_currency = str;
            proceedOverview.crypto_currency2 = str2;
            proceedOverview.text = charSequence;
            return proceedOverview;
        }
    }

    public static class ProceedOverviewCell extends LinearLayout {
        private final LinearLayout[] amountContainer;
        private final TextView[] amountView;
        private final AnimatedEmojiSpan.TextViewEmojis[] cryptoAmountView;
        private final DecimalFormat formatter;
        private final LinearLayout layout;
        private final Theme.ResourcesProvider resourcesProvider;
        private final TextView titleView;

        public ProceedOverviewCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.amountContainer = new LinearLayout[2];
            this.cryptoAmountView = new AnimatedEmojiSpan.TextViewEmojis[2];
            this.amountView = new TextView[2];
            this.resourcesProvider = resourcesProvider;
            setOrientation(1);
            LinearLayout linearLayout = new LinearLayout(context);
            this.layout = linearLayout;
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 22.0f, 9.0f, 22.0f, 0.0f));
            for (int i = 0; i < 2; i++) {
                this.amountContainer[i] = new LinearLayout(context);
                this.amountContainer[i].setOrientation(0);
                this.layout.addView(this.amountContainer[i], LayoutHelper.createLinear(-1, -2, 1.0f, 119));
                this.cryptoAmountView[i] = new AnimatedEmojiSpan.TextViewEmojis(context);
                this.cryptoAmountView[i].setTypeface(AndroidUtilities.bold());
                this.cryptoAmountView[i].setTextSize(1, 16.0f);
                this.cryptoAmountView[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
                this.amountContainer[i].addView(this.cryptoAmountView[i], LayoutHelper.createLinear(-2, -2, 80, 0, 0, 5, 0));
                this.amountView[i] = new AnimatedEmojiSpan.TextViewEmojis(context);
                this.amountView[i].setTextSize(1, 13.0f);
                this.amountView[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
                this.amountContainer[i].addView(this.amountView[i], LayoutHelper.createLinear(-2, -2, 80));
            }
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 13.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
            addView(textView, LayoutHelper.createLinear(-1, -2, 55, 22, 5, 22, 9));
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
            decimalFormatSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
            this.formatter = decimalFormat;
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(12);
            decimalFormat.setGroupingUsed(false);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }

        public void set(ProceedOverview proceedOverview) {
            CharSequence charSequence;
            int indexOf;
            LinearLayout linearLayout;
            this.titleView.setText(proceedOverview.text);
            int i = 0;
            while (i < 2) {
                String str = i == 0 ? proceedOverview.crypto_currency : proceedOverview.crypto_currency2;
                long j = i == 0 ? proceedOverview.crypto_amount : proceedOverview.crypto_amount2;
                long j2 = i == 0 ? proceedOverview.amount : proceedOverview.amount2;
                if (i == 0 && !proceedOverview.contains1) {
                    linearLayout = this.amountContainer[i];
                } else if (i != 1 || proceedOverview.contains2) {
                    String str2 = str + " ";
                    if ("TON".equalsIgnoreCase(str)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append((Object) str2);
                        DecimalFormat decimalFormat = this.formatter;
                        double d = j;
                        Double.isNaN(d);
                        sb.append(decimalFormat.format(d / 1.0E9d));
                        charSequence = ChannelMonetizationLayout.replaceTON(sb.toString(), this.cryptoAmountView[i].getPaint(), 0.87f, true);
                    } else if ("XTR".equalsIgnoreCase(str)) {
                        charSequence = StarsIntroActivity.replaceStarsWithPlain(((Object) str2) + LocaleController.formatNumber(j, ' '), 0.8f);
                    } else {
                        charSequence = ((Object) str2) + Long.toString(j);
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                    if ("TON".equalsIgnoreCase(str) && (indexOf = TextUtils.indexOf(spannableStringBuilder, ".")) >= 0) {
                        spannableStringBuilder.setSpan(new RelativeSizeSpan(0.8125f), indexOf, spannableStringBuilder.length(), 33);
                    }
                    this.amountContainer[i].setVisibility(0);
                    this.cryptoAmountView[i].setText(spannableStringBuilder);
                    this.amountView[i].setText("≈" + BillingController.getInstance().formatCurrency(j2, proceedOverview.currency));
                    i++;
                } else {
                    linearLayout = this.amountContainer[i];
                }
                linearLayout.setVisibility(8);
                i++;
            }
        }
    }

    public static class TransactionCell extends FrameLayout {
        private final TextView dateView;
        private final DecimalFormat formatter;
        private final LinearLayout layout;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private final TextView titleView;
        private final AnimatedEmojiSpan.TextViewEmojis valueText;

        public TransactionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            LinearLayout linearLayout = new LinearLayout(context);
            this.layout = linearLayout;
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 119, 17.0f, 9.0f, 130.0f, 9.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
            TextView textView2 = new TextView(context);
            this.dateView = textView2;
            textView2.setTextSize(1, 13.0f);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 4.0f, 0.0f, 0.0f));
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(context);
            this.valueText = textViewEmojis;
            textViewEmojis.setTypeface(AndroidUtilities.bold());
            textViewEmojis.setTextSize(1, 13.0f);
            addView(textViewEmojis, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 18.0f, 0.0f));
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
            decimalFormatSymbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
            this.formatter = decimalFormat;
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(12);
            decimalFormat.setGroupingUsed(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
                Paint paint = resourcesProvider != null ? resourcesProvider.getPaint("paintDivider") : Theme.dividerPaint;
                if (paint != null) {
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(17.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(17.0f) : 0), getMeasuredHeight() - 1, paint);
                }
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }

        public void set(TL_stats.BroadcastRevenueTransaction broadcastRevenueTransaction, boolean z) {
            long j;
            char c;
            boolean z2;
            String str;
            if (broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionWithdrawal) {
                TL_stats.TL_broadcastRevenueTransactionWithdrawal tL_broadcastRevenueTransactionWithdrawal = (TL_stats.TL_broadcastRevenueTransactionWithdrawal) broadcastRevenueTransaction;
                this.titleView.setText(LocaleController.getString(R.string.MonetizationTransactionWithdraw));
                if (tL_broadcastRevenueTransactionWithdrawal.pending) {
                    this.dateView.setText(LocaleController.getString(R.string.MonetizationTransactionPending));
                    z2 = false;
                } else {
                    z2 = tL_broadcastRevenueTransactionWithdrawal.failed;
                    TextView textView = this.dateView;
                    StringBuilder sb = new StringBuilder();
                    sb.append(LocaleController.formatShortDateTime(tL_broadcastRevenueTransactionWithdrawal.date));
                    if (z2) {
                        str = " — " + LocaleController.getString(R.string.MonetizationTransactionNotCompleted);
                    } else {
                        str = "";
                    }
                    sb.append(str);
                    textView.setText(sb.toString());
                }
                j = tL_broadcastRevenueTransactionWithdrawal.amount;
                c = 65535;
            } else {
                if (broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionProceeds) {
                    this.titleView.setText(LocaleController.getString(R.string.MonetizationTransactionProceed));
                    this.dateView.setText(LocaleController.formatShortDateTime(r9.from_date) + " - " + LocaleController.formatShortDateTime(r9.to_date));
                    j = ((TL_stats.TL_broadcastRevenueTransactionProceeds) broadcastRevenueTransaction).amount;
                } else {
                    if (!(broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionRefund)) {
                        return;
                    }
                    this.titleView.setText(LocaleController.getString(R.string.MonetizationTransactionRefund));
                    this.dateView.setText(LocaleController.formatShortDateTime(r9.from_date));
                    j = ((TL_stats.TL_broadcastRevenueTransactionRefund) broadcastRevenueTransaction).amount;
                }
                c = 1;
                z2 = false;
            }
            this.dateView.setTextColor(Theme.getColor(z2 ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteGrayText, this.resourcesProvider));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) (c < 0 ? "-" : "+"));
            spannableStringBuilder.append((CharSequence) "TON ");
            DecimalFormat decimalFormat = this.formatter;
            double abs = Math.abs(j);
            Double.isNaN(abs);
            spannableStringBuilder.append((CharSequence) decimalFormat.format(abs / 1.0E9d));
            int indexOf = TextUtils.indexOf(spannableStringBuilder, ".");
            if (indexOf >= 0) {
                spannableStringBuilder.setSpan(new RelativeSizeSpan(1.15f), 0, indexOf + 1, 33);
            }
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis = this.valueText;
            textViewEmojis.setText(ChannelMonetizationLayout.replaceTON(spannableStringBuilder, textViewEmojis.getPaint(), 1.1f, AndroidUtilities.dp(0.33f), false));
            this.valueText.setTextColor(Theme.getColor(c < 0 ? Theme.key_text_RedBold : Theme.key_avatar_nameInMessageGreen, this.resourcesProvider));
            this.needDivider = z;
            setWillNotDraw(!z);
        }
    }

    public ChannelMonetizationLayout(final Context context, final BaseFragment baseFragment, final int i, final long j, final Theme.ResourcesProvider resourcesProvider, boolean z, boolean z2) {
        super(context);
        this.shakeDp = 4;
        this.starRef = new ColoredImageSpan[1];
        this.starsBalanceEditTextIgnore = false;
        this.starsBalanceEditTextAll = true;
        this.switchOffValue = false;
        this.initialSwitchOffValue = false;
        this.proceedsAvailable = false;
        this.availableValue = ProceedOverview.as("TON", "XTR", LocaleController.getString(R.string.MonetizationOverviewAvailable));
        this.lastWithdrawalValue = ProceedOverview.as("TON", "XTR", LocaleController.getString(R.string.MonetizationOverviewLastWithdrawal));
        this.lifetimeValue = ProceedOverview.as("TON", "XTR", LocaleController.getString(R.string.MonetizationOverviewTotal));
        this.sendCpmUpdateRunnable = new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.sendCpmUpdate();
            }
        };
        this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.tonRevenueAvailable = z;
        this.starsRevenueAvailable = z2;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
        this.formatter = decimalFormat;
        decimalFormat.setMinimumFractionDigits(2);
        this.formatter.setMaximumFractionDigits(12);
        this.formatter.setGroupingUsed(false);
        this.fragment = baseFragment;
        this.resourcesProvider = resourcesProvider;
        this.currentAccount = i;
        this.dialogId = j;
        initLevel();
        this.titleInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.MonetizationInfo, 50), -1, 3, new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.lambda$new$0(BaseFragment.this, context, resourcesProvider);
            }
        }, resourcesProvider), true);
        this.balanceInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(MessagesController.getInstance(i).channelRevenueWithdrawalEnabled ? R.string.MonetizationBalanceInfo : R.string.MonetizationBalanceInfoNotAvailable), -1, 3, new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$1();
            }
        }), true);
        int i2 = (z2 && z) ? R.string.MonetizationProceedsStarsTONInfo : z2 ? R.string.MonetizationProceedsStarsInfo : R.string.MonetizationProceedsTONInfo;
        final int i3 = (z2 && z) ? R.string.MonetizationProceedsStarsTONInfoLink : z2 ? R.string.MonetizationProceedsStarsInfoLink : R.string.MonetizationProceedsTONInfoLink;
        this.proceedsInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(i2), -1, 3, new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$2(i3);
            }
        }, resourcesProvider), true);
        this.starsBalanceInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.MonetizationStarsInfo), new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$3();
            }
        }), true);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray, resourcesProvider));
        this.transactionsLayout = new ChannelTransactionsView(context, i, j, baseFragment.getClassGuid(), new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.updateList();
            }
        }, resourcesProvider);
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            protected void onMeasure(int i4, int i5) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i4), 1073741824), i5);
            }
        };
        this.balanceLayout = linearLayout;
        linearLayout.setOrientation(1);
        int i4 = Theme.key_windowBackgroundWhite;
        linearLayout.setBackgroundColor(Theme.getColor(i4, resourcesProvider));
        linearLayout.setPadding(0, 0, 0, AndroidUtilities.dp(17.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, true);
        this.balanceTitle = animatedTextView;
        animatedTextView.setTypeface(AndroidUtilities.bold());
        int i5 = Theme.key_windowBackgroundWhiteBlackText;
        animatedTextView.setTextColor(Theme.getColor(i5, resourcesProvider));
        animatedTextView.setTextSize(AndroidUtilities.dp(32.0f));
        animatedTextView.setGravity(17);
        this.balanceTitleSizeSpan = new RelativeSizeSpan(0.6770833f);
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-1, 38, 49, 22, 15, 22, 0));
        AnimatedTextView animatedTextView2 = new AnimatedTextView(context, true, true, true);
        this.balanceSubtitle = animatedTextView2;
        animatedTextView2.setGravity(17);
        int i6 = Theme.key_windowBackgroundWhiteGrayText;
        animatedTextView2.setTextColor(Theme.getColor(i6, resourcesProvider));
        animatedTextView2.setTextSize(AndroidUtilities.dp(14.0f));
        linearLayout.addView(animatedTextView2, LayoutHelper.createFrame(-1, 17.0f, 49, 22.0f, 4.0f, 22.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.balanceButton = buttonWithCounterView;
        buttonWithCounterView.setEnabled(MessagesController.getInstance(i).channelRevenueWithdrawalEnabled);
        buttonWithCounterView.setText(LocaleController.getString(R.string.MonetizationWithdraw), false);
        buttonWithCounterView.setVisibility(8);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChannelMonetizationLayout.this.lambda$new$6(baseFragment, view);
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 55, 18.0f, 13.0f, 18.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context) {
            @Override
            protected void onMeasure(int i7, int i8) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i7), 1073741824), i8);
            }
        };
        this.starsBalanceLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundColor(Theme.getColor(i4, resourcesProvider));
        linearLayout2.setPadding(0, 0, 0, AndroidUtilities.dp(17.0f));
        AnimatedTextView animatedTextView3 = new AnimatedTextView(context, false, true, true);
        this.starsBalanceTitle = animatedTextView3;
        animatedTextView3.setTypeface(AndroidUtilities.bold());
        animatedTextView3.setTextColor(Theme.getColor(i5, resourcesProvider));
        animatedTextView3.setTextSize(AndroidUtilities.dp(32.0f));
        animatedTextView3.setGravity(17);
        this.starsBalanceTitleSizeSpan = new RelativeSizeSpan(0.6770833f);
        linearLayout2.addView(animatedTextView3, LayoutHelper.createLinear(-1, 38, 49, 22, 15, 22, 0));
        AnimatedTextView animatedTextView4 = new AnimatedTextView(context, true, true, true);
        this.starsBalanceSubtitle = animatedTextView4;
        animatedTextView4.setGravity(17);
        animatedTextView4.setTextColor(Theme.getColor(i6, resourcesProvider));
        animatedTextView4.setTextSize(AndroidUtilities.dp(14.0f));
        linearLayout2.addView(animatedTextView4, LayoutHelper.createFrame(-1, 17.0f, 49, 22.0f, 4.0f, 22.0f, 0.0f));
        OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (ChannelMonetizationLayout.this.starsBalanceEditText != null && !ChannelMonetizationLayout.this.starsBalanceEditText.isFocusable()) {
                    ChannelMonetizationLayout.this.starsBalanceEditText.setFocusable(true);
                    ChannelMonetizationLayout.this.starsBalanceEditText.setFocusableInTouchMode(true);
                    int findPositionByItemId = ChannelMonetizationLayout.this.listView.findPositionByItemId(3);
                    if (findPositionByItemId >= 0 && findPositionByItemId < ChannelMonetizationLayout.this.listView.adapter.getItemCount()) {
                        ChannelMonetizationLayout.this.listView.stopScroll();
                        ChannelMonetizationLayout.this.listView.smoothScrollToPosition(findPositionByItemId);
                    }
                    ChannelMonetizationLayout.this.starsBalanceEditText.requestFocus();
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.starsBalanceEditTextContainer = outlineTextContainerView;
        outlineTextContainerView.setVisibility(8);
        this.starsBalanceEditTextContainer.setText(LocaleController.getString(R.string.BotStarsWithdrawPlaceholder));
        this.starsBalanceEditTextContainer.setLeftPadding(AndroidUtilities.dp(36.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AndroidUtilities.hideKeyboard(this);
            }
        };
        this.starsBalanceEditText = editTextBoldCursor;
        editTextBoldCursor.setFocusable(false);
        this.starsBalanceEditText.setTextColor(Theme.getColor(i5, resourcesProvider));
        this.starsBalanceEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.starsBalanceEditText.setCursorWidth(1.5f);
        this.starsBalanceEditText.setBackground(null);
        this.starsBalanceEditText.setTextSize(1, 18.0f);
        this.starsBalanceEditText.setMaxLines(1);
        int dp = AndroidUtilities.dp(16.0f);
        this.starsBalanceEditText.setPadding(AndroidUtilities.dp(6.0f), dp, dp, dp);
        this.starsBalanceEditText.setInputType(2);
        this.starsBalanceEditText.setTypeface(Typeface.DEFAULT);
        this.starsBalanceEditText.setHighlightColor(Theme.getColor(Theme.key_chat_inTextSelectionHighlight, resourcesProvider));
        this.starsBalanceEditText.setHandlesColor(Theme.getColor(Theme.key_chat_TextSelectionCursor, resourcesProvider));
        this.starsBalanceEditText.setGravity(LocaleController.isRTL ? 5 : 3);
        this.starsBalanceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public final void onFocusChange(View view, boolean z3) {
                ChannelMonetizationLayout.this.lambda$new$7(view, z3);
            }
        });
        this.starsBalanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (ChannelMonetizationLayout.this.starsBalanceEditTextIgnore) {
                    return;
                }
                long j2 = ChannelMonetizationLayout.this.starsBalance;
                ChannelMonetizationLayout.this.starsBalanceEditTextValue = TextUtils.isEmpty(editable) ? 0L : Long.parseLong(editable.toString());
                if (ChannelMonetizationLayout.this.starsBalanceEditTextValue > j2) {
                    ChannelMonetizationLayout.this.starsBalanceEditTextValue = j2;
                    ChannelMonetizationLayout.this.starsBalanceEditTextIgnore = true;
                    ChannelMonetizationLayout.this.starsBalanceEditText.setText(Long.toString(ChannelMonetizationLayout.this.starsBalanceEditTextValue));
                    ChannelMonetizationLayout.this.starsBalanceEditText.setSelection(ChannelMonetizationLayout.this.starsBalanceEditText.getText().length());
                    ChannelMonetizationLayout.this.starsBalanceEditTextIgnore = false;
                }
                ChannelMonetizationLayout channelMonetizationLayout = ChannelMonetizationLayout.this;
                channelMonetizationLayout.starsBalanceEditTextAll = channelMonetizationLayout.starsBalanceEditTextValue == j2;
                AndroidUtilities.cancelRunOnUIThread(ChannelMonetizationLayout.this.setStarsBalanceButtonText);
                ChannelMonetizationLayout.this.setStarsBalanceButtonText.run();
                ChannelMonetizationLayout.this.starsBalanceEditTextAll = false;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
            }
        });
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.star_small_inner);
        linearLayout3.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 19, 14, 0, 0, 0));
        linearLayout3.addView(this.starsBalanceEditText, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
        this.starsBalanceEditTextContainer.attachEditText(this.starsBalanceEditText);
        this.starsBalanceEditTextContainer.addView(linearLayout3, LayoutHelper.createFrame(-1, -2, 48));
        linearLayout2.addView(this.starsBalanceEditTextContainer, LayoutHelper.createLinear(-1, -2, 1, 18, 14, 18, 2));
        LinearLayout linearLayout4 = new LinearLayout(context);
        this.starsBalanceButtonsLayout = linearLayout4;
        linearLayout4.setOrientation(0);
        ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, resourcesProvider) {
            @Override
            protected boolean subTextSplitToWords() {
                return false;
            }
        };
        this.starsBalanceButton = buttonWithCounterView2;
        buttonWithCounterView2.setEnabled(false);
        buttonWithCounterView2.setText(LocaleController.formatPluralString("MonetizationStarsWithdraw", 0, new Object[0]), false);
        buttonWithCounterView2.setVisibility(0);
        buttonWithCounterView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChannelMonetizationLayout.this.lambda$new$11(i, baseFragment, view);
            }
        });
        ButtonWithCounterView buttonWithCounterView3 = new ButtonWithCounterView(context, resourcesProvider);
        this.starsAdsButton = buttonWithCounterView3;
        buttonWithCounterView3.setEnabled(false);
        buttonWithCounterView3.setText(LocaleController.getString(R.string.MonetizationStarsAds), false);
        buttonWithCounterView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChannelMonetizationLayout.this.lambda$new$15(i, j, context, view);
            }
        });
        linearLayout4.addView(buttonWithCounterView2, LayoutHelper.createLinear(-1, 48, 1.0f, 119));
        linearLayout4.addView(new Space(context), LayoutHelper.createLinear(8, 48, 0.0f, 119));
        linearLayout4.addView(buttonWithCounterView3, LayoutHelper.createLinear(-1, 48, 1.0f, 119));
        linearLayout2.addView(linearLayout4, LayoutHelper.createFrame(-1, 48.0f, 55, 18.0f, 13.0f, 18.0f, 0.0f));
        this.starsBalanceEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i7, KeyEvent keyEvent) {
                boolean lambda$new$18;
                lambda$new$18 = ChannelMonetizationLayout.this.lambda$new$18(baseFragment, textView, i7, keyEvent);
                return lambda$new$18;
            }
        });
        this.setStarsBalanceButtonText = new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$19(i);
            }
        };
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(baseFragment, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ChannelMonetizationLayout.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                ChannelMonetizationLayout.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                boolean onLongClick;
                onLongClick = ChannelMonetizationLayout.this.onLongClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                return Boolean.valueOf(onLongClick);
            }
        });
        this.listView = universalRecyclerView;
        addView(universalRecyclerView);
        LinearLayout linearLayout5 = new LinearLayout(context);
        linearLayout5.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        this.progress = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(i4, resourcesProvider));
        frameLayout.addView(linearLayout5, LayoutHelper.createFrame(-2, -2, 17));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAutoRepeat(true);
        rLottieImageView.setAnimation(R.raw.statistic_preload, 120, 120);
        rLottieImageView.playAnimation();
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i7 = Theme.key_player_actionBarTitle;
        textView.setTextColor(Theme.getColor(i7));
        textView.setTag(Integer.valueOf(i7));
        textView.setText(LocaleController.getString("LoadingStats", R.string.LoadingStats));
        textView.setGravity(1);
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 15.0f);
        int i8 = Theme.key_player_actionBarSubtitle;
        textView2.setTextColor(Theme.getColor(i8));
        textView2.setTag(Integer.valueOf(i8));
        textView2.setText(LocaleController.getString("LoadingStatsDescription", R.string.LoadingStatsDescription));
        textView2.setGravity(1);
        linearLayout5.addView(rLottieImageView, LayoutHelper.createLinear(120, 120, 1, 0, 0, 0, 20));
        linearLayout5.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 10));
        linearLayout5.addView(textView2, LayoutHelper.createLinear(-2, -2, 1));
        addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
    }

    public void lambda$loadStarsStats$25(TLRPC.TL_payments_starsRevenueStats tL_payments_starsRevenueStats) {
        FrameLayout frameLayout;
        ChartData chartData;
        ArrayList arrayList;
        this.stars_rate = tL_payments_starsRevenueStats.usd_rate;
        StatisticActivity.ChartViewData createViewData = StatisticActivity.createViewData(tL_payments_starsRevenueStats.revenue_graph, LocaleController.getString(R.string.MonetizationGraphStarsRevenue), 2);
        this.starsRevenueChart = createViewData;
        if (createViewData != null && (chartData = createViewData.chartData) != null && (arrayList = chartData.lines) != null && !arrayList.isEmpty() && this.starsRevenueChart.chartData.lines.get(0) != null) {
            ((ChartData.Line) this.starsRevenueChart.chartData.lines.get(0)).colorKey = Theme.key_statisticChartLine_golden;
            this.starsRevenueChart.chartData.yRate = (float) ((1.0d / this.stars_rate) / 100.0d);
        }
        setupBalances(tL_payments_starsRevenueStats.status);
        if (!this.tonRevenueAvailable && (frameLayout = this.progress) != null) {
            frameLayout.animate().alpha(0.0f).setDuration(380L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).withEndAction(new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.this.lambda$applyStarsStats$28();
                }
            }).start();
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }

    private void checkLearnSheet() {
        if (isAttachedToWindow() && this.tonRevenueAvailable && this.proceedsAvailable && MessagesController.getGlobalMainSettings().getBoolean("monetizationadshint", true)) {
            this.fragment.showDialog(makeLearnSheet(getContext(), false, this.resourcesProvider));
            MessagesController.getGlobalMainSettings().edit().putBoolean("monetizationadshint", false).apply();
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        StatisticActivity.ChartViewData chartViewData;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId));
        TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-this.dialogId);
        int i = chatFull != null ? chatFull.stats_dc : -1;
        if (this.tonRevenueAvailable) {
            arrayList.add(UItem.asCenterShadow(this.titleInfo));
            StatisticActivity.ChartViewData chartViewData2 = this.impressionsChart;
            if (chartViewData2 != null && !chartViewData2.isEmpty) {
                arrayList.add(UItem.asChart(5, i, chartViewData2));
                arrayList.add(UItem.asShadow(-1, null));
            }
            StatisticActivity.ChartViewData chartViewData3 = this.revenueChart;
            if (chartViewData3 != null && !chartViewData3.isEmpty) {
                arrayList.add(UItem.asChart(2, i, chartViewData3));
                arrayList.add(UItem.asShadow(-2, null));
            }
        }
        if (this.starsRevenueAvailable && (chartViewData = this.starsRevenueChart) != null && !chartViewData.isEmpty) {
            arrayList.add(UItem.asChart(2, i, chartViewData));
            arrayList.add(UItem.asShadow(-3, null));
        }
        if (this.proceedsAvailable) {
            arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.MonetizationOverview)));
            arrayList.add(UItem.asProceedOverview(this.availableValue));
            arrayList.add(UItem.asProceedOverview(this.lastWithdrawalValue));
            arrayList.add(UItem.asProceedOverview(this.lifetimeValue));
            arrayList.add(UItem.asShadow(-4, this.proceedsInfo));
        }
        if (chat != null && chat.creator) {
            if (this.tonRevenueAvailable) {
                arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.MonetizationBalance)));
                arrayList.add(UItem.asCustom(this.balanceLayout));
                arrayList.add(UItem.asShadow(-5, this.balanceInfo));
                int i2 = MessagesController.getInstance(this.currentAccount).channelRestrictSponsoredLevelMin;
                arrayList.add(UItem.asCheck(1, PeerColorActivity.withLevelLock(LocaleController.getString(R.string.MonetizationSwitchOff), this.currentBoostLevel < i2 ? i2 : 0)).setChecked(this.currentBoostLevel >= i2 && this.switchOffValue));
                arrayList.add(UItem.asShadow(-8, LocaleController.getString(R.string.MonetizationSwitchOffInfo)));
            }
            if (this.starsRevenueAvailable) {
                arrayList.add(UItem.asBlackHeader(LocaleController.getString(R.string.MonetizationStarsBalance)));
                arrayList.add(UItem.asCustom(3, this.starsBalanceLayout));
                arrayList.add(UItem.asShadow(-6, this.starsBalanceInfo));
            }
        }
        arrayList.add(this.transactionsLayout.hasTransactions() ? UItem.asFullscreenCustom(this.transactionsLayout, 0) : UItem.asShadow(-10, null));
    }

    private void initLevel() {
        int i;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId));
        if (chat != null) {
            this.currentBoostLevel = chat.level;
        }
        MessagesController.getInstance(this.currentAccount).getBoostsController().getBoostsStats(this.dialogId, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChannelMonetizationLayout.this.lambda$initLevel$30((TL_stories.TL_premium_boostsStatus) obj);
            }
        });
        loadStarsStats();
        if (!this.tonRevenueAvailable || ChatObject.isMegagroup(chat)) {
            return;
        }
        TL_stats.TL_getBroadcastRevenueStats tL_getBroadcastRevenueStats = new TL_stats.TL_getBroadcastRevenueStats();
        tL_getBroadcastRevenueStats.dark = Theme.isCurrentThemeDark();
        tL_getBroadcastRevenueStats.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-this.dialogId);
        if (chatFull != null) {
            int i2 = chatFull.stats_dc;
            boolean z = chatFull.restricted_sponsored;
            this.switchOffValue = z;
            this.initialSwitchOffValue = z;
            i = i2;
        } else {
            i = -1;
        }
        if (i == -1) {
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getBroadcastRevenueStats, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelMonetizationLayout.this.lambda$initLevel$33(tLObject, tL_error);
            }
        }, null, null, 0, i, 1, true);
    }

    private void initWithdraw(final boolean z, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, final TwoStepVerificationActivity twoStepVerificationActivity) {
        TL_stats.TL_getBroadcastRevenueWithdrawalUrl tL_getBroadcastRevenueWithdrawalUrl;
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null) {
            return;
        }
        final Activity parentActivity = baseFragment.getParentActivity();
        TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (parentActivity == null || currentUser == null) {
            return;
        }
        if (z) {
            TLRPC.TL_payments_getStarsRevenueWithdrawalUrl tL_payments_getStarsRevenueWithdrawalUrl = new TLRPC.TL_payments_getStarsRevenueWithdrawalUrl();
            tL_payments_getStarsRevenueWithdrawalUrl.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            if (inputCheckPasswordSRP == null) {
                inputCheckPasswordSRP = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            tL_payments_getStarsRevenueWithdrawalUrl.password = inputCheckPasswordSRP;
            tL_payments_getStarsRevenueWithdrawalUrl.stars = this.starsBalanceEditTextValue;
            tL_getBroadcastRevenueWithdrawalUrl = tL_payments_getStarsRevenueWithdrawalUrl;
        } else {
            TL_stats.TL_getBroadcastRevenueWithdrawalUrl tL_getBroadcastRevenueWithdrawalUrl2 = new TL_stats.TL_getBroadcastRevenueWithdrawalUrl();
            tL_getBroadcastRevenueWithdrawalUrl2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            if (inputCheckPasswordSRP == null) {
                inputCheckPasswordSRP = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            tL_getBroadcastRevenueWithdrawalUrl2.password = inputCheckPasswordSRP;
            tL_getBroadcastRevenueWithdrawalUrl = tL_getBroadcastRevenueWithdrawalUrl2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getBroadcastRevenueWithdrawalUrl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelMonetizationLayout.this.lambda$initWithdraw$24(twoStepVerificationActivity, parentActivity, z, tLObject, tL_error);
            }
        });
    }

    public void lambda$applyStarsStats$28() {
        this.progress.setVisibility(8);
    }

    public void lambda$initLevel$29(TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        UniversalAdapter universalAdapter;
        this.boostsStatus = tL_premium_boostsStatus;
        if (tL_premium_boostsStatus != null) {
            this.currentBoostLevel = tL_premium_boostsStatus.level;
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    public void lambda$initLevel$30(final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$initLevel$29(tL_premium_boostsStatus);
            }
        });
    }

    public void lambda$initLevel$31() {
        this.progress.setVisibility(8);
    }

    public void lambda$initLevel$32(TLObject tLObject) {
        if (tLObject instanceof TL_stats.TL_broadcastRevenueStats) {
            TL_stats.TL_broadcastRevenueStats tL_broadcastRevenueStats = (TL_stats.TL_broadcastRevenueStats) tLObject;
            this.impressionsChart = StatisticActivity.createViewData(tL_broadcastRevenueStats.top_hours_graph, LocaleController.getString(R.string.MonetizationGraphImpressions), 0);
            TL_stats.StatsGraph statsGraph = tL_broadcastRevenueStats.revenue_graph;
            if (statsGraph != null) {
                statsGraph.rate = (float) (1.0E7d / tL_broadcastRevenueStats.usd_rate);
            }
            this.revenueChart = StatisticActivity.createViewData(statsGraph, LocaleController.getString(R.string.MonetizationGraphRevenue), 2);
            StatisticActivity.ChartViewData chartViewData = this.impressionsChart;
            if (chartViewData != null) {
                chartViewData.useHourFormat = true;
            }
            this.ton_rate = tL_broadcastRevenueStats.usd_rate;
            setupBalances(tL_broadcastRevenueStats.balances);
            this.progress.animate().alpha(0.0f).setDuration(380L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).withEndAction(new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.this.lambda$initLevel$31();
                }
            }).start();
            checkLearnSheet();
        }
    }

    public void lambda$initLevel$33(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$initLevel$32(tLObject);
            }
        });
    }

    public void lambda$initWithdraw$20(DialogInterface dialogInterface, int i) {
        this.fragment.presentFragment(new TwoStepVerificationSetupActivity(6, null));
    }

    public void lambda$initWithdraw$21(TLRPC.TL_error tL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z) {
        if (tL_error == null) {
            TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo(null, account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(account_password);
            initWithdraw(z, twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity);
        }
    }

    public void lambda$initWithdraw$22(final TwoStepVerificationActivity twoStepVerificationActivity, final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$initWithdraw$21(tL_error, tLObject, twoStepVerificationActivity, z);
            }
        });
    }

    public void lambda$initWithdraw$23(TLRPC.TL_error tL_error, final TwoStepVerificationActivity twoStepVerificationActivity, Activity activity, final boolean z, TLObject tLObject) {
        int i;
        int i2;
        if (tL_error == null) {
            twoStepVerificationActivity.needHideProgress();
            twoStepVerificationActivity.lambda$onBackPressed$321();
            if (tLObject instanceof TL_stats.TL_broadcastRevenueWithdrawalUrl) {
                Browser.openUrl(getContext(), ((TL_stats.TL_broadcastRevenueWithdrawalUrl) tLObject).url);
                return;
            } else {
                if (tLObject instanceof TLRPC.TL_payments_starsRevenueWithdrawalUrl) {
                    Browser.openUrl(getContext(), ((TLRPC.TL_payments_starsRevenueWithdrawalUrl) tLObject).url);
                    loadStarsStats();
                    return;
                }
                return;
            }
        }
        if (!"PASSWORD_MISSING".equals(tL_error.text) && !tL_error.text.startsWith("PASSWORD_TOO_FRESH_") && !tL_error.text.startsWith("SESSION_TOO_FRESH_")) {
            if ("SRP_ID_INVALID".equals(tL_error.text)) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                        ChannelMonetizationLayout.this.lambda$initWithdraw$22(twoStepVerificationActivity, z, tLObject2, tL_error2);
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
                    ChannelMonetizationLayout.this.lambda$initWithdraw$20(dialogInterface, i5);
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
        if (twoStepVerificationActivity != null) {
            twoStepVerificationActivity.showDialog(builder.create());
        } else {
            this.fragment.showDialog(builder.create());
        }
    }

    public void lambda$initWithdraw$24(final TwoStepVerificationActivity twoStepVerificationActivity, final Activity activity, final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$initWithdraw$23(tL_error, twoStepVerificationActivity, activity, z, tLObject);
            }
        });
    }

    public void lambda$loadStarsStats$26(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_payments_starsRevenueStats) {
            lambda$loadStarsStats$25((TLRPC.TL_payments_starsRevenueStats) tLObject);
        }
    }

    public void lambda$loadStarsStats$27(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$loadStarsStats$26(tLObject);
            }
        });
    }

    public static void lambda$makeLearnSheet$39(Context context, boolean z) {
        Browser.openUrl(context, LocaleController.getString(z ? R.string.BotMonetizationInfoTONLink : R.string.MonetizationInfoTONLink));
    }

    public static void lambda$new$0(BaseFragment baseFragment, Context context, Theme.ResourcesProvider resourcesProvider) {
        baseFragment.showDialog(makeLearnSheet(context, false, resourcesProvider));
    }

    public void lambda$new$1() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.MonetizationBalanceInfoLink));
    }

    public void lambda$new$10(BaseFragment baseFragment, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.starsBalanceButton.setLoading(false);
        baseFragment.presentFragment(twoStepVerificationActivity);
    }

    public void lambda$new$11(final int i, final BaseFragment baseFragment, View view) {
        if (!view.isEnabled() || this.starsBalanceButton.isLoading() || this.balanceButton.isLoading()) {
            return;
        }
        int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
        if (this.starsBalanceBlockedUntil > currentTime) {
            this.withdrawalBulletin = BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.timer_3, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotStarsWithdrawalToast, BotStarsActivity.untilString(this.starsBalanceBlockedUntil - currentTime)))).show();
            return;
        }
        if (this.starsBalanceEditTextValue < MessagesController.getInstance(i).starsRevenueWithdrawalMin) {
            BulletinFactory.of(baseFragment).createSimpleBulletin(getContext().getResources().getDrawable(R.drawable.star_small_inner).mutate(), AndroidUtilities.replaceSingleTag(LocaleController.formatPluralString("BotStarsWithdrawMinLimit", (int) MessagesController.getInstance(i).starsRevenueWithdrawalMin, new Object[0]), new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.this.lambda$new$8(i);
                }
            })).show();
            return;
        }
        final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(1, new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() {
            @Override
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                ChannelMonetizationLayout.this.lambda$new$9(twoStepVerificationActivity, inputCheckPasswordSRP);
            }
        });
        this.starsBalanceButton.setLoading(true);
        twoStepVerificationActivity.preload(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$10(baseFragment, twoStepVerificationActivity);
            }
        });
    }

    public void lambda$new$12() {
        this.starsAdsButton.setLoading(false);
    }

    public void lambda$new$13(TLObject tLObject, Context context) {
        if (tLObject instanceof TLRPC.TL_payments_starsRevenueAdsAccountUrl) {
            Browser.openUrl(context, ((TLRPC.TL_payments_starsRevenueAdsAccountUrl) tLObject).url);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$12();
            }
        }, 1000L);
    }

    public void lambda$new$14(final Context context, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$13(tLObject, context);
            }
        });
    }

    public void lambda$new$15(int i, long j, final Context context, View view) {
        if (!view.isEnabled() || this.starsAdsButton.isLoading()) {
            return;
        }
        this.starsAdsButton.setLoading(true);
        TLRPC.TL_payments_getStarsRevenueAdsAccountUrl tL_payments_getStarsRevenueAdsAccountUrl = new TLRPC.TL_payments_getStarsRevenueAdsAccountUrl();
        tL_payments_getStarsRevenueAdsAccountUrl.peer = MessagesController.getInstance(i).getInputPeer(j);
        ConnectionsManager.getInstance(i).sendRequest(tL_payments_getStarsRevenueAdsAccountUrl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelMonetizationLayout.this.lambda$new$14(context, tLObject, tL_error);
            }
        });
    }

    public void lambda$new$16(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        initWithdraw(true, inputCheckPasswordSRP, twoStepVerificationActivity);
    }

    public void lambda$new$17(BaseFragment baseFragment, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.starsBalanceButton.setLoading(false);
        baseFragment.presentFragment(twoStepVerificationActivity);
    }

    public boolean lambda$new$18(final BaseFragment baseFragment, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(1, new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() {
            @Override
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                ChannelMonetizationLayout.this.lambda$new$16(twoStepVerificationActivity, inputCheckPasswordSRP);
            }
        });
        this.starsBalanceButton.setLoading(true);
        twoStepVerificationActivity.preload(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$new$17(baseFragment, twoStepVerificationActivity);
            }
        });
        return true;
    }

    public void lambda$new$19(int i) {
        int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
        this.starsBalanceButton.setEnabled(this.starsBalanceEditTextValue > 0 || this.starsBalanceBlockedUntil > currentTime);
        if (currentTime >= this.starsBalanceBlockedUntil) {
            this.starsBalanceButton.setSubText(null, true);
            this.starsBalanceButton.setText(StarsIntroActivity.replaceStars(this.starsBalanceEditTextAll ? LocaleController.getString(R.string.MonetizationStarsWithdrawAll) : LocaleController.formatPluralStringSpaced("MonetizationStarsWithdraw", (int) this.starsBalanceEditTextValue), this.starRef), true);
            return;
        }
        this.starsBalanceButton.setText(LocaleController.getString(R.string.MonetizationStarsWithdrawUntil), true);
        if (this.lock == null) {
            this.lock = new SpannableStringBuilder("l");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_switch_lock);
            coloredImageSpan.setTopOffset(1);
            this.lock.setSpan(coloredImageSpan, 0, 1, 33);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) this.lock).append((CharSequence) BotStarsActivity.untilString(this.starsBalanceBlockedUntil - currentTime));
        this.starsBalanceButton.setSubText(spannableStringBuilder, true);
        Bulletin bulletin = this.withdrawalBulletin;
        if (bulletin != null && (bulletin.getLayout() instanceof Bulletin.LottieLayout) && this.withdrawalBulletin.getLayout().isAttachedToWindow()) {
            ((Bulletin.LottieLayout) this.withdrawalBulletin.getLayout()).textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotStarsWithdrawalToast, BotStarsActivity.untilString(this.starsBalanceBlockedUntil - currentTime))));
        }
        AndroidUtilities.cancelRunOnUIThread(this.setStarsBalanceButtonText);
        AndroidUtilities.runOnUIThread(this.setStarsBalanceButtonText, 1000L);
    }

    public void lambda$new$2(int i) {
        Browser.openUrl(getContext(), LocaleController.getString(i));
    }

    public void lambda$new$3() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.MonetizationStarsInfoLink));
    }

    public void lambda$new$4(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        initWithdraw(false, inputCheckPasswordSRP, twoStepVerificationActivity);
    }

    public void lambda$new$5(BaseFragment baseFragment, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.balanceButton.setLoading(false);
        baseFragment.presentFragment(twoStepVerificationActivity);
    }

    public void lambda$new$6(final BaseFragment baseFragment, View view) {
        if (!view.isEnabled() || this.balanceButton.isLoading()) {
            return;
        }
        ButtonWithCounterView buttonWithCounterView = this.starsBalanceButton;
        if (buttonWithCounterView == null || !buttonWithCounterView.isLoading()) {
            final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            twoStepVerificationActivity.setDelegate(1, new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() {
                @Override
                public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                    ChannelMonetizationLayout.this.lambda$new$4(twoStepVerificationActivity, inputCheckPasswordSRP);
                }
            });
            this.balanceButton.setLoading(true);
            twoStepVerificationActivity.preload(new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.this.lambda$new$5(baseFragment, twoStepVerificationActivity);
                }
            });
        }
    }

    public void lambda$new$7(View view, boolean z) {
        this.starsBalanceEditTextContainer.animateSelection(z ? 1.0f : 0.0f);
    }

    public void lambda$new$8(int i) {
        Bulletin.hideVisible();
        long j = this.starsBalance;
        if (j < MessagesController.getInstance(i).starsRevenueWithdrawalMin) {
            this.starsBalanceEditTextAll = true;
        } else {
            this.starsBalanceEditTextAll = false;
            j = MessagesController.getInstance(i).starsRevenueWithdrawalMin;
        }
        this.starsBalanceEditTextValue = j;
        this.starsBalanceEditTextIgnore = true;
        this.starsBalanceEditText.setText(Long.toString(this.starsBalanceEditTextValue));
        EditTextBoldCursor editTextBoldCursor = this.starsBalanceEditText;
        editTextBoldCursor.setSelection(editTextBoldCursor.getText().length());
        this.starsBalanceEditTextIgnore = false;
        AndroidUtilities.cancelRunOnUIThread(this.setStarsBalanceButtonText);
        this.setStarsBalanceButtonText.run();
    }

    public void lambda$new$9(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        initWithdraw(true, inputCheckPasswordSRP, twoStepVerificationActivity);
    }

    public void lambda$onClick$34(LimitReachedBottomSheet limitReachedBottomSheet, ChannelBoostsController.CanApplyBoost canApplyBoost) {
        limitReachedBottomSheet.setCanApplyBoost(canApplyBoost);
        this.fragment.showDialog(limitReachedBottomSheet);
    }

    public void lambda$onNestedScroll$41() {
        try {
            RecyclerListView currentListView = this.transactionsLayout.getCurrentListView();
            if (currentListView == null || currentListView.getAdapter() == null) {
                return;
            }
            currentListView.getAdapter().notifyDataSetChanged();
        } catch (Throwable unused) {
        }
    }

    public void lambda$sendCpmUpdate$35(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else if (tLObject instanceof TLRPC.Updates) {
            this.initialSwitchOffValue = this.switchOffValue;
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$sendCpmUpdate$36(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.this.lambda$sendCpmUpdate$35(tL_error, tLObject);
            }
        });
    }

    public static void lambda$showTransactionSheet$37(Context context, TL_stats.TL_broadcastRevenueTransactionWithdrawal tL_broadcastRevenueTransactionWithdrawal, View view) {
        Browser.openUrl(context, tL_broadcastRevenueTransactionWithdrawal.transaction_url);
    }

    private void loadStarsStats() {
        if (this.starsRevenueAvailable) {
            final TLRPC.TL_payments_starsRevenueStats starsRevenueStats = BotStarsController.getInstance(this.currentAccount).getStarsRevenueStats(this.dialogId);
            if (starsRevenueStats != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChannelMonetizationLayout.this.lambda$loadStarsStats$25(starsRevenueStats);
                    }
                });
                return;
            }
            TLRPC.TL_payments_getStarsRevenueStats tL_payments_getStarsRevenueStats = new TLRPC.TL_payments_getStarsRevenueStats();
            tL_payments_getStarsRevenueStats.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_payments_getStarsRevenueStats.dark = Theme.isCurrentThemeDark();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getStarsRevenueStats, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelMonetizationLayout.this.lambda$loadStarsStats$27(tLObject, tL_error);
                }
            });
        }
    }

    public static BottomSheet makeLearnSheet(final Context context, final boolean z, Theme.ResourcesProvider resourcesProvider) {
        final BottomSheet bottomSheet = new BottomSheet(context, false, resourcesProvider);
        bottomSheet.fixNavigationBar();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setImageResource(R.drawable.large_monetize);
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider)));
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(80, 80, 1, 0, 16, 0, 16));
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i, resourcesProvider));
        textView.setText(LocaleController.getString(z ? R.string.BotMonetizationInfoTitle : R.string.MonetizationInfoTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 8.0f, 0.0f, 8.0f, 25.0f));
        linearLayout.addView(new FeatureCell(context, R.drawable.msg_channel, LocaleController.getString(z ? R.string.BotMonetizationInfoFeature1Name : R.string.MonetizationInfoFeature1Name), LocaleController.getString(z ? R.string.BotMonetizationInfoFeature1Text : R.string.MonetizationInfoFeature1Text), resourcesProvider), LayoutHelper.createLinear(-1, -2, 49, 0, 0, 0, 16));
        linearLayout.addView(new FeatureCell(context, R.drawable.menu_feature_split, LocaleController.getString(z ? R.string.BotMonetizationInfoFeature2Name : R.string.MonetizationInfoFeature2Name), LocaleController.getString(z ? R.string.BotMonetizationInfoFeature2Text : R.string.MonetizationInfoFeature2Text), resourcesProvider), LayoutHelper.createLinear(-1, -2, 49, 0, 0, 0, 16));
        linearLayout.addView(new FeatureCell(context, R.drawable.menu_feature_withdrawals, LocaleController.getString(z ? R.string.BotMonetizationInfoFeature3Name : R.string.MonetizationInfoFeature3Name), LocaleController.getString(z ? R.string.BotMonetizationInfoFeature3Text : R.string.MonetizationInfoFeature3Text), resourcesProvider), LayoutHelper.createLinear(-1, -2, 49, 0, 0, 0, 16));
        View view = new View(context);
        view.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 1.0f / AndroidUtilities.density, 55, 12, 0, 12, 0));
        AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(context);
        textViewEmojis.setGravity(17);
        textViewEmojis.setTextSize(1, 20.0f);
        textViewEmojis.setTypeface(AndroidUtilities.bold());
        textViewEmojis.setTextColor(Theme.getColor(i, resourcesProvider));
        SpannableString spannableString = new SpannableString("💎");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.ton);
        coloredImageSpan.setScale(0.9f, 0.9f);
        coloredImageSpan.setColorKey(Theme.key_windowBackgroundWhiteBlueText2);
        coloredImageSpan.setRelativeSize(textViewEmojis.getPaint().getFontMetricsInt());
        coloredImageSpan.spaceScaleX = 0.9f;
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length(), 33);
        textViewEmojis.setText(AndroidUtilities.replaceCharSequence("💎", LocaleController.getString(z ? R.string.BotMonetizationInfoTONTitle : R.string.MonetizationInfoTONTitle), spannableString));
        linearLayout.addView(textViewEmojis, LayoutHelper.createLinear(-1, -2, 8.0f, 20.0f, 8.0f, 0.0f));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setGravity(17);
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setTextColor(Theme.getColor(i, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linksTextView.setText(AndroidUtilities.withLearnMore(AndroidUtilities.replaceTags(LocaleController.getString(z ? R.string.BotMonetizationInfoTONText : R.string.MonetizationInfoTONText)), new Runnable() {
            @Override
            public final void run() {
                ChannelMonetizationLayout.lambda$makeLearnSheet$39(context, z);
            }
        }));
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 28.0f, 9.0f, 28.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString(R.string.GotIt), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                BottomSheet.this.dismiss();
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 55, 10, 25, 10, 14));
        bottomSheet.setCustomView(linearLayout);
        return bottomSheet;
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.id == 1) {
            if (this.currentBoostLevel >= MessagesController.getInstance(this.currentAccount).channelRestrictSponsoredLevelMin) {
                this.switchOffValue = !this.switchOffValue;
                AndroidUtilities.cancelRunOnUIThread(this.sendCpmUpdateRunnable);
                AndroidUtilities.runOnUIThread(this.sendCpmUpdateRunnable, 1000L);
                this.listView.adapter.update(true);
                return;
            }
            if (this.boostsStatus == null) {
                return;
            }
            final LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this.fragment, getContext(), 30, this.currentAccount, this.resourcesProvider);
            limitReachedBottomSheet.setDialogId(this.dialogId);
            limitReachedBottomSheet.setBoostsStats(this.boostsStatus, true);
            MessagesController.getInstance(this.currentAccount).getBoostsController().userCanBoostChannel(this.dialogId, this.boostsStatus, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ChannelMonetizationLayout.this.lambda$onClick$34(limitReachedBottomSheet, (ChannelBoostsController.CanApplyBoost) obj);
                }
            });
        }
    }

    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        return false;
    }

    public static CharSequence replaceTON(CharSequence charSequence, TextPaint textPaint) {
        return replaceTON(charSequence, textPaint, 1.0f, true);
    }

    public static CharSequence replaceTON(CharSequence charSequence, TextPaint textPaint, float f, float f2, boolean z) {
        if (tonString == null) {
            tonString = new HashMap();
        }
        int i = ((textPaint.getFontMetricsInt().bottom * (z ? 1 : -1)) * ((int) (f * 100.0f))) - ((int) (100.0f * f2));
        SpannableString spannableString = (SpannableString) tonString.get(Integer.valueOf(i));
        if (spannableString == null) {
            spannableString = new SpannableString("T");
            if (z) {
                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.ton);
                coloredImageSpan.setScale(f, f);
                coloredImageSpan.setColorKey(Theme.key_windowBackgroundWhiteBlueText2);
                coloredImageSpan.setRelativeSize(textPaint.getFontMetricsInt());
                coloredImageSpan.spaceScaleX = 0.9f;
                spannableString.setSpan(coloredImageSpan, 0, spannableString.length(), 33);
            } else {
                ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(R.drawable.mini_ton);
                coloredImageSpan2.setScale(f, f);
                coloredImageSpan2.setTranslateY(f2);
                coloredImageSpan2.spaceScaleX = 0.95f;
                spannableString.setSpan(coloredImageSpan2, 0, spannableString.length(), 33);
            }
            tonString.put(Integer.valueOf(i), spannableString);
        }
        return AndroidUtilities.replaceMultipleCharSequence("TON", charSequence, spannableString);
    }

    public static CharSequence replaceTON(CharSequence charSequence, TextPaint textPaint, float f, boolean z) {
        return replaceTON(charSequence, textPaint, f, 0.0f, z);
    }

    public void sendCpmUpdate() {
        AndroidUtilities.cancelRunOnUIThread(this.sendCpmUpdateRunnable);
        if (this.switchOffValue == this.initialSwitchOffValue) {
            return;
        }
        TLRPC.TL_channels_restrictSponsoredMessages tL_channels_restrictSponsoredMessages = new TLRPC.TL_channels_restrictSponsoredMessages();
        tL_channels_restrictSponsoredMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(-this.dialogId);
        tL_channels_restrictSponsoredMessages.restricted = this.switchOffValue;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_restrictSponsoredMessages, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelMonetizationLayout.this.lambda$sendCpmUpdate$36(tLObject, tL_error);
            }
        });
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
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replaceTON("TON " + this.formatter.format(d2), this.balanceTitle.getPaint(), 0.9f, true));
        int indexOf = TextUtils.indexOf(spannableStringBuilder, ".");
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(this.balanceTitleSizeSpan, indexOf, spannableStringBuilder.length(), 33);
        }
        this.balanceTitle.setText(spannableStringBuilder);
        this.balanceSubtitle.setText("≈" + BillingController.getInstance().formatCurrency(j2, "USD"));
    }

    private void setStarsBalance(long j, int i) {
        if (this.balanceTitle == null || this.balanceSubtitle == null) {
            return;
        }
        double d = this.stars_rate;
        double d2 = j;
        Double.isNaN(d2);
        long j2 = (long) (d * d2 * 100.0d);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(StarsIntroActivity.replaceStarsWithPlain("XTR " + LocaleController.formatNumber(j, ' '), 1.0f));
        int indexOf = TextUtils.indexOf(spannableStringBuilder, ".");
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(this.balanceTitleSizeSpan, indexOf, spannableStringBuilder.length(), 33);
        }
        this.starsBalance = j;
        this.starsBalanceTitle.setText(spannableStringBuilder);
        this.starsBalanceSubtitle.setText("≈" + BillingController.getInstance().formatCurrency(j2, "USD"));
        this.starsBalanceEditTextContainer.setVisibility(j > 0 ? 0 : 8);
        if (this.starsBalanceEditTextAll) {
            this.starsBalanceEditTextIgnore = true;
            EditTextBoldCursor editTextBoldCursor = this.starsBalanceEditText;
            this.starsBalanceEditTextValue = j;
            editTextBoldCursor.setText(Long.toString(j));
            EditTextBoldCursor editTextBoldCursor2 = this.starsBalanceEditText;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.getText().length());
            this.starsBalanceEditTextIgnore = false;
            this.starsBalanceButton.setEnabled(this.starsBalanceEditTextValue > 0);
        }
        ButtonWithCounterView buttonWithCounterView = this.starsAdsButton;
        if (buttonWithCounterView != null) {
            buttonWithCounterView.setEnabled(j2 > 0);
        }
        this.starsBalanceBlockedUntil = i;
        AndroidUtilities.cancelRunOnUIThread(this.setStarsBalanceButtonText);
        this.setStarsBalanceButtonText.run();
    }

    public static void showTransactionSheet(final Context context, int i, TL_stats.BroadcastRevenueTransaction broadcastRevenueTransaction, long j, Theme.ResourcesProvider resourcesProvider) {
        boolean z;
        ViewGroup viewGroup;
        long j2;
        long j3;
        String str;
        long j4;
        boolean z2;
        char c;
        boolean z3;
        String str2;
        BottomSheet bottomSheet;
        String userName;
        TLRPC.User user;
        BottomSheet bottomSheet2 = new BottomSheet(context, false, resourcesProvider);
        bottomSheet2.fixNavigationBar();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        boolean z4 = broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionWithdrawal;
        if (z4) {
            TL_stats.TL_broadcastRevenueTransactionWithdrawal tL_broadcastRevenueTransactionWithdrawal = (TL_stats.TL_broadcastRevenueTransactionWithdrawal) broadcastRevenueTransaction;
            String string = LocaleController.getString(R.string.MonetizationTransactionDetailWithdraw);
            j2 = tL_broadcastRevenueTransactionWithdrawal.date;
            z = z4;
            j3 = tL_broadcastRevenueTransactionWithdrawal.amount;
            viewGroup = linearLayout;
            z3 = tL_broadcastRevenueTransactionWithdrawal.pending;
            j4 = 0;
            c = 65535;
            str = string;
            z2 = tL_broadcastRevenueTransactionWithdrawal.failed;
        } else {
            z = z4;
            if (broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionProceeds) {
                TL_stats.TL_broadcastRevenueTransactionProceeds tL_broadcastRevenueTransactionProceeds = (TL_stats.TL_broadcastRevenueTransactionProceeds) broadcastRevenueTransaction;
                String string2 = LocaleController.getString(R.string.MonetizationTransactionDetailProceed);
                j2 = tL_broadcastRevenueTransactionProceeds.from_date;
                viewGroup = linearLayout;
                j4 = tL_broadcastRevenueTransactionProceeds.to_date;
                j3 = tL_broadcastRevenueTransactionProceeds.amount;
                str = string2;
            } else {
                viewGroup = linearLayout;
                if (!(broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionRefund)) {
                    return;
                }
                TL_stats.TL_broadcastRevenueTransactionRefund tL_broadcastRevenueTransactionRefund = (TL_stats.TL_broadcastRevenueTransactionRefund) broadcastRevenueTransaction;
                String string3 = LocaleController.getString(R.string.MonetizationTransactionDetailRefund);
                j2 = tL_broadcastRevenueTransactionRefund.from_date;
                j3 = tL_broadcastRevenueTransactionRefund.amount;
                str = string3;
                j4 = 0;
            }
            z2 = false;
            c = 1;
            z3 = false;
        }
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setDecimalSeparator('.');
        String str3 = str;
        DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setMaximumFractionDigits(12);
        decimalFormat.setGroupingUsed(false);
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.getColor(c < 0 ? Theme.key_text_RedBold : Theme.key_avatar_nameInMessageGreen));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) (c < 0 ? "-" : "+"));
        double abs = Math.abs(j3);
        Double.isNaN(abs);
        double round = Math.round((abs / 1.0E9d) * 100000.0d);
        Double.isNaN(round);
        spannableStringBuilder.append((CharSequence) decimalFormat.format(round / 100000.0d));
        spannableStringBuilder.append((CharSequence) " TON");
        int indexOf = TextUtils.indexOf(spannableStringBuilder, ".");
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(new RelativeSizeSpan(1.3333334f), 0, indexOf, 33);
        }
        textView.setText(spannableStringBuilder);
        ViewGroup viewGroup2 = viewGroup;
        viewGroup2.addView(textView, LayoutHelper.createLinear(-1, -2, 49, 0, 24, 0, 6));
        TextView textView2 = new TextView(context);
        textView2.setGravity(17);
        textView2.setTextSize(1, 13.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        if (z3) {
            str2 = LocaleController.getString(R.string.MonetizationTransactionPending);
        } else if (j2 == 0) {
            str2 = LocaleController.formatShortDateTime(j4);
        } else if (j4 == 0) {
            str2 = LocaleController.formatShortDateTime(j2);
        } else {
            str2 = LocaleController.formatShortDateTime(j2) + " - " + LocaleController.formatShortDateTime(j4);
        }
        textView2.setText(str2);
        if (z2) {
            textView2.setTextColor(Theme.getColor(Theme.key_text_RedBold, resourcesProvider));
            textView2.setText(TextUtils.concat(textView2.getText(), " — ", LocaleController.getString(R.string.MonetizationTransactionNotCompleted)));
        }
        viewGroup2.addView(textView2, LayoutHelper.createLinear(-1, -2, 49, 0, 0, 0, 0));
        TextView textView3 = new TextView(context);
        textView3.setGravity(17);
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView3.setText(str3);
        viewGroup2.addView(textView3, LayoutHelper.createLinear(-1, -2, 49, 0, 27, 0, 0));
        if (broadcastRevenueTransaction instanceof TL_stats.TL_broadcastRevenueTransactionProceeds) {
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_groupcreate_spanBackground, resourcesProvider)));
            MessagesController messagesController = MessagesController.getInstance(i);
            if (j < 0) {
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
                if (chat == null) {
                    userName = "";
                    user = chat;
                } else {
                    userName = chat.title;
                    user = chat;
                }
            } else {
                TLRPC.User user2 = messagesController.getUser(Long.valueOf(j));
                userName = UserObject.getUserName(user2);
                user = user2;
            }
            BackupImageView backupImageView = new BackupImageView(context);
            backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo((TLObject) user);
            backupImageView.setForUserOrChat(user, avatarDrawable);
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(28, 28, 51));
            TextView textView4 = new TextView(context);
            textView4.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
            textView4.setTextSize(1, 13.0f);
            textView4.setSingleLine();
            textView4.setText(userName);
            frameLayout.addView(textView4, LayoutHelper.createFrame(-2, -2.0f, 19, 37.0f, 0.0f, 10.0f, 0.0f));
            viewGroup2.addView(frameLayout, LayoutHelper.createLinear(-2, 28, 1, 42, 10, 42, 0));
        }
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        if (z) {
            final TL_stats.TL_broadcastRevenueTransactionWithdrawal tL_broadcastRevenueTransactionWithdrawal2 = (TL_stats.TL_broadcastRevenueTransactionWithdrawal) broadcastRevenueTransaction;
            if ((tL_broadcastRevenueTransactionWithdrawal2.flags & 2) != 0) {
                buttonWithCounterView.setText(LocaleController.getString(R.string.MonetizationTransactionDetailWithdrawButton), false);
                buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        ChannelMonetizationLayout.lambda$showTransactionSheet$37(context, tL_broadcastRevenueTransactionWithdrawal2, view);
                    }
                });
                bottomSheet = bottomSheet2;
                viewGroup2.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 55, 18, 30, 18, 14));
                bottomSheet.setCustomView(viewGroup2);
                bottomSheet.show();
            }
        }
        buttonWithCounterView.setText(LocaleController.getString(R.string.OK), false);
        final BottomSheet bottomSheet3 = bottomSheet2;
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BottomSheet.this.dismiss();
            }
        });
        bottomSheet = bottomSheet3;
        viewGroup2.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 55, 18, 30, 18, 14));
        bottomSheet.setCustomView(viewGroup2);
        bottomSheet.show();
    }

    @Override
    public void onAttachedToWindow() {
        instance = this;
        super.onAttachedToWindow();
        checkLearnSheet();
    }

    @Override
    public void onDetachedFromWindow() {
        instance = null;
        super.onDetachedFromWindow();
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setCastShadows(true);
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
        if (view == this.listView && this.transactionsLayout.isAttachedToWindow()) {
            ((View) this.transactionsLayout.getParent()).getTop();
            int i4 = AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY;
            ActionBar.getCurrentActionBarHeight();
            int bottom = ((View) this.transactionsLayout.getParent()).getBottom();
            if (i2 >= 0) {
                if (i2 > 0) {
                    RecyclerListView currentListView = this.transactionsLayout.getCurrentListView();
                    if (this.listView.getHeight() - bottom < 0 || currentListView == null || currentListView.canScrollVertically(1)) {
                        return;
                    }
                    iArr[1] = i2;
                    this.listView.stopScroll();
                    return;
                }
                return;
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.setCastShadows(!isAttachedToWindow() || this.listView.getHeight() - bottom < 0);
            }
            if (this.listView.getHeight() - bottom >= 0) {
                RecyclerListView currentListView2 = this.transactionsLayout.getCurrentListView();
                int findFirstVisibleItemPosition = ((LinearLayoutManager) currentListView2.getLayoutManager()).findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition != -1) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = currentListView2.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                    int top = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() : -1;
                    int paddingTop = currentListView2.getPaddingTop();
                    if (top == paddingTop && findFirstVisibleItemPosition == 0) {
                        return;
                    }
                    iArr[1] = findFirstVisibleItemPosition != 0 ? i2 : Math.max(i2, top - paddingTop);
                    currentListView2.scrollBy(0, i2);
                }
            }
        }
    }

    @Override
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
    }

    @Override
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        boolean z;
        try {
            if (view == this.listView && this.transactionsLayout.isAttachedToWindow()) {
                RecyclerListView currentListView = this.transactionsLayout.getCurrentListView();
                int bottom = ((View) this.transactionsLayout.getParent()).getBottom();
                ActionBar actionBar = this.actionBar;
                if (actionBar != null) {
                    if (isAttachedToWindow() && this.listView.getHeight() - bottom >= 0) {
                        z = false;
                        actionBar.setCastShadows(z);
                    }
                    z = true;
                    actionBar.setCastShadows(z);
                }
                if (this.listView.getHeight() - bottom >= 0) {
                    iArr[1] = i4;
                    currentListView.scrollBy(0, i4);
                }
            }
        } catch (Throwable th) {
            FileLog.e(th);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChannelMonetizationLayout.this.lambda$onNestedScroll$41();
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

    public void reloadTransactions() {
        this.transactionsLayout.reloadTransactions();
    }

    public void setActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    public void setupBalances(TLRPC.BroadcastRevenueBalances broadcastRevenueBalances) {
        UniversalAdapter universalAdapter;
        double d = this.ton_rate;
        if (d == 0.0d) {
            return;
        }
        ProceedOverview proceedOverview = this.availableValue;
        proceedOverview.contains1 = true;
        long j = broadcastRevenueBalances.available_balance;
        proceedOverview.crypto_amount = j;
        double d2 = j;
        Double.isNaN(d2);
        long j2 = (long) ((d2 / 1.0E9d) * d * 100.0d);
        proceedOverview.amount = j2;
        setBalance(j, j2);
        this.availableValue.currency = "USD";
        ProceedOverview proceedOverview2 = this.lastWithdrawalValue;
        proceedOverview2.contains1 = true;
        long j3 = broadcastRevenueBalances.current_balance;
        proceedOverview2.crypto_amount = j3;
        double d3 = j3;
        Double.isNaN(d3);
        double d4 = this.ton_rate;
        proceedOverview2.amount = (long) ((d3 / 1.0E9d) * d4 * 100.0d);
        proceedOverview2.currency = "USD";
        ProceedOverview proceedOverview3 = this.lifetimeValue;
        proceedOverview3.contains1 = true;
        long j4 = broadcastRevenueBalances.overall_revenue;
        proceedOverview3.crypto_amount = j4;
        double d5 = j4;
        Double.isNaN(d5);
        proceedOverview3.amount = (long) ((d5 / 1.0E9d) * d4 * 100.0d);
        proceedOverview3.currency = "USD";
        this.proceedsAvailable = true;
        this.balanceButton.setVisibility((broadcastRevenueBalances.available_balance <= 0 || !broadcastRevenueBalances.withdrawal_enabled) ? 8 : 0);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    public void setupBalances(TLRPC.TL_starsRevenueStatus tL_starsRevenueStatus) {
        UniversalAdapter universalAdapter;
        double d = this.stars_rate;
        if (d == 0.0d) {
            return;
        }
        ProceedOverview proceedOverview = this.availableValue;
        proceedOverview.contains2 = true;
        long j = tL_starsRevenueStatus.available_balance;
        proceedOverview.crypto_amount2 = j;
        double d2 = j;
        Double.isNaN(d2);
        proceedOverview.amount2 = (long) (d2 * d * 100.0d);
        setStarsBalance(j, tL_starsRevenueStatus.next_withdrawal_at);
        this.availableValue.currency = "USD";
        ProceedOverview proceedOverview2 = this.lastWithdrawalValue;
        proceedOverview2.contains2 = true;
        long j2 = tL_starsRevenueStatus.current_balance;
        proceedOverview2.crypto_amount2 = j2;
        double d3 = j2;
        double d4 = this.stars_rate;
        Double.isNaN(d3);
        proceedOverview2.amount2 = (long) (d3 * d4 * 100.0d);
        proceedOverview2.currency = "USD";
        ProceedOverview proceedOverview3 = this.lifetimeValue;
        proceedOverview3.contains2 = true;
        long j3 = tL_starsRevenueStatus.overall_revenue;
        proceedOverview3.crypto_amount2 = j3;
        double d5 = j3;
        Double.isNaN(d5);
        proceedOverview3.amount2 = (long) (d5 * d4 * 100.0d);
        proceedOverview3.currency = "USD";
        this.proceedsAvailable = true;
        LinearLayout linearLayout = this.starsBalanceButtonsLayout;
        if (linearLayout != null) {
            linearLayout.setVisibility(tL_starsRevenueStatus.withdrawal_enabled ? 0 : 8);
        }
        ButtonWithCounterView buttonWithCounterView = this.starsBalanceButton;
        if (buttonWithCounterView != null) {
            buttonWithCounterView.setVisibility((tL_starsRevenueStatus.available_balance > 0 || BuildVars.DEBUG_PRIVATE_VERSION) ? 0 : 8);
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    public void updateList() {
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }
}
