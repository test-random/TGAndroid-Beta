package org.telegram.ui.Stories;

import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.LongSparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_userStatusEmpty;
import org.telegram.tgnet.TLRPC$TL_users_getStoriesMaxIDs;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.UserListPoller;
public class UserListPoller {
    private static UserListPoller[] istances = new UserListPoller[4];
    final int currentAccount;
    Runnable requestCollectedRunnables;
    LongSparseLongArray userPollLastTime = new LongSparseLongArray();
    ArrayList<Long> dialogIds = new ArrayList<>();
    ArrayList<Long> collectedDialogIds = new ArrayList<>();

    private UserListPoller(int i) {
        new ArrayList();
        this.requestCollectedRunnables = new AnonymousClass1();
        this.currentAccount = i;
    }

    public static UserListPoller getInstance(int i) {
        UserListPoller[] userListPollerArr = istances;
        if (userListPollerArr[i] == null) {
            userListPollerArr[i] = new UserListPoller(i);
        }
        return istances[i];
    }

    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override
        public void run() {
            if (UserListPoller.this.collectedDialogIds.isEmpty()) {
                return;
            }
            final ArrayList arrayList = new ArrayList(UserListPoller.this.collectedDialogIds);
            UserListPoller.this.collectedDialogIds.clear();
            TLRPC$TL_users_getStoriesMaxIDs tLRPC$TL_users_getStoriesMaxIDs = new TLRPC$TL_users_getStoriesMaxIDs();
            for (int i = 0; i < arrayList.size(); i++) {
                tLRPC$TL_users_getStoriesMaxIDs.id.add(MessagesController.getInstance(UserListPoller.this.currentAccount).getInputUser(((Long) arrayList.get(i)).longValue()));
            }
            ConnectionsManager.getInstance(UserListPoller.this.currentAccount).sendRequest(tLRPC$TL_users_getStoriesMaxIDs, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    UserListPoller.AnonymousClass1.this.lambda$run$1(arrayList, tLObject, tLRPC$TL_error);
                }
            });
        }

        public void lambda$run$1(final ArrayList arrayList, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    UserListPoller.AnonymousClass1.this.lambda$run$0(tLObject, arrayList);
                }
            });
        }

        public void lambda$run$0(TLObject tLObject, ArrayList arrayList) {
            if (tLObject != null) {
                TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
                ArrayList arrayList2 = new ArrayList();
                for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                    TLRPC$User user = MessagesController.getInstance(UserListPoller.this.currentAccount).getUser((Long) arrayList.get(i));
                    if (user != null) {
                        int intValue = ((Integer) tLRPC$Vector.objects.get(i)).intValue();
                        user.stories_max_id = intValue;
                        if (intValue != 0) {
                            user.flags2 |= 32;
                        } else {
                            user.flags2 &= -33;
                        }
                        arrayList2.add(user);
                    }
                }
                MessagesStorage.getInstance(UserListPoller.this.currentAccount).putUsersAndChats(arrayList2, null, true, true);
                NotificationCenter.getInstance(UserListPoller.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
            }
        }
    }

    public void checkList(RecyclerListView recyclerListView) {
        long dialogId;
        TLRPC$User user;
        TLRPC$UserStatus tLRPC$UserStatus;
        long currentTimeMillis = System.currentTimeMillis();
        this.dialogIds.clear();
        for (int i = 0; i < recyclerListView.getChildCount(); i++) {
            View childAt = recyclerListView.getChildAt(i);
            if (childAt instanceof DialogCell) {
                dialogId = ((DialogCell) childAt).getDialogId();
            } else {
                dialogId = childAt instanceof UserCell ? ((UserCell) childAt).getDialogId() : 0L;
            }
            if (dialogId > 0 && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId))) != null && !user.bot && !user.self && !user.contact && (tLRPC$UserStatus = user.status) != null && !(tLRPC$UserStatus instanceof TLRPC$TL_userStatusEmpty) && currentTimeMillis - this.userPollLastTime.get(dialogId, 0L) > 3600000) {
                this.userPollLastTime.put(dialogId, currentTimeMillis);
                this.dialogIds.add(Long.valueOf(dialogId));
            }
        }
        if (this.dialogIds.isEmpty()) {
            return;
        }
        this.collectedDialogIds.addAll(this.dialogIds);
        AndroidUtilities.cancelRunOnUIThread(this.requestCollectedRunnables);
        AndroidUtilities.runOnUIThread(this.requestCollectedRunnables, 300L);
    }
}