package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoAttachPermissionCell extends FrameLayout {
    private ImageView imageView;
    private ImageView imageView2;
    private int itemSize;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public PhotoAttachPermissionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        ImageView imageView2 = this.imageView;
        int themedColor = getThemedColor(Theme.key_chat_attachPermissionImage);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView2.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        addView(this.imageView, LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        ImageView imageView3 = new ImageView(context);
        this.imageView2 = imageView3;
        imageView3.setScaleType(scaleType);
        this.imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_attachPermissionMark), mode));
        addView(this.imageView2, LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(getThemedColor(Theme.key_chat_attachPermissionText));
        this.textView.setTextSize(1, 12.0f);
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 5.0f, 13.0f, 5.0f, 0.0f));
        this.itemSize = AndroidUtilities.dp(80.0f);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), 1073741824));
    }

    public void setItemSize(int i) {
        this.itemSize = i;
    }

    public void setType(int i) {
        ImageView imageView;
        float f;
        float f2;
        int i2;
        float f3;
        int i3;
        float f4;
        if (i == 0) {
            this.imageView.setImageResource(R.drawable.permissions_camera1);
            this.imageView2.setImageResource(R.drawable.permissions_camera2);
            this.textView.setText(LocaleController.getString(R.string.CameraPermissionText));
            imageView = this.imageView;
            f = 0.0f;
            f2 = 27.0f;
            i2 = 44;
            f3 = 44.0f;
            i3 = 17;
            f4 = 5.0f;
        } else {
            this.imageView.setImageResource(R.drawable.permissions_gallery1);
            this.imageView2.setImageResource(R.drawable.permissions_gallery2);
            this.textView.setText(LocaleController.getString(R.string.GalleryPermissionText));
            imageView = this.imageView;
            f = 2.0f;
            f2 = 27.0f;
            i2 = 44;
            f3 = 44.0f;
            i3 = 17;
            f4 = 0.0f;
        }
        imageView.setLayoutParams(LayoutHelper.createFrame(i2, f3, i3, f4, 0.0f, f, f2));
        this.imageView2.setLayoutParams(LayoutHelper.createFrame(44, f3, i3, f4, 0.0f, f, f2));
    }
}
