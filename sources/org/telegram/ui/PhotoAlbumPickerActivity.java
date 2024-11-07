package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoPickerActivity;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static int SELECT_TYPE_ALL = 0;
    public static int SELECT_TYPE_AVATAR = 1;
    public static int SELECT_TYPE_AVATAR_VIDEO = 3;
    public static int SELECT_TYPE_QR = 10;
    public static int SELECT_TYPE_WALLPAPER = 2;
    private boolean allowCaption;
    private boolean allowGifs;
    private CharSequence caption;
    private ChatActivity chatActivity;
    private EditTextEmoji commentTextView;
    private PhotoAlbumPickerActivityDelegate delegate;
    private TextView emptyView;
    private FrameLayout frameLayout2;
    private ActionBarMenuSubItem[] itemCells;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int maxSelectedPhotos;
    private FrameLayout progressView;
    private int selectPhotoType;
    private View selectedCountView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;
    private HashMap selectedPhotos = new HashMap();
    private ArrayList selectedPhotosOrder = new ArrayList();
    private ArrayList albumsSorted = null;
    private boolean loading = false;
    private int columnsCount = 2;
    private boolean allowSearchImages = true;
    private boolean allowOrder = true;
    private TextPaint textPaint = new TextPaint(1);
    private RectF rect = new RectF();
    private Paint paint = new Paint(1);

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public void lambda$onCreateViewHolder$0(MediaController.AlbumEntry albumEntry) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
        }

        @Override
        public int getItemCount() {
            if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                return (int) Math.ceil(PhotoAlbumPickerActivity.this.albumsSorted.size() / PhotoAlbumPickerActivity.this.columnsCount);
            }
            return 0;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) viewHolder.itemView;
            photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
            for (int i2 = 0; i2 < PhotoAlbumPickerActivity.this.columnsCount; i2++) {
                int i3 = (PhotoAlbumPickerActivity.this.columnsCount * i) + i2;
                photoPickerAlbumsCell.setAlbum(i2, i3 < PhotoAlbumPickerActivity.this.albumsSorted.size() ? (MediaController.AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(i3) : null);
            }
            photoPickerAlbumsCell.requestLayout();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = new PhotoPickerAlbumsCell(this.mContext);
            photoPickerAlbumsCell.setDelegate(new PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate() {
                @Override
                public final void didSelectAlbum(MediaController.AlbumEntry albumEntry) {
                    PhotoAlbumPickerActivity.ListAdapter.this.lambda$onCreateViewHolder$0(albumEntry);
                }
            });
            return new RecyclerListView.Holder(photoPickerAlbumsCell);
        }
    }

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList arrayList, boolean z, int i);

        void startPhotoSelectActivity();
    }

    public PhotoAlbumPickerActivity(int i, boolean z, boolean z2, ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.selectPhotoType = i;
        this.allowGifs = z;
        this.allowCaption = z2;
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    PhotoAlbumPickerActivity.this.fixLayoutInternal();
                    if (PhotoAlbumPickerActivity.this.listView == null) {
                        return true;
                    }
                    PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    public void fixLayoutInternal() {
        if (getParentActivity() == null) {
            return;
        }
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        this.columnsCount = 2;
        if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
            this.columnsCount = 4;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public static boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$2(boolean z, int i) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, z, i);
        lambda$onBackPressed$321();
    }

    public void lambda$createView$3(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity != null && chatActivity.isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                @Override
                public final void didSelectDate(boolean z, int i) {
                    PhotoAlbumPickerActivity.this.lambda$createView$2(z, i);
                }
            });
        } else {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            lambda$onBackPressed$321();
        }
    }

    public void lambda$createView$4(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public void lambda$createView$5(boolean z, int i) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, z, i);
        lambda$onBackPressed$321();
    }

    public void lambda$createView$6(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                @Override
                public final void didSelectDate(boolean z, int i2) {
                    PhotoAlbumPickerActivity.this.lambda$createView$5(z, i2);
                }
            });
        } else {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0);
            lambda$onBackPressed$321();
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

                    @Override
                    public boolean onTouch(View view2, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || PhotoAlbumPickerActivity.this.sendPopupWindow == null || !PhotoAlbumPickerActivity.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view2.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        PhotoAlbumPickerActivity.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    @Override
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        PhotoAlbumPickerActivity.this.lambda$createView$4(keyEvent);
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
                                PhotoAlbumPickerActivity.this.lambda$createView$6(i3, view2);
                            }
                        });
                    }
                    i3++;
                }
                this.sendPopupLayout.setupRadialSelectors(Theme.getColor(Theme.key_dialogButtonSelector));
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

    public void openPhotoPicker(MediaController.AlbumEntry albumEntry, int i) {
        if (albumEntry != null) {
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(i, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, this.selectPhotoType, this.allowCaption, this.chatActivity, false);
            Editable text = this.commentTextView.getText();
            this.caption = text;
            photoPickerActivity.setCaption(text);
            photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                @Override
                public void actionButtonPressed(boolean z, boolean z2, int i2) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (z) {
                        return;
                    }
                    PhotoAlbumPickerActivity photoAlbumPickerActivity = PhotoAlbumPickerActivity.this;
                    photoAlbumPickerActivity.sendSelectedPhotos(photoAlbumPickerActivity.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder, z2, i2);
                }

                @Override
                public boolean canFinishFragment() {
                    return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
                }

                @Override
                public void onCaptionChanged(CharSequence charSequence) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
                }

                @Override
                public void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                @Override
                public void selectedPhotosChanged() {
                    PhotoAlbumPickerActivity.this.updatePhotosButton();
                }
            });
            photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerActivity);
            return;
        }
        final HashMap hashMap = new HashMap();
        final ArrayList arrayList = new ArrayList();
        if (this.allowGifs) {
            PhotoPickerSearchActivity photoPickerSearchActivity = new PhotoPickerSearchActivity(hashMap, arrayList, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text2 = this.commentTextView.getText();
            this.caption = text2;
            photoPickerSearchActivity.setCaption(text2);
            photoPickerSearchActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                @Override
                public void actionButtonPressed(boolean z, boolean z2, int i2) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (z) {
                        return;
                    }
                    PhotoAlbumPickerActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i2);
                }

                @Override
                public boolean canFinishFragment() {
                    return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
                }

                @Override
                public void onCaptionChanged(CharSequence charSequence) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
                }

                @Override
                public void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                @Override
                public void selectedPhotosChanged() {
                }
            });
            photoPickerSearchActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerSearchActivity);
            return;
        }
        PhotoPickerActivity photoPickerActivity2 = new PhotoPickerActivity(0, albumEntry, hashMap, arrayList, this.selectPhotoType, this.allowCaption, this.chatActivity, false);
        Editable text3 = this.commentTextView.getText();
        this.caption = text3;
        photoPickerActivity2.setCaption(text3);
        photoPickerActivity2.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            @Override
            public void actionButtonPressed(boolean z, boolean z2, int i2) {
                PhotoAlbumPickerActivity.this.removeSelfFromStack();
                if (z) {
                    return;
                }
                PhotoAlbumPickerActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i2);
            }

            @Override
            public boolean canFinishFragment() {
                return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
            }

            @Override
            public void onCaptionChanged(CharSequence charSequence) {
                PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = charSequence);
            }

            @Override
            public void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            @Override
            public void selectedPhotosChanged() {
            }
        });
        photoPickerActivity2.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        presentFragment(photoPickerActivity2);
    }

    public void sendSelectedPhotos(HashMap hashMap, ArrayList arrayList, boolean z, int i) {
        if (hashMap.isEmpty() || this.delegate == null || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            Object obj = hashMap.get(arrayList.get(i2));
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
            arrayList2.add(sendingMediaInfo);
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
        this.delegate.didSelectPhotos(arrayList2, z, i);
    }

    private boolean showCommentTextView(boolean z) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        FrameLayout frameLayout = this.frameLayout2;
        int i = z ? 0 : 4;
        frameLayout.setVisibility(i);
        this.writeButtonContainer.setVisibility(i);
        this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
        this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
        this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
        this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
        this.selectedCountView.setScaleY(z ? 1.0f : 0.2f);
        this.selectedCountView.setAlpha(z ? 1.0f : 0.0f);
        this.frameLayout2.setTranslationY(z ? 0.0f : AndroidUtilities.dp(48.0f));
        this.shadow.setTranslationY(z ? 0.0f : AndroidUtilities.dp(48.0f));
        return true;
    }

    public void updatePhotosButton() {
        boolean z;
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            z = false;
        } else {
            this.selectedCountView.invalidate();
            z = true;
        }
        showCommentTextView(z);
    }

    @Override
    public View createView(Context context) {
        RecyclerListView recyclerListView;
        TextView textView;
        ArrayList arrayList;
        ActionBar actionBar = this.actionBar;
        int i = Theme.key_dialogBackground;
        actionBar.setBackgroundColor(Theme.getColor(i));
        ActionBar actionBar2 = this.actionBar;
        int i2 = Theme.key_dialogTextBlack;
        actionBar2.setTitleColor(Theme.getColor(i2));
        this.actionBar.setItemsColor(Theme.getColor(i2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    PhotoAlbumPickerActivity.this.lambda$onBackPressed$321();
                    return;
                }
                if (i3 != 1) {
                    if (i3 == 2) {
                        PhotoAlbumPickerActivity.this.openPhotoPicker(null, 0);
                    }
                } else if (PhotoAlbumPickerActivity.this.delegate != null) {
                    PhotoAlbumPickerActivity.this.finishFragment(false);
                    PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.allowSearchImages) {
            createMenu.addItem(2, R.drawable.ic_ab_search).setContentDescription(LocaleController.getString(R.string.Search));
        }
        ActionBarMenuItem addItem = createMenu.addItem(0, R.drawable.ic_ab_other);
        addItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        addItem.addSubItem(1, R.drawable.msg_openin, LocaleController.getString(R.string.OpenInExternalApp));
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
            private boolean ignoreLayout;
            private int lastNotifyWidth;

            @Override
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoAlbumPickerActivity.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                int makeMeasureSpec;
                int paddingTop;
                int size = View.MeasureSpec.getSize(i3);
                int size2 = View.MeasureSpec.getSize(i4);
                setMeasuredDimension(size, size2);
                if (AndroidUtilities.dp(20.0f) < 0) {
                    this.ignoreLayout = true;
                    PhotoAlbumPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
                    i4 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
                }
                int childCount = getChildCount();
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt != null && childAt.getVisibility() != 8) {
                        if (PhotoAlbumPickerActivity.this.commentTextView == null || !PhotoAlbumPickerActivity.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i3, 0, i4, 0);
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
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.sizeNotifierFrameLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(i));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.actionBar.setTitle(LocaleController.getString(R.string.Gallery));
        RecyclerListView recyclerListView2 = new RecyclerListView(context);
        this.listView = recyclerListView2;
        recyclerListView2.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(54.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setDrawingCacheEnabled(false);
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor(i));
        TextView textView2 = new TextView(context);
        this.emptyView = textView2;
        textView2.setTextColor(-8355712);
        this.emptyView.setTextSize(1, 20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString(R.string.NoPhotos));
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = PhotoAlbumPickerActivity.lambda$createView$0(view, motionEvent);
                return lambda$createView$0;
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.progressView = frameLayout;
        frameLayout.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        radialProgressView.setProgressColor(-11371101);
        this.progressView.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.shadow.setTranslationY(AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout2 = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(i));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$createView$1;
                lambda$createView$1 = PhotoAlbumPickerActivity.lambda$createView$1(view2, motionEvent);
                return lambda$createView$1;
            }
        });
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context, this.sizeNotifierFrameLayout, null, 1, false);
        this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString(R.string.AddCaption));
        EditTextCaption editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        CharSequence charSequence = this.caption;
        if (charSequence != null) {
            this.commentTextView.setText(charSequence);
        }
        FrameLayout frameLayout3 = new FrameLayout(context) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoAlbumPickerActivity.this.selectedPhotos.size(), new Object[0]));
                accessibilityNodeInfo.setClassName(Button.class.getName());
                accessibilityNodeInfo.setLongClickable(true);
                accessibilityNodeInfo.setClickable(true);
            }
        };
        this.writeButtonContainer = frameLayout3;
        frameLayout3.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        this.writeButton = new ImageView(context);
        int dp = AndroidUtilities.dp(56.0f);
        int i3 = Theme.key_dialogFloatingButton;
        int color = Theme.getColor(i3);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 21) {
            i3 = Theme.key_dialogFloatingButtonPressed;
        }
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(i3));
        if (i4 < 21) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
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
        if (i4 >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view2, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(i4 >= 21 ? 56 : 60, i4 >= 21 ? 56.0f : 60.0f, 51, i4 >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PhotoAlbumPickerActivity.this.lambda$createView$3(view2);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view2) {
                boolean lambda$createView$7;
                lambda$createView$7 = PhotoAlbumPickerActivity.this.lambda$createView$7(view2);
                return lambda$createView$7;
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.bold());
        View view2 = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                String format = String.format("%d", Integer.valueOf(Math.max(1, PhotoAlbumPickerActivity.this.selectedPhotosOrder.size())));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(PhotoAlbumPickerActivity.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                getMeasuredHeight();
                PhotoAlbumPickerActivity.this.textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                int i5 = max / 2;
                PhotoAlbumPickerActivity.this.rect.set(measuredWidth - i5, 0.0f, i5 + measuredWidth, getMeasuredHeight());
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), PhotoAlbumPickerActivity.this.paint);
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
                PhotoAlbumPickerActivity.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), PhotoAlbumPickerActivity.this.paint);
                canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), PhotoAlbumPickerActivity.this.textPaint);
            }
        };
        this.selectedCountView = view2;
        view2.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
        if (this.selectPhotoType != SELECT_TYPE_ALL) {
            this.commentTextView.setVisibility(8);
        }
        if (this.loading && ((arrayList = this.albumsSorted) == null || arrayList.isEmpty())) {
            this.progressView.setVisibility(0);
            recyclerListView = this.listView;
            textView = null;
        } else {
            this.progressView.setVisibility(8);
            recyclerListView = this.listView;
            textView = this.emptyView;
        }
        recyclerListView.setEmptyView(textView);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.albumsDidLoad) {
            if (i == NotificationCenter.closeChats) {
                removeSelfFromStack(true);
                return;
            }
            return;
        }
        if (this.classGuid == ((Integer) objArr[0]).intValue()) {
            int i3 = this.selectPhotoType;
            this.albumsSorted = (ArrayList) ((i3 == SELECT_TYPE_AVATAR || i3 == SELECT_TYPE_WALLPAPER || i3 == SELECT_TYPE_QR || !this.allowSearchImages) ? objArr[2] : objArr[1]);
            FrameLayout frameLayout = this.progressView;
            if (frameLayout != null) {
                frameLayout.setVisibility(8);
            }
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null && recyclerListView.getEmptyView() == null) {
                this.listView.setEmptyView(this.emptyView);
            }
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            this.loading = false;
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        View view = this.fragmentView;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_dialogBackground;
        arrayList.add(new ThemeDescription(view, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i2));
        ActionBar actionBar = this.actionBar;
        int i3 = ThemeDescription.FLAG_AB_ITEMSCOLOR;
        int i4 = Theme.key_dialogTextBlack;
        arrayList.add(new ThemeDescription(actionBar, i3, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_dialogButtonSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, Theme.key_chat_attachEmptyImage));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, Theme.key_chat_attachPhotoBackground));
        return arrayList;
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
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    @Override
    public boolean onFragmentCreate() {
        int i = this.selectPhotoType;
        this.albumsSorted = (i == SELECT_TYPE_AVATAR || i == SELECT_TYPE_WALLPAPER || i == SELECT_TYPE_QR || !this.allowSearchImages) ? MediaController.allPhotoAlbums : MediaController.allMediaAlbums;
        this.loading = this.albumsSorted == null;
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
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
        fixLayout();
    }

    public void setAllowSearchImages(boolean z) {
        this.allowSearchImages = z;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate photoAlbumPickerActivityDelegate) {
        this.delegate = photoAlbumPickerActivityDelegate;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }
}
