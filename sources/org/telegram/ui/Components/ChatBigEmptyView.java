package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.Theme;

public class ChatBigEmptyView extends LinearLayout {
    private ArrayList imageViews;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView statusTextView;
    private ArrayList textViews;

    public ChatBigEmptyView(android.content.Context r22, android.view.View r23, int r24, org.telegram.ui.ActionBar.Theme.ResourcesProvider r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatBigEmptyView.<init>(android.content.Context, android.view.View, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public void setStatusText(CharSequence charSequence) {
        this.statusTextView.setText(charSequence);
    }

    public void setTextColor(int i) {
        for (int i2 = 0; i2 < this.textViews.size(); i2++) {
            ((TextView) this.textViews.get(i2)).setTextColor(i);
        }
        for (int i3 = 0; i3 < this.imageViews.size(); i3++) {
            ((ImageView) this.imageViews.get(i3)).setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
    }
}
