package org.telegram.ui.Business;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RLottieImageView;

public class QuickRepliesEmptyView extends LinearLayout {
    private TextView descriptionView;
    private TextView descriptionView2;
    public RLottieImageView imageView;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView titleView;

    private class DotTextView extends TextView {
        public DotTextView(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (getPaddingLeft() > 0) {
                canvas.drawCircle((getPaddingLeft() - AndroidUtilities.dp(2.5f)) / 2.0f, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.5f), getPaint());
            }
            super.dispatchDraw(canvas);
        }
    }

    public QuickRepliesEmptyView(android.content.Context r8, int r9, long r10, long r12, java.lang.String r14, org.telegram.ui.ActionBar.Theme.ResourcesProvider r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Business.QuickRepliesEmptyView.<init>(android.content.Context, int, long, long, java.lang.String, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private void updateColors() {
        TextView textView = this.titleView;
        int i = Theme.key_chat_serviceText;
        textView.setTextColor(getThemedColor(i));
        this.descriptionView.setTextColor(getThemedColor(i));
        TextView textView2 = this.descriptionView2;
        if (textView2 != null) {
            textView2.setTextColor(getThemedColor(i));
        }
    }
}
