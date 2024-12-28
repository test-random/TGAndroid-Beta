package org.telegram.messenger.utils;

import android.util.Base64;
import androidx.core.util.Pair;
import com.android.billingclient.api.AccountIdentifiers;
import com.android.billingclient.api.Purchase;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Charsets;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public abstract class BillingUtilities {

    public static class TL_savedPurpose extends TLObject {
        public int flags;
        public long id;
        public TLRPC.InputStorePaymentPurpose purpose;

        public static TL_savedPurpose TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            TL_savedPurpose tL_savedPurpose = i != 495638674 ? null : new TL_savedPurpose();
            if (tL_savedPurpose == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_savedPurpose", Integer.valueOf(i)));
            }
            if (tL_savedPurpose != null) {
                tL_savedPurpose.readParams(inputSerializedData, z);
            }
            return tL_savedPurpose;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.id = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.purpose = TLRPC.InputStorePaymentPurpose.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(495638674);
            outputSerializedData.writeInt32(this.flags);
            outputSerializedData.writeInt64(this.id);
            if ((this.flags & 1) != 0) {
                this.purpose.serializeToStream(outputSerializedData);
            }
        }
    }

    public static void cleanupPurchase(Purchase purchase) {
        clearPurpose(purchase.getAccountIdentifiers().getObfuscatedProfileId());
    }

    public static void clearPurpose(String str) {
        try {
            FileLog.d("BillingUtilities.clearPurpose: got {" + str + "}");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(str));
            TL_savedPurpose TLdeserialize = TL_savedPurpose.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            SerializedData serializedData2 = new SerializedData(8);
            serializedData2.writeInt64(TLdeserialize.id);
            String bytesToHex = Utilities.bytesToHex(serializedData2.toByteArray());
            serializedData2.cleanup();
            FileLog.d("BillingUtilities.clearPurpose: id_hex = " + bytesToHex);
            ApplicationLoader.applicationContext.getSharedPreferences("purchases", 0).edit().remove(bytesToHex).apply();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static Pair createDeveloperPayload(TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose, AccountInstance accountInstance) {
        return Pair.create(Base64.encodeToString(String.valueOf(accountInstance.getUserConfig().getClientUserId()).getBytes(Charsets.UTF_8), 0), savePurpose(inputStorePaymentPurpose));
    }

    public static void extractCurrencyExp(Map map) {
        if (map.isEmpty()) {
            try {
                InputStream open = ApplicationLoader.applicationContext.getAssets().open("currencies.json");
                JSONObject jSONObject = new JSONObject(new String(Util.toByteArray(open), Charsets.UTF_8));
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    map.put(next, Integer.valueOf(jSONObject.optJSONObject(next).optInt("exp")));
                }
                open.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static Pair extractDeveloperPayload(Purchase purchase) {
        TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose;
        AccountIdentifiers accountIdentifiers = purchase.getAccountIdentifiers();
        if (accountIdentifiers == null) {
            FileLog.d("Billing: Extract payload. No AccountIdentifiers");
            return null;
        }
        String obfuscatedAccountId = accountIdentifiers.getObfuscatedAccountId();
        String obfuscatedProfileId = accountIdentifiers.getObfuscatedProfileId();
        if (obfuscatedAccountId != null && !obfuscatedAccountId.isEmpty() && obfuscatedProfileId != null) {
            try {
                if (!obfuscatedProfileId.isEmpty()) {
                    try {
                        inputStorePaymentPurpose = getPurpose(obfuscatedProfileId);
                    } catch (Exception e) {
                        FileLog.e("Billing: Extract payload, failed to get purpose", e);
                        inputStorePaymentPurpose = null;
                    }
                    AccountInstance findAccountById = findAccountById(Long.parseLong(new String(Base64.decode(obfuscatedAccountId, 0), Charsets.UTF_8)));
                    if (findAccountById != null) {
                        return Pair.create(findAccountById, inputStorePaymentPurpose);
                    }
                    FileLog.d("Billing: Extract payload. AccountInstance not found");
                    return null;
                }
            } catch (Exception e2) {
                FileLog.e("Billing: Extract Payload", e2);
                return null;
            }
        }
        FileLog.d("Billing: Extract payload. Empty AccountIdentifiers");
        return null;
    }

    private static AccountInstance findAccountById(long j) {
        for (int i = 0; i < 4; i++) {
            AccountInstance accountInstance = AccountInstance.getInstance(i);
            if (accountInstance.getUserConfig().getClientUserId() == j) {
                return accountInstance;
            }
        }
        return null;
    }

    public static TLRPC.InputStorePaymentPurpose getPurpose(String str) {
        FileLog.d("BillingUtilities.getPurpose " + str);
        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(str));
        TL_savedPurpose TLdeserialize = TL_savedPurpose.TLdeserialize(serializedData, serializedData.readInt32(true), true);
        serializedData.cleanup();
        if (TLdeserialize.purpose != null) {
            FileLog.d("BillingUtilities.getPurpose: got purpose from received obfuscated profile id");
            return TLdeserialize.purpose;
        }
        SerializedData serializedData2 = new SerializedData(8);
        serializedData2.writeInt64(TLdeserialize.id);
        String bytesToHex = Utilities.bytesToHex(serializedData2.toByteArray());
        serializedData2.cleanup();
        FileLog.d("BillingUtilities.getPurpose: searching purpose under " + bytesToHex);
        String string = ApplicationLoader.applicationContext.getSharedPreferences("purchases", 0).getString(bytesToHex, null);
        if (string != null) {
            FileLog.d("BillingUtilities.getPurpose: got {" + string + "} under " + bytesToHex);
            SerializedData serializedData3 = new SerializedData(Utilities.hexToBytes(string));
            TL_savedPurpose TLdeserialize2 = TL_savedPurpose.TLdeserialize(serializedData3, serializedData3.readInt32(true), true);
            serializedData3.cleanup();
            return TLdeserialize2.purpose;
        }
        FileLog.d("BillingUtilities.getPurpose: purpose under " + bytesToHex + " not found");
        throw new RuntimeException("no purpose under " + bytesToHex + " found :(");
    }

    public static String savePurpose(TLRPC.InputStorePaymentPurpose inputStorePaymentPurpose) {
        long nextLong = Utilities.random.nextLong();
        FileLog.d("BillingUtilities.savePurpose id=" + nextLong + " paymentPurpose=" + inputStorePaymentPurpose);
        SerializedData serializedData = new SerializedData(8);
        serializedData.writeInt64(nextLong);
        String bytesToHex = Utilities.bytesToHex(serializedData.toByteArray());
        serializedData.cleanup();
        FileLog.d("BillingUtilities.savePurpose id_hex=" + bytesToHex + " paymentPurpose=" + inputStorePaymentPurpose);
        TL_savedPurpose tL_savedPurpose = new TL_savedPurpose();
        tL_savedPurpose.id = nextLong;
        tL_savedPurpose.flags = 1;
        tL_savedPurpose.purpose = inputStorePaymentPurpose;
        SerializedData serializedData2 = new SerializedData(tL_savedPurpose.getObjectSize());
        tL_savedPurpose.serializeToStream(serializedData2);
        String bytesToHex2 = Utilities.bytesToHex(serializedData2.toByteArray());
        serializedData2.cleanup();
        if (tL_savedPurpose.getObjectSize() > 28) {
            FileLog.d("BillingUtilities.savePurpose: sending short version, original size is " + tL_savedPurpose.getObjectSize() + " bytes");
            tL_savedPurpose.flags = 0;
            tL_savedPurpose.purpose = null;
        }
        SerializedData serializedData3 = new SerializedData(tL_savedPurpose.getObjectSize());
        tL_savedPurpose.serializeToStream(serializedData3);
        String bytesToHex3 = Utilities.bytesToHex(serializedData3.toByteArray());
        serializedData3.cleanup();
        ApplicationLoader.applicationContext.getSharedPreferences("purchases", 0).edit().putString(bytesToHex, bytesToHex2).apply();
        FileLog.d("BillingUtilities.savePurpose: saved {" + bytesToHex2 + "} under " + bytesToHex);
        StringBuilder sb = new StringBuilder();
        sb.append("BillingUtilities.savePurpose: but sending {");
        sb.append(bytesToHex3);
        sb.append("}");
        FileLog.d(sb.toString());
        return bytesToHex3;
    }
}
