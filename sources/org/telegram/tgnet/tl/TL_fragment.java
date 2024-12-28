package org.telegram.tgnet.tl;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;

public class TL_fragment {

    public static class InputCollectible extends TLObject {
        public static InputCollectible TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            InputCollectible tL_inputCollectibleUsername = i != -1562241884 ? i != -476815191 ? null : new TL_inputCollectibleUsername() : new TL_inputCollectiblePhone();
            if (tL_inputCollectibleUsername == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in InputCollectible", Integer.valueOf(i)));
            }
            if (tL_inputCollectibleUsername != null) {
                tL_inputCollectibleUsername.readParams(inputSerializedData, z);
            }
            return tL_inputCollectibleUsername;
        }
    }

    public static class TL_collectibleInfo extends TLObject {
        public static final int constructor = 1857945489;
        public long amount;
        public long crypto_amount;
        public String crypto_currency;
        public String currency;
        public int purchase_date;
        public String url;

        public static TL_collectibleInfo TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (1857945489 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_collectibleInfo", Integer.valueOf(i)));
                }
                return null;
            }
            TL_collectibleInfo tL_collectibleInfo = new TL_collectibleInfo();
            tL_collectibleInfo.readParams(inputSerializedData, z);
            return tL_collectibleInfo;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.purchase_date = inputSerializedData.readInt32(z);
            this.currency = inputSerializedData.readString(z);
            this.amount = inputSerializedData.readInt64(z);
            this.crypto_currency = inputSerializedData.readString(z);
            this.crypto_amount = inputSerializedData.readInt64(z);
            this.url = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1857945489);
            outputSerializedData.writeInt32(this.purchase_date);
            outputSerializedData.writeString(this.currency);
            outputSerializedData.writeInt64(this.amount);
            outputSerializedData.writeString(this.crypto_currency);
            outputSerializedData.writeInt64(this.crypto_amount);
            outputSerializedData.writeString(this.url);
        }
    }

    public static class TL_getCollectibleInfo extends TLObject {
        public static final int constructor = -1105295942;
        public InputCollectible collectible;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_collectibleInfo.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1105295942);
            this.collectible.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_inputCollectiblePhone extends InputCollectible {
        public static final int constructor = -1562241884;
        public String phone;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.phone = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1562241884);
            outputSerializedData.writeString(this.phone);
        }
    }

    public static class TL_inputCollectibleUsername extends InputCollectible {
        public static final int constructor = -476815191;
        public String username;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.username = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-476815191);
            outputSerializedData.writeString(this.username);
        }
    }
}
