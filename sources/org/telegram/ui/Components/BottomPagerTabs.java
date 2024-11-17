package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public abstract class BottomPagerTabs extends View {
    private Utilities.Callback onTabClick;
    private float progress;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean scrolling;
    private AnimatedFloat scrollingT;
    private final Paint selectPaint;
    private final Tab[] tabs;
    private boolean touchDown;
    private int value;

    public class Tab {
        private boolean active;
        final RectF clickRect;
        public int customEndFrameEnd;
        public int customEndFrameMid;
        public boolean customFrameInvert;
        final RLottieDrawable drawable;
        private int drawableColor;
        final int i;
        final StaticLayout layout;
        final float layoutLeft;
        final float layoutWidth;
        final AnimatedFloat nonscrollingT;
        final TextPaint paint;
        final Drawable ripple;

        public Tab(int i, int i2, int i3, int i4, CharSequence charSequence) {
            TextPaint textPaint = new TextPaint(1);
            this.paint = textPaint;
            this.clickRect = new RectF();
            this.nonscrollingT = new AnimatedFloat(BottomPagerTabs.this, 0L, 200L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.drawableColor = -1;
            this.i = i;
            this.customEndFrameMid = i3;
            this.customEndFrameEnd = i4;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(29.0f), AndroidUtilities.dp(29.0f));
            this.drawable = rLottieDrawable;
            rLottieDrawable.setMasterParent(BottomPagerTabs.this);
            rLottieDrawable.setAllowDecodeSingleFrame(true);
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            rLottieDrawable.setAutoRepeat(0);
            textPaint.setTypeface(AndroidUtilities.bold());
            textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            int i5 = Theme.key_windowBackgroundWhiteBlackText;
            textPaint.setColor(Theme.getColor(i5, BottomPagerTabs.this.resourcesProvider));
            StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.layout = staticLayout;
            this.layoutWidth = staticLayout.getLineCount() > 0 ? staticLayout.getLineWidth(0) : 0.0f;
            this.layoutLeft = staticLayout.getLineCount() > 0 ? staticLayout.getLineLeft(0) : 0.0f;
            this.ripple = Theme.createSelectorDrawable(Theme.multAlpha(Theme.getColor(i5, BottomPagerTabs.this.resourcesProvider), 0.1f), 7, AndroidUtilities.dp(16.0f));
        }

        public Tab customFrameInvert() {
            this.customFrameInvert = true;
            return this;
        }

        public void setActive(boolean r3, boolean r4) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BottomPagerTabs.Tab.setActive(boolean, boolean):void");
        }

        public void setColor(int i) {
            this.paint.setColor(i);
            if (this.drawableColor != i) {
                RLottieDrawable rLottieDrawable = this.drawable;
                this.drawableColor = i;
                rLottieDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public BottomPagerTabs(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.selectPaint = new Paint(1);
        this.scrollingT = new AnimatedFloat(this, 0L, 210L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.resourcesProvider = resourcesProvider;
        this.tabs = createTabs();
        setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        setProgress(0.0f, false);
    }

    private void setProgress(float f, boolean z) {
        float clamp = Utilities.clamp(f, this.tabs.length, 0.0f);
        this.progress = clamp;
        this.value = Math.round(clamp);
        int i = 0;
        while (true) {
            Tab[] tabArr = this.tabs;
            if (i >= tabArr.length) {
                invalidate();
                return;
            } else {
                tabArr[i].setActive(((float) Math.abs(this.value - i)) < (this.tabs[i].active ? 0.25f : 0.35f), z);
                i++;
            }
        }
    }

    public abstract Tab[] createTabs();

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
        canvas.drawRect(0.0f, 0.0f, getWidth(), AndroidUtilities.getShadowHeight(), Theme.dividerPaint);
        int width = ((getWidth() - getPaddingLeft()) - getPaddingRight()) / this.tabs.length;
        int min = Math.min(AndroidUtilities.dp(64.0f), width);
        float f = this.scrollingT.set(this.scrolling);
        float f2 = 9.0f;
        float f3 = 2.0f;
        float f4 = 0.0f;
        if (f > 0.0f) {
            double floor = Math.floor(this.progress) + 0.5d;
            double d = this.progress;
            Double.isNaN(d);
            double abs = (Math.abs(floor - d) * 1.2000000476837158d) + 0.4000000059604645d;
            Paint paint = this.selectPaint;
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider);
            double d2 = f;
            Double.isNaN(d2);
            paint.setColor(ColorUtils.setAlphaComponent(color, (int) (abs * 18.0d * d2)));
            float f5 = width;
            float f6 = f5 / 2.0f;
            float paddingLeft = getPaddingLeft() + AndroidUtilities.lerp((((float) Math.floor(this.progress)) * f5) + f6, (f5 * ((float) Math.ceil(this.progress))) + f6, this.progress - ((int) r13));
            RectF rectF = AndroidUtilities.rectTmp;
            float f7 = min / 2.0f;
            rectF.set(paddingLeft - f7, AndroidUtilities.dp(9.0f), paddingLeft + f7, AndroidUtilities.dp(41.0f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), this.selectPaint);
        }
        int i = 0;
        while (true) {
            Tab[] tabArr = this.tabs;
            if (i >= tabArr.length) {
                return;
            }
            Tab tab = tabArr[i];
            tab.clickRect.set(getPaddingLeft() + (i * width), f4, r13 + width, getHeight());
            float min2 = 1.0f - Math.min(1.0f, Math.abs(this.progress - i));
            int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6, this.resourcesProvider);
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            tab.setColor(ColorUtils.blendARGB(color2, Theme.getColor(i2, this.resourcesProvider), min2));
            android.graphics.Rect rect = AndroidUtilities.rectTmp2;
            float f8 = min / f3;
            rect.set((int) (tab.clickRect.centerX() - f8), AndroidUtilities.dp(f2), (int) (tab.clickRect.centerX() + f8), AndroidUtilities.dp(41.0f));
            float f9 = tab.nonscrollingT.set(min2 > 0.6f);
            if (f < 1.0f) {
                this.selectPaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(i2, this.resourcesProvider), (int) (f9 * 18.0f * (1.0f - f))));
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set(rect);
                canvas.drawRoundRect(rectF2, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), this.selectPaint);
            }
            tab.ripple.setBounds(rect);
            tab.ripple.draw(canvas);
            float dp = AndroidUtilities.dp(29.0f) / 2.0f;
            rect.set((int) (tab.clickRect.centerX() - dp), (int) (AndroidUtilities.dpf2(24.66f) - dp), (int) (tab.clickRect.centerX() + dp), (int) (AndroidUtilities.dpf2(24.66f) + dp));
            tab.drawable.setBounds(rect);
            tab.drawable.draw(canvas);
            canvas.save();
            canvas.translate((tab.clickRect.centerX() - (tab.layoutWidth / 2.0f)) - tab.layoutLeft, AndroidUtilities.dp(50.0f) - (tab.layout.getHeight() / 2.0f));
            tab.layout.draw(canvas);
            canvas.restore();
            i++;
            f2 = 9.0f;
            f3 = 2.0f;
            f4 = 0.0f;
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(64.0f) + AndroidUtilities.getShadowHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Utilities.Callback callback;
        if (motionEvent.getAction() == 0) {
            this.touchDown = true;
            return true;
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            int i = 0;
            while (true) {
                Tab[] tabArr = this.tabs;
                if (i >= tabArr.length) {
                    i = -1;
                    break;
                }
                RectF rectF = tabArr[i].clickRect;
                if (rectF.left >= x || rectF.right <= x) {
                    i++;
                } else if (motionEvent.getAction() != 1) {
                    if (this.touchDown) {
                        this.tabs[i].ripple.setState(new int[0]);
                    }
                    this.tabs[i].ripple.setState(new int[]{16842919, 16842910});
                }
            }
            for (int i2 = 0; i2 < this.tabs.length; i2++) {
                if (i2 != i || motionEvent.getAction() == 1) {
                    this.tabs[i2].ripple.setState(new int[0]);
                }
            }
            if (i >= 0 && this.value != i && (callback = this.onTabClick) != null) {
                callback.run(Integer.valueOf(i));
            }
            this.touchDown = false;
        } else if (motionEvent.getAction() == 3) {
            if (Build.VERSION.SDK_INT >= 21) {
                int i3 = 0;
                while (true) {
                    Tab[] tabArr2 = this.tabs;
                    if (i3 >= tabArr2.length) {
                        break;
                    }
                    tabArr2[i3].ripple.setState(new int[0]);
                    i3++;
                }
            }
            this.touchDown = false;
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setOnTabClick(Utilities.Callback<Integer> callback) {
        this.onTabClick = callback;
    }

    public void setProgress(float f) {
        setProgress(f, true);
    }

    public void setScrolling(boolean z) {
        if (this.scrolling == z) {
            return;
        }
        this.scrolling = z;
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        int i = 0;
        while (true) {
            Tab[] tabArr = this.tabs;
            if (i >= tabArr.length) {
                return super.verifyDrawable(drawable);
            }
            if (tabArr[i].ripple == drawable) {
                return true;
            }
            i++;
        }
    }
}
