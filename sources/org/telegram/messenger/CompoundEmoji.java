package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.CompoundEmoji;
import org.telegram.messenger.Emoji;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class CompoundEmoji {
    public static List<String> skinTones = Arrays.asList("🏻", "🏼", "🏽", "🏾", "🏿");
    private static Paint placeholderPaint = new Paint(2);

    public static class CompoundEmojiDrawable extends Emoji.EmojiDrawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        private DrawableInfo left;
        private AnimatedFloat leftUpdateT;
        private DrawableInfo newLeft;
        private DrawableInfo newRight;
        private View parent;
        private DrawableInfo right;
        private AnimatedFloat rightUpdateT;

        public CompoundEmojiDrawable(DrawableInfo drawableInfo, DrawableInfo drawableInfo2) {
            this.left = drawableInfo;
            this.right = drawableInfo2;
        }

        private void drawDrawableInfo(Canvas canvas, DrawableInfo drawableInfo, Rect rect2, float f) {
            int i;
            Bitmap bitmap = drawableInfo.getBitmap();
            if (bitmap != null) {
                Paint paint2 = drawableInfo.placeholder ? CompoundEmoji.placeholderPaint : paint;
                if (f < 1.0f) {
                    i = paint2.getAlpha();
                    paint2.setAlpha((int) (i * f));
                } else {
                    i = 255;
                }
                canvas.drawBitmap(bitmap, (Rect) null, rect2, paint2);
                if (f < 1.0f) {
                    paint2.setAlpha(i);
                }
            }
        }

        public void invalidate() {
            View view = this.parent;
            if (view != null) {
                view.invalidate();
            }
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            if (!isLoaded()) {
                preload();
                Emoji.placeholderPaint.setColor(this.placeholderColor);
                Rect bounds = getBounds();
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() * 0.4f, Emoji.placeholderPaint);
                return;
            }
            Rect drawRect = this.fullSize ? getDrawRect() : getBounds();
            if (canvas.quickReject(drawRect.left, drawRect.top, drawRect.right, drawRect.bottom, Canvas.EdgeType.AA)) {
                return;
            }
            if (this.newLeft != null) {
                if (this.leftUpdateT == null) {
                    this.leftUpdateT = new AnimatedFloat(0.0f, new Runnable() {
                        @Override
                        public final void run() {
                            CompoundEmoji.CompoundEmojiDrawable.this.invalidate();
                        }
                    }, 0L, 320L, CubicBezierInterpolator.EASE_OUT);
                }
                float f = this.leftUpdateT.set(1.0f);
                drawDrawableInfo(canvas, this.newLeft, drawRect, Math.min(1.0f, f * 1.5f));
                drawDrawableInfo(canvas, this.left, drawRect, 1.0f - f);
                if (f >= 1.0f) {
                    this.left = this.newLeft;
                    this.newLeft = null;
                }
            } else {
                drawDrawableInfo(canvas, this.left, drawRect, 1.0f);
            }
            if (this.newRight == null) {
                drawDrawableInfo(canvas, this.right, drawRect, 1.0f);
                return;
            }
            if (this.rightUpdateT == null) {
                this.rightUpdateT = new AnimatedFloat(0.0f, new Runnable() {
                    @Override
                    public final void run() {
                        CompoundEmoji.CompoundEmojiDrawable.this.invalidate();
                    }
                }, 0L, 320L, CubicBezierInterpolator.EASE_OUT);
            }
            float f2 = this.rightUpdateT.set(1.0f);
            drawDrawableInfo(canvas, this.newRight, drawRect, Math.min(1.0f, 1.5f * f2));
            drawDrawableInfo(canvas, this.right, drawRect, 1.0f - f2);
            if (f2 >= 1.0f) {
                this.right = this.newRight;
                this.newRight = null;
            }
        }

        public Rect getDrawRect() {
            Rect bounds = getBounds();
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            Rect rect2 = rect;
            boolean z = this.fullSize;
            rect2.left = centerX - ((z ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = centerX + ((z ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.top = centerY - ((z ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = centerY + ((z ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            return rect;
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public boolean isLoaded() {
            return this.left.isLoaded() && this.right.isLoaded();
        }

        @Override
        public void preload() {
            if (isLoaded()) {
                return;
            }
            this.left.load();
            this.right.load();
        }

        @Override
        public void setAlpha(int i) {
            paint.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void update(int i, int i2) {
            if (this.left.skin != i) {
                DrawableInfo drawableInfo = this.newLeft;
                if (drawableInfo != null) {
                    this.left = drawableInfo;
                }
                this.newLeft = this.left.updateSkin(i);
                AnimatedFloat animatedFloat = this.leftUpdateT;
                if (animatedFloat != null) {
                    animatedFloat.set(0.0f, true);
                }
            }
            if (this.right.skin != i2) {
                DrawableInfo drawableInfo2 = this.newRight;
                if (drawableInfo2 != null) {
                    this.right = drawableInfo2;
                }
                this.newRight = this.right.updateSkin(i2);
                AnimatedFloat animatedFloat2 = this.rightUpdateT;
                if (animatedFloat2 != null) {
                    animatedFloat2.set(0.0f, true);
                }
            }
            invalidate();
        }
    }

    public static class DrawableInfo {
        private static final SparseArray<Bitmap> bitmaps = new SparseArray<>();
        private static final ArrayList<Integer> loading = new ArrayList<>();
        int emoji;
        int hash;
        int place;
        boolean placeholder;
        int skin;

        public DrawableInfo(int i, int i2, int i3) {
            if (i2 == -2) {
                this.placeholder = true;
                i2 = -1;
            }
            this.emoji = i;
            Integer valueOf = Integer.valueOf(i);
            this.skin = i2;
            Integer valueOf2 = Integer.valueOf(i2);
            this.place = i3;
            this.hash = Objects.hash(valueOf, valueOf2, Integer.valueOf(i3));
        }

        public void lambda$load$0() {
            Bitmap loadBitmap = Emoji.loadBitmap("emoji/compound/" + this.emoji + "_" + this.skin + "_" + this.place + ".png");
            if (loadBitmap != null) {
                bitmaps.put(this.hash, loadBitmap);
                Runnable runnable = Emoji.invalidateUiRunnable;
                AndroidUtilities.cancelRunOnUIThread(runnable);
                AndroidUtilities.runOnUIThread(runnable);
            }
            loading.remove(Integer.valueOf(this.hash));
        }

        public Bitmap getBitmap() {
            return bitmaps.get(this.hash);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean isLoaded() {
            return bitmaps.indexOfKey(this.hash) >= 0;
        }

        public void load() {
            if (isLoaded()) {
                return;
            }
            ArrayList<Integer> arrayList = loading;
            if (arrayList.contains(Integer.valueOf(this.hash))) {
                return;
            }
            arrayList.add(Integer.valueOf(this.hash));
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    CompoundEmoji.DrawableInfo.this.lambda$load$0();
                }
            });
        }

        public DrawableInfo updateSkin(int i) {
            return this.skin == i ? this : new DrawableInfo(this.emoji, i, this.place);
        }
    }

    public static String applyColor(String str, String str2) {
        if (isHandshake(str) == null) {
            return str;
        }
        if (str2 == null) {
            return "🤝";
        }
        if (!str2.contains("\u200d")) {
            return "🤝" + str2;
        }
        String[] split = str2.split("\u200d");
        StringBuilder sb = new StringBuilder();
        sb.append("🫱");
        sb.append(split.length >= 1 ? split[0] : "");
        sb.append("\u200d🫲");
        sb.append(split.length >= 2 ? split[1] : "");
        return sb.toString();
    }

    public static CompoundEmojiDrawable getCompoundEmojiDrawable(String str) {
        return getCompoundEmojiDrawable(str, null, null);
    }

    public static CompoundEmojiDrawable getCompoundEmojiDrawable(String str, Integer num, Integer num2) {
        CompoundEmojiDrawable compoundEmojiDrawable = null;
        if (str == null) {
            return null;
        }
        Pair<Integer, Integer> isHandshake = isHandshake(str);
        if (isHandshake != null) {
            if (num == null) {
                num = (Integer) isHandshake.first;
            }
            DrawableInfo drawableInfo = new DrawableInfo(0, num.intValue(), 0);
            if (num2 == null) {
                num2 = (Integer) isHandshake.second;
            }
            compoundEmojiDrawable = new CompoundEmojiDrawable(drawableInfo, new DrawableInfo(0, num2.intValue(), 1));
        }
        return compoundEmojiDrawable;
    }

    public static int getSkinTone(String str) {
        if (str == null) {
            return -1;
        }
        return skinTones.indexOf(str.substring(str.length() - 2));
    }

    public static boolean isCompound(String str) {
        return getCompoundEmojiDrawable(str) != null;
    }

    public static Pair<Integer, Integer> isHandshake(String str) {
        int i;
        String[] split;
        if (str.startsWith("🤝")) {
            if (str.length() != 2) {
                if (str.length() == 4) {
                    i = getSkinTone(str);
                    r3 = i >= 0 ? i : -1;
                    split = str.split("\u200d");
                    if (split.length == 2 || !split[0].startsWith("🫱") || !split[1].startsWith("🫲")) {
                        return null;
                    }
                    if (split[0].length() != 2 && (split[0].length() != 4 || (i = getSkinTone(split[0])) < 0)) {
                        return null;
                    }
                    if (split[1].length() == 2 || (split[1].length() == 4 && (r3 = getSkinTone(split[1])) >= 0)) {
                        return new Pair<>(Integer.valueOf(i), Integer.valueOf(r3));
                    }
                    return null;
                }
            }
            return new Pair<>(Integer.valueOf(r3), Integer.valueOf(r3));
        }
        i = -1;
        split = str.split("\u200d");
        return split.length == 2 ? null : null;
    }

    public static void setPlaceholderColor(int i) {
        placeholderPaint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
    }
}
