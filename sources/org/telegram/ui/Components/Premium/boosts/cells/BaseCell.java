package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;

public abstract class BaseCell extends FrameLayout {
    protected final AvatarDrawable avatarDrawable;
    protected View backgroundView;
    protected final Paint dividerPaint;
    protected final BackupImageView imageView;
    protected boolean needDivider;
    protected final RadioButton radioButton;
    protected final Theme.ResourcesProvider resourcesProvider;
    public final SimpleTextView subtitleTextView;
    protected final SimpleTextView titleTextView;

    public BaseCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        this.dividerPaint = new Paint(1);
        this.resourcesProvider = resourcesProvider;
        View view = new View(context);
        this.backgroundView = view;
        addView(view, LayoutHelper.createLinear(-1, -1));
        this.backgroundView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
        avatarDrawable.setRoundRadius(AndroidUtilities.dp(40.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        addView(backupImageView);
        SimpleTextView simpleTextView = new SimpleTextView(context) {
            @Override
            public boolean setText(CharSequence charSequence) {
                return super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false));
            }
        };
        this.titleTextView = simpleTextView;
        NotificationCenter.listenEmojiLoading(simpleTextView);
        NotificationCenter.listenEmojiLoading(backupImageView);
        simpleTextView.setTextSize(16);
        int i = Theme.key_dialogTextBlack;
        simpleTextView.setTextColor(Theme.getColor(i, resourcesProvider));
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(simpleTextView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.subtitleTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        simpleTextView2.setTextColor(Theme.getColor(i, resourcesProvider));
        simpleTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(simpleTextView2);
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        radioButton.setColor(Theme.getColor(Theme.key_checkboxDisabled, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
        addView(radioButton);
        updateLayouts();
        if (needCheck()) {
            return;
        }
        radioButton.setVisibility(8);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.needDivider) {
            this.dividerPaint.setColor(Theme.getColor(Theme.key_divider, this.resourcesProvider));
            int i = needCheck() ? 105 : 70;
            if (this.imageView.getVisibility() == 8) {
                i -= 40;
            }
            int dividerPadding = i + dividerPadding();
            if (LocaleController.isRTL) {
                canvas.drawRect(0.0f, getHeight() - 1, getWidth() - AndroidUtilities.dp(dividerPadding), getHeight(), this.dividerPaint);
            } else {
                canvas.drawRect(AndroidUtilities.dp(dividerPadding), getHeight() - 1, getWidth(), getHeight(), this.dividerPaint);
            }
        }
    }

    protected int dividerPadding() {
        return 0;
    }

    protected int getFullHeight() {
        return 56;
    }

    public void markChecked(RecyclerListView recyclerListView) {
        if (needCheck()) {
            for (int i = 0; i < recyclerListView.getChildCount(); i++) {
                View childAt = recyclerListView.getChildAt(i);
                if (childAt.getClass().isInstance(this)) {
                    ((BaseCell) childAt).setChecked(childAt == this, true);
                }
            }
        }
    }

    protected abstract boolean needCheck();

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(getFullHeight()), 1073741824));
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.radioButton.getVisibility() == 0) {
            this.radioButton.setChecked(z, z2);
        }
    }

    public void setDivider(boolean z) {
        this.needDivider = z;
        invalidate();
    }

    public void setSubtitle(CharSequence charSequence) {
        SimpleTextView simpleTextView;
        float f;
        if (TextUtils.isEmpty(charSequence)) {
            this.titleTextView.setTranslationY(0.0f);
            this.subtitleTextView.setVisibility(8);
        } else {
            this.titleTextView.setTranslationY(AndroidUtilities.dp(-9.0f));
            this.subtitleTextView.setTranslationY(AndroidUtilities.dp(12.0f));
            this.subtitleTextView.setText(charSequence);
            this.subtitleTextView.setVisibility(0);
        }
        if (this.imageView.getVisibility() == 8) {
            if (LocaleController.isRTL) {
                simpleTextView = this.titleTextView;
                f = 40.0f;
            } else {
                simpleTextView = this.titleTextView;
                f = -40.0f;
            }
            simpleTextView.setTranslationX(AndroidUtilities.dp(f));
            this.subtitleTextView.setTranslationX(AndroidUtilities.dp(f));
        }
    }

    public void updateLayouts() {
        float f;
        float f2;
        float f3;
        float f4;
        this.imageView.setLayoutParams(LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 16, needCheck() ? 53.0f : 16.0f, 0.0f, needCheck() ? 53.0f : 16.0f, 0.0f));
        SimpleTextView simpleTextView = this.titleTextView;
        boolean z = LocaleController.isRTL;
        int i = (z ? 5 : 3) | 16;
        if (z) {
            f = 20.0f;
        } else {
            f = needCheck() ? 105 : 70;
        }
        if (LocaleController.isRTL) {
            f2 = needCheck() ? 105 : 70;
        } else {
            f2 = 20.0f;
        }
        simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i, f, 0.0f, f2, 0.0f));
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        boolean z2 = LocaleController.isRTL;
        int i2 = (z2 ? 5 : 3) | 16;
        if (z2) {
            f3 = 20.0f;
        } else {
            f3 = needCheck() ? 105 : 70;
        }
        if (LocaleController.isRTL) {
            f4 = needCheck() ? 105 : 70;
        } else {
            f4 = 20.0f;
        }
        simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i2, f3, 0.0f, f4, 0.0f));
        RadioButton radioButton = this.radioButton;
        boolean z3 = LocaleController.isRTL;
        radioButton.setLayoutParams(LayoutHelper.createFrame(22, 22.0f, (z3 ? 5 : 3) | 16, z3 ? 15.0f : 20.0f, 0.0f, z3 ? 20.0f : 15.0f, 0.0f));
    }

    public CharSequence withArrow(CharSequence charSequence) {
        SpannableString spannableString = new SpannableString(">");
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.attach_arrow_right);
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(drawable, 2);
        drawable.setBounds(0, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(12.0f));
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length(), 33);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(charSequence).append((CharSequence) " ").append((CharSequence) spannableString);
        return spannableStringBuilder;
    }
}
