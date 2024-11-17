package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class TopicSearchCell extends FrameLayout {
    BackupImageView backupImageView;
    public boolean drawDivider;
    TextView textView;
    TLRPC.TL_forumTopic topic;

    public TopicSearchCell(Context context) {
        super(context);
        float f;
        float f2;
        int i;
        float f3;
        TextView textView;
        int i2;
        float f4;
        float f5;
        this.backupImageView = new BackupImageView(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.bold());
        if (LocaleController.isRTL) {
            f2 = 0.0f;
            i = 21;
            f5 = 12.0f;
            f3 = 0.0f;
            addView(this.backupImageView, LayoutHelper.createFrame(30, 30.0f, 21, 12.0f, 0.0f, 12.0f, 0.0f));
            textView = this.textView;
            f = 56.0f;
            i2 = -1;
            f4 = -2.0f;
        } else {
            f = 12.0f;
            f2 = 0.0f;
            i = 16;
            f3 = 0.0f;
            addView(this.backupImageView, LayoutHelper.createFrame(30, 30.0f, 16, 12.0f, 0.0f, 12.0f, 0.0f));
            textView = this.textView;
            i2 = -1;
            f4 = -2.0f;
            f5 = 56.0f;
        }
        addView(textView, LayoutHelper.createFrame(i2, f4, i, f5, f3, f, f2));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.drawDivider) {
            int dp = AndroidUtilities.dp(56.0f);
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - dp, getMeasuredHeight() - 1, Theme.dividerPaint);
            } else {
                canvas.drawLine(dp, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public TLRPC.TL_forumTopic getTopic() {
        return this.topic;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
    }

    public void setTopic(TLRPC.TL_forumTopic tL_forumTopic) {
        TextView textView;
        CharSequence highlightText;
        this.topic = tL_forumTopic;
        if (TextUtils.isEmpty(tL_forumTopic.searchQuery)) {
            textView = this.textView;
            highlightText = AndroidUtilities.removeDiacritics(tL_forumTopic.title);
        } else {
            textView = this.textView;
            highlightText = AndroidUtilities.highlightText(AndroidUtilities.removeDiacritics(tL_forumTopic.title), tL_forumTopic.searchQuery, (Theme.ResourcesProvider) null);
        }
        textView.setText(highlightText);
        ForumUtilities.setTopicIcon(this.backupImageView, tL_forumTopic);
        BackupImageView backupImageView = this.backupImageView;
        if (backupImageView == null || backupImageView.getImageReceiver() == null || !(this.backupImageView.getImageReceiver().getDrawable() instanceof ForumUtilities.GeneralTopicDrawable)) {
            return;
        }
        ((ForumUtilities.GeneralTopicDrawable) this.backupImageView.getImageReceiver().getDrawable()).setColor(Theme.getColor(Theme.key_chats_archiveBackground));
    }
}
