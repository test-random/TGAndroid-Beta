package org.telegram.ui.Components;

import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorBtnCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class AdminLogFilterAlert2 extends BottomSheetWithRecyclerListView {
    private final ButtonWithCounterView actionButton;
    private UniversalAdapter adapter;
    private final SelectorBtnCell buttonContainer;
    private ArrayList currentAdmins;
    private TLRPC.TL_channelAdminLogEventsFilter currentFilter;
    private AdminLogFilterAlertDelegate delegate;
    private boolean isMegagroup;
    private boolean sectionMembersExpanded;
    private boolean sectionMessagesExpanded;
    private boolean sectionSettingsExpanded;
    private LongSparseArray selectedAdmins;

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, LongSparseArray longSparseArray);
    }

    public AdminLogFilterAlert2(BaseFragment baseFragment, TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, LongSparseArray longSparseArray, boolean z) {
        super(baseFragment.getContext(), baseFragment, false, false, false, true, BottomSheetWithRecyclerListView.ActionBarType.SLIDING, baseFragment.getResourceProvider());
        this.currentFilter = new TLRPC.TL_channelAdminLogEventsFilter();
        this.sectionMembersExpanded = false;
        this.sectionSettingsExpanded = false;
        this.sectionMessagesExpanded = false;
        this.topPadding = 0.35f;
        fixNavigationBar();
        setSlidingActionBar();
        setShowHandle(true);
        if (tL_channelAdminLogEventsFilter != null) {
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2 = this.currentFilter;
            tL_channelAdminLogEventsFilter2.join = tL_channelAdminLogEventsFilter.join;
            tL_channelAdminLogEventsFilter2.leave = tL_channelAdminLogEventsFilter.leave;
            tL_channelAdminLogEventsFilter2.invite = tL_channelAdminLogEventsFilter.invite;
            tL_channelAdminLogEventsFilter2.ban = tL_channelAdminLogEventsFilter.ban;
            tL_channelAdminLogEventsFilter2.unban = tL_channelAdminLogEventsFilter.unban;
            tL_channelAdminLogEventsFilter2.kick = tL_channelAdminLogEventsFilter.kick;
            tL_channelAdminLogEventsFilter2.unkick = tL_channelAdminLogEventsFilter.unkick;
            tL_channelAdminLogEventsFilter2.promote = tL_channelAdminLogEventsFilter.promote;
            tL_channelAdminLogEventsFilter2.demote = tL_channelAdminLogEventsFilter.demote;
            tL_channelAdminLogEventsFilter2.info = tL_channelAdminLogEventsFilter.info;
            tL_channelAdminLogEventsFilter2.settings = tL_channelAdminLogEventsFilter.settings;
            tL_channelAdminLogEventsFilter2.pinned = tL_channelAdminLogEventsFilter.pinned;
            tL_channelAdminLogEventsFilter2.edit = tL_channelAdminLogEventsFilter.edit;
            tL_channelAdminLogEventsFilter2.delete = tL_channelAdminLogEventsFilter.delete;
            tL_channelAdminLogEventsFilter2.group_call = tL_channelAdminLogEventsFilter.group_call;
            tL_channelAdminLogEventsFilter2.invites = tL_channelAdminLogEventsFilter.invites;
        } else {
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3 = this.currentFilter;
            tL_channelAdminLogEventsFilter3.join = true;
            tL_channelAdminLogEventsFilter3.leave = true;
            tL_channelAdminLogEventsFilter3.invite = true;
            tL_channelAdminLogEventsFilter3.ban = true;
            tL_channelAdminLogEventsFilter3.unban = true;
            tL_channelAdminLogEventsFilter3.kick = true;
            tL_channelAdminLogEventsFilter3.unkick = true;
            tL_channelAdminLogEventsFilter3.promote = true;
            tL_channelAdminLogEventsFilter3.demote = true;
            tL_channelAdminLogEventsFilter3.info = true;
            tL_channelAdminLogEventsFilter3.settings = true;
            tL_channelAdminLogEventsFilter3.pinned = true;
            tL_channelAdminLogEventsFilter3.edit = true;
            tL_channelAdminLogEventsFilter3.delete = true;
            tL_channelAdminLogEventsFilter3.group_call = true;
            tL_channelAdminLogEventsFilter3.invites = true;
        }
        if (longSparseArray != null) {
            this.selectedAdmins = longSparseArray.m2clone();
        }
        this.isMegagroup = z;
        this.adapter.update(false);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDurations(350L);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                AdminLogFilterAlert2.this.lambda$new$0(view, i, f, f2);
            }
        });
        SelectorBtnCell selectorBtnCell = new SelectorBtnCell(getContext(), this.resourcesProvider, null);
        this.buttonContainer = selectorBtnCell;
        selectorBtnCell.setClickable(true);
        selectorBtnCell.setOrientation(1);
        selectorBtnCell.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        selectorBtnCell.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), this.resourcesProvider);
        this.actionButton = buttonWithCounterView;
        buttonWithCounterView.setText(LocaleController.getString(R.string.EventLogFilterApply), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AdminLogFilterAlert2.this.lambda$new$1(view);
            }
        });
        selectorBtnCell.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 87));
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.addView(selectorBtnCell, LayoutHelper.createFrameMarginPx(-1, -2.0f, 87, i, 0, i, 0));
        RecyclerListView recyclerListView = this.recyclerListView;
        int i2 = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i2, 0, i2, AndroidUtilities.dp(68.0f));
    }

    private View.OnClickListener getGroupClick(final int i) {
        return new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AdminLogFilterAlert2.this.lambda$getGroupClick$2(i, view);
            }
        };
    }

    private String getGroupCount(int i) {
        StringBuilder sb;
        if (i != 0) {
            if (i != 1) {
                sb = new StringBuilder();
                TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = this.currentFilter;
                sb.append((tL_channelAdminLogEventsFilter.delete ? 1 : 0) + (tL_channelAdminLogEventsFilter.edit ? 1 : 0) + (tL_channelAdminLogEventsFilter.pinned ? 1 : 0));
            } else {
                sb = new StringBuilder();
                TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2 = this.currentFilter;
                sb.append(((tL_channelAdminLogEventsFilter2.info || tL_channelAdminLogEventsFilter2.settings) ? 1 : 0) + (tL_channelAdminLogEventsFilter2.invites ? 1 : 0) + (tL_channelAdminLogEventsFilter2.group_call ? 1 : 0));
            }
            sb.append("/3");
        } else {
            sb = new StringBuilder();
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3 = this.currentFilter;
            sb.append(((tL_channelAdminLogEventsFilter3.promote || tL_channelAdminLogEventsFilter3.demote) ? 1 : 0) + ((this.isMegagroup && (tL_channelAdminLogEventsFilter3.kick || tL_channelAdminLogEventsFilter3.ban || tL_channelAdminLogEventsFilter3.unkick || tL_channelAdminLogEventsFilter3.unban)) ? 1 : 0) + ((tL_channelAdminLogEventsFilter3.invite || tL_channelAdminLogEventsFilter3.join) ? 1 : 0) + (tL_channelAdminLogEventsFilter3.leave ? 1 : 0));
            sb.append("/");
            sb.append(this.isMegagroup ? 4 : 3);
        }
        return sb.toString();
    }

    public void lambda$getGroupClick$2(int i, View view) {
        if (i == 0) {
            this.sectionMembersExpanded = !this.sectionMembersExpanded;
        } else if (i == 1) {
            this.sectionSettingsExpanded = !this.sectionSettingsExpanded;
        } else if (i == 2) {
            this.sectionMessagesExpanded = !this.sectionMessagesExpanded;
        }
        this.adapter.update(true);
        applyScrolledPosition();
    }

    public void lambda$new$0(View view, int i, float f, float f2) {
        onClick(this.adapter.getItem(i - 1), view, f);
    }

    public void lambda$new$1(View view) {
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = this.currentFilter;
        if (tL_channelAdminLogEventsFilter.join && tL_channelAdminLogEventsFilter.leave && tL_channelAdminLogEventsFilter.invite && tL_channelAdminLogEventsFilter.ban && tL_channelAdminLogEventsFilter.unban && tL_channelAdminLogEventsFilter.kick && tL_channelAdminLogEventsFilter.unkick && tL_channelAdminLogEventsFilter.promote && tL_channelAdminLogEventsFilter.demote && tL_channelAdminLogEventsFilter.info && tL_channelAdminLogEventsFilter.settings && tL_channelAdminLogEventsFilter.pinned && tL_channelAdminLogEventsFilter.edit && tL_channelAdminLogEventsFilter.delete && tL_channelAdminLogEventsFilter.group_call && tL_channelAdminLogEventsFilter.invites) {
            this.currentFilter = null;
        }
        LongSparseArray longSparseArray = this.selectedAdmins;
        if (longSparseArray != null && this.currentAdmins != null && longSparseArray.size() >= this.currentAdmins.size()) {
            this.selectedAdmins = null;
        }
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        lambda$new$0();
    }

    @Override
    public boolean canDismissWithSwipe() {
        return !this.recyclerListView.canScrollVertically(-1);
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        UniversalAdapter universalAdapter = new UniversalAdapter(recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                AdminLogFilterAlert2.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, this.resourcesProvider);
        this.adapter = universalAdapter;
        return universalAdapter;
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        if (this.currentFilter == null) {
            return;
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EventLogFilterByActions)));
        UItem asRoundGroupCheckbox = UItem.asRoundGroupCheckbox(2, LocaleController.getString(this.isMegagroup ? R.string.EventLogFilterSectionMembers : R.string.EventLogFilterSectionSubscribers), getGroupCount(0));
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = this.currentFilter;
        arrayList.add(asRoundGroupCheckbox.setChecked(tL_channelAdminLogEventsFilter.promote || tL_channelAdminLogEventsFilter.demote || (this.isMegagroup && (tL_channelAdminLogEventsFilter.kick || tL_channelAdminLogEventsFilter.ban || tL_channelAdminLogEventsFilter.unkick || tL_channelAdminLogEventsFilter.unban)) || tL_channelAdminLogEventsFilter.invite || tL_channelAdminLogEventsFilter.join || tL_channelAdminLogEventsFilter.leave).setCollapsed(!this.sectionMembersExpanded).setClickCallback(getGroupClick(0)));
        if (this.sectionMembersExpanded) {
            UItem pad = UItem.asRoundCheckbox(3, LocaleController.getString(R.string.EventLogFilterSectionAdmin)).pad();
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2 = this.currentFilter;
            arrayList.add(pad.setChecked(tL_channelAdminLogEventsFilter2.promote || tL_channelAdminLogEventsFilter2.demote));
            if (this.isMegagroup) {
                UItem pad2 = UItem.asRoundCheckbox(4, LocaleController.getString(R.string.EventLogFilterNewRestrictions)).pad();
                TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3 = this.currentFilter;
                arrayList.add(pad2.setChecked(tL_channelAdminLogEventsFilter3.kick || tL_channelAdminLogEventsFilter3.ban || tL_channelAdminLogEventsFilter3.unkick || tL_channelAdminLogEventsFilter3.unban));
            }
            UItem pad3 = UItem.asRoundCheckbox(5, LocaleController.getString(this.isMegagroup ? R.string.EventLogFilterNewMembers : R.string.EventLogFilterNewSubscribers)).pad();
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter4 = this.currentFilter;
            arrayList.add(pad3.setChecked(tL_channelAdminLogEventsFilter4.invite || tL_channelAdminLogEventsFilter4.join));
            arrayList.add(UItem.asRoundCheckbox(6, LocaleController.getString(this.isMegagroup ? R.string.EventLogFilterLeavingMembers2 : R.string.EventLogFilterLeavingSubscribers2)).pad().setChecked(this.currentFilter.leave));
        }
        UItem asRoundGroupCheckbox2 = UItem.asRoundGroupCheckbox(7, LocaleController.getString(this.isMegagroup ? R.string.EventLogFilterSectionGroupSettings : R.string.EventLogFilterSectionChannelSettings), getGroupCount(1));
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter5 = this.currentFilter;
        arrayList.add(asRoundGroupCheckbox2.setChecked(tL_channelAdminLogEventsFilter5.info || tL_channelAdminLogEventsFilter5.settings || tL_channelAdminLogEventsFilter5.invites || tL_channelAdminLogEventsFilter5.group_call).setCollapsed(!this.sectionSettingsExpanded).setClickCallback(getGroupClick(1)));
        if (this.sectionSettingsExpanded) {
            UItem pad4 = UItem.asRoundCheckbox(8, LocaleController.getString(this.isMegagroup ? R.string.EventLogFilterGroupInfo : R.string.EventLogFilterChannelInfo)).pad();
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter6 = this.currentFilter;
            arrayList.add(pad4.setChecked(tL_channelAdminLogEventsFilter6.info || tL_channelAdminLogEventsFilter6.settings));
            arrayList.add(UItem.asRoundCheckbox(9, LocaleController.getString(R.string.EventLogFilterInvites)).pad().setChecked(this.currentFilter.invites));
            arrayList.add(UItem.asRoundCheckbox(10, LocaleController.getString(R.string.EventLogFilterCalls)).pad().setChecked(this.currentFilter.group_call));
        }
        UItem asRoundGroupCheckbox3 = UItem.asRoundGroupCheckbox(11, LocaleController.getString(R.string.EventLogFilterSectionMessages), getGroupCount(2));
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter7 = this.currentFilter;
        arrayList.add(asRoundGroupCheckbox3.setChecked(tL_channelAdminLogEventsFilter7.delete || tL_channelAdminLogEventsFilter7.edit || tL_channelAdminLogEventsFilter7.pinned).setCollapsed(!this.sectionMessagesExpanded).setClickCallback(getGroupClick(2)));
        if (this.sectionMessagesExpanded) {
            arrayList.add(UItem.asRoundCheckbox(12, LocaleController.getString(R.string.EventLogFilterDeletedMessages)).pad().setChecked(this.currentFilter.delete));
            arrayList.add(UItem.asRoundCheckbox(13, LocaleController.getString(R.string.EventLogFilterEditedMessages)).pad().setChecked(this.currentFilter.edit));
            arrayList.add(UItem.asRoundCheckbox(14, LocaleController.getString(R.string.EventLogFilterPinnedMessages)).pad().setChecked(this.currentFilter.pinned));
        }
        arrayList.add(UItem.asShadow(null));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EventLogFilterByAdmins)));
        UItem asRoundCheckbox = UItem.asRoundCheckbox(15, LocaleController.getString(R.string.EventLogFilterByAdminsAll));
        LongSparseArray longSparseArray = this.selectedAdmins;
        int size = longSparseArray == null ? 0 : longSparseArray.size();
        ArrayList arrayList2 = this.currentAdmins;
        arrayList.add(asRoundCheckbox.setChecked(size >= (arrayList2 == null ? 0 : arrayList2.size())));
        if (this.currentAdmins != null) {
            for (int i = 0; i < this.currentAdmins.size(); i++) {
                long peerDialogId = DialogObject.getPeerDialogId(((TLRPC.ChannelParticipant) this.currentAdmins.get(i)).peer);
                UItem pad5 = UItem.asUserCheckbox((-1) - i, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerDialogId))).pad();
                LongSparseArray longSparseArray2 = this.selectedAdmins;
                arrayList.add(pad5.setChecked(longSparseArray2 != null && longSparseArray2.containsKey(peerDialogId)));
            }
        }
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.EventLog);
    }

    public void onClick(org.telegram.ui.Components.UItem r7, android.view.View r8, float r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AdminLogFilterAlert2.onClick(org.telegram.ui.Components.UItem, android.view.View, float):void");
    }

    @Override
    public void onSmoothContainerViewLayout(float f) {
        super.onSmoothContainerViewLayout(f);
        this.buttonContainer.setTranslationY(-f);
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    public void setCurrentAdmins(ArrayList arrayList) {
        this.currentAdmins = arrayList;
        if (arrayList != null && this.selectedAdmins == null) {
            this.selectedAdmins = new LongSparseArray();
            Iterator it = this.currentAdmins.iterator();
            while (it.hasNext()) {
                long peerDialogId = DialogObject.getPeerDialogId(((TLRPC.ChannelParticipant) it.next()).peer);
                this.selectedAdmins.put(peerDialogId, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerDialogId)));
            }
        }
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter != null) {
            universalAdapter.update(true);
        }
    }
}
