package org.telegram.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.MotionBackgroundDrawable;

public class ChatBackgroundDrawable extends Drawable {
    private boolean attached;
    private boolean colorFilterSetted;
    float dimAmount;
    boolean isPattern;
    MotionBackgroundDrawable motionBackgroundDrawable;
    View parent;
    private final boolean themeIsDark;
    final TLRPC.WallPaper wallpaper;
    int alpha = 255;
    ImageReceiver imageReceiver = new ImageReceiver() {
        @Override
        public void invalidate() {
            View view = ChatBackgroundDrawable.this.parent;
            if (view != null) {
                view.invalidate();
            }
        }
    };
    private final ArrayList attachedViews = new ArrayList();

    public ChatBackgroundDrawable(final TLRPC.WallPaper wallPaper, boolean z, boolean z2) {
        TLRPC.WallPaperSettings wallPaperSettings;
        String str;
        ImageReceiver imageReceiver;
        ImageLocation forDocument;
        TLRPC.WallPaperSettings wallPaperSettings2;
        this.imageReceiver.setInvalidateAll(true);
        boolean z3 = wallPaper.pattern;
        this.isPattern = z3;
        this.wallpaper = wallPaper;
        this.themeIsDark = z;
        if (z && ((wallPaper.document != null || wallPaper.uploadingImage != null) && !z3 && (wallPaperSettings2 = wallPaper.settings) != null)) {
            this.dimAmount = wallPaperSettings2.intensity / 100.0f;
        }
        if ((z3 || wallPaper.document == null) && (wallPaperSettings = wallPaper.settings) != null && wallPaperSettings.second_background_color != 0 && wallPaperSettings.third_background_color != 0) {
            MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
            this.motionBackgroundDrawable = motionBackgroundDrawable;
            TLRPC.WallPaperSettings wallPaperSettings3 = wallPaper.settings;
            motionBackgroundDrawable.setColors(wallPaperSettings3.background_color, wallPaperSettings3.second_background_color, wallPaperSettings3.third_background_color, wallPaperSettings3.fourth_background_color);
            EmojiThemes.loadWallpaperImage(UserConfig.selectedAccount, wallPaper.id, wallPaper, new ResultCallback() {
                @Override
                public final void onComplete(Object obj) {
                    ChatBackgroundDrawable.this.lambda$new$0(wallPaper, (Pair) obj);
                }

                @Override
                public void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                @Override
                public void onError(TLRPC.TL_error tL_error) {
                    ResultCallback.CC.$default$onError(this, tL_error);
                }
            });
            return;
        }
        Point point = AndroidUtilities.displaySize;
        int min = Math.min(point.x, point.y);
        Point point2 = AndroidUtilities.displaySize;
        int max = Math.max(point2.x, point2.y);
        if (z2) {
            str = "150_150_wallpaper";
        } else {
            str = ((int) (min / AndroidUtilities.density)) + "_" + ((int) (max / AndroidUtilities.density)) + "_wallpaper";
        }
        String str2 = (str + wallPaper.id) + hash(wallPaper.settings);
        Drawable createThumb = createThumb(wallPaper);
        String str3 = wallPaper.uploadingImage;
        if (str3 != null) {
            imageReceiver = this.imageReceiver;
            forDocument = ImageLocation.getForPath(str3);
        } else {
            TLRPC.Document document = wallPaper.document;
            if (document == null) {
                this.imageReceiver.setImageBitmap(createThumb);
                return;
            } else {
                imageReceiver = this.imageReceiver;
                forDocument = ImageLocation.getForDocument(document);
            }
        }
        imageReceiver.setImage(forDocument, str2, createThumb, null, wallPaper, 1);
    }

