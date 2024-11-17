package org.telegram.ui.Charts.view_data;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class PieLegendView extends LegendSignatureView {
    TextView signature;
    TextView value;

    public PieLegendView(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
        TextView textView = new TextView(getContext());
        this.signature = textView;
        linearLayout.addView(textView);
        this.signature.getLayoutParams().width = AndroidUtilities.dp(96.0f);
        TextView textView2 = new TextView(getContext());
        this.value = textView2;
        linearLayout.addView(textView2);
        addView(linearLayout);
        this.value.setTypeface(Typeface.create("sans-serif-medium", 0));
        setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        this.chevron.setVisibility(8);
        this.zoomEnabled = false;
    }

    @Override
    public void recolor() {
        if (this.signature == null) {
            return;
        }
        super.recolor();
        this.signature.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
    }

    public void setData(String str, int i, int i2) {
        this.signature.setText(str);
        this.value.setText(Integer.toString(i));
        this.value.setTextColor(i2);
    }

    @Override
    public void setSize(int i) {
    }
}
