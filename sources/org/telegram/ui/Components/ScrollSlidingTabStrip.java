package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ScrollSlidingTabStrip;

public abstract class ScrollSlidingTabStrip extends HorizontalScrollView {
    public static float EXPANDED_WIDTH = 64.0f;
    private boolean animateFromPosition;
    boolean animateToExpanded;
    int currentDragPosition;
    SparseArray currentPlayingImages;
    SparseArray currentPlayingImagesTmp;
    private int currentPosition;
    private AnimatedFloat currentPositionAnimated;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding;
    float dragDx;
    private boolean dragEnabled;
    float draggindViewDxOnScreen;
    float draggindViewXOnScreen;
    View draggingView;
    float draggingViewOutProgress;
    private float expandOffset;
    float expandProgress;
    ValueAnimator expandStickerAnimator;
    boolean expanded;
    private SparseArray futureTabsPositions;
    private int imageReceiversPlayingNum;
    private int indicatorColor;
    private GradientDrawable indicatorDrawable;
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX;
    private RectF leftTabBounds;
    Runnable longClickRunnable;
    boolean longClickRunning;
    private float positionAnimationProgress;
    float pressedX;
    float pressedY;
    private HashMap prevTypes;
    private Paint rectPaint;
    private final Theme.ResourcesProvider resourcesProvider;
    private RectF rightTabBounds;
    private int scrollByOnNextMeasure;
    private int scrollOffset;
    boolean scrollRight;
    Runnable scrollRunnable;
    long scrollStartTime;
    private Paint selectorPaint;
    private boolean shouldExpand;
    private boolean showSelected;
    private AnimatedFloat showSelectedAlpha;
    private float startAnimationPosition;
    int startDragFromPosition;
    float startDragFromX;
    private float stickerTabExpandedWidth;
    private float stickerTabWidth;
    private RectF tabBounds;
    private int tabCount;
    private int tabPadding;
    private HashMap tabTypes;
    private LinearLayout tabsContainer;
    private float touchSlop;
    private Type type;
    private int underlineColor;
    private int underlineHeight;

    public class AnonymousClass3 extends Transition {
        AnonymousClass3() {
        }

        public void lambda$createAnimator$0(ValueAnimator valueAnimator) {
            ScrollSlidingTabStrip.this.invalidate();
        }

        @Override
        public void captureEndValues(TransitionValues transitionValues) {
        }

        @Override
        public void captureStartValues(TransitionValues transitionValues) {
        }

