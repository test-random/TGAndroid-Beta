package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;

public class CheckBoxCell extends FrameLayout {
    private AnimatedTextView animatedTextView;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private final View checkBox;
    private CheckBox2 checkBoxRound;
    private final int checkBoxSize;
    private CheckBoxSquare checkBoxSquare;
    private View click1Container;
    private View click2Container;
    private CollapseButton collapseButton;
    private View collapsedArrow;
    private final int currentType;
    private boolean isMultiline;
    public int itemId;
    private LinkSpanDrawable.LinksTextView linksTextView;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean textAnimated;
    private View textView;
    private final TextView valueTextView;

    public class CollapseButton extends LinearLayout {
        private final View collapsedArrow;
        private ImageView iconView;
        private final AnimatedTextView textView;

        public CollapseButton(Context context, int i) {
            super(context);
            int themedColor = CheckBoxCell.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
            if (i != 0) {
                ImageView imageView = new ImageView(context);
                this.iconView = imageView;
                imageView.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
                this.iconView.setImageResource(i);
            }
            AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, false);
            this.textView = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
            animatedTextView.setTextColor(themedColor);
            animatedTextView.setIncludeFontPadding(false);
            animatedTextView.setTypeface(AndroidUtilities.bold());
            View view = new View(context);
            this.collapsedArrow = view;
            Drawable mutate = getContext().getResources().getDrawable(R.drawable.arrow_more).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
            view.setBackground(mutate);
            if (LocaleController.isRTL) {
                addView(view, LayoutHelper.createLinear(16, 16, 16, 11, 0, 3, 0));
                addView(animatedTextView, LayoutHelper.createLinear(-2, 16, 16, 0, 0, this.iconView == null ? 11 : 3, 0));
                View view2 = this.iconView;
                if (view2 != null) {
                    addView(view2, LayoutHelper.createLinear(16, 16, 16, 0, 0, 11, 0));
                }
            } else {
                View view3 = this.iconView;
                if (view3 != null) {
                    addView(view3, LayoutHelper.createLinear(16, 16, 16, 11, 0, 3, 0));
                }
                addView(animatedTextView, LayoutHelper.createLinear(-2, 16, 16, this.iconView == null ? 11 : 0, 0, 3, 0));
                addView(view, LayoutHelper.createLinear(16, 16, 16, 0, 0, 11, 0));
            }
            setBackground(Theme.createRadSelectorDrawable(CheckBoxCell.this.getThemedColor(Theme.key_listSelector), 16, 16));
            setClickable(true);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
        }

        public void set(boolean z, CharSequence charSequence) {
            this.textView.cancelAnimation();
            this.textView.setText(charSequence);
            this.collapsedArrow.animate().cancel();
            this.collapsedArrow.animate().rotation(z ? 0.0f : 180.0f).setDuration(340L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
        }
    }

    public CheckBoxCell(Context context, int i) {
        this(context, i, 17, null);
    }

