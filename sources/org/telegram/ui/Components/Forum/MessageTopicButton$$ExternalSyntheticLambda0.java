package org.telegram.ui.Components.Forum;

import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiSpan;

public final class MessageTopicButton$$ExternalSyntheticLambda0 implements AnimatedEmojiSpan.InvalidateHolder {
    public final ChatMessageCell f$0;

    public MessageTopicButton$$ExternalSyntheticLambda0(ChatMessageCell chatMessageCell) {
        this.f$0 = chatMessageCell;
    }

    @Override
    public final void invalidate() {
        this.f$0.invalidateOutbounds();
    }
}
