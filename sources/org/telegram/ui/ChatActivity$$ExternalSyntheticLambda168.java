package org.telegram.ui;

import androidx.core.util.Consumer;
import java.util.List;
import org.telegram.ui.Components.ReactedUsersListView;

public final class ChatActivity$$ExternalSyntheticLambda168 implements Consumer {
    public final ReactedUsersListView f$0;

    public ChatActivity$$ExternalSyntheticLambda168(ReactedUsersListView reactedUsersListView) {
        this.f$0 = reactedUsersListView;
    }

    @Override
    public final void accept(Object obj) {
        this.f$0.setSeenUsers((List) obj);
    }
}