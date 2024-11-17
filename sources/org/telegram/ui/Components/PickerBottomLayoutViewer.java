package org.telegram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class PickerBottomLayoutViewer extends FrameLayout {
    public TextView cancelButton;
    public TextView doneButton;
    public TextView doneButtonBadgeTextView;
    private boolean isDarkTheme;

    public PickerBottomLayoutViewer(Context context) {
        this(context, true);
    }

    public PickerBottomLayoutViewer(Context context, boolean z) {
        super(context);
        this.isDarkTheme = z;
        setBackgroundColor(z ? -15066598 : -1);
        TextView textView = new TextView(context);
        this.cancelButton = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(this.isDarkTheme ? -12763843 : 788529152, 0));
        this.cancelButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelButton.setText(LocaleController.getString(R.string.Cancel).toUpperCase());
        this.cancelButton.setTypeface(AndroidUtilities.bold());
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        TextView textView2 = new TextView(context);
        this.doneButton = textView2;
        textView2.setTextSize(1, 14.0f);
        this.doneButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
        this.doneButton.setGravity(17);
        this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(this.isDarkTheme ? -12763843 : 788529152, 0));
        this.doneButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneButton.setText(LocaleController.getString(R.string.Send).toUpperCase());
        this.doneButton.setTypeface(AndroidUtilities.bold());
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        TextView textView3 = new TextView(context);
        this.doneButtonBadgeTextView = textView3;
        textView3.setTypeface(AndroidUtilities.bold());
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(-1);
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundResource(this.isDarkTheme ? R.drawable.photobadge : R.drawable.bluecounter);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        addView(this.doneButtonBadgeTextView, LayoutHelper.createFrame(-2, 23.0f, 53, 0.0f, 0.0f, 7.0f, 0.0f));
    }

    public void updateSelectedCount(int i, boolean z) {
        if (i != 0) {
            this.doneButtonBadgeTextView.setVisibility(0);
            this.doneButtonBadgeTextView.setText(String.format("%d", Integer.valueOf(i)));
            this.doneButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
            if (z) {
                this.doneButton.setEnabled(true);
                return;
            }
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(8);
        TextView textView = this.doneButton;
        if (!z) {
            textView.setTextColor(this.isDarkTheme ? -1 : -15095832);
        } else {
            textView.setTextColor(-6710887);
            this.doneButton.setEnabled(false);
        }
    }
}
