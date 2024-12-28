package org.telegram.ui.Stories.recorder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.TextUtils;
import j$.util.DesugarTimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.recorder.Weather;

public abstract class Weather {
    private static String cacheKey;
    private static State cacheValue;

    public static class State extends TLObject {
        public String emoji;
        public double lat;
        public double lng;
        public float temperature;

        public static State TLdeserialize(AbstractSerializedData abstractSerializedData) {
            State state = new State();
            state.lat = abstractSerializedData.readDouble(false);
            state.lng = abstractSerializedData.readDouble(false);
            state.emoji = abstractSerializedData.readString(false);
            state.temperature = abstractSerializedData.readFloat(false);
            return state;
        }

        public String getEmoji() {
            return this.emoji;
        }

        public String getTemperature() {
            return getTemperature(Weather.isDefaultCelsius());
        }

        public String getTemperature(boolean z) {
            StringBuilder sb;
            String str;
            if (z) {
                sb = new StringBuilder();
                sb.append(Math.round(this.temperature));
                str = "°C";
            } else {
                sb = new StringBuilder();
                double d = this.temperature;
                Double.isNaN(d);
                sb.append((int) Math.round(((d * 9.0d) / 5.0d) + 32.0d));
                str = "°F";
            }
            sb.append(str);
            return sb.toString();
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeDouble(this.lat);
            outputSerializedData.writeDouble(this.lng);
            outputSerializedData.writeString(this.emoji);
            outputSerializedData.writeFloat(this.temperature);
        }
    }

