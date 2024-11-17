package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class GraySectionCell extends FrameLayout implements Theme.Colorable {
    private int layerHeight;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatedTextView rightTextView;
    private AnimatedEmojiSpan.TextViewEmojis textView;

    public GraySectionCell(Context context) {
        this(context, null);
    }

    public GraySectionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.layerHeight = 32;
        this.resourcesProvider = resourcesProvider;
        setBackgroundColor(getThemedColor(Theme.key_graySection));
        AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(getContext());
        this.textView = textViewEmojis;
        textViewEmojis.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.bold());
        AnimatedEmojiSpan.TextViewEmojis textViewEmojis2 = this.textView;
        int i = Theme.key_graySectionText;
        textViewEmojis2.setTextColor(getThemedColor(i));
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), true, true, true) {
            @Override
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        this.rightTextView = animatedTextView;
        animatedTextView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        this.rightTextView.setAnimationProperties(0.9f, 0L, 420L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.rightTextView.setTextSize(AndroidUtilities.dp(14.0f));
        this.rightTextView.setTextColor(getThemedColor(i));
        this.rightTextView.setGravity(LocaleController.isRTL ? 3 : 5);
        addView(this.rightTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        ViewCompat.setAccessibilityHeading(this, true);
    }

    public static void createThemeDescriptions(List list, RecyclerListView recyclerListView) {
        int i = Theme.key_graySectionText;
        list.add(new ThemeDescription(recyclerListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        list.add(new ThemeDescription(recyclerListView, 0, new Class[]{GraySectionCell.class}, new String[]{"rightTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public CharSequence getText() {
        return this.textView.getText();
    }

    public TextView getTextView() {
        return this.textView;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.layerHeight), 1073741824));
    }

    public void setLayerHeight(int i) {
        this.layerHeight = i;
        requestLayout();
    }

    public void setRightText(CharSequence charSequence, View.OnClickListener onClickListener) {
        this.rightTextView.setText(charSequence, false);
        this.rightTextView.setOnClickListener(onClickListener);
        this.rightTextView.setVisibility(0);
    }

    public void setRightText(CharSequence charSequence, boolean z) {
        this.rightTextView.setText(charSequence, true, z);
        this.rightTextView.setVisibility(0);
    }

    public void setRightText(CharSequence charSequence, boolean z, View.OnClickListener onClickListener) {
        this.rightTextView.setText(charSequence, true, z);
        this.rightTextView.setOnClickListener(onClickListener);
        this.rightTextView.setVisibility(0);
    }

    public void setRightText(String str) {
        setRightText((CharSequence) str, true);
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
        this.rightTextView.setVisibility(8);
        this.rightTextView.setOnClickListener(null);
    }

    public void setText(CharSequence charSequence, CharSequence charSequence2, View.OnClickListener onClickListener) {
        this.textView.setText(charSequence);
        this.rightTextView.setText(charSequence2, false);
        this.rightTextView.setOnClickListener(onClickListener);
        this.rightTextView.setVisibility(0);
    }

    public void setTextColor(int i) {
        int themedColor = getThemedColor(i);
        this.textView.setTextColor(themedColor);
        this.rightTextView.setTextColor(themedColor);
    }

    @Override
    public void updateColors() {
        setBackgroundColor(getThemedColor(Theme.key_graySection));
        AnimatedEmojiSpan.TextViewEmojis textViewEmojis = this.textView;
        int i = Theme.key_graySectionText;
        textViewEmojis.setTextColor(getThemedColor(i));
        this.rightTextView.setTextColor(getThemedColor(i));
    }
}
