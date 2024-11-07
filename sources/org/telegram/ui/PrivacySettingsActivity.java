package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.SessionsActivity;
import org.telegram.ui.bots.BotBiometry;
import org.telegram.ui.bots.BotBiometrySettings;

public class PrivacySettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int advancedSectionRow;
    private boolean archiveChats;
    private int autoDeleteMesages;
    private int bioRow;
    private int birthdayRow;
    private int blockedRow;
    private int botsAndWebsitesShadowRow;
    private int botsBiometryRow;
    private int botsDetailRow;
    private int botsSectionRow;
    private int callsRow;
    private int contactsDeleteRow;
    private int contactsDetailRow;
    private int contactsSectionRow;
    private int contactsSuggestRow;
    private int contactsSyncRow;
    private TLRPC.account_Password currentPassword;
    private boolean currentSuggest;
    private boolean currentSync;
    private int deleteAccountDetailRow;
    private int deleteAccountRow;
    private boolean deleteAccountUpdate;
    private SessionsActivity devicesActivityPreload;
    private int emailLoginRow;
    private int forwardsRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int newChatsHeaderRow;
    private int newChatsRow;
    private int newChatsSectionRow;
    private boolean newSuggest;
    private boolean newSync;
    private int noncontactsRow;
    private boolean noncontactsValue;
    private int passcodeRow;
    private int passportRow;
    private int passwordRow;
    private int paymentsClearRow;
    private int phoneNumberRow;
    private SpannableString premiumStar;
    private int privacySectionRow;
    private int privacyShadowRow;
    private int profilePhotoRow;
    private AlertDialog progressDialog;
    private int rowCount;
    private int secretDetailRow;
    private int secretMapRow;
    private boolean secretMapUpdate;
    private int secretSectionRow;
    private int secretWebpageRow;
    private int securitySectionRow;
    private int sessionsDetailRow;
    private int sessionsRow;
    private int voicesRow;
    private SessionsActivity webSessionsActivityPreload;
    private int webSessionsRow;
    private final ArrayList biometryBots = new ArrayList();
    private boolean[] clear = new boolean[2];

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == PrivacySettingsActivity.this.passportRow || i == PrivacySettingsActivity.this.lastSeenRow || i == PrivacySettingsActivity.this.phoneNumberRow || i == PrivacySettingsActivity.this.deleteAccountRow || i == PrivacySettingsActivity.this.webSessionsRow || i == PrivacySettingsActivity.this.groupsRow || i == PrivacySettingsActivity.this.paymentsClearRow || i == PrivacySettingsActivity.this.secretMapRow || i == PrivacySettingsActivity.this.contactsDeleteRow || i == PrivacySettingsActivity.this.botsBiometryRow) {
                return 0;
            }
            if (i == PrivacySettingsActivity.this.privacyShadowRow || i == PrivacySettingsActivity.this.deleteAccountDetailRow || i == PrivacySettingsActivity.this.groupsDetailRow || i == PrivacySettingsActivity.this.sessionsDetailRow || i == PrivacySettingsActivity.this.secretDetailRow || i == PrivacySettingsActivity.this.botsDetailRow || i == PrivacySettingsActivity.this.contactsDetailRow || i == PrivacySettingsActivity.this.newChatsSectionRow) {
                return 1;
            }
            if (i == PrivacySettingsActivity.this.securitySectionRow || i == PrivacySettingsActivity.this.advancedSectionRow || i == PrivacySettingsActivity.this.privacySectionRow || i == PrivacySettingsActivity.this.secretSectionRow || i == PrivacySettingsActivity.this.botsSectionRow || i == PrivacySettingsActivity.this.contactsSectionRow || i == PrivacySettingsActivity.this.newChatsHeaderRow) {
                return 2;
            }
            if (i == PrivacySettingsActivity.this.secretWebpageRow || i == PrivacySettingsActivity.this.contactsSyncRow || i == PrivacySettingsActivity.this.contactsSuggestRow || i == PrivacySettingsActivity.this.newChatsRow) {
                return 3;
            }
            if (i == PrivacySettingsActivity.this.botsAndWebsitesShadowRow) {
                return 4;
            }
            return (i == PrivacySettingsActivity.this.autoDeleteMesages || i == PrivacySettingsActivity.this.sessionsRow || i == PrivacySettingsActivity.this.emailLoginRow || i == PrivacySettingsActivity.this.passwordRow || i == PrivacySettingsActivity.this.passcodeRow || i == PrivacySettingsActivity.this.blockedRow) ? 5 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition == PrivacySettingsActivity.this.passcodeRow || adapterPosition == PrivacySettingsActivity.this.passwordRow || adapterPosition == PrivacySettingsActivity.this.blockedRow || adapterPosition == PrivacySettingsActivity.this.sessionsRow || adapterPosition == PrivacySettingsActivity.this.secretWebpageRow || adapterPosition == PrivacySettingsActivity.this.webSessionsRow) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.groupsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(1)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.lastSeenRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(0)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.callsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(2)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.profilePhotoRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(4)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.bioRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(9)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.birthdayRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(11)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.forwardsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(5)) {
                return true;
            }
            if (adapterPosition == PrivacySettingsActivity.this.phoneNumberRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(6)) {
                return true;
            }
            if ((adapterPosition == PrivacySettingsActivity.this.voicesRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(8)) || adapterPosition == PrivacySettingsActivity.this.noncontactsRow) {
                return true;
            }
            if (adapterPosition != PrivacySettingsActivity.this.deleteAccountRow || PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                return (adapterPosition == PrivacySettingsActivity.this.newChatsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingGlobalSettings()) || adapterPosition == PrivacySettingsActivity.this.emailLoginRow || adapterPosition == PrivacySettingsActivity.this.paymentsClearRow || adapterPosition == PrivacySettingsActivity.this.secretMapRow || adapterPosition == PrivacySettingsActivity.this.contactsSyncRow || adapterPosition == PrivacySettingsActivity.this.passportRow || adapterPosition == PrivacySettingsActivity.this.contactsDeleteRow || adapterPosition == PrivacySettingsActivity.this.contactsSuggestRow || adapterPosition == PrivacySettingsActivity.this.autoDeleteMesages || adapterPosition == PrivacySettingsActivity.this.botsBiometryRow;
            }
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            String str;
            int i3;
            String str2;
            String string;
            boolean z;
            int i4;
            String str3;
            String string2;
            int i5;
            String str4;
            String string3;
            int i6;
            String str5;
            String string4;
            String str6;
            String format;
            String string5;
            int i7;
            String str7;
            String format2;
            boolean z2;
            boolean z3;
            boolean z4 = false;
            int itemViewType = viewHolder.getItemViewType();
            String str8 = null;
            int i8 = 16;
            if (itemViewType == 0) {
                boolean z5 = viewHolder.itemView.getTag() != null && ((Integer) viewHolder.itemView.getTag()).intValue() == i;
                viewHolder.itemView.setTag(Integer.valueOf(i));
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.webSessionsRow) {
                    textSettingsCell.setText(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), false);
                } else {
                    if (i == PrivacySettingsActivity.this.phoneNumberRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(6)) {
                            z4 = true;
                            i8 = 30;
                        } else {
                            str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 6);
                        }
                        i4 = R.string.PrivacyPhone;
                        str3 = "PrivacyPhone";
                    } else if (i == PrivacySettingsActivity.this.lastSeenRow) {
                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(0)) {
                            z4 = true;
                            i8 = 30;
                        } else {
                            str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 0);
                        }
                        i4 = R.string.PrivacyLastSeen;
                        str3 = "PrivacyLastSeen";
                    } else {
                        if (i == PrivacySettingsActivity.this.groupsRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(1)) {
                                i8 = 30;
                            } else {
                                str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 1);
                                r3 = false;
                            }
                            textSettingsCell.setTextAndValue(LocaleController.getString(R.string.PrivacyInvites), str8, false);
                        } else if (i == PrivacySettingsActivity.this.callsRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(2)) {
                                z4 = true;
                                i8 = 30;
                            } else {
                                str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 2);
                            }
                            i4 = R.string.Calls;
                            str3 = "Calls";
                        } else if (i == PrivacySettingsActivity.this.profilePhotoRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(4)) {
                                z4 = true;
                                i8 = 30;
                            } else {
                                str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 4);
                            }
                            i4 = R.string.PrivacyProfilePhoto;
                            str3 = "PrivacyProfilePhoto";
                        } else if (i == PrivacySettingsActivity.this.bioRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(9)) {
                                z4 = true;
                                i8 = 30;
                            } else {
                                str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 9);
                            }
                            i4 = R.string.PrivacyBio;
                            str3 = "PrivacyBio";
                        } else if (i == PrivacySettingsActivity.this.birthdayRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(11)) {
                                z4 = true;
                                i8 = 30;
                            } else {
                                str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 11);
                            }
                            string2 = LocaleController.getString(R.string.PrivacyBirthday);
                            textSettingsCell.setTextAndValue(string2, str8, true);
                        } else if (i == PrivacySettingsActivity.this.forwardsRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(5)) {
                                z4 = true;
                                i8 = 30;
                            } else {
                                str8 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 5);
                            }
                            i4 = R.string.PrivacyForwards;
                            str3 = "PrivacyForwards";
                        } else if (i == PrivacySettingsActivity.this.voicesRow) {
                            if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivacyInfo(8)) {
                                z = true;
                                i8 = 30;
                            } else {
                                str8 = !PrivacySettingsActivity.this.getUserConfig().isPremium() ? LocaleController.getString(R.string.P2PEverybody) : PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 8);
                                z = false;
                            }
                            textSettingsCell.setTextAndValue(PrivacySettingsActivity.this.addPremiumStar(LocaleController.getString(R.string.PrivacyVoiceMessages)), str8, PrivacySettingsActivity.this.noncontactsRow != -1);
                            textSettingsCell.getValueImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
                            z4 = z;
                        } else if (i == PrivacySettingsActivity.this.noncontactsRow) {
                            textSettingsCell.setTextAndValue(PrivacySettingsActivity.this.getMessagesController().newNoncontactPeersRequirePremiumWithoutOwnpremium ? LocaleController.getString(R.string.PrivacyMessages) : PrivacySettingsActivity.this.addPremiumStar(LocaleController.getString(R.string.PrivacyMessages)), LocaleController.getString(PrivacySettingsActivity.this.noncontactsValue ? R.string.ContactsAndPremium : R.string.P2PEverybody), PrivacySettingsActivity.this.bioRow != -1);
                        } else {
                            if (i == PrivacySettingsActivity.this.passportRow) {
                                i2 = R.string.TelegramPassport;
                                str = "TelegramPassport";
                            } else if (i == PrivacySettingsActivity.this.deleteAccountRow) {
                                if (!PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                                    int deleteAccountTTL = PrivacySettingsActivity.this.getContactsController().getDeleteAccountTTL();
                                    if (deleteAccountTTL <= 182) {
                                        str8 = LocaleController.formatPluralString("Months", deleteAccountTTL / 30, new Object[0]);
                                    } else if (deleteAccountTTL == 365) {
                                        str8 = LocaleController.formatPluralString("Months", 12, new Object[0]);
                                    } else if (deleteAccountTTL == 548) {
                                        str8 = LocaleController.formatPluralString("Months", 18, new Object[0]);
                                    } else if (deleteAccountTTL == 730) {
                                        str8 = LocaleController.formatPluralString("Months", 24, new Object[0]);
                                    } else if (deleteAccountTTL > 30) {
                                        double d = deleteAccountTTL;
                                        Double.isNaN(d);
                                        str8 = LocaleController.formatPluralString("Months", (int) Math.round(d / 30.0d), new Object[0]);
                                    } else {
                                        str8 = LocaleController.formatPluralString("Days", deleteAccountTTL, new Object[0]);
                                    }
                                    r3 = false;
                                }
                                textSettingsCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor3", R.string.DeleteAccountIfAwayFor3), str8, PrivacySettingsActivity.this.deleteAccountUpdate, false);
                                PrivacySettingsActivity.this.deleteAccountUpdate = false;
                            } else if (i == PrivacySettingsActivity.this.paymentsClearRow) {
                                i2 = R.string.PrivacyPaymentsClear;
                                str = "PrivacyPaymentsClear";
                            } else if (i == PrivacySettingsActivity.this.botsBiometryRow) {
                                string = LocaleController.getString(R.string.PrivacyBiometryBotsButton);
                                textSettingsCell.setText(string, true);
                            } else if (i == PrivacySettingsActivity.this.secretMapRow) {
                                int i9 = SharedConfig.mapPreviewType;
                                if (i9 == 0) {
                                    i3 = R.string.MapPreviewProviderTelegram;
                                    str2 = "MapPreviewProviderTelegram";
                                } else if (i9 == 1) {
                                    i3 = R.string.MapPreviewProviderGoogle;
                                    str2 = "MapPreviewProviderGoogle";
                                } else if (i9 != 2) {
                                    i3 = R.string.MapPreviewProviderYandex;
                                    str2 = "MapPreviewProviderYandex";
                                } else {
                                    i3 = R.string.MapPreviewProviderNobody;
                                    str2 = "MapPreviewProviderNobody";
                                }
                                textSettingsCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), LocaleController.getString(str2, i3), PrivacySettingsActivity.this.secretMapUpdate, true);
                                PrivacySettingsActivity.this.secretMapUpdate = false;
                            } else if (i == PrivacySettingsActivity.this.contactsDeleteRow) {
                                i2 = R.string.SyncContactsDelete;
                                str = "SyncContactsDelete";
                            }
                            string = LocaleController.getString(str, i2);
                            textSettingsCell.setText(string, true);
                        }
                        z4 = r3;
                    }
                    string2 = LocaleController.getString(str3, i4);
                    textSettingsCell.setTextAndValue(string2, str8, true);
                }
                textSettingsCell.setDrawLoading(z4, i8, z5);
                return;
            }
            if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(this.mContext, i == getItemCount() - 1 ? R.drawable.greydivider_bottom : R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                if (i == PrivacySettingsActivity.this.deleteAccountDetailRow) {
                    i5 = R.string.DeleteAccountHelp;
                    str4 = "DeleteAccountHelp";
                } else if (i == PrivacySettingsActivity.this.groupsDetailRow) {
                    i5 = R.string.GroupsAndChannelsHelp;
                    str4 = "GroupsAndChannelsHelp";
                } else if (i == PrivacySettingsActivity.this.sessionsDetailRow) {
                    i5 = R.string.SessionsSettingsInfo;
                    str4 = "SessionsSettingsInfo";
                } else if (i == PrivacySettingsActivity.this.secretDetailRow) {
                    i5 = R.string.SecretWebPageInfo;
                    str4 = "SecretWebPageInfo";
                } else if (i == PrivacySettingsActivity.this.botsDetailRow) {
                    i5 = R.string.PrivacyBotsInfo;
                    str4 = "PrivacyBotsInfo";
                } else if (i == PrivacySettingsActivity.this.privacyShadowRow) {
                    string3 = LocaleController.getString(R.string.PrivacyInvitesInfo);
                    textInfoPrivacyCell.setText(string3);
                    return;
                } else if (i == PrivacySettingsActivity.this.contactsDetailRow) {
                    i5 = R.string.SuggestContactsInfo;
                    str4 = "SuggestContactsInfo";
                } else {
                    if (i != PrivacySettingsActivity.this.newChatsSectionRow) {
                        return;
                    }
                    i5 = R.string.ArchiveAndMuteInfo;
                    str4 = "ArchiveAndMuteInfo";
                }
                string3 = LocaleController.getString(str4, i5);
                textInfoPrivacyCell.setText(string3);
                return;
            }
            if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.privacySectionRow) {
                    i6 = R.string.PrivacyTitle;
                    str5 = "PrivacyTitle";
                } else if (i == PrivacySettingsActivity.this.securitySectionRow) {
                    i6 = R.string.SecurityTitle;
                    str5 = "SecurityTitle";
                } else if (i == PrivacySettingsActivity.this.advancedSectionRow) {
                    i6 = R.string.DeleteMyAccount;
                    str5 = "DeleteMyAccount";
                } else if (i == PrivacySettingsActivity.this.secretSectionRow) {
                    i6 = R.string.SecretChat;
                    str5 = "SecretChat";
                } else if (i == PrivacySettingsActivity.this.botsSectionRow) {
                    i6 = R.string.PrivacyBots;
                    str5 = "PrivacyBots";
                } else if (i == PrivacySettingsActivity.this.contactsSectionRow) {
                    i6 = R.string.Contacts;
                    str5 = "Contacts";
                } else {
                    if (i != PrivacySettingsActivity.this.newChatsHeaderRow) {
                        return;
                    }
                    i6 = R.string.NewChatsFromNonContacts;
                    str5 = "NewChatsFromNonContacts";
                }
                headerCell.setText(LocaleController.getString(str5, i6));
                return;
            }
            if (itemViewType == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PrivacySettingsActivity.this.secretWebpageRow) {
                    string4 = LocaleController.getString("SecretWebPage", R.string.SecretWebPage);
                    if (PrivacySettingsActivity.this.getMessagesController().secretWebpagePreview != 1) {
                        r3 = false;
                    }
                } else if (i == PrivacySettingsActivity.this.contactsSyncRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", R.string.SyncContacts), PrivacySettingsActivity.this.newSync, true);
                    return;
                } else if (i == PrivacySettingsActivity.this.contactsSuggestRow) {
                    string4 = LocaleController.getString("SuggestContacts", R.string.SuggestContacts);
                    r3 = PrivacySettingsActivity.this.newSuggest;
                } else {
                    if (i != PrivacySettingsActivity.this.newChatsRow) {
                        return;
                    }
                    string4 = LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute);
                    r3 = PrivacySettingsActivity.this.archiveChats;
                }
                textCheckCell.setTextAndCheck(string4, r3, false);
                return;
            }
            if (itemViewType != 5) {
                return;
            }
            View view = viewHolder.itemView;
            TextCell textCell = (TextCell) view;
            boolean z6 = view.getTag() != null && ((Integer) viewHolder.itemView.getTag()).intValue() == i;
            viewHolder.itemView.setTag(Integer.valueOf(i));
            textCell.setPrioritizeTitleOverValue(false);
            if (i != PrivacySettingsActivity.this.autoDeleteMesages) {
                String str9 = "";
                if (i == PrivacySettingsActivity.this.sessionsRow) {
                    if (PrivacySettingsActivity.this.devicesActivityPreload.getSessionsCount() != 0) {
                        format2 = String.format(LocaleController.getInstance().getCurrentLocale(), "%d", Integer.valueOf(PrivacySettingsActivity.this.devicesActivityPreload.getSessionsCount()));
                    } else {
                        if (PrivacySettingsActivity.this.getMessagesController().lastKnownSessionsCount == 0) {
                            str6 = "";
                            PrivacySettingsActivity.this.getMessagesController().lastKnownSessionsCount = PrivacySettingsActivity.this.devicesActivityPreload.getSessionsCount();
                            string5 = LocaleController.getString("SessionsTitle", R.string.SessionsTitle);
                            i7 = R.drawable.msg2_devices;
                            z2 = true;
                            z3 = false;
                            textCell.setTextAndValueAndIcon(string5, str6, z2, i7, z3);
                            z4 = r3;
                            textCell.setDrawLoading(z4, 16, z6);
                        }
                        format2 = String.format(LocaleController.getInstance().getCurrentLocale(), "%d", Integer.valueOf(PrivacySettingsActivity.this.getMessagesController().lastKnownSessionsCount));
                    }
                    str6 = format2;
                    r3 = false;
                    PrivacySettingsActivity.this.getMessagesController().lastKnownSessionsCount = PrivacySettingsActivity.this.devicesActivityPreload.getSessionsCount();
                    string5 = LocaleController.getString("SessionsTitle", R.string.SessionsTitle);
                    i7 = R.drawable.msg2_devices;
                    z2 = true;
                    z3 = false;
                    textCell.setTextAndValueAndIcon(string5, str6, z2, i7, z3);
                    z4 = r3;
                    textCell.setDrawLoading(z4, 16, z6);
                }
                if (i == PrivacySettingsActivity.this.emailLoginRow) {
                    if (PrivacySettingsActivity.this.currentPassword == null) {
                        z4 = true;
                        str7 = str9;
                    } else {
                        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(PrivacySettingsActivity.this.currentPassword.login_email_pattern);
                        int indexOf = PrivacySettingsActivity.this.currentPassword.login_email_pattern.indexOf(42);
                        int lastIndexOf = PrivacySettingsActivity.this.currentPassword.login_email_pattern.lastIndexOf(42);
                        str7 = valueOf;
                        str7 = valueOf;
                        str7 = valueOf;
                        if (indexOf != lastIndexOf && indexOf != -1 && lastIndexOf != -1) {
                            TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                            textStyleRun.flags |= 256;
                            textStyleRun.start = indexOf;
                            int i10 = lastIndexOf + 1;
                            textStyleRun.end = i10;
                            valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i10, 0);
                            str7 = valueOf;
                        }
                    }
                    textCell.setPrioritizeTitleOverValue(true);
                    textCell.setTextAndSpoilersValueAndIcon(LocaleController.getString(R.string.EmailLogin), str7, R.drawable.msg2_email, true);
                } else if (i == PrivacySettingsActivity.this.passwordRow) {
                    if (PrivacySettingsActivity.this.currentPassword == null) {
                        str6 = "";
                    } else {
                        str6 = PrivacySettingsActivity.this.currentPassword.has_password ? LocaleController.getString("PasswordOn", R.string.PasswordOn) : LocaleController.getString("PasswordOff", R.string.PasswordOff);
                        r3 = false;
                    }
                    string5 = LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification);
                    i7 = R.drawable.msg2_permissions;
                } else if (i == PrivacySettingsActivity.this.passcodeRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("Passcode", R.string.Passcode), SharedConfig.passcodeHash.length() != 0 ? LocaleController.getString("PasswordOn", R.string.PasswordOn) : LocaleController.getString("PasswordOff", R.string.PasswordOff), true, R.drawable.msg2_secret, true);
                } else if (i == PrivacySettingsActivity.this.blockedRow) {
                    int i11 = PrivacySettingsActivity.this.getMessagesController().totalBlockedCount;
                    if (i11 == 0) {
                        format = LocaleController.getString("BlockedEmpty", R.string.BlockedEmpty);
                    } else if (i11 > 0) {
                        format = String.format(LocaleController.getInstance().getCurrentLocale(), "%d", Integer.valueOf(i11));
                    } else {
                        str6 = "";
                        string5 = LocaleController.getString("BlockedUsers", R.string.BlockedUsers);
                        i7 = R.drawable.msg2_block2;
                    }
                    str6 = format;
                    r3 = false;
                    string5 = LocaleController.getString("BlockedUsers", R.string.BlockedUsers);
                    i7 = R.drawable.msg2_block2;
                }
                textCell.setDrawLoading(z4, 16, z6);
            }
            int globalTTl = PrivacySettingsActivity.this.getUserConfig().getGlobalTTl();
            if (globalTTl == -1) {
                str6 = null;
            } else {
                str6 = globalTTl > 0 ? LocaleController.formatTTLString(globalTTl * 60) : LocaleController.getString("PasswordOff", R.string.PasswordOff);
                r3 = false;
            }
            string5 = LocaleController.getString("AutoDeleteMessages", R.string.AutoDeleteMessages);
            i7 = R.drawable.msg2_autodelete;
            z2 = true;
            z3 = true;
            textCell.setTextAndValueAndIcon(string5, str6, z2, i7, z3);
            z4 = r3;
            textCell.setDrawLoading(z4, 16, z6);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i != 0) {
                if (i == 1) {
                    textSettingsCell = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 2) {
                    textSettingsCell = new HeaderCell(this.mContext);
                } else if (i != 4) {
                    textSettingsCell = i != 5 ? new TextCheckCell(this.mContext) : new TextCell(this.mContext);
                } else {
                    textSettingsCell = new ShadowSectionCell(this.mContext);
                }
                return new RecyclerListView.Holder(textSettingsCell);
            }
            textSettingsCell = new TextSettingsCell(this.mContext);
            textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(textSettingsCell);
        }
    }

    public CharSequence addPremiumStar(String str) {
        if (this.premiumStar == null) {
            this.premiumStar = new SpannableString("★");
            AnimatedEmojiDrawable.WrapSizeDrawable wrapSizeDrawable = new AnimatedEmojiDrawable.WrapSizeDrawable(PremiumGradient.getInstance().premiumStarMenuDrawable, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
            wrapSizeDrawable.setBounds(0, 0, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
            this.premiumStar.setSpan(new ImageSpan(wrapSizeDrawable, 2), 0, this.premiumStar.length(), 17);
        }
        return new SpannableStringBuilder(str).append((CharSequence) " \u2009").append((CharSequence) this.premiumStar);
    }

    public static String formatRulesString(AccountInstance accountInstance, int i) {
        ArrayList<TLRPC.PrivacyRule> privacyRules = accountInstance.getContactsController().getPrivacyRules(i);
        if (privacyRules == null || privacyRules.size() == 0) {
            return i == 3 ? LocaleController.getString("P2PNobody", R.string.P2PNobody) : LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
        }
        char c = 65535;
        int i2 = 0;
        int i3 = 0;
        boolean z = false;
        for (int i4 = 0; i4 < privacyRules.size(); i4++) {
            TLRPC.PrivacyRule privacyRule = privacyRules.get(i4);
            if (privacyRule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                TLRPC.TL_privacyValueAllowChatParticipants tL_privacyValueAllowChatParticipants = (TLRPC.TL_privacyValueAllowChatParticipants) privacyRule;
                int size = tL_privacyValueAllowChatParticipants.chats.size();
                for (int i5 = 0; i5 < size; i5++) {
                    TLRPC.Chat chat = accountInstance.getMessagesController().getChat(tL_privacyValueAllowChatParticipants.chats.get(i5));
                    if (chat != null) {
                        i3 += chat.participants_count;
                    }
                }
            } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                TLRPC.TL_privacyValueDisallowChatParticipants tL_privacyValueDisallowChatParticipants = (TLRPC.TL_privacyValueDisallowChatParticipants) privacyRule;
                int size2 = tL_privacyValueDisallowChatParticipants.chats.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    TLRPC.Chat chat2 = accountInstance.getMessagesController().getChat(tL_privacyValueDisallowChatParticipants.chats.get(i6));
                    if (chat2 != null) {
                        i2 += chat2.participants_count;
                    }
                }
            } else if (privacyRule instanceof TLRPC.TL_privacyValueAllowUsers) {
                i3 += ((TLRPC.TL_privacyValueAllowUsers) privacyRule).users.size();
            } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                i2 += ((TLRPC.TL_privacyValueDisallowUsers) privacyRule).users.size();
            } else if (privacyRule instanceof TLRPC.TL_privacyValueAllowPremium) {
                z = true;
            } else if (c == 65535) {
                c = privacyRule instanceof TLRPC.TL_privacyValueAllowAll ? (char) 0 : privacyRule instanceof TLRPC.TL_privacyValueDisallowAll ? (char) 1 : (char) 2;
            }
        }
        if (c == 0 || (c == 65535 && i2 > 0)) {
            return i == 3 ? i2 == 0 ? LocaleController.getString("P2PEverybody", R.string.P2PEverybody) : LocaleController.formatString("P2PEverybodyMinus", R.string.P2PEverybodyMinus, Integer.valueOf(i2)) : i2 == 0 ? LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody) : LocaleController.formatString("LastSeenEverybodyMinus", R.string.LastSeenEverybodyMinus, Integer.valueOf(i2));
        }
        if (c != 2 && (c != 65535 || i2 <= 0 || i3 <= 0)) {
            if (c != 1 && i3 <= 0) {
                return "unknown";
            }
            if (i == 3) {
                return i3 == 0 ? LocaleController.getString("P2PNobody", R.string.P2PNobody) : LocaleController.formatString("P2PNobodyPlus", R.string.P2PNobodyPlus, Integer.valueOf(i3));
            }
            if (i3 == 0) {
                return LocaleController.getString(z ? R.string.LastSeenNobodyPremium : R.string.LastSeenNobody);
            }
            return LocaleController.formatString(z ? R.string.LastSeenNobodyPremiumPlus : R.string.LastSeenNobodyPlus, Integer.valueOf(i3));
        }
        if (i == 3) {
            return (i3 == 0 && i2 == 0) ? LocaleController.getString("P2PContacts", R.string.P2PContacts) : (i3 == 0 || i2 == 0) ? i2 != 0 ? LocaleController.formatString(R.string.P2PContactsMinus, Integer.valueOf(i2)) : LocaleController.formatString(R.string.P2PContactsPlus, Integer.valueOf(i3)) : LocaleController.formatString(R.string.P2PContactsMinusPlus, Integer.valueOf(i2), Integer.valueOf(i3));
        }
        if (i3 == 0 && i2 == 0) {
            return LocaleController.getString(z ? R.string.LastSeenContactsPremium : R.string.LastSeenContacts);
        }
        if (i3 != 0 && i2 != 0) {
            return LocaleController.formatString(z ? R.string.LastSeenContactsPremiumMinusPlus : R.string.LastSeenContactsMinusPlus, Integer.valueOf(i2), Integer.valueOf(i3));
        }
        if (i2 != 0) {
            return LocaleController.formatString(z ? R.string.LastSeenContactsPremiumMinus : R.string.LastSeenContactsMinus, Integer.valueOf(i2));
        }
        return LocaleController.formatString(z ? R.string.LastSeenContactsPremiumPlus : R.string.LastSeenContactsPlus, Integer.valueOf(i3));
    }

    private void initPassword() {
        TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
        if (!getUserConfig().hasSecureData && this.currentPassword.has_secure_values) {
            getUserConfig().hasSecureData = true;
            getUserConfig().saveConfig(false);
            updateRows();
            return;
        }
        TLRPC.account_Password account_password = this.currentPassword;
        if (account_password != null) {
            int i = this.emailLoginRow;
            String str = account_password.login_email_pattern;
            boolean z = str != null && i == -1;
            boolean z2 = str == null && i != -1;
            if (z || z2) {
                updateRows(false);
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    if (z) {
                        listAdapter.notifyItemInserted(this.emailLoginRow);
                    } else {
                        listAdapter.notifyItemRemoved(i);
                    }
                }
            }
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(this.passwordRow);
        }
    }

    public void lambda$createView$10(DialogInterface dialogInterface, int i) {
        AlertDialog show = new AlertDialog.Builder(getParentActivity(), 3, null).show();
        this.progressDialog = show;
        show.setCanCancel(false);
        if (this.currentSync != this.newSync) {
            UserConfig userConfig = getUserConfig();
            boolean z = this.newSync;
            userConfig.syncContacts = z;
            this.currentSync = z;
            getUserConfig().saveConfig(false);
        }
        getContactsController().deleteAllContacts(new Runnable() {
            @Override
            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$9();
            }
        });
    }

    public void lambda$createView$11(TextCheckCell textCheckCell) {
        boolean z = !this.newSuggest;
        this.newSuggest = z;
        textCheckCell.setChecked(z);
    }

    public void lambda$createView$12(final TextCheckCell textCheckCell, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$11(textCheckCell);
            }
        });
    }

    public void lambda$createView$13(final TextCheckCell textCheckCell, DialogInterface dialogInterface, int i) {
        TLRPC.TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TLRPC.TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tL_payments_clearSavedInfo.credentials = zArr[1];
        tL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tL_payments_clearSavedInfo, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.this.lambda$createView$12(textCheckCell, tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$14() {
        this.listAdapter.notifyDataSetChanged();
        this.secretMapUpdate = true;
    }

    public void lambda$createView$15(View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        boolean[] zArr = this.clear;
        boolean z = !zArr[intValue];
        zArr[intValue] = z;
        checkBoxCell.setChecked(z, true);
    }

    public static void lambda$createView$16(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$createView$17(DialogInterface dialogInterface, int i) {
        int i2;
        String str;
        TLRPC.TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TLRPC.TL_payments_clearSavedInfo();
        boolean[] zArr = this.clear;
        tL_payments_clearSavedInfo.credentials = zArr[1];
        tL_payments_clearSavedInfo.info = zArr[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(tL_payments_clearSavedInfo, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.lambda$createView$16(tLObject, tL_error);
            }
        });
        boolean[] zArr2 = this.clear;
        boolean z = zArr2[0];
        if (z && zArr2[1]) {
            i2 = R.string.PrivacyPaymentsPaymentShippingCleared;
            str = "PrivacyPaymentsPaymentShippingCleared";
        } else if (z) {
            i2 = R.string.PrivacyPaymentsShippingInfoCleared;
            str = "PrivacyPaymentsShippingInfoCleared";
        } else {
            if (!zArr2[1]) {
                return;
            }
            i2 = R.string.PrivacyPaymentsPaymentInfoCleared;
            str = "PrivacyPaymentsPaymentInfoCleared";
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(str, i2)).show();
    }

    public void lambda$createView$18(DialogInterface dialogInterface, int i) {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", R.string.PrivacyPaymentsClearAlertTitle));
        builder.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", R.string.PrivacyPaymentsClearAlert));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface2, int i2) {
                PrivacySettingsActivity.this.lambda$createView$17(dialogInterface2, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public void lambda$createView$19(Context context, View view, int i) {
        TextView textView;
        int i2;
        String str;
        boolean z;
        TextCheckCell textCheckCell;
        final TextCheckCell textCheckCell2;
        String str2;
        BaseFragment baseFragment;
        if (view.isEnabled()) {
            if (i == this.autoDeleteMesages && getUserConfig().getGlobalTTl() >= 0) {
                presentFragment(new AutoDeleteMessagesActivity());
            }
            if (i == this.blockedRow) {
                baseFragment = new PrivacyUsersActivity();
            } else if (i == this.sessionsRow) {
                this.devicesActivityPreload.resetFragment();
                baseFragment = this.devicesActivityPreload;
            } else if (i == this.webSessionsRow) {
                this.webSessionsActivityPreload.resetFragment();
                baseFragment = this.webSessionsActivityPreload;
            } else {
                int i3 = 4;
                boolean z2 = true;
                if (i == this.deleteAccountRow) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    int deleteAccountTTL = getContactsController().getDeleteAccountTTL();
                    if (deleteAccountTTL <= 31) {
                        i3 = 0;
                    } else if (deleteAccountTTL <= 93) {
                        i3 = 1;
                    } else if (deleteAccountTTL <= 182) {
                        i3 = 2;
                    } else if (deleteAccountTTL != 548) {
                        i3 = deleteAccountTTL == 730 ? 5 : 3;
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteAccountTitle", R.string.DeleteAccountTitle));
                    String[] strArr = {LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Months", 12, new Object[0]), LocaleController.formatPluralString("Months", 18, new Object[0]), LocaleController.formatPluralString("Months", 24, new Object[0])};
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    builder.setView(linearLayout);
                    int i4 = 0;
                    while (i4 < 6) {
                        RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                        radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        radioColorCell.setTag(Integer.valueOf(i4));
                        radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                        radioColorCell.setTextAndValue(strArr[i4], i3 == i4);
                        linearLayout.addView(radioColorCell);
                        radioColorCell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view2) {
                                PrivacySettingsActivity.this.lambda$createView$6(builder, view2);
                            }
                        });
                        i4++;
                    }
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                }
                if (i == this.lastSeenRow) {
                    baseFragment = new PrivacyControlActivity(0);
                } else if (i == this.phoneNumberRow) {
                    baseFragment = new PrivacyControlActivity(6);
                } else if (i == this.groupsRow) {
                    baseFragment = new PrivacyControlActivity(1);
                } else if (i == this.callsRow) {
                    baseFragment = new PrivacyControlActivity(2);
                } else if (i == this.profilePhotoRow) {
                    baseFragment = new PrivacyControlActivity(4);
                } else if (i == this.bioRow) {
                    baseFragment = new PrivacyControlActivity(9);
                } else if (i == this.birthdayRow) {
                    baseFragment = new PrivacyControlActivity(11);
                } else if (i == this.forwardsRow) {
                    baseFragment = new PrivacyControlActivity(5);
                } else if (i == this.voicesRow) {
                    baseFragment = new PrivacyControlActivity(8);
                } else if (i == this.noncontactsRow) {
                    baseFragment = new PrivacyControlActivity(10);
                } else {
                    if (i == this.emailLoginRow) {
                        TLRPC.account_Password account_password = this.currentPassword;
                        if (account_password == null || (str2 = account_password.login_email_pattern) == null) {
                            return;
                        }
                        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(str2);
                        int indexOf = this.currentPassword.login_email_pattern.indexOf(42);
                        int lastIndexOf = this.currentPassword.login_email_pattern.lastIndexOf(42);
                        if (indexOf != lastIndexOf && indexOf != -1 && lastIndexOf != -1) {
                            TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                            textStyleRun.flags |= 256;
                            textStyleRun.start = indexOf;
                            int i5 = lastIndexOf + 1;
                            textStyleRun.end = i5;
                            valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i5, 0);
                        }
                        new AlertDialog.Builder(context).setTitle(valueOf).setMessage(LocaleController.getString(R.string.EmailLoginChangeMessage)).setPositiveButton(LocaleController.getString(R.string.ChangeEmail), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i6) {
                                PrivacySettingsActivity.this.lambda$createView$8(dialogInterface, i6);
                            }
                        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
                        return;
                    }
                    if (i == this.passwordRow) {
                        TLRPC.account_Password account_password2 = this.currentPassword;
                        if (account_password2 == null) {
                            return;
                        }
                        if (!TwoStepVerificationActivity.canHandleCurrentPassword(account_password2, false)) {
                            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                        }
                        TLRPC.account_Password account_password3 = this.currentPassword;
                        if (account_password3.has_password) {
                            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                            twoStepVerificationActivity.setPassword(this.currentPassword);
                            baseFragment = twoStepVerificationActivity;
                        } else {
                            baseFragment = new TwoStepVerificationSetupActivity(TextUtils.isEmpty(account_password3.email_unconfirmed_pattern) ? 6 : 5, this.currentPassword);
                        }
                    } else {
                        if (i != this.passcodeRow) {
                            if (i != this.secretWebpageRow) {
                                if (i == this.contactsDeleteRow) {
                                    if (getParentActivity() == null) {
                                        return;
                                    }
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                                    builder2.setTitle(LocaleController.getString("SyncContactsDeleteTitle", R.string.SyncContactsDeleteTitle));
                                    builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("SyncContactsDeleteText", R.string.SyncContactsDeleteText)));
                                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() {
                                        @Override
                                        public final void onClick(DialogInterface dialogInterface, int i6) {
                                            PrivacySettingsActivity.this.lambda$createView$10(dialogInterface, i6);
                                        }
                                    });
                                    AlertDialog create = builder2.create();
                                    showDialog(create);
                                    textView = (TextView) create.getButton(-1);
                                    if (textView == null) {
                                        return;
                                    }
                                } else {
                                    if (i != this.contactsSuggestRow) {
                                        if (i == this.newChatsRow) {
                                            textCheckCell = (TextCheckCell) view;
                                            z = !this.archiveChats;
                                            this.archiveChats = z;
                                        } else if (i == this.contactsSyncRow) {
                                            z = !this.newSync;
                                            this.newSync = z;
                                            if (!(view instanceof TextCheckCell)) {
                                                return;
                                            } else {
                                                textCheckCell = (TextCheckCell) view;
                                            }
                                        } else {
                                            if (i == this.secretMapRow) {
                                                AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new Runnable() {
                                                    @Override
                                                    public final void run() {
                                                        PrivacySettingsActivity.this.lambda$createView$14();
                                                    }
                                                }, false, null);
                                                return;
                                            }
                                            if (i == this.paymentsClearRow) {
                                                AlertDialog.Builder builder3 = new AlertDialog.Builder(getParentActivity());
                                                builder3.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", R.string.PrivacyPaymentsClearAlertTitle));
                                                builder3.setMessage(LocaleController.getString("PrivacyPaymentsClearAlertText", R.string.PrivacyPaymentsClearAlertText));
                                                LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                                                linearLayout2.setOrientation(1);
                                                builder3.setView(linearLayout2);
                                                for (int i6 = 0; i6 < 2; i6++) {
                                                    if (i6 == 0) {
                                                        i2 = R.string.PrivacyClearShipping;
                                                        str = "PrivacyClearShipping";
                                                    } else {
                                                        i2 = R.string.PrivacyClearPayment;
                                                        str = "PrivacyClearPayment";
                                                    }
                                                    String string = LocaleController.getString(str, i2);
                                                    this.clear[i6] = true;
                                                    CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21, null);
                                                    checkBoxCell.setTag(Integer.valueOf(i6));
                                                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                                    checkBoxCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                                    linearLayout2.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                                                    checkBoxCell.setText(string, null, true, false);
                                                    checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                                    checkBoxCell.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public final void onClick(View view2) {
                                                            PrivacySettingsActivity.this.lambda$createView$15(view2);
                                                        }
                                                    });
                                                }
                                                builder3.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public final void onClick(DialogInterface dialogInterface, int i7) {
                                                        PrivacySettingsActivity.this.lambda$createView$18(dialogInterface, i7);
                                                    }
                                                });
                                                builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                                showDialog(builder3.create());
                                                AlertDialog create2 = builder3.create();
                                                showDialog(create2);
                                                textView = (TextView) create2.getButton(-1);
                                                if (textView == null) {
                                                    return;
                                                }
                                            } else if (i == this.passportRow) {
                                                baseFragment = new PassportActivity(5, 0L, "", "", (String) null, (String) null, (String) null, (TLRPC.TL_account_authorizationForm) null, (TLRPC.account_Password) null);
                                            } else if (i != this.botsBiometryRow) {
                                                return;
                                            } else {
                                                baseFragment = new BotBiometrySettings();
                                            }
                                        }
                                        textCheckCell.setChecked(z);
                                        return;
                                    }
                                    textCheckCell2 = (TextCheckCell) view;
                                    if (this.newSuggest) {
                                        AlertDialog.Builder builder4 = new AlertDialog.Builder(getParentActivity());
                                        builder4.setTitle(LocaleController.getString("SuggestContactsTitle", R.string.SuggestContactsTitle));
                                        builder4.setMessage(LocaleController.getString("SuggestContactsAlert", R.string.SuggestContactsAlert));
                                        builder4.setPositiveButton(LocaleController.getString("MuteDisable", R.string.MuteDisable), new DialogInterface.OnClickListener() {
                                            @Override
                                            public final void onClick(DialogInterface dialogInterface, int i7) {
                                                PrivacySettingsActivity.this.lambda$createView$13(textCheckCell2, dialogInterface, i7);
                                            }
                                        });
                                        builder4.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                        AlertDialog create3 = builder4.create();
                                        showDialog(create3);
                                        textView = (TextView) create3.getButton(-1);
                                        if (textView == null) {
                                            return;
                                        }
                                    } else {
                                        this.newSuggest = true;
                                    }
                                }
                                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                                return;
                            }
                            if (getMessagesController().secretWebpagePreview == 1) {
                                getMessagesController().secretWebpagePreview = 0;
                            } else {
                                getMessagesController().secretWebpagePreview = 1;
                            }
                            MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", getMessagesController().secretWebpagePreview).commit();
                            if (!(view instanceof TextCheckCell)) {
                                return;
                            }
                            textCheckCell2 = (TextCheckCell) view;
                            if (getMessagesController().secretWebpagePreview != 1) {
                                z2 = false;
                            }
                            textCheckCell2.setChecked(z2);
                            return;
                        }
                        baseFragment = PasscodeActivity.determineOpenFragment();
                    }
                }
            }
            presentFragment(baseFragment);
        }
    }

    public void lambda$createView$20(ArrayList arrayList) {
        this.biometryBots.clear();
        this.biometryBots.addAll(arrayList);
        updateRows(true);
    }

    public void lambda$createView$4(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_account_setAccountTTL tL_account_setAccountTTL) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            this.deleteAccountUpdate = true;
            getContactsController().setDeleteAccountTTL(tL_account_setAccountTTL.ttl.days);
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void lambda$createView$5(final AlertDialog alertDialog, final TLRPC.TL_account_setAccountTTL tL_account_setAccountTTL, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$4(alertDialog, tLObject, tL_account_setAccountTTL);
            }
        });
    }

    public void lambda$createView$6(AlertDialog.Builder builder, View view) {
        builder.getDismissRunnable().run();
        Integer num = (Integer) view.getTag();
        int i = num.intValue() == 0 ? 30 : num.intValue() == 1 ? 90 : num.intValue() == 2 ? 182 : num.intValue() == 3 ? 365 : num.intValue() == 4 ? 548 : num.intValue() == 5 ? 730 : 0;
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        final TLRPC.TL_account_setAccountTTL tL_account_setAccountTTL = new TLRPC.TL_account_setAccountTTL();
        TLRPC.TL_accountDaysTTL tL_accountDaysTTL = new TLRPC.TL_accountDaysTTL();
        tL_account_setAccountTTL.ttl = tL_accountDaysTTL;
        tL_accountDaysTTL.days = i;
        getConnectionsManager().sendRequest(tL_account_setAccountTTL, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.this.lambda$createView$5(alertDialog, tL_account_setAccountTTL, tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$7() {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), null);
        lottieLayout.setAnimation(R.raw.email_check_inbox, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString(R.string.YourLoginEmailChangedSuccess));
        Bulletin.make(this, lottieLayout, 1500).show();
        try {
            this.fragmentView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        loadPasswordSettings();
    }

    public void lambda$createView$8(DialogInterface dialogInterface, int i) {
        presentFragment(new LoginActivity().changeEmail(new Runnable() {
            @Override
            public final void run() {
                PrivacySettingsActivity.this.lambda$createView$7();
            }
        }));
    }

    public void lambda$createView$9() {
        this.progressDialog.dismiss();
    }

    public void lambda$loadPasswordSettings$21(TLRPC.account_Password account_password) {
        this.currentPassword = account_password;
        initPassword();
    }

    public void lambda$loadPasswordSettings$22(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            final TLRPC.account_Password account_password = (TLRPC.account_Password) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PrivacySettingsActivity.this.lambda$loadPasswordSettings$21(account_password);
                }
            });
        }
    }

    public void lambda$onFragmentCreate$0() {
        int i;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter == null || (i = this.sessionsRow) < 0) {
            return;
        }
        listAdapter.notifyItemChanged(i);
    }

    public void lambda$onFragmentCreate$1() {
        if (this.listAdapter != null) {
            int sessionsCount = this.webSessionsActivityPreload.getSessionsCount();
            if (this.webSessionsRow >= 0 || sessionsCount <= 0) {
                return;
            }
            updateRows();
        }
    }

    public static void lambda$onFragmentDestroy$2(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$onFragmentDestroy$3(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    private void loadPasswordSettings() {
        getConnectionsManager().sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.this.lambda$loadPasswordSettings$22(tLObject, tL_error);
            }
        }, 10);
    }

    private void updateRows() {
        updateRows(true);
    }

    private void updateRows(boolean z) {
        this.securitySectionRow = 0;
        this.passwordRow = 1;
        this.autoDeleteMesages = 2;
        this.rowCount = 4;
        this.passcodeRow = 3;
        TLRPC.account_Password account_password = this.currentPassword;
        if (account_password == null ? !SharedConfig.hasEmailLogin : account_password.login_email_pattern == null) {
            this.emailLoginRow = -1;
        } else {
            this.rowCount = 5;
            this.emailLoginRow = 4;
        }
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.blockedRow = i;
        if (account_password != null) {
            boolean z2 = account_password.login_email_pattern != null;
            if (SharedConfig.hasEmailLogin != z2) {
                SharedConfig.hasEmailLogin = z2;
                SharedConfig.saveConfig();
            }
        }
        int i2 = this.rowCount;
        this.sessionsRow = i2;
        this.sessionsDetailRow = i2 + 1;
        this.privacySectionRow = i2 + 2;
        this.phoneNumberRow = i2 + 3;
        this.lastSeenRow = i2 + 4;
        this.profilePhotoRow = i2 + 5;
        this.forwardsRow = i2 + 6;
        this.rowCount = i2 + 8;
        this.callsRow = i2 + 7;
        this.groupsDetailRow = -1;
        if (!getMessagesController().premiumFeaturesBlocked() || getUserConfig().isPremium()) {
            int i3 = this.rowCount;
            this.voicesRow = i3;
            this.rowCount = i3 + 2;
            this.noncontactsRow = i3 + 1;
        } else {
            this.voicesRow = -1;
            this.noncontactsRow = -1;
        }
        int i4 = this.rowCount;
        this.birthdayRow = i4;
        this.bioRow = i4 + 1;
        this.groupsRow = i4 + 2;
        this.rowCount = i4 + 4;
        this.privacyShadowRow = i4 + 3;
        if (getMessagesController().autoarchiveAvailable || getUserConfig().isPremium()) {
            int i5 = this.rowCount;
            this.newChatsHeaderRow = i5;
            this.newChatsRow = i5 + 1;
            this.rowCount = i5 + 3;
            this.newChatsSectionRow = i5 + 2;
        } else {
            this.newChatsHeaderRow = -1;
            this.newChatsRow = -1;
            this.newChatsSectionRow = -1;
        }
        int i6 = this.rowCount;
        this.advancedSectionRow = i6;
        this.deleteAccountRow = i6 + 1;
        this.deleteAccountDetailRow = i6 + 2;
        this.rowCount = i6 + 4;
        this.botsSectionRow = i6 + 3;
        if (getUserConfig().hasSecureData) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.passportRow = i7;
        } else {
            this.passportRow = -1;
        }
        int i8 = this.rowCount;
        this.rowCount = i8 + 1;
        this.paymentsClearRow = i8;
        if (this.biometryBots.isEmpty()) {
            this.botsBiometryRow = -1;
        } else {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.botsBiometryRow = i9;
        }
        SessionsActivity sessionsActivity = this.webSessionsActivityPreload;
        if (sessionsActivity == null || sessionsActivity.getSessionsCount() <= 0) {
            this.webSessionsRow = -1;
            this.botsDetailRow = -1;
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.botsAndWebsitesShadowRow = i10;
        } else {
            int i11 = this.rowCount;
            this.webSessionsRow = i11;
            this.rowCount = i11 + 2;
            this.botsDetailRow = i11 + 1;
            this.botsAndWebsitesShadowRow = -1;
        }
        int i12 = this.rowCount;
        this.contactsSectionRow = i12;
        this.contactsDeleteRow = i12 + 1;
        this.contactsSyncRow = i12 + 2;
        this.contactsSuggestRow = i12 + 3;
        this.contactsDetailRow = i12 + 4;
        this.secretSectionRow = i12 + 5;
        this.secretMapRow = i12 + 6;
        this.secretWebpageRow = i12 + 7;
        this.rowCount = i12 + 9;
        this.secretDetailRow = i12 + 8;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter == null || !z) {
            return;
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivacySettingsActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutAnimation(null);
        this.listView.setItemAnimator(null);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                PrivacySettingsActivity.this.lambda$createView$19(context, view, i);
            }
        });
        BotBiometry.getBots(getContext(), this.currentAccount, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                PrivacySettingsActivity.this.lambda$createView$20((ArrayList) obj);
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ListAdapter listAdapter;
        int i3;
        ListAdapter listAdapter2;
        if (i == NotificationCenter.privacyRulesUpdated) {
            TLRPC.TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
            if (globalPrivacySettings != null) {
                this.archiveChats = globalPrivacySettings.archive_and_mute_new_noncontact_peers;
                this.noncontactsValue = globalPrivacySettings.new_noncontact_peers_require_premium;
            }
            ListAdapter listAdapter3 = this.listAdapter;
            if (listAdapter3 != null) {
                listAdapter3.notifyDataSetChanged();
            }
        } else {
            if (i == NotificationCenter.blockedUsersDidLoad) {
                listAdapter = this.listAdapter;
                i3 = this.blockedRow;
            } else if (i == NotificationCenter.didSetOrRemoveTwoStepPassword) {
                if (objArr.length > 0) {
                    this.currentPassword = (TLRPC.account_Password) objArr[0];
                    listAdapter = this.listAdapter;
                    if (listAdapter != null) {
                        i3 = this.passwordRow;
                    }
                } else {
                    this.currentPassword = null;
                    loadPasswordSettings();
                    updateRows();
                }
            }
            listAdapter.notifyItemChanged(i3);
        }
        if (i != NotificationCenter.didUpdateGlobalAutoDeleteTimer || (listAdapter2 = this.listAdapter) == null) {
            return;
        }
        listAdapter2.notifyItemChanged(this.autoDeleteMesages);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
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
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        return arrayList;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getContactsController().loadPrivacySettings();
        getMessagesController().getBlockedPeers(true);
        boolean z = getUserConfig().syncContacts;
        this.newSync = z;
        this.currentSync = z;
        boolean z2 = getUserConfig().suggestContacts;
        this.newSuggest = z2;
        this.currentSuggest = z2;
        TLRPC.TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        if (globalPrivacySettings != null) {
            this.archiveChats = globalPrivacySettings.archive_and_mute_new_noncontact_peers;
            this.noncontactsValue = globalPrivacySettings.new_noncontact_peers_require_premium;
        }
        updateRows();
        loadPasswordSettings();
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.didSetOrRemoveTwoStepPassword);
        getNotificationCenter().addObserver(this, NotificationCenter.didUpdateGlobalAutoDeleteTimer);
        getUserConfig().loadGlobalTTl();
        SessionsActivity sessionsActivity = new SessionsActivity(0);
        this.devicesActivityPreload = sessionsActivity;
        sessionsActivity.setDelegate(new SessionsActivity.Delegate() {
            @Override
            public final void sessionsLoaded() {
                PrivacySettingsActivity.this.lambda$onFragmentCreate$0();
            }
        });
        this.devicesActivityPreload.lambda$loadSessions$17(false);
        SessionsActivity sessionsActivity2 = new SessionsActivity(1);
        this.webSessionsActivityPreload = sessionsActivity2;
        sessionsActivity2.setDelegate(new SessionsActivity.Delegate() {
            @Override
            public final void sessionsLoaded() {
                PrivacySettingsActivity.this.lambda$onFragmentCreate$1();
            }
        });
        this.webSessionsActivityPreload.lambda$loadSessions$17(false);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacySettingsActivity.onFragmentDestroy():void");
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public PrivacySettingsActivity setCurrentPassword(TLRPC.account_Password account_password) {
        this.currentPassword = account_password;
        if (account_password != null) {
            initPassword();
        }
        return this;
    }
}
