package org.telegram.tgnet;

public class TLRPC$TL_help_getPremiumPromo extends TLObject {
    public static int constructor = -1206152236;

    @Override
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_help_premiumPromo.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}