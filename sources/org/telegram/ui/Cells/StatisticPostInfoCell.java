package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.StoriesUtilities;

public abstract class StatisticPostInfoCell extends FrameLayout {
    private final AvatarDrawable avatarDrawable;
    private final TLRPC.ChatFull chat;
    private final TextView date;
    private final Paint dividerPaint;
    private final BackupImageView imageView;
    private final TextView likes;
    private final SimpleTextView message;
    private boolean needDivider;
    private StatisticActivity.RecentPostInfo postInfo;
    private final Theme.ResourcesProvider resourcesProvider;
    private final TextView shares;
    private final StoriesUtilities.AvatarStoryParams storyAvatarParams;
    private final TextView views;

    public StatisticPostInfoCell(Context context, TLRPC.ChatFull chatFull, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.dividerPaint = new Paint(1);
        this.avatarDrawable = new AvatarDrawable();
        this.storyAvatarParams = new StoriesUtilities.AvatarStoryParams(false);
        this.chat = chatFull;
        this.resourcesProvider = resourcesProvider;
        BackupImageView backupImageView = new BackupImageView(context) {
            @Override
            public void onDraw(Canvas canvas) {
                if (StatisticPostInfoCell.this.postInfo == null || !StatisticPostInfoCell.this.postInfo.isStory()) {
                    super.onDraw(canvas);
                    return;
                }
                float dp = AndroidUtilities.dp(1.0f);
                StatisticPostInfoCell.this.storyAvatarParams.originalAvatarRect.set(dp, dp, getMeasuredWidth() - r0, getMeasuredHeight() - r0);
                StatisticPostInfoCell.this.storyAvatarParams.drawSegments = false;
                StatisticPostInfoCell.this.storyAvatarParams.animate = false;
                StatisticPostInfoCell.this.storyAvatarParams.drawInside = true;
                StatisticPostInfoCell.this.storyAvatarParams.isArchive = false;
                StatisticPostInfoCell.this.storyAvatarParams.forceState = 1;
                StatisticPostInfoCell.this.storyAvatarParams.resourcesProvider = resourcesProvider;
                StoriesUtilities.drawAvatarWithStory(0L, canvas, this.imageReceiver, StatisticPostInfoCell.this.storyAvatarParams);
            }
        };
        this.imageView = backupImageView;
        setClipChildren(false);
        boolean z = LocaleController.isRTL;
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, (!z ? 8388611 : 8388613) | 16, !z ? 12.0f : 16.0f, 0.0f, !z ? 16.0f : 12.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        SimpleTextView simpleTextView = new SimpleTextView(context) {
            @Override
            public boolean setText(CharSequence charSequence) {
                return super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false));
            }
        };
        this.message = simpleTextView;
        NotificationCenter.listenEmojiLoading(simpleTextView);
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setTextSize(16);
        simpleTextView.setMaxLines(1);
        simpleTextView.setTextColor(-16777216);
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = new TextView(context);
        this.views = textView;
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-16777216);
        if (LocaleController.isRTL) {
            linearLayout2.addView(textView, LayoutHelper.createLinear(-2, -2, 80));
            linearLayout2.addView(simpleTextView, LayoutHelper.createLinear(0, -2, 1.0f, 0, 16, 0, 0, 0));
        } else {
            linearLayout2.addView(simpleTextView, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 16, 0));
            linearLayout2.addView(textView, LayoutHelper.createLinear(-2, -2, 80));
        }
        linearLayout.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.date = textView2;
        textView2.setTextSize(1, 13.0f);
        textView2.setTextColor(-16777216);
        textView2.setLines(1);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView3 = new TextView(context);
        this.shares = textView3;
        textView3.setTextSize(1, 13.0f);
        textView3.setTextColor(-16777216);
        textView3.setGravity(16);
        TextView textView4 = new TextView(context);
        this.likes = textView4;
        textView4.setTextSize(1, 13.0f);
        textView4.setTextColor(-16777216);
        textView4.setGravity(16);
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        if (LocaleController.isRTL) {
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 10, 0));
            linearLayout3.addView(textView4, LayoutHelper.createLinear(-2, -2, 16));
            linearLayout3.addView(textView2, LayoutHelper.createLinear(0, -2, 1.0f, 0, 8, 0, 0, 0));
        } else {
            linearLayout3.addView(textView2, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 8, 0));
            linearLayout3.addView(textView4, LayoutHelper.createLinear(-2, -2, 16));
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-2, -2, 16, 10, 0, 0, 0));
        }
        linearLayout.addView(linearLayout3, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 3.0f, 0.0f, 9.0f));
        boolean z2 = LocaleController.isRTL;
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, !z2 ? 72.0f : 18.0f, 0.0f, !z2 ? 18.0f : 72.0f, 0.0f));
        int i = Theme.key_dialogTextBlack;
        simpleTextView.setTextColor(Theme.getColor(i));
        textView.setTextColor(Theme.getColor(i));
        int i2 = Theme.key_windowBackgroundWhiteGrayText3;
        textView2.setTextColor(Theme.getColor(i2));
        textView3.setTextColor(Theme.getColor(i2));
        textView4.setTextColor(Theme.getColor(i2));
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.mini_stats_likes).mutate();
        DrawableCompat.setTint(mutate, Theme.getColor(i2));
        Drawable mutate2 = ContextCompat.getDrawable(context, R.drawable.mini_stats_shares).mutate();
        DrawableCompat.setTint(mutate2, Theme.getColor(i2));
        CombinedDrawable combinedDrawable = new CombinedDrawable(null, mutate, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable.setCustomSize(mutate2.getIntrinsicWidth(), mutate2.getIntrinsicHeight());
        textView4.setCompoundDrawablesWithIntrinsicBounds(combinedDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(null, mutate2, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable2.setCustomSize(mutate2.getIntrinsicWidth(), mutate2.getIntrinsicHeight());
        textView3.setCompoundDrawablesWithIntrinsicBounds(combinedDrawable2, (Drawable) null, (Drawable) null, (Drawable) null);
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        setWillNotDraw(false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.needDivider) {
            this.dividerPaint.setColor(Theme.getColor(Theme.key_divider, this.resourcesProvider));
            if (LocaleController.isRTL) {
                canvas.drawRect(0.0f, getHeight() - 1, getWidth() - AndroidUtilities.dp(72), getHeight(), this.dividerPaint);
            } else {
                canvas.drawRect(AndroidUtilities.dp(72), getHeight() - 1, getWidth(), getHeight(), this.dividerPaint);
            }
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public StatisticActivity.RecentPostInfo getPostInfo() {
        return this.postInfo;
    }

    public StoriesUtilities.AvatarStoryParams getStoryAvatarParams() {
        return this.storyAvatarParams;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.storyAvatarParams.onDetachFromWindow();
    }

    public void setData(StatisticActivity.MemberData memberData) {
        this.avatarDrawable.setInfo(memberData.user);
        this.imageView.setForUserOrChat(memberData.user, this.avatarDrawable);
        this.imageView.setRoundRadius(AndroidUtilities.dp(46.0f) >> 1);
        this.message.setText(memberData.user.first_name);
        this.date.setText(memberData.description);
        this.views.setVisibility(8);
        this.shares.setVisibility(8);
        this.likes.setVisibility(8);
    }

    public void setData(org.telegram.ui.StatisticActivity.RecentPostInfo r18, boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StatisticPostInfoCell.setData(org.telegram.ui.StatisticActivity$RecentPostInfo, boolean):void");
    }

    public void setImageViewAction(View.OnClickListener onClickListener) {
        this.imageView.setOnClickListener(onClickListener);
    }
}
