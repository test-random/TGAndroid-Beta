package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.HintView;

public class HintView extends FrameLayout {
    private AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private int backgroundColor;
    Paint backgroundPaint;
    private int bottomOffset;
    private int currentType;
    private View currentView;
    private boolean drawPath;
    private float extraTranslationY;
    private boolean hasCloseButton;
    private Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    private ChatMessageCell messageCell;
    private String overrideText;
    Path path;
    private final Theme.ResourcesProvider resourcesProvider;
    private long showingDuration;
    private int shownY;
    public TextView textView;
    private float translationY;
    private boolean useScale;

    public class AnonymousClass1 extends AnimatorListenerAdapter {
        AnonymousClass1() {
        }

        public void lambda$onAnimationEnd$0() {
            HintView.this.hide();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            HintView.this.animatorSet = null;
            if (HintView.this.hasCloseButton) {
                return;
            }
            AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() {
                @Override
                public final void run() {
                    HintView.AnonymousClass1.this.lambda$onAnimationEnd$0();
                }
            }, HintView.this.currentType == 0 ? 10000L : 2000L);
        }
    }

    public class AnonymousClass2 extends AnimatorListenerAdapter {
        AnonymousClass2() {
        }

        public void lambda$onAnimationEnd$0() {
            HintView.this.hide();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            HintView.this.animatorSet = null;
            if (HintView.this.hasCloseButton) {
                return;
            }
            AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() {
                @Override
                public final void run() {
                    HintView.AnonymousClass2.this.lambda$onAnimationEnd$0();
                }
            }, HintView.this.showingDuration);
        }
    }

    public interface VisibilityListener {
    }

    public HintView(Context context, int i) {
        this(context, i, false, null);
    }

    public HintView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, false, resourcesProvider);
    }

    public HintView(Context context, int i, boolean z) {
        this(context, i, z, null);
    }

    public HintView(android.content.Context r10, int r11, boolean r12, org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.<init>(android.content.Context, int, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$createCloseButton$0(View view) {
        hide(true);
    }

    private void updatePosition(android.view.View r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.updatePosition(android.view.View):void");
    }

    public void createCloseButton() {
        this.textView.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(36.0f), AndroidUtilities.dp(8.0f));
        this.hasCloseButton = true;
        ImageView imageView = new ImageView(getContext());
        this.imageView = imageView;
        imageView.setImageResource(R.drawable.msg_mini_close_tooltip);
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_chat_gifSaveHintText), 125), PorterDuff.Mode.MULTIPLY));
        ImageView imageView2 = this.imageView;
        boolean z = this.isTopArrow;
        addView(imageView2, LayoutHelper.createFrame(34, 34.0f, 21, 0.0f, z ? 3.0f : 0.0f, 0.0f, z ? 0.0f : 3.0f));
        setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                HintView.this.lambda$createCloseButton$0(view);
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.drawPath && this.path != null) {
            if (this.backgroundPaint == null) {
                Paint paint = new Paint(1);
                this.backgroundPaint = paint;
                paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dpf2(6.0f)));
                this.backgroundPaint.setColor(this.backgroundColor);
            }
            canvas.drawPath(this.path, this.backgroundPaint);
        }
        super.dispatchDraw(canvas);
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public ChatMessageCell getMessageCell() {
        return this.messageCell;
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean z) {
        if (getTag() == null) {
            return;
        }
        setTag(null);
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        if (!z) {
            setVisibility(4);
            this.currentView = null;
            this.messageCell = null;
            this.animatorSet = null;
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        if (this.useScale) {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.ALPHA, 1.0f, 0.0f), ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.SCALE_Y, 1.0f, 0.5f), ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.SCALE_X, 1.0f, 0.5f));
            this.animatorSet.setDuration(150L);
            this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        } else {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.ALPHA, 0.0f));
            this.animatorSet.setDuration(300L);
        }
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                HintView.this.setVisibility(4);
                HintView.this.getClass();
                HintView.this.currentView = null;
                HintView.this.messageCell = null;
                HintView.this.animatorSet = null;
            }
        });
        this.animatorSet.start();
    }

    public boolean isShowing() {
        return getTag() != null;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.drawPath) {
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            float x = this.arrowImageView.getX() + (this.arrowImageView.getMeasuredWidth() / 2.0f);
            Path path = this.path;
            if (path == null) {
                this.path = new Path();
            } else {
                path.rewind();
            }
            if (this.isTopArrow) {
                this.path.moveTo(0.0f, AndroidUtilities.dp(6.0f));
                float f = measuredHeight;
                this.path.lineTo(0.0f, f);
                float f2 = measuredWidth;
                this.path.lineTo(f2, f);
                this.path.lineTo(f2, AndroidUtilities.dp(6.0f));
                this.path.lineTo(AndroidUtilities.dp(7.0f) + x, AndroidUtilities.dp(6.0f));
                this.path.lineTo(x, -AndroidUtilities.dp(2.0f));
                this.path.lineTo(x - AndroidUtilities.dp(7.0f), AndroidUtilities.dp(6.0f));
            } else {
                this.path.moveTo(0.0f, measuredHeight - AndroidUtilities.dp(6.0f));
                this.path.lineTo(0.0f, 0.0f);
                float f3 = measuredWidth;
                this.path.lineTo(f3, 0.0f);
                this.path.lineTo(f3, measuredHeight - AndroidUtilities.dp(6.0f));
                this.path.lineTo(AndroidUtilities.dp(7.0f) + x, measuredHeight - AndroidUtilities.dp(6.0f));
                this.path.lineTo(x, AndroidUtilities.dp(2.0f) + measuredHeight);
                this.path.lineTo(x - AndroidUtilities.dp(7.0f), measuredHeight - AndroidUtilities.dp(6.0f));
            }
            this.path.close();
        }
    }

    public void setBackgroundColor(int i, int i2) {
        this.textView.setTextColor(i2);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        TextView textView = this.textView;
        int i3 = this.currentType;
        textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp((i3 == 7 || i3 == 8) ? 6.0f : 3.0f), i));
    }

    public void setBottomOffset(int i) {
        this.bottomOffset = i;
    }

    public void setExtraTranslationY(float f) {
        this.extraTranslationY = f;
        setTranslationY(f + this.translationY);
    }

    public void setOverrideText(String str) {
        this.overrideText = str;
        this.textView.setText(str);
        ChatMessageCell chatMessageCell = this.messageCell;
        if (chatMessageCell != null) {
            this.messageCell = null;
            showForMessageCell(chatMessageCell, false);
        }
    }

    public void setShowingDuration(long j) {
        this.showingDuration = j;
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public void setUseScale(boolean z) {
        this.useScale = z;
    }

    public void setVisibleListener(VisibilityListener visibilityListener) {
    }

    public boolean showForMessageCell(org.telegram.ui.Cells.ChatMessageCell r18, java.lang.Object r19, int r20, int r21, boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForMessageCell(org.telegram.ui.Cells.ChatMessageCell, java.lang.Object, int, int, boolean):boolean");
    }

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, boolean z) {
        return showForMessageCell(chatMessageCell, null, 0, 0, z);
    }

    public boolean showForView(View view, boolean z) {
        if (this.currentView == view || getTag() != null) {
            if (getTag() != null) {
                updatePosition(view);
            }
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        updatePosition(view);
        this.currentView = view;
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (z) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            if (this.useScale) {
                setPivotX(this.arrowImageView.getX() + (this.arrowImageView.getMeasuredWidth() / 2.0f));
                setPivotY(this.arrowImageView.getY() + (this.arrowImageView.getMeasuredHeight() / 2.0f));
                this.animatorSet.playTogether(ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.SCALE_Y, 0.5f, 1.0f), ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.SCALE_X, 0.5f, 1.0f));
                this.animatorSet.setDuration(350L);
                this.animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            } else {
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<HintView, Float>) View.ALPHA, 0.0f, 1.0f));
                this.animatorSet.setDuration(300L);
            }
            this.animatorSet.addListener(new AnonymousClass2());
            this.animatorSet.start();
        } else {
            setAlpha(1.0f);
        }
        return true;
    }

    public void updatePosition() {
        View view = this.currentView;
        if (view == null) {
            return;
        }
        updatePosition(view);
    }
}
