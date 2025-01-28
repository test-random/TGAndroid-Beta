package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PhotoViewer;

public class EditCoverButton extends View {
    private final Drawable arrowDrawable;
    private final PhotoViewerBlurDrawable blur;
    private final ButtonBounce bounce;
    private final RectF imageBounds;
    private ImageReceiver imageReceiver;
    private View.OnClickListener listener;
    private final Text text;

    public EditCoverButton(Context context, PhotoViewer photoViewer, CharSequence charSequence, boolean z) {
        super(context);
        this.bounce = new ButtonBounce(this);
        this.imageBounds = new RectF();
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.imageReceiver = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(22.66f));
        this.text = new Text(charSequence, 14.0f, AndroidUtilities.bold());
        this.blur = new PhotoViewerBlurDrawable(photoViewer, photoViewer.blurManager, this).setApplyBounds(false);
        if (!z) {
            this.arrowDrawable = null;
            return;
        }
        Drawable mutate = context.getResources().getDrawable(R.drawable.arrow_newchat).mutate();
        this.arrowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-1711276033, PorterDuff.Mode.SRC_IN));
    }

    public void lambda$setImage$1(String str) {
        final Bitmap decodeFile = BitmapFactory.decodeFile(str);
        Canvas canvas = new Canvas(Bitmap.createBitmap(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f), Bitmap.Config.ARGB_8888));
        Paint paint = new Paint(3);
        canvas.translate(r0.getWidth() / 2.0f, r0.getHeight() / 2.0f);
        float max = Math.max(r0.getWidth() / decodeFile.getWidth(), r0.getHeight() / decodeFile.getHeight());
        canvas.scale(max, max);
        canvas.drawBitmap(decodeFile, (-decodeFile.getWidth()) / 2.0f, (-decodeFile.getHeight()) / 2.0f, paint);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                EditCoverButton.this.lambda$setImage$0(decodeFile);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float scale = this.bounce.getScale(0.05f);
        canvas.save();
        canvas.scale(scale, scale, getWidth() / 2.0f, getHeight() / 2.0f);
        boolean hasBitmapImage = this.imageReceiver.hasBitmapImage();
        int ceil = ((int) Math.ceil(this.text.getCurrentWidth())) + AndroidUtilities.dp(hasBitmapImage ? 30.33f : 11.33f) + AndroidUtilities.dp(19.0f);
        int dp = AndroidUtilities.dp(24.0f);
        int width = (getWidth() - ceil) / 2;
        int height = getHeight() / 2;
        int i = dp / 2;
        int i2 = ceil + width;
        this.blur.setBounds(width, height - i, i2, i + height);
        this.blur.draw(canvas);
        if (hasBitmapImage) {
            this.imageBounds.set(AndroidUtilities.dp(0.66f) + width, height - (AndroidUtilities.dp(22.66f) / 2), AndroidUtilities.dp(23.32f) + width, (AndroidUtilities.dp(22.66f) / 2) + height);
            this.imageReceiver.setImageCoords(this.imageBounds);
            this.imageReceiver.draw(canvas);
        }
        this.text.draw(canvas, width + r1, height, -1, 1.0f);
        this.arrowDrawable.setBounds(i2 - AndroidUtilities.dp(17.0f), height - AndroidUtilities.dp(6.0f), i2 - AndroidUtilities.dp(5.0f), height + AndroidUtilities.dp(6.0f));
        this.arrowDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean contains = this.blur.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            this.bounce.setPressed(contains);
        } else if (motionEvent.getAction() == 2) {
            if (!contains) {
                this.bounce.setPressed(false);
            }
        } else if (motionEvent.getAction() == 3) {
            if (this.bounce.isPressed()) {
                this.bounce.setPressed(false);
                return true;
            }
        } else if (motionEvent.getAction() == 1 && this.bounce.isPressed()) {
            this.bounce.setPressed(false);
            View.OnClickListener onClickListener = this.listener;
            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
            return true;
        }
        return this.bounce.isPressed();
    }

    public void lambda$setImage$0(Bitmap bitmap) {
        this.imageReceiver.setImageBitmap(bitmap);
        invalidate();
    }

    public void setImage(final String str) {
        if (str == null) {
            lambda$setImage$0((Bitmap) null);
        } else {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    EditCoverButton.this.lambda$setImage$1(str);
                }
            });
        }
    }

    public void setImage(TLRPC.Photo photo, Object obj) {
        if (photo == null) {
            lambda$setImage$0((Bitmap) null);
            return;
        }
        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(48.0f), false, null, true);
        this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, photo), "24_24", ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(24.0f), false, closestPhotoSizeWithSize, false), photo), "24_24", 0L, null, obj, 0);
    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.listener = onClickListener;
    }
}
