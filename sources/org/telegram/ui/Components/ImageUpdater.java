package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;

public class ImageUpdater implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate {
    private TLRPC.PhotoSize bigPhoto;
    private boolean canSelectVideo;
    private boolean canceled;
    private ChatAttachAlert chatAttachAlert;
    private boolean clearAfterUpdate;
    private MessageObject convertingVideo;
    private float currentImageProgress;
    public String currentPicturePath;
    private ImageUpdaterDelegate delegate;
    private String finalPath;
    private boolean forUser;
    private boolean forceDarkTheme;
    private boolean isVideo;
    private boolean openWithFrontfaceCamera;
    public BaseFragment parentFragment;
    public final int setForType;
    private boolean showingFromDialog;
    private TLRPC.PhotoSize smallPhoto;
    private boolean supportEmojiMarkup;
    private int type;
    private TLRPC.InputFile uploadedPhoto;
    private TLRPC.InputFile uploadedVideo;
    private String uploadingImage;
    private String uploadingVideo;
    private TLRPC.User user;
    private TLRPC.VideoSize vectorMarkup;
    private String videoPath;
    private double videoTimestamp;
    private int currentAccount = UserConfig.selectedAccount;
    private File picturePath = null;
    private boolean useAttachMenu = true;
    private boolean searchAvailable = true;
    private boolean uploadAfterSelect = true;
    private ImageReceiver imageReceiver = new ImageReceiver(null);

    public class AnonymousClass1 implements PhotoPickerActivity.PhotoPickerActivityDelegate {
        private boolean sendPressed;
        final ArrayList val$order;
        final HashMap val$photos;

        AnonymousClass1(HashMap hashMap, ArrayList arrayList) {
            r2 = hashMap;
            r3 = arrayList;
        }

        @Override
        public void actionButtonPressed(boolean z, boolean z2, int i) {
            if (r2.isEmpty() || ImageUpdater.this.delegate == null || this.sendPressed || z) {
                return;
            }
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < r3.size(); i2++) {
                Object obj = r2.get(r3.get(i2));
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                arrayList.add(sendingMediaInfo);
                if (obj instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                    String str = searchImage.imagePath;
                    if (str != null) {
                        sendingMediaInfo.path = str;
                    } else {
                        sendingMediaInfo.searchImage = searchImage;
                    }
                    sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                    sendingMediaInfo.thumbPath = searchImage.thumbPath;
                    CharSequence charSequence = searchImage.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = searchImage.entities;
                    sendingMediaInfo.masks = searchImage.stickers;
                    sendingMediaInfo.ttl = searchImage.ttl;
                }
            }
            ImageUpdater.this.didSelectPhotos(arrayList);
        }

        @Override
        public boolean canFinishFragment() {
            return ImageUpdater.this.delegate.canFinishFragment();
        }

        @Override
        public void onCaptionChanged(CharSequence charSequence) {
        }

        @Override
        public void onOpenInPressed() {
            PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
        }

