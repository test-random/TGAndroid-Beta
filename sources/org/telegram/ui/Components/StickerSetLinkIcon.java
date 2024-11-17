package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class StickerSetLinkIcon extends Drawable {
    private final int N;
    private final int count;
    private final AnimatedEmojiDrawable[] drawables;
    public final boolean out;
    public int alpha = 255;
    private final RectF rect = new RectF();
    private boolean hit = false;

    public StickerSetLinkIcon(int i, boolean z, ArrayList arrayList, boolean z2) {
        this.out = z;
        int max = (int) Math.max(1.0d, Math.sqrt(arrayList.size()));
        this.N = max;
        int min = Math.min(max * max, arrayList.size());
        this.count = min;
        this.drawables = new AnimatedEmojiDrawable[min];
        if (!arrayList.isEmpty()) {
            MessageObject.isAnimatedEmoji((TLRPC.Document) arrayList.get(0));
        }
        int i2 = max < 2 ? 1 : 0;
        for (int i3 = 0; i3 < this.count; i3++) {
            this.drawables[i3] = AnimatedEmojiDrawable.make(i, i2, (TLRPC.Document) arrayList.get(i3));
        }
    }

    public void attach(View view) {
        for (int i = 0; i < this.count; i++) {
            this.drawables[i].addView(view);
        }
    }

    public void detach(View view) {
        for (int i = 0; i < this.count; i++) {
            this.drawables[i].removeView(view);
        }
    }

    public boolean die() {
        return this.hit;
    }

    @Override
    public void draw(Canvas canvas) {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        if (this.alpha <= 0) {
            return;
        }
        this.rect.set(getBounds());
        float centerX = this.rect.centerX() - (getIntrinsicWidth() / 2.0f);
        float centerY = this.rect.centerY() - (getIntrinsicHeight() / 2.0f);
        float intrinsicWidth = getIntrinsicWidth() / this.N;
        float intrinsicHeight = getIntrinsicHeight() / this.N;
        canvas.save();
        canvas.clipRect(centerX, centerY, getIntrinsicWidth() + centerX, getIntrinsicHeight() + centerY);
        for (int i = 0; i < this.N; i++) {
            int i2 = 0;
            while (true) {
                int i3 = this.N;
                if (i2 < i3) {
                    int i4 = (i3 * i) + i2;
                    if (i4 >= 0) {
                        AnimatedEmojiDrawable[] animatedEmojiDrawableArr = this.drawables;
                        if (i4 < animatedEmojiDrawableArr.length && (animatedEmojiDrawable = animatedEmojiDrawableArr[i4]) != null) {
                            animatedEmojiDrawable.setBounds((int) ((i2 * intrinsicWidth) + centerX), (int) ((i * intrinsicHeight) + centerY), (int) (((i2 + 1) * intrinsicWidth) + centerX), (int) (((i + 1) * intrinsicHeight) + centerY));
                            this.drawables[i4].setAlpha(this.alpha);
                            this.drawables[i4].setColorFilter(this.out ? Theme.chat_outAnimatedEmojiTextColorFilter : Theme.chat_animatedEmojiTextColorFilter);
                            this.drawables[i4].draw(canvas);
                        }
                    }
                    i2++;
                }
            }
        }
        canvas.restore();
    }

    public boolean equals(ArrayList arrayList) {
        if (arrayList == null) {
            return this.drawables.length == 0;
        }
        if (this.drawables.length != arrayList.size()) {
            return false;
        }
        int i = 0;
        while (true) {
            AnimatedEmojiDrawable[] animatedEmojiDrawableArr = this.drawables;
            if (i >= animatedEmojiDrawableArr.length) {
                return true;
            }
            TLRPC.Document document = animatedEmojiDrawableArr[i].getDocument();
            if ((document == null ? 0L : document.id) != ((TLRPC.Document) arrayList.get(i)).id) {
                return false;
            }
            i++;
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override
    public int getOpacity() {
        return -2;
    }

    public void keepAlive() {
        this.hit = false;
    }

    public void readyToDie() {
        this.hit = true;
    }

    @Override
    public void setAlpha(int i) {
        this.alpha = i;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
