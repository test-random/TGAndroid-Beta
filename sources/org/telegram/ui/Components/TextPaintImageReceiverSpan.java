package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.web.WebInstantView;

public class TextPaintImageReceiverSpan extends ReplacementSpan {
    private boolean alignTop;
    private int height;
    private ImageReceiver imageReceiver;
    private int width;

    public TextPaintImageReceiverSpan(View view, TLRPC.Document document, Object obj, int i, int i2, boolean z, boolean z2) {
        String format = String.format(Locale.US, "%d_%d_i", Integer.valueOf(i), Integer.valueOf(i2));
        this.width = i;
        this.height = i2;
        ImageReceiver imageReceiver = new ImageReceiver(view);
        this.imageReceiver = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        if (z2) {
            this.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                @Override
                public final void didSetImage(ImageReceiver imageReceiver2, boolean z3, boolean z4, boolean z5) {
                    TextPaintImageReceiverSpan.lambda$new$0(imageReceiver2, z3, z4, z5);
                }

                @Override
                public void didSetImageBitmap(int i3, String str, Drawable drawable) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i3, str, drawable);
                }

                @Override
                public void onAnimationReady(ImageReceiver imageReceiver2) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
                }
            });
        }
        this.imageReceiver.setImage(ImageLocation.getForDocument(document), format, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document), format, -1L, null, obj, 1);
        this.alignTop = z;
    }

    public TextPaintImageReceiverSpan(View view, WebInstantView.WebPhoto webPhoto, Object obj, int i, int i2, boolean z, boolean z2) {
        String.format(Locale.US, "%d_%d_i", Integer.valueOf(i), Integer.valueOf(i2));
        this.width = i;
        this.height = i2;
        ImageReceiver imageReceiver = new ImageReceiver(view);
        this.imageReceiver = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        if (z2) {
            this.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                @Override
                public final void didSetImage(ImageReceiver imageReceiver2, boolean z3, boolean z4, boolean z5) {
                    TextPaintImageReceiverSpan.lambda$new$1(imageReceiver2, z3, z4, z5);
                }

                @Override
                public void didSetImageBitmap(int i3, String str, Drawable drawable) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i3, str, drawable);
                }

                @Override
                public void onAnimationReady(ImageReceiver imageReceiver2) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
                }
            });
        }
        WebInstantView.loadPhoto(webPhoto, this.imageReceiver, new Runnable() {
            @Override
            public final void run() {
                TextPaintImageReceiverSpan.lambda$new$2();
            }
        });
        this.alignTop = z;
    }

    public static void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (imageReceiver.canInvertBitmap()) {
            imageReceiver.setColorFilter(new ColorMatrixColorFilter(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static void lambda$new$1(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (imageReceiver.canInvertBitmap()) {
            imageReceiver.setColorFilter(new ColorMatrixColorFilter(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static void lambda$new$2() {
    }

    @Override
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        canvas.save();
        if (this.alignTop) {
            this.imageReceiver.setImageCoords((int) f, i3 - 1, this.width, this.height);
        } else {
            int dp = (i5 - AndroidUtilities.dp(4.0f)) - i3;
            this.imageReceiver.setImageCoords((int) f, i3 + ((dp - r4) / 2), this.width, this.height);
        }
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt != null) {
            if (this.alignTop) {
                int dp = (fontMetricsInt.descent - fontMetricsInt.ascent) - AndroidUtilities.dp(4.0f);
                int i3 = this.height - dp;
                fontMetricsInt.descent = i3;
                fontMetricsInt.bottom = i3;
                int i4 = 0 - dp;
                fontMetricsInt.ascent = i4;
                fontMetricsInt.top = i4;
            } else {
                int dp2 = ((-this.height) / 2) - AndroidUtilities.dp(4.0f);
                fontMetricsInt.ascent = dp2;
                fontMetricsInt.top = dp2;
                int i5 = this.height;
                int dp3 = (i5 - (i5 / 2)) - AndroidUtilities.dp(4.0f);
                fontMetricsInt.descent = dp3;
                fontMetricsInt.bottom = dp3;
            }
        }
        return this.width;
    }
}
