package org.telegram.tgnet;

import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;

public final class TLRPC$TL_inputPrivacyValueAllowUsers$$ExternalSyntheticLambda0 implements Vector.TLDeserializer {
    @Override
    public final TLObject deserialize(InputSerializedData inputSerializedData, int i, boolean z) {
        return TLRPC.InputUser.TLdeserialize(inputSerializedData, i, z);
    }
}
