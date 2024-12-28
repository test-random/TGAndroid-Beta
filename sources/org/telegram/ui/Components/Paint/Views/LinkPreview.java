package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Text;

public class LinkPreview extends View {
    private boolean animated;
    public int backgroundColor;
    private final RectF bounds;
    private final AnimatedFloat captionAbove;
    private int currentAccount;
    public final float density;
    private StaticLayout descriptionLayout;
    private float descriptionLayoutLeft;
    private float descriptionLayoutWidth;
    private final TextPaint descriptionPaint;
    private final float flagIconPadding;
    public float h;
    private boolean hasDescription;
    public boolean hasPhoto;
    private boolean hasSiteName;
    private boolean hasTitle;
    private final AnimatedFloat height;
    private final Drawable icon;
    private final float iconPadding;
    private final float iconSize;
    private StaticLayout layout;
    private float layoutLeft;
    private final TextPaint layoutPaint;
    private float layoutWidth;
    public int maxWidth;
    private boolean messageAbove;
    private Text messageText;
    private final Paint outlinePaint;
    private final RectF padding;
    public final int padx;
    public final int pady;
    private final Path path;
    private final Path path2;
    private final AnimatedFloat photoAlphaProgress;
    private float photoHeight;
    private final ImageReceiver photoImage;
    private final AnimatedFloat photoSmallProgress;
    private float previewHeight;
    private final AnimatedFloat previewHeightProgress;
    private Paint previewPaint;
    private final AnimatedFloat previewProgress;
    private final AnimatedFloat previewTheme;
    public int previewType;
    private final RectF rect;
    private final RectF rect1;
    private final RectF rect2;
    private boolean relayout;
    private Text siteNameText;
    private boolean smallPhoto;
    private float textScale;
    private Text titleText;
    public int type;
    private boolean video;
    public float w;
    private WebPagePreview webpage;
    private final AnimatedFloat width;

    public static class WebPagePreview extends TLObject {
        public boolean captionAbove = true;
        public int flags;
        public boolean largePhoto;
        public String name;
        public int photoSize;
        public String url;
        public TLRPC.WebPage webpage;

