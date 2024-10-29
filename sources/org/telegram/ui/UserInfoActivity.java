package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.OpeningHoursActivity;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.UserInfoActivity;

public class UserInfoActivity extends UniversalFragment implements NotificationCenter.NotificationCenterDelegate {
    private EditTextCell bioEdit;
    private CharSequence bioInfo;
    private TLRPC.TL_birthday birthday;
    private CharSequence birthdayInfo;
    private TLRPC.Chat channel;
    private String currentBio;
    private TLRPC.TL_birthday currentBirthday;
    private long currentChannel;
    private String currentFirstName;
    private String currentLastName;
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private EditTextCell firstNameEdit;
    private boolean hadHours;
    private boolean hadLocation;
    private EditTextCell lastNameEdit;
    private boolean valueSet;
    private AdminedChannelsFetcher channels = new AdminedChannelsFetcher(this.currentAccount, true);
    private boolean wasSaved = false;
    private int shiftDp = -4;

    public static class AdminedChannelsFetcher {
        public final int currentAccount;
        public final boolean for_personal;
        public boolean loaded;
        public boolean loading;
        public final ArrayList chats = new ArrayList();
        private ArrayList callbacks = new ArrayList();

        public AdminedChannelsFetcher(int i, boolean z) {
            this.currentAccount = i;
            this.for_personal = z;
        }

        public void lambda$fetch$0(TLObject tLObject) {
            if (tLObject instanceof TLRPC.messages_Chats) {
                this.chats.clear();
                this.chats.addAll(((TLRPC.messages_Chats) tLObject).chats);
            }
            MessagesController.getInstance(this.currentAccount).putChats(this.chats, false);
            this.loading = false;
            this.loaded = true;
            Iterator it = this.callbacks.iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
            this.callbacks.clear();
        }

