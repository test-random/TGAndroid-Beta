package org.telegram.ui.Components.Premium.boosts.cells.statistics;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;

public class GiveawayCell extends UserCell {
    private CounterDrawable counterDrawable;
    private TL_stories.PrepaidGiveaway prepaidGiveaway;

    public GiveawayCell(Context context, int i, int i2, boolean z) {
        super(context, i, i2, z);
        init(context);
    }

    private void init(Context context) {
        this.counterDrawable = new CounterDrawable(context);
    }

    public TL_stories.PrepaidGiveaway getPrepaidGiveaway() {
        return this.prepaidGiveaway;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(70.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void setImage(TL_stories.PrepaidGiveaway prepaidGiveaway) {
        AvatarDrawable avatarDrawable;
        int i;
        int i2;
        String valueOf;
        this.prepaidGiveaway = prepaidGiveaway;
        if (!(prepaidGiveaway instanceof TL_stories.TL_prepaidStarsGiveaway)) {
            if (prepaidGiveaway instanceof TL_stories.TL_prepaidGiveaway) {
                this.avatarDrawable.setAvatarType(16);
                int i3 = ((TL_stories.TL_prepaidGiveaway) prepaidGiveaway).months;
                if (i3 == 12) {
                    avatarDrawable = this.avatarDrawable;
                    i = -31392;
                    i2 = -2796986;
                } else if (i3 == 6) {
                    avatarDrawable = this.avatarDrawable;
                    i = -10703110;
                    i2 = -12481584;
                } else {
                    avatarDrawable = this.avatarDrawable;
                    i = -6631068;
                    i2 = -11945404;
                }
                avatarDrawable.setColor(i, i2);
                valueOf = String.valueOf(prepaidGiveaway.quantity * BoostRepository.giveawayBoostsPerPremium());
            }
            this.nameTextView.setRightDrawable(this.counterDrawable);
        }
        this.avatarDrawable.setAvatarType(26);
        valueOf = String.valueOf(((TL_stories.TL_prepaidStarsGiveaway) prepaidGiveaway).stars / 500);
        this.counterDrawable.setText(valueOf);
        this.nameTextView.setRightDrawable(this.counterDrawable);
    }
}
