package org.telegram.messenger;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.messenger.video.MediaCodecVideoConvertor;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.LaunchActivity;

public class FileLog {
    private static volatile FileLog Instance = null;
    public static boolean databaseIsMalformed = false;
    private static HashSet<String> excludeRequests = null;
    private static ExclusionStrategy exclusionStrategy = null;
    private static Gson gson = null;
    private static boolean gsonDisabled = false;
    private static final String mtproto_tag = "MTProto";
    private static HashSet<String> privateFields = null;
    private static final String tag = "tmessages";
    private boolean initied;
    private OutputStreamWriter streamWriter = null;
    private FastDateFormat dateFormat = null;
    private FastDateFormat fileDateFormat = null;
    private DispatchQueue logQueue = null;
    private File currentFile = null;
    private File networkFile = null;
    private File tonlibFile = null;
    private OutputStreamWriter tlStreamWriter = null;
    private File tlRequestsFile = null;

    public static class IgnoreSentException extends Exception {
        public IgnoreSentException(String str) {
            super(str);
        }
    }

    public static class TLObjectDeserializer implements JsonSerializer {
        private TLObjectDeserializer() {
        }

        @Override
        public JsonElement serialize(TLObject tLObject, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            String name = tLObject.getClass().getName();
            if (name.startsWith("org.telegram.tgnet.")) {
                name = name.substring(19);
            }
            jsonObject.addProperty("_", name);
            try {
                for (Field field : tLObject.getClass().getFields()) {
                    if (FileLog.privateFields == null || !FileLog.privateFields.contains(field.getName())) {
                        field.setAccessible(true);
                        try {
                            Object obj = field.get(tLObject);
                            if (obj != null) {
                                Class<?> cls = obj.getClass();
                                if (!cls.isInstance(DispatchQueue.class)) {
                                    if (!cls.isInstance(AnimatedFileDrawable.class)) {
                                        if (!cls.isInstance(ColorStateList.class)) {
                                            if (cls.isInstance(Context.class)) {
                                            }
                                        }
                                    }
                                }
                            }
                            jsonObject.add(field.getName(), jsonSerializationContext.serialize(obj));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return jsonObject;
        }
    }

    public FileLog() {
        if (BuildVars.LOGS_ENABLED) {
            init();
        }
    }

    private static void checkGson() {
        if (gson == null) {
            HashSet<String> hashSet = new HashSet<>();
            privateFields = hashSet;
            hashSet.add("message");
            privateFields.add("phone");
            privateFields.add("about");
            privateFields.add("status_text");
            privateFields.add("bytes");
            privateFields.add("secret");
            privateFields.add("stripped_thumb");
            privateFields.add("strippedBitmap");
            privateFields.add("networkType");
            privateFields.add("disableFree");
            privateFields.add("mContext");
            privateFields.add("priority");
            privateFields.add("constructor");
            HashSet<String> hashSet2 = new HashSet<>();
            excludeRequests = hashSet2;
            hashSet2.add("TL_upload_getFile");
            excludeRequests.add("TL_upload_getWebFile");
            exclusionStrategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipClass(Class<?> cls) {
                    return cls.isInstance(DispatchQueue.class) || cls.isInstance(AnimatedFileDrawable.class) || cls.isInstance(ColorStateList.class) || cls.isInstance(Context.class);
                }

                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return FileLog.privateFields.contains(fieldAttributes.getName());
                }
            };
            gson = new GsonBuilder().addSerializationExclusionStrategy(exclusionStrategy).registerTypeAdapterFactory(RuntimeClassNameTypeAdapterFactory.of(TLObject.class, "type_", exclusionStrategy)).registerTypeHierarchyAdapter(TLObject.class, new TLObjectDeserializer()).create();
        }
    }

    public static void cleanupLogs() {
        File[] listFiles;
        ensureInitied();
        File logsDir = AndroidUtilities.getLogsDir();
        if (logsDir == null || (listFiles = logsDir.listFiles()) == null) {
            return;
        }
        for (File file : listFiles) {
            if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && ((getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath())) && (getInstance().tonlibFile == null || !file.getAbsolutePath().equals(getInstance().tonlibFile.getAbsolutePath())))) {
                file.delete();
            }
        }
    }

