package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow;
import org.telegram.ui.Components.Reactions.HwEmojis;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.recorder.HintView2;

public class ReactionsContainerLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final Property TRANSITION_PROGRESS_VALUE = new Property(Float.class, "transitionProgress") {
        AnonymousClass1(Class cls, String str) {
            super(cls, str);
        }

        @Override
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        @Override
        public void set(ReactionsContainerLayout reactionsContainerLayout, Float f) {
            reactionsContainerLayout.setTransitionProgress(f.floatValue());
        }
    };
    private boolean allReactionsAvailable;
    private boolean allReactionsIsDefault;
    public List allReactionsList;
    final HashSet alwaysSelectedReactions;
    private boolean animatePopup;
    private final boolean animationEnabled;
    private Paint bgPaint;
    public int bigCircleOffset;
    private float bigCircleRadius;
    public float bubblesOffset;
    ValueAnimator cancelPressedAnimation;
    private float cancelPressedProgress;
    public boolean channelReactions;
    ChatScrimPopupContainerLayout chatScrimPopupContainerLayout;
    private boolean clicked;
    private int currentAccount;
    private float customEmojiReactionsEnterProgress;
    private InternalImageView customEmojiReactionsIconView;
    FrameLayout customReactionsContainer;
    private ReactionsContainerDelegate delegate;
    public final float durationScale;
    private float flipVerticalProgress;
    BaseFragment fragment;
    public boolean hasHint;
    private boolean hasStar;
    private boolean hintMeasured;
    public TextView hintView;
    public int hintViewHeight;
    public int hintViewWidth;
    public boolean hitLimit;
    private boolean isFlippedVertically;
    public boolean isHiddenNextReaction;
    private boolean isTop;
    public ArrayList items;
    long lastReactionSentTime;
    private long lastUpdate;
    HashSet lastVisibleViews;
    HashSet lastVisibleViewsTmp;
    private float leftAlpha;
    private Paint leftShadowPaint;
    private LinearLayoutManager linearLayoutManager;
    private Adapter listAdapter;
    private int[] location;
    private Path mPath;
    public MessageObject messageObject;
    private float miniBubblesOffset;
    private boolean mirrorX;
    public ReactionHolderView nextRecentReaction;
    public final AnimationNotificationsLocker notificationsLocker;
    public ArrayList oldItems;
    private Runnable onSwitchedToLoopView;
    private float otherViewsScale;
    ChatScrimPopupContainerLayout parentLayout;
    public boolean paused;
    public boolean pausedExceptSelected;
    FrameLayout premiumLockContainer;
    private PremiumLockIconView premiumLockIconView;
    private List premiumLockedReactions;
    private boolean prepareAnimation;
    private float pressedProgress;
    private ReactionsLayoutInBubble.VisibleReaction pressedReaction;
    private int pressedReactionPosition;
    private float pressedViewScale;
    ValueAnimator pullingDownBackAnimator;
    float pullingLeftOffset;
    public float radius;
    CustomEmojiReactionsWindow reactionsWindow;
    public RectF rect;
    RectF rectF;
    public final RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    private float rightAlpha;
    private Paint rightShadowPaint;
    private final Paint selectedPaint;
    final HashSet selectedReactions;
    private Drawable shadow;
    private android.graphics.Rect shadowPad;
    private boolean showExpandableReactions;
    boolean skipDraw;
    public boolean skipEnterAnimation;
    private float smallCircleRadius;
    private LinearGradient starSelectedGradient;
    private Matrix starSelectedGradientMatrix;
    private Paint starSelectedGradientPaint;
    private final Paint starSelectedPaint;
    private float transitionProgress;
    private List triggeredReactions;
    private final int type;
    private List visibleReactionsList;
    private long waitingLoadingChatId;

    public class AnonymousClass1 extends Property {
        AnonymousClass1(Class cls, String str) {
            super(cls, str);
        }

        @Override
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        @Override
        public void set(ReactionsContainerLayout reactionsContainerLayout, Float f) {
            reactionsContainerLayout.setTransitionProgress(f.floatValue());
        }
    }

    public class AnonymousClass2 extends RecyclerListView {
        AnonymousClass2(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() != 1 || ReactionsContainerLayout.this.getPullingLeftProgress() <= 0.95f) {
                    ReactionsContainerLayout.this.animatePullingBack();
                } else {
                    ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (ReactionsContainerLayout.this.pressedReaction != null && (view instanceof ReactionHolderView) && ((ReactionHolderView) view).currentReaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }
    }

    public class AnonymousClass3 extends LinearLayoutManager {
        AnonymousClass3(Context context, int i, boolean z) {
            super(context, i, z);
        }

        @Override
        public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
            int i2;
            if (i < 0) {
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                if (reactionsContainerLayout.pullingLeftOffset != 0.0f) {
                    float pullingLeftProgress = reactionsContainerLayout.getPullingLeftProgress();
                    ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                    reactionsContainerLayout2.pullingLeftOffset += i;
                    if ((pullingLeftProgress > 1.0f) != (reactionsContainerLayout2.getPullingLeftProgress() > 1.0f)) {
                        try {
                            ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                        } catch (Exception unused) {
                        }
                    }
                    ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                    float f = reactionsContainerLayout3.pullingLeftOffset;
                    if (f < 0.0f) {
                        i2 = (int) f;
                        reactionsContainerLayout3.pullingLeftOffset = 0.0f;
                    } else {
                        i2 = 0;
                    }
                    FrameLayout frameLayout = reactionsContainerLayout3.customReactionsContainer;
                    if (frameLayout != null) {
                        frameLayout.invalidate();
                    }
                    ReactionsContainerLayout.this.recyclerListView.invalidate();
                    i = i2;
                }
            }
            int scrollHorizontallyBy = super.scrollHorizontallyBy(i, recycler, state);
            if (i > 0 && scrollHorizontallyBy == 0 && ReactionsContainerLayout.this.recyclerListView.getScrollState() == 1 && ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                ValueAnimator valueAnimator = ReactionsContainerLayout.this.pullingDownBackAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    ReactionsContainerLayout.this.pullingDownBackAnimator.cancel();
                }
                float pullingLeftProgress2 = ReactionsContainerLayout.this.getPullingLeftProgress();
                float f2 = pullingLeftProgress2 > 1.0f ? 0.05f : 0.6f;
                ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                reactionsContainerLayout4.pullingLeftOffset += i * f2;
                if ((pullingLeftProgress2 > 1.0f) != (reactionsContainerLayout4.getPullingLeftProgress() > 1.0f)) {
                    try {
                        ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                    } catch (Exception unused2) {
                    }
                }
                FrameLayout frameLayout2 = ReactionsContainerLayout.this.customReactionsContainer;
                if (frameLayout2 != null) {
                    frameLayout2.invalidate();
                }
                ReactionsContainerLayout.this.recyclerListView.invalidate();
            }
            return scrollHorizontallyBy;
        }
    }

    public class AnonymousClass4 extends RecyclerView.ItemDecoration {
        AnonymousClass4() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            int i;
            super.getItemOffsets(rect, view, recyclerView, state);
            if (ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                i = 0;
                rect.left = 0;
            } else {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect.left = AndroidUtilities.dp(6.0f);
                }
                rect.right = AndroidUtilities.dp(4.0f);
                if (childAdapterPosition != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    return;
                } else {
                    i = (ReactionsContainerLayout.this.showUnlockPremiumButton() || ReactionsContainerLayout.this.showCustomEmojiReaction()) ? AndroidUtilities.dp(2.0f) : AndroidUtilities.dp(6.0f);
                }
            }
            rect.right = i;
        }
    }

    public class AnonymousClass5 extends RecyclerView.OnScrollListener {
        AnonymousClass5() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (recyclerView.getChildCount() > 2) {
                recyclerView.getLocationInWindow(ReactionsContainerLayout.this.location);
                int i3 = ReactionsContainerLayout.this.location[0];
                View childAt = recyclerView.getChildAt(0);
                childAt.getLocationInWindow(ReactionsContainerLayout.this.location);
                float min = ((1.0f - Math.min(1.0f, (-Math.min(ReactionsContainerLayout.this.location[0] - i3, 0.0f)) / childAt.getWidth())) * 0.39999998f) + 0.6f;
                if (Float.isNaN(min)) {
                    min = 1.0f;
                }
                ReactionsContainerLayout.this.setChildScale(childAt, min);
                View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                float min2 = ((1.0f - Math.min(1.0f, (-Math.min((i3 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth()), 0.0f)) / childAt2.getWidth())) * 0.39999998f) + 0.6f;
                if (Float.isNaN(min2)) {
                    min2 = 1.0f;
                }
                ReactionsContainerLayout.this.setChildScale(childAt2, min2);
            }
            for (int i4 = 1; i4 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i4++) {
                ReactionsContainerLayout.this.setChildScale(ReactionsContainerLayout.this.recyclerListView.getChildAt(i4), 1.0f);
            }
            ReactionsContainerLayout.this.invalidate();
        }
    }

    public class AnonymousClass6 extends RecyclerView.ItemDecoration {
        AnonymousClass6() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            if (childAdapterPosition == 0) {
                rect.left = AndroidUtilities.dp(8.0f);
            }
            if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                rect.right = AndroidUtilities.dp(8.0f);
            }
        }
    }

    public class AnonymousClass7 extends AnimatorListenerAdapter {
        AnonymousClass7() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            ReactionsContainerLayout.this.notificationsLocker.unlock();
        }
    }

    public class AnonymousClass8 implements ValueAnimator.AnimatorUpdateListener {
        final float val$fromProgress;

        AnonymousClass8(float f) {
            r2 = f;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            reactionsContainerLayout.pressedProgress = r2 * (1.0f - reactionsContainerLayout.cancelPressedProgress);
            ReactionsContainerLayout.this.invalidate();
        }
    }

    public class AnonymousClass9 extends AnimatorListenerAdapter {
        AnonymousClass9() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            reactionsContainerLayout.cancelPressedAnimation = null;
            reactionsContainerLayout.pressedProgress = 0.0f;
            ReactionsContainerLayout.this.pressedReaction = null;
            ReactionsContainerLayout.this.invalidate();
        }
    }

    public class Adapter extends AdapterWithDiffUtils {
        public Adapter() {
        }

        public void lambda$onCreateViewHolder$0(View view) {
            view.getLocationOnScreen(new int[2]);
            ReactionsContainerLayout.this.showUnlockPremium(r0[0] + (view.getMeasuredWidth() / 2.0f), r0[1] + (view.getMeasuredHeight() / 2.0f));
        }

        public void lambda$onCreateViewHolder$1(View view) {
            ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
        }

        @Override
        public int getItemCount() {
            return ReactionsContainerLayout.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            return ((InnerItem) ReactionsContainerLayout.this.items.get(i)).viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 3) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) viewHolder.itemView;
                reactionHolderView.setScaleX(1.0f);
                reactionHolderView.setScaleY(1.0f);
                reactionHolderView.setReaction(((InnerItem) ReactionsContainerLayout.this.items.get(i)).reaction, i);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            InternalImageView internalImageView;
            PorterDuffColorFilter porterDuffColorFilter;
            if (i == 1) {
                ReactionsContainerLayout.this.premiumLockContainer = new FrameLayout(ReactionsContainerLayout.this.getContext());
                ReactionsContainerLayout.this.premiumLockIconView = new PremiumLockIconView(ReactionsContainerLayout.this.getContext(), PremiumLockIconView.TYPE_REACTIONS);
                PremiumLockIconView premiumLockIconView = ReactionsContainerLayout.this.premiumLockIconView;
                int color = Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon);
                int i2 = Theme.key_dialogBackground;
                premiumLockIconView.setColor(ColorUtils.blendARGB(color, Theme.getColor(i2), 0.7f));
                ReactionsContainerLayout.this.premiumLockIconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), PorterDuff.Mode.MULTIPLY));
                ReactionsContainerLayout.this.premiumLockIconView.setScaleX(0.0f);
                ReactionsContainerLayout.this.premiumLockIconView.setScaleY(0.0f);
                ReactionsContainerLayout.this.premiumLockIconView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                reactionsContainerLayout.premiumLockContainer.addView(reactionsContainerLayout.premiumLockIconView, LayoutHelper.createFrame(26, 26, 17));
                ReactionsContainerLayout.this.premiumLockIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ReactionsContainerLayout.Adapter.this.lambda$onCreateViewHolder$0(view2);
                    }
                });
                view = ReactionsContainerLayout.this.premiumLockContainer;
            } else if (i != 2) {
                ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                view = new ReactionHolderView(reactionsContainerLayout2.getContext(), true);
            } else {
                ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                reactionsContainerLayout3.customReactionsContainer = new CustomReactionsContainer(reactionsContainerLayout4.getContext());
                ReactionsContainerLayout reactionsContainerLayout5 = ReactionsContainerLayout.this;
                ReactionsContainerLayout reactionsContainerLayout6 = ReactionsContainerLayout.this;
                reactionsContainerLayout5.customEmojiReactionsIconView = new InternalImageView(reactionsContainerLayout6.getContext());
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setImageResource(R.drawable.msg_reactions_expand);
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if (ReactionsContainerLayout.this.type == 1 || ReactionsContainerLayout.this.type == 2 || ReactionsContainerLayout.this.type == 4) {
                    internalImageView = ReactionsContainerLayout.this.customEmojiReactionsIconView;
                    porterDuffColorFilter = new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY);
                } else {
                    internalImageView = ReactionsContainerLayout.this.customEmojiReactionsIconView;
                    porterDuffColorFilter = new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY);
                }
                internalImageView.setColorFilter(porterDuffColorFilter);
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(28.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_listSelector), 40)));
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setContentDescription(LocaleController.getString(R.string.AccDescrExpandPanel));
                ReactionsContainerLayout reactionsContainerLayout7 = ReactionsContainerLayout.this;
                reactionsContainerLayout7.customReactionsContainer.addView(reactionsContainerLayout7.customEmojiReactionsIconView, LayoutHelper.createFrame(30, 30, 17));
                ReactionsContainerLayout.this.customEmojiReactionsIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ReactionsContainerLayout.Adapter.this.lambda$onCreateViewHolder$1(view2);
                    }
                });
                view = ReactionsContainerLayout.this.customReactionsContainer;
            }
            int topOffset = ((ReactionsContainerLayout.this.getLayoutParams().height - ((int) ReactionsContainerLayout.this.getTopOffset())) - ReactionsContainerLayout.this.getPaddingTop()) - ReactionsContainerLayout.this.getPaddingBottom();
            view.setLayoutParams(new RecyclerView.LayoutParams(topOffset - AndroidUtilities.dp(12.0f), topOffset));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition;
            if ((viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 3) && (adapterPosition = viewHolder.getAdapterPosition()) >= 0 && adapterPosition < ReactionsContainerLayout.this.items.size()) {
                ((ReactionHolderView) viewHolder.itemView).updateSelected(((InnerItem) ReactionsContainerLayout.this.items.get(adapterPosition)).reaction, false);
            }
            super.onViewAttachedToWindow(viewHolder);
        }

        public void updateItems(boolean z) {
            ReactionsContainerLayout.this.oldItems.clear();
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            reactionsContainerLayout.oldItems.addAll(reactionsContainerLayout.items);
            ReactionsContainerLayout.this.items.clear();
            for (int i = 0; i < ReactionsContainerLayout.this.visibleReactionsList.size(); i++) {
                ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) ReactionsContainerLayout.this.visibleReactionsList.get(i);
                ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                reactionsContainerLayout2.items.add(new InnerItem(visibleReaction.emojicon == null ? 3 : 0, visibleReaction));
            }
            if (ReactionsContainerLayout.this.showUnlockPremiumButton()) {
                ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                reactionsContainerLayout3.items.add(new InnerItem(1, null));
            }
            if (ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                reactionsContainerLayout4.items.add(new InnerItem(2, null));
            }
            if (!z) {
                super.notifyDataSetChanged();
            } else {
                ReactionsContainerLayout reactionsContainerLayout5 = ReactionsContainerLayout.this;
                setItems(reactionsContainerLayout5.oldItems, reactionsContainerLayout5.items);
            }
        }
    }

    private class CustomReactionsContainer extends FrameLayout {
        Paint backgroundPaint;

        public CustomReactionsContainer(Context context) {
            super(context);
            this.backgroundPaint = new Paint(1);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.backgroundPaint.setColor((ReactionsContainerLayout.this.type == 1 || ReactionsContainerLayout.this.type == 2 || ReactionsContainerLayout.this.type == 4) ? ColorUtils.setAlphaComponent(-1, 30) : ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, ReactionsContainerLayout.this.resourcesProvider), Theme.getColor(Theme.key_dialogBackground, ReactionsContainerLayout.this.resourcesProvider), 0.7f));
            float measuredHeight = getMeasuredHeight() / 2.0f;
            float measuredWidth = getMeasuredWidth() / 2.0f;
            View childAt = getChildAt(0);
            float measuredWidth2 = (getMeasuredWidth() - AndroidUtilities.dpf2(6.0f)) / 2.0f;
            float expandSize = ReactionsContainerLayout.this.expandSize();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(measuredWidth - measuredWidth2, (measuredHeight - measuredWidth2) - expandSize, measuredWidth + measuredWidth2, measuredHeight + measuredWidth2 + expandSize);
            canvas.save();
            canvas.scale(childAt.getScaleX(), childAt.getScaleY(), measuredWidth, measuredHeight);
            canvas.drawRoundRect(rectF, measuredWidth2, measuredWidth2, this.backgroundPaint);
            canvas.restore();
            canvas.save();
            canvas.translate(0.0f, expandSize);
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }

    public class InnerItem extends AdapterWithDiffUtils.Item {
        ReactionsLayoutInBubble.VisibleReaction reaction;

        public InnerItem(int i, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            super(i, false);
            this.reaction = visibleReaction;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            InnerItem innerItem = (InnerItem) obj;
            int i = this.viewType;
            int i2 = innerItem.viewType;
            if (i != i2 || (i != 0 && i != 3)) {
                return i == i2;
            }
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.reaction;
            return visibleReaction != null && visibleReaction.equals(innerItem.reaction);
        }
    }

    public class InternalImageView extends ImageView {
        boolean isEnter;
        ValueAnimator valueAnimator;

        public InternalImageView(Context context) {
            super(context);
        }

        public void lambda$play$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setScaleX(floatValue);
            setScaleY(floatValue);
            ReactionsContainerLayout.this.customReactionsContainer.invalidate();
        }

        public void play(int i, boolean z) {
            this.isEnter = true;
            invalidate();
            ValueAnimator valueAnimator = this.valueAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.valueAnimator.cancel();
            }
            if (!z) {
                setScaleX(1.0f);
                setScaleY(1.0f);
                return;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(getScaleX(), 1.0f);
            this.valueAnimator = ofFloat;
            ofFloat.setInterpolator(AndroidUtilities.overshootInterpolator);
            this.valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ReactionsContainerLayout.InternalImageView.this.lambda$play$0(valueAnimator2);
                }
            });
            this.valueAnimator.setStartDelay(i * ReactionsContainerLayout.this.durationScale);
            this.valueAnimator.setDuration(300L);
            this.valueAnimator.start();
        }

        public void resetAnimation() {
            this.isEnter = false;
            setScaleX(0.0f);
            setScaleY(0.0f);
            ReactionsContainerLayout.this.customReactionsContainer.invalidate();
            ValueAnimator valueAnimator = this.valueAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }
    }

    public final class LeftRightShadowsListener extends RecyclerView.OnScrollListener {
        private ValueAnimator leftAnimator;
        private boolean leftVisible;
        private ValueAnimator rightAnimator;
        private boolean rightVisible;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final Runnable val$onEnd;

            AnonymousClass1(Runnable runnable) {
                r2 = runnable;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                r2.run();
            }
        }

        private LeftRightShadowsListener() {
        }

        LeftRightShadowsListener(ReactionsContainerLayout reactionsContainerLayout, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void lambda$onScrolled$0(Float f) {
            ReactionsContainerLayout.this.leftShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.leftAlpha = f.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        public void lambda$onScrolled$1() {
            this.leftAnimator = null;
        }

        public void lambda$onScrolled$2(Float f) {
            ReactionsContainerLayout.this.rightShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.rightAlpha = f.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        public void lambda$onScrolled$3() {
            this.rightAnimator = null;
        }

        public static void lambda$startAnimator$4(Consumer consumer, ValueAnimator valueAnimator) {
            consumer.accept((Float) valueAnimator.getAnimatedValue());
        }

        private ValueAnimator startAnimator(float f, float f2, final Consumer consumer, Runnable runnable) {
            ValueAnimator duration = ValueAnimator.ofFloat(f, f2).setDuration(Math.abs(f2 - f) * 150.0f);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ReactionsContainerLayout.LeftRightShadowsListener.lambda$startAnimator$4(Consumer.this, valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                final Runnable val$onEnd;

                AnonymousClass1(Runnable runnable2) {
                    r2 = runnable2;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    r2.run();
                }
            });
            duration.start();
            return duration;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            boolean z = ReactionsContainerLayout.this.linearLayoutManager.findFirstVisibleItemPosition() != 0;
            if (z != this.leftVisible) {
                ValueAnimator valueAnimator = this.leftAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.leftAnimator = startAnimator(ReactionsContainerLayout.this.leftAlpha, z ? 1.0f : 0.0f, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$0((Float) obj);
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$1();
                    }
                });
                this.leftVisible = z;
            }
            boolean z2 = ReactionsContainerLayout.this.linearLayoutManager.findLastVisibleItemPosition() != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1;
            if (z2 != this.rightVisible) {
                ValueAnimator valueAnimator2 = this.rightAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.rightAnimator = startAnimator(ReactionsContainerLayout.this.rightAlpha, z2 ? 1.0f : 0.0f, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$2((Float) obj);
                    }
                }, new Runnable() {
                    @Override
                    public final void run() {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.lambda$onScrolled$3();
                    }
                });
                this.rightVisible = z2;
            }
        }
    }

    public final class ReactionHolderView extends FrameLayout {
        public ReactionsLayoutInBubble.VisibleReaction currentReaction;
        public boolean drawSelected;
        public ValueAnimator enterAnimator;
        public BackupImageView enterImageView;
        public float enterScale;
        public boolean hasEnterAnimation;
        private boolean isEnter;
        public boolean isLocked;
        public PremiumLockIconView lockIconView;
        Runnable longPressRunnable;
        public BackupImageView loopImageView;
        public StarsReactionsSheet.Particles particles;
        Runnable playRunnable;
        public int position;
        private ImageReceiver preloadImageReceiver;
        boolean pressed;
        public BackupImageView pressedBackupImageView;
        float pressedX;
        float pressedY;
        private final boolean recyclerReaction;
        public boolean selected;
        public boolean shouldSwitchToLoopView;
        public float sideScale;
        public boolean switchedToLoopView;
        boolean touchable;
        public boolean waitingAnimation;

        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                if (ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().start();
                }
                ReactionHolderView.this.waitingAnimation = false;
            }
        }

        public class AnonymousClass2 extends BackupImageView {
            final ReactionsContainerLayout val$this$0;

            class AnonymousClass1 extends ImageReceiver {
                AnonymousClass1(View view) {
                    super(view);
                }

                @Override
                public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                    if (drawable instanceof RLottieDrawable) {
                        ((RLottieDrawable) drawable).setCurrentFrame(0, false, true);
                    }
                    return super.setImageBitmapByKey(drawable, str, i, z, i2);
                }
            }

            AnonymousClass2(Context context, ReactionsContainerLayout reactionsContainerLayout) {
                super(context);
                this.val$this$0 = reactionsContainerLayout;
            }

            public void lambda$dispatchDraw$0() {
                ReactionHolderView.this.enterImageView.setVisibility(4);
            }

            @Override
            protected ImageReceiver createImageReciever() {
                return new ImageReceiver(this) {
                    AnonymousClass1(View this) {
                        super(this);
                    }

                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                        if (drawable instanceof RLottieDrawable) {
                            ((RLottieDrawable) drawable).setCurrentFrame(0, false, true);
                        }
                        return super.setImageBitmapByKey(drawable, str, i, z, i2);
                    }
                };
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (this.imageReceiver.getLottieAnimation() != null && !ReactionHolderView.this.waitingAnimation) {
                    this.imageReceiver.getLottieAnimation().start();
                }
                ReactionHolderView reactionHolderView = ReactionHolderView.this;
                if (reactionHolderView.shouldSwitchToLoopView && !reactionHolderView.switchedToLoopView && this.imageReceiver.getLottieAnimation() != null && this.imageReceiver.getLottieAnimation().isLastFrame() && ReactionHolderView.this.loopImageView.imageReceiver.getLottieAnimation() != null && ReactionHolderView.this.loopImageView.imageReceiver.getLottieAnimation().hasBitmap()) {
                    ReactionHolderView reactionHolderView2 = ReactionHolderView.this;
                    reactionHolderView2.switchedToLoopView = true;
                    reactionHolderView2.loopImageView.imageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
                    ReactionHolderView.this.loopImageView.setVisibility(0);
                    if (ReactionsContainerLayout.this.onSwitchedToLoopView != null) {
                        ReactionsContainerLayout.this.onSwitchedToLoopView.run();
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            ReactionsContainerLayout.ReactionHolderView.AnonymousClass2.this.lambda$dispatchDraw$0();
                        }
                    });
                }
                invalidate();
            }

            @Override
            public void invalidate() {
                if (HwEmojis.grabIfWeakDevice(this, ReactionsContainerLayout.this)) {
                    return;
                }
                super.invalidate();
                ReactionsContainerLayout.this.invalidate();
            }

            @Override
            public void invalidate(int i, int i2, int i3, int i4) {
                if (HwEmojis.grabIfWeakDevice(this)) {
                    return;
                }
                super.invalidate(i, i2, i3, i4);
            }

            @Override
            public void invalidate(android.graphics.Rect rect) {
                if (HwEmojis.grabIfWeakDevice(this, ReactionsContainerLayout.this)) {
                    return;
                }
                super.invalidate(rect);
                ReactionsContainerLayout.this.invalidate();
            }
        }

        public class AnonymousClass3 extends BackupImageView {
            final ReactionsContainerLayout val$this$0;

            class AnonymousClass1 extends ImageReceiver {
                AnonymousClass1(View view) {
                    super(view);
                }

                @Override
                public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                    boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z, i2);
                    if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                        RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                        rLottieDrawable.setCurrentFrame(0, false, true);
                        rLottieDrawable.stop();
                    }
                    return imageBitmapByKey;
                }
            }

            AnonymousClass3(Context context, ReactionsContainerLayout reactionsContainerLayout) {
                super(context);
                r3 = reactionsContainerLayout;
            }

            @Override
            protected ImageReceiver createImageReciever() {
                return new ImageReceiver(this) {
                    AnonymousClass1(View this) {
                        super(this);
                    }

                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                        boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z, i2);
                        if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                            rLottieDrawable.setCurrentFrame(0, false, true);
                            rLottieDrawable.stop();
                        }
                        return imageBitmapByKey;
                    }
                };
            }

            @Override
            public void invalidate() {
                if (HwEmojis.grabIfWeakDevice(this)) {
                    return;
                }
                super.invalidate();
            }

            @Override
            public void invalidate(int i, int i2, int i3, int i4) {
                if (HwEmojis.grabIfWeakDevice(this)) {
                    return;
                }
                super.invalidate(i, i2, i3, i4);
            }

            @Override
            public void onDraw(Canvas canvas) {
                ReactionHolderView.this.checkPlayLoopImage();
                super.onDraw(canvas);
            }
        }

        public class AnonymousClass4 extends BackupImageView {
            final ReactionsContainerLayout val$this$0;

            AnonymousClass4(Context context, ReactionsContainerLayout reactionsContainerLayout) {
                super(context);
                r3 = reactionsContainerLayout;
            }

            @Override
            public void invalidate() {
                super.invalidate();
                ReactionsContainerLayout.this.invalidate();
            }

            @Override
            public void onDraw(Canvas canvas) {
                AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
                ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
                if (imageReceiver != null && imageReceiver.getLottieAnimation() != null) {
                    imageReceiver.getLottieAnimation().start();
                }
                super.onDraw(canvas);
            }
        }

        public class AnonymousClass5 implements Runnable {
            AnonymousClass5() {
            }

            @Override
            public void run() {
                try {
                    ReactionHolderView.this.performHapticFeedback(0);
                } catch (Exception unused) {
                }
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                reactionsContainerLayout.pressedReactionPosition = reactionsContainerLayout.visibleReactionsList.indexOf(ReactionHolderView.this.currentReaction);
                ReactionHolderView reactionHolderView = ReactionHolderView.this;
                ReactionsContainerLayout.this.pressedReaction = reactionHolderView.currentReaction;
                ReactionsContainerLayout.this.invalidate();
            }
        }

        ReactionHolderView(Context context, boolean z) {
            super(context);
            this.preloadImageReceiver = new ImageReceiver();
            this.sideScale = 1.0f;
            this.drawSelected = true;
            this.playRunnable = new Runnable() {
                AnonymousClass1() {
                }

                @Override
                public void run() {
                    if (ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                        ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().start();
                    }
                    ReactionHolderView.this.waitingAnimation = false;
                }
            };
            this.enterScale = 1.0f;
            this.longPressRunnable = new Runnable() {
                AnonymousClass5() {
                }

                @Override
                public void run() {
                    try {
                        ReactionHolderView.this.performHapticFeedback(0);
                    } catch (Exception unused) {
                    }
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.pressedReactionPosition = reactionsContainerLayout.visibleReactionsList.indexOf(ReactionHolderView.this.currentReaction);
                    ReactionHolderView reactionHolderView = ReactionHolderView.this;
                    ReactionsContainerLayout.this.pressedReaction = reactionHolderView.currentReaction;
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            this.touchable = true;
            this.recyclerReaction = z;
            this.enterImageView = new AnonymousClass2(context, ReactionsContainerLayout.this);
            this.loopImageView = new BackupImageView(context) {
                final ReactionsContainerLayout val$this$0;

                class AnonymousClass1 extends ImageReceiver {
                    AnonymousClass1(View this) {
                        super(this);
                    }

                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                        boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z, i2);
                        if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                            rLottieDrawable.setCurrentFrame(0, false, true);
                            rLottieDrawable.stop();
                        }
                        return imageBitmapByKey;
                    }
                }

                AnonymousClass3(Context context2, ReactionsContainerLayout reactionsContainerLayout) {
                    super(context2);
                    r3 = reactionsContainerLayout;
                }

                @Override
                protected ImageReceiver createImageReciever() {
                    return new ImageReceiver(this) {
                        AnonymousClass1(View this) {
                            super(this);
                        }

                        @Override
                        public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z2, int i2) {
                            boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z2, i2);
                            if (imageBitmapByKey && (drawable instanceof RLottieDrawable)) {
                                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                                rLottieDrawable.setCurrentFrame(0, false, true);
                                rLottieDrawable.stop();
                            }
                            return imageBitmapByKey;
                        }
                    };
                }

                @Override
                public void invalidate() {
                    if (HwEmojis.grabIfWeakDevice(this)) {
                        return;
                    }
                    super.invalidate();
                }

                @Override
                public void invalidate(int i, int i2, int i3, int i4) {
                    if (HwEmojis.grabIfWeakDevice(this)) {
                        return;
                    }
                    super.invalidate(i, i2, i3, i4);
                }

                @Override
                public void onDraw(Canvas canvas) {
                    ReactionHolderView.this.checkPlayLoopImage();
                    super.onDraw(canvas);
                }
            };
            this.enterImageView.getImageReceiver().setAutoRepeat(0);
            this.enterImageView.getImageReceiver().setAllowStartLottieAnimation(false);
            this.pressedBackupImageView = new BackupImageView(context2) {
                final ReactionsContainerLayout val$this$0;

                AnonymousClass4(Context context2, ReactionsContainerLayout reactionsContainerLayout) {
                    super(context2);
                    r3 = reactionsContainerLayout;
                }

                @Override
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }

                @Override
                public void onDraw(Canvas canvas) {
                    AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
                    ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
                    if (imageReceiver != null && imageReceiver.getLottieAnimation() != null) {
                        imageReceiver.getLottieAnimation().start();
                    }
                    super.onDraw(canvas);
                }
            };
            addView(this.enterImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.pressedBackupImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.loopImageView, LayoutHelper.createFrame(34, 34, 17));
            if (ReactionsContainerLayout.this.type == 4) {
                LayoutTransition layoutTransition = new LayoutTransition();
                layoutTransition.setDuration(100L);
                layoutTransition.enableTransitionType(4);
                setLayoutTransition(layoutTransition);
            }
            this.enterImageView.setLayerNum(Integer.MAX_VALUE);
            this.loopImageView.setLayerNum(Integer.MAX_VALUE);
            this.loopImageView.imageReceiver.setAutoRepeat(0);
            this.loopImageView.imageReceiver.setAllowStartAnimation(false);
            this.loopImageView.imageReceiver.setAllowStartLottieAnimation(false);
            this.pressedBackupImageView.setLayerNum(Integer.MAX_VALUE);
        }

        public void lambda$play$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.enterScale = floatValue;
            this.loopImageView.setScaleY(floatValue * (this.selected ? 0.76f : 1.0f));
            this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
        }

        public void setReaction(org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction r12, int r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.setReaction(org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction, int):void");
        }

        private void updateImage(org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction r29) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.updateImage(org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction):void");
        }

        public void checkPlayLoopImage() {
            BackupImageView backupImageView = this.loopImageView;
            AnimatedEmojiDrawable animatedEmojiDrawable = backupImageView.animatedEmojiDrawable;
            ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : backupImageView.imageReceiver;
            if (imageReceiver == null || imageReceiver.getLottieAnimation() == null) {
                return;
            }
            ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
            if (reactionsContainerLayout.reactionsWindow != null || this.pressed || !reactionsContainerLayout.allReactionsIsDefault) {
                imageReceiver.getLottieAnimation().start();
            } else if (imageReceiver.getLottieAnimation().getCurrentFrame() <= 2) {
                imageReceiver.getLottieAnimation().stop();
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.selected && this.drawSelected) {
                float measuredWidth = getMeasuredWidth() >> 1;
                float measuredHeight = getMeasuredHeight() >> 1;
                float measuredWidth2 = (getMeasuredWidth() >> 1) - AndroidUtilities.dp(1.0f);
                ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.currentReaction;
                canvas.drawCircle(measuredWidth, measuredHeight, measuredWidth2, (visibleReaction == null || !visibleReaction.isStar) ? ReactionsContainerLayout.this.selectedPaint : ReactionsContainerLayout.this.starSelectedPaint);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.loopImageView.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                if (this.position == 0) {
                    this.loopImageView.animatedEmojiDrawable.getImageReceiver().setRoundRadius(AndroidUtilities.dp(6.0f), 0, 0, AndroidUtilities.dp(6.0f));
                } else {
                    this.loopImageView.animatedEmojiDrawable.getImageReceiver().setRoundRadius(this.selected ? AndroidUtilities.dp(6.0f) : 0);
                }
            }
            ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = this.currentReaction;
            if (visibleReaction2 != null && visibleReaction2.isStar && this.particles != null && LiteMode.isEnabled(8200)) {
                RectF rectF = AndroidUtilities.rectTmp;
                float height = ((int) (getHeight() * 0.7f)) / 2.0f;
                rectF.set((getWidth() / 2.0f) - height, (getHeight() / 2.0f) - height, (getWidth() / 2.0f) + height, (getHeight() / 2.0f) + height);
                RLottieDrawable lottieAnimation = this.enterImageView.getImageReceiver().getLottieAnimation();
                this.particles.setVisible((lottieAnimation == null || lottieAnimation.getCurrentFrame() <= 30) ? 0.0f : Utilities.clamp01((lottieAnimation.getCurrentFrame() - 30) / 30.0f));
                this.particles.setBounds(rectF);
                this.particles.process();
                this.particles.draw(canvas, -673522);
                invalidate();
            }
            super.dispatchDraw(canvas);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            resetAnimation();
            this.preloadImageReceiver.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.preloadImageReceiver.onDetachedFromWindow();
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.currentReaction;
            if (visibleReaction != null) {
                String str = visibleReaction.emojicon;
                if (str == null) {
                    str = LocaleController.getString(R.string.AccDescrCustomEmoji);
                }
                accessibilityNodeInfo.setText(str);
                accessibilityNodeInfo.setEnabled(true);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!this.touchable || ReactionsContainerLayout.this.cancelPressedAnimation != null) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                this.pressed = true;
                this.pressedX = motionEvent.getX();
                this.pressedY = motionEvent.getY();
                if (this.sideScale == 1.0f && !this.isLocked && ReactionsContainerLayout.this.type != 3 && ReactionsContainerLayout.this.type != 4 && ReactionsContainerLayout.this.type != 5) {
                    AndroidUtilities.runOnUIThread(this.longPressRunnable, ViewConfiguration.getLongPressTimeout());
                }
            }
            float scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2.0f;
            if ((motionEvent.getAction() == 2 && (Math.abs(this.pressedX - motionEvent.getX()) > scaledTouchSlop || Math.abs(this.pressedY - motionEvent.getY()) > scaledTouchSlop)) || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 1 && this.pressed && ((ReactionsContainerLayout.this.pressedReaction == null || ReactionsContainerLayout.this.pressedProgress > 0.8f) && ReactionsContainerLayout.this.delegate != null)) {
                    ReactionsContainerLayout.this.clicked = true;
                    long currentTimeMillis = System.currentTimeMillis();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    if (currentTimeMillis - reactionsContainerLayout.lastReactionSentTime > 300) {
                        reactionsContainerLayout.lastReactionSentTime = System.currentTimeMillis();
                        ReactionsContainerLayout.this.delegate.onReactionClicked(this, this.currentReaction, ReactionsContainerLayout.this.pressedProgress > 0.8f, false);
                    }
                }
                if (!ReactionsContainerLayout.this.clicked) {
                    ReactionsContainerLayout.this.cancelPressed();
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                this.pressed = false;
            }
            return true;
        }

        public boolean play(int i) {
            if (!ReactionsContainerLayout.this.animationEnabled) {
                resetAnimation();
                this.isEnter = true;
                if (!this.hasEnterAnimation) {
                    this.loopImageView.setVisibility(0);
                    this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
                }
                return false;
            }
            AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
            if (this.hasEnterAnimation) {
                if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache() && !this.isEnter) {
                    this.isEnter = true;
                    if (i == 0) {
                        this.waitingAnimation = false;
                        this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        this.playRunnable.run();
                    } else {
                        this.waitingAnimation = true;
                        this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        AndroidUtilities.runOnUIThread(this.playRunnable, i);
                    }
                    return true;
                }
                if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && this.isEnter && !this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.enterImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                }
                this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                this.loopImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
            } else if (!this.isEnter) {
                this.enterScale = 0.0f;
                this.loopImageView.setScaleX(0.0f);
                this.loopImageView.setScaleY(0.0f);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.enterAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ReactionsContainerLayout.ReactionHolderView.this.lambda$play$0(valueAnimator);
                    }
                });
                this.enterAnimator.setDuration(150L);
                this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.enterAnimator.setStartDelay(i * ReactionsContainerLayout.this.durationScale);
                this.enterAnimator.start();
                this.isEnter = true;
            }
            return false;
        }

        public void resetAnimation() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.resetAnimation():void");
        }

        public void updateSelected(ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z) {
            boolean z2 = this.selected;
            boolean contains = ReactionsContainerLayout.this.selectedReactions.contains(visibleReaction);
            this.selected = contains;
            if (contains != z2) {
                if (z) {
                    ViewPropertyAnimator duration = this.loopImageView.animate().scaleX(this.enterScale * (this.selected ? 0.76f : 1.0f)).scaleY(this.enterScale * (this.selected ? 0.76f : 1.0f)).setDuration(240L);
                    CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                    duration.setInterpolator(cubicBezierInterpolator).start();
                    this.enterImageView.animate().scaleX(this.enterScale * (this.selected ? 0.76f : 1.0f)).scaleY(this.enterScale * (this.selected ? 0.76f : 1.0f)).setDuration(240L).setInterpolator(cubicBezierInterpolator).start();
                } else {
                    this.loopImageView.setScaleX(this.enterScale * (contains ? 0.76f : 1.0f));
                    this.loopImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.enterImageView.setScaleX(this.enterScale * (this.selected ? 0.76f : 1.0f));
                    this.enterImageView.setScaleY(this.enterScale * (this.selected ? 0.76f : 1.0f));
                }
                requestLayout();
                invalidate();
            }
        }
    }

    public interface ReactionsContainerDelegate {

        public abstract class CC {
            public static boolean $default$drawBackground(ReactionsContainerDelegate reactionsContainerDelegate) {
                return false;
            }

            public static void $default$drawRoundRect(ReactionsContainerDelegate reactionsContainerDelegate, Canvas canvas, RectF rectF, float f, float f2, float f3, int i, boolean z) {
            }

            public static boolean $default$needEnterText(ReactionsContainerDelegate reactionsContainerDelegate) {
                return false;
            }

            public static void $default$onEmojiWindowDismissed(ReactionsContainerDelegate reactionsContainerDelegate) {
            }
        }

        boolean drawBackground();

        void drawRoundRect(Canvas canvas, RectF rectF, float f, float f2, float f3, int i, boolean z);

        boolean needEnterText();

        void onEmojiWindowDismissed();

        void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2);
    }

    public ReactionsContainerLayout(final int i, BaseFragment baseFragment, Context context, int i2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        Paint paint;
        int blendARGB;
        this.items = new ArrayList();
        this.oldItems = new ArrayList();
        this.bgPaint = new Paint(1);
        this.leftShadowPaint = new Paint(1);
        this.rightShadowPaint = new Paint(1);
        this.transitionProgress = 1.0f;
        this.rect = new RectF();
        this.mPath = new Path();
        this.radius = AndroidUtilities.dp(72.0f);
        float dp = AndroidUtilities.dp(8.0f);
        this.bigCircleRadius = dp;
        this.smallCircleRadius = dp / 2.0f;
        this.bigCircleOffset = AndroidUtilities.dp(36.0f);
        this.visibleReactionsList = new ArrayList(20);
        this.premiumLockedReactions = new ArrayList(10);
        this.allReactionsList = new ArrayList(20);
        this.rectF = new RectF();
        this.hasStar = false;
        this.selectedReactions = new HashSet();
        this.alwaysSelectedReactions = new HashSet();
        this.location = new int[2];
        this.shadowPad = new android.graphics.Rect();
        this.triggeredReactions = new ArrayList();
        this.lastVisibleViews = new HashSet();
        this.lastVisibleViewsTmp = new HashSet();
        Paint paint2 = new Paint(1);
        this.selectedPaint = paint2;
        Paint paint3 = new Paint(1);
        this.starSelectedPaint = paint3;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.isHiddenNextReaction = true;
        this.paused = false;
        this.type = i;
        this.durationScale = Settings.Global.getFloat(context.getContentResolver(), "animator_duration_scale", 1.0f);
        paint2.setColor(Theme.getColor(Theme.key_listSelector, resourcesProvider));
        paint3.setColor(Theme.getColor(Theme.key_reactionStarSelector, resourcesProvider));
        this.resourcesProvider = resourcesProvider;
        this.currentAccount = i2;
        this.fragment = baseFragment;
        ReactionHolderView reactionHolderView = new ReactionHolderView(context, false);
        this.nextRecentReaction = reactionHolderView;
        reactionHolderView.setVisibility(8);
        ReactionHolderView reactionHolderView2 = this.nextRecentReaction;
        reactionHolderView2.touchable = false;
        reactionHolderView2.pressedBackupImageView.setVisibility(8);
        addView(this.nextRecentReaction);
        this.animationEnabled = SharedConfig.animationsEnabled() && SharedConfig.getDevicePerformanceClass() != 0;
        this.shadow = ContextCompat.getDrawable(context, R.drawable.reactions_bubble_shadow).mutate();
        android.graphics.Rect rect = this.shadowPad;
        int dp2 = AndroidUtilities.dp(7.0f);
        rect.bottom = dp2;
        rect.right = dp2;
        rect.top = dp2;
        rect.left = dp2;
        this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelShadow), PorterDuff.Mode.MULTIPLY));
        AnonymousClass2 anonymousClass2 = new RecyclerListView(context) {
            AnonymousClass2(Context context2) {
                super(context2);
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (motionEvent.getAction() != 1 || ReactionsContainerLayout.this.getPullingLeftProgress() <= 0.95f) {
                        ReactionsContainerLayout.this.animatePullingBack();
                    } else {
                        ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
                    }
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (ReactionsContainerLayout.this.pressedReaction != null && (view instanceof ReactionHolderView) && ((ReactionHolderView) view).currentReaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }
        };
        this.recyclerListView = anonymousClass2;
        anonymousClass2.setClipChildren(false);
        anonymousClass2.setClipToPadding(false);
        this.linearLayoutManager = new LinearLayoutManager(context2, 0, false) {
            AnonymousClass3(Context context2, int i3, boolean z) {
                super(context2, i3, z);
            }

            @Override
            public int scrollHorizontallyBy(int i3, RecyclerView.Recycler recycler, RecyclerView.State state) {
                int i22;
                if (i3 < 0) {
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    if (reactionsContainerLayout.pullingLeftOffset != 0.0f) {
                        float pullingLeftProgress = reactionsContainerLayout.getPullingLeftProgress();
                        ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                        reactionsContainerLayout2.pullingLeftOffset += i3;
                        if ((pullingLeftProgress > 1.0f) != (reactionsContainerLayout2.getPullingLeftProgress() > 1.0f)) {
                            try {
                                ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                            } catch (Exception unused) {
                            }
                        }
                        ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                        float f = reactionsContainerLayout3.pullingLeftOffset;
                        if (f < 0.0f) {
                            i22 = (int) f;
                            reactionsContainerLayout3.pullingLeftOffset = 0.0f;
                        } else {
                            i22 = 0;
                        }
                        FrameLayout frameLayout = reactionsContainerLayout3.customReactionsContainer;
                        if (frameLayout != null) {
                            frameLayout.invalidate();
                        }
                        ReactionsContainerLayout.this.recyclerListView.invalidate();
                        i3 = i22;
                    }
                }
                int scrollHorizontallyBy = super.scrollHorizontallyBy(i3, recycler, state);
                if (i3 > 0 && scrollHorizontallyBy == 0 && ReactionsContainerLayout.this.recyclerListView.getScrollState() == 1 && ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    ValueAnimator valueAnimator = ReactionsContainerLayout.this.pullingDownBackAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        ReactionsContainerLayout.this.pullingDownBackAnimator.cancel();
                    }
                    float pullingLeftProgress2 = ReactionsContainerLayout.this.getPullingLeftProgress();
                    float f2 = pullingLeftProgress2 > 1.0f ? 0.05f : 0.6f;
                    ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                    reactionsContainerLayout4.pullingLeftOffset += i3 * f2;
                    if ((pullingLeftProgress2 > 1.0f) != (reactionsContainerLayout4.getPullingLeftProgress() > 1.0f)) {
                        try {
                            ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                        } catch (Exception unused2) {
                        }
                    }
                    FrameLayout frameLayout2 = ReactionsContainerLayout.this.customReactionsContainer;
                    if (frameLayout2 != null) {
                        frameLayout2.invalidate();
                    }
                    ReactionsContainerLayout.this.recyclerListView.invalidate();
                }
                return scrollHorizontallyBy;
            }
        };
        anonymousClass2.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass4() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect2, View view, RecyclerView recyclerView, RecyclerView.State state) {
                int i3;
                super.getItemOffsets(rect2, view, recyclerView, state);
                if (ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    i3 = 0;
                    rect2.left = 0;
                } else {
                    int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                    if (childAdapterPosition == 0) {
                        rect2.left = AndroidUtilities.dp(6.0f);
                    }
                    rect2.right = AndroidUtilities.dp(4.0f);
                    if (childAdapterPosition != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                        return;
                    } else {
                        i3 = (ReactionsContainerLayout.this.showUnlockPremiumButton() || ReactionsContainerLayout.this.showCustomEmojiReaction()) ? AndroidUtilities.dp(2.0f) : AndroidUtilities.dp(6.0f);
                    }
                }
                rect2.right = i3;
            }
        });
        anonymousClass2.setLayoutManager(this.linearLayoutManager);
        anonymousClass2.setOverScrollMode(2);
        Adapter adapter = new Adapter();
        this.listAdapter = adapter;
        anonymousClass2.setAdapter(adapter);
        anonymousClass2.addOnScrollListener(new LeftRightShadowsListener());
        anonymousClass2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass5() {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i3, int i22) {
                if (recyclerView.getChildCount() > 2) {
                    recyclerView.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int i32 = ReactionsContainerLayout.this.location[0];
                    View childAt = recyclerView.getChildAt(0);
                    childAt.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min = ((1.0f - Math.min(1.0f, (-Math.min(ReactionsContainerLayout.this.location[0] - i32, 0.0f)) / childAt.getWidth())) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min)) {
                        min = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(childAt, min);
                    View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min2 = ((1.0f - Math.min(1.0f, (-Math.min((i32 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth()), 0.0f)) / childAt2.getWidth())) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min2)) {
                        min2 = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(childAt2, min2);
                }
                for (int i4 = 1; i4 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i4++) {
                    ReactionsContainerLayout.this.setChildScale(ReactionsContainerLayout.this.recyclerListView.getChildAt(i4), 1.0f);
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        anonymousClass2.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass6() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect2, View view, RecyclerView recyclerView, RecyclerView.State state) {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect2.left = AndroidUtilities.dp(8.0f);
                }
                if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    rect2.right = AndroidUtilities.dp(8.0f);
                }
            }
        });
        anonymousClass2.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                ReactionsContainerLayout.this.lambda$new$0(view, i3);
            }
        });
        anonymousClass2.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i3) {
                boolean lambda$new$1;
                lambda$new$1 = ReactionsContainerLayout.this.lambda$new$1(i, view, i3);
                return lambda$new$1;
            }
        });
        addView(anonymousClass2, LayoutHelper.createFrame(-1, -1.0f));
        setClipChildren(false);
        setClipToPadding(false);
        invalidateShaders();
        int paddingTop = (anonymousClass2.getLayoutParams().height - anonymousClass2.getPaddingTop()) - anonymousClass2.getPaddingBottom();
        this.nextRecentReaction.getLayoutParams().width = paddingTop - AndroidUtilities.dp(12.0f);
        this.nextRecentReaction.getLayoutParams().height = paddingTop;
        if (i == 2 || i == 4) {
            paint = this.bgPaint;
            blendARGB = ColorUtils.blendARGB(-16777216, -1, 0.13f);
        } else {
            paint = this.bgPaint;
            blendARGB = Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider);
        }
        paint.setColor(blendARGB);
        MediaDataController.getInstance(i2).preloadDefaultReactions();
    }

    public static boolean allowSmoothEnterTransition() {
        return SharedConfig.deviceIsHigh();
    }

    public void animatePullingBack() {
        if (this.pullingLeftOffset != 0.0f) {
            ValueAnimator valueAnimator = this.pullingDownBackAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.pullingLeftOffset, 0.0f);
            this.pullingDownBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ReactionsContainerLayout.this.lambda$animatePullingBack$2(valueAnimator2);
                }
            });
            this.pullingDownBackAnimator.setDuration(150L);
            this.pullingDownBackAnimator.start();
        }
    }

    public void cancelPressed() {
        if (this.pressedReaction != null) {
            this.cancelPressedProgress = 0.0f;
            float f = this.pressedProgress;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.cancelPressedAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                final float val$fromProgress;

                AnonymousClass8(float f2) {
                    r2 = f2;
                }

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.pressedProgress = r2 * (1.0f - reactionsContainerLayout.cancelPressedProgress);
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.addListener(new AnimatorListenerAdapter() {
                AnonymousClass9() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.cancelPressedAnimation = null;
                    reactionsContainerLayout.pressedProgress = 0.0f;
                    ReactionsContainerLayout.this.pressedReaction = null;
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.setDuration(150L);
            this.cancelPressedAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.cancelPressedAnimation.start();
        }
    }

    private void checkPressedProgress(Canvas canvas, ReactionHolderView reactionHolderView) {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        float f = 0.0f;
        float clamp = this.pullingLeftOffset != 0.0f ? Utilities.clamp(reactionHolderView.getLeft() / (getMeasuredWidth() - AndroidUtilities.dp(34.0f)), 1.0f, 0.0f) * getPullingLeftProgress() * AndroidUtilities.dp(46.0f) : 0.0f;
        if (!reactionHolderView.currentReaction.equals(this.pressedReaction)) {
            int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(reactionHolderView);
            float measuredWidth = ((reactionHolderView.getMeasuredWidth() * (this.pressedViewScale - 1.0f)) / 3.0f) - ((reactionHolderView.getMeasuredWidth() * (1.0f - this.otherViewsScale)) * (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1));
            if (childAdapterPosition < this.pressedReactionPosition) {
                reactionHolderView.setPivotX(0.0f);
                reactionHolderView.setTranslationX(-measuredWidth);
            } else {
                reactionHolderView.setPivotX(reactionHolderView.getMeasuredWidth() - clamp);
                reactionHolderView.setTranslationX(measuredWidth - clamp);
            }
            reactionHolderView.setPivotY(reactionHolderView.enterImageView.getY() + reactionHolderView.enterImageView.getMeasuredHeight());
            reactionHolderView.setScaleX(this.otherViewsScale);
            reactionHolderView.setScaleY(this.otherViewsScale);
            reactionHolderView.pressedBackupImageView.setVisibility(4);
            reactionHolderView.enterImageView.setAlpha(1.0f);
            return;
        }
        BackupImageView backupImageView = reactionHolderView.loopImageView.getVisibility() == 0 ? reactionHolderView.loopImageView : reactionHolderView.enterImageView;
        reactionHolderView.setPivotX(reactionHolderView.getMeasuredWidth() >> 1);
        reactionHolderView.setPivotY(backupImageView.getY() + backupImageView.getMeasuredHeight());
        reactionHolderView.setScaleX(this.pressedViewScale);
        reactionHolderView.setScaleY(this.pressedViewScale);
        if (!this.clicked) {
            if (this.cancelPressedAnimation == null) {
                reactionHolderView.pressedBackupImageView.setVisibility(0);
                reactionHolderView.pressedBackupImageView.setAlpha(1.0f);
                if (reactionHolderView.pressedBackupImageView.getImageReceiver().hasBitmapImage() || ((animatedEmojiDrawable = reactionHolderView.pressedBackupImageView.animatedEmojiDrawable) != null && animatedEmojiDrawable.getImageReceiver() != null && reactionHolderView.pressedBackupImageView.animatedEmojiDrawable.getImageReceiver().hasBitmapImage())) {
                    backupImageView.setAlpha(0.0f);
                }
            } else {
                reactionHolderView.pressedBackupImageView.setAlpha(1.0f - this.cancelPressedProgress);
                backupImageView.setAlpha(this.cancelPressedProgress);
            }
            if (this.pressedProgress == 1.0f) {
                this.clicked = true;
                if (System.currentTimeMillis() - this.lastReactionSentTime > 300) {
                    this.lastReactionSentTime = System.currentTimeMillis();
                    this.delegate.onReactionClicked(reactionHolderView, reactionHolderView.currentReaction, true, false);
                }
            }
        }
        canvas.save();
        float x = this.recyclerListView.getX() + reactionHolderView.getX();
        float measuredWidth2 = ((reactionHolderView.getMeasuredWidth() * reactionHolderView.getScaleX()) - reactionHolderView.getMeasuredWidth()) / 2.0f;
        float f2 = x - measuredWidth2;
        if (f2 >= 0.0f || reactionHolderView.getTranslationX() < 0.0f) {
            if (reactionHolderView.getMeasuredWidth() + x + measuredWidth2 > getMeasuredWidth() && reactionHolderView.getTranslationX() <= 0.0f) {
                f = ((getMeasuredWidth() - x) - reactionHolderView.getMeasuredWidth()) - measuredWidth2;
            }
            reactionHolderView.setTranslationX(f - clamp);
        } else {
            reactionHolderView.setTranslationX((-f2) - clamp);
        }
        canvas.translate(this.recyclerListView.getX() + reactionHolderView.getX(), this.recyclerListView.getY() + reactionHolderView.getY());
        canvas.scale(reactionHolderView.getScaleX(), reactionHolderView.getScaleY(), reactionHolderView.getPivotX(), reactionHolderView.getPivotY());
        reactionHolderView.draw(canvas);
        canvas.restore();
    }

    private void checkPressedProgressForOtherViews(View view) {
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(view);
        float measuredWidth = ((view.getMeasuredWidth() * (this.pressedViewScale - 1.0f)) / 3.0f) - ((view.getMeasuredWidth() * (1.0f - this.otherViewsScale)) * (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1));
        if (childAdapterPosition < this.pressedReactionPosition) {
            view.setPivotX(0.0f);
            view.setTranslationX(-measuredWidth);
        } else {
            view.setPivotX(view.getMeasuredWidth());
            view.setTranslationX(measuredWidth);
        }
        view.setScaleX(this.otherViewsScale);
        view.setScaleY(this.otherViewsScale);
    }

    private void drawBubbles(Canvas canvas, float f, float f2, float f3, int i) {
        if (this.type == 1) {
            return;
        }
        canvas.save();
        if (this.isTop) {
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), (AndroidUtilities.lerp(this.rect.top, getMeasuredHeight(), CubicBezierInterpolator.DEFAULT.getInterpolation(this.flipVerticalProgress)) - ((int) Math.ceil((this.rect.height() / 2.0f) * (1.0f - this.transitionProgress)))) + 1.0f);
        } else {
            float f4 = this.rect.bottom;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            canvas.clipRect(0.0f, (AndroidUtilities.lerp(f4, 0.0f, cubicBezierInterpolator.getInterpolation(this.flipVerticalProgress)) - ((int) Math.ceil((this.rect.height() / 2.0f) * (1.0f - this.transitionProgress)))) - 1.0f, getMeasuredWidth(), AndroidUtilities.lerp(getMeasuredHeight() + AndroidUtilities.dp(8.0f), getPaddingTop() - expandSize(), cubicBezierInterpolator.getInterpolation(this.flipVerticalProgress)));
        }
        float width = ((LocaleController.isRTL || this.mirrorX) ? this.bigCircleOffset : getWidth() - this.bigCircleOffset) + this.bubblesOffset;
        float paddingTop = this.isTop ? getPaddingTop() - expandSize() : (getHeight() - getPaddingBottom()) + expandSize();
        int dp = AndroidUtilities.dp(3.0f);
        this.shadow.setAlpha(i);
        this.bgPaint.setAlpha(i);
        float f5 = width - f;
        float f6 = dp;
        float f7 = f6 * f2;
        float f8 = paddingTop - f;
        float f9 = width + f;
        float f10 = paddingTop + f;
        this.shadow.setBounds((int) (f5 - f7), (int) (f8 - f7), (int) (f9 + f7), (int) (f7 + f10));
        this.shadow.draw(canvas);
        if (this.delegate.drawBackground()) {
            this.rectF.set(f5, f8, f9, f10);
            this.delegate.drawRoundRect(canvas, this.rectF, f, getX(), getY(), i, false);
        } else {
            canvas.drawCircle(width, paddingTop, f, this.bgPaint);
        }
        float width2 = ((LocaleController.isRTL || this.mirrorX) ? this.bigCircleOffset - this.bigCircleRadius : (getWidth() - this.bigCircleOffset) + this.bigCircleRadius) + this.bubblesOffset + this.miniBubblesOffset;
        float lerp = AndroidUtilities.lerp(this.isTop ? (getPaddingTop() - expandSize()) - AndroidUtilities.dp(16.0f) : ((getHeight() - this.smallCircleRadius) - f6) + expandSize(), (this.smallCircleRadius + f6) - expandSize(), CubicBezierInterpolator.DEFAULT.getInterpolation(this.flipVerticalProgress));
        float f11 = (-AndroidUtilities.dp(1.0f)) * f2;
        this.shadow.setBounds((int) ((width2 - f) - f11), (int) ((lerp - f) - f11), (int) (width2 + f + f11), (int) (lerp + f + f11));
        this.shadow.draw(canvas);
        if (this.delegate.drawBackground()) {
            this.rectF.set(width2 - f3, lerp - f3, width2 + f3, lerp + f3);
            this.delegate.drawRoundRect(canvas, this.rectF, f3, getX(), getY(), i, false);
        } else {
            canvas.drawCircle(width2, lerp, f3, this.bgPaint);
        }
        canvas.restore();
        this.shadow.setAlpha(255);
        this.bgPaint.setAlpha(255);
    }

    private void fillRecentReactionsList(List list) {
        HashSet hashSet = new HashSet();
        int i = this.type;
        int i2 = 0;
        if (i == 4) {
            Iterator it = this.selectedReactions.iterator();
            int i3 = 0;
            while (it.hasNext()) {
                ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) it.next();
                if (!hashSet.contains(visibleReaction)) {
                    hashSet.add(visibleReaction);
                    list.add(visibleReaction);
                    i3++;
                    if (i3 >= 8) {
                        return;
                    }
                }
            }
            List<TLRPC.TL_availableReaction> enabledReactionsList = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            while (i2 < enabledReactionsList.size()) {
                ReactionsLayoutInBubble.VisibleReaction fromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList.get(i2));
                if (!hashSet.contains(fromEmojicon)) {
                    hashSet.add(fromEmojicon);
                    list.add(fromEmojicon);
                    i3++;
                    if (i3 >= 8) {
                        return;
                    }
                }
                i2++;
            }
            return;
        }
        if (!this.allReactionsAvailable || i == 4) {
            if (i != 3) {
                List<TLRPC.TL_availableReaction> enabledReactionsList2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
                while (i2 < enabledReactionsList2.size()) {
                    list.add(ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList2.get(i2)));
                    i2++;
                }
                return;
            }
            ArrayList<TLRPC.Reaction> savedReactions = MediaDataController.getInstance(this.currentAccount).getSavedReactions();
            int i4 = 0;
            while (i2 < savedReactions.size()) {
                ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(savedReactions.get(i2));
                if (!hashSet.contains(fromTL)) {
                    hashSet.add(fromTL);
                    list.add(fromTL);
                    i4++;
                }
                if (i4 == 16) {
                    return;
                } else {
                    i2++;
                }
            }
            return;
        }
        if (i == 5) {
            TLRPC.messages_AvailableEffects availableEffects = MessagesController.getInstance(this.currentAccount).getAvailableEffects();
            if (availableEffects != null) {
                while (i2 < availableEffects.effects.size()) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL2 = ReactionsLayoutInBubble.VisibleReaction.fromTL(availableEffects.effects.get(i2));
                    if (!hashSet.contains(fromTL2)) {
                        hashSet.add(fromTL2);
                        list.add(fromTL2);
                    }
                    i2++;
                }
                return;
            }
            return;
        }
        ArrayList<TLRPC.Reaction> savedReactions2 = i == 3 ? MediaDataController.getInstance(this.currentAccount).getSavedReactions() : MediaDataController.getInstance(this.currentAccount).getTopReactions();
        if (this.type == 3) {
            TLRPC.TL_messages_savedReactionsTags savedReactionTags = MessagesController.getInstance(this.currentAccount).getSavedReactionTags(0L);
            if (savedReactionTags != null) {
                for (int i5 = 0; i5 < savedReactionTags.tags.size(); i5++) {
                    ReactionsLayoutInBubble.VisibleReaction fromTL3 = ReactionsLayoutInBubble.VisibleReaction.fromTL(savedReactionTags.tags.get(i5).reaction);
                    if (!hashSet.contains(fromTL3)) {
                        hashSet.add(fromTL3);
                        list.add(fromTL3);
                    }
                }
            }
            for (int i6 = 0; i6 < savedReactions2.size(); i6++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL4 = ReactionsLayoutInBubble.VisibleReaction.fromTL(savedReactions2.get(i6));
                if (!hashSet.contains(fromTL4)) {
                    hashSet.add(fromTL4);
                    list.add(fromTL4);
                }
            }
        } else {
            for (int i7 = 0; i7 < savedReactions2.size(); i7++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL5 = ReactionsLayoutInBubble.VisibleReaction.fromTL(savedReactions2.get(i7));
                if (!hashSet.contains(fromTL5) && (this.type == 3 || UserConfig.getInstance(this.currentAccount).isPremium() || fromTL5.documentId == 0)) {
                    hashSet.add(fromTL5);
                    list.add(fromTL5);
                }
            }
        }
        if (this.type != 3 || UserConfig.getInstance(this.currentAccount).isPremium()) {
            ArrayList<TLRPC.Reaction> recentReactions = MediaDataController.getInstance(this.currentAccount).getRecentReactions();
            for (int i8 = 0; i8 < recentReactions.size(); i8++) {
                ReactionsLayoutInBubble.VisibleReaction fromTL6 = ReactionsLayoutInBubble.VisibleReaction.fromTL(recentReactions.get(i8));
                if (!hashSet.contains(fromTL6)) {
                    hashSet.add(fromTL6);
                    list.add(fromTL6);
                }
            }
            List<TLRPC.TL_availableReaction> enabledReactionsList3 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            while (i2 < enabledReactionsList3.size()) {
                ReactionsLayoutInBubble.VisibleReaction fromEmojicon2 = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList3.get(i2));
                if (!hashSet.contains(fromEmojicon2)) {
                    hashSet.add(fromEmojicon2);
                    list.add(fromEmojicon2);
                }
                i2++;
            }
        }
    }

    private void filterReactions(List list) {
        HashSet hashSet = new HashSet();
        int i = 0;
        while (i < list.size()) {
            if (hashSet.contains(list.get(i))) {
                i--;
                list.remove(i);
            } else {
                hashSet.add((ReactionsLayoutInBubble.VisibleReaction) list.get(i));
            }
            i++;
        }
    }

    public static HashSet getInclusiveReactions(ArrayList arrayList) {
        TLRPC.TL_messageReactions tL_messageReactions;
        LongSparseArray longSparseArray = new LongSparseArray();
        HashSet hashSet = new HashSet();
        int i = 0;
        boolean z = true;
        while (i < arrayList.size()) {
            MessageObject messageObject = (MessageObject) arrayList.get(i);
            hashSet.clear();
            if (messageObject != null && (tL_messageReactions = messageObject.messageOwner.reactions) != null && tL_messageReactions.results != null) {
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    if (messageObject.messageOwner.reactions.results.get(i2).chosen) {
                        ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(messageObject.messageOwner.reactions.results.get(i2).reaction);
                        if (z || longSparseArray.indexOfKey(fromTL.hash) >= 0) {
                            hashSet.add(Long.valueOf(fromTL.hash));
                            longSparseArray.put(fromTL.hash, fromTL);
                        }
                    }
                }
            }
            int i3 = 0;
            while (i3 < longSparseArray.size()) {
                if (!hashSet.contains(Long.valueOf(longSparseArray.keyAt(i3)))) {
                    longSparseArray.removeAt(i3);
                    i3--;
                }
                i3++;
            }
            i++;
            z = false;
        }
        HashSet hashSet2 = new HashSet();
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            if (longSparseArray.valueAt(i4) != null) {
                hashSet2.add((ReactionsLayoutInBubble.VisibleReaction) longSparseArray.valueAt(i4));
            }
        }
        return hashSet2;
    }

    private Paint getStarGradientPaint(RectF rectF, float f) {
        if (this.starSelectedGradientPaint == null) {
            this.starSelectedGradientPaint = new Paint(1);
        }
        if (this.starSelectedGradientMatrix == null) {
            this.starSelectedGradientMatrix = new Matrix();
        }
        if (this.starSelectedGradient == null) {
            int color = Theme.getColor(Theme.key_reactionStarSelector, this.resourcesProvider);
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(64.0f), 0.0f, new int[]{color, Theme.multAlpha(color, 0.0f)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.starSelectedGradient = linearGradient;
            this.starSelectedGradientPaint.setShader(linearGradient);
        }
        this.starSelectedGradientMatrix.reset();
        this.starSelectedGradientMatrix.postTranslate(rectF.left, rectF.top);
        this.starSelectedGradient.setLocalMatrix(this.starSelectedGradientMatrix);
        this.starSelectedGradientPaint.setAlpha((int) (f * 255.0f));
        return this.starSelectedGradientPaint;
    }

    private void invalidateEmojis() {
        if (this.type != 4) {
            return;
        }
        invalidate();
        this.recyclerListView.invalidate();
        this.recyclerListView.invalidateViews();
        for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
            View childAt = this.recyclerListView.getChildAt(i);
            if (childAt instanceof ReactionHolderView) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) childAt;
                reactionHolderView.enterImageView.invalidate();
                childAt = reactionHolderView.loopImageView;
            }
            childAt.invalidate();
        }
    }

    private void invalidateShaders() {
        int dp = AndroidUtilities.dp(24.0f);
        float height = getHeight() / 2.0f;
        int color = Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground);
        Paint paint = this.leftShadowPaint;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        paint.setShader(new LinearGradient(0.0f, height, dp, height, color, 0, tileMode));
        this.rightShadowPaint.setShader(new LinearGradient(getWidth(), height, getWidth() - dp, height, color, 0, tileMode));
        invalidate();
    }

    public void lambda$animatePullingBack$2(ValueAnimator valueAnimator) {
        this.pullingLeftOffset = ((Float) this.pullingDownBackAnimator.getAnimatedValue()).floatValue();
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    public void lambda$new$0(View view, int i) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate == null || !(view instanceof ReactionHolderView)) {
            return;
        }
        reactionsContainerDelegate.onReactionClicked(this, ((ReactionHolderView) view).currentReaction, false, false);
    }

    public boolean lambda$new$1(int i, View view, int i2) {
        ReactionsContainerDelegate reactionsContainerDelegate;
        if (i == 5 || (reactionsContainerDelegate = this.delegate) == null || !(view instanceof ReactionHolderView)) {
            return false;
        }
        reactionsContainerDelegate.onReactionClicked(this, ((ReactionHolderView) view).currentReaction, true, false);
        return true;
    }

    public void lambda$reset$6(View view) {
        if (view instanceof ReactionHolderView) {
            ReactionHolderView reactionHolderView = (ReactionHolderView) view;
            reactionHolderView.pressed = false;
            reactionHolderView.loopImageView.setAlpha(1.0f);
            if (!this.skipEnterAnimation) {
                reactionHolderView.resetAnimation();
            } else {
                reactionHolderView.loopImageView.setScaleX(reactionHolderView.enterScale * (reactionHolderView.selected ? 0.76f : 1.0f));
                reactionHolderView.loopImageView.setScaleY(reactionHolderView.enterScale * (reactionHolderView.selected ? 0.76f : 1.0f));
            }
        }
    }

    public void lambda$showCustomEmojiReactionDialog$3() {
        this.reactionsWindow = null;
        invalidateLoopViews();
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null) {
            reactionsContainerDelegate.onEmojiWindowDismissed();
        }
    }

    public void lambda$updateSelected$4(boolean z, View view) {
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(view);
        if (childAdapterPosition < 0 || childAdapterPosition >= this.items.size() || !(view instanceof ReactionHolderView)) {
            return;
        }
        ((ReactionHolderView) view).updateSelected(((InnerItem) this.items.get(childAdapterPosition)).reaction, z);
    }

    public void setChildScale(View view, float f) {
        if (view instanceof ReactionHolderView) {
            ((ReactionHolderView) view).sideScale = f;
        } else {
            view.setScaleX(f);
            view.setScaleY(f);
        }
    }

    private void setVisibleReactionsList(List list, boolean z) {
        this.visibleReactionsList.clear();
        if (showCustomEmojiReaction()) {
            int dp = (AndroidUtilities.displaySize.x - AndroidUtilities.dp(36.0f)) / AndroidUtilities.dp(34.0f);
            if (dp > 7) {
                dp = 7;
            }
            if (dp < 1) {
                dp = 1;
            }
            int i = 0;
            while (i < Math.min(list.size(), dp)) {
                this.visibleReactionsList.add((ReactionsLayoutInBubble.VisibleReaction) list.get(i));
                i++;
            }
            if (i < list.size()) {
                this.nextRecentReaction.setReaction((ReactionsLayoutInBubble.VisibleReaction) list.get(i), -1);
            }
        } else {
            this.visibleReactionsList.addAll(list);
        }
        this.allReactionsIsDefault = true;
        for (int i2 = 0; i2 < this.visibleReactionsList.size(); i2++) {
            if (((ReactionsLayoutInBubble.VisibleReaction) this.visibleReactionsList.get(i2)).documentId != 0) {
                this.allReactionsIsDefault = false;
            }
        }
        this.allReactionsList.clear();
        this.allReactionsList.addAll(list);
        if ((((getLayoutParams().height - ((int) getTopOffset())) - getPaddingTop()) - getPaddingBottom()) * list.size() < AndroidUtilities.dp(200.0f)) {
            getLayoutParams().width = -2;
        }
        this.listAdapter.updateItems(z);
    }

    public void showCustomEmojiReactionDialog() {
        if (this.reactionsWindow != null) {
            return;
        }
        this.reactionsWindow = new CustomEmojiReactionsWindow(this.type, this.fragment, this.allReactionsList, this.selectedReactions, this, this.resourcesProvider);
        invalidateLoopViews();
        this.reactionsWindow.onDismissListener(new Runnable() {
            @Override
            public final void run() {
                ReactionsContainerLayout.this.lambda$showCustomEmojiReactionDialog$3();
            }
        });
        onShownCustomEmojiReactionDialog();
    }

    public void showUnlockPremium(float f, float f2) {
        new PremiumFeatureBottomSheet(this.fragment, 4, true).show();
    }

    public boolean showUnlockPremiumButton() {
        return (this.premiumLockedReactions.isEmpty() || MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()) ? false : true;
    }

    private void updateSelected(final boolean z) {
        AndroidUtilities.forEachViews((RecyclerView) this.recyclerListView, new com.google.android.exoplayer2.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                ReactionsContainerLayout.this.lambda$updateSelected$4(z, (View) obj);
            }
        });
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.chatInfoDidLoad) {
            if (i == NotificationCenter.emojiLoaded) {
                invalidateEmojis();
                return;
            } else {
                if (i == NotificationCenter.availableEffectsUpdate) {
                    setMessage(this.messageObject, null, true);
                    return;
                }
                return;
            }
        }
        TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
        if (chatFull.id != this.waitingLoadingChatId || getVisibility() == 0 || (chatFull.available_reactions instanceof TLRPC.TL_chatReactionsNone)) {
            return;
        }
        setMessage(this.messageObject, null, true);
        setVisibility(0);
        startEnterAnimation(false);
    }

    public void dismissParent(boolean z) {
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow != null) {
            customEmojiReactionsWindow.dismiss(z);
            this.reactionsWindow = null;
        }
    }

    public void dismissWindow() {
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow != null) {
            customEmojiReactionsWindow.dismiss();
        }
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactionsContainerLayout.dispatchDraw(android.graphics.Canvas):void");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (getAlpha() < 0.5f) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void drawBubbles(Canvas canvas) {
        float max = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        drawBubbles(canvas, this.bigCircleRadius * max, max, this.smallCircleRadius * max, this.type == 5 ? 255 : (int) (Utilities.clamp(this.customEmojiReactionsEnterProgress / 0.2f, 1.0f, 0.0f) * (1.0f - this.customEmojiReactionsEnterProgress) * 255.0f));
    }

    public float expandSize() {
        return (int) (getPullingLeftProgress() * AndroidUtilities.dp(6.0f));
    }

    public ReactionsContainerDelegate getDelegate() {
        return this.delegate;
    }

    public int getHintTextWidth() {
        return this.hintViewWidth;
    }

    public int getItemsCount() {
        return this.visibleReactionsList.size() + (showCustomEmojiReaction() ? 1 : 0) + 1;
    }

    public float getPullingLeftProgress() {
        return Utilities.clamp(this.pullingLeftOffset / AndroidUtilities.dp(42.0f), 2.0f, 0.0f);
    }

    public CustomEmojiReactionsWindow getReactionsWindow() {
        return this.reactionsWindow;
    }

    public String getSelectedEmoji() {
        TLRPC.Document findDocument;
        if (this.selectedReactions.isEmpty()) {
            return "";
        }
        ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) this.selectedReactions.iterator().next();
        long j = visibleReaction.documentId;
        String str = null;
        if (j != 0 && (findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, j)) != null) {
            str = MessageObject.findAnimatedEmojiEmoticon(findDocument, null);
        }
        if (TextUtils.isEmpty(str)) {
            str = visibleReaction.emojicon;
        }
        return TextUtils.isEmpty(str) ? "👍" : str;
    }

    public HashSet<ReactionsLayoutInBubble.VisibleReaction> getSelectedReactions() {
        return this.selectedReactions;
    }

    public float getTopOffset() {
        if (this.hasHint) {
            return ((FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams()).topMargin;
        }
        return 0.0f;
    }

    public int getTotalWidth() {
        int itemsCount = getItemsCount();
        return !showCustomEmojiReaction() ? (AndroidUtilities.dp(36.0f) * itemsCount) + (AndroidUtilities.dp(2.0f) * (itemsCount - 1)) + AndroidUtilities.dp(16.0f) : (AndroidUtilities.dp(36.0f) * itemsCount) - AndroidUtilities.dp(4.0f);
    }

    public List<ReactionsLayoutInBubble.VisibleReaction> getVisibleReactionsList() {
        return this.visibleReactionsList;
    }

    public int getWindowType() {
        int i = this.type;
        if (i == 4) {
            return 13;
        }
        if (i == 3) {
            return 11;
        }
        if (i == 5) {
            return 14;
        }
        return this.showExpandableReactions ? 8 : 1;
    }

    public void invalidateLoopViews() {
        for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
            View childAt = this.recyclerListView.getChildAt(i);
            if (childAt instanceof ReactionHolderView) {
                ((ReactionHolderView) childAt).loopImageView.invalidate();
            }
        }
    }

    public boolean isFlippedVertically() {
        return this.isFlippedVertically;
    }

    public void measureHint() {
        TextView textView;
        if (this.hintMeasured || !this.hasHint || getMeasuredWidth() <= 0) {
            return;
        }
        float f = 16.0f;
        int min = Math.min(AndroidUtilities.dp(320.0f), getMeasuredWidth() - AndroidUtilities.dp(16.0f));
        StaticLayout staticLayout = new StaticLayout(this.hintView.getText(), this.hintView.getPaint(), min, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.hintViewHeight = staticLayout.getHeight();
        this.hintViewWidth = 0;
        for (int i = 0; i < staticLayout.getLineCount(); i++) {
            this.hintViewWidth = Math.max(this.hintViewWidth, (int) Math.ceil(staticLayout.getLineWidth(i)));
        }
        if (staticLayout.getLineCount() <= 1 || this.hintView.getText().toString().contains("\n")) {
            textView = this.hintView;
        } else {
            min = HintView2.cutInFancyHalf(this.hintView.getText(), this.hintView.getPaint());
            StaticLayout staticLayout2 = new StaticLayout(this.hintView.getText(), this.hintView.getPaint(), min, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.hintViewHeight = staticLayout2.getHeight();
            this.hintViewWidth = 0;
            for (int i2 = 0; i2 < staticLayout2.getLineCount(); i2++) {
                this.hintViewWidth = Math.max(this.hintViewWidth, (int) Math.ceil(staticLayout2.getLineWidth(i2)));
            }
            this.hintView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
            textView = this.hintView;
            f = 48.0f;
        }
        textView.setWidth(AndroidUtilities.dp(f) + min);
        int max = Math.max(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(7.0f) + this.hintViewHeight);
        int i3 = this.type;
        if (i3 == 1 || i3 == 2) {
            max = AndroidUtilities.dp(20.0f);
        } else {
            getLayoutParams().height = AndroidUtilities.dp(52.0f) + max + AndroidUtilities.dp(22.0f);
        }
        ((FrameLayout.LayoutParams) this.nextRecentReaction.getLayoutParams()).topMargin = max;
        ((FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams()).topMargin = max;
        this.hintMeasured = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        if (this.type == 5) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.availableEffectsUpdate);
        }
    }

    public void onCustomEmojiWindowClosing() {
        ValueAnimator valueAnimator = this.pullingDownBackAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.pullingLeftOffset = 0.0f;
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    public void onCustomEmojiWindowOpened() {
        ValueAnimator valueAnimator = this.pullingDownBackAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.pullingLeftOffset = 0.0f;
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        if (this.type == 5) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.availableEffectsUpdate);
        }
    }

    public void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null) {
            reactionsContainerDelegate.onReactionClicked(view, visibleReaction, z, true);
        }
        if (this.type == 5) {
            try {
                performHapticFeedback(3, 1);
            } catch (Exception unused) {
            }
        }
    }

    protected void onShownCustomEmojiReactionDialog() {
    }

    @Override
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateShaders();
    }

    public void prepareAnimation(boolean z) {
        this.prepareAnimation = z;
        invalidate();
    }

    public void reset() {
        this.isHiddenNextReaction = true;
        this.pressedReactionPosition = 0;
        this.pressedProgress = 0.0f;
        this.pullingLeftOffset = 0.0f;
        this.pressedReaction = null;
        this.clicked = false;
        AndroidUtilities.forEachViews((RecyclerView) this.recyclerListView, new com.google.android.exoplayer2.util.Consumer() {
            @Override
            public final void accept(Object obj) {
                ReactionsContainerLayout.this.lambda$reset$6((View) obj);
            }
        });
        this.lastVisibleViews.clear();
        this.recyclerListView.invalidate();
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    @Override
    public void setAlpha(float f) {
        if (getAlpha() != f && f == 0.0f) {
            this.lastVisibleViews.clear();
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                    ((ReactionHolderView) this.recyclerListView.getChildAt(i)).resetAnimation();
                }
            }
        }
        super.setAlpha(f);
    }

    public void setBubbleOffset(float f) {
        this.bubblesOffset = f;
    }

    public void setChatScrimView(ChatScrimPopupContainerLayout chatScrimPopupContainerLayout) {
        this.chatScrimPopupContainerLayout = chatScrimPopupContainerLayout;
    }

    public void setCurrentAccount(int i) {
        this.currentAccount = i;
    }

    public void setCustomEmojiEnterProgress(float f) {
        this.customEmojiReactionsEnterProgress = f;
        ChatScrimPopupContainerLayout chatScrimPopupContainerLayout = this.chatScrimPopupContainerLayout;
        if (chatScrimPopupContainerLayout != null) {
            chatScrimPopupContainerLayout.setPopupAlpha(1.0f - f);
        }
        invalidate();
    }

    public void setCustomEmojiReactionsBackground(boolean z) {
        InternalImageView internalImageView;
        Drawable drawable;
        if (z) {
            internalImageView = this.customEmojiReactionsIconView;
            drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(28.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_listSelector), 40));
        } else {
            internalImageView = this.customEmojiReactionsIconView;
            drawable = null;
        }
        internalImageView.setBackground(drawable);
    }

    public void setDelegate(ReactionsContainerDelegate reactionsContainerDelegate) {
        this.delegate = reactionsContainerDelegate;
    }

    public void setFlippedVertically(boolean z) {
        this.isFlippedVertically = z;
        invalidate();
    }

    public void setFragment(BaseFragment baseFragment) {
        this.fragment = baseFragment;
    }

    public void setHint(CharSequence charSequence) {
        this.hasHint = true;
        if (this.hintView == null) {
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(getContext(), this.resourcesProvider);
            this.hintView = linksTextView;
            linksTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
            this.hintView.setClickable(true);
            this.hintView.setTextSize(1, 12.0f);
            int i = this.type;
            if (i == 1 || i == 2 || i == 4) {
                this.hintView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
                this.hintView.setAlpha(0.5f);
            } else {
                this.hintView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider));
            }
            this.hintView.setGravity(1);
            addView(this.hintView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 6.0f, 0.0f, 0.0f));
        }
        this.hintView.setText(charSequence);
        this.hintMeasured = false;
        ((FrameLayout.LayoutParams) this.nextRecentReaction.getLayoutParams()).topMargin = AndroidUtilities.dp(20.0f);
        ((FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams()).topMargin = AndroidUtilities.dp(20.0f);
    }

    public void setMessage(org.telegram.messenger.MessageObject r10, org.telegram.tgnet.TLRPC.ChatFull r11, boolean r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactionsContainerLayout.setMessage(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$ChatFull, boolean):void");
    }

    public void setMiniBubblesOffset(float f) {
        this.miniBubblesOffset = f;
    }

    public void setMirrorX(boolean z) {
        this.mirrorX = z;
        invalidate();
    }

    public void setOnSwitchedToLoopView(Runnable runnable) {
        this.onSwitchedToLoopView = runnable;
    }

    public void setParentLayout(ChatScrimPopupContainerLayout chatScrimPopupContainerLayout) {
        this.parentLayout = chatScrimPopupContainerLayout;
    }

    public void setPaused(boolean z, boolean z2) {
        if (this.paused == z) {
            return;
        }
        this.paused = z;
        this.pausedExceptSelected = z2;
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow == null || customEmojiReactionsWindow.getSelectAnimatedEmojiDialog() == null) {
            return;
        }
        this.reactionsWindow.getSelectAnimatedEmojiDialog().setPaused(this.paused, this.pausedExceptSelected);
    }

    public void setSelectedEmojis(ArrayList<String> arrayList) {
        this.selectedReactions.clear();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            ReactionsLayoutInBubble.VisibleReaction fromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(it.next());
            if (fromEmojicon != null) {
                this.selectedReactions.add(fromEmojicon);
                this.alwaysSelectedReactions.add(fromEmojicon);
            }
        }
        updateSelected(true);
    }

    public void setSelectedReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        this.selectedReactions.clear();
        if (visibleReaction != null) {
            this.selectedReactions.add(visibleReaction);
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void setSelectedReactionAnimated(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        this.selectedReactions.clear();
        if (visibleReaction != null) {
            this.selectedReactions.add(visibleReaction);
        }
        updateSelected(true);
    }

    public void setSelectedReactionInclusive(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        this.selectedReactions.clear();
        if (visibleReaction != null) {
            this.selectedReactions.add(visibleReaction);
        }
        updateSelected(true);
    }

    public void setSelectedReactions(ArrayList<MessageObject> arrayList) {
        TLRPC.TL_messageReactions tL_messageReactions;
        this.selectedReactions.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = arrayList.get(i);
            if (messageObject != null && (tL_messageReactions = messageObject.messageOwner.reactions) != null && tL_messageReactions.results != null) {
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    if (messageObject.messageOwner.reactions.results.get(i2).chosen) {
                        this.selectedReactions.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(messageObject.messageOwner.reactions.results.get(i2).reaction));
                    }
                }
            }
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void setSelectedReactionsInclusive(ArrayList<MessageObject> arrayList) {
        this.selectedReactions.clear();
        this.selectedReactions.addAll(getInclusiveReactions(arrayList));
        updateSelected(true);
    }

    public void setSkipDraw(boolean z) {
        if (this.skipDraw != z) {
            this.skipDraw = z;
            if (!z) {
                for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                    if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                        ReactionHolderView reactionHolderView = (ReactionHolderView) this.recyclerListView.getChildAt(i);
                        if (reactionHolderView.hasEnterAnimation && (reactionHolderView.loopImageView.getImageReceiver().getLottieAnimation() != null || reactionHolderView.loopImageView.getImageReceiver().getAnimation() != null)) {
                            reactionHolderView.loopImageView.setVisibility(0);
                            reactionHolderView.enterImageView.setVisibility(4);
                            if (reactionHolderView.shouldSwitchToLoopView) {
                                reactionHolderView.switchedToLoopView = true;
                            }
                        }
                        reactionHolderView.invalidate();
                    }
                }
            }
            invalidate();
        }
    }

    public void setStoryItem(TL_stories.StoryItem storyItem) {
        TLRPC.Reaction reaction;
        this.selectedReactions.clear();
        if (storyItem != null && (reaction = storyItem.sent_reaction) != null) {
            this.selectedReactions.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(reaction));
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void setTop(boolean z) {
        this.isTop = z;
    }

    public void setTransitionProgress(float f) {
        this.transitionProgress = f;
        ChatScrimPopupContainerLayout chatScrimPopupContainerLayout = this.parentLayout;
        if (chatScrimPopupContainerLayout != null) {
            if (!this.animatePopup || !allowSmoothEnterTransition()) {
                f = 1.0f;
            }
            chatScrimPopupContainerLayout.setReactionsTransitionProgress(f);
        }
        invalidate();
    }

    @Override
    public void setTranslationX(float f) {
        if (f != getTranslationX()) {
            super.setTranslationX(f);
        }
    }

    public boolean showCustomEmojiReaction() {
        return this.allReactionsAvailable || this.showExpandableReactions;
    }

    public boolean showExpandableReactions() {
        return this.showExpandableReactions;
    }

    public void startEnterAnimation(boolean z) {
        ObjectAnimator duration;
        OvershootInterpolator overshootInterpolator;
        this.animatePopup = z;
        setTransitionProgress(0.0f);
        setAlpha(1.0f);
        this.notificationsLocker.lock();
        if (allowSmoothEnterTransition()) {
            duration = ObjectAnimator.ofFloat(this, (Property<ReactionsContainerLayout, Float>) TRANSITION_PROGRESS_VALUE, 0.0f, 1.0f).setDuration(250L);
            overshootInterpolator = new OvershootInterpolator(0.5f);
        } else {
            duration = ObjectAnimator.ofFloat(this, (Property<ReactionsContainerLayout, Float>) TRANSITION_PROGRESS_VALUE, 0.0f, 1.0f).setDuration(250L);
            overshootInterpolator = new OvershootInterpolator(0.5f);
        }
        duration.setInterpolator(overshootInterpolator);
        duration.addListener(new AnimatorListenerAdapter() {
            AnonymousClass7() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                ReactionsContainerLayout.this.notificationsLocker.unlock();
            }
        });
        duration.start();
    }
}
