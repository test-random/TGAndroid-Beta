package org.telegram.ui.Stories;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Stories.SelfStoriesPreviewView;
import org.telegram.ui.Stories.SelfStoryViewsPage;
import org.telegram.ui.Stories.SelfStoryViewsView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoryViewer;

public class SelfStoryViewsView extends FrameLayout {
    public float bottomPadding;
    private int currentState;
    private long dialogId;
    ArrayList itemViews;
    int keyboardHeight;
    boolean listenPager;
    public float maxSelfStoriesViewsOffset;
    private final PagerAdapter pagerAdapter;
    float progressToKeyboard;
    float progressToOpen;
    Theme.ResourcesProvider resourcesProvider;
    SelfStoriesPreviewView selfStoriesPreviewView;
    float selfStoriesViewsOffset;
    private Drawable shadowDrawable;
    SelfStoryViewsPage.FiltersState sharedFilterState;
    ArrayList storyItems;
    StoryViewer storyViewer;
    float toHeight;
    public float toY;
    ViewPagerInner viewPager;
    ContainerView viewPagerContainer;

    public class AnonymousClass4 extends PagerAdapter {
        final Context val$context;
        final StoryViewer val$storyViewer;

        AnonymousClass4(StoryViewer storyViewer, Context context) {
            this.val$storyViewer = storyViewer;
            this.val$context = context;
        }

        public void lambda$instantiateItem$0(SelfStoryViewsPage selfStoryViewsPage) {
            for (int i = 0; i < SelfStoryViewsView.this.itemViews.size(); i++) {
                if (selfStoryViewsPage != SelfStoryViewsView.this.itemViews.get(i)) {
                    ((SelfStoryViewsPage) SelfStoryViewsView.this.itemViews.get(i)).updateSharedState();
                }
            }
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
            SelfStoryViewsView.this.itemViews.remove(obj);
        }

        @Override
        public int getCount() {
            return SelfStoryViewsView.this.storyItems.size();
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            SelfStoryViewsPage selfStoryViewsPage = new SelfStoryViewsPage(this.val$storyViewer, this.val$context, SelfStoryViewsView.this.sharedFilterState, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    SelfStoryViewsView.AnonymousClass4.this.lambda$instantiateItem$0((SelfStoryViewsPage) obj);
                }
            }) {
                @Override
                public void onTopOffsetChanged(int i2) {
                    super.onTopOffsetChanged(i2);
                    if (((Integer) getTag()).intValue() == SelfStoryViewsView.this.viewPager.getCurrentItem()) {
                        float f = i2;
                        SelfStoryViewsView.this.selfStoriesPreviewView.setAlpha(Utilities.clamp(f / SelfStoryViewsView.this.bottomPadding, 1.0f, 0.0f));
                        SelfStoryViewsView selfStoryViewsView = SelfStoryViewsView.this;
                        selfStoryViewsView.selfStoriesPreviewView.setTranslationY((-(selfStoryViewsView.bottomPadding - f)) / 2.0f);
                    }
                }
            };
            selfStoryViewsPage.setTag(Integer.valueOf(i));
            selfStoryViewsPage.setShadowDrawable(SelfStoryViewsView.this.shadowDrawable);
            selfStoryViewsPage.setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
            selfStoryViewsPage.setStoryItem(SelfStoryViewsView.this.dialogId, (StoryItemInternal) SelfStoryViewsView.this.storyItems.get(i));
            selfStoryViewsPage.setListBottomPadding(SelfStoryViewsView.this.bottomPadding);
            viewGroup.addView(selfStoryViewsPage);
            SelfStoryViewsView.this.itemViews.add(selfStoryViewsPage);
            return selfStoryViewsPage;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    public class ContainerView extends FrameLayout implements NestedScrollingParent3 {
        private final NestedScrollingParentHelper nestedScrollingParentHelper;

        public ContainerView(Context context) {
            super(context);
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        @Override
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
            SelfStoryViewsView selfStoryViewsView = SelfStoryViewsView.this;
            if (selfStoryViewsView.keyboardHeight > 0) {
                return;
            }
            float f = selfStoryViewsView.storyViewer.selfStoriesViewsOffset;
            float f2 = selfStoryViewsView.maxSelfStoriesViewsOffset;
            if (f >= f2 || i2 <= 0) {
                return;
            }
            float f3 = f + i2;
            iArr[1] = i2;
            if (f3 <= f2) {
                f2 = f3;
            }
            selfStoryViewsView.setOffset(f2);
            SelfStoryViewsView.this.storyViewer.setSelfStoriesViewsOffset(f2);
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            SelfStoryViewsView selfStoryViewsView = SelfStoryViewsView.this;
            if (selfStoryViewsView.keyboardHeight <= 0 && i4 != 0 && i2 == 0) {
                float f = selfStoryViewsView.storyViewer.selfStoriesViewsOffset;
                float f2 = i4 + f;
                if (f2 <= f) {
                    f = f2;
                }
                selfStoryViewsView.setOffset(f);
                SelfStoryViewsView.this.storyViewer.setSelfStoriesViewsOffset(f);
            }
        }

        @Override
        public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        }

