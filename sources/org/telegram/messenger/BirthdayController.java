package org.telegram.messenger;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;

public class BirthdayController {
    private static volatile BirthdayController[] Instance = new BirthdayController[4];
    private static final Object[] lockObjects = new Object[4];
    private final int currentAccount;
    private final Set<String> hiddenDays;
    private long lastCheckDate;
    private boolean loading;
    private BirthdayState state;

    public static class BirthdayState {
        public String todayKey;
        public String tomorrowKey;
        public String yesterdayKey;
        public final ArrayList<TLRPC.User> yesterday = new ArrayList<>();
        public final ArrayList<TLRPC.User> today = new ArrayList<>();
        public final ArrayList<TLRPC.User> tomorrow = new ArrayList<>();

        private BirthdayState(String str, String str2, String str3) {
            this.yesterdayKey = str;
            this.todayKey = str2;
            this.tomorrowKey = str3;
        }

        public static BirthdayState from(TL_account.contactBirthdays contactbirthdays) {
            Iterator<TL_account.TL_contactBirthday> it;
            int i;
            Calendar calendar = Calendar.getInstance();
            int i2 = calendar.get(5);
            int i3 = calendar.get(2) + 1;
            int i4 = calendar.get(1);
            calendar.add(5, -1);
            int i5 = calendar.get(5);
            int i6 = calendar.get(2) + 1;
            int i7 = calendar.get(1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(5, 1);
            int i8 = calendar2.get(5);
            int i9 = calendar2.get(2) + 1;
            String str = i5 + "_" + i6 + "_" + i7;
            String str2 = i2 + "_" + i3 + "_" + i4;
            BirthdayState birthdayState = new BirthdayState(str, str2, i8 + "_" + i9 + "_" + calendar2.get(1));
            Iterator<TL_account.TL_contactBirthday> it2 = contactbirthdays.contacts.iterator();
            while (it2.hasNext()) {
                TL_account.TL_contactBirthday next = it2.next();
                TL_account.TL_birthday tL_birthday = next.birthday;
                int i10 = tL_birthday.day;
                TLRPC.User user = null;
                ArrayList<TLRPC.User> arrayList = (i10 == i2 && tL_birthday.month == i3) ? birthdayState.today : (i10 == i5 && tL_birthday.month == i6) ? birthdayState.yesterday : (i10 == i8 && tL_birthday.month == i9) ? birthdayState.tomorrow : null;
                if (arrayList != null) {
                    int i11 = 0;
                    while (true) {
                        if (i11 >= contactbirthdays.users.size()) {
                            it = it2;
                            i = i8;
                            break;
                        }
                        it = it2;
                        i = i8;
                        if (contactbirthdays.users.get(i11).id == next.contact_id) {
                            user = contactbirthdays.users.get(i11);
                            break;
                        }
                        i11++;
                        i8 = i;
                        it2 = it;
                    }
                    if (user != null && !UserObject.isUserSelf(user)) {
                        arrayList.add(user);
                    }
                    i8 = i;
                    it2 = it;
                }
            }
            return birthdayState;
        }

        public boolean contains(long j) {
            Iterator<TLRPC.User> it = this.yesterday.iterator();
            while (it.hasNext()) {
                if (it.next().id == j) {
                    return true;
                }
            }
            Iterator<TLRPC.User> it2 = this.today.iterator();
            while (it2.hasNext()) {
                if (it2.next().id == j) {
                    return true;
                }
            }
            Iterator<TLRPC.User> it3 = this.tomorrow.iterator();
            while (it3.hasNext()) {
                if (it3.next().id == j) {
                    return true;
                }
            }
            return false;
        }

        public boolean isTodayEmpty() {
            return this.today.isEmpty();
        }
    }

    public static class TL_birthdays extends TLObject {
        public static final int constructor = 290452237;
        public ArrayList<TL_account.TL_contactBirthday> contacts;

        private TL_birthdays() {
            this.contacts = new ArrayList<>();
        }

