package org.telegram.ui.Stories.recorder;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CancellationSignal;
import android.util.Size;

public abstract class PreviewView$$ExternalSyntheticApiModelOutline0 {
    public static Bitmap m(ContentResolver contentResolver, Uri uri, Size size, CancellationSignal cancellationSignal) {
        return contentResolver.loadThumbnail(uri, size, cancellationSignal);
    }
}
