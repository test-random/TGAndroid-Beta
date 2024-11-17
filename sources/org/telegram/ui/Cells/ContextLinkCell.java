package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress2;

public class ContextLinkCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    public final Property IMAGE_SCALE;
    private int TAG;
    private AnimatorSet animator;
    private Paint backgroundPaint;
    private ButtonBounce buttonBounce;
    private boolean buttonPressed;
    private int buttonState;
    File cacheFile;
    private boolean canPreviewGif;
    private CheckBox2 checkBox;
    private int currentAccount;
    private int currentDate;
    private MessageObject currentMessageObject;
    private TLRPC.PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY;
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    boolean fileExist;
    String fileName;
    private float imageScale;
    private TLRPC.User inlineBot;
    private TLRPC.BotInlineResult inlineResult;
    private boolean isForceGif;
    private boolean isKeyboard;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private TLRPC.Photo photoAttach;
    private RadialProgress2 radialProgress;
    int resolveFileNameId;
    boolean resolvingFileName;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY;

    public class AnonymousClass1 implements Runnable {
        final boolean val$ifSame;
        final int val$localId;

        AnonymousClass1(int i, boolean z) {
            this.val$localId = i;
            this.val$ifSame = z;
        }

        public void lambda$run$0(int i, String str, File file, boolean z, boolean z2) {
            ContextLinkCell contextLinkCell = ContextLinkCell.this;
            contextLinkCell.resolvingFileName = false;
            if (contextLinkCell.resolveFileNameId == i) {
                contextLinkCell.fileName = str;
                if (str == null) {
                    contextLinkCell.fileName = "";
                }
                contextLinkCell.cacheFile = file;
                contextLinkCell.fileExist = z;
            }
            contextLinkCell.updateButtonState(z2, true);
        }

        @Override
        public void run() {
            File file;
            final File file2;
            final String str;
            String attachFileName;
            FileLoader fileLoader;
            TLObject tLObject;
            File file3;
            File file4;
            String str2 = null;
            if (ContextLinkCell.this.documentAttachType == 5 || ContextLinkCell.this.documentAttachType == 3) {
                if (ContextLinkCell.this.documentAttach != null) {
                    str2 = FileLoader.getAttachFileName(ContextLinkCell.this.documentAttach);
                    file = FileLoader.getInstance(ContextLinkCell.this.currentAccount).getPathToAttach(ContextLinkCell.this.documentAttach);
                } else {
                    if (ContextLinkCell.this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(Utilities.MD5(ContextLinkCell.this.inlineResult.content.url));
                        sb.append(".");
                        sb.append(ImageLoader.getHttpUrlExtension(ContextLinkCell.this.inlineResult.content.url, ContextLinkCell.this.documentAttachType == 5 ? "mp3" : "ogg"));
                        str2 = sb.toString();
                        file = new File(FileLoader.getDirectory(4), str2);
                    }
                    str = null;
                    file2 = null;
                }
                file2 = file;
                str = str2;
            } else {
                if (ContextLinkCell.this.mediaWebpage) {
                    if (ContextLinkCell.this.inlineResult != null) {
                        if (ContextLinkCell.this.inlineResult.document instanceof TLRPC.TL_document) {
                            attachFileName = FileLoader.getAttachFileName(ContextLinkCell.this.inlineResult.document);
                            fileLoader = FileLoader.getInstance(ContextLinkCell.this.currentAccount);
                            tLObject = ContextLinkCell.this.inlineResult.document;
                        } else if (ContextLinkCell.this.inlineResult.photo instanceof TLRPC.TL_photo) {
                            ContextLinkCell contextLinkCell = ContextLinkCell.this;
                            contextLinkCell.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(contextLinkCell.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                            attachFileName = FileLoader.getAttachFileName(ContextLinkCell.this.currentPhotoObject);
                            fileLoader = FileLoader.getInstance(ContextLinkCell.this.currentAccount);
                            tLObject = ContextLinkCell.this.currentPhotoObject;
                        } else {
                            if (ContextLinkCell.this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                                attachFileName = Utilities.MD5(ContextLinkCell.this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(ContextLinkCell.this.inlineResult.content.url, FileLoader.getMimeTypePart(ContextLinkCell.this.inlineResult.content.mime_type));
                                file4 = new File(FileLoader.getDirectory(4), attachFileName);
                                if (ContextLinkCell.this.documentAttachType == 2 && (ContextLinkCell.this.inlineResult.thumb instanceof TLRPC.TL_webDocument) && "video/mp4".equals(ContextLinkCell.this.inlineResult.thumb.mime_type)) {
                                    file3 = file4;
                                    attachFileName = null;
                                }
                            } else {
                                if (ContextLinkCell.this.inlineResult.thumb instanceof TLRPC.TL_webDocument) {
                                    attachFileName = Utilities.MD5(ContextLinkCell.this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(ContextLinkCell.this.inlineResult.thumb.url, FileLoader.getMimeTypePart(ContextLinkCell.this.inlineResult.thumb.mime_type));
                                    file4 = new File(FileLoader.getDirectory(4), attachFileName);
                                }
                                attachFileName = null;
                                file3 = null;
                            }
                            file3 = file4;
                        }
                        file3 = fileLoader.getPathToAttach(tLObject);
                    } else {
                        if (ContextLinkCell.this.documentAttach != null) {
                            attachFileName = FileLoader.getAttachFileName(ContextLinkCell.this.documentAttach);
                            fileLoader = FileLoader.getInstance(ContextLinkCell.this.currentAccount);
                            tLObject = ContextLinkCell.this.documentAttach;
                            file3 = fileLoader.getPathToAttach(tLObject);
                        }
                        attachFileName = null;
                        file3 = null;
                    }
                    if (ContextLinkCell.this.documentAttach == null || ContextLinkCell.this.documentAttachType != 2 || MessageObject.getDocumentVideoThumb(ContextLinkCell.this.documentAttach) == null) {
                        str = attachFileName;
                        file2 = file3;
                    } else {
                        file2 = file3;
                        str = str2;
                    }
                }
                str = null;
                file2 = null;
            }
            final boolean z = !TextUtils.isEmpty(str) && file2.exists();
            final int i = this.val$localId;
            final boolean z2 = this.val$ifSame;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ContextLinkCell.AnonymousClass1.this.lambda$run$0(i, str, file2, z, z2);
                }
            });
        }
    }

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        this(context, false, null);
    }

    public ContextLinkCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.titleY = AndroidUtilities.dp(7.0f);
        this.descriptionY = AndroidUtilities.dp(27.0f);
        this.cacheFile = null;
        this.imageScale = 1.0f;
        this.IMAGE_SCALE = new AnimationProperties.FloatProperty("animationValue") {
            @Override
            public Float get(ContextLinkCell contextLinkCell) {
                return Float.valueOf(ContextLinkCell.this.imageScale);
            }

            @Override
            public void setValue(ContextLinkCell contextLinkCell, float f) {
                ContextLinkCell.this.imageScale = f;
                ContextLinkCell.this.invalidate();
            }
        };
        this.resourcesProvider = resourcesProvider;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
        this.linkImageView.setLayerNum(1);
        this.linkImageView.setUseSharedAnimationQueue(true);
        this.letterDrawable = new LetterDrawable(resourcesProvider, 0);
        this.radialProgress = new RadialProgress2(this);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
        if (z) {
            Paint paint = new Paint();
            this.backgroundPaint = paint;
            int i = Theme.key_sharedMedia_photoPlaceholder;
            paint.setColor(Theme.getColor(i, resourcesProvider));
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(-1, i, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(1);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 1.0f, 1.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    private void didPressedButton() {
        int i;
        int i2 = this.documentAttachType;
        if (i2 == 3 || i2 == 5) {
            int i3 = this.buttonState;
            if (i3 == 0) {
                if (!MediaController.getInstance().playMessage(this.currentMessageObject)) {
                    return;
                } else {
                    i = 1;
                }
            } else if (i3 != 1) {
                i = 4;
                if (i3 == 2) {
                    this.radialProgress.setProgress(0.0f, false);
                    if (this.documentAttach != null) {
                        FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
                    } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                        FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 3, 1);
                    }
                } else {
                    if (i3 != 4) {
                        return;
                    }
                    if (this.documentAttach != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                    } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
                    }
                    i = 2;
                }
            } else if (!MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                return;
            } else {
                i = 0;
            }
            this.buttonState = i;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        }
    }

    private int getIconForCurrentState() {
        int i = this.documentAttachType;
        if (i != 3 && i != 5) {
            this.radialProgress.setColorKeys(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
            return this.buttonState == 1 ? 10 : 4;
        }
        this.radialProgress.setColorKeys(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
        int i2 = this.buttonState;
        if (i2 == 1) {
            return 1;
        }
        if (i2 == 2) {
            return 2;
        }
        return i2 == 4 ? 3 : 0;
    }

    private void setAttachType() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.setAttachType():void");
    }

    public void allowButtonBounce(boolean z) {
        if (z != (this.buttonBounce != null)) {
            this.buttonBounce = z ? new ButtonBounce(this, 1.0f, 3.0f).setReleaseDelay(120L) : null;
        }
    }

    public TLRPC.BotInlineResult getBotInlineResult() {
        return this.inlineResult;
    }

    public int getDate() {
        return this.currentDate;
    }

    public TLRPC.Document getDocument() {
        return this.documentAttach;
    }

    public TLRPC.User getInlineBot() {
        return this.inlineBot;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public TLRPC.BotInlineResult getResult() {
        return this.inlineResult;
    }

    public boolean isGif() {
        return this.documentAttachType == 2 && this.canPreviewGif;
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
        this.radialProgress.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.linkImageView.onDetachedFromWindow();
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override
    protected void onDraw(android.graphics.Canvas r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        int i;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder sb = new StringBuilder();
        switch (this.documentAttachType) {
            case 1:
                i = R.string.AttachDocument;
                break;
            case 2:
                i = R.string.AttachGif;
                break;
            case 3:
                i = R.string.AttachAudio;
                break;
            case 4:
                i = R.string.AttachVideo;
                break;
            case 5:
                i = R.string.AttachMusic;
                break;
            case 6:
                i = R.string.AttachSticker;
                break;
            case 7:
                i = R.string.AttachPhoto;
                break;
            case 8:
                i = R.string.AttachLocation;
                break;
        }
        sb.append(LocaleController.getString(i));
        StaticLayout staticLayout = this.titleLayout;
        boolean z = (staticLayout == null || TextUtils.isEmpty(staticLayout.getText())) ? false : true;
        StaticLayout staticLayout2 = this.descriptionLayout;
        boolean z2 = (staticLayout2 == null || TextUtils.isEmpty(staticLayout2.getText())) ? false : true;
        if (this.documentAttachType == 5 && z && z2) {
            sb.append(", ");
            sb.append(LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, this.descriptionLayout.getText(), this.titleLayout.getText()));
        } else {
            if (z) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(this.titleLayout.getText());
            }
            if (z2) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(this.descriptionLayout.getText());
            }
        }
        accessibilityNodeInfo.setText(sb);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null || !checkBox2.isChecked()) {
            return;
        }
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(true);
    }

    @Override
    protected void onMeasure(int r44, int r45) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.buttonState == 4) {
                return;
            }
        } else if (this.buttonState == 1) {
            return;
        }
        updateButtonState(false, true);
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSuccessDownload(String str) {
        this.fileExist = true;
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        TLRPC.WebDocument webDocument;
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(motionEvent);
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        AndroidUtilities.dp(48.0f);
        int i = this.documentAttachType;
        boolean z = true;
        if (i == 3 || i == 5) {
            boolean contains = this.letterDrawable.getBounds().contains(x, y);
            if (motionEvent.getAction() == 0) {
                if (contains) {
                    this.buttonPressed = true;
                    this.radialProgress.setPressed(true, false);
                    invalidate();
                }
            } else if (this.buttonPressed) {
                if (motionEvent.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                } else {
                    if (motionEvent.getAction() == 3 || (motionEvent.getAction() == 2 && !contains)) {
                        this.buttonPressed = false;
                    }
                    this.radialProgress.setPressed(this.buttonPressed, false);
                }
                invalidate();
                this.radialProgress.setPressed(this.buttonPressed, false);
            }
            z = false;
        } else {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult != null && (webDocument = botInlineResult.content) != null && !TextUtils.isEmpty(webDocument.url)) {
                if (motionEvent.getAction() == 0) {
                    if (this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = true;
                    }
                } else if (this.buttonPressed) {
                    if (motionEvent.getAction() == 1) {
                        this.buttonPressed = false;
                        playSoundEffect(0);
                        this.delegate.didPressedImage(this);
                    } else if (motionEvent.getAction() == 3 || (motionEvent.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y))) {
                        this.buttonPressed = false;
                    }
                }
            }
            z = false;
        }
        return !z ? super.onTouchEvent(motionEvent) : z;
    }

    public void setCanPreviewGif(boolean z) {
        this.canPreviewGif = z;
    }

    public void setChecked(final boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null) {
            return;
        }
        if (checkBox2.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
        AnimatorSet animatorSet = this.animator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animator = null;
        }
        if (!z2) {
            this.imageScale = z ? 0.85f : 1.0f;
            invalidate();
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animator = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<ContextLinkCell, Float>) this.IMAGE_SCALE, z ? 0.81f : 1.0f));
        this.animator.setDuration(200L);
        this.animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (ContextLinkCell.this.animator == null || !ContextLinkCell.this.animator.equals(animator)) {
                    return;
                }
                ContextLinkCell.this.animator = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ContextLinkCell.this.animator == null || !ContextLinkCell.this.animator.equals(animator)) {
                    return;
                }
                ContextLinkCell.this.animator = null;
                if (z) {
                    return;
                }
                ContextLinkCell.this.setBackgroundColor(0);
            }
        });
        this.animator.start();
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public void setGif(TLRPC.Document document, Object obj, int i, boolean z) {
        this.needDivider = z;
        this.needShadow = false;
        this.currentDate = i;
        this.inlineResult = null;
        this.parentObject = obj;
        this.documentAttach = document;
        this.photoAttach = null;
        this.mediaWebpage = true;
        this.isForceGif = true;
        setAttachType();
        this.documentAttachType = 2;
        requestLayout();
        this.fileName = null;
        this.cacheFile = null;
        this.fileExist = false;
        this.resolvingFileName = false;
        updateButtonState(false, false);
    }

    public void setGif(TLRPC.Document document, boolean z) {
        setGif(document, "gif" + document, 0, z);
    }

    public void setIsKeyboard(boolean z) {
        this.isKeyboard = z;
    }

    public void setLink(TLRPC.BotInlineResult botInlineResult, TLRPC.User user, boolean z, boolean z2, boolean z3, boolean z4) {
        this.needDivider = z2;
        this.needShadow = z3;
        this.inlineBot = user;
        this.inlineResult = botInlineResult;
        this.parentObject = botInlineResult;
        if (botInlineResult != null) {
            this.documentAttach = botInlineResult.document;
            this.photoAttach = botInlineResult.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = z;
        this.isForceGif = z4;
        setAttachType();
        if (z4) {
            this.documentAttachType = 2;
        }
        requestLayout();
        this.fileName = null;
        this.cacheFile = null;
        this.fileExist = false;
        this.resolvingFileName = false;
        updateButtonState(false, false);
    }

    @Override
    public void setPressed(boolean z) {
        super.setPressed(z);
        ButtonBounce buttonBounce = this.buttonBounce;
        if (buttonBounce != null) {
            buttonBounce.setPressed(z || this.scaled);
        }
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        ButtonBounce buttonBounce = this.buttonBounce;
        if (buttonBounce != null) {
            buttonBounce.setPressed(isPressed() || this.scaled);
        }
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public void updateButtonState(boolean z, boolean z2) {
        String str = this.fileName;
        if (str == null && !this.resolvingFileName) {
            this.resolvingFileName = true;
            int i = this.resolveFileNameId;
            this.resolveFileNameId = i;
            Utilities.searchQueue.postRunnable(new AnonymousClass1(i, z));
            this.radialProgress.setIcon(4, z, false);
            return;
        }
        if (TextUtils.isEmpty(str)) {
            this.buttonState = -1;
            this.radialProgress.setIcon(4, z, false);
            return;
        }
        boolean isLoadingFile = this.documentAttach != null ? FileLoader.getInstance(this.currentAccount).isLoadingFile(this.fileName) : ImageLoader.getInstance().isLoadingHttpFile(this.fileName);
        if (isLoadingFile || !this.fileExist) {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.fileName, this);
            int i2 = this.documentAttachType;
            if (i2 != 5 && i2 != 3) {
                this.buttonState = 1;
                Float fileProgress = ImageLoader.getInstance().getFileProgress(this.fileName);
                this.radialProgress.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
            } else if (isLoadingFile) {
                this.buttonState = 4;
                Float fileProgress2 = ImageLoader.getInstance().getFileProgress(this.fileName);
                if (fileProgress2 != null) {
                    this.radialProgress.setProgress(fileProgress2.floatValue(), z2);
                } else {
                    this.radialProgress.setProgress(0.0f, z2);
                }
            } else {
                this.buttonState = 2;
            }
        } else {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            int i3 = this.documentAttachType;
            if (i3 == 5 || i3 == 3) {
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, z2);
            } else {
                this.buttonState = -1;
            }
        }
        this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
        invalidate();
    }
}