        public static WebPagePreview TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-625858389 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in WebPagePreview", Integer.valueOf(i)));
                }
                return null;
            }
            WebPagePreview webPagePreview = new WebPagePreview();
            webPagePreview.readParams(inputSerializedData, z);
            return webPagePreview;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.largePhoto = (readInt32 & 8) != 0;
            this.captionAbove = (readInt32 & 16) != 0;
            this.url = inputSerializedData.readString(z);
            if ((this.flags & 1) != 0) {
                this.webpage = TLRPC.WebPage.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 2) != 0) {
                this.name = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photoSize = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-625858389);
            this.flags = this.webpage != null ? this.flags | 1 : this.flags & (-2);
            int i = !TextUtils.isEmpty(this.name) ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            int i2 = this.largePhoto ? i | 8 : i & (-9);
            this.flags = i2;
            int i3 = this.captionAbove ? i2 | 16 : i2 & (-17);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeString(this.url);
            if ((this.flags & 1) != 0) {
                this.webpage.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.name);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeInt32(this.photoSize);
            }
        }
    }

    public LinkPreview(Context context, float f) {
        super(context);
        this.relayout = true;
        this.textScale = 1.0f;
        TextPaint textPaint = new TextPaint(1);
        this.layoutPaint = textPaint;
        this.padding = new RectF(4.0f, 4.33f, 7.66f, 3.0f);
        this.iconPadding = 3.25f;
        this.flagIconPadding = 2.25f;
        this.iconSize = 30.0f;
        this.outlinePaint = new Paint(1);
        this.previewPaint = new Paint(1);
        this.descriptionPaint = new TextPaint(1);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.photoImage = imageReceiver;
        this.bounds = new RectF();
        this.rect = new RectF();
        this.path = new Path();
        this.path2 = new Path();
        this.rect1 = new RectF();
        this.rect2 = new RectF();
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.captionAbove = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.photoAlphaProgress = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.photoSmallProgress = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.previewProgress = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.previewTheme = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.previewHeightProgress = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.width = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.height = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.density = f;
        imageReceiver.setInvalidateAll(true);
        this.padx = (int) (f * 3.0f);
        this.pady = (int) (f * 1.0f);
        this.icon = context.getResources().getDrawable(R.drawable.story_link).mutate();
        textPaint.setTextSize(24.0f * f);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
    }

    public static String fromUrl(String str) {
        return str;
    }

    public static String fromUrlWithoutSchema(String str) {
        return str.startsWith("https://") ? str.substring(8) : str;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawInternal(canvas);
    }

    public void drawInternal(Canvas canvas) {
        int i;
        Text text;
        Text text2;
        setupLayout();
        float f = this.width.set(this.w);
        float f2 = this.height.set(this.h);
        float f3 = this.previewTheme.set(this.previewType == 0);
        float f4 = this.previewProgress.set(withPreview());
        float lerp = AndroidUtilities.lerp(0.2f * f2, this.density * 16.66f, f4);
        RectF rectF = this.bounds;
        float f5 = this.padx;
        float f6 = this.pady;
        rectF.set(f5, f6, f5 + f, f6 + f2);
        this.outlinePaint.setColor(ColorUtils.blendARGB(this.backgroundColor, ColorUtils.blendARGB(-1, -14670807, f3), f4));
        this.path2.rewind();
        Path path = this.path2;
        RectF rectF2 = this.bounds;
        Path.Direction direction = Path.Direction.CW;
        path.addRoundRect(rectF2, lerp, lerp, direction);
        canvas.drawPath(this.path2, this.outlinePaint);
        if (f4 > 0.0f) {
            canvas.save();
            canvas.clipPath(this.path2);
            canvas.translate(this.padx, this.pady);
            float f7 = this.captionAbove.set(this.messageAbove);
            float f8 = this.density;
            float f9 = (7.33f * f8) + 0.0f;
            Text text3 = this.messageText;
            if (text3 != null && f7 > 0.0f) {
                text3.draw(canvas, f8 * 10.0f, (f9 + (text3.getHeight() / 2.0f)) - ((this.messageText.getHeight() + (this.density * 15.0f)) * (1.0f - f7)), -15033089, f4);
                f9 += (this.messageText.getHeight() + (this.density * 7.0f)) * f7;
            }
            float f10 = f9;
            float f11 = this.previewHeightProgress.set(this.previewHeight);
            this.previewPaint.setAlpha(25);
            RectF rectF3 = this.rect;
            float f12 = this.density * 10.0f;
            float f13 = f10 + f11;
            rectF3.set(f12, f10, f - f12, f13);
            this.path.rewind();
            Path path2 = this.path;
            RectF rectF4 = this.rect;
            float f14 = this.density * 5.0f;
            path2.addRoundRect(rectF4, f14, f14, direction);
            canvas.drawPath(this.path, this.previewPaint);
            canvas.save();
            canvas.clipPath(this.path);
            this.previewPaint.setAlpha(255);
            float f15 = this.density;
            canvas.drawRect(f15 * 10.0f, f10, f15 * 13.0f, f13, this.previewPaint);
            canvas.restore();
            float f16 = this.density;
            float f17 = f10 + (5.66f * f16);
            if (this.hasSiteName && (text2 = this.siteNameText) != null) {
                text2.draw(canvas, f16 * 20.0f, f17 + (text2.getHeight() / 2.0f), this.previewPaint.getColor(), f4);
                f17 += this.siteNameText.getHeight() + (this.density * 2.66f);
            }
            if (!this.hasTitle || (text = this.titleText) == null) {
                i = -13421773;
            } else {
                i = -13421773;
                text.draw(canvas, this.density * 20.0f, f17 + (text.getHeight() / 2.0f), ColorUtils.blendARGB(-13421773, -1, f3), f4);
                f17 += this.titleText.getHeight() + (this.density * 2.66f);
            }
            if (this.hasDescription && this.descriptionLayout != null) {
                canvas.save();
                canvas.translate((this.density * 20.0f) - this.descriptionLayoutLeft, f17);
                this.descriptionPaint.setColor(ColorUtils.blendARGB(i, -1, f3));
                this.descriptionPaint.setAlpha((int) (f4 * 255.0f));
                this.descriptionLayout.draw(canvas);
                canvas.restore();
                f17 += this.descriptionLayout.getHeight() + (this.density * 2.66f);
            }
            float f18 = this.photoAlphaProgress.set(this.hasPhoto);
            if (f18 > 0.0f) {
                float f19 = this.photoSmallProgress.set(this.smallPhoto);
                RectF rectF5 = this.rect1;
                float f20 = this.density;
                float f21 = f20 * 20.0f;
                float f22 = (f20 * 2.66f) + f17;
                rectF5.set(f21, f22, f - f21, this.photoHeight + f22);
                RectF rectF6 = this.rect2;
                float f23 = this.density;
                float f24 = 6.0f * f23;
                float f25 = (f - (f23 * 10.0f)) - f24;
                float f26 = f23 * 48.0f;
                float f27 = f10 + f24;
                rectF6.set(f25 - f26, f27, f25, f26 + f27);
                AndroidUtilities.lerp(this.rect1, this.rect2, f19, this.rect);
                ImageReceiver imageReceiver = this.photoImage;
                RectF rectF7 = this.rect;
                imageReceiver.setImageCoords(rectF7.left, rectF7.top, rectF7.width(), this.rect.height());
                this.photoImage.setAlpha(f18 * f4);
                this.photoImage.draw(canvas);
                f17 += (1.0f - f19) * ((this.density * 2.66f) + this.photoHeight);
            }
            float f28 = this.density;
            float f29 = f17 + (7.0f * f28) + (5.0f * f28);
            Text text4 = this.messageText;
            if (text4 != null && 1.0f - f7 > 0.0f) {
                text4.draw(canvas, f28 * 10.0f, f29 + (text4.getHeight() / 2.0f) + ((this.messageText.getHeight() + (this.density * 15.0f)) * f7), -15033089, f4);
                this.messageText.getHeight();
            }
            canvas.restore();
        }
        if (f4 < 1.0f) {
            Drawable drawable = this.icon;
            int i2 = this.padx;
            float f30 = this.padding.left;
            float f31 = this.density;
            int i3 = this.pady;
            float f32 = f31 * 30.0f;
            drawable.setBounds(((int) (f30 * f31)) + i2, ((int) ((f2 - f32) / 2.0f)) + i3, i2 + ((int) ((f30 + 30.0f) * f31)), i3 + ((int) ((f32 + f2) / 2.0f)));
            int i4 = (int) ((1.0f - f4) * 255.0f);
            this.icon.setAlpha(i4);
            this.icon.draw(canvas);
            if (this.layout != null) {
                canvas.save();
                canvas.translate(this.padx + ((this.padding.left + 30.0f + 3.25f) * this.density), this.pady + (f2 / 2.0f));
                float f33 = this.textScale;
                canvas.scale(f33, f33);
                canvas.translate(-this.layoutLeft, (-this.layout.getHeight()) / 2.0f);
                this.layoutPaint.setAlpha(i4);
                this.layout.draw(canvas);
                canvas.restore();
            }
        }
    }

    public int getPhotoSide() {
        float f;
        if (this.smallPhoto) {
            f = 48.0f;
        } else {
            int i = this.maxWidth;
            int i2 = this.padx;
            f = (((i - i2) - i2) / this.density) - 40.0f;
        }
        return ((int) f) * 2;
    }

    public int getPreviewType() {
        return this.previewType;
    }

    public float getRadius() {
        float f;
        float f2;
        if (withPreview()) {
            f = this.density;
            f2 = 16.66f;
        } else {
            f = this.h;
            f2 = 0.2f;
        }
        return f * f2;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.photoImage.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.photoImage.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setupLayout();
        setMeasuredDimension(this.padx + ((int) Math.ceil(this.w)) + this.padx, this.pady + ((int) Math.ceil(this.h)) + this.pady);
    }

    public void pushPhotoToCache() {
        if (this.hasPhoto && this.photoImage.hasImageLoaded() && this.photoImage.getBitmap() != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(this.photoImage.getBitmap()), this.photoImage.getImageKey(), false);
        }
    }

    public void set(int i, WebPagePreview webPagePreview) {
        set(i, webPagePreview, false);
    }

    public void set(int i, WebPagePreview webPagePreview, boolean z) {
        this.currentAccount = i;
        if (this.webpage != webPagePreview || z) {
            this.webpage = webPagePreview;
            this.relayout = true;
            this.animated = z;
            requestLayout();
        }
    }

    public void setMaxWidth(int i) {
        this.maxWidth = i;
        this.relayout = true;
    }

    public void setPreviewType(int i) {
        this.previewType = i;
        invalidate();
    }

    public void setType(int i, int i2) {
        Drawable drawable;
        PorterDuffColorFilter porterDuffColorFilter;
        if (this.type == 1) {
            return;
        }
        if (i == 0) {
            this.backgroundColor = i2;
            int i3 = AndroidUtilities.computePerceivedBrightness(i2) < 0.721f ? -1 : -16777216;
            this.layoutPaint.setColor(i3);
            drawable = this.icon;
            porterDuffColorFilter = new PorterDuffColorFilter(i3, PorterDuff.Mode.SRC_IN);
        } else if (i == 1) {
            this.backgroundColor = -16777216;
            this.layoutPaint.setColor(-1);
            drawable = this.icon;
            porterDuffColorFilter = new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN);
        } else {
            if (i != 2) {
                this.backgroundColor = -1;
                this.layoutPaint.setColor(-13397548);
                this.icon.setColorFilter(new PorterDuffColorFilter(-13397548, PorterDuff.Mode.SRC_IN));
                invalidate();
            }
            this.backgroundColor = 1275068416;
            this.layoutPaint.setColor(-1);
            drawable = this.icon;
            porterDuffColorFilter = null;
        }
        drawable.setColorFilter(porterDuffColorFilter);
        invalidate();
    }

    public void setVideoTexture() {
        this.video = true;
    }

    public void setupLayout() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.LinkPreview.setupLayout():void");
    }

    public boolean withPreview() {
        WebPagePreview webPagePreview = this.webpage;
        return (webPagePreview == null || webPagePreview.webpage == null) ? false : true;
    }
}
