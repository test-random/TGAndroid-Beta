package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TypefaceSpan;

public class EditTextCell extends FrameLayout {
    public boolean autofocused;
    public final EditTextCaption editText;
    private boolean focused;
    private boolean ignoreEditText;
    AnimatedTextView.AnimatedTextDrawable limit;
    AnimatedColor limitColor;
    private int limitCount;
    private int maxLength;
    private boolean needDivider;
    private boolean showLimitWhenEmpty;
    private boolean showLimitWhenFocused;
    private int showLimitWhenNear;

    public EditTextCell(Context context, String str, final boolean z, final boolean z2, final int i, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.showLimitWhenNear = -1;
        this.limitColor = new AnimatedColor(this);
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.limit = animatedTextDrawable;
        animatedTextDrawable.setAnimationProperties(0.2f, 0L, 160L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.limit.setTextSize(AndroidUtilities.dp(15.33f));
        this.limit.setGravity(5);
        this.maxLength = i;
        EditTextCaption editTextCaption = new EditTextCaption(context, resourcesProvider) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                EditTextCell editTextCell = EditTextCell.this;
                editTextCell.limit.setTextColor(editTextCell.limitColor.set(Theme.getColor(editTextCell.limitCount <= 0 ? Theme.key_text_RedRegular : Theme.key_dialogSearchHint, resourcesProvider)));
                EditTextCell.this.limit.setBounds(getScrollX(), 0, ((getScrollX() + getWidth()) - getPaddingRight()) + AndroidUtilities.dp(42.0f), getHeight());
                EditTextCell.this.limit.draw(canvas);
            }

            @Override
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                if (z2) {
                    int i2 = R.id.menu_bold;
                    if (menu.findItem(i2) != null) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 23) {
                        menu.removeItem(16908341);
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.Bold));
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableStringBuilder.length(), 33);
                    int i3 = R.id.menu_groupbolditalic;
                    menu.add(i3, i2, 6, spannableStringBuilder);
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(LocaleController.getString(R.string.Italic));
                    spannableStringBuilder2.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf")), 0, spannableStringBuilder2.length(), 33);
                    menu.add(i3, R.id.menu_italic, 7, spannableStringBuilder2);
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(LocaleController.getString(R.string.Strike));
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 8;
                    spannableStringBuilder3.setSpan(new TextStyleSpan(textStyleRun), 0, spannableStringBuilder3.length(), 33);
                    menu.add(i3, R.id.menu_strike, 8, spannableStringBuilder3);
                    menu.add(i3, R.id.menu_regular, 9, LocaleController.getString(R.string.Regular));
                }
            }

            @Override
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(getScrollX() + getPaddingLeft(), 0, (getScrollX() + getWidth()) - getPaddingRight(), getHeight());
                super.onDraw(canvas);
                canvas.restore();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                super.onTextChanged(charSequence, i2, i3, i4);
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = EditTextCell.this.limit;
                if (animatedTextDrawable2 == null || i <= 0) {
                    return;
                }
                animatedTextDrawable2.cancelAnimation();
                EditTextCell.this.updateLimitText();
            }

            @Override
            protected boolean verifyDrawable(Drawable drawable) {
                return drawable == EditTextCell.this.limit || super.verifyDrawable(drawable);
            }
        };
        this.editText = editTextCaption;
        this.limit.setCallback(editTextCaption);
        editTextCaption.setTextSize(1, 17.0f);
        editTextCaption.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText, resourcesProvider));
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        editTextCaption.setTextColor(Theme.getColor(i2, resourcesProvider));
        editTextCaption.setBackground(null);
        if (z) {
            editTextCaption.setMaxLines(5);
            editTextCaption.setSingleLine(false);
        } else {
            editTextCaption.setMaxLines(1);
            editTextCaption.setSingleLine(true);
        }
        editTextCaption.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp((i > 0 ? 42 : 0) + 21), AndroidUtilities.dp(15.0f));
        editTextCaption.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        editTextCaption.setInputType((z ? 131072 : 0) | 573441);
        editTextCaption.setRawInputType(573441);
        editTextCaption.setHint(str);
        editTextCaption.setCursorColor(Theme.getColor(i2, resourcesProvider));
        editTextCaption.setCursorSize(AndroidUtilities.dp(19.0f));
        editTextCaption.setCursorWidth(1.5f);
        editTextCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (!EditTextCell.this.ignoreEditText) {
                    if (i > 0 && editable != null && editable.length() > i) {
                        EditTextCell.this.ignoreEditText = true;
                        EditTextCell.this.editText.setText(editable.subSequence(0, i));
                        EditTextCaption editTextCaption2 = EditTextCell.this.editText;
                        editTextCaption2.setSelection(editTextCaption2.length());
                        EditTextCell.this.ignoreEditText = false;
                    }
                    EditTextCell.this.onTextChanged(editable);
                }
                if (!z) {
                    return;
                }
                while (true) {
                    int indexOf = editable.toString().indexOf("\n");
                    if (indexOf < 0) {
                        return;
                    } else {
                        editable.delete(indexOf, indexOf + 1);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
                if (EditTextCell.this.ignoreEditText) {
                    return;
                }
                EditTextCell.this.autofocused = false;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }
        });
        editTextCaption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean z3) {
                EditTextCell.this.focused = z3;
                if (EditTextCell.this.showLimitWhenFocused) {
                    EditTextCell.this.updateLimitText();
                }
                EditTextCell.this.onFocusChanged(z3);
            }
        });
        addView(editTextCaption, LayoutHelper.createFrame(-1, -1, 48));
        updateLimitText();
    }

    public void lambda$hideKeyboardOnEnter$0() {
        AndroidUtilities.hideKeyboard(this.editText);
    }

    public void updateLimitText() {
        int i;
        if (this.editText == null) {
            return;
        }
        this.limitCount = this.maxLength - getText().length();
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.limit;
        String str = "";
        if ((!TextUtils.isEmpty(getText()) || this.showLimitWhenEmpty) && ((!this.showLimitWhenFocused || (this.focused && !this.autofocused)) && ((i = this.showLimitWhenNear) == -1 || this.limitCount <= i))) {
            str = "" + this.limitCount;
        }
        animatedTextDrawable.setText(str);
    }

    public CharSequence getText() {
        return this.editText.getText();
    }

    public void hideKeyboardOnEnter() {
        whenHitEnter(new Runnable() {
            @Override
            public final void run() {
                EditTextCell.this.lambda$hideKeyboardOnEnter$0();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(22.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(22.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    protected void onFocusChanged(boolean z) {
    }

    public void onTextChanged(CharSequence charSequence) {
    }

    public void setDivider(boolean z) {
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public ImageView setLeftDrawable(Drawable drawable) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(drawable);
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 18.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.editText.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(24.0f);
        this.editText.setLayoutParams(layoutParams);
        return imageView;
    }

    public void setShowLimitOnFocus(boolean z) {
        this.showLimitWhenFocused = z;
    }

    public void setShowLimitWhenEmpty(boolean z) {
        this.showLimitWhenEmpty = z;
        if (z) {
            updateLimitText();
        }
    }

    public void setShowLimitWhenNear(int i) {
        this.showLimitWhenNear = i;
        updateLimitText();
    }

    public void setText(CharSequence charSequence) {
        this.ignoreEditText = true;
        this.editText.setText(charSequence);
        EditTextCaption editTextCaption = this.editText;
        editTextCaption.setSelection(editTextCaption.getText().length());
        this.ignoreEditText = false;
    }

    public void whenHitEnter(final Runnable runnable) {
        this.editText.setImeOptions(6);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                runnable.run();
                return true;
            }
        });
    }
}
