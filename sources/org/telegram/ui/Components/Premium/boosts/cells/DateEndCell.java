package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DateEndCell extends FrameLayout {
    private final Theme.ResourcesProvider resourcesProvider;
    private long selectedTime;
    private final SimpleTextView timeTextView;
    private final SimpleTextView titleTextView;

    public DateEndCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(simpleTextView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.timeTextView = simpleTextView2;
        simpleTextView2.setTextSize(16);
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
        simpleTextView2.setGravity(LocaleController.isRTL ? 3 : 5);
        addView(simpleTextView2);
        simpleTextView.setText(LocaleController.formatString("BoostingDateAndTime", R.string.BoostingDateAndTime, new Object[0]));
        boolean z = LocaleController.isRTL;
        simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 16, z ? 0.0f : 21.0f, 0.0f, z ? 21.0f : 0.0f, 0.0f));
        boolean z2 = LocaleController.isRTL;
        simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z2 ? 3 : 5) | 16, z2 ? 21.0f : 0.0f, 0.0f, z2 ? 0.0f : 21.0f, 0.0f));
        setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
    }

    public long getSelectedTime() {
        return this.selectedTime;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
    }

    public void setDate(long j) {
        this.selectedTime = j;
        Date date = new Date(j);
        this.timeTextView.setText(LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().getFormatterDayMonth().format(date), LocaleController.getInstance().getFormatterDay().format(date)));
    }
}
