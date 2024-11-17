package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.GroupCallTabletGridAdapter;

public abstract class GroupCallGridCell extends FrameLayout {
    public boolean attached;
    public GroupCallTabletGridAdapter gridAdapter;
    private final boolean isTabletGrid;
    ChatObject.VideoParticipant participant;
    public int position;
    GroupCallMiniTextureView renderer;
    public int spanCount;

    public GroupCallGridCell(Context context, boolean z) {
        super(context);
        this.isTabletGrid = z;
    }

    public float getItemHeight() {
        return this.gridAdapter != null ? r0.getItemHeight(this.position) : getMeasuredHeight();
    }

    public ChatObject.VideoParticipant getParticipant() {
        return this.participant;
    }

    public GroupCallMiniTextureView getRenderer() {
        return this.renderer;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int dp;
        if (this.isTabletGrid) {
            ((View) getParent()).getMeasuredWidth();
            dp = this.gridAdapter.getItemHeight(this.position);
        } else {
            float f = GroupCallActivity.isLandscapeMode ? 3.0f : 2.0f;
            float measuredWidth = getParent() != null ? ((View) getParent()).getMeasuredWidth() : View.MeasureSpec.getSize(i);
            dp = (int) ((GroupCallActivity.isTabletMode ? measuredWidth / 2.0f : measuredWidth / f) + AndroidUtilities.dp(4.0f));
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(dp, 1073741824));
    }

    public void setData(AccountInstance accountInstance, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call, long j) {
        this.participant = videoParticipant;
    }

    public void setRenderer(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.renderer = groupCallMiniTextureView;
    }
}
