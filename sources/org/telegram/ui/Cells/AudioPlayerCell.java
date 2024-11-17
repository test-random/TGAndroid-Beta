package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.FilteredSearchView;

public class AudioPlayerCell extends View implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private int currentAccount;
    private MessageObject currentMessageObject;
    private StaticLayout descriptionLayout;
    private AnimatedEmojiSpan.EmojiGroupedSpans descriptionLayoutEmojis;
    private int descriptionY;
    private SpannableStringBuilder dotSpan;
    private int hasMiniProgress;
    private boolean miniButtonPressed;
    private int miniButtonState;
    private RadialProgress2 radialProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private StaticLayout titleLayout;
    private AnimatedEmojiSpan.EmojiGroupedSpans titleLayoutEmojis;
    private int titleY;
    private int viewType;

    public AudioPlayerCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.titleY = AndroidUtilities.dp(9.0f);
        this.descriptionY = AndroidUtilities.dp(29.0f);
        this.currentAccount = UserConfig.selectedAccount;
        this.resourcesProvider = resourcesProvider;
        this.viewType = i;
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider);
        this.radialProgress = radialProgress2;
        radialProgress2.setColorKeys(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
        if (i == 1) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new DotDividerSpan(), 0, 1, 0);
        }
    }

    private boolean checkAudioMotionEvent(MotionEvent motionEvent) {
        int dp;
        int dp2;
        int i;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int dp3 = AndroidUtilities.dp(36.0f);
        boolean z = this.miniButtonState >= 0 && x >= (dp2 = this.buttonX + (dp = AndroidUtilities.dp(27.0f))) && x <= dp2 + dp3 && y >= (i = this.buttonY + dp) && y <= i + dp3;
        if (motionEvent.getAction() == 0) {
            if (z) {
                this.miniButtonPressed = true;
                this.radialProgress.setPressed(true, true);
                invalidate();
                return true;
            }
        } else if (this.miniButtonPressed) {
            if (motionEvent.getAction() == 1) {
                this.miniButtonPressed = false;
                playSoundEffect(0);
                didPressedMiniButton(true);
            } else {
                if (motionEvent.getAction() == 3 || (motionEvent.getAction() == 2 && !z)) {
                    this.miniButtonPressed = false;
                }
                this.radialProgress.setPressed(this.miniButtonPressed, true);
            }
            invalidate();
            this.radialProgress.setPressed(this.miniButtonPressed, true);
        }
        return false;
    }

    private void didPressedMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 3, 0);
        } else {
            if (i != 1) {
                return;
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        }
        this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
        invalidate();
    }

    private int getIconForCurrentState() {
        int i = this.buttonState;
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        return i == 4 ? 3 : 0;
    }

    private int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void didPressedButton() {
        int i = this.buttonState;
        int i2 = 2;
        if (i == 0) {
            if (this.miniButtonState == 0) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            }
            if (!MediaController.getInstance().findMessageInPlaylistAndPlay(this.currentMessageObject)) {
                return;
            }
            if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                this.miniButtonState = 1;
                this.radialProgress.setProgress(0.0f, false);
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            }
            i2 = 1;
        } else if (i == 1) {
            if (!MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                return;
            } else {
                i2 = 0;
            }
        } else if (i == 2) {
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            i2 = 4;
        } else if (i != 4) {
            return;
        } else {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        }
        this.buttonState = i2;
        this.radialProgress.setIcon(getIconForCurrentState(), false, true);
        invalidate();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.radialProgress.onAttachedToWindow();
        this.titleLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.titleLayoutEmojis, this.titleLayout);
        this.descriptionLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.descriptionLayoutEmojis, this.descriptionLayout);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        AnimatedEmojiSpan.release(this, this.titleLayoutEmojis);
        AnimatedEmojiSpan.release(this, this.descriptionLayoutEmojis);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.titleY);
            this.titleLayout.draw(canvas);
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.titleLayout, this.titleLayoutEmojis, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : AndroidUtilities.leftBaseline), this.descriptionY);
            this.descriptionLayout.draw(canvas);
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.descriptionLayout, this.descriptionLayoutEmojis, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
            canvas.restore();
        }
        this.radialProgress.setProgressColor(getThemedColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
        this.radialProgress.draw(canvas);
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        String str;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.currentMessageObject.isMusic()) {
            str = LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, this.currentMessageObject.getMusicAuthor(), this.currentMessageObject.getMusicTitle());
        } else {
            str = ((Object) this.titleLayout.getText()) + ", " + ((Object) this.descriptionLayout.getText());
        }
        accessibilityNodeInfo.setText(str);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        this.descriptionLayout = null;
        this.titleLayout = null;
        int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(28.0f);
        try {
            StaticLayout staticLayout = new StaticLayout(TextUtils.ellipsize(this.currentMessageObject.getMusicTitle().replace('\n', ' '), Theme.chat_contextResult_titleTextPaint, Math.min((int) Math.ceil(Theme.chat_contextResult_titleTextPaint.measureText(r0)), size), TextUtils.TruncateAt.END), Theme.chat_contextResult_titleTextPaint, size + AndroidUtilities.dp(4.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.titleLayout = staticLayout;
            this.titleLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.titleLayoutEmojis, staticLayout);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            CharSequence replace = this.currentMessageObject.getMusicAuthor().replace('\n', ' ');
            if (this.viewType == 1) {
                replace = new SpannableStringBuilder(replace).append(' ').append((CharSequence) this.dotSpan).append(' ').append(FilteredSearchView.createFromInfoString(this.currentMessageObject, 2));
            }
            StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(replace, Theme.chat_contextResult_descriptionTextPaint, size, TextUtils.TruncateAt.END), Theme.chat_contextResult_descriptionTextPaint, size + AndroidUtilities.dp(4.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.descriptionLayout = staticLayout2;
            this.descriptionLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.descriptionLayoutEmojis, staticLayout2);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(56.0f));
        int size2 = LocaleController.isRTL ? (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - AndroidUtilities.dp(52.0f) : AndroidUtilities.dp(8.0f);
        RadialProgress2 radialProgress2 = this.radialProgress;
        int dp = AndroidUtilities.dp(4.0f) + size2;
        this.buttonX = dp;
        int dp2 = AndroidUtilities.dp(6.0f);
        this.buttonY = dp2;
        radialProgress2.setProgressRect(dp, dp2, size2 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(50.0f));
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        if (this.hasMiniProgress != 0) {
            if (this.miniButtonState == 1) {
                return;
            }
        } else if (this.buttonState == 4) {
            return;
        }
        updateButtonState(false, true);
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(motionEvent);
        }
        boolean checkAudioMotionEvent = checkAudioMotionEvent(motionEvent);
        if (motionEvent.getAction() != 3) {
            return checkAudioMotionEvent;
        }
        this.miniButtonPressed = false;
        this.buttonPressed = false;
        return false;
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        TLRPC.Document document = messageObject.getDocument();
        TLRPC.PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90) : null;
        if ((closestPhotoSizeWithSize instanceof TLRPC.TL_photoSize) || (closestPhotoSizeWithSize instanceof TLRPC.TL_photoSizeProgressive)) {
            this.radialProgress.setImageOverlay(closestPhotoSizeWithSize, document, messageObject);
        } else {
            String artworkUrl = messageObject.getArtworkUrl(true);
            if (TextUtils.isEmpty(artworkUrl)) {
                this.radialProgress.setImageOverlay(null, null, null);
            } else {
                this.radialProgress.setImageOverlay(artworkUrl);
            }
        }
        requestLayout();
        updateButtonState(false, false);
    }

    public void updateButtonState(boolean z, boolean z2) {
        String fileName = this.currentMessageObject.getFileName();
        File file = null;
        if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
            File file2 = new File(this.currentMessageObject.messageOwner.attachPath);
            if (file2.exists()) {
                file = file2;
            }
        }
        if (file == null) {
            file = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.currentMessageObject.getDocument());
        }
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        if (file.exists() && file.length() == 0) {
            file.delete();
        }
        boolean exists = file.exists();
        if (!SharedConfig.streamMedia || ((int) this.currentMessageObject.getDialogId()) == 0) {
            this.miniButtonState = -1;
        } else {
            this.hasMiniProgress = exists ? 1 : 2;
            exists = true;
        }
        if (this.hasMiniProgress == 0) {
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, z2);
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 4;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                } else {
                    this.buttonState = 2;
                }
            }
            this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
            invalidate();
            return;
        }
        this.radialProgress.setMiniProgressBackgroundColor(getThemedColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLoader : Theme.key_chat_inLoader));
        boolean isPlayingMessage2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
        if (!isPlayingMessage2 || (isPlayingMessage2 && MediaController.getInstance().isMessagePaused())) {
            this.buttonState = 0;
        } else {
            this.buttonState = 1;
        }
        this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
        if (this.hasMiniProgress == 1) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.miniButtonState = -1;
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
            if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                this.miniButtonState = 1;
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                Float fileProgress2 = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress2 != null) {
                    this.radialProgress.setProgress(fileProgress2.floatValue(), z2);
                    return;
                } else {
                    this.radialProgress.setProgress(0.0f, z2);
                    return;
                }
            }
            this.miniButtonState = 0;
        }
        this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
    }
}
