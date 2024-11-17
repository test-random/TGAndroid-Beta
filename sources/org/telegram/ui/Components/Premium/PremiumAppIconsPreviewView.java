package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.LauncherIconController;

public class PremiumAppIconsPreviewView extends FrameLayout implements PagerHeaderView {
    private AdaptiveIconImageView bottomLeftIcon;
    private AdaptiveIconImageView bottomRightIcon;
    private List icons;
    boolean isEmpty;
    private final Theme.ResourcesProvider resourcesProvider;
    private AdaptiveIconImageView topIcon;

    public class AdaptiveIconImageView extends AppIconsSelectorCell.AdaptiveIconImageView {
        StarParticlesView.Drawable drawable;
        Paint paint;
        float particlesScale;

        public AdaptiveIconImageView(Context context, int i) {
            super(context);
            this.drawable = new StarParticlesView.Drawable(20);
            this.paint = new Paint(1);
            StarParticlesView.Drawable drawable = this.drawable;
            drawable.size1 = 12;
            drawable.size2 = 8;
            drawable.size3 = 6;
            if (i == 1) {
                drawable.type = 1001;
            }
            if (i == 0) {
                drawable.type = 1002;
            }
            drawable.resourcesProvider = PremiumAppIconsPreviewView.this.resourcesProvider;
            StarParticlesView.Drawable drawable2 = this.drawable;
            drawable2.colorKey = Theme.key_premiumStartSmallStarsColor2;
            drawable2.init();
            this.paint.setColor(-1);
        }

        @Override
        public void draw(Canvas canvas) {
            int dp = AndroidUtilities.dp(10.0f);
            this.drawable.excludeRect.set(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), getMeasuredWidth() - AndroidUtilities.dp(5.0f), getMeasuredHeight() - AndroidUtilities.dp(5.0f));
            float f = -dp;
            this.drawable.rect.set(f, f, getWidth() + dp, getHeight() + dp);
            canvas.save();
            float f2 = 1.0f - this.particlesScale;
            canvas.scale(f2, f2, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
            this.drawable.onDraw(canvas);
            canvas.restore();
            invalidate();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), this.paint);
            super.draw(canvas);
        }
    }

    public PremiumAppIconsPreviewView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.icons = new ArrayList();
        this.resourcesProvider = resourcesProvider;
        for (LauncherIconController.LauncherIcon launcherIcon : LauncherIconController.LauncherIcon.values()) {
            if (launcherIcon.premium) {
                this.icons.add(launcherIcon);
            }
            if (this.icons.size() == 3) {
                break;
            }
        }
        if (this.icons.size() < 3) {
            FileLog.e(new IllegalArgumentException("There should be at least 3 premium icons!"));
            this.isEmpty = true;
        } else {
            this.topIcon = newIconView(context, 0);
            this.bottomLeftIcon = newIconView(context, 1);
            this.bottomRightIcon = newIconView(context, 2);
            setClipChildren(false);
        }
    }

    private AdaptiveIconImageView newIconView(Context context, int i) {
        LauncherIconController.LauncherIcon launcherIcon = (LauncherIconController.LauncherIcon) this.icons.get(i);
        AdaptiveIconImageView adaptiveIconImageView = new AdaptiveIconImageView(context, i);
        adaptiveIconImageView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 52.0f, 0.0f, 0.0f));
        adaptiveIconImageView.setForeground(launcherIcon.foreground);
        adaptiveIconImageView.setBackgroundResource(launcherIcon.background);
        adaptiveIconImageView.setPadding(AndroidUtilities.dp(8.0f));
        adaptiveIconImageView.setBackgroundOuterPadding(AndroidUtilities.dp(32.0f));
        addView(adaptiveIconImageView);
        return adaptiveIconImageView;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.isEmpty) {
            return;
        }
        int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        int dp = AndroidUtilities.dp(76.0f);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.topIcon.getLayoutParams();
        layoutParams.height = dp;
        layoutParams.width = dp;
        float f = dp;
        layoutParams.bottomMargin = (int) ((min * 0.1f) + f);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.bottomLeftIcon.getLayoutParams();
        layoutParams2.height = dp;
        layoutParams2.width = dp;
        int i3 = (int) (f * 0.95f);
        layoutParams2.rightMargin = i3;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.bottomRightIcon.getLayoutParams();
        layoutParams3.height = dp;
        layoutParams3.width = dp;
        layoutParams3.leftMargin = i3;
    }

    @Override
    public void setOffset(float f) {
        if (this.isEmpty) {
            return;
        }
        float abs = Math.abs(f / getMeasuredWidth());
        float interpolation = CubicBezierInterpolator.EASE_IN.getInterpolation(abs);
        this.bottomRightIcon.setTranslationX(((getRight() - this.bottomRightIcon.getRight()) + (this.bottomRightIcon.getWidth() * 1.5f) + AndroidUtilities.dp(32.0f)) * interpolation);
        this.bottomRightIcon.setTranslationY(AndroidUtilities.dp(16.0f) * interpolation);
        float clamp = Utilities.clamp(AndroidUtilities.lerp(1.0f, 1.5f, interpolation), 1.0f, 0.0f);
        this.bottomRightIcon.setScaleX(clamp);
        this.bottomRightIcon.setScaleY(clamp);
        this.topIcon.setTranslationY((((getTop() - this.topIcon.getTop()) - (this.topIcon.getHeight() * 1.8f)) - AndroidUtilities.dp(32.0f)) * abs);
        this.topIcon.setTranslationX(AndroidUtilities.dp(16.0f) * abs);
        float clamp2 = Utilities.clamp(AndroidUtilities.lerp(1.0f, 1.8f, abs), 1.0f, 0.0f);
        this.topIcon.setScaleX(clamp2);
        this.topIcon.setScaleY(clamp2);
        float interpolation2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(abs);
        this.bottomLeftIcon.setTranslationX((((getLeft() - this.bottomLeftIcon.getLeft()) - (this.bottomLeftIcon.getWidth() * 2.5f)) + AndroidUtilities.dp(32.0f)) * interpolation2);
        this.bottomLeftIcon.setTranslationY(interpolation2 * ((getBottom() - this.bottomLeftIcon.getBottom()) + (this.bottomLeftIcon.getHeight() * 2.5f) + AndroidUtilities.dp(32.0f)));
        float clamp3 = Utilities.clamp(AndroidUtilities.lerp(1.0f, 2.5f, abs), 1.0f, 0.0f);
        this.bottomLeftIcon.setScaleX(clamp3);
        this.bottomLeftIcon.setScaleY(clamp3);
        float f2 = abs < 0.4f ? abs / 0.4f : 1.0f;
        this.bottomRightIcon.particlesScale = f2;
        this.topIcon.particlesScale = f2;
        this.bottomLeftIcon.particlesScale = f2;
    }
}
