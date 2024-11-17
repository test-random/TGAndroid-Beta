package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatBackgroundDrawable;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public class ThemeSmallPreviewView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final float BUBBLE_HEIGHT;
    private final float BUBBLE_WIDTH;
    private final float INNER_RADIUS;
    private final float INNER_RECT_SPACE;
    private final float STROKE_RADIUS;
    ThemeDrawable animateOutThemeDrawable;
    Runnable animationCancelRunnable;
    boolean attached;
    private final Paint backgroundFillPaint;
    private BackupImageView backupImageView;
    private float changeThemeProgress;
    ChatBackgroundDrawable chatBackgroundDrawable;
    public ChatThemeBottomSheet.ChatThemeItem chatThemeItem;
    private final Path clipPath;
    private final int currentAccount;
    private int currentType;
    public TLRPC.WallPaper fallbackWallpaper;
    private boolean hasAnimatedEmoji;
    boolean isSelected;
    public int lastThemeIndex;
    Theme.MessageDrawable messageDrawableIn;
    Theme.MessageDrawable messageDrawableOut;
    private TextPaint noThemeTextPaint;
    Paint outlineBackgroundPaint;
    int patternColor;
    private final RectF rectF;
    private final Theme.ResourcesProvider resourcesProvider;
    private float selectionProgress;
    private ValueAnimator strokeAlphaAnimator;
    private StaticLayout textLayout;
    ThemeDrawable themeDrawable;

    public class ThemeDrawable {
        private final Paint inBubblePaint;
        private final Paint outBubblePaintSecond;
        Drawable previewDrawable;
        private final Paint strokePaint;

        ThemeDrawable() {
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            this.outBubblePaintSecond = new Paint(1);
            this.inBubblePaint = new Paint(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }

        public void draw(Canvas canvas, float f) {
            ThemeSmallPreviewView themeSmallPreviewView = ThemeSmallPreviewView.this;
            if (themeSmallPreviewView.isSelected || themeSmallPreviewView.strokeAlphaAnimator != null) {
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = ThemeSmallPreviewView.this.chatThemeItem;
                this.strokePaint.setColor(ThemeSmallPreviewView.this.chatThemeItem.chatTheme.isAnyStub() ? ThemeSmallPreviewView.this.getThemedColor(Theme.key_featuredStickers_addButton) : chatThemeItem.chatTheme.getThemeItem(chatThemeItem.themeIndex).outLineColor);
                this.strokePaint.setAlpha((int) (ThemeSmallPreviewView.this.selectionProgress * f * 255.0f));
                float strokeWidth = (this.strokePaint.getStrokeWidth() * 0.5f) + (AndroidUtilities.dp(4.0f) * (1.0f - ThemeSmallPreviewView.this.selectionProgress));
                ThemeSmallPreviewView.this.rectF.set(strokeWidth, strokeWidth, ThemeSmallPreviewView.this.getWidth() - strokeWidth, ThemeSmallPreviewView.this.getHeight() - strokeWidth);
                canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.STROKE_RADIUS, ThemeSmallPreviewView.this.STROKE_RADIUS, this.strokePaint);
            }
            int i = (int) (f * 255.0f);
            this.outBubblePaintSecond.setAlpha(i);
            this.inBubblePaint.setAlpha(i);
            ThemeSmallPreviewView.this.rectF.set(ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.getWidth() - ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.getHeight() - ThemeSmallPreviewView.this.INNER_RECT_SPACE);
            EmojiThemes emojiThemes = ThemeSmallPreviewView.this.chatThemeItem.chatTheme;
            if (emojiThemes == null || (emojiThemes.isAnyStub() && ThemeSmallPreviewView.this.chatThemeItem.chatTheme.wallpaper == null)) {
                ThemeSmallPreviewView themeSmallPreviewView2 = ThemeSmallPreviewView.this;
                if (themeSmallPreviewView2.fallbackWallpaper == null) {
                    canvas.drawRoundRect(themeSmallPreviewView2.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
                    canvas.save();
                    StaticLayout noThemeStaticLayout = ThemeSmallPreviewView.this.getNoThemeStaticLayout();
                    canvas.translate((ThemeSmallPreviewView.this.getWidth() - noThemeStaticLayout.getWidth()) * 0.5f, AndroidUtilities.dp(18.0f));
                    noThemeStaticLayout.draw(canvas);
                    canvas.restore();
                    return;
                }
                return;
            }
            if (ThemeSmallPreviewView.this.currentType != 4) {
                if (ThemeSmallPreviewView.this.currentType == 2) {
                    if (ThemeSmallPreviewView.this.chatThemeItem.icon != null) {
                        canvas.drawBitmap(ThemeSmallPreviewView.this.chatThemeItem.icon, (r2.getWidth() - ThemeSmallPreviewView.this.chatThemeItem.icon.getWidth()) * 0.5f, AndroidUtilities.dp(21.0f), (Paint) null);
                        return;
                    }
                    return;
                }
                float dp = ThemeSmallPreviewView.this.INNER_RECT_SPACE + AndroidUtilities.dp(8.0f);
                float dp2 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + AndroidUtilities.dp(ThemeSmallPreviewView.this.currentType == 3 ? 5.0f : 22.0f);
                if (ThemeSmallPreviewView.this.currentType == 0 || ThemeSmallPreviewView.this.currentType == 3) {
                    ThemeSmallPreviewView.this.rectF.set(dp2, dp, (ThemeSmallPreviewView.this.BUBBLE_WIDTH * (ThemeSmallPreviewView.this.currentType == 3 ? 1.2f : 1.0f)) + dp2, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + dp);
                } else {
                    dp = ThemeSmallPreviewView.this.getMeasuredHeight() * 0.12f;
                    ThemeSmallPreviewView.this.rectF.set(ThemeSmallPreviewView.this.getMeasuredWidth() - (ThemeSmallPreviewView.this.getMeasuredWidth() * 0.65f), dp, ThemeSmallPreviewView.this.getMeasuredWidth() - (ThemeSmallPreviewView.this.getMeasuredWidth() * 0.1f), ThemeSmallPreviewView.this.getMeasuredHeight() * 0.32f);
                }
                Paint paint = ThemeSmallPreviewView.this.currentType == 3 ? this.inBubblePaint : this.outBubblePaintSecond;
                if (ThemeSmallPreviewView.this.currentType == 0 || ThemeSmallPreviewView.this.currentType == 3) {
                    canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, paint);
                } else {
                    ThemeSmallPreviewView themeSmallPreviewView3 = ThemeSmallPreviewView.this;
                    themeSmallPreviewView3.messageDrawableOut.setBounds((int) themeSmallPreviewView3.rectF.left, ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), ((int) ThemeSmallPreviewView.this.rectF.right) + AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                    ThemeSmallPreviewView themeSmallPreviewView4 = ThemeSmallPreviewView.this;
                    themeSmallPreviewView4.messageDrawableOut.setRoundRadius((int) (themeSmallPreviewView4.rectF.height() * 0.5f));
                    ThemeSmallPreviewView.this.messageDrawableOut.draw(canvas, paint);
                }
                if (ThemeSmallPreviewView.this.currentType == 0 || ThemeSmallPreviewView.this.currentType == 3) {
                    float dp3 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + AndroidUtilities.dp(5.0f);
                    float dp4 = dp + ThemeSmallPreviewView.this.BUBBLE_HEIGHT + AndroidUtilities.dp(4.0f);
                    ThemeSmallPreviewView.this.rectF.set(dp3, dp4, (ThemeSmallPreviewView.this.BUBBLE_WIDTH * (ThemeSmallPreviewView.this.currentType == 3 ? 0.8f : 1.0f)) + dp3, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + dp4);
                } else {
                    ThemeSmallPreviewView.this.rectF.set(ThemeSmallPreviewView.this.getMeasuredWidth() * 0.1f, ThemeSmallPreviewView.this.getMeasuredHeight() * 0.35f, ThemeSmallPreviewView.this.getMeasuredWidth() * 0.65f, ThemeSmallPreviewView.this.getMeasuredHeight() * 0.55f);
                }
                if (ThemeSmallPreviewView.this.currentType == 0 || ThemeSmallPreviewView.this.currentType == 3) {
                    canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, this.inBubblePaint);
                    return;
                }
                ThemeSmallPreviewView themeSmallPreviewView5 = ThemeSmallPreviewView.this;
                themeSmallPreviewView5.messageDrawableIn.setBounds(((int) themeSmallPreviewView5.rectF.left) - AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), (int) ThemeSmallPreviewView.this.rectF.right, ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                ThemeSmallPreviewView themeSmallPreviewView6 = ThemeSmallPreviewView.this;
                themeSmallPreviewView6.messageDrawableIn.setRoundRadius((int) (themeSmallPreviewView6.rectF.height() * 0.5f));
                ThemeSmallPreviewView.this.messageDrawableIn.draw(canvas, this.inBubblePaint);
            }
        }

        public void drawBackground(Canvas canvas, float f) {
            EmojiThemes emojiThemes;
            if (this.previewDrawable == null) {
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = ThemeSmallPreviewView.this.chatThemeItem;
                if (chatThemeItem == null || (emojiThemes = chatThemeItem.chatTheme) == null || !emojiThemes.isAnyStub() || ThemeSmallPreviewView.this.chatBackgroundDrawable == null) {
                    canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
                    return;
                }
                return;
            }
            canvas.save();
            canvas.clipPath(ThemeSmallPreviewView.this.clipPath);
            Drawable drawable = this.previewDrawable;
            if (drawable instanceof BitmapDrawable) {
                float intrinsicWidth = drawable.getIntrinsicWidth();
                float intrinsicHeight = this.previewDrawable.getIntrinsicHeight();
                if (intrinsicWidth / intrinsicHeight > ThemeSmallPreviewView.this.getWidth() / ThemeSmallPreviewView.this.getHeight()) {
                    int width = (int) ((ThemeSmallPreviewView.this.getWidth() * intrinsicHeight) / intrinsicWidth);
                    int width2 = (width - ThemeSmallPreviewView.this.getWidth()) / 2;
                    this.previewDrawable.setBounds(width2, 0, width + width2, ThemeSmallPreviewView.this.getHeight());
                } else {
                    int height = (int) ((ThemeSmallPreviewView.this.getHeight() * intrinsicHeight) / intrinsicWidth);
                    int height2 = (ThemeSmallPreviewView.this.getHeight() - height) / 2;
                    this.previewDrawable.setBounds(0, height2, ThemeSmallPreviewView.this.getWidth(), height + height2);
                }
            } else {
                drawable.setBounds(0, 0, ThemeSmallPreviewView.this.getWidth(), ThemeSmallPreviewView.this.getHeight());
            }
            this.previewDrawable.setAlpha((int) (255.0f * f));
            this.previewDrawable.draw(canvas);
            Drawable drawable2 = this.previewDrawable;
            if ((drawable2 instanceof ColorDrawable) || ((drawable2 instanceof MotionBackgroundDrawable) && ((MotionBackgroundDrawable) drawable2).isOneColor())) {
                int alpha = ThemeSmallPreviewView.this.outlineBackgroundPaint.getAlpha();
                ThemeSmallPreviewView.this.outlineBackgroundPaint.setAlpha((int) (alpha * f));
                float f2 = ThemeSmallPreviewView.this.INNER_RECT_SPACE;
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(f2, f2, ThemeSmallPreviewView.this.getWidth() - f2, ThemeSmallPreviewView.this.getHeight() - f2);
                canvas.drawRoundRect(rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.outlineBackgroundPaint);
                ThemeSmallPreviewView.this.outlineBackgroundPaint.setAlpha(alpha);
            }
            canvas.restore();
        }
    }

    public ThemeSmallPreviewView(Context context, int i, Theme.ResourcesProvider resourcesProvider, int i2) {
        super(context);
        BackupImageView backupImageView;
        float f;
        float f2;
        int i3;
        float f3;
        this.STROKE_RADIUS = AndroidUtilities.dp(8.0f);
        this.INNER_RADIUS = AndroidUtilities.dp(6.0f);
        this.INNER_RECT_SPACE = AndroidUtilities.dp(4.0f);
        this.BUBBLE_HEIGHT = AndroidUtilities.dp(21.0f);
        this.BUBBLE_WIDTH = AndroidUtilities.dp(41.0f);
        this.themeDrawable = new ThemeDrawable();
        this.changeThemeProgress = 1.0f;
        this.outlineBackgroundPaint = new Paint(1);
        this.backgroundFillPaint = new Paint(1);
        this.rectF = new RectF();
        this.clipPath = new Path();
        this.messageDrawableOut = new Theme.MessageDrawable(0, true, false);
        this.messageDrawableIn = new Theme.MessageDrawable(0, false, false);
        this.currentType = i2;
        this.currentAccount = i;
        this.resourcesProvider = resourcesProvider;
        setBackgroundColor(getThemedColor(Theme.key_dialogBackgroundGray));
        BackupImageView backupImageView2 = new BackupImageView(context);
        this.backupImageView = backupImageView2;
        backupImageView2.getImageReceiver().setCrossfadeWithOldImage(true);
        this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
        this.backupImageView.getImageReceiver().setAutoRepeat(0);
        if (i2 == 0 || i2 == 3 || i2 == 2) {
            backupImageView = this.backupImageView;
            f = 0.0f;
            f2 = 12.0f;
            i3 = 28;
            f3 = 28.0f;
        } else {
            backupImageView = this.backupImageView;
            f = 0.0f;
            f2 = 12.0f;
            i3 = 36;
            f3 = 36.0f;
        }
        addView(backupImageView, LayoutHelper.createFrame(i3, f3, 81, 0.0f, 0.0f, f, f2));
        this.outlineBackgroundPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outlineBackgroundPaint.setStyle(Paint.Style.STROKE);
        this.outlineBackgroundPaint.setColor(551805923);
    }

    private void fillOutBubblePaint(Paint paint, List list) {
        LinearGradient linearGradient;
        if (list.size() > 1) {
            int[] iArr = new int[list.size()];
            for (int i = 0; i != list.size(); i++) {
                iArr[i] = ((Integer) list.get(i)).intValue();
            }
            float dp = this.INNER_RECT_SPACE + AndroidUtilities.dp(8.0f);
            linearGradient = new LinearGradient(0.0f, dp, 0.0f, dp + this.BUBBLE_HEIGHT, iArr, (float[]) null, Shader.TileMode.CLAMP);
        } else {
            linearGradient = null;
        }
        paint.setShader(linearGradient);
    }

    public StaticLayout getNoThemeStaticLayout() {
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            return staticLayout;
        }
        TextPaint textPaint = new TextPaint(129);
        this.noThemeTextPaint = textPaint;
        textPaint.setColor(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription));
        this.noThemeTextPaint.setTextSize(AndroidUtilities.dp(noThemeStringTextSize()));
        this.noThemeTextPaint.setTypeface(AndroidUtilities.bold());
        int dp = AndroidUtilities.dp(52.0f);
        int i = this.currentType;
        if (i == 3 || i == 4) {
            dp = AndroidUtilities.dp(77.0f);
        }
        int i2 = dp;
        StaticLayout createStaticLayout2 = StaticLayoutEx.createStaticLayout2(noThemeString(), this.noThemeTextPaint, i2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true, TextUtils.TruncateAt.END, i2, 3);
        this.textLayout = createStaticLayout2;
        return createStaticLayout2;
    }

    private Drawable getPreviewDrawable(TLRPC.TL_theme tL_theme, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        MotionBackgroundDrawable motionBackgroundDrawable;
        int i6;
        if (this.chatThemeItem == null) {
            return null;
        }
        if (i >= 0) {
            TLRPC.WallPaperSettings wallPaperSettings = tL_theme.settings.get(i).wallpaper.settings;
            int i7 = wallPaperSettings.background_color;
            int i8 = wallPaperSettings.second_background_color;
            int i9 = wallPaperSettings.third_background_color;
            i4 = wallPaperSettings.fourth_background_color;
            i5 = i7;
            i2 = i8;
            i3 = i9;
        } else {
            i2 = 0;
            i3 = 0;
            i4 = 0;
            i5 = 0;
        }
        if (i2 != 0) {
            motionBackgroundDrawable = new MotionBackgroundDrawable(i5, i2, i3, i4, true);
            i6 = motionBackgroundDrawable.getPatternColor();
        } else {
            motionBackgroundDrawable = new MotionBackgroundDrawable(i5, i5, i5, i5, true);
            i6 = -16777216;
        }
        this.patternColor = i6;
        this.chatThemeItem.previewDrawable = motionBackgroundDrawable;
        return motionBackgroundDrawable;
    }

    private Drawable getPreviewDrawable(EmojiThemes.ThemeItem themeItem) {
        int i;
        MotionBackgroundDrawable motionBackgroundDrawable;
        Drawable drawable = null;
        drawable = null;
        if (this.chatThemeItem == null) {
            return null;
        }
        int i2 = themeItem.patternBgColor;
        int i3 = themeItem.patternBgGradientColor1;
        int i4 = themeItem.patternBgGradientColor2;
        int i5 = themeItem.patternBgGradientColor3;
        int i6 = themeItem.patternBgRotation;
        if (themeItem.themeInfo.getAccent(false) != null) {
            if (i3 != 0) {
                MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(i2, i3, i4, i5, i6, true);
                i = motionBackgroundDrawable2.getPatternColor();
                motionBackgroundDrawable = motionBackgroundDrawable2;
            } else {
                i = -16777216;
                motionBackgroundDrawable = new MotionBackgroundDrawable(i2, i2, i2, i2, i6, true);
            }
            this.patternColor = i;
            drawable = motionBackgroundDrawable;
        } else if (i2 != 0 && i3 != 0) {
            drawable = new MotionBackgroundDrawable(i2, i3, i4, i5, i6, true);
        } else if (i2 != 0) {
            drawable = new ColorDrawable(i2);
        } else {
            Theme.ThemeInfo themeInfo = themeItem.themeInfo;
            if (themeInfo == null || (themeInfo.previewWallpaperOffset <= 0 && themeInfo.pathToWallpaper == null)) {
                EmojiThemes emojiThemes = this.chatThemeItem.chatTheme;
                if (emojiThemes == null || !emojiThemes.isAnyStub()) {
                    drawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, true);
                }
            } else {
                float dp = AndroidUtilities.dp(112.0f);
                float dp2 = AndroidUtilities.dp(134.0f);
                Theme.ThemeInfo themeInfo2 = themeItem.themeInfo;
                Bitmap scaledBitmap = AndroidUtilities.getScaledBitmap(dp, dp2, themeInfo2.pathToWallpaper, themeInfo2.pathToFile, themeInfo2.previewWallpaperOffset);
                if (scaledBitmap != null) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(scaledBitmap);
                    bitmapDrawable.setFilterBitmap(true);
                    drawable = bitmapDrawable;
                }
            }
        }
        this.chatThemeItem.previewDrawable = drawable;
        return drawable;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$playEmojiAnimation$5() {
        this.animationCancelRunnable = null;
        this.backupImageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    public void lambda$setItem$0(long j, ChatThemeBottomSheet.ChatThemeItem chatThemeItem, int i, Pair pair) {
        if (pair == null || ((Long) pair.first).longValue() != j) {
            return;
        }
        Drawable drawable = chatThemeItem.previewDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
            motionBackgroundDrawable.setPatternBitmap(i >= 0 ? 100 : -100, prescaleBitmap((Bitmap) pair.second), true);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
        }
        invalidate();
    }

    public void lambda$setItem$1(ChatThemeBottomSheet.ChatThemeItem chatThemeItem, TLRPC.WallPaper wallPaper, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        Bitmap bitmap;
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (!z || bitmapSafe == null || (bitmap = bitmapSafe.bitmap) == null) {
            return;
        }
        Drawable drawable = chatThemeItem.previewDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
            TLRPC.WallPaperSettings wallPaperSettings = wallPaper.settings;
            motionBackgroundDrawable.setPatternBitmap((wallPaperSettings == null || wallPaperSettings.intensity >= 0) ? 100 : -100, prescaleBitmap(bitmap), true);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    public void lambda$setItem$2(ChatThemeBottomSheet.ChatThemeItem chatThemeItem, int i, Bitmap bitmap) {
        Drawable drawable = chatThemeItem.previewDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
            motionBackgroundDrawable.setPatternBitmap(i, prescaleBitmap(bitmap), true);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    public void lambda$setItem$3(final ChatThemeBottomSheet.ChatThemeItem chatThemeItem, final int i) {
        final Bitmap bitmap = SvgHelper.getBitmap(R.raw.default_pattern, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(140.0f), -16777216, AndroidUtilities.density);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemeSmallPreviewView.this.lambda$setItem$2(chatThemeItem, i, bitmap);
            }
        });
    }

    public void lambda$setSelected$4(ValueAnimator valueAnimator) {
        this.selectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private Bitmap prescaleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        float max = Math.max(AndroidUtilities.dp(120.0f) / bitmap.getWidth(), AndroidUtilities.dp(140.0f) / bitmap.getHeight());
        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0 || Math.abs(max - 1.0f) < 0.0125f) {
            return bitmap;
        }
        int width = (int) (bitmap.getWidth() * max);
        int height = (int) (bitmap.getHeight() * max);
        return (height <= 0 || width <= 0) ? bitmap : Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public void cancelAnimation() {
        Runnable runnable = this.animationCancelRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.animationCancelRunnable.run();
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        ThemeDrawable themeDrawable;
        ThemeDrawable themeDrawable2;
        if (this.chatThemeItem == null) {
            super.dispatchDraw(canvas);
            return;
        }
        if (this.chatBackgroundDrawable != null) {
            canvas.save();
            canvas.clipPath(this.clipPath);
            this.chatBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.chatBackgroundDrawable.draw(canvas);
            canvas.restore();
        }
        if (this.changeThemeProgress != 1.0f && (themeDrawable2 = this.animateOutThemeDrawable) != null) {
            themeDrawable2.drawBackground(canvas, 1.0f);
        }
        float f = this.changeThemeProgress;
        if (f != 0.0f) {
            this.themeDrawable.drawBackground(canvas, f);
        }
        if (this.changeThemeProgress != 1.0f && (themeDrawable = this.animateOutThemeDrawable) != null) {
            themeDrawable.draw(canvas, 1.0f);
        }
        float f2 = this.changeThemeProgress;
        if (f2 != 0.0f) {
            this.themeDrawable.draw(canvas, f2);
        }
        float f3 = this.changeThemeProgress;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.10666667f;
            this.changeThemeProgress = f4;
            if (f4 >= 1.0f) {
                this.changeThemeProgress = 1.0f;
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    protected String noThemeString() {
        return LocaleController.getString(R.string.ChatNoTheme);
    }

    public int noThemeStringTextSize() {
        return 14;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.attached = true;
        ChatBackgroundDrawable chatBackgroundDrawable = this.chatBackgroundDrawable;
        if (chatBackgroundDrawable != null) {
            chatBackgroundDrawable.onAttachedToWindow(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        this.attached = false;
        ChatBackgroundDrawable chatBackgroundDrawable = this.chatBackgroundDrawable;
        if (chatBackgroundDrawable != null) {
            chatBackgroundDrawable.onDetachedFromWindow(this);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        accessibilityNodeInfo.setSelected(this.isSelected);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size;
        float f;
        float f2;
        int i3;
        int i4 = this.currentType;
        if (i4 != 1 && i4 != 4) {
            size = AndroidUtilities.dp(i4 == 0 ? 77.0f : 83.0f);
            i3 = View.MeasureSpec.getSize(i2);
            if (i3 == 0) {
                f = size;
                f2 = 1.35f;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(i3, 1073741824));
            this.backupImageView.setPivotY(r4.getMeasuredHeight());
            this.backupImageView.setPivotX(r4.getMeasuredWidth() / 2.0f);
        }
        size = View.MeasureSpec.getSize(i);
        f = size;
        f2 = 1.2f;
        i3 = (int) (f * f2);
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(i3, 1073741824));
        this.backupImageView.setPivotY(r4.getMeasuredHeight());
        this.backupImageView.setPivotX(r4.getMeasuredWidth() / 2.0f);
    }

    @Override
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i == i3 && i2 == i4) {
            return;
        }
        RectF rectF = this.rectF;
        float f = this.INNER_RECT_SPACE;
        rectF.set(f, f, i - f, i2 - f);
        this.clipPath.reset();
        Path path = this.clipPath;
        RectF rectF2 = this.rectF;
        float f2 = this.INNER_RADIUS;
        path.addRoundRect(rectF2, f2, f2, Path.Direction.CW);
    }

    public void playEmojiAnimation() {
        if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
            this.backupImageView.setVisibility(0);
            if (!this.backupImageView.getImageReceiver().getLottieAnimation().isRunning) {
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, true);
                this.backupImageView.getImageReceiver().getLottieAnimation().start();
            }
            this.backupImageView.animate().scaleX(2.0f).scaleY(2.0f).setDuration(300L).setInterpolator(AndroidUtilities.overshootInterpolator).start();
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    ThemeSmallPreviewView.this.lambda$playEmojiAnimation$5();
                }
            };
            this.animationCancelRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 2500L);
        }
    }

    @Override
    public void setBackgroundColor(int i) {
        this.backgroundFillPaint.setColor(getThemedColor(Theme.key_dialogBackgroundGray));
        TextPaint textPaint = this.noThemeTextPaint;
        if (textPaint != null) {
            textPaint.setColor(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription));
        }
        invalidate();
    }

    public void setFallbackWallpaper(TLRPC.WallPaper wallPaper) {
        if (this.fallbackWallpaper != wallPaper) {
            this.fallbackWallpaper = wallPaper;
            ChatThemeBottomSheet.ChatThemeItem chatThemeItem = this.chatThemeItem;
            if (chatThemeItem != null) {
                EmojiThemes emojiThemes = chatThemeItem.chatTheme;
                if (emojiThemes == null || emojiThemes.wallpaper == null) {
                    this.chatThemeItem = null;
                    setItem(chatThemeItem, false);
                }
            }
        }
    }

    public void setItem(final ChatThemeBottomSheet.ChatThemeItem chatThemeItem, boolean z) {
        TLRPC.TL_theme tL_theme;
        TLRPC.Document document;
        ChatBackgroundDrawable chatBackgroundDrawable;
        ChatBackgroundDrawable chatBackgroundDrawable2;
        boolean z2 = this.chatThemeItem != chatThemeItem;
        int i = this.lastThemeIndex;
        int i2 = chatThemeItem.themeIndex;
        boolean z3 = i != i2;
        this.lastThemeIndex = i2;
        this.chatThemeItem = chatThemeItem;
        this.hasAnimatedEmoji = false;
        TLRPC.Document emojiAnimatedSticker = chatThemeItem.chatTheme.getEmoticon() != null ? MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(chatThemeItem.chatTheme.getEmoticon()) : null;
        if (z2) {
            Runnable runnable = this.animationCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.animationCancelRunnable = null;
            }
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
        }
        if (z2) {
            Drawable svgThumb = emojiAnimatedSticker != null ? DocumentObject.getSvgThumb(emojiAnimatedSticker, Theme.key_emptyListPlaceholder, 0.2f) : null;
            if (svgThumb == null) {
                Emoji.preloadEmoji(chatThemeItem.chatTheme.getEmoticon());
                svgThumb = Emoji.getEmojiDrawable(chatThemeItem.chatTheme.getEmoticon());
            }
            this.backupImageView.setImage(ImageLocation.getForDocument(emojiAnimatedSticker), "50_50", svgThumb, (Object) null);
            TLRPC.WallPaper wallPaper = chatThemeItem.chatTheme.wallpaper;
            if (wallPaper == null) {
                wallPaper = this.fallbackWallpaper;
            }
            if (wallPaper != null) {
                if (this.attached && (chatBackgroundDrawable2 = this.chatBackgroundDrawable) != null) {
                    chatBackgroundDrawable2.onDetachedFromWindow(this);
                }
                ChatBackgroundDrawable chatBackgroundDrawable3 = new ChatBackgroundDrawable(wallPaper, false, true);
                this.chatBackgroundDrawable = chatBackgroundDrawable3;
                chatBackgroundDrawable3.setParent(this);
                if (this.attached) {
                    this.chatBackgroundDrawable.onAttachedToWindow(this);
                }
            } else {
                if (this.attached && (chatBackgroundDrawable = this.chatBackgroundDrawable) != null) {
                    chatBackgroundDrawable.onDetachedFromWindow(this);
                }
                this.chatBackgroundDrawable = null;
            }
        }
        this.backupImageView.setVisibility((!chatThemeItem.chatTheme.isAnyStub() || this.fallbackWallpaper == null) ? 0 : 8);
        if (z2 || z3) {
            if (z) {
                this.changeThemeProgress = 0.0f;
                this.animateOutThemeDrawable = this.themeDrawable;
                this.themeDrawable = new ThemeDrawable();
                invalidate();
            } else {
                this.changeThemeProgress = 1.0f;
            }
            updatePreviewBackground(this.themeDrawable);
            TLRPC.TL_theme tlTheme = chatThemeItem.chatTheme.getTlTheme(this.lastThemeIndex);
            if (tlTheme != null) {
                final long j = tlTheme.id;
                TLRPC.WallPaper wallpaper = chatThemeItem.chatTheme.getWallpaper(this.lastThemeIndex);
                if (wallpaper != null) {
                    final int i3 = wallpaper.settings.intensity;
                    chatThemeItem.chatTheme.loadWallpaperThumb(this.lastThemeIndex, new ResultCallback() {
                        @Override
                        public final void onComplete(Object obj) {
                            ThemeSmallPreviewView.this.lambda$setItem$0(j, chatThemeItem, i3, (Pair) obj);
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
                }
            } else {
                SparseArray sparseArray = chatThemeItem.chatTheme.getThemeInfo(this.lastThemeIndex).themeAccentsMap;
                Theme.ThemeAccent themeAccent = sparseArray != null ? (Theme.ThemeAccent) sparseArray.get(chatThemeItem.chatTheme.getAccentId(this.lastThemeIndex)) : null;
                if (themeAccent != null && (tL_theme = themeAccent.info) != null && tL_theme.settings.size() > 0) {
                    final TLRPC.WallPaper wallPaper2 = themeAccent.info.settings.get(0).wallpaper;
                    if (wallPaper2 != null && (document = wallPaper2.document) != null) {
                        ImageLocation forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 120), document);
                        ImageReceiver imageReceiver = new ImageReceiver();
                        imageReceiver.setAllowLoadingOnAttachedOnly(false);
                        imageReceiver.setImage(forDocument, "120_140", null, null, null, 1);
                        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                            @Override
                            public final void didSetImage(ImageReceiver imageReceiver2, boolean z4, boolean z5, boolean z6) {
                                ThemeSmallPreviewView.this.lambda$setItem$1(chatThemeItem, wallPaper2, imageReceiver2, z4, z5, z6);
                            }

                            @Override
                            public void didSetImageBitmap(int i4, String str, Drawable drawable) {
                                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i4, str, drawable);
                            }

                            @Override
                            public void onAnimationReady(ImageReceiver imageReceiver2) {
                                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
                            }
                        });
                        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    }
                } else if (themeAccent != null && themeAccent.info == null) {
                    final int i4 = (int) (themeAccent.patternIntensity * 100.0f);
                    Drawable drawable = chatThemeItem.previewDrawable;
                    if (drawable instanceof MotionBackgroundDrawable) {
                        ((MotionBackgroundDrawable) drawable).setPatternBitmap(i4);
                    }
                    ChatThemeController.chatThemeQueue.postRunnable(new Runnable() {
                        @Override
                        public final void run() {
                            ThemeSmallPreviewView.this.lambda$setItem$3(chatThemeItem, i4);
                        }
                    });
                }
            }
        }
        if (!z) {
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
            AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
            }
        }
        EmojiThemes emojiThemes = this.chatThemeItem.chatTheme;
        setContentDescription((emojiThemes == null || emojiThemes.isAnyStub()) ? LocaleController.getString(R.string.ChatNoTheme) : this.chatThemeItem.chatTheme.getEmoticon());
    }

    public void setSelected(final boolean z, boolean z2) {
        if (!z2) {
            ValueAnimator valueAnimator = this.strokeAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.isSelected = z;
            this.selectionProgress = z ? 1.0f : 0.0f;
            invalidate();
            return;
        }
        if (this.isSelected != z) {
            float f = this.selectionProgress;
            ValueAnimator valueAnimator2 = this.strokeAlphaAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
            this.strokeAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    ThemeSmallPreviewView.this.lambda$setSelected$4(valueAnimator3);
                }
            });
            this.strokeAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    ThemeSmallPreviewView.this.selectionProgress = z ? 1.0f : 0.0f;
                    ThemeSmallPreviewView.this.invalidate();
                }
            });
            this.strokeAlphaAnimator.setDuration(250L);
            this.strokeAlphaAnimator.start();
        }
        this.isSelected = z;
    }

    public void updatePreviewBackground(ThemeDrawable themeDrawable) {
        EmojiThemes emojiThemes;
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem = this.chatThemeItem;
        if (chatThemeItem == null || (emojiThemes = chatThemeItem.chatTheme) == null) {
            return;
        }
        EmojiThemes.ThemeItem themeItem = emojiThemes.getThemeItem(chatThemeItem.themeIndex);
        themeDrawable.inBubblePaint.setColor(themeItem.inBubbleColor);
        themeDrawable.outBubblePaintSecond.setColor(themeItem.outBubbleColor);
        int themedColor = this.chatThemeItem.chatTheme.isAnyStub() ? getThemedColor(Theme.key_featuredStickers_addButton) : themeItem.outLineColor;
        int alpha = themeDrawable.strokePaint.getAlpha();
        themeDrawable.strokePaint.setColor(themedColor);
        themeDrawable.strokePaint.setAlpha(alpha);
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem2 = this.chatThemeItem;
        TLRPC.TL_theme tlTheme = chatThemeItem2.chatTheme.getTlTheme(chatThemeItem2.themeIndex);
        if (tlTheme != null) {
            ChatThemeBottomSheet.ChatThemeItem chatThemeItem3 = this.chatThemeItem;
            int settingsIndex = chatThemeItem3.chatTheme.getSettingsIndex(chatThemeItem3.themeIndex);
            fillOutBubblePaint(themeDrawable.outBubblePaintSecond, tlTheme.settings.get(settingsIndex).message_colors);
            themeDrawable.outBubblePaintSecond.setAlpha(255);
            getPreviewDrawable(tlTheme, settingsIndex);
        } else {
            ChatThemeBottomSheet.ChatThemeItem chatThemeItem4 = this.chatThemeItem;
            getPreviewDrawable(chatThemeItem4.chatTheme.getThemeItem(chatThemeItem4.themeIndex));
        }
        themeDrawable.previewDrawable = this.chatThemeItem.previewDrawable;
        invalidate();
    }
}
