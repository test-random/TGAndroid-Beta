package org.telegram.ui.Business;

import android.content.SharedPreferences;
import android.text.TextUtils;
import j$.time.Instant;
import j$.time.ZoneId;
import j$.time.format.TextStyle;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class TimezonesController {
    private static volatile TimezonesController[] Instance = new TimezonesController[4];
    private static final Object[] lockObjects = new Object[4];
    public final int currentAccount;
    private boolean loaded;
    private boolean loading;
    private final ArrayList timezones = new ArrayList();

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private TimezonesController(int i) {
        this.currentAccount = i;
    }

    public static TimezonesController getInstance(int i) {
        TimezonesController timezonesController = Instance[i];
        if (timezonesController == null) {
            synchronized (lockObjects[i]) {
                try {
                    timezonesController = Instance[i];
                    if (timezonesController == null) {
                        TimezonesController[] timezonesControllerArr = Instance;
                        TimezonesController timezonesController2 = new TimezonesController(i);
                        timezonesControllerArr[i] = timezonesController2;
                        timezonesController = timezonesController2;
                    }
                } finally {
                }
            }
        }
        return timezonesController;
    }

    public void lambda$load$0(TLObject tLObject, SharedPreferences sharedPreferences) {
        if (tLObject instanceof TLRPC.TL_help_timezonesList) {
            this.timezones.clear();
            this.timezones.addAll(((TLRPC.TL_help_timezonesList) tLObject).timezones);
            SerializedData serializedData = new SerializedData(tLObject.getObjectSize());
            tLObject.serializeToStream(serializedData);
            sharedPreferences.edit().putString("timezones", Utilities.bytesToHex(serializedData.toByteArray())).apply();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.timezonesUpdated, new Object[0]);
        }
        this.loaded = true;
        this.loading = false;
    }

    public void lambda$load$1(final SharedPreferences sharedPreferences, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TimezonesController.this.lambda$load$0(tLObject, sharedPreferences);
            }
        });
    }

    public TLRPC.TL_timezone findTimezone(String str) {
        if (str == null) {
            return null;
        }
        load();
        for (int i = 0; i < this.timezones.size(); i++) {
            TLRPC.TL_timezone tL_timezone = (TLRPC.TL_timezone) this.timezones.get(i);
            if (TextUtils.equals(tL_timezone.id, str)) {
                return tL_timezone;
            }
        }
        return null;
    }

    public String getSystemTimezoneId() {
        ZoneId systemDefault = ZoneId.systemDefault();
        String id = systemDefault != null ? systemDefault.getId() : null;
        if (this.loading || !this.loaded) {
            load();
            return id;
        }
        for (int i = 0; i < this.timezones.size(); i++) {
            if (TextUtils.equals(((TLRPC.TL_timezone) this.timezones.get(i)).id, id)) {
                return id;
            }
        }
        int totalSeconds = systemDefault != null ? systemDefault.getRules().getOffset(Instant.now()).getTotalSeconds() : 0;
        for (int i2 = 0; i2 < this.timezones.size(); i2++) {
            TLRPC.TL_timezone tL_timezone = (TLRPC.TL_timezone) this.timezones.get(i2);
            if (totalSeconds == tL_timezone.utc_offset) {
                return tL_timezone.id;
            }
        }
        return !this.timezones.isEmpty() ? ((TLRPC.TL_timezone) this.timezones.get(0)).id : id;
    }

    public String getTimezoneName(String str, boolean z) {
        String str2;
        TLRPC.TL_timezone findTimezone = findTimezone(str);
        if (findTimezone != null) {
            return getTimezoneName(findTimezone, z);
        }
        ZoneId of = ZoneId.of(str);
        String str3 = "";
        if (of == null) {
            return "";
        }
        if (z) {
            String displayName = of.getRules().getOffset(Instant.now()).getDisplayName(TextStyle.FULL, LocaleController.getInstance().getCurrentLocale());
            str2 = "GMT";
            if (displayName.length() != 1 || displayName.charAt(0) != 'Z') {
                str2 = "GMT" + displayName;
            }
        } else {
            str2 = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(of.getId().replace("/", ", ").replace("_", " "));
        if (str2 != null) {
            str3 = ", " + str2;
        }
        sb.append(str3);
        return sb.toString();
    }

    public String getTimezoneName(TLRPC.TL_timezone tL_timezone, boolean z) {
        if (tL_timezone == null) {
            return null;
        }
        if (!z) {
            return tL_timezone.name;
        }
        return tL_timezone.name + ", " + getTimezoneOffsetName(tL_timezone);
    }

    public String getTimezoneOffsetName(TLRPC.TL_timezone tL_timezone) {
        if (tL_timezone.utc_offset == 0) {
            return "GMT";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("GMT");
        sb.append(tL_timezone.utc_offset < 0 ? "-" : "+");
        String sb2 = sb.toString();
        int abs = Math.abs(tL_timezone.utc_offset) / 60;
        int i = abs / 60;
        int i2 = abs % 60;
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(i < 10 ? "0" : "");
        sb3.append(i);
        String str = sb3.toString() + ":";
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(i2 < 10 ? "0" : "");
        sb4.append(i2);
        return sb4.toString();
    }

    public ArrayList getTimezones() {
        load();
        return this.timezones;
    }

    public void load() {
        if (this.loading || this.loaded) {
            return;
        }
        this.loading = true;
        final SharedPreferences mainSettings = MessagesController.getInstance(this.currentAccount).getMainSettings();
        TLRPC.help_timezonesList help_timezoneslist = null;
        String string = mainSettings.getString("timezones", null);
        if (string != null) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
            help_timezoneslist = TLRPC.help_timezonesList.TLdeserialize(serializedData, serializedData.readInt32(false), false);
        }
        this.timezones.clear();
        if (help_timezoneslist != null) {
            this.timezones.addAll(help_timezoneslist.timezones);
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.timezonesUpdated, new Object[0]);
        TLRPC.TL_help_getTimezonesList tL_help_getTimezonesList = new TLRPC.TL_help_getTimezonesList();
        tL_help_getTimezonesList.hash = help_timezoneslist != null ? help_timezoneslist.hash : 0;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getTimezonesList, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TimezonesController.this.lambda$load$1(mainSettings, tLObject, tL_error);
            }
        });
    }
}
