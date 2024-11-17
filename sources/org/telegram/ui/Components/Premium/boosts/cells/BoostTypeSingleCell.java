package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class BoostTypeSingleCell extends BoostTypeCell {
    public BoostTypeSingleCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
    }

    @Override
    protected boolean needCheck() {
        return false;
    }

    public void setGiveaway(TL_stories.PrepaidGiveaway prepaidGiveaway) {
        AvatarDrawable avatarDrawable;
        int i;
        int i2;
        String formatPluralString;
        this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3, this.resourcesProvider));
        if (!(prepaidGiveaway instanceof TL_stories.TL_prepaidStarsGiveaway)) {
            if (prepaidGiveaway instanceof TL_stories.TL_prepaidGiveaway) {
                this.titleTextView.setText(LocaleController.getString(R.string.BoostingPreparedGiveawayOne));
                this.avatarDrawable.setAvatarType(16);
                TL_stories.TL_prepaidGiveaway tL_prepaidGiveaway = (TL_stories.TL_prepaidGiveaway) prepaidGiveaway;
                int i3 = tL_prepaidGiveaway.months;
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
                formatPluralString = LocaleController.formatPluralString("BoostingPreparedGiveawaySubscriptionsPlural", prepaidGiveaway.quantity, LocaleController.formatPluralString("Months", tL_prepaidGiveaway.months, new Object[0]));
            }
            this.imageView.setImageDrawable(this.avatarDrawable);
            this.imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        }
        TL_stories.TL_prepaidStarsGiveaway tL_prepaidStarsGiveaway = (TL_stories.TL_prepaidStarsGiveaway) prepaidGiveaway;
        this.avatarDrawable.setAvatarType(26);
        this.titleTextView.setText(LocaleController.formatPluralStringComma("BoostingStarsPreparedGiveawaySubscriptionsPlural", (int) tL_prepaidStarsGiveaway.stars));
        formatPluralString = LocaleController.formatPluralString("AmongWinners", tL_prepaidStarsGiveaway.quantity, new Object[0]);
        setSubtitle(formatPluralString);
        this.imageView.setImageDrawable(this.avatarDrawable);
        this.imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
    }

    @Override
    public void updateLayouts() {
        this.imageView.setLayoutParams(LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 16, 16.0f, 0.0f, 16.0f, 0.0f));
        SimpleTextView simpleTextView = this.titleTextView;
        boolean z = LocaleController.isRTL;
        simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 16, z ? 20.0f : 69.0f, 0.0f, z ? 69.0f : 20.0f, 0.0f));
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        boolean z2 = LocaleController.isRTL;
        simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 16, z2 ? 20.0f : 69.0f, 0.0f, z2 ? 69.0f : 20.0f, 0.0f));
    }
}
