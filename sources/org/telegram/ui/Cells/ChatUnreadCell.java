package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ChatUnreadCell extends FrameLayout {
    private FrameLayout backgroundLayout;
    private ImageView imageView;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public ChatUnreadCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        FrameLayout frameLayout = new FrameLayout(context);
        this.backgroundLayout = frameLayout;
        frameLayout.setBackgroundResource(R.drawable.newmsg_divider);
        Drawable background = this.backgroundLayout.getBackground();
        int color = getColor(Theme.key_chat_unreadMessagesStartBackground);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        background.setColorFilter(new PorterDuffColorFilter(color, mode));
        addView(this.backgroundLayout, LayoutHelper.createFrame(-1, 27.0f, 51, 0.0f, 7.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setImageResource(R.drawable.ic_ab_new);
        this.imageView.setColorFilter(new PorterDuffColorFilter(getColor(Theme.key_chat_unreadMessagesStartArrowIcon), mode));
        this.imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.backgroundLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTextColor(getColor(Theme.key_chat_unreadMessagesStartText));
        this.textView.setTypeface(AndroidUtilities.bold());
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    private int getColor(int i) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer valueOf = resourcesProvider != null ? Integer.valueOf(resourcesProvider.getColor(i)) : null;
        return valueOf != null ? valueOf.intValue() : Theme.getColor(i);
    }

    public FrameLayout getBackgroundLayout() {
        return this.backgroundLayout;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public TextView getTextView() {
        return this.textView;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), 1073741824));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }
}
