package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FactCheckController$Key$$ExternalSyntheticBackport0;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SavedMessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell2;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatActivityContainer;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Gifts.ProfileGiftsContainer;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.UserListPoller;
import org.telegram.ui.Stories.ViewsForPeerStoriesRequester;
import org.telegram.ui.Stories.bots.BotPreviewsEditContainer;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TopicsFragment;

public abstract class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, DialogCell.DialogCellDelegate {
    private ActionBar actionBar;
    private AnimatorSet actionModeAnimation;
    private LinearLayout actionModeLayout;
    private ArrayList actionModeViews;
    private SpannableStringBuilder addPostButton;
    private float additionalFloatingTranslation;
    private boolean allowStoriesSingleColumn;
    private int animateToColumnsCount;
    private boolean animatingForward;
    private boolean animatingToOptions;
    private StoriesAdapter animationSupportingArchivedStoriesAdapter;
    private SharedPhotoVideoAdapter animationSupportingPhotoVideoAdapter;
    private StoriesAdapter animationSupportingStoriesAdapter;
    private StoriesAdapter archivedStoriesAdapter;
    private SharedDocumentsAdapter audioAdapter;
    private ArrayList audioCache;
    private ArrayList audioCellCache;
    private MediaSearchAdapter audioSearchAdapter;
    private boolean backAnimation;
    private BackDrawable backDrawable;
    private Paint backgroundPaint;
    private BotPreviewsEditContainer botPreviewsContainer;
    private ArrayList cache;
    private int cantDeleteMessagesCount;
    private ArrayList cellCache;
    private int changeColumnsTab;
    private boolean changeTypeAnimation;
    private ChannelRecommendationsAdapter channelRecommendationsAdapter;
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView closeButton;
    private CommonGroupsAdapter commonGroupsAdapter;
    final Delegate delegate;
    private ActionBarMenuItem deleteItem;
    private long dialog_id;
    private boolean disableScrolling;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private ActionBarMenuItem forwardItem;
    private FragmentContextView fragmentContextView;
    private HintView fwdRestrictedHint;
    private GifAdapter gifAdapter;
    public ProfileGiftsContainer giftsContainer;
    private long giftsLastHash;
    FlickerLoadingView globalGradientView;
    private ActionBarMenuItem gotoItem;
    private GroupUsersSearchAdapter groupUsersSearchAdapter;
    private int[] hasMedia;
    private Runnable hideFloatingDateRunnable;
    private boolean ignoreSearchCollapse;
    private TLRPC.ChatFull info;
    private int initialTab;
    protected boolean isActionModeShowed;
    boolean isInPinchToZoomTouchMode;
    boolean isPinnedToTop;
    Runnable jumpToRunnable;
    int lastMeasuredTopPadding;
    private int lastVisibleHeight;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private int maximumVelocity;
    boolean maybePinchToZoomTouchMode;
    boolean maybePinchToZoomTouchMode2;
    private boolean maybeStartTracking;
    private int[] mediaColumnsCount;
    private MediaPage[] mediaPages;
    private ActionBarMenuSubItem mediaZoomInItem;
    private ActionBarMenuSubItem mediaZoomOutItem;
    private long mergeDialogId;
    SparseArray messageAlphaEnter;
    AnimationNotificationsLocker notificationsLocker;
    private float optionsAlpha;
    private RLottieImageView optionsSearchImageView;
    ActionBarPopupWindow optionsWindow;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private boolean photoVideoChangeColumnsAnimation;
    private float photoVideoChangeColumnsProgress;
    public ImageView photoVideoOptionsItem;
    private ActionBarMenuItem pinItem;
    int pinchCenterOffset;
    int pinchCenterPosition;
    int pinchCenterX;
    int pinchCenterY;
    float pinchScale;
    boolean pinchScaleUp;
    float pinchStartDistance;
    private Drawable pinnedHeaderShadowDrawable;
    private int pointerId1;
    private int pointerId2;
    private BaseFragment profileActivity;
    private PhotoViewer.PhotoViewerProvider provider;
    android.graphics.Rect rect;
    private Theme.ResourcesProvider resourcesProvider;
    private SavedDialogsAdapter savedDialogsAdapter;
    private ChatActivityContainer savedMessagesContainer;
    private SavedMessagesSearchAdapter savedMessagesSearchAdapter;
    public ScrollSlidingTextTabStripInner scrollSlidingTextTabStrip;
    private boolean scrolling;
    public boolean scrollingByUser;
    private float searchAlpha;
    private ActionBarMenuItem searchItem;
    public ActionBarMenuItem searchItemIcon;
    private int searchItemState;
    public StoriesController.StoriesList searchStoriesList;
    public SearchTagsList searchTagsList;
    private boolean searchWas;
    private boolean searching;
    private ReactionsLayoutInBubble.VisibleReaction searchingReaction;
    private SparseArray[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private View shadowLine;
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate;
    private SharedMediaData[] sharedMediaData;
    private SharedMediaPreloader sharedMediaPreloader;
    private float shiftDp;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private StoriesAdapter storiesAdapter;
    private boolean storiesColumnsCountSet;
    private ItemTouchHelper storiesReorder;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    int topPadding;
    private long topicId;
    private ActionBarMenuItem unpinItem;
    private TLRPC.UserFull userInfo;
    private VelocityTracker velocityTracker;
    private final int viewType;
    private SharedDocumentsAdapter voiceAdapter;
    private static final int[] supportedFastScrollTypes = {0, 1, 2, 4};
    private static final Interpolator interpolator = new Interpolator() {
        @Override
        public final float getInterpolation(float f) {
            float lambda$static$2;
            lambda$static$2 = SharedMediaLayout.lambda$static$2(f);
            return lambda$static$2;
        }
    };

    public class AnonymousClass11 extends BotPreviewsEditContainer {
        AnonymousClass11(Context context, BaseFragment baseFragment, long j) {
            super(context, baseFragment, j);
        }

        public void lambda$unselect$0() {
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.isActionModeShowed) {
                sharedMediaLayout.showActionMode(false);
            }
        }

        @Override
        public int getStartedTrackingX() {
            return SharedMediaLayout.this.startedTrackingX;
        }

        @Override
        protected boolean isActionModeShowed() {
            return SharedMediaLayout.this.isActionModeShowed;
        }

        @Override
        protected boolean isSelected(MessageObject messageObject) {
            return SharedMediaLayout.this.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0;
        }

        @Override
        public void onSelectedTabChanged() {
            SharedMediaLayout.this.onSelectedTabChanged();
        }

        @Override
        protected boolean select(MessageObject messageObject) {
            if (messageObject == null) {
                return false;
            }
            char c = messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1;
            if (SharedMediaLayout.this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0 || SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size() >= 100) {
                return false;
            }
            SharedMediaLayout.this.selectedFiles[c].put(messageObject.getId(), messageObject);
            if (!messageObject.canDeleteMessage(false, null)) {
                SharedMediaLayout.access$5008(SharedMediaLayout.this);
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.isActionModeShowed) {
                sharedMediaLayout.selectedMessagesCountTextView.setNumber(SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size(), true);
            } else {
                AndroidUtilities.hideKeyboard(sharedMediaLayout.profileActivity.getParentActivity().getCurrentFocus());
                int i = 8;
                SharedMediaLayout.this.deleteItem.setVisibility(SharedMediaLayout.this.cantDeleteMessagesCount == 0 ? 0 : 8);
                if (SharedMediaLayout.this.gotoItem != null) {
                    SharedMediaLayout.this.gotoItem.setVisibility((SharedMediaLayout.this.getClosestTab() == 8 || SharedMediaLayout.this.getClosestTab() == 13) ? 8 : 0);
                }
                if (SharedMediaLayout.this.pinItem != null) {
                    SharedMediaLayout.this.pinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.unpinItem != null) {
                    SharedMediaLayout.this.unpinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.forwardItem != null) {
                    ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.forwardItem;
                    if (SharedMediaLayout.this.getClosestTab() != 8 && SharedMediaLayout.this.getClosestTab() != 13) {
                        i = 0;
                    }
                    actionBarMenuItem.setVisibility(i);
                }
                SharedMediaLayout.this.selectedMessagesCountTextView.setNumber(SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size(), false);
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < SharedMediaLayout.this.actionModeViews.size(); i2++) {
                    View view = (View) SharedMediaLayout.this.actionModeViews.get(i2);
                    AndroidUtilities.clearDrawableAnimation(view);
                    arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
                }
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(250L);
                animatorSet.start();
                SharedMediaLayout.this.scrolling = false;
                SharedMediaLayout.this.showActionMode(true);
            }
            updateSelection(true);
            return true;
        }

