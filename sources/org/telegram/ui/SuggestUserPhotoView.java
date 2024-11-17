package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.PhotoCropView;

public class SuggestUserPhotoView extends View {
    Drawable arrowDrawable;
    AvatarDrawable avatarDrawable;
    View containterView;
    ImageReceiver currentPhoto;
    ImageReceiver newPhoto;
    Path path;
    PhotoCropView photoCropView;

    public SuggestUserPhotoView(Context context) {
        super(context);
        this.currentPhoto = new ImageReceiver(this);
        this.newPhoto = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.path = new Path();
        AvatarDrawable avatarDrawable = this.avatarDrawable;
        int i = UserConfig.selectedAccount;
        avatarDrawable.setInfo(i, UserConfig.getInstance(i).getCurrentUser());
        this.currentPhoto.setForUserOrChat(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser(), this.avatarDrawable);
        this.newPhoto.setForUserOrChat(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser(), this.avatarDrawable);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.msg_arrow_avatar);
        this.arrowDrawable = drawable;
        drawable.setAlpha(100);
    }

    private void setImageCoords(ImageReceiver imageReceiver, int i, int i2) {
        imageReceiver.setImageCoords(i - AndroidUtilities.dp(30.0f), i2 - AndroidUtilities.dp(30.0f), AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f));
    }

    @Override
    public void draw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth() >> 1;
        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(30.0f);
        int dp = measuredWidth - AndroidUtilities.dp(46.0f);
        int dp2 = AndroidUtilities.dp(46.0f) + measuredWidth;
        setImageCoords(this.currentPhoto, dp, measuredHeight);
        setImageCoords(this.newPhoto, dp2, measuredHeight);
        Drawable drawable = this.arrowDrawable;
        drawable.setBounds(measuredWidth - (drawable.getIntrinsicWidth() / 2), measuredHeight - (this.arrowDrawable.getIntrinsicHeight() / 2), measuredWidth + (this.arrowDrawable.getIntrinsicWidth() / 2), (this.arrowDrawable.getIntrinsicHeight() / 2) + measuredHeight);
        this.arrowDrawable.draw(canvas);
        this.path.reset();
        this.path.addCircle(dp2, measuredHeight, AndroidUtilities.dp(30.0f), Path.Direction.CW);
        this.currentPhoto.draw(canvas);
        if (this.containterView != null) {
            float dp3 = AndroidUtilities.dp(60.0f);
            CropAreaView cropAreaView = this.photoCropView.cropView.areaView;
            float f = dp3 / cropAreaView.size;
            float top = (0.0f - this.photoCropView.getTop()) - cropAreaView.top;
            float left = (0.0f - this.photoCropView.getLeft()) - cropAreaView.left;
            canvas.save();
            canvas.clipPath(this.path);
            canvas.scale(f, f, 0.0f, 0.0f);
            canvas.translate(left, top);
            canvas.translate((dp2 - AndroidUtilities.dp(30.0f)) / f, (measuredHeight - AndroidUtilities.dp(30.0f)) / f);
            PhotoViewer.getInstance().skipLastFrameDraw = true;
            this.containterView.draw(canvas);
            PhotoViewer.getInstance().skipLastFrameDraw = false;
            canvas.restore();
        }
        super.draw(canvas);
        this.containterView.invalidate();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.currentPhoto.onAttachedToWindow();
        this.newPhoto.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.currentPhoto.onDetachedFromWindow();
        this.newPhoto.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        this.currentPhoto.setRoundRadius(AndroidUtilities.dp(30.0f));
        this.newPhoto.setRoundRadius(AndroidUtilities.dp(30.0f));
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), 1073741824));
    }

    public void setImages(TLObject tLObject, View view, PhotoCropView photoCropView) {
        this.avatarDrawable.setInfo(tLObject);
        this.currentPhoto.setForUserOrChat(tLObject, this.avatarDrawable);
        this.containterView = view;
        this.photoCropView = photoCropView;
    }
}
