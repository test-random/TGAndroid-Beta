package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final class MessagesController$$ExternalSyntheticLambda363 implements RequestDelegate {
    public static final MessagesController$$ExternalSyntheticLambda363 INSTANCE = new MessagesController$$ExternalSyntheticLambda363();

    private MessagesController$$ExternalSyntheticLambda363() {
    }

    @Override
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePeerSettingsBar$59(tLObject, tLRPC$TL_error);
    }
}