package org.telegram.ui.Cells;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public abstract class MaxFileSizeCell extends FrameLayout {
    private long currentSize;
    private SeekBarView seekBarView;
    private TextView sizeTextView;
    private TextView textView;

    public MaxFileSizeCell(Context context) {
        super(context);
        setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setImportantForAccessibility(2);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.sizeTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.sizeTextView.setTextSize(1, 16.0f);
        this.sizeTextView.setLines(1);
        this.sizeTextView.setMaxLines(1);
        this.sizeTextView.setSingleLine(true);
        this.sizeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        this.sizeTextView.setImportantForAccessibility(2);
        addView(this.sizeTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        SeekBarView seekBarView = new SeekBarView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.seekBarView = seekBarView;
        seekBarView.setReportChanges(true);
        this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public CharSequence getContentDescription() {
                return ((Object) MaxFileSizeCell.this.textView.getText()) + " " + ((Object) MaxFileSizeCell.this.sizeTextView.getText());
            }

            @Override
            public int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override
            public void onSeekBarDrag(boolean z, float f) {
                int i;
                float f2;
                float f3;
                float f4;
                if (f <= 0.25f) {
                    f2 = 512000;
                    f3 = f / 0.25f;
                    f4 = 536576.0f;
                } else {
                    float f5 = f - 0.25f;
                    if (f5 < 0.25f) {
                        f2 = 1048576;
                        f3 = f5 / 0.25f;
                        f4 = 9437184.0f;
                    } else {
                        float f6 = f5 - 0.25f;
                        if (f6 > 0.25f) {
                            i = (int) (104857600 + (((float) (2097152000 - 104857600)) * ((f6 - 0.25f) / 0.25f)));
                            long j = i;
                            MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", R.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize(j)));
                            MaxFileSizeCell.this.currentSize = j;
                            MaxFileSizeCell.this.didChangedSizeValue(i);
                        }
                        f2 = 10485760;
                        f3 = f6 / 0.25f;
                        f4 = 9.437184E7f;
                    }
                }
                i = (int) (f2 + (f3 * f4));
                long j2 = i;
                MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", R.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize(j2)));
                MaxFileSizeCell.this.currentSize = j2;
                MaxFileSizeCell.this.didChangedSizeValue(i);
            }

            @Override
            public void onSeekBarPressed(boolean z) {
            }
        });
        this.seekBarView.setImportantForAccessibility(2);
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 38.0f, 51, 6.0f, 36.0f, 6.0f, 0.0f));
        setImportantForAccessibility(1);
        setAccessibilityDelegate(this.seekBarView.getSeekBarAccessibilityDelegate());
    }

    protected abstract void didChangedSizeValue(int i);

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    public long getSize() {
        return this.currentSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return true;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(80.0f));
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(42.0f);
        this.sizeTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (measuredWidth - this.sizeTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f)), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
        this.seekBarView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - AndroidUtilities.dp(20.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    public void setEnabled(boolean z, ArrayList arrayList) {
        super.setEnabled(z);
        if (arrayList != null) {
            arrayList.add(ObjectAnimator.ofFloat(this.textView, "alpha", z ? 1.0f : 0.5f));
            arrayList.add(ObjectAnimator.ofFloat(this.seekBarView, "alpha", z ? 1.0f : 0.5f));
            arrayList.add(ObjectAnimator.ofFloat(this.sizeTextView, "alpha", z ? 1.0f : 0.5f));
        } else {
            this.textView.setAlpha(z ? 1.0f : 0.5f);
            this.seekBarView.setAlpha(z ? 1.0f : 0.5f);
            this.sizeTextView.setAlpha(z ? 1.0f : 0.5f);
        }
    }

    public void setSize(long j) {
        float max;
        float f;
        float f2;
        this.currentSize = j;
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", R.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize(j)));
        long j2 = j - 512000;
        if (j2 < 536576) {
            f2 = Math.max(0.0f, ((float) j2) / 536576.0f) * 0.25f;
        } else {
            long j3 = j - 1048576;
            if (j3 < 9437184) {
                f2 = (Math.max(0.0f, ((float) j3) / 9437184.0f) * 0.25f) + 0.25f;
            } else {
                long j4 = j - 10485760;
                if (j4 < 94371840) {
                    max = Math.max(0.0f, ((float) j4) / 9.437184E7f) * 0.25f;
                    f = 0.5f;
                } else {
                    max = Math.max(0.0f, ((float) (j - 104857600)) / 1.9922944E9f) * 0.25f;
                    f = 0.75f;
                }
                f2 = max + f;
            }
        }
        this.seekBarView.setProgress(Math.min(1.0f, f2));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }
}
