package org.telegram.ui.Stories;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;

public class MessageMediaStoryFull extends TLRPC.TL_messageMediaStory {
    public static int constructor = -946147811;

    @Override
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.id = abstractSerializedData.readInt32(z);
        this.storyItem = TL_stories.StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.via_mention = abstractSerializedData.readBool(z);
        this.peer = MessagesController.getInstance(UserConfig.selectedAccount).getPeer(this.user_id);
    }

    @Override
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeInt32(this.id);
        this.storyItem.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeBool(this.via_mention);
    }
}
