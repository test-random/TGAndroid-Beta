package org.telegram.tgnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;

public class Vector<T extends TLObject> extends TLObject {
    public static final int constructor = 481674261;
    private final TLDeserializer<T> itemDeserializer;
    public final ArrayList<T> objects = new ArrayList<>();

    public static class Int extends TLObject {
        public int value;

        public Int(int i) {
            this.value = i;
        }

        public static Int TLDeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            return new Int(i);
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.value = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(this.value);
        }
    }

    public static class Long extends TLObject {
        public long value;

        public Long(int i, int i2) {
            this.value = (i2 & 4294967295L) | (i << 32);
        }

        public static Long TLDeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            return new Long(i, inputSerializedData.readInt32(z));
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.value = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt64(this.value);
        }
    }

    @FunctionalInterface
    public interface TLDeserializer<T extends TLObject> {
        T deserialize(InputSerializedData inputSerializedData, int i, boolean z);
    }

    public Vector(TLDeserializer<T> tLDeserializer) {
        this.itemDeserializer = tLDeserializer;
    }

    public static <T extends TLObject> Vector<T> TLDeserialize(InputSerializedData inputSerializedData, int i, boolean z, TLDeserializer<T> tLDeserializer) {
        if (i != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in Vector", Integer.valueOf(i)));
            }
            return null;
        }
        Vector<T> vector = new Vector<>(tLDeserializer);
        vector.readParams(inputSerializedData, z);
        return vector;
    }

    public static Vector<Int> TLDeserializeInt(InputSerializedData inputSerializedData, int i, boolean z) {
        if (i != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGift", Integer.valueOf(i)));
            }
            return null;
        }
        Vector<Int> vector = new Vector<>(new Vector$$ExternalSyntheticLambda5());
        vector.readParams(inputSerializedData, z);
        return vector;
    }

    public static Vector<Int> TLDeserializeLong(InputSerializedData inputSerializedData, int i, boolean z) {
        if (i != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGift", Integer.valueOf(i)));
            }
            return null;
        }
        Vector<Int> vector = new Vector<>(new Vector$$ExternalSyntheticLambda5());
        vector.readParams(inputSerializedData, z);
        return vector;
    }

    public static <T> ArrayList<T> deserialize(InputSerializedData inputSerializedData, Utilities.CallbackReturn<Boolean, T> callbackReturn, boolean z) {
        int readInt32 = inputSerializedData.readInt32(z);
        if (readInt32 != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in Vector", Integer.valueOf(readInt32)));
            }
            return new ArrayList<>();
        }
        int readInt322 = inputSerializedData.readInt32(z);
        ArrayList<T> arrayList = new ArrayList<>(readInt322);
        for (int i = 0; i < readInt322; i++) {
            arrayList.add(callbackReturn.run(Boolean.valueOf(z)));
        }
        return arrayList;
    }

    public static <T extends TLObject> ArrayList<T> deserialize(InputSerializedData inputSerializedData, TLDeserializer<T> tLDeserializer, boolean z) {
        int readInt32 = inputSerializedData.readInt32(z);
        if (readInt32 != 481674261) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in Vector", Integer.valueOf(readInt32)));
            }
            return new ArrayList<>();
        }
        int readInt322 = inputSerializedData.readInt32(z);
        ArrayList<T> arrayList = new ArrayList<>(readInt322);
        for (int i = 0; i < readInt322; i++) {
            T deserialize = tLDeserializer.deserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if (deserialize != null) {
                arrayList.add(deserialize);
            }
        }
        return arrayList;
    }

    public static ArrayList<Integer> deserializeInt(final InputSerializedData inputSerializedData, boolean z) {
        Objects.requireNonNull(inputSerializedData);
        return deserialize(inputSerializedData, new Utilities.CallbackReturn() {
            @Override
            public final Object run(Object obj) {
                return Integer.valueOf(InputSerializedData.this.readInt32(((Boolean) obj).booleanValue()));
            }
        }, z);
    }

    public static ArrayList<java.lang.Long> deserializeLong(final InputSerializedData inputSerializedData, boolean z) {
        Objects.requireNonNull(inputSerializedData);
        return deserialize(inputSerializedData, new Utilities.CallbackReturn() {
            @Override
            public final Object run(Object obj) {
                return Long.valueOf(InputSerializedData.this.readInt64(((Boolean) obj).booleanValue()));
            }
        }, z);
    }

    public static ArrayList<String> deserializeString(final InputSerializedData inputSerializedData, boolean z) {
        Objects.requireNonNull(inputSerializedData);
        return deserialize(inputSerializedData, new Utilities.CallbackReturn() {
            @Override
            public final Object run(Object obj) {
                return InputSerializedData.this.readString(((Boolean) obj).booleanValue());
            }
        }, z);
    }

    public static <T extends TLObject> void serialize(OutputSerializedData outputSerializedData, ArrayList<T> arrayList) {
        outputSerializedData.writeInt32(481674261);
        outputSerializedData.writeInt32(arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).serializeToStream(outputSerializedData);
        }
    }

    public static <T> void serialize(OutputSerializedData outputSerializedData, Utilities.Callback<T> callback, ArrayList<T> arrayList) {
        outputSerializedData.writeInt32(481674261);
        outputSerializedData.writeInt32(arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            callback.run(arrayList.get(i));
        }
    }

    public static void serializeInt(OutputSerializedData outputSerializedData, ArrayList<Integer> arrayList) {
        Objects.requireNonNull(outputSerializedData);
        serialize(outputSerializedData, new Vector$$ExternalSyntheticLambda3(outputSerializedData), arrayList);
    }

    public static void serializeLong(final OutputSerializedData outputSerializedData, ArrayList<java.lang.Long> arrayList) {
        Objects.requireNonNull(outputSerializedData);
        serialize(outputSerializedData, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                OutputSerializedData.this.writeInt64(((Long) obj).longValue());
            }
        }, arrayList);
    }

    public static void serializeString(final OutputSerializedData outputSerializedData, ArrayList<String> arrayList) {
        Objects.requireNonNull(outputSerializedData);
        serialize(outputSerializedData, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                OutputSerializedData.this.writeString((String) obj);
            }
        }, arrayList);
    }

    @Override
    public void readParams(InputSerializedData inputSerializedData, boolean z) {
        int readInt32 = inputSerializedData.readInt32(z);
        for (int i = 0; i < readInt32; i++) {
            this.objects.add(this.itemDeserializer.deserialize(inputSerializedData, inputSerializedData.readInt32(z), z));
        }
    }

    @Override
    public void serializeToStream(OutputSerializedData outputSerializedData) {
        serialize(outputSerializedData, this.objects);
    }

    public ArrayList<Integer> toIntArray() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        Iterator<T> it = this.objects.iterator();
        while (it.hasNext()) {
            T next = it.next();
            if (next instanceof Int) {
                arrayList.add(Integer.valueOf(((Int) next).value));
            }
        }
        return arrayList;
    }

    public ArrayList<java.lang.Long> toLongArray() {
        ArrayList<java.lang.Long> arrayList = new ArrayList<>();
        Iterator<T> it = this.objects.iterator();
        while (it.hasNext()) {
            T next = it.next();
            if (next instanceof Long) {
                arrayList.add(java.lang.Long.valueOf(((Long) next).value));
            }
        }
        return arrayList;
    }
}