        @Override
        public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ScrollSlidingTabStrip.AnonymousClass3.this.lambda$createAnimator$0(valueAnimator);
                }
            });
            return ofFloat;
        }
    }

    public static class AnonymousClass7 {
        static final int[] $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type = iArr;
            try {
                iArr[Type.LINE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[Type.TAB.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    public enum Type {
        LINE,
        TAB
    }

    public ScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.imageReceiversPlayingNum = 1;
        this.type = Type.LINE;
        this.tabTypes = new HashMap();
        this.prevTypes = new HashMap();
        this.futureTabsPositions = new SparseArray();
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.currentPositionAnimated = new AnimatedFloat(this, 350L, cubicBezierInterpolator);
        this.leftTabBounds = new RectF();
        this.rightTabBounds = new RectF();
        this.tabBounds = new RectF();
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.indicatorDrawable = new GradientDrawable();
        this.scrollOffset = AndroidUtilities.dp(33.0f);
        this.underlineHeight = AndroidUtilities.dp(2.0f);
        this.dividerPadding = AndroidUtilities.dp(12.0f);
        this.tabPadding = AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        this.currentPlayingImages = new SparseArray();
        this.currentPlayingImagesTmp = new SparseArray();
        this.longClickRunnable = new Runnable() {
            @Override
            public void run() {
                ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip.longClickRunning = false;
                float scrollX = scrollSlidingTabStrip.getScrollX();
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip.startDragFromX = scrollX + scrollSlidingTabStrip2.pressedX;
                scrollSlidingTabStrip2.dragDx = 0.0f;
                int ceil = ((int) Math.ceil(scrollSlidingTabStrip2.startDragFromX / scrollSlidingTabStrip2.getTabSize())) - 1;
                ScrollSlidingTabStrip scrollSlidingTabStrip3 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip3.currentDragPosition = ceil;
                scrollSlidingTabStrip3.startDragFromPosition = ceil;
                if (scrollSlidingTabStrip3.canSwap(ceil) && ceil >= 0 && ceil < ScrollSlidingTabStrip.this.tabsContainer.getChildCount()) {
                    ScrollSlidingTabStrip.this.performHapticFeedback(0);
                    ScrollSlidingTabStrip scrollSlidingTabStrip4 = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip4.draggindViewDxOnScreen = 0.0f;
                    scrollSlidingTabStrip4.draggingViewOutProgress = 0.0f;
                    scrollSlidingTabStrip4.draggingView = scrollSlidingTabStrip4.tabsContainer.getChildAt(ceil);
                    ScrollSlidingTabStrip scrollSlidingTabStrip5 = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip5.draggindViewXOnScreen = scrollSlidingTabStrip5.draggingView.getX() - ScrollSlidingTabStrip.this.getScrollX();
                    ScrollSlidingTabStrip.this.draggingView.invalidate();
                    ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                    ScrollSlidingTabStrip.this.invalidateOverlays();
                    ScrollSlidingTabStrip.this.invalidate();
                }
            }
        };
        this.expanded = false;
        this.stickerTabExpandedWidth = AndroidUtilities.dp(EXPANDED_WIDTH);
        this.stickerTabWidth = AndroidUtilities.dp(33.0f);
        this.scrollByOnNextMeasure = -1;
        this.selectorPaint = new Paint();
        this.showSelected = true;
        this.showSelectedAlpha = new AnimatedFloat(this, 350L, cubicBezierInterpolator);
        this.scrollRunnable = new Runnable() {
            @Override
            public void run() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.AnonymousClass6.run():void");
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view instanceof StickerTabView) {
                    ((StickerTabView) view).updateExpandProgress(ScrollSlidingTabStrip.this.expandProgress);
                }
                if (view == ScrollSlidingTabStrip.this.draggingView) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }
        };
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setPadding(AndroidUtilities.dp(9.5f), 0, AndroidUtilities.dp(9.5f), 0);
        addView(this.tabsContainer, new FrameLayout.LayoutParams(-1, -1, 16));
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(33.0f), -1);
        this.defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
    }

    public boolean canSwap(int i) {
        if (this.dragEnabled && i >= 0 && i < this.tabsContainer.getChildCount()) {
            View childAt = this.tabsContainer.getChildAt(i);
            if (childAt instanceof StickerTabView) {
                StickerTabView stickerTabView = (StickerTabView) childAt;
                if (stickerTabView.type == 0 && !stickerTabView.isChatSticker) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkViewIndex(String str, View view, int i) {
        HashMap hashMap = this.prevTypes;
        if (hashMap != null) {
            hashMap.remove(str);
        }
        this.futureTabsPositions.put(i, view);
    }

    public int getTabSize() {
        return AndroidUtilities.dp(this.animateToExpanded ? EXPANDED_WIDTH : 33.0f);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$addEmojiTab$3(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(R.id.index_tag)).intValue());
    }

    public void lambda$addIconTab$0(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(R.id.index_tag)).intValue());
    }

    public void lambda$addStickerIconTab$1(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(R.id.index_tag)).intValue());
    }

    public void lambda$addStickerTab$2(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(R.id.index_tag)).intValue());
    }

    public void lambda$addStickerTab$4(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(R.id.index_tag)).intValue());
    }

    public void lambda$checkLongPress$6(ValueAnimator valueAnimator) {
        this.draggingViewOutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateOverlays();
    }

    public void lambda$expandStickers$5(boolean z, float f, ValueAnimator valueAnimator) {
        if (!z) {
            float childCount = this.stickerTabWidth * this.tabsContainer.getChildCount();
            float scrollX = (getScrollX() + f) / (this.stickerTabExpandedWidth * this.tabsContainer.getChildCount());
            float measuredWidth = (childCount - getMeasuredWidth()) / childCount;
            if (scrollX > measuredWidth) {
                scrollX = measuredWidth;
                f = 0.0f;
            }
            float f2 = childCount * scrollX;
            if (f2 - f < 0.0f) {
                f2 = f;
            }
            this.expandOffset = (getScrollX() + f) - f2;
        }
        this.expandProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
        this.tabsContainer.invalidate();
        updatePosition();
    }

    private void scrollToChild(int i) {
        if (this.tabCount == 0 || this.tabsContainer.getChildAt(i) == null) {
            return;
        }
        int left = this.tabsContainer.getChildAt(i).getLeft();
        if (i > 0) {
            left -= this.scrollOffset;
        }
        int scrollX = getScrollX();
        if (left != this.lastScrollX) {
            if (left >= scrollX) {
                if (this.scrollOffset + left <= (scrollX + getWidth()) - (this.scrollOffset * 2)) {
                    return;
                } else {
                    left = (left - getWidth()) + (this.scrollOffset * 3);
                }
            }
            this.lastScrollX = left;
            smoothScrollTo(left, 0);
        }
    }

    private void startScroll(boolean z) {
        this.scrollRight = z;
        if (this.scrollStartTime <= 0) {
            this.scrollStartTime = System.currentTimeMillis();
        }
        AndroidUtilities.runOnUIThread(this.scrollRunnable, 16L);
    }

    private void stopScroll() {
        this.scrollStartTime = -1L;
        AndroidUtilities.cancelRunOnUIThread(this.scrollRunnable);
    }

    public View addEmojiTab(int i, Emoji.EmojiDrawable emojiDrawable, TLRPC.Document document) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(str);
        if (stickerTabView != null) {
            checkViewIndex(str, stickerTabView, i2);
        } else {
            stickerTabView = new StickerTabView(getContext(), 2);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addEmojiTab$3(view);
                }
            });
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(stickerTabView, i2);
        }
        stickerTabView.isChatSticker = false;
        stickerTabView.setTag(R.id.index_tag, Integer.valueOf(i2));
        stickerTabView.setTag(R.id.parent_tag, emojiDrawable);
        stickerTabView.setTag(R.id.object_tag, document);
        stickerTabView.setSelected(i2 == this.currentPosition);
        this.tabTypes.put(str, stickerTabView);
        return stickerTabView;
    }

    public FrameLayout addIconTab(int i, Drawable drawable) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        FrameLayout frameLayout = (FrameLayout) this.prevTypes.get(str);
        if (frameLayout != null) {
            checkViewIndex(str, frameLayout, i2);
        } else {
            frameLayout = new FrameLayout(getContext());
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            frameLayout.addView(imageView, LayoutHelper.createFrame(24, 24, 17));
            frameLayout.setFocusable(true);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addIconTab$0(view);
                }
            });
            this.tabsContainer.addView(frameLayout, i2);
        }
        frameLayout.setTag(R.id.index_tag, Integer.valueOf(i2));
        frameLayout.setSelected(i2 == this.currentPosition);
        this.tabTypes.put(str, frameLayout);
        return frameLayout;
    }

    public StickerTabView addStickerIconTab(int i, Drawable drawable) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(str);
        if (stickerTabView != null) {
            checkViewIndex(str, stickerTabView, i2);
        } else {
            stickerTabView = new StickerTabView(getContext(), 1);
            stickerTabView.iconView.setImageDrawable(drawable);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addStickerIconTab$1(view);
                }
            });
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(stickerTabView, i2);
        }
        stickerTabView.isChatSticker = false;
        stickerTabView.setTag(R.id.index_tag, Integer.valueOf(i2));
        stickerTabView.setSelected(i2 == this.currentPosition);
        this.tabTypes.put(str, stickerTabView);
        return stickerTabView;
    }

    public View addStickerTab(TLObject tLObject, TLRPC.Document document, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("set");
        sb.append(tL_messages_stickerSet == null ? document.id : tL_messages_stickerSet.set.id);
        String sb2 = sb.toString();
        int i = this.tabCount;
        this.tabCount = i + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(sb2);
        if (stickerTabView != null) {
            checkViewIndex(sb2, stickerTabView, i);
        } else {
            stickerTabView = new StickerTabView(getContext(), 0);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addStickerTab$4(view);
                }
            });
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(stickerTabView, i);
        }
        stickerTabView.imageView.setLayerNum(this.imageReceiversPlayingNum);
        stickerTabView.isChatSticker = false;
        stickerTabView.setTag(tLObject);
        stickerTabView.setTag(R.id.index_tag, Integer.valueOf(i));
        stickerTabView.setTag(R.id.parent_tag, tL_messages_stickerSet);
        stickerTabView.setTag(R.id.object_tag, document);
        stickerTabView.setSelected(i == this.currentPosition);
        this.tabTypes.put(sb2, stickerTabView);
        return stickerTabView;
    }

    public void addStickerTab(TLRPC.Chat chat) {
        String str = "chat" + chat.id;
        int i = this.tabCount;
        this.tabCount = i + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(str);
        if (stickerTabView != null) {
            checkViewIndex(str, stickerTabView, i);
        } else {
            stickerTabView = new StickerTabView(getContext(), 0);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addStickerTab$2(view);
                }
            });
            this.tabsContainer.addView(stickerTabView, i);
            stickerTabView.setRoundImage();
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(UserConfig.selectedAccount, chat);
            BackupImageView backupImageView = stickerTabView.imageView;
            backupImageView.setLayerNum(this.imageReceiversPlayingNum);
            backupImageView.setForUserOrChat(chat, avatarDrawable);
            backupImageView.setAspectFit(true);
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            stickerTabView.textView.setText(chat.title);
        }
        stickerTabView.isChatSticker = true;
        stickerTabView.setTag(R.id.index_tag, Integer.valueOf(i));
        stickerTabView.setSelected(i == this.currentPosition);
        this.tabTypes.put(str, stickerTabView);
    }

    public void beginUpdate(boolean z) {
        this.prevTypes = this.tabTypes;
        this.tabTypes = new HashMap();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        if (z) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(250L);
            autoTransition.setOrdering(0);
            autoTransition.addTransition(new AnonymousClass3());
            TransitionManager.beginDelayedTransition(this.tabsContainer, autoTransition);
        }
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        this.longClickRunning = false;
        AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
    }

    public boolean checkLongPress(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.draggingView == null) {
            this.longClickRunning = true;
            AndroidUtilities.runOnUIThread(this.longClickRunnable, 500L);
            this.pressedX = motionEvent.getX();
            this.pressedY = motionEvent.getY();
        }
        if (this.longClickRunning && motionEvent.getAction() == 2 && (Math.abs(motionEvent.getX() - this.pressedX) > this.touchSlop || Math.abs(motionEvent.getY() - this.pressedY) > this.touchSlop)) {
            this.longClickRunning = false;
            AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
        }
        if (motionEvent.getAction() != 2 || this.draggingView == null) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                stopScroll();
                AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
                if (this.draggingView != null) {
                    int i = this.startDragFromPosition;
                    int i2 = this.currentDragPosition;
                    if (i != i2) {
                        stickerSetPositionChanged(i, i2);
                        for (int i3 = 0; i3 < this.tabsContainer.getChildCount(); i3++) {
                            this.tabsContainer.getChildAt(i3).setTag(R.id.index_tag, Integer.valueOf(i3));
                        }
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ScrollSlidingTabStrip.this.lambda$checkLongPress$6(valueAnimator);
                        }
                    });
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                            if (scrollSlidingTabStrip.draggingView != null) {
                                scrollSlidingTabStrip.invalidateOverlays();
                                ScrollSlidingTabStrip.this.draggingView.invalidate();
                                ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                                ScrollSlidingTabStrip.this.invalidate();
                                ScrollSlidingTabStrip.this.draggingView = null;
                            }
                        }
                    });
                    ofFloat.start();
                }
                this.longClickRunning = false;
                invalidateOverlays();
            }
            return false;
        }
        float scrollX = getScrollX() + motionEvent.getX();
        int ceil = ((int) Math.ceil(scrollX / getTabSize())) - 1;
        int i4 = this.currentDragPosition;
        if (ceil != i4) {
            if (ceil < i4) {
                while (!canSwap(ceil) && ceil != this.currentDragPosition) {
                    ceil++;
                }
            } else {
                while (!canSwap(ceil) && ceil != this.currentDragPosition) {
                    ceil--;
                }
            }
        }
        if (this.currentDragPosition != ceil && canSwap(ceil)) {
            for (int i5 = 0; i5 < this.tabsContainer.getChildCount(); i5++) {
                if (i5 != this.currentDragPosition) {
                    ((StickerTabView) this.tabsContainer.getChildAt(i5)).saveXPosition();
                }
            }
            this.startDragFromX += (ceil - this.currentDragPosition) * getTabSize();
            this.currentDragPosition = ceil;
            this.tabsContainer.removeView(this.draggingView);
            this.tabsContainer.addView(this.draggingView, this.currentDragPosition);
            invalidate();
        }
        this.dragDx = scrollX - this.startDragFromX;
        this.draggindViewDxOnScreen = this.pressedX - motionEvent.getX();
        float x = motionEvent.getX();
        if (x < this.draggingView.getMeasuredWidth() / 2.0f) {
            startScroll(false);
        } else if (x > getMeasuredWidth() - (this.draggingView.getMeasuredWidth() / 2.0f)) {
            startScroll(true);
        } else {
            stopScroll();
        }
        this.tabsContainer.invalidate();
        invalidateOverlays();
        return true;
    }

    public void commitUpdate() {
        HashMap hashMap = this.prevTypes;
        if (hashMap != null) {
            Iterator it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                this.tabsContainer.removeView((View) ((Map.Entry) it.next()).getValue());
            }
            this.prevTypes.clear();
        }
        int size = this.futureTabsPositions.size();
        for (int i = 0; i < size; i++) {
            int keyAt = this.futureTabsPositions.keyAt(i);
            View view = (View) this.futureTabsPositions.valueAt(i);
            if (this.tabsContainer.indexOfChild(view) != keyAt) {
                this.tabsContainer.removeView(view);
                this.tabsContainer.addView(view, keyAt);
            }
        }
        this.futureTabsPositions.clear();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float f;
        float textWidth;
        float f2 = this.stickerTabWidth - this.stickerTabExpandedWidth;
        float f3 = this.expandOffset * (1.0f - this.expandProgress);
        for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
            if (this.tabsContainer.getChildAt(i) instanceof StickerTabView) {
                StickerTabView stickerTabView = (StickerTabView) this.tabsContainer.getChildAt(i);
                stickerTabView.animateIfPositionChanged(this);
                stickerTabView.setTranslationX(this.animateToExpanded ? (i * f2 * (1.0f - this.expandProgress)) + f3 + stickerTabView.dragOffset : stickerTabView.dragOffset);
            }
        }
        float height = getHeight();
        if (this.animateToExpanded) {
            height = getHeight() - (AndroidUtilities.dp(50.0f) * (1.0f - this.expandProgress));
        }
        float f4 = height;
        float f5 = this.showSelectedAlpha.set(this.showSelected ? 1.0f : 0.0f);
        if (!isInEditMode() && this.tabCount != 0 && this.indicatorHeight >= 0) {
            float f6 = this.currentPositionAnimated.set(this.currentPosition);
            double d = f6;
            int floor = (int) Math.floor(d);
            int ceil = (int) Math.ceil(d);
            View view = null;
            View childAt = (floor < 0 || floor >= this.tabsContainer.getChildCount()) ? null : this.tabsContainer.getChildAt(floor);
            if (ceil >= 0 && ceil < this.tabsContainer.getChildCount()) {
                view = this.tabsContainer.getChildAt(ceil);
            }
            float f7 = f4 / 2.0f;
            if (childAt != null && view != null) {
                float f8 = f6 - floor;
                float lerp = AndroidUtilities.lerp(childAt.getLeft() + childAt.getTranslationX() + (AndroidUtilities.lerp(AndroidUtilities.dp(33.0f), AndroidUtilities.dp(EXPANDED_WIDTH), this.expandProgress) / 2.0f), view.getLeft() + view.getTranslationX() + (AndroidUtilities.lerp(AndroidUtilities.dp(33.0f), AndroidUtilities.dp(EXPANDED_WIDTH), this.expandProgress) / 2.0f), f8);
                textWidth = AndroidUtilities.lerp(childAt instanceof StickerTabView ? ((StickerTabView) childAt).getTextWidth() : 0.0f, view instanceof StickerTabView ? ((StickerTabView) view).getTextWidth() : 0.0f, f8);
                f = lerp;
            } else if (childAt != null) {
                f = childAt.getLeft() + childAt.getTranslationX() + (AndroidUtilities.lerp(AndroidUtilities.dp(33.0f), AndroidUtilities.dp(EXPANDED_WIDTH), this.expandProgress) / 2.0f);
                if (childAt instanceof StickerTabView) {
                    textWidth = ((StickerTabView) childAt).getTextWidth();
                }
                textWidth = 0.0f;
            } else {
                if (view != null) {
                    f = view.getLeft() + view.getTranslationX() + (AndroidUtilities.lerp(AndroidUtilities.dp(33.0f), AndroidUtilities.dp(EXPANDED_WIDTH), this.expandProgress) / 2.0f);
                    if (view instanceof StickerTabView) {
                        textWidth = ((StickerTabView) view).getTextWidth();
                    }
                } else {
                    f = 0.0f;
                }
                textWidth = 0.0f;
            }
            float dp = AndroidUtilities.dp(30.0f);
            float abs = (1.25f - ((Math.abs(0.5f - this.currentPositionAnimated.getTransitionProgressInterpolated()) * 0.25f) * 2.0f)) * dp;
            float abs2 = dp * ((Math.abs(0.5f - this.currentPositionAnimated.getTransitionProgressInterpolated()) * 0.1f * 2.0f) + 0.9f);
            float interpolation = CubicBezierInterpolator.EASE_IN.getInterpolation(this.expandProgress);
            float lerp2 = f7 + AndroidUtilities.lerp(0, AndroidUtilities.dp(26.0f), interpolation);
            float lerp3 = AndroidUtilities.lerp(abs, textWidth + AndroidUtilities.dp(10.0f), interpolation) / 2.0f;
            float lerp4 = (abs2 * AndroidUtilities.lerp(1.0f, 0.55f, interpolation)) / 2.0f;
            this.tabBounds.set(f - lerp3, lerp2 - lerp4, f + lerp3, lerp2 + lerp4);
            this.selectorPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_chat_emojiPanelIcon), 46));
            this.selectorPaint.setAlpha((int) (r2.getAlpha() * f5));
            canvas.drawRoundRect(this.tabBounds, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), this.selectorPaint);
        }
        super.dispatchDraw(canvas);
        if (isInEditMode() || this.tabCount == 0 || this.underlineHeight <= 0) {
            return;
        }
        this.rectPaint.setColor(this.underlineColor);
        canvas.drawRect(0.0f, f4 - this.underlineHeight, this.tabsContainer.getWidth(), f4, this.rectPaint);
    }

    public void drawOverlays(Canvas canvas) {
        if (this.draggingView != null) {
            canvas.save();
            float f = this.draggindViewXOnScreen - this.draggindViewDxOnScreen;
            float f2 = this.draggingViewOutProgress;
            if (f2 > 0.0f) {
                f = (f * (1.0f - f2)) + ((this.draggingView.getX() - getScrollX()) * this.draggingViewOutProgress);
            }
            canvas.translate(f, 0.0f);
            this.draggingView.draw(canvas);
            canvas.restore();
        }
    }

    public void expandStickers(final float f, final boolean z) {
        if (this.expanded != z) {
            this.expanded = z;
            if (!z) {
                fling(0);
            }
            ValueAnimator valueAnimator = this.expandStickerAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.expandStickerAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.expandProgress, z ? 1.0f : 0.0f);
            this.expandStickerAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ScrollSlidingTabStrip.this.lambda$expandStickers$5(z, f, valueAnimator2);
                }
            });
            this.expandStickerAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip.expandStickerAnimator = null;
                    scrollSlidingTabStrip.expandProgress = z ? 1.0f : 0.0f;
                    for (int i = 0; i < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i++) {
                        ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i).invalidate();
                    }
                    ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                    ScrollSlidingTabStrip.this.updatePosition();
                    if (z) {
                        return;
                    }
                    float childCount = ScrollSlidingTabStrip.this.stickerTabWidth * ScrollSlidingTabStrip.this.tabsContainer.getChildCount();
                    float scrollX = (ScrollSlidingTabStrip.this.getScrollX() + f) / (ScrollSlidingTabStrip.this.stickerTabExpandedWidth * ScrollSlidingTabStrip.this.tabsContainer.getChildCount());
                    float measuredWidth = (childCount - ScrollSlidingTabStrip.this.getMeasuredWidth()) / childCount;
                    float f2 = f;
                    if (scrollX > measuredWidth) {
                        scrollX = measuredWidth;
                        f2 = 0.0f;
                    }
                    float f3 = childCount * scrollX;
                    if (f3 - f2 < 0.0f) {
                        f3 = f2;
                    }
                    ScrollSlidingTabStrip.this.expandOffset = (r1.getScrollX() + f2) - f3;
                    ScrollSlidingTabStrip.this.scrollByOnNextMeasure = (int) (f3 - f2);
                    if (ScrollSlidingTabStrip.this.scrollByOnNextMeasure < 0) {
                        ScrollSlidingTabStrip.this.scrollByOnNextMeasure = 0;
                    }
                    for (int i2 = 0; i2 < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i2++) {
                        View childAt = ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i2);
                        if (childAt instanceof StickerTabView) {
                            ((StickerTabView) childAt).setExpanded(false);
                        }
                        childAt.getLayoutParams().width = AndroidUtilities.dp(33.0f);
                    }
                    ScrollSlidingTabStrip scrollSlidingTabStrip2 = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip2.animateToExpanded = false;
                    scrollSlidingTabStrip2.getLayoutParams().height = AndroidUtilities.dp(36.0f);
                    ScrollSlidingTabStrip.this.tabsContainer.requestLayout();
                }
            });
            this.expandStickerAnimator.start();
            if (z) {
                this.animateToExpanded = true;
                for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
                    View childAt = this.tabsContainer.getChildAt(i);
                    if (childAt instanceof StickerTabView) {
                        ((StickerTabView) childAt).setExpanded(true);
                    }
                    childAt.getLayoutParams().width = AndroidUtilities.dp(EXPANDED_WIDTH);
                }
                this.tabsContainer.requestLayout();
                getLayoutParams().height = AndroidUtilities.dp(86.0f);
            }
            if (z) {
                float childCount = this.stickerTabExpandedWidth * this.tabsContainer.getChildCount() * ((getScrollX() + f) / (this.stickerTabWidth * this.tabsContainer.getChildCount()));
                this.expandOffset = childCount - (getScrollX() + f);
                this.scrollByOnNextMeasure = (int) (childCount - f);
            }
        }
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public float getExpandedOffset() {
        if (this.animateToExpanded) {
            return AndroidUtilities.dp(50.0f) * this.expandProgress;
        }
        return 0.0f;
    }

    public Type getType() {
        return this.type;
    }

    protected void invalidateOverlays() {
    }

    public void invalidateTabs() {
        int childCount = this.tabsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
    }

    public boolean isDragging() {
        return this.draggingView != null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return checkLongPress(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setImages();
        int i5 = this.scrollByOnNextMeasure;
        if (i5 >= 0) {
            scrollTo(i5, 0);
            this.scrollByOnNextMeasure = -1;
        }
    }

    public void onPageScrolled(int i, int i2) {
        int i3 = this.currentPosition;
        if (i3 == i) {
            return;
        }
        if (this.tabsContainer.getChildAt(i3) != null) {
            this.startAnimationPosition = r0.getLeft();
            this.positionAnimationProgress = 0.0f;
            this.animateFromPosition = true;
            this.lastAnimationTime = SystemClock.elapsedRealtime();
        } else {
            this.animateFromPosition = false;
        }
        this.currentPosition = i;
        if (i >= this.tabsContainer.getChildCount()) {
            return;
        }
        this.positionAnimationProgress = 0.0f;
        int i4 = 0;
        while (i4 < this.tabsContainer.getChildCount()) {
            this.tabsContainer.getChildAt(i4).setSelected(i4 == i);
            i4++;
        }
        if (this.expandStickerAnimator == null) {
            if (i2 == i && i > 1) {
                i--;
            }
            scrollToChild(i);
        }
        invalidate();
    }

    @Override
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        setImages();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return checkLongPress(motionEvent) || super.onTouchEvent(motionEvent);
    }

    public void selectTab(int i) {
        if (i < 0 || i >= this.tabCount) {
            return;
        }
        this.tabsContainer.getChildAt(i).performClick();
    }

    public void setCurrentPosition(int i) {
        this.currentPosition = i;
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void setDragEnabled(boolean z) {
        this.dragEnabled = z;
    }

    public void setImageReceiversLayerNum(int i) {
        this.imageReceiversPlayingNum = i;
    }

    public void setImages() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.setImages():void");
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
    }

    public void setShouldExpand(boolean z) {
        this.shouldExpand = z;
        requestLayout();
    }

    public void setType(Type type) {
        if (type == null || this.type == type) {
            return;
        }
        this.type = type;
        int i = AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[type.ordinal()];
        if (i == 1) {
            this.indicatorDrawable.setCornerRadius(0.0f);
        } else {
            if (i != 2) {
                return;
            }
            float dpf2 = AndroidUtilities.dpf2(3.0f);
            this.indicatorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
        }
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.underlineColor = getResources().getColor(i);
        invalidate();
    }

    public void setUnderlineHeight(int i) {
        if (this.underlineHeight != i) {
            this.underlineHeight = i;
            invalidate();
        }
    }

    public void showSelected(boolean z) {
        this.showSelected = z;
        invalidate();
    }

    protected void stickerSetPositionChanged(int i, int i2) {
    }

    protected void updatePosition() {
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            this.tabsContainer.getChildAt(i).setLayoutParams(this.shouldExpand ? this.defaultExpandLayoutParams : this.defaultTabLayoutParams);
        }
    }
}
