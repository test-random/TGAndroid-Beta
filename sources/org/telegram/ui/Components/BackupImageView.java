package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;

public class BackupImageView extends View {
    public AnimatedEmojiDrawable animatedEmojiDrawable;
    public ColorFilter animatedEmojiDrawableColorFilter;
    public boolean applyAttach;
    boolean attached;
    private AvatarDrawable avatarDrawable;
    protected boolean blurAllowed;
    protected ImageReceiver blurImageReceiver;
    public Text blurText;
    private ColorFilter blurTextBgColorFilter;
    private Path blurTextClipPath;
    public boolean drawFromStart;
    protected boolean hasBlur;
    protected int height;
    protected ImageReceiver imageReceiver;
    ValueAnimator roundRadiusAnimator;
    protected int width;

    public BackupImageView(Context context) {
        super(context);
        this.width = -1;
        this.height = -1;
        this.applyAttach = true;
        ImageReceiver createImageReciever = createImageReciever();
        this.imageReceiver = createImageReciever;
        createImageReciever.setCrossfadeByScale(0.0f);
        this.imageReceiver.setAllowLoadingOnAttachedOnly(true);
        this.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                BackupImageView.this.lambda$new$0(imageReceiver, z, z2, z3);
            }

            @Override
            public void didSetImageBitmap(int i, String str, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i, str, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
    }

    private void checkCreateBlurredImage() {
        Bitmap bitmap;
        if (!this.hasBlur || this.blurImageReceiver.getBitmap() != null || this.imageReceiver.getBitmap() == null || (bitmap = this.imageReceiver.getBitmap()) == null || bitmap.isRecycled()) {
            return;
        }
        this.blurImageReceiver.setImageBitmap(Utilities.stackBlurBitmapMax(bitmap));
        invalidate();
    }

