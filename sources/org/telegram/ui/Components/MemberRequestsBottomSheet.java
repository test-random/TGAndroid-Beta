package org.telegram.ui.Components;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Delegates.MemberRequestsDelegate;
import org.telegram.ui.LaunchActivity;

public abstract class MemberRequestsBottomSheet extends UsersAlertBase {
    private final FlickerLoadingView currentLoadingView;
    private final MemberRequestsDelegate delegate;
    private boolean enterEventSent;
    private final StickerEmptyView membersEmptyView;
    private final StickerEmptyView membersSearchEmptyView;
    private final int touchSlop;
    private float yOffset;

    public MemberRequestsBottomSheet(BaseFragment baseFragment, long j) {
        super(baseFragment.getParentActivity(), false, baseFragment.getCurrentAccount(), baseFragment.getResourceProvider());
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.needSnapToTop = false;
        this.isEmptyViewVisible = false;
        MemberRequestsDelegate memberRequestsDelegate = new MemberRequestsDelegate(baseFragment, this.container, j, false) {
            @Override
            public void onImportersChanged(String str, boolean z, boolean z2) {
                if (!hasAllImporters()) {
                    if (MemberRequestsBottomSheet.this.membersEmptyView.getVisibility() != 4) {
                        MemberRequestsBottomSheet.this.membersEmptyView.setVisibility(4);
                    }
                } else if (z2) {
                    MemberRequestsBottomSheet.this.searchView.searchEditText.setText("");
                } else {
                    super.onImportersChanged(str, z, z2);
                }
            }
        };
        this.delegate = memberRequestsDelegate;
        memberRequestsDelegate.setShowLastItemDivider(false);
        setDimBehindAlpha(75);
        this.searchView.searchEditText.setHint(LocaleController.getString(R.string.SearchMemberRequests));
        MemberRequestsDelegate.Adapter adapter = memberRequestsDelegate.getAdapter();
        this.listViewAdapter = adapter;
        this.searchListViewAdapter = adapter;
        this.listView.setAdapter(adapter);
        memberRequestsDelegate.setRecyclerView(this.listView);
        int indexOfChild = ((ViewGroup) this.listView.getParent()).indexOfChild(this.listView);
        FlickerLoadingView loadingView = memberRequestsDelegate.getLoadingView();
        this.currentLoadingView = loadingView;
        this.containerView.addView(loadingView, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView emptyView = memberRequestsDelegate.getEmptyView();
        this.membersEmptyView = emptyView;
        this.containerView.addView(emptyView, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView searchEmptyView = memberRequestsDelegate.getSearchEmptyView();
        this.membersSearchEmptyView = searchEmptyView;
        this.containerView.addView(searchEmptyView, indexOfChild, LayoutHelper.createFrame(-1, -1.0f));
        memberRequestsDelegate.lambda$new$8();
    }

    public void lambda$onSearchViewTouched$1(final EditTextBoldCursor editTextBoldCursor) {
        setFocusable(true);
        editTextBoldCursor.requestFocus();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
            }
        });
    }

    public boolean isNeedRestoreDialog() {
        return this.delegate.isNeedRestoreList;
    }

    @Override
    public void onBackPressed() {
        if (this.delegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSearchViewTouched(MotionEvent motionEvent, final EditTextBoldCursor editTextBoldCursor) {
        BaseFragment baseFragment;
        if (motionEvent.getAction() == 0) {
            this.yOffset = this.scrollOffsetY;
            this.delegate.setAdapterItemsEnabled(false);
        } else if (motionEvent.getAction() == 1 && Math.abs(this.scrollOffsetY - this.yOffset) < this.touchSlop && !this.enterEventSent) {
            Activity findActivity = AndroidUtilities.findActivity(getContext());
            if (findActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) findActivity;
                baseFragment = (BaseFragment) launchActivity.getActionBarLayout().getFragmentStack().get(launchActivity.getActionBarLayout().getFragmentStack().size() - 1);
            } else {
                baseFragment = null;
            }
            if (baseFragment instanceof ChatActivity) {
                boolean needEnterText = ((ChatActivity) baseFragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        MemberRequestsBottomSheet.this.lambda$onSearchViewTouched$1(editTextBoldCursor);
                    }
                }, needEnterText ? 200L : 0L);
            } else {
                this.enterEventSent = true;
                setFocusable(true);
                editTextBoldCursor.requestFocus();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                });
            }
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.delegate.setAdapterItemsEnabled(true);
        }
    }

    @Override
    public void search(String str) {
        super.search(str);
        this.delegate.setQuery(str);
    }

    @Override
    public void setTranslationY(int i) {
        super.setTranslationY(i);
        this.currentLoadingView.setTranslationY(this.frameLayout.getMeasuredHeight() + i);
        float f = i;
        this.membersEmptyView.setTranslationY(f);
        this.membersSearchEmptyView.setTranslationY(f);
    }

    @Override
    public void show() {
        if (this.delegate.isNeedRestoreList && this.scrollOffsetY == 0) {
            this.scrollOffsetY = AndroidUtilities.dp(8.0f);
        }
        super.show();
        this.delegate.isNeedRestoreList = false;
    }

    @Override
    public void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            super.updateLayout();
            return;
        }
        int paddingTop = this.listView.getVisibility() == 0 ? this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f) : 0;
        if (this.scrollOffsetY != paddingTop) {
            this.scrollOffsetY = paddingTop;
            setTranslationY(paddingTop);
        }
    }
}
