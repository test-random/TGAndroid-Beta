package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.text.TextPaint;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TooManyCommunitiesHintCell extends FrameLayout {
    private TextView headerTextView;
    private FrameLayout imageLayout;
    private ImageView imageView;
    private TextView messageTextView;

    public TooManyCommunitiesHintCell(Context context) {
        super(context);
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        int i = Theme.key_chats_nameMessage_threeLines;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.MULTIPLY));
        TextView textView = new TextView(context);
        this.headerTextView = textView;
        textView.setTextColor(Theme.getColor(i));
        this.headerTextView.setTextSize(1, 20.0f);
        this.headerTextView.setTypeface(AndroidUtilities.bold());
        this.headerTextView.setGravity(17);
        addView(this.headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 75.0f, 52.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.messageTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chats_message));
        this.messageTextView.setTextSize(1, 14.0f);
        this.messageTextView.setGravity(17);
        addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 36.0f, 110.0f, 36.0f, 0.0f));
        final TextPaint textPaint = new TextPaint(1);
        textPaint.setColor(-1);
        textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        textPaint.setTypeface(AndroidUtilities.bold());
        final Paint paint = new Paint(1);
        final String str = "500";
        FrameLayout frameLayout = new FrameLayout(context) {
            RectF rect = new RectF();

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setColor(Theme.getColor(Theme.key_text_RedRegular));
                canvas.save();
                canvas.translate((getMeasuredWidth() - textPaint.measureText(str)) - AndroidUtilities.dp(8.0f), AndroidUtilities.dpf2(7.0f));
                this.rect.set(0.0f, 0.0f, textPaint.measureText(str), textPaint.getTextSize());
                this.rect.inset(-AndroidUtilities.dp(6.0f), -AndroidUtilities.dp(3.0f));
                float textSize = (textPaint.getTextSize() / 2.0f) + AndroidUtilities.dp(3.0f);
                canvas.drawRoundRect(this.rect, textSize, textSize, paint);
                canvas.drawText(str, 0.0f, textPaint.getTextSize() - AndroidUtilities.dpf2(2.0f), textPaint);
                canvas.restore();
            }
        };
        this.imageLayout = frameLayout;
        frameLayout.setWillNotDraw(false);
        this.imageLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2, 1));
        addView(this.imageLayout, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 12.0f, 0.0f, 6.0f));
        this.headerTextView.setText(LocaleController.getString(R.string.TooManyCommunities));
        this.imageView.setImageResource(R.drawable.groups_limit1);
    }

    public void setMessageText(String str) {
        this.messageTextView.setText(str);
    }
}
