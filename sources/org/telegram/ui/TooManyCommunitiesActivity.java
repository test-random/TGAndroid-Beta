package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TooManyCommunitiesHintCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.TooManyCommunitiesActivity;

public class TooManyCommunitiesActivity extends BaseFragment {
    private Adapter adapter;
    private int buttonAnimation;
    private FrameLayout buttonLayout;
    private TextView buttonTextView;
    private EmptyTextProgressView emptyView;
    private ValueAnimator enterAnimator;
    private float enterProgress;
    private TooManyCommunitiesHintCell hintCell;
    private RecyclerListView listView;
    protected RadialProgressView progressBar;
    private SearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private FrameLayout searchViewContainer;
    int type;
    private ArrayList inactiveChats = new ArrayList();
    private ArrayList inactiveChatsSignatures = new ArrayList();
    private Set selectedIds = new HashSet();
    private int buttonHeight = AndroidUtilities.dp(64.0f);
    Runnable showProgressRunnable = new Runnable() {
        @Override
        public void run() {
            TooManyCommunitiesActivity.this.progressBar.setVisibility(0);
            TooManyCommunitiesActivity.this.progressBar.setAlpha(0.0f);
            TooManyCommunitiesActivity.this.progressBar.animate().alpha(1.0f).start();
        }
    };
    RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
        @Override
        public final void onItemClick(View view, int i) {
            TooManyCommunitiesActivity.this.lambda$new$0(view, i);
        }
    };
    RecyclerListView.OnItemLongClickListener onItemLongClickListener = new RecyclerListView.OnItemLongClickListener() {
        @Override
        public final boolean onItemClick(View view, int i) {
            boolean lambda$new$1;
            lambda$new$1 = TooManyCommunitiesActivity.this.lambda$new$1(view, i);
            return lambda$new$1;
        }
    };

    public class Adapter extends RecyclerListView.SelectionAdapter {
        int endPaddingPosition;
        int headerPosition;
        int hintPosition;
        int inactiveChatsEndRow;
        int inactiveChatsStartRow;
        int rowCount;
        int shadowPosition;

        Adapter() {
        }

        @Override
        public int getItemCount() {
            return this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == this.hintPosition) {
                return 1;
            }
            if (i == this.shadowPosition) {
                return 2;
            }
            if (i == this.headerPosition) {
                return 3;
            }
            return i == this.endPaddingPosition ? 5 : 4;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() >= this.inactiveChatsStartRow && viewHolder.getAdapterPosition() < this.inactiveChatsEndRow;
        }

        @Override
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            View view;
            float f;
            int i2 = this.headerPosition;
            if (i < i2 || i2 <= 0) {
                view = viewHolder.itemView;
                f = 1.0f;
            } else {
                view = viewHolder.itemView;
                f = TooManyCommunitiesActivity.this.enterProgress;
            }
            view.setAlpha(f);
            if (getItemViewType(i) == 4) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                TLRPC.Chat chat = (TLRPC.Chat) TooManyCommunitiesActivity.this.inactiveChats.get(i - this.inactiveChatsStartRow);
                groupCreateUserCell.setObject(chat, chat.title, (String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(i - this.inactiveChatsStartRow), i != this.inactiveChatsEndRow - 1);
                groupCreateUserCell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Long.valueOf(chat.id)), false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 1) {
                TooManyCommunitiesActivity.this.hintCell = new TooManyCommunitiesHintCell(viewGroup.getContext());
                View view2 = TooManyCommunitiesActivity.this.hintCell;
                int i2 = TooManyCommunitiesActivity.this.type;
                TooManyCommunitiesActivity.this.hintCell.setMessageText(LocaleController.getString(i2 == 0 ? R.string.TooManyCommunitiesHintJoin : i2 == 1 ? R.string.TooManyCommunitiesHintEdit : R.string.TooManyCommunitiesHintCreate));
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = AndroidUtilities.dp(16.0f);
                ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(23.0f);
                TooManyCommunitiesActivity.this.hintCell.setLayoutParams(layoutParams);
                view = view2;
            } else if (i == 2) {
                View shadowSectionCell = new ShadowSectionCell(viewGroup.getContext());
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(viewGroup.getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                combinedDrawable.setFullsize(true);
                shadowSectionCell.setBackground(combinedDrawable);
                view = shadowSectionCell;
            } else if (i != 3) {
                view = i != 5 ? new GroupCreateUserCell(viewGroup.getContext(), 1, 0, false) : new EmptyCell(viewGroup.getContext(), AndroidUtilities.dp(12.0f));
            } else {
                HeaderCell headerCell = new HeaderCell(viewGroup.getContext(), Theme.key_windowBackgroundWhiteBlueHeader, 21, 8, false);
                headerCell.setHeight(54);
                headerCell.setText(LocaleController.getString(R.string.InactiveChats));
                view = headerCell;
            }
            return new RecyclerListView.Holder(view);
        }

        public void updateRows() {
            this.headerPosition = -1;
            this.inactiveChatsStartRow = -1;
            this.inactiveChatsEndRow = -1;
            this.endPaddingPosition = -1;
            this.hintPosition = 0;
            this.rowCount = 2;
            this.shadowPosition = 1;
            if (TooManyCommunitiesActivity.this.inactiveChats.isEmpty()) {
                return;
            }
            int i = this.rowCount;
            int i2 = i + 1;
            this.headerPosition = i;
            int i3 = i + 2;
            this.rowCount = i3;
            this.inactiveChatsStartRow = i2;
            int size = i3 + (TooManyCommunitiesActivity.this.inactiveChats.size() - 1);
            this.inactiveChatsEndRow = size;
            this.rowCount = size + 1;
            this.endPaddingPosition = size;
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        ArrayList searchResults = new ArrayList();
        ArrayList searchResultsSignatures = new ArrayList();
        private Runnable searchRunnable;

        SearchAdapter() {
        }

        public void lambda$processSearch$1(String str, int i) {
            String lowerCase = str.trim().toLowerCase();
            String str2 = null;
            if (lowerCase.length() == 0) {
                updateSearchResults(null, null, i);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (!lowerCase.equals(translitString) && translitString.length() != 0) {
                str2 = translitString;
            }
            int i2 = (str2 != null ? 1 : 0) + 1;
            String[] strArr = new String[i2];
            strArr[0] = lowerCase;
            if (str2 != null) {
                strArr[1] = str2;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i3 = 0; i3 < TooManyCommunitiesActivity.this.inactiveChats.size(); i3++) {
                TLRPC.Chat chat = (TLRPC.Chat) TooManyCommunitiesActivity.this.inactiveChats.get(i3);
                int i4 = 0;
                boolean z = false;
                while (true) {
                    if (i4 >= 2) {
                        break;
                    }
                    String publicUsername = i4 == 0 ? chat.title : ChatObject.getPublicUsername(chat);
                    if (publicUsername != null) {
                        String lowerCase2 = publicUsername.toLowerCase();
                        for (int i5 = 0; i5 < i2; i5++) {
                            String str3 = strArr[i5];
                            if (!lowerCase2.startsWith(str3)) {
                                if (!lowerCase2.contains(" " + str3)) {
                                }
                            }
                            z = true;
                            break;
                        }
                        if (z) {
                            arrayList.add(chat);
                            arrayList2.add((String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(i3));
                            break;
                        }
                    }
                    i4++;
                }
            }
            updateSearchResults(arrayList, arrayList2, i);
        }

        public void lambda$updateSearchResults$2(int i, ArrayList arrayList, ArrayList arrayList2) {
            EmptyTextProgressView emptyTextProgressView;
            int i2;
            if (i != this.lastSearchId) {
                return;
            }
            this.searchResults.clear();
            this.searchResultsSignatures.clear();
            if (arrayList != null) {
                this.searchResults.addAll(arrayList);
                this.searchResultsSignatures.addAll(arrayList2);
            }
            notifyDataSetChanged();
            if (this.searchResults.isEmpty()) {
                emptyTextProgressView = TooManyCommunitiesActivity.this.emptyView;
                i2 = 0;
            } else {
                emptyTextProgressView = TooManyCommunitiesActivity.this.emptyView;
                i2 = 8;
            }
            emptyTextProgressView.setVisibility(i2);
        }

        private void updateSearchResults(final ArrayList arrayList, final ArrayList arrayList2, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.lambda$updateSearchResults$2(i, arrayList, arrayList2);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.searchResults.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC.Chat chat = (TLRPC.Chat) this.searchResults.get(i);
            String str = (String) this.searchResultsSignatures.get(i);
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
            groupCreateUserCell.setObject(chat, chat.title, str, i != this.searchResults.size() - 1);
            groupCreateUserCell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Long.valueOf(chat.id)), false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new GroupCreateUserCell(viewGroup.getContext(), 1, 0, false));
        }

        public void lambda$search$0(final String str, final int i) {
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.lambda$processSearch$1(str, i);
                }
            });
        }

        public void search(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResults.clear();
                this.searchResultsSignatures.clear();
                notifyDataSetChanged();
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                return;
            }
            final int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.lambda$search$0(str, i);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }
    }

    public TooManyCommunitiesActivity(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", i);
        this.arguments = bundle;
    }

    public void lambda$createView$2(View view) {
        if (this.selectedIds.isEmpty()) {
            return;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.inactiveChats.size(); i++) {
            if (this.selectedIds.contains(Long.valueOf(((TLRPC.Chat) this.inactiveChats.get(i)).id))) {
                arrayList.add((TLRPC.Chat) this.inactiveChats.get(i));
            }
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC.Chat chat = (TLRPC.Chat) arrayList.get(i2);
            getMessagesController().putChat(chat, false);
            getMessagesController().deleteParticipantFromChat(chat.id, user);
        }
        lambda$onBackPressed$323();
    }

    public void lambda$getThemeDescriptions$6() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
        RecyclerListView recyclerListView2 = this.searchListView;
        if (recyclerListView2 != null) {
            int childCount2 = recyclerListView2.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt2 = this.searchListView.getChildAt(i2);
                if (childAt2 instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt2).update(0);
                }
            }
        }
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_progressCircle));
    }

    public void lambda$loadInactiveChannels$3(ValueAnimator valueAnimator) {
        View childAt;
        float f;
        this.enterProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RecyclerListView recyclerListView = this.listView;
            int childAdapterPosition = recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i));
            int i2 = this.adapter.headerPosition;
            if (childAdapterPosition < i2 || i2 <= 0) {
                childAt = this.listView.getChildAt(i);
                f = 1.0f;
            } else {
                childAt = this.listView.getChildAt(i);
                f = this.enterProgress;
            }
            childAt.setAlpha(f);
        }
    }

    public void lambda$loadInactiveChannels$4(ArrayList arrayList, TLRPC.TL_messages_inactiveChats tL_messages_inactiveChats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(arrayList);
        this.inactiveChats.addAll(tL_messages_inactiveChats.chats);
        this.adapter.notifyDataSetChanged();
        if (this.listView.getMeasuredHeight() > 0) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.enterAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TooManyCommunitiesActivity.this.lambda$loadInactiveChannels$3(valueAnimator);
                }
            });
            this.enterAnimator.setDuration(100L);
            this.enterAnimator.start();
        } else {
            this.enterProgress = 1.0f;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (this.progressBar.getVisibility() == 0) {
            this.progressBar.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    TooManyCommunitiesActivity.this.progressBar.setVisibility(8);
                }
            }).start();
        }
    }

    public void lambda$loadInactiveChannels$5(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            final TLRPC.TL_messages_inactiveChats tL_messages_inactiveChats = (TLRPC.TL_messages_inactiveChats) tLObject;
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_messages_inactiveChats.chats.size(); i++) {
                TLRPC.Chat chat = tL_messages_inactiveChats.chats.get(i);
                int currentTime = (getConnectionsManager().getCurrentTime() - tL_messages_inactiveChats.dates.get(i).intValue()) / 86400;
                String formatPluralString = currentTime < 30 ? LocaleController.formatPluralString("Days", currentTime, new Object[0]) : currentTime < 365 ? LocaleController.formatPluralString("Months", currentTime / 30, new Object[0]) : LocaleController.formatPluralString("Years", currentTime / 365, new Object[0]);
                arrayList.add(ChatObject.isMegagroup(chat) ? LocaleController.formatString("InactiveChatSignature", R.string.InactiveChatSignature, LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]), formatPluralString) : ChatObject.isChannel(chat) ? LocaleController.formatString("InactiveChannelSignature", R.string.InactiveChannelSignature, formatPluralString) : LocaleController.formatString("InactiveChatSignature", R.string.InactiveChatSignature, LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]), formatPluralString));
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    TooManyCommunitiesActivity.this.lambda$loadInactiveChannels$4(arrayList, tL_messages_inactiveChats);
                }
            });
        }
    }

    public void lambda$new$0(View view, int i) {
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            TLRPC.Chat chat = (TLRPC.Chat) groupCreateUserCell.getObject();
            if (this.selectedIds.contains(Long.valueOf(chat.id))) {
                this.selectedIds.remove(Long.valueOf(chat.id));
                groupCreateUserCell.setChecked(false, true);
            } else {
                this.selectedIds.add(Long.valueOf(chat.id));
                groupCreateUserCell.setChecked(true, true);
            }
            onSelectedCountChange();
            if (this.selectedIds.isEmpty()) {
                return;
            }
            RecyclerListView recyclerListView = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
            int height = recyclerListView.getHeight() - view.getBottom();
            int i2 = this.buttonHeight;
            if (height < i2) {
                recyclerListView.smoothScrollBy(0, i2 - height);
            }
        }
    }

    public boolean lambda$new$1(View view, int i) {
        this.onItemClickListener.onItemClick(view, i);
        return true;
    }

    private void loadInactiveChannels() {
        this.adapter.notifyDataSetChanged();
        this.enterProgress = 0.0f;
        AndroidUtilities.runOnUIThread(this.showProgressRunnable, 500L);
        getConnectionsManager().sendRequest(new TLRPC.TL_channels_getInactiveChannels(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TooManyCommunitiesActivity.this.lambda$loadInactiveChannels$5(tLObject, tL_error);
            }
        });
    }

    private void onSelectedCountChange() {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (this.selectedIds.isEmpty() && this.buttonAnimation != -1 && this.buttonLayout.getVisibility() == 0) {
            this.buttonAnimation = -1;
            this.buttonLayout.animate().setListener(null).cancel();
            this.buttonLayout.animate().translationY(this.buttonHeight).setDuration(200L).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    TooManyCommunitiesActivity.this.buttonAnimation = 0;
                    TooManyCommunitiesActivity.this.buttonLayout.setVisibility(8);
                }
            }).start();
            RecyclerListView recyclerListView = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
            recyclerListView.hideSelector(false);
            int findLastVisibleItemPosition = ((LinearLayoutManager) recyclerListView.getLayoutManager()).findLastVisibleItemPosition();
            if ((findLastVisibleItemPosition == recyclerListView.getAdapter().getItemCount() - 1 || (findLastVisibleItemPosition == recyclerListView.getAdapter().getItemCount() - 2 && recyclerListView == this.listView)) && (findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(findLastVisibleItemPosition)) != null) {
                int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
                if (findLastVisibleItemPosition == this.adapter.getItemCount() - 2) {
                    bottom += AndroidUtilities.dp(12.0f);
                }
                if (recyclerListView.getMeasuredHeight() - bottom <= this.buttonHeight) {
                    recyclerListView.setTranslationY(-(recyclerListView.getMeasuredHeight() - bottom));
                    recyclerListView.animate().translationY(0.0f).setDuration(200L).start();
                }
            }
            this.listView.setPadding(0, 0, 0, 0);
            this.searchListView.setPadding(0, 0, 0, 0);
        }
        if (!this.selectedIds.isEmpty() && this.buttonLayout.getVisibility() == 8 && this.buttonAnimation != 1) {
            this.buttonAnimation = 1;
            this.buttonLayout.setVisibility(0);
            this.buttonLayout.setTranslationY(this.buttonHeight);
            this.buttonLayout.animate().setListener(null).cancel();
            this.buttonLayout.animate().translationY(0.0f).setDuration(200L).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    TooManyCommunitiesActivity.this.buttonAnimation = 0;
                }
            }).start();
            this.listView.setPadding(0, 0, 0, this.buttonHeight - AndroidUtilities.dp(12.0f));
            this.searchListView.setPadding(0, 0, 0, this.buttonHeight);
        }
        if (this.selectedIds.isEmpty()) {
            return;
        }
        this.buttonTextView.setText(LocaleController.formatString("LeaveChats", R.string.LeaveChats, LocaleController.formatPluralString("Chats", this.selectedIds.size(), new Object[0])));
    }

    @Override
    public View createView(Context context) {
        this.type = this.arguments.getInt("type", 0);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.LimitReached));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    TooManyCommunitiesActivity.this.lambda$onBackPressed$323();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            boolean expanded = false;

            @Override
            public void onSearchCollapse() {
                super.onSearchCollapse();
                if (TooManyCommunitiesActivity.this.listView.getVisibility() != 0) {
                    TooManyCommunitiesActivity.this.listView.setVisibility(0);
                    TooManyCommunitiesActivity.this.listView.setAlpha(0.0f);
                }
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                TooManyCommunitiesActivity.this.adapter.notifyDataSetChanged();
                TooManyCommunitiesActivity.this.listView.animate().alpha(1.0f).setDuration(150L).setListener(null).start();
                TooManyCommunitiesActivity.this.searchViewContainer.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(8);
                    }
                }).start();
                this.expanded = false;
            }

            @Override
            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                TooManyCommunitiesActivity.this.searchAdapter.search(obj);
                if (this.expanded || TextUtils.isEmpty(obj)) {
                    if (this.expanded && TextUtils.isEmpty(obj)) {
                        onSearchCollapse();
                        return;
                    }
                    return;
                }
                if (TooManyCommunitiesActivity.this.searchViewContainer.getVisibility() != 0) {
                    TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(0);
                    TooManyCommunitiesActivity.this.searchViewContainer.setAlpha(0.0f);
                }
                TooManyCommunitiesActivity.this.listView.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        TooManyCommunitiesActivity.this.listView.setVisibility(8);
                    }
                }).start();
                TooManyCommunitiesActivity.this.searchAdapter.searchResultsSignatures.clear();
                TooManyCommunitiesActivity.this.searchAdapter.searchResults.clear();
                TooManyCommunitiesActivity.this.searchAdapter.notifyDataSetChanged();
                TooManyCommunitiesActivity.this.searchViewContainer.animate().setListener(null).alpha(1.0f).setDuration(150L).start();
                this.expanded = true;
            }
        });
        int i = R.string.Search;
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString(i));
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(i));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.listView.setClipToPadding(false);
        this.listView.setOnItemClickListener(this.onItemClickListener);
        this.listView.setOnItemLongClickListener(this.onItemLongClickListener);
        RecyclerListView recyclerListView3 = new RecyclerListView(context);
        this.searchListView = recyclerListView3;
        recyclerListView3.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView4 = this.searchListView;
        SearchAdapter searchAdapter = new SearchAdapter();
        this.searchAdapter = searchAdapter;
        recyclerListView4.setAdapter(searchAdapter);
        this.searchListView.setOnItemClickListener(this.onItemClickListener);
        this.searchListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 1) {
                    AndroidUtilities.hideKeyboard(TooManyCommunitiesActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString(R.string.NoResult));
        this.emptyView.showTextView();
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, -2.0f));
        this.adapter.updateRows();
        this.progressBar.setVisibility(8);
        frameLayout.addView(this.listView);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.searchViewContainer = frameLayout2;
        frameLayout2.addView(this.searchListView);
        this.searchViewContainer.addView(this.emptyView);
        this.searchViewContainer.setVisibility(8);
        frameLayout.addView(this.searchViewContainer);
        loadInactiveChannels();
        View view = this.fragmentView;
        int i2 = Theme.key_windowBackgroundWhite;
        view.setBackgroundColor(Theme.getColor(i2));
        FrameLayout frameLayout3 = new FrameLayout(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
        };
        this.buttonLayout = frameLayout3;
        frameLayout3.setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.buttonTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.bold());
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
        frameLayout.addView(this.buttonLayout, LayoutHelper.createFrame(-1, 64, 80));
        this.buttonLayout.setBackgroundColor(Theme.getColor(i2));
        this.buttonLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 12.0f, 16.0f, 12.0f));
        this.buttonLayout.setVisibility(8);
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                TooManyCommunitiesActivity.this.lambda$createView$2(view2);
            }
        });
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                TooManyCommunitiesActivity.this.lambda$getThemeDescriptions$6();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_AB_ITEMSCOLOR;
        int i2 = Theme.key_actionBarDefaultIcon;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        int i3 = Theme.key_chats_nameMessage_threeLines;
        arrayList.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        View view = this.fragmentView;
        int i4 = ThemeDescription.FLAG_BACKGROUND;
        int i5 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(view, i4, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.buttonLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int i6 = Theme.key_groupcreate_sectionText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        int i7 = Theme.key_checkbox;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        int i8 = Theme.key_checkboxDisabled;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        int i9 = Theme.key_checkboxCheck;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        int i10 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        int i11 = Theme.key_windowBackgroundWhiteGrayText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        Drawable[] drawableArr = Theme.avatarDrawables;
        int i12 = Theme.key_avatar_text;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, drawableArr, null, i12));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{GroupCreateUserCell.class}, null, Theme.avatarDrawables, null, i12));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.buttonTextView, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_addButton));
        TextView textView = this.buttonTextView;
        int i13 = Theme.key_featuredStickers_addButtonPressed;
        arrayList.add(new ThemeDescription(textView, 0, null, null, null, themeDescriptionDelegate, i13));
        arrayList.add(new ThemeDescription(this.progressBar, 0, null, null, null, themeDescriptionDelegate, i13));
        arrayList.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        return arrayList;
    }
}
