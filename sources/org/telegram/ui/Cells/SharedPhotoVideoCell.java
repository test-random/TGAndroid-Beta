package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class SharedPhotoVideoCell extends FrameLayout {
    private Paint backgroundPaint;
    private int currentAccount;
    private SharedPhotoVideoCellDelegate delegate;
    private boolean ignoreLayout;
    private int[] indeces;
    private boolean isFirst;
    private int itemsCount;
    private MessageObject[] messageObjects;
    private PhotoVideoView[] photoVideoViews;
    private int type;

    public class PhotoVideoView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox2 checkBox;
        private FrameLayout container;
        private MessageObject currentMessageObject;
        private BackupImageView imageView;
        private View selector;
        private FrameLayout videoInfoContainer;
        private TextView videoTextView;

        public PhotoVideoView(Context context) {
            super(context);
            setWillNotDraw(false);
            FrameLayout frameLayout = new FrameLayout(context);
            this.container = frameLayout;
            addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.getImageReceiver().setNeedsQualityThumb(true);
            this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
            this.container.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout2 = new FrameLayout(context) {
                private RectF rect = new RectF();

                @Override
                protected void onDraw(Canvas canvas) {
                    this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                }
            };
            this.videoInfoContainer = frameLayout2;
            frameLayout2.setWillNotDraw(false);
            this.videoInfoContainer.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
            this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-2, 17.0f, 83, 4.0f, 0.0f, 0.0f, 4.0f));
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.play_mini_video);
            this.videoInfoContainer.addView(imageView, LayoutHelper.createFrame(-2, -2, 19));
            TextView textView = new TextView(context);
            this.videoTextView = textView;
            textView.setTextColor(-1);
            this.videoTextView.setTypeface(AndroidUtilities.bold());
            this.videoTextView.setTextSize(1, 12.0f);
            this.videoTextView.setImportantForAccessibility(2);
            this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 13.0f, -0.7f, 0.0f, 0.0f));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(-1, Theme.key_sharedMedia_photoPlaceholder, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(1);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 1.0f, 1.0f, 0.0f));
        }

        @Override
        public void clearAnimation() {
            super.clearAnimation();
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || PhotoViewer.isShowingImage(this.currentMessageObject)) {
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), SharedPhotoVideoCell.this.backgroundPaint);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            String string;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (this.currentMessageObject.isVideo()) {
                string = LocaleController.getString(R.string.AttachVideo) + ", " + LocaleController.formatDuration((int) this.currentMessageObject.getDuration());
            } else {
                string = LocaleController.getString(R.string.AttachPhoto);
            }
            accessibilityNodeInfo.setText(string);
            if (this.checkBox.isChecked()) {
                accessibilityNodeInfo.setCheckable(true);
                accessibilityNodeInfo.setChecked(true);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setChecked(boolean z, boolean z2) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            if (!z2) {
                this.container.setScaleX(z ? 0.85f : 1.0f);
                this.container.setScaleY(z ? 0.85f : 1.0f);
                return;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animator = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.container, (Property<FrameLayout, Float>) View.SCALE_X, z ? 0.81f : 1.0f), ObjectAnimator.ofFloat(this.container, (Property<FrameLayout, Float>) View.SCALE_Y, z ? 0.81f : 1.0f));
            this.animator.setDuration(200L);
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (PhotoVideoView.this.animator == null || !PhotoVideoView.this.animator.equals(animator)) {
                        return;
                    }
                    PhotoVideoView.this.animator = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (PhotoVideoView.this.animator == null || !PhotoVideoView.this.animator.equals(animator)) {
                        return;
                    }
                    PhotoVideoView.this.animator = null;
                }
            });
            this.animator.start();
        }

        public void setMessageObject(MessageObject messageObject) {
            TLRPC.PhotoSize photoSize;
            Drawable drawable;
            ImageLocation forObject;
            String str;
            int i;
            BackupImageView backupImageView;
            ImageLocation imageLocation;
            String str2;
            String str3;
            Bitmap bitmap;
            this.currentMessageObject = messageObject;
            this.imageView.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(messageObject), false);
            if (TextUtils.isEmpty(MessagesController.getInstance(SharedPhotoVideoCell.this.currentAccount).getRestrictionReason(messageObject.messageOwner.restriction_reason))) {
                if (messageObject.isVideo()) {
                    this.videoInfoContainer.setVisibility(0);
                    this.videoTextView.setText(AndroidUtilities.formatShortDuration((int) messageObject.getDuration()));
                    TLRPC.Document document = messageObject.getDocument();
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                    TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                    photoSize = closestPhotoSizeWithSize != closestPhotoSizeWithSize2 ? closestPhotoSizeWithSize2 : null;
                    if (closestPhotoSizeWithSize != null) {
                        if (messageObject.strippedThumb != null) {
                            this.imageView.setImage(ImageLocation.getForDocument(photoSize, document), "100_100", (String) null, messageObject.strippedThumb, messageObject);
                            return;
                        }
                        BackupImageView backupImageView2 = this.imageView;
                        ImageLocation forDocument = ImageLocation.getForDocument(photoSize, document);
                        ImageLocation forDocument2 = ImageLocation.getForDocument(closestPhotoSizeWithSize, document);
                        drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_placeholder_in);
                        str = null;
                        i = 0;
                        backupImageView = backupImageView2;
                        imageLocation = forDocument;
                        str2 = "100_100";
                        forObject = forDocument2;
                        str3 = "b";
                        bitmap = null;
                    }
                    this.imageView.setImageResource(R.drawable.photo_placeholder_in);
                }
                TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                if ((messageMedia instanceof TLRPC.TL_messageMediaPhoto) && messageMedia.photo != null && !messageObject.photoThumbs.isEmpty()) {
                    this.videoInfoContainer.setVisibility(4);
                    TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
                    TLRPC.PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320, false, closestPhotoSizeWithSize3, false);
                    if (messageObject.mediaExists || DownloadController.getInstance(SharedPhotoVideoCell.this.currentAccount).canDownloadMedia(messageObject)) {
                        photoSize = closestPhotoSizeWithSize4 != closestPhotoSizeWithSize3 ? closestPhotoSizeWithSize3 : null;
                        if (messageObject.strippedThumb != null) {
                            this.imageView.getImageReceiver().setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject.photoThumbsObject), "100_100", null, null, messageObject.strippedThumb, closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0L, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                            return;
                        } else {
                            this.imageView.getImageReceiver().setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject.photoThumbsObject), "100_100", ImageLocation.getForObject(photoSize, messageObject.photoThumbsObject), "b", closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0L, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                            return;
                        }
                    }
                    drawable = messageObject.strippedThumb;
                    if (drawable != null) {
                        backupImageView = this.imageView;
                        str = null;
                        i = 0;
                        imageLocation = null;
                        str2 = null;
                        forObject = null;
                        str3 = null;
                        bitmap = null;
                    } else {
                        BackupImageView backupImageView3 = this.imageView;
                        forObject = ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject.photoThumbsObject);
                        drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_placeholder_in);
                        str = null;
                        i = 0;
                        backupImageView = backupImageView3;
                        imageLocation = null;
                        str2 = null;
                        str3 = "b";
                        bitmap = null;
                    }
                }
                backupImageView.setImage(imageLocation, str2, forObject, str3, drawable, bitmap, str, i, messageObject);
                return;
            }
            this.videoInfoContainer.setVisibility(4);
            this.imageView.setImageResource(R.drawable.photo_placeholder_in);
        }
    }

    public interface SharedPhotoVideoCellDelegate {
        void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);
    }

    public SharedPhotoVideoCell(Context context, int i) {
        super(context);
        Paint paint = new Paint();
        this.backgroundPaint = paint;
        this.currentAccount = UserConfig.selectedAccount;
        this.type = i;
        paint.setColor(Theme.getColor(Theme.key_sharedMedia_photoPlaceholder));
        this.messageObjects = new MessageObject[6];
        this.photoVideoViews = new PhotoVideoView[6];
        this.indeces = new int[6];
        for (int i2 = 0; i2 < 6; i2++) {
            this.photoVideoViews[i2] = new PhotoVideoView(context);
            addView(this.photoVideoViews[i2]);
            this.photoVideoViews[i2].setVisibility(4);
            this.photoVideoViews[i2].setTag(Integer.valueOf(i2));
            this.photoVideoViews[i2].setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedPhotoVideoCell.this.lambda$new$0(view);
                }
            });
            this.photoVideoViews[i2].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public final boolean onLongClick(View view) {
                    boolean lambda$new$1;
                    lambda$new$1 = SharedPhotoVideoCell.this.lambda$new$1(view);
                    return lambda$new$1;
                }
            });
        }
    }

    public static int getItemSize(int i) {
        return ((AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x) - ((i - 1) * AndroidUtilities.dp(2.0f))) / i;
    }

    public void lambda$new$0(View view) {
        if (this.delegate != null) {
            int intValue = ((Integer) view.getTag()).intValue();
            this.delegate.didClickItem(this, this.indeces[intValue], this.messageObjects[intValue], intValue);
        }
    }

    public boolean lambda$new$1(View view) {
        if (this.delegate == null) {
            return false;
        }
        int intValue = ((Integer) view.getTag()).intValue();
        return this.delegate.didLongClickItem(this, this.indeces[intValue], this.messageObjects[intValue], intValue);
    }

    public SharedPhotoVideoCellDelegate getDelegate() {
        return this.delegate;
    }

    public BackupImageView getImageView(int i) {
        if (i >= this.itemsCount) {
            return null;
        }
        return this.photoVideoViews[i].imageView;
    }

    public MessageObject getMessageObject(int i) {
        if (i >= this.itemsCount) {
            return null;
        }
        return this.messageObjects[i];
    }

    @Override
    public void invalidate() {
        for (int i = 0; i < 6; i++) {
            this.photoVideoViews[i].invalidate();
        }
        super.invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = this.type == 1 ? (View.MeasureSpec.getSize(i) - ((this.itemsCount - 1) * AndroidUtilities.dp(2.0f))) / this.itemsCount : getItemSize(this.itemsCount);
        this.ignoreLayout = true;
        for (int i3 = 0; i3 < this.itemsCount; i3++) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.photoVideoViews[i3].getLayoutParams();
            layoutParams.topMargin = this.isFirst ? 0 : AndroidUtilities.dp(2.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(2.0f) + size) * i3;
            if (i3 == this.itemsCount - 1) {
                layoutParams.width = (AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x) - ((this.itemsCount - 1) * (AndroidUtilities.dp(2.0f) + size));
            } else {
                layoutParams.width = size;
            }
            layoutParams.height = size;
            layoutParams.gravity = 51;
            this.photoVideoViews[i3].setLayoutParams(layoutParams);
        }
        this.ignoreLayout = false;
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((this.isFirst ? 0 : AndroidUtilities.dp(2.0f)) + size, 1073741824));
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.photoVideoViews[i].setChecked(z, z2);
    }

    public void setDelegate(SharedPhotoVideoCellDelegate sharedPhotoVideoCellDelegate) {
        this.delegate = sharedPhotoVideoCellDelegate;
    }

    public void setIsFirst(boolean z) {
        this.isFirst = z;
    }

    public void setItem(int i, int i2, MessageObject messageObject) {
        this.messageObjects[i] = messageObject;
        this.indeces[i] = i2;
        PhotoVideoView[] photoVideoViewArr = this.photoVideoViews;
        if (messageObject != null) {
            photoVideoViewArr[i].setVisibility(0);
            this.photoVideoViews[i].setMessageObject(messageObject);
        } else {
            photoVideoViewArr[i].clearAnimation();
            this.photoVideoViews[i].setVisibility(4);
            this.messageObjects[i] = null;
        }
    }

    public void setItemsCount(int i) {
        int i2 = 0;
        while (true) {
            PhotoVideoView[] photoVideoViewArr = this.photoVideoViews;
            if (i2 >= photoVideoViewArr.length) {
                this.itemsCount = i;
                return;
            } else {
                photoVideoViewArr[i2].clearAnimation();
                this.photoVideoViews[i2].setVisibility(i2 < i ? 0 : 4);
                i2++;
            }
        }
    }

    public void updateCheckboxColor() {
        for (int i = 0; i < 6; i++) {
            this.photoVideoViews[i].checkBox.invalidate();
        }
    }
}
