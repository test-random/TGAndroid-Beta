package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SuggestEmojiView;

public class PollEditTextCell extends FrameLayout implements SuggestEmojiView.AnchorViewDelegate {
    private boolean alwaysShowText2;
    private CheckBox2 checkBox;
    private AnimatorSet checkBoxAnimation;
    private ImageView deleteImageView;
    private ChatActivityEnterViewAnimatedIconView emojiButton;
    private ImageView moveImageView;
    private boolean needDivider;
    private boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;
    private ValueAnimator valueAnimator;

    public PollEditTextCell(Context context, View.OnClickListener onClickListener) {
        this(context, false, 0, onClickListener);
    }

    public PollEditTextCell(Context context, boolean z, int i, View.OnClickListener onClickListener) {
        super(context);
        EditTextCaption editTextCaption = new EditTextCaption(context, null) {
            @Override
            protected int emojiCacheType() {
                return 3;
            }

            @Override
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
                if (PollEditTextCell.this.showNextButton) {
                    editorInfo.imeOptions &= -1073741825;
                }
                return onCreateInputConnection;
            }

            @Override
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                PollEditTextCell.this.onEditTextDraw(this, canvas);
            }

            @Override
            public void onFocusChanged(boolean z2, int i2, Rect rect) {
                super.onFocusChanged(z2, i2, rect);
                PollEditTextCell.this.onEditTextFocusChanged(z2);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!isEnabled()) {
                    return false;
                }
                if (motionEvent.getAction() == 1) {
                    PollEditTextCell.this.onFieldTouchUp(this);
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override
            public ActionMode startActionMode(ActionMode.Callback callback) {
                ActionMode startActionMode = super.startActionMode(callback);
                PollEditTextCell.this.onActionModeStart(this, startActionMode);
                return startActionMode;
            }

            @Override
            public ActionMode startActionMode(ActionMode.Callback callback, int i2) {
                ActionMode startActionMode = super.startActionMode(callback, i2);
                PollEditTextCell.this.onActionModeStart(this, startActionMode);
                return startActionMode;
            }
        };
        this.textView = editTextCaption;
        editTextCaption.setAllowTextEntitiesIntersection(true);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setMaxLines(i == 1 ? 4 : Integer.MAX_VALUE);
        this.textView.setBackgroundDrawable(null);
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | 268435456);
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        this.textView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(11.0f));
        if (onClickListener != null) {
            int i2 = i == 1 ? 102 : 58;
            EditTextBoldCursor editTextBoldCursor3 = this.textView;
            boolean z2 = LocaleController.isRTL;
            addView(editTextBoldCursor3, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 16, z2 ? i2 : 64.0f, 0.0f, !z2 ? i2 : 64.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            ImageView imageView2 = this.moveImageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView2.setScaleType(scaleType);
            this.moveImageView.setImageResource(R.drawable.poll_reorder);
            ImageView imageView3 = this.moveImageView;
            int i3 = Theme.key_windowBackgroundWhiteGrayIcon;
            int color = Theme.getColor(i3);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            imageView3.setColorFilter(new PorterDuffColorFilter(color, mode));
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            ImageView imageView4 = new ImageView(context);
            this.deleteImageView = imageView4;
            imageView4.setFocusable(false);
            this.deleteImageView.setScaleType(scaleType);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.deleteImageView.setImageResource(R.drawable.poll_remove);
            this.deleteImageView.setOnClickListener(onClickListener);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i3), mode));
            this.deleteImageView.setContentDescription(LocaleController.getString(R.string.Delete));
            ImageView imageView5 = this.deleteImageView;
            boolean z3 = LocaleController.isRTL;
            addView(imageView5, LayoutHelper.createFrame(48, 50.0f, (z3 ? 3 : 5) | 48, z3 ? 3.0f : 0.0f, 0.0f, z3 ? 0.0f : 3.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            SimpleTextView simpleTextView2 = this.textView2;
            boolean z4 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(48, 24.0f, (z4 ? 3 : 5) | 48, z4 ? 20.0f : 0.0f, 43.0f, z4 ? 0.0f : 20.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, i3, Theme.key_checkboxCheck);
            this.checkBox.setContentDescription(LocaleController.getString(R.string.AccDescrQuizCorrectAnswer));
            this.checkBox.setDrawUnchecked(true);
            this.checkBox.setChecked(true, false);
            this.checkBox.setAlpha(0.0f);
            this.checkBox.setDrawBackgroundAsArc(8);
            addView(this.checkBox, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            this.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PollEditTextCell.this.lambda$new$0(view);
                }
            });
        } else {
            int i4 = i == 1 ? 80 : 19;
            EditTextBoldCursor editTextBoldCursor4 = this.textView;
            boolean z5 = LocaleController.isRTL;
            addView(editTextBoldCursor4, LayoutHelper.createFrame(-1, -2.0f, (z5 ? 5 : 3) | 16, z5 ? i4 : 19.0f, 0.0f, z5 ? 19.0f : i4, 0.0f));
        }
        if (i == 1) {
            ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = new ChatActivityEnterViewAnimatedIconView(context);
            this.emojiButton = chatActivityEnterViewAnimatedIconView;
            chatActivityEnterViewAnimatedIconView.setAlpha(0.8f);
            this.emojiButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.SRC_IN));
            this.emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.SMILE, false);
            int dp = AndroidUtilities.dp(9.5f);
            this.emojiButton.setPadding(dp, dp, dp, dp);
            this.emojiButton.setVisibility(8);
            int i5 = this.deleteImageView == null ? 3 : 48;
            ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView2 = this.emojiButton;
            boolean z6 = LocaleController.isRTL;
            addView(chatActivityEnterViewAnimatedIconView2, LayoutHelper.createFrame(48, 48.0f, z6 ? 3 : 5, z6 ? i5 : 0.0f, 0.0f, z6 ? 0.0f : i5, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.emojiButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            }
            this.emojiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PollEditTextCell.this.lambda$new$1(view);
                }
            });
            this.emojiButton.setContentDescription(LocaleController.getString(R.string.Emoji));
        }
    }

    public void lambda$new$0(View view) {
        if (this.checkBox.getTag() == null) {
            return;
        }
        onCheckBoxClick(this, !this.checkBox.isChecked());
    }

    public void lambda$setEmojiButtonVisibility$2(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.emojiButton.setScaleX(floatValue);
        this.emojiButton.setScaleY(floatValue);
        this.emojiButton.setAlpha(Math.max(floatValue, 0.8f));
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null && this.deleteImageView == null && simpleTextView.getVisibility() == 0) {
            this.textView2.setTranslationY(AndroidUtilities.dp(26.0f) * floatValue);
        }
    }

    @Override
    public void addTextChangedListener(TextWatcher textWatcher) {
        this.textView.addTextChangedListener(textWatcher);
    }

    public void addTextWatcher(TextWatcher textWatcher) {
        this.textView.addTextChangedListener(textWatcher);
    }

    public void callOnDelete() {
        ImageView imageView = this.deleteImageView;
        if (imageView == null) {
            return;
        }
        imageView.callOnClick();
    }

    public void createErrorTextView() {
        this.alwaysShowText2 = true;
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.textView2 = simpleTextView;
        simpleTextView.setTextSize(13);
        this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        SimpleTextView simpleTextView2 = this.textView2;
        boolean z = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(48, 24.0f, (z ? 3 : 5) | 48, z ? 20.0f : 0.0f, 17.0f, z ? 0.0f : 20.0f, 0.0f));
    }

    protected boolean drawDivider() {
        return true;
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    @Override
    public EditTextBoldCursor getEditField() {
        return this.textView;
    }

    @Override
    public Editable getEditText() {
        return this.textView.getText();
    }

    public ChatActivityEnterViewAnimatedIconView getEmojiButton() {
        return this.emojiButton;
    }

    @Override
    public CharSequence getFieldText() {
        if (this.textView.length() > 0) {
            return this.textView.getText();
        }
        return null;
    }

    @Override
    public BaseFragment getParentFragment() {
        return null;
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    protected boolean isChecked(PollEditTextCell pollEditTextCell) {
        return false;
    }

    protected void onActionModeStart(EditTextBoldCursor editTextBoldCursor, ActionMode actionMode) {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.checkBox != null) {
            setShowCheckBox(shouldShowCheckBox(), false);
            this.checkBox.setChecked(isChecked(this), false);
        }
    }

    public void onCheckBoxClick(PollEditTextCell pollEditTextCell, boolean z) {
        this.checkBox.setChecked(z, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float dp;
        int i;
        if (this.needDivider && drawDivider()) {
            if (LocaleController.isRTL) {
                dp = 0.0f;
            } else {
                dp = AndroidUtilities.dp(this.moveImageView != null ? 63.0f : 20.0f);
            }
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                i = AndroidUtilities.dp(this.moveImageView != null ? 63.0f : 20.0f);
            } else {
                i = 0;
            }
            canvas.drawLine(dp, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    protected void onEditTextDraw(EditTextBoldCursor editTextBoldCursor, Canvas canvas) {
    }

    protected void onEditTextFocusChanged(boolean z) {
    }

    public void lambda$new$1(PollEditTextCell pollEditTextCell) {
    }

    protected void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }
        ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = this.emojiButton;
        if (chatActivityEnterViewAnimatedIconView != null) {
            chatActivityEnterViewAnimatedIconView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }
        ImageView imageView2 = this.moveImageView;
        if (imageView2 != null) {
            imageView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824));
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((size - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(this.textView2 == null ? 42 : this.deleteImageView == null ? 70 : this.emojiButton != null ? 174 : 122), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        int measuredHeight = this.textView.getMeasuredHeight();
        setMeasuredDimension(size, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + (this.needDivider ? 1 : 0));
        SimpleTextView simpleTextView2 = this.textView2;
        if (simpleTextView2 == null || this.alwaysShowText2) {
            return;
        }
        simpleTextView2.setAlpha(measuredHeight >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setEmojiButtonVisibility(final boolean z) {
        ValueAnimator valueAnimator = this.valueAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z) {
            this.emojiButton.setVisibility(0);
            this.emojiButton.setScaleX(0.0f);
            this.emojiButton.setScaleY(0.0f);
            this.emojiButton.setAlpha(0.0f);
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f);
        this.valueAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                PollEditTextCell.this.lambda$setEmojiButtonVisibility$2(valueAnimator2);
            }
        });
        this.valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PollEditTextCell.this.emojiButton.setVisibility(8);
                    return;
                }
                PollEditTextCell.this.emojiButton.setScaleX(1.0f);
                PollEditTextCell.this.emojiButton.setScaleY(1.0f);
                PollEditTextCell.this.emojiButton.setAlpha(0.8f);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }
        });
        this.valueAnimator.setDuration(200L);
        this.valueAnimator.start();
    }

    @Override
    public void setFieldText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public void setShowCheckBox(boolean z, boolean z2) {
        if (z == (this.checkBox.getTag() != null)) {
            return;
        }
        AnimatorSet animatorSet = this.checkBoxAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.checkBoxAnimation = null;
        }
        this.checkBox.setTag(z ? 1 : null);
        if (!z2) {
            this.checkBox.setAlpha(z ? 1.0f : 0.0f);
            this.moveImageView.setAlpha(z ? 0.0f : 1.0f);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.checkBoxAnimation = animatorSet2;
        CheckBox2 checkBox2 = this.checkBox;
        Property property = View.ALPHA;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(checkBox2, (Property<CheckBox2, Float>) property, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.moveImageView, (Property<ImageView, Float>) property, z ? 0.0f : 1.0f));
        this.checkBoxAnimation.setDuration(180L);
        this.checkBoxAnimation.start();
    }

    public void setShowNextButton(boolean z) {
        this.showNextButton = z;
    }

    public void setText2(String str) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView == null) {
            return;
        }
        simpleTextView.setText(str);
    }

    public void setTextAndHint(CharSequence charSequence, String str, boolean z) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag(null);
        }
        this.textView.setText(charSequence);
        if (!TextUtils.isEmpty(charSequence)) {
            EditTextBoldCursor editTextBoldCursor = this.textView;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
        }
        this.textView.setHint(str);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    protected boolean shouldShowCheckBox() {
        return false;
    }
}
