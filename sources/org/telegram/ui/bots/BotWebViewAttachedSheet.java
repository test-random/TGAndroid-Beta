package org.telegram.ui.bots;

import androidx.activity.result.ActivityResultRegistry$$ExternalSyntheticThrowCCEIfNotNull0;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.LaunchActivity;

public abstract class BotWebViewAttachedSheet implements NotificationCenter.NotificationCenterDelegate, BaseFragment.AttachedSheet, BottomSheetTabsOverlay.Sheet {
    private static final SimpleFloatPropertyCompat ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float lambda$static$1;
            ActivityResultRegistry$$ExternalSyntheticThrowCCEIfNotNull0.m(obj);
            lambda$static$1 = BotWebViewAttachedSheet.lambda$static$1(null);
            return lambda$static$1;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            ActivityResultRegistry$$ExternalSyntheticThrowCCEIfNotNull0.m(obj);
            BotWebViewAttachedSheet.lambda$static$2(null, f);
        }
    }).setMultiplier(100.0f);

    public static boolean hasPrivacyCommand(TLRPC.UserFull userFull) {
        TL_bots.BotInfo botInfo;
        if (userFull == null || (botInfo = userFull.bot_info) == null) {
            return false;
        }
        if (botInfo.privacy_policy_url != null) {
            return true;
        }
        Iterator<TLRPC.TL_botCommand> it = botInfo.commands.iterator();
        while (it.hasNext()) {
            if ("privacy".equals(it.next().command)) {
                return true;
            }
        }
        return false;
    }

    public static void lambda$openPrivacy$37(int i, long j) {
        SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of("/privacy", j, null, null, null, false, null, null, null, true, 0, null, false));
    }

    public static float lambda$static$1(BotWebViewAttachedSheet botWebViewAttachedSheet) {
        throw null;
    }

    public static void lambda$static$2(BotWebViewAttachedSheet botWebViewAttachedSheet, float f) {
        throw null;
    }

    public static boolean openPrivacy(final int i, final long j) {
        TL_bots.BotInfo botInfo;
        TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j);
        if (userFull == null || (botInfo = userFull.bot_info) == null) {
            return false;
        }
        String str = botInfo.privacy_policy_url;
        if (str == null && !hasPrivacyCommand(userFull)) {
            str = LocaleController.getString(R.string.BotDefaultPrivacyPolicy);
        }
        if (str != null) {
            Browser.openUrl(ApplicationLoader.applicationContext, str);
            return false;
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return false;
        }
        if (!(safeLastFragment instanceof ChatActivity) || ((ChatActivity) safeLastFragment).getDialogId() != j) {
            safeLastFragment.presentFragment(ChatActivity.of(j));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewAttachedSheet.lambda$openPrivacy$37(i, j);
            }
        }, 150L);
        return true;
    }
}
