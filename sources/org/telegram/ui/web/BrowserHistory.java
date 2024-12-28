package org.telegram.ui.web;

import android.util.LongSparseArray;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.web.WebMetadataCache;

public abstract class BrowserHistory {
    private static ArrayList callbacks;
    private static ArrayList history;
    private static LongSparseArray historyById;
    public static boolean historyLoaded;
    public static boolean historyLoading;

    public static class Entry extends TLObject {
        public long id;
        public WebMetadataCache.WebMetadata meta;
        public long time;
        public String url;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.id = inputSerializedData.readInt64(z);
            this.time = inputSerializedData.readInt64(z);
            this.url = inputSerializedData.readString(z);
            WebMetadataCache.WebMetadata webMetadata = new WebMetadataCache.WebMetadata();
            this.meta = webMetadata;
            webMetadata.readParams(inputSerializedData, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.time);
            String str = this.url;
            if (str == null) {
                str = "";
            }
            outputSerializedData.writeString(str);
            this.meta.serializeToStream(outputSerializedData);
        }
    }

    public static void clearHistory() {
        try {
            history.clear();
            historyById.clear();
            File historyFile = getHistoryFile();
            if (historyFile.exists()) {
                historyFile.delete();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static ArrayList getHistory() {
        return getHistory(null);
    }

    public static ArrayList getHistory(Utilities.Callback callback) {
        boolean z;
        if (callback == null || historyLoaded) {
            z = false;
        } else {
            if (callbacks == null) {
                callbacks = new ArrayList();
            }
            callbacks.add(callback);
            z = true;
        }
        preloadHistory();
        if (z) {
            return null;
        }
        return history;
    }

    public static File getHistoryFile() {
        return new File(FileLoader.getDirectory(4), "webhistory.dat");
    }

    public static void lambda$preloadHistory$0(ArrayList arrayList, LongSparseArray longSparseArray) {
        history.addAll(0, arrayList);
        for (int i = 0; i < longSparseArray.size(); i++) {
            historyById.put(longSparseArray.keyAt(i), (Entry) longSparseArray.valueAt(i));
        }
        historyLoaded = true;
        historyLoading = false;
        ArrayList arrayList2 = callbacks;
        if (arrayList2 != null) {
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                ((Utilities.Callback) it.next()).run(arrayList);
            }
            callbacks = null;
        }
    }

    public static void lambda$preloadHistory$1() {
        final ArrayList arrayList = new ArrayList();
        final LongSparseArray longSparseArray = new LongSparseArray();
        try {
            File historyFile = getHistoryFile();
            if (historyFile.exists()) {
                SerializedData serializedData = new SerializedData(historyFile);
                long readInt64 = serializedData.readInt64(true);
                for (long j = 0; j < readInt64; j++) {
                    Entry entry = new Entry();
                    entry.readParams(serializedData, true);
                    arrayList.add(entry);
                    longSparseArray.put(entry.id, entry);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BrowserHistory.lambda$preloadHistory$0(arrayList, longSparseArray);
            }
        });
    }

    public static void lambda$saveHistory$2() {
        try {
            File historyFile = getHistoryFile();
            if (!historyFile.exists()) {
                historyFile.createNewFile();
            }
            long size = history.size();
            SerializedData serializedData = new SerializedData(true);
            serializedData.writeInt64(size);
            Iterator it = history.iterator();
            while (it.hasNext()) {
                ((Entry) it.next()).serializeToStream(serializedData);
            }
            SerializedData serializedData2 = new SerializedData(serializedData.length());
            serializedData2.writeInt64(size);
            Iterator it2 = history.iterator();
            while (it2.hasNext()) {
                ((Entry) it2.next()).serializeToStream(serializedData2);
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(historyFile);
                fileOutputStream.write(serializedData2.toByteArray());
                fileOutputStream.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static void preloadHistory() {
        if (historyLoading || historyLoaded) {
            return;
        }
        historyLoading = true;
        history = new ArrayList();
        historyById = new LongSparseArray();
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                BrowserHistory.lambda$preloadHistory$1();
            }
        });
    }

    public static void pushHistory(Entry entry) {
        if (entry == null || entry.meta == null) {
            return;
        }
        preloadHistory();
        Entry entry2 = (Entry) historyById.get(entry.id);
        if (entry2 != null) {
            entry2.meta = entry.meta;
        } else {
            history.add(entry);
            historyById.put(entry.id, entry);
        }
        scheduleHistorySave();
    }

    public static void saveHistory() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                BrowserHistory.lambda$saveHistory$2();
            }
        });
    }

    private static void scheduleHistorySave() {
        AndroidUtilities.cancelRunOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BrowserHistory.saveHistory();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BrowserHistory.saveHistory();
            }
        }, 1000L);
    }
}
