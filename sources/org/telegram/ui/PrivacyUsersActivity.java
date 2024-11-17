package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PrivacyUsersActivity;

public class PrivacyUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate {
    private int blockUserDetailRow;
    private int blockUserRow;
    private int deleteAllRow;
    private EmptyTextProgressView emptyView;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int rowCount;
    public int rulesType;
    private ArrayList uidArray;
    private int usersDetailRow;
    private int usersEndRow;
    private int usersHeaderRow;
    private int usersStartRow;
    private int currentType = 1;
    private boolean blockedUsersActivity = true;

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean lambda$onCreateViewHolder$0(ManageChatUserCell manageChatUserCell, boolean z) {
            if (!z) {
                return true;
            }
            PrivacyUsersActivity.this.showUnblockAlert((Long) manageChatUserCell.getTag(), manageChatUserCell);
            return true;
        }

        @Override
        public int getItemCount() {
            return PrivacyUsersActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == PrivacyUsersActivity.this.deleteAllRow) {
                return 4;
            }
            if (i == PrivacyUsersActivity.this.usersHeaderRow) {
                return 3;
            }
            if (i == PrivacyUsersActivity.this.blockUserRow) {
                return 2;
            }
            return (i == PrivacyUsersActivity.this.blockUserDetailRow || i == PrivacyUsersActivity.this.usersDetailRow) ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2 || itemViewType == 4;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 0) {
                ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 7, 6, true);
                manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                    @Override
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                        boolean lambda$onCreateViewHolder$0;
                        lambda$onCreateViewHolder$0 = PrivacyUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$0(manageChatUserCell2, z);
                        return lambda$onCreateViewHolder$0;
                    }
                });
                frameLayout = manageChatUserCell;
            } else if (i == 1) {
                frameLayout = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 2) {
                FrameLayout manageChatTextCell = new ManageChatTextCell(this.mContext);
                manageChatTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                frameLayout = manageChatTextCell;
            } else if (i != 4) {
                HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 11, false);
                headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                headerCell.setHeight(43);
                frameLayout = headerCell;
            } else {
                TextCell textCell = new TextCell(viewGroup.getContext());
                textCell.setText(LocaleController.getString(R.string.NotificationsDeleteAllException), false);
                textCell.setColors(-1, Theme.key_text_RedRegular);
                textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                frameLayout = textCell;
            }
            return new RecyclerListView.Holder(frameLayout);
        }
    }

    public Integer lambda$createView$0(Integer num) {
        if (num.intValue() == this.deleteAllRow) {
            return Integer.valueOf(Theme.multAlpha(Theme.getColor(Theme.key_text_RedRegular), 0.12f));
        }
        return null;
    }

    public void lambda$createView$1() {
        this.uidArray.clear();
        updateRows();
        lambda$onBackPressed$321();
    }

    public void lambda$createView$2(boolean z, boolean z2, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Long l = (Long) it.next();
            if (!this.uidArray.contains(l)) {
                this.uidArray.add(l);
            }
        }
        updateRows();
    }

    public void lambda$createView$3(View view, int i) {
        ProfileActivity profileActivity;
        String str;
        if (i == this.deleteAllRow) {
            AlertDialog create = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.NotificationsDeleteAllExceptionTitle), LocaleController.getString(R.string.NotificationsDeleteAllExceptionAlert), LocaleController.getString(R.string.Delete), new Runnable() {
                @Override
                public final void run() {
                    PrivacyUsersActivity.this.lambda$createView$1();
                }
            }, null).create();
            create.show();
            create.redPositive();
            return;
        }
        if (i == this.blockUserRow) {
            if (this.currentType == 1) {
                presentFragment(new DialogOrContactPickerActivity());
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
            if (this.isGroup) {
                bundle.putInt("chatAddType", 1);
            } else if (this.currentType == 2) {
                bundle.putInt("chatAddType", 2);
            }
            if (!this.isAlwaysShare || this.rulesType != 1) {
                str = this.rulesType == 12 ? "allowMiniapps" : "allowPremium";
                GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
                groupCreateActivity.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate() {
                    @Override
                    public final void didSelectUsers(boolean z, boolean z2, ArrayList arrayList) {
                        PrivacyUsersActivity.this.lambda$createView$2(z, z2, arrayList);
                    }
                });
                profileActivity = groupCreateActivity;
            }
            bundle.putBoolean(str, true);
            GroupCreateActivity groupCreateActivity2 = new GroupCreateActivity(bundle);
            groupCreateActivity2.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate() {
                @Override
                public final void didSelectUsers(boolean z, boolean z2, ArrayList arrayList) {
                    PrivacyUsersActivity.this.lambda$createView$2(z, z2, arrayList);
                }
            });
            profileActivity = groupCreateActivity2;
        } else {
            if (i < this.usersStartRow || i >= this.usersEndRow) {
                return;
            }
            if (this.currentType == 1) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("user_id", getMessagesController().blockePeers.keyAt(i - this.usersStartRow));
                profileActivity = new ProfileActivity(bundle2);
            } else {
                Bundle bundle3 = new Bundle();
                long longValue = ((Long) this.uidArray.get(i - this.usersStartRow)).longValue();
                if (DialogObject.isUserDialog(longValue)) {
                    bundle3.putLong("user_id", longValue);
                } else {
                    bundle3.putLong("chat_id", -longValue);
                }
                profileActivity = new ProfileActivity(bundle3);
            }
        }
        presentFragment(profileActivity);
    }

    public boolean lambda$createView$4(View view, int i) {
        int i2 = this.usersStartRow;
        if (i < i2 || i >= this.usersEndRow) {
            return false;
        }
        showUnblockAlert(this.currentType == 1 ? Long.valueOf(getMessagesController().blockePeers.keyAt(i - this.usersStartRow)) : (Long) this.uidArray.get(i - i2), view);
        return true;
    }

    public void lambda$getThemeDescriptions$7() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }

    public void lambda$showUnblockAlert$5(Long l) {
        getMessagesController().unblockPeer(l.longValue());
    }

    public void lambda$showUnblockAlert$6(Long l) {
        this.uidArray.remove(l);
        updateRows();
        if (this.uidArray.isEmpty()) {
            lambda$onBackPressed$321();
        }
    }

    public void showUnblockAlert(final Long l, View view) {
        if (getParentActivity() == null) {
            return;
        }
        ItemOptions addIf = ItemOptions.makeOptions(this, view).setScrimViewBackground(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite))).addIf(this.currentType == 1, 0, LocaleController.getString(R.string.Unblock), new Runnable() {
            @Override
            public final void run() {
                PrivacyUsersActivity.this.lambda$showUnblockAlert$5(l);
            }
        });
        int i = this.currentType;
        addIf.addIf(i != 1, i == 0 ? R.drawable.msg_user_remove : 0, (CharSequence) LocaleController.getString(R.string.Remove), true, new Runnable() {
            @Override
            public final void run() {
                PrivacyUsersActivity.this.lambda$showUnblockAlert$6(l);
            }
        }).setMinWidth(190).show();
    }

    private void updateRows() {
        this.rowCount = 0;
        this.usersHeaderRow = -1;
        this.blockUserDetailRow = -1;
        this.deleteAllRow = -1;
        if (!this.blockedUsersActivity || getMessagesController().totalBlockedCount >= 0) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.blockUserRow = i;
            int i3 = this.currentType;
            if (i3 == 1) {
                this.rowCount = i + 2;
                this.blockUserDetailRow = i2;
            }
            int size = i3 == 1 ? getMessagesController().blockePeers.size() : this.uidArray.size();
            if (size != 0) {
                int i4 = this.currentType;
                if (i4 == 1) {
                    int i5 = this.rowCount;
                    this.rowCount = i5 + 1;
                    this.usersHeaderRow = i5;
                }
                int i6 = this.rowCount;
                this.usersStartRow = i6;
                int i7 = i6 + size;
                this.usersEndRow = i7;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.usersDetailRow = i7;
                if (i4 != 1) {
                    this.rowCount = i7 + 2;
                    this.deleteAllRow = i8;
                }
            } else {
                this.usersHeaderRow = -1;
                this.usersStartRow = -1;
                this.usersEndRow = -1;
                this.usersDetailRow = -1;
                this.deleteAllRow = -1;
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateVisibleRows(int i) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof ManageChatUserCell) {
                ((ManageChatUserCell) childAt).update(i);
            }
        }
    }

    @Override
    public View createView(Context context) {
        ActionBar actionBar;
        int i;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.currentType;
        if (i2 == 1) {
            actionBar = this.actionBar;
            i = R.string.BlockedUsers;
        } else if (i2 == 2) {
            if (this.isAlwaysShare) {
                actionBar = this.actionBar;
                i = R.string.FilterAlwaysShow;
            } else {
                actionBar = this.actionBar;
                i = R.string.FilterNeverShow;
            }
        } else if (this.isGroup) {
            if (this.isAlwaysShare) {
                actionBar = this.actionBar;
                i = R.string.AlwaysAllow;
            } else {
                actionBar = this.actionBar;
                i = R.string.NeverAllow;
            }
        } else if (this.isAlwaysShare) {
            actionBar = this.actionBar;
            i = R.string.AlwaysShareWithTitle;
        } else {
            actionBar = this.actionBar;
            i = R.string.NeverShareWithTitle;
        }
        actionBar.setTitle(LocaleController.getString(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    PrivacyUsersActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString(this.currentType == 1 ? R.string.NoBlocked : R.string.NoContacts));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemSelectorColorProvider(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Integer lambda$createView$0;
                lambda$createView$0 = PrivacyUsersActivity.this.lambda$createView$0((Integer) obj);
                return lambda$createView$0;
            }
        });
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                PrivacyUsersActivity.this.lambda$createView$3(view, i3);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i3) {
                boolean lambda$createView$4;
                lambda$createView$4 = PrivacyUsersActivity.this.lambda$createView$4(view, i3);
                return lambda$createView$4;
            }
        });
        if (this.currentType == 1) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                    if (PrivacyUsersActivity.this.getMessagesController().blockedEndReached) {
                        return;
                    }
                    int abs = Math.abs(PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() - PrivacyUsersActivity.this.layoutManager.findFirstVisibleItemPosition()) + 1;
                    int itemCount = recyclerView.getAdapter().getItemCount();
                    if (abs <= 0 || PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() < itemCount - 10) {
                        return;
                    }
                    PrivacyUsersActivity.this.getMessagesController().getBlockedPeers(false);
                }
            });
            if (getMessagesController().totalBlockedCount < 0) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
        updateRows();
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.updateInterfaces) {
            if (i == NotificationCenter.blockedUsersDidLoad) {
                this.emptyView.showTextView();
                updateRows();
                return;
            }
            return;
        }
        int intValue = ((Integer) objArr[0]).intValue();
        if ((MessagesController.UPDATE_MASK_AVATAR & intValue) == 0 && (MessagesController.UPDATE_MASK_NAME & intValue) == 0) {
            return;
        }
        updateVisibleRows(intValue);
    }

    @Override
    public void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
        if (user == null) {
            return;
        }
        getMessagesController().blockPeer(user.id);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                PrivacyUsersActivity.this.lambda$getThemeDescriptions$7();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        return arrayList;
    }

    public PrivacyUsersActivity loadBlocked() {
        getMessagesController().getBlockedPeers(true);
        return this;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        if (this.currentType == 1) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        if (this.currentType == 1) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
