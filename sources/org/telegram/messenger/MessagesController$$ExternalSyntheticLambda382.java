package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
public final class MessagesController$$ExternalSyntheticLambda382 implements RequestDelegate {
    public static final MessagesController$$ExternalSyntheticLambda382 INSTANCE = new MessagesController$$ExternalSyntheticLambda382();

    private MessagesController$$ExternalSyntheticLambda382() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteParticipantFromChat$263(tLObject, tLRPC$TL_error);
    }
}