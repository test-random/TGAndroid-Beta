package org.telegram.ui.Stories;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class StoryFailView extends FrameLayout {
    private final TextView button;
    private final Paint redPaint;
    private final TextView subtitleTextView;
    private final TextView titleTextView;
    private final Paint whitePaint;

    public StoryFailView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        Paint paint = new Paint(1);
        this.redPaint = paint;
        Paint paint2 = new Paint(1);
        this.whitePaint = paint2;
        paint.setColor(Theme.getColor(Theme.key_text_RedBold, resourcesProvider));
        paint2.setColor(-1);
        setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString(R.string.StoryError));
        textView.setTextColor(-1);
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 19, 44.0f, 0.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subtitleTextView = textView2;
        textView2.setTextSize(1, 8.0f);
        textView2.setTextColor(Theme.multAlpha(-1, 0.5f));
        textView2.setVisibility(8);
        textView2.setTranslationY(AndroidUtilities.dp(9.0f));
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 19, 44.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.button = textView3;
        textView3.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
        textView3.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 536870911, 956301311));
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setText(LocaleController.getString(R.string.TryAgain));
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setGravity(17);
        addView(textView3, LayoutHelper.createFrame(-2, 32.0f, 21, 0.0f, 0.0f, 12.0f, 0.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float dp = AndroidUtilities.dp(23.0f);
        float height = getHeight() / 2.0f;
        canvas.drawCircle(dp, height, AndroidUtilities.dp(10.0f), this.redPaint);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(dp - AndroidUtilities.dp(1.0f), height - AndroidUtilities.dpf2(4.6f), AndroidUtilities.dp(1.0f) + dp, AndroidUtilities.dpf2(1.6f) + height);
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.whitePaint);
        rectF.set(dp - AndroidUtilities.dp(1.0f), AndroidUtilities.dpf2(2.6f) + height, dp + AndroidUtilities.dp(1.0f), height + AndroidUtilities.dpf2(4.6f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.whitePaint);
    }

    public void set(TLRPC.TL_error tL_error) {
        TextView textView;
        int i;
        if (tL_error == null || TextUtils.isEmpty(tL_error.text)) {
            this.titleTextView.setTranslationY(0.0f);
            textView = this.subtitleTextView;
            i = 8;
        } else {
            this.titleTextView.setTranslationY(-AndroidUtilities.dpf2(5.33f));
            this.subtitleTextView.setText(tL_error.text);
            textView = this.subtitleTextView;
            i = 0;
        }
        textView.setVisibility(i);
    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.button.setOnClickListener(onClickListener);
    }
}
