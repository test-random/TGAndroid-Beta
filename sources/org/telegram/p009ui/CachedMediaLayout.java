package org.telegram.p009ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C1072R;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.p009ui.ActionBar.ActionBarMenu;
import org.telegram.p009ui.ActionBar.ActionBarMenuItem;
import org.telegram.p009ui.ActionBar.ActionBarPopupWindow;
import org.telegram.p009ui.ActionBar.BackDrawable;
import org.telegram.p009ui.ActionBar.BaseFragment;
import org.telegram.p009ui.ActionBar.Theme;
import org.telegram.p009ui.CacheControlActivity;
import org.telegram.p009ui.CachedMediaLayout;
import org.telegram.p009ui.Cells.SharedAudioCell;
import org.telegram.p009ui.Cells.SharedDocumentCell;
import org.telegram.p009ui.Cells.SharedPhotoVideoCell2;
import org.telegram.p009ui.Components.AlertsCreator;
import org.telegram.p009ui.Components.AnimatedTextView;
import org.telegram.p009ui.Components.CheckBox2;
import org.telegram.p009ui.Components.CombinedDrawable;
import org.telegram.p009ui.Components.LayoutHelper;
import org.telegram.p009ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.p009ui.Components.NestedSizeNotifierLayout;
import org.telegram.p009ui.Components.RecyclerListView;
import org.telegram.p009ui.Components.ViewPagerFixed;
import org.telegram.p009ui.PhotoViewer;
import org.telegram.p009ui.Storage.CacheModel;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_peerUser;

public class CachedMediaLayout extends FrameLayout implements NestedSizeNotifierLayout.ChildLayout {
    private final LinearLayout actionModeLayout;
    private final ArrayList<View> actionModeViews;
    Page[] allPages;
    private final BackDrawable backDrawable;
    private int bottomPadding;
    CacheModel cacheModel;
    private final ActionBarMenuItem clearItem;
    private final ImageView closeButton;
    Delegate delegate;
    private final View divider;
    ArrayList<Page> pages;
    BaseFragment parentFragment;
    BasePlaceProvider placeProvider;
    public final AnimatedTextView selectedMessagesCountTextView;
    private final ViewPagerFixed.TabsView tabs;
    ViewPagerFixed viewPagerFixed;

    public interface Delegate {
        void clear();

        void clearSelection();

        void onItemSelected(CacheControlActivity.DialogFileEntities dialogFileEntities, CacheModel.FileInfo fileInfo, boolean z);
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    public void showActionMode(boolean z) {
    }

