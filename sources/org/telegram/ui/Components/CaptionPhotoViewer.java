package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Stories.recorder.CaptionContainerView;
import org.telegram.ui.Stories.recorder.HintView2;

public abstract class CaptionPhotoViewer extends CaptionContainerView {
    private final int SHOW_ONCE;
    private final ImageView addPhotoButton;
    private boolean addPhotoVisible;
    private final Runnable applyCaption;
    private final HintView2 hint;
    private boolean isVideo;
    private Utilities.Callback onTTLChange;
    private int timer;
    private final ImageView timerButton;
    private final CaptionContainerView.PeriodDrawable timerDrawable;
    private ItemOptions timerPopup;
    private boolean timerVisible;
    private final int[] values;

    public CaptionPhotoViewer(Context context, final FrameLayout frameLayout, SizeNotifierFrameLayout sizeNotifierFrameLayout, FrameLayout frameLayout2, Theme.ResourcesProvider resourcesProvider, BlurringShader.BlurManager blurManager, Runnable runnable) {
        super(context, frameLayout, sizeNotifierFrameLayout, frameLayout2, resourcesProvider, blurManager);
        this.timer = 0;
        this.SHOW_ONCE = Integer.MAX_VALUE;
        this.values = new int[]{Integer.MAX_VALUE, 3, 10, 30, 0};
        this.applyCaption = runnable;
        ImageView imageView = new ImageView(context);
        this.addPhotoButton = imageView;
        imageView.setImageResource(R.drawable.filled_add_photo);
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        imageView.setBackground(Theme.createSelectorDrawable(1090519039, 1, AndroidUtilities.dp(18.0f)));
        setAddPhotoVisible(false, false);
        addView(imageView, LayoutHelper.createFrame(44, 44.0f, 83, 14.0f, 0.0f, 0.0f, 10.0f));
        ImageView imageView2 = new ImageView(context);
        this.timerButton = imageView2;
        CaptionContainerView.PeriodDrawable periodDrawable = new CaptionContainerView.PeriodDrawable();
        this.timerDrawable = periodDrawable;
        imageView2.setImageDrawable(periodDrawable);
        imageView2.setBackground(Theme.createSelectorDrawable(1090519039, 1, AndroidUtilities.dp(18.0f)));
        imageView2.setScaleType(scaleType);
        setTimerVisible(false, false);
        addView(imageView2, LayoutHelper.createFrame(44, 44.0f, 85, 0.0f, 0.0f, 11.0f, 10.0f));
        HintView2 hintView2 = new HintView2(context, 3);
        this.hint = hintView2;
        hintView2.setRounding(12.0f);
        hintView2.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f));
        hintView2.setJoint(1.0f, -21.0f);
        hintView2.setMultilineText(true);
        addView(hintView2, LayoutHelper.createFrame(-1, 80, 85));
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CaptionPhotoViewer.this.lambda$new$1(frameLayout, view);
            }
        });
    }

    public void lambda$new$0(int r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CaptionPhotoViewer.lambda$new$0(int):void");
    }

    public void lambda$new$1(android.widget.FrameLayout r7, android.view.View r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CaptionPhotoViewer.lambda$new$1(android.widget.FrameLayout, android.view.View):void");
    }

    public void lambda$setAddPhotoVisible$2(boolean z) {
        if (z) {
            return;
        }
        this.timerButton.setVisibility(8);
    }

    public void lambda$setTimerVisible$3(boolean z) {
        if (z) {
            return;
        }
        this.timerButton.setVisibility(8);
    }

    @Override
    protected int additionalKeyboardHeight() {
        return 0;
    }

    @Override
    protected void afterUpdateShownKeyboard(boolean z) {
        this.timerButton.setVisibility((z || !this.timerVisible) ? 8 : 0);
        this.addPhotoButton.setVisibility((z || !this.addPhotoVisible) ? 8 : 0);
        if (z) {
            this.timerButton.setVisibility(8);
            this.addPhotoButton.setVisibility(8);
        }
    }

    @Override
    protected void beforeUpdateShownKeyboard(boolean z) {
        if (!z) {
            this.timerButton.setVisibility(this.timerVisible ? 0 : 8);
            this.addPhotoButton.setVisibility(this.addPhotoVisible ? 0 : 8);
        }
        HintView2 hintView2 = this.hint;
        if (hintView2 != null) {
            hintView2.hide();
        }
    }

    @Override
    protected boolean clipChild(View view) {
        return view != this.hint;
    }

    @Override
    protected int getCaptionDefaultLimit() {
        return MessagesController.getInstance(this.currentAccount).captionLengthLimitDefault;
    }

    @Override
    protected int getCaptionLimit() {
        return UserConfig.getInstance(this.currentAccount).isPremium() ? getCaptionPremiumLimit() : getCaptionDefaultLimit();
    }

    @Override
    protected int getCaptionPremiumLimit() {
        return MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium;
    }

    @Override
    protected int getEditTextLeft() {
        if (this.addPhotoVisible) {
            return AndroidUtilities.dp(31.0f);
        }
        return 0;
    }

    @Override
    protected int getEditTextStyle() {
        return 3;
    }

    public boolean hasTimer() {
        return this.timerVisible && this.timer > 0;
    }

    @Override
    protected void onEditHeightChange(int i) {
        this.hint.setTranslationY((-Math.min(AndroidUtilities.dp(34.0f), i)) - AndroidUtilities.dp(10.0f));
    }

    @Override
    public void lambda$new$1() {
        Runnable runnable = this.applyCaption;
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public void onUpdateShowKeyboard(float f) {
        float f2 = 1.0f - f;
        this.timerButton.setAlpha(f2);
        this.addPhotoButton.setAlpha(f2);
    }

    public void setAddPhotoVisible(final boolean z, boolean z2) {
        this.addPhotoVisible = z;
        this.addPhotoButton.animate().cancel();
        int i = 0;
        if (z2) {
            this.addPhotoButton.setVisibility(0);
            this.addPhotoButton.animate().alpha(z ? 1.0f : 0.0f).translationX(z ? 0.0f : AndroidUtilities.dp(-8.0f)).withEndAction(new Runnable() {
                @Override
                public final void run() {
                    CaptionPhotoViewer.this.lambda$setAddPhotoVisible$2(z);
                }
            }).start();
        } else {
            this.addPhotoButton.setVisibility(z ? 0 : 8);
            this.addPhotoButton.setAlpha(z ? 1.0f : 0.0f);
            this.addPhotoButton.setTranslationX(z ? 0.0f : AndroidUtilities.dp(-8.0f));
        }
        updateEditTextLeft();
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.editText.getLayoutParams();
        if (this.addPhotoVisible && this.timerVisible) {
            i = 33;
        }
        marginLayoutParams.rightMargin = AndroidUtilities.dp(12 + i);
        this.editText.setLayoutParams(marginLayoutParams);
    }

    public void setIsVideo(boolean z) {
        this.isVideo = z;
    }

    public void setOnAddPhotoClick(View.OnClickListener onClickListener) {
        this.addPhotoButton.setOnClickListener(onClickListener);
    }

    public void setOnTimerChange(Utilities.Callback<Integer> callback) {
        this.onTTLChange = callback;
    }

    public void setTimer(int i) {
        this.timer = i;
        this.timerDrawable.setValue(i == Integer.MAX_VALUE ? 1 : Math.max(1, i), this.timer > 0, true);
        HintView2 hintView2 = this.hint;
        if (hintView2 != null) {
            hintView2.hide();
        }
    }

    public void setTimerVisible(final boolean z, boolean z2) {
        this.timerVisible = z;
        this.timerButton.animate().cancel();
        int i = 0;
        if (z2) {
            this.timerButton.setVisibility(0);
            this.timerButton.animate().alpha(z ? 1.0f : 0.0f).translationX(z ? 0.0f : AndroidUtilities.dp(8.0f)).withEndAction(new Runnable() {
                @Override
                public final void run() {
                    CaptionPhotoViewer.this.lambda$setTimerVisible$3(z);
                }
            }).start();
        } else {
            this.timerButton.setVisibility(z ? 0 : 8);
            this.timerButton.setAlpha(z ? 1.0f : 0.0f);
            this.timerButton.setTranslationX(z ? 0.0f : AndroidUtilities.dp(8.0f));
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.editText.getLayoutParams();
        if (this.addPhotoVisible && this.timerVisible) {
            i = 33;
        }
        marginLayoutParams.rightMargin = AndroidUtilities.dp(12 + i);
        this.editText.setLayoutParams(marginLayoutParams);
    }

    @Override
    public void updateColors(Theme.ResourcesProvider resourcesProvider) {
        super.updateColors(resourcesProvider);
        this.timerDrawable.updateColors(-1, Theme.getColor(Theme.key_chat_editMediaButton, resourcesProvider), -1);
    }
}
