package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.WallpapersListActivity;

public abstract class WallpaperCell extends FrameLayout {
    private Paint backgroundPaint;
    private Drawable checkDrawable;
    private Paint circlePaint;
    private int currentType;
    public boolean drawStubBackground;
    private Paint framePaint;
    private boolean isBottom;
    private boolean isTop;
    int size;
    private int spanCount;
    private WallpaperView[] wallpaperViews;

    public class WallpaperView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox checkBox;
        private Object currentWallpaper;
        private BackupImageView imageView;
        private ImageView imageView2;
        private boolean isSelected;
        private View selector;

        public WallpaperView(Context context) {
            super(context);
            setWillNotDraw(false);
            BackupImageView backupImageView = new BackupImageView(context) {
                @Override
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if ((WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || (WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.FileWallpaper)) {
                        canvas.drawLine(1.0f, 0.0f, getMeasuredWidth() - 1, 0.0f, WallpaperCell.this.framePaint);
                        canvas.drawLine(0.0f, 0.0f, 0.0f, getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(getMeasuredWidth() - 1, 0.0f, getMeasuredWidth() - 1, getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(1.0f, getMeasuredHeight() - 1, getMeasuredWidth() - 1, getMeasuredHeight() - 1, WallpaperCell.this.framePaint);
                    }
                    if (WallpaperView.this.isSelected) {
                        WallpaperCell.this.circlePaint.setColor(Theme.serviceMessageColorBackup);
                        int measuredWidth = getMeasuredWidth() / 2;
                        int measuredHeight = getMeasuredHeight() / 2;
                        canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(20.0f), WallpaperCell.this.circlePaint);
                        WallpaperCell.this.checkDrawable.setBounds(measuredWidth - (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), measuredHeight - (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2), measuredWidth + (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), measuredHeight + (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2));
                        WallpaperCell.this.checkDrawable.draw(canvas);
                    }
                }
            };
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(-1, -1, 51));
            ImageView imageView = new ImageView(context);
            this.imageView2 = imageView;
            imageView.setImageResource(R.drawable.ic_gallery_background);
            this.imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView2, LayoutHelper.createFrame(-1, -1, 51));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            CheckBox checkBox = new CheckBox(context, R.drawable.round_check2);
            this.checkBox = checkBox;
            checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        @Override
        public void clearAnimation() {
            super.clearAnimation();
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();
            this.imageView.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!(WallpaperCell.this.drawStubBackground && this.checkBox.isChecked()) && this.imageView.getImageReceiver().hasBitmapImage() && this.imageView.getImageReceiver().getCurrentAlpha() == 1.0f) {
                return;
            }
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), WallpaperCell.this.backgroundPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setChecked(final boolean z, boolean z2) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.imageView, "scaleX", z ? 0.8875f : 1.0f), ObjectAnimator.ofFloat(this.imageView, "scaleY", z ? 0.8875f : 1.0f));
                this.animator.setDuration(200L);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animator) {
                        if (WallpaperView.this.animator == null || !WallpaperView.this.animator.equals(animator)) {
                            return;
                        }
                        WallpaperView.this.animator = null;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (WallpaperView.this.animator == null || !WallpaperView.this.animator.equals(animator)) {
                            return;
                        }
                        WallpaperView.this.animator = null;
                        if (z) {
                            return;
                        }
                        WallpaperView.this.setBackgroundColor(0);
                    }
                });
                this.animator.start();
            } else {
                this.imageView.setScaleX(z ? 0.8875f : 1.0f);
                this.imageView.setScaleY(z ? 0.8875f : 1.0f);
            }
            invalidate();
        }

        public void setWallpaper(Object obj, Object obj2, Drawable drawable, boolean z) {
            TLRPC.PhotoSize photoSize;
            BackupImageView backupImageView;
            ImageLocation forPhoto;
            ImageLocation forPhoto2;
            long j;
            File file;
            int patternColor;
            ImageReceiver imageReceiver;
            float abs;
            BlendMode blendMode;
            MediaController.SearchImage searchImage;
            int patternColor2;
            ImageLocation forDocument;
            String str;
            int i;
            ImageLocation imageLocation;
            BlendMode blendMode2;
            this.currentWallpaper = obj;
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            this.imageView.setBackgroundDrawable(null);
            this.imageView.getImageReceiver().setColorFilter(null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            this.imageView.getImageReceiver().setBlendMode(null);
            this.imageView.getImageReceiver().setGradientBitmap(null);
            this.isSelected = obj == obj2;
            String str2 = "180_180";
            String str3 = "100_100_b";
            if (obj instanceof TLRPC.TL_wallPaper) {
                ?? r1 = (TLRPC.TL_wallPaper) obj;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(r1.document.thumbs, AndroidUtilities.dp(100));
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(r1.document.thumbs, AndroidUtilities.dp(180));
                photoSize = closestPhotoSizeWithSize2 != closestPhotoSizeWithSize ? closestPhotoSizeWithSize2 : null;
                j = photoSize != null ? photoSize.size : r1.document.size;
                if (!r1.pattern) {
                    backupImageView = this.imageView;
                    TLRPC.Document document = r1.document;
                    forPhoto = photoSize != null ? ImageLocation.getForDocument(photoSize, document) : ImageLocation.getForDocument(document);
                    forPhoto2 = ImageLocation.getForDocument(closestPhotoSizeWithSize, r1.document);
                    searchImage = r1;
                    backupImageView.setImage(forPhoto, str2, forPhoto2, str3, "jpg", j, 1, searchImage);
                    return;
                }
                TLRPC.WallPaperSettings wallPaperSettings = r1.settings;
                if (wallPaperSettings.third_background_color != 0) {
                    TLRPC.WallPaperSettings wallPaperSettings2 = r1.settings;
                    MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(wallPaperSettings2.background_color, wallPaperSettings2.second_background_color, wallPaperSettings2.third_background_color, wallPaperSettings2.fourth_background_color, true);
                    if (r1.settings.intensity >= 0 || !Theme.getActiveTheme().isDark()) {
                        this.imageView.setBackground(motionBackgroundDrawable);
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageReceiver imageReceiver2 = this.imageView.getImageReceiver();
                            blendMode2 = BlendMode.SOFT_LIGHT;
                            imageReceiver2.setBlendMode(blendMode2);
                        }
                    } else {
                        this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                    }
                    TLRPC.WallPaperSettings wallPaperSettings3 = r1.settings;
                    patternColor2 = MotionBackgroundDrawable.getPatternColor(wallPaperSettings3.background_color, wallPaperSettings3.second_background_color, wallPaperSettings3.third_background_color, wallPaperSettings3.fourth_background_color);
                } else {
                    this.imageView.setBackgroundColor(Theme.getWallpaperColor(wallPaperSettings.background_color));
                    patternColor2 = AndroidUtilities.getPatternColor(r1.settings.background_color);
                }
                if (Build.VERSION.SDK_INT < 29 || r1.settings.third_background_color == 0) {
                    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor2), PorterDuff.Mode.SRC_IN));
                }
                ?? r8 = this.imageView;
                TLRPC.Document document2 = r1.document;
                if (photoSize != null) {
                    forDocument = ImageLocation.getForDocument(photoSize, document2);
                    imageLocation = ImageLocation.getForDocument(closestPhotoSizeWithSize, r1.document);
                    str = "jpg";
                    i = 1;
                } else {
                    forDocument = ImageLocation.getForDocument(closestPhotoSizeWithSize, document2);
                    str = "jpg";
                    i = 1;
                    imageLocation = null;
                }
                r8.setImage(forDocument, str2, imageLocation, null, str, j, i, r1);
                imageReceiver = this.imageView.getImageReceiver();
                abs = Math.abs(r1.settings.intensity) / 100.0f;
                imageReceiver.setAlpha(abs);
                return;
            }
            if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                if (colorWallpaper.path == null && colorWallpaper.pattern == null && !"d".equals(colorWallpaper.slug)) {
                    this.imageView.setImageBitmap(null);
                    if (colorWallpaper.isGradient) {
                        this.imageView.setBackground(new MotionBackgroundDrawable(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3, true));
                        return;
                    } else if (colorWallpaper.gradientColor1 != 0) {
                        this.imageView.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{colorWallpaper.color | (-16777216), colorWallpaper.gradientColor1 | (-16777216)}));
                        return;
                    } else {
                        this.imageView.setBackgroundColor(colorWallpaper.color | (-16777216));
                        return;
                    }
                }
                if (colorWallpaper.gradientColor2 != 0) {
                    MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3, true);
                    if (colorWallpaper.intensity >= 0.0f) {
                        this.imageView.setBackground(new MotionBackgroundDrawable(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3, true));
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageReceiver imageReceiver3 = this.imageView.getImageReceiver();
                            blendMode = BlendMode.SOFT_LIGHT;
                            imageReceiver3.setBlendMode(blendMode);
                        }
                    } else {
                        this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable2.getBitmap());
                    }
                    patternColor = MotionBackgroundDrawable.getPatternColor(colorWallpaper.color, colorWallpaper.gradientColor1, colorWallpaper.gradientColor2, colorWallpaper.gradientColor3);
                } else {
                    patternColor = AndroidUtilities.getPatternColor(colorWallpaper.color);
                }
                if ("d".equals(colorWallpaper.slug)) {
                    if (colorWallpaper.defaultCache == null) {
                        colorWallpaper.defaultCache = SvgHelper.getBitmap(R.raw.default_pattern, 100, 180, -16777216);
                    }
                    this.imageView.setImageBitmap(colorWallpaper.defaultCache);
                    imageReceiver = this.imageView.getImageReceiver();
                    abs = Math.abs(colorWallpaper.intensity);
                    imageReceiver.setAlpha(abs);
                    return;
                }
                file = colorWallpaper.path;
                if (file == null) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(colorWallpaper.pattern.document.thumbs, 100);
                    this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize3, colorWallpaper.pattern.document), str2, null, null, "jpg", closestPhotoSizeWithSize3 != null ? closestPhotoSizeWithSize3.size : colorWallpaper.pattern.document.size, 1, colorWallpaper.pattern);
                    this.imageView.getImageReceiver().setAlpha(Math.abs(colorWallpaper.intensity));
                    if (Build.VERSION.SDK_INT < 29 || colorWallpaper.gradientColor2 == 0) {
                        this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor), PorterDuff.Mode.SRC_IN));
                        return;
                    }
                    return;
                }
            } else {
                if (!(obj instanceof WallpapersListActivity.FileWallpaper)) {
                    if (!(obj instanceof MediaController.SearchImage)) {
                        this.isSelected = false;
                        return;
                    }
                    MediaController.SearchImage searchImage2 = (MediaController.SearchImage) obj;
                    TLRPC.Photo photo = searchImage2.photo;
                    if (photo == null) {
                        this.imageView.setImage(searchImage2.thumbUrl, str2, null);
                        return;
                    }
                    TLRPC.PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(100));
                    TLRPC.PhotoSize closestPhotoSizeWithSize5 = FileLoader.getClosestPhotoSizeWithSize(searchImage2.photo.sizes, AndroidUtilities.dp(180));
                    photoSize = closestPhotoSizeWithSize5 != closestPhotoSizeWithSize4 ? closestPhotoSizeWithSize5 : null;
                    int i2 = photoSize != null ? photoSize.size : 0;
                    backupImageView = this.imageView;
                    forPhoto = ImageLocation.getForPhoto(photoSize, searchImage2.photo);
                    forPhoto2 = ImageLocation.getForPhoto(closestPhotoSizeWithSize4, searchImage2.photo);
                    j = i2;
                    searchImage = searchImage2;
                    backupImageView.setImage(forPhoto, str2, forPhoto2, str3, "jpg", j, 1, searchImage);
                    return;
                }
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                file = fileWallpaper.originalPath;
                if (file == null && (file = fileWallpaper.path) == null) {
                    if (!"t".equals(fileWallpaper.slug)) {
                        this.imageView.setImageResource(fileWallpaper.thumbResId);
                        return;
                    } else {
                        BackupImageView backupImageView2 = this.imageView;
                        backupImageView2.setImageDrawable(Theme.getThemedWallpaper(true, backupImageView2));
                        return;
                    }
                }
            }
            this.imageView.setImage(file.getAbsolutePath(), str2, null);
        }
    }

    public WallpaperCell(Context context) {
        this(context, 5);
    }

    public WallpaperCell(Context context, int i) {
        super(context);
        this.drawStubBackground = true;
        this.spanCount = 3;
        this.wallpaperViews = new WallpaperView[i];
        final int i2 = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (i2 >= wallpaperViewArr.length) {
                Paint paint = new Paint();
                this.framePaint = paint;
                paint.setColor(855638016);
                this.circlePaint = new Paint(1);
                this.checkDrawable = context.getResources().getDrawable(R.drawable.background_selected).mutate();
                Paint paint2 = new Paint();
                this.backgroundPaint = paint2;
                paint2.setColor(Theme.getColor(Theme.key_sharedMedia_photoPlaceholder));
                return;
            }
            final WallpaperView wallpaperView = new WallpaperView(context);
            wallpaperViewArr[i2] = wallpaperView;
            addView(wallpaperView);
            wallpaperView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    WallpaperCell.this.lambda$new$0(wallpaperView, i2, view);
                }
            });
            wallpaperView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public final boolean onLongClick(View view) {
                    boolean lambda$new$1;
                    lambda$new$1 = WallpaperCell.this.lambda$new$1(wallpaperView, i2, view);
                    return lambda$new$1;
                }
            });
            i2++;
        }
    }

    public void lambda$new$0(WallpaperView wallpaperView, int i, View view) {
        onWallpaperClick(wallpaperView.currentWallpaper, i);
    }

    public boolean lambda$new$1(WallpaperView wallpaperView, int i, View view) {
        return onWallpaperLongClick(wallpaperView.currentWallpaper, i);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (int i = 0; i < this.spanCount; i++) {
            this.wallpaperViews[i].invalidate();
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.spanCount == 1) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int dp = AndroidUtilities.dp(14.0f);
        int dp2 = this.isTop ? AndroidUtilities.dp(14.0f) : 0;
        for (int i5 = 0; i5 < this.spanCount; i5++) {
            int measuredWidth = this.wallpaperViews[i5].getMeasuredWidth();
            WallpaperView wallpaperView = this.wallpaperViews[i5];
            wallpaperView.layout(dp, dp2, dp + measuredWidth, wallpaperView.getMeasuredHeight() + dp2);
            dp += measuredWidth + AndroidUtilities.dp(6.0f);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3 = 0;
        if (this.spanCount == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.size, 1073741824), View.MeasureSpec.makeMeasureSpec(this.size + AndroidUtilities.dp(6.0f), 1073741824));
            setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            return;
        }
        int size = View.MeasureSpec.getSize(i);
        int dp = size - AndroidUtilities.dp(((this.spanCount - 1) * 6) + 28);
        int i4 = dp / this.spanCount;
        int i5 = this.currentType;
        int dp2 = (i5 == 0 || i5 == 2 || i5 == 3) ? AndroidUtilities.dp(180.0f) : i4;
        setMeasuredDimension(size, (this.isTop ? AndroidUtilities.dp(14.0f) : 0) + dp2 + AndroidUtilities.dp(this.isBottom ? 14.0f : 6.0f));
        while (true) {
            int i6 = this.spanCount;
            if (i3 >= i6) {
                return;
            }
            this.wallpaperViews[i3].measure(View.MeasureSpec.makeMeasureSpec(i3 == i6 + (-1) ? dp : i4, 1073741824), View.MeasureSpec.makeMeasureSpec(dp2, 1073741824));
            dp -= i4;
            i3++;
        }
    }

    protected abstract void onWallpaperClick(Object obj, int i);

    protected boolean onWallpaperLongClick(Object obj, int i) {
        return false;
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.wallpaperViews[i].setChecked(z, z2);
    }

    public void setParams(int i, boolean z, boolean z2) {
        this.spanCount = i;
        this.isTop = z;
        this.isBottom = z2;
        int i2 = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (i2 >= wallpaperViewArr.length) {
                return;
            }
            wallpaperViewArr[i2].setVisibility(i2 < i ? 0 : 8);
            this.wallpaperViews[i2].clearAnimation();
            i2++;
        }
    }

    public void setSize(int i) {
        if (this.size != i) {
            this.size = i;
            requestLayout();
        }
    }

    public void setWallpaper(int i, int i2, Object obj, Object obj2, Drawable drawable, boolean z) {
        this.currentType = i;
        WallpaperView[] wallpaperViewArr = this.wallpaperViews;
        if (obj == null) {
            wallpaperViewArr[i2].setVisibility(8);
            this.wallpaperViews[i2].clearAnimation();
        } else {
            wallpaperViewArr[i2].setVisibility(0);
            this.wallpaperViews[i2].setWallpaper(obj, obj2, drawable, z);
        }
    }
}
