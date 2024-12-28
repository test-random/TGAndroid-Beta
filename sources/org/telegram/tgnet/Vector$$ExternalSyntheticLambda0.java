package org.telegram.tgnet;

import org.telegram.tgnet.Vector;

public final class Vector$$ExternalSyntheticLambda0 implements Vector.TLDeserializer {
    @Override
    public final TLObject deserialize(InputSerializedData inputSerializedData, int i, boolean z) {
        return Vector.Int.TLDeserialize(inputSerializedData, i, z);
    }
}