    public static void d(final String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d("tmessages", str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$d$6(str);
                    }
                });
            }
        }
    }

    public static void disableGson(boolean z) {
        gsonDisabled = z;
    }

    public static void dumpResponseAndRequest(final int i, TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error, final long j, final long j2, final int i2) {
        StringBuilder sb;
        String json;
        if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED && tLObject != null) {
            String simpleName = tLObject.getClass().getSimpleName();
            checkGson();
            if (excludeRequests.contains(simpleName) && tL_error == null) {
                return;
            }
            try {
                final String str = "req -> " + simpleName + " : " + gson.toJson(tLObject);
                String str2 = "null";
                if (tLObject2 == null) {
                    if (tL_error != null) {
                        sb = new StringBuilder();
                        sb.append("err -> ");
                        sb.append(tL_error.getClass().getSimpleName());
                        sb.append(" : ");
                        json = gson.toJson(tL_error);
                    }
                    final String str3 = str2;
                    final long currentTimeMillis = System.currentTimeMillis();
                    getInstance().logQueue.postRunnable(new Runnable() {
                        @Override
                        public final void run() {
                            FileLog.lambda$dumpResponseAndRequest$0(j, j2, i2, i, currentTimeMillis, str, str3);
                        }
                    });
                }
                sb = new StringBuilder();
                sb.append("res -> ");
                sb.append(tLObject2.getClass().getSimpleName());
                sb.append(" : ");
                json = gson.toJson(tLObject2);
                sb.append(json);
                str2 = sb.toString();
                final String str32 = str2;
                final long currentTimeMillis2 = System.currentTimeMillis();
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$dumpResponseAndRequest$0(j, j2, i2, i, currentTimeMillis2, str, str32);
                    }
                });
            } catch (Throwable th) {
                e(th, BuildVars.DEBUG_PRIVATE_VERSION);
            }
        }
    }

    public static void dumpUnparsedMessage(TLObject tLObject, final long j, final int i) {
        if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED && tLObject != null) {
            try {
                checkGson();
                getInstance().dateFormat.format(System.currentTimeMillis());
                StringBuilder sb = new StringBuilder();
                sb.append("receive message -> ");
                sb.append(tLObject.getClass().getSimpleName());
                sb.append(" : ");
                sb.append(gsonDisabled ? tLObject : gson.toJson(tLObject));
                final String sb2 = sb.toString();
                final long currentTimeMillis = System.currentTimeMillis();
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$dumpUnparsedMessage$1(currentTimeMillis, j, i, sb2);
                    }
                });
            } catch (Throwable unused) {
            }
        }
    }

    public static void e(final String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$e$3(str);
                    }
                });
            }
        }
    }

    public static void e(final String str, final Throwable th) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e("tmessages", str, th);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$e$2(str, th);
                    }
                });
            }
        }
    }

    public static void e(Throwable th) {
        e(th, true);
    }

    public static void e(final Throwable th, boolean z) {
        if (BuildVars.LOGS_ENABLED) {
            if (BuildVars.DEBUG_VERSION && needSent(th) && z) {
                AndroidUtilities.appCenterLog(th);
            }
            if (BuildVars.DEBUG_VERSION && th.getMessage() != null && th.getMessage().contains("disk image is malformed") && !databaseIsMalformed) {
                d("copy malformed files");
                databaseIsMalformed = true;
                File file = new File(ApplicationLoader.getFilesDirFixed(), "malformed_database/");
                file.mkdirs();
                ArrayList<File> databaseFiles = MessagesStorage.getInstance(UserConfig.selectedAccount).getDatabaseFiles();
                for (int i = 0; i < databaseFiles.size(); i++) {
                    try {
                        AndroidUtilities.copyFile(databaseFiles.get(i), new File(file, databaseFiles.get(i).getName()));
                    } catch (IOException e) {
                        e(e);
                    }
                }
            }
            ensureInitied();
            th.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$e$4(th);
                    }
                });
            } else {
                th.printStackTrace();
            }
        }
    }

    public static void ensureInitied() {
        getInstance().init();
    }

    public static void fatal(Throwable th) {
        fatal(th, true);
    }

    public static void fatal(final Throwable th, boolean z) {
        if (BuildVars.LOGS_ENABLED) {
            if (z && BuildVars.DEBUG_VERSION && needSent(th)) {
                AndroidUtilities.appCenterLog(th);
            }
            ensureInitied();
            th.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$fatal$5(th);
                    }
                });
                return;
            }
            th.printStackTrace();
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                System.exit(2);
            }
        }
    }

    public static FileLog getInstance() {
        FileLog fileLog = Instance;
        if (fileLog == null) {
            synchronized (FileLog.class) {
                try {
                    fileLog = Instance;
                    if (fileLog == null) {
                        fileLog = new FileLog();
                        Instance = fileLog;
                    }
                } finally {
                }
            }
        }
        return fileLog;
    }

    public static String getNetworkLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File logsDir = AndroidUtilities.getLogsDir();
            if (logsDir == null) {
                return "";
            }
            getInstance().networkFile = new File(logsDir, getInstance().fileDateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    public static String getTonlibLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File logsDir = AndroidUtilities.getLogsDir();
            if (logsDir == null) {
                return "";
            }
            getInstance().tonlibFile = new File(logsDir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_tonlib.txt");
            return getInstance().tonlibFile.getAbsolutePath();
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    public static void lambda$d$6(String str) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + str + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if (AndroidUtilities.isENOSPC(e)) {
                LaunchActivity.checkFreeDiscSpaceStatic(1);
            }
        }
    }

    public static void lambda$dumpResponseAndRequest$0(long j, long j2, int i, int i2, long j3, String str, String str2) {
        try {
            String str3 = "requestMsgId=" + j + " requestingTime=" + (System.currentTimeMillis() - j2) + " request_token=" + i + " account=" + i2;
            getInstance().tlStreamWriter.write(getInstance().dateFormat.format(j3) + " " + str3);
            getInstance().tlStreamWriter.write("\n");
            getInstance().tlStreamWriter.write(str);
            getInstance().tlStreamWriter.write("\n");
            getInstance().tlStreamWriter.write(str2);
            getInstance().tlStreamWriter.write("\n\n");
            getInstance().tlStreamWriter.flush();
            Log.d("MTProto", str3);
            Log.d("MTProto", str);
            Log.d("MTProto", str2);
            Log.d("MTProto", " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lambda$dumpUnparsedMessage$1(long j, long j2, int i, String str) {
        try {
            getInstance().tlStreamWriter.write(getInstance().dateFormat.format(j) + " msgId=" + j2 + " account=" + i);
            getInstance().tlStreamWriter.write("\n");
            getInstance().tlStreamWriter.write(str);
            getInstance().tlStreamWriter.write("\n\n");
            getInstance().tlStreamWriter.flush();
            Log.d("MTProto", "msgId=" + j2 + " account=" + i);
            Log.d("MTProto", str);
            Log.d("MTProto", " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lambda$e$2(String str, Throwable th) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + str + "\n");
            getInstance().streamWriter.write(th.toString());
            StackTraceElement[] stackTrace = th.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: \tat " + stackTraceElement + "\n");
            }
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lambda$e$3(String str) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + str + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lambda$e$4(Throwable th) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + th + "\n");
            for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: \tat " + stackTraceElement + "\n");
            }
            Throwable cause = th.getCause();
            if (cause != null) {
                getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: Caused by " + cause + "\n");
                for (StackTraceElement stackTraceElement2 : cause.getStackTrace()) {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: \tat " + stackTraceElement2 + "\n");
                }
            }
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lambda$fatal$5(Throwable th) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " FATAL/tmessages: " + th + "\n");
            for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " FATAL/tmessages: \tat " + stackTraceElement + "\n");
            }
            Throwable cause = th.getCause();
            if (cause != null) {
                getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: Caused by " + cause + "\n");
                for (StackTraceElement stackTraceElement2 : cause.getStackTrace()) {
                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: \tat " + stackTraceElement2 + "\n");
                }
            }
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            System.exit(2);
        }
    }

    public static void lambda$w$7(String str) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + str + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean needSent(Throwable th) {
        return ((th instanceof InterruptedException) || (th instanceof MediaCodecVideoConvertor.ConversionCanceledException) || (th instanceof IgnoreSentException)) ? false : true;
    }

    public static void w(final String str) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w("tmessages", str);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        FileLog.lambda$w$7(str);
                    }
                });
            }
        }
    }

    public void init() {
        File logsDir;
        if (this.initied) {
            return;
        }
        Locale locale = Locale.US;
        this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss.SSS", locale);
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", locale);
        this.fileDateFormat = fastDateFormat;
        String format = fastDateFormat.format(System.currentTimeMillis());
        try {
            logsDir = AndroidUtilities.getLogsDir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (logsDir == null) {
            return;
        }
        this.currentFile = new File(logsDir, format + ".txt");
        this.tlRequestsFile = new File(logsDir, format + "_mtproto.txt");
        try {
            this.logQueue = new DispatchQueue("logQueue");
            this.currentFile.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
            this.streamWriter = outputStreamWriter;
            outputStreamWriter.write("-----start log " + format + "-----\n");
            this.streamWriter.flush();
            OutputStreamWriter outputStreamWriter2 = new OutputStreamWriter(new FileOutputStream(this.tlRequestsFile));
            this.tlStreamWriter = outputStreamWriter2;
            outputStreamWriter2.write("-----start log " + format + "-----\n");
            this.tlStreamWriter.flush();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.initied = true;
    }
}
