package org.telegram.ui.Components;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;

public interface ChatActivityInterface {

    public abstract class CC {
        public static void $default$checkAndUpdateAvatar(ChatActivityInterface chatActivityInterface) {
        }

        public static TLRPC.User $default$getCurrentUser(ChatActivityInterface chatActivityInterface) {
            return null;
        }

        public static long $default$getMergeDialogId(ChatActivityInterface chatActivityInterface) {
            return 0L;
        }

        public static long $default$getTopicId(ChatActivityInterface chatActivityInterface) {
            return 0L;
        }

        public static boolean $default$openedWithLivestream(ChatActivityInterface chatActivityInterface) {
            return false;
        }

        public static void $default$scrollToMessageId(ChatActivityInterface chatActivityInterface, int i, int i2, boolean z, int i3, boolean z2, int i4) {
        }

        public static boolean $default$shouldShowImport(ChatActivityInterface chatActivityInterface) {
            return false;
        }
    }

    void checkAndUpdateAvatar();

    ActionBar getActionBar();

    ChatAvatarContainer getAvatarContainer();

    SizeNotifierFrameLayout getContentView();

    TLRPC.Chat getCurrentChat();

    TLRPC.User getCurrentUser();

    long getDialogId();

    ChatObject.Call getGroupCall();

    long getMergeDialogId();

    Theme.ResourcesProvider getResourceProvider();

    long getTopicId();

    boolean openedWithLivestream();

    void scrollToMessageId(int i, int i2, boolean z, int i3, boolean z2, int i4);

    boolean shouldShowImport();
}
