package org.telegram.ui.Components;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.TopicsFragment;

public abstract class BackButtonMenu {

    public static class PulledDialog {
        Class activity;
        TLRPC.Chat chat;
        long dialogId;
        int filterId;
        int folderId;
        int stackIndex;
        TLRPC.TL_forumTopic topic;
        TLRPC.User user;
    }

    public static void addToPulledDialogs(BaseFragment baseFragment, int i, TLRPC.Chat chat, TLRPC.User user, TLRPC.TL_forumTopic tL_forumTopic, long j, int i2, int i3) {
        INavigationLayout parentLayout;
        TLRPC.TL_forumTopic tL_forumTopic2;
        if ((chat == null && user == null) || baseFragment == null || (parentLayout = baseFragment.getParentLayout()) == null) {
            return;
        }
        if (parentLayout.getPulledDialogs() == null) {
            parentLayout.setPulledDialogs(new ArrayList());
        }
        for (PulledDialog pulledDialog : parentLayout.getPulledDialogs()) {
            if (tL_forumTopic == null && pulledDialog.dialogId == j) {
                return;
            }
            if (tL_forumTopic != null && (tL_forumTopic2 = pulledDialog.topic) != null && tL_forumTopic2.id == tL_forumTopic.id) {
                return;
            }
        }
        PulledDialog pulledDialog2 = new PulledDialog();
        pulledDialog2.activity = ChatActivity.class;
        pulledDialog2.stackIndex = i;
        pulledDialog2.dialogId = j;
        pulledDialog2.filterId = i3;
        pulledDialog2.folderId = i2;
        pulledDialog2.chat = chat;
        pulledDialog2.user = user;
        pulledDialog2.topic = tL_forumTopic;
        parentLayout.getPulledDialogs().add(pulledDialog2);
    }

    public static void clearPulledDialogs(BaseFragment baseFragment, int i) {
        INavigationLayout parentLayout;
        if (baseFragment == null || (parentLayout = baseFragment.getParentLayout()) == null || parentLayout.getPulledDialogs() == null) {
            return;
        }
        int i2 = 0;
        while (i2 < parentLayout.getPulledDialogs().size()) {
            if (((PulledDialog) parentLayout.getPulledDialogs().get(i2)).stackIndex > i) {
                parentLayout.getPulledDialogs().remove(i2);
                i2--;
            }
            i2++;
        }
    }

    public static java.util.ArrayList getStackedHistoryDialogs(org.telegram.ui.ActionBar.BaseFragment r17, long r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackButtonMenu.getStackedHistoryDialogs(org.telegram.ui.ActionBar.BaseFragment, long):java.util.ArrayList");
    }