    public void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (!z || z2) {
            return;
        }
        checkCreateBlurredImage();
    }

    public void animateToRoundRadius(int i) {
        if (getRoundRadius()[0] != i) {
            ValueAnimator valueAnimator = this.roundRadiusAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofInt = ValueAnimator.ofInt(getRoundRadius()[0], i);
            this.roundRadiusAnimator = ofInt;
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BackupImageView.this.setRoundRadius(((Integer) valueAnimator2.getAnimatedValue()).intValue());
                }
            });
            this.roundRadiusAnimator.setDuration(200L);
            this.roundRadiusAnimator.start();
        }
    }

    public void clearImage() {
        this.imageReceiver.clearImage();
    }

    protected ImageReceiver createImageReciever() {
        return new ImageReceiver(this);
    }

    public void drawBlurredText(Canvas canvas, float f) {
        float measuredWidth;
        float measuredHeight;
        int i;
        if (this.blurText == null) {
            return;
        }
        Path path = this.blurTextClipPath;
        if (path == null) {
            this.blurTextClipPath = new Path();
        } else {
            path.rewind();
        }
        int i2 = this.width;
        if (i2 == -1 || (i = this.height) == -1) {
            measuredWidth = getMeasuredWidth();
            measuredHeight = getMeasuredHeight();
        } else {
            measuredWidth = i2;
            measuredHeight = i;
        }
        float currentWidth = this.blurText.getCurrentWidth() + AndroidUtilities.dp(18.0f);
        float dp = AndroidUtilities.dp(28.0f);
        float f2 = (measuredWidth - currentWidth) / 2.0f;
        float f3 = measuredHeight / 2.0f;
        RectF rectF = AndroidUtilities.rectTmp;
        float f4 = dp / 2.0f;
        rectF.set(f2, f3 - f4, currentWidth + f2, f3 + f4);
        this.blurTextClipPath.addRoundRect(rectF, f4, f4, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.blurTextClipPath);
        ImageReceiver imageReceiver = this.blurImageReceiver;
        if (imageReceiver != null && this.blurAllowed) {
            imageReceiver.setColorFilter(this.blurTextBgColorFilter);
            float alpha = this.blurImageReceiver.getAlpha();
            this.blurImageReceiver.setAlpha(f);
            this.blurImageReceiver.draw(canvas);
            this.blurImageReceiver.setAlpha(alpha);
            this.blurImageReceiver.setColorFilter(null);
        }
        this.blurText.draw(canvas, f2 + AndroidUtilities.dp(9.0f), f3, -1, f);
        canvas.restore();
    }

    public AnimatedEmojiDrawable getAnimatedEmojiDrawable() {
        return this.animatedEmojiDrawable;
    }

    public AvatarDrawable getAvatarDrawable() {
        if (this.avatarDrawable == null) {
            this.avatarDrawable = new AvatarDrawable();
        }
        return this.avatarDrawable;
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }

    public int[] getRoundRadius() {
        return this.imageReceiver.getRoundRadius();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        if (this.applyAttach) {
            this.imageReceiver.onAttachedToWindow();
        }
        if (this.blurAllowed) {
            this.blurImageReceiver.onAttachedToWindow();
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.addView(this);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        if (this.applyAttach) {
            this.imageReceiver.onDetachedFromWindow();
        }
        if (this.blurAllowed) {
            this.blurImageReceiver.onDetachedFromWindow();
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this);
        }
    }

    @Override
    public void onDraw(android.graphics.Canvas r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackupImageView.onDraw(android.graphics.Canvas):void");
    }

    public void onNewImageSet() {
        if (this.hasBlur) {
            if (this.blurImageReceiver.getBitmap() != null && !this.blurImageReceiver.getBitmap().isRecycled()) {
                this.blurImageReceiver.getBitmap().recycle();
            }
            this.blurImageReceiver.setImageBitmap((Bitmap) null);
            checkCreateBlurredImage();
        }
    }

    public void setAnimatedEmojiDrawable(AnimatedEmojiDrawable animatedEmojiDrawable) {
        AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
        if (animatedEmojiDrawable2 == animatedEmojiDrawable) {
            return;
        }
        if (this.attached && animatedEmojiDrawable2 != null) {
            animatedEmojiDrawable2.removeView(this);
        }
        this.animatedEmojiDrawable = animatedEmojiDrawable;
        if (this.attached && animatedEmojiDrawable != null) {
            animatedEmojiDrawable.addView(this);
        }
        invalidate();
    }

    public void setAspectFit(boolean z) {
        this.imageReceiver.setAspectFit(z);
    }

    public void setBlurAllowed(boolean z) {
        if (this.attached) {
            throw new IllegalStateException("You should call setBlurAllowed(...) only when detached!");
        }
        this.blurAllowed = z;
        if (z) {
            this.blurImageReceiver = new ImageReceiver();
        }
    }

    public void setBlurredText(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            this.blurText = null;
            return;
        }
        this.blurText = new Text(charSequence, 16.5f, AndroidUtilities.bold());
        if (this.blurTextBgColorFilter == null) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1.2f);
            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, -0.2f);
            this.blurTextBgColorFilter = new ColorMatrixColorFilter(colorMatrix);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.imageReceiver.setColorFilter(colorFilter);
    }

    public void setEmojiColorFilter(ColorFilter colorFilter) {
        this.animatedEmojiDrawableColorFilter = colorFilter;
        invalidate();
    }

    public void setForUserOrChat(TLObject tLObject, AvatarDrawable avatarDrawable) {
        this.imageReceiver.setForUserOrChat(tLObject, avatarDrawable);
        onNewImageSet();
    }

    public void setForUserOrChat(TLObject tLObject, AvatarDrawable avatarDrawable, Object obj) {
        this.imageReceiver.setForUserOrChat(tLObject, avatarDrawable, obj);
        onNewImageSet();
    }

    public void setHasBlur(boolean z) {
        if (z && !this.blurAllowed) {
            throw new IllegalStateException("You should call setBlurAllowed(...) before calling setHasBlur(true)!");
        }
        this.hasBlur = z;
        if (!z) {
            if (this.blurImageReceiver.getBitmap() != null && !this.blurImageReceiver.getBitmap().isRecycled()) {
                this.blurImageReceiver.getBitmap().recycle();
            }
            this.blurImageReceiver.setImageBitmap((Bitmap) null);
        }
        checkCreateBlurredImage();
    }

    public void setImage(String str, String str2, Drawable drawable) {
        setImage(ImageLocation.getForPath(str), str2, (ImageLocation) null, (String) null, drawable, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(String str, String str2, String str3, String str4) {
        setImage(ImageLocation.getForPath(str), str2, ImageLocation.getForPath(str3), str4, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, int i, Object obj) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, (Bitmap) null, (String) null, i, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, Object obj) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, (Bitmap) null, (String) null, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, String str2, Drawable drawable, Object obj) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, (Bitmap) null, str2, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, int i, Object obj) {
        setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, (Bitmap) null, (String) null, i, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Bitmap bitmap, String str3, int i, Object obj) {
        BackupImageView backupImageView;
        Drawable drawable2;
        if (bitmap != null) {
            backupImageView = this;
            drawable2 = new BitmapDrawable((Resources) null, bitmap);
        } else {
            backupImageView = this;
            drawable2 = drawable;
        }
        backupImageView.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, drawable2, i, str3, obj, 0);
        onNewImageSet();
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, null, null, drawable, 0L, null, obj, 1);
        onNewImageSet();
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, String str3, long j, int i, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, drawable, j, str3, obj, i);
        onNewImageSet();
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, String str3, long j, int i, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, null, j, str3, obj, i);
        onNewImageSet();
    }

    public void setImage(SecureDocument secureDocument, String str) {
        setImage(ImageLocation.getForSecureDocument(secureDocument), str, (ImageLocation) null, (String) null, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.imageReceiver.setImageBitmap(bitmap);
        onNewImageSet();
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageReceiver.setImageBitmap(drawable);
        onNewImageSet();
    }

    public void setImageMedia(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Bitmap bitmap, int i, int i2, Object obj) {
        BackupImageView backupImageView;
        BitmapDrawable bitmapDrawable;
        if (bitmap != null) {
            backupImageView = this;
            bitmapDrawable = new BitmapDrawable((Resources) null, bitmap);
        } else {
            backupImageView = this;
            bitmapDrawable = null;
        }
        backupImageView.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, null, null, bitmapDrawable, i, null, obj, i2);
        onNewImageSet();
    }

    public void setImageMedia(VectorAvatarThumbDrawable vectorAvatarThumbDrawable, ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, ImageLocation imageLocation3, String str3, String str4, int i, int i2, Object obj) {
        if (vectorAvatarThumbDrawable != null) {
            this.imageReceiver.setImageBitmap(vectorAvatarThumbDrawable);
        } else {
            this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, imageLocation3, str3, null, i, str4, obj, i2);
        }
        onNewImageSet();
    }

    public void setImageResource(int i) {
        this.imageReceiver.setImageBitmap(getResources().getDrawable(i));
        invalidate();
        onNewImageSet();
    }

    public void setLayerNum(int i) {
        this.imageReceiver.setLayerNum(i);
    }

    public void setOrientation(int i, int i2, boolean z) {
        this.imageReceiver.setOrientation(i, i2, z);
    }

    public void setOrientation(int i, boolean z) {
        this.imageReceiver.setOrientation(i, z);
    }

    public void setRoundRadius(int i) {
        this.imageReceiver.setRoundRadius(i);
        if (this.blurAllowed) {
            this.blurImageReceiver.setRoundRadius(i);
        }
        invalidate();
    }

    public void setRoundRadius(int i, int i2, int i3, int i4) {
        this.imageReceiver.setRoundRadius(i, i2, i3, i4);
        if (this.blurAllowed) {
            this.blurImageReceiver.setRoundRadius(i, i2, i3, i4);
        }
        invalidate();
    }

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
        invalidate();
    }
}
