package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

public class OtherDocumentPlaceholderDrawable extends RecyclableDrawable implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private String ext;
    private String fileName;
    private String fileSize;
    private boolean loaded;
    private boolean loading;
    private MessageObject parentMessageObject;
    private View parentView;
    private String progress;
    private boolean progressVisible;
    private Drawable thumbDrawable;
    private static Paint paint = new Paint();
    private static Paint progressPaint = new Paint(1);
    private static TextPaint docPaint = new TextPaint(1);
    private static TextPaint namePaint = new TextPaint(1);
    private static TextPaint sizePaint = new TextPaint(1);
    private static TextPaint buttonPaint = new TextPaint(1);
    private static TextPaint percentPaint = new TextPaint(1);
    private static TextPaint openPaint = new TextPaint(1);
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private long lastUpdateTime = 0;
    private float currentProgress = 0.0f;
    private float animationProgressStart = 0.0f;
    private long currentProgressTime = 0;
    private float animatedProgressValue = 0.0f;
    private float animatedAlphaValue = 1.0f;

    static {
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(-14209998);
        docPaint.setColor(-1);
        namePaint.setColor(-1);
        sizePaint.setColor(-10327179);
        buttonPaint.setColor(-10327179);
        percentPaint.setColor(-1);
        openPaint.setColor(-1);
        docPaint.setTypeface(AndroidUtilities.bold());
        namePaint.setTypeface(AndroidUtilities.bold());
        buttonPaint.setTypeface(AndroidUtilities.bold());
        percentPaint.setTypeface(AndroidUtilities.bold());
        openPaint.setTypeface(AndroidUtilities.bold());
    }

    public OtherDocumentPlaceholderDrawable(Context context, View view, MessageObject messageObject) {
        docPaint.setTextSize(AndroidUtilities.dp(14.0f));
        namePaint.setTextSize(AndroidUtilities.dp(19.0f));
        sizePaint.setTextSize(AndroidUtilities.dp(15.0f));
        buttonPaint.setTextSize(AndroidUtilities.dp(15.0f));
        percentPaint.setTextSize(AndroidUtilities.dp(15.0f));
        openPaint.setTextSize(AndroidUtilities.dp(15.0f));
        progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.parentView = view;
        this.parentMessageObject = messageObject;
        this.TAG = DownloadController.getInstance(messageObject.currentAccount).generateObserverTag();
        TLRPC.Document document = messageObject.getDocument();
        if (document != null) {
            String documentFileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            this.fileName = documentFileName;
            if (TextUtils.isEmpty(documentFileName)) {
                this.fileName = "name";
            }
            int lastIndexOf = this.fileName.lastIndexOf(46);
            this.ext = lastIndexOf == -1 ? "" : this.fileName.substring(lastIndexOf + 1).toUpperCase();
            if (((int) Math.ceil(docPaint.measureText(r0))) > AndroidUtilities.dp(40.0f)) {
                this.ext = TextUtils.ellipsize(this.ext, docPaint, AndroidUtilities.dp(40.0f), TextUtils.TruncateAt.END).toString();
            }
            this.thumbDrawable = context.getResources().getDrawable(AndroidUtilities.getThumbForNameOrMime(this.fileName, messageObject.getDocument().mime_type, true)).mutate();
            this.fileSize = AndroidUtilities.formatFileSize(document.size);
            if (((int) Math.ceil(namePaint.measureText(this.fileName))) > AndroidUtilities.dp(320.0f)) {
                this.fileName = TextUtils.ellipsize(this.fileName, namePaint, AndroidUtilities.dp(320.0f), TextUtils.TruncateAt.END).toString();
            }
        }
        checkFileExist();
    }

    private void updateAnimation() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        float f = this.animatedProgressValue;
        if (f != 1.0f) {
            float f2 = this.currentProgress;
            if (f != f2) {
                float f3 = this.animationProgressStart;
                float f4 = f2 - f3;
                if (f4 > 0.0f) {
                    long j2 = this.currentProgressTime + j;
                    this.currentProgressTime = j2;
                    if (j2 >= 300) {
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0L;
                    } else {
                        this.animatedProgressValue = f3 + (f4 * decelerateInterpolator.getInterpolation(((float) j2) / 300.0f));
                    }
                }
                this.parentView.invalidate();
            }
        }
        float f5 = this.animatedProgressValue;
        if (f5 < 1.0f || f5 != 1.0f) {
            return;
        }
        float f6 = this.animatedAlphaValue;
        if (f6 != 0.0f) {
            float f7 = f6 - (((float) j) / 200.0f);
            this.animatedAlphaValue = f7;
            if (f7 <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
            }
            this.parentView.invalidate();
        }
    }

    public void checkFileExist() {
        MessageObject messageObject = this.parentMessageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            if (message.media != null) {
                String attachFileName = ((TextUtils.isEmpty(message.attachPath) || !new File(this.parentMessageObject.messageOwner.attachPath).exists()) && !FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(this.parentMessageObject.messageOwner).exists()) ? FileLoader.getAttachFileName(this.parentMessageObject.getDocument()) : null;
                this.loaded = false;
                if (attachFileName == null) {
                    this.progressVisible = false;
                    this.loading = false;
                    this.loaded = true;
                    DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
                    this.parentView.invalidate();
                }
                DownloadController.getInstance(this.parentMessageObject.currentAccount).addLoadingFileObserver(attachFileName, this);
                boolean isLoadingFile = FileLoader.getInstance(this.parentMessageObject.currentAccount).isLoadingFile(attachFileName);
                this.loading = isLoadingFile;
                if (isLoadingFile) {
                    this.progressVisible = true;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress == null) {
                        fileProgress = Float.valueOf(0.0f);
                    }
                    setProgress(fileProgress.floatValue(), false);
                } else {
                    this.progressVisible = false;
                }
                this.parentView.invalidate();
            }
        }
        this.loading = false;
        this.loaded = true;
        this.progressVisible = false;
        setProgress(0.0f, false);
        DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        this.parentView.invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        String upperCase;
        int dp;
        TextPaint textPaint;
        android.graphics.Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.drawRect(0.0f, 0.0f, width, height, paint);
        int dp2 = (height - AndroidUtilities.dp(240.0f)) / 2;
        int dp3 = (width - AndroidUtilities.dp(48.0f)) / 2;
        this.thumbDrawable.setBounds(dp3, dp2, AndroidUtilities.dp(48.0f) + dp3, AndroidUtilities.dp(48.0f) + dp2);
        this.thumbDrawable.draw(canvas);
        canvas.drawText(this.ext, (width - ((int) Math.ceil(docPaint.measureText(this.ext)))) / 2, AndroidUtilities.dp(31.0f) + dp2, docPaint);
        canvas.drawText(this.fileName, (width - ((int) Math.ceil(namePaint.measureText(this.fileName)))) / 2, AndroidUtilities.dp(96.0f) + dp2, namePaint);
        canvas.drawText(this.fileSize, (width - ((int) Math.ceil(sizePaint.measureText(this.fileSize)))) / 2, AndroidUtilities.dp(125.0f) + dp2, sizePaint);
        if (this.loaded) {
            upperCase = LocaleController.getString(R.string.OpenFile);
            textPaint = openPaint;
            dp = 0;
        } else {
            upperCase = this.loading ? LocaleController.getString(R.string.Cancel).toUpperCase() : LocaleController.getString(R.string.TapToDownload);
            dp = AndroidUtilities.dp(28.0f);
            textPaint = buttonPaint;
        }
        canvas.drawText(upperCase, (width - ((int) Math.ceil(textPaint.measureText(upperCase)))) / 2, AndroidUtilities.dp(235.0f) + dp2 + dp, textPaint);
        if (this.progressVisible) {
            if (this.progress != null) {
                canvas.drawText(this.progress, (width - ((int) Math.ceil(percentPaint.measureText(r3)))) / 2, AndroidUtilities.dp(210.0f) + dp2, percentPaint);
            }
            int dp4 = (width - AndroidUtilities.dp(240.0f)) / 2;
            int dp5 = dp2 + AndroidUtilities.dp(232.0f);
            progressPaint.setColor(-10327179);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            float f = dp5;
            canvas.drawRect(((int) (AndroidUtilities.dp(240.0f) * this.animatedProgressValue)) + dp4, f, AndroidUtilities.dp(240.0f) + dp4, AndroidUtilities.dp(2.0f) + dp5, progressPaint);
            progressPaint.setColor(-1);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            float f2 = dp4;
            canvas.drawRect(f2, f, f2 + (AndroidUtilities.dp(240.0f) * this.animatedProgressValue), dp5 + AndroidUtilities.dp(2.0f), progressPaint);
            updateAnimation();
        }
        canvas.restore();
    }

    @Override
    public int getIntrinsicHeight() {
        return this.parentView.getMeasuredHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return this.parentView.getMeasuredWidth();
    }

    @Override
    public int getMinimumHeight() {
        return this.parentView.getMeasuredHeight();
    }

    @Override
    public int getMinimumWidth() {
        return this.parentView.getMeasuredWidth();
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    @Override
    public int getOpacity() {
        return -1;
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        checkFileExist();
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        if (!this.progressVisible) {
            checkFileExist();
        }
        setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSuccessDownload(String str) {
        setProgress(1.0f, true);
        checkFileExist();
    }

    @Override
    public void recycle() {
        DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        this.parentView = null;
        this.parentMessageObject = null;
    }

    @Override
    public void setAlpha(int i) {
        Drawable drawable = this.thumbDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
        paint.setAlpha(i);
        docPaint.setAlpha(i);
        namePaint.setAlpha(i);
        sizePaint.setAlpha(i);
        buttonPaint.setAlpha(i);
        percentPaint.setAlpha(i);
        openPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setProgress(float f, boolean z) {
        if (z) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        }
        this.progress = String.format("%d%%", Integer.valueOf((int) (100.0f * f)));
        if (f != 1.0f) {
            this.animatedAlphaValue = 1.0f;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0L;
        this.lastUpdateTime = System.currentTimeMillis();
        this.parentView.invalidate();
    }
}
