package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.SpeedButtonsLayout;

public class SpeedButtonsLayout extends LinearLayout {
    ActionBarMenuSubItem[] speedItems;

    public interface Callback {
        void onSpeedSelected(float f, boolean z, boolean z2);
    }

    public SpeedButtonsLayout(Context context, final Callback callback) {
        super(context);
        this.speedItems = new ActionBarMenuSubItem[5];
        setOrientation(1);
        ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_0_2, LocaleController.getString(R.string.SpeedVerySlow), false, null);
        addItem.setColors(-328966, -328966);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SpeedButtonsLayout.Callback.this.onSpeedSelected(0.2f, true, true);
            }
        });
        addItem.setSelectorColor(268435455);
        this.speedItems[0] = addItem;
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_slow, LocaleController.getString(R.string.SpeedSlow), false, null);
        addItem2.setColors(-328966, -328966);
        addItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SpeedButtonsLayout.Callback.this.onSpeedSelected(0.5f, true, true);
            }
        });
        addItem2.setSelectorColor(268435455);
        this.speedItems[1] = addItem2;
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_normal, LocaleController.getString(R.string.SpeedNormal), false, null);
        addItem3.setColors(-328966, -328966);
        addItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SpeedButtonsLayout.Callback.this.onSpeedSelected(1.0f, true, true);
            }
        });
        addItem3.setSelectorColor(268435455);
        this.speedItems[2] = addItem3;
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_fast, LocaleController.getString(R.string.SpeedFast), false, null);
        addItem4.setColors(-328966, -328966);
        addItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SpeedButtonsLayout.Callback.this.onSpeedSelected(1.5f, true, true);
            }
        });
        addItem4.setSelectorColor(268435455);
        this.speedItems[3] = addItem4;
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_superfast, LocaleController.getString(R.string.SpeedVeryFast), false, null);
        addItem5.setColors(-328966, -328966);
        addItem5.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SpeedButtonsLayout.Callback.this.onSpeedSelected(2.0f, true, true);
            }
        });
        addItem5.setSelectorColor(268435455);
        this.speedItems[4] = addItem5;
        View view = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }
        };
        view.setMinimumWidth(AndroidUtilities.dp(196.0f));
        view.setBackgroundColor(-15198184);
        addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(8.0f);
        view.setLayoutParams(layoutParams);
    }

    public void update(float f, boolean z) {
        ActionBarMenuSubItem actionBarMenuSubItem;
        int i;
        for (int i2 = 0; i2 < this.speedItems.length; i2++) {
            if (!z || ((i2 != 0 || Math.abs(f - 0.2f) >= 0.01f) && ((i2 != 1 || Math.abs(f - 0.5f) >= 0.1f) && ((i2 != 2 || Math.abs(f - 1.0f) >= 0.1f) && ((i2 != 3 || Math.abs(f - 1.5f) >= 0.1f) && (i2 != 4 || Math.abs(f - 2.0f) >= 0.1f)))))) {
                actionBarMenuSubItem = this.speedItems[i2];
                i = -328966;
            } else {
                actionBarMenuSubItem = this.speedItems[i2];
                i = -9718023;
            }
            actionBarMenuSubItem.setColors(i, i);
        }
    }
}
