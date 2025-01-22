package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CacheByChatsController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.KeepMediaPopupView;

public class CacheChatsExceptionsFragment extends BaseFragment {
    private final int VIEW_TYPE_ADD_EXCEPTION;
    private final int VIEW_TYPE_CHAT;
    private final int VIEW_TYPE_DELETE_ALL;
    private final int VIEW_TYPE_DIVIDER;
    Adapter adapter;
    int currentType;
    ArrayList exceptionsDialogs;
    ArrayList items;
    RecyclerListView recyclerListView;

    public class Adapter extends AdapterWithDiffUtils {
        private Adapter() {
        }

        @Override
        public int getItemCount() {
            return CacheChatsExceptionsFragment.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            return ((Item) CacheChatsExceptionsFragment.this.items.get(i)).viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 2 || viewHolder.getItemViewType() == 4;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            if (((Item) CacheChatsExceptionsFragment.this.items.get(i)).viewType == 2) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                CacheByChatsController.KeepMediaException keepMediaException = ((Item) CacheChatsExceptionsFragment.this.items.get(i)).exception;
                TLObject userOrChat = CacheChatsExceptionsFragment.this.getMessagesController().getUserOrChat(keepMediaException.dialogId);
                if (userOrChat instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) userOrChat;
                    str = user.self ? LocaleController.getString(R.string.SavedMessages) : ContactsController.formatName(user.first_name, user.last_name);
                } else {
                    str = userOrChat instanceof TLRPC.Chat ? ((TLRPC.Chat) userOrChat).title : null;
                }
                String str2 = str;
                userCell.setSelfAsSavedMessages(true);
                userCell.setData(userOrChat, str2, CacheByChatsController.getKeepMediaString(keepMediaException.keepMedia), 0, i == CacheChatsExceptionsFragment.this.items.size() - 1 || ((Item) CacheChatsExceptionsFragment.this.items.get(i + 1)).viewType == 2);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            View view3;
            if (i == 1) {
                TextCell textCell = new TextCell(viewGroup.getContext());
                textCell.setTextAndIcon((CharSequence) LocaleController.getString(R.string.NotificationsAddAnException), R.drawable.msg_contact_add, true);
                textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                view3 = textCell;
            } else if (i == 2) {
                view3 = new UserCell(viewGroup.getContext(), 4, 0, false, false);
            } else {
                if (i == 3) {
                    view = new ShadowSectionCell(viewGroup.getContext());
                    view2 = view;
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view2);
                }
                if (i != 4) {
                    view2 = null;
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view2);
                }
                TextCell textCell2 = new TextCell(viewGroup.getContext());
                textCell2.setText(LocaleController.getString(R.string.NotificationsDeleteAllException), false);
                textCell2.setColors(-1, Theme.key_text_RedRegular);
                view3 = textCell2;
            }
            view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view = view3;
            view2 = view;
            view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view2);
        }
    }

    public class Item extends AdapterWithDiffUtils.Item {
        final CacheByChatsController.KeepMediaException exception;

        private Item(int i, CacheByChatsController.KeepMediaException keepMediaException) {
            super(i, false);
            this.exception = keepMediaException;
        }

        public boolean equals(Object obj) {
            CacheByChatsController.KeepMediaException keepMediaException;
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
            CacheByChatsController.KeepMediaException keepMediaException2 = this.exception;
            return keepMediaException2 == null || (keepMediaException = item.exception) == null || keepMediaException2.dialogId == keepMediaException.dialogId;
        }
    }

    public CacheChatsExceptionsFragment(Bundle bundle) {
        super(bundle);
        this.VIEW_TYPE_ADD_EXCEPTION = 1;
        this.VIEW_TYPE_CHAT = 2;
        this.VIEW_TYPE_DIVIDER = 3;
        this.VIEW_TYPE_DELETE_ALL = 4;
        this.items = new ArrayList();
        this.exceptionsDialogs = new ArrayList();
    }

    public boolean lambda$createView$0(DialogsActivity dialogsActivity, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        dialogsActivity.lambda$onBackPressed$323();
        CacheByChatsController.KeepMediaException keepMediaException = null;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            boolean z3 = true;
            if (i3 >= arrayList.size()) {
                break;
            }
            int i4 = 0;
            while (true) {
                if (i4 >= this.exceptionsDialogs.size()) {
                    z3 = false;
                    break;
                }
                if (((CacheByChatsController.KeepMediaException) this.exceptionsDialogs.get(i4)).dialogId == ((MessagesStorage.TopicKey) arrayList.get(i3)).dialogId) {
                    keepMediaException = (CacheByChatsController.KeepMediaException) this.exceptionsDialogs.get(i4);
                    break;
                }
                i4++;
            }
            if (!z3) {
                int i5 = CacheByChatsController.KEEP_MEDIA_FOREVER;
                if (getMessagesController().getCacheByChatsController().getKeepMedia(this.currentType) == CacheByChatsController.KEEP_MEDIA_FOREVER) {
                    i5 = CacheByChatsController.KEEP_MEDIA_ONE_DAY;
                }
                ArrayList arrayList2 = this.exceptionsDialogs;
                CacheByChatsController.KeepMediaException keepMediaException2 = new CacheByChatsController.KeepMediaException(((MessagesStorage.TopicKey) arrayList.get(i3)).dialogId, i5);
                arrayList2.add(keepMediaException2);
                keepMediaException = keepMediaException2;
            }
            i3++;
        }
        getMessagesController().getCacheByChatsController().saveKeepMediaExceptions(this.currentType, this.exceptionsDialogs);
        updateRows();
        if (keepMediaException != null) {
            int i6 = 0;
            while (true) {
                if (i6 < this.items.size()) {
                    if (((Item) this.items.get(i6)).exception != null && ((Item) this.items.get(i6)).exception.dialogId == keepMediaException.dialogId) {
                        i2 = i6;
                        break;
                    }
                    i6++;
                } else {
                    break;
                }
            }
            this.recyclerListView.scrollToPosition(i2);
            showPopupFor(keepMediaException);
        }
        return true;
    }

    public void lambda$createView$1(CacheByChatsController.KeepMediaException keepMediaException, int i, int i2) {
        if (i2 == CacheByChatsController.KEEP_MEDIA_DELETE) {
            this.exceptionsDialogs.remove(keepMediaException);
            updateRows();
        } else {
            keepMediaException.keepMedia = i2;
            AndroidUtilities.updateVisibleRows(this.recyclerListView);
        }
        getMessagesController().getCacheByChatsController().saveKeepMediaExceptions(this.currentType, this.exceptionsDialogs);
    }

    public void lambda$createView$2() {
        this.exceptionsDialogs.clear();
        getMessagesController().getCacheByChatsController().saveKeepMediaExceptions(this.currentType, this.exceptionsDialogs);
        updateRows();
        lambda$onBackPressed$323();
    }

    public void lambda$createView$3(View view, int i, float f, float f2) {
        int i2;
        if (((Item) this.items.get(i)).viewType != 1) {
            if (((Item) this.items.get(i)).viewType == 2) {
                final CacheByChatsController.KeepMediaException keepMediaException = ((Item) this.items.get(i)).exception;
                KeepMediaPopupView keepMediaPopupView = new KeepMediaPopupView(this, view.getContext());
                keepMediaPopupView.updateForDialog(false);
                keepMediaPopupView.setParentWindow(AlertsCreator.createSimplePopup(this, keepMediaPopupView, view, f, f2));
                keepMediaPopupView.setCallback(new KeepMediaPopupView.Callback() {
                    @Override
                    public final void onKeepMediaChange(int i3, int i4) {
                        CacheChatsExceptionsFragment.this.lambda$createView$1(keepMediaException, i3, i4);
                    }
                });
                return;
            }
            if (((Item) this.items.get(i)).viewType == 4) {
                AlertDialog create = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.NotificationsDeleteAllExceptionTitle), LocaleController.getString(R.string.NotificationsDeleteAllExceptionAlert), LocaleController.getString(R.string.Delete), new Runnable() {
                    @Override
                    public final void run() {
                        CacheChatsExceptionsFragment.this.lambda$createView$2();
                    }
                }, null).create();
                create.show();
                create.redPositive();
                return;
            }
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putBoolean("checkCanWrite", false);
        int i3 = this.currentType;
        if (i3 == 1) {
            i2 = 6;
        } else {
            if (i3 != 2) {
                bundle.putInt("dialogsType", 4);
                bundle.putBoolean("allowGlobalSearch", false);
                final DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                    @Override
                    public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i4, TopicsFragment topicsFragment) {
                        boolean lambda$createView$0;
                        lambda$createView$0 = CacheChatsExceptionsFragment.this.lambda$createView$0(dialogsActivity, dialogsActivity2, arrayList, charSequence, z, z2, i4, topicsFragment);
                        return lambda$createView$0;
                    }
                });
                presentFragment(dialogsActivity);
            }
            i2 = 5;
        }
        bundle.putInt("dialogsType", i2);
        bundle.putBoolean("allowGlobalSearch", false);
        final DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
        dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
            @Override
            public final boolean didSelectDialogs(DialogsActivity dialogsActivity22, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i4, TopicsFragment topicsFragment) {
                boolean lambda$createView$0;
                lambda$createView$0 = CacheChatsExceptionsFragment.this.lambda$createView$0(dialogsActivity2, dialogsActivity22, arrayList, charSequence, z, z2, i4, topicsFragment);
                return lambda$createView$0;
            }
        });
        presentFragment(dialogsActivity2);
    }

    public void lambda$showPopupFor$4(CacheByChatsController.KeepMediaException keepMediaException, int i, int i2) {
        keepMediaException.keepMedia = i2;
        getMessagesController().getCacheByChatsController().saveKeepMediaExceptions(this.currentType, this.exceptionsDialogs);
        AndroidUtilities.updateVisibleRows(this.recyclerListView);
    }

    public void lambda$showPopupFor$5(final CacheByChatsController.KeepMediaException keepMediaException) {
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 < this.items.size()) {
                if (((Item) this.items.get(i2)).exception != null && ((Item) this.items.get(i2)).exception.dialogId == keepMediaException.dialogId) {
                    i = i2;
                    break;
                }
                i2++;
            } else {
                break;
            }
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.recyclerListView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            KeepMediaPopupView keepMediaPopupView = new KeepMediaPopupView(this, getContext());
            keepMediaPopupView.updateForDialog(true);
            keepMediaPopupView.setParentWindow(AlertsCreator.createSimplePopup(this, keepMediaPopupView, findViewHolderForAdapterPosition.itemView, r2.getMeasuredWidth() / 2.0f, findViewHolderForAdapterPosition.itemView.getMeasuredHeight() / 2.0f));
            keepMediaPopupView.setCallback(new KeepMediaPopupView.Callback() {
                @Override
                public final void onKeepMediaChange(int i3, int i4) {
                    CacheChatsExceptionsFragment.this.lambda$showPopupFor$4(keepMediaException, i3, i4);
                }
            });
        }
    }

    private void updateRows() {
        ArrayList arrayList;
        boolean z = false;
        int i = 1;
        CacheByChatsController.KeepMediaException keepMediaException = null;
        Object[] objArr = 0;
        Object[] objArr2 = 0;
        Object[] objArr3 = 0;
        Object[] objArr4 = 0;
        Object[] objArr5 = 0;
        Object[] objArr6 = 0;
        Object[] objArr7 = 0;
        Object[] objArr8 = 0;
        if ((this.isPaused || this.adapter == null) ? false : true) {
            arrayList = new ArrayList();
            arrayList.addAll(this.items);
        } else {
            arrayList = null;
        }
        this.items.clear();
        this.items.add(new Item(i, keepMediaException));
        Iterator it = this.exceptionsDialogs.iterator();
        while (it.hasNext()) {
            this.items.add(new Item(2, (CacheByChatsController.KeepMediaException) it.next()));
            z = true;
        }
        int i2 = 3;
        if (z) {
            this.items.add(new Item(i2, objArr6 == true ? 1 : 0));
            this.items.add(new Item(4, objArr4 == true ? 1 : 0));
        }
        this.items.add(new Item(i2, objArr2 == true ? 1 : 0));
        Adapter adapter = this.adapter;
        if (adapter != null) {
            if (arrayList != null) {
                adapter.setItems(arrayList, this.items);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    CacheChatsExceptionsFragment.this.lambda$onBackPressed$323();
                }
            }
        });
        this.actionBar.setTitle(LocaleController.getString(R.string.NotificationsExceptions));
        this.recyclerListView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
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
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                CacheChatsExceptionsFragment.this.lambda$createView$3(view, i, f, f2);
            }
        });
        frameLayout.addView(this.recyclerListView);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        updateRows();
        return this.fragmentView;
    }

    @Override
    public boolean onFragmentCreate() {
        this.currentType = getArguments().getInt("type");
        updateRows();
        return super.onFragmentCreate();
    }

    public void setExceptions(ArrayList arrayList) {
        this.exceptionsDialogs = arrayList;
        updateRows();
    }

    public void showPopupFor(final CacheByChatsController.KeepMediaException keepMediaException) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                CacheChatsExceptionsFragment.this.lambda$showPopupFor$5(keepMediaException);
            }
        }, 150L);
    }
}
