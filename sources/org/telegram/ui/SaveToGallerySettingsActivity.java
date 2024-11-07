package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.SaveToGallerySettingsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Cells.UserCell2;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.DialogsActivity;

public class SaveToGallerySettingsActivity extends BaseFragment {
    private final int VIEW_TYPE_ADD_EXCEPTION;
    private final int VIEW_TYPE_CHAT;
    private final int VIEW_TYPE_CHOOSER;
    private final int VIEW_TYPE_DELETE_ALL;
    private final int VIEW_TYPE_DIVIDER;
    private final int VIEW_TYPE_DIVIDER_INFO;
    private final int VIEW_TYPE_DIVIDER_LAST;
    private final int VIEW_TYPE_HEADER;
    private final int VIEW_TYPE_TOGGLE;
    Adapter adapter;
    SaveToGallerySettingsHelper.DialogException dialogException;
    long dialogId;
    LongSparseArray exceptionsDialogs;
    boolean isNewException;
    ArrayList items;
    RecyclerListView recyclerListView;
    int savePhotosRow;
    int saveVideosRow;
    int type;
    int videoDividerRow;

    public class Adapter extends AdapterWithDiffUtils {
        private Adapter() {
        }

        @Override
        public int getItemCount() {
            return SaveToGallerySettingsActivity.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            return ((Item) SaveToGallerySettingsActivity.this.items.get(i)).viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 2 || viewHolder.getItemViewType() == 4 || viewHolder.getItemViewType() == 6;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            int themedColor;
            int i2;
            if (((Item) SaveToGallerySettingsActivity.this.items.get(i)).viewType == 1) {
                ((TextCell) viewHolder.itemView).setNeedDivider(SaveToGallerySettingsActivity.this.exceptionsDialogs.size() > 0);
                return;
            }
            if (((Item) SaveToGallerySettingsActivity.this.items.get(i)).viewType == 6) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                SaveToGallerySettingsHelper.Settings settings = SaveToGallerySettingsActivity.this.getSettings();
                if (i == SaveToGallerySettingsActivity.this.savePhotosRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString(R.string.SaveToGalleryPhotos), settings.savePhoto, true);
                    themedColor = SaveToGallerySettingsActivity.this.getThemedColor(Theme.key_statisticChartLine_lightblue);
                    i2 = R.drawable.msg_filled_data_photos;
                } else {
                    textCheckCell.setTextAndCheck(LocaleController.getString(R.string.SaveToGalleryVideos), settings.saveVideo, false);
                    themedColor = SaveToGallerySettingsActivity.this.getThemedColor(Theme.key_statisticChartLine_green);
                    i2 = R.drawable.msg_filled_data_videos;
                }
                textCheckCell.setColorfullIcon(themedColor, i2);
                return;
            }
            if (((Item) SaveToGallerySettingsActivity.this.items.get(i)).viewType != 7) {
                if (((Item) SaveToGallerySettingsActivity.this.items.get(i)).viewType == 5) {
                    ((HeaderCell) viewHolder.itemView).setText(((Item) SaveToGallerySettingsActivity.this.items.get(i)).title);
                    return;
                }
                if (((Item) SaveToGallerySettingsActivity.this.items.get(i)).viewType == 2) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    SaveToGallerySettingsHelper.DialogException dialogException = ((Item) SaveToGallerySettingsActivity.this.items.get(i)).exception;
                    TLObject userOrChat = SaveToGallerySettingsActivity.this.getMessagesController().getUserOrChat(dialogException.dialogId);
                    if (userOrChat instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) userOrChat;
                        str = user.self ? LocaleController.getString(R.string.SavedMessages) : ContactsController.formatName(user.first_name, user.last_name);
                    } else {
                        str = userOrChat instanceof TLRPC.Chat ? ((TLRPC.Chat) userOrChat).title : null;
                    }
                    String str3 = str;
                    userCell.setSelfAsSavedMessages(true);
                    userCell.setData(userOrChat, str3, dialogException.createDescription(((BaseFragment) SaveToGallerySettingsActivity.this).currentAccount), 0, i == SaveToGallerySettingsActivity.this.items.size() - 1 || ((Item) SaveToGallerySettingsActivity.this.items.get(i + 1)).viewType == 2);
                    return;
                }
                return;
            }
            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
            SaveToGallerySettingsActivity saveToGallerySettingsActivity = SaveToGallerySettingsActivity.this;
            if (i == saveToGallerySettingsActivity.videoDividerRow) {
                long j = saveToGallerySettingsActivity.getSettings().limitVideo;
                SaveToGallerySettingsActivity saveToGallerySettingsActivity2 = SaveToGallerySettingsActivity.this;
                if (saveToGallerySettingsActivity2.dialogException != null) {
                    str2 = LocaleController.formatString("SaveToGalleryVideoHintCurrent", R.string.SaveToGalleryVideoHintCurrent, new Object[0]);
                } else {
                    int i3 = saveToGallerySettingsActivity2.type;
                    if (i3 == 1) {
                        str2 = LocaleController.formatString("SaveToGalleryVideoHintUser", R.string.SaveToGalleryVideoHintUser, new Object[0]);
                    } else if (i3 == 4) {
                        str2 = LocaleController.formatString("SaveToGalleryVideoHintChannels", R.string.SaveToGalleryVideoHintChannels, new Object[0]);
                    } else if (i3 != 2) {
                        return;
                    } else {
                        str2 = LocaleController.formatString("SaveToGalleryVideoHintGroup", R.string.SaveToGalleryVideoHintGroup, new Object[0]);
                    }
                }
            } else {
                str2 = ((Item) saveToGallerySettingsActivity.items.get(i)).title;
            }
            textInfoPrivacyCell.setText(str2);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2 = null;
            switch (i) {
                case 1:
                    TextCell textCell = new TextCell(viewGroup.getContext());
                    textCell.setTextAndIcon((CharSequence) LocaleController.getString(R.string.NotificationsAddAnException), R.drawable.msg_contact_add, true);
                    textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    view = textCell;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view;
                    break;
                case 2:
                    view = new UserCell(viewGroup.getContext(), 4, 0, false, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view;
                    break;
                case 3:
                    view2 = new ShadowSectionCell(viewGroup.getContext());
                    break;
                case 4:
                    TextCell textCell2 = new TextCell(viewGroup.getContext());
                    textCell2.setText(LocaleController.getString(R.string.NotificationsDeleteAllException), false);
                    textCell2.setColors(-1, Theme.key_text_RedRegular);
                    view = textCell2;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view;
                    break;
                case 5:
                    view = new HeaderCell(viewGroup.getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view;
                    break;
                case 6:
                    view = new TextCheckCell(viewGroup.getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = view;
                    break;
                case 7:
                    view2 = new TextInfoPrivacyCell(viewGroup.getContext());
                    break;
                case 8:
                    LinearLayout linearLayout = new LinearLayout(SaveToGallerySettingsActivity.this.getContext());
                    linearLayout.setOrientation(1);
                    final SeekBarView seekBarView = new SeekBarView(SaveToGallerySettingsActivity.this.getContext());
                    FrameLayout frameLayout = new FrameLayout(SaveToGallerySettingsActivity.this.getContext());
                    SaveToGallerySettingsActivity saveToGallerySettingsActivity = SaveToGallerySettingsActivity.this;
                    final SelectableAnimatedTextView selectableAnimatedTextView = new SelectableAnimatedTextView(saveToGallerySettingsActivity.getContext());
                    selectableAnimatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
                    selectableAnimatedTextView.setText(AndroidUtilities.formatFileSize(524288L, true, false));
                    frameLayout.addView(selectableAnimatedTextView, LayoutHelper.createFrame(-2, -2, 83));
                    SaveToGallerySettingsActivity saveToGallerySettingsActivity2 = SaveToGallerySettingsActivity.this;
                    final SelectableAnimatedTextView selectableAnimatedTextView2 = new SelectableAnimatedTextView(saveToGallerySettingsActivity2.getContext());
                    selectableAnimatedTextView2.setTextSize(AndroidUtilities.dp(13.0f));
                    frameLayout.addView(selectableAnimatedTextView2, LayoutHelper.createFrame(-2, -2, 81));
                    SaveToGallerySettingsActivity saveToGallerySettingsActivity3 = SaveToGallerySettingsActivity.this;
                    final SelectableAnimatedTextView selectableAnimatedTextView3 = new SelectableAnimatedTextView(saveToGallerySettingsActivity3.getContext());
                    selectableAnimatedTextView3.setTextSize(AndroidUtilities.dp(13.0f));
                    long j = 4194304000L;
                    selectableAnimatedTextView3.setText(AndroidUtilities.formatFileSize(4194304000L, true, false));
                    frameLayout.addView(selectableAnimatedTextView3, LayoutHelper.createFrame(-2, -2, 85));
                    linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 20, 0, 21, 10, 21, 0));
                    linearLayout.addView(seekBarView, LayoutHelper.createLinear(-1, 38, 0, 5, 0, 5, 4));
                    long j2 = SaveToGallerySettingsActivity.this.getSettings().limitVideo;
                    if (j2 >= 0 && j2 <= 4194304000L) {
                        j = j2;
                    }
                    seekBarView.setReportChanges(true);
                    seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                        @Override
                        public CharSequence getContentDescription() {
                            return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
                        }

                        @Override
                        public int getStepsCount() {
                            return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
                        }

                        @Override
                        public void onSeekBarDrag(boolean r10, float r11) {
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SaveToGallerySettingsActivity.Adapter.AnonymousClass1.onSeekBarDrag(boolean, float):void");
                        }

                        @Override
                        public void onSeekBarPressed(boolean z) {
                        }
                    });
                    seekBarView.setProgress(((float) j) > ((float) 104857600) * 0.7f ? ((((float) (j - 104857600)) / ((float) 4089446400L)) * 0.3f) + 0.7f : (((float) (j - 524288)) / ((float) 104333312)) * 0.7f);
                    seekBarView.delegate.onSeekBarDrag(false, seekBarView.getProgress());
                    linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = linearLayout;
                    break;
                case 9:
                    UserCell2 userCell2 = new UserCell2(SaveToGallerySettingsActivity.this.getContext(), 4, 0, SaveToGallerySettingsActivity.this.getResourceProvider());
                    userCell2.setData(DialogObject.isUserDialog(SaveToGallerySettingsActivity.this.dialogId) ? MessagesController.getInstance(((BaseFragment) SaveToGallerySettingsActivity.this).currentAccount).getUser(Long.valueOf(SaveToGallerySettingsActivity.this.dialogId)) : MessagesController.getInstance(((BaseFragment) SaveToGallerySettingsActivity.this).currentAccount).getChat(Long.valueOf(-SaveToGallerySettingsActivity.this.dialogId)), null, null, 0);
                    userCell2.setBackgroundColor(SaveToGallerySettingsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view2 = userCell2;
                    break;
                case 10:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(viewGroup.getContext());
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(SaveToGallerySettingsActivity.this.getContext(), R.drawable.greydivider_bottom, Theme.getColor(Theme.key_windowBackgroundGrayShadow, SaveToGallerySettingsActivity.this.getResourceProvider())));
                    view2 = shadowSectionCell;
                    break;
            }
            view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view2);
        }
    }

    public class Item extends AdapterWithDiffUtils.Item {
        final SaveToGallerySettingsHelper.DialogException exception;
        String title;

        private Item(int i) {
            super(i, false);
            this.exception = null;
        }

        private Item(int i, String str) {
            super(i, false);
            this.title = str;
            this.exception = null;
        }

        private Item(int i, SaveToGallerySettingsHelper.DialogException dialogException) {
            super(i, false);
            this.exception = dialogException;
        }

        public boolean equals(Object obj) {
            SaveToGallerySettingsHelper.DialogException dialogException;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            if (this.viewType != item.viewType) {
                return false;
            }
            String str = this.title;
            if (str != null) {
                return str.equals(item.title);
            }
            SaveToGallerySettingsHelper.DialogException dialogException2 = this.exception;
            return dialogException2 == null || (dialogException = item.exception) == null || dialogException2.dialogId == dialogException.dialogId;
        }
    }

    private class SelectableAnimatedTextView extends AnimatedTextView {
        AnimatedFloat progressToSelect;
        boolean selected;

        public SelectableAnimatedTextView(Context context) {
            super(context, true, true, false);
            this.progressToSelect = new AnimatedFloat(this);
            getDrawable().setAllowCancel(true);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.progressToSelect.set(this.selected ? 1.0f : 0.0f);
            setTextColor(ColorUtils.blendARGB(SaveToGallerySettingsActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText), SaveToGallerySettingsActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText), this.progressToSelect.get()));
            super.dispatchDraw(canvas);
        }

        public void setSelectedInternal(boolean z, boolean z2) {
            if (this.selected != z) {
                this.selected = z;
                this.progressToSelect.set(z ? 1.0f : 0.0f, z2);
                invalidate();
            }
        }
    }

    public SaveToGallerySettingsActivity(Bundle bundle) {
        super(bundle);
        this.VIEW_TYPE_ADD_EXCEPTION = 1;
        this.VIEW_TYPE_CHAT = 2;
        this.VIEW_TYPE_DIVIDER = 3;
        this.VIEW_TYPE_DELETE_ALL = 4;
        this.VIEW_TYPE_HEADER = 5;
        this.VIEW_TYPE_TOGGLE = 6;
        this.VIEW_TYPE_DIVIDER_INFO = 7;
        this.VIEW_TYPE_CHOOSER = 8;
        this.VIEW_TYPE_DIVIDER_LAST = 10;
        this.items = new ArrayList();
        this.exceptionsDialogs = new LongSparseArray();
    }

    public boolean lambda$createView$0(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId);
        bundle.putInt("type", this.type);
        presentFragment(new SaveToGallerySettingsActivity(bundle), true);
        return true;
    }

    public void lambda$createView$1() {
        this.exceptionsDialogs.clear();
        getUserConfig().updateSaveGalleryExceptions(this.type, this.exceptionsDialogs);
        updateRows();
    }

    public void lambda$createView$2(View view, int i, float f, float f2) {
        SaveToGallerySettingsActivity saveToGallerySettingsActivity;
        int i2;
        if (i == this.savePhotosRow) {
            getSettings().savePhoto = !r8.savePhoto;
        } else {
            if (i != this.saveVideosRow) {
                if (((Item) this.items.get(i)).viewType == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putBoolean("checkCanWrite", false);
                    int i3 = this.type;
                    if (i3 == 2) {
                        i2 = 6;
                    } else if (i3 == 4) {
                        i2 = 5;
                    } else {
                        bundle.putInt("dialogsType", 4);
                        bundle.putBoolean("allowGlobalSearch", false);
                        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                            @Override
                            public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i4, TopicsFragment topicsFragment) {
                                boolean lambda$createView$0;
                                lambda$createView$0 = SaveToGallerySettingsActivity.this.lambda$createView$0(dialogsActivity2, arrayList, charSequence, z, z2, i4, topicsFragment);
                                return lambda$createView$0;
                            }
                        });
                        saveToGallerySettingsActivity = dialogsActivity;
                    }
                    bundle.putInt("dialogsType", i2);
                    bundle.putBoolean("allowGlobalSearch", false);
                    DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
                    dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                        @Override
                        public final boolean didSelectDialogs(DialogsActivity dialogsActivity22, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i4, TopicsFragment topicsFragment) {
                            boolean lambda$createView$0;
                            lambda$createView$0 = SaveToGallerySettingsActivity.this.lambda$createView$0(dialogsActivity22, arrayList, charSequence, z, z2, i4, topicsFragment);
                            return lambda$createView$0;
                        }
                    });
                    saveToGallerySettingsActivity = dialogsActivity2;
                } else {
                    if (((Item) this.items.get(i)).viewType != 2) {
                        if (((Item) this.items.get(i)).viewType == 4) {
                            AlertDialog create = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.NotificationsDeleteAllExceptionTitle), LocaleController.getString(R.string.NotificationsDeleteAllExceptionAlert), LocaleController.getString(R.string.Delete), new Runnable() {
                                @Override
                                public final void run() {
                                    SaveToGallerySettingsActivity.this.lambda$createView$1();
                                }
                            }, null).create();
                            create.show();
                            create.redPositive();
                            return;
                        }
                        return;
                    }
                    Bundle bundle2 = new Bundle();
                    bundle2.putLong("dialog_id", ((Item) this.items.get(i)).exception.dialogId);
                    bundle2.putInt("type", this.type);
                    saveToGallerySettingsActivity = new SaveToGallerySettingsActivity(bundle2);
                }
                presentFragment(saveToGallerySettingsActivity);
                return;
            }
            getSettings().saveVideo = !r8.saveVideo;
        }
        onSettingsUpdated();
        updateRows();
    }

    public void lambda$createView$3(ActionBarPopupWindow actionBarPopupWindow, int i, View view) {
        actionBarPopupWindow.dismiss();
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", ((Item) this.items.get(i)).exception.dialogId);
        bundle.putInt("type", this.type);
        presentFragment(new SaveToGallerySettingsActivity(bundle));
    }

    public void lambda$createView$4(ActionBarPopupWindow actionBarPopupWindow, SaveToGallerySettingsHelper.DialogException dialogException, View view) {
        actionBarPopupWindow.dismiss();
        LongSparseArray<SaveToGallerySettingsHelper.DialogException> saveGalleryExceptions = getUserConfig().getSaveGalleryExceptions(this.type);
        saveGalleryExceptions.remove(dialogException.dialogId);
        getUserConfig().updateSaveGalleryExceptions(this.type, saveGalleryExceptions);
        updateRows();
    }

    public boolean lambda$createView$5(View view, final int i, float f, float f2) {
        if (((Item) this.items.get(i)).viewType != 2) {
            return false;
        }
        final SaveToGallerySettingsHelper.DialogException dialogException = ((Item) this.items.get(i)).exception;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
        ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, R.drawable.msg_customize, LocaleController.getString(R.string.EditException), false, null);
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteException), false, null);
        int i2 = Theme.key_text_RedRegular;
        addItem2.setColors(Theme.getColor(i2), Theme.getColor(i2));
        final ActionBarPopupWindow createSimplePopup = AlertsCreator.createSimplePopup(this, actionBarPopupWindowLayout, view, f, f2);
        actionBarPopupWindowLayout.setParentWindow(createSimplePopup);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                SaveToGallerySettingsActivity.this.lambda$createView$3(createSimplePopup, i, view2);
            }
        });
        addItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                SaveToGallerySettingsActivity.this.lambda$createView$4(createSimplePopup, dialogException, view2);
            }
        });
        return true;
    }

    public void lambda$createView$6(View view) {
        if (this.isNewException) {
            LongSparseArray<SaveToGallerySettingsHelper.DialogException> saveGalleryExceptions = getUserConfig().getSaveGalleryExceptions(this.type);
            SaveToGallerySettingsHelper.DialogException dialogException = this.dialogException;
            saveGalleryExceptions.put(dialogException.dialogId, dialogException);
            getUserConfig().updateSaveGalleryExceptions(this.type, saveGalleryExceptions);
        }
        lambda$onBackPressed$321();
    }

    public void onSettingsUpdated() {
        if (this.isNewException) {
            return;
        }
        if (this.dialogException == null) {
            SaveToGallerySettingsHelper.saveSettings(this.type);
            return;
        }
        LongSparseArray<SaveToGallerySettingsHelper.DialogException> saveGalleryExceptions = getUserConfig().getSaveGalleryExceptions(this.type);
        SaveToGallerySettingsHelper.DialogException dialogException = this.dialogException;
        saveGalleryExceptions.put(dialogException.dialogId, dialogException);
        getUserConfig().updateSaveGalleryExceptions(this.type, saveGalleryExceptions);
    }

    private void updateRows() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SaveToGallerySettingsActivity.updateRows():void");
    }

    @Override
    public View createView(Context context) {
        ActionBar actionBar;
        int i;
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    SaveToGallerySettingsActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        if (this.dialogException == null) {
            int i2 = this.type;
            if (i2 == 1) {
                actionBar = this.actionBar;
                i = R.string.SaveToGalleryPrivate;
            } else if (i2 == 2) {
                actionBar = this.actionBar;
                i = R.string.SaveToGalleryGroups;
            } else {
                actionBar = this.actionBar;
                i = R.string.SaveToGalleryChannels;
            }
        } else if (this.isNewException) {
            actionBar = this.actionBar;
            i = R.string.NotificationsNewException;
        } else {
            actionBar = this.actionBar;
            i = R.string.SaveToGalleryException;
        }
        actionBar.setTitle(LocaleController.getString(i));
        this.recyclerListView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(400L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView = this.recyclerListView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
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
                SaveToGallerySettingsActivity.this.lambda$createView$2(view, i3, f, f2);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() {
            @Override
            public final boolean onItemClick(View view, int i3, float f, float f2) {
                boolean lambda$createView$5;
                lambda$createView$5 = SaveToGallerySettingsActivity.this.lambda$createView$5(view, i3, f, f2);
                return lambda$createView$5;
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
        frameLayout.addView(this.recyclerListView);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        if (this.dialogException != null) {
            FrameLayout frameLayout2 = new FrameLayout(getContext());
            frameLayout2.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 8.0f));
            TextView textView = new TextView(getContext());
            textView.setTextSize(1, 14.0f);
            textView.setText(LocaleController.getString(this.isNewException ? R.string.AddException : R.string.SaveException));
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2, 17));
            frameLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SaveToGallerySettingsActivity.this.lambda$createView$6(view);
                }
            });
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 16.0f, 16.0f, 16.0f));
        }
        updateRows();
        return this.fragmentView;
    }

    SaveToGallerySettingsHelper.Settings getSettings() {
        SaveToGallerySettingsHelper.DialogException dialogException = this.dialogException;
        return dialogException != null ? dialogException : SaveToGallerySettingsHelper.getSettings(this.type);
    }

    @Override
    public boolean onFragmentCreate() {
        this.type = getArguments().getInt("type");
        this.exceptionsDialogs = getUserConfig().getSaveGalleryExceptions(this.type);
        long j = getArguments().getLong("dialog_id");
        this.dialogId = j;
        if (j != 0) {
            SaveToGallerySettingsHelper.DialogException dialogException = UserConfig.getInstance(this.currentAccount).getSaveGalleryExceptions(this.type).get(this.dialogId);
            this.dialogException = dialogException;
            if (dialogException == null) {
                this.isNewException = true;
                this.dialogException = new SaveToGallerySettingsHelper.DialogException();
                SaveToGallerySettingsHelper.Settings settings = SaveToGallerySettingsHelper.getSettings(this.type);
                SaveToGallerySettingsHelper.DialogException dialogException2 = this.dialogException;
                dialogException2.savePhoto = settings.savePhoto;
                dialogException2.saveVideo = settings.saveVideo;
                dialogException2.limitVideo = settings.limitVideo;
                dialogException2.dialogId = this.dialogId;
            }
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRows();
    }
}
