package org.telegram.ui.Components;

import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.FloatingToolbar;

public class MenuToItemOptions implements Menu {
    private final ItemOptions itemOptions;
    private final Utilities.Callback onMenuClicked;
    private final Runnable premiumLock;

    public MenuToItemOptions(ItemOptions itemOptions, Utilities.Callback callback, Runnable runnable) {
        this.itemOptions = itemOptions;
        this.onMenuClicked = callback;
        this.premiumLock = runnable;
    }

    public void lambda$add$0(int i) {
        this.onMenuClicked.run(Integer.valueOf(i));
    }

    @Override
    public MenuItem add(int i) {
        return null;
    }

    @Override
    public MenuItem add(int i, int i2, int i3, int i4) {
        return add(i, i2, i3, LocaleController.getString(i4));
    }

    @Override
    public MenuItem add(int i, final int i2, int i3, CharSequence charSequence) {
        if (this.premiumLock != null && FloatingToolbar.premiumOptions.contains(Integer.valueOf(i2)) && MessagesController.getInstance(UserConfig.selectedAccount).premiumFeaturesBlocked()) {
            return null;
        }
        this.itemOptions.add(charSequence, new Runnable() {
            @Override
            public final void run() {
                MenuToItemOptions.this.lambda$add$0(i2);
            }
        });
        if (this.premiumLock != null && FloatingToolbar.premiumOptions.contains(Integer.valueOf(i2))) {
            this.itemOptions.putPremiumLock(this.premiumLock);
        }
        return null;
    }

    @Override
    public MenuItem add(CharSequence charSequence) {
        return null;
    }

    @Override
    public int addIntentOptions(int i, int i2, int i3, ComponentName componentName, Intent[] intentArr, Intent intent, int i4, MenuItem[] menuItemArr) {
        return 0;
    }

    @Override
    public SubMenu addSubMenu(int i) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i, int i2, int i3, int i4) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i, int i2, int i3, CharSequence charSequence) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(CharSequence charSequence) {
        return null;
    }

    @Override
    public void clear() {
    }

    @Override
    public void close() {
    }

    @Override
    public MenuItem findItem(int i) {
        return null;
    }

    @Override
    public MenuItem getItem(int i) {
        return null;
    }

    @Override
    public boolean hasVisibleItems() {
        return false;
    }

    @Override
    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean performIdentifierAction(int i, int i2) {
        return false;
    }

    @Override
    public boolean performShortcut(int i, KeyEvent keyEvent, int i2) {
        return false;
    }

    @Override
    public void removeGroup(int i) {
    }

    @Override
    public void removeItem(int i) {
    }

    @Override
    public void setGroupCheckable(int i, boolean z, boolean z2) {
    }

    @Override
    public void setGroupEnabled(int i, boolean z) {
    }

    @Override
    public void setGroupVisible(int i, boolean z) {
    }

    @Override
    public void setQwertyMode(boolean z) {
    }

    @Override
    public int size() {
        return 0;
    }
}
