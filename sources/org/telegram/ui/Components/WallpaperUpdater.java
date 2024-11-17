package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.PhotoAlbumPickerActivity;

public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private File picturePath = null;

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap, boolean z);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, BaseFragment baseFragment, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.parentFragment = baseFragment;
        this.delegate = wallpaperUpdaterDelegate;
    }

    public void didSelectPhotos(ArrayList arrayList) {
        try {
            if (arrayList.isEmpty()) {
                return;
            }
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = (SendMessagesHelper.SendingMediaInfo) arrayList.get(0);
            if (sendingMediaInfo.path != null) {
                this.currentWallpaperPath = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
                android.graphics.Point realScreenSize = AndroidUtilities.getRealScreenSize();
                Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
                loadBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                this.delegate.didSelectWallpaper(this.currentWallpaperPath, loadBitmap, true);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void lambda$showAlert$0(boolean z, DialogInterface dialogInterface, int i) {
        try {
            if (i != 0) {
                if (i == 1) {
                    openGallery();
                    return;
                }
                if (z) {
                    if (i == 2) {
                        this.delegate.needOpenColorPicker();
                        return;
                    } else {
                        if (i == 3) {
                            this.delegate.didSelectWallpaper(null, null, false);
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File generatePicturePath = AndroidUtilities.generatePicturePath();
                if (generatePicturePath != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        intent.putExtra("output", FileProvider.getUriForFile(this.parentActivity, ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                        intent.addFlags(2);
                        intent.addFlags(1);
                    } else {
                        intent.putExtra("output", Uri.fromFile(generatePicturePath));
                    }
                    this.currentPicturePath = generatePicturePath.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(intent, 10);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void cleanup() {
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void onActivityResult(int r8, int r9, android.content.Intent r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WallpaperUpdater.onActivityResult(int, int, android.content.Intent):void");
    }

    public void openGallery() {
        int checkSelfPermission;
        int checkSelfPermission2;
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null) {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            this.parentActivity.startActivityForResult(intent, 11);
            return;
        }
        Activity parentActivity = baseFragment.getParentActivity();
        if (parentActivity != null) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 33) {
                checkSelfPermission2 = parentActivity.checkSelfPermission("android.permission.READ_MEDIA_IMAGES");
                if (checkSelfPermission2 != 0) {
                    parentActivity.requestPermissions(new String[]{"android.permission.READ_MEDIA_IMAGES"}, 4);
                    return;
                }
            } else if (i >= 23) {
                checkSelfPermission = parentActivity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE");
                if (checkSelfPermission != 0) {
                    parentActivity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                    return;
                }
            }
        }
        PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER, false, false, null);
        photoAlbumPickerActivity.setAllowSearchImages(false);
        photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
            @Override
            public void didSelectPhotos(ArrayList arrayList, boolean z, int i2) {
                WallpaperUpdater.this.didSelectPhotos(arrayList);
            }

            @Override
            public void startPhotoSelectActivity() {
                try {
                    Intent intent2 = new Intent("android.intent.action.PICK");
                    intent2.setType("image/*");
                    WallpaperUpdater.this.parentActivity.startActivityForResult(intent2, 11);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        });
        this.parentFragment.presentFragment(photoAlbumPickerActivity);
    }

    public void setCurrentPicturePath(String str) {
        this.currentPicturePath = str;
    }

    public void showAlert(final boolean z) {
        CharSequence[] charSequenceArr;
        int[] iArr;
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString(R.string.ChoosePhoto), true);
        if (z) {
            charSequenceArr = new CharSequence[]{LocaleController.getString(R.string.ChooseTakePhoto), LocaleController.getString(R.string.SelectFromGallery), LocaleController.getString(R.string.SelectColor), LocaleController.getString(R.string.Default)};
            iArr = null;
        } else {
            charSequenceArr = new CharSequence[]{LocaleController.getString(R.string.ChooseTakePhoto), LocaleController.getString(R.string.SelectFromGallery)};
            iArr = new int[]{R.drawable.msg_camera, R.drawable.msg_photos};
        }
        builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                WallpaperUpdater.this.lambda$showAlert$0(z, dialogInterface, i);
            }
        });
        builder.show();
    }
}
