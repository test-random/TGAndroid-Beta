package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class MemberRequestsController extends BaseController {
    private static final MemberRequestsController[] instances = new MemberRequestsController[4];
    private final LongSparseArray<TLRPC.TL_messages_chatInviteImporters> firstImportersCache;

    public MemberRequestsController(int i) {
        super(i);
        this.firstImportersCache = new LongSparseArray<>();
    }

    public static MemberRequestsController getInstance(int i) {
        MemberRequestsController[] memberRequestsControllerArr = instances;
        MemberRequestsController memberRequestsController = memberRequestsControllerArr[i];
        if (memberRequestsController == null) {
            synchronized (MemberRequestsController.class) {
                try {
                    memberRequestsController = memberRequestsControllerArr[i];
                    if (memberRequestsController == null) {
                        memberRequestsController = new MemberRequestsController(i);
                        memberRequestsControllerArr[i] = memberRequestsController;
                    }
                } finally {
                }
            }
        }
        return memberRequestsController;
    }

    public void lambda$getImporters$0(TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_chatInviteImporter tL_chatInviteImporter, boolean z, long j, RequestDelegate requestDelegate) {
        if (tL_error == null) {
            TLRPC.TL_messages_chatInviteImporters tL_messages_chatInviteImporters = (TLRPC.TL_messages_chatInviteImporters) tLObject;
            if (tL_chatInviteImporter == null && z) {
                this.firstImportersCache.put(j, tL_messages_chatInviteImporters);
            }
        }
        requestDelegate.run(tLObject, tL_error);
    }

    public void lambda$getImporters$1(final TLRPC.TL_chatInviteImporter tL_chatInviteImporter, final boolean z, final long j, final RequestDelegate requestDelegate, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MemberRequestsController.this.lambda$getImporters$0(tL_error, tLObject, tL_chatInviteImporter, z, j, requestDelegate);
            }
        });
    }

    public TLRPC.TL_messages_chatInviteImporters getCachedImporters(long j) {
        return this.firstImportersCache.get(j);
    }

    public int getImporters(final long j, String str, final TLRPC.TL_chatInviteImporter tL_chatInviteImporter, LongSparseArray<TLRPC.User> longSparseArray, final RequestDelegate requestDelegate) {
        final boolean isEmpty = TextUtils.isEmpty(str);
        TLRPC.TL_messages_getChatInviteImporters tL_messages_getChatInviteImporters = new TLRPC.TL_messages_getChatInviteImporters();
        tL_messages_getChatInviteImporters.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-j);
        tL_messages_getChatInviteImporters.requested = true;
        tL_messages_getChatInviteImporters.limit = 30;
        if (!isEmpty) {
            tL_messages_getChatInviteImporters.q = str;
            tL_messages_getChatInviteImporters.flags |= 4;
        }
        if (tL_chatInviteImporter == null) {
            tL_messages_getChatInviteImporters.offset_user = new TLRPC.TL_inputUserEmpty();
        } else {
            tL_messages_getChatInviteImporters.offset_user = getMessagesController().getInputUser(longSparseArray.get(tL_chatInviteImporter.user_id));
            tL_messages_getChatInviteImporters.offset_date = tL_chatInviteImporter.date;
        }
        return getConnectionsManager().sendRequest(tL_messages_getChatInviteImporters, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MemberRequestsController.this.lambda$getImporters$1(tL_chatInviteImporter, isEmpty, j, requestDelegate, tLObject, tL_error);
            }
        });
    }

    public void onPendingRequestsUpdated(TLRPC.TL_updatePendingJoinRequests tL_updatePendingJoinRequests) {
        long j = -MessageObject.getPeerId(tL_updatePendingJoinRequests.peer);
        this.firstImportersCache.put(j, null);
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(j);
        if (chatFull != null) {
            chatFull.requests_pending = tL_updatePendingJoinRequests.requests_pending;
            chatFull.recent_requesters = tL_updatePendingJoinRequests.recent_requesters;
            chatFull.flags |= 131072;
            getMessagesStorage().updateChatInfo(chatFull, false);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i = NotificationCenter.chatInfoDidLoad;
            Boolean bool = Boolean.FALSE;
            notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
        }
    }
}
