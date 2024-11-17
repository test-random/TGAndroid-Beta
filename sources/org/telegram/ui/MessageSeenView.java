package org.telegram.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MessageSeenCheckDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StatusBadgeComponent;

public class MessageSeenView extends FrameLayout {
    AvatarsImageView avatarsImageView;
    int currentAccount;
    ArrayList dates;
    FlickerLoadingView flickerLoadingView;
    ImageView iconView;
    boolean ignoreLayout;
    boolean isVoice;
    private RecyclerListView listView;
    ArrayList peerIds;
    SimpleTextView titleView;
    public ArrayList users;

    private static class UserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        private static MessageSeenCheckDrawable seenDrawable = new MessageSeenCheckDrawable(R.drawable.msg_mini_checks, Theme.key_windowBackgroundWhiteGrayText);
        AvatarDrawable avatarDrawable;
        BackupImageView avatarImageView;
        private int currentAccount;
        SimpleTextView nameView;
        TLObject object;
        TextView readView;
        StatusBadgeComponent statusBadgeComponent;

        public UserCell(Context context) {
            super(context);
            float f;
            float f2;
            int i;
            float f3;
            TextView textView;
            float f4;
            int i2;
            this.currentAccount = UserConfig.selectedAccount;
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.nameView.setEllipsizeByGradient(!LocaleController.isRTL);
            this.nameView.setImportantForAccessibility(2);
            this.nameView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
            this.nameView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.statusBadgeComponent = new StatusBadgeComponent(this);
            this.nameView.setDrawablePadding(AndroidUtilities.dp(3.0f));
            TextView textView2 = new TextView(context);
            this.readView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.readView.setLines(1);
            this.readView.setEllipsize(TextUtils.TruncateAt.END);
            this.readView.setImportantForAccessibility(2);
            this.readView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.readView.setGravity(LocaleController.isRTL ? 5 : 3);
            if (LocaleController.isRTL) {
                f = 0.0f;
                addView(this.avatarImageView, LayoutHelper.createFrame(34, 34.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
                f4 = 55.0f;
                f2 = -2.0f;
                i = 53;
                addView(this.nameView, LayoutHelper.createFrame(-2, -2.0f, 53, 8.0f, 6.33f, 55.0f, 0.0f));
                textView = this.readView;
                i2 = -2;
                f3 = 13.0f;
            } else {
                f = 0.0f;
                addView(this.avatarImageView, LayoutHelper.createFrame(34, 34.0f, 19, 10.0f, 0.0f, 0.0f, 0.0f));
                f2 = -2.0f;
                i = 51;
                f3 = 55.0f;
                addView(this.nameView, LayoutHelper.createFrame(-2, -2.0f, 51, 55.0f, 6.33f, 8.0f, 0.0f));
                textView = this.readView;
                f4 = 13.0f;
                i2 = -2;
            }
            addView(textView, LayoutHelper.createFrame(i2, f2, i, f3, 20.0f, f4, f));
        }

        private void updateStatus(boolean z) {
            this.nameView.setRightDrawable(this.statusBadgeComponent.updateDrawable(this.object, Theme.getColor(Theme.key_chats_verifiedBackground), z));
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.userEmojiStatusUpdated) {
                TLRPC.User user = (TLRPC.User) objArr[0];
                TLObject tLObject = this.object;
                TLRPC.User user2 = tLObject instanceof TLRPC.User ? (TLRPC.User) tLObject : null;
                if (user2 == null || user == null || user2.id != user.id) {
                    return;
                }
                this.object = user;
                updateStatus(true);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.statusBadgeComponent.onAttachedToWindow();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userEmojiStatusUpdated);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.statusBadgeComponent.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            String formatString = LocaleController.formatString("AccDescrPersonHasSeen", R.string.AccDescrPersonHasSeen, this.nameView.getText());
            if (this.readView.getVisibility() == 0) {
                formatString = formatString + " " + ((Object) this.readView.getText());
            }
            accessibilityNodeInfo.setText(formatString);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }

        public void setUser(TLObject tLObject, int i) {
            SimpleTextView simpleTextView;
            float f;
            this.object = tLObject;
            updateStatus(false);
            if (tLObject != null) {
                this.avatarDrawable.setInfo(this.currentAccount, tLObject);
                this.avatarImageView.setImage(ImageLocation.getForUserOrChat(tLObject, 1), "50_50", this.avatarDrawable, tLObject);
                this.nameView.setText(ContactsController.formatName(tLObject));
            }
            TextView textView = this.readView;
            if (i <= 0) {
                textView.setVisibility(8);
                simpleTextView = this.nameView;
                f = AndroidUtilities.dp(9.0f);
            } else {
                textView.setText(TextUtils.concat(seenDrawable.getSpanned(getContext(), null), LocaleController.formatSeenDate(i)));
                this.readView.setVisibility(0);
                simpleTextView = this.nameView;
                f = 0.0f;
            }
            simpleTextView.setTranslationY(f);
        }
    }

