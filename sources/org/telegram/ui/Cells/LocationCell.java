package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationCell extends FrameLayout {
    private static FlickerLoadingView globalGradientView;
    private AnimatedTextView addressTextView;
    private boolean allowTextAnimation;
    private ShapeDrawable circleDrawable;
    private float enterAlpha;
    private ValueAnimator enterAnimator;
    private BackupImageView imageView;
    private CharSequence lastCompleteTitle;
    private String lastEmoji;
    private String lastTitle;
    private AnimatedTextView nameTextView;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean wrapContent;

    public LocationCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.enterAlpha = 0.0f;
        this.resourcesProvider = resourcesProvider;
        this.wrapContent = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), -1);
        this.circleDrawable = createCircleDrawable;
        backupImageView.setBackground(createCircleDrawable);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        BackupImageView backupImageView2 = this.imageView;
        boolean z2 = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(42, 42.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 15.0f, 11.0f, z2 ? 15.0f : 0.0f, 0.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.nameTextView = animatedTextView;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        animatedTextView.setAnimationProperties(0.4f, 0L, 350L, cubicBezierInterpolator);
        this.nameTextView.setScaleProperty(0.6f);
        this.nameTextView.setTextSize(AndroidUtilities.dp(16.0f));
        this.nameTextView.setEllipsizeByGradient(true);
        this.nameTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.getDrawable().setOverrideFullWidth(AndroidUtilities.displaySize.x);
        NotificationCenter.listenEmojiLoading(this.nameTextView);
        AnimatedTextView animatedTextView2 = this.nameTextView;
        boolean z3 = LocaleController.isRTL;
        addView(animatedTextView2, LayoutHelper.createFrame(-1, 22.0f, (z3 ? 5 : 3) | 48, z3 ? 16 : 73, 10.0f, z3 ? 73 : 16, 0.0f));
        AnimatedTextView animatedTextView3 = new AnimatedTextView(context, true, true, true);
        this.addressTextView = animatedTextView3;
        animatedTextView3.setScaleProperty(0.6f);
        this.addressTextView.setAnimationProperties(0.4f, 0L, 350L, cubicBezierInterpolator);
        this.addressTextView.setTextSize(AndroidUtilities.dp(14.0f));
        this.addressTextView.setEllipsizeByGradient(true);
        this.addressTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        AnimatedTextView animatedTextView4 = this.addressTextView;
        boolean z4 = LocaleController.isRTL;
        addView(animatedTextView4, LayoutHelper.createFrame(-1, 20.0f, (z4 ? 5 : 3) | 48, z4 ? 16 : 73, 35.0f, z4 ? 73 : 16, 0.0f));
        this.imageView.setAlpha(this.enterAlpha);
        this.nameTextView.setAlpha(this.enterAlpha);
        this.addressTextView.setAlpha(this.enterAlpha);
    }

    public static int getColorForIndex(int i) {
        int i2 = i % 7;
        if (i2 == 0) {
            return -1351584;
        }
        if (i2 == 1) {
            return -868277;
        }
        if (i2 == 2) {
            return -12214795;
        }
        if (i2 == 3) {
            return -13187226;
        }
        if (i2 != 4) {
            return i2 != 5 ? -1285237 : -12338729;
        }
        return -7900675;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private CharSequence getTitle(TLRPC.TL_messageMediaVenue tL_messageMediaVenue) {
        if (tL_messageMediaVenue == null) {
            return "";
        }
        if (TextUtils.equals(this.lastEmoji, tL_messageMediaVenue.emoji) && TextUtils.equals(this.lastTitle, tL_messageMediaVenue.title)) {
            return this.lastCompleteTitle;
        }
        CharSequence charSequence = tL_messageMediaVenue.title;
        if (!TextUtils.isEmpty(tL_messageMediaVenue.emoji)) {
            charSequence = Emoji.replaceEmoji(tL_messageMediaVenue.emoji + " " + ((Object) charSequence), this.nameTextView.getPaint().getFontMetricsInt(), false);
        }
        this.lastEmoji = tL_messageMediaVenue.emoji;
        this.lastTitle = tL_messageMediaVenue.title;
        this.lastCompleteTitle = charSequence;
        return charSequence;
    }

    public void lambda$setLocation$0(long j, long j2, float f, float f2, ValueAnimator valueAnimator) {
        float lerp = AndroidUtilities.lerp(f, f2, j2 > 0 ? Math.min(Math.max(((float) (SystemClock.elapsedRealtime() - j)) / ((float) j2), 0.0f), 1.0f) : 1.0f);
        this.enterAlpha = lerp;
        this.imageView.setAlpha(lerp);
        this.nameTextView.setAlpha(this.enterAlpha);
        this.addressTextView.setAlpha(this.enterAlpha);
        invalidate();
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (globalGradientView == null) {
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext(), this.resourcesProvider);
            globalGradientView = flickerLoadingView;
            flickerLoadingView.setIsSingleCell(true);
        }
        globalGradientView.setParentSize(getMeasuredWidth(), getMeasuredHeight(), (-(getParent() instanceof ViewGroup ? ((ViewGroup) getParent()).indexOfChild(this) : 0)) * AndroidUtilities.dp(56.0f));
        globalGradientView.setViewType(4);
        globalGradientView.updateColors();
        globalGradientView.updateGradient();
        canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
        canvas.translate(AndroidUtilities.dp(2.0f), (getMeasuredHeight() - AndroidUtilities.dp(56.0f)) / 2);
        globalGradientView.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);
        if (this.needDivider) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Paint paint = resourcesProvider == null ? null : resourcesProvider.getPaint("paintDivider");
            if (paint == null) {
                paint = Theme.dividerPaint;
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), getHeight() - 1, LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(72.0f) : getWidth(), getHeight() - 1, paint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        if (!this.wrapContent) {
            i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setAllowTextAnimation(boolean z) {
        this.allowTextAnimation = z;
    }

    public void setLocation(TLRPC.TL_messageMediaVenue tL_messageMediaVenue, int i, boolean z) {
        setLocation(tL_messageMediaVenue, null, i, z, false);
    }

    public void setLocation(TLRPC.TL_messageMediaVenue tL_messageMediaVenue, String str, int i, boolean z, boolean z2) {
        String str2;
        this.needDivider = z;
        if (tL_messageMediaVenue != null) {
            this.nameTextView.setText(getTitle(tL_messageMediaVenue), this.allowTextAnimation && !LocaleController.isRTL && z2);
        }
        if (str != null) {
            this.addressTextView.setText(str, this.allowTextAnimation && !LocaleController.isRTL);
        } else if (tL_messageMediaVenue != null) {
            this.addressTextView.setText(tL_messageMediaVenue.address, this.allowTextAnimation && !LocaleController.isRTL && z2);
        }
        int colorForIndex = getColorForIndex(i);
        if (tL_messageMediaVenue != null && (str2 = tL_messageMediaVenue.icon) != null) {
            if ("pin".equals(str2) || tL_messageMediaVenue.icon.startsWith("emoji")) {
                Drawable mutate = getResources().getDrawable(R.drawable.pin).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), 0), mutate);
                combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
                combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                this.imageView.setImageDrawable(combinedDrawable);
            } else {
                this.imageView.setImage(tL_messageMediaVenue.icon, null, null);
            }
        }
        this.circleDrawable.getPaint().setColor(colorForIndex);
        setWillNotDraw(false);
        setClickable(tL_messageMediaVenue == null);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        boolean z3 = tL_messageMediaVenue == null;
        final float f = this.enterAlpha;
        final float f2 = z3 ? 0.0f : 1.0f;
        final long abs = Math.abs(f - f2) * 150.0f;
        this.enterAnimator = ValueAnimator.ofFloat(f, f2);
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        this.enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LocationCell.this.lambda$setLocation$0(elapsedRealtime, abs, f, f2, valueAnimator2);
            }
        });
        ValueAnimator valueAnimator2 = this.enterAnimator;
        if (z3) {
            abs = Long.MAX_VALUE;
        }
        valueAnimator2.setDuration(abs);
        this.enterAnimator.start();
        this.imageView.setAlpha(f);
        this.nameTextView.setAlpha(f);
        this.addressTextView.setAlpha(f);
        invalidate();
    }
}
