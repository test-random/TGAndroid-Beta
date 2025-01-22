package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.NotificationPermissionDialog;

public class NotificationPermissionDialog extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private CounterView counterView;
    private RLottieImageView rLottieImageView;
    private long showTime;
    private Utilities.Callback whenGranted;

    public static class CounterView extends View {
        private final AnimatedFloat alpha;
        private ValueAnimator countAnimator;
        private float countScale;
        private final Paint fillPaint;
        private int lastCount;
        private final Paint strokePaint;
        AnimatedTextView.AnimatedTextDrawable textDrawable;

        public CounterView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.fillPaint = paint;
            Paint paint2 = new Paint(1);
            this.strokePaint = paint2;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.alpha = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
            this.textDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            this.countScale = 1.0f;
            paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
            paint2.setColor(Theme.getColor(Theme.key_dialogBackground));
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setStrokeWidth(AndroidUtilities.dp(4.0f));
            this.textDrawable.setCallback(this);
            this.textDrawable.setAnimationProperties(0.35f, 0L, 200L, cubicBezierInterpolator);
            this.textDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            this.textDrawable.getPaint().setStrokeWidth(AndroidUtilities.dp(0.24f));
            this.textDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);
            this.textDrawable.setTextSize(AndroidUtilities.dp(13.3f));
            this.textDrawable.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.textDrawable.setOverrideFullWidth(AndroidUtilities.dp(64.0f));
            this.textDrawable.setGravity(1);
        }

        private void animateBounce() {
            ValueAnimator valueAnimator = this.countAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.countAnimator = null;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.countAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NotificationPermissionDialog.CounterView.this.lambda$animateBounce$0(valueAnimator2);
                }
            });
            this.countAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    CounterView.this.countScale = 1.0f;
                    CounterView.this.invalidate();
                }
            });
            this.countAnimator.setInterpolator(new OvershootInterpolator(2.0f));
            this.countAnimator.setDuration(200L);
            this.countAnimator.start();
        }

        public void lambda$animateBounce$0(ValueAnimator valueAnimator) {
            this.countScale = Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float f = this.alpha.set(this.lastCount > 0 ? 1.0f : 0.0f);
            canvas.save();
            float f2 = this.countScale * f;
            canvas.scale(f2, f2, getWidth() / 2.0f, getHeight() / 2.0f);
            float currentWidth = this.textDrawable.getCurrentWidth() + AndroidUtilities.dpf2(12.66f);
            float dpf2 = AndroidUtilities.dpf2(20.3f);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set((getWidth() - currentWidth) / 2.0f, (getHeight() - dpf2) / 2.0f, (getWidth() + currentWidth) / 2.0f, (getHeight() + dpf2) / 2.0f);
            int i = (int) (f * 255.0f);
            this.strokePaint.setAlpha(i);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), this.strokePaint);
            this.fillPaint.setAlpha(i);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), this.fillPaint);
            canvas.save();
            canvas.translate(0.0f, -AndroidUtilities.dp(1.0f));
            this.textDrawable.setBounds(0, 0, getWidth(), getHeight());
            this.textDrawable.draw(canvas);
            canvas.restore();
            canvas.restore();
        }

        public boolean setCount(int i) {
            int i2 = this.lastCount;
            if (i2 != i) {
                r1 = i2 < i;
                this.lastCount = i;
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.textDrawable;
                String str = "";
                if (i > 0) {
                    str = "" + this.lastCount;
                }
                animatedTextDrawable.setText(str, true);
                if (r1) {
                    animateBounce();
                }
            }
            return r1;
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return drawable == this.textDrawable || super.verifyDrawable(drawable);
        }
    }

    private static class SectionView extends FrameLayout {
        public SectionView(Context context, int i, CharSequence charSequence) {
            super(context);
            setPadding(0, AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f));
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(i);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            int i2 = Theme.key_dialogTextBlack;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), PorterDuff.Mode.MULTIPLY));
            boolean z = LocaleController.isRTL;
            addView(imageView, LayoutHelper.createFrame(24, 24.0f, (z ? 5 : 3) | 16, z ? 0.0f : 22.0f, 0.0f, z ? 22.0f : 0.0f, 0.0f));
            TextView textView = new TextView(context);
            textView.setTextColor(Theme.getColor(i2));
            textView.setTextSize(1, 14.0f);
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView.setText(charSequence);
            boolean z2 = LocaleController.isRTL;
            addView(textView, LayoutHelper.createFrame(-1, -2.0f, 23, z2 ? 0.0f : 61.0f, 0.0f, z2 ? 61.0f : 0.0f, 0.0f));
        }
    }

    public NotificationPermissionDialog(Context context, boolean z, Utilities.Callback callback) {
        super(context, false);
        this.whenGranted = callback;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.rLottieImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.rLottieImageView.setAnimation(R.raw.silent_unmute, 46, 46);
        this.rLottieImageView.playAnimation();
        RLottieImageView rLottieImageView2 = this.rLottieImageView;
        int dp = AndroidUtilities.dp(72.0f);
        int i = Theme.key_featuredStickers_addButton;
        rLottieImageView2.setBackground(Theme.createCircleDrawable(dp, Theme.getColor(i)));
        frameLayout.addView(this.rLottieImageView, LayoutHelper.createFrame(72, 72, 17));
        CounterView counterView = new CounterView(context);
        this.counterView = counterView;
        frameLayout.addView(counterView, LayoutHelper.createFrame(64, 32.0f, 49, 29.0f, 16.0f, 0.0f, 0.0f));
        this.counterView.setCount(0);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                NotificationPermissionDialog.this.lambda$new$0(view);
            }
        });
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 110));
        TextView textView = new TextView(context);
        int i2 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i2));
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 20.0f);
        textView.setGravity(1);
        textView.setText(LocaleController.getString(R.string.NotificationsPermissionAlertTitle));
        textView.setPadding(AndroidUtilities.dp(30.0f), 0, AndroidUtilities.dp(30.0f), 0);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor(i2));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(1);
        textView2.setText(LocaleController.getString(R.string.NotificationsPermissionAlertSubtitle));
        textView2.setPadding(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(21.0f));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(new SectionView(context, R.drawable.msg_message_s, LocaleController.getString(R.string.NotificationsPermissionAlert1)), LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(new SectionView(context, R.drawable.msg_members_list2, LocaleController.getString(R.string.NotificationsPermissionAlert2)), LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(new SectionView(context, R.drawable.msg_customize_s, LocaleController.getString(R.string.NotificationsPermissionAlert3)), LayoutHelper.createLinear(-1, -2));
        setCustomView(linearLayout);
        fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
        TextView textView3 = new TextView(context);
        textView3.setText(LocaleController.getString(z ? R.string.NotificationsPermissionSettings : R.string.NotificationsPermissionContinue));
        textView3.setGravity(17);
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        textView3.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(i), 8.0f));
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                NotificationPermissionDialog.this.lambda$new$1(view);
            }
        });
        linearLayout.addView(textView3, LayoutHelper.createLinear(-1, 48, 14.0f, 14.0f, 14.0f, 10.0f));
        for (int i3 = 0; i3 < 4; i3++) {
            try {
                NotificationCenter.getInstance(i3).addObserver(this, NotificationCenter.updateInterfaces);
            } catch (Exception unused) {
            }
        }
    }

    public static void askLater() {
        long j = MessagesController.getGlobalMainSettings().getLong("askNotificationsDuration", 86400000L);
        long currentTimeMillis = System.currentTimeMillis() + j;
        long j2 = 259200000;
        if (j >= 259200000) {
            j2 = 604800000;
            if (j >= 604800000) {
                j2 = 2592000000L;
            }
        }
        MessagesController.getGlobalMainSettings().edit().putLong("askNotificationsAfter", currentTimeMillis).putLong("askNotificationsDuration", j2).apply();
    }

    public void lambda$new$0(View view) {
        if (this.rLottieImageView.isPlaying()) {
            return;
        }
        this.rLottieImageView.setProgress(0.0f);
        this.rLottieImageView.playAnimation();
    }

    public void lambda$new$1(View view) {
        Utilities.Callback callback = this.whenGranted;
        if (callback != null) {
            callback.run(Boolean.TRUE);
            this.whenGranted = null;
        }
        lambda$new$0();
    }

    public static boolean shouldAsk(Activity activity) {
        int checkSelfPermission;
        if (activity == null || Build.VERSION.SDK_INT < 23) {
            return false;
        }
        checkSelfPermission = activity.checkSelfPermission("android.permission.POST_NOTIFICATIONS");
        if (checkSelfPermission == 0) {
            return false;
        }
        long j = MessagesController.getGlobalMainSettings().getLong("askNotificationsAfter", -1L);
        if (j != -2) {
            return j < 0 || System.currentTimeMillis() >= j;
        }
        return false;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.updateInterfaces || (((Integer) objArr[0]).intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) < 0) {
            return;
        }
        updateCounter();
    }

    @Override
    public void lambda$new$0() {
        super.lambda$new$0();
        Utilities.Callback callback = this.whenGranted;
        if (callback != null) {
            callback.run(Boolean.FALSE);
            this.whenGranted = null;
            askLater();
        }
        for (int i = 0; i < 4; i++) {
            try {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
            } catch (Exception unused) {
            }
        }
    }

    @Override
    public void show() {
        super.show();
        this.showTime = System.currentTimeMillis();
    }

    public void updateCounter() {
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            MessagesStorage messagesStorage = MessagesStorage.getInstance(i2);
            if (messagesStorage != null) {
                i += messagesStorage.getMainUnreadCount();
            }
        }
        if (!this.counterView.setCount(i) || this.rLottieImageView.isPlaying()) {
            return;
        }
        this.rLottieImageView.setProgress(0.0f);
        this.rLottieImageView.playAnimation();
    }
}
