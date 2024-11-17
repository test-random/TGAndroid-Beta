package org.telegram.ui.bots;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.text.TextUtils;
import android.util.Pair;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

public class BotBiometry {
    private static final WeakHashMap instances = new WeakHashMap();
    private static KeyStore keyStore;
    public boolean access_granted;
    public boolean access_requested;
    public final long botId;
    private Utilities.Callback2 callback;
    public final Context context;
    public final int currentAccount;
    public boolean disabled;
    private String encrypted_token;
    private String iv;
    private BiometricPrompt prompt;

    public static class Bot {
        public boolean disabled;
        public TLRPC.User user;

        private Bot(TLRPC.User user, boolean z) {
            this.user = user;
            this.disabled = z;
        }
    }

    private BotBiometry(Context context, int i, long j) {
        this.context = context;
        this.currentAccount = i;
        this.botId = j;
        load();
    }

    public static void clear() {
        Context context = ApplicationLoader.applicationContext;
        if (context == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            context.getSharedPreferences("2botbiometry_" + i, 0).edit().clear().apply();
        }
        instances.clear();
    }

    public static BotBiometry get(Context context, int i, long j) {
        Pair pair = new Pair(Integer.valueOf(i), Long.valueOf(j));
        WeakHashMap weakHashMap = instances;
        BotBiometry botBiometry = (BotBiometry) weakHashMap.get(pair);
        if (botBiometry != null) {
            return botBiometry;
        }
        BotBiometry botBiometry2 = new BotBiometry(context, i, j);
        weakHashMap.put(pair, botBiometry2);
        return botBiometry2;
    }

    public static String getAvailableType(Context context) {
        try {
            BiometricManager from = BiometricManager.from(context);
            if (from == null) {
                return null;
            }
            if (from.canAuthenticate(15) != 0) {
                return null;
            }
            return "unknown";
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static void getBots(Context context, final int i, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("2botbiometry_" + i, 0);
        final ArrayList arrayList = new ArrayList();
        Iterator<Map.Entry<String, ?>> it = sharedPreferences.getAll().entrySet().iterator();
        while (it.hasNext()) {
            String key = it.next().getKey();
            if (key.endsWith("_requested")) {
                try {
                    arrayList.add(Long.valueOf(Long.parseLong(key.substring(0, key.length() - 10))));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
        final HashMap hashMap = new HashMap();
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            Long l = (Long) it2.next();
            BotBiometry botBiometry = get(context, i, l.longValue());
            if (botBiometry.access_granted && botBiometry.access_requested) {
                hashMap.put(l, Boolean.valueOf(!botBiometry.disabled));
            }
        }
        if (arrayList.isEmpty()) {
            callback.run(new ArrayList());
        } else {
            MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    BotBiometry.lambda$getBots$3(i, arrayList, hashMap, callback);
                }
            });
        }
    }

    private Cipher getCipher() {
        return Cipher.getInstance("AES/CBC/PKCS7Padding");
    }

    public static String getDeviceId(Context context, int i, long j) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("2botbiometry_" + i, 0);
        String string = sharedPreferences.getString("device_id" + j, null);
        if (string != null) {
            return string;
        }
        byte[] bArr = new byte[32];
        new SecureRandom().nextBytes(bArr);
        String bytesToHex = Utilities.bytesToHex(bArr);
        sharedPreferences.edit().putString("device_id" + j, bytesToHex).apply();
        return bytesToHex;
    }

    private SecretKey getSecretKey() {
        KeyGenParameterSpec build;
        if (keyStore == null) {
            KeyStore keyStore2 = KeyStore.getInstance("AndroidKeyStore");
            keyStore = keyStore2;
            keyStore2.load(null);
        }
        if (keyStore.containsAlias("9bot_" + this.botId)) {
            return (SecretKey) keyStore.getKey("9bot_" + this.botId, null);
        }
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder("9bot_" + this.botId, 3);
        builder.setBlockModes("CBC");
        builder.setEncryptionPaddings("PKCS7Padding");
        builder.setUserAuthenticationRequired(true);
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            builder.setUserAuthenticationParameters(60, 2);
        }
        if (i >= 24) {
            builder.setInvalidatedByBiometricEnrollment(true);
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
        build = builder.build();
        keyGenerator.init(build);
        return keyGenerator.generateKey();
    }

