package org.telegram.tgnet;

public abstract class AbstractSerializedData implements InputSerializedData, OutputSerializedData {
    @Override
    public abstract int getPosition();

    @Override
    public abstract int length();

    @Override
    public abstract boolean readBool(boolean z);

    @Override
    public abstract byte readByte(boolean z);

    @Override
    public abstract byte[] readByteArray(boolean z);

    @Override
    public abstract NativeByteBuffer readByteBuffer(boolean z);

    @Override
    public abstract void readBytes(byte[] bArr, boolean z);

    @Override
    public abstract byte[] readData(int i, boolean z);

    @Override
    public abstract double readDouble(boolean z);

    @Override
    public abstract float readFloat(boolean z);

    @Override
    public abstract int readInt32(boolean z);

    @Override
    public abstract long readInt64(boolean z);

    @Override
    public abstract String readString(boolean z);

    @Override
    public abstract int remaining();

    @Override
    public abstract void skip(int i);

    @Override
    public abstract void writeBool(boolean z);

    @Override
    public abstract void writeByte(byte b);

    @Override
    public abstract void writeByte(int i);

    @Override
    public abstract void writeByteArray(byte[] bArr);

    @Override
    public abstract void writeByteArray(byte[] bArr, int i, int i2);

    @Override
    public abstract void writeByteBuffer(NativeByteBuffer nativeByteBuffer);

    @Override
    public abstract void writeBytes(byte[] bArr);

    @Override
    public abstract void writeBytes(byte[] bArr, int i, int i2);

    @Override
    public abstract void writeDouble(double d);

    @Override
    public abstract void writeFloat(float f);

    @Override
    public abstract void writeInt32(int i);

    @Override
    public abstract void writeInt64(long j);

    @Override
    public abstract void writeString(String str);
}
