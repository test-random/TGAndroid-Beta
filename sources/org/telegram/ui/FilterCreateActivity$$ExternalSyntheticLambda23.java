package org.telegram.ui;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_chatlists;

public final class FilterCreateActivity$$ExternalSyntheticLambda23 implements Utilities.Callback {
    public final FilterCreateActivity f$0;

    public FilterCreateActivity$$ExternalSyntheticLambda23(FilterCreateActivity filterCreateActivity) {
        this.f$0 = filterCreateActivity;
    }

    @Override
    public final void run(Object obj) {
        this.f$0.onEdit((TL_chatlists.TL_exportedChatlistInvite) obj);
    }
}