    public CheckBoxCell(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, false, resourcesProvider);
    }

    public CheckBoxCell(android.content.Context r29, int r30, int r31, boolean r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.CheckBoxCell.<init>(android.content.Context, int, int, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public CheckBoxCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, 17, resourcesProvider);
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void updateCollapseArrowTranslation() {
        float f;
        if (this.collapsedArrow == null) {
            return;
        }
        try {
            f = this.textView.getMeasuredWidth();
        } catch (Exception unused) {
            f = 0.0f;
        }
        this.collapsedArrow.setTranslationX(LocaleController.isRTL ? (this.textView.getRight() - f) - AndroidUtilities.dp(20.0f) : this.textView.getLeft() + f + AndroidUtilities.dp(4.0f));
    }

    public void allowMultiline() {
        if (this.textAnimated) {
            return;
        }
        this.linksTextView.setLines(3);
        this.linksTextView.setMaxLines(3);
        this.linksTextView.setSingleLine(false);
    }

    public AnimatedTextView getAnimatedTextView() {
        return this.animatedTextView;
    }

    public CheckBox2 getCheckBoxRound() {
        return this.checkBoxRound;
    }

    public View getCheckBoxView() {
        return this.checkBox;
    }

    public TextView getTextView() {
        return this.linksTextView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public boolean hasIcon() {
        return this.checkBoxRound.hasIcon();
    }

    public boolean isCheckboxRound() {
        int i = this.currentType;
        return i == 4 || i == 8 || i == 6 || i == 7;
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBoxRound;
        return checkBox2 != null ? checkBox2.isChecked() : this.checkBoxSquare.isChecked();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            int dp = AndroidUtilities.dp(isCheckboxRound() ? 60.0f : 20.0f) + ((int) Math.abs(this.textView.getTranslationX()));
            if (this.currentType == 7) {
                dp += AndroidUtilities.dp(39.0f);
            }
            float f = LocaleController.isRTL ? 0.0f : dp;
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (!LocaleController.isRTL) {
                dp = 0;
            }
            canvas.drawLine(f, measuredHeight, measuredWidth - dp, getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        CharSequence text;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        accessibilityNodeInfo.setCheckable(true);
        AnimatedTextView animatedTextView = this.animatedTextView;
        if (animatedTextView == null) {
            LinkSpanDrawable.LinksTextView linksTextView = this.linksTextView;
            if (linksTextView != null) {
                text = linksTextView.getText();
            }
            accessibilityNodeInfo.setChecked(isChecked());
        }
        text = animatedTextView.getText();
        accessibilityNodeInfo.setText(text);
        accessibilityNodeInfo.setChecked(isChecked());
    }

    @Override
    protected void onMeasure(int i, int i2) {
        View view;
        int makeMeasureSpec;
        int size = View.MeasureSpec.getSize(i);
        if (this.currentType == 3) {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(34.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), Integer.MIN_VALUE));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), 1073741824));
            setMeasuredDimension(this.textView.getMeasuredWidth() + AndroidUtilities.dp(29.0f), AndroidUtilities.dp(50.0f));
        } else {
            boolean z = this.isMultiline;
            int size2 = View.MeasureSpec.getSize(i);
            if (z) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(size2, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
            } else {
                setMeasuredDimension(size2, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
                int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(isCheckboxRound() ? 60.0f : 34.0f);
                if (this.textAnimated) {
                    measuredWidth += (int) this.animatedTextView.getRightPadding();
                }
                if (this.currentType == 7) {
                    measuredWidth -= AndroidUtilities.dp(34.0f);
                }
                if (this.valueTextView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    measuredWidth -= ((ViewGroup.MarginLayoutParams) this.valueTextView.getLayoutParams()).rightMargin;
                }
                int i3 = measuredWidth / 2;
                this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
                int measuredWidth2 = this.valueTextView.getMeasuredWidth();
                CollapseButton collapseButton = this.collapseButton;
                if (collapseButton != null) {
                    collapseButton.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
                    measuredWidth2 += this.collapseButton.getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                }
                if (this.textView.getLayoutParams().width == -1) {
                    view = this.textView;
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(((measuredWidth - ((int) Math.abs(view.getTranslationX()))) - measuredWidth2) - AndroidUtilities.dp(8.0f), 1073741824);
                } else {
                    view = this.textView;
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(((measuredWidth - ((int) Math.abs(view.getTranslationX()))) - measuredWidth2) - AndroidUtilities.dp(8.0f), Integer.MIN_VALUE);
                }
                view.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
                BackupImageView backupImageView = this.avatarImageView;
                if (backupImageView != null) {
                    backupImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), 1073741824));
                }
                this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.checkBoxSize), 1073741824));
            }
        }
        View view2 = this.click1Container;
        if (view2 != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view2.getLayoutParams();
            this.click1Container.measure(View.MeasureSpec.makeMeasureSpec((size - marginLayoutParams.leftMargin) - marginLayoutParams.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }
        View view3 = this.click2Container;
        if (view3 != null) {
            view3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }
        View view4 = this.collapsedArrow;
        if (view4 != null) {
            view4.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(16.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(16.0f), 1073741824));
        }
    }

    public void setCheckBoxColor(int i, int i2, int i3) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setColor(i, i, i3);
        }
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        } else {
            this.checkBoxSquare.setChecked(z, z2);
        }
    }

    public void setCollapseButton(boolean z, CharSequence charSequence, View.OnClickListener onClickListener) {
        CollapseButton collapseButton = this.collapseButton;
        if (collapseButton != null) {
            collapseButton.set(z, charSequence);
            if (onClickListener != null) {
                this.collapseButton.setOnClickListener(onClickListener);
            }
        }
    }

    public void setCollapsed(Boolean bool) {
        if (bool == null) {
            View view = this.collapsedArrow;
            if (view != null) {
                removeView(view);
                this.collapsedArrow = null;
                return;
            }
            return;
        }
        if (this.collapsedArrow == null) {
            this.collapsedArrow = new View(getContext());
            Drawable mutate = getContext().getResources().getDrawable(R.drawable.arrow_more).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
            this.collapsedArrow.setBackground(mutate);
            addView(this.collapsedArrow, LayoutHelper.createFrame(16, 16, 16));
        }
        updateCollapseArrowTranslation();
        this.collapsedArrow.animate().cancel();
        this.collapsedArrow.animate().rotation(bool.booleanValue() ? 0.0f : 180.0f).setDuration(340L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
    }

    @Override
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        this.valueTextView.setAlpha(z ? 1.0f : 0.5f);
        this.checkBox.setAlpha(z ? 1.0f : 0.5f);
    }

    public void setIcon(int i) {
        this.checkBoxRound.setIcon(i);
    }

    public void setMultiline(boolean z) {
        if (this.textAnimated) {
            return;
        }
        this.isMultiline = z;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        if (this.isMultiline) {
            this.linksTextView.setLines(0);
            this.linksTextView.setMaxLines(0);
            this.linksTextView.setSingleLine(false);
            this.linksTextView.setEllipsize(null);
        } else {
            this.linksTextView.setLines(1);
            this.linksTextView.setMaxLines(1);
            this.linksTextView.setSingleLine(true);
            this.linksTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, 0, 0, 0);
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            layoutParams2.topMargin = AndroidUtilities.dp(15.0f);
        }
        this.textView.setLayoutParams(layoutParams);
        this.checkBox.setLayoutParams(layoutParams2);
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }

    public void setOnSectionsClickListener(View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        if (onClickListener == null) {
            View view = this.click1Container;
            if (view != null) {
                removeView(view);
                this.click1Container = null;
            }
        } else {
            if (this.click1Container == null) {
                View view2 = new View(getContext());
                this.click1Container = view2;
                view2.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 2));
                addView(this.click1Container, LayoutHelper.createFrame(-1, -1, 119));
            }
            this.click1Container.setOnClickListener(onClickListener);
        }
        View view3 = this.click2Container;
        if (onClickListener2 == null) {
            if (view3 != null) {
                removeView(view3);
                this.click2Container = null;
                return;
            }
            return;
        }
        if (view3 == null) {
            View view4 = new View(getContext());
            this.click2Container = view4;
            addView(view4, LayoutHelper.createFrame(56, -1, LocaleController.isRTL ? 5 : 3));
        }
        this.click2Container.setOnClickListener(onClickListener2);
    }

    public void setPad(int i) {
        int dp = AndroidUtilities.dp(i * 40 * (LocaleController.isRTL ? -1 : 1));
        View view = this.checkBox;
        if (view != null) {
            view.setTranslationX(dp);
        }
        float f = dp;
        this.textView.setTranslationX(f);
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setTranslationX(f);
        }
        View view2 = this.click1Container;
        if (view2 != null) {
            view2.setTranslationX(f);
        }
        View view3 = this.click2Container;
        if (view3 != null) {
            view3.setTranslationX(f);
        }
    }

    public void setSquareCheckBoxColor(int i, int i2, int i3) {
        CheckBoxSquare checkBoxSquare = this.checkBoxSquare;
        if (checkBoxSquare != null) {
            checkBoxSquare.setColors(i, i2, i3);
        }
    }

    public void setText(CharSequence charSequence, String str, boolean z, boolean z2) {
        setText(charSequence, str, z, z2, false);
    }

    public void setText(CharSequence charSequence, String str, boolean z, boolean z2, boolean z3) {
        if (this.textAnimated) {
            this.animatedTextView.setText(Emoji.replaceEmoji(charSequence, this.animatedTextView.getPaint().getFontMetricsInt(), false), z3);
        } else {
            this.linksTextView.setText(charSequence);
        }
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z3);
        } else {
            this.checkBoxSquare.setChecked(z, z3);
        }
        this.valueTextView.setText(str);
        this.needDivider = z2;
        setWillNotDraw(!z2);
    }

    public void setTextColor(int i) {
        if (this.textAnimated) {
            this.animatedTextView.setTextColor(i);
        } else {
            this.linksTextView.setTextColor(i);
        }
    }

    public void setUserOrChat(TLObject tLObject) {
        this.avatarDrawable.setInfo(tLObject);
        this.avatarImageView.setForUserOrChat(tLObject, this.avatarDrawable);
        boolean z = tLObject instanceof TLRPC.User;
        String userName = z ? UserObject.getUserName((TLRPC.User) tLObject) : ContactsController.formatName(tLObject);
        if (z && ((TLRPC.User) tLObject).id == MessagesController.getInstance(UserConfig.selectedAccount).telegramAntispamUserId) {
            userName = LocaleController.getString(R.string.ChannelAntiSpamUser);
        }
        if (this.textAnimated) {
            this.animatedTextView.setText(Emoji.replaceEmoji(userName, this.animatedTextView.getPaint().getFontMetricsInt(), false));
        } else {
            this.linksTextView.setText(userName);
        }
    }

    public void updateTextColor() {
        if (this.textAnimated) {
            AnimatedTextView animatedTextView = this.animatedTextView;
            int i = this.currentType;
            animatedTextView.setTextColor(getThemedColor((i == 1 || i == 5) ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        } else {
            LinkSpanDrawable.LinksTextView linksTextView = this.linksTextView;
            int i2 = this.currentType;
            linksTextView.setTextColor(getThemedColor((i2 == 1 || i2 == 5) ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
            LinkSpanDrawable.LinksTextView linksTextView2 = this.linksTextView;
            int i3 = this.currentType;
            linksTextView2.setLinkTextColor(getThemedColor((i3 == 1 || i3 == 5) ? Theme.key_dialogTextLink : Theme.key_windowBackgroundWhiteLinkText));
        }
        TextView textView = this.valueTextView;
        int i4 = this.currentType;
        textView.setTextColor(getThemedColor((i4 == 1 || i4 == 5) ? Theme.key_dialogTextBlue : Theme.key_windowBackgroundWhiteValueText));
    }
}
