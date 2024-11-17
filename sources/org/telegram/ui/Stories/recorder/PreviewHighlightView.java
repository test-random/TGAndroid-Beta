package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Stories.PeerStoriesView;
import org.telegram.ui.Stories.StoryCaptionView;

public class PreviewHighlightView extends FrameLayout {
    private final FrameLayout bottom;
    private int currentAccount;
    private boolean shownBottom;
    private boolean shownTop;
    private int storiesCount;
    private final StoryCaptionView storyCaptionView;
    private final FrameLayout top;

    public PreviewHighlightView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.storiesCount = 1;
        this.shownTop = false;
        this.shownBottom = false;
        this.currentAccount = i;
        TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            private RectF rectF = new RectF();
            private Paint barPaint = new Paint(1);

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                this.barPaint.setColor(-1);
                float width = ((getWidth() - (AndroidUtilities.dpf2(5.0f) * 2.0f)) - AndroidUtilities.dpf2((PreviewHighlightView.this.storiesCount - 1) * 2)) / PreviewHighlightView.this.storiesCount;
                float dpf2 = AndroidUtilities.dpf2(5.0f);
                int i2 = 0;
                while (i2 < PreviewHighlightView.this.storiesCount) {
                    this.rectF.set(dpf2, AndroidUtilities.dpf2(8.0f), dpf2 + width, AndroidUtilities.dpf2(10.0f));
                    this.barPaint.setAlpha(i2 < PreviewHighlightView.this.storiesCount + (-1) ? 255 : 133);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dpf2(1.0f), AndroidUtilities.dpf2(1.0f), this.barPaint);
                    dpf2 += AndroidUtilities.dpf2(2.0f) + width;
                    i2++;
                }
            }
        };
        this.top = frameLayout;
        PeerStoriesView.PeerHeaderView peerHeaderView = new PeerStoriesView.PeerHeaderView(getContext(), null);
        peerHeaderView.backupImageView.getAvatarDrawable().setInfo(i, currentUser);
        BackupImageView backupImageView = peerHeaderView.backupImageView;
        backupImageView.setForUserOrChat(currentUser, backupImageView.getAvatarDrawable());
        peerHeaderView.titleView.setText(Emoji.replaceEmoji(UserObject.getUserName(currentUser), peerHeaderView.titleView.getPaint().getFontMetricsInt(), false));
        peerHeaderView.setSubtitle(LocaleController.getString(R.string.RightNow), false);
        frameLayout.addView(peerHeaderView, LayoutHelper.createFrame(-1, -2.0f, 55, 0.0f, 17.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_close_white).mutate());
        imageView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, 53, 12.0f, 15.0f, 12.0f, 0.0f));
        addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        this.bottom = frameLayout2;
        StoryCaptionView storyCaptionView = new StoryCaptionView(getContext(), resourcesProvider);
        this.storyCaptionView = storyCaptionView;
        storyCaptionView.disableTouches = true;
        storyCaptionView.setTranslationY(AndroidUtilities.dp(8.0f));
        frameLayout2.addView(storyCaptionView, LayoutHelper.createFrame(-1, -1.0f, 87, 0.0f, 0.0f, 0.0f, 64.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.msg_share);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView2.setColorFilter(new PorterDuffColorFilter(-1, mode));
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 12.0f, 16.0f));
        FrameLayout frameLayout3 = new FrameLayout(context);
        frameLayout3.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(22.0f), ColorUtils.setAlphaComponent(-16777216, 122)));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(1694498815);
        textView.setText(LocaleController.getString(R.string.ReplyPrivately));
        frameLayout3.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 19, 24.0f, 0.0f, 24.0f, 0.0f));
        ImageView imageView3 = new ImageView(context);
        imageView3.setImageResource(R.drawable.input_attach);
        imageView3.setColorFilter(new PorterDuffColorFilter(-1, mode));
        frameLayout3.addView(imageView3, LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 9.0f, 0.0f));
        frameLayout2.addView(frameLayout3, LayoutHelper.createFrame(-1, 44.0f, 87, 9.0f, 8.0f, 55.0f, 8.0f));
        addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.setAlpha(0.0f);
        frameLayout2.setAlpha(0.0f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public void show(boolean z, boolean z2, View view) {
        if (z) {
            if (this.shownTop == z2) {
                return;
            } else {
                this.shownTop = z2;
            }
        } else if (this.shownBottom == z2) {
            return;
        } else {
            this.shownBottom = z2;
        }
        FrameLayout frameLayout = z ? this.top : this.bottom;
        frameLayout.clearAnimation();
        frameLayout.animate().alpha(z2 ? z ? 0.5f : 0.2f : 0.0f).start();
        if (view != null) {
            view.clearAnimation();
            view.animate().alpha(z2 ? 0.0f : 1.0f).start();
        }
    }

    public void updateCaption(CharSequence charSequence) {
        this.storyCaptionView.captionTextview.setText(AnimatedEmojiSpan.cloneSpans(new SpannableString(charSequence)), null, false, false);
    }

    public void updateCount() {
        this.storiesCount = MessagesController.getInstance(this.currentAccount).getStoriesController().getSelfStoriesCount() + 1;
        this.top.invalidate();
    }
}