        public void lambda$fetch$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    UserInfoActivity.AdminedChannelsFetcher.this.lambda$fetch$0(tLObject);
                }
            });
        }

        public void fetch() {
            if (this.loaded || this.loading) {
                return;
            }
            this.loading = true;
            TLRPC.TL_channels_getAdminedPublicChannels tL_channels_getAdminedPublicChannels = new TLRPC.TL_channels_getAdminedPublicChannels();
            tL_channels_getAdminedPublicChannels.for_personal = this.for_personal;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getAdminedPublicChannels, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    UserInfoActivity.AdminedChannelsFetcher.this.lambda$fetch$1(tLObject, tL_error);
                }
            });
        }

        public void invalidate() {
            this.loaded = false;
        }

        public void subscribe(Runnable runnable) {
            if (this.loaded) {
                runnable.run();
            } else {
                this.callbacks.add(runnable);
            }
        }
    }

    public static class ChooseChannelFragment extends UniversalFragment {
        private AdminedChannelsFetcher channels;
        private boolean invalidateAfterPause = false;
        private String query;
        private ActionBarMenuItem searchItem;
        private long selectedChannel;
        private Utilities.Callback whenSelected;

        public ChooseChannelFragment(AdminedChannelsFetcher adminedChannelsFetcher, long j, Utilities.Callback callback) {
            this.channels = adminedChannelsFetcher;
            this.selectedChannel = j;
            this.whenSelected = callback;
            adminedChannelsFetcher.subscribe(new Runnable() {
                @Override
                public final void run() {
                    UserInfoActivity.ChooseChannelFragment.this.lambda$new$0();
                }
            });
        }

        public void lambda$new$0() {
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
        }

        public void lambda$onResume$1() {
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
        }

        @Override
        public View createView(Context context) {
            ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search, getResourceProvider()).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                @Override
                public void onSearchCollapse() {
                    ChooseChannelFragment.this.query = null;
                    UniversalRecyclerView universalRecyclerView = ChooseChannelFragment.this.listView;
                    if (universalRecyclerView != null) {
                        universalRecyclerView.adapter.update(true);
                    }
                }

                @Override
                public void onSearchExpand() {
                }

                @Override
                public void onTextChanged(EditText editText) {
                    ChooseChannelFragment.this.query = editText.getText().toString();
                    UniversalRecyclerView universalRecyclerView = ChooseChannelFragment.this.listView;
                    if (universalRecyclerView != null) {
                        universalRecyclerView.adapter.update(true);
                    }
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            int i = R.string.Search;
            actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(i));
            this.searchItem.setContentDescription(LocaleController.getString(i));
            this.searchItem.setVisibility(8);
            super.createView(context);
            return this.fragmentView;
        }

        @Override
        public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
            if (TextUtils.isEmpty(this.query)) {
                arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EditProfileChannelSelect)));
            }
            if (TextUtils.isEmpty(this.query) && this.selectedChannel != 0) {
                arrayList.add(UItem.asButton(1, R.drawable.msg_archive_hide, LocaleController.getString(R.string.EditProfileChannelHide)).accent());
            }
            Iterator it = this.channels.chats.iterator();
            int i = 0;
            while (it.hasNext()) {
                TLRPC.Chat chat = (TLRPC.Chat) it.next();
                if (chat != null && !ChatObject.isMegagroup(chat)) {
                    i++;
                    if (!TextUtils.isEmpty(this.query)) {
                        String lowerCase = this.query.toLowerCase();
                        String translitSafe = AndroidUtilities.translitSafe(lowerCase);
                        String lowerCase2 = chat.title.toLowerCase();
                        String translitSafe2 = AndroidUtilities.translitSafe(lowerCase2);
                        if (!lowerCase2.startsWith(lowerCase)) {
                            if (!lowerCase2.contains(" " + lowerCase) && !translitSafe2.startsWith(translitSafe)) {
                                if (!translitSafe2.contains(" " + translitSafe)) {
                                }
                            }
                        }
                    }
                    arrayList.add(UItem.asFilterChat(true, -chat.id).setChecked(this.selectedChannel == chat.id));
                }
            }
            if (TextUtils.isEmpty(this.query) && i == 0) {
                arrayList.add(UItem.asButton(2, R.drawable.msg_channel_create, LocaleController.getString(R.string.EditProfileChannelStartNew)).accent());
            }
            arrayList.add(UItem.asShadow(null));
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(i <= 5 ? 8 : 0);
            }
        }

        @Override
        protected CharSequence getTitle() {
            return LocaleController.getString(R.string.EditProfileChannelTitle);
        }

        @Override
        public void onClick(UItem uItem, View view, int i, float f, float f2) {
            int i2 = uItem.id;
            if (i2 == 1) {
                this.whenSelected.run(null);
                lambda$onBackPressed$319();
                return;
            }
            if (i2 != 2) {
                if (uItem.viewType == 12) {
                    lambda$onBackPressed$319();
                    this.whenSelected.run(getMessagesController().getChat(Long.valueOf(-uItem.dialogId)));
                    return;
                }
                return;
            }
            this.invalidateAfterPause = true;
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean("channel_intro", false)) {
                presentFragment(new ActionIntroActivity(0));
                globalMainSettings.edit().putBoolean("channel_intro", true).apply();
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(bundle));
            }
        }

        @Override
        public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
            return false;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (this.invalidateAfterPause) {
                this.channels.invalidate();
                this.channels.subscribe(new Runnable() {
                    @Override
                    public final void run() {
                        UserInfoActivity.ChooseChannelFragment.this.lambda$onResume$1();
                    }
                });
                this.invalidateAfterPause = false;
            }
        }
    }

    public static String birthdayString(TLRPC.TL_birthday tL_birthday) {
        Calendar calendar;
        FastDateFormat formatterDayMonth;
        if (tL_birthday == null) {
            return "—";
        }
        if ((tL_birthday.flags & 1) != 0) {
            calendar = Calendar.getInstance();
            calendar.set(1, tL_birthday.year);
            calendar.set(2, tL_birthday.month - 1);
            calendar.set(5, tL_birthday.day);
            formatterDayMonth = LocaleController.getInstance().getFormatterBoostExpired();
        } else {
            calendar = Calendar.getInstance();
            calendar.set(2, tL_birthday.month - 1);
            calendar.set(5, tL_birthday.day);
            formatterDayMonth = LocaleController.getInstance().getFormatterDayMonth();
        }
        return formatterDayMonth.format(calendar.getTimeInMillis());
    }

    public static boolean birthdaysEqual(TLRPC.TL_birthday tL_birthday, TLRPC.TL_birthday tL_birthday2) {
        if ((tL_birthday == null) != (tL_birthday2 != null)) {
            return tL_birthday == null || (tL_birthday.day == tL_birthday2.day && tL_birthday.month == tL_birthday2.month && tL_birthday.year == tL_birthday2.year);
        }
        return false;
    }

    public void checkDone(boolean z) {
        if (this.doneButton == null) {
            return;
        }
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        if (z) {
            this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f).scaleY(hasChanges ? 1.0f : 0.0f).setDuration(180L).start();
            return;
        }
        this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleY(hasChanges ? 1.0f : 0.0f);
    }

    public void lambda$createView$0() {
        presentFragment(new PrivacyControlActivity(9, true));
    }

    public void lambda$fillItems$1() {
        presentFragment(new PrivacyControlActivity(11));
    }

    public void lambda$onClick$2(TLRPC.TL_birthday tL_birthday) {
        this.birthday = tL_birthday;
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
        checkDone(true);
    }

    public void lambda$onClick$3(TLRPC.Chat chat) {
        if (this.channel == chat) {
            return;
        }
        this.channel = chat;
        if (chat != null) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.EditProfileChannelSet)).show();
        }
        checkDone(true);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }

    public void lambda$onResume$4() {
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }

    public void lambda$processDone$5(TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_birthday tL_birthday, TLRPC.UserFull userFull, TLObject tLObject2, int[] iArr, ArrayList arrayList) {
        String str;
        if (tL_error == null) {
            if (tLObject2 instanceof TLRPC.TL_boolFalse) {
                this.doneButtonDrawable.animateToProgress(0.0f);
                BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
                return;
            }
            this.wasSaved = true;
            int i = iArr[0] + 1;
            iArr[0] = i;
            if (i == arrayList.size()) {
                lambda$onBackPressed$319();
                return;
            }
            return;
        }
        this.doneButtonDrawable.animateToProgress(0.0f);
        boolean z = tLObject instanceof TLRPC.TL_account_updateBirthday;
        if (!z || (str = tL_error.text) == null || !str.startsWith("FLOOD_WAIT_")) {
            BulletinFactory.showError(tL_error);
        } else if (getContext() != null) {
            showDialog(new AlertDialog.Builder(getContext(), this.resourceProvider).setTitle(LocaleController.getString(R.string.PrivacyBirthdayTooOftenTitle)).setMessage(LocaleController.getString(R.string.PrivacyBirthdayTooOftenMessage)).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
        }
        if (z) {
            int i2 = userFull.flags;
            userFull.flags = tL_birthday != null ? i2 | 32 : i2 & (-33);
            userFull.birthday = tL_birthday;
            getMessagesStorage().updateUserInfo(userFull, false);
        }
    }

    public void lambda$processDone$6(final TLObject tLObject, final TLRPC.TL_birthday tL_birthday, final TLRPC.UserFull userFull, final int[] iArr, final ArrayList arrayList, final TLObject tLObject2, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                UserInfoActivity.this.lambda$processDone$5(tL_error, tLObject, tL_birthday, userFull, tLObject2, iArr, arrayList);
            }
        });
    }

    public void processDone(boolean z) {
        if (this.doneButtonDrawable.getProgress() > 0.0f) {
            return;
        }
        if (z && TextUtils.isEmpty(this.firstNameEdit.getText())) {
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
            EditTextCell editTextCell = this.firstNameEdit;
            int i = -this.shiftDp;
            this.shiftDp = i;
            AndroidUtilities.shakeViewSpring(editTextCell, i);
            return;
        }
        this.doneButtonDrawable.animateToProgress(1.0f);
        TLRPC.User currentUser = getUserConfig().getCurrentUser();
        final TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        if (currentUser == null || userFull == null) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(this.firstNameEdit.getText()) && (!TextUtils.equals(this.currentFirstName, this.firstNameEdit.getText().toString()) || !TextUtils.equals(this.currentLastName, this.lastNameEdit.getText().toString()) || !TextUtils.equals(this.currentBio, this.bioEdit.getText().toString()))) {
            TLRPC.TL_account_updateProfile tL_account_updateProfile = new TLRPC.TL_account_updateProfile();
            tL_account_updateProfile.flags |= 1;
            String charSequence = this.firstNameEdit.getText().toString();
            currentUser.first_name = charSequence;
            tL_account_updateProfile.first_name = charSequence;
            tL_account_updateProfile.flags |= 2;
            String charSequence2 = this.lastNameEdit.getText().toString();
            currentUser.last_name = charSequence2;
            tL_account_updateProfile.last_name = charSequence2;
            tL_account_updateProfile.flags |= 4;
            String charSequence3 = this.bioEdit.getText().toString();
            userFull.about = charSequence3;
            tL_account_updateProfile.about = charSequence3;
            userFull.flags = TextUtils.isEmpty(charSequence3) ? userFull.flags & (-3) : userFull.flags | 2;
            arrayList.add(tL_account_updateProfile);
        }
        final TLRPC.TL_birthday tL_birthday = userFull.birthday;
        if (!birthdaysEqual(this.currentBirthday, this.birthday)) {
            TLRPC.TL_account_updateBirthday tL_account_updateBirthday = new TLRPC.TL_account_updateBirthday();
            TLRPC.TL_birthday tL_birthday2 = this.birthday;
            if (tL_birthday2 != null) {
                userFull.flags2 |= 32;
                userFull.birthday = tL_birthday2;
                tL_account_updateBirthday.flags |= 1;
                tL_account_updateBirthday.birthday = tL_birthday2;
            } else {
                userFull.flags2 &= -33;
                userFull.birthday = null;
            }
            arrayList.add(tL_account_updateBirthday);
            getMessagesController().invalidateContentSettings();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.premiumPromoUpdated, new Object[0]);
        }
        long j = this.currentChannel;
        TLRPC.Chat chat = this.channel;
        if (j != (chat != null ? chat.id : 0L)) {
            TLRPC.TL_account_updatePersonalChannel tL_account_updatePersonalChannel = new TLRPC.TL_account_updatePersonalChannel();
            tL_account_updatePersonalChannel.channel = MessagesController.getInputChannel(this.channel);
            TLRPC.Chat chat2 = this.channel;
            if (chat2 != null) {
                userFull.flags |= 64;
                long j2 = userFull.personal_channel_id;
                long j3 = chat2.id;
                if (j2 != j3) {
                    userFull.personal_channel_message = 0;
                }
                userFull.personal_channel_id = j3;
            } else {
                userFull.flags &= -65;
                userFull.personal_channel_message = 0;
                userFull.personal_channel_id = 0L;
            }
            arrayList.add(tL_account_updatePersonalChannel);
        }
        if (arrayList.isEmpty()) {
            lambda$onBackPressed$319();
            return;
        }
        final int[] iArr = {0};
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            final TLObject tLObject = (TLObject) arrayList.get(i2);
            getConnectionsManager().sendRequest(tLObject, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    UserInfoActivity.this.lambda$processDone$6(tLObject, tL_birthday, userFull, iArr, arrayList, tLObject2, tL_error);
                }
            }, 1024);
        }
        getMessagesStorage().updateUserInfo(userFull, false);
        getUserConfig().saveConfig(true);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
    }

    private void setValue() {
        TLRPC.Chat chat;
        UniversalAdapter universalAdapter;
        if (this.valueSet) {
            return;
        }
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        if (userFull == null) {
            getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, getClassGuid());
            return;
        }
        TLRPC.User user = userFull.user;
        if (user == null) {
            user = getUserConfig().getCurrentUser();
        }
        if (user == null) {
            return;
        }
        EditTextCell editTextCell = this.firstNameEdit;
        String str = user.first_name;
        this.currentFirstName = str;
        editTextCell.setText(str);
        EditTextCell editTextCell2 = this.lastNameEdit;
        String str2 = user.last_name;
        this.currentLastName = str2;
        editTextCell2.setText(str2);
        EditTextCell editTextCell3 = this.bioEdit;
        String str3 = userFull.about;
        this.currentBio = str3;
        editTextCell3.setText(str3);
        TLRPC.TL_birthday tL_birthday = userFull.birthday;
        this.currentBirthday = tL_birthday;
        this.birthday = tL_birthday;
        if ((userFull.flags2 & 64) != 0) {
            this.currentChannel = userFull.personal_channel_id;
            chat = getMessagesController().getChat(Long.valueOf(this.currentChannel));
        } else {
            this.currentChannel = 0L;
            chat = null;
        }
        this.channel = chat;
        this.hadHours = userFull.business_work_hours != null;
        this.hadLocation = userFull.business_location != null;
        checkDone(true);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        this.valueSet = true;
    }

    @Override
    public View createView(Context context) {
        boolean z = false;
        EditTextCell editTextCell = new EditTextCell(context, LocaleController.getString(R.string.EditProfileFirstName), z, false, -1, this.resourceProvider) {
            @Override
            public void onTextChanged(CharSequence charSequence) {
                super.onTextChanged(charSequence);
                UserInfoActivity.this.checkDone(true);
            }
        };
        this.firstNameEdit = editTextCell;
        int i = Theme.key_windowBackgroundWhite;
        editTextCell.setBackgroundColor(getThemedColor(i));
        this.firstNameEdit.setDivider(true);
        this.firstNameEdit.hideKeyboardOnEnter();
        boolean z2 = false;
        boolean z3 = false;
        EditTextCell editTextCell2 = new EditTextCell(context, LocaleController.getString(R.string.EditProfileLastName), z3, z2, -1, this.resourceProvider) {
            @Override
            public void onTextChanged(CharSequence charSequence) {
                super.onTextChanged(charSequence);
                UserInfoActivity.this.checkDone(true);
            }
        };
        this.lastNameEdit = editTextCell2;
        editTextCell2.setBackgroundColor(getThemedColor(i));
        this.lastNameEdit.hideKeyboardOnEnter();
        EditTextCell editTextCell3 = new EditTextCell(context, LocaleController.getString(R.string.EditProfileBioHint), true, z2, getMessagesController().getAboutLimit(), this.resourceProvider) {
            @Override
            public void onTextChanged(CharSequence charSequence) {
                super.onTextChanged(charSequence);
                UserInfoActivity.this.checkDone(true);
            }
        };
        this.bioEdit = editTextCell3;
        editTextCell3.setBackgroundColor(getThemedColor(i));
        this.bioEdit.setShowLimitWhenEmpty(true);
        this.bioInfo = AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.EditProfileBioInfo), new Runnable() {
            @Override
            public final void run() {
                UserInfoActivity.this.lambda$createView$0();
            }
        });
        super.createView(context);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    if (UserInfoActivity.this.onBackPressed()) {
                        UserInfoActivity.this.lambda$onBackPressed$319();
                    }
                } else if (i2 == 1) {
                    UserInfoActivity.this.processDone(true);
                }
            }
        });
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
        int i2 = Theme.key_actionBarDefaultIcon;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), PorterDuff.Mode.MULTIPLY));
        this.doneButtonDrawable = new CrossfadeDrawable(mutate, new CircularProgressDrawable(Theme.getColor(i2)));
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, this.doneButtonDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString(R.string.Done));
        checkDone(false);
        setValue();
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        if (i == NotificationCenter.userInfoDidLoad) {
            setValue();
        } else {
            if (i != NotificationCenter.privacyRulesUpdated || (universalRecyclerView = this.listView) == null) {
                return;
            }
            universalRecyclerView.adapter.update(true);
        }
    }

    @Override
    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        ArrayList<TLRPC.PrivacyRule> privacyRules;
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EditProfileName)));
        arrayList.add(UItem.asCustom(this.firstNameEdit));
        arrayList.add(UItem.asCustom(this.lastNameEdit));
        arrayList.add(UItem.asShadow(-1, null));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EditProfileChannel)));
        String string = LocaleController.getString(R.string.EditProfileChannelTitle);
        TLRPC.Chat chat = this.channel;
        arrayList.add(UItem.asButton(3, string, chat == null ? LocaleController.getString(R.string.EditProfileChannelAdd) : chat.title));
        arrayList.add(UItem.asShadow(-2, null));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EditProfileBio)));
        arrayList.add(UItem.asCustom(this.bioEdit));
        arrayList.add(UItem.asShadow(this.bioInfo));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EditProfileBirthday)));
        String string2 = LocaleController.getString(R.string.EditProfileBirthdayText);
        TLRPC.TL_birthday tL_birthday = this.birthday;
        arrayList.add(UItem.asButton(1, string2, tL_birthday == null ? LocaleController.getString(R.string.EditProfileBirthdayAdd) : birthdayString(tL_birthday)));
        if (this.birthday != null) {
            arrayList.add(UItem.asButton(2, LocaleController.getString(R.string.EditProfileBirthdayRemove)).red());
        }
        if (!getContactsController().getLoadingPrivacyInfo(11) && (privacyRules = getContactsController().getPrivacyRules(11)) != null && this.birthdayInfo == null) {
            String string3 = LocaleController.getString(R.string.EditProfileBirthdayInfoContacts);
            if (!privacyRules.isEmpty()) {
                int i = 0;
                while (true) {
                    if (i >= privacyRules.size()) {
                        break;
                    }
                    if (privacyRules.get(i) instanceof TLRPC.TL_privacyValueAllowContacts) {
                        string3 = LocaleController.getString(R.string.EditProfileBirthdayInfoContacts);
                        break;
                    }
                    if ((privacyRules.get(i) instanceof TLRPC.TL_privacyValueAllowAll) || (privacyRules.get(i) instanceof TLRPC.TL_privacyValueDisallowAll)) {
                        string3 = LocaleController.getString(R.string.EditProfileBirthdayInfo);
                    }
                    i++;
                }
            }
            this.birthdayInfo = AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(string3, new Runnable() {
                @Override
                public final void run() {
                    UserInfoActivity.this.lambda$fillItems$1();
                }
            }), true);
        }
        arrayList.add(UItem.asShadow(this.birthdayInfo));
        if (this.hadLocation) {
            arrayList.add(UItem.asButton(4, R.drawable.menu_premium_clock, LocaleController.getString(R.string.EditProfileHours)));
        }
        if (this.hadLocation) {
            arrayList.add(UItem.asButton(5, R.drawable.msg_map, LocaleController.getString(R.string.EditProfileLocation)));
        }
        if (this.hadLocation || this.hadHours) {
            arrayList.add(UItem.asShadow(-3, null));
        }
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.EditProfileInfo);
    }

    public boolean hasChanges() {
        String str = this.currentFirstName;
        if (str == null) {
            str = "";
        }
        if (TextUtils.equals(str, this.firstNameEdit.getText().toString())) {
            String str2 = this.currentLastName;
            if (str2 == null) {
                str2 = "";
            }
            if (TextUtils.equals(str2, this.lastNameEdit.getText().toString())) {
                String str3 = this.currentBio;
                if (TextUtils.equals(str3 != null ? str3 : "", this.bioEdit.getText().toString()) && birthdaysEqual(this.currentBirthday, this.birthday)) {
                    long j = this.currentChannel;
                    TLRPC.Chat chat = this.channel;
                    if (j == (chat != null ? chat.id : 0L)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        BaseFragment openingHoursActivity;
        int i2 = uItem.id;
        if (i2 == 1) {
            showDialog(AlertsCreator.createBirthdayPickerDialog(getContext(), LocaleController.getString(R.string.EditProfileBirthdayTitle), LocaleController.getString(R.string.EditProfileBirthdayButton), this.birthday, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    UserInfoActivity.this.lambda$onClick$2((TLRPC.TL_birthday) obj);
                }
            }, null, getResourceProvider()).create());
            return;
        }
        if (i2 == 2) {
            this.birthday = null;
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
            checkDone(true);
            return;
        }
        if (i2 == 3) {
            AdminedChannelsFetcher adminedChannelsFetcher = this.channels;
            TLRPC.Chat chat = this.channel;
            openingHoursActivity = new ChooseChannelFragment(adminedChannelsFetcher, chat == null ? 0L : chat.id, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    UserInfoActivity.this.lambda$onClick$3((TLRPC.Chat) obj);
                }
            });
        } else if (i2 == 5) {
            openingHoursActivity = new org.telegram.ui.Business.LocationActivity();
        } else if (i2 != 4) {
            return;
        } else {
            openingHoursActivity = new OpeningHoursActivity();
        }
        presentFragment(openingHoursActivity);
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        getContactsController().loadPrivacySettings();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        super.onFragmentDestroy();
        if (this.wasSaved) {
            return;
        }
        processDone(false);
    }

    @Override
    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.channels.invalidate();
        this.channels.subscribe(new Runnable() {
            @Override
            public final void run() {
                UserInfoActivity.this.lambda$onResume$4();
            }
        });
        this.channels.fetch();
        this.birthdayInfo = null;
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }
}
