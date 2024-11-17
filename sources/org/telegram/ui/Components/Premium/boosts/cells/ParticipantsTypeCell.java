package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import org.telegram.ui.ActionBar.Theme;

public class ParticipantsTypeCell extends BaseCell {
    public static int TYPE_ALL = 0;
    public static int TYPE_NEW = 1;
    private int selectedType;

    public ParticipantsTypeCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.imageView.setVisibility(8);
    }

    public int getSelectedType() {
        return this.selectedType;
    }

    @Override
    protected boolean needCheck() {
        return true;
    }

    public void setType(int r6, boolean r7, boolean r8, java.util.List r9, org.telegram.tgnet.TLRPC.Chat r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.boosts.cells.ParticipantsTypeCell.setType(int, boolean, boolean, java.util.List, org.telegram.tgnet.TLRPC$Chat):void");
    }
}