    private static Drawable bitmapDrawableOf(Drawable drawable) {
        Bitmap createBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, 20, 20);
        drawable.draw(canvas);
        return new BitmapDrawable(createBitmap);
    }

    public static Drawable createThumb(TLRPC.WallPaper wallPaper) {
        ColorDrawable colorDrawable;
        Drawable bitmapDrawableOf;
        Drawable drawable = wallPaper.thumbDrawable;
        if (drawable != null) {
            return drawable;
        }
        if (wallPaper.stripedThumb != null) {
            return new BitmapDrawable(wallPaper.stripedThumb);
        }
        if (wallPaper.pattern && wallPaper.settings == null) {
            return new ColorDrawable(-16777216);
        }
        if (wallPaper.document != null) {
            bitmapDrawableOf = null;
            while (r2 < wallPaper.document.thumbs.size()) {
                if (wallPaper.document.thumbs.get(r2) instanceof TLRPC.TL_photoStrippedSize) {
                    bitmapDrawableOf = new BitmapDrawable(ImageLoader.getStrippedPhotoBitmap(wallPaper.document.thumbs.get(r2).bytes, "b"));
                }
                r2++;
            }
        } else {
            TLRPC.WallPaperSettings wallPaperSettings = wallPaper.settings;
            if (wallPaperSettings == null || wallPaperSettings.intensity < 0) {
                colorDrawable = new ColorDrawable(-16777216);
            } else if (wallPaperSettings.second_background_color == 0) {
                colorDrawable = new ColorDrawable(ColorUtils.setAlphaComponent(wallPaper.settings.background_color, 255));
            } else {
                int i = wallPaperSettings.third_background_color;
                int alphaComponent = ColorUtils.setAlphaComponent(wallPaperSettings.background_color, 255);
                if (i == 0) {
                    bitmapDrawableOf = bitmapDrawableOf(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(wallPaper.settings.rotation), new int[]{alphaComponent, ColorUtils.setAlphaComponent(wallPaper.settings.second_background_color, 255)}));
                } else {
                    int alphaComponent2 = ColorUtils.setAlphaComponent(wallPaper.settings.second_background_color, 255);
                    int alphaComponent3 = ColorUtils.setAlphaComponent(wallPaper.settings.third_background_color, 255);
                    int i2 = wallPaper.settings.fourth_background_color;
                    r2 = i2 != 0 ? ColorUtils.setAlphaComponent(i2, 255) : 0;
                    MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
                    motionBackgroundDrawable.setColors(alphaComponent, alphaComponent2, alphaComponent3, r2);
                    bitmapDrawableOf = new BitmapDrawable(motionBackgroundDrawable.getBitmap());
                }
            }
            bitmapDrawableOf = bitmapDrawableOf(colorDrawable);
        }
        wallPaper.thumbDrawable = bitmapDrawableOf;
        return bitmapDrawableOf;
    }

    public static Drawable getOrCreate(Drawable drawable, TLRPC.WallPaper wallPaper, boolean z) {
        TLRPC.WallPaperSettings wallPaperSettings;
        TLRPC.WallPaperSettings wallPaperSettings2;
        if (drawable instanceof ChatBackgroundDrawable) {
            ChatBackgroundDrawable chatBackgroundDrawable = (ChatBackgroundDrawable) drawable;
            String str = wallPaper.uploadingImage;
            if (str != null) {
                if (str.equals(chatBackgroundDrawable.wallpaper.uploadingImage) && ((wallPaperSettings2 = wallPaper.settings) == null || chatBackgroundDrawable.wallpaper.settings == null || wallPaperSettings2.intensity <= 0 || chatBackgroundDrawable.themeIsDark == z)) {
                    return chatBackgroundDrawable;
                }
            } else if (wallPaper.id == chatBackgroundDrawable.wallpaper.id && TextUtils.equals(hash(wallPaper.settings), hash(chatBackgroundDrawable.wallpaper.settings)) && (wallPaper.document == null || wallPaper.pattern || (wallPaperSettings = wallPaper.settings) == null || wallPaperSettings.intensity <= 0 || chatBackgroundDrawable.themeIsDark == z)) {
                return chatBackgroundDrawable;
            }
        }
        return new ChatBackgroundDrawable(wallPaper, z, false);
    }

    public static String hash(TLRPC.WallPaperSettings wallPaperSettings) {
        return wallPaperSettings == null ? "" : String.valueOf(Objects.hash(Boolean.valueOf(wallPaperSettings.blur), Boolean.valueOf(wallPaperSettings.motion), Integer.valueOf(wallPaperSettings.intensity), Integer.valueOf(wallPaperSettings.background_color), Integer.valueOf(wallPaperSettings.second_background_color), Integer.valueOf(wallPaperSettings.third_background_color), Integer.valueOf(wallPaperSettings.fourth_background_color)));
    }

    private boolean isAttached() {
        return this.attachedViews.size() > 0;
    }

    public void lambda$new$0(TLRPC.WallPaper wallPaper, Pair pair) {
        this.motionBackgroundDrawable.setPatternBitmap(wallPaper.settings.intensity, (Bitmap) pair.second);
        View view = this.parent;
        if (view != null) {
            view.invalidate();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        MotionBackgroundDrawable motionBackgroundDrawable = this.motionBackgroundDrawable;
        if (motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setBounds(getBounds());
            this.motionBackgroundDrawable.setAlpha(this.alpha);
            this.motionBackgroundDrawable.draw(canvas);
            return;
        }
        boolean z = true;
        if (this.imageReceiver.hasImageLoaded() && this.imageReceiver.getCurrentAlpha() == 1.0f) {
            if (!this.colorFilterSetted) {
                this.colorFilterSetted = true;
                this.imageReceiver.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(-16777216, (int) (this.dimAmount * 255.0f)), PorterDuff.Mode.DARKEN));
            }
            z = false;
        }
        this.imageReceiver.setImageCoords(getBounds());
        this.imageReceiver.setAlpha(this.alpha / 255.0f);
        this.imageReceiver.draw(canvas);
        if (z) {
            float f = this.dimAmount;
            if (f != 0.0f) {
                canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (f * 255.0f)));
            }
        }
    }

    public float getDimAmount() {
        if (this.motionBackgroundDrawable == null) {
            return this.dimAmount;
        }
        return 0.0f;
    }

    public Drawable getDrawable(boolean z) {
        MotionBackgroundDrawable motionBackgroundDrawable = this.motionBackgroundDrawable;
        return motionBackgroundDrawable != null ? motionBackgroundDrawable : (!z || this.imageReceiver.getStaticThumb() == null) ? this.imageReceiver.getThumb() != null ? this.imageReceiver.getThumb() : this.imageReceiver.getDrawable() != null ? this.imageReceiver.getDrawable() : this.imageReceiver.getStaticThumb() : this.imageReceiver.getStaticThumb();
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void onAttachedToWindow(View view) {
        if (!this.attachedViews.contains(view)) {
            this.attachedViews.add(view);
        }
        if (isAttached() && !this.attached) {
            this.attached = true;
            this.imageReceiver.onAttachedToWindow();
        } else {
            if (isAttached() || !this.attached) {
                return;
            }
            this.attached = false;
            this.imageReceiver.onDetachedFromWindow();
        }
    }

    public void onDetachedFromWindow(View view) {
        if (!this.attachedViews.contains(view)) {
            this.attachedViews.remove(view);
        }
        if (isAttached() && !this.attached) {
            this.attached = true;
            this.imageReceiver.onAttachedToWindow();
        } else {
            if (isAttached() || !this.attached) {
                return;
            }
            this.attached = false;
            this.imageReceiver.onDetachedFromWindow();
        }
    }

    @Override
    public void setAlpha(int i) {
        if (this.alpha != i) {
            this.alpha = i;
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setParent(View view) {
        this.parent = view;
        MotionBackgroundDrawable motionBackgroundDrawable = this.motionBackgroundDrawable;
        if (motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setParentView(view);
        }
    }
}