    public static Runnable fetch(final double d, final double d2, final Utilities.Callback callback) {
        if (callback == null) {
            return null;
        }
        Date date = new Date();
        Calendar calendar = Calendar.getInstance(DesugarTimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        final String str = Math.round(d * 1000.0d) + ":" + Math.round(1000.0d * d2) + "at" + (((calendar.getTimeInMillis() / 1000) / 60) / 60);
        if (cacheValue != null && TextUtils.equals(cacheKey, str)) {
            callback.run(cacheValue);
            return null;
        }
        final int[] iArr = new int[1];
        final MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        final ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        String str2 = messagesController.weatherSearchUsername;
        final TLRPC.User[] userArr = {messagesController.getUser(str2)};
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$5(MessagesController.this, userArr, d, d2, iArr, connectionsManager, callback, str);
            }
        };
        if (userArr[0] == null) {
            TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = str2;
            iArr[0] = connectionsManager.sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    Weather.lambda$fetch$7(iArr, messagesController, userArr, runnable, callback, tLObject, tL_error);
                }
            });
        } else {
            runnable.run();
        }
        return new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$8(iArr, connectionsManager);
            }
        };
    }

    public static void fetch(final boolean z, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        getUserLocation(z, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Weather.lambda$fetch$2(Utilities.Callback.this, z, (Location) obj);
            }
        });
    }

    public static State getCached() {
        return cacheValue;
    }

    public static void getUserLocation(final boolean z, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        PermissionRequest.ensureEitherPermission(R.raw.permission_request_location, R.string.PermissionNoLocationStory, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Weather.lambda$getUserLocation$11(Utilities.Callback.this, z, (Boolean) obj);
            }
        });
    }

    public static boolean isDefaultCelsius() {
        String id = TimeZone.getDefault().getID();
        return (id.startsWith("US/") || "America/Nassau".equals(id) || "America/Belize".equals(id) || "America/Cayman".equals(id) || "Pacific/Palau".equals(id)) ? false : true;
    }

    public static void lambda$fetch$0(boolean z, AlertDialog alertDialog, Utilities.Callback callback, State state) {
        if (z) {
            alertDialog.dismissUnless(350L);
        }
        callback.run(state);
    }

    public static void lambda$fetch$2(final Utilities.Callback callback, final boolean z, Location location) {
        if (location == null) {
            callback.run(null);
            return;
        }
        Activity activity = LaunchActivity.instance;
        if (activity == null) {
            activity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
        }
        if (activity == null || activity.isFinishing()) {
            callback.run(null);
            return;
        }
        final AlertDialog alertDialog = z ? new AlertDialog(activity, 3, new DarkThemeResourceProvider()) : null;
        if (z) {
            alertDialog.showDelayed(200L);
        }
        final Runnable fetch = fetch(location.getLatitude(), location.getLongitude(), new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Weather.lambda$fetch$0(z, alertDialog, callback, (Weather.State) obj);
            }
        });
        if (!z || fetch == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                fetch.run();
            }
        });
    }

    public static void lambda$fetch$3(int[] iArr, TLObject tLObject, Utilities.Callback callback, double d, double d2, String str) {
        iArr[0] = 0;
        if (tLObject instanceof TLRPC.messages_BotResults) {
            TLRPC.messages_BotResults messages_botresults = (TLRPC.messages_BotResults) tLObject;
            if (!messages_botresults.results.isEmpty()) {
                TLRPC.BotInlineResult botInlineResult = messages_botresults.results.get(0);
                String str2 = botInlineResult.title;
                try {
                    float parseFloat = Float.parseFloat(botInlineResult.description);
                    State state = new State();
                    state.lat = d;
                    state.lng = d2;
                    state.emoji = str2;
                    state.temperature = parseFloat;
                    cacheKey = str;
                    cacheValue = state;
                    callback.run(state);
                    return;
                } catch (Exception unused) {
                    callback.run(null);
                    return;
                }
            }
        }
        callback.run(null);
    }

    public static void lambda$fetch$4(final int[] iArr, final Utilities.Callback callback, final double d, final double d2, final String str, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$3(iArr, tLObject, callback, d, d2, str);
            }
        });
    }

    public static void lambda$fetch$5(MessagesController messagesController, TLRPC.User[] userArr, final double d, final double d2, final int[] iArr, ConnectionsManager connectionsManager, final Utilities.Callback callback, final String str) {
        TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
        tL_messages_getInlineBotResults.bot = messagesController.getInputUser(userArr[0]);
        tL_messages_getInlineBotResults.query = "";
        tL_messages_getInlineBotResults.offset = "";
        tL_messages_getInlineBotResults.flags |= 1;
        TLRPC.TL_inputGeoPoint tL_inputGeoPoint = new TLRPC.TL_inputGeoPoint();
        tL_messages_getInlineBotResults.geo_point = tL_inputGeoPoint;
        tL_inputGeoPoint.lat = d;
        tL_inputGeoPoint._long = d2;
        tL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty();
        iArr[0] = connectionsManager.sendRequest(tL_messages_getInlineBotResults, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                Weather.lambda$fetch$4(iArr, callback, d, d2, str, tLObject, tL_error);
            }
        });
    }

    public static void lambda$fetch$6(int[] iArr, TLObject tLObject, MessagesController messagesController, TLRPC.User[] userArr, Runnable runnable, Utilities.Callback callback) {
        iArr[0] = 0;
        if (tLObject instanceof TLRPC.TL_contacts_resolvedPeer) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            messagesController.putUsers(tL_contacts_resolvedPeer.users, false);
            messagesController.putChats(tL_contacts_resolvedPeer.chats, false);
            TLRPC.User user = messagesController.getUser(Long.valueOf(DialogObject.getPeerDialogId(tL_contacts_resolvedPeer.peer)));
            userArr[0] = user;
            if (user != null) {
                runnable.run();
                return;
            }
        }
        callback.run(null);
    }

    public static void lambda$fetch$7(final int[] iArr, final MessagesController messagesController, final TLRPC.User[] userArr, final Runnable runnable, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Weather.lambda$fetch$6(iArr, tLObject, messagesController, userArr, runnable, callback);
            }
        });
    }

    public static void lambda$fetch$8(int[] iArr, ConnectionsManager connectionsManager) {
        int i = iArr[0];
        if (i != 0) {
            connectionsManager.cancelRequest(i, true);
            iArr[0] = 0;
        }
    }

    public static void lambda$getUserLocation$10(LocationListener[] locationListenerArr, LocationManager locationManager, Utilities.Callback[] callbackArr, Location location) {
        LocationListener locationListener = locationListenerArr[0];
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListenerArr[0] = null;
        }
        Utilities.Callback callback = callbackArr[0];
        if (callback != null) {
            callback.run(location);
            callbackArr[0] = null;
        }
    }

    public static void lambda$getUserLocation$11(Utilities.Callback callback, boolean z, Boolean bool) {
        if (!bool.booleanValue()) {
            callback.run(null);
            return;
        }
        final LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (int size = providers.size() - 1; size >= 0; size--) {
            location = locationManager.getLastKnownLocation(providers.get(size));
            if (location != null) {
                break;
            }
        }
        if (location == null && z) {
            if (locationManager.isProviderEnabled("gps")) {
                try {
                    final Utilities.Callback[] callbackArr = {callback};
                    final LocationListener[] locationListenerArr = {null};
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public final void onLocationChanged(Location location2) {
                            Weather.lambda$getUserLocation$10(locationListenerArr, locationManager, callbackArr, location2);
                        }
                    };
                    locationListenerArr[0] = locationListener;
                    locationManager.requestLocationUpdates("gps", 1L, 0.0f, locationListener);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    callback.run(null);
                    return;
                }
            }
            final Context context = LaunchActivity.instance;
            if (context == null) {
                context = ApplicationLoader.applicationContext;
            }
            if (context != null) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
                    builder.setMessage(LocaleController.getString(R.string.GpsDisabledAlertText));
                    builder.setPositiveButton(LocaleController.getString(R.string.Enable), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            Weather.lambda$getUserLocation$9(context, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                    builder.show();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        }
        callback.run(location);
    }

    public static void lambda$getUserLocation$9(Context context, DialogInterface dialogInterface, int i) {
        try {
            context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception unused) {
        }
    }
}
