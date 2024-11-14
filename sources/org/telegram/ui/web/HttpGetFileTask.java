package org.telegram.ui.web;

import android.os.AsyncTask;
import android.os.Build;
import android.webkit.MimeTypeMap;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Stories.recorder.StoryEntry;

public class HttpGetFileTask extends AsyncTask {
    private Utilities.Callback callback;
    private Exception exception;
    private File file;
    private long max_size = -1;

    public HttpGetFileTask(Utilities.Callback callback) {
        this.callback = callback;
    }

    @Override
    public File doInBackground(String... strArr) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(strArr[0]).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            int responseCode = httpURLConnection.getResponseCode();
            InputStream errorStream = (responseCode < 200 || responseCode >= 300) ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
            httpURLConnection.getResponseCode();
            long contentLengthLong = Build.VERSION.SDK_INT >= 24 ? httpURLConnection.getContentLengthLong() : httpURLConnection.getContentLength();
            long j = this.max_size;
            if (j > 0 && contentLengthLong > j) {
                errorStream.close();
                if (this.file != null) {
                    this.file = null;
                }
                return null;
            }
            if (this.file == null) {
                this.file = StoryEntry.makeCacheFile(UserConfig.selectedAccount, MimeTypeMap.getSingleton().getExtensionFromMimeType(httpURLConnection.getContentType()));
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.file));
            byte[] bArr = new byte[1024];
            while (true) {
                int read = errorStream.read(bArr);
                if (read == -1) {
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    errorStream.close();
                    return this.file;
                }
                bufferedOutputStream.write(bArr, 0, read);
            }
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    public void onPostExecute(File file) {
        Utilities.Callback callback = this.callback;
        if (callback != null) {
            if (this.exception != null) {
                file = null;
            }
            callback.run(file);
        }
    }

    public HttpGetFileTask setDestFile(File file) {
        this.file = file;
        return this;
    }

    public HttpGetFileTask setMaxSize(long j) {
        this.max_size = j;
        return this;
    }
}
