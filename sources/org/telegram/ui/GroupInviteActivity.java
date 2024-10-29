package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class GroupInviteActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private long chatId;
    private int copyLinkRow;
    private EmptyTextProgressView emptyView;
    private TLRPC.TL_chatInviteExported invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (GroupInviteActivity.this.loading) {
                return 0;
            }
            return GroupInviteActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (i == GroupInviteActivity.this.shadowRow || i == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            return i == GroupInviteActivity.this.linkRow ? 2 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == GroupInviteActivity.this.revokeLinkRow || adapterPosition == GroupInviteActivity.this.copyLinkRow || adapterPosition == GroupInviteActivity.this.shareLinkRow || adapterPosition == GroupInviteActivity.this.linkRow;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            Context context;
            int i3;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == GroupInviteActivity.this.copyLinkRow) {
                    i2 = R.string.CopyLink;
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    textSettingsCell.setText(LocaleController.getString(R.string.ShareLink), false);
                    return;
                } else if (i != GroupInviteActivity.this.revokeLinkRow) {
                    return;
                } else {
                    i2 = R.string.RevokeLink;
                }
                textSettingsCell.setText(LocaleController.getString(i2), true);
                return;
            }
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    return;
                }
                ((TextBlockCell) viewHolder.itemView).setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : "error", false);
                return;
            }
            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
            if (i == GroupInviteActivity.this.shadowRow) {
                textInfoPrivacyCell.setText("");
                context = this.mContext;
                i3 = R.drawable.greydivider_bottom;
            } else {
                if (i != GroupInviteActivity.this.linkInfoRow) {
                    return;
                }
                TLRPC.Chat chat = GroupInviteActivity.this.getMessagesController().getChat(Long.valueOf(GroupInviteActivity.this.chatId));
                textInfoPrivacyCell.setText(LocaleController.getString((!ChatObject.isChannel(chat) || chat.megagroup) ? R.string.LinkInfo : R.string.ChannelLinkInfo));
                context = this.mContext;
                i3 = R.drawable.greydivider;
            }
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i3, Theme.key_windowBackgroundGrayShadow));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i == 0) {
                textSettingsCell = new TextSettingsCell(this.mContext);
            } else {
                if (i == 1) {
                    textSettingsCell = new TextInfoPrivacyCell(this.mContext);
                    return new RecyclerListView.Holder(textSettingsCell);
                }
                textSettingsCell = new TextBlockCell(this.mContext);
            }
            textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(textSettingsCell);
        }
    }

    public GroupInviteActivity(long j) {
        this.chatId = j;
    }

    private void generateLink(final boolean z) {
        this.loading = true;
        TLRPC.TL_messages_exportChatInvite tL_messages_exportChatInvite = new TLRPC.TL_messages_exportChatInvite();
        tL_messages_exportChatInvite.peer = getMessagesController().getInputPeer(-this.chatId);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_exportChatInvite, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupInviteActivity.this.lambda$generateLink$3(z, tLObject, tL_error);
            }
        }), this.classGuid);
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void lambda$createView$0(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    public void lambda$createView$1(View view, int i) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            if (i == this.copyLinkRow || i == this.linkRow) {
                if (this.invite == null) {
                    return;
                }
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                BulletinFactory.createCopyLinkBulletin(this).show();
            } else {
                if (i != this.shareLinkRow) {
                    if (i == this.revokeLinkRow) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setMessage(LocaleController.getString(R.string.RevokeAlert));
                        builder.setTitle(LocaleController.getString(R.string.RevokeLink));
                        builder.setPositiveButton(LocaleController.getString(R.string.RevokeButton), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                GroupInviteActivity.this.lambda$createView$0(dialogInterface, i2);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                        showDialog(builder.create());
                        return;
                    }
                    return;
                }
                if (this.invite == null) {
                    return;
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.InviteToGroupByLink)), 500);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$generateLink$2(TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) tLObject;
            if (z) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString(R.string.RevokeAlertNewLink));
                builder.setTitle(LocaleController.getString(R.string.RevokeLink));
                builder.setNegativeButton(LocaleController.getString(R.string.OK), null);
                showDialog(builder.create());
            }
        }
        this.loading = false;
        this.listAdapter.notifyDataSetChanged();
    }

    public void lambda$generateLink$3(final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupInviteActivity.this.lambda$generateLink$2(tL_error, tLObject, z);
            }
        });
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.InviteLink));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupInviteActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                GroupInviteActivity.this.lambda$createView$1(view, i);
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
            int intValue = ((Integer) objArr[1]).intValue();
            if (chatFull.id == this.chatId && intValue == this.classGuid) {
                TLRPC.TL_chatInviteExported exportedInvite = getMessagesController().getExportedInvite(this.chatId);
                this.invite = exportedInvite;
                if (exportedInvite == null) {
                    generateLink(false);
                    return;
                }
                this.loading = false;
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextBlockCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
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
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        return arrayList;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        getMessagesController().loadFullChat(this.chatId, this.classGuid, true);
        this.loading = true;
        this.linkRow = 0;
        this.linkInfoRow = 1;
        this.copyLinkRow = 2;
        this.revokeLinkRow = 3;
        this.shareLinkRow = 4;
        this.rowCount = 6;
        this.shadowRow = 5;
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
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