    public CachedMediaLayout(Context context, BaseFragment baseFragment) {
        super(context);
        this.actionModeViews = new ArrayList<>();
        this.pages = new ArrayList<>();
        Page[] pageArr = new Page[5];
        this.allPages = pageArr;
        this.parentFragment = baseFragment;
        pageArr[0] = new Page(this, LocaleController.getString("Chats", C1072R.string.Chats), 0, new DialogsAdapter(this, null), null);
        this.allPages[1] = new Page(this, LocaleController.getString("Media", C1072R.string.Media), 1, new MediaAdapter(this, null), null);
        this.allPages[2] = new Page(this, LocaleController.getString("Files", C1072R.string.Files), 2, new DocumentsAdapter(this, null), null);
        this.allPages[3] = new Page(this, LocaleController.getString("Music", C1072R.string.Music), 3, new MusicAdapter(this, null), null);
        int i = 0;
        while (true) {
            Page[] pageArr2 = this.allPages;
            if (i < pageArr2.length) {
                if (pageArr2[i] != null) {
                    this.pages.add(i, pageArr2[i]);
                }
                i++;
            } else {
                ViewPagerFixed viewPagerFixed = new ViewPagerFixed(getContext());
                this.viewPagerFixed = viewPagerFixed;
                viewPagerFixed.setAllowDisallowInterceptTouch(false);
                addView(this.viewPagerFixed, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 48.0f, 0.0f, 0.0f));
                ViewPagerFixed.TabsView createTabsView = this.viewPagerFixed.createTabsView(true);
                this.tabs = createTabsView;
                addView(createTabsView, LayoutHelper.createFrame(-1, 48.0f));
                View view = new View(getContext());
                this.divider = view;
                view.setBackgroundColor(Theme.getColor("divider"));
                addView(view, LayoutHelper.createFrame(-1, 1.0f, 0, 0.0f, 48.0f, 0.0f, 0.0f));
                view.getLayoutParams().height = 1;
                this.viewPagerFixed.setAdapter(new C13341(context, baseFragment));
                LinearLayout linearLayout = new LinearLayout(context);
                this.actionModeLayout = linearLayout;
                linearLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                linearLayout.setAlpha(0.0f);
                linearLayout.setClickable(true);
                addView(linearLayout, LayoutHelper.createFrame(-1, 48.0f));
                AndroidUtilities.updateViewVisibilityAnimated(linearLayout, false, 1.0f, false);
                ImageView imageView = new ImageView(context);
                this.closeButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                BackDrawable backDrawable = new BackDrawable(true);
                this.backDrawable = backDrawable;
                imageView.setImageDrawable(backDrawable);
                backDrawable.setColor(Theme.getColor("actionBarActionModeDefaultIcon"));
                imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
                imageView.setContentDescription(LocaleController.getString("Close", C1072R.string.Close));
                linearLayout.addView(imageView, new LinearLayout.LayoutParams(AndroidUtilities.m35dp(54.0f), -1));
                this.actionModeViews.add(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        CachedMediaLayout.this.lambda$new$0(view2);
                    }
                });
                AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
                this.selectedMessagesCountTextView = animatedTextView;
                animatedTextView.setTextSize(AndroidUtilities.m35dp(18.0f));
                animatedTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                animatedTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
                linearLayout.addView(animatedTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
                this.actionModeViews.add(animatedTextView);
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("actionBarActionModeDefaultIcon"), false);
                this.clearItem = actionBarMenuItem;
                actionBarMenuItem.setIcon(C1072R.C1073drawable.msg_clear);
                actionBarMenuItem.setContentDescription(LocaleController.getString("Delete", C1072R.string.Delete));
                actionBarMenuItem.setDuplicateParentStateEnabled(false);
                linearLayout.addView(actionBarMenuItem, new LinearLayout.LayoutParams(AndroidUtilities.m35dp(54.0f), -1));
                this.actionModeViews.add(actionBarMenuItem);
                actionBarMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        CachedMediaLayout.this.lambda$new$1(view2);
                    }
                });
                return;
            }
        }
    }

    public class C13341 extends ViewPagerFixed.Adapter {
        private ActionBarPopupWindow popupWindow;
        final Context val$context;
        final BaseFragment val$parentFragment;

        @Override
        public boolean hasStableId() {
            return true;
        }

        C13341(Context context, BaseFragment baseFragment) {
            this.val$context = context;
            this.val$parentFragment = baseFragment;
        }

        @Override
        public String getItemTitle(int i) {
            return CachedMediaLayout.this.pages.get(i).title;
        }

        @Override
        public int getItemCount() {
            return CachedMediaLayout.this.pages.size();
        }

        @Override
        public int getItemId(int i) {
            return CachedMediaLayout.this.pages.get(i).type;
        }

        @Override
        public View createView(int i) {
            final RecyclerListView recyclerListView = new RecyclerListView(this.val$context);
            DefaultItemAnimator defaultItemAnimator = (DefaultItemAnimator) recyclerListView.getItemAnimator();
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            recyclerListView.setClipToPadding(false);
            recyclerListView.setPadding(0, 0, 0, CachedMediaLayout.this.bottomPadding);
            recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int i2) {
                    BaseAdapter baseAdapter = (BaseAdapter) recyclerListView.getAdapter();
                    ItemInner itemInner = baseAdapter.itemInners.get(i2);
                    if (view instanceof SharedPhotoVideoCell2) {
                        MediaAdapter mediaAdapter = (MediaAdapter) baseAdapter;
                        PhotoViewer.getInstance().setParentActivity(C13341.this.val$parentFragment);
                        CachedMediaLayout cachedMediaLayout = CachedMediaLayout.this;
                        if (cachedMediaLayout.placeProvider == null) {
                            cachedMediaLayout.placeProvider = new BasePlaceProvider(cachedMediaLayout, null);
                        }
                        CachedMediaLayout.this.placeProvider.setRecyclerListView(recyclerListView);
                        PhotoViewer.getInstance().openPhotoForSelect(mediaAdapter.getPhotos(), i2, -1, false, CachedMediaLayout.this.placeProvider, null);
                        return;
                    }
                    Delegate delegate = CachedMediaLayout.this.delegate;
                    if (delegate != null) {
                        delegate.onItemSelected(itemInner.entities, itemInner.file, false);
                    }
                }
            });
            final BaseFragment baseFragment = this.val$parentFragment;
            recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() {
                @Override
                public final boolean onItemClick(View view, int i2, float f, float f2) {
                    boolean lambda$createView$3;
                    lambda$createView$3 = CachedMediaLayout.C13341.this.lambda$createView$3(recyclerListView, baseFragment, view, i2, f, f2);
                    return lambda$createView$3;
                }

                @Override
                public void onLongClickRelease() {
                    RecyclerListView.OnItemLongClickListenerExtended.CC.$default$onLongClickRelease(this);
                }

                @Override
                public void onMove(float f, float f2) {
                    RecyclerListView.OnItemLongClickListenerExtended.CC.$default$onMove(this, f, f2);
                }
            });
            return recyclerListView;
        }

        public boolean lambda$createView$3(RecyclerListView recyclerListView, BaseFragment baseFragment, final View view, int i, float f, float f2) {
            int i2;
            String str;
            final ItemInner itemInner = ((BaseAdapter) recyclerListView.getAdapter()).itemInners.get(i);
            if (view instanceof CacheCell) {
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(CachedMediaLayout.this.getContext());
                if (((CacheCell) view).container.getChildAt(0) instanceof SharedAudioCell) {
                    ActionBarMenuItem.addItem(actionBarPopupWindowLayout, C1072R.C1073drawable.msg_played, LocaleController.getString("PlayFile", C1072R.string.PlayFile), false, null).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            CachedMediaLayout.C13341.this.lambda$createView$0(itemInner, view, view2);
                        }
                    });
                } else {
                    ActionBarMenuItem.addItem(actionBarPopupWindowLayout, C1072R.C1073drawable.msg_view_file, LocaleController.getString("CacheOpenFile", C1072R.string.CacheOpenFile), false, null).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            CachedMediaLayout.C13341.this.lambda$createView$1(itemInner, view, view2);
                        }
                    });
                }
                int i3 = C1072R.C1073drawable.msg_select;
                if (CachedMediaLayout.this.cacheModel.selectedFiles.contains(itemInner.file)) {
                    i2 = C1072R.string.Deselect;
                    str = "Deselect";
                } else {
                    i2 = C1072R.string.Select;
                    str = "Select";
                }
                ActionBarMenuItem.addItem(actionBarPopupWindowLayout, i3, LocaleController.getString(str, i2), false, null).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        CachedMediaLayout.C13341.this.lambda$createView$2(itemInner, view2);
                    }
                });
                this.popupWindow = AlertsCreator.createSimplePopup(baseFragment, actionBarPopupWindowLayout, view, (int) f, (int) f2);
            } else {
                Delegate delegate = CachedMediaLayout.this.delegate;
                if (delegate != null) {
                    delegate.onItemSelected(itemInner.entities, itemInner.file, true);
                }
            }
            return true;
        }

        public void lambda$createView$0(ItemInner itemInner, View view, View view2) {
            CachedMediaLayout.this.openItem(itemInner.file, (CacheCell) view);
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        public void lambda$createView$1(ItemInner itemInner, View view, View view2) {
            CachedMediaLayout.this.openItem(itemInner.file, (CacheCell) view);
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        public void lambda$createView$2(ItemInner itemInner, View view) {
            Delegate delegate = CachedMediaLayout.this.delegate;
            if (delegate != null) {
                delegate.onItemSelected(itemInner.entities, itemInner.file, true);
            }
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        @Override
        public void bindView(View view, int i, int i2) {
            RecyclerListView recyclerListView = (RecyclerListView) view;
            recyclerListView.setAdapter(CachedMediaLayout.this.pages.get(i).adapter);
            if (CachedMediaLayout.this.pages.get(i).type == 1) {
                recyclerListView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            } else {
                recyclerListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            }
            recyclerListView.setTag(Integer.valueOf(CachedMediaLayout.this.pages.get(i).type));
        }
    }

    public void lambda$new$0(View view) {
        this.delegate.clearSelection();
    }

    public void lambda$new$1(View view) {
        this.delegate.clear();
    }

    public void openItem(CacheModel.FileInfo fileInfo, CacheCell cacheCell) {
        RecyclerListView recyclerListView = (RecyclerListView) this.viewPagerFixed.getCurrentView();
        if (cacheCell.type == 2) {
            if (!(recyclerListView.getAdapter() instanceof DocumentsAdapter)) {
                return;
            }
            DocumentsAdapter documentsAdapter = (DocumentsAdapter) recyclerListView.getAdapter();
            PhotoViewer.getInstance().setParentActivity(this.parentFragment);
            if (this.placeProvider == null) {
                this.placeProvider = new BasePlaceProvider(this, null);
            }
            this.placeProvider.setRecyclerListView(recyclerListView);
            if (fileIsMedia(fileInfo.file)) {
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(new MediaController.PhotoEntry(0, 0, 0L, fileInfo.file.getPath(), 0, fileInfo.type == 1, 0, 0, 0L));
                PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, -1, false, this.placeProvider, null);
            } else {
                File file = fileInfo.file;
                AndroidUtilities.openForView(file, file.getName(), null, this.parentFragment.getParentActivity(), null);
            }
        }
        if (cacheCell.type == 3) {
            if (MediaController.getInstance().isPlayingMessage(fileInfo.messageObject)) {
                if (!MediaController.getInstance().isMessagePaused()) {
                    MediaController.getInstance().lambda$startAudioAgain$7(fileInfo.messageObject);
                    return;
                } else {
                    MediaController.getInstance().playMessage(fileInfo.messageObject);
                    return;
                }
            }
            MediaController.getInstance().playMessage(fileInfo.messageObject);
        }
    }

    public SharedPhotoVideoCell2 getCellForIndex(int i) {
        RecyclerListView listView = getListView();
        for (int i2 = 0; i2 < listView.getChildCount(); i2++) {
            View childAt = listView.getChildAt(i2);
            if (listView.getChildAdapterPosition(childAt) == i && (childAt instanceof SharedPhotoVideoCell2)) {
                return (SharedPhotoVideoCell2) childAt;
            }
        }
        return null;
    }

    public void setCacheModel(CacheModel cacheModel) {
        this.cacheModel = cacheModel;
        update();
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
    }

    public void update() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.p009ui.CachedMediaLayout.update():void");
    }

    @Override
    public RecyclerListView getListView() {
        if (this.viewPagerFixed.getCurrentView() == null) {
            return null;
        }
        return (RecyclerListView) this.viewPagerFixed.getCurrentView();
    }

    public void updateVisibleRows() {
        for (int i = 0; i < this.viewPagerFixed.getViewPages().length; i++) {
            AndroidUtilities.updateVisibleRows((RecyclerListView) this.viewPagerFixed.getViewPages()[i]);
        }
    }

    public void setBottomPadding(int i) {
        this.bottomPadding = i;
        for (int i2 = 0; i2 < this.viewPagerFixed.getViewPages().length; i2++) {
            RecyclerListView recyclerListView = (RecyclerListView) this.viewPagerFixed.getViewPages()[i2];
            if (recyclerListView != null) {
                recyclerListView.setPadding(0, 0, 0, i);
            }
        }
    }

    public class Page {
        public final BaseAdapter adapter;
        public final String title;
        public final int type;

        Page(CachedMediaLayout cachedMediaLayout, String str, int i, BaseAdapter baseAdapter, C13341 c13341) {
            this(cachedMediaLayout, str, i, baseAdapter);
        }

        private Page(CachedMediaLayout cachedMediaLayout, String str, int i, BaseAdapter baseAdapter) {
            this.title = str;
            this.type = i;
            this.adapter = baseAdapter;
        }
    }

    public abstract class BaseAdapter extends AdapterWithDiffUtils {
        ArrayList<ItemInner> itemInners;

        abstract void update();

        private BaseAdapter(CachedMediaLayout cachedMediaLayout) {
            this.itemInners = new ArrayList<>();
        }

        BaseAdapter(CachedMediaLayout cachedMediaLayout, C13341 c13341) {
            this(cachedMediaLayout);
        }

        @Override
        public int getItemViewType(int i) {
            return this.itemInners.get(i).viewType;
        }

        @Override
        public int getItemCount() {
            return this.itemInners.size();
        }
    }

    private class DialogsAdapter extends BaseAdapter {
        ArrayList<ItemInner> old;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private DialogsAdapter() {
            super(CachedMediaLayout.this, null);
            this.old = new ArrayList<>();
        }

        DialogsAdapter(CachedMediaLayout cachedMediaLayout, C13341 c13341) {
            this();
        }

        @Override
        void update() {
            this.old.clear();
            this.old.addAll(this.itemInners);
            this.itemInners.clear();
            if (CachedMediaLayout.this.cacheModel != null) {
                for (int i = 0; i < CachedMediaLayout.this.cacheModel.entities.size(); i++) {
                    ArrayList<ItemInner> arrayList = this.itemInners;
                    CachedMediaLayout cachedMediaLayout = CachedMediaLayout.this;
                    arrayList.add(new ItemInner(cachedMediaLayout, 1, cachedMediaLayout.cacheModel.entities.get(i)));
                }
            }
            setItems(this.old, this.itemInners);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CacheControlActivity.UserCell userCell = null;
            if (i == 1) {
                CacheControlActivity.UserCell userCell2 = new CacheControlActivity.UserCell(CachedMediaLayout.this.getContext(), null);
                userCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                userCell = userCell2;
            }
            return new RecyclerListView.Holder(userCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String dialogPhotoTitle;
            if (viewHolder.getItemViewType() != 1) {
                return;
            }
            CacheControlActivity.UserCell userCell = (CacheControlActivity.UserCell) viewHolder.itemView;
            CacheControlActivity.DialogFileEntities dialogFileEntities = this.itemInners.get(i).entities;
            TLObject userOrChat = CachedMediaLayout.this.parentFragment.getMessagesController().getUserOrChat(dialogFileEntities.dialogId);
            CacheControlActivity.DialogFileEntities dialogFileEntities2 = userCell.dialogFileEntities;
            boolean z = dialogFileEntities2 != null && dialogFileEntities2.dialogId == dialogFileEntities.dialogId;
            if (dialogFileEntities.dialogId == Long.MAX_VALUE) {
                dialogPhotoTitle = LocaleController.getString("CacheOtherChats", C1072R.string.CacheOtherChats);
                userCell.getImageView().getAvatarDrawable().setAvatarType(14);
                userCell.getImageView().setForUserOrChat(null, userCell.getImageView().getAvatarDrawable());
            } else {
                dialogPhotoTitle = DialogObject.setDialogPhotoTitle(userCell.getImageView(), userOrChat);
            }
            userCell.dialogFileEntities = dialogFileEntities;
            userCell.getImageView().setRoundRadius(AndroidUtilities.m35dp(((userOrChat instanceof TLRPC$Chat) && ((TLRPC$Chat) userOrChat).forum) ? 12.0f : 19.0f));
            userCell.setTextAndValue(dialogPhotoTitle, AndroidUtilities.formatFileSize(dialogFileEntities.totalSize), i < getItemCount() + (-2));
            userCell.setChecked(CachedMediaLayout.this.cacheModel.isSelected(dialogFileEntities.dialogId), z);
        }
    }

    private abstract class BaseFilesAdapter extends BaseAdapter {
        ArrayList<ItemInner> oldItems;
        final int type;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        protected BaseFilesAdapter(int i) {
            super(CachedMediaLayout.this, null);
            this.oldItems = new ArrayList<>();
            this.type = i;
        }

        @Override
        void update() {
            this.oldItems.clear();
            this.oldItems.addAll(this.itemInners);
            this.itemInners.clear();
            CacheModel cacheModel = CachedMediaLayout.this.cacheModel;
            if (cacheModel != null) {
                ArrayList<CacheModel.FileInfo> arrayList = null;
                int i = this.type;
                if (i == 1) {
                    arrayList = cacheModel.media;
                } else if (i == 2) {
                    arrayList = cacheModel.documents;
                } else if (i == 3) {
                    arrayList = cacheModel.music;
                } else if (i == 4) {
                    arrayList = cacheModel.voice;
                }
                if (arrayList != null) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        this.itemInners.add(new ItemInner(CachedMediaLayout.this, 2, arrayList.get(i2)));
                    }
                }
            }
            setItems(this.oldItems, this.itemInners);
        }
    }

    public class ItemInner extends AdapterWithDiffUtils.Item {
        CacheControlActivity.DialogFileEntities entities;
        CacheModel.FileInfo file;

        public ItemInner(CachedMediaLayout cachedMediaLayout, int i, CacheControlActivity.DialogFileEntities dialogFileEntities) {
            super(i, true);
            this.entities = dialogFileEntities;
        }

        public ItemInner(CachedMediaLayout cachedMediaLayout, int i, CacheModel.FileInfo fileInfo) {
            super(i, true);
            this.file = fileInfo;
        }

        public boolean equals(Object obj) {
            CacheModel.FileInfo fileInfo;
            CacheModel.FileInfo fileInfo2;
            CacheControlActivity.DialogFileEntities dialogFileEntities;
            CacheControlActivity.DialogFileEntities dialogFileEntities2;
            if (this == obj) {
                return true;
            }
            if (obj != null && ItemInner.class == obj.getClass()) {
                ItemInner itemInner = (ItemInner) obj;
                int i = this.viewType;
                if (i == itemInner.viewType) {
                    if (i == 1 && (dialogFileEntities = this.entities) != null && (dialogFileEntities2 = itemInner.entities) != null) {
                        return dialogFileEntities.dialogId == dialogFileEntities2.dialogId;
                    } else if (i == 2 && (fileInfo = this.file) != null && (fileInfo2 = itemInner.file) != null) {
                        return Objects.equals(fileInfo.file, fileInfo2.file);
                    }
                }
            }
            return false;
        }
    }

    private class MediaAdapter extends BaseFilesAdapter {
        ArrayList<Object> photoEntries;
        private SharedPhotoVideoCell2.SharedResources sharedResources;
        CombinedDrawable thumb;

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        MediaAdapter(CachedMediaLayout cachedMediaLayout, C13341 c13341) {
            this();
        }

        private MediaAdapter() {
            super(1);
            this.photoEntries = new ArrayList<>();
        }

        @Override
        void update() {
            super.update();
            this.photoEntries.clear();
            for (int i = 0; i < this.itemInners.size(); i++) {
                ArrayList<Object> arrayList = this.photoEntries;
                String path = this.itemInners.get(i).file.file.getPath();
                boolean z = true;
                if (this.itemInners.get(i).file.type != 1) {
                    z = false;
                }
                arrayList.add(new MediaController.PhotoEntry(0, 0, 0L, path, 0, z, 0, 0, 0L));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (this.sharedResources == null) {
                this.sharedResources = new SharedPhotoVideoCell2.SharedResources(viewGroup.getContext(), null);
            }
            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = new SharedPhotoVideoCell2(viewGroup.getContext(), this.sharedResources, CachedMediaLayout.this.parentFragment.getCurrentAccount()) {
                @Override
                public void onCheckBoxPressed() {
                    CachedMediaLayout.this.delegate.onItemSelected(null, (CacheModel.FileInfo) getTag(), true);
                }
            };
            sharedPhotoVideoCell2.setStyle(1);
            return new RecyclerListView.Holder(sharedPhotoVideoCell2);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (this.thumb == null) {
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("chat_attachPhotoBackground")), Theme.chat_attachEmptyDrawable);
                this.thumb = combinedDrawable;
                combinedDrawable.setFullsize(true);
            }
            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) viewHolder.itemView;
            CacheModel.FileInfo fileInfo = this.itemInners.get(i).file;
            boolean z = fileInfo == sharedPhotoVideoCell2.getTag();
            sharedPhotoVideoCell2.setTag(fileInfo);
            if (fileInfo.type == 1) {
                ImageReceiver imageReceiver = sharedPhotoVideoCell2.imageReceiver;
                imageReceiver.setImage(ImageLocation.getForPath("vthumb://0:" + fileInfo.file.getAbsolutePath()), null, this.thumb, null, null, 0);
                sharedPhotoVideoCell2.setVideoText(AndroidUtilities.formatFileSize(fileInfo.size), true);
            } else {
                ImageReceiver imageReceiver2 = sharedPhotoVideoCell2.imageReceiver;
                imageReceiver2.setImage(ImageLocation.getForPath("thumb://0:" + fileInfo.file.getAbsolutePath()), null, this.thumb, null, null, 0);
                sharedPhotoVideoCell2.setVideoText(AndroidUtilities.formatFileSize(fileInfo.size), false);
            }
            sharedPhotoVideoCell2.setChecked(CachedMediaLayout.this.cacheModel.isSelected(fileInfo), z);
        }

        public ArrayList<Object> getPhotos() {
            return this.photoEntries;
        }
    }

    public class DocumentsAdapter extends BaseFilesAdapter {
        ArrayList<Object> photoEntries;

        DocumentsAdapter(CachedMediaLayout cachedMediaLayout, C13341 c13341) {
            this();
        }

        private DocumentsAdapter() {
            super(2);
            this.photoEntries = new ArrayList<>();
        }

        @Override
        void update() {
            super.update();
            this.photoEntries.clear();
            for (int i = 0; i < this.itemInners.size(); i++) {
                ArrayList<Object> arrayList = this.photoEntries;
                String path = this.itemInners.get(i).file.file.getPath();
                boolean z = true;
                if (this.itemInners.get(i).file.type != 1) {
                    z = false;
                }
                arrayList.add(new MediaController.PhotoEntry(0, 0, 0L, path, 0, z, 0, 0, 0L));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            CacheCell cacheCell = new CacheCell(viewGroup.getContext()) {
                {
                    CachedMediaLayout cachedMediaLayout = CachedMediaLayout.this;
                }

                @Override
                public void onCheckBoxPressed() {
                    CachedMediaLayout.this.delegate.onItemSelected(null, (CacheModel.FileInfo) getTag(), true);
                }
            };
            cacheCell.type = 2;
            cacheCell.container.addView(new SharedDocumentCell(viewGroup.getContext(), 3, null));
            return new RecyclerListView.Holder(cacheCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            CacheCell cacheCell = (CacheCell) viewHolder.itemView;
            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) cacheCell.container.getChildAt(0);
            CacheModel.FileInfo fileInfo = this.itemInners.get(i).file;
            boolean z = fileInfo == viewHolder.itemView.getTag();
            boolean z2 = i != this.itemInners.size() - 1;
            viewHolder.itemView.setTag(fileInfo);
            sharedDocumentCell.setTextAndValueAndTypeAndThumb(fileInfo.file.getName(), LocaleController.formatDateAudio(fileInfo.file.lastModified(), true), Utilities.getExtension(fileInfo.file.getName()), null, 0, z2);
            if (!z) {
                sharedDocumentCell.setPhoto(fileInfo.file.getPath());
            }
            cacheCell.drawDivider = z2;
            cacheCell.sizeTextView.setText(AndroidUtilities.formatFileSize(fileInfo.size));
            cacheCell.checkBox.setChecked(CachedMediaLayout.this.cacheModel.isSelected(fileInfo), z);
        }
    }

    private class MusicAdapter extends BaseFilesAdapter {
        MusicAdapter(CachedMediaLayout cachedMediaLayout, C13341 c13341) {
            this();
        }

        private MusicAdapter() {
            super(3);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            final CacheCell cacheCell = new CacheCell(viewGroup.getContext()) {
                {
                    CachedMediaLayout cachedMediaLayout = CachedMediaLayout.this;
                }

                @Override
                public void onCheckBoxPressed() {
                    CachedMediaLayout.this.delegate.onItemSelected(null, (CacheModel.FileInfo) getTag(), true);
                }
            };
            cacheCell.type = 3;
            SharedAudioCell sharedAudioCell = new SharedAudioCell(viewGroup.getContext(), 0, null) {
                @Override
                public void didPressedButton() {
                    CachedMediaLayout.this.openItem((CacheModel.FileInfo) cacheCell.getTag(), cacheCell);
                }
            };
            sharedAudioCell.setCheckForButtonPress(true);
            cacheCell.container.addView(sharedAudioCell);
            return new RecyclerListView.Holder(cacheCell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            CacheCell cacheCell = (CacheCell) viewHolder.itemView;
            SharedAudioCell sharedAudioCell = (SharedAudioCell) cacheCell.container.getChildAt(0);
            CacheModel.FileInfo fileInfo = this.itemInners.get(i).file;
            boolean z = fileInfo == cacheCell.getTag();
            boolean z2 = i != this.itemInners.size() - 1;
            cacheCell.setTag(fileInfo);
            CachedMediaLayout.this.checkMessageObjectForAudio(fileInfo, i);
            sharedAudioCell.setMessageObject(fileInfo.messageObject, z2);
            cacheCell.drawDivider = z2;
            cacheCell.sizeTextView.setText(AndroidUtilities.formatFileSize(fileInfo.size));
            cacheCell.checkBox.setChecked(CachedMediaLayout.this.cacheModel.isSelected(fileInfo), z);
        }
    }

    public void checkMessageObjectForAudio(CacheModel.FileInfo fileInfo, int i) {
        if (fileInfo.messageObject == null) {
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            tLRPC$TL_message.out = true;
            tLRPC$TL_message.f881id = i;
            tLRPC$TL_message.peer_id = new TLRPC$TL_peerUser();
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
            TLRPC$Peer tLRPC$Peer = tLRPC$TL_message.peer_id;
            long clientUserId = UserConfig.getInstance(this.parentFragment.getCurrentAccount()).getClientUserId();
            tLRPC$TL_peerUser.user_id = clientUserId;
            tLRPC$Peer.user_id = clientUserId;
            tLRPC$TL_message.date = (int) (System.currentTimeMillis() / 1000);
            tLRPC$TL_message.message = "";
            tLRPC$TL_message.attachPath = fileInfo.file.getPath();
            TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
            tLRPC$TL_message.media = tLRPC$TL_messageMediaDocument;
            tLRPC$TL_messageMediaDocument.flags |= 3;
            tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
            tLRPC$TL_message.flags |= 768;
            String fileExtension = FileLoader.getFileExtension(fileInfo.file);
            TLRPC$Document tLRPC$Document = tLRPC$TL_message.media.document;
            tLRPC$Document.f865id = 0L;
            tLRPC$Document.access_hash = 0L;
            tLRPC$Document.file_reference = new byte[0];
            tLRPC$Document.date = tLRPC$TL_message.date;
            StringBuilder sb = new StringBuilder();
            sb.append("audio/");
            if (fileExtension.length() <= 0) {
                fileExtension = "mp3";
            }
            sb.append(fileExtension);
            tLRPC$Document.mime_type = sb.toString();
            TLRPC$Document tLRPC$Document2 = tLRPC$TL_message.media.document;
            tLRPC$Document2.size = fileInfo.size;
            tLRPC$Document2.dc_id = 0;
            TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
            if (fileInfo.metadata == null) {
                fileInfo.metadata = new CacheModel.FileInfo.FileMetadata();
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(getContext(), Uri.fromFile(fileInfo.file));
                fileInfo.metadata.title = mediaMetadataRetriever.extractMetadata(7);
                fileInfo.metadata.author = mediaMetadataRetriever.extractMetadata(2);
                mediaMetadataRetriever.close();
            }
            CacheModel.FileInfo.FileMetadata fileMetadata = fileInfo.metadata;
            tLRPC$TL_documentAttributeAudio.title = fileMetadata.title;
            tLRPC$TL_documentAttributeAudio.performer = fileMetadata.author;
            tLRPC$TL_documentAttributeAudio.flags |= 3;
            tLRPC$TL_message.media.document.attributes.add(tLRPC$TL_documentAttributeAudio);
            TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
            tLRPC$TL_documentAttributeFilename.file_name = fileInfo.file.getName();
            tLRPC$TL_message.media.document.attributes.add(tLRPC$TL_documentAttributeFilename);
            MessageObject messageObject = new MessageObject(this.parentFragment.getCurrentAccount(), tLRPC$TL_message, false, false);
            fileInfo.messageObject = messageObject;
            messageObject.mediaExists = true;
        }
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public class BasePlaceProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        RecyclerListView recyclerListView;

        private BasePlaceProvider() {
        }

        BasePlaceProvider(CachedMediaLayout cachedMediaLayout, C13341 c13341) {
            this();
        }

        public void setRecyclerListView(RecyclerListView recyclerListView) {
            this.recyclerListView = recyclerListView;
        }

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            SharedPhotoVideoCell2 cellForIndex = CachedMediaLayout.this.getCellForIndex(i);
            if (cellForIndex != null) {
                int[] iArr = new int[2];
                cellForIndex.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1];
                placeProviderObject.parentView = this.recyclerListView;
                ImageReceiver imageReceiver = cellForIndex.imageReceiver;
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                placeProviderObject.scale = cellForIndex.getScaleX();
                return placeProviderObject;
            }
            return null;
        }
    }

    public class CacheCell extends FrameLayout {
        CheckBox2 checkBox;
        FrameLayout container;
        boolean drawDivider;
        TextView sizeTextView;
        int type;

        public void onCheckBoxPressed() {
        }

        public CacheCell(CachedMediaLayout cachedMediaLayout, Context context) {
            super(context);
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setDrawBackgroundAsArc(14);
            this.checkBox.setColor("radioBackground", "radioBackground", "checkboxCheck");
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 19, 18.0f, 0.0f, 0.0f, 0.0f));
            View view = new View(getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    CachedMediaLayout.CacheCell.this.lambda$new$0(view2);
                }
            });
            addView(view, LayoutHelper.createFrame(40, 40.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.container = frameLayout;
            addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 48.0f, 0.0f, 90.0f, 0.0f));
            TextView textView = new TextView(context);
            this.sizeTextView = textView;
            textView.setTextSize(1, 16.0f);
            this.sizeTextView.setGravity(5);
            this.sizeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
            addView(this.sizeTextView, LayoutHelper.createFrame(69, -2.0f, 21, 0.0f, 0.0f, 21.0f, 0.0f));
        }

        public void lambda$new$0(View view) {
            onCheckBoxPressed();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.drawDivider) {
                canvas.drawLine(getMeasuredWidth() - AndroidUtilities.m35dp(90.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public static boolean fileIsMedia(File file) {
        String lowerCase = file.getName().toLowerCase();
        return file.getName().endsWith("mp4") || file.getName().endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif");
    }
}