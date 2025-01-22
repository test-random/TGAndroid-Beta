package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.PhotoViewer;

public class EditCoverButton extends View {
    private final Drawable arrowDrawable;
    private final PhotoViewerBlurDrawable blur;
    private final ButtonBounce bounce;
    private final Path clipPath;
    private Bitmap image;
    private final RectF imageBounds;
    private final Paint imagePaint;
    private View.OnClickListener listener;
    private final Text text;

    public EditCoverButton(Context context, PhotoViewer photoViewer, CharSequence charSequence, boolean z) {
        super(context);
        this.bounce = new ButtonBounce(this);
        this.clipPath = new Path();
        this.imagePaint = new Paint(3);
        this.imageBounds = new RectF();
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

    @Override
    protected void onDraw(Canvas canvas) {
        float scale = this.bounce.getScale(0.05f);
        canvas.save();
        canvas.scale(scale, scale, getWidth() / 2.0f, getHeight() / 2.0f);
        int ceil = ((int) Math.ceil(this.text.getCurrentWidth())) + AndroidUtilities.dp(this.image != null ? 30.33f : 11.33f) + AndroidUtilities.dp(19.0f);
        int dp = AndroidUtilities.dp(24.0f);
        int width = (getWidth() - ceil) / 2;
        int height = getHeight() / 2;
        int i = dp / 2;
        int i2 = ceil + width;
        this.blur.setBounds(width, height - i, i2, i + height);
        this.blur.draw(canvas);
        if (this.image != null) {
            this.imageBounds.set(AndroidUtilities.dp(0.66f) + width, height - (AndroidUtilities.dp(22.66f) / 2), AndroidUtilities.dp(23.32f) + width, (AndroidUtilities.dp(22.66f) / 2) + height);
            canvas.save();
            this.clipPath.rewind();
            this.clipPath.addRoundRect(this.imageBounds, AndroidUtilities.dp(22.66f), AndroidUtilities.dp(22.66f), Path.Direction.CW);
            canvas.clipPath(this.clipPath);
            float max = Math.max(AndroidUtilities.dp(22.66f) / this.image.getWidth(), AndroidUtilities.dp(22.66f) / this.image.getHeight());
            canvas.translate(this.imageBounds.centerX(), this.imageBounds.centerY());
            canvas.scale(max, max);
            canvas.translate((-this.image.getWidth()) / 2.0f, (-this.image.getHeight()) / 2.0f);
            canvas.drawBitmap(this.image, 0.0f, 0.0f, this.imagePaint);
            canvas.restore();
        }
        this.text.draw(canvas, width + r0, height, -1, 1.0f);
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

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
        invalidate();
    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.listener = onClickListener;
    }
}