        @Override
        public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
            return SelfStoryViewsView.this.keyboardHeight <= 0 && i == 2;
        }

        @Override
        public void onStopNestedScroll(View view, int i) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
        }
    }

    public class StoryItemInternal {
        public TL_stories.StoryItem storyItem;
        public StoriesController.UploadingStory uploadingStory;

        public StoryItemInternal(TL_stories.StoryItem storyItem) {
            this.storyItem = storyItem;
        }

        public StoryItemInternal(StoriesController.UploadingStory uploadingStory) {
            this.uploadingStory = uploadingStory;
        }
    }

    public class ViewPagerInner extends ViewPager {
        boolean gesturesEnabled;

        public ViewPagerInner(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.gesturesEnabled = true;
            }
            if (this.gesturesEnabled && SelfStoryViewsView.this.keyboardHeight <= 0) {
                try {
                    return super.onInterceptTouchEvent(motionEvent);
                } catch (Exception unused) {
                }
            }
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.gesturesEnabled = true;
            }
            if (!this.gesturesEnabled || SelfStoryViewsView.this.keyboardHeight > 0) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    public SelfStoryViewsView(Context context, final StoryViewer storyViewer) {
        super(context);
        this.storyItems = new ArrayList();
        this.itemViews = new ArrayList();
        this.sharedFilterState = new SelfStoryViewsPage.FiltersState();
        this.resourcesProvider = storyViewer.resourcesProvider;
        this.storyViewer = storyViewer;
        this.selfStoriesPreviewView = new SelfStoriesPreviewView(getContext()) {
            @Override
            public void onCenteredImageTap() {
                storyViewer.cancelSwipeToViews(false);
            }

            @Override
            public void onClosestPositionChanged(int i) {
                StoryViewer.PlaceProvider placeProvider;
                super.onClosestPositionChanged(i);
                SelfStoryViewsView selfStoryViewsView = SelfStoryViewsView.this;
                if (selfStoryViewsView.listenPager) {
                    return;
                }
                if (selfStoryViewsView.viewPager.getCurrentItem() != i) {
                    try {
                        SelfStoryViewsView.this.viewPager.setCurrentItem(i, false);
                    } catch (Throwable th) {
                        FileLog.e(th);
                        SelfStoryViewsView.this.viewPager.getAdapter().notifyDataSetChanged();
                        SelfStoryViewsView.this.viewPager.setCurrentItem(i, false);
                    }
                }
                StoryViewer storyViewer2 = storyViewer;
                if (storyViewer2.storiesList == null || (placeProvider = storyViewer2.placeProvider) == null) {
                    return;
                }
                if (i < 10) {
                    placeProvider.loadNext(false);
                } else if (i >= this.storyItems.size() - 10) {
                    storyViewer.placeProvider.loadNext(true);
                }
            }

            @Override
            void onDragging() {
                SelfStoryViewsView.this.listenPager = false;
            }
        };
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
        this.viewPagerContainer = new ContainerView(context);
        ViewPagerInner viewPagerInner = new ViewPagerInner(context) {
            private boolean checkTopOffset(MotionEvent motionEvent) {
                return motionEvent.getY() < SelfStoryViewsView.this.getCurrentTopOffset();
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (checkTopOffset(motionEvent) && motionEvent.getAction() == 0) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (!checkTopOffset(motionEvent) && Math.abs(SelfStoryViewsView.this.getCurrentTopOffset() - SelfStoryViewsView.this.bottomPadding) <= AndroidUtilities.dp(1.0f)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                return false;
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!checkTopOffset(motionEvent) && Math.abs(SelfStoryViewsView.this.getCurrentTopOffset() - SelfStoryViewsView.this.bottomPadding) <= AndroidUtilities.dp(1.0f)) {
                    return super.onTouchEvent(motionEvent);
                }
                return false;
            }
        };
        this.viewPager = viewPagerInner;
        viewPagerInner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i) {
                SelfStoryViewsView.this.currentState = i;
                if (SelfStoryViewsView.this.currentState == 1) {
                    SelfStoryViewsView.this.listenPager = true;
                }
            }

            @Override
            public void onPageScrolled(int i, float f, int i2) {
                SelfStoryViewsView selfStoryViewsView = SelfStoryViewsView.this;
                if (selfStoryViewsView.listenPager) {
                    selfStoryViewsView.selfStoriesPreviewView.scrollToPositionWithOffset(i, f);
                }
            }

            @Override
            public void onPageSelected(int i) {
            }
        });
        ViewPagerInner viewPagerInner2 = this.viewPager;
        AnonymousClass4 anonymousClass4 = new AnonymousClass4(storyViewer, context);
        this.pagerAdapter = anonymousClass4;
        viewPagerInner2.setAdapter(anonymousClass4);
        this.viewPagerContainer.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        addView(this.selfStoriesPreviewView, LayoutHelper.createFrame(-1, -1.0f));
        addView(this.viewPagerContainer);
        setVisibility(4);
    }

    public float getCurrentTopOffset() {
        float f = this.bottomPadding;
        SelfStoryViewsPage currentPage = getCurrentPage();
        return currentPage != null ? currentPage.getTopOffset() : f;
    }

    public void lambda$setKeyboardHeight$0(ValueAnimator valueAnimator) {
        this.progressToKeyboard = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateTranslation();
    }

    private void updateTranslation() {
        this.viewPagerContainer.setTranslationY(((-this.bottomPadding) + getMeasuredHeight()) - this.selfStoriesViewsOffset);
    }

    public SelfStoriesPreviewView.ImageHolder getCrossfadeToImage() {
        return this.selfStoriesPreviewView.getCenteredImageReciever();
    }

    public SelfStoryViewsPage getCurrentPage() {
        for (int i = 0; i < this.itemViews.size(); i++) {
            if (((Integer) ((SelfStoryViewsPage) this.itemViews.get(i)).getTag()).intValue() == this.viewPager.getCurrentItem()) {
                return (SelfStoryViewsPage) this.itemViews.get(i);
            }
        }
        return null;
    }

    public TL_stories.StoryItem getSelectedStory() {
        int closestPosition = this.selfStoriesPreviewView.getClosestPosition();
        if (closestPosition < 0 || closestPosition >= this.storyItems.size()) {
            return null;
        }
        return ((StoryItemInternal) this.storyItems.get(closestPosition)).storyItem;
    }

    public boolean onBackPressed() {
        if (this.keyboardHeight > 0) {
            AndroidUtilities.hideKeyboard(this);
            return true;
        }
        SelfStoryViewsPage currentPage = getCurrentPage();
        if (currentPage != null) {
            return currentPage.onBackPressed();
        }
        return false;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3 = this.storyViewer.ATTACH_TO_FRAGMENT ? AndroidUtilities.statusBarHeight : 0;
        int size = View.MeasureSpec.getSize(i2);
        ((FrameLayout.LayoutParams) this.selfStoriesPreviewView.getLayoutParams()).topMargin = i3;
        this.toHeight = this.selfStoriesPreviewView.getFinalHeight();
        this.toY = AndroidUtilities.dp(20.0f) + i3;
        ((FrameLayout.LayoutParams) this.viewPagerContainer.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
        float dp = (((i3 + AndroidUtilities.dp(20.0f)) + this.toHeight) + AndroidUtilities.dp(24.0f)) - AndroidUtilities.statusBarHeight;
        this.bottomPadding = dp;
        this.maxSelfStoriesViewsOffset = size - dp;
        for (int i4 = 0; i4 < this.itemViews.size(); i4++) {
            ((SelfStoryViewsPage) this.itemViews.get(i4)).setListBottomPadding(this.bottomPadding);
        }
        super.onMeasure(i, i2);
    }

    public void setItems(long j, ArrayList arrayList, int i) {
        this.storyItems.clear();
        this.dialogId = j;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            this.storyItems.add(new StoryItemInternal((TL_stories.StoryItem) arrayList.get(i2)));
        }
        ArrayList uploadingStories = MessagesController.getInstance(this.storyViewer.currentAccount).storiesController.getUploadingStories(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId());
        if (uploadingStories != null) {
            for (int i3 = 0; i3 < uploadingStories.size(); i3++) {
                this.storyItems.add(new StoryItemInternal((StoriesController.UploadingStory) uploadingStories.get(i3)));
            }
        }
        this.selfStoriesPreviewView.setItems(this.storyItems, i);
        this.viewPager.setAdapter(null);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.pagerAdapter.notifyDataSetChanged();
        this.viewPager.setCurrentItem(i);
    }

    public void setKeyboardHeight(int i) {
        SelfStoryViewsPage currentPage;
        boolean z = this.keyboardHeight >= AndroidUtilities.dp(20.0f);
        boolean z2 = i >= AndroidUtilities.dp(20.0f);
        if (z2 != z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToKeyboard, z2 ? 1.0f : 0.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SelfStoryViewsView.this.lambda$setKeyboardHeight$0(valueAnimator);
                }
            });
            ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
            ofFloat.setDuration(250L);
            ofFloat.start();
        }
        this.keyboardHeight = i;
        if (i <= 0 || (currentPage = getCurrentPage()) == null) {
            return;
        }
        currentPage.onKeyboardShown();
    }

    public void setOffset(float f) {
        if (this.selfStoriesViewsOffset == f) {
            return;
        }
        this.selfStoriesViewsOffset = f;
        updateTranslation();
        float f2 = this.progressToOpen;
        float clamp = Utilities.clamp(f / this.maxSelfStoriesViewsOffset, 1.0f, 0.0f);
        this.progressToOpen = clamp;
        Utilities.clamp(clamp / 0.5f, 1.0f, 0.0f);
        PeerStoriesView currentPeerView = this.storyViewer.getCurrentPeerView();
        if (f2 == 1.0f && this.progressToOpen != 1.0f) {
            if (this.storyViewer.storiesList != null) {
                MessageObject messageObject = (MessageObject) this.storyViewer.storiesList.messageObjects.get(Utilities.clamp(this.selfStoriesPreviewView.getClosestPosition(), this.storyViewer.storiesList.messageObjects.size() - 1, 0));
                long day = StoriesController.StoriesList.day(messageObject);
                ImageReceiver imageReceiver = this.storyViewer.transitionViewHolder.storyImage;
                if (imageReceiver != null) {
                    imageReceiver.setVisible(true, true);
                    this.storyViewer.transitionViewHolder.storyImage = null;
                }
                this.storyViewer.storiesViewPager.setCurrentDate(day, messageObject.storyItem.id);
            } else if (currentPeerView != null) {
                currentPeerView.selectPosition(this.selfStoriesPreviewView.getClosestPosition());
            }
            this.selfStoriesPreviewView.abortScroll();
        }
        if (currentPeerView != null) {
            this.selfStoriesPreviewView.imagesFromY = currentPeerView.storyContainer.getTop();
            this.selfStoriesPreviewView.imagesFromW = currentPeerView.storyContainer.getMeasuredWidth();
            this.selfStoriesPreviewView.imagesFromH = currentPeerView.storyContainer.getMeasuredHeight();
        }
        this.selfStoriesPreviewView.setProgressToOpen(this.progressToOpen);
        ViewPagerInner viewPagerInner = this.viewPager;
        if (viewPagerInner.gesturesEnabled && this.progressToOpen != 1.0f) {
            viewPagerInner.onTouchEvent(AndroidUtilities.emptyMotionEvent());
        }
        setVisibility(this.progressToOpen == 0.0f ? 4 : 0);
        if (this.progressToOpen != 1.0f) {
            this.viewPager.gesturesEnabled = false;
        }
    }
}