        @Override
        public void selectedPhotosChanged() {
        }
    }

    public class AnonymousClass2 implements ChatAttachAlert.ChatAttachViewDelegate {
        AnonymousClass2() {
        }

        private void processSelectedAttach(int i) {
            if (i == 0) {
                ImageUpdater.this.openCamera();
            }
        }

        @Override
        public void didPressedButton(int i, boolean z, boolean z2, int i2, long j, boolean z3, boolean z4) {
            BaseFragment baseFragment = ImageUpdater.this.parentFragment;
            if (baseFragment == null || baseFragment.getParentActivity() == null || ImageUpdater.this.chatAttachAlert == null) {
                return;
            }
            if (i != 8 && i != 7) {
                ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(i);
                processSelectedAttach(i);
                return;
            }
            HashMap<Object, Object> selectedPhotos = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
            ArrayList<Object> selectedPhotosOrder = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
            ArrayList arrayList = new ArrayList();
            for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                Object obj = selectedPhotos.get(selectedPhotosOrder.get(i3));
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                arrayList.add(sendingMediaInfo);
                if (obj instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                    String str = photoEntry.imagePath;
                    if (str == null) {
                        str = photoEntry.path;
                    }
                    sendingMediaInfo.path = str;
                    sendingMediaInfo.thumbPath = photoEntry.thumbPath;
                    sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    CharSequence charSequence = photoEntry.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    sendingMediaInfo.masks = photoEntry.stickers;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                    sendingMediaInfo.emojiMarkup = photoEntry.emojiMarkup;
                } else if (obj instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                    String str2 = searchImage.imagePath;
                    if (str2 != null) {
                        sendingMediaInfo.path = str2;
                    } else {
                        sendingMediaInfo.searchImage = searchImage;
                    }
                    sendingMediaInfo.thumbPath = searchImage.thumbPath;
                    sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                    CharSequence charSequence2 = searchImage.caption;
                    sendingMediaInfo.caption = charSequence2 != null ? charSequence2.toString() : null;
                    sendingMediaInfo.entities = searchImage.entities;
                    sendingMediaInfo.masks = searchImage.stickers;
                    sendingMediaInfo.ttl = searchImage.ttl;
                    TLRPC.BotInlineResult botInlineResult = searchImage.inlineResult;
                    if (botInlineResult != null && searchImage.type == 1) {
                        sendingMediaInfo.inlineResult = botInlineResult;
                        sendingMediaInfo.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
            }
            ImageUpdater.this.didSelectPhotos(arrayList);
            if (i != 8) {
                ImageUpdater.this.chatAttachAlert.dismiss(true);
            }
        }

        @Override
        public void didSelectBot(TLRPC.User user) {
        }

        @Override
        public void doOnIdle(Runnable runnable) {
            runnable.run();
        }

        @Override
        public boolean needEnterComment() {
            return false;
        }

        @Override
        public void onCameraOpened() {
            AndroidUtilities.hideKeyboard(ImageUpdater.this.parentFragment.getFragmentView().findFocus());
        }

        @Override
        public void onWallpaperSelected(Object obj) {
            ChatAttachAlert.ChatAttachViewDelegate.CC.$default$onWallpaperSelected(this, obj);
        }

        @Override
        public void openAvatarsSearch() {
            ImageUpdater.this.openSearch();
        }

        @Override
        public boolean selectItemOnClicking() {
            return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$selectItemOnClicking(this);
        }

        @Override
        public void sendAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2) {
            ChatAttachAlert.ChatAttachViewDelegate.CC.$default$sendAudio(this, arrayList, charSequence, z, i, j, z2);
        }
    }

    public class AnonymousClass3 implements PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate {
        AnonymousClass3() {
        }

        @Override
        public void didSelectPhotos(ArrayList arrayList, boolean z, int i) {
            ImageUpdater.this.didSelectPhotos(arrayList);
        }

        @Override
        public void startPhotoSelectActivity() {
            try {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                ImageUpdater.this.parentFragment.startActivityForResult(intent, 14);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public class AnonymousClass4 extends PhotoViewer.EmptyPhotoViewerProvider {
        final ArrayList val$arrayList;

        AnonymousClass4(ArrayList arrayList) {
            r2 = arrayList;
        }

        @Override
        public boolean allowCaption() {
            return false;
        }

        @Override
        public boolean canScrollAway() {
            return false;
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            ImageUpdater.this.processEntry((MediaController.PhotoEntry) r2.get(0));
        }
    }

    public static class AvatarFor {
        public TLRPC.User fromObject;
        public boolean isVideo;
        public final TLObject object;
        public boolean self;
        public final int type;

        public AvatarFor(TLObject tLObject, int i) {
            this.object = tLObject;
            this.type = i;
            this.self = (tLObject instanceof TLRPC.User) && ((TLRPC.User) tLObject).self;
        }
    }

    public interface ImageUpdaterDelegate {

        public abstract class CC {
            public static boolean $default$canFinishFragment(ImageUpdaterDelegate imageUpdaterDelegate) {
                return true;
            }

            public static void $default$didStartUpload(ImageUpdaterDelegate imageUpdaterDelegate, boolean z) {
            }

            public static void $default$didUploadFailed(ImageUpdaterDelegate imageUpdaterDelegate) {
            }

            public static String $default$getInitialSearchString(ImageUpdaterDelegate imageUpdaterDelegate) {
                return null;
            }

            public static void $default$onUploadProgressChanged(ImageUpdaterDelegate imageUpdaterDelegate, float f) {
            }
        }

        boolean canFinishFragment();

        void didStartUpload(boolean z);

        void didUploadFailed();

        void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, double d, String str, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2, boolean z, TLRPC.VideoSize videoSize);

        String getInitialSearchString();

        void onUploadProgressChanged(float f);
    }

    public ImageUpdater(boolean z, int i, boolean z2) {
        this.canSelectVideo = z;
        this.supportEmojiMarkup = z2;
        this.setForType = i;
    }

    private void cleanup() {
        this.uploadingImage = null;
        this.uploadingVideo = null;
        this.videoPath = null;
        this.convertingVideo = null;
        if (this.clearAfterUpdate) {
            this.imageReceiver.setImageBitmap((Drawable) null);
            this.parentFragment = null;
            this.delegate = null;
        }
    }

    private void createChatAttachView() {
        TextView selectedTextView;
        String formatString;
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert = new ChatAttachAlert(this.parentFragment.getParentActivity(), this.parentFragment, this.forceDarkTheme, this.showingFromDialog);
            this.chatAttachAlert = chatAttachAlert;
            chatAttachAlert.setAvatarPicker(this.canSelectVideo ? 2 : 1, this.searchAvailable);
            this.chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
                AnonymousClass2() {
                }

                private void processSelectedAttach(int i) {
                    if (i == 0) {
                        ImageUpdater.this.openCamera();
                    }
                }

                @Override
                public void didPressedButton(int i, boolean z, boolean z2, int i2, long j, boolean z3, boolean z4) {
                    BaseFragment baseFragment2 = ImageUpdater.this.parentFragment;
                    if (baseFragment2 == null || baseFragment2.getParentActivity() == null || ImageUpdater.this.chatAttachAlert == null) {
                        return;
                    }
                    if (i != 8 && i != 7) {
                        ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(i);
                        processSelectedAttach(i);
                        return;
                    }
                    HashMap<Object, Object> selectedPhotos = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                    ArrayList<Object> selectedPhotosOrder = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                    ArrayList arrayList = new ArrayList();
                    for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                        Object obj = selectedPhotos.get(selectedPhotosOrder.get(i3));
                        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                        arrayList.add(sendingMediaInfo);
                        if (obj instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                            String str = photoEntry.imagePath;
                            if (str == null) {
                                str = photoEntry.path;
                            }
                            sendingMediaInfo.path = str;
                            sendingMediaInfo.thumbPath = photoEntry.thumbPath;
                            sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                            sendingMediaInfo.isVideo = photoEntry.isVideo;
                            CharSequence charSequence = photoEntry.caption;
                            sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                            sendingMediaInfo.entities = photoEntry.entities;
                            sendingMediaInfo.masks = photoEntry.stickers;
                            sendingMediaInfo.ttl = photoEntry.ttl;
                            sendingMediaInfo.emojiMarkup = photoEntry.emojiMarkup;
                        } else if (obj instanceof MediaController.SearchImage) {
                            MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                            String str2 = searchImage.imagePath;
                            if (str2 != null) {
                                sendingMediaInfo.path = str2;
                            } else {
                                sendingMediaInfo.searchImage = searchImage;
                            }
                            sendingMediaInfo.thumbPath = searchImage.thumbPath;
                            sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                            CharSequence charSequence2 = searchImage.caption;
                            sendingMediaInfo.caption = charSequence2 != null ? charSequence2.toString() : null;
                            sendingMediaInfo.entities = searchImage.entities;
                            sendingMediaInfo.masks = searchImage.stickers;
                            sendingMediaInfo.ttl = searchImage.ttl;
                            TLRPC.BotInlineResult botInlineResult = searchImage.inlineResult;
                            if (botInlineResult != null && searchImage.type == 1) {
                                sendingMediaInfo.inlineResult = botInlineResult;
                                sendingMediaInfo.params = searchImage.params;
                            }
                            searchImage.date = (int) (System.currentTimeMillis() / 1000);
                        }
                    }
                    ImageUpdater.this.didSelectPhotos(arrayList);
                    if (i != 8) {
                        ImageUpdater.this.chatAttachAlert.dismiss(true);
                    }
                }

                @Override
                public void didSelectBot(TLRPC.User user) {
                }

                @Override
                public void doOnIdle(Runnable runnable) {
                    runnable.run();
                }

                @Override
                public boolean needEnterComment() {
                    return false;
                }

                @Override
                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(ImageUpdater.this.parentFragment.getFragmentView().findFocus());
                }

                @Override
                public void onWallpaperSelected(Object obj) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$onWallpaperSelected(this, obj);
                }

                @Override
                public void openAvatarsSearch() {
                    ImageUpdater.this.openSearch();
                }

                @Override
                public boolean selectItemOnClicking() {
                    return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$selectItemOnClicking(this);
                }

                @Override
                public void sendAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$sendAudio(this, arrayList, charSequence, z, i, j, z2);
                }
            });
            this.chatAttachAlert.setImageUpdater(this);
        }
        int i = this.type;
        if (i == 1) {
            selectedTextView = this.chatAttachAlert.getSelectedTextView();
            formatString = LocaleController.formatString("SetPhotoFor", R.string.SetPhotoFor, this.user.first_name);
        } else {
            if (i != 2) {
                return;
            }
            selectedTextView = this.chatAttachAlert.getSelectedTextView();
            formatString = LocaleController.formatString("SuggestPhotoFor", R.string.SuggestPhotoFor, this.user.first_name);
        }
        selectedTextView.setText(formatString);
    }

    public void didSelectPhotos(ArrayList arrayList) {
        MessageObject messageObject;
        File file;
        Bitmap loadBitmap;
        if (arrayList.isEmpty()) {
            return;
        }
        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = (SendMessagesHelper.SendingMediaInfo) arrayList.get(0);
        Bitmap bitmap = null;
        if (sendingMediaInfo.isVideo || sendingMediaInfo.videoEditedInfo != null) {
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.id = 0;
            tL_message.message = "";
            tL_message.media = new TLRPC.TL_messageMediaEmpty();
            tL_message.action = new TLRPC.TL_messageActionEmpty();
            tL_message.dialog_id = 0L;
            messageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false, false);
            messageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
            messageObject.videoEditedInfo = sendingMediaInfo.videoEditedInfo;
            messageObject.emojiMarkup = sendingMediaInfo.emojiMarkup;
            bitmap = ImageLoader.loadBitmap(sendingMediaInfo.thumbPath, null, 800.0f, 800.0f, true);
        } else {
            String str = sendingMediaInfo.path;
            if (str != null) {
                loadBitmap = ImageLoader.loadBitmap(str, null, 800.0f, 800.0f, true);
            } else {
                MediaController.SearchImage searchImage = sendingMediaInfo.searchImage;
                if (searchImage != null) {
                    TLRPC.Photo photo = searchImage.photo;
                    if (photo != null) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize != null) {
                            file = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true);
                            this.finalPath = file.getAbsolutePath();
                            if (!file.exists()) {
                                file = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, false);
                                if (!file.exists()) {
                                    file = null;
                                }
                            }
                            if (file == null) {
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
                                this.uploadingImage = FileLoader.getAttachFileName(closestPhotoSizeWithSize.location);
                                this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, sendingMediaInfo.searchImage.photo), null, null, "jpg", null, 1);
                            }
                            loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 800.0f, 800.0f, true);
                        }
                    } else if (searchImage.imageUrl != null) {
                        File file2 = new File(FileLoader.getDirectory(4), Utilities.MD5(sendingMediaInfo.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(sendingMediaInfo.searchImage.imageUrl, "jpg"));
                        this.finalPath = file2.getAbsolutePath();
                        if (!file2.exists() || file2.length() == 0) {
                            this.uploadingImage = sendingMediaInfo.searchImage.imageUrl;
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                            this.imageReceiver.setImage(sendingMediaInfo.searchImage.imageUrl, null, null, "jpg", 1L);
                        } else {
                            file = file2;
                            loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 800.0f, 800.0f, true);
                        }
                    }
                }
                messageObject = null;
            }
            messageObject = null;
            bitmap = loadBitmap;
        }
        processBitmap(bitmap, messageObject);
    }

    public void lambda$openMenu$0(ArrayList arrayList, Runnable runnable, DialogInterface dialogInterface, int i) {
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 0) {
            openCamera();
            return;
        }
        if (intValue == 1) {
            openGallery();
            return;
        }
        if (intValue == 2) {
            openSearch();
        } else if (intValue == 3) {
            runnable.run();
        } else {
            if (intValue != 4) {
                return;
            }
            openVideoCamera();
        }
    }

    public void lambda$startCrop$1(String str, Uri uri) {
        try {
            LaunchActivity launchActivity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (launchActivity == null) {
                return;
            }
            Bundle bundle = new Bundle();
            if (str != null) {
                bundle.putString("photoPath", str);
            } else if (uri != null) {
                bundle.putParcelable("photoUri", uri);
            }
            PhotoCropActivity photoCropActivity = new PhotoCropActivity(bundle);
            photoCropActivity.setDelegate(this);
            launchActivity.lambda$runLinkRequest$93(photoCropActivity);
        } catch (Exception e) {
            FileLog.e(e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true), null);
        }
    }

    private void openAttachMenu(DialogInterface.OnDismissListener onDismissListener) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        createChatAttachView();
        this.chatAttachAlert.setOpenWithFrontFaceCamera(this.openWithFrontfaceCamera);
        this.chatAttachAlert.setMaxSelectedPhotos(1, false);
        this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
        int i = Build.VERSION.SDK_INT;
        if (i == 21 || i == 22) {
            AndroidUtilities.hideKeyboard(this.parentFragment.getFragmentView().findFocus());
        }
        this.chatAttachAlert.init();
        this.chatAttachAlert.setOnHideListener(onDismissListener);
        int i2 = this.type;
        if (i2 != 0) {
            this.chatAttachAlert.avatarFor(new AvatarFor(this.user, i2));
        }
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        chatAttachAlert.forUser = this.forUser;
        this.parentFragment.showDialog(chatAttachAlert);
    }

    private void processBitmap(Bitmap bitmap, MessageObject messageObject) {
        if (bitmap == null) {
            return;
        }
        this.uploadedVideo = null;
        this.uploadedPhoto = null;
        this.convertingVideo = null;
        this.videoPath = null;
        this.vectorMarkup = messageObject == null ? null : messageObject.emojiMarkup;
        this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
        TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
        this.smallPhoto = scaleAndSaveImage;
        if (scaleAndSaveImage != null) {
            try {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath())), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
            } catch (Throwable unused) {
            }
        }
        bitmap.recycle();
        if (this.bigPhoto != null) {
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
            if (this.uploadAfterSelect) {
                if (messageObject == null || messageObject.videoEditedInfo == null) {
                    ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                    if (imageUpdaterDelegate != null) {
                        imageUpdaterDelegate.didStartUpload(false);
                    }
                    this.isVideo = false;
                } else {
                    if (this.supportEmojiMarkup && !MessagesController.getInstance(this.currentAccount).uploadMarkupVideo) {
                        ImageUpdaterDelegate imageUpdaterDelegate2 = this.delegate;
                        if (imageUpdaterDelegate2 != null) {
                            imageUpdaterDelegate2.didStartUpload(true);
                        }
                        ImageUpdaterDelegate imageUpdaterDelegate3 = this.delegate;
                        if (imageUpdaterDelegate3 != null) {
                            imageUpdaterDelegate3.didUploadPhoto(null, null, 0.0d, null, this.bigPhoto, this.smallPhoto, this.isVideo, null);
                            this.delegate.didUploadPhoto(null, null, this.videoTimestamp, this.videoPath, this.bigPhoto, this.smallPhoto, this.isVideo, this.vectorMarkup);
                            cleanup();
                            return;
                        }
                        return;
                    }
                    this.convertingVideo = messageObject;
                    VideoEditedInfo videoEditedInfo = messageObject.videoEditedInfo;
                    long j = videoEditedInfo.startTime;
                    if (j < 0) {
                        j = 0;
                    }
                    double d = videoEditedInfo.avatarStartTime - j;
                    Double.isNaN(d);
                    this.videoTimestamp = d / 1000000.0d;
                    videoEditedInfo.shouldLimitFps = false;
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                    MediaController.getInstance().scheduleVideoConvert(messageObject, true, true, false);
                    this.uploadingImage = null;
                    ImageUpdaterDelegate imageUpdaterDelegate4 = this.delegate;
                    if (imageUpdaterDelegate4 != null) {
                        imageUpdaterDelegate4.didStartUpload(true);
                    }
                    this.isVideo = true;
                }
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
                if (this.uploadingImage != null) {
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                }
            }
            ImageUpdaterDelegate imageUpdaterDelegate5 = this.delegate;
            if (imageUpdaterDelegate5 != null) {
                imageUpdaterDelegate5.didUploadPhoto(null, null, 0.0d, null, this.bigPhoto, this.smallPhoto, this.isVideo, null);
            }
        }
    }

    private void startCrop(final String str, final Uri uri) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ImageUpdater.this.lambda$startCrop$1(str, uri);
            }
        });
    }

    public void cancel() {
        this.canceled = true;
        if (this.uploadingImage != null) {
            FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.uploadingImage, false);
        }
        if (this.uploadingVideo != null) {
            FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.uploadingVideo, false);
        }
        ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
        if (imageUpdaterDelegate != null) {
            imageUpdaterDelegate.didUploadFailed();
        }
    }

    public void clear() {
        this.canceled = false;
        if (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) {
            this.parentFragment = null;
            this.delegate = null;
        } else {
            this.clearAfterUpdate = true;
        }
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
    }

    @Override
    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap, null);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ImageUpdaterDelegate imageUpdaterDelegate;
        BaseFragment baseFragment;
        int i3 = NotificationCenter.fileUploaded;
        if (i == i3 || i == NotificationCenter.fileUploadFailed) {
            String str = (String) objArr[0];
            if (str.equals(this.uploadingImage)) {
                this.uploadingImage = null;
                if (i == i3) {
                    this.uploadedPhoto = (TLRPC.InputFile) objArr[1];
                }
            } else {
                if (!str.equals(this.uploadingVideo)) {
                    return;
                }
                this.uploadingVideo = null;
                if (i == i3) {
                    this.uploadedVideo = (TLRPC.InputFile) objArr[1];
                }
            }
            if (this.uploadingImage != null || this.uploadingVideo != null || this.convertingVideo != null) {
                return;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i3);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
            if (i == i3 && (imageUpdaterDelegate = this.delegate) != null) {
                imageUpdaterDelegate.didUploadPhoto(this.uploadedPhoto, this.uploadedVideo, this.videoTimestamp, this.videoPath, this.bigPhoto, this.smallPhoto, this.isVideo, this.vectorMarkup);
            }
        } else {
            if (i == NotificationCenter.fileUploadProgressChanged) {
                String str2 = (String) objArr[0];
                String str3 = this.convertingVideo != null ? this.uploadingVideo : this.uploadingImage;
                if (this.delegate == null || !str2.equals(str3)) {
                    return;
                }
                float min = Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue()));
                ImageUpdaterDelegate imageUpdaterDelegate2 = this.delegate;
                this.currentImageProgress = min;
                imageUpdaterDelegate2.onUploadProgressChanged(min);
                return;
            }
            int i4 = NotificationCenter.fileLoaded;
            if (i == i4 || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidLoad || i == NotificationCenter.httpFileDidFailedLoad) {
                String str4 = (String) objArr[0];
                this.currentImageProgress = 1.0f;
                if (str4.equals(this.uploadingImage)) {
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i4);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
                    NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                    int i5 = NotificationCenter.httpFileDidLoad;
                    notificationCenter.removeObserver(this, i5);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                    this.uploadingImage = null;
                    if (i == i4 || i == i5) {
                        processBitmap(ImageLoader.loadBitmap(this.finalPath, null, 800.0f, 800.0f, true), null);
                        return;
                    }
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    ImageUpdaterDelegate imageUpdaterDelegate3 = this.delegate;
                    if (imageUpdaterDelegate3 != null) {
                        imageUpdaterDelegate3.didUploadFailed();
                        return;
                    }
                    return;
                }
                return;
            }
            int i6 = NotificationCenter.filePreparingFailed;
            if (i != i6) {
                if (i != NotificationCenter.fileNewChunkAvailable) {
                    if (i == NotificationCenter.filePreparingStarted && ((MessageObject) objArr[0]) == this.convertingVideo && (baseFragment = this.parentFragment) != null) {
                        this.uploadingVideo = (String) objArr[1];
                        baseFragment.getFileLoader().uploadFile(this.uploadingVideo, false, false, (int) this.convertingVideo.videoEditedInfo.estimatedSize, 33554432, false);
                        return;
                    }
                    return;
                }
                if (((MessageObject) objArr[0]) != this.convertingVideo || this.parentFragment == null) {
                    return;
                }
                String str5 = (String) objArr[1];
                long longValue = ((Long) objArr[2]).longValue();
                long longValue2 = ((Long) objArr[3]).longValue();
                this.parentFragment.getFileLoader().checkUploadNewDataAvailable(str5, false, longValue, longValue2);
                if (longValue2 != 0) {
                    double longValue3 = ((Long) objArr[5]).longValue();
                    Double.isNaN(longValue3);
                    double d = longValue3 / 1000000.0d;
                    if (this.videoTimestamp > d) {
                        this.videoTimestamp = d;
                    }
                    Bitmap createVideoThumbnailAtTime = SendMessagesHelper.createVideoThumbnailAtTime(str5, (long) (this.videoTimestamp * 1000.0d), null, true);
                    if (createVideoThumbnailAtTime != null) {
                        File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true);
                        if (pathToAttach != null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("delete file " + pathToAttach);
                            }
                            pathToAttach.delete();
                        }
                        File pathToAttach2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.bigPhoto, true);
                        if (pathToAttach2 != null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("delete file " + pathToAttach2);
                            }
                            pathToAttach2.delete();
                        }
                        this.bigPhoto = ImageLoader.scaleAndSaveImage(createVideoThumbnailAtTime, 800.0f, 800.0f, 80, false, 320, 320);
                        TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(createVideoThumbnailAtTime, 150.0f, 150.0f, 80, false, 150, 150);
                        this.smallPhoto = scaleAndSaveImage;
                        if (scaleAndSaveImage != null) {
                            try {
                                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath())), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
                            } catch (Throwable unused) {
                            }
                        }
                    }
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                    this.videoPath = str5;
                    this.uploadingVideo = str5;
                    this.convertingVideo = null;
                    return;
                }
                return;
            }
            if (((MessageObject) objArr[0]) != this.convertingVideo || this.parentFragment == null) {
                return;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i6);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        }
        cleanup();
    }

    public boolean dismissCurrentDialog(Dialog dialog) {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert == null || dialog != chatAttachAlert) {
            return false;
        }
        chatAttachAlert.getPhotoLayout().closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.getPhotoLayout().hideCamera(true);
        return true;
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert;
    }

    public float getCurrentImageProgress() {
        return this.currentImageProgress;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public boolean isUploadingImage() {
        return (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) ? false : true;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            if (i == 0 || i == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    chatAttachAlert.onActivityResultFragment(i, intent, this.currentPicturePath);
                }
            } else {
                if (i == 13) {
                    this.parentFragment.getParentActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                    openPhotoForEdit(this.currentPicturePath, (String) null, (Pair) AndroidUtilities.getImageOrientation(this.currentPicturePath), false);
                } else {
                    if (i == 14) {
                        if (intent == null || intent.getData() == null) {
                            return;
                        }
                        startCrop(null, intent.getData());
                        return;
                    }
                    if (i != 15) {
                        return;
                    } else {
                        openPhotoForEdit(this.currentPicturePath, (String) null, 0, true);
                    }
                }
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            }
            this.currentPicturePath = null;
        }
    }

    public void onPause() {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlertPhotoLayout photoLayout;
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            if (i == 17) {
                chatAttachAlert.getPhotoLayout().checkCamera(false);
                photoLayout = this.chatAttachAlert.getPhotoLayout();
            } else if (i != 4) {
                return;
            } else {
                photoLayout = chatAttachAlert.getPhotoLayout();
            }
            photoLayout.checkStorage();
        }
    }

    public void onResume() {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onResume();
        }
    }

    public void openCamera() {
        int checkSelfPermission;
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        try {
            int i = Build.VERSION.SDK_INT;
            if (i >= 23) {
                checkSelfPermission = this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA");
                if (checkSelfPermission != 0) {
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
                    return;
                }
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File generatePicturePath = AndroidUtilities.generatePicturePath();
            if (generatePicturePath != null) {
                if (i >= 24) {
                    intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                    intent.addFlags(2);
                    intent.addFlags(1);
                } else {
                    intent.putExtra("output", Uri.fromFile(generatePicturePath));
                }
                this.currentPicturePath = generatePicturePath.getAbsolutePath();
            }
            this.parentFragment.startActivityForResult(intent, 13);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void openGallery() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ImageUpdater.openGallery():void");
    }

    public void openMenu(boolean z, final Runnable runnable, DialogInterface.OnDismissListener onDismissListener, int i) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        this.canceled = false;
        this.type = i;
        if (this.useAttachMenu) {
            openAttachMenu(onDismissListener);
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
        builder.setTitle(i == 1 ? LocaleController.formatString("SetPhotoFor", R.string.SetPhotoFor, this.user.first_name) : i == 2 ? LocaleController.formatString("SuggestPhotoFor", R.string.SuggestPhotoFor, this.user.first_name) : LocaleController.getString(R.string.ChoosePhoto), true);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        arrayList.add(LocaleController.getString(R.string.ChooseTakePhoto));
        arrayList2.add(Integer.valueOf(R.drawable.msg_camera));
        arrayList3.add(0);
        if (this.canSelectVideo) {
            arrayList.add(LocaleController.getString(R.string.ChooseRecordVideo));
            arrayList2.add(Integer.valueOf(R.drawable.msg_video));
            arrayList3.add(4);
        }
        arrayList.add(LocaleController.getString(R.string.ChooseFromGallery));
        arrayList2.add(Integer.valueOf(R.drawable.msg_photos));
        arrayList3.add(1);
        if (this.searchAvailable) {
            arrayList.add(LocaleController.getString(R.string.ChooseFromSearch));
            arrayList2.add(Integer.valueOf(R.drawable.msg_search));
            arrayList3.add(2);
        }
        if (z) {
            arrayList.add(LocaleController.getString(R.string.DeletePhoto));
            arrayList2.add(Integer.valueOf(R.drawable.msg_delete));
            arrayList3.add(3);
        }
        int[] iArr = new int[arrayList2.size()];
        int size = arrayList2.size();
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = ((Integer) arrayList2.get(i2)).intValue();
        }
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                ImageUpdater.this.lambda$openMenu$0(arrayList3, runnable, dialogInterface, i3);
            }
        });
        BottomSheet create = builder.create();
        create.setOnHideListener(onDismissListener);
        this.parentFragment.showDialog(create);
        if (z) {
            create.setItemColor(arrayList.size() - 1, Theme.getColor(Theme.key_text_RedBold), Theme.getColor(Theme.key_text_RedRegular));
        }
    }

    public void openPhotoForEdit(String str, String str2, int i, boolean z) {
        openPhotoForEdit(str, str2, new Pair(Integer.valueOf(i), 0), z);
    }

    public void openPhotoForEdit(String str, String str2, Pair pair, boolean z) {
        ArrayList arrayList = new ArrayList();
        MediaController.PhotoEntry orientation = new MediaController.PhotoEntry(0, 0, 0L, str, ((Integer) pair.first).intValue(), false, 0, 0, 0L).setOrientation(pair);
        orientation.isVideo = z;
        orientation.thumbPath = str2;
        arrayList.add(orientation);
        PhotoViewer.getInstance().setParentActivity(this.parentFragment);
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() {
            final ArrayList val$arrayList;

            AnonymousClass4(ArrayList arrayList2) {
                r2 = arrayList2;
            }

            @Override
            public boolean allowCaption() {
                return false;
            }

            @Override
            public boolean canScrollAway() {
                return false;
            }

            @Override
            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z2, int i2, boolean z22) {
                ImageUpdater.this.processEntry((MediaController.PhotoEntry) r2.get(0));
            }
        }, null);
    }

    public void openSearch() {
        if (this.parentFragment == null) {
            return;
        }
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, null, hashMap, arrayList, 1, false, null, this.forceDarkTheme);
        photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            private boolean sendPressed;
            final ArrayList val$order;
            final HashMap val$photos;

            AnonymousClass1(HashMap hashMap2, ArrayList arrayList2) {
                r2 = hashMap2;
                r3 = arrayList2;
            }

            @Override
            public void actionButtonPressed(boolean z, boolean z2, int i) {
                if (r2.isEmpty() || ImageUpdater.this.delegate == null || this.sendPressed || z) {
                    return;
                }
                this.sendPressed = true;
                ArrayList arrayList2 = new ArrayList();
                for (int i2 = 0; i2 < r3.size(); i2++) {
                    Object obj = r2.get(r3.get(i2));
                    SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                    arrayList2.add(sendingMediaInfo);
                    if (obj instanceof MediaController.SearchImage) {
                        MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                        String str = searchImage.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            sendingMediaInfo.searchImage = searchImage;
                        }
                        sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                        sendingMediaInfo.thumbPath = searchImage.thumbPath;
                        CharSequence charSequence = searchImage.caption;
                        sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                        sendingMediaInfo.entities = searchImage.entities;
                        sendingMediaInfo.masks = searchImage.stickers;
                        sendingMediaInfo.ttl = searchImage.ttl;
                    }
                }
                ImageUpdater.this.didSelectPhotos(arrayList2);
            }

            @Override
            public boolean canFinishFragment() {
                return ImageUpdater.this.delegate.canFinishFragment();
            }

            @Override
            public void onCaptionChanged(CharSequence charSequence) {
            }

            @Override
            public void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            @Override
            public void selectedPhotosChanged() {
            }
        });
        photoPickerActivity.setMaxSelectedPhotos(1, false);
        photoPickerActivity.setInitialSearchString(this.delegate.getInitialSearchString());
        if (this.showingFromDialog) {
            this.parentFragment.showAsSheet(photoPickerActivity);
        } else {
            this.parentFragment.presentFragment(photoPickerActivity);
        }
    }

    public void openVideoCamera() {
        int checkSelfPermission;
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        try {
            int i = Build.VERSION.SDK_INT;
            if (i >= 23) {
                checkSelfPermission = this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA");
                if (checkSelfPermission != 0) {
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
                    return;
                }
            }
            Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
            File generateVideoPath = AndroidUtilities.generateVideoPath();
            if (generateVideoPath != null) {
                if (i >= 24) {
                    intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", generateVideoPath));
                    intent.addFlags(2);
                    intent.addFlags(1);
                } else {
                    intent.putExtra("output", Uri.fromFile(generateVideoPath));
                }
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                intent.putExtra("android.intent.extra.durationLimit", 10);
                this.currentPicturePath = generateVideoPath.getAbsolutePath();
            }
            this.parentFragment.startActivityForResult(intent, 15);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void processEntry(MediaController.PhotoEntry photoEntry) {
        Bitmap loadBitmap;
        String str = photoEntry.imagePath;
        if (str == null) {
            str = photoEntry.path;
        }
        MessageObject messageObject = null;
        if (photoEntry.isVideo || photoEntry.editedInfo != null) {
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.id = 0;
            tL_message.message = "";
            tL_message.media = new TLRPC.TL_messageMediaEmpty();
            tL_message.action = new TLRPC.TL_messageActionEmpty();
            tL_message.dialog_id = 0L;
            MessageObject messageObject2 = new MessageObject(UserConfig.selectedAccount, tL_message, false, false);
            messageObject2.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
            messageObject2.videoEditedInfo = photoEntry.editedInfo;
            messageObject2.emojiMarkup = photoEntry.emojiMarkup;
            loadBitmap = ImageLoader.loadBitmap(photoEntry.thumbPath, null, 800.0f, 800.0f, true);
            messageObject = messageObject2;
        } else {
            loadBitmap = ImageLoader.loadBitmap(str, null, 800.0f, 800.0f, true);
        }
        processBitmap(loadBitmap, messageObject);
    }

    public void setDelegate(ImageUpdaterDelegate imageUpdaterDelegate) {
        this.delegate = imageUpdaterDelegate;
    }

    public void setForceDarkTheme(boolean z) {
        this.forceDarkTheme = z;
    }

    public void setOpenWithFrontfaceCamera(boolean z) {
        this.openWithFrontfaceCamera = z;
    }

    public void setSearchAvailable(boolean z) {
        this.searchAvailable = z;
        this.useAttachMenu = z;
    }

    public void setSearchAvailable(boolean z, boolean z2) {
        this.useAttachMenu = z2;
        this.searchAvailable = z;
    }

    public void setShowingFromDialog(boolean z) {
        this.showingFromDialog = z;
    }

    public void setUploadAfterSelect(boolean z) {
        this.uploadAfterSelect = z;
    }

    public void setUser(TLRPC.User user) {
        this.user = user;
    }

    public void showAvatarConstructor(TLRPC.VideoSize videoSize) {
        createChatAttachView();
        this.chatAttachAlert.getPhotoLayout().showAvatarConstructorFragment(null, videoSize);
    }
}