    private static ArrayList getStackedHistoryForTopic(BaseFragment baseFragment, long j, long j2) {
        INavigationLayout parentLayout;
        int i;
        PulledDialog pulledDialog;
        ArrayList arrayList = new ArrayList();
        if (baseFragment == null || (parentLayout = baseFragment.getParentLayout()) == null) {
            return arrayList;
        }
        List pulledDialogs = parentLayout.getPulledDialogs();
        if (pulledDialogs != null) {
            i = -1;
            for (int i2 = 0; i2 < pulledDialogs.size(); i2++) {
                PulledDialog pulledDialog2 = (PulledDialog) pulledDialogs.get(i2);
                if (pulledDialog2.topic != null && r7.id != j2) {
                    int i3 = pulledDialog2.stackIndex;
                    if (i3 >= i) {
                        i = i3;
                    }
                    arrayList.add(pulledDialog2);
                }
            }
        } else {
            i = -1;
        }
        if (parentLayout.getFragmentStack().size() <= 1 || !(parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 2) instanceof TopicsFragment)) {
            pulledDialog = new PulledDialog();
        } else {
            PulledDialog pulledDialog3 = new PulledDialog();
            arrayList.add(pulledDialog3);
            pulledDialog3.stackIndex = i + 1;
            pulledDialog3.activity = DialogsActivity.class;
            pulledDialog = new PulledDialog();
        }
        arrayList.add(pulledDialog);
        pulledDialog.stackIndex = -1;
        pulledDialog.activity = TopicsFragment.class;
        pulledDialog.chat = MessagesController.getInstance(baseFragment.getCurrentAccount()).getChat(Long.valueOf(-j));
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$getStackedHistoryForTopic$1;
                lambda$getStackedHistoryForTopic$1 = BackButtonMenu.lambda$getStackedHistoryForTopic$1((BackButtonMenu.PulledDialog) obj, (BackButtonMenu.PulledDialog) obj2);
                return lambda$getStackedHistoryForTopic$1;
            }
        });
        return arrayList;
    }

    public static void goToPulledDialog(BaseFragment baseFragment, PulledDialog pulledDialog) {
        if (pulledDialog == null) {
            return;
        }
        Class cls = pulledDialog.activity;
        if (cls == ChatActivity.class) {
            Bundle bundle = new Bundle();
            TLRPC.Chat chat = pulledDialog.chat;
            if (chat != null) {
                bundle.putLong("chat_id", chat.id);
            } else {
                TLRPC.User user = pulledDialog.user;
                if (user != null) {
                    bundle.putLong("user_id", user.id);
                }
            }
            bundle.putInt("dialog_folder_id", pulledDialog.folderId);
            bundle.putInt("dialog_filter_id", pulledDialog.filterId);
            TLRPC.TL_forumTopic tL_forumTopic = pulledDialog.topic;
            baseFragment.presentFragment(tL_forumTopic != null ? ForumUtilities.getChatActivityForTopic(baseFragment, pulledDialog.chat.id, tL_forumTopic, 0, bundle) : new ChatActivity(bundle), true);
        } else if (cls == ProfileActivity.class) {
            Bundle bundle2 = new Bundle();
            bundle2.putLong("dialog_id", pulledDialog.dialogId);
            baseFragment.presentFragment(new ProfileActivity(bundle2), true);
        }
        if (pulledDialog.activity == TopicsFragment.class) {
            Bundle bundle3 = new Bundle();
            bundle3.putLong("chat_id", pulledDialog.chat.id);
            baseFragment.presentFragment(new TopicsFragment(bundle3), true);
        }
        if (pulledDialog.activity == DialogsActivity.class) {
            baseFragment.presentFragment(new DialogsActivity(null), true);
        }
    }

    public static int lambda$getStackedHistoryDialogs$2(PulledDialog pulledDialog, PulledDialog pulledDialog2) {
        return pulledDialog2.stackIndex - pulledDialog.stackIndex;
    }

    public static int lambda$getStackedHistoryForTopic$1(PulledDialog pulledDialog, PulledDialog pulledDialog2) {
        return pulledDialog2.stackIndex - pulledDialog.stackIndex;
    }

    public static void lambda$show$0(java.util.concurrent.atomic.AtomicReference r4, org.telegram.ui.Components.BackButtonMenu.PulledDialog r5, org.telegram.ui.ActionBar.INavigationLayout r6, org.telegram.tgnet.TLRPC.TL_forumTopic r7, org.telegram.ui.ActionBar.BaseFragment r8, android.view.View r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackButtonMenu.lambda$show$0(java.util.concurrent.atomic.AtomicReference, org.telegram.ui.Components.BackButtonMenu$PulledDialog, org.telegram.ui.ActionBar.INavigationLayout, org.telegram.tgnet.TLRPC$TL_forumTopic, org.telegram.ui.ActionBar.BaseFragment, android.view.View):void");
    }

    public static org.telegram.ui.ActionBar.ActionBarPopupWindow show(final org.telegram.ui.ActionBar.BaseFragment r28, android.view.View r29, long r30, long r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackButtonMenu.show(org.telegram.ui.ActionBar.BaseFragment, android.view.View, long, long, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.ActionBarPopupWindow");
    }
}
