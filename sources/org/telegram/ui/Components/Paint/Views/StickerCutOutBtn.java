package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class StickerCutOutBtn extends ButtonWithCounterView {
    protected final BlurringShader.StoryBlurDrawer blurDrawer;
    protected final RectF bounds;
    public int rad;
    private final Theme.ResourcesProvider resourcesProvider;
    private int state;
    private final StickerMakerView stickerMakerView;
    private boolean wrapContent;

    public StickerCutOutBtn(StickerMakerView stickerMakerView, Context context, Theme.ResourcesProvider resourcesProvider, BlurringShader.BlurManager blurManager) {
        super(context, false, resourcesProvider);
        this.bounds = new RectF();
        this.rad = 8;
        this.resourcesProvider = resourcesProvider;
        this.stickerMakerView = stickerMakerView;
        this.blurDrawer = new BlurringShader.StoryBlurDrawer(blurManager, this, 0, true);
        setWillNotDraw(false);
        setTextColor(-1);
        setFlickeringLoading(true);
        this.text.setTypeface(AndroidUtilities.bold());
        disableRippleView();
        setForeground(Theme.createRadSelectorDrawable(Theme.multAlpha(-1, 0.08f), 8, 8));
        setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
    }

    public void clean() {
        setCutOutState(false);
    }

    public void invalidateBlur() {
        invalidate();
    }

    public boolean isCancelState() {
        return this.state == 2;
    }

    public boolean isCutOutState() {
        return this.state == 0;
    }

    public boolean isUndoCutState() {
        return this.state == 1;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (this.wrapContentDynamic) {
            float currentWidth = this.text.getCurrentWidth() + getPaddingLeft() + getPaddingRight();
            this.bounds.set((getMeasuredWidth() - currentWidth) / 2.0f, 0.0f, (getMeasuredWidth() + currentWidth) / 2.0f, getMeasuredHeight());
        } else {
            this.bounds.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        }
        super.onDraw(canvas);
    }

    @Override
    public void onMeasure(int i, int i2) {
        if (this.wrapContent) {
            i = View.MeasureSpec.makeMeasureSpec(getPaddingLeft() + ((int) this.text.getCurrentWidth()) + getPaddingRight(), 1073741824);
        }
        super.onMeasure(i, i2);
    }

    @Override
    public void setAlpha(float f) {
        if (!this.stickerMakerView.hasSegmentedBitmap()) {
            f = 0.0f;
        }
        super.setAlpha(f);
    }

    public void setCancelState(boolean z) {
        this.state = 2;
        setText(LocaleController.getString(R.string.Cancel), z);
    }

    public void setCutOutState(boolean z) {
        this.state = 0;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.media_magic_cut);
        coloredImageSpan.setSize(AndroidUtilities.dp(22.0f));
        coloredImageSpan.setTranslateX(AndroidUtilities.dp(1.0f));
        coloredImageSpan.setTranslateY(AndroidUtilities.dp(2.0f));
        coloredImageSpan.spaceScaleX = 1.2f;
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.getString(R.string.SegmentationCutObject));
        setText(spannableStringBuilder, z);
    }

    public void setEraseState(boolean z) {
        this.state = 3;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.media_button_erase);
        coloredImageSpan.setSize(AndroidUtilities.dp(20.0f));
        coloredImageSpan.setTranslateX(AndroidUtilities.dp(-3.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.getString(R.string.SegmentationErase));
        setText(spannableStringBuilder, z);
    }

    public void setOutlineState(boolean z) {
        this.state = 6;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.media_sticker_stroke);
        coloredImageSpan.setSize(AndroidUtilities.dp(20.0f));
        coloredImageSpan.setTranslateX(AndroidUtilities.dp(-3.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.getString(R.string.SegmentationOutline));
        setText(spannableStringBuilder, z);
    }

    public void setRad(int i) {
        this.rad = i;
        setForeground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, this.resourcesProvider), i, i));
    }

    public void setRestoreState(boolean z) {
        this.state = 4;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.media_button_restore);
        coloredImageSpan.setSize(AndroidUtilities.dp(20.0f));
        coloredImageSpan.setTranslateX(AndroidUtilities.dp(-3.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.getString(R.string.SegmentationRestore));
        setText(spannableStringBuilder, z);
    }

    public void setUndoCutState(boolean z) {
        this.state = 1;
    }

    public void setUndoState(boolean z) {
        this.state = 5;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.photo_undo2);
        coloredImageSpan.setSize(AndroidUtilities.dp(20.0f));
        coloredImageSpan.setTranslateX(AndroidUtilities.dp(-3.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.getString(R.string.SegmentationUndo));
        setText(spannableStringBuilder, z);
    }

    @Override
    public void setVisibility(int i) {
        if (Build.VERSION.SDK_INT < 24) {
            i = 8;
        }
        super.setVisibility(i);
    }

    public void wrapContent() {
        this.wrapContent = true;
    }
}
