package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSlider;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ChooseSpeedLayout;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;

public class ChooseSpeedLayout {
    ActionBarMenuSlider.SpeedSlider slider;
    ActionBarMenuSubItem[] speedItems = new ActionBarMenuSubItem[5];
    public ActionBarPopupWindow.ActionBarPopupWindowLayout speedSwipeBackLayout;

    public interface Callback {
        void onSpeedSelected(float f, boolean z, boolean z2);
    }

    public ChooseSpeedLayout(Context context, final PopupSwipeBackLayout popupSwipeBackLayout, final Callback callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, null);
        this.speedSwipeBackLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_arrow_back, LocaleController.getString(R.string.Back), false, null);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PopupSwipeBackLayout.this.closeForeground();
            }
        });
        addItem.setColors(-328966, -328966);
        addItem.setSelectorColor(268435455);
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }
        };
        frameLayout.setMinimumWidth(AndroidUtilities.dp(196.0f));
        frameLayout.setBackgroundColor(-15198184);
        this.speedSwipeBackLayout.addView(frameLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(8.0f);
        frameLayout.setLayoutParams(layoutParams);
        ActionBarMenuSlider.SpeedSlider speedSlider = new ActionBarMenuSlider.SpeedSlider(context, null);
        this.slider = speedSlider;
        speedSlider.setMinimumWidth(AndroidUtilities.dp(196.0f));
        this.slider.setDrawShadow(false);
        this.slider.setBackgroundColor(-14540254);
        this.slider.setTextColor(-1);
        this.slider.setOnValueChange(new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ChooseSpeedLayout.lambda$new$1(ChooseSpeedLayout.Callback.this, (Float) obj, (Boolean) obj2);
            }
        });
        this.speedSwipeBackLayout.addView((View) this.slider, LayoutHelper.createLinear(-1, 44));
        FrameLayout frameLayout2 = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }
        };
        frameLayout2.setMinimumWidth(AndroidUtilities.dp(196.0f));
        frameLayout2.setBackgroundColor(-15198184);
        this.speedSwipeBackLayout.addView(frameLayout2);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) frameLayout2.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams2.gravity = 5;
        }
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(8.0f);
        frameLayout2.setLayoutParams(layoutParams2);
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_2, LocaleController.getString(R.string.SpeedVerySlow), false, null);
        addItem2.setColors(-328966, -328966);
        addItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(0.2f, true, true);
            }
        });
        addItem2.setSelectorColor(268435455);
        this.speedItems[0] = addItem2;
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_slow, LocaleController.getString(R.string.SpeedSlow), false, null);
        addItem3.setColors(-328966, -328966);
        addItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(0.5f, true, true);
            }
        });
        addItem3.setSelectorColor(268435455);
        this.speedItems[1] = addItem3;
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_normal, LocaleController.getString(R.string.SpeedNormal), false, null);
        addItem4.setColors(-328966, -328966);
        addItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(1.0f, true, true);
            }
        });
        addItem4.setSelectorColor(268435455);
        this.speedItems[2] = addItem4;
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_fast, LocaleController.getString(R.string.SpeedFast), false, null);
        addItem5.setColors(-328966, -328966);
        addItem5.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(1.5f, true, true);
            }
        });
        addItem5.setSelectorColor(268435455);
        this.speedItems[3] = addItem5;
        ActionBarMenuSubItem addItem6 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_superfast, LocaleController.getString(R.string.SpeedVeryFast), false, null);
        addItem6.setColors(-328966, -328966);
        addItem6.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(2.0f, true, true);
            }
        });
        addItem6.setSelectorColor(268435455);
        this.speedItems[4] = addItem6;
    }

    public static void lambda$new$1(Callback callback, Float f, Boolean bool) {
        callback.onSpeedSelected((f.floatValue() * 2.3f) + 0.2f, bool.booleanValue(), false);
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
        this.slider.setSpeed(f, true);
    }
}
