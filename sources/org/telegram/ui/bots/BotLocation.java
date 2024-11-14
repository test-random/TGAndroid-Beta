package org.telegram.ui.bots;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.View;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AttachableDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.LaunchActivity;

public class BotLocation {
    private static final HashMap instances = new HashMap();
    public final long botId;
    public final Context context;
    public final int currentAccount;
    public boolean granted;
    private final HashSet listeners = new HashSet();
    public boolean requested;

    public static class BotUserLocationDrawable extends Drawable implements AttachableDrawable {
        private final Paint arrowPaint;
        private final Paint bgPaint;
        private final ImageReceiver botImageReceiver;
        private final Drawable locationDrawable;
        private final RectF rect;
        private final ImageReceiver userImageReceiver;
        private final Paint whitePaint;

        public BotUserLocationDrawable(Context context, TLRPC.User user, TLRPC.User user2) {
            Paint paint = new Paint(1);
            this.arrowPaint = paint;
            this.bgPaint = new Paint(1);
            Paint paint2 = new Paint(1);
            this.whitePaint = paint2;
            ImageReceiver imageReceiver = new ImageReceiver();
            this.userImageReceiver = imageReceiver;
            ImageReceiver imageReceiver2 = new ImageReceiver();
            this.botImageReceiver = imageReceiver2;
            this.rect = new RectF();
            paint.setColor(-1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint2.setColor(-1);
            Drawable mutate = context.getResources().getDrawable(R.drawable.filled_location).mutate();
            this.locationDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTopBackground), PorterDuff.Mode.SRC_IN));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            imageReceiver.setForUserOrChat(user, avatarDrawable);
            imageReceiver.setRoundRadius(AndroidUtilities.dp(25.0f));
            AvatarDrawable avatarDrawable2 = new AvatarDrawable();
            avatarDrawable2.setInfo(user2);
            imageReceiver2.setForUserOrChat(user2, avatarDrawable2);
            imageReceiver2.setRoundRadius(AndroidUtilities.dp(25.0f));
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            this.bgPaint.setColor(Theme.getColor(Theme.key_dialogTopBackground));
            float dp = AndroidUtilities.dp(136.0f) / 2.0f;
            this.userImageReceiver.setImageCoords(bounds.centerX() - dp, bounds.centerY() - AndroidUtilities.dp(25.0f), AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f));
            this.userImageReceiver.draw(canvas);
            float centerX = (bounds.centerX() - dp) + AndroidUtilities.dp(41.0f);
            float centerY = bounds.centerY() + AndroidUtilities.dp(16.0f);
            canvas.drawCircle(centerX, centerY, AndroidUtilities.dp(14.0f), this.bgPaint);
            canvas.drawCircle(centerX, centerY, AndroidUtilities.dp(12.0f), this.whitePaint);
            this.locationDrawable.setBounds((int) (centerX - AndroidUtilities.dp(9.0f)), (int) (centerY - AndroidUtilities.dp(9.0f)), (int) (centerX + AndroidUtilities.dp(9.0f)), (int) (centerY + AndroidUtilities.dp(9.0f)));
            this.locationDrawable.draw(canvas);
            canvas.drawLine(bounds.centerX() - AndroidUtilities.dp(3.33f), bounds.centerY() - AndroidUtilities.dp(7.0f), bounds.centerX() + AndroidUtilities.dp(3.33f), bounds.centerY(), this.arrowPaint);
            canvas.drawLine(bounds.centerX() - AndroidUtilities.dp(3.33f), bounds.centerY() + AndroidUtilities.dp(7.0f), bounds.centerX() + AndroidUtilities.dp(3.33f), bounds.centerY(), this.arrowPaint);
            this.botImageReceiver.setImageCoords((bounds.centerX() + dp) - AndroidUtilities.dp(50.0f), bounds.centerY() - AndroidUtilities.dp(25.0f), AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f));
            this.botImageReceiver.draw(canvas);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void onAttachedToWindow(ImageReceiver imageReceiver) {
            this.userImageReceiver.onAttachedToWindow();
            this.botImageReceiver.onAttachedToWindow();
        }

        @Override
        public void onDetachedFromWindow(ImageReceiver imageReceiver) {
            this.userImageReceiver.onDetachedFromWindow();
            this.botImageReceiver.onDetachedFromWindow();
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public void setParent(View view) {
            this.botImageReceiver.setParentView(view);
            this.userImageReceiver.setParentView(view);
        }
    }

    private BotLocation(Context context, int i, long j) {
        this.context = context;
        this.currentAccount = i;
        this.botId = j;
        load();
    }

    private boolean appHasPermission() {
        int checkSelfPermission;
        int checkSelfPermission2;
        Activity activity = getActivity();
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity != null) {
                checkSelfPermission = activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
                if (checkSelfPermission != 0) {
                    checkSelfPermission2 = activity.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION");
                    if (checkSelfPermission2 == 0) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static void clear() {
        Context context = ApplicationLoader.applicationContext;
        if (context == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            context.getSharedPreferences("botlocation_" + i, 0).edit().clear().apply();
        }
        instances.clear();
    }

    private boolean deviceHasLocation() {
        return getActivity() != null && getActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps");
    }

    public static BotLocation get(Context context, int i, long j) {
        Pair pair = new Pair(Integer.valueOf(i), Long.valueOf(j));
        HashMap hashMap = instances;
        BotLocation botLocation = (BotLocation) hashMap.get(pair);
        if (botLocation != null) {
            return botLocation;
        }
        BotLocation botLocation2 = new BotLocation(context, i, j);
        hashMap.put(pair, botLocation2);
        return botLocation2;
    }

    private Activity getActivity() {
        Activity activity = LaunchActivity.instance;
        if (activity == null) {
            activity = AndroidUtilities.findActivity(this.context);
        }
        return activity == null ? AndroidUtilities.findActivity(ApplicationLoader.applicationContext) : activity;
    }

    public static void lambda$request$4(Activity activity, boolean[] zArr, Utilities.Callback2 callback2, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
        zArr[0] = true;
        if (callback2 != null) {
            Boolean bool = Boolean.FALSE;
            callback2.run(bool, bool);
        }
    }

    public void lambda$request$5(Utilities.Callback2 callback2, int[] iArr) {
        boolean z = false;
        for (int i : iArr) {
            if (i == 0) {
                z = true;
            }
        }
        this.requested = true;
        this.granted = true;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        if (callback2 != null) {
            callback2.run(Boolean.TRUE, Boolean.valueOf(z));
        }
    }

    public void lambda$request$6(boolean[] zArr, final Utilities.Callback2 callback2, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        if (!appHasPermission()) {
            PermissionRequest.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    BotLocation.this.lambda$request$5(callback2, (int[]) obj);
                }
            });
            return;
        }
        this.requested = true;
        this.granted = true;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        if (callback2 != null) {
            Boolean bool = Boolean.TRUE;
            callback2.run(bool, bool);
        }
    }

    public void lambda$request$7(boolean[] zArr, Utilities.Callback2 callback2, DialogInterface dialogInterface, int i) {
        if (zArr[0]) {
            return;
        }
        zArr[0] = true;
        this.requested = true;
        this.granted = false;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        if (callback2 != null) {
            callback2.run(Boolean.TRUE, Boolean.FALSE);
        }
    }

    public void lambda$request$8(boolean[] zArr, Utilities.Callback2 callback2, DialogInterface dialogInterface) {
        if (zArr[0]) {
            return;
        }
        this.requested = true;
        this.granted = false;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        zArr[0] = true;
        if (callback2 != null) {
            callback2.run(Boolean.TRUE, Boolean.FALSE);
        }
    }

    public static void lambda$requestObject$9(Context context, DialogInterface dialogInterface, int i) {
        try {
            context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception unused) {
        }
    }

    public static void lambda$setGranted$0(Activity activity, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$setGranted$1(Runnable runnable, int[] iArr) {
        boolean z = false;
        for (int i : iArr) {
            if (i == 0) {
                z = true;
            }
        }
        this.requested = z;
        this.granted = z;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$setGranted$2(final Runnable runnable, DialogInterface dialogInterface, int i) {
        if (!appHasPermission()) {
            PermissionRequest.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    BotLocation.this.lambda$setGranted$1(runnable, (int[]) obj);
                }
            });
            return;
        }
        this.requested = true;
        this.granted = true;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    public void lambda$setGranted$3(Runnable runnable, DialogInterface dialogInterface, int i) {
        this.requested = true;
        this.granted = false;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public JSONObject locationObject(Location location) {
        float speedAccuracyMetersPerSecond;
        float bearingAccuracyDegrees;
        float verticalAccuracyMeters;
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("available", true);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (location == null) {
            return jSONObject;
        }
        jSONObject.put("latitude", location.getLatitude());
        jSONObject.put("longitude", location.getLongitude());
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            jSONObject.put("horizontal_accuracy", location.getAccuracy());
        } else {
            jSONObject.put("horizontal_accuracy", (Object) null);
        }
        jSONObject.put("altitude", location.getAltitude());
        if (i >= 26) {
            verticalAccuracyMeters = location.getVerticalAccuracyMeters();
            jSONObject.put("vertical_accuracy", verticalAccuracyMeters);
        } else {
            jSONObject.put("vertical_accuracy", (Object) null);
        }
        jSONObject.put("course", location.getBearing());
        if (i >= 26) {
            bearingAccuracyDegrees = location.getBearingAccuracyDegrees();
            jSONObject.put("course_accuracy", bearingAccuracyDegrees);
        } else {
            jSONObject.put("course_accuracy", (Object) null);
        }
        jSONObject.put("speed", location.getSpeed());
        if (i >= 26) {
            speedAccuracyMetersPerSecond = location.getSpeedAccuracyMetersPerSecond();
            jSONObject.put("speed_accuracy", speedAccuracyMetersPerSecond);
        } else {
            jSONObject.put("speed_accuracy", (Object) null);
        }
        return jSONObject;
    }

    private boolean needToOpenSettings() {
        Activity activity;
        boolean shouldShowRequestPermissionRationale;
        boolean shouldShowRequestPermissionRationale2;
        if (Build.VERSION.SDK_INT < 23 || (activity = getActivity()) == null) {
            return false;
        }
        shouldShowRequestPermissionRationale = activity.shouldShowRequestPermissionRationale("android.permission.ACCESS_COARSE_LOCATION");
        if (shouldShowRequestPermissionRationale) {
            shouldShowRequestPermissionRationale2 = activity.shouldShowRequestPermissionRationale("android.permission.ACCESS_FINE_LOCATION");
            if (shouldShowRequestPermissionRationale2) {
                return false;
            }
        }
        return true;
    }

    public boolean asked() {
        return this.requested;
    }

    public JSONObject checkObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("available", deviceHasLocation());
            if (deviceHasLocation()) {
                jSONObject.put("access_requested", this.requested);
                if (this.requested) {
                    jSONObject.put("access_granted", this.granted && appHasPermission());
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return jSONObject;
    }

    public boolean granted() {
        return appHasPermission() && this.granted;
    }

    public void listen(Runnable runnable) {
        this.listeners.add(runnable);
    }

    public void load() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("botlocation_" + this.currentAccount, 0);
        this.requested = sharedPreferences.getBoolean(this.botId + "_requested", false);
        boolean z = sharedPreferences.getBoolean(this.botId + "_granted", false);
        this.granted = z;
        if (!z || appHasPermission()) {
            return;
        }
        this.granted = false;
        this.requested = false;
        save();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    public void request(final Utilities.Callback2 callback2) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (!deviceHasLocation()) {
            if (callback2 != null) {
                Boolean bool = Boolean.FALSE;
                callback2.run(bool, bool);
                return;
            }
            return;
        }
        if (appHasPermission() && (this.requested || this.granted)) {
            if (callback2 != null) {
                callback2.run(Boolean.FALSE, Boolean.TRUE);
                return;
            }
            return;
        }
        final boolean[] zArr = new boolean[1];
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, null);
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotLocationPermissionRequest, UserObject.getUserName(user), UserObject.getUserName(user))));
        builder.setTopImage(new BotUserLocationDrawable(this.context, UserConfig.getInstance(this.currentAccount).getCurrentUser(), user), Theme.getColor(Theme.key_dialogTopBackground));
        if (appHasPermission() || !needToOpenSettings()) {
            builder.setPositiveButton(LocaleController.getString(R.string.BotLocationPermissionAllow), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    BotLocation.this.lambda$request$6(zArr, callback2, dialogInterface, i);
                }
            });
        } else {
            builder.setPositiveButton(LocaleController.getString(R.string.BotLocationPermissionSettings), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    BotLocation.lambda$request$4(activity, zArr, callback2, dialogInterface, i);
                }
            });
        }
        builder.setNegativeButton(LocaleController.getString(R.string.BotLocationPermissionDecline), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                BotLocation.this.lambda$request$7(zArr, callback2, dialogInterface, i);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                BotLocation.this.lambda$request$8(zArr, callback2, dialogInterface);
            }
        });
        builder.show();
    }

    public void requestObject(final Utilities.Callback callback) {
        JSONObject locationObject;
        if (callback == null) {
            return;
        }
        JSONObject jSONObject = new JSONObject();
        if (!this.granted || !appHasPermission() || !deviceHasLocation()) {
            try {
                jSONObject.put("available", false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            callback.run(jSONObject);
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
        if (location == null && !locationManager.isProviderEnabled("gps")) {
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
                            BotLocation.lambda$requestObject$9(context, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                    builder.show();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            locationObject = locationObject(null);
        } else {
            if (location == null) {
                try {
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location2) {
                            locationManager.removeUpdates(r3[0]);
                            callback.run(BotLocation.this.locationObject(location2));
                        }
                    };
                    final LocationListener[] locationListenerArr = {locationListener};
                    locationManager.requestLocationUpdates("gps", 1L, 0.0f, locationListener);
                    return;
                } catch (Exception e3) {
                    FileLog.e(e3);
                    callback.run(locationObject(null));
                    return;
                }
            }
            locationObject = locationObject(location);
        }
        callback.run(locationObject);
    }

    public void save() {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("botlocation_" + this.currentAccount, 0).edit();
        edit.putBoolean(this.botId + "_granted", this.granted);
        edit.putBoolean(this.botId + "_requested", this.requested);
        edit.apply();
    }

    public void setGranted(boolean z, final Runnable runnable) {
        this.requested = true;
        if (!z || appHasPermission()) {
            this.granted = z;
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
            if (runnable != null) {
                runnable.run();
            }
        } else {
            final Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), null);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotLocationPermissionRequest, UserObject.getUserName(user), UserObject.getUserName(user))));
            builder.setTopImage(new BotUserLocationDrawable(this.context, UserConfig.getInstance(this.currentAccount).getCurrentUser(), user), Theme.getColor(Theme.key_dialogTopBackground));
            if (needToOpenSettings()) {
                builder.setPositiveButton(LocaleController.getString(R.string.BotLocationPermissionSettings), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        BotLocation.lambda$setGranted$0(activity, dialogInterface, i);
                    }
                });
            } else {
                builder.setPositiveButton(LocaleController.getString(R.string.BotLocationPermissionAllow), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        BotLocation.this.lambda$setGranted$2(runnable, dialogInterface, i);
                    }
                });
            }
            builder.setNegativeButton(LocaleController.getString(R.string.BotLocationPermissionDecline), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    BotLocation.this.lambda$setGranted$3(runnable, dialogInterface, i);
                }
            });
            builder.show();
        }
        save();
    }

    public void unlisten(Runnable runnable) {
        this.listeners.remove(runnable);
    }
}
