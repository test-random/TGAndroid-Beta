package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

public class ImportingAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private BottomSheetCell cell;
    private boolean completed;
    private RLottieDrawable completedDrawable;
    private RLottieImageView imageView;
    private TextView[] importCountTextView;
    private TextView[] infoTextView;
    private LineProgressView lineProgressView;
    private final Runnable onFinishCallback;
    private ChatActivity parentFragment;
    private TextView percentTextView;
    private String stickersShortName;

    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private RLottieImageView imageView;
        private LinearLayout linearLayout;
        private Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            View view = new View(context);
            this.background = view;
            int dp = AndroidUtilities.dp(4.0f);
            int i = Theme.key_featuredStickers_addButton;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(dp, getThemedColor(i), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 17));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            int dp2 = AndroidUtilities.dp(20.0f);
            int i2 = Theme.key_featuredStickers_buttonText;
            rLottieImageView.setBackground(Theme.createCircleDrawable(dp2, getThemedColor(i2)));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(i), PorterDuff.Mode.MULTIPLY));
            this.imageView.setAnimation(R.raw.import_check, 26, 26);
            this.imageView.setScaleX(0.8f);
            this.imageView.setScaleY(0.8f);
            this.linearLayout.addView(this.imageView, LayoutHelper.createLinear(20, 20, 16));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(getThemedColor(i2));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.bold());
            this.linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16, 10, 0, 0, 0));
        }

        protected int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }
    }

    public ImportingAlert(Context context, String str, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        NotificationCenter notificationCenter;
        int i;
        this.importCountTextView = new TextView[2];
        this.infoTextView = new TextView[2];
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                ImportingAlert.this.lambda$new$0();
            }
        };
        this.onFinishCallback = runnable;
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        this.parentFragment = chatActivity;
        this.stickersShortName = str;
        FrameLayout frameLayout = new FrameLayout(context);
        setCustomView(frameLayout);
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 20.0f);
        int i2 = Theme.key_dialogTextBlack;
        textView.setTextColor(getThemedColor(i2));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 17.0f, 20.0f, 17.0f, 0.0f));
        int i3 = R.raw.import_finish;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i3, "" + i3, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), false, null);
        this.completedDrawable = rLottieDrawable;
        rLottieDrawable.setAllowDecodeSingleFrame(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.imageView.setAnimation(R.raw.import_loop, 120, 120);
        this.imageView.playAnimation();
        frameLayout.addView(this.imageView, LayoutHelper.createFrame(160, 160.0f, 49, 17.0f, 79.0f, 17.0f, 0.0f));
        this.imageView.getAnimatedDrawable().setOnFinishCallback(runnable, 178);
        TextView textView2 = new TextView(context);
        this.percentTextView = textView2;
        textView2.setTypeface(AndroidUtilities.bold());
        this.percentTextView.setTextSize(1, 24.0f);
        this.percentTextView.setTextColor(getThemedColor(i2));
        frameLayout.addView(this.percentTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 262.0f, 17.0f, 0.0f));
        LineProgressView lineProgressView = new LineProgressView(getContext());
        this.lineProgressView = lineProgressView;
        lineProgressView.setProgressColor(getThemedColor(Theme.key_featuredStickers_addButton));
        this.lineProgressView.setBackColor(getThemedColor(Theme.key_dialogLineProgressBackground));
        frameLayout.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 4.0f, 51, 50.0f, 307.0f, 50.0f, 0.0f));
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context, resourcesProvider);
        this.cell = bottomSheetCell;
        bottomSheetCell.setBackground(null);
        this.cell.setText(LocaleController.getString(R.string.ImportDone));
        this.cell.setVisibility(4);
        this.cell.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ImportingAlert.this.lambda$new$1(view);
            }
        });
        this.cell.background.setPivotY(AndroidUtilities.dp(48.0f));
        this.cell.background.setScaleY(0.04f);
        frameLayout.addView(this.cell, LayoutHelper.createFrame(-1, 50.0f, 51, 34.0f, 247.0f, 34.0f, 0.0f));
        for (int i4 = 0; i4 < 2; i4++) {
            this.importCountTextView[i4] = new TextView(context);
            this.importCountTextView[i4].setTextSize(1, 16.0f);
            this.importCountTextView[i4].setTypeface(AndroidUtilities.bold());
            this.importCountTextView[i4].setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            frameLayout.addView(this.importCountTextView[i4], LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 340.0f, 17.0f, 0.0f));
            this.infoTextView[i4] = new TextView(context);
            this.infoTextView[i4].setTextSize(1, 14.0f);
            this.infoTextView[i4].setTextColor(getThemedColor(Theme.key_dialogTextGray3));
            this.infoTextView[i4].setGravity(1);
            frameLayout.addView(this.infoTextView[i4], LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 368.0f, 30.0f, 44.0f));
            TextView[] textViewArr = this.infoTextView;
            if (i4 == 0) {
                textViewArr[i4].setText(LocaleController.getString(R.string.ImportImportingInfo));
            } else {
                textViewArr[i4].setAlpha(0.0f);
                this.infoTextView[i4].setTranslationY(AndroidUtilities.dp(10.0f));
                this.importCountTextView[i4].setAlpha(0.0f);
                this.importCountTextView[i4].setTranslationY(AndroidUtilities.dp(10.0f));
            }
        }
        if (this.parentFragment != null) {
            textView.setText(LocaleController.getString(R.string.ImportImportingTitle));
            SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(this.parentFragment.getDialogId());
            this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingHistory.uploadProgress)));
            this.lineProgressView.setProgress(importingHistory.uploadProgress / 100.0f, false);
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
            this.infoTextView[1].setText(LocaleController.getString(R.string.ImportDoneInfo));
            this.importCountTextView[1].setText(LocaleController.getString(R.string.ImportDoneTitle));
            notificationCenter = this.parentFragment.getNotificationCenter();
            i = NotificationCenter.historyImportProgressChanged;
        } else {
            textView.setText(LocaleController.getString(R.string.ImportStickersImportingTitle));
            SendMessagesHelper.ImportingStickers importingStickers = SendMessagesHelper.getInstance(this.currentAccount).getImportingStickers(str);
            this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingStickers.uploadProgress)));
            this.lineProgressView.setProgress(importingStickers.uploadProgress / 100.0f, false);
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingStickers.getUploadedCount()), AndroidUtilities.formatFileSize(importingStickers.getTotalCount())));
            this.infoTextView[1].setText(LocaleController.getString(R.string.ImportStickersDoneInfo));
            this.importCountTextView[1].setText(LocaleController.getString(R.string.ImportStickersDoneTitle));
            notificationCenter = NotificationCenter.getInstance(this.currentAccount);
            i = NotificationCenter.stickersImportProgressChanged;
        }
        notificationCenter.addObserver(this, i);
    }

    public void lambda$new$0() {
        if (this.completed) {
            this.imageView.getAnimatedDrawable().setAutoRepeat(0);
            this.imageView.setAnimation(this.completedDrawable);
            this.imageView.playAnimation();
        }
    }

    public void lambda$new$1(View view) {
        lambda$new$0();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        LineProgressView lineProgressView;
        int i3;
        if (i == NotificationCenter.historyImportProgressChanged) {
            if (objArr.length > 1) {
                lambda$new$0();
                return;
            }
            SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(this.parentFragment.getDialogId());
            if (importingHistory == null) {
                setCompleted();
                return;
            }
            if (!this.completed) {
                double currentFrame = 180 - this.imageView.getAnimatedDrawable().getCurrentFrame();
                Double.isNaN(currentFrame);
                if ((currentFrame * 16.6d) + 3000.0d >= importingHistory.timeUntilFinish) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingHistory.uploadProgress)));
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
            lineProgressView = this.lineProgressView;
            i3 = importingHistory.uploadProgress;
        } else {
            if (i != NotificationCenter.stickersImportProgressChanged) {
                return;
            }
            if (objArr.length > 1) {
                lambda$new$0();
                return;
            }
            SendMessagesHelper.ImportingStickers importingStickers = SendMessagesHelper.getInstance(this.currentAccount).getImportingStickers(this.stickersShortName);
            if (importingStickers == null) {
                setCompleted();
                return;
            }
            if (!this.completed) {
                double currentFrame2 = 180 - this.imageView.getAnimatedDrawable().getCurrentFrame();
                Double.isNaN(currentFrame2);
                if ((currentFrame2 * 16.6d) + 3000.0d >= importingStickers.timeUntilFinish) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingStickers.uploadProgress)));
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingStickers.getUploadedCount()), AndroidUtilities.formatFileSize(importingStickers.getTotalCount())));
            lineProgressView = this.lineProgressView;
            i3 = importingStickers.uploadProgress;
        }
        lineProgressView.setProgress(i3 / 100.0f, true);
    }

    @Override
    public void dismissInternal() {
        NotificationCenter notificationCenter;
        int i;
        super.dismissInternal();
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            notificationCenter = chatActivity.getNotificationCenter();
            i = NotificationCenter.historyImportProgressChanged;
        } else {
            notificationCenter = NotificationCenter.getInstance(this.currentAccount);
            i = NotificationCenter.stickersImportProgressChanged;
        }
        notificationCenter.removeObserver(this, i);
    }

    public void setCompleted() {
        this.completed = true;
        this.imageView.setAutoRepeat(false);
        this.cell.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250L);
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        TextView textView = this.percentTextView;
        Property property = View.ALPHA;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property, 0.0f);
        TextView textView2 = this.percentTextView;
        Property property2 = View.TRANSLATION_Y;
        animatorSet.playTogether(ofFloat, ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property2, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.infoTextView[0], (Property<TextView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.infoTextView[0], (Property<TextView, Float>) property2, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.importCountTextView[0], (Property<TextView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.importCountTextView[0], (Property<TextView, Float>) property2, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.infoTextView[1], (Property<TextView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.infoTextView[1], (Property<TextView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.importCountTextView[1], (Property<TextView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.importCountTextView[1], (Property<TextView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.lineProgressView, (Property<LineProgressView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.cell.linearLayout, (Property<LinearLayout, Float>) property2, AndroidUtilities.dp(8.0f), 0.0f));
        this.cell.background.animate().scaleY(1.0f).setInterpolator(new OvershootInterpolator(1.02f)).setDuration(250L).start();
        this.cell.imageView.animate().scaleY(1.0f).scaleX(1.0f).setInterpolator(new OvershootInterpolator(1.02f)).setDuration(250L).start();
        this.cell.imageView.playAnimation();
        animatorSet.start();
    }
}
