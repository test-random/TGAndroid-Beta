package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PremiumPreviewFragment;

public class PremiumFeatureCell extends FrameLayout {
    public PremiumPreviewFragment.PremiumFeatureData data;
    private final TextView description;
    boolean drawDivider;
    public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable imageDrawable;
    public ImageView imageView;
    public final ImageView nextIcon;
    private Drawable premiumStar;
    private final SimpleTextView title;

    public PremiumFeatureCell(Context context) {
        this(context, null);
    }

    public PremiumFeatureCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setClipChildren(false);
        linearLayout.setClipChildren(false);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.title = simpleTextView;
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setTextSize(15);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        linearLayout.addView(simpleTextView, LayoutHelper.createLinear(-1, -2));
        TextView textView = new TextView(context);
        this.description = textView;
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 1, 0, 0));
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 62.0f, 8.0f, 48.0f, 9.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;
        imageView.setScaleType(scaleType);
        addView(this.imageView, LayoutHelper.createFrame(28, 28.0f, 0, 18.0f, 12.0f, 0.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.nextIcon = imageView2;
        imageView2.setScaleType(scaleType);
        imageView2.setImageResource(R.drawable.msg_arrowright);
        imageView2.setColorFilter(Theme.getColor(Theme.key_switchTrack, resourcesProvider));
        addView(imageView2, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 0.0f, 18.0f, 0.0f));
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.imageDrawable != null) {
            updateImageBounds();
            this.imageDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon)));
            this.imageDrawable.draw(canvas);
        }
        if (this.drawDivider) {
            canvas.drawRect(AndroidUtilities.dp(62.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.imageDrawable;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.attach();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.imageDrawable;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.detach();
        }
        super.onDetachedFromWindow();
    }

    public void setData(PremiumPreviewFragment.PremiumFeatureData premiumFeatureData, boolean z) {
        if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium() && premiumFeatureData.type == 12 && premiumFeatureData.icon == R.drawable.filled_premium_status2) {
            this.nextIcon.setVisibility(8);
            if (this.imageDrawable == null) {
                this.imageDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, AndroidUtilities.dp(24.0f), 13);
                if (isAttachedToWindow()) {
                    this.imageDrawable.attach();
                }
            }
            Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
            setEmoji(emojiStatusDocumentId == null ? 0L : emojiStatusDocumentId.longValue(), false);
        } else {
            this.nextIcon.setVisibility(0);
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.imageDrawable;
            if (swapAnimatedEmojiDrawable != null) {
                swapAnimatedEmojiDrawable.detach();
                this.imageDrawable = null;
            }
        }
        this.data = premiumFeatureData;
        this.title.setText(premiumFeatureData.title);
        this.description.setText(premiumFeatureData.description);
        this.imageView.setImageResource(premiumFeatureData.icon);
        this.drawDivider = z;
    }

    public void setEmoji(long j, boolean z) {
        if (j != 0) {
            this.imageDrawable.set(j, z);
            return;
        }
        if (this.premiumStar == null) {
            Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_premium_prolfilestar).mutate();
            this.premiumStar = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon), PorterDuff.Mode.SRC_IN));
        }
        this.imageDrawable.set(this.premiumStar, z);
    }

    public void updateImageBounds() {
        this.imageDrawable.setBounds((getWidth() - this.imageDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(21.0f), (getHeight() - this.imageDrawable.getIntrinsicHeight()) / 2, getWidth() - AndroidUtilities.dp(21.0f), (getHeight() + this.imageDrawable.getIntrinsicHeight()) / 2);
    }
}
