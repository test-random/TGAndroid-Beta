package org.telegram.ui.ActionBar;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;

public class BottomSheetTabDialog extends Dialog {
    private boolean attached;
    public final View navigationBar;
    public final Paint navigationBarPaint;
    public final BottomSheetTabsOverlay.Sheet sheet;
    public final BottomSheetTabsOverlay.SheetView sheetView;
    public final WindowView windowView;

    public static class WindowView extends FrameLayout implements BottomSheetTabsOverlay.SheetView {
        public final BottomSheetTabsOverlay.SheetView sheetView;

        public WindowView(BottomSheetTabsOverlay.SheetView sheetView) {
            super(sheetView.getContext());
            this.sheetView = sheetView;
        }

        @Override
        public float drawInto(Canvas canvas, RectF rectF, float f, RectF rectF2, float f2, boolean z) {
            return this.sheetView.drawInto(canvas, rectF, f, rectF2, f2, z);
        }

        @Override
        public RectF getRect() {
            return this.sheetView.getRect();
        }

        public void putView() {
            View view = (View) this.sheetView;
            AndroidUtilities.removeFromParent(view);
            addView(view, LayoutHelper.createFrame(-1, -1, 119));
        }

        @Override
        public void setDrawingFromOverlay(boolean z) {
            this.sheetView.setDrawingFromOverlay(z);
        }
    }

    public BottomSheetTabDialog(BottomSheetTabsOverlay.Sheet sheet) {
        super(sheet.mo997getWindowView().getContext(), R.style.TransparentDialog);
        Paint paint = new Paint(1);
        this.navigationBarPaint = paint;
        this.sheet = sheet;
        BottomSheetTabsOverlay.SheetView mo997getWindowView = sheet.mo997getWindowView();
        this.sheetView = mo997getWindowView;
        View view = new View(getContext()) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), BottomSheetTabDialog.this.navigationBarPaint);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.navigationBarHeight);
                setTranslationY(AndroidUtilities.navigationBarHeight);
            }
        };
        this.navigationBar = view;
        paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
        WindowView windowView = new WindowView(mo997getWindowView);
        this.windowView = windowView;
        setContentView(windowView, new ViewGroup.LayoutParams(-1, -1));
        windowView.addView(view, LayoutHelper.createFrame(-1, -2, 80));
        windowView.setClipToPadding(false);
    }

    public static BottomSheetTabsOverlay.Sheet checkSheet(BottomSheetTabsOverlay.Sheet sheet) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return sheet;
        }
        if (AndroidUtilities.isTablet() || sheet.hadDialog() || AndroidUtilities.hasDialogOnTop(safeLastFragment)) {
            BottomSheetTabDialog bottomSheetTabDialog = new BottomSheetTabDialog(sheet);
            if (sheet.setDialog(bottomSheetTabDialog)) {
                bottomSheetTabDialog.windowView.putView();
            }
        }
        return sheet;
    }

    public static WindowInsets lambda$onCreate$0(View view, WindowInsets windowInsets) {
        int systemWindowInsetBottom;
        WindowInsets consumeSystemWindowInsets;
        WindowInsets windowInsets2;
        systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
        view.setPadding(0, 0, 0, systemWindowInsetBottom);
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsets2 = WindowInsets.CONSUMED;
            return windowInsets2;
        }
        consumeSystemWindowInsets = windowInsets.consumeSystemWindowInsets();
        return consumeSystemWindowInsets;
    }

    public void attach() {
        if (this.attached) {
            return;
        }
        this.attached = true;
        try {
            super.show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void detach() {
        this.sheet.setDialog(null);
        if (this.attached) {
            this.attached = false;
            try {
                super.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @Override
    public void dismiss() {
        this.sheet.dismiss(false);
    }

    @Override
    protected void onCreate(android.os.Bundle r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BottomSheetTabDialog.onCreate(android.os.Bundle):void");
    }

    public void updateNavigationBarColor() {
        int navigationBarColor = this.sheet.getNavigationBarColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.navigationBarPaint.setColor(navigationBarColor);
        this.navigationBar.invalidate();
        AndroidUtilities.setNavigationBarColor(getWindow(), navigationBarColor);
        AndroidUtilities.setLightNavigationBar(getWindow(), AndroidUtilities.computePerceivedBrightness(navigationBarColor) >= 0.721f);
        LaunchActivity.instance.checkSystemBarColors(true, true, true, false);
    }
}
