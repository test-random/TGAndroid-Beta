package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChecksHintView;

public class ChecksHintView extends FrameLayout {
    private AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private View currentView;
    private Runnable hideRunnable;
    private RLottieImageView[] imageView;
    private ChatMessageCell messageCell;
    private final Theme.ResourcesProvider resourcesProvider;
    private long showingDuration;
    private TextView[] textView;
    private float translationY;

    public class AnonymousClass1 extends AnimatorListenerAdapter {
        AnonymousClass1() {
        }

        public void lambda$onAnimationEnd$0() {
            ChecksHintView.this.hide();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ChecksHintView.this.animatorSet = null;
            AndroidUtilities.runOnUIThread(ChecksHintView.this.hideRunnable = new Runnable() {
                @Override
                public final void run() {
                    ChecksHintView.AnonymousClass1.this.lambda$onAnimationEnd$0();
                }
            }, 3000L);
        }
    }

    public ChecksHintView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        TextView textView;
        int i;
        this.textView = new TextView[2];
        this.imageView = new RLottieImageView[2];
        this.showingDuration = 2000L;
        this.resourcesProvider = resourcesProvider;
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_chat_gifSaveHintBackground)));
        int i2 = 0;
        frameLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        addView(frameLayout, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 6.0f));
        while (i2 < 2) {
            this.imageView[i2] = new RLottieImageView(context);
            this.imageView[i2].setScaleType(ImageView.ScaleType.CENTER);
            frameLayout.addView(this.imageView[i2], LayoutHelper.createFrame(24, 24.0f, 51, 0.0f, i2 == 0 ? 0.0f : 24.0f, 0.0f, 0.0f));
            this.textView[i2] = new TextView(context);
            this.textView[i2].setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
            this.textView[i2].setTextSize(1, 14.0f);
            this.textView[i2].setMaxLines(1);
            this.textView[i2].setSingleLine(true);
            this.textView[i2].setMaxWidth(AndroidUtilities.dp(250.0f));
            this.textView[i2].setGravity(51);
            this.textView[i2].setPivotX(0.0f);
            frameLayout.addView(this.textView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 32.0f, i2 == 0 ? 2.0f : 26.0f, 10.0f, 0.0f));
            RLottieImageView[] rLottieImageViewArr = this.imageView;
            if (i2 == 0) {
                rLottieImageViewArr[i2].setAnimation(R.raw.ticks_single, 24, 24);
                textView = this.textView[i2];
                i = R.string.HintSent;
            } else {
                rLottieImageViewArr[i2].setAnimation(R.raw.ticks_double, 24, 24);
                textView = this.textView[i2];
                i = R.string.HintRead;
            }
            textView.setText(LocaleController.getString(i));
            this.imageView[i2].playAnimation();
            i2++;
        }
        ImageView imageView = new ImageView(context);
        this.arrowImageView = imageView;
        imageView.setImageResource(R.drawable.tooltip_arrow);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_gifSaveHintBackground), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public void hide() {
        if (getTag() == null) {
            return;
        }
        setTag(null);
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<ChecksHintView, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this, (Property<ChecksHintView, Float>) View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this, (Property<ChecksHintView, Float>) View.SCALE_Y, 0.0f));
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ChecksHintView.this.setVisibility(4);
                ChecksHintView.this.currentView = null;
                ChecksHintView.this.messageCell = null;
                ChecksHintView.this.animatorSet = null;
            }
        });
        this.animatorSet.setDuration(180L);
        this.animatorSet.start();
    }

    public boolean showForMessageCell(org.telegram.ui.Cells.ChatMessageCell r13, boolean r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChecksHintView.showForMessageCell(org.telegram.ui.Cells.ChatMessageCell, boolean):boolean");
    }
}
