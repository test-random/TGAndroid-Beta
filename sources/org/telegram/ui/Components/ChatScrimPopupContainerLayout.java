package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.PopupSwipeBackLayout;

public abstract class ChatScrimPopupContainerLayout extends LinearLayout {
    private View bottomView;
    private float bottomViewReactionsOffset;
    private float bottomViewYOffset;
    private float expandSize;
    private int maxHeight;
    private float popupLayoutLeftOffset;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    private float progressToSwipeBack;
    private ReactionsContainerLayout reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    public void lambda$setPopupWindowLayout$0(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        if (this.bottomView != null) {
            this.bottomViewYOffset = actionBarPopupWindowLayout.getVisibleHeight() - actionBarPopupWindowLayout.getMeasuredHeight();
            updateBottomViewPosition();
        }
    }

    public void lambda$setPopupWindowLayout$1(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(1.0f - f2);
        }
        this.progressToSwipeBack = f2;
        updatePopupTranslation();
    }

    private void updateBottomViewPosition() {
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationY(this.bottomViewYOffset + this.expandSize + this.bottomViewReactionsOffset);
        }
    }

    private void updatePopupTranslation() {
        float f = (1.0f - this.progressToSwipeBack) * this.popupLayoutLeftOffset;
        this.popupWindowLayout.setTranslationX(f);
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationX(f);
        }
    }

    public void applyViewBottom(FrameLayout frameLayout) {
        this.bottomView = frameLayout;
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4 = this.maxHeight;
        int makeMeasureSpec = i4 != 0 ? View.MeasureSpec.makeMeasureSpec(i4, Integer.MIN_VALUE) : i2;
        ReactionsContainerLayout reactionsContainerLayout = this.reactionsLayout;
        if (reactionsContainerLayout == null || this.popupWindowLayout == null) {
            i3 = i;
        } else {
            reactionsContainerLayout.getLayoutParams().width = -2;
            ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = 0;
            this.popupLayoutLeftOffset = 0.0f;
            i3 = i;
            super.onMeasure(i3, makeMeasureSpec);
            int measuredWidth = this.reactionsLayout.getMeasuredWidth();
            if (this.popupWindowLayout.getSwipeBack() != null && this.popupWindowLayout.getSwipeBack().getMeasuredWidth() > measuredWidth) {
                measuredWidth = this.popupWindowLayout.getSwipeBack().getMeasuredWidth();
            }
            if (this.popupWindowLayout.getMeasuredWidth() > measuredWidth) {
                measuredWidth = this.popupWindowLayout.getMeasuredWidth();
            }
            if (this.reactionsLayout.showCustomEmojiReaction()) {
                i3 = View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
            }
            this.reactionsLayout.measureHint();
            int totalWidth = this.reactionsLayout.getTotalWidth();
            View childAt = (this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack() : this.popupWindowLayout).getChildAt(0);
            int measuredWidth2 = childAt.getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
            int hintTextWidth = this.reactionsLayout.getHintTextWidth();
            if (hintTextWidth > measuredWidth2) {
                measuredWidth2 = hintTextWidth;
            } else if (measuredWidth2 > measuredWidth) {
                measuredWidth2 = measuredWidth;
            }
            this.reactionsLayout.bigCircleOffset = AndroidUtilities.dp(36.0f);
            if (this.reactionsLayout.showCustomEmojiReaction()) {
                this.reactionsLayout.getLayoutParams().width = totalWidth;
                this.reactionsLayout.bigCircleOffset = Math.max((totalWidth - childAt.getMeasuredWidth()) - AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            } else if (totalWidth > measuredWidth2) {
                int dp = ((measuredWidth2 - AndroidUtilities.dp(16.0f)) / AndroidUtilities.dp(36.0f)) + 1;
                int dp2 = (AndroidUtilities.dp(36.0f) * dp) + AndroidUtilities.dp(8.0f);
                if (hintTextWidth + AndroidUtilities.dp(24.0f) > dp2) {
                    dp2 = hintTextWidth + AndroidUtilities.dp(24.0f);
                }
                if (dp2 <= totalWidth && dp != this.reactionsLayout.getItemsCount()) {
                    totalWidth = dp2;
                }
                this.reactionsLayout.getLayoutParams().width = totalWidth;
            } else {
                this.reactionsLayout.getLayoutParams().width = -2;
            }
            if (this.reactionsLayout.getMeasuredWidth() == measuredWidth && this.reactionsLayout.showCustomEmojiReaction()) {
                float measuredWidth3 = (measuredWidth - childAt.getMeasuredWidth()) * 0.25f;
                this.popupLayoutLeftOffset = measuredWidth3;
                int i5 = (int) (r6.bigCircleOffset - measuredWidth3);
                this.reactionsLayout.bigCircleOffset = i5;
                if (i5 < AndroidUtilities.dp(36.0f)) {
                    this.popupLayoutLeftOffset = 0.0f;
                    this.reactionsLayout.bigCircleOffset = AndroidUtilities.dp(36.0f);
                }
            } else {
                int measuredWidth4 = this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth() : 0;
                if (this.reactionsLayout.getLayoutParams().width != -2 && this.reactionsLayout.getLayoutParams().width + measuredWidth4 > measuredWidth) {
                    measuredWidth4 = (measuredWidth - this.reactionsLayout.getLayoutParams().width) + AndroidUtilities.dp(8.0f);
                }
                r4 = measuredWidth4 >= 0 ? measuredWidth4 : 0;
                ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = r4;
                this.popupLayoutLeftOffset = 0.0f;
            }
            updatePopupTranslation();
            if (this.bottomView != null) {
                if (this.reactionsLayout.showCustomEmojiReaction()) {
                    this.bottomView.getLayoutParams().width = childAt.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                    updatePopupTranslation();
                } else {
                    this.bottomView.getLayoutParams().width = -1;
                }
                if (this.popupWindowLayout.getSwipeBack() != null) {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = r4 + AndroidUtilities.dp(36.0f);
                } else {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f);
                }
            }
        }
        super.onMeasure(i3, makeMeasureSpec);
        this.maxHeight = getMeasuredHeight();
    }

    public void setExpandSize(float f) {
        this.popupWindowLayout.setTranslationY(f);
        this.expandSize = f;
        updateBottomViewPosition();
    }

    public void setMaxHeight(int i) {
        this.maxHeight = i;
    }

    public void setPopupAlpha(float f) {
        this.popupWindowLayout.setAlpha(f);
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(f);
        }
    }

    public void setPopupWindowLayout(final ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        this.popupWindowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setOnSizeChangedListener(new ActionBarPopupWindow.onSizeChangedListener() {
            @Override
            public final void onSizeChanged() {
                ChatScrimPopupContainerLayout.this.lambda$setPopupWindowLayout$0(actionBarPopupWindowLayout);
            }
        });
        if (actionBarPopupWindowLayout.getSwipeBack() != null) {
            actionBarPopupWindowLayout.getSwipeBack().addOnSwipeBackProgressListener(new PopupSwipeBackLayout.OnSwipeBackProgressListener() {
                @Override
                public final void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
                    ChatScrimPopupContainerLayout.this.lambda$setPopupWindowLayout$1(popupSwipeBackLayout, f, f2);
                }
            });
        }
    }

    public void setReactionsLayout(ReactionsContainerLayout reactionsContainerLayout) {
        this.reactionsLayout = reactionsContainerLayout;
        if (reactionsContainerLayout != null) {
            reactionsContainerLayout.setChatScrimView(this);
        }
    }

    public void setReactionsTransitionProgress(float f) {
        this.popupWindowLayout.setReactionsTransitionProgress(f);
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(f);
            float f2 = (f * 0.5f) + 0.5f;
            this.bottomView.setPivotX(r0.getMeasuredWidth());
            this.bottomView.setPivotY(0.0f);
            this.bottomViewReactionsOffset = (-this.popupWindowLayout.getMeasuredHeight()) * (1.0f - f);
            updateBottomViewPosition();
            this.bottomView.setScaleX(f2);
            this.bottomView.setScaleY(f2);
        }
    }
}
