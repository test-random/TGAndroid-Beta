package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private boolean accountsShown;
    private SideMenultItemAnimator itemAnimator;
    private Context mContext;
    private DrawerLayoutContainer mDrawerLayoutContainer;
    private View.OnClickListener onPremiumDrawableClick;
    public DrawerProfileCell profileCell;
    private ArrayList items = new ArrayList(11);
    private ArrayList accountNumbers = new ArrayList();

    public static class Item {
        TLRPC.TL_attachMenuBot bot;
        public boolean error;
        public int icon;
        public int id;
        View.OnClickListener listener;
        public CharSequence text;

        public Item(int i, CharSequence charSequence, int i2) {
            this.icon = i2;
            this.id = i;
            this.text = charSequence;
        }

        public Item(TLRPC.TL_attachMenuBot tL_attachMenuBot) {
            this.bot = tL_attachMenuBot;
            this.id = (int) ((tL_attachMenuBot.bot_id >> 16) + 100);
        }

        public void bind(DrawerActionCell drawerActionCell) {
            TLRPC.TL_attachMenuBot tL_attachMenuBot = this.bot;
            if (tL_attachMenuBot != null) {
                drawerActionCell.setBot(tL_attachMenuBot);
            } else {
                drawerActionCell.setTextAndIcon(this.id, this.text, this.icon);
            }
            drawerActionCell.setError(this.error);
        }
    }

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator sideMenultItemAnimator, DrawerLayoutContainer drawerLayoutContainer) {
        this.mContext = context;
        this.mDrawerLayoutContainer = drawerLayoutContainer;
        this.itemAnimator = sideMenultItemAnimator;
        this.accountsShown = UserConfig.getActivatedAccountsCount() > 1 && MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true);
        Theme.createCommonDialogResources(context);
        resetItems();
    }

    private int getAccountRowsCount() {
        int size = this.accountNumbers.size();
        return this.accountNumbers.size() < 4 ? size + 2 : size + 1;
    }

    public static int lambda$resetItems$0(Integer num, Integer num2) {
        long j = UserConfig.getInstance(num.intValue()).loginTime;
        long j2 = UserConfig.getInstance(num2.intValue()).loginTime;
        if (j > j2) {
            return 1;
        }
        return j < j2 ? -1 : 0;
    }

    private void resetItems() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        ArrayList arrayList;
        Item item;
        this.accountNumbers.clear();
        for (int i8 = 0; i8 < 4; i8++) {
            if (UserConfig.getInstance(i8).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(i8));
            }
        }
        Collections.sort(this.accountNumbers, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$resetItems$0;
                lambda$resetItems$0 = DrawerLayoutAdapter.lambda$resetItems$0((Integer) obj, (Integer) obj2);
                return lambda$resetItems$0;
            }
        });
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            int eventType = Theme.getEventType();
            if (eventType == 0) {
                i = R.drawable.msg_groups_ny;
                i2 = R.drawable.msg_contacts_ny;
                i3 = R.drawable.msg_calls_ny;
                i4 = R.drawable.msg_saved_ny;
                i5 = R.drawable.msg_settings_ny;
                i6 = R.drawable.msg_invite_ny;
                i7 = R.drawable.msg_help_ny;
            } else {
                if (eventType == 1) {
                    i = R.drawable.msg_groups_14;
                    i2 = R.drawable.msg_contacts_14;
                    i3 = R.drawable.msg_calls_14;
                    i4 = R.drawable.msg_saved_14;
                    i5 = R.drawable.msg_settings_14;
                    i6 = R.drawable.msg_secret_ny;
                } else if (eventType == 2) {
                    i = R.drawable.msg_groups_hw;
                    i2 = R.drawable.msg_contacts_hw;
                    i3 = R.drawable.msg_calls_hw;
                    i4 = R.drawable.msg_saved_hw;
                    i5 = R.drawable.msg_settings_hw;
                    i6 = R.drawable.msg_invite_hw;
                    i7 = R.drawable.msg_help_hw;
                } else {
                    i = R.drawable.msg_groups;
                    i2 = R.drawable.msg_contacts;
                    i3 = R.drawable.msg_calls;
                    i4 = R.drawable.msg_saved;
                    i5 = R.drawable.msg_settings_old;
                    i6 = R.drawable.msg_invite;
                }
                i7 = R.drawable.msg_help;
            }
            UserConfig userConfig = UserConfig.getInstance(UserConfig.selectedAccount);
            this.items.add(new Item(16, LocaleController.getString(R.string.MyProfile), R.drawable.left_status_profile));
            if (userConfig != null && userConfig.isPremium()) {
                if (userConfig.getEmojiStatus() != null) {
                    arrayList = this.items;
                    item = new Item(15, LocaleController.getString(R.string.ChangeEmojiStatus), R.drawable.msg_status_edit);
                } else {
                    arrayList = this.items;
                    item = new Item(15, LocaleController.getString(R.string.SetEmojiStatus), R.drawable.msg_status_set);
                }
                arrayList.add(item);
            }
            ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
            if (applicationLoader != null) {
                applicationLoader.extendDrawer(this.items);
            }
            TLRPC.TL_attachMenuBots attachMenuBots = MediaDataController.getInstance(UserConfig.selectedAccount).getAttachMenuBots();
            if (attachMenuBots != null && attachMenuBots.bots != null) {
                for (int i9 = 0; i9 < attachMenuBots.bots.size(); i9++) {
                    TLRPC.TL_attachMenuBot tL_attachMenuBot = attachMenuBots.bots.get(i9);
                    if (tL_attachMenuBot.show_in_side_menu) {
                        this.items.add(new Item(tL_attachMenuBot));
                    }
                }
            }
            this.items.add(null);
            this.items.add(new Item(2, LocaleController.getString(R.string.NewGroup), i));
            this.items.add(new Item(6, LocaleController.getString(R.string.Contacts), i2));
            this.items.add(new Item(10, LocaleController.getString(R.string.Calls), i3));
            this.items.add(new Item(11, LocaleController.getString(R.string.SavedMessages), i4));
            this.items.add(new Item(8, LocaleController.getString(R.string.Settings), i5));
            this.items.add(null);
            this.items.add(new Item(7, LocaleController.getString(R.string.InviteFriends), i6));
            this.items.add(new Item(13, LocaleController.getString(R.string.TelegramFeatures), i7));
        }
    }

    public boolean click(View view, int i) {
        Item item;
        View.OnClickListener onClickListener;
        int i2 = i - 2;
        if (this.accountsShown) {
            i2 -= getAccountRowsCount();
        }
        if (i2 < 0 || i2 >= this.items.size() || (item = (Item) this.items.get(i2)) == null || (onClickListener = item.listener) == null) {
            return false;
        }
        onClickListener.onClick(view);
        return true;
    }

    public TLRPC.TL_attachMenuBot getAttachMenuBot(int i) {
        Item item;
        int i2 = i - 2;
        if (this.accountsShown) {
            i2 -= getAccountRowsCount();
        }
        if (i2 < 0 || i2 >= this.items.size() || (item = (Item) this.items.get(i2)) == null) {
            return null;
        }
        return item.bot;
    }

    public int getFirstAccountPosition() {
        return !this.accountsShown ? -1 : 2;
    }

    public int getId(int i) {
        Item item;
        int i2 = i - 2;
        if (this.accountsShown) {
            i2 -= getAccountRowsCount();
        }
        if (i2 < 0 || i2 >= this.items.size() || (item = (Item) this.items.get(i2)) == null) {
            return -1;
        }
        return item.id;
    }

    @Override
    public int getItemCount() {
        int size = this.items.size() + 2;
        return this.accountsShown ? size + getAccountRowsCount() : size;
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        int i2 = i - 2;
        if (this.accountsShown) {
            if (i2 < this.accountNumbers.size()) {
                return 4;
            }
            if (this.accountNumbers.size() < 4) {
                if (i2 == this.accountNumbers.size()) {
                    return 5;
                }
                if (i2 == this.accountNumbers.size() + 1) {
                    return 2;
                }
            } else if (i2 == this.accountNumbers.size()) {
                return 2;
            }
            i2 -= getAccountRowsCount();
        }
        return (i2 < 0 || i2 >= this.items.size() || this.items.get(i2) == null) ? 2 : 3;
    }

    public int getLastAccountPosition() {
        if (this.accountsShown) {
            return this.accountNumbers.size() + 1;
        }
        return -1;
    }

    public boolean isAccountsShown() {
        return this.accountsShown;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 3 || itemViewType == 4 || itemViewType == 5 || itemViewType == 6;
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            ((DrawerProfileCell) viewHolder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShown);
            return;
        }
        if (itemViewType != 3) {
            if (itemViewType != 4) {
                return;
            }
            ((DrawerUserCell) viewHolder.itemView).setAccount(((Integer) this.accountNumbers.get(i - 2)).intValue());
        } else {
            DrawerActionCell drawerActionCell = (DrawerActionCell) viewHolder.itemView;
            int i2 = i - 2;
            if (this.accountsShown) {
                i2 -= getAccountRowsCount();
            }
            ((Item) this.items.get(i2)).bind(drawerActionCell);
            drawerActionCell.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i != 0) {
            view = i != 2 ? i != 3 ? i != 4 ? i != 5 ? new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f)) : new DrawerAddCell(this.mContext) : new DrawerUserCell(this.mContext) : new DrawerActionCell(this.mContext) : new DividerCell(this.mContext);
        } else {
            DrawerProfileCell drawerProfileCell = new DrawerProfileCell(this.mContext, this.mDrawerLayoutContainer) {
                @Override
                protected void onPremiumClick() {
                    if (DrawerLayoutAdapter.this.onPremiumDrawableClick != null) {
                        DrawerLayoutAdapter.this.onPremiumDrawableClick.onClick(this);
                    }
                }
            };
            this.profileCell = drawerProfileCell;
            view = drawerProfileCell;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    public void setAccountsShown(boolean z, boolean z2) {
        if (this.accountsShown == z || this.itemAnimator.isRunning()) {
            return;
        }
        this.accountsShown = z;
        DrawerProfileCell drawerProfileCell = this.profileCell;
        if (drawerProfileCell != null) {
            drawerProfileCell.setAccountsShown(z, z2);
        }
        MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", this.accountsShown).commit();
        if (!z2) {
            notifyDataSetChanged();
            return;
        }
        this.itemAnimator.setShouldClipChildren(false);
        if (this.accountsShown) {
            notifyItemRangeInserted(2, getAccountRowsCount());
        } else {
            notifyItemRangeRemoved(2, getAccountRowsCount());
        }
    }

    public void setOnPremiumDrawableClick(View.OnClickListener onClickListener) {
        this.onPremiumDrawableClick = onClickListener;
    }

    public void swapElements(int i, int i2) {
        int i3 = i - 2;
        int i4 = i2 - 2;
        if (i3 < 0 || i4 < 0 || i3 >= this.accountNumbers.size() || i4 >= this.accountNumbers.size()) {
            return;
        }
        UserConfig userConfig = UserConfig.getInstance(((Integer) this.accountNumbers.get(i3)).intValue());
        UserConfig userConfig2 = UserConfig.getInstance(((Integer) this.accountNumbers.get(i4)).intValue());
        int i5 = userConfig.loginTime;
        userConfig.loginTime = userConfig2.loginTime;
        userConfig2.loginTime = i5;
        userConfig.saveConfig(false);
        userConfig2.saveConfig(false);
        Collections.swap(this.accountNumbers, i3, i4);
        notifyItemMoved(i, i2);
    }
}
