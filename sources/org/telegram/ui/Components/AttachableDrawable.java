package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.ImageReceiver;

public interface AttachableDrawable {

    public abstract class CC {
        public static void $default$setParent(AttachableDrawable attachableDrawable, View view) {
        }
    }

    void onAttachedToWindow(ImageReceiver imageReceiver);

    void onDetachedFromWindow(ImageReceiver imageReceiver);

    void setParent(View view);
}
