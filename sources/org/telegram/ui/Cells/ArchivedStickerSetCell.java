package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.ViewHelper;

public class ArchivedStickerSetCell extends FrameLayout implements Checkable {
    private final ProgressButton addButton;
    private AnimatorSet animatorSet;
    private final boolean checkable;
    private boolean checked;
    private Button currentButton;
    private final Button deleteButton;
    private final BackupImageView imageView;
    private boolean needDivider;
    private OnCheckedChangeListener onCheckedChangeListener;
    private TLRPC.StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z);
    }

    public ArchivedStickerSetCell(Context context, boolean z) {
        super(context);
        this.checkable = z;
        if (z) {
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            this.currentButton = progressButton;
            progressButton.setText(LocaleController.getString(R.string.Add));
            progressButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            progressButton.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
            progressButton.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
            addView(progressButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            int dp = AndroidUtilities.dp(60.0f);
            ProgressButton progressButton2 = new ProgressButton(context);
            this.deleteButton = progressButton2;
            progressButton2.setAllCaps(false);
            progressButton2.setMinWidth(dp);
            progressButton2.setMinimumWidth(dp);
            progressButton2.setTextSize(1, 14.0f);
            int i = Theme.key_featuredStickers_removeButtonText;
            progressButton2.setTextColor(Theme.getColor(i));
            progressButton2.setText(LocaleController.getString(R.string.StickersRemove));
            progressButton2.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor(i)));
            progressButton2.setTypeface(AndroidUtilities.bold());
            ViewHelper.setPadding(progressButton2, 8.0f, 0.0f, 8.0f, 0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                progressButton2.setOutlineProvider(null);
            }
            addView(progressButton2, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ArchivedStickerSetCell.this.lambda$new$0(view);
                }
            };
            progressButton.setOnClickListener(onClickListener);
            progressButton2.setOnClickListener(onClickListener);
            syncButtons(false);
        } else {
            this.addButton = null;
            this.deleteButton = null;
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        textView2.setTextSize(1, 13.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 35.0f, 21.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        backupImageView.setLayerNum(1);
        addView(backupImageView, LayoutHelper.createFrameRelatively(48.0f, 48.0f, 8388659, 12.0f, 8.0f, 0.0f, 0.0f));
    }

    public void lambda$new$0(View view) {
        toggle();
    }

    private void syncButtons(boolean z) {
        if (this.checkable) {
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            boolean z2 = this.checked;
            float f = z2 ? 1.0f : 0.0f;
            float f2 = z2 ? 0.0f : 1.0f;
            if (!z) {
                this.deleteButton.setVisibility(z2 ? 0 : 4);
                this.deleteButton.setAlpha(f);
                this.deleteButton.setScaleX(f);
                this.deleteButton.setScaleY(f);
                this.addButton.setVisibility(this.checked ? 4 : 0);
                this.addButton.setAlpha(f2);
                this.addButton.setScaleX(f2);
                this.addButton.setScaleY(f2);
                return;
            }
            this.currentButton = z2 ? this.deleteButton : this.addButton;
            this.addButton.setVisibility(0);
            this.deleteButton.setVisibility(0);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.setDuration(250L);
            AnimatorSet animatorSet3 = this.animatorSet;
            Button button = this.deleteButton;
            Property property = View.ALPHA;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(button, (Property<Button, Float>) property, f);
            Button button2 = this.deleteButton;
            Property property2 = View.SCALE_X;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(button2, (Property<Button, Float>) property2, f);
            Button button3 = this.deleteButton;
            Property property3 = View.SCALE_Y;
            animatorSet3.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(button3, (Property<Button, Float>) property3, f), ObjectAnimator.ofFloat(this.addButton, (Property<ProgressButton, Float>) property, f2), ObjectAnimator.ofFloat(this.addButton, (Property<ProgressButton, Float>) property2, f2), ObjectAnimator.ofFloat(this.addButton, (Property<ProgressButton, Float>) property3, f2));
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    (ArchivedStickerSetCell.this.currentButton == ArchivedStickerSetCell.this.addButton ? ArchivedStickerSetCell.this.deleteButton : ArchivedStickerSetCell.this.addButton).setVisibility(4);
                }
            });
            this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
            this.animatorSet.start();
        }
    }

    public TLRPC.StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    @Override
    public boolean isChecked() {
        return this.checked;
    }

    @Override
    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (this.checkable && view == this.textView) {
            i2 += Math.max(this.addButton.getMeasuredWidth(), this.deleteButton.getMeasuredWidth());
        }
        super.measureChildWithMargins(view, i, i2, i3, i4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    @Override
    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(z, z2, true);
    }

    public void setChecked(boolean z, boolean z2, boolean z3) {
        OnCheckedChangeListener onCheckedChangeListener;
        if (!this.checkable || this.checked == z) {
            return;
        }
        this.checked = z;
        syncButtons(z2);
        if (!z3 || (onCheckedChangeListener = this.onCheckedChangeListener) == null) {
            return;
        }
        onCheckedChangeListener.onCheckedChanged(this, z);
    }

    public void setDrawProgress(boolean z, boolean z2) {
        ProgressButton progressButton = this.addButton;
        if (progressButton != null) {
            progressButton.setDrawProgress(z, z2);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setStickersSet(org.telegram.tgnet.TLRPC.StickerSetCovered r13, boolean r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ArchivedStickerSetCell.setStickersSet(org.telegram.tgnet.TLRPC$StickerSetCovered, boolean):void");
    }

    @Override
    public void toggle() {
        if (this.checkable) {
            setChecked(!isChecked());
        }
    }
}
