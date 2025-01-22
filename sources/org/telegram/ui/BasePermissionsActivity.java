package org.telegram.ui;

import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.FragmentActivity;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public abstract class BasePermissionsActivity extends FragmentActivity {
    protected int currentAccount = -1;

    public void lambda$createPermissionErrorAlert$0(AlertDialog alertDialog, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void showPermissionErrorAlert(int i, String str) {
        createPermissionErrorAlert(i, str).show();
    }

    public boolean checkPermissionsResult(int r10, java.lang.String[] r11, int[] r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.BasePermissionsActivity.checkPermissionsResult(int, java.lang.String[], int[]):boolean");
    }

    public AlertDialog createPermissionErrorAlert(int i, String str) {
        return new AlertDialog.Builder(this).setTopAnimation(i, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setMessage(AndroidUtilities.replaceTags(str)).setPositiveButton(LocaleController.getString(R.string.PermissionOpenSettings), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                BasePermissionsActivity.this.lambda$createPermissionErrorAlert$0(alertDialog, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), null).create();
    }
}
