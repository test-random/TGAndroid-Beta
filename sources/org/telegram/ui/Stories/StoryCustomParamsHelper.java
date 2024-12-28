package org.telegram.ui.Stories;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;

public abstract class StoryCustomParamsHelper {

    private static class Params_v1 extends TLObject {
        int flags;
        final TL_stories.StoryItem storyItem;

        private Params_v1(TL_stories.StoryItem storyItem) {
            this.flags = 0;
            this.storyItem = storyItem;
            boolean z = storyItem.translated;
            this.flags = z ? 1 : 0;
            int i = (z ? 1 : 0) + (storyItem.detectedLng != null ? 2 : 0);
            this.flags = i;
            int i2 = i + (storyItem.translatedText != null ? 4 : 0);
            this.flags = i2;
            this.flags = i2 + (storyItem.translatedLng != null ? 8 : 0);
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(true);
            this.flags = readInt32;
            TL_stories.StoryItem storyItem = this.storyItem;
            storyItem.translated = (readInt32 & 1) != 0;
            if ((readInt32 & 2) != 0) {
                storyItem.detectedLng = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.storyItem.translatedText = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 8) != 0) {
                this.storyItem.translatedLng = inputSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.storyItem.detectedLng);
            }
            if ((this.flags & 4) != 0) {
                this.storyItem.translatedText.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeString(this.storyItem.translatedLng);
            }
        }
    }

    public static boolean isEmpty(TL_stories.StoryItem storyItem) {
        return storyItem.detectedLng == null && storyItem.translatedLng == null && !storyItem.translated && storyItem.translatedText == null;
    }

    public static void readLocalParams(TL_stories.StoryItem storyItem, NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return;
        }
        int readInt32 = nativeByteBuffer.readInt32(true);
        if (readInt32 == 1) {
            new Params_v1(storyItem).readParams(nativeByteBuffer, true);
            return;
        }
        throw new RuntimeException("(story) can't read params version = " + readInt32);
    }

    public static NativeByteBuffer writeLocalParams(TL_stories.StoryItem storyItem) {
        if (isEmpty(storyItem)) {
            return null;
        }
        Params_v1 params_v1 = new Params_v1(storyItem);
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(params_v1.getObjectSize());
            params_v1.serializeToStream(nativeByteBuffer);
            return nativeByteBuffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
