package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;

public class URLSpanCopyToClipboard extends URLSpanNoUnderline {
    private BaseFragment fragment;

    public URLSpanCopyToClipboard(String str, BaseFragment baseFragment) {
        super(str);
        this.fragment = baseFragment;
    }

    @Override
    public void onClick(View view) {
        AndroidUtilities.addToClipboard(getURL());
        BulletinFactory.of(this.fragment).createCopyLinkBulletin().show();
    }
}
