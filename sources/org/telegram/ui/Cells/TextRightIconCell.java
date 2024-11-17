package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextRightIconCell extends FrameLayout {
    private final ImageView ivIcon;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SimpleTextView textView;

    public TextRightIconCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        simpleTextView.setTextSize(16);
        simpleTextView.setGravity(19);
        addView(simpleTextView, LayoutHelper.createFrame(-1, 48.0f, 16, 22.0f, 0.0f, 56.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.ivIcon = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText5, resourcesProvider), PorterDuff.Mode.SRC_IN));
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 8388629, 0.0f, 0.0f, 16.0f, 0.0f));
        setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_dialogBackground));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.needDivider) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Paint paint = resourcesProvider != null ? resourcesProvider.getPaint("paintDivider") : null;
            if (paint == null) {
                paint = Theme.dividerPaint;
            }
            canvas.drawLine(AndroidUtilities.dp(22.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, paint);
        }
    }

    public void setDivider(boolean z) {
        this.needDivider = z;
    }

    public void setTextAndIcon(CharSequence charSequence, int i) {
        this.textView.setText(charSequence);
        this.ivIcon.setImageResource(i);
    }
}
