package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadialProgress2;

public class PatternCell extends BackupImageView implements DownloadController.FileDownloadProgressListener {
    private final int SIZE;
    private int TAG;
    private MotionBackgroundDrawable backgroundDrawable;
    private Paint backgroundPaint;
    private int currentAccount;
    private int currentBackgroundColor;
    private int currentGradientAngle;
    private int currentGradientColor1;
    private int currentGradientColor2;
    private int currentGradientColor3;
    private TLRPC.TL_wallPaper currentPattern;
    private PatternCellDelegate delegate;
    private LinearGradient gradientShader;
    private int maxWallpaperSize;
    private RadialProgress2 radialProgress;
    private RectF rect;

    public interface PatternCellDelegate {
        int getBackgroundColor();

        int getBackgroundGradientAngle();

        int getBackgroundGradientColor1();

        int getBackgroundGradientColor2();

        int getBackgroundGradientColor3();

        int getCheckColor();

        float getIntensity();

        int getPatternColor();

        TLRPC.TL_wallPaper getSelectedPattern();
    }

    public PatternCell(Context context, int i, PatternCellDelegate patternCellDelegate) {
        super(context);
        this.SIZE = 100;
        this.rect = new RectF();
        this.currentAccount = UserConfig.selectedAccount;
        setRoundRadius(AndroidUtilities.dp(6.0f));
        this.maxWallpaperSize = i;
        this.delegate = patternCellDelegate;
        RadialProgress2 radialProgress2 = new RadialProgress2(this);
        this.radialProgress = radialProgress2;
        radialProgress2.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
        this.backgroundPaint = new Paint(3);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), view.getMeasuredWidth() - AndroidUtilities.dp(1.0f), view.getMeasuredHeight() - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(6.0f));
                }
            });
            setClipToOutline(true);
        }
    }

    private void updateButtonState(Object obj, boolean z, boolean z2) {
        File httpFilePath;
        String name;
        boolean z3 = obj instanceof TLRPC.TL_wallPaper;
        if (z3 || (obj instanceof MediaController.SearchImage)) {
            if (z3) {
                TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
                name = FileLoader.getAttachFileName(tL_wallPaper.document);
                if (TextUtils.isEmpty(name)) {
                    return;
                } else {
                    httpFilePath = FileLoader.getInstance(this.currentAccount).getPathToAttach(tL_wallPaper.document, true);
                }
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC.Photo photo = searchImage.photo;
                if (photo != null) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, this.maxWallpaperSize, true);
                    File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true);
                    name = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    httpFilePath = pathToAttach;
                } else {
                    httpFilePath = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                    name = httpFilePath.getName();
                }
                if (TextUtils.isEmpty(name)) {
                    return;
                }
            }
            if (!httpFilePath.exists()) {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(name, null, this);
                FileLoader.getInstance(this.currentAccount).isLoadingFile(name);
                Float fileProgress = ImageLoader.getInstance().getFileProgress(name);
                if (fileProgress != null) {
                    this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                } else {
                    this.radialProgress.setProgress(0.0f, z2);
                }
                this.radialProgress.setIcon(10, z, z2);
                return;
            }
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.radialProgress.setProgress(1.0f, z2);
        }
        this.radialProgress.setIcon(6, z, z2);
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateSelected(false);
    }

    @Override
    public void onDraw(android.graphics.Canvas r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PatternCell.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if (!(tL_wallPaper == null && selectedPattern == null) && (selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) {
            return;
        }
        if (z) {
            this.radialProgress.setIcon(4, false, true);
        } else {
            updateButtonState(tL_wallPaper, true, z);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if ((!(tL_wallPaper == null && selectedPattern == null) && (selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) || this.radialProgress.getIcon() == 10) {
            return;
        }
        updateButtonState(this.currentPattern, false, true);
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if (!(tL_wallPaper == null && selectedPattern == null) && (selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) {
            return;
        }
        updateButtonState(tL_wallPaper, false, true);
    }

    public void setPattern(TLRPC.TL_wallPaper tL_wallPaper) {
        this.currentPattern = tL_wallPaper;
        if (tL_wallPaper != null) {
            setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, AndroidUtilities.dp(100.0f)), tL_wallPaper.document), "100_100", null, null, "png", 0L, 1, tL_wallPaper);
        } else {
            setImageDrawable(null);
        }
        updateSelected(false);
    }

    public void updateSelected(boolean z) {
        TLRPC.TL_wallPaper selectedPattern = this.delegate.getSelectedPattern();
        TLRPC.TL_wallPaper tL_wallPaper = this.currentPattern;
        if (!(tL_wallPaper == null && selectedPattern == null) && (selectedPattern == null || tL_wallPaper == null || tL_wallPaper.id != selectedPattern.id)) {
            this.radialProgress.setIcon(4, false, z);
        } else {
            updateButtonState(selectedPattern, false, z);
        }
        invalidate();
    }
}
