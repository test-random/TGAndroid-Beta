package org.telegram.tgnet;

import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_bots;

public final class TLRPC$TL_channelFull$$ExternalSyntheticLambda0 implements Vector.TLDeserializer {
    @Override
    public final TLObject deserialize(InputSerializedData inputSerializedData, int i, boolean z) {
        return TL_bots.BotInfo.TLdeserialize(inputSerializedData, i, z);
    }
}
