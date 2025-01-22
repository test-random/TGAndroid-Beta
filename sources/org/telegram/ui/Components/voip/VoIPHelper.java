package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.JoinCallByUrlAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;

public abstract class VoIPHelper {
    public static long lastCallTime;

    public static boolean canRateCall(TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall) {
        TLRPC.PhoneCallDiscardReason phoneCallDiscardReason = tL_messageActionPhoneCall.reason;
        if (!(phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) && !(phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
            Iterator<String> it = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET).iterator();
            while (it.hasNext()) {
                String[] split = it.next().split(" ");
                if (split.length >= 2) {
                    if (split[0].equals(tL_messageActionPhoneCall.call_id + "")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void doInitiateCall(TLRPC.User user, TLRPC.Chat chat, String str, TLRPC.InputPeer inputPeer, boolean z, boolean z2, boolean z3, boolean z4, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance, boolean z5, boolean z6) {
        doInitiateCall(user, chat, str, inputPeer, z, z2, z3, z4, activity, baseFragment, accountInstance, z5, z6, false);
    }

    public static void doInitiateCall(final TLRPC.User user, final TLRPC.Chat chat, final String str, final TLRPC.InputPeer inputPeer, boolean z, final boolean z2, final boolean z3, final boolean z4, final Activity activity, final BaseFragment baseFragment, final AccountInstance accountInstance, boolean z5, boolean z6, boolean z7) {
        long j;
        String str2;
        ChatObject.Call groupCall;
        TLRPC.ChatFull chatFull;
        int i;
        int i2;
        TLRPC.ChatFull chatFull2;
        TLRPC.Peer peer;
        if (activity == null) {
            return;
        }
        if (user == null && chat == null) {
            return;
        }
        if (SystemClock.elapsedRealtime() - lastCallTime < (chat != null ? 200 : 2000)) {
            return;
        }
        if (z5 && chat != null && !z4 && (chatFull2 = accountInstance.getMessagesController().getChatFull(chat.id)) != null && (peer = chatFull2.groupcall_default_join_as) != null) {
            final TLRPC.InputPeer inputPeer2 = accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(peer));
            JoinCallAlert.checkFewUsers(activity, -chat.id, accountInstance, new MessagesStorage.BooleanCallback() {
                @Override
                public final void run(boolean z8) {
                    VoIPHelper.lambda$doInitiateCall$4(str, activity, chat, user, inputPeer2, z2, z3, baseFragment, accountInstance, z8);
                }
            });
            return;
        }
        if (z5 && chat != null) {
            JoinCallAlert.open(activity, -chat.id, accountInstance, baseFragment, !z4 ? 1 : 0, null, new JoinCallAlert.JoinCallAlertDelegate() {
                @Override
                public final void didSelectChat(TLRPC.InputPeer inputPeer3, boolean z8, boolean z9, boolean z10) {
                    VoIPHelper.lambda$doInitiateCall$5(z4, activity, accountInstance, chat, str, user, z2, z3, baseFragment, inputPeer3, z8, z9, z10);
                }
            });
            return;
        }
        if (z6 && !z && (inputPeer instanceof TLRPC.TL_inputPeerUser) && ChatObject.shouldSendAnonymously(chat) && (!ChatObject.isChannel(chat) || chat.megagroup)) {
            new AlertDialog.Builder(activity).setTitle(LocaleController.getString(ChatObject.isChannelOrGiga(chat) ? R.string.VoipChannelVoiceChat : R.string.VoipGroupVoiceChat)).setMessage(LocaleController.getString(ChatObject.isChannelOrGiga(chat) ? R.string.VoipChannelJoinAnonymouseAlert : R.string.VoipGroupJoinAnonymouseAlert)).setPositiveButton(LocaleController.getString(R.string.VoipChatJoin), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i3) {
                    VoIPHelper.doInitiateCall(TLRPC.User.this, chat, str, inputPeer, false, z2, z3, z4, activity, baseFragment, accountInstance, false, false);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
            return;
        }
        if (chat != null && inputPeer != null && (chatFull = accountInstance.getMessagesController().getChatFull(chat.id)) != null) {
            if (inputPeer instanceof TLRPC.TL_inputPeerUser) {
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                chatFull.groupcall_default_join_as = tL_peerUser;
                tL_peerUser.user_id = inputPeer.user_id;
            } else if (inputPeer instanceof TLRPC.TL_inputPeerChat) {
                TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
                chatFull.groupcall_default_join_as = tL_peerChat;
                tL_peerChat.chat_id = inputPeer.chat_id;
            } else if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                chatFull.groupcall_default_join_as = tL_peerChannel;
                tL_peerChannel.channel_id = inputPeer.channel_id;
            }
            if (chatFull instanceof TLRPC.TL_chatFull) {
                i = chatFull.flags;
                i2 = 32768;
            } else {
                i = chatFull.flags;
                i2 = 67108864;
            }
            chatFull.flags = i | i2;
        }
        if (chat != null && !z4 && (groupCall = accountInstance.getMessagesController().getGroupCall(chat.id, false)) != null && groupCall.isScheduled()) {
            GroupCallActivity.create((LaunchActivity) activity, accountInstance, chat, inputPeer, z, str);
            return;
        }
        lastCallTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(activity, (Class<?>) VoIPService.class);
        try {
            if (user == null) {
                intent.putExtra("chat_id", chat.id);
                intent.putExtra("createGroupCall", z4);
                intent.putExtra("hasFewPeers", z);
                intent.putExtra("isRtmpStream", z7);
                intent.putExtra("hash", str);
                if (inputPeer != null) {
                    intent.putExtra("peerChannelId", inputPeer.channel_id);
                    intent.putExtra("peerChatId", inputPeer.chat_id);
                    intent.putExtra("peerUserId", inputPeer.user_id);
                    j = inputPeer.access_hash;
                    str2 = "peerAccessHash";
                }
                intent.putExtra("is_outgoing", true);
                intent.putExtra("start_incall_activity", true);
                intent.putExtra("video_call", z2);
                intent.putExtra("can_video_call", z3);
                intent.putExtra("account", UserConfig.selectedAccount);
                activity.startService(intent);
                return;
            }
            j = user.id;
            str2 = "user_id";
            activity.startService(intent);
            return;
        } catch (Throwable th) {
            FileLog.e(th);
            return;
        }
        intent.putExtra(str2, j);
        intent.putExtra("is_outgoing", true);
        intent.putExtra("start_incall_activity", true);
        intent.putExtra("video_call", z2);
        intent.putExtra("can_video_call", z3);
        intent.putExtra("account", UserConfig.selectedAccount);
    }

    public static int getDataSavingDefault() {
        boolean z = DownloadController.getInstance(0).lowPreset.lessCallData;
        boolean z2 = DownloadController.getInstance(0).mediumPreset.lessCallData;
        boolean z3 = DownloadController.getInstance(0).highPreset.lessCallData;
        if (!z && !z2 && !z3) {
            return 0;
        }
        if (z && !z2 && !z3) {
            return 3;
        }
        if (z && z2 && !z3) {
            return 1;
        }
        if (z && z2 && z3) {
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("Invalid call data saving preset configuration: " + z + "/" + z2 + "/" + z3);
        }
        return 0;
    }

    private static File getLogFile(long j) {
        File file;
        String[] list;
        if (BuildVars.DEBUG_VERSION && (list = (file = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), "logs")).list()) != null) {
            for (String str : list) {
                if (str.endsWith("voip" + j + ".txt")) {
                    return new File(file, str);
                }
            }
        }
        return new File(getLogsDir(), j + ".log");
    }

    public static String getLogFilePath(long j, boolean z) {
        File file;
        File[] listFiles;
        File logsDir = getLogsDir();
        if (!BuildVars.DEBUG_VERSION && (listFiles = logsDir.listFiles()) != null) {
            ArrayList arrayList = new ArrayList(Arrays.asList(listFiles));
            while (arrayList.size() > 20) {
                File file2 = (File) arrayList.get(0);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    File file3 = (File) it.next();
                    if (file3.getName().endsWith(".log") && file3.lastModified() < file2.lastModified()) {
                        file2 = file3;
                    }
                }
                file2.delete();
                arrayList.remove(file2);
            }
        }
        if (z) {
            file = new File(logsDir, j + "_stats.log");
        } else {
            file = new File(logsDir, j + ".log");
        }
        return file.getAbsolutePath();
    }

    public static String getLogFilePath(String str) {
        Calendar calendar = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(1)), Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12)), Integer.valueOf(calendar.get(13)), str)).getAbsolutePath();
    }

    public static File getLogsDir() {
        File file = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static void initiateCall(final TLRPC.User user, final TLRPC.Chat chat, final String str, final boolean z, final boolean z2, final boolean z3, Boolean bool, final Activity activity, final BaseFragment baseFragment, final AccountInstance accountInstance) {
        String str2;
        int i;
        if (activity != null) {
            if (user == null && chat == null) {
                return;
            }
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance == null) {
                if (VoIPService.callIShouldHavePutIntoIntent == null) {
                    doInitiateCall(user, chat, str, null, false, z, z2, z3, activity, baseFragment, accountInstance, bool != null ? bool.booleanValue() : true, true);
                    return;
                }
                return;
            }
            long j = user != null ? user.id : -chat.id;
            long callerId = VoIPService.getSharedInstance().getCallerId();
            if (callerId == j && sharedInstance.getAccount() == accountInstance.getCurrentAccount()) {
                if (user != null || !(activity instanceof LaunchActivity)) {
                    activity.startActivity(new Intent(activity, (Class<?>) LaunchActivity.class).setAction(user != null ? "voip" : "voip_chat"));
                    return;
                }
                if (!TextUtils.isEmpty(str)) {
                    sharedInstance.setGroupCallHash(str);
                }
                GroupCallActivity.create((LaunchActivity) activity, AccountInstance.getInstance(UserConfig.selectedAccount), null, null, false, null);
                return;
            }
            if (callerId > 0) {
                TLRPC.User user2 = sharedInstance.getUser();
                str2 = ContactsController.formatName(user2.first_name, user2.last_name);
                i = j > 0 ? R.string.VoipOngoingAlert : R.string.VoipOngoingAlert2;
            } else {
                str2 = sharedInstance.getChat().title;
                i = j > 0 ? R.string.VoipOngoingChatAlert2 : R.string.VoipOngoingChatAlert;
            }
            new AlertDialog.Builder(activity).setTitle(LocaleController.getString(callerId < 0 ? R.string.VoipOngoingChatAlertTitle : R.string.VoipOngoingAlertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(i, str2, user != null ? ContactsController.formatName(user.first_name, user.last_name) : chat.title))).setPositiveButton(LocaleController.getString(R.string.OK), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i2) {
                    VoIPHelper.lambda$initiateCall$3(TLRPC.User.this, chat, str, z, z2, z3, activity, baseFragment, accountInstance, alertDialog, i2);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
        }
    }

    public static void lambda$doInitiateCall$4(final String str, final Activity activity, final TLRPC.Chat chat, final TLRPC.User user, final TLRPC.InputPeer inputPeer, final boolean z, final boolean z2, final BaseFragment baseFragment, final AccountInstance accountInstance, boolean z3) {
        if (z3 || str == null) {
            doInitiateCall(user, chat, str, inputPeer, !z3, z, z2, false, activity, baseFragment, accountInstance, false, false);
            return;
        }
        JoinCallByUrlAlert joinCallByUrlAlert = new JoinCallByUrlAlert(activity, chat) {
            @Override
            protected void onJoin() {
                VoIPHelper.doInitiateCall(user, chat, str, inputPeer, true, z, z2, false, activity, baseFragment, accountInstance, false, false);
            }
        };
        if (baseFragment != null) {
            baseFragment.showDialog(joinCallByUrlAlert);
        }
    }

    public static void lambda$doInitiateCall$5(final boolean z, final Activity activity, final AccountInstance accountInstance, final TLRPC.Chat chat, final String str, final TLRPC.User user, final boolean z2, final boolean z3, final BaseFragment baseFragment, final TLRPC.InputPeer inputPeer, boolean z4, boolean z5, final boolean z6) {
        if (z && z5) {
            GroupCallActivity.create((LaunchActivity) activity, accountInstance, chat, inputPeer, z4, str);
            return;
        }
        if (z4 || str == null) {
            doInitiateCall(user, chat, str, inputPeer, z4, z2, z3, z, activity, baseFragment, accountInstance, false, true, z6);
            return;
        }
        JoinCallByUrlAlert joinCallByUrlAlert = new JoinCallByUrlAlert(activity, chat) {
            @Override
            protected void onJoin() {
                VoIPHelper.doInitiateCall(user, chat, str, inputPeer, false, z2, z3, z, activity, baseFragment, accountInstance, false, true, z6);
            }
        };
        if (baseFragment != null) {
            baseFragment.showDialog(joinCallByUrlAlert);
        }
    }

    public static void lambda$initiateCall$2(TLRPC.User user, TLRPC.Chat chat, String str, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        lastCallTime = 0L;
        doInitiateCall(user, chat, str, null, false, z, z2, z3, activity, baseFragment, accountInstance, true, true);
    }

    public static void lambda$initiateCall$3(final TLRPC.User user, final TLRPC.Chat chat, final String str, final boolean z, final boolean z2, final boolean z3, final Activity activity, final BaseFragment baseFragment, final AccountInstance accountInstance, AlertDialog alertDialog, int i) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(new Runnable() {
                @Override
                public final void run() {
                    VoIPHelper.lambda$initiateCall$2(TLRPC.User.this, chat, str, z, z2, z3, activity, baseFragment, accountInstance);
                }
            });
        } else {
            doInitiateCall(user, chat, str, null, false, z, z2, z3, activity, baseFragment, accountInstance, true, true);
        }
    }

    public static void lambda$permissionDenied$7(Activity activity, AlertDialog alertDialog, int i) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    public static void lambda$permissionDenied$8(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$sendCallRating$9(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_updates) {
            MessagesController.getInstance(i).processUpdates((TLRPC.TL_updates) tLObject, false);
        }
    }

    public static void lambda$showCallDebugSettings$18(SharedPreferences sharedPreferences, TextCheckCell textCheckCell, View view) {
        boolean z = sharedPreferences.getBoolean("dbg_force_tcp_in_calls", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        boolean z2 = !z;
        edit.putBoolean("dbg_force_tcp_in_calls", z2);
        edit.commit();
        textCheckCell.setChecked(z2);
    }

    public static void lambda$showCallDebugSettings$19(SharedPreferences sharedPreferences, TextCheckCell textCheckCell, View view) {
        boolean z = sharedPreferences.getBoolean("dbg_dump_call_stats", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        boolean z2 = !z;
        edit.putBoolean("dbg_dump_call_stats", z2);
        edit.commit();
        textCheckCell.setChecked(z2);
    }

    public static void lambda$showCallDebugSettings$20(SharedPreferences sharedPreferences, TextCheckCell textCheckCell, View view) {
        boolean z = sharedPreferences.getBoolean("dbg_force_connection_service", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        boolean z2 = !z;
        edit.putBoolean("dbg_force_connection_service", z2);
        edit.commit();
        textCheckCell.setChecked(z2);
    }

    public static void lambda$showGroupCallAlert$21(TLRPC.Chat chat, TLRPC.InputPeer inputPeer, BaseFragment baseFragment, AccountInstance accountInstance, boolean z) {
        startCall(chat, inputPeer, null, true, baseFragment.getParentActivity(), baseFragment, accountInstance);
    }

    public static void lambda$showRateAlert$10(View view) {
        ((CheckBoxCell) view).setChecked(!r2.isChecked(), true);
    }

    public static void lambda$showRateAlert$11(boolean[] zArr, CheckBoxCell checkBoxCell, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        checkBoxCell.setChecked(z, true);
    }

    public static void lambda$showRateAlert$12(AlertDialog alertDialog, int i) {
    }

    public static void lambda$showRateAlert$13(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$showRateAlert$14(Context context, File file, AlertDialog alertDialog, int i) {
        Intent intent = new Intent(context, (Class<?>) LaunchActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        context.startActivity(intent);
    }

    public static void lambda$showRateAlert$15(View view, int i) {
        view.setEnabled(i > 0);
        ((TextView) view).setText(LocaleController.getString(i < 4 ? R.string.Next : R.string.Send).toUpperCase());
    }

    public static void lambda$showRateAlert$16(int i, boolean[] zArr, File file, TL_phone.setCallRating setcallrating, ArrayList arrayList, Context context, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_updates) {
            MessagesController.getInstance(i).processUpdates((TLRPC.TL_updates) tLObject, false);
        }
        if (zArr[0] && file.exists() && setcallrating.rating < 4) {
            SendMessagesHelper.prepareSendingDocument(AccountInstance.getInstance(UserConfig.selectedAccount), file.getAbsolutePath(), file.getAbsolutePath(), null, TextUtils.join(" ", arrayList), "text/plain", 4244000L, null, null, null, null, null, true, 0, null, null, 0, false);
            Toast.makeText(context, LocaleController.getString(R.string.CallReportSent), 1).show();
        }
    }

    public static void lambda$showRateAlert$17(BetterRatingView betterRatingView, int[] iArr, LinearLayout linearLayout, EditTextBoldCursor editTextBoldCursor, final boolean[] zArr, long j, long j2, boolean z, int i, final File file, final Context context, AlertDialog alertDialog, TextView textView, CheckBoxCell checkBoxCell, TextView textView2, View view, View view2) {
        if (betterRatingView.getRating() < 4 && iArr[0] != 1) {
            iArr[0] = 1;
            betterRatingView.setVisibility(8);
            textView.setVisibility(8);
            alertDialog.setTitle(LocaleController.getString(R.string.CallReportHint));
            editTextBoldCursor.setVisibility(0);
            if (file.exists()) {
                checkBoxCell.setVisibility(0);
                textView2.setVisibility(0);
            }
            linearLayout.setVisibility(0);
            ((TextView) view).setText(LocaleController.getString(R.string.Send).toUpperCase());
            return;
        }
        final int i2 = UserConfig.selectedAccount;
        final TL_phone.setCallRating setcallrating = new TL_phone.setCallRating();
        setcallrating.rating = betterRatingView.getRating();
        final ArrayList arrayList = new ArrayList();
        for (int i3 = 0; i3 < linearLayout.getChildCount(); i3++) {
            CheckBoxCell checkBoxCell2 = (CheckBoxCell) linearLayout.getChildAt(i3);
            if (checkBoxCell2.isChecked()) {
                arrayList.add("#" + checkBoxCell2.getTag());
            }
        }
        setcallrating.comment = setcallrating.rating < 5 ? editTextBoldCursor.getText().toString() : "";
        if (!arrayList.isEmpty() && !zArr[0]) {
            setcallrating.comment += " " + TextUtils.join(" ", arrayList);
        }
        TLRPC.TL_inputPhoneCall tL_inputPhoneCall = new TLRPC.TL_inputPhoneCall();
        setcallrating.peer = tL_inputPhoneCall;
        tL_inputPhoneCall.access_hash = j;
        tL_inputPhoneCall.id = j2;
        setcallrating.user_initiative = z;
        ConnectionsManager.getInstance(i).sendRequest(setcallrating, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPHelper.lambda$showRateAlert$16(i2, zArr, file, setcallrating, arrayList, context, tLObject, tL_error);
            }
        });
        alertDialog.dismiss();
    }

    public static void permissionDenied(final Activity activity, final Runnable runnable, int i) {
        boolean shouldShowRequestPermissionRationale;
        boolean shouldShowRequestPermissionRationale2;
        boolean z = i == 102;
        shouldShowRequestPermissionRationale = activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
        if (shouldShowRequestPermissionRationale) {
            if (!z) {
                return;
            }
            shouldShowRequestPermissionRationale2 = activity.shouldShowRequestPermissionRationale("android.permission.CAMERA");
            if (shouldShowRequestPermissionRationale2) {
                return;
            }
        }
        new AlertDialog.Builder(activity).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(z ? R.string.VoipNeedMicCameraPermissionWithHint : R.string.VoipNeedMicPermissionWithHint))).setPositiveButton(LocaleController.getString(R.string.Settings), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                VoIPHelper.lambda$permissionDenied$7(activity, alertDialog, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                VoIPHelper.lambda$permissionDenied$8(runnable, dialogInterface);
            }
        }).setTopAnimation(z ? R.raw.permission_request_camera : R.raw.permission_request_microphone, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).show();
    }

    public static void sendCallRating(long j, long j2, int i, int i2) {
        final int i3 = UserConfig.selectedAccount;
        TL_phone.setCallRating setcallrating = new TL_phone.setCallRating();
        setcallrating.rating = i2;
        setcallrating.comment = "";
        TLRPC.TL_inputPhoneCall tL_inputPhoneCall = new TLRPC.TL_inputPhoneCall();
        setcallrating.peer = tL_inputPhoneCall;
        tL_inputPhoneCall.access_hash = j2;
        tL_inputPhoneCall.id = j;
        setcallrating.user_initiative = false;
        ConnectionsManager.getInstance(i).sendRequest(setcallrating, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                VoIPHelper.lambda$sendCallRating$9(i3, tLObject, tL_error);
            }
        });
    }

    public static void showCallDebugSettings(Context context) {
        final SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setText("Please only change these settings if you know exactly what they do.");
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 16.0f, 8.0f, 16.0f, 8.0f));
        final TextCheckCell textCheckCell = new TextCheckCell(context);
        textCheckCell.setTextAndCheck("Force TCP", globalMainSettings.getBoolean("dbg_force_tcp_in_calls", false), false);
        textCheckCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPHelper.lambda$showCallDebugSettings$18(globalMainSettings, textCheckCell, view);
            }
        });
        linearLayout.addView(textCheckCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            final TextCheckCell textCheckCell2 = new TextCheckCell(context);
            textCheckCell2.setTextAndCheck("Dump detailed stats", globalMainSettings.getBoolean("dbg_dump_call_stats", false), false);
            textCheckCell2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    VoIPHelper.lambda$showCallDebugSettings$19(globalMainSettings, textCheckCell2, view);
                }
            });
            linearLayout.addView(textCheckCell2);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            final TextCheckCell textCheckCell3 = new TextCheckCell(context);
            textCheckCell3.setTextAndCheck("Enable ConnectionService", globalMainSettings.getBoolean("dbg_force_connection_service", false), false);
            textCheckCell3.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    VoIPHelper.lambda$showCallDebugSettings$20(globalMainSettings, textCheckCell3, view);
                }
            });
            linearLayout.addView(textCheckCell3);
        }
        new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.DebugMenuCallSettings)).setView(linearLayout).show();
    }

    public static void showGroupCallAlert(final BaseFragment baseFragment, final TLRPC.Chat chat, final TLRPC.InputPeer inputPeer, boolean z, final AccountInstance accountInstance) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        JoinCallAlert.checkFewUsers(baseFragment.getParentActivity(), -chat.id, accountInstance, new MessagesStorage.BooleanCallback() {
            @Override
            public final void run(boolean z2) {
                VoIPHelper.lambda$showGroupCallAlert$21(TLRPC.Chat.this, inputPeer, baseFragment, accountInstance, z2);
            }
        });
    }

    public static void showRateAlert(final Context context, final Runnable runnable, boolean z, final long j, final long j2, final int i, final boolean z2) {
        int i2;
        String string;
        final File logFile = getLogFile(j);
        final int[] iArr = {0};
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int dp = AndroidUtilities.dp(16.0f);
        linearLayout.setPadding(dp, dp, dp, 0);
        final TextView textView = new TextView(context);
        textView.setTextSize(2, 16.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setGravity(17);
        textView.setText(LocaleController.getString(R.string.VoipRateCallAlert));
        linearLayout.addView(textView);
        final BetterRatingView betterRatingView = new BetterRatingView(context);
        linearLayout.addView(betterRatingView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        final LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPHelper.lambda$showRateAlert$10(view);
            }
        };
        String[] strArr = {z ? "distorted_video" : null, z ? "pixelated_video" : null, "echo", "noise", "interruptions", "distorted_speech", "silent_local", "silent_remote", "dropped"};
        for (int i3 = 0; i3 < 9; i3++) {
            if (strArr[i3] != null) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(context, 1);
                checkBoxCell.setClipToPadding(false);
                checkBoxCell.setTag(strArr[i3]);
                switch (i3) {
                    case 0:
                        i2 = R.string.RateCallVideoDistorted;
                        break;
                    case 1:
                        i2 = R.string.RateCallVideoPixelated;
                        break;
                    case 2:
                        i2 = R.string.RateCallEcho;
                        break;
                    case 3:
                        i2 = R.string.RateCallNoise;
                        break;
                    case 4:
                        i2 = R.string.RateCallInterruptions;
                        break;
                    case 5:
                        i2 = R.string.RateCallDistorted;
                        break;
                    case 6:
                        i2 = R.string.RateCallSilentLocal;
                        break;
                    case 7:
                        i2 = R.string.RateCallSilentRemote;
                        break;
                    case 8:
                        i2 = R.string.RateCallDropped;
                        break;
                    default:
                        string = null;
                        break;
                }
                string = LocaleController.getString(i2);
                checkBoxCell.setText(string, null, false, false);
                checkBoxCell.setOnClickListener(onClickListener);
                checkBoxCell.setTag(strArr[i3]);
                linearLayout2.addView(checkBoxCell);
            }
        }
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        linearLayout2.setVisibility(8);
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        editTextBoldCursor.setHint(LocaleController.getString(R.string.VoipFeedbackCommentHint));
        editTextBoldCursor.setInputType(147457);
        editTextBoldCursor.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editTextBoldCursor.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_text_RedBold));
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setVisibility(8);
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] zArr = {true};
        final CheckBoxCell checkBoxCell2 = new CheckBoxCell(context, 1);
        View.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPHelper.lambda$showRateAlert$11(zArr, checkBoxCell2, view);
            }
        };
        checkBoxCell2.setText(LocaleController.getString(R.string.CallReportIncludeLogs), null, true, false);
        checkBoxCell2.setClipToPadding(false);
        checkBoxCell2.setOnClickListener(onClickListener2);
        linearLayout.addView(checkBoxCell2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        final TextView textView2 = new TextView(context);
        textView2.setTextSize(2, 14.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        textView2.setText(LocaleController.getString(R.string.CallReportLogsExplain));
        textView2.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        textView2.setOnClickListener(onClickListener2);
        linearLayout.addView(textView2);
        checkBoxCell2.setVisibility(8);
        textView2.setVisibility(8);
        if (!logFile.exists()) {
            zArr[0] = false;
        }
        final AlertDialog create = new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.CallMessageReportProblem)).setView(linearLayout).setPositiveButton(LocaleController.getString(R.string.Send), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i4) {
                VoIPHelper.lambda$showRateAlert$12(alertDialog, i4);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                VoIPHelper.lambda$showRateAlert$13(runnable, dialogInterface);
            }
        }).create();
        if (BuildVars.LOGS_ENABLED && logFile.exists()) {
            create.setNeutralButton("Send log", new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i4) {
                    VoIPHelper.lambda$showRateAlert$14(context, logFile, alertDialog, i4);
                }
            });
        }
        create.show();
        create.getWindow().setSoftInputMode(3);
        final View button = create.getButton(-1);
        button.setEnabled(false);
        betterRatingView.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener() {
            @Override
            public final void onRatingChanged(int i4) {
                VoIPHelper.lambda$showRateAlert$15(button, i4);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPHelper.lambda$showRateAlert$17(BetterRatingView.this, iArr, linearLayout2, editTextBoldCursor, zArr, j2, j, z2, i, logFile, context, create, textView, checkBoxCell2, textView2, button, view);
            }
        });
    }

    public static void showRateAlert(Context context, TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall) {
        Iterator<String> it = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET).iterator();
        while (it.hasNext()) {
            String[] split = it.next().split(" ");
            if (split.length >= 2) {
                if (split[0].equals(tL_messageActionPhoneCall.call_id + "")) {
                    try {
                        showRateAlert(context, null, tL_messageActionPhoneCall.video, tL_messageActionPhoneCall.call_id, Long.parseLong(split[1]), UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        }
    }

    public static void startCall(TLRPC.Chat chat, TLRPC.InputPeer inputPeer, String str, boolean z, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        startCall(chat, inputPeer, str, z, null, activity, baseFragment, accountInstance);
    }

    public static void startCall(TLRPC.Chat chat, TLRPC.InputPeer inputPeer, String str, boolean z, Boolean bool, final Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        if (activity == null) {
            return;
        }
        if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() == 3) {
            initiateCall(null, chat, str, false, false, z, bool, activity, baseFragment, accountInstance);
            return;
        }
        boolean z2 = Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) != 0;
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(activity).setTitle(LocaleController.getString(z2 ? R.string.VoipOfflineAirplaneTitle : R.string.VoipOfflineTitle)).setMessage(LocaleController.getString(z2 ? R.string.VoipGroupOfflineAirplane : R.string.VoipGroupOffline)).setPositiveButton(LocaleController.getString(R.string.OK), null);
        if (z2) {
            final Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                positiveButton.setNeutralButton(LocaleController.getString(R.string.VoipOfflineOpenSettings), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i) {
                        activity.startActivity(intent);
                    }
                });
            }
        }
        try {
            positiveButton.show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void startCall(TLRPC.User user, boolean z, boolean z2, final Activity activity, TLRPC.UserFull userFull, AccountInstance accountInstance) {
        int checkSelfPermission;
        int checkSelfPermission2;
        if (userFull != null && userFull.phone_calls_private) {
            new AlertDialog.Builder(activity).setTitle(LocaleController.getString(R.string.VoipFailed)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
            return;
        }
        if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            boolean z3 = Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) != 0;
            AlertDialog.Builder positiveButton = new AlertDialog.Builder(activity).setTitle(LocaleController.getString(z3 ? R.string.VoipOfflineAirplaneTitle : R.string.VoipOfflineTitle)).setMessage(LocaleController.getString(z3 ? R.string.VoipOfflineAirplane : R.string.VoipOffline)).setPositiveButton(LocaleController.getString(R.string.OK), null);
            if (z3) {
                final Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    positiveButton.setNeutralButton(LocaleController.getString(R.string.VoipOfflineOpenSettings), new AlertDialog.OnButtonClickListener() {
                        @Override
                        public final void onClick(AlertDialog alertDialog, int i) {
                            activity.startActivity(intent);
                        }
                    });
                }
            }
            try {
                positiveButton.show();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList arrayList = new ArrayList();
            checkSelfPermission = activity.checkSelfPermission("android.permission.RECORD_AUDIO");
            if (checkSelfPermission != 0) {
                arrayList.add("android.permission.RECORD_AUDIO");
            }
            if (z) {
                checkSelfPermission2 = activity.checkSelfPermission("android.permission.CAMERA");
                if (checkSelfPermission2 != 0) {
                    arrayList.add("android.permission.CAMERA");
                }
            }
            if (!arrayList.isEmpty()) {
                activity.requestPermissions((String[]) arrayList.toArray(new String[0]), z ? 102 : 101);
                return;
            }
        }
        initiateCall(user, null, null, z, z2, false, null, activity, null, accountInstance);
    }
}
