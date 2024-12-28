package org.telegram.tgnet;

public interface OutputSerializedData {
    int getPosition();

    void skip(int i);

    void writeBool(boolean z);

    void writeByte(byte b);

    void writeByte(int i);

    void writeByteArray(byte[] bArr);

    void writeByteArray(byte[] bArr, int i, int i2);

    void writeByteBuffer(NativeByteBuffer nativeByteBuffer);

    void writeBytes(byte[] bArr);

    void writeBytes(byte[] bArr, int i, int i2);

    void writeDouble(double d);

    void writeFloat(float f);

    void writeInt32(int i);

    void writeInt64(long j);

    void writeString(String str);
}
