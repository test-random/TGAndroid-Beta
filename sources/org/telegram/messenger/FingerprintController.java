package org.telegram.messenger;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.security.keystore.KeyGenParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Locale;
import javax.crypto.Cipher;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;

public class FingerprintController {
    private static final String KEY_ALIAS = "tmessages_passcode";
    private static Boolean hasChangedFingerprints;
    private static KeyPairGenerator keyPairGenerator;
    private static KeyStore keyStore;

    public static boolean checkDeviceFingerprintsChanged() {
        Boolean bool = hasChangedFingerprints;
        if (bool != null) {
            return bool.booleanValue();
        }
        try {
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").init(2, keyStore.getKey("tmessages_passcode", null));
            hasChangedFingerprints = Boolean.FALSE;
            return false;
        } catch (Exception e) {
            FileLog.e(e);
            hasChangedFingerprints = Boolean.FALSE;
            return false;
        }
    }

    public static void checkKeyReady() {
        checkKeyReady(true);
    }

    public static void checkKeyReady(final boolean z) {
        if (!isKeyReady() && AndroidUtilities.isKeyguardSecure() && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected() && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).hasEnrolledFingerprints()) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FingerprintController.generateNewKey(z);
                }
            });
        }
    }

    public static void deleteInvalidKey() {
        try {
            getKeyStore().deleteEntry("tmessages_passcode");
        } catch (KeyStoreException e) {
            FileLog.e(e);
        }
        hasChangedFingerprints = null;
        checkKeyReady(false);
    }

    public static void generateNewKey(final boolean z) {
        KeyGenParameterSpec.Builder digests;
        KeyGenParameterSpec.Builder encryptionPaddings;
        KeyGenParameterSpec.Builder userAuthenticationRequired;
        KeyGenParameterSpec build;
        KeyPairGenerator keyPairGenerator2 = getKeyPairGenerator();
        if (keyPairGenerator2 != null) {
            try {
                Locale locale = Locale.getDefault();
                setLocale(Locale.ENGLISH);
                digests = new KeyGenParameterSpec.Builder("tmessages_passcode", 3).setDigests("SHA-256", "SHA-512");
                encryptionPaddings = digests.setEncryptionPaddings("OAEPPadding");
                userAuthenticationRequired = encryptionPaddings.setUserAuthenticationRequired(true);
                build = userAuthenticationRequired.build();
                keyPairGenerator2.initialize(build);
                keyPairGenerator2.generateKeyPair();
                setLocale(locale);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        FingerprintController.lambda$generateNewKey$0(z);
                    }
                });
            } catch (InvalidAlgorithmParameterException e) {
                e = e;
                FileLog.e(e);
            } catch (Exception e2) {
                e = e2;
                if (e.getClass().getName().equals("android.security.KeyStoreException")) {
                    return;
                }
                FileLog.e(e);
            }
        }
    }

    private static KeyPairGenerator getKeyPairGenerator() {
        KeyPairGenerator keyPairGenerator2 = keyPairGenerator;
        if (keyPairGenerator2 != null) {
            return keyPairGenerator2;
        }
        try {
            KeyPairGenerator keyPairGenerator3 = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            keyPairGenerator = keyPairGenerator3;
            return keyPairGenerator3;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static KeyStore getKeyStore() {
        KeyStore keyStore2 = keyStore;
        if (keyStore2 != null) {
            return keyStore2;
        }
        try {
            KeyStore keyStore3 = KeyStore.getInstance("AndroidKeyStore");
            keyStore = keyStore3;
            keyStore3.load(null);
            return keyStore;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static boolean isKeyReady() {
        try {
            return getKeyStore().containsAlias("tmessages_passcode");
        } catch (KeyStoreException e) {
            FileLog.e(e);
            return false;
        }
    }

    public static void lambda$generateNewKey$0(boolean z) {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didGenerateFingerprintKeyPair, Boolean.valueOf(z));
    }

    private static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources resources = ApplicationLoader.applicationContext.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
