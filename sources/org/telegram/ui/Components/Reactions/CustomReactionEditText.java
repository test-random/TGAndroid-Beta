package org.telegram.ui.Components.Reactions;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextCaption;

public abstract class CustomReactionEditText extends EditTextCaption {
    private final GestureDetectorCompat gestureDetector;
    private int maxLength;
    private Runnable onFocused;
    private final Theme.ResourcesProvider resourcesProvider;

    public CustomReactionEditText(Context context, Theme.ResourcesProvider resourcesProvider, int i) {
        super(context, resourcesProvider);
        this.resourcesProvider = resourcesProvider;
        this.gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                return true;
            }
        });
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setIncludeFontPadding(true);
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            setShowSoftInputOnFocus(false);
        }
        setSingleLine(false);
        setMaxLines(50);
        this.maxLength = i;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
        setTextSize(1, 22.0f);
        setGravity(80);
        setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(12.0f));
        setTextColor(getThemedColor(Theme.key_chat_messagePanelText));
        setLinkTextColor(getThemedColor(Theme.key_chat_messageLinkOut));
        setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        int i3 = Theme.key_chat_messagePanelHint;
        setHintColor(getThemedColor(i3));
        setHintTextColor(getThemedColor(i3));
        setCursorColor(getThemedColor(Theme.key_chat_messagePanelCursor));
        setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        if (i2 >= 28) {
            setFallbackLineSpacing(false);
        }
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public final void onFocusChange(View view, boolean z) {
                CustomReactionEditText.this.lambda$new$0(view, z);
            }
        });
        setTextIsSelectable(true);
        setLongClickable(false);
        setFocusableInTouchMode(false);
    }

    public void lambda$new$0(View view, boolean z) {
        if (!z) {
            addReactionsSpan();
            return;
        }
        removeReactionsSpan(true);
        Runnable runnable = this.onFocused;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$removeReactionsSpan$1(AddReactionsSpan addReactionsSpan) {
        getText().delete(getText().getSpanStart(addReactionsSpan), getText().getSpanEnd(addReactionsSpan));
        setCursorVisible(true);
        setLongClickable(true);
    }

    public void addReactionsSpan() {
        setLongClickable(false);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText());
        if (((AddReactionsSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AddReactionsSpan.class)).length == 0) {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("x");
            AddReactionsSpan addReactionsSpan = new AddReactionsSpan(15.0f, this.resourcesProvider);
            addReactionsSpan.show(this);
            spannableStringBuilder2.setSpan(addReactionsSpan, 0, spannableStringBuilder2.length(), 33);
            setText(getText().append((CharSequence) spannableStringBuilder2));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.gestureDetector.onTouchEvent(motionEvent) || isLongClickable()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return false;
    }

    @Override
    public void extendActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        int i = R.id.menu_delete;
        menu.add(i, i, 0, LocaleController.getString(R.string.Delete));
    }

    public int getEditTextSelectionEnd() {
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < 0) {
            return 0;
        }
        return selectionEnd;
    }

    public int getEditTextSelectionStart() {
        int selectionStart = getSelectionStart();
        if (selectionStart < 0) {
            return 0;
        }
        return selectionStart;
    }

    public Paint.FontMetricsInt getFontMetricsInt() {
        return getPaint().getFontMetricsInt();
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override
    public void onSelectionChanged(int i, int i2) {
        super.onSelectionChanged(i, i2);
        if (!hasSelection() || ((AddReactionsSpan[]) getText().getSpans(i, i2, AddReactionsSpan.class)).length == 0) {
            return;
        }
        setSelection(i, i2 - 1);
    }

    public void removeReactionsSpan(boolean z) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText());
        for (final AddReactionsSpan addReactionsSpan : (AddReactionsSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AddReactionsSpan.class)) {
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    CustomReactionEditText.this.lambda$removeReactionsSpan$1(addReactionsSpan);
                }
            };
            if (z) {
                setCursorVisible(false);
                addReactionsSpan.hide(this, runnable);
            } else {
                runnable.run();
            }
        }
    }

    public void setMaxLength(int i) {
        if (this.maxLength != i) {
            this.maxLength = i;
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
        }
    }

    public void setOnFocused(Runnable runnable) {
        this.onFocused = runnable;
    }
}
