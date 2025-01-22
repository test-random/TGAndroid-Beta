package org.telegram.ui.bots;

import android.content.Context;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;

public class SuggestedAffiliateProgramsFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private BackDrawable backDrawable;
    private final long dialogId;
    private UniversalRecyclerView listView;

    public SuggestedAffiliateProgramsFragment(long j) {
        this.dialogId = j;
    }

    @Override
    public View createView(Context context) {
        ActionBar actionBar = this.actionBar;
        BackDrawable backDrawable = new BackDrawable(false);
        this.backDrawable = backDrawable;
        actionBar.setBackButtonDrawable(backDrawable);
        this.backDrawable.setAnimationTime(240.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    SuggestedAffiliateProgramsFragment.this.lambda$onBackPressed$323();
                }
            }
        });
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ActionBar actionBar2 = this.actionBar;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        actionBar2.setItemsColor(Theme.getColor(i), false);
        this.actionBar.setItemsColor(Theme.getColor(i), true);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), false);
        this.actionBar.setTitleColor(Theme.getColor(i));
        this.actionBar.setTitle(LocaleController.getString(R.string.ChannelAffiliatePrograms));
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context);
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                SuggestedAffiliateProgramsFragment.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                SuggestedAffiliateProgramsFragment.this.onItemClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        sizeNotifierFrameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1, 119));
        this.fragmentView = sizeNotifierFrameLayout;
        return sizeNotifierFrameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        if (i == NotificationCenter.channelSuggestedBotsUpdate && ((Long) objArr[0]).longValue() == this.dialogId && (universalRecyclerView = this.listView) != null && (universalRecyclerView.getAdapter() instanceof UniversalAdapter)) {
            ((UniversalAdapter) this.listView.getAdapter()).update(true);
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        BotStarsController.ChannelSuggestedBots channelSuggestedBots = BotStarsController.getInstance(this.currentAccount).getChannelSuggestedBots(this.dialogId);
        for (int i = 0; i < channelSuggestedBots.bots.size(); i++) {
            arrayList.add(ChannelAffiliateProgramsFragment.BotCell.Factory.as(channelSuggestedBots.bots.get(i), false));
        }
        if (channelSuggestedBots.isLoading()) {
            arrayList.add(UItem.asFlicker(29));
            arrayList.add(UItem.asFlicker(29));
            arrayList.add(UItem.asFlicker(29));
        }
    }

    @Override
    public boolean isLightStatusBar() {
        if (getLastStoryViewer() != null && getLastStoryViewer().isShown()) {
            return false;
        }
        int color = Theme.getColor(Theme.key_windowBackgroundWhite);
        if (this.actionBar.isActionModeShowed()) {
            color = Theme.getColor(Theme.key_actionBarActionModeDefault);
        }
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.channelSuggestedBotsUpdate);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.channelSuggestedBotsUpdate);
        super.onFragmentDestroy();
    }

    public void onItemClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof TL_payments.starRefProgram) {
            ChannelAffiliateProgramsFragment.showConnectAffiliateAlert(getContext(), this.currentAccount, (TL_payments.starRefProgram) obj, this.dialogId, this.resourceProvider, false);
        }
    }
}
