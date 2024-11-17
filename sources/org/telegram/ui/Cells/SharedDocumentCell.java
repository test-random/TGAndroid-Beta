package org.telegram.ui.Cells;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.FilteredSearchView;

public class SharedDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private CharSequence caption;
    private TextView captionTextView;
    private CheckBox2 checkBox;
    private int currentAccount;
    private AnimatedEmojiSpan.TextViewEmojis dateTextView;
    private SpannableStringBuilder dotSpan;
    private long downloadedSize;
    private boolean drawDownloadIcon;
    float enterAlpha;
    private TextView extTextView;
    FlickerLoadingView globalGradientView;
    boolean ignoreRequestLayout;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    private ImageView placeholderImageView;
    private LineProgressView progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    public TextView rightDateTextView;
    boolean showReorderIcon;
    float showReorderIconProgress;
    private RLottieDrawable statusDrawable;
    private RLottieImageView statusImageView;
    private BackupImageView thumbImageView;
    private int viewType;

    public SharedDocumentCell(Context context, int i) {
        this(context, i, null);
    }

    public SharedDocumentCell(android.content.Context r29, int r30, org.telegram.ui.ActionBar.Theme.ResourcesProvider r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedDocumentCell.<init>(android.content.Context, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    private void drawDivider(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(AndroidUtilities.dp(72.0f), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.getThemePaint("paintDivider", this.resourcesProvider));
        }
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private void updateDateView() {
        MessageObject messageObject = this.message;
        if (messageObject == null || messageObject.getDocument() == null) {
            return;
        }
        MessageObject messageObject2 = this.message;
        long j = messageObject2.messageOwner.date * 1000;
        long j2 = this.downloadedSize;
        String formatFileSize = j2 == 0 ? AndroidUtilities.formatFileSize(messageObject2.getDocument().size) : String.format(Locale.ENGLISH, "%s / %s", AndroidUtilities.formatFileSize(j2), AndroidUtilities.formatFileSize(this.message.getDocument().size));
        if (this.viewType != 2) {
            this.dateTextView.setText(String.format("%s, %s", formatFileSize, LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().getFormatterYear().format(new Date(j)), LocaleController.getInstance().getFormatterDay().format(new Date(j)))));
        } else {
            this.dateTextView.setText(new SpannableStringBuilder().append((CharSequence) formatFileSize).append(' ').append((CharSequence) this.dotSpan).append(' ').append(FilteredSearchView.createFromInfoString(this.message, true, 2, this.dateTextView.getPaint())));
            this.rightDateTextView.setText(LocaleController.stringForMessageListDate(this.message.messageOwner.date));
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.enterAlpha == 1.0f || this.globalGradientView == null) {
            super.dispatchDraw(canvas);
            drawDivider(canvas);
        } else {
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
            this.globalGradientView.setViewType(3);
            this.globalGradientView.updateColors();
            this.globalGradientView.updateGradient();
            this.globalGradientView.draw(canvas);
            canvas.restore();
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) (this.enterAlpha * 255.0f), 31);
            super.dispatchDraw(canvas);
            drawDivider(canvas);
            canvas.restore();
        }
        boolean z = this.showReorderIcon;
        if (z || this.showReorderIconProgress != 0.0f) {
            if (z) {
                float f = this.showReorderIconProgress;
                if (f != 1.0f) {
                    this.showReorderIconProgress = f + 0.10666667f;
                    invalidate();
                    this.showReorderIconProgress = Utilities.clamp(this.showReorderIconProgress, 1.0f, 0.0f);
                    int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(12.0f)) - Theme.dialogs_reorderDrawable.getIntrinsicWidth();
                    int measuredHeight = (getMeasuredHeight() - Theme.dialogs_reorderDrawable.getIntrinsicHeight()) >> 1;
                    canvas.save();
                    float f2 = this.showReorderIconProgress;
                    canvas.scale(f2, f2, measuredWidth + (Theme.dialogs_reorderDrawable.getIntrinsicWidth() / 2.0f), measuredHeight + (Theme.dialogs_reorderDrawable.getIntrinsicHeight() / 2.0f));
                    Drawable drawable = Theme.dialogs_reorderDrawable;
                    drawable.setBounds(measuredWidth, measuredHeight, drawable.getIntrinsicWidth() + measuredWidth, Theme.dialogs_reorderDrawable.getIntrinsicHeight() + measuredHeight);
                    Theme.dialogs_reorderDrawable.draw(canvas);
                    canvas.restore();
                }
            }
            if (!z) {
                float f3 = this.showReorderIconProgress;
                if (f3 != 0.0f) {
                    this.showReorderIconProgress = f3 - 0.10666667f;
                    invalidate();
                }
            }
            this.showReorderIconProgress = Utilities.clamp(this.showReorderIconProgress, 1.0f, 0.0f);
            int measuredWidth2 = (getMeasuredWidth() - AndroidUtilities.dp(12.0f)) - Theme.dialogs_reorderDrawable.getIntrinsicWidth();
            int measuredHeight2 = (getMeasuredHeight() - Theme.dialogs_reorderDrawable.getIntrinsicHeight()) >> 1;
            canvas.save();
            float f22 = this.showReorderIconProgress;
            canvas.scale(f22, f22, measuredWidth2 + (Theme.dialogs_reorderDrawable.getIntrinsicWidth() / 2.0f), measuredHeight2 + (Theme.dialogs_reorderDrawable.getIntrinsicHeight() / 2.0f));
            Drawable drawable2 = Theme.dialogs_reorderDrawable;
            drawable2.setBounds(measuredWidth2, measuredHeight2, drawable2.getIntrinsicWidth() + measuredWidth2, Theme.dialogs_reorderDrawable.getIntrinsicHeight() + measuredHeight2);
            Theme.dialogs_reorderDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public BackupImageView getImageView() {
        return this.thumbImageView;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return this.loading;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.progressView.getVisibility() == 0) {
            updateFileExistIcon(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateFileExistIcon(true);
        this.downloadedSize = 0L;
        updateDateView();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(this.checkBox.isChecked());
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        TextView textView;
        super.onLayout(z, i, i2, i3, i4);
        if (this.viewType != 1) {
            if (this.nameTextView.getLineCount() > 1 || ((textView = this.captionTextView) != null && textView.getVisibility() == 0)) {
                int measuredHeight = this.nameTextView.getMeasuredHeight() - AndroidUtilities.dp(22.0f);
                TextView textView2 = this.captionTextView;
                if (textView2 != null && textView2.getVisibility() == 0) {
                    TextView textView3 = this.captionTextView;
                    textView3.layout(textView3.getLeft(), this.captionTextView.getTop() + measuredHeight, this.captionTextView.getRight(), this.captionTextView.getBottom() + measuredHeight);
                    measuredHeight += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
                }
                AnimatedEmojiSpan.TextViewEmojis textViewEmojis = this.dateTextView;
                textViewEmojis.layout(textViewEmojis.getLeft(), this.dateTextView.getTop() + measuredHeight, this.dateTextView.getRight(), this.dateTextView.getBottom() + measuredHeight);
                RLottieImageView rLottieImageView = this.statusImageView;
                rLottieImageView.layout(rLottieImageView.getLeft(), this.statusImageView.getTop() + measuredHeight, this.statusImageView.getRight(), measuredHeight + this.statusImageView.getBottom());
                LineProgressView lineProgressView = this.progressView;
                lineProgressView.layout(lineProgressView.getLeft(), (getMeasuredHeight() - this.progressView.getMeasuredHeight()) - (this.needDivider ? 1 : 0), this.progressView.getRight(), getMeasuredHeight() - (this.needDivider ? 1 : 0));
            }
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int makeMeasureSpec;
        int dp;
        int i3 = this.viewType;
        if (i3 == 1) {
            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
            dp = AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0);
        } else {
            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
            if (i3 != 0) {
                super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824));
                int dp2 = AndroidUtilities.dp(34.0f) + this.nameTextView.getMeasuredHeight() + (this.needDivider ? 1 : 0);
                if (this.caption != null && this.captionTextView != null && this.message.hasHighlightedWords()) {
                    this.ignoreRequestLayout = true;
                    this.captionTextView.setText(AndroidUtilities.ellipsizeCenterEnd(this.caption, this.message.highlightedWords.get(0), this.captionTextView.getMeasuredWidth(), this.captionTextView.getPaint(), 130));
                    this.ignoreRequestLayout = false;
                    dp2 += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
                }
                setMeasuredDimension(getMeasuredWidth(), dp2);
                return;
            }
            dp = AndroidUtilities.dp(56.0f);
        }
        super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(dp, 1073741824));
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon(true);
        }
        this.downloadedSize = j;
        updateDateView();
        this.progressView.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSuccessDownload(String str) {
        this.progressView.setProgress(1.0f, true);
        updateFileExistIcon(true);
        this.downloadedSize = 0L;
        updateDateView();
    }

    @Override
    public void requestLayout() {
        if (this.ignoreRequestLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setDocument(org.telegram.messenger.MessageObject r17, boolean r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedDocumentCell.setDocument(org.telegram.messenger.MessageObject, boolean):void");
    }

    public void setDrawDownloadIcon(boolean z) {
        this.drawDownloadIcon = z;
    }

    public void setEnterAnimationAlpha(float f) {
        if (this.enterAlpha != f) {
            this.enterAlpha = f;
            invalidate();
        }
    }

    public void setGlobalGradientView(FlickerLoadingView flickerLoadingView) {
        this.globalGradientView = flickerLoadingView;
    }

    public void setPhoto(String str) {
        BackupImageView backupImageView;
        StringBuilder sb;
        String str2;
        if (str.endsWith("mp4")) {
            backupImageView = this.thumbImageView;
            sb = new StringBuilder();
            str2 = "vthumb://0:";
        } else if (!str.endsWith(".jpg") && !str.endsWith(".jpeg") && !str.endsWith(".png") && !str.endsWith(".gif")) {
            this.thumbImageView.setVisibility(8);
            return;
        } else {
            backupImageView = this.thumbImageView;
            sb = new StringBuilder();
            str2 = "thumb://0:";
        }
        sb.append(str2);
        sb.append(str);
        backupImageView.setImage(sb.toString(), null, null);
        this.thumbImageView.setVisibility(0);
    }

    public void setPhotoEntry(MediaController.PhotoEntry photoEntry) {
        String str;
        BackupImageView backupImageView;
        StringBuilder sb;
        String str2;
        String str3 = photoEntry.thumbPath;
        if (str3 != null) {
            this.thumbImageView.setImage(str3, null, Theme.chat_attachEmptyDrawable);
            str = photoEntry.thumbPath;
        } else if (photoEntry.path != null) {
            if (photoEntry.isVideo) {
                this.thumbImageView.setOrientation(0, true);
                backupImageView = this.thumbImageView;
                sb = new StringBuilder();
                str2 = "vthumb://";
            } else {
                this.thumbImageView.setOrientation(photoEntry.orientation, photoEntry.invert, true);
                backupImageView = this.thumbImageView;
                sb = new StringBuilder();
                str2 = "thumb://";
            }
            sb.append(str2);
            sb.append(photoEntry.imageId);
            sb.append(":");
            sb.append(photoEntry.path);
            backupImageView.setImage(sb.toString(), null, Theme.chat_attachEmptyDrawable);
            str = photoEntry.path;
        } else {
            this.thumbImageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            str = "";
        }
        File file = new File(str);
        this.nameTextView.setText(file.getName());
        FileLoader.getFileExtension(file);
        this.extTextView.setVisibility(8);
        StringBuilder sb2 = new StringBuilder();
        if (photoEntry.width != 0 && photoEntry.height != 0) {
            if (sb2.length() > 0) {
                sb2.append(", ");
            }
            sb2.append(String.format(Locale.US, "%dx%d", Integer.valueOf(photoEntry.width), Integer.valueOf(photoEntry.height)));
        }
        if (photoEntry.isVideo) {
            if (sb2.length() > 0) {
                sb2.append(", ");
            }
            sb2.append(AndroidUtilities.formatShortDuration(photoEntry.duration));
        }
        if (photoEntry.size != 0) {
            if (sb2.length() > 0) {
                sb2.append(", ");
            }
            sb2.append(AndroidUtilities.formatFileSize(photoEntry.size));
        }
        if (sb2.length() > 0) {
            sb2.append(", ");
        }
        sb2.append(LocaleController.getInstance().getFormatterStats().format(photoEntry.dateTaken));
        this.dateTextView.setText(sb2);
        this.placeholderImageView.setVisibility(8);
    }

    public void setTextAndValueAndTypeAndThumb(String str, String str2, String str3, String str4, int i, boolean z) {
        int i2;
        int i3;
        this.nameTextView.setText(str);
        this.dateTextView.setText(str2);
        TextView textView = this.extTextView;
        if (str3 != null) {
            textView.setVisibility(0);
            this.extTextView.setText(str3.toLowerCase());
        } else {
            textView.setVisibility(4);
        }
        this.needDivider = z;
        if (i == 0) {
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(str, str3, false));
            this.placeholderImageView.setVisibility(0);
        } else {
            this.placeholderImageView.setVisibility(4);
        }
        if (str4 == null && i == 0) {
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            if (this.viewType != 3) {
                this.thumbImageView.setImageBitmap(null);
                this.thumbImageView.setVisibility(4);
            }
        } else {
            if (str4 == null) {
                CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(42.0f), i);
                if (i == R.drawable.files_storage) {
                    i2 = Theme.key_chat_attachLocationBackground;
                } else if (i == R.drawable.files_gallery) {
                    i2 = Theme.key_chat_attachContactBackground;
                } else if (i == R.drawable.files_music) {
                    i2 = Theme.key_chat_attachAudioBackground;
                } else if (i == R.drawable.files_internal) {
                    i2 = Theme.key_chat_attachGalleryBackground;
                } else {
                    i2 = Theme.key_files_folderIconBackground;
                    i3 = Theme.key_files_folderIcon;
                    Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(i2), false);
                    Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(i3), true);
                    this.thumbImageView.setImageDrawable(createCircleDrawableWithIcon);
                }
                i3 = Theme.key_chat_attachIcon;
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(i2), false);
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(i3), true);
                this.thumbImageView.setImageDrawable(createCircleDrawableWithIcon);
            } else if (this.viewType != 3) {
                this.thumbImageView.setImage(str4, "42_42", null);
            }
            this.thumbImageView.setVisibility(0);
        }
        setWillNotDraw(true ^ this.needDivider);
    }

    public void showReorderIcon(boolean z, boolean z2) {
        if (this.showReorderIcon == z) {
            return;
        }
        this.showReorderIcon = z;
        if (!z2) {
            this.showReorderIconProgress = z ? 1.0f : 0.0f;
        }
        invalidate();
    }

    public void updateFileExistIcon(boolean z) {
        if (z) {
            TransitionSet transitionSet = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150L);
            transitionSet.addTransition(new Fade().setDuration(150L)).addTransition(changeBounds);
            transitionSet.setOrdering(0);
            transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT);
            TransitionManager.beginDelayedTransition(this, transitionSet);
        }
        MessageObject messageObject = this.message;
        if (messageObject == null || messageObject.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressView.setVisibility(4);
            this.progressView.setProgress(0.0f, false);
            this.statusImageView.setVisibility(4);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 72.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 72.0f : 8.0f);
                this.dateTextView.requestLayout();
            }
        } else {
            this.loaded = false;
            if (!messageObject.attachPathExists && !messageObject.mediaExists && this.drawDownloadIcon) {
                String attachFileName = FileLoader.getAttachFileName(messageObject.getDocument());
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, this.message, this);
                this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                this.statusImageView.setVisibility(0);
                this.statusDrawable.setCustomEndFrame(this.loading ? 15 : 0);
                this.statusDrawable.setPlayInDirectionOfCustomEndFrame(true);
                if (z) {
                    this.statusImageView.playAnimation();
                } else {
                    this.statusDrawable.setCurrentFrame(this.loading ? 15 : 0);
                    this.statusImageView.invalidate();
                }
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
                if (layoutParams2 != null) {
                    layoutParams2.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 86.0f);
                    layoutParams2.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 86.0f : 8.0f);
                    this.dateTextView.requestLayout();
                }
                if (!this.loading) {
                    this.progressView.setVisibility(4);
                    return;
                }
                this.progressView.setVisibility(0);
                Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                if (fileProgress == null) {
                    fileProgress = Float.valueOf(0.0f);
                }
                this.progressView.setProgress(fileProgress.floatValue(), false);
                return;
            }
            this.statusImageView.setVisibility(4);
            this.progressView.setVisibility(4);
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
            if (layoutParams3 != null) {
                layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 72.0f);
                layoutParams3.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 72.0f : 8.0f);
                this.dateTextView.requestLayout();
            }
            this.loading = false;
            this.loaded = true;
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }
}
