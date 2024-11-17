package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Stories.recorder.HintView2;

public class TopViewCell extends LinearLayout {
    public final BackupImageView imageView;
    private int lastIconResId;
    private int maxWidth;
    public final LinkSpanDrawable.LinksTextView textView;

    public TopViewCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.getImageReceiver().setAutoRepeatCount(1);
        backupImageView.getImageReceiver().setAutoRepeat(1);
        backupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TopViewCell.this.lambda$new$0(view);
            }
        });
        addView(backupImageView, LayoutHelper.createLinear(90, 90, 17, 0, 9, 0, 9));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context) {
            @Override
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                if (TopViewCell.this.maxWidth > 0 && TopViewCell.this.maxWidth < size) {
                    size = TopViewCell.this.maxWidth;
                }
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), i2);
            }
        };
        this.textView = linksTextView;
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setGravity(17);
        linksTextView.setTextAlignment(4);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        addView(linksTextView, LayoutHelper.createLinear(-1, -2, 17, 48, 0, 48, 17));
    }

    public void lambda$new$0(View view) {
        this.imageView.getImageReceiver().startAnimation();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
    }

    public void setEmoji(int i) {
        if (this.lastIconResId != i) {
            BackupImageView backupImageView = this.imageView;
            this.lastIconResId = i;
            backupImageView.setImageDrawable(new RLottieDrawable(i, "" + i, AndroidUtilities.dp(90.0f), AndroidUtilities.dp(90.0f)));
            this.imageView.getImageReceiver().setAutoRepeat(2);
        }
    }

    public void setEmoji(String str, String str2) {
        MediaDataController.getInstance(UserConfig.selectedAccount).setPlaceholderImage(this.imageView, str, str2, "90_90");
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
        this.maxWidth = HintView2.cutInFancyHalf(charSequence, this.textView.getPaint());
        this.textView.requestLayout();
    }
}
