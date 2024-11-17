package org.telegram.ui.Components.Premium.boosts.cells.statistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class GiftedUserCell extends UserCell {
    private FrameLayout badgeLayout;
    private TextView badgeTextView;
    private TL_stories.Boost boost;
    private CounterDrawable counterDrawable;
    private Drawable giftDrawable;
    private Drawable giveawayDrawable;

    public GiftedUserCell(Context context, int i, int i2, boolean z) {
        super(context, i, i2, z);
        init();
    }

    private void init() {
        this.counterDrawable = new CounterDrawable(getContext());
        this.badgeLayout = new FrameLayout(getContext());
        TextView textView = new TextView(getContext());
        this.badgeTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
        this.badgeTextView.setTypeface(AndroidUtilities.bold());
        this.badgeTextView.setTextSize(12.0f);
        this.badgeTextView.setGravity(17);
        this.badgeLayout.addView(this.badgeTextView, LayoutHelper.createFrame(-2, 22.0f));
        this.badgeLayout.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        FrameLayout frameLayout = this.badgeLayout;
        boolean z = LocaleController.isRTL;
        addView(frameLayout, LayoutHelper.createFrame(-2, -2.0f, (z ? 3 : 5) | 48, z ? 9 : 0, 9.0f, z ? 0 : 9, 0.0f));
    }

    private void setAvatarColorByMonths(int i) {
        AvatarDrawable avatarDrawable;
        int i2;
        int i3;
        if (i == 12) {
            avatarDrawable = this.avatarDrawable;
            i2 = -31392;
            i3 = -2796986;
        } else if (i == 6) {
            avatarDrawable = this.avatarDrawable;
            i2 = -10703110;
            i3 = -12481584;
        } else {
            avatarDrawable = this.avatarDrawable;
            i2 = -6631068;
            i3 = -11945404;
        }
        avatarDrawable.setColor(i2, i3);
    }

    public TL_stories.Boost getBoost() {
        return this.boost;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(70.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void setStatus(org.telegram.tgnet.tl.TL_stories.Boost r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.boosts.cells.statistics.GiftedUserCell.setStatus(org.telegram.tgnet.tl.TL_stories$Boost):void");
    }
}
