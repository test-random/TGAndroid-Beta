package org.telegram.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;

public class ArchiveSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private RecyclerListView listView;
    private TLRPC.TL_globalPrivacySettings settings;
    private boolean changed = false;
    private int shiftDp = -3;
    private final ArrayList oldItems = new ArrayList();
    private final ArrayList items = new ArrayList();

    public static class ItemInner extends AdapterWithDiffUtils.Item {
        public int id;
        public CharSequence text;

        public ItemInner(int i, int i2, CharSequence charSequence) {
            super(i, false);
            this.id = i2;
            this.text = charSequence;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ItemInner itemInner = (ItemInner) obj;
            return this.id == itemInner.id && Objects.equals(this.text, itemInner.text);
        }
    }

    public class ListAdapter extends AdapterWithDiffUtils {
        private ListAdapter() {
        }

        @Override
        public int getItemCount() {
            return ArchiveSettingsActivity.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            if (i < 0 || i >= ArchiveSettingsActivity.this.items.size()) {
                return 0;
            }
            return ((ItemInner) ArchiveSettingsActivity.this.items.get(i)).viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return (viewHolder.getItemViewType() == 2 || viewHolder.getItemViewType() == 0) ? false : true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            boolean z;
            CharSequence charSequence;
            Context context;
            int i2;
            if (i < 0 || i >= ArchiveSettingsActivity.this.items.size()) {
                return;
            }
            ItemInner itemInner = (ItemInner) ArchiveSettingsActivity.this.items.get(i);
            int i3 = i + 1;
            int i4 = 0;
            boolean z2 = i3 < ArchiveSettingsActivity.this.items.size() && ((ItemInner) ArchiveSettingsActivity.this.items.get(i3)).viewType == itemInner.viewType;
            if (viewHolder.getItemViewType() == 0) {
                ((HeaderCell) viewHolder.itemView).setText(itemInner.text);
                return;
            }
            if (viewHolder.getItemViewType() == 2) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (TextUtils.isEmpty(itemInner.text)) {
                    textInfoPrivacyCell.setFixedSize(12);
                    charSequence = null;
                } else {
                    textInfoPrivacyCell.setFixedSize(0);
                    charSequence = itemInner.text;
                }
                textInfoPrivacyCell.setText(charSequence);
                if (z2) {
                    context = ArchiveSettingsActivity.this.getContext();
                    i2 = R.drawable.greydivider_bottom;
                } else {
                    context = ArchiveSettingsActivity.this.getContext();
                    i2 = R.drawable.greydivider;
                }
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(context, i2, Theme.key_windowBackgroundGrayShadow));
                return;
            }
            if (viewHolder.getItemViewType() == 1) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                int i5 = itemInner.id;
                if (i5 == 1) {
                    z = ArchiveSettingsActivity.this.settings.keep_archived_unmuted;
                } else if (i5 == 4) {
                    z = ArchiveSettingsActivity.this.settings.keep_archived_folders;
                } else {
                    if (i5 != 7) {
                        return;
                    }
                    z = ArchiveSettingsActivity.this.settings.archive_and_mute_new_noncontact_peers;
                    if (!ArchiveSettingsActivity.this.getUserConfig().isPremium() && !ArchiveSettingsActivity.this.getMessagesController().autoarchiveAvailable) {
                        i4 = R.drawable.permission_locked;
                    }
                }
                textCheckCell.setCheckBoxIcon(i4);
                textCheckCell.setTextAndCheck(itemInner.text, z, z2);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoPrivacyCell;
            if (i == 0) {
                textInfoPrivacyCell = new HeaderCell(ArchiveSettingsActivity.this.getContext());
            } else {
                if (i != 1) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(ArchiveSettingsActivity.this.getContext());
                    return new RecyclerListView.Holder(textInfoPrivacyCell);
                }
                textInfoPrivacyCell = new TextCheckCell(ArchiveSettingsActivity.this.getContext());
            }
            textInfoPrivacyCell.setBackgroundColor(ArchiveSettingsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(textInfoPrivacyCell);
        }
    }

    public void lambda$createView$0() {
        presentFragment(new PremiumPreviewFragment("settings"));
    }

    public void lambda$createView$1(View view, int i) {
        boolean z;
        if (i < 0 || i >= this.items.size()) {
            return;
        }
        int i2 = ((ItemInner) this.items.get(i)).id;
        if (i2 == 1) {
            TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings = this.settings;
            z = !tL_globalPrivacySettings.keep_archived_unmuted;
            tL_globalPrivacySettings.keep_archived_unmuted = z;
        } else if (i2 == 4) {
            TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings2 = this.settings;
            z = !tL_globalPrivacySettings2.keep_archived_folders;
            tL_globalPrivacySettings2.keep_archived_folders = z;
        } else {
            if (i2 != 7) {
                return;
            }
            if (!getUserConfig().isPremium() && !getMessagesController().autoarchiveAvailable && !this.settings.archive_and_mute_new_noncontact_peers) {
                Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(getContext(), getResourceProvider());
                simpleLayout.textView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.UnlockPremium), Theme.key_undo_cancelColor, 0, new Runnable() {
                    @Override
                    public final void run() {
                        ArchiveSettingsActivity.this.lambda$createView$0();
                    }
                }));
                simpleLayout.textView.setSingleLine(false);
                simpleLayout.textView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
                simpleLayout.imageView.setImageResource(R.drawable.msg_settings_premium);
                Bulletin.make(this, simpleLayout, 3500).show();
                int i3 = -this.shiftDp;
                this.shiftDp = i3;
                AndroidUtilities.shakeViewSpring(view, i3);
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                return;
            }
            TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings3 = this.settings;
            z = !tL_globalPrivacySettings3.archive_and_mute_new_noncontact_peers;
            tL_globalPrivacySettings3.archive_and_mute_new_noncontact_peers = z;
        }
        ((TextCheckCell) view).setChecked(z);
        this.changed = true;
    }

    public static void lambda$onFragmentDestroy$2(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    private void updateItems(boolean z) {
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        this.items.add(new ItemInner(0, 0, LocaleController.getString("ArchiveSettingUnmutedFolders")));
        this.items.add(new ItemInner(1, 1, LocaleController.getString("ArchiveSettingUnmutedFoldersCheck")));
        this.items.add(new ItemInner(2, 2, LocaleController.getString("ArchiveSettingUnmutedFoldersInfo")));
        if (getMessagesController().getDialogFilters().size() > 1) {
            this.items.add(new ItemInner(0, 3, LocaleController.getString("ArchiveSettingUnmutedChats")));
            this.items.add(new ItemInner(1, 4, LocaleController.getString("ArchiveSettingUnmutedChatsCheck")));
            this.items.add(new ItemInner(2, 5, LocaleController.getString("ArchiveSettingUnmutedChatsInfo")));
        }
        this.items.add(new ItemInner(0, 6, LocaleController.getString("NewChatsFromNonContacts")));
        this.items.add(new ItemInner(1, 7, LocaleController.getString("NewChatsFromNonContactsCheck")));
        this.items.add(new ItemInner(2, 8, LocaleController.getString("ArchiveAndMuteInfo")));
        ListAdapter listAdapter = this.adapter;
        if (listAdapter == null) {
            return;
        }
        if (z) {
            listAdapter.setItems(this.oldItems, this.items);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.ArchiveSettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    ArchiveSettingsActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter();
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                ArchiveSettingsActivity.this.lambda$createView$1(view, i);
            }
        });
        getContactsController().loadGlobalPrivacySetting();
        TLRPC.TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        this.settings = globalPrivacySettings;
        if (globalPrivacySettings == null) {
            this.settings = new TLRPC.TL_globalPrivacySettings();
        }
        updateItems(false);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TextCheckCell textCheckCell;
        boolean z;
        if (i != NotificationCenter.privacyRulesUpdated) {
            if (i == NotificationCenter.dialogFiltersUpdated) {
                updateItems(true);
                return;
            }
            return;
        }
        TLRPC.TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        this.settings = globalPrivacySettings;
        if (globalPrivacySettings == null) {
            this.settings = new TLRPC.TL_globalPrivacySettings();
        }
        if (this.listView != null) {
            for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
                View childAt = this.listView.getChildAt(i3);
                int childAdapterPosition = this.listView.getChildAdapterPosition(childAt);
                if (childAdapterPosition >= 0 && childAdapterPosition < this.items.size()) {
                    int i4 = ((ItemInner) this.items.get(childAdapterPosition)).id;
                    if (i4 == 1) {
                        textCheckCell = (TextCheckCell) childAt;
                        z = this.settings.keep_archived_unmuted;
                    } else if (i4 == 4) {
                        textCheckCell = (TextCheckCell) childAt;
                        z = this.settings.keep_archived_folders;
                    } else if (i4 == 7) {
                        textCheckCell = (TextCheckCell) childAt;
                        z = this.settings.archive_and_mute_new_noncontact_peers;
                    }
                    textCheckCell.setChecked(z);
                }
            }
        }
        this.changed = false;
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        super.onFragmentDestroy();
        if (this.changed) {
            TLRPC.TL_account_setGlobalPrivacySettings tL_account_setGlobalPrivacySettings = new TLRPC.TL_account_setGlobalPrivacySettings();
            tL_account_setGlobalPrivacySettings.settings = this.settings;
            getConnectionsManager().sendRequest(tL_account_setGlobalPrivacySettings, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ArchiveSettingsActivity.lambda$onFragmentDestroy$2(tLObject, tL_error);
                }
            });
            this.changed = false;
        }
    }
}
