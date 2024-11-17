package org.telegram.ui.Business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchField;

public class ChatAttachAlertQuickRepliesLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private EmptyTextProgressView emptyView;
    private FrameLayout frameLayout;
    private boolean ignoreLayout;
    private FillLastLinearLayoutManager layoutManager;
    private ShareAdapter listAdapter;
    private RecyclerListView listView;
    private ShareSearchAdapter searchAdapter;
    private SearchField searchField;
    private HashSet selectedReplies;
    private View shadow;
    private AnimatorSet shadowAnimation;

    public class ShareAdapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount;
        private Context mContext;
        private ArrayList replies;

        public ShareAdapter(Context context) {
            ArrayList arrayList = new ArrayList();
            this.replies = arrayList;
            int i = UserConfig.selectedAccount;
            this.currentAccount = i;
            this.mContext = context;
            arrayList.addAll(QuickRepliesController.getInstance(i).getFilteredReplies());
        }

        @Override
        public int getCountForSection(int i) {
            if (i == 0 || i == getSectionCount() - 1) {
                return 1;
            }
            return this.replies.size();
        }

        @Override
        public Object getItem(int i, int i2) {
            if (i != 0 && i2 >= 0 && i2 < this.replies.size()) {
                return this.replies.get(i2);
            }
            return null;
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            return i == getSectionCount() - 1 ? 2 : 0;
        }

        @Override
        public String getLetter(int i) {
            return null;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }

        @Override
        public int getSectionCount() {
            return 3;
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            return null;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            return (i == 0 || i == getSectionCount() - 1 || i2 >= this.replies.size()) ? false : true;
        }

        @Override
        public void notifyDataSetChanged() {
            this.replies.clear();
            this.replies.addAll(QuickRepliesController.getInstance(this.currentAccount).getFilteredReplies());
            super.notifyDataSetChanged();
            ChatAttachAlertQuickRepliesLayout.this.updateEmptyView();
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                QuickRepliesActivity.QuickReplyView quickReplyView = (QuickRepliesActivity.QuickReplyView) viewHolder.itemView;
                Object item = getItem(i, i2);
                boolean z = true;
                if (i == getSectionCount() - 2 && i2 == getCountForSection(i) - 1) {
                    z = false;
                }
                if (item instanceof QuickRepliesController.QuickReply) {
                    QuickRepliesController.QuickReply quickReply = (QuickRepliesController.QuickReply) item;
                    quickReplyView.set(quickReply, null, z);
                    quickReplyView.setChecked(ChatAttachAlertQuickRepliesLayout.this.selectedReplies.contains(Integer.valueOf(quickReply.id)), false);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View quickReplyView;
            if (i == 0) {
                quickReplyView = new QuickRepliesActivity.QuickReplyView(this.mContext, false, ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).resourcesProvider);
            } else if (i != 1) {
                quickReplyView = new View(this.mContext);
            } else {
                quickReplyView = new View(this.mContext);
                quickReplyView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(quickReplyView);
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        public String lastQuery;
        private Context mContext;
        private ArrayList searchResult = new ArrayList();
        private Runnable searchRunnable;

        public ShareSearchAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i2);
        }

        @Override
        public int getItemCount() {
            return this.searchResult.size() + 2;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            return i == getItemCount() - 1 ? 2 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertQuickRepliesLayout.this.updateEmptyView();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                QuickRepliesActivity.QuickReplyView quickReplyView = (QuickRepliesActivity.QuickReplyView) viewHolder.itemView;
                boolean z = i != getItemCount() + (-2);
                Object item = getItem(i);
                if (item instanceof QuickRepliesController.QuickReply) {
                    QuickRepliesController.QuickReply quickReply = (QuickRepliesController.QuickReply) item;
                    quickReplyView.set(quickReply, this.lastQuery, z);
                    quickReplyView.setChecked(ChatAttachAlertQuickRepliesLayout.this.selectedReplies.contains(Integer.valueOf(quickReply.id)), false);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View quickReplyView;
            if (i == 0) {
                quickReplyView = new QuickRepliesActivity.QuickReplyView(this.mContext, false, ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).resourcesProvider);
            } else if (i != 1) {
                quickReplyView = new View(this.mContext);
            } else {
                quickReplyView = new View(this.mContext);
                quickReplyView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(quickReplyView);
        }

        public void search(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.lastQuery = str;
            if (str != null) {
                String translitSafe = AndroidUtilities.translitSafe(str);
                if (translitSafe.startsWith("/")) {
                    translitSafe = translitSafe.substring(1);
                }
                QuickRepliesController quickRepliesController = QuickRepliesController.getInstance(UserConfig.selectedAccount);
                for (int i = 0; i < quickRepliesController.replies.size(); i++) {
                    QuickRepliesController.QuickReply quickReply = (QuickRepliesController.QuickReply) quickRepliesController.replies.get(i);
                    if (!quickReply.isSpecial()) {
                        String translitSafe2 = AndroidUtilities.translitSafe(quickReply.name);
                        if (!translitSafe2.startsWith(translitSafe)) {
                            if (!translitSafe2.contains(" " + translitSafe)) {
                            }
                        }
                        this.searchResult.add(quickReply);
                    }
                }
            }
            if (ChatAttachAlertQuickRepliesLayout.this.listView.getAdapter() != ChatAttachAlertQuickRepliesLayout.this.searchAdapter) {
                ChatAttachAlertQuickRepliesLayout.this.listView.setAdapter(ChatAttachAlertQuickRepliesLayout.this.searchAdapter);
            }
            notifyDataSetChanged();
        }
    }

    public static class UserCell extends FrameLayout {
    }

    public ChatAttachAlertQuickRepliesLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.selectedReplies = new HashSet();
        this.searchAdapter = new ShareSearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        SearchField searchField = new SearchField(context, false, resourcesProvider) {
            @Override
            protected void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
                ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).parentAlert.makeFocusable(editTextBoldCursor, true);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override
            public void onTextChange(String str) {
                if (str.length() != 0) {
                    if (ChatAttachAlertQuickRepliesLayout.this.emptyView != null) {
                        ChatAttachAlertQuickRepliesLayout.this.emptyView.setText(LocaleController.getString(R.string.NoResult));
                    }
                } else if (ChatAttachAlertQuickRepliesLayout.this.listView.getAdapter() != ChatAttachAlertQuickRepliesLayout.this.listAdapter) {
                    int currentTop = ChatAttachAlertQuickRepliesLayout.this.getCurrentTop();
                    ChatAttachAlertQuickRepliesLayout.this.emptyView.showTextView();
                    ChatAttachAlertQuickRepliesLayout.this.listView.setAdapter(ChatAttachAlertQuickRepliesLayout.this.listAdapter);
                    ChatAttachAlertQuickRepliesLayout.this.listAdapter.notifyDataSetChanged();
                    if (currentTop > 0) {
                        ChatAttachAlertQuickRepliesLayout.this.layoutManager.scrollToPositionWithOffset(0, -currentTop);
                    }
                }
                if (ChatAttachAlertQuickRepliesLayout.this.searchAdapter != null) {
                    ChatAttachAlertQuickRepliesLayout.this.searchAdapter.search(str);
                }
            }

            @Override
            public void processTouchEvent(MotionEvent motionEvent) {
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setLocation(obtain.getRawX(), (obtain.getRawY() - ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).parentAlert.getSheetContainer().getTranslationY()) - AndroidUtilities.dp(58.0f));
                ChatAttachAlertQuickRepliesLayout.this.listView.dispatchTouchEvent(obtain);
                obtain.recycle();
            }
        };
        this.searchField = searchField;
        searchField.setHint(LocaleController.getString(R.string.BusinessRepliesSearch));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        this.listView = new RecyclerListView(context, resourcesProvider) {
            @Override
            protected boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }

            @Override
            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
            }
        };
        NotificationCenter.getInstance(UserConfig.selectedAccount).listenGlobal(this.listView, NotificationCenter.emojiLoaded, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChatAttachAlertQuickRepliesLayout.this.lambda$new$1((Object[]) obj);
            }
        });
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public int calculateDyToMakeVisible(View view, int i2) {
                        return super.calculateDyToMakeVisible(view, i2) - (ChatAttachAlertQuickRepliesLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f));
                    }

                    @Override
                    public int calculateTimeForDeceleration(int i2) {
                        return super.calculateTimeForDeceleration(i2) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(i);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setBind(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ShareAdapter shareAdapter = new ShareAdapter(context);
        this.listAdapter = shareAdapter;
        recyclerListView2.setAdapter(shareAdapter);
        this.listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                ChatAttachAlertQuickRepliesLayout.this.lambda$new$2(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ((ChatAttachAlert.AttachAlertLayout) ChatAttachAlertQuickRepliesLayout.this).parentAlert.updateLayout(ChatAttachAlertQuickRepliesLayout.this, true, i2);
                ChatAttachAlertQuickRepliesLayout.this.updateEmptyViewPosition();
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, layoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateEmptyView();
    }

    public int getCurrentTop() {
        if (this.listView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
    }

    public void lambda$getThemeDescriptions$3() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.listView.getChildAt(i);
            }
        }
    }

    public static void lambda$new$0(View view) {
        if (view instanceof QuickRepliesActivity.QuickReplyView) {
            ((QuickRepliesActivity.QuickReplyView) view).invalidateEmojis();
        }
    }

    public void lambda$new$1(Object[] objArr) {
        AndroidUtilities.forEachViews((RecyclerView) this.listView, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChatAttachAlertQuickRepliesLayout.lambda$new$0((View) obj);
            }
        });
    }

    public void lambda$new$2(View view, int i) {
        Object item;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (adapter == shareSearchAdapter) {
            item = shareSearchAdapter.getItem(i);
        } else {
            int sectionForPosition = this.listAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = this.listAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition < 0 || sectionForPosition < 0) {
                return;
            } else {
                item = this.listAdapter.getItem(sectionForPosition, positionInSectionForPosition);
            }
        }
        if (item instanceof QuickRepliesController.QuickReply) {
            Object obj = this.parentAlert.baseFragment;
            if (obj instanceof ChatActivityInterface) {
                QuickRepliesController.getInstance(UserConfig.selectedAccount).sendQuickReplyTo(((ChatActivityInterface) obj).getDialogId(), (QuickRepliesController.QuickReply) item);
                this.parentAlert.dismiss();
            }
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((!z || this.shadow.getTag() == null) && (z || this.shadow.getTag() != null)) {
            return;
        }
        this.shadow.setTag(z ? null : 1);
        if (z) {
            this.shadow.setVisibility(0);
        }
        AnimatorSet animatorSet = this.shadowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.shadowAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.shadow, (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.shadowAnimation.setDuration(150L);
        this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (ChatAttachAlertQuickRepliesLayout.this.shadowAnimation == null || !ChatAttachAlertQuickRepliesLayout.this.shadowAnimation.equals(animator)) {
                    return;
                }
                ChatAttachAlertQuickRepliesLayout.this.shadowAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ChatAttachAlertQuickRepliesLayout.this.shadowAnimation == null || !ChatAttachAlertQuickRepliesLayout.this.shadowAnimation.equals(animator)) {
                    return;
                }
                if (!z) {
                    ChatAttachAlertQuickRepliesLayout.this.shadow.setVisibility(4);
                }
                ChatAttachAlertQuickRepliesLayout.this.shadowAnimation = null;
            }
        });
        this.shadowAnimation.start();
    }

    public void updateEmptyView() {
        this.emptyView.setVisibility(this.listView.getAdapter().getItemCount() == 2 ? 0 : 8);
        updateEmptyViewPosition();
    }

    public void updateEmptyViewPosition() {
        View childAt;
        if (this.emptyView.getVisibility() == 0 && (childAt = this.listView.getChildAt(0)) != null) {
            this.emptyView.setTranslationY(((r1.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2);
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
    }

    @Override
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
            top = i;
        } else {
            runShadowAnimation(false);
        }
        this.frameLayout.setTranslationY(top);
        return top + AndroidUtilities.dp(12.0f);
    }

    @Override
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    @Override
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override
    public int getSelectedItemsCount() {
        return 0;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ChatAttachAlertQuickRepliesLayout.this.lambda$getThemeDescriptions$3();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this.shadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        arrayList.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogSearchBackground));
        int i = Theme.key_dialogSearchIcon;
        arrayList.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogSearchText));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_dialogSearchHint));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_featuredStickers_addedIcon));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i2 = Theme.key_dialogTextGray2;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateEmptyViewPosition();
    }

    @Override
    public void onPreMeasure(int i, int i2) {
        int i3;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            i3 = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    i3 = (int) (i2 / 3.5f);
                    this.parentAlert.setAllowNestedScroll(true);
                }
            }
            i3 = (i2 / 5) * 2;
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != i3) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, i3, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
    }

    @Override
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
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
        this.listView.smoothScrollToPosition(0);
    }

    @Override
    public void sendSelectedItems(boolean z, int i, long j, boolean z2) {
    }

    @Override
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }
}
