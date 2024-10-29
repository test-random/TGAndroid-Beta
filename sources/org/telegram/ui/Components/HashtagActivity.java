package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.HashtagSearchController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MessagesSearchAdapter;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatActivityContainer;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Stories.StoriesController;

public class HashtagActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ChatActivityContainer chatContainer;
    private FrameLayout contentView;
    private ValueAnimator contentViewAnimator;
    private float contentViewValue;
    private final String hashtag;
    private final String query;
    private SharedMediaLayout sharedMediaLayout;
    private FrameLayout sharedMediaLayoutContainer;
    private final StoriesController.SearchStoriesList storiesList;
    private FrameLayout storiesTotal;
    private TextView storiesTotalTextView;
    private MessagesSearchAdapter.StoriesView storiesView;
    private boolean storiesVisible;
    private ValueAnimator transitAnimator;
    private float transitValue;
    private final String username;

    public HashtagActivity(String str) {
        this(str, null);
    }

    public HashtagActivity(String str, Theme.ResourcesProvider resourcesProvider) {
        String str2;
        setResourceProvider(resourcesProvider);
        String str3 = "";
        String trim = (str == null ? "" : str).trim();
        if (!trim.startsWith("#") && !trim.startsWith("$")) {
            trim = "#" + trim;
        }
        int indexOf = trim.indexOf("@");
        if (indexOf > 0) {
            this.hashtag = trim.substring(0, indexOf);
            str2 = trim.substring(indexOf + 1);
        } else {
            this.hashtag = trim;
            str2 = null;
        }
        this.username = str2;
        StringBuilder sb = new StringBuilder();
        sb.append(this.hashtag);
        if (!TextUtils.isEmpty(this.username)) {
            str3 = "@" + this.username;
        }
        sb.append(str3);
        this.query = sb.toString();
        this.storiesList = new StoriesController.SearchStoriesList(this.currentAccount, this.username, this.hashtag);
    }

    public void lambda$createView$0(View view) {
        transit(!this.storiesVisible, true);
        this.storiesView.transition(this.storiesVisible);
    }

    public void lambda$updateStoriesVisible$1(boolean z) {
        if (z) {
            return;
        }
        this.storiesView.setVisibility(8);
    }

    private void transit(final boolean z, boolean z2) {
        ChatActivity chatActivity;
        RecyclerListView recyclerListView;
        ValueAnimator valueAnimator = this.transitAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z2) {
            if (this.storiesVisible == z) {
                return;
            }
            this.storiesVisible = z;
            this.sharedMediaLayoutContainer.setVisibility(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.transitValue, z ? 1.0f : 0.0f);
            this.transitAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    HashtagActivity.this.transitValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    HashtagActivity.this.sharedMediaLayout.setScaleX(AndroidUtilities.lerp(0.95f, 1.0f, HashtagActivity.this.transitValue));
                    HashtagActivity.this.sharedMediaLayout.setScaleY(AndroidUtilities.lerp(0.95f, 1.0f, HashtagActivity.this.transitValue));
                    if (HashtagActivity.this.chatContainer != null && HashtagActivity.this.chatContainer.chatActivity != null && HashtagActivity.this.chatContainer.chatActivity.messagesSearchListView != null) {
                        HashtagActivity.this.chatContainer.chatActivity.messagesSearchListView.setScaleX(AndroidUtilities.lerp(1.0f, 0.95f, HashtagActivity.this.transitValue));
                        HashtagActivity.this.chatContainer.chatActivity.messagesSearchListView.setScaleY(AndroidUtilities.lerp(1.0f, 0.95f, HashtagActivity.this.transitValue));
                    }
                    HashtagActivity.this.sharedMediaLayoutContainer.setAlpha(HashtagActivity.this.transitValue);
                }
            });
            this.transitAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    HashtagActivity.this.transitValue = z ? 1.0f : 0.0f;
                    HashtagActivity.this.sharedMediaLayout.setScaleX(AndroidUtilities.lerp(0.95f, 1.0f, HashtagActivity.this.transitValue));
                    HashtagActivity.this.sharedMediaLayout.setScaleY(AndroidUtilities.lerp(0.95f, 1.0f, HashtagActivity.this.transitValue));
                    if (HashtagActivity.this.chatContainer != null && HashtagActivity.this.chatContainer.chatActivity != null && HashtagActivity.this.chatContainer.chatActivity.messagesSearchListView != null) {
                        HashtagActivity.this.chatContainer.chatActivity.messagesSearchListView.setScaleX(AndroidUtilities.lerp(1.0f, 0.95f, HashtagActivity.this.transitValue));
                        HashtagActivity.this.chatContainer.chatActivity.messagesSearchListView.setScaleY(AndroidUtilities.lerp(1.0f, 0.95f, HashtagActivity.this.transitValue));
                    }
                    HashtagActivity.this.sharedMediaLayoutContainer.setAlpha(HashtagActivity.this.transitValue);
                    if (z) {
                        return;
                    }
                    HashtagActivity.this.sharedMediaLayoutContainer.setVisibility(8);
                }
            });
            this.transitAnimator.setDuration(320L);
            this.transitAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.transitAnimator.start();
            return;
        }
        this.storiesVisible = z;
        this.transitValue = z ? 1.0f : 0.0f;
        this.sharedMediaLayout.setScaleX(z ? 1.0f : 0.95f);
        this.sharedMediaLayout.setScaleY(z ? 1.0f : 0.95f);
        this.sharedMediaLayoutContainer.setAlpha(z ? 1.0f : 0.0f);
        this.sharedMediaLayoutContainer.setVisibility(z ? 0 : 8);
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null || (recyclerListView = chatActivity.messagesSearchListView) == null) {
            return;
        }
        recyclerListView.setScaleX(AndroidUtilities.lerp(1.0f, 0.95f, this.transitValue));
        this.chatContainer.chatActivity.messagesSearchListView.setScaleY(AndroidUtilities.lerp(1.0f, 0.95f, this.transitValue));
    }

    private void updateStoriesVisible(final boolean z, boolean z2) {
        this.storiesView.animate().cancel();
        ValueAnimator valueAnimator = this.contentViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (!z2) {
            this.storiesView.setVisibility(z ? 0 : 8);
            this.storiesView.setTranslationY(z ? 0.0f : -AndroidUtilities.dp(48.0f));
            this.contentView.setTranslationY(z ? AndroidUtilities.dp(48.0f) : 0.0f);
            this.contentView.setPadding(0, 0, 0, z ? AndroidUtilities.dp(48.0f) : 0);
            return;
        }
        this.storiesView.setVisibility(0);
        ViewPropertyAnimator duration = this.storiesView.animate().translationY(z ? 0.0f : -AndroidUtilities.dp(48.0f)).withEndAction(new Runnable() {
            @Override
            public final void run() {
                HashtagActivity.this.lambda$updateStoriesVisible$1(z);
            }
        }).setDuration(320L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        duration.setInterpolator(cubicBezierInterpolator).start();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.contentViewValue, z ? 1.0f : 0.0f);
        this.contentViewAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                HashtagActivity.this.contentViewValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                HashtagActivity.this.contentView.setTranslationY(HashtagActivity.this.contentViewValue * AndroidUtilities.dp(48.0f));
                HashtagActivity.this.contentView.setPadding(0, 0, 0, (int) (HashtagActivity.this.contentViewValue * AndroidUtilities.dp(48.0f)));
            }
        });
        this.contentViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                HashtagActivity.this.contentViewValue = z ? 1.0f : 0.0f;
                HashtagActivity.this.contentView.setTranslationY(HashtagActivity.this.contentViewValue * AndroidUtilities.dp(48.0f));
                HashtagActivity.this.contentView.setPadding(0, 0, 0, (int) (HashtagActivity.this.contentViewValue * AndroidUtilities.dp(48.0f)));
            }
        });
        this.contentViewAnimator.setDuration(320L);
        this.contentViewAnimator.setInterpolator(cubicBezierInterpolator);
        this.contentViewAnimator.start();
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(this.query);
        ActionBar actionBar = this.actionBar;
        int i = Theme.key_windowBackgroundWhite;
        actionBar.setBackgroundColor(getThemedColor(i));
        ActionBar actionBar2 = this.actionBar;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        actionBar2.setItemsColor(getThemedColor(i2), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setTitleColor(getThemedColor(i2));
        this.actionBar.setCastShadows(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    HashtagActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(i));
        FrameLayout frameLayout2 = new FrameLayout(context) {
            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                setPadding(0, 0, (int) f, 0);
            }
        };
        this.contentView = frameLayout2;
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1, 119));
        HashtagSearchController.getInstance(this.currentAccount).clearSearchResults(3);
        Bundle bundle = new Bundle();
        bundle.putInt("chatMode", 7);
        bundle.putInt("searchType", 3);
        bundle.putString("searchHashtag", this.query);
        ChatActivityContainer chatActivityContainer = new ChatActivityContainer(context, getParentLayout(), bundle) {
            boolean activityCreated = false;

            @Override
            public void initChatActivity() {
                if (this.activityCreated) {
                    return;
                }
                this.activityCreated = true;
                super.initChatActivity();
            }
        };
        this.chatContainer = chatActivityContainer;
        this.contentView.addView(chatActivityContainer, LayoutHelper.createFrame(-1, -1, 119));
        long j = 0;
        int i3 = 0;
        ArrayList arrayList = null;
        TLRPC.ChatFull chatFull = null;
        TLRPC.UserFull userFull = null;
        SharedMediaLayout sharedMediaLayout = new SharedMediaLayout(context, j, new SharedMediaLayout.SharedMediaPreloader(null), i3, arrayList, chatFull, userFull, 8, this, new SharedMediaLayout.Delegate() {
            @Override
            public boolean canSearchMembers() {
                return false;
            }

            @Override
            public TLRPC.Chat getCurrentChat() {
                return null;
            }

            @Override
            public RecyclerListView getListView() {
                return null;
            }

            @Override
            public boolean isFragmentOpened() {
                return true;
            }

            @Override
            public boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, boolean z2, View view) {
                return false;
            }

            @Override
            public void scrollToSharedMedia() {
            }

            @Override
            public void updateSelectedMediaTabText() {
            }
        }, 0, this.resourceProvider) {
            @Override
            public boolean addActionButtons() {
                return false;
            }

            @Override
            protected boolean canShowSearchItem() {
                return false;
            }

            @Override
            protected boolean customTabs() {
                return true;
            }

            @Override
            protected void drawBackgroundWithBlur(Canvas canvas, float f, android.graphics.Rect rect, Paint paint) {
            }

            @Override
            protected int getInitialTab() {
                return 8;
            }

            @Override
            public String getStoriesHashtag() {
                return HashtagActivity.this.hashtag;
            }

            @Override
            public String getStoriesHashtagUsername() {
                return HashtagActivity.this.username;
            }

            @Override
            protected boolean includeSavedDialogs() {
                return false;
            }

            @Override
            protected boolean includeStories() {
                return false;
            }

            @Override
            protected void invalidateBlur() {
            }

            @Override
            protected boolean isArchivedOnlyStoriesView() {
                return false;
            }

            @Override
            public boolean isSearchingStories() {
                return true;
            }

            @Override
            protected boolean isStoriesView() {
                return false;
            }

            @Override
            public void onActionModeSelectedUpdate(SparseArray sparseArray) {
            }

            @Override
            protected void onSearchStateChanged(boolean z) {
            }

            @Override
            public void onTabProgress(float f) {
            }

            @Override
            protected void onTabScroll(boolean z) {
            }

            @Override
            public void showActionMode(boolean z) {
            }
        };
        this.sharedMediaLayout = sharedMediaLayout;
        if (sharedMediaLayout.getSearchOptionsItem() != null) {
            this.sharedMediaLayout.getSearchOptionsItem().setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, this.resourceProvider), PorterDuff.Mode.MULTIPLY));
        }
        this.sharedMediaLayout.setPinnedToTop(true);
        this.sharedMediaLayout.photoVideoOptionsItem.setTranslationY(0.0f);
        if (this.sharedMediaLayout.getSearchOptionsItem() != null) {
            this.sharedMediaLayout.getSearchOptionsItem().setTranslationY(0.0f);
        }
        this.sharedMediaLayout.setBackgroundColor(getThemedColor(i));
        this.sharedMediaLayout.updateStoriesList(this.storiesList);
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.sharedMediaLayoutContainer = frameLayout3;
        frameLayout3.setBackgroundColor(getThemedColor(i));
        this.sharedMediaLayoutContainer.addView(this.sharedMediaLayout, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 0.0f, 0.0f, 49.0f));
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.storiesTotal = frameLayout4;
        frameLayout4.setBackgroundColor(getThemedColor(i));
        TextView textView = new TextView(context);
        this.storiesTotalTextView = textView;
        textView.setTypeface(AndroidUtilities.bold());
        this.storiesTotalTextView.setTextSize(1, 15.0f);
        this.storiesTotalTextView.setTextColor(getThemedColor(Theme.key_chat_searchPanelText));
        this.storiesTotalTextView.setText(LocaleController.formatPluralString("FoundStories", this.storiesList.getCount(), new Object[0]));
        this.storiesTotal.addView(this.storiesTotalTextView, LayoutHelper.createFrame(-1, -2.0f, 19, 18.0f, 0.0f, 18.0f, 0.0f));
        View view = new View(context);
        view.setBackgroundColor(Theme.getColor(Theme.key_divider, this.resourceProvider));
        this.storiesTotal.addView(view, LayoutHelper.createFrame(-1.0f, 1.0f / AndroidUtilities.density, 55));
        this.sharedMediaLayoutContainer.addView(this.storiesTotal, LayoutHelper.createFrame(-1, 49, 87));
        this.contentView.addView(this.sharedMediaLayoutContainer, LayoutHelper.createFrame(-1, -1, 119));
        MessagesSearchAdapter.StoriesView storiesView = new MessagesSearchAdapter.StoriesView(context, this.resourceProvider);
        this.storiesView = storiesView;
        storiesView.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor(i), getThemedColor(Theme.key_listSelector)));
        this.storiesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                HashtagActivity.this.lambda$createView$0(view2);
            }
        });
        updateStoriesVisible(this.storiesView.set(this.storiesList), false);
        this.storiesView.setMessages(HashtagSearchController.getInstance(this.currentAccount).getCount(3), this.hashtag, this.username);
        frameLayout.addView(this.storiesView, LayoutHelper.createFrame(-1, 48, 55));
        transit(false, false);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ChatActivityContainer chatActivityContainer;
        if (i != NotificationCenter.storiesListUpdated) {
            if (i != NotificationCenter.hashtagSearchUpdated || (chatActivityContainer = this.chatContainer) == null || chatActivityContainer.chatActivity == null || ((Integer) objArr[0]).intValue() != this.chatContainer.chatActivity.getClassGuid()) {
                return;
            }
            int intValue = ((Integer) objArr[1]).intValue();
            MessagesSearchAdapter.StoriesView storiesView = this.storiesView;
            if (storiesView != null) {
                storiesView.setMessages(intValue, this.hashtag, this.username);
                return;
            }
            return;
        }
        Object obj = objArr[0];
        StoriesController.SearchStoriesList searchStoriesList = this.storiesList;
        if (obj == searchStoriesList) {
            MessagesSearchAdapter.StoriesView storiesView2 = this.storiesView;
            if (storiesView2 != null) {
                updateStoriesVisible(storiesView2.set(searchStoriesList), true);
            }
            TextView textView = this.storiesTotalTextView;
            if (textView != null) {
                textView.setText(LocaleController.formatPluralString("FoundStories", this.storiesList.getCount(), new Object[0]));
            }
        }
    }

    @Override
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) > 0.699999988079071d;
    }

    @Override
    public boolean onFragmentCreate() {
        getMessagesController().getStoriesController().attachedSearchLists.add(this.storiesList);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesListUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hashtagSearchUpdated);
        this.storiesList.load(true, 18);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getMessagesController().getStoriesController().attachedSearchLists.remove(this.storiesList);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesListUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hashtagSearchUpdated);
        super.onFragmentDestroy();
    }
}
