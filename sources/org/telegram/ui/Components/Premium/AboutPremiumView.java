package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class AboutPremiumView extends LinearLayout {
    public AboutPremiumView(Context context) {
        super(context);
        setOrientation(1);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i));
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.AboutPremiumTitle));
        addView(textView);
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(i));
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.AboutPremiumDescription)));
        addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 0, 0, 0));
        TextView textView3 = new TextView(context);
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(Theme.getColor(i));
        textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.AboutPremiumDescription2)));
        addView(textView3, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 24, 0, 0));
    }
}