        public static TL_birthdays TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (i != 290452237) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_birthdays", Integer.valueOf(i)));
                }
                return null;
            }
            TL_birthdays tL_birthdays = new TL_birthdays();
            tL_birthdays.readParams(inputSerializedData, z);
            return tL_birthdays;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
            } else {
                int readInt322 = inputSerializedData.readInt32(z);
                for (int i = 0; i < readInt322; i++) {
                    this.contacts.add(TL_account.TL_contactBirthday.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z));
                }
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(290452237);
            outputSerializedData.writeInt32(481674261);
            int size = this.contacts.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.contacts.get(i).serializeToStream(outputSerializedData);
            }
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private BirthdayController(final int i) {
        this.currentAccount = i;
        SharedPreferences mainSettings = MessagesController.getInstance(i).getMainSettings();
        this.lastCheckDate = mainSettings.getLong("bday_check", 0L);
        String string = mainSettings.getString("bday_contacts", null);
        if (string != null) {
            try {
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
                final TL_birthdays TLdeserialize = TL_birthdays.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                if (TLdeserialize != null && !TLdeserialize.contacts.isEmpty()) {
                    final ArrayList arrayList = new ArrayList();
                    for (int i2 = 0; i2 < TLdeserialize.contacts.size(); i2++) {
                        arrayList.add(Long.valueOf(TLdeserialize.contacts.get(i2).contact_id));
                    }
                    MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable() {
                        @Override
                        public final void run() {
                            BirthdayController.this.lambda$new$1(i, arrayList, TLdeserialize);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.hiddenDays = mainSettings.getStringSet("bday_hidden", new HashSet());
    }

    public static BirthdayController getInstance(int i) {
        BirthdayController birthdayController = Instance[i];
        if (birthdayController == null) {
            synchronized (lockObjects[i]) {
                try {
                    birthdayController = Instance[i];
                    if (birthdayController == null) {
                        BirthdayController[] birthdayControllerArr = Instance;
                        BirthdayController birthdayController2 = new BirthdayController(i);
                        birthdayControllerArr[i] = birthdayController2;
                        birthdayController = birthdayController2;
                    }
                } finally {
                }
            }
        }
        return birthdayController;
    }

    public static boolean isToday(TLRPC.UserFull userFull) {
        if (userFull == null) {
            return false;
        }
        return isToday(userFull.birthday);
    }

    public static boolean isToday(TL_account.TL_birthday tL_birthday) {
        if (tL_birthday == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        return tL_birthday.day == calendar.get(5) && tL_birthday.month == calendar.get(2) + 1;
    }

    public void lambda$check$2(TLObject tLObject) {
        if (tLObject instanceof TL_account.contactBirthdays) {
            this.lastCheckDate = System.currentTimeMillis();
            TL_account.contactBirthdays contactbirthdays = (TL_account.contactBirthdays) tLObject;
            this.state = BirthdayState.from(contactbirthdays);
            MessagesController.getInstance(this.currentAccount).putUsers(contactbirthdays.users, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(contactbirthdays.users, null, true, true);
            SharedPreferences.Editor edit = MessagesController.getInstance(this.currentAccount).getMainSettings().edit();
            edit.putLong("bday_check", this.lastCheckDate);
            TL_birthdays tL_birthdays = new TL_birthdays();
            tL_birthdays.contacts = contactbirthdays.contacts;
            SerializedData serializedData = new SerializedData(tL_birthdays.getObjectSize());
            tL_birthdays.serializeToStream(serializedData);
            edit.putString("bday_contacts", Utilities.bytesToHex(serializedData.toByteArray()));
            edit.apply();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.premiumPromoUpdated, new Object[0]);
            this.loading = false;
        }
    }

    public void lambda$check$3(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BirthdayController.this.lambda$check$2(tLObject);
            }
        });
    }

    public void lambda$new$0(TL_birthdays tL_birthdays, ArrayList arrayList) {
        TL_account.contactBirthdays contactbirthdays = new TL_account.contactBirthdays();
        contactbirthdays.contacts = tL_birthdays.contacts;
        contactbirthdays.users = arrayList;
        this.state = BirthdayState.from(contactbirthdays);
    }

    public void lambda$new$1(int i, ArrayList arrayList, final TL_birthdays tL_birthdays) {
        final ArrayList<TLRPC.User> users = MessagesStorage.getInstance(i).getUsers(arrayList);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BirthdayController.this.lambda$new$0(tL_birthdays, users);
            }
        });
    }

    public void check() {
        if (this.loading) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.lastCheckDate;
        boolean z = false;
        boolean z2 = j == 0;
        if (!z2) {
            z2 = currentTimeMillis - j > ((long) (BuildVars.DEBUG_PRIVATE_VERSION ? 25000 : 43200000));
        }
        if (z2) {
            z = z2;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(this.lastCheckDate);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(currentTimeMillis);
            if (calendar.get(5) != calendar2.get(5) || calendar.get(2) != calendar2.get(2) || calendar.get(1) != calendar2.get(1)) {
                z = true;
            }
        }
        if (z) {
            this.loading = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account.getBirthdays(), new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BirthdayController.this.lambda$check$3(tLObject, tL_error);
                }
            });
        }
    }

    public boolean contains() {
        BirthdayState state = getState();
        return (state == null || state.isTodayEmpty()) ? false : true;
    }

    public boolean contains(long j) {
        BirthdayState state = getState();
        return state != null && state.contains(j);
    }

    public BirthdayState getState() {
        BirthdayState birthdayState = this.state;
        if (birthdayState == null || this.hiddenDays.contains(birthdayState.todayKey)) {
            return null;
        }
        return this.state;
    }

    public void hide() {
        BirthdayState birthdayState = this.state;
        if (birthdayState == null || this.hiddenDays.contains(birthdayState.todayKey)) {
            return;
        }
        this.hiddenDays.add(this.state.todayKey);
        SharedPreferences.Editor edit = MessagesController.getInstance(this.currentAccount).getMainSettings().edit();
        edit.putStringSet("bday_hidden", this.hiddenDays);
        edit.apply();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.premiumPromoUpdated, new Object[0]);
    }

    public boolean isToday(long j) {
        BirthdayState birthdayState = this.state;
        if (birthdayState != null && birthdayState.contains(j)) {
            return true;
        }
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(j);
        return userFull != null && isToday(userFull.birthday);
    }
}
