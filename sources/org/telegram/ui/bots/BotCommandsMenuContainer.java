package org.telegram.ui.bots;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;

public abstract class BotCommandsMenuContainer extends FrameLayout implements NestedScrollingParent {
    Paint backgroundPaint;
    private float containerY;
    private ObjectAnimator currentAnimation;
    boolean dismissed;
    private boolean entering;
    public RecyclerListView listView;
    private NestedScrollingParentHelper nestedScrollingParentHelper;
    float scrollYOffset;
    Drawable shadowDrawable;
    Paint topBackground;

    public BotCommandsMenuContainer(Context context) {
        super(context);
        this.currentAnimation = null;
        this.backgroundPaint = new Paint();
        this.topBackground = new Paint(1);
        this.dismissed = true;
        this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                if (BotCommandsMenuContainer.this.listView.getLayoutManager() == null || BotCommandsMenuContainer.this.listView.getAdapter() == null || BotCommandsMenuContainer.this.listView.getAdapter().getItemCount() == 0) {
                    super.dispatchDraw(canvas);
                    return;
                }
                View findViewByPosition = BotCommandsMenuContainer.this.listView.getLayoutManager().findViewByPosition(0);
                float y = findViewByPosition != null ? findViewByPosition.getY() : 0.0f;
                if (y < 0.0f) {
                    y = 0.0f;
                }
                BotCommandsMenuContainer.this.scrollYOffset = y;
                float dp = y - AndroidUtilities.dp(8.0f);
                if (dp > 0.0f) {
                    int i = (int) dp;
                    BotCommandsMenuContainer.this.shadowDrawable.setBounds(-AndroidUtilities.dp(8.0f), i - AndroidUtilities.dp(24.0f), getMeasuredWidth() + AndroidUtilities.dp(8.0f), i);
                    BotCommandsMenuContainer.this.shadowDrawable.draw(canvas);
                }
                BotCommandsMenuContainer.this.containerY = dp - AndroidUtilities.dp(16.0f);
                canvas.drawRect(0.0f, dp, getMeasuredWidth(), getMeasuredHeight() + AndroidUtilities.dp(16.0f), BotCommandsMenuContainer.this.backgroundPaint);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((getMeasuredWidth() / 2.0f) - AndroidUtilities.dp(12.0f), dp - AndroidUtilities.dp(4.0f), (getMeasuredWidth() / 2.0f) + AndroidUtilities.dp(12.0f), dp);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), BotCommandsMenuContainer.this.topBackground);
                super.dispatchDraw(canvas);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(2);
        this.listView.setClipToPadding(false);
        addView(this.listView);
        updateColors();
        setClipChildren(false);
    }

    private void cancelCurrentAnimation() {
        ObjectAnimator objectAnimator = this.currentAnimation;
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
    }

    private void checkDismiss() {
        if (this.dismissed) {
            return;
        }
        if (this.listView.getTranslationY() > AndroidUtilities.dp(16.0f)) {
            dismiss();
        } else {
            playEnterAnim(false);
        }
    }

    private void playEnterAnim(boolean z) {
        ObjectAnimator objectAnimator;
        TimeInterpolator timeInterpolator;
        if (this.dismissed) {
            return;
        }
        RecyclerListView recyclerListView = this.listView;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recyclerListView, (Property<RecyclerListView, Float>) FrameLayout.TRANSLATION_Y, recyclerListView.getTranslationY(), 0.0f);
        this.currentAnimation = ofFloat;
        if (z) {
            ofFloat.setDuration(320L);
            objectAnimator = this.currentAnimation;
            timeInterpolator = new OvershootInterpolator(0.8f);
        } else {
            ofFloat.setDuration(150L);
            objectAnimator = this.currentAnimation;
            timeInterpolator = CubicBezierInterpolator.DEFAULT;
        }
        objectAnimator.setInterpolator(timeInterpolator);
        this.currentAnimation.start();
    }

    public float clipBottom() {
        if (this.dismissed) {
            return 0.0f;
        }
        return Math.max(0.0f, getMeasuredHeight() - (this.containerY + this.listView.getTranslationY()));
    }

    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        cancelCurrentAnimation();
        RecyclerListView recyclerListView = this.listView;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recyclerListView, (Property<RecyclerListView, Float>) FrameLayout.TRANSLATION_Y, recyclerListView.getTranslationY(), (getMeasuredHeight() - this.scrollYOffset) + AndroidUtilities.dp(40.0f));
        this.currentAnimation = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                BotCommandsMenuContainer.this.setVisibility(8);
                BotCommandsMenuContainer.this.currentAnimation = null;
            }
        });
        this.currentAnimation.setDuration(150L);
        this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.currentAnimation.start();
        onDismiss();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || motionEvent.getY() >= this.scrollYOffset - AndroidUtilities.dp(24.0f)) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return this.nestedScrollingParentHelper.getNestedScrollAxes();
    }

    public void onDismiss() {
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (!this.entering || this.dismissed) {
            return;
        }
        this.listView.setTranslationY((r2.getMeasuredHeight() - this.listView.getPaddingTop()) + AndroidUtilities.dp(16.0f));
        playEnterAnim(true);
        this.entering = false;
    }

    @Override
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View view, float f, float f2) {
        return false;
    }

    @Override
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        if (this.dismissed) {
            return;
        }
        cancelCurrentAnimation();
        float translationY = this.listView.getTranslationY();
        if (translationY <= 0.0f || i2 <= 0) {
            return;
        }
        float f = translationY - i2;
        iArr[1] = i2;
        this.listView.setTranslationY(f >= 0.0f ? f : 0.0f);
        invalidate();
    }

    @Override
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        if (this.dismissed) {
            return;
        }
        cancelCurrentAnimation();
        if (i4 != 0) {
            float translationY = this.listView.getTranslationY() - i4;
            if (translationY < 0.0f) {
                translationY = 0.0f;
            }
            this.listView.setTranslationY(translationY);
            invalidate();
        }
    }

    @Override
    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        if (this.dismissed) {
            return;
        }
        cancelCurrentAnimation();
    }

    @Override
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return !this.dismissed && i == 2;
    }

    @Override
    public void onStopNestedScroll(View view) {
        this.nestedScrollingParentHelper.onStopNestedScroll(view);
        if (this.dismissed) {
            return;
        }
        checkDismiss();
    }

    public void show() {
        if (getVisibility() != 0) {
            setVisibility(0);
            this.listView.scrollToPosition(0);
            this.entering = true;
            this.dismissed = false;
            return;
        }
        if (this.dismissed) {
            this.dismissed = false;
            cancelCurrentAnimation();
            playEnterAnim(false);
        }
    }

    public void updateColors() {
        this.topBackground.setColor(Theme.getColor(Theme.key_sheet_scrollUp));
        Paint paint = this.backgroundPaint;
        int i = Theme.key_windowBackgroundWhite;
        paint.setColor(Theme.getColor(i));
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.MULTIPLY));
        invalidate();
    }
}
