package org.telegram.messenger;

import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class MessageCustomParamsHelper {

    private static class Params_v1 extends TLObject {
        private static final int VERSION = 1;
        int flags;
        final TLRPC.Message message;

        private Params_v1(TLRPC.Message message) {
            this.flags = 0;
            this.message = message;
            int i = message.voiceTranscription != null ? 1 : 0;
            this.flags = i;
            int i2 = i + (message.voiceTranscriptionForce ? 2 : 0);
            this.flags = i2;
            int i3 = i2 + (message.originalLanguage != null ? 4 : 0);
            this.flags = i3;
            int i4 = i3 + (message.translatedToLanguage != null ? 8 : 0);
            this.flags = i4;
            this.flags = i4 + (message.translatedText != null ? 16 : 0);
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(true);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.message.voiceTranscription = abstractSerializedData.readString(z);
            }
            TLRPC.Message message = this.message;
            message.voiceTranscriptionForce = (this.flags & 2) != 0;
            message.voiceTranscriptionOpen = abstractSerializedData.readBool(z);
            this.message.voiceTranscriptionFinal = abstractSerializedData.readBool(z);
            this.message.voiceTranscriptionRated = abstractSerializedData.readBool(z);
            this.message.voiceTranscriptionId = abstractSerializedData.readInt64(z);
            this.message.premiumEffectWasPlayed = abstractSerializedData.readBool(z);
            if ((this.flags & 4) != 0) {
                this.message.originalLanguage = abstractSerializedData.readString(z);
            }
            if ((this.flags & 8) != 0) {
                this.message.translatedToLanguage = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.message.translatedText = TLRPC.TL_textWithEntities.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1);
            int i = this.message.voiceTranscriptionForce ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            if ((1 & this.flags) != 0) {
                abstractSerializedData.writeString(this.message.voiceTranscription);
            }
            abstractSerializedData.writeBool(this.message.voiceTranscriptionOpen);
            abstractSerializedData.writeBool(this.message.voiceTranscriptionFinal);
            abstractSerializedData.writeBool(this.message.voiceTranscriptionRated);
            abstractSerializedData.writeInt64(this.message.voiceTranscriptionId);
            abstractSerializedData.writeBool(this.message.premiumEffectWasPlayed);
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeString(this.message.originalLanguage);
            }
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeString(this.message.translatedToLanguage);
            }
            if ((this.flags & 16) != 0) {
                this.message.translatedText.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static void copyParams(TLRPC.Message message, TLRPC.Message message2) {
        message2.voiceTranscription = message.voiceTranscription;
        message2.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
        message2.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
        message2.voiceTranscriptionForce = message.voiceTranscriptionForce;
        message2.voiceTranscriptionRated = message.voiceTranscriptionRated;
        message2.voiceTranscriptionId = message.voiceTranscriptionId;
        message2.premiumEffectWasPlayed = message.premiumEffectWasPlayed;
        message2.originalLanguage = message.originalLanguage;
        message2.translatedToLanguage = message.translatedToLanguage;
        message2.translatedText = message.translatedText;
    }

    public static boolean isEmpty(TLRPC.Message message) {
        return message.voiceTranscription == null && !message.voiceTranscriptionOpen && !message.voiceTranscriptionFinal && !message.voiceTranscriptionRated && !message.voiceTranscriptionForce && message.voiceTranscriptionId == 0 && !message.premiumEffectWasPlayed && message.originalLanguage == null && message.translatedToLanguage == null && message.translatedText == null;
    }

    public static void readLocalParams(TLRPC.Message message, NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return;
        }
        int readInt32 = nativeByteBuffer.readInt32(true);
        if (readInt32 == 1) {
            new Params_v1(message).readParams(nativeByteBuffer, true);
            return;
        }
        throw new RuntimeException("can't read params version = " + readInt32);
    }

    public static NativeByteBuffer writeLocalParams(TLRPC.Message message) {
        if (isEmpty(message)) {
            return null;
        }
        Params_v1 params_v1 = new Params_v1(message);
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
