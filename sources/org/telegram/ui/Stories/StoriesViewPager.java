package org.telegram.ui.Stories;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Stories.PeerStoriesView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesViewPager;
import org.telegram.ui.Stories.StoryViewer;

public abstract class StoriesViewPager extends ViewPager {
    int currentAccount;
    public int currentState;
    ArrayList days;
    long daysDialogId;
    PeerStoriesView.Delegate delegate;
    ArrayList dialogs;
    public boolean dissallowInterceptCalled;
    Runnable doOnNextIdle;
    int keyboardHeight;
    float lastProgressToDismiss;
    Runnable lockTouchRunnable;
    PagerAdapter pagerAdapter;
    float progress;
    PeerStoriesView.SharedResources resources;
    int selectedPosition;
    private int selectedPositionInPage;
    StoryViewer storyViewer;
    int toPosition;
    boolean touchEnabled;
    private boolean touchLocked;
    boolean updateDelegate;
    private int updateVisibleItemPosition;

    public class PageLayout extends FrameLayout {
        ArrayList day;
        long dialogId;
        boolean isVisible;
        public PeerStoriesView peerStoryView;

        public PageLayout(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.isVisible) {
                super.dispatchDraw(canvas);
            }
        }

        public void setVisible(boolean z) {
            if (this.isVisible != z) {
                this.isVisible = z;
                invalidate();
                this.peerStoryView.setIsVisible(z);
                StoriesViewPager.this.checkAllowScreenshots();
            }
        }
    }

    public StoriesViewPager(final Context context, final StoryViewer storyViewer, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.dialogs = new ArrayList();
        this.touchEnabled = true;
        this.lockTouchRunnable = new Runnable() {
            @Override
            public void run() {
                StoriesViewPager.this.touchLocked = false;
            }
        };
        this.updateVisibleItemPosition = -1;
        this.resources = new PeerStoriesView.SharedResources(context);
        this.storyViewer = storyViewer;
        PagerAdapter pagerAdapter = new PagerAdapter() {
            private final ArrayList cachedViews = new ArrayList();

            @Override
            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                FrameLayout frameLayout = (FrameLayout) obj;
                viewGroup.removeView(frameLayout);
                PeerStoriesView peerStoriesView = (PeerStoriesView) frameLayout.getChildAt(0);
                AndroidUtilities.removeFromParent(peerStoriesView);
                this.cachedViews.add(peerStoriesView);
            }

            @Override
            public int getCount() {
                StoriesViewPager storiesViewPager = StoriesViewPager.this;
                ArrayList arrayList = storiesViewPager.days;
                return arrayList != null ? arrayList.size() : storiesViewPager.dialogs.size();
            }

            @Override
            public Object instantiateItem(ViewGroup viewGroup, int i) {
                PeerStoriesView peerStoriesView;
                long longValue;
                MessageObject findMessageObject;
                PageLayout pageLayout = new PageLayout(context);
                if (this.cachedViews.isEmpty()) {
                    peerStoriesView = new HwPeerStoriesView(context, storyViewer, StoriesViewPager.this.resources, resourcesProvider) {
                        @Override
                        public boolean isSelectedPeer() {
                            return getParent() != null && ((Integer) ((View) getParent()).getTag()).intValue() == StoriesViewPager.this.getCurrentItem();
                        }
                    };
                } else {
                    peerStoriesView = (PeerStoriesView) this.cachedViews.remove(0);
                    peerStoriesView.reset();
                }
                pageLayout.peerStoryView = peerStoriesView;
                peerStoriesView.setAccount(StoriesViewPager.this.currentAccount);
                peerStoriesView.setDelegate(StoriesViewPager.this.delegate);
                peerStoriesView.setLongpressed(storyViewer.isLongpressed);
                pageLayout.setTag(Integer.valueOf(i));
                StoriesViewPager storiesViewPager = StoriesViewPager.this;
                ArrayList arrayList = storiesViewPager.days;
                if (arrayList != null) {
                    if (storyViewer.reversed) {
                        i = (arrayList.size() - 1) - i;
                    }
                    ArrayList arrayList2 = (ArrayList) arrayList.get(i);
                    pageLayout.day = arrayList2;
                    StoriesController.StoriesList storiesList = storyViewer.storiesList;
                    longValue = (!(storiesList instanceof StoriesController.SearchStoriesList) || (findMessageObject = storiesList.findMessageObject(((Integer) arrayList2.get(0)).intValue())) == null) ? StoriesViewPager.this.daysDialogId : findMessageObject.getDialogId();
                } else {
                    pageLayout.day = null;
                    longValue = ((Long) storiesViewPager.dialogs.get(i)).longValue();
                }
                pageLayout.dialogId = longValue;
                pageLayout.addView(peerStoriesView);
                peerStoriesView.requestLayout();
                viewGroup.addView(pageLayout);
                return pageLayout;
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }
        };
        this.pagerAdapter = pagerAdapter;
        setAdapter(pagerAdapter);
        setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public final void transformPage(View view, float f) {
                StoriesViewPager.this.lambda$new$1(view, f);
            }
        });
        setOffscreenPageLimit(0);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i) {
                StoriesViewPager.this.delegate.setAllowTouchesByViewPager(i != 0);
                Runnable runnable = StoriesViewPager.this.doOnNextIdle;
                if (runnable != null && i == 0) {
                    runnable.run();
                    StoriesViewPager.this.doOnNextIdle = null;
                }
                StoriesViewPager storiesViewPager = StoriesViewPager.this;
                storiesViewPager.currentState = i;
                storiesViewPager.onStateChanged();
            }

            @Override
            public void onPageScrolled(int r3, float r4, int r5) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.StoriesViewPager.AnonymousClass3.onPageScrolled(int, float, int):void");
            }

            @Override
            public void onPageSelected(int i) {
                PeerStoriesView currentPeerView = StoriesViewPager.this.getCurrentPeerView();
                if (currentPeerView == null) {
                    return;
                }
                StoriesViewPager.this.delegate.onPeerSelected(currentPeerView.getCurrentPeer(), currentPeerView.getSelectedPosition());
                StoriesViewPager.this.updateActiveStory();
                StoryViewer.PlaceProvider placeProvider = storyViewer.placeProvider;
                if (placeProvider != null) {
                    if (i < 3) {
                        placeProvider.loadNext(false);
                    } else if (i > StoriesViewPager.this.pagerAdapter.getCount() - 4) {
                        storyViewer.placeProvider.loadNext(true);
                    }
                }
            }
        });
        setOverScrollMode(2);
    }

    public static boolean eq(ArrayList arrayList, ArrayList arrayList2) {
        if (arrayList == null && arrayList2 == null) {
            return true;
        }
        if (arrayList == null || arrayList2 == null || arrayList.size() != arrayList2.size()) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) != arrayList2.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean eqA(ArrayList arrayList, ArrayList arrayList2) {
        if (arrayList == null && arrayList2 == null) {
            return true;
        }
        if (arrayList == null || arrayList2 == null || arrayList.size() != arrayList2.size()) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (!eq((ArrayList) arrayList.get(i), (ArrayList) arrayList2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static void lambda$new$0(PageLayout pageLayout) {
        ArrayList arrayList = pageLayout.day;
        if (arrayList != null) {
            pageLayout.peerStoryView.day = arrayList;
        }
        pageLayout.peerStoryView.preloadMainImage(pageLayout.dialogId);
    }

    public void lambda$new$1(View view, float f) {
        final PageLayout pageLayout = (PageLayout) view;
        if (Math.abs(f) >= 1.0f) {
            pageLayout.setVisible(false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StoriesViewPager.lambda$new$0(StoriesViewPager.PageLayout.this);
                }
            }, 16L);
            return;
        }
        if (!pageLayout.isVisible) {
            pageLayout.setVisible(true);
            if (this.days != null) {
                pageLayout.peerStoryView.setDay(pageLayout.dialogId, pageLayout.day, -1);
            } else {
                pageLayout.peerStoryView.setDialogId(pageLayout.dialogId, -1);
            }
        }
        pageLayout.peerStoryView.setOffset(f);
        view.setCameraDistance(view.getWidth() * 15);
        view.setPivotX(f < 0.0f ? view.getWidth() : 0.0f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(f * 90.0f);
    }

    public void updateActiveStory() {
        for (int i = 0; i < getChildCount(); i++) {
            PeerStoriesView peerStoriesView = (PeerStoriesView) ((FrameLayout) getChildAt(i)).getChildAt(0);
            peerStoriesView.setActive(((Integer) getChildAt(i).getTag()).intValue() == getCurrentItem() && !peerStoriesView.editOpened);
        }
    }

    public boolean canScroll(float f) {
        int i = this.selectedPosition;
        if (i == 0 && this.progress == 0.0f && f < 0.0f) {
            return false;
        }
        return (i == getAdapter().getCount() - 1 && this.progress == 0.0f && f > 0.0f) ? false : true;
    }

    public void checkAllowScreenshots() {
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                z = true;
                break;
            }
            PageLayout pageLayout = (PageLayout) getChildAt(i);
            if (pageLayout.isVisible && !pageLayout.peerStoryView.currentStory.allowScreenshots()) {
                break;
            } else {
                i++;
            }
        }
        this.storyViewer.allowScreenshots(z);
    }

    public void checkPageVisibility() {
        if (this.updateVisibleItemPosition >= 0) {
            for (int i = 0; i < getChildCount(); i++) {
                if (((Integer) getChildAt(i).getTag()).intValue() == getCurrentItem() && getCurrentItem() == this.updateVisibleItemPosition) {
                    PageLayout pageLayout = (PageLayout) getChildAt(i);
                    if (!pageLayout.isVisible) {
                        this.updateVisibleItemPosition = -1;
                        pageLayout.setVisible(true);
                        if (this.days != null) {
                            pageLayout.peerStoryView.setDay(pageLayout.dialogId, pageLayout.day, this.selectedPositionInPage);
                        } else {
                            pageLayout.peerStoryView.setDialogId(pageLayout.dialogId, this.selectedPositionInPage);
                        }
                    }
                }
            }
        }
    }

    public void enableTouch(boolean z) {
        this.touchEnabled = z;
    }

    public long getCurrentDialogId() {
        if (this.days != null) {
            return this.daysDialogId;
        }
        if (getCurrentItem() < this.dialogs.size()) {
            return ((Long) this.dialogs.get(getCurrentItem())).longValue();
        }
        return 0L;
    }

    public PeerStoriesView getCurrentPeerView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (((Integer) getChildAt(i).getTag()).intValue() == getCurrentItem()) {
                return (PeerStoriesView) ((FrameLayout) getChildAt(i)).getChildAt(0);
            }
        }
        return null;
    }

    public ArrayList<Long> getDialogIds() {
        return this.dialogs;
    }

    public void lockTouchEvent(long j) {
        this.touchLocked = true;
        onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
        AndroidUtilities.cancelRunOnUIThread(this.lockTouchRunnable);
        AndroidUtilities.runOnUIThread(this.lockTouchRunnable, j);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.touchEnabled && !this.touchLocked) {
            try {
                return super.onInterceptTouchEvent(motionEvent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return false;
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.updateDelegate) {
            this.updateDelegate = false;
            PeerStoriesView currentPeerView = getCurrentPeerView();
            if (currentPeerView != null) {
                this.delegate.onPeerSelected(currentPeerView.getCurrentPeer(), currentPeerView.getSelectedPosition());
            }
        }
        checkPageVisibility();
        updateActiveStory();
    }

    public void onNextIdle(Runnable runnable) {
        this.doOnNextIdle = runnable;
    }

    public abstract void onStateChanged();

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.touchEnabled && !this.touchLocked) {
            return super.onTouchEvent(motionEvent);
        }
        if (this.touchLocked) {
            return motionEvent.getAction() == 0 || motionEvent.getAction() == 2;
        }
        return false;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (z) {
            this.dissallowInterceptCalled = true;
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void setCurrentDate(long j, int i) {
        for (int i2 = 0; i2 < this.days.size(); i2++) {
            if (j == StoriesController.StoriesList.day(this.storyViewer.storiesList.findMessageObject(((Integer) ((ArrayList) this.days.get(i2)).get(0)).intValue()))) {
                int size = this.storyViewer.reversed ? (this.days.size() - 1) - i2 : i2;
                int i3 = 0;
                while (true) {
                    if (i3 >= ((ArrayList) this.days.get(i2)).size()) {
                        i3 = 0;
                        break;
                    } else if (((Integer) ((ArrayList) this.days.get(i2)).get(i3)).intValue() == i) {
                        break;
                    } else {
                        i3++;
                    }
                }
                if (getCurrentPeerView() != null && getCurrentItem() == size) {
                    getCurrentPeerView().selectPosition(i3);
                    return;
                }
                setCurrentItem(size, false);
                PeerStoriesView currentPeerView = getCurrentPeerView();
                if (currentPeerView != null) {
                    PageLayout pageLayout = (PageLayout) currentPeerView.getParent();
                    pageLayout.setVisible(true);
                    if (this.days != null) {
                        pageLayout.peerStoryView.setDay(pageLayout.dialogId, pageLayout.day, i3);
                        return;
                    } else {
                        pageLayout.peerStoryView.setDialogId(pageLayout.dialogId, i3);
                        return;
                    }
                }
                return;
            }
        }
    }

    public void setDays(long j, ArrayList arrayList, int i) {
        if (this.daysDialogId == j && eqA(this.days, arrayList) && this.currentAccount == i) {
            return;
        }
        this.daysDialogId = j;
        this.days = arrayList;
        this.currentAccount = i;
        setAdapter(null);
        setAdapter(this.pagerAdapter);
        int i2 = 0;
        while (i2 < arrayList.size() && !((ArrayList) arrayList.get(i2)).contains(Integer.valueOf(this.storyViewer.dayStoryId))) {
            i2++;
        }
        if (this.storyViewer.reversed) {
            i2 = (arrayList.size() - 1) - i2;
        }
        setCurrentItem(i2);
        this.updateDelegate = true;
    }

    public void setDelegate(PeerStoriesView.Delegate delegate) {
        this.delegate = delegate;
    }

    public void setHorizontalProgressToDismiss(float f) {
        if (Math.abs(f) > 1.0f || this.lastProgressToDismiss == f) {
            return;
        }
        this.lastProgressToDismiss = f;
        setCameraDistance(getWidth() * 15);
        setPivotX(f < 0.0f ? getWidth() : 0.0f);
        setPivotY(getHeight() * 0.5f);
        setRotationY(f * 90.0f);
    }

    public void setKeyboardHeight(int i) {
        if (this.keyboardHeight != i) {
            this.keyboardHeight = i;
            PeerStoriesView currentPeerView = getCurrentPeerView();
            if (currentPeerView != null) {
                currentPeerView.requestLayout();
            }
        }
    }

    public void setPaused(boolean z) {
        for (int i = 0; i < getChildCount(); i++) {
            ((PeerStoriesView) ((FrameLayout) getChildAt(i)).getChildAt(0)).setPaused(z);
        }
    }

    public void setPeerIds(ArrayList arrayList, int i, int i2) {
        this.dialogs = arrayList;
        this.currentAccount = i;
        setAdapter(null);
        setAdapter(this.pagerAdapter);
        setCurrentItem(i2);
        this.updateDelegate = true;
    }

    public boolean switchToNext(boolean z) {
        int currentItem;
        if (z) {
            int currentItem2 = getCurrentItem();
            ArrayList arrayList = this.days;
            if (arrayList == null) {
                arrayList = this.dialogs;
            }
            if (currentItem2 < arrayList.size() - 1) {
                currentItem = getCurrentItem() + 1;
                setCurrentItem(currentItem, !useSurfaceInViewPagerWorkAround());
                return true;
            }
        }
        if (z || getCurrentItem() <= 0) {
            return false;
        }
        currentItem = getCurrentItem() - 1;
        setCurrentItem(currentItem, !useSurfaceInViewPagerWorkAround());
        return true;
    }

    public boolean useSurfaceInViewPagerWorkAround() {
        return this.storyViewer.USE_SURFACE_VIEW && Build.VERSION.SDK_INT < 33;
    }
}
