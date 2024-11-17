package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSessionWrapper;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPermissionCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarConstructorFragment;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertPhotoLayout;
import org.telegram.ui.Components.MessagePreviewView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.ShutterButton;
import org.telegram.ui.Components.ZoomControlView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.AlbumButton;

public class ChatAttachAlertPhotoLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private static boolean mediaFromExternalCamera;
    private PhotoAttachAdapter adapter;
    float additionCloseCameraY;
    private Runnable afterCameraInitRunnable;
    private int alertOnlyOnce;
    private int[] animateCameraValues;
    float animationClipBottom;
    float animationClipLeft;
    float animationClipRight;
    float animationClipTop;
    private boolean cameraAnimationInProgress;
    private PhotoAttachAdapter cameraAttachAdapter;
    protected PhotoAttachCameraCell cameraCell;
    private Drawable cameraDrawable;
    boolean cameraExpanded;
    protected FrameLayout cameraIcon;
    private AnimatorSet cameraInitAnimation;
    private float cameraOpenProgress;
    public boolean cameraOpened;
    private FrameLayout cameraPanel;
    private LinearLayoutManager cameraPhotoLayoutManager;
    private RecyclerListView cameraPhotoRecyclerView;
    private boolean cameraPhotoRecyclerViewIgnoreLayout;
    protected CameraView cameraView;
    private float[] cameraViewLocation;
    private float cameraViewOffsetBottomY;
    private float cameraViewOffsetX;
    private float cameraViewOffsetY;
    private float cameraZoom;
    private boolean canSaveCameraPreview;
    private boolean cancelTakingPhotos;
    public MessagePreviewView.ToggleButton captionItem;
    private boolean checkCameraWhenShown;
    private ActionBarMenuSubItem compressItem;
    private TextView counterTextView;
    public int currentItemTop;
    private float currentPanTranslationY;
    private int currentSelectedCount;
    private boolean deviceHasGoodCamera;
    private boolean documentsEnabled;
    private boolean dragging;
    public TextView dropDown;
    private ArrayList dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private boolean flashAnimationInProgress;
    private ImageView[] flashModeButton;
    boolean forceDarkTheme;
    private MediaController.AlbumEntry galleryAlbumEntry;
    private int gridExtraSpace;
    public RecyclerListView gridView;
    private ViewPropertyAnimator headerAnimator;
    private android.graphics.Rect hitRect;
    private boolean ignoreLayout;
    private DecelerateInterpolator interpolator;
    private Boolean isCameraFrontfaceBeforeEnteringEditMode;
    private boolean isHidden;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private int itemSize;
    private int itemsPerRow;
    private int lastItemSize;
    private int lastNotifyWidth;
    private float lastY;
    private GridLayoutManager layoutManager;
    private boolean loading;
    private boolean maybeStartDraging;
    private boolean mediaEnabled;
    private final boolean needCamera;
    private boolean noCameraPermissions;
    private boolean noGalleryPermissions;
    private AnimationNotificationsLocker notificationsLocker;
    private boolean photoEnabled;
    public PhotoViewer.PhotoViewerProvider photoViewerProvider;
    private float pinchStartDistance;
    private boolean pressed;
    protected ActionBarMenuSubItem previewItem;
    private EmptyTextProgressView progressView;
    private TextView recordTime;
    private boolean requestingPermissions;
    private MediaController.AlbumEntry selectedAlbumEntry;
    private boolean shouldSelect;
    private boolean showAvatarConstructor;
    private ShutterButton shutterButton;
    private ActionBarMenuSubItem spoilerItem;
    private ActionBarMenuSubItem starsItem;
    private ImageView switchCameraButton;
    private boolean takingPhoto;
    private TextView tooltipTextView;
    private boolean videoEnabled;
    private Runnable videoRecordRunnable;
    private int videoRecordTime;
    private int[] viewPosition;
    private AnimatorSet zoomControlAnimation;
    private Runnable zoomControlHideRunnable;
    private ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;
    private static ArrayList cameraPhotos = new ArrayList();
    public static HashMap selectedPhotos = new HashMap();
    public static ArrayList selectedPhotosOrder = new ArrayList();
    public static int lastImageId = -1;

    public class AnonymousClass1 extends BasePhotoProvider {
        AnonymousClass1() {
            super(ChatAttachAlertPhotoLayout.this, null);
        }

        public void lambda$onClose$0() {
            ChatAttachAlertPhotoLayout.this.setCurrentSpoilerVisible(-1, true);
        }

        @Override
        public boolean allowCaption() {
            return !ChatAttachAlertPhotoLayout.this.parentAlert.isPhotoPicker;
        }

        @Override
        public boolean cancelButtonPressed() {
            return false;
        }

        @Override
        public long getDialogId() {
            BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
            return baseFragment instanceof ChatActivity ? ((ChatActivity) baseFragment).getDialogId() : super.getDialogId();
        }

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
            PhotoAttachPhotoCell cellForIndex = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
            if (cellForIndex == null) {
                return null;
            }
            int[] iArr = new int[2];
            cellForIndex.getImageView().getLocationInWindow(iArr);
            if (Build.VERSION.SDK_INT < 26) {
                iArr[0] = iArr[0] - ChatAttachAlertPhotoLayout.this.parentAlert.getLeftInset();
            }
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1];
            placeProviderObject.parentView = ChatAttachAlertPhotoLayout.this.gridView;
            ImageReceiver imageReceiver = cellForIndex.getImageView().getImageReceiver();
            placeProviderObject.imageReceiver = imageReceiver;
            placeProviderObject.thumb = imageReceiver.getBitmapSafe();
            placeProviderObject.scale = cellForIndex.getScale();
            placeProviderObject.clipBottomAddition = (int) ChatAttachAlertPhotoLayout.this.parentAlert.getClipLayoutBottom();
            cellForIndex.showCheck(false);
            return placeProviderObject;
        }

        @Override
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell cellForIndex = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
            if (cellForIndex != null) {
                return cellForIndex.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        @Override
        public void onApplyCaption(CharSequence charSequence) {
            CharSequence charSequence2;
            ArrayList<TLRPC.MessageEntity> arrayList;
            if (ChatAttachAlertPhotoLayout.selectedPhotos.size() <= 0 || ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() <= 0) {
                return;
            }
            Object obj = ChatAttachAlertPhotoLayout.selectedPhotos.get(ChatAttachAlertPhotoLayout.selectedPhotosOrder.get(0));
            if (obj instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                charSequence2 = photoEntry.caption;
                arrayList = photoEntry.entities;
            } else {
                charSequence2 = null;
                arrayList = null;
            }
            if (obj instanceof MediaController.SearchImage) {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                charSequence2 = searchImage.caption;
                arrayList = searchImage.entities;
            }
            ArrayList<TLRPC.MessageEntity> arrayList2 = arrayList;
            if (charSequence2 != null && arrayList2 != null) {
                if (!(charSequence2 instanceof Spannable)) {
                    charSequence2 = new SpannableStringBuilder(charSequence2);
                }
                MessageObject.addEntitiesToText(charSequence2, arrayList2, false, false, false, false);
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.commentTextView.setText(AnimatedEmojiSpan.cloneSpans(charSequence2, 3));
        }

        @Override
        public void onClose() {
            ChatAttachAlertPhotoLayout.this.resumeCameraPreview();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertPhotoLayout.AnonymousClass1.this.lambda$onClose$0();
                }
            }, 150L);
        }

        @Override
        public void onEditModeChanged(boolean z) {
            ChatAttachAlertPhotoLayout.this.onPhotoEditModeChanged(z);
        }

        @Override
        public void onOpen() {
            ChatAttachAlertPhotoLayout.this.pauseCameraPreview();
            ChatAttachAlertPhotoLayout.this.setCurrentSpoilerVisible(-1, true);
        }

        @Override
        public void onPreClose() {
            ChatAttachAlertPhotoLayout.this.setCurrentSpoilerVisible(-1, false);
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            chatAttachAlertPhotoLayout.parentAlert.sent = true;
            MediaController.PhotoEntry photoEntryAtPosition = chatAttachAlertPhotoLayout.getPhotoEntryAtPosition(i);
            if (photoEntryAtPosition != null) {
                photoEntryAtPosition.editedInfo = videoEditedInfo;
            }
            if (ChatAttachAlertPhotoLayout.selectedPhotos.isEmpty() && photoEntryAtPosition != null) {
                ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntryAtPosition, -1);
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            if (chatAttachAlert.checkCaption(chatAttachAlert.commentTextView.getText())) {
                return;
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.applyCaption();
            if (PhotoViewer.getInstance().hasCaptionForAllMedia) {
                HashMap selectedPhotos = getSelectedPhotos();
                ArrayList selectedPhotosOrder = getSelectedPhotosOrder();
                if (!selectedPhotos.isEmpty()) {
                    for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                        Object obj = selectedPhotos.get(selectedPhotosOrder.get(i3));
                        if (obj instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                            if (i3 == 0) {
                                CharSequence[] charSequenceArr = {PhotoViewer.getInstance().captionForAllMedia};
                                photoEntry.entities = MediaDataController.getInstance(UserConfig.selectedAccount).getEntities(charSequenceArr, false);
                                CharSequence charSequence = charSequenceArr[0];
                                photoEntry.caption = charSequence;
                                if (ChatAttachAlertPhotoLayout.this.parentAlert.checkCaption(charSequence)) {
                                    return;
                                }
                            } else {
                                photoEntry.caption = null;
                            }
                        }
                    }
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(7, true, z, i2, 0L, false, z2);
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.cameraPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
        }

        @Override
        public void updatePhotoAtIndex(int i) {
            BackupImageView imageView;
            StringBuilder sb;
            String str;
            String sb2;
            PhotoAttachPhotoCell cellForIndex = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
            if (cellForIndex != null) {
                cellForIndex.getImageView().setOrientation(0, true);
                MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
                if (photoEntryAtPosition == null) {
                    return;
                }
                if (photoEntryAtPosition.thumbPath != null) {
                    imageView = cellForIndex.getImageView();
                    sb2 = photoEntryAtPosition.thumbPath;
                } else {
                    if (photoEntryAtPosition.path == null) {
                        cellForIndex.getImageView().setImageDrawable(Theme.chat_attachEmptyDrawable);
                        return;
                    }
                    cellForIndex.getImageView().setOrientation(photoEntryAtPosition.orientation, photoEntryAtPosition.invert, true);
                    boolean z = photoEntryAtPosition.isVideo;
                    imageView = cellForIndex.getImageView();
                    if (z) {
                        sb = new StringBuilder();
                        str = "vthumb://";
                    } else {
                        sb = new StringBuilder();
                        str = "thumb://";
                    }
                    sb.append(str);
                    sb.append(photoEntryAtPosition.imageId);
                    sb.append(":");
                    sb.append(photoEntryAtPosition.path);
                    sb2 = sb.toString();
                }
                imageView.setImage(sb2, null, Theme.chat_attachEmptyDrawable);
            }
        }

        @Override
        public void willHidePhotoViewer() {
            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) childAt).showCheck(true);
                }
            }
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell cellForIndex = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
            if (cellForIndex != null) {
                cellForIndex.showCheck(true);
            }
        }
    }

    public class AnonymousClass10 implements ShutterButton.ShutterButtonDelegate {
        private File outputFile;
        final FrameLayout val$container;
        final Theme.ResourcesProvider val$resourcesProvider;
        private boolean zoomingWas;

        AnonymousClass10(Theme.ResourcesProvider resourcesProvider, FrameLayout frameLayout) {
            this.val$resourcesProvider = resourcesProvider;
            this.val$container = frameLayout;
        }

        public void lambda$shutterLongPressed$0() {
            if (ChatAttachAlertPhotoLayout.this.videoRecordRunnable == null) {
                return;
            }
            ChatAttachAlertPhotoLayout.access$2908(ChatAttachAlertPhotoLayout.this);
            ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(ChatAttachAlertPhotoLayout.this.videoRecordTime));
            AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000L);
        }

        public void lambda$shutterLongPressed$1(String str, long j) {
            int i;
            int i2;
            int i3;
            MediaController.PhotoEntry photoEntry;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout;
            BitmapFactory.Options options;
            if (this.outputFile != null) {
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                if (chatAttachAlertPhotoLayout2.parentAlert.destroyed || chatAttachAlertPhotoLayout2.cameraView == null) {
                    return;
                }
                boolean unused = ChatAttachAlertPhotoLayout.mediaFromExternalCamera = false;
                try {
                    options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(new File(str).getAbsolutePath(), options);
                    i = options.outWidth;
                } catch (Exception unused2) {
                    i = 0;
                }
                try {
                    i3 = options.outHeight;
                    i2 = i;
                } catch (Exception unused3) {
                    i2 = i;
                    i3 = 0;
                    int i4 = ChatAttachAlertPhotoLayout.lastImageId;
                    ChatAttachAlertPhotoLayout.lastImageId = i4 - 1;
                    photoEntry = new MediaController.PhotoEntry(0, i4, 0L, this.outputFile.getAbsolutePath(), 0, true, i2, i3, 0L);
                    photoEntry.duration = (int) (((float) j) / 1000.0f);
                    photoEntry.thumbPath = str;
                    chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                    if (chatAttachAlertPhotoLayout.parentAlert.avatarPicker != 0) {
                        MediaController.CropState cropState = new MediaController.CropState();
                        photoEntry.cropState = cropState;
                        cropState.mirrored = true;
                        cropState.freeform = false;
                        cropState.lockedAspectRatio = 1.0f;
                    }
                    ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, false, false);
                }
                int i42 = ChatAttachAlertPhotoLayout.lastImageId;
                ChatAttachAlertPhotoLayout.lastImageId = i42 - 1;
                photoEntry = new MediaController.PhotoEntry(0, i42, 0L, this.outputFile.getAbsolutePath(), 0, true, i2, i3, 0L);
                photoEntry.duration = (int) (((float) j) / 1000.0f);
                photoEntry.thumbPath = str;
                chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                if (chatAttachAlertPhotoLayout.parentAlert.avatarPicker != 0 && chatAttachAlertPhotoLayout.cameraView.isFrontface()) {
                    MediaController.CropState cropState2 = new MediaController.CropState();
                    photoEntry.cropState = cropState2;
                    cropState2.mirrored = true;
                    cropState2.freeform = false;
                    cropState2.lockedAspectRatio = 1.0f;
                }
                ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, false, false);
            }
        }

        public void lambda$shutterLongPressed$2() {
            AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000L);
        }

        public void lambda$shutterReleased$3(java.io.File r18, boolean r19, java.lang.Integer r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.AnonymousClass10.lambda$shutterReleased$3(java.io.File, boolean, java.lang.Integer):void");
        }

        @Override
        public boolean onTranslationChanged(float f, float f2) {
            boolean z = this.val$container.getWidth() < this.val$container.getHeight();
            float f3 = z ? f : f2;
            float f4 = z ? f2 : f;
            if (!this.zoomingWas && Math.abs(f3) > Math.abs(f4)) {
                return ChatAttachAlertPhotoLayout.this.zoomControlView.getTag() == null;
            }
            if (f4 < 0.0f) {
                ChatAttachAlertPhotoLayout.this.showZoomControls(true, true);
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom((-f4) / AndroidUtilities.dp(200.0f), true);
                this.zoomingWas = true;
                return false;
            }
            if (this.zoomingWas) {
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, true);
            }
            if (f == 0.0f && f2 == 0.0f) {
                this.zoomingWas = false;
            }
            if (this.zoomingWas) {
                return false;
            }
            return (f == 0.0f && f2 == 0.0f) ? false : true;
        }

        @Override
        public void shutterCancel() {
            File file = this.outputFile;
            if (file != null) {
                file.delete();
                this.outputFile = null;
            }
            ChatAttachAlertPhotoLayout.this.resetRecordState();
            CameraController.getInstance().stopVideoRecording(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), true);
        }

        @Override
        public boolean shutterLongPressed() {
            int checkSelfPermission;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            ChatAttachAlert chatAttachAlert = chatAttachAlertPhotoLayout.parentAlert;
            if ((chatAttachAlert.avatarPicker == 2 || (chatAttachAlert.baseFragment instanceof ChatActivity)) && !chatAttachAlertPhotoLayout.takingPhoto) {
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                ChatAttachAlert chatAttachAlert2 = chatAttachAlertPhotoLayout2.parentAlert;
                if (chatAttachAlert2.destroyed || chatAttachAlertPhotoLayout2.cameraView == null || chatAttachAlert2.isStickerMode) {
                    return false;
                }
                BaseFragment baseFragment = chatAttachAlert2.baseFragment;
                if (baseFragment == null) {
                    baseFragment = LaunchActivity.getLastFragment();
                }
                if (baseFragment != null && baseFragment.getParentActivity() != null) {
                    if (!ChatAttachAlertPhotoLayout.this.videoEnabled) {
                        BulletinFactory.of(ChatAttachAlertPhotoLayout.this.cameraView, this.val$resourcesProvider).createErrorBulletin(LocaleController.getString(R.string.GlobalAttachVideoRestricted)).show();
                        return false;
                    }
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkSelfPermission = ChatAttachAlertPhotoLayout.this.getContext().checkSelfPermission("android.permission.RECORD_AUDIO");
                        if (checkSelfPermission != 0) {
                            ChatAttachAlertPhotoLayout.this.requestingPermissions = true;
                            baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                            return false;
                        }
                    }
                    for (int i = 0; i < 2; i++) {
                        ChatAttachAlertPhotoLayout.this.flashModeButton[i].animate().alpha(0.0f).translationX(AndroidUtilities.dp(30.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    }
                    ViewPropertyAnimator duration = ChatAttachAlertPhotoLayout.this.switchCameraButton.animate().alpha(0.0f).translationX(-AndroidUtilities.dp(30.0f)).setDuration(150L);
                    CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                    duration.setInterpolator(cubicBezierInterpolator).start();
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.animate().alpha(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                    BaseFragment baseFragment2 = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                    this.outputFile = AndroidUtilities.generateVideoPath((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isSecretChat());
                    AndroidUtilities.updateViewVisibilityAnimated(ChatAttachAlertPhotoLayout.this.recordTime, true);
                    ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(0));
                    ChatAttachAlertPhotoLayout.this.videoRecordTime = 0;
                    ChatAttachAlertPhotoLayout.this.videoRecordRunnable = new Runnable() {
                        @Override
                        public final void run() {
                            ChatAttachAlertPhotoLayout.AnonymousClass10.this.lambda$shutterLongPressed$0();
                        }
                    };
                    AndroidUtilities.lockOrientation(baseFragment.getParentActivity());
                    CameraController.getInstance().recordVideo(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSessionObject(), this.outputFile, ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0, new CameraController.VideoTakeCallback() {
                        @Override
                        public final void onFinishVideoRecording(String str, long j) {
                            ChatAttachAlertPhotoLayout.AnonymousClass10.this.lambda$shutterLongPressed$1(str, j);
                        }
                    }, new Runnable() {
                        @Override
                        public final void run() {
                            ChatAttachAlertPhotoLayout.AnonymousClass10.this.lambda$shutterLongPressed$2();
                        }
                    }, ChatAttachAlertPhotoLayout.this.cameraView);
                    ChatAttachAlertPhotoLayout.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
                    ChatAttachAlertPhotoLayout.this.cameraView.runHaptic();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void shutterReleased() {
            CameraView cameraView;
            if (ChatAttachAlertPhotoLayout.this.takingPhoto || (cameraView = ChatAttachAlertPhotoLayout.this.cameraView) == null || cameraView.getCameraSession() == null) {
                return;
            }
            if (ChatAttachAlertPhotoLayout.this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                ChatAttachAlertPhotoLayout.this.resetRecordState();
                CameraController.getInstance().stopVideoRecording(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), false);
                ChatAttachAlertPhotoLayout.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
            } else {
                if (!ChatAttachAlertPhotoLayout.this.photoEnabled) {
                    BulletinFactory.of(ChatAttachAlertPhotoLayout.this.cameraView, this.val$resourcesProvider).createErrorBulletin(LocaleController.getString(R.string.GlobalAttachPhotoRestricted)).show();
                    return;
                }
                BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                final File generatePicturePath = AndroidUtilities.generatePicturePath((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isSecretChat(), null);
                final boolean isSameTakePictureOrientation = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                CameraSessionWrapper cameraSession = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession();
                ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                cameraSession.setFlipFront((chatAttachAlert.baseFragment instanceof ChatActivity) || chatAttachAlert.avatarPicker == 2);
                ChatAttachAlertPhotoLayout.this.takingPhoto = CameraController.getInstance().takePicture(generatePicturePath, false, ChatAttachAlertPhotoLayout.this.cameraView.getCameraSessionObject(), new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ChatAttachAlertPhotoLayout.AnonymousClass10.this.lambda$shutterReleased$3(generatePicturePath, isSameTakePictureOrientation, (Integer) obj);
                    }
                });
                ChatAttachAlertPhotoLayout.this.cameraView.startTakePictureAnimation(true);
            }
        }
    }

    public class AnonymousClass15 extends BasePhotoProvider {
        final boolean val$sameTakePictureOrientation;

        AnonymousClass15(boolean z) {
            super(ChatAttachAlertPhotoLayout.this, null);
            this.val$sameTakePictureOrientation = z;
        }

        public void lambda$cancelButtonPressed$0() {
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            if (chatAttachAlertPhotoLayout.cameraView == null || chatAttachAlertPhotoLayout.parentAlert.isDismissed() || Build.VERSION.SDK_INT < 21) {
                return;
            }
            ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1028);
        }

        @Override
        public boolean allowCaption() {
            return !ChatAttachAlertPhotoLayout.this.parentAlert.isPhotoPicker;
        }

        @Override
        public boolean canCaptureMorePhotos() {
            return ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos != 1;
        }

        @Override
        public boolean canScrollAway() {
            return false;
        }

        @Override
        public boolean cancelButtonPressed() {
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            if (chatAttachAlertPhotoLayout.cameraOpened && chatAttachAlertPhotoLayout.cameraView != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChatAttachAlertPhotoLayout.AnonymousClass15.this.lambda$cancelButtonPressed$0();
                    }
                }, 1000L);
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, false);
                ChatAttachAlertPhotoLayout.this.cameraZoom = 0.0f;
                ChatAttachAlertPhotoLayout.this.cameraView.setZoom(0.0f);
                CameraController.getInstance().startPreview(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession());
            }
            if (ChatAttachAlertPhotoLayout.this.cancelTakingPhotos && ChatAttachAlertPhotoLayout.cameraPhotos.size() == 1) {
                int size = ChatAttachAlertPhotoLayout.cameraPhotos.size();
                for (int i = 0; i < size; i++) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(i);
                    new File(photoEntry.path).delete();
                    if (photoEntry.imagePath != null) {
                        new File(photoEntry.imagePath).delete();
                    }
                    if (photoEntry.thumbPath != null) {
                        new File(photoEntry.thumbPath).delete();
                    }
                }
                ChatAttachAlertPhotoLayout.cameraPhotos.clear();
                ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
                ChatAttachAlertPhotoLayout.selectedPhotos.clear();
                ChatAttachAlertPhotoLayout.this.counterTextView.setVisibility(4);
                ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(8);
                ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
                ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
                ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(0);
            }
            return true;
        }

        @Override
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            return null;
        }

        @Override
        public void needAddMorePhotos() {
            ChatAttachAlertPhotoLayout.this.cancelTakingPhotos = false;
            if (ChatAttachAlertPhotoLayout.mediaFromExternalCamera) {
                ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(0, true, true, 0, 0L, false, false);
                return;
            }
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            if (!chatAttachAlertPhotoLayout.cameraOpened) {
                chatAttachAlertPhotoLayout.openCamera(false);
            }
            ChatAttachAlertPhotoLayout.this.counterTextView.setVisibility(0);
            ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(0);
            ChatAttachAlertPhotoLayout.this.counterTextView.setAlpha(1.0f);
            ChatAttachAlertPhotoLayout.this.updatePhotosCounter(false);
        }

        @Override
        public void onClose() {
            ChatAttachAlertPhotoLayout.this.resumeCameraPreview();
        }

        @Override
        public void onEditModeChanged(boolean z) {
            ChatAttachAlertPhotoLayout.this.onPhotoEditModeChanged(z);
        }

        @Override
        public void onOpen() {
            ChatAttachAlertPhotoLayout.this.pauseCameraPreview();
        }

        @Override
        public boolean scaleToFill() {
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            if (chatAttachAlertPhotoLayout.parentAlert.destroyed) {
                return false;
            }
            return this.val$sameTakePictureOrientation || Settings.System.getInt(chatAttachAlertPhotoLayout.getContext().getContentResolver(), "accelerometer_rotation", 0) == 1;
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            ChatAttachAlertPhotoLayout.this.parentAlert.sent = true;
            if (ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty() || ChatAttachAlertPhotoLayout.this.parentAlert.destroyed) {
                return;
            }
            if (videoEditedInfo != null && i >= 0 && i < ChatAttachAlertPhotoLayout.cameraPhotos.size()) {
                ((MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(i)).editedInfo = videoEditedInfo;
            }
            BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
            if (!(baseFragment instanceof ChatActivity) || !((ChatActivity) baseFragment).isSecretChat()) {
                int size = ChatAttachAlertPhotoLayout.cameraPhotos.size();
                for (int i3 = 0; i3 < size; i3++) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(i3);
                    if (photoEntry.ttl <= 0) {
                        AndroidUtilities.addMediaToGallery(photoEntry.path);
                    }
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.applyCaption();
            ChatAttachAlertPhotoLayout.this.closeCamera(false);
            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(z2 ? 4 : 8, true, z, i2, 0L, false, z2);
            ChatAttachAlertPhotoLayout.cameraPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
            ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
            ChatAttachAlertPhotoLayout.this.parentAlert.dismiss(true);
        }

        @Override
        public void willHidePhotoViewer() {
            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    photoAttachPhotoCell.showImage();
                    photoAttachPhotoCell.showCheck(true);
                }
            }
        }
    }

    private class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        BasePhotoProvider(ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override
        public int getPhotoIndex(int i) {
            MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            if (photoEntryAtPosition == null) {
                return -1;
            }
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId));
        }

        @Override
        public int getSelectedCount() {
            return ChatAttachAlertPhotoLayout.selectedPhotos.size();
        }

        @Override
        public HashMap getSelectedPhotos() {
            return ChatAttachAlertPhotoLayout.selectedPhotos;
        }

        @Override
        public ArrayList getSelectedPhotosOrder() {
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder;
        }

        @Override
        public boolean isPhotoChecked(int i) {
            MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            return photoEntryAtPosition != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId));
        }

        @Override
        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry photoEntryAtPosition;
            boolean z;
            if ((ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0 && ChatAttachAlertPhotoLayout.selectedPhotos.size() >= ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos && !isPhotoChecked(i)) || (photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i)) == null || ChatAttachAlertPhotoLayout.this.checkSendMediaEnabled(photoEntryAtPosition)) {
                return -1;
            }
            if (ChatAttachAlertPhotoLayout.selectedPhotos.size() + 1 > ChatAttachAlertPhotoLayout.this.maxCount()) {
                return -1;
            }
            int addToSelectedPhotos = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntryAtPosition, -1);
            if (addToSelectedPhotos == -1) {
                addToSelectedPhotos = ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId));
                z = true;
            } else {
                photoEntryAtPosition.editedInfo = null;
                z = false;
            }
            photoEntryAtPosition.editedInfo = videoEditedInfo;
            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            int i2 = 0;
            while (true) {
                if (i2 >= childCount) {
                    break;
                }
                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i2);
                if ((childAt instanceof PhotoAttachPhotoCell) && ((Integer) childAt.getTag()).intValue() == i) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if ((chatAttachAlert.baseFragment instanceof ChatActivity) && chatAttachAlert.allowOrder) {
                        ((PhotoAttachPhotoCell) childAt).setChecked(addToSelectedPhotos, z, false);
                    } else {
                        ((PhotoAttachPhotoCell) childAt).setChecked(-1, z, false);
                    }
                } else {
                    i2++;
                }
            }
            int childCount2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildCount();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount2) {
                    break;
                }
                View childAt2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildAt(i3);
                if ((childAt2 instanceof PhotoAttachPhotoCell) && ((Integer) childAt2.getTag()).intValue() == i) {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if ((chatAttachAlert2.baseFragment instanceof ChatActivity) && chatAttachAlert2.allowOrder) {
                        ((PhotoAttachPhotoCell) childAt2).setChecked(addToSelectedPhotos, z, false);
                    } else {
                        ((PhotoAttachPhotoCell) childAt2).setChecked(-1, z, false);
                    }
                } else {
                    i3++;
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(z ? 1 : 2);
            return addToSelectedPhotos;
        }
    }

    public class PhotoAttachAdapter extends RecyclerListView.FastScrollAdapter {
        private int itemsCount;
        private Context mContext;
        private boolean needCamera;
        private int photosEndRow;
        private int photosStartRow;
        private ArrayList viewsCache = new ArrayList(8);

        public PhotoAttachAdapter(Context context, boolean z) {
            this.mContext = context;
            this.needCamera = z;
        }

        public MediaController.PhotoEntry getPhoto(int i) {
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                i--;
            }
            return ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
        }

        public void lambda$createHolder$0(PhotoAttachPhotoCell photoAttachPhotoCell, PhotoAttachPhotoCell photoAttachPhotoCell2) {
            PhotoAttachAdapter photoAttachAdapter;
            TLRPC.Chat currentChat;
            if (ChatAttachAlertPhotoLayout.this.mediaEnabled && ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker == 0) {
                int intValue = ((Integer) photoAttachPhotoCell2.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = photoAttachPhotoCell2.getPhotoEntry();
                if (ChatAttachAlertPhotoLayout.this.checkSendMediaEnabled(photoEntry)) {
                    return;
                }
                if (ChatAttachAlertPhotoLayout.selectedPhotos.size() + 1 > ChatAttachAlertPhotoLayout.this.maxCount()) {
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                    BulletinFactory.of(chatAttachAlertPhotoLayout.parentAlert.sizeNotifierFrameLayout, chatAttachAlertPhotoLayout.resourcesProvider).createErrorBulletin(AndroidUtilities.replaceTags(LocaleController.formatPluralString("BusinessRepliesToastLimit", ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getMessagesController().quickReplyMessagesLimit, new Object[0]))).show();
                    return;
                }
                boolean z = !ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                if (z && ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0) {
                    int size = ChatAttachAlertPhotoLayout.selectedPhotos.size();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (size >= chatAttachAlert.maxSelectedPhotos) {
                        if (chatAttachAlert.allowOrder) {
                            BaseFragment baseFragment = chatAttachAlert.baseFragment;
                            if (!(baseFragment instanceof ChatActivity) || (currentChat = ((ChatActivity) baseFragment).getCurrentChat()) == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled || ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 2) {
                                return;
                            }
                            AlertsCreator.createSimpleAlert(ChatAttachAlertPhotoLayout.this.getContext(), LocaleController.getString(R.string.Slowmode), LocaleController.getString(R.string.SlowmodeSelectSendError), ChatAttachAlertPhotoLayout.this.resourcesProvider).show();
                            if (ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 1) {
                                ChatAttachAlertPhotoLayout.this.alertOnlyOnce = 2;
                                return;
                            }
                            return;
                        }
                        return;
                    }
                }
                int size2 = z ? ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() : -1;
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
                if ((chatAttachAlert2.baseFragment instanceof ChatActivity) && chatAttachAlert2.allowOrder) {
                    photoAttachPhotoCell2.setChecked(size2, z, true);
                } else {
                    photoAttachPhotoCell2.setChecked(-1, z, true);
                }
                ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, intValue);
                if (this == ChatAttachAlertPhotoLayout.this.cameraAttachAdapter) {
                    if (ChatAttachAlertPhotoLayout.this.adapter.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                        intValue++;
                    }
                    photoAttachAdapter = ChatAttachAlertPhotoLayout.this.adapter;
                } else {
                    photoAttachAdapter = ChatAttachAlertPhotoLayout.this.cameraAttachAdapter;
                }
                photoAttachAdapter.notifyItemChanged(intValue);
                ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(z ? 1 : 2);
                photoAttachPhotoCell.setHasSpoiler(photoEntry.hasSpoiler);
                photoAttachPhotoCell.setStarsPrice(photoEntry.starsAmount, ChatAttachAlertPhotoLayout.selectedPhotos.size() > 1);
            }
        }

        public void createCache() {
            for (int i = 0; i < 8; i++) {
                this.viewsCache.add(createHolder());
            }
        }

        public RecyclerListView.Holder createHolder() {
            final PhotoAttachPhotoCell photoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
            if (Build.VERSION.SDK_INT >= 21 && this == ChatAttachAlertPhotoLayout.this.adapter) {
                photoAttachPhotoCell.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) view;
                        if (photoAttachPhotoCell2.getTag() == null) {
                            return;
                        }
                        int intValue = ((Integer) photoAttachPhotoCell2.getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                            intValue++;
                        }
                        if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor) {
                            intValue++;
                        }
                        if (intValue == 0) {
                            int dp = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, dp);
                        } else if (intValue != ChatAttachAlertPhotoLayout.this.itemsPerRow - 1) {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        } else {
                            int dp2 = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                            outline.setRoundRect(-dp2, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + dp2, dp2);
                        }
                    }
                });
                photoAttachPhotoCell.setClipToOutline(true);
            }
            photoAttachPhotoCell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() {
                @Override
                public final void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell2) {
                    ChatAttachAlertPhotoLayout.PhotoAttachAdapter.this.lambda$createHolder$0(photoAttachPhotoCell, photoAttachPhotoCell2);
                }
            });
            return new RecyclerListView.Holder(photoAttachPhotoCell);
        }

        @Override
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            return !(ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty() && (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == null || ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.isEmpty())) && ChatAttachAlertPhotoLayout.this.parentAlert.pinnedToTop && getTotalItemsCount() > 30;
        }

        @Override
        public int getItemCount() {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 1;
            }
            int i = (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) ? 1 : 0;
            if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor) {
                i++;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                i++;
            }
            this.photosStartRow = i;
            int size = i + ChatAttachAlertPhotoLayout.cameraPhotos.size();
            if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null) {
                size += ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size();
            }
            this.photosEndRow = size;
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                size++;
            }
            this.itemsCount = size;
            return size;
        }

        @Override
        public int getItemViewType(int i) {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && i == 0 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                return ChatAttachAlertPhotoLayout.this.noCameraPermissions ? 3 : 1;
            }
            int i2 = this.needCamera ? i - 1 : i;
            if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor && i2 == 0) {
                return 4;
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter && i == this.itemsCount - 1) {
                return 2;
            }
            return ChatAttachAlertPhotoLayout.this.noGalleryPermissions ? 3 : 0;
        }

        @Override
        public String getLetter(int i) {
            MediaController.PhotoEntry photoEntry;
            ArrayList<MediaController.PhotoEntry> arrayList;
            MediaController.PhotoEntry photo = getPhoto(i);
            if (photo == null) {
                if (i <= this.photosStartRow) {
                    if (!ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty()) {
                        arrayList = ChatAttachAlertPhotoLayout.cameraPhotos;
                    } else if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos != null) {
                        arrayList = ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos;
                    }
                    photoEntry = arrayList.get(0);
                    photo = photoEntry;
                } else if (!ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.isEmpty()) {
                    photoEntry = ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.get(ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size() - 1);
                    photo = photoEntry;
                }
            }
            if (photo == null) {
                return "";
            }
            long j = photo.dateTaken;
            if (Build.VERSION.SDK_INT <= 28) {
                j /= 1000;
            }
            return LocaleController.formatYearMont(j, true);
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            double ceil = Math.ceil(getTotalItemsCount() / ChatAttachAlertPhotoLayout.this.itemsPerRow);
            Double.isNaN(measuredHeight);
            float measuredHeight2 = f * (((int) (ceil * r4)) - recyclerListView.getMeasuredHeight());
            float f2 = measuredHeight;
            iArr[0] = ((int) (measuredHeight2 / f2)) * ChatAttachAlertPhotoLayout.this.itemsPerRow;
            int paddingTop = ((int) (measuredHeight2 % f2)) + recyclerListView.getPaddingTop();
            iArr[1] = paddingTop;
            if (iArr[0] != 0 || paddingTop >= ChatAttachAlertPhotoLayout.this.getListTopPadding()) {
                return;
            }
            iArr[1] = ChatAttachAlertPhotoLayout.this.getListTopPadding();
        }

        @Override
        public float getScrollProgress(RecyclerListView recyclerListView) {
            int i = ChatAttachAlertPhotoLayout.this.itemsPerRow;
            int ceil = (int) Math.ceil(this.itemsCount / i);
            if (recyclerListView.getChildCount() == 0) {
                return 0.0f;
            }
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(0)) < 0) {
                return 0.0f;
            }
            return Utilities.clamp((((r5 / i) * measuredHeight) - r2.getTop()) / ((ceil * measuredHeight) - recyclerListView.getMeasuredHeight()), 1.0f, 0.0f);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                ChatAttachAlertPhotoLayout.this.progressView.setVisibility((!(getItemCount() == 1 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == null) && ChatAttachAlertPhotoLayout.this.mediaEnabled) ? 4 : 0);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType != 1) {
                    if (itemViewType != 3) {
                        return;
                    }
                    PhotoAttachPermissionCell photoAttachPermissionCell = (PhotoAttachPermissionCell) viewHolder.itemView;
                    photoAttachPermissionCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    photoAttachPermissionCell.setType((this.needCamera && ChatAttachAlertPhotoLayout.this.noCameraPermissions && i == 0) ? 0 : 1);
                    return;
                }
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                chatAttachAlertPhotoLayout.cameraCell = (PhotoAttachCameraCell) viewHolder.itemView;
                CameraView cameraView = chatAttachAlertPhotoLayout.cameraView;
                if (cameraView == null || !cameraView.isInited() || ChatAttachAlertPhotoLayout.this.isHidden) {
                    ChatAttachAlertPhotoLayout.this.cameraCell.setVisibility(0);
                } else {
                    ChatAttachAlertPhotoLayout.this.cameraCell.setVisibility(4);
                }
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                chatAttachAlertPhotoLayout2.cameraCell.setItemSize(chatAttachAlertPhotoLayout2.itemSize);
                return;
            }
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                i--;
            }
            if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor) {
                i--;
            }
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                photoAttachPhotoCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
            } else {
                photoAttachPhotoCell.setIsVertical(ChatAttachAlertPhotoLayout.this.cameraPhotoLayoutManager.getOrientation() == 1);
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            if (chatAttachAlert.avatarPicker != 0 || chatAttachAlert.storyMediaPicker) {
                photoAttachPhotoCell.getCheckBox().setVisibility(8);
            } else {
                photoAttachPhotoCell.getCheckBox().setVisibility(0);
            }
            MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            if (photoEntryAtPosition == null) {
                return;
            }
            photoAttachPhotoCell.setPhotoEntry(photoEntryAtPosition, ChatAttachAlertPhotoLayout.selectedPhotos.size() > 1, this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry, i == getItemCount() - 1);
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
            if ((chatAttachAlert2.baseFragment instanceof ChatActivity) && chatAttachAlert2.allowOrder) {
                photoAttachPhotoCell.setChecked(ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)), ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), false);
            } else {
                photoAttachPhotoCell.setChecked(-1, ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), false);
            }
            photoAttachPhotoCell.setAlpha(((ChatAttachAlertPhotoLayout.this.videoEnabled || !photoEntryAtPosition.isVideo) && (ChatAttachAlertPhotoLayout.this.photoEnabled || photoEntryAtPosition.isVideo)) ? 1.0f : 0.3f);
            photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
            photoAttachPhotoCell.setTag(Integer.valueOf(i));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                if (this.viewsCache.isEmpty()) {
                    return createHolder();
                }
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.viewsCache.get(0);
                this.viewsCache.remove(0);
                return holder;
            }
            if (i != 1) {
                return i != 2 ? i != 4 ? new RecyclerListView.Holder(new PhotoAttachPermissionCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider)) : new RecyclerListView.Holder(new AvatarConstructorPreviewCell(this.mContext, ChatAttachAlertPhotoLayout.this.parentAlert.forUser) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.itemSize, 1073741824));
                    }
                }) : new RecyclerListView.Holder(new View(this.mContext) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.gridExtraSpace, 1073741824));
                    }
                });
            }
            ChatAttachAlertPhotoLayout.this.cameraCell = new PhotoAttachCameraCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
            if (Build.VERSION.SDK_INT >= 21) {
                ChatAttachAlertPhotoLayout.this.cameraCell.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        int dp = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                        outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, dp);
                    }
                });
                ChatAttachAlertPhotoLayout.this.cameraCell.setClipToOutline(true);
            }
            return new RecyclerListView.Holder(ChatAttachAlertPhotoLayout.this.cameraCell);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof PhotoAttachCameraCell) {
                ((PhotoAttachCameraCell) view).updateBitmap();
            }
        }
    }

    public ChatAttachAlertPhotoLayout(ChatAttachAlert chatAttachAlert, Context context, boolean z, final boolean z2, final Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.flashModeButton = new ImageView[2];
        this.cameraViewLocation = new float[2];
        this.viewPosition = new int[2];
        this.animateCameraValues = new int[5];
        this.interpolator = new DecelerateInterpolator(1.5f);
        this.isCameraFrontfaceBeforeEnteringEditMode = null;
        this.hitRect = new android.graphics.Rect();
        int dp = AndroidUtilities.dp(80.0f);
        this.itemSize = dp;
        this.lastItemSize = dp;
        this.itemsPerRow = 3;
        this.loading = true;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.photoViewerProvider = new AnonymousClass1();
        this.currentItemTop = 0;
        this.forceDarkTheme = z;
        this.needCamera = z2;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.cameraInitied);
        BottomSheet.ContainerView container = chatAttachAlert.getContainer();
        this.showAvatarConstructor = this.parentAlert.avatarPicker != 0;
        this.cameraDrawable = context.getResources().getDrawable(R.drawable.instant_camera).mutate();
        int i = 0;
        Object[] objArr = 0;
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, this.parentAlert.actionBar.createMenu(), 0, 0, resourcesProvider) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setText(ChatAttachAlertPhotoLayout.this.dropDown.getText());
            }
        };
        this.dropDownContainer = actionBarMenuItem;
        actionBarMenuItem.setSubMenuOpenSide(1);
        this.parentAlert.actionBar.addView(this.dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.dropDownContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.lambda$new$1(view);
            }
        });
        TextView textView = new TextView(context);
        this.dropDown = textView;
        textView.setImportantForAccessibility(2);
        this.dropDown.setGravity(3);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(1);
        this.dropDown.setMaxLines(1);
        this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView2 = this.dropDown;
        int i2 = Theme.key_dialogTextBlack;
        textView2.setTextColor(getThemedColor(i2));
        this.dropDown.setText(LocaleController.getString(R.string.ChatGallery));
        this.dropDown.setTypeface(AndroidUtilities.bold());
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_arrow_drop_down).mutate();
        this.dropDownDrawable = mutate;
        int themedColor = getThemedColor(i2);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        mutate.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        checkCamera(false);
        MessagePreviewView.ToggleButton toggleButton = new MessagePreviewView.ToggleButton(context, R.raw.position_below, LocaleController.getString(R.string.CaptionAbove), R.raw.position_above, LocaleController.getString(R.string.CaptionBelow), resourcesProvider);
        this.captionItem = toggleButton;
        toggleButton.setState(!this.parentAlert.captionAbove, false);
        this.previewItem = this.parentAlert.selectedMenuItem.addSubItem(6, R.drawable.msg_view_file, LocaleController.getString(R.string.AttachMediaPreviewButton));
        this.parentAlert.selectedMenuItem.addColoredGap(4);
        this.parentAlert.selectedMenuItem.addSubItem(3, R.drawable.msg_openin, LocaleController.getString(R.string.OpenInExternalApp));
        this.compressItem = this.parentAlert.selectedMenuItem.addSubItem(1, R.drawable.msg_filehq, LocaleController.getString(R.string.SendWithoutCompression));
        this.parentAlert.selectedMenuItem.addSubItem(0, R.drawable.msg_ungroup, LocaleController.getString(R.string.SendWithoutGrouping));
        this.parentAlert.selectedMenuItem.addColoredGap(5);
        this.spoilerItem = this.parentAlert.selectedMenuItem.addSubItem(2, R.drawable.msg_spoiler, LocaleController.getString(R.string.EnablePhotoSpoiler));
        this.parentAlert.selectedMenuItem.addSubItem(7, this.captionItem);
        this.starsItem = this.parentAlert.selectedMenuItem.addSubItem(8, R.drawable.menu_feature_paid, LocaleController.getString(R.string.PaidMediaButton));
        this.parentAlert.selectedMenuItem.setFitSubItems(true);
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - AndroidUtilities.dp(80.0f)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                return false;
            }

            @Override
            public void onLayout(boolean z3, int i3, int i4, int i5, int i6) {
                super.onLayout(z3, i3, i4, i5, i6);
                PhotoViewer.getInstance().checkCurrentImageVisibility();
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - AndroidUtilities.dp(80.0f)) {
                    return super.onTouchEvent(motionEvent);
                }
                return false;
            }
        };
        this.gridView = recyclerListView;
        recyclerListView.setFastScrollEnabled(1);
        this.gridView.setFastScrollVisible(true);
        this.gridView.getFastScroll().setAlpha(0.0f);
        this.gridView.getFastScroll().usePadding = false;
        RecyclerListView recyclerListView2 = this.gridView;
        PhotoAttachAdapter photoAttachAdapter = new PhotoAttachAdapter(context, z2);
        this.adapter = photoAttachAdapter;
        recyclerListView2.setAdapter(photoAttachAdapter);
        this.adapter.createCache();
        this.gridView.setClipToPadding(false);
        this.gridView.setItemAnimator(null);
        this.gridView.setLayoutAnimation(null);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f));
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean parentPinnedToTop;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                RecyclerListView.Holder holder;
                if (i3 == 0) {
                    int dp2 = AndroidUtilities.dp(13.0f);
                    ActionBarMenuItem actionBarMenuItem2 = ChatAttachAlertPhotoLayout.this.parentAlert.selectedMenuItem;
                    int dp3 = dp2 + (actionBarMenuItem2 != null ? AndroidUtilities.dp(actionBarMenuItem2.getAlpha() * 26.0f) : 0);
                    int backgroundPaddingTop = ChatAttachAlertPhotoLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp3) + backgroundPaddingTop >= ActionBar.getCurrentActionBarHeight() || (holder = (RecyclerListView.Holder) ChatAttachAlertPhotoLayout.this.gridView.findViewHolderForAdapterPosition(0)) == null || holder.itemView.getTop() <= AndroidUtilities.dp(7.0f)) {
                        return;
                    }
                    ChatAttachAlertPhotoLayout.this.gridView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                if (ChatAttachAlertPhotoLayout.this.gridView.getChildCount() <= 0) {
                    return;
                }
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                chatAttachAlertPhotoLayout.parentAlert.updateLayout(chatAttachAlertPhotoLayout, true, i4);
                if (ChatAttachAlertPhotoLayout.this.adapter.getTotalItemsCount() > 30) {
                    boolean z3 = this.parentPinnedToTop;
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                    boolean z4 = chatAttachAlertPhotoLayout2.parentAlert.pinnedToTop;
                    if (z3 != z4) {
                        this.parentPinnedToTop = z4;
                        chatAttachAlertPhotoLayout2.gridView.getFastScroll().animate().alpha(this.parentPinnedToTop ? 1.0f : 0.0f).setDuration(100L).start();
                    }
                } else {
                    ChatAttachAlertPhotoLayout.this.gridView.getFastScroll().setAlpha(0.0f);
                }
                if (i4 != 0) {
                    ChatAttachAlertPhotoLayout.this.checkCameraViewPosition();
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, this.itemSize) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i3) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public int calculateDyToMakeVisible(View view, int i4) {
                        return super.calculateDyToMakeVisible(view, i4) - (ChatAttachAlertPhotoLayout.this.gridView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    @Override
                    public int calculateTimeForDeceleration(int i4) {
                        return super.calculateTimeForDeceleration(i4) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(i3);
                startSmoothScroll(linearSmoothScroller);
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = gridLayoutManager;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i3) {
                if (i3 == ChatAttachAlertPhotoLayout.this.adapter.itemsCount - 1) {
                    return ChatAttachAlertPhotoLayout.this.layoutManager.getSpanCount();
                }
                return ChatAttachAlertPhotoLayout.this.itemSize + (i3 % ChatAttachAlertPhotoLayout.this.itemsPerRow != ChatAttachAlertPhotoLayout.this.itemsPerRow + (-1) ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.gridView.setLayoutManager(this.layoutManager);
        this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i3) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i3);
            }

            @Override
            public void onDoubleTap(View view, int i3, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i3, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i3, float f, float f2) {
                ChatAttachAlertPhotoLayout.this.lambda$new$3(z2, resourcesProvider, view, i3, f, f2);
            }
        });
        this.gridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i3) {
                boolean lambda$new$4;
                lambda$new$4 = ChatAttachAlertPhotoLayout.this.lambda$new$4(view, i3);
                return lambda$new$4;
            }
        });
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() {
            @Override
            public boolean isIndexSelectable(int i3) {
                return ChatAttachAlertPhotoLayout.this.adapter.getItemViewType(i3) == 0;
            }

            @Override
            public boolean isSelected(int i3) {
                MediaController.PhotoEntry photo = ChatAttachAlertPhotoLayout.this.adapter.getPhoto(i3);
                return photo != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photo.imageId));
            }

            @Override
            public void onStartStopSelection(boolean z3) {
                ChatAttachAlertPhotoLayout.this.alertOnlyOnce = z3 ? 1 : 0;
                ChatAttachAlertPhotoLayout.this.gridView.hideSelector(true);
            }

            @Override
            public void setSelected(View view, int i3, boolean z3) {
                if (z3 == ChatAttachAlertPhotoLayout.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        this.gridView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString(R.string.NoPhotos));
        this.progressView.setOnTouchListener(null);
        this.progressView.setTextSize(16);
        addView(this.progressView, LayoutHelper.createFrame(-1, -2.0f));
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        final Paint paint = new Paint(1);
        paint.setColor(-2468275);
        TextView textView3 = new TextView(context) {
            float alpha = 0.0f;
            boolean isIncr;

            @Override
            protected void onDraw(Canvas canvas) {
                boolean z3;
                paint.setAlpha((int) ((this.alpha * 130.0f) + 125.0f));
                if (this.isIncr) {
                    float f = this.alpha + 0.026666667f;
                    this.alpha = f;
                    if (f >= 1.0f) {
                        this.alpha = 1.0f;
                        z3 = false;
                        this.isIncr = z3;
                    }
                } else {
                    float f2 = this.alpha - 0.026666667f;
                    this.alpha = f2;
                    if (f2 <= 0.0f) {
                        this.alpha = 0.0f;
                        z3 = true;
                        this.isIncr = z3;
                    }
                }
                super.onDraw(canvas);
                canvas.drawCircle(AndroidUtilities.dp(14.0f), getMeasuredHeight() / 2, AndroidUtilities.dp(4.0f), paint);
                invalidate();
            }
        };
        this.recordTime = textView3;
        AndroidUtilities.updateViewVisibilityAnimated(textView3, false, 1.0f, false);
        this.recordTime.setBackgroundResource(R.drawable.system);
        this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(1711276032, mode));
        this.recordTime.setTextSize(1, 15.0f);
        this.recordTime.setTypeface(AndroidUtilities.bold());
        this.recordTime.setAlpha(0.0f);
        this.recordTime.setTextColor(-1);
        this.recordTime.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean z3, int i3, int i4, int i5, int i6) {
                int measuredWidth;
                int measuredHeight;
                int dp2;
                int measuredHeight2;
                int i7;
                int i8;
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    measuredWidth = getMeasuredWidth() / 2;
                    measuredHeight = getMeasuredHeight() / 2;
                    i7 = getMeasuredWidth() / 2;
                    int i9 = measuredHeight / 2;
                    i8 = measuredHeight + i9 + AndroidUtilities.dp(17.0f);
                    measuredHeight2 = i9 - AndroidUtilities.dp(17.0f);
                    dp2 = i7;
                } else {
                    measuredWidth = getMeasuredWidth() / 2;
                    measuredHeight = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    int i10 = measuredWidth / 2;
                    int dp3 = measuredWidth + i10 + AndroidUtilities.dp(17.0f);
                    dp2 = i10 - AndroidUtilities.dp(17.0f);
                    measuredHeight2 = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    i7 = dp3;
                    i8 = measuredHeight2;
                }
                int measuredHeight3 = (getMeasuredHeight() - ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight()) - AndroidUtilities.dp(12.0f);
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.layout(measuredWidth - (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2), getMeasuredHeight(), (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2) + measuredWidth, getMeasuredHeight() + ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight());
                } else {
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.layout(measuredWidth - (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2), measuredHeight3, (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2) + measuredWidth, ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight() + measuredHeight3);
                }
                ChatAttachAlertPhotoLayout.this.shutterButton.layout(measuredWidth - (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredWidth() / 2), measuredHeight - (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredHeight() / 2), measuredWidth + (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredWidth() / 2), measuredHeight + (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredHeight() / 2));
                ChatAttachAlertPhotoLayout.this.switchCameraButton.layout(i7 - (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredWidth() / 2), i8 - (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredHeight() / 2), i7 + (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredWidth() / 2), i8 + (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredHeight() / 2));
                for (int i11 = 0; i11 < 2; i11++) {
                    ChatAttachAlertPhotoLayout.this.flashModeButton[i11].layout(dp2 - (ChatAttachAlertPhotoLayout.this.flashModeButton[i11].getMeasuredWidth() / 2), measuredHeight2 - (ChatAttachAlertPhotoLayout.this.flashModeButton[i11].getMeasuredHeight() / 2), (ChatAttachAlertPhotoLayout.this.flashModeButton[i11].getMeasuredWidth() / 2) + dp2, (ChatAttachAlertPhotoLayout.this.flashModeButton[i11].getMeasuredHeight() / 2) + measuredHeight2);
                }
            }
        };
        this.cameraPanel = frameLayout;
        frameLayout.setVisibility(8);
        this.cameraPanel.setAlpha(0.0f);
        container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 126, 83));
        TextView textView4 = new TextView(context);
        this.counterTextView = textView4;
        textView4.setBackgroundResource(R.drawable.photos_rounded);
        this.counterTextView.setVisibility(8);
        this.counterTextView.setTextColor(-1);
        this.counterTextView.setGravity(17);
        this.counterTextView.setPivotX(0.0f);
        this.counterTextView.setPivotY(0.0f);
        this.counterTextView.setTypeface(AndroidUtilities.bold());
        this.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.photos_arrow, 0);
        this.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        container.addView(this.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.counterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.lambda$new$5(view);
            }
        });
        ZoomControlView zoomControlView = new ZoomControlView(context);
        this.zoomControlView = zoomControlView;
        zoomControlView.setVisibility(8);
        this.zoomControlView.setAlpha(0.0f);
        container.addView(this.zoomControlView, LayoutHelper.createFrame(-2, 50.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.zoomControlView.setDelegate(new ZoomControlView.ZoomControlViewDelegate() {
            @Override
            public final void didSetZoom(float f) {
                ChatAttachAlertPhotoLayout.this.lambda$new$6(f);
            }
        });
        ShutterButton shutterButton = new ShutterButton(context);
        this.shutterButton = shutterButton;
        this.cameraPanel.addView(shutterButton, LayoutHelper.createFrame(84, 84, 17));
        this.shutterButton.setDelegate(new AnonymousClass10(resourcesProvider, container));
        this.shutterButton.setFocusable(true);
        this.shutterButton.setContentDescription(LocaleController.getString(R.string.AccDescrShutter));
        ImageView imageView = new ImageView(context);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.lambda$new$7(view);
            }
        });
        this.switchCameraButton.setContentDescription(LocaleController.getString(R.string.AccDescrSwitchCamera));
        for (int i3 = 0; i3 < 2; i3++) {
            this.flashModeButton[i3] = new ImageView(context);
            this.flashModeButton[i3].setScaleType(ImageView.ScaleType.CENTER);
            this.flashModeButton[i3].setVisibility(4);
            this.cameraPanel.addView(this.flashModeButton[i3], LayoutHelper.createFrame(48, 48, 51));
            this.flashModeButton[i3].setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ChatAttachAlertPhotoLayout.this.lambda$new$8(view);
                }
            });
            this.flashModeButton[i3].setContentDescription("flash mode " + i3);
        }
        TextView textView5 = new TextView(context);
        this.tooltipTextView = textView5;
        textView5.setTextSize(1, 15.0f);
        this.tooltipTextView.setTextColor(-1);
        this.tooltipTextView.setText(LocaleController.getString(R.string.TapForVideo));
        this.tooltipTextView.setShadowLayer(AndroidUtilities.dp(3.33333f), 0.0f, AndroidUtilities.dp(0.666f), 1275068416);
        this.tooltipTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.cameraPanel.addView(this.tooltipTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
        RecyclerListView recyclerListView3 = new RecyclerListView(context, resourcesProvider) {
            @Override
            public void requestLayout() {
                if (ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.cameraPhotoRecyclerView = recyclerListView3;
        recyclerListView3.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView4 = this.cameraPhotoRecyclerView;
        PhotoAttachAdapter photoAttachAdapter2 = new PhotoAttachAdapter(context, false);
        this.cameraAttachAdapter = photoAttachAdapter2;
        recyclerListView4.setAdapter(photoAttachAdapter2);
        this.cameraAttachAdapter.createCache();
        this.cameraPhotoRecyclerView.setClipToPadding(false);
        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.cameraPhotoRecyclerView.setItemAnimator(null);
        this.cameraPhotoRecyclerView.setLayoutAnimation(null);
        this.cameraPhotoRecyclerView.setOverScrollMode(2);
        this.cameraPhotoRecyclerView.setVisibility(4);
        this.cameraPhotoRecyclerView.setAlpha(0.0f);
        container.addView(this.cameraPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, i, objArr == true ? 1 : 0) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.cameraPhotoLayoutManager = linearLayoutManager;
        this.cameraPhotoRecyclerView.setLayoutManager(linearLayoutManager);
        this.cameraPhotoRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i4) {
                ChatAttachAlertPhotoLayout.lambda$new$9(view, i4);
            }
        });
    }

    static int access$2908(ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout) {
        int i = chatAttachAlertPhotoLayout.videoRecordTime;
        chatAttachAlertPhotoLayout.videoRecordTime = i + 1;
        return i;
    }

    public int addToSelectedPhotos(MediaController.PhotoEntry photoEntry, int i) {
        Integer valueOf = Integer.valueOf(photoEntry.imageId);
        if (selectedPhotos.containsKey(valueOf)) {
            photoEntry.starsAmount = 0L;
            photoEntry.hasSpoiler = false;
            selectedPhotos.remove(valueOf);
            int indexOf = selectedPhotosOrder.indexOf(valueOf);
            if (indexOf >= 0) {
                selectedPhotosOrder.remove(indexOf);
            }
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            if (i >= 0) {
                photoEntry.reset();
                this.photoViewerProvider.updatePhotoAtIndex(i);
            }
            return indexOf;
        }
        photoEntry.starsAmount = getStarsPrice();
        photoEntry.hasSpoiler = getStarsPrice() > 0;
        photoEntry.isChatPreviewSpoilerRevealed = false;
        photoEntry.isAttachSpoilerRevealed = false;
        boolean checkSelectedCount = checkSelectedCount(true);
        selectedPhotos.put(valueOf, photoEntry);
        selectedPhotosOrder.add(valueOf);
        if (checkSelectedCount) {
            updateCheckedPhotos();
            return -1;
        }
        updatePhotosCounter(true);
        return -1;
    }

    private void applyCameraViewPosition() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            if (!this.cameraOpened) {
                cameraView.setTranslationX(this.cameraViewLocation[0]);
                this.cameraView.setTranslationY(this.cameraViewLocation[1] + this.currentPanTranslationY);
            }
            this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY + this.currentPanTranslationY);
            int i = this.itemSize;
            if (!this.cameraOpened) {
                this.cameraView.setClipTop((int) this.cameraViewOffsetY);
                this.cameraView.setClipBottom((int) this.cameraViewOffsetBottomY);
                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (layoutParams.height != i || layoutParams.width != i) {
                    layoutParams.width = i;
                    layoutParams.height = i;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            ChatAttachAlertPhotoLayout.this.lambda$applyCameraViewPosition$17(layoutParams);
                        }
                    });
                }
            }
            float f = this.itemSize;
            int i2 = (int) (f - this.cameraViewOffsetX);
            int i3 = (int) ((f - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY);
            final FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams2.height == i3 && layoutParams2.width == i2) {
                return;
            }
            layoutParams2.width = i2;
            layoutParams2.height = i3;
            this.cameraIcon.setLayoutParams(layoutParams2);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$applyCameraViewPosition$18(layoutParams2);
                }
            });
        }
    }

    private boolean checkSelectedCount(boolean z) {
        if (getStarsPrice() <= 0) {
            return false;
        }
        boolean z2 = false;
        while (selectedPhotos.size() > 10 - (z ? 1 : 0) && !selectedPhotosOrder.isEmpty()) {
            Object obj = selectedPhotos.get(selectedPhotosOrder.get(0));
            if (!(obj instanceof MediaController.PhotoEntry)) {
                break;
            }
            addToSelectedPhotos((MediaController.PhotoEntry) obj, -1);
            z2 = true;
        }
        return z2;
    }

    public boolean checkSendMediaEnabled(MediaController.PhotoEntry photoEntry) {
        BulletinFactory of;
        int i;
        if (this.videoEnabled || !photoEntry.isVideo) {
            if (this.photoEnabled || photoEntry.isVideo) {
                return false;
            }
            if (this.parentAlert.checkCanRemoveRestrictionsByBoosts()) {
                return true;
            }
            of = BulletinFactory.of(this.parentAlert.sizeNotifierFrameLayout, this.resourcesProvider);
            i = R.string.GlobalAttachPhotoRestricted;
        } else {
            if (this.parentAlert.checkCanRemoveRestrictionsByBoosts()) {
                return true;
            }
            of = BulletinFactory.of(this.parentAlert.sizeNotifierFrameLayout, this.resourcesProvider);
            i = R.string.GlobalAttachVideoRestricted;
        }
        of.createErrorBulletin(LocaleController.getString(i)).show();
        return true;
    }

    public PhotoAttachPhotoCell getCellForIndex(int i) {
        int childCount = this.gridView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.gridView.getChildAt(i2);
            if (childAt.getTop() < this.gridView.getMeasuredHeight() - this.parentAlert.getClipLayoutBottom() && (childAt instanceof PhotoAttachPhotoCell)) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                if (photoAttachPhotoCell.getImageView().getTag() != null && ((Integer) photoAttachPhotoCell.getImageView().getTag()).intValue() == i) {
                    return photoAttachPhotoCell;
                }
            }
        }
        return null;
    }

    public MediaController.PhotoEntry getPhotoEntryAtPosition(int i) {
        ArrayList<MediaController.PhotoEntry> arrayList;
        if (i < 0) {
            return null;
        }
        int size = cameraPhotos.size();
        if (i < size) {
            arrayList = cameraPhotos;
        } else {
            i -= size;
            MediaController.AlbumEntry albumEntry = this.selectedAlbumEntry;
            if (albumEntry == null || i >= albumEntry.photos.size()) {
                return null;
            }
            arrayList = this.selectedAlbumEntry.photos;
        }
        return arrayList.get(i);
    }

    private boolean isNoGalleryPermissions() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.isNoGalleryPermissions():boolean");
    }

    public void lambda$applyCameraViewPosition$17(FrameLayout.LayoutParams layoutParams) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setLayoutParams(layoutParams);
        }
    }

    public void lambda$applyCameraViewPosition$18(FrameLayout.LayoutParams layoutParams) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParams);
        }
    }

    public void lambda$hideCamera$16() {
        this.parentAlert.getContainer().removeView(this.cameraView);
        this.parentAlert.getContainer().removeView(this.cameraIcon);
        this.cameraView = null;
        this.cameraIcon = null;
    }

    public void lambda$new$1(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public void lambda$new$2(int i, BaseFragment baseFragment, ArrayList arrayList, int i2, ChatActivity chatActivity) {
        int i3;
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (!chatAttachAlert.isPhotoPicker || chatAttachAlert.isStickerMode) {
            i3 = i;
        } else {
            PhotoViewer.getInstance().setParentActivity(baseFragment);
            PhotoViewer.getInstance().setMaxSelectedPhotos(0, false);
            i3 = 3;
        }
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, i2, i3, false, this.photoViewerProvider, chatActivity);
        PhotoViewer.getInstance().setAvatarFor(this.parentAlert.getAvatarFor());
        ChatAttachAlert chatAttachAlert2 = this.parentAlert;
        if (chatAttachAlert2.isPhotoPicker && !chatAttachAlert2.isStickerMode) {
            PhotoViewer.getInstance().closePhotoAfterSelect = false;
        }
        if (this.parentAlert.isStickerMode) {
            PhotoViewer.getInstance().enableStickerMode(null, false, this.parentAlert.customStickerHandler);
        }
        if (captionForAllMedia()) {
            PhotoViewer.getInstance().setCaption(this.parentAlert.getCommentTextView().getText());
        }
    }

    public void lambda$new$3(boolean z, Theme.ResourcesProvider resourcesProvider, View view, int i, float f, float f2) {
        final ChatActivity chatActivity;
        final int i2;
        if (this.mediaEnabled) {
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            if (chatAttachAlert.destroyed) {
                return;
            }
            BaseFragment baseFragment = chatAttachAlert.baseFragment;
            if (baseFragment == null) {
                baseFragment = LaunchActivity.getLastFragment();
            }
            final BaseFragment baseFragment2 = baseFragment;
            if (baseFragment2 == null) {
                return;
            }
            int i3 = Build.VERSION.SDK_INT;
            if (i3 >= 23) {
                if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry && i == 0 && this.noCameraPermissions) {
                    try {
                        baseFragment2.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                } else if (this.noGalleryPermissions) {
                    try {
                        if (i3 >= 33) {
                            baseFragment2.getParentActivity().requestPermissions(new String[]{"android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_IMAGES"}, 4);
                        } else {
                            baseFragment2.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        }
                        return;
                    } catch (Exception unused2) {
                        return;
                    }
                }
            }
            if (i == 0 && z && this.selectedAlbumEntry == this.galleryAlbumEntry) {
                if (SharedConfig.inappCamera) {
                    openCamera(true);
                    return;
                }
                ChatAttachAlert.ChatAttachViewDelegate chatAttachViewDelegate = this.parentAlert.delegate;
                if (chatAttachViewDelegate != null) {
                    chatAttachViewDelegate.didPressedButton(0, false, true, 0, 0L, false, false);
                    return;
                }
                return;
            }
            if (this.selectedAlbumEntry == this.galleryAlbumEntry && z) {
                i--;
            }
            if (this.showAvatarConstructor) {
                if (i == 0) {
                    if (!(view instanceof AvatarConstructorPreviewCell)) {
                        return;
                    }
                    showAvatarConstructorFragment((AvatarConstructorPreviewCell) view, null);
                    this.parentAlert.dismiss();
                }
                i--;
            }
            final int i4 = i;
            final ArrayList<Object> allPhotosArray = getAllPhotosArray();
            if (i4 < 0 || i4 >= allPhotosArray.size()) {
                return;
            }
            ChatAttachAlert.ChatAttachViewDelegate chatAttachViewDelegate2 = this.parentAlert.delegate;
            if (chatAttachViewDelegate2 != null && chatAttachViewDelegate2.selectItemOnClicking() && (allPhotosArray.get(i4) instanceof MediaController.PhotoEntry)) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) allPhotosArray.get(i4);
                selectedPhotos.clear();
                if (photoEntry != null) {
                    addToSelectedPhotos(photoEntry, -1);
                }
                this.parentAlert.applyCaption();
                this.parentAlert.delegate.didPressedButton(7, true, true, 0, 0L, false, false);
                selectedPhotos.clear();
                cameraPhotos.clear();
                selectedPhotosOrder.clear();
                selectedPhotos.clear();
                return;
            }
            PhotoViewer.getInstance().setParentActivity(baseFragment2, resourcesProvider);
            PhotoViewer.getInstance().setParentAlert(this.parentAlert);
            PhotoViewer photoViewer = PhotoViewer.getInstance();
            ChatAttachAlert chatAttachAlert2 = this.parentAlert;
            photoViewer.setMaxSelectedPhotos(chatAttachAlert2.maxSelectedPhotos, chatAttachAlert2.allowOrder);
            ChatAttachAlert chatAttachAlert3 = this.parentAlert;
            if (chatAttachAlert3.isPhotoPicker && chatAttachAlert3.isStickerMode) {
                BaseFragment baseFragment3 = chatAttachAlert3.baseFragment;
                chatActivity = baseFragment3 instanceof ChatActivity ? (ChatActivity) baseFragment3 : null;
                i2 = 11;
            } else if (chatAttachAlert3.avatarPicker != 0) {
                chatActivity = null;
                i2 = 1;
            } else {
                BaseFragment baseFragment4 = chatAttachAlert3.baseFragment;
                if (baseFragment4 instanceof ChatActivity) {
                    chatActivity = (ChatActivity) baseFragment4;
                } else {
                    chatActivity = null;
                    if (!chatAttachAlert3.allowEnterCaption) {
                        i2 = 4;
                    }
                }
                i2 = 0;
            }
            if (!chatAttachAlert3.delegate.needEnterComment()) {
                AndroidUtilities.hideKeyboard(baseFragment2.getFragmentView().findFocus());
                AndroidUtilities.hideKeyboard(this.parentAlert.getContainer().findFocus());
            }
            if (selectedPhotos.size() > 0 && selectedPhotosOrder.size() > 0) {
                Object obj = selectedPhotos.get(selectedPhotosOrder.get(0));
                if (obj instanceof MediaController.PhotoEntry) {
                    ((MediaController.PhotoEntry) obj).caption = this.parentAlert.getCommentTextView().getText();
                }
                if (obj instanceof MediaController.SearchImage) {
                    ((MediaController.SearchImage) obj).caption = this.parentAlert.getCommentTextView().getText();
                }
            }
            if (this.parentAlert.getAvatarFor() != null) {
                this.parentAlert.getAvatarFor().isVideo = allPhotosArray.get(i4) instanceof MediaController.PhotoEntry ? ((MediaController.PhotoEntry) allPhotosArray.get(i4)).isVideo : false;
            }
            boolean z2 = (allPhotosArray.get(i4) instanceof MediaController.PhotoEntry) && ((MediaController.PhotoEntry) allPhotosArray.get(i4)).hasSpoiler;
            Object obj2 = allPhotosArray.get(i4);
            if ((obj2 instanceof MediaController.PhotoEntry) && checkSendMediaEnabled((MediaController.PhotoEntry) obj2)) {
                return;
            }
            if (z2) {
                setCurrentSpoilerVisible(i4, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$new$2(i2, baseFragment2, allPhotosArray, i4, chatActivity);
                }
            }, z2 ? 250L : 0L);
        }
    }

    public boolean lambda$new$4(View view, int i) {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert.storyMediaPicker) {
            return false;
        }
        if (i == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
            ChatAttachAlert.ChatAttachViewDelegate chatAttachViewDelegate = chatAttachAlert.delegate;
            if (chatAttachViewDelegate != null) {
                chatAttachViewDelegate.didPressedButton(0, false, true, 0, 0L, false, false);
            }
            return true;
        }
        if (view instanceof PhotoAttachPhotoCell) {
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !((PhotoAttachPhotoCell) view).isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, i, z);
        }
        return false;
    }

    public void lambda$new$5(View view) {
        if (this.cameraView == null) {
            return;
        }
        openPhotoViewer(null, false, false);
        CameraController.getInstance().stopPreview(this.cameraView.getCameraSessionObject());
    }

    public void lambda$new$6(float f) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            this.cameraZoom = f;
            cameraView.setZoom(f);
        }
        showZoomControls(true, true);
    }

    public void lambda$new$7(View view) {
        CameraView cameraView;
        if (this.takingPhoto || (cameraView = this.cameraView) == null || !cameraView.isInited()) {
            return;
        }
        this.canSaveCameraPreview = false;
        this.cameraView.switchCamera();
        ObjectAnimator duration = ObjectAnimator.ofFloat(this.switchCameraButton, (Property<ImageView, Float>) View.SCALE_X, 0.0f).setDuration(100L);
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ImageView imageView = ChatAttachAlertPhotoLayout.this.switchCameraButton;
                CameraView cameraView2 = ChatAttachAlertPhotoLayout.this.cameraView;
                imageView.setImageResource((cameraView2 == null || !cameraView2.isFrontface()) ? R.drawable.camera_revert2 : R.drawable.camera_revert1);
                ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.switchCameraButton, (Property<ImageView, Float>) View.SCALE_X, 1.0f).setDuration(100L).start();
            }
        });
        duration.start();
    }

    public void lambda$new$8(final View view) {
        CameraView cameraView;
        if (this.flashAnimationInProgress || (cameraView = this.cameraView) == null || !cameraView.isInited() || !this.cameraOpened) {
            return;
        }
        String currentFlashMode = this.cameraView.getCameraSession().getCurrentFlashMode();
        String nextFlashMode = this.cameraView.getCameraSession().getNextFlashMode();
        if (currentFlashMode.equals(nextFlashMode)) {
            return;
        }
        this.cameraView.getCameraSession().setCurrentFlashMode(nextFlashMode);
        this.flashAnimationInProgress = true;
        ImageView[] imageViewArr = this.flashModeButton;
        final ImageView imageView = imageViewArr[0];
        if (imageView == view) {
            imageView = imageViewArr[1];
        }
        imageView.setVisibility(0);
        setCameraFlashModeIcon(imageView, nextFlashMode);
        AnimatorSet animatorSet = new AnimatorSet();
        Property property = View.TRANSLATION_Y;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, (Property<View, Float>) property, 0.0f, AndroidUtilities.dp(48.0f));
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(imageView, (Property<ImageView, Float>) property, -AndroidUtilities.dp(48.0f), 0.0f);
        Property property2 = View.ALPHA;
        animatorSet.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(view, (Property<View, Float>) property2, 1.0f, 0.0f), ObjectAnimator.ofFloat(imageView, (Property<ImageView, Float>) property2, 0.0f, 1.0f));
        animatorSet.setDuration(220L);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ChatAttachAlertPhotoLayout.this.flashAnimationInProgress = false;
                view.setVisibility(4);
                imageView.sendAccessibilityEvent(8);
            }
        });
        animatorSet.start();
    }

    public static void lambda$new$9(View view, int i) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    public void lambda$onHide$25() {
        this.dropDownContainer.setVisibility(8);
    }

    public void lambda$onMenuItemClick$19(boolean z, int i) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(7, false, z, i, 0L, false, false);
    }

    public void lambda$onMenuItemClick$20(boolean z, int i) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(4, true, z, i, 0L, false, false);
    }

    public void lambda$onMenuItemClick$21(boolean z) {
        this.spoilerItem.setText(LocaleController.getString(z ? R.string.DisablePhotoSpoiler : R.string.EnablePhotoSpoiler));
        ActionBarMenuSubItem actionBarMenuSubItem = this.spoilerItem;
        if (z) {
            actionBarMenuSubItem.setIcon(R.drawable.msg_spoiler_off);
        } else {
            actionBarMenuSubItem.setAnimatedIcon(R.raw.photo_spoiler);
        }
        if (z) {
            this.parentAlert.selectedMenuItem.hideSubItem(1);
            if (getSelectedItemsCount() <= 1) {
                this.parentAlert.selectedMenuItem.hideSubItem(5);
                return;
            }
            return;
        }
        this.parentAlert.selectedMenuItem.showSubItem(1);
        if (getSelectedItemsCount() <= 1) {
            this.parentAlert.selectedMenuItem.showSubItem(5);
        }
    }

    public static void lambda$onMenuItemClick$22(List list, boolean z, View view) {
        if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
            MediaController.PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
            photoAttachPhotoCell.setHasSpoiler(photoEntry != null && list.contains(Integer.valueOf(photoEntry.imageId)) && z);
        }
    }

    public void lambda$onMenuItemClick$23(Long l, Runnable runnable) {
        runnable.run();
        setStarsPrice(l.longValue());
    }

    public void lambda$onPreMeasure$26() {
        this.adapter.notifyDataSetChanged();
    }

    public void lambda$onShow$24(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        int currentItemTop = attachAlertLayout.getCurrentItemTop();
        int listTopPadding = attachAlertLayout.getListTopPadding();
        RecyclerListView recyclerListView = this.gridView;
        if (currentItemTop > AndroidUtilities.dp(8.0f)) {
            listTopPadding -= currentItemTop;
        }
        recyclerListView.scrollBy(0, listTopPadding);
    }

    public void lambda$setCurrentSpoilerVisible$0(MediaController.PhotoEntry photoEntry, boolean z, View view) {
        if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
            if (photoAttachPhotoCell.getPhotoEntry() == photoEntry) {
                photoAttachPhotoCell.setHasSpoiler(z, Float.valueOf(250.0f));
                photoAttachPhotoCell.setStarsPrice(getStarsPrice(), selectedPhotos.size() > 1);
            }
        }
    }

    public void lambda$showAvatarConstructorFragment$10(org.telegram.ui.Components.AvatarConstructorFragment r29, org.telegram.ui.Components.AvatarConstructorFragment.BackgroundGradient r30, long r31, org.telegram.tgnet.TLRPC.Document r33, org.telegram.ui.Components.AvatarConstructorFragment.PreviewView r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.lambda$showAvatarConstructorFragment$10(org.telegram.ui.Components.AvatarConstructorFragment, org.telegram.ui.Components.AvatarConstructorFragment$BackgroundGradient, long, org.telegram.tgnet.TLRPC$Document, org.telegram.ui.Components.AvatarConstructorFragment$PreviewView):void");
    }

    public void lambda$showZoomControls$14() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    public void lambda$showZoomControls$15() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    public static int lambda$updateAlbumsDropDown$12(ArrayList arrayList, MediaController.AlbumEntry albumEntry, MediaController.AlbumEntry albumEntry2) {
        int indexOf;
        int indexOf2;
        int i = albumEntry.bucketId;
        if (i == 0 && albumEntry2.bucketId != 0) {
            return -1;
        }
        if ((i == 0 || albumEntry2.bucketId != 0) && (indexOf = arrayList.indexOf(albumEntry)) <= (indexOf2 = arrayList.indexOf(albumEntry2))) {
            return indexOf < indexOf2 ? -1 : 0;
        }
        return 1;
    }

    public void lambda$updateAlbumsDropDown$13(int i, View view) {
        this.parentAlert.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
        this.dropDownContainer.toggleSubMenu();
    }

    public static void lambda$updatePhotoStarsPrice$11(View view) {
        if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
            photoAttachPhotoCell.setHasSpoiler(photoAttachPhotoCell.getPhotoEntry() != null && photoAttachPhotoCell.getPhotoEntry().hasSpoiler, Float.valueOf(250.0f));
            photoAttachPhotoCell.setStarsPrice(photoAttachPhotoCell.getPhotoEntry() != null ? photoAttachPhotoCell.getPhotoEntry().starsAmount : 0L, selectedPhotos.size() > 1);
        }
    }

    public int maxCount() {
        BaseFragment baseFragment = this.parentAlert.baseFragment;
        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getChatMode() == 5) {
            return this.parentAlert.baseFragment.getMessagesController().quickReplyMessagesLimit - ((ChatActivity) this.parentAlert.baseFragment).messages.size();
        }
        return Integer.MAX_VALUE;
    }

    public void onPhotoEditModeChanged(boolean z) {
    }

    public void openCamera(boolean z) {
        CameraView cameraView;
        int i = 0;
        if (this.cameraView == null || this.cameraInitAnimation != null || this.parentAlert.isDismissed()) {
            return;
        }
        this.cameraView.initTexture();
        if (shouldLoadAllMedia()) {
            this.tooltipTextView.setVisibility(0);
        } else {
            this.tooltipTextView.setVisibility(8);
        }
        if (cameraPhotos.isEmpty()) {
            this.counterTextView.setVisibility(4);
            this.cameraPhotoRecyclerView.setVisibility(8);
        } else {
            this.counterTextView.setVisibility(0);
            this.cameraPhotoRecyclerView.setVisibility(0);
        }
        if (this.parentAlert.commentTextView.isKeyboardVisible() && isFocusable()) {
            this.parentAlert.commentTextView.closeKeyboard();
        }
        this.zoomControlView.setVisibility(0);
        this.zoomControlView.setAlpha(0.0f);
        this.cameraPanel.setVisibility(0);
        this.cameraPanel.setTag(null);
        int[] iArr = this.animateCameraValues;
        iArr[0] = 0;
        int i2 = this.itemSize;
        iArr[1] = i2;
        iArr[2] = i2;
        this.additionCloseCameraY = 0.0f;
        this.cameraExpanded = true;
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setFpsLimit(-1);
        }
        AndroidUtilities.hideKeyboard(this);
        AndroidUtilities.setLightNavigationBar(this.parentAlert.getWindow(), false);
        this.parentAlert.getWindow().addFlags(128);
        if (z) {
            setCameraOpenProgress(0.0f);
            this.cameraAnimationInProgress = true;
            this.notificationsLocker.lock();
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", 0.0f, 1.0f));
            FrameLayout frameLayout = this.cameraPanel;
            Property property = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, 1.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, (Property<TextView, Float>) property, 1.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, (Property<RecyclerListView, Float>) property, 1.0f));
            int i3 = 0;
            while (true) {
                if (i3 >= 2) {
                    break;
                }
                if (this.flashModeButton[i3].getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i3], (Property<ImageView, Float>) View.ALPHA, 1.0f));
                    break;
                }
                i3++;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(350L);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    CameraView cameraView3;
                    ChatAttachAlertPhotoLayout.this.notificationsLocker.unlock();
                    ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                    CameraView cameraView4 = ChatAttachAlertPhotoLayout.this.cameraView;
                    if (cameraView4 != null) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            cameraView4.invalidateOutline();
                        } else {
                            cameraView4.invalidate();
                        }
                    }
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                    if (chatAttachAlertPhotoLayout.cameraOpened) {
                        chatAttachAlertPhotoLayout.parentAlert.delegate.onCameraOpened();
                    }
                    if (Build.VERSION.SDK_INT < 21 || (cameraView3 = ChatAttachAlertPhotoLayout.this.cameraView) == null) {
                        return;
                    }
                    cameraView3.setSystemUiVisibility(1028);
                }
            });
            animatorSet.start();
        } else {
            setCameraOpenProgress(1.0f);
            this.cameraPanel.setAlpha(1.0f);
            this.counterTextView.setAlpha(1.0f);
            this.cameraPhotoRecyclerView.setAlpha(1.0f);
            while (true) {
                if (i >= 2) {
                    break;
                }
                if (this.flashModeButton[i].getVisibility() == 0) {
                    this.flashModeButton[i].setAlpha(1.0f);
                    break;
                }
                i++;
            }
            this.parentAlert.delegate.onCameraOpened();
            CameraView cameraView3 = this.cameraView;
            if (cameraView3 != null && Build.VERSION.SDK_INT >= 21) {
                cameraView3.setSystemUiVisibility(1028);
            }
        }
        this.cameraOpened = true;
        CameraView cameraView4 = this.cameraView;
        if (cameraView4 != null) {
            cameraView4.setImportantForAccessibility(2);
        }
        this.gridView.setImportantForAccessibility(4);
        if (LiteMode.isEnabled(98784) || (cameraView = this.cameraView) == null || !cameraView.isInited()) {
            return;
        }
        this.cameraView.showTexture(true, z);
    }

    public void pauseCameraPreview() {
        try {
            if (this.cameraView != null) {
                CameraController.getInstance().stopPreview(this.cameraView.getCameraSessionObject());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        CameraView cameraView;
        if (motionEvent == null) {
            return false;
        }
        if ((!this.pressed && motionEvent.getActionMasked() == 0) || motionEvent.getActionMasked() == 5) {
            this.zoomControlView.getHitRect(this.hitRect);
            if (this.zoomControlView.getTag() != null && this.hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            if (!this.takingPhoto && !this.dragging) {
                if (motionEvent.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot(motionEvent.getX(1) - motionEvent.getX(0), motionEvent.getY(1) - motionEvent.getY(0));
                    this.zooming = true;
                } else {
                    this.maybeStartDraging = true;
                    this.lastY = motionEvent.getY();
                    this.zooming = false;
                }
                this.zoomWas = false;
                this.pressed = true;
            }
        } else if (this.pressed) {
            if (motionEvent.getActionMasked() == 2) {
                if (this.zooming && motionEvent.getPointerCount() == 2 && !this.dragging) {
                    float hypot = (float) Math.hypot(motionEvent.getX(1) - motionEvent.getX(0), motionEvent.getY(1) - motionEvent.getY(0));
                    if (this.zoomWas) {
                        if (this.cameraView != null) {
                            float dp = (hypot - this.pinchStartDistance) / AndroidUtilities.dp(100.0f);
                            this.pinchStartDistance = hypot;
                            float f = this.cameraZoom + dp;
                            this.cameraZoom = f;
                            if (f < 0.0f) {
                                this.cameraZoom = 0.0f;
                            } else if (f > 1.0f) {
                                this.cameraZoom = 1.0f;
                            }
                            this.zoomControlView.setZoom(this.cameraZoom, false);
                            this.parentAlert.getSheetContainer().invalidate();
                            this.cameraView.setZoom(this.cameraZoom);
                            showZoomControls(true, true);
                        }
                    } else if (Math.abs(hypot - this.pinchStartDistance) >= AndroidUtilities.getPixelsInCM(0.4f, false)) {
                        this.pinchStartDistance = hypot;
                        this.zoomWas = true;
                    }
                } else {
                    float y = motionEvent.getY();
                    float f2 = y - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(f2) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && (cameraView = this.cameraView) != null) {
                        cameraView.setTranslationY(cameraView.getTranslationY() + f2);
                        this.lastY = y;
                        this.zoomControlView.setTag(null);
                        Runnable runnable = this.zoomControlHideRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                            this.zoomControlHideRunnable = null;
                        }
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(1);
                            AnimatorSet animatorSet = new AnimatorSet();
                            FrameLayout frameLayout = this.cameraPanel;
                            Property property = View.ALPHA;
                            animatorSet.playTogether(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.zoomControlView, (Property<ZoomControlView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.counterTextView, (Property<TextView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.flashModeButton[0], (Property<ImageView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.flashModeButton[1], (Property<ImageView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, (Property<RecyclerListView, Float>) property, 0.0f));
                            animatorSet.setDuration(220L);
                            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            animatorSet.start();
                        }
                    }
                }
            } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                this.pressed = false;
                this.zooming = false;
                if (this.dragging) {
                    this.dragging = false;
                    CameraView cameraView2 = this.cameraView;
                    if (cameraView2 != null) {
                        if (Math.abs(cameraView2.getTranslationY()) > this.cameraView.getMeasuredHeight() / 6.0f) {
                            closeCamera(true);
                        } else {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.cameraView, (Property<CameraView, Float>) View.TRANSLATION_Y, 0.0f);
                            FrameLayout frameLayout2 = this.cameraPanel;
                            Property property2 = View.ALPHA;
                            animatorSet2.playTogether(ofFloat, ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.counterTextView, (Property<TextView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.flashModeButton[0], (Property<ImageView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.flashModeButton[1], (Property<ImageView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, (Property<RecyclerListView, Float>) property2, 1.0f));
                            animatorSet2.setDuration(250L);
                            animatorSet2.setInterpolator(this.interpolator);
                            animatorSet2.start();
                            this.cameraPanel.setTag(null);
                        }
                    }
                } else {
                    CameraView cameraView3 = this.cameraView;
                    if (cameraView3 != null && !this.zoomWas) {
                        cameraView3.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (motionEvent.getRawX() - this.viewPosition[0]), (int) (motionEvent.getRawY() - this.viewPosition[1]));
                    }
                }
            }
        }
        return true;
    }

    public void resetRecordState() {
        if (this.parentAlert.destroyed) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            this.flashModeButton[i].animate().alpha(1.0f).translationX(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
        ViewPropertyAnimator duration = this.switchCameraButton.animate().alpha(1.0f).translationX(0.0f).setDuration(150L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        duration.setInterpolator(cubicBezierInterpolator).start();
        this.tooltipTextView.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        AndroidUtilities.updateViewVisibilityAnimated(this.recordTime, false);
        AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
        this.videoRecordRunnable = null;
        AndroidUtilities.unlockOrientation(AndroidUtilities.findActivity(getContext()));
    }

    public void resumeCameraPreview() {
        try {
            checkCamera(false);
            if (this.cameraView != null) {
                CameraController.getInstance().startPreview(this.cameraView.getCameraSessionObject());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void saveLastCameraBitmap() {
        if (this.canSaveCameraPreview) {
            try {
                Bitmap bitmap = this.cameraView.getTextureView().getBitmap();
                if (bitmap != null) {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.cameraView.getMatrix(), true);
                    bitmap.recycle();
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, 80, (int) (createBitmap.getHeight() / (createBitmap.getWidth() / 80.0f)), true);
                    if (createScaledBitmap != null) {
                        if (createScaledBitmap != createBitmap) {
                            createBitmap.recycle();
                        }
                        Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), createScaledBitmap.getHeight(), createScaledBitmap.getRowBytes());
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg"));
                        createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                        createScaledBitmap.recycle();
                        fileOutputStream.close();
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public void setCameraFlashModeIcon(ImageView imageView, String str) {
        int i;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 3551:
                if (str.equals("on")) {
                    c = 0;
                    break;
                }
                break;
            case 109935:
                if (str.equals("off")) {
                    c = 1;
                    break;
                }
                break;
            case 3005871:
                if (str.equals("auto")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                imageView.setImageResource(R.drawable.flash_on);
                i = R.string.AccDescrCameraFlashOn;
                imageView.setContentDescription(LocaleController.getString(i));
                return;
            case 1:
                imageView.setImageResource(R.drawable.flash_off);
                i = R.string.AccDescrCameraFlashOff;
                imageView.setContentDescription(LocaleController.getString(i));
                return;
            case 2:
                imageView.setImageResource(R.drawable.flash_auto);
                i = R.string.AccDescrCameraFlashAuto;
                imageView.setContentDescription(LocaleController.getString(i));
                return;
            default:
                return;
        }
    }

    public void setCurrentSpoilerVisible(int i, final boolean z) {
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        if (i == -1) {
            i = photoViewer.getCurrentIndex();
        }
        List imagesArrLocals = photoViewer.getImagesArrLocals();
        if (imagesArrLocals == null || imagesArrLocals.isEmpty() || i >= imagesArrLocals.size() || !(imagesArrLocals.get(i) instanceof MediaController.PhotoEntry) || !((MediaController.PhotoEntry) imagesArrLocals.get(i)).hasSpoiler) {
            return;
        }
        final MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) imagesArrLocals.get(i);
        this.gridView.forAllChild(new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChatAttachAlertPhotoLayout.this.lambda$setCurrentSpoilerVisible$0(photoEntry, z, (View) obj);
            }
        });
    }

    private boolean shouldLoadAllMedia() {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        return !chatAttachAlert.isPhotoPicker && ((chatAttachAlert.baseFragment instanceof ChatActivity) || chatAttachAlert.storyMediaPicker || chatAttachAlert.avatarPicker == 2);
    }

    public void showZoomControls(boolean z, boolean z2) {
        if ((this.zoomControlView.getTag() != null && z) || (this.zoomControlView.getTag() == null && !z)) {
            if (z) {
                Runnable runnable = this.zoomControlHideRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        ChatAttachAlertPhotoLayout.this.lambda$showZoomControls$14();
                    }
                };
                this.zoomControlHideRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 2000L);
                return;
            }
            return;
        }
        AnimatorSet animatorSet = this.zoomControlAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.zoomControlView.setTag(z ? 1 : null);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.zoomControlAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.zoomControlAnimation.playTogether(ObjectAnimator.ofFloat(this.zoomControlView, (Property<ZoomControlView, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ChatAttachAlertPhotoLayout.this.zoomControlAnimation = null;
            }
        });
        this.zoomControlAnimation.start();
        if (z) {
            Runnable runnable3 = new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$showZoomControls$15();
                }
            };
            this.zoomControlHideRunnable = runnable3;
            AndroidUtilities.runOnUIThread(runnable3, 2000L);
        }
    }

    private void updateAlbumsDropDown() {
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            final ArrayList<MediaController.AlbumEntry> arrayList = shouldLoadAllMedia() ? MediaController.allMediaAlbums : MediaController.allPhotoAlbums;
            ArrayList arrayList2 = new ArrayList(arrayList);
            this.dropDownAlbums = arrayList2;
            Collections.sort(arrayList2, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$updateAlbumsDropDown$12;
                    lambda$updateAlbumsDropDown$12 = ChatAttachAlertPhotoLayout.lambda$updateAlbumsDropDown$12(arrayList, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
                    return lambda$updateAlbumsDropDown$12;
                }
            });
        } else {
            this.dropDownAlbums = new ArrayList();
        }
        if (this.dropDownAlbums.isEmpty()) {
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
        int size = this.dropDownAlbums.size();
        for (int i = 0; i < size; i++) {
            MediaController.AlbumEntry albumEntry = (MediaController.AlbumEntry) this.dropDownAlbums.get(i);
            AlbumButton albumButton = new AlbumButton(getContext(), albumEntry.coverPhoto, albumEntry.bucketName, albumEntry.photos.size(), this.resourcesProvider);
            this.dropDownContainer.getPopupLayout().addView(albumButton);
            final int i2 = i + 10;
            albumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ChatAttachAlertPhotoLayout.this.lambda$updateAlbumsDropDown$13(i2, view);
                }
            });
        }
    }

    private void updatePhotoStarsPrice() {
        this.gridView.forAllChild(new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChatAttachAlertPhotoLayout.lambda$updatePhotoStarsPrice$11((View) obj);
            }
        });
    }

    private void updateStarsItem() {
        if (this.starsItem == null) {
            return;
        }
        long starsPrice = getStarsPrice();
        if (starsPrice > 0) {
            this.starsItem.setText(LocaleController.getString(R.string.PaidMediaPriceButton));
            this.starsItem.setSubtext(LocaleController.formatPluralString("Stars", (int) starsPrice, new Object[0]));
        } else {
            this.starsItem.setText(LocaleController.getString(R.string.PaidMediaButton));
            this.starsItem.setSubtext(null);
        }
    }

    @Override
    public void applyCaption(CharSequence charSequence) {
        Object obj;
        for (int i = 0; i < selectedPhotosOrder.size(); i++) {
            if (i == 0) {
                Object obj2 = selectedPhotosOrder.get(i);
                Object obj3 = selectedPhotos.get(obj2);
                if (obj3 instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry clone = ((MediaController.PhotoEntry) obj3).clone();
                    CharSequence[] charSequenceArr = {charSequence};
                    clone.entities = MediaDataController.getInstance(UserConfig.selectedAccount).getEntities(charSequenceArr, false);
                    clone.caption = charSequenceArr[0];
                    obj = clone;
                } else {
                    boolean z = obj3 instanceof MediaController.SearchImage;
                    obj = obj3;
                    if (z) {
                        MediaController.SearchImage clone2 = ((MediaController.SearchImage) obj3).clone();
                        CharSequence[] charSequenceArr2 = {charSequence};
                        clone2.entities = MediaDataController.getInstance(UserConfig.selectedAccount).getEntities(charSequenceArr2, false);
                        clone2.caption = charSequenceArr2[0];
                        obj = clone2;
                    }
                }
                selectedPhotos.put(obj2, obj);
            }
        }
    }

    @Override
    public boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    @Override
    public boolean canScheduleMessages() {
        Iterator it = selectedPhotos.entrySet().iterator();
        while (it.hasNext()) {
            Object value = ((Map.Entry) it.next()).getValue();
            if (value instanceof MediaController.PhotoEntry) {
                if (((MediaController.PhotoEntry) value).ttl != 0) {
                    return false;
                }
            } else if ((value instanceof MediaController.SearchImage) && ((MediaController.SearchImage) value).ttl != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean captionForAllMedia() {
        int i = 0;
        for (int i2 = 0; i2 < selectedPhotosOrder.size(); i2++) {
            Object obj = selectedPhotos.get(selectedPhotosOrder.get(i2));
            if (!TextUtils.isEmpty(obj instanceof MediaController.PhotoEntry ? ((MediaController.PhotoEntry) obj).caption : obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).caption : null)) {
                i++;
            }
        }
        return i <= 1;
    }

    public void checkCamera(boolean r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.checkCamera(boolean):void");
    }

    public void checkCameraViewPosition() {
        WindowInsets rootWindowInsets;
        int systemWindowInsetLeft;
        TextView textView;
        WindowInsets rootWindowInsets2;
        WindowInsets rootWindowInsets3;
        int systemWindowInsetTop;
        int dp;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().stickerMakerView != null && PhotoViewer.getInstance().stickerMakerView.isThanosInProgress) {
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null) {
                cameraView.invalidateOutline();
            }
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.gridView.findViewHolderForAdapterPosition(this.itemsPerRow - 1);
            if (findViewHolderForAdapterPosition2 != null) {
                findViewHolderForAdapterPosition2.itemView.invalidateOutline();
            }
            if ((!this.adapter.needCamera || !this.deviceHasGoodCamera || this.selectedAlbumEntry != this.galleryAlbumEntry) && (findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0)) != null) {
                findViewHolderForAdapterPosition.itemView.invalidateOutline();
            }
        }
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.invalidate();
        }
        if (i >= 23 && (textView = this.recordTime) != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            rootWindowInsets2 = getRootWindowInsets();
            if (rootWindowInsets2 == null) {
                dp = AndroidUtilities.dp(16.0f);
            } else {
                rootWindowInsets3 = getRootWindowInsets();
                systemWindowInsetTop = rootWindowInsets3.getSystemWindowInsetTop();
                dp = systemWindowInsetTop + AndroidUtilities.dp(2.0f);
            }
            marginLayoutParams.topMargin = dp;
        }
        if (this.deviceHasGoodCamera) {
            int childCount = this.gridView.getChildCount();
            int i2 = 0;
            while (true) {
                if (i2 >= childCount) {
                    break;
                }
                View childAt = this.gridView.getChildAt(i2);
                if (childAt instanceof PhotoAttachCameraCell) {
                    int i3 = Build.VERSION.SDK_INT;
                    if (childAt.isAttachedToWindow()) {
                        float y = childAt.getY() + this.gridView.getY() + getY();
                        float y2 = this.parentAlert.getSheetContainer().getY() + y;
                        float x = childAt.getX() + this.gridView.getX() + getX() + this.parentAlert.getSheetContainer().getX();
                        if (i3 >= 23) {
                            rootWindowInsets = getRootWindowInsets();
                            systemWindowInsetLeft = rootWindowInsets.getSystemWindowInsetLeft();
                            x -= systemWindowInsetLeft;
                        }
                        float currentActionBarHeight = ((i3 < 21 || this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight) + ActionBar.getCurrentActionBarHeight();
                        float f = y < currentActionBarHeight ? currentActionBarHeight - y : 0.0f;
                        if (f != this.cameraViewOffsetY) {
                            this.cameraViewOffsetY = f;
                            CameraView cameraView3 = this.cameraView;
                            if (cameraView3 != null) {
                                if (i3 >= 21) {
                                    cameraView3.invalidateOutline();
                                } else {
                                    cameraView3.invalidate();
                                }
                            }
                            FrameLayout frameLayout = this.cameraIcon;
                            if (frameLayout != null) {
                                frameLayout.invalidate();
                            }
                        }
                        float measuredHeight = (int) ((this.parentAlert.getSheetContainer().getMeasuredHeight() - this.parentAlert.buttonsRecyclerView.getMeasuredHeight()) + this.parentAlert.buttonsRecyclerView.getTranslationY());
                        MentionsContainerView mentionsContainerView = this.parentAlert.mentionContainer;
                        if (mentionsContainerView != null) {
                            measuredHeight -= mentionsContainerView.clipBottom() - AndroidUtilities.dp(6.0f);
                        }
                        if (childAt.getMeasuredHeight() + y > measuredHeight) {
                            this.cameraViewOffsetBottomY = Math.min(-AndroidUtilities.dp(5.0f), y - measuredHeight) + childAt.getMeasuredHeight();
                        } else {
                            this.cameraViewOffsetBottomY = 0.0f;
                        }
                        float[] fArr = this.cameraViewLocation;
                        fArr[0] = x;
                        fArr[1] = y2;
                    }
                } else {
                    i2++;
                }
            }
            if (this.cameraViewOffsetY != 0.0f || this.cameraViewOffsetX != 0.0f) {
                this.cameraViewOffsetX = 0.0f;
                this.cameraViewOffsetY = 0.0f;
                CameraView cameraView4 = this.cameraView;
                if (cameraView4 != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        cameraView4.invalidateOutline();
                    } else {
                        cameraView4.invalidate();
                    }
                }
                FrameLayout frameLayout2 = this.cameraIcon;
                if (frameLayout2 != null) {
                    frameLayout2.invalidate();
                }
            }
            this.cameraViewLocation[0] = AndroidUtilities.dp(-400.0f);
            this.cameraViewLocation[1] = 0.0f;
            applyCameraViewPosition();
        }
    }

    @Override
    public void checkColors() {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        int i = this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack;
        Drawable drawable = this.cameraDrawable;
        int i2 = Theme.key_dialogCameraIcon;
        Theme.setDrawableColor(drawable, getThemedColor(i2));
        this.progressView.setTextColor(getThemedColor(Theme.key_emptyListPlaceholder));
        this.gridView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof PhotoAttachCameraCell) {
                ((PhotoAttachCameraCell) view).getImageView().setColorFilter(new PorterDuffColorFilter(getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
            }
        }
        this.dropDown.setTextColor(getThemedColor(i));
        this.dropDownContainer.setPopupItemsColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_actionBarDefaultSubmenuItem), false);
        this.dropDownContainer.setPopupItemsColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_actionBarDefaultSubmenuItem), true);
        this.dropDownContainer.redrawPopup(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarUnscrolled : Theme.key_actionBarDefaultSubmenuBackground));
        Theme.setDrawableColor(this.dropDownDrawable, getThemedColor(i));
    }

    public void checkStorage() {
        if (!this.noGalleryPermissions || Build.VERSION.SDK_INT < 23) {
            return;
        }
        this.parentAlert.baseFragment.getParentActivity();
        boolean isNoGalleryPermissions = isNoGalleryPermissions();
        this.noGalleryPermissions = isNoGalleryPermissions;
        if (!isNoGalleryPermissions) {
            loadGalleryPhotos();
        }
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    public void clearSelectedPhotos() {
        this.spoilerItem.setText(LocaleController.getString(R.string.EnablePhotoSpoiler));
        this.spoilerItem.setAnimatedIcon(R.raw.photo_spoiler);
        this.parentAlert.selectedMenuItem.showSubItem(1);
        if (!selectedPhotos.isEmpty()) {
            Iterator it = selectedPhotos.entrySet().iterator();
            while (it.hasNext()) {
                ((MediaController.PhotoEntry) ((Map.Entry) it.next()).getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int i = 0; i < size; i++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) cameraPhotos.get(i);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
            }
            cameraPhotos.clear();
        }
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    public void closeCamera(boolean z) {
        CameraView cameraView;
        if (this.takingPhoto || this.cameraView == null) {
            return;
        }
        int[] iArr = this.animateCameraValues;
        int i = this.itemSize;
        iArr[1] = i;
        iArr[2] = i;
        Runnable runnable = this.zoomControlHideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.zoomControlHideRunnable = null;
        }
        AndroidUtilities.setLightNavigationBar(this.parentAlert.getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(getThemedColor(Theme.key_windowBackgroundGray))) > 0.721d);
        if (z) {
            this.additionCloseCameraY = this.cameraView.getTranslationY();
            this.cameraAnimationInProgress = true;
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", 0.0f));
            FrameLayout frameLayout = this.cameraPanel;
            Property property = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, 0.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.zoomControlView, (Property<ZoomControlView, Float>) property, 0.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, (Property<TextView, Float>) property, 0.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, (Property<RecyclerListView, Float>) property, 0.0f));
            int i2 = 0;
            while (true) {
                if (i2 >= 2) {
                    break;
                }
                if (this.flashModeButton[i2].getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i2], (Property<ImageView, Float>) View.ALPHA, 0.0f));
                    break;
                }
                i2++;
            }
            this.notificationsLocker.lock();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(220L);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlertPhotoLayout.this.notificationsLocker.unlock();
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                    chatAttachAlertPhotoLayout.cameraExpanded = false;
                    chatAttachAlertPhotoLayout.parentAlert.getWindow().clearFlags(128);
                    ChatAttachAlertPhotoLayout.this.setCameraOpenProgress(0.0f);
                    ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                    CameraView cameraView2 = ChatAttachAlertPhotoLayout.this.cameraView;
                    if (cameraView2 != null) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            cameraView2.invalidateOutline();
                        } else {
                            cameraView2.invalidate();
                        }
                    }
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                    chatAttachAlertPhotoLayout2.cameraOpened = false;
                    if (chatAttachAlertPhotoLayout2.cameraPanel != null) {
                        ChatAttachAlertPhotoLayout.this.cameraPanel.setVisibility(8);
                    }
                    if (ChatAttachAlertPhotoLayout.this.zoomControlView != null) {
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setVisibility(8);
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setTag(null);
                    }
                    if (ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(8);
                    }
                    CameraView cameraView3 = ChatAttachAlertPhotoLayout.this.cameraView;
                    if (cameraView3 != null) {
                        cameraView3.setFpsLimit(30);
                        if (Build.VERSION.SDK_INT >= 21) {
                            ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1024);
                        }
                    }
                }
            });
            animatorSet.start();
        } else {
            this.cameraExpanded = false;
            this.parentAlert.getWindow().clearFlags(128);
            setCameraOpenProgress(0.0f);
            this.animateCameraValues[0] = 0;
            setCameraOpenProgress(0.0f);
            this.cameraPanel.setAlpha(0.0f);
            this.cameraPanel.setVisibility(8);
            this.zoomControlView.setAlpha(0.0f);
            this.zoomControlView.setTag(null);
            this.zoomControlView.setVisibility(8);
            this.cameraPhotoRecyclerView.setAlpha(0.0f);
            this.counterTextView.setAlpha(0.0f);
            this.cameraPhotoRecyclerView.setVisibility(8);
            int i3 = 0;
            while (true) {
                if (i3 >= 2) {
                    break;
                }
                if (this.flashModeButton[i3].getVisibility() == 0) {
                    this.flashModeButton[i3].setAlpha(0.0f);
                    break;
                }
                i3++;
            }
            this.cameraOpened = false;
            CameraView cameraView2 = this.cameraView;
            if (cameraView2 != null) {
                cameraView2.setFpsLimit(30);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setSystemUiVisibility(1024);
                }
            }
        }
        CameraView cameraView3 = this.cameraView;
        if (cameraView3 != null) {
            cameraView3.setImportantForAccessibility(0);
        }
        this.gridView.setImportantForAccessibility(0);
        if (LiteMode.isEnabled(98784) || (cameraView = this.cameraView) == null) {
            return;
        }
        cameraView.showTexture(false, z);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ChatAttachAlert chatAttachAlert;
        if (i != NotificationCenter.albumsDidLoad) {
            if (i == NotificationCenter.cameraInitied) {
                checkCamera(false);
                return;
            }
            return;
        }
        if (this.adapter != null) {
            this.galleryAlbumEntry = shouldLoadAllMedia() ? MediaController.allMediaAlbumEntry : MediaController.allPhotosAlbumEntry;
            if (this.selectedAlbumEntry == null || ((chatAttachAlert = this.parentAlert) != null && chatAttachAlert.isStickerMode)) {
                this.selectedAlbumEntry = this.galleryAlbumEntry;
            } else if (shouldLoadAllMedia()) {
                int i3 = 0;
                while (true) {
                    if (i3 >= MediaController.allMediaAlbums.size()) {
                        break;
                    }
                    MediaController.AlbumEntry albumEntry = MediaController.allMediaAlbums.get(i3);
                    int i4 = albumEntry.bucketId;
                    MediaController.AlbumEntry albumEntry2 = this.selectedAlbumEntry;
                    if (i4 == albumEntry2.bucketId && albumEntry.videoOnly == albumEntry2.videoOnly) {
                        this.selectedAlbumEntry = albumEntry;
                        break;
                    }
                    i3++;
                }
            }
            this.loading = false;
            this.progressView.showTextView();
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
            if (!selectedPhotosOrder.isEmpty() && this.galleryAlbumEntry != null) {
                int size = selectedPhotosOrder.size();
                for (int i5 = 0; i5 < size; i5++) {
                    Integer num = (Integer) selectedPhotosOrder.get(i5);
                    Object obj = selectedPhotos.get(num);
                    MediaController.PhotoEntry photoEntry = this.galleryAlbumEntry.photosByIds.get(num.intValue());
                    if (photoEntry != null) {
                        if (obj instanceof MediaController.PhotoEntry) {
                            photoEntry.copyFrom((MediaController.PhotoEntry) obj);
                        }
                        selectedPhotos.put(num, photoEntry);
                    }
                }
            }
            updateAlbumsDropDown();
        }
    }

    protected ArrayList<Object> getAllPhotosArray() {
        if (this.selectedAlbumEntry == null) {
            return !cameraPhotos.isEmpty() ? cameraPhotos : new ArrayList<>(0);
        }
        if (cameraPhotos.isEmpty()) {
            return this.selectedAlbumEntry.photos;
        }
        ArrayList<Object> arrayList = new ArrayList<>(this.selectedAlbumEntry.photos.size() + cameraPhotos.size());
        arrayList.addAll(cameraPhotos);
        arrayList.addAll(this.selectedAlbumEntry.photos);
        return arrayList;
    }

    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    @Override
    public int getCurrentItemTop() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.currentItemTop = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.progressView.setTranslationY(0.0f);
            return Integer.MAX_VALUE;
        }
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        this.progressView.setTranslationY(((((getMeasuredHeight() - top) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2) + top);
        this.gridView.setTopGlowOffset(top);
        this.currentItemTop = top;
        return top;
    }

    @Override
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    @Override
    public int getListTopPadding() {
        return this.gridView.getPaddingTop();
    }

    @Override
    public int getSelectedItemsCount() {
        return selectedPhotosOrder.size();
    }

    public HashMap<Object, Object> getSelectedPhotos() {
        return selectedPhotos;
    }

    public ArrayList<Object> getSelectedPhotosOrder() {
        return selectedPhotosOrder;
    }

    public long getStarsPrice() {
        Iterator it = selectedPhotos.entrySet().iterator();
        if (it.hasNext()) {
            return ((MediaController.PhotoEntry) ((Map.Entry) it.next()).getValue()).starsAmount;
        }
        return 0L;
    }

    public void hideCamera(boolean z) {
        if (!this.deviceHasGoodCamera || this.cameraView == null) {
            return;
        }
        saveLastCameraBitmap();
        int childCount = this.gridView.getChildCount();
        int i = 0;
        while (true) {
            if (i >= childCount) {
                break;
            }
            View childAt = this.gridView.getChildAt(i);
            if (childAt instanceof PhotoAttachCameraCell) {
                childAt.setVisibility(0);
                ((PhotoAttachCameraCell) childAt).updateBitmap();
                break;
            }
            i++;
        }
        this.cameraView.destroy(z, null);
        AnimatorSet animatorSet = this.cameraInitAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.cameraInitAnimation = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertPhotoLayout.this.lambda$hideCamera$16();
            }
        }, 300L);
        this.canSaveCameraPreview = false;
    }

    public void loadGalleryPhotos() {
        if ((shouldLoadAllMedia() ? MediaController.allMediaAlbumEntry : MediaController.allPhotosAlbumEntry) != null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        MediaController.loadGalleryPhotosAlbums(0);
    }

    @Override
    public int needsActionBar() {
        return 1;
    }

    public void onActivityResultFragment(int r31, android.content.Intent r32, java.lang.String r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onActivityResultFragment(int, android.content.Intent, java.lang.String):void");
    }

    @Override
    public void onButtonsTranslationYUpdated() {
        checkCameraViewPosition();
        invalidate();
    }

    @Override
    public void onContainerTranslationUpdated(float f) {
        this.currentPanTranslationY = f;
        checkCameraViewPosition();
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                cameraView.invalidateOutline();
            } else {
                cameraView.invalidate();
            }
        }
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    @Override
    public boolean onContainerViewTouchEvent(MotionEvent motionEvent) {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            return processTouchEvent(motionEvent);
        }
        return false;
    }

    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        int dp;
        int measuredWidth;
        int i5 = i3 - i;
        int i6 = i4 - i2;
        boolean z = i5 < i6;
        if (view == this.cameraPanel) {
            int visibility = this.cameraPhotoRecyclerView.getVisibility();
            if (z) {
                if (visibility == 0) {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(222.0f), i5, i4 - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(126.0f), i5, i4);
                }
            } else if (visibility == 0) {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(222.0f), 0, i3 - AndroidUtilities.dp(96.0f), i6);
            } else {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(126.0f), 0, i3, i6);
            }
            return true;
        }
        if (view == this.zoomControlView) {
            if (z) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.zoomControlView.layout(0, i4 - AndroidUtilities.dp(310.0f), i5, i4 - AndroidUtilities.dp(260.0f));
                } else {
                    this.zoomControlView.layout(0, i4 - AndroidUtilities.dp(176.0f), i5, i4 - AndroidUtilities.dp(126.0f));
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.zoomControlView.layout(i3 - AndroidUtilities.dp(310.0f), 0, i3 - AndroidUtilities.dp(260.0f), i6);
            } else {
                this.zoomControlView.layout(i3 - AndroidUtilities.dp(176.0f), 0, i3 - AndroidUtilities.dp(126.0f), i6);
            }
            return true;
        }
        TextView textView = this.counterTextView;
        if (view != textView) {
            if (view != this.cameraPhotoRecyclerView) {
                return false;
            }
            if (z) {
                int dp2 = i6 - AndroidUtilities.dp(88.0f);
                view.layout(0, dp2, view.getMeasuredWidth(), view.getMeasuredHeight() + dp2);
            } else {
                int dp3 = (i + i5) - AndroidUtilities.dp(88.0f);
                view.layout(dp3, 0, view.getMeasuredWidth() + dp3, view.getMeasuredHeight());
            }
            return true;
        }
        if (z) {
            dp = (i5 - textView.getMeasuredWidth()) / 2;
            measuredWidth = i4 - AndroidUtilities.dp(167.0f);
            this.counterTextView.setRotation(0.0f);
            if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                measuredWidth -= AndroidUtilities.dp(96.0f);
            }
        } else {
            dp = i3 - AndroidUtilities.dp(167.0f);
            measuredWidth = (i6 / 2) + (this.counterTextView.getMeasuredWidth() / 2);
            this.counterTextView.setRotation(-90.0f);
            if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                dp -= AndroidUtilities.dp(96.0f);
            }
        }
        TextView textView2 = this.counterTextView;
        textView2.layout(dp, measuredWidth, textView2.getMeasuredWidth() + dp, this.counterTextView.getMeasuredHeight() + measuredWidth);
        return true;
    }

    public boolean onCustomMeasure(View view, int i, int i2) {
        int makeMeasureSpec;
        int makeMeasureSpec2;
        boolean z = i < i2;
        View view2 = this.cameraIcon;
        if (view != view2) {
            view2 = this.cameraView;
            if (view != view2) {
                FrameLayout frameLayout = this.cameraPanel;
                if (view == frameLayout) {
                    if (z) {
                        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), 1073741824));
                    } else {
                        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(i2, 1073741824));
                    }
                    return true;
                }
                ZoomControlView zoomControlView = this.zoomControlView;
                if (view == zoomControlView) {
                    if (z) {
                        zoomControlView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
                    } else {
                        zoomControlView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(i2, 1073741824));
                    }
                    return true;
                }
                RecyclerListView recyclerListView = this.cameraPhotoRecyclerView;
                if (view == recyclerListView) {
                    this.cameraPhotoRecyclerViewIgnoreLayout = true;
                    if (z) {
                        recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
                        if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                            this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                            this.cameraPhotoLayoutManager.setOrientation(0);
                            this.cameraAttachAdapter.notifyDataSetChanged();
                        }
                        this.cameraPhotoRecyclerViewIgnoreLayout = false;
                        return true;
                    }
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(i2, 1073741824));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 1) {
                        this.cameraPhotoRecyclerView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
                        this.cameraPhotoLayoutManager.setOrientation(1);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                    this.cameraPhotoRecyclerViewIgnoreLayout = false;
                    return true;
                }
            } else if (this.cameraOpened && !this.cameraAnimationInProgress) {
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
                makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i2 + this.parentAlert.getBottomInset(), 1073741824);
            }
            return false;
        }
        makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.itemSize, 1073741824);
        makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec((int) ((this.itemSize - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY), 1073741824);
        view2.measure(makeMeasureSpec, makeMeasureSpec2);
        return true;
    }

    @Override
    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.cameraInitied);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.albumsDidLoad);
    }

    @Override
    public boolean onDismiss() {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            closeCamera(true);
            return true;
        }
        hideCamera(true);
        return false;
    }

    @Override
    public void onDismissWithButtonClick(int i) {
        hideCamera((i == 0 || i == 2) ? false : true);
    }

    @Override
    public void onHidden() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setVisibility(8);
            this.cameraIcon.setVisibility(8);
        }
        for (Map.Entry entry : selectedPhotos.entrySet()) {
            if (entry.getValue() instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) entry.getValue()).isAttachSpoilerRevealed = false;
            }
        }
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onHide() {
        this.isHidden = true;
        int childCount = this.gridView.getChildCount();
        int i = 0;
        while (true) {
            if (i >= childCount) {
                break;
            }
            View childAt = this.gridView.getChildAt(i);
            if (childAt instanceof PhotoAttachCameraCell) {
                childAt.setVisibility(0);
                saveLastCameraBitmap();
                ((PhotoAttachCameraCell) childAt).updateBitmap();
                break;
            }
            i++;
        }
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator withEndAction = this.dropDown.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.EASE_BOTH).withEndAction(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertPhotoLayout.this.lambda$onHide$25();
            }
        });
        this.headerAnimator = withEndAction;
        withEndAction.start();
        pauseCameraPreview();
    }

    @Override
    public void onHideShowProgress(float f) {
        int i;
        CameraView cameraView;
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setAlpha(f);
            this.cameraIcon.setAlpha(f);
            if (f != 0.0f && this.cameraView.getVisibility() != 0) {
                cameraView = this.cameraView;
                i = 0;
            } else {
                if (f != 0.0f) {
                    return;
                }
                i = 4;
                if (this.cameraView.getVisibility() == 4) {
                    return;
                } else {
                    cameraView = this.cameraView;
                }
            }
            cameraView.setVisibility(i);
            this.cameraIcon.setVisibility(i);
        }
    }

    public void onInit(boolean r5, boolean r6, boolean r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onInit(boolean, boolean, boolean):void");
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            PhotoAttachAdapter photoAttachAdapter = this.adapter;
            if (photoAttachAdapter != null) {
                photoAttachAdapter.notifyDataSetChanged();
            }
        }
        super.onLayout(z, i, i2, i3, i4);
        checkCameraViewPosition();
    }

    @Override
    public void onMenuItemClick(int i) {
        TLRPC.Chat chat;
        boolean z;
        ChatAttachAlert.ChatAttachViewDelegate chatAttachViewDelegate;
        boolean z2;
        boolean z3;
        int i2;
        boolean z4;
        Context context;
        long dialogId;
        AlertsCreator.ScheduleDatePickerDelegate scheduleDatePickerDelegate;
        if (i == 7) {
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            boolean z5 = chatAttachAlert.captionAbove;
            chatAttachAlert.captionAbove = !z5;
            this.captionItem.setState(z5, true);
            return;
        }
        if ((i == 0 || i == 1) && this.parentAlert.maxSelectedPhotos > 0 && selectedPhotosOrder.size() > 1 && (chat = this.parentAlert.getChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled) {
            AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.Slowmode), LocaleController.getString(R.string.SlowmodeSendError), this.resourcesProvider).show();
            return;
        }
        if (i == 0) {
            ChatAttachAlert chatAttachAlert2 = this.parentAlert;
            if (chatAttachAlert2.editingMessageObject == null) {
                BaseFragment baseFragment = chatAttachAlert2.baseFragment;
                if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isInScheduleMode()) {
                    context = getContext();
                    dialogId = ((ChatActivity) this.parentAlert.baseFragment).getDialogId();
                    scheduleDatePickerDelegate = new AlertsCreator.ScheduleDatePickerDelegate() {
                        @Override
                        public final void didSelectDate(boolean z6, int i3) {
                            ChatAttachAlertPhotoLayout.this.lambda$onMenuItemClick$19(z6, i3);
                        }
                    };
                    AlertsCreator.createScheduleDatePickerDialog(context, dialogId, scheduleDatePickerDelegate, this.resourcesProvider);
                    return;
                }
            }
            this.parentAlert.applyCaption();
            chatAttachViewDelegate = this.parentAlert.delegate;
            z2 = false;
            z3 = false;
            i2 = 7;
            z4 = false;
            chatAttachViewDelegate.didPressedButton(i2, z4, true, 0, 0L, z2, z3);
            return;
        }
        if (i == 1) {
            ChatAttachAlert chatAttachAlert3 = this.parentAlert;
            if (chatAttachAlert3.editingMessageObject == null) {
                BaseFragment baseFragment2 = chatAttachAlert3.baseFragment;
                if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                    context = getContext();
                    dialogId = ((ChatActivity) this.parentAlert.baseFragment).getDialogId();
                    scheduleDatePickerDelegate = new AlertsCreator.ScheduleDatePickerDelegate() {
                        @Override
                        public final void didSelectDate(boolean z6, int i3) {
                            ChatAttachAlertPhotoLayout.this.lambda$onMenuItemClick$20(z6, i3);
                        }
                    };
                    AlertsCreator.createScheduleDatePickerDialog(context, dialogId, scheduleDatePickerDelegate, this.resourcesProvider);
                    return;
                }
            }
            this.parentAlert.applyCaption();
            chatAttachViewDelegate = this.parentAlert.delegate;
            z2 = false;
            z3 = false;
            i2 = 4;
            z4 = true;
            chatAttachViewDelegate.didPressedButton(i2, z4, true, 0, 0L, z2, z3);
            return;
        }
        if (i == 2) {
            if (this.parentAlert.getPhotoPreviewLayout() != null) {
                this.parentAlert.getPhotoPreviewLayout().startMediaCrossfade();
            }
            Iterator it = selectedPhotos.entrySet().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((MediaController.PhotoEntry) ((Map.Entry) it.next()).getValue()).hasSpoiler) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            final boolean z6 = !z;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$onMenuItemClick$21(z6);
                }
            }, 200L);
            final ArrayList arrayList = new ArrayList();
            for (Map.Entry entry : selectedPhotos.entrySet()) {
                if (entry.getValue() instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry.getValue();
                    photoEntry.hasSpoiler = z6;
                    photoEntry.isChatPreviewSpoilerRevealed = false;
                    photoEntry.isAttachSpoilerRevealed = false;
                    arrayList.add(Integer.valueOf(photoEntry.imageId));
                }
            }
            this.gridView.forAllChild(new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ChatAttachAlertPhotoLayout.lambda$onMenuItemClick$22(arrayList, z6, (View) obj);
                }
            });
            if (this.parentAlert.getCurrentAttachLayout() != this) {
                this.adapter.notifyDataSetChanged();
            }
            if (this.parentAlert.getPhotoPreviewLayout() != null) {
                this.parentAlert.getPhotoPreviewLayout().invalidateGroupsView();
                return;
            }
            return;
        }
        if (i != 3) {
            if (i == 6) {
                ChatAttachAlert chatAttachAlert4 = this.parentAlert;
                chatAttachAlert4.updatePhotoPreview(chatAttachAlert4.getCurrentAttachLayout() != this.parentAlert.getPhotoPreviewLayout());
                return;
            }
            if (i == 8) {
                StarsIntroActivity.showMediaPriceSheet(getContext(), getStarsPrice(), true, new Utilities.Callback2() {
                    @Override
                    public final void run(Object obj, Object obj2) {
                        ChatAttachAlertPhotoLayout.this.lambda$onMenuItemClick$23((Long) obj, (Runnable) obj2);
                    }
                }, this.resourcesProvider);
                return;
            }
            if (i >= 10) {
                MediaController.AlbumEntry albumEntry = (MediaController.AlbumEntry) this.dropDownAlbums.get(i - 10);
                this.selectedAlbumEntry = albumEntry;
                if (albumEntry == this.galleryAlbumEntry) {
                    this.dropDown.setText(LocaleController.getString(R.string.ChatGallery));
                } else {
                    this.dropDown.setText(albumEntry.bucketName);
                }
                this.adapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                this.layoutManager.scrollToPositionWithOffset(0, (-this.gridView.getPaddingTop()) + AndroidUtilities.dp(7.0f));
                return;
            }
            return;
        }
        try {
            ChatAttachAlert chatAttachAlert5 = this.parentAlert;
            if (!(chatAttachAlert5.baseFragment instanceof ChatActivity) && chatAttachAlert5.avatarPicker != 2) {
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                ChatAttachAlert chatAttachAlert6 = this.parentAlert;
                if (chatAttachAlert6.avatarPicker != 0) {
                    chatAttachAlert6.baseFragment.startActivityForResult(intent, 14);
                } else {
                    chatAttachAlert6.baseFragment.startActivityForResult(intent, 1);
                }
                this.parentAlert.dismiss(true);
            }
            Intent intent2 = new Intent();
            intent2.setType("video/*");
            intent2.setAction("android.intent.action.GET_CONTENT");
            intent2.putExtra("android.intent.extra.sizeLimit", 2097152000L);
            Intent intent3 = new Intent("android.intent.action.PICK");
            intent3.setType("image/*");
            Intent createChooser = Intent.createChooser(intent3, null);
            createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{intent2});
            ChatAttachAlert chatAttachAlert7 = this.parentAlert;
            if (chatAttachAlert7.avatarPicker != 0) {
                chatAttachAlert7.baseFragment.startActivityForResult(createChooser, 14);
            } else {
                chatAttachAlert7.baseFragment.startActivityForResult(createChooser, 1);
            }
            this.parentAlert.dismiss(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onOpenAnimationEnd() {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        checkCamera(chatAttachAlert != null && (chatAttachAlert.baseFragment instanceof ChatActivity));
    }

    @Override
    public void onPanTransitionStart(boolean z, int i) {
        super.onPanTransitionStart(z, i);
        checkCameraViewPosition();
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                cameraView.invalidateOutline();
            } else {
                cameraView.invalidate();
            }
        }
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    @Override
    public void onPause() {
        ShutterButton shutterButton = this.shutterButton;
        if (shutterButton == null) {
            return;
        }
        if (this.requestingPermissions) {
            if (this.cameraView != null && shutterButton.getState() == ShutterButton.State.RECORDING) {
                this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
            }
            this.requestingPermissions = false;
            return;
        }
        if (this.cameraView != null && shutterButton.getState() == ShutterButton.State.RECORDING) {
            resetRecordState();
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
            this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
        }
        if (this.cameraOpened) {
            closeCamera(false);
        }
        hideCamera(true);
    }

    @Override
    public void onPreMeasure(int r6, int r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onPreMeasure(int, int):void");
    }

    @Override
    public void onResume() {
        if (!this.parentAlert.isShowing() || this.parentAlert.isDismissed() || PhotoViewer.getInstance().isVisible()) {
            return;
        }
        checkCamera(false);
    }

    @Override
    public void onSelectedItemsCountChanged(int r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onSelectedItemsCountChanged(int):void");
    }

    @Override
    public boolean onSheetKeyDown(int i, KeyEvent keyEvent) {
        if (!this.cameraOpened) {
            return false;
        }
        if (i != 24 && i != 25 && i != 79 && i != 85) {
            return false;
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }

    @Override
    public void onShow(final ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        this.dropDownContainer.setVisibility(0);
        boolean z = attachAlertLayout instanceof ChatAttachAlertPhotoLayoutPreview;
        if (z) {
            ViewPropertyAnimator interpolator = this.dropDown.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.headerAnimator = interpolator;
            interpolator.start();
        } else {
            clearSelectedPhotos();
            this.dropDown.setAlpha(1.0f);
        }
        this.parentAlert.actionBar.setTitle("");
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        if (z) {
            this.gridView.post(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$onShow$24(attachAlertLayout);
                }
            });
        }
        checkCameraViewPosition();
        resumeCameraPreview();
    }

    @Override
    public void onShown() {
        this.isHidden = false;
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setVisibility(0);
        }
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setVisibility(0);
        }
        if (this.cameraView != null) {
            int childCount = this.gridView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    childAt.setVisibility(4);
                    break;
                }
                i++;
            }
        }
        if (this.checkCameraWhenShown) {
            this.checkCameraWhenShown = false;
            checkCamera(true);
        }
    }

    protected void openPhotoViewer(MediaController.PhotoEntry photoEntry, boolean z, boolean z2) {
        ChatActivity chatActivity;
        int i;
        ArrayList<Object> allPhotosArray;
        int size;
        if (photoEntry != null) {
            cameraPhotos.add(photoEntry);
            selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
            selectedPhotosOrder.add(Integer.valueOf(photoEntry.imageId));
            this.parentAlert.updateCountButton(0);
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
        if (photoEntry != null && !z2 && cameraPhotos.size() > 1) {
            updatePhotosCounter(false);
            if (this.cameraView != null) {
                this.zoomControlView.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
                this.cameraView.setZoom(0.0f);
                CameraController.getInstance().startPreview(this.cameraView.getCameraSessionObject());
                return;
            }
            return;
        }
        if (cameraPhotos.isEmpty()) {
            return;
        }
        this.cancelTakingPhotos = true;
        BaseFragment baseFragment = this.parentAlert.baseFragment;
        if (baseFragment == null) {
            baseFragment = LaunchActivity.getLastFragment();
        }
        if (baseFragment == null) {
            return;
        }
        PhotoViewer.getInstance().setParentActivity(baseFragment.getParentActivity(), this.resourcesProvider);
        PhotoViewer.getInstance().setParentAlert(this.parentAlert);
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        photoViewer.setMaxSelectedPhotos(chatAttachAlert.maxSelectedPhotos, chatAttachAlert.allowOrder);
        ChatAttachAlert chatAttachAlert2 = this.parentAlert;
        if (chatAttachAlert2.isPhotoPicker && chatAttachAlert2.isStickerMode) {
            chatActivity = (ChatActivity) chatAttachAlert2.baseFragment;
            i = 11;
        } else if (chatAttachAlert2.avatarPicker != 0) {
            chatActivity = null;
            i = 1;
        } else {
            BaseFragment baseFragment2 = chatAttachAlert2.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                chatActivity = (ChatActivity) baseFragment2;
                i = 2;
            } else {
                chatActivity = null;
                i = 5;
            }
        }
        if (chatAttachAlert2.avatarPicker != 0) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(photoEntry);
            allPhotosArray = arrayList;
            size = 0;
        } else {
            allPhotosArray = getAllPhotosArray();
            size = cameraPhotos.size() - 1;
        }
        if (this.parentAlert.getAvatarFor() != null && photoEntry != null) {
            this.parentAlert.getAvatarFor().isVideo = photoEntry.isVideo;
        }
        PhotoViewer.getInstance().openPhotoForSelect(allPhotosArray, size, i, false, new AnonymousClass15(z), chatActivity);
        PhotoViewer.getInstance().setAvatarFor(this.parentAlert.getAvatarFor());
        if (this.parentAlert.isStickerMode) {
            PhotoViewer.getInstance().enableStickerMode(null, false, this.parentAlert.customStickerHandler);
            PhotoViewer.getInstance().prepareSegmentImage();
        }
    }

    public void pauseCamera(boolean z) {
        if (!this.needCamera || this.noCameraPermissions) {
            return;
        }
        if (!z) {
            showCamera();
            return;
        }
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            this.isCameraFrontfaceBeforeEnteringEditMode = Boolean.valueOf(cameraView.isFrontface());
            hideCamera(true);
        }
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    public void scrollToTop() {
        this.gridView.smoothScrollToPosition(0);
    }

    public void setCameraOpenProgress(float f) {
        int i;
        int i2;
        if (this.cameraView == null) {
            return;
        }
        this.cameraOpenProgress = f;
        int[] iArr = this.animateCameraValues;
        float f2 = iArr[1];
        float f3 = iArr[2];
        int i3 = AndroidUtilities.displaySize.x;
        float width = (this.parentAlert.getContainer().getWidth() - this.parentAlert.getLeftInset()) - this.parentAlert.getRightInset();
        float height = this.parentAlert.getContainer().getHeight();
        float[] fArr = this.cameraViewLocation;
        float f4 = fArr[0];
        float f5 = fArr[1];
        float f6 = this.additionCloseCameraY;
        if (f == 0.0f) {
            this.cameraIcon.setTranslationX(f4);
            this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
        float textureHeight = this.cameraView.getTextureHeight(f2, f3) / this.cameraView.getTextureHeight(width, height);
        float f7 = f3 / height;
        float f8 = f2 / width;
        if (this.cameraExpanded) {
            i = (int) width;
            i2 = (int) height;
            float f9 = 1.0f - f;
            float f10 = (textureHeight * f9) + f;
            this.cameraView.getTextureView().setScaleX(f10);
            this.cameraView.getTextureView().setScaleY(f10);
            float f11 = ((1.0f - ((f7 * f9) + f)) * height) / 2.0f;
            float f12 = f4 * f9;
            this.cameraView.setTranslationX((f12 + (f * 0.0f)) - (((1.0f - ((f8 * f9) + f)) * width) / 2.0f));
            float f13 = f5 * f9;
            this.cameraView.setTranslationY(((f6 * f) + f13) - f11);
            this.animationClipTop = f13 - this.cameraView.getTranslationY();
            this.animationClipBottom = (((f5 + f3) * f9) - this.cameraView.getTranslationY()) + (height * f);
            this.animationClipLeft = f12 - this.cameraView.getTranslationX();
            this.animationClipRight = (((f4 + f2) * f9) - this.cameraView.getTranslationX()) + (width * f);
        } else {
            i = (int) f2;
            i2 = (int) f3;
            this.cameraView.getTextureView().setScaleX(1.0f);
            this.cameraView.getTextureView().setScaleY(1.0f);
            this.animationClipTop = 0.0f;
            this.animationClipBottom = height;
            this.animationClipLeft = 0.0f;
            this.animationClipRight = width;
            this.cameraView.setTranslationX(f4);
            this.cameraView.setTranslationY(f5);
        }
        if (f <= 0.5f) {
            this.cameraIcon.setAlpha(1.0f - (f / 0.5f));
        } else {
            this.cameraIcon.setAlpha(0.0f);
        }
        if (layoutParams.width != i || layoutParams.height != i2) {
            layoutParams.width = i;
            layoutParams.height = i2;
            this.cameraView.requestLayout();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.cameraView.invalidateOutline();
        } else {
            this.cameraView.invalidate();
        }
    }

    public void setCheckCameraWhenShown(boolean z) {
        this.checkCameraWhenShown = z;
    }

    public void setStarsPrice(long j) {
        if (!selectedPhotos.isEmpty()) {
            Iterator it = selectedPhotos.entrySet().iterator();
            while (it.hasNext()) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ((Map.Entry) it.next()).getValue();
                photoEntry.starsAmount = j;
                photoEntry.hasSpoiler = j > 0;
                photoEntry.isChatPreviewSpoilerRevealed = false;
                photoEntry.isAttachSpoilerRevealed = false;
            }
        }
        onSelectedItemsCountChanged(getSelectedItemsCount());
        if (checkSelectedCount(false)) {
            updateCheckedPhotos();
        }
    }

    @Override
    public void setTranslationY(float f) {
        float f2;
        View checkBox;
        if (this.parentAlert.getSheetAnimationType() == 1) {
            float f3 = (f / 40.0f) * (-0.1f);
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    PhotoAttachCameraCell photoAttachCameraCell = (PhotoAttachCameraCell) childAt;
                    f2 = 1.0f + f3;
                    photoAttachCameraCell.getImageView().setScaleX(f2);
                    checkBox = photoAttachCameraCell.getImageView();
                } else if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    f2 = 1.0f + f3;
                    photoAttachPhotoCell.getCheckBox().setScaleX(f2);
                    checkBox = photoAttachPhotoCell.getCheckBox();
                }
                checkBox.setScaleY(f2);
            }
        }
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
        invalidate();
    }

    public void showAvatarConstructorFragment(AvatarConstructorPreviewCell avatarConstructorPreviewCell, TLRPC.VideoSize videoSize) {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        final AvatarConstructorFragment avatarConstructorFragment = new AvatarConstructorFragment(chatAttachAlert.parentImageUpdater, chatAttachAlert.getAvatarFor());
        avatarConstructorFragment.finishOnDone = this.parentAlert.getAvatarFor() == null || this.parentAlert.getAvatarFor().type != 2;
        this.parentAlert.baseFragment.presentFragment(avatarConstructorFragment);
        if (avatarConstructorPreviewCell != null) {
            avatarConstructorFragment.startFrom(avatarConstructorPreviewCell);
        }
        if (videoSize != null) {
            avatarConstructorFragment.startFrom(videoSize);
        }
        avatarConstructorFragment.setDelegate(new AvatarConstructorFragment.Delegate() {
            @Override
            public final void onDone(AvatarConstructorFragment.BackgroundGradient backgroundGradient, long j, TLRPC.Document document, AvatarConstructorFragment.PreviewView previewView) {
                ChatAttachAlertPhotoLayout.this.lambda$showAvatarConstructorFragment$10(avatarConstructorFragment, backgroundGradient, j, document, previewView);
            }
        });
    }

    public void showCamera() {
        if (this.parentAlert.paused || !this.mediaEnabled) {
            return;
        }
        if (this.cameraView == null) {
            boolean z = !LiteMode.isEnabled(98784);
            Context context = getContext();
            Boolean bool = this.isCameraFrontfaceBeforeEnteringEditMode;
            CameraView cameraView = new CameraView(context, bool != null ? bool.booleanValue() : this.parentAlert.openWithFrontFaceCamera, z) {
                Bulletin.Delegate bulletinDelegate = new Bulletin.Delegate() {
                    @Override
                    public boolean allowLayoutChanges() {
                        return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
                    }

                    @Override
                    public boolean bottomOffsetAnimated() {
                        return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
                    }

                    @Override
                    public boolean clipWithGradient(int i) {
                        return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
                    }

                    @Override
                    public int getBottomOffset(int i) {
                        return AndroidUtilities.dp(126.0f) + ChatAttachAlertPhotoLayout.this.parentAlert.getBottomInset();
                    }

                    @Override
                    public int getTopOffset(int i) {
                        return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
                    }

                    @Override
                    public void onBottomOffsetChange(float f) {
                        Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                    }

                    @Override
                    public void onHide(Bulletin bulletin) {
                        Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                    }

                    @Override
                    public void onShow(Bulletin bulletin) {
                        Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                    }
                };

                @Override
                public void dispatchDraw(Canvas canvas) {
                    if (AndroidUtilities.makingGlobalBlurBitmap) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        super.dispatchDraw(canvas);
                        return;
                    }
                    float commentTextViewTop = ((ChatAttachAlertPhotoLayout.this.parentAlert.getCommentTextViewTop() + ChatAttachAlertPhotoLayout.this.currentPanTranslationY) + ChatAttachAlertPhotoLayout.this.parentAlert.getContainerView().getTranslationY()) - ChatAttachAlertPhotoLayout.this.cameraView.getTranslationY();
                    MentionsContainerView mentionsContainerView = ChatAttachAlertPhotoLayout.this.parentAlert.mentionContainer;
                    int min = (int) Math.min(commentTextViewTop - (mentionsContainerView != null ? mentionsContainerView.clipBottom() + AndroidUtilities.dp(8.0f) : 0.0f), getMeasuredHeight());
                    if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                        float f = chatAttachAlertPhotoLayout.animationClipLeft + (chatAttachAlertPhotoLayout.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                        float f2 = chatAttachAlertPhotoLayout2.animationClipTop + (chatAttachAlertPhotoLayout2.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout3 = ChatAttachAlertPhotoLayout.this;
                        rectF.set(f, f2, chatAttachAlertPhotoLayout3.animationClipRight, Math.min(min, chatAttachAlertPhotoLayout3.animationClipBottom));
                    } else {
                        if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout4 = ChatAttachAlertPhotoLayout.this;
                            if (!chatAttachAlertPhotoLayout4.cameraOpened) {
                                AndroidUtilities.rectTmp.set(chatAttachAlertPhotoLayout4.cameraViewOffsetX, ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, getMeasuredWidth(), Math.min(min, getMeasuredHeight()));
                            }
                        }
                        AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), Math.min(min, getMeasuredHeight()));
                    }
                    canvas.save();
                    canvas.clipRect(AndroidUtilities.rectTmp);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }

                @Override
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    Bulletin.addDelegate(ChatAttachAlertPhotoLayout.this.cameraView, this.bulletinDelegate);
                }

                @Override
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    Bulletin.removeDelegate(ChatAttachAlertPhotoLayout.this.cameraView);
                }
            };
            this.cameraView = cameraView;
            PhotoAttachCameraCell photoAttachCameraCell = this.cameraCell;
            if (photoAttachCameraCell != null && z) {
                cameraView.setThumbDrawable(photoAttachCameraCell.getDrawable());
            }
            CameraView cameraView2 = this.cameraView;
            BaseFragment baseFragment = this.parentAlert.baseFragment;
            cameraView2.setRecordFile(AndroidUtilities.generateVideoPath((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isSecretChat()));
            this.cameraView.setFocusable(true);
            this.cameraView.setFpsLimit(30);
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        float commentTextViewTop = ChatAttachAlertPhotoLayout.this.parentAlert.getCommentTextViewTop();
                        MentionsContainerView mentionsContainerView = ChatAttachAlertPhotoLayout.this.parentAlert.mentionContainer;
                        int min = (int) Math.min((((commentTextViewTop - (mentionsContainerView != null ? mentionsContainerView.clipBottom() + AndroidUtilities.dp(8.0f) : 0.0f)) + ChatAttachAlertPhotoLayout.this.currentPanTranslationY) + ChatAttachAlertPhotoLayout.this.parentAlert.getContainerView().getTranslationY()) - ChatAttachAlertPhotoLayout.this.cameraView.getTranslationY(), view.getMeasuredHeight());
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                        if (chatAttachAlertPhotoLayout.cameraOpened) {
                            min = view.getMeasuredHeight();
                        } else if (chatAttachAlertPhotoLayout.cameraAnimationInProgress) {
                            min = AndroidUtilities.lerp(min, view.getMeasuredHeight(), ChatAttachAlertPhotoLayout.this.cameraOpenProgress);
                        }
                        if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                            if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                                if (!chatAttachAlertPhotoLayout2.cameraOpened) {
                                    int dp = AndroidUtilities.dp(chatAttachAlertPhotoLayout2.parentAlert.cornerRadius * 8.0f);
                                    outline.setRoundRect((int) ChatAttachAlertPhotoLayout.this.cameraViewOffsetX, (int) ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, view.getMeasuredWidth() + dp, Math.min(min, view.getMeasuredHeight()) + dp, dp);
                                    return;
                                }
                            }
                            outline.setRect(0, 0, view.getMeasuredWidth(), Math.min(min, view.getMeasuredHeight()));
                            return;
                        }
                        RectF rectF = AndroidUtilities.rectTmp;
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout3 = ChatAttachAlertPhotoLayout.this;
                        float f = chatAttachAlertPhotoLayout3.animationClipLeft + (chatAttachAlertPhotoLayout3.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout4 = ChatAttachAlertPhotoLayout.this;
                        float f2 = chatAttachAlertPhotoLayout4.animationClipTop + (chatAttachAlertPhotoLayout4.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout5 = ChatAttachAlertPhotoLayout.this;
                        rectF.set(f, f2, chatAttachAlertPhotoLayout5.animationClipRight, chatAttachAlertPhotoLayout5.animationClipBottom);
                        outline.setRect((int) rectF.left, (int) rectF.top, (int) rectF.right, Math.min(min, (int) rectF.bottom));
                    }
                });
                this.cameraView.setClipToOutline(true);
            }
            this.cameraView.setContentDescription(LocaleController.getString(R.string.AccDescrInstantCamera));
            BottomSheet.ContainerView container = this.parentAlert.getContainer();
            CameraView cameraView3 = this.cameraView;
            int i = this.itemSize;
            container.addView(cameraView3, 1, new FrameLayout.LayoutParams(i, i));
            this.cameraView.setDelegate(new CameraView.CameraViewDelegate() {
                @Override
                public void onCameraInit() {
                    String currentFlashMode = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode();
                    String nextFlashMode = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getNextFlashMode();
                    if (currentFlashMode == null || nextFlashMode == null) {
                        return;
                    }
                    if (currentFlashMode.equals(nextFlashMode)) {
                        for (int i2 = 0; i2 < 2; i2++) {
                            ChatAttachAlertPhotoLayout.this.flashModeButton[i2].setVisibility(4);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[i2].setAlpha(0.0f);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[i2].setTranslationY(0.0f);
                        }
                    } else {
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                        chatAttachAlertPhotoLayout.setCameraFlashModeIcon(chatAttachAlertPhotoLayout.flashModeButton[0], ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode());
                        int i3 = 0;
                        while (i3 < 2) {
                            ChatAttachAlertPhotoLayout.this.flashModeButton[i3].setVisibility(i3 == 0 ? 0 : 4);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[i3].setAlpha((i3 == 0 && ChatAttachAlertPhotoLayout.this.cameraOpened) ? 1.0f : 0.0f);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[i3].setTranslationY(0.0f);
                            i3++;
                        }
                    }
                    ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource(ChatAttachAlertPhotoLayout.this.cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                    ChatAttachAlertPhotoLayout.this.switchCameraButton.setVisibility(ChatAttachAlertPhotoLayout.this.cameraView.hasFrontFaceCamera() ? 0 : 4);
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                    if (!chatAttachAlertPhotoLayout2.cameraOpened) {
                        chatAttachAlertPhotoLayout2.cameraInitAnimation = new AnimatorSet();
                        AnimatorSet animatorSet = ChatAttachAlertPhotoLayout.this.cameraInitAnimation;
                        CameraView cameraView4 = ChatAttachAlertPhotoLayout.this.cameraView;
                        Property property = View.ALPHA;
                        animatorSet.playTogether(ObjectAnimator.ofFloat(cameraView4, (Property<CameraView, Float>) property, 0.0f, 1.0f), ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraIcon, (Property<FrameLayout, Float>) property, 0.0f, 1.0f));
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.setDuration(180L);
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationCancel(Animator animator) {
                                ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if (animator.equals(ChatAttachAlertPhotoLayout.this.cameraInitAnimation)) {
                                    ChatAttachAlertPhotoLayout.this.canSaveCameraPreview = true;
                                    ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                                    if (ChatAttachAlertPhotoLayout.this.isHidden) {
                                        return;
                                    }
                                    int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                                    for (int i4 = 0; i4 < childCount; i4++) {
                                        View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i4);
                                        if (childAt instanceof PhotoAttachCameraCell) {
                                            childAt.setVisibility(4);
                                            return;
                                        }
                                    }
                                }
                            }
                        });
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.start();
                    }
                    if (ChatAttachAlertPhotoLayout.this.afterCameraInitRunnable != null) {
                        ChatAttachAlertPhotoLayout.this.afterCameraInitRunnable.run();
                    }
                }
            });
            if (this.cameraIcon == null) {
                FrameLayout frameLayout = new FrameLayout(getContext()) {
                    @Override
                    protected void onDraw(Canvas canvas) {
                        int min = (int) Math.min(((ChatAttachAlertPhotoLayout.this.parentAlert.getCommentTextViewTop() + ChatAttachAlertPhotoLayout.this.currentPanTranslationY) + ChatAttachAlertPhotoLayout.this.parentAlert.getContainerView().getTranslationY()) - ChatAttachAlertPhotoLayout.this.cameraView.getTranslationY(), getMeasuredHeight());
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                        if (chatAttachAlertPhotoLayout.cameraOpened) {
                            min = getMeasuredHeight();
                        } else if (chatAttachAlertPhotoLayout.cameraAnimationInProgress) {
                            min = AndroidUtilities.lerp(min, getMeasuredHeight(), ChatAttachAlertPhotoLayout.this.cameraOpenProgress);
                        }
                        int intrinsicWidth = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicWidth();
                        int intrinsicHeight = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicHeight();
                        int i2 = (ChatAttachAlertPhotoLayout.this.itemSize - intrinsicWidth) / 2;
                        int i3 = (ChatAttachAlertPhotoLayout.this.itemSize - intrinsicHeight) / 2;
                        if (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY != 0.0f) {
                            i3 = (int) (i3 - ChatAttachAlertPhotoLayout.this.cameraViewOffsetY);
                        }
                        boolean z2 = min < getMeasuredHeight();
                        if (z2) {
                            canvas.save();
                            canvas.clipRect(0, 0, getMeasuredWidth(), min);
                        }
                        ChatAttachAlertPhotoLayout.this.cameraDrawable.setBounds(i2, i3, intrinsicWidth + i2, intrinsicHeight + i3);
                        ChatAttachAlertPhotoLayout.this.cameraDrawable.draw(canvas);
                        if (z2) {
                            canvas.restore();
                        }
                    }
                };
                this.cameraIcon = frameLayout;
                frameLayout.setWillNotDraw(false);
                this.cameraIcon.setClipChildren(true);
            }
            BottomSheet.ContainerView container2 = this.parentAlert.getContainer();
            FrameLayout frameLayout2 = this.cameraIcon;
            int i2 = this.itemSize;
            container2.addView(frameLayout2, 2, new FrameLayout.LayoutParams(i2, i2));
            this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
            this.cameraView.setEnabled(this.mediaEnabled);
            this.cameraIcon.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
            this.cameraIcon.setEnabled(this.mediaEnabled);
            if (this.isHidden) {
                this.cameraView.setVisibility(8);
                this.cameraIcon.setVisibility(8);
            }
            if (this.cameraOpened) {
                this.cameraIcon.setAlpha(0.0f);
            } else {
                checkCameraViewPosition();
            }
            invalidate();
        }
        ZoomControlView zoomControlView = this.zoomControlView;
        if (zoomControlView != null) {
            zoomControlView.setZoom(0.0f, false);
            this.cameraZoom = 0.0f;
        }
        if (this.cameraOpened) {
            return;
        }
        this.cameraView.setTranslationX(this.cameraViewLocation[0]);
        this.cameraView.setTranslationY(this.cameraViewLocation[1] + this.currentPanTranslationY);
        this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
        this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY + this.currentPanTranslationY);
    }

    public void updateAvatarPicker() {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        this.showAvatarConstructor = (chatAttachAlert.avatarPicker == 0 || chatAttachAlert.isPhotoPicker) ? false : true;
    }

    protected void updateCheckedPhotoIndices() {
        if (this.parentAlert.baseFragment instanceof ChatActivity) {
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    MediaController.PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell.getTag()).intValue());
                    if (photoEntryAtPosition != null) {
                        photoAttachPhotoCell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)));
                    }
                }
            }
            int childCount2 = this.cameraPhotoRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt2 = this.cameraPhotoRecyclerView.getChildAt(i2);
                if (childAt2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) childAt2;
                    MediaController.PhotoEntry photoEntryAtPosition2 = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell2.getTag()).intValue());
                    if (photoEntryAtPosition2 != null) {
                        photoAttachPhotoCell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition2.imageId)));
                    }
                }
            }
        }
    }

    protected void updateCheckedPhotos() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.updateCheckedPhotos():void");
    }

    protected void updatePhotosCounter(boolean z) {
        TextView textView;
        String formatPluralString;
        if (this.counterTextView != null) {
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            if (chatAttachAlert.avatarPicker != 0 || chatAttachAlert.storyMediaPicker) {
                return;
            }
            Iterator it = selectedPhotos.entrySet().iterator();
            boolean z2 = false;
            boolean z3 = false;
            while (it.hasNext()) {
                if (((MediaController.PhotoEntry) ((Map.Entry) it.next()).getValue()).isVideo) {
                    z2 = true;
                } else {
                    z3 = true;
                }
                if (z2 && z3) {
                    break;
                }
            }
            int max = Math.max(1, selectedPhotos.size());
            if (z2 && z3) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size(), new Object[0]).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    textView = this.parentAlert.selectedTextView;
                    formatPluralString = LocaleController.formatPluralString("MediaSelected", max, new Object[0]);
                    textView.setText(formatPluralString);
                }
            } else if (z2) {
                this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size(), new Object[0]).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    textView = this.parentAlert.selectedTextView;
                    formatPluralString = LocaleController.formatPluralString("VideosSelected", max, new Object[0]);
                    textView.setText(formatPluralString);
                }
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size(), new Object[0]).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    textView = this.parentAlert.selectedTextView;
                    formatPluralString = LocaleController.formatPluralString("PhotosSelected", max, new Object[0]);
                    textView.setText(formatPluralString);
                }
            }
            this.parentAlert.setCanOpenPreview(max > 1);
            this.currentSelectedCount = max;
        }
    }

    public void updateSelected(HashMap hashMap, ArrayList arrayList, boolean z) {
        selectedPhotos.clear();
        selectedPhotos.putAll(hashMap);
        selectedPhotosOrder.clear();
        selectedPhotosOrder.addAll(arrayList);
        if (z) {
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    int childAdapterPosition = this.gridView.getChildAdapterPosition(childAt);
                    if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry) {
                        childAdapterPosition--;
                    }
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    if (this.parentAlert.avatarPicker != 0) {
                        photoAttachPhotoCell.getCheckBox().setVisibility(8);
                    }
                    MediaController.PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(childAdapterPosition);
                    if (photoEntryAtPosition != null) {
                        photoAttachPhotoCell.setPhotoEntry(photoEntryAtPosition, selectedPhotos.size() > 1, this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry, childAdapterPosition == this.adapter.getItemCount() - 1);
                        ChatAttachAlert chatAttachAlert = this.parentAlert;
                        if ((chatAttachAlert.baseFragment instanceof ChatActivity) && chatAttachAlert.allowOrder) {
                            photoAttachPhotoCell.setChecked(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)), selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), false);
                        } else {
                            photoAttachPhotoCell.setChecked(-1, selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), false);
                        }
                    }
                }
            }
        }
    }
}
