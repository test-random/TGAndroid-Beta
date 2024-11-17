package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;

public class EnterPrizeCell extends LinearLayout {
    private AfterTextChangedListener afterTextChangedListener;
    private final EditTextCaption editText;
    private final Theme.ResourcesProvider resourcesProvider;
    private final TextView textView;

    public interface AfterTextChangedListener {
        void afterTextChanged(String str);
    }

    public EnterPrizeCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setOrientation(0);
        EditTextCaption editTextCaption = new EditTextCaption(context, resourcesProvider);
        this.editText = editTextCaption;
        editTextCaption.setLines(1);
        editTextCaption.setSingleLine(true);
        InputFilter[] inputFilterArr = {new InputFilter.LengthFilter(128) {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                if (filter != null && filter.length() == 0) {
                    AndroidUtilities.shakeView(EnterPrizeCell.this.editText);
                    BotWebViewVibrationEffect.APP_ERROR.vibrate();
                }
                return filter;
            }
        }};
        editTextCaption.setInputType(16384);
        editTextCaption.setFilters(inputFilterArr);
        editTextCaption.setTextSize(1, 16.0f);
        editTextCaption.setTextColor(Theme.getColor(Theme.key_chat_messagePanelText, resourcesProvider));
        editTextCaption.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkOut, resourcesProvider));
        editTextCaption.setHighlightColor(Theme.getColor(Theme.key_chat_inTextSelectionHighlight, resourcesProvider));
        int i = Theme.key_chat_messagePanelHint;
        editTextCaption.setHintColor(Theme.getColor(i, resourcesProvider));
        editTextCaption.setHintTextColor(Theme.getColor(i, resourcesProvider));
        editTextCaption.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor, resourcesProvider));
        editTextCaption.setHandlesColor(Theme.getColor(Theme.key_chat_TextSelectionCursor, resourcesProvider));
        editTextCaption.setBackground(null);
        editTextCaption.setHint(LocaleController.getString(R.string.BoostingGiveawayEnterYourPrize));
        editTextCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (EnterPrizeCell.this.afterTextChangedListener != null) {
                    EnterPrizeCell.this.afterTextChangedListener.afterTextChanged(editable.toString().trim());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        editTextCaption.setImeOptions(6);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        if (!LocaleController.isRTL) {
            addView(textView, LayoutHelper.createLinear(-2, -2, 16, 20, 0, 0, 0));
            addView(editTextCaption, LayoutHelper.createLinear(-1, -2, 16, 36, 0, 20, 0));
        } else {
            LinearLayout.LayoutParams createLinear = LayoutHelper.createLinear(-1, -2, 16, 20, 0, 36, 0);
            createLinear.weight = 1.0f;
            addView(editTextCaption, createLinear);
            addView(textView, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 20, 0));
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
    }

    public void setAfterTextChangedListener(AfterTextChangedListener afterTextChangedListener) {
        this.afterTextChangedListener = afterTextChangedListener;
    }

    public void setCount(int i) {
        this.textView.setText(String.valueOf(i));
    }
}