        @Override
        protected boolean unselect(MessageObject messageObject) {
            if (messageObject == null) {
                return false;
            }
            char c = messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1;
            if (SharedMediaLayout.this.selectedFiles[c].indexOfKey(messageObject.getId()) < 0) {
                return false;
            }
            SharedMediaLayout.this.selectedFiles[c].remove(messageObject.getId());
            if (!messageObject.canDeleteMessage(false, null)) {
                SharedMediaLayout.access$5010(SharedMediaLayout.this);
            }
            if (SharedMediaLayout.this.selectedFiles[0].size() == 0 && SharedMediaLayout.this.selectedFiles[1].size() == 0) {
                AndroidUtilities.hideKeyboard(SharedMediaLayout.this.profileActivity.getParentActivity().getCurrentFocus());
                SharedMediaLayout.this.selectedFiles[0].clear();
                SharedMediaLayout.this.selectedFiles[1].clear();
                int i = 8;
                SharedMediaLayout.this.deleteItem.setVisibility(SharedMediaLayout.this.cantDeleteMessagesCount == 0 ? 0 : 8);
                if (SharedMediaLayout.this.gotoItem != null) {
                    SharedMediaLayout.this.gotoItem.setVisibility((SharedMediaLayout.this.getClosestTab() == 8 || SharedMediaLayout.this.getClosestTab() == 13) ? 8 : 0);
                }
                if (SharedMediaLayout.this.pinItem != null) {
                    SharedMediaLayout.this.pinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.unpinItem != null) {
                    SharedMediaLayout.this.unpinItem.setVisibility(8);
                }
                if (SharedMediaLayout.this.forwardItem != null) {
                    ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.forwardItem;
                    if (SharedMediaLayout.this.getClosestTab() != 8 && SharedMediaLayout.this.getClosestTab() != 13) {
                        i = 0;
                    }
                    actionBarMenuItem.setVisibility(i);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < SharedMediaLayout.this.actionModeViews.size(); i2++) {
                    View view = (View) SharedMediaLayout.this.actionModeViews.get(i2);
                    AndroidUtilities.clearDrawableAnimation(view);
                    arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 1.0f, 0.1f));
                }
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(250L);
                animatorSet.start();
                SharedMediaLayout.this.scrolling = false;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.AnonymousClass11.this.lambda$unselect$0();
                    }
                }, 20L);
            } else {
                SharedMediaLayout.this.selectedMessagesCountTextView.setNumber(SharedMediaLayout.this.selectedFiles[0].size() + SharedMediaLayout.this.selectedFiles[1].size(), true);
            }
            updateSelection(true);
            return true;
        }
    }

    public class AnonymousClass4 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        AnonymousClass4() {
        }

        public void lambda$onTextChanged$0() {
            SharedMediaLayout.this.switchToCurrentSelectedMode(false);
        }

        @Override
        public void onLayout(int i, int i2, int i3, int i4) {
            SharedMediaLayout.this.searchItem.setTranslationX(((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight());
        }

        @Override
        public void onSearchCollapse() {
            SharedMediaLayout.this.searching = false;
            SharedMediaLayout.this.searchingReaction = null;
            ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.searchItemIcon;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(0);
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.photoVideoOptionsItem != null && sharedMediaLayout.getPhotoVideoOptionsAlpha(0.0f) > 0.5f) {
                SharedMediaLayout.this.photoVideoOptionsItem.setVisibility(0);
            }
            SearchTagsList searchTagsList = SharedMediaLayout.this.searchTagsList;
            if (searchTagsList != null) {
                searchTagsList.clear();
                SharedMediaLayout.this.searchTagsList.show(false);
            }
            if (SharedMediaLayout.this.savedMessagesContainer != null) {
                SharedMediaLayout.this.savedMessagesContainer.chatActivity.clearSearch();
            }
            SharedMediaLayout.this.searchWas = false;
            SharedMediaLayout.this.searchItem.setVisibility(0);
            SharedMediaLayout.this.documentsSearchAdapter.search(null, true);
            SharedMediaLayout.this.linksSearchAdapter.search(null, true);
            SharedMediaLayout.this.audioSearchAdapter.search(null, true);
            SharedMediaLayout.this.groupUsersSearchAdapter.search(null, true);
            if (SharedMediaLayout.this.savedMessagesSearchAdapter != null) {
                SharedMediaLayout.this.savedMessagesSearchAdapter.search(null, null);
            }
            SharedMediaLayout.this.onSearchStateChanged(false);
            if (SharedMediaLayout.this.optionsSearchImageView != null) {
                SharedMediaLayout.this.optionsSearchImageView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
            if (SharedMediaLayout.this.ignoreSearchCollapse) {
                SharedMediaLayout.this.ignoreSearchCollapse = false;
            } else {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
        }

        @Override
        public void onSearchExpand() {
            SharedMediaLayout.this.searching = true;
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            SearchTagsList searchTagsList = sharedMediaLayout.searchTagsList;
            if (searchTagsList != null) {
                searchTagsList.show((sharedMediaLayout.getSelectedTab() == 11 || SharedMediaLayout.this.getSelectedTab() == 12) && SharedMediaLayout.this.searchTagsList.hasFilters());
            }
            ImageView imageView = SharedMediaLayout.this.photoVideoOptionsItem;
            if (imageView != null) {
                imageView.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem = SharedMediaLayout.this.searchItemIcon;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            SharedMediaLayout.this.searchItem.setVisibility(8);
            SharedMediaLayout.this.onSearchStateChanged(true);
            if (SharedMediaLayout.this.optionsSearchImageView != null) {
                SharedMediaLayout.this.optionsSearchImageView.animate().scaleX(0.6f).scaleY(0.6f).alpha(0.0f).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
        }

        @Override
        public void onSearchPressed(EditText editText) {
            super.onSearchPressed(editText);
            if (SharedMediaLayout.this.savedMessagesContainer != null) {
                SharedMediaLayout.this.savedMessagesContainer.chatActivity.hitSearch();
            }
        }

        @Override
        public void onTextChanged(EditText editText) {
            MediaSearchAdapter mediaSearchAdapter;
            String obj = editText.getText().toString();
            if (SharedMediaLayout.this.savedMessagesContainer != null) {
                SharedMediaLayout.this.savedMessagesContainer.chatActivity.setSearchQuery(obj);
                if (TextUtils.isEmpty(obj) && SharedMediaLayout.this.searchingReaction == null) {
                    SharedMediaLayout.this.savedMessagesContainer.chatActivity.clearSearch();
                }
            }
            SharedMediaLayout.this.searchItem.setVisibility(8);
            SharedMediaLayout.this.searchWas = (obj.length() == 0 && SharedMediaLayout.this.searchingReaction == null) ? false : true;
            SharedMediaLayout.this.post(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.AnonymousClass4.this.lambda$onTextChanged$0();
                }
            });
            if (SharedMediaLayout.this.mediaPages[0].selectedType == 1) {
                if (SharedMediaLayout.this.documentsSearchAdapter == null) {
                    return;
                } else {
                    mediaSearchAdapter = SharedMediaLayout.this.documentsSearchAdapter;
                }
            } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 3) {
                if (SharedMediaLayout.this.linksSearchAdapter == null) {
                    return;
                } else {
                    mediaSearchAdapter = SharedMediaLayout.this.linksSearchAdapter;
                }
            } else {
                if (SharedMediaLayout.this.mediaPages[0].selectedType != 4) {
                    if (SharedMediaLayout.this.mediaPages[0].selectedType == 7) {
                        if (SharedMediaLayout.this.groupUsersSearchAdapter == null) {
                            return;
                        }
                        SharedMediaLayout.this.groupUsersSearchAdapter.search(obj, true);
                        return;
                    } else {
                        if (SharedMediaLayout.this.mediaPages[0].selectedType != 11 || SharedMediaLayout.this.savedMessagesSearchAdapter == null) {
                            return;
                        }
                        SharedMediaLayout.this.savedMessagesSearchAdapter.search(obj, SharedMediaLayout.this.searchingReaction);
                        return;
                    }
                }
                if (SharedMediaLayout.this.audioSearchAdapter == null) {
                    return;
                } else {
                    mediaSearchAdapter = SharedMediaLayout.this.audioSearchAdapter;
                }
            }
            mediaSearchAdapter.search(obj, true);
        }
    }

    public class AnonymousClass41 implements ViewTreeObserver.OnPreDrawListener {
        final SparseBooleanArray val$addedMesages;
        final RecyclerListView val$finalListView;
        final View val$finalProgressView;
        final int val$oldItemCount;

        AnonymousClass41(RecyclerListView recyclerListView, SparseBooleanArray sparseBooleanArray, View view, int i) {
            this.val$finalListView = recyclerListView;
            this.val$addedMesages = sparseBooleanArray;
            this.val$finalProgressView = view;
            this.val$oldItemCount = i;
        }

        public void lambda$onPreDraw$0(int i, RecyclerListView recyclerListView, ValueAnimator valueAnimator) {
            SharedMediaLayout.this.messageAlphaEnter.put(i, (Float) valueAnimator.getAnimatedValue());
            recyclerListView.invalidate();
        }

        @Override
        public boolean onPreDraw() {
            SharedMediaLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
            RecyclerView.Adapter adapter = this.val$finalListView.getAdapter();
            if (adapter != SharedMediaLayout.this.photoVideoAdapter && adapter != SharedMediaLayout.this.documentsAdapter && adapter != SharedMediaLayout.this.audioAdapter && adapter != SharedMediaLayout.this.voiceAdapter) {
                int childCount = this.val$finalListView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < childCount; i++) {
                    View childAt = this.val$finalListView.getChildAt(i);
                    if (childAt != this.val$finalProgressView && this.val$finalListView.getChildAdapterPosition(childAt) >= this.val$oldItemCount - 1) {
                        childAt.setAlpha(0.0f);
                        int min = (int) ((Math.min(this.val$finalListView.getMeasuredHeight(), Math.max(0, childAt.getTop())) / this.val$finalListView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay(min);
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                    View view = this.val$finalProgressView;
                    if (view != null && view.getParent() == null) {
                        this.val$finalListView.addView(this.val$finalProgressView);
                        final RecyclerView.LayoutManager layoutManager = this.val$finalListView.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.ignoreView(this.val$finalProgressView);
                            View view2 = this.val$finalProgressView;
                            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.ALPHA, view2.getAlpha(), 0.0f);
                            ofFloat2.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    AnonymousClass41.this.val$finalProgressView.setAlpha(1.0f);
                                    layoutManager.stopIgnoringView(AnonymousClass41.this.val$finalProgressView);
                                    AnonymousClass41 anonymousClass41 = AnonymousClass41.this;
                                    anonymousClass41.val$finalListView.removeView(anonymousClass41.val$finalProgressView);
                                }
                            });
                            ofFloat2.start();
                        }
                    }
                }
                animatorSet.start();
            } else if (this.val$addedMesages != null) {
                int childCount2 = this.val$finalListView.getChildCount();
                for (int i2 = 0; i2 < childCount2; i2++) {
                    final int messageId = SharedMediaLayout.getMessageId(this.val$finalListView.getChildAt(i2));
                    if (messageId != 0 && this.val$addedMesages.get(messageId, false)) {
                        SharedMediaLayout.this.messageAlphaEnter.put(messageId, Float.valueOf(0.0f));
                        ValueAnimator ofFloat3 = ValueAnimator.ofFloat(0.0f, 1.0f);
                        final RecyclerListView recyclerListView = this.val$finalListView;
                        ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                SharedMediaLayout.AnonymousClass41.this.lambda$onPreDraw$0(messageId, recyclerListView, valueAnimator);
                            }
                        });
                        ofFloat3.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                SharedMediaLayout.this.messageAlphaEnter.remove(messageId);
                                AnonymousClass41.this.val$finalListView.invalidate();
                            }
                        });
                        ofFloat3.setStartDelay((int) ((Math.min(this.val$finalListView.getMeasuredHeight(), Math.max(0, r7.getTop())) / this.val$finalListView.getMeasuredHeight()) * 100.0f));
                        ofFloat3.setDuration(250L);
                        ofFloat3.start();
                    }
                    this.val$finalListView.invalidate();
                }
            }
            return true;
        }
    }

    public class AnonymousClass44 implements SharedLinkCell.SharedLinkCellDelegate {
        AnonymousClass44() {
        }

        public void lambda$onLinkPress$0(String str, DialogInterface dialogInterface, int i) {
            int i2;
            if (i == 0) {
                SharedMediaLayout.this.openUrl(str);
                return;
            }
            if (i == 1) {
                if (!str.startsWith("mailto:")) {
                    i2 = str.startsWith("tel:") ? 4 : 7;
                    AndroidUtilities.addToClipboard(str);
                }
                str = str.substring(i2);
                AndroidUtilities.addToClipboard(str);
            }
        }

        @Override
        public boolean canPerformActions() {
            return !SharedMediaLayout.this.isActionModeShowed;
        }

        @Override
        public void needOpenWebView(TLRPC.WebPage webPage, MessageObject messageObject) {
            SharedMediaLayout.this.openWebView(webPage, messageObject);
        }

        @Override
        public void onLinkPress(final String str, boolean z) {
            if (!z) {
                SharedMediaLayout.this.openUrl(str);
                return;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
            builder.setTitle(str);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    SharedMediaLayout.AnonymousClass44.this.lambda$onLinkPress$0(str, dialogInterface, i);
                }
            });
            SharedMediaLayout.this.profileActivity.showDialog(builder.create());
        }
    }

    public class AnonymousClass5 implements View.OnClickListener {
        final Context val$context;
        final Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass5(Theme.ResourcesProvider resourcesProvider, Context context) {
            this.val$resourcesProvider = resourcesProvider;
            this.val$context = context;
        }

        public void lambda$onClick$0() {
            StoryRecorder.getInstance(SharedMediaLayout.this.profileActivity.getParentActivity(), SharedMediaLayout.this.profileActivity.getCurrentAccount()).openBot(SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.botPreviewsContainer.getCurrentLang(), null);
        }

        public void lambda$onClick$1() {
            SharedMediaLayout.this.botPreviewsContainer.selectAll();
        }

        public void lambda$onClick$10(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 2);
            bundle.putLong("dialog_id", -SharedMediaLayout.this.info.id);
            MediaActivity mediaActivity = new MediaActivity(bundle, null);
            mediaActivity.setChatInfo(SharedMediaLayout.this.info);
            SharedMediaLayout.this.profileActivity.presentFragment(mediaActivity);
            ActionBarPopupWindow actionBarPopupWindow = SharedMediaLayout.this.optionsWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        public void lambda$onClick$11(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, StoriesAdapter storiesAdapter, View view) {
            if (SharedMediaLayout.this.changeTypeAnimation) {
                return;
            }
            if (!actionBarMenuSubItem.getCheckView().isChecked() && actionBarMenuSubItem2.getCheckView().isChecked()) {
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                AndroidUtilities.shakeViewSpring(view, sharedMediaLayout.shiftDp = -sharedMediaLayout.shiftDp);
                return;
            }
            actionBarMenuSubItem2.getCheckView().setChecked(!actionBarMenuSubItem2.getCheckView().isChecked(), true);
            StoriesController.StoriesList storiesList = storiesAdapter.storiesList;
            if (storiesList == null) {
                return;
            }
            storiesList.updateFilters(actionBarMenuSubItem2.getCheckView().isChecked(), actionBarMenuSubItem.getCheckView().isChecked());
        }

        public void lambda$onClick$12(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, StoriesAdapter storiesAdapter, View view) {
            if (SharedMediaLayout.this.changeTypeAnimation) {
                return;
            }
            if (!actionBarMenuSubItem.getCheckView().isChecked() && actionBarMenuSubItem2.getCheckView().isChecked()) {
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                AndroidUtilities.shakeViewSpring(view, sharedMediaLayout.shiftDp = -sharedMediaLayout.shiftDp);
                return;
            }
            actionBarMenuSubItem2.getCheckView().setChecked(!actionBarMenuSubItem2.getCheckView().isChecked(), true);
            StoriesController.StoriesList storiesList = storiesAdapter.storiesList;
            if (storiesList == null) {
                return;
            }
            storiesList.updateFilters(actionBarMenuSubItem.getCheckView().isChecked(), actionBarMenuSubItem2.getCheckView().isChecked());
        }

        public void lambda$onClick$2() {
            if (SharedMediaLayout.this.botPreviewsContainer.isSelectedAll()) {
                SharedMediaLayout.this.botPreviewsContainer.unselectAll();
            } else {
                SharedMediaLayout.this.botPreviewsContainer.selectAll();
            }
        }

        public void lambda$onClick$3() {
            SharedMediaLayout.this.botPreviewsContainer.deleteLang(SharedMediaLayout.this.botPreviewsContainer.getCurrentLang());
        }

        public void lambda$onClick$4() {
            SharedMediaLayout.this.profileActivity.getMessagesController().setSavedViewAs(false);
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId());
            SharedMediaLayout.this.profileActivity.presentFragment(new ChatActivity(bundle), true);
        }

        public void lambda$onClick$5() {
            try {
                SharedMediaLayout.this.profileActivity.getMediaDataController().installShortcut(SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId(), MediaDataController.SHORTCUT_TYPE_USER_OR_CHAT);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void lambda$onClick$6(TLRPC.User user, boolean z) {
            SharedMediaLayout.this.profileActivity.lambda$onBackPressed$319();
            if (SharedMediaLayout.this.profileActivity instanceof NotificationCenter.NotificationCenterDelegate) {
                SharedMediaLayout.this.profileActivity.getNotificationCenter().removeObserver((NotificationCenter.NotificationCenterDelegate) SharedMediaLayout.this.profileActivity, NotificationCenter.closeChats);
            }
            SharedMediaLayout.this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            SharedMediaLayout.this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needDeleteDialog, Long.valueOf(SharedMediaLayout.this.dialog_id), user, null, Boolean.valueOf(z));
            SharedMediaLayout.this.profileActivity.getMessagesController().setSavedViewAs(false);
        }

        public void lambda$onClick$7(Theme.ResourcesProvider resourcesProvider) {
            final TLRPC.User currentUser = SharedMediaLayout.this.profileActivity.getUserConfig().getCurrentUser();
            AlertsCreator.createClearOrDeleteDialogAlert(SharedMediaLayout.this.profileActivity, false, null, currentUser, false, true, true, new MessagesStorage.BooleanCallback() {
                @Override
                public final void run(boolean z) {
                    SharedMediaLayout.AnonymousClass5.this.lambda$onClick$6(currentUser, z);
                }
            }, resourcesProvider);
        }

        public void lambda$onClick$8(View view) {
            SharedMediaLayout.this.zoomIn();
        }

        public void lambda$onClick$9(View view) {
            SharedMediaLayout.this.zoomOut();
        }

        @Override
        public void onClick(android.view.View r24) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.AnonymousClass5.onClick(android.view.View):void");
        }
    }

    public class ChannelRecommendationsAdapter extends RecyclerListView.SelectionAdapter {
        private final ArrayList chats = new ArrayList();
        private final Context mContext;
        private int more;

        public ChannelRecommendationsAdapter(Context context) {
            this.mContext = context;
            update(false);
        }

        public void lambda$onCreateViewHolder$0() {
            if (SharedMediaLayout.this.profileActivity != null) {
                SharedMediaLayout.this.profileActivity.presentFragment(new PremiumPreviewFragment("similar_channels"));
            }
        }

        public void lambda$openPreview$1(View view) {
            if (SharedMediaLayout.this.profileActivity == null || SharedMediaLayout.this.profileActivity.getParentLayout() == null) {
                return;
            }
            SharedMediaLayout.this.profileActivity.getParentLayout().expandPreviewFragment();
        }

        public void lambda$openPreview$2(TLRPC.Chat chat) {
            BulletinFactory.of(SharedMediaLayout.this.profileActivity).createSimpleBulletin(R.raw.contact_check, LocaleController.formatString(R.string.YouJoinedChannel, chat == null ? "" : chat.title)).show(true);
        }

        public void lambda$openPreview$3(final TLRPC.Chat chat, int i, View view) {
            SharedMediaLayout.this.profileActivity.finishPreviewFragment();
            chat.left = false;
            update(false);
            notifyItemRemoved(i);
            if (this.chats.isEmpty()) {
                SharedMediaLayout.this.updateTabs(true);
                SharedMediaLayout.this.checkCurrentTabValid();
            }
            SharedMediaLayout.this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRecommendationsLoaded, Long.valueOf(-SharedMediaLayout.this.dialog_id));
            SharedMediaLayout.this.profileActivity.getMessagesController().addUserToChat(chat.id, SharedMediaLayout.this.profileActivity.getUserConfig().getCurrentUser(), 0, null, SharedMediaLayout.this.profileActivity, new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$openPreview$2(chat);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.chats.size();
        }

        @Override
        public int getItemViewType(int i) {
            return (this.more <= 0 || i != getItemCount() + (-1)) ? 17 : 18;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ProfileSearchCell profileSearchCell;
            if (viewHolder.getItemViewType() == 17) {
                View view = viewHolder.itemView;
                if (!(view instanceof ProfileSearchCell)) {
                    return;
                } else {
                    profileSearchCell = (ProfileSearchCell) view;
                }
            } else if (viewHolder.getItemViewType() == 18) {
                View view2 = viewHolder.itemView;
                if (!(view2 instanceof MoreRecommendationsCell)) {
                    return;
                } else {
                    profileSearchCell = ((MoreRecommendationsCell) view2).channelCell;
                }
            } else {
                profileSearchCell = null;
            }
            if (profileSearchCell != null) {
                profileSearchCell.setData((TLRPC.Chat) this.chats.get(i), null, null, null, false, false);
                profileSearchCell.useSeparator = i != this.chats.size() - 1;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View profileSearchCell;
            if (i == 18) {
                profileSearchCell = new MoreRecommendationsCell(SharedMediaLayout.this.profileActivity == null ? UserConfig.selectedAccount : SharedMediaLayout.this.profileActivity.getCurrentAccount(), this.mContext, SharedMediaLayout.this.resourcesProvider, new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$onCreateViewHolder$0();
                    }
                });
            } else {
                profileSearchCell = new ProfileSearchCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
            }
            profileSearchCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(profileSearchCell);
        }

        public void openPreview(final int i) {
            if (i < 0 || i >= this.chats.size()) {
                return;
            }
            final TLRPC.Chat chat = (TLRPC.Chat) this.chats.get(i);
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", chat.id);
            ChatActivity chatActivity = new ChatActivity(bundle);
            if (SharedMediaLayout.this.profileActivity instanceof ProfileActivity) {
                ((ProfileActivity) SharedMediaLayout.this.profileActivity).prepareBlurBitmap();
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(SharedMediaLayout.this.getContext(), R.drawable.popup_fixed_alert, SharedMediaLayout.this.resourcesProvider, 2);
            actionBarPopupWindowLayout.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(SharedMediaLayout.this.getContext(), false, false);
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.OpenChannel2), R.drawable.msg_channel);
            actionBarMenuSubItem.setMinimumWidth(160);
            actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$openPreview$1(view);
                }
            });
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(SharedMediaLayout.this.getContext(), false, false);
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.ProfileJoinChannel), R.drawable.msg_addbot);
            actionBarMenuSubItem2.setMinimumWidth(160);
            actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SharedMediaLayout.ChannelRecommendationsAdapter.this.lambda$openPreview$3(chat, i, view);
                }
            });
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
            SharedMediaLayout.this.profileActivity.presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayout);
        }

        public void update(boolean z) {
            TLRPC.Chat chat;
            if (SharedMediaLayout.this.profileActivity == null || !DialogObject.isChatDialog(SharedMediaLayout.this.dialog_id) || (chat = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getChat(Long.valueOf(-SharedMediaLayout.this.dialog_id))) == null || !ChatObject.isChannelAndNotMegaGroup(chat)) {
                return;
            }
            MessagesController.ChannelRecommendations channelRecommendations = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getChannelRecommendations(chat.id);
            this.chats.clear();
            int i = 0;
            if (channelRecommendations != null) {
                for (int i2 = 0; i2 < channelRecommendations.chats.size(); i2++) {
                    TLRPC.Chat chat2 = channelRecommendations.chats.get(i2);
                    if (chat2 != null && ChatObject.isNotInChat(chat2)) {
                        this.chats.add(chat2);
                    }
                }
            }
            if (!this.chats.isEmpty() && !UserConfig.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).isPremium()) {
                i = channelRecommendations.more;
            }
            this.more = i;
            if (z) {
                notifyDataSetChanged();
            }
        }
    }

    public class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.ChatFull chatInfo;
        private Context mContext;
        private ArrayList sortedUsers;

        public ChatUsersAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null && chatFull.participants.participants.isEmpty()) {
                return 1;
            }
            TLRPC.ChatFull chatFull2 = this.chatInfo;
            if (chatFull2 != null) {
                return chatFull2.participants.participants.size();
            }
            return 0;
        }

        @Override
        public int getItemViewType(int i) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            return (chatFull == null || !chatFull.participants.participants.isEmpty()) ? 21 : 20;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r8, int r9) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.ChatUsersAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 20) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 7, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            UserCell userCell = new UserCell(this.mContext, 9, 0, true, false, SharedMediaLayout.this.resourcesProvider);
            userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(userCell);
        }
    }

    public class CommonGroupsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList chats = new ArrayList();
        private boolean endReached;
        private boolean firstLoaded;
        private boolean loading;
        private Context mContext;

        public CommonGroupsAdapter(Context context) {
            this.mContext = context;
        }

        public void getChats(long j, final int i) {
            if (this.loading) {
                return;
            }
            TLRPC.TL_messages_getCommonChats tL_messages_getCommonChats = new TLRPC.TL_messages_getCommonChats();
            TLRPC.InputUser inputUser = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id) ? SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(SharedMediaLayout.this.dialog_id))).user_id : SharedMediaLayout.this.dialog_id);
            tL_messages_getCommonChats.user_id = inputUser;
            if (inputUser instanceof TLRPC.TL_inputUserEmpty) {
                return;
            }
            tL_messages_getCommonChats.limit = i;
            tL_messages_getCommonChats.max_id = j;
            this.loading = true;
            notifyDataSetChanged();
            SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tL_messages_getCommonChats, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SharedMediaLayout.CommonGroupsAdapter.this.lambda$getChats$1(i, tLObject, tL_error);
                }
            }), SharedMediaLayout.this.profileActivity.getClassGuid());
        }

        public void lambda$getChats$0(TLRPC.TL_error tL_error, TLObject tLObject, int i) {
            int itemCount = getItemCount();
            if (tL_error == null) {
                TLRPC.messages_Chats messages_chats = (TLRPC.messages_Chats) tLObject;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(messages_chats.chats, false);
                this.endReached = messages_chats.chats.isEmpty() || messages_chats.chats.size() != i;
                this.chats.addAll(messages_chats.chats);
            } else {
                this.endReached = true;
            }
            for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                if (SharedMediaLayout.this.mediaPages[i2].selectedType == 6 && SharedMediaLayout.this.mediaPages[i2].listView != null) {
                    InternalListView internalListView = SharedMediaLayout.this.mediaPages[i2].listView;
                    if (this.firstLoaded || itemCount == 0) {
                        SharedMediaLayout.this.animateItemsEnter(internalListView, 0, null);
                    }
                }
            }
            this.loading = false;
            this.firstLoaded = true;
            notifyDataSetChanged();
        }

        public void lambda$getChats$1(final int i, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.CommonGroupsAdapter.this.lambda$getChats$0(tL_error, tLObject, i);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (this.chats.isEmpty() && !this.loading) {
                return 1;
            }
            int size = this.chats.size();
            return (this.chats.isEmpty() || this.endReached) ? size : size + 1;
        }

        @Override
        public int getItemViewType(int i) {
            if (!this.chats.isEmpty() || this.loading) {
                return i < this.chats.size() ? 14 : 16;
            }
            return 15;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != this.chats.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 14) {
                View view = viewHolder.itemView;
                if (view instanceof ProfileSearchCell) {
                    ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
                    profileSearchCell.setData((TLRPC.Chat) this.chats.get(i), null, null, null, false, false);
                    boolean z = true;
                    if (i == this.chats.size() - 1 && this.endReached) {
                        z = false;
                    }
                    profileSearchCell.useSeparator = z;
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ProfileSearchCell profileSearchCell;
            if (i == 14) {
                profileSearchCell = new ProfileSearchCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
            } else {
                if (i == 15) {
                    View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 6, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(createEmptyStubView);
                }
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.showDate(false);
                flickerLoadingView.setViewType(1);
                profileSearchCell = flickerLoadingView;
            }
            profileSearchCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(profileSearchCell);
        }
    }

    public interface Delegate {
        boolean canSearchMembers();

        TLRPC.Chat getCurrentChat();

        RecyclerListView getListView();

        boolean isFragmentOpened();

        boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, boolean z2, View view);

        void scrollToSharedMedia();

        void updateSelectedMediaTabText();
    }

    public static class EmptyStubView extends LinearLayout {
        final ImageView emptyImageView;
        final TextView emptyTextView;
        boolean ignoreRequestLayout;

        public EmptyStubView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView = new TextView(context);
            this.emptyTextView = textView;
            ImageView imageView = new ImageView(context);
            this.emptyImageView = imageView;
            setOrientation(1);
            setGravity(17);
            addView(imageView, LayoutHelper.createLinear(-2, -2));
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            textView.setGravity(17);
            textView.setTextSize(1, 17.0f);
            textView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            addView(textView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.ignoreRequestLayout = true;
            if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            } else {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.ignoreRequestLayout = false;
            super.onMeasure(i, i2);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreRequestLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return SharedMediaLayout.this.sharedMediaData[5].messages.size();
            }
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemViewType(int i) {
            return (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) ? 12 : 11;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MessageObject messageObject;
            TLRPC.Document document;
            boolean z;
            if (viewHolder.getItemViewType() != 12 || (document = (messageObject = (MessageObject) SharedMediaLayout.this.sharedMediaData[5].messages.get(i)).getDocument()) == null) {
                return;
            }
            View view = viewHolder.itemView;
            if (view instanceof ContextLinkCell) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                contextLinkCell.setGif(document, messageObject, messageObject.messageOwner.date, false);
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                if (sharedMediaLayout.isActionModeShowed) {
                    r2 = sharedMediaLayout.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0;
                    z = SharedMediaLayout.this.scrolling;
                } else {
                    z = sharedMediaLayout.scrolling;
                }
                contextLinkCell.setChecked(r2, !z);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 11) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 5, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            ContextLinkCell contextLinkCell = new ContextLinkCell(this.mContext, true, SharedMediaLayout.this.resourcesProvider);
            contextLinkCell.setCanPreviewGif(true);
            return new RecyclerListView.Holder(contextLinkCell);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ContextLinkCell) {
                ImageReceiver photoImage = ((ContextLinkCell) view).getPhotoImage();
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 5) {
                    photoImage.setAllowStartAnimation(true);
                    photoImage.startAnimation();
                } else {
                    photoImage.setAllowStartAnimation(false);
                    photoImage.stopAnimation();
                }
            }
        }
    }

    public class GroupUsersSearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.Chat currentChat;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private ArrayList searchResultNames = new ArrayList();
        private int totalCount = 0;
        int searchCount = 0;

        public GroupUsersSearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                @Override
                public boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override
                public LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override
                public LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override
                public final void onDataSetChanged(int i) {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$new$0(i);
                }

                @Override
                public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
            this.currentChat = SharedMediaLayout.this.delegate.getCurrentChat();
        }

        private boolean createMenuForParticipant(TLObject tLObject, boolean z, View view) {
            if (tLObject instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                tL_chatChannelParticipant.channelParticipant = channelParticipant;
                tL_chatChannelParticipant.user_id = MessageObject.getPeerId(channelParticipant.peer);
                tL_chatChannelParticipant.inviter_id = channelParticipant.inviter_id;
                tL_chatChannelParticipant.date = channelParticipant.date;
                tLObject = tL_chatChannelParticipant;
            }
            return SharedMediaLayout.this.delegate.onMemberClick((TLRPC.ChatParticipant) tLObject, true, z, view);
        }

        public void lambda$new$0(int i) {
            notifyDataSetChanged();
            if (i == 1) {
                int i2 = this.searchCount - 1;
                this.searchCount = i2;
                if (i2 == 0) {
                    for (int i3 = 0; i3 < SharedMediaLayout.this.mediaPages.length; i3++) {
                        if (SharedMediaLayout.this.mediaPages[i3].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[i3].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i3].listView, 0, null);
                            }
                        }
                    }
                }
            }
        }

        public boolean lambda$onCreateViewHolder$5(ManageChatUserCell manageChatUserCell, boolean z) {
            TLObject item = getItem(((Integer) manageChatUserCell.getTag()).intValue());
            if (item instanceof TLRPC.ChannelParticipant) {
                return createMenuForParticipant((TLRPC.ChannelParticipant) item, !z, manageChatUserCell);
            }
            return false;
        }

        public void lambda$processSearch$2(java.lang.String r19, java.util.ArrayList r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList):void");
        }

        public void lambda$processSearch$3(final String str) {
            final ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(this.currentChat) && SharedMediaLayout.this.info != null) {
                arrayList = new ArrayList(SharedMediaLayout.this.info.participants.participants);
            }
            this.searchCount = 2;
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$processSearch$2(str, arrayList);
                    }
                });
            } else {
                this.searchCount = 1;
            }
            this.searchAdapterHelper.queryServerSearch(str, false, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0L, false, 2, 1);
        }

        public void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2) {
            if (SharedMediaLayout.this.searching) {
                this.searchResultNames = arrayList;
                this.searchCount--;
                if (!ChatObject.isChannel(this.currentChat)) {
                    ArrayList groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList2);
                }
                if (this.searchCount == 0) {
                    for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                        if (SharedMediaLayout.this.mediaPages[i].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i].listView, 0, null);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void lambda$search$1(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$processSearch$3(str);
                }
            });
        }

        private void updateSearchResults(final ArrayList arrayList, final ArrayList arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$updateSearchResults$4(arrayList, arrayList2);
                }
            });
        }

        public TLObject getItem(int i) {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (i < 0 || i >= size) {
                return null;
            }
            return (TLObject) this.searchAdapterHelper.getGroupSearch().get(i);
        }

        @Override
        public int getItemCount() {
            return this.totalCount;
        }

        @Override
        public int getItemViewType(int i) {
            return 22;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void notifyDataSetChanged() {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            this.totalCount = size;
            if (size > 0 && SharedMediaLayout.this.searching && SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.mediaPages[0].listView.getAdapter() != this) {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MessagesController messagesController;
            long j;
            SpannableStringBuilder spannableStringBuilder;
            TLObject item = getItem(i);
            if (item instanceof TLRPC.ChannelParticipant) {
                messagesController = SharedMediaLayout.this.profileActivity.getMessagesController();
                j = MessageObject.getPeerId(((TLRPC.ChannelParticipant) item).peer);
            } else {
                if (!(item instanceof TLRPC.ChatParticipant)) {
                    return;
                }
                messagesController = SharedMediaLayout.this.profileActivity.getMessagesController();
                j = ((TLRPC.ChatParticipant) item).user_id;
            }
            TLRPC.User user = messagesController.getUser(Long.valueOf(j));
            UserObject.getPublicUsername(user);
            this.searchAdapterHelper.getGroupSearch().size();
            String lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel();
            if (lastFoundChannel != null) {
                String userName = UserObject.getUserName(user);
                spannableStringBuilder = new SpannableStringBuilder(userName);
                int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(userName, lastFoundChannel);
                if (indexOfIgnoreCase != -1) {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOfIgnoreCase, lastFoundChannel.length() + indexOfIgnoreCase, 33);
                }
            } else {
                spannableStringBuilder = null;
            }
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) view;
                manageChatUserCell.setTag(Integer.valueOf(i));
                manageChatUserCell.setData(user, spannableStringBuilder, null, false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 9, 5, true, SharedMediaLayout.this.resourcesProvider);
            manageChatUserCell.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                @Override
                public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                    boolean lambda$onCreateViewHolder$5;
                    lambda$onCreateViewHolder$5 = SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$onCreateViewHolder$5(manageChatUserCell2, z);
                    return lambda$onCreateViewHolder$5;
                }
            });
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public void search(final String str, boolean z) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0L, false, 2, 0);
            notifyDataSetChanged();
            for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                if (SharedMediaLayout.this.mediaPages[i].selectedType == 7 && !TextUtils.isEmpty(str)) {
                    SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, z);
                }
            }
            if (TextUtils.isEmpty(str)) {
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$search$1(str);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }
    }

    public static class InternalListView extends BlurredRecyclerView implements StoriesListPlaceProvider.ClippedView {
        public int hintPaddingBottom;
        public int hintPaddingTop;

        public InternalListView(Context context) {
            super(context);
        }

        @Override
        public void updateClip(int[] iArr) {
            iArr[0] = (getPaddingTop() - AndroidUtilities.dp(2.0f)) - this.hintPaddingTop;
            iArr[1] = (getMeasuredHeight() - getPaddingBottom()) - this.hintPaddingBottom;
        }
    }

    public static class MediaPage extends FrameLayout {
        private ClippingImageView animatingImageView;
        private GridLayoutManager animationSupportingLayoutManager;
        private InternalListView animationSupportingListView;
        private ButtonWithCounterView buttonView;
        private StickerEmptyView emptyView;
        public ObjectAnimator fastScrollAnimator;
        public boolean fastScrollEnabled;
        public Runnable fastScrollHideHintRunnable;
        public boolean fastScrollHinWasShown;
        public SharedMediaFastScrollTooltip fastScrollHintView;
        public boolean highlightAnimation;
        public int highlightMessageId;
        public float highlightProgress;
        private DefaultItemAnimator itemAnimator;
        public long lastCheckScrollTime;
        private ExtendedGridLayoutManager layoutManager;
        private InternalListView listView;
        private FlickerLoadingView progressView;
        private RecyclerAnimationScrollHelper scrollHelper;
        private RecyclerView.RecycledViewPool searchViewPool;
        private int selectedType;
        private RecyclerView.RecycledViewPool viewPool;

        public MediaPage(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip = this.fastScrollHintView;
            if (sharedMediaFastScrollTooltip == null || sharedMediaFastScrollTooltip.getVisibility() != 0) {
                return;
            }
            RecyclerListView.FastScroll fastScroll = this.listView.getFastScroll();
            if (fastScroll != null) {
                float scrollBarY = fastScroll.getScrollBarY() + AndroidUtilities.dp(36.0f);
                if (this.selectedType == 9) {
                    scrollBarY += AndroidUtilities.dp(64.0f);
                }
                float measuredWidth = (getMeasuredWidth() - this.fastScrollHintView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
                this.fastScrollHintView.setPivotX(r2.getMeasuredWidth());
                this.fastScrollHintView.setPivotY(0.0f);
                this.fastScrollHintView.setTranslationX(measuredWidth);
                this.fastScrollHintView.setTranslationY(scrollBarY);
            }
            if (fastScroll.getProgress() > 0.85f) {
                SharedMediaLayout.showFastScrollHint(this, null, false);
            }
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.animationSupportingListView) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }
    }

    public class MediaSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentType;
        private int lastReqId;
        private Context mContext;
        private Runnable searchRunnable;
        private int searchesInProgress;
        private ArrayList searchResult = new ArrayList();
        protected ArrayList globalSearch = new ArrayList();
        private int reqId = 0;

        public MediaSearchAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public void lambda$queryServerSearch$0(int i, ArrayList arrayList, String str) {
            if (this.reqId != 0) {
                if (i == this.lastReqId) {
                    int itemCount = getItemCount();
                    this.globalSearch = arrayList;
                    this.searchesInProgress--;
                    int itemCount2 = getItemCount();
                    if (this.searchesInProgress == 0 || itemCount2 != 0) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                        if (SharedMediaLayout.this.mediaPages[i2].selectedType == this.currentType) {
                            if (this.searchesInProgress == 0 && itemCount2 == 0) {
                                SharedMediaLayout.this.mediaPages[i2].emptyView.title.setText(LocaleController.formatString("NoResultFoundFor", R.string.NoResultFoundFor, str));
                                SharedMediaLayout.this.mediaPages[i2].emptyView.button.setVisibility(8);
                                SharedMediaLayout.this.mediaPages[i2].emptyView.showProgress(false, true);
                            } else if (itemCount == 0) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i2].listView, 0, null);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
                this.reqId = 0;
            }
        }

        public void lambda$queryServerSearch$1(int i, final int i2, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
            final ArrayList arrayList = new ArrayList();
            if (tL_error == null) {
                TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                for (int i3 = 0; i3 < messages_messages.messages.size(); i3++) {
                    TLRPC.Message message = messages_messages.messages.get(i3);
                    if (i == 0 || message.id <= i) {
                        arrayList.add(new MessageObject(SharedMediaLayout.this.profileActivity.getCurrentAccount(), message, false, true));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$queryServerSearch$0(i2, arrayList, str);
                }
            });
        }

        public void lambda$search$2(String str, ArrayList arrayList) {
            boolean z;
            String str2;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                for (int i3 = 0; i3 < i; i3++) {
                    String str3 = strArr[i3];
                    String documentName = messageObject.getDocumentName();
                    if (documentName != null && documentName.length() != 0) {
                        if (!documentName.toLowerCase().contains(str3)) {
                            if (this.currentType != 4) {
                                continue;
                            } else {
                                TLRPC.Document document = messageObject.type == 0 ? MessageObject.getMedia(messageObject.messageOwner).webpage.document : MessageObject.getMedia(messageObject.messageOwner).document;
                                int i4 = 0;
                                while (true) {
                                    if (i4 >= document.attributes.size()) {
                                        z = false;
                                        break;
                                    }
                                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i4);
                                    if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                                        String str4 = documentAttribute.performer;
                                        z = str4 != null ? str4.toLowerCase().contains(str3) : false;
                                        if (!z && (str2 = documentAttribute.title) != null) {
                                            z = str2.toLowerCase().contains(str3);
                                        }
                                    } else {
                                        i4++;
                                    }
                                }
                                if (z) {
                                }
                            }
                        }
                        arrayList2.add(messageObject);
                        break;
                    }
                }
            }
            updateSearchResults(arrayList2);
        }

        public void lambda$search$3(final String str) {
            long j;
            long j2;
            int i;
            int i2;
            int i3;
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.isEmpty() || !((i3 = this.currentType) == 1 || i3 == 4)) {
                if (this.currentType == 3) {
                    j = SharedMediaLayout.this.dialog_id;
                    j2 = SharedMediaLayout.this.topicId;
                    i = 0;
                }
                i2 = this.currentType;
                if (i2 != 1 || i2 == 4) {
                    final ArrayList arrayList = new ArrayList(SharedMediaLayout.this.sharedMediaData[this.currentType].messages);
                    this.searchesInProgress++;
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        @Override
                        public final void run() {
                            SharedMediaLayout.MediaSearchAdapter.this.lambda$search$2(str, arrayList);
                        }
                    });
                }
                return;
            }
            MessageObject messageObject = (MessageObject) SharedMediaLayout.this.sharedMediaData[this.currentType].messages.get(SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() - 1);
            i = messageObject.getId();
            j = messageObject.getDialogId();
            j2 = SharedMediaLayout.this.dialog_id == SharedMediaLayout.this.profileActivity.getUserConfig().getClientUserId() ? messageObject.getSavedDialogId() : 0L;
            queryServerSearch(str, i, j, j2);
            i2 = this.currentType;
            if (i2 != 1) {
            }
            final ArrayList arrayList2 = new ArrayList(SharedMediaLayout.this.sharedMediaData[this.currentType].messages);
            this.searchesInProgress++;
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$search$2(str, arrayList2);
                }
            });
        }

        public void lambda$updateSearchResults$4(ArrayList arrayList) {
            if (SharedMediaLayout.this.searching) {
                this.searchesInProgress--;
                int itemCount = getItemCount();
                this.searchResult = arrayList;
                int itemCount2 = getItemCount();
                if (this.searchesInProgress == 0 || itemCount2 != 0) {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        if (this.searchesInProgress == 0 && itemCount2 == 0) {
                            SharedMediaLayout.this.mediaPages[i].emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            SharedMediaLayout.this.mediaPages[i].emptyView.button.setVisibility(8);
                            SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(false, true);
                        } else if (itemCount == 0) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i].listView, 0, null);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        private void updateSearchResults(final ArrayList arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$updateSearchResults$4(arrayList);
                }
            });
        }

        public MessageObject getItem(int i) {
            ArrayList arrayList;
            if (i < this.searchResult.size()) {
                arrayList = this.searchResult;
            } else {
                arrayList = this.globalSearch;
                i -= this.searchResult.size();
            }
            return (MessageObject) arrayList.get(i);
        }

        @Override
        public int getItemCount() {
            int size = this.searchResult.size();
            int size2 = this.globalSearch.size();
            return size2 != 0 ? size + size2 : size;
        }

        @Override
        public int getItemViewType(int i) {
            return 24;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return this.searchResult.size() + this.globalSearch.size() != 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2 = this.currentType;
            if (i2 == 1) {
                View view = viewHolder.itemView;
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    MessageObject item = getItem(i);
                    sharedDocumentCell.setDocument(item, i != getItemCount() - 1);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (sharedMediaLayout.isActionModeShowed) {
                        r1 = sharedMediaLayout.selectedFiles[(item.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (item.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(item.getId()) >= 0;
                        sharedMediaLayout = SharedMediaLayout.this;
                    }
                    sharedDocumentCell.setChecked(r1, !sharedMediaLayout.scrolling);
                    return;
                }
                return;
            }
            if (i2 == 3) {
                View view2 = viewHolder.itemView;
                if (view2 instanceof SharedLinkCell) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                    MessageObject item2 = getItem(i);
                    sharedLinkCell.setLink(item2, i != getItemCount() - 1);
                    SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                    if (sharedMediaLayout2.isActionModeShowed) {
                        r1 = sharedMediaLayout2.selectedFiles[(item2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (item2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(item2.getId()) >= 0;
                        sharedMediaLayout2 = SharedMediaLayout.this;
                    }
                    sharedLinkCell.setChecked(r1, !sharedMediaLayout2.scrolling);
                    return;
                }
                return;
            }
            if (i2 == 4) {
                View view3 = viewHolder.itemView;
                if (view3 instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) view3;
                    MessageObject item3 = getItem(i);
                    sharedAudioCell.setMessageObject(item3, i != getItemCount() - 1);
                    SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                    if (sharedMediaLayout3.isActionModeShowed) {
                        r1 = sharedMediaLayout3.selectedFiles[(item3.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (item3.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(item3.getId()) >= 0;
                        sharedMediaLayout3 = SharedMediaLayout.this;
                    }
                    sharedAudioCell.setChecked(r1, !sharedMediaLayout3.scrolling);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            int i2 = this.currentType;
            int i3 = 0;
            if (i2 == 1) {
                frameLayout = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
            } else if (i2 == 4) {
                frameLayout = new SharedAudioCell(this.mContext, i3, SharedMediaLayout.this.resourcesProvider) {
                    @Override
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
                            if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject, SharedMediaLayout.this.mergeDialogId);
                            }
                            return false;
                        }
                        boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                        MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? MediaSearchAdapter.this.searchResult : null, false);
                        if (messageObject.isRoundVideo()) {
                            MediaController.getInstance().setCurrentVideoVisible(false);
                        }
                        return playMessage;
                    }
                };
            } else {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                sharedLinkCell.setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                frameLayout = sharedLinkCell;
            }
            frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(frameLayout);
        }

        public void queryServerSearch(final java.lang.String r8, final int r9, long r10, long r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.MediaSearchAdapter.queryServerSearch(java.lang.String, int, long, long):void");
        }

        public void search(final String str, boolean z) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty()) {
                this.searchResult.clear();
                this.globalSearch.clear();
                notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(str)) {
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, z);
                    }
                }
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.MediaSearchAdapter.this.lambda$search$3(str);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
                return;
            }
            if (this.searchResult.isEmpty() && this.globalSearch.isEmpty() && this.searchesInProgress == 0) {
                return;
            }
            this.searchResult.clear();
            this.globalSearch.clear();
            if (this.reqId != 0) {
                SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
                this.searchesInProgress--;
            }
        }
    }

    private static class MoreRecommendationsCell extends FrameLayout {
        private final ButtonWithCounterView button;
        public final ProfileSearchCell channelCell;
        private final int currentAccount;
        private final View gradientView;
        private final Theme.ResourcesProvider resourcesProvider;
        private final LinkSpanDrawable.LinksTextView textView;

        public MoreRecommendationsCell(int i, Context context, Theme.ResourcesProvider resourcesProvider, final Runnable runnable) {
            super(context);
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context, resourcesProvider);
            this.channelCell = profileSearchCell;
            profileSearchCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 2));
            addView(profileSearchCell, LayoutHelper.createFrame(-1, -2.0f));
            View view = new View(context);
            this.gradientView = view;
            GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
            int i2 = Theme.key_windowBackgroundWhite;
            view.setBackground(new GradientDrawable(orientation, new int[]{Theme.multAlpha(Theme.getColor(i2, resourcesProvider), 0.4f), Theme.getColor(i2, resourcesProvider)}));
            addView(view, LayoutHelper.createFrame(-1, 60.0f));
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
            this.button = buttonWithCounterView;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.MoreSimilarButton));
            spannableStringBuilder.append((CharSequence) " ");
            SpannableString spannableString = new SpannableString("l");
            spannableString.setSpan(new ColoredImageSpan(R.drawable.msg_mini_lock2), 0, 1, 33);
            spannableStringBuilder.append((CharSequence) spannableString);
            buttonWithCounterView.setText(spannableStringBuilder, false);
            addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 48, 14.0f, 38.0f, 14.0f, 0.0f));
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    SharedMediaLayout.MoreRecommendationsCell.lambda$new$0(runnable, view2);
                }
            });
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            this.textView = linksTextView;
            linksTextView.setTextSize(1, 13.0f);
            linksTextView.setTextAlignment(4);
            linksTextView.setGravity(17);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
            linksTextView.setLineSpacing(AndroidUtilities.dp(3.0f), 1.0f);
            SpannableStringBuilder premiumText = AndroidUtilities.premiumText(LocaleController.getString(R.string.MoreSimilarText), new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.MoreRecommendationsCell.lambda$new$1(runnable);
                }
            });
            SpannableString spannableString2 = new SpannableString("" + MessagesController.getInstance(i).recommendedChannelsLimitPremium);
            spannableString2.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableString2.length(), 33);
            linksTextView.setText(AndroidUtilities.replaceCharSequence("%s", premiumText, spannableString2));
            addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 96.0f, 24.0f, 12.0f));
        }

        public static void lambda$new$0(Runnable runnable, View view) {
            if (runnable != null) {
                runnable.run();
            }
        }

        public static void lambda$new$1(Runnable runnable) {
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(145.0f), 1073741824));
        }
    }

    public static class Period {
        int date;
        public String formatedDate;
        int maxId;
        public int startOffset;

        public Period(TLRPC.TL_searchResultPosition tL_searchResultPosition) {
            int i = tL_searchResultPosition.date;
            this.date = i;
            this.maxId = tL_searchResultPosition.msg_id;
            this.startOffset = tL_searchResultPosition.offset;
            this.formatedDate = LocaleController.formatYearMont(i, true);
        }
    }

    public class SavedDialogsAdapter extends RecyclerListView.SelectionAdapter {
        public RecyclerListView attachedToRecyclerView;
        private final SavedMessagesController controller;
        private final Context mContext;
        private boolean orderChanged;
        private final ArrayList oldDialogs = new ArrayList();
        private final ArrayList dialogs = new ArrayList();
        private Runnable notifyOrderUpdate = new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.SavedDialogsAdapter.this.lambda$new$0();
            }
        };
        public final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        public final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            private SavedMessagesController.SavedDialog getDialog(RecyclerView.ViewHolder viewHolder) {
                int adapterPosition;
                if (viewHolder != null && (adapterPosition = viewHolder.getAdapterPosition()) >= 0 && adapterPosition < SavedDialogsAdapter.this.dialogs.size()) {
                    return (SavedMessagesController.SavedDialog) SavedDialogsAdapter.this.dialogs.get(adapterPosition);
                }
                return null;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setPressed(false);
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!SharedMediaLayout.this.isActionModeShowed || recyclerView.getAdapter() == SharedMediaLayout.this.savedMessagesSearchAdapter) {
                    return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
                }
                SavedMessagesController.SavedDialog dialog = getDialog(viewHolder);
                return (dialog == null || !dialog.pinned) ? ItemTouchHelper.Callback.makeMovementFlags(0, 0) : ItemTouchHelper.Callback.makeMovementFlags(3, 0);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                if (SharedMediaLayout.this.isActionModeShowed && recyclerView.getAdapter() != SharedMediaLayout.this.savedMessagesSearchAdapter) {
                    SavedMessagesController.SavedDialog dialog = getDialog(viewHolder);
                    SavedMessagesController.SavedDialog dialog2 = getDialog(viewHolder2);
                    if (dialog != null && dialog2 != null && dialog.pinned && dialog2.pinned) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        int adapterPosition2 = viewHolder2.getAdapterPosition();
                        SavedDialogsAdapter.this.dialogs.remove(adapterPosition);
                        SavedDialogsAdapter.this.dialogs.add(adapterPosition2, dialog);
                        SavedDialogsAdapter.this.notifyItemMoved(adapterPosition, adapterPosition2);
                        SavedDialogsAdapter.this.orderChanged = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
                RecyclerListView recyclerListView;
                if (viewHolder != null && (recyclerListView = SavedDialogsAdapter.this.attachedToRecyclerView) != null) {
                    recyclerListView.hideSelector(false);
                }
                if (i == 0) {
                    AndroidUtilities.cancelRunOnUIThread(SavedDialogsAdapter.this.notifyOrderUpdate);
                    AndroidUtilities.runOnUIThread(SavedDialogsAdapter.this.notifyOrderUpdate, 300L);
                }
                super.onSelectedChanged(viewHolder, i);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            }
        });
        public final HashSet selectedDialogs = new HashSet();

        public SavedDialogsAdapter(Context context) {
            this.mContext = context;
            SavedMessagesController savedMessagesController = SharedMediaLayout.this.profileActivity.getMessagesController().getSavedMessagesController();
            this.controller = savedMessagesController;
            if (SharedMediaLayout.this.includeSavedDialogs()) {
                savedMessagesController.loadDialogs(false);
            }
            setHasStableIds(true);
            update(false);
        }

        public void lambda$new$0() {
            if (this.orderChanged) {
                this.orderChanged = false;
                ArrayList<Long> arrayList = new ArrayList<>();
                for (int i = 0; i < this.dialogs.size(); i++) {
                    if (((SavedMessagesController.SavedDialog) this.dialogs.get(i)).pinned) {
                        arrayList.add(Long.valueOf(((SavedMessagesController.SavedDialog) this.dialogs.get(i)).dialogId));
                    }
                }
                SharedMediaLayout.this.profileActivity.getMessagesController().getSavedMessagesController().updatePinnedOrder(arrayList);
            }
        }

        @Override
        public int getItemCount() {
            return this.dialogs.size();
        }

        @Override
        public long getItemId(int i) {
            return (i < 0 || i >= this.dialogs.size()) ? i : ((SavedMessagesController.SavedDialog) this.dialogs.get(i)).dialogId;
        }

        @Override
        public int getItemViewType(int i) {
            return 13;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            View view = viewHolder.itemView;
            if (view instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) view;
                SavedMessagesController.SavedDialog savedDialog = (SavedMessagesController.SavedDialog) this.dialogs.get(i);
                dialogCell.setDialog(savedDialog.dialogId, savedDialog.message, savedDialog.getDate(), false, false);
                dialogCell.isSavedDialogCell = true;
                dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(savedDialog.dialogId)), false);
                dialogCell.useSeparator = i + 1 < getItemCount();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            DialogCell dialogCell = new DialogCell(null, this.mContext, false, true) {
                @Override
                public boolean getIsPinned() {
                    int childAdapterPosition;
                    RecyclerListView recyclerListView = SavedDialogsAdapter.this.attachedToRecyclerView;
                    if (recyclerListView == null) {
                        return false;
                    }
                    RecyclerView.Adapter adapter = recyclerListView.getAdapter();
                    SavedDialogsAdapter savedDialogsAdapter = SavedDialogsAdapter.this;
                    if (adapter != savedDialogsAdapter || (childAdapterPosition = savedDialogsAdapter.attachedToRecyclerView.getChildAdapterPosition(this)) < 0 || childAdapterPosition >= SavedDialogsAdapter.this.dialogs.size()) {
                        return false;
                    }
                    return ((SavedMessagesController.SavedDialog) SavedDialogsAdapter.this.dialogs.get(childAdapterPosition)).pinned;
                }

                @Override
                public boolean isForumCell() {
                    return false;
                }
            };
            dialogCell.setDialogCellDelegate(SharedMediaLayout.this);
            dialogCell.isSavedDialog = true;
            dialogCell.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(dialogCell);
        }

        public void select(View view) {
            SavedMessagesController.SavedDialog savedDialog;
            if (view instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) view;
                long dialogId = dialogCell.getDialogId();
                int i = 0;
                while (true) {
                    if (i >= this.dialogs.size()) {
                        savedDialog = null;
                        break;
                    } else {
                        if (((SavedMessagesController.SavedDialog) this.dialogs.get(i)).dialogId == dialogId) {
                            savedDialog = (SavedMessagesController.SavedDialog) this.dialogs.get(i);
                            break;
                        }
                        i++;
                    }
                }
                if (savedDialog == null) {
                    return;
                }
                if (this.selectedDialogs.contains(Long.valueOf(savedDialog.dialogId))) {
                    this.selectedDialogs.remove(Long.valueOf(savedDialog.dialogId));
                    if (this.selectedDialogs.size() <= 0) {
                        SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                        if (sharedMediaLayout.isActionModeShowed) {
                            sharedMediaLayout.showActionMode(false);
                        }
                    }
                } else {
                    this.selectedDialogs.add(Long.valueOf(savedDialog.dialogId));
                    if (this.selectedDialogs.size() > 0) {
                        SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                        if (!sharedMediaLayout2.isActionModeShowed) {
                            sharedMediaLayout2.showActionMode(true);
                            if (SharedMediaLayout.this.gotoItem != null) {
                                SharedMediaLayout.this.gotoItem.setVisibility(8);
                            }
                            if (SharedMediaLayout.this.forwardItem != null) {
                                SharedMediaLayout.this.forwardItem.setVisibility(8);
                            }
                        }
                    }
                }
                SharedMediaLayout.this.selectedMessagesCountTextView.setNumber(this.selectedDialogs.size(), true);
                boolean z = this.selectedDialogs.size() > 0;
                Iterator it = this.selectedDialogs.iterator();
                while (it.hasNext()) {
                    long longValue = ((Long) it.next()).longValue();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= this.dialogs.size()) {
                            break;
                        }
                        SavedMessagesController.SavedDialog savedDialog2 = (SavedMessagesController.SavedDialog) this.dialogs.get(i2);
                        if (savedDialog2.dialogId != longValue) {
                            i2++;
                        } else if (!savedDialog2.pinned) {
                            z = false;
                        }
                    }
                    if (!z) {
                        break;
                    }
                }
                if (SharedMediaLayout.this.pinItem != null) {
                    SharedMediaLayout.this.pinItem.setVisibility(z ? 8 : 0);
                }
                if (SharedMediaLayout.this.unpinItem != null) {
                    SharedMediaLayout.this.unpinItem.setVisibility(z ? 0 : 8);
                }
                if (view instanceof DialogCell) {
                    dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(savedDialog.dialogId)), true);
                }
            }
        }

        public void unselectAll() {
            this.selectedDialogs.clear();
        }

        public void update(boolean z) {
            this.oldDialogs.clear();
            this.oldDialogs.addAll(this.dialogs);
            this.dialogs.clear();
            this.dialogs.addAll(this.controller.allDialogs);
            if (z) {
                notifyDataSetChanged();
            }
        }
    }

    public class SavedMessagesSearchAdapter extends RecyclerListView.SelectionAdapter {
        private final int currentAccount;
        private String lastQuery;
        private ReactionsLayoutInBubble.VisibleReaction lastReaction;
        int lastSearchId;
        private boolean loading;
        private final Context mContext;
        public final ArrayList dialogs = new ArrayList();
        public final ArrayList messages = new ArrayList();
        public final ArrayList loadedMessages = new ArrayList();
        public final ArrayList cachedMessages = new ArrayList();
        private boolean endReached = false;
        private int oldItemCounts = 0;
        private int count = 0;
        private int reqId = -1;
        private Runnable searchRunnable = new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.SavedMessagesSearchAdapter.this.sendRequest();
            }
        };

        public SavedMessagesSearchAdapter(Context context) {
            this.mContext = context;
            this.currentAccount = SharedMediaLayout.this.profileActivity.getCurrentAccount();
            setHasStableIds(true);
        }

        public void lambda$sendRequest$0(org.telegram.tgnet.TLObject r6, int r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SavedMessagesSearchAdapter.lambda$sendRequest$0(org.telegram.tgnet.TLObject, int):void");
        }

        public void lambda$sendRequest$1(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$0(tLObject, i);
                }
            });
        }

        public void lambda$sendRequest$2(final int i, TLRPC.TL_messages_search tL_messages_search) {
            if (i != this.lastSearchId) {
                return;
            }
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$1(i, tLObject, tL_error);
                }
            });
        }

        public void lambda$sendRequest$3(Runnable runnable, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
            MessagesController.getInstance(this.currentAccount).putUsers(arrayList2, true);
            MessagesController.getInstance(this.currentAccount).putChats(arrayList3, true);
            AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).processDocuments(arrayList4);
            for (int i = 0; i < arrayList.size(); i++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i);
                if (messageObject.hasValidGroupId() && messageObject.messageOwner.reactions != null) {
                    messageObject.isPrimaryGroupMessage = true;
                }
                messageObject.setQuery(this.lastQuery);
                this.cachedMessages.add(messageObject);
            }
            updateMessages(true);
            AndroidUtilities.runOnUIThread(runnable, 540L);
        }

        public void sendRequest() {
            if (TextUtils.isEmpty(this.lastQuery) && this.lastReaction == null) {
                this.loading = false;
                return;
            }
            final TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(UserConfig.getInstance(this.currentAccount).getClientUserId());
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            tL_messages_search.q = this.lastQuery;
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.lastReaction;
            if (visibleReaction != null) {
                tL_messages_search.flags |= 8;
                tL_messages_search.saved_reaction.add(visibleReaction.toTLReaction());
            }
            if (this.loadedMessages.size() > 0) {
                tL_messages_search.offset_id = ((MessageObject) this.loadedMessages.get(r2.size() - 1)).getId();
            }
            tL_messages_search.limit = 10;
            this.endReached = false;
            final int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$2(i, tL_messages_search);
                }
            };
            if (this.lastReaction != null) {
                MessagesStorage.getInstance(this.currentAccount).searchSavedByTag(this.lastReaction.toTLReaction(), 0L, this.lastQuery, 100, this.cachedMessages.size(), new Utilities.Callback4() {
                    @Override
                    public final void run(Object obj, Object obj2, Object obj3, Object obj4) {
                        SharedMediaLayout.SavedMessagesSearchAdapter.this.lambda$sendRequest$3(runnable, (ArrayList) obj, (ArrayList) obj2, (ArrayList) obj3, (ArrayList) obj4);
                    }
                }, false);
            } else {
                runnable.run();
            }
        }

        private void updateMessages(boolean z) {
            this.messages.clear();
            HashSet hashSet = new HashSet();
            for (int i = 0; i < this.loadedMessages.size(); i++) {
                MessageObject messageObject = (MessageObject) this.loadedMessages.get(i);
                if (messageObject != null && !hashSet.contains(Integer.valueOf(messageObject.getId()))) {
                    hashSet.add(Integer.valueOf(messageObject.getId()));
                    this.messages.add(messageObject);
                }
            }
            for (int i2 = 0; i2 < this.cachedMessages.size(); i2++) {
                MessageObject messageObject2 = (MessageObject) this.cachedMessages.get(i2);
                if (messageObject2 != null && !hashSet.contains(Integer.valueOf(messageObject2.getId()))) {
                    hashSet.add(Integer.valueOf(messageObject2.getId()));
                    this.messages.add(messageObject2);
                }
            }
            if (!z || !this.cachedMessages.isEmpty()) {
                for (int i3 = 0; i3 < SharedMediaLayout.this.mediaPages.length; i3++) {
                    if (SharedMediaLayout.this.mediaPages[i3].selectedType == 11 && this.messages.isEmpty() && this.dialogs.isEmpty()) {
                        SharedMediaLayout.this.mediaPages[i3].emptyView.title.setText((this.lastReaction == null || !TextUtils.isEmpty(this.lastQuery)) ? LocaleController.formatString(R.string.NoResultFoundFor, this.lastQuery) : AndroidUtilities.replaceCharSequence("%s", LocaleController.getString(R.string.NoResultFoundForTag), this.lastReaction.toCharSequence(SharedMediaLayout.this.mediaPages[i3].emptyView.title.getPaint().getFontMetricsInt())));
                        SharedMediaLayout.this.mediaPages[i3].emptyView.button.setVisibility(8);
                        SharedMediaLayout.this.mediaPages[i3].emptyView.showProgress(false, true);
                    }
                }
            }
            this.oldItemCounts = this.count;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return this.dialogs.size() + this.messages.size();
        }

        @Override
        public long getItemId(int i) {
            int hash;
            if (i < 0) {
                return i;
            }
            if (i < this.dialogs.size()) {
                hash = Objects.hash(1, Long.valueOf(((SavedMessagesController.SavedDialog) this.dialogs.get(i)).dialogId));
            } else {
                int size = i - this.dialogs.size();
                if (size >= this.messages.size()) {
                    return size;
                }
                hash = Objects.hash(2, Long.valueOf(((MessageObject) this.messages.get(size)).getSavedDialogId()), Integer.valueOf(((MessageObject) this.messages.get(size)).getId()));
            }
            return hash;
        }

        @Override
        public int getItemViewType(int i) {
            return 23;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public void loadMore() {
            if (this.endReached || this.loading) {
                return;
            }
            this.loading = true;
            sendRequest();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MessageObject messageObject;
            long savedDialogId;
            int i2;
            if (i < 0) {
                return;
            }
            View view = viewHolder.itemView;
            if (view instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) view;
                dialogCell.useSeparator = i + 1 < getItemCount();
                if (i < this.dialogs.size()) {
                    SavedMessagesController.SavedDialog savedDialog = (SavedMessagesController.SavedDialog) this.dialogs.get(i);
                    savedDialogId = savedDialog.dialogId;
                    messageObject = savedDialog.message;
                    i2 = savedDialog.getDate();
                } else {
                    int size = i - this.dialogs.size();
                    if (size >= this.messages.size()) {
                        return;
                    }
                    messageObject = (MessageObject) this.messages.get(size);
                    savedDialogId = messageObject.getSavedDialogId();
                    i2 = messageObject.messageOwner.date;
                }
                dialogCell.setDialog(savedDialogId, messageObject, i2, false, false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            DialogCell dialogCell = new DialogCell(null, this.mContext, false, true) {
                @Override
                public boolean isForumCell() {
                    return false;
                }
            };
            dialogCell.setDialogCellDelegate(SharedMediaLayout.this);
            dialogCell.isSavedDialog = true;
            dialogCell.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(dialogCell);
        }

        public void search(String str, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            if (TextUtils.equals(str, this.lastQuery)) {
                ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = this.lastReaction;
                if (visibleReaction2 == null && visibleReaction == null) {
                    return;
                }
                if (visibleReaction2 != null && visibleReaction2.equals(visibleReaction)) {
                    return;
                }
            }
            this.lastQuery = str;
            this.lastReaction = visibleReaction;
            if (this.reqId >= 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = -1;
            }
            this.cachedMessages.clear();
            this.loadedMessages.clear();
            this.messages.clear();
            this.count = 0;
            this.endReached = false;
            this.loading = true;
            this.dialogs.clear();
            if (this.lastReaction == null) {
                this.dialogs.addAll(MessagesController.getInstance(this.currentAccount).getSavedMessagesController().searchDialogs(str));
            }
            for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                if (SharedMediaLayout.this.mediaPages[i].selectedType == 11) {
                    SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, true);
                }
            }
            if (this.lastReaction == null) {
                notifyDataSetChanged();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, this.lastReaction != null ? 60L : 600L);
        }
    }

    public class ScrollSlidingTextTabStripInner extends ScrollSlidingTextTabStrip {
        public int backgroundColor;
        protected Paint backgroundPaint;
        private android.graphics.Rect blurBounds;

        public ScrollSlidingTextTabStripInner(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.backgroundColor = 0;
            this.blurBounds = new android.graphics.Rect();
        }

        protected void drawBackground(Canvas canvas) {
            if (!SharedConfig.chatBlurEnabled() || this.backgroundColor == 0) {
                return;
            }
            if (this.backgroundPaint == null) {
                this.backgroundPaint = new Paint();
            }
            this.backgroundPaint.setColor(this.backgroundColor);
            this.blurBounds.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            SharedMediaLayout.this.drawBackgroundWithBlur(canvas, getY(), this.blurBounds, this.backgroundPaint);
        }

        @Override
        public void setBackgroundColor(int i) {
            this.backgroundColor = i;
            invalidate();
        }
    }

    public class SharedDocumentsAdapter extends RecyclerListView.FastScrollAdapter {
        private int currentType;
        private boolean inFastScrollMode;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        @Override
        public int getItemCount() {
            SharedMediaData sharedMediaData;
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].loadingAfterFastScroll) {
                sharedMediaData = SharedMediaLayout.this.sharedMediaData[this.currentType];
            } else {
                if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) && SharedMediaLayout.this.sharedMediaData[this.currentType].startReached)) {
                    return 0;
                }
                if (SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount == 0) {
                    int startOffset = SharedMediaLayout.this.sharedMediaData[this.currentType].getStartOffset() + SharedMediaLayout.this.sharedMediaData[this.currentType].getMessages().size();
                    return startOffset != 0 ? (SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] && SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) ? startOffset : SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() != 0 ? startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() : startOffset + 1 : startOffset;
                }
                sharedMediaData = SharedMediaLayout.this.sharedMediaData[this.currentType];
            }
            return sharedMediaData.totalCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 9;
            }
            if (i < SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset || i >= SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size()) {
                return 8;
            }
            int i2 = this.currentType;
            return (i2 == 2 || i2 == 4) ? 10 : 7;
        }

        @Override
        public String getLetter(int i) {
            Object obj;
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods == null) {
                return "";
            }
            ArrayList arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods;
            if (arrayList.isEmpty()) {
                return "";
            }
            int i2 = 0;
            while (true) {
                if (i2 >= arrayList.size()) {
                    obj = arrayList.get(arrayList.size() - 1);
                    break;
                }
                if (i <= ((Period) arrayList.get(i2)).startOffset) {
                    obj = arrayList.get(i2);
                    break;
                }
                i2++;
            }
            return ((Period) obj).formatedDate;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            float totalItemsCount = f * ((getTotalItemsCount() * measuredHeight) - (recyclerListView.getMeasuredHeight() - recyclerListView.getPaddingTop()));
            iArr[0] = (int) (totalItemsCount / measuredHeight);
            iArr[1] = ((int) totalItemsCount) % measuredHeight;
        }

        @Override
        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].messages;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 7) {
                View view = viewHolder.itemView;
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    MessageObject messageObject = (MessageObject) arrayList.get(i - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                    sharedDocumentCell.setDocument(messageObject, i != arrayList.size() - 1);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (sharedMediaLayout.isActionModeShowed) {
                        r3 = sharedMediaLayout.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0;
                        sharedMediaLayout = SharedMediaLayout.this;
                    }
                    sharedDocumentCell.setChecked(r3, !sharedMediaLayout.scrolling);
                    return;
                }
                return;
            }
            if (itemViewType != 10) {
                return;
            }
            View view2 = viewHolder.itemView;
            if (view2 instanceof SharedAudioCell) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                MessageObject messageObject2 = (MessageObject) arrayList.get(i - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                sharedAudioCell.setMessageObject(messageObject2, i != arrayList.size() - 1);
                SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                if (sharedMediaLayout2.isActionModeShowed) {
                    r3 = sharedMediaLayout2.selectedFiles[(messageObject2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0;
                    sharedMediaLayout2 = SharedMediaLayout.this;
                }
                sharedAudioCell.setChecked(r3, !sharedMediaLayout2.scrolling);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            int i2 = 0;
            if (i == 7) {
                SharedDocumentCell sharedDocumentCell = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                sharedDocumentCell.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                view = sharedDocumentCell;
            } else if (i == 8) {
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                if (this.currentType == 2) {
                    flickerLoadingView.setViewType(4);
                } else {
                    flickerLoadingView.setViewType(3);
                }
                flickerLoadingView.showDate(false);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                view = flickerLoadingView;
            } else {
                if (i == 9) {
                    View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, this.currentType, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(createEmptyStubView);
                }
                if (this.currentType != 4 || SharedMediaLayout.this.audioCellCache.isEmpty()) {
                    view2 = new SharedAudioCell(this.mContext, i2, SharedMediaLayout.this.resourcesProvider) {
                        @Override
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return playMessage;
                            }
                            if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                            }
                            return false;
                        }
                    };
                } else {
                    View view3 = (View) SharedMediaLayout.this.audioCellCache.get(0);
                    SharedMediaLayout.this.audioCellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) view3.getParent();
                    view2 = view3;
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(view3);
                        view2 = view3;
                    }
                }
                SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                sharedAudioCell.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                view = view2;
                if (this.currentType == 4) {
                    SharedMediaLayout.this.audioCache.add(sharedAudioCell);
                    view = view2;
                }
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (recyclerListView != null) {
                    int i = 0;
                    for (int i2 = 0; i2 < recyclerListView.getChildCount() && (i = SharedMediaLayout.getMessageId(recyclerListView.getChildAt(i2))) == 0; i2++) {
                    }
                    if (i == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(this.currentType, recyclerListView, true);
                    }
                }
            }
        }

        @Override
        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(this.currentType);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, null, false);
            }
        }
    }

    public class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCountForSection(int i) {
            if ((SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) && i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return ((ArrayList) SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i))).size() + (i == 0 ? 0 : 1);
            }
            return 1;
        }

        @Override
        public Object getItem(int i, int i2) {
            return null;
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 5;
            }
            if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return (i == 0 || i2 != 0) ? 4 : 3;
            }
            return 6;
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
            int i = 1;
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 1;
            }
            int size = SharedMediaLayout.this.sharedMediaData[3].sections.size();
            if (SharedMediaLayout.this.sharedMediaData[3].sections.isEmpty() || (SharedMediaLayout.this.sharedMediaData[3].endReached[0] && SharedMediaLayout.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_graySection) & (-218103809));
            }
            if (i == 0) {
                view.setAlpha(0.0f);
            } else if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate(((MessageObject) ((ArrayList) SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get((String) SharedMediaLayout.this.sharedMediaData[3].sections.get(i))).get(0)).messageOwner.date));
            }
            return view;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) {
                return i == 0 || i2 != 0;
            }
            return false;
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 6 || viewHolder.getItemViewType() == 5) {
                return;
            }
            ArrayList arrayList = (ArrayList) SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get((String) SharedMediaLayout.this.sharedMediaData[3].sections.get(i));
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 3) {
                MessageObject messageObject = (MessageObject) arrayList.get(0);
                View view = viewHolder.itemView;
                if (view instanceof GraySectionCell) {
                    ((GraySectionCell) view).setText(LocaleController.formatSectionDate(messageObject.messageOwner.date));
                    return;
                }
                return;
            }
            if (itemViewType != 4) {
                return;
            }
            if (i != 0) {
                i2--;
            }
            if (!(viewHolder.itemView instanceof SharedLinkCell) || i2 < 0 || i2 >= arrayList.size()) {
                return;
            }
            SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
            MessageObject messageObject2 = (MessageObject) arrayList.get(i2);
            sharedLinkCell.setLink(messageObject2, i2 != arrayList.size() - 1 || (i == SharedMediaLayout.this.sharedMediaData[3].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[3].loading));
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            if (sharedMediaLayout.isActionModeShowed) {
                r3 = sharedMediaLayout.selectedFiles[(messageObject2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0;
                sharedMediaLayout = SharedMediaLayout.this;
            }
            sharedLinkCell.setChecked(r3, !sharedMediaLayout.scrolling);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GraySectionCell graySectionCell;
            if (i == 3) {
                graySectionCell = new GraySectionCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
            } else if (i == 4) {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                sharedLinkCell.setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                graySectionCell = sharedLinkCell;
            } else {
                if (i == 5) {
                    View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 3, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(createEmptyStubView);
                }
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.showDate(false);
                flickerLoadingView.setViewType(5);
                graySectionCell = flickerLoadingView;
            }
            graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(graySectionCell);
        }
    }

    public static class SharedMediaData {
        private int endLoadingStubs;
        public boolean fastScrollDataLoaded;
        public int frozenEndLoadingStubs;
        public int frozenStartOffset;
        private boolean hasPhotos;
        private boolean hasVideos;
        public boolean isFrozen;
        public boolean loading;
        public boolean loadingAfterFastScroll;
        public int min_id;
        public int requestIndex;
        private int startOffset;
        public int totalCount;
        public ArrayList messages = new ArrayList();
        public SparseArray[] messagesDict = {new SparseArray(), new SparseArray()};
        public ArrayList sections = new ArrayList();
        public HashMap sectionArrays = new HashMap();
        public ArrayList fastScrollPeriods = new ArrayList();
        public boolean[] endReached = {false, true};
        public int[] max_id = {0, 0};
        public boolean startReached = true;
        public int filterType = 0;
        public ArrayList frozenMessages = new ArrayList();
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();

        static int access$10310(SharedMediaData sharedMediaData) {
            int i = sharedMediaData.endLoadingStubs;
            sharedMediaData.endLoadingStubs = i - 1;
            return i;
        }

        static int access$710(SharedMediaData sharedMediaData) {
            int i = sharedMediaData.startOffset;
            sharedMediaData.startOffset = i - 1;
            return i;
        }

        public boolean addMessage(MessageObject messageObject, int i, boolean z, boolean z2) {
            int min;
            if (this.messagesDict[i].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList arrayList = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.sectionArrays.put(messageObject.monthKey, arrayList);
                ArrayList arrayList2 = this.sections;
                String str = messageObject.monthKey;
                if (z) {
                    arrayList2.add(0, str);
                } else {
                    arrayList2.add(str);
                }
            }
            if (z) {
                arrayList.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                arrayList.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[i].put(messageObject.getId(), messageObject);
            if (!z2) {
                if (messageObject.getId() > 0) {
                    this.max_id[i] = Math.min(messageObject.getId(), this.max_id[i]);
                    min = Math.max(messageObject.getId(), this.min_id);
                }
                if (!this.hasVideos && messageObject.isVideo()) {
                    this.hasVideos = true;
                }
                if (!this.hasPhotos && messageObject.isPhoto()) {
                    this.hasPhotos = true;
                }
                return true;
            }
            this.max_id[i] = Math.max(messageObject.getId(), this.max_id[i]);
            min = Math.min(messageObject.getId(), this.min_id);
            this.min_id = min;
            if (!this.hasVideos) {
                this.hasVideos = true;
            }
            if (!this.hasPhotos) {
                this.hasPhotos = true;
            }
            return true;
        }

        public MessageObject deleteMessage(int i, int i2) {
            ArrayList arrayList;
            MessageObject messageObject = (MessageObject) this.messagesDict[i2].get(i);
            if (messageObject == null || (arrayList = (ArrayList) this.sectionArrays.get(messageObject.monthKey)) == null) {
                return null;
            }
            arrayList.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[i2].remove(messageObject.getId());
            if (arrayList.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            int i3 = this.totalCount - 1;
            this.totalCount = i3;
            if (i3 < 0) {
                this.totalCount = 0;
            }
            return messageObject;
        }

        public int getEndLoadingStubs() {
            return this.isFrozen ? this.frozenEndLoadingStubs : this.endLoadingStubs;
        }

        public ArrayList getMessages() {
            return this.isFrozen ? this.frozenMessages : this.messages;
        }

        public int getStartOffset() {
            return this.isFrozen ? this.frozenStartOffset : this.startOffset;
        }

        public void replaceMid(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.messagesDict[0].get(i);
            if (messageObject != null) {
                this.messagesDict[0].remove(i);
                this.messagesDict[0].put(i2, messageObject);
                messageObject.messageOwner.id = i2;
                int[] iArr = this.max_id;
                iArr[0] = Math.min(i2, iArr[0]);
            }
        }

        public void setEndReached(int i, boolean z) {
            this.endReached[i] = z;
        }

        public void setListFrozen(boolean z) {
            if (this.isFrozen == z) {
                return;
            }
            this.isFrozen = z;
            if (z) {
                this.frozenStartOffset = this.startOffset;
                this.frozenEndLoadingStubs = this.endLoadingStubs;
                this.frozenMessages.clear();
                this.frozenMessages.addAll(this.messages);
            }
        }

        public void setMaxId(int i, int i2) {
            this.max_id[i] = i2;
        }

        public void setTotalCount(int i) {
            this.totalCount = i;
        }
    }

    public static class SharedMediaListView extends InternalListView {
        private final ArrayList animationSupportingSortedCells;
        private int animationSupportingSortedCellsOffset;
        protected StaticLayout archivedHintLayout;
        protected float archivedHintLayoutLeft;
        protected float archivedHintLayoutWidth;
        protected TextPaint archivedHintPaint;
        final ArrayList drawingViews;
        final ArrayList drawingViews2;
        final ArrayList drawingViews3;
        final HashSet excludeDrawViews;
        UserListPoller poller;

        public SharedMediaListView(Context context) {
            super(context);
            this.excludeDrawViews = new HashSet();
            this.drawingViews = new ArrayList();
            this.drawingViews2 = new ArrayList();
            this.drawingViews3 = new ArrayList();
            this.animationSupportingSortedCells = new ArrayList();
        }

        public void checkHighlightCell(SharedPhotoVideoCell2 sharedPhotoVideoCell2) {
        }

        @Override
        public void dispatchDraw(android.graphics.Canvas r32) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedMediaListView.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            RecyclerListView.FastScrollAdapter movingAdapter = getMovingAdapter();
            if (isThisListView() && getAdapter() == movingAdapter && isChangeColumnsAnimation() && (view instanceof SharedPhotoVideoCell2)) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        public int getAnimateToColumnsCount() {
            return 3;
        }

        public float getChangeColumnsProgress() {
            return 0.0f;
        }

        public int getColumnsCount() {
            return 3;
        }

        public SparseArray<Float> getMessageAlphaEnter() {
            return null;
        }

        public RecyclerListView.FastScrollAdapter getMovingAdapter() {
            return null;
        }

        public int getPinchCenterPosition() {
            return 0;
        }

        public RecyclerListView.FastScrollAdapter getSupportingAdapter() {
            return null;
        }

        public InternalListView getSupportingListView() {
            return null;
        }

        public abstract boolean isChangeColumnsAnimation();

        public abstract boolean isStories();

        public boolean isThisListView() {
            return true;
        }
    }

    public static class SharedMediaPreloader implements NotificationCenter.NotificationCenterDelegate {
        private boolean checkedHasSavedMessages;
        private long dialogId;
        public boolean hasPreviews;
        public boolean hasSavedMessages;
        private boolean mediaWasLoaded;
        private long mergeDialogId;
        private BaseFragment parentFragment;
        private SharedMediaData[] sharedMediaData;
        private long topicId;
        private int[] mediaCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private int[] mediaMergeCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastLoadMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private ArrayList delegates = new ArrayList();

        public SharedMediaPreloader(BaseFragment baseFragment) {
            long clientUserId;
            SavedMessagesController savedMessagesController;
            long j;
            Utilities.Callback<Boolean> callback;
            this.parentFragment = baseFragment;
            if (baseFragment instanceof ChatActivityInterface) {
                ChatActivityInterface chatActivityInterface = (ChatActivityInterface) baseFragment;
                this.dialogId = chatActivityInterface.getDialogId();
                this.mergeDialogId = chatActivityInterface.getMergeDialogId();
                this.topicId = chatActivityInterface.getTopicId();
                if (this.dialogId != baseFragment.getUserConfig().getClientUserId()) {
                    savedMessagesController = baseFragment.getMessagesController().getSavedMessagesController();
                    j = this.dialogId;
                    callback = new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            SharedMediaLayout.SharedMediaPreloader.this.lambda$new$0((Boolean) obj);
                        }
                    };
                    savedMessagesController.hasSavedMessages(j, callback);
                }
            } else if (baseFragment instanceof ProfileActivity) {
                ProfileActivity profileActivity = (ProfileActivity) baseFragment;
                if (profileActivity.saved) {
                    this.dialogId = profileActivity.getUserConfig().getClientUserId();
                    this.topicId = profileActivity.getDialogId();
                } else {
                    this.dialogId = profileActivity.getDialogId();
                    this.topicId = profileActivity.getTopicId();
                    if (this.dialogId != baseFragment.getUserConfig().getClientUserId()) {
                        savedMessagesController = baseFragment.getMessagesController().getSavedMessagesController();
                        j = this.dialogId;
                        callback = new Utilities.Callback() {
                            @Override
                            public final void run(Object obj) {
                                SharedMediaLayout.SharedMediaPreloader.this.lambda$new$1((Boolean) obj);
                            }
                        };
                        savedMessagesController.hasSavedMessages(j, callback);
                    }
                }
            } else {
                if (baseFragment instanceof MediaActivity) {
                    clientUserId = ((MediaActivity) baseFragment).getDialogId();
                } else {
                    clientUserId = baseFragment instanceof DialogsActivity ? baseFragment.getUserConfig().getClientUserId() : clientUserId;
                }
                this.dialogId = clientUserId;
            }
            this.sharedMediaData = new SharedMediaData[6];
            int i = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (i >= sharedMediaDataArr.length) {
                    break;
                }
                sharedMediaDataArr[i] = new SharedMediaData();
                this.sharedMediaData[i].setMaxId(0, DialogObject.isEncryptedDialog(this.dialogId) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                i++;
            }
            loadMediaCounts();
            BaseFragment baseFragment2 = this.parentFragment;
            if (baseFragment2 == null) {
                return;
            }
            NotificationCenter notificationCenter = baseFragment2.getNotificationCenter();
            notificationCenter.addObserver(this, NotificationCenter.mediaCountsDidLoad);
            notificationCenter.addObserver(this, NotificationCenter.mediaCountDidLoad);
            notificationCenter.addObserver(this, NotificationCenter.didReceiveNewMessages);
            notificationCenter.addObserver(this, NotificationCenter.messageReceivedByServer);
            notificationCenter.addObserver(this, NotificationCenter.mediaDidLoad);
            notificationCenter.addObserver(this, NotificationCenter.messagesDeleted);
            notificationCenter.addObserver(this, NotificationCenter.replaceMessagesObjects);
            notificationCenter.addObserver(this, NotificationCenter.chatInfoDidLoad);
            notificationCenter.addObserver(this, NotificationCenter.fileLoaded);
            notificationCenter.addObserver(this, NotificationCenter.storiesListUpdated);
            notificationCenter.addObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
        }

        public void lambda$new$0(Boolean bool) {
            boolean booleanValue = bool.booleanValue();
            this.hasSavedMessages = booleanValue;
            this.checkedHasSavedMessages = true;
            if (booleanValue) {
                int size = this.delegates.size();
                for (int i = 0; i < size; i++) {
                    ((SharedMediaPreloaderDelegate) this.delegates.get(i)).mediaCountUpdated();
                }
            }
        }

        public void lambda$new$1(Boolean bool) {
            boolean booleanValue = bool.booleanValue();
            this.hasSavedMessages = booleanValue;
            this.checkedHasSavedMessages = true;
            if (booleanValue) {
                int size = this.delegates.size();
                for (int i = 0; i < size; i++) {
                    ((SharedMediaPreloaderDelegate) this.delegates.get(i)).mediaCountUpdated();
                }
            }
        }

        private void loadMediaCounts() {
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment == null) {
                return;
            }
            baseFragment.getMediaDataController().getMediaCounts(this.dialogId, this.topicId, this.parentFragment.getClassGuid());
            if (this.mergeDialogId != 0) {
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.topicId, this.parentFragment.getClassGuid());
            }
        }

        private void setChatInfo(TLRPC.ChatFull chatFull) {
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment == null || chatFull == null) {
                return;
            }
            long j = chatFull.migrated_from_chat_id;
            if (j == 0 || this.mergeDialogId != 0) {
                return;
            }
            this.mergeDialogId = -j;
            baseFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.topicId, this.parentFragment.getClassGuid());
        }

        public void addDelegate(SharedMediaPreloaderDelegate sharedMediaPreloaderDelegate) {
            this.delegates.add(sharedMediaPreloaderDelegate);
        }

        @Override
        public void didReceivedNotification(int r28, final int r29, java.lang.Object... r30) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloader.didReceivedNotification(int, int, java.lang.Object[]):void");
        }

        public int[] getLastMediaCount() {
            return this.lastMediaCount;
        }

        public SharedMediaData[] getSharedMediaData() {
            return this.sharedMediaData;
        }

        public boolean hasSharedMedia() {
            int[] lastMediaCount = getLastMediaCount();
            if (lastMediaCount == null) {
                return false;
            }
            for (int i : lastMediaCount) {
                if (i > 0) {
                    return true;
                }
            }
            if (this.hasSavedMessages) {
                return true;
            }
            BaseFragment baseFragment = this.parentFragment;
            return baseFragment != null && this.dialogId == baseFragment.getUserConfig().getClientUserId() && this.topicId == 0 && this.parentFragment.getMessagesController().getSavedMessagesController().hasDialogs();
        }

        public boolean isMediaWasLoaded() {
            return this.mediaWasLoaded;
        }

        public void onDestroy(BaseFragment baseFragment) {
            if (baseFragment != this.parentFragment) {
                return;
            }
            this.delegates.clear();
            BaseFragment baseFragment2 = this.parentFragment;
            if (baseFragment2 == null) {
                return;
            }
            NotificationCenter notificationCenter = baseFragment2.getNotificationCenter();
            notificationCenter.removeObserver(this, NotificationCenter.mediaCountsDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.mediaCountDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.didReceiveNewMessages);
            notificationCenter.removeObserver(this, NotificationCenter.messageReceivedByServer);
            notificationCenter.removeObserver(this, NotificationCenter.mediaDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.messagesDeleted);
            notificationCenter.removeObserver(this, NotificationCenter.replaceMessagesObjects);
            notificationCenter.removeObserver(this, NotificationCenter.chatInfoDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.fileLoaded);
            notificationCenter.removeObserver(this, NotificationCenter.storiesListUpdated);
            notificationCenter.removeObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
        }

        public void removeDelegate(SharedMediaPreloaderDelegate sharedMediaPreloaderDelegate) {
            this.delegates.remove(sharedMediaPreloaderDelegate);
        }
    }

    public interface SharedMediaPreloaderDelegate {
        void mediaCountUpdated();
    }

    public class SharedPhotoVideoAdapter extends RecyclerListView.FastScrollAdapter {
        protected boolean inFastScrollMode;
        protected Context mContext;
        SharedPhotoVideoCell2.SharedResources sharedResources;

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            if (SharedMediaLayout.this.isSearchingStories()) {
                return false;
            }
            return recyclerListView.getChildCount() != 0 && ((int) Math.ceil((double) (((float) getTotalItemsCount()) / ((float) ((this == SharedMediaLayout.this.photoVideoAdapter || this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[0] : SharedMediaLayout.this.animateToColumnsCount))))) * recyclerListView.getChildAt(0).getMeasuredHeight() > recyclerListView.getMeasuredHeight();
        }

        @Override
        public int getItemCount() {
            SharedMediaData sharedMediaData;
            if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1])) {
                    return 0;
                }
                int startOffset = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                return startOffset != 0 ? (SharedMediaLayout.this.sharedMediaData[0].endReached[0] && SharedMediaLayout.this.sharedMediaData[0].endReached[1]) ? startOffset : startOffset + 1 : startOffset;
            }
            if (SharedMediaLayout.this.sharedMediaData[0].loadingAfterFastScroll) {
                sharedMediaData = SharedMediaLayout.this.sharedMediaData[0];
            } else {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) && SharedMediaLayout.this.sharedMediaData[0].startReached)) {
                    return 0;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].totalCount == 0) {
                    int startOffset2 = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                    return startOffset2 != 0 ? (SharedMediaLayout.this.sharedMediaData[0].endReached[0] && SharedMediaLayout.this.sharedMediaData[0].endReached[1]) ? startOffset2 : SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() != 0 ? startOffset2 + SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() : startOffset2 + 1 : startOffset2;
                }
                sharedMediaData = SharedMediaLayout.this.sharedMediaData[0];
            }
            return sharedMediaData.totalCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (!this.inFastScrollMode && SharedMediaLayout.this.sharedMediaData[0].getMessages().size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading && SharedMediaLayout.this.sharedMediaData[0].startReached) {
                return 2;
            }
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            return 0;
        }

        @Override
        public String getLetter(int i) {
            Object obj;
            int i2 = 0;
            if (SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods == null) {
                return "";
            }
            ArrayList arrayList = SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods;
            if (arrayList.isEmpty()) {
                return "";
            }
            while (true) {
                if (i2 >= arrayList.size()) {
                    obj = arrayList.get(arrayList.size() - 1);
                    break;
                }
                if (i <= ((Period) arrayList.get(i2)).startOffset) {
                    obj = arrayList.get(i2);
                    break;
                }
                i2++;
            }
            return ((Period) obj).formatedDate;
        }

        public int getPositionForIndex(int i) {
            return SharedMediaLayout.this.sharedMediaData[0].startOffset + i;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            int i = (this == SharedMediaLayout.this.animationSupportingPhotoVideoAdapter || this == SharedMediaLayout.this.animationSupportingStoriesAdapter || this == SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter) ? SharedMediaLayout.this.animateToColumnsCount : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.mediaColumnsCount[0];
            double ceil = Math.ceil(getTotalItemsCount() / i);
            double d = measuredHeight;
            Double.isNaN(d);
            int i2 = (int) (ceil * d);
            int measuredHeight2 = recyclerListView.getMeasuredHeight() - recyclerListView.getPaddingTop();
            if (measuredHeight == 0) {
                iArr[1] = 0;
                iArr[0] = 0;
            } else {
                float f2 = f * (i2 - measuredHeight2);
                iArr[0] = ((int) (f2 / measuredHeight)) * i;
                iArr[1] = ((int) f2) % measuredHeight;
            }
        }

        @Override
        public float getScrollProgress(RecyclerListView recyclerListView) {
            int i = (this == SharedMediaLayout.this.animationSupportingPhotoVideoAdapter || this == SharedMediaLayout.this.animationSupportingStoriesAdapter || this == SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter) ? SharedMediaLayout.this.animateToColumnsCount : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.mediaColumnsCount[0];
            int ceil = (int) Math.ceil(getTotalItemsCount() / i);
            if (recyclerListView.getChildCount() == 0) {
                return 0.0f;
            }
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(0)) < 0) {
                return 0.0f;
            }
            return (((r5 / i) * measuredHeight) - (r1.getTop() - recyclerListView.getPaddingTop())) / ((ceil * measuredHeight) - (recyclerListView.getMeasuredHeight() - recyclerListView.getPaddingTop()));
        }

        @Override
        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[0].totalCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                boolean z = false;
                ArrayList messages = SharedMediaLayout.this.sharedMediaData[0].getMessages();
                int startOffset = i - SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
                View view = viewHolder.itemView;
                if (view instanceof SharedPhotoVideoCell2) {
                    SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                    int messageId = sharedPhotoVideoCell2.getMessageId();
                    int i2 = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount[0] : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.animateToColumnsCount;
                    if (startOffset < 0 || startOffset >= messages.size()) {
                        sharedPhotoVideoCell2.setMessageObject(null, i2);
                        sharedPhotoVideoCell2.setChecked(false, false);
                        return;
                    }
                    MessageObject messageObject = (MessageObject) messages.get(startOffset);
                    boolean z2 = messageObject.getId() == messageId;
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (sharedMediaLayout.isActionModeShowed) {
                        if (sharedMediaLayout.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                    }
                    sharedPhotoVideoCell2.setChecked(z, z2);
                    sharedPhotoVideoCell2.setMessageObject(messageObject, i2);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0 && i != 19) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            if (this.sharedResources == null) {
                this.sharedResources = new SharedPhotoVideoCell2.SharedResources(viewGroup.getContext(), SharedMediaLayout.this.resourcesProvider);
            }
            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = new SharedPhotoVideoCell2(this.mContext, this.sharedResources, SharedMediaLayout.this.profileActivity.getCurrentAccount());
            if (i == 19) {
                sharedPhotoVideoCell2.setCheck2();
            }
            sharedPhotoVideoCell2.setGradientView(SharedMediaLayout.this.globalGradientView);
            if (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) {
                sharedPhotoVideoCell2.isStory = true;
            }
            sharedPhotoVideoCell2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(sharedPhotoVideoCell2);
        }

        @Override
        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar(0, true);
        }

        @Override
        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (recyclerListView != null) {
                    int i = 0;
                    for (int i2 = 0; i2 < recyclerListView.getChildCount(); i2++) {
                        View childAt = recyclerListView.getChildAt(i2);
                        if (childAt instanceof SharedPhotoVideoCell2) {
                            i = ((SharedPhotoVideoCell2) childAt).getMessageId();
                        }
                        if (i != 0) {
                            break;
                        }
                    }
                    if (i == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(0, recyclerListView, true);
                    }
                }
            }
        }

        @Override
        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, null, false);
            }
        }
    }

    public class StoriesAdapter extends SharedPhotoVideoAdapter {
        public boolean applyingReorder;
        private int id;
        private final boolean isArchive;
        public ArrayList lastPinnedIds;
        private ViewsForPeerStoriesRequester poller;
        public StoriesController.StoriesList storiesList;
        private StoriesAdapter supportingAdapter;
        private final ArrayList uploadingStories;

        public StoriesAdapter(android.content.Context r9, boolean r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.StoriesAdapter.<init>(org.telegram.ui.Components.SharedMediaLayout, android.content.Context, boolean):void");
        }

        private void checkColumns() {
            if (this.storiesList == null || this.isArchive) {
                return;
            }
            if ((!SharedMediaLayout.this.storiesColumnsCountSet || (SharedMediaLayout.this.allowStoriesSingleColumn && this.storiesList.getCount() > 1)) && this.storiesList.getCount() > 0 && !SharedMediaLayout.this.isStoriesView()) {
                if (this.storiesList.getCount() < 5) {
                    SharedMediaLayout.this.mediaColumnsCount[1] = this.storiesList.getCount();
                    if (SharedMediaLayout.this.mediaPages != null && SharedMediaLayout.this.mediaPages[0] != null && SharedMediaLayout.this.mediaPages[1] != null && SharedMediaLayout.this.mediaPages[0].listView != null && SharedMediaLayout.this.mediaPages[1].listView != null) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    sharedMediaLayout.allowStoriesSingleColumn = sharedMediaLayout.mediaColumnsCount[1] == 1;
                } else if (SharedMediaLayout.this.allowStoriesSingleColumn) {
                    SharedMediaLayout.this.allowStoriesSingleColumn = false;
                    SharedMediaLayout.this.mediaColumnsCount[1] = Math.max(2, SharedConfig.storiesColumnsCount);
                    if (SharedMediaLayout.this.mediaPages != null && SharedMediaLayout.this.mediaPages[0] != null && SharedMediaLayout.this.mediaPages[1] != null && SharedMediaLayout.this.mediaPages[0].listView != null && SharedMediaLayout.this.mediaPages[1].listView != null) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                }
                SharedMediaLayout.this.storiesColumnsCountSet = true;
            }
        }

        public boolean canReorder(int i) {
            StoriesController.StoriesList storiesList;
            if (this.isArchive || (storiesList = this.storiesList) == null) {
                return false;
            }
            if (storiesList instanceof StoriesController.BotPreviewsList) {
                TLRPC.User user = MessagesController.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(SharedMediaLayout.this.dialog_id));
                return user != null && user.bot && user.bot_has_main_app && user.bot_can_edit;
            }
            if (i < 0 || i >= storiesList.messageObjects.size()) {
                return false;
            }
            return this.storiesList.isPinned(((MessageObject) this.storiesList.messageObjects.get(i)).getId());
        }

        public int columnsCount() {
            return this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount[0] : (this == SharedMediaLayout.this.storiesAdapter || this == SharedMediaLayout.this.archivedStoriesAdapter) ? SharedMediaLayout.this.mediaColumnsCount[1] : SharedMediaLayout.this.animateToColumnsCount;
        }

        public void destroy() {
            StoriesController.StoriesList storiesList = this.storiesList;
            if (storiesList != null) {
                storiesList.unlink(this.id);
            }
        }

        @Override
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            return super.fastScrollIsVisible(recyclerListView);
        }

        @Override
        public int getItemCount() {
            if (this.storiesList == null) {
                return 0;
            }
            return this.uploadingStories.size() + ((this.storiesList.isOnlyCache() && SharedMediaLayout.this.hasInternet()) ? 0 : this.storiesList.getCount());
        }

        @Override
        public int getItemViewType(int i) {
            return 19;
        }

        @Override
        public String getLetter(int i) {
            int topOffset;
            MessageObject messageObject;
            TL_stories.StoryItem storyItem;
            if (this.storiesList == null || (topOffset = i - getTopOffset()) < 0 || topOffset >= this.storiesList.messageObjects.size() || (messageObject = (MessageObject) this.storiesList.messageObjects.get(topOffset)) == null || (storyItem = messageObject.storyItem) == null) {
                return null;
            }
            return LocaleController.formatYearMont(storyItem.date, true);
        }

        @Override
        public int getPositionForIndex(int i) {
            return this.isArchive ? getTopOffset() + i : i;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            super.getPositionForScrollProgress(recyclerListView, f, iArr);
        }

        @Override
        public float getScrollProgress(RecyclerListView recyclerListView) {
            return super.getScrollProgress(recyclerListView);
        }

        public int getTopOffset() {
            return 0;
        }

        @Override
        public int getTotalItemsCount() {
            return getItemCount();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public void load(boolean z) {
            if (this.storiesList == null) {
                return;
            }
            int columnsCount = columnsCount();
            this.storiesList.load(z, Math.min(100, Math.max(1, columnsCount / 2) * columnsCount * columnsCount));
        }

        @Override
        public void notifyDataSetChanged() {
            if (this.storiesList != null && SharedMediaLayout.this.isBot()) {
                this.uploadingStories.clear();
                ArrayList uploadingStories = MessagesController.getInstance(this.storiesList.currentAccount).getStoriesController().getUploadingStories(SharedMediaLayout.this.dialog_id);
                if (uploadingStories != null) {
                    this.uploadingStories.addAll(uploadingStories);
                }
            }
            super.notifyDataSetChanged();
            StoriesAdapter storiesAdapter = this.supportingAdapter;
            if (storiesAdapter != null) {
                storiesAdapter.notifyDataSetChanged();
            }
            checkColumns();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (this.storiesList != null && viewHolder.getItemViewType() == 19) {
                View view = viewHolder.itemView;
                if (view instanceof SharedPhotoVideoCell2) {
                    SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                    sharedPhotoVideoCell2.isStory = true;
                    if (i >= 0 && i < this.uploadingStories.size()) {
                        StoriesController.UploadingStory uploadingStory = (StoriesController.UploadingStory) this.uploadingStories.get(i);
                        sharedPhotoVideoCell2.isStoryPinned = false;
                        if (uploadingStory.sharedMessageObject == null) {
                            TL_stories.TL_storyItem tL_storyItem = new TL_stories.TL_storyItem();
                            int m = FactCheckController$Key$$ExternalSyntheticBackport0.m(uploadingStory.random_id);
                            tL_storyItem.messageId = m;
                            tL_storyItem.id = m;
                            tL_storyItem.attachPath = uploadingStory.firstFramePath;
                            MessageObject messageObject = new MessageObject(this.storiesList.currentAccount, tL_storyItem) {
                                @Override
                                public float getProgress() {
                                    return this.uploadingStory.progress;
                                }
                            };
                            uploadingStory.sharedMessageObject = messageObject;
                            messageObject.uploadingStory = uploadingStory;
                        }
                        sharedPhotoVideoCell2.setMessageObject(uploadingStory.sharedMessageObject, columnsCount());
                        sharedPhotoVideoCell2.isStory = true;
                        sharedPhotoVideoCell2.setReorder(false);
                        sharedPhotoVideoCell2.setChecked(false, false);
                        return;
                    }
                    int size = i - this.uploadingStories.size();
                    if (size < 0 || size >= this.storiesList.messageObjects.size()) {
                        sharedPhotoVideoCell2.isStoryPinned = false;
                        sharedPhotoVideoCell2.setMessageObject(null, columnsCount());
                        sharedPhotoVideoCell2.isStory = true;
                        return;
                    }
                    MessageObject messageObject2 = (MessageObject) this.storiesList.messageObjects.get(size);
                    sharedPhotoVideoCell2.isStoryPinned = messageObject2 != null && this.storiesList.isPinned(messageObject2.getId());
                    sharedPhotoVideoCell2.setReorder(SharedMediaLayout.this.isBot() || sharedPhotoVideoCell2.isStoryPinned);
                    sharedPhotoVideoCell2.isSearchingHashtag = SharedMediaLayout.this.isSearchingStories();
                    sharedPhotoVideoCell2.setMessageObject(messageObject2, columnsCount());
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    if (!sharedMediaLayout.isActionModeShowed || messageObject2 == null) {
                        sharedPhotoVideoCell2.setChecked(false, false);
                    } else {
                        sharedPhotoVideoCell2.setChecked(sharedMediaLayout.selectedFiles[(messageObject2.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0, true);
                    }
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            RecyclerView.ViewHolder onCreateViewHolder = super.onCreateViewHolder(viewGroup, i);
            View view = onCreateViewHolder.itemView;
            if (view instanceof SharedPhotoVideoCell2) {
                ((SharedPhotoVideoCell2) view).isStory = true;
            }
            return onCreateViewHolder;
        }

        @Override
        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar(this.isArchive ? 9 : 8, true);
        }

        @Override
        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            super.onFinishFastScroll(recyclerListView);
        }

        @Override
        public void onStartFastScroll() {
            super.onStartFastScroll();
        }

        public void reorderDone() {
            StoriesController.StoriesList storiesList;
            ArrayList arrayList;
            if (this.isArchive || (storiesList = this.storiesList) == null || !this.applyingReorder) {
                return;
            }
            if (storiesList instanceof StoriesController.BotPreviewsList) {
                arrayList = new ArrayList();
                for (int i = 0; i < this.storiesList.messageObjects.size(); i++) {
                    arrayList.add(Integer.valueOf(((MessageObject) this.storiesList.messageObjects.get(i)).getId()));
                }
            } else {
                arrayList = storiesList.pinnedIds;
            }
            boolean z = this.lastPinnedIds.size() != arrayList.size();
            if (!z) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.lastPinnedIds.size()) {
                        break;
                    }
                    if (this.lastPinnedIds.get(i2) != arrayList.get(i2)) {
                        z = true;
                        break;
                    }
                    i2++;
                }
            }
            if (z) {
                this.storiesList.updatePinnedOrder(arrayList, true);
            }
            this.applyingReorder = false;
        }

        public boolean swapElements(int i, int i2) {
            StoriesController.StoriesList storiesList;
            ArrayList arrayList;
            if (this.isArchive || (storiesList = this.storiesList) == null || i < 0 || i >= storiesList.messageObjects.size() || i2 < 0 || i2 >= this.storiesList.messageObjects.size()) {
                return false;
            }
            if (this.storiesList instanceof StoriesController.BotPreviewsList) {
                arrayList = new ArrayList();
                for (int i3 = 0; i3 < this.storiesList.messageObjects.size(); i3++) {
                    arrayList.add(Integer.valueOf(((MessageObject) this.storiesList.messageObjects.get(i3)).getId()));
                }
            } else {
                arrayList = new ArrayList(this.storiesList.pinnedIds);
            }
            if (!this.applyingReorder) {
                this.lastPinnedIds.clear();
                this.lastPinnedIds.addAll(arrayList);
                this.applyingReorder = true;
            }
            MessageObject messageObject = (MessageObject) this.storiesList.messageObjects.get(i);
            arrayList.remove(Integer.valueOf(messageObject.getId()));
            arrayList.add(Utilities.clamp(i2, arrayList.size(), 0), Integer.valueOf(messageObject.getId()));
            this.storiesList.updatePinnedOrder(arrayList, false);
            notifyItemMoved(i, i2);
            return true;
        }
    }

    public SharedMediaLayout(android.content.Context r34, long r35, org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloader r37, int r38, java.util.ArrayList r39, org.telegram.tgnet.TLRPC.ChatFull r40, org.telegram.tgnet.TLRPC.UserFull r41, int r42, org.telegram.ui.ActionBar.BaseFragment r43, org.telegram.ui.Components.SharedMediaLayout.Delegate r44, int r45, org.telegram.ui.ActionBar.Theme.ResourcesProvider r46) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.<init>(android.content.Context, long, org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader, int, java.util.ArrayList, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.tgnet.TLRPC$UserFull, int, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.SharedMediaLayout$Delegate, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static int access$5008(SharedMediaLayout sharedMediaLayout) {
        int i = sharedMediaLayout.cantDeleteMessagesCount;
        sharedMediaLayout.cantDeleteMessagesCount = i + 1;
        return i;
    }

    static int access$5010(SharedMediaLayout sharedMediaLayout) {
        int i = sharedMediaLayout.cantDeleteMessagesCount;
        sharedMediaLayout.cantDeleteMessagesCount = i - 1;
        return i;
    }

    private CharSequence addPostText() {
        SpannableStringBuilder append;
        int i;
        if (this.addPostButton == null) {
            this.addPostButton = new SpannableStringBuilder();
            if (isBot()) {
                append = this.addPostButton;
                i = R.string.ProfileBotPreviewEmptyButton;
            } else {
                this.addPostButton.append((CharSequence) "c");
                this.addPostButton.setSpan(new ColoredImageSpan(R.drawable.filled_premium_camera), 0, 1, 33);
                append = this.addPostButton.append((CharSequence) "  ");
                i = R.string.StoriesAddPost;
            }
            append.append((CharSequence) LocaleController.getString(i));
        }
        return this.addPostButton;
    }

    public void animateItemsEnter(RecyclerListView recyclerListView, int i, SparseBooleanArray sparseBooleanArray) {
        int childCount = recyclerListView.getChildCount();
        View view = null;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = recyclerListView.getChildAt(i2);
            if (childAt instanceof FlickerLoadingView) {
                view = childAt;
            }
        }
        if (view != null) {
            recyclerListView.removeView(view);
        }
        getViewTreeObserver().addOnPreDrawListener(new AnonymousClass41(recyclerListView, sparseBooleanArray, view, i));
    }

    private void animateToMediaColumnsCount(final int i) {
        InternalListView internalListView;
        RecyclerView.Adapter adapter;
        final int i2 = 1;
        final MediaPage mediaPage = getMediaPage(this.changeColumnsTab);
        this.pinchCenterPosition = -1;
        if (mediaPage != null) {
            mediaPage.listView.stopScroll();
            this.animateToColumnsCount = i;
            mediaPage.animationSupportingListView.setVisibility(0);
            int i3 = this.changeColumnsTab;
            if (i3 == 8) {
                internalListView = mediaPage.animationSupportingListView;
                adapter = this.animationSupportingStoriesAdapter;
            } else if (i3 == 9) {
                internalListView = mediaPage.animationSupportingListView;
                adapter = this.animationSupportingArchivedStoriesAdapter;
            } else {
                internalListView = mediaPage.animationSupportingListView;
                adapter = this.animationSupportingPhotoVideoAdapter;
            }
            internalListView.setAdapter(adapter);
            InternalListView internalListView2 = mediaPage.animationSupportingListView;
            int paddingLeft = mediaPage.animationSupportingListView.getPaddingLeft();
            InternalListView internalListView3 = mediaPage.animationSupportingListView;
            int dp = this.changeColumnsTab == 9 ? AndroidUtilities.dp(64.0f) : 0;
            internalListView3.hintPaddingTop = dp;
            int paddingRight = mediaPage.animationSupportingListView.getPaddingRight();
            InternalListView internalListView4 = mediaPage.animationSupportingListView;
            int dp2 = isStoriesView() ? AndroidUtilities.dp(72.0f) : 0;
            internalListView4.hintPaddingBottom = dp2;
            internalListView2.setPadding(paddingLeft, dp, paddingRight, dp2);
            mediaPage.buttonView.setVisibility(this.changeColumnsTab == 8 ? 0 : 8);
            mediaPage.buttonView.setVisibility(this.changeColumnsTab == 8 ? 0 : 8);
            mediaPage.animationSupportingLayoutManager.setSpanCount(i);
            mediaPage.animationSupportingListView.invalidateItemDecorations();
            int i4 = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i4 >= mediaPageArr.length) {
                    break;
                }
                MediaPage mediaPage2 = mediaPageArr[i4];
                if (mediaPage2 != null && isTabZoomable(mediaPage2.selectedType)) {
                    AndroidUtilities.updateVisibleRows(this.mediaPages[i4].listView);
                }
                i4++;
            }
            this.photoVideoChangeColumnsAnimation = true;
            if (this.changeColumnsTab == 0) {
                this.sharedMediaData[0].setListFrozen(true);
            }
            this.photoVideoChangeColumnsProgress = 0.0f;
            saveScrollPosition();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.notificationsLocker.lock();
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    mediaPage.listView.invalidate();
                }
            });
            if (mediaPage.selectedType != 8 && mediaPage.selectedType != 9) {
                i2 = 0;
            }
            ofFloat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    RecyclerView.Adapter adapter2;
                    SharedMediaLayout.this.notificationsLocker.unlock();
                    SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    SharedMediaLayout.this.mediaColumnsCount[i2] = i;
                    for (int i5 = 0; i5 < SharedMediaLayout.this.mediaPages.length; i5++) {
                        if (SharedMediaLayout.this.mediaPages[i5] != null && SharedMediaLayout.this.mediaPages[i5].listView != null) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            if (sharedMediaLayout.isTabZoomable(sharedMediaLayout.mediaPages[i5].selectedType) && (adapter2 = SharedMediaLayout.this.mediaPages[i5].listView.getAdapter()) != null) {
                                int itemCount = adapter2.getItemCount();
                                if (i5 == 0) {
                                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                                }
                                SharedMediaLayout.this.mediaPages[i5].layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount[i2]);
                                SharedMediaLayout.this.mediaPages[i5].listView.invalidateItemDecorations();
                                if (adapter2.getItemCount() == itemCount) {
                                    AndroidUtilities.updateVisibleRows(SharedMediaLayout.this.mediaPages[i5].listView);
                                } else {
                                    adapter2.notifyDataSetChanged();
                                }
                                SharedMediaLayout.this.mediaPages[i5].animationSupportingListView.setVisibility(8);
                            }
                        }
                    }
                    SharedMediaLayout.this.saveScrollPosition();
                }
            });
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setStartDelay(100L);
            ofFloat.setDuration(350L);
            ofFloat.start();
        }
    }

    public void changeMediaFilterType() {
        final Bitmap bitmap;
        final MediaPage mediaPage = getMediaPage(0);
        if (mediaPage != null && mediaPage.getMeasuredHeight() > 0 && mediaPage.getMeasuredWidth() > 0) {
            try {
                bitmap = Bitmap.createBitmap(mediaPage.getMeasuredWidth(), mediaPage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            } catch (Exception e) {
                FileLog.e(e);
                bitmap = null;
            }
            if (bitmap != null) {
                this.changeTypeAnimation = true;
                mediaPage.listView.draw(new Canvas(bitmap));
                final View view = new View(mediaPage.getContext());
                view.setBackground(new BitmapDrawable(bitmap));
                mediaPage.addView(view);
                view.animate().alpha(0.0f).setDuration(200L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        SharedMediaLayout.this.changeTypeAnimation = false;
                        if (view.getParent() != null) {
                            mediaPage.removeView(view);
                            bitmap.recycle();
                        }
                    }
                }).start();
                mediaPage.listView.setAlpha(0.0f);
                mediaPage.listView.animate().alpha(1.0f).setDuration(200L).start();
            }
        }
        int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
        ArrayList arrayList = this.sharedMediaPreloader.getSharedMediaData()[0].messages;
        SharedMediaData sharedMediaData = this.sharedMediaData[0];
        int i = sharedMediaData.filterType;
        sharedMediaData.setTotalCount(i == 0 ? lastMediaCount[0] : i == 1 ? lastMediaCount[6] : lastMediaCount[7]);
        this.sharedMediaData[0].fastScrollDataLoaded = false;
        jumpToDate(0, DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE, 0, true);
        loadFastScrollData(false);
        this.delegate.updateSelectedMediaTabText();
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(this.dialog_id);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            MessageObject messageObject = (MessageObject) arrayList.get(i2);
            SharedMediaData sharedMediaData2 = this.sharedMediaData[0];
            int i3 = sharedMediaData2.filterType;
            if (i3 != 0) {
                boolean isPhoto = messageObject.isPhoto();
                if (i3 == 1) {
                    if (isPhoto) {
                        sharedMediaData2 = this.sharedMediaData[0];
                    }
                } else if (!isPhoto) {
                    sharedMediaData2 = this.sharedMediaData[0];
                }
            }
            sharedMediaData2.addMessage(messageObject, 0, false, isEncryptedDialog);
        }
    }

    public void checkCurrentTabValid() {
        if (this.scrollSlidingTextTabStrip.hasTab(this.scrollSlidingTextTabStrip.getCurrentTabId())) {
            return;
        }
        int firstTabId = this.scrollSlidingTextTabStrip.getFirstTabId();
        this.scrollSlidingTextTabStrip.setInitialTabId(firstTabId);
        this.mediaPages[0].selectedType = firstTabId;
        switchToCurrentSelectedMode(false);
    }

    public void checkLoadMoreScroll(MediaPage mediaPage, final RecyclerListView recyclerListView, LinearLayoutManager linearLayoutManager) {
        int i;
        int i2;
        MediaDataController mediaDataController;
        long j;
        int i3;
        long j2;
        int classGuid;
        SharedMediaData sharedMediaData;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        ChatActionCell chatActionCell;
        int date;
        StoriesAdapter storiesAdapter;
        if (this.photoVideoChangeColumnsAnimation || this.jumpToRunnable != null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (recyclerListView.getFastScroll() == null || !recyclerListView.getFastScroll().isPressed() || currentTimeMillis - mediaPage.lastCheckScrollTime >= 300) {
            mediaPage.lastCheckScrollTime = currentTimeMillis;
            if ((this.searching && this.searchWas && mediaPage.selectedType != 11) || mediaPage.selectedType == 7) {
                return;
            }
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            int itemCount = recyclerListView.getAdapter() == null ? 0 : recyclerListView.getAdapter().getItemCount();
            if (mediaPage.selectedType == 0 || mediaPage.selectedType == 1 || mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
                final int i4 = mediaPage.selectedType;
                int startOffset = this.sharedMediaData[i4].getStartOffset() + this.sharedMediaData[i4].messages.size();
                SharedMediaData sharedMediaData2 = this.sharedMediaData[i4];
                if (sharedMediaData2.fastScrollDataLoaded && sharedMediaData2.fastScrollPeriods.size() > 2 && mediaPage.selectedType == 0 && this.sharedMediaData[i4].messages.size() != 0) {
                    float f = i4 == 0 ? this.mediaColumnsCount[0] : 1;
                    int measuredHeight = (int) ((recyclerListView.getMeasuredHeight() / (recyclerListView.getMeasuredWidth() / f)) * f * 1.5f);
                    if (measuredHeight < 100) {
                        measuredHeight = 100;
                    }
                    if (measuredHeight < ((Period) this.sharedMediaData[i4].fastScrollPeriods.get(1)).startOffset) {
                        measuredHeight = ((Period) this.sharedMediaData[i4].fastScrollPeriods.get(1)).startOffset;
                    }
                    if ((findFirstVisibleItemPosition > startOffset && findFirstVisibleItemPosition - startOffset > measuredHeight) || ((i = findFirstVisibleItemPosition + abs) < this.sharedMediaData[i4].startOffset && this.sharedMediaData[0].startOffset - i > measuredHeight)) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public final void run() {
                                SharedMediaLayout.this.lambda$checkLoadMoreScroll$13(i4, recyclerListView);
                            }
                        };
                        this.jumpToRunnable = runnable;
                        AndroidUtilities.runOnUIThread(runnable);
                        return;
                    }
                }
                itemCount = startOffset;
            }
            if (mediaPage.selectedType == 7) {
                return;
            }
            if (mediaPage.selectedType == 8) {
                StoriesController.StoriesList storiesList = this.storiesAdapter.storiesList;
                if (storiesList == null || findFirstVisibleItemPosition + abs <= storiesList.getLoadedCount() - this.mediaColumnsCount[1]) {
                    return;
                } else {
                    storiesAdapter = this.storiesAdapter;
                }
            } else {
                if (mediaPage.selectedType != 9) {
                    if (mediaPage.selectedType == 6) {
                        if (abs <= 0 || this.commonGroupsAdapter.endReached || this.commonGroupsAdapter.loading || this.commonGroupsAdapter.chats.isEmpty() || findFirstVisibleItemPosition + abs < itemCount - 5) {
                            return;
                        }
                        CommonGroupsAdapter commonGroupsAdapter = this.commonGroupsAdapter;
                        commonGroupsAdapter.getChats(((TLRPC.Chat) commonGroupsAdapter.chats.get(this.commonGroupsAdapter.chats.size() - 1)).id, 100);
                        return;
                    }
                    if (mediaPage.selectedType == 11) {
                        int i5 = -1;
                        for (int i6 = 0; i6 < mediaPage.listView.getChildCount(); i6++) {
                            i5 = Math.max(mediaPage.listView.getChildAdapterPosition(mediaPage.listView.getChildAt(i6)), i5);
                        }
                        RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
                        SavedMessagesSearchAdapter savedMessagesSearchAdapter = this.savedMessagesSearchAdapter;
                        int i7 = i5 + 1;
                        if (adapter == savedMessagesSearchAdapter) {
                            if (i7 >= savedMessagesSearchAdapter.dialogs.size() + this.savedMessagesSearchAdapter.loadedMessages.size()) {
                                this.savedMessagesSearchAdapter.loadMore();
                                return;
                            }
                            return;
                        } else {
                            if (i7 >= this.profileActivity.getMessagesController().getSavedMessagesController().getLoadedCount()) {
                                this.profileActivity.getMessagesController().getSavedMessagesController().loadDialogs(false);
                                return;
                            }
                            return;
                        }
                    }
                    int i8 = 10;
                    if (mediaPage.selectedType == 10 || mediaPage.selectedType == 12 || mediaPage.selectedType == 13 || mediaPage.selectedType == 14) {
                        return;
                    }
                    if (mediaPage.selectedType == 0) {
                        i8 = 3;
                    } else if (mediaPage.selectedType != 5) {
                        i8 = 6;
                    }
                    if ((abs + findFirstVisibleItemPosition > itemCount - i8 || this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) && !this.sharedMediaData[mediaPage.selectedType].loading) {
                        if (mediaPage.selectedType == 0) {
                            int i9 = this.sharedMediaData[0].filterType;
                            i2 = i9 == 1 ? 6 : i9 == 2 ? 7 : 0;
                        } else {
                            i2 = mediaPage.selectedType == 1 ? 1 : mediaPage.selectedType == 2 ? 2 : mediaPage.selectedType == 4 ? 4 : mediaPage.selectedType == 5 ? 5 : 3;
                        }
                        if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                            this.sharedMediaData[mediaPage.selectedType].loading = true;
                            mediaDataController = this.profileActivity.getMediaDataController();
                            j = this.dialog_id;
                            i3 = this.sharedMediaData[mediaPage.selectedType].max_id[0];
                            j2 = this.topicId;
                            classGuid = this.profileActivity.getClassGuid();
                            sharedMediaData = this.sharedMediaData[mediaPage.selectedType];
                        } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                            this.sharedMediaData[mediaPage.selectedType].loading = true;
                            mediaDataController = this.profileActivity.getMediaDataController();
                            j = this.mergeDialogId;
                            i3 = this.sharedMediaData[mediaPage.selectedType].max_id[1];
                            j2 = this.topicId;
                            classGuid = this.profileActivity.getClassGuid();
                            sharedMediaData = this.sharedMediaData[mediaPage.selectedType];
                        }
                        mediaDataController.loadMedia(j, 50, i3, 0, i2, j2, 1, classGuid, sharedMediaData.requestIndex, null, null);
                    }
                    int i10 = this.sharedMediaData[mediaPage.selectedType].startOffset;
                    if (mediaPage.selectedType == 0) {
                        i10 = this.photoVideoAdapter.getPositionForIndex(0);
                    }
                    if (findFirstVisibleItemPosition - i10 < i8 + 1 && !this.sharedMediaData[mediaPage.selectedType].loading && !this.sharedMediaData[mediaPage.selectedType].startReached && !this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) {
                        loadFromStart(mediaPage.selectedType);
                    }
                    if (this.mediaPages[0].listView == recyclerListView) {
                        if ((this.mediaPages[0].selectedType != 0 && this.mediaPages[0].selectedType != 5) || findFirstVisibleItemPosition == -1 || (findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) == null) {
                            return;
                        }
                        if (findViewHolderForAdapterPosition.getItemViewType() == 0 || findViewHolderForAdapterPosition.getItemViewType() == 12) {
                            View view = findViewHolderForAdapterPosition.itemView;
                            if (view instanceof SharedPhotoVideoCell) {
                                MessageObject messageObject = ((SharedPhotoVideoCell) view).getMessageObject(0);
                                if (messageObject == null) {
                                    return;
                                }
                                chatActionCell = this.floatingDateView;
                                date = messageObject.messageOwner.date;
                            } else {
                                if (!(view instanceof ContextLinkCell)) {
                                    return;
                                }
                                chatActionCell = this.floatingDateView;
                                date = ((ContextLinkCell) view).getDate();
                            }
                            chatActionCell.setCustomDate(date, false, true);
                            return;
                        }
                        return;
                    }
                    return;
                }
                StoriesController.StoriesList storiesList2 = this.archivedStoriesAdapter.storiesList;
                if (storiesList2 == null || findFirstVisibleItemPosition + abs <= storiesList2.getLoadedCount() - this.mediaColumnsCount[1]) {
                    return;
                } else {
                    storiesAdapter = this.archivedStoriesAdapter;
                }
            }
            storiesAdapter.load(false);
        }
    }

    private boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0);
    }

    public static View createEmptyStubView(Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
        TextView textView;
        String str;
        int i2;
        EmptyStubView emptyStubView = new EmptyStubView(context, resourcesProvider);
        if (i == 0) {
            if (DialogObject.isEncryptedDialog(j)) {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoMediaSecret;
            } else {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoMedia;
            }
        } else if (i == 1) {
            if (DialogObject.isEncryptedDialog(j)) {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedFilesSecret;
            } else {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedFiles;
            }
        } else if (i == 2) {
            if (DialogObject.isEncryptedDialog(j)) {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedVoiceSecret;
            } else {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedVoice;
            }
        } else if (i == 3) {
            if (DialogObject.isEncryptedDialog(j)) {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedLinksSecret;
            } else {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedLinks;
            }
        } else if (i == 4) {
            if (DialogObject.isEncryptedDialog(j)) {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedAudioSecret;
            } else {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedAudio;
            }
        } else if (i == 5) {
            if (DialogObject.isEncryptedDialog(j)) {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoSharedGifSecret;
            } else {
                textView = emptyStubView.emptyTextView;
                i2 = R.string.NoGIFs;
            }
        } else {
            if (i != 6) {
                if (i == 7) {
                    emptyStubView.emptyImageView.setImageDrawable(null);
                    textView = emptyStubView.emptyTextView;
                    str = "";
                    textView.setText(str);
                }
                return emptyStubView;
            }
            emptyStubView.emptyImageView.setImageDrawable(null);
            textView = emptyStubView.emptyTextView;
            i2 = R.string.NoGroupsInCommon;
        }
        str = LocaleController.getString(i2);
        textView.setText(str);
        return emptyStubView;
    }

    private ScrollSlidingTextTabStripInner createScrollingTextTabStrip(Context context) {
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner = new ScrollSlidingTextTabStripInner(context, this.resourcesProvider) {
            @Override
            protected int processColor(int i) {
                return SharedMediaLayout.this.processColor(i);
            }
        };
        int i = this.initialTab;
        if (i != -1) {
            scrollSlidingTextTabStripInner.setInitialTabId(i);
            this.initialTab = -1;
        }
        scrollSlidingTextTabStripInner.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        scrollSlidingTextTabStripInner.setColors(Theme.key_profile_tabSelectedLine, Theme.key_profile_tabSelectedText, Theme.key_profile_tabText, Theme.key_profile_tabSelector);
        scrollSlidingTextTabStripInner.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            @Override
            public void onPageScrolled(float f) {
                MediaPage mediaPage;
                float measuredWidth;
                float measuredWidth2;
                if (f != 1.0f || SharedMediaLayout.this.mediaPages[1].getVisibility() == 0) {
                    if (SharedMediaLayout.this.animatingForward) {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX((-f) * SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                        mediaPage = SharedMediaLayout.this.mediaPages[1];
                        measuredWidth = SharedMediaLayout.this.mediaPages[0].getMeasuredWidth();
                        measuredWidth2 = SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * f;
                    } else {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX(SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * f);
                        mediaPage = SharedMediaLayout.this.mediaPages[1];
                        measuredWidth = SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * f;
                        measuredWidth2 = SharedMediaLayout.this.mediaPages[0].getMeasuredWidth();
                    }
                    mediaPage.setTranslationX(measuredWidth - measuredWidth2);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    sharedMediaLayout.onTabProgress(sharedMediaLayout.getTabProgress());
                    SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                    sharedMediaLayout2.optionsAlpha = sharedMediaLayout2.getPhotoVideoOptionsAlpha(f);
                    SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                    sharedMediaLayout3.photoVideoOptionsItem.setVisibility((sharedMediaLayout3.optionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem() || SharedMediaLayout.this.isArchivedOnlyStoriesView()) ? 4 : 0);
                    if (SharedMediaLayout.this.searchItem == null || SharedMediaLayout.this.canShowSearchItem()) {
                        SharedMediaLayout sharedMediaLayout4 = SharedMediaLayout.this;
                        sharedMediaLayout4.searchAlpha = sharedMediaLayout4.getSearchAlpha(f);
                        SharedMediaLayout.this.updateSearchItemIconAnimated();
                    } else {
                        SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                        SharedMediaLayout.this.searchAlpha = 0.0f;
                    }
                    SharedMediaLayout.this.updateOptionsSearch();
                    if (f == 1.0f) {
                        MediaPage mediaPage2 = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = mediaPage2;
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItem != null && SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                        }
                        SharedMediaLayout.this.searchItemState = 0;
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                }
            }

            @Override
            public void onPageSelected(int i2, boolean z) {
                if (SharedMediaLayout.this.mediaPages[0].selectedType == i2) {
                    return;
                }
                SharedMediaLayout.this.mediaPages[1].selectedType = i2;
                SharedMediaLayout.this.mediaPages[1].setVisibility(0);
                SharedMediaLayout.this.hideFloatingDateView(true);
                SharedMediaLayout.this.switchToCurrentSelectedMode(true);
                SharedMediaLayout.this.animatingForward = z;
                SharedMediaLayout.this.onSelectedTabChanged();
                SharedMediaLayout.this.animateSearchToOptions(!r5.isSearchItemVisible(i2), true);
                SharedMediaLayout.this.updateOptionsSearch(true);
            }

            @Override
            public void onSamePageSelected() {
                SharedMediaLayout.this.scrollToTop();
            }
        });
        return scrollSlidingTextTabStripInner;
    }

    private boolean fillMediaData(int r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.fillMediaData(int):boolean");
    }

    public void findPeriodAndJumpToDate(int i, RecyclerListView recyclerListView, boolean z) {
        ArrayList arrayList = this.sharedMediaData[i].fastScrollPeriods;
        int findFirstVisibleItemPosition = ((LinearLayoutManager) recyclerListView.getLayoutManager()).findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition >= 0) {
            Period period = null;
            if (arrayList != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    if (findFirstVisibleItemPosition <= ((Period) arrayList.get(i2)).startOffset) {
                        period = (Period) arrayList.get(i2);
                        break;
                    }
                    i2++;
                }
                if (period == null) {
                    period = (Period) arrayList.get(arrayList.size() - 1);
                }
            }
            if (period != null) {
                jumpToDate(i, period.maxId, period.startOffset + 1, z);
            }
        }
    }

    private void finishPinchToMediaColumnsCount() {
        final MediaPage mediaPage;
        RecyclerView.Adapter adapter;
        int i = 0;
        if (!this.photoVideoChangeColumnsAnimation) {
            return;
        }
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                mediaPage = null;
                break;
            } else {
                if (mediaPageArr[i2].selectedType == this.changeColumnsTab) {
                    mediaPage = this.mediaPages[i2];
                    break;
                }
                i2++;
            }
        }
        if (mediaPage == null) {
            return;
        }
        final int i3 = (mediaPage.selectedType == 8 || mediaPage.selectedType == 9) ? 1 : 0;
        float f = this.photoVideoChangeColumnsProgress;
        if (f != 1.0f) {
            if (f == 0.0f) {
                this.photoVideoChangeColumnsAnimation = false;
                if (this.changeColumnsTab == 0) {
                    this.sharedMediaData[0].setListFrozen(false);
                }
                mediaPage.animationSupportingListView.setVisibility(8);
                mediaPage.listView.invalidate();
                return;
            }
            final boolean z = f > 0.2f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    mediaPage.listView.invalidate();
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    View findViewByPosition;
                    RecyclerView.Adapter adapter2;
                    SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    if (z) {
                        SharedMediaLayout.this.mediaColumnsCount[i3] = SharedMediaLayout.this.animateToColumnsCount;
                        if (i3 == 0) {
                            SharedConfig.setMediaColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                        } else if (SharedMediaLayout.this.getStoriesCount(mediaPage.selectedType) >= 5) {
                            SharedConfig.setStoriesColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                        }
                    }
                    for (int i4 = 0; i4 < SharedMediaLayout.this.mediaPages.length; i4++) {
                        if (SharedMediaLayout.this.mediaPages[i4] != null && SharedMediaLayout.this.mediaPages[i4].listView != null) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            if (sharedMediaLayout.isTabZoomable(sharedMediaLayout.mediaPages[i4].selectedType) && (adapter2 = SharedMediaLayout.this.mediaPages[i4].listView.getAdapter()) != null) {
                                int itemCount = adapter2.getItemCount();
                                if (i4 == 0) {
                                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                                }
                                if (z) {
                                    SharedMediaLayout.this.mediaPages[i4].layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount[i3]);
                                    SharedMediaLayout.this.mediaPages[i4].listView.invalidateItemDecorations();
                                    if (adapter2.getItemCount() == itemCount) {
                                        AndroidUtilities.updateVisibleRows(SharedMediaLayout.this.mediaPages[i4].listView);
                                    } else {
                                        adapter2.notifyDataSetChanged();
                                    }
                                }
                                SharedMediaLayout.this.mediaPages[i4].animationSupportingListView.setVisibility(8);
                            }
                        }
                    }
                    SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                    if (sharedMediaLayout2.pinchCenterPosition >= 0) {
                        for (int i5 = 0; i5 < SharedMediaLayout.this.mediaPages.length; i5++) {
                            if (SharedMediaLayout.this.mediaPages[i5].selectedType == SharedMediaLayout.this.changeColumnsTab) {
                                if (z && (findViewByPosition = SharedMediaLayout.this.mediaPages[i5].animationSupportingLayoutManager.findViewByPosition(SharedMediaLayout.this.pinchCenterPosition)) != null) {
                                    SharedMediaLayout.this.pinchCenterOffset = findViewByPosition.getTop();
                                }
                                ExtendedGridLayoutManager extendedGridLayoutManager = SharedMediaLayout.this.mediaPages[i5].layoutManager;
                                SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                                extendedGridLayoutManager.scrollToPositionWithOffset(sharedMediaLayout3.pinchCenterPosition, (-sharedMediaLayout3.mediaPages[i5].listView.getPaddingTop()) + SharedMediaLayout.this.pinchCenterOffset);
                            }
                        }
                    } else {
                        sharedMediaLayout2.saveScrollPosition();
                    }
                    super.onAnimationEnd(animator);
                }
            });
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setDuration(200L);
            ofFloat.start();
            return;
        }
        this.photoVideoChangeColumnsAnimation = false;
        int[] iArr = this.mediaColumnsCount;
        int i4 = this.animateToColumnsCount;
        iArr[i3] = i4;
        if (i3 == 0) {
            SharedConfig.setMediaColumnsCount(i4);
        } else if (getStoriesCount(mediaPage.selectedType) >= 5) {
            SharedConfig.setStoriesColumnsCount(this.animateToColumnsCount);
        }
        int i5 = 0;
        while (true) {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            if (i5 >= mediaPageArr2.length) {
                break;
            }
            MediaPage mediaPage2 = mediaPageArr2[i5];
            if (mediaPage2 != null && mediaPage2.listView != null && isTabZoomable(this.mediaPages[i5].selectedType) && (adapter = this.mediaPages[i5].listView.getAdapter()) != null) {
                int itemCount = adapter.getItemCount();
                if (i5 == 0) {
                    this.sharedMediaData[0].setListFrozen(false);
                }
                this.mediaPages[i5].animationSupportingListView.setVisibility(8);
                this.mediaPages[i5].layoutManager.setSpanCount(this.mediaColumnsCount[i3]);
                this.mediaPages[i5].listView.invalidateItemDecorations();
                this.mediaPages[i5].listView.invalidate();
                if (adapter.getItemCount() == itemCount) {
                    AndroidUtilities.updateVisibleRows(this.mediaPages[i5].listView);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
            i5++;
        }
        if (this.pinchCenterPosition < 0) {
            saveScrollPosition();
            return;
        }
        while (true) {
            MediaPage[] mediaPageArr3 = this.mediaPages;
            if (i >= mediaPageArr3.length) {
                return;
            }
            if (mediaPageArr3[i].selectedType == this.changeColumnsTab) {
                View findViewByPosition = this.mediaPages[i].animationSupportingLayoutManager.findViewByPosition(this.pinchCenterPosition);
                if (findViewByPosition != null) {
                    this.pinchCenterOffset = findViewByPosition.getTop();
                }
                this.mediaPages[i].layoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, (-this.mediaPages[i].listView.getPaddingTop()) + this.pinchCenterOffset);
            }
            i++;
        }
    }

    public void fixLayoutInternal(int i) {
        NumberTextView numberTextView;
        int i2;
        ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (i == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                numberTextView = this.selectedMessagesCountTextView;
                i2 = 20;
            } else {
                numberTextView = this.selectedMessagesCountTextView;
                i2 = 18;
            }
            numberTextView.setTextSize(i2);
        }
        if (i == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    public MediaPage getMediaPage(int i) {
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                return null;
            }
            MediaPage mediaPage = mediaPageArr[i2];
            if (mediaPage != null && mediaPage.selectedType == i) {
                return this.mediaPages[i2];
            }
            i2++;
        }
    }

    public static int getMessageId(View view) {
        if (view instanceof SharedPhotoVideoCell2) {
            return ((SharedPhotoVideoCell2) view).getMessageId();
        }
        if (view instanceof SharedDocumentCell) {
            return ((SharedDocumentCell) view).getMessage().getId();
        }
        if (view instanceof SharedAudioCell) {
            return ((SharedAudioCell) view).getMessage().getId();
        }
        return 0;
    }

    public int getThemedColor(int i) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.getColor(i) : Theme.getColor(i);
    }

    private boolean hasNoforwardsMessage() {
        MessageObject messageObject;
        TLRPC.Message message;
        boolean z = false;
        for (int i = 1; i >= 0; i--) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedFiles[i].size(); i2++) {
                arrayList.add(Integer.valueOf(this.selectedFiles[i].keyAt(i2)));
            }
            Iterator it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Integer num = (Integer) it.next();
                if (num.intValue() > 0 && (messageObject = (MessageObject) this.selectedFiles[i].get(num.intValue())) != null && (message = messageObject.messageOwner) != null && message.noforwards) {
                    z = true;
                    break;
                }
            }
            if (z) {
                break;
            }
        }
        return z;
    }

    public void hideFloatingDateView(boolean z) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() == null) {
            return;
        }
        this.floatingDateView.setTag(null);
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.floatingDateAnimation = null;
        }
        if (!z) {
            this.floatingDateView.setAlpha(0.0f);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.floatingDateAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.TRANSLATION_Y, (-AndroidUtilities.dp(48.0f)) + this.additionalFloatingTranslation));
        this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                SharedMediaLayout.this.floatingDateAnimation = null;
            }
        });
        this.floatingDateAnimation.start();
    }

    public void jumpToDate(int i, int i2, int i3, boolean z) {
        this.sharedMediaData[i].messages.clear();
        this.sharedMediaData[i].messagesDict[0].clear();
        this.sharedMediaData[i].messagesDict[1].clear();
        this.sharedMediaData[i].setMaxId(0, i2);
        this.sharedMediaData[i].setEndReached(0, false);
        SharedMediaData sharedMediaData = this.sharedMediaData[i];
        sharedMediaData.startReached = false;
        sharedMediaData.startOffset = i3;
        SharedMediaData sharedMediaData2 = this.sharedMediaData[i];
        sharedMediaData2.endLoadingStubs = (sharedMediaData2.totalCount - i3) - 1;
        if (this.sharedMediaData[i].endLoadingStubs < 0) {
            this.sharedMediaData[i].endLoadingStubs = 0;
        }
        SharedMediaData sharedMediaData3 = this.sharedMediaData[i];
        sharedMediaData3.min_id = i2;
        sharedMediaData3.loadingAfterFastScroll = true;
        sharedMediaData3.loading = false;
        sharedMediaData3.requestIndex++;
        MediaPage mediaPage = getMediaPage(i);
        if (mediaPage != null && mediaPage.listView.getAdapter() != null) {
            mediaPage.listView.getAdapter().notifyDataSetChanged();
        }
        if (!z) {
            return;
        }
        int i4 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i4 >= mediaPageArr.length) {
                return;
            }
            if (mediaPageArr[i4].selectedType == i) {
                ExtendedGridLayoutManager extendedGridLayoutManager = this.mediaPages[i4].layoutManager;
                SharedMediaData sharedMediaData4 = this.sharedMediaData[i];
                extendedGridLayoutManager.scrollToPositionWithOffset(Math.min(sharedMediaData4.totalCount - 1, sharedMediaData4.startOffset), 0);
            }
            i4++;
        }
    }

    public void lambda$checkLoadMoreScroll$13(int i, RecyclerListView recyclerListView) {
        findPeriodAndJumpToDate(i, recyclerListView, false);
        this.jumpToRunnable = null;
    }

    public void lambda$getThemeDescriptions$28(int i) {
        if (this.mediaPages[i].listView != null) {
            int childCount = this.mediaPages[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) childAt).updateCheckboxColor();
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                } else if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }

    public static int lambda$loadFastScrollData$14(Period period, Period period2) {
        return period2.date - period.date;
    }

    public void lambda$loadFastScrollData$15(TLRPC.TL_error tL_error, int i, int i2, TLObject tLObject) {
        if (tL_error != null) {
            return;
        }
        SharedMediaData sharedMediaData = this.sharedMediaData[i2];
        if (i != sharedMediaData.requestIndex) {
            return;
        }
        TLRPC.TL_messages_searchResultsPositions tL_messages_searchResultsPositions = (TLRPC.TL_messages_searchResultsPositions) tLObject;
        sharedMediaData.fastScrollPeriods.clear();
        int size = tL_messages_searchResultsPositions.positions.size();
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            TLRPC.TL_searchResultPosition tL_searchResultPosition = tL_messages_searchResultsPositions.positions.get(i4);
            if (tL_searchResultPosition.date != 0) {
                this.sharedMediaData[i2].fastScrollPeriods.add(new Period(tL_searchResultPosition));
            }
        }
        Collections.sort(this.sharedMediaData[i2].fastScrollPeriods, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$loadFastScrollData$14;
                lambda$loadFastScrollData$14 = SharedMediaLayout.lambda$loadFastScrollData$14((SharedMediaLayout.Period) obj, (SharedMediaLayout.Period) obj2);
                return lambda$loadFastScrollData$14;
            }
        });
        this.sharedMediaData[i2].setTotalCount(tL_messages_searchResultsPositions.count);
        SharedMediaData sharedMediaData2 = this.sharedMediaData[i2];
        sharedMediaData2.fastScrollDataLoaded = true;
        if (!sharedMediaData2.fastScrollPeriods.isEmpty()) {
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i3 >= mediaPageArr.length) {
                    break;
                }
                if (mediaPageArr[i3].selectedType == i2) {
                    MediaPage mediaPage = this.mediaPages[i3];
                    mediaPage.fastScrollEnabled = true;
                    updateFastScrollVisibility(mediaPage, true);
                }
                i3++;
            }
        }
        this.photoVideoAdapter.notifyDataSetChanged();
    }

    public void lambda$loadFastScrollData$16(final TLRPC.TL_error tL_error, final int i, final int i2, final TLObject tLObject) {
        NotificationCenter.getInstance(this.profileActivity.getCurrentAccount()).doOnIdle(new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$loadFastScrollData$15(tL_error, i, i2, tLObject);
            }
        });
    }

    public void lambda$loadFastScrollData$17(final int i, final int i2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$loadFastScrollData$16(tL_error, i, i2, tLObject);
            }
        });
    }

    public void lambda$new$1() {
        hideFloatingDateView(true);
    }

    public void lambda$new$10(MediaPage mediaPage, View view, int i, float f, float f2) {
        MessageObject message;
        MessageObject messageObject;
        ChatActivity chatActivity;
        BaseFragment baseFragment;
        BaseFragment baseFragment2;
        BaseFragment topicsOrChat;
        long j;
        if (mediaPage.selectedType != 7) {
            if (mediaPage.selectedType != 6 || !(view instanceof ProfileSearchCell)) {
                if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
                    message = ((SharedDocumentCell) view).getMessage();
                } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
                    message = ((SharedLinkCell) view).getMessage();
                } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
                    message = ((SharedAudioCell) view).getMessage();
                } else if (mediaPage.selectedType == 5 && (view instanceof ContextLinkCell)) {
                    message = (MessageObject) ((ContextLinkCell) view).getParentObject();
                } else {
                    if (mediaPage.selectedType == 0 && (view instanceof SharedPhotoVideoCell2)) {
                        SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                        if (sharedPhotoVideoCell2.canRevealSpoiler()) {
                            sharedPhotoVideoCell2.startRevealMedia(f, f2);
                            return;
                        } else {
                            messageObject = sharedPhotoVideoCell2.getMessageObject();
                            if (messageObject == null) {
                                return;
                            }
                        }
                    } else if ((mediaPage.selectedType == 8 || mediaPage.selectedType == 9) && (view instanceof SharedPhotoVideoCell2)) {
                        messageObject = ((SharedPhotoVideoCell2) view).getMessageObject();
                        if (messageObject == null) {
                            return;
                        }
                    } else {
                        if (mediaPage.selectedType == 10) {
                            if (((view instanceof ProfileSearchCell) || f2 < AndroidUtilities.dp(60.0f)) && i >= 0 && i < this.channelRecommendationsAdapter.chats.size()) {
                                Bundle bundle = new Bundle();
                                bundle.putLong("chat_id", ((TLRPC.Chat) this.channelRecommendationsAdapter.chats.get(i)).id);
                                this.profileActivity.presentFragment(new ChatActivity(bundle));
                                return;
                            }
                            return;
                        }
                        if (mediaPage.selectedType != 11) {
                            return;
                        }
                        RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
                        SavedMessagesSearchAdapter savedMessagesSearchAdapter = this.savedMessagesSearchAdapter;
                        if (adapter == savedMessagesSearchAdapter) {
                            if (i < 0) {
                                return;
                            }
                            if (i < savedMessagesSearchAdapter.dialogs.size()) {
                                SavedMessagesController.SavedDialog savedDialog = (SavedMessagesController.SavedDialog) this.savedMessagesSearchAdapter.dialogs.get(i);
                                Bundle bundle2 = new Bundle();
                                bundle2.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
                                bundle2.putInt("chatMode", 3);
                                ChatActivity chatActivity2 = new ChatActivity(bundle2);
                                chatActivity2.setSavedDialog(savedDialog.dialogId);
                                this.profileActivity.presentFragment(chatActivity2);
                                return;
                            }
                            final int size = i - this.savedMessagesSearchAdapter.dialogs.size();
                            if (size < this.savedMessagesSearchAdapter.messages.size()) {
                                MessageObject messageObject2 = (MessageObject) this.savedMessagesSearchAdapter.messages.get(size);
                                Bundle bundle3 = new Bundle();
                                bundle3.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
                                bundle3.putInt("message_id", messageObject2.getId());
                                ChatActivity chatActivity3 = new ChatActivity(bundle3) {
                                    boolean firstCreateView = true;

                                    @Override
                                    public void onTransitionAnimationStart(boolean z, boolean z2) {
                                        if (this.firstCreateView) {
                                            if (this.searchItem != null) {
                                                lambda$openSearchWithText$322("");
                                                this.searchItem.setSearchFieldText(SharedMediaLayout.this.savedMessagesSearchAdapter.lastQuery, false);
                                            }
                                            SearchTagsList searchTagsList = this.actionBarSearchTags;
                                            if (searchTagsList != null) {
                                                searchTagsList.setChosen(SharedMediaLayout.this.savedMessagesSearchAdapter.lastReaction, false);
                                            }
                                            SharedMediaLayout.this.profileActivity.getMediaDataController().portSavedSearchResults(getClassGuid(), SharedMediaLayout.this.savedMessagesSearchAdapter.lastReaction, SharedMediaLayout.this.savedMessagesSearchAdapter.lastQuery, SharedMediaLayout.this.savedMessagesSearchAdapter.cachedMessages, SharedMediaLayout.this.savedMessagesSearchAdapter.loadedMessages, size, SharedMediaLayout.this.savedMessagesSearchAdapter.count, SharedMediaLayout.this.savedMessagesSearchAdapter.endReached);
                                            this.firstCreateView = false;
                                        }
                                        super.onTransitionAnimationStart(z, z2);
                                    }
                                };
                                chatActivity3.setHighlightMessageId(messageObject2.getId());
                                this.profileActivity.presentFragment(chatActivity3);
                                return;
                            }
                            return;
                        }
                        if (this.isActionModeShowed) {
                            if (this.savedDialogsAdapter.itemTouchHelper.isIdle()) {
                                this.savedDialogsAdapter.select(view);
                                return;
                            }
                            return;
                        }
                        Bundle bundle4 = new Bundle();
                        if (i < 0 || i >= this.savedDialogsAdapter.dialogs.size()) {
                            return;
                        }
                        SavedMessagesController.SavedDialog savedDialog2 = (SavedMessagesController.SavedDialog) this.savedDialogsAdapter.dialogs.get(i);
                        bundle4.putLong("user_id", this.profileActivity.getUserConfig().getClientUserId());
                        bundle4.putInt("chatMode", 3);
                        chatActivity = new ChatActivity(bundle4);
                        chatActivity.setSavedDialog(savedDialog2.dialogId);
                        baseFragment = this.profileActivity;
                    }
                    message = messageObject;
                }
                onItemClick(i, view, message, 0, mediaPage.selectedType);
                return;
            }
            TLRPC.Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle bundle5 = new Bundle();
            bundle5.putLong("chat_id", chat.id);
            if (!this.profileActivity.getMessagesController().checkCanOpenChat(bundle5, this.profileActivity)) {
                return;
            }
            if (chat.forum) {
                baseFragment2 = this.profileActivity;
                topicsOrChat = TopicsFragment.getTopicsOrChat(baseFragment2, bundle5);
            } else {
                baseFragment = this.profileActivity;
                chatActivity = new ChatActivity(bundle5);
            }
            baseFragment.presentFragment(chatActivity);
            return;
        }
        if (view instanceof UserCell) {
            if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                i = ((Integer) this.chatUsersAdapter.sortedUsers.get(i)).intValue();
            }
            TLRPC.ChatParticipant chatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(i);
            if (i < 0 || i >= this.chatUsersAdapter.chatInfo.participants.participants.size()) {
                return;
            }
            onMemberClick(chatParticipant, false, view);
            return;
        }
        RecyclerView.Adapter adapter2 = mediaPage.listView.getAdapter();
        GroupUsersSearchAdapter groupUsersSearchAdapter = this.groupUsersSearchAdapter;
        if (adapter2 != groupUsersSearchAdapter) {
            return;
        }
        TLObject item = groupUsersSearchAdapter.getItem(i);
        if (item instanceof TLRPC.ChannelParticipant) {
            j = MessageObject.getPeerId(((TLRPC.ChannelParticipant) item).peer);
        } else if (!(item instanceof TLRPC.ChatParticipant)) {
            return;
        } else {
            j = ((TLRPC.ChatParticipant) item).user_id;
        }
        if (j == 0 || j == this.profileActivity.getUserConfig().getClientUserId()) {
            return;
        }
        Bundle bundle6 = new Bundle();
        bundle6.putLong("user_id", j);
        baseFragment2 = this.profileActivity;
        topicsOrChat = new ProfileActivity(bundle6);
        baseFragment2.presentFragment(topicsOrChat);
    }

    public static boolean lambda$new$11(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$new$12(boolean z, boolean z2) {
        if (!z) {
            requestLayout();
        }
        setVisibleHeight(this.lastVisibleHeight);
    }

    public void lambda$new$3(View view) {
        closeActionMode();
    }

    public void lambda$new$4(View view) {
        onActionBarItemClick(view, 102);
    }

    public void lambda$new$5(View view) {
        onActionBarItemClick(view, 100);
    }

    public void lambda$new$6(View view) {
        onActionBarItemClick(view, 103);
    }

    public void lambda$new$7(View view) {
        onActionBarItemClick(view, 104);
    }

    public void lambda$new$8(View view) {
        onActionBarItemClick(view, 101);
    }

    public void lambda$new$9(View view) {
        if (view.getAlpha() < 0.5f) {
            return;
        }
        this.profileActivity.getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
        openStoryRecorder();
    }

    public void lambda$onActionBarItemClick$21(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            this.profileActivity.getMessagesController().deleteSavedDialog(((Long) arrayList.get(i2)).longValue());
        }
        closeActionMode();
    }

    public void lambda$onActionBarItemClick$22() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    public boolean lambda$onActionBarItemClick$23(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        String str;
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        int i2 = 1;
        while (true) {
            if (i2 < 0) {
                break;
            }
            ArrayList arrayList3 = new ArrayList();
            for (int i3 = 0; i3 < this.selectedFiles[i2].size(); i3++) {
                arrayList3.add(Integer.valueOf(this.selectedFiles[i2].keyAt(i3)));
            }
            Collections.sort(arrayList3);
            Iterator it = arrayList3.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList2.add((MessageObject) this.selectedFiles[i2].get(num.intValue()));
                }
            }
            this.selectedFiles[i2].clear();
            i2--;
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        SavedDialogsAdapter savedDialogsAdapter = this.savedDialogsAdapter;
        if (savedDialogsAdapter != null) {
            savedDialogsAdapter.unselectAll();
        }
        if (arrayList.size() > 1 || ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId == this.profileActivity.getUserConfig().getClientUserId() || charSequence != null) {
            updateRowsSelection(true);
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                long j = ((MessagesStorage.TopicKey) arrayList.get(i4)).dialogId;
                if (charSequence != null) {
                    this.profileActivity.getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence.toString(), j, null, null, null, true, null, null, null, true, 0, null, false));
                }
                this.profileActivity.getSendMessagesHelper().sendMessage(arrayList2, j, false, false, true, 0);
            }
            dialogsActivity.lambda$onBackPressed$319();
            BaseFragment baseFragment = this.profileActivity;
            UndoView undoView = baseFragment instanceof ProfileActivity ? ((ProfileActivity) baseFragment).getUndoView() : null;
            if (undoView != null) {
                if (arrayList.size() == 1) {
                    undoView.showWithAction(((MessagesStorage.TopicKey) arrayList.get(0)).dialogId, 53, Integer.valueOf(arrayList2.size()));
                } else {
                    undoView.showWithAction(0L, 53, Integer.valueOf(arrayList2.size()), Integer.valueOf(arrayList.size()), (Runnable) null, (Runnable) null);
                }
            }
        } else {
            long j2 = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j2)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j2));
            } else {
                if (DialogObject.isUserDialog(j2)) {
                    str = "user_id";
                } else {
                    j2 = -j2;
                    str = "chat_id";
                }
                bundle.putLong(str, j2);
                if (!this.profileActivity.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                    return true;
                }
            }
            this.profileActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity = new ChatActivity(bundle);
            ForumUtilities.applyTopic(chatActivity, (MessagesStorage.TopicKey) arrayList.get(0));
            dialogsActivity.presentFragment(chatActivity, true);
            chatActivity.showFieldPanelForForward(true, arrayList2);
        }
        return true;
    }

    public static void lambda$onItemClick$27(StoriesController.StoriesList storiesList, boolean z) {
        if (z) {
            storiesList.load(false, 30);
        }
    }

    public static void lambda$showFastScrollHint$18(MediaPage mediaPage, final SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip) {
        mediaPage.fastScrollHintView = null;
        mediaPage.fastScrollHideHintRunnable = null;
        sharedMediaFastScrollTooltip.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(220L).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (SharedMediaFastScrollTooltip.this.getParent() != null) {
                    ((ViewGroup) SharedMediaFastScrollTooltip.this.getParent()).removeView(SharedMediaFastScrollTooltip.this);
                }
            }
        }).start();
    }

    public static float lambda$static$2(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    public void lambda$stopScroll$24(ValueAnimator valueAnimator) {
        onTabProgress(getTabProgress());
    }

    public void lambda$switchToCurrentSelectedMode$25(View view) {
        this.profileActivity.getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
        StoryRecorder.getInstance(this.profileActivity.getParentActivity(), this.profileActivity.getCurrentAccount()).open(null);
    }

    public void lambda$switchToCurrentSelectedMode$26(View view) {
        this.profileActivity.getMessagesController().getMainSettings().edit().putBoolean("story_keep", true).apply();
        StoryRecorder.getInstance(this.profileActivity.getParentActivity(), this.profileActivity.getCurrentAccount()).open(null);
    }

    public void lambda$updateSearchItemIconAnimated$0(boolean z) {
        if (z) {
            return;
        }
        this.searchItemIcon.setVisibility(8);
    }

    private void loadFastScrollData(boolean z) {
        TLRPC.MessagesFilter tL_inputMessagesFilterDocument;
        if (this.topicId != 0 || isSearchingStories()) {
            return;
        }
        int i = 0;
        while (true) {
            int[] iArr = supportedFastScrollTypes;
            if (i >= iArr.length) {
                return;
            }
            final int i2 = iArr[i];
            if ((this.sharedMediaData[i2].fastScrollDataLoaded && !z) || DialogObject.isEncryptedDialog(this.dialog_id)) {
                return;
            }
            this.sharedMediaData[i2].fastScrollDataLoaded = false;
            TLRPC.TL_messages_getSearchResultsPositions tL_messages_getSearchResultsPositions = new TLRPC.TL_messages_getSearchResultsPositions();
            if (i2 == 0) {
                int i3 = this.sharedMediaData[i2].filterType;
                tL_inputMessagesFilterDocument = i3 == 1 ? new TLRPC.TL_inputMessagesFilterPhotos() : i3 == 2 ? new TLRPC.TL_inputMessagesFilterVideo() : new TLRPC.TL_inputMessagesFilterPhotoVideo();
            } else {
                tL_inputMessagesFilterDocument = i2 == 1 ? new TLRPC.TL_inputMessagesFilterDocument() : i2 == 2 ? new TLRPC.TL_inputMessagesFilterRoundVoice() : new TLRPC.TL_inputMessagesFilterMusic();
            }
            tL_messages_getSearchResultsPositions.filter = tL_inputMessagesFilterDocument;
            tL_messages_getSearchResultsPositions.limit = 100;
            tL_messages_getSearchResultsPositions.peer = this.profileActivity.getMessagesController().getInputPeer(this.dialog_id);
            if (this.topicId != 0 && this.profileActivity.getUserConfig().getClientUserId() == this.dialog_id) {
                tL_messages_getSearchResultsPositions.flags |= 4;
                tL_messages_getSearchResultsPositions.saved_peer_id = this.profileActivity.getMessagesController().getInputPeer(this.topicId);
            }
            final int i4 = this.sharedMediaData[i2].requestIndex;
            ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).bindRequestToGuid(ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).sendRequest(tL_messages_getSearchResultsPositions, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SharedMediaLayout.this.lambda$loadFastScrollData$17(i4, i2, tLObject, tL_error);
                }
            }), this.profileActivity.getClassGuid());
            i++;
        }
    }

    private void loadFromStart(int i) {
        int i2;
        if (i == 0) {
            int i3 = this.sharedMediaData[0].filterType;
            i2 = i3 == 1 ? 6 : i3 == 2 ? 7 : 0;
        } else {
            i2 = i == 1 ? 1 : i == 2 ? 2 : i == 4 ? 4 : i == 5 ? 5 : 3;
        }
        this.sharedMediaData[i].loading = true;
        this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, this.sharedMediaData[i].min_id, i2, this.topicId, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[i].requestIndex, null, null);
    }

    private void onItemClick(int r14, android.view.View r15, org.telegram.messenger.MessageObject r16, int r17, int r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.onItemClick(int, android.view.View, org.telegram.messenger.MessageObject, int, int):void");
    }

    public boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.isActionModeShowed || this.profileActivity.getParentActivity() == null || messageObject == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.profileActivity.getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? (char) 0 : (char) 1].put(messageObject.getId(), messageObject);
        if (!messageObject.canDeleteMessage(false, null)) {
            this.cantDeleteMessagesCount++;
        }
        int i2 = 8;
        this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        ActionBarMenuItem actionBarMenuItem = this.gotoItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility((getClosestTab() == 8 || getClosestTab() == 13 || getClosestTab() == 14) ? 8 : 0);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.forwardItem;
        if (actionBarMenuItem2 != null) {
            if (getClosestTab() != 8 && getClosestTab() != 13 && getClosestTab() != 14) {
                i2 = 0;
            }
            actionBarMenuItem2.setVisibility(i2);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        for (int i3 = 0; i3 < this.actionModeViews.size(); i3++) {
            View view2 = (View) this.actionModeViews.get(i3);
            AndroidUtilities.clearDrawableAnimation(view2);
            arrayList.add(ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(250L);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(true, true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell2) {
            ((SharedPhotoVideoCell2) view).setChecked(true, true);
        }
        if (!this.isActionModeShowed) {
            showActionMode(true);
        }
        onActionModeSelectedUpdate(this.selectedFiles[0]);
        updateForwardItem();
        return true;
    }

    public void openUrl(String str) {
        if (AndroidUtilities.shouldShowUrlInAlert(str)) {
            AlertsCreator.showOpenUrlAlert(this.profileActivity, str, true, true);
        } else {
            Browser.openUrl(this.profileActivity.getParentActivity(), str);
        }
    }

    public void openWebView(TLRPC.WebPage webPage, MessageObject messageObject) {
        EmbedBottomSheet.show(this.profileActivity, messageObject, this.provider, webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height, false);
    }

    private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
        MediaPage mediaPage;
        int i;
        MediaPage mediaPage2;
        BotPreviewsEditContainer botPreviewsEditContainer;
        int nextPageId = this.scrollSlidingTextTabStrip.getNextPageId(z);
        if (nextPageId < 0) {
            return false;
        }
        if (this.searchItem == null || canShowSearchItem()) {
            this.searchAlpha = getSearchAlpha(0.0f);
            updateSearchItemIcon(0.0f);
        } else {
            this.searchItem.setVisibility(isStoriesView() ? 8 : 4);
            this.searchAlpha = 0.0f;
        }
        if (this.searching && getSelectedTab() == 11) {
            return false;
        }
        if (canEditStories() && this.isActionModeShowed && getClosestTab() == 8) {
            return false;
        }
        MediaPage mediaPage3 = this.mediaPages[0];
        if (mediaPage3 != null && mediaPage3.selectedType == 13 && (botPreviewsEditContainer = this.botPreviewsContainer) != null && !botPreviewsEditContainer.canScroll(z)) {
            return false;
        }
        if (this.isActionModeShowed && (mediaPage2 = this.mediaPages[0]) != null && mediaPage2.selectedType == 13) {
            return false;
        }
        updateOptionsSearch();
        getParent().requestDisallowInterceptTouchEvent(true);
        hideFloatingDateView(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        onTabScroll(true);
        this.startedTrackingX = (int) motionEvent.getX();
        this.actionBar.setEnabled(false);
        this.scrollSlidingTextTabStrip.setEnabled(false);
        this.mediaPages[1].selectedType = nextPageId;
        this.mediaPages[1].setVisibility(0);
        this.animatingForward = z;
        switchToCurrentSelectedMode(true);
        MediaPage[] mediaPageArr = this.mediaPages;
        if (z) {
            mediaPage = mediaPageArr[1];
            i = mediaPageArr[0].getMeasuredWidth();
        } else {
            mediaPage = mediaPageArr[1];
            i = -mediaPageArr[0].getMeasuredWidth();
        }
        mediaPage.setTranslationX(i);
        onTabProgress(getTabProgress());
        return true;
    }

    private void recycleAdapter(RecyclerView.Adapter adapter) {
        ArrayList arrayList;
        if (adapter instanceof SharedPhotoVideoAdapter) {
            this.cellCache.addAll(this.cache);
            arrayList = this.cache;
        } else {
            if (adapter != this.audioAdapter) {
                return;
            }
            this.audioCellCache.addAll(this.audioCache);
            arrayList = this.audioCache;
        }
        arrayList.clear();
    }

    public void saveScrollPosition() {
        int i;
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                return;
            }
            InternalListView internalListView = mediaPageArr[i2].listView;
            if (internalListView != null) {
                int i3 = 0;
                int i4 = 0;
                for (int i5 = 0; i5 < internalListView.getChildCount(); i5++) {
                    View childAt = internalListView.getChildAt(i5);
                    if (childAt instanceof SharedPhotoVideoCell2) {
                        SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) childAt;
                        int messageId = sharedPhotoVideoCell2.getMessageId();
                        i4 = sharedPhotoVideoCell2.getTop();
                        i3 = messageId;
                    }
                    if (childAt instanceof SharedDocumentCell) {
                        SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                        int id = sharedDocumentCell.getMessage().getId();
                        i4 = sharedDocumentCell.getTop();
                        i3 = id;
                    }
                    if (childAt instanceof SharedAudioCell) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) childAt;
                        i3 = sharedAudioCell.getMessage().getId();
                        i4 = sharedAudioCell.getTop();
                    }
                    if (i3 != 0) {
                        break;
                    }
                }
                if (i3 != 0) {
                    int i6 = this.mediaPages[i2].selectedType;
                    int i7 = -1;
                    if (i6 == 8 || i6 == 9) {
                        StoriesAdapter storiesAdapter = i6 == 8 ? this.storiesAdapter : this.archivedStoriesAdapter;
                        if (storiesAdapter.storiesList != null) {
                            int i8 = 0;
                            while (true) {
                                if (i8 >= storiesAdapter.storiesList.messageObjects.size()) {
                                    break;
                                }
                                if (i3 == ((MessageObject) storiesAdapter.storiesList.messageObjects.get(i8)).getId()) {
                                    i7 = i8;
                                    break;
                                }
                                i8++;
                            }
                        }
                        i = i7;
                    } else if (i6 >= 0 && i6 < this.sharedMediaData.length) {
                        int i9 = 0;
                        while (true) {
                            if (i9 >= this.sharedMediaData[i6].messages.size()) {
                                break;
                            }
                            if (i3 == ((MessageObject) this.sharedMediaData[i6].messages.get(i9)).getId()) {
                                i7 = i9;
                                break;
                            }
                            i9++;
                        }
                        i = this.sharedMediaData[i6].startOffset + i7;
                    }
                    if (i7 >= 0) {
                        ((LinearLayoutManager) internalListView.getLayoutManager()).scrollToPositionWithOffset(i, (-this.mediaPages[i2].listView.getPaddingTop()) + i4);
                        if (this.photoVideoChangeColumnsAnimation) {
                            this.mediaPages[i2].animationSupportingLayoutManager.scrollToPositionWithOffset(i, (-this.mediaPages[i2].listView.getPaddingTop()) + i4);
                        }
                    }
                }
            }
            i2++;
        }
    }

    public void scrollToTop() {
        int itemSize;
        float f;
        int i = this.mediaPages[0].selectedType;
        if (i != 0) {
            if (i != 1 && i != 2) {
                if (i == 3) {
                    f = 100.0f;
                } else if (i != 4) {
                    f = i != 5 ? 58.0f : 60.0f;
                }
                itemSize = AndroidUtilities.dp(f);
            }
            f = 56.0f;
            itemSize = AndroidUtilities.dp(f);
        } else {
            itemSize = SharedPhotoVideoCell.getItemSize(1);
        }
        if ((this.mediaPages[0].selectedType == 0 ? this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() / this.mediaColumnsCount[0] : this.mediaPages[0].layoutManager.findFirstVisibleItemPosition()) * itemSize < this.mediaPages[0].listView.getMeasuredHeight() * 1.2f) {
            this.mediaPages[0].listView.smoothScrollToPosition(0);
        } else {
            this.mediaPages[0].scrollHelper.setScrollDirection(1);
            this.mediaPages[0].scrollHelper.scrollToPosition(0, 0, false, true);
        }
    }

    private void selectPinchPosition(int i, int i2) {
        this.pinchCenterPosition = -1;
        int i3 = i2 + this.mediaPages[0].listView.blurTopPadding;
        if (getY() != 0.0f && this.viewType == 1) {
            i3 = 0;
        }
        for (int i4 = 0; i4 < this.mediaPages[0].listView.getChildCount(); i4++) {
            View childAt = this.mediaPages[0].listView.getChildAt(i4);
            childAt.getHitRect(this.rect);
            if (this.rect.contains(i, i3)) {
                this.pinchCenterPosition = this.mediaPages[0].listView.getChildLayoutPosition(childAt);
                this.pinchCenterOffset = childAt.getTop();
            }
        }
        if (this.delegate.canSearchMembers() && this.pinchCenterPosition == -1) {
            this.pinchCenterPosition = (int) (this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() + ((this.mediaColumnsCount[(this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9) ? (char) 1 : (char) 0] - 1) * Math.min(1.0f, Math.max(i / this.mediaPages[0].listView.getMeasuredWidth(), 0.0f))));
            this.pinchCenterOffset = 0;
        }
    }

    public static void showFastScrollHint(final MediaPage mediaPage, SharedMediaData[] sharedMediaDataArr, boolean z) {
        Runnable runnable;
        if (!z) {
            if (mediaPage.fastScrollHintView == null || (runnable = mediaPage.fastScrollHideHintRunnable) == null) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(runnable);
            mediaPage.fastScrollHideHintRunnable.run();
            mediaPage.fastScrollHideHintRunnable = null;
            mediaPage.fastScrollHintView = null;
            return;
        }
        if (SharedConfig.fastScrollHintCount <= 0 || mediaPage.fastScrollHintView != null || mediaPage.fastScrollHinWasShown || mediaPage.listView.getFastScroll() == null || !mediaPage.listView.getFastScroll().isVisible || mediaPage.listView.getFastScroll().getVisibility() != 0 || sharedMediaDataArr[0].totalCount < 50) {
            return;
        }
        SharedConfig.setFastScrollHintCount(SharedConfig.fastScrollHintCount - 1);
        mediaPage.fastScrollHinWasShown = true;
        final SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip = new SharedMediaFastScrollTooltip(mediaPage.getContext());
        mediaPage.fastScrollHintView = sharedMediaFastScrollTooltip;
        mediaPage.addView(sharedMediaFastScrollTooltip, LayoutHelper.createFrame(-2, -2.0f));
        mediaPage.fastScrollHintView.setAlpha(0.0f);
        mediaPage.fastScrollHintView.setScaleX(0.8f);
        mediaPage.fastScrollHintView.setScaleY(0.8f);
        mediaPage.fastScrollHintView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
        mediaPage.invalidate();
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.lambda$showFastScrollHint$18(SharedMediaLayout.MediaPage.this, sharedMediaFastScrollTooltip);
            }
        };
        mediaPage.fastScrollHideHintRunnable = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 4000L);
    }

    public void showFloatingDateView() {
    }

    private void startPinchToMediaColumnsCount(boolean z) {
        int i;
        final MediaPage mediaPage;
        InternalListView internalListView;
        RecyclerView.Adapter adapter;
        int i2;
        if (this.photoVideoChangeColumnsAnimation) {
            return;
        }
        int i3 = 0;
        int i4 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            i = 8;
            if (i4 >= mediaPageArr.length) {
                mediaPage = null;
                break;
            } else if (mediaPageArr[i4].selectedType == 0 || this.mediaPages[i4].selectedType == 8 || this.mediaPages[i4].selectedType == 9) {
                break;
            } else {
                i4++;
            }
        }
        if (mediaPage == null) {
            return;
        }
        int i5 = mediaPage.selectedType;
        this.changeColumnsTab = i5;
        int i6 = (i5 == 8 || i5 == 9) ? 1 : 0;
        int nextMediaColumnsCount = getNextMediaColumnsCount(i6, this.mediaColumnsCount[i6], z);
        this.animateToColumnsCount = nextMediaColumnsCount;
        if (nextMediaColumnsCount == this.mediaColumnsCount[i6]) {
            return;
        }
        if (this.allowStoriesSingleColumn && ((i2 = this.changeColumnsTab) == 8 || i2 == 9)) {
            return;
        }
        mediaPage.animationSupportingListView.setVisibility(0);
        int i7 = this.changeColumnsTab;
        if (i7 == 8) {
            internalListView = mediaPage.animationSupportingListView;
            adapter = this.animationSupportingStoriesAdapter;
        } else if (i7 == 9) {
            internalListView = mediaPage.animationSupportingListView;
            adapter = this.animationSupportingArchivedStoriesAdapter;
        } else {
            internalListView = mediaPage.animationSupportingListView;
            adapter = this.animationSupportingPhotoVideoAdapter;
        }
        internalListView.setAdapter(adapter);
        mediaPage.animationSupportingListView.setPadding(mediaPage.animationSupportingListView.getPaddingLeft(), this.changeColumnsTab == 9 ? AndroidUtilities.dp(64.0f) : 0, mediaPage.animationSupportingListView.getPaddingRight(), isStoriesView() ? AndroidUtilities.dp(72.0f) : 0);
        ButtonWithCounterView buttonWithCounterView = mediaPage.buttonView;
        if (this.changeColumnsTab == 8 && isStoriesView()) {
            i = 0;
        }
        buttonWithCounterView.setVisibility(i);
        mediaPage.animationSupportingLayoutManager.setSpanCount(nextMediaColumnsCount);
        mediaPage.animationSupportingListView.invalidateItemDecorations();
        mediaPage.animationSupportingLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i8) {
                if (mediaPage.animationSupportingListView.getAdapter() == SharedMediaLayout.this.animationSupportingPhotoVideoAdapter) {
                    if (SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.getItemViewType(i8) != 2) {
                        return 1;
                    }
                } else if (mediaPage.animationSupportingListView.getAdapter() == SharedMediaLayout.this.animationSupportingStoriesAdapter) {
                    if (SharedMediaLayout.this.animationSupportingStoriesAdapter.getItemViewType(i8) != 2) {
                        return 1;
                    }
                } else if (mediaPage.animationSupportingListView.getAdapter() != SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter || SharedMediaLayout.this.animationSupportingArchivedStoriesAdapter.getItemViewType(i8) != 2) {
                    return 1;
                }
                return mediaPage.animationSupportingLayoutManager.getSpanCount();
            }
        });
        AndroidUtilities.updateVisibleRows(mediaPage.listView);
        this.photoVideoChangeColumnsAnimation = true;
        if (this.changeColumnsTab == 0) {
            this.sharedMediaData[0].setListFrozen(true);
        }
        this.photoVideoChangeColumnsProgress = 0.0f;
        if (this.pinchCenterPosition < 0) {
            saveScrollPosition();
            return;
        }
        while (true) {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            if (i3 >= mediaPageArr2.length) {
                return;
            }
            if (mediaPageArr2[i3].selectedType == this.changeColumnsTab) {
                this.mediaPages[i3].animationSupportingLayoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset - this.mediaPages[i3].animationSupportingListView.getPaddingTop());
            }
            i3++;
        }
    }

    public void startStopVisibleGifs() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            int childCount = mediaPageArr[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof ContextLinkCell) {
                    ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                    if (i == 0) {
                        photoImage.setAllowStartAnimation(true);
                        photoImage.startAnimation();
                    } else {
                        photoImage.setAllowStartAnimation(false);
                        photoImage.stopAnimation();
                    }
                }
            }
            i++;
        }
    }

    private void stopScroll(MotionEvent motionEvent) {
        float f;
        float f2;
        float measuredWidth;
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            return;
        }
        velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
        if (motionEvent == null || motionEvent.getAction() == 3) {
            f = 0.0f;
            f2 = 0.0f;
        } else {
            f = this.velocityTracker.getXVelocity();
            f2 = this.velocityTracker.getYVelocity();
            if (!this.startedTracking && Math.abs(f) >= 3000.0f && Math.abs(f) > Math.abs(f2)) {
                prepareForMoving(motionEvent, f < 0.0f);
            }
        }
        if (this.startedTracking) {
            float x = this.mediaPages[0].getX();
            this.tabsAnimation = new AnimatorSet();
            this.backAnimation = Math.abs(x) < ((float) this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f) < 3500.0f || Math.abs(f) < Math.abs(f2));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.lambda$stopScroll$24(valueAnimator);
                }
            });
            if (this.backAnimation) {
                measuredWidth = Math.abs(x);
                if (this.animatingForward) {
                    AnimatorSet animatorSet = this.tabsAnimation;
                    MediaPage mediaPage = this.mediaPages[0];
                    Property property = View.TRANSLATION_X;
                    animatorSet.playTogether(ObjectAnimator.ofFloat(mediaPage, (Property<MediaPage, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property, r11.getMeasuredWidth()), ofFloat);
                } else {
                    AnimatorSet animatorSet2 = this.tabsAnimation;
                    MediaPage mediaPage2 = this.mediaPages[0];
                    Property property2 = View.TRANSLATION_X;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(mediaPage2, (Property<MediaPage, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property2, -r11.getMeasuredWidth()), ofFloat);
                }
            } else {
                measuredWidth = this.mediaPages[0].getMeasuredWidth() - Math.abs(x);
                if (this.animatingForward) {
                    AnimatorSet animatorSet3 = this.tabsAnimation;
                    MediaPage mediaPage3 = this.mediaPages[0];
                    Property property3 = View.TRANSLATION_X;
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(mediaPage3, (Property<MediaPage, Float>) property3, -mediaPage3.getMeasuredWidth()), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property3, 0.0f), ofFloat);
                } else {
                    AnimatorSet animatorSet4 = this.tabsAnimation;
                    MediaPage mediaPage4 = this.mediaPages[0];
                    Property property4 = View.TRANSLATION_X;
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(mediaPage4, (Property<MediaPage, Float>) property4, mediaPage4.getMeasuredWidth()), ObjectAnimator.ofFloat(this.mediaPages[1], (Property<MediaPage, Float>) property4, 0.0f), ofFloat);
                }
            }
            this.tabsAnimation.setInterpolator(interpolator);
            int measuredWidth2 = getMeasuredWidth();
            float f3 = measuredWidth2 / 2;
            float distanceInfluenceForSnapDuration = f3 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (measuredWidth * 1.0f) / measuredWidth2)) * f3);
            this.tabsAnimation.setDuration(Math.max(150, Math.min(Math.abs(f) > 0.0f ? Math.round(Math.abs(distanceInfluenceForSnapDuration / r0) * 1000.0f) * 4 : (int) (((measuredWidth / getMeasuredWidth()) + 1.0f) * 100.0f), 600)));
            this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    SharedMediaLayout.this.tabsAnimation = null;
                    if (SharedMediaLayout.this.backAnimation) {
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItem == null || SharedMediaLayout.this.canShowSearchItem()) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.searchAlpha = sharedMediaLayout.getSearchAlpha(0.0f);
                            SharedMediaLayout.this.updateSearchItemIcon(0.0f);
                        } else {
                            SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                            SharedMediaLayout.this.searchAlpha = 0.0f;
                        }
                        SharedMediaLayout.this.updateOptionsSearch();
                        SharedMediaLayout.this.searchItemState = 0;
                    } else {
                        MediaPage mediaPage5 = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = mediaPage5;
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItem != null && SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setVisibility(SharedMediaLayout.this.isStoriesView() ? 8 : 4);
                        }
                        SharedMediaLayout.this.searchItemState = 0;
                        SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                        sharedMediaLayout2.scrollSlidingTextTabStrip.selectTabWithId(sharedMediaLayout2.mediaPages[0].selectedType, 1.0f);
                        SharedMediaLayout.this.onSelectedTabChanged();
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                    SharedMediaLayout.this.tabsAnimationInProgress = false;
                    SharedMediaLayout.this.maybeStartTracking = false;
                    SharedMediaLayout.this.startedTracking = false;
                    SharedMediaLayout.this.onTabScroll(false);
                    SharedMediaLayout.this.actionBar.setEnabled(true);
                    SharedMediaLayout.this.scrollSlidingTextTabStrip.setEnabled(true);
                }
            });
            this.tabsAnimation.start();
            this.tabsAnimationInProgress = true;
            this.startedTracking = false;
            onSelectedTabChanged();
        } else {
            this.maybeStartTracking = false;
            this.actionBar.setEnabled(true);
            this.scrollSlidingTextTabStrip.setEnabled(true);
        }
        VelocityTracker velocityTracker2 = this.velocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.recycle();
            this.velocityTracker = null;
        }
    }

    public void switchToCurrentSelectedMode(boolean r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.switchToCurrentSelectedMode(boolean):void");
    }

    private void updateForwardItem() {
        ActionBarMenuItem actionBarMenuItem;
        Drawable createSelectorDrawable;
        if (this.forwardItem == null) {
            return;
        }
        boolean z = this.profileActivity.getMessagesController().isChatNoForwards(-this.dialog_id) || hasNoforwardsMessage();
        this.forwardItem.setAlpha(z ? 0.5f : 1.0f);
        if (z && this.forwardItem.getBackground() != null) {
            actionBarMenuItem = this.forwardItem;
            createSelectorDrawable = null;
        } else {
            if (z || this.forwardItem.getBackground() != null) {
                return;
            }
            actionBarMenuItem = this.forwardItem;
            createSelectorDrawable = Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), 5);
        }
        actionBarMenuItem.setBackground(createSelectorDrawable);
    }

    public void updateOptionsSearch() {
        updateOptionsSearch(false);
    }

    public void updateOptionsSearch(boolean z) {
        RLottieImageView rLottieImageView = this.optionsSearchImageView;
        if (rLottieImageView == null) {
            return;
        }
        rLottieImageView.setAlpha(this.searching ? 0.0f : Utilities.clamp(this.searchAlpha + this.optionsAlpha, 1.0f, 0.0f));
        animateSearchToOptions(!z ? this.searchItemState != 2 ? this.searchAlpha >= 0.1f : this.optionsAlpha <= 0.1f : getPhotoVideoOptionsAlpha(1.0f) <= 0.5f, true);
    }

    private void updateRowsSelection(boolean z) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            int childCount = mediaPageArr[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) childAt).setChecked(false, z);
                } else if (childAt instanceof SharedPhotoVideoCell2) {
                    ((SharedPhotoVideoCell2) childAt).setChecked(false, z);
                } else if (childAt instanceof SharedLinkCell) {
                    ((SharedLinkCell) childAt).setChecked(false, z);
                } else if (childAt instanceof SharedAudioCell) {
                    ((SharedAudioCell) childAt).setChecked(false, z);
                } else if (childAt instanceof ContextLinkCell) {
                    ((ContextLinkCell) childAt).setChecked(false, z);
                } else if (childAt instanceof DialogCell) {
                    ((DialogCell) childAt).setChecked(false, z);
                }
            }
            i++;
        }
    }

    private void updateStoriesPinButton() {
        boolean z;
        StoriesController.StoriesList storiesList;
        ActionBarMenuItem actionBarMenuItem;
        if (isBot()) {
            ActionBarMenuItem actionBarMenuItem2 = this.pinItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setVisibility(8);
            }
            actionBarMenuItem = this.unpinItem;
            if (actionBarMenuItem == null) {
                return;
            }
        } else {
            if (getClosestTab() != 9) {
                if (getClosestTab() == 8) {
                    int i = 0;
                    while (true) {
                        if (i >= this.selectedFiles[0].size()) {
                            z = false;
                            break;
                        }
                        MessageObject messageObject = (MessageObject) this.selectedFiles[0].valueAt(i);
                        StoriesAdapter storiesAdapter = this.storiesAdapter;
                        if (storiesAdapter != null && (storiesList = storiesAdapter.storiesList) != null && !storiesList.isPinned(messageObject.getId())) {
                            z = true;
                            break;
                        }
                        i++;
                    }
                    ActionBarMenuItem actionBarMenuItem3 = this.pinItem;
                    if (actionBarMenuItem3 != null) {
                        actionBarMenuItem3.setVisibility(z ? 0 : 8);
                    }
                    ActionBarMenuItem actionBarMenuItem4 = this.unpinItem;
                    if (actionBarMenuItem4 != null) {
                        actionBarMenuItem4.setVisibility(z ? 8 : 0);
                        return;
                    }
                    return;
                }
                return;
            }
            ActionBarMenuItem actionBarMenuItem5 = this.pinItem;
            if (actionBarMenuItem5 != null) {
                actionBarMenuItem5.setVisibility(8);
            }
            actionBarMenuItem = this.unpinItem;
            if (actionBarMenuItem == null) {
                return;
            }
        }
        actionBarMenuItem.setVisibility(8);
    }

    public void updateTabs(boolean r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.updateTabs(boolean):void");
    }

    public boolean addActionButtons() {
        return true;
    }

    public void animateSearchToOptions(boolean z, boolean z2) {
        RLottieDrawable animatedDrawable;
        int i;
        RLottieImageView rLottieImageView = this.optionsSearchImageView;
        if (rLottieImageView == null || this.animatingToOptions == z) {
            return;
        }
        this.animatingToOptions = z;
        if (z || rLottieImageView.getAnimatedDrawable().getCurrentFrame() >= 20) {
            animatedDrawable = this.optionsSearchImageView.getAnimatedDrawable();
            i = this.animatingToOptions ? 50 : 100;
        } else {
            animatedDrawable = this.optionsSearchImageView.getAnimatedDrawable();
            i = 0;
        }
        animatedDrawable.setCustomEndFrame(i);
        RLottieDrawable animatedDrawable2 = this.optionsSearchImageView.getAnimatedDrawable();
        if (z2) {
            animatedDrawable2.start();
        } else {
            animatedDrawable2.setCurrentFrame(this.optionsSearchImageView.getAnimatedDrawable().getCustomEndFrame());
        }
    }

    @Override
    public boolean canClickButtonInside() {
        return false;
    }

    public boolean canEditStories() {
        BaseFragment baseFragment;
        if (!isBot()) {
            return isStoriesView() || ((baseFragment = this.profileActivity) != null && baseFragment.getMessagesController().getStoriesController().canEditStories(this.dialog_id));
        }
        TLRPC.User user = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(this.dialog_id));
        return user != null && user.bot && user.bot_can_edit;
    }

    protected boolean canShowSearchItem() {
        return true;
    }

    public boolean canZoomIn() {
        MediaPage mediaPage;
        MediaPage[] mediaPageArr = this.mediaPages;
        if (mediaPageArr == null || (mediaPage = mediaPageArr[0]) == null) {
            return false;
        }
        int i = (mediaPage.selectedType == 8 || this.mediaPages[0].selectedType == 9) ? 1 : 0;
        int i2 = this.mediaColumnsCount[i];
        return i2 != getNextMediaColumnsCount(i, i2, true);
    }

    public boolean canZoomOut() {
        MediaPage mediaPage;
        MediaPage[] mediaPageArr = this.mediaPages;
        if (mediaPageArr == null || (mediaPage = mediaPageArr[0]) == null) {
            return false;
        }
        if (this.allowStoriesSingleColumn && (mediaPage.selectedType == 8 || this.mediaPages[0].selectedType == 9)) {
            return false;
        }
        int i = (this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9) ? 1 : 0;
        int i2 = this.mediaColumnsCount[i];
        return i2 != getNextMediaColumnsCount(i, i2, false);
    }

    public boolean checkPinchToZoom(MotionEvent motionEvent) {
        BotPreviewsEditContainer botPreviewsEditContainer;
        int i = this.mediaPages[0].selectedType;
        if (i == 13 && (botPreviewsEditContainer = this.botPreviewsContainer) != null) {
            return botPreviewsEditContainer.checkPinchToZoom(motionEvent);
        }
        if ((i != 0 && i != 8 && i != 9) || getParent() == null) {
            return false;
        }
        if (this.photoVideoChangeColumnsAnimation && !this.isInPinchToZoomTouchMode) {
            return true;
        }
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && motionEvent.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot(motionEvent.getX(1) - motionEvent.getX(0), motionEvent.getY(1) - motionEvent.getY(0));
                this.pinchScale = 1.0f;
                this.pointerId1 = motionEvent.getPointerId(0);
                this.pointerId2 = motionEvent.getPointerId(1);
                this.mediaPages[0].listView.cancelClickRunnables(false);
                this.mediaPages[0].listView.cancelLongPress();
                this.mediaPages[0].listView.dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                View view = (View) getParent();
                this.pinchCenterX = (int) (((((int) ((motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f)) - view.getX()) - getX()) - this.mediaPages[0].getX());
                int y = (int) (((((int) ((motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f)) - view.getY()) - getY()) - this.mediaPages[0].getY());
                this.pinchCenterY = y;
                selectPinchPosition(this.pinchCenterX, y);
                this.maybePinchToZoomTouchMode2 = true;
            }
            if (motionEvent.getActionMasked() == 0) {
                if (((motionEvent.getY() - ((View) getParent()).getY()) - getY()) - this.mediaPages[0].getY() > 0.0f) {
                    this.maybePinchToZoomTouchMode = true;
                }
            }
        } else if (motionEvent.getActionMasked() == 2 && (this.isInPinchToZoomTouchMode || this.maybePinchToZoomTouchMode2)) {
            int i2 = -1;
            int i3 = -1;
            for (int i4 = 0; i4 < motionEvent.getPointerCount(); i4++) {
                if (this.pointerId1 == motionEvent.getPointerId(i4)) {
                    i2 = i4;
                }
                if (this.pointerId2 == motionEvent.getPointerId(i4)) {
                    i3 = i4;
                }
            }
            if (i2 == -1 || i3 == -1) {
                this.maybePinchToZoomTouchMode = false;
                this.maybePinchToZoomTouchMode2 = false;
                this.isInPinchToZoomTouchMode = false;
                finishPinchToMediaColumnsCount();
                return false;
            }
            float hypot = ((float) Math.hypot(motionEvent.getX(i3) - motionEvent.getX(i2), motionEvent.getY(i3) - motionEvent.getY(i2))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (!this.isInPinchToZoomTouchMode && (hypot > 1.01f || hypot < 0.99f)) {
                this.isInPinchToZoomTouchMode = true;
                boolean z = hypot > 1.0f;
                this.pinchScaleUp = z;
                startPinchToMediaColumnsCount(z);
            }
            if (this.isInPinchToZoomTouchMode) {
                boolean z2 = this.pinchScaleUp;
                if ((!z2 || this.pinchScale >= 1.0f) && (z2 || this.pinchScale <= 1.0f)) {
                    this.photoVideoChangeColumnsProgress = Math.max(0.0f, Math.min(1.0f, z2 ? 1.0f - ((2.0f - this.pinchScale) / 1.0f) : (1.0f - this.pinchScale) / 0.5f));
                } else {
                    this.photoVideoChangeColumnsProgress = 0.0f;
                }
                float f = this.photoVideoChangeColumnsProgress;
                if (f == 1.0f || f == 0.0f) {
                    int i5 = this.changeColumnsTab;
                    RecyclerView.Adapter adapter = i5 == 8 ? this.storiesAdapter : i5 == 9 ? this.archivedStoriesAdapter : this.photoVideoAdapter;
                    if (f == 1.0f) {
                        int i6 = this.animateToColumnsCount;
                        int ceil = (((int) Math.ceil(this.pinchCenterPosition / this.animateToColumnsCount)) * i6) + ((int) ((this.startedTrackingX / (this.mediaPages[0].listView.getMeasuredWidth() - ((int) (this.mediaPages[0].listView.getMeasuredWidth() / this.animateToColumnsCount)))) * (i6 - 1)));
                        if (ceil >= adapter.getItemCount()) {
                            ceil = adapter.getItemCount() - 1;
                        }
                        this.pinchCenterPosition = ceil;
                    }
                    finishPinchToMediaColumnsCount();
                    if (this.photoVideoChangeColumnsProgress == 0.0f) {
                        this.pinchScaleUp = !this.pinchScaleUp;
                    }
                    startPinchToMediaColumnsCount(this.pinchScaleUp);
                    this.pinchStartDistance = (float) Math.hypot(motionEvent.getX(1) - motionEvent.getX(0), motionEvent.getY(1) - motionEvent.getY(0));
                }
                this.mediaPages[0].listView.invalidate();
                MediaPage mediaPage = this.mediaPages[0];
                if (mediaPage.fastScrollHintView != null) {
                    mediaPage.invalidate();
                }
            }
        } else if ((motionEvent.getActionMasked() == 1 || ((motionEvent.getActionMasked() == 6 && checkPointerIds(motionEvent)) || motionEvent.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.maybePinchToZoomTouchMode2 = false;
            this.maybePinchToZoomTouchMode = false;
            this.isInPinchToZoomTouchMode = false;
            finishPinchToMediaColumnsCount();
        }
        return this.isInPinchToZoomTouchMode;
    }

    public boolean checkTabsAnimationInProgress() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.checkTabsAnimationInProgress():boolean");
    }

    public boolean closeActionMode() {
        return closeActionMode(true);
    }

    public boolean closeActionMode(boolean z) {
        if (!this.isActionModeShowed) {
            return false;
        }
        for (int i = 1; i >= 0; i--) {
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = 0;
        onActionModeSelectedUpdate(this.selectedFiles[0]);
        BotPreviewsEditContainer botPreviewsEditContainer = this.botPreviewsContainer;
        if (botPreviewsEditContainer != null) {
            botPreviewsEditContainer.unselectAll();
            this.botPreviewsContainer.updateSelection(true);
        }
        showActionMode(false);
        updateRowsSelection(z);
        SavedDialogsAdapter savedDialogsAdapter = this.savedDialogsAdapter;
        if (savedDialogsAdapter != null) {
            savedDialogsAdapter.unselectAll();
        }
        return true;
    }

    protected boolean customTabs() {
        return false;
    }

    @Override
    public void didReceivedNotification(int r32, int r33, java.lang.Object... r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public void disableScroll(boolean z) {
        if (z) {
            stopScroll(null);
        }
        this.disableScrolling = z;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.scrollSlidingTextTabStrip != null) {
            canvas.save();
            canvas.translate(this.scrollSlidingTextTabStrip.getX(), this.scrollSlidingTextTabStrip.getY());
            this.scrollSlidingTextTabStrip.drawBackground(canvas);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView == null || !fragmentContextView.isCallStyle()) {
            return;
        }
        canvas.save();
        canvas.translate(this.fragmentContextView.getX(), this.fragmentContextView.getY());
        this.fragmentContextView.setDrawOverlay(true);
        this.fragmentContextView.draw(canvas);
        this.fragmentContextView.setDrawOverlay(false);
        canvas.restore();
    }

    public boolean dispatchFastScrollEvent(MotionEvent motionEvent) {
        View view = (View) getParent();
        motionEvent.offsetLocation(((-view.getX()) - getX()) - this.mediaPages[0].listView.getFastScroll().getX(), (((-view.getY()) - getY()) - this.mediaPages[0].getY()) - this.mediaPages[0].listView.getFastScroll().getY());
        return this.mediaPages[0].listView.getFastScroll().dispatchTouchEvent(motionEvent);
    }

    protected void drawBackgroundWithBlur(Canvas canvas, float f, android.graphics.Rect rect, Paint paint) {
        canvas.drawRect(rect, paint);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view != this.fragmentContextView) {
            return super.drawChild(canvas, view, j);
        }
        canvas.save();
        canvas.clipRect(0, this.mediaPages[0].getTop(), view.getMeasuredWidth(), this.mediaPages[0].getTop() + view.getMeasuredHeight() + AndroidUtilities.dp(12.0f));
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    public void drawListForBlur(Canvas canvas, ArrayList arrayList) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            MediaPage mediaPage = mediaPageArr[i];
            if (mediaPage != null && mediaPage.getVisibility() == 0) {
                for (int i2 = 0; i2 < this.mediaPages[i].listView.getChildCount(); i2++) {
                    View childAt = this.mediaPages[i].listView.getChildAt(i2);
                    if (childAt.getY() < this.mediaPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)) {
                        int save = canvas.save();
                        canvas.translate(this.mediaPages[i].getX() + childAt.getX(), getY() + this.mediaPages[i].getY() + this.mediaPages[i].listView.getY() + childAt.getY());
                        childAt.draw(canvas);
                        if (arrayList != null && (childAt instanceof SizeNotifierFrameLayout.IViewWithInvalidateCallback)) {
                            arrayList.add((SizeNotifierFrameLayout.IViewWithInvalidateCallback) childAt);
                        }
                        canvas.restoreToCount(save);
                    }
                }
            }
            i++;
        }
    }

    @Override
    public void forceHasOverlappingRendering(boolean z) {
        super.forceHasOverlappingRendering(z);
    }

    public SparseArray<MessageObject> getActionModeSelected() {
        return this.selectedFiles[0];
    }

    public String getBotPreviewsSubtitle(boolean z) {
        int i;
        int i2;
        TLRPC.MessageMedia messageMedia;
        BotPreviewsEditContainer botPreviewsEditContainer;
        if (isBot()) {
            if (z && (botPreviewsEditContainer = this.botPreviewsContainer) != null) {
                return botPreviewsEditContainer.getBotPreviewsSubtitle();
            }
            StoriesAdapter storiesAdapter = this.storiesAdapter;
            if (storiesAdapter == null || storiesAdapter.storiesList == null) {
                i = 0;
                i2 = 0;
            } else {
                i = 0;
                i2 = 0;
                for (int i3 = 0; i3 < this.storiesAdapter.storiesList.messageObjects.size(); i3++) {
                    MessageObject messageObject = (MessageObject) this.storiesAdapter.storiesList.messageObjects.get(i3);
                    TL_stories.StoryItem storyItem = messageObject.storyItem;
                    if (storyItem != null && (messageMedia = storyItem.media) != null) {
                        if (MessageObject.isVideoDocument(messageMedia.document)) {
                            i2++;
                        } else if (messageObject.storyItem.media.photo != null) {
                            i++;
                        }
                    }
                }
            }
            if (i != 0 || i2 != 0) {
                StringBuilder sb = new StringBuilder();
                if (i > 0) {
                    sb.append(LocaleController.formatPluralString("Images", i, new Object[0]));
                }
                if (i2 > 0) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(LocaleController.formatPluralString("Videos", i2, new Object[0]));
                }
                return sb.toString();
            }
        }
        return LocaleController.getString(R.string.BotPreviewEmpty);
    }

    public int getClosestTab() {
        MediaPage mediaPage = this.mediaPages[1];
        if (mediaPage == null || mediaPage.getVisibility() != 0 || ((!this.tabsAnimationInProgress || this.backAnimation) && Math.abs(this.mediaPages[1].getTranslationX()) >= this.mediaPages[1].getMeasuredWidth() / 2.0f)) {
            return this.scrollSlidingTextTabStrip.getCurrentTabId();
        }
        return this.mediaPages[1].selectedType;
    }

    public RecyclerListView getCurrentListView() {
        ChatActivityContainer chatActivityContainer;
        return this.mediaPages[0].selectedType == 13 ? this.botPreviewsContainer.getCurrentListView() : this.mediaPages[0].selectedType == 14 ? this.giftsContainer.getCurrentListView() : (this.mediaPages[0].selectedType != 12 || (chatActivityContainer = this.savedMessagesContainer) == null) ? this.mediaPages[0].listView : chatActivityContainer.chatActivity.getChatListView();
    }

    protected int getInitialTab() {
        return 0;
    }

    public int getNextMediaColumnsCount(int i, int i2, boolean z) {
        int i3 = i2 + (!z ? 1 : -1);
        if (i3 > 6) {
            i3 = !z ? 9 : 6;
        }
        return Utilities.clamp(i3, 9, (this.allowStoriesSingleColumn && i == 1) ? 1 : 2);
    }

    public float getPhotoVideoOptionsAlpha(float f) {
        float f2 = 0.0f;
        if (isArchivedOnlyStoriesView()) {
            return 0.0f;
        }
        MediaPage mediaPage = this.mediaPages[1];
        if (mediaPage != null && (mediaPage.selectedType == 0 || ((this.mediaPages[1].selectedType == 8 && TextUtils.isEmpty(getStoriesHashtag())) || this.mediaPages[1].selectedType == 9 || this.mediaPages[1].selectedType == 11 || this.mediaPages[1].selectedType == 13))) {
            f2 = 0.0f + f;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        return mediaPage2 != null ? (mediaPage2.selectedType == 0 || (this.mediaPages[0].selectedType == 8 && TextUtils.isEmpty(getStoriesHashtag())) || this.mediaPages[0].selectedType == 9 || this.mediaPages[0].selectedType == 11 || this.mediaPages[0].selectedType == 13) ? f2 + (1.0f - f) : f2 : f2;
    }

    public int getPhotosVideosTypeFilter() {
        return this.sharedMediaData[0].filterType;
    }

    public float getSearchAlpha(float f) {
        float f2 = 0.0f;
        if (isArchivedOnlyStoriesView()) {
            return 0.0f;
        }
        MediaPage mediaPage = this.mediaPages[1];
        if (mediaPage != null && isSearchItemVisible(mediaPage.selectedType) && this.mediaPages[1].selectedType != 11) {
            f2 = 0.0f + f;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        return (mediaPage2 == null || !isSearchItemVisible(mediaPage2.selectedType) || this.mediaPages[0].selectedType == 11) ? f2 : f2 + (1.0f - f);
    }

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public RLottieImageView getSearchOptionsItem() {
        return this.optionsSearchImageView;
    }

    public int getSelectedTab() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId();
    }

    public TL_stories.MediaArea getStoriesArea() {
        return null;
    }

    public int getStoriesCount(int i) {
        StoriesAdapter storiesAdapter;
        if (i != 8) {
            if (i == 9) {
                storiesAdapter = this.archivedStoriesAdapter;
            }
            return 0;
        }
        storiesAdapter = this.storiesAdapter;
        StoriesController.StoriesList storiesList = storiesAdapter.storiesList;
        if (storiesList != null) {
            return storiesList.getCount();
        }
        return 0;
    }

    public String getStoriesHashtag() {
        return null;
    }

    public String getStoriesHashtagUsername() {
        return null;
    }

    public float getTabProgress() {
        float f = 0.0f;
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return f;
            }
            if (mediaPageArr[i] != null) {
                f += r2.selectedType * (1.0f - Math.abs(this.mediaPages[i].getTranslationX() / getWidth()));
            }
            i++;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.shadowLine, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        RLottieImageView iconView = this.deleteItem.getIconView();
        int i = ThemeDescription.FLAG_IMAGECOLOR;
        int i2 = Theme.key_actionBarActionModeDefaultIcon;
        arrayList.add(new ThemeDescription(iconView, i, null, null, null, null, i2));
        ActionBarMenuItem actionBarMenuItem = this.deleteItem;
        int i3 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i4 = Theme.key_actionBarActionModeDefaultSelector;
        arrayList.add(new ThemeDescription(actionBarMenuItem, i3, null, null, null, null, i4));
        if (this.gotoItem != null) {
            arrayList.add(new ThemeDescription(this.gotoItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.gotoItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i4));
        }
        if (this.forwardItem != null) {
            arrayList.add(new ThemeDescription(this.forwardItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.forwardItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i4));
        }
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, new Drawable[]{this.backDrawable}, null, i2));
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i4));
        LinearLayout linearLayout = this.actionModeLayout;
        int i5 = ThemeDescription.FLAG_BACKGROUND;
        int i6 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(linearLayout, i5, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, Theme.key_chat_mediaTimeBackground));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, Theme.key_chat_mediaTimeText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_tabSelectedLine));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabSelectedText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabSelector));
        if (this.fragmentContextView != null) {
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallBackground));
            arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallText));
        }
        for (final int i7 = 0; i7 < this.mediaPages.length; i7++) {
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
                @Override
                public final void didSetColor() {
                    SharedMediaLayout.this.lambda$getThemeDescriptions$28(i7);
                }

                @Override
                public void onAnimationProgress(float f) {
                    ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
            FlickerLoadingView flickerLoadingView = this.mediaPages[i7].progressView;
            int i8 = Theme.key_windowBackgroundWhite;
            arrayList.add(new ThemeDescription(flickerLoadingView, 0, null, null, null, null, i8));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
            int i9 = Theme.key_progressCircle;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
            int i10 = Theme.key_windowBackgroundWhiteBlackText;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            int i11 = Theme.key_windowBackgroundWhiteGrayText;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i11));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
            Drawable[] drawableArr = Theme.avatarDrawables;
            int i12 = Theme.key_avatar_text;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{UserCell.class}, null, drawableArr, null, i12));
            TextPaint[] textPaintArr = Theme.dialogs_namePaint;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
            TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ProfileSearchCell.class}, null, Theme.avatarDrawables, null, i12));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
            int i13 = Theme.key_windowBackgroundWhiteGrayText2;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyStubView.class}, new String[]{"emptyTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i13));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
            int i14 = Theme.key_sharedMedia_startStopLoadIcon;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i14));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i14));
            int i15 = Theme.key_checkbox;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
            int i16 = Theme.key_checkboxCheck;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, i13));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholderText));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SECTIONS | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{SharedMediaSectionCell.class}, null, null, null, i8));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
            int i17 = Theme.key_sharedMedia_photoPlaceholder;
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, themeDescriptionDelegate, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, themeDescriptionDelegate, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, new Class[]{ContextLinkCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{ContextLinkCell.class}, null, null, themeDescriptionDelegate, i15));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{ContextLinkCell.class}, null, null, themeDescriptionDelegate, i16));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].listView, 0, null, null, new Drawable[]{this.pinnedHeaderShadowDrawable}, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i10));
            arrayList.add(new ThemeDescription(this.mediaPages[i7].emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i11));
        }
        return arrayList;
    }

    public boolean hasInternet() {
        return this.profileActivity.getConnectionsManager().getConnectionState() == 3;
    }

    protected boolean includeSavedDialogs() {
        return false;
    }

    protected boolean includeStories() {
        return true;
    }

    protected void invalidateBlur() {
    }

    public boolean isActionModeShown() {
        return this.isActionModeShowed;
    }

    protected boolean isArchivedOnlyStoriesView() {
        return false;
    }

    protected boolean isBot() {
        TLRPC.User user;
        return this.dialog_id > 0 && (user = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getUser(Long.valueOf(this.dialog_id))) != null && user.bot;
    }

    public boolean isCalendarItemVisible() {
        return this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9 || this.mediaPages[0].selectedType == 11;
    }

    public boolean isCurrentTabFirst() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
    }

    public boolean isInFastScroll() {
        MediaPage mediaPage = this.mediaPages[0];
        return (mediaPage == null || mediaPage.listView.getFastScroll() == null || !this.mediaPages[0].listView.getFastScroll().isPressed()) ? false : true;
    }

    public boolean isOptionsItemVisible() {
        return this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 8 || this.mediaPages[0].selectedType == 9 || this.mediaPages[0].selectedType == 11 || this.mediaPages[0].selectedType == 13;
    }

    public boolean isPinnedToTop() {
        return this.isPinnedToTop;
    }

    public boolean isSearchItemVisible() {
        return isSearchItemVisible(this.mediaPages[0].selectedType);
    }

    public boolean isSearchItemVisible(int i) {
        return i == 7 ? this.delegate.canSearchMembers() : (isSearchingStories() || i == 0 || i == 8 || i == 9 || i == 2 || i == 5 || i == 6 || i == 11 || i == 10 || i == 13) ? false : true;
    }

    public boolean isSearchingStories() {
        return (TextUtils.isEmpty(getStoriesHashtag()) && getStoriesArea() == null) ? false : true;
    }

    protected boolean isSelf() {
        return false;
    }

    protected boolean isStoriesView() {
        return false;
    }

    public boolean isSwipeBackEnabled() {
        return ((canEditStories() && ((getClosestTab() == 8 || getClosestTab() == 13) && isActionModeShown())) || this.photoVideoChangeColumnsAnimation || this.tabsAnimationInProgress) ? false : true;
    }

    public boolean isTabZoomable(int i) {
        return i == 0 || i == 8 || i == 9;
    }

    public int mediaPageTopMargin() {
        return customTabs() ? 0 : 48;
    }

    public void onActionBarItemClick(View view, int i) {
        BulletinFactory of;
        int i2;
        String formatPluralString;
        Bulletin createSimpleBulletin;
        int i3;
        String str;
        AlertDialog create;
        BotPreviewsEditContainer botPreviewsEditContainer;
        TLRPC.Chat chat;
        TLRPC.User user;
        TLRPC.EncryptedChat encryptedChat;
        boolean z;
        TLRPC.User user2;
        if (i == 101) {
            if (getSelectedTab() == 8 || getSelectedTab() == 9 || getSelectedTab() == 13) {
                if (this.selectedFiles[0] != null) {
                    if (!isBot() || (botPreviewsEditContainer = this.botPreviewsContainer) == null || botPreviewsEditContainer.getCurrentList() == null) {
                        final ArrayList arrayList = new ArrayList();
                        for (int i4 = 0; i4 < this.selectedFiles[0].size(); i4++) {
                            TL_stories.StoryItem storyItem = ((MessageObject) this.selectedFiles[0].valueAt(i4)).storyItem;
                            if (storyItem != null) {
                                arrayList.add(storyItem);
                            }
                        }
                        if (arrayList.isEmpty()) {
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
                        builder.setTitle(LocaleController.getString(arrayList.size() > 1 ? R.string.DeleteStoriesTitle : R.string.DeleteStoryTitle));
                        builder.setMessage(LocaleController.formatPluralString("DeleteStoriesSubtitle", arrayList.size(), new Object[0]));
                        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i5) {
                                SharedMediaLayout.this.profileActivity.getMessagesController().getStoriesController().deleteStories(SharedMediaLayout.this.dialog_id, arrayList);
                                BulletinFactory.of(SharedMediaLayout.this.profileActivity).createSimpleBulletin(R.raw.ic_delete, LocaleController.formatPluralString("StoriesDeleted", arrayList.size(), new Object[0])).show();
                                SharedMediaLayout.this.closeActionMode(false);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i5) {
                                dialogInterface.dismiss();
                            }
                        });
                        create = builder.create();
                    } else {
                        final StoriesController.BotPreviewsList currentList = this.botPreviewsContainer.getCurrentList();
                        final ArrayList arrayList2 = new ArrayList();
                        for (int i5 = 0; i5 < this.selectedFiles[0].size(); i5++) {
                            TL_stories.StoryItem storyItem2 = ((MessageObject) this.selectedFiles[0].valueAt(i5)).storyItem;
                            if (storyItem2 != null) {
                                arrayList2.add(storyItem2.media);
                            }
                        }
                        if (arrayList2.isEmpty()) {
                            return;
                        }
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext(), this.resourcesProvider);
                        builder2.setTitle(LocaleController.getString(arrayList2.size() > 1 ? R.string.DeleteBotPreviewsTitle : R.string.DeleteBotPreviewTitle));
                        builder2.setMessage(LocaleController.formatPluralString("DeleteBotPreviewsSubtitle", arrayList2.size(), new Object[0]));
                        builder2.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i6) {
                                currentList.delete(arrayList2);
                                BulletinFactory.of(SharedMediaLayout.this.profileActivity).createSimpleBulletin(R.raw.ic_delete, LocaleController.formatPluralString("BotPreviewsDeleted", arrayList2.size(), new Object[0])).show();
                                SharedMediaLayout.this.closeActionMode(false);
                            }
                        });
                        builder2.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i6) {
                                dialogInterface.dismiss();
                            }
                        });
                        create = builder2.create();
                    }
                    create.show();
                    create.redPositive();
                    return;
                }
                return;
            }
            if (getSelectedTab() != 11) {
                if (DialogObject.isEncryptedDialog(this.dialog_id)) {
                    encryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialog_id)));
                    user = null;
                    chat = null;
                } else if (DialogObject.isUserDialog(this.dialog_id)) {
                    user = this.profileActivity.getMessagesController().getUser(Long.valueOf(this.dialog_id));
                    chat = null;
                    encryptedChat = null;
                } else {
                    chat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                    user = null;
                    encryptedChat = null;
                }
                AlertsCreator.createDeleteMessagesAlert(this.profileActivity, user, chat, encryptedChat, null, this.mergeDialogId, null, this.selectedFiles, null, 0, 0, null, new Runnable() {
                    @Override
                    public final void run() {
                        SharedMediaLayout.this.lambda$onActionBarItemClick$22();
                    }
                }, null, this.resourcesProvider);
                return;
            }
            SavedMessagesController savedMessagesController = this.profileActivity.getMessagesController().getSavedMessagesController();
            final ArrayList arrayList3 = new ArrayList();
            for (int i6 = 0; i6 < savedMessagesController.allDialogs.size(); i6++) {
                long j = savedMessagesController.allDialogs.get(i6).dialogId;
                if (this.savedDialogsAdapter.selectedDialogs.contains(Long.valueOf(j))) {
                    arrayList3.add(Long.valueOf(j));
                }
            }
            String str2 = "";
            if (arrayList3.isEmpty()) {
                z = false;
            } else {
                Long l = (Long) arrayList3.get(0);
                long longValue = l.longValue();
                z = longValue == this.profileActivity.getUserConfig().getClientUserId();
                if (longValue < 0) {
                    TLRPC.Chat chat2 = this.profileActivity.getMessagesController().getChat(Long.valueOf(-longValue));
                    if (chat2 != null) {
                        str2 = chat2.title;
                    }
                } else if (longValue >= 0 && (user2 = this.profileActivity.getMessagesController().getUser(l)) != null) {
                    str2 = UserObject.isAnonymous(user2) ? LocaleController.getString(R.string.AnonymousForward) : UserObject.getUserName(user2);
                }
            }
            AlertDialog create2 = new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(arrayList3.size() == 1 ? LocaleController.formatString(z ? R.string.ClearHistoryMyNotesTitle : R.string.ClearHistoryTitleSingle2, str2) : LocaleController.formatPluralString("ClearHistoryTitleMultiple", arrayList3.size(), new Object[0])).setMessage(arrayList3.size() == 1 ? LocaleController.formatString(z ? R.string.ClearHistoryMyNotesMessage : R.string.ClearHistoryMessageSingle, str2) : LocaleController.formatPluralString("ClearHistoryMessageMultiple", arrayList3.size(), new Object[0])).setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i7) {
                    SharedMediaLayout.this.lambda$onActionBarItemClick$21(arrayList3, dialogInterface, i7);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
            this.profileActivity.showDialog(create2);
            TextView textView = (TextView) create2.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                return;
            }
            return;
        }
        if (i == 100) {
            if (this.info != null) {
                TLRPC.Chat chat3 = this.profileActivity.getMessagesController().getChat(Long.valueOf(this.info.id));
                if (this.profileActivity.getMessagesController().isChatNoForwards(chat3)) {
                    HintView hintView = this.fwdRestrictedHint;
                    if (hintView != null) {
                        if (!ChatObject.isChannel(chat3) || chat3.megagroup) {
                            i3 = R.string.ForwardsRestrictedInfoGroup;
                            str = "ForwardsRestrictedInfoGroup";
                        } else {
                            i3 = R.string.ForwardsRestrictedInfoChannel;
                            str = "ForwardsRestrictedInfoChannel";
                        }
                        hintView.setText(LocaleController.getString(str, i3));
                        this.fwdRestrictedHint.showForView(view, true);
                        return;
                    }
                    return;
                }
            }
            if (hasNoforwardsMessage()) {
                HintView hintView2 = this.fwdRestrictedHint;
                if (hintView2 != null) {
                    hintView2.setText(LocaleController.getString("ForwardsRestrictedInfoBot", R.string.ForwardsRestrictedInfoBot));
                    this.fwdRestrictedHint.showForView(view, true);
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("canSelectTopics", true);
            bundle.putInt("dialogsType", 3);
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList4, CharSequence charSequence, boolean z2, boolean z3, int i7, TopicsFragment topicsFragment) {
                    boolean lambda$onActionBarItemClick$23;
                    lambda$onActionBarItemClick$23 = SharedMediaLayout.this.lambda$onActionBarItemClick$23(dialogsActivity2, arrayList4, charSequence, z2, z3, i7, topicsFragment);
                    return lambda$onActionBarItemClick$23;
                }
            });
            this.profileActivity.presentFragment(dialogsActivity);
            return;
        }
        if (i == 102) {
            if (this.selectedFiles[0].size() + this.selectedFiles[1].size() != 1) {
                return;
            }
            SparseArray[] sparseArrayArr = this.selectedFiles;
            MessageObject messageObject = (MessageObject) sparseArrayArr[sparseArrayArr[0].size() == 1 ? (char) 0 : (char) 1].valueAt(0);
            Bundle bundle2 = new Bundle();
            long dialogId = messageObject.getDialogId();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                bundle2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
            } else if (DialogObject.isUserDialog(dialogId)) {
                bundle2.putLong("user_id", dialogId);
            } else {
                TLRPC.Chat chat4 = this.profileActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
                if (chat4 != null && chat4.migrated_to != null) {
                    bundle2.putLong("migrated_to", dialogId);
                    dialogId = -chat4.migrated_to.channel_id;
                }
                bundle2.putLong("chat_id", -dialogId);
            }
            bundle2.putInt("message_id", messageObject.getId());
            bundle2.putBoolean("need_remove_previous_same_chat_activity", false);
            ChatActivity chatActivity = new ChatActivity(bundle2);
            chatActivity.highlightMessageId = messageObject.getId();
            long j2 = this.topicId;
            if (j2 != 0) {
                ForumUtilities.applyTopic(chatActivity, MessagesStorage.TopicKey.of(dialogId, j2));
                bundle2.putInt("message_id", messageObject.getId());
            }
            this.profileActivity.presentFragment(chatActivity, false);
            return;
        }
        if (i == 103 || i == 104) {
            if (getClosestTab() != 8) {
                SavedMessagesController savedMessagesController2 = this.profileActivity.getMessagesController().getSavedMessagesController();
                ArrayList<Long> arrayList4 = new ArrayList<>();
                for (int i7 = 0; i7 < savedMessagesController2.allDialogs.size(); i7++) {
                    long j3 = savedMessagesController2.allDialogs.get(i7).dialogId;
                    if (this.savedDialogsAdapter.selectedDialogs.contains(Long.valueOf(j3))) {
                        arrayList4.add(Long.valueOf(j3));
                    }
                }
                if (savedMessagesController2.updatePinned(arrayList4, i == 103, true)) {
                    int i8 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        if (i8 >= mediaPageArr.length) {
                            break;
                        }
                        if (mediaPageArr[i8].selectedType == 11) {
                            this.mediaPages[i8].layoutManager.scrollToPositionWithOffset(0, 0);
                            break;
                        }
                        i8++;
                    }
                } else {
                    this.profileActivity.showDialog(new LimitReachedBottomSheet(this.profileActivity, getContext(), 33, this.profileActivity.getCurrentAccount(), null));
                }
                closeActionMode(true);
                return;
            }
            StoriesAdapter storiesAdapter = this.storiesAdapter;
            if (storiesAdapter == null || storiesAdapter.storiesList == null) {
                return;
            }
            ArrayList arrayList5 = new ArrayList();
            for (int i9 = 0; i9 < this.selectedFiles[0].size(); i9++) {
                arrayList5.add(Integer.valueOf(((MessageObject) this.selectedFiles[0].valueAt(i9)).getId()));
            }
            if (i == 103 && arrayList5.size() > this.profileActivity.getMessagesController().storiesPinnedToTopCountMax) {
                BulletinFactory.of(this.profileActivity).createSimpleBulletin(R.raw.chats_infotip, AndroidUtilities.replaceTags(LocaleController.formatPluralString("StoriesPinLimit", this.profileActivity.getMessagesController().storiesPinnedToTopCountMax, new Object[0]))).show();
                return;
            }
            if (this.storiesAdapter.storiesList.updatePinned(arrayList5, i == 103)) {
                of = BulletinFactory.of(this.profileActivity);
                i2 = R.raw.chats_infotip;
                formatPluralString = LocaleController.formatPluralString("StoriesPinLimit", this.profileActivity.getMessagesController().storiesPinnedToTopCountMax, new Object[0]);
            } else if (i == 103) {
                createSimpleBulletin = BulletinFactory.of(this.profileActivity).createSimpleBulletin(R.raw.ic_pin, AndroidUtilities.replaceTags(LocaleController.formatPluralString("StoriesPinned", arrayList5.size(), new Object[0])), LocaleController.formatPluralString("StoriesPinnedText", arrayList5.size(), new Object[0]));
                createSimpleBulletin.show();
                closeActionMode(false);
            } else {
                of = BulletinFactory.of(this.profileActivity);
                i2 = R.raw.ic_unpin;
                formatPluralString = LocaleController.formatPluralString("StoriesUnpinned", arrayList5.size(), new Object[0]);
            }
            createSimpleBulletin = of.createSimpleBulletin(i2, AndroidUtilities.replaceTags(formatPluralString));
            createSimpleBulletin.show();
            closeActionMode(false);
        }
    }

    public void onActionModeSelectedUpdate(SparseArray sparseArray) {
    }

    @Override
    public void onButtonClicked(DialogCell dialogCell) {
    }

    @Override
    public void onButtonLongPress(DialogCell dialogCell) {
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            if (mediaPageArr[i].listView != null) {
                this.mediaPages[i].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        SharedMediaLayout.this.mediaPages[i].getViewTreeObserver().removeOnPreDrawListener(this);
                        SharedMediaLayout.this.fixLayoutInternal(i);
                        return true;
                    }
                });
            }
            i++;
        }
    }

    public void onDestroy() {
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidStart);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.storiesListUpdated);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.storiesUpdated);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.channelRecommendationsLoaded);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.savedMessagesDialogsUpdate);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.starUserGiftsLoaded);
        SearchTagsList searchTagsList = this.searchTagsList;
        if (searchTagsList != null) {
            searchTagsList.detach();
        }
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter != null && storiesAdapter.storiesList != null) {
            storiesAdapter.destroy();
        }
        StoriesAdapter storiesAdapter2 = this.archivedStoriesAdapter;
        if (storiesAdapter2 == null || storiesAdapter2.storiesList == null) {
            return;
        }
        storiesAdapter2.destroy();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return checkTabsAnimationInProgress() || this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int height = this.delegate.getListView() != null ? this.delegate.getListView().getHeight() : 0;
        if (height == 0) {
            height = View.MeasureSpec.getSize(i2);
        }
        setMeasuredDimension(size, height);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt != null && childAt.getVisibility() != 8) {
                if (childAt instanceof MediaPage) {
                    measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(height, 1073741824), 0);
                    MediaPage mediaPage = (MediaPage) childAt;
                    mediaPage.listView.setPadding(0, mediaPage.listView.topPadding, 0, mediaPage.listView.bottomPadding);
                } else {
                    measureChildWithMargins(childAt, i, 0, i2, 0);
                }
            }
        }
    }

    protected boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, View view) {
        return false;
    }

    public void onPause() {
        ChatActivityContainer chatActivityContainer = this.savedMessagesContainer;
        if (chatActivityContainer != null) {
            chatActivityContainer.onPause();
        }
    }

    public void onResume() {
        this.scrolling = true;
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        for (int i = 0; i < this.mediaPages.length; i++) {
            fixLayoutInternal(i);
        }
        ChatActivityContainer chatActivityContainer = this.savedMessagesContainer;
        if (chatActivityContainer != null) {
            chatActivityContainer.onResume();
        }
    }

    protected void onSearchStateChanged(boolean z) {
    }

    public void onSelectedTabChanged() {
        boolean z = isStoriesView() || isArchivedOnlyStoriesView();
        if (this.archivedStoriesAdapter.poller != null) {
            this.archivedStoriesAdapter.poller.start(z && getClosestTab() == 9);
        }
        if (this.storiesAdapter.poller != null) {
            this.storiesAdapter.poller.start(z && getClosestTab() == 8);
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            SearchTagsList searchTagsList = this.searchTagsList;
            actionBarMenuItem.setSearchFieldHint(LocaleController.getString((searchTagsList != null && searchTagsList.hasFilters() && getSelectedTab() == 11) ? R.string.SavedTagSearchHint : R.string.Search));
        }
    }

    public void onTabProgress(float f) {
    }

    protected void onTabScroll(boolean z) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        MediaPage mediaPage;
        int measuredWidth;
        boolean z;
        if (this.disableScrolling || this.profileActivity.getParentLayout() == null || this.profileActivity.getParentLayout().checkTransitionAnimation() || checkTabsAnimationInProgress() || this.isInPinchToZoomTouchMode) {
            return false;
        }
        if (motionEvent != null) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(motionEvent);
            HintView hintView = this.fwdRestrictedHint;
            if (hintView != null) {
                hintView.hide();
            }
        }
        if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking && motionEvent.getY() >= AndroidUtilities.dp(48.0f)) {
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
            this.startedTrackingY = (int) motionEvent.getY();
            this.velocityTracker.clear();
        } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
            int x = (int) (motionEvent.getX() - this.startedTrackingX);
            int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
            if (this.startedTracking && (((z = this.animatingForward) && x > 0) || (!z && x < 0))) {
                if (!prepareForMoving(motionEvent, x < 0)) {
                    this.maybeStartTracking = true;
                    this.startedTracking = false;
                    onTabScroll(false);
                    this.mediaPages[0].setTranslationX(0.0f);
                    this.mediaPages[1].setTranslationX(this.animatingForward ? r4[0].getMeasuredWidth() : -r4[0].getMeasuredWidth());
                    this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, 0.0f);
                    onTabProgress(getTabProgress());
                }
            }
            if (!this.maybeStartTracking || this.startedTracking) {
                if (this.startedTracking) {
                    this.mediaPages[0].setTranslationX(x);
                    if (this.animatingForward) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        mediaPage = mediaPageArr[1];
                        measuredWidth = mediaPageArr[0].getMeasuredWidth() + x;
                    } else {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        mediaPage = mediaPageArr2[1];
                        measuredWidth = x - mediaPageArr2[0].getMeasuredWidth();
                    }
                    mediaPage.setTranslationX(measuredWidth);
                    float abs2 = Math.abs(x) / this.mediaPages[0].getMeasuredWidth();
                    if (canShowSearchItem()) {
                        this.searchAlpha = getSearchAlpha(abs2);
                        updateSearchItemIcon(abs2);
                        float photoVideoOptionsAlpha = getPhotoVideoOptionsAlpha(abs2);
                        this.optionsAlpha = photoVideoOptionsAlpha;
                        this.photoVideoOptionsItem.setVisibility((photoVideoOptionsAlpha == 0.0f || !canShowSearchItem() || isArchivedOnlyStoriesView()) ? 4 : 0);
                    } else {
                        this.searchAlpha = 0.0f;
                    }
                    updateOptionsSearch();
                    this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, abs2);
                    onTabProgress(getTabProgress());
                    onSelectedTabChanged();
                }
            } else if (Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                prepareForMoving(motionEvent, x < 0);
            }
        } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
            stopScroll(motionEvent);
        }
        return this.startedTracking;
    }

    @Override
    public void openHiddenStories() {
    }

    @Override
    public void openStory(DialogCell dialogCell, Runnable runnable) {
        BaseFragment baseFragment = this.profileActivity;
        if (baseFragment != null && baseFragment.getMessagesController().getStoriesController().hasStories(dialogCell.getDialogId())) {
            this.profileActivity.getOrCreateStoryViewer().doOnAnimationReady(runnable);
            StoryViewer orCreateStoryViewer = this.profileActivity.getOrCreateStoryViewer();
            Context context = this.profileActivity.getContext();
            long dialogId = dialogCell.getDialogId();
            StoriesListPlaceProvider of = StoriesListPlaceProvider.of((RecyclerListView) dialogCell.getParent());
            BaseFragment baseFragment2 = this.profileActivity;
            orCreateStoryViewer.open(context, dialogId, of.addBottomClip(((baseFragment2 instanceof ProfileActivity) && ((ProfileActivity) baseFragment2).myProfile) ? AndroidUtilities.dp(68.0f) : 0));
        }
    }

    public void openStoryRecorder() {
        StoryRecorder.getInstance(this.profileActivity.getParentActivity(), this.profileActivity.getCurrentAccount()).open(null);
    }

    public int overrideColumnsCount() {
        return -1;
    }

    protected int processColor(int i) {
        return i;
    }

    public void scrollToPage(int i) {
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner;
        if (this.disableScrolling || (scrollSlidingTextTabStripInner = this.scrollSlidingTextTabStrip) == null) {
            return;
        }
        scrollSlidingTextTabStripInner.scrollTo(i);
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        TLRPC.ChatFull chatFull2 = this.info;
        boolean z = chatFull2 != null && chatFull2.stories_pinned_available;
        this.info = chatFull;
        if (chatFull != null) {
            long j = chatFull.migrated_from_chat_id;
            if (j != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -j;
                int i = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                    if (i >= sharedMediaDataArr.length) {
                        break;
                    }
                    SharedMediaData sharedMediaData = sharedMediaDataArr[i];
                    sharedMediaData.max_id[1] = this.info.migrated_from_max_id;
                    sharedMediaData.endReached[1] = false;
                    i++;
                }
            }
        }
        TLRPC.ChatFull chatFull3 = this.info;
        if (chatFull3 == null || z == chatFull3.stories_pinned_available) {
            return;
        }
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStripInner != null) {
            scrollSlidingTextTabStripInner.setInitialTabId(isArchivedOnlyStoriesView() ? 9 : 8);
        }
        updateTabs(true);
        switchToCurrentSelectedMode(false);
    }

    public void setChatUsers(ArrayList arrayList, TLRPC.ChatFull chatFull) {
        int i = 0;
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                if (this.topicId == 0) {
                    this.chatUsersAdapter.chatInfo = chatFull;
                    this.chatUsersAdapter.sortedUsers = arrayList;
                }
                updateTabs(true);
                while (true) {
                    MediaPage[] mediaPageArr2 = this.mediaPages;
                    if (i >= mediaPageArr2.length) {
                        return;
                    }
                    if (mediaPageArr2[i].selectedType == 7 && this.mediaPages[i].listView.getAdapter() != null) {
                        AndroidUtilities.notifyDataSetChanged(this.mediaPages[i].listView);
                    }
                    i++;
                }
            } else if (mediaPageArr[i2].selectedType == 7 && this.mediaPages[i2].listView.getAdapter() != null && this.mediaPages[i2].listView.getAdapter().getItemCount() != 0 && this.profileActivity.getMessagesController().getStoriesController().hasLoadingStories()) {
                return;
            } else {
                i2++;
            }
        }
    }

    public void setCommonGroupsCount(int i) {
        if (this.topicId == 0) {
            this.hasMedia[6] = i;
        }
        updateTabs(true);
        checkCurrentTabValid();
    }

    public void setForwardRestrictedHint(HintView hintView) {
        this.fwdRestrictedHint = hintView;
    }

    public void setMergeDialogId(long j) {
        this.mergeDialogId = j;
    }

    public void setNewMediaCounts(int[] iArr) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= 6) {
                z = false;
                break;
            } else {
                if (this.hasMedia[i] >= 0) {
                    z = true;
                    break;
                }
                i++;
            }
        }
        System.arraycopy(iArr, 0, this.hasMedia, 0, 6);
        updateTabs(true);
        if (!z && this.scrollSlidingTextTabStrip.getCurrentTabId() == 6) {
            this.scrollSlidingTextTabStrip.resetTab();
        }
        checkCurrentTabValid();
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    @Override
    public void setPadding(int i, int i2, int i3, int i4) {
        this.topPadding = i2;
        int i5 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i5 >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[i5].setTranslationY(this.topPadding - this.lastMeasuredTopPadding);
            i5++;
        }
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null) {
            fragmentContextView.setTranslationY(AndroidUtilities.dp(48.0f) + i2);
        }
        this.additionalFloatingTranslation = i2;
        ChatActionCell chatActionCell = this.floatingDateView;
        chatActionCell.setTranslationY((chatActionCell.getTag() == null ? -AndroidUtilities.dp(48.0f) : 0) + this.additionalFloatingTranslation);
    }

    public void setPinnedToTop(boolean z) {
        if (this.isPinnedToTop == z) {
            return;
        }
        this.isPinnedToTop = z;
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return;
            }
            updateFastScrollVisibility(mediaPageArr[i], true);
            i++;
        }
    }

    public void setStoriesFilter(boolean z, boolean z2) {
        StoriesController.StoriesList storiesList;
        StoriesController.StoriesList storiesList2;
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter != null && (storiesList2 = storiesAdapter.storiesList) != null) {
            storiesList2.updateFilters(z, z2);
        }
        StoriesAdapter storiesAdapter2 = this.archivedStoriesAdapter;
        if (storiesAdapter2 == null || (storiesList = storiesAdapter2.storiesList) == null) {
            return;
        }
        storiesList.updateFilters(z, z2);
    }

    public void setUserInfo(TLRPC.UserFull userFull) {
        TLRPC.UserFull userFull2 = this.userInfo;
        boolean z = userFull2 != null && userFull2.stories_pinned_available;
        this.userInfo = userFull;
        updateTabs(true);
        if (userFull == null || z == userFull.stories_pinned_available) {
            return;
        }
        scrollToPage(8);
    }

    public void setVisibleHeight(int i) {
        this.lastVisibleHeight = i;
        for (int i2 = 0; i2 < this.mediaPages.length; i2++) {
            float f = (-(getMeasuredHeight() - Math.max(i, AndroidUtilities.dp(this.mediaPages[i2].selectedType == 8 ? 280.0f : 120.0f)))) / 2.0f;
            this.mediaPages[i2].emptyView.setTranslationY(f);
            this.mediaPages[i2].progressView.setTranslationY(-f);
        }
        BotPreviewsEditContainer botPreviewsEditContainer = this.botPreviewsContainer;
        if (botPreviewsEditContainer != null) {
            botPreviewsEditContainer.setVisibleHeight(i);
        }
        if (this.giftsContainer != null) {
            int dp = i - AndroidUtilities.dp(48.0f);
            FragmentContextView fragmentContextView = this.fragmentContextView;
            if (fragmentContextView != null && fragmentContextView.getVisibility() == 0) {
                dp -= this.fragmentContextView.getHeight() - AndroidUtilities.dp(2.5f);
            }
            this.giftsContainer.setVisibleHeight(dp);
        }
    }

    public void showActionMode(final boolean z) {
        if (this.isActionModeShowed == z) {
            return;
        }
        this.isActionModeShowed = z;
        AnimatorSet animatorSet = this.actionModeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z) {
            this.actionModeLayout.setVisibility(0);
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.actionModeAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.actionModeLayout, (Property<LinearLayout, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.actionModeAnimation.setDuration(180L);
        this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                SharedMediaLayout.this.actionModeAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (SharedMediaLayout.this.actionModeAnimation == null) {
                    return;
                }
                SharedMediaLayout.this.actionModeAnimation = null;
                if (z) {
                    return;
                }
                SharedMediaLayout.this.actionModeLayout.setVisibility(4);
            }
        });
        this.actionModeAnimation.start();
        if (z) {
            updateStoriesPinButton();
        }
    }

    @Override
    public void showChatPreview(DialogCell dialogCell) {
    }

    public void showMediaCalendar(int r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.showMediaCalendar(int, boolean):void");
    }

    public void updateAdapters() {
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
        if (sharedDocumentsAdapter2 != null) {
            sharedDocumentsAdapter2.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
        if (sharedDocumentsAdapter3 != null) {
            sharedDocumentsAdapter3.notifyDataSetChanged();
        }
        GifAdapter gifAdapter = this.gifAdapter;
        if (gifAdapter != null) {
            gifAdapter.notifyDataSetChanged();
        }
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        if (storiesAdapter != null) {
            storiesAdapter.notifyDataSetChanged();
        }
    }

    public void updateFastScrollVisibility(MediaPage mediaPage, boolean z) {
        boolean z2 = mediaPage.fastScrollEnabled && this.isPinnedToTop;
        RecyclerListView.FastScroll fastScroll = mediaPage.listView.getFastScroll();
        ObjectAnimator objectAnimator = mediaPage.fastScrollAnimator;
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            mediaPage.fastScrollAnimator.cancel();
        }
        if (!z) {
            fastScroll.animate().setListener(null).cancel();
            fastScroll.setVisibility(z2 ? 0 : 8);
            fastScroll.setTag(z2 ? 1 : null);
            fastScroll.setAlpha(1.0f);
            fastScroll.setScaleX(1.0f);
            fastScroll.setScaleY(1.0f);
            return;
        }
        if (z2 && fastScroll.getTag() == null) {
            fastScroll.animate().setListener(null).cancel();
            if (fastScroll.getVisibility() != 0) {
                fastScroll.setVisibility(0);
                fastScroll.setAlpha(0.0f);
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(fastScroll, (Property<RecyclerListView.FastScroll, Float>) View.ALPHA, fastScroll.getAlpha(), 1.0f);
            mediaPage.fastScrollAnimator = ofFloat;
            ofFloat.setDuration(150L).start();
            fastScroll.setTag(r4);
            return;
        }
        if (z2 || fastScroll.getTag() == null) {
            return;
        }
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(fastScroll, (Property<RecyclerListView.FastScroll, Float>) View.ALPHA, fastScroll.getAlpha(), 0.0f);
        ofFloat2.addListener(new HideViewAfterAnimation(fastScroll));
        mediaPage.fastScrollAnimator = ofFloat2;
        ofFloat2.setDuration(150L).start();
        fastScroll.animate().setListener(null).cancel();
        fastScroll.setTag(null);
    }

    public void updateSearchItemIcon(float f) {
        if (this.searchItemIcon == null) {
            return;
        }
        MediaPage mediaPage = this.mediaPages[1];
        float f2 = 0.0f;
        if (mediaPage != null && mediaPage.selectedType == 11) {
            f2 = 0.0f + f;
        }
        MediaPage mediaPage2 = this.mediaPages[0];
        if (mediaPage2 != null && mediaPage2.selectedType == 11) {
            f2 += 1.0f - f;
        }
        this.searchItemIcon.setAlpha(f2);
        float f3 = (0.15f * f2) + 0.85f;
        this.searchItemIcon.setScaleX(f3);
        this.searchItemIcon.setScaleY(f3);
        this.searchItemIcon.setVisibility(f2 <= 0.01f ? 8 : 0);
    }

    public void updateSearchItemIconAnimated() {
        if (this.searchItemIcon == null) {
            return;
        }
        MediaPage mediaPage = this.mediaPages[1];
        final boolean z = mediaPage != null && mediaPage.selectedType == 11;
        if (z) {
            this.searchItemIcon.setVisibility(0);
        }
        this.searchItemIcon.animate().alpha(z ? 1.0f : 0.0f).scaleX(z ? 1.0f : 0.85f).scaleY(z ? 1.0f : 0.85f).withEndAction(new Runnable() {
            @Override
            public final void run() {
                SharedMediaLayout.this.lambda$updateSearchItemIconAnimated$0(z);
            }
        }).setDuration(420L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
    }

    public void updateStoriesList(StoriesController.StoriesList storiesList) {
        this.searchStoriesList = storiesList;
        StoriesAdapter storiesAdapter = this.storiesAdapter;
        storiesAdapter.storiesList = storiesList;
        storiesAdapter.notifyDataSetChanged();
        StoriesAdapter storiesAdapter2 = this.animationSupportingStoriesAdapter;
        storiesAdapter2.storiesList = storiesList;
        storiesAdapter2.notifyDataSetChanged();
    }

    public Boolean zoomIn() {
        if (this.photoVideoChangeColumnsAnimation) {
            return null;
        }
        MediaPage mediaPage = this.mediaPages[0];
        if (mediaPage == null) {
            return null;
        }
        int i = mediaPage.selectedType;
        this.changeColumnsTab = i;
        int i2 = (i == 8 || i == 9) ? 1 : 0;
        int nextMediaColumnsCount = getNextMediaColumnsCount(i2, this.mediaColumnsCount[i2], true);
        if (this.mediaZoomInItem != null && nextMediaColumnsCount == getNextMediaColumnsCount(i2, nextMediaColumnsCount, true)) {
            this.mediaZoomInItem.setEnabled(false);
            this.mediaZoomInItem.animate().alpha(0.5f).start();
        }
        if (this.mediaColumnsCount[i2] != nextMediaColumnsCount) {
            ActionBarMenuSubItem actionBarMenuSubItem = this.mediaZoomOutItem;
            if (actionBarMenuSubItem != null && !actionBarMenuSubItem.isEnabled()) {
                this.mediaZoomOutItem.setEnabled(true);
                this.mediaZoomOutItem.animate().alpha(1.0f).start();
            }
            if (i2 == 0) {
                SharedConfig.setMediaColumnsCount(nextMediaColumnsCount);
            } else if (getStoriesCount(this.mediaPages[0].selectedType) >= 5) {
                SharedConfig.setStoriesColumnsCount(nextMediaColumnsCount);
            }
            animateToMediaColumnsCount(nextMediaColumnsCount);
        }
        return Boolean.valueOf(nextMediaColumnsCount != getNextMediaColumnsCount(i2, nextMediaColumnsCount, true));
    }

    public Boolean zoomOut() {
        if (this.photoVideoChangeColumnsAnimation) {
            return null;
        }
        MediaPage mediaPage = this.mediaPages[0];
        if (mediaPage == null) {
            return null;
        }
        if (this.allowStoriesSingleColumn && (mediaPage.selectedType == 8 || this.mediaPages[0].selectedType == 9)) {
            return null;
        }
        int i = this.mediaPages[0].selectedType;
        this.changeColumnsTab = i;
        int i2 = (i == 8 || i == 9) ? 1 : 0;
        int nextMediaColumnsCount = getNextMediaColumnsCount(i2, this.mediaColumnsCount[i2], false);
        if (this.mediaZoomOutItem != null && nextMediaColumnsCount == getNextMediaColumnsCount(i2, nextMediaColumnsCount, false)) {
            this.mediaZoomOutItem.setEnabled(false);
            this.mediaZoomOutItem.animate().alpha(0.5f).start();
        }
        if (this.mediaColumnsCount[i2] != nextMediaColumnsCount) {
            ActionBarMenuSubItem actionBarMenuSubItem = this.mediaZoomInItem;
            if (actionBarMenuSubItem != null && !actionBarMenuSubItem.isEnabled()) {
                this.mediaZoomInItem.setEnabled(true);
                this.mediaZoomInItem.animate().alpha(1.0f).start();
            }
            if (i2 == 0) {
                SharedConfig.setMediaColumnsCount(nextMediaColumnsCount);
            } else if (getStoriesCount(this.mediaPages[0].selectedType) >= 5) {
                SharedConfig.setStoriesColumnsCount(nextMediaColumnsCount);
            }
            animateToMediaColumnsCount(nextMediaColumnsCount);
        }
        return Boolean.valueOf(nextMediaColumnsCount != getNextMediaColumnsCount(i2, nextMediaColumnsCount, false));
    }
}
