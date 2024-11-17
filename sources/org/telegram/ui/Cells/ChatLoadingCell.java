package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class ChatLoadingCell extends FrameLayout {
    private int backgroundHeight;
    private FrameLayout frameLayout;
    private RadialProgressView progressBar;
    private Theme.ResourcesProvider resourcesProvider;
    private float viewTop;

    public ChatLoadingCell(Context context, View view, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        FrameLayout frameLayout = new FrameLayout(context) {
            private final RectF rect = new RectF();

            @Override
            protected void dispatchDraw(Canvas canvas) {
                this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
                ChatLoadingCell.this.applyServiceShaderMatrix();
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), ChatLoadingCell.this.getThemedPaint("paintChatActionBackground"));
                if (ChatLoadingCell.this.hasGradientService()) {
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), ChatLoadingCell.this.getThemedPaint("paintChatActionBackgroundDarken"));
                }
                super.dispatchDraw(canvas);
            }
        };
        this.frameLayout = frameLayout;
        frameLayout.setWillNotDraw(false);
        addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context, resourcesProvider);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(getThemedColor(Theme.key_chat_serviceText));
        this.frameLayout.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
    }

    private void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(i, i2, f, f2);
        } else {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public void applyServiceShaderMatrix() {
        applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, getX(), this.viewTop);
    }

    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), 1073741824));
    }

    public void setProgressVisible(boolean z) {
        this.frameLayout.setVisibility(z ? 0 : 4);
    }

    public void setVisiblePart(float f, int i) {
        if (this.viewTop != f) {
            invalidate();
        }
        this.viewTop = f;
        this.backgroundHeight = i;
    }
}
