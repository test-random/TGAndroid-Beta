package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;

public class JoinGroupAlert extends BottomSheet {
    private TLRPC.ChatInvite chatInvite;
    private TLRPC.Chat currentChat;
    private final BaseFragment fragment;
    private final String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    public JoinGroupAlert(Context context, TLObject tLObject, String str, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        this(context, tLObject, str, baseFragment, resourcesProvider, -1);
    }

    public JoinGroupAlert(android.content.Context r28, org.telegram.tgnet.TLObject r29, java.lang.String r30, org.telegram.ui.ActionBar.BaseFragment r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32, final int r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLObject, java.lang.String, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.Theme$ResourcesProvider, int):void");
    }

    private CharSequence ellipsize(TextView textView, TLRPC.ChatInvite chatInvite, int i) {
        String str = chatInvite.participants.get(i).first_name;
        if (str == null) {
            str = "";
        }
        return TextUtils.ellipsize(str.trim(), textView.getPaint(), AndroidUtilities.dp(120.0f), TextUtils.TruncateAt.END);
    }

    private Drawable getScamDrawable(int i) {
        return i == 0 ? Theme.dialogs_scamDrawable : Theme.dialogs_fakeDrawable;
    }

    private Drawable getVerifiedCrossfadeDrawable() {
        return new CombinedDrawable(Theme.dialogs_verifiedDrawable, Theme.dialogs_verifiedCheckDrawable);
    }

    public void lambda$new$0(View view) {
        dismiss();
    }

    public void lambda$new$1() {
        if (isDismissed()) {
            return;
        }
        this.requestTextView.setVisibility(4);
        this.requestProgressView.setVisibility(0);
    }

    public void lambda$new$10(final int i, View view) {
        dismiss();
        final TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite = new TLRPC.TL_messages_importChatInvite();
        tL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_importChatInvite, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinGroupAlert.this.lambda$new$9(i, tL_messages_importChatInvite, tLObject, tL_error);
            }
        }, 2);
    }

    public void lambda$new$2(boolean z, DialogInterface dialogInterface) {
        showBulletin(getContext(), this.fragment, z);
    }

    public boolean lambda$new$3(final boolean z, TLRPC.TL_error tL_error) {
        if (tL_error != null && "INVITE_REQUEST_SENT".equals(tL_error.text)) {
            setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    JoinGroupAlert.this.lambda$new$2(z, dialogInterface);
                }
            });
        }
        dismiss();
        return false;
    }

    public void lambda$new$4(boolean z, DialogInterface dialogInterface) {
        showBulletin(getContext(), this.fragment, z);
    }

    public void lambda$new$5(TLRPC.TL_error tL_error, final boolean z, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (tL_error != null) {
            if ("INVITE_REQUEST_SENT".equals(tL_error.text)) {
                setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        JoinGroupAlert.this.lambda$new$4(z, dialogInterface);
                    }
                });
            } else {
                AlertsCreator.processError(this.currentAccount, tL_error, this.fragment, tL_messages_importChatInvite, new Object[0]);
            }
        }
        dismiss();
    }

    public void lambda$new$6(final boolean z, final TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                JoinGroupAlert.this.lambda$new$5(tL_error, z, tL_messages_importChatInvite);
            }
        });
    }

    public void lambda$new$7(final boolean z, View view) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                JoinGroupAlert.this.lambda$new$1();
            }
        }, 400L);
        if (this.chatInvite == null && this.currentChat != null) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), 0, null, null, true, new Runnable() {
                @Override
                public final void run() {
                    JoinGroupAlert.this.dismiss();
                }
            }, new MessagesController.ErrorDelegate() {
                @Override
                public final boolean run(TLRPC.TL_error tL_error) {
                    boolean lambda$new$3;
                    lambda$new$3 = JoinGroupAlert.this.lambda$new$3(z, tL_error);
                    return lambda$new$3;
                }
            });
            return;
        }
        final TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite = new TLRPC.TL_messages_importChatInvite();
        tL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_importChatInvite, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinGroupAlert.this.lambda$new$6(z, tL_messages_importChatInvite, tLObject, tL_error);
            }
        }, 2);
    }

    public void lambda$new$8(TLRPC.TL_error tL_error, TLObject tLObject, int i, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        TLRPC.ChatInvite chatInvite;
        TLRPC.Chat chat;
        long j;
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (tL_error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            if (updates.chats.isEmpty()) {
                return;
            }
            TLRPC.Chat chat2 = updates.chats.get(0);
            chat2.left = false;
            chat2.kicked = false;
            MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
            j = chat2.id;
        } else {
            if (!"USER_ALREADY_PARTICIPANT".equals(tL_error.text) || i != 0 || (chatInvite = this.chatInvite) == null || (chat = chatInvite.chat) == null) {
                AlertsCreator.processError(this.currentAccount, tL_error, this.fragment, tL_messages_importChatInvite, new Object[0]);
                return;
            }
            j = chat.id;
        }
        openChat(j);
    }

    public void lambda$new$9(final int i, final TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                JoinGroupAlert.this.lambda$new$8(tL_error, tLObject, i, tL_messages_importChatInvite);
            }
        });
    }

    private void openChat(long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", j);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this.fragment)) {
            ChatActivity chatActivity = new ChatActivity(bundle);
            BaseFragment baseFragment = this.fragment;
            baseFragment.presentFragment(chatActivity, baseFragment instanceof ChatActivity);
        }
    }

    public static void showBulletin(Context context, BaseFragment baseFragment, boolean z) {
        if (context == null) {
            if (baseFragment != null) {
                baseFragment.getContext();
            }
        } else {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(context, baseFragment.getResourceProvider());
            twoLineLottieLayout.imageView.setAnimation(R.raw.timer_3, 28, 28);
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString(R.string.RequestToJoinSent));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString(z ? R.string.RequestToJoinChannelSentDescription : R.string.RequestToJoinGroupSentDescription));
            Bulletin.make(baseFragment, twoLineLottieLayout, 2750).show();
        }
    }
}
