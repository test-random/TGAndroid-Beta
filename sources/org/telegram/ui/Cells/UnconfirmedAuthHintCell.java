package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UnconfirmedAuthController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BlurredFrameLayout;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.SessionsActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class UnconfirmedAuthHintCell extends BlurredFrameLayout {
    private final LinearLayout buttonsLayout;
    private int height;
    private final LinearLayout linearLayout;
    private final TextView messageTextView;
    private final TextViewWithLoading noButton;
    private final TextView titleTextView;
    private final TextViewWithLoading yesButton;

    public static class TextViewWithLoading extends TextView {
        private boolean loading;
        private final AnimatedFloat loadingT;
        private CircularProgressDrawable progressDrawable;

        public TextViewWithLoading(Context context) {
            super(context);
            this.loadingT = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float f = this.loadingT.set(this.loading);
            if (f <= 0.0f) {
                super.onDraw(canvas);
                return;
            }
            if (f < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) ((1.0f - f) * 255.0f), 31);
                float f2 = 1.0f - (0.2f * f);
                canvas.scale(f2, f2, getWidth() / 2.0f, getHeight() / 2.0f);
                canvas.translate(0.0f, AndroidUtilities.dp(-12.0f) * f);
                super.onDraw(canvas);
                canvas.restore();
            }
            if (this.progressDrawable == null) {
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(2.0f), getCurrentTextColor());
                this.progressDrawable = circularProgressDrawable;
                circularProgressDrawable.setCallback(this);
            }
            this.progressDrawable.setColor(getCurrentTextColor());
            float f3 = 1.0f - f;
            this.progressDrawable.setBounds(getWidth() / 2, (getHeight() / 2) + ((int) (AndroidUtilities.dp(12.0f) * f3)), getWidth() / 2, (getHeight() / 2) + ((int) (f3 * AndroidUtilities.dp(12.0f))));
            this.progressDrawable.setAlpha((int) (f * 255.0f));
            this.progressDrawable.draw(canvas);
            invalidate();
        }

        public void setLoading(boolean z) {
            setLoading(z, true);
        }

        public void setLoading(boolean z, boolean z2) {
            this.loading = z;
            boolean z3 = true;
            if (!z2) {
                this.loadingT.set(z, true);
            }
            if (!isPressed() && !z) {
                z3 = false;
            }
            super.setPressed(z3);
            invalidate();
        }

        @Override
        public void setPressed(boolean z) {
            super.setPressed(z || this.loading);
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return this.progressDrawable == drawable || super.verifyDrawable(drawable);
        }
    }

    public UnconfirmedAuthHintCell(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        super(context, sizeNotifierFrameLayout);
        setClickable(true);
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setGravity(17);
        textView.setTextSize(1, 14.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.UnconfirmedAuthTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 55, 28, 11, 28, 0));
        TextView textView2 = new TextView(context);
        this.messageTextView = textView2;
        textView2.setGravity(17);
        textView2.setTextSize(1, 13.0f);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 55, 28, 5, 28, 0));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.buttonsLayout = linearLayout2;
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(17);
        linearLayout2.addView(new Space(context), LayoutHelper.createLinear(-2, 1, 17.0f, 1));
        TextViewWithLoading textViewWithLoading = new TextViewWithLoading(context);
        this.yesButton = textViewWithLoading;
        textViewWithLoading.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(7.0f));
        textViewWithLoading.setTypeface(AndroidUtilities.bold());
        textViewWithLoading.setTextSize(1, 14.22f);
        textViewWithLoading.setText(LocaleController.getString(R.string.UnconfirmedAuthConfirm));
        linearLayout2.addView(textViewWithLoading, LayoutHelper.createLinear(-2, 30));
        linearLayout2.addView(new Space(context), LayoutHelper.createLinear(-2, 1, 17.0f, 1));
        TextViewWithLoading textViewWithLoading2 = new TextViewWithLoading(context);
        this.noButton = textViewWithLoading2;
        textViewWithLoading2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(7.0f));
        textViewWithLoading2.setTypeface(AndroidUtilities.bold());
        textViewWithLoading2.setTextSize(1, 14.22f);
        textViewWithLoading2.setText(LocaleController.getString(R.string.UnconfirmedAuthDeny));
        linearLayout2.addView(textViewWithLoading2, LayoutHelper.createLinear(-2, 30));
        linearLayout2.addView(new Space(context), LayoutHelper.createLinear(-2, 1, 17.0f, 1));
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 28.0f, 7.0f, 28.0f, 8.0f));
        addView(linearLayout, LayoutHelper.createFrame(-1, -1, 119));
        updateColors();
    }

    private static String from(UnconfirmedAuthController.UnconfirmedAuth unconfirmedAuth) {
        if (unconfirmedAuth == null) {
            return "";
        }
        String str = "" + unconfirmedAuth.device;
        if (!TextUtils.isEmpty(unconfirmedAuth.location) && !str.isEmpty()) {
            str = str + ", ";
        }
        return str + unconfirmedAuth.location;
    }

    public static void lambda$set$0(BaseFragment baseFragment) {
        Bulletin.hideVisible();
        baseFragment.presentFragment(new SessionsActivity(0));
    }

    public static void lambda$set$1(ArrayList arrayList) {
    }

    public static void lambda$set$2(final BaseFragment baseFragment, int i, ArrayList arrayList, View view) {
        String string = LocaleController.getString(R.string.UnconfirmedAuthConfirmedMessage);
        int i2 = Theme.key_undo_cancelColor;
        SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(string, i2, 0, new Runnable() {
            @Override
            public final void run() {
                UnconfirmedAuthHintCell.lambda$set$0(BaseFragment.this);
            }
        });
        SpannableString spannableString = new SpannableString(">");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.attach_arrow_right);
        coloredImageSpan.setOverrideColor(Theme.getColor(i2));
        coloredImageSpan.setScale(0.7f, 0.7f);
        coloredImageSpan.setWidth(AndroidUtilities.dp(12.0f));
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length(), 33);
        AndroidUtilities.replaceCharSequence(">", replaceSingleTag, spannableString);
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.UnconfirmedAuthConfirmed), replaceSingleTag).show();
        MessagesController.getInstance(i).getUnconfirmedAuthController().confirm(arrayList, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                UnconfirmedAuthHintCell.lambda$set$1((ArrayList) obj);
            }
        });
        MessagesController.getInstance(i).getUnconfirmedAuthController().cleanup();
    }

    public void lambda$set$3(int i, ArrayList arrayList) {
        if (LaunchActivity.isActive) {
            showLoginPreventedSheet(arrayList);
        }
        this.noButton.setLoading(false);
        MessagesController.getInstance(i).getUnconfirmedAuthController().cleanup();
    }

    public void lambda$set$4(final int i, ArrayList arrayList, View view) {
        this.noButton.setLoading(true);
        MessagesController.getInstance(i).getUnconfirmedAuthController().deny(arrayList, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                UnconfirmedAuthHintCell.this.lambda$set$3(i, (ArrayList) obj);
            }
        });
    }

    public static void lambda$showLoginPreventedSheet$5(BottomSheet bottomSheet) {
        bottomSheet.setCanDismissWithSwipe(true);
        bottomSheet.setCanDismissWithTouchOutside(true);
    }

    public static void lambda$showLoginPreventedSheet$6(ButtonWithCounterView buttonWithCounterView, BottomSheet bottomSheet, View view) {
        if (!buttonWithCounterView.isTimerActive()) {
            bottomSheet.dismiss();
        } else {
            AndroidUtilities.shakeViewSpring(buttonWithCounterView, 3.0f);
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        if (size <= 0) {
            size = AndroidUtilities.displaySize.x;
        }
        this.linearLayout.measure(View.MeasureSpec.makeMeasureSpec((size - getPaddingLeft()) - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        int measuredHeight = this.linearLayout.getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + 1;
        this.height = measuredHeight;
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824));
    }

    public void set(final BaseFragment baseFragment, final int i) {
        TextView textView;
        String formatPluralString;
        final ArrayList<UnconfirmedAuthController.UnconfirmedAuth> arrayList = MessagesController.getInstance(i).getUnconfirmedAuthController().auths;
        this.titleTextView.setText(LocaleController.getString(R.string.UnconfirmedAuthTitle));
        this.yesButton.setText(LocaleController.getString(R.string.UnconfirmedAuthConfirm));
        this.yesButton.setLoading(false, false);
        this.noButton.setText(LocaleController.getString(R.string.UnconfirmedAuthDeny));
        this.noButton.setLoading(false, false);
        if (arrayList == null || arrayList.size() != 1) {
            if (arrayList != null && arrayList.size() > 1) {
                String str = arrayList.get(0).location;
                int i2 = 1;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    if (!TextUtils.equals(str, arrayList.get(i2).location)) {
                        str = null;
                        break;
                    }
                    i2++;
                }
                if (str == null) {
                    this.messageTextView.setText(LocaleController.formatPluralString("UnconfirmedAuthMultiple", arrayList.size(), new Object[0]));
                } else {
                    textView = this.messageTextView;
                    formatPluralString = LocaleController.formatPluralString("UnconfirmedAuthMultipleFrom", arrayList.size(), str);
                }
            }
            this.yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    UnconfirmedAuthHintCell.lambda$set$2(BaseFragment.this, i, arrayList, view);
                }
            });
            this.noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    UnconfirmedAuthHintCell.this.lambda$set$4(i, arrayList, view);
                }
            });
        }
        String str2 = "" + arrayList.get(0).device;
        if (!TextUtils.isEmpty(arrayList.get(0).location) && !str2.isEmpty()) {
            str2 = str2 + ", ";
        }
        String str3 = str2 + arrayList.get(0).location;
        textView = this.messageTextView;
        formatPluralString = LocaleController.formatString(R.string.UnconfirmedAuthSingle, str3);
        textView.setText(formatPluralString);
        this.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                UnconfirmedAuthHintCell.lambda$set$2(BaseFragment.this, i, arrayList, view);
            }
        });
        this.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                UnconfirmedAuthHintCell.this.lambda$set$4(i, arrayList, view);
            }
        });
    }

    public void showLoginPreventedSheet(ArrayList arrayList) {
        String formatString;
        if (arrayList == null || arrayList.size() == 0) {
            BulletinFactory.of(Bulletin.BulletinWindow.make(getContext()), null).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
            return;
        }
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(getContext());
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        rLottieImageView.setAnimation(R.raw.ic_ban, 50, 50);
        rLottieImageView.playAnimation();
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_windowBackgroundWhiteValueText)));
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(80, 80, 17, 0, 14, 0, 0));
        TextView textView = new TextView(getContext());
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        textView.setText(LocaleController.formatPluralString("UnconfirmedAuthDeniedTitle", arrayList.size(), new Object[0]));
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 28.0f, 14.0f, 28.0f, 0.0f));
        TextView textView2 = new TextView(getContext());
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        if (arrayList.size() == 1) {
            formatString = LocaleController.formatString(R.string.UnconfirmedAuthDeniedMessageSingle, from((UnconfirmedAuthController.UnconfirmedAuth) arrayList.get(0)));
        } else {
            String str = "\n";
            for (int i = 0; i < Math.min(arrayList.size(), 10); i++) {
                str = str + "• " + from((UnconfirmedAuthController.UnconfirmedAuth) arrayList.get(i)) + "\n";
            }
            formatString = LocaleController.formatString(R.string.UnconfirmedAuthDeniedMessageMultiple, str);
        }
        textView2.setText(formatString);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 40.0f, 9.0f, 40.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        int dp = AndroidUtilities.dp(8.0f);
        int i2 = Theme.key_text_RedBold;
        frameLayout.setBackground(Theme.createRoundRectDrawable(dp, Theme.multAlpha(Theme.getColor(i2), Theme.isCurrentThemeDark() ? 0.2f : 0.15f)));
        TextView textView3 = new TextView(getContext());
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setTextSize(1, 14.0f);
        textView3.setGravity(17);
        textView3.setTextColor(Theme.getColor(i2));
        textView3.setText(LocaleController.getString(R.string.UnconfirmedAuthDeniedWarning));
        frameLayout.addView(textView3, LayoutHelper.createFrame(-1, -1, 119));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 14.0f, 19.0f, 14.0f, 0.0f));
        final ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), null);
        ScaleStateListAnimator.apply(buttonWithCounterView, 0.02f, 1.5f);
        buttonWithCounterView.setText(LocaleController.getString(R.string.GotIt), false);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 14.0f, 20.0f, 14.0f, 4.0f));
        final BottomSheet show = new BottomSheet.Builder(getContext()).setCustomView(linearLayout).show();
        show.setCanDismissWithSwipe(false);
        show.setCanDismissWithTouchOutside(false);
        buttonWithCounterView.setTimer(5, new Runnable() {
            @Override
            public final void run() {
                UnconfirmedAuthHintCell.lambda$showLoginPreventedSheet$5(BottomSheet.this);
            }
        });
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                UnconfirmedAuthHintCell.lambda$showLoginPreventedSheet$6(ButtonWithCounterView.this, show, view);
            }
        });
    }

    public void updateColors() {
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        TextViewWithLoading textViewWithLoading = this.yesButton;
        int i = Theme.key_windowBackgroundWhiteValueText;
        textViewWithLoading.setTextColor(Theme.getColor(i));
        this.yesButton.setBackground(Theme.createSelectorDrawable(Theme.multAlpha(Theme.getColor(i), Theme.isCurrentThemeDark() ? 0.3f : 0.15f), 7, AndroidUtilities.dp(8.0f)));
        TextViewWithLoading textViewWithLoading2 = this.noButton;
        int i2 = Theme.key_text_RedBold;
        textViewWithLoading2.setTextColor(Theme.getColor(i2));
        this.noButton.setBackground(Theme.createSelectorDrawable(Theme.multAlpha(Theme.getColor(i2), Theme.isCurrentThemeDark() ? 0.3f : 0.15f), 7, AndroidUtilities.dp(8.0f)));
    }
}
