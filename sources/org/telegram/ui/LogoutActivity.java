package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;

public class LogoutActivity extends BaseFragment {
    private int addAccountRow;
    private int alternativeHeaderRow;
    private int alternativeSectionRow;
    private int cacheRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int logoutRow;
    private int logoutSectionRow;
    private int passcodeRow;
    private int phoneRow;
    private int rowCount;
    private int supportRow;

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            return LogoutActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == LogoutActivity.this.alternativeHeaderRow) {
                return 0;
            }
            if (i == LogoutActivity.this.addAccountRow || i == LogoutActivity.this.passcodeRow || i == LogoutActivity.this.cacheRow || i == LogoutActivity.this.phoneRow || i == LogoutActivity.this.supportRow) {
                return 1;
            }
            if (i == LogoutActivity.this.alternativeSectionRow) {
                return 2;
            }
            return i == LogoutActivity.this.logoutRow ? 3 : 4;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == LogoutActivity.this.addAccountRow || adapterPosition == LogoutActivity.this.passcodeRow || adapterPosition == LogoutActivity.this.cacheRow || adapterPosition == LogoutActivity.this.phoneRow || adapterPosition == LogoutActivity.this.supportRow || adapterPosition == LogoutActivity.this.logoutRow;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String string;
            String string2;
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == LogoutActivity.this.alternativeHeaderRow) {
                    headerCell.setText(LocaleController.getString(R.string.AlternativeOptions));
                    return;
                }
                return;
            }
            if (itemViewType != 1) {
                if (itemViewType == 3) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == LogoutActivity.this.logoutRow) {
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                        textSettingsCell.setText(LocaleController.getString(R.string.LogOutTitle), false);
                        return;
                    }
                    return;
                }
                if (itemViewType != 4) {
                    return;
                }
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == LogoutActivity.this.logoutSectionRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.LogOutInfo));
                    return;
                }
                return;
            }
            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
            if (i == LogoutActivity.this.addAccountRow) {
                string = LocaleController.getString(R.string.AddAnotherAccount);
                string2 = LocaleController.getString(R.string.AddAnotherAccountInfo);
                i2 = R.drawable.msg_contact_add;
            } else if (i == LogoutActivity.this.passcodeRow) {
                string = LocaleController.getString(R.string.SetPasscode);
                string2 = LocaleController.getString(R.string.SetPasscodeInfo);
                i2 = R.drawable.msg_permissions;
            } else if (i == LogoutActivity.this.cacheRow) {
                string = LocaleController.getString(R.string.ClearCache);
                string2 = LocaleController.getString(R.string.ClearCacheInfo);
                i2 = R.drawable.msg_clearcache;
            } else if (i != LogoutActivity.this.phoneRow) {
                if (i == LogoutActivity.this.supportRow) {
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString(R.string.ContactSupport), LocaleController.getString(R.string.ContactSupportInfo), R.drawable.msg_help, false);
                    return;
                }
                return;
            } else {
                string = LocaleController.getString(R.string.ChangePhoneNumber);
                string2 = LocaleController.getString(R.string.ChangePhoneNumberInfo);
                i2 = R.drawable.msg_newphone;
            }
            textDetailSettingsCell.setTextAndValueAndIcon(string, string2, i2, true);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            if (i != 0) {
                if (i == 1) {
                    TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(this.mContext);
                    textDetailSettingsCell.setMultilineDetail(true);
                    textDetailSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    headerCell = textDetailSettingsCell;
                } else if (i == 2) {
                    headerCell = new ShadowSectionCell(this.mContext);
                } else if (i != 3) {
                    headerCell = new TextInfoPrivacyCell(this.mContext);
                    headerCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else {
                    headerCell = new TextSettingsCell(this.mContext);
                }
                headerCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(headerCell);
            }
            headerCell = new HeaderCell(this.mContext);
            headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            headerCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(headerCell);
        }
    }

    public void lambda$createView$0(View view, int i, float f, float f2) {
        Dialog makeLogOutDialog;
        BaseFragment actionIntroActivity;
        Integer num = null;
        if (i != this.addAccountRow) {
            if (i == this.passcodeRow) {
                actionIntroActivity = PasscodeActivity.determineOpenFragment();
            } else if (i == this.cacheRow) {
                actionIntroActivity = new CacheControlActivity();
            } else {
                if (i != this.phoneRow) {
                    if (i == this.supportRow) {
                        makeLogOutDialog = AlertsCreator.createSupportAlert(this, null);
                    } else if (i != this.logoutRow || getParentActivity() == null) {
                        return;
                    } else {
                        makeLogOutDialog = makeLogOutDialog(getParentActivity(), this.currentAccount);
                    }
                    showDialog(makeLogOutDialog);
                }
                actionIntroActivity = new ActionIntroActivity(3);
            }
            presentFragment(actionIntroActivity);
        }
        int i2 = 0;
        for (int i3 = 3; i3 >= 0; i3--) {
            if (!UserConfig.getInstance(i3).isClientActivated()) {
                i2++;
                if (num == null) {
                    num = Integer.valueOf(i3);
                }
            }
        }
        if (!UserConfig.hasPremiumOnAccounts()) {
            i2--;
        }
        if (i2 > 0 && num != null) {
            actionIntroActivity = new LoginActivity(num.intValue());
            presentFragment(actionIntroActivity);
        } else {
            if (UserConfig.hasPremiumOnAccounts()) {
                return;
            }
            makeLogOutDialog = new LimitReachedBottomSheet(this, getContext(), 7, this.currentAccount, null);
            showDialog(makeLogOutDialog);
        }
    }

    public static void lambda$makeLogOutDialog$1(int i, DialogInterface dialogInterface, int i2) {
        MessagesController.getInstance(i).performLogout(1);
    }

    public static AlertDialog makeLogOutDialog(Context context, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(LocaleController.getString(R.string.AreYouSureLogout));
        int i2 = R.string.LogOut;
        builder.setTitle(LocaleController.getString(i2));
        builder.setPositiveButton(LocaleController.getString(i2), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                LogoutActivity.lambda$makeLogOutDialog$1(i, dialogInterface, i3);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
        return create;
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString(R.string.LogOutTitle));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    LogoutActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
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
                LogoutActivity.this.lambda$createView$0(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i3 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        return arrayList;
    }

    @Override
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 1;
        this.alternativeHeaderRow = 0;
        if (UserConfig.getActivatedAccountsCount() < 4) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addAccountRow = i;
        } else {
            this.addAccountRow = -1;
        }
        if (SharedConfig.passcodeHash.length() <= 0) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.passcodeRow = i2;
        } else {
            this.passcodeRow = -1;
        }
        int i3 = this.rowCount;
        this.cacheRow = i3;
        this.phoneRow = i3 + 1;
        this.supportRow = i3 + 2;
        this.alternativeSectionRow = i3 + 3;
        this.logoutRow = i3 + 4;
        this.rowCount = i3 + 6;
        this.logoutSectionRow = i3 + 5;
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
