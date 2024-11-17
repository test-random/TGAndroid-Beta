package org.telegram.ui;

import android.content.Intent;
import java.util.ArrayList;

public class ContactsWidgetConfigActivity extends ExternalActionActivity {
    private int creatingAppWidgetId = 0;

    public void lambda$handleIntent$0(ArrayList arrayList) {
        Intent intent = new Intent();
        intent.putExtra("appWidgetId", this.creatingAppWidgetId);
        setResult(-1, intent);
        finish();
    }

    @Override
    protected boolean handleIntent(android.content.Intent r1, boolean r2, boolean r3, boolean r4, int r5, int r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsWidgetConfigActivity.handleIntent(android.content.Intent, boolean, boolean, boolean, int, int):boolean");
    }
}
