package org.telegram.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class BlurSettingsBottomSheet extends BottomSheet {
    public static float blurAlpha = 1.0f - (Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha)) / 255.0f);
    public static float blurRadius = 1.0f;
    public static float saturation = 1.0f;
    SizeNotifierFrameLayout contentView;
    BaseFragment fragment;

    private BlurSettingsBottomSheet(BaseFragment baseFragment) {
        super(baseFragment.getParentActivity(), false);
        this.fragment = baseFragment;
        if (baseFragment.getFragmentView() instanceof SizeNotifierFrameLayout) {
            this.contentView = (SizeNotifierFrameLayout) baseFragment.getFragmentView();
        }
        Activity parentActivity = baseFragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        final TextView textView = new TextView(parentActivity);
        textView.setText("Saturation " + (saturation * 5.0f));
        int i = Theme.key_dialogTextBlue2;
        textView.setTextColor(Theme.getColor(i));
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBarView = new SeekBarView(parentActivity);
        seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            @Override
            public int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override
            public void onSeekBarDrag(boolean z, float f) {
                BlurSettingsBottomSheet.saturation = f;
                textView.setText("Saturation " + (f * 5.0f));
                BlurSettingsBottomSheet.this.contentView.invalidateBlurredViews();
                BlurSettingsBottomSheet.this.contentView.invalidateBlur();
            }

            @Override
            public void onSeekBarPressed(boolean z) {
            }
        });
        seekBarView.setReportChanges(true);
        linearLayout.addView(seekBarView, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        final TextView textView2 = new TextView(parentActivity);
        textView2.setText("Alpha " + blurAlpha);
        textView2.setTextColor(Theme.getColor(i));
        textView2.setTextSize(1, 16.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(textView2, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBarView2 = new SeekBarView(parentActivity);
        seekBarView2.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            @Override
            public int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override
            public void onSeekBarDrag(boolean z, float f) {
                textView2.setText("Alpha " + BlurSettingsBottomSheet.blurAlpha);
                BlurSettingsBottomSheet.blurAlpha = f;
                BlurSettingsBottomSheet.this.contentView.invalidateBlur();
            }

            @Override
            public void onSeekBarPressed(boolean z) {
            }
        });
        seekBarView2.setReportChanges(true);
        linearLayout.addView(seekBarView2, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView textView3 = new TextView(parentActivity);
        textView3.setText("Blur Radius");
        textView3.setTextColor(Theme.getColor(i));
        textView3.setTextSize(1, 16.0f);
        textView3.setLines(1);
        textView3.setMaxLines(1);
        textView3.setSingleLine(true);
        textView3.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(textView3, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBarView3 = new SeekBarView(parentActivity);
        seekBarView3.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public CharSequence getContentDescription() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
            }

            @Override
            public int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override
            public void onSeekBarDrag(boolean z, float f) {
                BlurSettingsBottomSheet.blurRadius = f;
                BlurSettingsBottomSheet.this.contentView.invalidateBlur();
                BlurSettingsBottomSheet.this.contentView.invalidateBlurredViews();
            }

            @Override
            public void onSeekBarPressed(boolean z) {
                BlurSettingsBottomSheet.this.contentView.invalidateBlurredViews();
            }
        });
        seekBarView3.setReportChanges(true);
        linearLayout.addView(seekBarView3, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        linearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                seekBarView.setProgress(BlurSettingsBottomSheet.saturation);
                seekBarView3.setProgress(BlurSettingsBottomSheet.blurRadius);
                seekBarView2.setProgress(BlurSettingsBottomSheet.blurAlpha);
            }
        });
        ScrollView scrollView = new ScrollView(parentActivity);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    public static void onThemeApplyed() {
        blurAlpha = 1.0f - (Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha, null, true)) / 255.0f);
    }

    public static void show(BaseFragment baseFragment) {
        new BlurSettingsBottomSheet(baseFragment).show();
    }
}