    private void initPrompt() {
        if (this.prompt != null) {
            return;
        }
        this.prompt = new BiometricPrompt(LaunchActivity.instance, ContextCompat.getMainExecutor(this.context), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int i, CharSequence charSequence) {
                FileLog.d("BotBiometry onAuthenticationError " + i + " \"" + ((Object) charSequence) + "\"");
                if (BotBiometry.this.callback != null) {
                    Utilities.Callback2 callback2 = BotBiometry.this.callback;
                    BotBiometry.this.callback = null;
                    callback2.run(Boolean.FALSE, null);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                FileLog.d("BotBiometry onAuthenticationFailed");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
                FileLog.d("BotBiometry onAuthenticationSucceeded");
                if (BotBiometry.this.callback != null) {
                    Utilities.Callback2 callback2 = BotBiometry.this.callback;
                    BotBiometry.this.callback = null;
                    callback2.run(Boolean.TRUE, authenticationResult);
                }
            }
        });
    }

    public static void lambda$getBots$2(ArrayList arrayList, HashMap hashMap, Utilities.Callback callback) {
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.User user = (TLRPC.User) arrayList.get(i);
            Boolean bool = (Boolean) hashMap.get(Long.valueOf(user.id));
            arrayList2.add(new Bot(user, bool == null || !bool.booleanValue()));
        }
        callback.run(arrayList2);
    }

    public static void lambda$getBots$3(int i, ArrayList arrayList, final HashMap hashMap, final Utilities.Callback callback) {
        final ArrayList<TLRPC.User> users = MessagesStorage.getInstance(i).getUsers(arrayList);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotBiometry.lambda$getBots$2(users, hashMap, callback);
            }
        });
    }

    public void lambda$requestToken$0(Utilities.Callback2 callback2, Boolean bool, BiometricPrompt.AuthenticationResult authenticationResult) {
        String str = null;
        if (authenticationResult != null) {
            try {
                int i = Build.VERSION.SDK_INT;
                if (i >= 23) {
                    BiometricPrompt.CryptoObject makeCryptoObject = i >= 30 ? makeCryptoObject(true) : authenticationResult.getCryptoObject();
                    if (makeCryptoObject != null) {
                        if (!TextUtils.isEmpty(this.encrypted_token)) {
                            str = new String(makeCryptoObject.getCipher().doFinal(Utilities.hexToBytes(this.encrypted_token)), StandardCharsets.UTF_8);
                        }
                    } else if (!TextUtils.isEmpty(this.encrypted_token)) {
                        throw new RuntimeException("No cryptoObject found");
                    }
                }
                str = this.encrypted_token;
            } catch (Exception e) {
                FileLog.e(e);
                bool = Boolean.FALSE;
            }
        }
        callback2.run(bool, str);
    }

    public void lambda$updateToken$1(String str, Utilities.Callback callback, Boolean bool, BiometricPrompt.AuthenticationResult authenticationResult) {
        if (authenticationResult != null) {
            try {
                authenticationResult.getCryptoObject();
                if (TextUtils.isEmpty(str)) {
                    this.encrypted_token = null;
                } else {
                    int i = Build.VERSION.SDK_INT;
                    if (i < 23) {
                        this.encrypted_token = str;
                    } else {
                        BiometricPrompt.CryptoObject makeCryptoObject = i >= 30 ? makeCryptoObject(false) : authenticationResult.getCryptoObject();
                        if (makeCryptoObject == null) {
                            throw new RuntimeException("No cryptoObject found");
                        }
                        this.encrypted_token = Utilities.bytesToHex(makeCryptoObject.getCipher().doFinal(str.getBytes(StandardCharsets.UTF_8)));
                        this.iv = Utilities.bytesToHex(makeCryptoObject.getCipher().getIV());
                        save();
                    }
                }
                this.iv = null;
                save();
            } catch (Exception e) {
                FileLog.e(e);
                bool = Boolean.FALSE;
            }
        }
        callback.run(bool);
    }

    private BiometricPrompt.CryptoObject makeCryptoObject(boolean z) {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                return null;
            }
            Cipher cipher = getCipher();
            SecretKey secretKey = getSecretKey();
            if (z) {
                cipher.init(2, secretKey, new IvParameterSpec(Utilities.hexToBytes(this.iv)));
            } else {
                cipher.init(1, secretKey);
            }
            return new BiometricPrompt.CryptoObject(cipher);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private void prompt(String str, boolean z, String str2, Utilities.Callback2 callback2) {
        int i;
        this.callback = callback2;
        try {
            initPrompt();
            BiometricPrompt.CryptoObject makeCryptoObject = makeCryptoObject(z);
            BiometricPrompt.PromptInfo.Builder allowedAuthenticators = new BiometricPrompt.PromptInfo.Builder().setTitle(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)))).setNegativeButtonText(LocaleController.getString(R.string.Back)).setAllowedAuthenticators(15);
            if (!TextUtils.isEmpty(str)) {
                allowedAuthenticators.setDescription(str);
            }
            BiometricPrompt.PromptInfo build = allowedAuthenticators.build();
            if (makeCryptoObject != null && !z && (i = Build.VERSION.SDK_INT) >= 30) {
                try {
                    if (TextUtils.isEmpty(str2)) {
                        this.encrypted_token = null;
                    } else if (i < 23) {
                        this.encrypted_token = str2;
                    } else {
                        this.encrypted_token = Utilities.bytesToHex(makeCryptoObject.getCipher().doFinal(str2.getBytes(StandardCharsets.UTF_8)));
                        this.iv = Utilities.bytesToHex(makeCryptoObject.getCipher().getIV());
                    }
                    save();
                    this.callback = null;
                    callback2.run(Boolean.TRUE, null);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    makeCryptoObject = makeCryptoObject(z);
                }
            }
            if (makeCryptoObject == null || Build.VERSION.SDK_INT >= 30) {
                this.prompt.authenticate(build);
            } else {
                this.prompt.authenticate(build, makeCryptoObject);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
            callback2.run(Boolean.FALSE, null);
        }
    }

    public static void toggleBotDisabled(Context context, int i, long j, boolean z) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("2botbiometry_" + i, 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(j + "_disabled", z);
        if (!z && sharedPreferences.getString(String.valueOf(j), null) == null) {
            edit.putString(String.valueOf(j), "");
        }
        edit.apply();
    }

    public boolean asked() {
        return this.access_requested;
    }

    public JSONObject getStatus() {
        JSONObject jSONObject = new JSONObject();
        String availableType = getAvailableType(this.context);
        boolean z = false;
        if (availableType != null) {
            jSONObject.put("available", true);
            jSONObject.put("type", availableType);
        } else {
            jSONObject.put("available", false);
        }
        jSONObject.put("access_requested", this.access_requested);
        if (this.access_granted && !this.disabled) {
            z = true;
        }
        jSONObject.put("access_granted", z);
        jSONObject.put("token_saved", !TextUtils.isEmpty(this.encrypted_token));
        jSONObject.put("device_id", getDeviceId(this.context, this.currentAccount, this.botId));
        return jSONObject;
    }

    public boolean granted() {
        return this.access_granted;
    }

    public void load() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("2botbiometry_" + this.currentAccount, 0);
        this.encrypted_token = sharedPreferences.getString(String.valueOf(this.botId), null);
        this.iv = sharedPreferences.getString(String.valueOf(this.botId) + "_iv", null);
        boolean z = true;
        boolean z2 = this.encrypted_token != null;
        this.access_granted = z2;
        if (!z2) {
            if (!sharedPreferences.getBoolean(this.botId + "_requested", false)) {
                z = false;
            }
        }
        this.access_requested = z;
        this.disabled = sharedPreferences.getBoolean(this.botId + "_disabled", false);
    }

    public void requestToken(String str, final Utilities.Callback2 callback2) {
        prompt(str, true, null, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                BotBiometry.this.lambda$requestToken$0(callback2, (Boolean) obj, (BiometricPrompt.AuthenticationResult) obj2);
            }
        });
    }

    public void save() {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("2botbiometry_" + this.currentAccount, 0).edit();
        if (this.access_requested) {
            edit.putBoolean(this.botId + "_requested", true);
        } else {
            edit.remove(this.botId + "_requested");
        }
        boolean z = this.access_granted;
        long j = this.botId;
        if (z) {
            String valueOf = String.valueOf(j);
            String str = this.encrypted_token;
            if (str == null) {
                str = "";
            }
            edit.putString(valueOf, str);
            String str2 = String.valueOf(this.botId) + "_iv";
            String str3 = this.iv;
            edit.putString(str2, str3 != null ? str3 : "");
        } else {
            edit.remove(String.valueOf(j));
            edit.remove(String.valueOf(this.botId) + "_iv");
        }
        if (this.disabled) {
            edit.putBoolean(this.botId + "_disabled", true);
        } else {
            edit.remove(this.botId + "_disabled");
        }
        edit.apply();
    }

    public void setGranted(boolean z) {
        this.access_requested = true;
        this.access_granted = z;
        save();
    }

    public void updateToken(String str, final String str2, final Utilities.Callback callback) {
        prompt(str, false, str2, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                BotBiometry.this.lambda$updateToken$1(str2, callback, (Boolean) obj, (BiometricPrompt.AuthenticationResult) obj2);
            }
        });
    }
}
