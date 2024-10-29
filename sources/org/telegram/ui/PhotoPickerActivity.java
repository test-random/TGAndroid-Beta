package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int alertOnlyOnce;
    private boolean allowCaption;
    private boolean allowIndices;
    private AnimatorSet animatorSet;
    private CharSequence caption;
    private ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    private PhotoPickerActivityDelegate delegate;
    private final int dialogBackgroundKey;
    private StickerEmptyView emptyView;
    private FlickerLoadingView flickerView;
    private final boolean forceDarckTheme;
    protected FrameLayout frameLayout2;
    private int imageReqId;
    private String initialSearchString;
    private boolean isDocumentsPicker;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private String lastSearchImageString;
    private String lastSearchString;
    private int lastSearchToken;
    private GridLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private boolean listSort;
    private RecyclerListView listView;
    private int maxSelectedPhotos;
    private String nextImagesSearchOffset;
    private PhotoPickerActivitySearchDelegate searchDelegate;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private boolean searchingUser;
    private int selectPhotoType;
    private MediaController.AlbumEntry selectedAlbum;
    protected View selectedCountView;
    private HashMap selectedPhotos;
    private ArrayList selectedPhotosOrder;
    private final int selectorKey;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    protected View shadow;
    private boolean shouldSelect;
    private ActionBarMenuSubItem showAsListItem;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private final int textKey;
    private int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;
    private ArrayList searchResult = new ArrayList();
    private HashMap searchResultKeys = new HashMap();
    private HashMap searchResultUrls = new HashMap();
    private ArrayList recentSearches = new ArrayList();
    private boolean imageSearchEndReached = true;
    private boolean allowOrder = true;
    private int itemSize = 100;
    private int itemsPerRow = 3;
    private TextPaint textPaint = new TextPaint(1);
    private RectF rect = new RectF();
    private Paint paint = new Paint(1);
    private boolean needsBottomLayout = true;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        AnonymousClass1() {
        }

        @Override
        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        @Override
        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true, true, 0);
            PhotoPickerActivity.this.lambda$onBackPressed$319();
            return true;
        }

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
            PhotoAttachPhotoCell cellForIndex = PhotoPickerActivity.this.getCellForIndex(i);
            if (cellForIndex == null) {
                return null;
            }
            BackupImageView imageView = cellForIndex.getImageView();
            int[] iArr = new int[2];
            imageView.getLocationInWindow(iArr);
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            placeProviderObject.parentView = PhotoPickerActivity.this.listView;
            ImageReceiver imageReceiver = imageView.getImageReceiver();
            placeProviderObject.imageReceiver = imageReceiver;
            placeProviderObject.thumb = imageReceiver.getBitmapSafe();
            placeProviderObject.scale = cellForIndex.getScale();
            cellForIndex.showCheck(false);
            return placeProviderObject;
        }

        @Override
        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        @Override
        public HashMap getSelectedPhotos() {
            return PhotoPickerActivity.this.selectedPhotos;
        }

        @Override
        public ArrayList getSelectedPhotosOrder() {
            return PhotoPickerActivity.this.selectedPhotosOrder;
        }

        @Override
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell cellForIndex = PhotoPickerActivity.this.getCellForIndex(i);
            if (cellForIndex != null) {
                return cellForIndex.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        @Override
        public boolean isPhotoChecked(int i) {
            return PhotoPickerActivity.this.selectedAlbum != null ? i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(i).imageId)) : i >= 0 && i < PhotoPickerActivity.this.searchResult.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i)).id);
        }

        @Override
        public boolean scaleToFill() {
            return false;
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            MediaController.PhotoEntry photoEntry;
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (i < 0 || i >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        return;
                    } else {
                        photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    }
                } else if (i < 0 || i >= PhotoPickerActivity.this.searchResult.size()) {
                    return;
                } else {
                    photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                }
                photoEntry.editedInfo = videoEditedInfo;
                PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
            }
            PhotoPickerActivity.this.sendSelectedPhotos(z, i2);
        }

        @Override
        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            int addToSelectedPhotos;
            ArrayList arrayList;
            Object obj;
            MediaController.SearchImage searchImage;
            int i2;
            boolean z;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (i < 0 || i >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    return -1;
                }
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                addToSelectedPhotos = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                searchImage = photoEntry;
                if (addToSelectedPhotos == -1) {
                    photoEntry.editedInfo = videoEditedInfo;
                    arrayList = PhotoPickerActivity.this.selectedPhotosOrder;
                    obj = Integer.valueOf(photoEntry.imageId);
                    i2 = arrayList.indexOf(obj);
                    z = true;
                }
                searchImage.editedInfo = null;
                i2 = addToSelectedPhotos;
                z = false;
            } else {
                if (i < 0 || i >= PhotoPickerActivity.this.searchResult.size()) {
                    return -1;
                }
                MediaController.SearchImage searchImage2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                addToSelectedPhotos = PhotoPickerActivity.this.addToSelectedPhotos(searchImage2, -1);
                searchImage = searchImage2;
                if (addToSelectedPhotos == -1) {
                    searchImage2.editedInfo = videoEditedInfo;
                    arrayList = PhotoPickerActivity.this.selectedPhotosOrder;
                    obj = searchImage2.id;
                    i2 = arrayList.indexOf(obj);
                    z = true;
                }
                searchImage.editedInfo = null;
                i2 = addToSelectedPhotos;
                z = false;
            }
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i3);
                if (((Integer) childAt.getTag()).intValue() == i) {
                    ((PhotoAttachPhotoCell) childAt).setChecked(PhotoPickerActivity.this.allowIndices ? i2 : -1, z, false);
                } else {
                    i3++;
                }
            }
            PhotoPickerActivity.this.updatePhotosButton(z ? 1 : 2);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return i2;
        }

        @Override
        public int setPhotoUnchecked(Object obj) {
            Object valueOf = obj instanceof MediaController.PhotoEntry ? Integer.valueOf(((MediaController.PhotoEntry) obj).imageId) : obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).id : null;
            if (valueOf == null || !PhotoPickerActivity.this.selectedPhotos.containsKey(valueOf)) {
                return -1;
            }
            PhotoPickerActivity.this.selectedPhotos.remove(valueOf);
            int indexOf = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(valueOf);
            if (indexOf >= 0) {
                PhotoPickerActivity.this.selectedPhotosOrder.remove(indexOf);
            }
            if (PhotoPickerActivity.this.allowIndices) {
                PhotoPickerActivity.this.updateCheckedPhotoIndices();
            }
            return indexOf;
        }

        @Override
        public void updatePhotoAtIndex(int i) {
            StringBuilder sb;
            String str;
            PhotoAttachPhotoCell cellForIndex = PhotoPickerActivity.this.getCellForIndex(i);
            if (cellForIndex != null) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    cellForIndex.setPhotoEntry((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i), true, false);
                    return;
                }
                BackupImageView imageView = cellForIndex.getImageView();
                imageView.setOrientation(0, true);
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                String str2 = photoEntry.thumbPath;
                if (str2 != null) {
                    imageView.setImage(str2, null, Theme.chat_attachEmptyDrawable);
                    return;
                }
                if (photoEntry.path == null) {
                    imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                    return;
                }
                imageView.setOrientation(photoEntry.orientation, photoEntry.invert, true);
                if (photoEntry.isVideo) {
                    sb = new StringBuilder();
                    str = "vthumb://";
                } else {
                    sb = new StringBuilder();
                    str = "thumb://";
                }
                sb.append(str);
                sb.append(photoEntry.imageId);
                sb.append(":");
                sb.append(photoEntry.path);
                imageView.setImage(sb.toString(), null, Theme.chat_attachEmptyDrawable);
            }
        }

        @Override
        public void willHidePhotoViewer() {
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) childAt).showCheck(true);
                }
            }
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i2);
                if (childAt.getTag() != null) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    int intValue = ((Integer) childAt.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null ? !(intValue < 0 || intValue >= PhotoPickerActivity.this.searchResult.size()) : !(intValue < 0 || intValue >= PhotoPickerActivity.this.selectedAlbum.photos.size())) {
                        if (intValue == i) {
                            photoAttachPhotoCell.showCheck(true);
                            return;
                        }
                    }
                }
            }
        }
    };

    public class AnonymousClass1 extends PhotoViewer.EmptyPhotoViewerProvider {
        AnonymousClass1() {
        }

        @Override
        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        @Override
        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true, true, 0);
            PhotoPickerActivity.this.lambda$onBackPressed$319();
            return true;
        }

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
            PhotoAttachPhotoCell cellForIndex = PhotoPickerActivity.this.getCellForIndex(i);
            if (cellForIndex == null) {
                return null;
            }
            BackupImageView imageView = cellForIndex.getImageView();
            int[] iArr = new int[2];
            imageView.getLocationInWindow(iArr);
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            placeProviderObject.parentView = PhotoPickerActivity.this.listView;
            ImageReceiver imageReceiver = imageView.getImageReceiver();
            placeProviderObject.imageReceiver = imageReceiver;
            placeProviderObject.thumb = imageReceiver.getBitmapSafe();
            placeProviderObject.scale = cellForIndex.getScale();
            cellForIndex.showCheck(false);
            return placeProviderObject;
        }

        @Override
        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        @Override
        public HashMap getSelectedPhotos() {
            return PhotoPickerActivity.this.selectedPhotos;
        }

        @Override
        public ArrayList getSelectedPhotosOrder() {
            return PhotoPickerActivity.this.selectedPhotosOrder;
        }

        @Override
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell cellForIndex = PhotoPickerActivity.this.getCellForIndex(i);
            if (cellForIndex != null) {
                return cellForIndex.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        @Override
        public boolean isPhotoChecked(int i) {
            return PhotoPickerActivity.this.selectedAlbum != null ? i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(i).imageId)) : i >= 0 && i < PhotoPickerActivity.this.searchResult.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i)).id);
        }

        @Override
        public boolean scaleToFill() {
            return false;
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            MediaController.PhotoEntry photoEntry;
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (i < 0 || i >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        return;
                    } else {
                        photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    }
                } else if (i < 0 || i >= PhotoPickerActivity.this.searchResult.size()) {
                    return;
                } else {
                    photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                }
                photoEntry.editedInfo = videoEditedInfo;
                PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
            }
            PhotoPickerActivity.this.sendSelectedPhotos(z, i2);
        }

        @Override
        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            int addToSelectedPhotos;
            ArrayList arrayList;
            Object obj;
            MediaController.SearchImage searchImage;
            int i2;
            boolean z;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (i < 0 || i >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    return -1;
                }
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                addToSelectedPhotos = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                searchImage = photoEntry;
                if (addToSelectedPhotos == -1) {
                    photoEntry.editedInfo = videoEditedInfo;
                    arrayList = PhotoPickerActivity.this.selectedPhotosOrder;
                    obj = Integer.valueOf(photoEntry.imageId);
                    i2 = arrayList.indexOf(obj);
                    z = true;
                }
                searchImage.editedInfo = null;
                i2 = addToSelectedPhotos;
                z = false;
            } else {
                if (i < 0 || i >= PhotoPickerActivity.this.searchResult.size()) {
                    return -1;
                }
                MediaController.SearchImage searchImage2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                addToSelectedPhotos = PhotoPickerActivity.this.addToSelectedPhotos(searchImage2, -1);
                searchImage = searchImage2;
                if (addToSelectedPhotos == -1) {
                    searchImage2.editedInfo = videoEditedInfo;
                    arrayList = PhotoPickerActivity.this.selectedPhotosOrder;
                    obj = searchImage2.id;
                    i2 = arrayList.indexOf(obj);
                    z = true;
                }
                searchImage.editedInfo = null;
                i2 = addToSelectedPhotos;
                z = false;
            }
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i3);
                if (((Integer) childAt.getTag()).intValue() == i) {
                    ((PhotoAttachPhotoCell) childAt).setChecked(PhotoPickerActivity.this.allowIndices ? i2 : -1, z, false);
                } else {
                    i3++;
                }
            }
            PhotoPickerActivity.this.updatePhotosButton(z ? 1 : 2);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return i2;
        }

        @Override
        public int setPhotoUnchecked(Object obj) {
            Object valueOf = obj instanceof MediaController.PhotoEntry ? Integer.valueOf(((MediaController.PhotoEntry) obj).imageId) : obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).id : null;
            if (valueOf == null || !PhotoPickerActivity.this.selectedPhotos.containsKey(valueOf)) {
                return -1;
            }
            PhotoPickerActivity.this.selectedPhotos.remove(valueOf);
            int indexOf = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(valueOf);
            if (indexOf >= 0) {
                PhotoPickerActivity.this.selectedPhotosOrder.remove(indexOf);
            }
            if (PhotoPickerActivity.this.allowIndices) {
                PhotoPickerActivity.this.updateCheckedPhotoIndices();
            }
            return indexOf;
        }

        @Override
        public void updatePhotoAtIndex(int i) {
            StringBuilder sb;
            String str;
            PhotoAttachPhotoCell cellForIndex = PhotoPickerActivity.this.getCellForIndex(i);
            if (cellForIndex != null) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    cellForIndex.setPhotoEntry((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i), true, false);
                    return;
                }
                BackupImageView imageView = cellForIndex.getImageView();
                imageView.setOrientation(0, true);
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                String str2 = photoEntry.thumbPath;
                if (str2 != null) {
                    imageView.setImage(str2, null, Theme.chat_attachEmptyDrawable);
                    return;
                }
                if (photoEntry.path == null) {
                    imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                    return;
                }
                imageView.setOrientation(photoEntry.orientation, photoEntry.invert, true);
                if (photoEntry.isVideo) {
                    sb = new StringBuilder();
                    str = "vthumb://";
                } else {
                    sb = new StringBuilder();
                    str = "thumb://";
                }
                sb.append(str);
                sb.append(photoEntry.imageId);
                sb.append(":");
                sb.append(photoEntry.path);
                imageView.setImage(sb.toString(), null, Theme.chat_attachEmptyDrawable);
            }
        }

        @Override
        public void willHidePhotoViewer() {
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) childAt).showCheck(true);
                }
            }
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            int childCount = PhotoPickerActivity.this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = PhotoPickerActivity.this.listView.getChildAt(i2);
                if (childAt.getTag() != null) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    int intValue = ((Integer) childAt.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null ? !(intValue < 0 || intValue >= PhotoPickerActivity.this.searchResult.size()) : !(intValue < 0 || intValue >= PhotoPickerActivity.this.selectedAlbum.photos.size())) {
                        if (intValue == i) {
                            photoAttachPhotoCell.showCheck(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    class AnonymousClass10 extends RecyclerView.OnScrollListener {
        AnonymousClass10() {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                int findFirstVisibleItemPosition = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                if (abs <= 0 || findFirstVisibleItemPosition + abs <= PhotoPickerActivity.this.layoutManager.getItemCount() - 2 || PhotoPickerActivity.this.searching || PhotoPickerActivity.this.imageSearchEndReached) {
                    return;
                }
                PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                photoPickerActivity.searchImages(photoPickerActivity.type == 1, PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
            }
        }
    }

    class AnonymousClass11 implements TextWatcher {
        AnonymousClass11() {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (PhotoPickerActivity.this.delegate != null) {
                PhotoPickerActivity.this.delegate.onCaptionChanged(editable);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    class AnonymousClass12 extends FrameLayout {
        AnonymousClass12(Context context) {
            super(context);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoPickerActivity.this.selectedPhotos.size(), new Object[0]));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setLongClickable(true);
            accessibilityNodeInfo.setClickable(true);
        }
    }

    class AnonymousClass13 extends ViewOutlineProvider {
        AnonymousClass13() {
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    public class AnonymousClass14 implements View.OnTouchListener {
        private Rect popupRect = new Rect();

        AnonymousClass14() {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 0 || PhotoPickerActivity.this.sendPopupWindow == null || !PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                return false;
            }
            view.getHitRect(this.popupRect);
            if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            PhotoPickerActivity.this.sendPopupWindow.dismiss();
            return false;
        }
    }

    class AnonymousClass15 extends View {
        AnonymousClass15(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            String format = String.format("%d", Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size())));
            int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(PhotoPickerActivity.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
            int measuredWidth = getMeasuredWidth() / 2;
            getMeasuredHeight();
            PhotoPickerActivity.this.textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
            PhotoPickerActivity.this.paint.setColor(Theme.getColor(PhotoPickerActivity.this.dialogBackgroundKey));
            int i = max / 2;
            PhotoPickerActivity.this.rect.set(measuredWidth - i, 0.0f, i + measuredWidth, getMeasuredHeight());
            canvas.drawRoundRect(PhotoPickerActivity.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
            PhotoPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
            PhotoPickerActivity.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
            canvas.drawRoundRect(PhotoPickerActivity.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
            canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
        }
    }

    public class AnonymousClass16 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass16(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                PhotoPickerActivity.this.animatorSet = null;
            }
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                if (!r2) {
                    PhotoPickerActivity.this.frameLayout2.setVisibility(4);
                    PhotoPickerActivity.this.writeButtonContainer.setVisibility(4);
                }
                PhotoPickerActivity.this.animatorSet = null;
            }
        }
    }

    class AnonymousClass2 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass2() {
        }

        @Override
        public void onItemClick(int i) {
            if (i != -1) {
                if (i == 1) {
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    photoPickerActivity.listSort = true ^ photoPickerActivity.listSort;
                    if (PhotoPickerActivity.this.listSort) {
                        PhotoPickerActivity.this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
                    } else {
                        PhotoPickerActivity.this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
                    }
                    PhotoPickerActivity.this.listView.stopScroll();
                    PhotoPickerActivity.this.layoutManager.scrollToPositionWithOffset(0, 0);
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                    return;
                }
                if (i != 2) {
                    return;
                }
                if (PhotoPickerActivity.this.delegate != null) {
                    PhotoPickerActivity.this.delegate.onOpenInPressed();
                }
            }
            PhotoPickerActivity.this.lambda$onBackPressed$319();
        }
    }

    class AnonymousClass3 implements ActionBarMenuItem.ActionBarSubMenuItemDelegate {
        AnonymousClass3() {
        }

        @Override
        public void onHideSubMenu() {
        }

        @Override
        public void onShowSubMenu() {
            PhotoPickerActivity.this.showAsListItem.setText(LocaleController.getString(PhotoPickerActivity.this.listSort ? R.string.ShowAsGrid : R.string.ShowAsList));
            PhotoPickerActivity.this.showAsListItem.setIcon(PhotoPickerActivity.this.listSort ? R.drawable.msg_media : R.drawable.msg_list);
        }
    }

    public class AnonymousClass4 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        Runnable updateSearch = new Runnable() {
            @Override
            public final void run() {
                PhotoPickerActivity.AnonymousClass4.this.lambda$$0();
            }
        };

        AnonymousClass4() {
        }

        public void lambda$$0() {
            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
            photoPickerActivity.processSearch(photoPickerActivity.searchItem.getSearchField());
        }

        @Override
        public boolean canCollapseSearch() {
            PhotoPickerActivity.this.lambda$onBackPressed$319();
            return false;
        }

        @Override
        public void onSearchExpand() {
        }

        @Override
        public void onSearchPressed(EditText editText) {
            PhotoPickerActivity.this.processSearch(editText);
        }

        @Override
        public void onTextChanged(EditText editText) {
            if (editText.getText().length() != 0) {
                AndroidUtilities.cancelRunOnUIThread(this.updateSearch);
                AndroidUtilities.runOnUIThread(this.updateSearch, 1200L);
                return;
            }
            PhotoPickerActivity.this.searchResult.clear();
            PhotoPickerActivity.this.searchResultKeys.clear();
            PhotoPickerActivity.this.lastSearchString = null;
            PhotoPickerActivity.this.imageSearchEndReached = true;
            PhotoPickerActivity.this.searching = false;
            if (PhotoPickerActivity.this.imageReqId != 0) {
                ConnectionsManager.getInstance(((BaseFragment) PhotoPickerActivity.this).currentAccount).cancelRequest(PhotoPickerActivity.this.imageReqId, true);
                PhotoPickerActivity.this.imageReqId = 0;
            }
            PhotoPickerActivity.this.emptyView.title.setText(LocaleController.getString(R.string.NoRecentSearches));
            PhotoPickerActivity.this.emptyView.showProgress(false);
            PhotoPickerActivity.this.updateSearchInterface();
        }
    }

    public class AnonymousClass5 extends SizeNotifierFrameLayout {
        private boolean ignoreLayout;
        private int lastItemSize;
        private int lastNotifyWidth;

        AnonymousClass5(Context context) {
            super(context);
        }

        public void lambda$onMeasure$0() {
            PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
        }

        private void onMeasureInternal(int i, int i2) {
            int makeMeasureSpec;
            int paddingTop;
            EditTextEmoji editTextEmoji;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int measureKeyboardHeight = measureKeyboardHeight();
            if (AndroidUtilities.dp(20.0f) >= 0 && !AndroidUtilities.isInMultiwindow) {
                PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                if (photoPickerActivity.commentTextView != null && photoPickerActivity.frameLayout2.getParent() == this) {
                    size2 -= PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                    i2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
                }
            }
            if (measureKeyboardHeight > AndroidUtilities.dp(20.0f) && (editTextEmoji = PhotoPickerActivity.this.commentTextView) != null) {
                this.ignoreLayout = true;
                editTextEmoji.hideEmojiView();
                this.ignoreLayout = false;
            }
            EditTextEmoji editTextEmoji2 = PhotoPickerActivity.this.commentTextView;
            if (editTextEmoji2 != null && editTextEmoji2.isPopupShowing()) {
                PhotoPickerActivity.this.fragmentView.setTranslationY(0.0f);
                PhotoPickerActivity.this.listView.setTranslationY(0.0f);
                PhotoPickerActivity.this.emptyView.setTranslationY(0.0f);
            }
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt != null && childAt.getVisibility() != 8) {
                    EditTextEmoji editTextEmoji3 = PhotoPickerActivity.this.commentTextView;
                    if (editTextEmoji3 == null || !editTextEmoji3.isPopupView(childAt)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else {
                        if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                            paddingTop = childAt.getLayoutParams().height;
                        } else if (AndroidUtilities.isTablet()) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                            paddingTop = Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop());
                        } else {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                            paddingTop = (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop();
                        }
                        childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
                    }
                }
            }
        }

        @Override
        public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.AnonymousClass5.onLayout(boolean, int, int, int, int):void");
        }

        @Override
        protected void onMeasure(int r6, int r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.AnonymousClass5.onMeasure(int, int):void");
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    class AnonymousClass6 extends GridLayoutManager {
        AnonymousClass6(Context context, int i) {
            super(context, i);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    class AnonymousClass7 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass7() {
        }

        @Override
        public int getSpanSize(int i) {
            if (PhotoPickerActivity.this.listAdapter.getItemViewType(i) == 1 || PhotoPickerActivity.this.listSort || (PhotoPickerActivity.this.selectedAlbum == null && TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString))) {
                return PhotoPickerActivity.this.layoutManager.getSpanCount();
            }
            return PhotoPickerActivity.this.itemSize + (i % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow - 1 ? AndroidUtilities.dp(5.0f) : 0);
        }
    }

    class AnonymousClass8 implements RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate {
        AnonymousClass8() {
        }

        @Override
        public boolean isIndexSelectable(int i) {
            return PhotoPickerActivity.this.listAdapter.getItemViewType(i) == 0;
        }

        @Override
        public boolean isSelected(int i) {
            return PhotoPickerActivity.this.selectedPhotos.containsKey(PhotoPickerActivity.this.selectedAlbum != null ? Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(i).imageId) : ((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i)).id);
        }

        @Override
        public void onStartStopSelection(boolean z) {
            PhotoPickerActivity.this.alertOnlyOnce = z ? 1 : 0;
            if (z) {
                ((BaseFragment) PhotoPickerActivity.this).parentLayout.getView().requestDisallowInterceptTouchEvent(true);
            }
            PhotoPickerActivity.this.listView.hideSelector(true);
        }

        @Override
        public void setSelected(View view, int i, boolean z) {
            if (z == PhotoPickerActivity.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                ((PhotoAttachPhotoCell) view).callDelegate();
            }
        }
    }

    class AnonymousClass9 extends FlickerLoadingView {
        AnonymousClass9(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        public int getColumnsCount() {
            return 3;
        }

        @Override
        public int getViewType() {
            return 2;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        class AnonymousClass1 implements PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate {
            AnonymousClass1() {
            }

            private void checkSlowMode() {
                TLRPC.Chat currentChat;
                if (!PhotoPickerActivity.this.allowOrder || PhotoPickerActivity.this.chatActivity == null || (currentChat = PhotoPickerActivity.this.chatActivity.getCurrentChat()) == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled || PhotoPickerActivity.this.alertOnlyOnce == 2) {
                    return;
                }
                AlertsCreator.showSimpleAlert(PhotoPickerActivity.this, LocaleController.getString(R.string.Slowmode), LocaleController.getString(R.string.SlowmodeSelectSendError));
                if (PhotoPickerActivity.this.alertOnlyOnce == 1) {
                    PhotoPickerActivity.this.alertOnlyOnce = 2;
                }
            }

            @Override
            public void onCheckClick(org.telegram.ui.Cells.PhotoAttachPhotoCell r7) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.ListAdapter.AnonymousClass1.onCheckClick(org.telegram.ui.Cells.PhotoAttachPhotoCell):void");
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return PhotoPickerActivity.this.selectedAlbum.photos.size();
            }
            if (!PhotoPickerActivity.this.searchResult.isEmpty()) {
                return PhotoPickerActivity.this.searchResult.size() + (!PhotoPickerActivity.this.imageSearchEndReached ? 1 : 0);
            }
            if (!TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString) || PhotoPickerActivity.this.recentSearches.isEmpty()) {
                return 0;
            }
            return PhotoPickerActivity.this.recentSearches.size() + 2;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemViewType(int i) {
            if (PhotoPickerActivity.this.listSort) {
                return 2;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return 0;
            }
            return PhotoPickerActivity.this.searchResult.isEmpty() ? i == PhotoPickerActivity.this.recentSearches.size() ? 4 : 3 : i < PhotoPickerActivity.this.searchResult.size() ? 0 : 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                return TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString) ? viewHolder.getItemViewType() == 3 : viewHolder.getAdapterPosition() < PhotoPickerActivity.this.searchResult.size();
            }
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String pathToAttach;
            CharSequence string;
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                photoAttachPhotoCell.setItemSize(PhotoPickerActivity.this.itemSize);
                BackupImageView imageView = photoAttachPhotoCell.getImageView();
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
                imageView.setOrientation(0, true);
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    photoAttachPhotoCell.setPhotoEntry(photoEntry, PhotoPickerActivity.this.selectedPhotosOrder.size() > 1, true, false);
                    photoAttachPhotoCell.setChecked(PhotoPickerActivity.this.allowIndices ? PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)) : -1, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    pathToAttach = photoEntry.path;
                } else {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    photoAttachPhotoCell.setPhotoEntry(searchImage, true, false);
                    photoAttachPhotoCell.getVideoInfoContainer().setVisibility(4);
                    photoAttachPhotoCell.setChecked(PhotoPickerActivity.this.allowIndices ? PhotoPickerActivity.this.selectedPhotosOrder.indexOf(searchImage.id) : -1, PhotoPickerActivity.this.selectedPhotos.containsKey(searchImage.id), false);
                    pathToAttach = searchImage.getPathToAttach();
                }
                boolean isShowingImage = PhotoViewer.isShowingImage(pathToAttach);
                imageView.getImageReceiver().setVisible(!isShowingImage, true);
                photoAttachPhotoCell.getCheckBox().setVisibility((PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL || isShowingImage) ? 8 : 0);
                return;
            }
            if (itemViewType == 1) {
                ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = -1;
                    layoutParams.height = PhotoPickerActivity.this.itemSize;
                    viewHolder.itemView.setLayoutParams(layoutParams);
                    return;
                }
                return;
            }
            if (itemViewType == 2) {
                MediaController.PhotoEntry photoEntry2 = PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                sharedDocumentCell.setPhotoEntry(photoEntry2);
                sharedDocumentCell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId)), false);
                sharedDocumentCell.setTag(Integer.valueOf(i));
                return;
            }
            if (itemViewType != 3) {
                return;
            }
            TextCell textCell = (TextCell) viewHolder.itemView;
            if (i < PhotoPickerActivity.this.recentSearches.size()) {
                string = (CharSequence) PhotoPickerActivity.this.recentSearches.get(i);
                i2 = R.drawable.msg_recent;
            } else {
                string = LocaleController.getString(R.string.ClearRecentHistory);
                i2 = R.drawable.msg_clear_recent;
            }
            textCell.setTextAndIcon(string, i2, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewGroup viewGroup2;
            ViewGroup viewGroup3;
            if (i != 0) {
                if (i == 1) {
                    ViewGroup frameLayout = new FrameLayout(this.mContext);
                    frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    RadialProgressView radialProgressView = new RadialProgressView(this.mContext);
                    radialProgressView.setProgressColor(-11371101);
                    frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-1, -1.0f));
                    viewGroup3 = frameLayout;
                } else if (i == 2) {
                    viewGroup2 = new SharedDocumentCell(this.mContext, 1);
                } else if (i != 3) {
                    DividerCell dividerCell = new DividerCell(this.mContext);
                    dividerCell.setForceDarkTheme(PhotoPickerActivity.this.forceDarckTheme);
                    viewGroup2 = dividerCell;
                } else {
                    TextCell textCell = new TextCell(this.mContext, 23, true);
                    textCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    viewGroup3 = textCell;
                    if (PhotoPickerActivity.this.forceDarckTheme) {
                        textCell.textView.setTextColor(Theme.getColor(PhotoPickerActivity.this.textKey));
                        textCell.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_voipgroup_mutedIcon), PorterDuff.Mode.MULTIPLY));
                        viewGroup3 = textCell;
                    }
                }
                viewGroup2 = viewGroup3;
            } else {
                PhotoAttachPhotoCell photoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext, null);
                photoAttachPhotoCell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() {
                    AnonymousClass1() {
                    }

                    private void checkSlowMode() {
                        TLRPC.Chat currentChat;
                        if (!PhotoPickerActivity.this.allowOrder || PhotoPickerActivity.this.chatActivity == null || (currentChat = PhotoPickerActivity.this.chatActivity.getCurrentChat()) == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled || PhotoPickerActivity.this.alertOnlyOnce == 2) {
                            return;
                        }
                        AlertsCreator.showSimpleAlert(PhotoPickerActivity.this, LocaleController.getString(R.string.Slowmode), LocaleController.getString(R.string.SlowmodeSelectSendError));
                        if (PhotoPickerActivity.this.alertOnlyOnce == 1) {
                            PhotoPickerActivity.this.alertOnlyOnce = 2;
                        }
                    }

                    @Override
                    public void onCheckClick(org.telegram.ui.Cells.PhotoAttachPhotoCell r7) {
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerActivity.ListAdapter.AnonymousClass1.onCheckClick(org.telegram.ui.Cells.PhotoAttachPhotoCell):void");
                    }
                });
                photoAttachPhotoCell.getCheckFrame().setVisibility(PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL ? 8 : 0);
                viewGroup2 = photoAttachPhotoCell;
            }
            return new RecyclerListView.Holder(viewGroup2);
        }
    }

    public interface PhotoPickerActivityDelegate {

        public abstract class CC {
            public static boolean $default$canFinishFragment(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
                return true;
            }

            public static void $default$onOpenInPressed(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
            }
        }

        void actionButtonPressed(boolean z, boolean z2, int i);

        boolean canFinishFragment();

        void onCaptionChanged(CharSequence charSequence);

        void onOpenInPressed();

        void selectedPhotosChanged();
    }

    public interface PhotoPickerActivitySearchDelegate {
        void shouldClearRecentSearch();

        void shouldSearchText(String str);
    }

    public PhotoPickerActivity(int i, MediaController.AlbumEntry albumEntry, HashMap hashMap, ArrayList arrayList, int i2, boolean z, ChatActivity chatActivity, boolean z2) {
        int i3;
        this.selectedAlbum = albumEntry;
        this.selectedPhotos = hashMap;
        this.selectedPhotosOrder = arrayList;
        this.type = i;
        this.selectPhotoType = i2;
        this.chatActivity = chatActivity;
        this.allowCaption = z;
        this.forceDarckTheme = z2;
        if (albumEntry == null) {
            loadRecentSearch();
        }
        if (z2) {
            this.dialogBackgroundKey = Theme.key_voipgroup_dialogBackground;
            this.textKey = Theme.key_voipgroup_actionBarItems;
            i3 = Theme.key_voipgroup_actionBarItemsSelector;
        } else {
            this.dialogBackgroundKey = Theme.key_dialogBackground;
            this.textKey = Theme.key_dialogTextBlack;
            i3 = Theme.key_dialogButtonSelector;
        }
        this.selectorKey = i3;
    }

    private void addToRecentSearches(String str) {
        int size = this.recentSearches.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            if (((String) this.recentSearches.get(i)).equalsIgnoreCase(str)) {
                this.recentSearches.remove(i);
                break;
            }
            i++;
        }
        this.recentSearches.add(0, str);
        while (this.recentSearches.size() > 20) {
            this.recentSearches.remove(r5.size() - 1);
        }
        saveRecentSearch();
    }

    public int addToSelectedPhotos(Object obj, int i) {
        boolean z = obj instanceof MediaController.PhotoEntry;
        Object valueOf = z ? Integer.valueOf(((MediaController.PhotoEntry) obj).imageId) : obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).id : null;
        if (valueOf == null) {
            return -1;
        }
        if (!this.selectedPhotos.containsKey(valueOf)) {
            this.selectedPhotos.put(valueOf, obj);
            this.selectedPhotosOrder.add(valueOf);
            return -1;
        }
        this.selectedPhotos.remove(valueOf);
        int indexOf = this.selectedPhotosOrder.indexOf(valueOf);
        if (indexOf >= 0) {
            this.selectedPhotosOrder.remove(indexOf);
        }
        if (this.allowIndices) {
            updateCheckedPhotoIndices();
        }
        if (i >= 0) {
            if (z) {
                ((MediaController.PhotoEntry) obj).reset();
            } else if (obj instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) obj).reset();
            }
            this.provider.updatePhotoAtIndex(i);
        }
        return indexOf;
    }

    private void applyCaption() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || editTextEmoji.length() <= 0) {
            return;
        }
        Object obj = this.selectedPhotos.get(this.selectedPhotosOrder.get(0));
        if (obj instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) obj).caption = this.commentTextView.getText().toString();
        } else if (obj instanceof MediaController.SearchImage) {
            ((MediaController.SearchImage) obj).caption = this.commentTextView.getText().toString();
        }
    }

    public PhotoAttachPhotoCell getCellForIndex(int i) {
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                if (albumEntry == null ? !(intValue < 0 || intValue >= this.searchResult.size()) : !(intValue < 0 || intValue >= albumEntry.photos.size())) {
                    if (intValue == i) {
                        return photoAttachPhotoCell;
                    }
                }
            }
        }
        return null;
    }

    public void lambda$createView$0(DialogInterface dialogInterface, int i) {
        PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
        if (photoPickerActivitySearchDelegate != null) {
            photoPickerActivitySearchDelegate.shouldClearRecentSearch();
        } else {
            clearRecentSearch();
        }
    }

    public void lambda$createView$1(View view, int i) {
        if (this.selectedAlbum != null || !this.searchResult.isEmpty()) {
            MediaController.AlbumEntry albumEntry = this.selectedAlbum;
            ArrayList<MediaController.PhotoEntry> arrayList = albumEntry != null ? albumEntry.photos : this.searchResult;
            if (i < 0 || i >= arrayList.size()) {
                return;
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                AndroidUtilities.hideKeyboard(actionBarMenuItem.getSearchField());
            }
            if (this.listSort) {
                onListItemClick(view, arrayList.get(i));
                return;
            }
            int i2 = this.selectPhotoType;
            int i3 = (i2 == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR || i2 == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO) ? 1 : i2 == PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER ? 3 : i2 == PhotoAlbumPickerActivity.SELECT_TYPE_QR ? 10 : this.chatActivity == null ? 4 : 0;
            PhotoViewer.getInstance().setParentActivity(this);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, i, i3, this.isDocumentsPicker, this.provider, this.chatActivity);
            return;
        }
        if (i < this.recentSearches.size()) {
            String str = (String) this.recentSearches.get(i);
            PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
            if (photoPickerActivitySearchDelegate != null) {
                photoPickerActivitySearchDelegate.shouldSearchText(str);
                return;
            }
            this.searchItem.getSearchField().setText(str);
            this.searchItem.getSearchField().setSelection(str.length());
            processSearch(this.searchItem.getSearchField());
            return;
        }
        if (i == this.recentSearches.size() + 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertTitle));
            builder.setMessage(LocaleController.getString(R.string.ClearSearchAlert));
            builder.setPositiveButton(LocaleController.getString(R.string.ClearButton), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    PhotoPickerActivity.this.lambda$createView$0(dialogInterface, i4);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }
    }

    public boolean lambda$createView$2(View view, int i) {
        if (this.listSort) {
            onListItemClick(view, this.selectedAlbum.photos.get(i));
            return true;
        }
        if (!(view instanceof PhotoAttachPhotoCell)) {
            return false;
        }
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
        boolean z = !((PhotoAttachPhotoCell) view).isChecked();
        this.shouldSelect = z;
        recyclerViewItemRangeSelector.setIsActive(view, true, i, z);
        return false;
    }

    public static boolean lambda$createView$3(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$4(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
            sendSelectedPhotos(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoPickerActivity$$ExternalSyntheticLambda11(this));
        }
    }

    public void lambda$createView$5(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public void lambda$createView$6(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoPickerActivity$$ExternalSyntheticLambda11(this));
        } else {
            sendSelectedPhotos(true, 0);
        }
    }

    public boolean lambda$createView$7(View view) {
        ActionBarMenuSubItem actionBarMenuSubItem;
        String string;
        int i;
        int i2;
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity != null && this.maxSelectedPhotos != 1) {
            chatActivity.getCurrentChat();
            TLRPC.User currentUser = this.chatActivity.getCurrentUser();
            if (this.sendPopupLayout == null) {
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
                this.sendPopupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                    private Rect popupRect = new Rect();

                    AnonymousClass14() {
                    }

                    @Override
                    public boolean onTouch(View view2, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || PhotoPickerActivity.this.sendPopupWindow == null || !PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view2.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        PhotoPickerActivity.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    @Override
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        PhotoPickerActivity.this.lambda$createView$5(keyEvent);
                    }
                });
                this.sendPopupLayout.setShownFromBottom(false);
                this.itemCells = new ActionBarMenuSubItem[2];
                final int i3 = 0;
                while (i3 < 2) {
                    if ((i3 != 0 || this.chatActivity.canScheduleMessage()) && (i3 != 1 || !UserObject.isUserSelf(currentUser))) {
                        this.itemCells[i3] = new ActionBarMenuSubItem(getParentActivity(), i3 == 0, i3 == 1);
                        if (i3 == 0) {
                            if (UserObject.isUserSelf(currentUser)) {
                                actionBarMenuSubItem = this.itemCells[i3];
                                i2 = R.string.SetReminder;
                            } else {
                                actionBarMenuSubItem = this.itemCells[i3];
                                i2 = R.string.ScheduleMessage;
                            }
                            string = LocaleController.getString(i2);
                            i = R.drawable.msg_calendar2;
                        } else {
                            actionBarMenuSubItem = this.itemCells[i3];
                            string = LocaleController.getString(R.string.SendWithoutSound);
                            i = R.drawable.input_notify_off;
                        }
                        actionBarMenuSubItem.setTextAndIcon(string, i);
                        this.itemCells[i3].setMinimumWidth(AndroidUtilities.dp(196.0f));
                        this.sendPopupLayout.addView((View) this.itemCells[i3], LayoutHelper.createLinear(-1, 48));
                        this.itemCells[i3].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view2) {
                                PhotoPickerActivity.this.lambda$createView$6(i3, view2);
                            }
                        });
                    }
                    i3++;
                }
                this.sendPopupLayout.setupRadialSelectors(Theme.getColor(this.selectorKey));
                ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow = actionBarPopupWindow;
                actionBarPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }
            this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view.performHapticFeedback(3, 2);
        }
        return false;
    }

    public void lambda$searchBotUser$8(TLObject tLObject, boolean z) {
        TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(z, str, "", false);
    }

    public void lambda$searchBotUser$9(final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PhotoPickerActivity.this.lambda$searchBotUser$8(tLObject, z);
                }
            });
        }
    }

    public void lambda$searchImages$10(String str, int i, TLObject tLObject, boolean z, TLRPC.User user) {
        int i2;
        TLRPC.Photo photo;
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        addToRecentSearches(str);
        if (i != this.lastSearchToken) {
            return;
        }
        int size = this.searchResult.size();
        if (tLObject != null) {
            TLRPC.messages_BotResults messages_botresults = (TLRPC.messages_BotResults) tLObject;
            this.nextImagesSearchOffset = messages_botresults.next_offset;
            int size2 = messages_botresults.results.size();
            i2 = 0;
            for (int i3 = 0; i3 < size2; i3++) {
                TLRPC.BotInlineResult botInlineResult = messages_botresults.results.get(i3);
                if ((z || "photo".equals(botInlineResult.type)) && ((!z || "gif".equals(botInlineResult.type)) && !this.searchResultKeys.containsKey(botInlineResult.id))) {
                    MediaController.SearchImage searchImage = new MediaController.SearchImage();
                    if (z && botInlineResult.document != null) {
                        for (int i4 = 0; i4 < botInlineResult.document.attributes.size(); i4++) {
                            TLRPC.DocumentAttribute documentAttribute = botInlineResult.document.attributes.get(i4);
                            if ((documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) || (documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                searchImage.width = documentAttribute.w;
                                searchImage.height = documentAttribute.h;
                                break;
                            }
                        }
                        searchImage.document = botInlineResult.document;
                        searchImage.size = 0;
                        TLRPC.Photo photo2 = botInlineResult.photo;
                        if (photo2 != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, this.itemSize, true)) != null) {
                            botInlineResult.document.thumbs.add(closestPhotoSizeWithSize);
                            botInlineResult.document.flags |= 1;
                        }
                    } else if (!z && (photo = botInlineResult.photo) != null) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                        TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, 320);
                        if (closestPhotoSizeWithSize2 != null) {
                            searchImage.width = closestPhotoSizeWithSize2.w;
                            searchImage.height = closestPhotoSizeWithSize2.h;
                            searchImage.photoSize = closestPhotoSizeWithSize2;
                            searchImage.photo = botInlineResult.photo;
                            searchImage.size = closestPhotoSizeWithSize2.size;
                            searchImage.thumbPhotoSize = closestPhotoSizeWithSize3;
                        }
                    } else if (botInlineResult.content != null) {
                        int i5 = 0;
                        while (true) {
                            if (i5 >= botInlineResult.content.attributes.size()) {
                                break;
                            }
                            TLRPC.DocumentAttribute documentAttribute2 = botInlineResult.content.attributes.get(i5);
                            if (documentAttribute2 instanceof TLRPC.TL_documentAttributeImageSize) {
                                searchImage.width = documentAttribute2.w;
                                searchImage.height = documentAttribute2.h;
                                break;
                            }
                            i5++;
                        }
                        TLRPC.WebDocument webDocument = botInlineResult.thumb;
                        searchImage.thumbUrl = webDocument != null ? webDocument.url : null;
                        TLRPC.WebDocument webDocument2 = botInlineResult.content;
                        searchImage.imageUrl = webDocument2.url;
                        searchImage.size = z ? 0 : webDocument2.size;
                    }
                    searchImage.id = botInlineResult.id;
                    searchImage.type = z ? 1 : 0;
                    searchImage.inlineResult = botInlineResult;
                    HashMap<String, String> hashMap = new HashMap<>();
                    searchImage.params = hashMap;
                    hashMap.put("id", botInlineResult.id);
                    searchImage.params.put("query_id", "" + messages_botresults.query_id);
                    searchImage.params.put("bot_name", UserObject.getPublicUsername(user));
                    this.searchResult.add(searchImage);
                    this.searchResultKeys.put(searchImage.id, searchImage);
                    i2++;
                }
            }
            this.imageSearchEndReached = size == this.searchResult.size() || this.nextImagesSearchOffset == null;
        } else {
            i2 = 0;
        }
        this.searching = false;
        if (i2 != 0) {
            this.listAdapter.notifyItemRangeInserted(size, i2);
        } else if (this.imageSearchEndReached) {
            this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
        }
        if (this.searchResult.size() <= 0) {
            this.emptyView.showProgress(false);
        }
    }

    public void lambda$searchImages$11(final String str, final int i, final boolean z, final TLRPC.User user, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PhotoPickerActivity.this.lambda$searchImages$10(str, i, tLObject, z, user);
            }
        });
    }

    private void loadRecentSearch() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0);
        int i = sharedPreferences.getInt("count", 0);
        for (int i2 = 0; i2 < i; i2++) {
            String string = sharedPreferences.getString("recent" + i2, null);
            if (string == null) {
                return;
            }
            this.recentSearches.add(string);
        }
    }

    private void onListItemClick(View view, Object obj) {
        boolean z = addToSelectedPhotos(obj, -1) == -1;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(this.selectedPhotosOrder.contains(Integer.valueOf(this.selectedAlbum.photos.get(((Integer) view.getTag()).intValue()).imageId)), true);
        }
        updatePhotosButton(z ? 1 : 2);
        this.delegate.selectedPhotosChanged();
    }

    public void processSearch(EditText editText) {
        SpoilersTextView spoilersTextView;
        String formatString;
        if (editText.getText().length() == 0) {
            return;
        }
        String obj = editText.getText().toString();
        this.searchResult.clear();
        this.searchResultKeys.clear();
        this.imageSearchEndReached = true;
        searchImages(this.type == 1, obj, "", true);
        this.lastSearchString = obj;
        if (obj.length() == 0) {
            this.lastSearchString = null;
            spoilersTextView = this.emptyView.title;
            formatString = LocaleController.getString(R.string.NoRecentSearches);
        } else {
            spoilersTextView = this.emptyView.title;
            formatString = LocaleController.formatString("NoResultFoundFor", R.string.NoResultFoundFor, this.lastSearchString);
        }
        spoilersTextView.setText(formatString);
        updateSearchInterface();
    }

    private void saveRecentSearch() {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0).edit();
        edit.clear();
        edit.putInt("count", this.recentSearches.size());
        int size = this.recentSearches.size();
        for (int i = 0; i < size; i++) {
            edit.putString("recent" + i, (String) this.recentSearches.get(i));
        }
        edit.commit();
    }

    private void searchBotUser(final boolean z) {
        if (this.searchingUser) {
            return;
        }
        this.searchingUser = true;
        TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        tL_contacts_resolveUsername.username = z ? messagesController.gifSearchBot : messagesController.imageSearchBot;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PhotoPickerActivity.this.lambda$searchBotUser$9(z, tLObject, tL_error);
            }
        });
    }

    public void searchImages(final boolean z, final String str, String str2, boolean z2) {
        TLRPC.InputPeer tL_inputPeerEmpty;
        if (this.searching) {
            this.searching = false;
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = str;
        this.searching = true;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        MessagesController messagesController2 = MessagesController.getInstance(this.currentAccount);
        TLObject userOrChat = messagesController.getUserOrChat(z ? messagesController2.gifSearchBot : messagesController2.imageSearchBot);
        if (!(userOrChat instanceof TLRPC.User)) {
            if (z2) {
                searchBotUser(z);
                return;
            }
            return;
        }
        final TLRPC.User user = (TLRPC.User) userOrChat;
        TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
        tL_messages_getInlineBotResults.query = str == null ? "" : str;
        tL_messages_getInlineBotResults.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
        tL_messages_getInlineBotResults.offset = str2;
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity != null) {
            long dialogId = chatActivity.getDialogId();
            tL_inputPeerEmpty = DialogObject.isEncryptedDialog(dialogId) ? new TLRPC.TL_inputPeerEmpty() : getMessagesController().getInputPeer(dialogId);
        } else {
            tL_inputPeerEmpty = new TLRPC.TL_inputPeerEmpty();
        }
        tL_messages_getInlineBotResults.peer = tL_inputPeerEmpty;
        final int i = this.lastSearchToken + 1;
        this.lastSearchToken = i;
        this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PhotoPickerActivity.this.lambda$searchImages$11(str, i, z, user, tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
    }

    public void sendSelectedPhotos(boolean z, int i) {
        if (this.selectedPhotos.isEmpty() || this.delegate == null || this.sendPressed) {
            return;
        }
        applyCaption();
        this.sendPressed = true;
        this.delegate.actionButtonPressed(false, z, i);
        if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
            PhotoPickerActivityDelegate photoPickerActivityDelegate = this.delegate;
            if (photoPickerActivityDelegate == null || photoPickerActivityDelegate.canFinishFragment()) {
                lambda$onBackPressed$319();
            }
        }
    }

    private boolean showCommentTextView(boolean z, boolean z2) {
        if (this.commentTextView == null) {
            return false;
        }
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        if (z2) {
            this.animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, z ? 1.0f : 0.2f));
            FrameLayout frameLayout2 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) property2, z ? 1.0f : 0.2f));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property3, z ? 1.0f : 0.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property, z ? 1.0f : 0.2f));
            arrayList.add(ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property2, z ? 1.0f : 0.2f));
            arrayList.add(ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) property3, z ? 1.0f : 0.0f));
            FrameLayout frameLayout4 = this.frameLayout2;
            Property property4 = View.TRANSLATION_Y;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, (Property<FrameLayout, Float>) property4, z ? 0.0f : AndroidUtilities.dp(48.0f)));
            arrayList.add(ObjectAnimator.ofFloat(this.shadow, (Property<View, Float>) property4, z ? 0.0f : AndroidUtilities.dp(48.0f)));
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180L);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                final boolean val$show;

                AnonymousClass16(boolean z3) {
                    r2 = z3;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                        PhotoPickerActivity.this.animatorSet = null;
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoPickerActivity.this.animatorSet)) {
                        if (!r2) {
                            PhotoPickerActivity.this.frameLayout2.setVisibility(4);
                            PhotoPickerActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        PhotoPickerActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z3 ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z3 ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z3 ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z3 ? 1.0f : 0.2f);
            this.selectedCountView.setScaleY(z3 ? 1.0f : 0.2f);
            this.selectedCountView.setAlpha(z3 ? 1.0f : 0.0f);
            this.frameLayout2.setTranslationY(z3 ? 0.0f : AndroidUtilities.dp(48.0f));
            this.shadow.setTranslationY(z3 ? 0.0f : AndroidUtilities.dp(48.0f));
            if (!z3) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void updateCheckedPhotoIndices() {
        ArrayList arrayList;
        Object obj;
        int i;
        if (this.allowIndices) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    Integer num = (Integer) childAt.getTag();
                    MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                    if (albumEntry != null) {
                        MediaController.PhotoEntry photoEntry = albumEntry.photos.get(num.intValue());
                        if (this.allowIndices) {
                            arrayList = this.selectedPhotosOrder;
                            obj = Integer.valueOf(photoEntry.imageId);
                            i = arrayList.indexOf(obj);
                        }
                        i = -1;
                    } else {
                        MediaController.SearchImage searchImage = (MediaController.SearchImage) this.searchResult.get(num.intValue());
                        if (this.allowIndices) {
                            arrayList = this.selectedPhotosOrder;
                            obj = searchImage.id;
                            i = arrayList.indexOf(obj);
                        }
                        i = -1;
                    }
                    photoAttachPhotoCell.setNum(i);
                } else if (childAt instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) childAt).setChecked(this.selectedPhotosOrder.indexOf(Integer.valueOf(this.selectedAlbum.photos.get(((Integer) childAt.getTag()).intValue()).imageId)) != 0, false);
                }
            }
        }
    }

    public void updateSearchInterface() {
        StickerEmptyView stickerEmptyView;
        boolean z;
        String str;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.searching || (this.recentSearches.size() > 0 && ((str = this.lastSearchString) == null || TextUtils.isEmpty(str)))) {
            stickerEmptyView = this.emptyView;
            z = true;
        } else {
            stickerEmptyView = this.emptyView;
            z = false;
        }
        stickerEmptyView.showProgress(z);
    }

    public void clearRecentSearch() {
        this.recentSearches.clear();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        this.emptyView.showProgress(false);
        saveRecentSearch();
    }

    @Override
    public View createView(Context context) {
        ActionBar actionBar;
        int i;
        SpoilersTextView spoilersTextView;
        int i2;
        int i3;
        ActionBarMenuItem actionBarMenuItem;
        int i4;
        this.listSort = false;
        this.actionBar.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
        this.actionBar.setTitleColor(Theme.getColor(this.textKey));
        this.actionBar.setItemsColor(Theme.getColor(this.textKey), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(this.selectorKey), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        MediaController.AlbumEntry albumEntry = this.selectedAlbum;
        if (albumEntry != null) {
            this.actionBar.setTitle(albumEntry.bucketName);
        } else {
            int i5 = this.type;
            if (i5 == 0) {
                actionBar = this.actionBar;
                i = R.string.SearchImagesTitle;
            } else if (i5 == 1) {
                actionBar = this.actionBar;
                i = R.string.SearchGifsTitle;
            }
            actionBar.setTitle(LocaleController.getString(i));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            AnonymousClass2() {
            }

            @Override
            public void onItemClick(int i6) {
                if (i6 != -1) {
                    if (i6 == 1) {
                        PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                        photoPickerActivity.listSort = true ^ photoPickerActivity.listSort;
                        if (PhotoPickerActivity.this.listSort) {
                            PhotoPickerActivity.this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
                        } else {
                            PhotoPickerActivity.this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
                        }
                        PhotoPickerActivity.this.listView.stopScroll();
                        PhotoPickerActivity.this.layoutManager.scrollToPositionWithOffset(0, 0);
                        PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    if (i6 != 2) {
                        return;
                    }
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onOpenInPressed();
                    }
                }
                PhotoPickerActivity.this.lambda$onBackPressed$319();
            }
        });
        if (this.isDocumentsPicker) {
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_other);
            addItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() {
                AnonymousClass3() {
                }

                @Override
                public void onHideSubMenu() {
                }

                @Override
                public void onShowSubMenu() {
                    PhotoPickerActivity.this.showAsListItem.setText(LocaleController.getString(PhotoPickerActivity.this.listSort ? R.string.ShowAsGrid : R.string.ShowAsList));
                    PhotoPickerActivity.this.showAsListItem.setIcon(PhotoPickerActivity.this.listSort ? R.drawable.msg_media : R.drawable.msg_list);
                }
            });
            this.showAsListItem = addItem.addSubItem(1, R.drawable.msg_list, LocaleController.getString(R.string.ShowAsList));
            addItem.addSubItem(2, R.drawable.msg_openin, LocaleController.getString(R.string.OpenInExternalApp));
        }
        if (this.selectedAlbum == null) {
            ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new AnonymousClass4());
            this.searchItem = actionBarMenuItemSearchListener;
            EditTextBoldCursor searchField = actionBarMenuItemSearchListener.getSearchField();
            searchField.setTextColor(Theme.getColor(this.textKey));
            searchField.setCursorColor(Theme.getColor(this.textKey));
            searchField.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        }
        if (this.selectedAlbum == null) {
            int i6 = this.type;
            if (i6 == 0) {
                actionBarMenuItem = this.searchItem;
                i4 = R.string.SearchImagesTitle;
            } else if (i6 == 1) {
                actionBarMenuItem = this.searchItem;
                i4 = R.string.SearchGifsTitle;
            }
            actionBarMenuItem.setSearchFieldHint(LocaleController.getString(i4));
        }
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(context);
        this.sizeNotifierFrameLayout = anonymousClass5;
        anonymousClass5.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
        this.fragmentView = this.sizeNotifierFrameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass6 anonymousClass6 = new GridLayoutManager(context, 4) {
            AnonymousClass6(Context context2, int i7) {
                super(context2, i7);
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass6;
        recyclerListView2.setLayoutManager(anonymousClass6);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            AnonymousClass7() {
            }

            @Override
            public int getSpanSize(int i7) {
                if (PhotoPickerActivity.this.listAdapter.getItemViewType(i7) == 1 || PhotoPickerActivity.this.listSort || (PhotoPickerActivity.this.selectedAlbum == null && TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString))) {
                    return PhotoPickerActivity.this.layoutManager.getSpanCount();
                }
                return PhotoPickerActivity.this.itemSize + (i7 % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow - 1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor(this.dialogBackgroundKey));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i7) {
                PhotoPickerActivity.this.lambda$createView$1(view, i7);
            }
        });
        if (this.maxSelectedPhotos != 1) {
            this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
                @Override
                public final boolean onItemClick(View view, int i7) {
                    boolean lambda$createView$2;
                    lambda$createView$2 = PhotoPickerActivity.this.lambda$createView$2(view, i7);
                    return lambda$createView$2;
                }
            });
        }
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() {
            AnonymousClass8() {
            }

            @Override
            public boolean isIndexSelectable(int i7) {
                return PhotoPickerActivity.this.listAdapter.getItemViewType(i7) == 0;
            }

            @Override
            public boolean isSelected(int i7) {
                return PhotoPickerActivity.this.selectedPhotos.containsKey(PhotoPickerActivity.this.selectedAlbum != null ? Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(i7).imageId) : ((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(i7)).id);
            }

            @Override
            public void onStartStopSelection(boolean z) {
                PhotoPickerActivity.this.alertOnlyOnce = z ? 1 : 0;
                if (z) {
                    ((BaseFragment) PhotoPickerActivity.this).parentLayout.getView().requestDisallowInterceptTouchEvent(true);
                }
                PhotoPickerActivity.this.listView.hideSelector(true);
            }

            @Override
            public void setSelected(View view, int i7, boolean z) {
                if (z == PhotoPickerActivity.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        if (this.maxSelectedPhotos != 1) {
            this.listView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        }
        AnonymousClass9 anonymousClass9 = new FlickerLoadingView(context2, getResourceProvider()) {
            AnonymousClass9(Context context2, Theme.ResourcesProvider resourcesProvider) {
                super(context2, resourcesProvider);
            }

            @Override
            public int getColumnsCount() {
                return 3;
            }

            @Override
            public int getViewType() {
                return 2;
            }
        };
        this.flickerView = anonymousClass9;
        anonymousClass9.setAlpha(0.0f);
        this.flickerView.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context2, this.flickerView, 1, getResourceProvider());
        this.emptyView = stickerEmptyView;
        stickerEmptyView.setAnimateLayoutChange(true);
        this.emptyView.title.setTypeface(Typeface.DEFAULT);
        this.emptyView.title.setTextSize(1, 16.0f);
        this.emptyView.title.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        this.emptyView.addView(this.flickerView, 0);
        if (this.selectedAlbum != null) {
            spoilersTextView = this.emptyView.title;
            i2 = R.string.NoPhotos;
        } else {
            spoilersTextView = this.emptyView.title;
            i2 = R.string.NoRecentSearches;
        }
        spoilersTextView.setText(LocaleController.getString(i2));
        this.emptyView.showProgress(false, false);
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 126.0f, 0.0f, 0.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass10() {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i7) {
                if (i7 == 1) {
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i7, int i22) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    int findFirstVisibleItemPosition = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                    int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                    if (abs <= 0 || findFirstVisibleItemPosition + abs <= PhotoPickerActivity.this.layoutManager.getItemCount() - 2 || PhotoPickerActivity.this.searching || PhotoPickerActivity.this.imageSearchEndReached) {
                        return;
                    }
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    photoPickerActivity.searchImages(photoPickerActivity.type == 1, PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
                }
            }
        });
        if (this.selectedAlbum == null) {
            updateSearchInterface();
        }
        if (this.needsBottomLayout) {
            View view = new View(context2);
            this.shadow = view;
            view.setBackgroundResource(R.drawable.header_shadow_reverse);
            this.shadow.setTranslationY(AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.frameLayout2 = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
            this.frameLayout2.setVisibility(4);
            this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
            this.frameLayout2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$createView$3;
                    lambda$createView$3 = PhotoPickerActivity.lambda$createView$3(view2, motionEvent);
                    return lambda$createView$3;
                }
            });
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                editTextEmoji.onDestroy();
            }
            this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, null, 1, false);
            this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
            this.commentTextView.setHint(LocaleController.getString(R.string.AddCaption));
            this.commentTextView.onResume();
            EditTextCaption editText = this.commentTextView.getEditText();
            editText.setMaxLines(1);
            editText.setSingleLine(true);
            this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
            CharSequence charSequence = this.caption;
            if (charSequence != null) {
                this.commentTextView.setText(charSequence);
            }
            this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() {
                AnonymousClass11() {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onCaptionChanged(editable);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence2, int i7, int i22, int i32) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence2, int i7, int i22, int i32) {
                }
            });
            AnonymousClass12 anonymousClass12 = new FrameLayout(context2) {
                AnonymousClass12(Context context2) {
                    super(context2);
                }

                @Override
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoPickerActivity.this.selectedPhotos.size(), new Object[0]));
                    accessibilityNodeInfo.setClassName(Button.class.getName());
                    accessibilityNodeInfo.setLongClickable(true);
                    accessibilityNodeInfo.setClickable(true);
                }
            };
            this.writeButtonContainer = anonymousClass12;
            anonymousClass12.setFocusable(true);
            this.writeButtonContainer.setFocusableInTouchMode(true);
            this.writeButtonContainer.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
            this.writeButton = new ImageView(context2);
            int dp = AndroidUtilities.dp(56.0f);
            int i7 = Theme.key_dialogFloatingButton;
            int color = Theme.getColor(i7);
            int i8 = Build.VERSION.SDK_INT;
            if (i8 >= 21) {
                i7 = Theme.key_dialogFloatingButtonPressed;
            }
            this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(i7));
            if (i8 < 21) {
                Drawable mutate = context2.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, this.writeButtonDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                this.writeButtonDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
            this.writeButton.setImageResource(R.drawable.attach_send);
            this.writeButton.setImportantForAccessibility(2);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (i8 >= 21) {
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    AnonymousClass13() {
                    }

                    @Override
                    public void getOutline(View view2, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(i8 >= 21 ? 56 : 60, i8 >= 21 ? 56.0f : 60.0f, 51, i8 >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            this.writeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    PhotoPickerActivity.this.lambda$createView$4(view2);
                }
            });
            this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public final boolean onLongClick(View view2) {
                    boolean lambda$createView$7;
                    lambda$createView$7 = PhotoPickerActivity.this.lambda$createView$7(view2);
                    return lambda$createView$7;
                }
            });
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.bold());
            AnonymousClass15 anonymousClass15 = new View(context2) {
                AnonymousClass15(Context context2) {
                    super(context2);
                }

                @Override
                protected void onDraw(Canvas canvas) {
                    String format = String.format("%d", Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size())));
                    int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(PhotoPickerActivity.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
                    int measuredWidth = getMeasuredWidth() / 2;
                    getMeasuredHeight();
                    PhotoPickerActivity.this.textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(PhotoPickerActivity.this.dialogBackgroundKey));
                    int i9 = max / 2;
                    PhotoPickerActivity.this.rect.set(measuredWidth - i9, 0.0f, i9 + measuredWidth, getMeasuredHeight());
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
                    PhotoPickerActivity.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
                    canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
                }
            };
            this.selectedCountView = anonymousClass15;
            anonymousClass15.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
            if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL) {
                this.commentTextView.setVisibility(8);
            }
        }
        this.allowIndices = (this.selectedAlbum != null || (i3 = this.type) == 0 || i3 == 1) && this.allowOrder;
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        updatePhotosButton(0);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack(true);
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, this.dialogBackgroundKey));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, this.dialogBackgroundKey));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, this.textKey));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, this.textKey));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, this.selectorKey));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, this.textKey));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_chat_messagePanelHint));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        arrayList.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, this.textKey));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, this.dialogBackgroundKey));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, Theme.key_chat_attachEmptyImage));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, Theme.key_chat_attachPhotoBackground));
        return arrayList;
    }

    @Override
    public boolean isLightStatusBar() {
        return AndroidUtilities.computePerceivedBrightness(Theme.getColor(Theme.key_windowBackgroundGray)) > 0.721f;
    }

    @Override
    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return super.onBackPressed();
        }
        this.commentTextView.hidePopup(true);
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (this.imageReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
            this.imageReqId = 0;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        super.onFragmentDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.openSearch(true);
            if (!TextUtils.isEmpty(this.initialSearchString)) {
                this.searchItem.setSearchFieldText(this.initialSearchString, false);
                this.initialSearchString = null;
                processSearch(this.searchItem.getSearchField());
            }
            getParentActivity().getWindow().setSoftInputMode(32);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        ActionBarMenuItem actionBarMenuItem;
        if (!z || (actionBarMenuItem = this.searchItem) == null) {
            return;
        }
        AndroidUtilities.showKeyboard(actionBarMenuItem.getSearchField());
    }

    public void setCaption(CharSequence charSequence) {
        this.caption = charSequence;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.setText(charSequence);
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
    }

    public void setDocumentsPicker(boolean z) {
        this.isDocumentsPicker = z;
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    public void setLayoutViews(FrameLayout frameLayout, FrameLayout frameLayout2, View view, View view2, EditTextEmoji editTextEmoji) {
        this.frameLayout2 = frameLayout;
        this.writeButtonContainer = frameLayout2;
        this.commentTextView = editTextEmoji;
        this.selectedCountView = view;
        this.shadow = view2;
        this.needsBottomLayout = false;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
        if (i <= 0 || this.type != 1) {
            return;
        }
        this.maxSelectedPhotos = 1;
    }

    public void setSearchDelegate(PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate) {
        this.searchDelegate = photoPickerActivitySearchDelegate;
    }

    public void updatePhotosButton(int i) {
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false, i != 0);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, i != 0) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX(AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY(AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) View.SCALE_X, i == 1 ? 1.1f : 0.9f, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) View.SCALE_Y, i == 1 ? 1.1f : 0.9f, 1.0f));
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180L);
        animatorSet.start();
    }
}
