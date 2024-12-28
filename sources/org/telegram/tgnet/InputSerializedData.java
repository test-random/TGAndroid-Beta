package org.telegram.tgnet;

public interface InputSerializedData {
    int getPosition();

    int length();

    boolean readBool(boolean z);

    byte readByte(boolean z);

    byte[] readByteArray(boolean z);

    NativeByteBuffer readByteBuffer(boolean z);

    void readBytes(byte[] bArr, boolean z);

    byte[] readData(int i, boolean z);

    double readDouble(boolean z);

    float readFloat(boolean z);

    int readInt32(boolean z);

    long readInt64(boolean z);

    String readString(boolean z);

    int remaining();

    void skip(int i);
}
