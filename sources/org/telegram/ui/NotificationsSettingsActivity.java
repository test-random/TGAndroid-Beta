package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int accountsAllRow;
    private int accountsInfoRow;
    private int accountsSectionRow;
    private ListAdapter adapter;
    private int androidAutoAlertRow;
    private int badgeNumberMessagesRow;
    private int badgeNumberMutedRow;
    private int badgeNumberSection;
    private int badgeNumberSection2Row;
    private int badgeNumberShowRow;
    private int callsRingtoneRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int callsVibrateRow;
    private int channelsRow;
    private int contactJoinedRow;
    private int eventsSection2Row;
    private int eventsSectionRow;
    private int groupRow;
    private int inappPreviewRow;
    private int inappPriorityRow;
    private int inappSectionRow;
    private int inappSoundRow;
    private int inappVibrateRow;
    private int inchatSoundRow;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private int notificationsSection2Row;
    private int notificationsSectionRow;
    private int notificationsServiceConnectionRow;
    private int notificationsServiceRow;
    private int otherSection2Row;
    private int otherSectionRow;
    private int pinnedMessageRow;
    private int privateRow;
    private int reactionsRow;
    private int repeatRow;
    private int resetNotificationsRow;
    private int resetNotificationsSectionRow;
    private int resetSection2Row;
    private int resetSectionRow;
    private int storiesRow;
    private boolean updateRepeatNotifications;
    private boolean updateRingtone;
    private boolean updateVibrate;
    private boolean reseting = false;
    private ArrayList exceptionUsers = null;
    private ArrayList exceptionChats = null;
    private ArrayList exceptionChannels = null;
    private ArrayList exceptionStories = null;
    private ArrayList exceptionAutoStories = null;
    private int rowCount = 0;

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.callsSectionRow || i == NotificationsSettingsActivity.this.badgeNumberSection || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.notificationsSectionRow || i == NotificationsSettingsActivity.this.accountsSectionRow) {
                return 0;
            }
            if (i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberMutedRow || i == NotificationsSettingsActivity.this.badgeNumberMessagesRow || i == NotificationsSettingsActivity.this.badgeNumberShowRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow || i == NotificationsSettingsActivity.this.accountsAllRow) {
                return 1;
            }
            if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            if (i == NotificationsSettingsActivity.this.privateRow || i == NotificationsSettingsActivity.this.groupRow || i == NotificationsSettingsActivity.this.channelsRow || i == NotificationsSettingsActivity.this.storiesRow || i == NotificationsSettingsActivity.this.reactionsRow) {
                return 3;
            }
            if (i == NotificationsSettingsActivity.this.eventsSection2Row || i == NotificationsSettingsActivity.this.notificationsSection2Row || i == NotificationsSettingsActivity.this.otherSection2Row || i == NotificationsSettingsActivity.this.resetSection2Row || i == NotificationsSettingsActivity.this.callsSection2Row || i == NotificationsSettingsActivity.this.badgeNumberSection2Row || i == NotificationsSettingsActivity.this.resetNotificationsSectionRow) {
                return 4;
            }
            return i == NotificationsSettingsActivity.this.accountsInfoRow ? 6 : 5;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return (adapterPosition == NotificationsSettingsActivity.this.notificationsSectionRow || adapterPosition == NotificationsSettingsActivity.this.notificationsSection2Row || adapterPosition == NotificationsSettingsActivity.this.inappSectionRow || adapterPosition == NotificationsSettingsActivity.this.eventsSectionRow || adapterPosition == NotificationsSettingsActivity.this.otherSectionRow || adapterPosition == NotificationsSettingsActivity.this.resetSectionRow || adapterPosition == NotificationsSettingsActivity.this.badgeNumberSection || adapterPosition == NotificationsSettingsActivity.this.otherSection2Row || adapterPosition == NotificationsSettingsActivity.this.resetSection2Row || adapterPosition == NotificationsSettingsActivity.this.callsSection2Row || adapterPosition == NotificationsSettingsActivity.this.callsSectionRow || adapterPosition == NotificationsSettingsActivity.this.badgeNumberSection2Row || adapterPosition == NotificationsSettingsActivity.this.accountsSectionRow || adapterPosition == NotificationsSettingsActivity.this.accountsInfoRow || adapterPosition == NotificationsSettingsActivity.this.resetNotificationsSectionRow || adapterPosition == NotificationsSettingsActivity.this.eventsSection2Row) ? false : true;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            if (i == 0) {
                headerCell = new HeaderCell(this.mContext, ((BaseFragment) NotificationsSettingsActivity.this).resourceProvider);
            } else if (i == 1) {
                headerCell = new TextCheckCell(this.mContext, ((BaseFragment) NotificationsSettingsActivity.this).resourceProvider);
            } else if (i == 2) {
                headerCell = new TextDetailSettingsCell(this.mContext);
            } else {
                if (i != 3) {
                    if (i == 4) {
                        headerCell = new ShadowSectionCell(this.mContext, ((BaseFragment) NotificationsSettingsActivity.this).resourceProvider);
                    } else if (i != 5) {
                        headerCell = new TextInfoPrivacyCell(this.mContext, ((BaseFragment) NotificationsSettingsActivity.this).resourceProvider);
                        headerCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        headerCell = new TextSettingsCell(this.mContext, ((BaseFragment) NotificationsSettingsActivity.this).resourceProvider);
                    }
                    return new RecyclerListView.Holder(headerCell);
                }
                headerCell = new NotificationsCheckCell(this.mContext, 21, 64, true, ((BaseFragment) NotificationsSettingsActivity.this).resourceProvider);
            }
            headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(headerCell);
        }
    }

    public static class NotificationException {
        public boolean auto;
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
        public boolean story;
    }

    public void lambda$createView$10(View view, final int i, float f, float f2) {
        ArrayList arrayList;
        boolean isGlobalNotificationsEnabled;
        Dialog create;
        SharedPreferences.Editor edit;
        boolean z;
        String str;
        SharedPreferences.Editor edit2;
        String str2;
        boolean z2;
        SharedPreferences notificationsSettings;
        r3 = false;
        r3 = false;
        r3 = false;
        r3 = false;
        r3 = false;
        boolean z3 = false;
        final int i2 = 0;
        if (getParentActivity() == null) {
            return;
        }
        int i3 = this.privateRow;
        Parcelable parcelable = null;
        parcelable = null;
        if (i == i3 || i == this.groupRow || i == this.channelsRow || i == this.storiesRow || i == this.reactionsRow) {
            ArrayList arrayList2 = null;
            if (i == i3) {
                arrayList = this.exceptionUsers;
                isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(1);
                i2 = 1;
            } else if (i == this.groupRow) {
                arrayList = this.exceptionChats;
                isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(0);
            } else if (i == this.storiesRow) {
                ArrayList arrayList3 = this.exceptionStories;
                ArrayList arrayList4 = this.exceptionAutoStories;
                isGlobalNotificationsEnabled = getNotificationsSettings().getBoolean("EnableAllStories", false);
                i2 = 3;
                arrayList = arrayList3;
                arrayList2 = arrayList4;
            } else if (i == this.reactionsRow) {
                arrayList = null;
                isGlobalNotificationsEnabled = getNotificationsSettings().getBoolean("EnableReactionsMessages", true) || getNotificationsSettings().getBoolean("EnableReactionsStories", true);
                i2 = 4;
            } else {
                arrayList = this.exceptionChannels;
                isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(2);
                i2 = 2;
            }
            if (arrayList == null && i2 != 4) {
                return;
            }
            final NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
            if ((!LocaleController.isRTL || f > AndroidUtilities.dp(76.0f)) && (LocaleController.isRTL || f < view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                presentFragment(new NotificationsCustomSettingsActivity(i2, arrayList, arrayList2));
            } else {
                final boolean z4 = isGlobalNotificationsEnabled;
                showExceptionsAlert(i, new Runnable() {
                    @Override
                    public final void run() {
                        NotificationsSettingsActivity.this.lambda$createView$3(i2, z4, notificationsCheckCell, i);
                    }
                });
            }
            z3 = isGlobalNotificationsEnabled;
        } else if (i == this.callsRingtoneRow) {
            try {
                SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                Uri uri = Settings.System.DEFAULT_RINGTONE_URI;
                String path = uri != null ? uri.getPath() : null;
                String string = notificationsSettings2.getString("CallsRingtonePath", path);
                if (string != null && !string.equals("NoSound")) {
                    parcelable = string.equals(path) ? uri : Uri.parse(string);
                }
                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                startActivityForResult(intent, i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i == this.resetNotificationsRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("ResetNotificationsAlertTitle", R.string.ResetNotificationsAlertTitle));
            builder.setMessage(LocaleController.getString("ResetNotificationsAlert", R.string.ResetNotificationsAlert));
            builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    NotificationsSettingsActivity.this.lambda$createView$6(dialogInterface, i4);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog create2 = builder.create();
            showDialog(create2);
            TextView textView = (TextView) create2.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        } else {
            if (i == this.inappSoundRow) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                edit2 = notificationsSettings.edit();
                str2 = "EnableInAppSounds";
            } else if (i == this.inappVibrateRow) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                edit2 = notificationsSettings.edit();
                str2 = "EnableInAppVibrate";
            } else if (i == this.inappPreviewRow) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                edit2 = notificationsSettings.edit();
                str2 = "EnableInAppPreview";
            } else if (i == this.inchatSoundRow) {
                SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor edit3 = notificationsSettings3.edit();
                z3 = notificationsSettings3.getBoolean("EnableInChatSound", true);
                boolean z5 = !z3;
                edit3.putBoolean("EnableInChatSound", z5);
                edit3.commit();
                getNotificationsController().setInChatSoundEnabled(z5);
            } else if (i == this.inappPriorityRow) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                edit2 = notificationsSettings.edit();
                str2 = "EnableInAppPopup";
            } else if (i == this.contactJoinedRow) {
                SharedPreferences notificationsSettings4 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor edit4 = notificationsSettings4.edit();
                z3 = notificationsSettings4.getBoolean("EnableContactJoined", true);
                boolean z6 = !z3;
                MessagesController.getInstance(this.currentAccount).enableJoined = z6;
                edit4.putBoolean("EnableContactJoined", z6);
                edit4.commit();
                TLRPC.TL_account_setContactSignUpNotification tL_account_setContactSignUpNotification = new TLRPC.TL_account_setContactSignUpNotification();
                tL_account_setContactSignUpNotification.silent = z3;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setContactSignUpNotification, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        NotificationsSettingsActivity.lambda$createView$7(tLObject, tL_error);
                    }
                });
            } else if (i == this.pinnedMessageRow) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                edit2 = notificationsSettings.edit();
                str2 = "PinnedMessages";
            } else if (i == this.androidAutoAlertRow) {
                SharedPreferences notificationsSettings5 = MessagesController.getNotificationsSettings(this.currentAccount);
                edit2 = notificationsSettings5.edit();
                str2 = "EnableAutoNotifications";
                z2 = notificationsSettings5.getBoolean("EnableAutoNotifications", false);
                z3 = z2;
                edit2.putBoolean(str2, !z3);
                edit2.commit();
            } else {
                if (i == this.badgeNumberShowRow) {
                    edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    z3 = getNotificationsController().showBadgeNumber;
                    getNotificationsController().showBadgeNumber = !z3;
                    z = getNotificationsController().showBadgeNumber;
                    str = "badgeNumber";
                } else if (i == this.badgeNumberMutedRow) {
                    SharedPreferences.Editor edit5 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    z3 = getNotificationsController().showBadgeMuted;
                    getNotificationsController().showBadgeMuted = !z3;
                    edit5.putBoolean("badgeNumberMuted", getNotificationsController().showBadgeMuted);
                    edit5.commit();
                    getNotificationsController().updateBadge();
                    getMessagesStorage().updateMutedDialogsFiltersCounters();
                } else if (i == this.badgeNumberMessagesRow) {
                    edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    z3 = getNotificationsController().showBadgeMessages;
                    getNotificationsController().showBadgeMessages = !z3;
                    z = getNotificationsController().showBadgeMessages;
                    str = "badgeNumberMessages";
                } else if (i == this.notificationsServiceConnectionRow) {
                    SharedPreferences notificationsSettings6 = MessagesController.getNotificationsSettings(this.currentAccount);
                    boolean z7 = notificationsSettings6.getBoolean("pushConnection", getMessagesController().backgroundConnection);
                    SharedPreferences.Editor edit6 = notificationsSettings6.edit();
                    edit6.putBoolean("pushConnection", !z7);
                    edit6.commit();
                    ConnectionsManager connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
                    if (z7) {
                        connectionsManager.setPushConnectionEnabled(false);
                    } else {
                        connectionsManager.setPushConnectionEnabled(true);
                    }
                    z3 = z7;
                } else if (i == this.accountsAllRow) {
                    SharedPreferences globalNotificationsSettings = MessagesController.getGlobalNotificationsSettings();
                    boolean z8 = globalNotificationsSettings.getBoolean("AllAccounts", true);
                    SharedPreferences.Editor edit7 = globalNotificationsSettings.edit();
                    boolean z9 = !z8;
                    edit7.putBoolean("AllAccounts", z9);
                    edit7.commit();
                    SharedConfig.showNotificationsForAllAccounts = z9;
                    for (int i4 = 0; i4 < 4; i4++) {
                        if (SharedConfig.showNotificationsForAllAccounts || i4 == this.currentAccount) {
                            NotificationsController.getInstance(i4).showNotifications();
                        } else {
                            NotificationsController.getInstance(i4).hideNotifications();
                        }
                    }
                    z3 = z8;
                } else if (i == this.notificationsServiceRow) {
                    SharedPreferences notificationsSettings7 = MessagesController.getNotificationsSettings(this.currentAccount);
                    z3 = notificationsSettings7.getBoolean("pushService", getMessagesController().keepAliveService);
                    SharedPreferences.Editor edit8 = notificationsSettings7.edit();
                    edit8.putBoolean("pushService", !z3);
                    edit8.commit();
                    ApplicationLoader.startPushService();
                } else {
                    if (i == this.callsVibrateRow) {
                        if (getParentActivity() == null) {
                            return;
                        } else {
                            create = AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0L, 0L, i == this.callsVibrateRow ? "vibrate_calls" : null, new Runnable() {
                                @Override
                                public final void run() {
                                    NotificationsSettingsActivity.this.lambda$createView$8(i);
                                }
                            });
                        }
                    } else if (i == this.repeatRow) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                        builder2.setTitle(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications));
                        builder2.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5, new Object[0]), LocaleController.formatPluralString("Minutes", 10, new Object[0]), LocaleController.formatPluralString("Minutes", 30, new Object[0]), LocaleController.formatPluralString("Hours", 1, new Object[0]), LocaleController.formatPluralString("Hours", 2, new Object[0]), LocaleController.formatPluralString("Hours", 4, new Object[0])}, new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i5) {
                                NotificationsSettingsActivity.this.lambda$createView$9(i, dialogInterface, i5);
                            }
                        });
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        create = builder2.create();
                    }
                    showDialog(create);
                }
                edit.putBoolean(str, z);
                edit.commit();
                getNotificationsController().updateBadge();
            }
            z2 = notificationsSettings.getBoolean(str2, true);
            z3 = z2;
            edit2.putBoolean(str2, !z3);
            edit2.commit();
        }
        if (view instanceof TextCheckCell) {
            ((TextCheckCell) view).setChecked(!z3);
        }
    }

    public void lambda$createView$3(int i, boolean z, NotificationsCheckCell notificationsCheckCell, int i2) {
        if (i == 3) {
            SharedPreferences.Editor edit = getNotificationsSettings().edit();
            if (z) {
                edit.remove("EnableAllStories");
            } else {
                edit.putBoolean("EnableAllStories", true);
            }
            edit.apply();
            getNotificationsController().updateServerNotificationsSettings(i);
        } else if (i == 4 || i == 5) {
            SharedPreferences.Editor edit2 = getNotificationsSettings().edit();
            if (z) {
                edit2.putBoolean("EnableReactionsMessages", false);
                edit2.putBoolean("EnableReactionsStories", false);
            } else {
                edit2.putBoolean("EnableReactionsMessages", true);
                edit2.putBoolean("EnableReactionsStories", true);
            }
            edit2.apply();
            getNotificationsController().updateServerNotificationsSettings(i);
            getNotificationsController().deleteNotificationChannelGlobal(i);
        } else {
            getNotificationsController().setGlobalNotificationsEnabled(i, !z ? 0 : Integer.MAX_VALUE);
        }
        notificationsCheckCell.setChecked(!z, 0);
        this.adapter.notifyItemChanged(i2);
    }

    public void lambda$createView$4() {
        getMessagesController().enableJoined = true;
        this.reseting = false;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        edit.clear();
        edit.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", R.string.ResetNotificationsText), 0).show();
        }
        getMessagesStorage().updateMutedDialogsFiltersCounters();
    }

    public void lambda$createView$5(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                NotificationsSettingsActivity.this.lambda$createView$4();
            }
        });
    }

    public void lambda$createView$6(DialogInterface dialogInterface, int i) {
        if (this.reseting) {
            return;
        }
        this.reseting = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetNotifySettings(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NotificationsSettingsActivity.this.lambda$createView$5(tLObject, tL_error);
            }
        });
    }

    public static void lambda$createView$7(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$createView$8(int i) {
        this.updateVibrate = true;
        this.adapter.notifyItemChanged(i);
    }

    public void lambda$createView$9(int i, DialogInterface dialogInterface, int i2) {
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("repeat_messages", i2 != 1 ? i2 == 2 ? 10 : i2 == 3 ? 30 : i2 == 4 ? 60 : i2 == 5 ? 120 : i2 == 6 ? 240 : 0 : 5).commit();
        this.updateRepeatNotifications = true;
        this.adapter.notifyItemChanged(i);
    }

    public void lambda$loadExceptions$1(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6, ArrayList arrayList7, ArrayList arrayList8) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList3, true);
        this.exceptionUsers = arrayList4;
        this.exceptionChats = arrayList5;
        this.exceptionChannels = arrayList6;
        this.exceptionStories = arrayList7;
        this.exceptionAutoStories = arrayList8;
        this.adapter.notifyItemChanged(this.privateRow);
        this.adapter.notifyItemChanged(this.groupRow);
        this.adapter.notifyItemChanged(this.channelsRow);
        this.adapter.notifyItemChanged(this.storiesRow);
    }

    public void lambda$loadExceptions$2(java.util.ArrayList r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.lambda$loadExceptions$2(java.util.ArrayList):void");
    }

    public void lambda$showExceptionsAlert$11(ArrayList arrayList, ArrayList arrayList2, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, arrayList, arrayList2));
    }

    private void loadExceptions() {
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
        final ArrayList arrayList = new ArrayList(MediaDataController.getInstance(this.currentAccount).hints);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                NotificationsSettingsActivity.this.lambda$loadExceptions$2(arrayList);
            }
        });
    }

    private void showExceptionsAlert(int i, final Runnable runnable) {
        final ArrayList arrayList;
        String formatPluralString;
        final ArrayList arrayList2;
        String str = null;
        if (i == this.storiesRow) {
            arrayList = this.exceptionStories;
            arrayList2 = this.exceptionAutoStories;
            if (arrayList != null && !arrayList.isEmpty()) {
                str = LocaleController.formatPluralString("ChatsException", arrayList.size(), new Object[0]);
            }
        } else if (i == this.privateRow) {
            arrayList = this.exceptionUsers;
            if (arrayList != null && !arrayList.isEmpty()) {
                formatPluralString = LocaleController.formatPluralString("ChatsException", arrayList.size(), new Object[0]);
                str = formatPluralString;
                arrayList2 = null;
            }
            arrayList2 = null;
        } else if (i == this.groupRow) {
            arrayList = this.exceptionChats;
            if (arrayList != null && !arrayList.isEmpty()) {
                formatPluralString = LocaleController.formatPluralString("Groups", arrayList.size(), new Object[0]);
                str = formatPluralString;
                arrayList2 = null;
            }
            arrayList2 = null;
        } else {
            if (i == this.reactionsRow) {
                runnable.run();
                return;
            }
            arrayList = this.exceptionChannels;
            if (arrayList != null && !arrayList.isEmpty()) {
                formatPluralString = LocaleController.formatPluralString("Channels", arrayList.size(), new Object[0]);
                str = formatPluralString;
                arrayList2 = null;
            }
            arrayList2 = null;
        }
        if (str == null) {
            runnable.run();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(AndroidUtilities.replaceTags(arrayList.size() == 1 ? LocaleController.formatString("NotificationsExceptionsSingleAlert", R.string.NotificationsExceptionsSingleAlert, str) : LocaleController.formatString("NotificationsExceptionsAlert", R.string.NotificationsExceptionsAlert, str)));
        builder.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
        builder.setNeutralButton(LocaleController.getString("ViewExceptions", R.string.ViewExceptions), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                NotificationsSettingsActivity.this.lambda$showExceptionsAlert$11(arrayList, arrayList2, dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                runnable.run();
            }
        });
        showDialog(builder.create());
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    NotificationsSettingsActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsSettingsActivity.this.lambda$createView$10(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
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
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        int i4 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        int i5 = Theme.key_switchTrack;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        int i6 = Theme.key_switchTrackChecked;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        int i7 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        return arrayList;
    }

    @Override
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        String str;
        String str2;
        Ringtone ringtone;
        int i3;
        String str3;
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            if (uri == null || (ringtone = RingtoneManager.getRingtone(getParentActivity(), uri)) == null) {
                str = null;
            } else {
                if (i == this.callsRingtoneRow) {
                    if (uri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        i3 = R.string.DefaultRingtone;
                        str3 = "DefaultRingtone";
                        str = LocaleController.getString(str3, i3);
                    }
                    str = ringtone.getTitle(getParentActivity());
                } else {
                    if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                        i3 = R.string.SoundDefault;
                        str3 = "SoundDefault";
                        str = LocaleController.getString(str3, i3);
                    }
                    str = ringtone.getTitle(getParentActivity());
                }
                ringtone.stop();
            }
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (i == this.callsRingtoneRow) {
                if (str == null || uri == null) {
                    str2 = "NoSound";
                    edit.putString("CallsRingtone", "NoSound");
                } else {
                    edit.putString("CallsRingtone", str);
                    str2 = uri.toString();
                }
                edit.putString("CallsRingtonePath", str2);
                this.updateRingtone = true;
            }
            edit.commit();
            this.adapter.notifyItemChanged(i);
        }
    }

    @Override
    public boolean onFragmentCreate() {
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        if (UserConfig.getActivatedAccountsCount() > 1) {
            int i = this.rowCount;
            this.accountsSectionRow = i;
            this.accountsAllRow = i + 1;
            this.rowCount = i + 3;
            this.accountsInfoRow = i + 2;
        } else {
            this.accountsSectionRow = -1;
            this.accountsAllRow = -1;
            this.accountsInfoRow = -1;
        }
        int i2 = this.rowCount;
        this.notificationsSectionRow = i2;
        this.privateRow = i2 + 1;
        this.groupRow = i2 + 2;
        this.channelsRow = i2 + 3;
        this.storiesRow = i2 + 4;
        this.reactionsRow = i2 + 5;
        this.notificationsSection2Row = i2 + 6;
        this.callsSectionRow = i2 + 7;
        this.callsVibrateRow = i2 + 8;
        this.callsRingtoneRow = i2 + 9;
        this.eventsSection2Row = i2 + 10;
        this.badgeNumberSection = i2 + 11;
        this.badgeNumberShowRow = i2 + 12;
        this.badgeNumberMutedRow = i2 + 13;
        this.badgeNumberMessagesRow = i2 + 14;
        this.badgeNumberSection2Row = i2 + 15;
        this.inappSectionRow = i2 + 16;
        this.inappSoundRow = i2 + 17;
        this.inappVibrateRow = i2 + 18;
        this.inappPreviewRow = i2 + 19;
        int i3 = i2 + 21;
        this.rowCount = i3;
        this.inchatSoundRow = i2 + 20;
        if (Build.VERSION.SDK_INT >= 21) {
            this.rowCount = i2 + 22;
            this.inappPriorityRow = i3;
        } else {
            this.inappPriorityRow = -1;
        }
        int i4 = this.rowCount;
        this.callsSection2Row = i4;
        this.eventsSectionRow = i4 + 1;
        this.contactJoinedRow = i4 + 2;
        this.pinnedMessageRow = i4 + 3;
        this.otherSection2Row = i4 + 4;
        this.otherSectionRow = i4 + 5;
        this.notificationsServiceRow = i4 + 6;
        this.notificationsServiceConnectionRow = i4 + 7;
        this.androidAutoAlertRow = -1;
        this.repeatRow = i4 + 8;
        this.resetSection2Row = i4 + 9;
        this.resetSectionRow = i4 + 10;
        this.resetNotificationsRow = i4 + 11;
        this.rowCount = i4 + 13;
        this.resetNotificationsSectionRow = i4 + 12;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        getMessagesController().reloadReactionsNotifySettings();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
