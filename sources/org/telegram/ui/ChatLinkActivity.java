package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChatLinkActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.JoinToSendSettingsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingStickerDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateFinalActivity;

public class ChatLinkActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int chatEndRow;
    private int chatStartRow;
    private boolean chatsLoaded;
    private int createChatRow;
    private TLRPC.Chat currentChat;
    private long currentChatId;
    private int detailRow;
    private EmptyTextProgressView emptyView;
    private int helpRow;
    private TLRPC.ChatFull info;
    private boolean isChannel;
    private int joinToSendRow;
    private JoinToSendSettingsView joinToSendSettings;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingChats;
    private int removeChatRow;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private TLRPC.Chat waitingForFullChat;
    private AlertDialog waitingForFullChatProgressAlert;
    private ArrayList chats = new ArrayList();
    private boolean joinToSendProgress = false;
    private boolean joinRequestProgress = false;

    private static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private int currentAccount;
        private LoadingStickerDrawable drawable;
        private BackupImageView stickerView;

        public EmptyView(Context context) {
            super(context);
            this.currentAccount = UserConfig.selectedAccount;
            setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            setOrientation(1);
            this.stickerView = new BackupImageView(context);
            LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, "M476.1,397.4c25.8-47.2,0.3-105.9-50.9-120c-2.5-6.9-7.8-12.7-15-16.4l0.4-229.4c0-12.3-10-22.4-22.4-22.4H128.5c-12.3,0-22.4,10-22.4,22.4l-0.4,229.8v0c0,6.7,2.9,12.6,7.6,16.7c-51.6,15.9-79.2,77.2-48.1,116.4c-8.7,11.7-13.4,27.5-14,47.2c-1.7,34.5,21.6,45.8,55.9,45.8c52.3,0,99.1,4.6,105.1-36.2c16.5,0.9,7.1-37.3-6.5-53.3c18.4-22.4,18.3-52.9,4.9-78.2c-0.7-5.3-3.8-9.8-8.1-12.6c-1.5-2-1.6-2-2.1-2.7c0.2-1,1.2-11.8-3.4-20.9h138.5c-4.8,8.8-4.7,17-2.9,22.1c-5.3,4.8-6.8,12.3-5.2,17c-11.4,24.9-10,53.8,4.3,77.5c-6.8,9.7-11.2,21.7-12.6,31.6c-0.2-0.2-0.4-0.3-0.6-0.5c0.8-3.3,0.4-6.4-1.3-7.8c9.3-12.1-4.5-29.2-17-21.7c-3.8-2.8-10.6-3.2-18.1-0.5c-2.4-10.6-21.1-10.6-28.6-1c-1.3,0.3-2.9,0.8-4.5,1.9c-5.2-0.9-10.9,0.1-14.1,4.4c-6.9,3-9.5,10.4-7.8,17c-0.9,1.8-1.1,4-0.8,6.3c-1.6,1.2-2.3,3.1-2,4.9c0.1,0.6,10.4,56.6,11.2,62c0.3,1.8,1.5,3.2,3.1,3.9c8.7,3.4,12,3.8,30.1,9.4c2.7,0.8,2.4,0.8,6.7-0.1c16.4-3.5,30.2-8.9,30.8-9.2c1.6-0.6,2.7-2,3.1-3.7c0.1-0.4,6.8-36.5,10-53.2c0.9,4.2,3.3,7.3,7.4,7.5c1.2,7.8,4.4,14.5,9.5,19.9c16.4,17.3,44.9,15.7,64.9,16.1c38.3,0.8,74.5,1.5,84.4-24.4C488.9,453.5,491.3,421.3,476.1,397.4z", AndroidUtilities.dp(104.0f), AndroidUtilities.dp(104.0f));
            this.drawable = loadingStickerDrawable;
            this.stickerView.setImageDrawable(loadingStickerDrawable);
            addView(this.stickerView, LayoutHelper.createLinear(104, 104, 49, 0, 2, 0, 0));
        }

        private void setSticker() {
            TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (stickerSetByName == null) {
                stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSetByName;
            if (tL_messages_stickerSet != null && tL_messages_stickerSet.documents.size() >= 3) {
                this.stickerView.setImage(ImageLocation.getForDocument(tL_messages_stickerSet.documents.get(2)), "104_104", "tgs", this.drawable, tL_messages_stickerSet);
            } else {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, tL_messages_stickerSet == null);
                this.stickerView.setImageDrawable(this.drawable);
            }
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals((String) objArr[0])) {
                setSticker();
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }
    }

    public class HintInnerCell extends FrameLayout {
        private EmptyView emptyView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            TextView textView;
            String formatString;
            EmptyView emptyView = new EmptyView(context);
            this.emptyView = emptyView;
            addView(emptyView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.messageTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_chats_message));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            if (!ChatLinkActivity.this.isChannel) {
                TLRPC.Chat chat = ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(ChatLinkActivity.this.info.linked_chat_id));
                if (chat != null) {
                    textView = this.messageTextView;
                    formatString = LocaleController.formatString("DiscussionGroupHelp", R.string.DiscussionGroupHelp, chat.title);
                    textView.setText(AndroidUtilities.replaceTags(formatString));
                }
            } else if (ChatLinkActivity.this.info == null || ChatLinkActivity.this.info.linked_chat_id == 0) {
                this.messageTextView.setText(LocaleController.getString(R.string.DiscussionChannelHelp3));
            } else {
                TLRPC.Chat chat2 = ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(ChatLinkActivity.this.info.linked_chat_id));
                if (chat2 != null) {
                    textView = this.messageTextView;
                    formatString = LocaleController.formatString("DiscussionChannelGroupSetHelp2", R.string.DiscussionChannelGroupSetHelp2, chat2.title);
                    textView.setText(AndroidUtilities.replaceTags(formatString));
                }
            }
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 143.0f, 52.0f, 18.0f));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public class AnonymousClass1 extends JoinToSendSettingsView {
            final TLRPC.Chat val$chat;

            AnonymousClass1(Context context, TLRPC.Chat chat, TLRPC.Chat chat2) {
                super(context, chat);
                this.val$chat = chat2;
            }

            public void lambda$migrateIfNeeded$0(Runnable runnable, long j) {
                if (j != 0) {
                    if (ChatLinkActivity.this.isChannel) {
                        ChatLinkActivity.this.chats.set(0, ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(j)));
                    } else {
                        ChatLinkActivity.this.currentChatId = j;
                        ChatLinkActivity chatLinkActivity = ChatLinkActivity.this;
                        chatLinkActivity.currentChat = chatLinkActivity.getMessagesController().getChat(Long.valueOf(j));
                    }
                    runnable.run();
                }
            }

            public void lambda$onJoinRequestToggle$1() {
                ChatLinkActivity.this.joinRequestProgress = false;
            }

            public void lambda$onJoinRequestToggle$2(Runnable runnable) {
                ChatLinkActivity.this.joinRequestProgress = false;
                runnable.run();
            }

            public void lambda$onJoinRequestToggle$3(TLRPC.Chat chat, boolean z, final Runnable runnable) {
                chat.join_request = z;
                ChatLinkActivity.this.getMessagesController().toggleChatJoinRequest(chat.id, z, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinRequestToggle$1();
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinRequestToggle$2(runnable);
                    }
                });
            }

            public void lambda$onJoinToSendToggle$5() {
                ChatLinkActivity.this.joinRequestProgress = false;
            }

            public void lambda$onJoinToSendToggle$6(TLRPC.Chat chat) {
                chat.join_request = true;
                this.isJoinRequest = true;
                this.joinRequestCell.setChecked(true);
            }

            public void lambda$onJoinToSendToggle$7(boolean z, final TLRPC.Chat chat) {
                ChatLinkActivity.this.joinToSendProgress = false;
                if (z || !chat.join_request) {
                    return;
                }
                chat.join_request = false;
                ChatLinkActivity.this.joinRequestProgress = true;
                ChatLinkActivity.this.getMessagesController().toggleChatJoinRequest(chat.id, false, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinToSendToggle$5();
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinToSendToggle$6(chat);
                    }
                });
            }

            public void lambda$onJoinToSendToggle$8(Runnable runnable) {
                ChatLinkActivity.this.joinToSendProgress = false;
                runnable.run();
            }

            public void lambda$onJoinToSendToggle$9(final TLRPC.Chat chat, final boolean z, final Runnable runnable) {
                chat.join_to_send = z;
                ChatLinkActivity.this.getMessagesController().toggleChatJoinToSend(chat.id, z, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinToSendToggle$7(z, chat);
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinToSendToggle$8(runnable);
                    }
                });
            }

            public void lambda$overrideCancel$4(Runnable runnable) {
                ChatLinkActivity.this.joinToSendProgress = false;
                ChatLinkActivity.this.joinRequestProgress = false;
                runnable.run();
            }

            private void migrateIfNeeded(Runnable runnable, final Runnable runnable2) {
                if (ChatObject.isChannel(ChatLinkActivity.this.currentChat)) {
                    runnable2.run();
                } else {
                    ChatLinkActivity.this.getMessagesController().convertToMegaGroup(ChatLinkActivity.this.getParentActivity(), this.val$chat.id, ChatLinkActivity.this, new MessagesStorage.LongCallback() {
                        @Override
                        public final void run(long j) {
                            ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$migrateIfNeeded$0(runnable2, j);
                        }
                    }, runnable);
                }
            }

            private Runnable overrideCancel(final Runnable runnable) {
                return new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$overrideCancel$4(runnable);
                    }
                };
            }

            @Override
            public boolean onJoinRequestToggle(final boolean z, final Runnable runnable) {
                if (ChatLinkActivity.this.joinRequestProgress) {
                    return false;
                }
                ChatLinkActivity.this.joinRequestProgress = true;
                Runnable overrideCancel = overrideCancel(runnable);
                final TLRPC.Chat chat = this.val$chat;
                migrateIfNeeded(overrideCancel, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinRequestToggle$3(chat, z, runnable);
                    }
                });
                return true;
            }

            @Override
            public boolean onJoinToSendToggle(final boolean z, final Runnable runnable) {
                if (ChatLinkActivity.this.joinToSendProgress) {
                    return false;
                }
                ChatLinkActivity.this.joinToSendProgress = true;
                Runnable overrideCancel = overrideCancel(runnable);
                final TLRPC.Chat chat = this.val$chat;
                migrateIfNeeded(overrideCancel, new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.lambda$onJoinToSendToggle$9(chat, z, runnable);
                    }
                });
                return true;
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (!ChatLinkActivity.this.loadingChats || ChatLinkActivity.this.chatsLoaded) {
                return ChatLinkActivity.this.rowCount;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == ChatLinkActivity.this.helpRow) {
                return 3;
            }
            if (i == ChatLinkActivity.this.createChatRow || i == ChatLinkActivity.this.removeChatRow) {
                return 2;
            }
            if (i < ChatLinkActivity.this.chatStartRow || i >= ChatLinkActivity.this.chatEndRow) {
                return i == ChatLinkActivity.this.joinToSendRow ? 4 : 1;
            }
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                TLRPC.Chat chat = (TLRPC.Chat) ChatLinkActivity.this.chats.get(i - ChatLinkActivity.this.chatStartRow);
                String publicUsername = ChatObject.getPublicUsername(chat);
                if (TextUtils.isEmpty(publicUsername)) {
                    str = null;
                } else {
                    str = "@" + publicUsername;
                }
                manageChatUserCell.setData(chat, null, str, (i == ChatLinkActivity.this.chatEndRow - 1 && ChatLinkActivity.this.info.linked_chat_id == 0) ? false : true);
                return;
            }
            if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == ChatLinkActivity.this.detailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString(ChatLinkActivity.this.isChannel ? R.string.DiscussionChannelHelp2 : R.string.DiscussionGroupHelp2));
                    return;
                }
                return;
            }
            if (itemViewType != 2) {
                return;
            }
            ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
            if (!ChatLinkActivity.this.isChannel) {
                int i3 = Theme.key_text_RedRegular;
                manageChatTextCell.setColors(i3, i3);
                i2 = R.string.DiscussionUnlinkChannel;
            } else if (ChatLinkActivity.this.info.linked_chat_id == 0) {
                manageChatTextCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                manageChatTextCell.setText(LocaleController.getString(R.string.DiscussionCreateGroup), null, R.drawable.msg_groups, true);
                return;
            } else {
                int i4 = Theme.key_text_RedRegular;
                manageChatTextCell.setColors(i4, i4);
                i2 = R.string.DiscussionUnlinkGroup;
            }
            manageChatTextCell.setText(LocaleController.getString(i2), null, R.drawable.msg_remove, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
                manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = manageChatUserCell;
            } else if (i == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else if (i == 2) {
                view = new ManageChatTextCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (i != 4) {
                view = new HintInnerCell(this.mContext);
            } else {
                TLRPC.Chat chat = ChatLinkActivity.this.isChannel ? (TLRPC.Chat) ChatLinkActivity.this.chats.get(0) : ChatLinkActivity.this.currentChat;
                view = ChatLinkActivity.this.joinToSendSettings = new AnonymousClass1(this.mContext, chat, chat);
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList searchResult = new ArrayList();
        private ArrayList searchResultNames = new ArrayList();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void lambda$processSearch$1(java.lang.String r19, java.util.ArrayList r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatLinkActivity.SearchAdapter.lambda$processSearch$1(java.lang.String, java.util.ArrayList):void");
        }

        public void lambda$processSearch$2(final String str) {
            this.searchRunnable = null;
            final ArrayList arrayList = new ArrayList(ChatLinkActivity.this.chats);
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$processSearch$1(str, arrayList);
                }
            });
        }

        public void lambda$updateSearchResults$3(ArrayList arrayList, ArrayList arrayList2) {
            if (ChatLinkActivity.this.searching) {
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                if (ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter) {
                    ChatLinkActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }

        public void lambda$searchDialogs$0(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$processSearch$2(str);
                }
            });
        }

        private void updateSearchResults(final ArrayList arrayList, final ArrayList arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$updateSearchResults$3(arrayList, arrayList2);
                }
            });
        }

        public TLRPC.Chat getItem(int i) {
            return (TLRPC.Chat) this.searchResult.get(i);
        }

        @Override
        public int getItemCount() {
            return this.searchResult.size();
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC.Chat chat = (TLRPC.Chat) this.searchResult.get(i);
            String publicUsername = ChatObject.getPublicUsername(chat);
            CharSequence charSequence = (CharSequence) this.searchResultNames.get(i);
            CharSequence charSequence2 = null;
            if (charSequence != null && !TextUtils.isEmpty(publicUsername)) {
                if (charSequence.toString().startsWith("@" + publicUsername)) {
                    charSequence2 = charSequence;
                    charSequence = null;
                }
            }
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
            manageChatUserCell.setTag(Integer.valueOf(i));
            manageChatUserCell.setData(chat, charSequence, charSequence2, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
            manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public void searchDialogs(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
            } else {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.SearchAdapter.this.lambda$searchDialogs$0(str);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }
    }

    public ChatLinkActivity(long j) {
        boolean z = false;
        this.currentChatId = j;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(j));
        this.currentChat = chat;
        if (ChatObject.isChannel(chat) && !this.currentChat.megagroup) {
            z = true;
        }
        this.isChannel = z;
    }

    public void lambda$createView$0() {
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
    }

    public void lambda$createView$1(AlertDialog[] alertDialogArr) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.info.linked_chat_id = 0L;
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, this.info, 0, bool, bool);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$createView$0();
            }
        }, 1000L);
        if (this.isChannel) {
            return;
        }
        lambda$onBackPressed$323();
    }

    public void lambda$createView$2(final AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$createView$1(alertDialogArr);
            }
        });
    }

    public void lambda$createView$3(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void lambda$createView$4(AlertDialog[] alertDialogArr, final int i) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                ChatLinkActivity.this.lambda$createView$3(i, dialogInterface);
            }
        });
        showDialog(alertDialogArr[0]);
    }

    public void lambda$createView$5(AlertDialog alertDialog, int i) {
        TLRPC.InputChannel inputChannel;
        if (this.isChannel && this.info.linked_chat_id == 0) {
            return;
        }
        final AlertDialog[] alertDialogArr = {new AlertDialog(getParentActivity(), 3)};
        TLRPC.TL_channels_setDiscussionGroup tL_channels_setDiscussionGroup = new TLRPC.TL_channels_setDiscussionGroup();
        if (this.isChannel) {
            tL_channels_setDiscussionGroup.broadcast = MessagesController.getInputChannel(this.currentChat);
            inputChannel = new TLRPC.TL_inputChannelEmpty();
        } else {
            tL_channels_setDiscussionGroup.broadcast = new TLRPC.TL_inputChannelEmpty();
            inputChannel = MessagesController.getInputChannel(this.currentChat);
        }
        tL_channels_setDiscussionGroup.group = inputChannel;
        final int sendRequest = getConnectionsManager().sendRequest(tL_channels_setDiscussionGroup, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatLinkActivity.this.lambda$createView$2(alertDialogArr, tLObject, tL_error);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$createView$4(alertDialogArr, sendRequest);
            }
        }, 500L);
    }

    public void lambda$createView$6(View view, int i) {
        TLRPC.Chat chat;
        String string;
        String formatString;
        if (getParentActivity() == null) {
            return;
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchAdapter;
        if (adapter == searchAdapter) {
            chat = searchAdapter.getItem(i);
        } else {
            int i2 = this.chatStartRow;
            chat = (i < i2 || i >= this.chatEndRow) ? null : (TLRPC.Chat) this.chats.get(i - i2);
        }
        if (chat != null) {
            if (this.isChannel && this.info.linked_chat_id == 0) {
                showLinkAlert(chat, true);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", chat.id);
            presentFragment(new ChatActivity(bundle));
            return;
        }
        if (i == this.createChatRow) {
            if (this.isChannel && this.info.linked_chat_id == 0) {
                Bundle bundle2 = new Bundle();
                bundle2.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
                bundle2.putInt("chatType", 4);
                TLRPC.Chat chat2 = this.currentChat;
                if (chat2 != null) {
                    bundle2.putString("title", LocaleController.formatString("GroupCreateDiscussionDefaultName", R.string.GroupCreateDiscussionDefaultName, chat2.title));
                }
                GroupCreateFinalActivity groupCreateFinalActivity = new GroupCreateFinalActivity(bundle2);
                groupCreateFinalActivity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                    @Override
                    public void didFailChatCreation() {
                    }

                    @Override
                    public void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity2, long j) {
                        ChatLinkActivity chatLinkActivity = ChatLinkActivity.this;
                        chatLinkActivity.linkChat(chatLinkActivity.getMessagesController().getChat(Long.valueOf(j)), groupCreateFinalActivity2);
                    }

                    @Override
                    public void didStartChatCreation() {
                    }
                });
                presentFragment(groupCreateFinalActivity);
                return;
            }
            if (this.chats.isEmpty()) {
                return;
            }
            TLRPC.Chat chat3 = (TLRPC.Chat) this.chats.get(0);
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            if (this.isChannel) {
                string = LocaleController.getString(R.string.DiscussionUnlinkGroup);
                formatString = LocaleController.formatString("DiscussionUnlinkChannelAlert", R.string.DiscussionUnlinkChannelAlert, chat3.title);
            } else {
                string = LocaleController.getString(R.string.DiscussionUnlinkChannel);
                formatString = LocaleController.formatString("DiscussionUnlinkGroupAlert", R.string.DiscussionUnlinkGroupAlert, chat3.title);
            }
            builder.setTitle(string);
            builder.setMessage(AndroidUtilities.replaceTags(formatString));
            builder.setPositiveButton(LocaleController.getString(R.string.DiscussionUnlink), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i3) {
                    ChatLinkActivity.this.lambda$createView$5(alertDialog, i3);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }
    }

    public void lambda$getThemeDescriptions$18() {
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

    public void lambda$linkChat$10(BaseFragment baseFragment, long j) {
        if (j != 0) {
            getMessagesController().toggleChannelInvitesHistory(j, false);
            linkChat(getMessagesController().getChat(Long.valueOf(j)), baseFragment);
        }
    }

    public void lambda$linkChat$11() {
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
    }

    public void lambda$linkChat$12(AlertDialog[] alertDialogArr, TLRPC.Chat chat, BaseFragment baseFragment) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Throwable unused) {
            }
            alertDialogArr[0] = null;
        }
        this.info.linked_chat_id = chat.id;
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, this.info, 0, bool, bool);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$linkChat$11();
            }
        }, 1000L);
        if (baseFragment == null) {
            lambda$onBackPressed$323();
        } else {
            removeSelfFromStack();
            baseFragment.lambda$onBackPressed$323();
        }
    }

    public void lambda$linkChat$13(final AlertDialog[] alertDialogArr, final TLRPC.Chat chat, final BaseFragment baseFragment, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$linkChat$12(alertDialogArr, chat, baseFragment);
            }
        });
    }

    public void lambda$linkChat$14(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void lambda$linkChat$15(AlertDialog[] alertDialogArr, final int i) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                ChatLinkActivity.this.lambda$linkChat$14(i, dialogInterface);
            }
        });
        showDialog(alertDialogArr[0]);
    }

    public void lambda$loadChats$16(TLObject tLObject) {
        if (tLObject instanceof TLRPC.messages_Chats) {
            TLRPC.messages_Chats messages_chats = (TLRPC.messages_Chats) tLObject;
            getMessagesController().putChats(messages_chats.chats, false);
            ArrayList<TLRPC.Chat> arrayList = messages_chats.chats;
            this.chats = arrayList;
            Iterator<TLRPC.Chat> it = arrayList.iterator();
            while (it.hasNext()) {
                if (ChatObject.isForum(it.next())) {
                    it.remove();
                }
            }
        }
        this.loadingChats = false;
        this.chatsLoaded = true;
        updateRows();
    }

    public void lambda$loadChats$17(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$loadChats$16(tLObject);
            }
        });
    }

    public void lambda$showLinkAlert$7(DialogInterface dialogInterface) {
        this.waitingForFullChat = null;
    }

    public void lambda$showLinkAlert$8() {
        AlertDialog alertDialog = this.waitingForFullChatProgressAlert;
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                ChatLinkActivity.this.lambda$showLinkAlert$7(dialogInterface);
            }
        });
        showDialog(this.waitingForFullChatProgressAlert);
    }

    public void lambda$showLinkAlert$9(TLRPC.ChatFull chatFull, TLRPC.Chat chat, AlertDialog alertDialog, int i) {
        if (chatFull.hidden_prehistory) {
            getMessagesController().toggleChannelInvitesHistory(chat.id, false);
        }
        linkChat(chat, null);
    }

    public void linkChat(final TLRPC.Chat chat, final BaseFragment baseFragment) {
        if (chat == null) {
            return;
        }
        if (!ChatObject.isChannel(chat)) {
            getMessagesController().convertToMegaGroup(getParentActivity(), chat.id, this, new MessagesStorage.LongCallback() {
                @Override
                public final void run(long j) {
                    ChatLinkActivity.this.lambda$linkChat$10(baseFragment, j);
                }
            });
            return;
        }
        final AlertDialog[] alertDialogArr = {baseFragment != null ? null : new AlertDialog(getParentActivity(), 3)};
        TLRPC.TL_channels_setDiscussionGroup tL_channels_setDiscussionGroup = new TLRPC.TL_channels_setDiscussionGroup();
        tL_channels_setDiscussionGroup.broadcast = MessagesController.getInputChannel(this.currentChat);
        tL_channels_setDiscussionGroup.group = MessagesController.getInputChannel(chat);
        final int sendRequest = getConnectionsManager().sendRequest(tL_channels_setDiscussionGroup, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatLinkActivity.this.lambda$linkChat$13(alertDialogArr, chat, baseFragment, tLObject, tL_error);
            }
        }, 64);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatLinkActivity.this.lambda$linkChat$15(alertDialogArr, sendRequest);
            }
        }, 500L);
    }

    private void loadChats() {
        if (this.info.linked_chat_id != 0) {
            this.chats.clear();
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
            if (chat != null) {
                this.chats.add(chat);
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
        }
        if (!this.loadingChats && this.isChannel && this.info.linked_chat_id == 0) {
            this.loadingChats = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_channels_getGroupsForDiscussion(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatLinkActivity.this.lambda$loadChats$17(tLObject, tL_error);
                }
            });
        }
    }

    private void showLinkAlert(final TLRPC.Chat chat, boolean z) {
        final TLRPC.ChatFull chatFull = getMessagesController().getChatFull(chat.id);
        if (chatFull == null) {
            if (z) {
                getMessagesController().loadFullChat(chat.id, 0, true);
                this.waitingForFullChat = chat;
                this.waitingForFullChatProgressAlert = new AlertDialog(getParentActivity(), 3);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChatLinkActivity.this.lambda$showLinkAlert$8();
                    }
                }, 500L);
                return;
            }
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        TextView textView = new TextView(getParentActivity());
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        String formatString = !ChatObject.isPublic(chat) ? LocaleController.formatString("DiscussionLinkGroupPublicPrivateAlert", R.string.DiscussionLinkGroupPublicPrivateAlert, chat.title, this.currentChat.title) : !ChatObject.isPublic(this.currentChat) ? LocaleController.formatString("DiscussionLinkGroupPrivateAlert", R.string.DiscussionLinkGroupPrivateAlert, chat.title, this.currentChat.title) : LocaleController.formatString("DiscussionLinkGroupPublicAlert", R.string.DiscussionLinkGroupPublicAlert, chat.title, this.currentChat.title);
        if (chatFull.hidden_prehistory) {
            formatString = formatString + "\n\n" + LocaleController.getString(R.string.DiscussionLinkGroupAlertHistory);
        }
        textView.setText(AndroidUtilities.replaceTags(formatString));
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        builder.setView(frameLayout);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(getParentActivity());
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        TextView textView2 = new TextView(getParentActivity());
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView2.setTextSize(1, 20.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setText(chat.title);
        boolean z2 = LocaleController.isRTL;
        frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 76, 11.0f, z2 ? 76 : 21, 0.0f));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        avatarDrawable.setInfo(this.currentAccount, chat);
        backupImageView.setForUserOrChat(chat, avatarDrawable);
        builder.setPositiveButton(LocaleController.getString(R.string.DiscussionLinkGroup), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                ChatLinkActivity.this.lambda$showLinkAlert$9(chatFull, chat, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void updateRows() {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat == null) {
            return;
        }
        this.createChatRow = -1;
        this.chatStartRow = -1;
        this.chatEndRow = -1;
        this.removeChatRow = -1;
        this.detailRow = -1;
        this.joinToSendRow = -1;
        this.rowCount = 1;
        this.helpRow = 0;
        if (this.isChannel) {
            if (this.info.linked_chat_id == 0) {
                this.rowCount = 2;
                this.createChatRow = 1;
            }
            int i = this.rowCount;
            this.chatStartRow = i;
            int size = i + this.chats.size();
            this.rowCount = size;
            this.chatEndRow = size;
            if (this.info.linked_chat_id != 0) {
                this.rowCount = size + 1;
                this.createChatRow = size;
            }
        } else {
            this.chatStartRow = 1;
            int size2 = this.chats.size();
            int i2 = size2 + 1;
            this.chatEndRow = i2;
            this.rowCount = size2 + 2;
            this.createChatRow = i2;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.detailRow = i3;
        if (!this.isChannel || (this.chats.size() > 0 && this.info.linked_chat_id != 0)) {
            TLRPC.Chat chat2 = this.isChannel ? (TLRPC.Chat) this.chats.get(0) : this.currentChat;
            if (chat2 != null && ((!ChatObject.isPublic(chat2) || this.isChannel) && (chat2.creator || ((tL_chatAdminRights = chat2.admin_rights) != null && tL_chatAdminRights.ban_users)))) {
                int i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.joinToSendRow = i4;
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(this.chats.size() <= 10 ? 8 : 0);
        }
    }

    @Override
    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.Discussion));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatLinkActivity.this.lambda$onBackPressed$323();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchCollapse() {
                ChatLinkActivity.this.searchAdapter.searchDialogs(null);
                ChatLinkActivity.this.searching = false;
                ChatLinkActivity.this.searchWas = false;
                ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.listViewAdapter);
                ChatLinkActivity.this.listViewAdapter.notifyDataSetChanged();
                ChatLinkActivity.this.listView.setFastScrollVisible(true);
                ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(false);
                ChatLinkActivity.this.emptyView.setShowAtCenter(false);
                View view = ChatLinkActivity.this.fragmentView;
                int i = Theme.key_windowBackgroundGray;
                view.setBackgroundColor(Theme.getColor(i));
                ChatLinkActivity.this.fragmentView.setTag(Integer.valueOf(i));
                ChatLinkActivity.this.emptyView.showProgress();
            }

            @Override
            public void onSearchExpand() {
                ChatLinkActivity.this.searching = true;
                ChatLinkActivity.this.emptyView.setShowAtCenter(true);
            }

            @Override
            public void onTextChanged(EditText editText) {
                if (ChatLinkActivity.this.searchAdapter == null) {
                    return;
                }
                String obj = editText.getText().toString();
                if (obj.length() != 0) {
                    ChatLinkActivity.this.searchWas = true;
                    if (ChatLinkActivity.this.listView != null && ChatLinkActivity.this.listView.getAdapter() != ChatLinkActivity.this.searchAdapter) {
                        ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.searchAdapter);
                        View view = ChatLinkActivity.this.fragmentView;
                        int i = Theme.key_windowBackgroundWhite;
                        view.setBackgroundColor(Theme.getColor(i));
                        ChatLinkActivity.this.fragmentView.setTag(Integer.valueOf(i));
                        ChatLinkActivity.this.searchAdapter.notifyDataSetChanged();
                        ChatLinkActivity.this.listView.setFastScrollVisible(false);
                        ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(true);
                        ChatLinkActivity.this.emptyView.showProgress();
                    }
                }
                ChatLinkActivity.this.searchAdapter.searchDialogs(obj);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(R.string.Search));
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        int i = Theme.key_windowBackgroundGray;
        frameLayout.setBackgroundColor(Theme.getColor(i));
        this.fragmentView.setTag(Integer.valueOf(i));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        this.emptyView.setText(LocaleController.getString(R.string.NoResult));
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                ChatLinkActivity.this.lambda$createView$6(view, i2);
            }
        });
        updateRows();
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        JoinToSendSettingsView joinToSendSettingsView;
        TLRPC.Chat chat;
        TLRPC.Chat chat2 = null;
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
            long j = chatFull.id;
            if (j == this.currentChatId) {
                this.info = chatFull;
                loadChats();
                updateRows();
                return;
            }
            TLRPC.Chat chat3 = this.waitingForFullChat;
            if (chat3 == null || chat3.id != j) {
                return;
            }
            try {
                this.waitingForFullChatProgressAlert.dismiss();
            } catch (Throwable unused) {
            }
            this.waitingForFullChatProgressAlert = null;
            showLinkAlert(this.waitingForFullChat, false);
            this.waitingForFullChat = null;
            return;
        }
        if (i != NotificationCenter.updateInterfaces) {
            if (i == NotificationCenter.dialogDeleted) {
                if ((-this.currentChatId) == ((Long) objArr[0]).longValue()) {
                    INavigationLayout iNavigationLayout = this.parentLayout;
                    if (iNavigationLayout == null || iNavigationLayout.getLastFragment() != this) {
                        removeSelfFromStack();
                        return;
                    } else {
                        lambda$onBackPressed$323();
                        return;
                    }
                }
                return;
            }
            return;
        }
        if ((((Integer) objArr[0]).intValue() & MessagesController.UPDATE_MASK_CHAT) == 0 || this.currentChat == null) {
            return;
        }
        TLRPC.Chat chat4 = getMessagesController().getChat(Long.valueOf(this.currentChat.id));
        if (chat4 != null) {
            this.currentChat = chat4;
        }
        if (this.chats.size() > 0 && (chat = getMessagesController().getChat(Long.valueOf(((TLRPC.Chat) this.chats.get(0)).id))) != null) {
            this.chats.set(0, chat);
        }
        if (!this.isChannel) {
            chat2 = this.currentChat;
        } else if (this.chats.size() > 0) {
            chat2 = (TLRPC.Chat) this.chats.get(0);
        }
        if (chat2 == null || (joinToSendSettingsView = this.joinToSendSettings) == null) {
            return;
        }
        if (!this.joinRequestProgress) {
            joinToSendSettingsView.lambda$new$3(chat2.join_request);
        }
        if (this.joinToSendProgress) {
            return;
        }
        this.joinToSendSettings.setJoinToSend(chat2.join_to_send);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ChatLinkActivity.this.lambda$getThemeDescriptions$18();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, i));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
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
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        return arrayList;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogDeleted);
        loadChats();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogDeleted);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }
}
