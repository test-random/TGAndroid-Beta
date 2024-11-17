package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;

public abstract class SharedAudioCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private StaticLayout captionLayout;
    AnimatedEmojiSpan.EmojiGroupedSpans captionLayoutEmojis;
    private float captionLayoutLeft;
    private float captionLayoutWidth;
    private TextPaint captionTextPaint;
    private int captionY;
    private CheckBox2 checkBox;
    private boolean checkForButtonPress;
    private int currentAccount;
    private MessageObject currentMessageObject;
    private StaticLayout dateLayout;
    private int dateLayoutX;
    private TextPaint description2TextPaint;
    private StaticLayout descriptionLayout;
    AnimatedEmojiSpan.EmojiGroupedSpans descriptionLayoutEmojis;
    private float descriptionLayoutLeft;
    private float descriptionLayoutWidth;
    private int descriptionY;
    private SpannableStringBuilder dotSpan;
    float enterAlpha;
    FlickerLoadingView globalGradientView;
    private int hasMiniProgress;
    private boolean miniButtonPressed;
    private int miniButtonState;
    private boolean needDivider;
    private RadialProgress2 radialProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    boolean showName;
    float showNameProgress;
    boolean showReorderIcon;
    float showReorderIconProgress;
    private StaticLayout titleLayout;
    AnimatedEmojiSpan.EmojiGroupedSpans titleLayoutEmojis;
    private float titleLayoutLeft;
    private float titleLayoutWidth;
    TextPaint titlePaint;
    private int titleY;
    private int viewType;

    public SharedAudioCell(Context context) {
        this(context, 0, null);
    }

    public SharedAudioCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.titleY = AndroidUtilities.dp(9.0f);
        this.descriptionY = AndroidUtilities.dp(29.0f);
        this.captionY = AndroidUtilities.dp(29.0f);
        this.currentAccount = UserConfig.selectedAccount;
        this.showName = true;
        this.showNameProgress = 0.0f;
        this.enterAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider;
        this.viewType = i;
        setFocusable(true);
        setImportantForAccessibility(1);
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider);
        this.radialProgress = radialProgress2;
        radialProgress2.setColorKeys(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setWillNotDraw(false);
        CheckBox2 checkBox2 = new CheckBox2(context, 22, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(3);
        CheckBox2 checkBox22 = this.checkBox;
        boolean z = LocaleController.isRTL;
        addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z ? 5 : 3) | 48, z ? 0.0f : 38.1f, 32.1f, z ? 6.0f : 0.0f, 0.0f));
        if (i == 1) {
            TextPaint textPaint = new TextPaint(1);
            this.description2TextPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new DotDividerSpan(), 0, 1, 0);
        }
        TextPaint textPaint2 = new TextPaint(1);
        this.captionTextPaint = textPaint2;
        textPaint2.setTextSize(AndroidUtilities.dp(13.0f));
        if (resourcesProvider != null) {
            TextPaint textPaint3 = new TextPaint(1);
            this.titlePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.bold());
            this.titlePaint.setTextSize(AndroidUtilities.dp(15.0f));
            this.titlePaint.setColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        }
    }

    public SharedAudioCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 0, resourcesProvider);
    }

    private boolean checkAudioMotionEvent(MotionEvent motionEvent) {
        boolean z;
        int dp;
        int dp2;
        int i;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int dp3 = AndroidUtilities.dp(36.0f);
        boolean z2 = this.miniButtonState >= 0 && x >= (dp2 = this.buttonX + (dp = AndroidUtilities.dp(27.0f))) && x <= dp2 + dp3 && y >= (i = this.buttonY + dp) && y <= i + dp3;
        if (motionEvent.getAction() == 0) {
            if (z2) {
                this.miniButtonPressed = true;
                this.radialProgress.setPressed(true, true);
            } else if (this.checkForButtonPress && this.radialProgress.getProgressRect().contains(x, y)) {
                requestDisallowInterceptTouchEvent(true);
                this.buttonPressed = true;
                this.radialProgress.setPressed(true, false);
            }
            invalidate();
            z = true;
            this.radialProgress.setPressed(this.miniButtonPressed, true);
            return z || this.buttonPressed;
        }
        if (motionEvent.getAction() == 1) {
            if (this.miniButtonPressed) {
                this.miniButtonPressed = false;
                playSoundEffect(0);
                didPressedMiniButton(true);
            } else {
                if (this.buttonPressed) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                }
                requestDisallowInterceptTouchEvent(false);
            }
            invalidate();
            requestDisallowInterceptTouchEvent(false);
        } else {
            if (motionEvent.getAction() == 3) {
                requestDisallowInterceptTouchEvent(false);
                this.miniButtonPressed = false;
                this.buttonPressed = false;
            } else if (motionEvent.getAction() == 2 && !z2 && this.miniButtonPressed) {
                this.miniButtonPressed = false;
            }
            invalidate();
        }
        z = false;
        this.radialProgress.setPressed(this.miniButtonPressed, true);
        if (z) {
            return true;
        }
    }

    private void didPressedMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
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

    private void drawInternal(Canvas canvas) {
        float height;
        float width;
        float height2;
        Paint themePaint;
        Canvas canvas2;
        float f;
        StaticLayout staticLayout;
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        }
        int i = 0;
        if (this.dateLayout != null) {
            canvas.save();
            canvas.translate(AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX), this.titleY);
            this.dateLayout.draw(canvas);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            int alpha = Theme.chat_contextResult_titleTextPaint.getAlpha();
            float f2 = this.showNameProgress;
            if (f2 != 1.0f) {
                Theme.chat_contextResult_titleTextPaint.setAlpha((int) (alpha * f2));
            }
            canvas.save();
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL && (staticLayout = this.dateLayout) != null) {
                i = staticLayout.getWidth() + AndroidUtilities.dp(LocaleController.isRTL ? 12.0f : 4.0f);
            }
            canvas.translate(((dp + i) + (LocaleController.isRTL ? this.titleLayout.getWidth() - this.titleLayoutWidth : 0.0f)) - this.titleLayoutLeft, this.titleY);
            this.titleLayout.draw(canvas);
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.titleLayout, this.titleLayoutEmojis, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
            canvas.restore();
            if (this.showNameProgress != 1.0f) {
                Theme.chat_contextResult_titleTextPaint.setAlpha(alpha);
            }
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.save();
            canvas.translate((AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? this.captionLayout.getWidth() - this.captionLayoutWidth : 0.0f)) - this.captionLayoutLeft, this.captionY);
            this.captionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
            int alpha2 = Theme.chat_contextResult_descriptionTextPaint.getAlpha();
            float f3 = this.showNameProgress;
            if (f3 != 1.0f) {
                Theme.chat_contextResult_descriptionTextPaint.setAlpha((int) (alpha2 * f3));
            }
            canvas.save();
            canvas.translate((AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? this.descriptionLayout.getWidth() - this.descriptionLayoutWidth : 0.0f)) - this.descriptionLayoutLeft, this.descriptionY);
            this.descriptionLayout.draw(canvas);
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.descriptionLayout, this.descriptionLayoutEmojis, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
            canvas.restore();
            if (this.showNameProgress != 1.0f) {
                Theme.chat_contextResult_descriptionTextPaint.setAlpha(alpha2);
            }
        }
        this.radialProgress.setProgressColor(getThemedColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
        this.radialProgress.setOverlayImageAlpha(this.showNameProgress);
        this.radialProgress.draw(canvas);
        if (this.needDivider) {
            if (LocaleController.isRTL) {
                height = getHeight() - 1;
                width = (getWidth() - AndroidUtilities.dp(72.0f)) - getPaddingRight();
                height2 = getHeight() - 1;
                themePaint = Theme.getThemePaint("paintDivider", this.resourcesProvider);
                f = 0.0f;
                canvas2 = canvas;
            } else {
                float dp2 = AndroidUtilities.dp(72.0f);
                height = getHeight() - 1;
                width = getWidth() - getPaddingRight();
                height2 = getHeight() - 1;
                themePaint = Theme.getThemePaint("paintDivider", this.resourcesProvider);
                canvas2 = canvas;
                f = dp2;
            }
            canvas2.drawLine(f, height, width, height2, themePaint);
        }
    }

    private void drawReorder(Canvas canvas) {
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
                this.currentMessageObject.putInDownloadsStore = true;
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            }
            if (!needPlayMessage(this.currentMessageObject)) {
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
            this.currentMessageObject.putInDownloadsStore = true;
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

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        updateButtonState(false, true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        boolean z = this.showName;
        if (z) {
            float f = this.showNameProgress;
            if (f != 1.0f) {
                this.showNameProgress = f + 0.10666667f;
                invalidate();
                this.showNameProgress = Utilities.clamp(this.showNameProgress, 1.0f, 0.0f);
                if (this.enterAlpha != 1.0f || this.globalGradientView == null) {
                    drawInternal(canvas);
                    drawReorder(canvas);
                    super.dispatchDraw(canvas);
                }
                canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
                this.globalGradientView.setViewType(4);
                this.globalGradientView.updateColors();
                this.globalGradientView.updateGradient();
                this.globalGradientView.draw(canvas);
                canvas.restore();
                canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) (this.enterAlpha * 255.0f), 31);
                drawInternal(canvas);
                super.dispatchDraw(canvas);
                drawReorder(canvas);
                canvas.restore();
                return;
            }
        }
        if (!z) {
            float f2 = this.showNameProgress;
            if (f2 != 0.0f) {
                this.showNameProgress = f2 - 0.10666667f;
                invalidate();
            }
        }
        this.showNameProgress = Utilities.clamp(this.showNameProgress, 1.0f, 0.0f);
        if (this.enterAlpha != 1.0f) {
        }
        drawInternal(canvas);
        drawReorder(canvas);
        super.dispatchDraw(canvas);
    }

    public MessageObject getMessage() {
        return this.currentMessageObject;
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    public void initStreamingIcons() {
        this.radialProgress.initMiniIcons();
    }

    protected boolean needPlayMessage(MessageObject messageObject) {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.radialProgress.onAttachedToWindow();
        updateButtonState(false, false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        this.titleLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.titleLayoutEmojis, this.titleLayout);
        this.descriptionLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.descriptionLayoutEmojis, this.descriptionLayout);
        this.captionLayoutEmojis = AnimatedEmojiSpan.update(0, this, this.captionLayoutEmojis, this.captionLayout);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.radialProgress.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        AnimatedEmojiSpan.release(this, this.titleLayoutEmojis);
        AnimatedEmojiSpan.release(this, this.descriptionLayoutEmojis);
        AnimatedEmojiSpan.release(this, this.captionLayoutEmojis);
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedAudioCell.onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo):void");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int r22, int r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedAudioCell.onMeasure(int, int):void");
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
        this.radialProgress.setPressed(false, false);
        this.radialProgress.setPressed(this.miniButtonPressed, true);
        return false;
    }

    public void setCheckForButtonPress(boolean z) {
        this.checkForButtonPress = z;
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
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

    public void setMessageObject(MessageObject messageObject, boolean z) {
        this.needDivider = z;
        this.currentMessageObject = messageObject;
        TLRPC.Document document = messageObject.getDocument();
        TLRPC.PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 360) : null;
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
        updateButtonState(false, false);
        requestLayout();
    }

    public void showName(boolean z, boolean z2) {
        if (!z2) {
            this.showNameProgress = z ? 1.0f : 0.0f;
        }
        if (this.showName == z) {
            return;
        }
        this.showName = z;
        invalidate();
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

    public void updateButtonState(boolean z, boolean z2) {
        String fileName = this.currentMessageObject.getFileName();
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        boolean z3 = messageObject.attachPathExists || messageObject.mediaExists;
        if (SharedConfig.streamMedia && messageObject.isMusic() && ((int) this.currentMessageObject.getDialogId()) != 0) {
            this.hasMiniProgress = z3 ? 1 : 2;
            z3 = true;
        } else {
            this.hasMiniProgress = 0;
            this.miniButtonState = -1;
        }
        if (this.hasMiniProgress == 0) {
            if (z3) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, z2);
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 4;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                    }
                } else {
                    this.buttonState = 2;
                }
                this.radialProgress.setProgress(0.0f, z2);
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
