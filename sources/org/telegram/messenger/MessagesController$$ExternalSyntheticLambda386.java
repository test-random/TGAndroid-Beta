package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
public final class MessagesController$$ExternalSyntheticLambda386 implements RequestDelegate {
    public static final MessagesController$$ExternalSyntheticLambda386 INSTANCE = new MessagesController$$ExternalSyntheticLambda386();

    private MessagesController$$ExternalSyntheticLambda386() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$103(tLObject, tLRPC$TL_error);
    }
}