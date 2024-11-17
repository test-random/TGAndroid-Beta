package org.telegram.messenger.voip;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import org.telegram.messenger.ApplicationLoader;

public class CallNotificationSoundProvider extends ContentProvider {
    @Override
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String str) {
        if (!"r".equals(str)) {
            throw new SecurityException("Unexpected file mode " + str);
        }
        if (ApplicationLoader.applicationContext == null) {
            throw new FileNotFoundException("Unexpected application state");
        }
        try {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null) {
                sharedInstance.startRingtoneAndVibration();
            }
            ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(createPipe[1]);
            autoCloseOutputStream.write(new byte[]{82, 73, 70, 70, 41, 0, 0, 0, 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, 1, 0, 68, -84, 0, 0, 16, -79, 2, 0, 2, 0, 16, 0, 100, 97, 116, 97, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            autoCloseOutputStream.close();
            return createPipe[0];
        } catch (Exception e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Override
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
