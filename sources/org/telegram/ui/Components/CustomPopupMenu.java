package org.telegram.ui.Components;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public abstract class CustomPopupMenu {
    boolean isShowing;
    ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    ActionBarPopupWindow popupWindow;

    public CustomPopupMenu(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, R.drawable.popup_fixed_alert2, resourcesProvider, z ? 1 : 0);
        this.popupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setAnimationEnabled(false);
        this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$0;
                lambda$new$0 = CustomPopupMenu.this.lambda$new$0(view, motionEvent);
                return lambda$new$0;
            }
        });
        this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
            @Override
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                CustomPopupMenu.this.lambda$new$1(keyEvent);
            }
        });
        this.popupLayout.setShownFromBottom(false);
        onCreate(this.popupLayout);
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
        this.popupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setClippingEnabled(true);
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.setSoftInputMode(0);
        this.popupWindow.getContentView().setFocusableInTouchMode(true);
        this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public final void onDismiss() {
                CustomPopupMenu.this.lambda$new$2();
            }
        });
    }

    public boolean lambda$new$0(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        android.graphics.Rect rect = AndroidUtilities.rectTmp2;
        view.getHitRect(rect);
        if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public void lambda$new$1(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void lambda$new$2() {
        onDismissed();
        this.isShowing = false;
    }

    public void dismiss() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return this.isShowing;
    }

    protected abstract void onCreate(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout);

    protected abstract void onDismissed();

    public void show(View view, int i, int i2) {
        this.isShowing = true;
        this.popupWindow.showAsDropDown(view, i, i2);
    }
}
