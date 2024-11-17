package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.RadioButton;

public class StarGiveawayOptionCell extends FrameLayout {
    private final AnimatedFloat animatedStarsCount;
    private TL_stars.TL_starsGiveawayOption currentOption;
    private long currentOptionStarsPerUser;
    private SpannableString loading1;
    private SpannableString loading2;
    private TextView priceView;
    private RadioButton radioButton;
    private final Theme.ResourcesProvider resourcesProvider;
    private final Drawable starDrawable;
    private final Drawable starDrawableOutline;
    private int starsCount;
    private AnimatedTextView subtitleView;
    private AnimatedTextView titleView;

    public StarGiveawayOptionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.animatedStarsCount = new AnimatedFloat(this, 0L, 500L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.resourcesProvider = resourcesProvider;
        Drawable mutate = context.getResources().getDrawable(R.drawable.star_small_outline).mutate();
        this.starDrawableOutline = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground, resourcesProvider), PorterDuff.Mode.SRC_IN));
        this.starDrawable = context.getResources().getDrawable(R.drawable.star_small_inner).mutate();
        setWillNotDraw(false);
        AnimatedTextView animatedTextView = new AnimatedTextView(context);
        this.titleView = animatedTextView;
        animatedTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.titleView.setTypeface(AndroidUtilities.bold());
        this.titleView.setTextSize(AndroidUtilities.dp(16.0f));
        addView(this.titleView, LayoutHelper.createFrame(-1, 20.0f, 51, 64.0f, 8.0f, 80.0f, 0.0f));
        SpannableString spannableString = new SpannableString("x");
        this.loading1 = spannableString;
        spannableString.setSpan(new LoadingSpan(this.titleView, AndroidUtilities.dp(90.0f)), 0, 1, 33);
        AnimatedTextView animatedTextView2 = new AnimatedTextView(context, false, true, true);
        this.subtitleView = animatedTextView2;
        int i = Theme.key_windowBackgroundWhiteGrayText2;
        animatedTextView2.setTextColor(Theme.getColor(i, resourcesProvider));
        this.subtitleView.setTextSize(AndroidUtilities.dp(13.0f));
        addView(this.subtitleView, LayoutHelper.createFrame(-1, 14.0f, 51, 64.0f, 31.0f, 80.0f, 0.0f));
        SpannableString spannableString2 = new SpannableString("x");
        this.loading2 = spannableString2;
        spannableString2.setSpan(new LoadingSpan(this.subtitleView, AndroidUtilities.dp(70.0f)), 0, 1, 33);
        TextView textView = new TextView(context);
        this.priceView = textView;
        textView.setTextColor(Theme.getColor(i, resourcesProvider));
        this.priceView.setTextSize(1, 16.0f);
        this.priceView.setGravity(5);
        addView(this.priceView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor(Theme.key_checkboxDisabled, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
        addView(this.radioButton, LayoutHelper.createFrame(20, 20.0f, 19, 22.0f, 0.0f, 0.0f, 0.0f));
    }

    public TL_stars.TL_starsGiveawayOption getOption() {
        return this.currentOption;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = this.animatedStarsCount.set(this.starsCount);
        float dp = AndroidUtilities.dp(24.0f);
        float dp2 = AndroidUtilities.dp(24.0f);
        float dp3 = AndroidUtilities.dp(2.5f);
        float dp4 = AndroidUtilities.dp(64.0f);
        float dp5 = AndroidUtilities.dp(8.0f);
        for (int ceil = ((int) Math.ceil(f)) - 1; ceil >= 0; ceil--) {
            float clamp = Utilities.clamp(f - ceil, 1.0f, 0.0f);
            float f2 = (((ceil - 1) - (1.0f - clamp)) * dp3 * 1.0f) + dp4;
            int i = (int) f2;
            int i2 = (int) dp5;
            int i3 = (int) (f2 + dp);
            int i4 = (int) (dp5 + dp2);
            this.starDrawableOutline.setBounds(i, i2, i3, i4);
            int i5 = (int) (clamp * 255.0f);
            this.starDrawableOutline.setAlpha(i5);
            this.starDrawableOutline.draw(canvas);
            this.starDrawable.setBounds(i, i2, i3, i4);
            this.starDrawable.setAlpha(i5);
            this.starDrawable.draw(canvas);
        }
        this.titleView.setTranslationX(AndroidUtilities.dp(22.0f) + (dp3 * f));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824));
    }

    public void setOption(TL_stars.TL_starsGiveawayOption tL_starsGiveawayOption, int i, long j, boolean z, boolean z2) {
        boolean z3 = this.currentOption == tL_starsGiveawayOption;
        this.radioButton.setChecked(z, z3);
        this.currentOption = tL_starsGiveawayOption;
        this.currentOptionStarsPerUser = j;
        if (z3) {
            this.subtitleView.cancelAnimation();
        }
        if (tL_starsGiveawayOption == null) {
            this.titleView.setText(this.loading1, false);
            this.subtitleView.setText(this.loading2, z3);
            this.priceView.setText("");
        } else {
            this.titleView.setText(LocaleController.formatPluralStringComma("GiveawayStars", (int) tL_starsGiveawayOption.stars, ' '), false);
            this.subtitleView.setText(LocaleController.formatPluralStringComma("BoostingStarOptionPerUser", (int) j, ','), z3);
            this.priceView.setText(BillingController.getInstance().formatCurrency(tL_starsGiveawayOption.amount, tL_starsGiveawayOption.currency));
        }
        int i2 = i + 1;
        this.starsCount = i2;
        if (!z3) {
            this.animatedStarsCount.set(i2, true);
        }
        invalidate();
    }
}