    public MessageSeenView(Context context, final int i, MessageObject messageObject, final TLRPC.Chat chat) {
        super(context);
        this.peerIds = new ArrayList();
        this.dates = new ArrayList();
        this.users = new ArrayList();
        this.currentAccount = i;
        this.isVoice = messageObject.isRoundVideo() || messageObject.isVoice();
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setColors(Theme.key_actionBarDefaultSubmenuBackground, Theme.key_listSelector, -1);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.titleView.setEllipsizeByGradient(true);
        this.titleView.setRightPadding(AndroidUtilities.dp(62.0f));
        addView(this.titleView, LayoutHelper.createFrame(0, -2.0f, 19, 40.0f, 0.0f, 0.0f, 0.0f));
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView;
        avatarsImageView.setStyle(11);
        this.avatarsImageView.setAvatarsTextSize(AndroidUtilities.dp(22.0f));
        addView(this.avatarsImageView, LayoutHelper.createFrame(56, -1.0f, 21, 0.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        TLRPC.TL_messages_getMessageReadParticipants tL_messages_getMessageReadParticipants = new TLRPC.TL_messages_getMessageReadParticipants();
        tL_messages_getMessageReadParticipants.msg_id = messageObject.getId();
        tL_messages_getMessageReadParticipants.peer = MessagesController.getInstance(i).getInputPeer(messageObject.getDialogId());
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable mutate = ContextCompat.getDrawable(context, this.isVoice ? R.drawable.msg_played : R.drawable.msg_seen).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(mutate);
        this.avatarsImageView.setAlpha(0.0f);
        this.titleView.setAlpha(0.0f);
        TLRPC.Peer peer = messageObject.messageOwner.from_id;
        final long j = peer != null ? peer.user_id : 0L;
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_getMessageReadParticipants, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessageSeenView.this.lambda$new$5(j, i, chat, tLObject, tL_error);
            }
        });
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 6, 0));
        setEnabled(false);
    }

    public void lambda$new$0(TLObject tLObject, int i, HashMap hashMap, ArrayList arrayList) {
        if (tLObject != null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            for (int i2 = 0; i2 < tL_channels_channelParticipants.users.size(); i2++) {
                TLRPC.User user = tL_channels_channelParticipants.users.get(i2);
                MessagesController.getInstance(i).putUser(user, false);
                hashMap.put(Long.valueOf(user.id), user);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Pair pair = (Pair) arrayList.get(i3);
                this.peerIds.add((Long) pair.first);
                this.dates.add((Integer) pair.second);
                this.users.add((TLObject) hashMap.get(pair.first));
            }
        }
        updateView();
    }

    public void lambda$new$1(final int i, final HashMap hashMap, final ArrayList arrayList, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessageSeenView.this.lambda$new$0(tLObject, i, hashMap, arrayList);
            }
        });
    }

    public void lambda$new$2(TLObject tLObject, int i, HashMap hashMap, ArrayList arrayList) {
        if (tLObject != null) {
            TLRPC.TL_messages_chatFull tL_messages_chatFull = (TLRPC.TL_messages_chatFull) tLObject;
            for (int i2 = 0; i2 < tL_messages_chatFull.users.size(); i2++) {
                TLRPC.User user = tL_messages_chatFull.users.get(i2);
                MessagesController.getInstance(i).putUser(user, false);
                hashMap.put(Long.valueOf(user.id), user);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Pair pair = (Pair) arrayList.get(i3);
                this.peerIds.add((Long) pair.first);
                this.dates.add((Integer) pair.second);
                this.users.add((TLObject) hashMap.get(pair.first));
            }
        }
        updateView();
    }

    public void lambda$new$3(final int i, final HashMap hashMap, final ArrayList arrayList, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessageSeenView.this.lambda$new$2(tLObject, i, hashMap, arrayList);
            }
        });
    }

    public void lambda$new$4(TLRPC.TL_error tL_error, TLObject tLObject, long j, final int i, TLRPC.Chat chat) {
        RequestDelegate requestDelegate;
        TLRPC.TL_messages_getFullChat tL_messages_getFullChat;
        ConnectionsManager connectionsManager;
        Long l;
        if (tL_error == null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            final HashMap hashMap = new HashMap();
            final ArrayList arrayList3 = new ArrayList();
            int size = vector.objects.size();
            for (int i2 = 0; i2 < size; i2++) {
                Object obj = vector.objects.get(i2);
                if (obj instanceof TLRPC.TL_readParticipantDate) {
                    TLRPC.TL_readParticipantDate tL_readParticipantDate = (TLRPC.TL_readParticipantDate) obj;
                    int i3 = tL_readParticipantDate.date;
                    long j2 = tL_readParticipantDate.user_id;
                    l = Long.valueOf(j2);
                    if (j != j2) {
                        MessagesController.getInstance(i).getUser(l);
                        arrayList3.add(new Pair(l, Integer.valueOf(i3)));
                        arrayList.add(l);
                    }
                } else {
                    if (obj instanceof Long) {
                        l = (Long) obj;
                        if (j != l.longValue()) {
                            long longValue = l.longValue();
                            MessagesController messagesController = MessagesController.getInstance(i);
                            if (longValue > 0) {
                                messagesController.getUser(l);
                                arrayList3.add(new Pair(l, 0));
                                arrayList.add(l);
                            } else {
                                messagesController.getChat(Long.valueOf(-l.longValue()));
                                arrayList3.add(new Pair(l, 0));
                                arrayList2.add(l);
                            }
                        }
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                if (ChatObject.isChannel(chat)) {
                    TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
                    tL_channels_getParticipants.limit = MessagesController.getInstance(i).chatReadMarkSizeThreshold;
                    tL_channels_getParticipants.offset = 0;
                    tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
                    tL_channels_getParticipants.channel = MessagesController.getInstance(i).getInputChannel(chat.id);
                    ConnectionsManager connectionsManager2 = ConnectionsManager.getInstance(i);
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                            MessageSeenView.this.lambda$new$1(i, hashMap, arrayList3, tLObject2, tL_error2);
                        }
                    };
                    connectionsManager = connectionsManager2;
                    tL_messages_getFullChat = tL_channels_getParticipants;
                } else {
                    TLRPC.TL_messages_getFullChat tL_messages_getFullChat2 = new TLRPC.TL_messages_getFullChat();
                    tL_messages_getFullChat2.chat_id = chat.id;
                    ConnectionsManager connectionsManager3 = ConnectionsManager.getInstance(i);
                    requestDelegate = new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                            MessageSeenView.this.lambda$new$3(i, hashMap, arrayList3, tLObject2, tL_error2);
                        }
                    };
                    connectionsManager = connectionsManager3;
                    tL_messages_getFullChat = tL_messages_getFullChat2;
                }
                connectionsManager.sendRequest(tL_messages_getFullChat, requestDelegate);
                return;
            }
            for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                Pair pair = (Pair) arrayList3.get(i4);
                this.peerIds.add((Long) pair.first);
                this.dates.add((Integer) pair.second);
                this.users.add((TLObject) hashMap.get(pair.first));
            }
        }
        updateView();
    }

    public void lambda$new$5(final long j, final int i, final TLRPC.Chat chat, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessageSeenView.this.lambda$new$4(tL_error, tLObject, j, i, chat);
            }
        });
    }

    private void updateView() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MessageSeenView.updateView():void");
    }

    public RecyclerListView createListView() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            return recyclerListView;
        }
        RecyclerListView recyclerListView2 = new RecyclerListView(getContext()) {
            @Override
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i2);
                int dp = AndroidUtilities.dp(4.0f) + (AndroidUtilities.dp(50.0f) * getAdapter().getItemCount());
                if (dp <= size) {
                    size = dp;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, 1073741824));
            }
        };
        this.listView = recyclerListView2;
        recyclerListView2.setLayoutManager(new LinearLayoutManager(getContext()));
        this.listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                if (recyclerView.getChildAdapterPosition(view) == MessageSeenView.this.users.size() - 1) {
                    rect.bottom = AndroidUtilities.dp(4.0f);
                }
            }
        });
        this.listView.setAdapter(new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                return MessageSeenView.this.users.size();
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((UserCell) viewHolder.itemView).setUser((TLObject) MessageSeenView.this.users.get(i), ((Integer) MessageSeenView.this.dates.get(i)).intValue());
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                UserCell userCell = new UserCell(viewGroup.getContext());
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(50.0f)));
                return new RecyclerListView.Holder(userCell);
            }
        });
        return this.listView;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        View view = (View) getParent();
        if (view != null && view.getWidth() > 0) {
            i = View.MeasureSpec.makeMeasureSpec(view.getWidth(), 1073741824);
        }
        this.ignoreLayout = true;
        boolean z = this.flickerLoadingView.getVisibility() == 0;
        this.titleView.setVisibility(8);
        if (z) {
            this.flickerLoadingView.setVisibility(8);
        }
        super.onMeasure(i, i2);
        if (z) {
            this.flickerLoadingView.getLayoutParams().width = getMeasuredWidth();
            this.flickerLoadingView.setVisibility(0);
        }
        this.titleView.setVisibility(0);
        this.titleView.getLayoutParams().width = getMeasuredWidth() - AndroidUtilities.dp(40.0f);
        this.ignoreLayout = false;
        super.onMeasure(i, i2);
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }
}
